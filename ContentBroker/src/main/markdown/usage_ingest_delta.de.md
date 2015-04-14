# Delta Ingest

Durch den Delta Ingest erhält der Contractor die Möglichkeit, weitere Daten zu einem bereits bestehenden Objekt
hinzuzufügen.

## Vorgehensweise

### Vorraussetzungen 

* Es gelten die im [Ingest](usage_ingest.de.md#vorraussetzungen) beschriebenen Vorraussetzungen.
* Der Contractor hat bereits ein Objekt durch Ersteinlieferung eines SIP angelegt, welches nun ergänzt werden soll.
* Der Contractor hat den Originalnamen des infragekommenden Objektes notiert.

### Schritte

1. Der Contractor legt das SIP mit Hilfe des FileShare Client im Ordner "incoming" auf seinem Nutzungsbereich ab, und zwar mit dem dem Objekt zugeordneten Originalnamen. Die Containerendung (.tar,.tgz,.zip) bleibt dabei unberücksichtigt.
2. Die weiteren Schritte entsprechen den in [Ingest->Schritte](usage_ingest.de.md#schritte) beschriebenen.

### Der Email-Report

#### Erfolgreiche Einlieferung

Der Email-Report für eine erfolgreiche Einlieferung für Deltas sieht folgendermaßen aus:

```
Betreff: [System] Einlieferungsbeleg für Ihr Delta zum Objekt 2-20150409419765

Inhalt:

Ihrem archivierten Objekt mit dem Identifier 2-20150409419765 und der URN urn:nbn:de:system-2-20150409419765 wurde erfolgreich ein Delta mit dem Paketnamen "ATUseCaseIngestDelta_2_2015-04-08" hinzugefügt.
```

Wie auch im Einlieferungsbeleg zur erfolgreichen Einlieferung finden sich die Informationen zu URN, technischem Identifier
sowie Originalnamen wieder. Aus Betreff- und Inhaltstext geht zudem hervor, dass das SIP als zusätzliches AIP einem bestehenden Objekt hinzugefügt wird.




