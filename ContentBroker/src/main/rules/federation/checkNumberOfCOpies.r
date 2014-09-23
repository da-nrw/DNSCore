checkNumber {
	*numberOfCopies=0
	acGetNumberOfCopies(*dao,*numberOfCopies)
	writeLine("stdout","Copies # *numberOfCopies")
}
INPUT *dao="/krz/aip/TEST/7-20140922234/7-20140922234.pack_1.tar" 
OUTPUT ruleExecOut
