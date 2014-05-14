# Setting up a continuous integration node

Any release candidate of DNSCore which is meant to be used in 
a production environment, should be build via the maven build lifecycle 
in a continuous integration environment. When we call "mvn deploy" with the "-Pci"-param 
(which stands for continuous integration environment), DNSCore gets build and tested,
and all the acceptance tests run, before the final deliverable gets deployed to a build repository.
As opposed to a development environment (which typically runs on development workstations) all the 
acceptance tests run against an installation of the ContentBroker which is embedded in a fully
operational environment which is similar to the final production environment. This means, that
there are installation of iRODS and Fedora on the same machine and the ContentBroker acts in conjuntion
with these components to fulfill the test criteria (as opposed to the tests on development workstations where
these components are replaced by fake connectors). This helps to control the additional sources of error
that come from the co-working of DNSCore and the components. The final product of any proper build with
all tests passed then, the release candidate, is proved to be suitable to a high degree for further exploratory
or capacity testing on a very production similar grid of machines, which should lead to even higher confidence that
the release candidate can be finally turned in a succesfull release.

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

    name: CB
    user: cb_usr
    password: 

import this [dump](ContentBroker/src/main/conf/postgres_schema.dump) which contains
the schema for the database, the tests will fill the database itself later with data.

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

Add to bashrc

    export PG_PASSWORD=....
  

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





