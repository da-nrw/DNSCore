package daweb3

import geb.spock.GebSpec;
import geb.Page;
import spock.lang.Stepwise;
import pages.*

import java.awt.Menu

import javax.annotation.Resource as Slow

/**
 * 
 * @author Gaby Bender
 * tests Seite Bearbeitungsübersicht
 */
@Slow
@Stepwise
class Bearbeitungsuebersicht extends GebSpec {

	File getReportDir() {
		new File("target/reports/geb")
	}

	def 'test goToBearbeitungsuebersicht'() {
		given:
		to LoginPage

		when:
		loginForm.with {
			username = "TEST"
			password = "TEST"
		}

		and:
			loginButton.click()
		then:
			at WelcomePage


		when:
			verticalMenu
		and:
			linkBearbeitungsuebersicht.click()
		then:
			at BearbeitungsuebersichtPage

	}

	def 'test reloadStarten'() {
		given:
			to BearbeitungsuebersichtPage

		when:
			bearbeitungsform
		and:
			startenButton.first()
		and:
			startenButton.click()
		then:
			startenInactiv
	}

	def 'test reloadStoppen'() {
		given:
		to BearbeitungsuebersichtPage
		when:
			bearbeitungsform
		and:
			startenButton.first()
		and:
			stoppenButton.click()
		then:
			stoppenInactiv
	}
	
	def 'test Filter' () {
		given:
		to BearbeitungsuebersichtPage
		when:
			bearbeitungsform
		  and:
		   buttonFilter.click()
		  then:
		  searchForm.with {
			  searchStatus = 111
		   }
		   when:
		   	buttonSearch.click()
			 then:
				buttonFilterStatus
	}
	
	def 'test Filter loeschen' () {
		given:
		to BearbeitungsuebersichtPage
		when:
			bearbeitungsform
		  and:
		   buttonFilter.click()
		  then:
		  searchForm
		   when:
			   buttonSearchLoeschen.click()
			   then:
			   buttonFilter 
	}
	
	def 'test Statuscodes' () {
		given:
		to BearbeitungsuebersichtPage
		when:
			bearbeitungsform
		  and:
		  	buttonStatusCode.click()
		  then:
		  	displayBlock
	}
	
	def 'test show Details' () {
		to BearbeitungsuebersichtPage
		when:
			bearbeitungsform
		  and:
		  	linkUrn.click()
		  then:
		    detailview
			when:
			 cancel.click()
			 then:
			 bearbeitungsform
	}
}

