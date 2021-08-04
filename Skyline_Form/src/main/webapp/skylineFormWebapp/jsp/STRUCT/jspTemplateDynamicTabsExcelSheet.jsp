<%@ include file="../include/include.jsp"%>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=0.5, maximum-scale=0.5">
<title>Skyline ${browserTitle}</title>
<%@ include file="../include/includeCSS.jsp"%>
<script src="../skylineFormWebapp/deps/jquery-1.12.4.min.js"></script>
<script src="../skylineFormWebapp/deps/jquery-ui.custom_new.min.js"></script>
<script src="../skylineFormWebapp/deps/math.min.js"></script>

<%@ include file="../include/includeElement_js.jsp"%>
<%@ include file="../include/includeBL_js.jsp"%>
<%@ include file="../include/includeJS.jsp"%>
<%@ include file="../include/includeExtendedJS.jsp"%>
 
 
<script type="text/javascript">
	$(document).ready(function() {		
		@bm_list_ready@ 
		initPage();
	    renderElementAuthorizationImp();  
	    //$('.displayOnLoad').css('display','');
	});		
@bm_list_function@
</script>
</head>
@bm_list_html@
<body style="overflow-y: auto;">
	<input type="hidden" id="generalDisabledFlagParam" name="generalDisabledFlagParam" value="-1">
	<input type="hidden" id="userId" name="userId" value="${userId}"><input type="hidden" id="stateKey" name="stateKey" value="${stateKey}"><input type="hidden" id="formPathInfo" name="formPathInfo" value='${formPathInfo}'>
	<input type="hidden" id="formId" name="formId" value="${formId}">
	<input type="hidden" id="isNew" name="isNew" value="${isNew}">
	<input type="hidden" id="isStruct" name="isStruct" value="${isStruct}">
	<input type="hidden" id="formCode" name="formCode" value="${formCode}"><input type="hidden" id="formGeneralInfo" name="formGeneralInfo" value="">
	<input type="hidden" id="parentId" name="parentId" value="${PARENT_ID}">
	<input type="hidden" id="formTab" name="formTab" value="${formTab}">	
	<input type="hidden" id="backUrl" name="backUrl" value='../skylineFormWebapp/<%= session.getAttribute("homePage") %>'>
	<input type="hidden" id="newAddress" name="newAddress" value="">
	<input type="hidden" id="springMessages" name="springMessages" value='${springMessages}'>
	<input type="hidden" id="permissionsAccess" name="permissionsAccess" value="${PERMISSION_ACCESS}">	
	<input type="hidden" id="lastChangeUserId" name="lastChangeUserId" value="${lastChangeUserId}">
	<input type="hidden" id="lastChangeDate" name="lastChangeDate" value="${lastChangeDate}">
	<input type="hidden" id="permissionsSensLevelOrder" name="permissionsSensLevelOrder" value="${PERMISSION_SENSITIVITYLEVEL_ORDER}">
	<form id="doBackForm" action="doBack.request" method="post"><input type="hidden" id="formCode_request" name="formCode_request" value="${formCode}"><input type="hidden" id="stateKey_request" name="stateKey_request" value="${stateKey}"><input type="hidden" id="formCode_doBack" name="formCode_doBack"></form>
	<%@ include file="../include/includeBodyJS.jsp"%>
<!-- body -->
   <table style="width:100%;">
					<tr>
						<td class="top-bar-container">
							<div style="width:100%;">
								<%@ include file="./PageHeaderJsoTemplateForm.inc" %>
								<%@ include file="../include/includeMenu.jsp" %>
							</div>		
						</td>
					</tr>  	  
					<tr>
						<td class="ui-widget-content ui-corner-all innerTD" colspan="10">
							<table class="displayOnLoad" width="100%" style="display:none;">
								<tr>
									<td class="innerTD">
										<div id="tempalteTabs">
											<ul>
												@tempalteTabs@			
											</ul>
										
									<!--begin --><div class="tab-container" style="overflow-y: auto;">
												@body@
												<!--end --></div>
										</div>
									</td>
								</tr>
								<tr>
									<td class="submit-button-row">
											${bookmarkAuthorization}
											${Optional11}
											${Optional12}
											<button class="button" id="newButton" type="button" onclick="confirmWithOutSave(openWFDialog,[doNew]);">New</button>
											<button class="button" id="saveButton" perm_attr="cu" type="button" onclick="doSave('@afterSave@')" style="margin-left: 15px;"><spring:message code="Save" /></button>
											<button class="button" id="close_back" type="button" style="margin-left: 15px;" onclick="parent.$('#prevDialog').dialog('close');"><spring:message code="Close" /></button>
							   </tr>
							</table>
						</td>
					</tr>
				</table>			
</body>
</html>