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
 * Webcontroller for Authentication of contractors
 *  @Author Jens Peters
*/

import de.uzk.hki.da.login.IrodsLogin;
import de.uzk.hki.da.login.LoginFactory

class ContractorController {

	def authenticate = {
		Contractor contractor
		LoginFactory lf = Class.forName(grailsApplication.config.daweb3.loginManager.toString(),true,
			Thread.currentThread().contextClassLoader).newInstance(); 
		if (lf !=null && lf.login(params.login, params.password, grailsApplication)) {
			contractor = Contractor.findByShortName(params.login);
		} else flash.message = "Der Login schlug fehl! Bitte versuchen Sie es später erneut."
		if(contractor){
			session.contractor = contractor
			flash.message = "Hello ${contractor.shortName}!"
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
		LoginFactory lf = Class.forName(grailsApplication.config.daweb3.loginManager.toString(),true,
			Thread.currentThread().contextClassLoader).newInstance();
		if (lf !=null)
			lf.logout()
		redirect(uri: "/")
	}

	def index = {
		redirect(action:"authenticate")
	}
	
	def login = {
			
		
	}
	
}
