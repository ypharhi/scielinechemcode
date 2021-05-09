<!doctype html>
<%@ include file="../include/include.jsp"%>
<html lang="en">
<head>




    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>Skyline Reports Generator</title>
    <link rel="icon" href="../skylineFormWebapp/images/favicon.ico?<spring:message code="Env" text="" />" />   
    <script type="text/javascript" src="../skylineFormWebapp/deps/jquery-1.12.4.js?<spring:message code="Env" text="" />"></script>
    <script src="../skylineFormWebapp/deps/jquery-ui.custom_new.min.js"></script> 
    <script src="../skylineFormWebapp/js/foundation.min.js" ></script>
	<%@ include file="../include/includeElement_js.jsp"%>
	<%@ include file="../include/includeBL_js.jsp"%>
    <%@ include file="../include/includeJS.jsp"%>
    <!--Include webix library:-->
    <script src="../skylineFormWebapp/ui_reports/webix-pro/webix.js?<spring:message code="Env" text="" />"></script>
    <link rel="stylesheet" href="../skylineFormWebapp/ui_reports/webix-pro/webix.css?<spring:message code="Env" text="" />"> 
    <!--<link rel="stylesheet" href="../skylineFormWebapp/CSS/webix.css">-->
    <!-- Include webix query builder-->
    <script type="text/javascript" src="../skylineFormWebapp/ui_reports/querybuilder/querybuilder.js?<spring:message code="Env" text="" />"></script>
    <link rel="stylesheet" type="text/css" href="../skylineFormWebapp/ui_reports/querybuilder/querybuilder.css?<spring:message code="Env" text="" />">
    <!--Include application-->
    <script src="../skylineFormWebapp/ui_reports/main.js?<spring:message code="Env" text="" />"></script>
    <link rel="stylesheet" href="../skylineFormWebapp/ui_reports/main.css?<spring:message code="Env" text="" />">
    <link href="../skylineFormWebapp/CSS/comply_theme/app.css?<spring:message code="Env" text="" />" rel="stylesheet" type="text/css">
    <link href="../skylineFormWebapp/CSS/comply_theme/tmpCSS.css?<spring:message code="Env" text="" />" rel="stylesheet" type="text/css">
	
    <%-- 	<script src="../skylineFormWebapp/js/foundation.min.js" ></script>
	<%@ include file="../include/includeElement_js.jsp"%>
	<%@ include file="../include/includeBL_js.jsp"%> 
	<%@ include file="../include/includeJS.jsp"%> 
	<%@ include file="../include/includeExtendedJS.jsp"%> --%>
	<style>
		.webix_header > div
		{
		  color: #757575 !important; 
		}
		.webix_view.webix_header 
		{
		  background-color: transparent !important;
		}
		.webix_el_box
		{
			overflow:visible;
		}
		button.webix_el_htmlbutton, button.webixtype_base
		{
			margin-left: 0;
			padding: 0;
		}
		.webix_inp_top_label
		{
			font-weight:normal;
		}
		
		.webix_cell 
		{
			overflow: hidden;
			line-height: 31px;
			white-space: nowrap;
			text-overflow: ellipsis;
		}
		
		.webix_hcell 
		{
			width: 100%;
			overflow: hidden;
			white-space: nowrap;
			text-overflow: ellipsis;
		}
	</style>
	<script>
	    var COMPLY_reportTitleVar_ = "Report";
	    
	    function changeHeaderDesignCOMPLY(){
	    		console.log("header count: " + $('[view_id="$label1" ] div').length);
	    	//		while($('[view_id="$label1" ] div').length==0);
	    	var parentDiv = $('div.layout_reports_generated').find('div')[0]; //kd 08082018 changed 
	    	var childDiv = $(parentDiv).find('div')[0];
	    	var header = $(childDiv).html();
	    	
	    	$(childDiv) .html('');
    	    var element = document.createElement('h1');
    	    var text = document.createTextNode(header);
    	    element.appendChild(text);
    	    element.className += "cssPageHeader text-center";
    		$(childDiv) .append(element);
	    }
	
		function addWebixTooltip(eventName) {
			$('[role="gridcell"],[role="columnheader"]').each(function () {
				
				var lastCutExpresion = "</div>"; // in grouping the value contins expresion end with div 
		    	var elementObj = $(this);
		    	var elementHtml = elementObj.html();
		    	var lastCutPosition = elementHtml.lastIndexOf(lastCutExpresion);
		    	var elementHtmlDisplay = lastCutPosition > 0 ? elementHtml.substring(lastCutPosition + lastCutExpresion.length) : elementHtml;
		    	elementObj.attr('title', elementHtmlDisplay);
		    });
		    
			
			//also set COMPLY_reportTitleVar_
			try {
				var divTitle_ =  ($('[view_id="$label1"]').children()[0]);
				if (typeof divTitle_ !== "undefined") {  
					COMPLY_reportTitleVar_ = divTitle_.textContent;
				}
			} catch(e) {
				console.log("addWebixTooltip error in set COMPLY_reportTitleVar_");
			}
	    	 
		}
		
		function initOptionList(){
			var html='<div class="webix_view webix_window webix_popup" role="dialog" tabindex="0" view_id="$suggest_optionList" style="border-width: 1px; display: none;">\n'/*  width: 204px; height: 356px; top: 312px; left: 266px; z-index: 112; */
						+'<div class="webix_win_content" style="padding: 8px;">\n'
							+'<div class="webix_win_head" style="display: none;"></div>\n'
							+'<div class="webix_win_body">\n'
								+'<div class="webix_view webix_list" role="listbox" view_id="$suggest_optionList_list" style="overflow: hidden; ">'//width: 188px; height: 340px;
									
								+'</div>'
							+'</div>'
						+'</div>'
					+'</div>'
					+'<div class="webix_point_bottom" style="z-index: 130; position: absolute;display:none;"></div>';
			$('body').append(html);
			$( document.body  ).click(function() {
			    if($('[view_id="$suggest_optionList"]')!= "undefined" && $('[view_id="$suggest_optionList"]').css('display') == 'block'){
					$('[view_id="$suggest_optionList"]').css('display','none');
					$('[view_id="$suggest_optionList"]').next('.webix_point_bottom').css('display','none');
				return;
			}
			});
		}
		
		function addClickEventToQueryRuleCOMPLY(){
			$('.webix_querybuilder_rule_input').find(':input').off('click');
			$('.webix_querybuilder_rule_input').find(':input').click(function(){ openOptionList(this)});
		}
		
		function openOptionList(elementObj){
			var field = $(elementObj).parents('.webix_querybuilder_rule_input').siblings('.webix_querybuilder_value_select').text();
			var reportName = $('[view_id="$label1"]').text();
			if($('#userId').val() == '28991'){//system
				alert("reportName:"+reportName+".</br> field name:"+field);
			}
			var locationX = $(elementObj).offset().left;
			var locationY = $(elementObj).offset().top;
			var width = $(elementObj).css('width');
			var height = $(elementObj).css('height');
			if($('[view_id="$suggest_optionList"]').css('display') == 'block'){
				/* $('[view_id="$suggest_optionList"]').css('display','none');
				$('[view_id="$suggest_optionList"]').next('.webix_point_bottom').css('display','none'); */
				return;
			} 
			//ajax call
			var data_ = {
	              "reportName": reportName,
	              "fieldName": field
	        	};
			
			 $.ajax({
	            type: 'POST',
	            data: data_,
	            url: "./onUIReportRuleListChange.request",
	            //contentType: 'application/json',
	            dataType: 'json',
	            success: function (obj) {
	                if (obj.errorMsg != null && obj.errorMsg != '') {
	                    displayAlertDialog(obj.errorMsg);
	                } else if (obj.data[0].val == "") {
						return;
	                } else {
	                	$('[view_id="$suggest_optionList"]').css('display','none');
						$('[view_id="$suggest_optionList"]').next('.webix_point_bottom').css('display','none');
	                	var optionsHtml = '<div class="webix_scroll_cont">';
	                    optionsArray = obj.data[0].val.split(',');
	                    optionsArrayLength = optionsArray.length;
	                    for (i = 0; i < optionsArrayLength; i++) {
	                        optionsHtml += '<div webix_l_id="'+optionsArray[i]+'" class="webix_list_item" style="width:auto; height:34px; overflow:hidden;" role="option" tabindex="-1">'+optionsArray[i]+'</div>';
	                    }
	                    optionsHtml += '</div>';
	                    $('[view_id="$suggest_optionList_list"]').html(optionsHtml);
	                    
	                    var listHeight = 34*(optionsArrayLength%10)+16;
						$('[view_id="$suggest_optionList"]').width(width);
						$('[view_id="$suggest_optionList"]').height(listHeight);
						
						$('[view_id="$suggest_optionList_list"]').width(width-16);
						$('[view_id="$suggest_optionList_list"]').height(listHeight-16);
						
						//$('[view_id="$suggest_optionList"]').offset({top:(locationY-listHeight),left:locationX});
						$('[view_id="$suggest_optionList"]').css({top:(locationY-listHeight-6),left:locationX});
						$('[view_id="$suggest_optionList"]').css('z-index','130');
						
						$('[view_id="$suggest_optionList"]').next('.webix_point_bottom').css({top:(locationY-listHeight-5+listHeight),left:(locationX+Number(width.replace('px',''))/2)})
						
						$('[view_id="$suggest_optionList_list"] div.webix_list_item').click(function () {
					        
				        	$(elementObj).val($(this).text());
					        /* $('[view_id="$suggest_optionList"]').css('display','none');
					        $('[view_id="$suggest_optionList"]').next('.webix_point_bottom').css('display','none'); */
					    });
					    
					    $('[view_id="$suggest_optionList"]').css('display','block');
					    $('[view_id="$suggest_optionList"]').next('.webix_point_bottom').css('display','block');   
	                }
	            },
	            error: handleAjaxError
	        });
		}
			
		function handleAjaxError(xhr, textStatus, error) {
			hideWaitMessage();
			//alert("error: " + xhr + " status: " + textStatus + " er:" + error);
		    // ajax callback error
		    if (xhr.responseText.match(/TIME_IS_OUT/)) {
		        top.location.href = 'Login.jsp';
		    } else if (xhr.responseText.indexOf('function Login()') != -1) {
		        top.location.href = 'Login.jsp';
		    } else {
		    	if(textStatus != null && textStatus =='error') {
		    		console.log("handleAjaxError() textStatus == error");
		    		console.log(error); 		
		    		if(error !== 'Internal Server Error')
		    		{
		    			displayAlertDialog("Server connection error");
		    		}
		    	} else {
		    	    displayAlertDialog("error: " + xhr + " status: " + textStatus + " er:" + error);
		    	}
		    }
		    
		}			
		
		$(document).ready(function()
		{
	    	$(document).foundation();
	    	$('#tdIncludeBreadcrumbJsp').css('display', 'none');

	    	initOptionList();
		});
	</script>
</head>
<body id="app" style="overflow-x: auto;">
	<input type="hidden" id="userId" name="userId" value="${userId}">
	<input type="hidden" id="stateKey" name="stateKey" value="${stateKey}"><input type="hidden" id="formPathInfo" name="formPathInfo" value='${formPathInfo}'>
	<table style="width:100%;">
					<tr>
						<td class="top-bar-container">
							<div style="width:100%;">
								<%@ include file="./PageHeaderJsoTemplateForm.inc" %>
								<%@ include file="../include/includeMenu.jsp" %>
							</div>		
						</td>
					</tr>										
				</table>
</body>
</html>