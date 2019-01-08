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
 * Adds capability deleting incorrect SIP by automated systems (JSON responses)  
 * 
 * evaluates in the Queue
 * 
 * @Author Gaby Bender
 */
import org.hibernate.criterion.CriteriaSpecification;

import grails.converters.JSON

class DeleteController {
	
	def springSecurityService
	static QueueUtils que = new QueueUtils();
	
	def index() {
		def result = [:]
		def results = [:]
		
		def rList = null
		def contractor = User.findByShortName(springSecurityService.currentUser.toString())
		results.result = []
		
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
		}	
		
		boolean hasAQueueEntry = false
		def queueResult = "package will be deleted";
		// found a QueueEntry
			rList.each()  { instance ->
			
			result.type = "QueueEntry"
			result.urn = instance.obj.urn
			result.contractor = instance.obj.user.shortName;
			result.origName = instance.obj.origName
			result.identifier = instance.obj.identifier
			if (instance.status.endsWith("1")) {
				queueResult = "package state : (" + instance.status + ")  will be deleted"
				que.modifyJob(instance.id.toString(), "800")
			}
			if (instance.status.endsWith("3")) {
				queueResult = "package state : (" + instance.status + ")  will be deleted"
				que.modifyJob(instance.id.toString(), "800")
			}
			if (instance.status.endsWith("4")) {
				queueResult = "package state : (" + instance.status + ")  will be deleted"
				que.modifyJob(instance.id.toString(), "800")
			}
			if (instance.status.endsWith("5")) {
				queueResult = "package state : (" + instance.status + ")  will be deleted"
				que.modifyJob(instance.id.toString(), "800")
			}
			if (instance.status.endsWith("6")) {
				queueResult = "package state : (" + instance.status + ")  will be deleted"
				que.modifyJob(instance.id.toString(), "800")
			}
			if (instance.status.endsWith("7")) {
				queueResult = "package state : (" + instance.status + ")  will be deleted"
				que.modifyJob(instance.id.toString(), "800")
			}	
			result.status =queueResult
			
			results.result.add(result)
		}  
		
		render results as JSON
		
		return
	}
	
	def deleter() {
		def admin = 0
		User user = springSecurityService.currentUser
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		[user: user, admin: admin]
	}
}
