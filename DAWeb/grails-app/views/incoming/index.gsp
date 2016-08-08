<html>
	<head>
		<title>Verarbeitung manuell starten</title>
		<meta name="layout" content="main">
	</head>
	<body>
		<script type="text/javascript">
		alert('Hallo 1'); 
			function toggle(source) {
			  checkboxes = document.getElementsByName('currentFiles');
			  for(var i in checkboxes)
			    checkboxes[i].checked = source.checked;
			}
		</script>
		<g:if test="${msg}">
			<div class="message" role="status">${msg}</div>
		</g:if>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>
		<g:form controller="incoming" >
			<div><input type="checkbox"  onClick="toggle(this)"/>Alle an-/abwählen</div><br>
			<g:each in="${filelist}" var="currentFile" status="i">
			    <p><g:checkBox name="currentFiles" value="${currentFile.getName()}" checked="false"/>${currentFile.getName()}</p>
			</g:each>
			<g:actionSubmit value="Starten" action="save"/>
		</g:form>
	</body>
</html>