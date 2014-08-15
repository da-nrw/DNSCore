package daweb3

class LogoutController {

    def index() { 
		if (session!=null){
			session.invalidate()
			redirect(uri:'/')
		}
		
	}
}
