DNSCore
=======

---

The Core System of the DA-NRW Software Suite. 
The DNS software relies
exclusively on existing open source software components to implement a
distributed, self-validating repository, allowing depositors a high degree of control over
the methods applied to individual objects submitted for preservation and
distribution.

For Documentation see:

* this page
* the [Java API documentation](http://da-nrw.github.io/DNSCore/apidocs/)
* the [Java test documentation](http://da-nrw.github.io/DNSCore/testapidocs/)

### Feature list

As a result of various research programs in the recent years there are some 
software solutions available which handle preservation tasks. Since each of
these solutions was designed under a different focus and to solve different sets of
problems within the field of longterm preservation, it is worth mentioning
the two most distinctive features of DNSCore at first, before diving in into
a richer feater list.

#### Distinctive Features

The first big feature is the possibility for users to let the system automatically
generate derivates of your material destined for long term preservation which are
suited for online web presentation. The decision to integrate a presentation component
deep into the core of the software was originally politically motivated as it was
a state financed open source project and the idea to let the general public have a share
on cultural material accumulated in a state wide archive was only natural in the context of
this background. Also due to the technical possibilities of the digital domain it is relatively
easy to do so and give users access to this material in an easy and uncomplicated way and
this seems contemporary because users nowadays are used to get the information they want
as they are used to it in everyday internet life anyway. We think of it as a really great
feature.

While implementing this feature there was some confusion when speaking of DIPs as stated 
in the OAIS reference model as DIPs can designate any material accessed by the users of
such a system and the DIPs accessed from the long term component differ a lot from those
accessed via the presentation component. As a consequence we decided to reflect these
distinctions in a concise way in order to make communication easier. So we introduced
the concept of PIPs (Presentation Information Package) into our model, which is an
extension to the OAIS model specifically targeted at our needs.

The second big feature is the so called [Delta-Feature](ContentBroker/src/main/markdown/the_delta_feature.md). With deltas users can make additions
to contents already present in the long term archive. This feature is also deeply integrated
into the basic architecture of the software. Also it needed to create an object model that is
in line with the OAIS reference model but is more complicated in its specifics since the
software must handle the organization of objects and packages in the background. So when we
speak of an object in the context of DNSCore as our main entity in the object model, we have
a unit of data which can consist of data of more than one package/SIP at the same time. 

#### Other Features

* DAWeb - Graphical user interface for administrators and users
* Storage Layer - geographical distribution/replication of data
* Self validating ("audit")
* Format identification
* Codec identification
* Automatic format conversion

To build streamlined SIP based based on the used SIP specifications, take a look at:

* SIPBuilder - Graphical user interface for End-Users, which allows high degree control over 
SIP submitted to the DNS. The software could be easily integrated in subsystems, due to having 
a command line interface.



## Documentation

Welcome to the DNSCore documentation

We have several sources of comprehensive documentation for our project and our codebase.
For all questions related to the software as such and its usage, consider the primary source of
documentation which is rolled out directly as part of the source. You'll find the extended documentation 
below src/main/markdown/. It should be pointed out, that not all of the documents are linked here. 

Before diving into the documentation in the paragraphs below note that documentation about the
project is stored mainly in to places.
General overview you'll find it under:
<br>ContentBroker/src/main/markdown/
<br>For more information on DA-Web User Interface refer to:
<br>DAWeb/doc
<br>All the links below refer to documentation stored in one of these places.

**Note** that under the abovementioned links you'll always find the documentation attached to the master (e.g. snapshot) version. In most cases this should be what you need. In rare cases however, you want a documentation artifact that matches the exact state of implementation. In these cases you can go to
the releases page, follow the source code link for the corresponding version and then search for the document you're after in this special repository snapshot.

### General concepts, for all Audiences including End Users

* [Ingest and Retrieval](DAWeb/doc/manual_ingest_and_retrieval.md). A German version will follow soon.
* Presentation of the [Object model](ContentBroker/src/main/markdown/object_model.md).
* Presentation of the [Delta feature](ContentBroker/src/main/markdown/the_delta_feature.md).
* [DIP](ContentBroker/src/main/markdown/dip_specification.md) Specification
* [SIP](ContentBroker/src/main/markdown/sip_specification.md) Specification
* [AIP](ContentBroker/src/main/markdown/aip_specification.md) Specification
* [PREMIS Specification](ContentBroker/src/main/markdown/premis_specification.md)

### Administration

#### General concepts
* [Processing Stages](ContentBroker/src/main/markdown/processing_stages.md) demonstrates basic concepts necessary to administrate the system
* Overview over the system [components](ContentBroker/src/main/markdown/components_connectors.md)
* Overview of the [format module](src/main/markdown/format_module.md).
* Overview of the [metadata workflow](ContentBroker/src/main/markdown/metadata_workflow.md).

#### Installation related
* [click here](ContentBroker/src/main/markdown/installation_irods.md) to learn how to set up / upgrade iRODS for DNSCore node.
* [click here](ContentBroker/src/main/markdown/installation_cb.md) to learn how to set up the ContentBroker.
* [click here](ContentBroker/src/main/markdown/installation_irods_cb.md) to learn how connect iRODS and the ContentBroker
* [click here](ContentBroker/src/main/markdown/install_fedora.md)) to learn how connect Fedora and DNSCore
* [click here](ContentBroker/src/main/markdown/installation.md) if you want to update your node to a new version of DNSCore
* [click here](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/installation_ci.md) to learn how to set up a machine for continuous integration.
* [clck here](ContentBroker/src/main/markdown/needed_packages.md) contains information on needed packages to run DNSCore.
* [clck here](ContentBroker/src/main/markdown/using_iRODS_PAM_auth.md ) how to setup DNSCore for iRODS PAM Authentication

#### Running a node
* [click here](DAWeb/doc/contentBroker_administration.md) how to administer CB with DA-Web web user interface.  
* [click here](ContentBroker/src/main/markdown/system_configuration.md) to get an overview of how to set up the config files so that DNSCore will work in your environment.
* [click here](ContentBroker/src/main/markdown/administration-troubleshooting.md) to get help how to administrate the system when errors occur.
* [click here](ContentBroker/src/main/markdown/administration-interfaces.md) for a description of the non gui interfaces to the application.
* [click here](ContentBroker/src/main/markdown/open_ports.md) for a list of ports your firewall has to allow connections to.
* [clck here](ContentBroker/src/main/markdown/audit.md) to get information how the AIP are being checked automatically.

#### Presentation Repository Specific

* Medata [specification](ContentBroker/src/main/markdown/metadata_specification.md). The metadata formats accepted by DNS that will enable proper presentation.

The DA-NRW Presentation Repository constists of the following components:

* [Fedora](ContentBroker/src/main/markdown/install_fedora.md)
* [PrOAI](ContentBroker/src/main/markdown/install_proai.md)
* [Elasticsearch](ContentBroker/src/main/markdown/install_elasticsearch.md)

Additional documentation

* [Create user](ContentBroker/src/main/markdown/create_user.md)
* [How to setup iRODS for Presentation Repository](ContentBroker/src/main/markdown/setup_irods.md)

### Development

* Metadata [Workflow](ContentBroker/src/main/markdown/metadata_workflow.md). Technical description.
* [click here](ContentBroker/src/main/markdown/3rdPartyTools.md) to get general information about the use and redistribution of used 3rd party libraries and tools.
* [click here](ContentBroker/src/main/markdown/deploy.md) if you want to deploy the software yourself and build the source code on your local or on a continuous integration machine.
* [click here](ContentBroker/src/main/markdown/manual_testing_rc.md) to learn more about how our software is being tested.
* [click here](DAWeb/doc/deploy.md) for information about deploying DAWeb
* [click here](ContentBroker/src/main/markdown/javadoc.md) if you want to recreate the JavaDoc files and publish them on GitHub Pages



