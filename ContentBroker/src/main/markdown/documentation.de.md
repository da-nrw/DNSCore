# DNSCore - Dokumentation

Deutsche Version | [English Version](documentation.md)

Willkommen auf der Startseite der Dokumentation des DNSCore Softwarepakets! Dies hier ist die primäre Quelle zu allen Fragen bezüglich der Handhabung des Softwarepakets. 

**Schneller Überblick**

[Zusammenfassung der verschiedenen Spezifikationen zu SIP/AIP/DIP/Delta-SIP/PIP](./specifications_DNS.md)

[Zusammenfassung Leistungsmerkmale (Features) der Software](features.de.md)

Die gesamte Dokumentation (auch hier nicht explizit verlinkte Texte) findet sich unter 

[DNSCore/ContentBroker/src/main/markdown/](../markdown)

[DNSCore/DAWeb/doc](../../../../DAWeb/doc).

**Vorbemerkung zur Dokumentation**

Die Dokumentation ist Teil des Git-Repository der DNSCore und wird gemeinsam mit der Codebasis versioniert. Auf diese Weise wird der Stand der Dokumentation synchron zum Stand der Codebasis gehalten. Die Links sind, soweit es möglich ist, relativ, damit ein Checkout einer beliebigen Version selbstreferentiell und konsistent ist. Im Falle der Grafiken ist diese Vorgehensweise hinsichtlich der Darstellung in GitHub nicht möglich (wegen der Referenzen auf raw-Dateien). Hier finden sich die Quellen der Grafiken aber immer im ContentBroker/src/main/markdown Verzeichnis wieder.

##Für Anwender##

* Ingest ([deutsch](usage_ingest.de.md))
* Retrieval ([deutsch](usage_retrieval.de.md))
* Anbindung von Fachverfahren (RESTful) ([english](RESTFul-API.md))
* Konfigurierte Konversionen([deutsch](operations_format_conversion_current_configuration.de.md))
* Metadatenverarbeitung ([deutsch](specification_metadata.de.md))
* Fehlerbehebung ([deutsch](user_troubleshooting.de.md))

##Für Administratoren

* Fehleranalyse und Fehlerbehebung ([deutsch](administration-troubleshooting.de.md))
* Formatidentifikation mit DNSCore ([deutsch](operations_format_identification.de.md))
* Formatkonversion mit DNSCore ([deutsch](operations_format_conversion.de.md))
* Formatmodul ([englisch](format_module.md))
* 3rd Party Module ([english](3rdPartyTools.md))

###Langzeitarchivierung mit DNSCore verstehen

Hier befinden sich alle Dokus, die Grundkonzepte der Langzeitarchivierung im Kontext des Einsatzes von DNSCore beschreiben.

* Das DNSCore Objektmodell - Referenzdokumentation ([deutsch](object_model.de.md) | [englisch](object_model.md))
* SIP-Spezifikation ([deutsch](specification_sip.de.md) | [englisch](specification_sip.md))
* AIP-Spezifikation ([englisch](specification_aip.md))
* PREMIS-Spezifikation([english](specification_premis.md))
* DIP-Spezifikation ([englisch](specification_dip.md))
* Dokumentation der Metadaten-Verarbeitung ([deutsch](specification_metadata.de.md) | [englisch](specification_publication_metadata.md))
* Delta Feature - Beschreibung ([deutsch](the_delta_feature.de.md) | [english](the_delta_feature.md))

### Basiskonfiguration

Die hier untergebrachten Dokumentationen gelten generell für den Einsatz von DNSCore.

* config.properties - Referenzdokumentation ([deutsch](administration_config_properties_reference.de.md))
* beans.xml - Referenzdokumentation ([english](administration-beans.md))
* Speicherbereiche - Referenzdokumentation ([english](processing_stages.md))
* Der DNSCore Installer ([deutsch](administration-the-installer.de.md) | [english](administration-the-installer.md))

### Erweiterte Konfiguration und Installation

Die hier untergebrachten Dokumentationen setzen den Einsatz von DNSCore unter bestimmten Bedingungen voraus. Etwa die Anbindung an bestimmte externe Systeme, deren Bedingungen der Anbindung hier genauer beschrieben sind.

* Installation von ElasticSearch für DNSCore ([deutsch](install_elasticsearch.de.md))
* Installation von Fedora für DNSCore ([deutsch](install_fedora.de.md))
* Installation von PrOAI für DNSCore ([deutsch](install_proai.md))
* Installing iRODS for DNSCore ([englisch](installation_irods.md))

## Für Entwickler

Um DNSCore effektiv bauen, testen und ausliefern zu können, sollten die folgenden Dokus gelesen werden:

* Bauen und Testen der DNSCore ([englisch](development_deploy.md))
* Bauen und Ausliefern DAWeb ([english](../../../../DAWeb/doc/setup.md))
* Aufsetzen von Continuous Integration für DNSCore builds ([englisch](development_setting_up_ci.md))
* Systemkomponenten im Überblick ([english](components_connectors.md))
* 3rd Party Module ([english](3rdPartyTools.md))
* Metadata Workflow - Übersicht ([english](metadata_workflow.md))
