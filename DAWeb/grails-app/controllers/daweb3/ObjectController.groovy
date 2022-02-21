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



import grails.converters.*
import javassist.expr.Instanceof


class ObjectController {


	static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
	static QueueUtils qu = new QueueUtils();
	
	private static String IN_BEARBEITUNG = "sich in Bearbeitung befindlichen"
	private static String ALLE_OBJECTE = "gesamten"
	private static String ARCHIVIERTE_OBJECTE = "verarbeiteten"
	private static String FEHLERHAFTE_OBJECTE = "fehlerhaften"

	def springSecurityService
	boolean archivedObjects = false;
	boolean workingObjects = false;
	boolean errorObjects = false;

	def index() {
		redirect(action: "list", params: params)
	}

	def cancel(){
		redirect(action: "list")
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
		User user = springSecurityService.currentUser
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		// access table objects
		def mostRecentSecondAtt
		if (object.most_recent_secondary_attributes != null) {
			mostRecentSecondAtt = object.most_recent_secondary_attributes.toUpperCase();
		}
		objects = Object.findAll("from Object as o where o.most_recent_formats like :formats " +
				" or o.most_recent_secondary_attributes like :attributes)"   ,
				[formats:'%'+object.most_recent_formats+'%',
					attributes:"%"+ mostRecentSecondAtt +"%"])

		// list of results
		[ objects:objects ]

		if (objects == []&& (object.most_recent_formats != null  ||  object.most_recent_secondary_attributes != null)) {
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
			render (view:'listObjects', model:[objects:objects, extension:extList, name:nameList, user:user, admin:admin]);
		}
	}

	def listAll () {
		archivedObjects = false;
		workingObjects = false;
		errorObjects = false;
		redirect(action: "list", params: params)
	}

	def list() {
		if (archivedObjects ) {
			getObjects(100);
		} else if (workingObjects) {
			getObjects(50);
		} else if (errorObjects) {
			getObjects(55);
		}

		else {

			User user = springSecurityService.currentUser

			def contractorList = User.list()
			def cbNodeList = CbNode.list()
			def admin = 0;
			def relativeDir = user.getShortName() + "/outgoing"
			def filterOn = params.filterOn;
			if (filterOn==null) filterOn=0
			def objArt = ALLE_OBJECTE

			def baseFolder = grailsApplication.config.getProperty('localNode.userAreaRootPath') + "/" + relativeDir
			params.max = Math.min(params.max ? params.int('max') : 50, 200)

			if (params.searchContractorName){
				if(params.searchContractorName=="null"){
					params.remove("searchContractorName")
				}
			}
			
			if (params.initialNode){
				if(params.initialNode=="null"){
					params.remove("initialNode")
				}
			}
			
			def c1 = Object.createCriteria()
			def objectsTotalForCount 
			 
			if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
				objectsTotalForCount = c1.list() {
					between("objectState", 50,200)
				}
			} else {
				objectsTotalForCount = c1.list() {
					eq("user.id", user.id)
					between("objectState", 50,200)
				}
			}
			def totalObjs = objectsTotalForCount.size();
			
			
			
			def c = Object.createCriteria()
			log.debug(params.toString())
			def objects = c.list(max: params.max, offset: params.offset ?: 0) {
				/*if (params.search) params.search.each { key, value ->
				 if (value!="") filterOn=1
				 like(key, "%" + value + "%")
				 }*/


				/*
				 *like fuer alle  oder nur fuer die value!="" ??
				 */
				if (params.search) {
					params.search.each
					{ key, value ->
						if (!(value==""/* && value=="null" && value==null*/)) {
							filterOn=1
							like(key, "%" + value + "%")
						}

					}
				}

				log.debug("Date as Strings " + params.searchDateStart + " and " + params.searchDateEnd)
				def searchDateStart = params.searchDateStart
				def searchDateEnd = params.searchDateEnd
			

				def st = "" //"createdAt";

				String searchDateType = params.searchDateType;
				if (searchDateStart !=null || searchDateEnd !=null) {
					if  (!searchDateStart.equals("0") || !searchDateEnd.equals("0")) {
						if	(!searchDateStart.equals(" ") || !searchDateEnd.equals(" ")){
							if (!searchDateStart.equals("") || !searchDateEnd.equals("")) {
								if ( searchDateType == "null")  {
									params.searchDateType = "createdAt"
									searchDateType = "createdAt"
								}
							}
						}
					}
				}
				
				// Suchdatum Start und Ende befüllt
				if (searchDateStart !=null  && !searchDateStart.equals("") && !searchDateStart.equals(" ") &&
				searchDateEnd!=null   && !searchDateEnd.equals("") && !searchDateEnd.equals(" "))  {
					if (!searchDateStart.equals("0") && !searchDateEnd.equals("0")) {
						filterOn=1

						log.debug("Objects between " + searchDateStart + " and " + searchDateEnd)
						if (searchDateStart instanceof String) {
							searchDateStart = Object.convertStringIntoDate(searchDateStart)
						}
						
						if (searchDateEnd instanceof String) {
							searchDateEnd = Object.convertStringIntoDate(searchDateEnd)
						}
						
					 	between(searchDateType, searchDateStart, searchDateEnd)
					}
				}

				// Suchdatum Start gefüllt
				if (searchDateStart != null &&  searchDateEnd == null ){
					filterOn=1
					log.debug("Objects greater than " + searchDateStart)
					gt(searchDateType,searchDateStart)
				}

				// Suchdatum Ende gefüllt
				if (searchDateStart==null && searchDateEnd!=null ) {
					filterOn=1
					log.debug("Objects lower than " + searchDateEnd)
					lt(searchDateType,searchDateEnd)
				}

				if (params.searchQualityLevel!=null) {

					if(params.searchQualityLevel?.isInteger()){
						filterOn=1
						log.debug("QualityLevel filter on :"+params.searchQualityLevel)
						eq("quality_flag", Integer.valueOf(params.searchQualityLevel))
						//between("quality_flag", params.searchQualityLevel,params.searchQualityLevel+1)
					}
				}

				if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
					admin = 1;
				}

				if (admin==0) {

					eq("user.id", user.id)
				}

				if (admin==1) {
					if (params.searchContractorName!=null) {
						if	( !params.searchContractorName.isEmpty() || params.searchContractorName != "") {
							filterOn=1
							createAlias( "user", "c" )
							eq("c.shortName", params.searchContractorName)
						}
					}
					
					if (params.initialNode!=null) {
						if	( !params.initialNode.isEmpty() || params.initialNode != "") {
							filterOn=1
							eq("initialNode", params.search.initialNode)
						}
					}
				}

				between("objectState", 50,200)
				order(params.sort ?: "id", params.order ?: "desc")
			}

			log.debug("Search " + params.search)
			// workaround: make ALL params accessible for following http-requests
			def paramsList = params.search?.collectEntries { key, value -> ['search.'+key, value]}
			if(params.searchContractorName){
				paramsList.putAt("searchContractorName", params?.searchContractorName)

			}
			
			if(params.initialNode){
				paramsList.putAt("initialNode", params?.initialNode)

			}
			
			if (paramsList != null) {
				paramsList.putAt("searchDateType", params?.searchDateType);
				if (params?.searchDateStart instanceof String ) {
					paramsList.putAt("searchDateStart", Object.convertStringIntoDatString(params?.searchDateStart));
					params.putAt("searchDateStart", Object.convertStringIntoDate(params?.searchDateStart));
				} else {
					paramsList.putAt("searchDateStart", params?.searchDateStart);
				}
				if (params?.searchDateEnd  instanceof String) {
					paramsList.putAt("searchDateEnd", Object.convertStringIntoDatString(params?.searchDateEnd));
					params.putAt("searchDateEnd", Object.convertStringIntoDate(params?.searchDateEnd));
				} else {
					paramsList.putAt("searchDateEnd", params?.searchDateEnd);
				}
				
				paramsList.putAt("searchQualityLevel", params?.searchQualityLevel);
			}

	 
			if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
				render(view:"adminList", model:[	objectInstanceList: objects,
					objectInstanceTotal: objects.getTotalCount(),
					searchParams: params.search,
					filterOn: filterOn,
					paramsList: paramsList,
					paginate: true,
					admin: admin,
					totalObjs: totalObjs,
					baseFolder: baseFolder,
					contractorList: contractorList,
					user: user ,
					objArt: objArt,
					cbNodeList:cbNodeList,
					params: params
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
					contractorList: contractorList,
					user: user ,
					objArt: objArt,
					params: params
				]);
		}
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
		def sortedPackages = objectInstance.packages.sort{ it.id }
		def preslink = grailsApplication.config.getProperty('fedora.urlPrefix') +urn.replaceAll(~"urn:nbn:de:danrw-", "")

		/*
		 * DANRW-1417: extension about the access to the table format_mapping
		 */

		Map<String, String> extListSip = [:]
		if (objectInstance != []&& objectInstance.original_formats != null) {
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
		if (objectInstance != []&& objectInstance.most_recent_formats != null) {
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
			urn:urn,
			preslink:preslink,
			sortedPackages:sortedPackages,
			extensionSip:extListSip,
			extensionDip:extListDip,
			user: user, admin: admin
		]

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
						CbNode cbn = CbNode.get(grailsApplication.config.getProperty('localNode.id'))

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
					String lnid = grailsApplication.config.getProperty('localNode.id')
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
				String lnid = grailsApplication.config.getProperty('localNode.id')
				log.debug("Create Rebuild PIP job on node id: " + lnid)
				CbNode cbn = CbNode.get(Integer.parseInt(lnid))
				qu.createJob( object ,"700", cbn.getName())
				result.msg = "Auftrag zur Erstellung neuer Präsentationsformate erstellt ${object.urn}."
				result.success = true
			} catch ( Exception e ) {
				result.msg = "Auftrag zur Erstellung neuer Präsentationsformate für ${object.urn} konnte nicht angelegt werden."
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
				def ids= grailsApplication.config.getProperty('localNode.id')
				CbNode cbn = CbNode.get(Integer.valueOf(ids))
				qu.createJob( object, "5000" , cbn.getName())
				result.msg = "Das Objekt mit der URN ${object.urn} wurde zur Überprüfung in die Queue eingestellt!"
				result.success = true
			} catch(Exception e) {
				result.msg = "Fehler bei der Erstellung des Arbeitsauftrages zur Überprüfung des Objekts mit der URN ${object.urn}. Bitte wenden Sie sich an einen Administrator."
				log.error(result.msg)
			}
		}
		render result as JSON
	}

	def collectSearchParams = {
		def paramList = params.search?.collectEntries { key, value -> ['search.'+key, value]}
		paramsList.putAt("searchContractorName", params?.searchContractorName)
		paramsList.putAt("initialNode", params?.initialNode)
		return
		[paramsList:paramsList]
	}

	def getObjects(int status) {

		User user = springSecurityService.currentUser

		def contractorList = User.list()
		def cbNodeList = CbNode.list()
		def admin = 0;
		def relativeDir = user.getShortName() + "/outgoing"
		def filterOn = params.filterOn;
		if (filterOn==null) filterOn=0

		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		
		
		
		def objArt = IN_BEARBEITUNG
		if (status == 100) {
			objArt = ARCHIVIERTE_OBJECTE
		} else if (status == 55){
			objArt = FEHLERHAFTE_OBJECTE
 			status = 50
		}

		def baseFolder = grailsApplication.config.getProperty('localNode.userAreaRootPath') + "/" + relativeDir
		params.max = Math.min(params.getObjectsmax ? params.int('max') : status, status)

		if (params.searchContractorName){
			if(params.searchContractorName=="null"){
				params.remove("searchContractorName")
			}
		}
		
		if (params.initialNode){
			if(params.initialNode=="null"){
				params.remove("initialNode")
			}
		}
		

		def c1 = Object.createCriteria()
		def statusQueue
		def objectsArchivedForCount = c1.list() {
			if (admin == 1) {
				eq("objectState", status)
			} else 	{
				eq("user.id", user.id)
				eq("objectState", status)
			}
		}

		def totalArchivedObjects = objectsArchivedForCount.size();
if (objArt == )

		def c = Object.createCriteria()
		log.debug(params.toString())
		if (status == 50) {
			if ( params.search != null) {
				if (params.search.urn != null) {
					if (params.search.urn.isEmpty()) {
						params.search.remove("urn");
					}
				}
			}

			def cStatus = Object.createCriteria()
			List<Object> listObject= cStatus.list(){
				eq("user.id", user.id)
				eq("objectState", status)
			};

		}

		
		List<String> queueList = new ArrayList()
		List<Object> objects = c.list(max: params.max, offset: params.offset ?: 0) {

			if (params.search) {
				params.search.each
				{ key, value ->
					if (!(value==""/* && value=="null" && value==null*/)) {
						filterOn=1
						like(key, "%" + value + "%")
					}

				}
			}

			log.debug("Date as Strings " + params.searchDateStart + " and " + params.searchDateEnd)

			def searchDateStart = params.searchDateStart
			def searchDateEnd = params.searchDateEnd

			def st = "" //"createdAt"; BEG 310530
			String searchDateType = params.searchDateType;

			if (searchDateStart !=null || searchDateEnd !=null) {
				if  (!searchDateStart.equals("0") || !searchDateEnd.equals("0")) {
					if	(!searchDateStart.equals(" ") || !searchDateEnd.equals(" ")){
						if (!searchDateStart.equals("") || !searchDateEnd.equals("")) {
							if ( searchDateType == "null")  {
								params.searchDateType = "createdAt"
								searchDateType = "createdAt"
							}
						}
					}
				}
			}

			// Suchdatum Start und Ende befüllt
			if (searchDateStart !=null  && !searchDateStart.equals("") && !searchDateStart.equals(" ") &&
			searchDateEnd!=null   && !searchDateEnd.equals("") && !searchDateEnd.equals(" "))  {
				if (!searchDateStart.equals("0") && !searchDateEnd.equals("0")) {
					filterOn=1
					log.debug("Objects between " + searchDateStart + " and " + searchDateEnd)
					between(st, searchDateStart, searchDateEnd)
				}
			}

			// Suchdatum Start gefüllt
			if (searchDateStart!=null && searchDateEnd==null ) {
				if (!searchDateStart.equals("0") && searchDateEnd.equals("0")) {
					filterOn=1
					log.debug("Objects greater than " + searchDateStart)
					gt(st,searchDateStart)
				}
			}

			// Suchdatum Ende gefüllt
			if (searchDateStart==null && searchDateEnd !=null ) {
				if (searchDateStart.equals("0") || !searchDateEnd.equals("0")) {
					filterOn=1
					log.debug("Objects lower than " + searchDateEnd)
					lt(st,searchDateEnd)
				}
			}


			if (params.searchQualityLevel!=null) {

				if(params.searchQualityLevel?.isInteger()){
					filterOn=1
					log.debug("QualityLevel filter on :"+params.searchQualityLevel)
					eq("quality_flag", Integer.valueOf(params.searchQualityLevel))
				}
			}

			if (admin==1) {
				if (params.searchContractorName!=null) {
					if	( !params.searchContractorName.isEmpty() || params.searchContractorName != "") {
						filterOn=1
						createAlias( "user", "c" )
						eq("c.shortName", params.searchContractorName)
					}
				}
			
				if (params.initialNode!=null) {
					if	( !params.initialNode.isEmpty() || params.initialNode != "") {
						filterOn=1
						eq("initialNode", params.search.initialNode)
					}
				}
			}
			
			eq("objectState", status)
			order(params.sort ?: "id", params.order ?: "desc")

		}

		
		if (status == 50) {
			for ( int i= 0; i < objects.size(); i++) {
				int id =  objects.getAt(i).id;
				statusQueue = QueueEntry.findAll("from QueueEntry as q where q.obj.id = :data_pk and (q.status like '%0' OR q.status like '%2')",
						[data_pk: id]);
				/* um die Liste der fehlerhafte Pakete zu erstellen, müssen aus der Liste der Objecte 
				 * diejenigen entfernt werden, welche im Status auf 0 oder 2 enden. 
				 * Dies sind Pakete in der Verarbeitung 
				 */
				if (!statusQueue.isEmpty() && objArt.equals(FEHLERHAFTE_OBJECTE)) {
 					objects.remove(i);
					 i--;
				} 
				/* um die Liste der sich in Bearbeitung befindlichen Pakete zu erstellen, müssen aus der Liste der Objecte
				 * diejenigen entfernt werden, welche nicht im Status auf 0 oder 2 enden.
				 * Dies sind die fehlerhaften Pakete  
				 */
				if (statusQueue.isEmpty() && objArt.equals(IN_BEARBEITUNG)) {
					objects.remove(i);
					i--;
				}
				/*
				 * Wenn es sich um fehlerhaft Pakete handelt, so muss der Status
				 * aus der queue geholt werden
				 */
				if (objArt.equals(FEHLERHAFTE_OBJECTE) && statusQueue.isEmpty()) {
					def queue = QueueEntry.findAll("from QueueEntry as q where q.obj.id = :data_pk", [data_pk: id]);
					queueList.add(queue)
				}
			}
		}
		
		
		log.debug("Search " + params.search)
		// workaround: make ALL params accessible for following http-requests
		def paramsList = params.search?.collectEntries { key, value -> ['search.'+key, value]}
		if(params.searchContractorName){
			paramsList.putAt("searchContractorName", params?.searchContractorName)
		}
		
		if (params.initialNode) {
			paramsList.putAt("initialNode", params?.initialNode)
		}

		if (paramsList != null) {
			paramsList.putAt("searchDateType", params?.searchDateType);
			paramsList.putAt("searchDateStart", params?.searchDateStart);
			paramsList.putAt("searchDateEnd", params?.searchDateEnd);
			paramsList.putAt("searchQualityLevel", params?.searchQualityLevel);
		}

		
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			render(view:"adminList", model:[	objectInstanceList: objects,
				objectInstanceTotal: objects.getTotalCount(),
				searchParams: params.search,
				filterOn: filterOn,
				paramsList: paramsList,
				paginate: true,
				admin: admin,
				totalObjs: totalArchivedObjects,
				baseFolder: baseFolder,
				contractorList: contractorList,
				user: user,
				objArt: objArt,
				cbNodeList:cbNodeList,
				queueList: queueList
			]);
		} else render(view:"list", model:[	objectInstanceList: objects,
				objectInstanceTotal: objects.getTotalCount(),
				searchParams: params.search,
				filterOn: filterOn,
				paramsList: paramsList,
				paginate: true,
				admin: 0,
				totalObjs: objects.size(), //otalArchivedObjects,
				baseFolder: baseFolder,
				contractorList: contractorList,
				user: user,
				objArt: objArt,
			 	queueList: queueList
			]);

	}

	def archived() {
		archivedObjects = true;
		workingObjects = false;
		getObjects(100);
	}

	def working() {
		archivedObjects = false;
		workingObjects = true;
		getObjects(50);
	}

	def error() {
		archivedObjects = false;
		errorObjects = true;
		getObjects(55); // Dummy -Status fuer error
	}
}
