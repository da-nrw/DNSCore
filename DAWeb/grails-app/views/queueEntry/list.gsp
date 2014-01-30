


<%@ page import="daweb3.QueueEntry" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'queueEntry.label', default: 'QueueEntry')}" />
		<title>Status der Verarbeitung</title>
		<r:require modules="periodicalupdater, jqueryui"/>
		<r:script>
			var order = "asc";
			var sort = "id";
			var upd;
			$(function() {
				$("#legend").accordion({ collapsible: true, active: false, autoHeight: false });
			});
			$(function() {
				$("#filter").accordion({ collapsible: true, active: false });
			});
			
			function startUpdater(){
			upd = $.PeriodicalUpdater("./listSnippet",
				{
					method: "get",
					minTimeout: 1000,
					maxTimeout: 1000,
					data: function() {
						return { order: order, sort: sort }
					},
					success: function(data) {
						console.log("success - sort: "+sort+", order: "+order);
						$("#entry-list").html(data);
						$("#entry-list th.field-"+sort).addClass("sorted "+order);
					}
				}
			);
			}
			function stopUpdater() {
				upd.stop();
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
            			<td><g:textField name="search.status" value="${params.search?.status}" size="50"/></td>
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
            			<td><g:submitButton name="submit" value="Filter anwenden"/></td>
            		</tr>
            	</table>     
            </g:form>
           </div>
           
<g:if test="${ !params.search }">
	 Update:&nbsp;<a href="#" onclick="stopUpdater();">stop</a>&nbsp;<a href="#" onclick="startUpdater();">start</a>
     </g:if>   
			
			<!-- This div is updated through the periodical updater -->
			<div class="list" id="entry-list">
				<g:include action="listSnippet" />
			</div>
						
		</div>
		<div id="legend">
			<h1><a href="#">Hinweise zu den Statuscodes</a></h1>
			<div>
				<p style="font-style:italic">(xx1: bezeichnet einen Fehler, xx2: bezeichnet	arbeitend)<p> 
				<a target="liste" href="https://docs.google.com/drawings/d/1qEd_LVNXKiiHmAW_LKNL0hEkS6ixcVf8TgO9-5a9WrM/edit?pli=1">Übersichtsliste mit den aktuellen Statuskennungen</a>
			</div>
		</div>
	</body>
</html>
