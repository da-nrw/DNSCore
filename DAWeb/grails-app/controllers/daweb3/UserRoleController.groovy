package daweb3



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class UserRoleController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond UserRole.list(params), model:[userRoleInstanceCount: UserRole.count()]
    }

    def show(UserRole userRoleInstance) {
        respond userRoleInstance
    }

    def create() {
        respond new UserRole(params)
    }

    @Transactional
    def save(UserRole userRoleInstance) {
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
                flash.message = message(code: 'default.created.message', args: [message(code: 'userRoleInstance.label', default: 'UserRole'), userRoleInstance.id])
                redirect userRoleInstance
            }
            '*' { respond userRoleInstance, [status: CREATED] }
        }
    }

    def edit(UserRole userRoleInstance) {
        respond userRoleInstance
    }

    @Transactional
    def update(UserRole userRoleInstance) {
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
                flash.message = message(code: 'default.updated.message', args: [message(code: 'UserRole.label', default: 'UserRole'), userRoleInstance.id])
                redirect userRoleInstance
            }
            '*'{ respond userRoleInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(UserRole userRoleInstance) {

        if (userRoleInstance == null) {
            notFound()
            return
        }

        userRoleInstance.delete flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'UserRole.label', default: 'UserRole'), userRoleInstance.id])
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
