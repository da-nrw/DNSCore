package daweb3

class PackageController {

   	def index() {
			redirect(action: "show", params: params)
		}
	
	   def show() {
		   def packageInstance = Package.get(params.id)
		   if (!packageInstance) {
			   flash.message = message(code: 'default.not.found.message', args: [message(code: 'object.label', default: 'Package'), params.id])
			   redirect(action: "list")
			   return
		   }
		   [packageInstance: packageInstance]
	   }
   
	   }
