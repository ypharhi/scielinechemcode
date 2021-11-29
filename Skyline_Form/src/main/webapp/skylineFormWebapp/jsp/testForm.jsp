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

<script>
${page_TestForm_lTestForm_function} 	${page_TestForm_lTestForm_function} 	${General11_function} 	${General12_function} 	${General13_function} 	${General14_function} 	${General21_function} 	${General22_function} 	${General23_function} 	${General24_function} 	${General31_function} 	${General32_function} 	${General41_function} 	${General42_function} 	${General51_function} 	${General52_function} 	${General61_function} 	${General62_function} 	${Samples1_function} 	${General71_function} 	${General72_function} 	${General73_function} 	${General74_function} 	${General81_function} 	${General82_function} 	${General83_function} 	${General84_function} 	${General91_function} 	${General92_function} 	${General93_function} 	${General94_function} 	${Reactant1_function} 	${ActionDetails1_function} 	${ActionDetails2_function} 	${bookmarkAuthorization_function} 	${Optional11_function} 	${Optional12_function} 

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
		 ${page_TestForm_lTestForm_ready}  ${page_TestForm_lTestForm_ready}  ${General11_ready}  ${General12_ready}  ${General13_ready}  ${General14_ready}  ${General21_ready}  ${General22_ready}  ${General23_ready}  ${General24_ready}  ${General31_ready}  ${General32_ready}  ${General41_ready}  ${General42_ready}  ${General51_ready}  ${General52_ready}  ${General61_ready}  ${General62_ready}  ${Samples1_ready}  ${General71_ready}  ${General72_ready}  ${General73_ready}  ${General74_ready}  ${General81_ready}  ${General82_ready}  ${General83_ready}  ${General84_ready}  ${General91_ready}  ${General92_ready}  ${General93_ready}  ${General94_ready}  ${Reactant1_ready}  ${ActionDetails1_ready}  ${ActionDetails2_ready}  ${bookmarkAuthorization_ready}  ${Optional11_ready}  ${Optional12_ready} 
 
		initPage();
	    renderElementAuthorizationImp();
	    //$('.displayOnLoad').css('display','');
	});		

</script>
</head>
${page_TestForm_lTestForm_html}  ${page_TestForm_lTestForm_html}  ${General11_html}  ${General12_html}  ${General13_html}  ${General14_html}  ${General21_html}  ${General22_html}  ${General23_html}  ${General24_html}  ${General31_html}  ${General32_html}  ${General41_html}  ${General42_html}  ${General51_html}  ${General52_html}  ${General61_html}  ${General62_html}  ${Samples1_html}  ${General71_html}  ${General72_html}  ${General73_html}  ${General74_html}  ${General81_html}  ${General82_html}  ${General83_html}  ${General84_html}  ${General91_html}  ${General92_html}  ${General93_html}  ${General94_html}  ${Reactant1_html}  ${ActionDetails1_html}  ${ActionDetails2_html}  ${bookmarkAuthorization_html}  ${Optional11_html}  ${Optional12_html}
<body style="overflow-y: hidden;">
	<input type="hidden" id="generalDisabledFlagParam"
		name="generalDisabledFlagParam" value="-1">
	<input type="hidden" id="userId" name="userId" value="${userId}">
	<input type="hidden" id="stateKey" name="stateKey" value="${stateKey}">
	<input type="hidden" id="formPathInfo" name="formPathInfo"
		value='${formPathInfo}'>
	<input type="hidden" id="formId" name="formId" value="${formId}">
	<input type="hidden" id="isNew" name="isNew" value="${isNew}">
	<input type="hidden" id="isStruct" name="isStruct" value="${isStruct}">
	<input type="hidden" id="formCode" name="formCode" value="${formCode}">
	<input type="hidden" id="formGeneralInfo" name="formGeneralInfo"
		value="">
	<input type="hidden" id="parentId" name="parentId" value="${PARENT_ID}">
	<input type="hidden" id="formTab" name="formTab" value="${formTab}">
	<input type="hidden" id="previousEntityFormId"
		name="previousEntityFormId" value="${previousEntityFormId}">
	<input type="hidden" id="nextEntityFormId" name="nextEntityFormId"
		value="${nextEntityFormId}">
	<input type="hidden" id="backUrl" name="backUrl"
		value='../skylineFormWebapp/<%= session.getAttribute("homePage") %>'>
	<input type="hidden" id="newAddress" name="newAddress" value="">
	<input type="hidden" id="springMessages" name="springMessages"
		value='${springMessages}'>
	<input type="hidden" id="permissionsAccess" name="permissionsAccess"
		value="${PERMISSION_ACCESS}">
	<input type="hidden" id="permissionsSensLevelOrder"
		name="permissionsSensLevelOrder"
		value="${PERMISSION_SENSITIVITYLEVEL_ORDER}">
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
			<td class="top-bar-container">
				<div style="width: 100%;">
					<%@ include file="./PageHeaderJsoTemplateForm.inc"%>
					<%@ include file="../include/includeMenu.jsp"%>
				</div>
			</td>
		</tr>
		<tr>
			<td class="ui-widget-content ui-corner-all innerTD" colspan="10">
				<table class="displayOnLoad" width="100%" style="display: none;">
					<tr>
						<td class="innerTD">
							<div id="tempalteTabs">
								<ul>
									<li><a href="#GeneralTab" sensitivitylevel_order="0">General</a></li>
									<li><a href="#DetailsTab" sensitivitylevel_order="0">Details</a></li>

								</ul>

								<!--begin -->
								<div class="tab-container"
									style="height: calc(100vh - 280px); overflow-y: auto;">
									<div id="GeneralTab" title="General">
										<table style="width: 100%;">
											<tr>
												<td width="10%">${General11}</td>
												<td width="12%">${General12}</td>
												<td width="10%"></td>
												<td width="10%">${General13}</td>
												<td width="12%">${General14}</td>
												<td></td>
											</tr>
											<tr>
												<td colspan="6" height="20"></td>
											</tr>
											<tr>
												<td>${General21}</td>
												<td>${General22}</td>
												<td width="10%"></td>
												<td>${General23}</td>
												<td>${General24}</td>
												<td></td>
											</tr>
											<tr>
												<td colspan="6" height="20"></td>
											</tr>
											<tr>
												<td>${General31}</td>
												<td colspan="4">${General32}</td>
												<td></td>
											</tr>
											<tr>
												<td colspan="6" height="20"></td>
											</tr>
											<tr>
												<td>${General41}</td>
												<td colspan="4">${General42}</td>
												<td></td>
											</tr>
											<tr>
												<td colspan="6" height="20"></td>
											</tr>
											<tr>
												<td>${General51}</td>
												<td colspan="4">${General52}</td>
												<td></td>
											</tr>
											<tr>
												<td colspan="6" height="20"></td>
											</tr>
											<tr>
												<td>${General61}</td>
												<td colspan="4">${General62}</td>
												<td></td>
											</tr>
											<tr>
												<td colspan="6" height="20"></td>
											</tr>
											<tr>
												<td colspan="6">${Samples1}</td>
											</tr>
											<tr>
												<td colspan="6" height="20"></td>
											</tr>
											<tr>
												<td>${General71}</td>
												<td>${General72}</td>
												<td width="10%"></td>
												<td>${General73}</td>
												<td>${General74}</td>
												<td></td>
											</tr>
											<tr>
												<td colspan="6" height="20"></td>
											</tr>
											<tr>
												<td>${General81}</td>
												<td>${General82}</td>
												<td width="10%"></td>
												<td>${General83}</td>
												<td>${General84}</td>
												<td></td>
											</tr>
											<tr>
												<td colspan="6" height="20"></td>
											</tr>
											<tr>
												<td>${General91}</td>
												<td>${General92}</td>
												<td width="10%"></td>
												<td>${General93}</td>
												<td>${General94}</td>
												<td></td>
											</tr>
											<tr>
												<td colspan="6" height="20"></td>
											</tr>
											<tr>
												<td colspan="6">${Reactant1}</td>
											</tr>
										</table>
									</div>
									<div id="DetailsTab" title="Details">
										<table style="width: 100%;">
											<tr>
												<td>${ActionDetails1}</td>
											</tr>
											<tr>
												<td height="40px"></td>
											</tr>
											<tr>
												<td>${ActionDetails2}</td>
											</tr>
										</table>
									</div>
									<!--end -->
								</div>
							</div>
						</td>
					</tr>
					<tr>
						<td class="submit-button-row">
							<div style="float: left;">
								<a href="#" ATTR_INFO_PAGE_HREF="1" style="display: none"></a>
								<div style="overflow-y: auto;" ATTR_INFO_PAGE_DIV="1">
									<p id="pDisplayPageInfoOnAjaxChange" style="color: blue;"></p>
									<p id="SET_PAGEINFO_ONAJAXCHANGE"></p>
								</div>
							</div>
							<button class="button" id="previousEntityButton" type="button"
								onclick="confirmWithOutSave(doNavigatePrevious);">Previous</button>
							${bookmarkAuthorization} ${Optional11} ${Optional12}
							<button class="button" id="newButton" type="button"
								onclick="confirmWithOutSave(openWFDialog,[doNew]);">New</button>
							<!-- 											<button class="button" id="saveButton" type="button" perm_attr="cu" onclick="doSave('Reload')" style="margin-left: 15px;"><spring:message code="Save" /></button>											 -->
							<button class="button" id="saveButton" type="button"
								perm_attr="cu" onclick="doSave('Reload')"
								style="margin-left: 15px;">
								<spring:message code="Save" />
							</button>
							<button class="button" id="close_back" type="button"
								style="margin-left: 15px;"
								onclick="parent.$('#prevDialog').dialog('close');">
								<spring:message code="Close" />
							</button>
							<button class="button" id="nextEntityButton" type="button"
								onclick="confirmWithOutSave(doNavigateNext);">Next</button>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</body>
</html>
