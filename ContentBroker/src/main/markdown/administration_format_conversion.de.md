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

Formatkonversionen in DNSCore basieren auf einem Modell von [Konversionsrichtlinien](#datenmodell---konversionsrichtlinie) (technisch: [ConversionPolicy](../java/de/uzk/hki/da/model/ConversionPolicy.java)) und [Konversionsroutinen](#datenmodell---konversionroutine) (technisch: [ConversionRoutine](../java/de/uzk/hki/da/model/ConversionRoutine.java)). **Konversionsroutinen** beschreiben ein Verfahren, mit dessen Hilfe eine Datei eines bestimmten Formates in ein anderes Zielformat konvertiert werden kann. Im Verlaufe des Ingest-(möglicherweise auch weiterer) Workflow des ContentBroker wird dazu für jede Datei eine SIPs die [Formaterkennung](https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/administration_format_identification.de.md) durchgeführt. Als Ergebnis dieser Formaterkennung wird der Datei ein Identifier nach dem [PRONOM](http://apps.nationalarchives.gov.uk/PRONOM/Default.aspx)-Schema zugeordnet. Auf die Erkennung eben genau dieser PUIDs hin können *Konversionsroutinen* ausgeführt werden. Eine *Konversionsroutine* kann mit beliebig vielen Eingangsformaten verknüpft werden. Die Verknüpfung von Eingangsformat zur *Konversionsroutine* geschieht mithilfe der **Konversionsrichtlinien**.

![Bild](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/object_model_object_users.jpg)

Sowohl *Konversionsrichtlinien* als auch *Konversionsroutinen* sind Eigenschaften des **Gesamtsystems**. Wenn eine Konversionsroutine im System angemeldet wird, so bedeutet dies, dass alle **Knoten** des Systems diese unterstützen. Das erfordert in den meisten Fällen Synchronisation der Knotenadministratoren untereinander, die entsprechende Konverter in gleicher Version auf den von ihnen betreuten Knoten bereitstellen.

## Anlegen und Testen von neuen Konversionsrichtlinien und Routinen

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

Die Java-Klasse [ConversionPolicy](../java/de/uzk/hki/da/model/ConversionPolicy.java).

Für die Policies ist die Tabelle "conversion_policies" eingerichtet. Sie besitzt folgende wichtige Felder:

    source_format: varchar
    conversion_routines_id: int
    presentation: boolean
    
**source_format** Der Pronom Unique Identifier (PUID). Wird das Format einer Datei mit einem entsprechenden Identifier erkannt, so wird greifen alle Policies, die mit dem entsprechenden source_format übereinstimmen.

**conversion_routines_id** Der Primärschlüssel der Datenbanktabelle "conversion_routines". Der ContentBroker wird für die Datei später im Workflow die mit der conversion_routines_id markierte Routine ausführen, um die Datei zu konvertieren.

**presentation** Für LZA-Konversionen ist hier false, für Präsentationskonversionen ist hier true einzutragen.

### Datenmodell - Konversionroutine

Die Java-Klasse [ConversionRoutine](../java/de/uzk/hki/da/model/ConversionRoutine.java)

Für die Routinen ist die Tabelle "conversion_routines" eingerichtet. Sie besitzt folgende wichtige Felder:

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

Im Falle von kommandozeilenbasierten Routinentypen wird im Platzhalter output der Dateiname aus input übernommen, das Suffix jedoch durch target_suffix ersetzt. Bei einem Aufruf von ImageMagick determiniert dies automatisch das Zielformat.

## Datenmodell - KonversionsInstruktion

Die Java-Klasse [ConversionInstruction](../java/de/uzk/hki/da/model/ConversionInstruction.java)

Konversionsinstruktionen werden automatisch vom ContentBroker generiert, um Erkennungs- und Entscheidungsprozesse im ContentBroker von Durchführungsprozessen sauber zu trennen. Da Konversionsinstruktionen automatisch generiert werden, spielen sie für die fachliche Administration des Systems keine Rolle, wohl aber für die technische Administration des Systemes. Bei fehlgeschlagenen Aufräumprozessen kann es notwendig sein, die Datenbank von Zeit zu Zeit zu bereinigen. Hierzu ist ein Verständnis der [Arbeitsweise](#workflow-des-systems-zur-formatkonversion) des ContentBroker bzw. des zugrundeliegenden Datenmodells vonnöten.

![ConversionInstructions](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/object_model_conversion_dafiles.jpg)

TODO beschreibung

## Typen von Konversionsroutinen

**[de.uzk.hki.da.format.CLIConversionStrategy.java](../java/de/uzk/hki/da/format/CLIConversionStrategy.java)**

Setzt einen beliebigen Befehl auf der Kommandozeile ab und kann somit jegliche von dort aufrufbare Converter einbinden. Benötigt entsprechende Werte für "params" und "target_suffix".

**[de.uzk.hki.da.format.PublishImageConversionStrategy.java](../java/de/uzk/hki/da/format/PublishImageConversionStrategy.java)**

Das Zielformat ist jpg. Erstellt jeweils Zielformate für die "public" und "institution" PIPs für die Publikation (Policies -> presentation=true).

Benötigt wird das Programm **ImageMagick**. Die **Version** ist unspezifiziert. Der Kommandozeilenaufruf auf "convert" muss für den ContentBroker global sichtbar sein (evtl. Umgebungsvariablen setzen).
 
**[de.uzk.hki.da.format.PublishAudioConversionStrategy.java](../java/de/uzk/hki/da/format/PublishAudioConversionStrategy.java)**

Das Zielformat ist mp3. Erstellt jeweils Zielformate für die "public" und "institution" PIPs für die Publikation (Policies -> presentation=true). 

Benötigt wird das Programm **sox**. Die **Version** ist unspezifiziert. Der Kommandozeilenaufruf auf "sox" muss für den ContentBroker global sichtbar sein (evtl. Umgebungsvariablen setzen).

**[de.uzk.hki.da.format.PublishVideoConversionStrategy.java](../java/de/uzk/hki/da/format/PublishVideoConversionStrategy.java)**

Das Zielformat ist mpeg4 im avi-Container. Erstellt jeweils Zielformate für die "public" und "institution" PIPs für die Publikation (Policies -> presentation=true). 

Benötigt wird das Programm **HandBrakeCLI**. Die **Version** ist unspezifiziert. Der Kommandozeilenaufruf auf "HandBrakeCLI" muss für den ContentBroker global sichtbar sein (evtl. Umgebungsvariablen setzen).

**[de.uzk.hki.da.format.PublishPDFConversionStrategy.java](../java/de/uzk/hki/da/format/PublishPDFConversionStrategy.java)**

Das Zielformat ist mpeg4 im avi-Container. Erstellt jeweils Zielformate für die "public" und "institution" PIPs für die Publikation (Policies -> presentation=true). 

Benötigt wird das Programm **GhostScript**. Die **Version** ist unspezifiziert. Der Kommandozeilenaufruf auf "ghostscript" muss für den ContentBroker global sichtbar sein (evtl. Umgebungsvariablen setzen).

## Workflow des Systems zur Formatkonversion.

Die Formatkonversionen bilden einen zentralen Bestandteil der grundlegenden Workflows des ContentBroker. Während des **Ingest-Workflows** werden Konversionen zur Langzeitarchivierung der Formate vorgenommen. Anschließend werden die konvertierten Dateien (und falls nicht konvertiert die Originale) Dateien, sofern möglich, d.h. sofern entsprechende Policies für die Formate eingerichtet sind, wiederum in für die Präsentation geeignete Formate umgewandelt. Dies geschieht jeweils zweifach, einmal für die Institutions- und einmal für die Public-PIPs.

Die PIPs können auch mithilfe des **PIP-Generierungs-Workflows** nachträglich neu generiert werden, etwa wenn sich die Standardformate der Publikation ändern. Hier werden alle Anlieferungen auf die WorkArea zurückgeladen, und die entsprechenden Konversionsinstruktionen auf Basis der Präsentationspolicies (presentation=true) generiert und ausgeführt.

Weiterhin kann es auch dazu kommen, das Formate im Langzeitspeicher veralten. In diesem Falle greift der **Migrations-Workflow**. Die betroffenen Objekte werden auf die WorkArea zurückgespielt für die veralteten Dateien werden erneut Formatkonversionen vorgenommen.

![Formatkonversionsworkflow](https://raw.githubusercontent.com/da-nrw/DNSCore/master/ContentBroker/src/main/markdown/format_conversion_workflow.jpg)

Im IngestWorkflow findet in der **ScanAction** zunächst eine Formaterkennung für alle Dateien der Anlieferung (SIP) und eine erneute Formaterkennung für alle Dateien aus eventuellen vorigen Anlieferungen statt. Pro Datei und Policy, die mit dem erkannten Dateiformat übereinstimmt, wird jeweils eine entsprechende Konversionsinstruktionen erstellt. In der anschließenden **ConvertAction** werden die Konversionsinstruktionen ausgewertet und die Konversionen durchgeführt. Die Konversionsergebnisse werden in der **CheckFormatsAciton** noch einmal gescannt. In der **ScanForPresentationAction** wird eine Oberflächenansicht der neuesten Dateien generiert. Die Dateiformate werden mit den Policies für die Präsentation (presentation=true) abgeglichen. Für jede Datei und alle jeweils mit dem Dateiformat entsprechenden Policies werden erneut Konversionsinstruktionen erstellt. Diese werden anschließend in der **ConvertAction** durchgeführt.



