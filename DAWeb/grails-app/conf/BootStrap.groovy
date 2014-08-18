import daweb3.User
import daweb3.Role
import daweb3.UserRole
import grails.util.Environment
class BootStrap {

    def init = { servletContext ->
		
		if (Environment.current == Environment.DEVELOPMENT) {	
			println "BOOTSTRAPPED DEV Environment activated"
			def testuser = User.findByUsername('rods')
			testuser.setPassword("rods")
		}
	}
    def destroy = {
    }
}
