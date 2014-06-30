<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:edm="http://www.europeana.eu/schemas/edm/"
    xmlns:dcterms="http://purl.org/dc/terms/" xmlns:foaf="http://xmlns.com/foaf/0.1/"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:ore="http://www.openarchives.org/ore/terms/"
    xmlns:mets="http://www.loc.gov/METS/"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="2.0"
    xsi:schemaLocation="http://www.w3.org/1999/02/22-rdf-syntax-ns# http://www.danrw.de/schemas/edm/EDM.xsd">
    
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    
    <xsl:param name="urn"/>    
    <xsl:param name="cho-base-uri"/>
    <xsl:param name="aggr-base-uri"/>
    <xsl:param name="europeana-type"/>
    <xsl:param name="provider"/>
    
    <xsl:variable name="data-provider" select="/ead/archdesc/did/repository/corpname"/>
    <xsl:variable name="collection-title" select="/ead/archdesc/did/unittitle"/>

    <xsl:template match="/">
        <rdf:RDF>
            <xsl:apply-templates select="ead/archdesc[@level='collection']"/>
        </rdf:RDF>
    </xsl:template>

    <xsl:template match="archdesc[@level='collection']">
        
        <xsl:call-template name="component">
            <xsl:with-param name="cho-id" select="$cho-base-uri"/>
            <xsl:with-param name="aggr-id" select="$aggr-base-uri"/>
        </xsl:call-template>
        
         <xsl:apply-templates select="./dsc/c|./dsc/c01">
             <xsl:with-param name="parent-id" select="$cho-base-uri"/>
         </xsl:apply-templates>
        
    </xsl:template>

    <xsl:template match="c|c01|c02|c03|c04|c05|c06|c07|c08|c09|c10|c11|c12">
        
        <xsl:param name="parent-id"/>
        
        <xsl:variable name="cho-id">
            <xsl:value-of select="$cho-base-uri"/>-<xsl:value-of select="generate-id()"/>
        </xsl:variable>
        <xsl:variable name="aggr-id">
            <xsl:value-of select="$aggr-base-uri"/>-<xsl:value-of select="generate-id()"/>
        </xsl:variable>
        
        <xsl:call-template name="component">
            <xsl:with-param name="cho-id" select="$cho-id"/>
            <xsl:with-param name="aggr-id" select="$aggr-id"/>
            <xsl:with-param name="parent-id" select="$parent-id"/>
            <xsl:with-param name="title-prefix"><xsl:value-of select="$collection-title"/> - </xsl:with-param>
        </xsl:call-template>
        
        <xsl:apply-templates select="c|c01|c02|c03|c04|c05|c06|c07|c08|c09|c10|c11|c12">
            <xsl:with-param name="parent-id" select="$cho-id"/>
        </xsl:apply-templates>
        
    </xsl:template>

    <!-- component -->
    <xsl:template name="component">
        
        <xsl:param name="cho-id"/>
        <xsl:param name="aggr-id"/>
        <xsl:param name="parent-id"/>
        <xsl:param name="title-prefix"/>
        
        <!-- CHO -->
        <edm:ProvidedCHO rdf:about="{$cho-id}">
            
            <dc:identifier>
                <xsl:value-of select="$cho-id"/>
            </dc:identifier>
            
            <xsl:if test="./did/unittitle">
                <dc:title>
                    <xsl:value-of select="$title-prefix"/><xsl:value-of select="normalize-space(./did/unittitle)"/>
                </dc:title>
            </xsl:if>   
            <xsl:if test="./did/unitid">
                <dc:identifier>
                    <xsl:value-of select="./did/unitid"/>
                </dc:identifier>
            </xsl:if> 
            <!-- TODO durch SKOS-Konzepte ersetzen -->
            <xsl:if test="./did/unitid">
                <dc:type>
                    <xsl:value-of select="@level"/>
                </dc:type>
            </xsl:if>
            <xsl:if test="./did/unitdate/@normal">
                <dcterms:date>
                    <xsl:value-of select="./did/unitdate/@normal"/>
                </dcterms:date>
            </xsl:if>
            <xsl:if test="./did/unitdate">
                <dcterms:date>
                    <xsl:value-of select="./did/unitdate"/>
                </dcterms:date>
            </xsl:if>
            <xsl:if test="./index/indexentry/geogname">
                <xsl:for-each select="./index/indexentry/geogname">
                 <dcterms:spatial>
                     <xsl:value-of select="normalize-space(.)"/>
                 </dcterms:spatial>
                </xsl:for-each>
            </xsl:if>
            <xsl:if test="./did/physdesc/dimensions">
                <dcterms:extent>
                    <xsl:value-of select="./did/physdesc/dimensions"/>
                </dcterms:extent>
            </xsl:if>
            <xsl:if test="./did/physdesc/genreform">
                <dc:type>
                    <xsl:value-of select="normalize-space(./did/physdesc/genreform)"/>
                </dc:type>
            </xsl:if>
            <xsl:if test="./did/langmaterial">
                <dc:language>
                    <xsl:value-of select="normalize-space(./did/langmaterial)"/>
                </dc:language>
            </xsl:if>
            <xsl:if test="./did/physloc">
                <edm:currentLocation>
                    <xsl:value-of select="./did/physloc"/>
                </edm:currentLocation>
            </xsl:if>            
            <xsl:for-each select="./did/abstract|./did/note">
                <dc:description>
                    <xsl:value-of select="normalize-space(.)"/>
                </dc:description>
            </xsl:for-each>
            <xsl:for-each select="./scopecontent">
                <dc:description>
                    <xsl:if test="./head"><xsl:value-of select="./head"/>: </xsl:if>
                    <xsl:for-each select="./list/item">
                        <xsl:value-of select="normalize-space(.)"/>
                    </xsl:for-each>
                    <xsl:for-each select="./p">
                        <xsl:value-of select="normalize-space(.)"/>
                    </xsl:for-each>
                </dc:description>
            </xsl:for-each>
            <xsl:for-each select="./relatedmaterial">
                <dcterms:references>
                    <xsl:if test="./head"><xsl:value-of select="./head"/>: </xsl:if>
                    <xsl:for-each select="./list/item">
                        <xsl:value-of select="normalize-space(.)"/>
                    </xsl:for-each>
                    <xsl:for-each select="./p">
                        <xsl:value-of select="normalize-space(.)"/>
                    </xsl:for-each>
                </dcterms:references>
            </xsl:for-each>
            <xsl:for-each select="./odd">
                <dc:description>
                    <xsl:value-of select="./head"/>: <xsl:value-of select="normalize-space(.)"/>
                </dc:description>
            </xsl:for-each>            
            
            <xsl:if test="$parent-id">
                <dcterms:isPartOf rdf:resource="{$parent-id}"/>
            </xsl:if>
            
            <edm:type><xsl:value-of select="$europeana-type"/></edm:type>
            
        </edm:ProvidedCHO>
        
        <ore:Aggregation rdf:about="{$aggr-id}">
            
            <edm:aggregatedCHO rdf:resource="{$cho-id}" />
            
            <xsl:choose>
                <xsl:when test="./did/repository/corpname">
                    <edm:dataProvider><xsl:value-of select="./did/repository/corpname"/></edm:dataProvider>
                </xsl:when>
                <xsl:otherwise>
                    <edm:dataProvider><xsl:value-of select="$data-provider"/></edm:dataProvider>
                </xsl:otherwise>
            </xsl:choose>
            
            <edm:provider><xsl:value-of select="$provider"/></edm:provider>
            
            <xsl:for-each select="./daogrp/daoloc | ./did/daogrp/daoloc">
                
                <xsl:variable name="file-uri">                
                    <!-- keep absolute http references -->
                    <xsl:choose>
                        <xsl:when test="starts-with(@href, 'http://')">
                            <xsl:value-of select="@href"/>
                        </xsl:when>
                        <xsl:otherwise>
                            #<xsl:value-of select="@href"/>
                        </xsl:otherwise>
                    </xsl:choose>                    
                </xsl:variable>
                
                <xsl:variable name="file-format">
                    <xsl:choose>
                        <xsl:when test="ends-with(@href, '.xml')">mets</xsl:when>
                        <xsl:otherwise>
                            <xsl:if test="ends-with(@href, '.jpg')">image/jpeg</xsl:if>
                        </xsl:otherwise>
                    </xsl:choose>                    
                </xsl:variable>
                
                <xsl:if test="$file-format='mets'">
                    <!--<xsl:variable name="thumb-url">
                        <xsl:value-of select="document($file-uri)/mets:mets/mets:fileSec/mets:fileGrp[@USE='THUMBS']/mets:file/mets:FLocat/@xlink:href"/>
                    </xsl:variable>
                    <xsl:if test="$thumb-url != ''">
                        <edm:object rdf:resource="{$thumb-url}">
                            <dc:format>image/jpg</dc:format>
                        </edm:object>
                    </xsl:if>-->
                </xsl:if>
                
                <xsl:choose>
                    <xsl:when test="@role='image'">
                        <edm:isShownBy rdf:resource="{$file-uri}" dc:format="{$file-format}"/>                        
                    </xsl:when>
                    <xsl:when test="@role='image_thumb'">
                        <edm:object rdf:resource="{$file-uri}" dc:format="{$file-format}"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <edm:hasView rdf:resource="{$file-uri}" dc:format="{$file-format}"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:for-each>
            
            <!-- later extract rights from EAD or by param? -->
            <edm:rights rdf:resource="http://www.europeana.eu/rights/rr-r/"/>
            
        </ore:Aggregation>
        
    </xsl:template>

</xsl:stylesheet>
