/**
 * Set general disabled state for webix tables
 * @param isDisabled
 * @returns
 */
function setWebixTablesDisabled(isDisabled, limitIndex)
{
	if(limitIndex == null) {
		limitIndex = 0;
	}
	console.log("setGeneralDisablesStateWebixTables: " + isDisabled + ", limitIndex="  + limitIndex);
	$('div[class="webix_container"]:not(.authorizationDisabled,.disablePage)').each(function()
	{
		var webixObject;
		var tableID = $(this).attr('childTableID');
		//console.log('webix contains form: ' + $(this).find('div.webix_form').length);
		if($(this).find('div.webix_form').length > 0)
		{
			webixObject = $$("wbxForm_"+tableID);
		}
		else
		{
			webixObject = $$(tableID);
		}
		if (typeof webixObject === "undefined") {  
			if(limitIndex < 10) {
				 var tout_ = 200 * limitIndex;
				 console.log("webixObject not ready wait tout_=" + tout_);
				 setTimeout(function () {
					 setWebixTablesDisabled(isDisabled, limitIndex + 1);
				 }, (tout_));
			} else {
				alert('setWebixTablesDisabled initPage call');
				initPage();
			}
		} else {
			handelWebixGridDisabled(webixObject, isDisabled);
		}
	});
}

function handelWebixGridDisabled(grid, isDisabled) {
	if(grid)
	{
		if(isDisabled)
			grid.disable();
		else
			grid.enable();
	}
}

/**
 * Authz set for webix elements
 * @param isDisabled
 * @param info
 * @returns
 */
function setDisableWebixTables(isDisabled, info) // move to commen webix js code
{
	console.log('AUTH setDisableWebixTables: ' + isDisabled);
	var _dsbl = (isDisabled == "1")?true:false;
	$(".webix_container").each(function()
	{
		var webixObject;
		var tableID = $(this).attr('childTableID');
		//console.log('webix contains form: ' + $(this).find('div.webix_form').length);
		if($(this).find('div.webix_form').length > 0)
		{
			webixObject = $$("wbxForm_"+tableID);
		}
		else
		{
			webixObject = $$(tableID);
		}

		if(webixObject)
		{			
			if(_dsbl)
				webixObject.disable();
			else
				webixObject.enable();
		}
		 
		if(_dsbl)
			$(this).addClass('authorizationDisabled');
		else
			$(this).removeClass('authorizationDisabled');
	});
	if(_dsbl)
	{
		$('button[name="webixContainerButtons"]').addClass('authorizationDisabled'); 
		$('input[name="webixContainerInputFields"]').addClass('authorizationDisabled');
	}
	else
	{
		$('button[name="webixContainerButtons"]').removeClass('authorizationDisabled'); 
		$('input[name="webixContainerInputFields"]').removeClass('authorizationDisabled');
	}
}

/**
 * Authz set for webix elements
 * @param isDisabled
 * @param info
 * @returns
 */
function setDisableWebixTableId(isDisabled , elementId) // move to commen webix js code
{
	console.log('AUTH setDisableWebixTables: ' + isDisabled);
	var _dsbl = (isDisabled == "1")?true:false;
	$('#'+ elementId).parent().find(".webix_container").each(function()
	{
		var webixObject;
		var tableID = $(this).attr('childTableID');
		console.log('webix contains form: ' + $(this).find('div.webix_form').length);
		if($(this).find('div.webix_form').length > 0)
		{
			webixObject = $$("wbxForm_"+tableID);
		}
		else
		{
			webixObject = $$(tableID);
		}

		if(webixObject)
		{			
			if(_dsbl)
				webixObject.disable();
			else
				webixObject.enable();
		}
		 
		if(_dsbl)
			$(this).addClass('authorizationDisabled');
		else
			$(this).removeClass('authorizationDisabled');
	});
	if(_dsbl)
	{
		$('#'+ elementId).parent().find('button[name="webixContainerButtons"]').addClass('authorizationDisabled'); 
		$('#'+ elementId).parent().find('input[name="webixContainerInputFields"]').addClass('authorizationDisabled');
	}
	else
	{
		$('#'+ elementId).parent().find('button[name="webixContainerButtons"]').removeClass('authorizationDisabled'); 
		$('#'+ elementId).parent().find('input[name="webixContainerInputFields"]').removeClass('authorizationDisabled');
	}
}

/**
 * Validate for webix elements before Save()
 */
function elementWebixValidation()
{
	var doContinue = true;
	try
	{
		$('input[name="webixContainerInputFields"]:not([readonly],authorizationDisabled)').each(function()
		{
			var _fieldVal = $('#' + $(this).attr('id')).val();
			var _fieldLabel = $(this).closest('div').find('label').text();
			//console.log(_fieldLabel + "||" + _fieldVal);
			if(!fnValidateNumeric(_fieldVal, _fieldLabel, true, false))
			{
				doContinue = false;
				return;
			}
		});
		$('div[class="webix_container"][isWebixTableHidden="false"]:not(.authorizationDisabled,.disablePage)').each(function()
		{
			if(!doContinue)return;
			var tableID = $(this).attr('childTableID');
			
			if($(this).find('div.webix_form').length > 0)
			{
				console.log("webix validate FORM: " + "wbxForm_"+tableID);
				var $form = $$("wbxForm_"+tableID);
				var fieldsValues = $$("wbxForm_"+tableID).getValues();
				console.log(fieldsValues);
				
				$.each(fieldsValues, function (key, value) 
				{
			        console.log('key: '+ key + " val: " + value);
			        try
			        {
				        var _config = $$(key+tableID).config;
				        console.log(_config);
				        if(_config.view == "text" && _config.cellType)
				        {	
				        	if(_config.cellType == "decimal" && !fnValidateNumeric(value, _config.label, true,false,false))
							{
			    				doContinue = false;
			    				return;
							}
				        	if(_config.cellType == "string")
				        	{	
				        		if(!checkIsEmpty(value) && !validateLegalStringWX(value))
			    				{
			    					var alertMsg = (_config.label + " contains illegal characters" + "<br/>Please do not use \" \' ");			    					
			    					displayAlertDialog(alertMsg, {title:"Invalid Value"});
			    					doContinue = false;
			    					return;
			    				}
				        	}
				        }
				        if(_config.view == "combo" && _config.mandatory && _config.mandatory == true)
				        {
				        	if(checkIsEmpty(value))
	    					{
	    						displayAlertDialog(_config.label + " is required field", {title:"Required Data Missing"});
	    						doContinue = false;
	    						return;
	    					}
				        }
			        }
			        catch(e)
			        {
			        	console.log(e);
			        }
			    });
			}
			
			console.log("webix validate TABLE: " + tableID);
			var dtable = $$(tableID);
			dtable.editStop();
			var tableData = dtable.serialize();
			
			var rowIndx = -1;
			var emptyRow = [];
			try {
				if(tableID == 'tableID_webixAnalyticalSelfTest' || tableID == 'tableID_webixNonNumericSelfTest') {
					var valFound = false;
					//check empty row
					
					dtable.eachRow(function (row) { 
						$.each(dtable.Aj, function (key, value) {
							var rowObj = dtable.getItem(row);
							rowIndx = rowObj.index;
							if(!valFound && !checkIsEmpty(rowObj[key])) {
								valFound = true;
							}  
						});
						
						if(!valFound) {
							emptyRow.push(rowIndx);
						}
						
						valFound = false;
						rowIndx = -1;
					});
				}
			} catch(e) {
				emptyRow = [];
		    }
			
			dtable.eachColumn( 
				    function (columnId)
				    { 
				        if(!doContinue)return;
				    	var _config = dtable.getColumnConfig(columnId);
				    	//console.log("-------- elementWebixValidation() config ------------");
				    	//console.log( _config );
				    	var _isRequired = _config.mandatory;
				    	if(_config.editor == "text" && (_config.cellType == "decimal" || _config.cellType == "numeric"))
				    	{
				    		//console.log(columnId);
				    		var _isDecimal = (_config.cellType == "decimal")?true:false;
				    		for(var j=0;j<tableData.length;j++)
							{
								var _val = tableData[j][columnId];								
				    			if(!isEmptyRow(emptyRow, j + 1) && !fnValidateNumeric(_val, _config.header[0].text + " column ", _isDecimal, false,_isRequired))
								{
				    				console.log("row index: " + j + " val: " + _val);
				    				doContinue = false;
				    				break;
								}
							}
				    	}
				    	else if((_config.editor == "select" || _config.editor == "combo") && _isRequired)
				    	{
				    		dtable.eachRow(function (row)
		    				{
			    				var rowObj = dtable.getItem(row);
			    				var _curCellVal = rowObj[columnId];
			    				var _optionalCellVal = (_config.mandatoryOptionalField == "")?"":rowObj[_config.mandatoryOptionalField];
			    				if(!isEmptyRow(emptyRow, rowObj.index) && checkIsEmpty(_curCellVal) && checkIsEmpty(_optionalCellVal))
			    				{
			    					displayAlertDialog(_config.header[0].text + " column " + " is required", {title:"Required Data Missing"});
			    					doContinue = false;
			    					return;
			    				}
		    				});
				    	}
				    	else if(_config.editor == "popup")
				    	{
				    		dtable.eachRow(function (row)
		    				{
			    				var rowObj = dtable.getItem(row);
			    				var _curCellVal = rowObj[columnId];
			    				if(!checkIsEmpty(_curCellVal) && !validateLegalStringWX(_curCellVal))
			    				{
			    					var alertMsg = (_config.header[0].text + " contains illegal characters" + "<br/>Please do not use \" \' ");			    					
			    					displayAlertDialog(alertMsg, {title:"Invalid Value"});
			    					doContinue = false;
			    					return;
			    				}
		    				});
				    	}
				    }
				)
		});
		
		console.log("elementWebixValidation() doContinue: " + doContinue);
		return doContinue;
	}
	catch(e)
	{
		console.log("elementWebixValidation() ERROR:");
		console.log(e);
		return true;
	}	
}

function isEmptyRow(emptyRow, rowindex) {
	try {
		for(var i = 0; i < emptyRow.length; i++) {
			if(emptyRow[i] == rowindex) {
				return true;
			}
		}
	} catch (e) {
		
	}
	return false;
}

function validateDecimalWX(key, e)
{	
	console.log("validateDecimalWX: key="+key);
	var event = e ? e : window.event;
	var retVal = false;
	var value = event.srcElement.value;
	
	if((!e.shiftKey && (key >= 48 && key <=57)) // 0...9
	   || (key == 46 || key == 8) //delete || backspace
	   || (value.length > 0 && value.indexOf('.') == -1 && e.key == ".")// decimal point = 110, period = 190
	   || ((key == 86 || key == 67) && e.ctrlKey) // ctrl+v //ctrl+c
	  )
	{
		retVal = true;
	}
	
	if(!retVal)
	{ 
		event.preventDefault ? event.preventDefault() : (event.returnValue = false);		
	}       
}

function validateLegalCharWX(key, e)
{
	console.log("validateLegalCharWX: key="+key);    	
	var event = e ? e : window.event;
    var retVal = true;
    
    if ((e.shiftKey && key == 222) ||key == 222) // " '
    {
	 	retVal = false;
    }
	if(!retVal)
	{
      event.preventDefault ? event.preventDefault() : (event.returnValue = false);
	}
}

function validateLegalStringWX(inStr)
{
	var illegalChars = ['\"', '\''];
	if (inStr == null)
	{
		return true; 
	}
		
	for (var i in illegalChars)
	{
		if (inStr.indexOf(illegalChars[i]) > -1)
			return false;
	}
	return true;	
}

function funcParseJSONDataWX(jsonString,escapeChars)
{
	var obj = {};
	try
	{
		//console.log("parseJSONData: ");
		//console.log(jsonString);
		/*jsonString = jsonString.replace(/\n/g, " ")   //new line
								.replace(/\r/g, " ")  // carriage return
								.replace(/\t/g, " ")  // tab
								.replace(/\f/g, " "); //form-feed char
		 */		
		if(escapeChars)
		{		
			jsonString = jsonString.replace(/\\/g, "\\") // backslash
									.replace(/\n/g, "\\n")   //new line
				        			.replace(/\r/g, "\\r")  // carriage return
				        			.replace(/\t/g, "\\t")  // tab
				        			.replace(/\f/g, "\\f") //form-feed char
									;
			//console.log("parseJSONData: after replace");
			//console.log(jsonString);	
		}
		obj = JSON.parse(jsonString);
	}
	catch(e)
	{
		obj = {};
		console.log(jsonString);
		console.log("parseJSONData()",e);
	}
	return obj;
}

function clearWebixTableData(newData, tableID)
{
	var $tableId = $$(tableID);
    $tableId.clearAll();
    $tableId.parse(newData, "json");
    $tableId.refresh();
}

function addNewEmptyRowToWebixTable(tableID)
{
	var $tableId = $$(tableID);
	var _uid = webix.uid();
	console.log('_uid: ' + _uid);		
	$tableId.add({
		     id:_uid
		});
}

//function updateSelfTestResultWebixElementInstrum(columnName, domId) { //new 29122019
/*
 * update comboList of webixtable with id webixAnalyticalSelfTest
 * according to rows are defined in table with @domId
 * @columnName - name of column with comboList
 * it is possible to make more universal if change 'webixAnalyticalSelfTest' on parameter 
 * 
 */
function updateWebixElementList(columnName, domId) { //new 29122019
	for (var i = 0; i < $('#'+domId+' tbody tr').length; i++) 
	{
		var _id = $('#'+domId+' tbody tr td select:eq('+i+')').find(":selected").val();
		var _val = 0;
		if (_id != 0) { 
			_val = $('#'+domId+' tbody tr td select:eq('+i+')').find(":selected").text();
		}
		try {
			if (_id != "" && _val != "") {
				var $div = $("div[name='parentWebixContainer'][id='webixAnalyticalSelfTest']");
				var tableID = $($div.find(".webix_container")).attr('childTableID');
		
				var grid = $$(tableID);
				if (grid !=  undefined) {
					var _curColConfig = grid.getColumnConfig(columnName);
			
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
				}
			}
		} catch (e){
			console.log("webix - updateWebixElementList error");
		}
	}
}

/*
 * @webixId - webixAnalyticalSelfTest
 */

function updateWebixElementListByAjaxCall(webixId, columnName, dataList) { //new 29122019
	var data = JSON.parse(dataList.listdata);
	try {
		var $div = $("div[name='parentWebixContainer'][id=" + webixId + "]");
		var tableID = $($div.find(".webix_container")).attr('childTableID');

		var grid = $$(tableID);
		if (grid !=  undefined) {
			var _curColConfig = grid.getColumnConfig(columnName);
			var arr = [];// = _curColConfig.collection.config.data;
			
			for (var i in data) 
			{
				var _id = data[i].id;
				var _val = data[i].value;
			
//						if (!$.isEmptyObject(arr) && arr.length == 1 && arr[0].value == "") {
				if (!$.isEmptyObject(_curColConfig.collection.config.data) && _curColConfig.collection.config.data.length == 1 && _curColConfig.collection.config.data[0].value == "") {
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
			}
			
//					_curColConfig.collection.config.data = arr;
//					grid.refresh();
		}
	} catch (e){
		console.log("webix - updateWebixElementList error");
	}
	
}