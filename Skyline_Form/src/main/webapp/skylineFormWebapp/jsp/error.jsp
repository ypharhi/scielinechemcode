<%@ page isErrorPage="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
<title>Show Error Page</title>

    <link href="../skylineFormWebapp/CSS/comply_theme/Skyline_9.css"  onerror="this.href='./skylineFormWebapp/CSS/comply_theme/Skyline_9.css'" rel="stylesheet"  type="text/css">
	<link href="../skylineFormWebapp/CSS/comply_theme/jquery-ui.custom.css" onerror="this.href='./skylineFormWebapp/CSS/comply_theme/jquery-ui.custom.css'" rel="stylesheet" type="text/css"  />
	<script src="../skylineFormWebapp/deps/jquery-1.12.4.js" onerror="this.src='./skylineFormWebapp/deps/jquery-1.12.4.js'"></script>
	<script src="../skylineFormWebapp/deps/jquery-1.12.4.min.js"></script>
<script type="text/javascript">

	$(document).ready(function() { 
		if (window.self === window.top){    	
	    	
	    } 
	    else{
	    	$('#returnTo a').text('Close');
	    	$('#returnTo a').attr('onclick','closePage()')
	    } 
	});	
	
	function redirectLoginPage() {
		document.location.href = $('#loginPage').val();
	}
	
	function closePage() {
		if (parent.$('#prevDialog').length){
		   	parent.$('#prevDialog').dialog('close'); 
		}
		else{
			redirectLoginPage();
		}
	}
</script>
</head>
<body>
<spring:message code="loginPageProp" var="loginPageProp" />
 <input id="loginPage" type="hidden" value='/${loginPageProp}/'>
<table style="width: 100%; text-align: center;">  
    <tr>
        <td style="line-height: 40px;">&nbsp;</td>
    </tr>
    <tr>
        <td class="ui-widget-content ui-corner-all" colspan="10">
        <p class="cssLabel"><b>Sorry, an error occurred.</b></p>		
		<br>
		<p id="returnTo" class="cssLabel"><b>
		<a href="#" onclick="redirectLoginPage();" style="color: #1766ad;">click here to return to login page</a>
		</b></p>
		<br><br><br>
			<table width="100%" border="0" style="font-size:13px;">
				<tr valign="top">
				<td ><b>Error:</b></td>
				<td>${pageContext.exception}</td>
				</tr>
				<tr >
				<td><b>URI:</b></td>
				<td>${pageContext.errorData.requestURI}</td>
				</tr>
				<tr >
				<td><b>Status code:</b></td>
				<td>${pageContext.errorData.statusCode}</td>
				</tr>
				<tr>
				<td><b>Stack trace:</b></td>
				<td>
				
				<c:forEach var="trace" 
				         items="${pageContext.exception.stackTrace}">
				<p>${trace}</p>
				</c:forEach>
			
				</td>
				</tr>
			</table>
			<br>
			<br>
		
			<div id="printStackDiv" style="text-align: left;font-size:13px;">
			<p><b>Here is the exception stack trace: </b></p>					
				<% exception.printStackTrace(new java.io.PrintWriter(out)); %>
			</div>
        </td>
    </tr>
    
        
  

</table>

</body>
</html>