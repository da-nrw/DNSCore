package daweb3

import geb.spock.GebSpec;
import geb.Page; 
import spock.lang.Stepwise;
import pages.*
import javax.annotation.Resource as Slow

/**
 * 
 * @author jens Peters
 * tests Login Page of Spring Security
 */
@Slow
@Stepwise
class LoginAuthSpec extends GebSpec {

	File getReportDir() { 
		new File("target/reports/geb")
	 }

	def 'test invalid login'() {
	   given:
	   to  LoginPage
	   
	   when: 
	   loginForm.with {
		   username = "admin"
		   password = "ioguffwf"
//		   j_username = "admin"
//		   j_password = "ioguffwf"
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
		   username = "TEST"
		   password = "TEST"
	   }
	   
	   and:
	   loginButton.click()
	   then:
	   at WelcomePage
	   $().text().contains("Willkommen TEST")
	}

}

 