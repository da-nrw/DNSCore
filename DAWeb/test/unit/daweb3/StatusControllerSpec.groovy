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
 * @author jpeters
 */
package daweb3

import grails.test.GrailsMock
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import groovy.mock.interceptor.MockFor
import spock.lang.Specification

@Mock([User,QueueEntry])
@TestFor(daweb3.StatusController)
class StatusControllerSpec extends Specification {
void "test failed if not auth"() { 
	when: controller.index()
	then: response.text.indexOf("Login failed")  

}

void "test passed if login"() {
	given:
	def cont = new User()
	cont.setShortName("TEST")
	controller.session.bauthuser = "TEST"
	when: controller.index()
	then: response.text.indexOf("not found")
}

/*void "search for objects"() {

	given: 
		
		controller.session.bauthuser = "TEST"
		def obj1 = new daweb3.Object()
		def cont = new Contractor()
		def pack = new daweb3.Package();
		def packages = []
		def qe = new QueueEntry()
		packages.add(pack);
		controller.params.urn = "123"
		cont.setShortName("TEST")
		obj1.packages = packages
		obj1.setContractor(cont)
		obj1.setObject_state(100)
		obj1.setUrn("123")
		
		QueueEntry.metaClass.getAllQueueEntriesForShortNameAndUrn = {
			return []
		}
		
	  
	when: controller.index()
	then: controller.response.text.contains "archived"
	
}*/

}
