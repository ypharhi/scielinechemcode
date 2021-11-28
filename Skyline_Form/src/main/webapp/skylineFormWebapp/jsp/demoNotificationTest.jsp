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
	<link href="../skylineFormWebapp/CSS/comply_theme/ColumnFilterWidgets.css" rel="stylesheet" type="text/css" />      
	 
	<script src="../skylineFormWebapp/js/comply_js/jquery.js" type="text/javascript"></script>
	<script src="../skylineFormWebapp/js/comply_js/jquery.dataTables.js" type="text/javascript"></script>  
	<script src="../skylineFormWebapp/js/comply_js/jquery-ui.custom.js" type="text/javascript"></script>
	<script src="../skylineFormWebapp/js/comply_js/jquery.dataTables.columnFilter.js" type="text/javascript"></script>
	<script src="../skylineFormWebapp/js/comply_js/jquery-migrate.js" type="text/javascript"></script>
	<script src="../skylineFormWebapp/js/comply_js/ColumnFilterWidgets.js" type="text/javascript"></script>
	<script src="../skylineFormWebapp/js/comply_js/CommonFuncs.js" type="text/javascript"></script>
        <script src="../skylineFormWebapp/js/comply_js/loading.js" type="text/javascript"></script>   <!-- There are error: can't to find page-loader.gif -->
        <script src="../skylineFormWebapp/js/comply_js/Lang_Selection.js" type="text/javascript"></script>
	<title>
		General - Notification List
	</title>
	
	<script type="text/javascript">
	var userSite;
	var oTable;
	var totalRecords = 0;    
	var selActive;
	var rowID;
	var rowSelected;
	var selectedRowOrder;
	var displayStart = 0;
	var currNotificationID;
	var isViewer = true; // anyone without updating permissions (i.e, not admin)
    
    $(document).ready(function () 
    { 
		$('.button').button();
		userSite = $('#currUserSite').val();
		
		$("#notifications tbody").on('click', function (event) 
		{
			var has_row_selected = 0;
		   
			if($(event.target.parentNode).children().hasClass('row_light'))
			{
				has_row_selected = 1;
			}
			
			$('#notifications').find('.row_light').each(function()
			{
				$(this).removeClass("row_light"); 
				$("#btnUpdate").button('option', 'disabled', true);				
				$("#btnDelete").button('option', 'disabled', true); 
				//rowSelected = 0;
			});
			
			if(has_row_selected == 0)
			{
				if ($(event.target.parentNode).hasClass('row_selected')) 
				{  
					 $("#btnUpdate").button('option', 'disabled', true);					 
					 $("#btnDelete").button('option', 'disabled', true);
					 $(event.target.parentNode).removeClass('row_selected'); 
					 //rowSelected = 0;        
				} 
				else 
				{
					$(oTable.fnSettings().aoData).each(function () {
						$(this.nTr).removeClass('row_selected');
					}); 
					if(totalRecords > 0)
					{
						$("#btnUpdate").button('option', 'disabled', false);
						checkPagePermission();
						$(event.target.parentNode).addClass('row_selected');
						//rowSelected = 1;
					} 
				}
			}
		}); 

		var act = $('#action_id').val();		
		if(act == 'back')
		{
			var str = $('#displayParametersArray').val();
			var arr = new Array();
			arr = str.split(",");
			rowID = arr[0];
			selActive = arr[2];
			displayStart = (Math.floor(arr[1] / 15)) * 15;		
			$("#ddlActive").val(selActive);
		}
		else
		{			
			$("#ddlActive").val('1');
			displayStart = 0;	
			selActive = 1;
			rowID = 0;
		}

		oTable = $("#notifications").dataTable
		({
			"bServerSide": true,
			"sAjaxSource": "./notification.request?action_id=getNotificationTable&currActive=" + selActive,
			"bProcessing": true,
			"sPaginationType": "full_numbers",
			"iDisplayLength": 15,
			"iDisplayStart": displayStart,
			"bJQueryUI": true,
			"sDom": "Tfrtip",
			"oLanguage": { 
						 "sSearch": "Global Search:"     
						}, 
			"aoColumnDefs":[
								{
									"aTargets":[0],                 //[0] - NOTIFICATION_ID
									"bSearchable": false,
									"bSortable": false,
									"bVisible": false
								}, 
								{
									"aTargets":[1],                 //[1] - QUERY NAME
									"sWidth": "100px"									
								},
								{
									"aTargets":[2],                 //[2] - DESCRIPTION
									"sWidth": "300px"
								},																
								{
									"aTargets":[3],                 //[3] - ACTIVE
									"bSearchable": false,
									"bSortable": true,
									"sClass": "center",
									"sWidth": "50px",
									"fnRender": function(obj)
									{										   
									   if(obj.aData[3] == 0)
									   { 
										  return "No"; 
									   }
									   else
									   {
										  
										  return "Yes"; 
									   } 
									 }
								 },									
								 {
									"aTargets":[4],                 //[4] - ROW ORDER
									"bSearchable": false,
									"bSortable": false,
									"bVisible": false
								}
							],
			"fnCreatedRow": function( nRow, aData, iDataIndex )
			{ 
				if ( aData[0] == rowID)       
				{      
					$("td:eq(0)", nRow).addClass("row_light");
					$("td:eq(1)", nRow).addClass("row_light");
					$("td:eq(2)", nRow).addClass("row_light");
					$("td:eq(3)", nRow).addClass("row_light");
					$("td:eq(4)", nRow).addClass("row_light");							
					
					rowSelected = 1;
					$('#currNotificationID').val(aData[0]);
					selectedRowOrder = aData[4];
				} 						
			},
			"fnDrawCallback": function( oSettings ) 
			{         
				totalRecords = oSettings.fnRecordsDisplay();
				if(rowSelected == 1)
				{
					$("#btnUpdate").button('option', 'disabled', false);
					checkPagePermission(); 
					rowSelected = 0;
				}
				else
				{
					$("#btnUpdate").button('option', 'disabled', true);						
					$("#btnDelete").button('option', 'disabled', true);
				}
			},
			"fnServerData": function ( sSource, aoData, fnCallback ) 
			{     
				$.ajax
				({         
					"dataType": 'json',         
					"type": "POST",         
					"url": sSource,         
					"data": aoData,         
					"success": fnCallback,         
					"error": handleAjaxError       
				}); 
			}							
		});
		
		oTable.columnFilter({
			bUseColVis: true,
			aoColumns: [ null, {type: "text"}, {type: "text"}, null, null ]
		}); 
			  

		$('#notifications tbody').on('click dblclick', 'tr', function(event)
		{
			var aData = oTable.fnGetData(this);
			if(aData != null && totalRecords != 0)
			{
				$('#currNotificationID').val(aData[0]); 				
				rowID = aData[0];
				selectedRowOrder = aData[4];
				currNotificationID = $('#currNotificationID').val();
				$('#notifications tbody tr').dblclick(function()
				{
					if (!isViewer)
					{
						editNotification();
					}
				});
			}
		});
		 
		$("#ddlActive").change( function(e) {             
			selActive = $(this).val();				
			rowSelected = 0;
			var oSettings = oTable.fnSettings(); 
			oSettings._iDisplayStart = 0;
			oSettings.sAjaxSource = "./notification.request?action_id=getNotificationTable&currActive=" + selActive,
			oTable.fnDraw(false);
		}); 
		
		checkPagePermission(); 
		initUserCommentDiv();
		initConfirmDialogDiv();
                initWaitMessageDiv();
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
        hideWaitMessage();
        if (xhr.status == 200 && xhr.responseText.match(/TIME_IS_OUT/)) 
        {
            top.location.href = 'Login.jsp';
        }  
    }        
    
    function setDisplayParameters(mode)
    {
        var arr = new Array();
        if(mode == 0)
        {
            // selected row ID
            arr[0] = 0;
			$('#currNotificationID').val('0');
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
    
    function newNotification() 
    {
    	if($('#createNotifications').val() == null || $('#createNotifications').val() == 0) {
    		alert('Create new notifications is blocked! This ENV. gets new notifications only during the version updates.');
    		return;
    	}
        setDisplayParameters(0);
        $('#action_id').val('new');
        $('#main').attr('action', './notificationModule.request');
        $('#main').submit();
    }
    
    function editNotification() 
    {
        setDisplayParameters(1);
        $('#action_id').val('edit');
        $('#main').attr('action', './notificationModule.request');
        $('#main').submit();
    }
	
	function deleteNotification() 
    {   
		openConfirmDialog({ message:"OpenConfirmDialog_Msg_Short_Name_T_10",
							onConfirm: function() {
								openCommentDialogAndCommit({ maxLength:200, includeComment:true, okHandler:submitDelete });
							}
						});	
	}

	function submitDelete(comment)
	{
		showWaitMessage("Deleteing, please wait...");
		currNotificationID = $('#currNotificationID').val();
		var paramsStr = "action_id=deleteNotification&currNotificationID=" + currNotificationID + "&comment=" + comment; 
		var url="./notification.request"; 
		xmlHttp1.open("POST", url, true);
		xmlHttp1.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
		xmlHttp1.onreadystatechange = DeleteCompleted;            
		xmlHttp1.send(paramsStr);
    } 

	function DeleteCompleted()
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
                    displayAlertDialog("DispAlDial_Msg_Short_Name_T_102", { title:"TitleInMess_Msg_Short_Name_T_2" });                    
                }  
                else
                {
                    // selActive was set as global var on-load, and on-change
                    var oSettings = oTable.fnSettings();  
                    oSettings.sAjaxSource  = "./notification.request?action_id=getNotificationTable&currActive=" + selActive;
                    oTable.fnDraw(false);
                } 
            }
        }
    }
    
    function checkPagePermission()
    {    
        var role = $('#currUserRole').val(); 
		var maintenanceTable = $('#maintenanceTblPermission').val();
		
		/* if(role == 'A' || maintenanceTable == '1') */
		{
			$("#btnNew").button('option', 'disabled', false);
			$("#btnUpdate").button('option', 'disabled', false);              
			$("#btnDelete").button('option', 'disabled', false);
			isViewer = false;
		}/* 
		else
		{
			$("#btnNew").button('option', 'disabled', true);   
			$("#btnUpdate").button('option', 'disabled', true);   			
			$("#btnDelete").button('option', 'disabled', true);     
		} */
    }
	</script>
	
</head>
<body>
<%
   String pageHeader = "<label lang_key='Notifications' ></label>"; /* <spring:message code='Notifications' text="" /> */
   String pageTitle = "<SPAN class='cssActivePath' lang_key='Notification_List'></SPAN>"; /* <spring:message code='NotificationList' text="" /> */
   String pageIcon = "../images/IconProcManageS.gif";
   request.setAttribute("NAVIGATION", "");
%>

	<table style="width:100%;" class="containment-wrapper">
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
						<td class="ui-widget-content ui-corner-all">
							<table style="width:100%;">
								<tr>
									<td style="text-align:left; padding-left:15px" class="InitiationTitle">
										<h2 style="text-transform: uppercase;" lang_key="Notification_List"><spring:message code="NotificationList" text="Notification List" /></h2>
									</td>
								</tr> 
								<tr>
									<td>
										<table>
											<tr>
												<td class="cssStaticData" style="padding-left:15px;">
													<label lang_key="Show_Active"><spring:message code="ShowActive" text="Show Active" /></label>
												</td>
												<td class="cssTextData" style="width:120px">
													<select name="ddlActive" id="ddlActive" style="width:100%; height: 20px;">
														   <option value="2">Show All</option>
														   <option value="1">Active</option>
														   <option value="0">Not Active</option>
													</select>  
												</td>
											</tr>
										</table>
								   </td>
								</tr>			
								<tr>
									<td align="center">
										<div style="height:500px; width:98%; padding:2px;" >                                  
                                               <table id="notifications" class="display" align="center" width="100%" style="text-align:left;" >
                                                       <thead style="text-transform: uppercase;">
                                                               <tr >
                                                                       <th>NOTIFICATION ID</th>
                                                                       <th class="TDHMain" lang_key="Context"><spring:message code="Context" text="Context" /></th>  <!-- kd 10092017. Change Query Name on Context according to FDS -->     
                                                                       <th class="TDHMain" lang_key="Description"><spring:message code="Description" text="Description" /></th>                                                             
                                                                       <th class="TDHMain" lang_key="Active"><spring:message code="Active" text="Active" /></th>                                                                                                                        
                                                               </tr>
                                                       </thead>
                                                       <tbody>
                                                       </tbody>
                                                       <tfoot>
                                                               <tr>
                                                                       <th></th>
                                                                       <th></th>
                                                                       <th></th> 
                                                                       <th></th>                                                                                                                                                                              
                                                               </tr>
                                                       </tfoot>
                                               </table>											
										</div>
									</td>  
								</tr>
								<tr>
									<td style="text-align:center; padding-bottom:10px; padding-top:10px;">											
										<button type="button" id="btnUpdate" class="button" onclick="editNotification()" disabled><span lang_key="View"><spring:message code="View" text="View" /></span></button>&nbsp; <!-- kd 10092017. Change Update on View according to FDS -->                                              
										<button type="button" id="btnNew" class="button" onclick="newNotification()"><span lang_key="New"><spring:message code="New" text="New" /></span></button>&nbsp; 
										<%-- <button type="button" id="btnDelete" class="button" onclick="deleteNotification()" disabled><span lang_key="Delete"><spring:message code="Delete" text="Delete" /></span></button> --%> <!-- kd 10092017. Hided according to FDS -->
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</table>
				
				<!-- 
				<form name="main" id="main" method="post" action="frmnotificationlistservlet"> 
				-->
				<form name="main" id="main" method="post" action="./notification.request">
					<input type="hidden" name="action_id" id="action_id" value='<%= request.getAttribute("CURRENT_ACTION_VALUE")%>'>
					<%-- <input type="hidden" name="currUserRole" id="currUserRole" value='<%= ((User)session.getAttribute("USER")).ROLE %>'>
					<input type="hidden" name="currUserSite" id="currUserSite" value='<%= ((User)session.getAttribute("USER")).SITE_ID %>'> 
					<input type="hidden" name="maintenanceTblPermission" id="maintenanceTblPermission" value='<%= ((User)session.getAttribute("USER")).MAINTENANCE_TABLES%>'> --%>
					<input type="hidden" name="ddlSubstance_json" id="ddlSubstance_json" value='<%= request.getAttribute("DDL_SUBSTANCE")%>'></input> 
					<input type="hidden" name="ddlSite_json" id="ddlSite_json" value='<%= request.getAttribute("DDL_SITES")%>'></input> 
					<input type="hidden" name="currentSiteID" id="currentSiteID" value=""> 
					<input type="hidden" name="currNotificationID" id="currNotificationID" value="">
					<input type="hidden" name="newProductID" id="newProductID" value="">
					<input type="hidden" name="currProductCode" id="currProductCode" value="">
					<input type="hidden" name="displayParametersArray" id="displayParametersArray" value="<%= request.getAttribute("CURRENT_DISPLAY_PARAMETERS")%>">
					<input type="hidden" name="createNotifications" id="createNotifications" value="<%= request.getAttribute("CREATE_NOTIFICATION")%>">
					<input type="hidden" name="curVersion" id="curVersion" value=""> 
					<input type="hidden" name="curSubstanceID" id="curSubstanceID" value=""> 
					<input type="hidden" name="curSubstanceValue" id="curSubstanceValue" value=""> 
				</form>
			</td>
		</tr>
	</table>
</body>
</html>