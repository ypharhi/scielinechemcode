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
   width:95%;
/*   border: 1px solid red;  */
/*   padding : 5px;  */
 } 
</style>
<script>

	$(document).ready(function() { 
		${bookmark11_ready}  ${bookmark12_ready}  ${bookmark13_ready}  ${bookmark14_ready}  ${bookmark21_ready}  ${bookmark22_ready}  ${bookmark23_ready}  ${bookmark24_ready}  ${bookmark31_ready}  ${bookmark32_ready}  ${bookmark33_ready}  ${bookmark34_ready}  ${bookmark41_ready}  ${bookmark42_ready}  ${bookmark43_ready}  ${bookmark44_ready}  ${bookmark51_ready}  ${bookmark52_ready}  ${bookmark53_ready}  ${bookmark54_ready}  ${bookmark61_ready}  ${bookmark62_ready}  ${bookmark63_ready}  ${bookmark64_ready}  ${bookmark71_ready}  ${bookmark72_ready}  ${bookmark73_ready}  ${bookmark74_ready}  ${bookmark81_ready}  ${bookmark82_ready}  ${bookmark83_ready}  ${bookmark84_ready}  ${bookmark91_ready}  ${bookmark92_ready}  ${bookmark93_ready}  ${bookmark94_ready}  ${bookmark101_ready}  ${bookmark102_ready}  ${bookmark103_ready}  ${bookmark104_ready}  ${bookmark111_ready}  ${bookmark112_ready}  ${bookmark113_ready}  ${bookmark114_ready}  ${bookmark121_ready}  ${bookmark122_ready}  ${bookmark131_ready}  ${bookmarkAuthorization_ready}  ${Optional11_ready}  ${Optional12_ready}  ${Optional13_ready} 
 
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
	${bookmark11_function} 	${bookmark12_function} 	${bookmark13_function} 	${bookmark14_function} 	${bookmark21_function} 	${bookmark22_function} 	${bookmark23_function} 	${bookmark24_function} 	${bookmark31_function} 	${bookmark32_function} 	${bookmark33_function} 	${bookmark34_function} 	${bookmark41_function} 	${bookmark42_function} 	${bookmark43_function} 	${bookmark44_function} 	${bookmark51_function} 	${bookmark52_function} 	${bookmark53_function} 	${bookmark54_function} 	${bookmark61_function} 	${bookmark62_function} 	${bookmark63_function} 	${bookmark64_function} 	${bookmark71_function} 	${bookmark72_function} 	${bookmark73_function} 	${bookmark74_function} 	${bookmark81_function} 	${bookmark82_function} 	${bookmark83_function} 	${bookmark84_function} 	${bookmark91_function} 	${bookmark92_function} 	${bookmark93_function} 	${bookmark94_function} 	${bookmark101_function} 	${bookmark102_function} 	${bookmark103_function} 	${bookmark104_function} 	${bookmark111_function} 	${bookmark112_function} 	${bookmark113_function} 	${bookmark114_function} 	${bookmark121_function} 	${bookmark122_function} 	${bookmark131_function} 	${bookmarkAuthorization_function} 	${Optional11_function} 	${Optional12_function} 	${Optional13_function} 

</script>
</head>
 ${bookmark11_html}  ${bookmark12_html}  ${bookmark13_html}  ${bookmark14_html}  ${bookmark21_html}  ${bookmark22_html}  ${bookmark23_html}  ${bookmark24_html}  ${bookmark31_html}  ${bookmark32_html}  ${bookmark33_html}  ${bookmark34_html}  ${bookmark41_html}  ${bookmark42_html}  ${bookmark43_html}  ${bookmark44_html}  ${bookmark51_html}  ${bookmark52_html}  ${bookmark53_html}  ${bookmark54_html}  ${bookmark61_html}  ${bookmark62_html}  ${bookmark63_html}  ${bookmark64_html}  ${bookmark71_html}  ${bookmark72_html}  ${bookmark73_html}  ${bookmark74_html}  ${bookmark81_html}  ${bookmark82_html}  ${bookmark83_html}  ${bookmark84_html}  ${bookmark91_html}  ${bookmark92_html}  ${bookmark93_html}  ${bookmark94_html}  ${bookmark101_html}  ${bookmark102_html}  ${bookmark103_html}  ${bookmark104_html}  ${bookmark111_html}  ${bookmark112_html}  ${bookmark113_html}  ${bookmark114_html}  ${bookmark121_html}  ${bookmark122_html}  ${bookmark131_html}  ${bookmarkAuthorization_html}  ${Optional11_html}  ${Optional12_html}  ${Optional13_html} 
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
									 <!--begin --><div  overflow-y: auto;">
									 <table id="inner-main-table">
									    <tr>
											<td colspan="4" height="20"></td>
										</tr>
										<TR>
										  <TD>
											<button type="button" class="button" perm_attr="cu" id="save_" onclick="doSave('Reload')"><spring:message code="Save" text="Save" /></button>
										  </TD>
										  <td>
										  	${bookmarkappid}
										  </td>
										  <td>
										    ${bookmarkitemname}
										  </td>
										</TR>
										<tr>
											<td style="width: 10%;">${bookmark11}</td>
											<td style="width: 40%;">${bookmark12}</td>
											<td style="width: 10%;">${bookmark13}</td>
											<td style="width: 40%;">${bookmark14}</td>
										</tr>
										<tr>
											<td colspan="4" height="20"></td>
										</tr>
										<tr>
											<td>${bookmark21}</td>
											<td>${bookmark22}</td>
											<td>${bookmark23}</td>
											<td>${bookmark24}</td>
										</tr>
										<tr>
											<td colspan="4" height="20"></td>
										</tr>
										<tr>
											<td>${bookmark31}</td>
											<td>${bookmark32}</td>
											<td>${bookmark33}</td>
											<td>${bookmark34}</td>
										</tr>
										<tr>
											<td colspan="4" height="20"></td>
										</tr>
										<tr>
											<td>${bookmark41}</td>
											<td>${bookmark42}</td>
											<td>${bookmark43}</td>
											<td>${bookmark44}</td>
										</tr>
										<tr>
											<td colspan="4" height="20"></td>
										</tr>
										<tr>
											<td>${bookmark51}</td>
											<td>${bookmark52}</td>
											<td>${bookmark53}</td>
											<td>${bookmark54}</td>
										</tr>
										<tr>
											<td colspan="4" height="20"></td>
										</tr>
										<tr>
											<td>${bookmark61}</td>
											<td>${bookmark62}</td>
											<td>${bookmark63}</td>
											<td>${bookmark64}</td>
										</tr>
										<tr>
											<td colspan="4" height="20"></td>
										</tr>
										<tr>
											<td>${bookmark71}</td>
											<td>${bookmark72}</td>
											<td>${bookmark73}</td>
											<td>${bookmark74}</td>
										</tr>
										<tr>
											<td colspan="4" height="20"></td>
										</tr>
										<tr>
											<td>${bookmark81}</td>
											<td>${bookmark82}</td>
											<td>${bookmark83}</td>
											<td>${bookmark84}</td>
										</tr>
										<tr>
											<td colspan="4" height="20"></td>
										</tr>
										<tr>
											<td>${bookmark91}</td>
											<td>${bookmark92}</td>
											<td>${bookmark93}</td>
											<td>${bookmark94}</td>
										</tr>
										<tr>
											<td colspan="4" height="20"></td>
										</tr>
										<tr>
											<td>${bookmark101}</td>
											<td>${bookmark102}</td>
											<td>${bookmark103}</td>
											<td>${bookmark104}</td>
										</tr>
										<tr>
											<td colspan="4" height="20"></td>
										</tr>
										<tr>
											<td>${bookmark111}</td>
											<td>${bookmark112}</td>
											<td>${bookmark113}</td>
											<td>${bookmark114}</td>
										</tr>
										<tr>
											<td colspan="2">${bookmark121}</td>
											<td colspan="2">${bookmark122}</td>
										</tr>
										<tr>
											<td colspan="4">${bookmark131}</td>
										</tr>
									</table>
									<!--end --></div>
									</td>
								</tr> 							
							</table>
						</td>
					</tr>
				</table>					
</body>
</html>
