/**
 * general click event
 * @param action
 * @returns
 */
function elementButtonClickEvent(customerFunction, action) { 
	//general event call
	if (customerFunction == null || customerFunction.length == 0) {
		generalClickEvent(action); 
	} 
	//specific event from bl_<Customer>_js
	else {
		generalBL_generalClickEvent(customerFunction, action); //change name generalBL_generalClickEvent to customerClickEvent after chem branch (in bl_customers)
	}
}
 
/**
 * generalFormEvent
 * @returns
 */
function generalClickEvent(action) {
	showWaitMessage("Please wait...");
    prop.onChangeAjaxFlag = false;
    var allData = getformDataNoCallBack(1);

    var urlParam =
        "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&eventAction=" + action + "&isNew=" + $('#isNew').val();
    
    var newurl = url();
	newurl = newurl.substr(0,newurl.indexOf('/skylineForm'));
    var stringifyToPush = {
			code : 'localHost',
			val : newurl,
			type : "AJAX_BEAN",
			info : 'na'
		};
    var allData = allData.concat(stringifyToPush);

    var data_ = JSON.stringify({
        action: "doSave",
        data: allData,
        errorMsg: ""
    });

    $.ajax({
        type: 'POST',
        data: data_,
        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) {
        	hideWaitMessage();
        	//TODO find solution or use in Adama BL to the below code....
        	if(action != null && (action == 'AddExpSeriesIndex' || action == 'RemoveExpSeriesIndex')) { 
        		onElementDataTableApiChange('experimensTable');
            	onElementDataTableApiChange('formulationPropertiesTable');
        	}
        },
        error: handleAjaxError
    });
}