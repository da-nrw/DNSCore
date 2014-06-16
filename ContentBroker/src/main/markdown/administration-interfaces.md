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

    localNode.userAreaRootPath=/ci/storage/UserArea
    localNode.ingestAreaRootPath=/ci/storage/IngestArea
    localNode.workAreaRootPath=/ci/storage/WorkArea
    localNode.gridCacheAreaRootPath=/ci/storage/GridCacheArea
    
asd
    
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
    cb.implementation.grid=irodsGridFacade
    cb.implementation.distributedConversion=irodsDistributedConversionAdapter
    cb.implementation.repository=fedoraRepositoryFacade
    cb.bin.python=/ci/python/python

### irods

    irods.user=rods
    irods.password=WpXlLLg3a4/S/iYrs6UhtQ== 
    irods.server=cihost
    irods.zone=c-i
    irods.default_resc=ciWorkingResource
    irods.pam=false 
    irods.keyStorePassword=
    irods.keyStorePath=
    irods.trustStorePath=

### fedora

    fedora.url=http://localhost:8080/fedora
    fedora.user=fedoraAdmin
    fedora.password=BYi/MFjKDFd5Dpe52PSUoA==

### elasticsearch 

    elasticsearch.index=portal_ci
    elasticsearch.hosts=localhost
    elasticsearch.cluster=cluster_ci

### uris

    uris.file="http://data.danrw.de/file
    uris.cho=http://data.danrw.de/cho
    uris.aggr=http://data.danrw.de/aggregation
    uris.local=info:


## beans.xml

## logback.xml

## Logfiles
