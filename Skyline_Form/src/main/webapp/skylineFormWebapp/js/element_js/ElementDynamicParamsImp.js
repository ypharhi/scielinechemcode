var ElementDynamicParamsImp = {
    value_: function (val_) {
        var label, value, uom, active, obj = {},
            order = 0;
        var domId = $(val_).attr('id');
        var parentId = $('#parentId').val();
        $('#' + domId + ' .elementDynamicParamsRow').each(function () {
            label = $(this).find('input:eq(0)').val().trim();
            active = ($(this).is('[active="0"]') || !label) ? '0' : '1';
            value = ($(this).find('input:eq(1)').attr('realvalue')) ? $(this).find('input:eq(1)').attr('realvalue') : '';
            uom = $(this).find('select option:selected').val();
            obj[order] = {
                val: value,
                label: label,
                uom: uom,
                active: active,
                parentId: parentId
            };
            order++;
        });
        return JSON.stringify(obj);
    },
    setvalue_: function (val_) {

    },
    setDefaultValueForUnitTest_: function (val_) {
    }
};

/**
 * return from onAjaxCall()
 * @param obj
 * @returns
 */
function upDateElementDynamicParamsImp(obj) {
    var isDisabledDefined = (typeof obj.isDisabled !== 'undefined') ? true : false;
    if (isDisabledDefined) {
        if (obj.isDisabled.toLowerCase() == "false") {
            $('[id="' + obj.domId + '"] .inputAsLabel:not(:first)').prop('readonly', false);

            $('[id="' + obj.domId + '_parent"]').find('i').each(function () {
                $(this).parent().css('display', '');
            });

            $('[id="' + obj.domId + '"] .elementDynamicParamsRow').each(function () {
                $(this).find('input:eq(1), select').removeClass('disabledclass');
            });
        } else {
            $('[id="' + obj.domId + '"] .inputAsLabel:not(:first)').prop('readonly', true);

            $('[id="' + obj.domId + '_parent"]').find('i').each(function () {
                $(this).parent().css('display', 'none');
            });

            $('[id="' + obj.domId + '"] .elementDynamicParamsRow').each(function () {
                $(this).find('input:eq(1), select').addClass('disabledclass');
            });
        }
    }
    if (!isDisabledDefined || (isDisabledDefined && obj.isDisabled.toLowerCase() == "false")) {
        if (typeof obj.isReadonly !== 'undefined') {
            if (obj.isReadonly == "0") {
                $('[id="' + obj.domId + '"] .inputAsLabel:not(:first)').prop('readonly', false);

                $('[id="' + obj.domId + '_parent"]').find('i').each(function () {
                    $(this).parent().css('display', '');
                });
            } else {
                $('[id="' + obj.domId + '"] .inputAsLabel:not(:first)').prop('readonly', true);

                $('[id="' + obj.domId + '_parent"]').find('i').each(function () {
                    $(this).parent().css('display', 'none');
                });
            }
        }
    }
}

/**
 * Add a new row
 * @param domId
 * @returns
 */
function elementDynamicParamsImpAppendRow(domId) {
    $('[id="' + domId + '"]').append(
        '<div class=\"elementDynamicParamsRow\">' + '<input type=\"text\" class=\"inputAsLabel ml10\" placeholder=\"' + $('[id="' + domId + '"]').attr('placeholderLabel') + '\">' + '<input type=\"number\" class=\"ml10\">' + '<select class=\"ml10\" onchange=\"onElementDynamicParamsImpUomChange(this)\">' + getElementDynamicParamsUomOptions(domId) + '</select>' + '<a class=\"btn ml10\">' + '<i class=\"icon-minus-sign\" title=\"Delete\" onclick=\"elementDynamicParamsImpDeleteRow(this)\"></i>' + '</a>' + '</div>');
    
    $('[id="' + domId + '"] div.elementDynamicParamsRow:last input[type="number"]').on('input', function () {       
        $(this).attr('realvalue', this.value);
        $(this).attr('title', this.value);
    });
}

/**
 * Delete a row
 * @param input
 * @returns
 */
function elementDynamicParamsImpDeleteRow(input) {
    $(input).closest('div').css('display', 'none').attr('active', '0');
}

/**
 * Get UOM options
 * @param domId
 * @returns
 */
function getElementDynamicParamsUomOptions(domId) {
    var uomJSONArray = JSON.parse($('[id="' + domId + '_uom"]').val());
    var toReturn = '',
        i, uomJSONArrayLength = uomJSONArray.length;
    for (i = 0; i < uomJSONArrayLength; i++) {
        toReturn += '<option value=\"' + uomJSONArray[i].ID + '\">' + uomJSONArray[i].VAL + '</option>';
    }
    return toReturn;
}

/**
 * DynamicParams Validation
 * 
 * should be called from doSave
 * @returns
 */
function elementDynamicParamsImpValidation() {
    var label, value, mandatoryFlag = false,
        uniqueLabelFlag = true,
        labelArray = [],
        toReturnFlag = true;
    $('[element="ElementDynamicParamsImp"]').each(function () {
        mandatoryFlag = false;
        $(this).find('.elementDynamicParamsRow:not(.DynamicParamsTimePoint)').each(function () {
            if ($(this).attr('active') != '0') {
                label = $(this).find('input:eq(0)').val().trim();
                value = $(this).find('input:eq(1)').val().trim();
                mandatoryFlag = (label && value) ? true : mandatoryFlag;
                if (label) {
                    if (labelArray.indexOf(label) == -1) {
                        labelArray.push(label);
                    } else {
                        uniqueLabelFlag = false;
                        return false;
                    }
                }
            }
        });
        if (!uniqueLabelFlag) {
            displayAlertDialog(getSpringMessage('labelUniqueAlert'));
            toReturnFlag = false;
            return false;
        }
        if (!mandatoryFlag) {
            displayAlertDialog(getSpringMessage('PleaseFillTheRequiredFieldsDynamicParam'));
            toReturnFlag = false;
            return false;
        }
    });
    return toReturnFlag;
}

/**
 * onElementDynamicParamsImpUomChange
 * @returns
 */
function onElementDynamicParamsImpUomChange(input){
	$(input).siblings('input[type="number"]').val('');
	$(input).siblings('input[type="number"]').attr('realvalue','');
}