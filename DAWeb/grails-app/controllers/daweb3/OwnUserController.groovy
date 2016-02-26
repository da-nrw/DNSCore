package daweb3

import grails.transaction.Transactional

class OwnUserController {
	
	def springSecurityService
	
	def index() {
		User userInstance = springSecurityService.currentUser
		respond userInstance
	}

	@Transactional
	def update(User userInstance) {
		if (userInstance == null) {
			notFound()
			return
		}

		if (userInstance.hasErrors()) {
			respond userInstance.errors, view:'index'
			return
		}

		userInstance.save flush:true

		request.withFormat {
			form {
				flash.message = message(code: 'default.updated.message', args: [message(code: 'User.label', default: 'User'), userInstance.id])
				redirect controller: "ownUser", action: "index", method: "GET"
			}
			'*'{ respond userInstance } 
		}
	}
	
	protected void notFound() {
		request.withFormat {
			form {
				flash.message = message(code: 'default.not.found.message', args: [message(code: 'userInstance.label', default: 'User'), params.id])
				redirect controller: "ownUser", action: "index", method: "GET"
			}
			'*'{ render status: NOT_FOUND }
		}
	}
}
