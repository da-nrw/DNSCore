package daweb3

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(daweb3.StatusController)

class StatusControllerSpec extends Specification {
void "test hello"() { 
	when: controller.index()
	then: response.text.indexOf("Login failed") } 

}