# Jens Peters, LVRInfoKom 2014
# checks integrity of federated items on the local zone
#  DA-NRW Software Suite
#  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
#  Universität zu Köln, 2014 LVR InfoKom
#
#  This program is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program.  If not, see <http://www.gnu.org/licenses/>.
checkFederatedItemsOnLocalZone {
	delay("<EF>60m</EF>") {
	acLog("---Audit Service Started---")
	*i=1
	msiExecStrCondQuery("SELECT DATA_NAME, COLL_NAME, META_DATA_ATTR_NAME, META_DATA_ATTR_VALUE where COLL_NAME like '%/federated/%' and META_DATA_ATTR_NAME = 'last_checked' ORDER BY META_DATA_ATTR_VALUE ASC",*checkDaos)	
	foreach(*checkDaos) {
		acLog("No. *i")
		*status=0
		msiGetValByKey(*checkDaos,"DATA_NAME",*checkDao);
		msiGetValByKey(*checkDaos,"COLL_NAME",*checkColl);
		msiGetValByKey(*checkDaos,"META_DATA_ATTR_VALUE",*lc);
		*dao="*checkColl/*checkDao"
		acLog("checking ... *dao")
		acNeedCheck(*dao,*need,*trustYears)   
		
		if (*need==1) {
			msiDataObjChksum(*dao,"forceChksum=",*localCs)
        		acGetOrigChecksum(*dao,*origCs)
			acVerifyChecksum(*dao,*status)	
			if (*status==1) {
				if (*localCs != *origCs) {
					acLog("Federated COPY in E R R O R: *dao")
				} else {
					acLog("Copy seem to be OK")
				}
			} else {
				acLog("Federated COPY in E R R O R: *dao")
				if (*admin!="test@test.de"){
					msiSendEmail(*admin,"DNS-ERROR of federated copy",*dao)				
				}
			}

		  
		} else {
			acLog("No check needed on foreign copies yet!")
		} 	
		*i=*i+1		
		if (*i>=5) { break }
	}
acLog("---Audit Service ended---")
}
}
INPUT *zone=$"zone", *admin=$"test@test.de", *numbersPerRun=$5,*trustYears=$0
OUTPUT ruleExecOut
