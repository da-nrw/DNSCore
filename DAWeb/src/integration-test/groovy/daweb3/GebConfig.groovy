/*
	This is the Geb configuration file.

	See: http://www.gebish.org/manual/current/configuration.html
*/


import java.awt.Dimension

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.remote.DesiredCapabilities

// default driver ...
driver = { 

	//System.setProperty('webdriver.firefox.bin', '/usr/bin/firefox');
		//driver = new FirefoxDriver()
	System.setProperty('webdriver.chrome.bin', '/usr/bin/google-chrome');
	System.setProperty('webdriver.chrome.driver', '/opt/google/chromedriver-linux64/chromedriver');
	driver = new ChromeDriver() 
}

environments {
	
		htmlUnit {
			driver = { new HtmlUnitDriver() }
		}
	
		chrome {
			driver = { new ChromeDriver() }
		}
	
		firefox {

		     driver = { new FirefoxDriver() }
		}
//	
//		phantomJs {
//			driver = { new PhantomJSDriver() }
//		}
		
	}
	
	reportsDir = new File('target/geb-reports')
	
	baseUrl = "http://localhost:8080"
	baseNavigatorWaiting = true
	
	atCheckWaiting = true
	
	waiting {
		timeout = 30
		retryInterval = 1.0
	}
