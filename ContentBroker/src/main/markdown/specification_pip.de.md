	/*
	  DA-NRW Software Suite | ContentBroker
	  Copyright (C) 2014 Historisch-Kulturwissenschaftliche Informationsverarbeitung
	  Universität zu Köln
	  Copyright (C) 2015 LVRInfoKom
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
	
# Spezifikation PIP (Presentation Information Package) in DNSCore
Die Spezifikation befindet sich zur Zeit **im Aufbau** !!! 

## Inhalt eines PIP 
Ein **PIP** besteht prinzipiell aus Metadaten (mind. epicur.xml) und Originaldaten.
Ein PIP muss, um für DNSCore verarbeitbar zu sein, mindestens aus folgenden Metadaten bestehen: 

	epicur.xml
	EDM.xml
	<Datenformat>.xml (z.B. METS.xml, LIDO.xml, ...)
	


## Aufbau eines PIP
Der generelle Aufbau eines von DNSCore erstellten **PIP** sieht wie folgt aus:

    epicur.xml
    meinBild1.jpg
    meinBild2.jpg
    meinBildn.jpg
    
Zusätzlich können folgende Metadaten benötigt werden (falls eine Konvertierung stattfindet):

    DC.xml
    EDM.xml

Sowie die dazugehörigen Formatbeschreibungen:

    LIDO.xml
    EAD.xml
    METS.xml
    
### epicur.xml 
Die epicur.xml ist eine Metadaten-Datei, die in jedem PIP enthalten sein muss 
(s. [Spezifikation epicur](http://www.persistent-identifier.de/?link=210)).
Sie beteht aus einem Wurzelelement <epicur> und zwei hierarchisch untergeordneten Elementen
	<administrative_data>
	<record>
	
Der Aufbau sieht im DNSCore wie folgt aus:
	
	<epicur xsi:schemaLocation="urn:nbn:de:1111-2004033116 
		http://www.persistent-identifier.de/xepicur/version1.0xepicur.xsd">
		<administrative_data>
			<delivery>
				<update_status type="urn_new"/>
			</delivery>
		</administrative_data>
		<record>
			<identifier scheme="urn:nbn:de">urn:nbn:de:danrw-1-20160922818</identifier>
			<resource>
				<identifier scheme="url" role="primary">http://data.danrw.de/ead-viewer/#/browse?src=http%3A%2F%2Fdata.danrw.de%2Ffile%2F1-20160922818%2FEAD</identifier>
				<format scheme="imt">text/html</format>
			</resource>
		</record>
	</epicur>

<epicur> 

**epicur** ist das Wurzelement und hat "administrative_data" und "record" als Unterelemente.

1.Die Elementgruppe **administrative_data** 

Sie dient 

* der Kapselung von Legitimationsdaten, 
* der Spezifizierung des URN-Meldeprozesses,
* der verwendeten Transferschnittstelle   sowie der 
* Art der Rückmeldung

und hat das Unterelement "delivery". Diese Elementgruppe ist zwingend notwendig und muss genau einmal vorkommen.

Unterelement **delivery** 

Das Unterelement **delivery** muss das Element "update_status" beinhalten und dient der Spezifizierung des Meldeprozesses. 
**update_data** besitzt das Attribut "type", welches in DNSCore immer den Wert "urn_new" beinhaltet. "urn_new" kennzeichnet die Meldung als "URN-Erstregistrierung". Bei Einbindung in Open Archives Initiative (OAI) muss bei Neuerstellung von Records "urn_new" angegeben werden.

Die Elemtentgruppe kann weitere Unterelmente besitzen, welche in [Spezifikation epicur](http://www.persistent-identifier.de/?link=210) beschrieben werden.

2.Die Elementgruppe **record**

Sie kapselt die URN-URL-Beziehungen der Objekte, ist zwingend notwendig und darf wiederholt werden. Im DNSCore wird diese Elementgruppe allerdings nicht wiederholt. 

* Die Elementgruppe muss das Element "identifier" besitzen, welches genau einmal vorkommen muss. Es enthält Informationen über eine URN und URL.Im DNSCore wird immer über die urn referenziert (<identifier scheme="urn:nbn:de"> ...</identifier>)
* Desweiteren besitz die Elementgruppe im DNSCore das Element "resource". Dieses Element ist nicht obligatorisch und kann bei Bedarf wiederholt werden. In DNSCore wird es geanau einmal verwendet. Es kapselt die Unterelemente "identifier" mit den Attributen scheme="url" (dient der Erfassung der URN bzw. der URL des Objektes) und role="primary" (kennzeichnet eine URL als 'Master-URL', falls mehr als eine URL zu einer URN registriert ist) sowie das Element "format", welches zur Erfassung der URL und des MimeTypes des Objektes dient. Es ist nicht obligatorisch und darf max. einmal vorkommen. IM DNSCore kommt das Element genau einmal vor und hat immer den Content "text/html"

### DC.xml

Die DC.xml ist der 'Dublin Core Record' des Objektes. Sie wird verwendet, um die vorhandenen Daten-Dateien (z.B. Videos, Bilder, Web-Seiten, etc genauso wie physikalische Ressourcen wie Bücher oder CD's) zu beschreiben.  
Die Original DC-Metadaten können aus 15 Elementen bestehen (simple Standard), welche alle optional und wiederholbar sind. 

1.	Title
1.	Creator
1. Subject
1. Description
1. Publisher
1. Contributor
1. Date
1. Type
1. Format
1. Identifier
1. Source
1. Language
1. Relation
1. Coverage
1. Rights  


Die beispielhaft Struktur im DNSCore

	<oai_dc:dc xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
		<dc:title>Text Text// mahels///Titel</dc:title>
		<dc:title>Text Text</dc:title>
		<dc:contributor>Nachname, Vorname</dc:contributor>
		<dc:type>Text</dc:type>
		<dc:type>book</dc:type>
		<dc:publisher>Grimm]</dc:publisher>
		<dc:publisher>ULB</dc:publisher>
		<dc:language>ger</dc:language>
		<dc:description>Titel</dc:description>
		<dc:description>Referenz 1</dc:description>
		<dc:description>Referenznummer</dc:description>
		<dc:identifier>id99</dc:identifier>
		<dc:identifier>urn:nbn:de:hbz:99</dc:identifier>
		<dc:identifier>urn:nbn:de:hbz:99</dc:identifier>
		<dc:format>METS</dc:format>
	</oai_dc:dc>
    
### Europeana Data Model  EDM.xml

Die EDM.xml ist die Schnittstelle zum Portal. Aus jedem Metdatenformat wird eine EDM erstellt [ofizielle Web-Seite](http://pro.europeana.eu/page/edm-documentation). 

Im DNSCore werden lediglich die core Klassen des EDM erzeugt. Hier gibt es die Elemente 

1.  edm:ProvidedCHO - beinhaltet das bereitgestellte Objekt für das Kulturerbe z.B. das eigentliche Bild
1.	ore:Aggregation - Aggregation zur Gruppierung der Klassen 

#### Provided cultural heritage object (edm:ProvidedCHO)

Diese Objektgruppe kann im DSNCore folgende Elemente beinhalten:

* dc:title	 		- 'der Titel des CHO'
* dc:contributor 	- 'der Beitragender des CHO' 
* dc:creator 		- 'der Ersteller des CHO'
* dc:date			- 'ein markantes Datum für das CHO'
* edm:hasType		- 'Angabe des Typs des CHO (basierend auf Thesaurus)'
* dc:identifier		- 'Kennzeichnung (ID) des original CHO'
* dc:publisher		- 'Name des Herausgebers des CHO' 
* dcterms:hasPart	- 'beschreibt eine Resource, die entweder physikalisch oder logisch in das CHO eingebunden ist'
* dcterms:isPartOf	- 'beschreibt eine Resource, in die das CHO physikalisch oder logisch eingebunden ist'

#### Properties for the aggregation (ore:Aggregation)

Diese Objektgruppe kann im DSNCore folgende Elemente beinhalten:

* edm:dataProvider		- 'der Name des data providers des Objektes' 
* edm:isShownBy			 
* edm:isShownAt			 
* edm:aggregatedCHO		- 'die ID des Quellobjektes an sich. Dies kann eine vollwertige URL sein oder eine interne ID
* edm:hasView			- 'die URL der Web-Resource, welche die digitale Repräsentation des Objektes widerspiegelt'
* edm:object			- 'die URL des Objektes, welche für eine Vorschau für das Europeana Portal verwendet wird'
* edm:provider			- 'Name oder ID des providers des Objektes'


Beispielhaft Struktur im DNSCore

	<rdf:RDF>
		<edm:ProvidedCHO rdf:about="http://data.danrw.de/cho/1-20160922833-ISIL/lido/Inventarnummer">
			<edm:hasType>is root element</edm:hasType>
			<dc:title>Küchenmaschine</dc:title>
			<dc:date>01.01.1950-31.12.1959</dc:date>
			<dc:date>01.01.1950-31.12.1969</dc:date>
			<dc:identifier>1-20160922833</dc:identifier>
			<dc:identifier>urn:nbn:de:danrw-1-20160922833</dc:identifier>
			<dc:publisher>Bergisch Gladbach</dc:publisher>
		</edm:ProvidedCHO>
		<ore:Aggregation rdf:about="http://data.danrw.de/aggregation/1-20160922833-ISIL/lido/Inventarnummer">
			<edm:aggregatedCHO rdf:resource="http://data.danrw.de/cho/1-20160922833-ISIL/lido/Inventarnummer"/>
			<edm:isShownBy rdf:resource="http://data.danrw.de/file/1-20160922833/_c3836acf068a9b227834e0adda226ac2.jpg"/>
			<edm:object rdf:resource="http://data.danrw.de/file/1-20160922833/_c3836acf068a9b227834e0adda226ac2.jpg"/>
		</ore:Aggregation>
	</rdf:RDF>

## mögliche Formate
[Formatbeschreibung](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/specification_metadata.de.md)

### LIDO.xml

Formatbeschreibung siehe [Schema v1.0](http://lido-schema.org/schema/v1.0/lido-v1.0-schema-listing.html)

Mappingbeschreibung [LIDO -> EDM](https://wiki1.hbz-nrw.de/display/DANOPEN/LIDO+to+EDM)

### EAD.xml

Formatbeschreibung siehe [offizielle EAD Spezifikation](http://www.loc.gov/ead/index.html), oder spezielle [DDB-EAD-Spezifikation](http://www.dlib.indiana.edu/services/metadata/activities/EADManual.pdf)

Mappingbeschreibung [EAD -> EDM](https://wiki1.hbz-nrw.de/display/DANOPEN/EAD+zu+EDM)

### METS

Formatbeschreibung siehe [offizielle METS Spezifikation](http://www.loc.gov/standards/mets/)

Mappingbeschreibung [METS -> EDM](https://wiki1.hbz-nrw.de/display/DANOPEN/METSMods+zu+EDM)


	 



	





