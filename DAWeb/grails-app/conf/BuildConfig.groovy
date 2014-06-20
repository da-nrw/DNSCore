grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
grails.project.dependency.resolver="maven"
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolution = {
    inherits("global") {
    	excludes 'spring-asm'
    	excludes "slf4j-api"
    	excludes "slf4j-log4j12"
    	excludes "jul-to-slf4j"
    	excludes "jcl-over-slf4j"
    	
        // uncomment to disable ehcache
        // excludes 'ehcache'
        		       exclude group: 'org.slf4j', module:'jcl-over-slf4j'
        exclude group: 'org.slf4j', module:'jul-to-slf4j'
        exclude group: 'org.slf4j', module:'slf4j-api'
        exclude group: 'org.slf4j', module:'slf4j-simple'
    }
    log "verbose" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve

    repositories {
        inherits true // Whether to inherit repository definitions from plugins
        grailsPlugins()
        grailsHome()
        grailsCentral()
        // uncomment these to enable remote dependency resolution from public Maven repositories
        mavenCentral()
        mavenLocal()
	mavenRepo "http://repo.grails.org/grails/repo" 
	mavenRepo "http://download.java.net/maven/2/"
	mavenRepo "http://repository.jboss.org/nexus/content/groups/public-jboss/"
	mavenRepo "http://mvnrepository.com/"
        //mavenRepo "http://repository.codehaus.org"
        mavenRepo "http://download.java.net/maven/2/"
        mavenRepo "http://repo.fusesource.com/nexus/content/groups/public" 
	//mavenRepo "http://mvnrepository.com/artifact/"
	mavenRepo "http://ci-dev.renci.org/nexus/content/repositories/public"
    	mavenRepo "http://repo.grails.org/grails/plugins"
	}
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.
		// runtime 'mysql:mysql-connector-java:5.1.16'
		runtime 'postgresql:postgresql:9.0-801.jdbc4'
		runtime 'org.hsqldb:hsqldb:2.3.1'
		compile('org.apache.activemq:activemq-client:5.9.0',
			'org.apache.xbean:xbean-spring:3.7') {
			 excludes 'activemq-openwire-generator'
			 excludes 'commons-logging'
			 excludes "spring-context"
			 excludes 'xalan'
			 excludes 'slf4j-api'
			 excludes 'org.slf4j'
			
			 excludes 'xml-apis'
			 exported = false
		}
		compile('org.irods.jargon:jargon-core:3.3.2-beta1') {
			excludes "slf4j-api", "slf4j-log4j12", "commons-io", "commons-codec","cog-jglobus"
		}
    }

    plugins {
        runtime ":hibernate:3.6.10.2"
		runtime ":jquery:1.7.1"
        runtime ":resources:1.1.6"
        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.4"
	
		build ":tomcat:7.0.42"	
		compile (":jms:1.3") {
		excludes 'spring-asm'
		}
		compile ":jquery-ui:1.8.15" 
		compile ":modernizr:2.0.6"
    }
}
