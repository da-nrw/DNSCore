# Administration - Interface Reference 

While we have an administration and user frontend called DA-Web which lets users (end users and administrators) interact in a dynamic manner, there are several configuration options of DNSCore which are considered static properties of the system. They are configured in the old fashioned way of good old configuration files which can be edited with your good old favorite linux editor. Easy! 

These artefacts comprise the interface to the ContentBroker with which administrators must learn to deal with in order to configure and run a proper node of a DNSCore based system. The document is structed that each configuration file is described extensively in its own passage.

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

## beans.xml

## logback.xml

## Logfiles
