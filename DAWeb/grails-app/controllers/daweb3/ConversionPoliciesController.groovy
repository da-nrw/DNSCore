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

/**
 * @author jpeters
 */
import grails.plugin.springsecurity.annotation.Secured
import org.springframework.dao.DataIntegrityViolationException

class ConversionPoliciesController {

	
	def springSecurityService
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 30, 100)
        [conversionPoliciesInstanceList: ConversionPolicies.list(params), conversionPoliciesInstanceTotal: ConversionPolicies.count()]
    }

	@Secured(['ROLE_PSADMIN'])
    def create() {
        [conversionPoliciesInstance: new ConversionPolicies(params)]
	}

	@Secured(['ROLE_PSADMIN'])
    def save() {
		 def conversionPoliciesInstance = new ConversionPolicies(params)
        if (!conversionPoliciesInstance.save(flush: true)) {
            render(view: "create", model: [conversionPoliciesInstance: conversionPoliciesInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'conversionPolicies.label', default: 'ConversionPolicies'), conversionPoliciesInstance.id])
	 }

    def show(Long id) {
		def user = springSecurityService.currentUser
		def admin = 0;
		if (user.authorities.any { it.authority == "ROLE_PSADMIN"
		}) {
		admin = 1;
			}
        def conversionPoliciesInstance = ConversionPolicies.get(id)
        if (!conversionPoliciesInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'conversionPolicies.label', default: 'ConversionPolicies'), id])
            redirect(action: "list")
            return
        } 

        [conversionPoliciesInstance: conversionPoliciesInstance, admin: admin]
    }
	@Secured(['ROLE_PSADMIN'])
    def edit(Long id) {
      
			def conversionPoliciesInstance = ConversionPolicies.get(id)
        if (!conversionPoliciesInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'conversionPolicies.label', default: 'ConversionPolicies'), id])
            redirect(action: "list")
            return
        }

        [conversionPoliciesInstance: conversionPoliciesInstance]
       } 

	@Secured(['ROLE_PSADMIN'])
    def update(Long id, Long version) {
        def conversionPoliciesInstance = ConversionPolicies.get(id)
        if (!conversionPoliciesInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'conversionPolicies.label', default: 'ConversionPolicies'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (conversionPoliciesInstance.version > version) {
                conversionPoliciesInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'conversionPolicies.label', default: 'ConversionPolicies')] as Object[],
                          "Another user has updated this ConversionPolicies while you were editing")
                render(view: "edit", model: [conversionPoliciesInstance: conversionPoliciesInstance])
                return
            }
        }

        conversionPoliciesInstance.properties = params

        if (!conversionPoliciesInstance.save(flush: true)) {
            render(view: "edit", model: [conversionPoliciesInstance: conversionPoliciesInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'conversionPolicies.label', default: 'ConversionPolicies'), conversionPoliciesInstance.id])
        redirect(action: "show", id: conversionPoliciesInstance.id)
	}
}
