## config.properties

The file config.properties is a necessary part of every DNSCore installation. It has always to be stored
in the conf directory directly under the DNSCore main directory (which is the one that contains the ContentBroker.jar).
With the exception of the database settings, the config properties is intended to hold the settings necessary to
let your ContentBroker know how its environment is configured. Directory paths, adapter configurations etc. are alle stored in this single file.

The file is logically devided into several blocks of which each has a prefix to mark a property as beloning to the
according block:

### localNode

The localNode block contains all the settings related to the configuration of the machine itself as well as information
regarding the administrator role.

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

    system.min_repls=1
    system.sidecar_extensions=xmp;txt;xml
    system.presServer=localnode
    system.urnNameSpace=urn:nbn:de:danrw

### cb

    cb.serverSocketNumber=4455

asdf

    cb.implementation.grid=irodsGridFacade
    cb.implementation.distributedConversion=irodsDistributedConversionAdapter
    
asdf
    
    cb.implementation.repository=fedoraRepositoryFacade

asdf

    cb.bin.python=/ci/python/python

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

asdf

### fedora

    fedora.url=http://localhost:8080/fedora
    fedora.user=fedoraAdmin

adf

    fedora.password=BYi/MFjKDFd5Dpe52PSUoA==

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
