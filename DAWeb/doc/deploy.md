## Deploy Da-Web3 WAR

### Build Da-Web3
In normal build processes this is done automatically by the install processes called in
the maven build process. If you want to build DA-Web as isolated project, you will need 
to have GRAILS installed on your command line, while the project itself is not mavenized 
yet. 

The command 
<pre>grails war prod</pre>
war will build the target file for you. 

### Installation

In most cases dropping the built war into your servlet container and having the 
<pre>daweb_properties.groovy</pre> in place would be sufficient to run the application. Please
undeploy former versions of daweb completely. 

Please keep in mind: without having the properties file in place, the webapp won't start 
at all. 

### Configuration

We added a configuration system under which you might be able to alter most of the 
parameters being used in the webapp. 
The file MUST be located under the tomcat owner's home directory (e.g. User "tomcat")
<pre>
/home/tomcat/.grails/daweb_properties.groovy
</pre>
This file has to be just readable by the Tomcat process owner. 
You can find the file in the main folder daweb as well. The file lists the following properties:
<pre>
 
environments {
	production {
		//log4j = {
		//		debug   'grails.app'
		//}
		daweb3.loginManager = "de.uzk.hki.da.login.IrodsLogin"
		cb.port = 4455
		daweb3.logo = "DANRW-Logo_small.png"
		irods.server = "localhost"
		irods.default_resc = "your irods resc name"
		irods.zone = "your home zone name"
		localNode.userAreaRootPath = "/path_to_/userhome/SIP"
		localNode.ingestAreaRootPath = "path_to_/ingest"
		transferNode.downloadLinkPrefix = "prefix for URL to download DIP"
		fedora.urlPrefix = "http://prefix_for_fedora/fedora/objects/"
		cb.presServer= "CB nodename of fedora Server"
		
		// here it's up to you to decide whether environment you want to use:
		dataSource {
			pooled = true
			driverClassName = "org.hsqldb.jdbcDriver"
			dialect = org.hibernate.dialect.HSQLDialect
			url = "jdbc:hsqldb:hsql://localhost/xdb"
			dbCreate = "validate"
			username = "sa"
			password = ""
			characterEncoding = "UTF-8"
		}
	}


test {
			
		dataSource {
		}
}

development {		
		cb.homepath = ""
		daweb3.logo = ""
		irods.server = ""
		irods.default_resc = ""
		irods.zone = ""
		localNode.userAreaRootPath = ""
		localNode.ingestAreaRootPath = ""
		transferNode.downloadLinkPrefix = ""
		fedora.urlPrefix = ""
		cb.presServer= ""
		
		dataSource {
			pooled = true
			driverClassName = "org.postgresql.Driver"
			dialect = org.hibernate.dialect.PostgreSQLDialect
			dbCreate = "validate"
			url = "jdbc:postgresql://localhost:5432/cb?autoReconnect=true"
			username = "irods"
			password = "=="
			passwordEncryptionCodec = "de.uzk.hki.da.utils.DESCodec"
			characterEncoding = "UTF-8"
		
		}
	}
}
</pre> 

Although you might be able to add three environments, in fact the productional environment 
is used in normal software releases only. Of course you might be able to use the same productional
build in several environments as it depends only on the values added to properties file. 

Most of the params being used must have the same values like they've in the adjacent contentbroker
file "config.properties". 

To encode your own DB Password, you could use the command:

<pre>
groovy de.uzk.hki.da.utils.DESCodec <your_password>
</Pre>   
