<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
        
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:param name="object-id"/>
    
    <xsl:variable name="base-uri">http://www.danrw.de/data</xsl:variable>
    
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="daoloc">
        <xsl:variable name="ds-id">            
            <xsl:call-template name="replace-string">
                <xsl:with-param name="text">
                    <xsl:call-template name="replace-string">
                        <xsl:with-param name="text" select="@href"/>
                        <xsl:with-param name="replace" select="'='"/>
                        <xsl:with-param name="with" select="'_'"/>
                    </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="replace" select="'/'"/>
                <xsl:with-param name="with" select="'-'"/>
            </xsl:call-template>
        </xsl:variable>
        <daoloc href="{$base-uri}/{$object-id}/{$ds-id}" title="{@title}"/>
    </xsl:template>
    
    <!-- copy every element -->
    <xsl:template match="*|@*">
        <xsl:copy>
            <xsl:apply-templates select="*|@*|text()"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- utility templates -->
    <xsl:template name="replace-string">
        <xsl:param name="text"/>
        <xsl:param name="replace"/>
        <xsl:param name="with"/>
        <xsl:choose>
            <xsl:when test="contains($text,$replace)">
                <xsl:value-of select="substring-before($text,$replace)"/>
                <xsl:value-of select="$with"/>
                <xsl:call-template name="replace-string">
                    <xsl:with-param name="text"
                        select="substring-after($text,$replace)"/>
                    <xsl:with-param name="replace" select="$replace"/>
                    <xsl:with-param name="with" select="$with"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$text"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
