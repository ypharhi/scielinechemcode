<%@ include file="../include/include.jsp"%>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Skyline ${browserTitle}</title>
<%@ include file="../include/includeCSS.jsp"%>	
<script src="../skylineFormWebapp/deps/jquery-1.12.4.min.js"></script>
<script src="../skylineFormWebapp/deps/jquery-ui.custom_new.min.js"></script>
<%@ include file="../include/includeElement_js.jsp"%>
<%@ include file="../include/includeBL_js.jsp"%>
<%@ include file="../include/includeJS.jsp"%>
<%@ include file="../include/includeExtendedJS.jsp"%>
<script>

	$(document).ready(function() { 
		@bm_list_ready@ 
		initPage();
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
	<input type="hidden" id="generalDisabledFlagParam" name="generalDisabledFlagParam" value="-1">
	<input type="hidden" id="userId" name="userId" value="${userId}"><input type="hidden" id="stateKey" name="stateKey" value="${stateKey}"><input type="hidden" id="formPathInfo" name="formPathInfo" value='${formPathInfo}'>
	<input type="hidden" id="formId" name="formId" value="${formId}">
	<input type="hidden" id="formCode" name="formCode" value="${formCode}"><input type="hidden" id="formGeneralInfo" name="formGeneralInfo" value="">
	<input type="hidden" id="parentId" name="parentId" value="${PARENT_ID}">	
	<input type="hidden" id="backUrl" name="backUrl" value='../skylineFormWebapp/<%= session.getAttribute("homePage") %>'>
	<input type="hidden" id="springMessages" name="springMessages" value='${springMessages}'>
	<input type="hidden" id="permissionsAccess" name="permissionsAccess" value="${PERMISSION_ACCESS}">	
	<input type="hidden" id="lastChangeUserId" name="lastChangeUserId" value="${lastChangeUserId}">
	<input type="hidden" id="lastChangeDate" name="lastChangeDate" value="${lastChangeDate}">
	<form id="doBackForm" action="doBack.request" method="post"><input type="hidden" id="formCode_request" name="formCode_request" value="${formCode}"><input type="hidden" id="stateKey_request" name="stateKey_request" value="${stateKey}"><input type="hidden" id="formCode_doBack" name="formCode_doBack"></form>
	<%@ include file="../include/includeBodyJS.jsp"%>
	<!-- body -->
   <table style="width:100%;">								
					<tr>
						<td class="ui-widget-content ui-corner-all innerTD" colspan="10">
							<table class="displayOnLoad" width="100%" style="display:none;">
								<tr>
									<td class="innerTD">
									 <!--begin --><div style="height: calc(100vh - 50px);overflow-y: auto;">
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
										<button  type="button" class="button" perm_attr="cu" id="save_" onclick="doSave('@afterSave@')"><spring:message code="Save" text="Save" /></button>
										<button id="close_back" class="button" type="button" style="margin-left: 15px;" onclick="parent.$('#prevDialog').dialog('close');"><spring:message code="Close" text="Close" /></button>
									</td>									
								</tr>								
							</table>
						</td>
					</tr>
				</table>					
</body>
</html>