<html>
	<head>
		<title>Verarbeitung SIP starten</title>
		<meta name="layout" content="main">
	</head>
	<body>
		<div class="page-body">
			<div class="blue-box"></div>
			<h2 id="page-header">Verarbeitung für abgelieferte SIP starten</h2> 
			<script type="text/javascript">
				function toggle(source) {
					if (document.getElementById('waehlen').checked) {
					  checkboxes = document.getElementsByName('currentFiles');
					  var i = 0;
					  while ( i < checkboxes.length) {  
						checkboxes[i].checked = true;
					  	i++;
					  }
					} else {
						checkboxes = document.getElementsByName('currentFiles');
						  var i = 0;
						  while ( i < checkboxes.length) {  
							checkboxes[i].checked = false;
						  	i++;
						  }
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
			<g:form controller="incoming" >
			<g:if test="${msg.isEmpty()}">
				<div class="abstand-oben">
					<strong>
						<input type="checkbox" name="waehlen" value="" id="waehlen" onClick="toggle(this)"/> Alle an-/abwählen
					</strong>
				</div><br>
				<g:each in="${filelist}" var="currentFile" status="i">
				    <p><g:checkBox name="currentFiles" value="${currentFile.getName()}" checked="false" onClick="deselect(this)"/> 
				     ${currentFile.getName()}</p>
				</g:each>
				<br>
				<g:actionSubmit value="Starten" action="save" class="style-buttons"/>
			</g:if>
			</g:form>
		</div>
	</body>
</html>