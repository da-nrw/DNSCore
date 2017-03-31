<!doctype html>
<html>
<head>
<meta name="layout" content="main" />
<title>Willkommen bei der DA-NRW Weboberfläche</title>
<style type="text/css" media="screen">
#controller-list ul {
	list-style-position: inside;
}

#controller-list li {
	line-height: 1.3;
	list-style-position: inside;
	margin: 0.5em;
}

.bs-example {
	margin: 1.2em;
}
</style>

<r:script>
				$(function() {
					$("#legend").accordion({ collapsible: true, active: false, autoHeight: false });
				});
				$(function() {
					$("#filter").accordion({ collapsible: true, active: false });
				});
</r:script>
	





<!--  <script type="text/javascript">
	$(document).ready(function() {
		var isMobile = window.matchMedia("only screen and (max-width: 760px)");

		if (isMobile.matches) {
			$("#accordion").accordion({
				collapsible : true
			});
		}
	});
</script>-->

</head>

<body>
	<h1 style="text-align: center; margin-bottom: 1em; font-size: 34px;">
		Willkommen
		${user}
		!
		<g:if test="${admin==1}"> (Administrator)</g:if>
	</h1>

	<div class="bs-example">
		<ul class="nav nav-tabs">
			<li class="active"><a data-toggle="tab" href="#sectionQueueEntryList">Bearbeitungsübersicht</a></li>
			<g:if test="${ admin==1}">
					<li><a data-toggle="tab" href="#sectionObjectListObject">Auswahl Objekte nach Formaten</a></li>
			</g:if>
			<li><a data-toggle="tab" href="#sectionQueueEntryListRequests">Entscheidungsübersicht</a></li>
			<li><a data-toggle="tab" href="#sectionObjectList">Eingelieferte Objekte (AIP)</a></li>
			<li><a data-toggle="tab" href="#sectionIncomingIndex">Verarbeitung für abgelieferte SIP starten</a></li>
			<li><a data-toggle="tab" href="#sectionOutgoingIndex">Objekt entnehmen (DIP)</a></li>
			<li class="dropdown">
				<a data-toggle="dropdown" class="dropdown-toggle" href="#">Hinweise zur Ansteuerung über externe Systeme<b class="caret"></b></a>
				<ul class="dropdown-menu">
					<li><a style="background-color: white;" data-toggle="tab" href="#dropdownAutomatedRetrieval">Erstellung von Retrievalanfragen </a></li>
					<li><a style="background-color: white;" data-toggle="tab" href="#dropdownStatus">Abfrage der Verarbeitung und Archivierung</a></li>
				</ul>
			<li><a data-toggle="tab" href="#sectionConversionPolicies">Konfigurierte Konversionen</a></li>
			<li><a data-toggle="tab" href="#sectionReport">Abfragen verarbeiten</a></li>
			<li><a data-toggle="tab" href="#sectionSystemEvent">System-Eventsteuerung</a></li>
			<g:if test="${ admin==1}">
				<li><a data-toggle="tab" href="#sectionCbtalk">Adminfunktionen</a></li>
			</g:if>
		</ul>
		<div class="tab-content">
			<div id="sectionQueueEntryList" class="tab-pane fade in active">
				<g:include controller="queueEntry" action="list" />
			</div>
			<div id="sectionObjectListObject" class="tab-pane fade">
				<g:include controller="object" action="listObjects" />
			</div>
			<div id="sectionQueueEntryListRequests" class="tab-pane fade">
				<g:include controller="queueEntry" action="listRequests" />
			</div>
			<div id="sectionObjectList" class="tab-pane fade">
				<g:include controller="object" action="list" />
			</div>
			<div id="sectionIncomingIndex" class="tab-pane fade">
				<g:include controller="incoming" action="index" />
			</div>
			<div id="sectionOutgoingIndex" class="tab-pane fade">
				<g:include controller="outgoing" action="index" />
			</div>
			<div id="dropdownAutomatedRetrieval" class="tab-pane fade">
				<g:include controller="automatedRetrieval" action="index" />
			</div>
			<div id="dropdownStatus" class="tab-pane fade">
				<g:include controller="status" action="teaser" />
			</div>
			<div id="sectionConversionPolicies" class="tab-pane fade">
				<g:include controller="conversionPolicies" action="list"/>
			</div>
			<div id="sectionReport" class="tab-pane fade">
				<g:include controller="report" action="index" />
			</div>
			<div id="sectionSystemEvent" class="tab-pane fade">
				<g:include controller="systemEvent" action="index" />
			</div>
			<div id="sectionCbtalk" class="tab-pane fade">
				<g:include controller="cbtalk" action="index" />
			</div>
		</div>
	</div>
</body>
</html>