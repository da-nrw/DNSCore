<%@ page import="daweb3.FormatMapping" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'formatMapping.label', default: 'Format-Mapping')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<r:script>
 		function goBack() {
    	 	window.history.back();
		 }
	</r:script>
	<body>
		<a href="#list-formatMapping" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><a class="listl" href="${createLink(uri: '/cbtalk/index')}"><g:message message="zurück zur Administrationsseite"/></a></li>
			</ul>
		</div>
		
		<div> <br>
			<g:formRemote name="myForm" on404="alert('not found!')" url="[controller: 'formatMapping', action:'deleteAndFill']" onLoaded="queuedFor(data)">
				<g:actionSubmit value="Tabelle leeren und neu laden" action="deleteAndFill" onclick="return confirm('Are you sure???')" />
			</g:formRemote> 
		</div>
		<br>
<%--		<div id="list-formats" class="content scaffold-list" role="main" >--%>
		<div>
			<table>
				 <thead>							
						<tr>
						<g:sortableColumn property="puid" title="${message(code: 'formatMapping.puid', default: 'PUID')}" />
						<g:sortableColumn property="extension" title="${message(code: 'formatMapping.extension', default: 'Erweiterung')}" />
						<g:sortableColumn property="mimeType" title="${message(code: 'formatMapping.mimeType', default: 'MIME-Type')}" />
						<g:sortableColumn property="formatName" title="${message(code: 'formatMapping.formatName', default: 'Bezeichnung')}" />
						</tr>
				</thead>
				<tbody>	
					<g:each in="${formatMappings}" var="formatMapping" status="i">
		        		<tr class="${ ((i % 2) == 0 ? 'odd' : 'even') }">
		        			<td>${formatMapping.puid}</td>
		        			<td>${formatMapping.extension}</td>
		        			<td>${formatMapping.mimeType}</td>
		        			<td>${formatMapping.formatName}</td>
						</tr>
					</g:each>
				</tbody>
			</table>
		</div>
	</body>
</html>
