package daweb3
/*
 DA-NRW Software Suite | ContentBroker
 Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
 Universität zu Köln, 2014 LVRInfoKom

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


import static org.springframework.http.HttpStatus.*

import javax.management.relation.RoleList

import grails.transaction.Transactional

@Transactional(readOnly = true)
class UserController {
	
	def springSecurityService
    
	static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
	static CharacterEncodingUtils  ceu = new CharacterEncodingUtils()
	
    def index(Integer max) {
		def user = springSecurityService.currentUser
		def admin = 0;
					
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
        params.max = Math.min(max ?: 10, 100)
		ceu.setEncoding(response)
        respond User.list(params), model:[userInstanceCount: User.count(), user:user, admin:admin]
		
    }

    def show(User userInstance) {
		def user = springSecurityService.currentUser
		def admin = 0;
		
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		ceu.setEncoding(response)
        respond userInstance, model:[ user:user, admin:admin]
    }

    def create() {
		def user = springSecurityService.currentUser
		def admin = 0;
		
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		
		ceu.setEncoding(response)
        respond new User(params),  model:[ user:user, admin:admin]
    }

    @Transactional
    def save(User userInstance) {
		ceu.setEncoding(response)
        if (userInstance == null) {
            notFound()
            return
        }
        if (userInstance.hasErrors()) {
            respond userInstance.errors, view:'create'
            return
        }

        userInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'userInstance.label', default: 'User'), userInstance.id])
                redirect userInstance
            }
            '*' { respond userInstance, [status: CREATED] }
        }
    }

    def edit(User userInstance) {
		def user = springSecurityService.currentUser
		def admin = 0;
		
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		
		ceu.setEncoding(response)
        respond userInstance, model:[ user:user, admin:admin ]
    }
	
	
    @Transactional
    def update(User userInstance) {
		
		ceu.setEncoding(response)
        if (userInstance == null) {
            notFound()
            return
        }

        if (userInstance.hasErrors()) {
            respond userInstance.errors, view:'edit'
            return
        }

        userInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'User.label', default: 'User'), userInstance.id])
                redirect userInstance
            }
            '*'{ respond userInstance, [status: OK] }
        }
    }

	def cancel() {
		redirect(action: "show",  id: params.id)
	}
	
    @Transactional
    def delete(User userInstance) {

        if (userInstance == null) {
            notFound()
            return
        }

        userInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'User.label', default: 'User'), userInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }
	
    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'userInstance.label', default: 'User'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
	
	/*
	 * 
	 * DANRW-1568: zu einem Contractor soll es mehrere Benutzer geben. Nun soll jeder Benutzer
	 * 			   seine Benutzerdaten selber pflegen können 
	 */
	
	def indexUser() {
		def user = springSecurityService.currentUser;
		def admin = 0;
		def userInstance;
		userInstance = User.find("from User as c where c.username=:usn", [usn: user.getUsername()])
		render (view: 'indexUser',  model: [user:user.username, admin:admin, userInstance:userInstance]);
		
	}
	
	def editUser(User userInstance) {
		def user = springSecurityService.currentUser
		def admin = 0;
		
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		ceu.setEncoding(response)
		respond userInstance, model:[ user:user.username, admin:admin ]
	}

	def cancelUser() {
		redirect(action: "indexUser", id: params.id)
	}
	
	@Transactional
	def updateUser(User userInstance) {
		ceu.setEncoding(response)
		if (userInstance == null) {
			notFound()
			return
		}
		
		if (userInstance.hasErrors()) {
			respond userInstance.errors, view:'editUser'
			return
		}
		
		userInstance.save flush:true
		
		request.withFormat {
			form {
				flash.message = message(code: 'default.updated.message', args: [message(code: 'User.label', default: 'User'), userInstance.id])
				redirect (params: [User: userInstance], action:"indexUser")
			}
			
			'*User'{ respond userInstance, [status: OK], view: indexUser }
		}
	}
}
