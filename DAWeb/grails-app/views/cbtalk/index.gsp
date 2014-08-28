<%@ page contentType="text/html; charset=UTF-8" %>
<html>
  <head>
    <meta name="layout" content="main" />
    <title>Administrative Funktionen</title>         
  </head>
  <r:require modules="periodicalupdater, jqueryui"/>
		<r:script>
			$(function() {
				$("#legend").accordion({ collapsible: true, active: false, autoHeight: false });
			});
			$(function() {
				$("#filter").accordion({ collapsible: true, active: false });
			});
			
			$.PeriodicalUpdater("./messageSnippet",
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
  <body>
  <div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><a class="list" href="${createLink(controller: 'conversionPolicies', action: 'index')}">Conversion Policies</a></li>
				<li><a class="list" href="${createLink(controller: 'user', action: 'index')}">Users</a></li>
				<li><a class="list" href="${createLink(controller: 'role', action: 'index')}">Roles</a></li>
				<li><a class="list" href="${createLink(controller: 'userRole', action: 'index')}">UserRoles</a></li>
				<li><a class="list" href="${createLink(controller: 'PreservationSystem', action: 'index')}">PreservationSystem</a></li>
			</ul>
		</div>
    <div class="body">
      <h1>CbTalk</h1>
      <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
      </g:if>
      <g:form action="save" method="post">
   <g:submitButton name="stopFactory" value="stop Factory" />
   <g:submitButton name="startFactory" value="start Factory" /> 
   <g:submitButton name="showActions" value="show Actions" /> 
   <g:submitButton name="gracefulShutdown" value="ContentBroker graceful shutdown" />  
   <g:submitButton name="showVersion" value="Show Version of ContentBroker" />    
</g:form>
Rückmeldungen des ContentBroker (können verzögert eintreffen)
			<!-- This div is updated through the periodical updater -->
			<div class="list" id="entry-list">
				<g:include action="messageSnippet" />
			</div>
     </div>
  </body>
</html>