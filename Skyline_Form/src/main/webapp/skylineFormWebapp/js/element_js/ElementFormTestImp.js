var ElementFormTestImp = {
    value_: function (val_) {
        var obj = {};
        var domId = $(val_).attr('id');

        // collects all formTest's value
        $('#' + domId + ' input').each(function () {
            obj[$(this).attr('formtestconfig_id')] = {
                val: $(this).val(),
                uom_id: $('[id="' + $(this).attr('id') + '_uom' + '"]').val()
            };
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
 * 
 * @param obj
 * @returns
 */
function upDateElementFormTestImpl(obj) {
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
            $('[id="' + obj.domId + '"]').css('visibility', 'visible');
        } else {
            $('[id="' + obj.domId + '"]').css('visibility', 'hidden');
        }
    }
}