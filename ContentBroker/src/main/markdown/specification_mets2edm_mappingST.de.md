

## Mapping für Titel im Portal:

<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt in</sub></th>
</tr></thead><tbody><tr>
<td><sub> -mods.titleInfo.title $1<br>
-mods.titleInfo.subTitle $2<br>
-mods.titleInfo.nonSort $3<br>
-mods.titleInfo.displayLabel</sub></td>
<td><sub>dc.title</sub></td>
<td><sub>
-dc:title = $1 + " " + $2  ; wenn beide vorhanden.<br>
-dc:titel = $3 + " " + $1  ; wenn beide vorhanden.<br>
-dc:titel = $3 + " " + $1 + " " + $2  ; wenn drei vorhanden.<br></sub></td>
<td><sub>Titel</sub></td>
<td><sub>Build 1856</sub></td>
</tr></tbody></table>



## Mapping auf Person(en) / Institution(en) im Portal
<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt in</sub></th>
</tr></thead><tbody><tr>
<td><sub>
<b>wenn:</b> <br>
  mods/name/role/roleTerm[type=code] == 'aut' oder 'cre'<br>
<b>dann:</b> <br>
  -mods/name/namePart $1 <br>
  -mods/name/role/roleTerm[type=text] $2</sub></td>
<td><sub>dc:creator</sub></td>
<td><sub>dc:creator = $2 + ": " + $1 ; wenn beide vorhanden.</sub></td>
<td><sub>Person</sub></td>
<td><sub>Build 1856</sub></td>
</tr>
<tr>
<td><sub>
<b>wenn:</b> <br>
 -mods/name/role/roleTerm[type=code] != aut oder cre<br>
<b>dann:</b> <br>
  -mods/name/namePart $1 <br>
  -mods/name/role/roleTerm[type=text] $2</sub></td>
<td><sub>dc:contributor</sub></td>
<td><sub>dc:contributor= $2 + ": " + $1 ; wenn beide vorhanden.</sub></td>
<td><sub>Person</sub></td>
<td><sub>Build 1856</sub></td>
</tr>
<tr>
<td><sub>
  -mods.originInfo.publisher $1 <br>
  -mods.originInfo.place/placeTerm[type=text] $2<br>
  -mods.originInfo.place/dateIssued $3</sub></td>
<td><sub>-dc.publisher <br>
-dcterms.issued</sub></td>
<td><sub>-dc.publisher = $1 + " (" + $2 + ")"<br>
-dcterms.issued = $3</sub></td>
<td><sub>Verlag</sub></td>
<td><sub>Build 1856</sub></td>
</tr>
<tr>
<td><sub>
<b>wenn:</b> <br>
 -mods.originInfo.edition != "[Electronic ed.]"<br>
<b>dann:</b> <br>
 -mods.originInfo.publisher = $1<br>
-mods.originInfo.place.placeTerm[type=text] = $2<br>
-mods.originInfo.dateIssued = $3</sub></td></sub></td>
<td><sub>-dc.publisher<br>
-dcterms.issued </sub></td>
<td><sub>-dc.publisher = $1 + " (" + $2 + ")"<br>
-dcterms.issued = $3</sub></td>
<td><sub>Verlag</sub></td>
<td><sub>Vorschlag</sub></td>
</tr>
<tr>
<td><sub>
<b>wenn:</b> <br>
 -mods.originInfo.edition == "[Electronic ed.]"<br>
<b>dann:</b> <br>
 -mods.originInfo.publisher = $1<br>
-mods.originInfo.place.placeTerm[type=text] = $2<br>
-mods.originInfo.dateIssued = $3</sub></td></sub></td>
<td><sub>-dc.publisher<br>
-dcterms.issued</sub></td>
<td><sub>-dc.publisher = $1 + " (" + $2 + ")"+", [Elektr. Ed.]"<br>
-dcterms.issued = $3<br>
-Zusatz ", [Elektr. Ed.]" wird benötigt, um Publisher <br> zuordnen zu können. Soll im Portal nicht angezeigt werden.</sub></td>
<td><sub>Verlag</sub></td>
<td><sub>Vorschlag</sub></td>
</tr>
</tbody></table>

## Mapping für Umfang
<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt in</sub></th>
</tr></thead><tbody>
<tr>
<td><sub>
mods.physicalDescription.extent </sub></td>
<td><sub>dcterms.extend</sub></td>
<td><sub></sub></td>
<td><sub>Umfang</sub></td>
<td><sub>Build 1888</sub></td>
</tr>
<tr>
<td><sub>
$1 = mods.physicalDescription.extent <br>
$2 = mods.physicalDescription.note</sub></td>
<td><sub>dcterms.extend</sub></td>
<td><sub>dcterms.extend=$1+" , "+$2</sub></td>
<td><sub>Umfang</sub></td>
<td><sub></sub></td>
</tr></tbody></table>

## Mapping für Institutionstyp
<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt in</sub></th>
</tr></thead><tbody><tr>
<td><sub>
</sub></td>
<td><sub>@InstitutionType
</sub></td>
<td><sub>-Sofern der Contractor in der 'users'-Tabelle in der  
'provider_type'-Spalte einen nicht leeren String-Wert enthält, wird dieser in den Index als Wert der @institutionType-Variable übernommen.<br>
@InstitutionType=Archiv|Museum|Bibliothek</sub></td>
<td><sub>Für die Einschränkung der Suche auf einen Institutionstyp (Archiv, Museum, Bibliothek ...)</sub></td>
<td><sub>Build: 1954</sub></td>
</tr>
</tbody></table>


## Mapping für Objektbeziehnungen
<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt in</sub></th>
</tr></thead><tbody><tr>
<td><sub>
</sub></td>
<td><sub>dcterms:hasPart
</sub></td>
<td><sub>Zeigt ggf. auf die untergeordneten Objekte</sub></td>
<td><sub>Abhängige Objekte</sub></td>
<td><sub>Build: 1856</sub></td>
</tr>
<tr>
<td><sub>
</sub></td>
<td><sub>dcterms: isPartOf
</sub></td>
<td><sub>Zeigt ggf. auf das übergeordnete Objekt</sub></td>
<td><sub>Verweis</sub></td>
<td><sub>Build: 1856</sub></td>
</tr>
</tbody></table>

## Mapping für Thumbnail
<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt in</sub></th>
</tr></thead><tbody><tr>
<td><sub>FLocat href
</sub></td>
<td><sub>edm:isShownBy  <br>
edm:object  <br>
edm:hasView
</sub></td>
<td><sub>Wird nur befüllt, wenn das Objekt mehrere Referenzen auf Digitalisate enthält.</sub></td>
<td><sub>Thumbnail</sub></td>
<td><sub>Build: 1856</sub></td>
</tr>

</tbody></table>

## Mapping der Rechte/Lizenzen
<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt in</sub></th>
</tr></thead><tbody><tr>
<td><sub>
dv.rights.owner </sub></td>
<td><sub>edm.dataProvider </sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
<td><sub> </sub></td>
</tr>
<tr>
<td><sub>
rightsMD/mdWrap/xmlDate/rights/owner </sub></td>
<td><sub>edm.dataProvider </sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
<td><sub>Build 1856</sub></td>
</tr>
<tr>
<td><sub>
mods.accessCondition[type="use and reproduction"].attr('xlink:href') </sub></td>
<td><sub>edm.ProvidedCHO.dc.rights  </sub></td>
<td><sub></sub></td>
<td><sub>Nutzungsrechte</sub></td>
<td><sub>Build 1916</sub></td>
</tr>

</tbody></table>

## Zusätzliche Mappings für Europeana
<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt in</sub></th>
</tr></thead><tbody><tr>
<td><sub> </sub></td>
<td><sub>edm.ProvidedCHO.dc.description</sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
</tr><tr>
<td><sub>
mods.language.mods.languageTerm[authority=iso639-2b]\[type=code] </sub></td>
<td><sub>edm.ProvidedCHO.dc.language</sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
</tr>
<tr>
<td><sub>
"Digitales Archiv NRW" </sub></td>
<td><sub> edm.provider</sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
</tr><tr>
<td><sub>
mods.genre[authority=marcg] </sub></td>
<td><sub> edm.ProvidedCHO.dc.type</sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
</tr>
<tr>
<td><sub>
<b>wenn:</b>
mods.typeOfResource == "text"  <br>
<b>dann:</b> $1="TEXT"<br>
</sub></td>
<td><sub>edm.ProvidedCHO.dc.type =$1 <br> edm.ProvidedCHO.edm.type =$1</sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
</tr>
<tr>
<td><sub>
<b>wenn:</b>
mods.typeOfResource == "still image"  <br>
<b>dann:</b> $1="IMAGE"<br>
</sub></td>
<td><sub>edm.ProvidedCHO.dc.type =$1 <br> edm.ProvidedCHO.edm.type =$1</sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
</tr>
<tr>
<td><sub>
<b>wenn:</b>
mods.typeOfResource == "moving image"  <br>
<b>dann:</b> $1="VIDEO"<br>
</sub></td>
<td><sub>edm.ProvidedCHO.dc.type =$1 <br> edm.ProvidedCHO.edm.type =$1</sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
</tr>
<tr>
<td><sub>
<b>wenn:</b>
mods.typeOfResource == "sound recording"  <br>
<b>dann:</b> $1="SOUND"<br>
</sub></td>
<td><sub>edm.ProvidedCHO.dc.type =$1 <br> edm.ProvidedCHO.edm.type =$1</sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
</tr>
<tr>
<td><sub>
mods.accessCondition[type="use and reproduction"].attr('xlink:href') </sub></td>
<td><sub>edm.rights  </sub></td>
<td><sub></sub></td>
<td><sub>Nutzungsrechte</sub></td>
<td><sub></sub></td>
</tr>
</tbody></table>

