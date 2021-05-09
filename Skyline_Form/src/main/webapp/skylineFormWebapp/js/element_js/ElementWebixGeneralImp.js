var ElementWebixGeneralImp = {
		value_: function (val_, changeType) 
	    {
	    	var domId = $(val_).attr('id');
	        var elementID = $(val_).attr('elementID');
	        var data_ = {};
	        if(changeType == 2)
	        {
		        var object = getOutputDataGeneral(domId);
		        data_ = {
		        	"isTableHasRows":object.isWebixTableHasRows,
		            "objToSaveAsClob":object.tablesSnapshot,
		            "resultValue":object.output,
		            "parentID": $('#formId').val()
		        };		
	        }
			    console.log(JSON.stringify(data_));
		        return JSON.stringify(data_);
	    },
	    setvalue_: function (val_) {
	        //important: should add attribute 'lastvalue' to the element imp in the poll (like in ElementAutoCompleteIdValDDLImp)
	    },
	    setDefaultValueForUnitTest_: function (val_) {
	    }
};

function initElementDataTableWebixGeneral(domId, tableID, tableDivID, colConfigStr, data, additDataStr, isNew, isDisabled)
{    
	console.log(" -------------------- initElementDataTableWebixGeneral() ---------------------");
	console.log("data for table: " + domId);
	console.log(data);
	isDisabled = false; //yp 10032019 - fix patch for version 1.428.3 - adama prod - slow analytical experiment load
	/*console.log("additDataStr for table: " + domId);
	console.log(additDataStr);*/
	/*console.log("colConfigStr for table: " + domId);
	console.log(colConfigStr);*/
	
    var tableData = {};
    var _navigation = true,
    	_select = "cell",
    	_autoheight = true,
    	_autowidth = true,
    	_editable = true,
    	_math = true,
    	_tooltip = true,
    	_footer = false;
    var _additDataObj = $.parseJSON(additDataStr);
		
    var _colConfigObj = $.parseJSON(colConfigStr); 
    //console.log("_colConfigObj for table: " + domId);
	//console.log(_colConfigObj);
    var _columnsArr = [];
    for (var i=0; i < _colConfigObj.length; i++ ) 
    {       
    	var _editorType = _colConfigObj[i].editorType;
    	_columnsArr.push({ 
        	"id":_colConfigObj[i].columnID,
        	"header": _colConfigObj[i].columnHeader,
        	"editor":_editorType,
        	"options":(_editorType == 'select' || _editorType == 'richselect' || _editorType == 'combo')?setColumnDDLValues(_additDataObj, _colConfigObj[i].columnID):"",//_additDataObj[_colConfigObj[i].columnID]
        	"width":_colConfigObj[i].width,
        	"sort":_colConfigObj[i].sort,
        	"tooltip":_colConfigObj[i].tooltip,
        	"cellType":_colConfigObj[i].cellType,
        	"numberFormat":_colConfigObj[i].numberFormat,//"1.00",
        	"hidden":_colConfigObj[i].hidden,
        	"format":(_colConfigObj[i].format)?setCellFormat(_colConfigObj[i].cellType, _colConfigObj[i].precision):"",       	
        	"template":renderCellByTemplate(_colConfigObj[i].template),
        	"getUniqueID":(_colConfigObj[i].getUniqueID == null)?"":_colConfigObj[i].getUniqueID,
        	"mandatory":(_colConfigObj[i].mandatory == null)?false:_colConfigObj[i].mandatory,
        	"mandatoryOptionalField":(_colConfigObj[i].mandatoryOptionalField == null)?"":_colConfigObj[i].mandatoryOptionalField,
        	"defaultValue":(_colConfigObj[i].defaultValue == null)?"":_colConfigObj[i].defaultValue
        });
    }
    console.log("_columnsArr for table: " + domId);
	console.log(_columnsArr);
    if(isNew == "1")
	{
    	if($('#isNew').val()=='1')
    	{
    		isDisabled = false;
    	}
    	tableData = prepareInitDataToWebixGeneral(domId, tableID, data, _columnsArr);
	}
    else
	{   	
    	tableData = funcParseJSONDataWX(data,true);  	
	}	
    
    console.log("data after parse for tableID: " + tableID);
	console.log(tableData);
	console.log(" ------------------------------------------------------");
	webix.ready(function()
	{		
		webix.ui({
			container:tableDivID,
			view:"datatable",
			id: tableID,
			columns:_columnsArr,			
            navigation:_navigation,
            select:_select,
			autoheight:_autoheight,
			autowidth:_autowidth,
			editable:_editable,
			math: _math,
			tooltip:_tooltip,
			footer:_footer,
			disabled:isDisabled,
			data:tableData,
//			scheme:{
//		          $init:function(obj){
//		            obj.instrument = chooseInstrumListFirstId(_columnsArr); //kd 22122019  call it for choose instrument by default        
//		          }
//		        },
			on:{
				onBeforeEditStop:function(values, editor){
					var _editorType = editor.config.editor;
					if(_editorType == 'combo'){//TODO:check how to handle  the following cases.editor does not have getPopup()=>_editorType == 'select' || _editorType == 'richselect'
						if(editor.getPopup().getList().getItem(values.value)==undefined){
							return;
						}
						if(editor.getPopup().getList().getItem(values.value).$css=='disabled'){
							webix.delay(function(){
				                this.updateItem(editor.row, {[editor.column]: values.old})}, 
				            this);
						}
					}
				}, 
				onAfterEditStart:function(id){
					if(id.column == "non_numeric_result") {
					  this.getEditor().getInputNode().setAttribute("maxlength", 30);
					}
				},
				/*"onItemClick":function(id, e, trg){
					//id.column - column id
					//id.row - row id
					console.log("Click on row: " + id.row+", column: " + id.column + " row index: " + this.getIndexById(id.row));
					webix.message("Click on row: " + id.row+", column: " + id.column);
				},*/
				onAfterLoad:function()
				{
					console.log("ON AFTER LOAD");	
					console.log(this.config.columns[1]);
					try { // ADD try catch for bug 7801
						if(this.config.columns[1].id == "invitem_material")
						{
							var _curColConfig = this.config.columns[1];
							var arr = _curColConfig.collection.config.data;
							console.log(arr);
							console.log(arr.length + "  value: " + arr[0].value);
							if(arr.length == 1 && arr[0].value == "")
							{
								_curColConfig.collection.config.data = [];
								_curColConfig.collection.clearAll();
							}
						}
						
						if($('#formCode').val() == 'SelfTest' && domId == 'webixAnalyticalSelfTest') { // workaround for bug when download the label after creating samples and back to the self test -> because of the sownload we not getting to webix.ui({ code and the table is not displayed in the screen (without error in the log)
							//print label on load if we have data in _labelCode / _labelData (passed from previous form)
							if ( $('#_global_labelCode').length > 0 && $('#_global_labelCode').val() != "" && $('#_global_labelCode').val() != 'undefined' && $('#_global_labelData').length > 0 && $('#_global_labelData').val() != "" && $('#_global_labelData').val() != 'undefined')
							{
								outPutLabel("_global",$('#_global_labelCode').val(),$('#_global_labelData').val());
							}
						}
					} catch (e) {
							console.log("webix - ON AFTER LOAD error");
					}
				},
				"data->onStoreUpdated":function(){ // row index dynamically fixed after add/remove actions
			        this.data.each(function(obj, i){
			          obj.index = i+1;
			        })
			      }
			},
			onClick:{				
				"delbtn":function(e, id, trg){
			    	//id.column - column id
					//id.row - row id
					this.editStop();
					console.log(id);
					if(this.count() == 1)
					{
						//webix.message("Delete the last row is impossible: "+id);
						displayAlertDialog("Delete the last row is impossible");
					}
					else
					{
						//webix.message("Delete row: "+id);
						this.remove(id.row);
					}
					//block default onclick event
					return false;
			    },
			    "addbtn":function(e, id, trg){
					//id.column - column id
					//id.row - row id
			    	var _curInd = this.getIndexById(id.row);
			    	var _nextInd = _curInd + 1;
					var _uid = webix.uid();
					console.log(_uid);		
						//webix.message("Add row: "+ _uid);
						this.add({
						     id:_uid,
						     instrument : chooseInstrumListFirstId(_columnsArr) //kd 22122019 choose instrument by default in case only one Instrument
						},_nextInd);
							//block default onclick event
						return false;
				}			
			},
			ready:function()
			{				 				
				this.attachEvent("onKeyPress", function(code, e){
					console.log("ON KEY PRESS");
					var _thisEditor = this.getEditor();
					var _thisEditorConfig = _thisEditor.config;
					//console.log(_thisEditor.config);
					
					if(_thisEditor != undefined && _thisEditor != "0")
					{
						if(_thisEditorConfig.editor == "text" 
							&& (_thisEditorConfig.cellType == "decimal" || _thisEditorConfig.cellType == "numeric"))
						{
							validateDecimalWX(code, e);
						}
						else if(_thisEditorConfig.editor == "popup")
						{
							validateLegalCharWX(code, e);
						}
					}
					
				});
			}
		});	
			
	});
}

function setColumnDDLValues(obj, colId)
{
	//console.log("setColumnDDLValues()");
	var arr = obj[colId];
	//console.log(arr);
	if(colId == "invitem_material")
	{
		if($.isEmptyObject(arr))
		{
			arr = [{"id":"0", "value":""}];
		}
	}
	if(colId == "instrument") //kd 19122019
	{
		if($.isEmptyObject(arr))
		{
			arr = [{"id":"0", "value":""}];
		}
	}
	if(colId == "result_type") //kd 19122019
	{
		if($.isEmptyObject(arr))
		{
			arr = [{"id":"0", "value":""}];
		}
	}
	//if active is defined as 0 in the object then adding a disabled class to the option
	for(var i=0;i<arr.length;i++){
		var option = arr[i];
		if(option.active=='0'){
			arr[i].$css='disabled';
		}
	}
	return arr;
}

function setCellFormat(cellType, precision)
{
	if(cellType == 'decimal')
	{
		console.log("setCellFormat() precision: " + precision);
		var _precision = (precision==null || precision=="")?0:precision;
		return webix.Number.numToStr({
		    groupDelimiter:" ",
			groupSize:0,
			decimalDelimiter:".",
			decimalSize:_precision
		});
	}
}

function renderCellByTemplate(_template)
{
	if(_template == 'add_remove_btn')
	{
		//return "<button class='button addbtn' type='button' onclick=''>Add</button><button class='button delbtn' type='button' onclick=''>Delete</button>";
		return "<i class='delbtn webix_icon fa-trash' title='Remove row' style='margin-right:10px;cursor:pointer;'></i>" +
				"<i class='addbtn webix_icon fa-plus' title='Add row' style='cursor:pointer;'></i>";
	}
	else
		return "";
}

function prepareInitDataToWebixGeneral(domId, tableID, data, colArr)
{
	var objToReturn = [];	
	try
	{
		var obj = JSON.parse(data);			
		if(obj.length == 0)
		{
			return {};
		}		
		$(obj).map(function () 
	    {
			var curRowObj = this;
			var _tmpobj = {};
			_tmpobj["id"] = webix.uid();
			$(colArr).map(function () 
		    {
				var colID = this.id;
				_tmpobj[colID] = curRowObj[colID];
		    });
			_tmpobj["start"] = "";
			_tmpobj["end"] = "";
			objToReturn.push(_tmpobj);
	    }); 
	}
	catch(e)
	{
		console.log("ERROR in prepareInitDataToWebixGeneral():");
		console.log("tableData: " + data);
		console.log(e);
		return {};
	}
	return objToReturn;
}

/*
 * kd 22122019
 * this function fills instrument field from Instruments list in the Results table 
 * Fills on open form or on insert new row in the Redults table
 */
function chooseInstrumListFirstId(array) {
	var result;
	for (var i = 0; i < array.length; i++) {
		if (array[i].id =='instrument' && array[i].collection != undefined){
			if (array[i].collection.config.data.length == 1){
				result = array[i].collection.config.data[0].id;
			} else if (array[i].collection.config.data.length == 2 && array[i].collection.config.data[0].id == 0){
				result = array[i].collection.config.data[1].id;
			}
//			updateInstrumentFromList();
		}
	}
return result;
}

/*
 * kd 22122019
 * Adds one row to the list of Instruments in the Results table
 */
/*function updateSelfTestResultWebixElementInstrum(_id,_val) {
	console.log("-------- updateSelfTestResultWebixElementInstrum() ------------");
	console.log("instrumentId: " + _id);
	console.log("instrumentName: " + _val);
	console.log("--------------------");
	try {
		if (_id != "" && _val != "") {
			var $div = $("div[name='parentWebixContainer'][id='webixAnalyticalSelfTest']");
			var tableID = $($div.find(".webix_container")).attr('childTableID');
	
			var grid = $$(tableID);
			if (grid !=  undefined) {
				var _curColConfig = grid.getColumnConfig("instrument");
		
				var arr = _curColConfig.collection.config.data;
				
				if (!$.isEmptyObject(arr) && arr.length == 1 && arr[0].value == "") {
					_curColConfig.collection.config.data = [];
					_curColConfig.collection.clearAll();
		
					console.log(_curColConfig.collection.config.data);
					console.log(_curColConfig.collection);
		
					_curColConfig.collection.config.data = [ {
						id : _id,
						value : _val
					} ];
					_curColConfig.collection.parse([ {
						id : _id,
						value : _val
					} ]);
		
					console.log(_curColConfig.collection.config.data);
					console.log(_curColConfig.collection);
		
				} else {
					var alreadyExists = false;
					for (var i = 0; i < _curColConfig.collection.config.data.length; i++) {
						if (_curColConfig.collection.config.data[i].id == _id){
							alreadyExists = true;
						}
					}
					if (!alreadyExists) {
						arr.push({
							id : _id,
							value : _val
						});
					}
					_curColConfig.collection.parse(arr);
				}
				updateInstrumentFromList(_id);
			}
		}
	} catch (e){}
}*/



//function updateInstrumentFromList(_id)
function updateInstrumentFromList()
{
	/*if (_id == undefined){
		openConfirmDialog({
			onConfirm : function()
			{
				updateInstrumentIfWarning();
			},
			title : 'Warning',
			message : getSpringMessage('INSTRUMENT_FIELD_WILL_REMOVED')
		})
	} else
	{*/
		var $div = $("div[name='parentWebixContainer'][id='webixAnalyticalSelfTest']");
		var tableID = $($div.find(".webix_container")).attr('childTableID');
		console.log("updateInstrumentFromList for table id: " + tableID);
		try {
			if(tableID != null && tableID != "")
			{
				var grid = $$(tableID);	
	//			grid.editStop();
				if (grid !=  undefined) {
					var _curColConfig = grid.getColumnConfig("instrument");
					var arr = _curColConfig.collection.config.data;
					
					if (!$.isEmptyObject(arr) /*&& arr.length == 1 && arr[0].value == ""*/) {
						grid.eachRow( 
						    function (row)
						    { 
						    	var rowObj = grid.getItem(row);
						    	/*if (_id != undefined && (rowObj.instrument == _id || rowObj.instrument == 0 || rowObj.instrument == "" ))
						    	{
						    		if (arr.length == 1)
						        	{
						        		rowObj.instrument = arr[0].id;
									} else if (arr.length == 2 && arr[0].id == 0)
									{
										rowObj.instrument = arr[1].id;
									} else {
										rowObj.instrument = 0;
									}
						    		
						    	} else if (_id == undefined) 
						    	{
						    		if (_id == undefined){*/
				    					if (arr.length == 1)
							        	{
							        		rowObj.instrument = arr[0].id;
										} else if (arr.length == 2 && arr[0].id == 0)
										{
											rowObj.instrument = arr[1].id;
										} else {
											rowObj.instrument = 0;
										}
//						    		}
//						    	}
						    });
						grid.refresh();
					}
				}
			}
		} catch (e){
			console.log("webix - updateInstrumentFromList error");
		}
		
}

/*function updateInstrumentIfWarning()
{


INSTRUMENT_FIELD_WILL_CLEARED=All existed values in the Instrument column of Results table will be cleared or replaced


	var $div = $("div[name='parentWebixContainer'][id='webixAnalyticalSelfTest']");
	var tableID = $($div.find(".webix_container")).attr('childTableID');
	console.log("updateInstrumentIfWarning for table id: " + tableID);
	try {
		if(tableID != null && tableID != "")
		{
			var grid = $$(tableID);	
//			grid.editStop();
			if (grid !=  undefined) {
				var _curColConfig = grid.getColumnConfig("instrument");
				var arr = _curColConfig.collection.config.data;
				
				if (!$.isEmptyObject(arr) && arr.length == 1 && arr[0].value == "") {
					grid.eachRow( 
					    function (row)
					    { 
					    	var rowObj = grid.getItem(row);
					    	{
					    		if (_id == undefined){
					    			openConfirmDialog({
					    				onConfirm : function()
					    				{
					    					if (arr.length == 1)
								        	{
								        		rowObj.instrument = arr[0].id;
											} else if (arr.length == 2 && arr[0].id == 0)
											{
												rowObj.instrument = arr[1].id;
											} else {
												rowObj.instrument = 0;
											}
					    				},
					    				title : 'Warning',
					    				message : getSpringMessage('INSTRUMENT_FIELD_WILL_REMOVED')
					    			})
					    		}
					    	//}
					    });
					grid.refresh();
				}
			}
		}
	} catch (e){}
}*/

//function removeSelfTestResultWebixElementInstrumById(columnName, _id) {   instrument
//function removeRowByIdFromComboSelfTestResultWebixElement(columnName, _id) {
function removeRowByIdFromComboListSelfTestResultWebixElement(columnName, _id) {
	console.log("-------- removeRowByIdFromComboListSelfTestResultWebixElement() ------------");
	console.log(columnName+"Id: " + _id);
	console.log("--------------------");

	if (_id != "") {
		try {
			var $div = $("div[name='parentWebixContainer'][id='webixAnalyticalSelfTest']");
			var tableID = $($div.find(".webix_container")).attr('childTableID');
	
			var grid = $$(tableID);
			if (grid !=  undefined) {
				var _curColConfig = grid.getColumnConfig(columnName);
		
				var arr = _curColConfig.collection.config.data;
				
				if (!$.isEmptyObject(arr) && arr.length == 1 && arr[0].value == "") {
		
				} else {
					_curColConfig.collection.clearAll();
					_curColConfig.collection.config.data = [{id : "0",value : ""}];
					var j=0;
					for (var i = 0; i < arr.length; i++) {
						if (arr[i].id != _id && arr[i].id != undefined){
							_curColConfig.collection.config.data[j] = {
								id : arr[i].id,
								value : arr[i].value
							} ;
							_curColConfig.collection.parse( [{
								id : arr[i].id,
								value : arr[i].value
							}] );
							j++;
						}
					}
					if (j == 0){
						_curColConfig.collection.parse([{id : "0",value : ""} ])
					}
				}
	//			updateInstrumentFromList(_id);
				updateInstrumentFromList();
			}
		} catch (e){
			console.log("webix - removeRowByIdFromComboListSelfTestResultWebixElement error");
		}
	}
}

function clearComboListSelfTestResultWebixElement(columnName) {
	console.log("-------- clearComboListSelfTestResultWebixElement() ------------");
	console.log("--------------------");
	try {
		var $div = $("div[name='parentWebixContainer'][id='webixAnalyticalSelfTest']");
		var tableID = $($div.find(".webix_container")).attr('childTableID');
	
		var grid = $$(tableID);
		if (grid !=  undefined) {
			var _curColConfig = grid.getColumnConfig(columnName);
	
			_id = "0";
			_val = "";
			_curColConfig.collection.config.data = [];
			_curColConfig.collection.clearAll();
	
			console.log(_curColConfig.collection.config.data);
			console.log(_curColConfig.collection);
	
			_curColConfig.collection.config.data = [ {
				id : _id,
				value : _val
			} ];
			_curColConfig.collection.parse([ {
				id : _id,
				value : _val
			} ]);
	
		}
	} catch (e) {
		console.log("webix - clearComboListSelfTestResultWebixElement error");
	}
}

function getOutputDataGeneral(domId)
{			
	console.log("getOutputDataGeneral(): " + domId);
	var tablesSnapshotObj = [];
	var returnObj = {};
	var outputArr = [];
	var fullOutputArr = [];
	var isTableHasRows = 0;
	
	try {
		var $div = $("div[name='parentWebixContainer'][id='"+domId+"']");
		$div.find(".webix_container[isWebixTableHidden='false']").each(function()
		{
			var tableID = $(this).attr('childTableID');
			var grid = 	$$(tableID);
			
			if (grid !=  undefined) {
				grid.editStop();
			
				grid.eachRow(function (row)
				{ 
					isTableHasRows = 1;
					var rowObj = grid.getItem(row);
					var _tmpobj = {};
					grid.eachColumn( 
						    function (columnId)
						    { 
						    	var _curCellVal = rowObj[columnId];
						    	var _curColConfig = this.getColumnConfig(columnId);
						    	//console.log(_curColConfig);
						    	//console.log("_curCellVal: " + _curCellVal);
						    	if(_curColConfig["getUniqueID"] != "" && (_curCellVal == null || _curCellVal == ""))
						    	{
						    		//console.log(_curColConfig["getUniqueID"]);
						    		_curCellVal = generateFromId(_curColConfig["getUniqueID"]);
						    		//console.log(_curCellVal);
						    		rowObj[columnId] = _curCellVal;
						    	}
						    	if(_curColConfig["defaultValue"] != "" && (_curCellVal == null || _curCellVal == ""))
						    	{
						    		//console.log(_curColConfig["getUniqueID"]);
						    		_curCellVal = _curColConfig["defaultValue"];
						    		//console.log(_curCellVal);
						    		rowObj[columnId] = _curCellVal;
						    	}
						    	_tmpobj[columnId] = _curCellVal;
						    },
						    true // include hidden columns
				     );
					outputArr.push(_tmpobj);
				});
				
				/*console.log(" -------------- outputArr ---------------");
				console.log(outputArr);
				console.log("----- end -----------");*/
				
				tablesSnapshotObj = {data:grid.serialize()};
				fullOutputArr = {rowsData:outputArr};
			}
		//}	
		});
		returnObj = {isWebixTableHasRows:isTableHasRows, tablesSnapshot:tablesSnapshotObj, output: fullOutputArr};
		console.log("returnObj: ");
		console.log(JSON.stringify(returnObj));
	} catch (e){
		console.log("webix - getOutputDataGeneral error");
	}
	return returnObj;
}

