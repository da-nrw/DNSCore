package daweb3

import daweb3.User
import daweb3.UserRole
import daweb3.Role

import grails.util.Environment

class BootStrap {
	
	def springSecurityService

    def init = { servletContext ->
		
		if ((Environment.current ==  Environment.DEVELOPMENT) || (Environment.current == Environment.TEST)) {	
			println "BOOTSTRAPPED Environment " + Environment.current + " activated on CI - log in with rods/rods TEST/TEST"
		}
		
    }
	
    def destroy = {
    }
}
