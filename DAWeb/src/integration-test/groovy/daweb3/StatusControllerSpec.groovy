package daweb3
import geb.spock.GebSpec;
import spock.lang.Stepwise;
import spock.lang.Shared
import wslite.rest.*
import wslite.http.auth.*
import geb.spock.GebReportingSpec;
import java.net.Proxy
 /** 
 * @author jens Peters
 * tests status controller
 */

class StatusControllerSpec extends GebReportingSpec  {
	
	
	def 'test empty params'() {
	   //when:
	   
		// currently not working behind our auth Proxy
		// due to https://github.com/grails/grails-maven/pull/62
		
	  // def client = new RESTClient("http://localhost:8090/daweb3")
	  // client.authorization = new HTTPBasicAuthorization("TEST", "TEST")
	   
	  // def response = client.get(path:'/status/index')
	   
	   
	   //then:
	  // response.status == 200
	  // response.json.status == "not found"
	   
	   }
	  
	}

 