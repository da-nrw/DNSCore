<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:da="http://www.danrw.de/contract/v1"
>

	<xsl:output
        method="xml"
        indent="yes"
    />
    
    <!-- ROOT-Element -->
    
    <xsl:template match="/da:rightsGranted">
    	<preservationMetadata>
	    	<right>
	    		<xsl:apply-templates/>
	    	</right>
    	</preservationMetadata>
    </xsl:template>
    
    <xsl:template match="da:migrationRight">
    	<migrationRight>
    		<xsl:apply-templates/>
    	</migrationRight>
    </xsl:template>
    
    <xsl:template match="da:condition">
    	<condition>
    		<xsl:apply-templates/>
    	</condition>
    </xsl:template>
    
    <xsl:template match="da:publicationRight">
    	<publicationRight>
    		<xsl:apply-templates select="da:audience|da:startDate"/>
    		<xsl:apply-templates select="da:restrictions/*"/>
    	</publicationRight>
    </xsl:template>
    
    <xsl:template match="da:audience">
    	<audience>
    		<xsl:apply-templates/>
    	</audience>
    </xsl:template>
    
    <xsl:template match="da:startDate">
    	<startDate>
    		<xsl:apply-templates/>
    	</startDate>
    </xsl:template>
    
    <xsl:template match="da:expireDate">
    	<expireDate>
    		<xsl:apply-templates/>
    	</expireDate>
    </xsl:template>
	
	<xsl:template match="da:lawID">
		<lawID>
			<xsl:apply-templates/>
		</lawID>
	</xsl:template>
    
    <xsl:template match="da:restrictImage">
    	<restrictImage>
    		<xsl:apply-templates/>
    	</restrictImage>
    </xsl:template>
    
    <xsl:template match="da:restrictVideo">
    	<restrictVideo>
    		<xsl:apply-templates/>
    	</restrictVideo>
    </xsl:template>
    
    <xsl:template match="da:restrictAudio">
    	<restrictAudio>
    		<xsl:apply-templates/>
    	</restrictAudio>
    </xsl:template>
    
    <xsl:template match="da:restrictText">
    	<restrictText>
    		<xsl:apply-templates/>
    	</restrictText>
    </xsl:template>
    
    <xsl:template match="da:width">
    	<width>
    		<xsl:apply-templates/>
    	</width>
    </xsl:template>
    
    <xsl:template match="da:height">
    	<height>
    		<xsl:apply-templates/>
    	</height>
    </xsl:template>
    
    <xsl:template match="da:footerText">
    	<footerText>
    		<xsl:apply-templates/>
    	</footerText>
    </xsl:template>
    
    <xsl:template match="da:duration">
    	<duration>
    		<xsl:apply-templates/>
    	</duration>
    </xsl:template>
    
    <xsl:template match="da:pages">
    	<pages>
    		<xsl:apply-templates/>
    	</pages>
    </xsl:template>
	
	<xsl:template match="da:certainPages">
		<certainPages>
			<xsl:apply-templates/>
		</certainPages>
	</xsl:template>

</xsl:stylesheet>
