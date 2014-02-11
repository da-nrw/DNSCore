#DA-NRW Jens Peters 2013
#Rule keeps repository in synchronized state for a given Contractor in a single zone

#synchronizes all Collections beneath a given Contractor to all Ressgroups we should replicate to. 
#executed via cron: irule -F synchronize.r "\"TEST"\” for Contractor TEST for example

synchronize {
  *ContInxOld = 1;
  *Count = 0;
  *Size = 0;
  *missed=0;
  *failed=0;
  *replicated=0;
	msiMakeGenQuery("DATA_NAME, COLL_NAME, DATA_CHECKSUM","COLL_NAME like '/*zone/aip/*Contractor%%'",*GenQInp);
  msiExecGenQuery(*GenQInp, *GenQOut);
  msiGetContInxFromGenQueryOut(*GenQOut,*ContInxNew);
  while(*ContInxOld > 0) {
    if(*ContInxNew == 0) { *ContInxOld = 0; }
    foreach(*GenQOut) {
      msiGetValByKey(*GenQOut, "DATA_NAME", *dn);
      msiGetValByKey(*GenQOut, "COLL_NAME", *coll) 
      msiGetValByKey(*GenQOut, "DATA_CHECKSUM", *cs) 
	  writeLine("stdout","synchronizing *coll/*dn");
      acCheckReplPolicy("*coll/*dn",*min_repls,*status)
	  if (*status==0) {
		*missed=*missed+1
		writeLine("stdout","missed repl *coll/*dn");
		if (*cs=="") {
			writeLine("stdout","missed Checksum, too");
			acVerifyChecksum("*coll/*dn",*st)
		}

		acGetReplDests(*replDests) 
		*replList=split(*replDests, ",");
       		foreach(*repl in *replList){
				*err=0
				*err=errorcode(msiDataObjRepl("*coll/*dn","backupRescName=*repl++++all=++++verifyChksum=",*status));
				if (*err!=0) { 
                  	msiWriteRodsLog("Fehler *err bei Sync von *coll/*dn auf *repl",*out);
                  	writeLine("stdout","Fehler *err bei Sync von *coll/*dn auf *repl") 
             		*failed=*failed+1;
          		} else {
					writeLine("stdout","found *coll/*dn auf *repl")
				}
				if (*repl=="tsm") {
				    msiExecStrCondQuery("SELECT RESC_NAME WHERE RESC_GROUP_NAME = 'tsm' and RESC_NAME like '%cache%' ",*rescl);
                	foreach(*rescl) {
                    	msiGetValByKey(*rescl,"RESC_NAME",*rn);
                    	msiDataObjTrim("*coll/*dn",*rn,"null","null","null",*status);
                	}	
			   	} 
				
			}
        }
       *Count = *Count + 1;
	}
    if(*ContInxOld > 0) { msiGetMoreRows(*GenQInp,*GenQOut,*ContInxNew);}
  }
    writeLine("stdout","synchronizer stopped Synchronized in Total *Count Objects. *missed Objects missed. Failed in performing *failed Replications.");
}
INPUT *Contractor=$, *zone=$"da-nrw", *min_repls=$2
OUTPUT ruleExecOut
