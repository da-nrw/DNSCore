# Connecting iRODS and DNSCore

This document describes how to set up iRODS as a backend to an existing DNSCore installation.
Both systems connected, with the DNSCore beeing the business layer and iRODS beeing the storage layer,
form a fully operational node ready for production use. For the purpose of this document, the system is considered
as consisting only of this node, e.g. you can see it as a how to of setting up the master node of a system. For setup of
slave nodes or a consideration of other topologies (federation) see the notes at the bottom of this document.

## Why iRODS

DNScore uses iRODS as storage layer. The reasons why we have choosen iRODS as a storage layer framework were

1. It is open source
2. broadly being used in academic projects at large data scales 
3. being able to connect heterogenous existing hardware systems (act as abstraction layer)
4. "out-of-the-box" capabilities for replication, maintenance and low-level bitstream verification.
5. has a vivid community

The version described here is community iRODS Version (3.X), you may consider also the e-iRODS Version. www.eirods.org but this not tested with DNSCore.

Several hardware platforms are supported by iRODS "out-of-the-box", but having a standard "mount-point" (unix file system) is always a good start. Tape devices not being able to provide such, may be connected via MSS compound devices and may need special configuration.

In the following parts we assume 

1. You have read the documenation available under www.irods.org (e.g. the read the e-Book "iRODS Primer")
1. You have already experimented with iRODS at a local developer box and you are able to use it.
1. You should be familiar with


    iRODS Cli-commands esp. 
    irepl, ils, iput, irsync, iget
    iadmin

1. You are able to create resources (Please take a look at documentation at www.irods.org how to create iRODS resources). 

## Setup ContentBroker

The storage layer is separated of ContentBroker's internal business logic. The interface is composed by the
GridFacade abstract class and its respective implementations, to separate concerns. By use of this inteface the business code can access objects via logical names whitout knowing of the underlying storage system (which in this case is iRODS).

As ContentBroker has now an extended and comfortable interface for interacting with 
iRODS Servers (federated and single zone based architectures) based on the JARGON interface provided by RENCI (see https://code.renci.org/gf/project/jargon/) and our implematations of GridFacade, DNSCore
is deployed without the need for installed C-microservices for iRODS anymore.

In order to connect the two systems to prepare a node for production use, we assume that you already have set up
the ContentBroker as described in the getting started [guide](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/getting_started.md).

## Setup iRODS

Set up a basic iRODS > 3.2 installation with one default resource of type cache, pointing to "somewhere" (as described
in the getting started document). Make sure the installation is installed as ICAT-Enabled. 


iRODS Servers (as well in federated or in resource server mode) know two types of resources:

1. "Cache" type resource having a small latency and being fast, for objects that have to be accessed frequently.
1. "Archive" type resource having longer latency, generally targeted at permanent storage and less frequent access.

We adhere to these iRODS principles and use one cache type of resource as storage layer backend for the
WorkArea and DIPArea, where objects are processed by the DNSCore and one archive type resource where AIPs are
put onto and which should be a WORM device (for example tape storage).

### Change your folder layout

In the getting started document you have already created a basic folder structure which looks like this:

    [somewhere]/storage/
                    user/
                    ingest/
                    work/
                    pip/
                        institution/
                        public/
                    aip/ 

You now have to adjust this directory structure. First of all, for reasons explained in [here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/processing_stages.md) 
(at the sections UserArea and IngestArea),user and ingest get moved to an own folder. The workingResource and
archiveResource folders get created to match the iRODS resources we will create in this section.

    [somewhere]/archiveResource/
    [somewhere]/transferResource/
                            user/
                                 TEST/
                            ingest/
                                 TEST/
    [somewhere]/workingResource/
                            work/
                                 TEST/
                            pip/
                                 institution/
                                        TEST/
                            pip/ 
                                 public/
                                        TEST/
                            grid/
                                 TEST/

Create a working resource 

    iadmin mkresc [nameYourWorkingResource] "unix file system" cache [hostname] [somewhere]/workingResource

Create an archive resource

    iadmin mkresc [nameYourArchiveResource] "unix file system" archive [hostname] [somewhere]/archiveResource

and a resource group and make the recently created archive resource to be part of that resource group:




### Creating the resources

 The archive resource has to part of an named resource group. In case you're running the resource server mode, the resource names are your repl_destinations names in config.properties. In case of forming a federation, zone_names are listed in repl_destinations. 

Please note the settings of your iRODS installation, as they're needed for config.properties of CB and DA-Web.

1. danrw.re file Template: https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/rules/danrw.re

Alter "default resource" settings in core.re and in danrw.re for apropiate settings on your system as they might point
to some dummy resources. 

### Adding and changing the RuleSet

iRODS works with event based triggers being fired on certain actions. Additionally iRODS has the ability to automatically 
perform some time based actions (performed by the RuleEngine of Master ICAT). To support event based rules needed by 
DNSCore and to provide needed actions for the GridFacade, it is needed to add the RuleSet to reConfig rule base. The rule base config is located at:

    iRODS/server/config/server.config
  
Please add the entry on all connected servers by changing line 

    reRuleSet   contentbroker,core

And store the corresponding file [danrw.re](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/rules/danrw.re) as:

    iRODS/server/config/reConfigs/contentbroker.re

The file contentbroker.re must be changed to your local appropiate settings. 

    acDataDeletePolicy {ON($rescName == "") {msiDeleteDisallowed; }}         -- 
    acGetReplDests(*replDests) { replDests=""; }                             --
    acGetMyNode(*myNode,*myServer){ *myNode =""; *myServer ="" }             --
    acGetNodeAdmin(*email){ *email = "" }                                    --

Restart the iRODS server and check if it runs properly by typing in 

    ils
    
In case there is somethin wrong it will return a RE_PARSER_ERROR.
    
Please refer carefully to the iRODS Documentation
about needed change of other parameters, as wrong parameters could serverly harm your DNS system! There is no test if a ruleBase is operating well, while this file being parsed on demand whenever actions being fired. There are many more actions being neccessary or at least interesting to implement, please consider reading the documentation in these files as well. 


## Connecting DNSCore to the Storage Layer

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

## Open needed Ports

In order to connect several nodes or setup an irods which connects to a different master node,
you have to open several ports.

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

## Setting up a node topology

Please note: iRODS can be setup to use a "federation" of iRODS Servers forming a mostly independent "zones" as well as the concept of 
having one Zone with several resource servers. Please refer to the iRODS Documentation about this. 

DNSCore supports both operational modes, depending on your

    grid.implemenation 
    
Parameter in your config.properties. Possible values are so far:

    grid.implementation=fakeGridFacade
    grid.implementation=irodsGridFacade
    grid.implementation=federatedGridFacade

Each Zone needs at least one database (so called ICAT Server). The use of Postgres is encouraged here. 


