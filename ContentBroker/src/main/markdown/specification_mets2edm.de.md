# Mapping von METS / Mods zu EDM

Da auch Elternelemente in den XML-basierten Metadatenschemata relevant sein können, werden die Mappings werden in der Punktnotation bzw. in Form von jQuery/CSS-Selektoren dargestellt. Die Zweischeninhalte werden in Variablenschreibweise zwischengespeichert und teilweise mit Operatoren zum neuen Inhalt formiert.
Der '^'-Operator funktioniert wie XOR, d.h. entweder es wird der linke Operand genommen oder der rechte. Falls beide Vorhanden sind, so wird der linke bevorzugt, falls keiner der Operanden einen Wert besitzt, besitzt auch das Resultat der Operation keinen Wert.

Die Spalte "Quelle" gibt an, aus welchem Namensraum und Feldern aus dem Mets die relevante Daten für das Mapping bezogen werden können.
$1 bis $n können als Platzhalter für die Merging-Regeln verwendet werden.

Die Spalte "Mapping zu EDM" gibt an, wohin die aus der Quelle bezogenen Daten in das EDM geschrieben werden sollen.

Die Spalte "Bemerkung" beschreibt, wie mehrere Felder aus der Quelle in einem oder mehreren EDM-Feldern kombiniert werden.

Die Spalte "Portal" beschreibt, in welchem Feld die inhalte im Portal eingesehen werden können. Nicht alle verarbeiteten Felder werden in der Portal-Ansicht angezeigt.

Die Spalte "Umgesetzt" beschreibt den Umsetzungssstatus mögliche Ausprägungen ""(Leer), "Vorschlag", "Review durch ULB", "In Umsetzung", "Build XYZ".


### Mapping für Titel im Portal:

<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt</sub></th>
</tr></thead><tbody><tr>
<td><sub>  <b>fals mods.titleInfo vorhanden ist:</b> <br> <br>
 -mods.titleInfo.title $1<br>
-mods.titleInfo.displayLabel $2<br>
-mods.titleInfo.nonSort $3<br>
-mods.titleInfo.subTitle $4</sub></td>
<td><sub>dc.title</sub></td>
<td><sub>
  je nachdem welche Attribute vorhanden sind: <br> 
  -dc:title = $1  <br>
  -dc:title = $2  <br>
  -dc:title = $3  <br>
  -dc:title = $3 + " " + $1  <br>
  -dc:title = $1 + " : " + $4  <br>
  -dc:title = $2 + " : " + $4  <br>
  -dc:title = $3 + " : " + $4  <br>
  -dc:title = $3 + " " + $1 + " : " + $4  <br></sub></td>
<td><sub>Titel</sub></td>
<td><sub>Build 1856</sub></td>
</tr>
  
  <tr>
<td><sub>   <b>fals kein mods.titleInfo vorhanden ist:</b> <br> <br>
  -mods.relatedItem(@type=host).titleInfo.title $1<br><br>
  -mods.part.detail(@type='volume').number $VN  <br>
  -mods.part.detail(@type='volume').caption $VC  <br>
  -mods.part.detail(@type='issue').number $IN  <br> 
  -mods.part.detail(@type='issue').caption $IC  <br>
   
</sub></td>
<td><sub>dc.title</sub></td>
<td><sub>
   varVolume = $VN ^ $VC  <br>
   varIssue = $IN ^ $IC  <br>
    je nachdem welche Zwischenvariablen definiert sind: <br> <br>
  -dc:title = $1+", "+$varVolume+", "+$varIssue  <br>
  -dc:title = $1+", "+$varVolume  <br>
  -dc:title = $1+", "+$varIssue  <br>
  -dc:title = $1<br>
  </sub></td>
<td><sub>Titel</sub></td>
<td><sub>Vorschlag</sub></td>
</tr></tbody></table>



### Mapping auf Person(en) / Institution(en) im Portal
<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt</sub></th>
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

### Mapping für Umfang
<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt</sub></th>
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

### Mapping für Institutionstyp
<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt</sub></th>
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


### Mapping für Objektbeziehnungen
<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt</sub></th>
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

### Mapping für Thumbnail
<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt</sub></th>
</tr></thead><tbody><tr>
<td><sub>FLocat href
</sub></td>
<td><sub>edm:isShownBy  <br>
edm:object  <br>
edm:hasView
</sub></td>
<td><sub>Wird nur befüllt, wenn das Objekt mehrere Referenzen auf Digitalisate enthält.</sub></td>
<td><sub>Thumbnail</sub></td>
  <td><sub><span style="text-decoration-line:line-through;"> Build: 1856</span> Nein, fehlt in EDM</sub></td>
</tr>

</tbody></table>

### Mapping der Rechte/Lizenzen
<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt</sub></th>
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

### Zusätzliche Mappings für Europeana
<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Bemerkung</sub></th>
<th><sub>Portal</sub></th>
<th><sub>Umgesetzt</sub></th>
</tr></thead><tbody><tr>
<td><sub> </sub></td>
<td><sub>edm.ProvidedCHO.dc.description</sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
<td><sub>wahlfrei wenn dc.title, dann nicht verpflichtend</sub></td>
</tr><tr>
<td><sub>
mods.language.mods.languageTerm[authority=iso639-2b][type=code] </sub></td>
<td><sub>edm.ProvidedCHO.dc.language</sub></td>
<td><sub>Falls kein Language vorhanden ist, sollte 'zxx' als Code verwendet werden. <br> Bei mods.typeOfResource == "text" muss language vorhanden sein. </sub></td>
<td><sub></sub></td>
<td><sub>Vorschlag</sub></td>
</tr>
<tr>
<td><sub>
"Digitales Archiv NRW" </sub></td>
<td><sub> edm.provider</sub></td>
  <td><sub><em>mandatory</em></sub></td>
<td><sub></sub></td>
<td><sub>Vorschlag</sub></td>
</tr><tr>
<td><sub>
mods.genre[authority=marcg] </sub></td>
<td><sub> edm.ProvidedCHO.dc.type</sub></td>
<td><sub><em>mandatory</em></sub></td>
<td><sub></sub></td>
<td><sub></sub></td>
</tr>
<tr>
<td><sub>
<b>wenn:</b> mods.typeOfResource == "text"  <br>
<b>oder:</b> mods.typeOfResource == "notated music"  <br>
<b>oder:</b> mods.typeOfResource == "mixed material"  <br>
<b>oder:</b> mods.typeOfResource == UNBEKANNT  <br>
<b>dann:</b> $1="TEXT"<br>
</sub></td>
<td><sub>edm.ProvidedCHO.dc.type =$1 <br> edm.ProvidedCHO.edm.type =$1</sub></td>
<td><sub><em>mandatory</em></sub></td>
<td><sub></sub></td>
<td><sub>Vorschlag</sub></td>
</tr>
<tr>
<td><sub>
<b>wenn:</b>mods.typeOfResource == "still image"  <br>
<b>oder:</b> mods.typeOfResource == "cartographic"  <br>
<b>dann:</b> $1="IMAGE"<br>
</sub></td>
<td><sub>edm.ProvidedCHO.dc.type =$1 <br> edm.ProvidedCHO.edm.type =$1</sub></td>
<td><sub><em>mandatory</em></sub></td>
<td><sub></sub></td>
<td><sub>Vorschlag</sub></td>
</tr>
<tr>
<td><sub>
<b>wenn:</b> mods.typeOfResource == "moving image"  <br>
<b>dann:</b> $1="VIDEO"<br>
</sub></td>
<td><sub>edm.ProvidedCHO.dc.type =$1 <br> edm.ProvidedCHO.edm.type =$1</sub></td>
<td><sub><em>mandatory</em></sub></td>
<td><sub></sub></td>
<td><sub>Vorschlag</sub></td>
</tr>
<tr>
<td><sub>
  <b>wenn:</b>mods.typeOfResource == "sound recording"  <br>
  <b>dann:</b> $1="SOUND"<br>
</sub></td>
<td><sub>edm.ProvidedCHO.dc.type =$1 <br> edm.ProvidedCHO.edm.type =$1</sub></td>
<td><sub><em>mandatory</em></sub></td>
<td><sub></sub></td>
  <td><sub>Vorschlag</sub></td>
</tr>
  
  
<tr>
<td><sub>
  <b>wenn:</b>mods.typeOfResource == "three dimensional object"  <br>
  <b>dann:</b> $1="3D"<br>
</sub></td>
<td><sub>edm.ProvidedCHO.dc.type =$1 <br> edm.ProvidedCHO.edm.type =$1</sub></td>
<td><sub><em>mandatory</em></sub></td>
<td><sub></sub></td>
  <td><sub>Vorschlag</sub></td>
</tr>
  
  
<tr>
<td><sub>
mods.accessCondition[type="use and reproduction"].attr('xlink:href') </sub></td>
<td><sub>edm.rights  </sub></td>
<td><sub></sub></td>
<td><sub>Nutzungsrechte</sub></td>
<td><sub>Build 2046</sub></td>
</tr>
</tbody></table>

