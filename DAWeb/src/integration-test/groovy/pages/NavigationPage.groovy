package pages;

import geb.Page;

/**
 * 
 * @author Gaby Bender
 * Tests Navigation
 *
 */
public class NavigationPage extends Page {
 
	static url = "/daweb3/"
	
	static at = { title ==~ "Willkommen bei der DA-NRW Weboberfläche" }
	
	static content = {
		verticalMenu { $("div", id: "vertical-menu") }
		linkEntscheidungsuebersicht { $("a",text: "Entscheidungsübersicht") }
		linkEingelieferteObjecte { $("a",text: "Eingelieferte Objekte (AIP)") }
		linkVerarbeitungSipStarten { $("a",text: "Verarbeitung für abgelieferte SIP starten") }
		linkObjecteEntnehmen { $("a",text: "Angeforderte Objekte (DIP)") }
		linkAnsteuerungExterneSysteme { $("a",text: "Hinweise zur Ansteuerung über externe Systeme") }
		linkKonfigurierteKonversionen { $("a",text: "Konfigurierte Konversionen") }
		linkAbfragenVerarbeiten { $("a",text: "Abfragen verarbeiten") }
		linkSystemEventSteuerung { $("a",text: "System-Eventsteuerung") }
		 
	}
	
}