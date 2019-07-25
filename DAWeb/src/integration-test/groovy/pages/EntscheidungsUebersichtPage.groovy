package pages;

import geb.Page;

/**
 * 
 * @author Gaby Bender
 * Tests der  Seite Entscheidungsuebersicht
 *
 */
public class EntscheidungsuebersichtPage extends Page {
 
	static url = "/daweb3/queueEntry/listRequests"
	
	static at = { title ==~ "Entscheidungsübersicht" }
	
	static content = {
		h2 { $("h2", text: "Entscheidungsübersicht") }
		
	}
	
}