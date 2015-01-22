# Provides synchronizing Service 

# Author: Jens Peters
# This file is licenced under the GPLv3 as the main core of DNS, too.
#
# DA-NRW Software Suite
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
synchronizeService {
	#delay("<PLUSET>1m</PLUSET><EF>1m</EF>") {
	acLog("---started Synchronize Service---");
	msiExecStrCondQuery("SELECT DATA_NAME, COLL_NAME, META_DATA_ATTR_NAME, META_DATA_ATTR_VALUE where COLL_NAME like '/*homezone/aip/%' and META_DATA_ATTR_NAME = 'FEDERATED' and META_DATA_ATTR_VALUE = '0'",*colls)
	foreach(*colls) {
        msiGetValByKey(*colls,"COLL_NAME",*srcColl);
		msiGetValByKey(*colls,"DATA_NAME",*dao);
        msiSplitPath(*srcColl,*cColl,*objId)
		msiSplitPath(*cColl,*par,*chil)
		msiSplitPath(*par,*par,*aip)
		if (*aip=="aip") {
			federateObject(*srcColl,*dao,*destResc,*min_copies)
		}

	}
	acLog("---Ended Synchronize Service---");
#}
}
INPUT *destResc=$"lza", *homezone=$"lvr", *min_copies=$3
OUTPUT ruleExecOut

