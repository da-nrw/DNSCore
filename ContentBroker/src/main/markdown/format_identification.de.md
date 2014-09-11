# Formaterkennung mit DNSCore

Die Formaterkennung der DNSCore gliedert sich in zwei Stufen. Die erste Stufe orientiert sich an dem PRONOM (http://apps.nationalarchives.gov.uk/PRONOM/Default.aspx) Standard der National Archives. Die Erkennung der PUIDs (PRONOM Unique Identifier) geschieht dabei mithilfe des Programmes FIDO (https://github.com/openplanets/fido). Die sekundäre Formaterkennung kann mithilfe einer Plugin-Systematik Containerformate oder Kompressionsalgorhythmen erkennen, in abhängigkeit von der PUID.

Auf der Erkennung der PUIDs basiert die Migrationskomponente von DNSCore. Für jede Datei, die im DNSCore eingespielt wird, wird eine Abgleich der erkannten PUID mit den zur Verfügung stehenden ConversionPolicies vorgenommen. Gibt es einen oder mehrere Treffer, so werden die entsprechenden Aufträge erstellt und das System führt eine Formatmigration für die Datei durch.

## Sekundäre Formaterkennung

Zunächst ein Beispiel. Eine Tiff Datei enthält einen lzw-komprimierten Datenstrom. FIDO erkennt die Datei als vom Typ fmt/353. Das System führt daraufhin eine auf Tiff abgestimmte Überprüfung des Kompressionsformates durch und stellt lzw fest. Somit haben wir die Formatkombination [fmt/353, lzw] festgestellt. Um dieses Verhalten des Systems einzurichten, müssen einige Konfigurationen vorgenommen werden.

### SubformatIdentificationPolicy

Jede SubFormatIdentificationPolicy hat drei Felder.

    puid: PRONOM Unique Identifier
    allowed_values: Komma separierte Liste der akzeptierten Werte
    subformat_identification_routine: Link zu den Routines
   
**puid** Nachdem ein File seine PUID erhalten hat, wird ein Abgleich gegen die SubFormatIdentificationPolicy vorgenommen. Stimmt das Format mit einer Policy überein, wird die entsprechend verlinkte Routine ausgeführt.
**allowed_values** abc
**subformat_identification_policy_id** abc
   
   
### SubformatIdentificationRoutine

    String scriptName: (eventuell mit script: prefix)
    String healthCheckFile: relativer Pfad von CBHome zu einem Testfile
    String healthCheckExpectedOutcome: Erwarteter Output, wenn das script die healthCheckFile scannt.
    
## Standardkonfigurationen, mitgelieferte und empfohlene Konfigurationen.   
   




