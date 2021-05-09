var ElementRadioImp = {
    value_: function (val_) {
        var domId = $(val_).attr('id');
        return $('[name="' + domId + '"]:checked').val();
    },
    setvalue_: function (val_) {
        //important: should add attribute 'lastvalue' to the element imp in the poll (like in ElementAutoCompleteIdValDDLImp)
    },
    setDefaultValueForUnitTest_: function (val_) {
    },
    displayValue_: function (val_) {
    	try
    	{
	        var domId = $(val_).attr('id');
	        var value = getAuditTrailValueForElementRadio(domId);
	        //console.log("ElementRadioImp domID = " + domId + "  displayValue = " + value);
	        return value;
    	} 
    	catch(err) 
    	{
    		console.log("ERROR in displayValue_ in ElementRadioImp domID = " + domId);
    		console.log(err);
    		return "";
    	}
    }
};

/**
 * return from onAjaxCall()
 * @param obj
 * @returns
 */
function upDateElementRadio(obj) {
    if (typeof obj.isDisabled !== 'undefined') {
        if (obj.isDisabled.toLowerCase() == "false") {
            $('[id="' + obj.domId + '"]').parent().removeClass('disabledclass');
            $('[id="' + obj.domId + '"]').parent().find('input').prop('disabled', false);
        } else {
            $('[id="' + obj.domId + '"]').parent().addClass('disabledclass');
            $('[id="' + obj.domId + '"]').css('border-color', '');
            $('[id="' + obj.domId + '"]').css('outline', '');
            $('[id="' + obj.domId + '"]').parent().find('input').prop('disabled', true);
        }
    }
    if (typeof obj.isHidden !== 'undefined') {
        if (obj.isHidden.toLowerCase() == "false") {
            $('[id="' + obj.domId + '"]').parent().css('visibility', 'visible');
        } else {
            $('[id="' + obj.domId + '"]').parent().css('visibility', 'hidden');
        }
    }
}

function getAuditTrailValueForElementRadio(domId)
{
	return $('[name="' + domId + '"]:checked').val();
}