# Leistungsmerkmal: Ingest von SIP als entpackter Ordner


### Hintergrund:

Im Rahmen einer Benutzung der DNS kann es gewünscht sein, dass auch ungepackte Ordner als SIP an die Ingestschnittstelle übergeben werden. Dadurch entfällt die Bildung eines gepackten SIP. Diese Übergabe ist nur für automatisierte Übergaben aus Fachverfahren sinnvoll. Diese Übergabe ist 
in Form eines "Move" auf dem gleichen Filesystem zu bewerkstelligen, wie es bei der normalen Übergabe von gepackten SIP auch der Fall ist.  

#### Kontext:

* [ATIngestUnpackedSIP.java](../../test/java/de/uzk/hki/da/at/ATIngestUnpackedSIP.java)
* [SIP-Spezifikation](specification_sip.de.md)

#### Testordner:

* [ATIngestUnpackedSIP](https://cdn.rawgit.com/da-nrw/DNSCore/master/ContentBroker/src/test/resources/at/ATIngestUnpackedSIP

#### Vorbedingungen:

* Der Nutzer ist in der DAWeb in der Rolle "Vertragspartner" eingeloggt.
* Der Nutzer ist mit einem FileShare Client mit dem entsprechenden für ihn reservierten Nutzungsbereich auf dem Verarbeitungsknoten verbunden.
* Der Nuter hat Zugriff auf die Ausführung von "Move" - Operationen auf Filesystemebene.

#### Durchführung:

1. Das erste Paket wird in den User-Eingangsordner unter einem *für das Szenario* *jeweils eindeutigen Namen* abgelegt.
1. Die Paketverarbeitung wird gestartet über die Maske "Verarbeitung für abgelieferte SIP starten".
1. Warten auf die Email mit dem Einlieferungsbeleg.

#### Akzeptanzkriterien:

1. Das Objet wird ordnungsgemäß verabreitet und taucht unter "eingelieferte Objekte" auf. 

