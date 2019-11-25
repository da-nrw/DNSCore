package pages;

import geb.Page;

/**
 * 
 * @author Gaby Bender
 * Tests der  Seite Objekte entnehmen
 */
public class ObjecteEntnehmenPage extends Page {
 
	static url = "/daweb3/outgoing/index"
	
	static at = { title == "Objekte entnehmen (DIP)" }
	
	static content = {
		h2 { $("h2", text: "Objekte entnehmen (DIP) ") }
		
	}
	
}