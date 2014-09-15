# Jens Peters, LVRInfoKom 2014
# checks integrity of items on the local zone
checkItemsOnLocalZone {
	msiGetSystemTime(*out,"null")
	writeLine("stdout", "checking at *out")
	msiExecStrCondQuery("SELECT DATA_NAME, COLL_NAME, META_DATA_ATTR_NAME, META_DATA_ATTR_VALUE where COLL_NAME like '/*zone/aip/%' and META_DATA_ATTR_NAME = 'last_checked' ORDER BY META_DATA_ATTR_VALUE ASC",*checkDaos)	
	foreach(*checkDaos) {
		*ownState=0
		*status=0
		*foreignState=0
		msiGetValByKey(*checkDaos,"DATA_NAME",*checkDao);
		msiGetValByKey(*checkDaos,"COLL_NAME",*checkColl);
		*dao="*checkColl/*checkDao"
		writeLine("stdout", "checking ... *dao")
		acVerifyChecksum(*dao,*ownState)
		acVerifyChecksumFedSync(*dao,*errors)
        	if (*errors==0) {
        	        *foreignState=1
        	}
       		if (*ownState==1 && *foreignState==1) {
                	*status=1
        	}
	        msiString2KeyValPair("checked_foreign=*status",*kvpaircs)
        	msiSetKeyValuePairsToObj(*kvpaircs,"*dao","-d")
		if (*status==1) {
					writeLine("stdout","ALL Copies seem to be OK")
				} else {
					writeLine("stdout","At least one Copy seem to be NOK")
		}		

	}
}
INPUT *zone=$"krz", *admin=$"test@test.de" 
OUTPUT ruleExecOut
