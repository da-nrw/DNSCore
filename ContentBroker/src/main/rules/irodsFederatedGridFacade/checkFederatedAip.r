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
#	delay("<EF>60m</EF>") {
	acLog("---Audit Service Started---")
        acCheckRecievedFederatedCopies(*admin, *numbersPerRun,*trustYears)	
	acLog("---Audit Service ended---")
#}
}
INPUT *admin=$"test@test.de", *numbersPerRun=$5,*trustYears=$0
OUTPUT ruleExecOut
