<!doctype html>
<html lang="en" class="no-js">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <title>
        <g:layoutTitle default="Grails"/>
    </title>
    <meta name="viewport" content="width=device-width, initial-scale=1"/>
	<asset:link rel="icon" href="apple-touch-icon.png" type="image/x-ico" />
	<asset:link rel="icon" href="apple-touch-icon-retina.png" sizes="114x114" type="image/x-ico" />
	<asset:stylesheet src="main.css"/>
	<asset:stylesheet src="mobile.css"/>
	<g:layoutHead/>
	<asset:javascript src="jquery-2.2.0.min.js"/>
	<asset:javascript src="jquery.periodicalupdater.js"/>
	<asset:javascript src="jquery.ui.messages.min.js"/>
 	<asset:stylesheet src="jquery-ui.js"/> 
	<asset:javascript src="application.js"/>
	<asset:stylesheet src="application.css"/>
    <g:layoutHead/>
</head>
<body>

   <div id="header" role="banner">
		<g:link controller="home"> 
			<asset:image  src="DANRW-Logo_small.png" alt="DANRW"/> 
		</g:link> 
		<h1>Web Konsole</h1>
		<g:if test="${actionName=='auth'}" />
		<g:else>
			<div id="header-menu">
				<form name="submitForm" method="POST" action="${createLink(controller: 'logout')}">
					<input type="hidden" name="" value="" >
					<a HREF="javascript:document.submitForm.submit()">Logout</a>
				</form> 
			</div>
		</g:else>
	</div>
	
  	<g:if test="${actionName=='auth'}" />  
	<g:elseif test="${controllerName=='logout'}"/>
	<g:else>
		<div class="welcome">
			<h1>Willkommen  ${user} ! <g:if test="${admin==1}"> (Administrator)</g:if> </h1>
  			<div class="vertical-menu">
				<ul>
				 	<li class="controller"><g:link controller="queueEntry" action="index">Bearbeitungsübersicht</g:link></li> 
					<g:if test="${admin==1}">
						<li class="controller"><g:link controller="object" action="listObjects">Auswahl Objekte nach Formaten</g:link></li>
					</g:if>
					<li class="controller"><g:link controller="queueEntry" action="listRequests">Entscheidungsübersicht</g:link></li>
					<li class="controller"><g:link controller="object">Eingelieferte Objekte (AIP)</g:link></li>
					<li class="controller"><g:link controller="incoming">Verarbeitung für abgelieferte SIP starten</g:link></li>
					<li class="controller"><g:link controller="outgoing">Objekt entnehmen (DIP)</g:link></li>
					<li class="controller"><g:link controller="info">Hinweise zur Ansteuerung über externe Systeme</g:link></li>
					<li class="controller"><g:link controller="conversionPolicies">Konfigurierte Konversionen</g:link></li>
					<li class="controller"><g:link controller="report">Abfragen verarbeiten</g:link></li>
					<li class="controller"><g:link controller="systemEvent">System-Eventsteuerung</g:link></li>
					<g:if test="${admin==1}">
						<li class="controller"><g:link controller="cbtalk">Adminfunktionen</g:link></li>
					</g:if>
				</ul>
			</div>
		</div> 
	</g:else>
	
	<div id="page-body">
		<g:layoutBody/> 
	</div>
	<div align="center">
		<div class="footer" id="page-footer" role="contentinfo">
			<g:meta name="app.name"/> 
			daweb3 Build:  <g:meta name="app.version.buildNumber"/>, LVR-InfoKom (ab 2014). HKI, Universität zu Köln 2011-2014. 
			<g:if test="${grailsApplication.config.provider.logo}">
				<img src="${resource(dir: 'images', file: grailsApplication.config.provider.logo)}" alt="Provider-Logo"/>
			</g:if>
		</div>
	</div><div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>
		
</body>
</html>
