<!doctype html>
<html>
	<head>
		<meta name="layout" content="main"/>
		<title>Willkommen bei der DA-NRW Weboberfläche</title>
		<style type="text/css" media="screen">
			#controller-list ul {
				list-style-position: inside;
			}

			#controller-list li {
				line-height: 1.3;
				list-style-position: inside;
				margin: 0.5em;
			}
		</style>
	</head>
	<body>
		<h1>Willkommen ${user} ! <g:if test="${admin==1}"> (Administrator)
		</g:if></h1>
		<div id="controller-list" role="navigation">
			<h2>Funktionen:</h2>
			<ul>
				<li class="controller"><g:link controller="queueEntry">Bearbeitungsübersicht</g:link></li>
				<li class="controller"><g:link controller="queueEntry" action="listMigrationRequests">Entscheidungsübersicht</g:link></li>
				<li class="controller"><g:link controller="object">Eingelieferte Objekte (AIP)</g:link></li>
				<li class="controller"><g:link controller="incoming">Verarbeitung für abgelieferte SIP starten</g:link></li>
				<li class="controller"><g:link controller="outgoing">Objekt entnehmen (DIP)</g:link></li>
				<li class="controller"><g:link controller="info">Hinweise zur Ansteuerung über externe Systeme</g:link></li>
				<li class="controller"><g:link controller="conversionPolicies">Konfigurierte Konversionen</g:link></li>
				<g:if test="${ admin==1}">
				<li class="controller"><g:link controller="cbtalk">Adminfunktionen</g:link></li>
				</g:if>
			
			</ul>
		</div>
	</body>
</html>
