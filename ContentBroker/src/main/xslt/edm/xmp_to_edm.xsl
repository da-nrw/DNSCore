<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:edm="http://www.europeana.eu/schemas/edm/"
    xmlns:dcterms="http://purl.org/dc/terms/"
    xmlns:photoshop="http://ns.adobe.com/photoshop/1.0/"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:ore="http://www.openarchives.org/ore/terms/">
    
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/>
    
    <xsl:param name="urn"/>    
    <xsl:param name="cho-base-uri"/>
    <xsl:param name="aggr-base-uri"/>
    <xsl:param name="europeana-type"/>
    <xsl:param name="provider"/>
    
    <xsl:template match="/">
        <rdf:RDF>
            <xsl:apply-templates select="rdf:RDF/rdf:Description"/>
        </rdf:RDF>
    </xsl:template>
    
    <xsl:template match="rdf:Description">
        
        <!-- CHO -->
        <xsl:variable name="cho-id">
            <xsl:value-of select="$cho-base-uri"/>-<xsl:value-of select="count(preceding-sibling::*) + 1"/>
        </xsl:variable>
        <edm:ProvidedCHO rdf:about="{$cho-id}">
            <xsl:call-template name="mapDC">
                <xsl:with-param name="from">dc:title</xsl:with-param>
                <xsl:with-param name="to">dc:identifier</xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="map">
                <xsl:with-param name="from">photoshop:Headline</xsl:with-param>
                <xsl:with-param name="to">dc:title</xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="map">
                <xsl:with-param name="from">photoshop:City</xsl:with-param>
                <xsl:with-param name="to">dcterms:spatial</xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="map">
                <xsl:with-param name="from">photoshop:Country</xsl:with-param>
                <xsl:with-param name="to">dcterms:spatial</xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="map">
                <xsl:with-param name="from">photoshop:State</xsl:with-param>
                <xsl:with-param name="to">dcterms:spatial</xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="map">
                <xsl:with-param name="from">photoshop:DateCreated</xsl:with-param>
                <xsl:with-param name="to">dcterms:created</xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="mapDC">
                <xsl:with-param name="from">dc:rights</xsl:with-param>
                <xsl:with-param name="to">dc:rights</xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="mapDC">
                <xsl:with-param name="from">dc:description</xsl:with-param>
                <xsl:with-param name="to">dc:description</xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="mapDC">
                <xsl:with-param name="from">dc:creator</xsl:with-param>
                <xsl:with-param name="to">dc:creator</xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="mapDC">
                <xsl:with-param name="from">dc:subject</xsl:with-param>
                <xsl:with-param name="to">dc:subject</xsl:with-param>
            </xsl:call-template>
        </edm:ProvidedCHO>
        
        <!-- Aggregation -->
        <xsl:variable name="aggr-id">
            <xsl:value-of select="$aggr-base-uri"/>-<xsl:value-of select="count(preceding-sibling::*) + 1"/>
        </xsl:variable>
        <ore:Aggregation rdf:about="{$aggr-id}">
            
            <edm:aggregatedCHO rdf:resource="{$cho-id}"/>
            
            <xsl:call-template name="map">
                <xsl:with-param name="from">photoshop:Credit</xsl:with-param>
                <xsl:with-param name="to">edm:dataProvider</xsl:with-param>
            </xsl:call-template>
            
            <edm:isShownBy rdf:resource="{@rdf:about}"/>
            
            <!-- later extract rights from EAD or by param? -->
            <edm:rights rdf:resource="http://www.europeana.eu/rights/rr-r/"/>
            
        </ore:Aggregation>
        
    </xsl:template>
    
    <xsl:template name="map">
        <xsl:param name="from"/>
        <xsl:param name="to"/>
        <xsl:if test="*[name()=$from]">
             <xsl:element name="{$to}"><xsl:value-of select="*[name()=$from]"/></xsl:element>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="mapDC">
        <xsl:param name="from"/>
        <xsl:param name="to"/>
        <xsl:if test="*[name()=$from]">
            <xsl:for-each select="*[name()=$from]/rdf:Alt/rdf:li|*[name()=$from]/rdf:Bag/rdf:li|*[name()=$from]/rdf:Seq/rdf:li">
                <xsl:element name="{$to}"><xsl:value-of select="."/></xsl:element>
            </xsl:for-each>
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>
