# DA-NRW Rule Base
# Most of the listed operations now are performed by ContentBroker
 
# Please change both replList settings to the appropiate settings of your system,
# according to the order, you want to replicate them on. 
# Be careful by changing settings in this file. 
# Be sure, iRODS Server works with this file by having activated it in reRuleSet 
# And iRODS Server is still working. Try with ils. 

#The ressources on which we aren't allowed to delete items on
acDataDeletePolicy {ON($rescName == "lza1") {msiDeleteDisallowed; }}
acDataDeletePolicy {ON($rescName == "lza2") {msiDeleteDisallowed; }}

#Author: Jens Peters
#counts the number of total Repls
acGetTotalReplNumber(*objPath,*nr) {
 	msiSplitPath(*objPath,*coll,*dn);
	msiExecStrCondQuery("SELECT count(DATA_REPL_NUM) where DATA_NAME = '*dn' and COLL_NAME = '*coll'",*nrepls)
	*nr=0;
	foreach(*nrepls) {
	 	 msiGetValByKey(*nrepls,"DATA_REPL_NUM",*nr);
	}
}
#Author: Jens Peters
#counts the number of repls per rg
acGetTotalReplNumberPerGroup(*objPath,*rg,*nr) {
        *ress="";
	msiExecStrCondQuery("SELECT RESC_NAME WHERE RESC_GROUP_NAME = '*rg'",*rescl);
        foreach(*rescl) {
            msiGetValByKey(*rescl,"RESC_NAME",*rn);
            *ress="'*rn',*ress";
        }
	if (*ress=="") {
	 	writeLine("serverLog","No Resc found in RG *rg!");
		fail();
	}
	msiSplitPath(*objPath,*coll,*dn);
	msiExecStrCondQuery("SELECT count(DATA_REPL_NUM) WHERE COLL_NAME = '*coll' and DATA_NAME = '*dn' and RESC_NAME in (*ress) ",*grepls);
		*nr=0;
    	foreach(*grepls) {
         msiGetValByKey(*grepls,"DATA_REPL_NUM",*nr);
    	}
}
#Author: Jens Peters LVRInfoKom 2014
#IRODS TO IRODS Replication check
acPostProcForPut { ON($objPath like "/$irodsZone/federated/*.tar") {
	writeLine("serverLog","recieved $objPath as a federated package");
	acVerifyChecksum($objPath,*status)
	if (*status==0) {
		msiDataObjUnlink($objPath,"forceFlag=",*out)
		fail();
	}
}	
}
#Author: Jens Peters LVRInfoKo, 2014
#Checks the number of federated items in all zones being connected 
acGetNumberOfCopies(*homeDao,*numberOfCopies){
			*numberOfCopies = 0;
			msiAddSelectFieldToGenQuery("ZONE_NAME","null", *GenQ2)
                        msiAddSelectFieldToGenQuery("ZONE_TYPE","null", *GenQ2)
                        msiAddConditionToGenQuery("ZONE_TYPE"," = ","remote",*GenQ2)
                        msiExecGenQuery(*GenQ2, *zones)
                        *ok=0;
                        *err=errorcode(msiObjStat(*homeDao,*out));
                         writeLine("serverLog","*err for *homeDao");
                        if (*err==0 ) {
                                *numberOfCopies=1;
                        }
			foreach(*zones){
                        	msiGetValByKey(*zones,"ZONE_NAME",*zone);
				*destColl="/*zone/federated*homeDao"
				*err=errorcode(msiObjStat(*destColl,*out));
        			writeLine("serverLog","*err for *destColl");
				if (*err==0 ) {
           			*numberOfCopies=*numberOfCopies+1;
        			}
			}
			msiCloseGenQuery(*GenQ2,*status)
}

#Author: Jens Peters
#Checks the local integrity of a DO by checking it's local replications
# 0=failure, the checksum failed.
# 1=ok the object status is ok.
acVerifyChecksum(*objPath,*status){
	*status=0;
	*checksumchk=0;
	*replchk=0;
	msiGetSystemTime(*systime,nop)
	*error=errorcode( msiDataObjChksum("*objPath","ChksumAll=++++verifyChksum=",*localCs))
        if (*error < 0 ) {
                    *csf=""
                    if (*error == -314000 ) {
                        *csf="CHECKSUM "
			*checksumchk=0
                    }
		    if (*error != -314000 ) {
                        *checksumchk=0
                    }
                    writeLine("serverLog","SEVERE *csf FAILURE: *error *objPath");
         } else { *checksumchk=1 }
	msiString2KeyValPair("checked=*checksumchk",*kvpaircs)
        msiSetKeyValuePairsToObj(*kvpaircs,"*objPath","-d")
        msiString2KeyValPair("last_checked=*systime",*kvpairts)
        msiSetKeyValuePairsToObj(*kvpairts,"*objPath","-d")
	*status=*checksumchk;
}

# Author Jens Peters
# Verifies federated copies of DAO synchronously
# Returns amount of errors on registered federated copies
# 
acVerifyChecksumFedSync(*objPath,*errors){
	*errors=0
	msiAddSelectFieldToGenQuery("ZONE_NAME","null", *GenQ2)
        msiAddSelectFieldToGenQuery("ZONE_TYPE","null", *GenQ2)
        msiAddConditionToGenQuery("ZONE_TYPE"," = ","remote",*GenQ2)
        msiExecGenQuery(*GenQ2, *zones)
	foreach(*zones){
             	msiGetValByKey(*zones,"ZONE_NAME",*zone);
             	*destColl="/*zone/federated*objPath"
		*err=errorcode(msiObjStat(*destColl,*out));
                 writeLine("serverLog","*err for *destColl");
                 if (*err==0 ) {
         		*error=errorcode( msiDataObjChksum("*destColl","ChksumAll=++++verifyChksum=",*cs))
        		if (*error < 0 ) {
                      		writeLine("serverLog","FAILURE VerifyChecksum Source: *objPath federated Destination: *error *destColl")
				*errors=*errors+1		
			} 
		} 
	}
	msiCloseGenQuery(*GenQ2,*status)
	writeLine("serverLog","*objPath has (*errors) Errors on existing federated copies");
}

# Quick Checking object
# depends on running Service checkAIP per node
# Author Jens Peters
# INPUT objPath the object to check
# OUTPUT status 0 failure 1 ok
#
acIsValid(*objPath,*status) {
	msiSplitPath(*objPath, *coll, *dname)
	*lc=""
	*status=0
	*ownState="0"
	*foreignState="0"
	msiExecStrCondQuery("SELECT DATA_NAME, COLL_NAME, META_DATA_ATTR_NAME, META_DATA_ATTR_VALUE where COLL_NAME = '*coll' and DATA_NAME = '*dname' and META_DATA_ATTR_NAME = 'last_checked'",*lc)
	foreach(*lc) {
        	msiGetValByKey(*lc,"META_DATA_ATTR_VALUE",*last_checked);
        }
	writeLine("serverLog","Package *objPath *last_checked");
	msiExecStrCondQuery("SELECT DATA_NAME, COLL_NAME, META_DATA_ATTR_NAME, META_DATA_ATTR_VALUE where COLL_NAME = '*coll' and DATA_NAME = '*dname' and META_DATA_ATTR_NAME = 'checked'",*checkDaos)
        foreach(*checkDaos) {
		msiGetValByKey(*checkDaos,"META_DATA_ATTR_VALUE",*ownState);
		writeLine("serverLog","Package *objPath was checked locally *ownState");

	}
	msiExecStrCondQuery("SELECT DATA_NAME, COLL_NAME, META_DATA_ATTR_NAME, META_DATA_ATTR_VALUE where COLL_NAME = '*coll' and DATA_NAME = '*dname' and META_DATA_ATTR_NAME = 'checked_foreign'",*foreign)
       	foreach(*foreign) {
		msiGetValByKey(*foreign,"META_DATA_ATTR_VALUE",*foreignState);
		writeLine("serverLog","Package *objPath was checked remote *foreignState");
		
       	}
	if (*ownState=="1") {
		if (*foreignState=="1") {
			*status=1
		}
	}
}

# Gets the resource for a given Resource Group name
# INPUT rg resource group name
# OUTPUT res resource 
# Author Jens Peters
#
acGetRescForRg(*rg,*res) {
	*res=""
        msiExecStrCondQuery("SELECT RESC_NAME WHERE RESC_GROUP_NAME = '*rg' ",*rescl);
        foreach(*rescl) {
            msiGetValByKey(*rescl,"RESC_NAME",*res);
        }
}

# Gets a list of foreign zones connected to this node
# INPUT forbiddennodes, nodes to which the syning is disallowed
# OUTPUT hosts comma seperated values of remote hosts 
# Author Jens Peters
#

acGetHostsOnGrid(*hosts,*forbiddenNodes) {
        *hosts=list()
        *hst="";
        msiAddSelectFieldToGenQuery("ZONE_NAME","null", *GenQ2)
        msiAddSelectFieldToGenQuery("ZONE_TYPE","null", *GenQ2)
        msiAddSelectFieldToGenQuery("ZONE_CONNECTION","null", *GenQ2)
        msiAddConditionToGenQuery("ZONE_TYPE"," = ","remote",*GenQ2)
        *attrVall=split(*forbiddenNodes, ",");
        foreach(*attrVal in *attrVall) {
             msiAddConditionToGenQuery("ZONE_NAME"," != ",*attrVal,*GenQ2)
        }
        msiExecGenQuery(*GenQ2, *zones)
        foreach(*zones){
                 msiGetValByKey(*zones,"ZONE_CONNECTION",*host);
                *hst="*host;*hst"
        }
        msiCloseGenQuery(*GenQ2,*out)
        *hosts=split(*hst,";")
}


# Helper function to read the local zonename 
# remotely (workaround) 
# OUTPUT the zone name
# Author Jens Peters
#
acGetLocalZoneName(*zone) {
        *zone="";
        msiAddSelectFieldToGenQuery("ZONE_NAME","null", *GenQ2)
        msiAddSelectFieldToGenQuery("ZONE_TYPE","null", *GenQ2)
        msiAddConditionToGenQuery("ZONE_TYPE"," = ","local",*GenQ2)
        msiExecGenQuery(*GenQ2, *zones)
        foreach(*zones){
                 msiGetValByKey(*zones,"ZONE_NAME",*zone);
        }
        msiCloseGenQuery(*GenQ2,*out)
}

# Helper for log out 
# Author: Jens Peters
acLog(*log) {
       writeLine("stdout",*log)
       writeLine("serverLog",*log)
}
# Federates srcColl to the zones list 
# zones list must be list(list(zone, sizeMBytes))
# list successfully synced zones to successZones
# INPUT srcDao the sourceDao to be syned
# INPUT zones the zones to synchronize to
# OUTPUT successZones as comma separated values
# Author Jens Peters
#
acFederateToZones(*srcDao,*destResc,*zones,*successZones,*min_copies) {
	*ok=0
	*err=0
	*successZones=""
	foreach(*zones){
		if (*ok < *min_copies) {
            		*zone=elem(*zones,0)
            		*mspace=elem(*zones,1)
			*destDao="/*zone/federated*srcDao"
			*log="Synchronize *srcDao to *destDao *zone which has *mspace MB free"
           		acLog(*log)	
			*error=errorcode(msiDataObjRsync(*srcDao,"IRODS_TO_IRODS",*destResc,*destDao,*Status))
           		if (*error < 0 ) {
                   		*err=*err+1
                    		*log="recieved errorcode *error while federating to *zones *destDao"
				 acLog(*log)
			} else {
                   	*ok=*ok+1       
   	        	*successZones="*zone,*successZones"
			*log="Synchronized *srcDao"
			 acLog(*log)
			}
		}
 	}
} 

# Gets a List of Hostnames ordered by the actual 
# amount of free space on resource found in given resc group naem. 
# Depends on locally running ServerMonitoring rules by Jean Yves
# Author Jens Peters
# RETURNS *servers as list(list("zone_name","mb")) by zone names ordered by free space
# 
acGetHostsOrderedByFreeSpaceOnGridDesc(*servers, *rg,*forbiddenNodes) {
	*hst=""
        *ls=""
        *mbyte=0
        *hosts=list()
        acGetHostsOnGrid(*hosts,*forbiddenNodes)
        foreach(*hosts) {
                *err=errorcode(remote(*hosts,"null") {
                        *resource=""
                        acGetRescForRg(*rg,*resource)
                        acGetFreeSpaceOnResc(*resource,*mbyte)
                        acGetLocalZoneName(*zone)
                        *ls="*zone,*mbyte;"
                })
		if (*err<0) {
			acLog("recieved Error code *err on remotely determining free space")
		}
        }
	*servers=list()
	writeLine("stdout","Fill grade List: *ls")
	
	*sv=split(*ls,";")
	foreach(*sv) {
		*tmp=split(*sv,",")
		*byte=elem(*tmp,1)
		*serv=elem(*tmp,0)
		*servers=cons(list("*serv","*byte"),*servers)
	}
	# Do the bubble search
	*n=size(*servers)-1
        *unsortiert=1
        while (*unsortiert==1) {
                *unsortiert=0
                for(*i=0; *i < *n; *i=*i+1) {
                        *t1=int(elem(elem(*servers,*i),1))
                        *t2=int(elem(elem(*servers,*i+1),1))
                        if (*t1 > *t2) {
                                *tempL1=elem(*servers,*i)
                                *tempL2=elem(*servers,*i+1)
                                *servers=setelem(*servers,*i,*tempL2)
                                *servers=setelem(*servers,*i+1,*tempL1)
                                *unsortiert=1
                        }
                }
        }
	acLog("Sorted List by fill grades: *servers")
}
# Federate Dao to the least loaded connected zones
# INPUT dao The DataObject
# INPUT *destResc Group name
# OUTPUT sucessfully copied zones
# INPUT Forbidden zones
# INPUT min_copies
# Author: Jens Peters
#
acFederateLeastLoaded(*dao,*destResc,*successZones,*forb,*min_copies) {
  	*zones=""	 
	acGetHostsOrderedByFreeSpaceOnGridDesc(*zones,*destResc,*forb)
        acFederateToZones(*dao,*destResc,*zones,*successZones,*min_copies)
}

# Helper Function for Syncing results and stoing AVU Metadata to object
# INPUT dao The Data object
# INPUT successful synced zones
# INPUT min_copies
# Author: Jens Peters
#
acPrintSyncResults(*dao,*syncs,*min_copies) {
         *copies=size(split(*syncs,","))
         if (*copies == 0) {
             acLog("Not reached any zone!")
         }
         if (*copies > 0) {
             msiString2KeyValPair("SYNCHRONIZED_TO=*syncs",*kvpaircs2)
             msiSetKeyValuePairsToObj(*kvpaircs2,*dao,"-d")
         }
         if (*copies < *min_copies) {
             acLog("...still missing Copies, actually *copies, must have *min_copies!")
	 }
         if (*copies >= *min_copies) {
             msiString2KeyValPair("FEDERATED=1",*kvpaircs3)
             msiSetKeyValuePairsToObj(*kvpaircs3,*dao,"-d")
             acLog("...fulfilled Copies of *min_copies")
         }
}

# Gets the free space per resource
# to be called remotely
# Depends on running daemons
# serverMonStats.r
# serverMonStatsDigest.r
# Author: Jens Peters
#
acGetFreeSpaceOnResc(*resc,*mbyte) {
	*out=0
	msiExecStrCondQuery("SELECT RESC_NAME, RESC_LOC, RESC_FREE_SPACE WHERE RESC_NAME = '*resc' ",*lc)
        foreach(*lc) {
                msiGetValByKey(*lc,"RESC_FREE_SPACE",*out);
        } 
	writeLine("stdout","VT02: *resc *out")
	*mbyte=*out
}

#old fashioned rules, partly used for backward compatibility
@backwardCompatible "true"

#STRICTLY check ACL 
acAclPolicy||msiAclPolicy(STRICT)|nop

acPostProcForDelete||nop|nop

