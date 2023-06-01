<!doctype html>
<html>
<head>
<h1>Willkommen  ${user} ! <g:if test="${admin==1}"> (Administrator)</g:if> </h1>
	   <ul class="nav nav-tabs">
	       	<li role=presentation" class="controller"><g:link controller="queueEntry" action="index">Bearbeitungsübersicht</g:link></li> 
			<li role=presentation" class="controller"><g:link controller="incoming">Verarbeitung für abgelieferte SIP starten</g:link></li>
			<g:if test="${ admin==1}">
				<li role=presentation"  class="controller"><g:link controller="object" action="listObjects">Auswahl Objekte nach Formaten</g:link></li>
			</g:if>
			<li role=presentation" class="controller"><g:link controller="object">Eingelieferte Objekte (AIP)</g:link></li>
			<li role=presentation" class="controller"><g:link controller="queueEntry" action="listRequests">Entscheidungsübersicht</g:link></li>
			<li role=presentation" class="controller"><g:link controller="outgoing">Objekt entnehmen (DIP)</g:link></li>
			
<!-- 			<li role=presentation" class="controller"><g:link controller="statistics">Statistik über die eingelieferten Objekte</g:link></li> -->
			
			<li role=presentation" class="controller"><g:link controller="info">Hinweise zur Ansteuerung über externe Systeme</g:link></li>
			<li role=presentation" class="controller"><g:link controller="conversionPolicies">Konfigurierte Konversionen</g:link></li>
			<li role=presentation" class="controller"><g:link controller="report">Abfragen verarbeiten</g:link></li>
			<li role=presentation" class="controller"><g:link controller="systemEvent">System-Eventsteuerung</g:link></li>
			<g:if test="${ admin==1}">
				<li role=presentation" class="controller"><g:link controller="cbtalk">Adminfunktionen</g:link></li>
			</g:if>
	</ul>
</head>
<body/>
</html>