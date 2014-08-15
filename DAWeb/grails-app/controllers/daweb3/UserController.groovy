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

import grails.plugin.springsecurity.annotation.Secured;
import grails.transaction.Transactional;
import de.uzk.hki.da.login.IrodsLogin;
import de.uzk.hki.da.login.LoginFactory

class UserController {

	/*def authenticate() {
		User contractor
		/*LoginFactory lf = Class.forName(grailsApplication.config.daweb3.loginManager.toString(),true,
			Thread.currentThread().contextClassLoader).newInstance(); 
		if (lf !=null && lf.login(params.login, params.password, grailsApplication)) {
			*/
		//	contractor = User.findByShortName(params.login);
			//session.contractor = contractor
		/*
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
*/

	def logout = {
		if (session!=null) session.invalidate()
		flash.message = "Goodbye"
	}

	def index = {
		redirect(action:"authenticate")
	}
	
	@Secured(['ROLE_CONTRACTOR'])
	def login() {
			
		redirect(uri: "/")
	}
	
	def show(User usersInstance) {
		respond usersInstance
	}

	def create() {
		respond new User(params)
	}

	@Transactional
	def save(User usersInstance) {
		if (usersInstance == null) {
			notFound()
			return
		}

		if (usersInstance.hasErrors()) {
			respond usersInstance.errors, view:'create'
			return
		}

		usersInstance.save flush:true

		request.withFormat {
			form {
				flash.message = message(code: 'default.created.message', args: [message(code: 'usersInstance.label', default: 'Users'), usersInstance.id])
				redirect usersInstance
			}
			'*' { respond usersInstance, [status: CREATED] }
		}
	}

	def edit(User usersInstance) {
		respond usersInstance
	}

	@Transactional
	def update(User usersInstance) {
		if (usersInstance == null) {
			notFound()
			return
		}

		if (usersInstance.hasErrors()) {
			respond usersInstance.errors, view:'edit'
			return
		}

		usersInstance.save flush:true

		request.withFormat {
			form {
				flash.message = message(code: 'default.updated.message', args: [message(code: 'Users.label', default: 'Users'), usersInstance.id])
				redirect usersInstance
			}
			'*'{ respond usersInstance, [status: OK] }
		}
	}

	@Transactional
	def delete(User usersInstance) {

		if (usersInstance == null) {
			notFound()
			return
		}

		usersInstance.delete flush:true

		request.withFormat {
			form {
				flash.message = message(code: 'default.deleted.message', args: [message(code: 'Users.label', default: 'Users'), usersInstance.id])
				redirect action:"index", method:"GET"
			}
			'*'{ render status: NO_CONTENT }
		}
	}

	protected void notFound() {
		request.withFormat {
			form {
				flash.message = message(code: 'default.not.found.message', args: [message(code: 'usersInstance.label', default: 'Users'), params.id])
				redirect action: "index", method: "GET"
			}
			'*'{ render status: NOT_FOUND }
		}
	}
	
}
