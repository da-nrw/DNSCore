# Formaterkennung mit DNSCore

Die Erkennung von Formaten ist zentraler Bestandteil der Langzeitarchivierung. Durch das genaue Überprüfen der Formate einer jeden Datei und Hinterlegung dieser Information in der Objekt-Datenbank ist die Voraussetzung gegeben, zu jedem Zeitpunkt Maßnahmen zu ergreifen, die entsprechenden Pakete durch entsprechende Formatmigrationen auf einen neueren technischen Stand zu migrieren. Auf diese Weise können Daten langfristig auch über Technologiebrüche hinweg erhalten werden.

## Aufbau der Formaterkennung in DNSCore

Die Formaterkennung der DNSCore gliedert sich in zwei Stufen. Die erste Stufe orientiert sich an dem [PRONOM](http://apps.nationalarchives.gov.uk/PRONOM/Default.aspx) Standard der [National Archives](http://www.nationalarchives.gov.uk/). Die Erkennung der PUIDs (PRONOM Unique Identifier) geschieht dabei mithilfe des Programmes [FIDO](https://github.com/openplanets/fido). Eine zweite Stufe der Formaterkennung, die sog. Subformaterkennung, baut auf dem Ergebnis der primären Formaterkennung auf und kann die Dateien auf Kompressionsalgorithmen und Codecformate von Datenstreams innerhalb von Containerformaten überprüfen.

## Ablage der Formatinformationen in der Objektdatenbank

Als Ergebnis dieser Formatüberprüfung wird das einer physikalische Datei repräsentierende [DAFile](object_model.de.md#dafile) mit den entsprechenden Informationen für erkannten Primärformat und Subformat angereichert. Ob ein Wert für das Subformat ermittelt werden kann, hängt von der Konfiguration des Systemes, den installierten zusätzlichen Formatidentifiern, und dem Datenformat ab. Die Konfiguration wird weiter unten näher erläutert.

    Auszug Postgres Beschreibung der Tabelle "dafiles"
    format_puid               | character varying(255) | 
    subformat_identifier      | character varying(255) |

* **format_puid** Format Identifier im PRONOM Format
* **subformat_identifier** Subformat Identifier

Die Informationen zum DAFile werden während der Workflows der ContentBroker ermittelt und während der Laufzeit der Workflows in der Objektdatenbank vorgehalten. Am Ende des Ingest-Workflows werden diese Daten in die jedem Package zugehörige [premis.xml](specification_premis.md) serialisiert und aus der Objektdatenbank entfernt. Diese Maßnahme soll die Größe der Objektdatenbank minimieren. Um die in den Objekten enthaltenen Formate für spätere Maßnahmen der Langzeitarchivierung recherchierbar zu machen, werden stattdessen kommaseparierte Sets aller im Objekt enthaltenen Formate und Subformate generiert und als Teil des Objektes in der Datenbank dauerhaft vorgehalten:

    Auszug Postgres Beschreibung der Tabelle "objects"
    original_formats                 | character varying(255)      | 
    most_recent_formats              | character varying(255)      | 
    most_recent_secondary_attributes | character varying(255)      | 

* **original_formats** Kommasepariertes Set der PUIDs der jeweils neuesten Repräsentation der im Objekt enthaltenen Dateien
* **most_recent_formats** Kommasepariertes Set der PUIDs der gesamten im Objekt enthaltenen Dateien
* **most_recent_secondary_attributes** Kommasepariertes Set der Subformatidentifier der jeweils neuesten Repräsentation der im Objekt enthaltenen Dateien.

## Subformaterkennung

Aufbauend auf der primären Formaterkennung kann DNSCore eine erweiterte Formaterkennung durchführen. Der PreservationSystem-Administrator kann für erkannte Formate festlegen, ob, und wenn ja, mithilfe welcher Prozeduren,
die Subformaterkennung durchzuführen ist. Ergebnis dieser Subformaterkennung ist, dass File Format einer Datei um eine zustätzliche Information, den Subformat-Identifier angereichert wird. Die Identifier sind derzeit nicht standartisiert und hängen teilweise vom Output externer Programme ab.

Beispiel:

    Datei: abc.tif 
    -> Erkanntes Format: fmt/353
    Subformaterkennungsprozedur: ImageMagickSubformatIdentificationStrategy
    -> Erkanntes Subformat: lzw (lzw-codec)

Das Beispiel geht davon aus, dass die Prozedur "ImageMagickSubformatIdentificationStrategy" mit dem Format Identifier [fmt/353](http://apps.nationalarchives.gov.uk/PRONOM/Format/proFormatSearch.aspx?status=detailReport&id=1099) verknüft ist. Die als fmt/353 erkannte Beispieldatei durchläuft daraufhin die festgelegte Subformaterkennungsprozedur. Diese liefert "lzw" als Bezeichner für ein Kompressionsverfahren des im Tiff enthalteten Datenstrom zurück.

Für die Subformaterkennung stehen sowohl DNS-eigene Prozesse als auch  Wrapper-Klassen zur Verfügung, die die Dienste externer Programme wie z.B. ffmpeg für DNSCore nutzbar machen. Derzeit verfügbare Prozesse sind:

* [ImageMagickIdentificationStrategy](../java/de/uzk/hki/da/format/ImageMagickIdentifySubformatIdentificationStrategy.java)
* [XMLSubformatIdentificationStrategy](../java/de/uzk/hki/da/format/XMLSubformatIdentificationStrategy.java)
* [FFmpegSubformatIdentificationStrategy](../java/de/uzk/hki/da/format/FFmpegSubformatIdentificationStrategy.java)

## Einrichten der Subformaterkennung

### Zentrale Einrichtung 

Das Einrichten der Subformaterkennung ist Aufgabe des [PreservationSystem-Administrator](object_model.de.md#user---der-benutzer). Ihm kommt die Aufgabe zu, festzulegen, ob, und ja, mit welcher Prozedur Dateien der verschiedenen Primärformate auf Subformate geprüft werden. Diese Zuordnung wird in der Object-DB in der Tabelle "subformat_identification_strategy_puid_mappings" festgehalten. Diese Tabelle ist sehr einfach gehalten:

    Auszug Postgres Beschreibung der Tabelle "subformat_identification_strategy_puid_mappings"
    format_puid                            | character varying(255) | 
    subformat_identification_strategy_name | character varying(255) | 

* **format_puid** Der PRONOM Uniqe Identifier.
* **subformat_identification_strategy_name** Voll qualifizierter Name der FormatIdentificationStrategy implementierenden Java-Klasse, die zur Formatidentifikation der Dateien des entprechenden Primärformates fungiert.

Jeder Datensatz enthält einen PRONOM-Identifier und den Namen einer der durch DNSCore zur Verfügung gestellten Prozeduren. Dem oben genannte Beispiel liegt der folgende Datensatz zugrunde:

    fmt/353 | de.uzk.hki.da.format.TiffSubformatIdentificationStrategy
    
Die Subformaterkennung ist so konzipiert, dass sie innerhalb eines auf DNSCore basierenden [Gesamtsystem](object_model.de.md#preservationsystem---das-gesamtsystem) in gleicher Weise funkioniert. Datenmodelltechnisch ausgedrückt ist die Zuordnung von Subformaterkennungsprozessen zu PUIDs eine globale Eigenschaft des Gesamtsystems. Dies ist für die rein in Java implementierten Prozeduren unkritisch, da diese als Teil des ContentBroker jar-files ausgeliefert werden und somit automatisch auf jedem Knoten zur Verfügung stehen. Im Normalfall handelt es sich um bei den Prozeduren jedoch um Wrapperklassen, die den Output externer Programme (z.B. ffmpeg) auswerten. 
Die einzelnen Knoten des Gesamtsystems müssen dann dementsprechend bestimmte Voraussetzungen erfüllen, um die gemeinsam angebotenen Funktionalität auch tatsächlich anbieten zu können. Während die Wrapperklassen Teil des ContentBroker sind, müssen die externen Tools gesondert installiert werden, und zwar auf jedem der Knoten des Gesamtsystems.

### Einrichtung auf den Knoten

Um die einheitliche Funktionalität des Gesamtsystems sicherzustellen, stellt DNSCore einen Mechanismus bereit, der automatisch überprüfen kann, ob die entsprechend benötigten externen Format-Identifier (derzeit identify und ffmpeg) auf einem Knoten auch de facto vorliegen. Hierzu stellen die einzelnen Wrapper-Klassen ihrerseits **HealthChecks** bereit. Dabei handelt es sich um gemeinsam mit dem eigentlichen Wrapper-Code entwickeltem Code, der prüft, ob das benötigte externe Identifier-Tool vorliegt, und ob es in einer Version vorliegt, die unterstützt wird. Letzere Maßnahme ist notwendig, da sichergestellt werden muss, dass das Tool seinen Output in einer Format liefert, der von der Wrapper-Klasse entsprechend verarbeitet werden kann.

Der Aufruf dieser Health-Check-Prozeduren findet dann im Rahmen von **diagnostics** (siehe [services](administration-services.de.md)) statt, welches Teil der Startup Prozedur des ContentBroker ist. Wenn ein benötigter Identifier nicht oder nicht in der richtigen Version vorliegt, bricht *diagnostics* den Start des ContentBroker ab.

Da nicht in allen Fällen jede der Subformaterkennungsprozeduren benötigt wird, überprüft *diagnostics* dabei tatsächlich nur diejenigen Prozeduren, die tatsächlich auch global konfiguriert sind. Eine beispielhafte Systemkonfiguration, die lediglich die Erkennung von Tiff-Subformaten vorsieht, andere Subformate jedoch ausser acht lässt, könnte beispielsweise aus dem alleinigen Datensatz wie folgt bestehen:

        fmt/353 | de.uzk.hki.da.format.TiffSubformatIdentificationStrategy

diagnostics würde in dem Fall nur für diese eine Prozedur überprüfen, ob die notwendigen Voraussetzungen zu ihrer Ausführung auf dem jeweiligen Knoten gegeben sind (d.h. ob das entsprechende Tool einsatzbereit ist).


