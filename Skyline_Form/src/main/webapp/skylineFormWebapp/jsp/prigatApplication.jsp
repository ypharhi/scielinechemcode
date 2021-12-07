<%@ include file="../include/include.jsp"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Skyline ${browserTitle}</title>
<%@ include file="../include/includeCSS.jsp"%>
<script src="../skylineFormWebapp/deps/jquery-1.12.4.min.js"></script>
<script src="../skylineFormWebapp/deps/jquery-ui.custom_new.min.js"></script>
<%@ include file="../include/includeElement_js.jsp"%>
<%@ include file="../include/includeBL_js.jsp"%>
<%@ include file="../include/includeJS.jsp"%>
<%@ include file="../include/includeExtendedJS.jsp"%>
<style>
#wrapper {
/* 	border: 1px solid blue; */
}

#div1 {
	display: inline-block;
	width: 20p%;
	height: 100%;
/* 	border: 1px solid red; */
	vertical-align: top;
}

#div2 {
	display: inline-block;
	width: 80%;
	height: 100%;
/* 	border: 1px solid green; */
	vertical-align: top;
}

.my-iframe-container {
    border: 1px #1779ba solid;
	position: relative;
	overflow: hidden;
	width: 100%;
	padding-top: 40.25%; /* 16:9 Aspect Ratio (divide 9 by 16 = 0.5625) */
}

/* Then style the iframe to fit in the container div with full height and width */
.my-responsive-iframe {
	position: absolute;
	top: 0;
	left: 0;
	bottom: 0;
	right: 0;
	width: 100%;
	height: 100%;
/* 	border: 1px blue solid; */
}

.edit-item-wrapper {
	
}

.edit-item {
	display: inline;
	/*override block that make icons to move under the text*/
	width: 80%;
}

.main-data-div{ 
/*  display:none; */
  border: 1px #1779ba solid; 
  margin: auto;
  padding: 2px;
  width: 98%;
  
}

.div-table-wrapper {  
/*    border: 1px green solid;  */
   width: 80%;
   margin: auto;
  padding: 2px;
}
.td-place-holder {
  height: 5px;
}

</style>
<script>
	$(document).ready(function() { 
		${bookmark11_ready}  ${bookmark12_ready}  ${bookmark13_ready}  ${bookmark14_ready}  ${bookmark15_ready}  ${bookmark21_ready}  ${bookmark22_ready}  ${bookmark23_ready}  ${bookmark24_ready}  ${bookmark25_ready}  ${bookmark26_ready}  ${bookmark27_ready}  ${bookmark28_ready}  ${bookmark29_ready}  ${bookmark51_ready}  ${bookmark52_ready}  ${bookmark53_ready}  ${bookmark54_ready}  ${bookmark31_ready}  ${bookmark32_ready}  ${bookmark33_ready}  ${bookmark34_ready}  ${bookmark35_ready}  ${bookmark41_ready}  ${bookmark42_ready}  ${bookmark43_ready}  ${bookmark44_ready}  ${bookmark45_ready} 
 
		initPage();
	    if (window.self === window.top){    	
	    	 if($('#backUrl').val() != ""){    	    
	    		 //$('#close_back').attr('onclick','window.location.href = "' + $('#backUrl').val() + '"');
	    		  $('#close_back').attr('onclick','$("#doBackForm").submit()');
	    	 }
	    	 else{
	    		 $('#close_back').css('display','none');
	    	 }
	    } 
	    else{
	    	 $('#homePageHeaderJspTempalte').css('display','none');
	    	 $('#homePageHeaderJspTempalte').css('cursor','pointer');
	    	 $('#backHeaderJspTempalte').css('display','none');
	    	 $('#backHeaderJspTempalte').css('cursor','pointer');  
	    } 	
	    
	    //**********screen BL.....
	    initFrame();
	    //**********screen Done!
	    
	    renderElementAuthorizationImp();
	    //$('.displayOnLoad').css('display','');
	});
	${bookmark11_function} 	${bookmark12_function} 	${bookmark13_function} 	${bookmark14_function} 	${bookmark15_function} 	${bookmark21_function} 	${bookmark22_function} 	${bookmark23_function} 	${bookmark24_function} 	${bookmark25_function} 	${bookmark26_function} 	${bookmark27_function} 	${bookmark28_function} 	${bookmark29_function} 	${bookmark51_function} 	${bookmark52_function} 	${bookmark53_function} 	${bookmark54_function} 	${bookmark31_function} 	${bookmark32_function} 	${bookmark33_function} 	${bookmark34_function} 	${bookmark35_function} 	${bookmark41_function} 	${bookmark42_function} 	${bookmark43_function} 	${bookmark44_function} 	${bookmark45_function} 

	
	/******************************/
	/* screen BL funcs */
	/******************************/
	function initFrame() { 
		var appId = $('#formId').val(); 
	    $.ajax({
	        type: 'POST',
	        data: '{"action" : "getappitems","data":[{"code":"appid","val":"' + appId + '"}],' + '"errorMsg":""}',
	        url: "./getappitems.request",
	        contentType: 'application/json',
	        dataType: 'json',
	        success: function (obj) { 
				if(obj.errorMsg != null && obj.errorMsg.length > 0) {
					displayAlertDialog(obj.errorMsg);
				} else {
					$.each(obj.data, function( index, objCodeVal ) { //obj.data -> list of links objects -> [{code:'367760' ,val:'at1'},...]
			  	    	  insertLink(objCodeVal);
				  	});
					disableAllLinks();
				} 
	        },
	        error: function () {
	        	displayAlertDialog("Error - get item list!");
	        }
	    });
	}
	
	function disableAllLinks() {
		$('.edit-item').addClass('disabledclass');
	} 
	
	function removeIframe() {
		$('.my-responsive-iframe').remove();
	}
	
	function insertLink(objCodeVal) {
		//clone edit-item-wrapper-0  -> change id and val -> put before '.div-adhoc-marker'
  	  	var $div = $('#edit-item-wrapper-0').clone();
  	  	$div.attr('id','edit-item-wrapper-' + objCodeVal.code); // change the id
	    $('#div-adhoc-marker').before($div); // add before div-adhoc
	    var $input = $div.find(':input');
	    $input.val(objCodeVal.val); // change the val
	    $div.css("display", "block");
	    return $input;
	}
	
	function loadIframeById(id_) {
		var $iframeParent = $('.my-iframe-container');
		
		var formCode_ = 'ApplicationItem'; // TODO by some param
		var stataky_ = $('#stateKey').val();
		var userId_ = $('#userId').val();
		
		var src = 'init.request?formCode=' + formCode_ + 
				  '&formId=' + id_ + 
				  '&userId=' + userId_ + 
				  '&stateKey=' + stataky_ + 
				  '&tableType=&PARENT_ID=-1';
		
		var newElement = "<embed class='my-responsive-iframe' src='" + src + "'>";

		removeIframe();
		$iframeParent.append(newElement);
	}
	
	function editLink(obj) { 
		disableAllLinks();
		var $div = $(obj).parent('div');
		var $input = $div.find(':input');
		$input.removeClass('disabledclass');
		var id_ = $div.attr('id').replace('edit-item-wrapper-','');
		loadIframeById(id_);
	}
	
	function removeLink(obj) {
		openConfirmDialog({
			onConfirm : function()
			{ 
				var $div = $(obj).parent('div');
				var id_ = $div.attr('id').replace('edit-item-wrapper-','');
				$.ajax({
			        type: 'POST',
			        data: '{"action" : "deleteappitemname","data":[{"code":"id","val":"' +id_ +'"}],"errorMsg":""}',
			        url: "./deleteappitemname.request",
			        contentType: 'application/json',
			        dataType: 'json',
			        async: false,
			        success: function (obj) {
						if(obj.errorMsg != null && obj.errorMsg.length > 0) {
							displayAlertDialog(obj.errorMsg);
						} else {
 							$div.remove(); // remove from dom
 							removeIframe();
						}
			        },
			        error: function () {
			        	displayAlertDialog("Error - remove item!");
			        }
			    });
			},
			title : 'Warning',
			message : getSpringMessage('REMOVE_LINK_ITEM')
		})
	}
	
	function addLink() { 
		var formCode_ = 'ApplicationItem'; // TODO by some param
		var appId = $('#formId').val();
		$.ajax({
	        type: 'POST',
	        data: '{"action" : "insertappitems","data":[{"code":"appid","val":"' +appId +'"},{"code":"formcode","val":"' + formCode_ + '"}],"errorMsg":""}',
	        url: "./insertappitems.request",
	        contentType: 'application/json',
	        dataType: 'json',
	        async: false,
	        success: function (obj) {
				if(obj.errorMsg != null && obj.errorMsg.length > 0) {
					displayAlertDialog(obj.errorMsg);
				} else {
					$.each(obj.data, function( index, objCodeVal) { //obj.data -> list of links objects -> [{code:'367760' ,val:'at1'},...]
			  	    	var $input = insertLink(objCodeVal); //TODO serverside
			  			editLink($input);
				  	});
				} 
	        },
	        error: function () {
	        	displayAlertDialog("Error - add item!");
	        }
	    });
	}
	
	function updateAppItemName(obj) {
		var newVal = obj.value;
		var $div = $(obj).parent('div');
		var id_ = $div.attr('id').replace('edit-item-wrapper-','');
		$.ajax({
	        type: 'POST',
	        data: '{"action" : "updateappitemname","data":[{"code":"id","val":"' +id_ +'"},{"code":"newval","val":"' + newVal + '"}],"errorMsg":""}',
	        url: "./updateappitemname.request",
	        contentType: 'application/json',
	        dataType: 'json',
	        async: false,
	        success: function (obj) {
				if(obj.errorMsg != null && obj.errorMsg.length > 0) {
					displayAlertDialog(obj.errorMsg);
				}
	        },
	        error: function () {
	        	displayAlertDialog("Error - update item name!");
	        }
	    });
	}
	
</script>
</head>
${bookmark11_html} ${bookmark12_html} ${bookmark13_html}
${bookmark14_html} ${bookmark15_html} ${bookmark21_html}
${bookmark22_html} ${bookmark23_html} ${bookmark24_html}
${bookmark25_html} ${bookmark26_html} ${bookmark27_html}
${bookmark28_html} ${bookmark29_html} ${bookmark51_html}
${bookmark52_html} ${bookmark53_html} ${bookmark54_html}
${bookmark31_html} ${bookmark32_html} ${bookmark33_html}
${bookmark34_html} ${bookmark35_html} ${bookmark41_html}
${bookmark42_html} ${bookmark43_html} ${bookmark44_html}
${bookmark45_html}
<body">
	<!--  REMOVE THE OVERFLOE HIDEN  AJUST WITH THE my-iframe-container TOP ???!!!-->
	<input type="hidden" id="generalDisabledFlagParam"
		name="generalDisabledFlagParam" value="-1">
	<input type="hidden" id="isStruct" name="isStruct" value="${isStruct}">
	<input type="hidden" id="isNew" name="isNew" value="${isNew}">
	<input type="hidden" id="userId" name="userId" value="${userId}">
	<input type="hidden" id="stateKey" name="stateKey" value="${stateKey}">
	<input type="hidden" id="formPathInfo" name="formPathInfo"
		value='${formPathInfo}'>
	<input type="hidden" id="formId" name="formId" value="${formId}">
	<input type="hidden" id="formCode" name="formCode" value="${formCode}">
	<input type="hidden" id="formGeneralInfo" name="formGeneralInfo"
		value="">
	<input type="hidden" id="parentId" name="parentId" value="${PARENT_ID}">
	<input type="hidden" id="formCodeSource" name="formCodeSource"
		value="${source}">
	<input type="hidden" id="backUrl" name="backUrl"
		value='../skylineFormWebapp/<%= session.getAttribute("homePage") %>'>
	<input type="hidden" id="springMessages" name="springMessages"
		value='${springMessages}'>
	<input type="hidden" id="waitMessageCounter" name="waitMessageCounter"
		value="0">
	<input type="hidden" id="lastChangeUserId" name="lastChangeUserId"
		value="${lastChangeUserId}">
	<input type="hidden" id="lastChangeDate" name="lastChangeDate"
		value="${lastChangeDate}">
	<input type="hidden" id="permissionsAccess" name="permissionsAccess"
		value="${PERMISSION_ACCESS}">
	<form id="doBackForm" action="doBack.request" method="post">
		<input type="hidden" id="formCode_request" name="formCode_request"
			value="${formCode}"><input type="hidden"
			id="stateKey_request" name="stateKey_request" value="${stateKey}"><input
			type="hidden" id="formCode_doBack" name="formCode_doBack">
	</form>
	<%@ include file="../include/includeBodyJS.jsp"%>
	<!-- body -->
	<table style="width: 100%;">
		<tr>
			<td class="top-bar-container">
				<div style="width: 100%;">
					<%@ include file="./PageHeaderJsoTemplateForm.inc"%>
					<%@ include file="../include/includeMenu.jsp"%>
				</div>
			</td>
		</tr>
		<tr>
			<td class="ui-widget-content ui-corner-all innerTD" colspan="10">
				<table class="displayOnLoad" width="100%" style="display: none;">
					<tr>
						<td class="innerTD">
						    <div class ="main-data-div">
								<!--begin -->
								<button type="button" perm_attr="cu" class="button" id="save_"
									onclick="doSave('Reload')">
									<spring:message code="Save" text="Save" />
								</button>
								<button id="close_back" class="button" type="button" 
									onclick="parent.$('#prevDialog').dialog('close');">
									<spring:message code="Close" text="Close" />
								</button>
								<div class ="div-table-wrapper">
									<table>
										<tr>
											<td style="width: 15%;">${bookmark11}</td>
											<td style="width: 35%;">${bookmark12}</td>
											<td style="width: 15%;">${bookmark13}</td>
											<td style="width: 35%;">${bookmark14}</td>
										</tr>
										<tr>
											<td class="td-place-holder"></td>
										</tr>
										<tr>
											<td>${bookmark21}</td>
											<td>${bookmark22}</td>
											<td>${bookmark23}</td>
											<td>${bookmark24}</td>
										</tr>
										<tr>
											<td class="td-place-holder"></td>
										</tr>
										<tr>
											<td>${bookmark31}</td>
											<td>${bookmark32}</td>
											<td>${bookmark33}</td>
											<td>${bookmark34}</td>
										</tr>
										<tr>
											<td class="td-place-holder"></td>
										</tr>
										<tr>
											<td>${bookmark41}</td>
											<td>${bookmark42}</td>
											<td>${bookmark43}</td>
											<td>${bookmark44}</td>
										</tr>
										<tr>
											<td class="td-place-holder"></td>
										</tr>
										<tr>
											<td>${bookmark51}</td>
											<td>${bookmark52}</td>
											<td>${bookmark53}</td>
											<td>${bookmark54}</td>
										</tr>
										<tr>
											<td class="td-place-holder"></td>
										</tr>
										<tr>
											<td>${bookmark61}</td>
											<td>${bookmark62}</td>
											<td>${bookmark63}</td>
											<td>${bookmark64}</td>
										</tr>
										<tr>
											<td class="td-place-holder"></td>
										</tr>
										<tr>
											<td>${bookmark71}</td>
											<td>${bookmark72}</td>
											<td>${bookmark73}</td>
											<td>${bookmark74}</td>
										</tr>
										<tr>
											<td class="td-place-holder"></td>
										</tr>
										<tr>
											<td>${bookmark81}</td>
											<td>${bookmark82}</td>
											<td>${bookmark83}</td>
											<td>${bookmark84}</td>
										</tr>
										<tr>
											<td class="td-place-holder"></td>
										</tr>
										<tr>
											<td>${bookmark91}</td>
											<td>${bookmark92}</td>
											<td>${bookmark93}</td>
											<td>${bookmark94}</td>
										</tr>
										<tr>
											<td class="td-place-holder"></td>
										</tr>
										<tr>
											<td>${bookmark101}</td>
											<td>${bookmark102}</td>
											<td>${bookmark103}</td>
											<td>${bookmark104}</td>
										</tr>
										<tr>
											<td class="td-place-holder"></td>
										</tr>
										<tr>
											<td>${bookmark111}</td>
											<td>${bookmark112}</td>
											<td>${bookmark113}</td>
											<td>${bookmark114}</td>
										</tr>
									</table>
								</div>
							
						 
								<div id="wrapper">
									<div id="div1">
										<div id="edit-item-wrapper-0" class="edit-item-wrapper"
											style="display: none;">
											<input class="edit-item alphanumInputForm" type="text"
												onBlur='updateAppItemName(this);'> <span
												class="fa fa-edit" onclick="editLink(this)"></span> <span
												class="fa fa-remove" onclick="removeLink(this)"></span>
										</div>
										<div id="div-adhoc-marker"></div>
										<div>
											<span id="btnAddLink" class="fa fa-plus" onclick="addLink()">Add
												Item</span>
										</div>
									</div>
									<div id="div2">
										<div class="my-iframe-container">
											<embed class="my-responsive-iframe">
										</div>
									</div>
								</div> <!--end -->
							</div>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</body>
</html>
