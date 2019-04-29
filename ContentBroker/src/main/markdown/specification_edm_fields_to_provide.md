# Vorgaben für die nach EDM zu mappenden Felder

Grundlage für die Vorgaben sind die Anforderungen der Europeana für die Ablieferung von EDM-basierten Metadaten.

[EDM-Guidelines 2017](https://pro.europeana.eu/files/Europeana_Professional/Share_your_data/Technical_requirements/EDM_Documentation/EDM_Mapping_Guidelines_v2.4_102017.pdf)

## Felder im Namensraum edm:providedCHO

### Pflichtfelder

<table><thead>
<tr>
<td> Feld </td>
<td> Bedingung </td>
<td> Umsetzung </td>
<tr>
<td>  </td>
<td>  </td>
<td> Mods </td>
<td> LIDO </td>
</tr></thead>
<tr>
<td> dc:language </td>
<td> nur Pflicht, wenn edm:type = TEXT </td>
<td> ja </td>
<td> nein </td>
</tr>
<tr>
<td> edm:type </td>
<td> kontrolliertes Vokabular: <br>
TEXT,	VIDEO,	SOUND,	IMAGE,	3D
</td>
<td> ja </td>
<td> nein </td>
</tr>
<tr>
<td> dc:title </td>
<td> Pflichtfeld, wenn kein dc:description vorhanden 
</td>
<td> nein </td>
<td> nein </td>
</tr>
<tr>
<td> dc:title </td>
<td> Pflichtfeld, wenn kein dc:description vorhanden 
</td>
<td> ja </td>
<td> ja </td>
</tr>
<tr>
<td> dc:description </td>
<td> Pflichtfeld, wenn kein dc:title vorhanden 
</td>
<td> nein </td>
<td> nein </td>
</tr>
<tr>
<td> dc:type <br>
dc:spatial <br>
dc:subject <br>
dc:temporal </td>
<td> Mindestens eins der Felder muss vorhanden sein 
</td>
<td> ja </td>
<td> nein </td>
</tr>
<tr>
<td> dc:type <br>
dc:spatial <br>
dc:subject <br>
dc:temporal </td>
<td> Mindestens eins der Felder muss vorhanden sein 
</td>
<td> ja </td>
<td> nein </td>
</tr>
</table>

### Empfohlene Felder

<table><thead>
<tr>
<td> Feld </td>
<td> Bedingung </td>
<td> Umsetzung </td>
<tr>
<td>  </td>
<td>  </td>
<td> Mods </td>
<td> LIDO </td>
</tr></thead>
<tr>
<td> dc:creator </td>
<td> </td>
<td> ja </td>
<td> ja </td>
</tr>
<tr>
<td> dc:contributor </td>
<td> </td>
<td> ja </td>
<td> nein </td>
</tr>
<tr>
<td> dcterms:created </td>
<td> </td>
<td> ja </td>
<td> Vorschlag </td>
</tr>
<tr>
<td> dcterms:issued </td>
<td> </td>
<td> ja </td>
<td> Vorschlag </td>
</tr>
<tr>
<td> dc:date </td>
<td> </td>
<td> nein </td>
<td> ja </td>
</tr>
</table>

## Felder im Namensraum ore:Aggregation

### Pflichtfelder

<table><thead>
<tr>
<td> Feld </td>
<td> Bedingung </td>
<td> Umsetzung </td>
<tr>
<td>  </td>
<td>  </td>
<td> Mods </td>
<td> LIDO </td>
</tr></thead>
<tr>
<td> edm:aggegatedCHO </td>
<td>  </td>
<td> nein </td>
<td> nein </td>
</tr>
<tr>
<td> edm:dataProvider </td>
<td> Der Datengeber </td>
<td> nein </td>
<td> nein </td>
</tr>
<tr>
<td> edm:provider </td>
<td> Der Datenlieferant an die Europeana </td>
<td> ja </td>
<td> ja </td>
</tr>
<tr>
<td> edm:rights </td>
<td>  
</td>
<td> ja </td>
<td> nein </td>
</tr>
<tr>
<td> edm:isShownBy </td>
<td> Pflichtfeld, wenn kein edm:isShownAt vorhanden 
</td>
<td> ja </td>
<td> nein </td>
</tr>
<tr>
<td> edm:isShownAt </td>
<td> Pflichtfeld, wenn kein edm:isShownBy vorhanden 
</td>
<td> ja </td>
<td> nein </td>
</tr>
</table>

### Empfohlene Felder

<table><thead>
<tr>
<td> Feld </td>
<td> Bedingung </td>
<td> Umsetzung </td>
<tr>
<td>  </td>
<td>  </td>
<td> Mods </td>
<td> LIDO </td>
</tr></thead>
<tr>
<td> edm:IntermediateProvider </td>
<td> </td>
<td> nein </td>
<td> nein </td>
</tr>
</table>

## Felder im Namensraum cc:Licence

### Pflichtfelder

<table><thead>
<tr>
<td> Feld </td>
<td> Bedingung </td>
<td> Umsetzung </td>
<tr>
<td>  </td>
<td>  </td>
<td> Mods </td>
<td> LIDO </td>
</tr></thead>
<tr>
<td> odrl:inheritFrom </td>
<td>  </td>
<td> nein </td>
<td> nein </td>
</tr>
</table>

