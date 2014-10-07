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

Willkommen auf der Startseite der Dokumentation des DNSCore Softwarepaketes! Dies hier ist die primäre Quelle zu allen Fragen bezüglich der Handhabung des Softwarepaketes. 

Die Großteil der Dokumentation der DNSCore befindet sich unter

[DNSCore/ContentBroker/src/main/markdown/](../markdown)

wobei sich auch noch einige weitere Dokumention unter folgendem Link findet:

[DNSCore/DAWeb/doc](../../../../DAWeb/doc)

**Versionierung** der Dokumentation. Die Dokumentation ist Teil des Git-Repository der DNSCore und wird gemeinsam zur Codebasis versioniert, damit der Stand der Dokumentation möglichst synchron zum Stand der Codebasis gehalten werden kann. Die Links sind, soweit es möglich ist, relativ, damit ein Checkout einer beliebigen Version selbstreferentiell und konsistent ist. Im Falle der Grafiken war dies hinsichtlich der Darstellung in GitHub nicht möglich (wegen der Referenzen auf raw-Dateien). Hier finden sich die Quellen der Grafiken aber immer im ContentBroker/src/main/markdown Verzwichnis wieder.

#### Langzeitarchivierung mit DNSCore verstehen<br>[Alle Benutzergruppen]

* Das DNSCore Objektmodell - Referenzdokumentation ([deutsch](object_model.de.md)|[englisch](object_model.md))
* SIP-Spezifikation ([deutsch](specification_sip.de.md)|[englisch](specification_sip.md))
* DIP-Spezifikation ([englisch](specification_dip.md))
* Publikationsmetadatenformate - Spezifikation ([englisch](specification_publication_metadata.md))
* DNSCore Feature Liste ([englisch](features.md))
* Delta Feature - Beschreibung ([english](the_delta_feature.md))

Dokumentation der [Metadaten](2014-09-25_Metadaten_in_DA-NRW.pdf)-Verarbeitung

#### DNSCore verwenden<br>[Endnutzer]

* Ingest und Retrieval ([englisch](../../../../DAWeb/doc/manual_ingest_and_retrieval.md))

#### Bereitstellungen von Dienstleistungen an Endnutzer<br>[Systemadministratoren / Knotenadministratoren / Betreiber]

* AIP-Spezifikation ([englisch](specification_aip.md))
* PREMIS-Spezifikation([english](specification_premis.md))
* Formatidentifikation mit DNSCore ([deutsch](operations_format_identification.de.md))
* Formatkonversion mit DNSCore ([deutsch](operations_format_conversion.de.md))
* Formatmodul ([englisch](format_module.md))

#### Umgebungen mit DNSCore aufsetzen<br>[Knotenadministratoren]

###### Basiskonfiguration

* config.properties - Referenzdokumentation ([deutsch](administration_config_properties_reference.de.md))
* beans.xml - Referenzdokumentation ([english](administration-beans.xml))

###### Erweiterte Konfiguration

* a
* b

###### Installation der Software

#### Funktionalitäten der DNSCore erweitern<br>[Entwickler]

* Bauen und Testen der DNSCore ([englisch](development_deploy.md))
* Bauen DAWeb ([english](../../../../DAWeb/doc/deploy.md))
* Aufsetzen von Continuous Integration für DNSCore builds ([englisch](development_setting_up_ci.md))

###### Java API Dokumentation

Die Java API Dokumentation wird zum jetzigen Zeitpunkt nicht aktiv automatisch geupdatet, so dass er auf Bedarf manuell erzeugt werden muss.

* Javadoc für DNSCore erzeugen und auf GitHub publizieren ([englisch](javadoc.md))
* Javadoc API Dokumentation. Älterer Stand. ([hier](http://da-nrw.github.io/DNSCore/apidocs/))
* Javadoc Test Dokumentation. Älterer Stand. ([hier](http://da-nrw.github.io/DNSCore/testapidocs/))

