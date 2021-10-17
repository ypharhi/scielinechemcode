// init global variables
var globalEditableTableEmptyRowHolder = {};
var globalDataTableColumnWidthHolder = {};
var globalDataTableColumnsOrderHolder = {};
var globalDataTableFilterColumn = [];
var columnReOrderDisabledClass = "column-reorder-disabled";

var ElementDataTableApiImp = {
	value_: function (val_) {
        var domId = $(val_).attr('id');
        var role = $('#' + domId + '_role').val();
        var struct, criteria, display, linkToLastSelection, formCode, showDiv, formId = "",
            cols, table, toReturn, lastPageLength;
        var csvList_ = [];
        if(role == 'Multiple' || role == 'MultipleAjax'){
        	var lastMultiValues = $('#' + domId + '_value').val(); // get the values from lastMultiValues that may contain selection that not appear in this filter (criteria) table
        	if(lastMultiValues.length > 0) {
        		csvList_ = lastMultiValues.split(',');
        	} 
        	if ($.fn.DataTable.isDataTable('#' + domId)){//fixed bug 7377->check if the table has already been ready. if not->the value is the one that is stored in the value. this case can occur when changing a value of an element that causing onAjaxChange in which the values of all elements are sent to the server.
        												//The table may not be ready on loading of the site(navigating/save/refresh). in this case->there is no need in the following rows, since it select the data that was currently changed, and this can be done only after the table is ready.
	        	var table_ = $('#' + domId).DataTable(); 
	            
	        	// add selected values that are not in lastMultiValues + remove unselected values that are in this table (=> the user unselected them)
	            table_.$('input[Type="checkbox"]').each(function (index) {
	            	if((',' + lastMultiValues + ',').indexOf(',' + $(this).val() + ',') <= -1) {
	            		if($(this).prop('checked')) {
	            			csvList_.push($(this).val());
	            		}
	            	} else {
	            		if(!$(this).prop('checked')) {
	            			//remove val from array
	            			const index = csvList_.indexOf($(this).val());
	            			csvList_.splice(index, 1);
	            		}
	            	}
	            });  
        	}
        }
        if (role == "" || role == 'Search' || role == 'SharedAjax'|| role == 'MultipleAjax') { // return value when datatableapi has no role
            struct = $('#' + domId + '_structCatalogItem').val();
            criteria = $('#' + domId + '_criteriaCatalogItem').val();
            display = $('#' + domId + '_displayCatalogItem').val();
            linkToLastSelection = $('#' + domId + '_LinkToLastSelection').is(":checked") ? "1" : "0";
            formCode = $('#' + domId + '_selectDiv').attr('formCode');
            cols = $('#' + domId + '_colsArray').val();
            lastPageLength = $('#' + domId + '_Parent select[name$="_length"]').val();
            if ($('#' + domId).closest('[id*="show"]').length) { //showDiv is for the main screen (when we need to use plus and minus to hide the tables)
                if ($('#' + domId).closest('[id*="show"]').css('display') == "none") {
                    showDiv = "0";
                } else {
                    showDiv = "1";
                }
            } else {
                showDiv = "0";
            }
            
            if (role == 'SharedAjax') { // get formId for the selected row (second!!!! column) the first one is passed to the multiple popup
            	if ($.fn.DataTable.isDataTable('#' + domId)){//fixed bug 7377->check if the table has already been ready. if not->the value is the one that is stored in the value. this case can occur when changing a value of an element that causing onAjaxChange in which the values of all elements are sent to the server.
					//The table may not be ready on loading of the site(navigating/save/refresh). in this case->there is no need in the following rows, since it select the data that was currently changed, and this can be done only after the table is ready.
	            	var selectedTable = $('#' + domId).DataTable();
	                var custid = selectedTable.row('.selected').data();
	                if (typeof custid !== 'undefined') {
	                    formId = custid[1];
	                } else if($('#' + domId + '_sharedFormId').val()!== undefined && $('#' + domId + '_sharedFormId').val()!="-1"){
	                	formId = $('#' + domId + '_sharedFormId').val();
	                } else if (prop.onChangeAjaxFlag == true) {
	                    return "";
	                }
            	} else if($('#' + domId + '_sharedFormId').val()!== undefined && $('#' + domId + '_sharedFormId').val()!="-1"){
                	formId = $('#' + domId + '_sharedFormId').val();
                } else if (prop.onChangeAjaxFlag == true) {
                    return "";
                }
            } else if (role == 'MultipleAjax') { // get formId for the selected row (second!!!! column) the first one is passed to the multiple popup
            	if ($.fn.DataTable.isDataTable('#' + domId)){//fixed bug 7377->check if the table has already been ready. if not->the value is the one that is stored in the value. this case can occur when changing a value of an element that causing onAjaxChange in which the values of all elements are sent to the server.
					//The table may not be ready on loading of the site(navigating/save/refresh). in this case->there is no need in the following rows, since it select the data that was currently changed, and this can be done only after the table is ready.
	            	var selectedTable = $('#' + domId).DataTable();
	            	//add selected rows that were not checked because the checkbox was disabled
	            	selectedTable.rows('.selected').data().each(function(value, index){
	            		var data = value;
	            		var id = data[0];
	            		if(csvList_.indexOf(id)== -1){
	            			csvList_.push(id);
	            		}
	            	});
            	}
            	var custid = csvList_.toString().replace(/,/g, '@');
                if (typeof custid !== 'undefined' && custid !='') {
                    formId = custid;
                } else { //if (prop.onChangeAjaxFlag == true)
                    return "";
                }
            }
            else if ($('#' + domId + '_formId').val() != "") { // get formId for the selected row
                formId = $('#' + domId + '_formId').val();
                $('#' + domId + '_formId').val("");
            } else if ($('[id="' + domId + '"] thead').length) { // get formId for the selected row
                var selectedTable = $('#' + domId).DataTable();
                var custid = selectedTable.row('.selected').data();
                if (typeof custid !== 'undefined') {
                    formId = custid[0];
                } else if (prop.onChangeAjaxFlag == true) {
                	//yp 04032020 main form - with the workaround made in 27022020 - do not return empty (if table2 is linked after 3 changed in criteria we see all values without this workaround)
                	if($('#formCode').val() != 'Main') {
                		return "";
                	}
                }
            }
            return struct + ',' + criteria + ',' + display + ',' + linkToLastSelection + ',' + formCode + ',' + showDiv + ',' + lastPageLength + ',' + formId + ',' + cols;
        } else if (role == "Multiple") { // return value when datatableapi's role is Multiple (part of fix bug 5902) 
        	return csvList_.toString(); 
        } 
        return "";// return empty value when datatableapi has a role and its not Multiple
    },
    setvalue_: function (val_) {
        //important: should add attribute 'lastvalue' to the element imp in the poll (like in ElementAutoCompleteIdValDDLImp)
    },
    setDefaultValueForUnitTest_: function (val_) {
    },
	userLastSaveValue_ : function(val_) {
		var domId = $(val_).attr('id');
		
		//clean old values
		 $('#' + domId + '_userLastSaveSettings').val("");
		 
		//return new values 
		var role = $('#' + domId + '_role').val();
		var struct, criteria, display, linkToLastSelection, formCode, showDiv, formId = "", cols, table, toReturn, lastPageLength;
		var _val = "";
//		if ($('#isStruct').val() != "1" && (role == "" || role == 'Search')) { // return value only in non struct screens (without search role)
		struct = $('#' + domId + '_structCatalogItem').val();
		criteria = $('#' + domId + '_criteriaCatalogItem').val();
		display = $('#' + domId + '_displayCatalogItem').val();
		linkToLastSelection = $('#' + domId + '_LinkToLastSelection').is(":checked") ? "1" : "0";
		formCode = $('#' + domId + '_selectDiv').attr('formCode');
		cols = $('#' + domId + '_colsArray').val();
		lastPageLength = $('#' + domId + '_Parent select[name$="_length"]').val();
		if ($('#' + domId).closest('[id*="show"]').length) { //showDiv is for the main screen (when we need to use plus and minus to hide the tables)
			if ($('#' + domId).closest('[id*="show"]').css('display') == "none") {
				showDiv = "0";
			} else {
				showDiv = "1";
			}
		} else {
			showDiv = "0";
		}
		if (role == "MultipleAjax") {
			var lastMultiValues = $('#' + domId + '_value').val(); // get the values from lastMultiValues that may contain selection that not appear in this filter (criteria) table
        	var csvList_ = [];
        	if(lastMultiValues.length > 0) {
        		csvList_ = lastMultiValues.split(',');
        	} 
        	var table_ = $('#' + domId).DataTable(); 
            
        	// add selected values that are not in lastMultiValues + remove unselected values that are in this table (=> the user unselected them)
            table_.$('input[Type="checkbox"]').each(function (index) {
            	if((',' + lastMultiValues + ',').indexOf(',' + $(this).val() + ',') <= -1) {
            		if($(this).prop('checked')) {
            			csvList_.push($(this).val());
            		}
            	} else {
            		if(!$(this).prop('checked')) {
            			//remove val from array
            			const index = csvList_.indexOf($(this).val());
            			csvList_.splice(index, 1);
            		}
            	}
            });  
            _valmultipleDT = _val;
            _val =  csvList_.toString().replace(/,/g, '@');
            if(_val!=''){
            	formId =_val;
            } else if (prop.onChangeAjaxFlag == true) {
				return "";
			}
		} else if ($('#' + domId + '_formId').val() != "") { // get formId for the selected row
			formId = $('#' + domId + '_formId').val();
			$('#' + domId + '_formId').val("");
		} else if ($('[id="' + domId + '"] thead').length) { // get formId for the selected row
			var selectedTable = $('#' + domId).DataTable();
			var custid = selectedTable.row('.selected').data();
			if (typeof custid !== 'undefined') {
				formId = custid[0];
			} else if (prop.onChangeAjaxFlag == true) {
				return "";
			}

		} 
		
		_val = struct + ',' + criteria + ',' + display + ','
			+ linkToLastSelection + ',' + formCode + ',' + showDiv + ','
			+ lastPageLength + ',' + ((role == "Multiple" || ($('#isStruct').val() == '1')) ? "" :formId) + ',' + cols;
			
//		} else 
//		if (role == "Multiple") {
//			var lastMultiValues = $('#' + domId + '_value').val(); // get the values from lastMultiValues that may contain selection that not appear in this filter (criteria) table
//        	var csvList_ = [];
//        	if(lastMultiValues.length > 0) {
//        		csvList_ = lastMultiValues.split(',');
//        	} 
//        	var table_ = $('#' + domId).DataTable(); 
//            
//        	// add selected values that are not in lastMultiValues + remove unselected values that are in this table (=> the user unselected them)
//            table_.$('input[Type="checkbox"]').each(function (index) {
//            	if((',' + lastMultiValues + ',').indexOf(',' + $(this).val() + ',') <= -1) {
//            		if($(this).prop('checked')) {
//            			csvList_.push($(this).val());
//            		}
//            	} else {
//            		if(!$(this).prop('checked')) {
//            			//remove val from array
//            			const index = csvList_.indexOf($(this).val());
//            			csvList_.splice(index, 1);
//            		}
//            	}
//            });  
//            _valmultipleDT = _val;
//            _val =  csvList_.toString();
//		}	
		 
		// get user settings - filter/sort state 		
		var _tableConfig = [];
		try 
		{
			var _table = $('#' + domId).DataTable();
			var _settings = _table.settings();			
			var _colSearchArr = {};
			var _sortArr = [];
			var _colsWidthObj = {};
			var _colsOrderArr = [];
			var _colsFilterObj = {};
			
			if($('#' + domId).parent().hasClass('dt-colresizable-table-wrapper'))
			{
				_colsWidthObj = globalDataTableColumnWidthHolder[domId];
			}
			if(Object.keys(globalDataTableColumnsOrderHolder).length > 0 || globalDataTableColumnsOrderHolder.hasOwnProperty(domId))
			{
				_colsOrderArr = globalDataTableColumnsOrderHolder[domId];
			}
			if(Object.keys(globalDataTableFilterColumn).length > 0 || globalDataTableFilterColumn.hasOwnProperty(domId))
			{
				_colsFilterObj = globalDataTableFilterColumn[domId];
			}
			_table.columns().eq(0).each(function(colIdx) 
			{				
				var curColumn = _table.column(colIdx);								
				var input = $(curColumn.footer()).find('input[class="firstString"]');
				if($(input).length > 0 && $.trim($(input).val()) != "")
				{					
					var _header = $(curColumn.header())[0];
					var _title = getColumnUniqueName($(_header));
					
					_colSearchArr[_title] = {
												"search" : $(input).val(),
												"ddlSearch":$(input).siblings('select').val()												
											};
				}
			});

    		var indSortArr = _settings.order();
    		for (var i=0;i < indSortArr.length;i++)
    		{
    			var _colInd = indSortArr[i][0];
    			var _title = getColTitleByColIndex(_colInd, domId);
    			_sortArr[i] = [_title,indSortArr[i][1]];
    			
    		}
//    		console.log("sortArr",_sortArr);
			
			_tableConfig.push({
				"sort" : _sortArr,
				"columnSearch" : _colSearchArr,
				"columnWidth" : _colsWidthObj,
				"columnOrder" : _colsOrderArr,
				"columnFilter" : _colsFilterObj
			});
			console.log(domId, "tableConfig: "+ JSON.stringify(_tableConfig));
		} 
		catch (e) {
			console.log("error in eval user data table settings. e = " + e);
		}

		// build return object
		var toReturnObj_ = {
			"value" : _val,
			"settings" : _tableConfig
		};

		// return value when datatableapi has no role
		return JSON.stringify(toReturnObj_);

	}
};


function initEditableTableOnReadyScript()
{
	console.log("initEditableTableOnReadyScript()");	
	/**
	  * Disables creating the inline editor automatically for elements with
	  * the 'contenteditable' attribute set to 'true'.
	  *
	  *		CKEDITOR.disableAutoInline = true;
	  *
	  * @cfg {Boolean} [disableAutoInline=false]
	  */
	
	//CKEDITOR.disableAutoInline = true;
	 
	$('body').append(
		        '<div id="divPopupRichText" style="overflow-y:auto">\n' +
			        '<div style="width:100%">\n'+
			      		'<div autocomplete="off" id="tablecellRichtextInstance" class="ckeditor" placeholder="" style="border-radius: 5px; visibility: hidden; display: none;"></div>\n'+
			    	'</div>'+
		        '</div>\n');
		$("#divPopupRichText").dialog({
	        autoOpen: false,
	        width: '30%',
	        modal: false,
	        overflow:"auto",
	        open: function (event, ui) 
	        {
	        	setFocusToTablecellRichtextInstance();
	        },
	        close: function () {
	        	setNewCellDataRichText();
	        }
	    });
	    
		
	    $('#tablecellRichtextInstance').richtext({height:150,removeButtons:['insert','table','help']});

	    $(document).on('click', function (e) {
		    if ($(e.target).closest("#divPopupRichText").length === 0) 
		    { 
		    	if($("#divPopupRichText").dialog('isOpen'))
		    	{
		    		//console.log("document not_close", $("#divPopupRichText").hasClass("not_close"));
		    		//set focus on richtext
		    		$('.cke_wysiwyg_frame').contents().find('body').focus();

		    		if($("#divPopupRichText").hasClass("not_close"))
					{
						$("#divPopupRichText").removeClass("not_close");
					}
					else
					{
						$("#divPopupRichText").dialog('close');
					}
		    	}
		    }
		});
	
	
	    /******************* DataTable extension to sort in columns ***********/
	    dataTableCustomSort();
	    
	    /* Create an array with the values of all the input boxes in a column, parsed as date */
	    $.fn.dataTable.ext.order['time-custom-sort'] = function  ( settings, col )
	    {
	       // console.log("time-custom-sort", col);	        	        
	    	return this.api().column( col, {order:'index'} ).nodes().map( function ( td, i ) 
	        {
	    		var _val = 0;
	    		var $input = $(td).find('input');
	    		if($input.length > 0)
	    		{
	    			_val = $('input', td).val();
	    		}
	    		else
	    		{
	    			_val = $(td).html();
	    		}
	    		var toReturn = dtExt_getTimeValue(_val);
	    		//console.log(_val,toReturn);
	    		return toReturn;	    		
	        } );
	    }
	    
	    /* Create an array with the values of all the input boxes in a column, parsed as numbers */
	    $.fn.dataTable.ext.order['numeric-custom-sort'] = function  ( settings, col )
	    {
	       // console.log("numeric-custom-sort", col);	        
	        
	    	return this.api().column( col, {order:'index'} ).nodes().map( function ( td, i ) 
	        {
	    		var _val = 0;
	    		var $input = $(td).find('input');
	    		var $span = $(td).find('span.viewableSmartCell');
	    		if($input.length > 0)
	    		{
	    			_val = $.trim($('input', td).val());
	    		}
	    		else if($span.length > 0)
	    		{
	    			_val = $.trim($span.attr('originalValue'));
	    		}
	    		else
	    		{
	    			_val = $.trim($(td).html());
	    		}
	    		if(_val == null || _val.length == 0) _val = 0;
	    		return parseFloat(_val);	    		
	        } );
	    }
	    /***************************************************************************************/	    
}

var _isTableRowLinkClicked = false;
var _ignoreFirstTfootTd = "";
var _smartSelectTablesArray = [];
// yk
function onLevelSelectedChange(domId, options, triggerAjaxChange, actionName)
{
	var formCode = $('#' + domId + '_selectDiv').attr('formCode');
	var struct = $('#' + domId + '_structCatalogItem').val();
	var displayCatalog = $('#' + domId + '_displayCatalogItem').val();
	var isStructSelection = $('#' + domId + '_structSelection').val();
	var isAsync = (isStructSelection == '1')?false:true; // we make sync call just on tables that level can be chabge (without it we get wrong data)
	$('#' + domId + '_Caption').html(struct);
	changeElementDataTableApiCaption(domId); // remove DivCount and correct header
	
	// ajax call to the api service
    $.ajax({
        type: 'POST',
        async: isAsync,
        data: '{"action" : "onLevelSelectedChange","' +
            'data":[{"code":"struct","val":"' + struct + '"},{"code":"formCode","val":"' + formCode + '"},{"code":"displayCatalog","val":"' + displayCatalog + '"},{"code":"domId","val":"' + domId + '"}],' + '"errorMsg":""}',
        url: "./onLevelSelectedChange.request",
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) {
            if (obj.errorMsg != null && obj.errorMsg != '') {
                displayAlertDialog(obj.errorMsg);
            } else if (obj.data[0].val == "-1") {
                displayAlertDialog("Error");
            } else {
            	if(actionName!='init')
            		showWaitMessage(getSpringMessage('pleaseWait'));
            	
            	var colsArray = JSON.parse(obj.data[0].val).colArray[0];
            	setColsArray(domId,  colsArray);
            	
            	var o = JSON.parse(obj.data[0].val);
            	setCriteria(o, domId,  options, actionName);
            }
        },
        error: handleAjaxError
    });
}
//yk
function setCriteria(obj, domId, dataTableOptions, actionName ) {   
	 var lastSaveCriteria = $('#' + domId + '_criteriaCatalogItem').val();
	 var criteriaOption = "";
	 var option = "";
	 var flag = false;
	 var isDefault = 0;
	  	 		
	 for(var i = 0; i< obj.data.length;i++) {
		 option = obj.data[i].SYSCONFSQLCRITERIANAME.toString();
		 isDefault = obj.data[i].ISDEFAULT.toString();
		 
		 if( actionName=="init" && lastSaveCriteria != null && lastSaveCriteria.length > 0) {
			 if(lastSaveCriteria == option) {
				 criteriaOption +="<option selected value=\""+option+"\">"+option+"</option>\n";
				 flag = true;
			 }
			 else {
				 criteriaOption += "<option value=\""+option+"\">"+ option+"</option>\n"
			 }
		 } else {
			 if(isDefault=='1' && !flag ) {
				 criteriaOption +="<option selected value=\""+option+"\">"+option+"</option>\n";
				 flag = true;
			 }  else {
				 criteriaOption += "<option value=\""+option+"\">"+ option+"</option>\n"
			 }
		 }
	 }
	 
	 if(obj.data.length == 0 || !flag)	{ 
		 criteriaOption ="<option selected value=\"ALL\">ALL</option>\n" + criteriaOption;	 
	 } else {
 		 criteriaOption ="<option value=\"ALL\">ALL</option>\n" + criteriaOption;
 	 } 		
			 			
	 $('#' + domId + '_criteriaCatalogItem').html(criteriaOption);
}

function setColsArray(domId, colsArray) {   
	 $('#' + domId + '_colsArray').val(colsArray);
}

/**
 * on DataTableApi Change
 * @param domId
 * @param options
 * @param triggerAjaxChange
 * @returns
 */
function onElementDataTableApiChange(domId, options, triggerAjaxChange, genarateReportTable) {	

	//yp 27022020 fix performance bug in main screen (should be not just in main in the next versions! consider better solution instead of this workaround) - avoid the table render on ajax when link to last selection is not checked (when the call is made from the element pool on ajax change - we recognize this context using fromInitTable option false - means ajax)
	try {
		if($('#formCode').val() == 'Main') {
			if (typeof options !== 'undefined' && options != null) {
				var dtop_ = JSON.parse(options);
				if(!$('#' + domId + '_LinkToLastSelection').is(":checked") && dtop_.fromInitTable!== undefined && dtop_.fromInitTable.toLowerCase()=="false") {
					return;
				}
			}
		}	
	} catch (e) {
		// do nothing
	}

    var triggerAjaxChangeFlag = false, genarateReportTableFlag = false,
        dataTableOptions;
    if (typeof options !== 'undefined' && options != null) { // save the options to use it after the ajax call (ajax to the api service)
        $('#' + domId + '_dataTableOptions').val(options);
    } else {
    	options = $('#' + domId + '_dataTableOptions').val();
        
    }
    if ((typeof triggerAjaxChange !== 'undefined' && triggerAjaxChange != null) && (triggerAjaxChange)) { // trigger ajax after back from api service
        triggerAjaxChangeFlag = true;
    }
    if ((typeof genarateReportTable !== 'undefined' && genarateReportTable != null) && (genarateReportTable)) { // trigger ajax after back from api service
    	genarateReportTableFlag = true;
    }
    dataTableOptions = JSON.parse(options);
    //fixed bug 8546 - opening a required table and closing it without adding data has removed the required attr from the table
    if($('#'+ domId).attr('required')!=undefined && $('#'+ domId).attr('required')=='required' && typeof options.isMandatory == 'undefined'){//
    	dataTableOptions["isMandatory"] = "true"; 
	}
    var role = $('#' + domId + '_role').val();
    var updateMultiValues = false;
    if (!genarateReportTableFlag && role.toLowerCase().indexOf('shared')==-1 && $('#' + domId + '_LinkToLastSelection').is(":checked")) { // on render empty and not genarateReportTable
        if (typeof dataTableOptions !== 'undefined') {
        	if(!triggerAjaxChangeFlag && dataTableOptions.clearSelected!== undefined && dataTableOptions.clearSelected.toLowerCase()=="true"){   		
        		if(role.indexOf('Multiple')!='-1'){
        			updateMultiValues = true;
        			/*var table_ = $('#' + domId).DataTable(); 
        			$('#' + domId + '_value').val('');
        			table_.$('input[Type="checkbox"]').each(function (index) {
                    	$(this).prop('checked',false);
        			});*/
        		}
        	}
            if (typeof dataTableOptions.clearDataWhenEmpty !== 'undefined') {
                if (dataTableOptions.clearDataWhenEmpty.toLowerCase() == "true" && doClearWhenEmpty(domId,$('#formCode').val())) { 
                	clearDataTableWhenEmpty(domId,dataTableOptions);
                	if(updateMultiValues == true){//it is multiple/multiplaAjax role and selected values should be clear
                		$('#' + domId + '_value').val('');
                	}
                	return;
                }
            }
        }
    }
    var struct = $('#' + domId + '_structCatalogItem').val();
    var criteria = $('#' + domId + '_criteriaCatalogItem').val();
    var display = $('#' + domId + '_displayCatalogItem').val();
    var linkToLastSelection = $('#' + domId + '_LinkToLastSelection').is(":checked") ? '1' : '0';
    var formCode = $('#' + domId + '_selectDiv').attr('formCode');
    var thisFormId = $('#' + domId + '_selectDiv').attr('thisFormId');
    var stateKey = $('#' + domId + '_selectDiv').attr('stateKey');
    var tableType = $('#' + domId + '_tableType').val();
    var urlCallParam = $('#' + domId + '_urlCallParam').val();
    var sourceElementImpCode = $('#' + domId + '_sourceElementImpCode').val();
    var hideEmptyColumns = (typeof dataTableOptions.hideEmptyColumns !== 'undefined') ? 'true' : 'false';
    var followingHiddenColsInt = 0;
    if (typeof dataTableOptions.followingHiddenCols !== 'undefined') {
        var followingHiddenCols = dataTableOptions.followingHiddenCols;
        followingHiddenColsInt = (isNaN(followingHiddenCols)) ? 0 : Number(followingHiddenCols);
    }
    
    //yp 16052018 - fix bug 5902
    var lastMultiValues = $('#' + domId + '_value').val(); 
    // try get selection from Multiple role table if exists (will not exists in first page load)
    try {
    	if(typeof dataTableOptions.role !== 'undefined' && dataTableOptions.role.indexOf('Multiple')!=-1 && $('[id="' + domId + '"] thead').length){ 
    		    		
    		var csvList_ = [];
        	if(lastMultiValues.length > 0) {
        		csvList_ = lastMultiValues.split(',');
        	} 
        	var table_ = $('#' + domId).DataTable(); 
            
        	// add selected values that are not in lastMultiValues + remove unselected values that are in this table (=> the user unselected them)
            table_.$('input[Type="checkbox"]').each(function (index) {
            	if((',' + lastMultiValues + ',').indexOf(',' + $(this).val() + ',') <= -1) {
            		if($(this).prop('checked')) {
            			csvList_.push($(this).val());
            		}
            	} else {
            		if(!$(this).prop('checked')) {
            			//remove val from array
            			const index = csvList_.indexOf($(this).val());
            			csvList_.splice(index, 1);
            		}
            	}
            }); 
            lastMultiValues =  csvList_.toString();
        }  
    	//else {
        //	lastMultiValues = $('#' + domId + '_value').val();
        //}
    } catch(e) {}
    
    // update the hidden parameter with last selection
    $('#' + domId + '_value').val(lastMultiValues);
    //Done (bug bug 5902)
    
    if(!isDivShown(domId)){
    	$('#' + domId + '_structCatalogItem').val('Choose');
    	struct = 'Choose';
    }
    
    if ((struct == 'Choose') || (struct == '')) {//isDivShown -for the main page. if the table is invisible then not rendering the table and let the struct to be 'Choose'.
    	hideWaitMessage();
//    	initDivCount(domId); // remove DivCount and correct header
    	clearDataTableWhenEmpty(domId,dataTableOptions);
    	//$('#hiddenformMessage_'+ domId).hide();
        return;
    }
    
    if(triggerAjaxChangeFlag || genarateReportTableFlag) {
    	showWaitMessage(getSpringMessage('pleaseWait'));
    }    
    /*var columnNameOfMultiValues = "";
    if(updateMultiValues == true){
	    var table_ = $('#' + domId).DataTable(); 
	    var _uTitle = "";
	    table_.columns().iterator('column', function ( settings, column) 
    	{//
    		//console.log(column);
    		//console.log(settings.aoColumns[ column ]);
    		var _thisColumn = settings.aoColumns[ column ];
    		if(column == followingHiddenColsInt){
		    	_uTitle = _thisColumn.uniqueTitle;
    		}
	    });
	    //var array = $('[id="' + domId + '_colsArray"]').val().split('@');
	    columnNameOfMultiValues = _uTitle;
    }*/
    // ajax call to the api service
    $.ajax({
        type: 'POST',
        data: '{"action" : "onElementDataTableApiChange","' +
            'data":[{"code":"struct","val":"' + struct + '"},{"code":"criteria","val":"' + criteria + '"},{"code":"display","val":"' + display + '"},{"code":"linkToLastSelection","val":"' + linkToLastSelection + '"},{"code":"formCode","val":"' + formCode + '"},{"code":"tableType","val":"' + tableType + '"},{"code":"sourceElementImpCode","val":"' + sourceElementImpCode + '"},{"code":"hideEmptyColumns","val":"' + hideEmptyColumns + '"},{"code":"lastMultiValues","val":"' + lastMultiValues + '"},{"code":"thisFormId","val":"' + thisFormId + '"},{"code":"stateKey","val":"' + stateKey + '"},{"code":"updateMultiValues","val":"' + updateMultiValues + '"},{"code":"followingHiddenCol","val":"' + followingHiddenColsInt + '"}],' + '"errorMsg":""}',
        url: "./onElementDataTableApiChange.request",
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) {
            if (obj.errorMsg != null && obj.errorMsg != '') {
                displayAlertDialog(obj.errorMsg);
            } else if (obj.data[0].val == "-1") {
                displayAlertDialog("Error");
            } else {
            	//showWaitMessage(getSpringMessage('pleaseWait'));
                var o = JSON.parse(obj.data[0].val);
                var sql_info = "";
                if(obj.data[1]) {//if(obj.data[obj.data.length - 1])
                	var sql_info = obj.data[1].val; // sql system info
//                	console.log("sql_info for domId "+ domId +": " + sql_info);
                	var sql_warning_info = obj.data[1].info; // sql warning info
                	if(sql_warning_info != null && sql_warning_info != '') {
                		 displayAlertDialog(sql_warning_info);
                	} 
                	var element_info = $('#' + domId + '_infoElemnetDialog');
                	if(typeof element_info !== 'undefined' && element_info.length > 0) 
                	{
                		//console.log("sql_info: " + sql_info);
                		$('#' + domId + '_infoElemnetDialog').find('p#DATA_TABLE_SQL_INFO').html(sql_info);
                		//var element_info_val = $('#' + domId + '_infoElemnetDialog').html();                		
                		//console.log("element_info_val: " + $('#' + domId + '_infoElemnetDialog').html());
                	}
                }
                if(obj.data[2]) {//if(obj.data[obj.data.length - 1])
                	var formIdForShared = obj.data[2].val; // formId of shared table
                	if((typeof dataTableOptions.role !== 'undefined') && (dataTableOptions.role.toLowerCase().indexOf('shared')!=-1)){
                		$('#' + domId + '_sharedFormId').val(formIdForShared);
                	}
                }
                if ($('#' + domId + '_LinkToLastSelection').is(":checked") && !genarateReportTableFlag) { // on render empty and not genarateReportTable
                    if (typeof dataTableOptions !== 'undefined') {
                        if (typeof dataTableOptions.clearDataWhenEmpty !== 'undefined') {
                            if (dataTableOptions.clearDataWhenEmpty.toLowerCase() == "true" && doClearWhenEmpty(domId,$('#formCode').val())) { 
                            	clearDataTableWhenEmpty(domId,dataTableOptions);
                            	return;
                            }
                        }
                    }
                }
                if(obj.data[3]) {
                	
                	var displayTopRows = obj.data[3].val;
                	if($('#' + domId + '_displayTopRows').length > 0) {
	               		 $('#' + domId + '_displayTopRows').css('display','none');
	               	}
                	if(displayTopRows !='-1'){
                		topRowsToDisplay_BL(domId,displayTopRows);
                	}
                }
                if(obj.data[4]) {
                	var updatedLastMultiValues = obj.data[4].val; // formId of shared table
        			$('#' + domId + '_value').val(updatedLastMultiValues);
                }
                /*if ($('#' + domId + '_LinkToLastSelection').is(":checked") && !genarateReportTableFlag) { // on render empty and not genarateReportTable
                    if (typeof dataTableOptions !== 'undefined') {
                        if (typeof dataTableOptions.clearDataWhenEmpty !== 'undefined') {
                            if (dataTableOptions.clearDataWhenEmpty.toLowerCase() == "true" && doClearWhenEmpty(domId,$('#formCode').val())) { 
                            	clearDataTableWhenEmpty(domId,dataTableOptions);
                            	return;
                            }
                        }
                    }
                }*/

                buildElementDataTableApi(o, domId, dataTableOptions, triggerAjaxChangeFlag);
            }
        },
        error: handleAjaxError
    });

    var doOnChangeSelectJSCall="";
    if($('[id="' + domId + '"] .chosen-select').length > 0) {
    	$('[id="' + domId + '"] .chosen-select').chosen({allow_single_deselect:true,search_contains:true})
    	.on('change', function(){ doOnChangeSelectJSCall });
    }
}

function clearDataTableWhenEmpty(domId,dataTableOptions)
{
	// move dataTableStructButtons after tableFilterControls (outside of the table wrapper) because we destroy the table
	$('#' + domId + '_length').remove();
	$('#' + domId + '_tableFilterControls').after($('#' + domId + '_dataTableStructButtons'));

	cleanDataTable(domId); // clean,destroy and unbind old data and events from the datatable
	var colArray = [{ //init datatable options for render empty
	    showPageFilter: "0",
	    title: ""
		}];
	var object = {};
	object.columns = colArray;
	object.bDestroy = true;
	object.dom = 'lfrtip';
	object.pagingType = 'full_numbers';
	object.initComplete = function(settings, json)
	{
		//console.log(domId + " DataTables has finished its initialisation.");
		$('#hiddenformMessage_'+ domId).hide();
	}
	var table = $('[id="' + domId + '"]').DataTable(object);
	table.clear().draw();
	$('[id="' + domId + '"] tbody').off('click');
	if (typeof dataTableOptions != 'undefined' && typeof dataTableOptions.isHidden !== 'undefined') {
	    if (dataTableOptions.isHidden.toLowerCase() == "false") {
	        $('[id="' + domId + '_Parent"]').css('visibility', 'visible');
	    } else {
	        $('[id="' + domId + '_Parent"]').css('visibility', 'hidden');
	    }
	}
	//$('#' + domId + '_wrapper').css('width', '100%');
	if(typeof dataTableOptions != 'undefined'){
		if ((typeof dataTableOptions.role !== 'undefined') && (dataTableOptions.role == 'Attachment')) {
		    $('#' + domId + '_wrapper').css('width', '700px');
		    hideDataTableApiImpExtras(domId);
		} else if (typeof dataTableOptions.hideExtras !== 'undefined') {
		    hideDataTableApiImpExtras(domId);
		}
		if (typeof dataTableOptions.actionButtons !== 'undefined') {
		    $('#' + domId + '_wrapper').css('width', '100%');
		    if ((typeof dataTableOptions.role !== 'undefined') && (dataTableOptions.role == 'Attachment')) {
		        $('#' + domId + '_wrapper').css('width', '700px');
		    }
		}
		if (typeof dataTableOptions.isMandatory !== 'undefined') {
		    $('#' + domId).attr('required', 'required');
		} else {
		    $('#' + domId).removeAttr('required');
		}
	}

	//move the DIV _dataTableStructButtons inside table wrapper
    $('#' + domId + '_wrapper').prepend($('#' + domId + '_dataTableStructButtons'));
    //add tools button
    $('#' + domId + '_wrapper').prepend(' <div class="dropdown dropdown-button">\
			  <button type=\"button\" class=\"button ui-state-default dataTableApiButton dataTableApiButtonTools\"><i class="fa fa-cog" aria-hidden="true"></i></button>\
			  <div class="dropdown-content"></div></div>');  
    // move table display rows ddl after tableFilterControls
    $('#' + domId + '_tableFilterControls').append($('#' + domId + '_length'));
    
	// disable buttons (because the render empty)						 
	$('#' + domId + '_dataTableStructButtons button:not([dataTableApiTypeNew],.dataTableApiLabel)').addClass('disabledclass');
	elementDataTableApiImpBL(domId); // bl integration
	 $('#' + domId + '_wrapper').addClass("section-toggle-content"); 
	
	//show data table select div sections (hidden during the loading process)
    $('.datatableapiselectloadinglabel').each(function(){
    	$(this).removeClass('datatableapiselectloadinglabel');
    });

    changeElementDataTableApiCaption(domId); //kd 21122020 fixed bug-8707 part II, workaround - Label for Level not shown or had some value instead Choose
    
	hideWaitMessage();
	//initDivCount(domId);
}

function buildElementDataTableApi(obj, domId, dataTableOptions, triggerAjaxChangeFlag) {    
    if ($.isEmptyObject(obj)) { // check if obj is empty		
    	hideWaitMessage();
        return;
    }
    // init variables
    _ignoreFirstTfootTd ="";
    var dataTableOptionsFlag = false,
        role = "",
        object = {},
        pageLength = "",
//        ignoreFirstTfootTd = "",
        valueArray,
        checked = "",
        chkDisabled = "",
        followingHiddenCols = "",
        followingHiddenColsInt = 0,
        i = "",
        exportsArray,
        table,
        onclickScript = "",
        isHidden = "",
        uniqueValue = "",
        uniqueColumn = 2,
        column = "",
        columnData = "", //  - >data
        columnDataLength = "",
        doOnChangeJSCall = "",
        actionButtons = "",
        hideExtras = "",
        visibleFalseArray = [],
        isDisabled = "",
        isMandatory = "",
        objColsLength = obj.columns.length,
        oneRowOnly = "",
        maximumRows = "",
    	cellData,
    	colIsVisible,
    	colIsSearchable,
    	colIsOrderable,
    	orderByColName = "", 
    	orderByColAsc,
    	disallowRemoveColumns = false,
    	displayColumnsWhenNoRows = false,
    	isTableEditable = false,
    	colSearchObj = {}, 
        colFilterObj = {};
    
    if (typeof dataTableOptions !== 'undefined') {
        dataTableOptionsFlag = true; // set flag if dataTableOptions exists
    }
    // check if options exists inside the dataTableOptions object and use variables for more readable code
    if (dataTableOptionsFlag) {
        if (typeof dataTableOptions.role !== 'undefined') {
            role = dataTableOptions.role;
        }
        if (typeof dataTableOptions.followingHiddenCols !== 'undefined') {
            followingHiddenCols = dataTableOptions.followingHiddenCols;
            followingHiddenColsInt = (isNaN(followingHiddenCols)) ? 0 : Number(followingHiddenCols);
        }
        if (typeof dataTableOptions.pageLength !== 'undefined') {
            pageLength = dataTableOptions.pageLength;
        }
        if (typeof dataTableOptions.isHidden !== 'undefined') {
            if (dataTableOptions.isHidden.toLowerCase() == "false") {
                isHidden = "false";
            } else {
                isHidden = "true";
            }
        }
        if (typeof dataTableOptions.uniqueValue !== 'undefined') {
            uniqueValue = $('#' + dataTableOptions.uniqueValue).val();
            if (typeof dataTableOptions.uniqueColumn !== 'undefined') {
                uniqueColumn = dataTableOptions.uniqueColumn;
            }
        }
        if (typeof dataTableOptions.doOnChangeJSCall !== 'undefined') {
            doOnChangeJSCall = dataTableOptions.doOnChangeJSCall;
        }
        if (typeof dataTableOptions.actionButtons !== 'undefined') {
            actionButtons = dataTableOptions.actionButtons;
        }
        if (typeof dataTableOptions.hideExtras !== 'undefined') {
            hideExtras = dataTableOptions.hideExtras;
        }       
        if (typeof dataTableOptions.isDisabled !== 'undefined') {
            isDisabled = dataTableOptions.isDisabled;
        }
        if (typeof dataTableOptions.isMandatory !== 'undefined') {
            isMandatory = dataTableOptions.isMandatory;
        }
        if (typeof dataTableOptions.oneRowOnly !== 'undefined') {
            oneRowOnly = dataTableOptions.oneRowOnly;
        }
        if (typeof dataTableOptions.maximumRows !== 'undefined') {
            maximumRows = dataTableOptions.maximumRows;
        }
        if (typeof dataTableOptions.orderByColumnName !== 'undefined') 
        {
        	orderByColName = dataTableOptions.orderByColumnName;
        	if (typeof dataTableOptions.orderByColumnAsc !== 'undefined') 
            {
        		orderByColAsc = dataTableOptions.orderByColumnAsc;
            }
        }
        if (typeof dataTableOptions.displayColumnsWhenNoRows !== 'undefined') {
            if (dataTableOptions.displayColumnsWhenNoRows.toLowerCase() == "true") {
            	displayColumnsWhenNoRows = true;
            } 
        }
        if (typeof dataTableOptions.isTableEditable !== 'undefined') {
            if (dataTableOptions.isTableEditable.toLowerCase() == "true") {
            	isTableEditable = true;
            } 
        }
        if (typeof dataTableOptions.disallowRemoveColumns !== 'undefined') {
        	if (dataTableOptions.disallowRemoveColumns.toLowerCase() == "true") {
        		disallowRemoveColumns = true;
            }
        }
    }
    
    console.log("---------------- START build table " + domId);
    console.time("buildElementDataTableApi TIME on table: " + domId);

    // move dataTableStructButtons after tableFilterControls (outside of the table wrapper) because we destroy the table
	$('#' + domId + '_length').remove();
	$('#' + domId + '_tableFilterControls').after($('#' + domId + '_dataTableStructButtons'));
	
    cleanDataTable(domId); // clean,destroy and unbind old data and events from the datatable
	
    $('[name="' + domId + '_metaData"]').val('[' + obj.metaData + ']'); //save the metadata for future use

    $('[id="' + domId + '"]').append(buildTfootByLength(objColsLength)); // append tfoot to the datatable	

    //When there is no data, we need to copy the first column because we always hide the first column.
    if ((objColsLength == 1) && (role != 'Multiple')) {
        obj.columns = obj.columns.concat(obj.columns);
    }
      
    var _savedObject = [];
    var _savedObjectValue = $('[id="' + domId + '_userLastSaveSettings"]').val();
//    console.log(domId, " savedObjectValue: " + _savedObjectValue);
    try{
    	if(_savedObjectValue != null && _savedObjectValue.length > 0) {
    		_savedObject = JSON.parse(_savedObjectValue);
    	}
    } catch (e) {
    	console.log("error in eval _savedObject. e = " + e);
    }
    
    /** DATATABLE INITIAL OPTIONS */
    var isTableMergeable = false;
    var isTableResizable = bl_isTableResizable(domId);     
    var isTableDrawUpgraded = bl_isTableDrawUpgraded(domId);
    var isTableReorderable = false;
    var tableDom = 'Blfrtip';    
    //TODO: set configuration in Form Builder
    if($('#formCode').val() == 'SelfTest' || $('#formCode').val() == "Request" ||  $('#formCode').val() == 'ReportDesignExp')
    {
    	tableDom = 'Bfrtip';
    	object.pageLength = 5;
    }
    object.data = obj.data;
    object.columns = obj.columns;
    object.order = []; // disabled auto sorting 
    object.bDestroy = true; // datatable can be destory
    object.deferRender = bl_isDeferRender(domId); //When set to true, will cause DataTables to defer the creation of the table elements for each row until they are needed for a draw
    object.dom = tableDom;
    object.pagingType = 'full_numbers';  
    object.colReorder = {enable: false};
    object.buttons = getDTReportsConfig(domId);
    
    // var used by 'Column Reorder' & 'Column Resize' features: by default first column is always hidden in the most cases and property 'followingHiddenCols' is not defined in case there is only one hidden column
    var followingHdnColsIntUpdated = (followingHiddenColsInt == 0)?1:followingHiddenColsInt; 
    if(obj.data != null && obj.data.length > 0)
    {    	
	    /** init TABLE REORDER (before smartRender() call) */
		isTableReorderable = bl_isTableReorderable(domId);
		if(isTableReorderable) {
			object.colReorder = {enable: true, fixedColumnsLeft: followingHdnColsIntUpdated};
		}		
    }

    /** TABLE PAGE LENGTH MENU */
    if (pageLength) { //if pageLength option is exists
        object.pageLength = parseInt(pageLength);
        object.lengthMenu = JSON.parse(dataTableOptions.lengthMenu);
        //object.language = {lengthMenu : "Entries per table _MENU_ "};
    }
    if ($('#' + domId + '_lastPageLength').val() != '') { //use last Page Length (use because of the ajax)
        object.pageLength = parseInt($('#' + domId + '_lastPageLength').val());
    }        
//    for developer purpose
//    object.pageLength = 2;
//    object.lengthMenu = [2,4,10];

    
    /** COLUMNS DEFINITION (initial) */
    object.columnDefs = [{
        "targets": [0],
        "visible": false
    }]; // we always hide the first column. This property is often being overridden
    
    // ab 23052019: datatable property "defaultContent" - creates static content for a column in case there is undefined data for this column (data not exists)
    //object.columnDefs.push({ "defaultContent": getCellDefaultContent(domId), "targets": "_all" });  
    
    if (role == 'Attachment') {
        object.columnDefs = [{
            "targets": [0, 1, 2, 3, 4, 5],
            "visible": false
        }, {
            "targets": [6],
            "width": "20%",
            "className":"word-break",
            "render": function (data, type, full, meta) {
            	var type = "";
            	
        		if($("table[id='"+domId+"']").attr("disableeditable")!= undefined && $("table[id='"+domId+"']").attr("disableeditable")=="0"){
        			type = full[8];
        		}else{            			
            		type = full[7];
            	}            		            
            	return '<a onclick="openAttachmentElementDataTableApiImp(\'' + data + '\',\'' + full[1] + '\',\'' + type + '\',\'' + domId + '\');event.stopImmediatePropagation();" >' + data + '</a>';
            	
            		
            }
    	}];
    }

    //if followingHiddenCols options is exists
    if (followingHiddenCols) {
        if (role != "Attachment") {
            object.columnDefs = [];
            for (i = 0; i < followingHiddenColsInt; i++) {
                visibleFalseArray.push(i);
            }
            object.columnDefs.push({
                "targets": visibleFalseArray,
                "visible": false
            });
        }
    }
    
    /** COLUMNS DEFINITION: DATA RENDER BY 'CREATED CELL' dt function */
    if(role.indexOf('Multiple')==-1)//role != 'Multiple'
    {        
    	smartRender(object, followingHiddenCols, domId, _savedObject, isTableResizable);
    } 
    else //(role == 'Multiple') 
    {
    	var colIndex = null;
    	var disabledListArray= $('#'+ domId + '_disabledList').val().split(',');
    	//console.log(object.columns);
    	
    	_ignoreFirstTfootTd = ":not(:first)";
        object.columnDefs = (followingHiddenColsInt == 0) ? [] : object.columnDefs;
        //console.log("followingHiddenColsInt:"+followingHiddenColsInt);
        if (obj.columns.length > followingHiddenColsInt) {
            obj.columns[followingHiddenColsInt].title = '';
        }
             
        object.columnDefs.push({
            "targets": [followingHiddenColsInt],
            "width": "1%",
            "className": "dt-center",
            'searchable': false,
            'orderable': false,
            "render": function (data, type, full, meta) {
                valueArray = $('#' + domId + '_value').val().split(',');
                checked = "";
                chkDisabled = "";
                if (valueArray.indexOf(data) > -1) {
                    checked = "checked";
                }
                if (disabledListArray.indexOf(data) > -1) {
                	chkDisabled = "disabled";
                }
                return '<input type="checkbox" value="' + $('<div/>').text(data).html() + '" ' + checked + '  '+chkDisabled+' onclick="smartSelectStateMng(\'' + domId + '\',false,this.checked,1)">';
            }
        });
        //in multiple type there will have a possibility to select all
        if(object.columns.length>followingHiddenColsInt){//if the table has data
	        var checked = '';
	        var chkDisabled = '';
	        object.columns[followingHiddenColsInt].title = '<input type="checkbox" ' + checked + '  '+chkDisabled+' id="chbSelectAllNone_'+domId+'" onclick="smartSelectStateMng(\'' + domId + '\',true,this.checked,1)">';
        }
        //ab 10012018: added call to next function because of special screen Sample->Results tab that contains smartlinks inside Multiple role table 
        smartRender(object, followingHiddenCols, domId, _savedObject, isTableResizable);
    }
    
    /** DEFINE SORT,SEARCH, COLUMNS ORDER, COLUMNS WIDTH BY SAVED USER SETTINGS
 		DEFINE IF TABLE HAS ABILITY TO BE MERGED, RESIZED, REORDERED */
    // if table has data
    if(obj.data != null && obj.data.length > 0)
    {
    	var objectColumnsLength = object.columns.length;

    	// set table SORT by settings from FormBuilder        
        if(orderByColName != "")
        {
        	var objColTitle, orderByColIndex, orderType = "";
        	for (var i = 0; i < objectColumnsLength; i++) 
    	    {
    	        objColTitle = object.columns[i].title;
    	        if(objColTitle.toUpperCase() == orderByColName.toUpperCase())
            	{
    	        	orderByColIndex = i;
    	        	orderType = (orderByColAsc.toLowerCase() == 'true')?"asc":"desc";
    	        	object.order = [[i, orderType]];
    	        	//console.log(object);
    	        	break;
            	}
    	    }
        }    	
    	
    	//init TABLE MERGE
    	isTableMergeable = bl_isTableMergeable(domId);
    	if(isTableMergeable)
        { 
    		var columnsIndex = [];
    		for(i = followingHiddenColsInt; i<objectColumnsLength;i++){
    			columnsIndex.push(i);
    		}
    		object.rowsGroup = columnsIndex;
        }    	
    	
    	if(isSameStructTable(domId))
    	{
    		try 
    		{
        		///////// SORT /////////
        		var sortArr = [];
        		var innerArr = (_savedObject.length > 0)?_savedObject[0].sort:[];
        		var sortInd = 0;
        		
        		for (var i=0;i < innerArr.length;i++)
        		{
        			var _title = innerArr[i][0];
        			var _sortType = innerArr[i][1];
        			for(var j=0;j < objectColumnsLength;j++)
            		{
            			var colInd = j;
        				var _newTitle = object.columns[j].uniqueTitle;
            			if(_title == _newTitle)
            			{
            				sortArr[sortInd++] = [colInd,_sortType];
            				break;
            			}
            			
            		}
        		}
        		
        		///////// SEARCH /////////
        		colSearchObj = (_savedObject.length > 0)?_savedObject[0].columnSearch:{};
        		var colSearchObjLength = Object.keys(colSearchObj).length;
        		var _allColumnsSearch = [];    		
        		var allColumnsNameIndexObj = {};// filled by column titles for COLUMN ORDER
        		
        		colFilterObj = (_savedObject.length > 0)?_savedObject[0].columnFilter:{};
        		if(_savedObject.length > 0 && _savedObject[0].columnFilter!=undefined){
        			globalDataTableFilterColumn[domId] = _savedObject[0].columnFilter;
        		}
        		
        		var colFilterObjLength = colFilterObj!=undefined?Object.keys(colFilterObj).length:0;
        		if(colSearchObjLength > 0 || colFilterObjLength > 0 || isTableReorderable)
        		{
	        		// loop through all columns
	        		for(var j=0;j < objectColumnsLength;j++)
	        		{
	        			var _col = object.columns[j];
	        			var _title = _col.uniqueTitle;
	        			allColumnsNameIndexObj[_title] = j; 
	        			
	        			if(colSearchObjLength > 0 || colFilterObjLength > 0)
	    				{
	    					if(colSearchObjLength > 0&& colSearchObj.hasOwnProperty(_title))
	    					{
	    						_allColumnsSearch.push(colSearchObj[_title]);
	    					}
	    					/*else if(colFilterObjLength > 0&& colFilterObj.hasOwnProperty(_title)){
	    						//var val =  colFilterObj[_title].join('\$|\^');//arr.join('\$|\^')
	    						//_allColumnsSearch.push({"search":val,bRegex: true});//,
	    					}*/
	    					else
	    					{
	    						_allColumnsSearch.push({"search":""});	
	    					}
	    				} 
	        		}
        		}
        		object.order = sortArr;
                object.searchCols = _allColumnsSearch;
                
                //////// COLUMN ORDER //////////////
                if(isTableReorderable)
                {
    	            /* get last saved value*/
    			    var colsNamesArr = getSavedColumnsOrderArray(domId, _savedObject);    			    
    	            if(colsNamesArr && colsNamesArr.length > 0)
    	            {
    					var colsNamesArrUpdated = [];
    					var cnt = 0;
    					var newOrderArr = [];
    									
//    					console.log(domId+" allColumnsNameIndexObj: ",JSON.stringify(allColumnsNameIndexObj));
    					
    					// update colsNamesArr for remove columns that was saved but does not contains already in the table
    					for(var i=0;i<colsNamesArr.length;i++)
    					{
    						var n = colsNamesArr[i];
    						if(allColumnsNameIndexObj.hasOwnProperty(n)) {
    							colsNamesArrUpdated[cnt++] = n;
    						}
    					}
//    					console.info(domId+" colsNamesArrUpdated: ",colsNamesArrUpdated);
    					
    					newOrderArr = dtGetMergedColumnsOrder(domId, objectColumnsLength, allColumnsNameIndexObj, colsNamesArrUpdated);    	
    					object.colReorder.order = newOrderArr;
    					
    	            }
                }
                
        	} catch (e) {
        		console.error("error in getting data from _savedObjectValue. e = " + e);
        	}
    	}
    }
 // END if table has data
    
    /** TABLE CALLBACKS FUNCTION */
    object.drawCallback = function( settings ) 
    {
    	favoriteHeaderMng(domId);
    	//console.log("---"+domId + " DataTables drawCallback.");
    	if($.inArray(domId, _smartSelectTablesArray) != -1 || $("#"+domId+"_role").val().indexOf("Multiple")!="-1")
        {
        	smartSelectStateMng(domId,false,null);
        }
    	
    	if(isTableEditable) 
    	{
//    		console.log(domId + " DataTables drawCallback.");   		
    		console.time("time on table drawcallback");
    		
    		var $table = $('table[id="'+domId+'"]');
    		
    		$table.find('.editableCell.dragAndDrop').each(function(){
    			initDragAndDropHadle($(this));
    		});
    		
    		$table.find('input.editableSmartCell.date-picker').each(function() 
	        {
		          if(!$(this).hasClass('hasDatepicker'))
		          {
		        	   initDatePickerWithOptionsByClass('date-picker',{beforeShow:onEditableTableEvent});
		          }
	        });

//    		var t0 = performance.now();
//    		console.time("----- time on table datepicker-inline");
    		$table.find('input.editableSmartCell.datepicker-inline').each(function() 
	        {
		          if(!$(this).hasClass('hasDatepicker'))
		          {
		        	   initDatePickerWithOptionsByClass('datepicker-inline',{beforeShow:onEditableTableEvent,showOn:"focus"});
		          }
	        });
//    		var t1 = performance.now();
//    		console.log("datepicker-inline TABLE find each " + (t1 - t0) + " milliseconds.");
//    		console.timeEnd("----- time on table datepicker-inline");
    		

//    		console.time("--CHOSEN TIME");
    		$table.find('select.editableSmartCell').each(function() 
	    	{
	    		var id = $(this).attr('id');
	    		if($(this).next('div.chosen-container').length == 0)
	    		{
	    			 $("#"+id).on("chosen:ready", function() {
	    	    	    $(this).next('div.chosen-container').on('keyup', function(e) {
	    	    	    	dtExt_manageTableNavigation($(this), e);
	    	    	    });
	    	    	    $(this).next('div.chosen-container').on('keydown', function(e) {
	    	    	    	dtExt_manageTableNavigation($(this), e);
	    	    	    });
	    	    	    if(this.hasAttribute('isLink')) 
	    	    	    {
	    	    	    	var $this = $(this);
	    	    	    	var $optionSelected = $this.find("option:selected");
	    	    	    	//console.log($this.prop('selectedIndex'),$optionSelected.val());
	    	    	    	if($this.prop('selectedIndex') > 0) 
	    	    	    	{
		    	    	    	var _formId = ($optionSelected.length>0?$optionSelected.val():$this.val());
		    	    	    	var _formCode = $this.attr('formCode');
	    	    	    		var attr = [''+_formId+'' ,''+ _formCode+'','', true];	
	    	    	    		var $chosenDiv = $this.next('div.chosen-container');
	    	    	    		$($chosenDiv).find('a').attr('contextmenu_data','["' + _formId + '","' + _formCode + '"]');
		    	    	    	$($chosenDiv).find('span')
			    	    	    	.addClass('linkElement')
			    	    	    	.on('click', function(){
			    	    	    		checkAndNavigate(attr);
			    	    	    	});
	    	    	    }
	    	    	    }
	    	    	});
	    			$("#"+id).on("change", function() {
	    				if(this.hasAttribute('isLink')) 
	    	    	    {
	    					var $this = $(this);
	    					var $chosenDiv = $this.next('div.chosen-container');
	    					//console.log("selectedIndex",$this.prop('selectedIndex'));
	    					if($this.prop('selectedIndex') > 0) 
	    	    	    	{
	    						var _formId = $this.val();
	    						var _formCode = $this.attr('formCode');
	    						var attr = [''+_formId+'' ,''+_formCode+'','', true];
	    	    	    		$($chosenDiv).find('a').attr('contextmenu_data','["' + _formId + '","' + _formCode + '"]');
		    	    	    	$($chosenDiv).find('span')
			    	    	    		.addClass('linkElement')
				    	    	    	.off('click')
				    	    	    	.on('click', function(){
				    	    	    		checkAndNavigate(attr);
				    	    	    	});
		    	    	    }
	    					else
	    					{
	    						$($chosenDiv).find('a').attr('contextmenu_data','');
	    						$($chosenDiv).find('span')
		    	    	    		.removeClass('linkElement')
		    	    	    		.off('click');
	    					}
	    	    	    }
	    			});
	    			var _width = (this.hasAttribute('chosen_width'))?$(this).attr('chosen_width'):"100%";
	    			var chosen_config = {
		    				allow_single_deselect:(this.hasAttribute('disallowSingleDeselect')?false:true) ,
					    	search_contains:true,
					    	width:_width
		    		};
	    			
		    		if(this.hasAttribute('maxShownResults'))
		    		{
		    			chosen_config["max_shown_results"] = $(this).attr('maxShownResults');
		    		}
		    		
		    		$('#'+id).chosen(chosen_config);
	    		}
	    	});  
//    		console.timeEnd("--CHOSEN TIME");
    	
    		$table.find('textarea.editableSmartCell.autogrow_marker').autogrow({onInitialize: true});
    		$table.tableNavigation();
    		$table.tableEditableDivEvents();
    		
    		//TODO: Alex -> remove from here to app.css
    		  $('table[id="'+domId+'"]').find("div.editableSmartCell").focusin(function(){
	  	    	  $(this).css("border-color", "#1c91cd");	        
	  	      })
	  	      .focusout(function(){
	  	      		$(this).css("border-color", "#ced4da");	
	  	      });
    		  $('table[id="'+domId+'"]').find('input.editableSmartCell[type="checkbox"]').focusin(function(){
	  	    	$(this).css('cssText', 'outline: 1px solid #1c91cd !important');
	  	      })
	  	      .focusout(function(){
	  	    	$(this).css('cssText', 'outline: none');
	  	      });
    		  
	  	    // init alphanum
			if(domId == "action"){
				  initAlphaNumEditable(domId,',');
			}else{
				  initAlphaNumEditable(domId);
			}
			
	  	    
	  	  console.timeEnd("time on table drawcallback");
    	}
    	
    	addSummaryLine(domId);
    }; 
   
    /* add tooltip to whole row: required appropriate changes in the view that brings data for current table */
    object.createdRow = function ( row, data, index ) 
	{		
    	$.each(data, function (i, o) 
        {   		
    		if (checkIfJSON(o))
    		{ 
    			try 
    			{
    				var curObj;
    				var jsonData = funcParseJSONData(o,true);
                    if(jsonData instanceof Array)
                	{
                    	jsonObject = jsonData;
                	}
                    else
                	{
                    	jsonObject = funcParseJSONData("["+o+"]",true);
                	}
    				
                    for(var y=0; y<jsonObject.length;y++)
                	{
                    	curObj = jsonObject[y];
                    	if((curObj.objectType) && curObj.objectType == 'SMARTTOOLTIP')
        				{    				
        					$(row).attr( 'title', curObj.val);    				
        				}
                	}
    				
    			} catch(Err) 
    			{
    				console.log("error in createdRow: " + o + ". Err=" + Err);
//    				displayAlertDialog(o);
    			}
            } 
        });
	}
    
    object.headerCallback = function( nHead, aData, iStart, iEnd, aiDisplay ) 
    {
    	//console.log(domId + " DataTables headerCallback."); 
    	//console.log(nHead);
    	
    	if(!isTableDrawUpgraded)
    	{
	    	//important! set 'uniqueTitle' as header attribute to use it when build table footer(tfoot)
	    	$('#' + domId).DataTable().columns().iterator('column', function ( settings, column) 
	    	{
	    		//console.log(column);
	    		//console.log(settings.aoColumns[ column ]);
	    		var _thisColumn = settings.aoColumns[ column ];
	    		var _uTitle = "";
			    if (_thisColumn.uniqueTitle !== undefined) 
			    {
			    	_uTitle = _thisColumn.uniqueTitle;
			    }
			    else
			    {
			    	_uTitle = _thisColumn.title;
			    }
			    $($('#' + domId).DataTable().column(column).header()).attr('uniqueTitle', _uTitle);
			  });
    	}
    	
	};	
	
	//register an custom search event for every column	
    var i = 0;
    /* 
     * '_inputObjArr' array used to store input objects that have select option different from "Contain"
     *  used later for trigger up that inputs
     */
    var _inputObjArr = [];  
    /*
     * '_delayCounterForInputs' used to store delay time(in miliseconds) 
     * it's value increased by number of columns that have select option different from "Contain"
     * later it's used for set table page to right position
     */
    var _delayCounterForInputs = 0;
	
	object.initComplete = function(settings, json)
    {    	
		if(isTableDrawUpgraded)
		{	    	
			/** loop through all columns */
			/*note: this.api().columns().every() takes more time (~twice), because loop through header and footer nodes also*/			
			console.time("COLUMN ITERATOR");
	    	if($('[id="' + domId + '"] tfoot th' + _ignoreFirstTfootTd).length > 0) 
	    	{
	    		var removedColNameArr = $('[id="' + domId + '_colsArray"]').val().split('@');
	    		var dtColumnsApi = this.api().columns();
				dtColumnsApi.iterator('column', function ( settings, columnIndex) {
		            
		            var column = this.column(columnIndex);
		            var searchExp = '';
		            var ddlSearchOpt = 'co';
			    	var _uTitle = "";		    	
			    	var column_settings = settings.aoColumns[ columnIndex ];
			    	//console.log("column_settings",column_settings);
			    	
			    	/** set attribute 'uniqueTitle' for the table header */
				    if (column_settings.uniqueTitle !== undefined) 
				    {
				    	_uTitle = column_settings.uniqueTitle;
				    }
				    else
				    {
				    	_uTitle = column_settings.title;
				    }
				    $(column.header()).attr('uniqueTitle', _uTitle);
				    /****/

				    /** create and append search row for all columns that should be visible (before custom settings takes place)  */
				    if(column_settings.bVisible)
			    	{			    	
				    	try 
				    	{				    		
				    		if(colSearchObj !== undefined && Object.keys(colSearchObj).length > 0)
				    		{
				    			if(colSearchObj.hasOwnProperty(_uTitle))
				    			{
				    				searchExp = colSearchObj[_uTitle].search;
				        			ddlSearchOpt = colSearchObj[_uTitle].ddlSearch;
				    			}
				    			else
				    			{
				    				searchExp = '';
				    		    	ddlSearchOpt = 'co';
				    			}
				    		}
				    		
				    	} catch (e) {
				    		console.log("error in eval searchExp. (init tfoot of table "+domId+") e = " + e);
				    	}    
				    	
				    	$(column.footer()).append(
				                '<select style="display: block;" onchange=\"optionChanged(this)\" class=\"datatableapiselect\">\n' + 
				                '<option title=\"Contain\" value=\"co\" '+((ddlSearchOpt == 'co')?"selected":"")+'>|*|</option>\n' + 
				                '<option title=\"Not Contain\" value=\"cn\"  '+((ddlSearchOpt == 'cn')?"selected":"")+'>| |</option>\n' + 
				                '	<option title=\"Equal\" value=\"eq\"  '+((ddlSearchOpt == 'eq')?"selected":"")+'>=</option>' + 
				    			'	<option title=\"Not Equal\" value=\"ne\"  '+((ddlSearchOpt == 'ne')?"selected":"")+'>&lt;&gt;</option>' + 
				    			'	<option title=\"Greater than\" value=\"gt\"  '+((ddlSearchOpt == 'gt')?"selected":"")+'>&gt;</option>' + 
				    			'	<option title=\"Greater than or Equal\" value=\"ge\"  '+((ddlSearchOpt == 'ge')?"selected":"")+'>&gt;=</option>' + 
				    			'	<option title=\"Less than\" value=\"lt\"  '+((ddlSearchOpt == 'lt')?"selected":"")+'>&lt;</option>' + 
				    			'	<option title=\"Less than or Equal\" value=\"le\"  '+((ddlSearchOpt == 'le')?"selected":"")+'>&lt;=</option>' +
				                '</select>\n' 
				    			+ 
				    			'<input value=\"'+ searchExp +'\" type=\"text\" class=\"firstString\" formCode=\"' + domId + '\" onkeypress=\"if ( event.which == 59 || event.keyCode == 59) return false;\">'
				         );
				    	
				    	/****/
			    	}
				    
				    /** update columns visibility (set to false) from custom settings */
//				    if(isSameStructTable(domId))
				    {
					    try
					    {						    
						    if( $.inArray(_uTitle, removedColNameArr) != -1) {
						    	
						    	column.visible(false,false);
						    }
					    }
					    catch(e)
					    {
					    	console.log(e);
					    }
				    }
				    /****/
				    
				    if(column_settings.bVisible)
				    {
				    	/** for visible fields only  
				    	 * fill array with search fields that should be triggered after datatable initialization */
				    	
				    	if(ddlSearchOpt != "co" && searchExp != "")
				        {
				        	_inputObjArr[i++] = $(column.footer()).find('input[class="firstString"]');
				        }
				    	/****/
				    }
		        } );
				
				/** append footer search row to header */
				var $header = $('[id="' + domId + '"] thead');
				$header.append($('[id="' + domId + '"] tfoot tr'));	
				
				bl_dtHeaderRenderCustom(domId, dtColumnsApi, $header);
	    	}
			
			console.timeEnd("COLUMN ITERATOR");
 			
//			console.log("---"+domId + " DataTables has finished its initialisation.");  
    	}
		
    	$('#hiddenformMessage_'+ domId).hide();  
    };
    
    /*DataTables handles errors options of errMode: alert -(default)Alert the error; throw - Throw a Javascript error; none - Do nothing*/
    //$.fn.dataTable.ext.errMode = 'none';
   
    //CREATE datatable
    table = $('[id="' + domId + '"]')
		    .on( 'error.dt', function ( e, settings, techNote, message ) {
		        console.log( 'An error has been reported by DataTables: ', message );
		    } )
		    .DataTable(object);
   
    //Log time taken to draw the page 
    var startTime;    
    table
        .on( 'preDraw', function () {
            startTime = new Date().getTime();
        } )
        .on( 'draw.dt', function () {
            console.log( 'Redraw took at: '+(new Date().getTime()-startTime)+'mS' );
        } );
    
    
    if(isTableReorderable)
    {
	    table.on( 'column-reorder', function ( e, settings, details ) {	        
	    	console.log("column-reorder details: ", details);	
	    } );
    }
    if(isTableResizable)
    {	   	    
    	table.on("column-resized.dt", function(event, columnIndex, newColumnWidth) {
//    		console.log("column-resized: colNum -> " + columnIndex,newColumnWidth);
    		
    		table.colResize.redraw();
    		initFixedHeaders(domId);
	    });	 
    	table.on( 'column-sizing.dt', function ( e, settings ) {
    	    console.log( 'Column width recalculated in table' );
	    });	    
    }
    
    if(bl_isTableHasContextMenu(domId, role))
    {
    	$('table[id="'+domId+'"]').tableContextMenu({tableID:domId,tableDT:table});
    }
    
    if(!isTableDrawUpgraded)
    {
    	//init tfoot    
	    $('[id="' + domId + '"] tfoot th' + _ignoreFirstTfootTd).each(function (i) {
	    	
	    	var $tableTHObj = $(this.parentElement.parentElement.parentElement.tHead).find('tr').find('th');
	    	//var _thisTitle = $($tableTHObj[(_ignoreFirstTfootTd==''?i:i+1)]).text();
	    	var _thisTitle = $($tableTHObj[(_ignoreFirstTfootTd==''?i:i+1)]).attr('uniqueTitle');
	    	
	    	var searchExp = '';
	    	var ddlSearchOpt = 'co';
	    	try 
	    	{
	    		if(colSearchObj !== undefined && Object.keys(colSearchObj).length > 0)
	    		{
	    			if(colSearchObj.hasOwnProperty(_thisTitle))
	    			{
	    				searchExp = colSearchObj[_thisTitle].search;
	        			ddlSearchOpt = colSearchObj[_thisTitle].ddlSearch;
	    			}
	    			else
	    			{
	    				searchExp = '';
	    		    	ddlSearchOpt = 'co';
	    			}
	    		}
	    		
	    	} catch (e) {
	    		console.log("error in eval searchExp. (init tfoot of table "+domId+") e = " + e);
	    	}    	
	        $(this).html(
	            '<select style="display: block;" onchange=\"optionChanged(this)\" class=\"datatableapiselect\">\n' + 
	            '<option title=\"Contain\" value=\"co\" '+((ddlSearchOpt == 'co')?"selected":"")+'>|*|</option>\n' + 
	            '<option title=\"Not Contain\" value=\"cn\"  '+((ddlSearchOpt == 'cn')?"selected":"")+'>| |</option>\n' + 
	            '	<option title=\"Equal\" value=\"eq\"  '+((ddlSearchOpt == 'eq')?"selected":"")+'>=</option>' + 
				'	<option title=\"Not Equal\" value=\"ne\"  '+((ddlSearchOpt == 'ne')?"selected":"")+'>&lt;&gt;</option>' + 
				'	<option title=\"Greater than\" value=\"gt\"  '+((ddlSearchOpt == 'gt')?"selected":"")+'>&gt;</option>' + 
				'	<option title=\"Greater than or Equal\" value=\"ge\"  '+((ddlSearchOpt == 'ge')?"selected":"")+'>&gt;=</option>' + 
				'	<option title=\"Less than\" value=\"lt\"  '+((ddlSearchOpt == 'lt')?"selected":"")+'>&lt;</option>' + 
				'	<option title=\"Less than or Equal\" value=\"le\"  '+((ddlSearchOpt == 'le')?"selected":"")+'>&lt;=</option>' +
	            '</select>\n' 
				+ '<input value=\"'+ searchExp +'\" type=\"text\" class=\"firstString\" formCode=\"' + domId + '\" onkeypress=\"if ( event.which == 59 || event.keyCode == 59) return false;\">'
	        );
	    });
	
	    $('[id="' + domId + '"] thead').append($('[id="' + domId + '"] tfoot tr')); // append the 'search row' to thead (the contain...)
	}

    if(!isTableDrawUpgraded)
    {
	    table.columns().every(function (index) {
	        var that = this;
	        var input = $(that.footer()).find('input[class="firstString"]');
	        if($(input).length > 0)
	        {
		        var optVal = $(input).siblings('select').val();
		        //console.log(optVal,$(input).val());
		        if(optVal != "co" && $(input).val() != "")
		        {
		        	_inputObjArr[i++] = input;
		        }
	        } 
	    });
    }
    
    // Apply individual column filter
    searchDatatable(domId, table);
    
    
	for(var j=0; j<_inputObjArr.length;j++)
	{
		_delayCounterForInputs += 100;
		_inputObjArr[j].trigger('keyup');
	}
	if(globalDataTableFilterColumn!=undefined && globalDataTableFilterColumn[domId]!=undefined ){
    	searchSaveDisplay(domId);
	}

//	if(domId == "action") console.time("------- ACTION ADDITIONAL RENDER");
    
    //attaches an event handler to the lengthPage select,
    //we save the lengthPage number to use it after in AJAX call
    $('#' + domId + '_Parent select[name$="_length"]').on('change.dataChanged', function () {
        $('#' + domId + '_lastPageLength').val($('#' + domId + '_Parent select[name$="_length"]').val());
    });

    // hide/show table
    if (isHidden == "false") 
    {
        //$('[id="' + domId + '_Parent"]').css('visibility', 'visible');
        $('[id="' + domId + '_Parent"]').show();
    } 
    else if (isHidden == "true") 
    {
        //$('[id="' + domId + '_Parent"]').css('visibility', 'hidden');
        $('[id="' + domId + '_Parent"]').hide();
    }

    //disable add button when the table has unique value and its already exists in the table
    if (uniqueValue != "") {
        column = table.column(parseInt(uniqueColumn));
        columnData = column.data();
        columnDataLength = columnData.length;
        for (i = 0; i < columnDataLength; i++) {
            if (columnData[i] == uniqueValue) {
                $('#' + domId + '_dataTableStructButtons button.dataTableApiAdd').addClass('disabledclass');
                break;
            }
        }
    }

    if ((oneRowOnly != "") && (object.data.length > 0)) {
        $('#' + domId + '_dataTableStructButtons button.dataTableApiAdd').addClass('disabledclass');
        $('#' + domId + '_dataTableStructButtons button.dataTableApiNew').addClass('disabledclass');
    }
    
    else if ((maximumRows != "") && (!isNaN(maximumRows)) && (object.data.length > Number(maximumRows)-1)) {
        $('#' + domId + '_dataTableStructButtons button.dataTableApiAdd').addClass('disabledclass');
    }

    $('[id="' + domId + '"] tbody').off('click'); // unbind click event from the table tbody
    

    $('#' + domId + '_wrapper').addClass("section-toggle-content");
    
    //move the DIV _dataTableStructButtons inside table wrapper
    $('#' + domId + '_wrapper').prepend($('#' + domId + '_dataTableStructButtons'));
   
    //add tools button
    $('#' + domId + '_wrapper').prepend(' <div class="dropdown dropdown-button">\
			  <button type=\"button\" class=\"button ui-state-default dataTableApiButton dataTableApiButtonTools\"><i class="fa fa-cog" aria-hidden="true"></i></button>\
			  <div class="dropdown-content"></div></div>');  
   
    //append HTML5 datatable buttons to the tools button
    $('#' + domId + '_wrapper .dropdown-content:not(".rowAdditionCount")').append($('#' + domId + '_Parent .dt-buttons:first'));
    // move table display rows ddl after tableFilterControls
    $('#' + domId + '_tableFilterControls').append($('#' + domId + '_length')).append($('#' + domId + '_displayTopRows'));   

    //disable all buttons (accept the tools button)
    if (isDisabled) {
        $('#' + domId + '_Parent .dataTableApiButton:not(.dataTableApiButtonTools,.dataTableApiView,.dataTableApiLabel)').addClass('dataTableApiButtonDisabled').addClass('disabledclass');
    } else {
        $('#' + domId + '_Parent .dataTableApiButtonDisabled').removeClass('dataTableApiButtonDisabled').removeClass('disabledclass');
    }

    if (isMandatory) {
        $('#' + domId).attr('required', 'required');
    } else {
        $('#' + domId).removeAttr('required');
    }
    
    var timer = 0;
    var delay = 300;
    var preventClick = false;
    
    /* BEGIN --------- ONROWCLICK -------*/
    $('[id="' + domId + '"] tbody').on('click', 'tr', function (e) 
    {
    	if(displayColumnsWhenNoRows)
    	{
    		var table = $(this).closest('table').DataTable();
    		var recordsDisplay = table.page.info().recordsDisplay;
    		if(recordsDisplay == 0) return;
    	}
    	else if(object.data !== undefined && object.data.length == 0) return;
    	
    	
    	/*prevent from continue regular row click code, in case clicked cell has build-in html elements*/
    	if($(e.target).prop('tagName').toLowerCase() != 'td' && $(e.target).prop('tagName').toLowerCase() != 'tr'){
    		if(role != 'MultipleAjax' || $(e.target).prop('type') === undefined || $(e.target).prop('type').toLowerCase() != 'checkbox') {
    			return;
    		}
    	}
    	//in MultipleAjax role-> selecting a row affects on checked value	
    	if ($(this).hasClass('selected')) 
    	{
            $(this).removeClass('selected');
            if(role == 'MultipleAjax'){
            	var disabledListArray= $('#'+ domId + '_disabledList').val().split(',');
            	if(disabledListArray.indexOf($(this).find('input:checkbox').val()) == -1){
	            	$(this).find('input:checkbox').prop('checked',false);
	            	smartSelectStateMng(domId,false,false,1);
            	}
            }
        } 
    	else 
    	{
    		if(role != 'MultipleAjax'){//td clicking
	        	   var table = $(this).closest('table').DataTable();
		            table.$('tr.selected').removeClass('selected');
    		}
    		if(role == 'MultipleAjax' || (role == 'Multiple' && window.self !== window.top)){
    			var disabledListArray= $('#'+ domId + '_disabledList').val().split(',');
            	if(disabledListArray.indexOf($(this).find('input:checkbox').val()) == -1){
	    			$(this).find('input:checkbox').prop('checked',true);
	    			smartSelectStateMng(domId,false,true,1);
            	}
            }  
            $(this).addClass('selected');
        }
    	
    	if(e.originalEvent!== undefined && e.originalEvent.detail > 1)
    		return;
    	// important to save objects: 'this'&'event' before 'setTimeout' function, because they'll changed inside it.
    	var $this = $(this);
    	var eventO = e;
    	var currRowData = [];
    	
    	//console.log('fireClickAlongWithDblClick: ' + dataTableOptions.fireClickAlongWithDblClick);
    	if(dataTableOptions.fireClickAlongWithDblClick == 'true')
		{
    		delay = 0;
		}
    	
    	timer = setTimeout(function() 
    	{
  	      //console.log("_isTableRowLinkClicked: " + _isTableRowLinkClicked);
    	  if (!preventClick && !_isTableRowLinkClicked) 
  	      {	
  	    	  	console.log('click'); 	
  	    	  	var csvList_ = [];	
  	    	  	if(role == 'MultipleAjax'){//if the row was not checked then do not allow to remove it
			            
	  	    	  	var lastMultiValues = $('#' + domId + '_value').val(); // get the values from lastMultiValues that may contain selection that not appear in this filter (criteria) table
	  	        	if(lastMultiValues.length > 0) {
	  	        		csvList_ = lastMultiValues.split(',');
	  	        	} 
	  	        	var table_ = $('#' + domId).DataTable(); 
	  	            
	  	        	// add selected values that are not in lastMultiValues + remove unselected values that are in this table (=> the user unselected them)
	  	            table_.$('input[Type="checkbox"]').each(function (index) {
	  	            	if((',' + lastMultiValues + ',').indexOf(',' + $(this).val() + ',') <= -1) {
	  	            		if($(this).prop('checked')) {
	  	            			csvList_.push($(this).val());
	  	            		}
	  	            	} else {
	  	            		if(!$(this).prop('checked')) {
	  	            			//remove val from array
	  	            			const index = csvList_.indexOf($(this).val());
	  	            			csvList_.splice(index, 1);
	  	            		}
	  	            	}
	  	            });
  	    	  	}
  	    	//TODO:add handling in view/new/remove buttons when role='MultipleAjax'
  	    	  	if (($this.hasClass('selected')) && ($('[id="' + domId + '"]').DataTable().page.info().recordsDisplay > 0)) 
		        {
		            //($('[id="' + domId + '"]').DataTable().page.info().recordsDisplay > 0) is to prevent select row when there is no data
//		            var editButton = $('#editButton');
//		            if ((editButton.length == 0) || ((editButton.length) && (editButton.val() == "1"))) {
//		                // enable buttons on a row click(and prevent the enable on 'edit mode')
//		                $('#' + domId + '_dataTableStructButtons button:not([dataTableApiTypeNew], .dataTableApiButtonDisabled)').removeClass('disabledclass');
//		                allowCloneElementDataTableApi(domId);
//		            } // yp 13112018 - not needed after the open from in edit cahnge (from this version we removed all main edit butoons from the fomrs)

  	    	  		$('#' + domId + '_dataTableStructButtons button:not([dataTableApiTypeNew], .dataTableApiButtonDisabled,.dataTableApiLabel)').removeClass('disabledclass');
	  	    	  	if(role == 'MultipleAjax'){//if the row was not checked then do not allow to remove it
		  	            if(csvList_.toString()==''){//there are no checked rows
  		            		$('#' + domId + '_dataTableStructButtons button:not([dataTableApiTypeNew], .dataTableApiButtonDisabled, .dataTableApiLabel).dataTableApiRemove').addClass('disabledclass');
	            		}
	            	}
  	    	  		
  	    	  		currRowData = $('#'+domId).DataTable().row($this).data();// fix bug 7211
		        } 
		        else 
		        {
		            //disable buttons on rows blur
		            $('#' + domId + '_dataTableStructButtons button:not([dataTableApiTypeNew], .dataTableApiButtonDisabled, .dataTableApiLabel)').addClass('disabledclass');
		            if(role == 'MultipleAjax'){//if there are some checked rows then allow to remove them
		  	            if(csvList_.toString()!=''){//there are no checked rows
  		            		$('#' + domId + '_dataTableStructButtons button:not([dataTableApiTypeNew], .dataTableApiButtonDisabled, .dataTableApiLabel).dataTableApiRemove').removeClass('disabledclass');
	            		}
	            	}
		        }
		        //show preview when role is attachment
		        $('[id="' + domId + '_isPreview"]').val('1');

		        event = eventO;
		        eval($this.find('a[onclick*="openAttachment"]').attr('onclick'));
		        //console.log('doOnChangeJSCall: ' + doOnChangeJSCall + " role: " + role);
		        if ((doOnChangeJSCall != "") && ((role == "") || (role == "Search") || (role == "SharedAjax")|| (role == "MultipleAjax")/* || (role == 'smartLink')*/)) 
		        {
		        	eval(doOnChangeJSCall.trim().replace("(", "('").replace(")", "')").replace(",", "','"));
		        }
		        
		        generalBL_DTRowClickEvent(domId, currRowData, null);
  	      }  	    
	    }, delay);
    	preventClick = false;
  	    _isTableRowLinkClicked = false;
    })
    .off('dblclick') //method removes event handlers that were attached and prevent from fire double click more than one time 
    .on('dblclick', 'tr', function (e) 
    {
    	console.log('dblclick');
    	if(displayColumnsWhenNoRows)
    	{
    		var table = $(this).closest('table').DataTable();
    		var recordsDisplay = table.page.info().recordsDisplay;
    		if(recordsDisplay == 0) return;
    	}
    	else if(object.data !== undefined && object.data.length == 0) return;
    	
    	/*prevent from continue regular row dblclick code, in case clicked cell has build-in html elements*/
    	if($(e.target).prop('tagName').toLowerCase() != 'td') return;
    	
    	clearTimeout(timer);
    	if(dataTableOptions.fireClickAlongWithDblClick != 'true') //can be false or empty
		{
    		preventClick = true;
		}        
        if(!((typeof dataTableOptions !== 'undefined') && (typeof dataTableOptions.role !== 'undefined') && (dataTableOptions.role == 'Multiple')))
    	{
        	// prevent from dblclick:
        	//   - if edit mode(all buttons disabled before clicking on edit) 
        	//    or
        	//   - if edit/view button has one of the classes used for disable elements 
        	if(   
        		   //||
        		   (
        		    $('[tableid="' + domId + '"] button:contains("View")').is('.authorizationDisabled,.disablePage,.dataTableApiButtonDisabled')
        				   ||
        		    $('[tableid="' + domId + '"] button:contains("Edit")').is('.authorizationDisabled,.disablePage,.dataTableApiButtonDisabled')
        		   )
        	   ) { 
        	       return;
        	}  
        	 
        	$(this).addClass('selected');
        	if($('[tableid="' + domId + '"] button:contains("View")').is(":visible") || $('[tableid="' + domId + '"] button:contains("Edit")').is(":visible")) { // yp 20022021 add condition for fixing bug 8908 (this bug is in the reason I made it here and not in the customer bl for each table)
           	    $('[tableid="' + domId + '"] button:contains("View"),[tableid="' + domId + '"] button:contains("Edit")').click(); 
        	}
        	 if(typeof dataTableOptions !== 'undefined' && typeof dataTableOptions.role !== 'undefined' && dataTableOptions.role == 'Search')
         	{
             	if($('#save_').is(":visible")){
                	   $('#save_').click();
             	}
             }
        }
    });

    /* END --------- ONROWCLICK -------*/    
    
    //change table width by role    
    if (actionButtons) {
        if (role == 'Attachment') {
            $('#' + domId + '_wrapper').css('width', '700px');
            //$('#' + domId + '_wrapper').css('width','');
        } else {
            $('#' + domId + '_wrapper').css('width', '100%');
        }
    }
    // datatableapi bl integration
    if (!elementDataTableApiImpBL(domId)) {
        //dynamic table caption(caption use the struct name)
        $('#' + domId + '_Caption').html($('#' + domId + '_structCatalogItem').val());
    }
    
    //show data table select div sections (hidden during the loading process)
    $('.datatableapiselectloadinglabel').each(function(){
    	$(this).removeClass('datatableapiselectloadinglabel');
    });

    if (dataTableOptions.role == 'Attachment') { //kd 07042019 fixed bug-7382
	    $('#' + domId + '_wrapper').css({'overflow-x':'auto','overflow-y':'hidden'}); 
    }
    if($('#' + domId + '_showDragAndDrop').val()=="true" && $('div[data-parentelemet="'+domId+'"]').length>0)
	{
    	$('div[data-parentelemet="'+domId+'"]').parent().detach().appendTo('#'+domId+'_destinationDocUpload');
		$('div[data-parentelemet="'+domId+'"]').parent().css("display","inline-block");		
	}
    else if($('div[data-parentelemet="'+domId+'"]').length>0){
		$('div[data-parentelemet="'+domId+'"]').parent().css("display","none");
	}
    
    $( window ).resize(function() {
    	var width = 0;
    	var formsArr = ['Main','Step','ChemSearchTable','SelfTest','ExperimentCP','ExperimentFor'];
    	if($.inArray($('#formCode').val(), formsArr) < 0) // resize window cause UI bug when scroll located in main window
    	{
		    $('#' + domId + '_Parent').parents('td').siblings('td').each(function () {
		        width += $(this).width()
		    });
		    if ($('#' + domId + '_Parent .dataTableStructButtons').attr('buttonslayout') === 'Vertical') 
		    {		    	
		    	$('#' + domId + '_Parent').css('max-width', $('body')[0].clientWidth - width - 80 + 'px');
		    } 
		    else 
		    {
		    	if(window.self == window.top) {
		    		$('#' + domId + '_Parent').css('max-width', $('body')[0].clientWidth - width - 50 + 'px');}
		    }
    	}
    });
    
    $('.dt-buttons a').removeClass();
    $('.dropdown-content-search a,.dropdown-content-search a').removeClass();
    
    if(object.data !== undefined && object.data.length == 0 && !displayColumnsWhenNoRows) {
    	$('#' + domId + '_wrapper').find('div.DataTables_sort_wrapper').remove();
    }
    else if(!disallowRemoveColumns)
    {
    	//init remove columns option
        initRemoveColumnDatatable(domId);
    }
    initFilterColumnDatatable(domId);
//  ab 16092020: function call relocated to initElementDataTableApi()
//    //hide extras when hideExtras option is exists or when rols is Attachment
//    if ((hideExtras) || (role == 'Attachment')) {
//        hideDataTableApiImpExtras(domId);
//    }
    
    showExtras(domId); // show extras by formCode if nedded

    if(!isTableDrawUpgraded)
	{
	    //load Last Saved removed Columns
	    loadLastSavedColumns(domId);
	}
    
    if(object.data !== undefined && object.data.length > 0)
    {
	    /*  
	     * Display empty TD and TH(placeholders) in case all columns in row is invisible,
	     *  May be caused by removed(set as invisible) columns and set property 'hideEmptyColumns' to true or
	     *  	for example, in MonitoringParameters its enough to save param fields without any value
	     */
    	var atLeastOneColumnVisible = false;
    	/* first check if all columns in table invisible */
	    table.columns().every(function (index) 
	    {
	    	var _col = table.column(index);
			var _head = $(_col.header())[0];
			var _title = $(_head).text();
			
	    	if(table.column(index).visible() === true)
	    	{   		
	    		//console.log(domId, _title, "VISIBLE");
	    		atLeastOneColumnVisible = true;
	    	}
	    });
	    if(!atLeastOneColumnVisible)
	    {
	    	//if none of columns is visible append empty column(td for each row)
	    	var row = $(table.table().body()).find('tr');
	    	$($(table.table().header()).find('tr')[0]).append('<th colplaceholder></th>');
	    	for(var i=0;i<row.length;i++)
	    	{
	    		$(row[i]).attr('role', 'row');
	    		$(row[i]).append('<td colplaceholder style="text-align:center;"><span >' + getSpringMessage('DATA_TABLE_ROW_NO_VISIBLE_CONTENT') + '</span></td>');
	    	}
	    }
    }
    
    //attach 'form builder element change' event (for example onChangeAjax),
    //on a row click when doOnChangeJSCall option is exists.    
    if ((doOnChangeJSCall != "") && ((role == "") || (role == "Search") || (role == "SharedAjax")|| (role == "MultipleAjax")/* || (role == 'smartLink')*/)) 
    {
       
        if (triggerAjaxChangeFlag) {
            // trigger 'form builder element change' event when triggerAjaxChangeFlag option is true
        	eval(doOnChangeJSCall.trim().replace("(", "('").replace(")", "')").replace(",", "','"));
        }
    }
    
    selectLastSelectedRow(domId, _delayCounterForInputs);
    
    if($('table[id="' + domId + '"]')[0].hasAttribute("PREVENT_FROM_INLINE_EDITING_STATE"))
	{
    	$('table[id="' + domId + '"]')[0].removeAttribute("PREVENT_FROM_INLINE_EDITING_STATE");
	}
    
    hideWaitMessage();

    bl_elementDatatableEditableCustomFuncHandler(domId, "onTableBuilt", null, []);
    console.timeEnd("buildElementDataTableApi TIME on table: " + domId);
} 

/**
 * render columnDefs for smart columns
 * @param obj
 * @returns
 */
function smartRender(object, followingHiddenCols, domId, savedObject, isTableResizable) {
	var i,
        jsonObject,
        display,
        objColTitle,
        objColsLength = object.columns.length,
        indexColsArray = {},
        o, cellData,
        smartType = "",
        data,
        followingFixedColsCount = 0; // used for count number of columns that should be fixed and not enabled to reorder
	followingHiddenCols = (followingHiddenCols) ? Number(followingHiddenCols) : 0;

    if (objColsLength < 3) { // instead check of empty table
        object.columnDefs = [];
        object.columnDefs.push({
            "targets": [0],
            "visible": false
        });
        object.columnDefs.push({
            "targets": [1],
            "visible": true
        });
        return;
    }
    
    /** init TABLE RESIZE */
    var colWidthObj = {};
	var colWidthObjLength = 0;
	var resizableColDefaultWidth = 150;
	if(isTableResizable)
    {    	    		
		object.colResize = {resizeTable : true, minColumnWidth: 40, tableInstanceID: domId};
	    object.scrollX = false;
	    object.autoWidth = false;
	    
	   /* important note: in case table defined as resizable, column width must be defined also, even value in 'userLastSaveSettings' was not previously saved */
	   // var used by 'Column Reorder' & 'Column Resize' features: by default first column is always hidden in the most cases and property 'followingHiddenCols' is not defined in case there is only one hidden column
	    var followingHdnColsIntUpdated = (followingHiddenCols == 0)?1:followingHiddenCols; 
	    /* get default width object*/
        $colWidth = bl_getColumnDefaultWidth(domId, objColsLength, followingHdnColsIntUpdated);
        resizableColDefaultWidth = $colWidth.DefaultWidth_; // set resizableColDefaultWidth with DefaultWidth_ (and clean it form the object)
        if(!jQuery.isEmptyObject($colWidth)){
        	delete $colWidth.DefaultWidth_;
        }
	    /* get last saved value (or use the default if empty)*/
	    colWidthObj = getSavedColumnsWidthObject(domId, savedObject);
	    if(jQuery.isEmptyObject(colWidthObj) && !jQuery.isEmptyObject($colWidth)) {
	    	colWidthObj = $colWidth;
	    }
	    colWidthObjLength = Object.keys(colWidthObj).length;   
    }

	/* Loop through all table columns */
    for (i = 0; i < objColsLength; i++) 
    {
        var currColumn = object.columns[i];
    	objColTitle = currColumn.title;
        
       if(!isTableResizable){
        	var initialWidth = object.columns[i].initialWidth;
        	if(initialWidth !== undefined) object.columns[i].width = initialWidth;
        	console.log('objColTitle: ' + objColTitle, " initialWidth: "+initialWidth);
      }	
    	
    	if(isTableResizable)
    	{
    		var initialWidth = currColumn.initialWidth;			
			/* set default width for resizable columns */
			var _w = resizableColDefaultWidth;
			if(colWidthObjLength > 0 && colWidthObj.hasOwnProperty(currColumn.uniqueTitle))
			{
				_w = colWidthObj[currColumn.uniqueTitle];
			}
			else if(initialWidth !== undefined)
			{
				_w = initialWidth;
			}
			// set object.columns width
			currColumn.width = _w;
    	}
        
        if (objColTitle.indexOf('_SMARTPATH', objColTitle.length - '_SMARTPATH'.length) !== -1) { // endsWith '_SMARTPATH'		
            object.columns[i].title = objColTitle.replace('_SMARTPATH', '');    
            indexColsArray[i] = 'SMARTLINK';
        }
        if (objColTitle.indexOf('_SMARTSAMPLELIST', objColTitle.length - '_SMARTSAMPLELIST'.length) !== -1) { // endsWith '_SMARTSAMPLELIST'		
            object.columns[i].title = objColTitle.replace('_SMARTSAMPLELIST', '');    
            indexColsArray[i] = 'SMARTEDIT';
        }
        if (objColTitle.indexOf('_SMARTEXPLIST', objColTitle.length - '_SMARTEXPLIST'.length) !== -1) { // endsWith '_SMARTSAMPLELIST'		
            object.columns[i].title = objColTitle.replace('_SMARTEXPLIST', '');    
            indexColsArray[i] = 'SMARTLINK';
            object.columns[i].type = 'string';
        }
        if (objColTitle.indexOf('_SMARTLINK', objColTitle.length - '_SMARTLINK'.length) !== -1) { // endsWith '_SMARTLINK'		
            object.columns[i].title = objColTitle.replace('_SMARTLINK', '');    
            indexColsArray[i] = 'SMARTLINK';
            object.columns[i].type = 'string';
        }
        else if (objColTitle.indexOf('_SMARTFILE', objColTitle.length - '_SMARTFILE'.length) !== -1) { // endsWith '_SMARTFILE'		
            object.columns[i].title = objColTitle.replace('_SMARTFILE', '');
            indexColsArray[i] = 'SMARTFILE';
        }
        else if (objColTitle.indexOf('_SMARTICON', objColTitle.length - '_SMARTICON'.length) !== -1) { // endsWith '_SMARTICON'		
            object.columns[i].title = objColTitle.replace('_SMARTICON', '');
            indexColsArray[i] = 'SMARTICON';
        }
        else if (objColTitle.indexOf('_SMARTTOOLTIP', objColTitle.length - '_SMARTTOOLTIP'.length) !== -1) { // endsWith '_SMARTTOOLTIP'		
            indexColsArray[i] = 'SMARTTOOLTIP';
            object.columns[i].className = columnReOrderDisabledClass;
            followingFixedColsCount++;
        }
        else if (objColTitle.indexOf('_SMARTELLIPSIS', objColTitle.length - '_SMARTELLIPSIS'.length) !== -1) { // endsWith '_SMARTELLIPSIS'		
        	object.columns[i].title = objColTitle.replace('_SMARTELLIPSIS', '');  
        	/* use the next className to restrict cell width and break word if necessary */
        	object.columns[i].className = 'cell-max-width';
        	indexColsArray[i] = 'SMARTELLIPSIS';
        }
        else if (objColTitle.indexOf('_SMARTDATE', objColTitle.length - '_SMARTDATE'.length) !== -1) { // endsWith '_SMARTDATE'		
            object.columns[i].title = objColTitle.replace('_SMARTDATE', '');
            indexColsArray[i] = 'SMARTDATE';
            object.columns[i].orderDataType = 'dom-custom-sort';
            object.columns[i].type = 'date';
        }
        else if (objColTitle.indexOf('_SMARTTIME', objColTitle.length - '_SMARTTIME'.length) !== -1) { // endsWith '_SMARTTIME'		
            object.columns[i].title = objColTitle.replace('_SMARTTIME', '');
            object.columns[i].orderDataType = 'time-custom-sort'; //separated from 'dom-custom-sort' because of issues when deferRender is active on current table
            indexColsArray[i] = 'SMARTTIME';
            object.columns[i].type = 'date';
        }
        else if (objColTitle.indexOf('_SMARTDATETIME', objColTitle.length - '_SMARTDATETIME'.length) !== -1) { // endsWith '_SMARTDATETIME'		
            object.columns[i].title = objColTitle.replace('_SMARTDATETIME', '');
            object.columns[i].orderDataType = 'dom-custom-sort';
            indexColsArray[i] = 'SMARTDATETIME';
        }
        else if (objColTitle.indexOf('_SMARTNUM', objColTitle.length - '_SMARTNUM'.length) !== -1) { // endsWith '_SMARTNUM'		
            object.columns[i].title = objColTitle.replace('_SMARTNUM', '');
            indexColsArray[i] = 'SMARTNUM';
            object.columns[i].orderDataType = 'numeric-custom-sort'; //separated from 'dom-custom-sort' because of issues when deferRender is active on current table
            object.columns[i].type = 'html-num';
        }
        else if (objColTitle.indexOf('_SMARTSELECT', objColTitle.length - '_SMARTSELECT'.length) !== -1) { // endsWith '_SMARTSELECT'		
            object.columns[i].title = objColTitle.replace('_SMARTSELECT', '');
            object.columns[i].width = '1%';
            object.columns[i].initialWidth = '1%';
            object.columns[i].className = columnReOrderDisabledClass;
            followingFixedColsCount++;
            indexColsArray[i] = 'SMARTSELECT';
            _ignoreFirstTfootTd = ":not(:first)";
        }
        else if (objColTitle.indexOf('_SMARTSELECTALLNONE', objColTitle.length - '_SMARTSELECTALLNONE'.length) !== -1) { // endsWith 'SMARTSELECTALLNONE'		
            object.columns[i].title = '<input type="checkbox" id="chbSelectAllNone_'+domId+'" class="dataTableApiSelectAllNone" onclick="smartSelectStateMng(\'' + domId + '\',true,this.checked)">';
            _smartSelectTablesArray[_smartSelectTablesArray.length] = domId;
            object.columns[i].width = '1%';
            object.columns[i].initialWidth = '1%';
            object.columns[i].className = columnReOrderDisabledClass;
            followingFixedColsCount++;
            indexColsArray[i] = 'SMARTSELECTALLNONE';
            _ignoreFirstTfootTd = ":not(:first)";
        }
        else if (objColTitle.indexOf('_SMARTSELECTALLNONELABEL', objColTitle.length - '_SMARTSELECTALLNONELABEL'.length) !== -1) { // endsWith '_SMARTSELECTALLNONELABEL'		
        	object.columns[i].title = 'Label <input type="checkbox" id="chbSelectAllNone_'+domId+'" class="dataTableApiSelectAllNone" onclick="smartSelectStateMng(\'' + domId  
        								+ '\',true,this.checked, \'label\')" style="position: absolute; left: 2.8em; top: 50%; margin-top: -8px;">'; //10022020 Added word 'Label' to checkbox _SMARTSELECTALLNONELABEL fixed bug-7859
        	if(bl_removeLabelFromColName(domId)){
        		object.columns[i].title = '<input type="checkbox" id="chbSelectAllNone_'+domId+'" class="dataTableApiSelectAllNone" onclick="smartSelectStateMng(\'' + domId + '\',true,this.checked, \'label\')">';
        	}
        	_smartSelectTablesArray[_smartSelectTablesArray.length] = domId;
        	object.columns[i].width = '1%';
        	object.columns[i].initialWidth = '1%';
        	indexColsArray[i] = 'SMARTSELECTALLNONELABEL';
        	//_ignoreFirstTfootTd = ":not(:first)";// fixed bug 7859
        }
        else if (objColTitle.toLowerCase().indexOf('_uom') !== -1) 
        {       	
        	object.columns[i].title = "UOM"; //change th of columns that contain '_uom' to 'UOM'
        	if (objColTitle.indexOf('_SMARTEDIT', objColTitle.length - '_SMARTEDIT'.length) !== -1) { // endsWith '_SMARTEDIT'		
                object.columns[i].orderDataType = 'dom-custom-sort';
                indexColsArray[i] = 'SMARTEDIT';
            }
        	else
        	{
        		object.columns[i].width = '30px'; //limit column width only if there isn't dropdown list here
        		object.columns[i].initialWidth = '30px';
        	}
        }
        else if (objColTitle.indexOf('_SMARTRANGE', objColTitle.length - '_SMARTRANGE'.length) !== -1) { // endsWith '_SMARTICON'		
            object.columns[i].title = objColTitle.replace('_SMARTRANGE', '');
            indexColsArray[i] = 'SMARTRANGE';
        }
        else if (objColTitle.indexOf('_SMARTEDIT', objColTitle.length - '_SMARTEDIT'.length) !== -1) { // endsWith '_SMARTEDIT'		
        	currColumn.title = objColTitle.replace('_SMARTEDIT', '');
        	currColumn.orderDataType = 'dom-custom-sort';
            //object.columns[i].type = 'string';
        	
            indexColsArray[i] = 'SMARTEDIT';
            
            if(objColTitle.indexOf('Favorite') >-1) { // note: it was better to define the favorite as smartfavorite
                object.columns[i].title = '<i class="fa fa-star-o" aria-hidden="true" style="color:black;cursor:pointer;font-size:1.5em;"  sorttype="star" name="chb_favorite" value="no" oldvalue="no"  onClick="onChangeHeaderFavorite(this,'+domId+')"></i>';
                object.columns[i].width = '1%';
                object.columns[i].initialWidth = '1%';
            }	
            
            if(!bl_isColumnReorderable(domId, currColumn.uniqueTitle))
            {
            	object.columns[i].className = columnReOrderDisabledClass;
            	followingFixedColsCount++;
            }
        }
        else if (objColTitle.indexOf('SMARTACTIONS', objColTitle.length - 'SMARTACTIONS'.length) !== -1) { // endsWith 'SMARTACTIONS'		
            indexColsArray[i] = 'SMARTACTIONS';
            object.columns[i].className = columnReOrderDisabledClass;
            followingFixedColsCount++;
            
            bl_dtColumnFeaturesCustom(domId, object, currColumn, i);
        }
        else if (objColTitle.indexOf('SMARTWIDTH', objColTitle.length - 'SMARTWIDTH'.length) !== -1) { // endsWith 'SMARTWIDTH'		
            indexColsArray[i] = 'SMARTWIDTH';
            object.columns[i].className = columnReOrderDisabledClass;
        }
        else if (objColTitle.indexOf('_SMARTSTYLE', objColTitle.length - '_SMARTSTYLE'.length) !== -1) { // endsWith '_SMARTICON'		
            object.columns[i].title = objColTitle.replace('_SMARTSTYLE', '');
            indexColsArray[i] = 'SMARTSTYLE';
        }
        else if (objColTitle.indexOf('_SMARTHTML', objColTitle.length - '_SMARTHTML'.length) !== -1) { // endsWith '_SMARTHTML'		
            object.columns[i].title = objColTitle.replace('_SMARTHTML', '');
            indexColsArray[i] = 'SMARTHTML';
        }
        else if (objColTitle.indexOf('_SMARTCHEMDOODLE', objColTitle.length - '_SMARTCHEMDOODLE'.length) !== -1) { // endsWith '_SMARTHTML'		
            object.columns[i].title = objColTitle.replace('_SMARTCHEMDOODLE', '');
            indexColsArray[i] = 'SMARTCHEMDOODLE';
        }
        else if (objColTitle.indexOf('ROW_SELECTION_HELPER', objColTitle.length - 'ROW_SELECTION_HELPER'.length) !== -1) { // endsWith 'ROW_SELECTION_HELPER'		
            object.columns[i].title = objColTitle.replace('ROW_SELECTION_HELPER', '');
            object.columns[i].className = "row-selection-helper"+ " " + columnReOrderDisabledClass;
            followingFixedColsCount++;
            //object.columns[i].width = '40px';
            indexColsArray[i] = 'ROW_SELECTION_HELPER';
            _ignoreFirstTfootTd = ":not(:first)";
        }
        else if (objColTitle.indexOf('_SMARTSPLIT', objColTitle.length - '_SMARTSPLIT'.length) !== -1) { // endsWith '_SMARTSPLIT'		
            object.columns[i].title = objColTitle.replace('_SMARTSPLIT', '');
            indexColsArray[i] = 'SMARTSPLIT';
        }
    }
    if (Object.keys(indexColsArray).length == 0) {
        return;
    } 
       
    for (i = followingHiddenCols; i < objColsLength; i++) 
    {    	
    	if (indexColsArray.hasOwnProperty(i)) 
        {
    		if(domId != null && domId=='resentlyResults') { //yp 29062020 fix bug 8320  - patch! use search and filter in search screen on SMARTFILE ( domId = resentlyResults ) TODO on all tables
    			colIsVisible = ($.inArray(indexColsArray[i], ['SMARTTOOLTIP','SMARTACTIONS','SMARTWIDTH']) != -1)?false:true; //array of columns that should be invisible
        		colIsSearchable = ($.inArray(indexColsArray[i], ['SMARTSELECT','SMARTSELECTALLNONE','SMARTACTIONS','SMARTWIDTH','SMARTSELECTALLNONELABEL','ROW_SELECTION_HELPER']) != -1)?false:true;
        		colIsOrderable = ($.inArray(indexColsArray[i], ['SMARTSELECT','SMARTSELECTALLNONE','SMARTACTIONS','SMARTWIDTH','SMARTSELECTALLNONELABEL','ROW_SELECTION_HELPER']) != -1)?false:true;
        		
    		} else {
    			colIsVisible = ($.inArray(indexColsArray[i], ['SMARTTOOLTIP','SMARTACTIONS','SMARTWIDTH']) != -1)?false:true; //array of columns that should be invisible
        		colIsSearchable = ($.inArray(indexColsArray[i], ['SMARTSELECT','SMARTFILE','SMARTSELECTALLNONE','SMARTACTIONS','SMARTWIDTH','SMARTSELECTALLNONELABEL','ROW_SELECTION_HELPER']) != -1)?false:true;
        		colIsOrderable = ($.inArray(indexColsArray[i], ['SMARTSELECT','SMARTFILE','SMARTSELECTALLNONE','SMARTACTIONS','SMARTWIDTH','SMARTSELECTALLNONELABEL','ROW_SELECTION_HELPER']) != -1)?false:true;
        		
    		}
    		
    		var _smartActionsArr = [];
    		var _rowValidationObj = [];
    		
    		/*
    		 * kd 04012021 add it workaround code till the problem with sorting editable columns in deferRendered tables is not solved
    		 * TODO remove it in case of solvie problem with sorting. See previous row
    		 */
    		if (object.columns[i].title.indexOf('name="chb_favorite"')>-1) { 
    			colIsOrderable = false;
    		}
    		
    		object.columnDefs.push({
                "targets": [i],
                "visible": colIsVisible,
                "searchable": colIsSearchable,
                "orderable": colIsOrderable,
                "data": function ( row, type, val, meta ) 
                {
                    /* used for define data to filter/sort through */
                	if (type === 'filter' || type === 'sort') 
                    {
                		var valueToFilter = getDisplayValueFromSmarts(row[meta.col]);
                		//console.log("valueToFilter",valueToFilter);
                    	return valueToFilter;
                    }
                    return row[meta.col];
                 },
                "createdCell": function (td, cdata, rowData, row, col) // 'createdCell' function is used instead of 'render' because 'render' fired not only on cell creation but also on other datatable events like filter, sort, etc
                {                	
                	data = cdata;
                	smartType = indexColsArray[col];
                	var colTitle = object.columns[col].title;
                	var startCellDataWrapper = "", endCellDataWrapper = "";
                	var smartlinksdata = "";
                	var rowMandatoryField = "", rowMandatoryFieldID = "", rowMandatoryFieldDisplayName = "";
                	var rowId = rowData[0];     
                	
                	//code used to prevent data rendering issues in "SMART" columns when table use "deferRender" property
                	// in this case, 'smartType' and 'colTitle' should be defined in json also 
                	if(object.deferRender)
                	{
                		if (checkIfJSON(data)) { // check if json or not
                            var jArray = funcParseJSONDataAsJSONArray(data);
                            var dObj = jArray[0];
                            smartType = (dObj.smartType !== undefined)?dObj.smartType:smartType;
                            colTitle = (dObj.title !== undefined)?dObj.title:colTitle;
	                    }                 		
                	}
                	
                	if(smartType == 'SMARTEDIT')
                	{  
                		//if object.deferRender colTitle is already Favorite (it was better to define the favorite as smartfavorite)
                		if(colTitle.indexOf('name="chb_favorite"') >-1) {
                			colTitle="Favorite";
                		}
                	}
                	
                	if(smartType == "ROW_SELECTION_HELPER")
                	{
                		$(td).addClass("row-selection-helper");
                	} 
                	else if(smartType == "SMARTELLIPSIS")
                	{
                		var _cellLength = 100;
                		cellData = (data.length > _cellLength)?'<span title="'+data+'">'+data.substr( 0, _cellLength ) +'...'+'</span>':data;
                	} 
                	else if(smartType == "SMARTSELECT")
            		{   var checked = "";    
	        			var chkDisabled = ""; //"disabled";
	            		cellData = '<input type="checkbox" class="dataTableApiSelectInfo" value="' + $('<div/>').text(data).html() + '" ' + checked + '  '+chkDisabled+' >';
	        		}
                	else if(smartType == 'SMARTSELECTALLNONE')
                	{   var checked = "";    
	        			var chkDisabled = ""; //"disabled";
	            		cellData = '<input type="checkbox" class="dataTableApiSelectInfo" value="' + $('<div/>').text(data).html() + '" ' + checked + '  '+chkDisabled+' onclick="smartSelectStateMng(\'' + domId + '\',false,this.checked)">';
	        		}
                	else if(smartType == 'SMARTSELECTALLNONELABEL')
                	{   var checked = "";    
	        			var chkDisabled = ""; //"disabled";
	            		cellData = '<input type="checkbox" class="dataTableApiSelectInfoLabel" value="' + $('<div/>').text(data).html() + '" ' + checked + '  '+chkDisabled+' onclick="smartSelectStateMng(\'' + domId + '\',false,this.checked, \'label\')">';
	        		}
                	else // START ELSE_BOOKMARK
                	{
	                	if (!checkIfJSON(data)) { // check if json or not
	                		cellData = data;
	                    } 
	                    else 
	                    {                    	
	                    	
	                    	try 
	                    	{
	                    		jsonObject = funcParseJSONDataAsJSONArray(data);
		                        //console.log(jsonObject);
		                        cellData = '';
		                        if(smartType == 'SMARTACTIONS')
		                    	{
		                    		_smartActionsArr = jsonObject[0];		                    		
		                    		if(_smartActionsArr.hasOwnProperty("rowValidation"))
		                    		{
		                    			_rowValidationObj = _smartActionsArr["rowValidation"];
		                    		}
		                    	}
		                        else
		                        {
		                        	var isMandatory, 
		                        	    isRenderTable,
		                        	    htmlType,
		                        	    saveType,
		                        	    dbColumnName,
		                        	    onChangeFunc = "",
		                        	    onTableEventFunc = "",
		                        	    style,
		                        	    formCode,
		                        		additionalAttr = "",
		                        		width,
		                        		autoResizeMarker,
		                        		rowValidationAttr = "",
		                        		columnId = "",
		                        		cellId = "",
		                        		isCellAutoSaved = false,
		                        		ignorDataChange = "";
		                        		cellCustomFuncName = "",
		                        		cellCustomFuncParams = [];
		                        	
		                        	for(var y=0; y<jsonObject.length;y++)
			                    	{
		                        		o = jsonObject[y];
		                        		if(cellData.length > 0)
			                        	{
		                        			if(o.delimiter!== undefined){
			                        			cellData += o.delimiter + ' ';
			                        		}else{
			                        			cellData += ', ';
			                        		}
			                        	}
			                        	//o = jsonObject[y];
			                        	style = (o.style !== undefined)?o.style:"";
			                        	formCode = (o.formCode !== undefined)?o.formCode:encodeURIComponent($('[id="' + domId + '_structCatalogItem"]').val());

			                        	//condition for hiding the displayName on SMARTICON with displayName start with ~ 
			                        	//in this way the filter and sort will operate on the value without show it to the user (for example displayName="~yes/warn/1...")
			                        	//the reason we don't update o.displayName is because it is not the same object as in getDisplayValueFromSmarts(row[meta.col]);
			                        	var onlyIconCondition = (smartType == 'SMARTICON') && o.icon && o.displayName && o.displayName.startsWith("~") && !o.description && !o.htmlType;
			                        	
			                        	display = '<span style="margin:0px;' + (onlyIconCondition?'color:transparent;':'')+style+'" '
			                        			+(((o.icon==undefined||o.icon=='')&&o.tooltip!=undefined&&o.tooltip!='')? ('title = "'+o.tooltip+'"')
			                        										:(o.description&&o.description.length>=30)?('title = "'+o.description+'"'):'')+'>'
			            						+ ((o.icon) ? '<i style="cursor: pointer;color: '+(o.icon.indexOf('red')==-1?'#2779aa':'red')+';font-size: 13pt;" '
			            						+((o.tooltip)?'title="'+o.tooltip+'"':'')+' class="' + o.icon + '"></i>'+o.displayName : (o.description?'':o.displayName))
			            						+ (o.description? (o.description.length>=20? o.description.substr(0,27)+"...":o.description) :'')
			            						+'</span>';
			                        	if($.inArray(smartType, ["SMARTDATE","SMARTEDIT","SMARTTIME","SMARTNUM"]) !== -1)
			                        	{
			                        		isCellAutoSaved = (o.autoSave !== undefined && o.autoSave == "true")?true:false;
			                        		isMandatory = (o.mandatory !== undefined && o.mandatory == "true")?true:false;
			                        		isRenderTable = (o.renderTableAfterSave !== undefined && o.renderTableAfterSave == "true")?true:false;
			                        		htmlType = (o.htmlType !== undefined)?o.htmlType:"";
			                        		saveType = (o.saveType !== undefined)?o.saveType:htmlType; // if saveType not defined, saveType should be equal to htmlType
			                        		dbColumnName = (o.dbColumnName !== undefined)?o.dbColumnName:"";			                        		
			                        		//columnId = (o.dbColumnName !== undefined)?o.dbColumnName:col;
			                        		columnId = (saveType == 'pivot')?col:((o.dbColumnName !== undefined)?o.dbColumnName:col);
			                        		cellId = domId+'_col_'+columnId+'_row_'+rowId;
			                        		autoResizeMarker = (o.autoresize !== undefined && o.autoresize == "true")?" autogrow_marker":"";
			                        		/* onChange custom function*/
			                        		cellCustomFuncName = (o.customFuncName !== undefined)?o.customFuncName:"";
			                        		cellCustomFuncParams = (o.customFuncParams !== undefined)?o.customFuncParams:[];
			                        		/* onFocus custom function */
			                        		var cellCustomFuncOnFocus = (o.customFuncOnFocus !== undefined)?o.customFuncOnFocus:"";
			                        		var cellCustomFuncOnFocusParams = (o.customFuncOnFocusParams !== undefined)?o.customFuncOnFocusParams:[];
			                        		/* onBeforeChange custom function */
			                        		var cellCustomFuncOnBeforeChange = (o.customFuncOnBlur !== undefined)?o.customFuncOnBlur:"";
			                        		//var cellCustomFuncOnBeforeChangeParams = (o.customFuncOnBlurParams !== undefined)?o.customFuncOnBlurParams:[];
			                        		var pivotFormId = (o.pivotFormId !== undefined)?o.pivotFormId:"";
			                        		var rowMandatoryNonAffectedColumnsId = [];
			                        		if(_rowValidationObj.hasOwnProperty("mandatoryForRow"))
				                    		{
			                        			if(_rowValidationObj["mandatoryForRow"].length>1){
			                        				var length = _rowValidationObj["mandatoryForRow"].length;
			                        				rowMandatoryField = []; rowMandatoryFieldID = []; rowMandatoryFieldDisplayName = [];
			                        				
			                        				for(i=0;i<length;i++){
			                        					rowMandatoryField.push((_rowValidationObj["mandatoryForRow"])[i].columnId);
			                        					rowMandatoryFieldDisplayName.push((_rowValidationObj["mandatoryForRow"])[i].colDisplayName);
			                        					rowMandatoryFieldID.push(domId+'_col_'+rowMandatoryField[i]+'_row_'+rowId);
			                        					if((_rowValidationObj["mandatoryForRow"])[i].nonAffectedColumns){
				                        					for(j=0;j<(_rowValidationObj["mandatoryForRow"])[i].nonAffectedColumns.length;j++){
				                        						var rowMandatoryNonAffectedColumnsRowId = domId+'_col_'+((_rowValidationObj["mandatoryForRow"])[i].nonAffectedColumns)[j]+'_row_'+rowId;
				                        						rowMandatoryNonAffectedColumnsId.push(rowMandatoryNonAffectedColumnsRowId);
				                        					}
			                        					}
			                        				}
			                        			}else{
			                        				rowMandatoryField = (_rowValidationObj["mandatoryForRow"])[0].columnId;
				                        			rowMandatoryFieldDisplayName = (_rowValidationObj["mandatoryForRow"])[0].colDisplayName;
				                        			rowMandatoryFieldID = domId+'_col_'+rowMandatoryField+'_row_'+rowId;
				                        			if((_rowValidationObj["mandatoryForRow"])[0].nonAffectedColumns){
					                        			for(j=0;j<(_rowValidationObj["mandatoryForRow"])[0].nonAffectedColumns.length;j++){
					                        				var rowMandatoryNonAffectedColumnsRowId = domId+'_col_'+((_rowValidationObj["mandatoryForRow"])[0].nonAffectedColumns)[j]+'_row_'+rowId;
			                        						rowMandatoryNonAffectedColumnsId.push(rowMandatoryNonAffectedColumnsRowId);
			                        					}
				                        			}
			                        			}
			                        			rowValidationAttr = " vld_rowMandatoryField=1 row_mandatory_field_name='"+rowMandatoryField+"' row_mandatory_field_id='"+rowMandatoryFieldID+"' row_mandatory_field_display_name='"+rowMandatoryFieldDisplayName+"' rowMandatoryNonAffectedColumnsId='"+rowMandatoryNonAffectedColumnsId+"'";
				                    		}
			                        		// TODO: change this. temporary workaround
			                        		var additDynamicParam = "";
			                        		if(domId != "action")
			                        		{
			                        			if(cellCustomFuncName != "" && cellCustomFuncParams.length > 0)
			                        			{
			                        				additDynamicParam = rowId;
			                        			}
			                        		}
			                        		/* define onChange custom function*/
			                        		var colParamsObj = '{'
                    							+'htmlType'+':'+"'"+htmlType+"'"+','
                    							+'dbColumnName'+':'+"'"+dbColumnName+"'"+','
                    							+'formCode'+':'+"'"+formCode+"'"+','
                    							+'isMandatory'+':'+isMandatory+','
                    							+'colTitle'+':'+"'"+colTitle+"'"+','
                    							+'isRenderTable'+':'+isRenderTable+','
                    							+'formNumberID'+':'+"'"+((o.formNumberID !== undefined)?o.formNumberID:"")+"'"+','
                    							+'saveType'+':'+"'"+saveType+"'"+','
                    							+'isCellAutoSaved'+':'+isCellAutoSaved+','
                    							+'pivotFormId'+':'+"'"+pivotFormId+"'"+','
                    							+'customFuncName'+':'+"'"+cellCustomFuncName+"'"+','
                    							+'customFuncParams' + ':' + convertFuncParams(cellCustomFuncParams,additDynamicParam)+','
                    							+'customFuncOnBeforeChange'+':'+"'"+cellCustomFuncOnBeforeChange+"'"+','
                    							+'rowMandatoryField'+':'+"'"+rowMandatoryField+"'"+','
                    							+'rowMandatoryFieldID'+':'+"'"+rowMandatoryFieldID+"'"+','
                    							+'rowMandatoryFieldDisplayName'+':'+"'"+rowMandatoryFieldDisplayName+"'"+','
                    							+'insertIntoSelectTable'+':'+"'"+((o.insertIntoSelectTable !== undefined)?o.insertIntoSelectTable:"")+"'"
                    							+'}';
			                        		onChangeFunc = 'onChangeTableCell(this,\''+domId+'\', '+row+', '+col+', '+colParamsObj+')';
			                        		/* **************************** */
			                        		
			                        		 /* table cell events */
			                        		var colParamsObj = '{'
                    							+'customFuncName'+':'+"'"+cellCustomFuncOnFocus+"'"+','
                    							+'customFuncParams' + ':' + convertFuncParams(cellCustomFuncOnFocusParams)
                    							//+'rowMandatoryField'+':'+"'"+rowMandatoryField+"'"+','
                    							//+'rowMandatoryFieldID'+':'+"'"+rowMandatoryFieldID+"'"+','
                    							//+'rowMandatoryFieldDisplayName'+':'+"'"+rowMandatoryFieldDisplayName+"'"
                    							+'}';
			                        		onTableEventFunc = 'onEditableTableEvent(this, event,\''+domId+'\', '+colParamsObj+')';
			                        		/* **************************** */
			                        		
			                        		if(saveType == 'monitoringParams')
			                        		{
			                        			additionalAttr = " mp_formid='"+o.mp_formid+"' mp_name='"+o.mp_name+"' is_uom='"+o.is_uom+"' ";
			                        		}
			                        		
			                        		if(isCellAutoSaved && domId == "action") { // patch for action in step (need another attribute)
			                        			ignorDataChange = "ignor_data_change";
			                        		}
			                        	}
			                        	if(smartType == "SMARTSTYLE")
			                    		{                       		  
			                        		cellData = display;
			                    		}
			                        	else if(smartType == "SMARTLINK")
			                    		{  
			                        		var tooltip = (o.tooltip !== undefined && o.tooltip !== "NA")?"title='"+o.tooltip+"'":"";
			                        		cellData += '<a class="smartlink-contextmenu" contextmenu_data=["' + o.formId + '","' + o.formCode + '","' + o.tab + '"]'+ tooltip +' onclick="checkAndNavigate([\'' + o.formId + '\',\'' + o.formCode + '\',\'' + o.tab + '\'])">' + display + '</a>';
			                        		smartlinksdata += o.displayName;
			                    		}
			                        	else if(smartType == "SMARTFILE")
			                    		{  
			                        		if(o.fileId || o.formId)
			                        		{
				                        		if(o.fileId == null || $.trim(o.fileId) == ''){
				                        			
				                        			if(o.fileType &&o.fileType=='report' ){
				                        				cellData += '<a onclick="openReport(\'' + domId + '\',[\'' + o.formId + '\',\'' + o.formCode + '\',\'' + o.tab + '\'])">' + display + '</a>';
				                        			}
				                        			else {
				                        				if(o.type!== undefined && o.type == 'Link'){
				                        					cellData += '<a target="_blank" rel="noopener noreferrer" href="'+o.displayName+'">' + display + '</a>';
				                        				} else {
				                        					cellData += '<a onclick="checkAndNavigate([\'' + o.formId + '\',\'' + o.formCode + '\',\'' + o.tab + '\'])">' + display + '</a>';
				                        				}
				                        			}
				                        		}
				                        		else
				                        			cellData += '<a onclick="smartFile(\'' + domId + '\',\'' + o.fileId + '\')">' + display + '</a>';
			                        		}
			                        		else{
			                        			cellData += display;
			                        		}
			                        		continue;
			                    		}
			                        	else if(smartType == "SMARTICON")
			                    		{      
			                        		if(o.htmlType == 'text')//icon with text
			                        		{
			                        			cellData =o.displayName+ '<i style="cursor: pointer;color: '+(o.icon.indexOf('red')==-1?'#2779aa':'red')+';font-size: 13pt;" '
					            						+((o.tooltip)?'title="'+o.tooltip+'"':'')+' class="' + o.icon + '"></i>';
			                        		}else{
			                        			cellData += display;
			                        		}
			                        		
			                    		}
			                        	else if(smartType == "SMARTCHEMDOODLE")//adib
			                    		{    
			                        		var width_= "width:auto;";
			                        		var height_ =  "height:auto;";
			                        		var coordinateX = "-"+o.coordinateX;
			                        		var coordinateY = "-"+o.coordinateY;
			                        		var html="";
			                        		if(!(coordinateX=="-"||coordinateY=="-")){
			                        			html ="<div style=\"background-image:url('../skylineFormWebapp/images/ic-molecule.png');background-repeat:no-repeat;width:600px;height:168px;background-position:"+coordinateX+"px "+coordinateY+"px\"></div>"
			                        		} else {
			                        			html ="<div style=\"width:600px;height:168px;\"></div>"
			                        		}
			                        		cellData += html;
			                    		}
			                        	else if(smartType == "SMARTDATE")
			                    		{                       		  
			                        		if(htmlType == 'date')
			                        		{		                        			
		                        				cellData = '<div class="dateInput">'
			                        					   +'<input rowId="'+rowId+'" oldValue="'+o.displayName+'" class="editableSmartCell datepicker-inline ' + ignorDataChange + '" type="text" style="width:100%;text-align:center;min-width: 80px;"'
			                        					   +' id="'+cellId+'" '	
			                        					   +' value="'+o.displayName+'" '
			                        					   +' sortType="date" '
			                        					   +' onchange="'+onChangeFunc+'" '
			                        					   +' onclick="onEditableTableEvent(this, event)"'
			                        					   +' onkeydown="onEditableTableEvent(this, event)"'
			                        					   +' onpaste="onEditableTableEvent(this, event)"'
			                        					   + rowValidationAttr
			                        					   +' />'
			                        					   +'</div>';
		                        				$(td).css('overflow','hidden');
		                        				$(td).addClass('editableSmartCellParent');
			                        		}
			                        		else
			                        		{
			                        			cellData = o.displayName;
			                        		}
			                    		}
			                        	else if(smartType == "SMARTTIME")
			                    		{                       		  
			                        		if(o.htmlType == 'time')
			                        		{
			                        			cellData = '<div>'
				                        					   + '<input rowId="'+rowId+'" class="editableSmartCell tablecell_input_time ' + ignorDataChange + '" min="0" autocomplete="off" type="text" placeholder="--:--" maxlength="" '
				                        					   +' id="'+ cellId + '" '
				                        					   + ' style="text-align:center;width:100%;outline:none" '
				                        					   +' sortType="time" '
				                        					   +' value="'+o.displayName+'" '
				                        					   +' oldValue="'+o.displayName+'" '
				                        					   +' onchange="'+onChangeFunc+'" '
				                        					   +' onclick="onEditableTableEvent(this, event)"'
				                        					   +' onkeydown="onEditableTableEvent(this, event)"'
				                        					   +' onpaste="onEditableTableEvent(this, event)"'
				                        					   +' onkeyup="isTime(this)" '
				                        					   + rowValidationAttr
				                        					   +' />'
			                        					   + '</div>';
			                        			$(td).css('overflow','hidden');
			                        			$(td).addClass('editableSmartCellParent');
			                        		}
			                        		else
			                        		{
			                        			cellData = o.displayName;
			                        		}
			                    		}
			                        	else if(smartType == "SMARTDATETIME"){
			                        		cellData = '<span sortType="datetime"' 
                        						+' realvalue="'+o.realvalue+'" '
                        						+' >'
                        						+ o.displayName
                        						+'</span>'
			                        	}
			                        	else if(smartType == "SMARTNUM")
			                        	{			                        		
			                        		var originalVal = o.displayName;
			                        		var valToDisplay = originalVal;
			                        		var doConvert = (o.convertDecimalToExponential !== undefined && o.convertDecimalToExponential == "true")?true:false;			                        		
			                        		if(doConvert) {
			                        			valToDisplay = convertDecimalToExponential(originalVal);
			                        			additionalAttr += ' convertToExponMarker ';
			                        		}			                        		
			                        		if(o.htmlType == 'text')
			                        		{
				                        		width = (o.width !== undefined)?o.width:"100%";	
				                        		var isDisabled = (o.isDisabled !== undefined && o.isDisabled == "true")?' disabled ':'';
			                        			var minVal = o.minVal !== undefined ? 'minVal="'+o.minVal+'"':"";//TODO:expand the minval to support all the values(should be handled in the onclick event). For now supports in the value 0 only.
			                        			var maxVal = o.maxVal !== undefined ? 'maxVal="'+o.maxVal+'"':"";
				                        		cellData = '<input rowId="'+rowId+'"  class="editableSmartCell alphanumInputForm ' + ignorDataChange + '" type="number" '
			                        						+' style="width:'+width+';text-align:center;'+style+'" ' 
			                        						+' sortType="numeric" '
			                        						+' id="'+cellId+'" '	
			                        						+ isDisabled
			                        					    + additionalAttr
			                        					    + rowValidationAttr
			                        					    + minVal
			                        					    + maxVal
			                        					    +' title="'+originalVal+'" '
			                        					    +' oldValue="'+valToDisplay+'" '
			                        					    +' onblur="'+onChangeFunc+'" '
				                        					+' onclick="onEditableTableEvent(this, event)"'
				                        					+' onkeydown="onEditableTableEvent(this, event)"'
				                        					+' onpaste="onEditableTableEvent(this, event)"'
				                        					+' onInput="onEditableTableEvent(this, event)"'
				                        					+' value="'+valToDisplay+'"'
				                        					+' originalValue="'+originalVal+'" />';
			                        			$(td).addClass('editableSmartCellParent');
			                        		}
			                        		else if(o.htmlType == 'span') 
			                        		{
			                        			cellData = '<span class="viewableSmartCell" '
			                        				+' id="'+cellId+'" '
			                        				+' style="width:100%;'+style+'" ' 
			                        				+' sortType="numeric" '	
			                        				+' spanType="number" '
			                        				+' title="'+originalVal+'" '
			                        				+ additionalAttr
			                        				+' originalValue="'+originalVal+'">'
			                        				+ valToDisplay
			                        				+'</>';
			                        			
			                        			$(td).addClass('numericCell');
			                        		}
			                        		else
			                        		{
			                        			$(td).addClass('numericCell');
			                        			cellData = valToDisplay;
			                        		}
			                        	}
			                        	else if(smartType == "SMARTEDIT")
			                        	{
			                        		var dataMap=[];
			                        		
			                        		if(o.htmlType == 'selectOperType')
			                        		{
			                        			var idName = domId+'_col'+col+'_select';
			                        			if($("table[id='"+domId+"']").attr("disableeditable")=="0"){
				                                	if(domId =="operartinTypeTable" ){
				                                		idName = idName+"_"+row;
				                                	}
				                                }
			                        			
			                        			dataMap = o.fullList;
			                        			var filterName = o.filterField;
			                        			var filterVal = filterName!=''?$('#'+filterName).val():'';
				                        		var selectList =  '<select rowId="'+rowId+'" onchange="'+onChangeFunc+'" id="'+idName+'"'
			                        							+' style="width:100%; text-align: left;" class="editableSmartCell tablecell-chosen-select ' + ignorDataChange + '" data-placeholder="Choose Operation Type:">';
	
			                        			var selectedOption = o.displayName ==''?$.parseJSON('{"ID":"","VAL":""}'):o.displayName;
			                        			var isSelectedExist = filterName==''||o.displayName ==''?true:false;//if there is no filter, or there was not chosen any option, there there is no  a need to add the selected option
				                        		/*	var tableRows = $('#' + domId).DataTable().rows().data();
			                        			var selectedIds=[];
			                        			if(o.once_chosen!==undefined && o.once_chosen=='true'){//can choose each option in one row only.
				                        			//collect the id's that where already chosen
				                        			tableRows.each(function(value, index){
				                        		        //console.log(value[0], lastFormId);
				                        				if(row==index){//runs on the other rows
				                        					return true;
				                        				}
				                        				var currentOp= JSON.parse(value[col]).displayName;
				                        		    	if(currentOp != '') 
				                        		    	{
				                        		    		selectedIds.push(currentOp.ID);
				                        		    	}
				                        		    });
				                        			}*/
			                        			$(dataMap).each(function ()  
			                        			{
				                        				/*if(selectedIds.indexOf(this.ID)!=-1){
			                        					return true;//skip to next iteration
				                        				}*/
			                        				var selected = selectedOption.ID == this.ID?'selected = "selected"':'';
			                        				if(filterName==''){//no filtering
			                        					selectList = selectList + '<option '+selected+' value='+this.ID+'>'+this.VAL+'</>';		                        				
			                        				} else if(this.FILTER_ID!='' && this.FILTER_ID == filterVal){ //filter according to the value in the filterField
			                        					selectList = selectList + '<option '+selected+' value='+this.ID+'>'+this.VAL+'</>';
			                        					if(this.ID == selectedOption.ID){
			                        						isSelectedExist = true;
			                        					}
			                        				}
			                        				
			                        			});
			                        			if(isSelectedExist==false){
			                        				selectList = selectList + '<option selected value='+selectedOption.ID+' disabled>'+selectedOption.VAL+'</>';
			                        				
			                        			}
			                        			if(selectList.indexOf('selected')==-1){//no option is chosen
			                        				selectList = selectList +'<option selected disabled value="-1"></>'
			                        			} else{
			                        				selectList = selectList +'<option disabled value="-1"></>'
			                        			}
			                        			cellData = selectList;
			                        			$(td).addClass('editableSmartCellParent');
			                        		}
			                        		else if(o.htmlType == 'select')
			                        		{
			                        			var isDisabled = (o.isDisabled !== undefined && o.isDisabled == "true")?' disabled ':'';
			                        			var isMultiple = (o.multiple !== undefined && o.multiple == "true")?true:false;
				                        		var displayAsLink = (o.displayAsLink && o.displayAsLink == "true")?' isLink formCode="'+o.formCode+'" ':' ';
				                        		var allowSingleDeselect = (o.allowSingleDeselect && o.allowSingleDeselect == "false")? ' disallowSingleDeselect ':' ';
				                        		var maxShownResults = (o.maxShownResults)?' maxShownResults="'+o.maxShownResults+'" ':' ';
				                        		var multiple = (isMultiple)?'multiple':' ';
				                        		var chosen_width = (o.width)?' chosen_width="'+o.width+'" ':' ';
			                        			dataMap = o.fullList;
			                        			var selectBody = ""
			                        			var selectOptionList = '<option value="0"></>';
				                        		var _currDisplayName = o.displayName;
				                        		var selectedOptionArr = []; // to support multiple choice also
				                        		var filterName = (o.filterField != undefined)?o.filterField:"";
			                        			var filterVal = filterName!=''?$('#'+filterName).val():'';
				                        		if( $('#' + domId + '_role').val()=="Shared" && $("table[id='"+domId+"']").attr("disableeditable")=="0"){
				                                	if(domId =="instruments" || domId =="columns" || domId =="samples" ){
				                                		//var obj = JSON.parse(rowData[3]);
				                                    	//rowId = obj.formId;
				                                		cellId = cellId+"_"+row;
				                                	}
				                                }
				                        		if( $('#' + domId + '_role').val()!="Shared" && $("table[id='"+domId+"']").attr("disableeditable")=="0"){
				                                	if(domId =="columnSelect"){
				                                		//var obj = JSON.parse(rowData[3]);
				                                    	//rowId = obj.formId;
				                                		cellId = cellId+"_"+row;
				                                	}
				                                }
				                        		if(_currDisplayName =='')
				                        		{
				                        			selectedOptionArr = [];
				                        		}
				                        		else
				                        		{				                        			
				                        			if(o.displayName instanceof Array)
				    		                    	{
				                        				_currDisplayName = o.displayName;
				    		                    	}
				    		                        else
				    		                    	{
				    		                        	_currDisplayName = funcParseJSONData("["+o.displayName+"]");
				    		                    	}
				                        			
				    		                        for(var y=0; y<_currDisplayName.length;y++)
				    		            	    	{				    		            	        	
				    		            	        	var o = _currDisplayName[y];
				    		            	        	selectedOptionArr[y] = o.ID;
				    		            	    	}
				                        		}		                        						
			                        			$(dataMap).each(function ()  
			                        			{
			                        				if(o.excludeID == this.ID)
			                        				{
			                        					return true;
			                        				}
			                        				if(this.ID != "")
			                        				{
			                        					var selected = ($.inArray(this.ID, selectedOptionArr) > -1)?'selected = "selected"':'';
			                        					var this_VAL = dtExt_htmlEscapeEntities(this.VAL);
			                        					var disabled  = this.ACTIVE == '1'||this.ACTIVE == undefined?'':'disabled';
			                        					var tooltipOption="";
			                        					if (typeof this.TOOLTIP === "undefined"){
			                        						tooltipOption = dtExt_htmlEscapeEntities(this.VAL);
			                        					}else{
			                        						
			                        						try {
			                        							tooltipOption = dtExt_htmlEscapeEntities(this.TOOLTIP);
															} catch (e) {
																tooltipOption = "NA";
															}
			                        					}
				                        				if(filterName==''){//no filtering	
				                        					selectOptionList = selectOptionList + '<option title="'+tooltipOption+'" '+selected+' value="'+this.ID+'" '+disabled+'>'+this_VAL+'</>';
				                        				}else if(filterName!=''){
				                        					if(this.FILTER_ID!='' && this.FILTER_ID == filterVal){ //filter according to the value in the filterField
				                        						selectOptionList = selectOptionList + '<option title="'+tooltipOption+'" '+selected+' value="'+this.ID+'" '+disabled+'>'+this_VAL+'</>';					                        					
				                        					}
//				                        					else if(selected!=""){
//				                        						selectOptionList = selectOptionList + '<option title="'+tooltipOption+'" '+selected+' value="'+this.ID+'" '+disabled+'>'+this_VAL+'</>';
//					                        					
//				                        					}
				                        				}
			                        				}
			                        				
			                        			});			                        			
			                        			var placeHolder = 'data-placeholder=" "';
			                        			if(!isMultiple) {
			                        				placeHolder = (o.placeHolder)?'data-placeholder="'+o.placeHolder+'"':' data-placeholder="Choose '+colTitle+': "';
			                        			}
			                        			selectBody = '<select rowId="'+rowId+'" style="text-align: left;" '+multiple+' class="editableSmartCell tablecell-chosen-select ' + ignorDataChange + '"'
			                        						+ placeHolder
				                     					    +' id="'+cellId+'" '
				                     					    +' sortType="string" '
				                     					    + isDisabled
				                     					    + chosen_width
				                     					    + displayAsLink
				                     					    + allowSingleDeselect
				                     					    + maxShownResults
				                     					    + additionalAttr
				                     					    + rowValidationAttr
				                     					    +' oldValue="'+selectedOptionArr.join()+'"'
				                     					    +' onchange="'+onChangeFunc+'" '
			                    							+' >';
				                        		cellData = selectBody + selectOptionList + '</select>';
				                        		$(td).addClass('editableSmartCellParent');
			                        		}
			                        		else if(o.htmlType == 'date') // made for "Taro develop" but it is good to have it also for Adama - we add date elements types under SMARTEDIT - in this case sort and filter will be made on the string of the date (in taro the date elements is under a column of results from different types of data)
			                        		{		                        			
		                        				cellData = '<div class="dateInput" style="white-space:nowrap;min-width: 130px;">'
			                        					   +'<input rowId="'+rowId+'" oldValue="'+o.displayName+'" class="editableSmartCell date-picker ' + ignorDataChange + '" style="margin-top:1px;height:50px" type="text" '
			                        					   +' id="'+cellId+'" '	
			                        					   +' value="'+o.displayName+'" '
			                        					   +' sortType="date" '
			                        					   +' onchange="'+onChangeFunc+'" '
			                        					   +' onclick="onEditableTableEvent(this, event)"'
			                        					   +' onkeydown="onEditableTableEvent(this, event)"'
			                        					   +' onpaste="onEditableTableEvent(this, event)"'
			                        					   + rowValidationAttr
			                        					   + additionalAttr
			                        					   +' />'
			                        					   +'</div>';
			                        		}
			                        		else if(o.htmlType == 'checkbox')
			                        		{			                        			
			                        			$(td).css({"text-align":"center", "vertical-align": "middle"});
			                        			var checked = (o.displayName==1)?"checked":"";
			                        			var tooltip = (o.tooltip !== undefined)?'title = "'+o.tooltip+'"':'';  
			                        			var isDisabled = (o.isDisabled !== undefined && o.isDisabled == "true")?' disabled ':'';
			                        			cellData = '<input type="checkbox" rowId="'+ rowId+'" class="editableSmartCell ' + ignorDataChange + '" '
			                        						 +' id="'+cellId+'" '
			                        						 //+' sortType="string" '
			                        						 + isDisabled
			                        						 +' name="chb_'+columnId+'" '
			                        						 +' value="'+o.displayName+'" '
			                        						 + checked
			                        						 + tooltip
			                        						 +' oldValue="'+o.displayName+'"'
					                        				 +' onchange="'+onChangeFunc+'" '
					                        				 +'/>';
			                        			$(td).addClass('editableSmartCellParent');
			                        		}
			                        		else if(o.htmlType == 'checkbox_star')
			                        		{			             
		                        				var tooltip = (o.tooltip)?"title='"+o.tooltip+"'":"";
		                        				//var onClickAction = o.onclick;
		                        				
		                        				$(td).css({"text-align":"center", "vertical-align": "middle"});
			                        			var checked = (o.displayName=='yes')?"checked":"";
			                        			var isDisabled = (o.isDisabled !== undefined && o.isDisabled == "true")?' disabled ':'';
			                        			var faClass = '', value = 'no';
		                        				
		                        				if(icon != "")
		                        				{
		                        					if (checked=='checked') {
		                        						faClass = "<i class='fa fa-star' aria-hidden=\"true\" rowId=\""+ rowId+"\" "+tooltip+" style='color:#62B2DB;cursor:pointer;font-size:1.5em;' ";
		                        						value = 'yes';
		                        					} else {
		                        						
		                        						faClass ="<i class='fa fa-star-o' aria-hidden=\"true\" rowId=\""+ rowId+"\" "+tooltip+" style='color:black;cursor:pointer;font-size:1.5em;' "
		                        					}
		                        					cellData = "<div style='float:left;'><span>"
		                        							 + faClass
		                        							 +' id="'+cellId+'" '
			                        						 +' sortType="star" '
			                        						 + isDisabled
			                        						 +' name="chb_'+columnId+'" '
			                        						 +' value="'+value+'" ' //o.displayName
			                        						 + checked
			                        						 +' oldValue="'+o.displayName+'"'
		                									 +' onclick="'+onChangeFunc+'" '
		                									 //+" onclick=\"generalBL_elementDataTableClickEvent('"+domId+"','"+onClickAction+"',["+rowData[0]+",'"+formCode+"'])
		                        							 +"\"></i></span>"
		                									 +"</div>";
		                        					$(td).css({"text-align":"center", "vertical-align": "middle"});
		                        				}
			                        			
			                        		}
			                        		else if(o.htmlType == 'text')
			                        		{		
			                        			width = (o.width !== undefined)?o.width:"100%";	
			                        			var isDisabled = (o.isDisabled !== undefined && o.isDisabled == "true")?' disabled ':'';
			                        			cellData = '<input rowId="'+rowId+'"  class="editableSmartCell alphanumInputForm ' + ignorDataChange + '" type="text" '
			                        						+' style="'+style+';width:'+width+';" '
			                        						+' id="'+cellId+'" '		    
			                        						+' sortType="string" '
			                        					    + additionalAttr
			                        					    + rowValidationAttr
			                        					    + isDisabled
			                        					    +' oldValue="'+o.displayName+'" '
			                        					    +' title="'+o.displayName+'" '
				                        					+' onblur="'+onChangeFunc+'" '
				                        					+((cellCustomFuncOnFocus != "")?' onfocus="'+onTableEventFunc+'" ':'')				                        					
				                        					+' onclick="'+onTableEventFunc+'"'
				                        					+' onkeydown="'+onTableEventFunc+'"'
				                        					+' onpaste="'+onTableEventFunc+'"'
				                        					+' value="'+o.displayName+'"/>';
			                        			$(td).addClass('editableSmartCellParent');
			                        		}
			                        		else if(o.htmlType == 'textarea')
			                        		{				                        						                        			
			                        			width = (o.width !== undefined)?o.width:"100%";
			                        			var isDisabled = (o.isDisabled !== undefined && o.isDisabled == "true")?' disabled ':'';
			                        			cellData = '<textarea rowId="'+rowId+'" rows="" cols="30" class="editableSmartCell alphanumInputForm '+autoResizeMarker+' '+ ignorDataChange + '" '
			                        				    +' style="width:'+width+';resize: none;cursor:default;" '
			                        					+' sortType="string" '		
			                        					+' title="'+o.displayName+'" '
			                        					+' onblur="'+onChangeFunc+'" '
			                        					+' id="'+cellId+'" '
			                        					+' oldValue="'+o.displayName+'" '
			                        					+' onclick="onEditableTableEvent(this, event)"'
			                        					+' onkeydown="onEditableTableEvent(this, event)"'
			                        					+' onpaste="onEditableTableEvent(this, event)"'
			                        					+ rowValidationAttr
			                        					+ isDisabled
			                        					+'>'
			                        					+  o.displayName
			                        					+'</textarea>';
			                        			$(td).addClass('editableSmartCellParent');
			                        		}
			                        		else if(o.htmlType == 'editableDiv')
			                        		{		                        			
			                        			width = (o.width !== undefined)?o.width:"100%";
			                        			var dataMaxLength = (o.dataMaxLength)?o.dataMaxLength:500;
			                        			var _content = o.displayName;
			                        			var _contentToDisplay = _content.replace(/\n/gi,'<br>');
			                        			var isContenteditable = (o.isDisabled !== undefined && o.isDisabled == "true")?'false':'true';
			                        			cellData = '<div contenteditable="' + isContenteditable + '" spellcheck="false" data-max-length="'+dataMaxLength+'" class="editableSmartCell  contentEditableMarker '+ ignorDataChange + '" '
			                        				    +' style="width:'+width+';" '	
			                        					+' title="'+_content+'" '
			                        					+' rowId="'+rowId+'" '
			                        					+' id="'+cellId+'" '
			                        					+' oldValue="'+_content+'" '
			                        					+' onblur="'+onChangeFunc+'" '
			                        					+' onclick="onEditableTableEvent(this, event)"'
			                        					+' onkeydown="onEditableTableEvent(this, event)"'
			                        					+' onpaste="onEditableTableEvent(this, event)"'
			                        					+ rowValidationAttr
			                        					+'>'
			                        					+  _contentToDisplay
			                        					+'</div>'
			                        					;
			                        			$(td).addClass('editableSmartCellParent');
			                        		}
			                        		else if(o.htmlType == 'richtext')
			                        		{
			                        			width = (o.width !== undefined)?o.width:"90%";		
			                        			var tooltip = (o.tooltip !== undefined)?o.tooltip:o.displayName;     
			                        			var isContenteditable = (o.isDisabled !== undefined && o.isDisabled == "true")?false:true;
			                        			if(isContenteditable) {
			                        				cellData = '<div contenteditable="true" rowId="'+rowId+'" class="editableSmartCell richtext contentEditableMarker ' + ignorDataChange + '" '
	                        						+' style="width:'+width+';float:left;" '
		                        				//	+'  onclick="openRichtextContent(this,event,\''+domId+'\', \''+col+'\', \''+row+'\',\''+formCode+'\',\''+dbColumnName+'\');" '
		                        					//+' onchange="'+onChangeFunc+'" '
		                        					+' onblur="'+onChangeFunc+'" '
		                        					//+' onclick="'+onChangeFunc+'"'
		                        					//+' onkeydown="'+onChangeFunc+'"'
		                        					//+' onpaste="'+onChangeFunc+'"'
		                        					+' plainTextValue="" '
		                        					+' oldValue=""'
		                        					+' title="'+tooltip+'" '
		                        					+' id="'+cellId+'" '
		                        					+' tabindex="0" '
		                        					+ rowValidationAttr
		                        					+ isDisabled
		                        					+'>'
		                        					+  o.displayName
		                        					+'</div>'
		                        					+ '<i class="fa fa-expand ignor_data_change" style="float: right;width:10%" onclick="openRichtextContent(this.previousElementSibling,event,\''+domId+'\', \''+col+'\', \''+row+'\',\''+formCode+'\',\''+dbColumnName+'\');">';
			                        				$(td).addClass('editableSmartCellParent');
			                        			} else {
			                        				cellData = o.displayName;
			                        			}
			                        		}
			                        		else if(o.htmlType == 'button')
			                        		{
			                        			if(o.displayName == "")
			                        			{
			                        				var icon = (o.icon)?o.icon:"";
			                        				var tooltip = (o.tooltip)?"title='"+o.tooltip+"'":"";
			                        				var onClickAction = o.onclick;
			                        				if(icon != "")
			                        				{
			                        					cellData = "<div style='float:left;'><span><i class='"+o.icon+"' "+tooltip+" style='cursor:pointer;font-size:1.5em;' "
			                									+" onclick=\"generalBL_elementDataTableClickEvent('"+domId+"','"+onClickAction+"',["+rowData[0]+",'"+formCode+"'])\"></i></span>"
			                									+"</div>";
			                        				}
			                        				else
			                        				{
				                        				var btn_style = "style=\"margin-left:0;width:100%;min-width:70px;\"";
					                        			cellData = "<button type=\"button\" "+btn_style+" class=\"button ui-button ui-corner-all ui-widget\" "
					                        					+" onclick=\"generalBL_elementDataTableClickEvent('"+domId+"','"+onClickAction+"',["+rowData[0]+",'"+formCode+"'])\">Remove action</button>";	
					                        			$(td).css('overflow','hidden');
			                        				}
			                        			}
			                        			else
			                        			{
			                        				cellData = o.displayName;
			                        			}
			                        		}
			                        		else if(o.htmlType == 'checkCharSample')
			                        		{
			                        			var checkedList = [];
			                        			if(o.displayName.length>0){
			                        				checkedList = o.displayName.split(',')
			                        			}
			                        			var currentVal = $('#'+dbColumnName).val();
			                        			if(currentVal.length>0){
			                        				currentCheckedList = currentVal.split(',').sort();
			                        				currentVal = currentCheckedList.toString();
			                        			}
			                        			if(currentVal.length>0 && currentVal != checkedList.sort().toString()){//it happens on render
			                        				checkedList = currentVal.split(',');
			                        			}
			                        			$(td).css("text-align","center");
			                        			var role = $('#' + domId + '_role').val();
			                        			var rowId = role == 'SharedAjax'?rowData[1] : rowData[0]; //TODO: handle the case of 'shared' type- should add the referredFormId
			                        			cellData = '<input Type="checkbox" rowId="'+ rowId+'" name="checkCharSample"'
					                        				 +' onchange="'+onChangeFunc+'" '
					                        				 + (o.group == 'true'? ' group="'+ domId+'_'+col+'_group"':'')
					                        				 + (checkedList.indexOf(rowId)!=-1?' checked':'')
					                        				 + (o.disabled=='true'?' disabled':'')
					                        				 +'/>';
			                        		}
			                        		else if(o.htmlType == 'span') 
			                        		{		  
			                        			var icon = (o.icon)?o.icon:"";
		                        				var tooltip = (o.tooltip)?"title='"+o.tooltip+"'":"";
		                        				var onClickAction = o.onclick;
			                        			cellData = '<span class="viewableSmartCell" style="'+style+';width:100%;"'
			                        				+' id="'+cellId+'" '
			                        				+' type="text" ' 
			                        				+' sortType="numeric" '	
			                        				+' spanType="number" '
			                        				+' title="'+o.displayName+'" '			                        				
			                        				+' originalValue="'+o.displayName+'">'
			                        				+ o.displayName
			                        				+(icon?("<i class='"+icon+"' "+tooltip1+" style='cursor:pointer;font-size: 0.9em;' "
			                        					+ " onclick=\""+onClickAction+"\"></i>"):"")
			                        				+'</>';
			                        			$(td).addClass('editableSmartCellParent');
			                        			//$(td).addClass('numericCell');//editableSmartCellParent
			                        		}
			                        		else
			                        		{			                        		
			                        			//$(td).css({"text-align":"center", "vertical-align": "middle"});
			                        			$(td).css({"text-align":"center"});
			                        			cellData = o.displayName;
			                        		}
			                        	}
			                        	 
			                            display = '';
			                    	}
		                        }
							} 
	                    	catch (e) {
								cellData = data;
								console.log("error in createdCell: " + data);
								console.log("error: " + e);
	//		    				displayAlertDialog(data);
							}
	                    }
                	}//END ELSE_BOOKMARK
                	if(_smartActionsArr.hasOwnProperty(colTitle.toLowerCase()))
                	{
                		var _obj = _smartActionsArr[colTitle.toLowerCase()];

            			if (colTitle.toLowerCase() == "self-test" || (colTitle.toLowerCase() == "request" && _smartActionsArr[colTitle.toLowerCase()][0]!=undefined)){   // kd 01122019 getData additional data (second element of array self-test) from the veiw (fg_s_action_dte_v) and add new span with icon "copy"
            				_obj = _smartActionsArr[colTitle.toLowerCase()][0];
            			}
                		var hasIcon = (_obj.icon && _obj.icon != "")?true:false;
                		var mainDivWidth = (_obj.width)?"min-width:"+_obj.width+"":"";
                		var cellDataDivWidth = hasIcon?"width:90%":"width:100%";
                		var curCellType = (_obj.cellType)?_obj.cellType:"";
                		var tooltip = (_obj.tooltip)?"title='"+_obj.tooltip+"'":"";
                		var padding = "";
                		if(curCellType == "link" && smartlinksdata == "")
                		{
                			mainDivWidth = ""; //not define width if there aren't links in the column
                		}
                		else {
                			padding = "padding-right: 6px;";
                		}
                		if(_rowValidationObj.hasOwnProperty("mandatoryForRow"))
                		{
                			rowMandatoryField = (_rowValidationObj["mandatoryForRow"])[0].columnId;
                			rowMandatoryFieldDisplayName = (_rowValidationObj["mandatoryForRow"])[0].colDisplayName;
                			rowMandatoryFieldID = domId+'_col_'+rowMandatoryField+'_row_'+rowId;
                		}
                		// TODO: change additDynamicParam implementation. temporary workaround
                		var additDynamicParam = "";
                		if(domId != "action")
                		{
                			additDynamicParam = rowId;
                		}
                		//console.log(_obj.params);
                		var paramsArr = convertFuncParams(_obj.params, additDynamicParam);
                		
                		var additionalSpan = ""; //kd 01122019
                		if (colTitle.toLowerCase() == "self-test" || (colTitle.toLowerCase() == "request" &&  _smartActionsArr[colTitle.toLowerCase()][0]!=undefined)){  //kd 01122019   
            				_obj = _smartActionsArr[colTitle.toLowerCase()][0];
                			var _obj1 = _smartActionsArr[colTitle.toLowerCase()][1]; 
                			if(_obj1!=undefined) {
                				var tooltip1 = (_obj1.tooltip)?"title='"+_obj1.tooltip+"'":""; 
                				var paramsArr1 = convertFuncParams(_obj1.params, additDynamicParam); 
                				
                				additionalSpan = 
                					"<span style='margin-left: 4px;float: right;'><i class='"+_obj1.icon+"' "+tooltip1+" style='cursor:pointer;font-size: 0.9em;' "
                					+ " onclick=\"beforeEditableDataTableClickEvent('"+domId+"','"+_obj1.funcName+"', "+paramsArr1+", ['"+rowMandatoryField+"','"+rowMandatoryFieldID+"','"+rowMandatoryFieldDisplayName+"'])\"></i></span>";
                			}
            			}
                		
                		var overrideRowIdForAttachNewFile = rowId;
                		var isAttachFile = (_obj.funcName == 'attachNewFile')?true:false;
                		if(isAttachFile && _obj.params != null && _obj.params.length > 0) {// 1st param is the formId that holds the document (2sd is the formcode of the first para)
                			overrideRowIdForAttachNewFile = _obj.params[0];
                		}
                		
                		var overrideRowIdTableType = "documents";
                		if(isAttachFile && _obj.params != null && _obj.params.length > 2) {// 3rd param is the tabletype (optional, the default documents)
                			overrideRowIdTableType = _obj.params[2];
                		}
                		
                		startCellDataWrapper = "<div style='"+mainDivWidth+"'>"
                							   +"<div rowId='" + overrideRowIdForAttachNewFile + "' style='float:left;"+cellDataDivWidth+";"+padding+"'"+(isAttachFile?" class='editableCell dragAndDrop' docTableType='" + overrideRowIdTableType + "'":"")+">"
                							   +(isAttachFile?(cellData==""?"<label class='watermark'>Drag & Drop</label>":""):"") 
                							   + cellData 
                							   + "</div>";
                		if(hasIcon)
                		{
	                		endCellDataWrapper = "<div style='float:right;width:10%'><span style='margin-left: 4px;float: right;'><i class='"+_obj.icon+"' "+tooltip+" style='cursor:pointer;' "
	                							+" onclick=\"beforeEditableDataTableClickEvent('"+domId+"','"+_obj.funcName+"', "+paramsArr+", ['"+rowMandatoryField+"','"+rowMandatoryFieldID+"','"+rowMandatoryFieldDisplayName+"'])\"></i></span>"
	                							+ additionalSpan
	                							+"</div></div>";
                		}
                		cellData = startCellDataWrapper + endCellDataWrapper;
                		
                		if(isAttachFile)
                		{
                			$(td).addClass('editableSmartCellParent');
                		}
                	}
                	else
                	{ 
    					if(_smartActionsArr.hasOwnProperty("rowDisabledClass")){
    						$(td).addClass(_smartActionsArr["rowDisabledClass"]);
    					}
    					if(_smartActionsArr.hasOwnProperty("rowBoldedClass")){
    						$(td).addClass(_smartActionsArr["rowBoldedClass"]);
    					}
    					if(_smartActionsArr.hasOwnProperty("leftBorderBoldedClass")){
    						$(td).addClass(_smartActionsArr["leftBorderBoldedClass"]);
    					}
                	}
                	$(td).html(cellData);
                	/*if(_obj.funcName=='attachNewFile'){
                		//init drag&drop for the case that this element exists in the table
                	}*/
                }
                
            });
        } 
        else {
            object.columnDefs.push({
                "targets": [i],
                "visible": true,
                "data": function ( row, type, val, meta ) 
                {                	
                	return dtExt_htmlEscapeEntities(row[meta.col]);
                }
            });
        }
    }
    if(object.colReorder != undefined && object.colReorder.enable && followingFixedColsCount > 0) {
    	object.colReorder.fixedColumnsLeft = object.colReorder.fixedColumnsLeft + followingFixedColsCount;
//    	console.log("fixedColumnsLeft: ",object.colReorder.fixedColumnsLeft);
    }
}

function getCellDefaultContent(domId)
{
	console.log("getCellDefaultContent", "There is problem with table "+domId);
	return "NaN";
}

function convertFuncParams(origArr, additDynamicParam)
{	
	var str = "";
	try 
	{
		
		if(origArr instanceof Array)
		{
			var convertedArr;
			if(additDynamicParam != "" && origArr.length > 0)
			{
				str = "'"+ additDynamicParam +"'" + ",";
			}
			for(var i=0;i < origArr.length;i++)
			{
				str += "'"+ origArr[i] +"'" + ",";
			}
			str = str.substring(0, str.length-1);
			convertedArr = "["+str+"]";
			//console.log("convertFuncParams convertedArr",convertedArr);
			return convertedArr;
		}
	} catch (e) {
		console.log("convertFuncParams() error:", e);
	}
	return origArr;
}

/**
 * Function handle next table cell events:  onclick, onfocus, onkeydown, onpaste
 * Events 'onclick' and 'onfocus' are very similar and maybe alternative to each other,
 *   however, there are elements that have 'onclick' event together with 'onfocus':
 * 	   - with type 'date', because of datepicker 'inline' type is use 'focus' event to be shown
 *     - with type 'text', in case has defined 'customFuncOnFocus' property
 * 
 * @param elem
 * @param event
 * @param domId
 * @param object with relevent paramenters
 * @returns
 */
function onEditableTableEvent(elem, e, domId, paramsObj)
{
	console.log("onEditableTableEvent event",e);
	
	var continuePropagation = true;
	var $elem = $(elem);
	var isFocusEvent = (e !== undefined && e.type == 'focus')?true:false;
	
	if(e.type == 'paste'){
		if($elem.attr('minVal') && $elem.attr('minVal')==0){
			var inputVal = e.target.value;
			if(Number(inputVal)<0){
				$elem.val($elem.attr('oldvalue'));
				return false;
			}
		}
	}else if(e.type == 'input'){
		if($elem.attr('minVal') && $elem.attr('minVal')==0){
			var inputVal = e.target.value;
			if(Number(inputVal)<0){
				$elem.val($elem.attr('oldvalue'));
				return false;
			}
		}
		if($elem.attr('maxVal') ){
			var inputVal = e.target.value;
			if(Number(inputVal)>$elem.attr('maxVal')){
				$elem.val(inputVal.slice(0, inputVal.length-1));
			}
		}
	}
	
	if(elem.hasAttribute('vld_rowMandatoryField'))
	{
		var funcParamsArr = [$elem.attr("row_mandatory_field_name"),$elem.attr("row_mandatory_field_id"), $elem.attr("row_mandatory_field_display_name"),$elem.attr('rowMandatoryNonAffectedColumnsId')];		
		/** prevent from 'displayAlertDialog' to be shown (cause the bug) in case there is 'focus' event defined together with 'click' event */
		var showAlertDialog = (!isFocusEvent && !isDisplayAlertDialogVisible());
		continuePropagation = validateRowMandatoryField(funcParamsArr, $elem, showAlertDialog);
	}
	if(continuePropagation && paramsObj && isFocusEvent)
	{
		var customFuncName = paramsObj.customFuncName;
		var customFuncParams = paramsObj.customFuncParams;
		
		if(customFuncName != "")
		{
			continuePropagation = bl_editableCellOnFocusCustomFuncHandler($elem, customFuncName, customFuncParams);
		}
	}
	return continuePropagation;
}

function validateRowMandatoryField(paramsArr, $elem, showAlert)
{
	var rowMandatoryField = paramsArr[0].split(",");
	var rowMandatoryFieldID = paramsArr[1].split(",");
	var rowMandatoryFieldDisplayName = paramsArr[2].split(",");
	var rowMandatoryNonAffectedColumnsId = paramsArr[3]?paramsArr[3].split(","):"";
	var currFieldIsRowMandatoryField = false;
	var currentFielsIsNonMandatoryAffectedColumn = false;
	var showAlertDialog = (showAlert!=undefined)?showAlert:true;
	
	if(arguments.length >= 2)
	{
		if (typeof arguments[1] == 'object')
		{
			var len = rowMandatoryField.length;
			for(var i= 0;i<len;i++){
				currFieldIsRowMandatoryField = $elem.attr('id') == rowMandatoryFieldID[i];
				if(currFieldIsRowMandatoryField){
					break;//continue;
				}
			}
			//checkes if the current cell should not be validated for the mandatory field
			if(rowMandatoryNonAffectedColumnsId){
				len = rowMandatoryNonAffectedColumnsId.length;
				for(var i= 0;i<len;i++){
					currentFielsIsNonMandatoryAffectedColumn = $elem.attr('id') == rowMandatoryNonAffectedColumnsId[i];
					if(currentFielsIsNonMandatoryAffectedColumn){
						break;//continue;
					}
				}
			}
			
		}
	}
	
	/*Check before if field that set to be mandatory for whole row and leave the non mandatory affected columns to not be checked */
	var len = rowMandatoryField.length;
	for(var i= 0;i<len;i++)
	{
		if(rowMandatoryFieldID[i] != "" && !currFieldIsRowMandatoryField && !currentFielsIsNonMandatoryAffectedColumn)
		{
			var mfieldval = "";
			var $currRowMandatoryField = $('#'+rowMandatoryFieldID[i]);
			var currMandatoryFieldTagName = $currRowMandatoryField.prop('tagName');
			if(currMandatoryFieldTagName.toLowerCase() == 'select')
			{
				mfieldval = $.trim($currRowMandatoryField.find('option:selected').val());
				mfieldval = (mfieldval == "0")?"":mfieldval;
			}
			else if(currMandatoryFieldTagName.toLowerCase() == 'div')
			{
				mfieldval = $currRowMandatoryField.text().trim();
			}
			else
			{
				mfieldval = $currRowMandatoryField.val().trim();
			}
			if(mfieldval == "")
			{
				if(showAlertDialog)
					displayAlertDialog(rowMandatoryFieldDisplayName[i]+" is mandatory field. The data will not be saved.");	
				
				return false;
			}
		}
	}
	return true;
}

function beforeEditableDataTableClickEvent(domId, funcName, funcParamsArr, additParamsArr)
{
	if(!validateRowMandatoryField(additParamsArr))
	{
		return;
	}

	generalBL_elementDataTableClickEvent(domId, funcName, funcParamsArr);
}

function onChangeTableCell(htmlObj, domId, rowInx, origColInx, paramsObj)
{
	console.log("------START ---ON CHANGE TABLE CELL EVENT-------");
	console.log(" htmlObj:", htmlObj);
	console.log(" paramsObj:", paramsObj);
	
	/* get params **/
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
	var $htmlObj = $(htmlObj);
	var insertIntoSelectTable = paramsObj.insertIntoSelectTable;
	
	// Call function to do some manipulations before code below
	if(customFuncOnBeforeChange != "")
	{
		bl_elementDatatableEditableCustomFuncHandler(domId, customFuncOnBeforeChange, $htmlObj, []);
	}
	
	var newVal = "";
	var oldVal = $.trim($htmlObj.attr('oldValue'));	
	var onChangeFormId = $htmlObj.attr('rowId');
	var isMultiple = ($htmlObj.attr('multiple') == 'multiple')?true:false;
	var plainTextRichTextValue = "", htmlRichTextValue = "", richTextValueForFilter = "";
	var plainTextValue = "";
	var displaySelectValues = "";
	var updateCellFilterValue = "";
	var multipleSelectValToUpdate = "";
	var oldValueOnSuccess = "";
	var rowMandatoryNonAffectedColumnsId = $htmlObj.attr('rowMandatoryNonAffectedColumnsId')?$htmlObj.attr('rowMandatoryNonAffectedColumnsId').split(","):"";
	var currentFielsIsNonMandatoryAffectedColumn = false;
	
	//checkes if the current cell should not be validated for the mandatory field
	if(rowMandatoryNonAffectedColumnsId){
		var len = rowMandatoryNonAffectedColumnsId.length;
		for(var i= 0;i<len;i++){
			currentFielsIsNonMandatoryAffectedColumn = $htmlObj.attr('id') == rowMandatoryNonAffectedColumnsId[i];
			if(currentFielsIsNonMandatoryAffectedColumn){
				break;//continue;
			}
		}
	}
	
	/*Check before if field that set to be mandatory for whole row  */
	if(rowMandatoryFieldID != "" && $htmlObj.attr('id') != rowMandatoryFieldID && !currentFielsIsNonMandatoryAffectedColumn)
	{
		var mfieldval = "";
		var mandatoryFieldTagName = $('#'+rowMandatoryFieldID).prop('tagName');
		if(mandatoryFieldTagName != undefined)
		{
			if(mandatoryFieldTagName.toLowerCase() == 'select')
			{
				mfieldval = $.trim($('#'+rowMandatoryFieldID).find('option:selected').val());
				mfieldval = (mfieldval == "0")?"":mfieldval;
			}
			else if(mandatoryFieldTagName.toLowerCase() == 'div')
			{
				mfieldval = $('#'+rowMandatoryFieldID).text().trim();
			}
			else
			{
				mfieldval = $.trim($('#'+rowMandatoryFieldID).val());
			}
		}
		if(mfieldval == "")
		{
			if(htmlType == "select" || htmlType == "checkbox")
			{
				if(htmlType == "checkbox")
				{
					$htmlObj.prop('checked', !$htmlObj.prop('checked'));
				}
				displayAlertDialog(rowMandatoryFieldDisplayName+" is mandatory field. The data will not be saved.");
			}
			console.log("onChangeTableCell(): " + rowMandatoryFieldDisplayName+" is mandatory field. The data will not be saved.");
			return;
		}
	}
	
	/* Step 1: get value of current input */
	if(htmlType == "selectOperType")
	{
		newVal = $.trim($htmlObj.val());	
		displaySelectValues = $.trim($htmlObj.find('option:selected').text());
	}	
	else if(htmlType == "text" || htmlType == "date" || htmlType == "time")
	{
		newVal = $.trim($htmlObj.val());	
		if($htmlObj.attr('type') == 'number')
		{
			$htmlObj.attr('originalValue', newVal);
			if(!isCellAutoSaved) $htmlObj.attr('title', newVal);
		}
	}
	else if(htmlType == "checkbox")
	{
		newVal = $htmlObj.prop('checked')?1:0;	
	}
	else if(htmlType == "textarea")
	{		
		newVal = $.trim($htmlObj.val());
		plainTextValue = newVal.replace(/\n/g, "\\n");  //new line
		//console.log("textarea",newVal);
	}
	else if(htmlType == "editableDiv")
	{		
		var html = $htmlObj.html();
		plainTextValue = $htmlObj.text();
		console.log("divEditable: ", html);
		
		if (!fnValidateString({ inStr:plainTextValue, fieldName:colTitle, focusOn:$htmlObj.attr('id')}))
		{
			return false;
		}
		
		newVal = returnBasicHtml(html); // yp 28122020 - fix bug 8759 - remove all tag except b u i using returnBasicHtml (new function in general func)
		
		console.log("divEditable escaped: ",newVal);	
		console.log("divEditable old value: ",oldVal);	
		
		if(newVal.length > $htmlObj.attr('data-max-length'))
		{
			displayAlertDialog("Value too large for column " + colTitle + " (" + newVal.length + " is above " + $htmlObj.attr('data-max-length') + " limitation)");
			return false;
		}		
	}
	else if(htmlType == "select")
	{
		if(isMultiple)
		{
			
			//$htmlObj.find('option:selected').attr('disabled', true).trigger('chosen:updated');
			newVal = $htmlObj.find('option:selected').map(function()
			        {
						if(displaySelectValues.length > 0)
        	        	{
							displaySelectValues += ',';
        	        	}
						displaySelectValues += $.trim($(this).text());
						return  $.trim($(this).val());
			        })
			        .get()
			        .join();
			
			var oldValArr = (oldVal == "")?[]:oldVal.split(",");
			var newValArr = (newVal == "")?[]:newVal.split(",");
			
			if(oldValArr.length > 0)
			{
				for(var i=0;i<newValArr.length;i++)
				{
					if($.inArray(newValArr[i], oldValArr) == -1)
					{
						multipleSelectValToUpdate = newValArr[i]+",1;";
					}
				}
				if(multipleSelectValToUpdate == "")
				{
					for(var i=0;i<oldValArr.length;i++)
					{
						if($.inArray(oldValArr[i], newValArr) == -1)
						{
							multipleSelectValToUpdate = oldValArr[i]+",0;";
						}
					}
				}
			}
			else
			{
				multipleSelectValToUpdate = newVal+",1;";
			}
		}
		else
		{
			newVal = $.trim($htmlObj.find('option:selected').val());		
			displaySelectValues = $.trim($htmlObj.find('option:selected').text());
		}
		if(!isCellAutoSaved){//if the cell is auto saved then the oldValue attribute gets the new value after saving the element
			$htmlObj.attr('oldValue',newVal);
		}
		//console.log("oldValue", newVal);
	}
	else if(htmlType == 'richtext')
	{
		htmlRichTextValue = $.trim($htmlObj.html());
		plainTextRichTextValue = $.trim($htmlObj.attr('plainTextValue')); 
		if(plainTextRichTextValue==""){
			plainTextRichTextValue = $.trim($htmlObj.text());
		}
		richTextValueForFilter = plainTextRichTextValue.replace(/\n/g, " ");  //replace new line with empty space for filter
		newVal = htmlRichTextValue;
		var legals = "apostrophe";

		if (!fnValidateString({ inStr:plainTextRichTextValue, fieldName:colTitle, focusOn:$htmlObj.attr('id'), inLegals: legals}))
		{
			return false;
		}

	}
	else if(htmlType=='checkCharSample')
	{
		var $box = $htmlObj;
		if($box.attr("group")!== undefined){//the checkboxe in the column are in group
			if ($box.is(":checked")) {
			    // the name of the box is retrieved using the .attr() method
			    // as it is assumed and expected to be immutable
			    var group = "input:checkbox[group='" + $box.attr("group") + "']";
			    // the checked state of the group/box on the other hand will change
			    // and the current value is retrieved using .prop() method
			    $(group).prop("checked", false);
			    $box.prop("checked", true);
			    $('#'+dbColumnName).val(onChangeFormId);
		    } else {
		    	$box.prop("checked", false);
		    	$('#'+dbColumnName).val('');
		    }
		} else {
			var lastValue = $('#'+dbColumnName).val();
			var lastValArr=[];
			if(lastValue.length > 0){
				lastValArr = lastValue.split(',');
			}
			if ($box.is(":checked")) {
				lastValArr.push(onChangeFormId);
			} else {
				const index = lastValArr.indexOf(onChangeFormId);
				lastValArr.splice(index, 1);
			}
			$('#'+dbColumnName).val(lastValArr.toString());
		}
		return;
	}
	
	// Step 1.1: call  custom function
	if(customFuncName != "")
	{
		bl_elementDatatableEditableCustomFuncHandler(domId, customFuncName, $htmlObj, customFuncParams);
	}
	
	/* Step 2: check if current input is mandatory */
	// TODO: define select/select multiple empty value checking when mandatory
	if( isMandatory == true 
			&&
		(
			((htmlType == "text" || htmlType == "date" || htmlType == "time" || htmlType == "textarea" || htmlType == "editableDiv") && newVal == "")
				||
			(htmlType == "select" && (newVal == "0" || newVal == "-1"))
		)
	  )
	{
		displayAlertDialog(colTitle+" is mandatory field.");
		//return;
	}
	
	/* Step 3: prevent from save in case value not changed */
	if((newVal == oldVal && htmlType != "checkbox")|| newVal === oldVal)
	{
		console.log("There were no changes (newVal == oldVal)");
		return;
	}
	
	if($('table[id="' + domId + '"]')[0].hasAttribute("PREVENT_FROM_INLINE_EDITING_STATE"))
	{
		return;
	}
	if(isRenderTable)
	{
		$('table[id="' + domId + '"]').attr("PREVENT_FROM_INLINE_EDITING_STATE","1");
		showWaitMessage("Please wait, the table is going to be refreshed.");
	}
	
	/* Step 4: save oldValue for further use if save successed */
	oldValueOnSuccess = newVal;
	/* Step 4.1: update value to 'tooltip' attribute of current input  */
	if($htmlObj[0].hasAttribute('title'))
	{
		if(htmlType == 'richtext')
		{			
			$htmlObj.attr('title',plainTextRichTextValue);
		}
		else
		{
			$htmlObj.attr('title',newVal);
		}
	}
	
	/* Step 5: get value for cell filterData */
	if(htmlType == 'richtext')
	{
		updateCellFilterValue = richTextValueForFilter;
	}
	else if(htmlType == 'select')
	{
		updateCellFilterValue = displaySelectValues;
	}
	else if(htmlType == "editableDiv" || htmlType == "textarea")
	{		
		updateCellFilterValue = plainTextValue;
	}
	else
	{
		updateCellFilterValue = newVal;
	}
	
	
	if(isCellAutoSaved)
	{
	
			/* Step 6: redefine values for special inputs */
			if(htmlType == 'richtext')
			{
				var _obj = {
			            "value": htmlRichTextValue,
			            "plainText": plainTextRichTextValue
			        };
				newVal = JSON.stringify(_obj);
				oldVal = ""; // not relevant
			}
			else if(htmlType == "select" && isMultiple  && insertIntoSelectTable!=="false")
			{
				newVal = multipleSelectValToUpdate;
			}
			
			/* when saveType == 'monitoringParams', htmlType can be as 'text' as 'select' */
			if(saveType == 'monitoringParams')
			{
				dbColumnName = $htmlObj.attr('mp_name');
				var _obj = {
						"mpFormid": $htmlObj.attr('mp_formid'),
						"mpName":$htmlObj.attr('mp_name'),
						"value": newVal,
			            "isUOM":$htmlObj.attr('is_uom')
			        };
				newVal = JSON.stringify(_obj);
			}
			else if(saveType == "pivot") {
				var _obj = {
						"pivotFormId": paramsObj.pivotFormId,
						"value": newVal
			        };
				newVal = JSON.stringify(_obj);
			}

			if(formNumberID == "")
			{				
				formNumberID = editableBL_getAdditParams(domId,rowInx,"formNumberID");
				console.log("formNumberID", formNumberID);
			}
			
			/* Step 7: define data array and save */
			var allData = [
				{code:"formId",val:$('#formId').val()}, 
				{code:"formCode",val:formCode},
				{code:"parentFormCode",val:$('#formCode').val()},
				{code:"userId",val:$('#userId').val()},
				{code:"formNumberID",val:formNumberID},
				{code:"saveType",val:saveType},
				{code:"onChangeFormId",val:onChangeFormId},
				{code:"onChangecolumnName",val:dbColumnName},
				{code:"onChangecolumnVal",val:newVal},
				{code:"oldVal","val":oldVal}
			];
			
			var data_ = JSON.stringify({
				action : "onChangeDataTableCell",
				data : allData,
				errorMsg : ""
			});
			
			console.log("onChangeDataTableCell AJAX call data_", data_);
		
			$.ajax({
				type : 'POST',
				data : data_,
				url : "./onChangeDataTableCell.request?stateKey=" + $('#stateKey').val(),
				contentType : 'application/json',
				dataType : 'json',
				success : function(obj) {
		
					if (obj.errorMsg != null && obj.errorMsg != '') 
					{
						displayAlertDialog(obj.errorMsg);
						console.log("---ERROR onChangeDataTableCell on SUCCESS :",obj.errorMsg);
						//displayAlertDialog('update '+colTitle+' failed');ta 060119 fixed bug 7184
					} 
					else 
					{
						var _rVal = obj.data[0].val;
						console.log("--- onChangeDataTableCell on SUCCESS :",_rVal);
						
						if(_rVal == '0')
						{
							if(htmlType == "select")
							{
								isRenderTable = true;
							}
							displayAlertDialog(colTitle+' '+getSpringMessage('CELL_NOT_SAVED'));
						}
						else if(_rVal == '-1')
						{					
							if(htmlType == "select")
							{
								isRenderTable = true;
							}
							displayAlertDialog(colTitle+' '+getSpringMessage('CELL_ALREADY_CHANGED'));
						}
						else if(_rVal == '-2')
						{
							isRenderTable = true;
							displayAlertDialog(getSpringMessage('Sample can not be deleted.'));
						}
						else if(_rVal == '-3')
						{
							displayAlertDialog(getSpringMessage('updateFailed'));
						}
						else if(_rVal == '-4')
						{
							displayAlertDialog(getSpringMessage('invalidDate'));
						}
						else if(_rVal == '-5')
						{
							displayAlertDialog(getSpringMessage('invalidTime'));
						}
						else if(_rVal.indexOf("saved value") > 0)
						{					
							//if(htmlType == "select")
							//{
								isRenderTable = true;
							//}
							displayAlertDialog(colTitle+' '+getSpringMessage('CELL_ALREADY_CHANGED')+ _rVal);
						}
						else //if _rVal == '1' or _rVal is jsonObject
						{
							/* update/set value to 'old value' attribute of current input  */
							if(htmlType != "select")
							{
								$htmlObj.attr('oldValue',oldValueOnSuccess);
							}
						}
						/*if(isRenderTable)
						{
							//$('#' + domId + '_editableLastFocusField').val($htmlObj.attr('id'));
						}*/
						bl_elementDatatableEditableAfterSaveHandler(domId, isRenderTable, _rVal, $htmlObj, paramsObj, rowInx);
					}
					hideWaitMessage();
				},
				error : handleAjaxError
			});
	}
	
   /* Last step: update cell filterData that used in search engine of datatable with 'updateCellFilterValue'*/
	// get current column index
	var currColInx = dtExt_convertColumnInx($('#' + domId).DataTable(), origColInx);
	dtExt_updateCellFilterData(domId, rowInx, currColInx, updateCellFilterValue);
	
	
	console.log("-----END ---ON CHANGE TABLE CELL EVENT-----");
}

/* function returns array of whole table data or specific row data(by rowIndex) 
 * TODO: add value of 'old value' to the returned json object 
 * */
function getDataFromEditableTable(domId, rowInx)
{    
	var fullDataArr = {};
	var domIdArr = [];
	
	// variables for error msg data
	var e_curRowIndex = -1,
		e_curDomId = "",
		e_failedData = "";
	try 
	{
		if(domId instanceof Array)
		{
			domIdArr = domId;
		}
		else
		{
			domIdArr[0] = domId;
		}
//		console.time("TIME getDataFromEditableTable");
		for(var j=0; j<domIdArr.length;j++)
		{
			var curDomId = domIdArr[j];
			e_curDomId = curDomId;
			var table = $('#'+curDomId).DataTable();	
			var loopObj = (rowInx == -1)?table.rows():table.row(rowInx);
	
			loopObj.eq(0).each( function ( index ) 
			{
				e_curRowIndex = index+1;
				var rowDataObj = {};
			    var cellsDataObj = {};
				var row = table.row( index );	 
			    var rowData = row.data();
			    var fullDataArrInd = (rowData[0] == -99999)?index:rowData[0]; // -99999 when there is a pivot table ('Taro develop')
			    //console.log("getDataFromEditableTable() rowData: ",rowData);
			    var dataLength = rowData.length;
			    for(var i=0;i<dataLength;i++)
			    {	    	
			    	
			    	var cell = table.cell({row: index, column: i});	 
				    var cdata = cell.data();
				    var node = cell.node();
				    //console.log(cdata);
				    //console.log(node);
				    
				    var cellVal;
				    //'find select' should be before 'find input' because chosen also contain 'input field inside'
				    if($(node).find('select').length > 0)
		    		{
		    			var $select = $(node).find('select');
		    			cellVal = $.trim($select.find('option:selected').val());
		    			if(cellVal == "0")cellVal = "";
		    		}
				    else if($(node).find('input').length > 0)
		    		{
		    			var $input = $(node).find('input');
		    			if($input.attr('type') == "checkbox")
		    			{
			    			cellVal = $input.prop('checked')?1:0;
		    			}
		    			else if($input.attr('type') == "number")
		    			{
		    				cellVal = $input.attr('originalValue');
		    			}
		    			else
		    			{
		    				cellVal = $.trim($input.val());
		    			}
		    		}
		    		else if($(node).find('textarea').length > 0)
		    		{
		    			var $textarea = $(node).find('textarea');
		    			cellVal = $.trim($textarea.val());
		    		}
		    		else if($(node).find('span.viewableSmartCell').length > 0)
		    		{
		    			var $span = $(node).find('span.viewableSmartCell');
		    			if($span.attr('spanType') == "number")
		    			{
		    				cellVal = $.trim($span.attr('originalValue'));
		    			}
		    			else
		    			{
		    				cellVal = $.trim($span.text());
		    			}
		    		}
		    		else if(checkIfJSON(cdata)) // in case current node doesn't have html object, check for necessary data in json
		    		{
		    			e_failedData = JSON.stringify(cdata);
		    			var _obj = funcParseJSONData_errorThrowed(cdata);
		    			if(_obj instanceof Object)
		    			{
		    				// get addittional data
		    				if(_obj.hasOwnProperty("hiddenRowData"))
		    				{
		    					var _tObj = _obj["hiddenRowData"];
		    					for(var key in _tObj)
		    					{
		    						cellsDataObj[key] = _tObj[key];
		    	    				//console.log(key, _tObj[key]);	    	    				
		    					}
		    					continue;
		    				}
		    				else
		    				{
		    					cellVal = $(node).text();
		    				}
		    				/*else
		    				{
		    					// get value from json object
		    					cellVal = getDisplayValueFromSmartsParsed(funcParseJSONData("["+cdata+"]",true));
		    				}*/
		    			}
		    			/*else if(_obj instanceof Array)
		    			{
		    				// get value from json array
		    				cellVal = getDisplayValueFromSmartsParsed(_obj);
		    			}*/
		    		}
		    		else
		    		{
		    			cellVal = $(node).text();
		    		}
		    		//console.log("cellVal",cellVal);
		    		
		    		//get column params from json
		    		var cellObj = {};
		    		if (checkIfJSON(cdata)) {
		    			cellObj = funcParseJSONData_errorThrowed(cdata);
		    			if(cellObj.hasOwnProperty("colCalcId")) // for fg_s_materialref_dte___ tables
		    			{
		    				cellsDataObj[cellObj.colCalcId] = cellVal;
		    				//console.log(cellObj.colCalcId,cellVal);
		    			}
		    			else if(cellObj.hasOwnProperty("pivotFormId")) // for 'Taro develop' (fg_s_api_stb_result_dte_v)
		    			{
		    				cellsDataObj[cellObj.pivotFormId] = cellVal;
		    			}
		            } 	
			    }
			    fullDataArr[fullDataArrInd] = cellsDataObj;
			} );
		}
	} 
	catch (e) {
//		console.timeEnd("TIME getDataFromEditableTable");
		console.log("error in getDataFromEditableTable()", e);
		console.log("fullDataArr ", fullDataArr);
		console.log("cellData ", e_failedData);
		return "There is problem with "+e_curDomId+" table data on row " + e_curRowIndex + ".\n Failed on data: " + e_failedData+".";
	}
	console.log("getDataFromEditableTable() return data: ", fullDataArr);
//	console.timeEnd("TIME getDataFromEditableTable");
	return fullDataArr;
}

function updateEditableTableData(domId, data)
{	
	
	var fullDataArr = {};
	var domIdArr = [];
	try 
	{
//		console.time("TIME updateEditableTableData");
		var dataObj = funcParseJSONData(data); 
		console.log("updateEditableTableData() new data parsed: ", dataObj);
		if(Object.keys(dataObj).length > 0)
		{
			if(domId instanceof Array)
			{
				domIdArr = domId;
			}
			else
			{
				domIdArr[0] = domId;
			}
			for(var j=0; j<domIdArr.length;j++)
			{
				var curDomId = domIdArr[j];			
				var table = $('#'+curDomId).DataTable();
				table.rows().eq(0).each( function ( index ) 
				{
					var row = table.row( index );	 
				    var rowData = row.data();
				    var rowID = rowData[0];
				    var updatedRowDataObj = [];
		
			    	if(dataObj.hasOwnProperty(rowID))
					{
			    		updatedRowDataObj = dataObj[rowID];
			    		
			    		var dataLength = rowData.length;
					    for(var i=0;i<dataLength;i++)
					    {	    				    	
					    	var cell = table.cell({row: index, column: i});	 
						    var cdata = cell.data();
						    var cnode = cell.node();
						    var cellObj = {};
						    if (checkIfJSON(cdata)) { // check if json or not
						    	cellObj = funcParseJSONData(cdata);
						    	var colName = cellObj.colCalcId;
						    	if(updatedRowDataObj.hasOwnProperty(colName))
						    	{
						    		var _newVal = updatedRowDataObj[colName];
						    		if($(cnode).find('select').length > 0)
						    		{
						    			$(cnode).find('select').val(_newVal).trigger('chosen:updated');
						    			$(cnode).find('select').attr('oldValue',_newVal);
						    		}
						    		else if($(cnode).find('input').length > 0)
						    		{
						    			var $input = $(cnode).find('input');
						    			$input.attr('oldValue',_newVal);
						    			if($input.attr('type') == "number")
						    			{
						    				$input.attr('originalValue',_newVal);
						    				$input.attr('title',_newVal);
							    			
							    			//console.log(colName + " -> convertToExponMarker",$input.attr('convertToExponMarker'));
							    			if($input.attr('convertToExponMarker') !== undefined)
							    			{						    										    				
							    				$input.val(convertDecimalToExponential(_newVal));
							    				$input.attr('oldValue',convertDecimalToExponential(_newVal));
							    			}
							    			else
							    			{
							    				$input.val(_newVal);
							    			}
						    			}
						    			else
						    			{
						    				$input.val(_newVal);
						    			}						    			
						    		}
						    		else if($(cnode).find('textarea').length > 0)
						    		{
						    			$(cnode).find('textarea').val(_newVal);
						    			$(cnode).find('textarea').attr('oldValue',_newVal);
						    		}
						    		else if($(cnode).find('span.viewableSmartCell').length > 0)
						    		{
						    			var $span = $(cnode).find('span.viewableSmartCell');
						    			$span.attr('oldValue',_newVal);
						    			if($span.attr('spanType') == "number")
						    			{
						    				$span.attr('originalValue',_newVal);
						    				$span.attr('title',_newVal);
						    				if($span.attr('convertToExponMarker') !== undefined)
							    			{						    										    				
							    				$span.text(convertDecimalToExponential(_newVal));
							    				$span.attr('oldValue',convertDecimalToExponential(_newVal));
							    			}
							    			else
							    			{
							    				$span.text(_newVal);
							    			}
						    			}
						    		}
						    		else
						    		{
						    			$(cnode).text(_newVal);
						    			$(cnode).attr('oldValue',_newVal);
						    		}
						    		//important: update column filter with new data
						    		dtExt_updateCellFilterData(domId, index, i, _newVal);
						    	}
				            }
					    }
					}
				});
			}
		}
	}
	catch(e)
	{
		console.log("error in updateEditableTableData() for domId", domId)
		console.log("error in updateEditableTableData() error", e);
	}
//	console.timeEnd("TIME updateEditableTableData");
}

function openRichtextContent(input, e, domId, colInd, rowInd, formCode, dbColName)
{
	try
	{
		if(!onEditableTableEvent(input, e))
			{
				return;
			}
		
		//var currHtml = $.trim($(input).attr('origHtmlValue'));
		var currHtml = $.trim($(input).html());
		popupRichText(input, domId, colInd, rowInd, currHtml);
	}
	catch(e)
	{
		console.log("openRichtextContent error:", e);
	}
}

function popupRichText(input, domId, colInd, rowInd, html)
{	
	var $tbody = $(input).parent();
		
	toggleFocusToTablecell($(input).parent(), true);

	$("#divPopupRichText").addClass("not_close");
	$("#divPopupRichText").parent().find("div.ui-draggable-handle").css('cursor', 'move');
	
	if($("#divPopupRichText").dialog('isOpen'))
	{
		setNewCellDataRichText();
		
		if($("#divPopupRichText").attr('curRowIndex') != rowInd)
		{
			loadRichtextContentById($('#tablecellRichtextInstance'),html);
		}
		else
		{
			toggleFocusToTablecell($(input).parent(), true);
		}
		$("#divPopupRichText").attr('curRowIndex', rowInd);
		setFocusToTablecellRichtextInstance();
	}
	else
	{
		$("#divPopupRichText").attr('curRowIndex', rowInd);
		$("#divPopupRichText").attr('curTableID', domId);
		$("#divPopupRichText").attr('origColIndex', colInd);
		
		loadRichtextContentById($('#tablecellRichtextInstance'),html);
		
		$("#divPopupRichText").dialog({
						  position: { my: "left", at: "right", of: $tbody }
						});
		$("#divPopupRichText").dialog('open');
	}		
}

function setNewCellDataRichText()
{
	var rowInd = $("#divPopupRichText").attr('curRowIndex');
	var domId = $("#divPopupRichText").attr('curTableID');
	var table = $('#'+domId).DataTable();
	var origColIndex = $("#divPopupRichText").attr('origColIndex');
	var colInd = dtExt_convertColumnInx(table, origColIndex);
	var instObj = $('#tablecellRichtextInstance');
	var newCellData_html = getValueForElementRichText(instObj);	
	var newCellData_text = getPlainTextFromHtml(newCellData_html);
	//console.log("text:", newCellData_text);
	var row = table.row(rowInd).node(); 
	var txtAreaObj = $(table.cell(row, colInd).node()).find('div');
	
	txtAreaObj.attr('oldValue',txtAreaObj.html());
	txtAreaObj.attr('plainTextValue',newCellData_text);
	txtAreaObj.html(newCellData_html).trigger('change');
	txtAreaObj.trigger('blur');
	toggleFocusToTablecell($(table.cell(row, colInd).node()), false);
}

function getPlainTextFromHtml(html)
{
	var dom = document.createElement("DIV");
    dom.innerHTML = html;
    var plain_text = (dom.textContent || dom.innerText);
    //plain_text = plain_text.replace(/\r?\n|\r/gm," "); // remove line breaks   
    //plain_text = plain_text.replace(/\s\s+/g, " ").trim(); // remove double spaces
    return plain_text;	
}

// toggle focus on richtext field when popuped
function setFocusToTablecellRichtextInstance()
{ 
	var timer = 0;
    timer = setTimeout(function() 
    {
    	focusRichtext($('#tablecellRichtextInstance'));
		
	    clearTimeout(timer);
	    
    }, 100);
}

// set focus by add border color to clicked cell 
function toggleFocusToTablecell(cell, doFocus)
{
	if(doFocus)
	{
		cell.css('border', '3px solid #e9ef79');
	}
	else
	{
		cell.css('border', '1px solid #bdbdbd');
	}
}

function dataTableAddRow(domId,doAddMultiRows)
{	
	var rowNumToAdd = 1;
	if(doAddMultiRows!=undefined && doAddMultiRows==true){
		var mandatoryIndicator= isMandatoryFieldsRequired();
	    if (!(mandatoryIndicator.setRequired == '1'&& checkRequired() || (mandatoryIndicator.setRequired == '0' && checkRequiredByList(mandatoryIndicator.mandatoryList)))
	    || (!checkDateMinMaxValidity() || !checkNumberMinMaxValidity() || !checkTimeValidity() || !checkEmailValidity() 
        		|| !elementDynamicParamsImpValidation() || !elementRichTextEditorValidation() || !elementWebixValidation())
        		
            ) 
        {
        		return;
        }
		rowNumToAdd = $('#rowQuantity').val();
	}
	
	console.log("full globalEditableTableEmptyRowHolder", globalEditableTableEmptyRowHolder);
	var toReturn = "";
	if(Object.keys(globalEditableTableEmptyRowHolder).length > 0 && globalEditableTableEmptyRowHolder.hasOwnProperty(domId))
	{		
		var table = $('#'+domId).DataTable();
		var currTableArr = globalEditableTableEmptyRowHolder[domId];			
		var rowID = currTableArr[0];
		var newRowTmpId = (Number(rowID)-1).toString();	
		toReturn = newRowTmpId;			
		var newDataArr = [];
		for(var i=0;i<currTableArr.length;i++)
		{
			if(i==0)
			{
				newDataArr[0] = newRowTmpId;
			}
			else
			{
				newDataArr[i] = currTableArr[i];
			}
		}
		globalEditableTableEmptyRowHolder[domId] = newDataArr;		
		dataTableAddRowAndGoToPage(domId, newDataArr);		
	}
	else
	{
		var allElementsData = getformDataNoCallBack(1);
		var parentFormCode = $('#formCode').val();
		var formId =$('#formId').val();
		var formCode = encodeURIComponent($('[id="' + domId + '_structCatalogItem"]').val());
		var tableType = $('#'+domId+'_tableType').val();
		var bl_excludeParentCondition = parent.$('#formCode').val() != 'Maintenance'// under maintenance screen
			 					        && (parentFormCode != null && parentFormCode !='StepMinFr'); // under StepMinFr screen
		if(window.self !== window.top && bl_excludeParentCondition) {
			parentFormCode = parent.$('#formCode').val();
			formId = parent.$('#formId').val();
			formCode = encodeURIComponent(parent.$('[id="' + domId + '_structCatalogItem"]').val());
			allElementsData = getformDataNoCallBack(1,parent.$('body'));
			tableType = parent.$('#'+domId+'_tableType').val();
		}
		
		var allData = [
			{code:"formId",val:formId}, 
			{code:"formCode",val:formCode},
			{code:"parentFormCode",val:parentFormCode},
			{code:"userId",val:$('#userId').val()},
			{code:"domId",val:domId},
			{code:"rowNumToAdd",val:rowNumToAdd},
			{code:"tableType",val:tableType}
		];
		allData = allData.concat(allElementsData);
		
		var data_ = JSON.stringify({
			action : "dataTableAddRow",
			data : allData,
			errorMsg : ""
		});
		// call...
		$.ajax({
			type : 'POST',
			data : data_,
			url : "./dataTableAddRow.request?stateKey=" + $('#stateKey').val(),
			contentType : 'application/json',
			dataType : 'json',
			async:false,
			success : function(obj) {
				if (obj.errorMsg != null && obj.errorMsg != '') {
					displayAlertDialog(obj.errorMsg);
				}				
				else if(($('#formCode').val()=='Step' &&
						(domId == "reactants" || domId == "solvents" || domId == "products"))) //TODO: config through settings(form builder) which case should be choosed(domId =="action"&&$('#'+domId).DataTable().rows().count()>0||)
				{	
					var dataStr = obj.data[0].val;		
					if(window.self !== window.top){
						var returnParam = obj.data[0].val;
		                parent.$('#prevDialog').data('isRowsAdded', true);
		                parent.$('#prevDialog').data('returnParam', returnParam);
		                parent.$('#prevDialog').data('rowData', dataStr);
		                parent.$('#prevDialog').dialog('close');
		                return;
					}
					if(dataStr != null)
					{
						var dataArr = $.parseJSON(dataStr)[0];
						console.log("dataTableAddRow() new row data ", dataArr);
						if(dataArr.length > 0)
						{
							globalEditableTableEmptyRowHolder[domId] = dataArr;
							toReturn = dataArr[0];
							dataTableAddRowAndGoToPage(domId, dataArr);							
						}
					}
				}
				else {
					var bl_excludeParentCloseDialog = parent.$('#formCode').val() != 'Maintenance'// under maintenance screen
					        						  && ($('#formCode').val() == 'MultiAddRows' || (parentFormCode != null && parentFormCode !='StepMinFr')); // under StepMinFr screen if not MultiAddRows (in action table)
					if(window.self !== window.top && bl_excludeParentCloseDialog) {
						//parent.$("iframe").attr('src', 'about:blank');
						var returnParam = obj.data[0].val;
		                parent.$('#prevDialog').data('isRowsAdded', true);
		                parent.$('#prevDialog').data('returnParam', returnParam);
		                parent.$('#prevDialog').dialog('close');
		                return;
					}
					onElementDataTableApiChange(domId); 
					var returnParam = obj.data[0].val;
					bl_elementDatatableEditableCustomFuncHandler(domId, "onTableRowAdded", null, [returnParam]);
				}
				hideWaitMessage();
			},
			error : handleAjaxError
		});
	}
	return toReturn;
}

function dataTableAddRowAndGoToPage(domId, dataArr, rowNumToAdd)
{
	try {
			var table = $('#'+domId).DataTable();		
			var newRow = table.row.add(dataArr).draw(false);
			var newRowIndex = newRow.index();
			//console.log("newRowIndex", newRowIndex);
			var _pageSettings = table.page.info();
		    var _rowsPerPage = _pageSettings.length;
		    var _pageInd = Math.floor(newRowIndex / _rowsPerPage); 
		    
		    if(newRowIndex != null) {
		    	 /*table.rows( newRowIndex )
		    	    .nodes()
		    	    .to$()
		    	    .addClass( 'selected' );*/
		    	 table.page(_pageInd).draw('page');
		    } else {
		    	table.page(0).draw('page');
		    }
		    bl_elementDatatableEditableCustomFuncHandler(domId, "onTableRowAdded", null, [dataArr[0],newRowIndex],rowNumToAdd);	 
		    
		    
	} 
	catch (e) {
		console.log("error in addRowAndGoToPage() dataArr: ", dataArr);
		console.log("error", e);
	}
}

/**
 * Download file
 * @returns
 */
function smartFile(domId, fileId) 
{
	//console.log('smartFile() domId: ' + domId + "|fileId:" + fileId);
	if(fileId == null || fileId == "")
	{
		displayAlertDialog("Not enough data to download file.");
	}
	else
	{
		$('[name="' + domId + '_FILE_ID"]').val(fileId);
		$('#' + domId + '_AttachmentForm').submit();
	}
}

function checkAndNavigate(paramsArr)
{	
	//console.log("checkAndNavigate() params:", paramsArr);
	var formId = paramsArr[0]; 
	var formCode = paramsArr[1];	
	
	if(formId == null || formId == "")
	{
		displayAlertDialog("Not enough data to open link.");
	}
	else if(formCode == null || formCode == "")
	{
		formCode = getFormCodeBySeqId(formId);
		if(formCode == null || formCode == ""){
			displayAlertDialog("Not enough data to open link.");
		}else{
			paramsArr[1] = formCode;
			confirmWithOutSaveWithPermissions(smartLink, paramsArr, [formCode,formId]);
		}
	}
	else
	{	
		confirmWithOutSaveWithPermissions(smartLink, paramsArr, [formCode,formId]);
	}
}

/**
 * Redirect link
 * paramsArr: 0-formid, 1-formcode, 2-tab, 3-isCheckDataChanged, 4-hideMsg, 5-openMode (if not defined or "self" will be open in the current tab else in new tab)
 * @returns
 */
function smartLink(paramsArr) 
{	
	_isTableRowLinkClicked = true;
	var formId = paramsArr[0]; 
	var formCode = paramsArr[1];
	var tab = '';
	if(paramsArr.length > 2) {
		tab = paramsArr[2];
	}
	var hideMsg = false;
	if(paramsArr.length > 4) {
		hideMsg = paramsArr[4];
	} 
	
	if(!hideMsg) {
		showWaitMessage(getSpringMessage('Loading...'));
	}
	var openMode = "self";
	if(paramsArr.length > 5) {
		openMode = paramsArr[5];
	} 
	
	console.log('smartLink() paramsArr: '+paramsArr);
	//yp 07122020 note: using parent to support also iframe step in formulation (for navigate beck to experiment formulation)
	var page = "./init.request?stateKey=" + parent.$('#stateKey').val() + "&formCode=" + formCode + "&formId=" + formId + "&userId=" + $('#userId').val() + '&PARENT_ID=' + -1 + '&formTab=' + tab;	
	
	if(openMode == "self")
	{
		if(window.self !== window.top) {    
			window.top.location.href = page;  
			return;
		}
		fgReloadForm(page);//window.location.href = page;
	}
	else //new tab
	{
		openNewTab(page);
	}
}

function smartSelectStateMng(domID,isParentChkbChanged,bln,columnNum)
{
	//console.log("smartSelectStateMng ", domID);	
	if(isParentChkbChanged) //if parent/header checkbox clicked 
	{
		var obj = null;
		if(columnNum == undefined || columnNum == 'label')
		{
			var formCode = $('#formCode').val();
			//obj = $(this);
			if ((columnNum == 'label' && domID == 'samples') && (formCode == 'InvItemBatch' || formCode == 'Request')) {
			    $("#"+domID+" > tbody > tr input[type='checkbox'][class='dataTableApiSelectInfoLabel']").each(function(i)
					    {
			    			obj = $(this);
					        if(obj.attr('disabled')==undefined||obj.attr('disabled')=='false'){
					        	obj.prop('checked', bln);
					        }
					    });
			} else {
			    $("#"+domID+" > tbody > tr input[type='checkbox'][class='dataTableApiSelectInfo']").each(function(i)
			    {
			        obj = $(this);
			        if(obj.attr('disabled')==undefined||obj.attr('disabled')=='false'){
			        	obj.prop('checked', bln);
			        }
			    });
			}
		} 
		else 
		{
			var role = $('#' + domID + '_role').val();
			var disabledListArray="";
			if(role.indexOf('Multiple')>-1){
	        	disabledListArray= $('#'+ domID + '_disabledList').val().split(',');
			}
			var $loopThrough;
		    if($('#formCode').val() == 'ExpAnalysisReport')
		    {
		    	var table = $('#'+domID).DataTable();			    	
		    	var column = table.column(0,{search:'applied'}).nodes();
		    	$loopThrough = $(column).find("input[type='checkbox']");
		    }
		    else
		    {
		    	$loopThrough = $("#"+domID+" > tbody > tr>td:nth-child("+columnNum+") input[type='checkbox']");
		    }
		    $loopThrough.each(function(i)
    	    {
    	        obj = $(this);
                if(disabledListArray.indexOf(obj.val()) == -1){
                	obj.prop('checked', bln);
                }
    	    });
		    
		    if(role == 'MultipleAjax')
		    {
		    	if($('#formCode').val() == 'ExpAnalysisReport' || $('#formCode').val() == 'ExperimentReport')
			    {
			    	var visibleRow = $('[id="' + domID + '"] tbody > tr')[0];
		    		var table = $('#'+domID).DataTable();	
			    	var tableRowsLength = table.rows().eq(0).length;
				    table.rows().eq(0).each( function ( index ) 
					{
				    	var row = table.row( index );
						var $rowNode = $(row.node());
						var isLastIteration = tableRowsLength - 1 == index;
						if(isLastIteration)
						{				    		
							var isChecked = $(visibleRow).find("td:nth-child("+columnNum+") input[type='checkbox']").is(':checked');
				    		$(visibleRow).find("td:nth-child("+columnNum+") input[type='checkbox']").prop('checked',!isChecked);//uncheck/check the row and recheck/uncheck it by clicking on it. the purpose is to recall the onChangeAjax operation.
				    		if (isChecked)
			    	    	{
				    			$(visibleRow).removeClass('selected');
			    	        } 
			    	    	else 
			    	    	{
			    	    		$(visibleRow).addClass('selected');
			    	        }
				    	}
						//in MultipleAjax role-> selecting a row affects on checked value	
						if ($rowNode.find("input[type='checkbox']").is(':checked'))
		    	    	{
		    	    		$rowNode.addClass('selected');
		    	        } 
		    	    	else 
		    	    	{
		    	    		$rowNode.removeClass('selected');
		    	        }
					});
				    $(visibleRow).click();
			    }
		    	else
		    	{
				    $('[id="' + domID + '"] tbody > tr').each(function(index)
		    	    {
				    	var isLastIteration = $('[id="' + domID + '"] tbody > tr').length - 1 == index;
				    	if(isLastIteration){
				    		var isChecked = $(this).find("td:nth-child("+columnNum+") input[type='checkbox']").is(':checked');
				    		$(this).find("td:nth-child("+columnNum+") input[type='checkbox']").prop('checked',!isChecked);//uncheck/check the row and recheck/uncheck it by clicking on it. the purpose is to recall the onChangeAjax operation.
				    		$(this).click();
				    		return;
				    	}
		    	    	//in MultipleAjax role-> selecting a row affects on checked value	
		    	    	if ($(this).find("td:nth-child("+columnNum+") input[type='checkbox']").is(':checked'))
		    	    	{
		    	            $(this).addClass('selected');
		    	        } 
		    	    	else 
		    	    	{
		    	    		 $(this).removeClass('selected');
		    	        }
		    	    });
		    	}
		    }
		}
	}
	else if(!isParentChkbChanged)//if child/body checkbox clicked, also on table page change, sort, search
	{
	    var unchecked_counter = 0;
	    if($('#formCode').val() == 'ExpAnalysisReport')
	    {
	    	var table = $('#'+domID).DataTable();	
	    	
//		    console.time("-- loopThrough column unchecked in domID - "+domID);
		    var column = table.column(0,{search:'applied'}).nodes();
		    unchecked_counter = $(column).find("input[type='checkbox']:not(:checked)").length;
//		    console.timeEnd("-- loopThrough column unchecked in domID - "+domID);
	    }
	    else
	    {
	    	unchecked_counter = $("#"+domID+" > tbody > tr input[type='checkbox']:not(:checked)").length;
	    }
	    
	    if(unchecked_counter == 0)
	    {
	    	$('#chbSelectAllNone_'+domID).prop('checked', true);
	    }
	    else
	    {
	    	$('#chbSelectAllNone_'+domID).prop('checked', false);
	    }
	}
	
	/* ab 18042019: code relocated into smartSelectStateMngBL
	 * var counter = 0;
    $("#"+domID+" > tbody > tr input[type='checkbox']").each(function(i)
    {
        if($(this).prop('checked'))
        {
            ++counter;
        }
    });*/
    smartSelectStateMngBL(domID);
}

/**
 * clean data ,destroy table and unbind event from datatable
 * @param domId
 * @returns
 */
function cleanDataTable(domId) {
    
	if ($('[id="' + domId + '"] thead').length) 
    {
        $('[id="' + domId + '"]').dataTable().fnDestroy();
        $('[id="' + domId + '"] thead').html('');
        $('[id="' + domId + '"] tbody').html('');
        $('[id="' + domId + '"] tfoot').remove();
        $('[id="' + domId + '"]').off();
    }
}

/**
 * 
 * @param len
 * @returns html tfoot by cols length
 */
function buildTfootByLength(len) {
    var th = "",
        i;
    for (i = 0; i < len; i++) {
        th += "<th></th>";
    }
    return "<tfoot><tr class=\"dataTable-header-search-row\">" + th + "</tr></tfoot>";
}

/**
 * init Element DataTable Api
 * @param domId
 * @param dataTableOptions
 * @param lastStruct
 * @param lastCriteria
 * @param lastDisplay
 * @param lastLinktoLastSelected
 * @param showDiv
 * @param lastFormId
 * @param lastPageLength
 * @returns
 */
function initElementDataTableApi(domId, dataTableOptions, lastStruct, lastCriteria, lastDisplay, lastLinktoLastSelected, showDiv, lastFormId, lastPageLength) {
	
	if (lastStruct != "")
        $('#' + domId + '_structCatalogItem').val(lastStruct);
    if (lastCriteria != "")
        $('#' + domId + '_criteriaCatalogItem').val(lastCriteria);
    if (lastDisplay != "")
        $('#' + domId + '_displayCatalogItem').val(lastDisplay);
    if (lastLinktoLastSelected == "1")
        $('#' + domId + '_LinkToLastSelection').prop('checked', true);
    else
        $('#' + domId + '_LinkToLastSelection').prop('checked', false);

    if (lastPageLength != "")
        $('#' + domId + '_lastPageLength').val(lastPageLength);

    $('#' + domId + '_formId').val(lastFormId);
    $('#' + domId + '_showDiv').val(showDiv);
    
    changeElementDataTableApiCaption(domId); // Change caption on special cases.
    
    if (typeof dataTableOptions !== 'undefined' && dataTableOptions != "") {
	    //hide extras when hideExtras option is exists or when rols is Attachment
	    var role,hideExtras = "";
	    var options = JSON.parse(dataTableOptions);
	    var role = $('#' + domId + '_role').val();
	    
	    if (typeof options.role !== 'undefined') {
	        role = options.role;
	    }
	    if (typeof options.hideExtras !== 'undefined') {
	        hideExtras = options.hideExtras;
	    }
	    if ((hideExtras) || (role == 'Attachment')) {
	        hideDataTableApiImpExtras(domId);
	    }
    }
    
    //attach remove event
    if ($('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').length) {
        $('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').click(function () {
            $('[name="' + domId + '_Iframe"]').attr('src', 'about:blank');
            $('[name="' + domId + '_Iframe"]').css('display', 'none');
            
            var confirmOnRemoveAction = $('#' + domId + '_confirmOnRemoveAction').val();
            if(confirmOnRemoveAction != null && confirmOnRemoveAction != '') {
            	checkDTConfirmDialog(this);
            	return;
            } else {
            	openConfirmDialog({
                    onConfirm: deleteRowElementDataTableApiImp,
                    onConfirmParams: this
                });
            }
        });
    }
   
    /******************* DataTable extension to sort in columns ***********/
    dataTableCustomSort();

    //forward to onElementDataTableApiChange function
    //onLevelSelectedChange(domId, dataTableOptions, true, "init");
    onElementDataTableApiChange(domId, dataTableOptions);
}

function dataTableCustomSort() {
	$.fn.dataTable.ext.order['dom-custom-sort'] = function  ( settings, col )
    {
        //console.log("------settings", settings);
        //console.log("dom-custom-sort", col);
        
        var _domObjType = "";
    	return this.api().column( col, {order:'index'} ).nodes().map( function ( td, i ) 
        {
    		//console.log(td);
    		if(_domObjType == "")
    		{
        		var $input = $(td).find('input');
        		var $textarea = $(td).find('textarea');
        		var $select = $(td).find('select');
        		
        	    if($select.length > 0)
        		{
        			//console.log($select);
        			_domObjType = 'select';
        		}
        		else if($textarea.length > 0)
        		{
        			//console.log($textarea);
        			_domObjType = 'textarea';
        		}
        		else if($input.length > 0)
        		{
        			//console.log($input);
        			_domObjType = $input.attr('sorttype');
        		}
        		else
        		{
        			var $span = $(td).find('span');
        			if($span.length > 0){
        				if($span.find('i').length >0){
        					_domObjType = $span.find('i').attr('sorttype');
        				} else
            			_domObjType = $span.attr('sorttype');
            		}
            		else
            		{
            			_domObjType = 'none';
            		}
        		}
        	    //console.log("dom-custom-sort",_domObjType);
    		}
    		
    		var _val = "";
    		if(_domObjType == 'string')
    		{
    			_val = $('input', td).val();
    		}
    		else if(_domObjType == 'date') /*note:column is sorted as 'date' only if column type defined as 'date' (see 'SMARTDATE')*/
    		{
    			_val = $('input', td).val();
    		}
    		else if(_domObjType == 'select')
    		{
    			_val = $('select >option:selected', td).text();
    		}
    		else if(_domObjType == 'textarea')
    		{
    			_val = $('textarea', td).text();
    		}
    		else if(_domObjType == 'datetime') // coming from span
    		{
    			_val = parseInt($('span', td).attr('realvalue'));
    		}
    		else if(_domObjType == 'star') // coming from span
    		{
    			_val = $(td).find('span').find('i').attr('value');
    		}
    		else
    		{
    			_val = $(td).html();
    		}
    			
    		//console.log("dom-custom-sort",_domObjType+": " + _val);
        	return _val;
        } );
    }
}
 
/**
 * select Last Selected saved Row
 * @param domId
 * @returns
 */
function selectLastSelectedRow(domId, currDelayValue) {
    
	var role = $('#' + domId + '_role').val();	
	if ($('#' + domId + '_formId').val() == "" && (role!='MultipleAjax' || $('#' + domId + '_value').val() == "")) {		
        $('#' + domId + '_dataTableStructButtons button:not([dataTableApiTypeNew], .dataTableApiLabel)').addClass('disabledclass');
        if(formCode.value == "Request" && domId =="samples"){
        	$('#' + domId + '_dataTableStructButtons button.dataTableApiEdit').removeClass('disabledclass');  
        }
        return;
    }
	var timer = 0;
    var delay = currDelayValue;
    //console.log(domId, "currDelayValue: " + currDelayValue)
    timer = setTimeout(function() 
    {
    	var lastFormId, table;
    	var _selectedRowIndex = 0;
    	
    	//in MultipleAjax role-> the checked rows affects on selected rows
	    lastFormId = role != 'MultipleAjax'? $('#' + domId + '_formId').val() : $('#' + domId + '_value').val();
	    table = $('[id="' + domId + '"]').DataTable();
	    $('#' + domId + '_formId').val("");

	    var indexes = table.rows().eq( 0 ).filter( function (rowIdx) {
	    	if((',' + lastFormId + ',').indexOf(',' + table.cell( rowIdx, 0 ).data() + ',') > -1)// table.cell( rowIdx, 0 ).data() === lastFormId) 
	        {
	        	return true;
	        } else {
	        	return false;
	        }
	    });
	    table.rows( { filter: 'applied' } ).data().each(function(value, index) 
	    {
	        //console.log(value[0], lastFormId);
	    	if((',' + lastFormId + ',').indexOf(',' + value[0] + ',') > -1 )// value[0] == lastFormId
	    	{
	    		_selectedRowIndex = index;
	    	}
	    });
	    
	    //console.log(table.page.info());
	    var _pageSettings = table.page.info();
	    var _rowsPerPage = _pageSettings.length;
	    var _pageInd = Math.floor(_selectedRowIndex / _rowsPerPage); 
	    
	    //console.log(domId, "lastFormId: " + lastFormId+  " |_selectedRowIndex: "+ _selectedRowIndex + " |_rowsPerPage: " + _rowsPerPage + " |_pageInd: " + _pageInd);
	    
	    if(indexes != null) {	    	 
	    	 table.page(_pageInd).draw('page');
	    	 table.rows( indexes )
	    	    	.nodes()
	    	    	.to$()
	    	    	.addClass( 'selected' );
	    } else {
	    	table.page(0).draw('page');
	    }
	    
	    clearTimeout(timer);
	    
    }, delay);
}

function openInsetRowsDialog(domId){
	var selectedTable;
    var custid;
    var page;
    var formId = -1;
    var parentId = $('#formId').val();
    var title = $('[id="' + domId + '_structCatalogItem"]').val();
    var formCode = encodeURIComponent($('[id="' + domId + '_structCatalogItem"]').val());
    var stateKey = $('#' + domId + '_selectDiv').attr('stateKey');
    var onActionButtons = $('[id="' + domId + '_onActionButtons"]').val();
    var popupSize = $('[id="' + domId + '_popupSize"]').val();
    // parentStruct = parentTable != '' && $('#' + parentTable).is('table') ? $('#' + parentTable + '_structCatalogItem').val():$('#formCode').val();
        
    //isValidationExist(formCode,parentStruct,parentId,input,isCloneAttrFlag,formId,smartSelectList.toString());
   
	formCode = 'MultiAddRows';
    dialogHeight = 200;
    dialogWidth = 450;
    title = "Insert Rows";
    formId = -1;   
    selectedTable = $('#' + domId).DataTable();
    var page = "./init.request?stateKey=" + stateKey + "&formCode=" + formCode + "&formId=" + formId + "&userId=" + $('#userId').val() + '&tableType=' + $('[id="' + domId + '_tableType"]').val() + '&urlCallParam=' + replaceDTUrlCallParamVal($('[id="' + domId + '_urlCallParam"]').val(), formId) + '&PARENT_ID=' + parentId + '&tableId=' + domId;

    // open iframe inside dialog
    var $dialog = $('<div id="prevDialog" style="overflow-y: hidden;""></div>')
        .html('<iframe id="formIframeId" style="border: 0px;width:100%;height:100%" src="' + page + '"></iframe>')
        .dialog({
            autoOpen: false,
            modal: true,
            height: dialogHeight,
            width: dialogWidth,
            close: function () {
                $('#prevDialog iframe').attr('src', 'about:blank');
            	var isRowsAdded = $(this).data('isRowsAdded');
            	if(isRowsAdded!=undefined && isRowsAdded){
					var returnParam = $(this).data('returnParam');
					var dataStr = $(this).data('rowData');
					$('#prevDialog').remove();
					if(dataStr != null)//check if it's necessary to reload the table(in the first row) or just add the additional rows to the table by drawing
					{
						var dataListOfRows = $.parseJSON(dataStr);
						 dataListOfRows.forEach(function(rowData){						
							 var dataArr = rowData;
							 console.log("dataTableAddRow() new row data ", dataArr);
							if(dataArr.length > 0)
							{
								if(domId !="action"){
									globalEditableTableEmptyRowHolder[domId] = dataArr;
								}
								toReturn = dataArr[0];
								dataTableAddRowAndGoToPage(domId, dataArr);							
							}
						 });
					} else {
						onElementDataTableApiChange(domId); 
						bl_elementDatatableEditableCustomFuncHandler(domId, "onTableRowAdded", null, [returnParam]);
					}
            	} else{
            		$('#prevDialog').remove();
            	}
            },
            title:title
        });

    $dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
}

/**
 * toggle tables on main pages
 */
//var divCount = 0; // remove DivCount
//function initDivCount(domId) {
//    if ($('#' + domId + '_showDiv').val() == "1") {
//        $('#' + domId + '_showDiv').val("");
//        divCount++;
//    }
//}

/**
 * on button click
 * @param input
 * @returns
 */
function ElementDataTableApiImpOnButtonClick(input) {
	var selectedTable;
    var custid;
    var page;
    var jqueryInput = $(input);
    var formId = -1;
    var parentId = $('#formId').val();
    var domId = $(input).parent().attr('tableid');
    var title = $('[id="' + domId + '_structCatalogItem"]').val();
    var formCode = encodeURIComponent($('[id="' + domId + '_structCatalogItem"]').val());
    var stateKey = $('#' + domId + '_selectDiv').attr('stateKey');
    var onActionButtons = $('[id="' + domId + '_onActionButtons"]').val();
    var popupSize = $('[id="' + domId + '_popupSize"]').val();
    var dialogWidth = $(window).width()*0.85;
    var dialogHeight = $(window).height()*0.85;
    var availableDataStructures, parentTable;
    var str;
    var isCloneAttrFlag = "";
    var urlPermissionDisabled = "";
    if (popupSize == "Medium") {
        dialogWidth = 650;
        dialogHeight = 500;
    }
    else if (popupSize != "") {
        dialogWidth = 700;
        dialogHeight = 270;
    }
    if (formCode == "OperationType"){
    	dialogWidth = 850;
    }
    if (formCode =="ManualResultsRef"){
    	var dialogWidth = $(window).width()-60;
        var dialogHeight = $(window).height()-60;
    }
    // If edit or view
    if ((jqueryInput.hasClass('dataTableApiView')) || (jqueryInput.hasClass('dataTableApiEdit'))) {
        if ($('[id="' + domId + '"] thead').length) {
            selectedTable = $('#' + domId).DataTable();
            custid = selectedTable.row('.selected').data();
            if (typeof custid !== 'undefined') {
                formId = custid[0];
                //////////////// start permission
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
                        
                        hideWaitMessage();
                        
//                        canRead = "1"; // !!! develop until we set permission
                        if(canRead != "1") {
                        	displayAlertDialog("View is not allowed");
                        	return;
                        } else {
                        	// make view code
                        	var page = "./init.request?stateKey=" + stateKey + "&formCode=" + formCode + "&formId=" + formId + "&userId=" + $('#userId').val() + '&tableType=' + $('[id="' + domId + '_tableType"]').val() + '&urlCallParam=' + replaceDTUrlCallParamVal($('[id="' + domId + '_urlCallParam"]').val(), formId) + '&PARENT_ID=' + parentId + isCloneAttrFlag;

                            if ((onActionButtons != 'PopUp') && (!jqueryInput.hasClass('dataTableApiSplit')) && (!jqueryInput.hasClass('dataTableApiMultiClone'))) {
                                showWaitMessage(getSpringMessage('pleaseWait'));
                                window.location = page;
                                return;
                            }
                            
                            // open iframe inside dialog
                            var generalDisabledFlagParam_ = $('#generalDisabledFlagParam').val();
                        	var buttonText_ = '';
                        	try {
                        		buttonText_ = $('#' + domId + "_dataTableStructButtons").find("button.dataTableApiEdit").text();
                        	} catch(e) {}
                        	var permissionDisabledVal_ = 0;
                        	if(generalDisabledFlagParam_ == 1 || buttonText_.toUpperCase() == getSpringMessage("View").toUpperCase()) {
                        		permissionDisabledVal_ = 1;
                        	}
                            urlPermissionDisabled = '&PERMISSION_DISABLED=' + permissionDisabledVal_;
                            var $dialog = $('<div id="prevDialog" style="overflow-y: hidden;""></div>')
                                .html('<iframe style="border: 0px;width:100%;height:100%" src="' + page + urlPermissionDisabled + '"></iframe>')
                                .dialog({
                                    autoOpen: false,
                                    modal: true,
                                    height: dialogHeight,
                                    width: dialogWidth,
                                    //  title: title,
                                    close: function () {
                                        $('#prevDialog iframe').attr('src', 'about:blank');
                                        $('#prevDialog').remove();
                                        onElementDataTableApiChange(domId);
                                    }
                                });

                            $dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
                        }
                    },
                    error: handleAjaxError
                });
                
                return;
                //////////////// end permission
            } else {
                return;
            }
        }
    }
    // If new
    else if (jqueryInput.hasClass('dataTableApiNew')) {
    	if(formCode == 'Choose'){
        	displayAlertDialog('Create new is not possible. please select a Level.');//fixed bug 8542
        	return;
        }
        parentTable = $(input).parents('div[parentelement]')
            .attr('parentelement');
        var smartSelectList = [];  
        if (parentTable != '') {
            if ($('#' + parentTable).is('table')) {
                selectedTable = $('[id="' + parentTable + '"]').DataTable();
                custid = selectedTable.row('.selected').data();
                $('#'+ parentTable + ' input[class="dataTableApiSelectInfo"]:checked').each(function (index) {
                	smartSelectList.push($(this).val());
                });
                
                if (typeof custid !== 'undefined'|| smartSelectList.length!=0) {
                    availableDataStructures = dataTableApiDataStructure(formCode, parentTable, true);
                    if (availableDataStructures != '') {
                        displayAlertDialog(getSpringMessage('pleaseSelect') + " " + availableDataStructures + " " + getSpringMessage('inTheTableAbove'));                       
                        return;
                    }
                    parentId = smartSelectList.length == 0 ?custid[0]:smartSelectList[0];
                } else {
                    str = dataTableApiDataStructure(formCode, parentTable, false);
                    if (str != '') {
                        displayAlertDialog(getSpringMessage('pleaseSelect') + " " + str + " " + getSpringMessage('inTheTableAbove'));
                        return;
                    }
                }
            } else {
                availableDataStructures = dataTableApiDataStructure(formCode, parentTable, true);
                if (availableDataStructures != '') {
                    displayAlertDialog(getSpringMessage('pleaseSelect') + " " + availableDataStructures + " " + getSpringMessage('inTheTableAbove'));
                    return;
                }
            }
        } else {
        	availableDataStructures = dataTableApiDataStructure(formCode, parentTable, false);
        	if (availableDataStructures != '') {
                displayAlertDialog(getSpringMessage('pleaseSelect') + " " + availableDataStructures + " " + getSpringMessage('inTheTableAbove'));
                return;
            }
        } 
        
         parentStruct = parentTable != '' && $('#' + parentTable).is('table') ? $('#' + parentTable + '_structCatalogItem').val():$('#formCode').val();
        
        isValidationExist(formCode,parentStruct,parentId,input,isCloneAttrFlag,formId,smartSelectList.toString());
        
        /*
        formCode = onNewButtonIntegration(formCode); // change formCode if needed on new button //  onNewButtonIntegration exists in generalBl		
    
        showWaitMessage("Please wait...");
        
        $.ajax({
            type: 'POST',
            data: '{"action" : "getNewAvailableFormList","' + 'data":[' + '{"code":"formCode","val":"' + formCode + '"},' + '{"code":"formId","val":"' + parentId + '"}' + '],' + '"errorMsg":""}',
            url: "./getNewAvailableFormListById.request",
            contentType: 'application/json',
            dataType: 'json',
            success: function (obj) {
            	var canNewByList = false;
                if (obj.errorMsg != null && obj.errorMsg != '') {
                    displayAlertDialog(obj.errorMsg);
                    return;
                } else if ((obj.data[0].val == "-1") || (obj.data[0].val == "")) {
                	//false
                } else {
                	
                    optionsArray = obj.data[0].val.split(',');
                    optionsArrayLength = optionsArray.length;
                    
                    for (i = 0; i < optionsArrayLength; i++) {
                    	if(!canNewByList && optionsArray[i] == formCode) {
                    		canNewByList = true;
                    	}
                    }
//                    optionsHtml += '<option value="' + optionsArray[i] + '">' + optionsArray[i].replace("InvItem", "") + '</option>';
                }
                
                hideWaitMessage();
                
//                canNewByList = true;  // !!! develop until we set permission
                if(!canNewByList) {
                	displayAlertDialog("Create new is not allowed");
                	return;
                } else {
                	
                	// make view code
                	var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=" + formCode + "&formId=" + formId + "&userId=" + $('#userId').val() + '&tableType=' + $('[id="' + domId + '_tableType"]').val() + '&urlCallParam=' + replaceDTUrlCallParamVal($('[id="' + domId + '_urlCallParam"]').val(), formId) + '&PARENT_ID=' + parentId + isCloneAttrFlag;

                    if ((onActionButtons != 'PopUp') && (!jqueryInput.hasClass('dataTableApiSplit')) && (!jqueryInput.hasClass('dataTableApiMultiClone'))) {
                        showWaitMessage(getSpringMessage('pleaseWait'));
                        window.location = page;
                        return;
                    }
                    // open iframe inside dialog
                    var $dialog = $('<div id="prevDialog" style="overflow-y: hidden;""></div>')
                        .html('<iframe style="border: 0px;width:100%;height:100%" src="' + page + '"></iframe>')
                        .dialog({
                            autoOpen: false,
                            modal: true,
                            height: dialogHeight,
                            width: dialogWidth,
                            //  title: title,
                            close: function () {
                                $('#prevDialog iframe').attr('src', 'about:blank');
                                $('#prevDialog').remove();
                                onElementDataTableApiChange(domId);
                            }
                        });

                    $dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
                }
            },
            error: handleAjaxError
        });*/
        
        return;
    }
    //edit shared
    else if (jqueryInput.hasClass('dataTableApiEditShared')) {
    	var generalDisabledFlagParam_ = $('#generalDisabledFlagParam').val();
    	var buttonText_ = '';
    	try {
    		buttonText_ = $('#' + domId + "_dataTableStructButtons").find("button.dataTableApiEditShared").text();
    	} catch(e) {}
    	var permissionDisabledVal_ = 0;
    	if(generalDisabledFlagParam_ == 1 || buttonText_.toUpperCase() == getSpringMessage("View").toUpperCase()) {
    		permissionDisabledVal_ = 1;
    	}
    	urlPermissionDisabled = '&PERMISSION_DISABLED=' + permissionDisabledVal_;
        if ($('[id="' + domId + '"] thead').length) {
            selectedTable = $('#' + domId).DataTable();
            custid = selectedTable.row(':eq(0)').data();
            if (typeof custid !== 'undefined') {
                formId = custid[0];
            } else if($('#' + domId + '_sharedFormId').val()!== undefined && $('#' + domId + '_sharedFormId').val()!="-1"){
            	formId = $('#' + domId + '_sharedFormId').val();
            }
        }
    }
    //if clone
    else if (jqueryInput.hasClass('dataTableApiClone')) {
        if ($('[id="' + domId + '"] thead').length) {
            selectedTable = $('#' + domId).DataTable();
            custid = selectedTable.row('.selected').data();
            if (typeof custid !== 'undefined') {
                formId = custid[0];
                $.ajax({
                    type: 'POST',
                    data: '{"action" : "doClone","' + 'data":[' + '{"code":"formId","val":"' + formId + '"}' + '],' + '"errorMsg":""}',
                    url: "./doClone.request",
                    contentType: 'application/json',
                    dataType: 'json',
                    success: function (obj) {
                        if (obj.errorMsg != null && obj.errorMsg != '') {
                            displayAlertDialog(obj.errorMsg);
                        } else if (obj.data[0].val == "-1") {
                            displayAlertDialog(getSpringMessage('cloneFailed'));
                        } else if (obj.data[0].val.toString().indexOf(',') != '-1' && obj.data[0].val.toString().substring(0, 2) == '-3') {
	           	             doSaveMessage = obj.data[0].val.split(',')[1];
	        	           	 var defaultMessage = doSaveMessage.split("_").join(" ").toLowerCase();
	        	           	 displayAlertDialog(getSpringMessage(doSaveMessage,defaultMessage.charAt(0).toUpperCase() + defaultMessage.slice(1) + " " + getSpringMessage('invalidInSystem')));
	        	           	 hideWaitMessage();
	                    } else {
//                            page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=" + formCode + "&formId=" + obj.data[0].val + "&userId=" + $('#userId').val() + "&isClone=1";
                            isCloneAttrFlag = "&isClone=1";
                            formId = obj.data[0].val;
//                            window.location = page;
                            
                            var page = "./init.request?stateKey=" + stateKey + "&formCode=" + formCode + "&formId=" + formId + "&userId=" + $('#userId').val() + '&tableType=' + $('[id="' + domId + '_tableType"]').val() + '&urlCallParam=' + replaceDTUrlCallParamVal($('[id="' + domId + '_urlCallParam"]').val(), formId) + '&PARENT_ID=' + parentId + isCloneAttrFlag;

                            if ((onActionButtons != 'PopUp') && (!jqueryInput.hasClass('dataTableApiSplit')) && (!jqueryInput.hasClass('dataTableApiMultiClone'))) {
                                showWaitMessage(getSpringMessage('pleaseWait'));
                                window.location = page;
                                return;
                            }
                            // open iframe inside dialog
                            var $dialog = $('<div id="prevDialog" style="overflow-y: hidden;""></div>')
                                .html('<iframe style="border: 0px;width:100%;height:100%" src="' + page + '"></iframe>')
                                .dialog({
                                    autoOpen: false,
                                    modal: true,
                                    height: dialogHeight,
                                    width: dialogWidth,
                                    //  title: title,
                                    close: function () {
                                        $('#prevDialog iframe').attr('src', 'about:blank');
                                        $('#prevDialog').remove();
                                        onElementDataTableApiChange(domId);
                                    }
                                });

                            $dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
                            return;
                        }
                    },
                    error: handleAjaxError
                });
                return;
            } else
                return;
        }
    } 
   //MultiClone
    else if ((jqueryInput.hasClass('dataTableApiSplit')) || (jqueryInput.hasClass('dataTableApiMultiClone'))) {
        if ($('[id="' + domId + '"] thead').length) {
            if (jqueryInput.hasClass('dataTableApiSplit')) {
                formCode = 'Split';
                dialogHeight = 380;
                dialogWidth = 550;
            } else {
                formCode = 'MultiClone';
                dialogHeight = 200;
                dialogWidth = 450;
            }
            formId = -1;   
            selectedTable = $('#' + domId).DataTable();
            custid = selectedTable.row('.selected').data();
            if (typeof custid !== 'undefined') {
                parentId = custid[0];
                parentId += '&struct=' + title
            } else
                return;
            if (jqueryInput.hasClass('dataTableApiMultiClone')) {
                title = "Clone";
            }
        }
    }

    var page = "./init.request?stateKey=" + stateKey + "&formCode=" + formCode + "&formId=" + formId + "&userId=" + $('#userId').val() + '&tableType=' + $('[id="' + domId + '_tableType"]').val() + '&urlCallParam=' + replaceDTUrlCallParamVal($('[id="' + domId + '_urlCallParam"]').val(), formId) + '&PARENT_ID=' + parentId + isCloneAttrFlag;
    if(formCode == "Document"){
    	if($('[id="' + domId + '_showDragAndDrop"]').val()=="true"){
    		page = page +'&defaultLinkAttachment=Link';
    	}
    	else{
    		page = page +'&defaultLinkAttachment=Attachment';
    	}
    }
    if ((onActionButtons != 'PopUp') && (!jqueryInput.hasClass('dataTableApiSplit')) && (!jqueryInput.hasClass('dataTableApiMultiClone'))) {
        showWaitMessage(getSpringMessage('pleaseWait'));
        window.location = page;
        return;
    }
    // open iframe inside dialog
    var $dialog = $('<div id="prevDialog" style="overflow-y: hidden;""></div>')
        .html('<iframe id="formIframeId" style="border: 0px;width:100%;height:100%" src="' + page + urlPermissionDisabled + '"></iframe>')
        .dialog({
            autoOpen: false,
            modal: true,
            height: dialogHeight,
            width: dialogWidth,
            //  title: title,
            close: function () {
                $('#prevDialog iframe').attr('src', 'about:blank');
                $('#prevDialog').remove();
                onElementDataTableApiChange(domId);
            }
        });

    $dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
}

/**
 * hide DataTableApi Extras
 * @param domId
 * @returns
 */
function hideDataTableApiImpExtras(domId) {
    $('#' + domId + '_selectDiv').css('display', 'none');
//    var btnElm = $('#' + domId + '_dataTableStructButtons');
//    btnElm.css('margin-bottom','10px');
////    $('#' + domId + '_wrapper div.dt-buttons:first').css('display', 'none');
////    $('#' + domId + '_wrapper .dropdown:first').css('display', 'none');
//    $('#' + domId + ' thead img').css('display', 'none');
//    fixLink(domId);
}

/**
 * ab 16092020 TODO: remove not in use
 * fix css of links (inside the rows)
 * @returns
 */
function fixLink(domId) {
    if ($('#' + domId + ' a[onclick*="openAttachment"]').length) 
    {
    	$('#' + domId + ' a[onclick*="openAttachment"]').removeClass().css('color', '#519bcd').css('cursor', 'pointer');
    }
    if ($('#' + domId + ' a[onclick*="smartFile"]').length) 
    {
    	$('#' + domId + ' a[onclick*="smartFile"]').removeClass().css('color', '#519bcd').css('cursor', 'pointer');
    }
    if ($('#' + domId + ' a[onclick*="smartLink"]').length) 
    {
    	$('#' + domId + ' a[onclick*="smartLink"]').removeClass().css('color', '#519bcd').css('cursor', 'pointer');
    }
}
/**
 * open Attachment
 * @param TITLE
 * @param FILE_ID
 * @param type
 * @param domId
 * @returns
 */
function openAttachmentElementDataTableApiImp(TITLE, FILE_ID, type, domId) 
{    
	$('[name="' + domId + '_ContentDisposition"]').val('attachment');
    var isPreview = $('[id="' + domId + '_isPreview"]').val();
    if (isPreview == "1") 
    {
    	if (type != "Link") {
            $('[name="' + domId + '_ContentDisposition"]').val('inline');
            $('[name="' + domId + '_FILE_ID"]').val(FILE_ID);
            $('[id="' + domId + '_isPreview"]').val('');
           
            //open preview doc as popup if isAttachmentPopupPreview=true
            var popup = $('[id="' + domId + '_IframePopup"]');
            if(popup.length>0){
            	if(popup.attr("data-isAttachmentPopupPreview")=="true"){
            		$('[id="' + domId + '_IframePopup"]').dialog({                    
                        modal: true,
                        height: 600,
                        width:600,
                        resizable:true                        
                    });
            		
            		$('[id="' + domId + '_IframePopup"]> [name="' + domId + '_Iframe"]').css('display', 'inline-block');
            	}
            	
            }else{
            	$('[name="' + domId + '_Iframe"]').css('display', 'inline-block');
            }
            	
            
             
            $('#' + domId + '_AttachmentForm').submit();
 
        } else {
            $('[id="' + domId + '_isPreview"]').val('');
            $('[name="' + domId + '_Iframe"]').attr('src', 'about:blank');
            $('[name="' + domId + '_Iframe"]').css('display', 'none');
            $('#' + domId + '_IframeClose').css('display', 'none');
        }
    } else {
        if (type == "Link") {
            $('[name="' + domId + '_Iframe"]').attr('src', 'about:blank');
            $('[name="' + domId + '_Iframe"]').css('display', 'none');
            window.open(TITLE, '_blank');
        } else {
            $('[name="' + domId + '_FILE_ID"]').val(FILE_ID);
            $('#' + domId + '_AttachmentForm').submit();
        }
    }
}

//function closeAttachmentPreview(domId)
//{
//	 $('[name="' + domId + '_Iframe"]').css('display', 'none');
//	 $('#' + domId + '_IframeClose').css('display', 'none');
//	
//}

function checkDTConfirmDialog(input) {
	var divId = $(input).parent().attr('id');
    var domId = divId.substring(0, divId.indexOf('_'));
    var struct = $('[id="' + domId + '_structCatalogItem"]').val();
    var confirmOnRemoveAction = $('#' + domId + '_confirmOnRemoveAction').val();
    var selectedTable = $('#' + domId).DataTable();
    var custid = selectedTable.row('.selected').data();
    var formId = "";
    if (typeof custid !== 'undefined') {
        formId = custid[0];
    }    
    	
    $.ajax({
        type: 'POST',
        data: '{"action" : "confirmDeleteRowElementDataTableApiImp","' + 'data":[{"code":"struct","val":"' + struct + '"},{"code":"formId","val":"' + formId + '"},{"code":"confirmOnRemoveAction","val":"' + confirmOnRemoveAction + '"}],' + '"errorMsg":""}',
        url: "./confirmDeleteRowElementDataTableApiImp.request",
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) {
            if (obj.errorMsg != null && obj.errorMsg != '') {
                displayAlertDialog(obj.errorMsg);
            } else if (obj.data[0].val == null || obj.data[0].val == "") {
            	openConfirmDialog({
                    onConfirm: deleteRowElementDataTableApiImp,
                    onConfirmParams: input
                });        
            } else {
            	openConfirmDialog({
            		message: obj.data[0].val,
                    onConfirm: deleteRowElementDataTableApiImp,
                    onConfirmParams: input
                });
          
//			    //forward to onElementDataTableApiChange function
//			    onLevelSelectedChange(domId, dataTableOptions, true, "init");
//			    onElementDataTableApiChange(domId, dataTableOptions);
            }
        },
        error: handleAjaxError
    });
}

/**
 * delete Rows
 * @param input
 * @returns
 */
function deleteRowElementDataTableApiImp(input) {
    var divId = $(input).parent().attr('id');
    var domId = divId.substring(0, divId.indexOf('_'));
    var struct = $('[id="' + domId + '_structCatalogItem"]').val();
    var selectedTable = $('#' + domId).DataTable();
    var custid = selectedTable.row('.selected').data();
    var role = $('#' + domId + '_role').val();
    var formId = "";
    var rowId = "";
    if (typeof custid !== 'undefined') {
        formId = custid[0];
    }
    
    if(role=="Shared" && $("table[id='"+domId+"']").attr("disableeditable")=="0"){
    	if(domId =="instruments" || domId =="columns" || domId =="samples" ){
    		var obj = JSON.parse(custid[4]);
        	rowId = obj.formId;
    	}
    }
    
    if(domId =="instruments"){
    	removeRowByIdFromComboListSelfTestResultWebixElement('instrument',rowId);
    }
    $.ajax({
        type: 'POST',
        data: '{"action" : "deleteRowElementDataTableApiImp","' + 'data":[{"code":"struct","val":"' + struct + '"},{"code":"formId","val":"' + formId + '"},{"code":"domId","val":"' + domId + '"},{"code":"rowId","val":"' + rowId + '"}],' + '"errorMsg":""}', 
        url: "./deleteRowElementDataTableApiImp.request",
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) {
            if (obj.errorMsg != null && obj.errorMsg != '') {
                displayAlertDialog(obj.errorMsg);
            } else if (obj.data[0].val == "-1") {
                displayAlertDialog(getSpringMessage('deleteFailed'));
            } else if (obj.data[0].val == "0") {
                //alert("delete 0 records");              
            } else {
//            	selectedTable.row('.selected').remove().draw(false); -> yp 06052019 - avoid delete row in all the maintenance tables
            	try {
            		if($('#formCode').length > 0 && $('#formCode').val() == 'Maintenance') {
                		$('#eMaintenanceTableApi_Parent').fadeToggle('fast', function() { 					 
    						 onElementDataTableApiChange('eMaintenanceTableApi'); 
    					       $('#eMaintenanceTableApi_Parent').fadeToggle('slow'); 
    				     }); 
        			} else {
        				selectedTable.row('.selected').remove().draw(false);
        			}
            	} catch(e) {
            		selectedTable.row('.selected').remove().draw(false);
            	}
            	
            	
                $('#' + domId + '_dataTableStructButtons button:not([dataTableApiTypeNew],.dataTableApiLabel)').addClass('disabledclass');
                if ($('[id="' + domId + '__oneRowOnly"]').val() != ''
                		||($('[id="' + domId + '__maximumRows"]').val() != '') && !isNaN($('[id="' + domId + '__maximumRows"]').val())) {//one row was removed->it's able to add at least one more row
                    $('#' + domId + '_dataTableStructButtons button[dataTableApiTypeNew], .dataTableApiLabel').removeClass('disabledclass');
                }
                bl_elementDatatableEditableCustomFuncHandler(domId, "onTableRowRemoved");
            }
        },
        error: handleAjaxError
    });
}

function getColumnUniqueName($header) 
{
	var _uTitle = $header.attr('uniqueTitle');	
	var _title;
	if(_uTitle !== undefined && _uTitle != "")
	{
		_title = _uTitle;
	}
	else
	{		
		_title = $header.text();
	}
	return _title.trim();
}

/**
 * init Remove option for columns
 * @param tableID
 * @returns
 */
function initRemoveColumnDatatable(tableID) {
    $('#' + tableID + '_wrapper').find('div.dropdown-content div.dt-buttons')
        .append('<a class="" tabindex="0" aria-controls="firstTable" onclick="resetColumns(\'' + tableID + '\')"><span>Columns</span></a>');

    var table = $('#' + tableID).DataTable();
    table.columns().iterator('column', function (ctx, idx) 
    {
    	//if ($(table.column(idx).header()).html() != "")
    	//append remove icon only:
    	//   - if column is sortable, like it was before style change
    	if (!$(table.column(idx).header()).hasClass('sorting_disabled')) 
        {
            var headerObj = table.column(idx).header();
			var _title = getColumnUniqueName($(headerObj));
			
        	$(headerObj).find('div')
        		.append('<span><i class="close-icon" title="Remove column" onclick="removeColumnDatatable($(this).closest(\'table\').attr(\'id\'),\''+_title+'\');event.stopImmediatePropagation();"></i></span>');
        }
    });
}

function resetColumns(domId) {

	var removedArray = $('[id="' + domId + '_colsArray"]').val().split('@');
	var colArray = [];
	var allColumnIndObj = {};
	var hiddenColsIndexArr = [];
	var _table = $('#' + domId).DataTable();
	var _$header, _uTitle, _column, _headerText;
	var options = $('#' + domId + '_dataTableOptions').val();
	var dataTableOptions = JSON.parse(options);
	var isTableReorderable = (_table.context[0]._colReorder.s.enable)?true:false;
	var followingHiddenColsInt = 1;
	if ((typeof dataTableOptions.role !== 'undefined')
			&& (dataTableOptions.role == 'Attachment')) {
		followingHiddenColsInt = 6;
	}
	if (typeof dataTableOptions.followingHiddenCols !== 'undefined') {
		var followingHiddenCols = dataTableOptions.followingHiddenCols;
		followingHiddenColsInt = (isNaN(followingHiddenCols)) ? 0: Number(followingHiddenCols);
	}
	for (var i = 0; i < _table.columns().header().length; i++) {
		_column = _table.column(i);
		_$header = $(_column.header());
		_uTitle = _$header.attr('uniqueTitle').trim();
		allColumnIndObj[_uTitle] = i;
		
		if(i >= followingHiddenColsInt) 
		{
			_headerText = _$header.text().trim();
			var _isColRemoveEnabled = _$header.hasClass('sorting_disabled')?false:true; // need to separate logic of sorting and removing on table creation
			var _isColReorderEnabled = (isTableReorderable && !_$header.hasClass(columnReOrderDisabledClass))?true:false; //TRUE if table is reorderable by definition and if column does NOT have 'columnReOrderDisabledClass' class
	
			if (_isColRemoveEnabled || _isColReorderEnabled) {
				var _title = _uTitle;
				if(_headerText != _uTitle ){
					_title = _uTitle+";"+_headerText;
				}
				colArray.push({"title":_title, "isColRemoveEnabled":_isColRemoveEnabled, "isColReorderEnabled":_isColReorderEnabled, "colIndex":i});
			}
			else
			{
				hiddenColsIndexArr[i] = i;
			}
			}
		else
		{
			hiddenColsIndexArr[i] = i;
		}
	}
	
	try {
		var formCode = 'ColumnsDefinition';
		var dialogHeight = 620;
		var dialogWidth = 350;
		var page = "./init.request?stateKey=" + $('#stateKey').val()
				+ "&formCode=" + formCode + "&formId=-1" + "&userId="
				+ $('#userId').val() + '&tableId=' + domId; 
		// open iframe inside dialog
		var $dialog = $(
				'<div id="prevDialog" style="overflow-y: hidden;""></div>')
				.html(
						'<iframe id="formIframeId" style="border: 0px;width:100%;height:100%" src="'
								+ page /* + urlPermissionDisabled */
								+ '"></iframe>').dialog({
					autoOpen : false,
					modal : true,
					height : dialogHeight,
					width : dialogWidth,
					// title: title,
					close : function() {
						var iframeContents = $(this).find('iframe').contents();
						if (iframeContents.find('#close_back').attr('flag')) {// detect if save button was clicked.
							var toReturn = iframeContents.find('#toReturn').val();							
							console.log("toReturn",toReturn);							
							var toReturnObj = JSON.parse(toReturn);
							
							var removedCols = toReturnObj["colsRemoved"].split('@');
							var _table = $('#' + domId).DataTable();
							
							for(var i=0; i < _table.columns().header().length; i++)//fixed  bug 9239
								{
									_column = _table.column(i);
									_header = $(_column.header())[0];
									_title = getColumnUniqueName($(_header));
									if(_column.visible() && removedCols.indexOf(_title)>-1)
									{
										var colIndex = getColumnIndexByColHeader(domId, _title);
									    var input = $(_table.column(colIndex).footer()).find('input[class="firstString"]');
									    $(input).val('');
									    $(input).siblings('select').val('co');
									    input.trigger('keyup');
									}
								}
							setColVisibilityByColTitleArr(domId, true, "resetcolumns_before_colarray_update");
							$('[id="' + domId + '_colsArray"]').val(toReturnObj["colsRemoved"]);
							setColVisibilityByColTitleArr(domId, false, "resetcolumns_after_colarray_update");
							
							// update columns order
							if(isTableReorderable) {								
								setColOrderOnChangeColumnDefinition(_table, domId, hiddenColsIndexArr, toReturnObj["colsNewOrder"], toReturnObj["colsNames"]);
								//15022021 kd workaround. Added next if and call func searchDatatable because bug was happened in case Set Columns window -> unhide column -> search by added column is not worked; in case of appear the problem with performance -> change searchDatatable -> add listeners for specific (just was added) columns only
								if (bl_isTableDrawUpgraded(domId)) { 
									searchDatatable(domId, _table);
								}
							}
						}
						$('#prevDialog iframe').attr('src', 'about:blank');
						$('#prevDialog').remove();
					}
				});

		$dialog.dialog('option', 'dialogClass', 'noTitleStuff').data(
				'colArray', colArray).data('removedArray', removedArray)
				.dialog('open');

	} catch (e) {
		console.log('resetColumns error in eval change struct condition');
	}
}



/**
 * Remove columns
 * 
 * @param domId
 * @param input
 * @returns
 */
function removeColumnDatatable(domId, colTitle) 
{   
	if ($('#' + domId + ' thead  tr:eq(0) th').length == 1) 
	{
		$('#' + domId + ' thead th img').remove();
        return;
    }
    var table = $('#' + domId).DataTable();
    var array = $('[id="' + domId + '_colsArray"]').val().split('@');
    if (array[0] == "")
    {
        array = [];
    }
    
    //fix bug 29352 insert into DT _colsArray (HIDDEN IST) only if the name not exists (case insensitive)
    if($.inArray(colTitle, array) == -1) {
    	array.push(colTitle);
    }
    
    var arrString = "";
    for(var i=0;i<array.length;i++)
    {
    	if(arrString == "")
    	{
    		arrString = array[i] ;
    	}
    	else
    	{
    		arrString = arrString + "@"  + array[i];
    	}
    }    
    $('[id="' + domId + '_colsArray"]').val(arrString);
    
    var colIndex = getColumnIndexByColHeader(domId, colTitle);
    var input = $(table.column(colIndex).footer()).find('input[class="firstString"]');
    $(input).val('');
    $(input).siblings('select').val('co');
    input.trigger('keyup');    
    
    if(globalDataTableFilterColumn!=undefined && globalDataTableFilterColumn[domId]!=undefined){
		delete globalDataTableFilterColumn[domId][colTitle];	
	}
    

    //table = $('#' + domId).dataTable();
    //table.fnSetColumnVis(colIndex, false);
    table.column(colIndex).visible(false);
}

// function returns column "unique title" property
function getColTitleByColIndex(ind, domId)
{
	var _table = $('#' + domId).DataTable();
	var _header = $(_table.column(ind).header())[0];
	var _title = getColumnUniqueName($(_header));	
	return _title;
}

function setColVisibilityByColTitleArr(domId, isVisible, context)
{	
	// check if tere is override by bl_setColVisibilityByColTitleArr
	if(!bl_setColVisibilityByColTitleArr(domId, isVisible, context)) {//setColVisibilityByColTitleArrByReportDesignBL
		var array_ = $('[id="' + domId + '_colsArray"]').val().split('@');
		if(context == "load_last_save_columns") {
			//here is the originally conditions from loadLastSavedColumns
			if ($('#' + domId + ' thead th').length == 1) {
				return;
			}
			
			if (array_[0] == "") {
				return;
			}
			
			try {
				if($('#' + domId + '_structCatalogItem').val() == $('[id="' + domId + '_lastStruct"]').val()) {
					setColVisibilityByColTitleArrCore(domId, isVisible, array_);	
				}
			} catch (e) { 
				return;
				console.log('loadLastSavedColumns error in eval change struct condition');
			}
		} else {
			setColVisibilityByColTitleArrCore(domId, isVisible, array_);
		}
	}
}

function setColVisibilityByColTitleArrCore(domId, isVisible, array) {
	
	var _table = $('#' + domId).DataTable();
	/*//get 'uniqueTitle' from column settings 
	var _settings = _table.settings();		
	var columns_settings = _settings.init().columns;*/
	var _header, _title, _column;
	
	/*find out if table has placeholder column to remove it if at least one of the columns became visible */
	var th_placeholder = null;
	var tds_placeholder = null;
	if($('#' + domId + ' thead th').length == 1)
	{
		th_placeholder = $('#' + domId + ' thead th[colplaceholder]');
		tds_placeholder = $('#' + domId + ' tbody td[colplaceholder]');
	}
	
	try
	{
		for (var j = 0; j < array.length; j++) 
		{
			for(var i=0; i < _table.columns().header().length; i++)
			{
				_column = _table.column(i);
				if(_column.visible() == isVisible)
				{
					continue;
				}
				_header = $(_column.header())[0];
				_title = getColumnUniqueName($(_header));
				
				if(array[j] == _title)
				{
					if((th_placeholder != null && th_placeholder.length > 0) 
							&& 
						(tds_placeholder != null && tds_placeholder.length > 0))
					{
						th_placeholder.remove();
						tds_placeholder.remove();						
						th_placeholder = null;
						tds_placeholder = null;
					}
					_column.visible(isVisible, false); //false means -> defer recalculate column layout for better perfomance
				    break;
				}
			}
		}
		_table.columns.adjust().draw( false ); // adjust column sizing and redraw (recalculate all columns layout after loop)
	}
	catch(e)
	{
		console.log("error in setColVisibilityByColTitleArrCore() for table: " + domId);
		console.log("columns array",array);
		console.log(e);
	}
}

function getColumnIndexByColHeader(domId, colName)
{
	var columnIndObj = {};
	var colNameArr = [];
	var counter = 0;
	var table = $('#'+domId).DataTable();
	if(colName instanceof Array)
	{
		colNameArr = colName;
	}
	else
	{
		colNameArr[0] = colName;
	}
	var colNameArrSize = colNameArr.length;
//	for(var i=0; i < table.columns().header().length; i++)
//	{
//		var _column = table.column(i);
//		var _header = $(_column.header())[0];
//		var _headerText = getColumnUniqueName($(_header));
//		
//		if($.inArray(_headerText, colNameArr) != -1)
//		{
//			columnIndObj[_headerText] = _column.index();
//			counter++;
//			if(colNameArrSize == counter) break;
//		}			
//	} //--> better performance(?) - >
	$.each( table.columns().header(), function( i, value ){
		var _column = table.column(i);
		var _header = $(_column.header())[0];
		var _headerText = getColumnUniqueName($(_header));
		
		if($.inArray(_headerText, colNameArr) != -1)
		{
			columnIndObj[_headerText] = _column.index();
			counter++;
			if(colNameArrSize == counter) return false;
		}
	});
	if(colNameArrSize == 1) return columnIndObj[colName];
	return columnIndObj;
}

/**
 * load Last Saved (removed) Columns
 * @param domId
 * @returns
 */
function loadLastSavedColumns(domId) 
{
//	var array = $('[id="' + domId + '_colsArray"]').val().split('@');
//    if ($('#' + domId + ' thead th').length == 1) {
//        //$('#' + domId + ' thead th img').remove();
//        return;
//    }
//    if (array[0] == "") {
//        return;
//    }
//    var table = $('#' + domId).dataTable();
//    var i, arrayLength = array.length;
//    
//    try {
//    	if($('#' + domId + '_structCatalogItem').val() == $('[id="' + domId + '_lastStruct"]').val()) { // fix bug 
//    		setColVisibilityByColTitleArr(domId,false,"load_last_save_columns");   		
//        }
//    } catch (e) { 
//    	console.log('loadLastSavedColumns error in eval change struct condition');
//	}
    
    setColVisibilityByColTitleArr(domId,false,"load_last_save_columns");
	
    
    /*if ($('#' + domId + ' thead th').length == 1) {
        $('#' + domId + ' thead th img').remove();
        return;
    }*/
}

function getDisplayValueFromSmarts(text)
{
	var jsonData, jsonObject, obj;
	var toreturn = "";
	if (checkIfJSON(text))
	{
		try 
		{
			jsonData = funcParseJSONData(text,true);
	        if(jsonData instanceof Array)
	    	{
	        	jsonObject = jsonData;
	    	}
	        else
	    	{
            	jsonObject = funcParseJSONData("["+text+"]",true);
	    	}
	        
	        for(var y=0; y<jsonObject.length;y++)
	    	{
	        	if(toreturn.length > 0)
	        	{
	        		toreturn += ', ';
	        	}
	        	obj = jsonObject[y];
	        	var _currDisplayName = obj.displayName;
	        	/* in case value of displayName is array of json objects */
	        	if(_currDisplayName instanceof Array)
        		{				                        			
                    for(var i=0; i<_currDisplayName.length;i++)
        	    	{				    		            	        	
                    	if(toreturn.length > 0)
        	        	{
        	        		toreturn += ', ';
        	        	}
                    	var o = _currDisplayName[i];
                    	toreturn += o.displayName;
        	    	}
        		}
	        	else
	        	{
	        		toreturn += _currDisplayName;
	        	}
	    	}
		}
		catch (e) 
		{
			toreturn = text;
			console.log("error in getDisplayValueFromSmarts(): " + text);
			console.log(e);
		}
	}
	else
	{
		toreturn = text;
	}
	return toreturn;
}

function getDisplayValueFromSmartsParsed(jsonArr)
{
	var obj;
	var toreturn = "";
	try 
	{		
        for(var y=0; y<jsonArr.length;y++)
    	{
        	if(toreturn.length > 0)
        	{
        		toreturn += ', ';
        	}
        	obj = jsonArr[y];
        	var _currDisplayName = obj.displayName;
        	/* in case value of displayName is array of json objects */
        	if(_currDisplayName instanceof Array)
    		{				                        			
                for(var i=0; i<_currDisplayName.length;i++)
    	    	{				    		            	        	
                	if(toreturn.length > 0)
    	        	{
    	        		toreturn += ', ';
    	        	}
                	var o = _currDisplayName[i];
                	toreturn += o.displayName;
    	    	}
    		}
        	else
        	{
        		toreturn += _currDisplayName;
        	}
    	}
	}
	catch (e) 
	{
		toreturn = jsonArr;
		console.log("error in getDisplayValueFromSmarts(): " + jsonArr);
		console.log(e);
	}
	return toreturn;
}

function processingHebrewChars(text)
{
	var toreturn = "";
	if (text.charCodeAt(0) > 0x590 && text.charCodeAt(0) < 0x5FF) 
	{
		toreturn = text.split("").reverse().join("");
    }
	else
	{
		toreturn = text;
	}
	return toreturn;
}

function getCheckboxValueFromSmartsParsed(text,row_idx,domId,header)//fixed bug 8572
{
	var obj;
	var toreturn = "";
	try 
	{	
		if (checkIfJSON(text)){
			var obj = funcParseJSONData(text,true);
        	var _currDisplayName = obj.displayName;
        	/* in case value of displayName is array of json objects */
        	if((obj.htmlType == 'checkbox' || obj.htmlType == 'checkCharSample') && _currDisplayName == ''){
        		var table = $('#'+domId).DataTable();
        		var columnInd = getColumnIndexByColHeader(domId,header);
        		var cell = table.cell({row: row_idx, column: columnInd});
        		var cnode = cell.node();	
        		var $input = $(cnode).find('input');
        		var isChecked = $input.prop('checked');
        		if(isChecked != undefined && isChecked){
        			toreturn = "Yes";
        		}else{
        			toreturn = "No";
        		}
        	}
        	//smarticon show tooltip
        	if(obj.icon && _currDisplayName.startsWith('~') && obj.tooltip) {
        		toreturn =  obj.tooltip;
        	}
	   }
	}
	catch (e) 
	{
		toreturn = '';
		//console.log("error in getDisplayValueFromSmarts(): " + jsonArr);
		console.log(e);
	}
	return toreturn;
}

function funcParseJSONData(jsonString,escapeChars)
{
	var obj = {};
	var jsonStringParsed = "";
	escapeChars = (arguments.length === 2 && arguments[1] == false)?false:true;
	
	try
	{	
		if(escapeChars)
		{		
			jsonStringParsed = jsonString.replace(/\\/g, "\\") // backslash
									.replace(/\n/g, "\\n")   //new line
				        			.replace(/\r/g, "\\r")  // carriage return
				        			.replace(/\t/g, "\\t")  // tab
				        			.replace(/\f/g, "\\f") //form-feed char
									;
			obj = JSON.parse(jsonStringParsed);
		}
		else
		{
			obj = JSON.parse(jsonString);
		}
	}
	catch(e)
	{
		console.log("jsonString: ",jsonString);
		console.log("jsonString stringified: ",JSON.stringify(jsonString));
		console.log("jsonStringParsed: ",jsonStringParsed);		
		console.log("jsonStringParsed stringified: ",JSON.stringify(jsonStringParsed));
		console.log("error in parseJSONData()",e);
		return jsonString;
	}
	return obj;
}

function funcParseJSONData_errorThrowed(jsonString)
{
	var obj = {};
	var jsonStringParsed = "";

	jsonStringParsed = jsonString.replace(/\\/g, "\\") // backslash
					.replace(/\n/g, "\\n")   //new line
					.replace(/\r/g, "\\r")  // carriage return
					.replace(/\t/g, "\\t")  // tab
					.replace(/\f/g, "\\f") //form-feed char
					;
	obj = JSON.parse(jsonStringParsed);

	return obj;
}

function funcParseJSONDataAsJSONArray(jsonString)
{	
	var jData = funcParseJSONData(jsonString,true);
	var jArray = [];
    if(jData instanceof Array)
	{
    	jArray = jData;
	}
    else
	{
    	jArray = funcParseJSONData("["+jsonString+"]",true);
	}
    return jArray;
}

function replaceDTUrlCallParamVal(objVal, formId) {
	//TODO add replace columns
	var toReturn = "";
	if( objVal !== 'undefined') {
		toReturn = objVal.replace('@ID@',formId);
	}
	return encodeURIComponent(toReturn);
}

function disableAllDataTableLements (enableElements) { 
	$('table[formElement=1]').each(function(){
		var elemObj = $(this);
		var elementDomId_ = $(elemObj).attr('id');
		if( enableElements == undefined || enableElements.indexOf(elementDomId_) == -1 ) {
			changeSingleDTLabelByDisabledState(elementDomId_, true);//+"_dataTableStructButtons"
		}
	}); 
} 

function changeSingleDTLabelByDisabledState (domid, disabled_state) {
	if(disabled_state) { //disabled
		$('#' + domid + "_Parent").find("button.dataTableApiEdit").text(getSpringMessage("View"));
		$('#' + domid + "_Parent").find("button.dataTableApiEditShared").text(getSpringMessage("View"));
		disableSingleDTinnerElements(domid,disabled_state);//adib 090620 disables the table
	} else { //enable
		$('#' + domid + "_Parent").find("button.dataTableApiEdit").text(getSpringMessage("Edit"));
		$('#' + domid + "_Parent").find("button.dataTableApiEditShared").text(getSpringMessage("Edit"));
		disableSingleDTinnerElements(domid,disabled_state);//adib 211220 enables the table
//		setTimeout(function () {
//            $('#' + domid).find("tbody tr td input[name='checkCharSample']").attr("disabled",false);
//        }, 100);
	}
}

function disableSingleDTinnerElements(domid,disabled_state) {//adib 211220 added disabled_state in order to enable the table
	
	setTimeout(function () {
		if(disabled_state){//disabled
		    $('#' + domid + '_Parent input:not([type="search"],.firstString,[id*="_LinkToLastSelection"],.dataTableApiSelectInfo,[name="checkCharSample"],.dataTableApiSelectInfoLabel,.dataTableApiSelectAllNone)').addClass('disablePage');
		    $('#' + domid + '_Parent button:not(.dataTableApiEditShared,.dataTableApiEdit,.dataTableApiView,.dataTableApiButtonTools,.ireport,.dataTableApiLabel,.collapsible_iframes)').addClass('disablePage');//,
		    $('#' + domid + '_Parent select.disablePage,input[type="radio"].disablePage,input[type="checkbox"]:not(.dataTableApiSelectInfo,[name="checkCharSample"]).disablePage').prop("disabled", true);
	        //$('#' + domid).find("tbody tr td input[name='checkCharSample']").attr("disabled",true);adib 25072019 removed the disabling operation bug 7507
	        if($('#'+domid).hasClass('editable')){
	            // disable all  the inner elements in the edit table
	            disableEditableTables(domid);// there is no way for now to re-enable the editable tables
			}
		} else {//TODO handle the editable tables when enabling them
		    $('#' + domid + '_Parent input:not([type="search"],.firstString,[id*="_LinkToLastSelection"],.dataTableApiSelectInfo,[name="checkCharSample"],.dataTableApiSelectInfoLabel,.dataTableApiSelectAllNone)').removeClass('disablePage');
		    $('#' + domid + '_Parent button:not(.dataTableApiEditShared,.dataTableApiEdit,.dataTableApiView,.dataTableApiButtonTools,.ireport,.dataTableApiLabel,.collapsible_iframes)').removeClass('disablePage');//,
		    $('#' + domid + '_Parent select.disablePage,input[type="radio"].disablePage,input[type="checkbox"]:not(.dataTableApiSelectInfo,[name="checkCharSample"]).disablePage').prop("disabled", false);
		}
    }, 100);
}

function isValidationExist(selectedFormCode,parentFormcode,formId,input,isCloneAttrFlag,newformId,smartSelectList){
	 
    var action = 'checkIfNewFormHasValidation';
	var stringifyToPush = {
 			code: "newFormCode",
 			val:  selectedFormCode,
 			type: "AJAX_BEAN",
 			info: 'na'
 	};
	
	//showWaitMessage("Please wait...");
	prop.onChangeAjaxFlag = false;
	var allData = getformDataNoCallBack(1); 
	var allData = allData.concat(stringifyToPush);
	stringifyToPush = {
 			code: "parentIdList",
 			val:  smartSelectList,
 			type: "AJAX_BEAN",
 			info: 'na'
 	}; 
	allData = allData.concat(stringifyToPush);
	//url call
	var urlParam =
	 "?formId=" + formId + "&formCode=" + parentFormcode + '&userId=' + $('#userId').val() + "&eventAction=" + action + "&isNew=" + $('#isNew').val();
	
	
	var data_ = JSON.stringify({
	 action: "doSave",
	 data: allData,
	 errorMsg: ""
	});
	
	//call...
	 $.ajax({
	        type: 'POST',
	        data:data_,
	        async: false,
	        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
	        contentType: 'application/json',
	        dataType: 'json',
	        success: function (obj) 
	        {
	        	var cnf = obj.data[0].val;
	        	if (cnf !=null && cnf != "")
	        	{  
	        		if(cnf.slice(0,1)=="1"){// a confirmation message
		        		openConfirmDialog({
		        	        onConfirm: function(){	
		        	        	hideWaitMessage();
		        	        	getNewAvailableForm(input,isCloneAttrFlag,newformId,formId,smartSelectList);
		        	        },
		        	        title: 'Warning',
		        	        message: getSpringMessage(cnf.slice(2)),
		        	        onCancel: function(){
		        	        	hideWaitMessage();
		        	        	//return
		        	        	
		        	        }
		        	    });  
	        		} else if(cnf.slice(0,1)=="2"){//an exception. an alert message will be displayed
	        			displayAlertDialog(getSpringMessage(cnf.slice(2)));
	        		}
	        	}
	        	else{
	        		getNewAvailableForm(input,isCloneAttrFlag,newformId,formId,smartSelectList);
	        	}
	        },
	        error: handleAjaxError
	    });
}
function getNewAvailableForm(input,isCloneAttrFlag,formId,parentId,smartSelectList){
	//var parentId = $('#formId').val();*
	var domId = $(input).parent().attr('tableid');
	var parentTable = $(input).parents('div[parentelement]')
    .attr('parentelement');
	parentStruct = parentTable != ''  && $('#' + parentTable).is('table')? $('#' + parentTable + '_structCatalogItem').val():$('#formCode').val();
    var formCode = encodeURIComponent($('[id="' + domId + '_structCatalogItem"]').val());
    var onActionButtons = $('[id="' + domId + '_onActionButtons"]').val();
    var popupSize = $('[id="' + domId + '_popupSize"]').val();
    var jqueryInput = $(input); 
    var popupSize = $('[id="' + domId + '_popupSize"]').val();
    var dialogWidth = $(window).width() - 10;
    var dialogHeight = $(window).height();
    var stateKey = $('#' + domId + '_selectDiv').attr('stateKey');
    if (popupSize != "") {
        dialogWidth = 700;
        dialogHeight = 270;
    }
    
    showWaitMessage("Please wait...");
    
    $.ajax({
        type: 'POST',
        data: '{"action" : "getNewAvailableFormList","' + 'data":[' + '{"code":"formCode","val":"' + formCode + '"},' + '{"code":"formId","val":"' + parentId + '"},' + '{"code":"formIdListCsv","val":"' + smartSelectList + '"},' + '{"code":"stateKey","val":"' + stateKey + '"}' + '],' + '"errorMsg":""}',
        url: "./getNewAvailableFormListById.request",
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) {
        	var canNewByList = false;
            if (obj.errorMsg != null && obj.errorMsg != '') {
            	hideWaitMessage();
                displayAlertDialog(obj.errorMsg);
                return;
            } else if ((obj.data[0].val == "-1") || (obj.data[0].val == "")) {
            	//false
            } else {
            	
                optionsArray = obj.data[0].val.split(',');
                optionsArrayLength = optionsArray.length;
                
                for (i = 0; i < optionsArrayLength; i++) {
                	if(!canNewByList && optionsArray[i] == formCode) {
                		canNewByList = true;
                	}
                }
//                optionsHtml += '<option value="' + optionsArray[i] + '">' + optionsArray[i].replace("InvItem", "") + '</option>';
            }
            
            hideWaitMessage();
            
//            canNewByList = true;  // !!! develop until we set permission
            if(!canNewByList) {
            	displayAlertDialog("<\span><\i  onclick=\"customInfoClickEvent('getWFStatusInfo','STEPS_WF_LIST_INFO','"+parentStruct+"')\""
						+ " style=\"cursor: pointer;margin-right: 5px;\" title=\"WF Info\" class=\"fa fa-info\"><\/i><\/span>\n"
						+"Create new is not allowed");
            	return;
            } else {
            	formCode = onNewButtonIntegration(formCode,$('#formCode').val(),formId,parentId); // change formCode if needed on new button //  onNewButtonIntegration exists in generalBl
            	if(formCode == -1){
            		return;
            	}
            	// make view code
            	var page = "./init.request?stateKey=" + stateKey + "&formCode=" + formCode + "&formId=" + formId + "&userId=" + $('#userId').val() + '&tableType=' + $('[id="' + domId + '_tableType"]').val() + '&urlCallParam=' + replaceDTUrlCallParamVal($('[id="' + domId + '_urlCallParam"]').val(), formId) + '&PARENT_ID=' + parentId + "&smartSelectList=" + smartSelectList + isCloneAttrFlag;

                if ((onActionButtons != 'PopUp') && (!jqueryInput.hasClass('dataTableApiSplit')) && (!jqueryInput.hasClass('dataTableApiMultiClone'))) {
                	showWaitMessage(getSpringMessage('pleaseWait'));
                    window.location = page;
                    return;
                }
                // open iframe inside dialog
                var $dialog = $('<div id="prevDialog" style="overflow-y: hidden;""></div>')
                    .html('<iframe style="border: 0px;width:100%;height:100%" src="' + page + '"></iframe>')
                    .dialog({
                        autoOpen: false,
                        modal: true,
                        height: dialogHeight,
                        width: dialogWidth,
                        //  title: title,
                        close: function () {
                            $('#prevDialog iframe').attr('src', 'about:blank');
                            $('#prevDialog').remove();
                            onElementDataTableApiChange(domId);
                        }
                    });

                $dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
            }
        },
        error: handleAjaxError
    });
    return;
}

function isDivShown(domId)
{
//	if($('#formCode').val()=='Main' && $('#' + domId + '_showDiv').val()=='0'){
//		return false;
//	}
	if($('#formCode').val()=='Main')
	{	
		if($('#' + domId + '_showDiv').val() == '0')
		{
			return false;
		}
		else
		{
			$('#' + domId).closest('[id*="show"]').show(); 
			//init div Count (for the main page)
//		    initDivCount(domId); // remove DivCount
		}
	}
	return true;
}

/*
 * Update columns order and global variable
 */
function setColOrderOnChangeColumnDefinition(dtTable, domId, hiddens, updatedInx, updatedNames )
{
	try {
		var startTime = new Date().getTime();							
		var newOrderArr = hiddens.concat(updatedInx);						
		//console.log( 'concat array took at: '+(new Date().getTime()-startTime)+' mS' );
		dtTable.colReorder.order(newOrderArr);
		console.log( 'Reorder table columns took at: '+(new Date().getTime()-startTime)+' mS' );
		
		// update global variable
		globalDataTableColumnsOrderHolder[domId] = updatedNames;
		//redraw
		dtTable.draw( false );
		// init dtable Fixed Headers
	    initFixedHeaders(domId);
	}
	catch(e) {
		console.error(e);
	}
}

/* Used to merge between original set of columns (loaded from DB) and columns actually saved after reorder 
 * Return array of column's indexes ordered after merge 
 */
function dtGetMergedColumnsOrder(domId, originalColumnsLength, originalColsNameInxObj, savedColsNameArr) 
{
	var newOrderArrInx = 0;
	var isExists = false;
	var startPos = 0;
	var colNewPos;
	var colsLength = originalColumnsLength;
	var newOrderArr = [];	
	var emptyCounter = colsLength; // count empty positions in array
	var maxLoopCnt = colsLength; // set addition counter to prevent infinite loop
		
	try {
		var startTime = new Date().getTime();
		while (maxLoopCnt > 0 && emptyCounter > 0) 
		{
			maxLoopCnt--;
			for(key in originalColsNameInxObj) {
				
				var origOrder =  originalColsNameInxObj[key];
													
				if(newOrderArr.indexOf(origOrder) > -1) continue;									
				
				for(var i=startPos; i<savedColsNameArr.length;i++) {
					var colName = savedColsNameArr[i];
					if(key == colName) {
						isExists = true;
						colNewPos = i;
						break;
					}
				}
				if(!isExists) {
					newOrderArr[newOrderArrInx++] = origOrder;
					emptyCounter--;
				}
				else {
					if(colNewPos == startPos) {
						newOrderArr[newOrderArrInx++] = origOrder;
						startPos++;
						emptyCounter--;
						isExists = false;
					}
				}						
			}
		}
		console.info( 'Merge array took at: '+(new Date().getTime()-startTime)+' mS' );
	}
	catch (e) {
		console.error(e);
	}
	
	if(colsLength > newOrderArr.length) 
	{
		console.info("maxLoopCnt: "+maxLoopCnt, "; emptyCounter: "+emptyCounter);
		console.warn("DataTables warning: table id='"+domId+"' - ColReorder - array reorder does not match known number of columns. Leave default settings.");
		newOrderArr = Object.keys(originalColsNameInxObj);
	}
//	console.log(domId+" newOrderArr: ",newOrderArr);
	return newOrderArr;
}

/**
 * For Hebrew fonts : 
 * ***information can be found here: http://www.rudeprogrammer.com/2016/01/changing-your-datatablepdfmake-font-in-pdf-print-button-for-right-to-left/***
 * 1. Add custom font js instead of the '../deps/vfs_fonts.js' . ***Font can be took from https://github.com/xErik/pdfmake-fonts-google/tree/master/lib ***
 * 	  In our case you can use arial font '../deps/arialFontPDF.js'  (change the import of the js in the page).
 * 2. Run window.pdfMake.fonts = { arial: { normal: 'Arial.ttf', bold: 'Arial.ttf', italics: 'Arial.ttf', bolditalics: 'Arial.ttf', } };
 * 3. Add customize in the pdf config from below
 */
function getDTReportsConfig(domId) 
{
	return [
        {
            extend: 'excel',
            footer: false,
            exportOptions: {
                columns: "thead th"
            },
            customizeData: function (obj) 
            {                
            	//console.log(obj);
            	var b = obj.body;
            	var h = obj.header;
                //console.log(b);
                $.each(b, function (i, rowData) 
                {
                	//console.log(rowData);
                	$.each(rowData, function (j) 
                	{                  		
                    	var _text = getDisplayValueFromSmarts(rowData[j]);
                    	var checkboxVal = getCheckboxValueFromSmartsParsed(rowData[j],i,domId,h[j]);
                    	if (checkboxVal!= ""){
                    		_text = checkboxVal;
                    	}
                    	rowData[j] = processingHebrewChars(_text);
                	});
                });
            },
            customize: function (xlsx)
            {
            	customizeExcelDTReport(xlsx,domId);
            }
        },
        {
            extend: 'pdf',
            pageSize : 'A0',
            orientation: 'landscape',
            footer: false,
            exportOptions: {
                columns: "thead th"
            },
            customize: function (doc) {
                doc.defaultStyle = {
                    font: 'arial',
                    fontSize : 20,
                    alignment: 'center'
//                    ,pageMargins: [ 0, 0, 0, 0 ], 
//                    margin: [ 0, 0, 0, 0 ] 
                }
                doc.styles.tableHeader.fontSize = 20;
                doc.styles.title.fontSize = 24;
//                doc.styles.title.alignment = 'left';
                
                //console.log(doc);
                var b = doc.content[1].table.body;
                var h = doc.content[1].table.headerRows;
                //console.log(b);
                $.each(b, function (i, item) {
                	var rowHeaderList_ = b[0];
                    $.each(this, function (j,rowNames) 
                    {                  		
                    	var _text = getDisplayValueFromSmarts(this.text);
                    	try{
                    		var checkboxVal = getCheckboxValueFromSmartsParsed(this.text,i-h,domId,rowHeaderList_[j].text);
                    		if (checkboxVal!= ""){
                        		_text = checkboxVal;
                    		}
                    	}catch(e) {}
                    		
                    	this.text = processingHebrewChars(_text);
                    });
                });
                /*console.log(doc.content[1].table);
              //doc.content[1].table.widths = [ '10%', '10%', '10%', '10%', '10%', '10%', '10%', '10%'];
                //console.log(doc.content[1].table);
                
                doc.styles['td:nth-child(6)'] = { 
                	       'width': '50px',
                	       'max-width': '50px'
                	     };
                console.log(doc.content[1].table);*/
            }
        },
        {
            extend: 'print',
            footer: false,
            exportOptions: {
                columns: "thead th"
            },
            autoPrint: true,
            customize: function (win) 
            {
            	 var headerList_ = [];
            	 $(win.document.body).find('th').each(function(i){
            		headerList_[i] = $(this).html();
            	});
                //$(win.document.body).find('table').addClass('display').css('font-size', '13px');
            	//console.log($(win.document.body));
            	$(win.document.body).find('tr>td').each(function(index)
                {                                      		
                	var _text = getDisplayValueFromSmarts($(this).html());
                	try{
                		var cell_idx = $(this).context.cellIndex;
                	    var headerName = headerList_[cell_idx];
                	    var checkboxVal = getCheckboxValueFromSmartsParsed($(this).html(),$(this).context.parentNode.rowIndex,domId,headerName);
                	    if (checkboxVal!= ""){
                	    	_text = checkboxVal;
                	    }
                	}catch(e) {}
                	$(this).html(processingHebrewChars(_text));
                });
            }
     }
  ];
}

/* Get last saved value from DB or from temporary global variable
 *  in case global variable is empty - populate it with saved or empty object
 */
function getSavedColumnsWidthObject(domId, savedObj)
{
	var colWidthObj = {};
	try 
	{
	    if(Object.keys(globalDataTableColumnWidthHolder).length == 0 || !globalDataTableColumnWidthHolder.hasOwnProperty(domId))
		{
			globalDataTableColumnWidthHolder[domId] = {};				
			if(savedObj.length > 0)
			{
	    		var _tmp = savedObj[0].columnWidth;
	    		if(_tmp && Object.keys(_tmp).length > 0) colWidthObj = _tmp;	    		
			}
			globalDataTableColumnWidthHolder[domId] = colWidthObj;
		}
	    else
	    {
	    	colWidthObj = globalDataTableColumnWidthHolder[domId];
	    }
	}
	catch(e) {
		colWidthObj = {};
		console.error(e);
	}
    return colWidthObj;
}

/* Get last saved value from DB or from temporary global variable
 *  in case global variable is empty - populate it with saved or empty object
 */
function getSavedColumnsOrderArray(domId, savedObj)
{
    var colsNamesArr = [];
    try 
    {
	    if(Object.keys(globalDataTableColumnsOrderHolder).length == 0 || !globalDataTableColumnsOrderHolder.hasOwnProperty(domId))
		{
	    	globalDataTableColumnsOrderHolder[domId] = {};	
	    	if(savedObj.length > 0)
			{
		    	var _saved = savedObj[0].columnOrder;
				if(_saved && _saved.length > 0) {
					colsNamesArr = _saved;
				}	
			}
	    	globalDataTableColumnsOrderHolder[domId] = colsNamesArr;
		}
	    else
	    {
	    	colsNamesArr = globalDataTableColumnsOrderHolder[domId];
	    }
	}
	catch(e) {
		colsNamesArr = [];
		console.error(e);
	}
	return colsNamesArr;
}

function setGlobalDataTableFilter( domId,colName, values,filterByEmptyVal )
{
	try {
		var val = values.split("|");
		if(!filterByEmptyVal && val==""){//clicking "OK" without checked values
			if(globalDataTableFilterColumn!=undefined && globalDataTableFilterColumn[domId]!=undefined){
				delete globalDataTableFilterColumn[domId][colName];	
			}
			return;
		}
        var obj ={};
        obj[colName] = val;
		if(globalDataTableFilterColumn[domId]!= undefined){
			globalDataTableFilterColumn[domId][colName]= val;
		}else{
			globalDataTableFilterColumn[domId]= obj;		
		}
	}
	catch(e) {
		console.error(e);
	}
}

function initFilterColumnDatatable(tableID) {
	if(bl_initFilterColumnDatatable(tableID)){
	   var table = $('#' + tableID).DataTable();
	    table.columns().iterator('column', function (ctx, idx) {
	    	var _title = getColTitleByColIndex(idx,tableID);
        	
	        if ($(table.column(idx).header()).html() != "" &&_title != 'Favorite' && _title != 'Report') {
	        	var obj_ = $(table.column(idx).footer()).find('.firstString');
				if(globalDataTableFilterColumn!=undefined && globalDataTableFilterColumn[tableID]!=undefined  && globalDataTableFilterColumn[tableID][_title]!=undefined){
	        		obj_.before('<img src=\"../skylineFormWebapp/images/filter.png\" id="filterIcon" style="position: absolute;top:60%;width:11px;"  onclick="filterColumn($(this).closest(\'table\').attr(\'id\'),\''+_title+'\')">');
	                
	        	}else{
	        		obj_.before('<img src=\"../skylineFormWebapp/images/filter_empty.png\" id="filterIcon" style="position: absolute;top:60%;width:11px;"  onclick="filterColumn($(this).closest(\'table\').attr(\'id\'),\''+_title+'\')">');
	        	}
				}
	    });
	}
	}
function deleteGlobalDataTableFilterColumn(domId,idx){
	try{
		if(bl_initFilterColumnDatatable(domId)){
			 var selectedTable = $('#' + domId).DataTable();
			 $(selectedTable.column(idx).footer()).find('#filterIcon').attr("src", "../skylineFormWebapp/images/filter_empty.png");
		     var _title = getColTitleByColIndex(idx,domId);
		 if(globalDataTableFilterColumn!=undefined && globalDataTableFilterColumn[domId]!=undefined){
				delete globalDataTableFilterColumn[domId][_title];	
			}
		}
	}catch(e){
		console.log("deleteGlobalDataTableFilterColumn error",e);
	}
	 
}

function toggleSectionCollapse(elem) {
	var $span = $(elem);
	var title = ($span.hasClass("collapse-icon"))?'Expand':'Collapse';
	$span.toggleClass("collapse-icon expand-icon")
	     .toggleClass("fa-angle-up fa-angle-down")
			.attr('title', title)
			.closest(".section-parent")
			.find(".section-toggle-content")
			.toggle();
}