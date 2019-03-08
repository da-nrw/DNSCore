<html>
	<head>
		<title>Objekte entnehmen (DIP)</title>
		<meta name="layout" content="main">
	
		
	</head>
	<body>
		<div class="page-body">
			<div class="blue-box"></div>
			<h2 id="page-header">Objekte entnehmen (DIP) </h2>
			<g:if test="${msg}">
				<div class="message" role="status">${msg}</div>
			</g:if>
			<div style="margin: 0.8em 0 0.3em">
				<g:each var="currentFile" in="${filelist}">
					<g:link controller="outgoing" action="download" params="['filename':currentFile.getName()]">${currentFile.getName() }</g:link><br>
				</g:each>
			</div>
		</div>
	</body>
</html>