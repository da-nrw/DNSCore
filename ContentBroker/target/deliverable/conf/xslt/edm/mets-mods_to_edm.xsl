<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:mets="http://www.loc.gov/METS/"
	xmlns:mods="http://www.loc.gov/mods/v3"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dcterms="http://purl.org/dc/terms/"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:ore="http://www.openarchives.org/ore/terms/"
	xmlns:edm="http://www.europeana.eu/schemas/edm/"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:owl="http://www.w3.org/2002/07/owl#"
	xmlns:dv="http://dfg-viewer.de/"
	exclude-result-prefixes="mods mets xlink">

	<xsl:output method="xml" indent="yes"/>
	<xsl:strip-space elements="mods:*"/>
	
	<xsl:param name="urn"/>    
	<xsl:param name="cho-base-uri"/>
	<xsl:param name="aggr-base-uri"/>
	<xsl:param name="provider"/>
	<xsl:param name="local-base-uri"/>
	
	<xsl:variable name="data-provider" select="/mets:mets/mets:amdSec[mets:rightsMD/mets:mdWrap/@OTHERMDTYPE='DVRIGHTS']/mets:rightsMD/mets:mdWrap/mets:xmlData/dv:rights/dv:owner"/>
	
	<xsl:template match="/">
		<rdf:RDF>
			<xsl:apply-templates select="mets:mets/mets:structMap[@TYPE='LOGICAL']/mets:div"/>
		</rdf:RDF>
	</xsl:template>
	
	<!-- generate CHOs and aggregations for divs with referenced dmdSec -->
	<xsl:template match="mets:div[@DMDID]">
		
		<xsl:param name="parent-id"/>
		
		<xsl:variable name="cho-id">
			<xsl:value-of select="$cho-base-uri"/>-<xsl:value-of select="@DMDID"/>
		</xsl:variable>
		<xsl:variable name="aggr-id">
			<xsl:value-of select="$aggr-base-uri"/>-<xsl:value-of select="@DMDID"/>
		</xsl:variable>
		
		<xsl:variable name="dmd-id" select="@DMDID"/>
		<xsl:apply-templates select="//mets:dmdSec[@ID=$dmd-id]">
			<xsl:with-param name="cho-id" select="$cho-id"/>
			<xsl:with-param name="parent-id" select="$parent-id"/>
		</xsl:apply-templates>
		
		<xsl:call-template name="aggregationFromDiv">
			<xsl:with-param name="cho-id" select="$cho-id"/>
			<xsl:with-param name="aggr-id" select="$aggr-id"/>
		</xsl:call-template>
		
		<xsl:apply-templates select="mets:div">
			<xsl:with-param name="parent-id" select="$cho-id"/>
		</xsl:apply-templates>	
		
	</xsl:template>
	
	<!-- just recurse into subdivs if no dmdSec is referenced -->
	<xsl:template match="mets:div">		
		<xsl:param name="parent-id"/>
		<xsl:apply-templates select="mets:div">
			<xsl:with-param name="parent-id" select="$parent-id"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<xsl:template match="mets:dmdSec">
		
		<xsl:param name="parent-id"/>
		<xsl:param name="cho-id"/>
		
		<xsl:call-template name="choFromDmdSec">
			<xsl:with-param name="cho-id" select="$cho-id"/>
			<xsl:with-param name="parent-id" select="$parent-id"/>
		</xsl:call-template>
		
	</xsl:template>
	
	<xsl:template name="choFromDmdSec">
		
		<xsl:param name="cho-id"/>
		<xsl:param name="parent-id"/>
		
		<!-- CHO -->
		<edm:ProvidedCHO rdf:about="{$cho-id}">
			<owl:sameAs rdf:resource="{$urn}-{@ID}"/>
			<dc:identifier><xsl:value-of select="$urn"/>-<xsl:value-of select="@ID"/></dc:identifier>
			<dc:identifier><xsl:value-of select="$cho-id"/></dc:identifier>
			
			<xsl:apply-templates select="mets:mdWrap/mets:xmlData/mods:mods/*"></xsl:apply-templates>
			
			<xsl:if test="$parent-id">
				<dcterms:isPartOf rdf:resource="{$parent-id}"/>
			</xsl:if>
			
		</edm:ProvidedCHO>
		
	</xsl:template>
	
	<xsl:template name="aggregationFromDiv">
		
		<xsl:param name="cho-id"/>
		<xsl:param name="aggr-id"/>
		
		<ore:Aggregation rdf:about="{$aggr-id}">
			
			<edm:aggregatedCHO rdf:resource="{$cho-id}" />
			
			<edm:dataProvider><xsl:value-of select="$data-provider"/></edm:dataProvider>
			<edm:provider><xsl:value-of select="$provider"/></edm:provider>
			<edm:isShownAt rdf:resource="{$aggr-id}" />
			
			<xsl:choose>
				<xsl:when test="descendant-or-self::mets:div[@TYPE='TitlePage']">
					<xsl:apply-templates select="descendant-or-self::mets:div[@TYPE='TitlePage']" mode="title-page"/>
				</xsl:when>
				<xsl:when test="descendant-or-self::mets:div[@TYPE='title_page']">
					<xsl:apply-templates select="descendant-or-self::mets:div[@TYPE='title_page']" mode="title-page"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="." mode="title-page"/>
				</xsl:otherwise>
			</xsl:choose>
			
		</ore:Aggregation>
		
	</xsl:template>
	
	<xsl:template match="mets:div" mode="title-page">
		<xsl:variable name="log-id" select="@ID"/>
		<xsl:apply-templates select="/mets:mets/mets:structLink/mets:smLink[@xlink:from=$log-id][1]" mode="title-page"/>
	</xsl:template>
	
	<xsl:template match="mets:smLink" mode="title-page">
		<xsl:variable name="phys-id" select="@xlink:to"/>
		<xsl:apply-templates select="/mets:mets/mets:structMap[@TYPE='PHYSICAL']//mets:div[@ID=$phys-id]" mode="title-page-physical"/>
	</xsl:template>
	
	<xsl:template match="mets:div" mode="title-page-physical">
		<xsl:choose>
			<xsl:when test="mets:fptr">
				<xsl:variable name="file-id" select="mets:fptr/@FILEID"/>
				<xsl:choose>
					<xsl:when test="/mets:mets/mets:fileSec/mets:fileGrp/@USE='THUMBS'">
						<edm:object rdf:resource="{/mets:mets/mets:fileSec/mets:fileGrp[@USE='THUMBS']/mets:file[@ID=$file-id]/mets:FLocat/@xlink:href}"/>
					</xsl:when>
					<xsl:otherwise>
						<edm:object rdf:resource="{/mets:mets/mets:fileSec/mets:fileGrp/mets:file[@ID=$file-id]/mets:FLocat/@xlink:href}"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="mets:div" mode="title-page"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	
	<!-- MODS to DC transformations -->
	
	<xsl:template match="mods:titleInfo[@type='alternative']">
		<dcterms:alternative>
			<xsl:call-template name="titleFromTitleInfo">
				<xsl:with-param name="titleInfo" select="."/>
			</xsl:call-template>
		</dcterms:alternative>
	</xsl:template>
	
	<xsl:template match="mods:titleInfo">
		<dc:title>
			<xsl:call-template name="titleFromTitleInfo">
				<xsl:with-param name="titleInfo" select="."/>
			</xsl:call-template>
		</dc:title>
	</xsl:template>
	
	<xsl:template name="titleFromTitleInfo">
		<xsl:value-of select="mods:nonSort"/>
		<xsl:if test="mods:nonSort">
			<xsl:text> </xsl:text>
		</xsl:if>
		<xsl:value-of select="mods:title"/>
		<xsl:if test="mods:subTitle">
			<xsl:text>: </xsl:text>
			<xsl:value-of select="mods:subTitle"/>
		</xsl:if>
		<xsl:if test="mods:partNumber">
			<xsl:text>. </xsl:text>
			<xsl:value-of select="mods:partNumber"/>
		</xsl:if>
		<xsl:if test="mods:partName">
			<xsl:text>. </xsl:text>
			<xsl:value-of select="mods:partName"/>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="mods:part">
		<xsl:if test="mods:detail[mods:number]">
			<dc:title><xsl:value-of select="mods:detail/mods:number"/></dc:title>
			<xsl:if test="mods:detail/@type">
				<dc:type><xsl:value-of select="mods:detail/@type"/></dc:type>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<xsl:template match="mods:name">
		<xsl:choose>
			<xsl:when
				test="mods:role/mods:roleTerm[@type='text']='creator' or mods:role/mods:roleTerm[@type='code']='cre' or mods:role/mods:roleTerm[@type='code']='aut'">
				<dc:creator>
					<xsl:call-template name="name"/>
				</dc:creator>
			</xsl:when>

			<xsl:otherwise>
				<dc:contributor>
					<xsl:call-template name="name"/>
				</dc:contributor>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="mods:classification">
		<dc:subject>
			<xsl:value-of select="."/>
		</dc:subject>
	</xsl:template>

	<xsl:template match="mods:subject[mods:topic | mods:name | mods:occupation | mods:geographic | mods:hierarchicalGeographic | mods:cartographics | mods:temporal] ">
		
		<xsl:for-each select="mods:topic | mods:occupation">
			<dc:subject><xsl:value-of select="."/></dc:subject>
		</xsl:for-each>
		
		<xsl:for-each select="mods:name">
			<dc:subject><xsl:call-template name="name"/></dc:subject>
		</xsl:for-each>

		<xsl:for-each select="mods:titleInfo/mods:title">
			<dc:subject><xsl:value-of select="mods:titleInfo/mods:title"/></dc:subject>
		</xsl:for-each>

		<xsl:for-each select="mods:geographic">
			<dcterms:spatial><xsl:value-of select="."/></dcterms:spatial>
		</xsl:for-each>

		<xsl:for-each select="mods:hierarchicalGeographic">
			<dcterms:spatial>
				<xsl:for-each
					select="mods:continent|mods:country|mods:provence|mods:region|mods:state|mods:territory|mods:county|mods:city|mods:island|mods:area">
					<xsl:value-of select="."/>
					<xsl:if test="position()!=last()">--</xsl:if>
				</xsl:for-each>
			</dcterms:spatial>
		</xsl:for-each>

		<xsl:for-each select="mods:cartographics/*">
			<dcterms:spatial>
				<xsl:value-of select="."/>
			</dcterms:spatial>
		</xsl:for-each>

		<xsl:if test="mods:temporal">
			<dcterms:temporal>
				<xsl:for-each select="mods:temporal">
					<xsl:value-of select="."/>
					<xsl:if test="position()!=last()">-</xsl:if>
				</xsl:for-each>
			</dcterms:temporal>
		</xsl:if>
		
	</xsl:template>

	<xsl:template match="mods:abstract | mods:tableOfContents | mods:note">
		<dc:description>
			<xsl:value-of select="."/>
		</dc:description>
	</xsl:template>

	<xsl:template match="mods:originInfo[mods:publisher]">
		<xsl:apply-templates select="mods:dateIssued | mods:dateCreated | mods:dateCaptured | mods:dateOther"/>
		<dc:publisher>
			<xsl:value-of select="mods:publisher"/><xsl:if test="mods:place"> (<xsl:value-of select="mods:place/mods:placeTerm"/>)</xsl:if>
			<xsl:if test="mods:edition"><xsl:text> </xsl:text><xsl:value-of select="mods:edition"/></xsl:if>			
		</dc:publisher>
	</xsl:template>
	
	<xsl:template match="mods:dateIssued[@encoding='w3cdtf' and @point='start']">
		<dcterms:issued>
			<xsl:value-of select="."/>/<xsl:value-of select="../mods:dateIssued[@point='end']"/>
		</dcterms:issued>
	</xsl:template>
	
	<xsl:template match="mods:dateIssued[@encoding='w3cdtf' and not(@point)]">
		<dcterms:issued>
			<xsl:value-of select="."/>
		</dcterms:issued>
	</xsl:template>
	
	<xsl:template match="mods:dateCreated[@encoding='w3cdtf' and @point='start']">
		<dcterms:created>
			<xsl:value-of select="."/>/<xsl:value-of select="../mods:dateCreated[@point='end']"/>
		</dcterms:created>
	</xsl:template>
	
	<xsl:template match="mods:dateCreated[@encoding='w3cdtf' and not(@point)]">
		<dcterms:created>
			<xsl:value-of select="."/>
		</dcterms:created>
	</xsl:template>

	<xsl:template match="mods:dateCaptured[@point='start'] | mods:dateOther[@point='start'] ">
		<xsl:variable name="dateName" select="local-name()"/>
		<dc:date>
			<xsl:value-of select="."/>-<xsl:value-of select="../*[local-name()=$dateName][@point='end']"/>
		</dc:date>
	</xsl:template>
	
	<xsl:template match="mods:dateIssued[not(@point) and @encoding!='w3cdtf'] | mods:dateCreated[not(@point)] | mods:dateCaptured[not(@point)] | mods:dateOther[not(@point)]">
		<dc:date>
			<xsl:value-of select="."/>
		</dc:date>
	</xsl:template>
	
	<xsl:template match="mods:genre">
		<dc:type>
			<xsl:value-of select="."/>
		</dc:type>
	</xsl:template>

	<xsl:template match="mods:typeOfResource">
		<xsl:if test="@collection='yes'">
			<dc:type>collection</dc:type>
		</xsl:if>
		<xsl:if test=".='software'">
			<dc:type>software</dc:type>
		</xsl:if>
		<xsl:if test=".='cartographic material'">
			<dc:type>cartographic material</dc:type>
		</xsl:if>
		<xsl:if test=".='multimedia'">
			<dc:type>multimedia</dc:type>
		</xsl:if>
		<xsl:if test=".='moving image'">
			<edm:type>VIDEO</edm:type>
		</xsl:if>
		<xsl:if test=".='three-dimensional object'">
			<edm:type>3D</edm:type>
		</xsl:if>
		<xsl:if test="starts-with(.,'sound recording')">
			<edm:type>SOUND</edm:type>
		</xsl:if>
		<xsl:if test=".='still image'">
			<edm:type>IMAGE</edm:type>
		</xsl:if>
		<xsl:if test=". ='text'">
			<edm:type>TEXT</edm:type>
		</xsl:if>
		<xsl:if test=".='notated music'">
			<edm:type>TEXT</edm:type>
		</xsl:if>
	</xsl:template>

	<xsl:template match="mods:physicalDescription">
		<xsl:if test="mods:extent">
			<dcterms:extent>
				<xsl:value-of select="mods:extent"/>
			</dcterms:extent>
		</xsl:if>
		<xsl:if test="mods:form">
			<dc:format>
				<xsl:value-of select="mods:form"/>
			</dc:format>
		</xsl:if>
		<xsl:if test="mods:internetMediaType">
			<dc:format>
				<xsl:value-of select="mods:internetMediaType"/>
			</dc:format>
		</xsl:if>
	</xsl:template>

	<xsl:template match="mods:mimeType">
		<dc:format>
			<xsl:value-of select="."/>
		</dc:format>
	</xsl:template>

	<xsl:template match="mods:identifier">
		<xsl:variable name="type" select="translate(@type,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/>
		<xsl:choose>
			<xsl:when test="contains ('isbn issn uri doi lccn uri', $type)">
				<dc:identifier>
					<xsl:value-of select="$type"/>: <xsl:value-of select="."/>
				</dc:identifier>
			</xsl:when>
			<xsl:otherwise>
				<dc:identifier>
					<xsl:value-of select="."/>
				</dc:identifier>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:choose>
			<xsl:when test="contains('uri urn', $type)">
				<owl:sameAs rdf:resource="{.}"/>
			</xsl:when>
			<xsl:otherwise>
				<owl:sameAs rdf:resource="{$local-base-uri}{$type}/{.}"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="mods:location[child::mods:url]">
		<dc:identifier>
			<xsl:for-each select="mods:url">
				<xsl:value-of select="."/>
			</xsl:for-each>
		</dc:identifier>
	</xsl:template>

	<xsl:template match="mods:language">
		<dc:language>
			<xsl:value-of select="normalize-space(.)"/>
		</dc:language>
	</xsl:template>

	<xsl:template match="mods:relatedItem">
		<xsl:choose>
			<xsl:when test="@type='original' and mods:recordInfo/mods:recordIdentifier[@source]">
				<dcterms:isVersionOf rdf:resource="{$local-base-uri}{mods:recordInfo/mods:recordIdentifier/@source}/{mods:recordInfo/mods:recordIdentifier}"/>				
			</xsl:when>
			<xsl:when test="@type='series'"/>
			<xsl:when test="@type='preceding' and mods:recordInfo/mods:recordIdentifier[@source]">
				<edm:isNextInSequence rdf:resource="{$local-base-uri}{mods:recordInfo/mods:recordIdentifier/@source}/{mods:recordInfo/mods:recordIdentifier}"/>				
			</xsl:when>
			<xsl:when test="@type='host' and mods:recordInfo/mods:recordIdentifier[@source]">
				<dcterms:isPartOf rdf:resource="{$local-base-uri}{mods:recordInfo/mods:recordIdentifier/@source}/{mods:recordInfo/mods:recordIdentifier}"/>				
			</xsl:when>
			<xsl:otherwise>
				<dc:relation>
					<xsl:for-each
						select="mods:titleInfo/mods:title | mods:identifier | mods:location/mods:url">
						<xsl:if test="normalize-space(.)!= ''">
							<xsl:value-of select="."/>
							<xsl:if test="position()!=last()">--</xsl:if>
						</xsl:if>
					</xsl:for-each>
				</dc:relation>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="mods:accessCondition">
		<dc:rights>
			<xsl:value-of select="."/>
		</dc:rights>
	</xsl:template>

	<xsl:template name="name">
		<xsl:variable name="name">
			<xsl:for-each select="mods:namePart[not(@type)]">
				<xsl:value-of select="."/>
				<xsl:text> </xsl:text>
			</xsl:for-each>
			<xsl:value-of select="mods:namePart[@type='family']"/>
			<xsl:if test="mods:namePart[@type='given']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select="mods:namePart[@type='given']"/>
			</xsl:if>
			<xsl:if test="mods:namePart[@type='date']">
				<xsl:text>, </xsl:text>
				<xsl:value-of select="mods:namePart[@type='date']"/>
				<xsl:text/>
			</xsl:if>
			<xsl:if test="mods:displayForm">
				<xsl:text> (</xsl:text>
				<xsl:value-of select="mods:displayForm"/>
				<xsl:text>) </xsl:text>
			</xsl:if>
			<xsl:for-each select="mods:role[mods:roleTerm[@type='text']!='creator']">
				<xsl:text> (</xsl:text>
				<xsl:value-of select="normalize-space(.)"/>
				<xsl:text>) </xsl:text>
			</xsl:for-each>
		</xsl:variable>
		<xsl:value-of select="normalize-space($name)"/>
	</xsl:template>
	
	<xsl:template match="mods:temporal[@point='start']  ">
		<xsl:value-of select="."/>-<xsl:value-of select="../mods:temporal[@point='end']"/>
	</xsl:template>
	
	<xsl:template match="mods:temporal[@point!='start' and @point!='end']  ">
		<xsl:value-of select="."/>
	</xsl:template>
	
	<!-- suppress all else:-->
	<xsl:template match="*"/>
		

	
</xsl:stylesheet>
