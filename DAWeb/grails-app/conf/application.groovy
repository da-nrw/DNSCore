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

dataSource{
    pooled= true
    jmxExport= true
    driverClassName= 'org.postgresql.Driver'
    dialect= 'org.hibernate.dialect.PostgreSQLDialect'
    username= 'cb_usr'
	password= "4qS1BWisL6gUrvNYVFgFog=="
	passwordEncryptionCodec= 'de.uzk.hki.da.utils.DESCodec'
	characterEncoding= 'UTF-8'
	
}

environments{
	development{
		dataSource{
			pooled= true
			dialect= 'org.hibernate.dialect.PostgreSQLDialect'
			url= "jdbc:postgresql://localhost:5432/CB"
			dbCreate= 'validate'
		}
	}
	test{
		dataSource{
			pooled= true
			dialect= 'org.hibernate.dialect.PostgreSQLDialect'
			url= "jdbc:postgresql://localhost:5432/CB"
			dbCreate= 'validate'
		}
	}
	production{
		dataSource{
			pooled= true
			dialect= 'org.hibernate.dialect.PostgreSQLDialect'
			url= "jdbc:postgresql://localhost:5432/CB"
			dbCreate= 'none'
//			properties:
//			  jmxEnabled: true
//			  initialSize: 5
//			  maxActive: 50
//			  minIdle: 5
//			  maxIdle: 25
//			  maxWait: 10000
//			  maxAge: 600000
//			  timeBetweenEvictionRunsMillis: 5000
//			  minEvictableIdleTimeMillis: 60000
//			  validationQuery: SELECT 1
//			  validationQueryTimeout: 3
//			  validationInterval: 15000
//			  testOnBorrow: true
//			  testWhileIdle: true
//			  testOnReturn: false
//			  jdbcInterceptors: ConnectionState
//			  defaultTransactionIsolation: 2
		}
	}
}