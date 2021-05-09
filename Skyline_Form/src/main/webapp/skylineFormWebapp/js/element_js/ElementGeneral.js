var ElementGeneral = {
    value_: function (val_) {
        return $(val_).val();
    },
    setvalue_: function (val_) {
        //important: should add attribute 'lastvalue' to the element imp in the poll (like in ElementAutoCompleteIdValDDLImp)
        $(val_).val($(val_).attr('lastvalue'));
    },
    setDefaultValueForUnitTest_: function (val_) {
    },
    displayValue_: function (val_) {
    	try
    	{
	        var domId = $(val_).attr('id');
	        var element = $(val_);
	        var value = getAuditTrailValueForElementTextArea(element);
	        //console.log("ElementGeneral domID = " + domId + "  displayValue = " + value);
	        return value;
    	} 
    	catch(err) 
    	{
    		console.log("ERROR in displayValue_ in ElementGeneral domID = " + domId);
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
function upDateElementGeneral(obj) {
    if (typeof obj.value_ !== 'undefined') {
        $('[id="' + obj.domId + '"]').val(obj.value_);
    }
    if (typeof obj.isDisabled !== 'undefined') {
        if (obj.isDisabled.toLowerCase() == "false") {
            $('[id="' + obj.domId + '"]').removeClass('disabledclass');
        } else {
            $('[id="' + obj.domId + '"]').addClass('disabledclass');
            $('[id="' + obj.domId + '"]').css('border-color', '');
            $('[id="' + obj.domId + '"]').css('outline', '');
        }
    }
    if (typeof obj.isHidden !== 'undefined') {
        if (obj.isHidden.toLowerCase() == "false") {
            $('[id="' + obj.domId + '"]').css('display', 'block');
        } else {
            $('[id="' + obj.domId + '"]').css('display', 'none');
        }
    }
}

function getAuditTrailValueForElementTextArea(element)
{
	return element.val();
}