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
 * Lists the SIP being converted or DIP being retrieved and their respective status 
 * @Author Jens Peters
 * @Author Sebastian Cuy 
 */
import java.util.logging.Logger;
import org.hibernate.criterion.CriteriaSpecification;

import org.springframework.aop.TrueClassFilter;
import org.springframework.dao.DataIntegrityViolationException

class QueueEntryController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
		def contractorList
		def cbNodeList = CbNode.list()
		if (session.contractor.admin == 1) {	
			contractorList = Contractor.list()
		} else {
			contractorList = Contractor.findAll("from Contractor as c where c.shortName=:csn",
	        [csn: session.contractor.shortName])
		}
		[contractorList:contractorList,
		cbNodeList:cbNodeList]
    }
    

    def listSnippet() {
    	def queueEntries = null	
		def admin = false
		def periodical = true;	
		def contractorList = Contractor.list()
		
		if (params.search==null){		
			if (session.contractor.admin != 1) {	
				queueEntries = QueueEntry.findAll("from QueueEntry as q where q.obj.contractor.shortName=:csn",
	             [csn: session.contractor.shortName])
			} else {
				admin = true;
				queueEntries = QueueEntry.findAll("from QueueEntry as q")
				
			}
			[queueEntryInstanceList: queueEntries,
				admin:admin, periodical:periodical ]
		} else {
			periodical = false;
			def c = QueueEntry.createCriteria()
			queueEntries = c.list() {
				if (params.search?.obj) params.search.obj.each { key, value ->
						if (value!="") {
						projections {
							obj {
								like(key, "%" + value + "%")
							}
						}
					}
				}
				if (params.search?.initialNode) { 
					if (params.search?.initialNode !="null"){ 
						like("initialNode", params.search.initialNode+"%")
					}
				}
				if (params.search?.status) 
					like("status", params.search.status+"%")
				if (session.contractor.admin==0) {
					def contract = Contractor.findByShortName(session.contractor.shortName)
					projections {
						obj {
								contractor {
									eq("shortName", contract.shortName)								
								}
						}
					}
				} else {
				admin = true;
				if (params.search?.contractor){
					if(params.search?.contractor !="null"){
						projections {
							obj {
									contractor {
										eq("shortName", params.search.contractor)
									}
								}
							}	
						}
					}
				}
			}
		} 
		[queueEntryInstanceList: queueEntries,
			admin:admin, periodical:periodical,
			contractorList:contractorList ]
    }
	
	/** 
	 * Generates detailed view for one item (SIP) in workflow
	 */
    def show() {
        def queueEntryInstance = QueueEntry.get(params.id)
        if (!queueEntryInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'queueEntry.label', default: 'QueueEntry'), params.id])
            redirect(action: "list")
            return
        }

        [queueEntryInstance: queueEntryInstance]
    }
	
	/**
	 * Applies button and functionality to retry the last workflow step for an item
	 */
	def queueRetry() {
		def queueEntryInstance = QueueEntry.get(params.id)
		if (queueEntryInstance) {
			def status = queueEntryInstance.getStatus()
			if (status.endsWith("1")) {
				def newstat = status.substring(0,status.length()-1)
				newstat = newstat + "0"
				queueEntryInstance.status = newstat
				queueEntryInstance.modified = Math.round(new Date().getTime()/1000L)
				if( !queueEntryInstance.save() ) {
					log.debug("Validation errors on save")
					queueEntryInstance.errors.each {
						log.debug(it)
					}
				} 
				flash.message = "Status zurückgesetzt!" 
			}
			redirect(action: "list")
			return
			
		} else flash.message = message(code: 'default.not.found.message', args: [message(code: 'queueEntry.label', default: 'QueueEntry'), params.id])
		redirect(action: "list")
		return

		[queueEntryInstance: queueEntryInstance]
	}
	
	/**
	 * Applies button and functionality to recover all the workflow for an item
	 */
	def queueRecover() {
		def queueEntryInstance = QueueEntry.get(params.id)
		if (queueEntryInstance) {
			def status = queueEntryInstance.getStatus()
			int state = status.toInteger();
			
			if ((state>=123 && state<=353) && status.endsWith("3") && !status.endsWith("1")) {
				// Recover state is 600
				def newstat = "600"
				queueEntryInstance.status = newstat
				queueEntryInstance.modified = Math.round(new Date().getTime()/1000L)
				if( !queueEntryInstance.save() ) {
					log.debug("Validation errors on save")
					queueEntryInstance.errors.each {
						log.debug(it)
					}
				}
				flash.message = "Paket recovered!"
			} else flash.message = "Paket ist nicht zurückstellbar"
			redirect(action: "list")
			return
			
		} else flash.message = message(code: 'default.not.found.message', args: [message(code: 'queueEntry.label', default: 'QueueEntry'), params.id])
		redirect(action: "list")
		return

		[queueEntryInstance: queueEntryInstance]
	}
	
	/**
	 * Applies button and functionality to remove an item from ContentBroker workflow
	 */
	def queueDelete() {
		def queueEntryInstance = QueueEntry.get(params.id)
		if (queueEntryInstance) {
			def status = queueEntryInstance.getStatus()
			int state = status.toInteger();
			
			if (queueEntryInstance.showDeletionButton() && state <401) {
				// Delete state is 800
				def newstat = "800"
				queueEntryInstance.status = newstat
				queueEntryInstance.modified = Math.round(new Date().getTime()/1000L)
				if( !queueEntryInstance.save() ) {
					log.debug("Validation errors on save")
					queueEntryInstance.errors.each {
						log.debug(it)
					}
				}
				flash.message = "Paket für Löschung vorgesehen"
			} else flash.message = "Paket ist nicht löschbar, wenden Sie sich an Ihren Knotenadmin!"
			redirect(action: "list")
			return
			
		} else flash.message = message(code: 'default.not.found.message', args: [message(code: 'queueEntry.label', default: 'QueueEntry'), params.id])
		redirect(action: "list")
		return

		[queueEntryInstance: queueEntryInstance]
	}

}
