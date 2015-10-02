package daweb3
import geb.spock.GebSpec;
import spock.lang.Stepwise;
import pages.*


/**
 * 
 * @author jens Peters
 * tests Login Page of Spring Security
 */

@Stepwise
class LoginAuthSpec extends GebSpec {

	File getReportDir() { new File("target/reports/geb") }

	def 'test invalid login'() {
	   given:
	   to LoginPage
	   when: 
	   
	   loginForm.with {
		   j_username = "admin"
		   j_password = "ioguffwf"
	   }
	   and:
	   loginButton.click()

	   then:
	   at LoginPage
	}
	
	def 'test user login'() {
	   given: 
	   to LoginPage

	   when: 
	   loginForm.with {
		   j_username = "TEST"
		   j_password = "TESTTEST"
	   }
	   
	   and:
	   loginButton.click()
	   then:
	   at WelcomePage
	   $().text().contains("Willkommen TEST")
	}
}

 