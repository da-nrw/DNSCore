# Formaterkennung mit DNSCore

TODO: Hintergrund der Formaterkennung und der Subformaterkennung erläutern.

Die Formaterkennung der DNSCore gliedert sich in zwei Stufen. Die erste Stufe orientiert sich an dem PRONOM (http://apps.nationalarchives.gov.uk/PRONOM/Default.aspx) Standard der National Archives. Die Erkennung der PUIDs (PRONOM Unique Identifier) geschieht dabei mithilfe des Programmes FIDO (https://github.com/openplanets/fido). Die sekundäre Formaterkennung kann mithilfe einer Plugin-Systematik Containerformate oder Kompressionsalgorhythmen erkennen, in abhängigkeit von der PUID.

Auf der Erkennung der PUIDs basiert die Migrationskomponente von DNSCore. Für jede Datei, die im DNSCore eingespielt wird, wird eine Abgleich der erkannten PUID mit den zur Verfügung stehenden ConversionPolicies vorgenommen. Gibt es einen oder mehrere Treffer, so werden die entsprechenden Aufträge erstellt und das System führt eine Formatmigration für die Datei durch.

## Subformaterkennung

Aufbauend auf der primären Formaterkennung kann DNSCore eine erweiterte Formaterkennung durchführen. Der PreservationSystem-Administrator kann an für erkannte Formate festlegen, ob, und wenn ja, mithilfe welcher Prozeduren,
die Subformaterkennung durchzuführen ist. Ergebnis dieser Subforamterkennung ist, dass File Format einer Datei um eine zustätzliche Information, den Subformat-Identifier angereichert wird. Die Identifier sind derzeit nicht standartisiert und hängen teilweise vom Output externer Programme ab.

Beispiel:

    Datei: abc.tif 
    -> Erkanntes Format: fmt/353
    Subformaterkennungsprozedur: TiffSubformatIdentificationStrategy
    -> Erkanntes Subformat: lzw (lzw-codec)

Das Beispiel geht davon aus, dass die Prozedur "TiffSubformatIdentificationStrategy" mit dem Format Identifier "fmt/353" verknüft ist. Die als fmt/353 erkannte Beispieldatei wird daraufhin durchläuft daraufhin die festgelegte Subformaterkennungsprozedur. Diese liefert "lzw" als Bezeichner für ein Kompressionsverfahren den im Tiff enthalteten Datenstrom zurück.

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

Die Subformaterkennung ist so konzipiert, dass sie innerhalb eines auf DNSCore basierenden Gesamtsystem in gleicher Weise funkioniert. Datenmodelltechnisch ausgedrückt ist die Zuordnung von Subformaterkennungsprozessen zu PUIDs eine globale Eigenschaft des Gesamtsystems. 

Dies ist für die rein in Java implementierten Prozeduren unkritisch, da diese als Teil des jar files des ContentBroker ausgeliefert werden. Im Normalfall handelt es sich um bei den Prozeduren jedoch um Wrapperklassen, die den Output externer Programme (z.B. ffmpeg) auswerten. 

Um sicherzustellen, dass alle Knoten in gleicher Weise die benötigten externen Programme zur Formatidentifizierung vorliegen haben, stellt diagnostics (TODO link) vor Start des ContentBrokers fest, ob alle



