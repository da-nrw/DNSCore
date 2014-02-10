#Jens Peters 2013
#DA-NRW

# Sends audit Report for given contractor collection to given address
# The report contains detalied information about the faulted resource name

AuditReport {
	*tx="sending each *performAtDays Day(s) Checksum-Report on *contractor_collection"
    msiWriteRodsLog("started service: *tx",*junk);
	*de="<EF>"++str(*performAtDays)++"d</EF>"
	msiWriteRodsLog(*de,*junk)
    *body=""
    *Select="distinct SELECT COLL_NAME, DATA_NAME, META_DATA_MODIFY_TIME WHERE COLL_NAME LIKE '*contractor_collection/%' and META_DATA_ATTR_NAME = 'checked' and META_DATA_ATTR_VALUE = '0'"
    *Subject="Checksum Test Report"
   delay(*de) {
	msiWriteRodsLog("reporting on *tx now",*junk)
	writeLine("stdout","*tx");
	writeLine("stdout"," ");
	*lc=""
	*empt=errorcode(msiExecStrCondQueryWithOptions(*Select, "zeroOk","1000", *GenQOut));
	if (*empt != -808000 ) { 
		*i=0
		foreach(*GenQOut) {
            *i=*i+1
		}
		writeLine("stdout","Found (*i) suspicious packages ");
		foreach(*GenQOut) {
   		 msiGetValByKey(*GenQOut,"COLL_NAME",*coll_name);
		 msiGetValByKey(*GenQOut,"DATA_NAME",*dn);
		 msiWriteRodsLog("reporting suspicious Data-Object *coll_name/*dn",*junk) 
		 msiExecStrCondQuery("SELECT META_DATA_MODIFY_TIME WHERE COLL_NAME = '*coll_name' and DATA_NAME = '*dn' and META_DATA_ATTR_NAME = 'last_checked'",*lastc)
		 foreach(*lastc) {
         		msiGetValByKey(*lastc,"META_DATA_MODIFY_TIME",*lc);
		 }
        msiGetSystemTime(*systime,nop)                                            
        *err=errorcode( msiDataObjChksum("*coll_name/*dn","ChksumAll=++++verifyChksum=",*status))
         if (*err < 0 ) {
             *check=0
         } else { *check=1 
			msiWriteRodsLog("*coll_name/*dn is now without any error",*junk)
			writeLine("stdout","*coll_name/*dn is checked ok in reporting scan");
		}
         msiString2KeyValPair("checked=*check",*kvpaircs)
         msiSetKeyValuePairsToObj(*kvpaircs,"*coll_name/*dn","-d")
         msiString2KeyValPair("last_checked=*systime",*kvpairts)
         msiSetKeyValuePairsToObj(*kvpairts,"*coll_name/*dn","-d")   
		 
		 if (*err < 0) {
		 msiExecStrCondQuery("SELECT DATA_NAME,DATA_RESC_NAME,DATA_REPL_NUM
                WHERE COLL_NAME = '*coll_name' and DATA_NAME = '*dn' and 
                RESC_NAME in (*lzares)",*grepls);
		 foreach(*grepls) {
                            msiGetValByKey(*grepls,"DATA_RESC_NAME",*rn);
                            msiGetValByKey(*grepls,"DATA_REPL_NUM",*repln);
                            *error=errorcode(msiDataObjChksum("*coll_name/*dn","verifyChksum=++++replNum=*repln",*ch))
							if (*error == -314000) {
                                 writeLine("stdout","Data-Object *coll_name/*dn")
								 writeLine("stdout","CORRUPT: Found faulted replica on *rn")
								 msiWriteRodsLog("Found faulted replica on *rn",*junk)
                            } else {
								if (*error != 0) {
									writeLine("stdout","Data-Object *coll_name/*dn")
								    writeLine("stdout","Found error *error reading on resource *rn")
								  	msiWriteRodsLog("Found error *error reading on resource *rn",*junk)
								} 
							}
          }
  		} 	
	}
}
writeLine("stdout","Seen on: *lc -End-");   
msiSendStdoutAsEmail(*Address,*Subject) 

}  
}
INPUT *contractor_collection=$"/da-nrw/aip/TEST", *Address=$"test@uni-koeln.de", *performAtDays=$1, *lzares=$"'sam-fs','san','tsm'"
OUTPUT ruleExecOut

