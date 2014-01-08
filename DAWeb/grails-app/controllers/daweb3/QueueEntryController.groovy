package daweb3
/**
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
 @Author Jens Peters
 @Author scuy
*/
import java.util.logging.Logger;

import org.springframework.aop.TrueClassFilter;
import org.springframework.dao.DataIntegrityViolationException

class QueueEntryController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        
    }
    
    def listSnippet() {
    	def queueEntries = null	
		def admin = false	
		if (session.contractor.admin != 1) {
			queueEntries = QueueEntry.findAll("from QueueEntry as q where q.obj.contractor.shortName=:csn",
             [csn: session.contractor.shortName])
		} else {
			admin = true;
			queueEntries = QueueEntry.findAll(params)
		}
		[queueEntryInstanceList: queueEntries,
			admin:admin ]
    }

    def show() {
        def queueEntryInstance = QueueEntry.get(params.id)
        if (!queueEntryInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'queueEntry.label', default: 'QueueEntry'), params.id])
            redirect(action: "list")
            return
        }

        [queueEntryInstance: queueEntryInstance]
    }
	
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
}
