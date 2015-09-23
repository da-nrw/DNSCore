<html>
<head>
	<title>Bericht verarbeiten</title>
	<meta name="layout" content="main">
</head>

<g:if test="${msg}">
			<div class="message" role="status">${msg}</div>
			</g:if>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>
		
<br>
<g:form controller="report" method="POST" action="save" enctype="multipart/form-data">
				<input type="file" name="file"/>
<input type="submit" value="Hochladen" />
</g:form>
		

<p>
<script language="JavaScript">
function toggle(source) {
	  checkboxes = document.getElementsByName('currentFiles');
	  for(var i in checkboxes)
	    checkboxes[i].checked = source.checked;
}
</script>
	
<form id="form2" action="decider" >
  
<div id="items"><ul>
<g:each in="${filelist}" var="currentFile" status="i">
    <li><label><g:checkBox name="currentFiles" value="${currentFile.getName()}" checked="false" /><a href="${httpurl + "/" + currentFile.getName()}">${currentFile.getName()}</a></label></li>
	<g:if test="${i == filelist.size() - 1}">
        <div><label><input type="checkbox"  onClick="toggle(this)"/>Alle an-/abwählen</div></label><br>
    </g:if>
</g:each>
</ul></div>

Aktion:<br>
<g:select name="answer" from="${['start': 'Erneut generieren', 'retrieval': 'Retrieval', 'delete': 'Löschen']}" optionKey="key" optionValue="value"/>
<g:actionSubmit value="Starten" action="decider"/></form>
<br>
EXCEL CSV Datei mit Spaltenkopf:<br>
identifier;origName;statuscode;erfolg;bemerkung
</body>