# Provides delayed eceuted Federation Service
# Author: Jens Peters
federateService {
	delay("<PLUSET>1m</PLUSET><EF>REPEAT FOREVER</EF>") {
	acLog("---started Federation Service---");
	msiExecStrCondQuery("SELECT DATA_NAME, COLL_NAME, META_DATA_ATTR_NAME, META_DATA_ATTR_VALUE where COLL_NAME like '/*homezone/aip/%' and META_DATA_ATTR_NAME = 'FEDERATED' and META_DATA_ATTR_VALUE = '0'",*colls)
	foreach(*colls) {
                msiGetValByKey(*colls,"COLL_NAME",*srcColl);
		msiGetValByKey(*colls,"DATA_NAME",*dao);
                msiSplitPath(*srcColl,*cColl,*objId)
		msiSplitPath(*cColl,*par,*chil)
		msiSplitPath(*par,*par,*aip)
		if (*aip=="aip") {
			msiExecStrCondQuery("SELECT META_DATA_ATTR_NAME, META_DATA_ATTR_VALUE where COLL_NAME = '*srcColl' and DATA_NAME = '*dao' ",*attrs)
			*forb=""
			*syncs=""
			foreach(*attrs) {
				msiGetValByKey(*attrs,"META_DATA_ATTR_NAME",*attr);
				if (*attr=="FORBIDDEN_NODES"){
					msiGetValByKey(*attrs,"META_DATA_ATTR_VALUE",*forb);	
				} 
                                if (*attr=="SYNCHRONIZED_TO"){
                                        msiGetValByKey(*attrs,"META_DATA_ATTR_VALUE",*syncs);
                                }
                        }
			*sync=split(*syncs,",")
			if (size(*sync)==0) {
				acLog("syncing for the first time")	
				acFederateLeastLoaded("*srcColl/*dao",*destResc,*successZones,*forb,*min_copies)
				*syncs=*successZones
			} else {
				*syncs="*syncs*forb"
				acGetHostsOrderedByFreeSpaceOnGridDesc(*zones,*destResc,*syncs)
				*lis=list();
				*syc=split(*syncs,",")
				foreach(*syc) {
					*lis=cons(list("*syc","9999"),*lis)
				}
				acLog("checking already synced Zones: *lis")
				acFederateToZones("*srcColl/*dao",*destResc,*lis,*successZonesAlready,*min_copies)
				acLog("now synching to other Zones")
				acFederateToZones("*srcColl/*dao",*destResc,*zones,*successZones,*min_copies)
                                *syncs="*syncs*successZones"
			}
			msiGetSystemTime(*hu,*bulk)
                        msiString2KeyValPair("SYNCHRONIZE_EVENT=*hu",*kvpaircs)
                        msiSetKeyValuePairsToObj(*kvpaircs,"*srcColl/*dao","-d")
			acPrintSyncResults("*srcColl/*dao",*syncs,*min_copies)	
		}

	}
	acLog("---Ended Federation Service---");
}
}
INPUT *destResc=$"lza", *homezone=$"krz", *min_copies=$3
OUTPUT ruleExecOut
