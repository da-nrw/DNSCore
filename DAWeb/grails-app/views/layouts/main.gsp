<!doctype html>
<html lang="en" class="no-js" >
	<head>
	    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
	    <title>
	        <g:layoutTitle default="Grails"/>
	    </title>
	    <meta name="viewport" content="width=device-width, initial-scale=1"/>
		<asset:link rel="icon" href="apple-touch-icon.png" type="image/x-ico" />
		<asset:link rel="icon" href="apple-touch-icon-retina.png" sizes="114x114" type="image/x-ico" />
		<asset:link rel="shortcut icon" href="/icons/favicon.ico" type="image/x-icon" />
		<asset:stylesheet src="main.css"/>
		<asset:stylesheet src="mobile.css"/>
		<g:layoutHead/>
		<asset:javascript src="jquery-2.2.0.min.js"/>
		<asset:javascript src="jquery.periodicalupdater.js"/>
		<asset:javascript src="jquery.ui.messages.min.js"/>
		<asset:javascript src="jquery-ui.js"/>
	 	<asset:stylesheet src="jquery-ui.css"/> 
		<asset:javascript src="application.js"/>
		<asset:stylesheet src="application.css"/>
	    <g:layoutHead/>
	</head>
	<body>
	   <div id="header" role="banner">
			<g:link controller="home"> 
				<asset:image src="DA_NRW268x80.png" alt="DANRW" />
	        </g:link>
			<asset:image src="DANRW_P_OBEN.png" alt="DANRW" align="right" width="268" height="80" /> 
	        <div class="trennerHeader" role="banner">
	         	<h1>Web Konsole</h1>
	        </div>
		   
			
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
				<h1>Willkommen  ${user} ! <g:if test="${admin==1}"> (Administrator)</g:if></h1>
	  			<div id="vertical-menu">
					<ul>
						<!-- Bearbeitungsübersicht / Entscheidungsübersicht  -->
						<g:if test="${controllerName=='queueEntry'}">
							<g:if test="${actionName=='list'}">
					 			<li id="aktuell"><g:link controller="queueEntry" action="index">Bearbeitungsübersicht</g:link></li>
					 			<li><g:link controller="queueEntry" action="listRequests">Entscheidungsübersicht</g:link></li> 
							</g:if>
							<g:elseif test="${actionName=='listRequests'}">
								<li><g:link controller="queueEntry" action="index">Bearbeitungsübersicht</g:link></li>
								<li id="aktuell"><g:link controller="queueEntry" action="listRequests">Entscheidungsübersicht</g:link></li>
							</g:elseif>
							<g:elseif test="${actionName=='show'}">
								<li id="aktuell"><g:link controller="queueEntry" action="index">Bearbeitungsübersicht</g:link></li>
								<li><g:link controller="queueEntry" action="listRequests">Entscheidungsübersicht</g:link></li>
							</g:elseif>
						</g:if>
						<g:else>
							<li><g:link controller="queueEntry" action="index">Bearbeitungsübersicht</g:link></li>
							<li><g:link controller="queueEntry" action="listRequests">Entscheidungsübersicht</g:link></li>
						</g:else>
						
						<!-- Auswahl Objekte nach Formaten -->
						<g:if test="${admin==1}">
							<g:if test="${controllerName=='object'}">
								<g:if test="${actionName=='listObjects'}">
									<li id="aktuell"><g:link controller="object" action="listObjects">Auswahl Objekte nach Formaten</g:link></li>
								</g:if>
								<g:else>
									<li><g:link controller="object" action="listObjects">Auswahl Objekte nach Formaten</g:link></li>
								</g:else>
							</g:if>
							<g:else>
								<li><g:link controller="object" action="listObjects">Auswahl Objekte nach Formaten</g:link></li>
							</g:else>
						</g:if>
						
						<!-- Eingelieferte Objekte (AIP) -->
						<g:if test="${controllerName=='object'}">
							<g:if test="${actionName!='listObjects'}">
								<li id="aktuell"><g:link controller="object">Eingelieferte Objekte (AIP)</g:link></li>
							</g:if>
							<g:else>
								<li><g:link controller="object">Eingelieferte Objekte (AIP)</g:link></li>
							</g:else>
						</g:if>
						<g:else>
							<li><g:link controller="object">Eingelieferte Objekte (AIP)</g:link></li>
						</g:else>
						
						<!-- Verarbeitung für abgelieferte SIP starten -->
						<g:if test="${controllerName=='incoming'}">
							<li id="aktuell"><g:link controller="incoming">Verarbeitung für abgelieferte SIP starten</g:link></li>
						</g:if>
						<g:else>
							<li><g:link controller="incoming">Verarbeitung für abgelieferte SIP starten</g:link></li>
						</g:else>
							
						<!-- Objekt entnehmen (DIP) -->
						<g:if test="${controllerName=='outgoing'}">
							<li id="aktuell"><g:link controller="outgoing">Objekt entnehmen (DIP)</g:link></li>
						</g:if>
						<g:else>
							<li><g:link controller="outgoing">Objekt entnehmen (DIP)</g:link></li>
						</g:else>
						
						<!-- Hinweise zur Ansteuerung über externe Systeme -->
						<g:if test="${controllerName=='info'}">
							<li id="aktuell"><g:link controller="info">Hinweise zur Ansteuerung über externe Systeme</g:link></li>
						</g:if>
						<g:else>
							<li><g:link controller="info">Hinweise zur Ansteuerung über externe Systeme</g:link></li>
						</g:else>
						
						<!-- Konfigurierte Konversionen -->
						<g:if test="${controllerName=='conversionPolicies'}">
							<li id="aktuell"><g:link controller="conversionPolicies">Konfigurierte Konversionen</g:link></li>
						</g:if>
						<g:else>
							<li><g:link controller="conversionPolicies">Konfigurierte Konversionen</g:link></li>
						</g:else>
						
						<!-- Abfragen verarbeiten -->
						<g:if test="${controllerName=='report'}">
							<li id="aktuell"><g:link controller="report">Abfragen verarbeiten</g:link></li>
						</g:if>
						<g:else>	
							<li><g:link controller="report">Abfragen verarbeiten</g:link></li>
						</g:else>
						
						<!-- System-Eventsteuerung -->
						<g:if test="${controllerName=='systemEvent'}">
							<li id="aktuell"><g:link controller="systemEvent">System-Eventsteuerung</g:link></li>
						</g:if>
						<g:else>	
							<li><g:link controller="systemEvent">System-Eventsteuerung</g:link></li>
						</g:else>
						
						<!-- Adminfunktionen -->
						<g:if test="${admin==1}">
							<g:if test="${controllerName=='cbtalk'}">
								<li id="aktuell"><g:link controller="cbtalk">Adminfunktionen</g:link></li>
							</g:if>
							<g:else>
								<li><g:link controller="cbtalk">Adminfunktionen</g:link></li>
							</g:else>
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
