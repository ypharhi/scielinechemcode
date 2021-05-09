<script>
	initWaitMessageDiv(); // init Wait Message Div.
	if ($('#springMessages').length > 0) {
		// init getSpringMessage('pleaseWait') (the object that holds the spring messages).
		prop.springMessagesObj = JSON.parse($('#springMessages').val());
	}	
</script>
<form id="_global_FormLabelSection" action="outPutLabel.request" method="post">
	<input type="hidden" id="_pass_labelCode" name="_pass_labelCode" value="">
	<input type="hidden" id="_pass_labelData" name="_pass_labelData" value="">	
	<input type="hidden" id="_global_labelCode" name="_labelCode" value="${PRINT_PARAM_PASSLABELCODE}"> 
	<input type="hidden" id="_global_labelData" name="_labelData" value="${PRINT_PARAM_PASSLABELDATA}">
</form>