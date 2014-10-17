	/*
	  DA-NRW Software Suite | ContentBroker
	  Copyright (C) 2011-2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
	  Universität zu Köln
	  Copyright (C) 2014 LVRInfoKom
	  Landschaftsverband Rheinland
	
	  This program is free software: you can redistribute it and/or modify
	  it under the terms of the GNU General Public License as published by
	  the Free Software Foundation, either version 3 of the License, or
	  (at your option) any later version.
	
	  This program is distributed in the hope that it will be useful,
	  but WITHOUT ANY WARRANTY; without even the implied warranty of
	  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	  GNU General Public License for more details.
	
	  You should have received a copy of the GNU General Public License
	  along with this program.  If not, see <http://www.gnu.org/licenses/>.
        */

# DNSCore - Dokumentation

Deutsche Version | [English Version](documentation.md)

Willkommen auf der Startseite der Dokumentation des DNSCore Softwarepakets! Dies hier ist die primäre Quelle zu allen Fragen bezüglich der Handhabung des Softwarepakets. 

Der Großteil der Dokumentation befindet sich unter

[DNSCore/ContentBroker/src/main/markdown/](../markdown),

wobei sich auch noch einige weitere Dokumention unter folgendem Link findet:

[DNSCore/DAWeb/doc](../../../../DAWeb/doc).

**Versionierung** der Dokumentation:
Die Dokumentation ist Teil des Git-Repository der DNSCore und wird gemeinsam mit der Codebasis versioniert. Auf diese Weise wird der Stand der Dokumentation synchron zum Stand der Codebasis gehalten. Die Links sind, soweit es möglich ist, relativ, damit ein Checkout einer beliebigen Version selbstreferentiell und konsistent ist. Im Falle der Grafiken ist diese Vorgehensweise hinsichtlich der Darstellung in GitHub nicht möglich (wegen der Referenzen auf raw-Dateien). Hier finden sich die Quellen der Grafiken aber immer im ContentBroker/src/main/markdown Verzeichnis wieder.

#### Langzeitarchivierung mit DNSCore verstehen<br>[Alle Benutzergruppen]

Hier befinden sich alle Dokus, die Grundkonzepte der Langzeitarchivierung im Kontext des Einsatzes von DNSCore beschreiben.

* Das DNSCore Objektmodell - Referenzdokumentation ([deutsch](object_model.de.md) | [englisch](object_model.md))
* SIP-Spezifikation ([deutsch](specification_sip.de.md) | [englisch](specification_sip.md))
* DIP-Spezifikation ([englisch](specification_dip.md))
* Dokumentation der Metadaten-Verarbeitung ([deutsch](2014-09-25_Metadaten_in_DA-NRW.pdf) | [englisch](specification_publication_metadata.md))
* DNSCore Feature Liste ([englisch](features.md))
* Delta Feature - Beschreibung ([english](the_delta_feature.md))

#### DNSCore verwenden<br>[Endnutzer]

Dieser Bereich wendet sich an die Anwender der DNSCore Lösung für Langzeitarchivierung.

* Ingest und Retrieval ([englisch](../../../../DAWeb/doc/manual_ingest_and_retrieval.md))

#### Bereitstellungen von Dienstleistungen an Endnutzer<br>[Systemadministratoren / Knotenadministratoren / Betreiber]

Dieser Bereich umfasst Dokumentationen, die sich damit beschäftigen, wie mithilfe eines Verbundes von Knoten, auf denen DNSCore läuft, Langzeitarchivierungsfunktionalität für Endkunden bereitgestellt werden kann.

* AIP-Spezifikation ([englisch](specification_aip.md))
* PREMIS-Spezifikation([english](specification_premis.md))
* Formatidentifikation mit DNSCore ([deutsch](operations_format_identification.de.md))
* Formatkonversion mit DNSCore ([deutsch](operations_format_conversion.de.md))
* Formatmodul ([englisch](format_module.md))

#### Umgebungen mit DNSCore aufsetzen<br>[Knotenadministratoren]

In diesem Bereich sind Dokumentationen untergebracht, die die Installation der Software DNSCore,
die vorbereitenden Anpassung von Umgebungen für den Einsatz von DNSCore bzw. Anpassung von DNSCore an die Umgebungen, sowie der Anbindung von DNSCore an Fremdkomponenten thematisieren. 

###### Basiskonfiguration

Die hier untergebrachten Dokumentationen gelten generell für den Einsatz von DNSCore.

* config.properties - Referenzdokumentation ([deutsch](administration_config_properties_reference.de.md))
* beans.xml - Referenzdokumentation ([english](administration-beans.xml))
* Speicherbereiche - Referenzdokumentation ([english](processing_stages.md))
* Der DNSCore Installer ([deutsch](administration-the-installer.de.md) | [english](administration-the-installer.md))

###### Erweiterte Konfiguration

Die hier untergebrachten Dokumentationen setzen den Einsatz von DNSCore unter bestimmten Bedingungen voraus. Etwa die Anbindung an bestimmte externe Systeme, deren Bedingungen der Anbindung hier genauer beschrieben sind.

* Installation von ElasticSearch für DNSCore ([deutsch](install_elasticsearch.de.md))
* Installation von Fedora für DNSCore ([englisch](install_fedora.md))
* Installation von PrOAI für DNSCore ([deutsch](install_proai.md))
* Installing iRODS for DNSCore ([englisch](installation_irods.md))

#### Fehlerbehebung<br>[Systemadministratoren / Knotenadministratoren]
Die aufgeführten Dokumente geben Hinweise zur Fehlerbehebung bei der Knotenadministration.
* Spezifikation der Fehlerstatus & Hinweise zur Fehlerbehebung ([english](administration-troubleshooting.md) | [deutsch](administration-troubleshooting_de.md) )

#### Funktionalitäten der DNSCore erweitern<br>[Entwickler]

Um DNSCore effektiv bauen, testen und ausliefern zu können, sollten die folgenden Dokus gelesen werden:

* Bauen und Testen der DNSCore ([englisch](development_deploy.md))
* Bauen DAWeb ([english](../../../../DAWeb/doc/deploy.md))
* Aufsetzen von Continuous Integration für DNSCore builds ([englisch](development_setting_up_ci.md))
* Systemkomponenten im Überblick ([english](components_connectors.md))
* 3rd Party Module ([english](3rdPartyTools.md))
* Metadata Workflow - Übersicht ([english](metadata_workflow.md))

###### Java API Dokumentation

Die Java API Dokumentation wird zum jetzigen Zeitpunkt nicht aktiv automatisch geupdatet, so dass er auf Bedarf manuell erzeugt werden muss.

* Javadoc für DNSCore erzeugen und auf GitHub publizieren ([englisch](javadoc.md))
* Javadoc API Dokumentation. Älterer Stand. ([hier](http://da-nrw.github.io/DNSCore/apidocs/))
* Javadoc Test Dokumentation. Älterer Stand. ([hier](http://da-nrw.github.io/DNSCore/testapidocs/))

