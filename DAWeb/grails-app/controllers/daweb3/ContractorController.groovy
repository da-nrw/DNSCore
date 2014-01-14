package daweb3

/*
 DA-NRW Software Suite | ContentBroker
 Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
 Universität zu Köln

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
/**
 * Webcontroller for Autentication of contractors
 *  @Author Jens Peters
*/
import org.irods.jargon.core.connection.IRODSAccount
import org.irods.jargon.core.connection.IRODSProtocolManager
import org.irods.jargon.core.connection.IRODSSession
import org.irods.jargon.core.pub.IRODSAccessObjectFactory
import org.irods.jargon.core.pub.UserAO
import org.irods.jargon.core.connection.IRODSSimpleProtocolManager
import org.irods.jargon.core.exception.JargonException;
import org.irods.jargon.core.pub.IRODSAccessObjectFactoryImpl;

class ContractorController {

	def authenticate = {
		Contractor contractor
		def userAuth = false
		UserAO userAO
		try {
			// TODO: wire this up in the spring context!
			IRODSProtocolManager irodsConnectionManager = IRODSSimpleProtocolManager.instance()
			if (grailsApplication.config.irods.server.toString()==null ||grailsApplication.config.irods.server.toString().equals(""))
			log.error("Server property is empty :" + grailsApplication.config.irods.server.toString() )
			log.debug("Server: " +  grailsApplication.config.irods.server.toString())
			IRODSAccount irodsAccount = new IRODSAccount(
				grailsApplication.config.irods.server.toString(),
				1247,
				params.login,
				params.password,
				"/" + grailsApplication.config.irods.zone +"/home/"+params.login,grailsApplication.config.irods.zone.toString(),
				grailsApplication.config.irods.default_resc.toString()
			)
			IRODSSession irodsSession = IRODSSession.instance(irodsConnectionManager);
			IRODSAccessObjectFactory irodsAccessObjectFactory = IRODSAccessObjectFactoryImpl.instance(irodsSession);

			userAO = irodsAccessObjectFactory.getUserAO(irodsAccount)

			if (userAO!=null) {
				userAuth = true;
				session.irodsSession = irodsSession
				session.userAO = userAO
				session.irodsAccount = irodsAccount
			}
			
		} catch (Exception e) {
		
			flash.message = "Der Login schlug fehl! Bitte versuchen Sie es später erneut."
			log.error("Fehler beim Login! " + e.printStackTrace())
			userAuth = false;	
			userAO=null;
			if (session.irodsSession!= null) session.irodsSession.closeSession()
			session.contractor = null
			session.irodsAccount = null;
			session.userAO = null;
			redirect(action:"login");
			return;
		}

		if (userAuth) {
			 contractor = Contractor.findByShortName(params.login);
		}

		//def user = User.findByLoginAndPassword(params.login, params.password.encodeAsMD5().encodeAsHex())
		if(contractor){
			session.contractor = contractor
			flash.message = "Hello ${contractor.shortName}!"
			// redirect(controller:"queueEntry", action:"list")
			redirect(uri: "/")
		}else{
			flash.message = "Sorry, ${params.login}. Not a known Contractor. Please try again."
			redirect(action:"login")
		}
		
	}

	def logout = {
		if(session.contractor!=null){
			flash.message = "Goodbye"
		}
		if (session.irodsSession!= null) session.irodsSession.closeSession()
		session.contractor = null
		session.irodsAccount = null;
		session.userAO = null;
		redirect(uri: "/")
	}

	def index = {
		redirect(action:"authenticate")
	}
	
	def login = {
			
		
	}
	
}
