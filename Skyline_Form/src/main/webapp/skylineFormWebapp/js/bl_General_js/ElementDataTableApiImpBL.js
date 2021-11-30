/**
 * ElementDataTableApiImp BL General integration
 * 
 * @param domId
 * @returns
 */
function elementDataTableApiImpBL(domId) {
    
	onElementDataTableApiIsNew(domId); // Invoke code when isNew = 1.
	// ab 16092020: function is relocated to initElementDataTableApi()
	//    changeElementDataTableApiCaption(domId); // Change caption on special cases.
    elementDataTableApiImpGeneralDisabled(domId); // Disable datatableApi's
													// buttons after loading.
    $('#' + domId + ' th:contains("_uom")').text("UOM"); // change th of
									 						// columns that
															// 'UOM'
    var _formCode = $('#formCode').val();
    /* 'isPageInitFlag' flag is TRUE when table is first loaded on page initialization, 
     *   use this flag when there is not need to run specific code for current domId again (on onElementDataTableApiChange() call) */
    var isPageInitFlag = $('#'+domId+'_isPageInitFlag').val();
    if(isPageInitFlag == 'true') {
    	isPageInitFlag = true;
    	$('#'+domId+'_isPageInitFlag').val('false');
    }
    else {
    	isPageInitFlag = false;
    }
    
 
   if(_formCode == "Maintenance"){//bug 9439
	  var maintFormCode = $('#eMaintenanceTableApi_structCatalogItem').val();
	  if(maintFormCode != undefined && maintFormCode =='MP'){
		  $('[id="' + domId + '_colsArray"]').val('WL@WH@SL@SH');
		  var _table = $('#'+domId).DataTable();
//		   var wvLFlag = 0;
		   for(var i=0; i < _table.columns().header().length; i++) {
				var col_ = _table.column(i);
				
				col_ = _table.column(i);
				var _$header = $(col_.header());
				
				if(_$header.text() == 'WL' ||_$header.text() == 'WH'
					||_$header.text() == 'SL' ||_$header.text() == 'SH') {
					removeColumnDatatable(domId, _$header.text());	
				}
		   }
	  }
   }
   return true;
}  

function openTableInNewTab(domId){
	debugger;
	var page = "";
	if(domId == 'XXX'){
//		page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=" + "XXX" + "&formId=" +"-1" + "&userId=" + $('#userId').val() + '&PARENT_ID=' + $('#formId').val();
	}
	if(page!=""){
		openNewTab(page);
	}
}
 
/**
 * Invoke code when isNew = 1
 * 
 * @param domId
 * @returns
 */
function onElementDataTableApiIsNew(domId) {
    var isNew = $('#isNew');
    if (isNew.length > 0) {
        if (isNew.val() == "1") {
            if ($('#' + domId + '_onActionButtons').val() == 'Full Screen') {
                // Disable New button when isNew = 1 and the page target is on
				// full screen
                $('#' + domId + '_dataTableStructButtons button.dataTableApiNew').addClass('disabledclass');
                var $divMultiAddRows = $('#' + domId + '_dataTableStructButtons button.dataTableApiNew').parent('.multiRowsDiv');
                if( $divMultiAddRows.length = '1'){
                	$divMultiAddRows.addClass('disabledclass')
                }
            }
        }
    }
}

/**
 * Change caption on special cases
 * 
 * @param domId
 * @returns
 */
function changeElementDataTableApiCaption(domId) {
   
}

/**
 * Disable datatableApi's buttons after loading. 
 * Note! - this function should cover the scenario in which the table is render after the authz. 
 *         the isGeneralDisabledStateForLateRender return true if this scenario happened (mostly when no permission on the page)
 *		   in General we only put the disabled page on all of the 
 *       
 * 
 * @param domId
 * @returns
 */
function elementDataTableApiImpGeneralDisabled(domId) {
	//fix bug 8452
	if(domId != null && domId == 'training') {
		return;
	}
	//Edit to view popup in disabledclass (form builder disabled deffinition) 
	if($('#' + domId + '_dataTableStructButtons button.dataTableApiEdit').hasClass('dataTableApiButtonDisabled')) {
		$('#' + domId + '_dataTableStructButtons button.dataTableApiEdit').removeClass('disabledclass');
		$('#' + domId + '_dataTableStructButtons button.dataTableApiEdit').removeClass('dataTableApiButtonDisabled');
    	changeSingleDTLabelByDisabledState(domId, true);//+"_dataTableStructButtons"
	}  
	
	//EditShared stays disabled (form builder disabled deffinition )
	if($('#' + domId + '_dataTableStructButtons button.dataTableApiEditShared').hasClass('dataTableApiButtonDisabled')) {
		$('#' + domId + '_dataTableStructButtons button.dataTableApiEditShared').removeClass('disabledclass');
		$('#' + domId + '_dataTableStructButtons button.dataTableApiEditShared').removeClass('dataTableApiButtonDisabled');
    	changeSingleDTLabelByDisabledState(domId, true);//+"_dataTableStructButtons"
	}
	
    if($('#' + domId).attr("disableEditable")=="1"){//for editable tables, this code will ensure the disabling of them
		console.log("elementDataTableApiImpGeneralDisabled = ", domId);
		changeSingleDTLabelByDisabledState(domId, true);
	}  
	
	//late render scenario
	var isNew = $('#isNew');
    if (isNew.length > 0) {
        if ((isNew.val() == "0") && ($('#isStruct').val() == "1")) {
            if ($('#' + domId + '_Parent').attr('isfirstload') == '1') {
                $('#' + domId + '_Parent').attr('isfirstload', '0');
                if($('#' + domId).attr("disableEditable")==undefined && isGeneralDisabledStateForLateRender()) {
                	console.log("elementDataTableApiImpGeneralDisabled = ", domId);
                	$('#' + domId + '_dataTableStructButtons button.dataTableApiButton:not(.dataTableApiEditShared, .dataTableApiView, .dataTableApiLabel)').addClass('disablePage'); //kd 26112019 added .dataTableApiLabel for fixed bug7681
                	$('#' + domId + '_dataTableStructButtons button.dataTableApiButton:not(.dataTableApiEditShared, .dataTableApiView)').attr('LATE_RENDER','1'); // yp add it for debuging
                	changeSingleDTLabelByDisabledState(domId, true); // yp 17012019 put it to be on the safe side     //+"_dataTableStructButtons"
                }
            }
        }
	}
      
}

/**
 * Allow Clone for Struct with legal data
 * 
 * @param domId
 * @returns
 */
function allowCloneElementDataTableApi(domId) {
   
}

/**
 * 
 * @param struct
 * @param parentTable
 * @param isParentSelected
 * @returns Available DataStructure
 */
function dataTableApiDataStructure(struct, parentTable, isParentSelected)  {
	 var parentStruct, availableDataStructureArray;
	    var object = {
	    	'SubProject': 'Project' 
	    };

	    
	    // handle first table (parentTable.length == 0) new click that is not
		// project (not allow)
	    if($('#formCode').val() == 'XXX ') {
	        if (typeof object[struct] !== 'undefined') {
	            return object[struct];
	        } else {
	            return '';
	        }
	    } else if (!$('#' + parentTable).is('table')) {
	        if ($('#formCode').val() == 'YYY ') {
	            if (typeof object[struct] !== 'undefined') {
	                return object[struct];
	            }
	        }
	        return '';
	    }

	    parentStruct = $('#' + parentTable + '_structCatalogItem').val();
	    if (!isParentSelected) {
	        if (typeof object[struct] === 'undefined') {
	            return '';
	        } else return object[struct];
	    } else {
	        if (typeof object[struct] !== 'undefined') {
	            availableDataStructureArray = object[struct].split(' or ');
	            if ($.inArray(parentStruct, availableDataStructureArray) == -1) {
	                return object[struct];
	            } else {
	                return '';
	            }
	        } else {
	            return '';
	        }
	    }
}

/**
 * show Extras by formCode when needed
 * 
 * @returns
 */
function showExtras(domId) {
     
}

function generalBL_elementDataTableClickEvent(domId, customerFunction, params, $htmlObj)
{
 
} 

function generalBL_DTRowClickEvent(domId, rowDataArr, paramsObj)
{
 
}

function topRowsToDisplay_BL(domId,displayTopRows){
	if(displayTopRows !="-2"){//the table is not empty
		 $('#' + domId + '_displayTopRows').html('');
		 $('#' + domId + '_displayTopRows').prepend('<label class="cssStaticData">Top '+displayTopRows+' Results</label>');
		 $('#' + domId + '_displayTopRows').css('display','block');		
	 }
}

function doClearWhenEmpty(domId,formCode){
	return true;
}
   
function bl_elementDatatableEditableCustomFuncHandler(domId, customerFunction, $htmlObj, params)
{
	console.log("customerFunction", customerFunction);
	console.log("params", params);
	var formCode = $('#formCode').val();

	//favoritestar
	if (customerFunction == "onChangefavoritestar"){
		var rowId = $htmlObj.attr('rowId');
		var isFavoriteChecked = -1;
		if ($htmlObj.hasClass('fa fa-star')) {
			isFavoriteChecked = 0;
			$htmlObj.removeClass('fa fa-star');
			$htmlObj.addClass('fa fa-star-o');
			$htmlObj.css("color","black");
			$htmlObj.attr('value','no');
		} else if ($htmlObj.hasClass('fa fa-star-o')) {
			isFavoriteChecked = 1;
			$htmlObj.removeClass('fa fa-star-o');
			$htmlObj.addClass('fa fa-star');
			$htmlObj.css("color","#62B2DB");
			$htmlObj.attr('value','yes');
			
		}
		onChangefavorite(rowId,isFavoriteChecked,true,domId);	
		return;
	}
}
 
function bl_elementDatatableEditableAfterSaveHandler(domId, isRenderTable, afterSave_retVal, $htmlObj, paramsObj, rowIndex)
{
	console.log("bl_elementDatatableEditableAfterSaveHandler() domId: ", domId);
	console.log("isRenderTable: "+isRenderTable);
	
	var htmlType = paramsObj.htmlType;
	var dbColumnName = paramsObj.dbColumnName;
	var formCode = paramsObj.formCode;
	var isMandatory = paramsObj.isMandatory;
	var colTitle = paramsObj.colTitle;
	var isRenderTable = paramsObj.isRenderTable;
	var formNumberID = paramsObj.formNumberID;
	var saveType = paramsObj.saveType;
	var rowMandatoryField = paramsObj.rowMandatoryField;
	var rowMandatoryFieldID = paramsObj.rowMandatoryFieldID;
	var rowMandatoryFieldDisplayName = paramsObj.rowMandatoryFieldDisplayName;
	var isCellAutoSaved = paramsObj.isCellAutoSaved;
	var customFuncName = paramsObj.customFuncName;
	var customFuncParams = paramsObj.customFuncParams;
	var customFuncOnBeforeChange = paramsObj.customFuncOnBeforeChange;
	
	
	if(isRenderTable)
	{
		onElementDataTableApiChange(domId);		
	}
	else
	{
		var formCode = $('#formCode').val();
		
		if(formCode == "XXX")
		{
			 
		}
	}
}

function editableBL_getAdditParams(domId,rowInx,paramName)
{
	var returnData = "";
//	if(domId == "xxx" && paramName == "formNumberID")
//	{
//		var table = $('#'+domId).DataTable();		
//		returnData = table.row(rowInx).data()[5];
//	}
	return returnData;
}

function bl_isDeferRender(domId)
{
	var toReturn = false;
	 
	return toReturn;
}

function bl_isTableResizable(domId)
{ 
	var toReturn = false;
	var formCode = $('#formCode').val();
	if(formCode == "SystemLogReport")
	{
		toReturn = true;
	}
	return toReturn;
}

function bl_getResizableTables()
{
	var formCode = $('#formCode').val();
	if(formCode == "SystemLogReport") 
	{
		return $('table.dataTable');
	}
	
	return [];
}

function bl_getColumnDefaultWidth(domId, colsCount, followingHdnColsCount)
{
	var $colWidth = {};
	var formCode = $('#formCode').val();
	var resizableColDefaultWidth = 100;
	var winWidth = $(window).outerWidth();	
//	console.log("init winWidth: "+ winWidth," $(document).outerWidth(): "+$(document).outerWidth());
	var invisibleColsArr = [];
	
    if(isSameStructTable(domId)) {
    	var invisibleCols = $('[id="' + domId + '_colsArray"]').val();
    	invisibleColsArr = (invisibleCols.length > 0)?invisibleCols.split('@'):[];
    }
	var actualDisplayedColsCount = colsCount - (followingHdnColsCount + invisibleColsArr.length);
	
		
	resizableColDefaultWidth = winWidth/actualDisplayedColsCount; 
	
	
	$colWidth.DefaultWidth_ = resizableColDefaultWidth;
	return $colWidth;
}

function bl_isTableMergeable(domId)
{
	var toReturn = false;
	return toReturn;
}

function bl_isTableHasContextMenu(domId, tableRole)
{
	var toReturn = true;	
	var formCode = $('#formCode').val();
    if(tableRole == 'Attachment')
	{
		toReturn = false;
	}
	
	return toReturn;
}

function bl_isTableDrawUpgraded(domId)
{
	var toReturn = false;
	return toReturn;
}

/*
 * TODO: add Column Reorder support for all editable tables, for now only 'Action' table is fully supported.
 * 		Main reason for this, that the other editable tables may have specific behavior, different from editable table default behaviors, 
 * 			like in reactions table for example: if column was reordered and after this row added, the new row columns location not fitted actually columns location in previous rows, 
 * 			the reason is that the data structure object for new row saved on the client side (except for the first added row)
 * 		Each other editable tables should be checked before support for reorder.
 */
function bl_isTableReorderable(domId)
{
	var toReturn = true;
	
	return toReturn;
}

function bl_isColumnReorderable(domId, colName) 
{
	var toReturn = true;
 
	return toReturn;
}
 
function bl_removeLabelFromColName(domId) 
{
	var toReturn = false;
	
	return toReturn;
}

function outPutLabelDTWrapper(obj) {
	
//	outPutLabel(domId, labelCode, selectIdList);
}
    
function convertDecimalToExponential(valueToChange, prec) 
{	
	var valueToReturn = $.trim(valueToChange);
	try
	{
		var floatVal = parseFloat(valueToReturn);
		var precision = (prec !== undefined && prec != "")?prec:"3";
		precision = parseInt(precision);		
		
		if(!isNaN(floatVal) && floatVal != 0)
		{			
			// in case after round by precision, value display only zeros -> convert to Exponential
			var zeros = "";
			while(zeros.length < precision)
			{
				zeros += "0";
			}
			zeros = parseInt("1"+zeros);	
			
			if((floatVal * zeros) < 1)
			{
				valueToReturn = floatVal.toExponential(precision);
			}
			else
			{
				valueToReturn = floatVal.toFixed(precision);
			}
			var str = valueToReturn.toString();
			if (str.indexOf('e') !== -1) 
			{
				valueToReturn = str.toUpperCase();
			}
		}
	}
	catch(e)
	{
		console.log("ERROR in convertDecimalToExponential() for value: "+valueToChange);
		console.error(e);
	}
	return valueToReturn;
}

function bl_editableCellOnFocusCustomFuncHandler($htmlObj, customFuncName, customFuncParams)
{
	
	//if(customFuncName == "XXX") return handleYYY($htmlObj, false);
	
	return true;
}
 
function bl_setColVisibilityByColTitleArr(domId, isVisible, context) {
	var isOverride = false;
	//....
	return isOverride;
}

function openFormInNewWindow (domId,formId)
{
	var formCode = encodeURIComponent($('[id="' + domId + '_structCatalogItem"]').val());
	var stateKey = $('#' + domId + '_selectDiv').attr('stateKey');
	var parentId = $('#formId').val();
	
	var canRead = "0"
       $.ajax({
           type: 'POST',
           data: '{"action" : "getReadPermissionById","' + 'data":[' + '{"code":"formCode","val":"' + formCode + '"},' + '{"code":"formId","val":"' + formId + '"}' + '],' + '"errorMsg":""}',
           url: "./getReadPermissionById.request",
           contentType: 'application/json',
           dataType: 'json',
           success: function (obj) {
           	var canView= false;
               if (obj.errorMsg != null && obj.errorMsg != '') {
                   displayAlertDialog(obj.errorMsg);
                   return;
               } else if ((obj.data[0].val == "-1") || (obj.data[0].val == "")) {
               	//false
               } else {
                   canRead = obj.data[0].val;
               }
               
               if(canRead != "1") {
               		displayAlertDialog("View is not allowed");
               		return;
               } 
               else {
               		// make view code
               		var page = "./init.request?stateKey=" + stateKey + "&formCode=" + formCode + "&formId=" + formId + "&userId=" + $('#userId').val() + '&tableType=' + $('[id="' + domId + '_tableType"]').val()
               					+ '&urlCallParam=' + replaceDTUrlCallParamVal($('[id="' + domId + '_urlCallParam"]').val(), formId) + '&PARENT_ID=' + parentId;

               		openNewTab(page);
               		//return;
               	
               }
           },
           error: handleAjaxError
       }); 
}

function customizeExcelDTReport(xlsx, domId)
{
	 
} 

function addTitle(sheet , title) {
//	 var sheet = xlsx.xl.worksheets['sheet1.xml'];
     var downrows = 1;
     var clRow = $('row', sheet);
     //update Row
     clRow.each(function () {
         var attr = $(this).attr('r');
         var ind = parseInt(attr);
         ind = ind + downrows;
         $(this).attr("r",ind);
     });

     // Update  row > c
     $('row c ', sheet).each(function () {
         var attr = $(this).attr('r');
         var pre = attr.substring(0, 1);
         var ind = parseInt(attr.substring(1, attr.length));
         ind = ind + downrows;
         $(this).attr("r", pre + ind);
     });

     function Addrow(index,data) {
         msg='<row r="'+index+'">'
         for(i=0;i<data.length;i++){
             var key=data[i].k;
             var value=data[i].v;
             msg += '<c t="inlineStr" r="' + key + index + '">';
             msg += '<is>';
             msg +=  '<t>'+value+'</t>';
             msg+=  '</is>';
             msg+='</c>';
         }
         msg += '</row>';
         return msg;
     }

     //insert
     var r1 = Addrow(1, [{ k: 'A', v: title }]);
//     $('row c[r*="1"]', sheet).attr( 's', '2' );
      
     sheet.childNodes[0].childNodes[1].innerHTML = r1 + sheet.childNodes[0].childNodes[1].innerHTML;
}

function bl_dtHeaderRenderCustom(domId, dtColumnsApi, $header) {
	
}

function bl_dtColumnFeaturesCustom(domId, dtObject, currColumn, colIndex) {
	 
}
 
function isSameStructTable(domId)
{
	var _structCatalogItem = $('#' + domId + '_structCatalogItem').val();
    var _lastStruct = $('[id="' + domId + '_lastStruct"]').val();
    try{
    	_structCatalogItem = (_structCatalogItem == null || _structCatalogItem == undefined || _structCatalogItem == "undefined")? "": _structCatalogItem;
        _lastStruct = (_lastStruct == null || _lastStruct == undefined || _lastStruct == "undefined")? "": _lastStruct;
    }
    catch (e) {
    	_structCatalogItem = "";
    	_lastStruct = "";
	}
    if( _structCatalogItem == _lastStruct) {
    	return true;
    }
    return false;
}
 
function openConfirmMaterialCancelationDialog(domId){
    var struct = $('[id="' + domId + '_structCatalogItem"]').val();
    var selectedTable = $('#' + domId).DataTable();
    var custid = selectedTable.row('.selected').data();
    var formId = "";
    if (typeof custid !== 'undefined') {
        formId = custid[0];
    }
	var allData = [{
			code : "cancelledId",
			val : formId,
			type : "AJAX_BEAN",
			info : 'na'
		}];
	/*
	 * var allData = getformDataNoCallBack(1); var allData =
	 * allData.concat(stringifyToPush); var allData = { code : "batchList", val :
	 * batchList, type : "AJAX_BEAN", info : 'na' }
	 */
	
	// url call
	var urlParam = "?formId="+ $('#formId').val()
				+ "&formCode="+ $('#formCode').val()
				+ "&userId="+ $('#userId').val()
				+ "&eventAction=checkCancelMaterial"
				+ "&isNew=" + $('#isNew').val();
	var data_ = JSON.stringify({
		action : "checkCancelMaterial",
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
			if (obj.errorMsg != null && obj.errorMsg != '') {
				hideWaitMessage();
				displayAlertDialog(obj.errorMsg);
			} else {
				hideWaitMessage();
				openConfirmDialog({
					onConfirm : function(){onClickCancelMaterial(formId);},
					title : 'Warning',
					message : getSpringMessage('confirmMaterialCancellation')
				});
			}
		},
		error : handleAjaxError
	});
}
  
function bl_initFilterColumnDatatable(domId){
	 
	var toReturn = false;
	//...
	return toReturn;
}

function removeRowColumnSelect(domId){
	selectedTable = $('#' + domId).DataTable();
    custid = selectedTable.row('.selected').data();
    if(custid != undefined ){
    	rowId = custid[1];
    	var allData = [{
			code : "removedId",
			val : rowId,
			type : "AJAX_BEAN",
			info : 'na'
		}];
	// url call
	var urlParam = "?formId="+ $('#formId').val()
				+ "&formCode="+ $('#formCode').val()
				+ "&userId="+ $('#userId').val()
				+ "&eventAction=removeRowColumnSelect"
				+ "&isNew=" + $('#isNew').val();
	var data_ = JSON.stringify({
		action : "removeRowColumnSelect",
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
			if (obj.errorMsg != null && obj.errorMsg != '') {
				hideWaitMessage();
				displayAlertDialog(obj.errorMsg);
			} else if (obj.data[0].val == "-1") {
                hideWaitMessage();
                displayAlertDialog(getSpringMessage('remove failed'));
            } else {
				hideWaitMessage();
				onElementDataTableApiChange('columnSelect');
			}
		},
		error : handleAjaxError
	});
    }
}

/**
 * replace uomPlaceHolder (prop) in the table columns with the UOM of all the rows (as csv). 
 * in each row the UOM is taken from the [] expression and remove the UOM data from the cell value.
 * for example for uomPlaceHolder <$U>:
 * 
 * Col1 $U ... will become ->  Col1 [gr,kg] 
 * 1 [gr]					   1
 * 2 [kg]					   2
 * 						
 * @param domId
 * @returns
 */
function collectUOMtoColumnTitle(domId, uomPlaceHolder) {
   var _table = $('#'+domId).DataTable(); 
   for (var i=0; i < _table.columns().header().length; i++) {
	   try {
		   var col_ = _table.column(i);
			col_ = _table.column(i);
			var _$header = $(col_.header());
			var old_header = _$header.text();
			if(_$header.text().indexOf(uomPlaceHolder) >= 0) { 
				var uomData = [];
				_table.rows().eq(0).each( function ( index ) {
					var cell = _table.cell({row: index, column: i});	 
					var cdata = cell.data();
					var cnode = cell.node();
					if(cdata != null) {
						var uom_match_=cdata.match(/\[([^)]+)\]/); 
						if(uom_match_ != null) {
							var uom_= uom_match_[1];
							if(uomData.indexOf(uom_) == -1) {
								uomData.push(uom_);
							}
							var newCellVal = cdata.replace('[' + uom_ + ']','');
							cell.data(newCellVal).draw(false);
							$(cnode).text(newCellVal);
							dtExt_updateCellFilterData(domId, index, i, newCellVal); // need for the export data (or if calling not from elementDataTableApiImpBL)
						} else if(cdata.indexOf("[]") != -1) { //cover also empty uom (remove it)
							var newCellVal = cdata.replace('[]','').trim();
							
							cell.data(newCellVal).draw(false);
							$(cnode).text(newCellVal);
							dtExt_updateCellFilterData(domId, index, i, newCellVal); // need for the export data (or if calling not from elementDataTableApiImpBL)
						}
					}
				}); 
				var originHtml = _$header.html();
				var newHtml = originHtml.replace(uomPlaceHolder,uomData.length > 0?" [" + uomData.join() + "]":"");
				$(_table.column(col_).header()).html(newHtml); // replace the title in the column html (if using set text we will not have the sort and close icons)
				$('[name="' + domId + '_metaData"]').val($('[name="' + domId + '_metaData"]').val().replace(old_header,_$header.text()+"_SMARTNUM"));//fixed bug in the filter 
		    }
	   } catch(e) {
			console.log("collectUOMtoColumnTitlefailed in column index=" + i);
	   }
   }
}
