/**
 * customer click event
 * 
 * @param customerFunction
 * @param action
 * @returns
 */
function generalBL_generalClickEvent(customerFunction, action) { // customerClickEvent
	if (customerFunction == 'makeAjaxCallEvent') {
		makeAjaxCallEvent(action);
	} else if (customerFunction == 'sysConfigCalcSetter') {
		sysConfigCalcSetter(action);
	} else if (customerFunction == "executeSQLGenerator") {
		executeSQLGenerator();
	} else if(customerFunction == "generateDynamicReport") { // yp 12042020 adama demo report develop
		generateDynamicReport();
	}
}

function makeAjaxCallEvent(action) {
	onChangeAjax(action);
}

function sysConfigCalcSetter(action) {

	showWaitMessage("Please wait...");
	prop.onChangeAjaxFlag = false;
	var allData = getformDataNoCallBack(1);

	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + "&userId=" + $('#userId').val()
			+ "&eventAction=" + action + "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "doSave",
		data : allData,
		errorMsg : ""
	});

	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',
		success : function(obj) {
			hideWaitMessage();
			object = JSON.parse(obj.data[0].val);
			for (key in object) {
				if (object.hasOwnProperty(key)) {
					if ($('#' + key).attr('type') == 'Number') {
						$('#' + key).attr('realvalue', object[key]);
						$('#' + key).attr('title', object[key]);
						$('#' + key).val(object[key]);
						// $('#' + key).val('999'); //for tests...
					}
				}
			}
		},
		error : handleAjaxError
	});

}

function executeSQLGenerator() {
	//check if some query is selected
	if(($('#sqlText').val() == null || $('#sqlText').val() == '')) {
		displayAlertDialog("Please type your query.");
		return;
	}
	// get all
	var allData = getformDataNoCallBack(1);
	
	// url call
	var urlParam = "?formId="+ $('#formId').val()
	+ "&formCode="+ $('#formCode').val()
	+ "&eventAction=executeSQLGenerator";
	
	var data_ = JSON.stringify({
		action : "executeSQLGenerator",
		data : allData,
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
			if(obj.data[0].val == null || obj.data[0].val.length == 0) {
				onElementDataTableApiChange('sqlResultTable', null,null,true); 
			} else {
				onElementDataTableApiChange('sqlResultTable', null,null,true); 
			}
		},
		error : handleAjaxError
	});
}


function generateDynamicReport() {
	//check if some query is selected
	if(($('#DYNAMICREPORTSQL_ID').val() == null || $('#DYNAMICREPORTSQL_ID').val() == '')) {
		displayAlertDialog("Please select data for the report.");
		return;
	}
	// get all
	var allData = getformDataNoCallBack(1);
	
	// url call
	var urlParam = "?formId="+ $('#formId').val()
	+ "&formCode="+ $('#formCode').val()
	+ "&eventAction=generateDynamicReport";
	
	var data_ = JSON.stringify({
		action : "generateDynamicReport",
		data : allData,
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
			if(obj.data[0].val == null || obj.data[0].val.length == 0) {
				onElementDataTableApiChange('sqlResultTable', null,null,true); 
			} else {
				onElementDataTableApiChange('sqlResultTable', null,null,true); 
			}
		},
		error : handleAjaxError
	});
}

