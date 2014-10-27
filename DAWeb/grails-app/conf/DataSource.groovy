hibernate {
    cache.use_second_level_cache = false
    cache.use_query_cache = false
    //cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory'
}
// environment specific settings
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
		grails.config.locations = ["file:${userHome}/.grails/${appName}_properties.groovy"]				
	}
}
