// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'daweb3.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'daweb3.UserRole'
grails.plugin.springsecurity.authority.className = 'daweb3.Role'

grails.plugin.springsecurity.rejectIfNoRule = false

grails.plugin.springsecurity.fii.rejectPublicInvocations = false

grails.plugin.springsecurity.filterChain.chainMap = [
	[pattern: '/assets/**',      filters: 'none'],
	[pattern: '/**/js/**',       filters: 'none'],
	[pattern: '/**/css/**',      filters: 'none'],
	[pattern: '/**/images/**',   filters: 'none'],
	[pattern: '/**/favicon.ico', filters: 'none'],
	[pattern: '/**',             filters: 'JOINED_FILTERS']
]
