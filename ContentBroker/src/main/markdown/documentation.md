DNSCore - Documentation
===

English Version | [Deutsche Version](documentation.de.md)

# Preface 
The Documentation is actually under review. Therefore you may find parts of the documentation not at the places you expect. Please always refer to this document to find the most recent documentation.

The documentation will be restructured to match the several users need. Please select the part appropriate to your roles needs. The following guides are available:

## General Understanding

* The DNSCore object model - reference documentation ([english](object_model.md)|[german](object_model.de.md))
* SIP specification ([english](specification_sip.md)|[german](specification_sip.de.md))
* DIP specification ([english](specification_dip.md))
* Publicaton metadata - specification ([english](specification_publication_metadata.md))
* Description of the delta feature ([english](the_delta_feature.md))


## User Guides

* Ingest ([german](usage_ingest.de.md))
* Retrieval ([german](usage_retrieval.de.md))
* RESTful-API ([english](RESTFul-API.md))


## Administration Guides

### Basic configuration 
* [Controlling](administration-services.de.md) the application. Description of the non gui interfaces to the application.
* config.properties - reference documentation (english](administration_config_properties_reference.de.md)).
* beans.xml - reference documentation ([english](administration-beans.md))
* Processing stages - reference documentation ([english](processing_stages.md))
* The DNSCore installer script ([english](administration-the-installer.md)|[deutsch](administration-the-installer.de.md))
* other interfaces - reference documentation ([english](administration-interfaces.md))
* How to perfom a minimal installation of [DNSCore](installation_minimal.md).
* Overview of the common installation [modes](administration-dnscore-modes.de.md) "pres" and "node" (german version).

### Extended configuration
* Installing ElasticSearch for DNSCore ([german](install_elasticsearch.de.md))
* Installing Fedora for DNSCore ([german](install_fedora.de.md))
* Installing PrOAI for DNSCore ([german](install_proai.md))
* Installing iRODS for DNSCore ([english](installation_irods.md))
* [Create user](create_user.md)
* [click here](needed_packages.md) contains information on needed packages to run DNSCore.
* [click here](installation_open_ports.md) for a list of ports your firewall has to allow connections to.
* How to how to setup DNSCore for iRODS [PAM](using_iRODS_PAM_auth.md) Authentication
* How to extend the minimal installation to work with [iRODS](installation_irods_cb.md).
* How to connect DNSCore and [Fedora](install_fedora.md).
* [How to setup iRODS for Presentation Repository](installation_setup_irods.md)


## Developer Guide

### Enhancing the functions of DNSCore

* Building and testing DNSCore ([english](development_deploy.md))
* Build and Deploy DAWeb ([english](../../../../DAWeb/doc/setup.md))
* Setting up Continuous Integration for DNSCore builds ([english](development_setting_up_ci.md))
* System components overview ([english](components_connectors.md))
* 3rd party modules ([english](3rdPartyTools.md))
* Metadata Workflow - Overview (english](metadata_workflow.md))

### Java API documentation

* [click here](javadoc.md) if you want to recreate the JavaDoc files and publish them on GitHub Pages
* the [Java API documentation](http://da-nrw.github.io/DNSCore/apidocs/)
* the [Java test documentation](http://da-nrw.github.io/DNSCore/testapidocs/)


## Additional Remarks

We have several sources of comprehensive documentation for our project and our codebase.
For all questions related to the software as such and its usage, consider the primary source of documentation which is rolled out directly as part of the source. You'll find the extended documentation below src/main/markdown/. It should be pointed out, that not all of the documents are linked here. 

Before diving into the documentation in the paragraphs below note that documentation about the project is stored mainly in to places.
General overview you'll find it under:

     ContentBroker/src/main/markdown/

For more information on DA-Web User Interface refer to:

    DAWeb/doc

All the links below refer to documentation stored in one of these places.

**Note** that under the abovementioned links you'll always find the documentation attached to the master (e.g. snapshot) version. In most cases this should be what you need. In rare cases however, you want a documentation artifact that matches the exact state of implementation. In these cases you can go to
the releases page, follow the source code link for the corresponding version and then search for the document you're after in this special repository snapshot.

