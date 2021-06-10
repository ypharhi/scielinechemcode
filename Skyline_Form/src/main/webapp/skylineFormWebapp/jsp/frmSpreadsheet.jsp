<!DOCTYPE html>
<html>
<head>
	
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	
	<!-- <link href="../CSS/font-awesome-4.7.0/css/font-awesome.min.css" rel="stylesheet" type="text/css" /> -->

   	<link href="../gc.spread.sheets/gc.spread.sheets.excel2013white.14.0.4.css" rel="stylesheet" type="text/css" />
	<link href="../gc.spread.sheets/gc.spread.sheets.designer.14.0.4.min.css" rel="stylesheet" type="text/css" media="all" />
	
	<script src="../gc.spread.sheets/gc.spread.sheets.all.14.0.4.min.js" type="text/javascript"></script>      
	<script src="../gc.spread.sheets/gc.spread.sheets.charts.14.0.4.min.js"></script>
	<script src="../gc.spread.sheets/gc.spread.sheets.shapes.14.0.4.min.js" type="text/javascript"></script>
	<script src="../gc.spread.sheets/gc.spread.sheets.print.14.0.4.min.js" type="text/javascript"></script>
	<script src="../gc.spread.sheets/gc.spread.sheets.barcode.14.0.4.min.js" type="text/javascript"></script>
	<script src="../gc.spread.sheets/gc.spread.sheets.pdf.14.0.4.min.js" type="text/javascript"></script>
	<script src="../gc.spread.sheets/gc.spread.pivot.pivottables.14.0.4.min.js"></script>
	<script src="../gc.spread.sheets/gc.spread.excelio.14.0.4.min.js" type="text/javascript"></script>	
	<script src="../gc.spread.sheets/gc.spread.sheets.designer.resource.en.14.0.4.min.js" type="text/javascript"></script>
	<script src="../gc.spread.sheets/gc.spread.sheets.designer.all.14.0.4.min.js" type="text/javascript"></script>  
	
	<script src="../deps/jquery-1.12.4.min.js"></script>
	<script src="../deps/jquery-ui.custom_new.min.js"></script>
	
	<link href="../CSS/font-awesome-4.7.0/css/font-awesome.min.css" rel="stylesheet" type="text/css" />
	
	<style type="text/css">
		
		#gc-designer-container {
			min-height: 96vh;
		}
	</style>
	
	<script type="text/javascript">

	var designer = [];
	var isToolBarDisplay=[];
	var isDisabled = [];//an array of all the excel objects when each cell mentions whether it's disabled or not

		function onLoadIframeSpreadsheet(domId,SpreadSheetsLicenseKey,SpreadSheetsDesignerLicenseKey){
			GC.Spread.Sheets.LicenseKey = SpreadSheetsLicenseKey;
			GC.Spread.Sheets.Designer.LicenseKey = SpreadSheetsDesignerLicenseKey;
			var config = getConfig(domId);
			designer[domId] = new GC.Spread.Sheets.Designer.Designer(document.getElementById("gc-designer-container"),config);
		}
		
		function onLoadSpreadsheetData(data,domId,isToolBarDisplay,isDisabled,SpreadSheetsLicenseKey,SpreadSheetsDesignerLicenseKey) {
			parent.console.log("----------------ON LOAD SPREADSHEET--------------");
			var jsonData = {};
			var hideRibbonPanel = "0";
			this.isToolBarDisplay[domId] = isToolBarDisplay;
			this.isDisabled[domId] = isDisabled;
			var workBook = designer[domId].getWorkbook();
			workBook.fromJSON(data);
			parent.console.log(data);
			setTimeout(function(){//sorrounded with timeout in order to ensure that the customizations defined after the spreadJs finished loding
				defineSpreadsheet(domId);//important! this operation should be executed after the fromJson operation.
			},100);
		}  
		
		function getConfig(domId) {
		  var config = GC.Spread.Sheets.Designer.DefaultConfig;
		  updateDefaultConfig(config,domId);
		  return config;
		}
		
		function updateDefaultConfig(config,domId) {
		  // remove fil menu
		  removeFileMenu(config);
		  updateHomeTab(config,domId);
		}
		
		function removeFileMenu(config) {
		  config.fileMenu = null;
		}
	
		function updateHomeTab(config,domId) {
		  var homeTab = config.ribbon.find(function(r){return r.id === 'home';});
		  // add save option
		  var customBtn = {
		    label: 'Print',
		    thumbnailClass: '',
		    commandGroup: {
		      children: [
		        {
		          direction: 'vertical',
		          commands: ['print']
		        }
		      ]
		    }
		  };
		  homeTab.buttonGroups = [customBtn, ...homeTab.buttonGroups];
		
		  // add custom command too
		  var commandMap = config.commandMap;
		  if (!config.commandMap) {
		    commandMap = config.commandMap = {};
		  }
		
		  commandMap['print'] = {
		    title: 'Print',
		    text: 'Print',
		    iconClass: 'fa fa-print fa-3x',
		    bigButton: true,
		    commandName: 'print',
		    subCommands: ['printActiveSheet','printSelection']
		  };
		  
		  commandMap['printActiveSheet'] = {
		    title: 'Only print the active sheet',
		    text: 'Print Active Sheet',
		    //iconClass: 'icon-pdf',
		    bigButton: false,
		    commandName: 'printActiveSheet',
		    execute: function(context, propName) {
		      	var workBook = designer[domId].getWorkbook();
		      	var sheet = workBook.getActiveSheet();
				var sheetColumnCount = sheet.getColumnCount();
				var sheetRowCount = sheet.getRowCount();
		      	var printInfo = new GC.Spread.Sheets.Print.PrintInfo();
		      	//printInfo.rowStart(0);
		        //printInfo.columnStart(0);
		        //printInfo.rowEnd();
		        //printInfo.columnEnd();
		       // printInfo.showGridLine(true);
		        //printInfo.showRowHeader(false);
		        sheet.printInfo(printInfo);
		      	workBook.print(workBook.getActiveSheetIndex());
		      	
		    }
		  };
		  
		  commandMap['printSelection'] = {
		    title: 'Only print the current selection',
		    text: 'Print Selection',
		    //iconClass: 'ribbon-thumbnail-cells',
		    bigButton: false,
		    commandName: 'printSelection',
		    execute: function(context, propName) {
		      	var workBook = designer[domId].getWorkbook();		      	
		     	var sheet = workBook.getActiveSheet();
		        var currSel = sheet.getSelections()[0];
		        var printInfo = new GC.Spread.Sheets.Print.PrintInfo();

		       // printInfo.bestFitColumns(true);

		        printInfo.rowStart(currSel.row);
		        printInfo.columnStart(currSel.col);
		        printInfo.rowEnd(currSel.row + currSel.rowCount - 1);
		        printInfo.columnEnd(currSel.col + currSel.colCount - 1);
		        printInfo.useMax(false);//whether to print only rows and columns that contain data.
		        sheet.printInfo(printInfo);
		        workBook.print(workBook.getActiveSheetIndex());
		    }
		  };
		}

		function onSpreadFocused(domId){
			onSpreadsheetChange(domId);
		}

		function defineSpreadsheet(domId){
			//define the visible tabs to 1 only and disable adding another tab
			var workBook = designer[domId].getWorkbook();
			workBook.setSheetCount(1);
			workBook.options.newTabVisible = false;
			/* var insertSheetIndex = null;
			$.each(workBook.contextMenu.menuData, function (p, v) {
			    if (v.name === 'gc.spread.contextMenu.insertSheet') { //disables adding tab by the right click
			    	insertSheetIndex = p;//removing the element in the p index in the contextMennu array
			    }
			});
			if(insertSheetIndex!=null){
				workBook.contextMenu.menuData.splice(insertSheetIndex, 1);
			} */
			workBook.contextMenu.menuData = workBook.contextMenu.menuData.filter(function(item) {
			    return item.name != 'gc.spread.contextMenu.insertSheet';
		    });
			
			//fits the width and the height of the column and the row to the text.
			workBook.bind(GC.Spread.Sheets.Events.CellChanged, function (e, args) {
				/* var width = args.sheet.getColumnWidth(args.col, GC.Spread.Sheets.SheetArea.viewport);
			    if(width == 62){
			    	args.sheet.autoFitColumn(args.col);
		    	} */
			//    args.sheet.getCell(args.row,args.col).wordWrap(true);
			    args.sheet.autoFitRow(args.row);
		 	});
		 	
		 	var fileMenuTemplate = GC.Spread.Sheets.Designer.getTemplate(
			  GC.Spread.Sheets.Designer.TemplateNames.FileMenuPanelTemplate
			);
			var listContainer =
			    fileMenuTemplate["content"][0]["children"][0]["children"][0]["children"][0][
			      "children"
			    ][1];
		    listContainer.items[2];
		 	
		 	//added a find command (ctrl+f). Register it to the showFindDialog function.
		 	workBook
		    .commandManager()
		    .register("showFindDialog", showFindDialog, 70, true, false, false, false);
					
			/* //hide specific tabs in the toolbar
			var hiddenTabs = getHiddenTabsBL();
			for(var i=0;i<hiddenTabs.length;i++){
				hideRibbonDesignTab(hiddenTabs[i]);//hide the 'file' tab
			}
			//hide the 'File' tab in the toolbar
			$(".gc-ribbon-bar>>ul>.fileButton").css("display", "none");
			 */
			//hide the toolbar if the element is being defined so
			if(isToolBarDisplay[domId]==false){
				$('.header').css('display','none');
				$('.vertical-splitter').css('display','none');
				$('.fill-spread-content').css('position','inherit');
			}
			
			//set the row & column count to the maximum(the column count is set according  to the data as long as the data contains more the 26 columns, else - define 26 cols)
			var sheet = workBook.getActiveSheet();
			var sheetCount = sheet.getColumnCount();
			if(sheetCount && sheetCount>=26){
				sheet.setColumnCount(sheet.getColumnCount());
			} else {
				sheet.setColumnCount(26);
			}
			sheet.setRowCount(3000);
			
			workBook.bind( GC.Spread.Sheets.Events.SelectionChanged , function (e, args) {
					onSpreadsheetChange(domId);
			});
			
			//set no active cell
			//workBook.focus(false); 
			sheet.setSelection([{row:-1,col:-1,rowCount:-1,colCount:-1}]);
			
			//set the f4 key to switch the formula reference between relative, absolute, and mixed when editing formulas
			workBook 
			    .commandManager()
			    .setShortcutKey("changeFormulaReference", 115, false, false, false, false);
			
			//add expand/compress
			var expandCompressElem = '<span style="right:1%;position:absolute;" class="expand_compress_li">'
									+'<a onclick="expandCompressSpreadIframe(this,\''+domId+'\');">'
									+'<i class="fa fa-expand"></i></a></span>';
			if($('.contentList.ribbon-navigation').has('.expand_compress_li').length ==0){
				$('.contentList.ribbon-navigation').append(expandCompressElem);
			}
			
		    disableSpreadsheet(domId);//disables the spreadsheet from being editable
// 		    spreadOnLoadBL(domId); // demo for spreadsheet develop
		}
		
		function expandCompressSpreadIframe(elem,domId){
			var $wrapperElement = parent.$('#'+domId) ;
			var $expandCompressItag = $(elem).find('i');
			 if($expandCompressItag.attr('class').indexOf('expand')!=-1){
				 var width = $(window.top).width();
				 var height = $(window.top).height();
				 //$iframe.resizable();
				 var zindex = parent.$('.floatingButtonsPanelContainer').css('z-index');//gets the z-index of the floating buttons in order that the spreadsheet be under them.
				 zindex = Number(zindex)-1;
				 $wrapperElement.css('position','absolute').css('left',0).css('z-index',zindex)
				 		.css('width',width).css('height',height).css('top',0).addClass('full-screen'); 
				 $expandCompressItag.removeClass('fa fa-expand').addClass('fa fa-compress');
			 } else if($expandCompressItag.attr('class').indexOf('compress')!=-1){
				 var width = $wrapperElement.attr('basicWidth');
				 var height = $wrapperElement.attr('basicHeight');
				 $wrapperElement.css('top','auto').css('position','relative').css('z-index','auto')
				 		.css('width',width).css('height',height).removeClass('full-screen');
				 $expandCompressItag.removeClass('fa fa-compress').addClass('fa fa-expand');
			 }

		}
		
		function showFindDialog() {
		  document
		    .querySelector(
		      ".ribbon-button-thumbnail-group-icon.ribbon-thumbnail-editing"
		    )
		    .click();
		  document.querySelector(".ribbon-button-item-icon.ribbon-button-find").click();
		  document
		    .querySelector(".ribbon-control-dropdown-find.gc-list-control-item-icon")
		    .click();
		  return true;
		}

		function disableSpreadsheet(domId,isDisabled){
			var $wraperElement = parent.$('#'+domId);
			if(isDisabled || isDisabled==undefined && this.isDisabled[domId]
			||$wraperElement.hasClass('generalDisabled')||$wraperElement.hasClass('disablePage')
			||$wraperElement.hasClass('disabledclass')||$wraperElement.hasClass('authorizationDisabled')){
				$('.ribbon-panel').css('pointer-events','none').css('cursor','not-allowed');//toolbar
				$('#vp_vp').css('pointer-events','none').css('cursor','not-allowed');//#ssvp_vp
				$('.gc-formulaBar').css('pointer-events','none').css('cursor','not-allowed');//formula bar
				/* var workBook = designer[domId].getWorkbook()
				workBook.options.allowContextMenu = false;
				var activeSheet = workBook.getActiveSheet();
				activeSheet.endEdit(true); */
			}else{
				$('.ribbon-panel').css('pointer-events','auto').css('cursor','auto');//toolbar
				$('#vp_vp').css('pointer-events','auto').css('cursor','auto');//#ssvp_vp
				$('.gc-formulaBar').css('pointer-events','auto').css('cursor','auto');//formula bar
				var workBook = designer[domId].getWorkbook()
				var activeSheet = workBook.getActiveSheet();
				// Set to be always in input mode.
			 	/*workBook.bind(GC.Spread.Sheets.Events.EnterCell, function (event, data)
			    {
			      var sheet = data.sheet;
			      activeSheet.startEdit(false);
			    });*/
			}
		}
		
		function getHiddenTabsBL(){
			var hiddenTabs = [];
			return hiddenTabs;
		}

		function hideRibbonDesignTab(domId,tabIndex) {
			if(tabIndex == 0){
				$(".gc-ribbon-bar>>ul>.fileButton").css("display", "none");
			}
			$(".gc-ribbon-bar>>ul").find("li:eq(" + tabIndex + ")").css("display", "none");
			$(".gc-ribbon-bar>>div>div.ribbon-panel-item:eq(" + tabIndex + ")").attr("aria-hidden", true);
		}

		function isSpreadsheetEmpty(domId) {
			var sheet = designer[domId].getWorkbook();//$('#ss').data('workbook');  
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

		function importExcel(domId,fileWrapper,buffer, file){
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
		       			var workbook = designer[domId].getWorkbook();
		       			var value = JSON.stringify(json);
		   				workbook.fromJSON(JSON.parse(value));
		       			if(json.sheetCount>1){
		                   	workbook.removeSheet(0);//COMPLY -remove the sheet that evaluated automatically by the API
		                   }
		                   onSpreadsheetChange(domId);//COMPLY
		                   defineSpreadsheet(domId);//COMPLY - reset the newTabVisible to false

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

		function onSpreadsheetChange(domID){
			//update the datachanged prop
			parent.prop.dataChanged = true;
			parent.$('#'+domID).attr('is_changed_flag','1');
		}

		// return output sheet (spread.getSheet(2)) as array of key and values
		function getValueFromOutputSheet(domId) {
			try {
				console.log("start getValueFromOutputSheet func");
				var ROW_MAX = 10;
				var workBook = designer[domId].getWorkbook();//$('#ss').data('workbook');
				workBook.getActiveSheet().endEdit(false);
			    var dataObj = {};
			    var fullObj = {};
			    var sheet = workBook.getSheetFromName('output');
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
			    var currSpreadConfig = workBook.toJSON();
			    fullObj["output"] = dataObj;
			    fullObj["excelFullData"] = currSpreadConfig;
			    console.log("end getValueFromOutputSheet func");
			    return JSON.stringify(fullObj);
			} catch(err) {
				console.log(err);	
			}
		}

		function clearSpreadsheet(domId){
			setValueToSpreadSheet(domId,'{}');
		}

		function setValueToSpreadSheet(domId,value){
			var workBook = designer[domId].getWorkbook();
			workBook.fromJSON(JSON.parse(value));
			defineSpreadsheet(domId);
			onSpreadsheetChange(domId);
		}
		
		//************* example code for spreadsheet develop ********************
		function spreadOnLoadBL(domId) {
			
			var workBook = designer[domId].getWorkbook();//$('#ss').data('workbook');
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
			
		}
		//**************** demo code END!

	</script>
</head>
<body unselectable="on" style="overflow: hidden">
	<div id="gc-designer-container"> </div> 
	
	<%-- <div>
		<input type="hidden" name="returnValue" id="returnValue" value=''>
        <input type="hidden" name="hideRibbonPanel" id="hideRibbonPanel" value='<%= request.getAttribute("HIDE_RIBBON_PANEL") %>'>
        <input type="hidden" name="parent" id="parent" value='<%= request.getAttribute("PARENT") %>'>
	</div> --%>
</body>
</html>