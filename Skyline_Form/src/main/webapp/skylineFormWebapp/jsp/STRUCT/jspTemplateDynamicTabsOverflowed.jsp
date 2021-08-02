<%@ include file="../include/include.jsp"%>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Skyline ${browserTitle}</title>
<%@ include file="../include/includeCSS.jsp"%>	
<script src="../skylineFormWebapp/deps/jquery-1.12.4.min.js"></script>
<script src="../skylineFormWebapp/deps/jquery-ui.custom_new.min.js"></script>

<%@ include file="../include/includeJS.jsp"%>
<%@ include file="../include/includeExtendedJS.jsp"%>
<%@ include file="../include/includeElement_js.jsp"%>
<%@ include file="../include/includeBL_js.jsp"%>
<%@ include file="../include/includeExcelSheet_js.jsp"%>

<script>
	@bm_list_function@
	$(document).ready(function() {
		if($('#previousEntityFormId').val() == '') {//change the button to be hidden
			$('#previousEntityButton').css('display', 'none');
		} else if($('#previousEntityFormId').val()=='-1'){//disable the previous button
			$('#previousEntityButton').addClass('disablePage');
		} else {
			$('#previousEntityButton').removeClass('disablePage');
		}
		if($('#nextEntityFormId').val() == '') {//change the button to be hidden
			$('#nextEntityButton').css('display', 'none');
		} else if($('#nextEntityFormId').val()=='-1'){//disable the previous button
			$('#nextEntityButton').addClass('disablePage');
		} else {
			$('#nextEntityButton').removeClass('disablePage');
		}
		@bm_list_ready@ 
		initPage();
	    renderElementAuthorizationImp();
	    //$('.displayOnLoad').css('display','');
	});		
</script>
</head>
@bm_list_html@
<body style="overflow-y: auto;">
	<input type="hidden" id="generalDisabledFlagParam" name="generalDisabledFlagParam" value="-1">
	<input type="hidden" id="userId" name="userId" value="${userId}"><input type="hidden" id="stateKey" name="stateKey" value="${stateKey}"><input type="hidden" id="formPathInfo" name="formPathInfo" value='${formPathInfo}'>
	<input type="hidden" id="formId" name="formId" value="${formId}">
	<input type="hidden" id="isNew" name="isNew" value="${isNew}">
	<input type="hidden" id="isStruct" name="isStruct" value="${isStruct}">
	<input type="hidden" id="formCode" name="formCode" value="${formCode}">
	<input type="hidden" id="formGeneralInfo" name="formGeneralInfo" value="">
	<input type="hidden" id="parentId" name="parentId" value="${PARENT_ID}">
	<input type="hidden" id="formTab" name="formTab" value="${formTab}">
	<input type="hidden" id="previousEntityFormId" name="previousEntityFormId" value="${previousEntityFormId}">
	<input type="hidden" id="nextEntityFormId" name="nextEntityFormId" value="${nextEntityFormId}">	
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
											<div style="float: left;">
												<a href="#" ATTR_INFO_PAGE_HREF="1" style="display:none"></a>
												<div style="overflow-y: auto;" ATTR_INFO_PAGE_DIV="1"> 
													<p id="pDisplayPageInfoOnAjaxChange" style="color:blue;"></p><p id="SET_PAGEINFO_ONAJAXCHANGE"></p>
												</div>
											</div>
											<div class="bottom-page-buttons">
												<button class="button" id="previousEntityButton" type="button" onclick="confirmWithOutSave(doNavigatePrevious);">Previous</button>
												${bookmarkAuthorization}
												${Optional11}
												${Optional111}
												${Optional12}
												<button class="button" id="newButton" type="button" onclick="confirmWithOutSave(openWFDialog,[doNew]);">New</button>
	<!-- 											<button class="button" id="saveButton" type="button" perm_attr="cu" onclick="doSave('@afterSave@')" style="margin-left: 15px;"><spring:message code="Save" /></button>											 -->
												<button class="button" id="saveButton" type="button" perm_attr="cu" onclick="doSave('@afterSave@')" style="margin-left: 15px;"><spring:message code="Save" /></button>																						
												<button class="button" id="close_back" type="button" style="margin-left: 15px;" onclick="parent.$('#prevDialog').dialog('close');"><spring:message code="Close" /></button>
												<button class="button" id="nextEntityButton" type="button" onclick="confirmWithOutSave(doNavigateNext);">Next</button>
											</div>
							   </tr>
							</table>
						</td>
					</tr>
				</table>			
</body>
</html>