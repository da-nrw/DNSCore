# DNS Rule Base dns.re Build No.: BUILD_NUMBER
#
# LVR InfoKom 2015
# This file is published under the GPL v3
#  DA-NRW Software Suite
#   Copyright (C)
#   2015 LVR InfoKom
#
#   This program is free software: you can redistribute it and/or modify
#   it under the terms of the GNU General Public License as published by
#   the Free Software Foundation, either version 3 of the License, or
#   (at your option) any later version.
#
#   This program is distributed in the hope that it will be useful,
#   but WITHOUT ANY WARRANTY; without even the implied warranty of
#   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#   GNU General Public License for more details.
#
#   You should have received a copy of the GNU General Public License
#   along with this program.  If not, see <http://www.gnu.org/licenses/>.
#
# Be careful by changing settings in this file, failures might
# harm your capability of working with DNS.
#
# https://github.com/da-nrw
#
# 1. Store this file to /etc/irods/ as root
# 2. Please be sure to have this file enabled in your server_config.json
# "re_rulebase_set": [
#        {
#                "filename": "dns"
#        },
#        {
#                "filename": "core"
#        }
#    ],
# 3. Check for the rescNames addressed as ARCHIVERESCGROUP here, they must fit to your environment!
#
#The ressources on which we aren't allowed to delete items on
acDataDeletePolicy {ON($rescName == "ARCHIVERESCGROUP") {msiDeleteDisallowed; }}

#Enforce ACL
acAclPolicy {msiAclPolicy("STRICT"); }

#Enforce LTA Group for certain paths
acSetRescSchemeForCreate {ON($objPath like "*/aip/*") {
*lta="ARCHIVERESCGROUP"
msiWriteRodsLog("$objPath recieved, writing to Resc Group: *lta",*out)
msiSetDefaultResc(*lta,"preferred")} }
