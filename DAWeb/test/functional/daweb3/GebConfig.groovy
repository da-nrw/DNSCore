/*
	This is the Geb configuration file.

	See: http://www.gebish.org/manual/current/configuration.html
*/


import org.openqa.selenium.Dimension
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxProfile
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.remote.DesiredCapabilities

driver = {
	
	// headless:
	
    def d = new PhantomJSDriver(new DesiredCapabilities())   
	d.manage().window().setSize(new Dimension(1028, 768))  
	d
	
	//LIVE running browser firefox 26 !!
	//System.setProperty('webdriver.firefox.bin', '/home/jens/firefox/firefox')
	//driver = new FirefoxDriver();
} 

baseNavigatorWaiting = true 

atCheckWaiting = true 

waiting {
	timeout = 30
	retryInterval = 1.0
}