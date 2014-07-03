package daweb3

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */

@Mock([Contractor,daweb3.Object])
@TestFor(daweb3.ObjectController)
@TestMixin(GrailsUnitTestMixin)
class ObjectControllerSpec extends Specification {
	def cont = new Contractor(id:1)
	def obj1 = new daweb3.Object(id:1)
	def pack = new daweb3.Package(id:1);
	def results = []
	
    def setup() {
		
		def packages = []
		packages.add(pack);
		cont.setShortName("TEST")
		cont.setAdmin(0)
		cont.save(flush:true)
		obj1.packages = packages
		obj1.setContractor(cont)
		obj1.setObject_state(100)
		obj1.setUrn("123")
		obj1.save(flush:true)
	}

    def cleanup() {
    }
	
    void "test listing objects"() {
		
		given:
		assertTrue cont.validate()
		
		session.contractor = cont

		
		
		params.offset=0
		params.max=10
		params.search = null
		def retrieveData = { Integer offset, Integer maxRows ->
			if(offset==0) return [ obj1 ]
			else return []}
		
		grailsApplication.config.localNode.userAreaRootPath = "test"
		daweb3.Object.metaClass.createCriteria = {retrieveData}

		when: controller.list()
		then: controller.response.text.contains "123"
		
		
    }

	
	
}
