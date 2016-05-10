package daweb3

import geb.Browser
import geb.spock.GebSpec;
import groovy.ui.text.FindReplaceUtility.CloseAction;

import java.io.File;

import spock.lang.Stepwise;

import org.openqa.selenium.firefox.FirefoxDriver

import pages.*

import javax.annotation.Resource as Slow

/**
 * @author gbender
 *
 */
@Slow
@Stepwise
class FormatMappingControllerSpec extends GebSpec{
	
	File getReportDir() { new File("target/reports/geb") }
	
	def 'test Datei nicht vorhanden'() {
		given:
		to LoginWithParamsPage
		
		when:
		login("rods", "rods")
		
		then:
			to FormatMappingPage 	
//				when:
// 					assert withConfirm(true) { $("#mapSnippet input[type=submit]").click()} == 1
//					$("#mapSnippet input[type=submit]").click()
//				and:
//				   $(".message").text() == "Benutzerordner /ci/storage/IngestArea/rods/incoming oder Datei /ci/storage/IngestArea/rods/incoming/DROID_SignatureFile_20160503.xml existiert nicht!"
//				then:
//				  to FormatMapFileMissingPage
				
	}
}
