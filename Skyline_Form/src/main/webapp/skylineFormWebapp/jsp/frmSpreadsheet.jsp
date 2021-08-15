<!DOCTYPE html>
<html>
<head>
<!-- 	https://www.grapecity.com/spreadjs/docs/v14/online/API_documentation.html -->
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	
	<!-- <link href="../CSS/font-awesome-4.7.0/css/font-awesome.min.css" rel="stylesheet" type="text/css" /> -->

   	<link href="../gc.spread.sheets/gc.spread.sheets.excel2013white.14.1.4.css" rel="stylesheet" type="text/css" />
	<link href="../gc.spread.sheets/gc.spread.sheets.designer.14.1.4.min.css" rel="stylesheet" type="text/css" media="all" />
	
	<script src="../gc.spread.sheets/gc.spread.sheets.all.14.1.4.min.js" type="text/javascript"></script>      
	<script src="../gc.spread.sheets/gc.spread.sheets.charts.14.1.4.min.js"></script>
	<script src="../gc.spread.sheets/gc.spread.sheets.shapes.14.1.4.min.js" type="text/javascript"></script>
	<script src="../gc.spread.sheets/gc.spread.sheets.print.14.1.4.min.js" type="text/javascript"></script>
	<script src="../gc.spread.sheets/gc.spread.sheets.barcode.14.1.4.min.js" type="text/javascript"></script>
	<script src="../gc.spread.sheets/gc.spread.sheets.pdf.14.1.4.min.js" type="text/javascript"></script>
	<script src="../gc.spread.sheets/gc.spread.pivot.pivottables.14.1.4.min.js"></script>
	<script src="../gc.spread.sheets/gc.spread.excelio.14.1.4.min.js" type="text/javascript"></script>	
	<script src="../gc.spread.sheets/gc.spread.sheets.designer.resource.en.14.1.4.min.js" type="text/javascript"></script>
	<script src="../gc.spread.sheets/gc.spread.sheets.designer.all.14.1.4.min.js" type="text/javascript"></script>  
	
	<script src="../deps/jquery-1.12.4.min.js"></script>
	<script src="../deps/jquery-ui.custom_new.min.js"></script>
	
	<link href="../CSS/font-awesome-4.7.0/css/font-awesome.min.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="../deps/pako.js"></script>
	
	<style type="text/css">
		
		#gc-designer-container {
			min-height: 96vh;
		}
		
		.insert-text-box {
			background-image: url('data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAD4AAAAzCAYAAADPX7uaAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAJcEhZcwAAEnQAABJ0Ad5mH3gAAALmSURBVGhD7ZpfSFNRHMe/jhk6y00tsbLagw3RB3NQSEl/noxRRMSe+kPv0UMQ9RQIPRVBT71L1EsREcVICPJBhyRMfVDGWmVLUyzdpjYNh2u/47kYsdzdw/nd6NwPXM7vnN+543y5d/f7O7srC4fDOWhIWS6PjLXCIVvtsIXrhi1cN2zhumHaxx8Of5MR4K504kxzjexZw4toEunlrOwBl9p3yMgcpoXvuxeREdC8vQK9l1tkzxq6esYR/b4ie8Dn634ZmaMk4d3Hd2FkJoP+6CQqnt+UGWtYOXsHnc2NONDgQnffV7XCG6vLsZzNwe1cw62meZmxhtvxWqSzDlQ6yzC5sKpO+LXQBAYHB+HxeNDpb8WNIw0yYw13B2bQHxlDKpVCR0cH7ge8MmMO0091+uC22bcIlMcsF03QGmgttKZSRRO2j+uGJcKDTz7g1OP3smcN7MIfvJvF8HRGeDDFVsEufGgqgzqXE65yB/omFuUoP6zCPyV/oj+xCP9OV/6oEleexqyAVfjT8aRoAz4PzrWu1/o9I3Oi5YZVeCiWEtXfaZ9bHHTLv46nZZYXNuEvY2lRWh7zVssR4GSTG3OZrMhxwyacrjbR1bQh3Iifja1/BThhEz6QWEJ7/qF2eM9WOQIR0xY3Mv1DjvDBIpz8OrO6hoO7q+TIBke920SONh2csAgnvybfLrS5CbbUiNzQFO9VVy6cfJr8mqAy9c/jaighcjQn/GVJxBwoF2749F73FtEWwsj1xhdEy4Fy4eTT5Nevzu/f9OD2dKXCyZ/Jp6lELQbNobmPRnkqOaXCDX++2FYn2s0w5rz5yLNxUSqc/JlK1N+9+2/QHJpL53BsXJQJp1uWHlq0ITFLsLVWnEPFjmqUCb+Qv3XpoVXKD5NXDtWLc+hc1Si91f9lbOHFoBcKo/UnEFr1sdfVhaA10FpoTbS2UrFfIRVD65eGBlq9Jtb2jwH/G7ad6YYtXDds4bqhqXDgFyNKW4+1hn87AAAAAElFTkSuQmCC')
		}
	</style>
	
	<script type="text/javascript">

	var ENABLE_COMPRESS = true;
	
	var designer = [];
	var isToolBarDisplay=[];
	var isDisabled = [];//an array of all the excel objects when each cell mentions whether it's disabled or not
	var outputData = []; // contains the custom output fof the spreadsheet
	
		function onLoadIframeSpreadsheet(domId,SpreadSheetsLicenseKey,SpreadSheetsDesignerLicenseKey){
			GC.Spread.Sheets.LicenseKey = SpreadSheetsLicenseKey;
			GC.Spread.Sheets.Designer.LicenseKey = SpreadSheetsDesignerLicenseKey;
		}
		
		function onLoadSpreadsheetData(data,outputData,domId,isToolBarDisplay,isDisabled,SpreadSheetsLicenseKey,SpreadSheetsDesignerLicenseKey) {
			parent.console.log("----------------ON LOAD SPREADSHEET--------------");

			var config = getConfig(domId);
			designer[domId] = new GC.Spread.Sheets.Designer.Designer(document.getElementById("gc-designer-container"),config);
		
			var jsonData = {};
			var hideRibbonPanel = "0";
			this.isToolBarDisplay[domId] = isToolBarDisplay;
			this.isDisabled[domId] = isDisabled;
			this.outputData[domId] = outputData;
			var workBook = designer[domId].getWorkbook();
			var data_;
			if(typeof data.version !== 'undefined') { // if the data contins the version attr it is not compress
				data_ = data;
			} else {
				data_ = JSON.parse(pako.ungzip(data,{ to: 'string' }));
			}

			workBook.fromJSON(data_);
			//parent.console.log(data);
			customizeCommandMap(domId);
			setTimeout(function(){//sorrounded with timeout in order to ensure that the customizations defined after the spreadJs finished loding
				defineSpreadsheet(domId);//important! this operation should be executed after the fromJson operation.
			},100);
		}  
		
		function customizeCommandMap(domId){
			var workbook = designer[domId].getWorkbook();
			workbook.commandManager().register('insertTextBox', {
				canUndo: true,
				execute: function (workbook, options, isUndo) {
					var Commands = GC.Spread.Sheets.Commands;
					var sheet = workbook.getSheetFromName(options.sheetName);
					var x = options.x || 0, y = options.y || 0, w = options.w || 120, h = options.h || 50;
					if (isUndo) {
						Commands.undoTransaction(workbook, options);
						return true;
					} else {
						Commands.startTransaction(workbook, options);
						workbook.suspendPaint();
						var shape = sheet.shapes.add(
							'textBox' + new Date().valueOf(),
							GC.Spread.Sheets.Shapes.AutoShapeType.rectangle, x, y, w, h);
						var style = shape.style();
						style.fill.color = 'white';
						style.line.color = 'rgb(212, 212, 212)';
						style.textEffect.color = 'black';
						shape.style(style);
						shape.isSelected(true);
						workbook.resumePaint();
						Commands.endTransaction(workbook, options);
						return true;
					}
				}
			});
		}
		
		function getConfig(domId) {
		  var config = GC.Spread.Sheets.Designer.DefaultConfig;
		  updateDefaultConfig(config,domId);
		  return config;
		}
		
		function updateDefaultConfig(config,domId) {
		  // remove fil menu
		  removeFileMenu(config);
		  updateInsertTab(config,domId);
		  updateHomeTab(config,domId);
		}
		
		function removeFileMenu(config) {
		  config.fileMenu = null;
		}
	
		function updateInsertTab(config,domId){
			config.commandMap = {
					insertTextShape: {
						title: "Text Box",
						text: "Text Box",
						iconClass: "insert-text-box",
						bigButton: "true",
						commandName: "insertTextShape",
						execute: function (context, propertyName) {
							var workbook = context.getWorkbook();
							var sheet = workbook.getActiveSheet();
							var sel = sheet.getSelections()[0];
							var rect = sheet.getCellRect(sel.row, sel.col);
							workbook.commandManager().execute({ cmd: 'insertTextBox', sheetName: sheet.name(), x: rect.x, y: rect.y, w: 150, h: 50 });
						}
					}
				};
				config.ribbon[1].buttonGroups[4].commandGroup.commands.push("insertTextShape");
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
			if(parent.isSingleSheetOnly(parent.$('#formCode').val(),domId)){
				//workBook.setSheetCount(1);
				var sheetCount = workBook.getSheetCount();
				for(var i=1;i<sheetCount;i++){
					workBook.sheets[i].visible(false);
				}
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
			} else {
				var sheetCount = workBook.getSheetCount();
				for(var i=1;i<sheetCount;i++){
					workBook.sheets[i].visible(true);
				}
			}
			
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
			var sheetRowCount = sheet.getRowCount();
			if(sheetRowCount && sheetRowCount>=300){
				sheet.setRowCount(sheetRowCount);
			} else {
				sheet.setRowCount(300);
			}
			
			workBook.bind( GC.Spread.Sheets.Events.SelectionChanged , function (e, args) {
					onSpreadsheetChange(domId);
			});
			

			sheet.bind(GC.Spread.Sheets.Events.ColumnChanging, function (e, info) {
			    parent.onColumnChanging(parent.$('#formCode').val(),domId,e,info);
			});
			
			sheet.bind(GC.Spread.Sheets.Events.RowChanging, function (e, info) {
			    parent.onRowChanging(parent.$('#formCode').val(),domId,e,info);
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
			
			let oldEl = GC.Spread.Sheets.CellTypes.Text.prototype.createEditorElement;

			GC.Spread.Sheets.CellTypes.Text.prototype.createEditorElement = function () {
			  let el = oldEl.apply(this, arguments);

			  el.addEventListener("keydown", function(e) {
			    parent.onKeyDown(parent.$('#formCode').val(),domId,e,designer);
			  });
			  return el;
			};
			
		    disableSpreadsheet(domId);//disables the spreadsheet from being editable
 		    parent.spreadOnLoadBL(parent.$('#formCode').val(),domId,designer,outputData[domId]); // demo for spreadsheet develop
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
			    fullObj["output"] = parent.getOutputValueBL(parent.$('#formCode').val(),domId,designer);
			    var fullData_;
			    var currSpreadConfig = workBook.toJSON({includeBindingSource:true});
			    if(ENABLE_COMPRESS) {
			    	var currSpreadConfigSfy = JSON.stringify(currSpreadConfig);
				    var currSpreadConfigZip = pako.gzip(currSpreadConfigSfy,{ to: 'string' });
				    var currSpreadConfigUnZip = pako.ungzip(currSpreadConfigZip,{ to: 'string' });
				    if(currSpreadConfigSfy === currSpreadConfigUnZip) {
				    	fullData_ = currSpreadConfigZip;
				    } else {
				    	fullData_ = currSpreadConfig;
				    }
			    } else {
			    	fullData_ = currSpreadConfig;
			    }
			   
			    fullObj["excelFullData"] = fullData_;
			    fullObj["validationMessage"] = parent.getValidationMessage(parent.$('#formCode').val(),domId,designer);
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