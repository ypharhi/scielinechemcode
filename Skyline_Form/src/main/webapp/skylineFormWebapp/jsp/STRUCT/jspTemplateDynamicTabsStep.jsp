<%@ include file="../include/include.jsp"%>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Skyline ${browserTitle}</title>

<!-- ******************************** -->
<!-- ******** from incldeCSS ******** -->
<!-- ******************************** -->
	<link rel="icon" href="../skylineFormWebapp/images/favicon.png?<spring:message code="Env" text="" />" />
<!-- ******** webix css ******** -->
	<link rel="stylesheet" type="text/css" href="../skylineFormWebapp/CSS/webix.min.css">
<!-- ******** general css ******** -->
	<link href="../skylineFormWebapp/js/chosen.min.css?<spring:message code="Env" text="" />" rel="stylesheet" type="text/css">
	    	<%-- <link href="../skylineFormWebapp/dist/themes/default/style.min.css?<spring:message code="Env" text="" />" rel="stylesheet"> --%>
	<link rel="stylesheet" type="text/css" href="../skylineFormWebapp/CSS/comply_theme/dataTables.colResize.css?<spring:message code="Env" text="" />" />
	<link rel="stylesheet" type="text/css" href="../skylineFormWebapp/CSS/comply_theme/colReorder.dataTables.css?<spring:message code="Env" text="" />" />
	<link href="../skylineFormWebapp/CSS/comply_theme/jquery-resizable-ui.css?<spring:message code="Env" text="" />" rel="stylesheet" type="text/css">
	<link href="../skylineFormWebapp/CSS/comply_theme/app.css?<spring:message code="Env" text="" />" rel="stylesheet" type="text/css">
	<link href="../skylineFormWebapp/CSS/comply_theme/tmpCSS.css?<spring:message code="Env" text="" />" rel="stylesheet" type="text/css">
<!-- ******** richtext css and files ******** -->
	<link href="../skylineFormWebapp/CSS/comply_theme/summernote/summernote-lite.css" rel="stylesheet" type="text/css" media="all" />
	<style type="text/css">
		/* for summernote editor */ 
		.note-editor .note-editing-area {
		    background-color: white;
		}
		.close {
			line-height: 2;
		}
		.note-modal-footer {
			padding: 0;
			padding-right: 30px;
		}
	</style>
	<script defer src="../skylineFormWebapp/js/summernote/summernote-lite.min.js" type="text/javascript"></script> 
	<script defer src="../skylineFormWebapp/js/summernote/summernote-ext-rtl.js" type="text/javascript"></script>
<!-- ******************************** -->
<!-- ******************************** -->
<!-- ******************************** -->

<script src="../skylineFormWebapp/deps/jquery-1.12.4.min.js"></script>
<script src="../skylineFormWebapp/deps/jquery-ui.custom_new.min.js"></script>
<script src="../skylineFormWebapp/js/foundation.min.js" ></script>

<script src="../skylineFormWebapp/deps/moment.min.js"></script>
<script src="../skylineFormWebapp/js/properties.js"></script>
<script src="../skylineFormWebapp/js/jquery.cookie.js"></script>
<script src="../skylineFormWebapp/js/generalFunc<spring:message code="generalFuncMin" text="." />js?<spring:message code="Env" text="" />"></script>

<script>
	//$(document).ready(function()
	//{
		console.log("!!!!script init TABS");
		
    	$("#tempalteTabs").tabs({
    		    /* active : $.cookie($('#formCode').val() + '_' + $('#formId').val()  + '_activetab'),
    		    activate : function( event, ui ){
    		        $.cookie($('#formCode').val() + '_' + $('#formId').val()  + '_activetab', ui.newTab.index(),{
    		            expires : 1
    		        });
    		    } */
    		    
         }); // init tabs
		$('.displayOnLoad').css('display','');
	//});
</script>

<!-- IMPORTANT note for jquery.dataTables v1.10.20.js library: there are custom compatibility changes in the library code, be careful on upgrade library -->
<script	src="../skylineFormWebapp/deps/jquery.dataTables<spring:message code="jqueryDataTablesMin" text="." />js?<spring:message code="Env" text="" />"></script> 
<script src="../skylineFormWebapp/deps/dataTables.jqueryui.js"></script>
<!-- IMPORTANT note for dataTables.colResize.js library: there are custom compatibility changes in the library code, be careful on upgrade library -->
<script type="text/javascript" src="../skylineFormWebapp/deps/dataTables.colResize.js?<spring:message code="Env" text="" />"></script>
<script type="text/javascript" src="../skylineFormWebapp/deps/dataTables.colReorder.js?<spring:message code="Env" text="" />"></script> 
<script src="../skylineFormWebapp/deps/dataTables.rowsGroup.js"></script>
<script src="../skylineFormWebapp/js/chosen.jquery.min.js"></script>
<script src="../skylineFormWebapp/deps/jquery.ui-contextmenu.min.js"></script>
<script src="../skylineFormWebapp/deps/jquery.alphanum.js"></script>
<script src="../skylineFormWebapp/deps/dataTables.buttons.min.js"></script>
<script src="../skylineFormWebapp/deps/jszip.min.js"></script>
<script src="../skylineFormWebapp/deps/pdfmake.min.js"></script>
<script src="../skylineFormWebapp/deps/vfs_fonts.js" defer></script>
<script src="../skylineFormWebapp/deps/arialFontPDF.js" defer></script>
<script src="../skylineFormWebapp/deps/buttons.html5.min.js"></script>
<script src="../skylineFormWebapp/deps/buttons.print.min.js"></script>
<script src="../skylineFormWebapp/deps/datetime-moment.js"></script>
<script src="../skylineFormWebapp/deps/bignumber.min.js"  defer></script>
<script src="../skylineFormWebapp/deps/url.min.js"  defer></script>
<script src="../skylineFormWebapp/js/history_navigation.js?<spring:message code="Env" text="" />"  defer></script>
<script src="../skylineFormWebapp/deps/traits.js"></script>
<script src="../skylineFormWebapp/js/element_js/ElementDataTableApiImp<spring:message code="ElementDataTableApiImpMin" text="." />js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementDataTableExtImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementChemDoodleImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementRichTextEditorImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementAutoCompleteDDLImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementInputImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementTreeImp.js?<spring:message code="Env" text="" />" defer></script>
<script src="../skylineFormWebapp/js/element_js/ElementUploadFileImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementGeneral.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementRadioImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementApiElementSetterImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementParamMonitoringImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementSmartSearchImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementAsyncIframeImp.js?<spring:message code="Env" text="" />" defer></script>
<script src="../skylineFormWebapp/js/element_js/ElementChemDoodleSearchImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementRangeFilterImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementAuthorizationImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementDynamicParamsImp.js?<spring:message code="Env" text="" />" defer></script>
<script src="../skylineFormWebapp/js/element_js/ElementUOMImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementButtonImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementDiagramImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/DiagramMindfusionWeb/MindFusion.Common.js" defer></script>
<script src="../skylineFormWebapp/js/DiagramMindfusionWeb/MindFusion.Diagramming.js" defer></script>
<script src="../skylineFormWebapp/deps/webix.js" defer></script>
<script src="../skylineFormWebapp/js/element_js/ElementWebixMassBalanceCalcImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementWebixMassBalanceInfoImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementWebixCommonFuncImp.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/bl_Adama_js/generalBL<spring:message code="generalBLMin" text="." />js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/bl_Adama_js/ElementAuthorizationImpBL.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/bl_Adama_js/ElementButtonImpBL<spring:message code="ElementButtonImpBLMin" text="." />js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/bl_Adama_js/ElementDataTableApiImpBL.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/bl_Adama_js/ElementChemDoodleImpBL.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/marvin/gui/lib/promise-1.0.0.min.js" defer></script>
<script src="../skylineFormWebapp/marvin/js/marvinjslauncher.js" defer></script>
<script src="../skylineFormWebapp/js/bl_Adama_js/ElementAdditionalInfo.js"></script>
<script src="../skylineFormWebapp/js/bl_Adama_unittest_js/unittestFunc.js?<spring:message code="Env" text="" />"></script>
<script src="../skylineFormWebapp/js/element_js/ElementIreportImp.js?<spring:message code="Env" text="" />"></script>
 <!-- ---------------------------- -->

<script>
	$(document).ready(function()
	{
		console.log("!!!!$(document).ready(function()) foundation");
		//INIT foundation
    	$(document).foundation();
    	
    	$("#tempalteTabs").tabs({
    		    active : $.cookie($('#formCode').val() + '_' + $('#formId').val()  + '_activetab'),
    		    activate : function( event, ui ){
    		        $.cookie($('#formCode').val() + '_' + $('#formId').val()  + '_activetab', ui.newTab.index(),{
    		            expires : 1
    		        });
    		    }
    		    
         });  // init tabs
		$('.displayOnLoad').css('display','');
		
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
    	
    	//asyn call to update the local storage with the spreadsheet data
    	var checkIntervalSpreadsheet = 300000;
    	if($('[element = "ElementExcelSheetImp"]').length>0 && checkIntervalSpreadsheet>0){
	    	//console.log("checkIntervalMS_",checkIntervalMS_);
	    	setInterval(function(){
	    	    setSpreadsheetUserData();
	    	}, checkIntervalSpreadsheet);
    	}
    	
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
	});
</script> 

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
		
		//initPage();
	    console.log("start initPage");
	    $("button").button();
	    //getTitleAndSubtitleForm();   
	    var startTime = new Date().getTime();
	    
	     initDatePickerWithOptionsByClass('date-picker');
	    initConfirmDialogDiv();
	    initAlphaNumForm();
	    initMandatory();
	    initElementInfo();
	    initPageInfoOnAjaxChange();
	    initAdditInfoDialog();
	    if ($("#tempalteTabs").length > 0) 
	    {
	    	 initWFDialog();
	        $('div[title].ui-tabs-panel').attr('title', ''); // remove title from
	        // the tabs
	        if ($('#isNew').val() == "1") {
	            $('#newButton').addClass('disabledclass');
	            $('#newFloatingButton').addClass('disabledclass');
	        }
	        initDataChanged();
	        //yp 20/10/2020 make it in navigationTabSelection function in authez to prevent click on unzuthrized tab
// 	        if ($('#formTab').val() != "") 
// 	        {
// 	        	$('a[href="#' + $('#formTab').val() + 'Tab"]').click();
// 	        }
	        if (window.self === window.top) {
	            if ($('#backUrl').val() != "") {
	                $('#close_back').attr('onclick', 'confirmWithOutSave(doBack);');
	            } else {
	                $('#close_back').css('display', 'none');
	            }
	        } else {
	            $('#homePageHeaderJspTempalte').css({'display':'none','cursor':'pointer'});
	            $('#backHeaderJspTempalte').css({'display':'none','cursor':'pointer'});
	            var mbmcpebul_table_tr = $('#mbmcpebul_table').closest('tr');
	            $(mbmcpebul_table_tr.siblings()[1]).css('display', 'none');
	            mbmcpebul_table_tr.css('display', 'none');            
	        }
	    } else { 
	    	 $('.displayOnLoad').css('display','');
	    }
	    
	    // update pdfMake.fonts (for hebrew pdf)
	    window.pdfMake.fonts = {
	        arial: {
	            normal: 'Arial.ttf',
	            bold: 'Arial.ttf',
	            italics: 'Arial.ttf',
	            bolditalics: 'Arial.ttf',
	        }
	    };
	
	    $('input[type="Number"]').on('input', function () {
	        // update realvalue and title(tooltip) with the new value on ui change
	        // for input with the type number.
	        $(this).attr('realvalue', this.value);
	        $(this).attr('title', this.value);
	    });
	
	    elementUOMImpInit(); //init UOM elements.
	    
	    //innit save display button - popup and struct => display only else display and save (if other behaviour is needed make it in customer bl)
	    $('.popupSaveFormAndDefinitionBtn').css('display', 'none');
		$('.popupSaveDefinitionBtn').css('display', 'inline').attr('title', 'Save popup display');
	    
		if($('#isStruct').val() == 0) {
			$('.mainSaveFormAndDefinitionBtn').css('display', 'inline').attr('title', 'Save data and display');
			$('.mainSaveDefinitionBtn').css('display', 'none');
		} else {
			$('.mainSaveFormAndDefinitionBtn').css('display', 'none').attr('title', 'Save display');
			$('.mainSaveDefinitionBtn').css('display', 'inline');
		}
		
		initFormSaveDisplayButtons();
		initNavigationTreeButton();
		console.log("call initForm");
	    initForm();
	
		renderElementAuthorizationImp();
		    
		console.log( 'initPage took at: '+(new Date().getTime()-startTime)+'mS' );
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