var ElementWebixAnalytCalcImp = {
		value_: function (val_, changeType) 
	    {
	    	var domId = $(val_).attr('id');
	        var elementID = $(val_).attr('elementID');
	        var data_ = {};
	        if(changeType == 2)
	        {	    	
		        var object = getOutputDataAnalyt(domId);
		        data_ = {
		            //"elementId": elementID,
		            "isTableHasRows":object.isWebixTableHasRows,
		            "objToSaveAsClob":object.tablesSnapshot,
		            "resultValue": object.resultOutput,
		            "parentID": $('#formId').val(),
		            "output":object.tablesSnapshot
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

var tableIDArr = [];
var allTablesSpanArr = [];
var NO_PURITY_MARKER = false; // marks if there is no purity exists for current batch

function initElementDataTableWebixAnalyt(domId, tableID, tableDivID, data, isNew, isDisabled)
{    
    console.log("initElementDataTableWebixAnalyt(): isNew: " + isNew);
	console.log(data);
	isDisabled = false; //yp 10032019 - fix patch for version 1.428.3 - adama prod - slow analytical experiment load
	
	tableIDArr.push(tableID);
    var tableData = {};
    if(isNew == "1")//NEW
	{
    	if($('#isNew').val()=='1')
    	{
    		isDisabled = false;
    	}
    	tableData = prepareDataToWebixAnalyt(tableID, data);
	}
    else if(isNew == "2")//UPLOAD
	{    	
    	tableData = prepareDataToWebixAnalyt(tableID, data);
	}
    else //EDIT
	{   	
    	tableData = funcParseJSONDataWX(data, true);
    	allTablesSpanArr[tableID] = tableData.spans;
    	//console.log("tableID: " + tableID);
    	//console.log(tableData.spans);
	}	
    
	webix.ready(function(){
		webix.ui({
			container:tableDivID,
			view:"datatable",
			id: tableID,
			columns:[
						{ id:"num",	header:"No", width:50},
						{ id:"type", 	header:"Type" ,width:80},
						{ id:"batchname",	header:{text:"Batch/Sample Number",css:"overflow-wrap: break-word"} , width:180, tooltip: "#batchname#"},
						{ id:"weighting",	header:"Weighting"},
						{ id:"ch1", header:"Include", template:"{common.checkbox()}", width:70},
				        { id:"weight",	header:"Weight(mg)", width:100, /*sort:"int",*/ editor:"text", cellType:"decimal"},
				        { id:"volume",	header:"Volume(ml)", width:100, /*sort:"int",*/ editor:"text", cellType:"decimal"},
				        { id:"conc",	header:"Conc.(mg/ml)", width:120, /*sort:"int",*/ /*math:"[$r,:5] / [$r,volume]",*/ format:webix.Number.numToStr({
						    groupDelimiter:" ",
							groupSize:0,
							decimalDelimiter:".",
							decimalSize:2
						})},
				        { id:"dilution", header:"Dilution", width:70, editor:"text", cellType:"decimal"},
				        { id:"area",	header:"Area(cm^2)", /*sort:"int",*/ 	width:100, editor:"text", cellType:"decimal"},
				        { id:"rf",	header:"RF", 	width:90, format:webix.Number.numToStr({
						    groupDelimiter:" ",
							groupSize:0,
							decimalDelimiter:".",
							decimalSize:3
						})
						}, 
						{ id:"ww",	header:"%W/W", /*sort:"int",*/ 	width:90,
							format:webix.Number.numToStr({
							    groupDelimiter:" ",
								groupSize:0,
								decimalDelimiter:".",
								decimalSize:3
							})
					},
			        { id:"meanrf",	header:"Mean RF", 
			        	format:webix.Number.numToStr({
					    groupDelimiter:" ",
						groupSize:0,
						decimalDelimiter:".",
						decimalSize:3
					})
			        },
			        { id:"result",	header:"Result(%)", format:webix.Number.numToStr({
					    groupDelimiter:" ",
						groupSize:0,
						decimalDelimiter:".",
						decimalSize:3
					})},
			        { id:"std",	header:"Std", 	width:90, format:webix.Number.numToStr({
					    groupDelimiter:" ",
						groupSize:0,
						decimalDelimiter:".",
						decimalSize:3
					})},
			        { id:"rsd",	header:"RSD(%)", 	width:90, 
			        	format:webix.Number.numToStr({
						    groupDelimiter:" ",
							groupSize:0,
							decimalDelimiter:".",
							decimalSize:3
					})},                
			        { id:"retention",	header:"Retention Time(min)", editor:"text", cellType:"decimal"},
			        { id:"comment",	header:"Comments", 	editor:"popup", width:200}
				],
			navigation:true,
            spans:true,
            select:"cell",
			autoheight:true,
			autowidth:true,
			editable:true,
			math: true,
			tooltip:true,
			disabled:isDisabled,
			data:tableData,
			on:{
				onAfterLoad:function() //23022020 kd added as workaround for fix bug-7963. And 
				{
					console.log("ON AFTER LOAD ElementWebixAnalytCalcImp");	
					console.log(this.config.columns[1]);
					try { 
						if($('#formCode').val() == 'ExperimentAn' && domId == 'webixAnalytTable') { // workaround for bug when download the label after creating samples and back to the self test -> because of the sownload we not getting to webix.ui({ code and the table is not displayed in the screen (without error in the log)
							//print label on load if we have data in _labelCode / _labelData (passed from previous form)
							if ( $('#_global_labelCode').length > 0 && $('#_global_labelCode').val() != "" && $('#_global_labelCode').val() != 'undefined' && $('#_global_labelData').length > 0 && $('#_global_labelData').val() != "" && $('#_global_labelData').val() != 'undefined')
							{
								outPutLabel("_global",$('#_global_labelCode').val(),$('#_global_labelData').val());
							}
						}
					} catch (e) {
							console.log("webix analyt - ON AFTER LOAD error");
					}
				}
			},
			ready:function()
			{
				this.attachEvent("onEditorChange", function(id, value)
				{
					console.log("ON EDITOR CHANGE");
					//console.log(id);
					//this.getItem(id.row)[id.column] = value;
				    //this.refresh(id.row);
				    if( id == "1"
				    	&&(this.getEditor() != undefined && this.getEditor() != "0" 
				    	&& this.getEditor().column == "retention")
				    )
				    {
				    	updateColumnData(tableID, value);
				    }
				});
				this.attachEvent("onKeyPress", function(code, e){
					console.log("ON KEY PRESS");
					if(this.getEditor() != undefined && this.getEditor() != "0")
					{
						if(this.getEditor().column != "comment")
						{
							validateDecimalWX(code, e);
						}
						else
						{
							validateLegalCharWX(code, e);
						}
					}
				});
			}
		});				
	});
}

function prepareDataToWebixAnalyt(tableID, data)
{
	var obj = funcParseJSONDataWX(data, false);
	var objToReturn = {};
	var dataLength = obj.length;
	
	if(dataLength > 0)
	{
		var dataArr = [];
		var spanArrFull = [];
		var standardObj = [];
		var lastStandardObj = [];
		var idcounter = 1; //used to set ID for first standard rows
		var lastIdcounter; //used to set ID for last standard rows 
		var standardRowsTotal, 
			standardRowsHalfOfTotal,
		    standardRowsConfig,
		    lastNum; // used to define NUM for last standard rows
		var hasStandards = false;
		var spanHeightArr = [];
		var spanHeightCounter = 0;
		var lastGroupNum = 0;
		var maxWeighting = 0;
		var lastStandardRowsCounter = 1; //used to define last standard rows WEIGHTING (rows group sub numbers)
		var standardRowsCounter;//used to define first standard rows WEIGHTING (rows group sub numbers)
		var samplesRowsLength;
		
		$(obj).map(function (crntIndex) 
	    {
	        var $this = this;
			if($this.type == "Standard")
	    	{
	        	hasStandards = true;
	        	standardRowsConfig = parseInt($this.standard_rows_configuration);//$this.standard_rows_configuration;
	        	standardRowsTotal = parseInt($this.standard_rows_total);//$this.standard_rows_total;
	        	standardRowsHalfOfTotal = standardRowsTotal/2;
	        	lastNum = parseInt($this.max_num)//$this.max_num;
	        	
	        	//to define IDs of last standard rows need to find out length of samples rows
	        	samplesRowsLength = dataLength - standardRowsTotal;
	        	
	        	if(standardRowsTotal == 1) //NEW STANDARD, not saved yet
	        	{	        		
	        		lastIdcounter = samplesRowsLength + standardRowsConfig;
	        		for(var i=1;i<=standardRowsConfig;i++)
		    		{	        			    			
		        		standardObj.push(fillTmpObjectWebixAnalyt(idcounter, $this, $this.num, i));
		        		lastStandardObj.push(fillTmpObjectWebixAnalyt((lastIdcounter + idcounter), $this, (lastNum + 1), i));		  
		        		idcounter++;
		    		}
	        	}
	        	else
	        	{
	        		standardRowsCounter = crntIndex + 1;
	        		lastIdcounter = samplesRowsLength + standardRowsCounter;
	        		if(standardRowsConfig == standardRowsHalfOfTotal)// if configured same as saved
	        		{
		        			if(crntIndex < standardRowsConfig)
		        			{
		        				standardObj.push(fillTmpObjectWebixAnalyt(idcounter++, $this, $this.num, standardRowsCounter));
		        			}
		        			else
		        			{
		        				lastStandardObj.push(fillTmpObjectWebixAnalyt(lastIdcounter, $this, (lastNum + 1), lastStandardRowsCounter++));		  
		        			}
	        		}
	        		else if(standardRowsConfig > standardRowsHalfOfTotal)//if configured more than saved
	        		{
		        			var missingRows = standardRowsConfig - standardRowsHalfOfTotal;
		        			if(crntIndex < standardRowsHalfOfTotal)
		        			{
		        				standardObj.push(fillTmpObjectWebixAnalyt(idcounter++, $this, $this.num, standardRowsCounter));
		        				//if configured more than saved, we need to add default rows to standards till count of configured rows will be equal to count of displayed rows
		        				if((crntIndex+1) == standardRowsHalfOfTotal)
		        				{
		        					for(var i=1;i <= missingRows;i++)
		        					{
		        						standardObj.push(fillTmpObjWithDefaultDataWebixAnalyt(idcounter++, $this, $this.num, (standardRowsCounter+i)));
		        					}
		        				}
		        			}
		        			else
		        			{
		        				lastIdcounter = lastIdcounter + missingRows;
		        				lastStandardObj.push(fillTmpObjectWebixAnalyt(lastIdcounter, $this, (lastNum + 1), lastStandardRowsCounter++));	
		        				if((crntIndex+1)/2 == standardRowsHalfOfTotal)
		        				{
		        					for(var i=1;i <= missingRows;i++)
		        					{
		        						lastStandardObj.push(fillTmpObjWithDefaultDataWebixAnalyt((lastIdcounter + i), $this, (lastNum + 1), lastStandardRowsCounter++));
		        					}
		        				}
		        			}
	        		}
	        		else if(standardRowsConfig < standardRowsHalfOfTotal)//if configured less than saved
	        		{
	        			if(crntIndex < standardRowsHalfOfTotal)
	        			{
		        			if(crntIndex < standardRowsConfig)// display only configured count of rows
		        			{
		        				standardObj.push(fillTmpObjectWebixAnalyt(idcounter++, $this, $this.num, standardRowsCounter));
		        			}
	        			}
	        			else
	        			{
	        				if(lastStandardRowsCounter <= standardRowsConfig)// display only configured number of rows
	        				{
	        					var extraRows = standardRowsHalfOfTotal - standardRowsConfig;
	        					lastStandardObj.push(fillTmpObjectWebixAnalyt((lastIdcounter - extraRows), $this, (lastNum + 1), lastStandardRowsCounter++));
	        				}
	        			}
	        		}
	        	}
	        	       	
	    	}
	        else // SAMPLE
	    	{
	        	if(lastGroupNum != 0)
	    		{       		
	        		if(lastGroupNum != $this.num)
	    			{
	        			spanHeightArr[spanHeightCounter++] = parseInt(maxWeighting);
	        			lastGroupNum = $this.num;
	        			maxWeighting = $this.weighting;
	    			}
	        		else
	    			{
	        			maxWeighting = $this.weighting;
	    			}
	    		}
	        	else // there is the first SAMPLE after STANDARDS
	    		{
	        		if(hasStandards)
	        		{
	        			spanHeightArr[spanHeightCounter++] = standardRowsConfig;
	        			dataArr = standardObj;
	        		}
		        	
	        		lastGroupNum = $this.num;
	        		maxWeighting = $this.weighting;
	    		}
	        	
	        	dataArr.push(fillTmpObjectWebixAnalyt(idcounter++, $this, $this.num, $this.weighting));	        	
	    	}
	    }); //END OBJECT MAP
		
		if(dataArr.length > 0) 
		{
			spanHeightArr[spanHeightCounter++] = parseInt(maxWeighting);
		}
		else // in case not even one sample is defined
		{
			if(hasStandards)
    		{
    			spanHeightArr[spanHeightCounter++] = standardRowsConfig;
    			dataArr = standardObj;
    		}
		}
		
		if(hasStandards)// the last STANDARD
		{
			for(var i=0;i < lastStandardObj.length;i++)
			{
				dataArr.push(lastStandardObj[i]);
			}
			spanHeightArr[spanHeightCounter++] = standardRowsConfig;
		}
		console.log("---------spanHeightArr:--------------");
		console.log(spanHeightArr);
		console.log("---------dataArr:--------------");
		console.log(dataArr);

		/* 
		 	TABLE SPAN BUILD 
		 */
		var spanStartFrom = 1;
		var rowid = 0;
		var spanArrFullCounter = 0;
		var spanColArr = ["num","type","batchname","meanrf","result","std","rsd","comment"];
		for(var i=0;i<spanHeightArr.length;i++)
		{
			if(rowid == 0)
			{
				rowid = spanStartFrom;
			}
			else
			{
				rowid = parseInt(rowid) + parseInt(spanHeightArr[i-1]);
			}
			for(var j=0;j<spanColArr.length;j++)
			{
				var tmp = [];
				tmp[0] = rowid;						//id
				tmp[1] = spanColArr[j]; 			//column
				tmp[2] = 1; 						//width
				tmp[3] = parseInt(spanHeightArr[i]);//height
				tmp[4] = "";						//value
				tmp[5] = "center";					//css
				
				spanArrFull[spanArrFullCounter++] = tmp;
			}
		}
		//fill global array with span data
		allTablesSpanArr[tableID] = spanArrFull;
		objToReturn = {data:dataArr, spans:spanArrFull};
	}
	else
	{
		objToReturn = {data:[], spans:[]};
	}
	console.log("WebixAnalytical objToReturn:");
	console.log(objToReturn);
	
	return objToReturn;
}

function fillTmpObjectWebixAnalyt(rowid, data, rowGrNumber, rowGrSubNumer)
{
	var _tmpObj = {};
	_tmpObj = {
		id: rowid,
		parent_id:data.parent_id,
		result_name:data.result_name,
		result_test_name:data.result_test_name,
		result_type:data.result_type,
		materialid:data.materialid,
		is_basic:data.is_basic,
		sample_id:data.sample_id,
		purity:data.purity,
		num: rowGrNumber,
		weighting: rowGrSubNumer,
		type:data.type,
		batchname: data.batchname,
		weight: data.weight,
		volume: data.volume,		
		dilution:data.dilution,
		component_name:data.component_name,
		coefficient:data.coefficient,
		ch1:data.chb_include,
		batch_with_purity:data.batch_with_purity,
		component_id:data.component_id,
		batch_id:data.batch_id,
		preparationref_id:data.preperationref_id,
		area:data.area,
		retention:data.retention,
		comment:data.result_comment,
		start:"", 
		end:""            		
	};
	return _tmpObj;
}

function fillTmpObjWithDefaultDataWebixAnalyt(rowid, data, rowGrNumber, rowGrSubNumer)
{
	var _tmpObj = {};
	_tmpObj = {
		id: rowid,
		parent_id:data.parent_id,
		result_name:data.result_name,
		result_test_name:data.result_test_name,
		result_type:data.result_type,
		materialid:data.materialid,
		is_basic:data.is_basic,
		sample_id:data.sample_id,
		purity:data.purity,
		num: rowGrNumber,
		weighting: rowGrSubNumer,
		type:data.type,
		batchname: data.batchname,
		weight: "",
		volume: "",		
		dilution:1,
		component_name:data.component_name,
		coefficient:data.coefficient,
		ch1:1,
		batch_with_purity:data.batch_with_purity,
		component_id:data.component_id,
		batch_id:data.batch_id,
		preparationref_id:data.preperationref_id,
		area:"",
		retention:"",
		comment:"",
		start:"", 
		end:""            		
	};
	return _tmpObj;
}

function updateColumnData(tableID, newValue)
{
	if(tableID != null && tableID != "")
	{
		var grid = $$(tableID);		
		grid.editStop();
		var nums = grid.collectValues("num");
		
		for(var i=0;i<nums.length;i++)
		{
			var numValue = nums[i].value;
			var result_ = grid.find(function(obj){
				return obj.num.toString().indexOf(numValue) != -1;
			});
			//console.log(result_);
			for(var j=0;j<result_.length;j++)
			{
				var rowObj = result_[j];
				rowObj["retention"] = newValue;
			}
		}
		grid.refresh();
	}
}

function calcAnalyt(tableID, tableIsBasic, basicStandardMeanRF)
{
	console.log("CALC for tableid: " + tableID + ", tableIsBasic: " + tableIsBasic);
	
	var standardMeanRF_toreturn = "";
	if(tableID != null && tableID != "")
	{
		var grid = $$(tableID);		
		grid.editStop();
		var nums = grid.collectValues("num");
		var standardMeanRF = "";		
		var isTableHasStandard = tableIsBasic;
		var rowRFSum = 0;
		var rowRFcount = 0;
		var rowWW = 0;
		var rowWWSum = 0;
		var rowWWcount = 0;
		var rowStdArr = [];
		var rowStd = "";
		var rowConc = null;
		var rowRF = null;
		
		if(nums.length > 0)
		{
			if(!tableIsBasic)
			{
				var _rowsStandard = grid.find(function(obj)
				{
					return (obj.num.toString().indexOf(1) != -1 && obj.type.toString().indexOf("Standard") != -1);
				});
				if(_rowsStandard.length > 0)
				{
					isTableHasStandard = true;
				}
				else
				{
					standardMeanRF = basicStandardMeanRF;
				}
			}
			
			for(var i=0;i<nums.length;i++)
			{
				rowRFSum = 0;
				rowRFcount = 0;
				rowWWSum = 0;
				rowWWcount = 0;
				rowStdArr = [];
				rowStd = "";
				var isStandard = false;
				var numValue = nums[i].value;
				var rowRFAvg = 0;
				
				/* result_ = get all rows that have the same 'num' value*/
				var result_ = grid.find(function(obj){
									return obj.num.toString().indexOf(numValue) != -1;
								});
				for(var j=0;j<result_.length;j++)
				{
					rowWW = "";
					rowConc = "";
					rowRF = "";	
					var rowObj = result_[j];
					
					if(isTableHasStandard && rowObj.batch_with_purity == "0")
    				{
						NO_PURITY_MARKER = true;
						console.log("no purity");
						displayAlertDialog("Standard " + rowObj.batchname + " does not have defined purity. Please select a different standard on Samples tab or define purity for this standard on Batch screen.");
    					return;
    				}
					
					//console.log("--------------------------------------------------------------------------");
					//console.log("Component Name: " + rowObj.component_name + " Batch: " + rowObj.batchname + "  Weighting:" + (j+1));
					
					isStandard = (rowObj.type == "Standard")?true:false;			
			        var isInclude = (rowObj.ch1 != null && rowObj.ch1 == 1)?true:false;
			        
			        if(rowObj.weight != null && rowObj.volume != null)
			        {
			        	rowConc = (isInclude)?divideInAnalyt(rowObj.weight, rowObj.volume):"";
			        	rowObj["conc"] = rowConc;
			        	
			        	if(rowObj.area != null)
				        {
			        		console.log("isStandard: " + isStandard + "|area:" + rowObj.area + "|conc:" + rowConc);
			        		rowRF = (isInclude)?divideInAnalyt(rowObj.area, rowConc):"";
			        		console.log("rowRF: " + rowRF);
				        	rowObj["rf"] = rowRF;
				        	
							if(rowRF != "" && rowRF != "NA")
			        		{
			        			rowRFSum = rowRFSum + Math.round(rowRF * 1000) /1000;
			        			rowStdArr[rowRFcount++] = rowRF;
			        			
			        			if(!isStandard && isInclude)
			        			{
				        			if(isTableHasStandard)
				        			{
				        				console.log("purity:" + rowObj.purity + " standardMeanRF: " + standardMeanRF);
				        				rowWW = (rowObj.purity != "" && standardMeanRF != "")?divideInAnalyt(rowRF * rowObj.purity, standardMeanRF):"";
				        			}
				        			else
									{
				        				console.log("coefficient:" + rowObj.coefficient + " basicStandardMeanRF: " + standardMeanRF);
				        				// standardMeanRF == basicStandardMeanRF
				        				rowWW = (standardMeanRF != "" && (rowObj.coefficient != null && rowObj.coefficient != ""))?divideInAnalyt(rowRF, standardMeanRF * rowObj.coefficient) * 100:"";
									}
			        			}
			        		}						
			        		rowObj["ww"] = rowWW;
			        		console.log("WW%: " + rowRF);
			        		if(rowWW != "" && rowWW != "NA")
		        			{
			        			rowWWSum = rowWWSum + parseFloat(rowWW);//Math.round(rowObj.ww * 1000) /1000;;
			        			rowWWcount++;
		        			}
				        }
			        }
			        //console.log("-------------------------------------------------------------------------------------");
				}
				var record = grid.getItem(result_[0].id);
				//console.log(record);
				if(isStandard)
				{
					standardMeanRF = (rowRFcount > 0)?rowRFSum/rowRFcount : "";
					standardMeanRF_toreturn = (numValue == 1)?standardMeanRF:standardMeanRF_toreturn;
					rowRFAvg = standardMeanRF;
				}
				else
				{
					rowRFAvg = (rowRFcount > 0)?rowRFSum/rowRFcount : "";
				}
				record["meanrf"] = (isStandard)?standardMeanRF:"";	
				record["result"] = (!isStandard && rowWWcount > 0)?rowWWSum/rowWWcount : "";
				
				//console.log("rowStdArr: " + rowStdArr + "|rowRFAvg: " + rowRFAvg);
				
				rowStd = (rowStdArr.length > 0)?Math.round(math.std(rowStdArr) * 1000) /1000 : "";
				record["std"] = rowStd;
				var rsd = divideInAnalyt(rowStd, rowRFAvg);
				record["rsd"] = (rsd !="" && rsd != "0" && rsd != "NA")?parseFloat(rsd) * 100 : "";
			}
			grid.refresh();
		}
	}		
	return standardMeanRF_toreturn;
}

function divideInAnalyt(a,b)
{
	return divide(a,b,true);
}

function divide(a,b,isDecimal)
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

function calcAllAnalyt(domID)
{
	var standardMeanRF = "";
	var $div = $("div[name='parentWebixContainer'][id='"+domID+"']");
	$div.find(".webix_container[isBasic='1']").each(function()
	{
		var tableID = $(this).attr('childTableID');
		//console.log("basic table: " + tableID);
		if(!NO_PURITY_MARKER)
		{
			standardMeanRF = calcAnalyt(tableID, true, "");
		}
	});
	$div.find(".webix_container[isBasic='0']").each(function()
	{
		var tableID = $(this).attr('childTableID');
		//console.log("table: " + tableID);
		if(!NO_PURITY_MARKER)
		{
			calcAnalyt(tableID, false, standardMeanRF);
		}
	});
	NO_PURITY_MARKER = false;
}

function getOutputDataAnalyt(domID)
{	
	// calculate all before save
	calcAllAnalyt(domID);
	
	var tablesSnapshotObj = [];
	var returnObj = {};
	var resultOutputArr = [];
	var isTableHasRows = 0;
	console.log(tableIDArr);
	for(var i=0;i<tableIDArr.length;i++)
	{		
		var grid = 	$$(tableIDArr[i]);
		var tableData = grid.serialize();
		
		if(tableData.length > 0)
		{
			isTableHasRows = 1;
			var state = grid.getState();
			var span = allTablesSpanArr[tableIDArr[i]];
						
			for(var j=0;j<tableData.length;j++)
			{
				var _curComment = tableData[j].comment;
				console.log(_curComment);
				/*if(_curComment != null && _curComment != undefined && _curComment != "")
				{
					_curComment = _curComment.replace(/[\r\n]+/g," ");
					console.log(_curComment);
					tableData[j].comment = _curComment;
				}*/
				if(tableData[j].type == "Sample" && (tableData[j].result != null && tableData[j].result != ""))
				{
					resultOutputArr.push({
						"experiment_id":tableData[j].parent_id,
						"result_test_name":tableData[j].result_test_name,
		        		"result_name":tableData[j].result_name,
		        		"sample_id":tableData[j].sample_id,
		        		"result_value":tableData[j].result,
		        		"result_uom_id":"%",
		        		"result_type":tableData[j].result_type,
		        		"result_material_id":tableData[j].materialid,
		        		"result_comment":_curComment,
		        		"SELFTEST_ID":""
					});
				}
			}
		}
		tablesSnapshotObj.push({data:tableData,spans:span});
	}
	//console.log("tablesSnapshotObj: ");
	//console.log(JSON.stringify(tablesSnapshotObj));	
	
	returnObj = {isWebixTableHasRows:isTableHasRows, tablesSnapshot:tablesSnapshotObj, resultOutput: resultOutputArr};
	
	console.log("returnObj: ");
	console.log(JSON.stringify(returnObj));
	
	return returnObj;
}

function uploadAnalyticalData(domId)
{
	console.log("uploadAnalyticalData");
	showWaitMessage(getSpringMessage('pleaseWait'));
    // ajax call to the api service
   
    var data_ = {
	              "formId": $('#formId').val(),
	              "domId": domId
	        	};
    $.ajax({
        type: 'POST',
        data: data_,
        url: "./onElementWebixAnalytCalcUpload.request",
        //contentType: 'application/json',
        dataType: 'json',
        success: function (obj) 
        {      
            console.log(obj);
            if (obj.errorMsg != null && obj.errorMsg != '') {
                displayAlertDialog(obj.errorMsg);
            } else if (obj.data[0].val == "-1") {
                displayAlertDialog("Error");
            } else {
            	showWaitMessage(getSpringMessage('pleaseWait'));
                var o = funcParseJSONDataWX(obj.data[0].val,false);
                //console.log(o);
                for(var i=0;i<tableIDArr.length;i++)
				{
					var grid = 	$$(tableIDArr[i]);
					if (grid)
        				grid.destructor();
				}
                var $div = $("div[name='parentWebixContainer'][id='"+domId+"']");
            	$div.empty();		
            	$div.append(o.allTablesDiv);
                tableIDArr = [];
				allTablesSpanArr = [];
				for(var i=0;i<o.tablesToInit.length;i++)
				{
					//console.log(o.tablesToInit[i]);
					var t = o.tablesToInit[i];
					initElementDataTableWebixAnalyt(domId, t.tableID, t.tableDivID, t.tableData, "2", false);
				}
				calcAllAnalyt(domId);
                
            }
            hideWaitMessage();
        },
        error: handleAjaxError
    });
}