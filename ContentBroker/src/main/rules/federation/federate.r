# Federates items between connected zones based on time schedule
# Jens Peters, LVR InfoKom 2014
federateService {
	delay("<PLUSET>1m</PLUSET><EF>REPEAT FOREVER</EF>") {
	*zones=""
	writeLine("serverLog","started Federation Service");
	msiExecStrCondQuery("SELECT COLL_NAME where COLL_NAME like '/*homezone/aip/%'",*colls)
	foreach(*colls) {
                msiGetValByKey(*colls,"COLL_NAME",*srcColl);
                msiSplitPath(*srcColl,*par,*chil)
		msiSplitPath(*par,*par2,*chil2)
		*sql=""
		if (*chil2=="aip") {
			msiExecStrCondQuery("SELECT META_COLL_ATTR_NAME, META_COLL_ATTR_VALUE where COLL_NAME = '*srcColl'",*attrs)
			*forb=""
			*syncs=""
			foreach(*attrs) {
				msiGetValByKey(*attrs,"META_COLL_ATTR_NAME",*attr);
				if (*attr=="FORBIDDEN_NODES"){
					msiGetValByKey(*attrs,"META_COLL_ATTR_VALUE",*forb);	
				} 
				if (*attr=="SYNCHRONIZED_TO"){
					msiGetValByKey(*attrs,"META_COLL_ATTR_VALUE",*syncs);
				}
			}
			*sync=split(*syncs,",")
			if (size(*sync)==0) {
				acGetHostsOrderedByFreeSpaceOnGridDesc(*zones,*destResc,*forb)
				acFederateToZones(*srcColl,*zones,*successZones)
				*syncs=*successZones
			} else {
				*syncs="*syncs*forb"
				acGetHostsOrderedByFreeSpaceOnGridDesc(*zones,*destResc,*syncs)
				acFederateToZones(*srcColl,*zones,*successZones)
				*syncs="*syncs*successZones"
			}
			writeLine("stdout","Replicated to [*successZones]")
                        msiGetSystemTime(*hu,*bulk)
                        msiString2KeyValPair("SYNCHRONIZE_EVENT=*hu",*kvpaircs)
                        msiSetKeyValuePairsToObj(*kvpaircs,*srcColl,"-C")
                        
			msiString2KeyValPair("SYNCHRONIZED_TO=*syncs",*kvpaircs2)
                        msiSetKeyValuePairsToObj(*kvpaircs2,*srcColl,"-C")
			writeLine("serverLog","---Ended Federation Service---");
			msiCloseGenQuery(*GenQ,*status)
		}

	}
	}
}
INPUT *destResc="lza", *homezone="krz", *min_repls=3
OUTPUT ruleExecOut
