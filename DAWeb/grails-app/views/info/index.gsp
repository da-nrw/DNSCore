<html>
<head>
	<title>Hinweise zur Verwaltung der DNS durch externe Systeme</title>
	<meta name="layout" content="main">
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

	<div class="nav" role="navigation">
		<ul>
			<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
		</ul>
	</div>
	<div id="controller-list" role="navigation"  style="margin: 0.8em 0 0.3em">
		<h2>REST Funktionen:</h2>
		<ul>
			<li class="controller"><g:link controller="automatedRetrieval">Erstellung von Retrievalanfragen </g:link></li>
			<li class="controller"><g:link controller="status" action="teaser">Abfrage der Verarbeitung und Archivierung</g:link></li>
		</ul>
	</div>
</body>
</html>
