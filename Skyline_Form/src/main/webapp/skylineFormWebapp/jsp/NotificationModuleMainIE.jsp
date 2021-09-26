<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!doctype html>
<html lang="en">

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

	<meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
	 <link rel="icon" href="../skylineFormWebapp/images/favicon.png?<spring:message code="Env" text="" />" />   
	<link href="../skylineFormWebapp/CSS/comply_theme/Skyline_9.css" rel="stylesheet"  type="text/css">
	<link href="../skylineFormWebapp/CSS/comply_theme/jquery-ui.custom.css" rel="stylesheet" type="text/css" media="all" />		
	<link href="../skylineFormWebapp/CSS/comply_theme/demo_table_jui.css" rel="stylesheet" type="text/css" />
	<link href="../skylineFormWebapp/CSS/comply_theme/jHtmlArea.css" rel="stylesheet" type="text/css" />
	<link href="../skylineFormWebapp/CSS/comply_theme/jHtmlArea.ColorPickerMenu.css" rel="stylesheet" type="text/css" />
	 
	<script src="../skylineFormWebapp/js/comply_js/jquery.js" type="text/javascript"></script>
	<script src="../skylineFormWebapp/js/comply_js/jquery.dataTables.js" type="text/javascript"></script>  
	<script src="../skylineFormWebapp/js/comply_js/jquery-ui.custom.js" type="text/javascript"></script>
	<script src="../skylineFormWebapp/js/comply_js/CommonFuncs.js" type="text/javascript"></script>	 	
	<script src="../skylineFormWebapp/js/comply_js/jHtmlArea.js" type="text/javascript"></script>    
	<script src="../skylineFormWebapp/js/comply_js/jHtmlArea.ColorPickerMenu.js" type="text/javascript"></script>
	<script src="../skylineFormWebapp/js/comply_js/jquery.caret.js" type="text/javascript"></script>	 
        <script src="../skylineFormWebapp/js/comply_js/jquery-migrate.js" type="text/javascript"></script>
        <script src="../skylineFormWebapp/js/comply_js/loading.js" type="text/javascript"></script>
        
	<title>
		Adama - Notification Module
	</title>
	
	<style type="text/css">
		
	/* css for rich text editor toolbar */
	div.jHtmlArea .ToolBar ul li a.customButton 
	{
		visibility: hidden; /* shown when a field is selected */
		width: 25px;
		background: url(../skylineFormWebapp/images/sort_asc.gif) no-repeat; /* js/images/disk.png */
		background-position: 0 -3;
	}
	
	div.jHtmlArea { border: solid 1px #ccc; }
	
	/* css for paste-to-subject button */
	#btnPasteField
	{
		cursor: pointer; 
		width: 25px; 
		height: 25px; 
		background: url(../skylineFormWebapp/images/arrow_left.gif) no-repeat; 
		background-position: -3 -3; 
		visibility:hidden; 
	}
    </style>	
	
    <script type="text/javascript">
	var bOnChange = false;	
	var oTable;
	var totalRecords = 0;  
	var activeTab;
	var activeDiv = null;	
	var selActive;
	var rowID;
	var rowSelected;
	var selectedRowOrder;
	var newRowOrder;
	var displayStart = 0;
	var currNotificationID = 0;
	var isNewNotification = false;
	var currConditionID = 0;	
	var currAddresseeID = 0;
	var isEditCondition = false;
	var blurred = null;
    var columnNumberAlertFlag = false;
    
    $(document).ready(function () 
    { 
		$('.button').button();
		$('#tabs').tabs();
		$('#tabs').tabs({ active: 0, beforeActivate:beforeShowTab, activate:onShowTab });
		/* LoadLabelsJson(); */ 		
		var act = $('#action_id').val();
		
		// store id of elements that lose focus for the use of Message Subject caret keeping
		$('*').on('blur', function () { 
			blurred = $(this).attr('id'); 
		});
	
		var dataMods = $.parseJSON($('#ddlModuleName_json').val()); 
		var dataMsgs = $.parseJSON($('#ddlMsgType_json').val());
		var dataTriggerType = $.parseJSON($('#ddlTriggerType_json').val());
		var dataSched = $.parseJSON($('#ddlScheduler_json').val());
		var dataOnSave = $.parseJSON($('#ddlOnSave_json').val());
		var dataFields = $.parseJSON($('#ddlAvailFields_json').val());
		var dataAddType = $.parseJSON($('#rdAddressType_json').val());

		$(dataMods).map(function () 
		{
			$("#ddlModuleName").append($('<option>').val(this.MODULE_ID).text(this.MODULE_NAME));
		}); 
		
		$(dataMsgs).map(function () 
		{
			$("#ddlMessageType").append($('<option>').val(this.MESSAGE_TYPE_ID).text(this.MESSAGE_TYPE_NAME));
		});
		
		$(dataTriggerType).map(function () 
		{
			$("#ddlTriggerType").append($('<option>').val(this.TRIGGER_TYPE_ID).text(this.TRIGGER_TYPE_NAME));
		});
		
		$(dataSched).map(function () 
		{
			$("#ddlScheduler").append($('<option>').val(this.SCHEDULER_ID).text(this.SCHEDULER_NAME));
		});
		
		$(dataOnSave).map(function () 
		{
			$("#ddlOnSave").append($('<option>').val(this.ON_SAVE_ID).text(this.ON_SAVE_NAME));
		});
		
		$(dataFields).map(function () 
		{
			$("#ddlAvailFields").append($('<option>').val(this.FIELD_NAME).text(this.DISPLAY_NAME)); 
			$("#ddlConditionField").append($('<option>').val(this.FIELD_ID).text(this.DISPLAY_NAME).attr('datatype', this.DATA_TYPE));		
		});
		
		$(dataAddType).map(function ()
		{
			$("#tdRadioAddressType").append($('<input>').prop('type','radio').prop('id','rd'+this.text).attr('name','rdAddressType').attr('typename', this.text)
									.val(this.id).on('change', function (event){ loadAddresseeList(this.value); setParametrOfChange(); }))									
									.append('<span>' + this.text + '</>');
			$("#tdRadioAddressType").append('<span>&nbsp;&nbsp;</>');
		});
		
		$('#btnSelectField').prop('disabled',true);		
		
		if(act == 'new')
		{
			isNewNotification = true;				
		}	
		
		$('#MessageCriteria').on('click', function (event) {
			if ( !bOnChange )
			{
				loadCriteriaTable();
				cancelCondition();
			}
		});	
		
		$('#tblCriteria tbody').on('click', function(event) {
			var has_row_selected = 0;
			if($(event.target.parentNode).children().hasClass('row_light'))
			{ // raise flag if any rows were already selected
				has_row_selected = 1;
			}
			
			$('#tblCriteria').find('.row_light').each(function()
			{ // remove all previously selected td's
				$(this).removeClass("row_light"); 
				$("#btnUpdateCondition").button('option', 'disabled', true);
				$("#btnDeleteCondition").button('option', 'disabled', true);
			});
			
			if(has_row_selected == 0)
			{	
				if ($(event.target.parentNode).hasClass('row_selected')) 
				{  
				   $("#btnUpdateCondition").button('option', 'disabled', true);
				   $("#btnDeleteCondition").button('option', 'disabled', true);
				   $(event.target.parentNode).removeClass('row_selected');    
				} 
				else 
				{
					$(oTable.fnSettings().aoData).each(function () {
						$(this.nTr).removeClass('row_selected');
					});                 
					if(totalRecords > 0)
					{ 						
						$("#btnUpdateCondition").button('option', 'disabled', false);
						$("#btnDeleteCondition").button('option', 'disabled', false);						
						$(event.target.parentNode).addClass('row_selected');
					}
				}
			}
		}); 
		
		$('#tblCriteria tbody').on('click', 'tr', function (event){
			var aData = oTable.fnGetData(this); 
			if(aData != null && totalRecords != 0)
			{
				$('#selConditionID').val(aData[0]);
				$('#selConditionType').val(aData[1]);
				$('#selFieldID').val(aData[3]); 
				$('#selOperatorID').val(aData[5]);
				$('#selConditionValue').val(aData[6]);
				currConditionID = $('#selConditionID').val();				
			}
			$('#fsConditionProperties').css('visibility','hidden'); // hide if was shown because of different selection
		}); 

		$('#DistList').on('click', function (event)	{ 
			if ( !bOnChange )
			{
				loadDistributionTable();
				cancelAddressee(); 
			}
		});
			
		$('#tblDistribution tbody').on('click', function (event) {
			var has_row_selected = 0;
			if($(event.target.parentNode).children().hasClass('row_light'))
			{ // raise flag if any rows were already selected
				has_row_selected = 1;
			} 
			
			$('#tblDistribution').find('.row_light').each(function()
			{ // remove all previously selected rows
				$(this).removeClass("row_light"); 
				$("#btnUpdateAddressee").button('option', 'disabled', true);
				$("#btnDeleteAddressee").button('option', 'disabled', true);
			});
			
			if(has_row_selected == 0)
			{	
				if ($(event.target.parentNode).hasClass('row_selected')) 
				{  // 
				   $("#btnUpdateAddressee").button('option', 'disabled', true);
				   $("#btnDeleteAddressee").button('option', 'disabled', true);
				   $(event.target.parentNode).removeClass('row_selected');    
				} 
				else 
				{
					$(oTable.fnSettings().aoData).each(function () {
						$(this.nTr).removeClass('row_selected');
					});                 
					if(totalRecords > 0)
					{ 						
						$("#btnUpdateAddressee").button('option', 'disabled', false);
						$("#btnDeleteAddressee").button('option', 'disabled', false);						
						$(event.target.parentNode).addClass('row_selected');
					}
				}
			}
		}); 
		
		$('#tblDistribution tbody').on('click', 'tr', function (event){
			var aData = oTable.fnGetData(this); 
			if(aData != null && totalRecords != 0)
			{
				$('#selAddresseeID').val(aData[0]);
				$('#selAddressTypeID').val(aData[2]);
				$('#selUserID').val(aData[4]); 							
				$('#selSendType').val(aData[5]);
				currAddresseeID = $('#selAddresseeID').val();
				$('#selEmailExternal').val(aData[3]);
				if(aData[6] == 1)
				{
					$('#cbMessageOnly').prop('checked', true);
				}
				else
				{
					$('#cbMessageOnly').prop('checked', false);
				}
			}
			$('#fsDistributionProperties').css('visibility','hidden'); // hide if was shown because of different selection
		});
		
		$('#ddlAvailFields').on('change', function(){ 			
			if ($('option:selected', this).length)		
			{			
				// $('.enableOnSelect').prop('disabled', false);
				selectField();
			}			
		});	
		
		// keep start and end carets for Message Subject upon clicking and typing
		$('#txtMsgSubject').on('mouseup', function() {			
			$('#txtMsgSubjectCaretStart').val($(this).caret().start);
			$('#txtMsgSubjectCaretEnd').val($(this).caret().end);
		});
		
		$('#txtMsgSubject').on('keyup', function() {			
			$('#txtMsgSubjectCaretStart').val($(this).caret().start);
			$('#txtMsgSubjectCaretEnd').val($(this).caret().end);
		});
		
		// if selection was just made with Subject, insert selected field where the selection is
		// otherwise, append selected field at the end of the Subject text
		$('#btnPasteField').on('click', function() {			
			var originalSubject = $('#txtMsgSubject').val();
			var updatedSubject; 			
			var iCaretStart = parseInt($('#txtMsgSubjectCaretStart').val()); 
			var iCaretEnd = parseInt($('#txtMsgSubjectCaretEnd').val()); 
			
			if (iCaretStart == NaN)
			{
				iCaretStart = 0;
			}
			
			if (iCaretEnd == NaN)
			{
				iCaretEnd = iCaretStart;
			}
			
			if (blurred == 'txtMsgSubject')
			{
				updatedSubject = originalSubject.substring(0, iCaretStart) + 
								 $('#txtSelectedField').val() + 
								 originalSubject.substr(iCaretEnd);
			}
			else
			{
				updatedSubject =  $('#txtMsgSubject').val() + $('#txtSelectedField').val();
			}
			$('#txtMsgSubject').val(updatedSubject);
		});
		
		initUserCommentDiv();
		initConfirmDialogDiv();
       	initWaitMessageDiv();
		getNotificationInfo();
	});
    //////////// end of (DOCUMENT).READY FUNCTION  //////////////
    
    var xmlHttp1 = new getXMLObject(); //xmlhttp holds the ajax object
    
    function getXMLObject()  //XML OBJECT
    {
        var xmlHttp1 = false;
        try {
            xmlHttp1 = new ActiveXObject("Msxml2.XMLHTTP")  // For Old Microsoft Browsers
        }
        catch (e) {
        try {
            xmlHttp1 = new ActiveXObject("Microsoft.XMLHTTP")  // For Microsoft IE 6.0+
        }
        catch (e2) {
            xmlHttp1 = false   // No Browser accepts the XMLHTTP Object then false
        }
        }
        if (!xmlHttp1 && typeof XMLHttpRequest != 'undefined') {
            xmlHttp1 = new XMLHttpRequest();        //For Mozilla, Opera Browsers
        }
        return xmlHttp1;  // Mandatory Statement returning the ajax object created
    }
    
    function handleAjaxError( xhr, textStatus, error ) 
    {     
		//hideMessage();
		hideWaitMessage();
        if (xhr.status == 200 && xhr.responseText.match(/TIME_IS_OUT/)) 
        {
            top.location.href = 'Login.jsp';
        }  
    }  

	function getNotificationInfo()
	{
		currNotificationID = $('#currNotificationID').val();
        if(currNotificationID != 0)
        {
            var param = $('#displayParametersArray').val();
            $.ajax 
			({
				 type: "POST",
				 contentType: "application/json; charset=utf-8",
				 //url: "notificationmodulemainservlet?action_id=getNotificationInfo&currNotificationID=" + currNotificationID + "&pageParameters=" + param,
				 url: "./notificationModule.request?action_id=getNotificationInfo&currNotificationID=" + currNotificationID + "&pageParameters=" + param,
				 dataType: "json",
				 success: function(data)
				 { 
					$('#ddlModuleName').val(data.MODULE_ID); 
					$('#ddlMessageType').val(data.MESSAGE_TYPE_ID);
					if (data.COLUMN_NUMBER != -1)
					{
						$('#txtColumnNumber').val(data.COLUMN_NUMBER);
					}
					else
					{
						$('#txtColumnNumber').val('');
					}
					$('#txtDescription').text(data.DESCRIPTION);	
					$('#ddlTriggerType').val(data.TRIGGER_TYPE_ID);
					$('#ddlScheduler').val(data.SCHEDULER_ID);  
					$('#ddlOnSave').val(data.ON_SAVE_ID);
					$('#txtInterval').val(data.SCHEDULER_INTERVAL);					
					if(data.RESEND == 1)
					{
						$('#cbResend').prop('checked', true);
					}
					if(data.IS_ACTIVE == 1)
					{
						$('#cbActive').prop('checked', true);
					}
					if(data.COPY_TO_PRODUCTION == 1)
					{
						$('#cbCopyToProduction').prop('checked', true);
					}
					if(data.INCLUDE_ATTACHMENT == 1)
					{
						$('#cbAttachment').prop('checked', true);
					}
					$('#txtMsgSubject').val(data.EMAIL_SUBJECT);
						
					$('#ddlSendType').html(''); // several calls might be invoked and without this line the options will accumulate
					$('#ddlSendType').append('<option value="TO">TO</>');
					if(data.MESSAGE_TYPE_ID == 2) // group 
					{
						$('#tdColumnNumber').css('visibility','visible');
						$('#txtColumnNumber').css('visibility','visible');
					}
					else
					{
						$('#tdColumnNumber').css('visibility','hidden');
						$('#txtColumnNumber').css('visibility','hidden');
						$('#ddlSendType').append('<option value="CC">CC</>'); // only single message have CC
					}	
					
					if ($('#ddlTriggerType').val() == '0') // Choose
						{
							$('#lblSchedulerOnSave').css('visibility','hidden');
							$('#mandatoryMarkSchedulerOnSave').css('visibility','hidden');
							$('#ddlOnSave').prop('hidden',true);
							$('#ddlScheduler').prop('hidden',true);
							$('#tdInteraval').css('visibility','hidden');
							$('#txtInterval').css('visibility','hidden');
						}
						else if ($('#ddlTriggerType').val() == '1') // Scheduler
						{
							$('#lblSchedulerOnSave').css('visibility','visible');
							$('#mandatoryMarkSchedulerOnSave').css('visibility','visible');
							$('#lblSchedulerOnSave').text('Scheduler');
							$('#ddlOnSave').prop('hidden',true);
							$('#ddlScheduler').prop('hidden',false);
							$('#tdInteraval').css('visibility','visible');
							$('#txtInterval').css('visibility','visible');
						}
						else if ($('#ddlTriggerType').val() == '2') // On Save
						{
							$('#lblSchedulerOnSave').css('visibility','visible');
							$('#mandatoryMarkSchedulerOnSave').css('visibility','visible');
							$('#lblSchedulerOnSave').text('On Save');
							$('#ddlOnSave').prop('hidden',false);
							$('#ddlScheduler').prop('hidden',true);
							$('#tdInteraval').css('visibility','hidden');
							$('#txtInterval').css('visibility','hidden');
						}
					
					$('#lblIntervalType').html(data.SCHEDULER_NAME); 
					$('#txtMsgBody').val(data.EMAIL_BODY);
									
					newRowOrder = data.notificationRowOrder;
					var act = $('#action_id').val();	
					$('#tabs').tabs('enable'); // enable all				
				}, 
				error: handleAjaxError 
			}); 
        }
        else
        {   
            $('#tabs').tabs('option', 'disabled', [1,2,3]); // all but General tab		
            $('#cbActive').prop('checked', true);
			$('#tdColumnNumber').css('visibility','hidden');
			$('#txtColumnNumber').css('visibility','hidden');
			$('#ddlOnSave').prop('hidden',true);
			$('#ddlScheduler').prop('hidden',true);
			$('#tdInteraval').css('visibility','hidden');
			$('#txtInterval').css('visibility','hidden');
        }
	}
     
    function setDisplayParameters(mode)
    {
        var arr = new Array();
        if(mode == 0)
        {
            // selected row ID
            arr[0] = 0;
            // selected row Order
            arr[1] = 0;
        }
        else
        {
            // selected row ID
            arr[0] = $('#currNotificationID').val();
            // selected row Order
            arr[1] = selectedRowOrder;
        }
        // selected active
        arr[2] = $("#ddlActive").val();
        $('#displayParametersArray').val(arr);
    }
								
	function beforeShowTab(event, ui)
	{   
		var newLink = $('a', ui.newTab); 
		
		if( bOnChange )
		{
			openConfirmDialog({message:"Any changes made will be lost. Are you sure you want to leave the tab?", //"OpenConfirmDialog_Msg_Short_Name_T_22",
								onConfirm:function(newLink){
									// reset bOnChange so next click will switch tabs
									bOnChange = false;									
									$(newLink).click();
								},
								onConfirmParams:[newLink]								
							});			
			// cancel operation until user decides
			return false; 			
		}			
		
		getNotificationInfo(); 
	}
	
	function onShowTab(event, ui)
	{
		if( $('a', ui.newTab).attr('id') == 'EditMessage' )
		{
			refreshHtmlArea();
		}		
	}
        
	function refreshHtmlArea()
	{	
		$('#txtMsgBody:visible').htmlarea(
		{
			loaded: function () 
                        { 
				// var elem = this.editor.body;
				$(this.editor.body).on('keypress', function () { 
					bOnChange = true; 
				});	
				// $(this.editor.body).on('blur', function () { // commented because an issue it raised: pasteHTML loses position of cursor on blur
					// $('#txtMsgBody').trigger('blur');
				// });	
			},
			toolbar: [
				["bold", "italic", "underline"],
				["increasefontsize", "decreasefontsize", "|", "forecolor"],
				["html", "link", "unlink", "|", "image"], 
				[{
					// The CSS class used to style the <a> tag of the toolbar button
					css: 'customButton',

					// The text to use as the <a> tags "Alt" attribute value
					text: 'Insert selected field',

					// The callback function to execute when the toolbar button is clicked
					action: function (btn) {
						// 'this' = jHtmlArea object
						// 'btn' = jQuery object that represents the <a> ("anchor") tag for the toolbar button
						
                                                this.pasteHTML($('#txtSelectedField').val()); 
						bOnChange = true;
					}
				 }]
			]
		}); 
		$('#txtMsgBody').htmlarea('updateHtmlArea'); // refresh text within htmlarea object	
	}
    
    function imposeMaxLength(Object, MaxLen)
    {
        return (Object.value.length <= MaxLen);
    }
	
	function cancel()
	{
		var str = $('#displayParametersArray').val();
        var arr = new Array();
        arr = str.split(",");
        arr[0] = $('#currNotificationID').val(); 
        if(isNewNotification) 
        {   // set new Notification row Order            
            arr[1] = newRowOrder;
        }
        $('#displayParametersArray').val(arr);
		
		exitPageWarning({ onConfirm:submitCancel, isChanged:bOnChange });
	}
	
	function submitCancel()
	{
		$('#action_id').val('back');
		//$('#main').attr('action', 'frmnotificationlistservlet');
		$('#main').attr('action', './notification.request');
		$('#main').submit();
	}
	
	function saveGeneral()
	{
		var modID = $('#ddlModuleName').val();
		var msgTypeID = $('#ddlMessageType').val();
		var colNum = $('#txtColumnNumber').val();
		var desc = $.trim($('#txtDescription').val());
		var triggTypeID = $('#ddlTriggerType').val();
		var schedID = $('#ddlScheduler').val();
		var onSaveID = $('#ddlOnSave').val();
		var interval = $('#txtInterval').val();
		var resend = $('#cbResend').is(':checked') ? 1:0;
		var active = $('#cbActive').is(':checked') ? 1:0;
		var copyToProduction = $('#cbCopyToProduction').is(':checked') ? 1:0;
		var incAttach = $('#cbAttachment').is(':checked') ? 1:0;	
		
		if (!validateFields(modID, msgTypeID, colNum, desc, triggTypeID, onSaveID, schedID, interval))
		{
			return;
		}
		  
		showWaitMessage("Saving, please wait...");
                
        bOnChange = false;
		if (interval == '')
		{
			interval = 0;
		}
		if(currNotificationID != 0) 
		{
			var paramsStr = "action_id=updateNotificationGeneralData&currNotificationID=" + currNotificationID + "&moduleID=" + modID +
			   "&msgTypeID=" + msgTypeID + "&colNumber=" + colNum + "&description=" + encodeURIComponent(desc) + "&schedulerID=" + schedID +
			   "&triggerTypeID=" + triggTypeID +"&onSaveID=" + onSaveID + "&interval=" + interval + "&resend=" + resend + "&isActive=" + active + "&copyToProduction=" + copyToProduction +  "&addAttachment=" + incAttach + 
			   "&displayParametersArray=" + $('#displayParametersArray').val(); 
			//var url="notificationmodulemainservlet";
			var url="./notificationModule.request";
			xmlHttp1.open("POST", url, true);
			xmlHttp1.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
			xmlHttp1.onreadystatechange = updateGeneralCompleted;            
			xmlHttp1.send(paramsStr);
		}		
		else
		{
			var paramsStr = "action_id=saveNotificationGeneralData&currNotificationID=" + currNotificationID + "&moduleID=" + modID +
			   "&msgTypeID=" + msgTypeID + "&colNumber=" + colNum + "&description=" + encodeURIComponent(desc) + "&schedulerID=" + schedID +
			   "&triggerTypeID=" + triggTypeID +"&onSaveID=" + onSaveID + "&interval=" + interval + "&resend=" + resend + "&isActive=" + active + "&copyToProduction=" + copyToProduction +  "&addAttachment=" + incAttach +  			   
			   "&displayParametersArray=" + $('#displayParametersArray').val();
			//var url="notificationmodulemainservlet";
			var url="./notificationModule.request"; 
			xmlHttp1.open("POST", url, true);
			xmlHttp1.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
			xmlHttp1.onreadystatechange = saveGeneralCompleted;
			xmlHttp1.send(paramsStr);   
		}
	}
	
	function validateFields (modID, msgTypeID, colNum, desc, triggTypeID, onSaveID, schedID, interval)
	{
		if (!fnValidateString({ inStr:modID, fieldName:"Context", isRequired:true, forbiddenValues:"0", focusOn:"ddlModuleName" }))
                { 
                    return false;
                } 
                        
                        if (!fnValidateString({ inStr:msgTypeID, fieldName:"Notification Type", isRequired:true, forbiddenValues:"0", focusOn:"ddlMessageType" }))
                { 
                    return false;
                }
                        
                if (msgTypeID == 2 && !fnValidatePositiveNumber(colNum, "Column Number", true, "txtColumnNumber")) // mandatory only for Group message
                {   
                    return false;
                }
                        
                        if (!fnValidateString({ inStr:desc, fieldName:"Description", isRequired:true, focusOn:"txtDescription" }))
                {
                    return false;
                }
                        if (!fnValidateString({ inStr:triggTypeID, fieldName:"Trigger Type", isRequired:true, forbiddenValues:"0", focusOn:"ddlTriggerType" }))
                {
                    return false;
                }
                if ($('#ddlTriggerType').val() == '1') //Sheduler
                {
	                if (!fnValidateString({ inStr:schedID, fieldName:"Scheduler", isRequired:true, forbiddenValues:"0", focusOn:"ddlScheduler" }))
	                {
	                    return false;
	                }
	                if (!fnValidatePositiveNumber(interval, "Interval", true, "txtInterval"))
	                {
	                    return false;
	                }
                }
                if ($('#ddlTriggerType').val() == '2') //On Save
                {
	                if (!fnValidateString({ inStr:onSaveID, fieldName:"On Save", isRequired:true, forbiddenValues:"0", focusOn:"ddlOnSave" }))
	                {
	                    return false;
	                }
                }
                
		return true;
	}
	
	function saveGeneralCompleted()
    {
        if (xmlHttp1.readyState==4 || xmlHttp1.readyState=="complete")
        {
            hideWaitMessage();
            var retVal = xmlHttp1.responseText; 
            if(retVal.match(/TIME_IS_OUT/))
            {
                top.location.href = 'Login.jsp';
            }
            else
            {
                if(retVal == "-1")
                {                    
                    displayAlertDialog("Save failed", { title:"Error" }); // "DispAlDial_Msg_Short_Name_T_95" / "TitleInMess_Msg_Short_Name_T_2"           
                }                  
                else
                {
                    $('#currNotificationID').val(retVal);                                        
                    getNotificationInfo(); 
					loadAvailableFieldsDDL();
					$('#action_id').val('edit');					
                }
            }
        }
    }
	
	function updateGeneralCompleted()
    {
        if (xmlHttp1.readyState==4 || xmlHttp1.readyState=="complete")
        {
            hideWaitMessage();
            var retVal = xmlHttp1.responseText;
            if(retVal.match(/TIME_IS_OUT/))
            {
                top.location.href = 'Login.jsp';
            }
            else
            {
                if(retVal == "-1")
                {                    
                    displayAlertDialog("Update failed", { title:"Error" }); // DispAlDial_Msg_Short_Name_T_176 / TitleInMess_Msg_Short_Name_T_2                    
                }                  
                else
                {        
                    getNotificationInfo(); 
					loadAvailableFieldsDDL();
					$('#action_id').val('edit');					
                }
            }
        }
    }
	
	function onChangeMessageType()
	{
		$('#ddlSendType').html(''); 
		$('#ddlSendType').append('<option value="TO">TO</>'); 
		
		if($('#ddlMessageType').val() == '2') // group
		{
			$('#tdColumnNumber').css('visibility','visible');
			$('#txtColumnNumber').css('visibility','visible');
		}
		else
		{
			$('#tdColumnNumber').css('visibility','hidden');
			$('#txtColumnNumber').css('visibility','hidden'); 
			$('#txtColumnNumber').val('');	
			$('#ddlSendType').append('<option value="CC">CC</>'); 
		}
	}
	
	function onChangeScheduler()
	{
		if ($('#ddlScheduler').prop('selectedIndex') > 0)
		{
			$('#lblIntervalType').html($('#ddlScheduler option:selected').text());
		}
		else
		{
			$('#lblIntervalType').html('');
		}
	}
	
	function onChangeTriggerType()
	{
		if ($('#ddlTriggerType').val() == '0') // Choose
		{
			$('#lblSchedulerOnSave').css('visibility','hidden');
			$('#mandatoryMarkSchedulerOnSave').css('visibility','hidden');
			
			$('#ddlOnSave').prop('hidden',true);
			
			$('#ddlScheduler').prop('hidden',true);
			$('#tdInteraval').css('visibility','hidden');
			$('#txtInterval').css('visibility','hidden');
			
			$('#ddlScheduler').val(0);
			$('#txtInterval').val('');
			$('#ddlOnSave').val(0);
		}
		else if ($('#ddlTriggerType').val() == '1') // Scheduler
		{
			$('#lblSchedulerOnSave').css('visibility','visible');
			$('#mandatoryMarkSchedulerOnSave').css('visibility','visible');
			$('#lblSchedulerOnSave').text('Scheduler');
			$('#ddlOnSave').prop('hidden',true);
			$('#ddlOnSave').val(0);
			$('#ddlScheduler').prop('hidden',false);
			$('#tdInteraval').css('visibility','visible');
			$('#txtInterval').css('visibility','visible');
		}
		else if ($('#ddlTriggerType').val() == '2') // On Save
		{
			$('#lblSchedulerOnSave').css('visibility','visible');
			$('#mandatoryMarkSchedulerOnSave').css('visibility','visible');
			$('#lblSchedulerOnSave').text('On Save');
			$('#ddlOnSave').prop('hidden',false);
			$('#ddlScheduler').prop('hidden',true);
			$('#tdInteraval').css('visibility','hidden');
			$('#txtInterval').css('visibility','hidden');
			$('#ddlScheduler').val(0);
			$('#txtInterval').val('');
		}
		onChangeScheduler();
	}
	
	function onChangeOnSave()
	{
		/* if ($('#ddlOnSave').prop('selectedIndex') > 0)
		{
			$('#lblIntervalType').html($('#ddlScheduler option:selected').text());
		}
		else
		{
			$('#lblIntervalType').html(''); 
		}*/
	}
	
	function selectField()
	{
		var selectedField = '@@' + $('#ddlAvailFields option:selected').val() + '@@';
		$('#txtSelectedField').val(selectedField);
		$('.customButton').css('visibility','visible');
		$('#btnPasteField').css('visibility','visible');
	}
	
	function saveMessage()
	{
		var subject = $.trim($('#txtMsgSubject').val());
		var msgBody = $.trim($('#txtMsgBody').val());
		//var tagRegex = /(<([^>]+)>)/ig; 
		var htmlMsgBody = fnTrimString(msgBody.toLowerCase().replace(/(<([^>]+)>)/ig,'').replace(/\s/g,'').replace(/^(&nbsp;)+/,'')); // yp 11042016 fix bug 3426 - remove all tags content / space / &nbsp
		
		if (!fnValidateString({ inStr:subject, fieldName:"Subject", isRequired:true, legals:'@', focusOn:"txtMsgSubject" }))
        { 
            return;
        } 
		 
		//alert(msgBody);
		//alert(htmlMsgBody);
		if ( msgBody == "" || htmlMsgBody == "" )
        {      
			displayAlertDialog("Message Body is a required field", { title:"Required Field Missing" }); //"DispAlDial_Msg_Short_Name_T_177" / TitleInMess_Msg_Short_Name_T_8
            txtMsgBody.focus();
            return;
        }
		
		showWaitMessage("Saving, please wait...");

        bOnChange = false;
		
		var paramsStr = "action_id=updateNotificationMessage&currNotificationID=" + currNotificationID + "&subject=" + 
				encodeURIComponent(subject) + "&msgBody=" + encodeURIComponent(msgBody); 
		//var url="notificationmodulemainservlet";
		var url="./notificationModule.request";
		xmlHttp1.open("POST", url, true);
		xmlHttp1.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		xmlHttp1.onreadystatechange = updateGeneralCompleted; // uses same function as saveGeneral   
		xmlHttp1.send(paramsStr);				
	}
	
	function loadCriteriaTable()
	{
		resetTable("tblCriteria");
                oTable = $("#tblCriteria").dataTable({
			"bServerSide": true,
			"bProcessing": false,
			//"sAjaxSource": "notificationmodulemainservlet?action_id=getCriteriaTable&currNotificationID=" + currNotificationID,
			"sAjaxSource": "./notificationModule.request?action_id=getCriteriaTable&currNotificationID=" + currNotificationID,
			"sPaginationType": "full_numbers",
			"iDisplayLength": 8,
			"iDisplayStart": 0, 
			"bJQueryUI": true,
			"bDestroy": true,
			"sDom": "Trtip",
			"aoColumnDefs": [
							  { "aTargets":[0],             //CONDITION ID
								"bSearchable": false,
								"bSortable": false,
								"bVisible": false
							  }, 
							  { "sTitle":"And/Or",
								"aTargets":[1],
								"bSortable": false
							  }, 
							  { "sTitle":"Field",
								"aTargets":[2],
								"bSortable": false
							  }, 
							  {	"aTargets":[3],             //FIELD ID
								"bSearchable": false,
								"bSortable": false,
								"bVisible": false								  
							  },
							  { "sTitle":"Condition",
								"aTargets":[4],
								"bSortable": false
							  },
							  {	"aTargets":[5],             //OPERATOR ID
								"bSearchable": false,
								"bSortable": false,
								"bVisible": false								  
							  },
							  { "sTitle":"Value",
								"aTargets":[6],
								"bSortable": false
							  }
							],
			 "fnCreatedRow": function( nRow, aData, iDataIndex )
			  {     
					  if ( aData[0] == currConditionID ) 
					  {     
							$("td:eq(0)", nRow).addClass("row_light");
							$("td:eq(1)", nRow).addClass("row_light");
							$("td:eq(2)", nRow).addClass("row_light");
							$("td:eq(3)", nRow).addClass("row_light");								
							
							rowSelected = 1;
							$('#selConditionID').val(aData[0]);                                   
					  } 
			  },
			 "fnDrawCallback": function( oSettings ) 
			 {	
				 totalRecords = oSettings.fnRecordsDisplay(); 
				 if(rowSelected == 1)
				 {
					$("#btnUpdateCondition").button('option', 'disabled', false);
					$("#btnDeleteCondition").button('option', 'disabled', false);                        
					rowSelected = 0;
				 }
				 else
				 {
					$("#btnUpdateCondition").button('option', 'disabled', true);
					$("#btnDeleteCondition").button('option', 'disabled', true);
				 }
			 },
			"fnServerData": function ( sSource, aoData, fnCallback ) 
			 {    
					$.ajax
					({         
						"dataType": "json",         
						"type": "POST",         
						"url": sSource,         
						"data": aoData,         
						"success": fnCallback,         
						"error": handleAjaxError       
					  } ); 
			  }                              
		});     
	}
	
	function addCondition()
	{		
		$('#ddlConditionField').val(0).change(); // reset field and trigger 'onchange' to affect the other fields
		bOnChange = false; // was 'true' because of trigger above
		$('#fsConditionProperties').css('visibility','visible');
		currConditionID = 0;
	}
	
	function saveCondition()
	{
		var type = $('#ddlConditionType').val();
		var field = $('#ddlConditionField').val();
		var oper = $('#ddlOperator').val();
		var isValExpected = ($('#ddlOperator option:selected').attr('valexpected') == 1) ? true:false;
		var value = $.trim($('#txtConditionValue').val());
		var dataType = $('#ddlConditionField option:selected').attr('datatype');
		
		if (!validateCondition(field, oper, isValExpected, value, dataType))
		{
			return;
		}
		
		showWaitMessage("Saving, please wait...");
        bOnChange = false;
		
		if(currConditionID != 0)
        {
            var param = $('#displayParametersArray').val();
            $.ajax 
			({
				 type: "POST",
				 contentType: "application/json; charset=utf-8",
				 //url: "notificationmodulemainservlet?action_id=updateNotificationCondition&currNotificationID=" + currNotificationID +
				 url: "./notificationModule.request?action_id=updateNotificationCondition&currNotificationID=" + currNotificationID +  
						"&conditionID=" + currConditionID + "&type=" + type + "&fieldID=" + field + "&operatorID=" + oper + 
						"&condValue=" + encodeURIComponent(value) + "&pageParameters=" + param,
				 dataType: "text",
				 success: function(data, textStatus, retVal)
				 { 
					hideWaitMessage();					
					if(retVal.responseText == "-1")
					{                    
						displayAlertDialog("Update failed", { title:"Error" }); // DispAlDial_Msg_Short_Name_T_176 / TitleInMess_Msg_Short_Name_T_2
					}                  
					else
					{        
						$('#fsConditionProperties').css('visibility','hidden');
						currConditionID = 0; // remove selected condition
						loadCriteriaTable();						
					}					
				}, 
				error: handleAjaxError
			});                    
        }
        else
        {   
            var param = $('#displayParametersArray').val();
            $.ajax 
			({
				 type: "POST",
				 contentType: "application/json; charset=utf-8",
				 //url: "notificationmodulemainservlet?action_id=addNewCondition&currNotificationID=" + currNotificationID +
				 url: "./notificationModule.request?action_id=addNewCondition&currNotificationID=" + currNotificationID +  
						"&type=" + type + "&fieldID=" + field + "&operatorID=" + oper + "&condValue=" + encodeURIComponent(value) + "&pageParameters=" + param,						
				 dataType: "text",				 
				 success: function(data, textStatus, retVal)
				 { 					
					hideWaitMessage();					
					if(retVal.responseText == "-1")
					{                    
						displayAlertDialog("Save failed", { title:"Error" }); // "DispAlDial_Msg_Short_Name_T_95" / "TitleInMess_Msg_Short_Name_T_2"
					}                  
					else
					{      
						$('#fsConditionProperties').css('visibility','hidden');
						loadCriteriaTable();					
					}															
				}, 
				error: handleAjaxError
			});            
        }
	}
	
	function validateCondition(field, oper, isValExpected, value, dataType)
	{
		if (!fnValidateString ({ inStr:field, fieldName:"Field", isRequired:true, forbiddenValues:"0", focusOn:"ddlConditionField" }))
		{
			return false;
		}
		
		if (!fnValidateString ({ inStr:oper, fieldName:"Operator", isRequired:true, forbiddenValues:"0", focusOn:"ddlOperator" }))
		{
			return false;
		}
		
		if (isValExpected)
		{ // if value is expected, validate it according to data-type
			if (dataType == 'N')
			{
				if (!fnValidateNumeric (value, "Value", true, true, true, "txtConditionValue"))
				{
					return false;
				}
			}
			else if (dataType == 'D')
			{
				if (value.length <= 7) // less than "Today +X"
				{
					displayAlertDialog("Value field should be formatted as 'Today +/-[number of days]'"); //"DispAlDial_Msg_Short_Name_V_48"
					txtConditionValue.focus();
					return false;
				}
			}
			else if (dataType == 'T')
			{
				if (!fnValidateString ({ inStr:value, fieldName:"Value", isRequired:true, focusOn:"txtConditionValue", legals:"@,;=" }))
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	function onChangeConditionField(oSelect)
	{
		var fieldID = $(oSelect).val();	
		// redraw operator list according to field's data-type
		if (fieldID != 0) 
        {            
            $.ajax 
			({
				type: "POST",
				contentType: "application/json; charset=utf-8",
				//url: "notificationmodulemainservlet?action_id=getOperatorList&fieldID=" + fieldID,
				url: "./notificationModule.request?action_id=getOperatorList&fieldID=" + fieldID,
				dataType: "json",
				success: function(data)
				{ 	
					$('#ddlOperator').html(''); 
					$('#ddlOperator').append('<option value="0">Choose</>'); 
					$(data).map(function () 
					{
						$("#ddlOperator").append($('<option>').val(this.id).text(this.text).attr('valexpected', this.attributes[0]));
					})
					
					if (currConditionID != 0)
					{
						$("#ddlOperator").val($('#selOperatorID').val()).change();
					}
				}
			});     
						
			// arrange value field according to data-type
			$('#txtConditionValue').removeAttr('disabled').val(''); // enable value field
			$('#txtConditionValue').off('keydown').off('keypress').off('paste'); // remove previous handlers
			var dataType = $('option:selected', oSelect).attr('datatype'); 
			if (dataType == null || dataType == "")
			{
				dataType = 'N'; // number as default (also the default in DB)
			}
			
			if (dataType == 'N')
			{
				$('#txtConditionValue').keypress(function(event){
					return validateDecimal($(this).val(), event.which, event);
				});
				
				$('#txtConditionValue').on('paste', function(event){ 
					var element = this; 
					var oldText = $(this).val(); 
					// validate pasted value; if illegal previous-to-paste value is kept
					setTimeout(function () { // the setTimeout is needed because it takes time for the paste to proceed
						var text = $(element).val(); 
						if (!fnValidateNumeric (text, "Value", true, true, false))
						{
							$(element).val(oldText);
						}
					  }, 0);							
				});
			}
			else if (dataType == 'D')
			{
				$('#txtConditionValue').val('Today +');
				$('#txtConditionValue').keydown(function(event){						
					var padding = (event.which == 8) ? 2:1; // on Backspace length will change only AFTER return, so count it in advance
					if ($(this).val().length < ('Today').length + padding)
					{
						$('#txtConditionValue').val('Today +');
					}
				});
				
				$('#txtConditionValue').keypress(function(event){
					var len = $(this).val().length; 
					var	minLen = ('Today ').length;						
					return (len > minLen && validateNumber(event.which, event)) // any digit/s after the +/-
							|| (len == minLen && (event.which == 43 || event.which == 45)); // only +/- at first index after 'Today '
				});
				
				$('#txtConditionValue').on('paste', function(event){ 
					var element = this; 
					var oldText = $(this).val(); 
					// validate pasted value; if illegal previous-to-paste value is kept
					setTimeout(function () { // the setTimeout is needed because it takes time for the paste to proceed
						var text = $(element).val(); 
						if (!(text.indexOf('Today ') == 0
								&& (text.indexOf('+') == 6 || text.indexOf('-') == 6)
								&& (text.length > 7 && fnValidateNumeric(text.substr(7), 'Value', false, false, false))
							))								
						{
							$(element).val(oldText);
						}
					  }, 0);							
				});
			}
			else // 'T'
			{
				$('#txtConditionValue').keypress( function(event){
					//return validateLegalChar(event.which, event);
					return validateLegalCharSelectiveObj({event:event, legals:[44,59,61,64]}); // kd 30112017 fixed bug 4747.
				});
				
				$('#txtConditionValue').on('paste', function(event){ 
					var element = this; 
					var oldText = $(this).val(); 
					// validate pasted value; if illegal previous-to-paste value is kept
					setTimeout(function () { // the setTimeout is needed because it takes time for the paste to proceed
						var text = $(element).val(); 
						if (!fnValidateString({ inStr:text, fieldName:"Value" }))
						{
							$(element).val(oldText);
						}
					  }, 0);							
				});
			}			
        } 
		else // "Choose"
		{
			$('#ddlOperator').html(''); //css('width','1px'); // workaround IE bug where select shrinks if size not in px
			$('#ddlOperator').append('<option value="0">Choose</>'); 			
			$('#txtConditionValue').val('').attr('disabled','disabled');
		}
	}
	
	function onChangeOperator(oSelect)
	{// check if value field is needed according to operator	
	 // isEditCondition == true when Edit was clicked, and thus value from table should be displayed
		if ($('option:selected', oSelect).attr('valexpected') == 0) // value is not expected as comparator
		{
			$('#txtConditionValue').val('').attr('disabled','disabled');
			$('#mandatoryValue').css('visibility', 'hidden');
		}
		else
		{
			$('#txtConditionValue').removeAttr('disabled');
			$('#mandatoryValue').css('visibility', 'visible');
			if ($('#ddlConditionField option:selected').attr('datatype') == 'D' && !isEditCondition)
			{ 
				$('#txtConditionValue').val('Today +');
			}
		}
		isEditCondition = false; // so updating operator will update value if 'date'
	}
	
	function cancelCondition()
	{	
		bOnChange = false;
		$('#fsConditionProperties').css('visibility','hidden');		
	}
	
	function updateCondition()
	{
		isEditCondition = true;
		$('#fsConditionProperties').css('visibility','visible');
		currConditionID = $('#selConditionID').val();
		var tempbOnChange = bOnChange;
		// set field and trigger 'onchange', it updates and selects ddlOperator accordingly, and thus also the Value validation
		$('#ddlConditionField').val($('#selFieldID').val()).change(); // isEditCondition prevents defaulting date value to 'Today +'
		$('#txtConditionValue').val($('#selConditionValue').val()); 
		$('#ddlConditionType').val($('#selConditionType').val());
		bOnChange = tempbOnChange; // restore bOnChange to what it was before triggering auto-onchange			
	}
	
	function deleteCondition()
	{
		openConfirmDialog({ message:"Are you sure you want to delete this item?", // "OpenConfirmDialog_Msg_Short_Name_T_12"
							onConfirm: function() {
								openCommentDialogAndCommit({ maxLength:200, includeComment:true, okHandler:submitDeleteCondition });
							}
						});
	}
	
	function submitDeleteCondition(comment)
	{		
		showWaitMessage("Deleting, please wait...");
        bOnChange = false;
		
		$.ajax 
		({
			 type: "POST",
			 contentType: "application/json; charset=utf-8",
			 //url: "notificationmodulemainservlet?action_id=deleteNotificationCondition&currNotificationID=" + currNotificationID + 
			 url: "./notificationModule.request?action_id=deleteNotificationCondition&currNotificationID=" + currNotificationID +
					"&conditionID=" + currConditionID + "&comment=" + comment,
			 dataType: "text",
			 success: function(data, textStatus, retVal)
			 { 
				hideWaitMessage();					
				if(retVal.responseText == "-1")
				{                    
					displayAlertDialog("Delete failed", { title:"Error" }); //"DispAlDial_Msg_Short_Name_T_102" / "TitleInMess_Msg_Short_Name_T_2"
				}                  
				else
				{   
					$('#fsConditionProperties').css('visibility','hidden'); // in case it was on 'edit' before				
					loadCriteriaTable();						
				}					
			}, 
			error: handleAjaxError
		});    	
	}
	
	function loadDistributionTable()
	{
		resetTable("tblDistribution");
                oTable = $("#tblDistribution").dataTable({
			"bServerSide": true,
			"bProcessing": false,
			//"sAjaxSource": "notificationmodulemainservlet?action_id=getDistributionTable&currNotificationID=" + currNotificationID,
			"sAjaxSource": "./notificationModule.request?action_id=getDistributionTable&currNotificationID=" + currNotificationID,
			"sPaginationType": "full_numbers",
			"iDisplayLength": 8,
			"iDisplayStart": 0, 
			"bJQueryUI": true,
			"bDestroy": true,
			"sDom": "Trtip",
			"aoColumnDefs": [
							  { "aTargets":[0],             //ADDRESSEE ID
								"bSearchable": false,
								"bSortable": false,
								"bVisible": false
							  }, 
							  { "sTitle":"Type of Recipient",
								"aTargets":[1]
							  }, 
							  { "aTargets":[2],             //ADDRESS TYPE ID
								"bSearchable": false,
								"bSortable": false,
								"bVisible": false
							  }, 
							  { "sTitle":"Recipient",			// USER NAME
								"aTargets":[3]
							  }, 
							  { "aTargets":[4],             //USER ID/GROUP ID
								"bSearchable": false,
								"bSortable": false,
								"bVisible": false
							  }, 								  
							  { "sTitle":"Send",				// SEND TYPE
								"aTargets":[5]
							  }
							],
			"fnCreatedRow": function( nRow, aData, iDataIndex )
			{     
				if ( aData[0] == currAddresseeID ) 
				{     
					$("td:eq(0)", nRow).addClass("row_light");
					$("td:eq(1)", nRow).addClass("row_light");
					$("td:eq(2)", nRow).addClass("row_light");
					$("td:eq(3)", nRow).addClass("row_light");								
					
					rowSelected = 1;
					$('#selAddresseeID').val(aData[0]);                                   
				} 
			},
			"fnDrawCallback": function( oSettings ) 
			{	
				totalRecords = oSettings.fnRecordsDisplay(); 
				if(rowSelected == 1)
				{
					$("#btnUpdateAddressee").button('option', 'disabled', false);
					$("#btnDeleteAddressee").button('option', 'disabled', false);                        
					rowSelected = 0;
				}
				else
				{
					$("#btnUpdateAddressee").button('option', 'disabled', true);
					$("#btnDeleteAddressee").button('option', 'disabled', true);
				}
			},
			"fnServerData": function ( sSource, aoData, fnCallback ) 
			{    
				$.ajax
				({         
					"dataType": "json",         
					"type": "POST",         
					"url": sSource,         
					"data": aoData,         
					"success": fnCallback,         
					"error": handleAjaxError       
				}); 
			}                              
		});     
	}
	
	function addAddressee()
	{	
		$('#ddlSendType').val('TO'); // default
		$('input[name=rdAddressType]:nth(0)').prop('checked', true); // set default radio
		loadAddresseeList($('input[name=rdAddressType]:nth(0)').val()); // set list for default type
		$('#ddlAddressee').val(0);
		$('#txtExternalEmail').val(""); // reset field to affect the other fields
		$('#fsDistributionProperties').css('visibility','visible');
		currAddresseeID = 0;
	}
	
	function updateAddressee()
	{
		$('#fsDistributionProperties').css('visibility','visible');
		currAddresseeID = $('#selAddresseeID').val();
		var tempbOnChange = bOnChange;
		// set fields according to chosen row
		$('#ddlSendType').val($('#selSendType').val());
		$('#txtExternalEmail').val($('#selEmailExternal').val());
		// check relevant radio button and trigger "onchange" to refresh ddlAddressee and selects value if on edit
		$('input[name=rdAddressType][value=' + ($('#selAddressTypeID').val()) + ']').prop('checked', true).change(); 			
		bOnChange = tempbOnChange; // restore bOnChange to what it was before triggering auto-onchange	
	}
	
	function cancelAddressee()
	{		
		bOnChange = false;
		$('#fsDistributionProperties').css('visibility','hidden');		
	}
	
	function loadAddresseeList(typeID)
	{
		var userID = $('#selUserID').val();
		$.ajax 
		({
			type: "POST",
			contentType: "application/json; charset=utf-8",
			//url: "notificationmodulemainservlet?action_id=getAddresseeList&notificationID=" + currNotificationID + "&addressTypeID=" + typeID,
			url: "./notificationModule.request?action_id=getAddresseeList&notificationID=" + currNotificationID + "&addressTypeID=" + typeID,
			dataType: "json",
			success: function(data)
			{ 	
				$('#ddlAddressee').html(''); 
				$('#ddlAddressee').append('<option value="0">Choose</>'); 
				$(data).map(function () 
				{
					$("#ddlAddressee").append($('<option>').val(this.id).text(this.text));
				});
				
				if (currAddresseeID != 0)
				{
					$('#ddlAddressee').val($('#selUserID').val());
					if( $('#ddlAddressee option:selected').length == 0 )
					{ // addressee wasn't found. happens on user change of type (employee/group)
						$('#ddlAddressee').val(0);
					}
				}
			}
		});   
		// kd 22082017
				if ( $('input[name=rdAddressType]:checked').attr('typename') == 'External')
				{
						$('#txtExternalEmail').prop('hidden',false);
						$('#txtExternalEmail').prop('disabled',false);
						$('#ddlAddressee').prop('hidden',true);
				} else
				{
					$('#txtExternalEmail').prop('hidden',true);
					$('#txtExternalEmail').prop('disabled',true);
					$('#txtExternalEmail').val("");
						$('#ddlAddressee').prop('hidden',false);
				}
		
		$('#lblAddressee').text($('input[name=rdAddressType]:checked').attr('typename')); 		
	}
	
	function deleteAddressee()
	{
		openConfirmDialog({ message:"Are you sure you want to delete this item?", //"OpenConfirmDialog_Msg_Short_Name_T_12"
							onConfirm: function() {
								openCommentDialogAndCommit({ maxLength:200, includeComment:true, okHandler:submitDeleteAddressee });
							}
						});	
	}
	
	function submitDeleteAddressee(comment)
	{
		showWaitMessage("Deleting, please wait...");
        bOnChange = false;
		
		$.ajax 
		({
			type: "POST",
			contentType: "application/json; charset=utf-8",
			//url: "notificationmodulemainservlet?action_id=deleteNotificationAddressee&addresseeID=" + currAddresseeID + "&comment=" + comment,
			url: "./notificationModule.request?action_id=deleteNotificationAddressee&addresseeID=" + currAddresseeID + "&comment=" + comment,
			dataType: "text",
			success: function(data, textStatus, retVal)
			{ 
				hideWaitMessage();					
				if(retVal.responseText == "-1")
				{                    
					displayAlertDialog("Delete failed", { title:"Error" }); //"DispAlDial_Msg_Short_Name_T_102" / "TitleInMess_Msg_Short_Name_T_2"            
				}                  
				else
				{   
					$('#fsDistributionProperties').css('visibility','hidden'); // in case it was on 'edit' before				
					loadDistributionTable();						
				}					
			}, 
			error: handleAjaxError
		});    	
	}
	
	function saveAddressee()
	{
		var sendType = $('#ddlSendType option:selected').val();
		var addrTypeID = $('input[name=rdAddressType]:checked').val();
		var addrType = $('#lblAddressee').html();
		var userID = $('#ddlAddressee option:selected').val();
		var notifAddrExternal = $('#txtExternalEmail').val();
		var messageOnly = $('#cbMessageOnly').is(':checked') ? 1:0;
		if (addrType == 'External')
		{
		if(!fnValidateString({ inStr:notifAddrExternal, fieldName:addrType, isRequired:true, forbiddenValues:"0", focusOn:"ddlAddressee", legals:'@' }))
			{
				return;
			}
		} 
		else
		{
			if(!fnValidateString({ inStr:userID, fieldName:addrType, isRequired:true, forbiddenValues:"0", focusOn:"ddlAddressee" }))
			{
				return;
			}
		}
		
		showWaitMessage("Saving, please wait...");
		bOnChange = false;
		
		if (currAddresseeID != 0)
		{			
            $.ajax 
			({
				type: "POST",
				contentType: "application/json; charset=utf-8",
				//url: "notificationmodulemainservlet?action_id=updateNotificationAddressee&currAddresseeID=" + currAddresseeID + 
				url: "./notificationModule.request?action_id=updateNotificationAddressee&currAddresseeID=" + currAddresseeID +
					"&sendType=" + sendType + "&typeID=" + addrTypeID +  "&addrUserID=" + userID + "&notifAddrExternal=" + notifAddrExternal + "&messageOnly=" + messageOnly,
				dataType: "text",
				success: function(data, textStatus, retVal)
				{ 
					hideWaitMessage();				
					if(retVal.responseText == "-1")
					{                    
						displayAlertDialog("Update failed", { title:"Error" }); // "DispAlDial_Msg_Short_Name_T_176" / "TitleInMess_Msg_Short_Name_T_2"
					}                  
					else
					{        
						$('#fsDistributionProperties').css('visibility','hidden');
						currAddresseeID = 0; // remove selected condition
						loadDistributionTable();						
					}					
				}, 
				error: handleAjaxError
			});                    
        }
        else // new addressee
        {               
            $.ajax 
			({
				type: "POST",
				contentType: "application/json; charset=utf-8",
				//url: "notificationmodulemainservlet?action_id=addNewAddressee&currNotificationID=" + currNotificationID + 
				url: "./notificationModule.request?action_id=addNewAddressee&currNotificationID=" + currNotificationID +
					"&sendType=" + sendType + "&typeID=" + addrTypeID +  "&addrUserID=" + userID + "&notifAddrExternal=" + notifAddrExternal + "&messageOnly=" + messageOnly,
				dataType: "text",				 
				success: function(data, textStatus, retVal)
				{ 					
					hideWaitMessage();				
					if(retVal.responseText == "-1")
					{                    
						displayAlertDialog("Save failed", { title:"Error" }); // "DispAlDial_Msg_Short_Name_T_95" / "TitleInMess_Msg_Short_Name_T_2"
					}                  
					else
					{      
						$('#fsDistributionProperties').css('visibility','hidden');
						loadDistributionTable();					
					}															
				}, 
				error: handleAjaxError
			});            
        }		
	}
	
	function loadAvailableFieldsDDL()
	{
		$.ajax 
		({
			type: "POST",
			contentType: "application/json; charset=utf-8",
			//url: "notificationmodulemainservlet?action_id=getAvailableFieldsDDL&currNotificationID=" + currNotificationID, 
			url: "./notificationModule.request?action_id=getAvailableFieldsDDL&currNotificationID=" + currNotificationID,
			dataType: "json",				 
			success: function(data)
			{ 
				$('#ddlAvailFields').html('');
				$('#ddlConditionField').html('');
				$('#ddlConditionField').append('<option value="0">Choose</>'); 
				
				$(data).map(function () 
				{
					$("#ddlAvailFields").append($('<option>').val(this.FIELD_NAME).text(this.DISPLAY_NAME));
					$("#ddlConditionField").append($('<option>').val(this.FIELD_ID).text(this.DISPLAY_NAME).attr('datatype', this.DATA_TYPE));		
				});	
			}, 
			error: handleAjaxError
		});   		
	}
	
	function notifyUser(oInput)
	{
		if (currNotificationID != 0 && $(oInput).val() !== '' && !columnNumberAlertFlag)
		{
                        columnNumberAlertFlag = true;
			displayAlertDialog("Note that if you update the number of columns message body will reset and changes that were typed in will be lost"); //"DispAlDial_Msg_Short_Name_T_178"
		}
	}
        
        function resetTable(tableId)
	{
		var table = $('#' + tableId).dataTable();
		if(table != null)
		{
			table.fnDestroy();			
		}
		$('#' + tableId + ' tbody').empty();
	}
        
	</script>
</head>
<body>
<%
   String pageHeader = "<label lang_key='Notifications' ></label>";
   String pageTitle = "<SPAN class='cssActivePath' lang_key='Notification_Management'></SPAN>";
   String pageIcon = "../skylineFormWebapp/images/IconProcManageS.gif";

   String navigation = "";
   request.setAttribute("NAVIGATION", navigation);
%>	

	<table style="width:99%;" align="center" class="containment-wrapper">
		<tr>
			<td> 
				<table style="width:100%;">
					<tr>
						<td>   
							<%@ include file="./PageHeaderNewForm.inc"%>
						</td>
					</tr>
					<tr><td><br/></td></tr>            
					<tr>
						<td>
							<div id="tabs">
								<ul style="text-transform: uppercase;">
									<li><a href="#divGeneral" id="General" lang_key="General"><spring:message code="General" text="General" /></a></li> 
									<li><a href="#divEditMessage" id="EditMessage" lang_key="Notification_Contents"><spring:message code="NotificationContents" text="Notification Contents" /></a></li> 
									<li><a href="#divMessageCriteria" id="MessageCriteria" lang_key="Notification_Criteria"><spring:message code="NotificationCriteria" text="Notification Criteria" /></a></li> 
									<li><a href="#divDistList" id="DistList" lang_key="Distribution_List"><spring:message code="DistributionList" text="Distribution List" /></a></li> 
								</ul> 
								<div id="divGeneral" style="height:550px;">
									<table style="width:100%;">
										<tr>
											<td class="ui-widget-content ui-corner-all">
												<table style="width:98%; height:530px; margin:3px 3px 6px 6px; padding:2px;">
													<tr>                
														<td style="text-align:left; padding-left:15px;" >
															<h2 class="InitiationTitle" style="text-transform: uppercase;" lang_key="General"><spring:message code="General" text="" /><spring:message code="General" text="General" /></h2>
														</td>
													</tr> 
													<tr valign="top">
														<td>
															<table id="tblGeneral" width="65%" align="left" style="table-layout:fixed; border-collapse: separate; border-spacing:4px; *border-collapse: expression('separate', cellSpacing = '4px');">
																<tr>
																	<td class="cssStaticData" nowrap>
																		<span class="mandatoryFieldMark">*</span>
																		<label lang_key="Context"><spring:message code="Context" text="Context" /></label>															
																	</td>
																	<td class="cssTextData" height="30px">
																		<select id="ddlModuleName" name="ddlModuleName" onchange="setParametrOfChange();" style="width:90%;"> 
																			<option value="0">Choose</option>
																		</select>
																	</td>
																	<td colspan=2></td>
																</tr>
																<tr>
																	<td class="cssStaticData" nowrap>
																		<span class="mandatoryFieldMark">*</span>
																		<label lang_key="Notification_Type"><spring:message code="NotificationType" text="Notification Type" /></label>															
																	</td>
																	<td class="cssTextData" height="30px">
																		<select id="ddlMessageType" name="ddlMessageType"  onchange="onChangeMessageType();setParametrOfChange();" style="width:90%;"> 
																			<option value="0">Choose</option>
																		</select>
																	</td>
																	<td class="cssStaticData" id="tdColumnNumber" nowrap> 
																		<span class="mandatoryFieldMark">*</span>
																		<label lang_key="Column_Number"><spring:message code="ColumnNumber" text="Column Number" /></label>
																	</td>
																	<td>
																		<input type="text" onchange="setParametrOfChange();" name="txtColumnNumber" id="txtColumnNumber" size="5" maxlength="3" 
																		onblur="focusInput(event)" onfocus="notifyUser(this)" 
																		onkeypress="validatePositiveNumber(event.keyCode, this.value, event); setParametrOfChange();">
																	</td>
																</tr>
																<tr><td><br/></td></tr>
																<tr>
																	<td rowspan="5" class="cssStaticData" nowrap valign="Top">
																		<span class="mandatoryFieldMark">*</span>
																		<label lang_key="Description"><spring:message code="Description" text="Description" /></label>															
																	</td>
																	<td rowspan="5" colspan="3" valign="Top">
																		<textarea rows="5" cols="70" class="InputStyleWithoutHeight" id="txtDescription" name="txtDescription" onchange="setParametrOfChange();"
																			 onblur="focusInput(event)" onkeypress="return imposeMaxLength(this,500);" onfocus="focusInput(event)" 
																			 style="resize: none;"></textarea>
																	</td>																
																</tr>
																<tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><!-- otherwise next rows will be added along the textarea -->
																
																<tr>
																	<td class="cssStaticData" nowrap>
																		<span class="mandatoryFieldMark">*</span>
																		<label lang_key="Trigger_Type"><spring:message code="TriggerType" text="Trigger Type" /></label>															
																	</td>
																	<td class="cssTextData" height="30px">
																		<select id="ddlTriggerType" name="ddlTriggerType" onchange="onChangeTriggerType();setParametrOfChange();" style="width:90%;"> 
																			<option value="0">Choose</option>
																		</select>
																	</td>
																		<td class="cssStaticData" nowrap>
																			<span class="mandatoryFieldMark" id="mandatoryMarkSchedulerOnSave">*</span>
																			<span id="lblSchedulerOnSave" ></span>
																			<%-- <label lang_key="On_Save"><spring:message code="OnSave" text="On Save" /></label> --%>															
																		</td>
																		<td >
																			<select id="ddlScheduler" name="ddlScheduler" onchange="onChangeScheduler();setParametrOfChange();" style="width:90%;"> 
																				<option value="0">Choose</option>
																			</select>
																			<select id="ddlOnSave" name="ddlOnSave" onchange="onChangeOnSave();setParametrOfChange();" style="width:90%;"> 
																				<option value="0">Choose</option>
																			</select>
																				
																		</td>
																		<td class="cssStaticData" id="tdInteraval" nowrap> 
																			<span class="mandatoryFieldMark">*</span>
																			<label lang_key="Interval"><spring:message code="Interval" text="Interval" /></label>
																		</td>
																		<td>
																			<input type="text" onchange="setParametrOfChange();" name="txtInterval" id="txtInterval" size="4" maxlength="3" 
																			onblur="focusInput(event)" onfocus="focusInput(event)" onkeypress="validatePositiveNumber(event.keyCode, this.value, event); setParametrOfChange();">
																			<span class="cssStaticData" id="lblIntervalType"></span>
																		</td>
																</tr>
																<tr>
																	<td valign="Top" align="right">
																		<input type="checkbox" id="cbActive" name="cbActive" onchange="setParametrOfChange();">	    
																	</td>
																	<td class="cssStaticData" valign="Top" style="text-align:left" nowrap> 
																		<label lang_key="Active"><spring:message code="Active" text="Active" /></label>
																	</td>														
																	<td colspan=2></td>
																</tr>
																<tr>
																	<td valign="Top" align="right">
																		<input type="checkbox" id="cbResend" name="cbResend" onchange="setParametrOfChange();">	    
																	</td>
																	<td class="cssStaticData" valign="Top" style="text-align:left" nowrap> 
																		<label lang_key="Resend_Notification"><spring:message code="ResendNotification" text="Resend Notification" /></label>
																	</td>														
																	<td colspan=2></td>
																</tr>
																<tr>
																	<td valign="Top" align="right">
																		<input type="checkbox" id="cbCopyToProduction" name="cbCopyToProduction" onchange="setParametrOfChange();">	    
																	</td>
																	<td class="cssStaticData" valign="Top" style="text-align:left" nowrap> 
																		<label lang_key="CopyToProduction"><spring:message code="CopyToProduction" text="Copy To Production" /></label>
																	</td>														
																	<td colspan=2></td>
																</tr>
																<tr style="display:none;"> <!-- kd 10092017 hided according to FDS -->
																	<td valign="Top" align="right">
																		<input type="checkbox" id="cbAttachment" name="cbAttachment" onchange="setParametrOfChange();">	    
																	</td>
																	<td class="cssStaticData" valign="Top" style="text-align:left;" nowrap> 
																		<label lang_key="Include_Attachment" ><spring:message code="IncludeAttachment" text="Include Attachment" /></label>
																	</td>														
																	<td colspan=2></td>
																</tr>
															</table>
														 </td>
													</tr>
													<tr>                              
														<td vAlign="Bottom" style="text-align: center; padding-bottom:4px;" noWrap>													 
															<button type="button" class="button" id="btnSaveGeneral" onclick="saveGeneral()"><span lang_key="Save"><spring:message code="Save" text="Save" /></span></button>&nbsp; 
															<button type="button" class="button" id="btnCancel" onclick="cancel()"><span lang_key="Close"><spring:message code="Close" text="Close" /></span></button>
														</td> 
													</tr>
												</table>
											</td>
										</tr>
									</table>
								</div>
								<div id="divEditMessage" style="height:550px;">
									<table style="width:100%;">
										<tr>
											<td class="ui-widget-content ui-corner-all">
												<table style="width:98%; height:530px; margin:3px 3px 6px 6px; padding:2px;">
													<tr>                
														<td style="text-align:left; padding-left:15px;" >
															<h2 class="InitiationTitle" style="text-transform: uppercase;" lang_key="Notification_Contents"><spring:message code="NotificationContents" text="Notification Contents" /></h2>
														</td>
													</tr> 
													<tr>
														<td valign="top">
															<table id="tblMessage" width="100%" align="left" style="table-layout:fixed; border-collapse: separate; border-spacing:4px; *border-collapse: expression('separate', cellSpacing = '4px');">
																<tr>
																	<td class="cssStaticData" nowrap width="15%">
																		<span class="mandatoryFieldMark">*</span>
																		<label lang_key="Subject"><spring:message code="Subject" text="Subject" /></label>															
																	</td>
																	<td width="85%" colspan=3>
																		<input type="text" onchange="setParametrOfChange();" name="txtMsgSubject" id="txtMsgSubject" size="100" maxlength="1000" 
																		onblur="focusInput(event)" onfocus="focusInput(event)" onkeypress="event.returnValue = validateLegalCharSelective(event.keyCode, null, '@');">
																		<input type="button" id="btnPasteField" title="Insert selected field"/>
																	</td>													
																</tr>
																<tr><td><br/></td></tr>
																<tr>
																	<td class="cssStaticData" rowspan="15" valign="Top" nowrap>
																		<span class="mandatoryFieldMark">*</span>
																		<label lang_key="Body"><spring:message code="Body" text="Body" /></label>															
																	</td>
																	<td rowspan="15" valign="Top" colspan=3> <!-- TODO: should impose length? -->
																		<textarea rows="15" cols="100"  id="txtMsgBody" name="txtMsgBody" 
																		 onblur="focusInput(event)" onkeypress="return imposeMaxLength(this,32000);" onfocus="focusInput(event)" 
																		></textarea>
																	</td>														
																</tr>
																<tr></tr><tr></tr><tr></tr><tr></tr><tr></tr>
																<tr></tr><tr></tr><tr></tr><tr></tr><tr></tr>
																<tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><!-- otherwise next rows will be added along the textarea -->
																<tr>
																	<td class="cssStaticData" nowrap valign="Top">
																		<label lang_key="Available_Fields"><spring:message code="AvailableFields" text="Available Fields" /></label>															
																	</td>
																	<td class="cssTextData" colspan=2>
																		<select id="ddlAvailFields" name="ddlAvailFields" size="7" style="width:620px;"> 																
																		</select>
																	</td>	
																	<td></td>
																</tr>																										
															</table>
														 </td>
													</tr>
													<tr>                              
														<td vAlign="Bottom" style="text-align: center; padding-bottom:4px;">													 
															<button type="button" class="button" id="btnSaveMessage" onclick="saveMessage()"><span lang_key="Save"><spring:message code="Save" text="Save" /></span></button>&nbsp; 
															<button type="button" class="button" id="btnCancel" onclick="cancel()"><span lang_key="Close"><spring:message code="Close" text="Close" /></span></button>
														</td> 
													</tr>
												</table>											
											</td>
										</tr>
									</table>
								</div>
								<div id="divMessageCriteria" style="height:550px;">
									<table style="width:100%;">
										<tr>
											<td class="ui-widget-content ui-corner-all">
												<table style="width:99%; height:530px; margin:3px 3px 6px 6px; padding:2px;">
													<tr>                
                                                                                                            <td style="text-align:left; padding-left:15px;" >
                                                                                                                    <h2 class="InitiationTitle" style="text-transform: uppercase;" lang_key="Notification_Criteria"><spring:message code="NotificationCriteria" text="Notication Criteria" /></h2>
                                                                                                            </td>
													</tr> 
													<tr>
                                                                                                            <td valign="top" align="center">
                                                                                                                <div style="width:99%;"> 
                                                                                                                    <table id="tblCriteria" class="display" width="100%" align="center">
                                                                                                                            <thead>
                                                                                                                                    <tr>
                                                                                                                                            <th>CONDITION ID</th>
                                                                                                                                            <th class="TDHMain" lang_key="And_Or"><spring:message code="AndOr" text="And/Or" /></th>
                                                                                                                                            <th class="TDHMain" lang_key="Field"><spring:message code="Field" text="Field" /></th>
                                                                                                                                            <th>FIELD ID</th>
                                                                                                                                            <th class="TDHMain" lang_key="Condition"><spring:message code="Condition" text="Condition" /></th>
                                                                                                                                            <th>OPERATOR ID</th>		
                                                                                                                                            <th class="TDHMain" lang_key="Value"><spring:message code="Value" text="Value" /></th> 																														
                                                                                                                                    </tr>
                                                                                                                            </thead>
                                                                                                                            <tbody>
                                                                                                                                    <tr>
                                                                                                                                            <td></td>
                                                                                                                                            <td></td>
                                                                                                                                            <td></td> 
                                                                                                                                            <td></td>
                                                                                                                                            <td></td> 
                                                                                                                                            <td></td>															
                                                                                                                                            <td></td>
                                                                                                                                    </tr>
                                                                                                                            </tbody>                                                   										
                                                                                                                    </table>
                                                                                                                </div>
                                                                                                            </td>
													</tr>					
													<tr><td><br/></td></tr>
													<tr>                              
														<td vAlign="Bottom" style="text-align: center; padding-bottom:4px;" noWrap>
															<button type="button" class="button" id="btnAddCondition" onclick="addCondition()"><span lang_key="Add"><spring:message code="Add" text="Add" /></span></button>&nbsp; 
															<button type="button" class="button" id="btnUpdateCondition" onclick="updateCondition()" disabled><span lang_key="Edit"><spring:message code="Edit" text="Edit" /></span></button>&nbsp; 
															<button type="button" class="button" id="btnDeleteCondition" onclick="deleteCondition()" disabled><span lang_key="Remove"><spring:message code="Remove" text="Remove" /></span></button>
														</td> 
													</tr>
													<tr><td><br/></td></tr>
													<tr>
														<td>
															<fieldset id="fsConditionProperties" style="border: solid 1px #C3C3C3; padding:5px; visibility:hidden;">
																<legend style="font-size: 11pt;" class="InitiationTitle" lang_key="Condition_Properties"><spring:message code="ConditionProperties" text="Condition Properties" /></legend>
																<table style="width:98%; table-layout:fixed; border-collapse: separate; border-spacing:4px; *border-collapse: expression('separate', cellSpacing = '4px');">													
																	<tr>
																		<td class="cssStaticData" nowrap width="15%">
																			<span class="mandatoryFieldMark">*</span>
																			<label lang_key="AndOr"><spring:message code="And/Or" text="And/Or" /></label>															
																		</td>
																		<td class="cssTextData" style="height:30px; width:30%;">
																			<select id="ddlConditionType" name="ddlConditionType" onchange="setParametrOfChange();" style="width:90%;"> 
																				<option value="And">And</option>
																				<option value="Or">Or</option>
																			</select>
																		</td>															
																		<td class="cssStaticData" nowrap width="15%"> 
																			<span class="mandatoryFieldMark">*</span>
																			<label lang_key="Field"><spring:message code="Field" text="Field" /></label>
																		</td>
																		<td class="cssTextData" style="height:30px; width:30%;">
																			<select id="ddlConditionField" name="ddlConditionField" onchange="onChangeConditionField(this);setParametrOfChange();" style="width:90%;"> 
																				<option value="0">Choose</option>
																			</select>
																		</td>																														
																	</tr>
																	<tr>															
																		<td class="cssStaticData" nowrap> 
																			<span class="mandatoryFieldMark">*</span>
																			<label lang_key="Condition"><spring:message code="Condition" text="Condition" /></label>
																		</td>	
																		<td class="cssTextData" height="30px">
																			<select id="ddlOperator" name="ddlOperator" onchange="onChangeOperator(this);setParametrOfChange();" style="width:90%;"> 
																				<option value="0">Choose</option>
																			</select>
																		</td>																													
																		<td class="cssStaticData" nowrap>
																			<span class="mandatoryFieldMark" id="mandatoryValue">*</span>
																			<label lang_key="Value"><spring:message code="Value" text="Value" /></label>
																		</td>
																		<td>
																			<input type="text" onchange="setParametrOfChange();" name="txtConditionValue" id="txtConditionValue" style="width:88%;" 
																			onblur="focusInput(event)" onfocus="focusInput(event)" disabled='disabled'>
																		</td>			
																		
																													
																	</tr>	
																	<tr>                              
																		<td style="text-align:center; padding-bottom:4px;" noWrap colspan=4>													 
																			<button type="button" class="button" id="btnSaveCondition" onclick="saveCondition()"><span lang_key="Update"><spring:message code="Update" text="Update" /></span></button>&nbsp; 
																			<button type="button" class="button" id="btnCancelCondition" onclick="cancelCondition()"><span lang_key="Clear"><spring:message code="Clear" text="Clear" /></span></button>
																		</td> 
																	</tr>													
																</table>
															</fieldset>
														 </td>
													</tr>										
												</table>
											</td>
										</tr>
									</table>
								</div>
								<div id="divDistList" style="height:550px;">
									<table style="width:100%;">
										<tr>
											<td class="ui-widget-content ui-corner-all">
												<table style="width:99%; height:530px; margin:3px 3px 6px 6px; padding:2px;">
													<tr>                
														<td style="text-align:left; padding-left:15px;" >
															<h2 class="InitiationTitle" style="text-transform: uppercase;" lang_key="Distribution_List"><spring:message code="DistributionList" text="Distribution List" /></h2>
														</td>
													</tr> 
													<tr>
														<td valign="top" align="center">
                                                                                                                    <div style="width:99%;"> 
															<table id="tblDistribution" class="display" width="100%" align="center">
																<thead>
																	<tr>
																		<th>ADDRESSEE ID</th>
																		<th class="TDHMain" lang_key="Type_of_Recipient"><spring:message code="TypeOfRecipient" text="Type Of Recipient" /></th>
																		<th>ADDRESS TYPE ID</th>
																		<th class="TDHMain" lang_key="Recipient"><spring:message code="Recipient" text="Recipient" /></th>		
																		<th>USER ID</th>
																		<th class="TDHMain"></th>																													
																	</tr>
																</thead>
																<tbody>
																	<tr>
																		<td></td>
																		<td></td>
																		<td></td> 
																		<td></td> 
																		<td></td> 
																		<td></td> 															
																	</tr>
																</tbody>                                                   										
															</table>
                                                                                                                    </div>
														</td>
													</tr>					
													<tr><td><br/></td></tr>
													<tr>                              
														<td vAlign="Bottom" style="text-align: center; padding-bottom:4px;" noWrap>
															<button type="button" class="button" id="btnAddAddressee" onclick="addAddressee()"><span lang_key="Add"><spring:message code="Add" text="Add" /></span></button>&nbsp; 
															<button type="button" class="button" id="btnUpdateAddressee" onclick="updateAddressee()" disabled><span lang_key="Edit"><spring:message code="Edit" text="Edit" /></span></button>&nbsp; 
															<button type="button" class="button" id="btnDeleteAddressee" onclick="deleteAddressee()" disabled><span lang_key="Remove"><spring:message code="Remove" text="Remove" /></span></button>
														</td> 
													</tr>
													<tr><td><br/></td></tr>
													<tr>
														<td>
															<fieldset id="fsDistributionProperties" style="border: solid 1px #C3C3C3; padding:5px; visibility:hidden; ">
																<legend style="font-size: 11pt;" class="InitiationTitle" lang_key="Distribution_Properties"><spring:message code="DistributionProperties" text="Distribution Properties" /></legend>
																<table style="width:98%; table-layout:fixed; border-collapse: separate; border-spacing:5px; *border-collapse: expression('separate', cellSpacing = '4px');">													
																	<tr>
																		<td class="cssStaticData" nowrap width="15%">
																			<span class="mandatoryFieldMark">*</span>
																			<label lang_key="Send"><spring:message code="Send" text="Send" /></label>
																		</td>
																		<td class="cssTextData" height="30px">
																			<select id="ddlSendType" name="ddlSendType" onchange="setParametrOfChange();" style="width:25%;"></select>
																		</td>		
																		<td valign="Top" align="right">
																			<input type="checkbox" id="cbMessageOnly" name="cbMessageOnly" onchange="setParametrOfChange();" style="display:none;">	 <!-- kd 31082017 for next version-->   
																		</td>
																		<td class="cssStaticData" valign="Top" style="text-align:left" nowrap> 
																			<label lang_key="Message_Only" style="display:none;"><spring:message code="IsMessageOnly" text="Message Only" /></label>  <!-- kd 31082017 for next version-->   
																		</td>														
																		<td colspan=2></td>
																	</tr>
																	<tr>															
																		<td class="cssStaticData" nowrap height="30px"> 
																			<span class="mandatoryFieldMark">*</span>
																			<!-- <label lang_key="Address_Type"></label> -->
																			<spring:message code="TypeOfRecipient" text="Type Of Recipient" />
																		</td>	
																		<td class="cssTextData" id="tdRadioAddressType">																
																		</td>
																	</tr>
																	<tr>					
																		<td class="cssStaticData" nowrap>
																			<span class="mandatoryFieldMark" id="mandatoryValue">*</span>
																			<span id="lblAddressee" ></span>
																		</td>
																		<td>
																			<select id="ddlAddressee" name="ddlAddressee" onchange="setParametrOfChange();" > 
																				<option value="0">Choose</option>																	
																			</select>
																			<!-- Begin  22082017 kd added field for sending email to external address -->
																			<input type="text" onchange="setParametrOfChange();" name="txtExternalEmail" id="txtExternalEmail" style="width:88%;" 
																			onblur="focusInput(event)" onfocus="focusInput(event)" disabled='disabled'>
																			<!-- End -->
																		</td>															
																	</tr>
																	<tr>                              
																		<td vAlign="Bottom" style="text-align: center; padding-bottom:4px;" noWrap colspan=4>													 
																			<button type="button" class="button" id="btnSaveAddressee" onclick="saveAddressee()"><span lang_key="Save"><spring:message code="Save" text="Save" /></span></button>&nbsp; 
																			<button type="button" class="button" id="btnCancelAddressee" onclick="cancelAddressee()"><span lang_key="Close"><spring:message code="Close" text="Close" /></span></button>
																		</td> 
																	</tr>													
																</table>
															</fieldset> 
														 </td>
													</tr>										
												</table>
											</td>
										</tr>
									</table>
								</div>
							</div>
						</td>
					</tr>					
				</table>
				<!-- <form name="main" id="main" method="post" action="notificationmodulemainservlet"> -->
				<form name="main" id="main" method="post" action="./notificationModule.request">
					<!-- globals -->
					<input type="hidden" name="action_id" id="action_id" value='<%= request.getAttribute("CURRENT_ACTION_VALUE")%>'>				
					<input type="hidden" name="currNotificationID" id="currNotificationID" value='<%= request.getAttribute("CURRENT_NOTIFICATION_ID")%>'>		
					<input type="hidden" name="displayParametersArray" id="displayParametersArray" value="<%= request.getAttribute("CURRENT_DISPLAY_PARAMETERS")%>">
					<!-- General tab -->
					<input type="hidden" name="ddlModuleName_json" id="ddlModuleName_json" value='<%= request.getAttribute("DDL_MODULE")%>'>
					<input type="hidden" name="ddlMsgType_json" id="ddlMsgType_json" value='<%= request.getAttribute("DDL_MESSAGE_TYPE")%>'>
					<input type="hidden" name="ddlScheduler_json" id="ddlScheduler_json" value='<%= request.getAttribute("DDL_SCHEDULER")%>'>
					<input type="hidden" name="ddlTriggerType_json" id="ddlTriggerType_json" value='<%= request.getAttribute("DDL_TRIGGERTYPE")%>'>
					<input type="hidden" name="ddlOnSave_json" id="ddlOnSave_json" value='<%= request.getAttribute("DDL_ONSAVE")%>'> 	
					<!-- Edit Message tab -->	
					<input type="hidden" name="ddlAvailFields_json" id="ddlAvailFields_json" value='<%= request.getAttribute("DDL_FIELD")%>'>				
					<input type="hidden" name="txtSelectedField" id="txtSelectedField" value="">
					<input type="hidden" name="txtMsgSubjectCaretStart" id="txtMsgSubjectCaretStart" value="">				
					<input type="hidden" name="txtMsgSubjectCaretEnd" id="txtMsgSubjectCaretEnd" value="">				
					<!-- Criteria tab -->
					<input type="hidden" name="selConditionID" id="selConditionID" value="">	
					<input type="hidden" name="selFieldID" id="selFieldID" value="">
					<input type="hidden" name="selOperatorID" id="selOperatorID" value="">
					<input type="hidden" name="selConditionValue" id="selConditionValue" value="">
					<input type="hidden" name="selConditionType" id="selConditionType" value="">
					<!-- Distribution tab -->
					<input type="hidden" name="selAddresseeID" id="selAddresseeID" value="">
					<input type="hidden" name="selSendType" id="selSendType" value="">
					<input type="hidden" name="selUserID" id="selUserID" value="">
					<input type="hidden" name="selAddressTypeID" id="selAddressTypeID" value="">
					<input type="hidden" name="selEmailExternal" id="selEmailExternal" value="">
					<input type="hidden" name="rdAddressType_json" id="rdAddressType_json" value='<%= request.getAttribute("RADIO_ADDRESS_TYPE")%>'>				
				</form>
			</td>
		</tr>
	</table>
</body>
</html>
