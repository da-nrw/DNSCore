/*
  DA-NRW Software Suite | ContentBroker
  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
  Universität zu Köln, 2014 LVR InfoKom

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

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
//import grails.test.mixin.gorm.Domain
//import grails.test.mixin.hibernate.HibernateTestMixin
import spock.lang.Specification
import org.codehaus.groovy.grails.web.servlet.mvc.SynchronizerTokensHolder
/**
 * @author jpeters
 * TODO: will work with Grails 2.4.2 
 * */

@Mock([User,daweb3.Object])
@TestFor(daweb3.ObjectController)
//@TestMixin(HibernateTestMixin)
class ObjectControllerSpec extends Specification {
	def cont = new User(id:1)
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
			params.offset=0
			params.max=10
			params.search = null
			def retrieveData = { Integer offset, Integer maxRows ->
			if(offset==0) return [ obj1 ]
			else return []}	
			grailsApplication.config.localNode.userAreaRootPath = "test"
			session.contractor = cont
			def tokenHolder = SynchronizerTokensHolder.store(session)
			daweb3.Object.metaClass.createCriteria = {retrieveData}
		when: 		
			controller.list()
		then: controller.response.text.contains "123"
		
		
    }

	
	
}
