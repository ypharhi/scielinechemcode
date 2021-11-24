function initFormSaveDisplayButtons() {
	
}

function initForm() {
	
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