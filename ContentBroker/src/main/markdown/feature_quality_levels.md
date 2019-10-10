
# Leistungsmerkmal: Qualitätsstufen


Die Qualitätsstufen drücken die Eignung eines SIP-Paketes für Langzeitarchivierbarkeit nach aktuellen Maßstäben aus.
Die Zuordnung zu Qualitätsstufen über einen Zeitraum ist ebenso fließend, wie auch die Einschätzung von „fehlerhaft“ insgesamt. Daraus folgt: Die Zuordnung eines Information Packages oder seiner Bestandteile zu einer Qualitätsstufe kann/muss sich in der Archivierungsdauer durchaus ändern. Deshalb ist es die Intention der Qualitätsstufen, für den **Zeitpunkt des Ingests** festzulegen, welcher Level, welche Qualität der digitalen Langzeitarchivierung mindestens erreicht werden kann. 

Es wird ein fünfstufiges Verfahren vorgeschlagen, wobei die Stufe 0 kein eigentliches Qualitätsmerkmal der Objekte darstellt.


| Kriterium  | Stufe 0  | Stufe 1  | Stufe 2  |Stufe 3   | Stufe 4  | Stufe 5  |
|---|---|---|---|---|---|---|
| DNS konformes SIP.  |   | + | +  | +  |  + | +  |
| SIP ist virenfrei und konform zur SIP-Spezifikation.  |  | +  | +  | +  | +  | +  |
| Fehlerfreie Dateiidentifikation  |   |   |  + | +  | +  | +  |
| Alle Dateien unterstützter Formate im SIP sind nach Aussage eines Validators zu der jeweiligen Formatspezifikation konform. (Validation)  |   |   | +  |   | +  |  + |
| Alle Dateien unterstützter Formate können (falls notwendig) zu LZA-Format migriert werden. (LZA-Migrierbarkeit)  |   |   |   |  + |  + |  + |
| Alle Dateien im AIP sind im Sinne der DNS Spezifikation unterstützte Formate  |   |   |   |   |   | +  |

Die unterstützten Formate befinden sich in der Tabelle https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/operations_format_conversion_current_configuration.de.md  

##### Qualitätsstufe 0

Erfüllt ein eingeliefertes SIP eines oder mehrere Kriterien der Stufe 1 nicht, schließt das eine weitere Verarbeitung im DNS aus. Das System kann nur DNS konforme SIPs verarbeiten.
Die Qualitätsstufe 0 stellt demzufolge keine Qualitätsstufe im eigentlichen Sinne dar, weil alle SIPs, die in diese Stufe eingeordnet werden, vom DNS-System abgelehnt werden müssen. Weder die Langzeitspeicherung des reinen Bitstreams noch eine digitale Langzeitarchivierung kann für SIPs in dieser Stufe umgesetzt werden.

##### Qualitätsstufe 1

Die zur Qualitätsstufe 1 gehörenden Kriterien belegen, dass das eingelieferte SIP und das Containerformat des SIP den Vorgaben des DNS-Systems entspricht.
Dafür wird das SIP innerhalb der DNS-Software gegen die Spezifikationen validiert.
Das SIP wird mithilfe der eingesetzte Virenerkennungssoftware (ClamAV Ver. 0.98) auf Viren geprüft und darf keine Viren enthalten.
Die Kriterien der Stufe 1 müssen auch für alle höheren Stufen erfüllt werden.
Qualitätsstufe 1 ist für die reine Bitstream-Speicherung ausreichend.


##### Qualitätsstufe 2

Die Qualitätsstufe 2 liefert eine Bewertung bzgl. der potentiellen LZA-Fähigkeit aller Dateien. Zunächst müssen alle Dateien dafür fehlerfrei und erfolgreich identifizierbar sein. Bei komplexen Formaten ist die Subformat-Identifikation z.B. Codec ebenfalls essentiell.

Die Identifikation jeder Datei ist notwendig um eine individuelle Validations-/Migrationsroutine zu nutzen. Können z.B. mangels fehlerhafter Identifikation die Validation **und** die Migration für mindestens eine beinhaltende Datei nicht durchgeführt oder geplant werden, so ist die Qualitätsstufe 1 anzunehmen.  

Bei der Qualitätsstufe 2 verläuft die Migration für mindestens eine Datei aus dem SIP nicht erfolgreich. Eine Validierung mittels eines Validators muss aber für die Qualitätsstufe 2 für alle Dateien erfolgreich sein. Da keine LZA-Migration erfolgen kann, kommt für die nicht migrierbaren Dateien nur eine Bitstream-Langzeitspeicherung in Frage

##### Qualitätsstufe 3

Die Qualitätsstufe 3 liefert bzgl. LZA-Fähigkeit der Dateien eine sicherere Zusage als Qualitätsstufe 2, denn unabhängig vom Validationsergebnis können die zu wandelnden Dateien alle erfolgreich in ein LZA-fähiges Format umgewandelt werden.
Die Migrierbarkeit zu einem LZA-Format stellt die Grundlage zu weiterführenden Erhaltungsmaßnahmen dar.
Im Unterschied zur Qualitätsstufe 2 ist diese Stufe ebenfalls erreicht, sollte die Validation einer Datei fehlschlagen. 
Pakete die auf Grund der PREMIS-Einstellungen nicht migriert werden dürfen, können die Qualitätsstufe 3 und dementsprechend die darauffolgenden Qualitätsstufen nicht erreichen.

##### Qualitätsstufe 4

Diese Qualitätsstufe stellt nach dem heutigen Stand der Technik sicher, dass jede Datei mit einem verstandenen Format valide und migrierbar ist.

##### Qualitätsstufe 5

Sichert zu, dass die Kriterien der vorherigen Stufe für alle Dateien im SIP gelten und daher das SIP in seiner Gesamtheit migrierbar ist. Es bedeutet gleichzeitig, dass das SIP nur **Dateien unterstützter Formate** beinhaltet. 

##### 

Die Qualitätsstufe ist über die Restful-Schnittstelle abfragbar und in der DAWeb in der AIP-Ansicht als Spalte definiert. In der DAWeb bietet sich die Möglichkeit die AIP-Tabelle nach eine Qualitätstufe zu selektieren.

