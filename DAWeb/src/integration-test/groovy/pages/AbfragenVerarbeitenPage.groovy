package pages;

import geb.Page;

/**
 * 
 * @author Gaby Bender
 * Tests der  Seite Verarbeitung starten
 */
public class AbfragenVerarbeitenPage extends Page {
 
	static url = "/daweb3/incoming/index"
	
	static at = { title ==~ "Abfragen verarbeiten" }
	
	static content = {
		h2 { $("h2", text: "Bericht hochladen") }
		
	}
	
}