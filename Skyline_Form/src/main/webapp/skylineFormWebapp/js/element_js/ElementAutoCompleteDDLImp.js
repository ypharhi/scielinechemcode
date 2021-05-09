var ElementAutoCompleteDDLImp = {
    value_: function (val_, changeType) {
        if ($(val_).val() == null) {
            return "";
        }
        
        var val = getValueForElementAutoCompleteDDL(val_,changeType);
        return val;
    },
    setvalue_: function (val_) {
        $(val_).val($(val_).attr('lastvalue'));
        $(val_).trigger('chosen:updated');
    },
    setDefaultValueForUnitTest_: function (val_) {//$('#PROJECTTYPE_ID option:eq(1)').val()
    	var domId = $(val_).attr('id');
    	$('#'+domId).val($('#'+domId+" option:eq(1)").val());
        $('#'+domId).trigger('chosen:updated');
   },
    displayValue_: function (val_) {
    	try
    	{
	        var domId = $(val_).attr('id');
	        var element = $(val_);
	        var value = getAuditTrailValueForElementAutoCompleteDDL(element);
	        //console.log("ElementAutoCompleteDDLImp domID = " + domId + "  displayValue = " + value);
	        return value;
    	} 
    	catch(err) 
    	{
    		console.log("ERROR in displayValue_ in ElementAutoCompleteDDLImp domID = " + domId);
    		console.log(err);
    		return "";
    	}
    }/*,
    userLastSaveValue_: function (val_) {
        if ($(val_).val() == null) {
            return "";
        }
        var val = getValueForElementAutoCompleteDDL(val_);
        return val;
    }*/
};


//only use for testing call back this code should be removed
function dummyEcho(elementObj_, resultArray_, callback ) {
	
	var element = elementObj_.attr('element');
	var echoArg_ =  getValue_(element, elementObj_); //to send to server taken form above (ElementAutoCompleteDDLImp getValue_)
	console.log("echoArg_=" + echoArg_);
	 
    var urlParam =
        "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&eventAction=ECHO&echoArg=" +echoArg_ + "&isNew=" + $('#isNew').val();

    var data_ = JSON.stringify({
        action: "doSave",
        data: null,
        errorMsg: ""
    }); 
	
	 $.ajax({
	        type: 'POST',
	        data: data_,
	        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
	        contentType: 'application/json',
	        dataType: 'json',
	        success: function (obj) {
	        	var stringifyInfo = '{"formPreventSave":"' + elementObj_.attr("formPreventSave") +
	            '", "type":"' + elementObj_.attr("type") +
	            '", "saveType":"' + elementObj_.attr("saveType") +
	            '"}';
	        	var stringifyToPush = {
	        			    code: elementObj_.attr('id'),
	        	            val: echoArg_, //THIS IS A DUMMY it should be result from server
	        	            type: "AJAX_BEAN",
	        	            info: stringifyInfo
	                };
	        	resultArray_.push(stringifyToPush);
	        	//MUST CALL ->
	        	fireNextCallback(callback, resultArray_);
	        },
	        error: handleAjaxError
	    });
}

/**
 * return from onAjaxCall()
 * @param obj
 * @returns
 */
function upDateElementChosen(obj) 
{
	var element_info = $('#' + obj.domId + '_infoElemnetDialog');
	if(typeof element_info !== 'undefined' && element_info.length > 0) 
	{
		$('#' + obj.domId + '_infoElemnetDialog').find('p#CATALOG_SQL_INFO').html(obj.sqlInfo);    
	}
	$('[id="' + obj.domId + '"]').html(obj.val);
    if (typeof obj.isHidden !== 'undefined') {
        if (obj.isHidden.toLowerCase() == "false") {
            $('[id="' + obj.domId + '_chosen"]').css('display', 'block');
            $('[id="' + obj.domId + '_chosen"]').css('visibility', 'visible');
            if ($('[id="' + obj.domId + '"]').parent().hasClass('labelDiv')) {
                $('[id="' + obj.domId + '"]').parent().css('display',
                    'block');
            }
        } else {
            $('[id="' + obj.domId + '_chosen"]').css('display', 'none');
            if ($('[id="' + obj.domId + '"]').parent().hasClass('labelDiv')) {
                $('[id="' + obj.domId + '"]').parent().css('display',
                    'none');
            }
        }
    }
    if (typeof obj.isDisabled !== 'undefined') {
        if (obj.isDisabled.toLowerCase() == "false") {
            $('[id="' + obj.domId + '"]').prop("disabled", false);
            $('[id="' + obj.domId + '_chosen"]').find('a:first').css('border-color', '');
        } else {
            $('[id="' + obj.domId + '"]').prop("disabled", true);
        }
    }
    var last_val_ =  ""; 
    try {
    	last_val_ = $('[id="' + obj.domId + '"]').val().toString();
    } catch(e) {
    	console.log('exception last value in ddl obj.domId=' + obj.domId);
    }
    
    $('[id="' + obj.domId + '"]').attr('lastvalue',last_val_);
    $('[id="' + obj.domId + '"]').trigger('chosen:updated');
}

/**
 * cleanOnAllSelection
 * @param chosen
 * @returns
 */
function handleMultipleSelection(multipleSelectedValues,hasAllOption,chosen, ADD_ALL_ON_EMPTY_DATA) {
   var valIndex, valArray = $(chosen).val();
  // cleanOnAllSelection
   if(hasAllOption) {
	    //scenario 1
	    if ((valArray != null) && ((valIndex = valArray.indexOf('ALL')) !== -1)) { //there is value contains all
	    	 
	    		if ((',' + $(chosen).attr('lastvalue') + ',').indexOf(',ALL,') !== -1) { //was all
	    			// => add value to all so we cut the all
		    		valArray.splice(valIndex, 1);
		            $(chosen).val(valArray);
		            $(chosen).trigger('chosen:updated');
		           
	    		}
	    		
	    		if ((',' + $(chosen).attr('lastvalue') + ',').indexOf(',ALL,') === -1) {//no all before
	    			// => add all to value we show only all
	    			$(chosen).val(['ALL']);
	    			$(chosen).trigger('chosen:updated');
	                return;
	            }
	    }
	    
	   //scenario 2
	   if ((valArray != null) && ((valIndex = valArray.indexOf('ALL')) === -1)) { //there is value without all
	    	if ((',' + $(chosen).attr('lastvalue') + ',').indexOf(',ALL,') !== -1) { //was all
	    		//do nothing
	        }
	    }
	   
	    //scenario 3
	    if ((valArray == null) && (ADD_ALL_ON_EMPTY_DATA)) {    	
	    	 if ($(chosen).attr('lastvalue') != '') {
	    		 $(chosen).val([$(chosen).attr('lastvalue')]);    		
	    	 }
	    	 else {
	    		 $(chosen).val(['ALL']);
	    	 }
	    	 $(chosen).trigger('chosen:updated');
	    	 return;
	    }
   }

   // multipleSelectedValues rules auto selection...
   // for each multipleSelectedValue rule (rule is the items of multipleSelectedValues split by ; for example multipleSelectedValues =..;val1:val2,val3;..)
   // if the rule is defined in the form builder as follow: val1:val2,val3
   // we auto select val2 and val3 if val1 selected. 
   // NOTE: in this example val1 will be removed from the selection. in order to include it it should be defined as val1:val1,val2,val3
   if (multipleSelectedValues != null && multipleSelectedValues != "" && valArray != null && valArray != "") {
		var multipleValuesArr = multipleSelectedValues.split(";");
		for (i = 0; i < multipleValuesArr.length; i++) {
			for (j = 0; j < valArray.length; j++) {
				if (multipleValuesArr[i].split(":")[0].toLowerCase() == valArray[j]
						.toLowerCase()) {
					var additionalOptions = multipleValuesArr[i].split(":")[1];
					if (additionalOptions.indexOf(valArray[j]) < 0) {
						valArray.splice(valArray[j], 1);
					}
					var allOptions = valArray.concat(additionalOptions
							.split(","));
					$(chosen).val(allOptions);
					$(chosen).trigger('chosen:updated');
				}
	
			}
		}
	}
}

function getValueForElementAutoCompleteDDL(obj, changeType)
{
	getValueForElementAutoCompleteDDL
	var retVal = "";
	var o = $(obj);
	if(o[0].hasAttribute("saveValueAsJSON"))
	{
		var attrVal = o.attr("saveValueAsJSON");
		if(attrVal == 'true')
		{
			var values = [];
            values = o.find(':selected').map(function()
            {
                 return  $(this).val();
            })
            .get();
            //console.log("values: ");
            //console.log(values);
            if(typeof changeType !== 'undefined' && changeType !=null && changeType == 2 && values != null && values.length == 1 && values[0] == 'ALL') {  // YP 13022019 - prevent FirstOption ALL value to enetr into the DB (critical in id val ddl) on save (changeType = 2 in ajax dataflow changeType = 1 we need it ALL)
            	retVal = ""; 
            } else {
            	retVal = JSON.stringify(values);   
            }    
		}
		else
		{
			retVal = o.val().toString();//toString() is required on Multiple Select 
			if(typeof changeType !== 'undefined' && changeType !=null && changeType == 2 && retVal != null && retVal == 'ALL') {  // YP 13022019 - prevent FirstOption ALL value to enetr into the DB (critical in id val ddl) on save (changeType = 2 in ajax dataflow changeType = 1 we need it ALL)
				retVal = "";
			}
		}
	}
	else
	{
		retVal = o.val().toString();//toString() is required on Multiple Select 
		if(typeof changeType !== 'undefined' && changeType !=null && changeType == 2 && retVal != null && retVal == 'ALL') { // YP 13022019 - prevent FirstOption ALL value to enetr into the DB (critical in id val ddl) on save (changeType = 2 in ajax dataflow changeType = 1 we need it ALL)
			retVal = "";
		}
	}
	//console.log("retVal: " + retVal);
	return retVal;
}

function getAuditTrailValueForElementAutoCompleteDDL(element)
{
	var retVal = "";
    element.find(':selected').map(function()
    {
    	if(retVal == "")
    		retVal = $(this).text();
    	else
    		retVal += "," + $(this).text();
    	return  retVal;
    });     
	//console.log("getAuditTrailValueForElementAutoCompleteDDL retVal: " + retVal);
	return retVal;
}

/**
 * 
 * @param domId
 * @param selectValuesArry array of values that can not be remove (can be defined also as upper case for case insensitive)
 * @returns
 * Note: add patch directly in chosen code to avoid backspace click deletion by adding return... Chosen.prototype.keydown_backstroke=function(){return; 
 */
function forceMandatoryDDLValues(domId, selectValuesArry) {
	try {
		$("#" + domId + "_chosen").find(".search-choice").each(function() {
			var $li = $(this);
			var selectionText = $li.text();
			if(selectValuesArry.includes(selectionText.trim()) || selectValuesArry.includes(selectionText.toUpperCase().trim())) {
				$this_a = $li.find(".search-choice-close");
				$this_a.removeClass('search-choice-close');
				//$li.find("span").css('color', '#1779ba'); // if we want to change the color of the text
				console.log("forceMandatoryListValues remove the close option for: " + selectionText);
			}
		});
	} catch(e) {
		console.log("forceMandatoryListValues error");
	}
}