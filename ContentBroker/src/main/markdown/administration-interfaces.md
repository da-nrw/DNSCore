# Administration - Interface Reference 

While we have an administration and user frontend called DA-Web which lets users (end users and administrators) interact in a dynamic manner, there are several configuration options of DNSCore which are considered static properties of the system. They are configured in the old fashioned way of good old configuration files which can be edited with your good old favorite linux editor. Easy! 

These artefacts comprise the interface to the ContentBroker with which administrators must learn to deal with in order to configure and run a proper node of a DNSCore based system. The document is structed that each configuration file is described extensively in its own passage.

Note that for the purporse of the discussion of this document, we call the ContentBroker installation directory ${CB_HOME}

## config.properties

The file config.properties is a necessary part of every DNSCore installation. It has always to be stored
in the conf directory directly under the DNSCore main directory (which is the one that contains the ContentBroker.jar).
With the exception of the database settings, the config properties is intended to hold the settings necessary to
let your ContentBroker know how its environment is configured. Directory paths, adapter configurations etc. are alle stored in this single file.

The file is logically devided into several blocks of which each has a prefix to mark a property as beloning to the
according block:

### localNode

The localNode block contains all the settings related to the configuration of the machine itself as well as information
regarding the administrator role. Note that the localNode is the implementation of the node concept of the domain business model.

    localNode.userAreaRootPath=
    localNode.ingestAreaRootPath=
    localNode.workAreaRootPath=
    localNode.gridCacheAreaRootPath=
    
These four properties should be set to the absolute physical paths of the mount points in the file system which hold
the aip data during the ContentBroker workflow processing stages. If you don't know anything about the concept of areas
in context of DNSCore, have a look at [this](processing_stages.md) document. It doesn't matter if the paths carry a trailing slash or not. But make sure the paths are absolute and not relative.
    
    localNode.workingResource=ciWorkingResource
    localNode.replDestinations=ciArchiveResourceGroup
   
asdf 
    
    localNode.name=localnode

df

    localNode.id= 
    
(This setting must contain the integer value primary key of the nodes correspoding entry in the nodes table of the object db 
    localNode.admin_email=da-nrw-notifier@uni-koeln.de

### system

System properties apply to all nodes comprising the so called "system" (TODO link to glossary or data model). As
a consequence, for a properly working system all the nodes system properties have to be exactly the same. If the
nodes are maintained by different administrators (perhaps if the nodes are distributed geographically or organisationally) the administrators must agree upon the common setings.

    system.min_repls=1
    
The minimum number of replications the ContentBroker asks the grid component for to fulfill to consider a copy (an AIP) long term archived. Normally it is 3.
    
    system.sidecar_extensions=xmp;txt;xml
    system.presServer=localnode
    system.urnNameSpace=urn:nbn:de:danrw

### cb

There are a couple of settings that relate strongly to the node concept, but are not related to business concepts in any way. Instead they relate to technical concepts only. Hence they merit their own category which is called "cb" which stands for ContentBroker settings.

    cb.serverSocketNumber=

In order to let peripheral components of DNSCore (primary use of course is DA-Web) talk to and have introspection into the application state of the ContentBroker, a server process is beeing established by the ContentBroker at startup. Any free port will do. As under normal circumstances DA-Web runs on the same node (i.r. localnode will suffice) as the ContentBroker, firewall issues could possibly be neglected.

    cb.implementation.grid=

The full and node mode installations of the ContentBroker require a grid component onto which they put and from which they retrieve the long term archive contents, which correspond to the AIP in OAIS terms, and always relate to containered files (.tar) on the technical level. At the moment there exist three implementations of grid subsystems, of which two relate to iRODS configurations in different modes.

    cb.implementation.grid=irodsGridFacade
    
If set to irodsGridFacade, the iRODS installation is assumed to be configured properly to run a one zone based grid.

block described below must be inserted to a working config.properties. 
    
    cb.implementation.grid=irodsFederatedGridFacade

As opposed to the irodsGridFacade, the irodsFederatedGridFacade assumes to have an iRODS system running which is configured in a federated (TODO link to documents) manner. Note: At the moment this feature is considered experimental until it is fully tested in a load test environment.

    cb.implementation.grid=fakeGridFacade
    
The fakeGridFacade is a simple implementation which resigns any third party subsystems but only the local file system.
It has been written primarily for purposes of testing or easy experimentation for evaluation or showcasing purposes.

asdf


    cb.implementation.distributedConversion=irodsDistributedConversionAdapter

    
asdf
    
    cb.implementation.repository=fedoraRepositoryFacade

asdf

    cb.bin.python=

Here you have to insert the command to run an instance of python (at the moment >= 2.7 is required). If you are sure the required command is globally visible in the environment (the shell or process) in which the ContentBroker.jar is intended to run, you simple can insert something as simple as "python" as a value. If this is not the case, for example if the packaging system of your distro has only python in a version < 2.7 and you have a self compiled version at another path
on your file system, you should insert the full path to the python binary as a value.


### irods

These settings are optional and must be set only if cb.implementation.grid or cb.implementaion.districutedConversion
are set to use the corresponding irods specific implementations. Nodes not using irods dont need these parameters.

    irods.user=rods
    irods.server=cihost
    irods.zone=c-i
    irods.default_resc=ciWorkingResource
    irods.pam=false 
    irods.keyStorePassword=
    irods.keyStorePath=
    irods.trustStorePath=

asdf

    irods.password=WpXlLLg3a4/S/iYrs6UhtQ== 

The password has to be encrypted with the password encryptor/decryptor which is part of the DNSCore project itself (if you haven't already, you can see the sub project [here](https://github.com/da-nrw/DNSCore/tree/master/PasswordEncryptor).


### fedora

    fedora.url=http://localhost:8080/fedora
    fedora.user=fedoraAdmin

adf

    fedora.password=BYi/MFjKDFd5Dpe52PSUoA==
    
The passwort has to be encrypted/decrypted with the PasswordEncryptor of DNSCore.
    

asdf

### elasticsearch 

    elasticsearch.index=portal_ci
    elasticsearch.hosts=localhost
    elasticsearch.cluster=cluster_ci

### uris

Independently if the repository functionality is used or not, these settings are needed:

    uris.file="http://data.danrw.de/file
    uris.cho=http://data.danrw.de/cho
    uris.aggr=http://data.danrw.de/aggregation
    uris.local=info:

## hibernateCentralDB.cfg.xml

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

## beans.xml

Depending on the mode of installation (f,p,n) the beans.xml can look a little different respectively. However here are described the building blocks which comprise the beans.xml:

### The General params section 

	<bean class="de.uzk.hki.da.core.IngestGate" id="ingestGate">
		<property name="workAreaRootPath" value="${localNode.workAreaRootPath}" />
		<property name="fileSizeFactor" value="3" />
		<property name="freeDiskSpacePercent" value="5" />
	</bean>

	<task:executor id="taskExecutor" pool-size="10"
		queue-capacity="20" rejection-policy="CALLER_RUNS" />
	<task:scheduled-tasks scheduler="taskScheduler">
		<task:scheduled ref="contentBroker" method="scheduleTask" fixed-delay="200" />
		<task:scheduled ref="ingestAreaScannerWorker" method="scheduleTask" fixed-delay="1000" />
		<task:scheduled ref="userAreaScannerWorker" method="scheduleTask" fixed-delay="1000" />
		<task:scheduled ref="integrityScannerWorker" method="scheduleTask" fixed-delay="20000" />
	</task:scheduled-tasks>
	<task:scheduler id="taskScheduler" pool-size="20" />


### The action engine related settings

	<util:map id="actionThreads">
		<entry key="DeleteObjectAction" value="3" />
		<entry key="RestartIngestWorkflowAction" value="3" />
		<entry key="IngestRegisterURNAction" value="3" />
		...
		<entry key="PIPGenObjectToWorkAreaAction" value="3" />
		<entry key="PIPGenCleanWorkAreaAction" value="3" />
	</util:map>

TODO description

	<util:list id="actionOrdering">
   		<value>DeleteObjectAction</value>
		<value>RestartIngestWorkflowAction</value>
		<value>RetrievalAction</value>
		...
		<value>PIPGenScanForPresentationAction</value>
		<value>PIPGenObjectToWorkAreaAction</value>
        <value>PostRetrievalAction</value>
	</util:list>
	
TODO description	

### The import secion

    <!-- beans.xml.full -->

    <import resource="classpath*:META-INF/beans-infrastructure.common.xml"/>
    <import resource="classpath*:META-INF/beans-infrastructure.identifier.xml"/>
    <import resource="classpath*:META-INF/beans-infrastructure.fedora.xml"/>
    <import resource="classpath*:META-INF/beans-infrastructure.irods.xml"/>

    <import resource="classpath*:META-INF/beans-workflow.presentation.xml"/>
    <import resource="classpath*:META-INF/beans-workflow.ingest.xml"/>
    <import resource="classpath*:META-INF/beans-workflow.retrieval.xml"/>
    <import resource="classpath*:META-INF/beans-workflow.pipgen.xml"/>
    <import resource="classpath*:META-INF/beans-workflow.other.xml"/>

You'll typically find this section at the top of a beans.xml file. When installing the application, the installer delivers it with a beans.xml which has the right imports for your version (node,pres,full). The imports activate certain components of the application which would be inactive otherwise. When the imports are active the application requires the presence of certain properties in the config.properties file, dependent on the modules activated. Compare the parameters section above.

## TODO

### Logfiles

Down below there is a discussion of how logging is configured in DNSCore. If you don't want this fine grained control, which is propably normal for startes with DNSCore, you can choose to install DNSCore with a default settings logback.xml (which can be retrieved from here) . The effects of using it are described here ...

    grid.log
    contentbroker.log
    
TODO description
    
    object-logs
    
TODO description
   
    
### cbTalk.


## logback.xml

In order to present its output, the ContentBroker.jar expects a ${CB_HOME}/conf/logback.xml file, though
the ContentBroker.jar would still work without the file, but without useful logging. 

### The package to appender section        
        
First we will discuss the section usually found at the bottom of the logback.xml file
        
	<logger name="de.uzk.hki.da.core" additivity="false" level="DEBUG">
                <appender-ref ref="FILE" />
        </logger>

        <logger name="de.uzk.hki.da.core.IngestAreaScannerWorker"  additivity="false" level="TRACE">
                <appender-ref ref="INGEST" />
        </logger>

		<logger name="de.uzk.hki.da.integrity" additivity="false" level="DEBUG">
                <appender-ref ref="INTEGRITY" />
        </logger>

        <logger name="de.uzk.hki.da.grid"  additivity="false" level="TRACE">
                <appender-ref ref="GRID" />
        </logger>

        <logger name="de.uzk.hki.da"  additivity="false" level="TRACE">
                <appender-ref ref="OBJECT" />
        </logger>

        <logger name="org.hibernate" level="INFO">
                <appender-ref ref="OBJECT" />
        </logger>
        <logger name="org.apache.activemq" level="ERROR">
                <appender-ref ref="FILE" />
        </logger>
        <root level="OFF" />




### The console appender

<configuration scan="true">
	<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
		<encoder>
			<pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
		</encoder>
	</appender>

### The grid appender

     <appender name="GRID" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>TRACE</level>
        </filter>
		<file>log/grid.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>log/grid.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
	    <encoder>
	        <pattern>%d [%thread] %-5level %logger{35} - %msg%n</pattern>
	    </encoder>
	</appender>   

### The ingest appender

     <appender name="INGEST" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>TRACE</level>
        </filter>
		<file>log/ingest.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>log/ingest.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
	    <encoder>
	        <pattern>%d [%thread] %-5level %logger{35} - %msg%n</pattern>
	    </encoder>
	</appender>   

### The integrity appender

     <appender name="INTEGRITY" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>TRACE</level>
        </filter>
		<file>log/integrity.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>log/integrity.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
	    <encoder>
	        <pattern>%d [%thread] %-5level %logger{35} - %msg%n</pattern>
	    </encoder>
	</appender>   

### The file appender

	<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
            <level>TRACE</level>
        </filter>
		<file>log/contentbroker.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>log/contentbroker.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
	    <encoder>
	        <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{35} - %msg%n</pattern>
	    </encoder>
	</appender>

### The object appender

	<appender name="OBJECT" class="ch.qos.logback.classic.sift.SiftingAppender">
		<discriminator>
			<key>object_id</key>
			<defaultValue>default-object-log</defaultValue>
		</discriminator>
		<sift>
			<appender name="FILE-${object_id}" class="ch.qos.logback.core.FileAppender">
				<file>log/object-logs/${object_id}.log</file>
				<encoder>
					<pattern>%d %level %logger{35} - %msg%n</pattern>
				</encoder>
			</appender>
		</sift>
	</appender>




</configuration>









