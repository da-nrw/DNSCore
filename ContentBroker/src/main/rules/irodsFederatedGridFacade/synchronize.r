# Provides synchronizing Service 

# Author: Jens Peters
# This file is licenced under the GPLv3 as the main core of DNS, too.
#
# DA-NRW Software Suite 2014 LVR InfoKom
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
synchronizeService {
	#delay("<PLUSET>1m</PLUSET><EF>6h</EF>") {
	acLog("---started Synchronize Service---");
	msiGetSystemTime(*timeNow,"")
	*ts=(*retryOlderThanHours*3600)
	msiExecStrCondQuery("SELECT DATA_NAME,DATA_MODIFY_TIME, COLL_NAME, META_DATA_ATTR_NAME, META_DATA_ATTR_VALUE where COLL_NAME like '/*homezone/aip/%' and META_DATA_ATTR_NAME = 'FEDERATED' and META_DATA_ATTR_VALUE = '0'",*colls)
	foreach(*colls) {
        msiGetValByKey(*colls,"COLL_NAME",*srcColl);
		msiGetValByKey(*colls,"DATA_NAME",*dao);
        	msiSplitPath(*srcColl,*cColl,*objId)
		msiSplitPath(*cColl,*par,*chil)
		msiSplitPath(*par,*par,*aip)
		if (*aip=="aip") {
			msiGetValByKey(*colls,"DATA_MODIFY_TIME",*dmt)
			msiGetDiffTime(*dmt,*timeNow,"",*et)
			if (int(*et)>*ts) {
				acLog("Retry synchronizing *srcColl/*dao last attempt is older than *retryOlderThanHours Hours in the past")
				federateObject(*srcColl,*dao,*destResc,*min_copies)
			}
		}

	}
	acLog("---Ended Synchronize Service---");
#}
}
INPUT *destResc=$"lza", *homezone=$"lvr", *min_copies=$3, *retryOlderThanHours=$24
OUTPUT ruleExecOut

