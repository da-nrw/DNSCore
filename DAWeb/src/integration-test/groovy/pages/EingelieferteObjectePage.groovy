package pages;

import geb.Page;

/**
 * 
 * @author Gaby Bender
 * Tests der  Seite Eingelieferte Objecte
 *
 */
public class EingelieferteObjectePage extends Page {
 
	static url = "/daweb3/object/list"
	
	static at = { title == "Eingelieferte Objekte (AIP)" }
	
	static content = {
		h2 { $("h2", text: "Eingelieferte Objekte (AIP)") }
		
	}
	
}