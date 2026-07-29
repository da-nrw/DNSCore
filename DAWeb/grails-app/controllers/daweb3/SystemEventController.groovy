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


import static org.springframework.http.HttpStatus.*
import grails.plugin.springsecurity.annotation.Secured
import org.hibernate.criterion.CriteriaSpecification;
import grails.transaction.Transactional

@Transactional(readOnly = true)
class SystemEventController {
	
	def springSecurityService
	

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"] 
	
	static CharacterEncodingUtils  ceu = new CharacterEncodingUtils()
	
    def index(Integer max) {
		User user = springSecurityService.currentUser
		def admin = 0;
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		
		CbNode node = CbNode.findById(grailsApplication.config.getProperty('localNode.id'));
        params.max = Math.min(max ?: 10, 100)
		ceu.setEncoding(response)
        respond SystemEvent.findAllByUserAndNode(user,node), 
				model:[systemEventInstanceCount: SystemEvent.count(), 
						user: user, admin: admin] 
		
    }

    def show(SystemEvent systemEventInstance) {
		User user = springSecurityService.currentUser
		def admin = 0;
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		ceu.setEncoding(response)
		
        respond systemEventInstance, model:[user:user, admin:admin]
    }

    def create() {
		User user = springSecurityService.currentUser
		def admin = 0;
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		CbNode node = CbNode.findById(grailsApplication.config.getProperty('localNode.id'));
		def se = new SystemEvent(params)
		se.setNode(node)
		se.setUser(user)
		ceu.setEncoding(response)
        respond se, model:[user:user, admin: admin]
    }

    @Transactional
    def save(SystemEvent systemEventInstance) {
		User user = springSecurityService.currentUser
		def admin = 0;
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		CbNode node = CbNode.findById(grailsApplication.config.getProperty('localNode.id'));
		ceu.setEncoding(response)
		if (systemEventInstance.user.id !=  springSecurityService.currentUser.id) {
			notFound()
			return
		}
        if (systemEventInstance == null) {
            notFound()
            return
        }
        if (systemEventInstance.hasErrors()) {
            respond systemEventInstance.errors, view:'create', model:[user:user, admin: admin]
            return
        }
		if (!SystemEvent.findAllByUserAndNodeAndType(user,node,params.type).isEmpty()) {
			respond flash.message = 'Es gibt bereits einen SystemEvent diesen Typs!',
									 view:'create',  model:[user:user, admin: admin]
			return
		}
        systemEventInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'systemEventInstance.label', default: 'SystemEvent'), systemEventInstance.id])
                redirect systemEventInstance
            }
            '*' { respond systemEventInstance, [status: CREATED] , [user:user, admin: admin] }
        }
    }

    def edit(SystemEvent systemEventInstance) {
		User user = springSecurityService.currentUser
		def admin = 0;
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		ceu.setEncoding(response)
        respond systemEventInstance, model:[user:user, admin:admin]
    }

    @Transactional
    def update(SystemEvent systemEventInstance) {
		User user = springSecurityService.currentUser
		def admin = 0;
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		ceu.setEncoding(response)
        if (systemEventInstance == null) {
            notFound()
            return
        }
		if (systemEventInstance.user.id !=  springSecurityService.currentUser.id) {
			notFound()
			return
		}

        if (systemEventInstance.hasErrors()) {
            respond systemEventInstance.errors, view:'edit'
            return
        }

        systemEventInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'SystemEvent.label', default: 'SystemEvent'), systemEventInstance.id])
                redirect systemEventInstance
            }
            '*'{ respond systemEventInstance, [status: OK] }
        }
    }
	
	/**
	 * cancel: Abbrechen-Button
	 * @return
	 */
	def cancel() {
		redirect(action: "show",  id: params.id)
	}
	
	def cancelCreate() {
		redirect(action: "index",  id: params.id)
	}
	
    @Transactional
    def delete(SystemEvent systemEventInstance) {
		def admin = 0;
		if (springSecurityService.currentUser.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		if (systemEventInstance == null) {
			notFound()
			return
		}
		
		if (systemEventInstance.user.id !=  springSecurityService.currentUser.id) {
			notFound()
			return
		}
        

        systemEventInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'SystemEvent.label', default: 'SystemEvent'), systemEventInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'systemEventInstance.label', default: 'SystemEvent'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
