leastloadedResc {
	*servers=""
	acGetHostsOrderedByFreeSpaceOnGridDesc(*servers,"lza","")	
	writeLine("stdout","ordered by load : *servers" )
}
INPUT null
OUTPUT ruleExecOut
