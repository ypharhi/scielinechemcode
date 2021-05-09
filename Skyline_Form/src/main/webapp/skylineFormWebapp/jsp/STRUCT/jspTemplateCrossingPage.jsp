<%@ include file="../include/include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Skyline ${browserTitle}</title>
<%@ include file="../include/includeCSS.jsp"%>	
<script src="../skylineFormWebapp/deps/jquery-1.12.4.min.js"></script>
<script src="../skylineFormWebapp/deps/jquery-ui.custom_new.min.js"></script>
<%@ include file="../include/includeElement_js.jsp"%>
<%@ include file="../include/includeBL_js.jsp"%>
<%@ include file="../include/includeJS.jsp"%>
<%@ include file="../include/includeExtendedJS.jsp"%>

<style>
.DivDataStability {
    width: 100%;
    /* height: 100px; */
    overflow: auto;
    scrollbar-arrow-color: #006AAA;
    scrollbar-base-color: #006AAA;
    scrollbar-face-color: #FFFFFF;
    scrollbar-shadow-color: #BEC6DA;
    scrollbar-highlight-color: #FFFFFF;
    scrollbar-3dlight-color: #BEC6DA;
    scrollbar-darkshadow-color: #FFFFFF;
    scrollbar-track-color: #FAFAFA;
}
	 
.cssTableColumnFirstRowMain {
    height: 27px;
    font-size: 18px;
    font-family: Verdana, Tahoma, Sans-Serif;
    color: #000000;
    text-align: center;
    cursor: default;
    background-color: #D3D3D3;
    direction: ltr;
    
}

.TDVMainLine {
    font-size: 9pt;
    font-family: Verdana, Tahoma, Sans-Serif;
    border-top: 1px solid gray;
    border-left: 1px solid gray;
    border-bottom: 1px solid gray;
    border-right: 1px solid gray;
    padding: 2px;
    cursor: default;
}

.TDHMain {
    font-size: 10pt;
    border-top: 1px solid gray;
    border-left: 1px solid gray;
    border-bottom: 1px solid gray;
    border-right: 1px solid gray;
    text-align: center;
    background-color: #D3D3D3;
    vertical-align: middle;
} 
</style>
<script type="text/javascript">

	$(document).ready(function() { 
		@bm_list_ready@ 
		initPage();
	    if (window.self === window.top){    	
	    	 if($('#backUrl').val() != ""){    	    
	    		 //$('#close_back').attr('onclick','window.location.href = "' + $('#backUrl').val() + '"');
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
<body style="overflow-y: hidden;">
	<input type="hidden" id="generalDisabledFlagParam" name="generalDisabledFlagParam" value="-1">
	<input type="hidden" id="userId" name="userId" value="${userId}"><input type="hidden" id="stateKey" name="stateKey" value="${stateKey}"><input type="hidden" id="formPathInfo" name="formPathInfo" value='${formPathInfo}'>
	<input type="hidden" id="formId" name="formId" value="${formId}">
	<input type="hidden" id="isNew" name="isNew" value="1">
	<input type="hidden" id="formCode" name="formCode" value="${formCode}">
	<input type="hidden" id="formGeneralInfo" name="formGeneralInfo" value="">
	<input type="hidden" id="parentId" name="parentId" value="${PARENT_ID}">	
	<input type="hidden" id="backUrl" name="backUrl" value='../skylineFormWebapp/<%= session.getAttribute("homePage") %>'>
	<input type="hidden" id="springMessages" name="springMessages" value='${springMessages}'>
	<input type="hidden" id="permissionsAccess" name="permissionsAccess" value="${PERMISSION_ACCESS}">
	<input type="hidden" id="lastChangeUserId" name="lastChangeUserId" value="${lastChangeUserId}">
	<input type="hidden" id="lastChangeDate" name="lastChangeDate" value="${lastChangeDate}">
	<form id="doBackForm" action="doBack.request" method="post">
	<input type="hidden" id="formCode_request" name="formCode_request" value="${formCode}">
	<input type="hidden" id="stateKey_request" name="stateKey_request" value="${stateKey}">
	<input type="hidden" id="formCode_doBack" name="formCode_doBack">
	</form>
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
									 <!--begin --><div style="height: calc(100vh - 200px);overflow-y: auto;">
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
											<button class="button" type="button" id="saveAndOpen_" perm_attr="cu" onclick="doSave('save_and_forward')"><spring:message code="SaveAndOpen" text="Save & Open" /></button>
											<button class="button" type="button" id="saveAndClose_" perm_attr="cu" onclick="doSave('save_and_close')" style="margin-left: 15px;"><spring:message code="SaveAndClose" text="Save & Close" /></button>
											<button class="button" id="close_back" type="button" style="margin-left: 15px;" onclick="parent.$('#prevDialog').dialog('close');"><spring:message code="Close" text="Close" /></button>
									</td>									
								</tr>								
							</table>
						</td>
					</tr>
				</table>			
</body>
</html>