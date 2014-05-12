# Setting up a continuous integration node

## Prerequisites

* Python > 2.7
* Postgres > 9.0
* Oracle Java 1.6
* iRODS = 3.2
* git

## Prepare the database

Create 2 databases

# irods db

name: ICAT
user: irods

# contentbroker db

name: CI-CB
user: cb_usr

## Prepare iRODS

Set up iRODS

... TODO

    zonename = c-i
    irodsuser = rods
    irodspassword = 

Create 2 resources 

    WorkResource. name: ciWorkingResource. vaultPath: /ci/storage/WorkArea
    ArchiveResource. name: ciArchiveResource ... vaultPath: /ci/archiveStorage

Create a resource group and add archive resource to it.

    ciArchiveResourceGroup 

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
                         
As opposed to a regular node install or an installation on a local development workstation,
the dirs are fixed and correspond to the settings in src/main/conf/config.properties.vm3






