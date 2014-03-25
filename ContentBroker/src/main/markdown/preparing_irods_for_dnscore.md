# Preparing iRODS for DNSCore

DNScore uses iRODS as storage layer. Therefore at least a running instance of iRODS is needed
for DNSCore to perform in testing or pre-productional usage. 

You are able to install and run DNSCore without having iRODS installed, due to have a so called 
"FakeGridFacade" implementation configured in your config.properties. This os ONLY recommended in development and
"Getting started" usage of DNSCore. Please refer to that respective documentation.

In the following parts we assume 

1. You have read the documenation available under www.irods.org (e.g. the read the e-Book "iRODS Primer")
2. You have already have at least one iRODS Server at your site
2. It is running and you have already performed some basic tests. 
3. You are able to create resources. 

## Introduction

The storage layer is separated of ContentBroker's internal business logic, the interface is composed by 
GridFace abstract classes and its respective implementations. The only thing GridFacade needs
to know is an instance of a storage policy which has to be achieved and the logical pathname (address) the
object is stored under. This helps to seperate the concerns of DNSCore between ContentBroker's business logic and the Storage layer. 

## iRODS

The reasons why we have choosen iRODS as a storage layer framework were

1. It is open source
2. broadly being used in academic projects at large data scales 
3. being able to connect heterogenous existing hardware systems (act as abstraction layer)
4. "out-of-the-box" capabilities for replication, maintenance and low-level bitstream verification.
5. has a vivid community

The version described here is community iRODS Version (3.X), you may consider also the e-iRODS Version. www.eirods.org but this not tested with DNSCore.

Several hardware platforms are supported by iRODS "out-of-the-box", but having a standard "mount-point" (unix file system) is always a good start. Tape devices not being able to provide such, may be connected via MSS compound devices and may need special configuration. 

## Setup iRODS

To successfully run ContentBroker/DNSCore with iRODS, you have to prepare your running installation of iRODS.
Please start customizing iRODS install after having done a complete check of your iRODS installation: you should be familiar with 
iRODS Cli-commands esp. 

    irepl, ils, iput, irsync, iget

As iRODS Admin (of each zone being used) you have to be familiar as well with command 

    iadmin
 
Please note: iRODS can be setup to use a "federation" of iRODS Servers forming a mostly independent "zones" as well as the concept of 
having one Zone with several resource servers. Please refer to the iRODS Documentation about this. DNSCore supports both operational modes. 

Each Zone needs at least one database (so called ICAT Server). The use of Postgres is encouraged here.  

All iRODS Servers (as well in federated or in resource server mode) need at least to have two resources:

1. "Cache" resource having a small latency and being fast, to store all objects after they are put to the grid.  
1. "Archive" resource having longer latency (tape device or mount point for storage devices) for acessing the WORM devices of long term storage. 

Please take a look at documentation at www.irods.org how to create iRODS resources. 

The archive resource has to part of an named resource group. In case you're running the resource server mode, the 
resource names are your repl_destinations names in config.properties. In case of forming a federation, zone_names are listed in repl_destinations. 

Please note the settings of your iRODS installation, as they're needed for config.properties of CB and DA-Web.

## Prerequisites

1. running iRODS Server > 3.2 
1. danrw.re file Template: https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/rules/danrw.re

## Open needed Ports

There are several ports which needed to be opened on your Firewall for iRODS to perform. It is possible to set the desired port numbers in the iRODS install scripts, but we recommend the standard ports. 

TCP:

    1247 (iRODS)
    20.000-20.199 (iRODS)
    5432 (DNS DB)
    
opened exclusively to all participants of your desired storage layer nodes.

Other ports such as

    8080
    80
    443
    
could be openend as you might need them, but hey might depend on your setup. Please disable all running desktop firewalls (e.g. iptables) on your server as they may cause problems.  

## Default Resource

Alter "default resource" settings in core.re and in danrw.re for apropiate settings on your system as they might point
to some dummy resources. 

## Adding and changing the RuleSet

iRODS works with event based triggers being fired on certain actions. Additionally iRODS has the ability to automatically 
perform some time based actions (performed by the RuleEngine of Master ICAT). To support event based rules needed by 
DNSCore and to provide needed actions for the GridFacade, it is needed to add the RuleSet to reConfig rule base. The rule base config is located at:

    iRODS/server/config/server.config
  

Please add the entry on all connected servers by changing line 

    reRuleSet   danrw,core

And store the corresponding file danrw.re in:


    iRODS/server/config/reConfigs

The file danrw.re must be changed to your local appropiate settings. 

Please refer carefully to the iRODS Documentation
about needed change of other parameters, as wrong parameters could serverly harm your DNS system! There is no test if a ruleBase is operating well, while this file being parsed on demand whenever actions being fired. In case of severe  
errors error, commands like 

    ils 
    
will return with RE_PARSER_ERROR. Any change done to ruleBase should be followed issueing at least this command. There are many more actions being neccessary or at least interesting to implement, please consider reading the documenation in these files as well. 

## Connecting DNSCore to the Storage Layer

As ContentBroker has now an extended and comfortable interface for interacting with 
iRODS Servers (federated and single zone based architectures) based on the JARGON interface provided by RENCI (see https://code.renci.org/gf/project/jargon/) and our implematations of GridFacade, DNSCore
is deployed without the need for installed C-microservices for iRODS anymore.

In order to work together, you just have to follow the steps outlined in the following paragraphs.


In the getting started document you have already created a basic folder structure which looks like this:

    [somewhere]/storage/
                    user/
                    ingest/
                    work/
                    pip/
                        institution/
                        public/
                    aip/ 
                    
Now, select a partition or filemount on your box which is separated from the partition you let the ContentBroker
do his work. The reason for this are explained 
[here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/processing_stages.md) 
(at the sections UserArea and IngestArea).

Move the user/ and ingest/ directories already created including the TEST subfolders to a location at the other partition
so that you have a directory structure like this:

    [otherPartition]/[location]/
                                user/
                                     TEST/
                                          incoming/
                                          outgoing/
                                ingest/
                                     TEST/
    
In the following the steps of the "Preparing iRODS for DNSCore" document, you already have created a cache resource.
This is the iRODS resource that helps the ContentBroker work together with ContentBroker other instances on other nodes
when working with unpacked objects. Make sure that the vault path of this cache resource is pointed at 
the root of your storage location, which you either can do by moving your storage folder to the path denoted
by the resources vault path or by making the resources vault path point at your storage location. Either way, you
should end up having something like that:

    [vaultPathOfIrodsResource]/
                               work/
                                    TEST/
                               pip/
                                   institution/
                                               TEST/
                                   public/
                                          TEST/
                               aip/ 
                                   TEST/
                                   
As the folders exist now physically, iRODS has to know about them, so execute the following steps

    imkdir [zonePath]/work
    imkdir [zonePath]/work/TEST
    imkdir [zonePath]/pip
    imkdir [zonePath]/pip/institution
    imkdir [zonePath]/pip/institution/TEST
    imkdir [zonePath]/pip/public
    imkdir [zonePath]/pip/public/TEST
    imkdir [zonePath]/aip                               
    imkdir [zonePath]/aip/TEST
	
Finally edit the config.properties to reflect your changes:                               

    localNode.userAreaRootPath=[otherPartition]/[location]/user
    localNode.ingestAreaRootPath=[otherPartition]/[location]/ingest
    localNode.workAreaRootPath=[vaultPathOfIrodsResource]/work
    localNode.dipAreaRootPath=[vaultPathOfIrodsResource]/pip
    localNode.gridCacheAreaRootPath=[vaultPathOfIrodsResource]/aip

                                 
To let the grid component know how to speak to the iRODS server set the 
following properties to match your iRODS configuration:

    irods.user=[yourIrodsUser]
    irods.password=[encryptedIrodsPasswd] (TODO show how to encrypt)
    irods.server=[domainNameOfYourServer]
    irods.zone=[yourZoneName]
    irods.default_resc=[nameOfYourCacheResc]

To let the core component of DNSCore know how to speak to the grid set the following properties (esp. when you followed the Getting Started Tutorial, the following parameters might point to some fake Adapters):

    localNode.workingResource=localhost TODO ......
    grid.implementation=IrodsGridFacade
    implementation.distributedConversion=IrodsDistributedConversionAdapter
	

### Adding users to DNSCore

DNSCore needs a technical user for the application ContentBroker as well as the Contractors 
being Users of iRODS. 

DNSCore delivers a bash script for easily creating contractors and iRODS directories. 

