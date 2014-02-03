#Jens Peters 2013

#DA-NRW
#Performs needed Replciations of objects on ingest processing
performReplicationsNeeded {
    msiWriteRodsLog("started service: doing each *performAtMins Mins Replications on all AIPs on Ingest",*junk);
    *de="<EF>"++str(*performAtMins)++"m</EF>"
	delay(*de){                                                                                  
	*err=errorcode(msiExecStrCondQuery("SELECT META_DATA_ATTR_NAME, DATA_NAME, COLL_NAME, META_DATA_ATTR_VALUE WHERE META_DATA_ATTR_NAME = 'replicated' and META_DATA_ATTR_VALUE = '0'", *GenQOut));
    msiWriteRodsLog("Replication cleanup service for ingest started...",*junk);
	  if (*err != -808000) {
	foreach(*GenQOut) {
    	msiGetValByKey(*GenQOut,"COLL_NAME",*coll_name);
        msiGetValByKey(*GenQOut,"DATA_NAME",*data_name)    
	*err2=errorcode(msiExecStrCondQuery("SELECT META_DATA_ATTR_NAME, META_DATA_ATTR_VALUE WHERE COLL_NAME = '*coll_name' and DATA_NAME = '*data_name' and META_DATA_ATTR_NAME = 'replicate_to'", *GenQ));
		if (*err2 != -808000) {
		foreach(*GenQ) {
			msiWriteRodsLog("Queuing Replication of Object: *coll_name/*data_name",*junk)	 
			msiGetValByKey(*GenQ,"META_DATA_ATTR_VALUE",*mv);
             msiWriteRodsLog("Replicating to *mv",*junk)
		   	 *replList = split("*mv", ",");
			*i=0; 
			foreach(*replList) { 
				msiWriteRodsLog("Replicating to *replList",*junk)
				if (errorcode(msiDataObjRepl("*coll_name/*data_name","backupRescName=*replList++++all=++++verifyChksum=",*status)) < 0){
         			msiWriteRodsLog("Failure *replList Replication of Object named: *coll_name/*data_name",*junk)
				} 
				#we just do the replication here, we don't check for the fulfillment of our policy, this is CB's job for now!                   
			}
		} 
		} else { msiWriteRodsLog("No policy found for object!",*junk) }
	} 
	}
	msiWriteRodsLog("Replication ended...",*junk);
   } 
}
INPUT *performAtMins=$1
OUTPUT ruleExecOut

