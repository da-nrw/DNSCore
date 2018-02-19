package pages

/**
 * @author jens Peters
 * Tests Google internet access
 */

import geb.Page

class GoogleHomePage extends Page {

	static url = "http://www.google.de"
	
	static at = { title == "Google" }
	
	static content = {
		searchBox { $('input[type="text"]', name:'q') }
        searchButton { $('button', text:contains('Google Suche')) }
    }
}
