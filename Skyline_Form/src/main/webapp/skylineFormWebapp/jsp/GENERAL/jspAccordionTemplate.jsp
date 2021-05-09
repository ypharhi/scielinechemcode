<%@ include file="../include/include.jsp"%>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Skyline ${browserTitle}</title>

<%@ include file="../include/includeCSS.jsp"%>	
 <link href="../skylineFormWebapp/CSS/comply_theme/jquery-ui.custom.css" rel="stylesheet" type="text/css"> 
<script src="../skylineFormWebapp/deps/jquery-1.12.4.min.js"></script>
<script src="../skylineFormWebapp/deps/jquery-ui.custom_new.min.js"></script>
<%@ include file="../include/includeElement_js.jsp"%>
<%@ include file="../include/includeBL_js.jsp"%>
<%@ include file="../include/includeJS.jsp"%>
<script src="../skylineFormWebapp/deps/jquery.browser.js"></script>
<script src="../skylineFormWebapp/deps/splitter.js"></script>
<script src="../skylineFormWebapp/deps/jquery.cookie.js"></script>
<%@ include file="../include/includeExtendedJS.jsp"%>

<style type="text/css">
.comboDetails {
	FONT-SIZE: 12px;
	font-weight: normal;
	COLOR: #000000;
	FONT-FAMILY: Verdana, Tahoma, Sans-Serif;
	TEXT-ALIGN: left;
	width: 500px;
	margin-top: 0px;
	margin-bottom: 0px;
}

.break{
    display:block;
}
caption{
    FONT-SIZE: 12pt;
    font-weight: bold;
    COLOR: #1766ad;
    text-align:center;   
    padding-right: 10px;
    white-space: noWrap;
}
.firstString {
	width:100px !important;
    margin-top: 5px !important;
    margin-bottom: 5px !important;
}
input::-ms-clear {
    display: none;
}
.ui-widget.ui-widget-content {
  border: 1px solid #dddddd;
}
.ui-widget.ui-widget-content {
  font-size: 14px;
}
.ui-widget-content a {
 color: #1779ba;
}
.splitter {
/* 	height: 540px; */
height: calc(100vh - 280px);
}
input:-webkit-autofill {
    -webkit-box-shadow: 0 0 0px 1000px white inset;
}
.ui-datepicker-trigger {
	cursor: pointer;
}
table thead th.ui-state-default, table thead th.ui-widget-content table thead th.ui-state-default, table thead th.ui-widget-header table thead th.ui-state-default{
	font-weight: bold;
} 
/* @media screen and (max-width: 1200px), screen and (max-height: 800px) { */
/*     #simple { */
/*          height:350px !important; */
/*     } */
/*      .splitter-bar-vertical{ */
/*      	height: 348px !important; */
/*     } */
/*     .splitter-bar-vertical{ */
/*      	height: 348px !important; */
/*     } */
/*     .splitter-pane{ */
/*     	overflow-y: scroll; */
/*     } */
/* } */
/* @media screen and (min-width: 1200px) and (min-height: 800px){ */
/*     #simple { */
/*         height: 540px !important;		 */
/*     } */
/*     .splitter-bar-vertical{ */
/*     	 height: 538px !important; */
/*     } */
/* } */

/* datatable tools menu */
.dropdown {
    position: relative;
    /* display: inline-block; */
    white-space: normal;
}
.dropdown-content{
    display: none;
    position: absolute; 
     background-color: #f9f9f9; 
    width: 100%;
    z-index: 1;
}
.dropdown-content a{
    color: black;    
    text-decoration: none;
    padding: 6px 16px;
    display: block;
}
.dropdown-content a:hover{background-color: #f1f1f1}
.dropdown:hover .dropdown-content{
    display: block;   
}
.splitter-pane{
    overflow: auto;    
} 
.button.ui-corner-all, [type='button'].ui-corner-all, [type='submit'].ui-corner-all {
    border-radius: 3px;
    margin-left: 15px;
    padding: 0.5em 1.5em;
}
.ui-dialog .ui-dialog-title{
	width:100%;
}
.ui-dialog .ui-dialog-titlebar{
	padding:0;
}
.ui-dialog .ui-dialog-titlebar-close{
	margin:0;
}
.ui-dialog .ui-dialog-titlebar-close.ui-corner-all,.ui-dialog .ui-dialog-titlebar.ui-corner-all{
	border-radius:0;
}
.splitter-bar-vertical {
    width: 6px;
}
.ui-icon, .ui-widget-content .ui-icon{
    background-image: none;
}
.ui-widget-header .ui-icon{
  background-image: none;
}

/* #accordion .ui-state-active, #accordion .ui-widget-content .ui-state-active, #accordion .ui-widget-header .ui-state-active{
    background: #e5e5e5;
    font-weight:bold;
}

#accordion .ui-state-default
,#accordion .ui-widget-content .ui-state-default
,#accordion .ui-widget-header .ui-state-default{
	background-color: #e5e5e5;
	color: white;
    border: 1px solid #bdbdbd;
}
.dataTableParent table.dataTable thead .ui-state-default
, .dataTableParent table.dataTable thead .ui-widget-content .ui-state-default
, .dataTableParent table.dataTable thead .ui-widget-header .ui-state-default{
    background: transparent;
    color: #757575;
    border: 1px solid #bdbdbd;
}

.dataTableParent table.dataTable thead tr[role="row"] .ui-state-default
, .dataTableParent table.dataTable thead tr[role="row"] .ui-state-default
, .dataTableParent table.dataTable thead tr[role="row"] .ui-widget-content .ui-state-default
, .dataTableParent table.dataTable thead tr[role="row"] .ui-widget-header .ui-state-default{
    background-color: #e5e5e5;
    font-weight:bold;
}

 .ui-state-default.button, .ui-widget-content .ui-state-default.button, .ui-widget-header .ui-state-default.button {
    display: inline-block;
    vertical-align: middle;
    margin: 0 0 1rem 0;
    padding: 0.85em 1em;
    -webkit-appearance: none;
    border: 1px solid transparent;
    border-radius: 3px;
    transition: background-color 0.25s ease-out,color 0.25s ease-out;
    font-size: 0.9rem;
    line-height: 1;
    text-align: center;
    cursor: pointer;
    background: #1779ba;
    color: #fefefe;
} 
.button:hover, .button:focus {
    background-color: #14679e;
    color: #fefefe;
} */
</style>

<script type="text/javascript">

	$(document).ready(function() { 
		var firstResize = 1;	
		@bm_list_ready@ 
		initPage();
		$('#toMoveRight div:first').prependTo('#simple'); // for the splitter	  
		$('#toMoveLeft table:first').prependTo('#settingContent'); // for the splitter
		$('#toRemove').parent().remove(); // for the splitter
		$(".simple").splitter({
	          type: "v",
	          outline: true, 
	          sizeLeft: 300,
	          minLeft: 10,
	          minRight: 10,
	          resizeToWidth: true,
	          dock: "left",
	          dockSpeed: 500,
	          cookie: "docksplitter",
	          sizeOfset: 500,
	          dockKey: 'Z',   // Alt-Shift-Z in FF/IE
	          accessKey: 'I'  // Alt-Shift-I in FF/IE   
	    }); 
		eval($('#accordion_hidden').val()); // should append when using accordion element
		$( "#simple" ).height($('.splitter-bar-vertical').height()+2);
		$( window ).resize(function() {
			  $('#simple').css('height','calc(100vh - 280px)');
			  $('.splitter-bar-vertical').css('height','calc(100vh - 282px)');
			  if(firstResize == 1){
					firstResize = 0;
					$(window).trigger('resize');
			  }
		});
	    if (window.self === window.top){
	    	
	    }
	    else{
	    	 $('#homePageHeaderJspTempalte').css('display','none');
	    	 $('#homePageHeaderJspTempalte').css('cursor','pointer');
	    	 $('#backHeaderJspTempalte').css('display','none');
	    	 $('#backHeaderJspTempalte').css('cursor','pointer');
	    	 $('#mbmcpebul_table').closest('tr').css('display','none');
	    	 $($( '#mbmcpebul_table').closest('tr').siblings()[1]).css('display','none');
	    }
	     
	    $('#accordion a').each(function() {
	    	if($( this ).text() != $( this ).text().toUpperCase()){	    		
	    	 var result = $( this ).text().replace( /([A-Z])/g, " $1" );
	    	 $( this ).text(result.charAt(0).toUpperCase() + result.slice(1)) ; // capitalize the first letter
	    	}
	    });
	    $('#simple a').on('click', function(){
	    	  $('#settingContent').css('display','');
	    	  $('#simple a').off('click');
	    });
	    // device detection
		if(/(android|bb\d+|meego).+mobile|avantgo|bada\/|blackberry|blazer|compal|elaine|fennec|hiptop|iemobile|ip(hone|od)|ipad|iris|kindle|Android|Silk|lge |maemo|midp|mmp|netfront|opera m(ob|in)i|palm( os)?|phone|p(ixi|re)\/|plucker|pocket|psp|series(4|6)0|symbian|treo|up\.(browser|link)|vodafone|wap|windows (ce|phone)|xda|xiino/i.test(navigator.userAgent) 
		     || /1207|6310|6590|3gso|4thp|50[1-6]i|770s|802s|a wa|abac|ac(er|oo|s\-)|ai(ko|rn)|al(av|ca|co)|amoi|an(ex|ny|yw)|aptu|ar(ch|go)|as(te|us)|attw|au(di|\-m|r |s )|avan|be(ck|ll|nq)|bi(lb|rd)|bl(ac|az)|br(e|v)w|bumb|bw\-(n|u)|c55\/|capi|ccwa|cdm\-|cell|chtm|cldc|cmd\-|co(mp|nd)|craw|da(it|ll|ng)|dbte|dc\-s|devi|dica|dmob|do(c|p)o|ds(12|\-d)|el(49|ai)|em(l2|ul)|er(ic|k0)|esl8|ez([4-7]0|os|wa|ze)|fetc|fly(\-|_)|g1 u|g560|gene|gf\-5|g\-mo|go(\.w|od)|gr(ad|un)|haie|hcit|hd\-(m|p|t)|hei\-|hi(pt|ta)|hp( i|ip)|hs\-c|ht(c(\-| |_|a|g|p|s|t)|tp)|hu(aw|tc)|i\-(20|go|ma)|i230|iac( |\-|\/)|ibro|idea|ig01|ikom|im1k|inno|ipaq|iris|ja(t|v)a|jbro|jemu|jigs|kddi|keji|kgt( |\/)|klon|kpt |kwc\-|kyo(c|k)|le(no|xi)|lg( g|\/(k|l|u)|50|54|\-[a-w])|libw|lynx|m1\-w|m3ga|m50\/|ma(te|ui|xo)|mc(01|21|ca)|m\-cr|me(rc|ri)|mi(o8|oa|ts)|mmef|mo(01|02|bi|de|do|t(\-| |o|v)|zz)|mt(50|p1|v )|mwbp|mywa|n10[0-2]|n20[2-3]|n30(0|2)|n50(0|2|5)|n7(0(0|1)|10)|ne((c|m)\-|on|tf|wf|wg|wt)|nok(6|i)|nzph|o2im|op(ti|wv)|oran|owg1|p800|pan(a|d|t)|pdxg|pg(13|\-([1-8]|c))|phil|pire|pl(ay|uc)|pn\-2|po(ck|rt|se)|prox|psio|pt\-g|qa\-a|qc(07|12|21|32|60|\-[2-7]|i\-)|qtek|r380|r600|raks|rim9|ro(ve|zo)|s55\/|sa(ge|ma|mm|ms|ny|va)|sc(01|h\-|oo|p\-)|sdk\/|se(c(\-|0|1)|47|mc|nd|ri)|sgh\-|shar|sie(\-|m)|sk\-0|sl(45|id)|sm(al|ar|b3|it|t5)|so(ft|ny)|sp(01|h\-|v\-|v )|sy(01|mb)|t2(18|50)|t6(00|10|18)|ta(gt|lk)|tcl\-|tdg\-|tel(i|m)|tim\-|t\-mo|to(pl|sh)|ts(70|m\-|m3|m5)|tx\-9|up(\.b|g1|si)|utst|v400|v750|veri|vi(rg|te)|vk(40|5[0-3]|\-v)|vm40|voda|vulc|vx(52|53|60|61|70|80|81|83|85|98)|w3c(\-| )|webc|whit|wi(g |nc|nw)|wmlb|wonu|x700|yas\-|your|zeto|zte\-/i.test(navigator.userAgent.substr(0,4))){
		
			$('div.splitter-bar-vertical').click(function() {
				 $('div.splitter-bar-vertical').dblclick();
			});
		}
		renderElementAuthorizationImp();
	    $('.displayOnLoad').css('visibility','');
	});	
@bm_list_function@

</script>
</head>
@bm_list_html@
<body style="overflow-y: hidden;">
	<input type="hidden" id="generalDisabledFlagParam" name="generalDisabledFlagParam" value="-1">
	<input type="hidden" id="isStruct" name="isStruct" value="${isStruct}">
	<input type="hidden" id="userId" name="userId" value="${userId}"><input type="hidden" id="stateKey" name="stateKey" value="${stateKey}"><input type="hidden" id="formPathInfo" name="formPathInfo" value='${formPathInfo}'>
	<input type="hidden" id="isNew" name="isNew" value="${isNew}">
	<input type="hidden" id="formId" name="formId" value="${formId}">
	<input type="hidden" id="formCode" name="formCode" value="${formCode}">
	<input type="hidden" id="formGeneralInfo" name="formGeneralInfo" value="">
	<input type="hidden" id="parentId" name="parentId" value="${PARENT_ID}">
	<input type="hidden" id="backUrl" name="backUrl" value='../skylineFormWebapp/<%= session.getAttribute("homePage") %>'>
	<input type="hidden" id="springMessages" name="springMessages" value='${springMessages}'>
	<input type="hidden" id="permissionsAccess" name="permissionsAccess" value="${PERMISSION_ACCESS}">
	<form id="doBackForm" action="doBack.request" method="post">
		<input type="hidden" id="formCode_request" name="formCode_request" value="${formCode}">
		<input type="hidden" id="stateKey_request" name="stateKey_request" value="${stateKey}">
		<input type="hidden" id="formCode_doBack" name="formCode_doBack">
	</form>
	<%@ include file="../include/includeBodyJS.jsp"%>
<!-- body -->
   <table style="width:100%;visibility:hidden;" class="displayOnLoad">
					<tr>
						<td class="top-bar-container">
							<div style="width:100%;">
								<%@ include file="./PageHeaderJsoTemplateForm.inc" %>
								<%@ include file="../include/includeMenu.jsp" %>
							</div>		
						</td>
					</tr> 				
					<tr>
						<td class="ui-widget-content ui-corner-all innerTD" colspan="10">
							<table width="100%">
								<tr>
									<td align="center" id="simpleTd" class="innerTD">
									   <div id="simple" class="simple" style="font-size: 14px">
										 <!--begin --><div>
										@body@
										<!--end --></div>
										<div id="settingContent" style="overflow:auto;display: none;">
	                        			</div>
									</div>
									</td>									
								</tr>
							</table>
						</td>
					</tr>
				</table>
</body>
</html>