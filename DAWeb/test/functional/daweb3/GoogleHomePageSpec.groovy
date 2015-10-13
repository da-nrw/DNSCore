package daweb3
import geb.spock.GebReportingSpec;
import spock.lang.Stepwise;
import pages.*

@Stepwise
class GoogleHomePageSpec extends GebReportingSpec {
	
	def 'test Google is up'() {
		when:
			to GoogleHomePage
		then:
			at GoogleHomePage
	}
}