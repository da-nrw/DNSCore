# Jens Peters, LVRInfoKom 2014
checkItemsQuick {
	*status=0
	msiGetSystemTime(*out,"null")
	writeLine("stdout", "checking at *out")
	acIsValid(*dao,*status)
	if (*status==1) {
		writeLine("stdout","ALL Copies seem to be OK")
		} else {
		writeLine("stdout","At least one Copy seem to be NOK")
	}		
	
}
INPUT *dao="/krz/aip/TEST2/78910/testpackage_pack1.tgz"
OUTPUT ruleExecOut
