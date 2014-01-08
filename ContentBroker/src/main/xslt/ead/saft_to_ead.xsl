<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    
    <xsl:template match="/Findmittel">
        <ead>
            <eadheader>
                <eadid><xsl:value-of select="Findmittel_Info/FM_Sig"/></eadid>                
                <filedesc>
                    <titlestmt>                
                        <titleproper><xsl:value-of select="Findmittel_Info/FM_Name"/></titleproper>
                    </titlestmt>
                </filedesc>
                <profiledesc>                    
                    <creation>
                        <xsl:value-of select="Datei_Info/Erstellung/Bearbeiter"/>                        
                        <date><xsl:value-of select="Datei_Info/Erstellung/Datum"/></date>
                    </creation>
                </profiledesc>
            </eadheader>
            <archdesc level="collection">
                <did>
                    <unittitle><xsl:value-of select="Findmittel_Info/Bestand_Info/Bestandsname"/></unittitle>
                    <unitid><xsl:value-of select="Findmittel_Info/Bestand_Info/Bestand_Sig"/></unitid>
                    <unitdate><xsl:value-of select="Findmittel_Info/Bestand_Info/Laufzeit/LZ_Text"/></unitdate>
                    <xsl:apply-templates select="Findmittel_Info/Bestand_Info/Provenienz | Findmittel_Info/Bestand_Info/Bem"/>
                    <physdesc>
                        <extent>
                            <xsl:if test="@Mass">
                                <xsl:attribute name="UNIT"><xsl:value-of select="@Mass"/></xsl:attribute>
                            </xsl:if>
                            <xsl:value-of select="Findmittel_Info/Bestand_Info/Umfang"/>
                        </extent>
                    </physdesc>
                </did>
                <xsl:apply-templates select="Findmittel_Info/Bestand_Info/Rechtsstatus | Findmittel_Info/Bestand_Info/Hilfsfeld"/>
                <dsc>
                    <xsl:apply-templates select="Klassifikation"></xsl:apply-templates>
                </dsc>
            </archdesc>
        </ead>
    </xsl:template>
    
    <xsl:template match="Provenienz">
        <origination>
            <xsl:value-of select="."/>
        </origination>
    </xsl:template>
    
    <xsl:template match="Bem">
        <note>
            <xsl:value-of select="."/>
        </note>
    </xsl:template>
    
    <xsl:template match="Rechtsstatus">
        <accessrestrict>
            <p><xsl:value-of select="."/></p>
        </accessrestrict>
    </xsl:template>
    
    <xsl:template match="Hilfsfeld">
        <odd>
            <xsl:if test="@Fkt">
                <head><xsl:value-of select="@Fkt"/></head>
            </xsl:if>
            <p><xsl:value-of select="."/></p>
        </odd>
    </xsl:template>
    
    <xsl:template match="Klassifikation">
            <xsl:apply-templates />
    </xsl:template>
    
    <xsl:template match="Verzeichnungseinheiten">   
        <xsl:apply-templates />
    </xsl:template>
    
    <xsl:template match="Film">
        <c TYPE="file">
            <did>
                <xsl:apply-templates select="Titel|Signatur|Laufzeit"/>
                <physdesc>
                    <genreform>Film</genreform>
                </physdesc>
            </did>
            <scopecontent>
                <list>                    
                    <xsl:apply-templates select="Person|Institution" />
                </list>
                <xsl:apply-templates select="Inhalt"/>
            </scopecontent>
            <xsl:apply-templates select="Rechtsstatus|Hilfsfeld" />
        </c>
    </xsl:template>
    
    <xsl:template match="Titel">
        <unittitle>
            <xsl:if test="@Titel_Art">
                <xsl:attribute name="TYPE"><xsl:value-of select="@Titel_Art"/></xsl:attribute>
            </xsl:if>
            <xsl:value-of select="."/>
        </unittitle>
    </xsl:template>
    
    <xsl:template match="Signatur">
        <unitid>
            <xsl:value-of select="."/>
        </unitid>
    </xsl:template>
    
    <xsl:template match="Laufzeit">
        <unitdate>
            <xsl:if test="@LZ_Art">
                <xsl:attribute name="CERTAINTY"><xsl:value-of select="@LZ_Art"/></xsl:attribute>
            </xsl:if>
            <xsl:if test="@LZ_Art">
                <xsl:attribute name="CERTAINTY"><xsl:value-of select="@LZ_Art"/></xsl:attribute>
            </xsl:if>
            <xsl:if test="@Dati_Fkt">
                <xsl:attribute name="DATECHAR"><xsl:value-of select="@Dati_Fkt"/></xsl:attribute>
            </xsl:if>
            <xsl:value-of select="Datierung"/>
            <xsl:value-of select="LZ_Text"/>
        </unitdate>
    </xsl:template>
    
    <xsl:template match="Inhalt">
        <xsl:apply-templates />
    </xsl:template>
    
    <xsl:template match="Szene_Anf">
        <head><xsl:value-of select="."/></head>
    </xsl:template>
    
    <xsl:template match="Inhalt/text()">
        <xsl:if test="normalize-space(.)">
            <p><xsl:copy-of select="normalize-space(.)"/></p>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="Inhalt/p">
    </xsl:template>
    
    <xsl:template match="Person">
        <item>
            <persname>                
                <xsl:if test="@Pers_Fkt">
                    <xsl:attribute name="ROLE"><xsl:value-of select="@Pers_Fkt"/></xsl:attribute>
                </xsl:if>
                <xsl:value-of select="."/>
            </persname>
        </item>
    </xsl:template>
    
    <xsl:template match="Institution">
        <item>
            <corpname>                
                <xsl:if test="@Inst_Fkt">
                    <xsl:attribute name="ROLE"><xsl:value-of select="@Inst_Fkt"/></xsl:attribute>
                </xsl:if>
                <xsl:value-of select="."/>
            </corpname>
        </item>
    </xsl:template>
    
    <!-- suppress all else:-->
    <xsl:template match="*"/>
    
</xsl:stylesheet>
