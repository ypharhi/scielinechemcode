<%@ include file="../include/include.jsp"%>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Skyline ${browserTitle}</title>
<link rel="icon" href="../skylineFormWebapp/images/favicon.ico?<spring:message code="Env" text="" />" />
<!--  <style type="text/css">
/*!
 * jQuery UI CSS Framework 1.12.1
 * http://jqueryui.com
 *
 * Copyright jQuery Foundation and other contributors
 * Released under the MIT license.
 * http://jquery.org/license
 *
 * http://api.jqueryui.com/category/theming/
 */



/* Interaction Cues
----------------------------------*/
.ui-state-disabled {
	cursor: default !important;
	pointer-events: none;
}
.ui-widget-overlay {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
}

/* Icons
----------------------------------*/


/* Misc visuals
----------------------------------*/

/* Overlays */

.ui-resizable {
	position: relative;
}
.ui-resizable-handle {
	position: absolute;
	font-size: 0.1px;
	display: block;
	-ms-touch-action: none;
	touch-action: none;
}
.ui-resizable-disabled .ui-resizable-handle,
.ui-resizable-autohide .ui-resizable-handle {
	display: none;
}
.ui-resizable-n {
	cursor: n-resize;
	height: 7px;
	width: 100%;
	top: -5px;
	left: 0;
}
.ui-resizable-s {
	cursor: s-resize;
	height: 7px;
	width: 100%;
	bottom: -5px;
	left: 0;
}
.ui-resizable-e {
	cursor: e-resize;
	width: 7px;
	right: -5px;
	top: 0;
	height: 100%;
}
.ui-resizable-w {
	cursor: w-resize;
	width: 7px;
	left: -5px;
	top: 0;
	height: 100%;
}
.ui-resizable-se {
	cursor: se-resize;
	width: 12px;
	height: 12px;
	right: 1px;
	bottom: 1px;
}
.ui-resizable-sw {
	cursor: sw-resize;
	width: 9px;
	height: 9px;
	left: -5px;
	bottom: -5px;
}
.ui-resizable-nw {
	cursor: nw-resize;
	width: 9px;
	height: 9px;
	left: -5px;
	top: -5px;
}
.ui-resizable-ne {
	cursor: ne-resize;
	width: 9px;
	height: 9px;
	right: -5px;
	top: -5px;
}






/* Spinner specific style fixes */

.ui-dialog {
	position: absolute;
	top: 0;
	left: 0;
	padding: .2em;
	outline: 0;
}


.ui-dialog .ui-dialog-title {
	float: left;
	margin: .1em 0;
	white-space: nowrap;
	width: 100%;
	overflow: hidden;
	text-overflow: ellipsis;
}

.ui-dialog .ui-dialog-content {
	position: relative;
	border: 0;
	padding: .5em 1em;
	background: none;
	overflow: auto;
}

.ui-dialog .ui-dialog-content {
	position: relative;
	border: 0;
	padding: .5em 1em;
	background: none;
	overflow: auto;
}



.ui-dialog .ui-resizable-n {
	height: 2px;
	top: 0;
}
.ui-dialog .ui-resizable-e {
	width: 2px;
	right: 0;
}
.ui-dialog .ui-resizable-s {
	height: 2px;
	bottom: 0;
}
.ui-dialog .ui-resizable-w {
	width: 2px;
	left: 0;
}
.ui-dialog .ui-resizable-se,
.ui-dialog .ui-resizable-sw,
.ui-dialog .ui-resizable-ne,
.ui-dialog .ui-resizable-nw {
	width: 7px;
	height: 7px;
}
.ui-dialog .ui-resizable-se {
	right: 0;
	bottom: 0;
}
.ui-dialog .ui-resizable-sw {
	left: 0;
	bottom: 0;
}
.ui-dialog .ui-resizable-ne {
	right: 0;
	top: 0;
}
.ui-dialog .ui-resizable-nw {
	left: 0;
	top: 0;
}


</style>-->
<style>
.body-container-wrapper {
	overflow: scroll;
	height: 100vh;
}

.sticky-menu-header {
	position: sticky;
	left: 0;
	width: 100%;
	z-index: 20;
}
.dynamic-savedisplay-alert-box {
	padding: 15px;
    margin-bottom: 20px;
    border: 1px solid transparent;
    border-radius: 2px;  
    position: -webkit-sticky;
 position: sticky;
 bottom: 0;
    margin-top: -70px;
    margin-left: 20px;
    width: 250px;
    height: 50px;
    display: none;
}

.dynamic-savedisplay-success {
    color: white;
    background-color: #2294CE;
    border-color: #2294CE;
    display: none;
}
</style>

<link rel="stylesheet" type="text/css" href="../skylineFormWebapp/CSS/webix.min.css">
<link href="../skylineFormWebapp/dist/themes/default/style.min.css?<spring:message code="Env" text="" />" rel="stylesheet"> 
<link rel="stylesheet" type="text/css" href="../skylineFormWebapp/CSS/comply_theme/dataTables.colResize.css?<spring:message code="Env" text="" />" />
<link rel="stylesheet" type="text/css" href="../skylineFormWebapp/CSS/comply_theme/colReorder.dataTables.css?<spring:message code="Env" text="" />" />
<link href="../skylineFormWebapp/CSS/comply_theme/app.css?<spring:message code="Env" text="" />" rel="stylesheet" type="text/css">
<!-- 	<link href="../skylineFormWebapp/CSS/comply_theme/jquery-ui.custom.css" rel="stylesheet" type="text/css" media="all" />-->
	
<script src="../skylineFormWebapp/deps/jquery-1.12.4.min.js"></script>
<script src="../skylineFormWebapp/deps/jquery-ui.custom_new.min.js"></script>

<script src="../skylineFormWebapp/deps/moment.min.js"></script>
<script src="../skylineFormWebapp/js/generalFunc<spring:message code="generalFuncMin" text="." />js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/jquery.cookie.js" ></script>
<script src="../skylineFormWebapp/js/properties.js" ></script>

<!-- IMPORTANT note for jquery.dataTables v1.10.20.js library: there are custom compatibility changes in the library code, be careful on upgrade library -->
<script	src="../skylineFormWebapp/deps/jquery.dataTables<spring:message code="jqueryDataTablesMin" text="." />js?<spring:message code="Env" text="" />"></script> 
<script src="../skylineFormWebapp/deps/dataTables.jqueryui.js"></script>

<script src="../skylineFormWebapp/deps/jquery.ui-contextmenu.min.js"></script>
<!-- IMPORTANT note for dataTables.colResize.js library: there are custom compatibility changes in the library code, be careful on upgrade library -->
<script type="text/javascript" src="../skylineFormWebapp/deps/dataTables.colResize.js?<spring:message code="Env" text="" />"></script>
<script type="text/javascript" src="../skylineFormWebapp/deps/dataTables.colReorder.js?<spring:message code="Env" text="" />"></script> 

<script src="../skylineFormWebapp/deps/dataTables.buttons.min.js"></script>
<script src="../skylineFormWebapp/deps/jszip.min.js"></script>
<script src="../skylineFormWebapp/deps/pdfmake.min.js"></script>
<script src="../skylineFormWebapp/deps/vfs_fonts.js"></script>
<script src="../skylineFormWebapp/deps/arialFontPDF.js"></script>
<script src="../skylineFormWebapp/deps/buttons.html5.min.js"></script>
<script src="../skylineFormWebapp/deps/buttons.print.min.js"></script>
<script src="../skylineFormWebapp/deps/datetime-moment.js"></script>
<script src="../skylineFormWebapp/deps/jquery.alphanum.js"></script>
<script src="../skylineFormWebapp/deps/bignumber.min.js"></script>
<script src="../skylineFormWebapp/js/foundation.min.js" ></script>
<script src="../skylineFormWebapp/deps/url.min.js"></script>
<script src="../skylineFormWebapp/js/history_navigation.js?<spring:message code="Env" text="" />"></script>

<script>
	$(document).ready(function()
	{
		//INIT foundation
    	$(document).foundation();
		
        // update message icon with new message count
        //update with the session result (the server side eval messageCount in every page)
    	var messageCounter = <%= (session.getAttribute("messageCount") != null) ? session.getAttribute("messageCount") : "0"  %>;
    	updateMenuItemUI(messageCounter);
    	
    	//asyn call to update messageCount every checkInterval_
    	var counter = 0;
    	var checkInterval_ = <%= (session.getAttribute("messageCheckInterval") != null) ? session.getAttribute("messageCheckInterval") : "20"  %>;  // 300000 ms = 5 minutes
    	var checkIntervalMS_ = checkInterval_ * 6000000;
    	//console.log("checkIntervalMS_",checkIntervalMS_);
    	var timer = setInterval(function(){
    	    counter++;
    	    checkNotificationMessage();

    	    if (counter >= 10) {
    	       clearInterval(timer);
    	    }
    	}, checkIntervalMS_);
    	
		// handel browser navigation:
		// F5 - NOTE refresh icon is not handaled  in this case the page will reload with another statkey and the bread crunb will start form this from
		$("body").keydown(function(e) {
		    if(e.which==116){
		    	e.preventDefault();
// 		    	alert('f5 clicked');
		    	fgReloadForm(null, true);
		    	return;
// 		        e.preventDefault();
		    }
		});
		 
    	// forword - disabled it
	 	window.addEventListener('next', function(e) {
	 		// do nothing
// 	 		alert('next button clicked');
// 	 		 e.preventDefault();
	  	}, false);
	  
    	// back - will activate the doBack() function
	  	window.addEventListener('previous', function(e) {
// 	  		 alert('back button clicked');
	  		 doBack();
	  		 return;
// 	  		 e.preventDefault();
	  	}, false);

    	
    	//call authzCheckOnDonReady
    	var newurl = url();
    	
    	//remove STATE_KEY
// 		if (history.pushState) {
			
//             if(newurl.indexOf("&stateKey") > 0) {
//             	newurl = newurl.substr(0, newurl.indexOf("&stateKey"));
//             }
// 			//push new URL
//             window.history.pushState({
//                 path: newurl
//             }, '', newurl);
//         }
    	
    	authzCheckOnDonReady(newurl);
    	
    	$(document).unbind('keydown').bind('keydown', function (event) 
		{
		    if (event.keyCode === 9) 
		    {		       
		        event.preventDefault();		        
		    }
		});
		
		$(document).on('click', function (e) 
		{
		    if ($(e.target).closest("div.breadcrumb-dropdown").length === 0) 
		    { 		    			    	
		    	var _divO = $("div#breadcrumbDropdownDiv");
		    	if(_divO.css('display') == 'block')
		    	{
		    		_divO.hide();
		    	}
		    }
		});
		var toolbar=$('.toolbar')[0];
		$(toolbar).css('display', 'none');	
	});
	
	

</script> 

<script src="../skylineFormWebapp/deps/traits.js"></script>
<script src="../skylineFormWebapp/js/element_js/ElementDataTableApiImp<spring:message code="ElementDataTableApiImpMin" text="." />js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementDataTableExtImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/dist/jstree.min.js"></script>
<script src="../skylineFormWebapp/js/element_js/ElementTreeImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementAuthorizationImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementUOMImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementDynamicParamsImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementRichTextEditorImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementWebixCommonFuncImp.js?<spring:message code="Env" text="" />"></script>

<%-- <%@ include file="../include/includeBL_js.jsp"%> --%>
<script src="../skylineFormWebapp/js/bl_Adama_unittest_js/unittestFunc.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/bl_Adama_js/generalBL<spring:message code="generalBLMin" text="." />js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/bl_Adama_js/ElementAuthorizationImpBL.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/bl_Adama_js/ElementDataTableApiImpBL.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/bl_Adama_js/ElementAdditionalInfo.js"></script>
<script src="../skylineFormWebapp/js/bl_Adama_js/ElementButtonImpBL<spring:message code="ElementButtonImpBLMin" text="." />js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/bl_Adama_js/reportDesignBL.js"></script>






<script type="text/javascript">

	$(document).ready(function() {
				
	   @bm_list_ready@ 
	   
	   initPage();
		
	    if (window.self === window.top){
	    	 $('#close_button').css('display','none');
	    }
	    else{
	    	 $('#homePageHeaderJspTempalte').css('display','none');
	    	 $('#homePageHeaderJspTempalte').css('cursor','pointer');
	    	 $('#backHeaderJspTempalte').css('display','none');
	    	 $('#backHeaderJspTempalte').css('cursor','pointer');
	    	 $('#mbmcpebul_table').closest('tr').css('display','none');
	    	 $($( '#mbmcpebul_table').closest('tr').siblings()[1]).css('display','none');
	    }
		
	    renderElementAuthorizationImp(); 
	    $('#mainPlusButton').attr('onclick', 'showTableDiv(this)');
        $('#mainMinusButton').attr('onclick', 'hideLastTableDiv(this)');
	});
	
@bm_list_function@
</script>
</head>
@bm_list_html@
<!-- body -->
<body>
	<input type="hidden" id="generalDisabledFlagParam" name="generalDisabledFlagParam" value="-1">
	<input type="hidden" id="isStruct" name="isStruct" value="${isStruct}">
	<input type="hidden" id="isNew" name="isNew" value="${isNew}">
	<input type="hidden" id="userId" name="userId" value="${userId}"><input type="hidden" id="stateKey" name="stateKey" value="${stateKey}"><input type="hidden" id="formPathInfo" name="formPathInfo" value='${formPathInfo}'>
	<input type="hidden" id="formId" name="formId" value="${formId}">
	<input type="hidden" id="formCode" name="formCode" value="${formCode}"><input type="hidden" id="formGeneralInfo" name="formGeneralInfo" value="">
	<input type="hidden" id="parentId" name="parentId" value="${PARENT_ID}">
	<input type="hidden" id="backUrl" name="backUrl" value='../skylineFormWebapp/<%= session.getAttribute("homePage") %>'>
	<input type="hidden" id="springMessages" name="springMessages" value='${springMessages}'>
	<input type="hidden" id="permissionsAccess" name="permissionsAccess" value="${PERMISSION_ACCESS}">
	<%@ include file="../include/includeBodyJS.jsp"%>

	<div class="body-container-wrapper"> 
	<div class="sticky-menu-header" >
	<%@ include file="./PageHeaderJsoTemplateForm.inc" %>
	<%-- 	<%@ include file="../include/includeMenu.jsp" %> take the part that we need for the main screen ... -> --%> 
		<div id="dropDownMenuBar" class="sub-header" style="width:100%;text-align: left;float: left;position: relative;">
			<div style="float: left;position: relative;width: 95%;">
				<%= ((String)session.getAttribute("MAIN_MENU")).replace("@@STATEKEY_HOLDER@@", request.getParameter("stateKey")) %>
			</div>	
			<div id="divIconQRCode" style="float: right;position: relative;" class="QR_code">
				<span onclick="openSearchLabelDialog(this);return false;"></span>
			</div>  
		</div>
		<div id="tdIncludeBreadcrumbJsp" style="display:inline;width: 100%;position: relative;float: left;">
			<div class="row expanded page-header">
				<div class="small-4 columns text-left breadcrumbs-container" style="width:32%;">
				<% if(session != null && session.getAttribute("SHOW_RESTORE_BREADCRUMB_BUTTON").toString().equals("1")) { %>
					<div style="padding-right:3em;">
						<button type="button" class="button" style="display:inline-block;width:180px;height:25px;font-size:12px;" id="restoreLastSessionBtn" onclick="restoreUserBreadcrumbsFromLastSession()">Restore Breadcrumb</button>
					</div>
				<% 
					session.setAttribute("SHOW_RESTORE_BREADCRUMB_BUTTON","0");
				} else {} 
				%>
					
					${breadCrumbHtml}
					<span><img src="../skylineFormWebapp/images/navigation_tree_open.png" id="navigationTree"  class="mainNavigationTreeBtn" onclick="openNavigationTree()" style="width: 23px;cursor:pointer;display:none;"/></span> 
				</div>
				<div class="small-4 columns text-center" style="width:7%;">
	                <select id="headerSelect" class="chosen-select" data-placeholder="Choose:" lastvalue="" style="display:none;"></select>
				</div>
				<div class="small-4 columns text-center" style="width:32%;">
	                <h1 id="pageTitle"  style = "white-space:nowrap;${formTitleCSS}" ${formTitleTooltip}>${formTitle}</h1>
	                <h2 id="pageSubTitle" width="54%" ${formSubTitleTooltip}>${formSubTitle}</h2>
	                ${formPath}
					
				</div>
				
			    <div class="floatingButtonsPanelContainer" id="divFloatingButtonsPanelContainerMain">
					<div class="floatingButtonsShowHideIcon" id="divFloatingButtonsShowHideIconMain"><i class="fa fa-angle-right" aria-hidden="true" onclick="floatingButtonPanelMainToggleClick(this)"></i></div>
					<div class="floatingButtonsPanel" style="display:none;">
						<button type="button" class="button mainPlusMinusBtn floating-button" id="mainPlusButton" title="Add Table" onclick="showTableDiv(this)"><i class="fa fa-plus" aria-hidden="true"></i></button>	
						<button type="button" class="button mainMinusButton floating-button" id="mainMinusButton" title="Remove Last Table" perm_attr="cu" onclick="hideLastTableDiv(this)"><i class="fa fa-minus" aria-hidden="true"></i></button>		
						
						<!-- <button type="button" class="button mainSaveDefinitionBtn floating-button" id="close_backFloatingButton___" title="Close" onclick="$('#close_back')[0].click()"><i class="fa fa-close" aria-hidden="true"></i></button> -->
						<button type="button" class="button ignor_data_change save-definition-button floating-button" id="mainSaveDefinitionFloatingBtnMain" title="Save display" onclick="doSave('','SAVE_FORM_AND_USER_SETTINGS')" >
							<img src="../skylineFormWebapp/images/save_display.png"/>
						</button>	
					</div>
				</div>	
			    	
			</div>
		</div>
	</div> 
   <table style="width:100%;">
					<tr>
					<td>
					<div class="page-header">
 					</div>
					</td>
				
					</tr>		
					<tr>
						<td class="innerTD" colspan="10">
							<table width="100%">
								<tr>
									<td id="simpleTd" class="innerTD">
									   <div id="simple" class="simple">
										 <!--begin --><div>
										@body@
										<!--end --></div>
										</div>
									</td>									
								</tr>	
								<tr>
									<td class="submit-button-row">
										<button id="close_button" type="button" style="margin-left: 15px;"
										 onclick="parent.$('#prevDialog').dialog('close');"><spring:message code="Close" text="Close" /></button>										
									</td>
								</tr>						
								
							</table>
						</td>
					</tr>
				</table>
	</div>
</body>
</html>