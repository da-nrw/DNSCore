<?xml version="1.0" encoding="UTF-8"?>
<!--
Based on:
MIMO - Musical Instrument Museum Online
MIMO-LIDO to EDM Mapping - 08-08-2011 - v0.66
Technical Contact : Rodolphe Bailly - rbailly@cite-musique.fr
-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ns0="http://www.lido-schema.org" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xml="http://www.w3.org/XML/1998/namespace" exclude-result-prefixes="ns0 xs fn">
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dcterms="http://purl.org/dc/terms/" xmlns:edm="http://www.europeana.eu/schemas/edm/" xmlns:enrichment="http://www.europeana.eu/schemas/edm/enrichment/" xmlns:owl="http://www.w3.org/2002/07/owl#" xmlns:wgs84="http://www.w3.org/2003/01/geo/wgs84_pos#" xmlns:skos="http://www.w3.org/2004/02/skos/core#" xmlns:ore="http://www.openarchives.org/ore/terms/">
			<xsl:attribute name="xsi:schemaLocation" namespace="http://www.w3.org/2001/XMLSchema-instance" select="'http://www.w3.org/1999/02/22-rdf-syntax-ns# EDM.xsd'"/>
			<xsl:for-each select="ns0:lidoWrap/ns0:lido">
				<xsl:variable name="var1_lidoRecID" as="node()+" select="ns0:lidoRecID"/>
				<xsl:variable name="var2_administrativeMetadata" as="node()+" select="ns0:administrativeMetadata"/>
				<xsl:variable name="var3_descriptiveMetadata" as="node()+" select="ns0:descriptiveMetadata"/>
				
				<xsl:variable name="var55_result" as="xs:boolean+">
					<xsl:for-each select="$var2_administrativeMetadata">
						<xsl:variable name="var44_result" as="xs:boolean*">
							<xsl:for-each select="ns0:recordWrap/ns0:recordInfoSet">
								<xsl:sequence select="fn:exists(ns0:recordInfoLink)"/>
							</xsl:for-each>
						</xsl:variable>
						<xsl:sequence select="fn:exists($var44_result[.])"/>
					</xsl:for-each>
				</xsl:variable>
				<xsl:variable name="var6_resultof_any" as="xs:boolean" select="fn:exists($var55_result[.])"/>
				
				<xsl:variable name="var_isShownByAssignedByPrefImage_temp1" as="xs:boolean+">
					<xsl:for-each select="$var2_administrativeMetadata">
						<xsl:variable name="var_isShownByAssignedByPrefImage_temp2" as="xs:boolean*">
							<xsl:for-each select="ns0:resourceWrap/ns0:resourceSet">							
								<xsl:sequence select="fn:exists (ns0:resourceID/@ns0:pref)"/>							
							</xsl:for-each>
						</xsl:variable>
						<xsl:sequence select = "fn:exists($var_isShownByAssignedByPrefImage_temp2[.])"/>
					</xsl:for-each>				
				</xsl:variable>
				<xsl:variable name="var_isShownByAssignedByPrefImage" as="xs:boolean" select="fn:exists($var_isShownByAssignedByPrefImage_temp1[.])"/>

				<edm:ProvidedCHO>
					<xsl:for-each select="$var1_lidoRecID">
						<xsl:attribute name="rdf:about" select="xs:string(xs:anyURI(fn:concat('#', fn:string(.))))"/>
					</xsl:for-each>
					<dc:coverage>
						<xsl:for-each select="$var3_descriptiveMetadata/ns0:eventWrap/ns0:eventSet/ns0:event/ns0:eventPlace/ns0:place/ns0:placeID">
							<xsl:attribute name="rdf:resource" select="fn:concat(fn:concat('http://sws.', fn:substring-after(fn:string(.), 'http://www.')), '/')"/>
						</xsl:for-each>
					</dc:coverage>
						<xsl:for-each select="$var3_descriptiveMetadata/ns0:eventWrap/ns0:eventSet/ns0:event">
							<xsl:variable name="var6_cur" as="node()" select="."/>
							<xsl:for-each select="ns0:eventActor/ns0:actorInRole/ns0:actor/ns0:actorID">
								<xsl:variable name="var5_cur" as="node()" select="."/>
								<xsl:variable name="var4_result" as="xs:boolean*">
									<xsl:for-each select="$var6_cur/ns0:eventType/ns0:term">
										<xsl:sequence select="((fn:string($var5_cur/@ns0:type) = 'URI') and (fn:contains(fn:string(.), 'production') or fn:contains(fn:string(.), 'Production')))"/>
									</xsl:for-each>
								</xsl:variable>
								<xsl:if test="fn:exists($var4_result[.])">
								<dc:creator>
									<xsl:attribute name="rdf:resource" select="fn:string(.)"/>
								</dc:creator>	
								</xsl:if>
							</xsl:for-each>
							<xsl:for-each select="ns0:eventActor/ns0:actorInRole/ns0:actor/ns0:nameActorSet/ns0:appellationValue">
								<xsl:variable name="appVal" as="node()" select="."/>
								<xsl:variable name="var4_result" as="xs:boolean*">
									<xsl:for-each select="$var6_cur/ns0:eventType/ns0:term">
										<xsl:sequence select="(fn:contains(fn:string(.), 'production') or fn:contains(fn:string(.), 'Production'))"/>
									</xsl:for-each>
								</xsl:variable>
								<xsl:if test="((fn:exists($var4_result[.])) and (fn:exists($appVal[.])) and not(fn:string(.) ='') and (@ns0:label='Name'))">
								<dc:creator>
									<xsl:sequence select="fn:string(.)"/>
								</dc:creator>	
								</xsl:if>
							</xsl:for-each>
						</xsl:for-each>
					<xsl:for-each select="$var3_descriptiveMetadata/ns0:eventWrap/ns0:eventSet/ns0:event">
						<xsl:variable name="var10_cur" as="node()" select="."/>
						<xsl:for-each select="ns0:eventType/ns0:term">
							<xsl:variable name="var4_resultof_contains" as="xs:boolean" select="fn:contains(fn:string(.), 'production') or fn:contains(fn:string(.), 'Production')"/>
							<xsl:variable name="var9_result" as="xs:boolean">
								<xsl:choose>
									<xsl:when test="$var4_resultof_contains">
										<xsl:variable name="var5_result" as="xs:boolean?">
											<xsl:for-each select="$var10_cur/ns0:eventDate">
												<xsl:sequence select="fn:exists(ns0:displayDate)"/>
											</xsl:for-each>
										</xsl:variable>
										<xsl:sequence select="fn:exists($var5_result[.])"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:sequence select="fn:false()"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							<xsl:if test="$var9_result">
								<dc:date>
									<xsl:choose>
										<xsl:when test="$var4_resultof_contains">
											<xsl:variable name="var6_eventDate" as="node()?" select="$var10_cur/ns0:eventDate"/>
											<xsl:variable name="var7_result" as="xs:boolean?">
												<xsl:for-each select="$var6_eventDate">
													<xsl:sequence select="fn:exists(ns0:displayDate)"/>
												</xsl:for-each>
											</xsl:variable>
											<xsl:if test="fn:exists($var7_result[.])">
												<xsl:variable name="var8_result" as="xs:string*">
													<xsl:for-each select="$var6_eventDate/ns0:displayDate">
														<xsl:sequence select="fn:string(.)"/>
													</xsl:for-each>
												</xsl:variable>
												<xsl:sequence select="xs:string(fn:string-join(for $x in $var8_result return xs:string($x), ' '))"/>
											</xsl:if>
										</xsl:when>
									</xsl:choose>
								</dc:date>
							</xsl:if>
						</xsl:for-each>
					</xsl:for-each>
					<xsl:for-each select="$var3_descriptiveMetadata/ns0:objectIdentificationWrap/ns0:objectDescriptionWrap/ns0:objectDescriptionSet">
						<xsl:variable name="var12_desc" as="node()?" select="ns0:descriptiveNoteValue"/>
						<xsl:variable name="var11_lang" as="node()?" select="ns0:descriptiveNoteValue/@xml:lang"/>
						<xsl:if test="(fn:exists($var12_desc)) and ( not ($var12_desc =''))">						
						<dc:description>
							<xsl:if test="fn:exists($var11_lang)">
								<xsl:attribute name="xml:lang" select="fn:string($var11_lang)"/>
							</xsl:if>
							<xsl:sequence select="fn:string(.)"/>
						</dc:description>
						</xsl:if>
					</xsl:for-each>
					<xsl:for-each select="$var3_descriptiveMetadata/ns0:objectIdentificationWrap/ns0:objectMeasurementsWrap/ns0:objectMeasurementsSet/ns0:displayObjectMeasurements">
						<xsl:variable name="var12_label" as="node()?" select="@ns0:label"/>
						<xsl:if test="fn:exists($var12_label)">
							<xsl:variable name="var13_lang" as="node()?" select="@xml:lang"/>
							<dc:format>
								<xsl:if test="fn:exists($var13_lang)">
									<xsl:attribute name="xml:lang" select="fn:string($var13_lang)"/>
								</xsl:if>
								<xsl:sequence select="fn:string($var12_label)"/>
							</dc:format>
						</xsl:if>
					</xsl:for-each>
					<xsl:for-each select="$var3_descriptiveMetadata/ns0:objectIdentificationWrap/ns0:titleWrap/ns0:titleSet/ns0:appellationValue">
						<xsl:variable name="var213_lang" as="node()?" select="@xml:lang"/>
						<dc:title>
							<xsl:if test="fn:exists($var213_lang)">
								<xsl:attribute name="xml:lang" select="fn:string($var213_lang)"/>
							</xsl:if>
							<xsl:sequence select="fn:string(.)"/>
						</dc:title>
					</xsl:for-each>
					<xsl:for-each select="$var3_descriptiveMetadata/ns0:objectClassificationWrap/ns0:classificationWrap/ns0:classification">
						<xsl:variable name="var19_cur" as="node()" select="."/>
						<xsl:for-each select="ns0:conceptID">
							<xsl:variable name="var14_type" as="node()?" select="$var19_cur/@ns0:type"/>
							<xsl:variable name="var18_result" as="xs:boolean">
								<xsl:choose>
									<xsl:when test="fn:exists($var14_type)">
										<xsl:variable name="var15_resultof_cast" as="xs:string" select="fn:string($var14_type)"/>
										<xsl:sequence select="(('HornbostelSachsClass' = $var15_resultof_cast) or ('InstrumentsKeywordsPivot' = $var15_resultof_cast))"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:sequence select="fn:false()"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							<xsl:if test="$var18_result">
								<xsl:variable name="var16_" as="xs:double" select="xs:double(xs:decimal('0'))"/>
								<xsl:variable name="var17_resultof_cast" as="xs:string" select="fn:string(.)"/>
								<dc:type>
									<xsl:attribute name="rdf:resource" select="$var17_resultof_cast"/>
									<xsl:sequence select="fn:substring($var17_resultof_cast, $var16_, $var16_)"/>
								</dc:type>
							</xsl:if>
						</xsl:for-each>
					</xsl:for-each>
					<edm:type>IMAGE</edm:type>
				</edm:ProvidedCHO>
				<xsl:for-each select="$var2_administrativeMetadata/ns0:resourceWrap/ns0:resourceSet">
					<edm:WebResource>
						<xsl:for-each select="ns0:resourceID">
							<xsl:attribute name="rdf:about" select="xs:string(xs:anyURI(fn:string(.)))"/>
						</xsl:for-each>
						<xsl:for-each select="ns0:rightsResource/ns0:creditLine">
							<xsl:variable name="var20_lang" as="node()?" select="@xml:lang"/>
							<dc:rights>
								<xsl:if test="fn:exists($var20_lang)">
									<xsl:attribute name="xml:lang" select="fn:string($var20_lang)"/>
								</xsl:if>
								<xsl:sequence select="fn:string(.)"/>
							</dc:rights>
						</xsl:for-each>
					</edm:WebResource>
				</xsl:for-each>

			<xsl:if test="$var3_descriptiveMetadata/ns0:eventWrap/ns0:eventSet/ns0:event/ns0:eventActor/ns0:actorInRole/ns0:actor/ns0:actorID[(fn:string(@ns0:type) = 'URI')]">	
				<edm:Agent>
					<xsl:for-each select="$var3_descriptiveMetadata/ns0:eventWrap/ns0:eventSet/ns0:event/ns0:eventActor/ns0:actorInRole/ns0:actor/ns0:actorID[(fn:string(@ns0:type) = 'URI')]">
						<xsl:attribute name="rdf:about" select="xs:string(xs:anyURI(fn:string(.)))"/>
					</xsl:for-each>
					<xsl:for-each select="$var3_descriptiveMetadata/ns0:eventWrap/ns0:eventSet/ns0:event/ns0:eventActor/ns0:actorInRole/ns0:actor/ns0:nameActorSet/ns0:appellationValue">
						<xsl:variable name="var21_label" as="node()?" select="@ns0:label"/>
						<xsl:if test="(fn:exists($var21_label) and ('Fullname' = fn:string($var21_label)))">
							<skos:prefLabel>
								<xsl:sequence select="fn:string(.)"/>
							</skos:prefLabel>
						</xsl:if>
					</xsl:for-each>
				</edm:Agent>
			</xsl:if>	

				<xsl:for-each select="$var3_descriptiveMetadata/ns0:eventWrap/ns0:eventSet/ns0:event/ns0:eventPlace">
					<xsl:variable name="var34_cur" as="node()" select="."/>
					<xsl:for-each select="ns0:place">
						<edm:Place>
							<xsl:for-each select="ns0:placeID">
								<xsl:attribute name="rdf:about" select="xs:string(xs:anyURI(fn:concat(fn:concat('http://sws.', fn:substring-after(fn:string(.), 'http://www.')), '/')))"/>
							</xsl:for-each>
							<xsl:for-each select="ns0:namePlaceSet/ns0:appellationValue">
								<xsl:variable name="var32_lang" as="node()?" select="@xml:lang"/>
								<skos:prefLabel>
									<xsl:if test="fn:exists($var32_lang)">
										<xsl:attribute name="xml:lang" select="fn:string($var32_lang)"/>
									</xsl:if>
									<xsl:sequence select="fn:string(.)"/>
								</skos:prefLabel>
							</xsl:for-each>
							<xsl:for-each select="$var34_cur/ns0:displayPlace">
								<xsl:variable name="var33_lang" as="node()?" select="@xml:lang"/>
								<skos:altLabel>
									<xsl:if test="fn:exists($var33_lang)">
										<xsl:attribute name="xml:lang" select="fn:string($var33_lang)"/>
									</xsl:if>
									<xsl:sequence select="fn:string(.)"/>
								</skos:altLabel>
							</xsl:for-each>
						</edm:Place>
					</xsl:for-each>				
			</xsl:for-each>
			
<xsl:for-each select="$var3_descriptiveMetadata/ns0:objectClassificationWrap/ns0:classificationWrap/ns0:classification">
					<xsl:variable name="var28_cur" as="node()" select="."/>
					<xsl:for-each select="ns0:conceptID">
						<xsl:variable name="var24_type" as="node()?" select="$var28_cur/@ns0:type"/>
						<xsl:variable name="var27_result" as="xs:boolean">
							<xsl:choose>
								<xsl:when test="fn:exists($var24_type)">
									<xsl:variable name="var25_resultof_cast" as="xs:string" select="fn:string($var24_type)"/>
									<xsl:sequence select="(('HornbostelSachsClass' = $var25_resultof_cast) or ('InstrumentsKeywordsPivot' = $var25_resultof_cast))"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:sequence select="fn:false()"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<xsl:if test="$var27_result">
							<skos:Concept>
								<xsl:attribute name="rdf:about" select="xs:string(xs:anyURI(fn:string(.)))"/>
								<xsl:for-each select="$var28_cur/ns0:term">
									<xsl:variable name="var26_lang" as="node()?" select="@xml:lang"/>
									<skos:prefLabel>
										<xsl:if test="fn:exists($var26_lang)">
											<xsl:attribute name="xml:lang" select="fn:string($var26_lang)"/>
										</xsl:if>
										<xsl:sequence select="fn:string(.)"/>
									</skos:prefLabel>
								</xsl:for-each>
							</skos:Concept>
						</xsl:if>
					</xsl:for-each>
				</xsl:for-each>
				<ore:Aggregation>
					<xsl:for-each select="$var1_lidoRecID">
						<xsl:variable name="var29_resultof_cast" as="xs:string" select="fn:string(.)"/>
						<xsl:variable name="var30_result" as="xs:string">
							<xsl:choose>
								<xsl:when test="fn:contains($var29_resultof_cast, ':')">
									<xsl:sequence select="replace($var29_resultof_cast, ':', '/')"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:sequence select="replace($var29_resultof_cast, '-', '/')"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<xsl:attribute name="rdf:about" select="xs:string(xs:anyURI(fn:concat('http://www.mimo-db.eu/', $var30_result)))"/>
					</xsl:for-each>
					<edm:aggregatedCHO>
						<xsl:for-each select="$var1_lidoRecID">
							<xsl:attribute name="rdf:resource" select="xs:string(xs:anyURI(fn:concat('', fn:string(.))))"/>
						</xsl:for-each>
					</edm:aggregatedCHO>
					<xsl:for-each select="$var2_administrativeMetadata/ns0:resourceWrap/ns0:resourceSet/ns0:resourcePresentation">
						<edm:hasView>
							<xsl:for-each select="ns0:linkResource">
								<xsl:attribute name="rdf:resource" select="xs:string(xs:anyURI(fn:string(.)))"/>
							</xsl:for-each>
						</edm:hasView>
					</xsl:for-each>
					<xsl:for-each select="$var2_administrativeMetadata/ns0:recordWrap/ns0:recordSource/ns0:legalBodyName/ns0:appellationValue">
						<xsl:variable name="var31_label" as="node()?" select="@ns0:label"/>
						<xsl:if test="(fn:exists($var31_label) and ('Preferred Name (local language)' = fn:string($var31_label)))">
							<edm:dataProvider>
								<xsl:sequence select="fn:string(.)"/>
							</edm:dataProvider>
						</xsl:if>
					</xsl:for-each>
					<edm:provider>DA-NRW - Digitales Archiv Nordrhein-Westfalen</edm:provider>
					<edm:isShownBy>
						<xsl:for-each select="$var2_administrativeMetadata/ns0:resourceWrap/ns0:resourceSet/ns0:resourceID">
							<xsl:variable name="var6_pref" as="node()?" select="@ns0:pref"/>							
							<xsl:choose>
							<xsl:when test="fn:exists($var6_pref)">
								<xsl:attribute name="rdf:resource">
									<xsl:choose>
										<xsl:when test="fn:contains(fn:string($var6_pref), 'preferred')">
											<xsl:sequence select="fn:string(.)"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:sequence select="''"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
							</xsl:when>	
							<xsl:otherwise>
							<xsl:if test="not($var_isShownByAssignedByPrefImage)">
								<xsl:variable name="var32_resultof_cast" as="xs:string" select="fn:string(.)"/>
									<xsl:if test="(fn:ends-with($var32_resultof_cast, '.jpg') or fn:ends-with($var32_resultof_cast, '.JPG'))">
										<xsl:attribute name="rdf:resource" select="xs:string(xs:anyURI($var32_resultof_cast))"/>
									</xsl:if>
							</xsl:if>
							</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</edm:isShownBy>					
					<xsl:variable name="var45_result" as="xs:boolean">
						<xsl:choose>
							<xsl:when test="$var6_resultof_any">
								<xsl:variable name="var37_result" as="xs:boolean+">
									<xsl:for-each select="$var2_administrativeMetadata">
										<xsl:variable name="var36_result" as="xs:boolean*">
											<xsl:for-each select="ns0:recordWrap/ns0:recordInfoSet">
												<xsl:sequence select="fn:exists(ns0:recordInfoLink)"/>
											</xsl:for-each>
										</xsl:variable>
										<xsl:sequence select="fn:exists($var36_result[.])"/>
									</xsl:for-each>
								</xsl:variable>
								<xsl:sequence select="fn:exists($var37_result[.])"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:sequence select="fn:true()"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<edm:isShownAt>
						<xsl:if test="$var45_result">
							<xsl:variable name="var44_result">
								<xsl:choose>
									<xsl:when test="$var6_resultof_any">	
									<xsl:for-each select="$var2_administrativeMetadata/ns0:recordWrap/ns0:recordInfoSet">
										<xsl:for-each select="ns0:recordInfoLink[(fn:position() = xs:decimal('1'))]">
											<xsl:sequence select="xs:string(xs:anyURI(fn:string(.)))"/>
										</xsl:for-each>
									</xsl:for-each>									
									</xsl:when>
									<xsl:otherwise>
										<xsl:variable name="var43_result" as="xs:string+">
											<xsl:for-each select="$var1_lidoRecID">
												<xsl:variable name="var41_resultof_cast" as="xs:string" select="fn:string(.)"/>
												<xsl:variable name="var42_result" as="xs:string">
													<xsl:choose>
														<xsl:when test="fn:contains($var41_resultof_cast, ':')">
															<xsl:sequence select="replace($var41_resultof_cast, ':', '/')"/>
														</xsl:when>
														<xsl:otherwise>
															<xsl:sequence select="replace($var41_resultof_cast, '-', '/')"/>
														</xsl:otherwise>
													</xsl:choose>
												</xsl:variable>
												<xsl:sequence select="fn:concat('http://www.mimo-db.eu/', $var42_result)"/>
											</xsl:for-each>
										</xsl:variable>
										<xsl:sequence select="xs:string(fn:string-join(for $x in $var43_result return xs:string($x), ' '))"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							<xsl:attribute name="rdf:resource" select="xs:string(xs:anyURI($var44_result))"/>
						</xsl:if>
					</edm:isShownAt>
					<edm:object>
						<xsl:for-each select="$var2_administrativeMetadata/ns0:resourceWrap/ns0:resourceSet/ns0:resourceID">
							<xsl:variable name="var6_pref" as="node()?" select="@ns0:pref"/>							
							<xsl:choose>
							<xsl:when test="fn:exists($var6_pref)">
								<xsl:attribute name="rdf:resource">
									<xsl:choose>
										<xsl:when test="fn:contains(fn:string($var6_pref), 'preferred')">
											<xsl:sequence select="fn:string(.)"/>
										</xsl:when>
										<xsl:otherwise>
											<xsl:sequence select="''"/>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:attribute>
							</xsl:when>	
							<xsl:otherwise>
							<xsl:if test="not($var_isShownByAssignedByPrefImage)">
								<xsl:variable name="var32_resultof_cast" as="xs:string" select="fn:string(.)"/>
									<xsl:if test="(fn:ends-with($var32_resultof_cast, '.jpg') or fn:ends-with($var32_resultof_cast, '.JPG'))">
										<xsl:attribute name="rdf:resource" select="xs:string(xs:anyURI($var32_resultof_cast))"/>
									</xsl:if>
							</xsl:if>
							</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</edm:object>
				</ore:Aggregation>
			</xsl:for-each>
		</rdf:RDF>
	</xsl:template>
</xsl:stylesheet>
