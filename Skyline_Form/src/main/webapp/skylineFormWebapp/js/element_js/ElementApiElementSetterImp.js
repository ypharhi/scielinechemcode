var ElementApiElementSetterImp = {
    value_: function (val_) {
        if ($(val_).attr('type') == 'Checkbox') {
            if ($(val_).is(":checked")) {
                return "1";
            } else {
                return "0";
            }
        } else {
            return $(val_).val();
        }
    },
    setvalue_: function (val_) {

    }
};

/**
 * get values from Api (TODO cas api is hard coded and need to use the element configuration)
 * @param domId
 * @returns
 */
function getValuesFromApi(domId) {
    if ($('#' + domId).hasClass('disabledclass')) {
        return false;
    }
    showWaitMessage(getSpringMessage('pleaseWait'));
    var object, key, stringifyToPush;
    var allData = getformDataNoCallBack(1);
    allData = allData.concat(getParamMonitoringData()); // ParamMonitoring  data
    allData.push({
        code: "mainArgCode",
        val: domId,
        type: "AJAX_API_MAINARGCODE",
        info: "main element id use as argument"
    });
    allData.push({
        code: "mainArg",
        val: $("#" + domId).val(),
        type: "AJAX_API_MAINARG",
        info: "main element value use as argument"
    });
    allData.push({
        code: "apiCodes",
        val: $("#" + domId + "_apiCodes").val(),
        type: "AJAX_API_CODELIST",
        info: "api codes expected"
    });
    allData.push({
        code: "matchElements",
        val: $("#" + domId + "_matchElements").val(),
        type: "AJAX_API_MATCHELEMENTLIST",
        info: "element codes to update"
    });

	var data_ = JSON.stringify({
		action : "getValuesFromApi",
		data : allData,
		errorMsg : ""
	});
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + '&userId=' + $('#userId').val();
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./getValuesFromApi.request" + urlParam,
		contentType : 'application/json',
		dataType : 'json',
		success : function(obj) {
			var element, key;
			if (obj.errorMsg != null && obj.errorMsg != '') {
				displayAlertDialog(obj.errorMsg);
			} else {
				object = JSON.parse(obj.data[0].val);
				for (key in object) {
					if (object.hasOwnProperty(key)) {
						if ($('#' + key).attr('type') == 'Number') {
							$('#' + key).attr('realvalue', object[key]);
							$('#' + key).attr('title', object[key]);
						}
						$('#' + key).val(object[key]);
					}
				}
			}
			hideWaitMessage();
		},
		error : handleAjaxError
	});
}

/**
 * return from onAjaxCall()
 * @param obj
 * @returns
 */
function upDateElementApiElementSetter(obj) {
    var icon;
    if (typeof obj.isHidden !== 'undefined') {
        if (obj.isHidden.toLowerCase() == "false") {
            $('[id="' + obj.domId + '_wrapper"]').css('visibility', 'visible');
        } else {
            $('[id="' + obj.domId + '_wrapper"]').css('visibility', 'hidden');
        }
    }
    if (typeof obj.isDisabled !== 'undefined') {
        icon = $('[id="' + obj.domId + '"]').siblings('i');
        if (obj.isDisabled.toLowerCase() == "false") {
            icon.attr('onclick', icon.attr('function'));
            icon.removeClass("disabledclass");
        } else {
            icon.attr('onclick', '');
            icon.addClass("disabledclass");
        }
    }
}

