# DNSCore - Documentation

## Documentation

Welcome to the DNSCore documentation

We have several sources of comprehensive documentation for our project and our codebase.
For all questions related to the software as such and its usage, consider the primary source of
documentation which is rolled out directly as part of the source. You'll find the extended documentation 
below src/main/markdown/. It should be pointed out, that not all of the documents are linked here. 

Before diving into the documentation in the paragraphs below note that documentation about the
project is stored mainly in to places.
General overview you'll find it under:

     ContentBroker/src/main/markdown/

For more information on DA-Web User Interface refer to:

    DAWeb/doc

All the links below refer to documentation stored in one of these places.

**Note** that under the abovementioned links you'll always find the documentation attached to the master (e.g. snapshot) version. In most cases this should be what you need. In rare cases however, you want a documentation artifact that matches the exact state of implementation. In these cases you can go to
the releases page, follow the source code link for the corresponding version and then search for the document you're after in this special repository snapshot.

### General concepts, for all Audiences including End Users

* [Ingest and Retrieval](../../../../DAWeb/doc/manual_ingest_and_retrieval.md). A German version will follow soon.
* [DIP](specification_dip.md) Specification
* [SIP](specification_sip.md) Specification
* [AIP](specification_aip.md) Specification
* [PREMIS](specification.md) Specification
* The DNSCore [Object](ContentBroker/src/main/markdown/object_model.md) mode.
* Feature [list](features.md)
* Description of the [Delta](ContentBroker/src/main/markdown/the_delta_feature.md) feature.

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
* [click here](DAWeb/doc/deploy.md) for information about deploying DAWeb
* [click here](ContentBroker/src/main/markdown/javadoc.md) if you want to recreate the JavaDoc files and publish them on GitHub Pages


* the [Java API documentation](http://da-nrw.github.io/DNSCore/apidocs/)
* the [Java test documentation](http://da-nrw.github.io/DNSCore/testapidocs/)
