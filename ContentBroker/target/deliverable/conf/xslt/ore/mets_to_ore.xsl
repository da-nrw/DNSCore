<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
    xmlns:owl="http://www.w3.org/2002/07/owl#"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:ore="http://www.openarchives.org/ore/terms/"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:mets="http://www.loc.gov/METS/"
    xmlns:xlink="http://www.w3.org/1999/xlink">
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:template match="/">
        <rdf:RDF>
            <xsl:apply-templates/>
        </rdf:RDF>
    </xsl:template>
    
    <xsl:key name="id" match="mets:*" use="@ID"/>
    
    <xsl:template match="mets:structMap">
        <ore:Aggregation>
            <xsl:apply-templates select="*|@*"/>
        </ore:Aggregation>        
    </xsl:template>
    
    <xsl:template match="mets:div[@ID]">
        <ore:aggreates>
            <ore:Aggretation rdf:ID="{@ID}">
                <xsl:apply-templates select="*|@*"/>
            </ore:Aggretation>
        </ore:aggreates>        
    </xsl:template>
    
    <xsl:template match="mets:div">
        <ore:aggreates>
            <ore:Aggretation>
                <xsl:apply-templates select="*|@*"/>
            </ore:Aggretation>
        </ore:aggreates>        
    </xsl:template>
    
    <xsl:template match="mets:fptr">
        <xsl:variable name="file" select="key('id',@FILEID)"/>
        <ore:aggregates rdf:resource="{$file/mets:FLocat/@xlink:href}"/>
    </xsl:template>
    
    <xsl:template match="mets:structLink/mets:smLink">
        <rdf:Description rdf:about="#{@xlink:from}">
            <ore:aggregates rdf:resource="#{@xlink:to}"/>
        </rdf:Description>
    </xsl:template>
    
    <xsl:template match="@TYPE">
        <dc:type><xsl:value-of select="."/></dc:type>
    </xsl:template>
    
    <xsl:template match="@LABEL">
        <rdfs:label><xsl:value-of select="."/></rdfs:label>
    </xsl:template>
    
    <xsl:template match="@DMDID">
        <xsl:variable name="dmdSec" select="key('id',.)"/>
        <xsl:result-document href="metadata/{.}.xml">
            <xsl:copy-of select="$dmdSec/mets:mdWrap/mets:xmlData/*[1]"/>
        </xsl:result-document>
        <ore:aggregates rdf:resource="metadata/{.}.xml">
            <dcterms:conformsTo rdf:resource="{namespace-uri($dmdSec/mets:mdWrap/mets:xmlData/*[1])}"/>
        </ore:aggregates>
    </xsl:template>
    
    <!-- Override built-in template rules -->    
    <xsl:template match="text()|@*"/>
    
</xsl:stylesheet>
