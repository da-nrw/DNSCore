
		/**
		 * Display the iDrop lite gui, passing in the given irods base collection name
		 */




function showIdropLite( idropLiteUrl) {
	
	var idropLiteSelector = "#idropLiteArea";
	
	

// first hide Browse Data Details table
$("#toggleBrowseDataDetailsTable").hide('slow');
$("#toggleBrowseDataDetailsTable").width = "0%";
$("#toggleBrowseDataDetailsTable").height = "0%";

//lcShowBusyIconInDiv(idropLiteSelector);
var params;

var jqxhr = $.post(idropLiteUrl, params, function(data, status, xhr) {
//lcClearDivAndDivClass(idropLiteSelector);
}, "html").error(function(xhr, status, error) {

//setMessageInArea(idropLiteSelector, xhr.responseText);

}).success(
function(data) {

var dataJSON = jQuery.parseJSON(data);
var appletDiv = $("#idropLiteArea");
$(appletDiv).append(
"<div id='appletMenu'>iDropLite gestartet (Wenn iDropLite nicht geladen wird, ueberpruefen Sie bitte Ihre Browsereinstellungen und lassen Sie Java-Applets zu)</div>")
var appletTagDiv = document.createElement('div');
appletTagDiv.setAttribute('id', 'appletTagDiv');
var a = document.createElement('applet');
appletTagDiv.appendChild(a);
a.setAttribute('code', dataJSON.appletCode);
a.setAttribute('codebase', dataJSON.appletUrl);
a.setAttribute('archive', dataJSON.archive);
a.setAttribute('width', 600);
a.setAttribute('height', 600);
var p = document.createElement('param');
p.setAttribute('name', 'mode');
p.setAttribute('value', dataJSON.mode);
a.appendChild(p);
p = document.createElement('param');
p.setAttribute('name', 'host');
p.setAttribute('value', dataJSON.host);
a.appendChild(p);
p = document.createElement('param');
p.setAttribute('name', 'port');
p.setAttribute('value', dataJSON.port);
a.appendChild(p);
p = document.createElement('param');
p.setAttribute('name', 'zone');
p.setAttribute('value', dataJSON.zone);
a.appendChild(p);
p = document.createElement('param');
p.setAttribute('name', 'user');
p.setAttribute('value', dataJSON.user);
a.appendChild(p);
p = document.createElement('param');
p.setAttribute('name', 'password');
p.setAttribute('value', dataJSON.password);
a.appendChild(p);
p = document.createElement('param');
p.setAttribute('name', 'absPath');
p.setAttribute('value',  dataJSON.absolutePath);
a.appendChild(p);
p = document.createElement('param');
p.setAttribute('name', 'displayMode');
p.setAttribute('value', 1);
a.appendChild(p);
p = document.createElement('param');
p.setAttribute('name', 'uploadDest');
p.setAttribute('value', dataJSON.absolutePath);

a.appendChild(p);
p = document.createElement('param');
p.setAttribute('name', 'defaultStorageResource');
p.setAttribute('value',
dataJSON.defaultStorageResource);
a.appendChild(p);
appletDiv.append(appletTagDiv);

$("#idropLiteArea").removeAttr('style');

}).error(function(xhr, status, error) {
//setMessageInArea(idropLiteSelector, xhr.responseText);
});

}