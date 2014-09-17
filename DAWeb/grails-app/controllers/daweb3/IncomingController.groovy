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
 * Incoming Controller, lists all existing data objects 
 * beneath tje incomingfolderprefix + userfolder + icoming
 * for selecting them as work items for the archival workflow. 
 */

class IncomingController {
	
	def springSecurityService
	
    def index = { 
		def user = springSecurityService.currentUser
		
		def relativeDir = user.getShortName() + "/incoming"
		def baseFolder = grailsApplication.config.localNode.userAreaRootPath + "/" + relativeDir
		def msg = ""
		def baseDir;
		def filelist = []
		try {
			baseDir = new File(baseFolder)
			if (!baseDir.exists()) {
				msg = "Benutzerordner nicht gefunden"
				log.error(msg);
			}
		
		
		baseDir.eachFileMatch(~/^(?!\.).*?\.zip/) { file -> filelist.add(file)}
		baseDir.eachFileMatch(~/^(?!\.).*?\.tar/) { file -> filelist.add(file)}
		baseDir.eachFileMatch(~/^(?!\.).*?\.tgz/) { file -> filelist.add(file)}
		baseDir.eachFileMatch(~/^(?!\.).*?\.tar.gz/) { file -> filelist.add(file)}
		if (filelist.empty) msg ="Keine Dateien im Eingangsordner gefunden";
 	
		} catch (e) {
		msg = "Benutzerordner " + baseFolder+ " existiert nicht!"
		log.error(msg);
	
		}
			[filelist:filelist,
			 msg:msg]
	}
	
	def save = {
		def user = springSecurityService.currentUser
		
		def files = params.list("currentFiles")

		def msg = ""
		files.each {
			 log.info "Datei ${it}" 
			 
			 File source = new File(grailsApplication.config.localNode.userAreaRootPath +"/" 
				 		+ user.getShortName() + "/incoming/" + it);
			File target =  new File(grailsApplication.config.localNode.ingestAreaRootPath
				 		+"/"+user.getShortName() + "/"+ it)
			if (target.exists()) {
				msg = "Datei existiert bereits ${it}"
				[msg:msg]
				
				redirect(action:"index")
			}
			 try {
				 boolean fileMoved = source.renameTo(target);
				 if (!fileMoved) msg = "Fehler bei der Erstellung eines Arbeitsauftrages fuer ${it}"
			 	log.error "Fehler bei der Erstellung der Datei " + target.getAbsolutePath();
			} catch (Exception e) {
				msg = "Exception beim Verschieben der Datei ${it}"
				log.error "Datei ${it} Exception " + e.printStackTrace()
			}
				}
		[msg:msg]
		redirect(action:"index")
	}
	
	def fail = {
		
		
	}
}
