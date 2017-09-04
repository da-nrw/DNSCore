package daweb3

import grails.converters.JSON
import javax.jms.Session
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.annotation.Secured
import groovy.lang.Closure

@Secured(closure= {true})
class LogoutController {
	def springSecurityService
	def index = {
		session.invalidate()
//		sessionRegistry.removeSessionInformation(session.id)
//		redirect uri:  'login/auth' //SpringSecurityUtils.securityConfig.logout.afterLogoutUrl
		render (view: "/login/auth" , model:[admin: 0,user:null])
	
	}
}
