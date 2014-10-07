# Formaterkennung mit DNSCore

Die Formaterkennung der DNSCore gliedert sich in zwei Stufen. Die erste Stufe orientiert sich an dem PRONOM (http://apps.nationalarchives.gov.uk/PRONOM/Default.aspx) Standard der National Archives. Die Erkennung der PUIDs (PRONOM Unique Identifier) geschieht dabei mithilfe des Programmes FIDO (https://github.com/openplanets/fido). Die sekundäre Formaterkennung kann mithilfe einer Plugin-Systematik Containerformate oder Kompressionsalgorhythmen erkennen, in abhängigkeit von der PUID.

Auf der Erkennung der PUIDs basiert die Migrationskomponente von DNSCore. Für jede Datei, die im DNSCore eingespielt wird, wird eine Abgleich der erkannten PUID mit den zur Verfügung stehenden ConversionPolicies vorgenommen. Gibt es einen oder mehrere Treffer, so werden die entsprechenden Aufträge erstellt und das System führt eine Formatmigration für die Datei durch.

## Sekundäre Formaterkennung

Zunächst ein Beispiel. Eine Tiff Datei enthält einen lzw-komprimierten Datenstrom. FIDO erkennt die Datei als vom Typ fmt/353. Das System führt daraufhin eine auf Tiff abgestimmte Überprüfung des Kompressionsformates durch und stellt lzw fest. Somit haben wir die Formatkombination [fmt/353, lzw] festgestellt. Um dieses Verhalten des Systems einzurichten, müssen einige Konfigurationen vorgenommen werden.

### SubformatIdentificationPolicy

Jede SubFormatIdentificationPolicy hat drei Felder. Die entsprechende Datenbanktabelle heisst subformat_identification_policies.

    puid: varchar / String (java)
    allowed_values: varchar / String (java)
    subformat_identification_routine: int / int (java)
   
**puid** Der PRONOM Uniqe Identifier. Nachdem ein File seine PUID erhalten hat, wird ein Abgleich gegen die SubFormatIdentificationPolicy vorgenommen. Stimmt das Format mit einer Policy überein, wird die entsprechend verlinkte Routine ausgeführt.

**allowed_values** Komma separierte Liste der akzeptierten Werte. Wenn die entsprechend referenzierte Routine auf eine Datei ausgeführt wird, darf sie exakt einen der erlaubten Werte zurückliefern. In jedem anderen Fall wird die Verarbeitung mit einem technischen Fehler beendet.

**subformat_identification_routine_id** Primärschlüsselverknüpfung zur entprechenden Routine, welche ausgeführt werden soll, wenn die Policy getriggert wird.
   
   
### SubformatIdentificationRoutine

Eine Routine kapselt im wesentlich zwei Informationen. Einmal der name eines Skriptes oder einer Java-Klasse, die die Identifikation auf ein File durchführen kann. Zum anderen Informationen, um auf allen Knoten eines PreservationSystems einen HealthCheck durchführen zu können. Policies und Routinen sind Eigenschaften des Gesamtsystems. Daher muss es einen Weg geben, automatisiert festzustellen, ob jeder Knoten des Systems die entsprechenden Skripte zur Verfügung hat und ob diese grundsätzlich Einsatzfähig sind. Der HealthCheck wird typischerweise vom diagnostics-Tool angetriggert.

    testMethod: varchar / String (java)
    healthCheckFile: varchar / String (java)
    healthCheckExpectedOutcome: varchar / String (java)
    
**testMethod** Entweder der Pfad zu einem Skript oder der vollqualifizierte Name einer Java-Klasse.

* Als Skript: "script:{relativer Pfad zum Skript von CBHome}", z.B. "script:bash/ffmpeg.sh"
* Als Klasse: "fqjavaname", z.B. "de.uzk.hki.da.ff.TiffCompressionIdentifier"

(eventuell mit script: prefix)

**healthCheckFile** Relativer Pfad von CBHome zu einem Testfile, welches durch den sich hinter testMethod aufgerufenen Code überprüft wird.

**healthCheckExpectedOutcome** Erwarteter Output, wenn das script die healthCheckFile scannt. HealthCheck ist FAILED, wenn der Output nicht übereinstimmt oder es einen anderen Fehler gibt (z.B. testMethod nicht vorhanden):
    
## Sekundäre Formaterkennung: Standardkonfigurationen
   
Zum aktuellen Zeitpunkt der Entwicklung ...
TODO: tiff
TODO: diagnostics




