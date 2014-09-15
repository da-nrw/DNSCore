// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }


grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
                      all: '*/*',
                      json: ['application/json','text/json'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']


// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// enable query caching by default
grails.hibernate.cache.queries = true

// set per-environment serverURL stem for creating absolute links
environments {
    development {
        grails.logging.jul.usebridge = true
    }
    test {
        grails.logging.jul.usebridge = true
    }
    production {
        grails.logging.jul.usebridge = false
    }
}

grails.plugins.activemq.active=false
environments {
	test {
		// set per-environment irods config in classpath file!
		grails.config.locations = ["file:${userHome}/.grails/${appName}_properties.groovy"]
	}	
	production {
		// set per-environment irods config in classpath file!
		grails.config.locations = ["file:${userHome}/.grails/${appName}_properties.groovy"]
	}
	development {
		// running this on vm3 would cause a lot of catalina.out stuff!!		
		//log4j = {
		//		debug   'grails.app'
		//}
		grails.config.locations = ["file:${userHome}/.grails/${appName}_properties.groovy"]		
	}
}

// log4j configuration
log4j = {

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
	
	warn   'grails.app'	
}

grails.plugin.springsecurity.userLookup.userDomainClassName = 'daweb3.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'daweb3.UserRole'
grails.plugin.springsecurity.authority.className = 'daweb3.Role'
// if all components are secured true should be the default
grails.plugin.springsecurity.rejectIfNoRule = true
grails.plugin.springsecurity.fii.rejectPublicInvocations = false
grails.plugin.springsecurity.useSecurityEventListener = true
// Basic auth is needed for REST style interface to DNS
grails.plugin.springsecurity.useBasicAuth = true
grails.plugin.springsecurity.basic.realmName = "DAWEB - DNSCORE"
grails.plugin.springsecurity.filterChain.chainMap = [
	'/status/**': 'JOINED_FILTERS,-exceptionTranslationFilter',
    '/**': 'JOINED_FILTERS,-basicAuthenticationFilter,-basicExceptionTranslationFilter'
]
grails.plugin.springsecurity.onAbstractAuthenticationFailureEvent = { e, appCtx ->
   println "\nERROR auth failed for user $e.authentication.name: $e.exception.message\n"
}
grails.plugin.springsecurity.securityConfigType = "InterceptUrlMap"
grails.plugin.springsecurity.interceptUrlMap = [
	'/':                  ['ROLE_CONTRACTOR'],
	'/index':             ['ROLE_CONTRACTOR'],
	'/index.gsp':         ['ROLE_CONTRACTOR'],
	'/**/js/**':          ['permitAll'],
	'/**/css/**':         ['permitAll'],
	'/**/images/**':      ['permitAll'],
	'/**/favicon.ico':    ['permitAll'],
	'/login/**':          ['permitAll'],
	'/logout/**':         ['permitAll'],
	'/contractor/**':         ['permitAll'],
	'/queueEntry/**':     ['ROLE_CONTRACTOR'],
	'/automatedRetrieval/**':     ['ROLE_CONTRACTOR'],
	'/object/**':    	  ['ROLE_CONTRACTOR'],
	'/incoming/**':       ['ROLE_CONTRACTOR'],
	'/outgoing/**':       ['ROLE_CONTRACTOR'],
	'/status/**':       ['ROLE_CONTRACTOR'],
	'/package/**':       ['ROLE_CONTRACTOR'],
	'/info/**':       ['ROLE_CONTRACTOR'],
	'/conversionPolicies/**':       ['ROLE_CONTRACTOR'],
	'/cbtalk/**':       ['ROLE_NODEADMIN'],
	'/user/**':         ['ROLE_PSADMIN'],
	'/userRole/**':         ['ROLE_PSADMIN'],
	'/role/**':         ['ROLE_PSADMIN'],
	'/preservationSystem/**':         ['ROLE_PSADMIN']
 ]
// Uncomment and edit the following lines to start using Grails encoding & escaping improvements

/* remove this line 
// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside null
                scriptlet = 'none' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}
remove this line */
