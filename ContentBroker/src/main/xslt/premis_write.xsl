<?xml version="1.0" encoding="iso-8859-1"?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:premis="info:lc/xmlns/premis-v2"
    xmlns="info:lc/xmlns/premis-v2"
>
 
    <xsl:output
        method="xml"
        indent="yes"
    />
 
 	<!-- ROOT-Element -->
    <xsl:template match="preservationMetadata">    	
    	<premis version="2.1" xsi:schemaLocation="info:lc/xmlns/premis-v2 http://www.loc.gov/standards/premis/v2/premis-v2-1.xsd">
            <xsl:apply-templates select="object"/>
            <xsl:apply-templates select="event"/>
            <xsl:apply-templates select="agent"/>
            <rights>
            	<xsl:apply-templates select="right"/>
            </rights>
        </premis>        
    </xsl:template>
    
    <!-- Objects -->
    <xsl:template match="preservationMetadata/object">
    	<object xsi:type="representation">
    		<xsl:apply-templates select="id"/>
    		<xsl:apply-templates select="originalName"/>
    		<xsl:apply-templates select="event"/>
    		<xsl:apply-templates select="right"/>
    	</object>
    </xsl:template>
    
    <xsl:template match="object/id">
    	<objectIdentifier>
    		<objectIdentifierType>urn</objectIdentifierType>
    		<objectIdentifierValue><xsl:apply-templates/></objectIdentifierValue>
    	</objectIdentifier>
    </xsl:template>
    
    <xsl:template match="object/originalName">
    	<originalName>
    		<xsl:apply-templates/>
    	</originalName>
    </xsl:template>
 	
 	<xsl:template match="object/event">
    	<linkingEventIdentifier>
    		<linkingEventIdentifierType>da-nrw-event-id</linkingEventIdentifierType>
    		<linkingEventIdentifierValue><xsl:apply-templates/></linkingEventIdentifierValue>
    	</linkingEventIdentifier> 
 	</xsl:template>
 	
 	<xsl:template match="object/right">
    	<linkingRightsStatementIdentifier>
    		<linkingRightsStatementIdentifierType>da-nrw-rights-id</linkingRightsStatementIdentifierType>
    		<linkingRightsStatementIdentifierValue><xsl:apply-templates/></linkingRightsStatementIdentifierValue>
    	</linkingRightsStatementIdentifier> 
 	</xsl:template>
    
    <!-- Events -->
    <xsl:template match="preservationMetadata/event">
    	<event>
    		<xsl:apply-templates select="id"/>
    		<xsl:apply-templates select="type"/>
    		<xsl:apply-templates select="date"/>
    		<xsl:apply-templates select="outcome"/>
    		<xsl:apply-templates select="object"/>
    	</event>
    </xsl:template>
    
    <xsl:template match="event/id">
    	<eventIdentifier>
    		<eventIdentifierType>da-nrw-event-id</eventIdentifierType>
    		<eventIdentifierValue><xsl:apply-templates/></eventIdentifierValue>
    	</eventIdentifier> 
 	</xsl:template>
 	
 	<xsl:template match="event/date">
    	<eventDateTime>
    		<xsl:apply-templates/>
    	</eventDateTime> 
 	</xsl:template>
 	
 	<xsl:template match="event/type">
    	<eventType>
    		<xsl:apply-templates/>
    	</eventType> 
 	</xsl:template>
 	
 	<xsl:template match="event/outcome">
    	<eventOutcomeInformation>
    		<eventOutcome><xsl:apply-templates/></eventOutcome>
    	</eventOutcomeInformation> 
 	</xsl:template>
 	
 	<xsl:template match="event/object">
    	<linkingObjectIdentifier>
    		<linkingObjectIdentifierType>urn</linkingObjectIdentifierType>
    		<linkingObjectIdentifierValue><xsl:apply-templates/></linkingObjectIdentifierValue>
    	</linkingObjectIdentifier> 
 	</xsl:template>
 	
 	<!-- Agents -->
 	<xsl:template match="preservationMetadata/agent">
    	<agent>
    		<xsl:apply-templates select="id"/>
    		<xsl:apply-templates select="name"/>
    		<xsl:apply-templates select="type"/>
    	</agent>
    </xsl:template>
    
    <xsl:template match="agent/id">
    	<agentIdentifier>
    		<agentIdentifierType>da-nrw-agent-id</agentIdentifierType>
    		<agentIdentifierValue><xsl:apply-templates/></agentIdentifierValue>
    	</agentIdentifier> 
 	</xsl:template>
 	
 	<xsl:template match="agent/name">
    	<agentName><xsl:apply-templates/></agentName>
 	</xsl:template>
 	
 	<xsl:template match="agent/type">
    	<agentType><xsl:apply-templates/></agentType>
 	</xsl:template>
    
 	<!-- Rights -->
 	<xsl:template match="preservationMetadata/right">
    	<rightsStatement>    	
    		<xsl:apply-templates select="id" mode="premis"/>
    		<rightsBasis>license</rightsBasis>
    		<xsl:apply-templates select="publicationRight" mode="premis"/>
    		<xsl:apply-templates select="migrationRight" mode="premis"/>
    	</rightsStatement>
    	<rightsExtension>
    		<rightsGranted xmlns="http://www.danrw.de/contract/v1">
    			<xsl:apply-templates mode="danrw-contract"/>
    		</rightsGranted>
    	</rightsExtension>
    </xsl:template>
 	
 	<xsl:template match="right/id" mode="premis">
    	<rightsStatementIdentifier>
    		<rightsStatementIdentifierType>da-nrw-rights-id</rightsStatementIdentifierType>
    		<rightsStatementIdentifierValue><xsl:apply-templates/></rightsStatementIdentifierValue>
    	</rightsStatementIdentifier> 
 	</xsl:template>
 	
 	<xsl:template match="right/id" mode="danrw-contract">
 	</xsl:template>
 	
 	<xsl:template match="right/publicationRight" mode="premis">
    	<rightsGranted>
    		<act>PUBLICATION</act>
    		<restriction>See rightsExtension</restriction>
    		<termOfGrant>
    			<startDate><xsl:value-of select="startDate"/></startDate>
    		</termOfGrant>
    	</rightsGranted> 
 	</xsl:template>
 	
 	<xsl:template match="right/publicationRight" mode="danrw-contract">
    	<publicationRight xmlns="http://www.danrw.de/contract/v1">
    		<xsl:apply-templates select="audience|startDate" mode="danrw-contract"/>
    		<restrictions>
    			<xsl:apply-templates select="lawID|expireDate|restrictImage|restrictAudio|restrictVideo|restrictText" mode="danrw-contract"/>
    		</restrictions>
    	</publicationRight> 
 	</xsl:template>
 	
 	<xsl:template match="right/migrationRight" mode="premis">
    	<rightsGranted>
    		<act>MIGRATION</act>
    		<restriction>See rightsExtension</restriction>
    		<termOfGrant>
    			<startDate>2011</startDate>
    		</termOfGrant>
    	</rightsGranted> 
 	</xsl:template>
 	
 	<xsl:template match="right/migrationRight" mode="danrw-contract">
    	<migrationRight xmlns="http://www.danrw.de/contract/v1">
    		<xsl:apply-templates mode="danrw-contract"/>
    	</migrationRight> 
 	</xsl:template>
 
    <xsl:template match="node()|@*" mode="danrw-contract">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*" mode="danrw-contract"/>
        </xsl:copy>
    </xsl:template>
    
</xsl:stylesheet>
