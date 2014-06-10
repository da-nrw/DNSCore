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

/**
 * Create a queue entry in dedicated state
 * @author Jens Peters, Sebastian Cuy
 *
 */

class QueueUtils {

	/**
	 * Creates a job with status on responsibleNode
	 * for a selected object. Also sets the object_state to 50
	 * to indicate it is in workflow state.
	 *
	 * @param object
	 * @param status
	 * @param responsibleNodeName The name of the node which gets assigned responsibility for executing the job.
	 * @throws Exception if entry could not be created.
	 * @throws Exception if object state could not be updated.
	 * @throws IllegalArgumentException if object is null.
	 * @author Jens Peters
	 * @author Daniel M. de Oliveira
	 *
	 */
	String createJob( daweb3.Object object, status, responsibleNodeName) {
		if (object == null) throw new IllegalArgumentException ( "Object is not valid" )
		if (responsibleNodeName == null) throw new IllegalArgumentException("responsibleNodeName must not be null")
		object.object_state = 50

		log.debug "object.contractor.shortName: " + object.contractor.shortName
		log.debug "session.contractor.shortName: " + object.contractor.shortName
		
		def list = QueueEntry.findByObjAndStatus(object, status)
		if (list != null) throw new RuntimeException ("Bereits angefordert.");
		
		def job = new QueueEntry()
		job.status = status
		job.setObj(object);
		job.created = Math.round(new Date().getTime()/1000L)
		job.modified = Math.round(new Date().getTime()/1000L)
		
		job.setInitialNode(responsibleNodeName)
		
					
		def errorMsg = ""
		if( !object.save() ) {
			
			object.errors.each { errorMsg += it }
			throw new Exception(errorMsg)
		}
		errorMsg = ""
		if( !job.save()  ) {
			
			job.errors.each { errorMsg += it }
			throw new Exception(errorMsg)
		}
	}

}
