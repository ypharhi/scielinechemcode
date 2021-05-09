/**
 * Init (all) element UOM imp * 
 * 
 * The function called from generalBl.js's initForm() at the loading of the page.
 * 
 * @returns
 */
function elementUOMImpInit() {
    elementUOMImpDisplayPrecision(); // update element with his UOM's precision
    elementUOMImpBindChange(); //  Bind click for (all) UOM elements
    initLastUomValue();
}

/**
 * update element with his UOM's precision
 * @returns
 */
function elementUOMImpDisplayPrecision() {
    var precision, elementId, element;
    $.ajax({
        type: 'POST',
        data: '{"action" : "initDateFormatter","data":[],"errorMsg":""}',
        url: "./getPrecision.request",
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) {
            if (obj.errorMsg != null && obj.errorMsg != '') {
                displayAlertDialog(obj.errorMsg);
            } else {
                prop.precision = obj.data[0].val;
                $('[ElementUOM]').each(function () {
                    elementId = $(this).attr('elementId');
                    element = (elementId) ? $('[id="' + elementId + '"]') : '';
                    if (elementId && element.length && element.val()) {
                        if (!element.is('[precision]') || element.attr('precision') == '') {
                            precision = $(this).find('option:selected').attr('precision');
                            if (precision != '') {
                                precision = (Number(precision) > prop.precision) ? prop.precision : precision;
                                $('[id="' + elementId + '"]').val(Number($('[id="' + elementId + '"]').val()).toFixed(precision));
                            }
                        }
                    }
                });
            }
        },
        error: handleAjaxError
    });

}

/**
 * Bind 'change' for (all) UOM elements
 * 
 * @returns
 */
function elementUOMImpBindChange() {
    $('[ElementUOM]').on("change", function () {
        var selectedOption = $(this).find('option:selected');
        var selectedFactor = $(selectedOption).attr('factor');
        var selectedPrecision = $(selectedOption).attr('precision');
        var lastUOMValueOption = $(this).find('option[value="' + $(this).attr('lastuomvalue') + '"]');
        var lastIsNormal = $(lastUOMValueOption).attr('isnormal');
        var lastFactor = $(lastUOMValueOption).attr('factor');
        
        var elementIdArr = [];
        var elementsIdString = $(this).attr('elementsIdString');
        var elementId = $(this).attr('elementId');
        if(elementsIdString != null && elementsIdString != "")
        {
        	elementIdArr = elementsIdString.split(',');
        }
        else
        {
        	elementIdArr[0] = elementId;
        }
        for(var i=0;i<elementIdArr.length;i++)
        {
	        var curElemId = elementIdArr[i];
        	var element, elementPrecision, precision;
	        var elementValBigNumber, selectedFactorBigNumber, lastFactorBigNumber, bigNumberResult;
	        curElemId = (curElemId && $('[id="' + curElemId + '"]').length) ? curElemId : '';
	        if (!selectedFactor || !lastFactor || !curElemId) { // factor is undefined or 0
	            return;
	        }
	        element = $('[id="' + curElemId + '"]');
	        if (element.hasClass('disabledclass')) {
	            if (element.attr('realvalue') != '') {
	                elementPrecision = element.attr('precision');
	                precision = ((typeof elementPrecision !== 'undefined') && (elementPrecision != '')) ? elementPrecision : (selectedPrecision != '') ? selectedPrecision : '';
	                precision = (precision != '') ? ((Number(precision) < prop.precision) ? precision : prop.precision) : '';
	
	                elementValBigNumber = new BigNumber(element.attr('realvalue'));
	                selectedFactorBigNumber = new BigNumber(selectedFactor);
	                lastFactorBigNumber = new BigNumber(lastFactor);
	
	                if (lastIsNormal == '1') {
	                    bigNumberResult = elementValBigNumber.div(selectedFactorBigNumber);
	                } else {
	                    bigNumberResult = elementValBigNumber.mul(lastFactorBigNumber).div(selectedFactorBigNumber);
	                }
	
	                element.attr('realvalue', (countDecimals(Number(bigNumberResult.toString())) > prop.precision) ? bigNumberResult.toFixed(prop.precision) : bigNumberResult.toString());
	                element.val((precision != '') ? bigNumberResult.toFixed(precision) : bigNumberResult.toString());
	                element.attr('title', element.attr('realvalue'));
	            }
	        } else {
	            element.val('');
	            element.attr('realvalue', '');
	            element.attr('title', '');
	        }
        }
        $(this).attr('lastuomvalue', selectedOption.val());
    });
}

/**
 * update lastuomvalue if empty
 * @returns
 */
function initLastUomValue() {
	$('[ElementUOM]').each(function () {
		if($(this).attr('lastuomvalue') === ''){
			$(this).attr('lastuomvalue', $(this).find('option:selected').val());
		}
		
	});
}