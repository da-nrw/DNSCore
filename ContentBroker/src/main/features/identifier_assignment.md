# Leistungsmerkmal: Vergabe von Identifiern
In DNSCore gibt es verschiedene Objektbezogene Identifier. 

Der technische Identifier, der systemweit eindeutig ist und automatisch vom System vergeben wird.
Der Originalname des Paketes, der vom Vertragspartner vergeben wird. Dieser ist für den jeweiligen Vertragspartner eindeutig und macht ermöglicht eine Zuordnung von Deltas zu einem Objekt.
Die URN. Diese wird entweder vom System automatisch generiert oder wird vom Vertragspartner vergeben.
Die URN wird in jedem Fall nur einmal vergeben. Im Falle von Deltas wird die URN nicht abgeändert.

## Kontext: 

Dokumentation: SIPSpezifikation / URN-Vergabe

# Hintergrund: 

## Vorbedingungen:

Der Nutzer ist unter der Rolle "Contractor" angemeldet/eingeloggt in der "DAWeb"
Durchführung:
Das Paket wird in den Vertragspartner-Eingangsordner abgelegt.
Die Paketverarbeitung wird gestartet über die Maske "Verarbeitung für abgelieferte SIP starten".
Warten auf die Email mit dem Einlieferungsbeleg.

## Szenario AT-IV-1 Automatische Vergabe der URN
Kontext
ATIdentifierAssignment.urnBasedOnTechnicalIdentifier()
Testpaket(e):
(GitHub) ATUseCaseIngest1.tgz
data/premis.xml
data/   (Primärdaten)
Vorbedingungen:
siehe Hintergrund.
Durchführung:
siehe Hintergrund.
In der Maske "Eingelieferte Objekte" das Objekt per technischem Identifier recherchieren.
Akkzeptanzkriterien:
Die Ziffernfolge des technischen Identifier ist vollständig in der URN enthalten. D.h. die URN urn:nbn:de:xyz-1-2013100836773 muss für den Identfier 1-2013100836773 gebildet worden sein.
