# DA-NRW Rule Base
# Most of the listed operations now are performed by ContentBroker
 
# Please change both replList settings to the appropiate settings of your system,
# according to the order, you want to replicate them on. 
# Be careful by changing settings in this file. 
# Be sure, iRODS Server works with this file by having activated it in reRuleSet 
# And iRODS Server is still working. Try with ils. 

#The ressources on which we aren't allowed to delete items on
acDataDeletePolicy {ON($rescName == "") {msiDeleteDisallowed; }}
acDataDeletePolicy {ON($rescName == "") {msiDeleteDisallowed; }}
acDataDeletePolicy {ON($rescName == "") {msiDeleteDisallowed; }}

#Returns a string of ReplDestinations given in Resc Group Names
acGetReplDests(*replDests) {
*replDests = "";
}

#Returns myNode and myserver
acGetMyNode(*myNode,*myServer) {
*myNode = "";
*myServer = ""
}

#Returns Node Admin email address
acGetNodeAdmin(*email) {
*email = ""
}

#Action to replicate Objects to resources
acReplicate(*objPath,*rescLoc) {
 delay("<EF>30s REPEAT UNTIL SUCCESS</EF>") {
                #PLEASE CHANGE IF NECESSARY: we have to repeat the settings for the repls, due to the RE calling us
                *replList = list("","");
				*rescName = "";
                *err=0;
                foreach(*repl in *replList)
                {

                    *error=errorcode(remote(*rescLoc,"null") {
							writeLine("serverLog","Replicate *objPath on *rescLoc to *repl: delayed after put operation");
							msiDataObjRepl(*objPath,"backupRescName=*repl++++rescName=*rescName++++all=++++verifyChksum=",*status);
							});
                    if (*error<0){ *err=*err+1; }
                }
                #if there are errors we'll fail and retry later, until we recieve a complete success in repl
                if (*err>0) { fail(); }
                #Trim the caches
                msiExecStrCondQuery("SELECT RESC_NAME WHERE RESC_GROUP_NAME = 'cache' ",*rescl);
                foreach(*rescl) {
                    msiGetValByKey(*rescl,"RESC_NAME",*rn);
                    msiDataObjTrim(*objPath,*rn,"null","null","null",*status);
                }
			    #Trim TSM Cache
				msiExecStrCondQuery("SELECT RESC_NAME WHERE RESC_GROUP_NAME = 'tsm' and RESC_NAME like '%cache%' ",*rescl);
                foreach(*rescl) {
                    msiGetValByKey(*rescl,"RESC_NAME",*rn);
                    msiDataObjTrim(*objPath,*rn,"null","null","null",*status);
                }
				
}
}

#Author: Jens Peters
#Archival rule, works on the aip folder 
#computes Checksum on DO
#triggers the delayed execution on other nodes
acPostProcForPut { ON($objPath like "/da-nrw/aip/*.tar") {
	#acGetMyNode(*node,*myServer)
	#msiWriteRodsLog("Replicate $objPath to LZA: *node after put operation on $rescLoc", *out);
	#msiDataObjRepl($objPath,"backupRescName=*node++++all=++++verifyChksum=",*status);
	*obj=$objPath
	*rl=$rescLoc
	#acPostIngestOperations(*obj, *rl)	
}
}

#Author: Jens Peters
acPostProcForFilePathReg { ON($objPath like "/da-nrw/aip/*.tar") {
	#if (errorcode(acPostIngestOperations($objPath, $rescLoc)) < 0) {
	#	cut;
	#	fail;
	#}
}
}	

acPostIngestOperations(*obj, *rl) {
	if (errorcode(acComputeChecksum(*obj)) < 0) {
        cut;
        fail;
    }
    if (errorcode(acReplicate(*obj,*rl)) < 0) {
        cut;
        fail;
    }
}

#Author: Jens Peters
#Returns 0 or 1 (true or false) if the given objPath is 
#available on the required resources
acCheckReplPolicy(*objPath,*min_repls,*status){
     *status=0;
    if (*min_repls==0){ fail;}
    *lzares="";
    *replDests="";
    acGetReplDests(*replDests)
    *replList=split(*replDests, ",");
    foreach(*repl in *replList){
        msiExecStrCondQuery("SELECT RESC_NAME WHERE RESC_GROUP_NAME = '*repl'",*rescl);
        foreach(*rescl) {
            msiGetValByKey(*rescl,"RESC_NAME",*rn);
            *lzares="'*rn',*lzares";
        }
    }
    #to support old style lza group
	*lzares="*lzares,'lza'";

	#sets MV To Object and Coll
    #replicate_to
    msiString2KeyValPair("replicate_to=*replDests",*kvpaircs)
	msiSplitPath(*objPath,*coll,*dn);
	msiSetKeyValuePairsToObj(*kvpaircs,*coll,"-C")
    msiSetKeyValuePairsToObj(*kvpaircs,*objPath,"-d")
	msiExecStrCondQuery("SELECT DATA_NAME,DATA_RESC_NAME,DATA_REPL_NUM WHERE COLL_NAME = '*coll' and DATA_NAME = '*dn' and RESC_NAME in (*lzares) and RESC_NAME NOT LIKE '%cache%'",*grepls);
    *i=0;
    foreach(*grepls) {
        *i=*i+1;
    }

    if (*i>=*min_repls) {
         msiString2KeyValPair("replicated=1",*kvrepl)
		msiSetKeyValuePairsToObj(*kvrepl,*objPath,"-d")
		*status=1;
    } else { 
         msiString2KeyValPair("replicated=0",*kvrepl)
        msiSetKeyValuePairsToObj(*kvrepl,*objPath,"-d")
		*status=0; }
}
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
		msiSplitPath(*objPath,*coll,*dn);
		msiExecStrCondQuery("SELECT count(DATA_REPL_NUM) WHERE COLL_NAME = '*coll' and DATA_NAME = '*dn' and RESC_NAME in (*ress) ",*grepls);
		*nr=0;
    	foreach(*grepls) {
         msiGetValByKey(*grepls,"DATA_REPL_NUM",*nr);
    	}
}

#Author: Jens Peters
#Checks the availibility of federated items in all zones being connected 
acCheckFederationPolicy(*myzone,*path,*nrfeds,*status){
    msiExecStrCondQuery("SELECT ZONE_NAME",*zones)
    *err=-1;
	*i=0;
	*status=0;
    foreach(*zones){
		msiGetValByKey(*zones,"ZONE_NAME",*fedzone);
        if (*myzone!=*fedzone) {
			*err=errorcode(msiObjStat("/*fedzone/*path",*out));
        	writeLine("serverLog","*err for /*fedzone/*path");
			if (*err==0 ) {
           		*i=*i+1;
        	}
		}
    }
    if (*i>=*nrfeds) {
        *status=1;
    } else { *status=0; }

}

#Autor: Jens Peters
#Checks the integrity of a DO.
#integrity means that a DO has the required nums of min_repls and
#has the same checksum it had, when we first ingested it. 

# 0=failure, either the min_repls is not achieved or the checksum failed.
# 1=ok the object status is ok.
# 2=To be rechecked (e.g. Resource unavailable)
acVerifyChecksum(*objPath,*status){
	*status=0;
	*checksumchk=0;
	*replchk=0;
	msiGetSystemTime(*systime,nop)
	*error=errorcode( msiDataObjChksum("*objPath","ChksumAll=++++verifyChksum=",*status))
        if (*error < 0 ) {
                    *csf=""
                    if (*error == -314000 ) {
                        *csf="CHECKSUM "
			*checksumchk=0
                    }
		    if (*error != -314000 ) {
                        *checksumchk=0
                    }
                    writeLine("stdout","SERVERE *csf FAILURE: *error *objPath");
                    msiWriteRodsLog("*csf FAILURE: *error *objPath",*junk) 
         } else { *checksumchk=1 }
         msiString2KeyValPair("checked=*checksumchk",*kvpaircs)
         msiSetKeyValuePairsToObj(*kvpaircs,"*objPath","-d")
         msiString2KeyValPair("last_checked=*systime",*kvpairts)
         msiSetKeyValuePairsToObj(*kvpairts,"*objPath","-d")
		*status=*checksumchk;
}


#old fashioned rules, partly used for backward compatibility
@backwardCompatible "true"

#STRICTLY check ACL 
acAclPolicy||msiAclPolicy(STRICT)|nop

acPostProcForDelete||nop|nop

#Jens Peters
#computes Checksum on DO
acComputeChecksum(*destObject)||msiWriteRodsLog("CS operation on *destObject", *out)##msiDataObjChksum(*destObject,"forceChksum=",*chksum)##msiWriteRodsLog("recieved *chksum",*out)##msiString2KeyValPair("chksum=*chksum",*kvpaircs)##msiSetKeyValuePairsToObj(*kvpaircs,*destObject,"-d")##msiGetSystemTime(*systime,nop)##msiString2KeyValPair("last_checked=*systime",*kvpairts)##msiSetKeyValuePairsToObj(*kvpairts,*destObject,"-d")|nop

#Jens Peters, to secure insert into queue from added temp files done by webdav: we know it is a regular ZIP file: ->URN
#sets status to 100
#we check if we've got the Packagename already in the Queue.
#acPostProcForPut|($objPath like /da-nrw/home/*) && ($objPath not like /da-nrw/home/*._*) && ($dataSize > 0)&& ($objPath not like /da-nrw/home/*/outgoing/*.tar)|msiWriteRodsLog('STANDARD package recieved: $objPath, $dataSize',*junk)##acPutObject($objPath,$rescLoc,$rescName,100)|nop

#Jens Peters
#call checksum computation
acPostProcForObjRename(*sourceObject,*destObject)|(*destObject like /da-nrw/aip/*.tar)|acComputeChecksum(*destObject)|nop

#Jens Peters
#computes Checksum on DO
acPostProcForObjCreate|($objPath like /da-nrw/aip/*.tar)|acComputeChecksum($objPath)|nop

#Author Jens Peters
#Doing PostPutOperations on Object, fetch URN, check if not already registered in queue and register it into the queue
#URN fetcher hardcoded on da-nrw, needs to be TODO migrated to C function
acPutObject(*objPath, *rescLoc, *rescName, *status)||msiSplitPath(*objPath,*incomingColl,*orig_name)##msiSplitPath(*incomingColl,*trash,*contractorShortName)##msiWriteRodsLog('acPutObject called for *objPath',*trash)##msiWriteRodsLog('*contractorShortName fetch URN and queue insert for *objPath on ressource *rescName',*junk)##msiStrlen(*orig_name,*orig_name_length)##assign(*strlen,*orig_name_length - 4)##msiSubstr(*orig_name,0,*strlen,*orig_name_stripped)##msiIsPackageNotAlreadyRegisteredInQueue(*contractorShortName,*orig_name_stripped,*output)##ifExec(*output == 0,msiGetSystemTime(*date_created,null)##msiGenericSQL("insert into queue (contractor_short_name,orig_name,status,date_created,initial_node) values ('*contractorShortName','*orig_name_stripped',*status,'*date_created','*rescLoc')"),msiWriteRodsLog(something failed while adding *orig_name to queue,*out),msiWriteRodsLog(already registered Object *orig_name in Queue, *junk),nop)|nop

#Read on outgoing path
acPreprocForDataObjOpen|($writeFlag == 0) && ($objPath like /da-nrw/home/*/outgoing/*)|msiWriteRodsLog(open for read AIP on outgoing path, *junk)##msiSetDataObjPreferredResc(cache)##msiSplitPath($objPath,*trash,*data_name)##msiStrlen(*data_name,*length)##assign(*strlen,*length - 4)##msiSubstr(*data_name,0,*strlen,*data_name)##msiWriteRodsLog(set user has read item status for AIP *data_name, *junk)##msiGetSystemTime(*systime,nope)##msiGenericSQL("UPDATE queue set status='960', date_modified=*systime WHERE urn='*data_name' and status='950'")|nop

#Read access on archival storage path
acPreprocForDataObjOpen|($writeFlag == 0) && ($objPath like /da-nrw/aip/*)|msiWriteRodsLog(open for read AIP on archival storage path from $userNameClient, *junk)|nop

#Author Jens Peters
#Updates status of URN in Queue
acUpdateStatus(*status,*id)||msiWriteRodsLog("Set Status of *id to *status",*junk)##msiGetSystemTime(*systime,nope)##msiGenericSQL("UPDATE queue set status='*status', date_modified=*systime WHERE id='*id'")|nop
