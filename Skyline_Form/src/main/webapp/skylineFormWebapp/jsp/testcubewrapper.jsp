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
    border: 1px solid blue;
}
#div1 {
    display: inline-block;
    width:20p%;
    height:100%;
    border: 1px solid red;
    vertical-align: top;
}
#div2 {
    display: inline-block;
    width:80%;
    height:100%;
    border: 1px solid green;
    vertical-align: top;
}

 .my-iframe-container {
  position: relative;
/*   overflow: hidden; */
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
  border: 1px blue solid;
}
.edit-item-wrapper {

 }
 .edit-item {
 	display:inline; /*override block that make icons to move under the text*/
 	width:80%;
 }
</style>
<script>
	$(document).ready(function() { 
		 ${page_TestForm2_Testform2_ready}  ${page_TestForm2_Testform2_ready}  ${bookmark11_ready}  ${bookmark12_ready}  ${bookmark13_ready}  ${bookmark14_ready}  ${bookmark15_ready}  ${bookmark21_ready}  ${bookmark22_ready}  ${bookmark23_ready}  ${bookmark24_ready}  ${bookmark25_ready}  ${bookmark26_ready}  ${bookmark27_ready}  ${bookmark28_ready}  ${bookmark29_ready}  ${bookmark51_ready}  ${bookmark52_ready}  ${bookmark53_ready}  ${bookmark54_ready}  ${bookmark31_ready}  ${bookmark32_ready}  ${bookmark33_ready}  ${bookmark34_ready}  ${bookmark35_ready}  ${bookmark41_ready}  ${bookmark42_ready}  ${bookmark43_ready}  ${bookmark44_ready}  ${bookmark45_ready} 
 
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
	    
	    renderElementAuthorizationImp();
	    //$('.displayOnLoad').css('display','');
	    
	    //!!!!!!!!!!!develop!!!!!!!!!!!!!!!! 
	    // ids -> 367642,367656
	    var arrCubeIds = [ '367642', '367656' ];
	    
	    $.each(arrCubeIds, function( index, value ) {
	    	  //alert( index + ": " + value );
	    	  
	    	  //clone to adhoc section
	    	  var $div = $('.edit-item-wrapper').clone();
		      $('#div-adhoc').html($div);
		      $div.attr('frameId',value);
		      
	    });
	    
// 	    debugger;
	    
	    //show links in disabled mode
	    $('.edit-item-wrapper').each(function() {
	    	 var $div = $( this );
	    	 var frameId = $div.attr('frameId');
	    	 if(frameId != "-1") { // the first one (our template need to stay hidden)
	    		 $div.css("display", "block");
	    	 }
	    }); 
	});
	${page_TestForm2_Testform2_function} 	${page_TestForm2_Testform2_function} 	${bookmark11_function} 	${bookmark12_function} 	${bookmark13_function} 	${bookmark14_function} 	${bookmark15_function} 	${bookmark21_function} 	${bookmark22_function} 	${bookmark23_function} 	${bookmark24_function} 	${bookmark25_function} 	${bookmark26_function} 	${bookmark27_function} 	${bookmark28_function} 	${bookmark29_function} 	${bookmark51_function} 	${bookmark52_function} 	${bookmark53_function} 	${bookmark54_function} 	${bookmark31_function} 	${bookmark32_function} 	${bookmark33_function} 	${bookmark34_function} 	${bookmark35_function} 	${bookmark41_function} 	${bookmark42_function} 	${bookmark43_function} 	${bookmark44_function} 	${bookmark45_function} 

	
	/******************************/
	/* inner funcs */
	/******************************/
	
	function editLink(obj) {
		alert('editLink');
	}
	
	function removeLink(obj) {
		alert('removeLink');
	}
	
	function addLink(obj) {
		alert('addLink');
	}
	
</script>
</head>
 ${page_TestForm2_Testform2_html}  ${page_TestForm2_Testform2_html}  ${bookmark11_html}  ${bookmark12_html}  ${bookmark13_html}  ${bookmark14_html}  ${bookmark15_html}  ${bookmark21_html}  ${bookmark22_html}  ${bookmark23_html}  ${bookmark24_html}  ${bookmark25_html}  ${bookmark26_html}  ${bookmark27_html}  ${bookmark28_html}  ${bookmark29_html}  ${bookmark51_html}  ${bookmark52_html}  ${bookmark53_html}  ${bookmark54_html}  ${bookmark31_html}  ${bookmark32_html}  ${bookmark33_html}  ${bookmark34_html}  ${bookmark35_html}  ${bookmark41_html}  ${bookmark42_html}  ${bookmark43_html}  ${bookmark44_html}  ${bookmark45_html} 
<body style="overflow-y: hidden;">
	<input type="hidden" id="generalDisabledFlagParam" name="generalDisabledFlagParam" value="-1">
	<input type="hidden" id="isStruct" name="isStruct" value="${isStruct}">
	<input type="hidden" id="isNew" name="isNew" value="${isNew}">
	<input type="hidden" id="userId" name="userId" value="${userId}"><input type="hidden" id="stateKey" name="stateKey" value="${stateKey}"><input type="hidden" id="formPathInfo" name="formPathInfo" value='${formPathInfo}'>
	<input type="hidden" id="formId" name="formId" value="${formId}">
	<input type="hidden" id="formCode" name="formCode" value="${formCode}"><input type="hidden" id="formGeneralInfo" name="formGeneralInfo" value="">
	<input type="hidden" id="parentId" name="parentId" value="${PARENT_ID}">
	<input type="hidden" id="formCodeSource" name="formCodeSource" value="${source}">
	<input type="hidden" id="backUrl" name="backUrl" value='../skylineFormWebapp/<%= session.getAttribute("homePage") %>'>
	<input type="hidden" id="springMessages" name="springMessages" value='${springMessages}'>
	<input type="hidden" id="waitMessageCounter" name="waitMessageCounter" value="0">
	<input type="hidden" id="permissionsAccess" name="permissionsAccess" value="${PERMISSION_ACCESS}">
	<form id="doBackForm" action="doBack.request" method="post"><input type="hidden" id="formCode_request" name="formCode_request" value="${formCode}"><input type="hidden" id="stateKey_request" name="stateKey_request" value="${stateKey}"><input type="hidden" id="formCode_doBack" name="formCode_doBack"></form>
	<%@ include file="../include/includeBodyJS.jsp"%>
<!-- body -->
   <table style="width:100%;">
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
							<table class="displayOnLoad" width="100%" style="display:none;">
								<tr>
									<td class="innerTD">
									<!--begin -->
									<div id="wrapper">
									<div id="div1">
								            <div frameId="-1" class="edit-item-wrapper" style="display: none;">
								                <input class="edit-item disabledclass">
								                <span class="fa fa-edit" onclick="editLink(this)"></span>
								            	<span class="fa fa-remove" onclick="removeLink(this)"></span>
								            </div>
								            <div id="div-adhoc">
											</div>
								            <div>
								                <span id="btnAddLink" class="fa fa-plus" onclick="addLink(this)">Add Item</span>
								            </div>
									</div>
    								<div id="div2">
										<div class="my-iframe-container">
											<embed class="my-responsive-iframe" src="init.request?formCode=TestCube&formId=367642&userId=28991&stateKey=1638274980435991&tableType=&PARENT_ID=-1">
										</div>
									</div>
								    </div>
									<!--end -->
									</td>
								</tr>										
							</table>
						</td>
					</tr>
				</table>			
</body>
</html>
