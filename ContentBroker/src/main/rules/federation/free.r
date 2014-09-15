checkSpace {
        acGetFreeSpaceOnResc("demoResc",*out)
        writeLine("stdout","freeSPace # *out")
	writeLine("stdout","minimaler Ressourcenstand *minimal")
	*servers=list(list("LDA-VT02-lvrintern.lvr.de","80"),list("LDA-VT03.lvrintern.de","200"),list("www.kcg.de","50"));
 	foreach(*servers) {
		*server=elem(*servers,0)
		*minmem=elem(*servers,1)
		writeLine("stdout","Server *server")
		writeLine("stdout","Server *minmem")
	}
	*n=size(*servers)-1
	*unsortiert=1
	while (*unsortiert==1) {	
		*unsortiert=0
		for(*i=0; *i < *n; *i=*i+1) {
			*t1=int(elem(elem(*servers,*i),1))
			*t2=int(elem(elem(*servers,*i+1),1))
			if (*t1 > *t2) {
				*tempL1=elem(*servers,*i)
				*tempL2=elem(*servers,*i+1)
				*servers=setelem(*servers,*i,*tempL2)
				*servers=setelem(*servers,*i+1,*tempL1)
				*unsortiert=1	
			}
		}  
	}
	writeLine("stdout","--------")
	    foreach(*servers) {
                *server=elem(*servers,0)
                *minmem=elem(*servers,1)
                writeLine("stdout","Server *server")
                writeLine("stdout","Server *minmem")
		}
}
INPUT null
OUTPUT ruleExecOut
