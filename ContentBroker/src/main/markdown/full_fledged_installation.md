# Full fledged installation

## Prerequisites
 
* iRODS > 3.2
* ffmpeg
* You already have a fully working basic version running as described in 
[this](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/installation.md) document.

## Step by Step

Prepare your iRODS ruleset for DNSCore following 
[this](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/preparing_irods_for_dnscore.md) 
tutorial. 

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


