<%@ include file="../include/include.jsp"%>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Skyline ${browserTitle}</title> 

<link href="../skylineFormWebapp/CSS/comply_theme/app.css?<spring:message code="Env" text="" />" rel="stylesheet" type="text/css">
<link href="../skylineFormWebapp/dist/themes/default/style.min.css?<spring:message code="Env" text="" />" rel="stylesheet">

<script src="../skylineFormWebapp/js/properties.js" ></script>

<script src="../skylineFormWebapp/deps/jquery-1.12.4.min.js"></script>
<script src="../skylineFormWebapp/deps/jquery-ui.custom_new.min.js"></script>

<script src="../skylineFormWebapp/deps/traits.js"></script>
<script src="../skylineFormWebapp/dist/jstree.min.js"></script>
<script src="../skylineFormWebapp/deps/jquery.ui-contextmenu.min.js"></script>
<script src="../skylineFormWebapp/js/element_js/ElementTreeImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementAuthorizationImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/bl_General_js/generalBL<spring:message code="generalBLMin" text="." />js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementInputImp.js?<spring:message code="Env" text="" />"></script>

<script src="../skylineFormWebapp/js/generalFunc<spring:message code="generalFuncMin" text="." />js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementDataTableApiImp<spring:message code="ElementDataTableApiImpMin" text="." />js?<spring:message code="Env" text="" />"></script>

<script>

	$(document).ready(function() { 
		@bm_list_ready@ 
		
		//initPage();
		initForm();	
		
	    if (window.self === window.top){    	
	    	 if($('#backUrl').val() != ""){	    	
	    		 $('#close_back').attr('onclick','$("#doBackForm").submit()');	    		 
	    	 }
	    	 else{
	    		 $('#close_back').css('display','none');
	    	 }
	    } 
	    else{ 
	    	 $('#homePageHeaderJspTempalte').css('display','none');
	    	 $('#homePageHeaderJspTempalte').css('cursor','pointer');
	    	 $('#backHeaderJspTempalte').css('display','none');
	    	 $('#backHeaderJspTempalte').css('cursor','pointer');  
	    } 
	    renderElementAuthorizationImp();
	    //$('.displayOnLoad').css('display','');
	});
@bm_list_function@
</script>
</head>
@bm_list_html@
<body>
	<!-- <input type="hidden" id="generalDisabledFlagParam" name="generalDisabledFlagParam" value="-1"> -->
	<%-- <input type="hidden" id="isStruct" name="isStruct" value="${isStruct}">
	<input type="hidden" id="isNew" name="isNew" value="${isNew}"> --%>
	<input type="hidden" id="userId" name="userId" value="${userId}">
	<input type="hidden" id="stateKey" name="stateKey" value="${stateKey}">
	<%-- <input type="hidden" id="formPathInfo" name="formPathInfo" value='${formPathInfo}'> --%>
	<input type="hidden" id="formId" name="formId" value="${formId}">
	<input type="hidden" id="formCode" name="formCode" value="${formCode}"><input type="hidden" id="formGeneralInfo" name="formGeneralInfo" value="">
	<%-- <input type="hidden" id="parentId" name="parentId" value="${PARENT_ID}">	 --%>
	<%-- <input type="hidden" id="backUrl" name="backUrl" value='../skylineFormWebapp/<%= session.getAttribute("homePage") %>'>
	<input type="hidden" id="springMessages" name="springMessages" value='${springMessages}'> --%>
	<%-- <input type="hidden" id="permissionsAccess" name="permissionsAccess" value="${PERMISSION_ACCESS}"> --%>
	<%-- <form id="doBackForm" action="doBack.request" method="post">
	<input type="hidden" id="formCode_request" name="formCode_request" value="${formCode}">
	<input type="hidden" id="stateKey_request" name="stateKey_request" value="${stateKey}">
	<input type="hidden" id="formCode_doBack" name="formCode_doBack"></form> --%>
	 <%@ include file="../include/includeBodyJS.jsp"%>
	<!-- body -->
   <table style="width:100%;">
					<tr>
						<%-- <td>
							<%@ include file="./PopupHeader.inc" %>
						</td> --%>
					</tr>										
					<tr>
						<td class="ui-widget-content ui-corner-all innerTD" colspan="10">
							<table class="displayOnLoad" width="100%" >
								<tr>
									<td class="innerTD">
									 <!--begin --><div style="height: calc(100vh - 55px);overflow-y: auto;">
									@body@
									<!--end --></div>
									</td>
								</tr>	
								<tr>
						<td class="submit-button-row">
							<div style="float: left;">
								<a href="#" ATTR_INFO_PAGE_HREF="1" style="display:none"></a>
								<div style="overflow-y: auto;" ATTR_INFO_PAGE_DIV="1"> 
									<p id="pDisplayPageInfoOnAjaxChange" style="color:blue;"></p><p id="SET_PAGEINFO_ONAJAXCHANGE"></p>
								</div>
							</div>
							${bookmarkAuthorization}
							${Optional11}
							${Optional12}
							<button id="close_back" class="button" type="button" style="margin-left: 15px;" onclick="parent.$('#prevDialog').dialog('close');"><spring:message code="Close" text="Close" /></button>
						</td>									
								</tr>								
							</table>
						</td>
					</tr>
				</table>					
</body>
</html>