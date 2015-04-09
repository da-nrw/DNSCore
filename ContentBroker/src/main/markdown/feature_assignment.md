# Leistungsmerkmal: Vergabe von Identifiern

In DNSCore gibt es verschiedene Objektbezogene Identifier.

# Der technische Identifier, der systemweit eindeutig ist und automatisch vom System vergeben wird.{color}
# Der Originalname des Paketes, der vom Vertragspartner vergeben wird. Dieser ist für den jeweiligen Vertragspartner eindeutig und macht ermöglicht eine Zuordnung von Deltas zu einem Objekt.{color}
# Die URN. Diese wird entweder vom System automatisch generiert oder wird vom Vertragspartner vergeben.


{color:#000000}Die URN wird in jedem Fall nur einmal vergeben. Im Falle von Deltas wird die URN nicht abgeändert.{color}


#### {color:#000000}﻿Kontext:&nbsp;{color}

* Dokumentation: SIPSpezifikation /&nbsp;[URN-Vergabe|https://github.com/da-nrw/DNSCore/blob/master/ContentBroker/src/main/markdown/specification_sip.de.md#urn-vergabe]


## Hintergrund:&nbsp;


#### Vorbedingungen:

* Der Nutzer ist unter der Rolle "Contractor" angemeldet/eingeloggt in der "DAWeb"

#### Durchführung:

# Das Paket wird in den Vertragspartner-Eingangsordner abgelegt.
# Die Paketverarbeitung wird gestartet über die Maske "Verarbeitung für abgelieferte SIP starten".
# Warten auf die Email mit dem Einlieferungsbeleg.



## Szenario AT-IV-1 Automatische Vergabe der URN


#### Kontext

* [ATIdentifierAssignment|https://github.com/da-nrw/DNSCore/blob/b92d795dca2100d42eddcd5a5b58f084fb133040/ContentBroker/src/test/java/de/uzk/hki/da/at/ATIdentifierAssignment.java].urnBasedOnTechnicalIdentifier()

#### Testpaket(e):

* (GitHub) ATUseCaseIngest1.tgz
** data/premis.xml
** data/ &nbsp; (Primärdaten)

#### Vorbedingungen:

* siehe Hintergrund.

#### Durchführung:

# siehe Hintergrund.
# In der Maske "Eingelieferte Objekte" das Objekt per technischem Identifier recherchieren.

#### Akkzeptanzkriterien:

* Die Ziffernfolge des technischen Identifier ist vollständig in der URN enthalten. D.h. die URN *urn:nbn:de:xyz-1-2013100836773* muss für den Identfier&nbsp;*1-2013100836773* gebildet worden sein.

## Szenario AT-IV-2 Nutzergesteuerte URN-Vergabe

#### Kontext:

* [ATIdentifierAssignment|https://github.com/da-nrw/DNSCore/blob/b92d795dca2100d42eddcd5a5b58f084fb133040/ContentBroker/src/test/java/de/uzk/hki/da/at/ATIdentifierAssignment.java].{color:#795da3}urnByUserAssignment{color}()

#### Testpaket(e):


*  (GitHub) ATReadURNFromSIP.tgz
**    data/premis.xml
**    data/(Weitere Primärdaten)


Inhalt premis.xml

```xml
  <object xsi:type="representation">
  <objectIdentifier>
      <objectIdentifierType>URN</objectIdentifierType>
      <objectIdentifierValue>urn:nbn:de:xyz-1-20131008367735</objectIdentifierValue>
  </objectIdentifier>
  </object>
```

#### Vorbedingungen:

* siehe Hintergrund.

#### Durchführung:

# Siehe Hintergrund.
# In der Maske "Eingelieferte Objekte" das Objekt per technischem Identifier recherchieren.

#### Akzeptanzkriterien:

* In der Maske "Eingelieferte Objekte" wird das Objekt mit der URN&nbsp;*urn:nbn:de:xyz-1-20131008367735* gelistet.
* Der Einlieferungsbeleg enthält den Hinweis, dass dem Paket die URN&nbsp;*urn:nbn:de:xyz-1-20131008367735* zugewiesen wurde.

## Szenario AT-IV-6 URN-Vergabe bei Deltas

Das Szenario beschreibt den Fall, in dem eine Delta abgeliefert wird, in dem der Nutzer eine URN vergibt. Diese vergebene URN stimmt jedoch nicht mit der URN des Objektes überein, welche in der Erstanlieferung auf Basis des technischen Identifier vergeben wurde. Die URN kann nur einmal vergeben werden.

* Derzeitige Implementation: Die neue URN wird ignoriert.
* Alternativer Vorschlag: Ablehnung des Paketes (kann über Änderungsantrag beantragt werden).


#### Kontext:

* [ATIdentifierAssignment|https://github.com/da-nrw/DNSCore/blob/b92d795dca2100d42eddcd5a5b58f084fb133040/ContentBroker/src/test/java/de/uzk/hki/da/at/ATIdentifierAssignment.java].{color:#795da3}keepURNOnDeltaIngest{color}()

#### Testpaket(e):

*  (GitHub) Testpaket 1: ATUseCaseIngest1.tgz
**    data/premis.xml
**    data/   (Primärdaten)

* (GitHub) Testpaket 2: ATReadURNFromSIP.tgz
**    data/premis.xml
**    data/(Weitere Primärdaten)

Inhalt premis.xml des 2. Paketes

```
    <object xsi:type="representation">
    <objectIdentifier>
        <objectIdentifierType>URN</objectIdentifierType>
        <objectIdentifierValue>urn:nbn:de:xyz-1-20131008367735</objectIdentifierValue>
    </objectIdentifier>
    </object>
```

#### Vorbedingungen:

* siehe Hintergrund.

#### Durchführung:

# Das erste Paket wird eingeliefert unter einem frei zu wählenden Originalnamen abgelegt. Warten auf Bestätigungsmail (Die Bestätigungsmail enthält eine URN die, auf dem technische Identifier basiert und nicht die urn:nbn:de:xyz-1-20131008367735).
# Das zweite Paket wird eingeliefert, unter demselben Originalnamen wie das erste Paket.&nbsp;
# Warten auf Mail.
# Mailinhalt prüfen.

#### Akzeptanzkriterien:

* Die URN in der zweiten Mail ist mit der URN aus der ersten Mail identisch. Sie ist nicht urn:nbn:de:xyz-1-20131008367735.
* Alternativer Vorschlag: Der Nutzer wird in einer Fehlermeldung darauf hingewiesen, dass die im Paket übergebene URN nicht mit der ursprünglichen Paket URN übereinstimmt. Der weitere Ingest des Delta wird abgelehnt.





## {color:#ff0000}Szenario&nbsp;AT-IV-3{color} {color:#000000}Nutzergesteuerte URN-Vergabe per METS - Datei{color}

{color:#ff0000}Dieses Szenario ist nicht implementiert.&nbsp;{color}

{color:#000000}Wir haben derzeit zwei Beispiele von Metadaten vorliegen bzw. ausgewählt, die sich lediglich minimal unterscheiden. In beiden Fällen ist innerhalb des Mets das oberste Objekt im METS-Baum durch eine dmdSec mit der entsprechenden ID beschreiben. Innerhalb dieser dmdSec findet man über mets:mdWrap\-{color}{color:#000000}{-}mets:xmlData{-}{color}{color:#000000}\-mods:identifier type=urn die entsprechende URN. Es wird diejenige dmdSec berücksichtitgt, welche dem obersten hierarchischen Element (siehe structMap) der METS-Datei entspricht.{color}

#### Testpaket(e):

{noformat:nopanel=true}
  (GitHub) Testpaket enhält
    data/export_mets.xml
    data/premis.xml
    data/(Weitere Primärdaten)
{noformat}

{color:#000000}{*}Inhalt export_mets.xml{*}{color}

{color:#000000}Beispieldatensatz 1: Die im Februar 2015 (Mail&nbsp;WG: DA NRW / hier: Testszenario für Digitalisate aus dem LAV) vorgeschlagene Unterbringung der METS lautet wie folgt:{color}

{noformat:nopanel=true}
<?xml version="1.0" encoding="UTF-8"?>
<mets:mets xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <mets:dmdSec ID="dmd35716">
        <mets:mdWrap MDTYPE="MODS">
        <mets:xmlData>
          <mods:mods>
            <mods:identifier type="urn">urn:nbn:de:hbz:xy</mods:identifier>
          </mods:mods>
        </mets:xmlData>
        </mets:mdWrap>
    </mets:dmdSec>
{noformat}

{color:#000000}Beispieldatensatz 2: Älterer Datensatz aus Zeiten vor der Projektübernahme LVR-Infokom{color}


{noformat:nopanel=true}
    <mets:dmdSec ID="md775911">
        <mets:mdWrap MIMETYPE="text/xml" MDTYPE="MODS">
            <mets:xmlData>
                <mods xmlns="http://www.loc.gov/mods/v3" version="3.4"
                xsi:schemaLocation="http://www.loc.gov/mods/v3 http://www.loc.gov/standards/mods/v3/mods-3-4.xsd">
                    <identifier type="urn">urn:nbn:de:hbz:xyz</identifier>
{noformat}



#### Vorbedingungen:

* siehe Hintergrund.

#### Durchführung:

# Siehe Hintergrund.
# In der Maske "Eingelieferte Objekte" das Objekt per technischem Identifier recherchieren.

#### Akzeptanzkriterien:

* In der Maske "Eingelieferte Objekte" wird das Objekt mit der URN {color:#000000}{*}urn:nbn:de:hbz:xyz{*}{color}&nbsp;gelistet.
* Der Einlieferungsbeleg enthält den Hinweis, dass dem Paket die URN{color:#ff0000}&nbsp;{color}{color:#000000}{*}urn:nbn:de:hbz:xyz{*}{color}{color:#ff0000}&nbsp;{color}zugewiesen wurde.

#### {color:#ff0000}offene Punkte{color}:

* {color:#ff0000}Zu beschließen: Ist die URN-Unterbringung im METS-XML-Baum von allen Beteiligten des AK-F so akzeptiert?{color}






## Szenario AT-IV-5 Nutzergesteuerte URN-Vergabe in der METS-Datei: Mehrere Objekte auf oberster Ebene

Dieses Szenario ist nicht implementiert.

METS lässt unterschiedliche Arten der Strukturierung von Objekten zu. Die StructMap bildet diese Strukturierung ab. Für die URN-Generierung sind alle Fälle problematisch, in denen es kein einzelnes Objekt auf oberster Hierarchieebene gibt. Ein Objekt mit Kindern ist kein Problem, mehrere Objekte auf der obersten Ebene sind ein Problem.

* Vorschlag 1 : Eventuell mitgelieferte URN werden in jedem Fall, sobald kein eindeutiges Elternobjekt auszumachen ist, ignoriert.
* Vorschlag 2 : In dem Fall, dass es mehrere Objekte auf der höchsten Ebene gibt, und mindestens eines davon eine URN trägt, informiert das System der User per Fehlerreport und bricht den Ingestvorgang ab.

#### Testpaket(e):

  (GitHub) Testpaket enhält
    data/export_mets.xml
    data/premis.xml
    data/(Weitere Primärdaten)


Inhalt export_mets.xml


<mets:mets xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <mets:dmdSec ID="dmd35716">
        <mods:identifier type="urn">urn:nbn:de:hbz:xyz</mods:identifier>
    <mets:dmdSec ID="dmd35717">
       <mods:identifier type="urn">urn:nbn:de:hbz:abcde</mods:identifier>

Aus der StructMap geht dabei hervor, dass beide Teilobjekte (dmd35717,dmd35716) auf oberster Ebene gleichwertig im METS-Baum aufgehangen sind.

#### Vorbedingungen:

* siehe Hintergrund.

#### Durchführung:

# Siehe Hintergrund.

#### Akzeptanzkriterien: (Vorschlag 1)

* In der Maske "Eingelieferte Objekte" wird das Objekt nicht mit dem URN urn:nbn:de:hbz:xyz gelistet.
* In der Maske "Eingelieferte Objekte" wird das Objekt mit einer vom DNSCore vergebenen URN startend mit{color} {color:#ff0000}{*}urn:nbn:danrw{*}{color} {color:#ff0000}gelistet.{color}
* Die Objektlogdatei enthält eine Warning mit dem Verweis darauf, dass eventuell mitgelieferte URN vergeben wurden.{color}

#### Akzeptanzkriterien: (Vorschlag 2)

* Der User wird per Mail informiert, dass die mitgelieferte METS-URN nicht eindeutig einem Objekt auf höchster Ebene zugewiesen werden kann.{color}
* Der Ingest wird abgelehnt.{color}



## {color:#ff0000}Szenario&nbsp;AT-IV-4{color} {color:#000000}Präzedenzregelung bei mitgelieferter URN in METS und PREMIS{color}

{color:#ff0000}Dieses Szenario ist nicht implementiert.{color} {color:#000000}Es befindet sich derzeit in der Konzeptionsphase.&nbsp;{color}

{color:#ff0000}{*}Vorschlag 1 :*{color} {color:#ff0000}Eine URN wird der anderen vorgezogen (z.B. die PREMIS hat Vorrang):{color}

{color:#ff0000}{*}Vorschlag 2{*}{color}{color:#ff0000}: Eine{color} {color:#ff0000}Benutzerfehlermeldung{color} {color:#ff0000}wird vom System ausgegeben. Das Paket wird abgelehnt.{color}

#### Testpaket(e):

{noformat:nopanel=true}
  (GitHub) Testpaket enhält
    data/export_mets.xml
    data/premis.xml
    data/(Weitere Primärdaten)
{noformat}

{color:#000000}{*}Inhalt export_mets.xml{*}{color}

{noformat:nopanel=true}
    <?xml version="1.0" encoding="UTF-8"?>
    <mets:mets xmlns:mets="http://www.loc.gov/METS/" xmlns:mods="http://www.loc.gov/mods/v3" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <mets:dmdSec ID="dmd35716">
        <mets:mdWrap MDTYPE="MODS">
        <mets:xmlData>
        <mods:mods>
        <mods:titleInfo>
            <mods:title>Nr. 1</mods:title>
        </mods:titleInfo>
        <mods:identifier type="VERA dockey">Vz f30cfb5b-f914-4973-a5cf-04e110ad55c9</mods:identifier>
        <mods:identifier type="urn">urn:nbn:de:danrw:de2190-f30cfb5b-f914-4973-a5cf-04e110ad55c9[Prüfziffer]</mods:identifier>
        <mods:relatedItem>
            <mods:identifier>Best 1b70eccc-bb1c-43a2-b402-14df11310578</mods:identifier>
        </mods:relatedItem>
        </mods:mods>
        </mets:xmlData>
        </mets:mdWrap>
    </mets:dmdSec>
{noformat}


*Inhalt premis.xml*

{noformat:nopanel=true}
    <object xsi:type="representation">
    <objectIdentifier>
        <objectIdentifierType>URN</objectIdentifierType>
        <objectIdentifierValue>urn:nbn:de:xyz-1-20131008367735</objectIdentifierValue>
    </objectIdentifier>
    </object>

{noformat}

#### Vorbedingungen:

* siehe Hintergrund.

#### Durchführung:

# Siehe Hintergrund.

#### Akzeptanzkriterien: (Vorschlag 1)

* In der Maske "Eingelieferte Objekte" wird das Objekt mit der URN gelistet.
** Der Einlieferungsbeleg enthält den Hinweis, dass dem Paket die URN zugewiesen wurde.

Welche URN soll hier genommen werden?

#### Akzeptanzkriterien: (Vorschlag 2)

* Das System informiert den User mit per Fehlermeldung, dass die URN nicht eindeutig ermittelt werden kann und der Ingest abgebrochen wird.
