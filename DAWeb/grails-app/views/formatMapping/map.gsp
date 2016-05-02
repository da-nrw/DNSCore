<%@ page contentType="text/html; charset=UTF-8" %>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'formatMapping.label', default: 'Format-Mapping')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
 	<r:require modules="periodicalupdater, jqueryui"/>
 	<jqui:resources/>
		<r:script>
			var order = "desc";
			var sort = "created";
			$(function() {
				$("#legend").accordion({ collapsible: true, active: false, autoHeight: false });
			});
			$(function() {
				$("#filter").accordion({ collapsible: true, active: false });
			});
			var obj = $.PeriodicalUpdater("./mapSnippet",
				{
					method: "get",
					minTimeout: 10000,
					maxTimeout: 10000,
					success: function(data) {
						$("#entry-list").html(data);
					}
				}
			);
		</r:script>
	</head>
	<body>
       <h1 class="page-header">Format-Mapping</h1> 
		<a href="#list-formatMapping" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><a class="listl" href="${createLink(uri: '/cbtalk/index')}"><g:message message="zurück zur Administrationsseite"/></a></li>
			</ul>
		</div><br>
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
		<g:each in="${formatMappings}" var="formatMappingSn" status="i">
       		<tr class="${ ((i % 2) == 0 ? 'odd' : 'even') }">
       			<td>${formatMappingSn.puid}</td>
       			<td>${formatMappingSn.extension}</td>
       			<td>${formatMappingSn.mimeType}</td>
       			<td>${formatMappingSn.formatName}</td>
			</tr>
		</g:each>
	</tbody>
	<!-- This div is updated through the periodical updater -->
	<div class="list" id="entry-list">
		<g:include action="mapSnippet" />
	</div>
	</table>
  </body>
</html>
