# Leistungsmerkmal: Lizenzverarbeitung für LIDO-SIP

Ausgehend von einer NRW-Regelung müssen alle mit Publikationsabsicht eingeliefrten SIP's eine gültige Veröffentlichungslizenz beinhalten.

Die Lizenz kann in den LIDO-Metadaten stehen, oder in der PREMIS-Datei enthalten sein, jedoch nicht an beiden stellen gleichzeitig. Die Lizenzangabe aus der PREMIS-Datei wird in die LIDO-Datei des PIP übernommen. Anschließend wird die angegebene Lizenz ins EDM übernommen und bis zum Portal weitergereicht. Der Ort für die Lizenzangabe in den LIDO-Metadaten ist **lido/administrativeMetadata/resourceWrap/resourceSet/rightsResource/rightsType**. Wichtig ist dass es einen conceptID-Element vom URI-Type und einen term-Element gibt. Die URI wird bis ins Portal weitergereicht.


Beispielhafte Verschachtelung der Lizenz in einem LIDO-XML-Bereich:
```    
<lido:lido>  
  ...  
  <lido:administrativeMetadata>
    <lido:resourceWrap>
      <lido:resourceSet>
	<lido:rightsResource>
          <lido:rightsType>
            <lido:conceptID lido:type="URI">http://creativecommons.org/licenses/by/3.0/de/</lido:conceptID>
            <lido:term lido:pref="preferred" lido:addedSearchTerm="no" xml:lang="de">CC BY 3.0 DE</lido:term>
          </lido:rightsType>
        </lido:rightsResource>
      </lido:resourceSet>
    </lido:resourceWrap>
  </lido:administrativeMetadata>
</lido:lido>
```

 
#### Kontext:

* [ATIngestLicensedLidoSip](../../test/java/de/uzk/hki/da/at/ATIngestLicensedLidoSip.java)


#### Testpakete:

```
  (GitHub) ../../src/test/resources/at/ATLidoSipLicenseInLido.tgz
  (GitHub) ../../src/test/resources/at/ATLidoSipLicenseInPremis.tgz
  (GitHub) ../../src/test/resources/at/ATLidoSipLicenseInPremisMultipleEmptyAM.tgz
```

#### Vorbedingungen:

* Der Tester ist unter der Rolle "Contractor" angemeldet/eingeloggt in der "DAWeb"

#### Durchführung:

* Testpaket ins Incoming-Order ablegen und die Verarbeitung starten (Maske "Verarbeitung für abgelieferte SIP starten")
* Warten auf die Bestätigungsmail.

#### Akzeptanzkriterien:
* Die Lizenz ist in den PIP-Metadaten und im Portal bei dem Objekt enthalten
* Überprüfen ob die Lizenz in den PIP-Metadaten (LIDO, EDM, DC) in Fedora enthalten ist.
* Überprüfen ob die Lizenz bei dem entsp. Objekt im Portal erscheint.


## Szenario AT-LIDO-MULTI Es gibt mehrere Administrative-Bereiche
Bei dem Anwendungsfall, dass die Lizenz in der PREMIS-Datei angegeben worden ist und die LIDO-Datei mehrere Administrativen-Bereichen(administrativeMetadata) enthält, müssen alle administrativeMetadata-Bereiche vom ContentBrocker mit Lizenz und ggf. entsprechenden weiteren strukturellen Elementen ergänzt werden.

#### Durchführung:

* Testpaket (ATLidoSipLicenseInPremisMultipleEmptyAM.tgz) ins Incoming-Order ablegen und die Verarbeitung starten (Maske "Verarbeitung für abgelieferte SIP starten")
* Warten auf die Bestätigungsmail.

#### Akzeptanzkriterien:
* Die Lizenz ist in den PIP-Metadaten und im Portal bei dem Objekt enthalten
* Überprüfen ob die Lizenz in allen administrativeMetadata-Bereichen der LIDO-Datei (aus Fedora-Repository) existiert.
