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
/**
 * The Main DA-NRW object Controller for listing Objects (AIP) stored in the DA-NRW 
 * @Author Jens Peters, Sebastian Cuy
 * 
 */


import org.springframework.dao.DataIntegrityViolationException
import grails.converters.*

class ObjectController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
				def admin = false;
				def relativeDir = session.contractor.shortName+ "/outgoing"
				def baseFolder = grailsApplication.config.localNode.userAreaRootPath + "/" + relativeDir
				
				if (params.search==null||((params.search.origName==null||params.search.origName=="")
						&&(params.search.urn==null||params.search.urn==""))) params.search=null;
        
				if (params.search==null){
		
					params.max = Math.min(params.max ? params.int('max') : 10, 100)
					
					def c = Object.createCriteria()
					log.debug(params.toString())
					def objects = c.list(max: params.max, offset: params.offset ?: 0) {
						if (params.search) params.search.each { key, value ->
							like(key, "%" + value + "%")
						}
						if (session.contractor.admin==0) {
							def contractor = Contractor.findByShortName(session.contractor.shortName)
							eq("contractor.id", contractor.id)
							
						}
						if (session.contractor.admin==1) {
							admin = true;
						}
						between("object_state", 50,100)
						order(params.sort ?: "id", params.order ?: "desc")
					}
					log.debug(params.search)
					return [
						objectInstanceList: objects,
						objectInstanceTotal: objects.getTotalCount(),
						searchParams: params.search,
						paginate: true,
						admin: admin,
						baseFolder: baseFolder
				]
				} else {
					
					def c = Object.createCriteria()
					log.debug(params.toString())
					def objects = c.list {
						if (params.search) params.search.each { key, value ->
							like(key, "%" + value + "%")
						}
						if (session.contractor.admin!=1) {
							def contractor = Contractor.findByShortName(session.contractor.shortName)
							eq("contractor.id", contractor.id)
						}
						if (session.contractor.admin==1) {
							admin = true;
						}
						between("object_state", 50,100)
						order(params.sort ?: "id", params.order ?: "desc")
					}
					if (params.search.urn!="") params.search.urn = params.search.urn.replaceAll(~"\\+",":")
					
					log.debug(params.search)
					
					
					
					return [
						objectInstanceList: objects,
						searchParams: params.search,
						admin: admin,
						baseFolder: baseFolder
				]
				}
    }

    def show() {
		def c = Object.createCriteria()
		log.debug(params.toString())
		def objectInstance;
		def contractor;
		
		
		if (session.contractor.admin==0) {
			contractor = Contractor.findByShortName(session.contractor.shortName)
			objectInstance = Object.findByIdAndContractor (params.id, contractor);
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
				if (object.contractor.shortName != session.contractor.shortName) {
					result.msg += "${object.urn} - KEINE BERECHTIGUNG. "
					result.success = false
				} else {
					try {
					createQueueEntryForObject( object ,"900", null) + "\n"	
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




    def queueForRetrieval = {
			def result = [success:false]
			
			def object = Object.get(params.id)
					
			if ( object == null ) {
				 result.msg = "Das Objekt ${object.urn} konnte nicht gefunden werden!"
			}
			else {
				if (object.contractor.shortName != session.contractor.shortName) {
					result.msg = "Sie haben nicht die nötigen Berechtigungen, um das Objekt ${object.urn} anzufordern!"
					
				} else {
					try {
					createQueueEntryForObject( object ,"900", null) 
					result.msg = "Objekt ${object.urn} erfolgreich angefordert."
					result.success = true
					} catch ( Exception e ) { 
					result.msg = "Objekt ${object.urn} konnte nicht angefordert werden."
					println e
					}
				}
			}
			render result as JSON
		}
	
	
	
	def queueForRebuildPresentation = {
		def result = [success:false]
		
		def object = Object.get(params.id)
			
		if ( object == null ) result.msg += "Das Objekt ${object.urn} konnte nicht gefunden werden!"
		else {
			try {
				createQueueEntryForObject( object, "700", null )
				result.msg = "Auftrag zur Erstellung neuer Präsentationsformate erstellt ${object.urn}."
				result.success = true
			} catch ( Exception e ) {
				result.msg = "Auftrag zur Erstellung neuer Präsentationsformate für ${object.urn} konnte nicht angelegt werden."
				println e
			}
		}
		render result as JSON
	}
	
	def queueForIndex = {
		def result = [success:false]
		
		def object = Object.get(params.id)
			
		if ( object == null ) result.msg += "Das Objekt ${object.urn} konnte nicht gefunden werden!"
		else {
			try {
				createQueueEntryForObject( object, "560" ,grailsApplication.config.cb.presServer)
				result.msg = "Auftrag zur Indizierung erstellt ${object.urn}."
				result.success = true
			} catch ( Exception e ) {
				result.msg = "Auftrag zur Indizierung für ${object.urn} konnte nicht angelegt werden."
				println e
			}
		}
		render result as JSON
	}

		
	
		/**
		 * @param object valid instance of an object
		 * throws Exception if entry could not be created
		 */
		String createQueueEntryForObject(object, status, optionalInitialNode) {
			if (object == null) throw new RuntimeException ( "Object is not valid" )

			log.debug "object.contractor.shortName: " + object.contractor.shortName
			log.debug "session.contractor.shortName: " + session.contractor.shortName
	
			def entry = new QueueEntry()		
			entry.status = status
			entry.setObj(object);
			entry.created = Math.round(new Date().getTime()/1000L)
			entry.modified = Math.round(new Date().getTime()/1000L)
			if (optionalInitialNode==null)
			entry.setInitialNode(grailsApplication.config.irods.server)
			else entry.setInitialNode(optionalInitialNode)
			def errorMsg = ""
			if( !entry.save() ) {
				entry.errors.each { errorMsg += it }
				throw new Exception(errorMsg)
			} 			
		}



	
	def queueForInspect = {
		
			def result = [success:false]
			
			def object = Object.get(params.id)
			log.debug "found object with URN " + object.urn
			
			if (object != null) {
			
				log.debug "object.contractor.shortName: " + object.contractor.shortName
				log.debug "session.contractor.shortName: " + session.contractor.shortName
					
					// set Object state to 50: orange 
					object.setObject_state(50);
					def entry = new QueueEntry()
					entry.status = "5000";
					entry.initialNode = grailsApplication.config.irods.server
					entry.created = Math.round(new Date().getTime()/1000L)
					entry.setObj(object);
					if( !entry.save() ) {
						entry.errors.each {
							log.warn it
						}
						result.msg = "Fehler bei der Erstellung des Arbeitsauftrages zur Überprüfung des Objekts mit der URN ${object.urn}. Bitte wenden Sie sich an einen Administrator."
					} else {
						result.msg = "Das Objekt mit der URN ${object.urn} wurde zur Überprüfung in die Queue eingestellt!"
						result.success = true
					}
			} else {
				result.msg = "couldn't find object in database"
			}
			log.info result.msg
		
			render result as JSON
			
		}

}
