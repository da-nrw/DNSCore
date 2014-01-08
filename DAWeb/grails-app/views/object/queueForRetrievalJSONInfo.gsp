<html>
<head>
	<title>Automatische Retrievalanforderung</title>
	<meta name="layout" content="main">
	<r:require modules="jqueryui,messagebox"/>
	</head>
<body>

<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
			</ul>
		</div>
<div id="show-object" class="content scaffold-show" role="main">
			<h1>Hinweise zum automatischen Retrievalanforderung an die DNS </h1>
	<ul class="property-list object">
			
						<li><span class="property-value">
						Sie können das Retrieval von Objekten auch über externe Systeme erstellen lassen.
						</span></li>
						<li><span class="property-value">
						Übergeben Sie dafür einen JSON Request im POST (Aufbau analog des Ergebnisses der Statusabfrage) an die folgende URL: 
						<b>https://Servername<g:createLink controller="object" action="queueForRetrievalJSON"/></b><br>
						</span></li>
						<span class="property-value"><li>Als Antwort erhalten Sie ein maschinenlesbares Ergebnis (JSON) über die Erstellung eines Abfragevorgangs<br></li>
						</span>
						</ul>
<r:script>
$('form').submit(function(e) {
	 e.preventDefault();
    var identifier = $('#identifier').val();
    alert (identifier)
    var formData = { identifier: identifier };                
  $.ajax({
    type: 'POST',
    cache: false,
    dataType:'json',
    data: JSON.stringify(formData),
    contentType: 'application/json; charset=utf-8',
    url: '<g:createLink controller="object" action="queueForRetrievalJSON"/>',
    success:
      	function queuedFor(result) {
				var type = "error";
				if (result.success) type = "info";
				var messageBox = $("<div class='message-box'></div>");
				$("#page-body").prepend(messageBox);
				messageBox.message({
					type: type, message: result.msg
				});
			}
   });
});
</r:script>
<form method="POST" action="">
Identifier:<input id="identifier" name="identifier" type="text" value="">
<input type="submit" value="Als JSON senden (testweise)" class="button"/> 
</form>
<!-- {"urn":"urn:nbn:de:hbz:6:1-20814","contractor":"TEST","origName":"scuy_test_2013-20-12_1","identifier":"131614-2013122027085","status":"archived - but in progress","packages":["1"]} --></p>
</div>
</body>
</html>
