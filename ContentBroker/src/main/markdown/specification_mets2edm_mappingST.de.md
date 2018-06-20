## Mapping für Titel im Portal:

<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Regeln für das Mergen der Felder</sub></th>
<th><sub>Status</sub></th>
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
<td><sub>Umgesetzt in Build 1856</sub></td>
</tr></tbody></table>



## Mapping auf Person(en) / Institution(en) im Portal
<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Regeln für das Mergen der Felder</sub></th>
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
<td><sub>Build 1856</sub></td>
</tr>
<tr>
<td><sub>
  -mods.originInfo.publisher $1 <br>
  -mods.originInfo.place/placeTerm[type=text] $2<br>
  -mods.originInfo.place/dateIssued $3</sub></td>
<td><sub>-dc.publisher $1 und $2 <br>
-dcterms.issued = $3</sub></td>
<td><sub>-dc.publisher = $1 + " (" + $2 + ")"<br>
-dcterms.issued = $3</sub></td>
<td><sub>Build 1856</sub></td>
</tr>
</tbody></table>

## Mapping für edmDataProvider in OAI-PMH
<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Regeln für das Mergen der Felder</sub></th>
<th><sub>Umgesetzt in</sub></th>
</tr></thead><tbody><tr>
<td><sub>
mods.physicalDescription.extent </sub></td>
<td><sub>dcterms.extend</sub></td>
<td><sub></sub></td>
<td><sub>Build 1888</sub></td>
</tr></tbody></table>

## Mapping der Rechte/Lizenzen
<table><thead><tr>
<th><sub>Quelle Mods</sub></th>
<th><sub>Mapping zu EDM</sub></th>
<th><sub>Regeln für das Mergen der Felder</sub></th>
<th><sub>Umgesetzt in</sub></th>
</tr></thead><tbody><tr>
<td><sub>
dv.rights.owner </sub></td>
<td><sub>edm.dataProvider</sub></td>
<td><sub></sub></td>
<td><sub>Build 1856</sub></td>
</tr>
<tr>
<td><sub>
mods.accessCondition[type="use and reproduction"].attr('xlink:href') </sub></td>
<td><sub>edm.ProvidedCHO.dc.rights</sub></td>
<td><sub></sub></td>
<td><sub>Build 1916</sub></td>
</tr></tbody></table>
