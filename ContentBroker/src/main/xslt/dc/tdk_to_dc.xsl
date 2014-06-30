<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:template match="/">
        <oai_dc:dc xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
            <xsl:apply-templates select="Theater_der_Klaenge_Archiv_Videoband/*" />
            <dc:publisher>Theater der Klänge</dc:publisher>
            <dc:rights>Theater der Klänge</dc:rights>
            <dc:description>
                <xsl:if test="Theater_der_Klaenge_Archiv_Videoband/buehnenwerk">Bühnenwerk: <xsl:value-of select="Theater_der_Klaenge_Archiv_Videoband/buehnenwerk"/>
                </xsl:if>             
                <xsl:if test="Theater_der_Klaenge_Archiv_Videoband/laenge_min">
Länge in Minuten: <xsl:value-of select="Theater_der_Klaenge_Archiv_Videoband/laenge_min"/>
                </xsl:if>
                <xsl:if test="Theater_der_Klaenge_Archiv_Videoband/vorspann_vorhanden">
Vorspann vorhanden: <xsl:value-of select="Theater_der_Klaenge_Archiv_Videoband/vorspann_vorhanden"/>
                </xsl:if>
                <xsl:if test="Theater_der_Klaenge_Archiv_Videoband/abspann_vorhanden">
Abspann vorhanden: <xsl:value-of select="Theater_der_Klaenge_Archiv_Videoband/abspann_vorhanden"/>
                </xsl:if>
                <xsl:if test="Theater_der_Klaenge_Archiv_Videoband/applaus_vorhanden">
Applaus vorhanden: <xsl:value-of select="Theater_der_Klaenge_Archiv_Videoband/applaus_vorhanden"/>
                </xsl:if>
                <xsl:if test="Theater_der_Klaenge_Archiv_Videoband/aufzeichnung_handlungsbegleitend">
Aufzeichnung handlungsbegleitend: <xsl:value-of select="Theater_der_Klaenge_Archiv_Videoband/aufzeichnung_handlungsbegleitend"/>
                </xsl:if>
                <xsl:if test="Theater_der_Klaenge_Archiv_Videoband/aufzeichnung_totale">
Aufzeichnung totale: <xsl:value-of select="Theater_der_Klaenge_Archiv_Videoband/aufzeichnung_totale"/>
                </xsl:if>
                <xsl:if test="Theater_der_Klaenge_Archiv_Videoband/stueck_komplett">
Stück komplett: <xsl:value-of select="Theater_der_Klaenge_Archiv_Videoband/stueck_komplett"/>
                </xsl:if>
                <xsl:if test="Theater_der_Klaenge_Archiv_Videoband/film_komplett">
Film komplett: <xsl:value-of select="Theater_der_Klaenge_Archiv_Videoband/film_komplett"/>
                </xsl:if>
                <xsl:if test="Theater_der_Klaenge_Archiv_Videoband/technischer_untertitel">
Technischer Untertitel: <xsl:value-of select="Theater_der_Klaenge_Archiv_Videoband/technischer_untertitel"/>
                </xsl:if>
            </dc:description>
        </oai_dc:dc>
    </xsl:template>
    
    <xsl:template match="bandnummer">
        <dc:identifier><xsl:value-of select="."/></dc:identifier>
    </xsl:template>
    
    <xsl:template match="titel[following-sibling::untertitel]">
        <dc:title><xsl:value-of select="."/> : <xsl:value-of select="following-sibling::untertitel"/></dc:title>
    </xsl:template>
    
    <xsl:template match="titel">
        <dc:title><xsl:value-of select="."/></dc:title>
    </xsl:template>
    
    <xsl:template match="datum">
        <dc:date><xsl:value-of select="."/></dc:date>
    </xsl:template>
    
    <xsl:template match="personUndFunktion">
        <xsl:apply-templates select="*"/>
    </xsl:template>
    
    <xsl:template match="institution">
        <xsl:apply-templates select="*"/>
    </xsl:template>
    
    <xsl:template match="personUndFunktion/darsteller">
        <xsl:apply-templates select="*"/>
    </xsl:template>
    
    <xsl:template match="funktion">
        <dc:contributor><xsl:value-of select="."/><xsl:if test="@type"> (<xsl:value-of select="@type"/>)</xsl:if></dc:contributor>
    </xsl:template>
    
    <xsl:template match="rolle">
        <dc:contributor><xsl:value-of select="."/><xsl:if test="@name"> (<xsl:value-of select="@name"/>)</xsl:if></dc:contributor>
    </xsl:template>
    
    <!-- suppress all else:-->
    <xsl:template match="*"/>
    
</xsl:stylesheet>
