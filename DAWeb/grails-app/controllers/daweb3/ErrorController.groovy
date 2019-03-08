package daweb3

class ErrorController {
	
		def error() {
			render view: 'error'    }
	
		def invalid() {
			render view: 'error'    }
	
		def denied() {
			render view: 'denied'    }
	
		def notFound() {
			render view: 'notFound'    }
}