grails.plugin.springsecurity.onAbstractAuthenticationFailureEvent = { e, appCtx ->
	println "\nERROR auth failed for user $e.authentication.name: $e.exception.message\n"
}

dataSource {
		pooled = true
		jmxExport=true
		driverClassName = "org.postgresql.Driver"
		dialect = org.hibernate.dialect.PostgreSQL8Dialect
		username = "cb_usr"
		password = "4kj8yne/hx7g6D2EhjMlrg=="
		passwordEncryptionCodec = "de.uzk.hki.da.utils.DESCodec"
		characterEncoding = "UTF-8"
		dbCreate = "validate"
		url = "jdbc:postgresql://localhost:5432/CB?autoReconnect=true"
}

grails.config.locations = [
		'file:${catalina.home}/.grails/daweb3_properties.groovy'
]

environments{
	localNode.id = 1
	localNode.userAreaRootPath = "/ci/storage/UserArea"
	localNode.ingestAreaRootPath = "/ci/storage/IngestArea"
	
	main.css = "main.css"
	mobile.css = "mobile.css"
			
	daweb3.loginManager = "de.uzk.hki.da.login.PlainLogin"
	cb.port = 4455
	daweb3.logo = "DANRW-Logo_small.png"
	transferNode.downloadLinkPrefix = "localhost/transfer"
	fedora.urlPrefix = "https://HOSTPRES/fedora/objects/"
	cb.presServer= "HOSTPRES"

}
