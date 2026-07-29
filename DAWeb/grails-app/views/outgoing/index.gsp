<html>
	<head>
		<title>angeforderte Objekte (DIP)</title>
		<meta name="layout" content="main">
	
		
	</head>
	<body>
		<div class="page-body">
			<div class="blue-box"></div>
			<h2 id="page-header">angeforderte Objekte (DIP) </h2>
			<g:if test="${msg}">
				<div class="message" role="status">${msg}</div>
			</g:if>
			<div style="margin: 0.8em 0 0.3em">
				Folgende Pakete liegen für Sie im Verzeichnis <i>'${basedir}'</i> zum Abholen bereit:
				<g:each var="currentFile" in="${filelist}">
					<ul>
						<li class="outgoing-li">${currentFile.getName() }</li>
					</ul>
					
				</g:each>
			</div>
		</div>
	</body>
</html>