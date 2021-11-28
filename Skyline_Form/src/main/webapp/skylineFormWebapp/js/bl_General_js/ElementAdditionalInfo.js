/*** JS contents realization of custom functions ***/

function customInfoClickEvent(customerFunction, action,formCode)
{
	if(customerFunction == "getWFStatusInfo")
	{
		getWFStatusInfo(action,formCode);
	}
	else if(customerFunction == "getGeneralInfo")
	{
		getGeneralInfo(action,formCode);
	}
}

function getGeneralInfo(action,formCode) 
{
	var authInfo = insertAuthzGenralInfo();
	$('#formGeneralInfo').val(authInfo);
	var  text = $('#formGeneralInfo').val();
	console.log("text: " + text);
	if(text != null && text != "")	
	{
		$('#elementAdditInfoDialog span').html(text);
		var elem = $('div[id="elementAdditInfoDialog"]').parent();
    	elem.css('cssText', elem.attr('style') + 'z-index: 350 !important'); // it's important to add new css to already exists style   
		$("#elementAdditInfoDialog").dialog('open');
	}
}

function getWFStatusInfo(action,formCode) 
{
	
	showWaitMessage("Please wait...");
    prop.onChangeAjaxFlag = false;
    var allData = getformDataNoCallBack(1);

    var urlParam =
        "?formId=" + $('#formId').val() + "&formCode=" + ((typeof formCode === 'undefined') ? $('#formCode').val():formCode) + "&userId=" + $('#userId').val() + "&eventAction=" + action + "&isNew=" + $('#isNew').val();
    //console.log(urlParam);
    var data_ = JSON.stringify({
        action: "doSave",
        data: allData,
        errorMsg: ""
    });
    $.ajax({
        type: 'POST',
        data:data_,
        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(), //TODO key check
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) 
        {
        	//console.log("getWFStatusInfo obj: ");
        	//console.log(obj);
        	try
        	{
        		if(obj != null && obj !== undefined)
        		{
	        		var  text = obj.data[0].val;
	        		console.log("text: " + text);
		        	if(text != null && text != "")
		        	{
		        		$('#elementAdditInfoDialog span').html(text);
		        		var elem = $('div[id="elementAdditInfoDialog"]').parent();
	                	elem.css('cssText', elem.attr('style') + 'z-index: 350 !important'); // it's important to add new css to already exists style   
		        		$("#elementAdditInfoDialog").dialog('open');
		        	}
        		}
	        	hideWaitMessage();
        	}
        	catch(e)
        	{
        		hideWaitMessage();
        		console.log("ERROR in getWFStatusInfo(action): " + action + " return obj: ");
        		console.log(obj);
        		console.log(e);
        	}
        },
        error: handleAjaxError
    });
}

function getCalculatedFieldsInfo()
{
	$('#elementAdditInfoDialog span').html(getSpringMessage('missingOneOfTheArgumentsInCalc'));
	$("#elementAdditInfoDialog").dialog('open');
}

function hideAdditInfoDialog()
{
	try {
		$('div.divAdditCustomInfo').style('display','none');
	} catch(e) {
		
	}
	
}

function initAdditInfoDialog()
{
	$('body').append(
	        '<div id="elementAdditInfoDialog" style="overflow-y:auto">\n' +
	        '<span ></span>'+
	        '</div>\n');
    var dialog = $("#elementAdditInfoDialog").dialog({
        autoOpen: false,
        title: 'Information',
        height: 300,
        width: 500,
        modal: false,
        overflow:"auto"
    });
}