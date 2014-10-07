# DNS Rule Base dns.re
# 
# HKI (2011-2013)
# LVR InfoKom 2014
# This file is published under the GPL v3 
#  DA-NRW Software Suite
#   Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
#   Universität zu Köln, 2014 LVR InfoKom
# 
#   This program is free software: you can redistribute it and/or modify
#   it under the terms of the GNU General Public License as published by
#   the Free Software Foundation, either version 3 of the License, or
#   (at your option) any later version.
# 
#   This program is distributed in the hope that it will be useful,
#   but WITHOUT ANY WARRANTY; without even the implied warranty of
#   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#   GNU General Public License for more details.
# 
#   You should have received a copy of the GNU General Public License
#   along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
# Be careful by changing settings in this file, failures might 
# harm your capability of working with DNS. 
#
# https://github.com/da-nrw
# 
#
#
#The ressources on which we aren't allowed to delete items on
acDataDeletePolicy {ON($rescName == "lza1") {msiDeleteDisallowed; }}

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
#Author: Jens Peters
#For incoming federated copies we need to compute the checksum 
acPostProcForCreate {ON($objPath like "*/federated/*") {
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
                        if (*err==0 ) {
                                *numberOfCopies=1;
                        }
			foreach(*zones){
                        	msiGetValByKey(*zones,"ZONE_NAME",*zone);
				*destColl="/*zone/federated*homeDao"
				*err=errorcode(msiObjStat(*destColl,*out));
				if (*err==0 ) {
           			*numberOfCopies=*numberOfCopies+1;
        			}
			}
			msiCloseGenQuery(*GenQ2,*status)
}

#Author: Jens Peters
#Checks the local integrity of a objectPath by checking it's local replications
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
                    acLog("SEVERE *csf FAILURE: *error *objPath");
        			
	} else { 
        	 *checksumchk=1
	}
	msiString2KeyValPair("checked=*checksumchk",*kvpaircs)
        msiSetKeyValuePairsToObj(*kvpaircs,"*objPath","-d")
        msiString2KeyValPair("last_checked=*systime",*kvpairts)
        msiSetKeyValuePairsToObj(*kvpairts,"*objPath","-d")
	*status=*checksumchk;
}

# Author Jens Peters
# Verifies federated copies of DAO
# Returns amount of errors on registered federated copies
# 
# Depends on service refreshing remotely kept copies from time to time
# otherwise this action will return false positive results
#
acVerifyChecksumFed(*masterCs,*objPath,*errors){
	*errors=0
	msiAddSelectFieldToGenQuery("ZONE_NAME","null", *GenQ2)
        msiAddSelectFieldToGenQuery("ZONE_TYPE","null", *GenQ2)
        msiAddSelectFieldToGenQuery("ZONE_CONNECTION","null",*GenQ2)
	msiAddConditionToGenQuery("ZONE_TYPE"," = ","remote",*GenQ2)
        msiExecGenQuery(*GenQ2, *zones)
	foreach(*zones){
             	msiGetValByKey(*zones,"ZONE_NAME",*zone);
		msiGetValByKey(*zones,"ZONE_CONNECTION",*host);
             	*destColl="/*zone/federated*objPath"
		*err=errorcode(msiObjStat(*destColl,*out));
                 if (*err==0 ) {
         		*error=errorcode(msiDataObjChksum(*destColl,"",*remoteCs))
        		if (*error < 0 ) {
                      		acLog("*objPath federated Destination: *error *destColl")
				*errors=*errors+1		
			} 
			if (*masterCs!=*remoteCs) {
				acLog("Master CS: *masterCs != *remoteCs for *destColl")
				*errors=*errors+1
			}
		}
		# it doesn't mean necessarily an error if we don't find the object in each zone 
	}
	msiCloseGenQuery(*GenQ2,*status)
	acLog("*objPath has (*errors) Errors on existing federated copies");
}
# Gives back flag if dao needs to be checked
# 1: true, needs to be checked
# 0. false: not needed to be checked yet (usually you define a "trust" how long
#  you'll trust a stored checksum, given in trustYears)
# Author: Jens Peters
#
acNeedCheck(*objPath,*needed,*trustYears) {
	*needed=0
	*years=0
	msiGetSystemTime(*now,"null")
        acGetAVUField(*objPath,"last_checked",*lc)
        *ilc=int(*lc)
        *inow=int(*now)
        *diff=*inow-*ilc
        acLog("last check is (*diff) seconds in the past")
	*years=(31536000 * *trustYears)
	if (*diff > *years) {
		*needed=1
	} 
}
# Get the Dataobject AVU Value of given field
# INPUT objPath 
# INPUT fieldName 
# OUTPUT fieldValue
# Author Jens Peters
#
acGetAVUField(*objPath,*fieldName,*fieldValue) {
	*fieldValue=""
	msiSplitPath(*objPath, *coll, *dname)
	msiExecStrCondQuery("SELECT DATA_NAME, COLL_NAME, META_DATA_ATTR_NAME, META_DATA_ATTR_VALUE where COLL_NAME = '*coll' and DATA_NAME = '*dname' and META_DATA_ATTR_NAME = '*fieldName'",*avus)
        foreach(*avus) {
                msiGetValByKey(*avus,"META_DATA_ATTR_VALUE",*fieldValue);
                acLog("read *fieldValue *coll/*dname")
        }
}
# CB Client method: 
# depends on running Service checkAIP per foreign node, which checks federated items
# Author Jens Peters
# INPUT objPath the object to check
# OUTPUT status 0:false== failure 1: true == ok
#
acIsValid(*objPath,*status) {
        *status=0
 	msiDataObjChksum(*objPath,"",*localCs)	
	acGetOrigChecksum(*objPath,*origCs)
	if (*localCs == *origCs) {
		acVerifyChecksumFed(*origCs,*objPath,*errors)
		if (*errors == 0) { 
			acVerifyChecksum(*objPath, *status)	
		}
	}
	acLog("*objPath has state *status in IsValid()")
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

# Gets a list of foreign host names (not zones names) connected to this node
# INPUT forbiddennodes, nodes as zone names to which the syning is disallowed
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

# Gets a List of zone names connectect to this node
# OUTPUT zone names
# INPUT forbidden zone names to be not included in *zones
# author: Jens Peters

acGetZonesOnGrid(*zones,*forbiddenNodes) {
        *zones=list()
        *zn="";
        msiAddSelectFieldToGenQuery("ZONE_NAME","null", *GenQ2)
        msiAddSelectFieldToGenQuery("ZONE_TYPE","null", *GenQ2)
        msiAddSelectFieldToGenQuery("ZONE_CONNECTION","null", *GenQ2)
        msiAddConditionToGenQuery("ZONE_TYPE"," = ","remote",*GenQ2)
        *attrVall=split(*forbiddenNodes, ",");
        foreach(*attrVal in *attrVall) {
             msiAddConditionToGenQuery("ZONE_NAME"," != ",*attrVal,*GenQ2)
        }
        msiExecGenQuery(*GenQ2, *zonesRes)
        foreach(*zonesRes){
                 msiGetValByKey(*zonesRes,"ZONE_NAME",*zone);
                *zn="*zone;*zn"
        }
        msiCloseGenQuery(*GenQ2,*out)
        *zones=split(*zn,";")
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

# Helper for retrieving the orginal checksum
# Author: Jens Peters
acGetOrigChecksum(*objPath,*chksum) {
	*orig="X"
	acGetAVUField(*objPath,"chksum",*orig)
	*chksum=*orig
}

# Federates items to the zones list 
# zones list must be list(list(zone, sizeBytes))
# list successfully synced zones to successZones
# INPUT srcDao the sourceDao to be syned
# INPUT zones the zones to synchronize to
# OUTPUT successZones as comma separated values
# Author Jens Peters
#
acFederateToZones(*coll,*dao,*destResc,*zones,*successZones,*min_copies) {
	*ok=0
	*err=0
	*successZones=""
	*chksum="X"
	acGetOrigChecksum("*coll/*dao",*chksum)		
	foreach(*zones){
		if (*ok < *min_copies) {
            		*zone=elem(*zones,0)
            		*mspace=elem(*zones,1)
			*destColl="/*zone/federated*coll"
			*err=errorcode(msiCollCreate(*destColl,"1",*nope))
			*destDao="*destColl/*dao"
			*log="Synchronize *coll/*dao to *destDao *zone which has already *mspace B used"
           		acLog(*log)	
			*error=errorcode(msiDataObjRsync("*coll/*dao","IRODS_TO_IRODS",*destResc,*destDao,*Status))
           		if (*error < 0 ) {
                   		*err=*err+1
                    		*log="recieved errorcode *error while synchronzing *coll/*dao to *destDao"
				 acLog(*log)
			} else {
                   	*ok=*ok+1       
   	        	*successZones="*zone,*successZones"
			*log="Synchronized *coll/*dao"
			acLog(*log)
			msiString2KeyValPair("chksum=*chksum",*kvpaircs3)
             		msiSetKeyValuePairsToObj(*kvpaircs3,"*destDao","-d")
			}
		}
 	}
}

# Synchronizes Source Collections of n zones to given Collection
# Useful for all kinds of distributed actions
# Author Jens Peters
acSynchronizeZonesToCollection(*zones,*srcCollWithoutZone,*destColl,*destResc,*deleteSource,*status) {
	*status=0
	# as workaround for some weird api bug this is deactivated
	*deleteSource=0
	*err=errorcode(msiCollCreate(*destColl,"1",*nope))
	if (*err < 0 ) {
             *log="recieved errorcode *error while creating *destColl"
             acLog(*log)
        }
	foreach(*zones){
		*srcColl="/*zones*srcCollWithoutZone"
		acLog("Synchronizing now *srcColl to *destColl")
		*error=errorcode(msiCollRsync(*srcColl,*destColl,*destResc,"IRODS_TO_IRODS",*Status))
                 if (*error < 0 ) {
                        *log="recieved errorcode *error while synchronizing *srcColl to *destColl"
                        acLog(*log)
                 } else {
			 if (*deleteSource==1) {
				*er=errorcode(msiRmColl(*srcColl,"forceFlag=",*status))
				if (*er < 0 ) {	
					acLog("recieved *er while trying to delete sourceColl *srcColl")
				} else {
					acLog("sucessfully deleted *srcColl")
				} 
			}
		} 		
	}
	*status=1
} 

# Gets a List of Hostnames ordered by the actual 
# amount of free space on resource found in given resc group naem. 
# Depends on locally running ServerMonitoring rules by Jean Yves
# Author Jens Peters
# RETURNS *servers as list(list("zone_name","mb")) by zone names ordered by free space
# 
acGetHostsOrderedByDataStoredAsc(*servers, *rg,*forbiddenNodes) {
	*hst=""
        *ls=""
        *mbyte=0
        *hosts=list()
        acGetHostsOnGrid(*hosts,*forbiddenNodes)
        foreach(*hosts) {
                *err=errorcode(remote(*hosts,"null") {
                        *resource=""
                        acGetRescForRg(*rg,*resource)
                        acGetUsedSpaceOnResc(*resource,*mbyte,"aip")
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
	# Do the bubble sort
	*n=size(*servers)-1
        *unsortiert=1
        while (*unsortiert==1) {
                *unsortiert=0
                for(*i=0; *i < *n; *i=*i+1) {
                        *t1=int(elem(elem(*servers,*i),1))
                        *t2=int(elem(elem(*servers,*i+1),1))
                        if (*t1 < *t2) {
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
acFederateLeastLoaded(*coll,*dao,*destResc,*successZones,*forb,*min_copies) {
  	*zones=""	 
	acGetHostsOrderedByDataStoredAsc(*zones,*destResc,*forb)
        acFederateToZones(*coll,*dao,*destResc,*zones,*successZones,*min_copies)
}

# Helper Function for Syncing results and stoing AVU Metadata to object
# Final method to mark objects as correctly federated
# INPUT dao The Data object
# INPUT successful synced zones
# INPUT min_copies
# Author: Jens Peters
#
acPrintSyncResults(*dao,*syncs,*min_copies) {
        *copies=size(split(*syncs,","))
        #assuming one copy for the local node
	*copies=*copies+1
	 if (*copies == 1) {
             acLog("Not reached any zone!")
         }
         if (*copies > 1) {
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

# Determines the used space in Bytes per collection and resc_name 
# INPUT resc_name the resource name
# OUTPUT bytes used
# INPUT Collection name
acGetUsedSpaceOnResc(*resc,*byte,*coll) {
	*out=0
        msiExecStrCondQuery("SELECT sum(DATA_SIZE) where COLL_NAME like '%/*coll/%' and RESC_NAME = '*resc' ",*lc)
        foreach(*lc) {
                msiGetValByKey(*lc,"DATA_SIZE",*out);
        }
	writeLine("stdout","RESC *resc *out B")
        *byte=*out
}
#old fashioned rules, partly used for backward compatibility
@backwardCompatible "true"

#STRICTLY check ACL 
acAclPolicy||msiAclPolicy(STRICT)|nop

acPostProcForDelete||nop|nop

