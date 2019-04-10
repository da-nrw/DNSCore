package pages;

import geb.Page;

/**
 * 
 * @author Gaby Bender
 * Tests der  Seite Bearbeitungsübersicht
 *
 */
public class BearbeitungsuebersichtPage extends Page {
 
	static url = "/daweb3/queueEntry/list"
	
	static at = { title ==~ /Bearbeitungsübersicht|queueEntry/ }
	
	static content = {
		bearbeitungsform { $("form") }
		
		//starten/stoppen
		startenButton { $("input", id: "starter")}
		startenInactiv{ $("input", id: "starter", disabled: "") }
		stoppenButton { $("input", id: "stopper")}
		stoppenInactiv{ $("input", id : "stopper", disabled: "") }
		
		// Filter
		buttonFilter { $("button", text: "Filter") }
		searchForm { $("form" , id: "searchForm") }
		searchStatus { $("input", id: "search.status") }
		buttonSearch { $("input", id: "submit", value: "Filter anwenden") }
		buttonSearchLoeschen { $("input", id: "loeschen") }
 		buttonFilterStatus { $("button", class: "accordion") }
		 
		// Statuscodes
		buttonStatusCode { $("button", text: "Hinweise zu den Statuscodes:") }
		displayBlock { $("div", style: "display: block;") }
		
		// Detailansicht
		linkUrn { $("a", href:"/daweb3/queueEntry/show/6") } 
		detailview { $("form") }//("div", id: "show-queueEntry") }
		cancel { $("input", class : "cancel", type: "submit") }
		 
	}
	
}