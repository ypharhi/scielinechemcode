<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">   
	<title>
	<spring:message code="appTitle" text="" /> <spring:message code="Env" text="" />
	</title>
	<link rel="icon" href="./skylineFormWebapp/images/favicon.ico?<spring:message code="Env" text="" />" />
	<link href="./skylineFormWebapp/CSS/comply_theme/login.css" rel="stylesheet"  type="text/css">
	<link href="./skylineFormWebapp/CSS/comply_theme/jquery-ui.custom.css" rel="stylesheet" type="text/css" media="all" />
	<script type="text/javascript" src="skylineFormWebapp/deps/jquery-1.12.4.js"></script>
	<script type="text/javascript" src="skylineFormWebapp/deps/jquery-ui.custom.js"></script>
	<script type="text/javascript" src="skylineFormWebapp/deps/url.min.js"></script>
	<script src="skylineFormWebapp/js/generalFunc.js" type="text/javascript"></script>
	<script type="text/javascript">
	$(document).ready( function() { 
		
// 		PERMISSION_DENIED=Permission denied!
// 		PERMISSION_DENIED_TAB=Permission denied on new tab! Please make login.
// 		PERMISSION_DENIED_MULTI_USER=Permission denied. You are already logged in with a different user!

		var PERMISSION_DENIED_FLAG = false;
		if(url().indexOf("PERMISSION_DENIED=1") > 0) {
			PERMISSION_DENIED_FLAG=true;
			displayAlertDialog("Permission denied!");
		}
		
		if(url().indexOf("PERMISSION_DENIED_TAB=1") > 0) {
			PERMISSION_DENIED_FLAG=true;
			displayAlertDialog("Permission denied on new tab! Please make login.");
		}
		
		if(url().indexOf("PERMISSION_DENIED_MULTI_USER=1") > 0) {
			PERMISSION_DENIED_FLAG=true;
			displayAlertDialog("Permission denied. You are already logged in with a different user!");
		}
		
		
		//$('.button').button();
		if (parent.$('#prevDialog').length){
	   		  parent.$('#prevDialog').dialog('close'); 
	    }
		var dialog = $("#changePasswordDialog").dialog({
           autoOpen: false,
           height: 210,
           width: 350,
           title: $('#txtSysMessage').val(),
           modal: true,
           buttons: {
               "Save":function() {
	               if($('#newPass').val() != $('#confirmPass').val()){
	               	displayAlertDialog('Passwords do not match');
	               }
	               else{
	                 $('[name="oldPassword"]').val($('#oldPass').val());
	              	 $('[name="newPassword"]').val($('#newPass').val());
	              	 $('[name="txtUser"]').val($('#txtUserName').val());
	              	 $('[name="txtPassword"]').val($('#newPass').val());
	              	 $('#form1').attr('action','changePassword.request');
	              	 $('#form1').submit();	             
	               }
                   
               },
               Cancel: function() {                  
                   dialog.dialog("close");
               }
           },
           close: function() {               
           }
        });
		try
		{
			checkBrowserSupport();
			$('#txtUser').focus();
		}
		catch(e){}
		
   		var isPwdChange = ($('#txtIsChangePassword').val() == '1') ? true : false;
   		
		var msg = $('#txtSysMessage').val();
		
		if (msg != null && msg != 'null' && msg != '' && !isPwdChange)
		{
			displayAlertDialog(msg);
		}
  
		
		if(isPwdChange){
			$('#changePasswordDialog').dialog('open');
			return;
		}	
		
		if (typeof(Storage) !== "undefined") {
			if(window.location.href.indexOf('exit') == -1){
				if (msg != null && msg != 'null' && msg != '')
				{
					localStorage.clear();
				}			
				else
				{			
				 	if(!PERMISSION_DENIED_FLAG && (localStorage.getItem("skylineUserName") != null) && (localStorage.getItem("skylineUserName") != '')){
					   if((localStorage.getItem("skylinePass") != null) && (localStorage.getItem("skylinePass") != '')){
						   $('[name="txtUser"]').val(localStorage.getItem("skylineUserName"));
						   $('[name="txtPassword"]').val(localStorage.getItem("skylinePass"));
						   $('body').css('display','none');
						   $('#form1').submit();
					   }
			  		 }
				}
			}
		}
		
		// we don't need the login img during develop 
		if(url().indexOf("localhost") > 0) {
			$("body").removeClass("login_body");
		}
		
	});
	</script>
	<script type="text/javascript">
    function OnEnterKeyDown()
    { 
        if (event.keyCode == 13)
        {
			Login();
			event.returnValue = false;
        }
    }
    
	function Login() 
	{ 
		 if($('[name="txtUser"]').val() == '' || $('[name="txtPassword"]').val() == '') {
			 displayAlertDialog("Please, enter user name or/and password!");
			 return;
			} 
			if($('#rememberMe').is(":checked")){
				if (typeof(Storage) !== "undefined") {  
				    localStorage.setItem("skylineUserName", $('[name="txtUser"]').val()); 
				    localStorage.setItem("skylinePass", $('[name="txtPassword"]').val()); 
				}
			} 
			$('#actionid').val('Login');
			$('#form1').submit();
	}
    
	function checkBrowserSupport() // yp 01072019 change name from getBrowserSupport and make a call to generalfunc getBrowserSupport
    {
		var bname = getBrowserName();
		if(bname == 'mozilla' || bname == 'chrome' || bname == 'msie' ) {
			 document.getElementById('browserName').value = bname;
        }       
        else
        {
			displayAlertDialog("This Site requires IE v.11 or higher, Firefox or Chrome");

			$('[name="txtUser"]').prop('disabled', true);
			$('[name="txtPassword"]').prop('disabled', true);
			$('[name="login"]').prop('disabled', true);       
        }
    }
    
    function focusInput(currEvent)
    {       
    	
    	var event = window.event || currEvent;
    	var e = event.target || event.srcElement;
    	//alert(window.event + "  :  " + e);
    	if (e == null)
    	{
    		return;
    	}
    	//alert(event.type);
    	if (event.type == 'focus')
    	{
    		currColor = e.style.borderColor;
    		e.style.border = '1px solid';
    		e.style.borderColor = '#5897fb';
    	}
    	else if(event.type == 'blur') //for support to IE11(cause Event Object may return other event.types)
    	{
    		e.style.borderColor = currColor;
    		e.style.border = '#aaa 1px solid';
    	}
    	
    }
	</script>
	<style>
	
	</style>
</head>
<%
boolean isExit = (request.getParameter("exit") != null) ? true : false;
String curTabStateKey = (request.getParameter("curTabStateKey") == null)?"":request.getParameter("curTabStateKey");
if (isExit)
{
	session.setAttribute("CURRENT_TAB_STATE_KEY", curTabStateKey);
	session.setAttribute("userId", null);
	session.invalidate(); 
	
}
%>
<body oncontextmenu="return false;" onkeydown="OnEnterKeyDown();" class="login_body">
<div class="loginmodal-container">					
				 <form name="form1" id="form1" method="post" action="loginAction.request">
				 <div class="login_top_section">
					<img src="./skylineFormWebapp/images/skyline_logo_login.svg" border="0" style="width: 175px;height: auto;">
					</br>
					<label class="title"><spring:message code="EnvTitle" text="" /><spring:message code="Env" text="" /></label>
				</div>
					<input type="text" name="txtUser" placeholder="User name">
					<input type="password" name="txtPassword" placeholder="Password" >
					<input type="hidden" name="oldPassword">
					<input type="hidden" name="newPassword">				
					<input type="button" name="login" class="login loginmodal-submit" value="Login" onclick="Login();">
					<input id="rememberMe" type="checkbox"><label class="checkbox_label">Remember me</label>
					 
					 
					 
					<input type="hidden" name="actionid" id="actionid">
					<input type="hidden" name="browserName" id="browserName">
					<input type="hidden" id="txtIsChangePassword" name="txtIsChangePassword" value='${CHANGE_PASSWORD}'/>
					<input type="hidden" id="txtUserId" name="txtUserId" value='${USER_ID}'/>
					<input type="hidden" id="txtUserName" name="txtUserName" value='${lastUserName}'/>
					<input type="hidden" id="txtSysMessage" name="txtSysMessage" value='${msg}' />
					<input type="hidden" name="txtTabId" id="txtTabId" value="" >
					
					
				  </form>					
				  
				</div>

	<div id="changePasswordDialog">
		<lable class="cssStaticData "> <spring:message code="Password"
			text="Password" />:</lable>
		<input id="oldPass" type="password"
			style="width: 150px; height: 22px;"> <br>
		<lable class="cssStaticData "> <spring:message code="New Password"
			text="New Password" />:</lable>
		<input id="newPass" type="password"
			style="width: 150px; height: 22px;"> <br>
		<lable class="cssStaticData "> <spring:message
			code="Confirm Password" text="Confirm Password" />:</lable>
		<input id="confirmPass" type="password"
			style="width: 150px; height: 22px;">
	</div>
</body>
</html>
