/** USED FOR DISPLAY MASS BALANCE TABLE FOR READ ONLY PURPOSE **/

function initWebixMassBalanceTableInfo(tableID, tableDivID, data)
{    
    console.log("initWebixMassBalanceTableInfo(): data for tableID: " + tableID);
	console.log(data);
	
	var isDisabled = true; 
			
    var tableData = [];
    var fieldsData = {};
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
						{ id:"description",	header:"Description", 	editor:"popup", width:300}
					];
    
    var _dataObj = funcParseJSONDataWX(data,true);
	//console.log(_dataObj);
	tableData = _dataObj["data"];
	fieldsData = _dataObj["fieldsData"];
	//console.log("fieldsData", fieldsData);
	
	var isStepSamplesChecked = (fieldsData["chkStepSamples"] == 0)?"":"checked";
	   
    var formMain = [	
    				{ margin:10, cols:[
									
									{ view:"text", 
									  name:"txtStream",
									  id:"txtStream" + tableID,									
									  value:'', 
									  label:"Stream",
									  labelWidth:"60",
									  cellType:"string"
									  //width: 500
									},
									{ view:"checkbox", 
									  name:"chkStepSamples",
									  //id:"chk"+tableID,
									  template:function(obj)
						  			  { 
							  			return '<div style="line-height:32px;"><label style="width: 100px;text-align: left;line-height:32px; margin-top: 2px;" class="webix_inp_label ">Step Samples</label>'+ 
							  			'<input id="chkStepSamples_'+tableID+'" style="margin-top: 14px;margin-left: 5px;" onclick="updateSamplesList(this.checked, \''+tableID+'\', true)" type="checkbox"  '+isStepSamplesChecked+'></div>';
					     			  },
									  customCheckbox:false,
									  width:130
									},
									{ 
										view:"combo", width:510,
										id:"ddlSample" + tableID,	
										label: 'Sample',
										labelWidth:"60",
										name:"ddlSample",
										mandatory:true,
										options:getSampleListOption(fieldsData)
									},
									{ view:"text", 
									  width:190,
									  id:"txtMass" + tableID, 
									  name:"txtMass",
									  label:"Mass",
									  labelWidth:"45",
									  cellType:"decimal"
									}
								]
				},
				{
					view:"datatable",
					id: tableID,
					name:"table",
					columns:_columnsArr,
					navigation:false,
		            select:"cell",
					autoheight:true,
					autowidth:true,
					editable:false,
					math: true,
					tooltip:true,
					data:tableData,
					disabled:isDisabled
				}
	];
	
	webix.ui({
		container:tableDivID,
		margin:30, cols:[
			{ margin:30, rows:[
				{ view:"form", id:"wbxForm_"+tableID, scroll:false, width:1630, elements: formMain, data:fieldsData,
				  disabled:isDisabled
				}
			]}
		]
	});
}
// END initWebixMassBalanceTableInfo() function

function getSampleListOption(fieldsData)
{
	var _id = fieldsData["ddlSample"];
	var _val = fieldsData["ddlSampleValue"];
	
	if(_id != null && _id != "")
	{
		if(_val == undefined || _val == null || _val == "")
		{
			var objlist = _webixMassBalanceDDLDataObject["ddlSample"];
			for(var i=0;i<objlist.length;i++)
			{
				var _obj = objlist[i];
				if(_obj["id"] == _id)
				{
					_val = _obj["value"];
					break;
				}
			}
		}
		return [{"id":_id,"value":_val}];
	}
	
	
	return [];
}

function getAllTablesDataForMassBalanceInfoForEachTable(args){
	var runNumber = args[0];
	$("*[formelement=1][element=ElementWebixMassBalanceCalcImp]").each(function(){
		var domId = $(this).attr("id");
		getAllTablesDataForWebixMassBalanceInfo(domId+"StepsInfo",runNumber);
	});
}

function getAllTablesDataForWebixMassBalanceInfo(domId,runNumber)
{
	if(runNumber==undefined){
		if($('#formCode').val() == 'ExperimentCP'){
			var cName = $('#userId').val()+':'+$('#formId').val()+':'+'$RUNNUMBER';
			var run = getCookie(cName);
			runNumber = run;
		} else{
			runNumber = "";
		}	 
	}
		//showWaitMessage(getSpringMessage('pleaseWait'));				
		var data_ = {
	            "formId": $('#formId').val(),
	            "domId": domId,
	            "runNumber": runNumber
	      	};
		$.ajax({
		  type: 'POST',
		  data: data_,
		  url: "./getWebixMassBalanceStepsInfo.request",
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
		      	//showWaitMessage(getSpringMessage('pleaseWait'));
		        try 
		        {
					var fullObject = JSON.parse(obj.data[0].val);             	          
			        console.log("WebixMassBalanceInfo",fullObject); 
			        var keys = Object.keys(fullObject);
			        var keysLength = keys.length;
			        var $containerDiv = $("div[id='"+domId+"Container']");
		        	$containerDiv.html('');//first, empty the existing tables. second, add all the steps mass balance
			         
			        if(keysLength > 0)
			        {
			        	keys.sort(); //sort keys
			        	for (var j = 0; j < keysLength; j++)
						{
			        		var key = keys[j];
			        		var currStepId = key;			        		
							var mainArr = fullObject[key];
							var isCharacterMassBalance = mainArr[0];
							var currStepName = mainArr[1];
							var tablesArr =  mainArr[2];
							//console.log(currStepName,tablesArr); 
							
							var isCheckboxChecked = "";
							if(isCharacterMassBalance == "1")
							{
								isCheckboxChecked = "checked";
								$('#hdnOldDefaultMassBalance').val("Step,"+currStepId+""); 
							}
							var checkboxDiv = "<div><label style='float: left;width: 15em;margin-top: 15px;display: table-cell' >Characteristic Mass Balance:</label></div>"
											+"<div style='float: left; margin-top: 1.8em;'>"
											+"<input type='checkbox' name='chkStepCharacterMassBalance_"+currStepId+"' chkParentId='"+currStepId+"' class='experimentDefaultMassBalanceClass' "
											+"       value='"+isCharacterMassBalance+"' onclick='checkForCharacterMassBalanceOnClick(this,\"0\")' "+isCheckboxChecked+">"
											+"</div>";
							var updateButton = '<div class="button" id=exp_update_res_'+currStepId+' onClick="updateExpResultsMassb(\''+currStepId+'\',\''+domId+'\')" style="float: left; margin-top: 1.4em;display:none">Update Results</div>';
							var $mainDiv = $containerDiv.append("<div style=\"margin-top: 15px;float: left;margin-right: 40px;\"><h2 class=\"cssStaticData\">"+currStepName+"</h2></div>"+checkboxDiv+"").append(updateButton);
							
							for(var i=0;i<tablesArr.length;i++)
							{
								var table = tablesArr[i];
								
								var tableDivID = table["tableDivID"];
								var tableID = table["tableID"];
								var tableData = table["tableData"];
								
								$mainDiv.append(table["tableDiv"]);
								
								initWebixMassBalanceTableInfo(tableID, tableDivID, tableData);
								ShowExpWebixMassBalanceButton(currStepId);
							}
					    }
			        	
			        	var oneIsChecked = $('.experimentDefaultMassBalanceClass').is(':checked');
			        	if(oneIsChecked)
			        	{
			        		$('.experimentDefaultMassBalanceClass:not(:checked)').prop('disabled',true);
			        	}
			        }
				} catch (e) {
					console.error("ERROR in getAllTablesDataForWebixMassBalanceInfo() ",e);
				}
		          
		      }
		      //hideWaitMessage();
		  },
		  error: handleAjaxError
		});
	
}

function ShowExpWebixMassBalanceButton(step_id){
	//showWaitMessage(getSpringMessage('pleaseWait'));
	var allData = getformDataNoCallBack(1);
    var stringifyToPush = {
			code : 'CURR_STEP_ID',
			val : step_id,
			type : "AJAX_BEAN",
			info : 'na'
		};
    
    allData = allData.concat(stringifyToPush);
    var urlParam = "?formId=" +$('#formId').val()
    + "&formCode=" 	+ $('#formCode').val() + "&userId=" + $('#userId').val()
	+ "&eventAction=ShowExpWebixMassBalanceBtn&isNew="; // + $('#isNew').val();
    var data_ = JSON.stringify({
        action: "ShowExpWebixMassBalanceButton",
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
        		$('#exp_update_res_'+step_id).css('display', '');
        		$('#exp_update_res_'+step_id).removeAttr('onclick');
        	}else if(obj.data[0].val == "2"){
        		$('#exp_update_res_'+step_id).css('display', '');
        		$('#exp_update_res_'+step_id).attr('disabled','disabled');
        		//$('#exp_update_res_').prop('disabled', true);
        		$('#exp_update_res_'+step_id).removeAttr('onclick');
        	}
        	
        },
        error: handleAjaxError
    });

}
function reloadExpWebix(domId){
	var $div = $("div[id=webixMassBalanceTableStepsInfoContainer]");
	$div.attr('isWebixTableHidden',true);
    $div.empty();
    getAllTablesDataForWebixMassBalanceInfo(domId);

	calculateMassBalanceFields(domId);
}
