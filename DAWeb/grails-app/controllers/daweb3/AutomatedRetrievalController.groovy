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

package daweb3
import grails.converters.*


/**
 * Create Retrieval Requests from JSON requests
 * @Author Jens Peters
 */
class AutomatedRetrievalController {
	
    def index() { }
	
	def queueForRetrievalJSON () {
		def QueueUtils qu = new QueueUtils(); 
		def result = [success:false]
		def jsonObject = request.JSON
		
		def instance = Object.findByIdentifier(jsonObject['identifier'])
		
		if (instance!=null) {
			
			if (instance.contractor.shortName != session.bauthuser) {
				result.msg = "Sie haben nicht die nötigen Berechtigungen, um das Objekt " + jsonObject['identifier'] + " anzufordern!"
				render result as JSON
				return
			}
			qu.createJob( instance ,"900", grailsApplication.config.irods.server)
			result = [success:true]
			result.msg = "Erfolgreich Arbeitsauftrag erstellt für "  + jsonObject['identifier']
			render result as JSON
			return
			}
		instance = Object.findByUrn(jsonObject['urn'])
		if (instance!=null) {
			if (instance.contractor.shortName != session.bauthuser) {
				result.msg = "Sie haben nicht die nötigen Berechtigungen, um das Objekt "+ jsonObject['urn'] + " anzufordern!"
				render result as JSON
				return
			}
			qu.createJob( instance ,"900", null)
			result = [success:true]
			result.msg = "Erfolgreich Arbeitsauftrag erstellt für "  + jsonObject['urn']
			render result as JSON
			return
		}
		instance = Object.findByOrigName(jsonObject['origName'])
		if (instance!=null) {
			if (instance.contractor.shortName != session.bauthuser) {
				result.msg = "Sie haben nicht die nötigen Berechtigungen, um das Objekt "+ jsonObject['origName'] + " anzufordern!"
				render result as JSON
				return
			}
			qu.createJob( instance ,"900", null)
			result = [success:true]
			result.msg = "Erfolgreich Arbeitsauftrag erstellt für "  + jsonObject['origName']
			render result as JSON
			return
		}
		
		result.msg = "Fehler bei Erstellung eines Arbeitsauftrages"
		render result as JSON
		
	}
}
