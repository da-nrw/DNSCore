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
als **ContainerName** bezeichnet.

Das entpackte SIP enthält auf der ersten Hierarchieebene einen Ordner, 
der denselben Namen trägt wie der Name des SIP ohne die Dateiendung.
Dieser Ordner wiederum enthält Checksummen zum Paket im bagIt-Format.
Die vier bagIt-Dateien dienen dem Zweck der Integritätsprüfung, d.h. zur 
Sicherstellung dessen, dass die Daten auf dem Weg vom Endnutzer hinein
in das System nicht verändert wurden. Auf derselben Hierarchieebene befindet sich
das **data**-Verzeichnis welche die Nutzerdaten behinhaltet. Für den Inhalt des
**data**-Verzeichnisses gilt die Minimalvorgabe, dass es mindestens eine Datei
mit dem Namen premis.xml beinhalten muss 
(siehe Abschnitt [unten](specification_sip.de.md#premis)).

### Original Name

Der Original-Name eines SIP, technisch **OriginalName** genannt, besteht
aus dem Containernamen ohne die Dateiendung. Dieser Name trägt eine spezielle
Bedeutung, da eine pro Nutzer eindeutige Kennung eines Objektes innerhalb 
des Systemes darstellt. Wird zu einem späteren Zeitpunkt ein weiteres Paket mit dem einem bereits vergebenen 
Originalnamen eingeliefert, so behandelt das System dieses Paket als ein **Delta** zu einem bereits vorhandenen Objekt.

### PREMIS

Innerhalb des **data**-Verzeichnisses muss sich eine Datei mit genau dem Dateinamen **premis.xml**
berfinden. Diese Datei muss zunächst dem Premis-Standard selbst gehorchen, wie er [hier](http://www.loc.gov/standards/premis/v2/premis-2-2.pdf) beschrieben ist.

Die PREMIS-Datei eines SIPs dient der Übemittlung von festgelegten Vertragseinstellungen zwischen dem 
einem Vertragspartner (contractor) und einem Knoten eines DNSCore-Systems. Die enthaltenen Einstellungen
beschreiben z.B. das Verhalten des Systemes bezüglich der Migration oder der Veröffentlichung.

Die PREMIS-Datei wird vom System entgegengenommen, ausgewertet und die Einstellungen in entsprechende Handlungen
übernommen. Anschließend wird die PREMIS-Datei mit Informationen über die Paketverarbeitung angereichert und als
Teil des AIP gespeichert. Sie dient damit der Nachvollziehbarkeit der Pakethistorie und ist ein integraler Bestandteil
der Langzeitarchivierung mit DNSCore. Eine detaillierte Spezifikation des dazu von DNSCore festgelegten 
Elemente-Vokabulars findet sich [hier](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/specification_premis.md).

### URN-Vergabe

Im Normalfall wird einem Objekt innerhalb eines DNSCore-Gesamtsystems während der Paketverarbeitung der **Ersteinlieferung**
ein eindeutiger technischer **Identifier** zugewiesen. Von diesem abgeleitet, 
wird dem Objekt ebenfalls eine eindeutige **URN** zugewiesen.

#### Nutzergesteuerte URN-Vergabe

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
Die URN <code>urn:nbn:de:xyz-1-20131008367735</code> würde anschließend vom System ausgelesen und verwendet, anstatt eine neue URN aus dem generierten Identifier abzuleiten.

Bitte beachten Sie, dass keinerlei Validierung oder Prüfung des angegebenen Wertes stattfindet. Der Wert des vorgesehenen Feldes wird "as is" ausgelesen und verwendet. Dementsprechend findet auch keine Prüfung hinsichtlich der Eindeutigkeit
der vergebenen URN oder ähnliches statt.

#### URNs und Deltas

URNs werden nur ein einziges Mal vergeben. Wird ein Delta eingeliefert, so wird die bisher vergebene URN weiter verwendet,
unabhängig davon, ob die ursprünglich vergebene URN automatisch generiert und nutzergesteuert vergeben wurde und unabhängig davon, ob eine neue URN im Delta nutzergesteuert mitgeliefert wird.

### Encoding

Dateinamen innerhalb von SIPs müssen UTF-8 enkodiert sein. Der Pfadseparator ist ein Unix style Slash.

## Richtlinien für die Strukturierung von SIPs

Wie bereits erwähnt, herrscht systemseitig neben des Vorhandenseins 
der premis.xml keine grundsätzliche Anforderung an den Inhalt des **data**-Verzeichnisses.

Ansonsten kann der Nutzer den Inhalt des **data**-Verzeichnisses selbst gestalten. Er kann dabei auch die Ordnerhierarchie
innerhalb des data-Verzeichnisses selbst gestaltet. Einige wenige Regeln müssen jedoch genau beachtet werden. Diese
werden im folgenden beschrieben. Missachtung dieser Regeln führt unweigerlich dazu, dass SIPs vom System zurückgewiesen 
werden, quittiert mit einer Email an den Nutzer.

#### Besonderheiten bei unterstüzten Metadatenformaten

Weitere Anforderungen bestehen, sofern der Nutzer von erweiterten 
Leistungsmerkmalen bezüglich der Metadatenverarbeitung und Publikation profitieren möchte.
Dann müssen die in [diesem](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/2015-01-14_Metadaten_in_DA-NRW.pdf?raw=true) Dokument beschriebenen Regeln befolgt werden.

#### Dateinamen

Wenn man den relativen Pfad einer Datei (d.h. beim SIP ausgehend vom data - Ordner) nimmt und die Extension abtrennt, erhält man den Dokumentnamen (technisch **DocumentName**) einer Datei. Das Konzept des Dokumentes wurde entwickelt, um einen zeitlichen Verlauf von Dateien über die Lebensdauer eines Objektes zu modellieren. So ist es möglich, Nachfolger von Dateien, die z.b. durch Konversionen oder Deltas entstehen, abzubilden. Mehr zm Konzept des Dokumentes finden man [hier](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/object_model.de.md#document---das-dokument).

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
