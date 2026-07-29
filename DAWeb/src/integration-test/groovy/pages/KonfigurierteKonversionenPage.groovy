package pages;

import geb.Page;

/**
 * 
 * @author Gaby Bender
 * Tests der  Seite Verarbeitung starten
 */
public class KonfigurierteKonversionenPage extends Page {
 
	static url = "/daweb3/conversionPolicies/list"
	
	static at = { title ==~ "Konfigurierte Konversionen List" }
	
	static content = {
		h2 { $("h2", text: "Konfigurierte Konversionen Liste") }
		
	}
	
}