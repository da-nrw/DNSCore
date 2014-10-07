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

# Formatkonversion mit DNSCore

Formatkonversionen in DNSCore basieren auf einem Modell von [Konversionsrichtlinien](object_model.de.md#conversionpolicy---die-regel-zur-anwendung-einer-konversion) (technisch: [ConversionPolicy](../java/de/uzk/hki/da/model/ConversionPolicy.java)) und [Konversionsroutinen](object_model.de.md#conversionroutine---die-konversionsroutine) (technisch: [ConversionRoutine](../java/de/uzk/hki/da/model/ConversionRoutine.java)). **Konversionsroutinen** beschreiben ein Verfahren, mit dessen Hilfe eine Datei eines bestimmten Formates in ein anderes Zielformat konvertiert werden kann. **Konversionsrichtlinien** hingegen legen fest, welche **Konversionsroutinen** für Dateien mit bestimmten Dateiformaten durchzuführen sind, nachdem ebendiese Dateiformate vom System erkannt wurden.

![Bild](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/object_model_object_users.jpg)

Sowohl **Konversionsrichtlinien** als auch **Konversionsroutinen** sind Eigenschaften des **Gesamtsystems**. Wenn eine Konversionsroutine im System angemeldet wird, so bedeutet dies, dass alle **Knoten** des Systems diese unterstützen. Das erfordert in den meisten Fällen Synchronisation der Knotenadministratoren untereinander, die entsprechende Konverter in gleicher Version auf den von ihnen betreuten Knoten bereitstellen.

## Funktionsweise - Kurzfassung

Im Verlaufe des Ingest-(möglicherweise auch weiterer) Workflow des ContentBroker wird für jede Datei eine SIPs eine [Formaterkennung](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/administration_format_identification.de.md) durchgeführt. Als Ergebnis dieser Formaterkennung wird der Datei ein Identifier nach dem [PRONOM](http://apps.nationalarchives.gov.uk/PRONOM/Default.aspx)-Schema zugeordnet.

Auf die Erkennung eben genau dieser PUIDs hin können Konversionsroutinen ausgeführt werden. Eine Konversionsroutine kann mit beliebig vielen Eingangsformaten verknüpft werden. Die Verknüpfung geschieht mithilfe der Konversionsrichtlinien.



## Anlegen und Testen von neuen Konversionsrichtlinien und Routinen

TODO CLI wird hier beschrieben.

**1 - Evaluationsphase** Um eine neue Konversionsroutine systemweit bereitstellen zu können, muss diese zunächst im Vorgeld evaluiert werden. 

Hier sind zwei Fälle zu unterscheiden. 

1. Die Routine basiert auf Java-Code. Dies ist für auf die Publikation ausgerichtete Konversionsroutinen immer der Fall, für die LZA gibt es aber auch einige Fälle. In diesem Falle kann die Evaluationsphase insofern abgeschlossen werden, als dass die [Konversionsroutinentypen](#typen-von-konversionsroutinen) selbst genau vorgeben, für welche Eingangsformate sie eingesetzt werden dürfen, welche Zielformate sie produzieren, und welche Tools (falls nicht Java-) auf der Unix-Umgebung eingerichtet werden müssen, damit sie eingebunden werden können.

1. Die Routine basiert auf einem Unix-Kommandozeilenaufruf. Der Kommandozeilenaufruf muss notiert werden. Die Platzhalter "input" und "output" markieren dabei die Stellen, an denen der ContentBroker später die Pfade konkreter Dateien eintragen kann.

**2 - Installationsphase** Als nächstes müssen alle Administratoren des Systems auf den jeweils von ihnen betreuten Knoten die entsprechend Konverter (falls durch die Konversionsroutine benötigt) in der spezifizierten Version eingerichtet werden. 

**3 - Anmeldungsphase** Dann müssen die Einträge in der Datenbank eingerichtet werden. Dies geschieht manuell per sql-insert Statements. Die Beschreibungen der Tabllenstruktur ist weiter [unten](#einrichten--db) beschrieben. Es der Eintrag für die Konversionsroutine und verschiedene Einträge für die Konversionrichtlinien hinzugefügt werden. Für jede mögliche PUID, auf die hin eine Konversionsroutine getriggert werden soll, muss eine gesonderte Zeile in die Policies-Tabelle eingefügt werden.

**4 - Konfigurationstestphase** Abschließend muss für jede der neuen Richtlinien ein Test durchgeführt werden, welcher die Konversion durchführt. Eine Datei mit dem spezifizierten Quellformat wird dazu in einem SIP abgelegt und im System zum Ingest abgegeben. Anschließend wird das Paket retrieved und die Zieldatei geprüft.

**Bitte beachten** Die Konfigurationstest für jede der Policies müssen auf jedem Knoten gesondert durchgeführt werden. Sie werden zwar systemweit festgelegt, jedoch kann nur ein auf den jeweiligen Knoten durchgeführter Test zeigen, ob die entsprechenden Konverter richtig installiert sind und korrekt arbeiten.


## Einrichten / DB

### Datenmodell - Konversionsrichtlinie

Für die Policies ist die Tabelle "conversion_policies" eingerichtet.

    source_format: varchar
    conversion_routines_id: int
    presentation: boolean
    
**source_format** Erklärung

**conversion_routines_id** Erklärung

**presentation** Erklärung

### Datenmodell - Konversionroutine

Für die Routinen ist die Tabelle

    name
    type
    target_suffix
    params
    
**name** Ein beliebig zur wählender Name für die Konversionsroutine. Er kann so gewählt werden, das er z.B. Aufschluss über die eingesetzten Konverter liefert.

**type** Hier ist ein vollqualifizierter Name einer Java-Klasse einzusetzen. Die zur Verfügung stehenden Typen sind in einem folgenden [Abschnitt](administration_format_conversion.de.md#typen-von-konversionsroutinen) beschrieben.

**params** Optionaler Parameter für kommandozeilenbasierte Konversionsroutinentypen (type=CLIConversionStrategy bzw. PublishCLIConversionStrategy). Kann bei anderen Konversionsroutinentypen leer bleiben. 

"params" spezifiziert den Kommandozeilenaufruf inklusive der Platzhalter für die Ein- und Ausgangsdatei. Im einfachsten Falle kann dies wie folgt aussehen

    convert input output

Der ContentBroker würde die Platzhalter durch die entsprechenden Pfade der Ein- und Ausgangsdateien ersetzen und den Befehl convert (Bestandteil von ImageMagick) auf der Kommandozeile absetzen.

**target_suffix** Optionaler Parameter für kommandozeilenbasierte Konversionsroutinentypen (type=CLIConversionStrategy bzw. PublishCLIConversionStrategy). Kann für die übrigen Konversionsroutinentypen leer bleiben.


## Typen von Konversionsroutinen

**de.uzk.hki.da.format.CLIConversionStrategy.java**

Setzt einen beliebigen Befehl auf der Kommandozeile ab und kann somit jegliche von dort aufrufbare Converter einbinden. Benötigt entsprechende Werte für "params" und "target_suffix".

**de.uzk.hki.da.format.PublishImageConversionStrategy.java**
 
**de.uzk.hki.da.format.PublishAudioConversionStrategy.java**

**de.uzk.hki.da.format.PublishVideoConversionStrategy.java**

**de.uzk.hki.da.format.PublishImageConversionStrategy.java**

**de.uzk.hki.da.format.PublishPDFConversionStrategy.java**

## Workflow des Systems zur Formatkonversion.

![Formatkonversionsworkflow](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/format_conversion_workflow.jpg)

Ingest
Migration
PIPGenerierung

## Funktionsweise - Workflow

![ConversionInstructions](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/object_model_conversion_dafiles.jpg)

Generierung der Konversionsinstruktionen



