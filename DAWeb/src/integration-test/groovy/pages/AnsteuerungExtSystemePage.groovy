package pages;

import geb.Page;

/**
 * 
 * @author Gaby Bender
 * Tests der  Seite Verarbeitung starten
 */
public class AnsteuerungExtSystemePage extends Page {
 
	static url = "/daweb3/info/index"
	
	static at = { title ==~ "Hinweise zur Ansteuerung über externe Systeme" }
	
	static content = {
		h2 { $("h2", text: "Hinweise zur Ansteuerung über externe Systeme (REST)") }
		
	}
	
}