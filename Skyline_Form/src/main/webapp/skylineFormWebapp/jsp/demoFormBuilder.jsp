<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!doctype html>
<html lang="en">

<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Demo Form Builder</title>
<link rel="icon" href="../skylineFormWebapp/images/favicon.png?<spring:message code="Env" text="" />" />
<link href="../skylineFormWebapp/CSS/comply_theme/Skyline_9.css" rel="stylesheet" type="text/css">
<link href="../skylineFormWebapp/CSS/comply_theme/jquery-ui.custom.css" rel="stylesheet" type="text/css" media="all" />
 <link href="../skylineFormWebapp/CSS/comply_theme/mbcsmbmcp_form.css" rel="stylesheet" type="text/css" />	
<link href="../skylineFormWebapp/CSS/comply_theme/demo_table_jui_form.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" href="../skylineFormWebapp/CSS/layoutFormBuilderCSS.css">
<link href="../skylineFormWebapp/CSS/comply_theme/app_formBuilder.css" rel="stylesheet" type="text/css">
<link href="../skylineFormWebapp/CSS/comply_theme/tmpCSS.css" rel="stylesheet" type="text/css">

<script type="text/javascript" src="../skylineFormWebapp/deps/jquery-1.12.4.js"></script>
<script type="text/javascript" src="../skylineFormWebapp/deps/underscore.js"></script>
<!-- <script type="text/javascript" src="../skylineFormWebapp/deps/jquery-ui.custom_new.js"></script> -->
<script type="text/javascript" src="../skylineFormWebapp/deps/jquery-ui.custom.js"></script>
<script src="../skylineFormWebapp/js/generalFunc.js" type="text/javascript"></script>
<script type="text/javascript" src="../skylineFormWebapp/deps/opt/jsv.js"></script>
<script type="text/javascript" src="../skylineFormWebapp/lib/jsonform.js"></script>

<script type="text/javascript" src="../skylineFormWebapp/deps/moment.js"></script>
<script type="text/javascript" src="../skylineFormWebapp/deps/jquery.alphanum.js"></script>

<script src="../skylineFormWebapp/deps/jquery.dataTables.1.12.min.js"></script>

 <link href="../skylineFormWebapp/CSS/comply_theme/ColumnFilterWidgets.css" rel="stylesheet" type="text/css" />
<script src="../skylineFormWebapp/deps/jquery.dataTables.columnFilter.js" type="text/javascript"></script>
<script src="../skylineFormWebapp/deps/ColumnFilterWidgets.js" type="text/javascript"></script>

<script type="text/javascript" src="../skylineFormWebapp/deps/splitter.js"></script>
<script src="../skylineFormWebapp/js/foundation.min.js" ></script>

<link href="../skylineFormWebapp/js/chosen.min.css?<spring:message code="Env" text="" />" rel="stylesheet" type="text/css">
<script src="../skylineFormWebapp/js/chosen.jquery.min.js"></script>


<%@ include file="../include/includeBL_js.jsp"%>
<style>

	#menuDiv {
	    zoom: 0.85;
	    -moz-transform: scale(0.85);
	}
	
    .disabledbutton {
         pointer-events: none; 
        opacity: 0.4;      
    }  
    
    body,input:disabled{
      -webkit-touch-callout: none;
		-webkit-user-select: none;
		-khtml-user-select: none;
		-moz-user-select: none;
		-ms-user-select: none;
		user-select: text;
    }

    .button {
        border-radius: 5px;
        width: 200px;
        height: 35px;
        margin: 30px;
        border: 1px solid #aaa;
        background: linear-gradient(#fff 20%, #f6f6f6 50%, #eee 52%, #f4f4f4 100%);
    }

    .select,
    .input[type="text"] {
        margin: 30px;
    }

    .lable {
        margin: 10px;
    }

    .checkbox {
        min-height: 0px;
        margin-left: 30px;
        margin-bottom: 0px;
        line-height: 30px;
    }

    textarea {
        border: 1px solid #aaa;
        border-radius: 5px;
        height: 20px;
        line-height: 18px;
        padding: 0px;
    }

    .icon-plus-sign {
        background-position: 0 -96px;
    }

    .icon-minus-sign {
        background-position: -24px -96px;
    }

    [class^="icon-"],
    [class*=" icon-"] {
        display: inline-block;
        width: 14px;
        height: 14px;
        margin-top: 1px;
        line-height: 14px;
        vertical-align: text-top;
        background-image: url("../skylineFormWebapp/CSS/images/glyphicons-halflings.png");
        /*background-position: 14px 14px;*/
        background-repeat: no-repeat;
    }

    .elementIndiv {
        vertical-align: middle;
    }  

    .ui-draggable,
    .ui-droppable {
        background-position: top;
    }

    div#users-contain {
        width: 350px;
        margin: 20px 0;
    }

    div#users-contain table {
        margin: 1em 0;
        border-collapse: collapse;
        width: 100%;
    }

    div#users-contain table td,
    div#users-contain table th {
        border: 1px solid #eee;
        padding: .6em 10px;
        text-align: left;
    }

    table.chooseBookmark td:hover {
        background-color: azure !important;
    }

    table.chooseBookmark {
        border-collapse: collapse;
        text-align: center;
        width: 100%;
        height: 100%
    }

    table.chooseBookmark,
    table.chooseBookmark td,
    table.chooseBookmark th {
        border: 1px solid #aed0ea;
    }

    table.chooseBookmark td,
    table.chooseBookmark th {
        overflow: hidden;
        white-space: nowrap;
        background-color: transparent;
    }

    #fsDistributionProperties {
        display: inline-block;
        width: 300px;
        float: left;
    }

    #chooseBookmark {
/*         margin-top: 40px; */
		width:100%;
        overflow-y: scroll;
        overflow-x: hidden;
    }
    
    .btn{
	    display: inline-block;
	    padding: 4px 12px;	   
	    font-size: 14px;
	    line-height: 20px;	 
	    text-align: center;
	    text-shadow: 0 1px 1px rgba(255, 255, 255, 0.75);
	    vertical-align: middle;
	    cursor: pointer;	    
	    background-image: -moz-linear-gradient(top, #ffffff, #e6e6e6);
	    background-image: -webkit-gradient(linear, 0 0, 0 100%, from(#ffffff), to(#e6e6e6));
	    background-image: -webkit-linear-gradient(top, #ffffff, #e6e6e6);
	    background-image: -o-linear-gradient(top, #ffffff, #e6e6e6);
	    background-image: linear-gradient(to bottom, #ffffff, #e6e6e6);
	    background-repeat: repeat-x;
	    border: 1px solid #bbbbbb;
	    border-color: #e6e6e6 #e6e6e6 #bfbfbf;
	    border-color: rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.1) rgba(0, 0, 0, 0.25);
	    border-bottom-color: #a2a2a2;
	    -webkit-border-radius: 4px;
	    -moz-border-radius: 4px;
	    border-radius: 4px;
	    filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#ffffffff', endColorstr='#ffe6e6e6', GradientType=0);
	    filter: progid:DXImageTransform.Microsoft.gradient(enabled=false);
	    -webkit-box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.05);
	    -moz-box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.05);
	    box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.2), 0 1px 2px rgba(0, 0, 0, 0.05);
        border: 1px solid #74b2e2;
	    background: #e4f1fb url(../skylineFormWebapp/images/ui-bg_glass_100_e4f1fb_1x400.png) 50% 50% repeat-x;
	    font-weight: normal;
	    color: #0070a3;
	    margin-bottom: 8px;
    }
    input[type="search"]{
	    height: 20px;
	    line-height: 18px;
	    border: 1px solid #aaa;
	    border-radius: 5px;
	    background-clip: padding-box;
	    box-shadow: 0 0 3px white inset, 0 1px 1px rgba(0, 0, 0, 0.1);
	    color: #000000;
	    text-decoration: none;
	    white-space: nowrap;
	    padding: 0 3px 0 3px;
	}
	.dataTables_info{
		white-space:nowrap;
	}
	
  	input[type="text"],input[type="password"]{
		/* width:-webkit-fill-available */ 
		width:97% 
	}
	
	.dataTables_element_config {
		word-break: break-all;
	}  
	
/* 	.truncate { */
/* 		  width: 250px; */
/* 		  white-space: nowrap; */
/* 		  overflow: hidden; */
/* 		  text-overflow: ellipsis; */
/* 	} */

</style>
<script>
    
    $(document).ready(function() {
        $(document).foundation();
        //some hard coded css adjustments
        $('.sub-header').css('border-bottom-width', '0px');
    	$('#tdIncludeBreadcrumbJsp').css('display', 'none');
    	$('.cssPageHeader').css('display', 'none');
    	$('#pageSubTitle').css("display","none");
    	$('.cssUser:nth-child(1)').css('display','none');
    	$('.cssUser').css('position','absolute')
    	$('.cssUser').css('right','0');
    	$('.cssUser').css('padding-right','20px');
    	
        initAlphaNum(); // init check for special characters
        initConfirmDialogDiv(); // init skyline confirm message
        initWaitMessageDiv(); // init wait message
        var elementMap;
	
        //init page header
        $('#pageTitle').html("Form Builder"); // page title
        $('#pageSubTitle').html("Form Builder"); // page subtitle
        $('#headerShowBackIcon').css('display', 'inline-block'); // back icon init
        $("#headerShowBackIcon").attr("href", "#"); // back icon init
        $("#headerShowBackIcon").attr("onclick", "backToMainPage()"); // back icon init

        $("button").button(); 
     //   getFormTypeValues("formType"); // fill dropdown list (select name='formType') with values
        var selectArray;
        $("#tabs").tabs(); // init tabs
        $("#tabs").tabs( "option", "disabled", [ 0, 1, 2 ] );        
        $("#tabs").addClass("disabledbutton");        
        $('#newEntityBtn').button("option", "disabled", true);
        $('#previewBtn').button("option", "disabled", true);
        
        //init 3 datatables
        var array = ["LayoutTable", "CatalogTable", "ElementTable"];
        var i;
        for (i = 0; i < 3; i++) {
            if(array[i] != "LayoutTable"){ //kd 25092019 added filter to the tables
	        	var footer = '<tfoot><tr>';
	            for(var j=0; j < 3; j++)
	            {
	                footer += '<th></\th>';
	            }
	            footer += '</></>';
	            $('#' + array[i]).append(footer);     
            	/*if(array[i] != "LayoutTable"){
		             $('#' + array[i] + ' thead tr:eq(1) th').each( function () {
					    var title = $('#' + array[i] + ' thead tr:eq(0) th').eq( $(this).index() ).text();
					    $(this).html( '<input type="text" placeholder="" style="width:-webkit-fill-available" />' );
					} ); 
				}*/ 
			}
			var _columnDefs = [];
			if(array[i] == "ElementTable"){
				_columnDefs =[{
					        "targets": [2],
					        "class": "dataTables_element_config"
					    }]
			}
			var table = $('#' + array[i]).DataTable({
                "pageLength": 3,
                "lengthMenu": [
                             [3 ,10, 25, 50, 100 ],
                             ['3','10', '25', '50', '100' ]
                         ],
               "pagingType": "full_numbers",
               "autoWidth": false,
               "columnDefs": _columnDefs
                //                     "columnDefs": [ {  "targets": [ 2 ],"visible": false } ] // to hide init json
                //orderCellsTop: true //kd 25092019 use it in case use filter fields on top, before data of the table
            });
			
            $('[name="CatalogTable_length"]').prop('disabled',true);    
            $('[type="search"]').prop('disabled',true); 
            $('#' + array[i] + ' tbody').on('click', 'tr', function() {
                if ($(this).hasClass('selected')) {
                    $(this).removeClass('selected');
                    $('#deleteBtn').button("option", "disabled", true);
                    $('#editBtn').button("option", "disabled", true);
                    //$('#execOperationBtn').button("option", "disabled", true);
                    $('#entity_code').val("");
                } else {
                    var entity = $('#tabs li[aria-selected="true"] a').attr('href').substring(1);
                    var selectedTable = $('#' + entity + 'Table').DataTable();
                    selectedTable.$('tr.selected').removeClass('selected');
                    $(this).addClass('selected');
                    $('#deleteBtn').button("option", "disabled", false);
                    $('#editBtn').button("option", "disabled", false);
                    //$('#execOperationBtn').button("option", "disabled", false);
                }
            });
            if(array[i] != "LayoutTable"){
	            $('#' + array[i] + ' tbody').on('dblclick', 'tr', function() {              
	            	   $(this).addClass('selected');
	            	   editForm();
	            });
            }
            dataTableStyle(array[i]);
            initDatatableHeader(array[i]);

            var colFilterTypeArr = []; //kd 25092019 added filter
            colFilterTypeArr.push({type: "text"},{type: "text"},{type: "text"}); 
	       //     if(_bFooter && _bColumnFilter)
              // { 
                   $("#" + array[i]).dataTable().columnFilter
                   ({ 
                       aoColumns: colFilterTypeArr,//fnAddColumnFilterWidget(currTableID, colDefinArr),
                       bUseColVis: true
                   });  
               // } 
        }

        $('#deleteBtn').click(function() {
            openConfirmDialog({
                onConfirm: beforeDeleteEntity
            });
        });
        
        $("#comboChooseOperation").append($('<option>').val(1).text('Create Labels'));
        
        //dialog
        var dialog, form;

        function onSaveBtn() {
            // on clicking dialog's save button
            //the function trigger submit on jsonform              
            $('[value="Submit"]').click();
            dialog.dialog("close");
        }
        dialog = $("#dialog-form").dialog({
            autoOpen: false,
            height: $(window).height() - 50,
            width: $(window).width() - 50,
            modal: true,
            buttons: {
                "Save": onSaveBtn,
                Cancel: function() {
                    $("#result-form").html("");
                    dialog.dialog("close");
                }
            },
            close: function() {
                $("#result-form").html("");
            }
        });
        // read query string from address bar
        var queryString = QueryString();
        if (typeof queryString.formCode !== "undefined") {
            $("#formCode_hidden").val(queryString.formCode);
            $('#formCode_text').val(queryString.formCode);
            getFormInformation(); // get information of form (title,subtile,description and etc...)
        }
        if ((typeof queryString.update !== "undefined") || (typeof queryString.createLike !== "undefined")) { // if update or create like were chosen			
            $("#tabs").removeClass("disabledbutton");
            $("#tabs").tabs( "enable" );
            onTabsDisableFalse();
            getEntityTables();
            if (!canEditCatalog())
                $('#tabs li:eq(1) a').click(); // load dynamic div 
            else
                dynamicDiv("Catalog");
         
            $('#headerForm *').prop('disabled',true);
            $('#saveFormInformation').button("option", "disabled", true);   
            onTabsDisableFalse();
            
        } else { // if new form was chosen
            $('#headerForm').removeClass('disabledbutton'); //if new form was chosen
         
            $('#headerForm *').prop('disabled',false);
            
            $('#formCode_text').prop('disabled',true);
            $('[name="formType"]').prop('disabled',true);
            
            $('#saveFormInformation').button("option", "disabled", false);            
            $('#editForm').button("option", "disabled", true);
        }
        hideEditCatalogFields(); //hide Edit Catalog Fields if the user is not an admin
        
        var beforePreviewDialog;
        function continueToPreview() {        	
        	buildPreview();
        	beforePreviewDialog.dialog("close");
        }
        beforePreviewDialog = $("#beforePreview-dialog").dialog({
            autoOpen: false,
            height: 130,
            width: 300,
            modal: true,
            buttons: {
                "Continue": continueToPreview,
                Cancel: function() {                   
                	beforePreviewDialog.dialog("close");
                }
            },
            close: function() {             
            }
        }); 
        $('body').append(
			        '<div id="insertLabelsDialog" style="overflow-y:auto">\n' +
		     		'<table style="width:100%;max-width:100%;">'+
			        
			        '<tr> <td> <lable class="cssStaticData" > Bookmark prefix </lable> </td>'+
		            	  '<td> <input type="text" id="BookmarkPrefix" class="elementIndiv" style="width: 150px;" value="bookmark"> </td> </tr>'+				
					'<tr> <td> <lable class="cssStaticData" > noEntityimpcodeList </lable> </td>'+
		            '<td> <input type="text" id="noEntityimpcodeList_in" class="elementIndiv" style="width: 150px;"> </td> </tr>'+
		            
			        '</table>'+
			        '</div>\n');
		    	var $dialog = $("#insertLabelsDialog").dialog({
			        autoOpen: false,
			        title: 'Insert parameters',
			        height: 170,
			        width: 350,
			        modal: false,
			        buttons: {
			                        "Insert": insertParameters,
			                        Cancel: function() {
			                            //$('#createLikeDiv').css('display', 'none');
			                            //$('#newFormDiv').css('display', 'none');
			                            $dialog.dialog("close");
			                        }
			                    }, 
			                     close: function() {
			                        // $('#createLikeDiv').css('display', 'none');
			                        // $('#newFormDiv').css('display', 'none');
			                        $dialog.dialog("close");
			                    },
			        overflow:"auto"
		    	});
		/* $("#ElementTable >tbody >tr").each(function(){
    		$(this).find(">td:eq(2)").css('word-break', 'break-all');
    	}); */
    });

    function afterFormSubmit(values, isEdit) {
        //callback after sumbit jsonform
        var selectedEntity = $('#tabs li[aria-selected="true"] a').attr('href').substring(1);
        var selectedType = selectedEntity + "Type";
//         $('#' + selectedEntity + 'Table').DataTable().clear().draw();
        //$('#InitCode_hidden').val((JSON.stringify($('#result-form').serializeObject())).replace(/\"/g,"\\\"" ));
        var temp = JSON.stringify(JSON.stringify(values));
        $('#InitCode_hidden').val(temp.substring(1, temp.length - 1));
        newFormEntity(selectedEntity, selectedType, isEdit);
        onCancelEntityBtn();
        onTabsDisableFalse();
    }

    function beforeDeleteEntity() {
        //before delete the entity, this function check if there is an entity that depends on this entity
        var entity = $('#tabs li[aria-selected="true"] a').attr('href').substring(1);
        var selectedTable = $('#' + entity + 'Table').DataTable();
        var custid = selectedTable.row('.selected').data();
        if (typeof custid !== 'undefined') {
            canDeleteFormEntity(custid[0], selectedTable, entity);
        }
    }

    function canDeleteFormEntity(entityImpCode, selectedTable, entity) { //check if another entity using this entity
        $.ajax({
            type: 'POST',
            data: '{"action" : "canDeleteFormEntity","' + 'data":[{"code":"formCode","val":"' + $("#formCode_hidden").val()

                + '"},{"code":"entityImpCode","val":"' + entityImpCode + '"}],' + '"errorMsg":""}',
            url: "./canDeleteFormEntity.request",
            contentType: 'application/json',
            dataType: 'json',
            success: function(obj) {
                if (obj.errorMsg != null && obj.errorMsg != '') {
                    displayAlertDialog(obj.errorMsg);
                } else if (obj.data[0].val == "1") {
                    deleteFormEntity(entityImpCode, entity);
                    selectedTable.row('.selected').remove().draw(false);
                } else {
                    displayAlertDialog('Delete Failed');
                }
            },
            error: handleAjaxError
        });
    }

    function getEntityTables() {
        //insert data to datatables 
        var array = ["Layout", "Catalog", "Element"];
        var i;
        for (i = 0; i < array.length; i++) {
            getFormEntityListByFormCodeAndType(array[i], false);
                            // $('#' + array[i] + 'Table th:contains("ORDER")').click();
        }
    }

    $.fn.serializeObject = function() {
        var o = {};
        var a = this.serializeArray();
        $.each(a, function() {
            if (o[this.name] !== undefined) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    }

    function onSaveFormInformation() {
        // on saving form information
        $('#headerForm').addClass('disabledbutton');       
        $('#editForm').button("option", "disabled", false);

        $('#headerForm *').prop('disabled',true);      
        $('#saveFormInformation').button("option", "disabled", true);
                
        var active;
        if ($('[name="active"]').is(":checked"))
            active = "1";
        else
            active = "0";

        var useAsTemplate;
        if ($('[name="useAsTemplate"]').is(":checked"))
            useAsTemplate = "1";
        else
            useAsTemplate = "0";
            
        var ignoreNav;
        if ($('[name="ignoreNav"]').is(":checked"))
            ignoreNav = "1";
        else
            ignoreNav = "0";

		var useCache;
        if ($('[name="useCache"]').is(":checked"))
            useCache = "1";
        else
            useCache = "0";
            
        $("#tabs").removeClass("disabledbutton");
        $("#tabs").tabs( "enable" );
        onTabsDisableFalse();
        $("#formCode_hidden").val($("#formCode_text").val());
        newForm(active, useAsTemplate, ignoreNav, useCache); // save information           
        if (!canEditCatalog())
            $('#tabs li:eq(1) a').click(); // load dynamic div 
        else
            dynamicDiv("Catalog");
        if (document.location.search.indexOf('update') == -1) {
            if (history.pushState) {
                var newurl = window.location.protocol + "//" + window.location.host + window.location.pathname + document.location.search + '&update=true';
                window.history.pushState({
                    path: newurl
                }, '', newurl);
            }
        }
    }

    function newForm(active, useAsTemplate, ignoreNav, useCache) { //insert or update form into the db
        $.ajax({
            type: 'POST',
            data: '{"action" : "newForm","' + 'data":[{"code":"formCode","val":"' + $("#formCode_hidden").val() + '"},{"code":"description","val":"' + $('[name="description"]').val().replace(/\n/g, '\\n') + '"},{"code":"type","val":"' + $('[name="formType"]').val() + '"},{"code":"title","val":"' + $('#Title_text').val() + '"},{"code":"subtitle","val":"' + $('#Subtitle_text').val() + '"},{"code":"useAsTemplate","val":"' + useAsTemplate + '"},{"code":"active","val":"' + active + '"},{"code":"FormOrder","val":"' + $('#FormOrder').val() + '"},{"code":"GroupName","val":"' + $('#GroupName').val() + '"},{"code":"fromCodeEntity","val":"' + $('#fromCodeEntity').val() + '"},{"code":"ignoreNav","val":"' + ignoreNav + '"}, {"code":"useCache","val":"' + useCache + '"}],' + '"errorMsg":""}',
            url: "./newForm.request",
            contentType: 'application/json',
            dataType: 'json',
            success: function(obj) {
                if (obj.errorMsg != null && obj.errorMsg != '') {
                    displayAlertDialog(obj.errorMsg);
                } else if (obj.data[0].val == "-1") {
                    displayAlertDialog("insert failed");
                }
            },
            error: handleAjaxError
        });
    }

    function buildNewForm(lastType, lastValue) {
        // new form entity
        showWaitMessage("Please wait...");
        setTimeout(function() { //setTimeOut is for refreshing the DOM for "Please wait..." message
            var selectedEntity = $('#tabs li[aria-selected="true"] a').attr('href').substring(1);
            var selectedType = selectedEntity + "Type";

            if (typeof lastType !== 'undefined') {
               // $('#' + selectedType).val($('[name="selectImp"] option:contains("' + lastType + '")').val());
               $('#' + selectedType).val($('[name="selectImp"] option:contains("' + lastType + '")').filter(function() {
			       return $(this).text() == lastType;
			   }).val());
            }
            var i;
            var schema;
            for (i = 0; i < selectArray.length; i++) {
                if (selectArray[i].code == $('#' + selectedType).val()) {
                    schema = selectArray[i].value;
                    break;
                }
            }

            hideWaitMessage();
            buildForm(schema, lastValue);
        }, 0);
    }

    function editForm() {
        //edit form entity
        showWaitMessage("Please wait...");
        var selectedEntity = $('#tabs li[aria-selected="true"] a').attr('href').substring(1);
        var entityImpCode;
        var type;
        var lastValue;
        var selectedTable = $('#' + selectedEntity + 'Table').DataTable();
        var custid = selectedTable.row('.selected').data();
        if (typeof custid !== 'undefined') {
            entityImpCode = custid[0];
            type = custid[1];
            $('#entity_code').val(entityImpCode);
            //                     $('#entity_order').val(custid[1]);
        }
        $.ajax({
            type: 'POST',
            data: '{"action" : "newFormEntity","' + 'data":[{"code":"formCode","val":"' + $("#formCode_hidden").val() + '"},{"code":"entityType","val":"' + selectedEntity + '"},{"code":"entityImpCode","val":"' + entityImpCode + '"}],' + '"errorMsg":""}',
            url: "./getFormEntityInit.request",
            contentType: 'application/json',
            dataType: 'json',
            success: function(obj) {
                if (obj.errorMsg != null && obj.errorMsg != '') {
                    displayAlertDialog(obj.errorMsg);
                } else {
                    hideWaitMessage();
                    buildNewForm(type, obj.data[0].val);
                }
            },
            error: handleAjaxError
        });
    }

    function buildForm(schema, lastValue) {
        // build jsonform from schema
        var formStr = "";
        if (schema != "") {
            if (typeof lastValue === 'undefined') { // new schema			
                formStr = "$('#result-form').jsonForm({" + schema + ",onSubmit: function (errors, values){afterFormSubmit(values,false);}});"
            } else {
                //update schema
                formStr = "$('#result-form').jsonForm({" + schema + ",'value': " + lastValue + ",onSubmit: function (errors, values){afterFormSubmit(values,true);}});"
            }
            eval(formStr);
            //improve style of jsonForm          
            $('#result-form div:eq(0) div div').css('display', 'inline-block');
            $('#result-form div:eq(0) div label').css('display', 'inline-block');
            $('#result-form div:eq(0) div div input[type="text"]').css('margin', '5px');
            $('#result-form div:eq(0) div div select').css('margin', '5px');
            //$('#result-form div:eq(0) div').css('display','inline-block');
            $('#result-form div:eq(0) label').addClass('cssStaticData');
            
//             truncate
            //for information
            //$('form select').attr('onchange','showInformation(this)');
            //$('form select').trigger("change");

            //for + and - icon on jsonForm
            //$('<br>').insertAfter( $('[class$="-hidden"] label:first'));
            //$('<br>').insertAfter( $('[class$="-disable"] label:first'));
            //$('<br>').insertAfter( $('[class$="-cacheTableList"] label:first'));
            $('<br>').insertAfter($($('[class$="_jsonform-array-buttons"]').parent().parent().parent()).find('label:first'));

            $('.icon-list').css('display', 'none');

            $("#result-form a").click(function() {
                $('.icon-list').css('display', 'none');
                $('#result-form div:eq(0) div div').css('display', 'inline-block');
                $('#result-form div:eq(0) div label').css('display', 'inline-block');
                $('#result-form div:eq(0) div div input[type="text"]').css('margin', '5px');
                $('#result-form div:eq(0) div div select').css('margin', '5px');
                $('#result-form div:eq(0) label').addClass('cssStaticData');
            });

            // make jsonForm schema straighten
            $('#result-form div:first').prepend('<table id="dialogTable"><tbody></tbody></table>');
            $('#result-form div:first > div:not(:has(br))').each(function(index) {
                $(this).children().wrap($(this).clone().children().remove().end()[0])
            });
            $('#result-form div:first > div:not(:has(br))').each(function(index) {
                $('#dialogTable tbody').append('<tr><td><td>')
                $(this).children().eq(0).appendTo($('#dialogTable tr:last td:first'))
                $(this).children().eq(0).appendTo($('#dialogTable tr:last td:last'))
            });
            
            //replace the the array label with row number (and make the text fields 800px)
            updateArrayDesign();
             
            // improve labels and make text fields 500px (if below 500)
            var maxSize_ = 100;
            $('#result-form :input , :checkbox').each(function() {
            	var $input = $(this);
//             	if($input.attr('type') == 'text') {
            		if($('label[for="'+ $input.attr('id') +'"]').length) {
            			var $label = $('label[for="'+ $input.attr('id') +'"]');
            			if($label.text().length > maxSize_) {
            				$label.prop('title',$label.text());
            				$label.text($label.text().substring(0, maxSize_ - 3) + "...");
            				$label.css('min-width','700px');
            				$label.css('text-align','left');	
            			}
            		}
            		
            		if($input.attr('type') == 'text') {
            			if($input.width() < 800) {
            				$input.css('width','800px');	
            			}
            		}
            });
            
            //make all select jquery chosen 
            updateAllSelect();
            
            if ($('[name="type"]').length){
	            $('[name="type"]').change(function() {
	            	  if($('[name="type"] option:selected').val() == 'Checkbox'){
	            		$('[name="defaultValue"]').val('0')
	            	  }
	            });
            }
        }
        
        $('#result-form input').attr('autocomplete', 'off');
        $('.form-actions').css('display', 'none');
        $(".ui-sortable").unbind(); // remove drag and drop event from all jsonform's array-type elements, because it's affect the style.
        $("#dialog-form").dialog("open");
    }
        
    function updateArrayDesign() {
    	$('#result-form div:has(br) div label').each(function(index) {
            var div_ = $(this).closest('div');
            var $label = $(this);
            var labelfor = $label.attr('for');
            if(labelfor != null) {
         	   var mache = (/\[(.*?)\]/ig).exec(labelfor);
         	   if(mache != null && mache.length > 0) {
         	   		if((mache[1] + ":") != $label.html()) { // skip the lines we already made 
	         	   		$label.html(mache[1] + ":");
					 	 
						var labelHolder_ = $label.get(0).outerHTML;
						$label.remove();
						var input1 = div_.find('div :input:first');
						
						input1.before( labelHolder_ );
						if(input1.attr('type') == 'text') {
							input1.css('width',$( window ).width() - 200); 
						}
         	   		}
         	   }
            }
         });
    }
        
    function updateAllSelect() {
    	$('#result-form select').each(function() {
        	var $input = $(this);
			$input.chosen({allow_single_deselect:true, search_contains:true, disable_search:false, width: '400px'});
			$input.closest('div').css("padding", "3px");
        });
    }
    
    function updateDesign() {
    	updateAllSelect();
    	updateArrayDesign();
    }

    function getResourceValueByType(type) {
        //content of inputs inside the schema
        return JSON.parse($.ajax({
            type: 'POST',
            data: '{"action" : "getResourceValueByType","data":[{"code":"' + '","val":"' + $("#formCode_hidden").val() + '"},{"code":"","val":"' + type + '"},{"code":"","val":"' + $('#entity_code').val() + '"}' + '],"errorMsg":""}',
            url: "./getResourceValueByType.request",
            contentType: 'application/json',
            dataType: 'json',
            error: handleAjaxError,
            async: false
        }).responseText).data[0].val.split(",");
    }

    function newFormEntity(selectedEntity, selectedType, isEdit) { //insert into db
    	var isDesignHtmlImp = $('#LayoutType').length > 0 && $('#LayoutType').val() == 'LayoutDesignHtmlImp';
    	if ((selectedEntity == "Layout") && (isEdit == false) && isDesignHtmlImp)        
            $("#entity_code").val(getValidJspName($("#formCode_hidden").val(),$("#entity_code").val()));
        //         	else if (selectedEntity == "Catalog")
        //         		$("#entity_code").val("catalog_" + $("#entity_code").val());
        $.ajax({
            type: 'POST',
            data: '{"action" : "newFormEntity","' + 'data":[{"code":"formCode","val":"' + $("#formCode_hidden").val() + '"},{"code":"order","val":"' + $("#entity_order").val() + '"},{"code":"entityType","val":"' + selectedEntity + '"},{"code":"entityImpCode","val":"' + $("#entity_code").val() + '"},{"code":"entityImpClass","val":"' + $('#' + selectedType).val() + '"},{"code":"entityImpInit","val":"' + $('#InitCode_hidden').val() + '"}],' + '"errorMsg":""}',
            url: "./newFormEntity.request",
            contentType: 'application/json',
            dataType: 'json',
            success: function(obj) {
                if (obj.errorMsg != null && obj.errorMsg != '') {
                    displayAlertDialog(obj.errorMsg);
                } else if (obj.data[0].val == "-1") {
                    displayAlertDialog("insert failed");
                } else if (obj.data[0].val == "0") {                	
                    //alert("record already exist");                        
                   // getFormEntityListByFormCodeAndType(selectedEntity, true);
                } else {
                    //alert("insert successfully");                    
			        $('#' + selectedEntity + 'Table').DataTable().clear().draw();
                    getFormEntityListByFormCodeAndType(selectedEntity, true);
                }
            },
            error: handleAjaxError
        });
    }

    function deleteFormEntity(entityImpCode, entity) { //delete from db
        $.ajax({
            type: 'POST',
            data: '{"action" : "deleteFormEntity","' + 'data":[{"code":"formCode","val":"' + $("#formCode_hidden").val()

                + '"},{"code":"entityImpCode","val":"' + entityImpCode + '"}],' + '"errorMsg":""}',
            url: "./deleteFormEntity.request",
            contentType: 'application/json',
            dataType: 'json',
            success: function(obj) {
                if (obj.errorMsg != null && obj.errorMsg != '') {
                    displayAlertDialog(obj.errorMsg);
                } else if (obj.data[0].val == "-1") {
                    displayAlertDialog("delete failed");
                } else if (obj.data[0].val == "0") {
                    //alert("delete 0 records");

                } else {
                    //alert("delete successfully");		
                    if (entity == "Element") {
                        if (typeof elementMap !== 'undefined') {
                            for (var key in elementMap) {
                                if (elementMap.hasOwnProperty(key)) {
                                    if (elementMap[key] == entityImpCode) {
                                        delete elementMap[key];
                                        break;
                                    }
                                }
                            }

                        }
                    }
                    $('#chooseBookmark div').html('');
                    getTableOfBookmarks(false);
                    newButtonDisabled(entity);
                }
            },
            error: handleAjaxError
        });
    }

    function newButtonDisabled(entity) {
        if (entity == "Layout") {
            if ($('#' + entity + 'Table').DataTable().page.info().recordsTotal != 0) {             
                $('#newEntityBtn').button("option", "disabled", true);
            } else {
            	$('#newEntityBtn').button("option", "disabled", false);
            }
        } else {
        	$('#newEntityBtn').button("option", "disabled", false);
        }
    }

    function dynamicDiv(EntityType) {    	
        // information and operations of entity type
        showWaitMessage("Please wait...");
        setTimeout(function() {
//         	if(EntityType == "Layout"){
//         		$('#editBtn').css('display','none');
//         	}
//         	else{
//         		$('#editBtn').css('display','inline-block');
//         	}
            $('#fieldsetDiv').remove();
            $('#brdiv').remove();
            $('#newBtn').button("option", "disabled", true);
            selectArray = getResourceCodeValueInfoByType(EntityType);
            if(EntityType === 'Element'){
           		selectArray = hideUnnecessaryElementsFormBuilder(selectArray); // general bl            	
            }
            var selectString = "";
            var divString = "";
            var i;
            for (i = 0; i < selectArray.length; i++) {
                selectString += '<option value="' + selectArray[i].code + '">' + selectArray[i].info + '</option>\n'
            }
            divString += '<div id="fieldsetDiv" style="display:none;"><fieldset id="fsDistributionProperties" style="border: 1px solid rgb(195, 195, 195); padding: 5px; visibility: visible;">';
            divString += '<legend style="font-size: 11pt;text-align:left;" class="InitiationTitle" lang_key="Distribution_Properties"><spring:message code="EntityProperties" text="" /></legend>'
            divString += '<div style="text-align:left;padding: 20px;" id="dynamicDiv">\n';
            divString += '<table><tbody><tr><td>';
            divString += '<lable class="cssStaticData"><spring:message code="Name" text="" />:</lable></td><td><input type="text" id="entity_code" class="alphanumInputEntity"  onkeyup="fieldsRequired()" style="width:390px;"></td></tr>\n';
            divString += '<tr><td><lable class="cssStaticData" style="display:none;"><spring:message code="Order" text="" />:</lable> </td><td><input type="number" id="entity_order"  onkeyup="fieldsRequired()" style="width:150px;display:none;\n" value="0"></td></tr>'; //&nbsp;&nbsp\n
            divString += '<tr><td><lable class="cssStaticData"><spring:message code="Type" text="" />:</lable></td>\n';
            divString += '<td><select name="selectImp"  onchange="fieldsRequired()" id="' + EntityType + 'Type" required style="width:150px;">\n';
            divString += '<option value="">Choose</option>\n' + selectString + '</select></td></tr></tbody></table></div><button id="newBtn" onclick="buildNewForm()" disabled><spring:message code="Configuration" text="" /></button><button style="margin-left: 15px;" onclick="onCancelEntityBtn()"><spring:message code="Cancel" /></button></fieldset>';
            divString += '</div>';
            divString += '<div id="brdiv" style="display:none;"><br><br></div>';
            
            $('#' + EntityType).prepend(divString);
    		
            initAlphaNum();
            initAlphaNumEntity();
            $("button").button();
            onCancelEntityBtn();
            newButtonDisabled(EntityType);
            //                 $(window).trigger('resize');
            hideWaitMessage();
        }, 0);
    }

    function fieldsRequired() {
        //check for required fields
        if (($('#entity_code').val() != "") && ($('[name="selectImp"]').val() != ""))
            $('#newBtn').button("option", "disabled", false);
        else
            $('#newBtn').button("option", "disabled", true);
    }

    function getResourceCodeValueInfoByType(type) {
        // get Resource Code, Value and Info By Type
        var isEntity = false;
        var isPath = false;
        if (type.indexOf("PATH") !== -1) {
            isPath = true;
        } else if (type == "Catalog") {
            type = "LAYER_CATALOG_SCHEMA";
            isEntity = true;
        } else if (type == "Layout") {
            type = "LAYER_LAYOUT_SCHEMA";
            isEntity = true;
        } else if (type == "Element") {
            type = "LAYER_ELEMENT_SCHEMA";
            isEntity = true;
        }
        var data;
        if (isEntity)
            data = '{"action" : "getResourceCodeValueInfoByType","data":[{"code":"' + $("#formCode_hidden").val() + '","val":"' + type + '"}],"errorMsg":""}';
        else if (isPath)
            data = '{"action" : "getResourceCodeValueInfoByType","data":[{"code":"","val":"' + type + '"}],"errorMsg":""}'

        var jsonInnerLayer = JSON
            .parse($
                .ajax({
                    type: 'POST',
                    data: data,
                    url: "./getResourceCodeValueInfoByType.request",
                    contentType: 'application/json',
                    dataType: 'json',
                    error: handleAjaxError,
                    async: false
                }).responseText).data[0].val;
        var jsonOuterLayer = JSON.parse(jsonInnerLayer)
        var i;
        var array = [];
        if (isEntity) {
            for (i = 0; i < jsonOuterLayer.length; i++) {
                array[i] = new Object();
                var temp = jsonOuterLayer[i].split(';')
                array[i].code = temp[0];
                array[i].value = temp[1];
                array[i].info = temp[2];
            }
        } else if (isPath) {
            for (i = 0; i < jsonOuterLayer.length; i++) {
                var temp = jsonOuterLayer[i].split(';')
                array[i] = temp[1];
            }
        }
        return array
    }

    function getFormEntityListByFormCodeAndType(EntityType, loadMetrix) {
        ///insert data to datatables by entity type
        if (EntityType == "Element") {
            elementMap = {};
        }
        var jsonInnerLayer = JSON
            .parse($
                .ajax({
                    type: 'POST',
                    data: '{"action" : "getFormEntityListByFormCodeAndType","data":[{"code":"' + $("#formCode_hidden").val() + '","val":"' + EntityType + '"}],"errorMsg":""}',
                    url: "./getFormEntityListByFormCodeAndType.request",
                    contentType: 'application/json',
                    dataType: 'json',
                    error: handleAjaxError,
                    async: false
                }).responseText).data[0].val;
        var jsonOuterLayer = JSON.parse(jsonInnerLayer)
        var i;
        
        
        for (i = 0; i < jsonOuterLayer.length; i++) {
            $('#' + EntityType + 'Table').DataTable().row.add(
                jsonOuterLayer[i].split(";")).draw(false);
            if (EntityType == "Element") {
            	try {
            		if(jsonOuterLayer[i].indexOf("layoutBookMarkItem:") != -1) {
                		var rege = /layoutBookMarkitem:(.*?),/ig;
                		var matches = (rege).exec(jsonOuterLayer[i]);
                		var match1 = matches[1]; // matches[1] should be the layoutBookMarkItem value 
                		elementMap[match1] = jsonOuterLayer[i].split(';')[0]; // elementMap key = bookmark / val = element -> for showing them in formnuilder layout display
                	}
            	} catch(e) {
                	alert("invalid token the element " + jsonOuterLayer[i].split(';')[0] + " will not be displayed");
                	elementMap = {};
                	continue;
                }
            }
        }

        if (loadMetrix == true) {
            $('#chooseBookmark div').html('');
            getTableOfBookmarks(false);
        } else if (EntityType == "Element") {
            $('#chooseBookmark div').html('');
            getTableOfBookmarks(true);
        }

        $('#entity_code').val("");
        // $('#entity_order').val("");
        $('[name="selectImp"]').val("");
        fieldsRequired();
        $('#editBtn').button('option', 'disabled', true);
        $('#execOperationBtn').button('option', 'disabled', false);

        newButtonDisabled(EntityType);
        $(window).trigger('resize');
    }

    function backToMainPage() {
        //back to demoFormBuilderMain
        var str;
        if ($('#formCode_hidden').val() != "") {
            str = 'demoFormBuilderMainInit.request?stateKey=' + $('#stateKey').val() + '&formCode=' + encodeURIComponent($('#formCode_hidden').val());
        } else {
            str = 'demoFormBuilderMainInit.request?stateKey=' + $('#stateKey').val();
        }

        $("#builderModal").attr("action", str);
        $('#builderModal').submit()
    }

    function viewOfEnum(enumArray) {
        // return value of enumarray
        var i;
        var o = new Object()
        for (i = 0; i < enumArray.length; i++) {
            o[enumArray[i]] = enumArray[i].split('_EnumDelimiter_')[1];
        }
        return o;
    }

    function getEnumArray(enumName) { //test
        if ((enumName == "hidden") || (enumName == "mandatory"))
            return ["False_EnumDelimiter_False_EnumDelimiter_info: information about False", "True_EnumDelimiter_True_EnumDelimiter_info: information about true", "element_EnumDelimiter_element_EnumDelimiter_info: information about element"]
    }

    function showInformation(obj) {
        //show information of enum
        var toRemove = $(obj).attr('name') + 'Info';
        $('#' + toRemove).remove();
        $(obj).parent().parent().append('<div id="' + $(obj).attr('name') + 'Info" style="display:inline-block;background-color: lightblue;">' + $(obj).val().split(';')[2] + '</div>')
    }

    function buildPreview() {
        //build Preview of form
        var page = "./init.request?formCode=" + encodeURIComponent($("#formCode_hidden").val()) + "&formId=" + $('#selectFormId').val() + "&userId=" + $('#userId').val() + "&PARENT_ID=-1&formBuilderSaveFlag=1";
        var $dialog = $('<div id="prevDialog" style="overflow-y: hidden;""></div>')
            .html('<iframe style="border: 0px;width:100%;height:100%" src="' + page + '"></iframe>')
            .dialog({
                autoOpen: false,
                modal: true,
                //                     height: 800,
                //                     width: 900,
                height: $(window).height() - 50,
                width: $(window).width() - 50,
                title: "Form",
                close: function() {
                    $('#prevDialog').remove();
                }
            });
        $dialog.dialog('open');
    }

    function getFormType() {
        //for getting files from folder
       
        return "PATH_JSP_" + $('[name="formType"]').val();
    }

    function getFilesNamesByFormType() {
        // get Files Names By Form Type
        var temp = getFormType();
        return getResourceCodeValueInfoByType(temp);
    }

    function getFormInformation() {
        //getFormInformation
        showWaitMessage("Please wait...");
        $.ajax({
            type: 'POST',
            data: '{"action" : "getFormInformation","' + 'data":[{"code":"formCode","val":"' + $("#formCode_hidden").val() + '"}],' + '"errorMsg":""}',
            url: "./getFormInformation.request",
            contentType: 'application/json',
            dataType: 'json',
            success: function(obj) {
                if (obj.errorMsg != null && obj.errorMsg != '') {
                    displayAlertDialog(obj.errorMsg);
                } else {
                    var formInformationArray = obj.data[0].val.split(';');
                    $('#Title_text').val(formInformationArray[1]);
                    $('#Subtitle_text').val(formInformationArray[2]);
                    $('[name=description]').val(formInformationArray[3]);
                    $('[name=formType]').val(formInformationArray[5]);
                    if (formInformationArray[4].toUpperCase() == "YES")
                        $('[name=active]').prop('checked', true);
                    else
                        $('[name=active]').prop('checked', false);
                    if (formInformationArray[6].toUpperCase() == "YES")
                        $('[name=useAsTemplate]').prop('checked', true);
                    else
                        $('[name=useAsTemplate]').prop('checked', false);
                    $('#FormOrder').val(formInformationArray[7]);
                    $('#GroupName').val(formInformationArray[8]);
                    $('#fromCodeEntity').val(formInformationArray[9]);
                    if (formInformationArray[10].toUpperCase() == "YES")
                        $('[name="ignoreNav"]').prop('checked', true);
                    else
                        $('[name="ignoreNav"]').prop('checked', false);
                    if (formInformationArray[11].toUpperCase() == "YES")
                        $('[name="useCache"]').prop('checked', true);
                    else
                        $('[name="useCache"]').prop('checked', false);                        
                        
                    hideWaitMessage();
                }
            },
            error: handleAjaxError
        });
    }

    function onEditForm() {
        //onEditForm button change disabled
        if ($('#headerForm').hasClass('disabledbutton')) {
            $('#headerForm').removeClass('disabledbutton');
           
        
            $('#headerForm *').prop('disabled',false);           
            $('#saveFormInformation').button("option", "disabled", false);
            
            $('#formCode_text').prop('disabled',true);
            $('[name="formType"]').prop('disabled',true);
            
        } else {
            $('#headerForm').addClass('disabledbutton');            
           
            $('#headerForm *').prop('disabled',true); 
    
            $('#saveFormInformation').button("option", "disabled", true);
        }
    }

    function onNewEntityBtn() {
        // on new button // starting the process of building jsonform and creating new entity
        //$('#fsDistributionProperties').css('display','block');
        //                 var entity = $('#tabs > div').filter(function() {
        //                     return $(this).css('display') != 'none';
        //                 })[0].id;

        //                 dynamicDiv(entity);
        $('[name="selectImp"]').val('');
        $('#entity_code').val('');
        $('#fieldsetDiv').css('display', 'block');

        $('#brdiv').css('display', 'block');
        $('#LayoutTable_wrapper').addClass('disabledbutton');
        $('#ElementTable_wrapper').addClass('disabledbutton');
        $('#CatalogTable_wrapper').addClass('disabledbutton');
        $('#buttons').addClass('disabledbutton');
        
        $('[name="selectImp"]').chosen({allow_single_deselect:true, search_contains:true, disable_search:false, width: '400px'});
    }

    function onCancelEntityBtn() {
        // on new entity process cancellation 
        $('#fieldsetDiv').css('display', 'none');
        $('#brdiv').css('display', 'none');
        $('#LayoutTable_wrapper').removeClass('disabledbutton');
        $('#ElementTable_wrapper').removeClass('disabledbutton');
        $('#CatalogTable_wrapper').removeClass('disabledbutton');
        $('#buttons').removeClass('disabledbutton');
        $('[name="selectImp"]').val('');
        $('#entity_code').val('');

    }

    function getTableOfBookmarks(firstTime) {
        //getTableOfBookmarks
        showWaitMessage("Please wait...");
        var selectedTable = $('#LayoutTable').DataTable();
        var custid = selectedTable.row(':eq(0)').data();
        var entityCode;
        if (typeof custid !== 'undefined') {
            entityCode = custid[0];
        } else {
            hideWaitMessage();
            $('#chooseBookmark').css('border', '0');
            return;
        }
        $('#chooseBookmark').css('border', '1px solid #aed0ea');
        $.ajax({
            type: 'POST',
            data: '{"action" : "getTableOfBookmarks","data":[{"code":"' + $("#formCode_hidden").val() + '","val":"' + entityCode + '"},{"code":"","val":"' + firstTime + '"}],"errorMsg":""}',
            url: "./getTableOfBookmarks.request",
            contentType: 'application/json',
            dataType: 'json',
            success: function(obj) {
                if (obj.errorMsg != null && obj.errorMsg != '') {
                    displayAlertDialog(obj.errorMsg);
                    hideWaitMessage();
                } else {
                    $('#chooseBookmark div').append(obj.data[0].val);
                    var jspBookmarkArray = $('.chooseBookmark').text().trim().replace(/\r?\n|\r/g, ',').replace(/ /g, ',').replace(/\t/g, ',').split(',');
                    var bookmarkArray = getResourceValueByType('LAYOUT_ITEM_TEXT');
                    var arrayLength = bookmarkArray.length;
                    for (var i = 0; i < arrayLength; i++) {
                        for (var j = 0; j < jspBookmarkArray.length; j++) {
                            if (jspBookmarkArray[j] == bookmarkArray[i]) {
                                jspBookmarkArray[j] = "";
                            }
                        }
                    }
                    //                         for (var j = 0; j < jspBookmarkArray.length; j++) {
                    //                             if (jspBookmarkArray[j] != "") {
                    //                                 $('div.chooseBookmark div:contains(' + jspBookmarkArray[j] + ')').filter(function() {
                    //                                     return $(this).text() == jspBookmarkArray[j];
                    //                                 }).css('background-color', '#c4dae9');
                    //                             }
                    //                         }
                    if (typeof elementMap !== 'undefined') {
                        for (var key in elementMap) {
                            if (elementMap.hasOwnProperty(key)) {
                                $('div.chooseBookmark div:contains(' + key + '),div.chooseBookmark td:contains(' + key + ')').filter(function() {
                                    return $(this).text() == key;
                                }).html(elementMap[key]);
                            }
                        }
                    }
                    $('.chooseBookmark div').css('border','1px solid #d7ebf9');
                    
                    if($( "#tempalteTabs" ).length > 0 ) {
                    	 $( "#tempalteTabs" ).tabs();
                    }
                    hideWaitMessage();
                }
            },
            error: handleAjaxError
        });
    }
    function onTabsDisableFalse() {
    	$('li a:contains("Catalog")').attr('onclick',"dynamicDiv('Catalog');$('#CatalogTable tr.selected').removeClass('selected');$('#editBtn').button( 'option', 'disabled', true );$('#execOperationBtn').button( 'option', 'disabled', false );");
    	$('li a:contains("Element")').attr('onclick',"dynamicDiv('Element');$('#ElementTable tr.selected').removeClass('selected');$('#editBtn').button( 'option', 'disabled', true );$('#execOperationBtn').button( 'option', 'disabled', false );");
    	$('li a:contains("Layout")').attr('onclick',"dynamicDiv('Layout');$('#LayoutTable tr.selected').removeClass('selected');$('#editBtn').button( 'option', 'disabled', true );$('#execOperationBtn').button( 'option', 'disabled', false );");
    	$('[name="CatalogTable_length"]').prop('disabled',false);    
    	$('[type="search"]').prop('disabled',false); 
    	$('#newEntityBtn').button("option", "disabled", false);
        $('#previewBtn').button("option", "disabled", false);
        
    }
    
    function executeOperation() {

     if ( $('#comboChooseOperation option:selected').val() == 0) {
                          	 displayAlertDialog("Choose value in drop-down list");
        } else {
		     $('#insertLabelsDialog').dialog('open');
    	}
    }
    
    function insertParameters() {
    	$.ajax({
                type: 'POST',
                data: '{"data":[{"code":"operation","val":"' + $('#comboChooseOperation option:selected').val() + 
                		'"},{"code":"formCode","val":"' + $("#formCode_text").val() + '"},{"code":"bookMarkPrefix","val":"' + $("#insertLabelsDialog #BookmarkPrefix").val() + 
                		'"},{"code":"noEntityimpcode","val":"' + $("#insertLabelsDialog #noEntityimpcodeList_in").val() + '"}],' + '"errorMsg":""}',   
                url: "./executeOperation.request",
                contentType: 'application/json',
                dataType: 'json',
                success: function(obj) {
                    if (obj.errorMsg != null && obj.errorMsg != '') {
                        displayAlertDialog(obj.errorMsg);
                        hideWaitMessage();
                    } else if (obj.data[0].val == "-1") {
                        displayAlertDialog("insert failed");
                        hideWaitMessage();
                    } else if ( $('#comboChooseOperation option:selected').val() == 0) {
                     	 displayAlertDialog("Choose value in drop-down list");
                    } else {
                    	$("#insertLabelsDialog").dialog("close");
                   	displayAlertDialog("Done!");
                    }
                },
                error: handleAjaxError
            });
    }
    /* function closeDialog() {
    	$("#insertLabelsDialog").dialog('close')
    }  */
    function onClickShowMessage() //kd 25092019 added alert message
    {
    	alert('Group names: _System Event Handler,_System Configuration Pool and _System Configuration Report \nwill be coppied in the installtion and the inner relations in this forms\nshould not depend on id numbers only on names');
    }
    
    
</script>
</head>

<body style="overflow-y: auto;">

<form style="display:none;" action="" method="post" id="builderModal"></form>
<!-- return to form builder main -->

<div id="menuDiv">
			<div id="dropDownMenuBar" class="sub-header"
				style="width: 100%; text-align: left; float: left; position: relative;">
				<div style="float: left; position: relative; width: 95%;">
					<%=((String) session.getAttribute("MAIN_MENU")).replace("@@STATEKEY_HOLDER@@",
						request.getParameter("stateKey"))%>
				</div>
				<div id="divIconQRCode" style="float: right;" class="QR_code">
					<span onclick="openSearchLabelDialog(this);return false;"></span>
				</div>
			</div>
		</div>
		<%@ include file="PageHeaderNewForm.inc"%>

<table style="width: 100%; text-align: center;">
     
        <td class="ui-widget-content ui-corner-all" colspan="10">
            <table width="100%">
                <tr>
                    <td style="text-align: left; padding-left: 15px;">
                        <h2 class="InitiationTitle" style="display:inline-block;">
<label lang_key='Form' style="text-transform: uppercase;"><spring:message code="Form" text="" />:</label>
</h2>
                    </td>
                </tr>
                <tr>
                    <td>
                        <div id="headerForm" style="text-align:left; margin-left: 20px; white-space: nowrap;" class="disabledbutton">
                            <table style="width:100%;max-width:100%;">
                                <tbody>
                                    <tr>
                                        <td>
                                            <lable class="cssStaticData ">
                                                <spring:message code="Name" text="" />:</lable>
                                        </td>
                                        <td>
                                            <input type="text" id="formCode_text" class="elementIndiv disabledbutton" style="width: 150px;" disabled>
                                        </td>
                                        <td>
                                            <lable class="cssStaticData ">
                                                <spring:message code="Title" text="" />:</lable>
                                        </td>
                                        <td>
                                            <input type="text" id="Title_text" class="elementIndiv alphanumInput" style="width: 150px;">
                                        </td>
										<td><input type="checkbox" name="active" class="elementIndiv"></td>
                                        <td>
                                            <input type='hidden' name="activeValue">
                                            
                                            <lable class="cssStaticData">
                                                <spring:message code="Active" text="" />
                                            </lable>
                                        </td>
                                        <td>
                                            <lable class="cssStaticData">
                                                <spring:message code="Description" text="" />:</lable>
                                        </td>

                                        <td rowspan="3">
                                            <textarea name="description" style="width:400px; height:70px;" class="elementIndiv" onkeypress="if ( event.which == 59 || event.keyCode == 59) return false;"></textarea>
                                        </td>
                                        <td style="display:none;">
                                            <lable class="cssStaticData" style="display:none;">
                                                <spring:message code="Order" text="" />:</lable>
                                        </td>
                                        <td style="display:none;">
                                            <input type="number" id="FormOrder" class="elementIndiv" style="width: 150px;display:none;" value="0">
                                        </td>

                                    </tr>
                                    <tr>
                                        <td colspan="8">&nbsp;</td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <lable class="cssStaticData">
                                                <spring:message code="Type" text="" />:</lable>
                                        </td>
                                        <td>                                          
                                            <input type="text" name="formType" class="elementIndiv disabledbutton" style="width: 150px;">
                                        </td>
                                        <td>
                                            <lable class="cssStaticData ">
                                                <spring:message code="Subtitle" text="" />:</lable>
                                        </td>
                                        <td>
                                            <input type="text" id="Subtitle_text" class="elementIndiv alphanumInput" style="width: 150px;">
                                        </td>                                        
                                        <td class="useAsTemplateDiv"><input type="checkbox" name="useAsTemplate" class="elementIndiv"></td>
                                        <td>
                                            <div class="useAsTemplateDiv" style="display:inline-block;">
                                              
                                                <lable class="cssStaticData">
                                                    <spring:message code="UseAsTemplate" text="" />
                                                </lable>
                                            </div>
                                        </td>
                                        <td>
                                        </td>
                                    </tr>
                                      <tr>
                                        <td colspan="8">&nbsp;</td>
                                    </tr>
                                    <tr>
                                      <td>
                                      	   <img src="../skylineFormWebapp/images/IconInfoS.gif" border="0" title="Main Menu" style="margin-top: 3px;" onclick="onClickShowMessage();"> 
                                           <lable class="cssStaticData">
                                                <spring:message code="GroupName" text="" />:</lable>
                                        </td>
                                        <td>
                                            <input type="text" id="GroupName" class="elementIndiv" style="width: 150px;">
                                        </td>
                                        <td>
                                            <lable class="cssStaticData">
                                                <spring:message code="fromCodeEntity" text="" />:</lable>
                                        </td>
                                        <td>
                                            <input type="text" id="fromCodeEntity" class="elementIndiv" style="width: 150px;">
                                        </td>
                                        
                                        <td><input type="checkbox" name="ignoreNav" class="elementIndiv"></td>
                                        <td>
                                          <lable class="cssStaticData">
                                              <spring:message code="IgnoreNav" text="Ignore Navigation" />
                                          </lable>                                           
                                        </td>  
                                        <td></td>
                                         <td><input type="checkbox" name="useCache" class="elementIndiv">                                        
	                                          <lable style="margin-left: 10px;" class="cssStaticData">
	                                              <spring:message code="useCache" text="Use Cache" />
	                                          </lable>                                           
                                         </td>                                  
                                        
                                    </tr>
                                </tbody>
                            </table>


                        </div>
                        <div style="margin-top:25px;text-align:center;">
                            <button id="saveFormInformation" onclick="onSaveFormInformation();" style="">
                                <spring:message code="Save" text="" />
                            </button>
                            <button id="editForm" onclick="onEditForm();" style="margin-left:50px;">
                                <spring:message code="Edit" text="" />
                            </button>
                        </div>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td>&nbsp;</td>
    </tr>
    <tr>
        <td class="ui-widget-content ui-corner-all" colspan="10">
        <table style="width: 100%;">
        <tbody>
        <tr><td style="width:50%;vertical-align: top;">
            <table width="100%;" style="float:left;"><!-- display:inline-block; -->
                <tr>
                    <td style="text-align: left; padding-left: 15px;">
                        <h2 class="InitiationTitle">
    <label lang_key='Details' style="text-transform: uppercase;"><spring:message code="Details" text="" />:</label>
</h2>
                    </td>
                </tr>

                <tr>
                    <td>
                        <div id="tabs">
                            <ul>
                                <li>
                                    <a href="#Catalog" onclick="">
                                        <spring:message code="Catalog" text="" />
                                    </a>
                                </li>
                                <li>
                                    <a href="#Layout" onclick="">
                                        <spring:message code="Layout" text="" />
                                    </a>
                                </li>

                                <li>
                                    <a href="#Element" onclick="">
                                        <spring:message code="Element" text="" />
                                    </a>
                                </li>
                            </ul>

                            <br>
                            <br>
                            <div id="Layout">
                                <table id="LayoutTable" class="display" style="text-align: left;width:100%;">
                                    <thead>
                                        <tr>
                                            <th>
                                                <div><spring:message code="NAME" text="" /></div>
                                            </th>
                                            <!--<th> <spring:message code="ORDER" /> </th> -->
                                            <th>
                                                <div><spring:message code="TYPE" text="" /></div>
                                            </th>
                                            <th>
                                                <div><spring:message code="CONFIGURATION" text="" /></div>
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                    </tbody>
                                </table>
                            </div>
                            <div id="Catalog">
                                <table id="CatalogTable" class="display" style="text-align: left;width:100%;">
                                    <thead>
                                        <tr>
                                            <th>
                                               <div><spring:message code="NAME" text="" /></div>
                                            </th>
                                            <!--<th> <spring:message code="ORDER" /> </th> -->
                                            <th>
                                                <div><spring:message code="TYPE" text="" /></div>
                                            </th>
                                            <th>
                                                <div><spring:message code="CONFIGURATION" text="" /></div>
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                    </tbody>
                                </table>
                            </div>
                            <div id="Element">
                                <table id="ElementTable" class="display" style="text-align: left;width:100%;">
                                    <thead>
                                        <tr>
                                            <th style="width:25%;">
                                                <div><spring:message code="NAME" text="" /></div>
                                            </th>
                                            <!--<th> <spring:message code="ORDER" text="" /> </th> -->
                                            <th style="width:25%;">
                                                <div><spring:message code="TYPE" text="" /></div>
                                            </th>
                                            <th style="width:50%;">
                                                <div><spring:message code="CONFIGURATION" text="" /></div>
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                    </tbody>
                                </table>
                            </div>
                            <div>&nbsp;</div>
                            <div id="buttons" style="text-align: center;white-space:nowrap;">
                                <div>&nbsp;</div>
                                <button id="newEntityBtn" onclick="onNewEntityBtn()">
                                    <spring:message code="New" text="" />
                                </button>
                                <button id="deleteBtn" disabled>
                                    <spring:message code="Delete" text="" />
                                </button>
                                <button id="editBtn" onclick="editForm()" disabled>
                                    <spring:message code="Edit" text="" />
                                </button>
                                <!-- <select name="comboChooseOperation" id="comboChooseOperation" style="font-size:11;text-align:left;width:200px; visibility:hidden;">
                                            <option value="0">Choose</option>
                                </select> -->
                                <script type="text/javascript">
									var hidden = '<%=request.getAttribute("userName")%>';
									hidden = hidden.toUpperCase();
									if(hidden == "SYSTEM") {
										document.writeln("<select name='comboChooseOperation' id='comboChooseOperation' style='font-size:11;text-align:left;width:200px; '>");
										document.writeln("<option value='0'>Choose</option></select>");
										document.writeln("<button id='execOperationBtn' style='width:100px;' onclick='executeOperation();' disabled>Execute</button>");
									} else {
										document.writeln("<select name='comboChooseOperation' id='comboChooseOperation' style='font-size:6;text-align:left;width:1px; visibility:hidden; '>");
										document.writeln("<option value='0'>Choose</option></select>");
										document.writeln("<button id='execOperationBtn' style='width:1px;visibility:hidden;' onclick='executeOperation();' disabled>Execute</button>"); 
									} 
								</script>
                                <%--  <button id="execOperationBtn" style="width:100px;<p id="addHidden"></p>" onclick="executeOperation();" disabled">Execute</button> --%>
                                <button id="previewBtn" onclick="getFormsId('selectFormId');$('#beforePreview-dialog').dialog('open');"><!--       buildPreview() -->
                                    <spring:message code="Preview" text="" />
                                </button>
                                
                                <%-- <button id="execOperationBtn" onclick="executeOperation();"><!--       buildPreview() -->
                                    <spring:message code="Preview" text="" />
                                </button> --%>
                                
                                
                            </div>



                            <div id="dialog-form" title="Create New Entity">
                                <form id="result-form"></form>
                            </div>
                            <div id="beforePreview-dialog" title="Form Id">
                            <label class="cssStaticData">Choose Form Id:</label>
                              <select id="selectFormId" name="selectFormId"><option value="-1">new</option></select>
                            </div>
                            <input type="hidden" id="formCode_hidden">
                            <input type="hidden" id="InitCode_hidden">
                            <input type="hidden" id="lastValue_hidden">
                    </td>
                </tr>
            </table>
          </td>
          <td style="width:50%;vertical-align: top;padding-top:48px;">
            <div id="chooseBookmark" title="Choose Bookmark" style="max-height: 400px;zoom:0.85;">
                <div class="chooseBookmark"></div>
            </div>
            </td>
            </tr>
            </tbody>
            </table>
        </td>
    </tr>

</table>
<input type="hidden" id="canEditCatalog" value='<%=request.getAttribute("canEditCatalog")%>' />
<input type="hidden" id="userId" value='<%=request.getAttribute("userId")%>' />
<input type="hidden" id="stateKey" name="stateKey" value="${stateKey}"><input type="hidden" id="formPathInfo" name="formPathInfo" value='${formPathInfo}'>


</body>

</html>