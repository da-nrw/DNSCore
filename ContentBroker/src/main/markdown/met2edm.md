# Mapping Tabelle METS/Mods zu EDM

Diese Tabelle enthält die Spezifikationen für das Metadaten-Mapping


Teste html


<table class='confluenceTable'><tbody>
<tr>
<th class='confluenceTh'> MODS <br class="atl-forced-newline" /> </th>
<th class='confluenceTh'> EDM / Index <br class="atl-forced-newline" /> </th>
<th class='confluenceTh'> Format <br class="atl-forced-newline" /> </th>
<th class='confluenceTh'> Bemerkung <br class="atl-forced-newline" /> </th>
<th class='confluenceTh'> Portal <br class="atl-forced-newline" /> </th>
<th class='confluenceTh'> Status </th>
</tr>
<tr>
<td class='confluenceTd'> titleInfo <br class="atl-forced-newline" />
displayLabel <br class="atl-forced-newline" />
subTitle <br class="atl-forced-newline" /> </td>
<td class='confluenceTd'> dc:title </td>
<td class='confluenceTd'> String <br class="atl-forced-newline" /> </td>
<td class='confluenceTd'>&nbsp;</td>
<td class='confluenceTd'> Titel </td>
<td class='confluenceTd'> <font color="#008000">Umgesetzt für Build&nbsp;</font><font color="#008000">1856</font> </td>
</tr>
<tr>
<td class='confluenceTd'> Alle name-Elemente: <br class="atl-forced-newline" />
name/role/roleTerm[@type='code'][text() = 'cre' or text()='aut'] <br class="atl-forced-newline" /> </td>
<td class='confluenceTd'> dc:creator </td>
<td class='confluenceTd'> String </td>
<td class='confluenceTd'> roleTerm/@type=code darf nicht in EDM übernommen werden&#33; <br class="atl-forced-newline" />
'Wenn roleTerm/@type=text existiert, Inhalt vor den Namen in die EDM schreiben. <br class="atl-forced-newline" />
&lt;dc:creator&gt;[roleTerm/@type=text]:Johann Wolfgang Goethe&lt;/dc:creator&gt; <br class="atl-forced-newline" /> </td>
<td class='confluenceTd'> Person <br class="atl-forced-newline" /> </td>
<td class='confluenceTd'> <font color="#008000">Umgesetzt für Build&nbsp;</font><font color="#008000">1888</font><br class="atl-forced-newline" /> </td>
</tr>
<tr>
<td class='confluenceTd'> Alle name-Elemente </td>
<td class='confluenceTd'> dc:contributor </td>
<td class='confluenceTd'> String </td>
<td class='confluenceTd'> roleTerm type=code darf nicht in EDM übernommen werden&#33; <br class="atl-forced-newline" />
Wenn roleTerm/@type=text existiert, Inhalt vor den Namen in die EDM schreiben. <br class="atl-forced-newline" />
&lt;dc:contributor&gt;[roleTerm/@type=text]: Johann Wolfgang Goethe&lt;/dc:contributor&gt; </td>
<td class='confluenceTd'> Person </td>
<td class='confluenceTd'> <font color="#008000">Umgesetzt für Build&nbsp;</font><font color="#008000">1888</font><br class="atl-forced-newline" /> </td>
</tr>
<tr>
<td class='confluenceTd'> originInfo/publisher </td>
<td class='confluenceTd'> dc:publisher </td>
<td class='confluenceTd'> String </td>
<td class='confluenceTd'>&nbsp;</td>
<td class='confluenceTd'> Verlag <br class="atl-forced-newline" /> </td>
<td class='confluenceTd'> <font color="#008000">Umgesetzt für Build&nbsp;</font><font color="#008000">1856</font> </td>
</tr>
<tr>
<td class='confluenceTd'> place/placeTerm text oder publisher </td>
<td class='confluenceTd'> dc:publisher </td>
<td class='confluenceTd'> String </td>
<td class='confluenceTd'>&nbsp;</td>
<td class='confluenceTd'> Verlag <br class="atl-forced-newline" /> </td>
<td class='confluenceTd'> <font color="#008000">Umgesetzt für Build&nbsp;</font><font color="#008000">1856</font> </td>
</tr>
<tr>
<td class='confluenceTd'> dateIssued (wenn Inhalt von&nbsp;&lt;mods:edition&gt; ungleich "[Electronic&nbsp;ed.]") </td>
<td class='confluenceTd'> &lt;dcterms:issued&gt; </td>
<td class='confluenceTd'> String </td>
<td class='confluenceTd'> EDM ermöglicht Refinements für spezielle Felder. Hier sollten sie anstelle des allgemeinen &lt;dc:date&gt; verwendet werden. </td>
<td class='confluenceTd'>&nbsp;</td>
<td class='confluenceTd'> <font color="#008000">Umgesetzt für Build&nbsp;</font><font color="#008000">1888</font> </td>
</tr>
<tr>
<td class='confluenceTd'> dateIssued (wenn Inhalt von&nbsp;&lt;mods:edition&gt; gleich "[Electronic&nbsp;ed.]") <br class="atl-forced-newline" /> </td>
<td class='confluenceTd'> &lt;dcterms:created&gt; </td>
<td class='confluenceTd'> String </td>
<td class='confluenceTd'>&nbsp;</td>
<td class='confluenceTd'>&nbsp;</td>
<td class='confluenceTd'> <font color="#008000">Umgesetzt für Build&nbsp;</font><font color="#008000">1888.</font> <font color="#ff6600">Dabei ist neuer weiterer Spezifikationsbedarf aufgefallen (vgl. nächstes Feld).</font><br class="atl-forced-newline" /> </td>
</tr>
<tr>
<td class='confluenceTd'> originInfo/publisher und wenn Inhalt von mods:edition unterhalb des originInfo </td>
<td class='confluenceTd'> dc:publisher Literal des Publishers mit Zusatz ﻿﻿[elektr. Ed.] </td>
<td class='confluenceTd'> String </td>
<td class='confluenceTd'> Hiermit soll die Zuordnung eines Publishers zu einer Ausgabe ermöglicht werden. EDM unterstützt eine solche Zuordnung über weitere Prädikate <b>nicht.</b> </td>
<td class='confluenceTd'>&nbsp;</td>
<td class='confluenceTd'> <font color="#ff0000">Vorschlag, muss von ULBs reviewed werden.</font> </td>
</tr>
<tr>
<td class='confluenceTd'> rightsMD/mdWrap/xmlDate/rights/owner </td>
<td class='confluenceTd'> edm:dataProvider </td>
<td class='confluenceTd'> String </td>
<td class='confluenceTd'>&nbsp;</td>
<td class='confluenceTd'>&nbsp;</td>
<td class='confluenceTd'> <font color="#008000">Umgesetzt für Build&nbsp;</font><font color="#008000">1856</font> </td>
</tr>
<tr>
<td class='confluenceTd'> FLocat href </td>
<td class='confluenceTd'> edm:isShownBy <br class="atl-forced-newline" />
edm:object <br class="atl-forced-newline" />
edm:hasView <br class="atl-forced-newline" /> </td>
<td class='confluenceTd'> String <br class="atl-forced-newline" />
String <br class="atl-forced-newline" />
String <br class="atl-forced-newline" /> </td>
<td class='confluenceTd'> <br class="atl-forced-newline" />
<br class="atl-forced-newline" />
Wird nur befüllt, wenn das Objekt mehrere Referenzen auf Digitalisate enthält. <br class="atl-forced-newline" /> </td>
<td class='confluenceTd'> <br class="atl-forced-newline" />
<br class="atl-forced-newline" />
Thumbnail <br class="atl-forced-newline" /> </td>
<td class='confluenceTd'> <font color="#008000">Umgesetzt für Build&nbsp;</font><font color="#008000">1856</font> </td>
</tr>
<tr>
<td class='confluenceTd'>&nbsp;</td>
<td class='confluenceTd'> dcterms: hasPart </td>
<td class='confluenceTd'> String </td>
<td class='confluenceTd'> Zeigt ggf. auf die untergeordneten Objekte </td>
<td class='confluenceTd'> Abhängige Objekte <br class="atl-forced-newline" /> </td>
<td class='confluenceTd'> <font color="#008000">Umgesetzt für Build&nbsp;</font><font color="#008000">1856</font> </td>
</tr>
<tr>
<td class='confluenceTd'>&nbsp;</td>
<td class='confluenceTd'> dcterms: isPartOf </td>
<td class='confluenceTd'> String </td>
<td class='confluenceTd'> Zeigt ggf. auf das übergeordnete Objekt </td>
<td class='confluenceTd'> Verweis </td>
<td class='confluenceTd'> <font color="#008000">Umgesetzt für Build&nbsp;</font><font color="#008000">1856</font> </td>
</tr>
<tr>
<td class='confluenceTd'> physicalDescription/extent </td>
<td class='confluenceTd'> dcterms:extent </td>
<td class='confluenceTd'> String </td>
<td class='confluenceTd'>&nbsp;</td>
<td class='confluenceTd'> Umfang <br class="atl-forced-newline" /> </td>
<td class='confluenceTd'> <font color="#008000">Umgesetzt für Build&nbsp;</font><font color="#008000">1888</font><br class="atl-forced-newline" /> </td>
</tr>
<tr>
<td class='confluenceTd'> originInfo/edition </td>
<td class='confluenceTd'> dc:publisher ? </td>
<td class='confluenceTd'> String </td>
<td class='confluenceTd'> eine eigene EDM-Kategorie für edition scheint es nicht zu geben <br class="atl-forced-newline" /> </td>
<td class='confluenceTd'> Ort / Verlag, Jahr </td>
<td class='confluenceTd'> <font color="#ff6600">In Planung</font> </td>
</tr>
<tr>
<td class='confluenceTd'> mods/accessCondition(type=use and reproduction)xlink:href </td>
<td class='confluenceTd'> edm:ProvidedCHO/dc:rights </td>
<td class='confluenceTd'> String(URL) </td>
<td class='confluenceTd'> Nur die URL aus dem mods lesen&#33; </td>
<td class='confluenceTd'> Nutzungsrechte </td>
<td class='confluenceTd'> <font color="#ff0000">In Planung: Wichtig für nächsten Build</font><br class="atl-forced-newline" /> </td>
</tr>
</tbody></table>

