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
		<script type="text/javascript">
		 $(document).ready(function(){
				$("#filter").accordion({ collapsible: true, active: false });
				<g:if test="${filterOn==1}">
				$( "#filter" ).accordion( "option", "active", 0 );
				</g:if>
		 });
		</script>
		<div id="filter" style="margin: 0.8em 0 0.3em">
			<h1><a href="#">Filter</a></h1> 
            <g:form name="searchForm" action="list">
            <g:hiddenField name="filterOn" value="${filterOn}" />
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
            		<tr>
            		<td>
            		Datumsbereich:
            		</td><td>
            		<g:select id="datetype" name="searchDateType" from="${['Datum erstellt','Datum geändert']}" keys="${['created','modified']}" value="${params.searchDateType}" />
            			</td>
						</tr>
            			<tr>
            			<td>Von Datum: </td>
            			<td><jqueryPicker:time name="searchDateStart" value=""/>(TT.MM.JJJJ HH:mm:ss)
            			</td>
            		</tr>
            			<tr>
            			<td>Bis Datum: </td>
            			<td><jqueryPicker:time name="searchDateEnd" value=""/>(TT.MM.JJJJ HH:mm:ss)
            				<% // fix for https://github.com/zoran119/grails-jquery-date-time-picker/issues/12 %>
            			<script type="text/javascript">
            			 $(document).ready(function(){
            			$("#searchDateStart").val("${params.searchDateStart}")
            			$("#searchDateEnd").val("${params.searchDateEnd}")
            			 })
            			</script>
            			</td>
            		</tr>
            		<tr>
            			<td></td>
            			<td><g:submitButton name="submit" value="Filter anwenden"/><g:submitButton name="loeschen" type="submit" value="Filter löschen"/></td>
            			<script type="text/javascript">
            			$(document).ready(function(){
            				 	$("#loeschen").click(function() {                				 
			            			$('#searchForm').find(':input').each(function() {
			            	            switch(this.type) {
			                            case 'text':
			                            	$(this).val('');
			                                break;                      
			                            case 'textarea':
			                                $(this).val('');
			                                break;
			            			 	case 'hidden':
			                                $(this).val('0');
			                                break;
			                            }
			            			});
            				    });
            			});</script>
            		</tr>
            	</table>     
            </g:form>
        </div>
		<div id="list-object" class="content scaffold-list" role="main">
			<h1>Ihre DA-NRW Objekte (${objectInstanceList.size()} Treffer von ${totalObjs} insgesamt)</h1>
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
							<g:sortableColumn property="origName" title="${message(code: 'object.origName.label', default: 'Orig Name')}" />
							<g:sortableColumn property="created" title="${message(code: 'object.created.label', default: 'Erstellt')}" />
							<g:sortableColumn property="modified" title="${message(code: 'object.modified.label', default: 'Geändert')}" />
							<th style="text-align: center">Publ.</th>
							<th style="text-align: center">Anfordern				
								<g:if test="${!paginate}">
										<g:actionSubmitImage value="submit" action="submit"
                     	src="${resource(dir: 'images/icons', file: 'boxdownload32.png')}" />
								</g:if>
							</th>
							<th style="text-align: center">Entnahme			
							</th>	
							<th style="text-align: center">Premis
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
						
							<td>${fieldValue(bean: objectInstance, field: "origName")}</td>
							<td>${objectInstance.getFormattedCreatedDate()}</td>
							<td>${objectInstance.getFormattedModifiedDate()}</td>
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
							<g:if test="${!objectInstance.isInWorkflowButton()}">
								<td style="text-align: center">
									<g:remoteLink action="queueForRetrieval" onLoaded="queuedFor(data)" id="${objectInstance.id}">
										<g:img style="width:16px; height:16px" uri="/images/icons/boxdownload32.png"/>
									</g:remoteLink>
								</td>
							</g:if><g:else><td style="text-align: center">Objekt in der Verarbeitung
							</td>
							</g:else>
							<td style="text-align: center">
								<g:if test="${new File(baseFolder+ "/"+ objectInstance.identifier +".tar").exists()}">
									 <g:link controller="outgoing" action="download" params="['filename':objectInstance.identifier +'.tar']">
										<g:img style="width:16px; height:16px" uri="/images/icons/delivery.png"/>
									</g:link>
								</g:if>
							</td>
							<g:if test="${!objectInstance.isInWorkflowButton()}">
								<td style="text-align: center">
									<g:link action="premisAnfordern" onLoaded="queuedFor(data)" id="${objectInstance.id}"  params="['objectIdentifier':objectInstance.identifier, 'objName':objectInstance.origName, 'pkg':objectInstance.packages[0].id, 'first': true, 'file': baseFolder+ '/'+ objectInstance.identifier +'.tar', 'id': objectInstance.id, 'filename':objectInstance.identifier +'.tar']">
										Premis anfordern<g:img style="width:12px; height:16px" uri="/images/icons/file-add-icon.png"/>
									</g:link>
								<g:if test="${!objectInstance.isInWorkflowButton()}">
								
								</g:if>
									<g:link action="premis" params="['objectIdentifier':objectInstance.identifier, 'objName':objectInstance.origName, 'pkg':objectInstance.packages[0].id, 'first': true, 'urn': objectInstance.urn]">
										Premis anzeigen<g:img style="width:18px; height:18px" uri="/images/icons/text-file-icon.png"/>
									</g:link>
								</td>
							</g:if><g:else><td style="text-align: center">Objekt in der Verarbeitung</td>
							</g:else>
						</tr>
					</g:each>
					</tbody>
				</table>
				<!-- 123${objectIdentifiers }456 -->
				StandardView
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
