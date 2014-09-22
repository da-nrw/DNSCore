<%@ page import="daweb3.Object" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'object.label', default: 'Object')}" />
		<title>DA-NRW Objekte</title>
		<r:require module="messagebox"/>
		<r:script>
			$(function() {
				$("#filter").accordion({ collapsible: true, active: false });
			});
			function queuedFor(result) {
				var type = "error";
				if (result.success) type = "info";
				var messageBox = $("<div class='message-box'></div>");
				$("#page-body").prepend(messageBox);
				messageBox.message({
					type: type, message: result.msg
				});
			}
		</r:script>
	</head>
	<body>
		<a href="#list-object" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>
		<div id="filter" style="margin: 0.8em 0 0.3em">
			<h1><a href="#">Filter</a></h1> 
            <g:form name="searchForm" action="list">
            	<table>
            		<tr>
            			<td>Original Name:</td>
            			<td><g:textField name="search.origName" value="${params.search?.origName}" size="50"/></td>
            		</tr>
            		<tr>
            			<td>URN:</td>
            			<td><g:textField name="search.urn" value="${params.search?.urn}" size="50"/></td>
            		</tr>
            			<tr>
            			<td>Identifier:</td>
            			<td><g:textField name="search.identifier" value="${params.search?.identifier}" size="50"/></td>
            		</tr>
            		<g:if test="${admin}">
            			<tr>
            			<td>Contractor:</td>
            			<td>
            				<g:select id="contractor" name="searchContractorName" from="${contractorList}" noSelection="[null:'Alle auswählen']" optionKey="shortName" required="" value="${objectInstance?.contractorList?.shortName}" class="many-to-one"/>
						</td>
            		</tr>
            		</g:if>
            		<tr>
            			<td></td>
            			<td><g:submitButton name="submit" value="Filter anwenden"/></td>
            		</tr>
            	</table>     
            </g:form>
        </div>
		<div id="list-object" class="content scaffold-list" role="main">
			<h1>Ihre DA-NRW Objekte</h1>
			<g:if test="${flash.message}">
				<div class="message" role="status">${flash.message}</div>
			</g:if>

			
			<g:formRemote name="myForm" on404="alert('not found!')" 
              url="[controller: 'object', action:'queueAllForRetrieval']" 
              onLoaded="queuedFor(data)">

				<table>
					<thead>
						<tr>
							<th><g:message code="object.identifier.label" default="Ident" /></th>
							
							<g:sortableColumn property="urn" title="${message(code: 'object.urn.label', default: 'Urn')}" />
						
							<g:sortableColumn property="contractor" title="${message(code: 'object.user.label', default: 'Contractor')}" />
							<!-- <th><g:message code="object.contractor.label" default="Contractor" /></th> -->
							
							
							<g:sortableColumn property="origName" title="${message(code: 'object.origName.label', default: 'Orig Name')}" />
							<g:sortableColumn property="created" title="${message(code: 'object.created.label', default: 'Erstellt')}" />
							<g:sortableColumn property="modified" title="${message(code: 'object.modified.label', default: 'Geändert')}" />
							
							<g:if test="${admin}">
							<g:sortableColumn style="text-align: center" property="object_state" title="${message(code: 'object.object_state.label', default: 'Objekt Status')}" />
							<th style="text-align: center">Überprüfen</th>
							<th style="text-align: center">Pres. Derivate</th>
							<th style="text-align: center">Index</th>
							</g:if>
							<th style="text-align: center">Publ.</th>
							<th style="text-align: center">Anfordern				
								<g:if test="${!paginate}">
										<g:actionSubmitImage value="submit" action="submit"
                     	src="${resource(dir: 'images/icons', file: 'boxdownload32.png')}" />
								</g:if>
							</th>
							<th style="text-align: center">Entnahme			
							</th>			
							

						</tr>
					</thead>
					<tbody>
					<g:each in="${objectInstanceList}" status="i" var="objectInstance">
											
						<g:set var="statusCode" value="100" />
						<g:if test="${admin}">				
						<g:set var="statusCode" value="${objectInstance.getStatusCode()}" />
						</g:if>
						<tr class="${ ((i % 2) == 0 ? 'odd' : 'even') + ' status-type-' + statusCode}">
								<td>${fieldValue(bean: objectInstance, field: "identifier")}</td>
						
							<td><g:link action="show" id="${objectInstance.id}">${objectInstance.getFormattedUrn()}</g:link></td>
						
							<td>${fieldValue(bean: objectInstance, field: "user")}</td>
						
							<td>${fieldValue(bean: objectInstance, field: "origName")}</td>
							<td>${objectInstance.getFormattedCreatedDate()}</td>
							<td>${objectInstance.getFormattedModifiedDate()}</td>
							<g:if test="${admin}">
							<td style="text-align: center">
								<g:if test="${statusCode == 1}">
									<g:img style="width:16px; height:16px" uri="/images/icons/warning32.png"/>
								</g:if>
								<g:elseif test="${statusCode == 2}">
									<g:img style="width:16px; height:16px" uri="/images/icons/clock32.png"/>
								</g:elseif>
								<g:elseif test="${statusCode == 0}">
									<g:img style="width:16px; height:16px" uri="/images/icons/check32.png"/>
								</g:elseif>
							</td>
							<td style="text-align: center">
								<g:remoteLink action="queueForInspect" onLoaded="queuedFor(data)" id="${objectInstance.id}">
									<g:img style="width:16px; height:16px" uri="/images/icons/search32.png"
									title="${message(code: 'default.ltp.icon.queueForInspect', default: 'Objekt zum manuellen Überprüfen anfordern')}" 
									alt="${message(code: 'default.ltp.icon.queueForInspect', default: 'Objekt zum manuellen Überprüfen anfordern')}"/>
								</g:remoteLink>
							</td>
							<td style="text-align: center">
								<g:remoteLink action="queueForRebuildPresentation" onLoaded="queuedFor(data)" id="${objectInstance.id}">
									<g:img style="width:16px; height:16px" uri="/images/icons/exchange32.png"
									title="${message(code: 'default.ltp.icon.rebuildPR', default: 'Objekt für Präsentation neu erzeugen')}" 
									alt="${message(code: 'default.ltp.icon.rebuildPR', default: 'Objekt für Präsentation neu erzeugen')}"/>
								</g:remoteLink>
							</td>
							<td style="text-align: center">
								<g:remoteLink action="queueForIndex" onLoaded="queuedFor(data)" id="${objectInstance.id}">
									<g:img style="width:16px; height:16px" uri="/images/icons/exchange32.png"
									title="${message(code: 'default.ltp.icon.rebuildIndex', default: 'Objekt neu indexieren')}" 
									alt="${message(code: 'default.ltp.icon.rebuildIndex', default: 'Objekt neu indexieren')}"/>
								</g:remoteLink>
							</td>
							</g:if>
							<td>	
						<g:if test="${objectInstance.getPublished_flag()==1}">
							<g:link url="${objectInstance.getPublicPresLink()}" target="_blank"><g:img style="width:16px; height:16px" uri="/images/icons/globe.png"/></g:link>
						</g:if>
						<g:if test="${objectInstance.getPublished_flag()==2}">
							<g:link url="${objectInstance.getInstPresLink()}" target="_blank"><g:img style="width:16px; height:16px" uri="/images/icons/globe.png"/></g:link>
						</g:if>
						<g:if test="${objectInstance.getPublished_flag()==3}">
							<g:img style="width:16px; height:16px" uri="/images/icons/globe.png"/>
						</g:if>
							</td>
							<g:if test="${paginate}">
								<td style="text-align: center">
									<g:remoteLink action="queueForRetrieval" onLoaded="queuedFor(data)" id="${objectInstance.id}">
										<g:img style="width:16px; height:16px" uri="/images/icons/boxdownload32.png"/>
									</g:remoteLink>
								</td>
							</g:if>
							<g:else>
								<td style="text-align: center"><g:checkBox checked="false" value="${objectInstance.id}" name="check" id="i"/></td>	
							</g:else>
							<td style="text-align: center">
								<g:if test="${new File(baseFolder+ "/"+ objectInstance.identifier +".tar").exists()}">
									 <g:link controller="outgoing" action="download" params="['filename':objectInstance.identifier +'.tar']">
										<g:img style="width:16px; height:16px" uri="/images/icons/delivery.png"/>
									</g:link>
								</g:if>
							</td>
						</tr>
					</g:each>
					</tbody>
				</table>AdminView
			</g:formRemote>



			<g:if test="${paginate}" >
				<!-- workaround weil paginate die search map zerhackstückelt -->
				<g:set var="searchParams" value="${paramsList}"/>
				<div class="pagination">
					<g:paginate total="${objectInstanceTotal}" params="${searchParams}" />
				</div>
			</g:if>
		</div>
	</body>
</html>
