package daweb3



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional
import grails.plugin.springsecurity.annotation.Secured

@Transactional(readOnly = true)
class CbNodeController {

	def springSecurityService
	
    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]
	static CharacterEncodingUtils  ceu = new CharacterEncodingUtils()
	
    def index(Integer max) {
		User user = springSecurityService.currentUser
		def admin = 0;
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
        params.max = Math.min(max ?: 10, 100)
		ceu.setEncoding(response)
        respond CbNode.list(params), model:[cbNodeInstanceCount: CbNode.count(), user:user, admin:admin]
    }

    def show(CbNode cbNodeInstance) {
		User user = springSecurityService.currentUser
		def admin = 0;
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		ceu.setEncoding(response)
        respond cbNodeInstance, model:[user:user, admin:admin]
    }

    def create() {
		User user = springSecurityService.currentUser
		def admin = 0;
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		ceu.setEncoding(response)
        respond new CbNode(params), model:[user:user, admin:admin]
    }

    @Transactional
    def save(CbNode cbNodeInstance) {
		User user = springSecurityService.currentUser
		def admin = 0;
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		ceu.setEncoding(response)
		
        if (cbNodeInstance == null) {
            notFound()
            return
        }

        if (cbNodeInstance.hasErrors()) {
            respond cbNodeInstance.errors, view:'create', model:[user:user, admin:admin]
            return
        }

        cbNodeInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'cbNodeInstance.label', default: 'CbNode'), cbNodeInstance.id])
                redirect cbNodeInstance
            }
            '*' { respond cbNodeInstance, [status: CREATED], model:[user:user, admin:admin] }
        }
    }

	def cancel(CbNode cbNodeInstance) {
		redirect cbNodeInstance
	}
	
    def edit(CbNode cbNodeInstance) {
		User user = springSecurityService.currentUser
		def admin = 0;
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		ceu.setEncoding(response)
        respond cbNodeInstance, model:[user:user, admin:admin]
    }

    @Transactional
    def update(CbNode cbNodeInstance) {
		User user = springSecurityService.currentUser
		def admin = 0;
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
        if (cbNodeInstance == null) {
            notFound()
            return
        }

        if (cbNodeInstance.hasErrors()) {
            respond cbNodeInstance.errors, view:'edit', model:[user:user, admin:admin]
            return
        }

        cbNodeInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'CbNode.label', default: 'CbNode'), cbNodeInstance.id])
                redirect cbNodeInstance
            }
            '*'{ respond cbNodeInstance, [status: OK], model:[user:user, admin:admin] }
        }
    }

    protected void notFound() {
		User user = springSecurityService.currentUser
		def admin = 0;
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'cbNodeInstance.label', default: 'CbNode'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND, model:[user:user, admin:admin] }
        }
    }
}
