package daweb3



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class UserRoleController {
	
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
        respond UserRole.list(params), model:[userRoleInstanceCount: UserRole.count(), user:user, admin:admin]
    }

	def show(Long userId, Long roleId) {
		ceu.setEncoding(response)
		def user = springSecurityService.currentUser
		def admin = 0;
		
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		
 		def userRoleInstance = UserRole.get(userId, roleId)
		
		if (!userRoleInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'userRoleInstance.label', default: 'userRoleInstance not found'), id])
			redirect(action: "list")
			return
		}
		[userRoleInstance: userRoleInstance, user: user, admin: admin]
	}

    def create() {
		
		def user = springSecurityService.currentUser
		def admin = 0;
		
		if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
			admin = 1;
		}
		
		ceu.setEncoding(response)
        respond new UserRole(params), model: [ user: user, admin: admin]
    }
	
	def cancel() {
		redirect(action: "show",  id: params.id)
	}

    @Transactional
    def save(UserRole userRoleInstance) {
		ceu.setEncoding(response)
        if (userRoleInstance == null) {
            notFound()
            return
        }

        if (userRoleInstance.hasErrors()) {
            respond userRoleInstance.errors, view:'create'
            return
        }

        userRoleInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'userRoleInstance.label', default: 'UserRole'), ""])
                redirect(action: 'index')
            }
            '*' { respond UserRole.list(params), model:[userRoleInstanceCount: UserRole.count()] }
        }
    }

   def edit(Long userId, Long roleId) {
	   
	   def user = springSecurityService.currentUser
	   def admin = 0;
	   
	   if (user.authorities.any { it.authority == "ROLE_NODEADMIN" }) {
		   admin = 1;
	   }
	   
		def userRoleInstance = UserRole.get(userId, roleId)
		if (!userRoleInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'userRoleInstance.label', default: 'userRoleInstance not found'), id])
			redirect(action: "list")
			return
		}
		[userRoleInstance: userRoleInstance,  user: user, admin: admin]
	}

    @Transactional
    def update(Long userId, Long roleId) {
		ceu.setEncoding(response)
		def userRoleInstance = UserRole.get(userId, roleId)
        if (userRoleInstance == null) {
            notFound()
            return
        }

        if (userRoleInstance.hasErrors()) {
            respond userRoleInstance.errors, view:'edit'
            return
        }

        userRoleInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'UserRole.label', default: 'UserRole'), ""])
                redirect show(userId,roleId)
            }
            '*'{ respond UserRole.list(params), model:[userRoleInstanceCount: UserRole.count()]}
        }
    }

    @Transactional
    def delete(Long userId, Long roleId) {
		def userRoleInstance = UserRole.get(userId, roleId)
        if (userRoleInstance == null) {
            notFound()
            return
        }

        userRoleInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'UserRole.label', default: 'UserRole'), ""])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'userRoleInstance.label', default: 'UserRole'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }

	}
