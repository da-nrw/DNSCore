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
      Momentan laufende Action des CB:
      <g:each var="ActionDescription" in="${myList}">
      	 <p>${ActionDescription}</p>
      </g:each> 
     </div>
  </body>
</html>