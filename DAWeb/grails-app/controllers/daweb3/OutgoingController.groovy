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
		def relativeDir = user.getContractorShortName() + "/outgoing"
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
			user:user.username, admin:admin]

		
	}
	
	def download() {
		// set Queue Entry to read. 
		log.debug("Setting read status of file " + params.filename)
		def idn = params.filename.substring(0,params.filename.length()-4)
		log.debug("Setting read status of object <" + idn + ">")
		User user = springSecurityService.currentUser
		def que = QueueEntry.findAll("from QueueEntry as q where q.obj.user.contractorShortName=:csn and q.obj.identifier=:idn",
             [csn: user.getContractorShortName(),
				idn: idn])
		que.each {
			
			//it.setStatus("960")
			
			def modi = new Date()
			log.debug("Modified at:" + modi)
			
			it.setModifiedAt(modi)
			it.save();
		}
		def redirecturl = request.getHeader('referer');
		if (grailsApplication.config.transferNode.downloadLinkPrefix && grailsApplication.config.getProperty('transferNode.downloadLinkPrefix').trim().length() > 0) {
			redirecturl = grailsApplication.config.getProperty('transferNode.downloadLinkPrefix') +"/"+   user.getContractorShortName()  + "/outgoing/" + params.filename
		}
		redirect(url:redirecturl)
	}

}
