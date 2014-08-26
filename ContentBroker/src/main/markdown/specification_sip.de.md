	/*
	  DA-NRW Software Suite | ContentBroker
	  Copyright (C) 2013 Historisch-Kulturwissenschaftliche Informationsverarbeitung
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

# SIP Spezifikation

[Englische](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/specification_sip.md) Version.

## SIP - Generelle Struktur

### Containerformate

### Containername

### Bagit

### PREMIS

### Original Name

### Encoding

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
    
    
    










