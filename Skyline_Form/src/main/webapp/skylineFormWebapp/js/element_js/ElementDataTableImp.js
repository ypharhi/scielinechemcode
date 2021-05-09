var ElementDataTableImp = {
    value_: function (val_) {
        return $(val_).val();
    },
    setvalue_: function (val_) {
        //important: should add attribute 'lastvalue' to the element imp in the poll (like in ElementAutoCompleteIdValDDLImp)
    },
    setDefaultValueForUnitTest_: function (val_) {
    }
};


/**
 * return from onAjaxCall()
 * @param obj
 * @returns
 */
function upDateElementDataTable(obj) {
    $('[id="' + obj.domId + '"]').dataTable().fnDestroy();
    var removeEvent = $('[id="' + obj.domId + '"]').DataTable();
    removeEvent.columns().every(function () {
        var that = this;
        $(that.footer()).find('input[class="firstString"]').unbind("keyup");
        $(that.footer()).find('input[class="firstString"]').val("");
    });
    var object = {};
    object.data = JSON.parse(obj.val.data.replace(/\r/g, ''));
    object.columns = JSON.parse(obj.val.columns.replace(/\r/g, ''));
    object.bDestroy = true;
    object.dom = 'Blfrtip';
    object.pagingType = 'full_numbers';
    object.buttons = ['copy', 'csv', 'excel', 'pdf', 'print'];
    if (typeof obj.val.pageLength !== 'undefined') {
        object.pageLength = obj.val.pageLength;
        object.lengthMenu = obj.val.lengthMenu;
    }
    var table = $('[id="' + obj.domId + '"]').DataTable(object);

    table.columns().every(function () {
        var that = this;
        searchDatatable(that);
    });

    if (typeof obj.isHidden !== 'undefined') {
        if (obj.isHidden.toLowerCase() == "false") {
            $('[id="' + obj.domId + '_wrapper"]').css('visibility', 'visible');
        } else {
            $('[id="' + obj.domId + '_wrapper"]').css('visibility', 'hidden');
        }
    }

    $('.dt-buttons').css('margin-bottom', '15px');
    fixLinkDatatable(obj.domId, table);
}

/**
 * return from onAjaxCall()
 * @param obj
 * @returns
 */
function upDateElementDataTable_clear(obj) {
    var table = $('[id="' + obj.domId + '"]').DataTable();
    table.clear().draw();
    if (typeof obj.isHidden !== 'undefined') {
        if (obj.isHidden.toLowerCase() == "false") {
            $('[id="' + obj.domId + '_wrapper"]').css('visibility', 'visible');
        } else {
            $('[id="' + obj.domId + '_wrapper"]').css('visibility', 'hidden');
        }
    }
}