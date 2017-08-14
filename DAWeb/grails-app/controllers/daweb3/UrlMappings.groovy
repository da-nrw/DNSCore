package daweb3

class UrlMappings {
	
	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
		"/contractor/login"(controller:"login")
		"/status/$action/$contractor/$origName"(controller:"status", action:"index")
		"/status/$action/$urn"(controller:"status", action:"index")

		
		"/"(controller:"home")
		
//		"/"(view:"/index")
		
		"400"(controller: "error", action: "invalid")
		"500"(controller: "error", action: "error")
		"403"(controller: "error", action: "denied")
		"404"(controller: "error", action: "notFound")
		
    }
}
