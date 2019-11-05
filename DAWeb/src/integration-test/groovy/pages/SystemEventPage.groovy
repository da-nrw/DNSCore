package pages;

import geb.Page;

/**
 * 
 * @author Gaby Bender
 * Tests der  Seite Verarbeitung starten
 */
public class SystemEventPage extends Page {
 
	static url = "/daweb3/systemEvent/index"
	
	static at = { title ==~ "System-Eventsteuerung List" }
	
	static content = {
		h2 { $("h2", text: "System-Eventsteuerung Liste") }
		
	}
	
}