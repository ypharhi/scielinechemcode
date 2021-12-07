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
<style>
#inner-main-table {
	width: 95%;
	/*   border: 1px solid red;  */
	/*   padding : 5px;  */
}
</style>
<script>

${bookmark111_ready} ${bookmark112_ready} ${bookmark113_ready}
${bookmark114_ready} ${bookmark121_ready} ${bookmark122_ready}
${bookmark123_ready} ${bookmark124_ready} ${bookmark131_ready}
${bookmark132_ready} ${bookmark133_ready} ${bookmark134_ready}
${bookmark141_ready} ${bookmark142_ready} ${bookmark143_ready}
${bookmark144_ready} ${bookmark151_ready} ${bookmark152_ready}
${bookmark153_ready} ${bookmark154_ready} ${bookmark161_ready}
${bookmark162_ready} ${bookmark163_ready} ${bookmark164_ready}
${bookmark171_ready} ${bookmark172_ready} ${bookmark173_ready}
${bookmark174_ready} ${bookmark181_ready} ${bookmark182_ready}
${bookmark183_ready} ${bookmark184_ready} ${bookmark191_ready}
${bookmark192_ready} ${bookmark193_ready} ${bookmark194_ready}

	$(document).ready(function() { 
 
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
	    
	    //align radio fix
	    $(":radio").closest('td').css('vertical-align','bottom').css('padding-bottom','2px');	});
	
	${bookmark111_function} ${bookmark112_function} ${bookmark113_function}
	${bookmark114_function} ${bookmark121_function} ${bookmark122_function}
	${bookmark123_function} ${bookmark124_function} ${bookmark131_function}
	${bookmark132_function} ${bookmark133_function} ${bookmark134_function}
	${bookmark141_function} ${bookmark142_function} ${bookmark143_function}
	${bookmark144_function} ${bookmark151_function} ${bookmark152_function}
	${bookmark153_function} ${bookmark154_function} ${bookmark161_function}
	${bookmark162_function} ${bookmark163_function} ${bookmark164_function}
	${bookmark171_function} ${bookmark172_function} ${bookmark173_function}
	${bookmark174_function} ${bookmark181_function} ${bookmark182_function}
	${bookmark183_function} ${bookmark184_function} ${bookmark191_function}
	${bookmark192_function} ${bookmark193_function} ${bookmark194_function} 

</script>
</head>

${bookmark111_html} ${bookmark112_html} ${bookmark113_html}
${bookmark114_html} ${bookmark121_html} ${bookmark122_html}
${bookmark123_html} ${bookmark124_html} ${bookmark131_html}
${bookmark132_html} ${bookmark133_html} ${bookmark134_html}
${bookmark141_html} ${bookmark142_html} ${bookmark143_html}
${bookmark144_html} ${bookmark151_html} ${bookmark152_html}
${bookmark153_html} ${bookmark154_html} ${bookmark161_html}
${bookmark162_html} ${bookmark163_html} ${bookmark164_html}
${bookmark171_html} ${bookmark172_html} ${bookmark173_html}
${bookmark174_html} ${bookmark181_html} ${bookmark182_html}
${bookmark183_html} ${bookmark184_html} ${bookmark191_html}
${bookmark192_html} ${bookmark193_html} ${bookmark194_html}
<body>
	<input type="hidden" id="generalDisabledFlagParam"
		name="generalDisabledFlagParam" value="-1">
	<input type="hidden" id="userId" name="userId" value="${userId}">
	<input type="hidden" id="stateKey" name="stateKey" value="${stateKey}">
	<input type="hidden" id="formPathInfo" name="formPathInfo"
		value='${formPathInfo}'>
	<input type="hidden" id="formId" name="formId" value="${formId}">
	<input type="hidden" id="formCode" name="formCode" value="${formCode}">
	<input type="hidden" id="formGeneralInfo" name="formGeneralInfo"
		value="">
	<input type="hidden" id="parentId" name="parentId" value="${PARENT_ID}">
	<input type="hidden" id="backUrl" name="backUrl"
		value='../skylineFormWebapp/<%= session.getAttribute("homePage") %>'>
	<input type="hidden" id="springMessages" name="springMessages"
		value='${springMessages}'>
	<input type="hidden" id="permissionsAccess" name="permissionsAccess"
		value="${PERMISSION_ACCESS}">
	<input type="hidden" id="lastChangeUserId" name="lastChangeUserId"
		value="${lastChangeUserId}">
	<input type="hidden" id="lastChangeDate" name="lastChangeDate"
		value="${lastChangeDate}">
	<form id="doBackForm" action="doBack.request" method="post">
		<input type="hidden" id="formCode_request" name="formCode_request"
			value="${formCode}"><input type="hidden"
			id="stateKey_request" name="stateKey_request" value="${stateKey}"><input
			type="hidden" id="formCode_doBack" name="formCode_doBack">
	</form>
	<%@ include file="../include/includeBodyJS.jsp"%>
	<!-- body -->
	<table style="width: 100%;">
		<tr>
			<td class="ui-widget-content ui-corner-all innerTD" colspan="10">
				<table class="displayOnLoad" width="100%" style="display: none;">
					<tr>
						<td class="innerTD">
							<!--begin --> <divoverflow-y:auto;">
								<table id="inner-main-table">
									<tr>
										<td colspan="4" height="20"></td>
									</tr>
									<TR>
										<TD>
											<button type="button" class="button" perm_attr="cu"
												id="save_" onclick="doSave('Reload')">
												<spring:message code="Save" text="Save" />
											</button>
										</TD>
										<td>Local</td>
										<td>${bookmarkappid}</td>
										<td>${bookmarkitemname}</td>
									</TR>
									<tr>
										<td style="width: 10%;">${bookmark111}</td>
										<td style="width: 40%;">${bookmark112}</td>
										<td style="width: 10%;">${bookmark113}</td>
										<td style="width: 40%;">${bookmark114}</td>
									</tr>
									<tr>
										<td colspan="4" height="20"></td>
									</tr>
									<tr>
										<td>${bookmark121}</td>
										<td>${bookmark122}</td>
										<td>${bookmark123}</td>
										<td>${bookmark124}</td>
									</tr>
									<tr>
										<td colspan="4" height="20"></td>
									</tr>
									<tr>
										<td>${bookmark131}</td>
										<td>${bookmark132}</td>
										<td>${bookmark133}</td>
										<td>${bookmark134}</td>
									</tr>
									<tr>
										<td colspan="4" height="20"></td>
									</tr>
									<tr>
										<td>${bookmark141}</td>
										<td>${bookmark142}</td>
										<td>${bookmark143}</td>
										<td>${bookmark144}</td>
									</tr>
									<tr>
										<td colspan="4" height="20"></td>
									</tr>
									<tr>
										<td>${bookmark151}</td>
										<td>${bookmark152}</td>
										<td>${bookmark153}</td>
										<td>${bookmark154}</td>
									</tr>
									<tr>
										<td colspan="4" height="20"></td>
									</tr>
									<tr>
										<td>${bookmark161}</td>
										<td>${bookmark162}</td>
										<td>${bookmark163}</td>
										<td>${bookmark164}</td>
									</tr>
									<tr>
										<td colspan="4" height="20"></td>
									</tr>
									<tr>
										<td>${bookmark171}</td>
										<td>${bookmark172}</td>
										<td>${bookmark173}</td>
										<td>${bookmark174}</td>
									</tr>
									<tr>
										<td colspan="4" height="20"></td>
									</tr>
									<tr>
										<td>${bookmark181}</td>
										<td>${bookmark182}</td>
										<td>${bookmark183}</td>
										<td>${bookmark184}</td>
									</tr>
									<tr>
										<td colspan="4" height="20"></td>
									</tr>
									<tr>
										<td>${bookmark191}</td>
										<td>${bookmark192}</td>
										<td>${bookmark193}</td>
										<td>${bookmark194}</td>
									</tr>
								</table>
								<!--end -->
								</div>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</body>
</html>
