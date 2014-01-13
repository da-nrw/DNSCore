<%@ page contentType="text/html; charset=UTF-8" %>
<html>
  <head>
    <meta name="layout" content="main" />
    <title>CBTalk</title>         
  </head>
  <body>
    <div class="body">
      <h1>CbTalk</h1>
      <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
      </g:if>
      <g:form action="save" method="post">
   <g:submitButton name="stopFactory" value="stop Factory" />
   <g:submitButton name="startFactory" value="start Factory" /> 
   <g:submitButton name="showActions" value="show Actions" /> 
   <g:submitButton name="gracefulShutdown" value="ContentBroker graceful shutdown" />   
</g:form>
      <g:each var="message" in="${messages}">
      	 <p>${message}</p>
      </g:each> 
         Momentan laufende Action des CB:
      <g:each var="ActionDescription" in="${myList}">
      	 <p>${ActionDescription}</p>
      </g:each> 
      <p>
Errors:      
<g:each var="Error" in="${errors}">
      	 <p>${Error}</p>
      </g:each> 
     </div>
  </body>
</html>