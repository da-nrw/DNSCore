<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
    xmlns:lido="http://www.lido-schema.org"
    exclude-result-prefixes="lido">
    
    <xsl:output method="xml" indent="yes"/>
    
    <xsl:template match="/">
        <oai_dc:dc xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
            <xsl:apply-templates />
        </oai_dc:dc>
    </xsl:template>
    
    <xsl:template match="lido:lido">
        <dc:title><xsl:value-of select="lido:descriptiveMetadata/lido:objectIdentificationWrap/lido:titleWrap/lido:titleSet/lido:appellationValue"/></dc:title>
        <dc:identifier><xsl:value-of select="lido:lidoRecID"/></dc:identifier>
        <dc:type><xsl:value-of select="lido:category/lido:term"/></dc:type>
        <!-- format is now used to store the package type -->
        <!--
        <dc:format><xsl:value-of select="lido:descriptiveMetadata/lido:objectIdentificationWrap/lido:objectMeasurementsWrap/lido:objectMeasurementsSet/lido:displayObjectMeasurements"/></dc:format>
        -->
        <xsl:for-each select="lido:descriptiveMetadata/lido:objectClassificationWrap/lido:classificationWrap/lido:classification/lido:term">
            <dc:subject><xsl:value-of select="normalize-space(.)"/></dc:subject>
        </xsl:for-each>
        <dc:description><xsl:value-of select="lido:descriptiveMetadata/lido:objectIdentificationWrap/lido:objectDescriptionWrap/lido:objectDescriptionSet/lido:descriptiveNoteValue"/></dc:description>
        <dc:rights><xsl:value-of select="lido:administrativeMetadata/lido:rightsWorkWrap/lido:rightsWorkSet/lido:rightsHolder/lido:legalBodyName/lido:appellationValue"/></dc:rights>
    </xsl:template>

</xsl:stylesheet>
