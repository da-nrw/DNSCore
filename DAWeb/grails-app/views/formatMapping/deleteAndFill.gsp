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
		<a href="#list-formatMapping" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><a class="listl" href="${createLink(uri: '/cbtalk/index')}"><g:message message="zurück zur Administrationsseite"/></a></li>
			</ul>
		</div><br>
		<!-- This div is updated through the periodical updater -->
		<div class="list" id="entry-list">
			<g:include action="mapSnippet" />
		</div>
	</body>
</body>
</html>