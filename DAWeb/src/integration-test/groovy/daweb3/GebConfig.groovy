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
//	System.setProperty('webdriver.phantomjs.bin', '/bin/phantomjs')
//	driver =  new PhantomJSDriver()
	System.setProperty('webdriver.firefox.bin', '/home/gabender/Programme/firefox/firefox');
	driver = new FirefoxDriver()
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
	
//driver = {
//	
//	// headless:
//	
//    def d = new PhantomJSDriver(new DesiredCapabilities())   
//	d.manage().window().setSize(new Dimension(1028, 768))  
//	d
//	
//	//LIVE running only with browser firefox 26 !!
//	//System.setProperty('webdriver.firefox.bin', '/home/jens/firefox/firefox')
//	//driver = new FirefoxDriver();
//} 
//
//baseNavigatorWaiting = true 
//
//atCheckWaiting = true 
//
//waiting {
//	timeout = 30
//	retryInterval = 1.0
//}