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

# SIP Spezifikation

[Englische](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/specification_sip.md) Version.

## SIP - Generelle Struktur

Die generelle Struktur eines von DNSCore verarbeitbaren **SIP**s sieht wie folgt aus.

    meinSIP.(tgz|zip|tar)
        meinSIP/
        	bag-info.txt
        	bagit.txt
        	manifest-md5.txt
        	tagmanifest-md5.txt
        	data/
        		premis.xml
        		someFile1.x
        		einUnterOrdner/eineDatei.x

Ein SIP ist immer verpackt in einem der zulässigen Containerformate.
Erlaubte Dateiendungen sind

* .tgz
* .zip
* .tar

Der Dateiname des Containers inklusive der Dateiendung wird technisch
als **Containername** bezeichnet.

Das entpackte SIP enthält auf der ersten Hierarchieebene einen Ordner, 
der denselben Namen trägt wie der Name des SIP ohne die Dateiendung.
Dieser Ordner wiederum enthält Checksummen zum Paket im bagIt-Format.
Die vier bagIt-Dateien dienen dem Zweck der Integritätsprüfung, d.h. zur 
Sicherstellung dessen, dass die Daten auf dem Weg vom Endnutzer hinein
in das System nicht verändert wurden.

### Original Name

Der Original-Name eines SIP, technisch **OriginalName** genannt, besteht
aus dem Containernamen ohne die Dateiendung. Dieser Name trägt eine spezielle
Bedeutung, da eine pro Nutzer eindeutige Kennung eines Objektes innerhalb 
des Systemes darstellt. 

### PREMIS

Innerhalb des "data"-Verzeichnisses muss sich eine Datei mit genau dem Dateinamen **premis.xml**
berfinden. Diese Datei muss zunächst dem Premis-Standard selbst gehorchen, wie er [hier](http://www.loc.gov/standards/premis/v2/premis-2-2.pdf) beschrieben ist.

Die PREMIS-Datei eines SIPs dient der Übemittlung von festgelegten Vertragseinstellungen zwischen dem 
einem Vertragspartner (contractor) und einem Knoten eines DNSCore-Systems. Die enthaltenen Einstellungen
beschreiben z.B. das Verhalten des Systemes bezüglich der Migration oder der Veröffentlichung.

Die PREMIS-Datei wird vom System entgegengenommen, ausgewertet und die Einstellungen in entsprechende Handlungen
übernommen. Anschließend wird die PREMIS-Datei mit Informationen über die Paketverarbeitung angereichert und als
Teil des AIP gespeichert. Sie dient damit der Nachvollziehbarkeit der Pakethistorie und ist ein integraler Bestandteil
der Langzeitarchivierung mit DNSCore. Eine detaillierte Spezifikation des dazu von DNSCore festgelegten 
Elemente-Vokabulars findet sich [hier](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/premis_specification.md).

### Nutzergesteurte URN-Vergabe

Im Normalfall wird einem Objekt innerhalb eines DNSCore-Gesamtsystems während der Paketverarbeitung der **Ersteinlieferung**
ein eindeutiger technischer **Identifier** zugewiesen. Von diesem abgeleitet, 
wird dem Objekt ebenfalls eine eindeutige **URN** zugewiesen.

Es ist dem Vertragspartner jedoch auch möglich, Einfluss auf die Zuordnung einer URN zu einem Objekt zu nehmen. 
Hierzu muss die mitgelierferte **premis.xml** folgende Eintrage aufweisen:

```xml
<object xsi:type="representation">
    <objectIdentifier>
        <objectIdentifierType>URN</objectIdentifierType>
	<objectIdentifierValue>urn:nbn:de:xyz-1-20131008367735</objectIdentifierValue>
    </objectIdentifier>
</object>
```

Die URN urn:nbn:de:xyz-1-20131008367735 würde anschließend vom System ausgelesen und verwendet, anstatt eine neue URN
aus dem generierten Identifier abzuleiten.

### Encoding

Dateinamen innerhalb von SIPs müssen UTF-8 enkodiert sein. Der Pfadseparator ist ein Unix style Slash.

## Richtlinien für die Strukturierung von DIPs

#### Dateinamen

Wenn man den relativen Pfad einer Datei (d.h. beim SIP ausgehend vom data - Ordner) nimmt und die Extension abtrennt, erhält man den Dokumentnamen einer Datei. Das Konzept des Dokumentes wurde entwickelt, um einen zeitlichen Verlauf von Dateien über die Lebensdauer eines Objektes zu modellieren. So ist es möglich, Nachfolger von Dateien, die z.b. durch Konversionen oder Deltas entstehen, abzubilden. Mehr zm Konzept des Dokumentes finden man [hier](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/object_model.de.md#document---das-dokument).

Das Konzept des Dokumentes erfordert dann jedoch, dass innerhalb einer Einlieferung eines SIPs eine Datei eindeutig einem Dokument entspricht, dass heisst, dass jeder Dokumentname nur einmal in einem SIP vorkommen darf. Verboten innerhalb eines SIP sind dann somit Kombinationen wie

    data/abc.jpg
    data/abc.tif

oder 

    data/unterordner/cde.jpg
    data/unterordner/cde.tif
    
da im ersten Fall der Dokumentname [abc], im zweiten Fall [unterordner/cde] doppelt vergeben würde. Das System weist solche SIPs mit einem Benutzerfehler zurück.

Wenn es wirklich gewünscht ist, Dateien mit verschiedenen Formatendungen innerhalb eines SIPs einzuliefern, so muss der Benutzer dies explizit kenntlich machen durch Verwendung von Unterordnern.

    data/jpgs/abc.jpg
    data/jpgs/cde.jpg
    data/tifs/abc.tif
    data/tifs/cde.jpg
    
Hier liegen vier eindeutige Dokumentnamen vor, nämlich

    jpgs/abc
    jpgs/cde
    tifs/abc
    tifs/cde
