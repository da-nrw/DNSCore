package daweb3



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class SystemEventController {
	
	def springSecurityService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
	
    def index(Integer max) {
	
		User user = springSecurityService.currentUser
		CbNode node = CbNode.findById(grailsApplication.config.localNode.id);
        params.max = Math.min(max ?: 10, 100)
        respond SystemEvent.findAllByUserAndNode(user,node), model:[systemEventInstanceCount: SystemEvent.count()]
    }

    def show(SystemEvent systemEventInstance) {
        respond systemEventInstance
    }

    def create() {
		User user = springSecurityService.currentUser
		CbNode node = CbNode.findById(grailsApplication.config.localNode.id);
		def se = new SystemEvent(params)
		se.setNode(node)
		se.setUser(user)
        respond se
    }

    @Transactional
    def save(SystemEvent systemEventInstance) {
		User user = springSecurityService.currentUser
		CbNode node = CbNode.findById(grailsApplication.config.localNode.id);
		
		if (systemEventInstance.user.id !=  springSecurityService.currentUser.id) {
			notFound()
			return
		}
        if (systemEventInstance == null) {
            notFound()
            return
        }
        if (systemEventInstance.hasErrors()) {
            respond systemEventInstance.errors, view:'create'
            return
        }
		if (!SystemEvent.findAllByUserAndNodeAndType(user,node,params.type).isEmpty()) {
			respond flash.message = 'Es gibt bereits einen SystemEvent diesen Typs!', view:'create'
			return
		}
        systemEventInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'systemEventInstance.label', default: 'SystemEvent'), systemEventInstance.id])
                redirect systemEventInstance
            }
            '*' { respond systemEventInstance, [status: CREATED] }
        }
    }

    def edit(SystemEvent systemEventInstance) {
        respond systemEventInstance
    }

    @Transactional
    def update(SystemEvent systemEventInstance) {
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

    @Transactional
    def delete(SystemEvent systemEventInstance) {
		if (systemEventInstance.user.id !=  springSecurityService.currentUser.id) {
			notFound()
			return
		}
        if (systemEventInstance == null) {
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
