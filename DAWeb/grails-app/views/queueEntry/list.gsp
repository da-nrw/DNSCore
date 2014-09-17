<%@ page import="daweb3.QueueEntry" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'queueEntry.label', default: 'QueueEntry')}" />
		<title>Status der Verarbeitung</title>
		<r:require modules="periodicalupdater, jqueryui"/>
		 <jqui:resources/>
		<r:script>
			var order = "asc";
			var sort = "id";
			$(function() {
				$("#legend").accordion({ collapsible: true, active: false, autoHeight: false });
			});
			$(function() {
				$("#filter").accordion({ collapsible: true, active: false });
			});
	
		<g:if test="${ !params.search }">		
			var obj = $.PeriodicalUpdater("./listSnippet",
				{
					method: "get",
					minTimeout: 1000,
					maxTimeout: 1000,
					data: function() {
						return { order: order, sort: sort}
					},
					success: function(data) {
						console.log("success - sort: "+sort+", order: "+order);
						$("#entry-list").html(data);
						$("#entry-list th.field-"+sort).addClass("sorted "+order);
					}
				}
			);
			</g:if>
			function stopUpdater() {		
				obj.stop();
			}
		
			function sortQueue(field) {
				console.log("sortQueue: "+field);
				if (field == sort && order == "asc") order = "desc";
				else order = "asc";
				sort = field;
				console.log("sortQueue - sort: "+sort+", order: "+order);
				return false;
			}
			function test(field) {
				console.log(field);
				return false;
			}
			
		</r:script>
	</head>
	<body>
	

		<a href="#list-queueEntry" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>
	
		<div id="list-queueEntry" class="content scaffold-list" role="main">			
			<h1>Bearbeitungsübersicht</h1>			
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>
				<div id="filter" style="margin: 0.8em 0 0.3em">
			<h1><a href="#">Filter</a></h1> 
            <g:form name="searchForm" id="filterform" action="list">
            	<table>
            	<tr>
            		<td>Status:</td>
            			<td><g:textField name="search.status" value="${params.search?.status}" size="5"/></td>
            		</tr>  		
            		<tr>
            			<td>Originalname:</td>
            			<td><g:textField name="search.obj.origName" value="${params.search?.obj?.origName}" size="50"/></td>
            		</tr>
            		<tr>
            			<td>URN:</td>
            			<td><g:textField name="search.obj.urn" value="${params.search?.obj?.urn}" size="50"/></td>
            		</tr>
            			<tr>
            			<td>Identifier:</td>
            			<td><g:textField name="search.obj.identifier" value="${params.search?.obj?.identifier}" size="50"/></td>
            		</tr>
            	
            		<tr>
            			<td></td>
            			<td>
            			<g:submitButton name="submit" value="Filter anwenden"/></td>
            		</tr>
            	</table>     
            </g:form>
           </div>
           
<g:if test="${ !params.search }">
	<!-- Update:&nbsp;<a href="#" onclick="stopUpdater();">stop</a>&nbsp;<a href="#" onclick="startUpdater();">start</a> -->	
     </g:if>   
			
			<!-- This div is updated through the periodical updater -->
			<div class="list" id="entry-list">
				<g:include action="listSnippet" />
			</div>
						
		</div>
		<div id="legend">
			<h1><a href="#">Hinweise zu den Statuscodes:</a></h1>
			<div>
			<p style="font-style:italic">(xx0 bedeutet "wartend" - hingegen bezeichnen xx1,xx3,xx4 einen Fehler, xx2: bezeichnet arbeitend)</p> 
			<table>
			<tr><th>Statuscode</th><th>Kurzbezeichnung</th><th>Beschreibung</th></tr>
			<tr><td>110</td><td>IngestUnpackAction</td><td>Auspacken & Vollständigkeitstests</td></tr>
			<tr><td>120</td><td>IngestRegisterObjectAction</td><td>Objekt- oder Deltaerkennung, Typerkennung</td></tr>
			<tr><td>220</td><td>IngestScanAction</td><td>Formaterkennung</td></tr>
			<tr><td>230</td><td>IngestConvertAction</td><td>LZA Konvertierung</td></tr>
			<tr><td>250</td><td>IngestMetadataUpdateAction</td><td>Update der Metadaten</td></tr>
			<tr><td>260</td><td>IngestCheckFormatAction</td><td>Überprüfung der LZA Konvertierung</td></tr>
			<tr><td>270</td><td>IngestCreatePremisAction</td><td>Bearbeitung der PREMIS-Datei</td></tr>
			<tr><td>310</td><td>IngestScanForPresentationAction</td><td>Formaterkennung für Präsentation auf Basis der LZA Formate</td></tr>
			<tr><td>320</td><td>IngestConvertForPresentationAction</td><td>Bildung der PIPs (Präsentationsderivate)</td></tr>
			<tr><td>330</td><td>IngestPreProcessForPresentationAction</td><td>Verschieben der PIPs</td></tr>
			<tr><td>340</td><td>IngestShortenFilenamesAction</td><td>Kürzung der PIP Dateinamen</td></tr>
			<tr><td>350</td><td>IngestPreUpdateMetadataAction</td><td>Update der Metadaten nach PIP Erstellung</td></tr>
			<tr><td>360</td><td>IngestPrepareSendToPresenterAction</td><td>Anmeldung der PIP zur Übertragung ans Pres. Repository</td></tr>
			<tr><td>370</td><td>IngestBuildAIPAction</td><td>AIP Erstellung</td></tr>
			<tr><td>380</td><td>IngestTarAction</td><td>AIP Erstellung als TAR-Archiv</td></tr>
			<tr><td>400</td><td>ArchiveReplicationAction</td><td>Ablage auf LZA Medien und Replikation</td></tr>
			<tr><td>440</td><td>ArchiveReplicationCheckAction</td><td>Prüfung der Replikationen</td></tr>
			<tr><td>540</td><td>FetchPIPsAction</td><td>Replikation der PIP an den Presentation Repository Knoten</td></tr>
			<tr><td>550</td><td>SendToPresenterAction</td><td>Einspielung der PIP in das Presentation Repository</td></tr>
			<tr><td>560</td><td>CreateEDMAction</td><td>EDM Metadaten-Erstellung</td></tr>
			<tr><td>570</td><td>IndexESAction</td><td>Indizierung im Elasticsearch Suchindex</td></tr>			
			<tr><td>580</td><td>FriendshipConversionAction</td><td>Konvertierung auf anderem Knoten</td></tr>			
			<tr><td>600</td><td>RestartIngestWorkflowAction</td><td>Zurücksetzung des Ingestworkflows</td></tr>			
			<tr><td>700</td><td>PIPGenObjectToWorkareaAction</td><td>Übertragung von AIP an das Knotenarbeitsverzeichnis</td></tr>
			<tr><td>710</td><td>PIPGenScanForPresentationAction</td><td>Scannen der Präsentationsformate</td></tr>
			<tr><td>720</td><td>PIPGenConvertForPrestationAction</td><td>Bildung der PIPs (Präsentationsderivate)</td></tr>
			<tr><td>730</td><td>PIPGenPreProcessForPresentationAction</td><td>Verschieben der PIPs</td></tr>
			<tr><td>740</td><td>PIPGenShortenFilenamesAction</td><td>Kürzung der PIP Dateinamen</td></tr>
			<tr><td>750</td><td>PIPGenPreUpdateMetadataAction</td><td>Update der Metadaten nach PIP Erstellung</td></tr>
			<tr><td>760</td><td>PIPGenPrepareSendToPresenterAction</td><td>Anmeldung der PIP zur Übertragung ans Pres. Repository</td></tr>
			<tr><td>770</td><td>PIPGenCleanWorkareaAction</td><td>Säuberung des Arbeitsverzeichnisses</td></tr>
			<tr><td>800</td><td>DeleteSIPPackageAction</td><td>Löschung des SIP vom Arbeitsverzeichnis</td></tr>
			<tr><td>900</td><td>RetrievalObjectToWorkAreaAction</td><td>Auslesen eines AIP von den LZA Medien</td></tr>
			<tr><td>910</td><td>RetrievalAction</td><td>Bildung des DIP, Übertragung an das Ausgabeverzeichnis des Contractor</td></tr>
			<tr><td>950</td><td>RetrievalDeliveredDIPAction</td><td>Warten auf Abholung durch Contractor</td></tr>
			<tr><td>960</td><td>PostRetrievaAction</td><td>Wurde abgeholt, vorbereiten auf Löschung DIP</td></tr>
			<tr><td>5000</td><td>AuditAction</td><td>Überprüfung des AIP</td></tr>
			</table>
				<a target="liste" href="https://docs.google.com/drawings/d/1qEd_LVNXKiiHmAW_LKNL0hEkS6ixcVf8TgO9-5a9WrM/edit?pli=1">(Quelle: Workflowdiagramm)</a>
			</div>
		</div>
	</body>
</html>
