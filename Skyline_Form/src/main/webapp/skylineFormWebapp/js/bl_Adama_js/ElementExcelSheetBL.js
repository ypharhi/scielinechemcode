//default values are of version V1
var _materialLocation = {
		x:4,
		y:5
};
var _unknownMaterialLocation = {
		x:4,
		y:1
};
var _sampleLocation = {
		x:0,
		y:6
};
var _resultTypeLocation = {
		x:4,
		y:0
};
var _rtLocation = {
		x:4,
		y:3
};
var _massLocation = {
		x:4,
		y:2
};
var _resultCommentLocation = {
		x:3,
		y:5
}
var _uomLocation = {
		x:4,
		y:4
}

function initializeFields(version){
	if(version == 'V1'){
		return;
	}
}

function spreadOnLoadBL(formCode,domId,designer,outputData) {
	if(formCode == 'ExperimentAn' && domId == 'spreadsheetResults'){
		getComponentList().then(function(componentList){
		    //1. get the expected material list& result types& samples and set them in the hidden sheets 'Materials,'Result Types','Samples'
			// When the data in the hidden sheets got filled, the lists in the main sheet should be ready with data too.
			var workBook = designer[domId].getWorkbook();
		    
			//gets the version of the spreadsheet results template and makes the relevant code
			var sheet = workBook.getSheetFromName('Version');
			var version = sheet == null ? 'V1': sheet.getValue(0,0);
			initializeFields(version);
			if(version == 'V1'){
				
			    var sheet = workBook.getSheetFromName('Materials'); //spread.getActiveSheet();
				sheet.autoGenerateColumns = false;
				sheet.setDataSource(componentList['Materials']);
				sheet.bindColumn(0, "NAME");
				sheet.bindColumn(1, "ID");
				
				var sheet = workBook.getSheetFromName('Samples'); //spread.getActiveSheet();
				sheet.autoGenerateColumns = false;
				sheet.setDataSource(componentList['Samples']);
				sheet.bindColumn(0, "NAME");
				sheet.bindColumn(1, "ID");
				sheet.bindColumn(2, "DESCRIPTION");
				sheet.bindColumn(3, "COMMENTS");
				
				var sheet = workBook.getSheetFromName('ResultTypes'); //spread.getActiveSheet();
				sheet.autoGenerateColumns = false;
				sheet.setDataSource(componentList['ResultTypes']);
				sheet.bindColumn(0, "NAME");
				sheet.bindColumn(1, "ID");
				
				
				var sheet = workBook.getSheetFromName('Uom'); //spread.getActiveSheet();
				sheet.autoGenerateColumns = false;
				sheet.setDataSource(componentList['Uom']);
				sheet.bindColumn(0, "NAME");
				sheet.bindColumn(1, "ID");
				
				var sheet = workBook.getSheet(0);
				
				//sheet.getRange("F6").locked(false);
				//sheet.getRange("E:KR").locked(false);
				//protect the sheet from deleting rows
				//sheet.options.isProtected = true;
				/*sheet.options.protectionOptions.allowDragInsertRows = true;
				sheet.options.protectionOptions.allowDragInsertColumns = true;
				sheet.options.protectionOptions.allowInsertRows = true;
				sheet.options.protectionOptions.allowInsertColumns = true;
				sheet.options.protectionOptions.allowDeleteRows = false;
				sheet.options.protectionOptions.allowDeleteColumns = true;
				sheet.options.protectionOptions.allowSelectLockedCells = true;
				sheet.options.protectionOptions.allowSelectUnlockedCells = true;
				sheet.options.protectionOptions.allowSort = true;
				sheet.options.protectionOptions.allowFilter = true;
				sheet.options.protectionOptions.allowEditObjects = true;
				sheet.options.protectionOptions.allowResizeRows = true;
				sheet.options.protectionOptions.allowResizeColumns = true;
				sheet.options.protectionOptions.allowOutlineRows = true;
				sheet.options.protectionOptions.allowOutlineColumns = true;*/
				
				//2. Add the tested components to the spreadsheet results(if it was not selected manually in the excel)i.e select the, in the main sheet
				var rowCount = sheet.getRowCount();
			    var columnCount = sheet.getColumnCount();
				var i=0;
				var j=_materialLocation.x;
				var currentMaterialList = [];
				for(i=0;i<columnCount-j;i++){
					currentMaterialList[i] = sheet.getValue(_materialLocation.y,j+i);
				}
				var firstEmptyCol = _materialLocation.x;
				for(;j<columnCount;j++){
					/*for(i=0;i<rowCount;i++){
						var val = sheet.getValue(i,j);
						if(val != null){
							break;
						}
					}
					if(i==rowCount){//checked all the lines and found them empty
						firstEmptyCol = j;
						break;
					}*/
					var material = sheet.getValue(_materialLocation.y,j);
					var unknown_material = sheet.getValue(_unknownMaterialLocation.y,j);
					if(material == null && unknown_material ==null){
						firstEmptyCol = j;
						break;
					}
				}
				for(var index in componentList['TestedComponents']){
					var name = componentList['TestedComponents'][index]['COMPONENTNAME'];
					var rtName = componentList['TestedComponents'][index]['RT'];
					var typeName = componentList['TestedComponents'][index]['TESTEDCOMPTYPENAME'];
					if(currentMaterialList.indexOf(name)==-1){
						//add the material to the materials in the result spreadsheet
						sheet.setValue(_materialLocation.y,firstEmptyCol,name);
						sheet.setValue(_rtLocation.y,firstEmptyCol,rtName);
						sheet.setValue(_resultTypeLocation.y,firstEmptyCol,typeName);
						firstEmptyCol++;
					}
				}
				
				//3. Add the samples from the sample select to the spreadsheet results
				var j=_sampleLocation.y;
				var currentSampleList = [];
				for(var i=0;i<rowCount-j;i++){
					currentSampleList[i] = sheet.getValue(j+i,0);
				}
				
				var sampleSelectList = [];
				for(var index in componentList['Samples']){
					var name = componentList['Samples'][index]['NAME'];
					if(name == 'NA'){
						continue;
					}
					sampleSelectList.push(name);
				}
				
				//4. delete the rows of the samples that are not in the sample select anymore(were removed by the user)
				var commonSamples = currentSampleList.filter(function(val){
					return sampleSelectList.indexOf(val)!==-1;
				});
				for(var i = _sampleLocation.y;i<rowCount;i++){
					var val = sheet.getValue(i,_sampleLocation.x);
					if(val!=null && commonSamples.indexOf(val)==-1){//check whether the sample was deleted from the sample select
						sheet.deleteRows(i,1);
						--i;//decrease the index in order to check again the row that now contains the content of the original following row(before deleting this one)
						currentSampleList = currentSampleList.slice(currentSampleList.indexOf(val)+1);
					}
				}
				var firstEmptyRow = _sampleLocation.y;
				for(var i=_sampleLocation.y;i<rowCount;i++){
					var val = sheet.getValue(i,_sampleLocation.x);
					if(val == null){
						firstEmptyRow = i;
						break;
					}
				}
				for(var item in componentList['Samples']){
					var name = componentList['Samples'][item]['NAME'];
					if(name == 'NA'){
						continue;
					}
					if(currentSampleList.indexOf(name)==-1){
						//add the material to the materials in the result spreadsheet
						sheet.setValue(firstEmptyRow++,_sampleLocation.x,name);
					}
				}
				
				var statusName = parent.$('#STATUS_ID option:selected').text();
				/*
				//delete the manual materials and select them in the material list
				
				var columnCount = sheet.getColumnCount();
				if(statusName == 'Completed' || statusName == 'Approved'){
					for(var j = _unknownMaterialLocation.x; j < columnCount; j++){
						var unknownMaterial = sheet.getValue(_unknownMaterialLocation.y,j);
						if(unknownMaterial!=null){
							sheet.setValue(_materialLocation.y,j,unknownMaterial);
							sheet.setValue(_unknownMaterialLocation.y,j,'');
						}
					}
				}*/
				
				//5. Update the selected materials in the results sheet with the actual name in case it was change in the system
				var sheetMaterialId = workBook.getSheetFromName("IdLookup");
				var sheetActualMaterialId = workBook.getSheetFromName("ActualMaterialId");
			    var columnCount = sheetMaterialId.getColumnCount();
			    for (var j = _materialLocation.x; j < columnCount; j++) {
			    	var idValLookup = sheetMaterialId.getValue(_materialLocation.y,j);
			    	var idValLastSave = sheetActualMaterialId.getValue(j,0);
			    	if(idValLookup != idValLastSave && idValLastSave!=-1 && idValLastSave!=""){//if the lookup is not identical to the last saved id, it means that the name was changed.
			    		var sheetMaterial = workBook.getSheetFromName("Materials");
			    		var rowCount = sheetMaterial.getRowCount();
			    		for(var row = 0;row<rowCount; row++){
			    			var materialId = sheetMaterial.getValue(row,1);
			    			if(materialId == idValLastSave){
			    				var actualMaterialName = sheetMaterial.getValue(row,0);
			    				sheet.setValue(_materialLocation.y,j,actualMaterialName);
			    				break;
			    			}
			    		}
			    	} else if(idValLastSave == -1 && sheet.getValue(_unknownMaterialLocation.y,j)!=null){
			    		//if the last saved material was an unknown-> then on completed status it gets an ID.
			    		//Now, Onload of the page, the new material_id is already stored on the DB, and here we get from the output.
			    		if(statusName == 'Completed' || statusName == 'Approved'){
			    			var originUnknownMaterial = sheet.getValue(_unknownMaterialLocation.y,j);
				    		var material_id = '-1';
			    			if(outputData!='{}'){
				    			var outputArray = outputData[0];
				    			outputArray.forEach(function(element){
				    				if(element["Unknown Materials"]==originUnknownMaterial){
				    					material_id = element["material_id"];
				    					return;
				    				}
				    			});
				    		}
			    			var sheetMaterial = workBook.getSheetFromName("Materials");
				    		var rowCount = sheetMaterial.getRowCount();
			    			for(var row = 0;row<rowCount; row++){
				    			var materialId = sheetMaterial.getValue(row,1);
				    			if(materialId == material_id){
				    				var actualMaterialName = sheetMaterial.getValue(row,0);
				    				sheet.setValue(_materialLocation.y,j,actualMaterialName);
				    				sheet.setValue(_unknownMaterialLocation.y,j,'');
				    				break;
				    			}
			    			}
			    		}
			    	}
			    }
			}//end of if version
		});
		onSpreadFocused(domId);
	}
	/* var workBook = designer[domId].getWorkbook();//$('#ss').data('workbook');
			var sheet = workBook.getSheetFromName('Sheet1'); //spread.getActiveSheet();
			
			sheet.autoGenerateColumns = false;
			
			//**** set cells (better use binding) ->
// 			for (var i = 1; i < 1000; i++) {
// 				sheet.setValue(i,0,'num' + i);
// 				sheet.setValue(i,1,i);
// 		    }

			//**** using binding (as an example of getting the material from the inventory into the A B columns in input sheet - that will be used for the list in the main sheet)->
// 			var sampleTable =
// 				   [
// 				    {"ID":1, "Text":"num1"},
// 				    {"ID":2, "Text":"num2"},
// 				    {"ID":3, "Text":"num3"},
// 				    {"ID":4, "Text":"num4"},
// 				    {"ID":5, "Text":"num5"}
// 				   ];
			var sampleTable = [];
			for (var i = 1; i < 1000; i++) {
				sampleTable.push({"ID":i, "Text":"num" + i});
		    }

			sheet.setDataSource(sampleTable);
			
			sheet.bindColumn(0, "Text");
			sheet.bindColumn(1, "ID");
			// **** end binding
			
			
			//this works  -> example for setting the lists with materials from the analytical experiment (num4,num44,num444) - in row 3 we have already made lists in the excel that using the above binding data.
// 			alert("the id of the first material: " + sheet.getValue(2,7)); -- row 2 contains a lookup that map the name to the id
			
			sheet.setValue(3,7,'num4');
			sheet.setValue(3,8,'num44');
			sheet.setValue(3,9,'num444');
			
// 			alert("the id of the first material (after change): " + sheet.getValue(2,7));

			// the excel json used in this example (with the lookup and the lists)
// 			{"output":{},"excelFullData":{"version":"14.0.4","newTabVisible":false,"customList":[],"sheets":{"Sheet1":{"name":"Sheet1","isSelected":true,"rowCount":999,"columnCount":26,"activeRow":10,"activeCol":18,"theme":"Office","data":{"dataTable":{"2":{"7":{"value":{"_calcError":"#N/A","_code":42},"style":{"backColor":"#FFFF00","font":"11pt Calibri"},"formula":"VLOOKUP(H4,A1:B10000,2,FALSE)"},"8":{"value":{"_calcError":"#N/A","_code":42},"style":{"backColor":"#FFFF00","hAlign":3,"vAlign":0,"font":"11pt Calibri","themeFont":"Body","imeMode":1},"formula":"VLOOKUP(I4,A1:B10000,2,FALSE)"},"9":{"value":{"_calcError":"#N/A","_code":42},"style":{"backColor":"#FFFF00","hAlign":3,"vAlign":0,"font":"11pt Calibri","themeFont":"Body","imeMode":1},"formula":"VLOOKUP(J4,A1:B10000,2,FALSE)"},"10":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"11":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"12":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"13":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"14":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"15":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"16":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"17":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"18":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"19":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"20":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"21":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"22":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}}},"3":{"0":{"style":{"vAlign":0,"font":"14.6667px Calibri"}},"1":{"style":{"vAlign":0,"font":"14.6667px Calibri"}},"7":{"style":{"backColor":"#92D050","font":"11pt Calibri"}},"8":{"style":{"backColor":"#92D050","hAlign":3,"vAlign":0,"font":"11pt Calibri","themeFont":"Body","imeMode":1}},"9":{"style":{"backColor":"#92D050","hAlign":3,"vAlign":0,"font":"11pt Calibri","themeFont":"Body","imeMode":1}},"10":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"11":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"12":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"13":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"14":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"15":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"16":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"17":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"18":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"19":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"20":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"21":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"22":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"23":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"24":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}},"25":{"style":{"hAlign":3,"vAlign":0,"themeFont":"Body","imeMode":1}}},"4":{"0":{"style":{"vAlign":0,"font":"14.6667px Calibri"}},"1":{"style":{"vAlign":0,"font":"14.6667px Calibri"}},"7":{"value":1},"8":{"value":11},"9":{"value":4}},"5":{"7":{"value":2},"8":{"value":22},"9":{"value":5}},"6":{"7":{"value":3},"8":{"value":33},"9":{"value":3}}},"defaultDataNode":{"style":{"themeFont":"Body"}}},"rowHeaderData":{"defaultDataNode":{"style":{"themeFont":"Body"}}},"colHeaderData":{"defaultDataNode":{"style":{"themeFont":"Body"}}},"rows":[null,{"size":21},{"size":21},{"size":21},{"size":21},{"size":21},{"size":21}],"columns":[{"name":"Text"},{"name":"ID"},null,null,null,null,null,{"size":113}],"leftCellIndex":0,"topCellIndex":0,"selections":{"0":{"row":10,"rowCount":1,"col":18,"colCount":1},"length":1},"autoGenerateColumns":false,"rowOutlines":{"items":[]},"columnOutlines":{"items":[]},"validations":[{"type":3,"condition":{"conType":12,"ignoreBlank":true,"expected":"","formula":"Sheet1!$A$4:$B$6","ranges":[{"row":3,"rowCount":1,"col":21,"colCount":1},{"row":3,"rowCount":1,"col":22,"colCount":1},{"row":3,"rowCount":1,"col":23,"colCount":1},{"row":3,"rowCount":1,"col":24,"colCount":1},{"row":3,"rowCount":1,"col":25,"colCount":1}]},"ranges":"V4, W4, X4, Y4, Z4","highlightStyle":"{\"type\":0,\"color\":\"#FF0000\"}"},{"ignoreBlank":false,"type":3,"condition":{"conType":12,"expected":"","formula":"Sheet1!$A$1:$B$10000","ranges":[{"row":3,"rowCount":1,"col":7,"colCount":1},{"row":3,"rowCount":1,"col":8,"colCount":1},{"row":3,"rowCount":1,"col":10,"colCount":1},{"row":3,"rowCount":1,"col":11,"colCount":1},{"row":3,"rowCount":1,"col":12,"colCount":1},{"row":3,"rowCount":1,"col":13,"colCount":1},{"row":3,"rowCount":1,"col":14,"colCount":1},{"row":3,"rowCount":1,"col":15,"colCount":1},{"row":3,"rowCount":1,"col":16,"colCount":1},{"row":3,"rowCount":1,"col":17,"colCount":1},{"row":3,"rowCount":1,"col":18,"colCount":1},{"row":3,"rowCount":1,"col":19,"colCount":1},{"row":3,"rowCount":1,"col":20,"colCount":1}]},"ranges":"H4, I4, K4, L4, M4, N4, O4, P4, Q4, R4, S4, T4, U4","highlightStyle":"{\"type\":0,\"color\":\"#FF0000\"}"},{"ignoreBlank":false,"type":3,"condition":{"conType":12,"expected":"","formula":"Sheet1!$A$1:$A$10000","ranges":[{"row":3,"rowCount":1,"col":9,"colCount":1}]},"ranges":"J4","highlightStyle":"{\"type\":0,\"color\":\"#FF0000\"}"}],"cellStates":{},"outlineColumnOptions":{},"autoMergeRangeInfos":[],"printInfo":{"paperSize":{"width":850,"height":1100,"kind":1}},"index":0}},"pivotCaches":{}}}
			 */
}
//**************** demo code END!

function getComponentList(){
	var promise = new Promise(function(resolve,reject){
		var allData = [];// getformDataNoCallBack(1);

		var urlParam = "?formId=" + parent.$('#formId').val() + "&formCode="
				+ parent.$('#formCode').val() + "&userId=" + parent.$('#userId').val();

		var data_ = JSON.stringify({
			action : "getExcelComponentList",
			data : allData,
			errorMsg : ""
		});

		parent.$.ajax({
			type : 'POST',
			data : data_,
			url : "./getExcelComponentList.request" + urlParam + "&stateKey=" + parent.$('#stateKey').val(),
			contentType : 'application/json',
			dataType : 'json',
			success : function(obj) {
				var componentList = JSON.parse(obj.data[0].val);
				resolve(componentList);
			},
			error : parent.handleAjaxError
		});
	});
	return promise;
}

function isSingleSheetOnly(formCode,domId){
	if(/*formCode == 'ExperimentAn' && domId == 'spreadsheetResults'
		|| formCode == 'ExperimentFor' && domId == 'spreadsheetExcel'
		||*/ formCode == 'SysConfExcelData' && domId == 'ExcelData'
			){
		return false;
	}
	return true;
}

function getOutputValueBL(formCode,domId,designer){
	
	var returnVal = {};
	/* var sheet = workBook.getSheetFromName('output');
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
    } */
    if(formCode == 'ExperimentAn' && domId == 'spreadsheetResults'){
    	console.log("START GET OUTPUT!!!!!!")
    	console.time("SPREAD_OUTPUT");
    	var workBook = designer[domId].getWorkbook();
    	var sheet = workBook.getSheetFromName('Version');
		var version = sheet == null ? 'V1': sheet.getValue(0,0);
		initializeFields(version);
		if(version == 'V1'){
			console.time("SPREAD_IDLOOKUP");
		    var sheetMaterialId = workBook.getSheetFromName("IdLookup");
		    var sheetActualMaterialId = workBook.getSheetFromName("ActualMaterialId");
		    var columnCount = sheetMaterialId.getColumnCount();
		    var array = [];
		    for (var j = 0; j < columnCount-_materialLocation.x; j++) {
		    	var idVal = sheetMaterialId.getValue(_materialLocation.y,j);
		    	array.push({"ID":idVal});
		    //	sheetMaterialId.setValue(_materialLocation.y+1,j,idVal);
		    }
		    sheetActualMaterialId.autoGenerateColumns = false;
		    sheetActualMaterialId.setDataSource(array);
		    sheetActualMaterialId.bindColumn(0,"ID");
		    console.timeEnd("SPREAD_IDLOOKUP");
		   
		    console.time("SPREAD_GETOUTPUT");
		    var sheet = workBook.getSheet(0);
		    var dataArray = [];
		    //_resultTypeLocation
		    var rowCount = sheet.getRowCount();
		    var columnCount = sheet.getColumnCount();
		    for (var j = _resultTypeLocation.x; j < columnCount; j++) {
		    	var dataObj = {};
		    	var currFieldID = "Results Type";
		        var currFieldValue = sheet.getValue(_resultTypeLocation.y, j);
		        dataObj[currFieldID] = currFieldValue;
		        var currFieldID = "Unknown Materials";
		        var currFieldValue = sheet.getValue(_unknownMaterialLocation.y, j);
		        dataObj[currFieldID] = currFieldValue;
		        var currFieldID = "Mass";
		        var currFieldValue = sheet.getValue(_massLocation.y, j);
		        dataObj[currFieldID] = currFieldValue;
		        var currFieldID = "RT";
		        var currFieldValue = sheet.getValue(_rtLocation.y, j);
		        dataObj[currFieldID] = currFieldValue;
		        var currFieldID = "Material";
		        var currFieldValue = sheet.getValue(_materialLocation.y, j);
		        dataObj[currFieldID] = currFieldValue;
		        var currFieldID = "Uom";
		        var currFieldValue = sheet.getValue(_uomLocation.y, j);
		        dataObj[currFieldID] = currFieldValue;
		    	if((dataObj['Material'] == null || dataObj['Material'] == 'null') 
		    			&& (dataObj['Unknown Materials'] == null || dataObj['Unknown Materials'] == 'null')){
		    		continue;
		    	}
		    	var idVal = sheetMaterialId.getValue(_materialLocation.y,j);
		    	dataObj['material_id'] = idVal;
		    	for(var i = _sampleLocation.y; i<rowCount; i++){
		    		var fullDataObj = Object.assign({}, dataObj);
		    		fullDataObj['Sample'] = sheet.getValue(i , _sampleLocation.x);
		    		if(fullDataObj['Sample'] == null || fullDataObj['Sample'] == 'null'){
		    			continue;
		    		}
		    		fullDataObj['value'] = sheet.getValue(i,j);
		    		fullDataObj['comment'] = sheet.getValue(i,_resultCommentLocation.x);
		    		dataArray.push(fullDataObj);
		    	}
		    }
		    console.timeEnd("SPREAD_GETOUTPUT");
		    returnVal[0] = dataArray;
		}
		console.timeEnd("SPREAD_OUTPUT");
    }
    return returnVal;
}

function onColumnChanging(formCode,domId,e,info){
	 if(formCode == 'ExperimentAn' && domId == 'spreadsheetResults'){
		 if(info.col>=0 && info.col<=_materialLocation.x){
			 displayAlertDialog('Data may be disrupted.</br> Please redo the last operation');
		 }
	 }
}

function onRowChanging(formCode,domId,e,info){
	 if(formCode == 'ExperimentAn' && domId == 'spreadsheetResults'){
		 if(info.row>=0 && info.row<=_materialLocation.y){
			 displayAlertDialog('Data may be disrupted.</br> Please redo the last operation');
		 }
	 }
}

function getValidationMessage(formCode,domId,designer){
	var errMessage = "";
	var workBook = designer[domId].getWorkbook();
    
	//gets the version of the spreadsheet results template and makes the relevant code
	var sheet = workBook.getSheetFromName('Version');
	var version = sheet == null ? 'V1': sheet.getValue(0,0);
	if(formCode == 'ExperimentAn' && domId == 'spreadsheetResults'){
		sheet = workBook.getSheet(0);
		var titleArr = [];
		var expectedTitles = ['Results Type','Unknown Materials','Mass','RT','Uom','Sample No/Materials'];
		for(var i = 0; i<_sampleLocation.y; i++){
    		titleArr.push(sheet.getValue(i , _sampleLocation.x));
		}
		var missingTitles = expectedTitles.filter(function(val){
			return titleArr.indexOf(val)==-1;
		});
		if(missingTitles.length!=0){
			errMessage = 'The following lines are missing:</br>'+missingTitles.toString()+'</br>or Some unexpected rows were added.</br>Please re-arrange the data';
		}
		if(expectedTitles.length<titleArr.length){
			errMessage = 'Some unexpected lines were added.</br>Please delete them.'
		}
		//check the columns
		var titleArr = [];
		var expectedTitles = ['Sample No/Materials','Sample description','Sample Comments','Results Comments'];
		for(var i = 0; i<_materialLocation.x; i++){
    		titleArr.push(sheet.getValue(_materialLocation.y,i));
		}
		var missingTitles = expectedTitles.filter(function(val){
			return titleArr.indexOf(val)==-1;
		});
		if(missingTitles.length!=0){
			errMessage = 'The following columns are missing:</br>'+missingTitles.toString()+'</br>or Some unexpected columns were added.</br>Please re-arrange the data';
		}
		if(expectedTitles.length<titleArr.length){
			errMessage = 'Some unexpected columns were added.</br>Please delete them.'
		}
	}
	return errMessage;
}