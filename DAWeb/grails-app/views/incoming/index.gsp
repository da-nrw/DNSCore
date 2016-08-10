<html>
	<head>
		<title>Verarbeitung manuell starten</title>
		<meta name="layout" content="main">
	</head>
	<body>
		<script type="text/javascript">
			function toggle(source) {
			  checkboxes = document.getElementsByName('currentFiles');
			  for(var i in checkboxes) {
			    checkboxes[i].checked = source.checked;
			  }
			}
			function deselect(source) {
				if (document.getElementById('waehlen').checked) {
					if (source.checked ) {
					} else {
						document.getElementById('waehlen').checked = false;
					}
				} else {
					checkboxes = document.getElementsByName('currentFiles');
					var i= 0;
 					while ( i < checkboxes.length) {  
						 
					 	if( checkboxes[i].checked) {
						 	check = true;
						 	i++;
						 } else {
						 	check = false;
						 	i++;
						 	break;
						}
					}
					if (check) {
						document.getElementById('waehlen').checked = true;
					} else {
						document.getElementById('waehlen').checked = false;
					}
				}
			}	
			
		</script>
		<g:if test="${msg}">
			<div class="message" role="status">${msg}</div>
		</g:if>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div><br>
		<g:form controller="incoming" >
			<div><input type="checkbox" name="waehlen" value="" id="waehlen" onClick="toggle(this)"/> Alle an-/abwählen</div><br>
			<g:each in="${filelist}" var="currentFile" status="i">
			    <p><g:checkBox name="currentFiles" value="${currentFile.getName()}" checked="false" onClick="deselect(this)"/>  ${currentFile.getName()}</p>
			</g:each>
			<br>
			<g:actionSubmit value="Starten" action="save"/>
		</g:form>
	</body>
</html>