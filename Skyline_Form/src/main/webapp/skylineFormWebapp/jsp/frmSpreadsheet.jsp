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

	/* window.onload = function(e){
			GC.Spread.Sheets.Designer.LicenseKey = "Designer-523526556479995#B0A4G8VkREd5RSp6dJllSiFlWSZENQxkcPBFaJ9WY4QUR6U4RCJnbrYXdvNlUzhHa0lVM78ETxxUboZnUHZTUlBDasFndadFbFtkQqFDNlVlQnRWU5V6UXFlc7R6UBNFbiFXUMZjbzsEW7ZzcUJ6a43mNyVUWnVkUulUWyFDWC34YhlDW4U7RMhUdQVFdURkTDZmS6lWNoBDNWx4aspkUCl4MvY5LYFGTYdjMKFHezYTaHh7Qml5Tz3kb8NHNa5GZTdDTttEbv9WbsBFMIVFWttWSORDRRxmdDNDdktSdnRGaIZVaSF4R8gmI0IyUiwiIFdTMDNzN9EjI0ICSiwiM5QTOxcjMzkTM0IicfJye&Qf35VfiUURJZlI0IyQiwiI4EjL6BCITpEIkFWZyB7UiojIOJyebpjIkJHUiwiI6AjNxcDMgQTMzATMyAjMiojI4J7QiwiIw8CMuAjLw2icl96ZpNXZkJiOiMXbEJCLikHbw56bDJiOiEmTDJCLiUTO9kzN4YTN5YjM5MjM5IiOiQWSiwSflVnc4pjIyNHZisnOiwmbBJye0ICRiwiI34TQ8t4bWVFMRdXWDpUVh5WMihmZzoVQLJUWr4UWvIzKp3SOOllcYd4VKxWWqJmSygTMoB5M4RUZjhTV63SZrZ7SJZXTS3Cewo7KGlVbyVmQG9EVDlzMrUTZ9UkSt3kS0pmYWNnWU5mUmFXOsyWTr";
			var spread;
			var jsonData = {};
			var hideRibbonPanel = "0";//document.getElementById("hideRibbonPanel").value;
			designer = new GC.Spread.Sheets.Designer.Designer(document.getElementById("gc-designer-container"));
		}; */
		function onLoadSpreadsheet(data,domId,isToolBarDisplay,isDisabled,SpreadSheetsLicenseKey,SpreadSheetsDesignerLicenseKey) {
			GC.Spread.Sheets.LicenseKey = SpreadSheetsLicenseKey;
			GC.Spread.Sheets.Designer.LicenseKey = SpreadSheetsDesignerLicenseKey;
			
			var jsonData = {};
			var hideRibbonPanel = "0";
			designer[domId] = new GC.Spread.Sheets.Designer.Designer(document.getElementById("gc-designer-container"));
			this.isToolBarDisplay[domId] = isToolBarDisplay;
			this.isDisabled[domId] = isDisabled;
			var workBook = designer[domId].getWorkbook();
			workBook.fromJSON(data);
			defineSpreadsheet(domId);//important! this operation should be executed after the fromJson operation.
		}  


		function onSpreadFocused(domId){
			onSpreadsheetChange(domId);
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

		function defineSpreadsheet(domId){
			//define the visible tabs to 1 only and disable adding another tab
			var workBook = designer[domId].getWorkbook();
			workBook.setSheetCount(1);
			workBook.options.newTabVisible = false;
			var insertSheetIndex = null;
			$.each(workBook.contextMenu.menuData, function (p, v) {
			    if (v.name === 'gc.spread.contextMenu.insertSheet') { //disables adding tab by the right click
			    	insertSheetIndex = p;//removing the element in the p index in the contextMennu array
			    }
			});
			if(insertSheetIndex!=null){
				workBook.contextMenu.menuData.splice(insertSheetIndex, 1);
			}
			workBook.bind( GC.Spread.Sheets.Events.SelectionChanged , function (e, args) {
					onSpreadsheetChange(domId);
			});
			
			//hide specific tabs in the toolbar
			var hiddenTabs = getHiddenTabsBL();
			for(var i=0;i<hiddenTabs.length;i++){
				hideRibbonDesignTab(hiddenTabs[i]);//hide the 'file' tab
			}
			//hide the 'File' tab in the toolbar
			$(".gc-ribbon-bar>>ul>.fileButton").css("display", "none");
			
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
			//set no cell as active and ready foe writing
			workBook.focus(false); 
			
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
			 	workBook.bind(GC.Spread.Sheets.Events.EnterCell, function (event, data)
			    {
			      var sheet = data.sheet;
			      activeSheet.startEdit(false);
			    });
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