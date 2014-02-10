import javax.servlet.SessionCookieConfig;

import de.uzk.hki.da.login.LoginFactory;

class SecurityFilters {

    def filters = {
        
    	loginCheck(controller: 'status', invert: true) {
			before = {
				if (!session.contractor && actionName != "login" && actionName != "authenticate") {
					redirect(controller: "contractor", action: "login")
					return false
				}
			}
    	}
      
		basicAuthCheck(controller: 'status') {
            before = {				
				def auth = request.getHeader('Authorization') 
				if (!auth) { 
					 response.addHeader("WWW-Authenticate", "Basic realm=\"DA-Web\"") 
					 response.sendError(401, "Authorization required") 
					 return false 
				}
				
			def credentials = new String(new sun.misc.BASE64Decoder().decodeBuffer(auth - 'Basic ')).split(':') 		
			if (credentials.size()!=2) {
				response.addHeader("WWW-Authenticate", "Basic realm=\"DA-Web\"")
				response.sendError(401, "Authorization required")
				return false;
			}	
			session.bauthuser = credentials[0]		
			if (grailsApplication.config.daweb3.loginManager==null) {
					response.sendError(500, "Login Manager not found")
			}
			LoginFactory lf = Class.forName(grailsApplication.config.daweb3.loginManager.toString(),true,
			Thread.currentThread().contextClassLoader).newInstance();
			if (lf.login(credentials[0], credentials[1], grailsApplication)) {
						return true;
				} else {
					response.addHeader("WWW-Authenticate", "Basic realm=\"DA-Web\"") 
					response.sendError(401, "Authorization required") 
					return false 
				}
				
				return false;
			}
		}
        
		basicAuthCheck(controller: 'automatedRetrieval') {
			before = {	
				def auth = request.getHeader('Authorization')
				if (!auth) {
					 response.addHeader("WWW-Authenticate", "Basic realm=\"DA-Web\"")
					 response.sendError(401, "Authorization required")
					 return false
				}
			def credentials = new String(new sun.misc.BASE64Decoder().decodeBuffer(auth - 'Basic ')).split(':')
			if (credentials.size()!=2) {
				response.addHeader("WWW-Authenticate", "Basic realm=\"DA-Web\"")
				response.sendError(401, "Authorization required")
				return false;
			}
			if (grailsApplication.config.daweb3.loginManager==null) {
				response.sendError(500, "Login Manager not found")
			}
			LoginFactory lf = Class.forName(grailsApplication.config.daweb3.loginManager.toString(),true,
			Thread.currentThread().contextClassLoader).newInstance();
			if (lf.login(credentials[0], credentials[1], grailsApplication)) {
					return true;
				} else {
					response.addHeader("WWW-Authenticate", "Basic realm=\"DA-Web\"")
					response.sendError(401, "Authorization required")
					return false
				}				
			return false;
				
			}
		}

      
        
    }
    
}
