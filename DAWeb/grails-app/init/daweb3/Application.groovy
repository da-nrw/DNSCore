package daweb3

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration

class Application extends GrailsAutoConfiguration {
	
	static void main(String[] args) {
//        GrailsApp.run(Application, args)
		try
		{
			GrailsApp.run(Application, args)
		} catch (Throwable e) {
			print e.getStackTrace();
		}
    }
}