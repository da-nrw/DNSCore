	/*
	  DA-NRW Software Suite | ContentBroker
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

# Objektmodell-Referenz

Die Java [Object](../java/de/uzk/hki/da/model/Object.java) Klasse.

### Object - Das Objekt

![](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/object_model_objects_packages.jpg)

Die fundamentale Klasse des Datenmodells heisst einfach "Object". Ein Objekt repräsentiert eine logisch zusammenhängende Sammlung von Dateien. Den Zusammenhang der Dateien legt dabei der User fest (siehe pre_ingest.md). Jedes Objekt innerhalb innerhalb eines PreservationSystems trägt einen eindeutigen technischen Identifier, der zum Identifizieren und Auffinden von Objekten innerhalb des PreservationSystems dienen kann. Dieser technische Identifier wird einem User daher  (Contractor) am Ende eines Ingest-Workflows übermittelt.

### Package - Das Informationspaket

### User - Der Benutzer

![](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/object_model_object_users.jpg)

Ein Benutzer ist ein Anwender der Applikation. Endnutzer verwenden das System auf verschiedene Art und Weise, je nach Rolle. Derzeit sind drei Benutzerrollen möglich:

* PreservationSystem Administrator - Fachlicher Ansprechpartner für ein PreservationSystem
* Node Administrator - Technischer Ansprechpartner für einen Knoten
* Contractor - 

Contractoren sind die Endanwender der Applikation. Sie liefern ihre Pakete immer auf jeweils einem Knoten ein.

### PreservationSystem - Das Gesamtsystem

The Java [PreservationSystem](../java/de/uzk/hki/da/model/PreservationSystem.java) class.

Die Klasse PreservationSystem repräsentiert das Gesamtsystem. Es besteht aus einzelnen Knoten, auf denen Contractoren ihre Objekte einliefern. Das Gesamtsystem hält die global gültigen Regeln zur Anwendung von Konversionen vor und bestimmt, welche Konversionsroutinen von allen Knoten gleichermaßen angeboten werden. 

### Node - Der Knoten


The Java [Node](../java/de/uzk/hki/da/model/Node.java) class.

An einem Knoten liefern Contractoren ihre Objekte ein. Die Zuordnung eines Contractors zu einem Knoten, seinem sogenannten Primärknoten, ist eindeutig. Jeder Contractor liefert seine Objekte immer an seinem Primärknoten ein. Der Knoten steht dabei für ein konkretes technisches System am Standort eines Betreibers. Verschiedene Knoten verschiedener Betreiber an verschiedenen Standorten formen ein PreservationSystem. Die Daten der Contractoren werden zwischen den Knoten, die das PreservationSystem formen, gespiegelt. Die Am Primärknoten eingelieferten Kopien der Objekte werden daher formell auch Primärkopien genannt.


### ConversionPolicy - Die Regel zur Anwendung einer Konversion

The Java [ConversionPolicy](../java/de/uzk/hki/da/model/ConversionPolicy.java) class.

![](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/object_model_conversion_dafiles.jpg)

### ConversionRoutine - Die Konversionsroutine

The Java [ConversionRoutine](../java/de/uzk/hki/da/model/ConversionRoutine.java) class.

### DAFile

Die Java [DAFile](../java/de/uzk/hki/da/model/DAFile.java) Klasse.

Bitte beachten Sie, dass das tatsächlich implementierte Modell vom in der Nachfolgenden Skizze gezeigten konzeptuellen Modell abweicht. Die Repräsentation ist als Teil der DAFile modelliert. Das Dokument implizit über die Kombination von rep_name und relative_path.

![](https://raw.github.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/object_model_dafiles_documents.jpg)

Jede der zu einem Objekt gehörenden Dateien entspricht einem DAFile im Datenmodell. Das DAFile speichert einige Metadaten, die für die Geschäftslogik von DNSCore wichtig sind.

So wird zum Beispiel die PUID ([PRONOM Identifier]((http://www.nationalarchives.gov.uk/PRONOM/Default.aspx)))gespeichert, welche das Format der Datei, wie es von [FIDO](http://www.openplanetsfoundation.org/software/fido) ermittelt wurde.

Weiterhin wird über die Klasse DAFile die Zugehörigkeit des Files zu einer bestimmten Repräsentation des Objektes modelliert. Die Repräsentation ist dabei als rep_name gekennzeichnet. Der Pfad der Datei innerhalb der Repräsentation als relative_path. Konkrete Beispiele für die Verwendung von Repräsentationen siehe [AIP-Spezifikation](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/aip_specification.md).

#### Representation - Die Repräsentationen

Repräsentationen dienen dazu, die Objekthistorie nachvollziehbar im Dateisystem abzubilden. Für jedes Paket, welches zu einem Objekt abgeliefert wird, werden zwei Repräsentation eingeliefert. Die +a Repräsentation enthält die Daten des Nutzers in unveränderter Form. Die +b Repräsentation enthält konvertierte oder modifizierte Daten. Eine Repräsentation hat die Form jjjj_mm_tt+ss_mm+x. Durch alphabetische Sortierung über die Repräsentationsnamen ist die Objekthistorie sofort transparent (siehe auch Abschnitt "Dokument").  

Ein Beispielobjekt, welches eine Originallieferung und zwei Deltas umfasst, könnte dann z.B. die folgenden sechs Repräsentationen umfassen

    2014_10_01+12_12+a
    2014_10_01+12_12+b
    2015_10_03+10_00+a
    2015_10_03+10_00+b
    2016_09_03+01_10+a
    2016_09_03+01_10+b

Der relative Pfad einer Datei wird immer relativ zu einer dieser Repräsentation ausgedrückt. Eine Datei

    [WorkArea]/[csn]/[oid]/data/2014_10_01+12_12+a/subfolder/abc.tif

hat den Repräsentationsnamen "2014_10_01+12_12+a" und den relativen Pfad "subfolder/abc.tif". 

Wichtig ist, dass die Repräsentationen ein von den konkreten Paketen eines Objektes unabhängiges Konzept darstellen (TODO Repackaging).

#### Document - Das Dokument

Ein Dokument innerhalb eines Objektes ist eindeutig definiert über den relativen Pfad eines DAFile ohne die Extension. Die Dateien

    2014_10_01+12_12+a/subfolder/1.jpg
    2014_10_01+12_12+b/subfolder/1.tif
    
tragen beide den Dokumentennamen "subfolder/1" und werden somit vom System als ein logisches Dokument aufgefasst. Die Datei "1.tif" wird als Nachfolger der Datei "1.jpg" aufgefasst. Dieser kann durch Modifikation, Konversion oder durch Nachlieferung per Delta zustandegekommen sein. Für diese Ordnungssystematik ist es erforderlich, dass es nur ein DAFile mit demselben Dokumentennamen pro Repräsentation vorhanden ist. 
    

