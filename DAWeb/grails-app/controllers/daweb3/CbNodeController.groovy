package daweb3



import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Transactional(readOnly = true)
class CbNodeController {

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond CbNode.list(params), model:[cbNodeInstanceCount: CbNode.count()]
    }

    def show(CbNode cbNodeInstance) {
        respond cbNodeInstance
    }

    def create() {
        respond new CbNode(params)
    }

    @Transactional
    def save(CbNode cbNodeInstance) {
        if (cbNodeInstance == null) {
            notFound()
            return
        }

        if (cbNodeInstance.hasErrors()) {
            respond cbNodeInstance.errors, view:'create'
            return
        }

        cbNodeInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.created.message', args: [message(code: 'cbNodeInstance.label', default: 'CbNode'), cbNodeInstance.id])
                redirect cbNodeInstance
            }
            '*' { respond cbNodeInstance, [status: CREATED] }
        }
    }

    def edit(CbNode cbNodeInstance) {
        respond cbNodeInstance
    }

    @Transactional
    def update(CbNode cbNodeInstance) {
        if (cbNodeInstance == null) {
            notFound()
            return
        }

        if (cbNodeInstance.hasErrors()) {
            respond cbNodeInstance.errors, view:'edit'
            return
        }

        cbNodeInstance.save flush:true

        request.withFormat {
            form {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'CbNode.label', default: 'CbNode'), cbNodeInstance.id])
                redirect cbNodeInstance
            }
            '*'{ respond cbNodeInstance, [status: OK] }
        }
    }

    protected void notFound() {
        request.withFormat {
            form {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'cbNodeInstance.label', default: 'CbNode'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
