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


import org.apache.commons.lang.StringUtils;
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

	/**
	 * Suchkriterien prüfen und auswerten
	 * @return
	 */
	def listObjectsSearch () {
		Object object = new Object(params);
	   log.debug("most_recent_formats 1: " + object.most_recent_formats + "   most_recent_secondary_attributes 1 : "+ object.most_recent_secondary_attributes);
	   if (object.most_recent_formats == null   && object.most_recent_secondary_attributes == null)  {
		   render (view:'listObjects', model:[suLeer:"Bitte Suchkriterien eingeben!"]);
	   }  else {
	   	 log.debug("most_recent_formats: " + object.most_recent_formats + "   most_recent_secondary_attributes: "+ object.most_recent_secondary_attributes);
		 listObjects( );
	   }
	}
	   
	/**
	 * Wenn Suchkriterien eingegeben wurden, so kann die Suche beginnen
	 *
	 * @return
	 */
 	def listObjects ( ) {
		 Object object = new Object(params)
		 def objects = null
		 def admin = 0
		
		 log.debug("object: " + object.most_recent_formats);
		 // Zugriff auf Tabelle objects
		 objects = Object.findAll("from Object as o where o.most_recent_formats like :formats " +
			 " or o.most_recent_secondary_attributes like :attributes)"   ,
			  [formats:'%'+object.most_recent_formats+'%',
			  attributes:"%"+object.most_recent_secondary_attributes+"%"])
		 
		 // Ergebnisliste
		 [ objects:objects ]
		 
		 if (objects == [] && (object.most_recent_formats != null  ||  object.most_recent_secondary_attributes != null)) {
			 render (view:'listObjects', model:[sqlLeer:"Keine Datensätze gefunden!"]);
		 } else {
			  render (view:'listObjects', model:[objects:objects] );
		 }
 	} 
	 
	 def scanAndConvert(){
		 
	 }
	
	def list() {
		User user = springSecurityService.currentUser

		def contractorList = User.list()
		def admin = 0;
		def relativeDir = user.getShortName() + "/outgoing"
		def filterOn = params.filterOn;
		if (filterOn==null) filterOn=0

		def baseFolder = grailsApplication.config.localNode.userAreaRootPath + "/" + relativeDir
		params.max = Math.min(params.max ? params.int('max') : 50, 200)

		if (params.searchContractorName){
			if(params.searchContractorName=="null"){
				params.remove("searchContractorName")
			}
		}
		def c1 = Object.createCriteria()
		def objtsTotalForCont = c1.list() {
			eq("user.id", user.id)
			between("object_state", 50,200)
		}
		def totalObjs = objtsTotalForCont.size();
		
		
		def c = Object.createCriteria()
		log.debug(params.toString())
		def objects = c.list(max: params.max, offset: params.offset ?: 0) {

			if (params.search) params.search.each { key, value ->
				if (value!="") filterOn=1
				like(key, "%" + value + "%")
			}

			log.debug("Date as Strings " + params.searchDateStart + " and " + params.searchDateEnd)
			def ds = daweb3.Object.convertDateIntoStringDate(params.searchDateStart)
			def de = daweb3.Object.convertDateIntoStringDate(params.searchDateEnd)

			def st = "created"
			if (params.searchDateType!=null) {
				st = params.searchDateType;
			}

			log.debug("Search in Field " + params.searchDateType)

			if (ds!=null && de!=null) {
				filterOn=1
				log.debug("Objects between " + ds + " and " + de)
				between(st, ds, de)
			}
			if (ds!=null && de==null) {
				filterOn=1
				log.debug("Objects greater than " + ds)
				gt(st,ds)
			}
			if (ds==null && de!=null) {
				filterOn=1
				log.debug("Objects lower than " + de)
				lt(st,de)
			}


			if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
				admin = 1;
			}
			if (admin==0) {
				
				eq("user.id", user.id)
			}
			if (admin==1) {
				if (params.searchContractorName!=null) {
					filterOn=1
					createAlias( "user", "c" )
					eq("c.shortName", params.searchContractorName)
				}
			}
			between("object_state", 50,200)
			order(params.sort ?: "id", params.order ?: "desc")
		}
		log.debug("Search " + params.search)

		// workaround: make ALL params accessible for following http-requests
		def paramsList = params.search?.collectEntries { key, value -> ['search.'+key, value]}
		if(params.searchContractorName){
			paramsList.putAt("searchContractorName", params?.searchContractorName)
		}

		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			render(view:"adminList", model:[	objectInstanceList: objects,
				objectInstanceTotal: objects.getTotalCount(),
				searchParams: params.search,
				filterOn: filterOn,
				paramsList: paramsList,
				paginate: true,
				admin: admin,
				baseFolder: baseFolder,
				contractorList: contractorList
			]);
		} else render(view:"list", model:[	objectInstanceList: objects,
				objectInstanceTotal: objects.getTotalCount(),
				searchParams: params.search,
				filterOn: filterOn,
				paramsList: paramsList,
				paginate: true,
				admin: 0,
				totalObjs: totalObjs,
				baseFolder: baseFolder,
				contractorList: contractorList
			]);
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
			flash.message = message(code: 'default.not.found.message', args: [
				message(code: 'object.label', default: 'Object'),
				params.id
			])
			redirect(action: "list")
			return
		}
		def urn = objectInstance.urn
		urn = urn.replaceAll(~"\\+",":")
		def sortedPackages = objectInstance.packages.sort{it.id}
		def preslink = grailsApplication.config.fedora.urlPrefix +urn.replaceAll(~"urn:nbn:de:danrw-", "")
		[objectInstance: objectInstance,
			urn:urn,preslink:preslink,sortedPackages:sortedPackages]
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
					String lnid = grailsApplication.config.localNode.id
					log.debug("Create Retrieval job on node id: " + lnid)

					CbNode cbn = CbNode.get(Integer.parseInt(lnid))
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
				String lnid = grailsApplication.config.localNode.id
				log.debug("Create Rebuild PIP job on node id: " + lnid)
				CbNode cbn = CbNode.get(Integer.parseInt(lnid))
				qu.createJob( object ,"700", cbn.getName())
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
				PreservationSystem ps = PreservationSystem.get(1);
				String pserver  = ps.getPresServer();
				log.debug("Create Rebuild ES job on node" + pserver)
				if (pserver!=null && pserver!= "") {
					qu.createJob( object, "560" ,pserver)
				}
				result.msg = "Auftrag zur Indizierung 3 ${object.urn}."
				result.success = true
			} catch ( Exception e ) {
				result.msg = "Auftrag zur Indizierung für ${object.urn} konnte nicht angelegt werden."
				println e
			}
		}
		render result as JSON
	}



	def c = {

		def result = [success:false]

		def object = Object.get(params.id)
		log.debug "found object with URN " + object.urn

		if (object != null) {

			log.debug "object.contractor.shortName: " + object.user.shortName

			try {
				def ids= grailsApplication.config.localNode.id
				CbNode cbn = CbNode.get(Integer.valueOf(ids))
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
		def paramList = params.search?.collectEntries { key, value -> ['search.'+key, value]}
		paramsList.putAt("searchContractorName", params?.searchContractorName)
		return
		[paramsList:paramsList]
	}	
	
}
