# Connecting iRODS and DNSCore

## Prerequisites

* iRODS > 3.2
* DNSCore specific iRODS configuration ([here](installation_irods.md))
* ContentBroker ([here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/installation_cb.md))



This document describes how to set up iRODS as a backend to an existing DNSCore installation.
Both systems connected, with the DNSCore beeing the business layer and iRODS beeing the storage layer,
form a fully operational node ready for production use. For the purpose of this document, the system is considered
as consisting only of this node, e.g. you can see it as a how to of setting up the master node of a system. For setup of
slave nodes or a consideration of other topologies (federation) see the notes at the bottom of this document.


    [irodsuser] - The irods user we will use to let the ContentBroker talk to the iRODS server.
    [irodspassword] - The password of this user.
    [irodszone] - The irods zone the ContentBroker will work in. Choose an appropriate name.
    
To let the grid component of the ContentBroker as a client know how to speak to the iRODS server set the 
following lines of your config.properties to match your iRODS configuration:

    irods.user=[irodsuser]
    irods.password=[encryptedirodspassword] (TODO show how to encrypt)
    irods.server=[nameOfYourIrodsServerInstance]
    irods.zone=[irodszone]

If we use iRODS as our backend, from the perspective of our application, we use it to serve two different purposes.
On the one hand we use it to replicate (during ingest for example) DIPs to other nodes on which the presentation repository runs. This function is represented by the interface "DistributedConversionHelper". On the other hand we use
it to build up a grid between different nodes to realize the necessary geographical distribution of a long term archive.

    cb.implementation.grid=irodsGridFacade
    cb.implementation.distributedConversion=irodsDistributedConversionAdapter
    
#### iRODS resources

In order to let iRODS replicate data, it must be placed on so called resources, which basically are paths on your file systems which you define to be a resource. This function is represented business-code-wise by the interface GridFacade. You can choose amongst different implementations of which we use our irods implementations. This is reflected by entries
in the config.properties, which you should open and edit now so that it looks like:

iRODS Servers (as well in federated or in resource server mode) know two types of resources:

1. "Cache" type resource having a small latency and being fast, for objects that have to be accessed frequently.
1. "Archive" type resource having longer latency, generally targeted at permanent storage and less frequent access.

We adhere to these iRODS principles and use one cache type of resource to fulfill the first of the abovementioned functions and the archive type resource for the storage where AIPs are
put onto and which should be a WORM device (for example tape storage)..

Create a working resource 

    iadmin mkresc [nameOfYourWorkingResource] "unix file system" cache [hostname] [vaultPathOfWorkingResources]

Create an archive resource

    iadmin mkresc [nameOfYourArchiveResource] "unix file system" archive [hostname] [vaultPathOfYourArchiveResource]

For both of your resources you have to choose an appropriate name and a vaultPath. A resource in iRODS most of the times
is a simple mapping to some random path on a file system which is mounted on your computer. All files and folders below
this path, which is called the "vaultPath" of the resource, belong to this resource and get mapped to an appropriate logical path by iRODS (the details don't matter here).


In order to work properly, two further things have to be done. First, for reasons explained later in this document the ContentBroker stores data to resource groups, not to resources directly, when working in its long term archive mode.
As a consequence, we have to create a resource group and add the recently created archive resource to it.

    iadmin mkgroup [nameOfYourArchiveResouceGroup]
    iadmin atrg [nameOfYourArchiveResourceGroup] [nameOfYourArchiveResource]

##### connect the ContentBroker

The ContentBroker needs to know with which resources it can work for each of its two modes.
For the distributedConversion mode this setting is needed:

    irods.default_resc=[nameOfYourWorkingRescource]
    localNode.workingResource=[nameOfYourWorkingResource]
    
For the long term archive mode, these settings are needed:

    cb.min_repls=1
    localNode.replDestinations=[nameOfYourArchiveResourceGroup]
    
These settings mean that the ContentBroker will replicate the AIPs to exactly one resource location which
is denoted by [nameOfYourArchiveResourceGroup]. The min_repls setting, then, denotes, that it considers
long term archival done when one replica of an AIP is existent in the grid. 
Of course, these minimal settings are only for instructional purposes. On a real productions grid, we would need
at least three different geographically districuted locations.

### Understanding the mapping of iRODS resources to ContentBroker Areas

Coming from the Getting Started Guide, where you already set up your ContentBroker, you should have a directory 
layout which looks like this:

    [...]/storage/
                    user/
                    ingest/
                    work/
                    pip/
                        institution/
                        public/
                    grid/
        
This means that somewhere on your filesystem is the whole storage tree as needed by the ContentBroker when 
working without iRODS. When working with iRODS, we need to move some of the folders below the workingResourceVaultPath.

    [...]/storage/
                            user/
                            ingest/
    [vaultPathOfYourWorkingResource]/
                            work/
                            pip/ 
                            grid/

The following sketch illustrates the mapping between iRODS resources and file system locations:

![](https://raw.github.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/different_views.jpg)

You now have to adjust the folder structure to reflect this. First of all, for reasons explained in [here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/processing_stages.md) 
(at the sections UserArea and IngestArea),user and ingest get moved to an own folder. The workingResource and
archiveResource folders get created to match the iRODS resources we will create in this section.

Edit the config.properties to reflect your changes:                               

    localNode.userAreaRootPath=[...]/storage/user
    localNode.ingestAreaRootPath=[...]/storage/ingest
    localNode.workAreaRootPath=[vaultPathOfYourWorkingResource] 
    localNode.gridCacheAreaRootPath=[vaultPathOfYourWorkingResource]/grid (the chosen phys. path must be subdir of vaultpath of iRODS workingResource)

### Adjust iRODS installation

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

 In case you're running the resource server mode, the resource names are your repl_destinations names in config.properties. In case of forming a federation, zone_names are listed in repl_destinations. 

Please note the settings of your iRODS installation, as they're needed for config.properties of CB and DA-Web.

1. danrw.re file Template: https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/rules/danrw.re

Alter "default resource" settings in core.re and in danrw.re for apropiate settings on your system as they might point
to some dummy resources. 


## TODO other stuff

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

