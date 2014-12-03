# Formaterkennung mit DNSCore

TODO: Hintergrund der Formaterkennung und der Subformaterkennung erläutern.

Die Formaterkennung der DNSCore gliedert sich in zwei Stufen. Die erste Stufe orientiert sich an dem [PRONOM](http://apps.nationalarchives.gov.uk/PRONOM/Default.aspx) Standard der National Archives. Die Erkennung der PUIDs (PRONOM Unique Identifier) geschieht dabei mithilfe des Programmes [FIDO](https://github.com/openplanets/fido). Die sekundäre Formaterkennung kann mithilfe einer Plugin-Systematik Containerformate oder Kompressionsalgorhythmen erkennen, in abhängigkeit von der PUID.

Auf der Erkennung der PUIDs basiert die Migrationskomponente von DNSCore. Für jede Datei, die im DNSCore eingespielt wird, wird eine Abgleich der erkannten PUID mit den zur Verfügung stehenden ConversionPolicies vorgenommen. Gibt es einen oder mehrere Treffer, so werden die entsprechenden Aufträge erstellt und das System führt eine Formatmigration für die Datei durch.

## Subformaterkennung

Aufbauend auf der primären Formaterkennung kann DNSCore eine erweiterte Formaterkennung durchführen. Der PreservationSystem-Administrator kann an für erkannte Formate festlegen, ob, und wenn ja, mithilfe welcher Prozeduren,
die Subformaterkennung durchzuführen ist. Ergebnis dieser Subforamterkennung ist, dass File Format einer Datei um eine zustätzliche Information, den Subformat-Identifier angereichert wird. Die Identifier sind derzeit nicht standartisiert und hängen teilweise vom Output externer Programme ab.

Beispiel:

    Datei: abc.tif 
    -> Erkanntes Format: fmt/353
    Subformaterkennungsprozedur: TiffSubformatIdentificationStrategy
    -> Erkanntes Subformat: lzw (lzw-codec)

Das Beispiel geht davon aus, dass die Prozedur "TiffSubformatIdentificationStrategy" mit dem Format Identifier [fmt/353](http://apps.nationalarchives.gov.uk/PRONOM/Format/proFormatSearch.aspx?status=detailReport&id=1099) verknüft ist. Die als fmt/353 erkannte Beispieldatei wird daraufhin durchläuft daraufhin die festgelegte Subformaterkennungsprozedur. Diese liefert "lzw" als Bezeichner für ein Kompressionsverfahren den im Tiff enthalteten Datenstrom zurück.

Für die Subformaterkennung stehen sowohl DNS-eigene Prozesse als auch  Wrapper-Klassen zur Verfügung, die die Dienste externer Programme wie z.B. ffmpeg für DNSCore nutzbar machen. Derzeit verfügbare Prozesse sind:

* [ImageMagickIdentificationStrategy](../java/de/uzk/hki/da/format/ImageMagickIdentifySubformatIdentificationStrategy.java)
* [XMLSubformatIdentificationStrategy](../java/de/uzk/hki/da/format/XMLSubformatIdentificationStrategy.java)
* [FFmpegSubformatIdentificationStrategy](../java/de/uzk/hki/da/format/FFmpegSubformatIdentificationStrategy.java)
* TODO: TiffSubformatIdentificationStrategy

## Einrichten der Subformaterkennung

### Zentrale Einrichtung 

Das Einrichten der Subformaterkennung ist Aufgabe des PreservationSystem-Admin (TODO Link). Ihm kommt die Aufgabe zu, festzulegen, ob, und ja, mit welcher Prozedur Dateien der verschiedenen Primärformate auf Subformate geprüft werden. Diese Zuordnung wird in der Object-DB in der Tabelle "subformat_identification_strategy_puid_mappings" festgehalten. Diese Tabelle ist sehr einfach gehalten:

    format_puid                            | character varying(255) | 
    subformat_identification_strategy_name | character varying(255) | <- fully qualified Java Name

**format_puid** Der PRONOM Uniqe Identifier.

Jeder Datensatz enthält einen PRONOM-Identifier und den Namen einer der durch DNSCore zur Verfügung gestellten Prozeduren. Dem oben genannte Beispiel liegt der folgende Datensatz zugrunde:

    fmt/353 | de.uzk.hki.da.format.TiffSubformatIdentificationStrategy
    
### Einrichtung auf den Knoten

Die Subformaterkennung ist so konzipiert, dass sie innerhalb eines auf DNSCore basierenden Gesamtsystem in gleicher Weise funkioniert. Datenmodelltechnisch ausgedrückt ist die Zuordnung von Subformaterkennungsprozessen zu PUIDs eine globale Eigenschaft des Gesamtsystems. Dies ist für die rein in Java implementierten Prozeduren unkritisch, da diese als Teil des ContentBroker jar-files ausgeliefert werden und somit automatisch auf jedem Knoten zur Verfügung stehen. Im Normalfall handelt es sich um bei den Prozeduren jedoch um Wrapperklassen, die den Output externer Programme (z.B. ffmpeg) auswerten. 
Die einzelnen Knoten des Gesamtsystems müssen dann dementsprechend bestimmte Voraussetzungen erfüllen, um die gemeinsam angebotenen Funktionalität auch tatsächlich anbieten zu können. Während die Wrapperklassen Teil des ContentBroker sind, müssen die externen Tools gesondert installiert werden.

Um die einheitliche Funktionalität des Gesamtsystems sicherzustellen, stellt DNSCore daher einen Mechanismus bereit, der automatisch überprüfen kann, ob die entsprechend benötigten externen Format-Identifier auch auf einem Knoten de facto vorliegen. Hierzu stellen die einzelnen Wrapper-Klassen ihrerseits **HealthChecks** bereit. Dabei handelt es sich um gemeinsam mit dem eigentlichen Wrapper-Code entwickeltem Code, der prüft, ob das benötigte externe Identifier-Tool vorliegt, und ob es in einer Version vorliegt, die unterstützt wird. Letzere Maßnahme ist notwendig, da sichergestellt werden muss, dass das Tool seinen Output in einer Format liefert, der von der Wrapper-Klasse entsprechend verarbeitet werden kann.

Der Aufruf dieser Health-Check-Prozeduren findet dann im Rahmen von **diagnostics** statt, welches Teil der Startup Prozedur des ContentBroker ist. Wenn ein benötigter Identifier nicht oder nicht in der richtigen Version vorliegt, bricht **diagnostics** den Start des ContentBroker ab.

Da nicht in allen Fällen jede der Subformaterkennungsprozeduren benötigt wird, überprüft diagnostics dabei tatsächlich nur diejenigen Prozeduren, die tatsächlich auch global konfiguriert sind. Eine beispielhafte Systemkonfiguration, die lediglich die Erkennung von Tiff-Subformaten vorsieht, andere Subformate jedoch ausser acht lässt, könnte beispielsweise aus dem alleinigen Datensatz wie folgt bestehen:

        fmt/353 | de.uzk.hki.da.format.TiffSubformatIdentificationStrategy

diagnostics würde in dem Fall nur für diese eine Prozedur überprüfen, ob die notwendigen Voraussetzungen zu ihrer Ausführung auf dem jeweiligen Knoten gegeben sind (d.h. ob das entsprechende Tool einsatzbereit ist).
