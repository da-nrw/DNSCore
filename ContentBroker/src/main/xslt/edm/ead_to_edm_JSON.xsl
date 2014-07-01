<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
    
    <xsl:output method="text"/>
    
    <xsl:param name="nummer" select="''"/>
    
    <xsl:template match="/">
[<xsl:apply-templates select="ead/archdesc[@level='collection']"/>
]
    </xsl:template>

    <xsl:template match="archdesc[@level='collection']"> 
        <xsl:if test="./dsc/c01">
            <xsl:for-each select="./dsc/c01">
                <xsl:call-template name="class_numbered"/>
            </xsl:for-each>
        </xsl:if>
    </xsl:template>
   
    <xsl:template name="class_numbered">
        <xsl:for-each select="./c02/c03">
            <xsl:call-template name="file"/>           
        </xsl:for-each>
    </xsl:template>
  
  
  <xsl:template name="file">
       <xsl:variable name="number"><xsl:choose><xsl:when test="position() &gt; 100"><xsl:value-of select="position()"/></xsl:when><xsl:otherwise><xsl:choose><xsl:when test="position() &gt; 10">0<xsl:value-of select="position()"/></xsl:when><xsl:otherwise>00<xsl:value-of select="position()"/></xsl:otherwise></xsl:choose></xsl:otherwise></xsl:choose>
       </xsl:variable>
{
   "@id": "http://www.danrw.de/cho/<xsl:value-of select="$nummer"/>-<xsl:value-of select="position()"/>",
   "edm:providedCHO": {
       "@id": "http://www.danrw.de/cho/<xsl:value-of select="$nummer"/>-<xsl:value-of select="position()"/>",
       "dc:title": "<xsl:value-of select="normalize-space(./did/unittitle)"/>",
       "dcterms:isPartOf": "<xsl:value-of select="normalize-space(../../did/unittitle)"/> - <xsl:value-of select="normalize-space(../did/unittitle)"/>", 
       <!--"dc:date": "<xsl:value-of select="./did/unitdate/@normal"/>",-->             <xsl:if test="./index/indexentry/geogname">
       "dcterms:spatial": "<xsl:value-of select="./index/indexentry/geogname"/>",</xsl:if> <xsl:if test="./did/physdesc/dimensions">
       "dcterms:extent": "<xsl:value-of select="./did/physdesc/dimensions"/>",</xsl:if>    <xsl:if test="./did/physdesc/genreform">
       "dc:type": "<xsl:value-of select="./did/physdesc/genreform"/>",</xsl:if>
       "dc:identifier": [<xsl:for-each select="./did/unitid">
           "<xsl:value-of select="."/>"<xsl:if test="not(position()=last())">,</xsl:if></xsl:for-each>
       ]<xsl:if test="./did/langmaterial">,
       "dc:language": "<xsl:value-of select="./did/langmaterial"/>"</xsl:if>       
   }<xsl:if test="./did/daogrp">,
     "edm:hasView":<xsl:for-each select="./did/daogrp/daoloc[@role='image']"><xsl:if test="not(last()=1)"><xsl:if test="position()=1">[</xsl:if></xsl:if>
         {"@id": "<xsl:value-of select="@href"/>", "dc:format": "image/jpeg"}<xsl:if test="not(position()=last())">,</xsl:if>
         <xsl:if test="not(last()=1)"><xsl:if test="position()=last()">
         ]</xsl:if></xsl:if>,
     </xsl:for-each>
     "edm:object":<xsl:for-each select="./did/daogrp/daoloc[@role='image_thumb']"><xsl:if test="not(last()=1)"><xsl:if test="position()=1">[</xsl:if></xsl:if>
         {"@id": "<xsl:value-of select="@href"/>", "dc:format": "image/jpeg"}<xsl:if test="not(position()=last())">,</xsl:if>
         <xsl:if test="not(last()=1)"><xsl:if test="position()=last()">
         ]</xsl:if></xsl:if>,
     </xsl:for-each>
     "edm:dataProvider": {
       "dc:title": "<xsl:value-of select="normalize-space(/ead/archdesc/did/unittitle)"/>",
       "dc:type": "Archiv"
     }
    </xsl:if>
}<xsl:if test="not(position()=last())">,</xsl:if>
</xsl:template>
   
</xsl:stylesheet>