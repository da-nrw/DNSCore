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
 * @Author Jens Peters
 */
import de.uzk.hki.da.core.ActionDescription
import javax.jms.ObjectMessage
import javax.jms.TextMessage


/**
 * Webcontroller to Talk with running ContentBroker via Webinterface
 * @author Jens Peters
 *
 */

class CbtalkController {
	def jmsService
	def cbtalkService
	def cberrorService

	def index() { 	
		if (grailsApplication.config.localNode.id==null || grailsApplication.config.localNode.id=="")
		flash.message = "LOCALNODE.ID not set!"
	}
	def messageSnippet() {
		def messages = cbtalkService.getMessages()
		def errors = cberrorService.getMessages()
		def date = new Date();
		[messages: messages,
			errors: errors,
			date: date]
	}
	
	def save() {
			
		def message = ""
		if (params.stopFactory) {
			message = "STOP_FACTORY"	
		}			
		if (params.startFactory) {
			
			message = "START_FACTORY"
		
		}
		if (params.showActions){
			
			message = "SHOW_ACTIONS";
		}
		if (params.gracefulShutdown){
			
			message = "GRACEFUL_SHUTDOWN";
		}
		if (params.showVersion){	
			message = "SHOW_VERSION";
		}
		log.debug(message)
		try {
			
	
		jmsService.send(queue:'CB.SYSTEM', message)
		} catch (Exception e) {
			flash.message= "Fehler in der Sendekommunikation mit dem ActiveMQ Broker! " + e.getCause()
			log.error(e);
		}
		redirect(action: "index")
		
	} 
	
	
		
		
}
