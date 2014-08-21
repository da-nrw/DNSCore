package daweb3

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
