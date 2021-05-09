<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
    <!doctype html>
    <html lang="en">

    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>Form Builder Main</title>
		<link rel="icon" href="../skylineFormWebapp/images/favicon.ico?<spring:message code="Env" text="" />" />
        <link href="../skylineFormWebapp/CSS/comply_theme/Skyline_9.css" rel="stylesheet" type="text/css">
		<link href="../skylineFormWebapp/CSS/comply_theme/jquery-ui.custom.css" rel="stylesheet" type="text/css" media="all" />
		<link href="../skylineFormWebapp/CSS/comply_theme/mbcsmbmcp_form.css" rel="stylesheet" type="text/css" />	
		<link href="../skylineFormWebapp/CSS/comply_theme/demo_table_jui_form.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" type="text/css" href="../skylineFormWebapp/CSS/layoutFormBuilderCSS.css">
		<link href="../skylineFormWebapp/CSS/comply_theme/app_formBuilder.css" rel="stylesheet" type="text/css">
		<link href="../skylineFormWebapp/CSS/comply_theme/tmpCSS.css" rel="stylesheet" type="text/css">


        <script type="text/javascript" src="../skylineFormWebapp/deps/jquery-1.12.4.js"></script>
        <script type="text/javascript" src="../skylineFormWebapp/deps/underscore.js"></script>
        <script type="text/javascript" src="../skylineFormWebapp/deps/jquery-ui.custom.js"></script>
        <script src="../skylineFormWebapp/js/generalFunc.js" type="text/javascript"></script>
        <script type="text/javascript" src="../skylineFormWebapp/deps/opt/jsv.js"></script>
        <script type="text/javascript" src="../skylineFormWebapp/lib/jsonform.js"></script>
        <script src="../skylineFormWebapp/deps/jquery.dataTables.1.12.min.js"></script>
        <script type="text/javascript" src="../skylineFormWebapp/deps/splitter.js"></script>
        <script type="text/javascript" src="../skylineFormWebapp/deps/jquery.alphanum.js"></script>
        <script src="../skylineFormWebapp/js/foundation.min.js" ></script>
        <script type="text/javascript" src="../skylineFormWebapp/deps/ColReorderWithResize.js"></script>
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
            
            .elementIndiv {
                vertical-align: middle;
            }
            /*  jquery ui dialog */
            
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
                overflow: hidden;
                white-spac: nowrap;
                text-overflow: ellipsis
            }
            
            input[type="search"],input[type="number"] 	{
				  height: 20px;
				  line-height:18px;
				  border: 1px solid #aaa;
				  border-radius: 5px;
				  /*background-color: #fff;
				  background: -webkit-gradient(linear, 50% 0%, 50% 100%, color-stop(20%, #ffffff), color-stop(50%, #f6f6f6), color-stop(52%, #eeeeee), color-stop(100%, #f4f4f4));
				  background: -webkit-linear-gradient(top, #ffffff 20%, #f6f6f6 50%, #eeeeee 52%, #f4f4f4 100%);
				  background: -moz-linear-gradient(top, #ffffff 20%, #f6f6f6 50%, #eeeeee 52%, #f4f4f4 100%);
				  background: -o-linear-gradient(top, #ffffff 20%, #f6f6f6 50%, #eeeeee 52%, #f4f4f4 100%);
				  background: linear-gradient(top, #ffffff 20%, #f6f6f6 50%, #eeeeee 52%, #f4f4f4 100%);*/
				  background-clip: padding-box;
				  box-shadow: 0 0 3px white inset, 0 1px 1px rgba(0, 0, 0, 0.1);
				  color: #000000;
				  text-decoration: none;
				  white-space: nowrap;
				  padding: 0 3px 0 3px;
				  width:800px;
			}
        </style>
        <script>
            $(document).ready(function() {
            	$(document).foundation();
            	//some hard coded css adjustments
            	$('.sub-header').css('border-bottom-width', '0px');
             	$('#tdIncludeBreadcrumbJsp').css('display', 'none');
             	$('.cssPageHeader').css('display', 'none');
             	$('.cssUser:nth-child(1)').css('display','none');
             	$('.cssUser').css('position','absolute')
             	$('.cssUser').css('right','0');
             	$('.cssUser').css('padding-right','20px');
             	
           		 $('#table thead tr:eq(1) th').each( function () { //kd 25092019 added filter to the tables
			    var title = $('#table thead tr:eq(0) th').eq( $(this).index() ).text();
			    
	
			    {
			    	$(this).html( '<input type="text" placeholder="" style="width:93%" />' );
			    }
			    
			} ); 
                initAlphaNum();
                initWaitMessageDiv(); // init wait message
                getFormTypeValues("formType");
                $('#pageTitle').html("Form Builder Main");
                $('#pageSubTitle').html("Form Builder Main");
                $("button").button();
                // init datatable
                var table = $('#table').DataTable({
                	"dom": "Rlfrtip",
                	"colReorder": {
                        "allowReorder": false
                    },
                    "pageLength": 10,
                    "pagingType": "full_numbers",
//                     bSort:false
                    "order": [], // disable initial sorting
                    orderCellsTop: true,
                    destroy: true
                });

                $('#table >tbody').on('click', 'tr', function() {
                    if ($(this).hasClass('selected')) {
                        $(this).removeClass('selected');
                        $('#updateForm').button("option", "disabled", true);
                        $('#createLike').button("option", "disabled", true);
                        $('#previewBtn').button("option", "disabled", true);
                        $('#formCode_hidden').val('');
                    } else {
                        table.$('tr.selected').removeClass('selected');
                        $(this).addClass('selected');
                        $('#formCode_hidden').val($(this).find('td').eq(0).text());
                        $('#formCode_text').val($(this).find('td').eq(0).text());
                        $('[name="formType"]').val($(this).find('td').eq(5).text());
                        if ($('#formCode_hidden').val() != "No data available in table") {
                            $('#updateForm').button("option", "disabled", false);
                            $('#createLike').button("option", "disabled", false);
                            $('#previewBtn').button("option", "disabled", false);
                        }
                    }
                });
                $('#table tbody').on('dblclick', 'tr', function() {
                    $('#formCode_hidden').val($(this).find('td').eq(0).text());
                    var str = './demoFormBuilderInit.request?stateKey=' + $('#stateKey').val() + '&formCode=' + encodeURIComponent($('#formCode_hidden').val()) + '&update=true';
                    $("#builderModal").attr("action", str);
                    $('#builderModal').submit();
                });
                
                $("#comboChooseOperation").append($('<option>').val(1).text('Init Properties and messages'));
                $("#comboChooseOperation").append($('<option>').val(2).text('Update Cache'));
                $("#comboChooseOperation").append($('<option>').val(3).text('Create Version Data Script (in c:/Logs)'));
                $("#comboChooseOperation").append($('<option>').val(4).text('Data Migration'));
                $("#comboChooseOperation").append($('<option>').val(5).text('Adding Data For Performance'));
                $("#comboChooseOperation").append($('<option>').val(6).text('Delete Form Data'));
                $("#comboChooseOperation").append($('<option>').val(7).text('Find out all duplicate materials'));
                
                getForms();
                init();
                dataTableStyle("table");
                hideEditCatalogFields();
                initDatatableHeader("table");

                //dialog
                var dialog, form, dialogDelete, dialogMigration;

                function onSaveBtn() {
                    if ($('#formCode_Dialog').val() == "")
                        return;
                    if ($('#dialog-form').dialog('option', 'title') == 'New Form') {
                    	showWaitMessage("Please wait...");
                        $.ajax({
                            type: 'POST',
                            data: '{"action" : "getFormInformation","' + 'data":[{"code":"formCode","val":"' + $('#formCode_Dialog').val() + '"}],' + '"errorMsg":""}',
                            url: "./getFormInformation.request",
                            contentType: 'application/json',
                            dataType: 'json',
                            success: function(obj) {
                                if (obj.errorMsg != null && obj.errorMsg != '') {
                                    displayAlertDialog(obj.errorMsg);
                                    hideWaitMessage();
                                } else if (obj.data[0].val == "-1") {
                                    displayAlertDialog("insert failed");
                                    hideWaitMessage();
                                } else {
                                   if(obj.data[0].val == "")
                                	   newForm();                                   
                                   else{
                                	   displayAlertDialog("Form name already exists");
                                	   hideWaitMessage();
                                   }
                                }
                            },
                            error: handleAjaxError
                        });
                    } else if ($('#dialog-form').dialog('option', 'title') == 'Create Like') {
                    	showWaitMessage("Please wait...");
                    	 $.ajax({
                             type: 'POST',
                             data: '{"action" : "getFormInformation","' + 'data":[{"code":"formCode","val":"' + $('#formCode_Dialog').val() + '"}],' + '"errorMsg":""}',
                             url: "./getFormInformation.request",
                             contentType: 'application/json',
                             dataType: 'json',
                             success: function(obj) {
                                 if (obj.errorMsg != null && obj.errorMsg != '') {
                                     displayAlertDialog(obj.errorMsg);
                                 } else if (obj.data[0].val == "-1") {
                                     displayAlertDialog("insert failed");
                                     hideWaitMessage();
                                 } else {
                                    if(obj.data[0].val == "")
                                    	createLike();
                                    else{
                                 	   displayAlertDialog("Form name already exists");
                                 	  hideWaitMessage();
                                    }
                                 }
                             },
                             error: handleAjaxError
                         });
                    } else if ($('#dialog-form').dialog('option', 'title') == 'Update Form') {
                        var str = './demoFormBuilderInit.request?stateKey=' + $('#stateKey').val() + '&formCode=' + encodeURIComponent($('#formCode_hidden').val()) + '&update=true';
                        $("#builderModal").attr("action", str);
                        $('#builderModal').submit();
                    }
                }
                
                function deleteFormData() {
                	showWaitMessage("Please wait...");
	                $.ajax({
	                     type: 'POST',
	                     data: '{"data":[{"code":"operation","val":"' + $('#comboChooseOperation option:selected').val() + 
	                     			 '"},{"code":"formCode","val":"' + $("#dialog-form-delete #formCode_delete_data").val() + '"},{"code":"deleteFormDef","val":"' + $("#dialog-form-delete #formCode_delete_def").val() + 
                				  	 '"}],' + '"errorMsg":""}',  
	                     url: "./setCache.request",
	                     contentType: 'application/json',
	                     dataType: 'json',
	                     success: function(obj) {
	                         if (obj.errorMsg != null && obj.errorMsg != '') {
	                             displayAlertDialog(obj.errorMsg);
	                             hideWaitMessage();
	                         }  else if (obj.data[0].val == "-1") {
	                             displayAlertDialog("insert failed");
	                             hideWaitMessage(); 
	                         } else if ( $('#comboChooseOperation option:selected').val() == 0) {
	                          	 displayAlertDialog("Choose value in drop-down list");
	                         } else {
	                         	hideWaitMessage();
	                         	$("#dialog-form-delete").dialog("close");
	                        	displayAlertDialog("Done!");
	                         }
	                     },
	                     error: handleAjaxError
	                 });
                }
                
                function dataMigration() {
                	showWaitMessage("Please wait...");
	                $.ajax({
	                     type: 'POST',
	                     data: '{"data":[{"code":"operation","val":"' + $('#comboChooseOperation option:selected').val() + 
	                     			 '"},{"code":"formCode","val":"' + $("#dialog-data-migration #formCode_migration_parameter").val() + '"}],' + '"errorMsg":""}',  
	                     url: "./setCache.request",
	                     contentType: 'application/json',
	                     dataType: 'json',
	                     success: function(obj) {
	                         if (obj.errorMsg != null && obj.errorMsg != '') {
	                             displayAlertDialog(obj.errorMsg);
	                             hideWaitMessage();
	                         }  else if (obj.data[0].val == "-1") {
	                             displayAlertDialog("data migration failed");
	                             $("#dialog-data-migration").dialog("close");
	                             hideWaitMessage(); 
	                         } else if ( $('#comboChooseOperation option:selected').val() == 0) {
	                          	 displayAlertDialog("Choose value in drop-down list");
	                         } else {
	                         	hideWaitMessage();
	                         	$("#dialog-data-migration").dialog("close");
	                        	displayAlertDialog("Done!");
	                         }
	                     },
	                     error: handleAjaxError
	                 });
                }
                
                dialog = $("#dialog-form").dialog({
                    autoOpen: false,
                    height: 200,
                    width: 300,
                    modal: true,
                    buttons: {
                        "Continue": onSaveBtn,
                        Cancel: function() {
                            $('#createLikeDiv').css('display', 'none');
                            $('#newFormDiv').css('display', 'none');
                            dialog.dialog("close");
                        }
                    },
                    close: function() {
                        $('#createLikeDiv').css('display', 'none');
                        $('#newFormDiv').css('display', 'none');
                    }
                });
                
		    	dialogDelete = $("#dialog-form-delete").dialog({
			        autoOpen: false,
			        title: 'Delete Form Data',
			        height: 165,
			        width: 455,
			        modal: false,
			        buttons: {
			                        "Delete": deleteFormData,
			                        Cancel: function() {
			                            //$('#createLikeDiv').css('display', 'none');
			                            //$('#newFormDiv').css('display', 'none');
			                            dialogDelete.dialog("close");
			                        }
			                    }, 
			                     close: function() {
			                        // $('#createLikeDiv').css('display', 'none');
			                        // $('#newFormDiv').css('display', 'none');
			                        dialogDelete.dialog("close");
			                    },
			        overflow:"auto"
		    	});
		    	
		    	dialogMigration = $("#dialog-data-migration").dialog({
			        autoOpen: false,
			        title: 'Data Migration',
			        height: 155,
			        width: 750,
			        modal: false,
			        buttons: {
			        				"Run Migration": dataMigration,
			                        Cancel: function() {
			                            //$('#createLikeDiv').css('display', 'none');
			                            //$('#newFormDiv').css('display', 'none');
			                            dialogMigration.dialog("close");
			                        }
			                    }, 
			                     close: function() {
			                        // $('#createLikeDiv').css('display', 'none');
			                        // $('#newFormDiv').css('display', 'none');
			                        dialogMigration.dialog("close");
			                    },
			        overflow:"auto"
		    	});
		    	
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
	            // Apply the search //kd 25092019 added filter to the tables
				table.columns().every(function (index) {
				   $('#table thead tr:eq(1) th:eq(' + index + ') input').on('keyup change', function () {
				       table.column($(this).parent().index() + ':visible')
				           .search(this.value)
				           .draw();
				   });
				}); 
                
            });

            function getForms() {
                //get all forms
                var jsonInnerLayer = JSON
                    .parse($
                        .ajax({
                            type: 'POST',
                            data: '{"action" : "getForms","data":[{"code":"","val":""}],"errorMsg":""}',
                            url: "./getForms.request",
                            contentType: 'application/json',
                            dataType: 'json',
                            error: handleAjaxError,
                            async: false
                        }).responseText).data[0].val;
                var jsonOuterLayer = JSON.parse(jsonInnerLayer)
                var i;
                for (i = 0; i < Object.keys(jsonOuterLayer).length; i++) {
                    $('#table').DataTable().row.add(jsonOuterLayer[i].split(";")).draw(false);
                }
            }

            function init() {
                //init page
                var queryString = QueryString();
                if (typeof queryString.formCode !== "undefined") {
                    $('#formCode_hidden').val(queryString.formCode);
                    $('#formCode_text').val(queryString.formCode);
                    var table = $('#table').DataTable();
                    var allData = table.rows().data();
                    var i, count = 0,
                        isFound = 0;
                    //select last value
                    for (i = 0; i < allData.length; i++) {
                        if (count > $('#table tbody tr').length - 1) {
                            table.page('next').draw('page');
                            count = 0;
                        }
                        if (allData[i][0] == queryString.formCode) {
                            $('#table tbody tr').eq(count).addClass('selected');
                            $('#updateForm').button("option", "disabled", false);
                            $('#createLike').button("option", "disabled", false);
                            $('#previewBtn').button("option", "disabled", false);
                            isFound = 1;
                            break;
                        }
                        count++;
                    }
                    if (!isFound)
                        table.page(0).draw('page');
                    $('#updateForm').prop('disabled', false);
                    $('#createLike').prop('disabled', false);
                    $('#previewBtn').prop('disabled', false);
                }
                fixDatatablePageDisabledInit();
            }

            function QueryString() {

                // the return value is assigned to QueryString!
                var query_string = {};
                var query = window.location.search.substring(1);
                var vars = query.split("&");
                for (var i = 0; i < vars.length; i++) {
                    var pair = vars[i].split("=");
                    // If first entry with this name
                    if (typeof query_string[pair[0]] === "undefined") {
                        query_string[pair[0]] = decodeURIComponent(pair[1]);
                        // If second entry with this name
                    } else if (typeof query_string[pair[0]] === "string") {
                        var arr = [query_string[pair[0]], decodeURIComponent(pair[1])];
                        query_string[pair[0]] = arr;
                        // If third or later entry with this name
                    } else {
                        query_string[pair[0]].push(decodeURIComponent(pair[1]));
                    }
                }
                return query_string;
            }

            function openDialog(title) {
                // open dialog 
                $('#formCode_Dialog').val('');
                $('#formCode_Dialog').removeClass("disabledbutton");
                if (title == 'Create Like') {
                    $('#createLikeDiv').css('display', 'inline-block');
                } else if (title == 'New Form') {
                    $('#newFormDiv').css('display', 'inline-block');
                }
                $('#dialog-form').dialog('open');
                $('#dialog-form').dialog('option', 'title', title);
                $('.ui-dialog-buttonset button:contains("Continue")').button("option", "disabled", true);
                if (title == 'Update Form') {
                    // 			$('#formCode_Dialog').val($('#formCode_hidden').val());
                    // 			$('#formCode_Dialog').blur();
                    // 			$('#formCode_Dialog').addClass("disabledbutton");
                    // 			$('.ui-dialog-buttonset button:contains("Continue")').button( "option", "disabled", false );
                } else if (title == 'Create Like') {
                    $('#formCode_create_Like').val($('#formCode_hidden').val());
                    $('#formCode_create_Like').addClass("disabledbutton");
                    $('#formCode_create_Like').blur();
                    $('#formCode_Dialog').focus();
                }
            }

            function fieldsRequired() {
                // check if formCode is not empty
                if ($('#formCode_Dialog').val() != "")
                    $('.ui-dialog-buttonset button:contains("Continue")').button("option", "disabled", false);
                else
                    $('.ui-dialog-buttonset button:contains("Continue")').button("option", "disabled", true);
            }

            function updateCatalogForNewForm(formCode) {
                //on update button
                $.ajax({
                    type: 'POST',
                    data: '{"action" : "updateCatalogForNewForm","' + 'data":[{"code":"formCode","val":"' + formCode + '"},{"code":"type","val":"' + $('[name="formType"]').val() + '"}],' + '"errorMsg":""}',
                    url: "./updateCatalogForNewForm.request",
                    contentType: 'application/json',
                    dataType: 'json',
                    success: function(obj) {
                        if (obj.errorMsg != null && obj.errorMsg != '') {
                            displayAlertDialog(obj.errorMsg);
                            hideWaitMessage();
                        } else if (obj.data[0].val == "-1") {
                            displayAlertDialog("insert failed");
                            hideWaitMessage();
                        } else {                        	
                            $('#builderModal').submit();
                        }
                    },
                    error: handleAjaxError
                });
            }

            function updateForm() {
                var str = './demoFormBuilderInit.request?stateKey=' + $('#stateKey').val() + '&formCode=' + encodeURIComponent($('#formCode_hidden').val()) + '&update=true';
                $("#builderModal").attr("action", str);
                $('#builderModal').submit();
            }
            function newForm() {
            	 var formCode = $('#formCode_Dialog').val();
                 var str = './demoFormBuilderInit.request?stateKey=' + $('#stateKey').val() + '&formCode=' + encodeURIComponent(formCode);
                 str = ($('[name="formType"]').val() == 'REF') ? str + '&update=true' : str;
                 $("#builderModal").attr("action", str);
                 $.ajax({
                     type: 'POST',
                     data: '{"action" : "newForm","' + 'data":[{"code":"formCode","val":"' + $('#formCode_Dialog').val() + '"},{"code":"description","val":"' + '"},{"code":"type","val":"' + $('[name="formType"]').val() + '"},{"code":"title","val":"' + '"},{"code":"subtitle","val":"' + '"},{"code":"useAsTemplate","val":"0' + '"},{"code":"active","val":"0"},{"code":"FormOrder","val":"0"},{"code":"GroupName","val":""},{"code":"formCodeEntity","val":"' + $('#formCode_Dialog').val() + '"},{"code":"ignoreNav","val":"0"}, {"code":"useCache","val":"0"}],' + '"errorMsg":""}',
                     url: "./newForm.request",
                     contentType: 'application/json',
                     dataType: 'json',
                     success: function(obj) {
                         if (obj.errorMsg != null && obj.errorMsg != '') {
                             displayAlertDialog(obj.errorMsg);
                             hideWaitMessage();
                         } else if (obj.data[0].val == "-1") {
                             displayAlertDialog("insert failed");
                             hideWaitMessage();
                         } else {
                             if (canEditCatalog()) {                            	
                                 $('#builderModal').submit();
                             } else {                            	
                                 updateCatalogForNewForm(formCode);
                             }
                         }
                     },
                     error: handleAjaxError
                 });
            }
 			function createLike() {
 				 var formCode = $('#formCode_Dialog').val();
                 $.ajax({
                     type: 'POST',
                     data: '{"action" : "createLike","' + 'data":[{"code":"newFormCode","val":"' + $('#formCode_Dialog').val() + '"},{"code":"oldFormCode","val":"' + $('#formCode_create_Like').val() + '"}],' + '"errorMsg":""}',
                     url: "./createLike.request",
                     contentType: 'application/json',
                     dataType: 'json',
                     success: function(obj) {
                         if (obj.errorMsg != null && obj.errorMsg != '') {
                             displayAlertDialog(obj.errorMsg);
                             hideWaitMessage();
                         } else if (obj.data[0].val == "-1") {
                             displayAlertDialog("insert failed");
                             hideWaitMessage();
                         } else {
                        	 hideWaitMessage();
                             var str = './demoFormBuilderInit.request?stateKey=' + $('#stateKey').val() + '&formCode=' + encodeURIComponent(formCode) + '&createLike=true';
                             $("#builderModal").attr("action", str);
                             $('#builderModal').submit();
                         }
                     },
                     error: handleAjaxError
                 });
            }
            
             function viewConnectionLog() {
                $('#viewConnectionLogForm').attr('action', 'viewConnectionLog.request');
				$('#viewConnectionLogForm').submit();	
            }
            
             function setCacheOrOpenDialogDelete() {
                // open dialog 
                if ($('#comboChooseOperation option:selected').val() != 6 && $('#comboChooseOperation option:selected').val() != 4) {
                	setCache()
                } else if ($('#comboChooseOperation option:selected').val() == 6) {
                
//	                $('#formCode_delete_data').removeClass("disabledbutton");
	                /* $('#formCode_Dialog').val('');
	                $('#formCode_Dialog').removeClass("disabledbutton");
	                if (title == 'Create Like') {
	                    $('#createLikeDiv').css('display', 'inline-block');
	                } else if (title == 'New Form') {
	                    $('#newFormDiv').css('display', 'inline-block');
	                } */
	                $('#dialog-form-delete').dialog('open');
	                
	                $('#formCode_delete_data').val($('#formCode_hidden').val());
	                $('#formCode_delete_def').val(0);
	                
//                    $('#formCode_delete_data').addClass("disabledbutton");
                    $('#formCode_delete_data').blur();
                    $('#formCode_delete_data').focus();
	                
	                //$('#dialog-form-delete').dialog('option', 'title', title);
	                //$('.ui-dialog-buttonset button:contains("Continue")').button("option", "disabled", true);
	                /* if (title == 'Update Form') {
	                    // 			$('#formCode_Dialog').val($('#formCode_hidden').val());
	                    // 			$('#formCode_Dialog').blur();
	                    // 			$('#formCode_Dialog').addClass("disabledbutton");
	                    // 			$('.ui-dialog-buttonset button:contains("Continue")').button( "option", "disabled", false );
	                } else if (title == 'Create Like') {
	                    $('#formCode_create_Like').val($('#formCode_hidden').val());
	                    $('#formCode_create_Like').addClass("disabledbutton");
	                    $('#formCode_create_Like').blur();
	                    $('#formCode_Dialog').focus();
	                } */
	            } else if ($('#comboChooseOperation option:selected').val() == 4) {
                
	                $('#dialog-data-migration').dialog('open');
	                
	                $('#formCode_migration_parameter').val();
	                
                    $('#formCode_migration_parameter').blur();
                    $('#formCode_migration_parameter').focus();
	                
	            }
            }
             function setCache() {
            	 $.ajax({
                     type: 'POST',
                     data: '{"data":[{"code":"operation","val":"' + $('#comboChooseOperation option:selected').val() + '"}],' + '"errorMsg":""}',
                     url: "./setCache.request",
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
                        	 displayAlertDialog("Done!");
                         }
                     },
                     error: handleAjaxError
                 });
             }
             
             function buildPreview() {
		        //build Preview of form
		        var page = "./init.request?formCode=" + encodeURIComponent($("#formCode_hidden").val()) + "&formId=" + $('#selectFormId').val() + "&userId=" + $('#userId').val();
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
            
        </script>
    </head>

    <body>
        <form style="display:none;" action="" method="post" id="builderModal"></form>
        <form style="display:none;" action="" method="get" id="viewConnectionLogForm"></form>
        <!-- move to form builder page -->
        <table style="width: 100%; text-align: center;">
            <tr>
                <td>
                    <%@ include file="PageHeaderNewForm.inc"%>
                </td>
            </tr>
            </tr>
				<div id="menuDiv">
		      		<%@ include file="../include/includeMenu.jsp" %> 
		      	</div>
			<tr>
            <!-- <tr>
                <td style="line-height:40px;">&nbsp;
                </td>
            </tr>  -->            
			<tr><td colspan="10" style="text-align:right;font-size: 10pt;"><a href="#" id="spnLogConnection" onclick="viewConnectionLog()">view connection log</a></td></tr>
            <tr>
                <td class="ui-widget-content ui-corner-all" colspan="10">
                    <table width="100%">
                        <tr>
                            <td style="text-align: left; padding-left: 15px;">                            
                                <h2 class="InitiationTitle" style="display:inline-block;">
								<label lang_key='Form Builder Main'
									style="text-transform: uppercase;"><spring:message code="FormBuilderMain" text="FormBuilderMain"/></label>
							</h2>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <div id="tableDiv">
                                    <table id="table" class="display">
                                        <thead>
                                            <tr>
                                                <th>
                                                   <div><spring:message code="FormName" text="FormName" /></div>
                                                </th>
                                                <th>
                                                    <div><spring:message code="Title" text="Title" /></div>
                                                </th>
                                                <th>
                                                    <div><spring:message code="Subtitle" text="Subtitle" /></div>
                                                </th>
                                                <th>
                                                    <div><spring:message code="Description" text="Description" /></div>
                                                </th>
                                                <th>
                                                    <div><spring:message code="Active" text="Active" /></div>
                                                </th>
                                                <th>
                                                    <div><spring:message code="Type" text="Type" /></div>
                                                </th>
                                                <th>
                                                    <div><spring:message code="UseAsTemplate" text="UseAsTemplate" /></div>
                                                </th>
                                            </tr>
                                            <tr>
                                            	<th>
                                                   <div ></div>
                                                </th>
                                                <th>
                                                    <div></div>
                                                </th>
                                                <th>
                                                    <div></div>
                                                </th>
                                                <th>
                                                    <div></div>
                                                </th>
                                                <th>
                                                    <div></div>
                                                </th>
                                                <th>
                                                    <div></div>
                                                </th>
                                                <th>
                                                    <div></div>
                                                </th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                        </tbody>
                                    </table>
                                </div>
                                <div>&nbsp;</div>
                                <div>&nbsp;</div>
                                <div style="text-align: center;">

                                    <button id="newForm" onclick="openDialog('New Form');">
                                        <spring:message code="New" text="" />
                                    </button>

                                    <button id="updateForm" onclick="updateForm();" disabled>
                                        <spring:message code="Update" text="" />
                                    </button>

                                    <button id="createLike" onclick="openDialog('Create Like');" disabled>
                                        <spring:message code="CreateLike" text="" />
                                    </button>
                                    
                                    <button id="previewBtn" onclick="getFormsId('selectFormId');$('#beforePreview-dialog').dialog('open');" disabled><!--       buildPreview() -->
                                    	<spring:message code="Preview" text="" />
                                	</button>
                                    
                                    
                                    <script type="text/javascript">
										var hidden = '<%=request.getAttribute("userName")%>';
										hidden = hidden.toUpperCase();
										if(hidden == "SYSTEM") {
											document.writeln("&nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp;");
											document.writeln("<select name='comboChooseOperation' id='comboChooseOperation' style='font-size:11;text-align:left;width:300px; '>");
											document.writeln("<option value='0'>Choose</option></select>");
											document.writeln("<button id='update cache' style='width:80px;' onclick='setCacheOrOpenDialogDelete();' >Execute</button>");
										} else {
											document.writeln("<select name='comboChooseOperation' id='comboChooseOperation' style='font-size:6;text-align:left;width:1px; visibility:hidden; '>");
											document.writeln("<option value='0'>Choose</option></select>");
											document.writeln("<button id='update cache' style='width:1px;visibility:hidden;' onclick='setCacheOrOpenDialogDelete();' >Execute</button>"); 
										} 
									</script>
                                    <!-- <select name="comboChooseOperation" id="comboChooseOperation" style="font-size:11;text-align:left;width:300px; visibility:hidden;">
                                            <option value="0">Choose</option>
                                    </select>
                                    
                                    <button id="update cache" style="width:80px; visibility:hidden;" onclick="setCacheOrOpenDialogDelete();" >Execute</button> -->
                                    
									<input type="hidden" id="stateKey" name="stateKey" value="${stateKey}">
									<input type="hidden" id="formPathInfo" name="formPathInfo" value='${formPathInfo}'>
                                    <input type="hidden" id="formCode_hidden">
                                    <input type="hidden" id="description_hidden">
                                    <input type="hidden" id="active_hidden">
                                    <input type="hidden" id="formType_hidden">
                                    <input type="hidden" id="title_hidden">
                                    <input type="hidden" id="subtitle_hidden">
                                    <input type="hidden" id="userId" value='<%=request.getAttribute("userId")%>' />
                                    <input type="hidden" id="canEditCatalog" value='<%=request.getAttribute("canEditCatalog")%>' />
                                    <input type="hidden" id="formCode_text">
                                </div>
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
        <div id="dialog-form" style="display:none;">
            <lable class="cssStaticData ">
                <spring:message code="FormName" text="" />:</lable>
            <input type="text" class="elementIndiv alphanumInput" id="formCode_Dialog" onkeyup="fieldsRequired();" style="width: 150px;">
            <div id="createLikeDiv" style="margin-top: 10px;display:none;">
                <lable class="cssStaticData ">
                    <spring:message code="CreateLike" text="" />:</lable>
                <input type="text" class="elementIndiv alphanumInput" id="formCode_create_Like" style="width: 150px;">
            </div>
            <div id="newFormDiv" style="margin-top: 10px;display:none;">
                <lable class="cssStaticData ">
                    <spring:message code="FormType" text="" />:</lable>
                <select name="formType" style="width: 120px;" class="elementIndiv"></select>
            </div>
        </div>
		<div id="dialog-form-delete" >
		     		 <table style="width:100%;max-width:100%;"> 
<%-- 			<lable class="cssStaticData ">
                <spring:message code="FormName" text="" />:</lable>
            <input type="text" class="elementIndiv alphanumInput" id="formCode_Dialog" onkeyup="fieldsRequired();" style="width: 150px;"> --%>
            <!-- <div id="createLikeDiv" style="margin-top: 10px;display:none;"> -->
                <tr> <td> <lable class="cssStaticData " >
                    		<spring:message code="DeleteDataForForm" text="" />:</lable></td>
                <td><input type="text" class="elementIndiv alphanumInput" id="formCode_delete_data" style="width: 150px;"></td></tr>
                <!-- <div id="formDef" style="margin-top: 10px;"> -->
	            <tr> <td> <lable class="cssStaticData " >
	                    	<spring:message code="DeleteDef" text="" />:</lable></td>
	            <td><input type="text" class="elementIndiv alphanumInput" id="formCode_delete_def" style="width: 150px;"></td> </tr>
	            <!--  </div> -->
            </table>
		</div>
		<div id="dialog-data-migration" >
		    <table style="width:100%;max-width:100%;"> 
                <tr> <td> <lable class="cssStaticData " >
                    <spring:message code="DataMigrationParam" text="" />:</lable></td>
                <td><input type="text" class="elementIndiv alphanumInput" id="formCode_migration_parameter" style="width: 150px;"></td></tr>
            </table>
		</div>
        <div id="beforePreview-dialog" title="Form Id">
        <label class="cssStaticData">Choose Form Id:</label>
            <select id="selectFormId" name="selectFormId"><option value="-1">new</option></select>
        </div>
    </body>
 </html>