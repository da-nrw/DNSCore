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
 * Adds capability of getting information about archived objects (AIP)
 * or SIP by automated systems (JSON responses)  
 * 
 * evaluates at first status in the Queue, then AIP 
 * 
 * @Author Jens Peters 
 * @Author Sebastian Cuy
 */
import org.hibernate.criterion.CriteriaSpecification;

import grails.converters.JSON
import groovy.json.StringEscapeUtils
import groovy.xml.StreamingDOMBuilder
import com.google.common.base.CharMatcher

class StatusController {
	
	def springSecurityService
	
	def index() {
		def result = [:]
		def results = [:]
		
		def rList = null
		def contractor = User.findByShortName(springSecurityService.currentUser.toString())
		// listall objects of Contractor
		results.result = []
		if (params.listallobjects) {
			def objects = Object.findAllByUserAndObjectStateGreaterThan(contractor, 49)
			
			objects.each()  { inst ->
				result.type = "Object"
				result.status = inst.getTextualObjectState()
				result.urn = inst.urn
				result.contractor = inst.user.shortName
				result.origName = inst.origName
				def packages = []
				result.packages = packages
				//inst.packages.each() {pack ->
				//		result.packages.add(pack.container_name)
				//}
				// rolled back to:
				
				def spackages = inst.packages.sort{it.id}
				spackages.each() {pack ->
					//result.packages.add(pack.container_name)
					result.packages.add(pack.delta)
				}
				result = [:]
				results.result.add(result)
			}
			
			render results as JSON
			return
		}
				
		if (params.urn ) {
			rList = QueueEntry.getAllQueueEntriesForShortNameAndUrn(springSecurityService.currentUser.toString(), params.urn)
		} else if (params.origName) {
			rList = QueueEntry.findAll("from QueueEntry as q where q.obj.user.shortName=:csn and q.obj.origName=:on",
             [on: params.origName,
				 csn: springSecurityService.currentUser.toString()]);
		} else if (params.identifier) {
			rList = QueueEntry.findAll("from QueueEntry as q where q.obj.user.shortName=:csn and q.obj.identifier=:idn",
			 [idn: params.identifier, 
				 csn: springSecurityService.currentUser.toString()]);
		} else if (params.containerName) {
			def criteria = QueueEntry.createCriteria ()
			rList = criteria.listDistinct {
				obj {
					and {
						user { eq 'shortName', springSecurityService.currentUser.toString() }
						packages { eq 'container_name', params.containerName  }
					}
				}
			}
		}
		boolean hasAQueueEntry = false
		def queueResult = "package in progress";
		def statusInt
		def statusLongDescription
		// found a QueueEntry
			rList.each()  { instance ->
			
			result.type = "QueueEntry"
			result.urn = instance.obj.urn
			result.contractor = instance.obj.user.shortName;
			result.origName = instance.obj.origName
			result.identifier = instance.obj.identifier
			if (instance.errorText == null) {
				instance.errorText = "";
			} else {
				String strErrorText = instance.errorText;
				if (strErrorText.contains(System.getProperty("line.separator"))) {  
				instance.errorText = strErrorText.replaceAll(System.getProperty("line.separator"),"")    
				}
			} 
			
			if (instance.status.endsWith("1")) {
				queueResult = "package in progress error: (" + instance.status + ") "
				statusInt = Integer.parseInt(instance.status)
				if (!instance.errorText.equals("")) {
					statusLongDescription = ["package in progress error: (" + instance.status + ") ", instance.errorText]
				} else {
					statusLongDescription = "package in progress error: (" + instance.status + ") "
				}
			}
			if (instance.status.endsWith("3")) {
				queueResult = "package in progress error: (" + instance.status + ") "
				statusInt = Integer.parseInt(instance.status)
				if (!instance.errorText.equals("")) {
					statusLongDescription = ["package in progress error: (" + instance.status + ") ", instance.errorText]
				} else {
					statusLongDescription = "package in progress error: (" + instance.status + ") "
				}
			}
			if (instance.status.endsWith("4")) {
				queueResult = "package in progress error: (" + instance.status + ") "
				statusInt = Integer.parseInt(instance.status)
				if (!instance.errorText.equals("")) {
					statusLongDescription = ["package in progress error: (" + instance.status + ") ", instance.errorText]
				} else {
					statusLongDescription = "package in progress error: (" + instance.status + ") "
				}
			}
			if (instance.status.endsWith("5")) {
				queueResult = "package in progress error: (" + instance.status + ") "
				statusInt = Integer.parseInt(instance.status)
				if (!instance.errorText.equals("")) {
					statusLongDescription = ["package in progress error: (" + instance.status + ") ", instance.errorText]
				} else {
					statusLongDescription = "package in progress error: (" + instance.status + ") "
				}
			}
			if (instance.status.endsWith("6")) {
				queueResult = "package in progress error: (" + instance.status + ") "
				statusInt = Integer.parseInt(instance.status)
				if (!instance.errorText.equals("")) {
					statusLongDescription = ["package in progress error: (" + instance.status + ") ", instance.errorText]
				} else {
					statusLongDescription = "package in progress error: (" + instance.status + ") "
				}
			}
			if (instance.status.endsWith("7")) {
				queueResult = "package in progress error: (" + instance.status + ") "
				statusInt = Integer.parseInt(instance.status)
				if (!instance.errorText.equals("")) {
					statusLongDescription = ["package in progress error: (" + instance.status + ") ", instance.errorText]
				} else {
					statusLongDescription = "package in progress error: (" + instance.status + ") "
				}
			}
			if (instance.status.endsWith("8")) {
				queueResult = "package in progress error: (" + instance.status + ") "
				statusInt = Integer.parseInt(instance.status)
				if (!instance.errorText.equals("")) {
					statusLongDescription = ["package in progress error: (" + instance.status + ") ", instance.errorText]
				} else {
					statusLongDescription = "package in progress error: (" + instance.status + ") "
				}
			}
			if (instance.status.endsWith("0")) {
				queueResult = "package in progress error: (" + instance.status + ") "
				statusInt = Integer.parseInt(instance.status)
				if (!instance.errorText.equals("")) {
					statusLongDescription = ["package in progress error: (" + instance.status + ") ", instance.errorText]
				} else {
					statusLongDescription = "package in progress error: (" + instance.status + ") "
				}
			}
			if (instance.status.endsWith("2")) {
				queueResult = "package in progress error: (" + instance.status + ") "
				statusInt = Integer.parseInt(instance.status)
			}
			result.status =queueResult
			result.statusInt=statusInt
			result.statusLongDescription=statusLongDescription
			
			hasAQueueEntry = true;
			results.result.add(result)
			result = [:]
		}  
		
		if (params.urn) {
			rList = Object.findAllByUserAndUrnAndObjectStateBetween(contractor, params.urn,50,100)
		}
		if (params.origName) {
				rList = Object.findAllByUserAndOrigNameAndObjectStateBetween(contractor, params.origName,50,100)
		} 
		if (params.identifier) {
				rList = Object.findAllByUserAndIdentifierAndObjectStateBetween(contractor, params.identifier,50,100)	
		}
		if (params.containerName) {
			def criteria = Object.createCriteria ()
			rList = criteria.listDistinct {
				and {
					eq 'user', contractor
				}
				packages{
					eq 'container_name', params.containerName 
				}
			}
		}
		// Found Object, must be true if we found anything (Queue or Object only)
		boolean foundObject = false;
		rList.each()  { instance ->	
				result.type = "Object"		
				result.status = instance.getTextualObjectState()
				result.statusInt = instance.objectState
				result.statusLongDescription = instance.getTextualObjectState()
				result.urn = instance.urn
				result.contractor = instance.user.shortName
				result.origName = instance.origName
				result.identifier = instance.identifier
				def packages = []
				result.packages = packages;
				
				//instance.packages.each() {pack ->
				//		result.packages.add(pack.container_name)
				//}
				def spackages = instance.packages.sort{it.id}
				spackages.each() {pack ->
					//result.packages.add(pack.container_name)
					result.packages.add(pack.delta)
				} 
				results.result.add(result)
				result = [:]
				foundObject = true;
		} 
		// unknown item
		if (!foundObject && !hasAQueueEntry) {
		response.status = 404
		result = [ status : "not found", statusInt : 404]
		render result as JSON
		return	
		}
		
		render results as JSON
		return
	}
	
	
	def teaser() {
		def admin = 0
		User user = springSecurityService.currentUser
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		[user: user, admin: admin]
	}
	
	

}
