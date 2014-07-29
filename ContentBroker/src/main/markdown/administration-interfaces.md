# Administration - Interface Reference 

While we have an administration and user frontend called DA-Web which lets users (end users and administrators) interact in a dynamic manner, there are several configuration options of DNSCore which are considered static properties of the system. They are configured in the old fashioned way of good old configuration files which can be edited with your good old favorite linux editor. Easy! 

These artefacts comprise the interface to the ContentBroker with which administrators must learn to deal with in order to configure and run a proper node of a DNSCore based system. The document is structed in that way that each configuration file is described extensively in its own passage.

**Note** that for the purporse of the discussion of this document, we call the ContentBroker installation directory ${CB_HOME}

**Note** that, as the interfaces can change from time to time, in doubt you should look at the right version of this document. Go to the releases page and you'll find the source tree for this specific version. 

## Application configuration

    ${CB_HOME}/conf/config.properties

The file config.properties is a necessary part of every DNSCore installation. It has always to be stored
in the conf directory directly under the DNSCore main directory (which is the one that contains the ContentBroker.jar).
With the exception of the database settings, the config properties is intended to hold the settings necessary to
let your ContentBroker know how its environment is configured. Directory paths, adapter configurations etc. are alle stored in this single file.

The file is logically devided into several blocks of which each has a prefix to mark a property as beloning to the
according block:

### localNode

Example from [config.properties.ci](../conf/config.properties.ci)

    localNode.admin_email=da-nrw-notifier@uni-koeln.de
    localNode.userAreaRootPath=/ci/storage/UserArea
    localNode.ingestAreaRootPath=/ci/storage/IngestArea 
    localNode.workAreaRootPath=/ci/storage/WorkArea
    localNode.gridCacheAreaRootPath=/ci/storage/GridCacheArea
    localNode.workingResource=ciWorkingResource 
    localNode.replDestinations=ciArchiveResourceGroup
    localNode.name=localnode
    localNode.id=


The localNode block contains all the settings related to the configuration of the machine itself as well as information
regarding the administrator role. Note that the localNode is the implementation of the node concept of the domain business model.

    localNode.userAreaRootPath=
    localNode.ingestAreaRootPath=
    localNode.workAreaRootPath=
    localNode.gridCacheAreaRootPath=
    
These four properties should be set to the absolute physical paths of the mount points in the file system which hold
the aip data during the ContentBroker workflow processing stages. If you don't know anything about the concept of areas
in context of DNSCore, have a look at [this](processing_stages.md) document. It doesn't matter if the paths carry a trailing slash or not. But make sure the paths are absolute and not relative.

    localNode.replDestinations=ciArchiveResourceGroup
   
For this setting you can insert either a single destination or a comma seperated list of several destinations which are considered endpoints for the node. These are the nodes holding the secondary copies of objects for long term archival.
It depends on the implementations of the underlying grid what these node names TODO(why not node names?, here is a discrepancy between the business model side and the technical implementation details) refer to. In a typical iRODS (zone) installation these are the names of resource groups to which the objects can be replicated to.

    localNode.workingResource=

TODO does it belong on the node section?
    
    localNode.name=localnode

This property lets you specify the name which identifies the node in the system. It is used for example for synchronizing jobs between nodes (by using the DistributedConversionAdapter). While the name is theoretically arbitrary, it is recommended to use the fully qualified domain name of the node, which typically should equal the irods.server setting on iRODS based node installations (see description of irods settings below).

    localNode.id= 
    
This setting must contain the integer value primary key of the nodes correspoding entry in the nodes table of the object db.

    localNode.admin_email=da-nrw-notifier@uni-koeln.de

The email address of the administrator responsible for the node. Note that who is meant here is the administrator in the domain model sense, the person who takes responsibility for the working of the node and supervision of packages and workflow. It can be but has not necessarily to be the same person who is responsible for maintaining the machine in the infrastructure sense.

### system

Example from [config.properties.ci](../conf/config.properties.ci)

    system.min_repls=1
    system.sidecar_extensions=xmp;txt;xml
    system.presServer=localnode
    system.urnNameSpace=urn:nbn:de:danrw
    system.uris.local=info:
    system.uris.file=http://data.danrw.de/file
    system.uris.cho=http://data.danrw.de/cho
    system.uris.aggr=http://data.danrw.de/aggregation
    system.uris.openCollectionName=danrw
    system.uris.closedCollectionName=danrw-closed
    system.emailFrom=noreply@danrw.de

System properties apply to all nodes comprising the so called "system" (TODO link to glossary or data model). As
a consequence, for a properly working system all the nodes system properties have to be exactly the same. If the
nodes are maintained by different administrators (perhaps if the nodes are distributed geographically or organisationally) the administrators must agree upon the common setings.

    system.min_repls=1
    
The minimum number of replications the ContentBroker asks the grid component for to fulfill to consider a copy (an AIP) long term archived. Normally it is 3.
    
    system.sidecar_extensions=xmp;txt;xml

TODO describe   

    system.presServer=localnode
    
This is the name of the node which hosts the presentation repository. It must be the same name which is configured on the conf/config.properties of the presentation repositories ContentBroker installation.
    
    system.urnNameSpace=
    
TODO explanation

    system.uris.local=
    
This path gets added to all metadata streams which get updated during creation of the PIPs. Local file system paths inside metadata which point to the files inside the packages get prefixed by system.uris.local during metadata update. It denotes the path to every single datestream in the presentation repository.

    system.uris.file=
    system.uris.cho=
    system.uris.aggr=

First note that these three properties are only needed on installations of type "pres" or "full". Normal node installations do not need them. DNS makes it possible to feed a search index with different references to objects. Objects are split into aggregations, files, and chos. 

    system.openCollectionName=
    system.closedCollectionName=

These settings also are only necessary on nodes of type "pres" or "full". The open collection is the collection of the presentation repository which hosts the public datastreams. The closed collection is the collection which hosts the datastream which can only be accessed by the institutions themselves.

### cb

Example from [config.properties.ci](../conf/config.properties.ci)

    cb.serverSocketNumber=4455
    cb.implementation.grid=irodsGridFacade
    cb.implementation.distributedConversion=irodsDistributedConversionAdapter
    cb.implementation.repository=fedoraRepositoryFacade
    cb.bin.python=/ci/python/python

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

    cb.implementation.distributedConversion=

The ContentBroker is able to synchronize jobs between nodes in the system. To accomplish this, the unpacked objects in the WorkArea can be transfered to another node and an action can then create a job for the other node to execute which will then work with the replicated data. So two or more nodes can modify the objects state sequentially. The setting lets you choose an implementation which provides the necessary replicaton facilities.

    cb.implementation.distributedConversion=irodsDistributedConversionAdapter

When selecting the irodsDistributedConversionAdapter, an installation of iRODS (in zone mode) is used. The irods settings in config.properties have to be present (also see irods section below).
    
    cb.implementation.distributedConversion=fakeDistributedConversionAdapter

This implementation is useful for testing or evaluation purposes.
    
    cb.implementation.repository=

This setting determines the connection to the presentation repository subsystem.
    
    cb.implementation.repository=fedoraRepositoryFacade
    
When choosing fedoraRepositoryFacade, Fedora is used as the presentation layer subsystem.

    cb.implementation.repository=fakeRepositoryFacade

This implementation is useful for testing or evaluation purposes.

    cb.bin.python=

Here you have to insert the command to run an instance of python (at the moment >= 2.7 is required). If you are sure the required command is globally visible in the environment (the shell or process) in which the ContentBroker.jar is intended to run, you simple can insert something as simple as "python" as a value. If this is not the case, for example if the packaging system of your distro has only python in a version < 2.7 and you have a self compiled version at another path
on your file system, you should insert the full path to the python binary as a value.

### irods

Example from [config.properties.ci](../conf/config.properties.ci)

    irods.user=rods
    irods.password=WpXlLLg3a4/S/iYrs6UhtQ==
    irods.server=cihost
    irods.zone=c-i
    irods.default_resc=ciWorkingResource
    irods.pam=false
    irods.keyStorePassword=
    irods.keyStorePath=
    irods.trustStorePath=

These settings are optional and must be set only if cb.implementation.grid or cb.implementaion.districutedConversion
are set to use the corresponding irods specific implementations. Nodes not using irods dont need these parameters.

    irods.user=
    irods.server=
    irods.zone=
    irods.default_resc=
    irods.pam=false 
    irods.keyStorePassword=
    irods.keyStorePath=
    irods.trustStorePath=

asdf

    irods.password= 

The password has to be encrypted with the password encryptor/decryptor which is part of the DNSCore project itself (if you haven't already, you can see the sub project [here](https://github.com/da-nrw/DNSCore/tree/master/PasswordEncryptor).

### fedora

Example from [config.properties.ci](../conf/config.properties.ci)

    fedora.url=http://localhost:8080/fedora
    fedora.user=fedoraAdmin
    fedora.password=BYi/MFjKDFd5Dpe52PSUoA==

When the application has been installed in one of wither pres or full mode, the presentation module is activated via its respective import in the import section of the beans.xml (see down below).

    fedora.url=
    fedora.user=

In pres or full mode the ContentBroker and the presentation repository are hosted on one and the same machine. Fedora runs on a tomcat and fedora.url points to the http://... adress of Fedora while fedora.user is a fedora user prepared
for usage by the ContentBroker.

    fedora.password=
    
The passwort has to be encrypted/decrypted with the PasswordEncryptor of DNSCore.

### elasticsearch 

Example from [config.properties.ci](../conf/config.properties.ci)

    elasticsearch.index=portal_ci
    elasticsearch.hosts=localhost
    elasticsearch.cluster=cluster_ci

The elasticsearch settings only are necessary on nodes which provide presentation repository functionality, which is enabled by choosing either the full or pres setting during installation.

    elasticsearch.index=
    elasticsearch.hosts=
    elasticsearch.cluster=
    
Make sure you insert the same settings you have used during your elasticsearch installation.


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

## Application module configuration

${CB_HOME}/conf/beans.xml

Depending on the mode of installation (f,p,n) the beans.xml can look a little different respectively. However here are described the building blocks which comprise the beans.xml:

### The General params section 

#### Ingest Gate

The IngestGate is a special feature which was designed with the purpose of protecting the [WorkArea](processing_stages.md#WorkArea)'s file system from overflow. The ContentBroker's workflow engine recieves orders from various other components that always have the consequence that data from different resources must be fetched to the WorkArea. There the data get unpacked, processed and repacked.

Example from [beans.xml.full](../xml/beans.xml.full)

	<bean class="de.uzk.hki.da.core.IngestGate" id="ingestGate">
		<property name="workAreaRootPath" value="${localNode.workAreaRootPath}" />
		<property name="fileSizeFactor" value="3" />
		<property name="freeDiskSpacePercent" value="5" />
	</bean>

The workAreaRootPath is set automatically to the correspoding variable from config.properties so you don't have to take care of that.

    <property name="freeDiskSpacePercent" value="5" />

This is the value an administrator has to consider setting right for his machine. It defines how many percent of the WorkArea's file system have to be vacant in order to allow the ContentBroker to touch new jobs and fetch the corresponding packages from the IngestArea or long term resources to unpack and process them. When setting up a new node, a good value to start could be 50. When gained a little bit of experience with the ContentBroker on the particular machine, the value later can be refined. 

    <property name="fileSizeFactor" value="3" />

This value is tweakable, but does not depend on the machine but on the workflow and the packages' data. A value of 3 means that it is expected that an incoming package gets (during the Ingest workflow) bloated temporarily to three times its original size. Usually let the value as it is and only modify it when you have strong evidence that overflows of the file system occur due to huge package sizes.

#### Worker scheduler

Example from [beans.xml.full](../xml/beans.xml.full)

	<task:executor id="taskExecutor" pool-size="10"
		queue-capacity="20" rejection-policy="CALLER_RUNS" />
	<task:scheduled-tasks scheduler="taskScheduler">
		<task:scheduled ref="contentBroker" method="scheduleTask" fixed-delay="200" />
		<task:scheduled ref="ingestAreaScannerWorker" method="scheduleTask" fixed-delay="1000" />
		<task:scheduled ref="userAreaScannerWorker" method="scheduleTask" fixed-delay="1000" />
		<task:scheduled ref="integrityScannerWorker" method="scheduleTask" fixed-delay="20000" />
	</task:scheduled-tasks>
	<task:scheduler id="taskScheduler" pool-size="20" />

The ContentBroker has a scheduling mechanism which starts worker threads periodically.


### The action engine related settings

Example from [beans.xml.full](../xml/beans.xml.full)

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

	





