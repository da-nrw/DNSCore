# Setting up a continuous integration node

## Prerequisites

* Python > 2.7
* Postgres > 9.0
* Oracle Java 1.6
* iRODS = 3.2
* git
* Maven

## Preparing the directory structure

Create the following folders

    /ci/ContentBroker
    /ci/BuildRepository
    /ci/DNSCore (clone the repo into this folder)
    /ci/storage/UserArea/
                        TEST/
                            incoming/
                            outgoing/
                IngestArea/
                        TEST/
                WorkArea/TEST
    /ci/storage/GridCacheArea/
                         TEST/
    /ci/archiveStorage/
    /ci/iRODS/
    /ci/python
                         
As opposed to a regular node install or an installation on a local development workstation,
the dirs are fixed and correspond to the settings in src/main/conf/config.properties.vm3

# Install python

make sure there is a python binary or symlink at

    /ci/python/python

## Prepare the database

Create 2 databases

### irods db

    name: ICAT
    user: irods

### contentbroker db

    name: CI-CB
    user: cb_usr
    password: 

## Prepare iRODS

Set up iRODS

... TODO

    zonename = c-i
    irodsuser = rods
    irodspassword = 
    
Modify server/config/irodsHost so that our irods server hostname is cihost

    cihost localhost
    
And in /etc/hosts

    127.0.0.1 ......... cihost

Create 2 resources 

    WorkResource. name: ciWorkingResource. vaultPath: /ci/storage/WorkArea (irods resource type: cache). hostname: cihost
    ArchiveResource. name: ciArchiveResource ... vaultPath: /ci/archiveStorage (irods resource type: archive)

Create a resource group and add archive resource to it.

    ciArchiveResourceGroup 

### Linux user

User must be

    irods
    
the iRODS and DNSCore as well as the storage locations should all be owned by this user.

## Preparing the irods environment

Create the following collections

    imkdir /somewhere/aip
    imkdir /c-i/work
    imkdir /c-i/aip
    imkdir /c-i/pips/institution
    imkdir /c-i/pips/public




## Performing a testrun by hand

* git clone https://github.com/da-nrw/DNSCore DNSCore

* cd DNSCore/ContentBroker

* mvn clean && mvn deploy -Pci





