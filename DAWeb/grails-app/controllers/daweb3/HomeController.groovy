package daweb3

import grails.plugin.springsecurity.SpringSecurityService

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
 * Webcontroller to start the Webapp DA-Web
 * @author Jens Peters
 *
 */
class HomeController {

	
	def springSecurityService
	def index() {
		def username = springSecurityService.currentUser
		def admin = 0;
		User user = User.findByUsername(username)
		
		if (user.authorities.any { it.authority == "ROLE_PSADMIN" 
							}) {
			admin = 1;
		}
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN"
							}) {
			admin = 1;
		}
		
		render(view:"/index", model:[admin:admin,user:username])
	}

}
