package pages;

import geb.Page;

/**
 * 
 * @author Gaby Bender
 * Tests der  Seite Verarbeitung starten
 */
public class VerarbeitungSipStartenPage extends Page {
 
	static url = "/daweb3/incoming/index"
	
	static at = { title ==~ "Verarbeitung SIP starten" }
	
	static content = {
		h2 { $("h2", text: "Verarbeitung für abgelieferte SIP starten") }
		
	}
	
}