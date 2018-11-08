<html>
	<head>
		<title>Automatische Retrievalanforderung</title>
		<meta name="layout" content="main">
		<r:require modules="jqueryui,messagebox"/>
	</head>
<body>
	<div class="page-body">
		<div id="show-object" class="content scaffold-show" role="main">
			<div class="blue-box"></div>
			<h2>Hinweise zur automatischen Retrievalanforderung an die DNS </h2>
			<div id="warnung"></div>
			
				<ul class="property-list object property-list-position">
				
					<li><span class="property-value">
					Sie können das Retrieval von Objekten auch über externe Systeme erstellen lassen.
					</span></li>
					<li><span class="property-value">
					Übergeben Sie dafür einen JSON Request im POST (Aufbau analog des Ergebnisses der Statusabfrage) an die folgende <br>URL: 
					<b>https://Servername<g:createLink controller="automatedRetrieval" action="queueForRetrievalJSON"/></b><br>
					</span></li>
					<li><span class="property-value">Als Antwort erhalten Sie ein maschinenlesbares Ergebnis (JSON) über die Erstellung eines Abfragevorgangs<br></span></li>
				</ul>
				
				<form method="POST" action="">
					Identifier: <input id="identifier" name="identifier" type="text" value="" class="input-hoehe">
				 	<input type="submit" value="Als JSON senden (testweise)" class="button button-json"/> 
				</form>
			</div>
			<g:javascript>
		 		$('form').submit(function(e) {
					 e.preventDefault();
				    var identifier = $('#identifier').val();
				    var formData = { identifier: identifier };                
				  $.ajax({
				    type: 'POST',
				    cache: false,
				    dataType:'json',
				    data: JSON.stringify(formData),
				    contentType: 'application/json; charset=utf-8',
				    url: '<g:createLink controller="automatedRetrieval" action="queueForRetrievalJSON"/>',
				    success:
				      	function queuedFor(result) {
								var type = "error";
								if (result.success) type = "info";
								var messageBox = $("<div class='message-box' style='margin-top: 10px;' ></div>");
								$("#warnung").prepend(messageBox);
								messageBox.message({
									type: type, message: result.msg
								});
							}
				   });
				});
			</g:javascript> 
			
		</div>
	</body>
</html>
