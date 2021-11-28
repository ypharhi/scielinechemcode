var ElementWebixMassBalanceCalcImp = {
		value_: function (val_, changeType, parentId) 
	    {
	    	var domId = $(val_).attr('id');
	        var elementID = $(val_).attr('elementID');
	        var data_ = {};
	        if(changeType == 2)
	        {	    	
		        var object = getOutputDataMassBalance(domId);
		        data_ = {
		            //"elementId": elementID,
		            "isTableHasRows":object.isWebixTableHasRows,
		            "objToSaveAsClob":object.tablesSnapshot,
		            "output": object.output,
		            "parentID": parentId!== undefined?parentId:$('#formId').val(),
		            "webixTableGroupNumber":object.webixTableGroupNumber
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
var _webixMassBalanceDDLDataObject;
var _webixMassBalanceLAGlobal = {};
//var _limitingAgentMolesGlobal = 0;

function setWebixMassBalanceInitData(data, domId)
{
	_webixMassBalanceDDLDataObject = $.parseJSON(data);
	setWebixMassBalanceLimitingAgentMole(domId);
}

function setWebixMassBalanceLimitingAgentMole(domId)
{
	if(massBalanceOwner() == "Step")
		_webixMassBalanceLAGlobal[domId] = parseFloat($("#limitingAgentMole").attr('realvalue'));
	else
	{
		var fieldInx = "";
		if(domId == "webixMassBalanceTable2") fieldInx = "2";
		else if(domId == "webixMassBalanceTable3") fieldInx = "3";
		_webixMassBalanceLAGlobal[domId] = parseFloat($("#limitingAgentMole"+fieldInx).attr('realvalue'));
	}
}

function initElementDataTableWebixMassBalance(domId, tableID, tableDivID, data, isNew, isDisabled)
{    
//    console.log("initElementDataTableWebixMassBalance(): data for tableID: " + tableID);
//	console.log(data);
	isDisabled = false; //yp 10032019 - fix patch for version 1.428.3 - general prod - slow analytical experiment load
			
    var tableData = [];
    var fieldsData = {};
    var isStepSamplesChecked = (massBalanceOwner() == "Step")?"checked":"";
    var _columnsArr = [
						{ id:"material_name", header:"Material", width:210},
						{ id:"substance_type", 	header:"Substance Type", width:190, editor:"select", 
							options:["product","impurity","product isomer","limiting agent"]						
						},
						{ id:"perc_of_product",	header:"% of product" , width:130, format:webix.Number.numToStr({
						    groupDelimiter:" ",
							groupSize:0,
							decimalDelimiter:".",
							decimalSize:3
						})},
						{ id:"weight",	header:"Weight(g)", width:100, format:webix.Number.numToStr({
						    groupDelimiter:" ",
							groupSize:0,
							decimalDelimiter:".",
							decimalSize:3
						})},
						{ id:"moles",	header:"Moles", width:100, format:webix.Number.numToStr({
						    groupDelimiter:" ",
							groupSize:0,
							decimalDelimiter:".",
							decimalSize:5
						})},
						{ id:"yield",	header:"Yield(%)", width:100, format:webix.Number.numToStr({
						    groupDelimiter:" ",
							groupSize:0,
							decimalDelimiter:".",
							decimalSize:3
						})},
						{ id:"is_chemical", header:"Chemical", template:"{common.checkbox()}", width:80, css:"center"},
						{ id:"is_isolated", header:"Isolated", template:"{common.checkbox()}", width:80, css:"center"},
						{ id:"is_yield", header:"Yield loss", template:"{common.checkbox()}", width:90, css:"center"},
						{ id:"is_summary", header:"Summary", template:"{common.checkbox()}", width:80, css:"center"},
						{ id:"description",	header:"Description", 	editor:"popup", width:360},
						{ id:"removeRowBtn", header:"", width:40, tooltip:"", css:"center", template:function(obj)
				  		 { 
				  			return "<i class='delbtn webix_icon fa-trash' title='Remove row' style='margin-right:10px;cursor:pointer;'></i>";
		     			 }
						},
						{ id:"molecula_weight", hidden:true},
						{ id:"inv_item_material_id", hidden:true}
					];
    
    //console.log("_columnsArr for table: " + tableID);
	//console.log(_columnsArr);
	
    if(isNew == "1")
	{
    	if($('#isNew').val()=='1')
    	{
    		isDisabled = false;
    	}
	}
    else
	{   	
    	var _dataObj = funcParseJSONDataWX(data,true);
    	//console.log(_dataObj);
    	tableData = _dataObj["data"];
    	fieldsData = _dataObj["fieldsData"];
    	//console.log("fieldsData", fieldsData);
    	if(massBalanceOwner() == "Step" && fieldsData!=undefined)
    		isStepSamplesChecked = (fieldsData["chkStepSamples"] == 0)?"":"checked";
	}	
    var checkboxViewObj = {width:10};
    if(massBalanceOwner() == "Step")
    {
    	checkboxViewObj = { view:"checkbox", 
							  name:"chkStepSamples",
							  template:function(obj)
				  			  { 
					  			return '<div style="line-height:32px;"><label style="width: 100px;text-align: left;line-height:32px; margin-top: 2px;" class="webix_inp_label ">Step Samples</label>'+ 
					  			'<input id="chkStepSamples_'+tableID+'" style="margin-top: 14px;margin-left: 5px;" onclick="updateSamplesList(this.checked, \''+tableID+'\', true)" type="checkbox"  '+isStepSamplesChecked+'></div>';
			     			  },
							  customCheckbox:false,
							  width:115
						  };
    }
    
    var formMain = [	
    				{ margin:10, cols:[
									{ view:"text", 
									  id:"btnRemove" + tableID,
									  width:120, template:function(obj)
									  			 { 
										  			return '<div class="webix_el_button"><button class="webixtype_base" onClick="removeForm(\''+tableID+'\')" style="height:35px">Remove stream</button></div>';
								     			 }
									},
									{ view:"text", 
									  name:"txtStream",
									  id:"txtStream" + tableID,									
									  value:'', 
									  label:"Stream",
									  labelWidth:"60",
									  cellType:"string",
									  width: 500
									},
									checkboxViewObj,
									{ 
										view:"combo", width:510,
										id:"ddlSample" + tableID,	
										label: 'Sample',  
										labelWidth:"60",
										name:"ddlSample",
										mandatory:true,
										options:_webixMassBalanceDDLDataObject["ddlSample"]
									},
									{ view:"text", 
									  width:190,
									  id:"txtMass" + tableID, 
									  name:"txtMass",
									  label:"Mass",
									  labelWidth:"45",
									  cellType:"decimal",
									  on:{	
										    onKeyPress:function(code, e)
											{
										    	//console.log("ON KEY PRESS IN MASS");
											  	//console.log(code);
											  	if(code != 13)
											  	{
											  		validateDecimalWX(code, e);
											  	}
											}	
									  }
									},
									{ view:"button", 
									  id:"btnUpdate" + tableID,
									  name:"btnUpdate",
										width:110, 
										template:function(obj)
										  			 { 
											  			return '<div class="webix_el_button"><button class="webixtype_base btnResToUpdate" id="btnResToUpdate' + tableID +'" style="height:35px; display:none">Update Results</button></div>';
									     			 }
									
										}
								]
				},
				{
					view:"datatable",
					id: tableID,
					name:"table",
					columns:_columnsArr,
					navigation:true,
		            select:"cell",
					autoheight:true,
					autowidth:true,
					editable:true,
					math: true,
					tooltip:true,
					data:tableData,
					onClick:{				
						"delbtn":function(e, id, trg){
					    	//id.column - column id
							//id.row - row id
							this.editStop();
							//console.log(id);
							var $o = this;
							openConfirmDialog
							({
			        	        onConfirm: function(){
			        	        	$o.remove(id.row);
			        	        },
			        	        message: getSpringMessage('confirmRowDeletion'),
			        	        onCancel: function(){
			        	        	return false;
			        	        }
			        	    });
							/*if(this.count() == 1)
							{
								displayAlertDialog("Delete the last row is impossible");
							}
							else
							{
								this.remove(id.row);
							}*/
							//block default onclick event
							return false;
					    }		
					},
//					on:{	
//						onAfterLoad:function()
//						{
//							console.log("ON AFTER LOAD TABLE",domId);
//							//calcWebixMassBalance(tableID,domId);
//						}
//				    },
					ready:function()
					{
						this.attachEvent("onEditorChange", function(id, value)
						{
							//console.log("ON EDITOR CHANGE");
							//console.log(value);
							//console.log(id.column);
							if(id.column == "substance_type")
							{
								if(value == "product")
								{
									this.getItem(id.row)["is_chemical"] = 1;
									this.getItem(id.row)["is_isolated"] = 0;
									this.getItem(id.row)["is_yield"] = 0;
								}
								if(value == "limiting agent")
								{
									this.getItem(id.row)["is_yield"] = 1;
									this.getItem(id.row)["is_chemical"] = 0;
									this.getItem(id.row)["is_isolated"] = 0;
								}
							}
						    this.refresh(id.row);
						    calcWebixMassBalance(tableID,domId);
						});
						this.attachEvent("onCheck", function(row, column, state)
						{
							calcWebixMassBalance(tableID,domId);
						});
						this.attachEvent("onKeyPress", function(code, e){
							//console.log("ON KEY PRESS");
							if(this.getEditor() != undefined && this.getEditor() != "0")
							{
								if(this.getEditor().column == "description")
								{
									validateLegalCharWX(code, e);
								}
							}
						});
						hideWebixMassBalanceButton(tableID);
					}
				}
	];
	webix.ui({
		container:tableDivID,
		margin:30, cols:[
			{ margin:30, rows:[
				{ view:"form", id:"wbxForm_"+tableID, scroll:false, width:1630, elements: formMain, data:fieldsData,
				  disabled:isDisabled,
				  on:{	
						onAfterLoad:function()
						{
							console.log("ON AFTER LOAD FORM",domId);
							console.debug("is step samples checked: ",isStepSamplesChecked);
							if (fieldsData.ddlSample !== undefined && fieldsData.ddlSample !== "" && fieldsData.ddlSample !== "0" && fieldsData.ddlSample !== "-1")
							{
								getTooltip(tableID, fieldsData.ddlSample); // 05082020 kd added tooltip for sample field
							}
							if(massBalanceOwner() == "Step" && isStepSamplesChecked == "")
							{
								updateSamplesList(false, tableID, false);
							}
						}
				  }
				}
			]}
		]
	});
    
	$$("wbxForm_"+tableID).elements["txtMass"].attachEvent("onChange", function(newvalue, oldv)
	{
		//console.log("ON CHANGE MASS"); 
		calcWebixMassBalance(tableID,domId);
	});		
	$$("wbxForm_"+tableID).elements["ddlSample"].attachEvent("onChange", function(newvalue, oldv)
	{
		//console.log("ON CHANGE SAMPLE"); 
		updateTableDataForWebixMassBalance(_columnsArr, newvalue, tableID, domId);
		updateFieldsBySampleId(newvalue, tableID);
	});
	$$("wbxForm_"+tableID).elements["ddlSample"].attachEvent("onEnter",function(ev){
			//console.log("ON ENTER SAMPLE"); 
			this.callEvent("onChange", ["",""]);
	});
	$$("wbxForm_"+tableID).elements["btnUpdate"].attachEvent("onItemClick",function(ev){
		if(!$('#btnResToUpdate' + tableID).hasClass('disablePage')){
			updateResultsMassb(tableID,domId,_columnsArr);
		}
});// onClick="updateResultsMassb(\''+tableID+'\',\''+domId+'\',\''+_columnsArr+'\')"
}
// END initElementDataTableWebixMassBalance() function

/**
 * updateSampleListAndShowOrHideStream is invoked when changing the run DDL.
 * it firstly fired after the page is loaded and the run DDL is initiated.
 * @param args contains an array of arguments. the first argument is expected to be the run number.
 * @returns
 */
function updateSampleListAndShowOrHideStream(args){
	runNumber = args[0];
	var data_ = {
			"samplesScope": "experiment",
			"parentID": $('#formId').val(),
			"runNumber": runNumber
      	};
	showWaitMessage(getSpringMessage('pleaseWait'));
	//console.log("webixMassBalance updateSamplesList(): "+tableID,data_);
	$.ajax({
	  type: 'POST',
	  data: data_,
	  url: "./updateWebixMassBalanceSamplesList.request",
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
	          var o = JSON.parse(obj.data[0].val);
	          //console.log("UPDATE SAMPLES LIST:");                	          
	          //console.log(o.listdata);
	          var sampleList = JSON.parse(o.listdata);
	          _webixMassBalanceDDLDataObject["ddlSample"] = sampleList;//update the sample list according to the selected run
	          
	          //runs through all the mass balance tables(in every tab)and show or hide the streams 
	          $("*[formelement=1][element=ElementWebixMassBalanceCalcImp]").each(function(){
	      		var domId = $(this).attr("id");
		        var $div = $("div[name='parentWebixContainer'][id='"+domId+"']");
		      	var tableIndex = $div.find(".webix_container").length;
		      	
		      	//runs through each stream that was added  in a mass balance element
		      	for(var i=1;i<=tableIndex;i++){
			      	var tableID = "tableID_"+domId + "_" + i;
			      	var tableDivID = "tableDiv_"+domId + "_" + i;
			        
			      	//the sample list contains all the samples that were created from the selected run.
			      	//if the stored sample is in the expected sample list then it indicates
			      	/////on a stream that refers to the current run, and the stream is shown, else it's hidden.
			      	var  isSampleInList = false;
			        var selectedSample = $$("ddlSample"+tableID).getValue();
		      	    for(var j = 0;j<sampleList.length;j++){
		      	    	if(sampleList[j].id==selectedSample){
		      	    		isSampleInList = true;
		      	    		break;
		      	    	}
		      	    }
		      	    var isHidden = !isSampleInList;
		        
		          if(!isHidden){
		        	  //this section update the sample list of the an existing stream.
		        	  //it's necessary for a saved stream.
		        	  var list = $$("ddlSample"+tableID).getPopup().getList();
			          list.clearAll();	          
			          list.parse(o.listdata);
			          ////////////////////////////////////////////////
			          $("#"+tableDivID).show();
			          $("#"+tableDivID).attr("iswebixtablehidden",false);
		          }else{
		        	  $("#"+tableDivID).hide();
		        	  $("#"+tableDivID).attr("iswebixtablehidden",true);
		          }
		      	}
		      	var tabIndex = domId.slice(domId.length - 1);
		      	calculateMassBalanceFields(domId,!isNaN(parseInt(tabIndex, 10))?tabIndex:"");
	      	});
	      }
	      hideWaitMessage();
	  },
	  error: handleAjaxError
	});
}

function updateSamplesList(isChecked, tableID, doRefreshTable)
{
	var scopeObj = "step";
	if(!isChecked)
	{
		scopeObj = "experiment";
	}
	
	var data_ = {
			"samplesScope": scopeObj,
			"parentID": (scopeObj == 'step')?$('#formId').val():$('#EXPERIMENT_ID').val(),
			"runNumber":""
      	};
	//console.log("webixMassBalance updateSamplesList(): "+tableID,data_);
	$.ajax({
	  type: 'POST',
	  data: data_,
	  url: "./updateWebixMassBalanceSamplesList.request",
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
	          //console.log("UPDATE SAMPLES LIST:");                	          
	          //console.log(o.listdata);   
	          
	          var list = $$("ddlSample"+tableID).getPopup().getList();
	          list.clearAll();	          
	          list.parse(o.listdata);
	          if(doRefreshTable)
	          {
	        	  $$("ddlSample"+tableID).setValue("");
	        	  refreshWebixTable([], tableID);
	          }
	      }
	      hideWaitMessage();
	  },
	  error: handleAjaxError
	});
}

function removeForm(tableID)
{
	//console.log('remove table: ' + tableID);
	var $form = $$("wbxForm_"+tableID);
    if ($form)
    {
    	$form.destructor();
    }
    var $div = $("div[childTableID='"+tableID+"']");
    $div.attr('isWebixTableHidden',true);
	$div.empty();
}

function updateTableDataForWebixMassBalance(colArr, newvalue, tableID, domId)
{
	//console.log("updateTableDataForWebixMassBalance");
	//console.log("newvalue: " + newvalue);
	
	if(newvalue == "")
	{		
		refreshWebixTable([], tableID);
	}
	else
	{
		showWaitMessage(getSpringMessage('pleaseWait'));				
		var data_ = {
	            "sampleID": newvalue,
	            "domId": domId
	      	};
		$.ajax({
		  type: 'POST',
		  data: data_,
		  url: "./onElementWebixMassBalanceCalcUpdate.request",
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
		          //console.log("UPLOAD calc table:");                	          
		          //console.log(o.tableData);   
		          
		          var _newData = prepareDataToWebixMassBalance(o.tableData, colArr, tableID);
		          refreshWebixTable(_newData, tableID);

		          //Get COMMENTSFORCOA from o.tableData and set it as a tooltip for Sample dropdown list 
		          // from data parameter 
		          try
		          {
			          var obj = JSON.parse(o.tableData);
			          var _comments = '';
			  		  if (obj.length > 0) {
			  			_comments = obj[0]["comments"];
			  		  }
			          if (_comments === undefined || _comments === null)
			          {
			        	  _comments = '';
			          }
			          updateTooltip(_comments, tableID);
		          } 
		          catch(e)
			  	  {
		        	  console.log(e);
			  	  }
		      }
		      hideWaitMessage();
		  },
		  error: handleAjaxError
		});
	}
}

function updateFieldsBySampleId(sampleId, tableID) 
{
	if(sampleId == "")
	{
		$$("txtStream"+tableID).setValue("");
		$$("txtMass"+tableID).setValue("");
	}
	else
	{
		
		try 
		{
			// get data by specific ID from exists list
			var _obj = $$("ddlSample"+tableID).getList().data.pull[sampleId];
			$$("txtStream"+tableID).setValue(_obj["sample_origin"]);
			$$("txtMass"+tableID).setValue(_obj["sample_ammount"]);
		}
		catch(e)
		{
			console.log(e);
		}
	}
}

function refreshWebixTable(newData, tableID)
{
	var $tableId = $$(tableID);
    $tableId.clearAll();
    $tableId.parse(newData, "json");
    $tableId.refresh();
}
/**
 * 05082020 kd
 * this function set tooltip for a Sample ddl
 *
 * @param newData
 * @param tableID
 * @returns
 */
function updateTooltip(newData, tableID)
{
	var $ddlId = $$('ddlSample' + tableID);
	$ddlId.tooltip_setter(newData);    
}

/**
 * 05082020 kd
 * this function is used on load form. Get COMMENTSFORCOA 
 * from fg_s_sample_v by sample_id 
 *  
 * @param tableID
 * @param ddlSampleID
 * @returns
 */
function getTooltip(tableID, ddlSampleID)
{
	var data_ = {
			"sampleID": ddlSampleID
      	};
	$.ajax({
	  type: 'POST',
	  data: data_,
	  url: "./getTooltipForWebixMassBalanceSamplesField.request",
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
	          updateTooltip(obj.data[0].val, tableID)
	      }
	  },
	  error: handleAjaxError
	});
}

function prepareDataToWebixMassBalance(data, colArr, tableID)
{
	var objToReturn = [];	
	try
	{
		var obj = JSON.parse(data);	
		//console.log(obj);
		/*if(obj.length == 0)
		{
			return {};
		}	*/	
		$(obj).map(function () 
	    {
			var curRowObj = this;
			var _tmpobj = {};
			//_tmpobj["id"] = webix.uid();
			$(colArr).map(function () 
		    {
				var colID = this.id;
				//console.log("colimn id: " + colID);
				_tmpobj[colID] = curRowObj[colID];
		    });
			_tmpobj["start"] = "";
			_tmpobj["end"] = "";
			_tmpobj["molecula_weight"] = curRowObj["molecula_weight"];
			_tmpobj["inv_item_material_id"] = curRowObj["inv_item_material_id"];
			//console.log("_tmpobj",_tmpobj);		
			objToReturn.push(_tmpobj);
	    }); 
	}
	catch(e)
	{
		console.log("ERROR in prepareDataToWebixMassBalance():");
		console.log("tableData: " + data);
		console.log(e);
		//return {};
	}
	//console.log("prepareDataToWebixMassBalance() objToReturn:");
	//console.log(objToReturn);
	return objToReturn;
}

function addWebixMassBalanceTable(domId)
{
	//console.log("addWebixMassBalanceTable: " + domId);
	
	var $div = $("div[name='parentWebixContainer'][id='"+domId+"']");
	var tableIndex = ($div.find(".webix_container").length) + 1;
	
	var tableDivID = "tableDiv_"+domId + "_" + tableIndex;
	var tableID = "tableID_"+domId + "_" + tableIndex;
	
	var tabledivhtml = "<div class=\"webix_container\" childTableID=\""+tableID+"\" id=\""+tableDivID+"\" isWebixTableHidden=\"false\" style=\"margin-bottom: 20px;\"></div>";
	$div.append(tabledivhtml);
	
	initElementDataTableWebixMassBalance(domId, tableID, tableDivID, "[]", "1", false);
}

function calcWebixMassBalance(tableID,domId)
{
	//console.log("CALC for tableid: " + tableID);
	var returnObj = {};
	if(tableID != null && tableID != "")
	{

		var grid = $$(tableID);		
		/*var fieldsValues = $$("wbxForm_"+tableID).getValues();
		console.log(fieldsValues);*/
		var _fMass = $$("txtMass" + tableID).getValue();
		//console.log(_fMass);
		var _summary = 0, _conversion = 0, 
			_totalChemical = 0, _totalIsolated = 0;
		
		grid.eachRow( 
			    function (row)
			    { 
			    	var _weight, _percOfProd, _moles, _yield = "",
			    		_isChemicalChk, _isIsolatedChk, _isYieldChk, _isSummaryChk;
			    	var rowObj = grid.getItem(row);
			    	//console.log("current row: ");
			        //console.log(rowObj);
			        
			        _isChemicalChk = (rowObj.is_chemical != null && rowObj.is_chemical == 1)?true:false;
			        _isIsolatedChk = (rowObj.is_isolated != null && rowObj.is_isolated == 1)?true:false;
			        _isYieldChk = (rowObj.is_yield != null && rowObj.is_yield == 1)?true:false;
			        _isSummaryChk = (rowObj.is_summary != null && rowObj.is_summary == 1)?true:false;
			        
			        _percOfProd = rowObj.perc_of_product;
			        if(_percOfProd != null && _fMass != null)
			    	{			    					    		
			        	if(isNaN(_fMass))
			    		{
			        		_fMass = "";
			    		}
			        	_weight = (_percOfProd != "" && _fMass != "")?(_fMass * (_percOfProd/100)):"";
			    		rowObj["weight"] = _weight;
			    	}
			        if(rowObj.weight != null)
			        {
			        	//console.log("MW: " + rowObj.molecula_weight);
			        	_moles = divideInWebixMassBalance(rowObj.weight, rowObj.molecula_weight);
			        	rowObj["moles"] = _moles;
			        }
			        if(rowObj.moles != null)
		        	{
			        	if(_moles != ""  && (_isChemicalChk || _isIsolatedChk || _isYieldChk))
			        	{
			        		_yield = divideInWebixMassBalance((_moles * 100),_webixMassBalanceLAGlobal[domId]);
			        		
			        		if(_yield != "")
			        		{
			        			if(_isSummaryChk)
				        		{
			        				_summary += parseFloat(_yield);
				        		} 
			        			if(rowObj.substance_type == "limiting agent")
				        		{
				        			_conversion += parseFloat(_yield); 
				        		}
				        		if(_isChemicalChk)
				        		{
				        			_totalChemical += parseFloat(_yield);
				        		}
				        		if(_isIsolatedChk)
				        		{
				        			_totalIsolated += parseFloat(_yield);
				        		}
			        		}
			        	}
			        	rowObj["yield"] = _yield;
		        	}
			    }
			)
		
		returnObj = {"summary": _summary,"conversion":_conversion,"totalChemical":_totalChemical,"totalIsolated":_totalIsolated};
		grid.refresh();
	}
	return returnObj;
}

function divideInWebixMassBalance(a,b)
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

function calcAllMassBalance(domID)
{
	var standardMeanRF = "";
	var calcArr = [];
	var returnObj = {};
	var $div = $("div[name='parentWebixContainer'][id='"+domID+"']");
	$div.find(".webix_container[isWebixTableHidden='false']").each(function()
	{
		var tableID = $(this).attr('childTableID');
		var obj = calcWebixMassBalance(tableID,domID);
		calcArr.push(obj);
	});
	//console.log("calcAllMassBalance calcArr");
	//console.log(calcArr);
	
	var f1 = 0,
		f2 = 0,
		f3 = 0,
		f4 = 0;
	$(calcArr).map(function () 
    {
		var o = this;
		f1 += o["summary"];
		f2 += o["conversion"];
		f3 += o["totalChemical"];
		f4 += o["totalIsolated"];
    });
	returnObj = {"summary": f1,"conversion":(100 - f2),"totalChemical":f3,"totalIsolated":f4};
	//console.log(returnObj);
	return returnObj;
}

function getOutputDataMassBalance(domID)
{	
	
	var tablesSnapshotObj = [];
	var returnObj = {};
	var outputArr = [];
	var fullOutputArr = [];
	//var isTableHasRows = 0;
	var allSelSamplesList = "";
	var tablesCounter = 0;
		
	var $div = $("div[name='parentWebixContainer'][id='"+domID+"']");
	$div.find(".webix_container[isWebixTableHidden='false']").each(function()
	{
		//isTableHasRows = 1;
		outputArr = [];
		var tableID = $(this).attr('childTableID');
		//console.log("table: " + tableID);
		var grid = 	$$(tableID);
		grid.editStop();

		var fieldsValues = $$("wbxForm_"+tableID).getValues();
		if(massBalanceOwner() == "Step")
		{
			fieldsValues["chkStepSamples"] = ($("#chkStepSamples_"+tableID).prop('checked'))?1:0;
		}
		fieldsValues["ddlSampleValue"] = $$("ddlSample"+tableID).getText();
		
		//console.log(fieldsValues);
		var _val = fieldsValues["ddlSample"];
		if(_val != "")
		{
			if(allSelSamplesList == "")
			{
				allSelSamplesList = fieldsValues["ddlSample"];
			}
			else
			{
				allSelSamplesList += "," + fieldsValues["ddlSample"];
			}
		}
		//console.log('collect data for table: ' + tableID);
		grid.eachRow(function (row)
		{ 
			//isTableHasRows = 1;
			var rowObj = grid.getItem(row);
			var _tmpobj = [];
			_tmpobj.push({
				"material_name":rowObj["material_name"],
				"substance_type":rowObj["substance_type"],
				"perc_of_product":rowObj["perc_of_product"],
				"weight":rowObj["weight"],
				"moles":rowObj["moles"],
				"yield":rowObj["yield"],
				"is_chemical":rowObj["is_chemical"],
				"is_isolated":rowObj["is_isolated"],
				"is_yield":rowObj["is_yield"],
				"is_summary":rowObj["is_summary"],
				"description":(rowObj["description"] == null)?"":rowObj["description"],
				"molecula_weight":rowObj["molecula_weight"],
				"material_id":(rowObj["inv_item_material_id"] == null)?"":rowObj["inv_item_material_id"]
				
			});
			/*grid.eachColumn( 
				    function (columnId)
				    { 
				    	var _curCellVal = rowObj[columnId];
				    	//console.log(columnId);
				    	_tmpobj[columnId] = _curCellVal;
				    },
				    true // include hidden columns
		     );*/
			outputArr.push(_tmpobj);
		});
		
		/*console.log(" -------------- outputArr ---------------");
		console.log(outputArr);
		console.log("----- end -----------");*/
		
		fullOutputArr.push({rowsData:outputArr, fieldsData:fieldsValues, tableNumber:++tablesCounter});		
		tablesSnapshotObj.push({data:grid.serialize(), fieldsData:fieldsValues});
	});
	
	var webixTableGroupInx = "";
	if(massBalanceOwner() == "Experiment")
	{
		webixTableGroupInx = "1";
		if(domID == "webixMassBalanceTable2") webixTableGroupInx = "2";
		else if(domID == "webixMassBalanceTable3") webixTableGroupInx = "3";
	}
	//console.log("fullOutputArr",fullOutputArr);
	//returnObj = {isWebixTableHasRows:isTableHasRows, tablesSnapshot:tablesSnapshotObj, output: fullOutputArr};
	returnObj = {isWebixTableHasRows:1, webixTableGroupNumber:webixTableGroupInx, tablesSnapshot:tablesSnapshotObj, output: {tablesData:fullOutputArr, samplesList:allSelSamplesList, webixTableGroupNumber:webixTableGroupInx}};
	console.log("returnObj: ");
	console.log(JSON.stringify(returnObj));
	
	return returnObj;
}

function massBalanceOwner()
{
	var fc = $('#formCode').val();
	if(fc == 'Step') return 'Step';
	else if(fc == 'Experiment' || fc == 'ExperimentCP') return 'Experiment';
}

function hideWebixMassBalanceButton(tableID){

	var allData = [{
			code : 'SAMPLE_ID',
			val : $$("ddlSample" + tableID).getValue(),
			type : "AJAX_BEAN",
			info : 'na'
		}];
    
    var urlParam = "?formId=" +$('#formId').val()
    + "&formCode=" 	+ $('#formCode').val() + "&userId=" + $('#userId').val()
	+ "&eventAction=hideWebixMassBalanceButton&isNew="; // + $('#isNew').val();
    var data_ = JSON.stringify({
        action: "hideWebixMassBalanceButton",
        data: allData,
        errorMsg: ""
    });
    $.ajax({
        type: 'POST',
        data: data_,
        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) 
        {
        	if(obj.data[0].val == "1"){
        		$('#btnResToUpdate'+tableID).css('display', '');
        	}
        },
        error: handleAjaxError
    });

}

function uploadWebixMassBalanceData(domId,newvalue,colArr,tableID)
{
	showWaitMessage(getSpringMessage('pleaseWait'));
    // ajax call to the api service
   
    var data_ = {
	              "formId": $('#formId').val(),
	              "domId": domId,
	              "sampleId":newvalue
	        	};
	/*var data_ = {
            "sampleID": newvalue,
            "domId": domId
      	};*/
    $.ajax({
        type: 'POST',
        data: data_,
        url: "./onElementWebixMassBalanceCalcResUpload.request",
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
                
                var grid = $$(o.tableID);
                if (grid)
    				grid.destructor();
                	                
                var $div = $("div[name='parentWebixContainer'][id='"+domId+"']");
            	var _newData = prepareDataToWebixMassBalance(o.tableData, colArr, o.tableID);
		          refreshWebixTable(_newData,  tableID);
		          calcWebixMassBalance(tableID,domId);
            }
            hideWaitMessage();
        },
        error: handleAjaxError
    });
}