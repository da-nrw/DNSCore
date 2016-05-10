package pages;
import geb.Page
import geb.navigator.Navigator;

/**
 * 
 * @author jens Peters
 * Tests available Login Page
 *
 */
public class FormatMappingPage extends Page {
	
	static url = "/daweb3/formatMapping/map"
	static at = { title == "Format-Mapping List" }
	
	static content = {
		deleteAndLoadForm { $("#mapSnippet")}
		deleteAndLoadButton { $("input", id: "submit") }
		
		message {$(".message")}
	}
	
	 

}
