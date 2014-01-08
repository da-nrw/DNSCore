<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/">
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:template match="/">
        <oai_dc:dc xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
            <xsl:apply-templates select="/ead/archdesc"/>
        </oai_dc:dc>
    </xsl:template>
    
    <xsl:template match="archdesc">
        <xsl:apply-templates select="*"/>
        <xsl:if test="@type">
            <dc:type><xsl:value-of select="normalize-space(@type)"/></dc:type>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="geogname">
        <dc:coverage>
            <xsl:value-of select="normalize-space(.)" />
        </dc:coverage>
    </xsl:template>
    
    <xsl:template match="scopecontent | abstract">
        <dc:description>
            <xsl:value-of select="normalize-space(.)" />
        </dc:description>
    </xsl:template>
    
    <xsl:template match="controlaccess">
        <xsl:apply-templates />
    </xsl:template>    
    <xsl:template match="subject">
        <dc:subject>
            <xsl:value-of select="normalize-space(.)" />
        </dc:subject>
    </xsl:template>
    
    <xsl:template match="did">
        <xsl:apply-templates />
    </xsl:template>
    <xsl:template match="unittitle">
        <dc:title>
            <xsl:value-of select="normalize-space(.)" />
        </dc:title>
    </xsl:template>
    <xsl:template match="unitdate">
        <dc:date>
            <xsl:value-of select="normalize-space(.)" />
        </dc:date>
    </xsl:template>
    <xsl:template match="unitid">
        <dc:identifier>
            <xsl:value-of select="normalize-space(.)" />
        </dc:identifier>
    </xsl:template>
    <xsl:template match="origination">
        <dc:contributor>
            <xsl:value-of select="normalize-space(.)" />
        </dc:contributor>
    </xsl:template>
    
    <xsl:template match="langmaterial">
        <xsl:apply-templates />
    </xsl:template>
    <xsl:template match="language">
        <dc:language>
            <xsl:value-of select="normalize-space(.)" />
        </dc:language>
    </xsl:template>
    
    <xsl:template match="repository">
        <dc:publisher>
            <xsl:value-of select="normalize-space(.)" />
        </dc:publisher>
    </xsl:template>
    
    <!-- suppress all else:-->
    <xsl:template match="*"/>
    
</xsl:stylesheet>
