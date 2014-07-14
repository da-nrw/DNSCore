<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:premis="info:lc/xmlns/premis-v2"
    xmlns:da="http://www.danrw.de/contract/v1"
>

	<xsl:output
        method="xml"
        indent="yes"
    />
    
    <!-- ROOT-Element -->
    <xsl:template match="premis:premis">    	
    	<preservationMetadata>
            <xsl:apply-templates />
        </preservationMetadata>        
    </xsl:template>
    
    <!-- Objects -->
    <xsl:template match="premis:object">
    	<object>    		
    		<id><xsl:value-of select="premis:objectIdentifier/premis:objectIdentifierValue"/></id>
    		<originalName><xsl:value-of select="premis:originalName"/></originalName>
    		<xsl:for-each select="premis:linkingEventIdentifier">
    			<event><xsl:value-of select="premis:linkingEventIdentifierValue"/></event>
    		</xsl:for-each>
    		<xsl:for-each select="premis:linkingRightsStatementIdentifier">
    			<right><xsl:value-of select="premis:linkingRightsStatementIdentifierValue"/></right>
    		</xsl:for-each>
    	</object>
    </xsl:template>
    
    <!-- Events -->
    <xsl:template match="premis:event">
    	<event>    		
    		<id><xsl:value-of select="premis:eventIdentifier/premis:eventIdentifierValue"/></id>
    		<type><xsl:value-of select="premis:eventType"/></type>
    		<date><xsl:value-of select="premis:eventDateTime"/></date>
    		<xsl:for-each select="premis:linkingObjectIdentifier">
    			<object><xsl:value-of select="premis:linkingObjectIdentifierValue"/></object>
    		</xsl:for-each>
    		<outcome><xsl:value-of select="premis:eventOutcomeInformation/premis:eventOutcome"/></outcome>
    	</event>
    </xsl:template>
    
    <!-- Agents -->
    <xsl:template match="premis:agent">
    	<agent>    		
    		<id><xsl:value-of select="premis:agentIdentifier/premis:agentIdentifierValue"/></id>
    		<name><xsl:value-of select="premis:agentName"/></name>
    		<type><xsl:value-of select="premis:agentType"/></type>
    	</agent>
    </xsl:template>
    
    <!-- Rights (Custom Extension) -->
    <xsl:template match="premis:rights">
    	<xsl:apply-templates select="premis:rightsExtension/da:rightsGranted"/>
    </xsl:template>
    
    <xsl:template match="da:rightsGranted">
    	<right>
    		<xsl:apply-templates/>
    	</right>
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
	
	<xsl:template match="*"/>

</xsl:stylesheet>
