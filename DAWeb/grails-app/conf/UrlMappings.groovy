class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}
		"/contractor/login"(controller:"login")
		"/status/$action/$contractor/$origName"(controller:"status",action:"index")
		"/status/$action/$urn"(controller:"status",action:"index")
		"/"(controller:"home")
		"500"(view:'/error')
	}
}
