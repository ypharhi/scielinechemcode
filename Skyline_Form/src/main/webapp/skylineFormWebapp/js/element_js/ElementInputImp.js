var ElementInputImp = {
    value_: function (val_) {
    	try
    	{
	        var element = $(val_);
	        var value = getValueForElementInput(element);
	        return value;
    	} 
    	catch(err) {}
    },
    setvalue_: function (val_) {
        //important: should add attribute 'lastvalue' to the element imp in the poll (like in ElementAutoCompleteIdValDDLImp)
        $(val_).val($(val_).attr('lastvalue'));
    },
    setDefaultValueForUnitTest_: function (val_) {
    	 var formId = $('#formId').val();
    	 var element = $(val_);
    	 if (element.attr('type') == 'Checkbox') 
    	    {
    		 $(val_).prop('checked', true);
    	    } 
    	    else if (element.hasClass('date-picker')) 
    	    {
    	    	var d = new Date();
    	        if (element.val() == '00/00/0000') 
    	        {
    	        	$(val_).val(moment(d).format(prop.dateFormat.userDateFormatClient));
    	        }
    	    } 
    	    else if (element.attr('type') == 'Number') 
    	    {
    	    	 $(val_).val(formId) ;
    	    }
    	    else if (element.attr('type_') == 'time') 
    	    {
    	    	  var d = new Date();
    	    	  var h = d.getHours();
    	    	  var m = d.getMinutes();
    	    	  if ( h < 10) {
    	    		  h = "0" +  h;
    	    		  }
    	    	  if ( m < 10) {
    	    		  m = "0" +  m;
    	    		  }
    	    	 $(val_).val(h+':'+m) ;
    	    }
    	    else{
    	    	$(val_).val('unittest'+formId); 
    	    }
    },
    displayValue_: function (val_) {
    	try
    	{
	        var element = $(val_);
	        var id = $(val_).attr('id');
	        var value = getAuditTrailValueForElementInput(element);
	        //console.log("ElementInputImp domID = " + id + "  displayValue = " + value);
	        return value;
    	} 
    	catch(err) 
    	{
    		console.log("ERROR in displayValue_ in ElementInputImp domID = " + id);
    		console.log(err);
    		return "";
    	}
    }
};

/**
 * return from onAjaxCall()
 * 
 * @param obj
 * @returns
 */
function upDateElementGenericInput(obj) {
    var element = $('[id="' + obj.domId + '"]');
    var isDate = (element.hasClass('date-picker')) ? true : false;
    if (typeof obj.value_ !== 'undefined') {
        element.val(obj.value_);
    }
    if (typeof obj.isDisabled !== 'undefined') {
        if (obj.isDisabled.toLowerCase() == "false") {
            element.removeClass('disabledclass');
            if (element.attr('type') == 'checkbox') {
                element.prop("disabled", false);
            }
        } else {
            element.addClass('disabledclass');
            element.css('border-color', '');
            element.css('outline', '');
            if (element.attr('type') == 'checkbox') {
                element.prop("disabled", true);
            }
        }
    }
    if (isDate) {
        if (element.hasClass('disabledclass')) {
            element.datepicker('disable');
        } else {
            element.datepicker('enable');
        }
        element = element.parent();
    }
    if (typeof obj.isHidden !== 'undefined') {
        if (obj.isHidden.toLowerCase() == "false") {
            element.css('display', 'block');
        } else {
            element.css('display', 'none');
        }
    }
}

/**
 * Init Email validation
 * @param domId
 * @returns
 */
function onEmailInputKeyUp(domId) {
    $("#" + domId).keyup(function () {
        isEmail(this);
    });
    $("#" + domId).after("<br><span id='" + domId + "_Message' style='display:none;color: #a94442; margin-left: 5px;margin-top: 5px;margin-bottom: 10px;'>Enter a valid email</span>");
    $("#" + domId).css('outline', 'none');
}

/**
 * Init checkBox
 * @param domId
 * @returns
 */
function initCheckBox(domId) {
    if ($("#" + domId).val() == "1") {
        $("#" + domId).prop('checked', true);
    }
    if ($("#" + domId).hasClass('disabledclass')) {
        $("#" + domId).prop('disabled', true);
    }
}

/**
 * Init input time validation
 * @param domId
 * @returns
 */
function onTimeInputKeyUp(domId) {
    $("#" + domId).keyup(function () {
        isTime(this);
    });
    $("#" + domId).after("<span id='" + domId + "_Message' style='display:none;color: #a94442; margin-left: 5px;margin-top: 5px;margin-bottom: 10px;'>Enter a valid time</span>");
    $("#" + domId).css('outline', 'none');
}

function onTextInputKeydown(domId)
{
	$("#" + domId).keydown(function () 
	{
		var str = $(this).attr('alphanumAllowChars');		
		if(str != "")
			$(this).alphanum({allow:updateAllows(str)});
    });
}

function getValueForElementInput(element)
{
	if (element.attr('type') == 'Checkbox') 
    {
        if (element.is(":checked")) 
        {
            return "1";
        } 
        else 
        {
            return "0";
        }
    } 
    else if (element.hasClass('date-picker')) 
    {
        if (element.val() == '00/00/0000') 
        {
            return "";
        }
        return moment(element.val(), prop.dateFormat.userDateFormatClient, true).format(prop.dateFormat.savedConventionDbDateFormat);
    } 
    else if (element.attr('type') == 'Number') 
    {
        if (element.is('[realvalue]') && element.attr('realvalue') != '' && element.val() != '') {
            return element.attr('realvalue');
        } 
        else 
        {
            return element.val();
        }
    } 
    else 
    {
        return element.val();
    }
}

function getAuditTrailValueForElementInput(element)
{
	if (element.attr('type') == 'Checkbox') 
    {
        if (element.is(":checked")) 
        {
            return "Yes";
        } 
        else 
        {
            return "No";
        }
    } 
    else if (element.hasClass('date-picker')) 
    {
        if (element.val() == '00/00/0000') 
        {
            return "";
        }
        return moment(element.val(), prop.dateFormat.userDateFormatClient, true).format(prop.dateFormat.savedConventionDbDateFormat);
    } 
    else if (element.attr('type') == 'Number') 
    {
        if (element.is('[realvalue]') && element.attr('realvalue') != '' && element.val() != '') {
            return element.attr('realvalue');
        } 
        else 
        {
            return element.val();
        }
    } 
    else 
    {
        return element.val();
    }
}

/**
 * 
 * @param domId
 * @returns
 */
function onKeyUpTooltip(domId) {
	$("#" + domId).parent().attr('title', $("#" + domId).val());
    $("#" + domId).keyup(function () {
    	 $("#" + domId).attr('title', $("#" + domId).val());
    });
}

/**
 * 
 * @param domId,val
 * @returns
 */
function onKeyUpTooltipFreeText(domId,val) {
	$("#" + domId).parent().attr('title', val);
    $("#" + domId).keyup(function () {
    	 $("#" + domId).attr('title', val);
    });
}