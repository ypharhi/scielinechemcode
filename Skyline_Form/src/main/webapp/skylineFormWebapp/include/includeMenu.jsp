<div id="dropDownMenuBar" class="sub-header" style="width:100vw;text-align: left;float: left;position: relative;">
		<div style="float: left;position: relative;width: 90vw;">
			<%= ((String)session.getAttribute("MAIN_MENU")).replace("@@STATEKEY_HOLDER@@", request.getParameter("stateKey")) %>
		</div>	
		<div id="divIconQRCode" style="float: right;padding-right:17px" class="QR_code">
			<span onclick="openSearchLabelDialog(this);return false;"></span>
		</div>  
</div> 
<%@ include file="./includeBreadcrumb.jsp" %>