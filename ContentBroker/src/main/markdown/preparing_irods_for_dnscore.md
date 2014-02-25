	/*
	  DA-NRW Software Suite | ContentBroker
	  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
	  Universität zu Köln
	
	  This program is free software: you can redistribute it and/or modify
	  it under the terms of the GNU General Public License as published by
	  the Free Software Foundation, either version 3 of the License, or
	  (at your option) any later version.
	
	  This program is distributed in the hope that it will be useful,
	  but WITHOUT ANY WARRANTY; without even the implied warranty of
	  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	  GNU General Public License for more details.
	
	  You should have received a copy of the GNU General Public License
	  along with this program.  If not, see <http://www.gnu.org/licenses/>.
	*/
	
	
# Preparing iRODS for DNSCore

DNScore uses iRODS as storage layer. Therefore at least a running instance of iRODS is needed
for DNSCore to perform. To get a deeper overview of the iRODS System please refer to the 
documentation available at http://www.irods.org . 

The storage layer is separated of ContentBroker's internal business logic, the interface is composed by 
GridFace abstract classes and its respective implementations. The only thing GridFacade needs
to know is an instance of a storage policy which has to be achieved and the logical pathname (address) the
object is stored under.  

## Setup iRODS

To successfully run ContentBroker/DNSCore with iRODS, you have to prepare your running installation of iRODS.
Please start customizing iRODS install after having done a complete check of your iRODS installation: you should be familiar with 
iRODS Cli-commands esp. irepl, ils, iput, irsync, iget. As iRODS Admin you have to be familiar as well with command iadmin. 
 
Please note: iRODS can be setup to use a "federation" of iRODS Servers forming a mostly independent "zone" as well as the concept of 
having one Zone with several resource servers. Please refer to the iRODS Documentation about this. DNSCore supports both operational 
modes.  
All iRODS Servers (as well in federated or in resource server mode) need at least to have two resources:

1. "Cache" resource having a small latency and being fast, to store all objects after they are put to the grid.  
1. "Archive" resource having longer latency (tape device or mount point) for acessing the WORM devices of long term storage.

The archive resource has to part of an named resource group. In case you're running the resource server mode, the 
resource names are your repl_destinations names. In case of forming a federation, zone_names are your repl_destinations. 

Please note the settings of your iRODS installation, as they're needed for config.properties of CB.

### Adding and changing the RuleSet

iRODS works with event based triggers being fired on certain actions. Additionally iRODS has the ability to automatically 
perform some time based actions (performed by the RuleEngine of Master ICAT). To support event based rules needed by 
DNSCore and to provide needed actions for the GridFacade, it is needed to add the RuleSet to the reConfig rule base, located 
at 

    iRODS/server/config/server.config
  

Please add the entry 

    reRuleSet   danrw,core


And store the corresponding file danrw.re in:


    iRODS/server/config/reConfigs

The file danrw.re must be changed to your local appropiate settings. Please refer carefully to the iRODS Documentation
about needed change of other parameters, as wrong parameters could harm your system!



## Microservices

As ContentBroker has now an extended and comfortable interface for interacting with all kinds of 
iRODS Servers (iRODSSystemConnector) based on the JARGON interface provided by RENCI, DNSCore
is deployed without the need for installed microservices anymore. 



## Connection DNSCore to the Storage Layer

In the getting started document you created a basic folder structure which looks like this:

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

To let the core component of DNSCore know how to speak to the grid set the following properties:


    localNode.workingResource=localhost TODO ......
	grid.implementation=IrodsGridFacade
	implementation.distributedConversion=IrodsDistributedConversionAdapter
	
TODO ffmpeg




### Adding users to DNSCore

DNSCore needs a technical user for the application ContentBroker as well as the Contractors 
being Users of iRODS. 

DNSCore delivers a bash script for easily creating contractors and iRODS directories. 

