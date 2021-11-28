function initFormSaveDisplayButtons() {
	console.log("start initForm");
	
	var _formCode = "";
	if($('#formCode').length > 0) {
		_formCode = $('#formCode').val();
	}
	
	//********** ColumnsDefinition *************
	if (_formCode == 'ColumnsDefinition') 
	{
		$('.popupSaveDefinitionBtn').css('display', 'none');
	}
}

function initForm() {
	
	console.log("start initForm");
	
	var _formCode = "";
	if($('#formCode').length > 0) {
		_formCode = $('#formCode').val();
	}
	 
	//********** SearchLabel *************
	if(_formCode == 'SearchLabel') {
    	$('#close_back').hide();
    	$('.expanded.page-header').hide();
    	$('#pageTitle').closest('table').closest('tr').remove();
        $('.ui-widget-content table:last').closest('div').css('height', '');
        //hide the loading label (it has no id so we use font[color=red] to get this dom element)
        $("font[color=red]").css('display', 'none');
    	setTimeout(function () {
	    	$("#SeachLabelName").focus();
	    	$("#SeachLabelName").keyup(function(e) {
	    		if(e.which == 13){ // qrcode scan will include enter as last char
	    			searchLabel();
	    		}
	    	});
    	},100);
    }
	
	//********** ColumnsDefinition *************
	if (_formCode == 'ColumnsDefinition') 
	{
		var colObjArr = parent.$('#prevDialog').data('colArray');
		var removedArr = parent.$('#prevDialog').data('removedArray');
		var tableId = $('#tableId').val();
		$('#close_back').html('OK');
		$('#close_back').attr('onclick', 'doReset();');
		
		
		var ulAll = "<ul>";
		var ulElem = "<ul id=\"reorderable\">";
		var mainContainer_ = $('#cblist');
		var id = 0, checked_counter = 0;
		var showResetDefault = (parent.$('#formCode').length > 0 && (
				parent.$('#formCode').val() == 'Main' || 
				parent.$('#formCode').val() == 'StepMinFr' 
		    )
		);
		
		for (var j = 0; j < colObjArr.length; j++) {
			if (colObjArr[j] != undefined) 
			{
				var obj = colObjArr[j];
				var isColRemoveEnabled = obj.isColRemoveEnabled;
				var isColReorderEnabled = obj.isColReorderEnabled;
				var name = obj.title;
			    var val = name;
			    if(name.indexOf(';') != '-1'){
				   val = name.split(';')[0];
				   name = name.split(';')[1];
			    }
			    if(id == 0)
			    {
			    	ulAll += "<td>";
			    	ulAll += "<input type='checkbox' id='chbAllNone'></>" + "<label>All</label>";
			    	if(showResetDefault) {
			    		ulAll += "<span> <img src='../skylineFormWebapp/images/settings.png' style='cursor:pointer;width: 26px;float: right;margin-right: 25px;' onclick='restoreColumnsByDefault()'> </span>" //"+domId+".id
			    	}
			    	ulAll += "</td>";
			    	ulAll += "</ul>";
			    	mainContainer_.append($(ulAll));
			    }
			    id++;			    
			    var checked = "", disabled = "", 
			    	reorderDisabledClass = "", 
			    	colIndex="colIndex="+obj.colIndex+"";
			    
			    if (removedArr.indexOf(val) == '-1') {
			    	checked = "checked";
			    	checked_counter++;
				}
			    if(!isColReorderEnabled) {
			    	reorderDisabledClass = "ui-state-disabled";
				}
			    
			    if(!isColRemoveEnabled) {
			    	disabled = "disabled";
		    	}
			    
			    ulElem += "<li class='ui-state-default "+reorderDisabledClass+"'>";
			    ulElem += "<input type='checkbox' id='cb"+id+"' value='"+val+"' "+checked+" "+disabled+" "+colIndex+"></>";
			    ulElem += "<label>"+name+"</label>";
			    ulElem += "</li>";	
			}
		}
		ulElem += "</ul>";
		mainContainer_.append($(ulElem));
		
		var checkbox_counter = $( "#reorderable" ).find('input[type="checkbox"]').length;
		if(checkbox_counter > 0 && checkbox_counter == checked_counter) 
		{
			$('#chbAllNone').prop('checked',true);
		}
		
		$( function() {
		    $( "#reorderable" ).sortable({
		      items: "li:not(.ui-state-disabled)"
		    });
		});
		
		$('ul#reorderable input[type="checkbox"]').on('click',function(){
			var isChecked = $(this).is(':checked');
			if(isChecked) {				
				var unchecked_counter = $( "#reorderable" ).find('input[type="checkbox"]:not(:checked)').length;
				if(checkbox_counter > 0 && unchecked_counter == 0) 
				{
					$('#chbAllNone').prop('checked',true);
					}
				}
			else {
				$('#chbAllNone').prop('checked',false)
			}
		});
		
		$('#chbAllNone').change(function() {
			var chk = false;
			if ($('#chbAllNone').is(":checked")) {
				chk = true;
			}
			$('ul#reorderable input[type="checkbox"]:not(:disabled)').each(function(){
				var $el = $(this);
				$el.prop("checked", chk);
			});
		});

	}
	 
	//********** NavigationTree *************
    if(_formCode == 'NavigationTree') {
    	var parentFormId = parent.$('#prevDialog').data('parentFormId');
    	var parentFormCode = parent.$('#prevDialog').data('parentFormCode');
    	
    	var elementTree='<div id="tree" style="font-size: 11px;element=\"ElementTreeImp\"></div>'
    	+ '<input type="hidden" id="tree_catalog_hidden" value="FG_I_TREE_CONNECTION_V">'
    	+ '<input type="hidden" id="tree_tree_lastValue" value="">'
    	+ '<input type="hidden" id="tree_selected" value="">'
    	+ '<input type="hidden" id="tree_firstTime" value="0">'
    	+ '<input type="hidden" id="tree_doOnChangeJSCall" value=" onChangeAjax(\'tree\'); ">';
    		
    	var urlParam = "?formId="+ parentFormId
		+ "&formCode="+ _formCode
		+ "&userId="+ $('#userId').val()
		+ "&eventAction=getNavigationProject"
		+ "&isNew=" + $('#isNew').val();
    	
    	var allData = getformDataNoCallBack(1);
    	
    	var data_ = JSON.stringify({
    		action : "getNavigationProject",
    		data : [],
    		errorMsg : ""
    			});
    	// call...
    	$.ajax({
    		type : 'POST',
    		data : data_,
    		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
    		contentType : 'application/json',
    		dataType : 'json',
    		success : function(obj) {
    			if (obj.errorMsg != null && obj.errorMsg != '') {
    				displayAlertDialog(obj.errorMsg);
    				}
    			else{
    				if(obj.data[0].val!=undefined && obj.data[0].val!=null){
    					var res = obj.data[0].val.split('@');
        				var projectId = res[0];
        				var projectName = res[1];
        				var lastValue = res[2];
        				$('#navigationTree_').append(elementTree);
        				initTree('tree',' onChangeAjax(\'tree\'); ','{"_tree_lastValue":"'+lastValue+'","_selected":"'+parentFormId+','+parentFormCode+'"}','Project',projectId,projectName,'Project',projectId,parentFormId);
    				}else{
    					displayAlertDialog(getSpringMessage('alertError'));
    				}
    			}
    			},
    			error : handleAjaxError
    			});
	}
}

/**
 * change landing page on new button and do some operations according to the client requirements
 * @param formCode
 * @returns -1 in case of operating some actions according to the client requirements
 * else returns the formCode of the landing page 
 */
function onNewButtonIntegration(formCode,currentFormCode,formId,parentId) {
    return formCode;
}


function getFormPathInfo(scopeElements) {
	if(scopeElements!==undefined && scopeElements.length>0){
		return  scopeElements.find('#formPathInfo').val();
	}
	return  $('#formPathInfo').val();
}

function initNavigationTreeButton() {
//	$('.mainNavigationTreeBtn').css('display', 'block');
}

function confirmWithOutSave(functionName, params) {
	var cssName = $("#saveButton").attr("class");
	//if($('#formCode').val() != 'Template' ||  $('#STATUS_ID').val()!='Approved')
	$('#formCode_doBack').val($('#formCode').val());
	if(skipConfirmSave())
	{
		 functionName(params);
	}
	else {		 
		    if (prop.dataChanged || $('#formCode').val()=='XXX') {//checks conditions
		        //$('iframe').attr('src', ''); // code cause the bug: embedded iframe of richText(ckeditor) is reseted too.
		        openConfirmDialog({
		            onConfirm: functionName,
		            title: 'Warning',
		            onConfirmParams: params,
		            message: getSpringMessage('confirmWithOutSaveMessage')
		        });
		    } else {
		        functionName(params);
		    }
       
    }
}

/**
 * Confirm message before exit form page without save by click on maim menu
 * @param functionName
 * @param params
 * @returns
 */
function confirmWithOutSaveMainMenu(href) {
	  
    if ((skipConfirmSave()) || (href == "#") || (href == "http://192.168.10.72/Skyline_Dev/wizstabproductlistservlet_")) { // patch "Taro develop" navigation
    	window.location.href=href; 
    	return true;
    } else {
       return openConfirmDialog({
        	onConfirm:function(){
		        	  //$(elem).find('a').attr('href',href);
        		window.location.href=href;
        		return true;
  		        },
            title: 'Warning',
            message: getSpringMessage('confirmWithOutSaveMessage'),//$('#confirmWithOutSaveMessage').val(),
            onCancel:function(){
	        	  return false;
	        }
        });
    }
}

function confirmWithOutSaveLink(functionName, params) {
    $('#formCode_doBack').val($(params).attr('name'));
    if (skipConfirmSave()) {
    	functionName($('#formCode_doBack'));
    } else {
        //$('iframe').attr('src', ''); // code cause the bug: embedded iframe of richText(ckeditor) is reseted too.
        openConfirmDialog({
            onConfirm: functionName,
            title: 'Warning',
            onConfirmParams: $('#formCode_doBack'),
            message: $('#confirmWithOutSaveMessage').val()
        });
    }
}

/**
 * check if confirm message is needed. 
 * Note! in the popups the dataChanged prop of the parent is not evaluated (so this function will return true if there is no change in the popup only in the parent)
 *   [ We can pass the parent prop.dataChanged to the popup in the next version, until then we can change the default behaviour by always return true in popup (form with #save_ or ok buttons) or by put the specific form code the confirm is always needed - before this commit there where no confirm message in navigation from tables)] 
 * @returns
 */
function skipConfirmSave() {
	 
	if($('#isNew').val()=="1"){ 
		return false;
	}
	
	//check dataChanged (if no change we can skip the message)
	var isChanged_ = false;
	try {
		isChanged_ = prop.dataChanged;
		if($('#formCode').val()=='StepMinFr'){
			isChanged_ = isChanged_|| parent.prop.dataChanged;
		}
	} catch(e) {}
	if(!isChanged_) {
		return true;
	}
 
	//check if can be saved (if not we need to skeep the confirm message)
	var cssName = "";
	if($("#saveButton").length > 0) {
		cssName = $("#saveButton").attr("class"); // full screen
	}  else if($("#save_").length > 0) {
		cssName = $("#save_").attr("class"); //popup 
	}
	if(cssName.toLowerCase().indexOf("disabled") > -1) {
		return true;
	}
	
	return false;
}

function doBack() {
	// remove tab selection cookie
	$.removeCookie($('#formCode').val() + '_' + $('#formId').val()  + '_activetab');
    showWaitMessage(getSpringMessage('pleaseWait'));
    $('#doBackForm').submit();
    return;
}

function getForwardPage() {
    var formCode = $('#formCode').val();
    return formCode;
}

function confirmWithOutSaveWithPermissions(functionName, functionParams, permissParams) 
{
	var formCode = permissParams[0];
	var formId = permissParams[1];
	var isCheckDataChanged = '';
	if(functionParams.length>3){
		isCheckDataChanged = functionParams[3];
	}
	//start permission	
    var canRead = "0"
    $.ajax({
        type: 'POST',
        data: '{"action" : "getReadPermissionById","' + 'data":[' + '{"code":"formCode","val":"' + formCode + '"},' + '{"code":"formId","val":"' + formId + '"}' + '],' + '"errorMsg":""}',
        url: "./getReadPermissionById.request",
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) {
        	var canView= false;
            if (obj.errorMsg != null && obj.errorMsg != '') {
                displayAlertDialog(obj.errorMsg);
                return;
            } else if ((obj.data[0].val == "-1") || (obj.data[0].val == "")) {
            	//false
            } else {
                canRead = obj.data[0].val;
            }
//            canRead = "1"; // !!! develop until we set permission
            if(canRead != "1") 
            {
            	displayAlertDialog("Navigation is not allowed");
            } 
            else 
            {
            	//in popups - when confirmWithOutSavePopupMessage is different from NA then when navigating from popup to some other form, the confirmation message will be displayed with no refer to the datachanged flag
            	if(window.self !== window.top&&!isGeneralPopup()){
            		if(isCheckDataChanged!='false') {//its is a popup, and it's necessary to display the message
	        			if(getSpringMessage('confirmWithOutSavePopupMessage')!='NA'){//if the message is NA then do not display the confirm massage
	            			openConfirmDialog({
	            	            onConfirm: functionName,
	            	            title: 'Warning',
	            	            onConfirmParams: functionParams,
	            	            message: getSpringMessage('confirmWithOutSavePopupMessage')
	            	        });
	            			return;
	        			}
            		} else {
            			functionName(functionParams);
            			return; //fix bug 8022 - add return to avoid double tabs when navigate from popup tree
            		}
        		}
            	//console.log("confirmWithOutSave: " + prop.dataChanged);
            	if((isCheckDataChanged=='false')//if should not check the dataChanged, then displaying the confirmation message with no additional check
            			|| skipConfirmSave()) 
            	{ 
            		saveForm(functionName, functionParams);
            		//functionName(functionParams);
            	} else {//data was changed
            		$('#formCode_doBack').val($('#formCode').val());
            		
            		//if (prop.dataChanged)//the dataChanged flag is being already checked in the skipconfirmSave function
        	    	openConfirmDialog({
        	            onConfirm: functionName,
        	            title: 'Warning',
        	            onConfirmParams: functionParams,
        	            message: getSpringMessage('confirmWithOutSaveMessage')
        	        });
               }
            }
        },
        error: handleAjaxError
    });
    //////////////// end permission	
}

function openSearchLabelDialog(clickedObj)
{
	if($('#searchDialog iframe').length !=0)//whether the searchDialog is already opened
		return;
	//console.log("clickedObj",clickedObj);
	var $this = $(clickedObj);	
	var left, top;
	console.log("$this",$this);
	dialogHeight = 220;
    dialogWidth = 450;   
    if($this.length > 0)
    {
    	left = $this.offset().left - dialogWidth;
        top = $this.offset().top + $this.height() + 10;
        console.log("left",left);
        console.log("top",top);
    }
    else
    {
    	left = $(document).width() - dialogWidth - 100;
    	top = $(document).height()/2 - dialogHeight - 200;
    	console.log("left",left);
        console.log("top",top);
    }    
    
	var parentId = $('#formId').val();
	var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=SearchLabel&formId=-1&userId="
			+ $('#userId').val()
			+ '&tableType=&PARENT_ID=';

	// open iframe inside dialog
	var $dialog = $(
			'<div id="searchDialog" style="overflow-y: hidden;""></div>')//prevDialog
			.html(
					'<iframe style="border: 0px;width:100%;height:100%" src="'
							+ page + '"></iframe>')
			.dialog(
					{
						autoOpen : false,
						modal : true,
						height : dialogHeight,
						width : dialogWidth,
						// title: title,
						close : function() {
							$('#searchDialog iframe').attr('src', 'about:blank');
							$('#searchDialog').remove();
						},
						open: function(event, ui) 
						{
			                $(this).parent().css({'top': top,'left':left});
			            }
					});

	$dialog.dialog('option', 'dialogClass', 'noTitleStuff')
			.dialog('open');
}

function isGeneralPopup() {
	 //TODO check the popup Kind
	if (window.self.$('#saveButton').val()==undefined)
		return true;
	else return false;
}

function saveForm(functionName, functionParams){
	if($('#formCode').val() == 'SearchReport'){
		$('#isSavedSearch').val("1");//flag- use in ElementDataTableApiImpBL
		doSave((function(){functionName(functionParams);}),'SAVE_FORM_AND_USER_SETTINGS');
		//functionName(functionParams);
	}
	else{
		functionName(functionParams);
	}
}

function favoriteHeaderMng(domId) {

}


/**
 * Disable all elements in the page
 * @param enableElements- list of element to be editable
 * @param enableEditButton
 * @returns
 */
function generalBL_disablePage(enableElements, enableEditButton) { 
	console.log('generalBL_disablePage(): enableElements = ',enableElements);
	var startTime = new Date().getTime();
	
	disableAllDataTableLements(enableElements);
	$('input:not([type="search"],.firstString,[id*="_LinkToLastSelection"],.dataTableApiSelectInfo,[name="checkCharSample"]),select:not(#selectWfFormCode,[name*="_length"], .datatableapiselect)~div:not(:has(.linkElement)),textarea:not(.ckeditor),i.fa-calculator,i.fa-search,i.fa-trash,div[chemdoodle],.excelSheet,.dataTableApiAddMultiRows').addClass('disablePage');
    $('button:not(#newButton,#close_back,#cloneButton,.floating-button,.ui-dialog-titlebar-close,:contains("Confirm"),:contains("Continue"),:contains("Cancel"),:contains("Save"):not(.dataTableApiAdd),.dataTableApiEditShared,.dataTableApiButtonTools,.dataTableApiView,.dataTableApiEdit,.ireport,.dataTableApiLabel,.collapsible_iframes,.collapsible_button,.note-btn)').addClass('disablePage');//,
    $('select.disablePage,input[type="radio"].disablePage,input[type="checkbox"]:not(.dataTableApiSelectInfo,[name="checkCharSample"]).disablePage').prop("disabled", true);
    $('div.dateInput').addClass('disablePage'); 
    $('.fileUpload').addClass('disablePage');
    
    var ckeditorEnableList = getCkeditorEnableList(enableElements);
    
    $('[name=parentDiagramContainer]').each(function(){
		var elemObj = $(this);
    	var elementDomId_ = $(elemObj).attr('id');
    	setDisabledDiagram(true,elemObj);
	});
    $('div.ckeditor').each(function(){
    	var elemObj = $(this);
    	var elementDomId_ = $(elemObj).attr('id');
    	if( (ckeditorEnableList == "" || ("," + ckeditorEnableList + ",").indexOf("," + elementDomId_ + ",") == -1) ) {
    		setDisabledByElementId(elementDomId_, true);
    	}
    });   
    
    if(enableElements != undefined && enableElements != null)
    {
	    for(var i = 0; i<enableElements.length; i++) 
		{
	    	if ($('#' + enableElements[i]).length) {
		        $('#' + enableElements[i]).removeClass('disablePage');
		        if($('#' + enableElements[i]).is("select")){
		        	$('#' + enableElements[i]+"_chosen").prop("disabled",false);
		        	$('#' + enableElements[i]+"_chosen").removeClass('disablePage');
		        }
		        else if($('#' + enableElements[i]).is("div.ckeditor")){
		        	setDisabledByElementId(enableElements[i], false);
		        }
		        else if($('#' + enableElements[i]).is("textarea:not(.ckeditor)")){
		        	//$('#' + enableElements[i]+"_parent").prop("disabled",false);
		        	$('#' + enableElements[i]+"_parent").removeClass('disablePage');
		        }
		        else if($('#' + enableElements[i]).is('[name=parentDiagramContainer]')){
		        	setDisabledDiagram(false,$('#'+enableElements[i]));
		    	}
		        else if($('#' + enableElements[i]).is("table")){
		        	//$('#' + enableElements[i]+"_parent").prop("disabled",false);
		        	$('#' + enableElements[i]+"_dataTableStructButtons").find('.fileUpload').removeClass('disablePage');
		        	//$('#' + enableElements[i]+"_dataTableStructButtons button:not(.dataTableApiRemove,.dataTableApiNew)").removeClass('disablePage'); // yp 16122019 add :not(.dataTableApiRemove,.dataTableApiNew) to fix bug (remove and add row button was enabled in edit table)
		        	$('#' + enableElements[i]+"_dataTableStructButtons button").removeClass('disablePage'); // adib 24062020 removed :not(.dataTableApiRemove,.dataTableApiNew) to fix bug when there are tables that actually should be enabled. the case of the edit tables that are enabled occurs in the template->it was fixed by changing the func' setEmptyContentAuthzFunc
		        	changeSingleDTLabelByDisabledState(enableElements[i], false);//+"_dataTableStructButtons"
		        	
		        }
		        else if($('#' + enableElements[i]).is("input[type='checkbox']")){
		        	$('#' + enableElements[i]).prop('disabled',false);
		        }
		        else if($('#' + enableElements[i]).hasClass("date-picker")){
		        	$('#' + enableElements[i]).parent().removeClass('disablePage');
		        }
		    }
		}
    }
    if (enableEditButton == undefined || enableEditButton == null || !enableEditButton) { // yp 13112018 before the "remove edit button" we enabled the edit - because there is no edit button we prevent save when the edit was originally disabled
    	$('.submit-button-row').find('button:contains("Save")').addClass('disablePage');
    	$('#saveFloatingButton').addClass('disablePage');
    	$('#generalDisabledFlagParam').val('1'); // yp add this part of the "open forms in edit" task (and remove edit button) it uses the rich text and data table that load after the screen render (from this version it will happen only if no edit permission on the page)
    	disableAllDataTableLements(enableElements);
    }
    $('div.divAdditCustomInfo').removeClass('disablePage'); 
    $('select.disablePage').trigger('chosen:updated');
    $('.date-picker.disablePage').datepicker('disable');
    
    console.log( 'generalBL_disablePage took at: '+(new Date().getTime()-startTime)+'mS' );
//    var enableElementsString = "";
//    if(enableElements != null && enableElements != undefined)
//    {
//	    for(var i = 0; i<enableElements.length; i++) 
//		{
//	    	if(enableElementsString == "")enableElementsString="#";
//	    	enableElementsString += enableElements[i]+(i + 1 < enableElements.length?",#":"");
//		}
//    }
}

function getCkeditorEnableList(enableElements) {
	 var eList = "";
	 if(enableElements != undefined && enableElements != null) {
	    for(var i = 0; i<enableElements.length; i++) {
	    	 if($('#' + enableElements[i]).is("div.ckeditor")) {
	    		 if(eList == "") {
	    			 eList = enableElements[i];
	    		 } else {
	    			 eList = eList + "," + enableElements[i];
	    		 }
		     }
		}
	 }
	 return eList;
}

/**
 * hideUnnecessaryElementsFormBuilder
 * @param selectArray
 * @returns
 */
function hideUnnecessaryElementsFormBuilder(selectArray) {
	return selectArray;
}

function hideUnnecessaryFormTypesFormBuilder() {

}

function restoreUserBreadcrumbsFromLastSession()
{
	window.location = "./restore.request?stateKey=" + $('#stateKey').val() + "&userId=" + $('#userId').val();
}
