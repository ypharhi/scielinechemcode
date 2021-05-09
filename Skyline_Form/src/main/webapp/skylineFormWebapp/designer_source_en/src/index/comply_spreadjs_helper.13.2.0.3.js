//Very important!In order to avoid updating the cache when upgrading the version, do the following:
//When changing this file, do the following:
//1.change the postfix of the file name- promote the version 13.2.0.(X+1)
//2.change the link to this file in two places: skylineFormWebapp/include/includeExcelSheet.jsp
//												skylineFormWebapp/designer_source_en/src/index/index.html

//every operation of inserting data to the spreadsheet should be followed by the functions defineSpreadsheet&updateDataChanged

var dataholder_=[];
var spraedWorkbook_=[];
var isToolBarDisplay_=[];
var isDisabled_ = [];//an array of all the excel objects when each cell mentions whether it's disabled or not

//calling this function from outside the iframe.
function onLoadIframeSpreadJS_(data,domId,isToolBarDisplay,isDisabled) {
	dataholder_[domId] = data;
	isToolBarDisplay_[domId] = isToolBarDisplay;
	isDisabled_[domId] = isDisabled;
}

//calling the following function from within the iframe. Referring to the global data is from the parent.
//calling those 2 function from contextMenu.js designer.loader.ready
function onReadySpreadJS_(spraedWorkbook) {
	var spread = spraedWorkbook;
	var domId = $(window.frameElement).parent('[element="ElementExcelSheetImp"]').attr('id');
	spread.fromJSON(parent.dataholder_[domId]);
	defineSpreadsheet(spraedWorkbook);//important! this operation should be executed after the fromJson operation.
	spraedWorkbook_[domId] = spraedWorkbook;
	parent.document.getElementById(domId+"_spreadIframe").contentWindow.onclick  = function(){parent.onSpreadFocused();};
}

function defineSpreadsheet(spraedWorkbook){
	//define the visible tabs to 1 only and disable adding another tab
	spraedWorkbook.setSheetCount(1);
	spraedWorkbook.options.newTabVisible = false;
	var insertSheetIndex = null;
	$.each(spraedWorkbook.contextMenu.menuData, function (p, v) {
	    if (v.name === 'designer.insertSheet') { //disables adding tab by the right click
	    	insertSheetIndex = p;//removing the element in the p index in the contextMennu array
	    }
	});
	if(insertSheetIndex!=null){
		spraedWorkbook.contextMenu.menuData.splice(insertSheetIndex, 1);
	}
	spraedWorkbook.bind( GC.Spread.Sheets.Events.SelectionChanged , function (e, args) {
			updateDataChanged();
	});
	var hiddenTabs = getHiddenTabsBL();
	for(var i=0;i<hiddenTabs.length;i++){
		hideRibbonDesignTab(hiddenTabs[i]);//hide the 'file' tab
	}
	var domId = $(window.frameElement).parent('[element="ElementExcelSheetImp"]').attr('id');
	if(parent.isToolBarDisplay_[domId]==false){
		$('.header').css('display','none');
		$('.vertical-splitter').css('display','none');
		$('.fill-spread-content').css('position','inherit');
	}
	
	var sheet = spraedWorkbook.getActiveSheet();
	sheet.setColumnCount(26);
	sheet.setRowCount(3000);
	//set the f4 key to switch the formula reference between relative, absolute, and mixed when editing formulas
	spraedWorkbook 
	    .commandManager()
	    .setShortcutKey("changeFormulaReference", 115, false, false, false, false);
	
    disableSpreadsheet_();//COMPLY- disables the spreadsheet from being editable
}

function getHiddenTabsBL(){
	var hiddenTabs = [];
	//if(parent.$('#formCode').val() != 'SpreadsheetTempla'){
		hiddenTabs.push(0);
	//}
	return hiddenTabs;
}

function hideRibbonDesignTab(tabIndex) {
    $(".ribbon-bar>ul").find("li:eq(" + tabIndex + ")").css("display", "none");
    $(".ribbon-bar>div:eq(" + tabIndex + ")").attr("aria-hidden", true);
}

function setValueToSpreadSheet_(domId,value){  
	var wb3 = spraedWorkbook_[domId];
	wb3.fromJSON(JSON.parse(value));
	defineSpreadsheet(wb3);
	updateDataChanged();
}

//the function  is fired from widgets/gcui/gcui.js
function disableSpreadsheet_(isDisabled){//adding a check if the parent element has authorizatonDisabled/disabledclass
	var domId = $(window.frameElement).parent('[element="ElementExcelSheetImp"]').attr('id');
	var $wraperElement = parent.$('#'+domId);
	if(isDisabled || isDisabled==undefined && parent.isDisabled_[domId]
	||$wraperElement.hasClass('generalDisabled')||$wraperElement.hasClass('disablePage')
	||$wraperElement.hasClass('disabledclass')||$wraperElement.hasClass('authorizationDisabled')){
		$('.ui-tabs-panel.gcui-ribbon-panel').css('pointer-events','none').css('cursor','not-allowed');//toolbar
		$('#ssvp_vp').css('pointer-events','none').css('cursor','not-allowed');//#ssvp_vp
		$('.formulaBar').css('pointer-events','none').css('cursor','not-allowed');//formula bar
	}else{
		$('.ui-tabs-panel.gcui-ribbon-panel').css('pointer-events','auto').css('cursor','auto');//toolbar
		$('#ssvp_vp').css('pointer-events','auto').css('cursor','auto');//#ssvp_vp
		$('.formulaBar').css('pointer-events','auto').css('cursor','auto');//formula bar
	}
}

function updateDataChanged(){
	var domId = $(window.frameElement).parent('[element="ElementExcelSheetImp"]').attr('id');
	parent.onSpreadsheetChange(domId);
}

function getValueFromOutputSheet_() {
	try {
		var ROW_MAX = 10;
	    var spread = $('#ss').data('workbook');
	    spread.getActiveSheet().endEdit(false);
	    var dataObj = {};
	    var fullObj = {};
//	    var sheet = spread.getSheet(2);
	    var sheet = spread.getSheetFromName('output');
	    if(sheet != null) {
	    	sheet.setRowCount(ROW_MAX, GC.Spread.Sheets.SheetArea.viewport);
		    var rowCount = sheet.getRowCount();
		    for (var i = 1; i < rowCount; i++) {
		        var currFieldID = sheet.getValue(i, 0);
		        var currFieldValue = sheet.getValue(i, 1);
		        if (currFieldID != null) {
		            dataObj[currFieldID] = currFieldValue;
		        }
		    }
	    }
	    var currSpreadConfig = spread.toJSON();
	    fullObj["output"] = dataObj;
	    fullObj["excelFullData"] = currSpreadConfig;  
	    //alert(JSON.stringify(fullObj));
	    return JSON.stringify(fullObj);
	} catch(err) {
		console.log(err);	
	} 
}

function isSpreadsheetEmpty_() {
	var sheet = $('#ss').data('workbook');  
	var json = sheet.toJSON();
	  var isEmpty = true;
	  $.each(json.sheets,function(index){
		  if(json.sheets[index].charts !== undefined ||
				json.sheets[index].floatingObjects !== undefined ||
				json.sheets[index].data !== undefined && json.sheets[index].data.dataTable !== undefined){
			  isEmpty = false;
			  return false;
		  }
	  });
	  return isEmpty;
}
 function expandCompressSpreadIframe(elem){
	 var $iframe = $(window.frameElement).parent('[element="ElementExcelSheetImp"]');
	 var $expandCompressItag = $(elem).find('i');
	 if($expandCompressItag.attr('class').indexOf('expand')!=-1){
		 var width = $(window.top).width();
		 var height = $(window.top).height();
		 //$iframe.resizable();
		 var zindex = parent.$('.floatingButtonsPanelContainer').css('z-index');
		 zindex = Number(zindex)-1;
		 $iframe.css('position','absolute').css('left',0).css('z-index',zindex)
		 		.css('width',width).css('height',height).css('top',0); 
		 $expandCompressItag.removeClass('fa fa-expand').addClass('fa fa-compress');
	 } else if($expandCompressItag.attr('class').indexOf('compress')!=-1){
		 var width = $iframe.attr('basicWidth');
		 var height = $iframe.attr('basicHeight');
		 $iframe.css('top','auto').css('position','relative').css('z-index','auto')
		 		.css('width',width).css('height',height); 
		 $expandCompressItag.removeClass('fa fa-compress').addClass('fa fa-expand');
	 }

 }
	
 function importExcel_(domId,fileWrapper,buffer, file){
	 var file;
	 if(buffer!=undefined){
		 file = new Blob([buffer]);
	 } else {
		 var files = parent.$(fileWrapper)[0].files;
    	file = files && files[0];
	 }
	
     var type = getExtension(file.name);
	 //var type = "xlsx";
     var reader = new FileReader();
     reader.onload = function () {
         if (type === "xlsx") {
             //importExcel('spreadsheet',this.result, file);
        	 var options='{"excelOpenFlags":{"ignoreStyle":false,"ignoreFormula":false,"frozenColumnsAsRowHeaders":false,"frozenRowsAsColumnHeaders":false,"doNotRecalculateAfterLoad":false},"password":""}';
        		options = JSON.parse(options); 
        		var excelIo = new GC.Spread.Excel.IO();
        		excelIo.open(this.result, function (json) {
        			var workbook = spraedWorkbook_[domId];
        			var value = JSON.stringify(json);
    				workbook.fromJSON(JSON.parse(value));
        			if(json.sheetCount>1){
                    	workbook.removeSheet(0);//COMPLY -remove the sheet that evaluated automatically by the API
                    }
                    updateDataChanged();//COMPLY
                    defineSpreadsheet(workbook);//COMPLY - reset the newTabVisible to false

        		},function (err) {
        			console.log('importFile ERROR!!!');
        	    },options);
         } else {
             target.data = this.result;
             callback(target);
         }
     };
     switch (type) {
         case "dataurl":
             reader.readAsDataURL(file);
             break;
         case "xlsx":
             reader.readAsArrayBuffer(file);
             break;
         default:
             reader.readAsText(file);
             break;
     }
 }
 
 function getExtension(fileName, detail) {
     var pos = fileName.lastIndexOf('.');
     if (detail) {
         detail.pos = pos;
         detail.name = fileName.substr(0, pos);
     }
     return pos === -1 ? "" : fileName.substring(pos + 1).toLowerCase();
 }