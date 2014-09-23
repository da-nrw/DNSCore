


## Application logging

DNSCore uses log4j extensively to present the output of the application in a meaningful, consistent and easy to debug manner.

### Basic logging

Before we consider the application specific logging, let us first point out that there are two logfiles which capture the basic output of the java command. In case your ContentBroker doesn't start, have a look at the content of these files.

    ${CB_HOME}/log/stderr.log
    
The stderr output from the execution of the java command started via nohup in ContentBroker.sh. Gets created on startup. Gets overwritten completely every time the ContentBroker gets restarted. **Note** that if this file has content but it doesn't change from startup to startup, it is the stderr from the last run. After a proper run the file always should be empty.

    ${CB_HOME}/log/stdout.log
    
The stdout output from the execution of the java command started via nohup in ContentBroker.sh. Gets created on startup. Gets overwritten completely every time the ContentBroker gets restarted.

### Application specific configuration - default behaviour

The ContentBroker finds its logging configuration at

    ${CB_HOME}/conf/logback.xml
 
The file automatically gets installed by the installer. Without this file beeing present, the ContentBroker would theoretically work, but without useful logging. By the usage of this file, the ContentBroker configures itself to log into several files. These are described below

    ${CB_HOME}/log/contentbroker.log
    
This logger outputs information about the workings of the base system, which is responsible for fetching jobs from the database and making actions out of them. The business code of the actions itself gets logged by other loggers. Also setup of the ContentBroker is logged here, why it is usually a good idea to start looking here if the ContentBroker seems not to start properly.

    ${CB_HOME}/log/ingest.log
    
This logger provides information coming from the IngestScannerWorker, the component which scans the [IngestArea](processing_stages.md#ingestarea)s contractor folders for incoming SIP packages. If wonder why there are packages in the IngestArea and the ContentBroker doesn't fetch them, this file is one of the most obvious places to start debugging.

    ${CB_HOME}/log/grid.log
    
The grid log provides information about from the package grid. 
    
    ${CB_HOME}/log/object-logs
    
TODO describe

**Note** that it is also possible to override the default settings by modifying the logback.xml. This is for experimental purposes only. The logback.xml gets automatically overwritten by the installer on every update of the application so all changes will be lost after an update.


### cbTalk

In the ContentBroker HOME directory, you find a command line tool called 

    cbTalk.sh 
    
This is a wrapper Tool connecting to the Active MQ Broker, CB establishes while starting. 

Stopping CB`s main factory, useful when restarting CB. CB will perform any operations to their dedicated end state, but won't perform any newly created queue entries. This preserves CB from any inconsitencies while performing operations on SIP. 

    ./cbTalk.sh STOP_FACTORY
    
Starting CB`s main factory

     ./cbTalk.sh START_FACTORY
     
Graceful stopping factory, same as STOP_FACTORY but tries to exit the running JVM. (This feature is experimental, Admins should always ensure to kill CB'S proc after executing coammand)

     ./cbTalk.sh GRACEFUL_SHUTDOWN

Show Version of CB 
     
     ./cbTalk.sh SHOW_VERSION

Show running Actions of working CB

     ./cbTalk.sh SHOW_ACTIONS 
     
Same operations could be carried out via DAWeb

	
# Administration - Interface Reference 

While we have an administration and user frontend called DA-Web which lets users (end users and administrators) interact in a dynamic manner, there are several configuration options of DNSCore which are considered static properties of the system. They are configured in the old fashioned way of good old configuration files which can be edited with your good old favorite linux editor. Easy! 

These artefacts comprise the interface to the ContentBroker with which administrators must learn to deal with in order to configure and run a proper node of a DNSCore based system. The document is structed in that way that each configuration file is described extensively in its own passage.

**Note** that for the purporse of the discussion of this document, we call the ContentBroker installation directory ${CB_HOME}

**Note** that, as the interfaces can change from time to time, in doubt you should look at the right version of this document. Go to the releases page and you'll find the source tree for this specific version. 



## Application Database configuration

&{CB_HOME}/conf/hibernateCentralDB.cfg.xml

    <property name="connection.driver_class">org.postgresql.Driver</property>
    <property name="connection.url">jdbc:postgresql://localhost:5432/CB</property>
    <property name="connection.username">cb_usr</property>
    <property name="connection.password">vb9gpJq/TjrkFcJ0jaJu+w==</property>

TODO ergänzen. auf encryption hinweisen
 
    <property name="connection.pool_size">5</property>
    <property name="dialect">org.hibernate.dialect.PostgreSQLDialect</property>

    <property name="current_session_context_class">thread</property>
 
    <property name="format_sql">true</property>
    <property name="hbm2ddl.auto">validate</property>
 
TODO hinweis auf contentbroker -create, validate sollte nicht verwendet werden.
 
    <mapping class="de.uzk.hki.da.model.Contractor"/>
    <mapping class="de.uzk.hki.da.model.Node"/>
    ...
    <mapping class="de.uzk.hki.da.model.SecondStageScanPolicy"/>

TODO description 

    <property name="cache.provider_class">org.hibernate.cache.NoCacheProvider</property>

TODO anmerkung feststehende properties.




