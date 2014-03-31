package daweb3

import org.springframework.dao.DataIntegrityViolationException

class ConversionPoliciesController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [conversionPoliciesInstanceList: ConversionPolicies.list(params), conversionPoliciesInstanceTotal: ConversionPolicies.count()]
    }

    def create() {
		if (session.contractor.admin==1) {
        [conversionPoliciesInstance: new ConversionPolicies(params)]
		} else redirect(action: "list") 
	}

    def save() {
		if (session.contractor.admin==1) {
		 def conversionPoliciesInstance = new ConversionPolicies(params)
        if (!conversionPoliciesInstance.save(flush: true)) {
            render(view: "create", model: [conversionPoliciesInstance: conversionPoliciesInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'conversionPolicies.label', default: 'ConversionPolicies'), conversionPoliciesInstance.id])
		}
	    redirect(action: "show", id: conversionPoliciesInstance.id)
   
	 }

    def show(Long id) {
        def conversionPoliciesInstance = ConversionPolicies.get(id)
        if (!conversionPoliciesInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'conversionPolicies.label', default: 'ConversionPolicies'), id])
            redirect(action: "list")
            return
        } 

        [conversionPoliciesInstance: conversionPoliciesInstance]
    }

    def edit(Long id) {
        if (session.contractor.admin==1) {
			def conversionPoliciesInstance = ConversionPolicies.get(id)
        if (!conversionPoliciesInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'conversionPolicies.label', default: 'ConversionPolicies'), id])
            redirect(action: "list")
            return
        }

        [conversionPoliciesInstance: conversionPoliciesInstance]
        } else redirect(action: "list")
	}

    def update(Long id, Long version) {
		if (session.contractor.admin==1) {
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
		} else redirect(action: "list") 
	}

  	 /*def delete(Long id) {
        def conversionPoliciesInstance = ConversionPolicies.get(id)
        if (!conversionPoliciesInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'conversionPolicies.label', default: 'ConversionPolicies'), id])
            redirect(action: "list")
            return
        }

        try {
            conversionPoliciesInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'conversionPolicies.label', default: 'ConversionPolicies'), id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'conversionPolicies.label', default: 'ConversionPolicies'), id])
            redirect(action: "show", id: id)
        }
      } */
}
