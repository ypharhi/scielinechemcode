var ElementWebixFormulCalcImp = {
		value_: function (val_, changeType) 
	    {
	    	var domId = $(val_).attr('id');
	        var elementID = $(val_).attr('elementID');
	        var data_ = {};
	        if(changeType == 2)
	        {
		        var object = getOutputDataFormul(domId);
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

function initElementDataTableWebixFormul(domId, tableID, tableDivID, data, isNew, isDisabled)
{    
	//console.log("data for table: " + domId);
	//console.log(data);
	isDisabled = false; //yp 10032019 - fix patch for version 1.428.3 - general prod - slow analytical experiment load
	
    var tableData = {};
    if(isNew == "1")
	{
    	if($('#isNew').val()=='1')
    		isDisabled = false;
    	tableData = prepareDataToWebixFormul(domId, tableID, data);
	}
    else
	{   	
    	tableData = JSON.parse(data);
    	//console.log("tableID: " + tableID);
    	//console.log(tableData);
	}	
    
	webix.ready(function()
	{
		webix.ui.datafilter.massSumColumn = webix.extend({
			refresh:function(master, node, value){ 
		        var result = 0;
		        var _origval;
		        master.mapCells(null, value.columnId, null, 1, function(value)
		        {
		        	_origval = value;
		        	value = value*1;
		            if (!isNaN(value) && _origval != '')
		                result += parseFloat(value);
		            else
		            	value = '';
		            return value;
		        });
		        //result = result.toFixed(_webixFormulPrecision);
		        result = parseFloat(Math.round(result * 1000) / 1000);
		        var density_val = $('#density_' + domId).val();	
		        //console.log(node);
		        if((density_val != null && $.trim(density_val) != "") && result != 0 && result != (parseFloat($.trim(density_val)) * 1000))
		        {
		        	node.setAttribute("style","background-color:red");
		        	node.setAttribute("title","Mass(in 1000 ml) is not equal to Density * 1000");
		        }
		        else
		        {
		        	node.setAttribute("style","background-color:#fafafa");
		        	node.removeAttribute("title");
		        }
		        node.firstChild.innerHTML = result;
		    }
		}, webix.ui.datafilter.summColumn);
		
		webix.ui({
			container:tableDivID,
			view:"datatable",
			id: tableID,
			columns:[
				{ id:"material_name", 	header:"Material Name" ,width:170, footer:"Total", sort:"string"},
				{ id:"item_id",	header:"Item ID" , width:100, sort:"string"},
				{ id:"material_type",	header:"Material type" , width:190, sort:"string"},
				{ id:"ai",	header:"AI", editor:"select", options:["yes", "no"], sort:"string", width:60},
				{ id:"batch_name",	header:"Batch" , width:170, sort:"string"},
				{ id:"purity",	header:"Purity (%)" , width:100, sort:"string"},
				{ id:"aconc",	header:"Aimed Concentration (g/L)" , width:210, sort:"int", editor:"text", cellType:"decimal"},
				{ id:"ww",	header:"%W/W", sort:"int", 	width:100, footer:{content:'summColumn'}, format:webix.Number.numToStr({
				    groupDelimiter:" ",
					groupSize:0,
					decimalDelimiter:".",
					decimalSize:_webixFormulPrecision
				})},
                { id:"mass", header:"Mass (in 1000ml) (g)", width:165, sort:"int", footer:{content:'massSumColumn'}, format:webix.Number.numToStr({
				    groupDelimiter:" ",
					groupSize:0,
					decimalDelimiter:".",
					decimalSize:_webixFormulPrecision
				})},
                { id:"mass_final",	header:"Mass (in final volume, L) (g)", width:210, sort:"int", footer:{content:'summColumn'}, format:webix.Number.numToStr({
				    groupDelimiter:" ",
					groupSize:0,
					decimalDelimiter:".",
					decimalSize:_webixFormulPrecision
				})},
                { id:"solidL",	header:"Solid", editor:"select", options:["yes", "no"], sort:"string", width:60}
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
			on:{	
				onAfterLoad:function()
				{
					//console.log("ON AFTER LOAD");
					calcFormul(domId, tableID);
					if($('#formCode').val() == 'ExperimentFor' && domId == 'webixFormulCalc') { // workaround for bug when download the label after creating samples and back to the experiment -> because of the download we not getting to webix.ui({ code and the table is not displayed in the screen (without error in the log)
						//print label on load if we have data in _labelCode / _labelData (passed from previous form)
						if ( $('#_global_labelCode').length > 0 && $('#_global_labelCode').val() != "" && $('#_global_labelCode').val() != 'undefined' && $('#_global_labelData').length > 0 && $('#_global_labelData').val() != "" && $('#_global_labelData').val() != 'undefined')
						{
							outPutLabel("_global",$('#_global_labelCode').val(),$('#_global_labelData').val());
						}
					}
				}
			},
			ready:function()
			{				 
				this.attachEvent("onEditorChange", function(id, value)
				{
					console.log("ON EDITOR CHANGE");
					this.getItem(id.row)[id.column] = value;
				    this.refresh(id.row);
				    calcFormul(domId, tableID);
				});
				this.attachEvent("onKeyPress", function(code, e){
					console.log("ON KEY PRESS");
					//console.log(code);
					if(this.getEditor() != undefined && this.getEditor() != "0" && this.getEditor().column == "aconc")
						validateDecimalWX(code, e);
				});
			}
		});		
	});
}


function prepareDataToWebixFormul(domId, tableID, data)
{
	var obj = JSON.parse(data);
	var objToReturn = [];	
	var idcounter = 1;
	
	$(obj).map(function () 
    {
		objToReturn.push({
			id: idcounter++, // unique id for each row
			material_id: this.material_id,
			material_name:this.material_name,
			batch_name:this.batch_name,
			ai:this.ai,
			ww:this.ww,
			mass:this.mass,
			mass_final:this.mass_final,
			solidL:this.solid_list,
			batch_id:this.batch_id,
			purity:this.purity,
			item_id:this.itemid,
			material_type:this.materialtypename,
			aconc:this.aconc
    	});
    }); 
	//console.log("objToReturn:");
	//console.log(objToReturn);
	
	return objToReturn;
}

function calcFormul(domId, tableID)
{
	console.log("CALCULATE for tableid: " + tableID);
	if(tableID != null && tableID != "")
	{
		
		var F = $('#factor_' + domId).val();
		var D = $('#density_' + domId).val();
		var mf = "";
		var ww = "";
		var mfSum = 0;
		var msolidSum = 0;
		var msum = 0;
		var _mass = "";
		
		var grid = $$(tableID);			
		grid.eachRow( 
			    function (row)
			    { 
			    	mf = "";
			    	_mass = "";
			    	ww = "";
			    	var rowObj = grid.getItem(row);
			        //console.log(rowObj);
			    	if(rowObj.aconc != null)
			    	{			    		
			    		//console.log(isNaN(rowObj.aconc));
			    		if(isNaN(rowObj.aconc))
			    		{
			    			rowObj["aconc"] = "";
			    		}
			    		
			    		_mass = (rowObj.aconc != "")?divideInFormul(rowObj.aconc, rowObj.purity/100):"";
			    		rowObj["mass"] = _mass;
			    	}
			        if(rowObj.mass != null)
			        {
				        mf = (rowObj.mass != "" && F != "")?rowObj.mass * F : "";
				        rowObj["mass_final"] = mf;
				        
				        if(mf != "")
				        {
				        	mfSum += parseFloat(mf);
				        }
				       //----------------------------------------------------
				        var solid = rowObj.solidL;
				        if(rowObj.mass != "")
				        {
				        	msum += parseFloat(rowObj.mass);
				        	
				        	if(solid !=  null && solid.toLowerCase() == 'yes')
					        {
					        	msolidSum += parseFloat(rowObj.mass);
					        }
				        }
				        //---------------------------------------------------
				        ww = (rowObj.mass != "" && D != "")?divideInFormul(rowObj.mass, D*10):"";
				        rowObj["ww"] = (ww != "")?parseFloat(ww):"";
				        
			        }
			    }
			)
		
		/*grid.eachRow( 
			    function (row)
			    { 
			    	ww = "";
			    	var rowObj = grid.getItem(row);
			        //console.log(rowObj);
			    	
			    	ww = (rowObj.mass_final != null && rowObj.mass_final !="")?divideInFormul(rowObj.mass_final, mfSum):"";		        
			        rowObj["ww"] = (ww != "")?parseFloat(ww)*100:"";
			    }
			)*/
		
		var val = divideInFormul(msolidSum, msum);
		var valToDspl = "";
		if(val != "")
		{			
			val = parseFloat(val)*100;
			//valToDspl = val.toFixed(_webixFormulPrecision);
			valToDspl = parseFloat(Math.round(val * 1000) / 1000);
		}
		else
		{
			val = "";
			valToDspl = "";
		}
		$('#solid_' + domId).val(valToDspl);
		$('#solid_' + domId).attr('originalValue', val);
		
		//markFooterCell(tableID, domId, msum);
		grid.refresh();
	}
}

function divideInFormul(a,b)
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

function calcAllFormul(domID)
{
	var standardMeanRF = "";
	var $div = $("div[name='parentWebixContainer'][id='"+domID+"']");
	var tableID = $($div.find(".webix_container")).attr('childTableID');
	calcFormul(domID, tableID);
	
	/*$div.find(".webix_container").each(function()
	{
		var tableID = $(this).attr('childTableID');
		calc(domID, tableID);
	});*/
}


function getOutputDataFormul(domId)
{	
	// calculate all before save
	calcAllFormul(domId);
	
	var tablesSnapshotObj = [];
	var returnObj = {};
	var outputArr = [];
	var fullOutputArr = [];
	var isTableHasRows = 0;
	
	var $div = $("div[name='parentWebixContainer'][id='"+domId+"']");
	var tableID = $($div.find(".webix_container")).attr('childTableID');
	var _factor = $('#factor_' + domId).val();
	var _density = $('#density_' + domId).val();
	var solidOrig = $('#solid_' + domId).attr('originalValue');
	var solidDisplay = $('#solid_' + domId).val();
	
	var grid = 	$$(tableID);
	var tableData = grid.serialize();
	
	if(tableData.length > 0)
	{		
		isTableHasRows = 1;
		tablesSnapshotObj = {data:tableData, factor: _factor, solid: solidOrig, density: _density, solidToDisplay:solidDisplay};
			
		for(var j=0;j<tableData.length;j++)
		{
			outputArr.push({
				"batch_id":tableData[j].batch_id,
				"material_id":tableData[j].material_id,
				"mass_in_final":tableData[j].mass_final,
				"mass":tableData[j].mass
			});
		}
		fullOutputArr = {rowsData:outputArr, totalMassFinal:grid.getColumnConfig("mass_final").footer[0].value};
	}	
	returnObj = {isWebixTableHasRows:isTableHasRows, tablesSnapshot:tablesSnapshotObj, output: fullOutputArr};	
	console.log("returnObj: ");
	console.log(JSON.stringify(returnObj));
	
	return returnObj;
}

function uploadFormulationData(domId)
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
        url: "./onElementWebixFormulCalcUpload.request",
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
                console.log("UPLOAD calc table:");                
                console.log(o);
                $('#solid_' + domId).val('');
                $('#solid_' + domId).attr('originalValue','');
                
                var fieldsData = JSON.parse(o.tableData);
                console.log(fieldsData);
                if(fieldsData.length > 0)
                {
	                $('#factor_' + domId).val(fieldsData[0].factor);
	                $('#density_' + domId).val(fieldsData[0].density);
                }
                var grid = $$(o.tableID);
                if (grid)
    				grid.destructor();
                	                
                var $div = $("div[name='parentWebixContainer'][id='"+domId+"']");
            	$div.empty();		
            	$div.append(o.tableDiv);
            	initElementDataTableWebixFormul(domId, o.tableID, o.tableDivID, o.tableData, 1, false);                
            }
            hideWaitMessage();
        },
        error: handleAjaxError
    });
}

function upDateElementFormulWebixExpStepImp(obj)
{
	console.log("upDateElementFormulWebixExpStepImp obj: ");
	console.log(obj);
	if(obj.val != "")
	{
		var wholeData = JSON.parse(obj.val);
		console.log(wholeData);	
		var tableData = [];
		if(wholeData.data != null) {
			tableData = wholeData.data;
		} 
		
		$('#solid_' + obj.domId).val(wholeData.solidToDisplay);
		$('#solid_' + obj.domId).attr('originalValue',wholeData.solid);
		$('#factor_' + obj.domId).val(wholeData.factor);
        $('#density_' + obj.domId).val(wholeData.density);
		
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
				$($div.find("input")).prop("disabled", false);
				isTableDisabled = false;		
		    }
		    else
		    {
		    	$($div.find("button")).prop("disabled", true);
		    	$($div.find("input")).prop("disabled", true);
		    	isTableDisabled = true;	
		    }
		}
		if (grid)
			grid.destructor();
		 	
		initElementDataTableWebixFormul(obj.domId, tableID, tableDivID, JSON.stringify(tableData), 0, isTableDisabled);
	}
}
