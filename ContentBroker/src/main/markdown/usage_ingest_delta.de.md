# Delta Ingest

Durch den Delta Ingest erhält der Contractor die Möglichkeit, weitere Daten zu einem bereits bestehenden Objekt
hinzuzufügen.

## Vorgehensweise

### Vorraussetzungen 

* Es gelten die im [Ingest](usage_ingest.de.md) beschriebenen Vorraussetzungen.
* Der Contractor hat bereits ein Objekt durch Ersteinlieferung eines SIP angelegt, welches nun ergänzt werden soll.
* Der Contractor hat den Originalnamen des infragekommenden Objektes notiert.

### Schritte

1. Der Contractor legt das SIP mit Hilfe des FileShare Client im Ordner "incoming" auf seinem Nutzungsbereich ab, und zwar mit dem dem Objekt zugeordneten Originalnamen. Die Containerendung (.tar,.tgz,.zip) bleibt dabei unberücksichtigt.
2. Die weiteren Schritte entsprechen den in [Ingest->Schritte](usage_ingest.de.md#schritte) beschriebenen.





