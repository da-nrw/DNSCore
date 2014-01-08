<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:template match="/">
        <oai_dc:dc xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
            <xsl:apply-templates select="descendant::*"/>
        </oai_dc:dc>
    </xsl:template>
    
    <xsl:template match="dc:contributor/rdf:Bag/rdf:li">
        <dc:contributor><xsl:value-of select="."/></dc:contributor>
    </xsl:template>
    
    <xsl:template match="dc:coverage">
        <dc:coverage><xsl:value-of select="."/></dc:coverage>
    </xsl:template>
    
    <xsl:template match="dc:creator/rdf:Seq/rdf:li">
        <dc:creator><xsl:value-of select="."/></dc:creator>
    </xsl:template>
    
    <xsl:template match="dc:date/rdf:Seq/rdf:li">
        <dc:date><xsl:value-of select="."/></dc:date>
    </xsl:template>
    
    <xsl:template match="dc:description/rdf:Alt/rdf:li">
        <dc:description><xsl:value-of select="."/></dc:description>
    </xsl:template>
    
    <!-- format is now used to store the package type -->
	<!--
    <xsl:template match="dc:format">
        <dc:format><xsl:value-of select="."/></dc:format>
    </xsl:template>
    -->
    
    <xsl:template match="dc:identifier">
        <dc:identifier><xsl:value-of select="."/></dc:identifier>
    </xsl:template>
    
    <xsl:template match="dc:language/rdf:Bag/rdf:li">
        <dc:language><xsl:value-of select="."/></dc:language>
    </xsl:template>
    
    <xsl:template match="dc:publisher/rdf:Bag/rdf:li">
        <dc:publisher><xsl:value-of select="."/></dc:publisher>
    </xsl:template>
    
    <xsl:template match="dc:relation/rdf:Bag/rdf:li">
        <dc:relation><xsl:value-of select="."/></dc:relation>
    </xsl:template>
    
    <xsl:template match="dc:rights/rdf:Alt/rdf:li">
        <dc:rights><xsl:value-of select="."/></dc:rights>
    </xsl:template>
    
    <xsl:template match="dc:source">
        <dc:source><xsl:value-of select="."/></dc:source>
    </xsl:template>
    
    <xsl:template match="dc:subject/rdf:Bag/rdf:li">
        <dc:subject><xsl:value-of select="."/></dc:subject>
    </xsl:template>
    
    <xsl:template match="dc:title/rdf:Alt/rdf:li">
        <dc:title><xsl:value-of select="."/></dc:title>
    </xsl:template>
    
    <xsl:template match="dc:type/rdf:Bag/rdf:li">
        <dc:type><xsl:value-of select="."/></dc:type>
    </xsl:template>
    
    <!-- suppress all else:-->
    <xsl:template match="*"/>
    
</xsl:stylesheet>
