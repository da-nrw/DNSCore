import daweb3.User
import daweb3.Role
import daweb3.UserRole
import grails.util.Environment
class BootStrap {

    def init = { servletContext ->
		
		if ((Environment.current ==  Environment.DEVELOPMENT) || (Environment.current == Environment.TEST)) {	
			println "BOOTSTRAPPED Environment activated on CI - log in with rods/rods TEST/TEST"
			//User testuser = User.findByUsername('rods')
			//testuser.setPassword("rods")
			//if (!testuser.save(flush: true)) {
			//	println "Error updating Admin DEV User!"
			//	testuser.errors.allErrors.each {
			//		println it
			//	}
			//}
			//testuser = User.findByUsername('TEST')
			//testuser.setPassword("TEST")
			//if (!testuser.save(flush: true)) {
			//	println "Error updating TEST DEV User!"
			//	testuser.errors.allErrors.each {
			//		println it
			//	}
			//}

		}
	}
    def destroy = {
    }
}
