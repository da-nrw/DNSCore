package pages

import geb.Page

/**
 * 
 * @author jens Peters
 *
 */

class WelcomePage extends Page {

	static url = "/daweb3/"
	
	static at = { title ==~ "Willkommen bei der DA-NRW Weboberfläche" }

	static content = {
		verticalMenu { $("li") }
		linkBearbeitungsuebersicht { $("a",text: 'Bearbeitungsübersicht') }
		
		}
	}
