package daweb3

import grails.converters.JSON
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
/**
 * 
 * @author Jens Peters
 * Actions dedicated to Packages of AIP Objects, 1:n
 * 
 */
class PackageController {
	
	static allowedMethods = [retrievePackages: "POST" ]
	
	def springSecurityService
	static QueueUtils qu = new QueueUtils();
	
	def show() {
		   def packageInstance = Package.get(params.id)
		   if (!packageInstance) {
			   flash.message = message(code: 'default.not.found.message', args: [message(code: 'object.label', default: 'Package'), params.id])
			   redirect(contoller: "object", action: "list")
			   return
		   }
		   [packageInstance: packageInstance]
	   }
	   
	def retrievePackages() {
		   def result = ""
		   if (request.getParameter("oid")==null) {
			   log.error ("Das Objekt ist null!")
			  flash.message = "Das Objekt darf nicht null sein!"
			  redirect(action: "error")
			  return
			}
		   
		   Object obj = Object.get(params.oid)
		   if (!obj) {
		   		flash.message = "Das Objekt konnte nicht gefunden werden!"
				redirect(action: "error")
				return
			}
		   
	   		User user = springSecurityService.currentUser
			if (user.getShortName() != obj.getUser().getShortName()) {
				flash.message = "Sie sind nicht berechtigt das Retrieval für das Package zu starten!"
				redirect(action: "error")
				return
			}
			def packagesIds = params.list("currentPackages")
			
			def packages = []
			packagesIds.each { 
				packages.add(Package.get(it).getName())
			}
			log.debug("RETRIEVE:"+packages.join(","))
			CbNode cbn = CbNode.get(grailsApplication.config.localNode.id)
			try {
			qu.createJob( obj, 900, cbn.getName(), "RETRIEVE:"+packages.join(",")) 
				result = "Packages konnten angefordert werden."
			} catch ( Exception e ) { 
				result = "Packages konnten nicht angefordert werden: "+ packages
				log.error(result + " "+e.printStackTrace())
			}		
			flash.message = result
			redirect(controller: "object", action: "show", id:obj.getId())
   
	}
	
	def error() {
			
	}
  }
