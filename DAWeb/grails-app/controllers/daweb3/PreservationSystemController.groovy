package daweb3



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class PreservationSystemController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond PreservationSystem.list(params), model:[preservationSystemInstanceCount: PreservationSystem.count()]
    }

    def show(PreservationSystem preservationSystemInstance) {
        respond preservationSystemInstance
    }

    @Transactional
    def save(PreservationSystem preservationSystemInstance) {
        if (preservationSystemInstance == null) {
            notFound()
            return
        }

        if (preservationSystemInstance.hasErrors()) {
            respond preservationSystemInstance.errors, view:'create'
            return
        }

        preservationSystemInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'preservationSystemInstance.label', default: 'PreservationSystem'), preservationSystemInstance.id])
                redirect preservationSystemInstance
            }
            '*' { respond preservationSystemInstance, [status: CREATED] }
        }
    }

    def edit(PreservationSystem preservationSystemInstance) {
        respond preservationSystemInstance
    }

    @Transactional
    def update(PreservationSystem preservationSystemInstance) {
        if (preservationSystemInstance == null) {
            notFound()
            return
        }

        if (preservationSystemInstance.hasErrors()) {
            respond preservationSystemInstance.errors, view:'edit'
            return
        }

        preservationSystemInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'PreservationSystem.label', default: 'PreservationSystem'), preservationSystemInstance.id])
                redirect preservationSystemInstance
            }
            '*'{ respond preservationSystemInstance, [status: OK] }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'preservationSystemInstance.label', default: 'PreservationSystem'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
