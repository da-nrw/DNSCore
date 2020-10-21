package daweb3
/*
 DA-NRW Software Suite | ContentBroker
 Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
 Universität zu Köln

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
import java.io.File

/**
 * 
 * @author Jens Peters DANRW 2013
 * Lists all Objects being successfully retrieved beneath the outgoing folder.
 * Redirects user to the WebServer. Marks the objects as being read by the user. (Status 960)
 *
 */

class OutgoingController {

	def springSecurityService
	
    def index() {
		def user = springSecurityService.currentUser
		def relativeDir = user.getShortName() + "/outgoing"
		def baseFolder = grailsApplication.config.getProperty('localNode.userAreaRootPath') + "/" + relativeDir
		def baseDir
		def filelist = []
		def msg = ""
		def admin = 0;
		
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		
		try {
			baseDir  = new File(baseFolder)
			if (!baseDir.exists()) {
			msg = "Benutzerordner nicht gefunden"
			log.error(msg);
			}		
			
		baseDir.eachFileMatch(~/.*?\.tar/) { file -> filelist.add(file)}
		if (filelist.empty) msg ="Keine Dateien im Ausgangsordner gefunden";
			
		} catch (e) {
			msg = "Benutzerordner " + baseFolder+ " existiert nicht!"
			log.error(msg);
		}
		[filelist:filelist,
			msg:msg,
			user:user, admin:admin,
			basedir:baseDir]
	}

}
