# Setting up a continuous integration node

## Prerequisites

* Python > 2.7
* Postgres > 9.0
* Oracle Java 1.6
* iRODS > 3.2
* git

## Prepare iRODS

Set up iRODS

... TODO

zonename = c-i
irodsuser = 
irodspassword =

Create 2 resources 

1. WorkResource. name: TODO. vaultPath: /ci/storage/WorkArea
2. ArchiveResource. name: ... vaultPath: /ci/archiveStorage

Create a resource group and add archive resource to it.

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
                         
As opposed to a regular node install or an installation on a local development workstation,
the dirs are fixed and correspond to the settings in src/main/conf/config.properties.vm3

## Prepare the database





