hibernate {
    cache.use_second_level_cache = false
    cache.use_query_cache = false
    //cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}
// environment specific settings
environments {
	test {
		// set per-environment irods config in classpath file!
		grails.config.locations = ["classpath:${appName}_properties.groovy"]
	}	
	production {
		// set per-environment irods config in classpath file!
		grails.config.locations = ["classpath:${appName}_properties.groovy"]
	}
	development {
		
		dataSource {
			pooled = true
			driverClassName = "org.postgresql.Driver"
			dialect = org.hibernate.dialect.PostgreSQLDialect
			dbCreate = "validate"
			url = "jdbc:postgresql://da-nrw-vm3.hki.uni-koeln.de:5435/contentbroker?autoReconnect=true"
			username = "irods"
			password = "98Q4P4ZgUWCey8PHPYxM3g=="
			passwordEncryptionCodec = "de.uzk.hki.da.utils.DESCodec"
			characterEncoding = "UTF-8"
		}
		
	}
}
