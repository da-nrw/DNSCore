<!doctype html>
<html lang="en" class="no-js" >
	<head>
	    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
	    <title>
	        <g:layoutTitle default="Grails"/>
	    </title>
	    <meta name="viewport" content="width=device-width, initial-scale=1"/>
<!-- 		<asset:link rel="icon" href="apple-touch-icon.png" type="image/x-ico" /> -->
<!-- 		<asset:link rel="icon" href="apple-touch-icon-retina.png" sizes="114x114" type="image/x-ico" /> -->
		<asset:link rel="icon" href="/icons/favicon.ico" type="image/x-ico" />
 		<asset:link rel="icon" href="/icons/favicon.ico" sizes="114x114" type="image/x-ico" />
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
   	    <div class="logos">
			<asset:image src="DA_NRW268x80.png" alt="DANRW" />
		    <asset:image src="DANRW_P_OBEN.png" alt="DANRW" align="right"  width="240" height="80" />
	    </div> 
	    <div id="header" role="banner">
			<div id="trennerHeader" role="banner">
	         	<h1>Web Konsole</h1>
	        </div>
			<g:if test="${actionName=='auth'}" />
			<g:else>
			 	<div id="header-menu"> 
					<form name="submitForm" method="POST" action="${createLink(controller: 'logout')}">
					 	<input type="hidden" name="" value="" > 
						<a HREF="javascript:document.submitForm.submit()" class="submitForm-btn">Logout</a>
					</form> 
				</div> 
			</g:else>
		</div>
		
	  	<g:if test="${actionName=='auth'}" />  
		<g:elseif test="${controllerName=='logout'}"/>
		<g:else>
			<div id="welcome">
				<span>Willkommen <strong>${user} </strong> !</span> 
				<span class="admin-style">
 					<g:if test="${admin==1}"> (Administrator)</g:if>
				</span>
			</div>
  			<div id="vertical-menu">
  				<div class="nav-blue-box"></div>
  				<div class="nav-ecke-oben" ></div>
				<ul class="nav-rahmen">
					<!-- Bearbeitungsübersicht / Entscheidungsübersicht  -->
					<g:if test="${controllerName=='queueEntry'}">
						<g:if test="${actionName=='list'}">
				 			<li class="aktuell"><g:link controller="queueEntry" action="index">Bearbeitungsübersicht</g:link></li>
				 			<li><g:link controller="queueEntry" action="listRequests">Entscheidungsübersicht</g:link></li> 
						</g:if>
						<g:elseif test="${actionName=='listRequests'}">
							<li><g:link controller="queueEntry" action="index">Bearbeitungsübersicht</g:link></li>
							<li class="aktuell"><g:link controller="queueEntry" action="listRequests">Entscheidungsübersicht</g:link></li>
						</g:elseif>
						<g:elseif test="${actionName=='show'}">
							<li class="aktuell"><g:link controller="queueEntry" action="index">Bearbeitungsübersicht</g:link></li>
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
							<g:if test="${actionName=='listObjects' || actionName =='listObjectsSearch'}">
								<li class="aktuell"><g:link controller="object" action="listObjects">Auswahl Objekte nach Formaten</g:link></li>
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
						<g:if test="${actionName=='list' || actionName=='archived' || actionName=='working' || actionName=='show'}">
							<li class="aktuell"><g:link controller="object" action="list">Eingelieferte Objekte (AIP)</g:link></li>
						</g:if>
						<g:else>
							<li><g:link controller="object" action="list">Eingelieferte Objekte (AIP)</g:link></li>
						</g:else>
					</g:if>
					<g:else>
						<li><g:link controller="object" action="list">Eingelieferte Objekte (AIP)</g:link></li>
					</g:else>
					
					<!-- Verarbeitung für abgelieferte SIP starten -->
					<g:if test="${controllerName=='incoming'}">
						<li class="aktuell"><g:link controller="incoming">Verarbeitung für abgelieferte SIP starten</g:link></li>
					</g:if>
					<g:else>
						<li><g:link controller="incoming">Verarbeitung für abgelieferte SIP starten</g:link></li>
					</g:else>
						
					<!-- Angeforderte Objekt (DIP) -->
					<g:if test="${controllerName=='outgoing'}">
						<li class="aktuell"><g:link controller="outgoing">Angeforderte Objekte (DIP)</g:link></li>
					</g:if>
					<g:else>
						<li><g:link controller="outgoing">Angeforderte Objekte (DIP)</g:link></li>
					</g:else>
					
					<!-- Hinweise zur Ansteuerung über externe Systeme -->
					<g:if test="${controllerName=='info' || controllerName=='automatedRetrieval' || controllerName=='status'}">
						<li class="aktuell"><g:link controller="info">Hinweise zur Ansteuerung über externe Systeme</g:link></li>
					</g:if>
					<g:else>
						<li><g:link controller="info">Hinweise zur Ansteuerung über externe Systeme</g:link></li>
					</g:else>
					
					<!-- Konfigurierte Konversionen -->
					<g:if test="${controllerName=='conversionPolicies'}">
						<li class="aktuell"><g:link controller="conversionPolicies">Konfigurierte Konversionen</g:link></li>
					</g:if>
					<g:else>
						<li><g:link controller="conversionPolicies">Konfigurierte Konversionen</g:link></li>
					</g:else>
					
					<!-- Abfragen verarbeiten -->
					<g:if test="${controllerName=='report'}">
						<li class="aktuell"><g:link controller="report">Abfragen verarbeiten</g:link></li>
					</g:if>
					<g:else>	
						<li><g:link controller="report">Abfragen verarbeiten</g:link></li>
					</g:else>
					
					<!-- System-Eventsteuerung -->
					<g:if test="${controllerName=='systemEvent' || controllerName=='cbNode'}">
						<li class="aktuell"><g:link controller="systemEvent">System-Eventsteuerung</g:link></li>
					</g:if>
					<g:else>	
						<li><g:link controller="systemEvent">System-Eventsteuerung</g:link></li>
					</g:else>
					
					<!-- Adminfunktionen -->
					<g:if test="${admin==1}">
						<g:if test="${controllerName=='cbtalk' || controllerName=='user' || controllerName=='role' 
									|| controllerName=='userRole' || controllerName=='preservationSystem' || controllerName=='formatMapping'}">
							<li class="aktuell"><g:link controller="cbtalk">Adminfunktionen</g:link></li>
						</g:if>
						<g:else>
							<li><g:link controller="cbtalk">Adminfunktionen</g:link></li>
						</g:else>
					</g:if>
				</ul>
			</div>
		</g:else>
		
		<g:if test="${actionName=='auth'}">
			<div class="page-body" style="margin-left: 0px;" >
				<g:layoutBody/>
			</div>
		</g:if>
		<g:else>
		<div class="page-body">
			<g:layoutBody/> 
		</div> 
		</g:else>
		<div align="center">
			<div id="footer" role="contentinfo">
				<g:meta name="app.name"/> 
				daweb3 Build:  <g:meta name="app.version.buildNumber"/>, LVR-InfoKom (ab 2014). HKI, Universität zu Köln 2011-2014. 
				<g:if test="${grailsApplication.config.provider.logo}">
					<img src="${resource(dir: 'images', file: grailsApplication.config.provider.logo)}" alt="Provider-Logo"/>	 					
				</g:if>
			</div>
		</div>
		<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>
	</body>
</html>
