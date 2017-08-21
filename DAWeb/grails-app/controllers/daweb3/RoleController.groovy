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
import grails.transaction.Transactional

@Transactional(readOnly = true)
class RoleController {
	
	def springSecurityService
	
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
	static CharacterEncodingUtils ceu = new CharacterEncodingUtils()
	
    def index(Integer max) {
		def user = springSecurityService.currentUser
		def admin = 0;
		
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
        params.max = Math.min(max ?: 10, 100)
		ceu.setEncoding(response)
        respond Role.list(params), model:[roleInstanceCount: Role.count(), user: user, admin: admin]
    }

    def show(Role roleInstance) {
		def user = springSecurityService.currentUser
		def admin = 0;
		
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		ceu.setEncoding(response)
        respond roleInstance, model: [user:user, admin: admin]
    }

    def create() {
		def user = springSecurityService.currentUser
		def admin = 0;
		
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		ceu.setEncoding(response)
        respond new Role(params), model:[user: user, admin: admin] 
    }
	
	def cancel() {
		redirect(action: "show",  id: params.id)
	}

    @Transactional
    def save(Role roleInstance) {
		
		ceu.setEncoding(response)
        if (roleInstance == null) {
            notFound()
            return
        }

        if (roleInstance.hasErrors()) {
            respond roleInstance.errors, view:'create'
            return
        }

        roleInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'roleInstance.label', default: 'Role'), roleInstance.id])
                redirect roleInstance
            }
            '*' { respond roleInstance, [status: CREATED] }
        }
    }

    def edit(Role roleInstance) {
		def user = springSecurityService.currentUser
		def admin = 0;
		
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		ceu.setEncoding(response)
        respond roleInstance, model: [user:user, admin:admin]
    }

    @Transactional
    def update(Role roleInstance) {
		ceu.setEncoding(response)
        if (roleInstance == null) {
            notFound()
            return
        }

        if (roleInstance.hasErrors()) {
            respond roleInstance.errors, view:'edit'
            return
        }

        roleInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Role.label', default: 'Role'), roleInstance.id])
                redirect roleInstance
            }
            '*'{ respond roleInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Role roleInstance) {

        if (roleInstance == null) {
            notFound()
            return
        }

        roleInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Role.label', default: 'Role'), roleInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'roleInstance.label', default: 'Role'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
