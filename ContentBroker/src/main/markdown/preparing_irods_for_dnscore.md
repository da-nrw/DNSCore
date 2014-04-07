# Connecting iRODS and DNSCore

This document describes how to set up iRODS as a backend to an existing DNSCore installation.
Both systems connected, with the DNSCore beeing the business layer and iRODS beeing the storage layer,
form a fully operational node ready for production use. For the purpose of this document, the system is considered
as consisting only of this node, e.g. you can see it as a how to of setting up the master node of a system. For setup of
slave nodes or a consideration of other topologies (federation) see the notes at the bottom of this document.

## About iRODS

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

### How the ContentBroker is connected to the storage layer

The storage layer is separated of ContentBroker's internal business logic. The interface is composed by the
GridFacade abstract class and its respective implementations, to separate concerns. By use of this inteface the business code can access objects via logical names whitout knowing of the underlying storage system (which in this case is iRODS).

As ContentBroker has now an extended and comfortable interface for interacting with 
iRODS Servers (federated and single zone based architectures) based on the JARGON interface provided by RENCI (see https://code.renci.org/gf/project/jargon/) and our implematations of GridFacade, DNSCore
is deployed without the need for installed C-microservices for iRODS anymore.

In order to connect the two systems to prepare a node for production use, we assume that you already have set up
the ContentBroker as described in the getting started [guide](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/getting_started.md).

## Setup iRODS

Se
