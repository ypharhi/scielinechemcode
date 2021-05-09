var ElementRangeFilterImp = {
    value_: function (val_) {
        if ($(val_).val() == null) {
            return "";
        }
        var id = $(val_).attr('id');
        var firstInput = ($(val_).find('input:not("[type=hidden]"):first').val() == '00/00/0000') ? '00/00/0000' :
            moment($(val_).find('input:not("[type=hidden]"):first').val(), prop.dateFormat.userDateFormatClient, true)
            .format(prop.dateFormat.savedConventionDbDateFormat);
        var secondInput = ($(val_).find('input:not("[type=hidden]"):last').val() == '00/00/0000') ? '00/00/0000' :
            moment($(val_).find('input:not("[type=hidden]"):last').val(), prop.dateFormat.userDateFormatClient, true)
            .format(prop.dateFormat.savedConventionDbDateFormat); 
        var selectedDateFilter = "NA";
        if($('#' +id+ '_optionItem').length > 0) {
        	selectedDateFilter = $('#' +id+ '_optionItem').val();
        }
        return firstInput + ';' + secondInput+';'+selectedDateFilter;

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
function upDateElementRangeFilterImp() {

}

/**
 * initRangeDatePicker
 * @returns
 */
function initRangeDatePicker(datepickerFormat, doOnChangeJSCall) {
    $('.elementrange input:not("[type=hidden]")').datepicker({
        showOn: "button",
        showButtonPanel: true,
        dateFormat: datepickerFormat,
        buttonImage: "../skylineFormWebapp/images/calendar.png",
        buttonImageOnly: true,
        changeMonth: true,
        changeYear: true,
        disabled: false,
        defaultDate: null,
        altFormat: ['dd-mm-yy', 'ddmmyy'], // ef task 7078
        constrainInput: false, // ef task 7078
        beforeShow: function (input) {
            var objId = $(input).attr('id');
            var min=($(input).attr('min') != null && $(input).attr('min') !="") ? moment($(input).attr('min'),prop.dateFormat.userDateFormatClient, true) : null;//'DD-MMM-YYYY'
            var max=($(input).attr('max') != null && $(input).attr('max') !="") ? moment($(input).attr('max'),prop.dateFormat.userDateFormatClient, true) : null;
           
            var maxDate = new Date(max);
            $(input).datepicker('option','maxDate',max!=null?maxDate:null);
            
            var minDate = new Date(min);
           $(input).datepicker('option','minDate',min!=null?minDate:null);
            overrideDefaultButtonPanel(input, objId, null, false);
        },
        onSelect: function (dateValue) {
            
            if ($(this).hasClass('date-from')) {
                $(this).siblings('input:not("[type=hidden]")').datepicker("option", "minDate", dateValue);               
                if ($(this).siblings('input:not("[type=hidden]")').val() == '') {
                	$(this).siblings('input:not("[type=hidden]")').val(dateValue);
                }
            } else//selected toDate element
            	{
            		$(this).siblings('input.date-from').datepicker("option", "maxDate", dateValue);
            		 if ($(this).siblings('input.date-from').val() == '') {
                     	$(this).siblings('input.date-from').val(dateValue);
                     }
            	}
            eval(doOnChangeJSCall);
        },
        onChangeMonthYear: function (year, month, inst) {}
    }).on('change', function () {
        if (moment($(this).parent().find('input:not("[type=hidden]"):first').val(), prop.dateFormat.userDateFormatClient, true) >
				moment($(this).parent().find('input:not("[type=hidden]"):last').val(), prop.dateFormat.userDateFormatClient, true)) {
        	$(this).parent().find('input:last').val($(this).parent().find('input:not("[type=hidden]"):first').val());        	
        }    	
        if (validDateWithMomentJS(this)) {
            eval(doOnChangeJSCall);
        } else {
        	$(this).parent().find('input:not("[type=hidden]"):last').val('00/00/0000');
        	$(this).parent().find('input:not("[type=hidden]"):first').val('00/00/0000');
        	eval(doOnChangeJSCall);
        }
//        if ($(this).hasClass('date-from')) {
//            $(this).siblings('input').datepicker("option", "minDate", $(this).val());
//            if ($(this).siblings('input').val() == '') {
//            	$(this).siblings('input').val($(this).val());
//            }           
//        }
    });
    $('.hasDatepicker').prop('readonly', false); // ef task 7078
}