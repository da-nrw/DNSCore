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
 * tests  der Navigation
 */
@Slow
@Stepwise
class Navigation extends GebSpec {

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

	def 'test goToEntscheidungsuebersicht'() {
		given:
		to NavigationPage

		when:
		verticalMenu

		and:
		linkEntscheidungsuebersicht.click()

		then:
		at EntscheidungsuebersichtPage
	}

	def 'test goToEingelieferteObjecte'() {
		given:
		to NavigationPage

		when:
		verticalMenu

		and:
		linkEingelieferteObjecte.click()

		then:
		at EingelieferteObjectePage
	}

	def 'test goToVerarbeitungSipStarten'() {
		given:
		to NavigationPage

		when:
		verticalMenu

		and:
		linkVerarbeitungSipStarten.click()

		then:
		at VerarbeitungSipStartenPage
	}

	def 'test goToObjecteEntnehmen'() {
		given:
		to NavigationPage

		when:
		verticalMenu

		and:
		linkObjecteEntnehmen.click()

		then:
		at ObjecteEntnehmenPage
	}

	def 'test goToAnsteuerungExterneSysteme'() {
		given:
		to NavigationPage

		when:
		verticalMenu

		and:
		linkAnsteuerungExterneSysteme.click()

		then:
		at AnsteuerungExtSystemePage

	}

	def 'test goToKonfigurierteKonversionen'() {
		given:
		to NavigationPage

		when:
		verticalMenu

		and:
		linkKonfigurierteKonversionen.click()

		then:
		at KonfigurierteKonversionenPage

	}

	def 'test goToAbfragenVerarbeiten'() {
		given:
		to NavigationPage

		when:
		verticalMenu

		and:
		linkAbfragenVerarbeiten.click()

		then:
		at AbfragenVerarbeitenPage

	}

	def 'test goToSystemEventSteuerung'() {
		given:
		to NavigationPage

		when:
		verticalMenu

		and:
		linkSystemEventSteuerung.click()

		then:
		at SystemEventPage
	}
}

