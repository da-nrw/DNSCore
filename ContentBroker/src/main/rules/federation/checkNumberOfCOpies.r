checkNumber {
	*numberOfCopies=0
	acGetNumberOfCopies(*dao,*numberOfCopies)
	writeLine("stdout","Copies # *numberOfCopies")
}
INPUT *dao="/krz/aip/TEST2/78910/testpackage_pack1.tgz" 
OUTPUT ruleExecOut
