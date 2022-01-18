# Leistungsmerkmal: Ingest von SIP als entpackter Ordner ohne Bagit


### Hintergrund:

Im Rahmen einer Benutzung der DNS kann es gewünscht sein, dass auch ungepackte Ordner als SIP an die Ingestschnittstelle übergeben werden, welche kein Bagit enthalten. Dadurch entfällt die Bildung eines gepackten SIP und die Bildung des Bagit. Diese Übergabe ist nur für automatisierte Übergaben aus Fachverfahren sinnvoll. Diese Übergabe ist 
in Form eines "Move" auf dem gleichen Filesystem zu bewerkstelligen, wie es bei der normalen Übergabe von gepackten SIP auch der Fall ist.  

#### Kontext:

* [ATIngestUnpackedSIPNoBagit.java](../../test/java/de/uzk/hki/da/at/ATIngestUnpackedSIPNoBagit.java)
* [SIP-Spezifikation](specification_sip.de.md)

#### Testordner:

* [ATIngestUnpackedSIPNoBagit](../../test/resources/at/ATIngestUnpackedSIPNoBagit)

#### Vorbedingungen:

* Dieses Feature kann momentan nur durch testweise deaktivierung der Lizenzüberprüfung getestet werden.
* Der Nutzer ist mit einem FileShare Client mit dem entsprechenden für ihn reservierten Nutzungsbereich auf dem Verarbeitungsknoten verbunden.
* Der Nutzer hat Zugriff auf die Ausführung von "Move" - Operationen auf Filesystemebene.

#### Durchführung:

1. Das Paket wird in den Ingest-Ordner/noBagit unter einem *für das Szenario* *jeweils eindeutigen Namen* per move abgelegt.
1. Warten auf die Email mit dem Einlieferungsbeleg.

#### Akzeptanzkriterien:

1. Das Objekt wird ordnungsgemäß verabreitet und taucht unter "eingelieferte Objekte" auf. 

