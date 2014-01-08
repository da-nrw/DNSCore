<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:mets="http://www.loc.gov/METS/">
        
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:param name="object-id"/>
    
    <xsl:variable name="base-uri">http://www.danrw.de/data</xsl:variable>
    
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="//mets:fileGrp[1]">
        <mets:fileGrp USE="DEFAULT">
            <xsl:apply-templates select="mets:file">
                <xsl:with-param name="prefix" select="'public'"/>
            </xsl:apply-templates>
        </mets:fileGrp>
        <mets:fileGrp USE="THUMBS">
            <xsl:apply-templates select="mets:file">
                <xsl:with-param name="prefix" select="'thumbnail'"/>
            </xsl:apply-templates>
        </mets:fileGrp>
        <mets:fileGrp USE="INTERNAL">
            <xsl:apply-templates select="mets:file">
                <xsl:with-param name="prefix" select="'institution'"/>
            </xsl:apply-templates>
        </mets:fileGrp>
    </xsl:template>
    
    <xsl:template match="mets:file">
        <xsl:param name="prefix"/>
        <mets:file ID="{@ID}_{$prefix}">
            <xsl:apply-templates select="mets:FLocat">
                <xsl:with-param name="prefix" select="$prefix"/>
            </xsl:apply-templates>
        </mets:file>
    </xsl:template>
    
    <xsl:template match="mets:FLocat">
        <xsl:param name="prefix"/>
        <xsl:variable name="ds-id">            
            <xsl:call-template name="replace-string">
                <xsl:with-param name="text">
                    <xsl:call-template name="replace-string">
                        <xsl:with-param name="text">
                            <xsl:call-template name="replace-string">
                                <xsl:with-param name="text" select="@xlink:href"/>
                                <xsl:with-param name="replace" select="'.tif'"/>
                                <xsl:with-param name="with" select="'.jpg'"/>
                            </xsl:call-template>
                        </xsl:with-param>
                        <xsl:with-param name="replace" select="'='"/>
                        <xsl:with-param name="with" select="'_'"/>
                    </xsl:call-template>
                </xsl:with-param>
                <xsl:with-param name="replace" select="'/'"/>
                <xsl:with-param name="with" select="'-'"/>
            </xsl:call-template>
        </xsl:variable>
        <mets:FLocat xlink:href="{$base-uri}/{$object-id}/{$prefix}-{$ds-id}" LOCTYPE="{@LOCTYPE}"/>
    </xsl:template>
    
    <xsl:template match="//mets:fptr[@FILEID]">
        <mets:fptr FILEID="{@FILEID}_public"/>
        <mets:fptr FILEID="{@FILEID}_thumbnail"/>
        <mets:fptr FILEID="{@FILEID}_institution"/>
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
