### Feature sets of the DNSCore

In order to give the administrator only the necessary configuration options for a specified feature set, the admin can
choose, when installing the DNSCore, between three feature sets and one additional option:

1. (n)ode 
2. (p)res
3. (f)ull
4. preserve (e)xisting beans.

This choice lets the installer deliver only the beans.xml with the necessary configuration.

TODO dependency injection
TODO config.properties.

### config.properties


### beans.xml

For the best overview for the administrator, the installer installs a specific version of the spring configuration for 
the feature set at hand. After an installation you'll always find this configuration at [ContentBroker]/conf/beans.xml.
Depending on the feature set, the installer copies one of the four existing (we'll only consider three version here, since the
fourth version is a version used for tests and hence discussed elsewhere TODO) to the target file beans.xml.

You'll find the different version either in the source code repository at src/main/conf or directly in the installer/binaries for 
your build of choice.

The choice of any of the xmls results in different application context which is generated trough different imports. The following
block will show which beans.xml version will generate which application context:

<pre>
	-> beans.xml.node (This is the beans you see as beans.xml when you install your DNSCore with feature set (n)ode)
	    -> imports src/main/resources/META-INF/beans-infrastructure.node.xml
            -> imports src/main/resources/META-INF/beans-infrastructure.common.xml
                  -> imports src/main/resources/META-INF/beans-infrastructure.core.xml    
       -> imports src/main/resources/META-INF/beans-workflow.ingest.xml  
       -> imports src/main/resources/META-INF/beans-workflow.retrieval.xml  
       -> imports src/main/resources/META-INF/beans-workflow.pipgen.xml  
       -> imports src/main/resources/META-INF/beans-workflow.other.xml  
	
	-> beans.xml.full (This is the beans you see as beans.xml when you install your DNSCore with feature set (f)ull)
	    -> imports src/main/resources/META-INF/beans-infrastructure.full.xml
            -> imports src/main/resources/META-INF/beans-infrastructure.common.xml
                  -> imports src/main/resources/META-INF/beans-infrastructure.core.xml
       -> imports src/main/resources/META-INF/beans-workflow.ingest.xml  
       -> imports src/main/resources/META-INF/beans-workflow.retrieval.xml  
       -> imports src/main/resources/META-INF/beans-workflow.pipgen.xml  
       -> imports src/main/resources/META-INF/beans-workflow.other.xml  
       -> imports src/main/resources/META-INF/beans-workflow.presentation.xml  

	-> beans.xml.pres (This is the beans you see as beans.xml when you install your DNSCore with feature set (p)res)
		-> imports src/main/resources/META-INF/beans-infrastructure.pres.xml
            -> imports src/main/resources/META-INF/beans-infrastructure.common.xml
                  -> imports src/main/resources/META-INF/beans-infrastructure.core.xml
       -> imports src/main/resources/META-INF/beans-workflow.presentation.xml
</pre>



