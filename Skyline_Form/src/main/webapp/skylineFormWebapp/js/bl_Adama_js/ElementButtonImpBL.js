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
	} else if (customerFunction == "generateHistoryReport") {
		generateHistoryReport(action);
	} else if(customerFunction == "searchLabel"){
		searchLabel();
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

function generateHistoryReport(action) {
 	var allData = getformDataNoCallBack(1);
	// url call
	var urlParam = "?formId="+ $('#formId').val()
				+ "&formCode="+ $('#formCode').val()
				+ "&userId="+ $('#userId').val()
				+ "&eventAction=generateHistoryReport"
				+ "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "generateHistoryReport",
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
				//displayAlertDialog("Enter formId");
				onElementDataTableApiChange('hstTable', null,null,true); 
			} else {
				onElementDataTableApiChange('hstTable', null,null,true); 
			}
		},
		error : handleAjaxError
	});
}

function searchLabel() {
	var formId =$("#SeachLabelName").val().trim();
	formCode = getFormCodeBySeqId(formId);
	// show the Loading... label with fade
	$("font[color=red]").css('display', 'block');
	$("font[color=red]").fadeOut(3000);
	if((formCode == null || formCode == "") || (formId == null || formId == ""))
	{
		displayAlertDialog("Not found");
	}else{
		// get form type=> navigate to struct and invitem only

		var allData = [{
				code : 'formCode',
				val : formCode,
				type : "AJAX_BEAN",
				info : 'na'
			}];
		// url call
		var urlParam = "./getFormType.request?formCode="+formCode;

		var data_ = JSON.stringify({
			action : "getFormType",
			data : allData,
			errorMsg : ""
		});

		//showWaitMessage();
		// call...
		$.ajax({
			type : 'POST',
			data : data_,
			url :  urlParam ,
			contentType : 'application/json',
			dataType : 'json',

			success : function(obj) {
				var isStruct = (obj.data[0].val == 'STRUCT' || obj.data[0].val =='INVITEM');// obj.data[0].val
				if(!isStruct){
					displayAlertDialog('Navigation has been stopped since the expected form is not a struct');
				} else {
					checkAndNavigate([formId ,formCode,'','false',true]);
				}
				//hideWaitMessage();
			},
			error : handleAjaxError
		});
	}
}

