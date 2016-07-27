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
import org.apache.tools.ant.types.resources.selectors.InstanceOf;
import org.springframework.dao.DataIntegrityViolationException;

import grails.converters.*;

import java.awt.image.renderable.RenderableImage;
import java.security.InvalidParameterException;

import groovy.util.XmlSlurper
import groovy.util.XmlParser

class ObjectController {

	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	static QueueUtils qu = new QueueUtils();
	
	static ArrayList<PremisPackage> sortPackages =null
	static Map<Integer, ArrayList> eventList = null
	static Map<Integer, ArrayList> eventPkgList = null
	static Map<Integer, ArrayList> dafileList = null
	static Map<Integer, ArrayList> dafileMetaList = null
	static Map<Integer, ArrayList> eventMetaList = null
	static LinkedHashMap<Integer, Event[]> pkgEvents = null
	static LinkedHashMap<String, ArrayList> dafiles = null
	static LinkedHashMap<String, ArrayList> meta = null
	static LinkedHashMap<String, ArrayList> tmpDaFiles = null
	static LinkedHashMap<String, ArrayList> tmpMeta = null
	static LinkedHashMap<String, String> targetNames = null
	static int dafilesMaxSize = 0
	static int metaMaxSize = 0
	static def premisObject = null
	static def dataName = "%"
	static int oldOffset1 = 0
	static int oldOffset2 = 0
	
	def springSecurityService

	def index() {
		redirect(action: "list", params: params)
	}

	/**
	 * proof and evaluate the search key
	 */
	def listObjectsSearch () {
		Object object = new Object(params);
	   if (object.most_recent_formats == null   && object.most_recent_secondary_attributes == null)  {
		   render (view:'listObjects', model:[suLeer:"Bitte Suchkriterien eingeben!"]);
	   }  else {
		 listObjects( );
	   }
	}
	   
	/**
	 * If there id a search key, search can start
	 */
 	def listObjects ( ) {
		 Object object = new Object(params)
		 def objects = null
		 def admin = 0
 		 def String extension = ""
		 def List<String> extList = new ArrayList<String>()
		 def String name = ""
		 def List<String> nameList = new ArrayList<String>();
		
		 // access table objects
		 objects = Object.findAll("from Object as o where o.most_recent_formats like :formats " +
			 " or o.most_recent_secondary_attributes like :attributes)"   ,
			  [formats:'%'+object.most_recent_formats+'%',
			  attributes:"%"+object.most_recent_secondary_attributes+"%"])
		
		 // list of results
		 [ objects:objects ]
		 
		 if (objects == [] && (object.most_recent_formats != null  ||  object.most_recent_secondary_attributes != null)) {
			 render (view:'listObjects', model:[sqlLeer:"Keine Datensätze gefunden!"]);
		 } else {
		 	def FormatMapping = new FormatMapping()
			def mappings = null;
			
			 /*
			  * To get the right format for the mapping, there must be a splitting of
			  * each fetched row to access the table format_mapping
			  */
			 for (item in objects){
				 def formatArray = (String[]) item.most_recent_formats.split(",")
				 extension = ""
				 name = "";
				 
				 int counter = 0;
				 
				 while (formatArray.size() > counter ) {
					 def format = formatArray[counter];
					 
					 /*
					  * now you can read the table format_mapping 
					  */
					 
					 mappings = FormatMapping.findAll("from FormatMapping where puid = :puid", [puid : format])
					 
					 if (extension.isEmpty()) {
						 extension = mappings.extension
						 name = mappings.formatName
					 } else {
					 	 extension = extension + ", " + mappings.extension
						 name = name + "," + mappings.formatName
					 }		
					 // and at last increment the counter
					 counter = counter + 1;
				 } // end of format - list
				 
				 extList.add(extension)  
				 nameList.add(name)
				 
			 } // end of object - list
			 render (view:'listObjects', model:[objects:objects, extension:extList, name:nameList]);
		 }
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
		
		
		//
		//def objectIdentifiers = PremisObject.findAll("from PremisObject where identifier in :ident", [ident: objects.identifier])
		//
		

		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			render(view:"adminList", model:[	objectInstanceList: objects,
				objectInstanceTotal: objects.getTotalCount(),
				searchParams: params.search,
				filterOn: filterOn,
				paramsList: paramsList,
				paginate: true,
				admin: admin,
				baseFolder: baseFolder,
				contractorList: contractorList/*,
				objectIdentifiers: objectIdentifiers*/
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
		
		/*
		 * DANRW-1417: extension about the access to the table format_mapping
		 */
		
		Map<String, String> extListSip = [:]
		if (objectInstance != [] && objectInstance.original_formats != null) {
			/*
			 * To get the right format for the mapping, there must be a splitting of
			 * each fetched row to access the table format_mapping
			 */
			for (item in objectInstance){
				String[] formatArray = (String[]) item.original_formats.split(",")
				
				extListSip = formatMapping(formatArray, extListSip);
				
			} // end of object - list
		}
		
		Map<String, String> extListDip = [:]
		if (objectInstance != [] && objectInstance.most_recent_formats != null) {
			/*
			 * To get the right format for the mapping, there must be a splitting of
			 * each fetched row to access the table format_mapping
			 */
			for (item in objectInstance){
				String[] formatArray = (String[]) item.most_recent_formats.split(",")
				
				extListDip = formatMapping(formatArray, extListDip);
				
			} // end of object - list
		}
		[objectInstance: objectInstance,
			urn:urn,preslink:preslink,sortedPackages:sortedPackages, extensionSip:extListSip, extensionDip:extListDip]
	
	}

	/**
	 * @param formatArray
	 * @param extList
	 * @return
	 */
 	private Map<String, String> formatMapping(String[] formatArray, Map<String, String> extList) {
		FormatMapping fm = new FormatMapping()
		def mappings = null;
		String extension = ""
		int counter = 0;
		def format
		
		while (formatArray.size() > counter ) {
			format = formatArray[counter];
			
			/*
			 * now you can read the table format_mapping
			 */
			
			mappings = fm.findAll("from FormatMapping where puid = :puid", [puid : format])
			
			// and at last increment the counter
			counter = counter + 1; 
			extList.put(format, mappings.extension)
		} // end of format - list
		return	extList 
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
				/*
				 * DANRW-1419: error messages must be better specified
				 * 1. RuntimeException: "Es gibt bereits einen laufenden Arbeitsauftrag für dieses Objekt"
				 * 2. IllegalArgumentException: object == null oder responsibleNodeName == null
				 * 3. Exception: !object.save() oder !job.save()
				 */
					if (e instanceof RuntimeException) {
						result.msg = e.getMessage() +  " ${object.urn}" 
					} else 
					if (e instanceof IllegalArgumentException) { 
						if (e.getMessage().equals("Object is not valid")) {
							result.msg = "Objekt ${object.urn} konnte nicht angefordert werden. Das Object ist ungültig."
						} else if (e.getMessage().equals("responsibleNodeName must not be null")) {
							result.msg = "Objekt ${object.urn} konnte nicht angefordert werden. Der Knotenname ist nicht gesetzt."
						}
					} else {
						result.msg = "Objekt ${object.urn} konnte nicht angefordert werden."
					}
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
	

	def removeSpace = {s ->
		if(s != null && s.size() > 0) {
			int beginWord = 0
			int endWord = s.size()-1
			boolean word = false
			for (int i = 0; i < s.size(); i++) {
				if(s.getAt(i) == " ") {
					if(!word) {
						beginWord = i+1
					} 
				} else {
					word = true
					endWord = i
				}
			}
			if(beginWord <= endWord) {
				s = s.getAt(beginWord..endWord)
			} else {
				s = ""
			}
		}
	}
	
	def abc123 = {
		def a1 = [name: 'abc123']
		render a1 as JSON
	}
	
	def premisAnfordern() {
		
//		abc123()
//		queueForRetrieval()
//		abc123()
//		OutgoingController.download()
		
		
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
				/*
				 * DANRW-1419: error messages must be better specified
				 * 1. RuntimeException: "Es gibt bereits einen laufenden Arbeitsauftrag für dieses Objekt"
				 * 2. IllegalArgumentException: object == null oder responsibleNodeName == null
				 * 3. Exception: !object.save() oder !job.save()
				 */
					if (e instanceof RuntimeException) {
						result.msg = e.getMessage() +  " ${object.urn}"
					} else
					if (e instanceof IllegalArgumentException) {
						if (e.getMessage().equals("Object is not valid")) {
							result.msg = "Objekt ${object.urn} konnte nicht angefordert werden. Das Object ist ungültig."
						} else if (e.getMessage().equals("responsibleNodeName must not be null")) {
							result.msg = "Objekt ${object.urn} konnte nicht angefordert werden. Der Knotenname ist nicht gesetzt."
						}
					} else {
						result.msg = "Objekt ${object.urn} konnte nicht angefordert werden."
					}
					log.error("Error saving Retrieval request : " + e.printStackTrace())
				}
			}
		}
//		render result as JSON
		
		
		
//		def download() {
			// set Queue Entry to read.
			log.debug("Setting read status of file " + params.filename)
			def idn = params.filename.substring(0,params.filename.length()-4)
			log.debug("Setting read status of object <" + idn + ">")
//			User user = springSecurityService.currentUser
			def que = QueueEntry.findAll("from QueueEntry as q where q.obj.user.shortName=:csn and q.obj.identifier=:idn",
				 [csn: user.getShortName(),
					idn: idn])
			que.each {
				
				//it.setStatus("960")
				
				def dateCode = Math.round(new Date().getTime()/1000L)
				log.debug("dateCode:"+dateCode)
				log.debug("String Value of:"+String.valueOf(dateCode))
				
				it.setModified(String.valueOf(dateCode))
				it.save();
			}
			
//			def webdavurl = grailsApplication.config.transferNode.downloadLinkPrefix +"/"+   user.getShortName()  + "/outgoing"
//			redirect(url:webdavurl + "/" + params.filename)
//		}
		
		def obj = Object.find("from Object where origName like :name", [name: params.objName])
		int coun = 0
		final long timeStart = System.currentTimeMillis()
		long time = System.currentTimeMillis()
		while(obj.isInWorkflowButton() && (time-timeStart) < 600) {
			coun++
			time = System.currentTimeMillis()
		}	
			
		
		//def obj = Object.findAll("from Object where origName like :name", [name: params.objName])
		//def obj = Object.find("from Object where origName like :name", [name: params.objName])
		if(obj != null) {
		def result2 = [success2:false]
		try {
			def list = QueueEntry.findAll("from QueueEntry where obj = :object and status in ('952', '991', '1000')", [object: obj])
			if (list != null && list.size() > 0) {
				QueueEntry.executeUpdate("delete QueueEntry where obj = :object and status in ('952', '991', '1000')", [object: obj])
			}
			
			String lnid = grailsApplication.config.localNode.id
			log.debug("Create Retrieval job on node id: " + lnid)

			CbNode cbn = CbNode.get(Integer.parseInt(lnid))
			qu.createJob( obj ,"990", cbn.getName())
			result2.msg2 = "Objekt ${obj.urn} erfolgreich angefordert."
			result2.success2 = true
		} catch ( Exception e ) {
		/*
		 * DANRW-1419: error messages must be better specified
		 * 1. RuntimeException: "Es gibt bereits einen laufenden Arbeitsauftrag für dieses Objekt"
		 * 2. IllegalArgumentException: object == null oder responsibleNodeName == null
		 * 3. Exception: !object.save() oder !job.save()
		 */
			if (e instanceof RuntimeException) {
				result2.msg2 = e.getMessage() +  " ${obj.urn}"
			} else
			if (e instanceof IllegalArgumentException) {
				if (e.getMessage().equals("Object is not valid")) {
					result2.msg2 = "Objekt ${obj.urn} konnte nicht angefordert werden. Das Object ist ungültig."
				} else if (e.getMessage().equals("responsibleNodeName must not be null")) {
					result2.msg2 = "Objekt ${obj.urn} konnte nicht angefordert werden. Der Knotenname ist nicht gesetzt."
				}
			} else {
				result2.msg2 = "Objekt ${obj.urn} konnte nicht angefordert werden."
			}
			log.error("Error saving Retrieval request : " + e.printStackTrace())
		}
		result.r = "r1"
		result2.r = "r2"
		def res = [success3:true, counter:coun]
		res += result
		res += result2
		render (res) as JSON
		} else {
		def res = [success3:false]
		res.fehler = "fehler"
		render res as JSON
		}
		def list = QueueEntry.findAll("from QueueEntry where obj = :object and status in ('952', '991', '1000')", [object: obj])
		if (list != null && list.size() > 0) {
			QueueEntry.executeUpdate("delete QueueEntry where obj = :object and status in ('952', '991', '1000')", [object: obj])
		}
	}
	
	def premis() {
		
		
		def zeit1 = new Date()
		
		
		params.max = Math.min(params.max ? params.int('max') : 10, 100)
		params.offset1 = params.int('offset1') ?: oldOffset1
		params.offset2 = params.int('offset2') ?: oldOffset2
		params.first = params.boolean('first') == null ? true : params.boolean('first')
			
		int offset1 = params.int('offset1')
		int offset2 = params.int('offset2')
		
		if(params.boolean('first')) {
			dataName = "%"
		}
		
		def dName = params.search?.dataName
		def dExtension = params.search?.dataExtension
	
		
		if((dName != null && dName != "") || (dExtension != null && dExtension != "")) {
			params.offset1 = 0
			params.offset2 = 0
			offset1 = params.int('offset1')
			offset2 = params.int('offset2')
			params.first = true
			if(dName != null && dName != "") {
				dName = removeSpace(dName)
				dataName += dName + "%"
			}
			if(dExtension != null && dExtension != "") {
				dExtension = removeSpace(dExtension)
				if(!dExtension.startsWith(".")) {
					dExtension = "." + dExtension
				}
				dataName += dExtension + "%"
			}
		}
		
		if(premisObject == null || params.boolean('first') ) {
			finde(params.objName)
			tmpDaFiles = new LinkedHashMap<String, ArrayList>()
			tmpMeta = new LinkedHashMap<String, ArrayList>()
			params.offset1 = 0
			offset1 = 0
			params.offset2 = 0
			offset2 = 0
		} 
		if(premisObject != null) {
			if(oldOffset1 != offset1 || params.boolean('first')) {
				def iter = dafiles.entrySet().iterator()
				def tmp
				def counterTmp = 0
					
				while(iter.hasNext() && counterTmp < params.max) {
					tmp = iter.next()
					tmpDaFiles.put(tmp.key, tmp.value)
					iter.remove()
					counterTmp++
				}
				
				oldOffset1 = offset1
			} 
			
			if(oldOffset2 != offset2 || params.boolean('first')) {
				def iter = meta.entrySet().iterator()
				def tmp
				def counterTmp = 0
						
				while(iter.hasNext() && counterTmp < params.max) {
					tmp = iter.next()
					tmpMeta.put(tmp.key, tmp.value)
					iter.remove()
					counterTmp++
				}
				
				oldOffset2 = offset2
			}
			
			int numberShowFiles = params.max
			
			int max = 0
					
			if(dafiles.size() > meta.size()) {
				max = dafiles.size()
			} else {
				max = meta.size()
			}
			
			params.first = false
			
		//if(premisObject != null) {
			render(view:"premis", model:[targetNames: targetNames, zeit1:zeit1, dafilesMaxSize: dafilesMaxSize, metaMaxSize: metaMaxSize, tmpDafiles: tmpDaFiles, tmpMeta: tmpMeta, pkgEvents: pkgEvents, dataName: dataName, numberShowFiles: numberShowFiles, max: max, meta: meta, dafiles: dafiles, packages: sortPackages, object: premisObject, eventPkgList: eventPkgList])
		}
		else {
			render("Premis konnte nicht geladen werden.")
		}
		
	} 
	
	
	/*def showNext = {
		
		tmpDaFiles = new LinkedHashMap<String, ArrayList>()
		tmpMeta = new LinkedHashMap<String, ArrayList>()
		
		def iter = dafiles.entrySet().iterator()
		
		def tmp
		def counterTmp = 0
				
		while(iter.hasNext() && counterTmp < params.max) {
			tmp = iter.next()
			tmpDaFiles.put(tmp.key, tmp.value)
			iter.remove()
			counterTmp++
		}
		
		//tmpDaFiles = dafiles
		
		render(template:"premisFileList", model:[dafiles: tmpDaFiles, dafilesSize: dafilesMaxSize, fileMeta: "Digitalisate", i: 0])
	}*/
	
	
	def finde = {	objName ->

		def xmldocument = "" 
		premisObject = PremisObject.find("from PremisObject where origName = :name", [name: objName]) //'2-20160414450456']) //'2-20160405449925'])  '1-2016070651'])  //'1-2016062726']) //'1-20160627268']) //'1-2016070168']) //'1-2016062164']) //'1-20160530106']) // params.objectIdentifier
		
		
		
		if(premisObject != null) {
			if(sortPackages!=null && eventList!=null && eventPkgList!=null &&dafileList!=null){
				render(view:"premis", model:[packages: sortPackages, object: premisObject, eventPkgList: eventPkgList, eventList: eventList, dafileList: dafileList])
			}

			targetNames = new LinkedHashMap<String, String>()
			sortPackages = new ArrayList<PremisPackage>(premisObject.packages)
			eventList = new HashMap<Integer, ArrayList>()
			eventMetaList = new HashMap<Integer, ArrayList>()
			eventPkgList = new HashMap<Integer, ArrayList>()
			dafileList = new HashMap<Integer, ArrayList>()
			dafileMetaList = new HashMap<Integer, ArrayList>()
			sortPackages.sort { x,y ->
				y.getName().compareTo(x.getName())
			}
			pkgEvents = new HashMap<Integer, Event[]>()
			
			dafiles = new LinkedHashMap<String, ArrayList>()
			meta = new LinkedHashMap<String, ArrayList>()
		
			//if(object != null) {
				xmldocument = premisObject.xml
				sortPackages.each {
					
					int id = it.id
					def pkg = it
					
					def pkgSIP = Event.find("from Event where pkg_id=:pkg and type like 'SIP_CREATION'", [pkg: pkg.id])
					def pkgING = Event.find("from Event where pkg_id=:pkg and type like 'INGEST'", [pkg: pkg.id])
					def ev = [pkgSIP, pkgING]
					pkgEvents.put(id, ev)
					
					def dafile = new ArrayList<DAFile>()
					def eventCopy = new ArrayList<Event>()
					def event = new ArrayList<Event>()
					
					event.addAll(Event.findAll("from Event as event, FormatMapping as format1, FormatMapping as format2 " +
						"where event.sourceFile.format_puid = format1.puid and event.targetFile.format_puid = format2.puid " +
						"and event.type like 'CONVERT' and event.pkg_id = :pkg and " +
						"lower(event.sourceFile.relative_path) not like '%.xml' and lower(event.sourceFile.relative_path) like lower(:name)", [pkg: id, name: dataName]))
					event.each{
						def dat = it
						def datName = dat[0].getSourceFile().getRelative_path()
						def name = datName.substring(0, datName.lastIndexOf('.'))
						def targetName = dat[0].getTargetFile().getRelative_path()
						targetNames.put(name, targetName)
						if(!dafiles.containsKey(name)) {
							def d = new ArrayList<Object[]>()
							def a = [dat, pkg]
							d.add(a)
							dafiles.put(name, d)
						} else {
							def d = dafiles.get(name)
							def a = [dat, pkg]
							d.add(a)
						}
					}
					eventCopy.addAll(Event.findAll("from Event as event, FormatMapping as format1, FormatMapping as format2 " +
						"where event.sourceFile.format_puid = format1.puid and event.targetFile.format_puid = format2.puid " +
						"and event.type like 'COPY' and event.pkg_id = :pkg and " +
						"lower(event.sourceFile.relative_path) not like '%.xml' and lower(event.sourceFile.relative_path) like lower(:name)", [pkg: id, name: dataName]))
					eventCopy.each{
						def dat = [it[0].getSourceFile(), it[1]]
						def datName = dat[0].getRelative_path()
						def name = datName.substring(0, datName.lastIndexOf('.'))
						def targetName = it[0].getTargetFile().getRelative_path()
						targetNames.put(name, targetName)
						if(!dafiles.containsKey(name)) {
							def d = new ArrayList<Object[]>()
							def a = [dat, pkg]
							d.add(a)
							dafiles.put(name, d)
						} else {
							def d = dafiles.get(name)
							def a = [dat, pkg]
							d.add(a)
						}
					}
					dafile.addAll(DAFile.findAll("from DAFile as file, FormatMapping as format where file.format_puid = format.puid " +
						"and file.id not in (select event.sourceFile.id from Event as event " +
						"where event.sourceFile.id is not null and (event.type like 'CONVERT' or event.type like 'COPY') and event.pkg_id = :pkg) and " +
						"file.id not in (select event.targetFile.id from Event as event where event.targetFile.id is not null " +
						"and (event.type like 'CONVERT' or event.type like 'COPY') and event.pkg_id = :pkg) and lower(file.relative_path) not like '%.xml' and "+
						"lower(file.relative_path) like lower(:name) and file.pkg_id = :pkg", [pkg: id, name: dataName]))
					dafile.each{
						def dat = it
						def datName = dat[0].getRelative_path()
						def name = datName.substring(0, datName.lastIndexOf('.'))
						if(!dafiles.containsKey(name)) {
							def d = new ArrayList<Object[]>()
							def a = [dat, pkg]
							d.add(a)
							dafiles.put(name, d)
						} else {
							def d = dafiles.get(name)
							def a = [dat, pkg]
							d.add(a)
						}
					}
					
					
					def dafileMeta = new ArrayList<DAFile>()
					def eventCopyMeta = new ArrayList<Event>()
					def eventMeta = new ArrayList<Event>()
					
					eventMeta.addAll(Event.findAll("from Event as event, FormatMapping as format1, FormatMapping as format2 " +
						"where event.sourceFile.format_puid = format1.puid and event.targetFile.format_puid = format2.puid " +
						"and event.type like 'CONVERT' and event.pkg_id = :pkg and " +
						"lower(event.sourceFile.relative_path) like '%.xml' and event.sourceFile.relative_path <> 'premis.xml' " +
						"and lower(event.sourceFile.relative_path) like lower(:name)", [pkg: id, name: dataName]))
					eventMeta.each{
						def dat = it
						def datName = dat[0].getSourceFile().getRelative_path()
						def name = datName.substring(0, datName.lastIndexOf('.'))
						def targetName = dat[0].getTargetFile().getRelative_path()
						targetNames.put(name, targetName)
						if(!meta.containsKey(name)) {
							def d = new ArrayList<Object[]>()
							def a = [dat, pkg]
							d.add(a)
							meta.put(name, d)
						} else {
							def d = meta.get(name)
							def a = [dat, pkg]
							d.add(a)
						}
					}
					eventCopyMeta.addAll(Event.findAll("from Event as event, FormatMapping as format1, FormatMapping as format2 " +
						"where event.sourceFile.format_puid = format1.puid and event.targetFile.format_puid = format2.puid " +
						"and event.type like 'COPY' and event.pkg_id = :pkg and " +
						"lower(event.sourceFile.relative_path) like '%.xml' and event.sourceFile.relative_path <> 'premis.xml' " +
						"and lower(event.sourceFile.relative_path) like lower(:name)", [pkg: id, name: dataName]))
					eventCopyMeta.each{
						def dat = [it[0].getSourceFile(), it[1]]
						def datName = dat[0].getRelative_path()
						def name = datName.substring(0, datName.lastIndexOf('.'))
						def targetName = it[0].getTargetFile().getRelative_path()
						targetNames.put(name, targetName)
						if(!meta.containsKey(name)) {
							def d = new ArrayList<Object[]>()
							def a = [dat, pkg]
							d.add(a)
							meta.put(name, d)
						} else {
							def d = meta.get(name)
							def a = [dat, pkg]
							d.add(a)
						}
					}
					dafileMeta.addAll(DAFile.findAll("from DAFile as file, FormatMapping as format where file.format_puid = format.puid " +
						"and file.id not in (select event.sourceFile.id from Event as event " +
						"where event.sourceFile.id is not null and (event.type like 'CONVERT' or event.type like 'COPY') and event.pkg_id = :pkg) and " +
						"file.id not in (select event.targetFile.id from Event as event where event.targetFile.id is not null and " +
						"(event.type like 'CONVERT' or event.type like 'COPY') and event.pkg_id = :pkg) and lower(file.relative_path) like '%.xml' and " +
						"lower(file.relative_path) like lower(:name) and lower(file.relative_path) <> 'premis.xml' and " +
						"file.pkg_id = :pkg", [pkg: id, name: dataName]))
					/*"from DAFile as file, FormatMapping as format where file.format_puid = format.puid " +
						"and file.id not in (select event.sourceFile.id from Event as event " +
						"where event.sourceFile.id is not null and event.type <> 'CONVERT' and event.pkg_id = :pkg) and " +
						"file.id not in (select event.targetFile.id from Event as event where event.targetFile.id is not null " +
						"and event.type <> 'CONVERT' and event.pkg_id = :pkg) and lower(file.relative_path) like '%.xml' and " +
						"file.relative_path <> 'premis.xml' and lower(file.relative_path) like lower(:name) and file.pkg_id = :pkg", [pkg: id, name: dataName]))
					*/
					dafileMeta.each{
						def dat = it
						def datName = dat[0].getRelative_path()
						def name = datName.substring(0, datName.lastIndexOf('.'))
						if(!meta.containsKey(name)) {
							def d = new ArrayList<Object[]>()
							def a = [dat, pkg]
							d.add(a)
							meta.put(name, d)
						} else {
							def d = meta.get(name)
							def a = [dat, pkg]
							d.add(a)
						}
					} 
				} 
			//}
			
			int numberShowFiles = 10
	
			int max = 0
			
			if(dafiles.size() > meta.size()) {
				max = dafiles.size()
			} else {
				max = meta.size()
			}
		
			def zeit2 = new Date()
			
			dafilesMaxSize = dafiles.size()
			metaMaxSize = meta.size()
			
		//render(view:"premis", model:[zeit1: zeit1, zeit2: zeit2, pkgEvents: pkgEvents, dataName: dataName, numberShowFiles: numberShowFiles, max: max, meta: meta, dafiles: dafiles, packages: sortPackages, object: object, eventPkgList: eventPkgList])
		}
		else {
			render("Premis konnte nicht geladen werden.")
		}
	}

	def premisAnzeigen() {
		def xml = (new FileInputStream(params.xmldocument)).getText()
		render (text: xml, contentType: "text/xml", encoding: "UTF-8")
	}
	
}
