# Mapping Tabelle METS/Mods zu EDM

Diese Tabelle enthält die Spezifikationen für das Metadaten-Mapping


| METS/Mods | EDM/Index | Format | Bemerkung | Portal | Status |
| --------- | --------- | ------ | --------- | ------ | ------ |
| titleInfo \\
displayLabel \\
subTitle \\ | dc:title | String \\ | | Titel | {color:#008000}Umgesetzt für Build&nbsp;{color}{color:#008000}1856{color} |
| Alle name-Elemente: \\
name/role/roleTerm\[@type='code'\]\[text() = 'cre' or text()='aut'\] \\ | dc:creator | String | roleTerm/@type=code darf nicht in EDM übernommen werden\! \\
'Wenn roleTerm/@type=text existiert, Inhalt vor den Namen in die EDM schreiben. \\
<dc:creator>\[roleTerm/@type=text\]:Johann Wolfgang Goethe</dc:creator> \\ | Person \\ | {color:#008000}Umgesetzt für Build&nbsp;{color}{color:#008000}1888{color}\\ |
| Alle name-Elemente | dc:contributor | String | roleTerm type=code darf nicht in EDM übernommen werden\! \\
Wenn roleTerm/@type=text existiert, Inhalt vor den Namen in die EDM schreiben. \\
<dc:contributor>\[roleTerm/@type=text\]: Johann Wolfgang Goethe</dc:contributor> | Person | {color:#008000}Umgesetzt für Build&nbsp;{color}{color:#008000}1888{color}\\ |
| originInfo/publisher | dc:publisher | String | | Verlag \\ | {color:#008000}Umgesetzt für Build&nbsp;{color}{color:#008000}1856{color} |
| place/placeTerm text oder publisher | dc:publisher | String | | Verlag \\ | {color:#008000}Umgesetzt für Build&nbsp;{color}{color:#008000}1856{color} |
| dateIssued (wenn Inhalt von&nbsp;<mods:edition> ungleich "\[Electronic&nbsp;ed.\]") | <dcterms:issued> | String | EDM ermöglicht Refinements für spezielle Felder. Hier sollten sie anstelle des allgemeinen <dc:date> verwendet werden. | | {color:#008000}Umgesetzt für Build&nbsp;{color}{color:#008000}1888{color} |
| dateIssued (wenn Inhalt von&nbsp;<mods:edition> gleich "\[Electronic&nbsp;ed.\]") \\ | <dcterms:created> | String | | | {color:#008000}Umgesetzt für Build&nbsp;{color}{color:#008000}1888.{color} {color:#ff6600}Dabei ist neuer weiterer Spezifikationsbedarf aufgefallen (vgl. nächstes Feld).{color}\\ |
| originInfo/publisher und wenn Inhalt von mods:edition unterhalb des originInfo | dc:publisher Literal des Publishers mit Zusatz ﻿﻿\[elektr. Ed.\] | String | Hiermit soll die Zuordnung eines Publishers zu einer Ausgabe ermöglicht werden. EDM unterstützt eine solche Zuordnung über weitere Prädikate *nicht.* | | {color:#ff0000}Vorschlag, muss von ULBs reviewed werden.{color} |
| rightsMD/mdWrap/xmlDate/rights/owner | edm:dataProvider | String | | | {color:#008000}Umgesetzt für Build&nbsp;{color}{color:#008000}1856{color} |
| FLocat href | edm:isShownBy \\
edm:object \\
edm:hasView \\ | String \\
String \\
String \\ | \\
\\
Wird nur befüllt, wenn das Objekt mehrere Referenzen auf Digitalisate enthält. \\ | \\
\\
Thumbnail \\ | {color:#008000}Umgesetzt für Build&nbsp;{color}{color:#008000}1856{color} |
| | dcterms: hasPart | String | Zeigt ggf. auf die untergeordneten Objekte | Abhängige Objekte \\ | {color:#008000}Umgesetzt für Build&nbsp;{color}{color:#008000}1856{color} |
| | dcterms: isPartOf | String | Zeigt ggf. auf das übergeordnete Objekt | Verweis | {color:#008000}Umgesetzt für Build&nbsp;{color}{color:#008000}1856{color} |
| physicalDescription/extent | dcterms:extent | String | | Umfang \\ | {color:#008000}Umgesetzt für Build&nbsp;{color}{color:#008000}1888{color}\\ |
| originInfo/edition | dc:publisher ? | String | eine eigene EDM-Kategorie für edition scheint es nicht zu geben \\ | Ort / Verlag, Jahr | {color:#ff6600}In Planung{color} |
| mods/accessCondition(type=use and reproduction)xlink:href | edm:ProvidedCHO/dc:rights | String(URL) | Nur die URL aus dem mods lesen\! | Nutzungsrechte | {color:#ff0000}In Planung: Wichtig für nächsten Build{color}\\ | 

