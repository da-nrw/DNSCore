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
 * The Main DA-NRW object Controller for listing Objects (AIP) stored in DNS 
 * @Author Jens Peters, Sebastian Cuy
 * 
 */


import org.springframework.dao.DataIntegrityViolationException
import grails.converters.*
import java.security.InvalidParameterException


class ObjectController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	static QueueUtils qu = new QueueUtils();
	
	def springSecurityService
	
    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
				User user = springSecurityService.currentUser
				
				def contractorList = User.list()
				def admin = 0;
				def relativeDir = user.getShortName() + "/outgoing"
				def baseFolder = grailsApplication.config.localNode.userAreaRootPath + "/" + relativeDir				
					params.max = Math.min(params.max ? params.int('max') : 10, 100)

					if (params.searchContractorName){
						if(params.searchContractorName=="null"){
							params.remove("searchContractorName")
						}
					}

					def c = Object.createCriteria()
					log.debug(params.toString())
					def objects = c.list(max: params.max, offset: params.offset ?: 0) {
						
						if (params.search) params.search.each { key, value ->
								like(key, "%" + value + "%")
						}
						
						
						
						if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
							admin = 1;
						}
						if (admin==0) {
						
							eq("user.id", user.id)
							
						}
						if (admin==1) {
							if (params.searchContractorName!=null) {
								createAlias( "user", "c" )
								eq("c.shortName", params.searchContractorName)
							}
						}
						between("object_state", 50,100)
						order(params.sort ?: "id", params.order ?: "desc")
					}
					log.debug(params.search)

					// workaround: make ALL params accessible for following http-requests
					def paramsList = params.search?.collectEntries { key, value -> ['search.'+key, value] }
					if(params.searchContractorName){
						paramsList.putAt("searchContractorName", params?.searchContractorName)
					}

					if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
						render(view:"adminList", model:[	objectInstanceList: objects,
						objectInstanceTotal: objects.getTotalCount(),
						searchParams: params.search,
						paramsList: paramsList,
						paginate: true,
						admin: admin,
						baseFolder: baseFolder,
						contractorList: contractorList]);
					} else render(view:"list", model:[	objectInstanceList: objects,
						objectInstanceTotal: objects.getTotalCount(),
						searchParams: params.search,
						paramsList: paramsList,
						paginate: true,
						admin: admin,
						baseFolder: baseFolder,
						contractorList: contractorList]);
    }

    def show() {
		def username = springSecurityService.currentUser
		
		def c = Object.createCriteria()
		def objectInstance;
		def contractor;
		User user = User.findByUsername(username)
		def admin = 0
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		if (admin==0) {
			objectInstance = Object.findByIdAndUser (params.id, user);
		}  else objectInstance = Object.get(params.id);
	    if (!objectInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'object.label', default: 'Object'), params.id])
            redirect(action: "list")
            return
        }
		def urn = objectInstance.urn
		urn = urn.replaceAll(~"\\+",":")	
		def preslink = grailsApplication.config.fedora.urlPrefix +urn.replaceAll(~"urn:nbn:de:danrw-", "")
        [objectInstance: objectInstance,
			urn:urn,preslink:preslink]
    }
    


		/**
		 * Creates retrieval jobs for the objects with the ids specified in params.check
     */
		def queueAllForRetrieval = {
			def result = [success:true]
			User user = springSecurityService.currentUser
			def admin = 0
			if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
				admin = 1;
			}
			result.msg = "Retrieving objects:\n"

			List<String> urnList = new ArrayList<String>();
				
			// If there is only one entry
			if (params.check.getClass()==String){
					def str = params.check
				params.check = new ArrayList<String>()
				params.check.add ( str )
			}
			for ( String objectId : params.check ) {
				def object = Object.get( objectId.toInteger() )
				if ( object == null ) {
					result.msg += "${object.urn} - NICHT GEFUNDEN. "
					result.success = false
				} else {
				if (object.user.shortName != user.getShortName()) {
					result.msg += "${object.urn} - KEINE BERECHTIGUNG. "
					result.success = false
				} else {
					try {
					CbNode cbn = CbNode.get(grailsApplication.config.localNode.id)
					
					qu.createJob( object ,"900", cbn.getName()) + "\n"	
					result.msg += "${object.urn} - OK. " 
					} catch ( Exception e ) { 
					result.msg += "${object.urn} - FEHLER. "
					result.success = false
					}
				}
				}
			}
			render result as JSON
		}


		/**
		 * Create Queue Entry for Retrieval
		 */


    def queueForRetrieval = {
			def result = [success:false]
			User user = springSecurityService.currentUser
			def object = Object.get(params.id)
		
			if ( object == null ) {
				 result.msg = "Das Objekt ${object.urn} konnte nicht gefunden werden!"
			}
			else {
				if (object.user.shortName != user.getShortName()) {
					result.msg = "Sie haben nicht die nötigen Berechtigungen, um das Objekt ${object.urn} anzufordern!"
					
				} else {
					try {
					
					CbNode cbn = CbNode.get(grailsApplication.config.localNode.id)						
					qu.createJob( object ,"900", cbn.getName()) 
					result.msg = "Objekt ${object.urn} erfolgreich angefordert."
					result.success = true
					} catch ( Exception e ) { 
					result.msg = "Objekt ${object.urn} konnte nicht angefordert werden."
					log.error("Error saving Retrieval request : " + e.printStackTrace())
					}
				}
			}
			render result as JSON
		}
	
	/**
	 * Creates QueueEntry for PIP rebuild
	 */
	
	def queueForRebuildPresentation = {
		def result = [success:false]
		
		def object = Object.get(params.id)
			
		if ( object == null ) result.msg += "Das Objekt ${object.urn} konnte nicht gefunden werden!"
		else {
			try {
				qu.createJob( object, "700", grailsApplication.config.cb.presServer )
				result.msg = "Auftrag zur Erstellung neuer Präsentationsformate erstellt ${object.urn}."
				result.success = true
			} catch ( Exception e ) {
				result.msg = "Auftrag zur Erstellung neuer Präsentationsformate für ${object.urn} konnte nicht angelegt werden."
				println e
			}
		}
		render result as JSON
	}
	
	/**
	 * Creates QueueEntry for recreating elasticSearchIndex
	 * 
	 */
	def queueForIndex = {
		def result = [success:false]
		
		def object = Object.get(params.id)
			
		if ( object == null ) result.msg += "Das Objekt ${object.urn} konnte nicht gefunden werden!"
		else {
			try {
				qu.createJob( object, "560" ,grailsApplication.config.cb.presServer)
				result.msg = "Auftrag zur Indizierung erstellt ${object.urn}."
				result.success = true
			} catch ( Exception e ) {
				result.msg = "Auftrag zur Indizierung für ${object.urn} konnte nicht angelegt werden."
				println e
			}
		}
		render result as JSON
	}

		
	
	def queueForInspect = {
		
			def result = [success:false]
			
			def object = Object.get(params.id)
			log.debug "found object with URN " + object.urn
			
			if (object != null) {
			
				log.debug "object.contractor.shortName: " + object.user.shortName
					
				try {
						CbNode cbn = CbNode.get(grailsApplication.config.localNode.id)
						qu.createJob( object, "5000" , cbn.getName())
						result.msg = "Das Objekt mit der URN ${object.urn} wurde zur Überprüfung in die Queue eingestellt!"
						result.success = true
					} catch(Exception e) {
					result.msg = "Fehler bei der Erstellung des Arbeitsauftrages zur Überprüfung des Objekts mit der URN ${object.urn}. Bitte wenden Sie sich an einen Administrator."
					log.error(result.msg)
					println e
					}
			}
			render result as JSON
	}

	def collectSearchParams = {
		def paramList = params.search?.collectEntries { key, value -> ['search.'+key, value] }
		paramsList.putAt("searchContractorName", params?.searchContractorName)
		return
		[paramsList:paramsList]
	}
}
