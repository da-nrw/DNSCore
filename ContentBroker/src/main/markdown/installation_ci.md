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

In order to always work in a predictable manner with the ever changing code base, of which the build scripts itself
are part of, the machine set up for continuous integration has to be configured in a certain way (without the degrees
of freedom you have when installing a typical node). 
This document describes in detail how to set up such a machine.

## Prerequisites

Please start with installing the following components to your machine first:

* Python > 2.7
* http2lib module for python
* Postgres > 9.0
* Oracle Java 1.6
* ImageMagick
* git
* Maven

## Linux environment

In /etc/profile.d/dns.sh (for all users)

    export FEDORA_HOME=/ci/fedora
    export JAVA_HOME=
    export JAVA_OPTS
    export CATALINA_HOME

iRODS, DNSCore, as well as the build repository and all the other storage locations from the
following paragraphs, should be owned by a single linux user, which is preferably named

    irods
    
Make sure to edit .bashrc of this user and add the following entry
    
    export PG_PASSWORD=[db password of cb_usr]

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
                         
Make sure to symlink your python 2.7 binary to this location:

    /ci/python/python

And in /etc/hosts

    127.0.0.1 ......... cihost
    
/ci/fedora should be owned by irods:tomcat with all write permissions for tomcat granted.
Test can be done with ./fedora-ingest-demos.sh localhost 8080 fedoraAdmin [password] http
    

## Prepare the database

Create 4 databases

### irods db

    name: ICAT
    user: irods

### contentbroker db

    name: CB
    user: cb_usr

Import this [dump](../conf/postgres_schema.dump) which contains
the schema for the database, the tests will fill the database itself later with data.

### fedora db

    name: FED
    user: fed_usr

### fedora triplestore

    name: TRIP
    user: fed_usr

## iRODS installation

Install [iRODS](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/installation_irods.md) 3.2

    zonename = c-i
    irodsuser = rods
    irodspassword = 
    
Modify server/config/irodsHost so that our irods server hostname is cihost

    cihost localhost

Create 2 resources 

    WorkResource. name: ciWorkingResource. vaultPath: /ci/storage/WorkArea (irods resource type: cache). hostname: cihost
    ArchiveResource. name: ciArchiveResource ... vaultPath: /ci/archiveStorage (irods resource type: archive)

Create a resource group and add archive resource to it.

    ciArchiveResourceGroup 

in server/config/reConfigs/core.re

    acSetRescSchemeForCreate {msiSetDefaultResc("ciWorkingResource","null"); }
    acSetRescSchemeForRepl {msiSetDefaultResc("ciWorkingResource","null"); }

Create the following collections

    imkdir /somewhere/aip
    imkdir /c-i/work
    imkdir /c-i/aip
    imkdir /c-i/pips/institution
    imkdir /c-i/pips/public

## Install Fedora

While executing the following steps, compare the fedora [installation](https://github.com/da-nrw/prepscripts/blob/master/doc/install_fedora.md) documtn.

in your config/install.properties set the following entries accordingly

    fedora.admin.pass=(SET)
    fedora.serverHost=cihost
    database.password=(SET)
    database.username=fed_usr
    database.jdbcURL=jdbc\:postgresql\://localhost\:5432/FED
    database.postgresql.jdbcURL=jdbc\:postgresql\://localhost\:5432/FED
    tomcat.home=(SET)
    fedora.home=/ci/fedora

Remember the admins name is fedoraAdmin

..

Attention the scripts for setting up the policies seemed to be a little buggy insofar
as prepscripts/scripts/fedorarest.py had to be adjusted with the right credentials.
The configuration via setup-policies.py didn't work properly.

## Install elasticsearch

follow the instructions from [this](https://github.com/da-nrw/prepscripts/blob/master/doc/install_elasticsearch.md) documents

choose the following settings

    portal_ci
    cluster_ci


## Performing a testrun by hand

* git clone https://github.com/da-nrw/DNSCore DNSCore

* cd DNSCore/ContentBroker

* mvn clean && mvn install -Pci

## Settings

As opposed to a regular node install or an installation on a local development workstation,
the dirs are fixed and correspond to the settings in src/main/conf/config.properties.vm3





