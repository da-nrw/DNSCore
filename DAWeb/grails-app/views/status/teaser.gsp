<html>
<head>
	<title>Hinweise zur Abfrage durch externe Systeme</title>
	<meta name="layout" content="main">
</head>
<body>

<div class="nav" role="navigation">
	<ul>
		<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
		<li><a class="list" href="<g:createLink controller="info" action="index" />">REST Funktionen</a></li>
	</ul>
</div>
<div id="show-object" class="content scaffold-show" role="main">
	<h1>Hinweise</h1>
	<ul class="property-list object">
			
		<li><span class="property-value">
		Sie können den Zustand Ihrer Objekte (in Bearbeitung/Archivierung, jeweils mit Status) auch über externe Systeme abfragen lassen.
		</span></li>
		<li><span class="property-value">
		Nutzen Sie dafür eine URL der folgenden Schemata:<br>
		<b>https://Servername/<g:createLink controller="status" action="index" params="[urn: 'IhreURN']"/></b><br>
		<b>https://Servername/<g:createLink controller="status" action="index" params="[origName: 'IhrAblieferungsname']"/></b><br> 
		<b>https://Servername/<g:createLink controller="status" action="index" params="[identifier: 'IhrIdentifier']"/></b> <br>
		<b>https://Servername/<g:createLink controller="status" action="index" params="[containerName: 'IhrContainername']"/></b> <br>
		
		</span></li>
		<span class="property-value"><li>Als Antwort erhalten Sie ein maschinenlesbares Ergebnis (JSON)<br></li></span>
		<li><span class="property-value">
		Sie müssen sich zum Abruf authentifizieren. 
		</span></li>	
	</ul>
</div>
</body>
</html>
