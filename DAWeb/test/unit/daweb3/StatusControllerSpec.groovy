package daweb3

import grails.test.GrailsMock
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import groovy.mock.interceptor.MockFor
import spock.lang.Specification

@Mock([Contractor,QueueEntry])
@TestFor(daweb3.StatusController)
class StatusControllerSpec extends Specification {
void "test failed if not auth"() { 
	when: controller.index()
	then: response.text.indexOf("Login failed")  

}

void "test passed if login"() {
	given:
	def cont = new Contractor()
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
