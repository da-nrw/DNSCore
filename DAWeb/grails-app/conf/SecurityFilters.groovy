import javax.servlet.SessionCookieConfig;

import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.connection.IRODSProtocolManager
import org.irods.jargon.core.connection.IRODSSession
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.UserAO
import org.irods.jargon.core.connection.IRODSSimpleProtocolManager
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSAccessObjectFactoryImpl;

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
				try {
					session.bauthuser = credentials[0]
					// TODO: wire this up in the spring context!
					IRODSProtocolManager irodsConnectionManager = IRODSSimpleProtocolManager.instance()
					IRODSAccount irodsAccount = new IRODSAccount(
						grailsApplication.config.irods.server,
						1247,
						credentials[0],
						credentials[1],
						"/" + grailsApplication.config.irods.zone +"/home/"+credentials[0],grailsApplication.config.irods.zone,
						grailsApplication.config.irods.default_resc
					)
					IRODSSession irodsSession = IRODSSession.instance(irodsConnectionManager);
					IRODSAccessObjectFactory irodsAccessObjectFactory = IRODSAccessObjectFactoryImpl.instance(irodsSession);

					def userAO = irodsAccessObjectFactory.getUserAO(irodsAccount)

					if (userAO!=null) {
						request.contractor = credentials[0]
					} else {
						response.addHeader("WWW-Authenticate", "Basic realm=\"DA-Web\"") 
						response.sendError(401, "Authorization required") 
						return false 
					}

				} catch (Exception e) {
					response.addHeader("WWW-Authenticate", "Basic realm=\"DA-Web\"") 
					response.sendError(401, "Authorization required") 
					return false 
				}
				
            }
		}
        
      
        
    }
    
}
