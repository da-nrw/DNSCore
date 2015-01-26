modules = {
    application {
        resource url:'js/application.js'
    }
    jqueryuitemplate {
    	resource url: 'css/smoothness/jquery-ui-1.8.18.custom.css'
    }
    jqueryui {
    	dependsOn 'jquery'
    	dependsOn 'jqueryuitemplate'
    	resource url: 'js/jquery.ui.min.js'
    }
    periodicalupdater {
        dependsOn 'jquery'
        resource url: 'js/jquery.periodicalupdater.js'
    }
    messagebox {
    	dependsOn 'jqueryui'
    	resource url: 'js/jquery.ui.message.min.js'
    }
	
	
}
