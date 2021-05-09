var ElementWebixExperimentStepCalcImp = {
		value_: function (val_, changeType) 
	    {
	    	var domId = $(val_).attr('id');
	        var elementID = $(val_).attr('elementID');
	        var data_ = {};
	        if(changeType == 2)
	        {	    	
		        var object = getOutputDataExpStep(domId);
		        data_ = {
		        	"isTableHasRows":object.isWebixTableHasRows,
		            "objToSaveAsClob":object.tablesSnapshot,
		            "output":object.output,
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

var _webixFormulPrecision = 3;
function initElementDataTableWebixExpStep(domId, tableID, tableDivID, data, isTableNew, isDisabled)
{    
	//console.log("data for table: " + domId + " isDisabled: " + isDisabled);
	//console.log(data);
	isDisabled = false; //yp 10032019 - fix patch for version 1.428.3 - adama prod - slow analytical experiment load
	
    var tableData = JSON.parse(data);
    if(isTableNew == "1")
	{
    	if($('#isNew').val()=='1')
    		isDisabled = false;
    	tableData = prepareDataToWebixExpStep(domId, tableID, tableData);
	}	
    
	webix.ready(function()
	{
		webix.ui({
			container:tableDivID,
			view:"datatable",
			id: tableID,
			columns:[
				{ id:"material_name", 	header:"Material Name" ,width:200, footer:"Total"},
				{ id:"batch_name",	header:"Batch" , width:200},
				{ id:"ai",	header:"AI", editor:"select", options:["yes", "no"], icon:"caret-down",},
				{ id:"ww",	header:"%W/W", sort:"int", 	width:100, footer:{content:'summColumn'},
				 format:webix.Number.numToStr({
				    groupDelimiter:" ",
					groupSize:0,
					decimalDelimiter:".",
					decimalSize:_webixFormulPrecision
				})
				},
                { id:"mass_final",	header:"Mass(in final volume), gr", editor:"text", width:190, sort:"int", cssFormat:setBorder, footer:{content:'summColumn'}, 
					format:webix.Number.numToStr({
					    groupDelimiter:" ",
						groupSize:0,
						decimalDelimiter:".",
						decimalSize:_webixFormulPrecision
					})	
                },
                { id:"solidL",	header:"Solid", editor:"select", options:["yes", "no"]}
            ],
            navigation:true,
            select:"cell",
			autoheight:true,
			autowidth:true,
			editable:true,
			math: true,
			tooltip:true,
			footer:true,
			disabled:isDisabled,
			data:tableData,			
			ready:function()
			{				 
				this.attachEvent("onEditorChange", function(id, value)
				{
					//console.log("ON EDITOR CHANGE");
					//console.log();
					this.getItem(id.row)[id.column] = value;
				    this.refresh(id.row);
				    calcExpStep(domId, tableID);
				});
				this.attachEvent("onKeyPress", function(code, e){
				    //console.log(code);
				    //console.log(e);
					if(this.getEditor() != undefined && this.getEditor() != "0" && this.getEditor().column == "mass_final")
						validateDecimalWX(code, e);
				});
			}
		});
	});
}


function prepareDataToWebixExpStep(domId, tableID, tableData)
{
	var objToReturn = [];	
	var idcounter = 1;
	
	$(tableData).map(function () 
    {
		objToReturn.push({
			id: idcounter++, // unique id for each row
			material_id: this.material_id,
			material_name:this.material_name,
			batch_name:this.batch_name,
			ai:this.ai,
			ww:this.ww,
			mass_final:this.mass_final,
			solidL:this.solid_list,
			quantity:this.inv_quantity,
			batch_id:this.batch_id
    	});
    }); 
	//console.log("objToReturn:");
	//console.log(objToReturn);
	
	return objToReturn;
}

function setBorder(value, config)
{
	console.log("quantity = " + config.quantity);
	console.log("value = " + value);
	if (parseFloat(value) > parseFloat(config.quantity))
        return { "border-style":"solid !important","border-color":"#f99e9e !important","border-width":"2px !important"};
    else
    	return {};
    return value;
}

function calcExpStep(domId, tableID)
{
	console.log("CALCULATE for tableid: " + tableID);
	if(tableID != null && tableID != "")
	{
				
		var mf = "";
		var ww = "";
		var mfSum = 0;
		var msolidSum = 0;
		var msum = 0;
		
		var grid = $$(tableID);			
		grid.eachRow( 
			    function (row)
			    { 
			    	mf = "";
			    	var rowObj = grid.getItem(row);
			        //console.log(rowObj);
			        if(rowObj.mass_final != null)
			        {
				        mf = rowObj.mass_final;
				        if(mf != "")
				        {
				        	mfSum += parseFloat(mf);
				        	var solid = rowObj.solidL;
				        	if(solid !=  null && solid.toLowerCase() == 'yes')
					        {
					        	msolidSum += parseFloat(mf);
					        }
				         }
			        }
			    });
		
		grid.eachRow( 
			    function (row)
			    { 
			    	ww = "";
			    	var rowObj = grid.getItem(row);
			        //console.log(rowObj);
			    	
			    	ww = (rowObj.mass_final != null && rowObj.mass_final !="")?divideInExpStep(rowObj.mass_final, mfSum):"";		        
			        rowObj["ww"] = (ww != "")?parseFloat(ww)*100:"";
			    });
		
		//console.log("msolidSum = " + msolidSum + " |mfSum = " + mfSum);
		var val = divideInExpStep(msolidSum, mfSum);
		val = (val != "")?parseFloat(val)*100:"";
		$('#solid_' + domId).val(val);
		
		grid.refresh();
	}
}

function divideInExpStep(a,b)
{
	return divide(a,b,true);
}

function divide(a,b, isDecimal)
{	
	var result = "";
	a = $.trim(a);
	b = $.trim(b);
	if(a != "" && b != "")
	{
		if(validateNumeric(a, isDecimal, false) && validateNumeric(b, isDecimal, false))
		{			
			a = parseFloat(a);
			b = parseFloat(b);
			if(a == 0)
			{
				result = "0";
			}
			else if(b == 0)
			{
				//result = "NA";
				result = "";
			}
			else
			{
				result = a/b;
			}
		}
	}
	//console.log("a: |" + a + "| b: |" + b + "| result = " + result);
	return result;
}

function calcAllExpStep(domID)
{
	var standardMeanRF = "";
	var $div = $("div[name='parentWebixContainer'][id='"+domID+"']");
	var tableID = $($div.find(".webix_container")).attr('childTableID');
	calcExpStep(domID, tableID);
	
	/*$div.find(".webix_container").each(function()
	{
		var tableID = $(this).attr('childTableID');
		calc(domID, tableID);
	});*/
}

function upDateElementWebixExpStepImp(obj)
{
	console.log("upDateElementWebixExpStepImp obj: ");
	console.log(obj);
	if(obj.val != "")
	{
		var wholeData = JSON.parse(obj.val);
		console.log(wholeData);	
		var tableData = wholeData.data;
		var solidData = wholeData.solid;
		$('#solid_' + obj.domId).val(solidData);
		var $div = $("div[id='"+obj.domId+"_Parent']");
		var wc = $($div.find(".webix_container"));
		var tableID = wc.attr('childTableID');
		var tableDivID = wc.attr('id');
		var grid = $$(tableID);
		var isTableDisabled = false;
	
		if (typeof obj.isDisabled !== 'undefined') 
		{
			if (obj.isDisabled.toLowerCase() == "false") 
		    {			
				$($div.find("button")).prop("disabled", false);
				isTableDisabled = false;		
		    }
		    else
		    {
		    	$($div.find("button")).prop("disabled", true);
		    	isTableDisabled = true;	
		    }
		}
		if (grid)
			grid.destructor();
		
		initElementDataTableWebixExpStep(obj.domId, tableID, tableDivID, JSON.stringify(tableData), 0, isTableDisabled);  	
	}
}


function getOutputDataExpStep(domId)
{	
	// calculate all before save
	calcAllExpStep(domId);
	
	var tablesSnapshotObj = [];
	var returnObj = {};
	var outputArr = [];
	var fullOutputArr = [];
	var isTableHasRows = 0;
	
	var $div = $("div[name='parentWebixContainer'][id='"+domId+"']");
	var tableID = $($div.find(".webix_container")).attr('childTableID');
	var S = $('#solid_' + domId).val();
	
	var grid = 	$$(tableID);
	var tableData = grid.serialize();		
	
	if(tableData.length > 0)
	{
		isTableHasRows = 1;
		tablesSnapshotObj = {data:tableData, solid: S};
		
		for(var j=0;j<tableData.length;j++)
		{
			outputArr.push({
				"batch_id":tableData[j].batch_id,
				"material_id":tableData[j].material_id,
				"mass_in_final":tableData[j].mass_final
			});
		}
		fullOutputArr = {rowsData:outputArr, totalMassFinal:grid.getColumnConfig("mass_final").footer[0].value};
	}
	returnObj = {isWebixTableHasRows:isTableHasRows, tablesSnapshot:tablesSnapshotObj, output: fullOutputArr};
	
	console.log("returnObj: ");
	console.log(JSON.stringify(returnObj));
	
	return returnObj;
}

function uploadExpStepData(domId)
{
	showWaitMessage(getSpringMessage('pleaseWait'));
    // ajax call to the api service
   
    var data_ = {
	              "formId": $('#formId').val(),
	              "domId": domId
	        	};
    $.ajax({
        type: 'POST',
        data: data_,
        url: "./onElementWebixExpStepCalcUpload.request",
        //contentType: 'application/json',
        dataType: 'json',
        success: function (obj) 
        {      
            
            if (obj.errorMsg != null && obj.errorMsg != '') {
                displayAlertDialog(obj.errorMsg);
            } else if (obj.data[0].val == "-1") {
                displayAlertDialog("Error");
            } else 
            {
            	showWaitMessage(getSpringMessage('pleaseWait'));
                var o = JSON.parse(obj.data[0].val);
                console.log("upload calc table:");
                console.log(o);
                
            	$('#solid_' + domId).val("");
            	
                var grid = $$(o.tableID);
                if (grid)
    				grid.destructor();
                	                
                var $div = $("div[name='parentWebixContainer'][id='"+domId+"']");
            	$div.empty();		
            	$div.append(o.tableDiv);
            	initElementDataTableWebixExpStep(domId, o.tableID, o.tableDivID, o.tableData, 1, false);                
            }
            hideWaitMessage();
        },
        error: handleAjaxError
    });
}