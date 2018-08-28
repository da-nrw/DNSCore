// Added by the Spring Security Core plugin:
//grails.plugin.springsecurity.userLookup.userDomainClassName = 'daweb3.User'
//grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'daweb3.UserRole'
//grails.plugin.springsecurity.authority.className = 'daweb3.Role'

//grails.plugin.springsecurity.rejectIfNoRule = false

//grails.plugin.springsecurity.fii.rejectPublicInvocations = false

//grails.plugin.springsecurity.filterChain.chainMap = [
//	[pattern: '/assets/**',      filters: 'none'],
//	[pattern: '/**/js/**',       filters: 'none'],
//	[pattern: '/**/css/**',      filters: 'none'],
//	[pattern: '/**/images/**',   filters: 'none'],
//	[pattern: '/**/favicon.ico', filters: 'none'],
//	[pattern: '/**',             filters: 'JOINED_FILTERS']
//]

grails.plugin.springsecurity.onAbstractAuthenticationFailureEvent = { e, appCtx ->
	println "\nERROR auth failed for user $e.authentication.name: $e.exception.message\n"
}

dataSource{
	pooled=""
	jmxExport=true
	driverClassName=""
	dialect=""
	username=""
	password=""
	passwordEncryptionCodec=""
	characterEncoding=""
	url=""
}

grails.config.locations = [
		'file:${catalina.home}/.grails/daweb3_properties.groovy'
]

environments{
	
	localNode.id= ""
	main.css = ""
	mobile.css = "" 
	daweb3.loginManager = "de.uzk.hki.da.login.PlainLogin"
	cb.port = ""
	daweb3.logo =  ""
	irods.server = "localnode"
	irods.default_resc = "ciWorkingResource"
	irods.zone = "c-i"
	localNode.userAreaRootPath = ""
	localNode.ingestAreaRootPath = ""
	transferNode.downloadLinkPrefix = ""
	fedora.urlPrefix = ""
	cb.presServer= ""
}
