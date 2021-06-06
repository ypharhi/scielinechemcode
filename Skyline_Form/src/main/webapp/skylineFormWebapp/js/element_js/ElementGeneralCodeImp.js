var ElementGeneralCodeImp = {
	value_ : function(val_) {
		var toReturn = "";
		var element = $(val_);
		//CODE_STB_SELECT_TABLE
		if(element.attr('codeName') == 'CODE_STB_SELECT_TABLE') {
			var existsFlag_ = getAllCheckedSp();
			if(existsFlag_ == 1) {
				toReturn = $('#txtSpIdList').val();
			}
		}
		//CODE_STB_SELECT_TABLE
		if(element.attr('codeName') == 'CODE_STB_SELECT_TABLE') {
			var existsFlag_ = getAllCheckedSp();
			if(existsFlag_ == 1) {
				toReturn = $('#txtSpIdList').val();
			}
		}
		return toReturn;
	},
	setvalue_ : function(val_) {
		// important: should add attribute 'lastvalue' to the element imp in the
		// poll (like in ElementAutoCompleteIdValDDLImp)
	},
	setDefaultValueForUnitTest_ : function(val_) {
		return null;
	}
};

/**
 * return from onAjaxCall()
 * 
 * @param obj
 * @returns
 */
function upDateElementGeneralCode(obj) {
//    if (typeof obj.value_ !== 'undefined') {
//        $('[id="' + obj.domId + '"]').val(obj.value_);
//        //alert('experimenttype_id=' + obj.value_ );
//        $('#txtStageID').val("");
//        
//        showWaitMessage(getSpringMessage('pleaseWait'));
//        
//    	$.ajax
//		({ 
//			type: "POST",
//			//contentType: "application/json",
//			contentType: "application/x-www-form-urlencoded; charset=utf-8",
//			url: "./getStabValuesFromApi.request",
//			data: "&ajProduct=" +  obj.value_, 
//			dataType: "json",
//			success: function(data) 
//			{  
//				$('#tdStages').html(data.tdStage);
//				if ( $('#STAGE_ID').length )
//				{
//					$('#STAGE_ID').on('change', function () { 
//						if (isControlDown === true)
//						{	
//							selectionDone = true;
//							return false;
//						}
//							
//						stageChange();
//					});
//				}
//				
//				if (data.txtStageID && data.txtStageID != "")
//				{	// Stability stages for product template
//					$('#txtStageID').val(data.txtStageID);				
//					//toggleCopyAndRunNumber(false);
//					//comboChangeAjax('ddlSourceRun', passExtraParam(1));
//				}
//				hideWaitMessage();
//			 },
//			error: handleAjaxError
//		}); 
//        
//        
//    }
}

function stageChange()
{
    $('#STAGE_ID').blur();	
//	toggleCopyAndRunNumber(true); // disable
			
	var values = "";
	$('#STAGE_ID option:selected').each( function(index) {
		if(values != "")
		{
			values = values + ","
		}
		values = values + $(this).val();
	});
	
	$('#txtStageID').val(values); 
	
	if ( $('#txtStageID').val() == "" ) // no stages were selected
		return;
		
	// *** this code runs only if stages were selected ***//														 
    // load runs list to copy parameters/mats from														 
//	comboChangeAjax('ddlSourceRun', passExtraParam(1));
//	toggleCopyAndRunNumber(false);	
}

function getAuditTrailValueForElementTextArea(element)
{
	return element.val();
}


function allClick()
{ 
	//getElementsByAttribute(oElm, strTagName, strAttributeName, strAttributeValue)
	var tdSpArray = filterElementsByAttribute (document.getElementsByTagName('td'), 'checkSpFlag', null, false, null);
	
	 for (var i = 0; i< tdSpArray.length; i++)
	 {
		tdSpArray[i].innerHTML = '<img border="0" src="../skylineFormWebapp/images/available.png" width="13px" height="13px">';
		tdSpArray[i].setAttribute('checkSpFlag', 1);			
	 } 
}


function noneClick()
{ 
	//getElementsByAttribute(oElm, strTagName, strAttributeName, strAttributeValue)
	var tdSpArray = filterElementsByAttribute (document.getElementsByTagName('td'), 'checkSpFlag', null, false, null);
	 
	 for (var i = 0; i< tdSpArray.length; i++)
	 {
		 tdSpArray[i].innerHTML = '&nbsp;';
		 tdSpArray[i].setAttribute('checkSpFlag', 0);
	 }
}

function checkSp(obj)
{ 
	if(obj.getAttribute('checkSpFlag') == 1) //exists
	{
		obj.innerHTML = '&nbsp;';
		obj.setAttribute('checkSpFlag', 0);
	}
	else
	{
		 obj.innerHTML = '<img border="0" src="../skylineFormWebapp/images/available.png" width="13px" height="13px">';
		 obj.setAttribute('checkSpFlag', 1);
	}  
}

function openCreateSampleDialog() // kd 20112019 "Taro develop"
{
	var formId = $("#formId").val().trim();
	var domId = 'ExperimentMain';// 'samples';
	if($('#prevDialog iframe').length !=0)//whether the searchDialog is already opened
		return;
 
	var left, top;
	dialogHeight = 900;
    dialogWidth = 1600;   
	left = 150; 
	top = 0; 
	console.log("left",left);
    console.log("top",top);
    
    var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=SampleSelect"
    + "&formId=-1" /// + sampleFormId
    + "&PARENT_ID=" + formId
    //+ '&urlCallParam=' + replaceDTUrlCallParamVal($('[id="' + domId + '_urlCallParam"]').val(), formId) +
    + "&userId=" + $('#userId').val();
    
	// open iframe inside dialog
	var $dialog = $(
			'<div id="prevDialog" style="overflow-y: hidden;""></div>')//prevDialog
			.html(
					'<iframe style="border: 0px;width:100%;height:100%" src="'
							+ page + '"></iframe>')
			.dialog(
					{
						autoOpen : false,
						modal : true,
						height : dialogHeight,
						width : dialogWidth,
						// title: title,
						close : function() {
							$('#prevDialog iframe').attr('src', 'about:blank');
							$('#prevDialog').remove();
						},
						open: function(event, ui) 
						{
			                $(this).parent().css({'top': top,'left':left});
			            }
					});

	$dialog.dialog('option', 'dialogClass', 'noTitleStuff')
			.dialog('open');
}

function getAllCheckedSp()
{ 
	var existsFlag = 0;
	//getElementsByAttribute(oElm, strTagName, strAttributeName, strAttributeValue)
	var tdSpArray = filterElementsByAttribute (document.getElementsByTagName('td'), 'checkSpFlag', 1, false, null);
	var values = "";
	 
	 for (var i = 0; i< tdSpArray.length; i++)
	 {
		if(values != "")
		{
			values = values + ","
		}
		 values = values + tdSpArray[i].id; 
	 } 
	 $('#txtSpIdList').val(values);
	 
	 if(values != "")
	 {
		existsFlag = 1;
	 }
	 return existsFlag;
}


function filterElementsByAttribute (sourceNodeList, attrName, attrValue, isPartOfValue, whichPartOfValue)
{
	var hasValue = (attrValue != null && attrValue != "") ? true:false;
	var partOfVal = (whichPartOfValue == null || (whichPartOfValue != "end" && whichPartOfValue != "start")) ? "contain":whichPartOfValue;
	var filteredNodeList = new Array(); 
	if (sourceNodeList == null) return null;
	
	for (var i = 0; i < sourceNodeList.length; i++)
	{
		if (sourceNodeList[i].attributes[attrName] != null)
		{
			if (!hasValue)
			{
				filteredNodeList.push(sourceNodeList[i]);
			}
			else if (!isPartOfValue && sourceNodeList[i].getAttribute(attrName) == attrValue)
			{ 
				filteredNodeList.push(sourceNodeList[i]);			
			}
			else if (whichPartOfValue == "contain" && sourceNodeList[i].attributes[attrName].value.indexOf(attrValue) > -1)
			{
				filteredNodeList.push(sourceNodeList[i]);			
			}
			else // whichPartOfValue == ("end" || "start")
			{ 
				var len = attrValue.length;
				if ((whichPartOfValue == "end" && sourceNodeList[i].attributes[attrName].value.slice(-1 * len) == attrValue) ||
					(whichPartOfValue == "start" && sourceNodeList[i].attributes[attrName].value.slice(0, len) == attrValue))
				{
					filteredNodeList.push(sourceNodeList[i]);			
				}				
			}
		}
	}

	return filteredNodeList;
}

function getParametersData() {
	
	var selMap = {};
	// loop through each TR
	$('.designreport-parameters-row').each(function(i) {
		var $tr = $( this );
		var $chkbox = $tr.find('input.designReportParameter');
		var $label = $tr.find('label.designReportParameter');
		var $select = $tr.find("select.designReportParameter");
		
		if(($chkbox.length > 0 && $label.length > 0 && $select.length > 0)
				&&
			($chkbox.is(":checked") && $select.find('option:selected').length > 0) // add to map only if checkbox checked and minimum one value selected
		)
		{
			var label = $.trim($label.text());
//			label = label.substr(0,label.length-1);	//colons removed
			
			$($select.find('option:selected')).each(function() {
				var _val = $.trim($(this).text());
				var _arr = [];
				if(Object.keys(selMap).length > 0 && selMap.hasOwnProperty(_val))
				{
					_arr = selMap[_val];
					
				}
				_arr.push(label);
				selMap[_val] = _arr;				
			});		
		}
	});	
	
	console.log(JSON.stringify(selMap));
	return JSON.stringify(selMap);
}

function setParametersData() {
	
	var savedValue = $('#parametersDesign').val();
	if(checkIfJSON(savedValue))
	{
		var savedMap = JSON.parse(savedValue);	
		var newMap = {};
	
		try
		{
			// first reverse saved data into more suitable object/map
			for(key in savedMap)
			{
				var _arr = savedMap[key].split(',');
				var _arrLength = _arr.length;
				
				for(var i=0;i<_arrLength;i++)
				{
					var _newArr = [];
					var _val = _arr[i];
					if(Object.keys(newMap).length > 0 && newMap.hasOwnProperty(_val))
					{
						_newArr = newMap[_val];
						
					}
					_newArr.push(key);
					newMap[_val] = _newArr;
				}
			}
			console.log(JSON.stringify(newMap));
		}
		catch(e)
		{
			console.error(e);
		}
	
		if(Object.keys(newMap).length > 0) {
				
			try
			{
				// took into consideration that all parameters step list data is the same,
				//   its enough to use data from the first one
				var selectionlist = {};
				var firstTR = $('.designreport-parameters-row').eq(0);
				var firstList = $("select.designReportParameter", firstTR);		
				$(firstList.find('option')).each(function() {
					var $this = $(this);
					selectionlist[$.trim($this.text())] = $this.val();
				}); 
				
				$('.designreport-parameters-row').each(function(i) {
					var $tr = $( this );
					var $chkbox = $tr.find('input.designReportParameter');
					var $label = $tr.find('label.designReportParameter');
					var $select = $tr.find("select.designReportParameter");
					
					if($chkbox.length > 0 && $label.length > 0 && $select.length > 0) {
						
						var label = $.trim($label.text());
//						label = label.substr(0,label.length-1);	colons removed
						
						if(newMap.hasOwnProperty(label))
						{					
							$chkbox.prop('checked', true);
							var _arr = newMap[label];
							var _arrLength = _arr.length;
							var multiArr = [];
							for(var i=0;i<_arrLength;i++) 
							{
								var _val = _arr[i];
								if(selectionlist.hasOwnProperty(_val))
								{
									multiArr.push(selectionlist[_val]);
								}
							}
							$select.val(multiArr).trigger('chosen:updated');
						}
					}
				});	
			}
			catch(e){
				console.error(e);
			}
		}
	}
}









