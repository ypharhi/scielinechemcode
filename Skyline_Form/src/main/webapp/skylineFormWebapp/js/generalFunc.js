var CLEAN_STORAGE_HOURS_AGO = 3;//how time ago inserted data cleared when clearing the localStorage

/**
 * return the Browser Name that we support(see checkBrowserSupport in Login.jsp): mozilla (Firefox) / chrome/ msie (ie) or na if not one of them
 * @returns
 */
function getBrowserName() {
	 var browserName = "na";
//	 try {
//		 browserName = ($.browser.name).toLocaleLowerCase();
//	 } catch(e) {
//		 
//	 }
//	 return browserName; // the code is taken from the checkBrowserName (originally called getBrowserName) and not the code above that didn't work when calling from the main screen (yp I didn't checked why (probably because of missing include (jquery?) in the login.jsp and I didn't want to make a major change) so I used it here to avoid code duplication)
	var name = navigator.appName;
	var agent = navigator.userAgent; 
    
	if(agent.indexOf('Firefox') != -1)
    {
		browserName = 'mozilla';
    }
	else if(agent.indexOf('Edg') != -1)
    {
    	browserName = 'edge';
    }
    else if(agent.indexOf('Chrome') != -1)
    {
    	browserName = 'chrome';
    }
    else if(( name == 'Microsoft Internet Explorer' && agent.indexOf('MSIE') != -1 ) || 
			( name == 'Netscape' && agent.indexOf('Trident') != -1 ))
    {  // second condition for IE11, and first for older versions
    	browserName = 'msie';
    }
	return browserName;
}

function onChangeAjax(actionVal, avoidPleaseWait) {
    // on element change
	if(typeof avoidPleaseWait === 'undefined' || !avoidPleaseWait) {
		 showWaitMessage("Please wait...");
	}
    var element = $('[id="' + actionVal + '"]').attr('element');

    $('[id="' + actionVal + '"]').attr('lastvalue', getValue_(element, $('[id="' + actionVal + '"]'))); //save last value
    
    //init post save arg object
    var callbackResultArray_ = [];
	var postCallbackArgObj = {arg_action: actionVal};
	// get all callback element in array
	var changeType = 1; //changeType = 1-ajax / =2  - save
    var allCallbacks = getformDataCallbacks(postCallbackArgObj, changeType); 
    if (allCallbacks != null && allCallbacks.length > 0) {
    	 //fire the first callback
        fireNextCallback(allCallbacks.pop(), callbackResultArray_);
    } else {
    	// fire postCallbacksSave
    	postCallbackOnChangeAjax(postCallbackArgObj, callbackResultArray_);
    }
}

function postCallbackOnChangeAjax(postCallbackArgObj, callbackResultArray_) {
	
	 var actionVal = postCallbackArgObj.arg_action;
	 var allDataNoCallBack = getformDataNoCallBack();
	 var allData = allDataNoCallBack.concat(callbackResultArray_);
	
	 var isDatatableApi = false; // if one of the elements is datatableapi -  we dont invoke hideWaitMessage() from here.
//	 var element = $('[id="' + actionVal + '"]').attr('element');
     var i, objDataVal, objDataLength;
     prop.onChangeAjaxFlag = true;
	    
	var urlParam =
       "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + '&userId=' + $('#userId').val() + '&stateKey=' + $('#stateKey').val();

   var data_ = JSON.stringify({
       action: actionVal,
       data: allData,
       errorMsg: ''
   });

   $.ajax({ //http://hmkcode.com/spring-mvc-json-json-to-java/
       type: 'POST',
       data: data_,
       url: "action.request" + urlParam,
       contentType: 'application/json',
       dataType: 'json',
       success: function (obj) {
           if (obj.errorMsg != null && obj.errorMsg != '') {
               displayAlertDialog(obj.errorMsg);
           } else if (obj.data[0] == null) {
               hideWaitMessage();
           } else {
//               $.each(obj.data, function (index, element) {
//                   element.val = getValidEval(element.val);                  
//                   isDatatableApi = (element.val.indexOf('onElementDataTableApiChange')  != -1) ? true : isDatatableApi;
//                   eval(element.val);
//               });
               objDataLength = obj.data.length;               
               var authzHolder;
               for (i = 0; i < objDataLength; i++) 
               { 
            	   if(obj.data[i].code == "UPDATED_INFO_ONAJAXCHANGE")
        		   {
            		   //console.log("UPDATED_INFO_ONAJAXCHANGE");
            		   //console.log(obj.data[i]);
            		   if(obj.data[i].val != "")
        			   {
            			   $('[ATTR_INFO_PAGE_HREF="1"]').css('display','block');
            			   $('#SET_PAGEINFO_ONAJAXCHANGE').html(obj.data[i].val);
        			   }           		   
            		   continue;
        		   }
            	   objDataVal = getValidEval(obj.data[i].val);
            	   if(typeof objDataVal !== 'undefined' && objDataVal.indexOf("ElementAuthorizationImp") > 0) { //save ElementAuthorizationImp holder (we make his eval last)
            		   authzHolder = objDataVal;
            	   } else {
                       isDatatableApi = (!isDatatableApi && objDataVal.indexOf('onElementDataTableApiChange') != -1) ? true : isDatatableApi;
                       eval(objDataVal);
            	   } 
               }
               
               //eval ElementAuthorizationImp
               if (authzHolder != null) {
            	   eval(authzHolder);
               }
           }
           if (!isDatatableApi) {
               hideWaitMessage();
           }
       },
       error: handleAjaxError
   });
}
 
/**
 * return from onAjaxCall()
 * 
 * @param obj
 * update element content after ajax
 */
function upDateElement(obj) {
	
	var disabelFlag_ = $('#generalDisabledFlagParam').val();
	if (disabelFlag_ === 'undefined' || disabelFlag_ != 1) { //.. ajax allow if generalDisabledFlagParam is not up
		 if (typeof obj.isMandatory !== 'undefined') {
		        if (obj.isMandatory.toLowerCase() == "false") {
		            $('[id="' + obj.domId + '"]').prop('required', false);
		            if ($('label[for="' + obj.domId + '"]').length > 0) {
		                $('label[for="' + obj.domId + '"]').css('visibility', 'hidden');
		            }
		        } else {
		            $('[id="' + obj.domId + '"]').prop('required', true);
		            if ($('label[for="' + obj.domId + '"]').length > 0) {
		                $('label[for="' + obj.domId + '"]').css('visibility', 'visible');
		            }
		        }
		    }

		    if (obj.type == "chosen") {
		        upDateElementChosen(obj)
		    } else if (obj.type == "text") {
		        upDateElementGenericInput(obj);
		    } else if (obj.type == "general"||obj.type == "label") {
		        upDateElementGeneral(obj);
		    } else if (obj.type == "dataTable_clear") {
		        // yp 20112016 fix bug when no data found (send dataTable_clear as type in element data table)
		        upDateElementDataTable_clear(obj);
		    } else if (obj.type == "dataTable") {
		        upDateElementDataTable(obj);
		    } else if (obj.type == "upload") {
		        upDateElementUpload(obj);
		    } else if (obj.type == "radio") {
		        upDateElementRadio(obj);
		    } else if (obj.type == "apiElementSetter") {
		        upDateElementApiElementSetter(obj);
		    } else if (obj.type == "smartSearch") {
		        upDateElementSmartSearch(obj);
		    } else if (obj.type == "asyncIframe") {
		        upDateElementAsyncIframe(obj);
		    } else if (obj.type == "chemdoodle") {
		        upDateElementChemDoodle(obj);
		    } else if (obj.type == "CKEDITOR") {
		    	upDateElementRichtext(obj);
		    } else if (obj.type == "ElementAuthorizationImp") {
		        upDateElementAuthorizationImp(obj);
		    } else if (obj.type == "dynamicParams") {
		        upDateElementDynamicParamsImp(obj);
		    } 
		    else if (obj.type == "webixExpStepCalc") {
		        upDateElementWebixExpStepImp(obj);
		    }
		    else if (obj.type == "formulWebixExpStepCalc") {
		    	upDateElementFormulWebixExpStepImp(obj);
		    }
		    else if (obj.type == "elementGeneralCode") {
		    	upDateElementGeneralCode(obj);
		    }
		    else if(obj.type == "excelSheet"){
		    	updateElementExcelSheet(obj);
		    }
		    else {
		        if (typeof obj.isDisabled !== 'undefined') {
		            if (obj.isDisabled.toLowerCase() == "false") {
		                $('[id="' + obj.domId + '"]').removeClass('disabledclass');
		            } else {
		                $('[id="' + obj.domId + '"]').addClass('disabledclass');
		                $('[id="' + obj.domId + '"]').css('border-color', '');
		                $('[id="' + obj.domId + '"]').css('outline', '');
		            }
		        }

		        if (typeof obj.isHidden !== 'undefined') {
		            if (obj.isHidden.toLowerCase() == "false") {
		                $('[id="' + obj.domId + '"]').css('visibility', 'visible');
		            } else {
		                $('[id="' + obj.domId + '"]').css('visibility', 'hidden');
		            }
		        }
		    }    
		    
	} else { //  data table...
		if (obj.type == "dataTable_clear") {
	        // yp 20112016 fix bug when no data found (send dataTable_clear as type in element data table)
	        upDateElementDataTable_clear(obj);
	    } else if (obj.type == "dataTable") {
	        upDateElementDataTable(obj);
	    } else if (obj.type == "ElementAuthorizationImp") {
	        upDateElementAuthorizationImp(obj);
	    }
	}
   
}

function getformDataCallbacks(postArgObj, changeType, scopeElements) { 
	var  val_;
    var callbacks = [];
    var element;
    var nextCallObj;
    if(changeType == 2) { // changeType 2 - save (1 -Ajax)
        nextCallObj = {callFunc:postCallbacksSave, callArg:postArgObj, nextCall: null};

    } else {
        nextCallObj = {callFunc:postCallbackOnChangeAjax, callArg:postArgObj, nextCall: null};

    }
    if(scopeElements!==undefined && scopeElements.length>0){
    	scopeElements.find('[formElement=1]').each(function () {
	    	var elementObj = $(this);
	    	callBackFunc = getCallBackFuncByElement(elementObj, changeType);
	    	if(callBackFunc != null) {
	    		callbacks.push({callFunc:callBackFunc,callArg: $(this),nextCall:nextCallObj});
	        	nextCallObj = {callFunc:callBackFunc,callArg: $(this),nextCall:nextCallObj};
	    	} 
	    });
    } else {
	    $('[formElement=1]').each(function () {
	    	var elementObj = $(this);
	    	callBackFunc = getCallBackFuncByElement(elementObj, changeType);
	    	if(callBackFunc != null) {
	    		callbacks.push({callFunc:callBackFunc,callArg: $(this),nextCall:nextCallObj});
	        	nextCallObj = {callFunc:callBackFunc,callArg: $(this),nextCall:nextCallObj};
	    	} 
	    });
    }
    return callbacks;
}

function getCallBackFuncByElement(elementObj, changeType) { //changeType 2 - save (1 ajax)
	var funcToReturn = null;
	var element = elementObj.attr('element');
	
	if(element == 'ElementChemDoodleImp' && changeType == 2) {
		if(elementObj.attr('id').indexOf('Pln') > 0) {
			funcToReturn = getMarvinData_pln;
		} else {
			funcToReturn = getMarvinData;
		}
	}
	else if(element == 'ElementChemDoodleSearchImp') {
		funcToReturn = getMarvinSearch;
	}
	
	return funcToReturn;
}



//ab 11092017 where 'changeType' means which action is currently running: ajaxOnChange or Save  
//function getformData(changeType) {
//    //get data of elements in the form
//    var values = [], element, stringifyInfo, stringifyToPush;
//    $('[formElement=1]').each(function () {
//    		element = $(this).attr('element');
//    		stringifyInfo = '{"formPreventSave":"' + $(this).attr("formPreventSave") +
//    		'", "type":"' + $(this).attr("type") +
//    		'", "saveType":"' + $(this).attr("saveType") +
//    		'"}';
//    		stringifyToPush = {
//    				code: $(this).attr('id'),
//    				val: getValue_(element, this, changeType),
//    				type: "AJAX_BEAN",
//    				info: stringifyInfo
//    		};
//    		values.push(stringifyToPush);
//    	
//    });
//    return values;
//} // not in use ->
//function getformData(changeType) {
//	getformDataNoCallBack(changeType);
//}


function getformDataNoCallBack(changeType,scopeElements) //changeType = 1-ajax / =2 - save
{
    //get data of elements in the form
    var values = [], element, stringifyInfo, stringifyToPush, currDisplayValue, 
    	currDisplayValueToInfo = '', currUserLastSaveValueToInfo = '';
    if(scopeElements!== undefined && scopeElements .length>0){
    	var parentId = scopeElements.find('#formId').val();
    	scopeElements.find('[formElement=1]').each(function () {
    		var elementObj = $(this);
    		var elementImpCode = $(this).attr('element');
        	callBackFunc = getCallBackFuncByElement(elementObj, changeType);
        	if(callBackFunc == null) {
//    		if(element != 'ElementChemDoodleImp' && element != 'ElementChemDoodleSearchImp') { //if(element == 'ElementAutoCompleteDDLImp')  its only for CHECK callback it should be remove from here
        		if(changeType == 2) //property 'displayValue' needed to save ONLY
        		{
	        		currDisplayValue = getDisplayValue_(elementImpCode, this);
	        		currDisplayValueToInfo = '';
	        		if(currDisplayValue != null)
	        		{
	        			currDisplayValueToInfo = '", "displayValue":"' + $.trim(currDisplayValue);
	        		}
	        		
	        		currUserLastSaveValue = getUserLastSaveValue_(elementImpCode, this);
	        		currUserLastSaveValueToInfo = '';
	        		if(currUserLastSaveValue != null)
	        		{
	        			currUserLastSaveValueToInfo = '", "userLastSaveValue":"' + currUserLastSaveValue;
	        		}
        		}
    			stringifyInfo = '{"formPreventSave":"' + $(this).attr("formPreventSave") +
    			'", "type":"' + $(this).attr("type") +
    			'", "saveType":"' + $(this).attr("saveType") +
    				currDisplayValueToInfo + currUserLastSaveValueToInfo +
    			'"}';
    			stringifyToPush = {
    					code: $(this).attr('id'),
    					val: getValue_(elementImpCode, this, changeType,parentId),
    					type: "AJAX_BEAN",
    					info: stringifyInfo
    			};
    			values.push(stringifyToPush);
    		}
    	});
    } else {
	    $('[formElement=1]').each(function () {
	    		var elementObj = $(this);
	    		var elementImpCode = $(this).attr('element');
	        	callBackFunc = getCallBackFuncByElement(elementObj, changeType);
	        	if(callBackFunc == null) {
	//    		if(element != 'ElementChemDoodleImp' && element != 'ElementChemDoodleSearchImp') { //if(element == 'ElementAutoCompleteDDLImp')  its only for CHECK callback it should be remove from here
	        		if(changeType == 2) //property 'displayValue' needed to save ONLY
	        		{
		        		currDisplayValue = getDisplayValue_(elementImpCode, this);
		        		currDisplayValueToInfo = '';
		        		if(currDisplayValue != null)
		        		{
		        			currDisplayValueToInfo = '", "displayValue":"' + $.trim(currDisplayValue);
		        		}
		        		
		        		currUserLastSaveValue = getUserLastSaveValue_(elementImpCode, this);
		        		currUserLastSaveValueToInfo = '';
		        		if(currUserLastSaveValue != null)
		        		{
		        			currUserLastSaveValueToInfo = '", "userLastSaveValue":"' + currUserLastSaveValue;
		        		}
	        		}
	    			stringifyInfo = '{"formPreventSave":"' + $(this).attr("formPreventSave") +
	    			'", "type":"' + $(this).attr("type") +
	    			'", "saveType":"' + $(this).attr("saveType") +
	    				currDisplayValueToInfo + currUserLastSaveValueToInfo +
	    			'"}';
	    			stringifyToPush = {
	    					code: $(this).attr('id'),
	    					val: getValue_(elementImpCode, this, changeType),
	    					type: "AJAX_BEAN",
	    					info: stringifyInfo
	    			};
	    			values.push(stringifyToPush);
	    		}
	    });
    }
    return values;
}

//function isCallback(val) {
//    if (val === null) { return false;}
//    return ( (typeof val === 'function') );
//}

function fireNextCallback (obj, callbackResultArray_) {
	var func_ = obj.callFunc;
	var arg_ =  obj.callArg;
	var nextCall_ =  obj.nextCall;
	func_(arg_,callbackResultArray_, nextCall_);
}

/**
 * 
 * @param afterSave - close/save&forward/function etc...
 * @param saveAction
 * @param saveName
 * @param saveDescription
 * @param afterSaveAdditionalArgs - used for additional data to get used on the doSaveAfterValidation function
 * @returns
 */
function doSave(afterSave, saveAction, saveName, saveDescription, afterSaveAdditionalArgs) {
    //on save    
    var boolAttachment = 0,
        doSaveMessage;
    var attachmentAction = "";
//    var location = window.location.href;
    var saveAction_ = ""; 
     
    if (typeof(saveAction) === 'undefined') {
    	saveAction = "NA";
    }
    
    if ($('[name="uploadFile"]').length > 0) {
        if ($('[name="uploadFile"]').val() != "") {
            boolAttachment = 1;
            if($("#save_").length > 0 && $("#save_").text("Upload & Save...")) {
            	$("#save_").text("Save"); // back to save in order to have the save label back in case of failure
            }
        } else {
        	if($('#formCode').val() != "InvItemMaterial"&&$('#formCode').val() != "InvItemMaterialFr"&&$('#formCode').val() != "InvItemMaterialPr" && $('#formCode').val() != "SelfTest" && $('#formCode').val() != "Request" && $('#formCode').val() != "SpreadsheetTempla")
    		{
        		afterSave = "Close";
    		}
        }
    }
    
    if (saveAction == "SAVE_USER_SETTINGS") {
    	doSaveAfterValidation(afterSave, saveAction, boolAttachment);
    	return;
    }
    
    var mandatoryIndicator= isMandatoryFieldsRequired();
    if ((mandatoryIndicator.setRequired == '1'&& checkRequired()) || (mandatoryIndicator.setRequired == '0' && checkRequiredByList(mandatoryIndicator.mandatoryList))) {
        if (!checkDateMinMaxValidity() || !checkNumberMinMaxValidity() || !checkTimeValidity() || !checkEmailValidity() 
        		|| !elementDynamicParamsImpValidation() || !elementRichTextEditorValidation() || !elementWebixValidation()
        		||(boolAttachment == 1 && !validateUploadFile())
            ) 
        {
        		prop.onChangeAjaxFlag = false;
        		return;
        } 
        
        doSaveAfterValidation(afterSave, saveAction, boolAttachment, saveName, saveDescription,'',afterSaveAdditionalArgs);
        
    } else {
    	if(mandatoryIndicator.setRequired == '1'){
    		displayAlertDialog(getSpringMessage('PleaseFillTheRequiredFields'));
    	} else{
    		displayAlertDialog(getSpringMessage(mandatoryIndicator.message));
    	}
        
    }
    /*} else{
    	if(mandatoryIndicator.mandatoryList.length>0){
    		if(checkRequired()){//add a function that gets an array of fields and checks if they are filled
    		}
    		else{var strMessage = "Please fill the required fields:\n";
	    	for(var i = 0; i<mandatoryIndicator.mandatoryList.length;i++){
	    		strMessage += mandatoryIndicator.mandatoryList[i] + (i+1<mandatoryIndicator.mandatoryList.length? ",":"");
	    	}
	    	displayAlertDialog(getSpringMessage(strMessage));
	    	return;
    		}
    	}
    }*/
}

function doSaveAfterValidation(afterSave, saveAction, boolAttachment, saveName, saveDescription, scopeElements, additionalInfAfterSave) {
	
	 var callbackResultArray_ = [];
	 
	showWaitMessage("Please wait...");
    prop.onChangeAjaxFlag = false;

    //init post save arg object
	var postSaveArgObj = {arg_saveAction: saveAction, arg_afterSave: afterSave, arg_boolAttachment: boolAttachment, arg_saveName: (saveName!==undefined)?saveName:'', arg_saveDescription: (saveDescription!==undefined)?saveDescription:'',arg_scopeElements:scopeElements ,arg_additionalInfAfterSave:additionalInfAfterSave};
	// get all callback element in array
	var changeType = 2; //changeType = 1-ajax / =2 - save
    var allCallbacks = getformDataCallbacks(postSaveArgObj, changeType, scopeElements);
    if (allCallbacks != null && allCallbacks.length > 0) {
    	 //fire the first callback
        fireNextCallback(allCallbacks.pop(), callbackResultArray_);
    } else {
    	// fire postCallbacksSave
    	postCallbacksSave(postSaveArgObj, callbackResultArray_);
    }
}
 
function postCallbacksSave(postSaveArgObj, callbackResultArray_) {
	
	var saveAction = postSaveArgObj.arg_saveAction;
	var afterSave = postSaveArgObj.arg_afterSave;
	var boolAttachment = postSaveArgObj.arg_boolAttachment;
	var saveName = postSaveArgObj.arg_saveName;
	var saveDescription = postSaveArgObj.arg_saveDescription;
	var allDataNoCallBack = getformDataNoCallBack(2,postSaveArgObj.arg_scopeElements);
	var allData = allDataNoCallBack.concat(callbackResultArray_);
	var additionalInfAfterSave = postSaveArgObj.arg_additionalInfAfterSave;
	var scopeElements = postSaveArgObj.arg_scopeElements;
//	var allData = getformData(2); //changeType - 'save'
	
	if(boolAttachment == 1)//scopeElements was not handled
	{
		var uploadFailedBln = false;
		$('.fileUploadElementForm input[name="fileFormId"]').each(function()
		{
			if($(this).attr('elementID') == "-1")
			{
				uploadFailedBln = true;
			}
		});
		if(uploadFailedBln)
		{
			displayAlertDialog(getSpringMessage('updateFailed'));
            hideWaitMessage();
            return;
		}
	}
	
     var urlParam ='';
     if(scopeElements!=undefined && scopeElements.length>0){
    	 urlParam = "?formId=" + scopeElements.find('#formId').val() + "&formCode=" + scopeElements.find('#formCode').val() + "&userId=" + $('#userId').val() + "&saveAction=" + saveAction+ "&isNew=" + scopeElements.find('#isNew').val() + "&stateKey=" + $('#stateKey').val()
         +(saveName!=''?"&saveName="+saveName:'')
         +(saveDescription!=''?"&description="+saveDescription:'') + "&useLoginsessionidScopeFlag=" + $('#useLoginsessionidScopeFlag').val() + "&formPathInfo=" + encodeURIComponent(getFormPathInfo(scopeElements))
         +"&lastChangeUserId=" + $('#lastChangeUserId').val() + "&lastChangeDate=" + $('#lastChangeDate').val();

     } else {
         urlParam = "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&saveAction=" + saveAction+ "&isNew=" + $('#isNew').val() + "&stateKey=" + $('#stateKey').val()
         +(saveName!=''?"&saveName="+saveName:'')
         +(saveDescription!=''?"&description="+saveDescription:'') + "&useLoginsessionidScopeFlag=" + $('#useLoginsessionidScopeFlag').val() + "&formPathInfo=" + encodeURIComponent(getFormPathInfo())
         +"&lastChangeUserId=" + $('#lastChangeUserId').val() + "&lastChangeDate=" + $('#lastChangeDate').val();
     }

     var data_ = JSON.stringify({
         action: "doSave",
         data: allData,
         errorMsg: ""
     });
     
     $.ajax({
         type: 'POST',
         data: data_,
         url: "doSave.request" + urlParam,
         contentType: 'application/json',
         dataType: 'json',
         success: function (obj) {
             var errMsg = obj.errorMsg;
        	 if (errMsg != null && errMsg != '') {                 
                 hideWaitMessage();
                 
                 //yp 20092019 workaround - when validation code moved to chemdao we get this prefix "java.lang.Exception: {" so we remove it (!? need to be checked)
                 if(errMsg.indexOf("java.lang.Exception: {") >= 0) {
                	 errMsg = errMsg.replace("java.lang.Exception: {","{");
             	 }
                 
                 if(($('#formCode').val() == "InvItemMaterial"|| $('#formCode').val() == "InvItemMaterialFr"||$('#formCode').val() == "InvItemMaterialPr")
                	  	&&
                	 checkIfJSON(errMsg)
                   )
                 {
                	 showMaterialValidationErrorMsg(errMsg);                	 
                 }
                 else
                 {
                	 displayAlertDialog(errMsg);
                 }               
             } else if (obj.data[0].val == "-1") {
                 displayAlertDialog(getSpringMessage('updateFailed'));
                 hideWaitMessage();
                 insertSpreadsheetIntoLocalStorage();             
             } else if (obj.data[0].val.toString().indexOf(',') != '-1' && obj.data[0].val.toString().substring(0, 2) == '-2') {
                 doSaveMessage = getSpringMessage(obj.data[0].val.split(',')[1]);/*.split("_").join(" ").toLowerCase();*/
                 displayAlertDialog(doSaveMessage/*.charAt(0).toUpperCase() + doSaveMessage.slice(1)*/ + " " + getSpringMessage('alreadyExistsInSystem'));//ab 22/03/18 fixed bug 4161
                 hideWaitMessage();
             } else if (obj.data[0].val.toString().indexOf(',') != '-1' && obj.data[0].val.toString().substring(0, 2) == '-3') {
                 /*doSaveMessage = obj.data[0].val.split(',')[1].split("_").join(" ").toLowerCase();
                 displayAlertDialog(doSaveMessage.charAt(0).toUpperCase() + doSaveMessage.slice(1) + " " + getSpringMessage('invalidInSystem'));*/
            	 doSaveMessage = obj.data[0].val.split(',')[1];
            	 var defaultMessage = doSaveMessage.split("_").join(" ").toLowerCase();
            	 displayAlertDialog(getSpringMessage(doSaveMessage,defaultMessage.charAt(0).toUpperCase() + defaultMessage.slice(1) + " " + getSpringMessage('invalidInSystem')));
                 hideWaitMessage();
               //  insertSpreadsheetIntoLocalStorage();
             } else {    
            	 clearLocalStorage();//clear the localstorage that holds the spreadsheet data in case the spreadsheet was already saved in the DB(on the save process)
            	 if (obj.data[0].info.toString() !='') {//there's is a message to display
            		 //displayAlertDialog(getSpringMessage(obj.data[0].info.toString()));
            		 hideWaitMessage();
            		 openConfirmDialog({
            		        onConfirm: function(){
            		        	showWaitMessage();
            		        	doAftersave(afterSave,additionalInfAfterSave);
            		        },
            		        title: 'Alert',
            		        message: getSpringMessage(getSpringMessage(obj.data[0].info.toString())),
            		        onCancel: function(){
            		        	showWaitMessage();
            		        	doAftersave(afterSave,additionalInfAfterSave);
            		        }
            		    },true);
            		 
            	 }
            	 else{
            		 $.each( obj.data, function( index, bean_ ) {
            			if(bean_.type == "PRINT_ON_LOAD") {
            				 if($("#_pass_labelCode").length > 0) {
            					 $("#_pass_labelCode").val(bean_.code);
            				 }
            				 
            				 if($("#_pass_labelData").length > 0) {
            					 $("#_pass_labelData").val(bean_.val);
            				 }
            			 }
            		 });
            		 doAftersave(afterSave,additionalInfAfterSave);
            	 }
             }
         },
         error: function(xhr, textStatus, error){
        	 //insertSpreadsheetIntoLocalStorage();
        	 handleAjaxError(xhr, textStatus, error);
         } 
        	 
     });
	
}

function doAftersave(afterSave,additionalInfAfterSave)
{
	//init passPrintParam (this made for adama print label on load task so for now we pass it only afterSave action that we have in SampleMain screen)
	var passPrintParam = "";
 	if($('#_pass_labelCode').length > 0 && $('#_pass_labelCode').val() != null && $('#_pass_labelData').length > 0 && $('#_pass_labelData').val() != null) {
 		var printParam_ = JSON.stringify({
	 		passLabelCode: $('#_pass_labelCode').val(),
	 		passLabelData: $('#_pass_labelData').val()
	    });
	 	passPrintParam = "urlPrintParam=" + encodeURIComponent(printParam_);
 	}
	 	
 	// make afterSave....
	if (typeof afterSave==='function'){
		afterSave(additionalInfAfterSave);
	} else if ((typeof afterSave !== 'undefined') && (afterSave.toLowerCase() == "close")){
		var formCode = getForwardPage();
		
        //parent.$("input").first().focus().blur(); // fix ie bug cannot enter text to input
        parent.$("#prevDialog iframe").attr('src', 'about:blank'); // fix ie bug cannot enter text to input
        parent.$('#prevDialog').dialog('close');                       
        return;

    } else if ((typeof afterSave !== 'undefined') && (afterSave.toLowerCase() == "save_and_close")) {  
        if($('#backUrl').val() != "") { 
        	if(passPrintParam != "") {
        		$("#doBackForm").attr('action', 'doBack.request?' + passPrintParam);
        	}
        	var url = window.location.href;
    		if (url.indexOf('&isStructAsPopup=1') > 0){
    			if($('#_pass_labelCode').length > 0 && $('#_pass_labelCode').val() != null && $('#_pass_labelData').length > 0 && $('#_pass_labelData').val() != null) {
    				//outPutLabel('_global', $('#_pass_labelCode').val(), $('#_pass_labelData').val());
    				parent.$('#prevDialog').data('_pass_labelCode',
        					$('#_pass_labelCode').val());
    				parent.$('#prevDialog').data('_pass_labelData',
    						$('#_pass_labelData').val());
    			}
    			parent.$('#prevDialog').dialog('close');
    			return;
        	}else{
        		$("#doBackForm").submit();
        		return;
        	}	    		
   	 	} else {
	   		parent.$("#prevDialog iframe").attr('src', 'about:blank'); // fix ie bug cannot enter text to input
	   		parent.$('#prevDialog').dialog('close');
	   		return;
   	 	}
    } else if ((typeof afterSave !== 'undefined') && (afterSave.toLowerCase() == "save_and_forward")) {
   	 var formCode = getForwardPage();
		var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=" + formCode
			    + "&formId=" + $('#formId').val()
			    + "&userId=" + $('#userId').val();
		if(passPrintParam != "") {
			page=page + "&" + passPrintParam;
		}
		var url = window.location.href;
		if (url.indexOf('&isStructAsPopup=1') > 0){
			window.top.location.href = page;
			return;
    	}	
		 window.location = page;
		 return;	 
    } else if ((typeof afterSave !== 'undefined') && (afterSave.toLowerCase() == "reload")) {
	   	var url = window.location.href;
	   	 
	   	//workaround for rare scenario (fix bug in 5957)
	   	if($('#formCode').val() == "InvItemMaterial"||$('#formCode').val() == "InvItemMaterialFr"||$('#formCode').val() == "InvItemMaterialPr")
		{
	   		if (url.indexOf('&PARENT_ID=-1#') > 0) {
	   			url = url.replace('&PARENT_ID=-1#','&PARENT_ID=-1');
	   		}
		}
	   	fgReloadForm(url); //window.location.href = url;
	   //window.location.replace(url);
        return;
    } else if ((typeof afterSave !== 'undefined') && (afterSave.toLowerCase().lastIndexOf('redirect', 0) === 0)) {
        doNew(afterSave.substring('redirect:'.length));
    } else if ((typeof afterSave !== 'undefined') && (afterSave.toLowerCase().lastIndexOf('urlredirect', 0) === 0)) {
    	var indexParam = afterSave.toLowerCase().indexOf('?', 0);
    	var redirect_ = afterSave.substring('urlredirect:'.length, indexParam);
    	var appendUrl_ = afterSave.substring(indexParam + 1);
        doNew(redirect_,appendUrl_);
    } else {
 
	 displayFadeMessage(getSpringMessage('updateSuccessfully'));
	//displayAlertDialog(getSpringMessage('updateSuccessfully'),'');
    	
    }

     hideWaitMessage();
}

/**
 * @returns formCode name referenced to the accepted formId argument
 */
function getFormCodeBySeqId(formId){
	showWaitMessage("Please wait...");
	prop.onChangeAjaxFlag = false;
	// get all data and add removeIndexId
	var allData = getformDataNoCallBack(1);

	// url call
	var urlParam = "?formId=" + formId + "&formCode=&userId=" + $('#userId').val()
			+ "&eventAction=getFormCodeBySeqId&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "doSave",
		data : allData,
		errorMsg : ""
	});
	var formCode ='';

	// call...
	$
			.ajax({
				type : 'POST',
				data : data_,
				url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
				contentType : 'application/json',
				dataType : 'json',
				async: false,
				success : function(obj) {
					hideWaitMessage();

					formCode = obj.data[0].val;
					if (formCode == '') {
						displayAlertDialog(getSpringMessage('formCode of current formId:'+formId+' was not found'));
					}
				},
				error : handleAjaxError
			});
	return formCode;
}

function getSysProp(porpName,defaultValue){
	var propVal='';
	$.ajax({
        type: 'POST',
        data: '{"action" : "getPropByName","' + 'data":[' + '{"code":"porpName","val":"' +porpName + '"},' + '{"code":"defaultValue","val":"' + defaultValue + '"}' + '],' + '"errorMsg":""}',
        url: "./getPropByName.request",
        contentType: 'application/json',
        dataType: 'json',
        async: false,
        success: function (obj) {
        	propVal = obj.data[0].val;
        },
        error: function (){
        	handleAjaxError
        }
    }); 
	return propVal;
}
;
/**
 * check Time Validity
 * @returns
 */
function checkTimeValidity() {
    var flag = true, label, labelDisplay, compareLabelDisplay;
    var min, max, compareValue, timeVal, regex = /^([01][0-9]|2[0-3]):?[0-5][0-9]$/;
    $('[formElement=1][type_="time"]').each(function () {
        timeVal = $(this).val();      
        if (!regex.test(timeVal) && timeVal != "") {          
            displayAlertDialog(getSpringMessage('enterValid') + " " + getSpringMessage('time'));            
            flag = false;
        }
        min = $(this).attr('min');
        max = $(this).attr('max');
        if (flag && min) {        	
        	if (('[id="' + min + '"]').length) {
        		compareValue = $('[id="' + min + '"]').val();
        		compareLabelDisplay = $('[for="' + this.min + '"]').siblings('label').text().slice(0, -1);
        	}
        	else{
        		compareValue = min;
        		compareLabelDisplay = min;
        	}
        	if($(this).val()!="" && compareValue !="" && regex.test(compareValue) && $(this).val() < compareValue) {
        		 label = $('[for="' + this.id + '"]').siblings('label');
                 labelDisplay = (label.length) ? label.text().slice(0, -1) : (this.id.charAt(0).toUpperCase() + this.id.slice(1));
        		 displayAlertDialog(labelDisplay + " " + getSpringMessage('mustBeGreaterThenOrEqualTo') + " " + compareLabelDisplay);
        	}
        }
        
        if (flag && max) {        	
        	if (('[id="' + max + '"]').length){
        		compareValue = $('[id="' + max + '"]').val();
        		compareLabelDisplay = $('[for="' + this.max + '"]').siblings('label').text().slice(0, -1);
        	}
        	else{
        		compareValue = max;
        		compareLabelDisplay = max;
        	}
        	if($(this).val()!="" && compareValue !="" && regex.test(compareValue) && $(this).val() > compareValue){
        		 label = $('[for="' + this.id + '"]').siblings('label');
                 labelDisplay = (label.length) ? label.text().slice(0, -1) : (this.id.charAt(0).toUpperCase() + this.id.slice(1));
        		 displayAlertDialog(labelDisplay + " " + getSpringMessage('mustBeLessThenOrEqualTo') + " " + compareLabelDisplay);
                 flag = false;
        	}
        }
    });
    return flag;
}

/**
 * check Email Validity
 * @returns
 */
function checkEmailValidity() {
    var flag = true;
    $('[formElement=1][type_="email"]').each(function () {
        var email = $(this).val();
        var domId = $(this).attr('id');
        var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
        if (!regex.test(email) && email != "") {
            displayAlertDialog(getSpringMessage('enterValid') + " " + getSpringMessage('email'));
            flag = false;
        }
    });
    return flag;
}

/**
 * @returns true if all dates with attribute [formElement=1] contains valid data (Min and Max Validity).
 */
function checkDateMinMaxValidity() {
    var bool = true,
        min, max, date,
        isValidDate, compareDateMin, compareDateMax,
        isValidCompareDateMin, isValidCompareDateMax, element, compareDateDisplay, dateDisplay, dateLabel;
    $('[formElement=1].date-picker').each(function () {
        compareDateDisplay = min = ((typeof this.min !== 'undefined') && (this.min != '')) ? this.min : "";
        max = ((typeof this.max !== 'undefined') && (this.max != '')) ? this.max : "";

        if ((min != '') || (max != '')) {
            date = moment(this.value, prop.dateFormat.userDateFormatClient, true);
            isValidDate = date.isValid();

            compareDateMin = (min) ? moment(this.min, prop.dateFormat.userDateFormatClient, true) : "";
            compareDateMax = (max) ? moment(this.max, prop.dateFormat.userDateFormatClient, true) : "";

            isValidCompareDateMin = (compareDateMin) ? compareDateMin.isValid() : "";
            isValidCompareDateMax = (compareDateMax) ? compareDateMax.isValid() : "";

            if (isValidCompareDateMin === false) {
                element = $('#' + this.min);
                if (element.length) {
                    compareDateDisplay = $('[for="' + this.min + '"]').siblings('label').text().slice(0, -1);
                    compareDateMin = (element.val()) ? moment(element.val(), prop.dateFormat.userDateFormatClient, true) : "";
                    isValidCompareDateMin = (compareDateMin) ? compareDateMin.isValid() : "";
                }
            }

            if ((isValidCompareDateMin === true) && (((isValidDate) && (date < compareDateMin)) || ((!isValidDate) && (this.required == true)))) {
                dateLabel = $('[for="' + this.id + '"]').siblings('label');
                dateDisplay = (dateLabel.length) ? dateLabel.text().slice(0, -1) : (this.id.charAt(0).toUpperCase() + this.id.slice(1));
                displayAlertDialog(dateDisplay + " " + getSpringMessage('mustBeGreaterThenOrEqualTo') + " " + compareDateDisplay);
                bool = false;
                return false;
            }

            compareDateDisplay = max;

            if (isValidCompareDateMax === false) {
                element = $('#' + this.max);
                if (element.length) {
                    compareDateDisplay = $('[for="' + this.max + '"]').siblings('label').text().slice(0, -1);
                    compareDateMax = (element.val()) ? moment(element.val(), prop.dateFormat.userDateFormatClient, true) : "";
                    isValidCompareDateMax = (compareDateMax) ? compareDateMax.isValid() : "";
                }
            }

            if ((isValidCompareDateMax === true) && (((isValidDate) && (date > compareDateMax))||((!isValidDate) && (this.required == true)))) {
                dateLabel = $('[for="' + this.id + '"]').siblings('label');
                dateDisplay = (dateLabel.length) ? dateLabel.text().slice(0, -1) : (this.id.charAt(0).toUpperCase() + this.id.slice(1));
                displayAlertDialog(dateDisplay + " " + getSpringMessage('mustBeLessThenOrEqualTo') + " " + compareDateDisplay);
                bool = false;
                return false;
            }
        }
    });


    //	 $('[formElement=1]').each(function() {
    //		//example for using JavaScript Validation API.
    //		if(this.checkValidity && !this.checkValidity()){
    //			 displayAlertDialog( $('[for="' + this.id + '"]').siblings('label').text() + "<br><br>" +this.validationMessage);
    //			 bool = false;
    //			 return false;
    //		}		
    //	 });	 

    return bool;
}

/**
 * @returns true if all inputs with type numbers with attribute [formElement=1] contains valid data (Min and Max Validity).
 */
function checkNumberMinMaxValidity() {
    var bool = true,
        min, max, compareNumDisplay, thisLabel, thisDisplay, compareLabel;
    $('[formElement=1][type="Number"]:not(.authorizationDisabled,:disabled,.disabledclass)').each(function () {
        min = ((typeof this.min !== 'undefined') && (this.min != '')) ? this.min : "";
        max = ((typeof this.max !== 'undefined') && (this.max != '')) ? this.max : "";
        
        //min = (min) ? ((!isNaN(min)) ? min : (($('#' + min)).length) ? $('#' + min).val() : "") : "";
        //max = (max) ? ((!isNaN(max)) ? max : (($('#' + max)).length) ? $('#' + max).val() : "") : "";
        
        //if ((min != "") && (Number(this.value) < Number(min)))
        if (compareNormalNumbers(this,min,'<')) 
        {
            thisLabel = $('[for="' + this.id + '"]').siblings('label');
            thisDisplay = (thisLabel.length) ? thisLabel.text().slice(0, -1) : (this.id.charAt(0).toUpperCase() + this.id.slice(1));
            compareLabel = $('[for="' + this.min + '"]').siblings('label');
            compareNumDisplay = isNaN(this.min) ? ((compareLabel.length) ?
                compareLabel.text().slice(0, -1) : (this.min.charAt(0).toUpperCase() + this.min.slice(1))) : this.min;
            displayAlertDialog(thisDisplay + " " + getSpringMessage('mustBeGreaterThenOrEqualTo') + " " + compareNumDisplay);
            bool = false;
            return false;
        }
        //if ((max != "") && (Number(this.value) > Number(max)))
        if (compareNormalNumbers(this,max,'>')) 
        {
            thisLabel = $('[for="' + this.id + '"]').siblings('label');
            thisDisplay = (thisLabel.length) ? thisLabel.text().slice(0, -1) : (this.id.charAt(0).toUpperCase() + this.id.slice(1));
            compareLabel = $('[for="' + this.max + '"]').siblings('label');

            compareNumDisplay = isNaN(this.max) ?
                ((compareLabel.length) ? compareLabel.text().slice(0, -1) : (this.max.charAt(0).toUpperCase() + this.max.slice(1))) : this.max;
            displayAlertDialog(thisDisplay + " " + getSpringMessage('mustBeLessThenOrEqualTo') + " " + compareNumDisplay);
            bool = false;
            return false;
        }
    });
    return bool;
}

function compareNormalNumbers(o,compareVal,compareSign)
{
	console.log('compareNormalNumbers()');
	console.log("id: " + o.id + "  value: " + compareVal + " compareSign: " + compareSign);
	
	var isObject = false;
	var compareObjID = "";
	try
	{
		if(compareVal)
		{
			if(!isNaN(compareVal))// check if Not a Number
			{
				compareVal = compareVal;
			}
			else if(($('#' + compareVal)).length)
			{
				isObject = true;
				compareObjID = compareVal;
				compareVal = $('#' + compareVal).val();			
			}
			else
				compareVal = "";
		}
		else
			compareVal = "";
		
		if(compareVal != "")
		{
			var selO = $('[elementuom][elementid="'+o.id+'"]').find(':selected');
			//console.log(selO);
			var type = $.trim(selO.attr('uomtypename'));
			var factor = selO.attr('factor');
			if(factor == null || factor == "")factor = 1;
			var typeCompare = "", factorCompare = "";
			
			console.log(type + "|"+factor);
			
			if(isObject)
			{
				var selO2 = $('[elementuom][elementid="'+compareObjID+'"]').find(':selected');
				typeCompare = $.trim(selO2.attr('uomtypename'));
				factorCompare = selO2.attr('factor');			
				console.log(typeCompare + "||"+factorCompare);
				if(factorCompare == null || factorCompare == "")
					factorCompare = 1;
			}
			else
				factorCompare = 1;
			
			if(type != typeCompare)
			{
				factor = 1;
				factorCompare = 1;
			}
			else
			{
				factor = parseFloat(factor);
				factorCompare = parseFloat(factorCompare);
			}
			console.log(parseFloat(Number(o.value)) + "|||" + factor + "|||" + parseFloat(compareVal) + "|||" + factorCompare);
			if((compareSign == '<') && (parseFloat(Number(o.value)) * factor) < (parseFloat(Number(compareVal)) * factorCompare))
			{	
				return true;
			}
			else if((compareSign == '>') && (parseFloat(Number(o.value)) * factor) > (parseFloat(Number(compareVal)) * factorCompare))
			{	
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	catch(e)
	{
		console.log(e);
		return false;
	}
}

function setCookie(cname, cvalue, exdays) {
  var d = new Date();
  d.setTime(d.getTime() + (exdays * 24 * 60 * 60 * 1000));
  var expires = "expires="+d.toUTCString();
  document.cookie = cname + "=" + cvalue + ";" + expires + ";path=/";
}

function getCookie(cname) {
  var name = cname + "=";
  var ca = document.cookie.split(';');
  for(var i = 0; i < ca.length; i++) {
    var c = ca[i];
    while (c.charAt(0) == ' ') {
      c = c.substring(1);
    }
    if (c.indexOf(name) == 0) {
      return c.substring(name.length, c.length);
    }
  }
  return "";
}

function doSplit() {
	$('#save_').attr('onclick', '');//fixed bug 7476 - duplicated batches.Happens when the user clicks the OK button more than once
    /*if ((Number($('#splitQuantity').val()) > Number($('#currentQuantity').val())) || Number($('#splitQuantity').val()) < 1) {
        displayAlertDialog(getSpringMessage('splitedFailed'));
        return;
    }*/
    $.ajax({
        type: 'POST',
        data: '{"action" : "doSplit","' + 'data":[' + '{"code":"formId","val":"' + $('#FORM_ID').val() + '"},' + '{"code":"currentQuantity","val":"' + $('#currentQuantity').val() + '"},' + '{"code":"splitQuantity","val":"' + $('#splitQuantity').val() + '"},' + '{"code":"splitQuantityUom","val":"' + $('#splitQuantityUnits').val() + '"}' + '],' + '"errorMsg":""}',
        url: "./doSplit.request",
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) {
            if (obj.errorMsg != null && obj.errorMsg != '') {
                displayAlertDialog(obj.errorMsg);
            } else if (obj.data[0].val == "-1") {
            	displayAlertDialog(getSpringMessage('SPLITED_FAILED'));
            } else if (obj.data[0].val.toString().indexOf(',') != '-1' && obj.data[0].val.toString().substring(0, 2) == '-3') {
	             doSaveMessage = obj.data[0].val.split(',')[1];
	           	 var defaultMessage = doSaveMessage.split("_").join(" ").toLowerCase();
	           	 displayAlertDialog(getSpringMessage(doSaveMessage,defaultMessage.charAt(0).toUpperCase() + defaultMessage.slice(1) + " " + getSpringMessage('invalidInSystem')));
	           	 hideWaitMessage();
            } else {
                parent.$("iframe").attr('src', 'about:blank'); // fix ie bug cannot enter text to input
                parent.$('#prevDialog').dialog('close');
            }
        },
        error: handleAjaxError
    });
}

function doMultiClone() {
	$('#save_').attr('onclick', '');//fixed bug 7476 - duplicated batches.Happens when the user clicks the OK button more than once
	//show the in process... label with fade
	$("font[color=red]").css('display', 'block');
	$("font[color=red]").fadeOut(3000);
    if ((Number($('#cloneQuantity').val()) < 1) || (Number($('#cloneQuantity').val()) > 100)) {
        displayAlertDialog(getSpringMessage('cloneFailed_Quantity'));
        return;
    }
    $.ajax({
        type: 'POST',
        data: '{"action" : "doMultiClone","' + 'data":[' + '{"code":"formId","val":"' + $('#FORM_ID').val() + '"},' + '{"code":"cloneQuantity","val":"' + $('#cloneQuantity').val() + '"}' + '],' + '"errorMsg":""}',
        url: "./doMultiClone.request",
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) {
            if (obj.errorMsg != null && obj.errorMsg != '') {
                displayAlertDialog(obj.errorMsg);
            } else if (obj.data[0].val == "-1") {
                displayAlertDialog(getSpringMessage('cloneFailed'));
            } else if (obj.data[0].val.toString().indexOf(',') != '-1' && obj.data[0].val.toString().substring(0, 2) == '-3') {
	             doSaveMessage = obj.data[0].val.split(',')[1];
	           	 var defaultMessage = doSaveMessage.split("_").join(" ").toLowerCase();
	           	 displayAlertDialog(getSpringMessage(doSaveMessage,defaultMessage.charAt(0).toUpperCase() + defaultMessage.slice(1) + " " + getSpringMessage('invalidInSystem')));
	           	 hideWaitMessage();
            } else {
                parent.$("iframe").attr('src', 'about:blank'); // fix ie bug cannot enter text to input
                parent.$('#prevDialog').dialog('close');
            }
        },
        error: handleAjaxError
    });
}

getValue_ = function (trait, input, changeType,parentId) //ab 11092017 where 'changeType' means which action is currently running: changeType = 1-ajax / =2 - save
{
    var chngType = changeType;
    if(changeType == null || changeType == 'undefined') chngType = 0;
	trait = this[trait];
    var o = Object.create(Object.prototype, Trait(trait));
    return o.value_(input, chngType,parentId);
}

getDisplayValue_ = function (trait, input) 
{
	trait = this[trait];
    var o = Object.create(Object.prototype, Trait(trait));
    var toReturn = null;
    try
    {
    	if(o.hasOwnProperty('displayValue_')) {
    		toReturn = o.displayValue_(input);
    		if(toReturn != null) {
    			toReturn = (toReturn.replace(/"/g, '\\"')).replace(/\n/g,' '); // add back slash to " / yp 07082018 replace new line with empty value
    		}
    	}
    }
    catch(e){}
//    alert('deisplay return: ' + toReturn);
    return toReturn;
}

getUserLastSaveValue_ =  function (trait, input) 
{
	trait = this[trait];
    var o = Object.create(Object.prototype, Trait(trait));
    var toReturn = null;
    try
    {
    	if(o.hasOwnProperty('userLastSaveValue_')) {
    		toReturn = o.userLastSaveValue_(input);
    		if(toReturn != null) {
    			toReturn = toReturn.replace(/"/g, '\\"');
    		}
    	}
    }
    catch(e){}
    return toReturn;
}

setValue_ = function (trait, input) {
    trait = this[trait];
    var o = Object.create(Object.prototype, Trait(trait));
    o.setvalue_(input);
}

/**
 * checkrequiredByList- validates that all the elements in the that were sent in the argument are filled
 * @param requiredElements
 * @returns
 */
function checkRequiredByList(requiredElements) 
{
	console.log('checkRequired()');
	
	var changedTabsObj = [];
	var bool = 1;
	var loopThroughObj;
	var inputO;
	var currInputO = {};
	for(var i = 0; i<requiredElements.length; i++){
		if ($('ul[role="tablist"]').length > 0) 
		{		
			try 
			{
				var activeTabIndex = $("#tempalteTabs").tabs('option', 'active');  
				//console.log(activeTabIndex);
				var activeTabID = "";
				//$('ul[role="tablist"] li:not([style*="display: none"]) >a').each(function(i)
				$('ul[role="tablist"] li').each(function(index)
				{
					//console.log("css display = " + $(this).css("display"));
					if($(this).css("display") != "none")
					{
						var href = ($(this).find('a')).attr('href');
						var id = href.substr(1,href.length);
					    if(activeTabIndex == index)activeTabID = id;
					    
					    loopThroughObj = $('div#'+id).find(requiredElements[i]);
					    inputO = checkRequiredByScreen(loopThroughObj);
					    console.log("tab ID: " + id + "  ,inputO:");
					    console.log(inputO);
					    if (Object.keys(inputO).length > 0)
					    {
					    	bool = 0;
					    	changedTabsObj[id] = inputO;
					    }
				    }
				});
				if(Object.keys(changedTabsObj).length > 0)
				{
					if(!changedTabsObj.hasOwnProperty(activeTabID))
					{
						for(var key in changedTabsObj) 
						{
							if(changedTabsObj.hasOwnProperty(key))
							{
								currInputO = changedTabsObj[key];
								break;
							}
					    }
						//console.log("firstKey: " + key);
						$('ul[role="tablist"] li > a[href="#'+key+'"]').click();
					}
					else
					{
						currInputO = changedTabsObj[activeTabID];				
					}
				}
			} 
			catch (e) 
			{
				console.log("checkRequired(1) error: " + e);
				currInputO = {};
			}
			
	    }
		else
		{
			loopThroughObj = $(requiredElements[i]);
			currInputO = checkRequiredByScreen(loopThroughObj);
		}
	    if (Object.keys(currInputO).length > 0)
		{
			bool = 0;
			console.log("currInputO:");
		    console.log(currInputO);
		    try 
		    {
		    	var _el = currInputO['element'];
			    var _input = currInputO['input'];
			    if(_el == 'chosen')
			    {
			    	$(_input).trigger('chosen:activate');
			    }
			    else if(_el == 'div')
			    {
			    	document.getElementById(_input).scrollIntoView(true);
			    }
			    else //'basic'
			    	$(_input).focus();	    
			} 
		    catch (e) 
		    {
		    	console.log("checkRequired(2) error: " + e);
			}	    
		}
	}
	
	
	//return false;
	return bool;
}



/**
 * checkRequired
 * @returns
 */
function checkRequired() 
{
	console.log('checkRequired()');
	
	var changedTabsObj = [];
	var bool = 1;
	var loopThroughObj;
	var inputO;
	var currInputO = {};
	if ($('ul[role="tablist"]').length > 0) 
	{		
		try 
		{
			var activeTabIndex = $("#tempalteTabs").tabs('option', 'active');  
			//console.log(activeTabIndex);
			var activeTabID = "";
			//$('ul[role="tablist"] li:not([style*="display: none"]) >a').each(function(i)
			$('ul[role="tablist"] li').each(function(i)
			{
				//console.log("css display = " + $(this).css("display"));
				if($(this).css("display") != "none")
				{
					var href = ($(this).find('a')).attr('href');
					var id = href.substr(1,href.length);
				    if(activeTabIndex == i)activeTabID = id;
				    
				    loopThroughObj = $('div#'+id).find('[formElement=1]');
				    inputO = checkRequiredByScreen(loopThroughObj);
				    console.log("tab ID: " + id + "  ,inputO:");
				    console.log(inputO);
				    if (Object.keys(inputO).length > 0)
				    {
				    	bool = 0;
				    	changedTabsObj[id] = inputO;
				    }
			    }
			});
			if(Object.keys(changedTabsObj).length > 0)
			{
				if(!changedTabsObj.hasOwnProperty(activeTabID))
				{
					for(var key in changedTabsObj) 
					{
						if(changedTabsObj.hasOwnProperty(key))
						{
							currInputO = changedTabsObj[key];
							break;
						}
				    }
					//console.log("firstKey: " + key);
					$('ul[role="tablist"] li > a[href="#'+key+'"]').click();
				}
				else
				{
					currInputO = changedTabsObj[activeTabID];				
				}
			}
		} 
		catch (e) 
		{
			console.log("checkRequired(1) error: " + e);
			currInputO = {};
		}
		
    }
	else
	{
		loopThroughObj = $('[formElement=1]');
		currInputO = checkRequiredByScreen(loopThroughObj);
	}
    if (Object.keys(currInputO).length > 0)
	{
		bool = 0;
		console.log("currInputO:");
	    console.log(currInputO);
	    try 
	    {
	    	var _el = currInputO['element'];
		    var _input = currInputO['input'];
		    if(_el == 'chosen')
		    {
		    	$(_input).trigger('chosen:activate');
		    }
		    else if(_el == 'div')
		    {
		    	document.getElementById(_input).scrollIntoView(true);
		    }
		    else //'basic'
		    	$(_input).focus();	    
		} 
	    catch (e) 
	    {
	    	console.log("checkRequired(2) error: " + e);
		}	    
	}
	
	
	//return false;
	return bool;
}

function checkRequiredByScreen(loopThroughObj) {
    //check for required fileds
    var bool = 1;
    var focusFlag = 1;
    //var input;
    var inputObj = {};
    var id_;
    loopThroughObj.each(function (i) {
        var attr = $(this).attr('required');
        if (typeof attr !== typeof undefined && attr !== false) {
            if ((typeof $(this).attr('type') !== "undefined") && (($(this).attr('type').toUpperCase() == "TEXT") || ($(this).attr('type').toUpperCase() == "NUMBER"))) {
                if (($(this).val() == "") || ($(this).val().replace(/\s/g, '').length == 0) || ($(this).val() == "00/00/0000")) {
                    $(this).css('border-color', '#a94442').css('outline', 'transparent');
                    bool = 0;
                    if (focusFlag == 1) {
                        //input = this;
                    	inputObj = {'element':'basic','input':this};
                        focusFlag = 0;
                    }
                } else {
                    $(this).css('border-color', '').css('outline', '');
                }
            } else if ((typeof $(this).attr('type') !== "undefined") && ($(this).attr('type').toUpperCase() == "CHECKBOX")) {
                if (!$(this).is(":checked")) {
                    $(this).css('outline', '1px solid #a94442');
                    bool = 0;
                    if (focusFlag == 1) {
                        //input = this;
                    	inputObj = {'element':'basic','input':this};
                        focusFlag = 0;
                    }
                } else $(this).css('outline', '');
            } 
            else if ($(this).hasClass('chosen-select')) 
            {
                if (($(this).val() == "") || ($(this).val() == null)) 
                {
                	id_ = $(this).attr('id');
                	$('#' + id_ + '_chosen').css('outline', '1px solid #a94442').find(':first').css('border-color', '#a94442');
                    bool = 0;
                    if (focusFlag == 1) {
                        //input = $('#'+id_);
                        inputObj = {'element':'chosen','input':$('#'+id_)};
                        focusFlag = 0;
                    }
                } else {
                    $('#' + $(this).attr('id') + '_chosen').css('outline', '').find('a:first').css('border-color', '');
                }
            } else if ($(this).get(0).tagName == "TEXTAREA") {
                if (!$(this).hasClass('ckeditor')) {
                    if (($(this).val() == "") || ($(this).val().replace(/\s/g, '').length == 0)) {
                        $(this).css('border-color', '#a94442').css('outline', 'transparent');
                        bool = 0;
                        if (focusFlag == 1) {
                            //input = this;
                            inputObj = {'element':'basic','input':this};
                            focusFlag = 0;
                        }
                    } else {
                        $(this).css('border-color', '').css('outline', '');
                    }
                }
            } else if($(this).eq(0).hasClass('ckeditor')){//richtext
                id_ = $(this).attr('id');
                try{
                    if (isRichTextEmpty($(this))) {
                        $('[id="' + id_ + '_parent"]').find('.note-editor').css('border-color', '#a94442').css('outline', 'transparent');
                        bool = 0;
                        if (focusFlag == 1) 
                        {
                        	//input = CKEDITOR.instances[id_];
                        	//document.getElementById(id_ + '_parent').scrollIntoView(true);
                        	inputObj = {'element':'div','input':id_ + '_parent'};
                            focusFlag = 0;
                        }
                    } else {
                        $('[id="' + id_ + '_parent"]').find('.note-editor').css('border-color', '').css('outline', '');
                    }
                }
                catch(Err){//adib 19072020 added try-catch. if the element was not loaded, probably the user has not changed it yet
                	console.log(Err);
                	if($('#'+id).text()==''){
                		$('[id="' + id_ + '_parent"]').find('.note-editor').css('border-color', '#a94442').css('outline', 'transparent');
                        bool = 0;
                        if (focusFlag == 1) 
                        {
                        	//input = CKEDITOR.instances[id_];
                        	//document.getElementById(id_ + '_parent').scrollIntoView(true);
                        	inputObj = {'element':'div','input':id_ + '_parent'};
                            focusFlag = 0;
                        }
                    } else {
                        $('[id="' + id_ + '_parent"]').find('.note-editor').css('border-color', '').css('outline', '');
                    }
            	}
                
            } else if ((typeof $(this).attr('type') !== "undefined") && ($(this).attr('type').toUpperCase() == "HIDDEN")) {
                if ((typeof $(this).attr('name') !== "undefined") 
                		&& ($(this).attr('name').toUpperCase() == "FILE_ID"||$(this).attr('name').toUpperCase() == "FILEFORMID")) {
                    if ($('[name="uploadFile"]').val() == "" && $('.fileUploadElementForm input[id=\"'+$(this).attr('id')+'\"]').attr('elementID')=="") {//adib 20062019-removed this condition since the logic was changed && $('#removeFileDiv_'+$(this).attr('id')).css('display') == 'none'
                        $('[name="uploadFile"]').parent().css('outline', '1px solid #a94442').css('border','1px solid #a94442');
                        bool = 0;
                        if (focusFlag == 1) {
                            //input = this;
                            inputObj = {'element':'basic','input':this};
                            focusFlag = 0;
                        }
                    } else {
                        $('[name="uploadFile"]').parent().css('outline', '').css('border','');
                    }
                }
            } else if ((typeof $(this).attr('element') !== "undefined") && ($(this).attr('element') == "ElementDataTableApiImp")) {
                var domId = $(this).attr('id');
                var selectedTable = $('[id="' + domId + '"]').DataTable();                
                if (selectedTable.page.info().recordsDisplay === 0) 
                {
                	if($('#' + domId + '_sharedFormId').val() == '' || $('#' + domId + '_sharedFormId').val() =='-1'){
	                	var elem = $('[id="' + domId + '_Parent"]');
	                	elem.css('cssText', (elem.attr('style')==undefined?"":elem.attr('style')) + 'outline: 1px solid #a94442 !important'); // it's important to add new css to already exists style                    
	                    bool = 0;                    
	                    if (focusFlag == 1) 
	                    {                    	
	                    	inputObj = {'element':'div','input':domId + '_Parent'};
	                    	//document.getElementById('procedure_Parent').scrollIntoView(true);
	                        focusFlag = 0;
	                    }
                	} else {
                		$('[id="' + domId + '_Parent"]').css('outline', '');
                	}
                }
                else if ($(this).closest('div[parentelement]').is('[chooserequire]')) {
                    var custid = selectedTable.row('.selected').data();
                    if (typeof custid === 'undefined') {
                    	var elem = $('[id="' + domId + '_Parent"]');
                    	elem.css('cssText', elem.attr('style') + 'outline: 1px solid #a94442 !important'); // it's important to add new css to already exists style  
                        bool = 0;
                        if (focusFlag == 1) 
                        {                    	
                        	inputObj = {'element':'div','input':domId + '_Parent'};
                        	//document.getElementById('procedure_Parent').scrollIntoView(true);
                            focusFlag = 0;
                        }
                    } else {
                        $('[id="' + domId + '_Parent"]').css('outline', '');
                    }
                }
                else {
                    $('[id="' + domId + '_Parent"]').css('outline', '');
                }
            }
        }
    });
       
    return inputObj;
}

function QueryString() {

    // the return value is assigned to QueryString!
    var query_string = {};
    var query = window.location.search.substring(1);
    var vars = query.split("&");
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split("=");
        // If first entry with this name
        if (typeof query_string[pair[0]] === "undefined") {
            query_string[pair[0]] = decodeURIComponent(pair[1]);
            // If second entry with this name
        } else if (typeof query_string[pair[0]] === "string") {
            var arr = [query_string[pair[0]], decodeURIComponent(pair[1])];
            query_string[pair[0]] = arr;
            // If third or later entry with this name
        } else {
            query_string[pair[0]].push(decodeURIComponent(pair[1]));
        }
    }
    return query_string;
}

///**
// * title and subtitle of the form
// * @returns
// */
//function getTitleAndSubtitleForm() {
//    $.ajax({
//        type: 'POST',
//        data: '{"action" : "getTitleAndSubtitleForm","' + 'data":[{"code":"","val":"' + $('#formCode').val() + '"},{"code":"","val":"' + $('#stateKey').val() + '"}],' + '"errorMsg":""}',
//        url: "./getTitleAndSubtitleForm.request",
//        contentType: 'application/json',
//        dataType: 'json',
//        success: function (obj) {
//        	
//            if (obj.errorMsg != null && obj.errorMsg != '') {
//                displayAlertDialog(obj.errorMsg);
//            } else {
//            		var array = obj.data[0].val.split(",");
//                $('#pageTitle').html(array[0]);
//                $('#pageSubTitle').html(array[1]);
//                $('#pageTitle').css('white-space','nowrap');
//            }
//        },
//        error: handleAjaxError
//    });
//}

function initWaitMessageDiv(obj) {
    // init Wait Message function
    var bodyElem = $('body')[0];
    if (bodyElem == null)
        return;

    var _obj = null;
    if (arguments != null && typeof arguments[0] == 'object') {
        _obj = arguments[0];
    }
    var _width = (_obj != null && _obj.width != null) ? _obj.width : 300;
    var _height = (_obj != null && _obj.height != null) ? _obj.height : 100;

    var _left_default = ($(document).width() / 2) - (_width / 2);
    var _top_default = ($(window).height() / 2) - (_height);
    //alert($(window).height() + " : "+ $(document).height());
    var _left = (_obj != null && _obj.left != null) ? _obj.left : _left_default;
    var _top = (_obj != null && _obj.top != null) ? _obj.top : _top_default;

    var firstDiv = document.createElement('div');
    $(firstDiv).attr({
        'id': 'mask',
        'class': 'pageDefaultMask'
    }).append();
    bodyElem.appendChild(firstDiv);

    var secDiv = document.createElement('div');
    $(secDiv).attr({
            'id': 'hiddenformMessage',
            'class': 'cssWaitMessageDiv'
        })
        .on('click', hideWaitMessage).css({
            'top': _top,
            'left': _left,
            'width': _width,
            'height': _height
        });
    $(secDiv).append($('<h2>').attr({
            'id': 'waitMessage',
            'class': 'cssUserMessage'
        }))
        //.text('Please wait....'))
        .append($('<img style="width:32px;height:32px;">').attr('src', '../skylineFormWebapp/images/circular.gif'));
    bodyElem.appendChild(secDiv);
}

function showWaitMessage(obj,isMaskShown) {
    // show Wait Message
    var _obj = null;
    var _text = null;
    var counter = "0";
    /* waitMessageCounter is hidden input in jsp/General/jspTemplateWithMenu
     * The counter holds the number of calls to the showWaitMessage function and decreases each call to the hideWaitMessage function.
     * The hideWaitMessage function is only performed on the last call when the counter is reset
     * Fixed bug 7581
     * $('#formCode').val() == "SearchReport" - fixed bug when formCode = InvItemSamplesMain/ExperimAuditTrail*/
    if($('#formCode').val() == "SearchReport" && $('#waitMessageCounter').val()!=undefined && $('#waitMessageCounter').val() != null){
    	counter = parseInt($('#waitMessageCounter').val());
    	$('#waitMessageCounter').val(counter+1);
    }

    if (typeof arguments[0] == 'object') {
        _obj = arguments[0];
    } else {
        if (typeof jsonData != "undefined") {
            _text = (arguments[0] != null && arguments[0].length != "") ? jsonData["Processing"] : jsonData["Please_Wait"]; // kd 1072016 for multilingual purpose
        } else {
            _text = (arguments[0] != null && arguments[0].length != "") ? arguments[0] : "Please wait..."; // kd 1072016 only this string was before adding if..else condition
        }
    }

    $('#waitMessage').text(_text);
    if(typeof isMaskShown == 'undefined' || isMaskShown == true){
	    $('#mask').css('z-index', '1001');
	    $("#mask").show();
    }
    if( counter == "0"){
    	$("#hiddenformMessage").show();
        $("#hiddenformMessage").css('z-index', '1002');
    }

}

function hideWaitMessage() {
	try{
		var counter = "0";
		if($('#waitMessageCounter').val()!=undefined ){
			counter = parseInt($('#waitMessageCounter').val());
		}
		// hide Wait Message
		if(counter == "0"){
			$("#hiddenformMessage").hide();
			$("#mask").hide();
		}
		else{
			$('#waitMessageCounter').val(counter-1);
			if($('#waitMessageCounter').val()=="0"){
				$("#hiddenformMessage").hide();
				$("#mask").hide();
			}
			}
	}catch(e) {
		$("#hiddenformMessage").hide();
		$("#mask").hide();
		}
}

//for datatable init
function optionChanged(option) {
    var input = $(option).parent().find('input[class="firstString"]');
    //	if($($(option).parents('table:first')[0]).attr('element') != 'ElementDataTableImp'){
    //		var icon = $(option).parent().find('i');
    //		input.val("");
    //		var spanHtml = $(option).parent().find('span');
    //		if(option.value == "co"){
    //			spanHtml.html("Contain");
    //		}
    //		else if(option.value == "cn"){
    //			spanHtml.html("Not Contain");
    //		}
    //		else if(option.value == "eq"){
    //			spanHtml.html("&#61;");
    //		}
    //		else if(option.value == "ne"){
    //			spanHtml.html("&#x2260;");
    //		}
    //		else if(option.value == "gt"){
    //			spanHtml.html("&#62;");
    //		}
    //		else if(option.value == "ge"){
    //			spanHtml.html("&#x2265;");
    //		}
    //		else if(option.value == "lt"){
    //			spanHtml.html("&#60;");
    //		}
    //		else if(option.value == "le"){
    //			spanHtml.html("&#x2264;");
    //		}
    //		datatableApiSearchSetting(icon); // show the search textbox  after choosing option
    //	}
    input.trigger('keyup');
}

//for datatable init
function searchDatatableOld(that) {
    // datatable search option
    $(that.footer()).find('input[class="firstString"]').on('keyup', function () { //, that.footer() 

        showWaitMessage();
        input = this;
        setTimeout(function () {
            if (input.value == "") {
                that.search(input.value).draw();
                hideWaitMessage();
                return;
            }
            var option = $(input).siblings('select').val();
            if (option == "cn") {
                var strVal = input.value.replace(/[*()?\[\]^\\$|_=+]/g, "\\$&")
                that.search("^((?!" + strVal + ").)*$", true, false).draw();
            } else if (option == "co") {
                that.search(input.value, false, true).draw();
                // that.search(input.value).draw();
            } else {
                var i, str = "";
                var type;
                // var colName = $(that.header()).text();
                var colName = $(input).parent().closest('table').find('thead th').eq($(input).parent().index()).text()
                var length = $('[name="metaData"]').val().length;
                var columnsArray = $('[name="metaData"]').val().substring(1, length - 1).split(",");
                for (var k = 0; k < columnsArray.length; k++) {
                    var columnArray = columnsArray[k].trim().split(":");
                    if (columnArray[0] == colName) {
                        if (columnArray[1] == "DATE")
                            type = "date";
                        else if (columnArray[1] == "NUMBER")
                            type = "number";
                        else
                            type = "string";
                        break;
                    }
                }
                if (type == "date") {
                    for (i = 0; i < that.data().length; i++) {
                        if (option == "eq") {
                            if (moment(that.data()[i], prop.dateFormat.userDateFormatClient, true).isSame(moment(input.value, prop.dateFormat.userDateFormatClient, true)))
                                str += that.data()[i] + ';';
                        } else if (option == "ne") {
                            if (!(moment(that.data()[i], prop.dateFormat.userDateFormatClient, true).isSame(moment(input.value, prop.dateFormat.userDateFormatClient, true))))
                                str += that.data()[i] + ';';
                        } else
                        if (prop.operators[option](moment(that.data()[i], prop.dateFormat.userDateFormatClient, true), moment(input.value, prop.dateFormat.userDateFormatClient, true)))
                            str += that.data()[i] + ';';
                    }
                } else if (type == "string") {
                    for (i = 0; i < that.data().length; i++) {
                        if (prop.operators[option](that.data()[i].toLowerCase(), input.value.toLowerCase()))
                            str += that.data()[i] + ';';
                    }
                } else if (type == "number") {
                    for (i = 0; i < that.data().length; i++) {
                        if (prop.operators[option](parseFloat(that.data()[i]), parseFloat(input.value)))
                            str += that.data()[i] + ';';
                    }
                }
                if (str == "") {
                    that.search("expressionNotFound").draw();
                    hideWaitMessage();
                    return;
                }
                str = str.substring(0, str.length - 1);
                //var arr = str.split(';');
                var arr = str.replace(/[*()?\[\]^\\$|_=+]/g, "\\$&").split(';');
                //var pattern = ("\\b\^" + arr.join('\$\\b|\\b\^') + '\$\\b');                
                var pattern = ("\^" + arr.join('\$|\^') + '\$')
                that.search(pattern, true, false).draw();

                if ($(that.tables().body()).find('td').hasClass('dataTables_empty')) // if search failed
                    displayAlertDialog(getSpringMessage('tableNotShowAllData'));
            }

            hideWaitMessage();
        }, 0);
    });
}

function dataTableStyle(table) {
    // datatable skyline style
    $('.dataTable th').addClass('ui-state-default');
    $('.dataTables_length').css('text-align', 'left');
    $('.dataTable').closest('[id*="_wrapper"]').find('a:not([onclick*="openAttachment"],[onclick*="smartLink"],[onclick*="smartFile"])').addClass('fg-button ui-button ui-state-default');
    $('.dataTable').closest('[id*="_wrapper"]').find('a.first').addClass('ui-corner-tl ui-corner-bl');
    $('.dataTable').closest('[id*="_wrapper"]').find('a.last').addClass('ui-corner-tr ui-corner-br');

    $('#' + table).on('draw.dt', function () {
        $(this).closest('[id*="_wrapper"]').find('a:not([onclick*="openAttachment"],[onclick*="smartLink"],[onclick*="smartFile"])').addClass('fg-button ui-button ui-state-default');
        $(this).closest('[id*="_wrapper"]').find('a.first').addClass('ui-corner-tl ui-corner-bl');
        $(this).closest('[id*="_wrapper"]').find('a.last').addClass('ui-corner-tr ui-corner-br');

        var t = $(this).DataTable();
        info = t.page.info();
        page = info.page + 1;

        $(this).closest('[id*="_wrapper"]').find('a').removeClass("ui-state-disabled");
        $(this).closest('[id*="_wrapper"]').find('a').filter(function () {
            return $(this).text() == page;
        }).addClass('ui-state-disabled');


        if ((page == 1) || (page == 0)) {
            $(this).closest('[id*="_wrapper"]').find('a').removeClass("ui-state-disabled");
            $(this).closest('[id*="_wrapper"]').find('a').filter(function () {
                return $(this).text() == "Previous" || $(this).text() == "First" || $(this).text() == "1";
            }).addClass('ui-state-disabled');
        }

        if ((info.pages == info.page) || (info.pages == info.page + 1)) {
            $(this).closest('[id*="_wrapper"]').find('a').filter(function () {
                return $(this).text() == "Last" || $(this).text() == "Next"
            }).addClass('ui-state-disabled');
        }
    });
}

function handleAjaxError(xhr, textStatus, error) {
	insertSpreadsheetIntoLocalStorage();
	hideWaitMessage();
	//alert("error: " + xhr + " status: " + textStatus + " er:" + error);
    // ajax callback error
    if (xhr.responseText.match(/TIME_IS_OUT/)) {
        top.location.href = 'Login.jsp';
    } else if (xhr.responseText.indexOf('function Login()') != -1) {
        top.location.href = 'Login.jsp';
    } else {
    	if(textStatus != null && textStatus =='error') {
    		console.log("handleAjaxError() textStatus == error");
    		console.log(error); 		
    		if(error !== 'Internal Server Error')
    		{
    			displayAlertDialog("Server connection error");
    		}
    	} else {
    	    displayAlertDialog("error: " + xhr + " status: " + textStatus + " er:" + error);
    	}
    }
    
}

function getFormTypeValues(SelectName) {
    // get all exists forms types
    $.ajax({
        type: 'POST',
        data: '{"action" : "getFormTypeValues","' + 'data":[{"code":"","val":"' + '"}],' + '"errorMsg":""}',
        url: "./getFormTypeValues.request",
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) {
            if (obj.errorMsg != null && obj.errorMsg != '') {
                displayAlertDialog(obj.errorMsg);
            } else {
                fillSelectOptions(SelectName, obj.data[0].val.split(","));
                hideUnnecessaryFormTypesFormBuilder();
            }
        },
        error: handleAjaxError
    });
}

function fillSelectOptions(SelectName, objArray) {
    // fill options in select
    var options = "", i;
    for (i = 0; i < objArray.length; i++){
        options += '<option value="' + objArray[i] + '">' + objArray[i] + '</option>';
    }
    $('[name="' + SelectName + '"]').html(options);
}

function initAlphaNum() {
    // ignore special chars
    $('.alphanumInput').alphanum({
        allow: '.{}()$_#',
        disallow: '',
        allowSpace: true,
        allowNumeric: true,
        allowUpper: true,
        allowLower: true,
        allowCaseless: true,
        allowLatin: true,
        allowOtherCharSets: true,
        forceUpper: false,
        forceLower: false,
        maxLength: NaN
    });
    //max-length
    $('input[type="text"]:not("#Subtitle_text"),input[type="number"]').attr('maxlength', '100');
    $('textarea').attr('maxlength', '500');
    if ($('#formCode_Dialog').length) {
        $('#formCode_Dialog').attr('maxlength', '18');
    }
    if ($('#formCode_Dialog').length) {
        $('#formCode_Dialog').attr('maxlength', '18');
    }
}

function initAlphaNumEntity() {
    // ignore special chars
    $('.alphanumInputEntity').alphanum({
        allow: '._',
        disallow: '',
        allowSpace: false,
        allowNumeric: true,
        allowUpper: true,
        allowLower: true,
        allowCaseless: true,
        allowLatin: true,
        allowOtherCharSets: true,
        forceUpper: false,
        forceLower: false,
        maxLength: NaN
    });
    //max-length
 
    $('input[type="text"]:not("#Subtitle_text"),input[type="number"]').attr('maxlength', '100');
    $('textarea').attr('maxlength', '3999');
    if ($('#entity_code').length) { // from builder new entity (element can be max 28 because we need to save 2 chars to element prop when we insert fg_formlastsavevalue_inf using pivot sql on the pivot table inf in FG_ADAMA_TASK.FG_SET_INF_MISSING_ROW_DATA DB Procedure)
        $('#entity_code').attr('maxlength', '28');
    }
}

function initAlphaNumForm() 
{
	// ignore special chars
    $('.alphanumInputForm:not([type="Number"],.editableSmartCell)').alphanum({//fixed bug 7573 - Happens when "initAlphaNumForm" function overruled the "initAlphaNumEditable" function  
    	allow:returnDefaultAllows(),
        disallow: '@',
        allowSpace: false,
        allowNumeric: true,
        allowUpper: true,
        allowLower: true,
        allowCaseless: true,
        allowLatin: true,
        allowOtherCharSets: true,
        forceUpper: false,
        forceLower: false,
        maxLength: NaN
    });
}


function initAlphaNumEditable(domId,allowChar) 
{
	// ignore special chars
    $('#'+domId).find('.alphanumInputForm:not([type="Number"] )').alphanum({
    	allow:returnDefaultAllows(allowChar),
        disallow: '@',
        allowSpace: false,
        allowNumeric: true,
        allowUpper: true,
        allowLower: true,
        allowCaseless: true,
        allowLatin: true,
        allowOtherCharSets: true,
        forceUpper: false,
        forceLower: false,
        maxLength: NaN
    });
}

function returnDefaultAllows(allowChar)
{
	if (allowChar!= undefined){
		return '._ ()[]-=/:;~!@#$%^&*+'+allowChar;
	}
	return '._ ()[]-=/:;~!@#$%^&*+';
}

function updateAllows(legals)
{
	var allows = returnDefaultAllows();
	//var allows = '.';
	//legals = "34,39";
	var illegalsObj = {34:'"',39:'\'',92:'\\',44:',',124:'|',123:'{',125:'}',63:'?',60:'<',62:'>'}; //not allow: ' " ` \ , {} | ? <>
	var larr = legals.split(',');
	for(var i=0;i<larr.length;i++)
	{
		if(illegalsObj.hasOwnProperty(larr[i]))
		{
			allows += illegalsObj[larr[i]];
		}
	}
	console.log("string of allows: " + allows);
	return allows;
}

/* 
 * inStr - string to be evaluated. Should be sent trimmed already. 
 * focusOn - The id of the element that should get focus after alert-dialog is close.
 * frameId - If focusOn field is within an iFrame and called from the parents send frame elementId by this parameter
 * legals - A string of concatenated id of chars(see func createIllegalCharString()), which are an exception from the usual list of illegal chars.
 * note: @legals is considered ONLY if @illegals is null */ 
function fnValidateString (obj) /* inStr, fieldName, focusOn, frameId */
{	
	// set defaults if no values sent
	var _inFieldName = (obj.fieldName == null) ? '' : obj.fieldName,
		_hasFocus = (obj.focusOn == null) ? '' : obj.focusOn,
		_frameId = (obj.frameId == null) ? '' : obj.frameId,
		_inStr = obj.inStr,
		_inLegals = (obj.inLegals == null) ? '' : obj.inLegals;
	
	var _illegalsChars = createIllegalCharString(_inLegals);
	var alertMsg = '';
	if ( !validateLegalString(_inStr, _illegalsChars) )
	{		
        alertMsg = (_inFieldName + " contains illegal characters" + 
        			"<br/>Please do not use " + displayStringOfIllegalCharsWithSpace(_illegalsChars));
		displayAlertDialog(alertMsg, { title:"Invalid Value", focusOn:_hasFocus, frameId:_frameId });
		return false;
	}
	
    return true;
}

function validateLegalString (inStr, illegalsChars)
{
	var illegalsCharsArr = illegalsChars.split('');
	if (inStr == null)
	{
		return true;
	}
		
	for (var i in illegalsCharsArr)
	{
		if (inStr.indexOf(illegalsCharsArr[i]) > -1)
			return false;
	}
	return true;	
}

function createIllegalCharString (inLegals)
{
	var defIllegalsStr = "";	
	var defIllegalsObj = {backslash:'\\', lt:'<', quotmarks:'"', apostrophe:'\''}; 
	var legalsArr = inLegals.split(',');
	for(illegal_key in defIllegalsObj)
	{		
		if($.inArray(illegal_key, legalsArr) == -1) 
		{
			defIllegalsStr += defIllegalsObj[illegal_key];
		}
	}	
	return defIllegalsStr;
}

/*
function separates string with spaces for comfort display
*/
  function displayStringOfIllegalCharsWithSpace(illegals)
  {        
      var strWithSpace = " ";
     
      for (var i = 0; i < illegals.length; i++)
      {
              strWithSpace += illegals.charAt(i) + " ";                    
      }
      return strWithSpace;
  }


function hideEditCatalogFields() {
    // hide options of catalog edit for none admin users
    if ($('#canEditCatalog').val() != "yes") {
        $('li a:contains("Catalog")').parent().css('display', 'none');
        $('.useAsTemplateDiv').css('display', 'none');
        $('#createLike').css('display', 'none');
    }
}

function canEditCatalog() {
    // check if user cant edit
    if ($('#canEditCatalog').val() == "yes") {
        return true;
    }
    return false;
}

/* beta: create and attach div to be used with $.dialog() as alert */
var alertDialog;
$(function () {
    var bodyElem = $('body')[0]; //document.getElementsByTagName('body')[0];
    if (bodyElem == null)
        return;

    var eDiv = document.createElement('div');
    $(eDiv).attr('id', 'divAlertDialog')
        .attr('title', 'Alert');

    var eInnerDiv = document.createElement('div');
    $(eInnerDiv).attr('id', 'divAlertText').css('padding', '7px')

    eDiv.appendChild(eInnerDiv);
    bodyElem.appendChild(eDiv);

    // set dialog properties
    $('#divAlertDialog').dialog({
        autoOpen: false,
        show: 0,
        modal: true,
        close: function (e, ui) {
            var focusId = $(this).data('hasFocus');
            var iFrame = $(this).data('focusInFrame');

            if (focusId) {
                if (iFrame && iFrame != '') {
                    $('#' + iFrame).contents().find('#' + focusId).focus();
                } else {
                    $('#' + focusId).focus();
                }
            }
            // clear data so next alert won't focus on the wrong element
            $(this).data('hasFocus', '');
            $(this).data('focusInFrame', '');
        }
    });

    $('#divAlertDialog').parent().on('keydown', 'button', function (e) {
        if (e.which == 13) {
            $('#divAlertDialog').dialog('close');
            return false;
        }
    });

    alertDialog = $('#divAlertDialog');
});

function displayAlertDialog(message, title, obj) {
	
	//UNITTEST - block unittestuser
	try {
		if($('#userId').length > 0 && $('#userId').val() == 100) { // 100 is the unittestuser formid
			alert(getSpringMessage(message));
		} 
    } catch(e) { 
    }
    
    // alert message skyline style
    var _title = null,
        _obj = null,
        _message = "", // kd 18052016 added multilingual
        dict = {}, // kd 06072016 added for using with variables in message lables
        dialog_buttons = {}; 
    if (arguments.length === 3) {
        _title = title;
        _obj = obj;
    } else if (arguments.length === 2) {
        if (typeof arguments[1] == 'object') {
            _obj = arguments[1];
        } else {
            _title = arguments[1];
        }
    }
    var elem = $('div[id="divAlertDialog"]').parent();
    elem.css('cssText', elem.attr('style') + 'z-index: 350 !important'); 
    if (_obj != null) {
        $('#divAlertDialog').data('hasFocus', (_obj.focusOn != null ? _obj.focusOn : ''));
        $('#divAlertDialog').data('focusInFrame', (_obj.frameId != null ? _obj.frameId : ''));
        if (_title == null && _obj.title != null) {
            _title = _obj.title;
        }
        if(_obj.button != null){
        	var button_name = _obj.button;
        	dialog_buttons[button_name] = function(){ $(this).dialog('close'); }  
        }
    }
     
    try {
    	$('#divAlertDialog div').html(getSpringMessage(message));
    } catch(e) {
    	$('#divAlertDialog div').html(message);
    }
    
    
    $('#divAlertDialog').dialog('option', "buttons",dialog_buttons, 'title', (_title != null) ? _title : 'Alert').dialog('open');
}

/**
 * function check if alert dialog is open
 * @returns
 */
function isDisplayAlertDialogOpen()
{
	return (alertDialog)?alertDialog.dialog('isOpen'):$('#divAlertDialog').dialog('isOpen');
}

/**
 * function check if alert dialog is visible
 * useful in case when isDisplayAlertDialogOpen() returns true even though alert dialog is hidden
 * @returns
 */
function isDisplayAlertDialogVisible()
{
	return $("#divAlertDialog").is(":visible");
}

/**
 * 
 * @param obj dialog object
 * @param isAlertOnly id the dialog should behave as alert only and not a confirmation message than the value should be true
 * @returns
 */
function openConfirmDialog(obj,isAlertOnly) {
    var oDialog = $('#divConfirmDialog');
    // check if dialog was instantiated
    if (oDialog.length == 0)
        return;
   
    oDialog.parent().css('cssText', oDialog.parent().attr('style') + 'z-index: 1000 !important'); 

    $('#divConfirmDialog').parent().find("button:contains('Cancel')").addClass("ignor_data_change");
	//$('#divConfirmDialog').parent().find("button:contains('Confirm')").addClass("ignor_data_change");
    $('#divConfirmDialog').parent().find("button.confirmBtn").addClass("ignor_data_change");
	$('#divConfirmDialog').parent().find("button:contains('Close')").addClass("ignor_data_change");
	
    if(isAlertOnly == true){
    	$('#divConfirmDialog').parent().find("button:contains('Cancel')").css("display","none");
    		//$('#divConfirmDialog').parent().find("button:contains('Confirm')").html("Ok");
    	$('#divConfirmDialog').parent().find("button.confirmBtn").html("Ok");
    	$('#divConfirmDialog').parent().find("button:contains('Close')").css("display","none");
    } else {
    	$('#divConfirmDialog').parent().find("button:contains('Cancel')").css("display","");
    		//$('#divConfirmDialog').parent().find("button:contains('Ok')").html("Confirm");
    	$('#divConfirmDialog').parent().find("button.confirmBtn").html("Confirm");
    	$('#divConfirmDialog').parent().find("button:contains('Close')").css("display","");
    }
    if(obj.confirmButtonHtml != undefined && obj.confirmButtonHtml!= null){
    	$('#divConfirmDialog').parent().find("button:contains('Confirm')").html(obj.confirmButtonHtml);
    }
    if(obj.cancelButtonHtml != undefined && obj.cancelButtonHtml!= null){
    	$('#divConfirmDialog').parent().find("button:contains('Cancel')").html(obj.cancelButtonHtml);
    }
    if(obj.hideCloseIcon != undefined && obj.hideCloseIcon == true){
    	$('#divConfirmDialog').parent().find("button.ui-dialog-titlebar-close").css('display','none');
    }
    var _message = (obj.message == null) ? "Are you sure?" : obj.message,
        _okHandler = (obj.onConfirm == null) ? null : obj.onConfirm,
        		
        _okHandlerParams = (obj.onConfirmParams == null) ? [] : obj.onConfirmParams,
        _cancelHandler = (obj.onCancel == null) ? null : obj.onCancel,
        _cancelHandlerParams = (obj.onCancelParams == null) ? [] : obj.onCancelParams,
        _title = (obj.title == null) ? "Please Confirm" : obj.title,
        _replaceme = (obj.replaceme == null) ? null : obj.replaceme, // kd 08082016 for using with variables in labels of message
        msg; // for use multilanguage

    // kd 08082016  
    if (typeof jsonData != "undefined") {
        msg = jsonData[_message];
        // kd 22112016
        if (_title != null) {
            tempVar = _title;
            _title = jsonData[_title];
            if (typeof (_title) == "undefined") {
                _title = tempVar;
            }
        } // kd end
    }

    if (msg != "" && msg != null) {
        if (_replaceme != null) {
            for (i = 0; i < _replaceme.length; i++) {
                msg = msg.replace("{" + _replaceme[i].key + "}", _replaceme[i].val);
            }
        }
        $('#divConfirmDialog div').html(msg);
    }
    // kd end
    else {
        // set message
        $('#divConfirmDialog div').html(_message);
    }
    
    //unittestusert confirm all
    try {
    	if( $('#userId').length > 0 && $('#userId').val() == '100') {
        	_okHandler(_okHandlerParams);
        	return;
        }
    } catch(e) {
    	
    }
    
    //26082019 fixed bug 7602
//    $('#divConfirmDialog').on('dialogclose', function(event) {
//    	_cancelHandler(_cancelHandlerParams);
//    	return;
//    });
    //adib added the if clause 21102020
    if(obj.isCloseIcondisplayed == undefined || obj.isCloseIcondisplayed == null
    		||obj.isCloseIcondisplayed == true){
	   //yp 02092019 fixed bug 7602
	    $('#divConfirmDialog').parent().find(".ui-dialog-titlebar-close").click( function() { 
	    	try {
		    	if(_cancelHandler != null) {
		    		_cancelHandler(_cancelHandlerParams);
		    	}
	    	} catch (e) {}
	    	return;
	    });
    }

    // set parameters and open dialog
    oDialog.data('confirmHandler', _okHandler)
        .data('confirmHandlerParams', _okHandlerParams)
        .data('cancelHandler', _cancelHandler)
        .data('cancelHandlerParams', _cancelHandlerParams)
        .dialog('option', 'title', _title)
        .dialog('open');
}

function initConfirmDialogDiv() {
    // confirm message skyline style init
    var bodyElem = $('body')[0]; //document.getElementsByTagName('body')[0];
    if (bodyElem == null)
        return;

    var eDiv = document.createElement('div');
    $(eDiv).attr('id', 'divConfirmDialog')
        .attr('title', 'Please Confirm');

    var eInnerDiv = document.createElement('div');
    $(eInnerDiv).attr('id', 'divConfirmMessage').css('padding', '7px');
    $(eInnerDiv).attr('id', 'divConfirmMessage').css('word-break', 'break-word');//ta 19102020 fixed bug 8568

    eDiv.appendChild(eInnerDiv);
    bodyElem.appendChild(eDiv);

    // set dialog properties
    $('#divConfirmDialog').dialog({
        autoOpen: false,
        show: 0,
        width:'390px',
        modal: true,
        buttons: [{
            text: 'Confirm',
            click: function () {
	                var _handler = $(this).data('confirmHandler');
	
	                $(this).dialog("close");
	
	                if (_handler != null && typeof (_handler) == 'function') {
	                    // _handler.apply(null, $(this).data('confirmHandlerParams'));
	                    var paramsArray = [];
	                    paramsArray.push($(this).data('confirmHandlerParams'));
	                    _handler.apply(null, paramsArray);
	                }
	            },
            class: 'confirmBtn'
	        },
	        
            {
            text: 'Cancel',
            click: function () {
	                var _handler = $(this).data('cancelHandler');
	
	                $(this).dialog("close");
	
	                //                if (_handler != null && typeof(_handler) == 'function') {
	                //                    _handler.apply(null, $(this).data('cancelHandlerParams'));
	                //                }
	                if (_handler != null && typeof (_handler) == 'function') {
	                    var paramsArray = [];
	                    paramsArray.push($(this).data('cancelHandlerParams'));
	                    _handler.apply(null, paramsArray);
	                }
	                
	            },
	            class: 'cancelBtn'
	        }]
    });

    return $('#divConfirmDialog');
}

function elementRollBack(domId) {
    //	$('#' + domId).val($('#' + domId).attr('lastvalue'));
    //	if($('#' + domId).prop('type') == 'select-one')
    //		$('#' + domId).trigger('chosen:updated');	
    var element = $('[id="' + domId + '"]').attr('element');
    setValue_(element, $('#' + domId));

}

function initDatatableHeader(tableID) {

    var table = $('#' + tableID).DataTable();

    table.columns().iterator('column', function (ctx, idx) {
        $(table.column(idx).header()).find('div').append('<span class="ui-icon ui-icon-carat-2-n-s"/>');
        $(table.column(idx).header()).click(function () {
            changeClassDatatableHeader(this, tableID);
        });
    });

}

function changeClassDatatableHeader(th, tableID) {
    var table = $('#' + tableID).DataTable();
    table.columns().iterator('column', function (ctx, idx) {
        if (!$(table.column(idx).header()).find('div span').is($(th).find('div span')))
            $(table.column(idx).header()).find('div span').removeClass('ui-icon-triangle-1-s').removeClass('ui-icon-triangle-1-n').addClass('ui-icon-carat-2-n-s');
    });

    if ($(th).find('div span').hasClass('ui-icon-carat-2-n-s')) {
        $(th).find('div span').removeClass('ui-icon-carat-2-n-s');
        $(th).find('div span').addClass('ui-icon-triangle-1-s');
    } else if ($(th).find('div span').hasClass('ui-icon-triangle-1-s')) {
        $(th).find('div span').removeClass('ui-icon-triangle-1-s');
        $(th).find('div span').addClass('ui-icon-triangle-1-n');
    } else if ($(th).find('div span').hasClass('ui-icon-triangle-1-n')) {
        $(th).find('div span').removeClass('ui-icon-triangle-1-n');
        $(th).find('div span').addClass('ui-icon-triangle-1-s');
    }


}

function getValidJspName(formCode, entity_code) {
    var str = "page_" + formCode + "_" + entity_code;
    return str.replace(/ /g, "_nbsp_").replace(/\t/g, "_nbsp_").replace(/\./g, "_d_");
}

function fixDatatablePageDisabledInit() { // for demoFormBuilderMain datatable init

    $('#table').closest('[id*="_wrapper"]').find('a:not([onclick*="openAttachment"],[onclick*="smartLink"],[onclick*="smartFile"])').addClass('fg-button ui-button ui-state-default');
    $('#table').closest('[id*="_wrapper"]').find('a.first').addClass('ui-corner-tl ui-corner-bl');
    $('#table').closest('[id*="_wrapper"]').find('a.last').addClass('ui-corner-tr ui-corner-br');

    var t = $('#table').DataTable();
    info = t.page.info();
    page = info.page + 1;

    $('#table').closest('[id*="_wrapper"]').find('a').removeClass("ui-state-disabled");
    $('#table').closest('[id*="_wrapper"]').find('a').filter(function () {
        return $(this).text() == page;
    }).addClass('ui-state-disabled');


    if ((page == 1) || (page == 0)) {
        $('#table').closest('[id*="_wrapper"]').find('a').removeClass("ui-state-disabled");
        $('#table').closest('[id*="_wrapper"]').find('a').filter(function () {
            return $(this).text() == "Previous" || $(this).text() == "First" || $(this).text() == "1";
        }).addClass('ui-state-disabled');
    }

    if ((info.pages == info.page) || (info.pages == info.page + 1)) {
        $('#table').closest('[id*="_wrapper"]').find('a').filter(function () {
            return $(this).text() == "Last" || $(this).text() == "Next"
        }).addClass('ui-state-disabled');
    }

}



function getFormsId(SelectName) {
    // get all exists forms types
    $.ajax({
        type: 'POST',
        data: '{"action" : "getFormsId","' + 'data":[{"code":"' + $('#formCode_text').val() + '","val":"' + $('[name="formType"]').val() + '"}],' + '"errorMsg":""}',
        url: "./getFormsId.request",
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) {
            if (obj.errorMsg != null && obj.errorMsg != '') {
                displayAlertDialog(obj.errorMsg);
            } else {
                fillSelectOptions(SelectName, obj.data[0].val.split(","));
                $('#' + SelectName + ' option:first').text("New");
            }
        },
        error: handleAjaxError
    });
}

/**
 * This function is invoked when clicking the Add Table button on the Project 
 * Management page
 */ 
function showTableDiv(button) 
{
//    if ((divCount >= 9) || $('#show_' + (divCount + 1)).is(':empty'))
//        return;
//    divCount++;
//    $('#show_' + divCount).closest('[id*="show"]').show("slow");
//    var domId = $('#show_' + divCount).find('table').attr('id');
//    $('#' + domId + '_structCatalogItem').val('Choose');
//    if($('#' + domId + '_showDiv').val()=='0'){
//    	$('#' + domId + '_showDiv').val('');
//    }
//    clearDataTableWhenEmpty(domId);   // remove DivCount and correct header  ->
	var breakFlag = false;
	var domId = "";
	
	$('[id^="show_"]').each(function() {
	   if(!$(this).is(':visible') && !breakFlag) {
		   breakFlag = true;
		   $(this).show("slow");
		   domId = $(this).find('table').attr('id');
	   }
	});
	
	if(domId != "") {
		$('#' + domId + '_showDiv').val('');
		$('#' + domId + '_Caption').css('display','none');
		clearDataTableWhenEmpty(domId);
	}
}

/**
 * This function is invoked when clicking the Remove Last Table button on the Project 
 * Management page
 */ 
function hideLastTableDiv(button) {
//    if (divCount <= 0)
//        return;
//    $('#show_' + divCount).closest('[id*="show"]').hide("fast");
//    //var domId = $('#show_' + divCount).find('table').attr('id');
//    //$('#' + domId + '_showDiv').val('0');
//    
//    //kd 21122020 fixed bug-8707. Workaround, call onChange trigger for Level ddl when removing the last table
//    var domId = $('#show_' + divCount).find('table').attr('id');
//    $("#"+ domId + "_structCatalogItem").val("Project"); //Choose -not working
//    $("#"+ domId + "_structCatalogItem").change();       
//    divCount--;    // remove DivCount and correct header (and use choose instead ot project it work if _showDiv set to 0)  ->
	var breakFlag = false;
	var domId = "";
	var arrDiv = [];
	$('[id^="show_"]').each(function() {
		if($(this).is(':visible') && !breakFlag) {
			arrDiv.push($(this));
		}
	});
	
	if(arrDiv.length > 1) {
		var $div = arrDiv.pop();
		domId = $div.find('table').attr('id');
		if(domId == 'firstTable') {
			alert("The first table can not be removed"); // we not suppose to see this alert if arrDiv.length > 1
		}
		
		$div.hide("fast");
		$('#' + domId + '_showDiv').val('0');
		$('#' + domId + '_structCatalogItem').val('Choose');
		$('#' + domId + '_criteriaCatalogItem').empty().append('<option value="ALL">ALL</option>');
		$('#' + domId + '_LinkToLastSelection').prop('checked', true);
//		$('#' + domId + '_Caption').html('Choose');
//		$('#' + domId + '_Caption').css('display','block');
		clearDataTableWhenEmpty(domId);
	}
}

function initProp_() {
    var dataArray = ("DD/MMM/YYYY;DD/MM/YYYY;dd/M/yy").split(';');
	prop.dateFormat = {};
	prop.dateFormat.userDateFormatClient = dataArray[0];
	prop.dateFormat.savedConventionDbDateFormat = dataArray[1];
	prop.dateFormat.datepickerFormat = dataArray[2];
}


// Data picker code (from skyline)
function initDatePickerWithOptionsByClass(cssClass, opt) {
//    $.ajax({
//        type: 'POST',
//        data: '{"action" : "initDateFormatter","data":[],"errorMsg":""}',
//        url: "./initDateFormatter.request",
//        contentType: 'application/json',
//        dataType: 'json',
//        success: function (obj) {
//            if (obj.errorMsg != null && obj.errorMsg != '') {
//                displayAlertDialog(obj.errorMsg);
//            } else if (obj.data[0] == null) {
//                obj.userDateFormatClient = "DD/MM/YYYY";
//            } else {
				// because of performance issues - avoid this call to the server and make it hard coded:
				initProp_()
                
                //var _min  =($("." + cssClass + ":not(.elementrange)").attr('min') != null && $("." + cssClass + ":not(.elementrange)").attr('min') !="") ? moment($("." + cssClass + ":not(.elementrange)").attr('min'), 'DD/MM/YYYY').format('dd-mm-yy') : null;
                //var _max  =($("." + cssClass + ":not(.elementrange)").attr('max') != null && $("." + cssClass + ":not(.elementrange)").attr('max') !="") ? moment($("." + cssClass + ":not(.elementrange)").attr('max'), 'DD/MM/YYYY').format('dd-mm-yy') : null;
                var _showOn = "button";
                var _showBP = true; // show button panel
                var _isShowBP = (opt != null && opt.showButtonPanel != null) ? opt.showButtonPanel : _showBP;
                var _dateFormat = prop.dateFormat.datepickerFormat; // =
                // ";dd/mm/yy"
                var _disabled = (opt != null && opt.disabled != null) ? opt.disabled : false;
                var _defaultDate = (opt != null && opt.defaultDate != null) ? opt.defaultDate : null;
                var _handler = (opt != null && opt.onSelect != null) ? opt.onSelect : onSelectDateDefault;
                var _handlerBeforeShow = (opt != null && opt.beforeShow != null) ? opt.beforeShow : null;
                var _isRemoveClear = (opt != null && opt.removeClear != null) ? opt.removeClear : false;
               
                $("." + cssClass + ":not(.elementrange)").datepicker({
                    showOn: (opt != null && opt.showOn != null) ? opt.showOn : _showOn,
                    showButtonPanel: _isShowBP,
                    dateFormat: (opt != null && opt.dateFormat != null) ? opt.dateFormat : _dateFormat,
                    buttonImage: "../skylineFormWebapp/images/calendar.png",
                    buttonImageOnly: true,
                    buttonText:"",
                    changeMonth: true,
                    changeYear: true,
                    disabled: _disabled,
                    defaultDate: _defaultDate,
                    altFormat: ['dd-mm-yy', 'ddmmyy'], // ef task 7078
                    constrainInput: false, // ef task 7078
                    beforeShow: function (input) {
	                    var min=($(input).attr('min') != null && $(input).attr('min') !="") ? moment($(input).attr('min'),prop.dateFormat.userDateFormatClient, true) : null;//'DD-MMM-YYYY'
	                    var max=($(input).attr('max') != null && $(input).attr('max') !="") ? moment($(input).attr('max'),prop.dateFormat.userDateFormatClient, true) : null;
	                   
	                    var maxDate = new Date(max);
	                    $(input).datepicker('option','maxDate',max!=null?maxDate:null);
	                    
	                    var minDate = new Date(min);
	                   $(input).datepicker('option','minDate',min!=null?minDate:null);
                        if (_isShowBP) {
                            var objId = $(input).attr('id');
                            overrideDefaultButtonPanel(input, objId, _handler, _isRemoveClear);
                        }

                        if (_handlerBeforeShow != null) {
                            return _handlerBeforeShow(input,event);
                        }
                    },
                    onSelect: _handler,
                    onChangeMonthYear: function (year, month, inst) {
                        if (_isShowBP) {
                            var objId = $(this).attr('id');
                            overrideDefaultButtonPanel(inst.input, objId, _handler, _isRemoveClear);
                        }
                    },
                    onClose: function(dateText,datePickerInstance) 
        	        {
        	            var $this = $(this);
                    	if($this.hasClass('editableSmartCell'))
                    	{
	                    	var oldValue = $this.data('oldvalue') || "";
	        	            if (dateText !== oldValue) {
	        	            	$this.data('oldvalue',dateText);
	        	            	$this.trigger('onchange');
	        	            }
                    	}
        	        }
                }).on('change', function () {
                    validDateWithMomentJS(this);
                });
                $('.hasDatepicker').prop('readonly', false);
                fixDatePicker(); // fix date picker
                $.fn.dataTable.moment(prop.dateFormat.userDateFormatClient); // data table sort by date
//            }
//        },
//        error: handleAjaxError
//    });
}

function overrideDefaultButtonPanel(input, objId, handler, isRemoveClear) //input - HTMLInputElement Object
{
    var _handler = (handler == null) ? onSelectDateDefault : handler;

    setTimeout(function () {
        var btnWidget = $(input).datepicker("widget");
        /* override 'Today' button */
        btnWidget.find('.ui-datepicker-current').removeClass('ui-priority-secondary').addClass('ui-priority-primary')
            .text('Today')
            .click(function () {
                $(input).datepicker('setDate', new Date()).datepicker('hide');
                $('.ui-datepicker-current-day').click();
            });

        if (isRemoveClear) {
            btnWidget.find('.ui-datepicker-close').remove();
        } else {
            /* override 'Done' button */
            btnWidget.find('.ui-datepicker-close')
                .text('Clear')
                .click(function () {
                    $.datepicker._clearDate(input);
                    $(input).datepicker("hide");
                    //$(input).datepicker('option', 'onSelect');			
                    $('#' + objId).val("00/00/0000");
                });
        }
    }, 1);
}

function validDateWithMomentJS(date) {
	// ef task 7078
    var dateFormat = 0;
    var firstFormat = moment(date.value, prop.dateFormat.userDateFormatClient, true);
    var secondFormat = moment(moment(date.value, prop.dateFormat.userDateFormatClient, true).format(prop.dateFormat.userDateFormatClient), prop.dateFormat.userDateFormatClient, true);
    
    if (firstFormat.isValid()) {
        dateFormat = firstFormat;
    } else if (secondFormat.isValid()) {
        dateFormat = secondFormat;
    }
    
    if ((dateFormat == 0) || (dateFormat.format('YYYY') == "0000")) {
        displayAlertDialog(getSpringMessage('invalidDate'));
        $(date).val('00/00/0000');
        return 0;
    }

    if (parseInt(dateFormat.format('YYYY') / 1000) == 0) {
        if (parseInt(dateFormat.format('YYYY') / 100) != 0) {
            displayAlertDialog(getSpringMessage('invalidDate'));
            $(date).val('00/00/0000');
            return 0;
        }
        dateFormat.add(2000, 'years')
        $(date).val(dateFormat.format(prop.dateFormat.userDateFormatClient));
    }

    return 1;
}

function onSelectDateDefault() 
{
	try 
	{
		var $this = $(this);
		console.log("onSelectDateDefault", $this);
		if(!$this.hasClass('ignor_data_change'))
		{
			if($('#tempalteTabs').length > 0) {
				$('#tempalteTabs').trigger('change');	
			} else if($('#tempalteNoTabs').length > 0) {
				$('#tempalteNoTabs').trigger('change');
			}
		}
	} 
	catch (e) {
		console.log("onSelectDateDefault", e);
		$('#tempalteTabs').trigger('change');
		return;
	}
	return;
}

/**
 * Email validation
 * @param input
 * @returns
 */
function isEmail(input) {
    var email = $(input).val();
    var domId = $(input).attr('id');
    var regex = /^([a-zA-Z0-9_.+-])+\@(([a-zA-Z0-9-])+\.)+([a-zA-Z0-9]{2,4})+$/;
    if (regex.test(email) || email == "") {
        $(input).css('border-color', '');
        $('#' + domId + '_Message').css('display', 'none');

    } else {
        $(input).css('border-color', '#a94442');
        $('#' + domId + '_Message').css('display', 'block');
    }
}

/**
 * Generic input type Time validation
 * @param input
 * @returns
 */
function isTime(input) {
    var timeVal = $(input).val();
    var domId = $(input).attr('id');
    var regex = /^([01][0-9]|2[0-3]):?[0-5][0-9]$/;
    if (regex.test(timeVal) || timeVal == "") {
        if ((timeVal.length == 4) && (!isNaN(timeVal))) {
            $(input).val(timeVal.slice(0, 2) + ":" + timeVal.slice(2))
        }
        $(input).css('border-color', '');
        $('#' + domId + '_Message').css('display', 'none');

    } else {
        $(input).css('border-color', '#a94442');
        $('#' + domId + '_Message').css('display', 'inline-block');
    }
}

/**
 * @returns true if the browser is IE (without Edge)
 */
function detectIE() {
    var ua = window.navigator.userAgent;

    var msie = ua.indexOf('MSIE ');
    if (msie > 0) {
        // IE 10 or older => return version number
        return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
    }

    var trident = ua.indexOf('Trident/');
    if (trident > 0) {
        // IE 11 => return version number
        var rv = ua.indexOf('rv:');
        return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
    }
    return false;
}

/**
 * @returns true if the browser is IE (include Edge)
 */
function detectIEFull() {
    var ua = window.navigator.userAgent;

    var msie = ua.indexOf('MSIE ');
    if (msie > 0) {
        // IE 10 or older => return version number
        return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
    }

    var trident = ua.indexOf('Trident/');
    if (trident > 0) {
        // IE 11 => return version number
        var rv = ua.indexOf('rv:');
        return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
    }

    var edge = ua.indexOf('Edge/');
    if (edge > 0) {
        // Edge (IE 12+) => return version number
        return parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10);
    }

    // other browser
    return false;
}

/**
 * Disable\Enable Date elements 
 * @returns
 */
function fixDatePicker() {
    $('.date-picker.disabledclass').datepicker('disable');
    $('.date-picker:not(.disabledclass)').datepicker('enable');
}

/**
 * Add asterisk for the inputs' labels which are mandatory
 * @returns
 */
function initMandatory() {
    $('input[formelement="1"],textarea[formelement="1"],select[formelement="1"],div[formelement="1"][class*="ckeditor"]').each(function (index) {
        var domId = $(this).attr('id');
        if (typeof domId !== 'undefined') {
            if ($(this).attr('required')) {
                if ($('label[for="' + domId + '"]').length > 0) {
                    $('label[for="' + domId + '"]').css('visibility', 'visible');
                }
            } else {
                if ($('label[for="' + domId + '"]').length > 0) {
                    $('label[for="' + domId + '"]').css('visibility', 'hidden');
                }
            }
        }
    });
}


/**
 * return an indication whether the current form is editable
 * @returns
 */
function isGeneralDisabledStateForLateRender() {
	if( $('#isNew').val() == '0' && 
		($('#generalDisabledFlagParam').length > 0 && $('#generalDisabledFlagParam').val() == '1') 
	  ) {
	  return true; 
	}
	return false ;
}


/**
 * The function detect change for all of the elements in the page and raise prop.dataChanged flag (global variable).
 * Close and New buttons use this flag to check whether element is changed.
 * The function will work with jspTemplateDynamicTabs only.
 * If the function is needed in other templates, the '#tempalteTabs' selector should be changed 
 * to the elements common parent (root). In a complicated case the selector can be ':input' (all elements).
 * @returns
 */
function initDataChanged() 
{
	$('#tempalteTabs').change(function(e) {
		try {
			if($(e.target).hasClass('dataTableApiSelectInfo') || $(e.target).hasClass('dataTableApiSelectAllNone') || $(e.target).hasClass('ignor_data_change')) {
				return;
			}
		} catch(e){
			
		}
		console.log("tempalteTabs prop.dataChanged = true");
		prop.dataChanged = true;
        $('#tempalteTabs').off('change');
        $('button,.ui-datepicker-trigger').off('click.dataChanged');
    });
    $('#popupTemplate').change(function(){
    	console.log("popupTemplate prop.dataChanged = true");
    	prop.dataChanged = true;
    	$('#popupTemplate').off('change');
        $('button,.ui-datepicker-trigger').off('click.dataChanged');
    });
    $('button:not(:disabled,#newButton,#newFloatingButton,#close_back,#close_backFloatingButton,#editButton,.dataTableApiTools,.dataTableApiSplit,.fullScreenOpenBtn,.ui-button-icon-only,.ignor_data_change)').on('click.dataChanged', function () {
    	try {
			if($(this).hasClass('ignor_data_change')) {
				return;
			}
			if($(this).hasClass("dataTableApiEdit")||$(this).hasClass("dataTableApiEditShared")){
				var generalDisabledFlagParam_ = $('#generalDisabledFlagParam').val();
				if(generalDisabledFlagParam_ == 1 || $(this).text()=="View"){
					return;
				}
			}
		} catch(e){
			
		}
    	console.log("button prop.dataChanged = true");
    	prop.dataChanged = true;
        $('#tempalteTabs').off('change');
        $('button,.ui-datepicker-trigger').off('click.dataChanged');
    });
}

function initDataChangedNoTabs() 
{
	$('#tempalteNoTabs').change(function(e) {
		try {
			if($(e.target).hasClass('dataTableApiSelectInfo') || $(e.target).hasClass('dataTableApiSelectAllNone') || $(e.target).hasClass('ignor_data_change')) {
				return;
			}
		} catch(e){
			
		}
		console.log("tempalteNoTabs prop.dataChanged = true");
		prop.dataChanged = true;
        $('#tempalteNoTabs').off('change');
        $('button,.ui-datepicker-trigger').off('click.dataChanged');
    });
    $('#popupTemplate').change(function(){
    	console.log("popupTemplate prop.dataChanged = true");
    	prop.dataChanged = true;
    	$('#popupTemplate').off('change');
        $('button,.ui-datepicker-trigger').off('click.dataChanged');
    });
    $('button:not(:disabled,#saveAsButton,#newButton,#newFloatingButton,#close_back,#close_backFloatingButton,#editButton,.dataTableApiTools,.dataTableApiSplit,.fullScreenOpenBtn,.ui-button-icon-only,.ignor_data_change)').on('click.dataChanged', function () {
    	try {
			if($(this).hasClass('ignor_data_change')) {
				return;
			}
			if($(this).hasClass("dataTableApiEdit")||$(this).hasClass("dataTableApiEditShared")){
				var generalDisabledFlagParam_ = $('#generalDisabledFlagParam').val();
				if(generalDisabledFlagParam_ == 1 || $(this).text()=="View"){
					return;
				}
			}
		} catch(e){
			
		}
    	console.log("button prop.dataChanged = true");
    	prop.dataChanged = true;
        $('#tempalteNoTabs').off('change');
        $('button,.ui-datepicker-trigger').off('click.dataChanged');
    });
}

/**
 * Init of the WF dialog which Can be opened by clicking the 'New Button' of the page
 * @returns
 */
function initWFDialog() {
    $('body').append(    	
    	'<div id="wfDialog">\n' +
    	   '<div id="selectWfFormCode" name="selectWfFormCode" class="selectionListLink" style="width: 100%;position: relative;float: left;"></div>\n' +
    	   '<div style="position: relative;float: left;margin-top: 50px;margin-bottom: 10px;">\n'+
				'<i  onclick="customInfoClickEvent(\'getWFStatusInfo\',\'STEPS_WF_LIST_INFO\')" style="cursor: pointer;margin-right: 10px;" title="WF Info" class="fa fa-info"><span style="color: gray;padding-left: 5px;">Additional Info</span></i>\n'+
			'</div>\n'+ 
        '</div>\n');
    var dialog = $("#wfDialog").dialog({
        autoOpen: false,
        title: 'Create New',
        width: 250,
        modal: true,
        close: function () {
            dialog.dialog("close");
        }
    });
}

/**
 * Open the WF dialog which Can be opened by clicking the 'New Button' of the page.
 * Please notice that 'formCode' and 'formId' elements must be available in the DOM !!
 * 
 *  ***  Important: 
 *       The code **.replace("InvItem","")** which exists 
 *       in the success callback is workaround for Adama. *  
 *  ***
 *  
 * @param okHandlerAndParamsArray. **The first cell of the array is the function, the rest of the array is the params**
 * @returns
 */
function openWFDialog(okHandlerAndParamsArray) 
{
	var _okHandler, _okHandlerParams, i, optionsArray, optionsArrayLength, optionsHtml = "";
    if ((typeof okHandlerAndParamsArray !== 'undefined') && (okHandlerAndParamsArray != null) && (okHandlerAndParamsArray.length > 0)) {
        _okHandler = okHandlerAndParamsArray[0];
        _okHandlerParams = (okHandlerAndParamsArray.length > 1) ? okHandlerAndParamsArray.shift() : undefined;
        $('#selectWfFormCode').html('');
        
        $.ajax({
            type: 'POST',
            data: '{"action" : "getNewAvailableFormList","' + 'data":[' + '{"code":"formCode","val":"' + formCode.value + '"},' + '{"code":"formId","val":"' + formId.value + '"},' + '{"code":"stateKey","val":"' + stateKey.value + '"}' + '],' + '"errorMsg":""}', //TODO key check
            url: "./getNewAvailableFormList.request",
            contentType: 'application/json',
            dataType: 'json',
            success: function (obj) {
                if (obj.errorMsg != null && obj.errorMsg != '') {
                    displayAlertDialog(obj.errorMsg);
                } else if ((obj.data[0].val == "-1") /*|| (obj.data[0].val == "")*/) {

                } else {
	                if(obj.data[0].val != ""){
	                    optionsArray = obj.data[0].val.split(',');
	                    optionsArrayLength = optionsArray.length;                    
	                    for (i = 0; i < optionsArrayLength; i++) {
	                        
	                    	var optVal = getNewFormCodeDisplay(optionsArray[i]); 
	                    	//optionsHtml += "<div class=\""+getEntityIconByFormCode(optVal)+"\"><span onclick=\"isValidation('"+optionsArray[i]+"')\">" + optVal + "</span></div>";
	                    	optionsHtml += "<div><div>"+getEntityIconByFormCode(optVal)+"</div>"
	                    						+"<div><span onclick=\"isValidation('"+optionsArray[i]+"')\">" + optVal + "</span></div></div>";
	                    	
	                    }
	                }
	                if(optionsHtml == "")
	                {
	                	optionsHtml = "<div class=\"no-data-found\">No form creation is available. <br>For more information, click the 'Additional Info' below.</div>";
	                }
                    $('#selectWfFormCode').html(optionsHtml);
                    $("#wfDialog")
                        .data('confirmHandler', _okHandler)
                        .data('confirmHandlerParams', _okHandlerParams)
                        .dialog('open');
                }
            },
            error: handleAjaxError
        });
    }
}

function onWFContinue(value)
{
	try
	{
		var $this = $('#wfDialog');
		var _handler = $this.data('confirmHandler');
	    var _params = $this.data('confirmHandlerParams');
	    
	    $this.dialog("close");
		var paramsArray = [];
	    paramsArray.push(value);
	    
	    if (_handler != null && typeof (_handler) === 'function') {
	        if (typeof _params !== 'undefined') {
	            paramsArray.push(_params);
	        }
	        _handler.apply(null, paramsArray);
	    }
	}
	catch(e)
	{
		console.log("onWFContinue",value);
		console.log(e);
	}
	
}


function isValidation(selectedFormCode) 
{
	 
	var action = 'checkIfNewFormHasValidation';
	var stringifyToPush = {
 			code: "newFormCode",
 			val:  selectedFormCode,
 			type: "AJAX_BEAN",
 			info: 'na'
 	};

	//showWaitMessage("Please wait...");
	prop.onChangeAjaxFlag = false;
	var allData = getformDataNoCallBack(1); 
	var allData = allData.concat(stringifyToPush);
	//url call
	var urlParam =
	 "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + '&userId=' + $('#userId').val() + "&eventAction=" + action + "&isNew=" + $('#isNew').val();
	
	
	var data_ = JSON.stringify({
	 action: "doSave",
	 data: allData,
	 errorMsg: ""
	});
	
	//call...
	 $.ajax({
	        type: 'POST',
	        data:data_,
	        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
	        contentType: 'application/json',
	        dataType: 'json',
	        success: function (obj) 
	        {
	        	var cnf = obj.data[0].val;
	        	if (cnf !=null && cnf != "")
	        	{     
	        		openConfirmDialog({
	        	        onConfirm: function(){	        	        	
	        	        	onWFContinue(selectedFormCode);
	        	        },
	        	        title: 'Warning',
	        	        message: getSpringMessage(cnf.slice(2)),
	        	        onCancel: function(){
	        	        }
	        	    });     
	        	}
	        	else{
	        		onWFContinue(selectedFormCode);
	        	}
	        },
	        error: handleAjaxError
	    });
}


/**
 * return count of decimals 
 * @param value
 * @returns
 */
function countDecimals(value) {
    if (Math.floor(value) === value) return 0;
    return value.toString().split(".")[1].length || 0;
}

function defer(method) {
    if (window.jQuery) {
        method();
    } else {
        setTimeout(function() { defer(method) }, 50);
    }
}

/**
 * init page (do on docuemnt ready)
 * 
 * @returns
 */
function initPage() {
	console.log("start initPage");
    $("button").button();
    //getTitleAndSubtitleForm();   
    var startTime = new Date().getTime();
    initProp_();
    setTimeout(function() { initDatePickerWithOptionsByClass('date-picker'); }, 50);
    initConfirmDialogDiv();
    initAlphaNumForm();
    initMandatory();
    initElementInfo();
    initPageInfoOnAjaxChange();
    initAdditInfoDialog();
    if ($("#tempalteTabs").length) {
//    	var tabCookieName = $('#formCode').val();
    	 $("#tempalteTabs").tabs({
    		    active : $.cookie($('#formCode').val() + '_' + $('#formId').val()  + '_activetab'),
    		    activate : function( event, ui ){
    		        $.cookie($('#formCode').val() + '_' + $('#formId').val()  + '_activetab', ui.newTab.index(),{
    		            expires : 1
    		        });
    		    }
         }); // init tabs
    	 $('.displayOnLoad').css('display','');
    	 initWFDialog();
        $('div[title].ui-tabs-panel').attr('title', ''); // remove title from
        // the tabs
        if ($('#isNew').val() == "1") {
            $('#newButton').addClass('disabledclass');
            $('#newFloatingButton').addClass('disabledclass');
            $('#dataTableAddRowFloatingButton').addClass('disabledclass');
        }
        initDataChanged();
        //yp 20/10/2020 make it in navigationTabSelection function in authez to prevent click on unzuthrized tab
//        if ($('#formTab').val() != "") 
//        {
//        	$('a[href="#' + $('#formTab').val() + 'Tab"]').click();
//        }
        if (window.self === window.top) {
            if ($('#backUrl').val() != "") {
                $('#close_back').attr('onclick', 'confirmWithOutSave(doBack);');
            } else {
                $('#close_back').css('display', 'none');
            }
        } else {
            $('#homePageHeaderJspTempalte').css('display', 'none');
            $('#homePageHeaderJspTempalte').css('cursor', 'pointer');
            $('#backHeaderJspTempalte').css('display', 'none');
            $('#backHeaderJspTempalte').css('cursor', 'pointer');
            $('#mbmcpebul_table').closest('tr').css('display', 'none');
            $($('#mbmcpebul_table').closest('tr').siblings()[1]).css('display', 'none');
        }
    }
//   ab 22122020: code for Demo floating tabs
//    else if ($("#floatingVerticalTabs").length) {
//    	$("#floatingVerticalTabs").tabs().addClass( "ui-tabs-vertical ui-helper-clearfix" );
//		$("#floatingVerticalTabs").removeClass( "ui-corner-top" ).addClass( "ui-corner-left" );
//		$('.displayOnLoad').css('display','');
//    }
    else { 
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
	$('.popupSaveDefinitionBtn').css('display', 'inline');
    $('.popupSaveDefinitionBtn').attr('title', 'Save popup display');
    
	if($('#isStruct').val() == 0) {
		$('.mainSaveFormAndDefinitionBtn').css('display', 'inline');
		$('.mainSaveFormAndDefinitionBtn').attr('title', 'Save data and display');
		$('.mainSaveDefinitionBtn').css('display', 'none');
	} else {
		$('.mainSaveFormAndDefinitionBtn').css('display', 'none');
		$('.mainSaveFormAndDefinitionBtn').attr('title', 'Save display');
		$('.mainSaveDefinitionBtn').css('display', 'inline');
	}
	initFormSaveDisplayButtons();
	initNavigationTreeButton();
	console.log("call initForm");
    initForm();
    
    console.log( 'initPage took at: '+(new Date().getTime()-startTime)+'mS' );
}

/**
 * toggle - show/hide options (of select)
 * (jquery show()/hide() not working on IE)
 */
$.fn.toggleOption = function (show) {
    $(this).toggle(show);
    if (show) {
        if ($(this).parent('span.toggleOption').length)
            $(this).unwrap();
    } else {
        if ($(this).parent('span.toggleOption').length == 0)
            $(this).wrap('<span class="toggleOption" style="display: none;">');
    }
};

/**
 * get Spring Message
 * @param key
 * @returns
 */
function getSpringMessage(key,defaultMessage) {
	return (prop.springMessagesObj[key]) ? prop.springMessagesObj[key] : (defaultMessage == undefined ? key: defaultMessage);
}

/**
 * Remove required attribute from all elements in the page
 * @returns
 */
function removeRequiredAttribute() {
	$("[required]").removeAttr("required");
	 
}

/**
*
* @returns valid string before eval
*/
function getValidEval(value) {
   if (value.indexOf('\\n') != '-1') {
       return value.replace(/\\n/g, '').replace(/\r/g, '');
   }
   return value;
}



/**
 * 
 * @param elementName domid
 * @param istRequired true for required
 * @returns
 * Note: handle the label astrix 
 */
function setRequiredByElementId(elementName, istRequired) {
	if (istRequired) {
		$('[id="' + elementName + '"]').attr('required',true);
		  if ($('label[for="' + elementName + '"]').length > 0) {
			  $('label[for="' + elementName + '"]').css('visibility', 'visible');
          }
	 } else {       
	
			 $('[id="' + elementName + '"]').attr('required',false);
			 if ($('label[for="' + elementName + '"]').length > 0) {
	                $('label[for="' + elementName + '"]').css('visibility', 'hidden');
	         }
	}
}

function initElementInfo() {
	 
	$('[ATTR_INFO_ELEMENT_DIV="1"]').dialog({ 
		autoOpen: false, 
		height: $(window).height() - 50,
        width: $(window).width() - 50,
        overflow:"auto"
    });
	
	$('[ATTR_INFO_ELEMENT_HREF="1"]').click( function() {
		   var thelink = $(this);
		   $( '[id="' + thelink.attr('impCode') + '_infoElemnetDialog"]' ).dialog('open');
		   return false;
		});
}

function initPageInfoOnAjaxChange() {
	 
	$('#pDisplayPageInfoOnAjaxChange').html("Changes after ajax call:");
	$('[ATTR_INFO_PAGE_HREF="1"]').html("Check page info changes");
	
	$('[ATTR_INFO_PAGE_DIV="1"]').dialog({ 
		autoOpen: false, 
		height: $(window).height() - 50,
        width: $(window).width() - 50,
        overflow:"auto"
    });
	
	$('[ATTR_INFO_PAGE_HREF="1"]').click( function() {
		   $('[ATTR_INFO_PAGE_DIV="1"]').dialog('open');
		   return false;
		});
}

function collectElementsDisplayValues()
{
	//console.log('start collectElementsDisplayValues()');	
	var values = [];
	var i=0;
	$('[formElement=1],div[formLabelElement=1]:visible').each(function () 
	{
		var elementObj = $(this);	
		var domID = elementObj.attr('id');
		//console.log(domID);
		if((elementObj.hasClass('chosen-select') && $('#' + domID + '_chosen').css('visibility')  == 'hidden')
			||
			(typeof elementObj.attr('type') !== "undefined" 
					&& ((elementObj.attr('type').toUpperCase() == "TEXT") || (elementObj.attr('type').toUpperCase() == "NUMBER"))
					&& elementObj.css('display') == 'none')
			||
			(elementObj.hasClass('ckeditor') && $('[id="cke_' + domID + '"]').css('display') == 'none')
		)
		{
			return;
		}
		var _val = "";		
		if(elementObj[0].hasAttribute("element"))
		{
			var elementImpCode = elementObj.attr('element');			
			 _val = getDisplayValue_(elementImpCode, this);
		}
		else
		{
			_val = elementObj.find('label').last().html();			
		}
		//console.log(_val);
		values[i++] = {key:domID,val:_val};
	});
	//console.log(JSON.stringify(values));
	return values;
}

function getElementsDisplayValue(domId)
{ 
		var elementObj = $('#' + domId);
		var elementImpCode = elementObj.attr('element'); 
		var _val = getDisplayValue_(elementImpCode, elementObj); 
		return _val; 
}

function isAlive()
{ 
	console.log("isAlive()");
	
	var toReturn = false;
	 $.ajax
	({ 
		type: "POST",
		url: "./isSessionAlive.request",
		dataType: "json",
		async: false,
		success: function( data ) 
		{  
			toReturn = true;
		},
		error: handleAjaxError
	});	
	
	 console.log("isAlive(): " + toReturn);
	return toReturn;
}
var IS_SPREADSHEET_SAVE_DISPLAY = true;
function setSpreadsheetUserData(){
	var allData = [];
	$('[element = "ElementExcelSheetImp"]').each(function(){
		var $element = $(this);
		var elementImpCode = $element.attr('element');
		var stringifyInfo = '{"formPreventSave":"' + $element.attr("formPreventSave") +
				'", "type":"' + $element.attr("type") +
				'", "saveType":"' + $element.attr("saveType")
		'"}';
		var stringifyToPush = {
				code: $element.attr('id'),
				val: JSON.stringify(getValueFromSpreadsheet($element.attr('id'))),
				type: "AJAX_BEAN",
				info: stringifyInfo
		};
		if($element.attr("is_changed_flag") == "1"){
			allData.push(stringifyToPush);
		}
	});
	var data_ = JSON.stringify({
		action : "saveSpreadsheet",
		data : allData,
		errorMsg : ""
	});
	if(allData.length == 0){
		return;
	}
	var urlParam =
	       "?userId=" + $('#userId').val()+"&formId="+ $('#formId').val();
	 $.ajax({
	       type: 'POST',
	       data: data_,
	       url: "setSpreadsheetUserData.request" + urlParam,
	       contentType: 'application/json',
	       dataType: 'json',
	       success: function (obj) {
	           if (obj.errorMsg != null && obj.errorMsg != '' || obj.data[0].val == "-1") {
	        	   insertSpreadsheetIntoLocalStorage();
	           } else {
        		   clearLocalStorage();
	        	   if(IS_SPREADSHEET_SAVE_DISPLAY){
	        		   displayFadeMessage(getSpringMessage('The spreadsheet data saved on the temporary storage in the DB'));
	        	   }
	           }
	       },
	       error: insertSpreadsheetIntoLocalStorage
	 });
	 return;
}

/**
 * The function invoked when the spreadsheet failed to be stored in the db on the schedular.
 * insertSpreadsheetIntoLocalStorage() may be firedc also when the save process has been failed
 * @returns
 */
function insertSpreadsheetIntoLocalStorage(){
	var errMessage = "Server connection error.<br>" ;
   try{
	   var localStorageInsertCount = 0;
	   clearLocalStorage()//localStorage.clear();
 	   var time = $.now();
 	   $('[element = "ElementExcelSheetImp"]').each(function(){
				var allData = [];
				var $element = $(this);
				var domId = $element.attr('id');
				var val = JSON.stringify(getValueFromSpreadsheet(domId));
				var key = domId+"_"+$('#formId').val()+"_"+time;
				if($element.attr("is_changed_flag") == "1"){
					localStorage.setItem(key,val);
					localStorageInsertCount++;
				}
 	   });
 	   if(localStorageInsertCount > 0){//localStorage is not empty->data has been stored
 		   displayFadeMessage(errMessage + getSpringMessage('The spreadsheet data saved on the local storage successfully'));
 	   }
	}
    catch(e){
    	if( e instanceof DOMException && (//if failed because the storage space is full then clear the storage and re-try to insert the data again
                // everything except Firefox
                e.code === 22 ||
                // Firefox
                e.code === 1014 ||
                // test name field too, because code might not be present
                // everything except Firefox
                e.name === 'QuotaExceededError' ||
                // Firefox
                e.name === 'NS_ERROR_DOM_QUOTA_REACHED') &&
                // acknowledge QuotaExceededError only if there's something already stored
                (localStorage && localStorage.length !== 0)){//if the storage has even no one item at least-> then the first item we try to insert is too large and a message is displayed to the user
    				localStorage.clear();
    				insertSpreadsheetIntoLocalStorage();
    	} else {
    		displayFadeMessage(errMessage + getSpringMessage('The spreadsheet data failed to be saved on the local storage'));
    	}
    }
}


function clearLocalStorage(hours,domId,is_general_clear){
	//the domId arg is in order to allow earing the keys that refer to the accepted domId
	if(hours==undefined || hours == null){
		hours = CLEAN_STORAGE_HOURS_AGO;
	}
	var keyPart="";
	if(domId!==undefined){
		keyPart = domId+"_"+ $('#formId').val()+"_";
	}
	var hours_ms = hours*60*60*1000;//hours*60 min*60 sec *1000 millisec  
	for (var i = 0; i <= localStorage.length - 1; i++) {
		   var key = localStorage.key(i);
		   if(keyPart!="" && key.indexOf(keyPart)!=-1 || keyPart == ""){
			   var value = localStorage.getItem(key);
			   var timestamp = key.split("_")[2];
			   if((is_general_clear == undefined|| is_general_clear == false) && timestamp <= $.now()-hours_ms 
					   || is_general_clear == true){
				   localStorage.removeItem(key);
			   }   
		   }
	}
}

function checkNotificationMessage() {
	var urlParam =
	       "?userId=" + $('#userId').val();
	 $.ajax({
	       type: 'POST',
	       data: '',
	       url: "getMessageCount.request" + urlParam,
	       contentType: 'application/json',
	       dataType: 'json',
	       success: function (obj) {
	           if (obj.errorMsg != null && obj.errorMsg != '') {
	               //displayAlertDialog(obj.errorMsg);
	           } else if (obj.data[0] == null) {
	               //hideWaitMessage();
	           } else {
	        	   try{
	        		   var num = obj.data[0].val;
	       			   updateMenuItemUI(num);
	        	   } catch(e) {
	        		   //
	        	   }
	           }
	       }
	 });
	 return;
}

function validateDecimal(key, e)
{	
	var event = e ? e : window.event;
	var retVal = false;
	var value = event.srcElement.value;

	if(key == 46 && value.length == 0)
	{
		retVal = false;
	}
	else if((key >= 48 && key <=57) || (key == 46 && value.indexOf(".") == -1))
	{
		retVal = true;
	}
	if(!retVal)
	{ 
		event.preventDefault ? event.preventDefault() : (event.returnValue = false);		
	}  
}

/* validates a numeric whole field for illegal chars or format
isDecimal: if True allows for a single '.' 
isSigned: if True allows a '-' at the first index only
any illegal format or character returns false   */
function validateNumeric (str, isDecimal, isSigned)
{
	var inStr = $.trim(str);
	var reg = /[^0-9.-]/	
	
	//console.log("validateNumeric: "+inStr.indexOf('.') + "|" + inStr.length);
	
	if (!isSigned && inStr.indexOf('-') > -1)
		return false;
		
	if (!isDecimal && inStr.indexOf('.') > -1)
		return false;
	
	if (reg.test(inStr))
		return false;
	
	if(inStr.indexOf('.') > -1 && inStr.length == 1)
		return false;
	
	if(inStr.indexOf('.') == 0)
		return false;
	
	if (inStr.indexOf('.') != inStr.lastIndexOf('.'))
		return false;
	
	if(inStr.indexOf('.') == (inStr.length - 1))
		return false;
		
	if (inStr.lastIndexOf('-') > 0)
		return false;
	
	return true;
}

/* uses validateNumeric to alert for illegal chars or format in a numeric string
if illegal an alert pops up using the relevant field name and false is returned;
if legal returns true  */
function fnValidateNumeric(inStr, inFieldName, isDecimal, isSigned, isRequired)
{	
	if (!checkIsEmpty(inStr) && !validateNumeric(inStr, isDecimal, isSigned))
	{
		displayAlertDialog(inFieldName + " contains illegal characters or format", {title:"Invalid Number" });
		return false;
	}	
	if (isRequired && checkIsEmpty(inStr))
	{	
	    displayAlertDialog(inFieldName + " is required", {title:"Required Data Missing" });
		return false;
	}
	return true;
}



function checkIsEmpty(inval)
{
	if(inval == null || inval == undefined || inval == "undefined" || $.trim(inval) == "")
	{
		return true;
	}
	else 
	{
		return false;
	}
}



 function generateFromId(formCode) {
	 var toReturn = "-1";
	 var urlParam =
	       "?formCode=" + formCode;
	 $.ajax({
	       type: 'POST',
	       data: '',
	       url: "generateFromId.request" + urlParam,
	       contentType: 'application/json',
	       dataType: 'json',
	       async: false,
	       success: function (obj) {
	           if (obj.errorMsg != null && obj.errorMsg != '') {
	               //displayAlertDialog(obj.errorMsg);
	           } else if (obj.data[0] == null) {
	               //hideWaitMessage();
	           } else {
	        	   try{
	        		   toReturn = obj.data[0].val;
	        	   } catch(e) {
	        		   //
	        	   }
	           }
	       }
	 });
	 return toReturn;
 }
 
 function updateMenuItemUI(new_text, show)
 {
	try
 	{
 		console.log("updateMenuItemUI() new_text: ",new_text);
 		new_text = (new_text == null || new_text == undefined || new_text == 'undefined' || new_text == "" || new_text == "0")?"":"("+new_text+")";		
 		var hrefObj = $('#dropDownMenuBar').find('li[class="messages"] >a');
 		$(hrefObj).css("white-space", "nowrap");
 		var old_text = "";
 		
 		if(hrefObj.find('span').length > 0)
 		{
 			var $span = hrefObj.find('span');
 			old_text = $span.text();
 			$span.text(new_text);
 		}
 		else
 		{
 			hrefObj.html(hrefObj.text() + "<span style='color: red;'>"+new_text+"</span>");
 		}
// 		console.log("old_text:"+old_text+"|new_text:"+new_text+"|");
// 		if(show && old_text != new_text)
// 		{
// 			toastr.info("A new message has arrived", "",{timeOut: 3000, closeButton: true,closeDuration: 0}); //extendedTimeOut: 0,
// 		}
 	}
 	catch(e)
 	{
 		$('tr#dropDownMenuBar').find('li[class="messages"] >a').text("Messages");
 		console.log(e);
 	}
 	
 }
 
 function fgReloadForm(newUrl, isRefresh) {
	 if(typeof newUrl !== 'undefined' && newUrl != null && $.trim(newUrl) != '') {
		 url_ = newUrl;
	 } else {
		 url_ = window.location.href;
	 }
	  
	 if(url_.indexOf('/initid.request') > 0) {  
		 url_ = url_.replace('/initid.request','/init.request');
	 }
	 
	 if(url_.indexOf('&stateKey=') == -1) {
		 url_ = url_ + '&stateKey=' + $('#stateKey').val();
	 }
	 
	 if(typeof isRefresh !== 'undefined' && isRefresh != null && isRefresh) {
		 if(url_.indexOf('#!')>-1){//when it is a report page and the suffix of the url is taken from the report api
			 //url_ = url_.substr(url_,url_.indexOf('#!'))+ "&refreshFlag=1"+url_.substr(url_.indexOf('#!'))
		 } else {
			 url_ = url_ + "&refreshFlag=1";
		 }
	 }
	 
//	 alert(url_);
	 window.location.href = url_;
	 return true;
 }
 
 function openBreadcrumbDropdown()
 {
	 $("div#breadcrumbDropdownDiv").toggle();
 }
 
 function Logout()
 {
	 window.location.href = '../?exit=true&curTabStateKey='+$('#stateKey').val();
 }
 
 /* check if given string is/contains a JSON */
 function checkIfJSON(text)
 {
 	try {
 	var len = text.length;
 	var lpi = text.indexOf('{');
 	var rpi = text.lastIndexOf('}');
 	if ((lpi == 0 && rpi == len-1)  // json object - {}
 			||
 		(lpi == 1 && rpi == len-2)  // json array - [{}]
 	   )
 	{
 		return true;
 	}
 	else
 		return false;
 }
 	catch(e) {
 		console.log("error in checkIfJSON() for text:",text);
 		console.error(e);
 		return false;
 	}
 }
 
 function doReset(){
	try 
	{
		var domId = $('#tableId').val();
		var allCols = [];
		var removedArr =  [];
		var colNamesArr = [];
		var colNewOrderArr = [];
		var arrString = "";
		var $chks_elements = $('ul#reorderable input[type="checkbox"]');
		var chks_elements_length = $chks_elements.length;
		
		$chks_elements.each(function(){
			var $el = $(this);
			var colName = $el.val();
			var isChecked = $el.is(':checked');
			if(!isChecked) {
				removedArr.push(colName);
				}
			if($el.attr('colIndex')) {
				colNewOrderArr.push(parseInt($el.attr('colIndex')));
				}
			colNamesArr.push(colName);
		});
		
		var j=0;
		if (removedArr.length == chks_elements_length){
				j=1;
		}
		for(;j<removedArr.length;j++) {
	    	if(arrString == "")
	    	{
	    		arrString = removedArr[j] ;
	    	}
	    	else
	    	{
	    		arrString = arrString + "@"  + removedArr[j];
	    	}
	    }    
		var toReturnObj = {"colsRemoved":arrString,"colsNames":colNamesArr,"colsNewOrder":colNewOrderArr};
		$('#toReturn').val(JSON.stringify(toReturnObj));
		
		close_back.setAttribute('flag', '1'); 
		parent.$('#prevDialog').dialog('close');
	}
	catch(e){ 
		parent.$('#prevDialog').dialog('close');
	}
	}
 
 function outPutLabel(domId, labelCode, labelData) {
	 try {
		 $('#' + domId + '_FormLabelSection').find('input[type="hidden"][name="_labelCode"]').val(labelCode);
			$('#' + domId + '_FormLabelSection').find('input[type="hidden"][name="_labelData"]').val(labelData);
			$('#' + domId + '_FormLabelSection').submit();
	 } catch(e) {
		 displayAlertDialog("Error on label print!");
	 }
		
 }
 
 function getActiveTabID()
 {
	var activeTabIndex = $("#tempalteTabs").tabs('option', 'active');
	var href = $("#tempalteTabs ul>li a").eq(activeTabIndex).attr('href');
	var activeTabId = href.substr(1,href.length);
	return activeTabId;
 }
 
 function openNewTab(page) {
	    var page_ = page;
	    try {
	    	page_ = replaceUrlParam(page,"stateKey","-1");
	    } catch(e) {
	    	alert("openNewTab replaceUrlParam failure");
	    }
	    
		var win = window.open(page_);
		if (win) {
		    //Browser has allowed it to be opened
		    win.focus();
		    return;
		} else {
		    //Browser has blocked it
		    alert('Please allow popups for this website');
		}
}
 
 function replaceUrlParam(url, paramName, paramValue) {
     if (paramValue == null) {
         paramValue = '';
     }
     var pattern = new RegExp('\\b('+paramName+'=).*?(&|#|$)');
     if (url.search(pattern)>=0) {
         return url.replace(pattern,'$1' + paramValue + '$2');
     }
     return url;
 }
 
 /**
  * 
  * @param formCode_
  * @param formId_
  * @param paramName
  * @param paramVal
  * @param callbackFunc
  * @param callbackFuncArgs array of arguments
  * @returns
  */
 function setFormParamMap(formCode_, formId_,paramName,paramVal,callbackFunc,callbackFuncArgs) {
	 
		var urlParam =
		       "?formId=" + formId_ + "&formCode=" + formCode_ + '&userId=' + $('#userId').val() + '&stateKey=' + $('#stateKey').val();
	 
	   
	    // call...
		$.ajax({
			type : 'POST',
	        data: '{"action" : "setParameterMap","' + 'data":[{"code":"' + paramName + '","val":"' + paramVal + '"}' + '],' + '"errorMsg":""}',
			url : "./setParameterMap.request" + urlParam,
			contentType : 'application/json',
			dataType : 'json', 
			success : function(obj) {
				if(callbackFunc!=undefined && typeof callbackFunc ==='function'){
					callbackFunc(callbackFuncArgs);
				}
			},
			error : handleAjaxError
		});
 }
 
 function displayFadeMessage(message)
 {
	 try {
		 if($( "div.dynamic-savedisplay-success" ).length > 0) {
			 $( "div.dynamic-savedisplay-success" ).remove();
		 }
		
		 $('body').append('<div id="dynamicFadeDiv" class="dynamic-savedisplay-alert-box dynamic-savedisplay-success">'+message+'</div>');
		 if($('.dynamic-savedisplay-alert-box').css('position') != 'sticky') { // in the dynamic-savedisplay-alert-box class the  position is absolute if not (static is the default position) the css is not exists and we will show the save successfully alert message.
			 $( "div.dynamic-savedisplay-success" ).remove();
			 displayAlertDialog(getSpringMessage('updateSuccessfully'),'');
		 } else {
			 var elems = document.body.getElementsByTagName("*");
			  var highest = Number.MIN_SAFE_INTEGER || -(Math.pow(2, 53) - 1);
			  for (var i = 0; i < elems.length; i++)
			  {
			    var zindex = Number.parseInt(
			      document.defaultView.getComputedStyle(elems[i], null).getPropertyValue("z-index"),
			      10
			    );
			    if (zindex > highest)
			    {
			      highest = zindex;
			    }
			  }
			 $( "div.dynamic-savedisplay-success" ).css('z-index',highest).fadeIn( 300 ).delay( 1500 ).fadeOut( 400 );
		 }
	 } catch(e) {
		 //do nothing
	 }
 }
 
 function returnBasicHtml (html) {
	 var newVal;
	 newVal = html.replace(/<br>/gi,'\n').replace(/&nbsp;/gi,' ').replace(/&gt;/gi,'>');
	 newVal = newVal.replace(/style+=+"[^\"]*"/gi,'').replace(/style+=+'[^\']*'/gi,''); // remove style
	 newVal = newVal.replace(/"[^\"]*"/gi,'').replace(/'[^\']*'/gi,'').replace(/'/gi,'').replace(/"/gi,''); // remove any string between quotes and quotes
	 newVal = newVal.replace(/(<\/?(?:b|i|u)[^>]*>)|<[^>]+>/gi,'$1'); // remove all tags except italic bold and underline 
	 newVal = newVal.trim();
	 return newVal;
 }
 