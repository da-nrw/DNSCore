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

class StatusController {

	def index() {
	
		def result = [:]
		def results = [:]
		
		def instance = null
		if (session.bauthuser == null) {
			log.error "Login failed";
			response.status = 403
			result = [status: "forbidden"]
			render result as JSON
			return
		}
		// listall objects of Contractor
		if (params.listallobjects) {
			def contractor = Contractor.findByShortName(session.bauthuser)
			def objects = Object.findAllByContractorAndObject_stateGreaterThan(contractor, 50)
			results.result = []
			objects.each()  { inst ->
				if (inst.object_state==100) result.status = "archived"
				else result.status = "failure"
				result.urn = inst.urn
				result.contractor = inst.contractor.shortName
				result.origName = inst.origName
				
				
				def packages = []
				result.packages = packages
				inst.packages.each() {pack ->
						result.packages.add(pack.name)
				}
				result = [:]
				
				results.result.add(result)
			}
			render results as JSON
			return
		}
				
		if (params.urn ) {
			Contractor cont = Contractor.findByShortName(session.bauthuser);
		
			instance = QueueEntry.withCriteria(uniqueResult: true) {
					createAlias('obj', 'o', 
						CriteriaSpecification.INNER_JOIN)
					createAlias('o.contractor', 'contractor', CriteriaSpecification.INNER_JOIN)
					eq("contractor.shortName", session.bauthuser)
					eq("o.urn", params.urn)
			};
			} else if (params.origName) {
			Contractor cont = Contractor.findByShortName(session.bauthuser);
			instance = QueueEntry.find("from QueueEntry as q where q.obj.contractor.shortName=:csn and q.obj.origName=:on",
             [on: params.origName,
				 csn: session.bauthuser]);
		} else if (params.identifier) {
			Contractor cont = Contractor.findByShortName(session.bauthuser);
			instance = QueueEntry.find("from QueueEntry as q where q.obj.contractor.shortName=:csn and q.obj.identifier=:idn",
			 [idn: params.identifier, 
				 csn: session.bauthuser]);
		}
		boolean hasAQueueEntry = false
		def queueResult = "in progress ";
		// found a QueueEntry
		if (instance!=null) {
			result.urn = instance.obj.urn
			result.contractor = instance.obj.contractor.shortName;
			result.origName = instance.obj.origName
			result.identifier = instance.obj.identifier
			if (instance.status.endsWith("1"))
				queueResult = "in progress error : (" + instance.status + ")"
				hasAQueueEntry = true;
		}  
		
		if (params.urn) {
				def contractor = Contractor.findByShortName(session.bauthuser)
				instance = Object.findByContractorAndUrn(contractor, params.urn)
		}
		if (params.origName) {
				def contractor = Contractor.findByShortName(session.bauthuser)
				instance = Object.findByContractorAndOrigName(contractor, params.origName)
		} 
		if (params.identifier) {
				def contractor = Contractor.findByShortName(session.bauthuser)
				instance = Object.findByContractorAndIdentifier(contractor, params.identifier)	
		}
		// Found Object, must be true if we found anything (Queue or Object only)
		if (instance != null && instance.count()==1) {
				if (instance.object_state==100 && !hasAQueueEntry) result.status = "archived"
				else if (hasAQueueEntry && instance.object_state==100) {
					result.status = "archived - but " + queueResult
				} else if (hasAQueueEntry && instance.object_state==0) {
					result.status = queueResult;
				} 
				result.urn = instance.urn
				result.contractor = instance.contractor.shortName
				result.origName = instance.origName
				result.identifier = instance.identifier
				def packages = []
				result.packages = packages;
				instance.packages.each() {pack ->
						result.packages.add(pack.name)
				} 
		// ambiguous item : mainly due to missing internal uniqueness of field urn!
		} else if (instance != null && instance.count()>1){
			response.status = 404
			result.status = "ambiguous item, alter request"
			render result as JSON
			return

		} else {
		// unknown item
				response.status = 404
				result.status = "not found"
				render result as JSON
				return
		}
		if (session.bauthuser!= result.contractor) {
			response.status = 403 
			result = [status: "forbidden"]
		}
    	
    	render result as JSON
    	
	}
	def teaser() {
		
		
	}
	
	

}
