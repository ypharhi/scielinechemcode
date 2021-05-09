var ElementParamMonitoringImp = {
    value_: function (val_) {
        var inputId, inputVal, inputTxt, obj = {};
        var domId = $(val_).attr('id');

        // collects all inputs' value which are not empty
        $('#' + domId + ' input').each(function () {
            inputId = $(this).attr('id');
            inputVal = $(this).attr('realvalue');
            if (inputId) {
                inputId = inputId.substring(0, inputId.lastIndexOf("_" + domId));
                obj[inputId] = {
                    val: inputVal,
                    formid: $(this).attr('formid')
                };
            }
        });

        // collects all selects' value which are not empty
        // and their compatible input is not empty
        $('#' + domId + ' select').each(function () {
            inputId = $(this).attr('id');
            inputVal = $(this).val();
            inputTxt = $(this).find('option:selected').text();
            if (inputId) {
                inputId = inputId.substring(0, inputId.lastIndexOf("_" + domId));
                obj[inputId] = {
                    val: inputVal,
                    text: inputTxt
                };
            }
        });

        return JSON.stringify(obj);
    },
    setvalue_: function (val_) {
        //important: should add attribute 'lastvalue' to the element imp in the poll (like in ElementAutoCompleteIdValDDLImp)
    },
    setDefaultValueForUnitTest_: function (val_) {
    }
};

function getParamMonitoringData() {
    var values = [],
        element,
        stringifyInfo,
        stringifyToPush,
        type;
    $('[element="ElementParamMonitoringImp"] input, [element="ElementParamMonitoringImp"] select').each(function () {
        if (this.tagName == 'INPUT') {
            element = 'ElementInputImp';
            type = $(this).attr("type");
        } else if (this.tagName == 'SELECT') {
            element = 'ElementAutoCompleteDDLImp';
            type = '';
        }
        stringifyInfo = '{"formPreventSave":"0", "type":"' + type +
            '", "saveType":"' + $(this).attr("saveType") + '"}';
        stringifyToPush = {
            code: $(this).attr('id'),
            val: getValue_(element, this),
            type: "AJAX_BEAN",
            info: stringifyInfo
        };
        values.push(stringifyToPush);
    });
    return values;
}