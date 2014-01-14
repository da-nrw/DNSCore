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
import org.codehaus.groovy.grails.commons.ConfigurationHolder

class QueueUtils {

	/**
	 * @param object valid instance of an object
	 * throws Exception if entry could not be created
	 */
	String createQueueEntryForObject(object, status, optionalInitialNode) {
		if (object == null) throw new RuntimeException ( "Object is not valid" )
		def entry = new QueueEntry()
		entry.status = status
		entry.setObj(object);
		entry.created = Math.round(new Date().getTime()/1000L)
		entry.modified = Math.round(new Date().getTime()/1000L)
		if (optionalInitialNode==null) {
			entry.setInitialNode(ConfigurationHolder.config.irods.server)
		} else entry.setInitialNode(optionalInitialNode)
		def errorMsg = ""
		if( !entry.save() ) {
			entry.errors.each { errorMsg += it }
			throw new Exception(errorMsg)
		}
	}

}
