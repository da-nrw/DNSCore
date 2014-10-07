# DNSCore - Documentation

English Version|[Deutsche Version](documentation.de.md)

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

#### Understanding long term preservation with DNSCore<br>[General / All Audiences]

* Feature [list](features.md)
* The DNSCore [Object](object_model.md) model.
* [DIP](specification_dip.md) Specification
* [SIP](specification_sip.md) Specification
* [AIP](specification_aip.md) Specification
* Medata [specification](metadata_specification.md). The metadata formats accepted by DNS that will enable proper presentation.
* [PREMIS](specification_premis.md) Specification
* Description of the [Delta](the_delta_feature.md) feature.

#### Using DNSCore<br>[End Users]

* [Ingest and Retrieval](../../../../DAWeb/doc/manual_ingest_and_retrieval.md). A German version will follow soon.

#### Providing services to end users<br>[System administrators / Node administrators / Operators]

* Format identification with DNSCore ([german](operations_format_conversion.de.md))
* Format conversion with DNSCore ([german](operations_format_conversion.de.md))
* [click here](../../../../DAWeb/doc/contentBroker_administration.md) how to administer CB with DA-Web web user interface.
* Job [states](administration-troubleshooting.md). How to deal with jobs in error states.
* [clck here](audit.md) to get information how the AIP are being checked automatically.
* [Create user](create_user.md)
* Processing [stages](processing_stages.md) demonstrates basic concepts necessary to administrate the system
* [Controlling](administration-services.md) the application. Description of the non gui interfaces to the application.
* Information on the [installer](administration-the-installer.md) script.

#### Setting up Environments with DNSCore<br>[Node administrators]

###### Configuring the environment

* config.properties - reference documentation ([config.properties](administration_config_properties_reference.de.md)).

###### Installing the software

* Overview of the common installation [modes](administration-dnscore-modes.de.md) "pres" and "node" (german version).
* How to perfom a minimal installation of [DNSCore](installation_minimal.md).
* How to set up [iRODS](installation_irods.md) for DNSCore.
* How to extend the minimal installation to work with [iRODS](installation_irods_cb.md).
* How to install [Fedora](install_fedora.md) for DNSCore.
* How to connect DNSCore and [Fedora](install_fedora.md).
* How to install [PrOAI](install_proai.md) for DNSCore
* How to install [Elasticsearch](install_elasticsearch.md) for DNSCore.
* [click here](installation.md) if you want to update your node to a new version of DNSCore
* [click here](installation_ci.md) to learn how to set up a machine for continuous integration.
* [click here](needed_packages.md) contains information on needed packages to run DNSCore.
* How to how to setup DNSCore for iRODS [PAM](using_iRODS_PAM_auth.md) Authentication
* [click here](installation_open_ports.md) for a list of ports your firewall has to allow connections to.
* [How to setup iRODS for Presentation Repository](installation_setup_irods.md)
* Reference for the [beans.xml](administration-beans.md) configuration file.

#### Enhancing the functions of DNSCore<br>[Developers]

* Building and testing DNSCore ([english](development_deploy.md))

* Metadata [Workflow](metadata_workflow.md). Technical description.
* [click here](3rdPartyTools.md) to get general information about the use and redistribution of used 3rd party libraries and tools.
* [click here](../../../../DAWeb/doc/deploy.md) for information about deploying DAWeb
* Overview over the system [components](components_connectors.md)
* Overview of the [format module](format_module.md).
* Overview of the [metadata workflow](metadata_workflow.md).
* 
###### Java API documentation

* [click here](javadoc.md) if you want to recreate the JavaDoc files and publish them on GitHub Pages
* the [Java API documentation](http://da-nrw.github.io/DNSCore/apidocs/)
* the [Java test documentation](http://da-nrw.github.io/DNSCore/testapidocs/)
