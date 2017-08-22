package daweb3
/*
 DA-NRW Software Suite | ContentBroker
 Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
 Universität zu Köln, 2014 LVRInfoKom

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

/**
 * @author gbender
 */
import java.text.SimpleDateFormat;

import grails.plugin.springsecurity.annotation.Secured

import org.springframework.dao.DataIntegrityViolationException

class FormatMappingController {
	
	def springSecurityService
	 
	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	
	
    def map(){
		FormatMapping formatMapping = new FormatMapping()
		def formatMappings = null
		def msg = null;
		def admin = 0;
		
		if (springSecurityService.currentUser.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
	   
		if (params.order != null ) {
			formatMappings = formatMapping.findAll("from FormatMapping  order by ${params.sort} ")
		} else {
			formatMappings = formatMapping.findAll("from FormatMapping  order by puid desc")
		}
			
		msg  = params.get("msg");

		// list of results
		[formatMappings : formatMappings, msg:msg]
				
		render (view: 'map', model: [formatMappings : formatMappings, msg:msg,
							 user: springSecurityService.currentUser,
						 admin: admin]);
		
	}
		
	/**
	 * read the xml-File und fill the table format_mapping 
	 * @return
	 */
	def deleteAndFill() {
		def user = springSecurityService.currentUser
		def relativeDir = user.getShortName() + "/incoming"
		
		def baseFolder = grailsApplication.config.localNode.userAreaRootPath + "/" + relativeDir
		def msg
		def baseDir
		def incomingXmlFile
		def fileToParse
		def datum;
		
		try {
			baseDir = new File(baseFolder)
			if (!baseDir.exists()) {
				msg = "Benutzerordner nicht gefunden"
				log.error(msg);
			}
			
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd")
			datum = dateFormatter.format(new Date()).toString()
			
			incomingXmlFile = "DROID_SignatureFile_" + datum + ".xml"
			fileToParse = baseDir.toString() + "/" + incomingXmlFile
			
			def pronomXml = new XmlSlurper().parse(fileToParse)
			def String mapExt = "";  
			FormatMapping mapping = new FormatMapping()
			
			// first the table has to be purged
			FormatMapping.executeUpdate("delete FormatMapping")
			
			pronomXml.FileFormatCollection.FileFormat.each {
				
				mapping = new FormatMapping()
				mapping.puid = it.@PUID.toString()
				
				if (it.Extension.size() > 1) {
					int counter = 0;
					while (it.Extension.size() > counter ) {
						if (counter == 0) {
							mapExt =it.Extension.getAt(counter)
						} else {
							mapExt  = mapExt + "/" + it.Extension.getAt(counter)
						}
						counter = counter + 1;
					}
					mapping.extension = mapExt
				} else {
					mapping.extension = it.Extension.toString()
				}
				mapping.mimeType = it.@MIMEType.toString()
				mapping.formatName = it.@Name.toString()
				mapping.creationDate = new Date() 
				
//				mapping.save()
				def ms = mapping.save flush:true
				if (!ms) {
					println ("mapping.save: " + ms);
					mapping.errors.allErrors.each {
						println it
					}
				}
			}
 		redirect (action: "map")
		} catch (e) {
			msg = "Benutzerordner " + baseFolder + " oder Datei " + fileToParse + " existiert nicht!"
			log.error(msg, e) 
			
			return redirect( action : "map" , params:[msg:msg])
		}
	}
	
}
