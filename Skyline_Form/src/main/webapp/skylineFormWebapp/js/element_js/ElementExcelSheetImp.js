//var spreadSheetObject = {};
//var catalogItemDataObject = {};
var dataHolder = [];

var ElementExcelSheetImp = {
    value_: function (val_) {
    	var domId = $(val_).attr('id');
        var elementID = $(val_).attr('elementID');
    	var isChangedflag=0;
    	var iframe = document.getElementById(domId+'_spreadIframe');
		//var internalChange = iframe.contentWindow.actions_.isFileModified;
        if($('#'+domId).attr("is_changed_flag") == "1") {
        	isChangedflag = 1;
        } else {
        	isChangedflag = 0;//TODO:check if all the cases are covered in the comply spreadjs helper.EditStarting
        }
        var data_ = {
            "elementID": elementID,
        	"value": getValueFromOutputSheet(domId),
            "isChangedflag": isChangedflag 
        };
        return JSON.stringify(data_);
    },
    setvalue_: function (val_) {
        //important: should add attribute 'lastvalue' to the element imp in the poll (like in ElementAutoCompleteIdValDDLImp)
        $(val_).val($(val_).attr('lastvalue'));
    },
    setDefaultValueForUnitTest_: function (val_) {
    },
    displayValue_: function (val_) {
    	'Excel Data';
    }
};

function onSpreadFocused(domId){
	onSpreadsheetChange(domId);
}

function updateElementExcelSheet(obj){
	if (typeof obj.isDisabled !== 'undefined') {
        if (obj.isDisabled.toLowerCase() == "false") {
        	disableSpreadsheet(obj.domId ,false);
        } else {
            disableSpreadsheet(obj.domId ,true);
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

function disableSpreadsheet(domId,isDisabled){
	if(	window.frames[domId+'_spreadIframe'].length>0){
		window.frames[domId+'_spreadIframe'].disableSpreadsheet(domId,isDisabled);
	}
}

function reloadExcelSheet(domId){
	document.getElementById(domId+'_spreadIframe').contentWindow.location.reload();
}

function onLoadSpreadJS(data,domId,isToolBarDisplay,isDisabled) {
	dataHolder[domId] = data;
	document.getElementById(domId+"_spreadIframe").contentWindow.onclick  = function(){onSpreadFocused();};
}  

function onLoadIframeSpreadsheet(domId,isToolBarDisplay,isDisabled,SpreadSheetsLicenseKey,SpreadSheetsDesignerLicenseKey) {
	document.getElementById(domId+"_spreadIframe").contentWindow.onLoadSpreadsheetElement(domId,SpreadSheetsLicenseKey,SpreadSheetsDesignerLicenseKey);
	setTimeout(function(){//added timeout in order to avoid the compressed toolbar and empty data (since onLoadIframeSpreadsheet was fired before onLoadSpreadJS)
		$('#'+domId).find('iframe')[0].contentWindow.onLoadSpreadsheet(dataHolder[domId],domId,isToolBarDisplay,isDisabled,SpreadSheetsLicenseKey,SpreadSheetsDesignerLicenseKey);
	},100);
}  

function isSpreadsheetEmpty(domId) {
	return window.frames[domId+'_spreadIframe'].isSpreadsheetEmpty(domId);
}

function onSpreadsheetChange(domID){
	//update the datachanged prop
	parent.prop.dataChanged = true;
	$('#'+domID).attr('is_changed_flag','1');
}

// return output sheet (spread.getSheet(2)) as array of key and values
function getValueFromOutputSheet(domId) {
	try{
		return window.frames[domId+'_spreadIframe'].getValueFromOutputSheet(domId);
	} catch(err){
		return null;
	}
}

function clearSpreadsheet(domId){
	setValueToSpreadSheet(domId,'{}');
}

function setValueToSpreadSheet(domId,value){
	window.frames[domId+'_spreadIframe'].setValueToSpreadSheet(domId,value);
}

function importExcel(domId,fileWrapper,buffer, file){
	window.frames[domId+'_spreadIframe'].importExcel(domId,fileWrapper,buffer, file);
}