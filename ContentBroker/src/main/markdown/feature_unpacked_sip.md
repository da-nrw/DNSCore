# Leistungsmerkmal: Spezieller Ingest starten mittels Bagit - Ordner


#### Kontext:

* [Ingest](feature_ingest_retrieval.md)
* Der Use-Case dieses Features ist sehr speziell. Im Normalbetrieb kann  der Nutzer dieses Verfahren kaum bedienen, da es für spezielle 
automatisierte Einlieferungen von SIP gedacht ist, die nicht als unkomprimiertes .tar oder komprimiertes .tgz, .zip angeliefert werden, sondern direkt als 
entpacktes Verzeichnis. Es gelten ansonsten die gleichen Anforderungen wie für alle SIP
* [vgl: SIP Spezifikationen](specification_sip.de.md)
* [ATIngestUnpackedSIP.java](../../test/java/de/uzk/hki/da/at/ATIngestUnpackedSIP.java)

## Hintergrund:

Gilt für alle Szenarien!

#### Testpaket(e):

* ../../src/test/resources/at/ATIngestUnpackedSIP (Ordner!)

#### Vorbedingungen:

* Der User kann die Verarbeitung nicht manuell starten,
sondern nur über die Moveoperation an die interne Ingestschnittstelle des Ordners. 

#### Durchführung:

* mv src/test/resources/at/ATIngestUnpackedSIP /<ingestSchnittstelle>/<csn>/incoming/

#### Akzeptanzkriterien:

Das Objekt wird wie gewohnt verabreitet und taucht in "eingelieferte Objekte" auf.