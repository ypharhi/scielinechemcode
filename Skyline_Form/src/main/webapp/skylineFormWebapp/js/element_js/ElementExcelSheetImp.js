//var spreadSheetObject = {};
//var catalogItemDataObject = {};
var dataHolder = [];
var outputDataHolder = [];

var ElementExcelSheetImp = {
    value_: function (val_, changeType) { // changeType 1-ajax / 2 -save
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
        	"value": getDataFromSpreadSheet(domId, changeType),
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
	console.log('SPREADSHEET RELOAD!!!')
	document.getElementById(domId+'_spreadIframe').contentWindow.location.reload();
}

function onLoadSpreadsheetElement(data,outputData,domId,isToolBarDisplay,isDisabled) {
		console.log('---------1. ON LOAD SPREAD ELEMENT------------');
		dataHolder[domId] = data;
		outputDataHolder[domId] = outputData;
		document.getElementById(domId+"_spreadIframe").contentWindow.onclick  = function(){onSpreadFocused(domId);};
}   

function onLoadIframeSpreadsheet_(isAjaxExcelLoad, fileId, defaultfileId, domId,isToolBarDisplay,isDisabled,SpreadSheetsLicenseKey,SpreadSheetsDesignerLicenseKey) {
	var iframe = document.getElementById(domId+"_spreadIframe");
    var iframeDoc = iframe.contentDocument || iframe.contentWindow.document;
	if(isAjaxExcelLoad == 1) { //isAjaxExcelLoad (defined in app prop and pass as parameter) will use the build in compress in ajax call data (if define in the tomcat server.xml compress=on)
		$(document).ready(function(){//not initializing the designer until the document is ready,else there are some wrong UI and functionalities(such as transparent dropdown) 
			document.getElementById(domId+"_spreadIframe").contentWindow.onLoadIframeSpreadsheet(domId,SpreadSheetsLicenseKey,SpreadSheetsDesignerLicenseKey);
			var checkExist = setInterval(function() {//added timeout in order to avoid the compressed toolbar and empty data (since onLoadIframeSpreadsheet was fired before onLoadSpreadElement)
				if (typeof $('#'+domId).find('iframe')[0].contentWindow.onLoadSpreadsheetData === 'function' 
					&& iframeDoc.readyState  == 'complete' ) {//check if the loading of the iframe is complete
				      clearInterval(checkExist);
				      afterLoadIframeSpreadsheet(isAjaxExcelLoad, fileId, defaultfileId, domId,isToolBarDisplay,isDisabled,SpreadSheetsLicenseKey,SpreadSheetsDesignerLicenseKey);
				   }
				}, 100);
		});
	} else {
		$(document).ready(function(){//not initializing the designer until the document is ready,else there are some wrong UI and functionalities(such as transparent dropdown) 
			if (dataHolder == null || dataHolder == 'null' || dataHolder == 'undefined') { //when the func onLoadIframeSpreadsheet fired before onLoadSpreadsheetElement -> then the data should be taken by from the server
				onLoadIframeSpreadsheet_(1, fileId, defaultfileId, domId,isToolBarDisplay,isDisabled,SpreadSheetsLicenseKey,SpreadSheetsDesignerLicenseKey)
			} else {
            	document.getElementById(domId+"_spreadIframe").contentWindow.onLoadIframeSpreadsheet(domId,SpreadSheetsLicenseKey,SpreadSheetsDesignerLicenseKey);
            	var checkExist = setInterval(function() {//added timeout in order to avoid the compressed toolbar and empty data (since onLoadIframeSpreadsheet was fired before onLoadSpreadElement)
    				if (typeof $('#'+domId).find('iframe')[0].contentWindow.onLoadSpreadsheetData === 'function'
    					&& iframeDoc.readyState  == 'complete' ) {
    				      clearInterval(checkExist);
    				      afterLoadIframeSpreadsheet(isAjaxExcelLoad, fileId, defaultfileId, domId,isToolBarDisplay,isDisabled,SpreadSheetsLicenseKey,SpreadSheetsDesignerLicenseKey);
    				   }
    				}, 100);
            }
		});
	}
} 

function afterLoadIframeSpreadsheet(isAjaxExcelLoad, fileId, defaultfileId, domId,isToolBarDisplay,isDisabled,SpreadSheetsLicenseKey,SpreadSheetsDesignerLicenseKey){
	if(isAjaxExcelLoad == 1) { //isAjaxExcelLoad (defined in app prop and pass as parameter) will use the build in compress in ajax call data (if define in the tomcat server.xml compress=on)
		$(document).ready(function(){//not initializing the designer until the document is ready,else there are some wrong UI and functionalities(such as transparent dropdown) 
			setTimeout(function(){//added timeout in order to avoid the compressed toolbar and empty data (since onLoadIframeSpreadsheet was fired before onLoadSpreadElement)
				console.log('---------2. ON LOAD SPREAD IFRAME (ajax) ------------');
				
				var urlParam = "?formId=" + $('#formId').val() + "&formCode="
				+ $('#formCode').val() + "&userId=" + $('#userId').val() +
				"&stateKey=" + $('#stateKey').val() + "&fileId=" + fileId + "&defaultfileId=" + defaultfileId + "&domId=" + domId;
				
				$.ajax({
					type : 'POST',
				    url : "./getExcelDataById.request" + urlParam,
					contentType : 'application/json',
					dataType : 'json',
//					async: false,
					success : function(obj) {
						var edata_ = JSON.parse(obj.data[0].val);
						console.log('---------2.1 .Ajax - LOAD SPREAD ELEMENT------------');
						document.getElementById(domId+"_spreadIframe").contentWindow.onclick = function(){onSpreadFocused(domId);};
						$('#'+domId).find('iframe')[0].contentWindow.onLoadSpreadsheetData(edata_.excelFullData, edata_.outputData,domId,isToolBarDisplay,isDisabled,SpreadSheetsLicenseKey,SpreadSheetsDesignerLicenseKey);
					},
					error : handleAjaxError
				});
			},100);
		});
	} else {
		$(document).ready(function(){//not initializing the designer until the document is ready,else there are some wrong UI and functionalities(such as transparent dropdown) 
			setTimeout(function(){//added timeout in order to avoid the compressed toolbar and empty data (since onLoadIframeSpreadsheet was fired before onLoadSpreadJS)
				console.log('---------2. ON LOAD SPREAD IFRAME------------');
				console.log('Spreadsheet dataHolder:',dataHolder);
				$('#'+domId).find('iframe')[0].contentWindow.onLoadSpreadsheetData(dataHolder[domId],outputDataHolder[domId],domId,isToolBarDisplay,isDisabled,SpreadSheetsLicenseKey,SpreadSheetsDesignerLicenseKey);
			},100);
			
		});
	}
}

//***** Load DATA by data param END!

function isSpreadsheetEmpty(domId) {
	return window.frames[domId+'_spreadIframe'].isSpreadsheetEmpty(domId);
}

function onSpreadsheetChange(domID){
	//update the datachanged prop
	parent.prop.dataChanged = true;
	$('#'+domID).attr('is_changed_flag','1');
}

// return spread sheet data
function getDataFromSpreadSheet(domId, calltype) {
	try{
		return window.frames[domId+'_spreadIframe'].getDataFromSpreadSheet(domId, calltype);
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