/**
 * customer click event
 * 
 * @param customerFunction
 * @param action
 * @returns
 */
function generalBL_generalClickEvent(customerFunction, action) { // customerClickEvent
	if (customerFunction == 'chemDoodleReactionTabEvent') {
		chemDoodleReactionTabEvent(action);
	}  else if (customerFunction == 'chemDoodleReactionTabUp') {
		chemDoodleReactionTabUp(action);
	}  else if (customerFunction == 'deleteAndInsertMaterial') {
		deleteAndInsertMaterial(action);
	}  else if (customerFunction == 'openExpSeriesCreation') {
		openExpSeriesCreation(action);
	} else if (customerFunction == 'removeExpSeriesIndexFunc') {
		removeExpSeriesIndexFunc(action);
	} else if (customerFunction == 'makeAjaxCallEvent') {
		makeAjaxCallEvent(action);
	} else if (customerFunction == 'openSearchMaterial') {
		// openSearchMaterial(action); not in use
	} else if (customerFunction == 'removeFormulant') {
		// removeFormulant(action); not in use
	} else if (customerFunction == 'updateTemplateVersion') {
		updateTemplateVersion(action);
	} else if (customerFunction == 'checkForPlannedTemplateAndUpdate') {
		checkForPlannedTemplateAndUpdate(action);
	} else if (customerFunction == 'sysConfigCalcSetter') {
		sysConfigCalcSetter(action);
	} else if (customerFunction == 'addMaterialToMotherLiquerTable') {
		addMaterialToMotherLiquerTable(action);
	} else if (customerFunction == 'openEnvMonitPrintForm') {
		openEnvMonitPrintForm(action);
	} else if (customerFunction == 'openCoAPrintForm') {
		openCoAPrintForm(action);
	} else if (customerFunction == 'makeWuFractionCallEvent') {
		makeWuFractionCallEvent(action);
	} else if (customerFunction == 'coaReportMaterialSearch') {
		coaReportMaterialSearch(action);
	} else if (customerFunction == 'saveActionAndCreateNewObj') {
		saveActionAndCreateNewObj(action);
	} else if (customerFunction == 'openQuickAction') {
		openQuickAction(action);
	} else if (customerFunction == 'saveActionAndClose') {
		saveActionAndClose(action);
	} else if (customerFunction == 'saveActionAndForward') {
		saveActionAndForward(action);
	} else if (customerFunction == 'saveActionAndClean') {
		saveActionAndClean(action);
	} else if (customerFunction == "updateSelfTestResultWebixElement") {
		updateSelfTestResultWebixElement();
	} else if (customerFunction == "insertValueAfterSearch") {
		insertValueAfterSearch();
	} else if (customerFunction == "CloneExperiment") {
		CloneExperiment();
	} else if (customerFunction == "addStreamTable") {
		addStreamTable();
	} else if (customerFunction == "copyExpFormulationData") {
		copyExpFormulationData();
	} else if (customerFunction == "calculateMassBalanceFields") {
		calculateMassBalanceFields();
	} else if (customerFunction == "openSelectSpecification") {
		openSelectSpecification(action);
	} else if (customerFunction == "exp_imp_Specification") {
		exp_imp_Specification(action);
	} else if (customerFunction == "generateExpAnalysisReport") {
		generateExpAnalysisReport(action);
	} else if (customerFunction == "generateExpReport") {
		generateExpReport(action);
	} else if (customerFunction == "deleteAction") {
		// ab 31032019 info: the function called from FormBuilder in StepFr. For
		// deletion of action in Step(Organic) the func called from
		// elemntDataTable BL.
		deleteAction();
	}else if(customerFunction == "changeNotifMessageState"){
		changeNotifMessageState(action);
	}else if(customerFunction == "saveReport"){
		saveReport(action);
		
	}
	else if(customerFunction == "saveReportDesign"){
		saveReportDesign(action);
		
	}else if(customerFunction == "viewExpAnalysisReport"){
		viewExpAnalysisReport(action);
	}else if(customerFunction == "searchLabel"){
		searchLabel();
	} else if(customerFunction == "openSaveReportDialog"){
		openSaveReportDialog();
	} else if(customerFunction == "openSaveReportDesignDialog"){
		openSaveReportDesignDialog($('#reportDesignExpName').val());

	} else if(customerFunction == "closeSaveReport"){
		closeSaveReport();
	}else if(customerFunction == "searchAll"){
		searchAll(action);
	}else if(customerFunction == "invAdvancedSearch"){
		invAdvancedSearch();
	} else if(customerFunction == "openMaterialDuplicatesForm"){
		openMaterialDuplicatesForm(action);
	}else if(customerFunction == "runUnitTest"){
		runUnitTest();
	}else if(customerFunction == "openMultiStepCopy"){
		openMultiStepCopy();
	}else if(customerFunction == "stopQuery"){
		stopQuery();
	}else if(customerFunction == 'updateSolventTable'){
		updateSolventTable();
	}else if(customerFunction == 'updateProductTable'){
		updateProductTable();
	}else if(customerFunction == 'updateReactantTable'){
		updateReactantTable();
	}else if(customerFunction == 'updateComponentTable'){
		updateComponentTable();	
	}else if (customerFunction == "generateHistoryReport") {
		generateHistoryReport(action);
	}else if(customerFunction == "notifMessageReadAll"){
		notifMessageReadAll();
	}else if(customerFunction == "calcMaterialCasNumber"){
		calcMaterialCasNumber();
	}else if(customerFunction == "createNewSampleFromSelfTest"){
		createNewSampleFromSelfTest();
	}else if (customerFunction == "insertSelfTestSampleAfterSearch") {
		insertSelfTestSampleAfterSearch();
	}else if (customerFunction == "cleanDataEventClick") {
		cleanDataEventClick();
	}/*else if (customerFunction == "importRecipeToFormulantRef") {
		importRecipeToFormulantRef();
	}*/else if (customerFunction == "executeSQLGenerator") {
		executeSQLGenerator();
	}else if(customerFunction == "onLoadExpAnalysisReportDesign"){
		onLoadExpAnalysisReportDesign();
	}
	else if(customerFunction == "onLoadExpAnalysisReportDesignForEdit"){ // not in use
		onLoadExpAnalysisReportDesignForEdit();
	}
	else if(customerFunction == "openReportDesignScreen"){
		openReportDesignScreen();
	}else if(customerFunction == "openCopyStepsDialog"){
		openCopyStepDesignDialog();
	}else if(customerFunction == "copyStepsDesignToAll"){
		copyStepsDesignToAll();
	}else if(customerFunction == "deleteRun") {
		deleteRun();
	}else if(customerFunction == "calculateRunsPlanning") {
		calculateRunsPlanning();
	}else if(customerFunction == "createRun") {
		createRun();
	} else if(customerFunction == "checkStepStatusAndCreateRun") {
		checkStepStatusAndCreateRun(action);
	}else if(customerFunction == "generateDynamicReport") { // yp 12042020 adama demo report develop
		generateDynamicReport();
	}
    else if(customerFunction == "useDesign") { // yp 12042020 adama demo report develop
	     useDesign();
    }
    else if(customerFunction == "clearDesign") { // yp 12042020 adama demo report develop
	    clearDesign();
    }
    else if(customerFunction == "saveDesign"){
		saveCurrentDesign();
	} else if(customerFunction == "loadScheme"){
		loadScheme();
	} else if(customerFunction == "openLoadDesignPopup"){
		openLoadDesignPopup();
	}
	else if(customerFunction == "openSummaryReport"){
		openSummaryReport(action);
	}
	else if(customerFunction=="openSaveTemplateDialog"){
		openSaveSpreadsheetDialog();
	}
	else if(customerFunction=="viewInSpreadsheet"){
		viewInSpreadsheet();
	}
	else if(customerFunction=="clearSpreadsheetTemplate"){
		clearSpreadsheetTemplate();
	}
	else if(customerFunction=="openHistoricalDataWithConfirm"){
		openHistoricalDataWithConfirm();
	}
	else if(customerFunction=="getHistoricalSpreadData"){
		getHistoricalSpreadData();
	}
	else if(customerFunction=='updateProductList'){
		updateProductList();
	}
	else if(customerFunction=='exportRecipe'){
		exportRecipe();
	}
	else if(customerFunction=='openExperimentGroupPopUp'){
		openExperimentGroupPopUp();
	}
	else if(customerFunction=='renderImportedCompositionTable'){
		renderImportedCompositionTable();
	}
	else if(customerFunction=='copyCompositionToPlanned'){
		copyCompositionToPlanned();
	}
	else if(customerFunction=='checkBalance'){
		checkBalance(action);
	}else if(customerFunction == 'updateInstrumentTable'){
		updateInstrumentTable();
	}
	else if(customerFunction == 'saveAndCreateRequest'){
		saveAndCreateRequest();
	}
	else if(customerFunction == 'updateTestedComponentTable'){
		updateTestedComponentTable();
	}
	
}

function checkBalance(){
	showWaitMessage("Please wait...");
	var allData = getformDataNoCallBack(1);
	var action = "checkBalance";
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + '&userId=' + $('#userId').val()
			+ "&eventAction=" + action + "&isNew=" + $('#isNew').val();
	
	var stringifyToPush = {
			code : "doCheckBalance",
			val : "1",
			type : "AJAX_BEAN",
			info : 'na'
		};
	allData = allData.concat(stringifyToPush);
	
	var stringifyToPush = {
		code : "parentId",
		val : $('#formId').val(),
		type : "AJAX_BEAN",
		info : 'na'
	};
	allData = allData.concat(stringifyToPush);
	
	var data_ = JSON.stringify({
		action : "doSave",
		data : allData,
		errorMsg : ""
	});
	// call...
	$.ajax({
		type : 'POST',
		// async: false,
		data : data_,
		url : "./generalEvent.request" + urlParam ,
		contentType : 'application/json',
		dataType : 'json',
		success : function(obj) {
			hideWaitMessage();
			var warningToDisplay = "";
			var fullObj = funcParseJSONData(obj.data[0].val); 
			if(fullObj!=null && Object.keys(fullObj).length > 0)
			{
				for(key in fullObj)
				{
					var _valObj = fullObj[key];
					if(_valObj.hasOwnProperty("warningMsg"))
					{
						warningToDisplay = _valObj["warningMsg"];
					}
				}
				if(warningToDisplay.length > 0)
				{
					displayAlertDialog(warningToDisplay);
					return false;
				}
			}
			//if(doSave == '0'){
				parent.onElementDataTableApiChange('plannedCompositions');
				var stepIframeList = parent.$('[id*="AsyncIframe_stepIframes_"]');
				for(var i=0;i<stepIframeList.length;i++){
					var stepId = stepIframeList[i].id;
					var iframe = stepIframeList[i];//document.getElementById('AsyncIframe_stepIframes_'+stepId);
					try {
						iframe.contentWindow.onElementDataTableApiChange('products');
					} catch(e) {
						alert(1);
						console.log('error in refresh step product table on creation');
					}
				}
			/*} else {
				if(warningToDisplay==""){//if the difference is invalid then the save process can't continue
					checkNonFamiliarAndSave(doSaveExperimentFr,'Reload');
				}
			}*/
		},
		error : function() {
			hideWaitMessage();
		} 
	});
	return true;
}

function renderImportedCompositionTable() {
	onChangeAjax('compositionParent_id');
//	setFormParamMap('ExperimentFor', $('#formId').val(),"RECIPEFORMULATION_ID",$('#importedRecipe_id').val(),onElementDataTableApiChange('importedRecipe_id'));
}

function copyCompositionToPlanned(){
	var isPlannedcompositionEmpty =$('#plannedCompositions td[class="dataTables_empty"]').length>0? true:false;
	var stepCount = $('[id*="arent_stepIframes_"]').length;
	if(!isPlannedcompositionEmpty || stepCount>0){
		openConfirmDialog({
	        onConfirm: function(){
	        	$('#RECIPEFORMULATION_ID').val($('#importedRecipe_id').val());//copy the basic recipe
	        	cloneImportedToPlannedCompositions($('#compositionParent_id').val(),$('#importedRecipe_id').val());
	        },
	        title: 'Warning',
	        message: getSpringMessage('COPY_IMPORTEDCOMPOSITIONS_ALERT')
	    });
	} else {
		$('#RECIPEFORMULATION_ID').val($('#importedRecipe_id').val());//copy the basic recipe
		cloneImportedToPlannedCompositions($('#compositionParent_id').val(),$('#importedRecipe_id').val());		
	}
}

function cloneImportedToPlannedCompositions(compositionParent_id,importedRecipe_id){
	showWaitMessage("Please wait...");
	var allData = getformDataNoCallBack(1);
	var action = "cloneImportedToPlannedCompositions"
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + '&userId=' + $('#userId').val()
			+ "&eventAction=" + action + "&isNew=" + $('#isNew').val();
	
	var data_ = JSON.stringify({
		action : "doSave",
		data : allData,
		errorMsg : ""
	});

	// call...
	$.ajax({
		type : 'POST',
		// async: false,
		data : data_,
		url : "./generalEvent.request" + urlParam ,
		contentType : 'application/json',
		dataType : 'json',
		success : function(obj) {
			hideWaitMessage();
			var returnVal = JSON.parse(obj.data[0].val);
			var formulationtype_id = returnVal.FORMULATIONTYPE_ID;
			var stepListToDelete = returnVal.stepListToDelete;
			var density = returnVal.density;
			var externalCode = returnVal.externalCode;
			var batchSize = returnVal.batchSize;
			var BATCHSIZE_UOM = returnVal.BATCHSIZE_UOM;
			$('#FORMULATIONTYPE_ID').val(formulationtype_id).trigger("chosen:updated");
			$('#density').val(density);
			$('#density').attr('realvalue',density);
			$('#batchSize').attr('oldvalue','');
			$('#batchSize').val(batchSize);
			$('#batchSize').attr('realvalue',batchSize);
			if(BATCHSIZE_UOM!=""){
				$('#BATCHSIZE_UOM').val(BATCHSIZE_UOM).trigger('chosen:updated');
			}
			$('#externalCode').val(externalCode);
			onChangeAjax('FORMULATIONTYPE_ID');//EXECUTE the changing operation ,so that the composition type will get the right value
			onElementDataTableApiChange('plannedCompositions');
			if(batchSize!=''){
				$('#batchSize').blur();
			}
			for(var i=0;i<stepListToDelete.length;i++){
				var stepId = stepListToDelete[i];
				$('#parent_stepIframes_' + stepId).remove();
			}
		},
		error : function() {
			hideWaitMessage();
		} 
	});
}

function getRecipeAndShowData(){
	showWaitMessage("Please wait...");
	var allData = getformDataNoCallBack(1);
	var action = "get"
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + '&userId=' + $('#userId').val()
			+ "&eventAction=" + action + "&isNew=" + $('#isNew').val();
	
	var data_ = JSON.stringify({
		action : "doSave",
		data : allData,
		errorMsg : ""
	});

	// call...
	$.ajax({
		type : 'POST',
		// async: false,
		data : data_,
		url : "./generalEvent.request" + urlParam ,
		contentType : 'application/json',
		dataType : 'json',
		success : function(obj) {
			hideWaitMessage();
			onElementDataTableApiChange('AnalytMethods');
			$('#AnalytMethods_dataTableStructButtons button.dataTableApiAdd').removeClass('disabledclass'); 
			$('#AnalytMethods_dataTableStructButtons button.dataTableApiNew').removeClass('disabledclass');
			onElementDataTableApiChange('columns');
			onElementDataTableApiChange('instruments');
			onElementDataTableApiChange('chromatograms');
			//onElementDataTableApiChange('samples'); //sample should not be cleared
		},
		error : function() {
			hideWaitMessage();
			displayAlertDialog(getSpringMessage('Clean Failed'));
		} 
	});
}

function exportRecipe(){
	//TODO:check what should be done here
	openConfirmDialog({
        onConfirm: function(){
        	$('#exportToDataBank').val('1');
        	$('#STATUS_ID').val($('#STATUS_ID option:contains("Approved")'));
        	$('#STATUS_ID').trigger('chosen:updated');
        	doSave('Reload');
        	/*if(changeBy != undefined && changeDate!= undefined){
				$('#lastChangeUserId').val(changeBy);
				$('#lastChangeDate').val(changeDate);
			}*/
        },
        title: 'Warning',
        message: getSpringMessage('Are you sure?'),
        onCancel: function(){}//lastChangeUserId
    });
	
}
function openExperimentGroupPopUp(formId){
	try {
		var formCode = 'ExperimentGroup';
		var dialogHeight =400;
		var dialogWidth = 750;
		var form_id='-1';
		var isNew='1'
		if(formId!=null&&formId!=undefined&&formId!=""){
			form_id=formId;
			isNew='0';
		}
		var page = "./init.request?stateKey=" + $('#stateKey').val()
				+ "&formCode=" + formCode + "&formId=" + form_id + "&userId="
				+ $('#userId').val()+ "&parentId=" + $('#formId').val()+"&isNew="
					+ isNew;
		// open iframe inside dialog
		var $dialog = $(
				'<div id="prevDialog" style="overflow-y: hidden;""></div>')
				.html(
						'<iframe id="formIframeId" style="border: 0px;width:100%;height:100%" src="'
								+ page /* + urlPermissionDisabled */
								+ '"></iframe>').dialog({
					autoOpen : false,
					modal : true,
					height : dialogHeight,
					width : dialogWidth,
					close : function() {
						if($('#prevDialog').data("experimentGroupDesc")!= undefined&&$('#prevDialog').data("experimentGroupDesc")!=""){
							if($('#formCode').val()!='Project'){
								updateExperimentGroupDDL();
								}
							else{
								  onElementDataTableApiChange('groups');
							}
						
						}
						$('#prevDialog iframe').attr('src', 'about:blank');
					      $('#prevDialog').remove();
						$('#prevDialog').dialog('close');
						// $('#EXPERIMENTGROUP_ID').trigger('chosen:updated');
					}
				});
		$dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
	} catch (e) {
		
	}
	
}
function updateProductList(){
	var productId = $('#productId').val();
	var productName = $('#productName').val();
	var selectedOptionElem = $('#PRODUCT_ID option[value = "'+productId+'"]');
	if(selectedOptionElem.length>0){
		$('#PRODUCT_ID').val(productId).trigger("chosen:updated");
	} else {
		$('#PRODUCT_ID').append($('<option>', {
		    value: productId,
		    text: productName
		}).attr("selected","selected")).trigger("chosen:updated");
	}
}

function getHistoricalSpreadData(){
	var mandatoryIndicator= isMandatoryFieldsRequired();
    if ((mandatoryIndicator.setRequired == '1'&& checkRequired()) || (mandatoryIndicator.setRequired == '0' && checkRequiredByList(mandatoryIndicator.mandatoryList))) {
        if (!checkDateMinMaxValidity() || !checkNumberMinMaxValidity() || !checkTimeValidity() || !checkEmailValidity() 
        		|| !elementRichTextEditorValidation()
            ) 
        {
        		prop.onChangeAjaxFlag = false;
        		return;
        } 
    }
    onElementDataTableApiChange('historicalDataTable', null,null,true);
	//setFormParamMap("HistoricalData", "-1","fromDate",$('#fromDate').val());
	//setFormParamMap("HistoricalData", "-1","toDate",$('#toDate').val(),function(){onElementDataTableApiChange('historicalDataTable', null,null,true);});
}

function openHistoricalDataWithConfirm(){
	//confirmWithOutSaveWithPermissions(openHistoricalData, ["-1","HistoricalData"], ["HistoricalData","-1"]);
	
	var dialogWidth = $(window).width()*0.9;
	var dialogHeight = $(window).height()*0.9;
	var parentId = $('#formId').val();
	var href = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=HistoricalData&formId=-1&userId=" + $('#userId').val() + '&PARENT_ID=' + $('#formId').val();
	var $dialog = $('<div id="prevDialog" style="overflow-y: hidden;""></div>')
		.html(
				'<iframe style="border: 0px;width:100%;height:100%" src="'
						+ href + '"></iframe>').dialog({
			autoOpen : false,
			modal : true,
			height : dialogHeight,
			width : dialogWidth,
			/*height : 'auto',
			width : 'auto',
			resizable: true,
			minHeight:125,
            minWidth:520,
            maxWidth:1000,
            maxHeight:1000,*/
			// title: title,
			close : function() {
				$('#prevDialog iframe').attr('src', 'about:blank');
				$('#prevDialog').remove();
			}
		});
	
	$dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
}

function openHistoricalData(paramsArr){
	_isTableRowLinkClicked = true;
	var formId = paramsArr[0]; 
	var formCode = paramsArr[1];
	var tab = '';
	if(paramsArr.length > 2) {
		tab = paramsArr[2];
	}
	var hideMsg = false;
	if(paramsArr.length > 3) {
		hideMsg = paramsArr[3];
	} 
	
	if(!hideMsg) {
		showWaitMessage(getSpringMessage('Loading...'));
	}
	var openMode = "self";
	if(paramsArr.length > 4) {
		openMode = paramsArr[4];
	} 
	
	console.log('smartLink() paramsArr: '+paramsArr);
	var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=" + formCode + "&formId=" + formId + "&userId=" + $('#userId').val() + '&PARENT_ID=' + $('#formId').val() + '&formTab=' + tab;	
	
	if(openMode == "self")
	{
		if(window.self !== window.top) {    
			window.top.location.href = page;  
			return;
		}
		fgReloadForm(page);//window.location.href = page;
	}
	else //new tab
	{
		openNewTab(page);
	}
}

function loadScheme(){
	var dialogWidth = $(window).width() - 10;
	var dialogHeight = $(window).height();
	var parentId = $('#formId').val();
	var href = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=ExpAnalyReportPop&formId=-1&userId=" + $('#userId').val() + '&PARENT_ID=' + $('#formId').val();
	var $dialog = $('<div id="prevDialog" style="overflow-y: hidden;""></div>')
		.html(
				'<iframe style="border: 0px;width:100%;height:100%" src="'
						+ href + '"></iframe>').dialog({
			autoOpen : false,
			modal : true,
			height : dialogHeight,
			width : dialogWidth,
			// title: title,
			close : function() {
				$('#prevDialog iframe').attr('src', 'about:blank');
				var page = $(this).data(
						'navigationPage');
				$('#prevDialog').remove();
				if (typeof page !== 'undefined') {
					showWaitMessage("Please wait...");
					window.location = page;
				}
			}
		});
	
	$dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
	//confirmWithOutSaveMainMenu(href);
}

function cleanDataEventClick() {
	//***** SelfTest
	if($('#formCode').val() == 'SelfTest') {
		openConfirmDialog({
	        onConfirm: function(){
	        	cleanDataEventClickSelfTest();
	        },
	        title: 'Warning',
	        message: getSpringMessage('confirmSelfTestClearData'),
	        onCancel: function(){}
	    });
	}
	
	//***** request
	if($('#formCode').val() == 'Request') {
		openConfirmDialog({
	        onConfirm: function(){
	        	cleanDataEventClickRequest();
	        },
	        title: 'Warning',
	        message: getSpringMessage('confirmRequestClearData'),
	        onCancel: function(){}
	    });
	}
}

function cleanDataEventClickSelfTest() {
	clearWebixTableData([], 'tableID_webixAnalyticalSelfTest');
	addNewEmptyRowToWebixTable('tableID_webixAnalyticalSelfTest');
	
	var action = 'cleanDataEventClick';
	showWaitMessage("Please wait...");
	prop.onChangeAjaxFlag = false; 
	var allData = getformDataNoCallBack(1);
	
	clearRichTextContent($('#summary')); //clearSummary richtext field
	clearRichTextContent($('#description')); //clearSummary richtext field
	$('#instrumentExt').val('');

	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + '&userId=' + $('#userId').val()
			+ "&eventAction=" + action + "&isNew=" + $('#isNew').val();
	
	var data_ = JSON.stringify({
		action : "doSave",
		data : allData,
		errorMsg : ""
	});

	// call...
	$.ajax({
		type : 'POST',
		// async: false,
		data : data_,
		url : "./generalEvent.request" + urlParam ,
		contentType : 'application/json',
		dataType : 'json',
		success : function(obj) {
			hideWaitMessage();
			onElementDataTableApiChange('AnalytMethods');
			$('#AnalytMethods_dataTableStructButtons button.dataTableApiAdd').removeClass('disabledclass'); 
			$('#AnalytMethods_dataTableStructButtons button.dataTableApiNew').removeClass('disabledclass');
			onElementDataTableApiChange('columns');
			onElementDataTableApiChange('instruments');
			onElementDataTableApiChange('chromatograms');
			//onElementDataTableApiChange('samples'); //sample should not be cleared
		},
		error : function() {
			hideWaitMessage();
			displayAlertDialog(getSpringMessage('Clean Failed'));
		} 
	});
}

function cleanDataEventClickRequest() {
	var action = 'cleanDataEventClickRequest';//onChangeAjax
	showWaitMessage("Please wait...");
	prop.onChangeAjaxFlag = false; 
	var allData = getformDataNoCallBack(1);
	
	clearRichTextContent($('#testPurpose')); //clear richtext field
    
	$('#DESTSITE_ID').val('');
	$("#DESTSITE_ID").trigger('chosen:updated');
	$('#DESTUNIT_ID').val('');
	$("#DESTUNIT_ID").trigger('chosen:updated');
	$('#DESTLAB_ID').val('');
	$("#DESTLAB_ID").trigger('chosen:updated');
	$('#REQUESTTYPE_ID').val('');
	$("#REQUESTTYPE_ID").trigger('chosen:updated');
	onChangeAjax('DESTSITE_ID');
	
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + '&userId=' + $('#userId').val()
			+ "&eventAction=" + action + "&isNew=" + $('#isNew').val();
	
	var data_ = JSON.stringify({
		action : "doSave",
		data : allData,
		errorMsg : ""
	});

	// call...
	$.ajax({
		type : 'POST',
		// async: false,
		data : data_,
		url : "./generalEvent.request" + urlParam ,
		contentType : 'application/json',
		dataType : 'json',
		success : function(obj) {
			hideWaitMessage();
			onElementDataTableApiChange('groups');
			onElementDataTableApiChange('users');
			onElementDataTableApiChange('operartinTypeTable');
		},
		error : function() {
			hideWaitMessage();
			displayAlertDialog(getSpringMessage('Clean Failed'));
		} 
	});
}

function runUnitTest(){
	var action = 'runUnitTest';
	var quantityOfRuns = 1;
	showWaitMessage("Please wait...");
	prop.onChangeAjaxFlag = false;
	if($('#gQuantityOfRuns').val() != "") {
		quantityOfRuns = $('#gQuantityOfRuns').val()
	}
	
	var allData = [{
		code : 'quantityOfRuns',
		val : quantityOfRuns,
		type : "AJAX_BEAN",
		info : 'na'
	}];
	
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + '&userId=' + $('#userId').val()
			+ "&eventAction=" + action + "&isNew=" + $('#isNew').val();// +
																		// "&gQuantityOfRuns="
																		// +
																		// $('#gQuantityOfRuns').val()

	var data_ = JSON.stringify({
		action : "doSave",
		data : allData,
		errorMsg : ""
	});
	
	// call...
	$.ajax({
		type : 'POST',
		// async: false,
		data : data_,
		url : "./generalEvent.request" + urlParam ,
		contentType : 'application/json',
		dataType : 'json',
		success : function(obj) {
			hideWaitMessage();
			displayAlertDialog("For get rusults of the Test please refresh the screen");
		},
		error : function() {
			hideWaitMessage();
			displayAlertDialog(getSpringMessage('Copy Failed'));
		}
	// handleAjaxError
	});
}	

function searchLabel(){
	var formId =$("#SeachLabelName").val().trim();
	formCode = getFormCodeBySeqId(formId);
	// show the Loading... label with fade
	$("font[color=red]").css('display', 'block');
	$("font[color=red]").fadeOut(3000);
	if((formCode == null || formCode == "") || (formId == null || formId == ""))
	{
		displayAlertDialog("Not found");
	}else{
		// get form type=> navigate to struct and invitem only

		var allData = [{
				code : 'formCode',
				val : formCode,
				type : "AJAX_BEAN",
				info : 'na'
			}];
		// url call
		var urlParam = "./getFormType.request?formCode="+formCode;

		var data_ = JSON.stringify({
			action : "getFormType",
			data : allData,
			errorMsg : ""
		});

		//showWaitMessage();
		// call...
		$.ajax({
			type : 'POST',
			data : data_,
			url :  urlParam ,
			contentType : 'application/json',
			dataType : 'json',

			success : function(obj) {
				var isStruct = (obj.data[0].val == 'STRUCT' || obj.data[0].val =='INVITEM');// obj.data[0].val
				if(!isStruct){
					displayAlertDialog('Navigation has been stopped since the expected form is not a struct');
				} else {
					checkAndNavigate([formId ,formCode,'','false',true]);
				}
				//hideWaitMessage();
			},
			error : handleAjaxError
		});
	}
}

function viewExpAnalysisReport(){
	var selectedId = "";
	var selectedTable = $('#expAnReportTable').DataTable();
	var custid = selectedTable.row('.selected').data();
	if (typeof custid !== 'undefined') {
		selectedId = custid[0];
	}
	// ajax- open new window of the selected save_name_id report
	var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=ExpAnalysisReport&formId=-1" + "&userId=" + $('#userId').val() + '&PARENT_ID=' + $('#formId').val() + "&nameId=" + selectedId;
	if (window.self === window.top){   
		window.location = page;
	} else {
		parent.$("#prevDialog iframe").attr('src',
			'about:blank');
		parent.$('#prevDialog').data('navigationPage',
				page);
		parent.$('#prevDialog').dialog('close');
	}
}

function saveReport(action){
	// if(action=='save'){
	var nameId = parent.$('#nameId').val();
	reportName = $('#reportName').val();
	reportDescription = $('#description').val();
	parentFormCode =  parent.$('#prevDialog').data('PARENT_FORMCODE');
	validateOtherReportSameNameExists(nameId,reportName,reportDescription,parentFormCode);
		
		/*
		 * parent.$('#prevDialog').data('reportName', reportName);
		 * parent.$('#prevDialog').data('reportDescription', reportDescription);
		 * //} else if(action =='save_as'){ //error- report name already exists
		 * //} parent.$("#prevDialog iframe").attr('src','about:blank');
		 * parent.$('#prevDialog').dialog('close'); return;
		 */
}


function saveActionAndClean(action) {
	doSaveAction('saveQuickAction', 'clean');
}

function saveActionAndClose(action) {
	doSaveAction('saveQuickAction', 'save_and_close');
}

function saveActionAndForward(action) {
	doSaveAction('saveQuickAction', 'save_and_forward');
}

function saveActionAndCreateNewObj(nextFormCode) {
	doSaveAction('saveQuickAction', 'save_and_new', nextFormCode);
}

function doSaveAction(action, afterSave, nextFormCode) {

	var mandatoryIndicator = isMandatoryFieldsRequired();
	if ((mandatoryIndicator.setRequired == '1' && checkRequired())
			|| (mandatoryIndicator.setRequired == '0' && checkRequiredByList(mandatoryIndicator.mandatoryList))) {
		if (!checkDateMinMaxValidity() || !checkNumberMinMaxValidity()
				|| !checkTimeValidity() || !checkEmailValidity()
				|| !elementDynamicParamsImpValidation()
				|| !elementRichTextEditorValidation()
				|| !elementWebixValidation()) {
			prop.onChangeAjaxFlag = false;
			return;
		}
		showWaitMessage("Please wait...");
		prop.onChangeAjaxFlag = false;

		var stringifyToPush = {
			code : "newFormCode",
			val : afterSave == 'save_and_new'
					&& (typeof nextFormCode !== 'undefined') ? nextFormCode
					: '',
			type : "AJAX_BEAN",
			info : 'na'
		};

		// get all data and add removeIndexId
		var allData = getformDataNoCallBack(1);
		var allData = allData.concat(stringifyToPush);

		// url call
		var urlParam = "?formId=" + $('#formId').val() + "&formCode="
				+ $('#formCode').val() + "&userId=" + $('#userId').val()
				+ "&eventAction=" + action + "&isNew=" + $('#isNew').val();

		var data_ = JSON.stringify({
			action : "doSave",
			data : allData,
			errorMsg : ""
		});

		// call...
		$
				.ajax({
					type : 'POST',
					data : data_,
					url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
					contentType : 'application/json',
					dataType : 'json',

					success : function(obj) {
						if (obj.errorMsg != null && obj.errorMsg != '') {
							displayAlertDialog(obj.errorMsg);
							hideWaitMessage();
						} else if (obj.data[0].val == "-1") {
							displayAlertDialog(getSpringMessage('updateFailed'));
							hideWaitMessage();
						} else if (obj.data[0].val.toString().indexOf(',') != '-1'
								&& obj.data[0].val.toString().substring(0, 2) == '-2') {
							doSaveMessage = getSpringMessage(obj.data[0].val
									.split(',')[1]);/*
													 * .split("_").join("
													 * ").toLowerCase();
													 */
							displayAlertDialog(doSaveMessage/*
															 * .charAt(0).toUpperCase() +
															 * doSaveMessage.slice(1)
															 */
									+ " "
									+ getSpringMessage('alreadyExistsInSystem'));// ab
																					// 22/03/18
																					// fixed
																					// bug
																					// 4161
							hideWaitMessage();
						} else if (obj.data[0].val.toString().indexOf(',') != '-1'
								&& obj.data[0].val.toString().substring(0, 2) == '-3') {
							doSaveMessage = obj.data[0].val.split(',')[1]
									.split("_").join(" ").toLowerCase();
							displayAlertDialog(doSaveMessage.charAt(0)
									.toUpperCase()
									+ doSaveMessage.slice(1)
									+ " "
									+ getSpringMessage('invalidInSystem'));
							hideWaitMessage();
						} else if (obj.data[0].val.toString().indexOf(',') != '-1'
								&& obj.data[0].val.toString().indexOf("WF") !== -1) {
							doSaveMessage = obj.data[0].val.split(',')[1];
							var warningCode = obj.data[0].val.split(',')[0];
							displayAlertDialog("<\span><\i  onclick=\"customInfoClickEvent('getWFStatusInfo','STEPS_WF_LIST_INFO','"
									+ warningCode.split('_')[2]
									+ "')\""
									+ " style=\"cursor: pointer;margin-right: 5px;\" title=\"WF Info\" class=\"fa fa-info\"><\/i><\/span>\n"
									+ doSaveMessage.charAt(0).toUpperCase()
									+ doSaveMessage.slice(1));
							hideWaitMessage();
						} else if (obj.data[0].val.toString().indexOf(',') != '-1') {
							doSaveMessage = obj.data[0].val.split(',')[1];
							displayAlertDialog(doSaveMessage.charAt(0)
									.toUpperCase()
									+ doSaveMessage.slice(1));
							hideWaitMessage();
						} else {
							if ((typeof afterSave !== 'undefined')
									&& (afterSave.toLowerCase() == "save_and_close")) {
								parent.$("#prevDialog iframe").attr('src',
										'about:blank');
								parent.$('#prevDialog').dialog('close');
								return;
							} else if ((typeof afterSave !== 'undefined')
									&& (afterSave.toLowerCase() == "save_and_forward")) {// in
																							// case
																							// of
																							// clicking
																							// on
																							// 'Save
																							// and
																							// Open'
								var formCode = "Action";
								var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode="
										+ formCode + "&formId="
										+ obj.data[0].val + "&userId="
										+ $('#userId').val() + "&PARENT_ID="
										+ $('#parentId').val();

								// window.location = page;
								parent.$("#prevDialog iframe").attr('src',
										'about:blank');
								parent.$('#prevDialog').data('navigationPage',
										page);
								parent.$('#prevDialog').dialog('close');
								return;
							} else if ((typeof afterSave !== 'undefined')
									&& (afterSave.toLowerCase() == "save_and_new")) {// in
																						// case
																						// of
																						// clicking
																						// on
																						// 'Sample'/Request/Selftest
																						// button
								formCode = onNewButtonIntegration((typeof nextFormCode !== 'undefined') ? nextFormCode
										: "Action"); // change formCode name
														// if needed
								var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode="
										+ formCode + "&formId=-1" + "&userId="
										+ $('#userId').val() + '&PARENT_ID='
										+ obj.data[0].val;
								// window.location = page;
								parent.$("#prevDialog iframe").attr('src',
										'about:blank');
								parent.$('#prevDialog').data('navigationPage',
										page);
								parent.$('#prevDialog').dialog('close');
								return;
							} else if ((typeof afterSave !== 'undefined')
									&& (afterSave.toLowerCase() == "reload")) {
								fgReloadForm(); // window.location.href =
												// location;
								return;
							} else if ((typeof afterSave !== 'undefined')
									&& (afterSave.toLowerCase() == 'clean')) {
								showWaitMessage(getSpringMessage('pleaseWait'));
								clearRichTextContent($('#observation'));
								$('#actionName').val('');
								var d = new Date();
								var hh = d.getHours();
								hh = hh < 10 ? '0' + hh : hh;
								var min = d.getMinutes();
								min = min < 10 ? '0' + min : min;
								// var currentTime =
								// d.toLocaleTimeString().match(/\d{2}:\d{2}|\d:\d{2}/)[0];
								// if(new RegExp(/^\d:\d{2}/).test(currentTime){
								// currentTime = '0'+currentTime;
								// }
								/*
								 * ('#time') .val(hh + ":" + min/* new
								 * RegExp(/^\d:\d{2}/).test(d.toLocaleTimeString().match(/\d{2}:\d{2}|\d:\d{2}/)[0])?'0'+d.toLocaleTimeString().match(/\d{2}:\d{2}|\d:\d{2}/)[0]:d.toLocaleTimeString().match(/\d{2}:\d{2}|\d:\d{2}/)[0]
								 */// );
								$('#time')
								.val('');
								hideWaitMessage();
								displayAlertDialog(getSpringMessage('Action was created successfully'));
							} else {
								displayAlertDialog(getSpringMessage('updateSuccessfully'));
							}
							hideWaitMessage();
						}
					},
					error : handleAjaxError
				});
	} else {
		if (mandatoryIndicator.setRequired == '1') {
			displayAlertDialog(getSpringMessage('PleaseFillTheRequiredFields'));
		} else {
			displayAlertDialog(getSpringMessage(mandatoryIndicator.message));
		}

	}
}

function exp_imp_Specification(action) {

	var smartSelectList = "";
	var toReturn = [];
	$('input[class="dataTableApiSelectInfo"]:checked').each(function(index) {
		toReturn.push($(this).val());
	});
	smartSelectList = toReturn.toString();
	var stringifyToPush = {
		code : 'smartSelectList',
		val : smartSelectList,
		type : "AJAX_BEAN",
		info : 'na'
	};

	// get all data and add removeIndexId
	var allData = getformDataNoCallBack(1);
	var allData = allData.concat(stringifyToPush);

	// url call
	var urlParam = "?formId="
			+ $('#formId').val()
			+ "&formCode="
			+ $('#formCode').val()
			+ "&userId="
			+ $('#userId').val()
			+ "&eventAction="
			+ (action == 'export' ? 'exportSpecification'
					: 'importSpecification') + "&tableType=" + action
			+ "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "doSave",
		data : allData,
		errorMsg : ""
	});

	// call...
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',

		success : function(obj) {
			if (obj.errorMsg != null && obj.errorMsg != '') {
				displayAlertDialog(obj.errorMsg);
			} else if ((obj.data[0].val == "1")) {
				displayAlertDialog(getSpringMessage(action
						+ " was done successfully!"));
			} else if (obj.data[0].val == "0") {
				displayAlertDialog(getSpringMessage(action + " failed!"));
			}

			hideWaitMessage();
		},
		error : handleAjaxError
	});
}

function openQuickAction(action) {
	// validates it is possible to create action according to the WF
	$.ajax({
				type : 'POST',
				data : '{"action" : "getNewAvailableFormList","' + 'data":['
						+ '{"code":"formCode","val":"' + $('#formCode').val()
						+ '"},' + '{"code":"formId","val":"'
						+ $('#formId').val() + '"},' + '{"code":"stateKey","val":"'
						+ $('#stateKey').val() + '"}' + '],' + '"errorMsg":""}',
				url : "./getNewAvailableFormList.request",
				contentType : 'application/json',
				dataType : 'json',
				success : function(obj) {
					var canNewByList = false;
					if (obj.errorMsg != null && obj.errorMsg != '') {
						displayAlertDialog(obj.errorMsg);
					} else if ((obj.data[0].val == "-1")
							|| (obj.data[0].val == "")) {

					} else {
						optionsArray = obj.data[0].val.split(',');
						for (i = 0; i < optionsArray.length; i++) {
							if (!canNewByList && optionsArray[i] == "Action") {
								canNewByList = true;
							}
						}
					}
					if (!canNewByList) {
						displayAlertDialog("<\span><\i  onclick=\"customInfoClickEvent('getWFStatusInfo','STEPS_WF_LIST_INFO')\""
								+ " style=\"cursor: pointer;margin-right: 5px;\" title=\"WF Info\" class=\"fa fa-info\"><\/i><\/span>\n"
								+ "Create new Action is not allowed");
					} else {// action is in the available new formCodes=> open
							// quickAction popup
						var dialogWidth = ($(window).width() - 10) * 0.5;
						var dialogHeight = ($(window).height());
						var parentId = $('#formId').val();
						var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=QuickAction&formId=-1&userId="
								+ $('#userId').val()
								+ '&tableType=&PARENT_ID='
								+ parentId;

						// open iframe inside dialog
						var $dialog = $(
								'<div id="prevDialog" style="overflow-y: hidden;""></div>')
								.html(
										'<iframe style="border: 0px;width:100%;height:100%" src="'
												+ page + '"></iframe>')
								.dialog(
										{
											autoOpen : false,
											modal : true,
											height : dialogHeight,
											width : dialogWidth,
											// title: title,
											close : function() {
												$('#prevDialog iframe').attr(
														'src', 'about:blank');
												var page = $(this).data(
														'navigationPage');
												$('#prevDialog').remove();
												if (typeof page !== 'undefined') {
													showWaitMessage("Please wait...");
													window.location = page;
												} else {
													onElementDataTableApiChange('action');// refresh
																							// the
																							// table
												}

											}
										});

						$dialog.dialog('option', 'dialogClass', 'noTitleStuff')
								.dialog('open');
					}
				},
				error : handleAjaxError
			});
}

function openSelectSpecification(action) {
	var dialogWidth = $(window).width() - 10;
	var dialogHeight = $(window).height();
	var parentId = $('#formId').val();
	var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=ExpImpSpec&formId=-1&userId="
			+ $('#userId').val() + '&tableType=' + action + '&PARENT_ID='
			+ parentId;

	// open iframe inside dialog
	var $dialog = $('<div id="prevDialog" style="overflow-y: hidden;""></div>')
			.html(
					'<iframe style="border: 0px;width:100%;height:100%" src="'
							+ page + '"></iframe>').dialog({
				autoOpen : false,
				modal : true,
				height : dialogHeight,
				width : dialogWidth,
				// title: title,
				close : function() {
					$('#prevDialog iframe').attr('src', 'about:blank');
					$('#prevDialog').remove();
					onElementDataTableApiChange('spacifications');// refresh
																	// the table

				}
			});

	$dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
}

function openExpSeriesCreation(action) {
	// var popupSize = $('[id="' + domId + '_popupSize"]').val();
	var dialogWidth = $(window).width() - 10;
	var dialogHeight = $(window).height();
	var parentId = $('#formId').val();
	var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=ExpSeriesCreation&formId=-1&userId="
			+ $('#userId').val() + '&tableType=&PARENT_ID=' + parentId;

	// open iframe inside dialog
	var $dialog = $('<div id="prevDialog" style="overflow-y: hidden;""></div>')
			.html(
					'<iframe style="border: 0px;width:100%;height:100%" src="'
							+ page + '"></iframe>').dialog({
				autoOpen : false,
				modal : true,
				height : dialogHeight,
				width : dialogWidth,
				// title: title,
				close : function() {
					$('#prevDialog iframe').attr('src', 'about:blank');
					$('#prevDialog').remove();

					// refresh tables
					onElementDataTableApiChange('experimensTable');

				}
			});

	$dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
}

function openEnvMonitPrintForm(action) {
	if (isAlive()) {

		var dialogWidth = $(window).width() - 10;
		var dialogHeight = $(window).height() - 10;
		var parentId = $('#formId').val();
		var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=EnvMonitPrintFilt&formId=-1&userId="
				+ $('#userId').val() + '&tableType=&PARENT_ID=' + parentId;

		// open iframe inside dialog
		var $dialog = $(
				'<div id="prevDialog" style="overflow-y: hidden;""></div>')
				.html(
						'<iframe style="border: 0px;width:100%;height:100%" src="'
								+ page + '"></iframe>').dialog({
					autoOpen : false,
					modal : true,
					height : dialogHeight,
					width : dialogWidth,
					// title: title,
					close : function() {
						$('#prevDialog iframe').attr('src', 'about:blank');
						$('#prevDialog').remove();

						return;
					}
				});

		$dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
	}
}

function insertValueAfterSearch() {
	var purity = $('#purityInf').val();
	if (purity == "") {
		document.getElementById('purityInf').value = '100';
		$("#PURITYUOM_ID_INF").val(
				$("#PURITYUOM_ID_INF option:contains('%')").val());
		$("#PURITYUOM_ID_INF").trigger('chosen:updated');
	}
}

function openCoAPrintForm(action) {

	if (isAlive()) {
		var dialogWidth = $(window).width() - 10;
		var dialogHeight = $(window).height() - 10;
		var parentId = $('#formId').val();
		var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=CoAPrintFilt&formId=-1&userId="
				+ $('#userId').val() + '&tableType=&PARENT_ID=' + parentId;

		// open iframe inside dialog
		var $dialog = $(
				'<div id="prevDialog" style="overflow-y: hidden;""></div>')
				.html(
						'<iframe style="border: 0px;width:100%;height:100%" src="'
								+ page + '"></iframe>').dialog({
					autoOpen : false,
					modal : true,
					height : dialogHeight,
					width : dialogWidth,
					// title: title,
					close : function() {
						$('#prevDialog iframe').attr('src', 'about:blank');
						$('#prevDialog').remove();

						// refresh tables
						// onElementDataTableApiChange('experimensTable');

					}
				});

		$dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
	}
}

function removeExpSeriesIndexFunc(action) {

	showWaitMessage("Please wait...");
	prop.onChangeAjaxFlag = false;

	// get remove Id
	var removeId = "";
	var selectedTable = $('#experimensTable').DataTable();
	var custid = selectedTable.row('.selected').data();
	if (typeof custid !== 'undefined') {
		removeId = custid[0];
	}

	// make bean
	var stringifyToPush = {
		code : 'removeIndexId',
		val : removeId,
		type : "AJAX_BEAN",
		info : 'na'
	};

	// get all data and add removeIndexId
	var allData = getformDataNoCallBack(1);
	var allData = allData.concat(stringifyToPush);

	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + "&userId=" + $('#userId').val()
			+ "&eventAction=" + action + "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "doSave",
		data : allData,
		errorMsg : ""
	});

	// call...
	$
			.ajax({
				type : 'POST',
				data : data_,
				url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
				contentType : 'application/json',
				dataType : 'json',

				success : function(obj) {
					hideWaitMessage();

					var toRemove = obj.data[0].val;// 1 if possible to remove,
													// 0 if not possible
					if (toRemove == '1') {
						// message:are you sure that you want to remove the
						// index?
						openConfirmDialog({
							onConfirm : refreshTable,
							title : 'Warning',
							message : getSpringMessage('confirmRemoveIndexInSeries')
						});
						// if yes- refresh...

					} else {
						displayAlertDialog(getSpringMessage('impossibleRemoveIndexInSeries'));
						hideWaitMessage();
					}

				},
				error : handleAjaxError
			});
}

function refreshTable() {

	showWaitMessage("Please wait...");
	prop.onChangeAjaxFlag = false;

	// get remove Id
	var removeId = "";
	var selectedTable = $('#experimensTable').DataTable();
	var custid = selectedTable.row('.selected').data();
	if (typeof custid !== 'undefined') {
		removeId = custid[0];
	}

	// make bean
	var stringifyToPush = {
		code : 'removeIndexId',
		val : removeId,
		type : "AJAX_BEAN",
		info : 'na'
	};

	// get all data and add removeIndexId
	var allData = getformDataNoCallBack(1);
	var allData = allData.concat(stringifyToPush);
	var action = "RemoveExpSeriesIndex";
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + "&userId=" + $('#userId').val()
			+ "&eventAction=" + action + "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "doSave",
		data : allData,
		errorMsg : ""
	});

	// call...
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',

		success : function(obj) {
			hideWaitMessage();
			// refresh table
			onElementDataTableApiChange('experimensTable');
			onElementDataTableApiChange('formulationPropertiesTable');

		},
		error : handleAjaxError
	});
}

function makeAjaxCallEvent(action) {
	onChangeAjax(action);
}

function openSearchMaterial(action) {
	// //////////
	// not in use - we use formulantRef from instead
	// //////////

	// var page, $dialog, dialogWidth, dialogHeight, parentId, i, elementsArray,
	// colsArray, iframeContents, toReturn;

	// dialogWidth = $(window).width() - 8;
	// dialogHeight = $(window).height() - 10;
	// parentId = $('#formId').val(); // maybe unnecessary
	//
	// page = "./init.request?stateKey=" + $('#stateKey').val() +
	// "&formCode=MaterialSearch&formId=-1&userId=" +
	// $('#userId').val() + '&tableType=&PARENT_ID=' + parentId;
	//
	// $dialog = $('<div id="prevDialog" style="overflow-y: hidden;""></div>')
	// .html('<iframe style="border: 0px;width:100%;height:100%" src="' + page +
	// '"></iframe>')
	// .dialog({
	// autoOpen: false,
	// modal: true,
	// height: dialogHeight,
	// width: dialogWidth,
	// //title: 'Search',
	// close: function () {
	// iframeContents = $(this).find('iframe').contents();
	// if (iframeContents.find('#save_').attr('flag')) { // detect if save
	// button was clicked.
	// toReturn = JSON.parse(iframeContents.find('#toReturn').val());
	// }
	// addFormulant(action,toReturn);
	// $(this).find('iframe').attr('src', 'about:blank');
	// $(this).remove();
	// }
	// });
	// $dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
}

function removeFormulant(action) {
	// NOT IN USE

	// showWaitMessage("Please wait...");
	// prop.onChangeAjaxFlag = false;
	// if(action=="removeFormulant")
	// var selectedTable = $('#formulants').DataTable();
	// else var selectedTable = $('#productMixture').DataTable();
	// //get remove Id
	// var removeId= "";
	//	   
	// var custid = selectedTable.row('.selected').data();
	// if (typeof custid !== 'undefined') {
	// removeId = custid[0];
	// }
	//	    
	// //make bean
	// var stringifyToPush = {
	// code: 'removeId',
	// val: removeId,
	// type: "AJAX_BEAN",
	// info: 'na'
	// };
	//	    
	// //get all data and add removeIndexId
	// var allData = getformDataNoCallBack(1);
	// var allData = allData.concat(stringifyToPush);
	//	     
	// //url call
	// var urlParam =
	// "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() +
	// '&userId=' + $('#userId').val() + "&eventAction=" + action + "&isNew=" +
	// $('#isNew').val();
	//	 
	//	
	// var data_ = JSON.stringify({
	// action: "doSave",
	// data: allData,
	// errorMsg: ""
	// });
	//	
	// //call...
	// $.ajax({
	// type: 'POST',
	// data: data_,
	// url: "./generalEvent.request" + urlParam + "&stateKey=" +
	// $('#stateKey').val(),
	// contentType: 'application/json',
	// dataType: 'json',
	// success: function (obj) {
	// hideWaitMessage();
	// //refresh table
	// if(action=="removeFormulant")
	// onElementDataTableApiChange('formulants');
	// else onElementDataTableApiChange('productMixture');
	// },
	// error: handleAjaxError
	// });
}

function addFormulant(action, toReturn) {
	// NOT IN USE

	// showWaitMessage("Please wait...");
	// prop.onChangeAjaxFlag = false;
	//     
	// //get material id and batch id
	// var materialId= toReturn["INVITEMMATERIAL_ID"];
	// var batchId= toReturn["Batch id"];
	//   
	//    
	// //make bean
	// var materialStringifyToPush = {
	// code: 'materialId',
	// val: materialId,
	// type: "AJAX_BEAN",
	// info: 'na'
	// };
	// //make bean
	// var batchStringifyToPush = {
	// code: 'batchId',
	// val:batchId,
	// type: "AJAX_BEAN",
	// info: 'na'
	// };
	//    
	// //get all data and add removeIndexId
	// var allData = getformDataNoCallBack(1);
	// var allData =
	// allData.concat(batchStringifyToPush,materialStringifyToPush);
	//     
	// //url call
	// var urlParam =
	// "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() +
	// '&userId=' + $('#userId').val() + "&eventAction=" + action + "&isNew=" +
	// $('#isNew').val();
	//
	// var data_ = JSON.stringify({
	// action: "doSave",
	// data: allData,
	// errorMsg: ""
	// });
	//
	// //call...
	// $.ajax({
	// type: 'POST',
	// data: data_,
	// url: "./generalEvent.request" + urlParam + "&stateKey=" +
	// $('#stateKey').val(),
	// contentType: 'application/json',
	// dataType: 'json',
	// success: function (obj) {
	// hideWaitMessage();
	// //refresh table
	// if(action=='addFormulant')
	// onElementDataTableApiChange('formulants');
	// else
	// onElementDataTableApiChange('productMixture');
	// },
	// error: handleAjaxError
	// });
}

function checkForPlannedTemplateAndUpdate(action) {
	action = 'checkForPlannedTemplateAndUpdate';
	showWaitMessage("Please wait...");
	prop.onChangeAjaxFlag = false;
	var allData = getformDataNoCallBack(1);
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + '&userId=' + $('#userId').val()
			+ "&eventAction=" + action + "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "doSave",
		data : allData,
		errorMsg : ""
	});

	// call...
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',
		success : function(obj) {
			hideWaitMessage();//newFormId
			var value = obj.data[0].val;
			if(value.indexOf("PLANNED")!=-1){//there are planned templates based on the same template 
				var plannedTempLst = value.split(":")[1];
				openConfirmDialog({
					onConfirm : function(){updateTemplateVersion(allData,plannedTempLst)},
					title : 'Warning',
					message : getSpringMessage('PLANNED_TEMPLATE_EXIST'),
					onCancel : function(){
						//navigate to the last planned
						var newFormId = plannedTempLst.split(",")[0];
						var newUrl = window.location.pathname + "?formId=" + newFormId
								+ "&formCode=" + $('#formCode').val() + '&userId='
								+ $('#userId').val() + "&eventAction=" + action
								+ "&isNew=0";
						fgReloadForm(newUrl); // window.location = newUrl;
					},
					confirmButtonHtml : "Yes, continue",
					cancelButtonHtml : "Open Template",
					isCloseIcondisplayed : false
				});
			} else {
				var newFormId = value;
				var newUrl = window.location.pathname + "?formId=" + newFormId
						+ "&formCode=" + $('#formCode').val() + '&userId='
						+ $('#userId').val() + "&eventAction=updateTemplateVersion&isNew=1";
				fgReloadForm(newUrl); // window.location = newUrl;
			}
			
		},
		error : handleAjaxError
	});
}

function updateTemplateVersion(allData,plannedTempLst) {
	var action = 'updateTemplateVersion';
	showWaitMessage("Please wait...");
	prop.onChangeAjaxFlag = false;
	var stringifyToPush = {
			code : 'plannedTempLst',
			val : plannedTempLst,
			type : "AJAX_BEAN",
			info : 'na'
	};
	allData = allData.concat(stringifyToPush);
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + '&userId=' + $('#userId').val()
			+ "&eventAction=" + action + "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "doSave",
		data : allData,
		errorMsg : ""
	});

	// call...
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',
		success : function(obj) {
			hideWaitMessage();//newFormId
			var value = obj.data[0].val;
			var newFormId = value;
			var newUrl = window.location.pathname + "?formId=" + newFormId
					+ "&formCode=" + $('#formCode').val() + '&userId='
					+ $('#userId').val() + "&eventAction=" + action
					+ "&isNew=1";
			fgReloadForm(newUrl); // window.location = newUrl;
		},
		error : handleAjaxError
	});
}


function sysConfigCalcSetter(action) {

	showWaitMessage("Please wait...");
	prop.onChangeAjaxFlag = false;
	var allData = getformDataNoCallBack(1);

	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + "&userId=" + $('#userId').val()
			+ "&eventAction=" + action + "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "doSave",
		data : allData,
		errorMsg : ""
	});

	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',
		success : function(obj) {
			hideWaitMessage();
			object = JSON.parse(obj.data[0].val);
			for (key in object) {
				if (object.hasOwnProperty(key)) {
					if ($('#' + key).attr('type') == 'Number') {
						$('#' + key).attr('realvalue', object[key]);
						$('#' + key).attr('title', object[key]);
						$('#' + key).val(object[key]);
						// $('#' + key).val('999'); //for tests...
					}
				}
			}
		},
		error : handleAjaxError
	});

}

function addMaterialToMotherLiquerTable(action) {
	var action = 'addMaterialToMotherLiquerTable';
	showWaitMessage("Please wait...");
	prop.onChangeAjaxFlag = false;
	var allData = getformDataNoCallBack(1);
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + '&userId=' + $('#userId').val()
			+ "&eventAction=" + action + "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "doSave",
		data : allData,
		errorMsg : ""
	});

	// call...
	$.ajax({
		type : 'POST',
		// async: false,
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',
		success : function(obj) {
			hideWaitMessage();
			onElementDataTableApiChange('motherLiquor');
		},
		error : handleAjaxError
	});
}


function CloneExperimentByFormId(formId) {
	// for nor now the id of the experiment is not matter... using getCreatePermissionFormCode
	$.ajax({
		type : 'POST',
		data : '{"action" : "getCreatePermissionFormCode","' + 'data":['
				+ '{"code":"formCode","val":"Experiment"},{"code":"formId","val":"' + formId + '"},{"code":"stateKey","val":"'+ $('#stateKey').val() + '"}' + '],' + '"errorMsg":""}',
		url : "./getCreatePermissionFormCode.request",
		contentType : 'application/json',
		dataType : 'json',
		success : function(obj) {
			var canNewByList = false;
			if (obj.data[0].val != '1') {
				hideWaitMessage();
				displayAlertDialog("The user is not allowed to create a new experiment.");
			} else {
				var newUrl = window.location.pathname + "?formId=" + "-1"
				+ "&formCode=ExpCloneMain" + '&userId='
				+ $('#userId').val() + "&eventAction="
				+ "&isNew=1" + '&PARENT_ID=' + formId;
			    fgReloadForm(newUrl); // window.location = newUrl;
			}
		},
		error : handleAjaxError
	});
}

function CloneExperiment(action) {
	var action = 'CloneExperiment';
	openConfirmDialog({
        onConfirm: function(){
        	showWaitMessage("Please wait...");
        	
        				var newUrl = window.location.pathname + "?formId=" + "-1"
						+ "&formCode=ExpCloneMain" + '&userId='
						+ $('#userId').val() + "&eventAction=" + action
						+ "&isNew=1" + '&PARENT_ID=' + $('#formId').val();
				        fgReloadForm(newUrl); // window.location = newUrl;
				        hideWaitMessage();
        },
        title: 'Warning',
        message: getSpringMessage('confirmCloneExperiment'),
        onCancel: function(){
        }
    });
	
}

function makeWuFractionCallEvent(action) {
	$('#inProductInf').val('');
	makeAjaxCallEvent('MATERIAL_ID');
}

function coaReportMaterialSearch(action) {
	makeAjaxCallEvent('materialId');
	makeAjaxCallEvent('BATCH_ID');
}

function updateSelfTestResultWebixElement() {
	var _id = $('#materialId').val();
	var _val = $('#materialName').val().replace(/</g, '&lt;');

	console.log("-------- updateSelfTestResultWebixElement() ------------");
	console.log("materialId: " + _id);
	console.log("materialName: " + _val);
	console.log("--------------------");

	if (_id != "" && _val != "") {
		var $div = $("div[name='parentWebixContainer'][id='webixAnalyticalSelfTest']");
		var tableID = $($div.find(".webix_container")).attr('childTableID');

		var grid = $$(tableID);
		var _curColConfig = grid.getColumnConfig("invitem_material");
		// console.log(_curColConfig);

		/*
		 * var options = _curColConfig.options; console.log(options);
		 * if($.isEmptyObject(options)) { _curColConfig.options =
		 * [{"id":_id,"value":_val}]; } else { var arr = options;
		 * arr.push({"id":_id,"value":_val}); _curColConfig.options = arr; }
		 */
		// console.log(_curColConfig.collection);
		var arr = _curColConfig.collection.config.data;
		// console.log(arr);
		// console.log($.isEmptyObject(arr));
		// console.log(arr.length + " value: " + arr[0].value);
		if (!$.isEmptyObject(arr) && arr.length == 1 && arr[0].value == "") {
			_curColConfig.collection.config.data = [];
			_curColConfig.collection.clearAll();

			console.log(_curColConfig.collection.config.data);
			console.log(_curColConfig.collection);

			_curColConfig.collection.config.data = [ {
				id : _id,
				value : _val
			} ];
			_curColConfig.collection.parse([ {
				id : _id,
				value : _val
			} ]);

			console.log(_curColConfig.collection.config.data);
			console.log(_curColConfig.collection);

		} else {
			// console.log("else");
			arr.push({
				id : _id,
				value : _val
			});
			// console.log(arr);
			// _curColConfig.collection.config.data = arr;
			_curColConfig.collection.parse(arr);
		}
	}
}

function addStreamTable() {
	
	var activeTabId = getActiveTabID();
	var currDomId = "webixMassBalanceTable";
	if(activeTabId == "MassBallance2Tab") currDomId = "webixMassBalanceTable2";
	else if(activeTabId == "MassBallance3Tab") currDomId = "webixMassBalanceTable3";
	
	addWebixMassBalanceTable(currDomId);
}

function calculateMassBalanceFields(domId,fInx) {
	
	var currDomId = domId;
	var fieldInx = fInx;
	if(domId == undefined || domId == null || domId == "")
	{
		var activeTabId = getActiveTabID();
		currDomId = "webixMassBalanceTable";
		fieldInx = "";
		if(activeTabId == "MassBallance2Tab"){
			currDomId = "webixMassBalanceTable2";
			fieldInx = "2";
		}
		else if(activeTabId == "MassBallance3Tab"){
			currDomId = "webixMassBalanceTable3";
			fieldInx = "3";
		}
	}
	
	var obj = calcAllMassBalance(currDomId);
	$('#summary'+fieldInx).val(obj["summary"]);
	$('#summary'+fieldInx).attr('realvalue',obj["summary"]);
	$('#conversion'+fieldInx).val(obj["conversion"]);
	$('#conversion'+fieldInx).attr('realvalue',obj["conversion"]);
	$('#chemicalYield'+fieldInx).val(obj["totalChemical"]);
	$('#chemicalYield'+fieldInx).attr('realvalue',obj["totalChemical"]);
	$('#isolatedYield'+fieldInx).val(obj["totalIsolated"]);
	$('#isolatedYield'+fieldInx).attr('realvalue',obj["totalIsolated"]);
	
	if($('#formCode').val()=="ExperimentCP"){
		updateMassbalanceDataperRun(fieldInx);
	}
}

function copyExpFormulationData() {
//	openConfirmDialog({
//		onConfirm : doCopyExpFormulationData,
//		title : 'Warning',
//		message : getSpringMessage('overrideStepFrData')
//	});
}

function doCopyExpFormulationData() {
//	var action = 'copyExpFormulationData';
//	showWaitMessage("Please wait...");
//	prop.onChangeAjaxFlag = false;
//	var allData = getformDataNoCallBack(1);
//	// url call
//	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
//			+ $('#formCode').val() + '&userId=' + $('#userId').val()
//			+ "&eventAction=" + action + "&isNew=" + $('#isNew').val();
//
//	var data_ = JSON.stringify({
//		action : "doSave",
//		data : allData,
//		errorMsg : ""
//	});
//
//	// call...
//	$.ajax({
//		type : 'POST',
//		// async: false,
//		data : data_,
//		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
//		contentType : 'application/json',
//		dataType : 'json',
//		success : function(obj) {
//			hideWaitMessage();
//			// webixExpVal
//			// id=\"" + domId + "\" elementID=\"" + value
//
//			$('#webixExpStep').attr('elementID', obj.data[0].val);
//			onElementDataTableApiChange('formulants');
//			// makeAjaxCallEvent('formulants');
//			onElementDataTableApiChange('productMixture');
//			// makeAjaxCallEvent('productMixture');
//			makeAjaxCallEvent('planned_actual');
//			// displayAlertDialog(getSpringMessage('Clone Succeeded'));
//			// onElementDataTableApiChange('motherLiquor');
//		},
//		error : function() {
//			hideWaitMessage();
//			displayAlertDialog(getSpringMessage('Copy Failed'));
//		}
//	// handleAjaxError
//	});
	
}

/**
 * The function starts the expected run after validating some important data
 * Validations are:
 * 		All preparation steps are active(not planned)- this validation should be made before calling createRun
 *      All calculations of the run were made successfully
 * After creating the run- the run step is changed to active.
 * If the function was called by changing the header DDL in the step- then it navigates to the started run step
 * @param sourceCall 0 -called when clicking on the design table,
 * 					 1- called when changing the header DDL to a planned run in the experiment form
 *                   2- called when changing the header DDL to a planned run in the step form
 * @param runNumber- run number to get started
 * @returns
 */
function createRun(sourceCall,runNumber)
{
	showWaitMessage("Please wait...");
	
	var selectedTable = $('#expRunPlanningTable').DataTable();
	var selRow = sourceCall==undefined||sourceCall==0?selectedTable.row('.selected').data():[];
	if(sourceCall==1){
		selectedTable.rows().eq(0).each( function ( index ) {
		    var row = selectedTable.row( index );
		    var data = row.data();
		    if(data[1]==runNumber){
		    	selRow = data;
		    }
		} );
	} else if(sourceCall==2){//called from the step form-need to get the runId from the server
		selRow[0] = '';
		selRow[1] = runNumber;
		selRow[2] = '';
	}
	
	prop.onChangeAjaxFlag = false;
	if (typeof selRow !== 'undefined') 
	{
		var runNumber = selRow[1];
		var runID = selRow[0];
		var expHasStartedRuns = selRow[2];
		var urlParam = "?formId="+$('#formId').val()+"&formCode="
			+ $('#formCode').val() + "&userId=" + $('#userId').val()
			+ "&eventAction=calculateAndCheckForStartedRuns&isNew=" + $('#isNew').val();
		
		var data_ = JSON.stringify({
			action : "calculateAndCheckForStartedRuns",
			data : [{code : "runId", val:runID},
					{code : "expHasStartedRuns", val:expHasStartedRuns},
					{code : "runNumber", val:runNumber},
					{code : "EXPERIMENT_ID", val:$('#EXPERIMENT_ID').val()}
				],
			errorMsg : ""
		});
		
		$.ajax({
				type : 'POST',
				data : data_,
				url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
				contentType : 'application/json',
				dataType : 'json',
				success : function(obj) {
					hideWaitMessage();
					if (obj.errorMsg != null && obj.errorMsg != '') {
						displayAlertDialog(obj.errorMsg);
					} 
					else if (obj.data[0].val != null)
					{
						var _val = obj.data[0].val;
						if (checkIfJSON(_val)) {
							var warnMsg = "";
							var calcInfoMsg = "";
							var fullObj = funcParseJSONData(_val); 
							if(Object.keys(fullObj).length > 0)
							{
								if(fullObj.hasOwnProperty("warningMsg"))
								{
									warnMsg = fullObj["warningMsg"];
								}
							}
							if(warnMsg != "")
							{
								displayAlertDialog(warnMsg);
								if((sourceCall==1 && $('#formCode').val()=="ExperimentCP")
										|| sourceCall==2){
									var lastvalue = $("#headerSelect").attr('lastvalue');
									$("#headerSelect").val(lastvalue);
									$("#headerSelect").trigger('chosen:updated');
								}
							}
						}
						else {
							openConfirmDialog({
								onConfirm : doCreateRun,//checkPrevRunStepsPlannedActualValues,
								onConfirmParams: [runID,expHasStartedRuns,runNumber,sourceCall],
								title : 'Warning',
								message : _val,
								onCancel: function(){
									if((sourceCall==1 && $('#formCode').val()=="ExperimentCP")
											|| sourceCall==2){
										var lastvalue = $("#headerSelect").attr('lastvalue');
										$("#headerSelect").val(lastvalue);
										$("#headerSelect").trigger('chosen:updated');
									}
								}
							});
						}
					}
				},
				error : handleAjaxError
			});
	}
}

//not in use when starting run anymore-TODO:check its necessity for other uses
function checkPrevRunStepsPlannedActualValues(params)
{
	var runID = params[0];
	var expHasStartedRuns = params[1];
	var runNumber = params[2];
	var sourceCall = params[3];
	
	// in case Experiment has not yet Active(started) Runs, skip any validation
	if(expHasStartedRuns == "0")
	{
		doCreateRun(params);
	}
	else
	{
		var urlParam = "?formId="+ $('#formId').val() //experimentID
					+ "&formCode="+ $('#formCode').val()
					+ "&userId="+ $('#userId').val()
					+ "&eventAction=checkPrevRunStepsPlannedActualValues"
					+ "&isNew=" + $('#isNew').val();
	
		var data_ = JSON.stringify({
			action : "checkPrevRunStepsPlannedActualValues",
			data : [{code : "runId", val:runID}],
			errorMsg : ""
		});

		$.ajax({
				type : 'POST',
				data : data_,
				url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
				contentType : 'application/json',
				dataType : 'json',
				success : function(obj) {
					if (obj.errorMsg != null && obj.errorMsg != '') {
						displayAlertDialog(obj.errorMsg);
						if(sourceCall==1 && $('#formCode').val()=="ExperimentCP"){
							var lastvalue = $("#headerSelect").attr('lastvalue');
							$("#headerSelect").val(lastvalue);
							$("#headerSelect").trigger('chosen:updated');
						}
					} 
					else if(obj.data[0].val != '1')//There is a confirmation message
					{
						openConfirmDialog({
					        onConfirm: function(){
					        	hideWaitMessage();
					        	doCreateRun(params);
					        },
					        title: 'Warning',
					        message: getSpringMessage(obj.data[0].val),
					        onCancel: function(){
					        	if(sourceCall==1 && $('#formCode').val()=="ExperimentCP"){
									var lastvalue = $("#headerSelect").attr('lastvalue');
									$("#headerSelect").val(lastvalue);
									$("#headerSelect").trigger('chosen:updated');
								}
				        		hideWaitMessage();
					        }
					    });
						hideWaitMessage();
					} else {
						hideWaitMessage();
						doCreateRun(params);
					}
					
				},
				error : handleAjaxError
			});
	}
}

function doCreateRun(params)
{
	showWaitMessage("Please wait...");
	var sourceCall=params!=undefined?params[3]:0;
	var selectedTable = $('#expRunPlanningTable').DataTable();
	var selRow = selectedTable.row('.selected').data();
	prop.onChangeAjaxFlag = false;
	if (typeof selRow !== 'undefined' || params != undefined) 
	{
		var runNumber = params == undefined?selRow[1]:params[2];
		var runId = params == undefined?selRow[0]:params[0];
		var expHasStartedRuns = params == undefined?selRow[2]:params[1];
		var urlParam = "?formId="+$('#formId').val()+"&formCode="
			+ $('#formCode').val() + "&userId=" + $('#userId').val()
			+ "&eventAction=createRun&isNew=" + $('#isNew').val();
		
		var allData = [
			{code : "runId", val:runId},
			{code : "runNumber", val:runNumber},
			{code : "expHasStartedRuns", val:expHasStartedRuns},
			{code : "EXPERIMENT_ID", val:$('#EXPERIMENT_ID').val()}
		];
		
		var data_ = JSON.stringify({
			action : "createRun",
			data : allData,
			errorMsg : ""
		});
		
		$.ajax({
				type : 'POST',
				data : data_,
				url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
				contentType : 'application/json',
				dataType : 'json',
				success : function(obj) {
					hideWaitMessage();
					if (obj.errorMsg != null && obj.errorMsg != '') {
						//displayAlertDialog(obj.errorMsg);
						if(obj.errorMsg.indexOf('##')){
							var msgPart = obj.errorMsg.split('##');
							var message_ = msgPart[0];
			        		var materialList = msgPart[1];
			        		var isAletMsg = msgPart[2];
			        		var isFamiliarAfterConfirm = msgPart[3];
			        		//show an alert of unfamiliarity message. In the future should mimic the operation from the step
		        			displayAlertDialog(message_);
		        			if((sourceCall==1 && $('#formCode').val()=="ExperimentCP")
									|| sourceCall==2){
									var lastvalue = $("#headerSelect").attr('lastvalue');
									$("#headerSelect").val(lastvalue);
									$("#headerSelect").trigger('chosen:updated');
								}
						} else {
							displayAlertDialog(obj.errorMsg);
						}
					} 
					else if (obj.data[0].val != null)
					{
						if(sourceCall==2){
							navigateToRun(runNumber);
						} else {
							if(sourceCall==1){//source call is the changing of the header DDL in the experiment form
								$('#headerSelect').attr('lastvalue', $('#headerSelect option:selected').val())
								setFormParamMap('ExperimentCP', $('#formId').val(),"RUNNUMBER_PARAM",runNumber,filterExpTablesAfterSelectRun,[runNumber]);
								var cName = $('#userId').val()+':'+$('#formId').val()+':'+'$RUNNUMBER';
								setCookie(cName,runNumber,365);
							}
							if(sourceCall == undefined || sourceCall == 0|| sourceCall == 1){
								//update the experiment fields changes
								var valuesToChange =  JSON.parse(obj.data[0].val);
								
								$.each(valuesToChange, function(key, value) { 
									$('#'+key).val(value);
									$('#'+key).attr("realvalue",value);
									/*if(key=="STATUS_ID"){
										$('#'+key).trigger("chosen:updated");
									}*/
								});
								

								var isFirstRun =  $('#headerSelect option[value*="Active"]').length==0?true:false;
								if(isFirstRun){
									renderStatusField(valuesToChange["STATUS_ID"]);
								}
							}
							//update the option to be an active run
							updateRunFromPlannedToActive(runNumber);
							onElementDataTableApiChange("expRunPlanningTable");
						}
					}
				},
				error : handleAjaxError
			});
	}
}

/**
 * The function update the Workflow states and then call updateStatusValAndAuthorizePage that sets the status value to the value arg and generates authorization
 * @param value
 * @returns
 */
function renderStatusField(value){
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
	+ $('#formCode').val() + "&userId=" + $('#userId').val() + "&isNew=" + $('#isNew').val();

	var allData = getformDataNoCallBack(1);
	
	var data_ = JSON.stringify({
	action : "updateWFState",
	data : allData,
	errorMsg : ""
	});
	
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./updateWFState.request" + urlParam + "&stateKey="
				+ $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',
		success : function(obj) {
			hideWaitMessage();
			updateStatusValAndAuthorizePage(value);
			if(obj.data[0].val != null){
				
			}
		},
		error : handleAjaxError
	});
}

function updateStatusValAndAuthorizePage(value){
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
	+ $('#formCode').val() + "&userId=" + $('#userId').val() + "&isNew="+ $('#isNew').val();

	var data_ = {
			"elementName":"STATUS_ID",
			"value":value
			};
	
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./updateElementBody.request" + urlParam + "&stateKey="
				+ $('#stateKey').val(),
		//contentType : 'application/json',
		dataType : 'json',
		success : function(obj) {
			hideWaitMessage();
			if(obj.data[0].val != null){
				eval(obj.data[0].val);
				$("#STATUS_ID").trigger("chosen:updated");
				//onChangeAjax("STATUS_ID");
				//generate the influence of the active state on the form
				ExpDisableMandatoryByStatus(1,'Active:Active:1');
			}
		},
		error : handleAjaxError
	});
}

/**
 * 
 * @param sourceCall 0 -called when clicking on the design table,
 * 					 1- called when changing the header DDL to a planned run in the experiment form
 *                   2- called when changing the header DDL to a planned run in the step form
 * @param runNumber- run number to get started
 * @returns
 */
function checkStepStatusAndCreateRun(sourceCall,runNumber) {
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + "&userId=" + $('#userId').val()
			+ "&eventAction=checkRunStepsStatus&isNew=" + $('#isNew').val();

	var stringifyToPush = {
			code : 'runNumber',
			val : runNumber,
			type : "AJAX_BEAN",
			info : 'na'
	};
	var allData = getformDataNoCallBack(1);
	allData = allData.concat(stringifyToPush);
	
	var data_ = JSON.stringify({
		action : "checkRunStepsStatus",
		data : allData,
		errorMsg : ""
	});

	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey="
				+ $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',
		success : function(obj) {
			hideWaitMessage();
			if (obj.errorMsg != null && obj.errorMsg != '') {
				displayAlertDialog(obj.errorMsg);
				if($('#formCode').val()=="Step"){
					var lastValue = $("#headerSelect").attr('lastvalue');
					$("#headerSelect").val($("#headerSelect option[value='"+(lastValue!=""?lastValue:'undefined')+"']").val());
					$("#headerSelect").trigger('chosen:updated');
					/*$("#headerSelect").val($("#headerSelect option:contains('"+$('#runNumber').val()+"')").val());
					$("#headerSelect").trigger('chosen:updated');*/
				} else if($('#formCode').val()=="ExperimentCP"){
					if(sourceCall != "0"){
						var lastvalue = $("#headerSelect").attr('lastvalue');
						$("#headerSelect").val(lastvalue);
						$("#headerSelect").trigger('chosen:updated');
					}
				}
			} else if(obj.data[0].val != null){
				if(obj.data[0].val == "0") {//all preparation runs are active
					createRun(sourceCall,runNumber);
				} else {
					displayAlertDialog("Some preparation steps are still in plan");
					if($('#formCode').val()=="Step"){
						var lastValue = $("#headerSelect").attr('lastvalue');
						$("#headerSelect").val($("#headerSelect option[value='"+(lastValue!=""?lastValue:'undefined')+"']").val());
						$("#headerSelect").trigger('chosen:updated');
						/*$("#headerSelect").val($("#headerSelect option:contains('"+$('#runNumber').val()+"')").val());
						$("#headerSelect").trigger('chosen:updated');*/
					} else if($('#formCode').val()=="ExperimentCP"){
						if(sourceCall!="0"){//started the run from the experiment header DDL
							var lastvalue = $("#headerSelect").attr('lastvalue');
							$("#headerSelect").val(lastvalue);
							$("#headerSelect").trigger('chosen:updated');
						}
					}
				}
			}/*else if ($('#formCode').val() == 'Step') {
				navigateToRun(runNumber);
			} else {// experiment
				$('#headerSelect').attr('lastvalue', $('#headerSelect option:selected').val())
				setFormParamMap('ExperimentCP', $('#formId').val(),"RUNNUMBER_PARAM",runNumber,filterExpTablesAfterSelectRun,[runNumber]);
				var cName = $('#userId').val()+':'+$('#formId').val()+':'+'$RUNNUMBER';
				setCookie(cName,runNumber,365);;
				//update the option to be an active run
				updateRunFromPlannedToActive(runNumber);
			}*/
		},
		error : handleAjaxError
	});
}

function calculateRunsPlanning()
{
	showWaitMessage("Please wait...");

	var urlParam = "?formId=-1&formCode="
			+ $('#formCode').val() + "&userId=" + $('#userId').val()
			+ "&eventAction=calcMaterialRates&isNew=" + $('#isNew').val();
	
	var allData = [
		{code : "PARENT_ID", val:$('#formId').val()}
	];
	
	var data_ = JSON.stringify({
		action : "calcMaterialRates",
		data : allData,
		errorMsg : ""
	});
	
	$.ajax({
			type : 'POST',
			data : data_,
			url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
			contentType : 'application/json',
			dataType : 'json',
			success : function(obj) {
				hideWaitMessage();
				if (obj.errorMsg != null && obj.errorMsg != '') {
					displayAlertDialog(obj.errorMsg);
				} 
				else if (obj.data[0].val != null)
				{
					var warnMsg = "";
					var calcInfoMsg = "";
					var fullObj = funcParseJSONData(obj.data[0].val); 
					if(Object.keys(fullObj).length > 0)
					{
						if(fullObj.hasOwnProperty("warningMsg"))
						{
							warnMsg = fullObj["warningMsg"];
						}
						if(fullObj.hasOwnProperty("calcInfo"))
						{
							calcInfoMsg = fullObj["calcInfo"];
						}
					}
					if(calcInfoMsg != "")
					{
						console.log("RATE CALCULATION INFO: ",calcInfoMsg);
					}
					if(warnMsg != "")
					{
						displayAlertDialog(warnMsg);
					}
					else
					{
						onElementDataTableApiChange("expRunPlanningTable");
					}
				}
			},
			error : handleAjaxError
		});
}

function deleteRun() {
	var selectedTable = $('#expRunPlanningTable').DataTable();
	var selRow = selectedTable.row('.selected').data();
	prop.onChangeAjaxFlag = false;
	if (typeof selRow !== 'undefined') {
		
		openConfirmDialog({
			onConfirm : doDeleteRun,
			onConfirmParams:[selRow[0],selRow[1]],
			title : 'Warning',
			message : "Are you sure you want to delete this Run?"//getSpringMessage('confirmDeleteRun')
		});
	}
}

function doDeleteRun(params)
{
	var rowid = params[0];
	var runNumber = params[1];
	
	showWaitMessage("Please wait...");

	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + "&userId=" + $('#userId').val()
			+ "&eventAction=deleteRun&isNew=" + $('#isNew').val();

	var allData = [
		{code : "removeId", val:rowid}
	];

	var data_ = JSON.stringify({
		action : "deleteRun",
		data : allData,
		errorMsg : ""
	});
	
	$.ajax({
			type : 'POST',
			data : data_,
			url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
			contentType : 'application/json',
			dataType : 'json',
			success : function(obj) {
				hideWaitMessage();
               onElementDataTableApiChange('expRunPlanningTable'); 
               removeRunFromHeaderSelect(runNumber);
			},
			error : handleAjaxError
		});
}

function deleteAction() {
	var selectedTable = $('#action').DataTable();
	var custid = selectedTable.row('.selected').data();
	prop.onChangeAjaxFlag = false;
	if (typeof custid !== 'undefined'
		||$("#action > tbody > tr input[class='dataTableApiSelectInfo']:checked").length!=0)
	{
		openConfirmDialog({
			onConfirm : deleteSpesificAction,
			title : 'Warning',
			message : getSpringMessage('confirmDeleteAction')
		});
	}
	
}
 function deleteSpesificAction()
 {
	 
	 showWaitMessage("Please wait...");
	// get remove Id
		var removeId = "";
		var orderNum = "";
		var selectedTable = $('#action').DataTable();
		var custid = selectedTable.row('.selected').data();
		if (typeof custid !== 'undefined') {
			removeId = custid[0];
			orderNum = custid[5];
			console.log("deleteSpesificAction removeId", removeId);
			console.log("deleteSpesificAction formNumberID", orderNum);
		} else {
			var csvList_=[];
        	// add selected values that are not in lastMultiValues + remove unselected values that are in this table (=> the user unselected them)
			selectedTable.$('input[class="dataTableApiSelectInfo"]').each(function (index) {
        		if($(this).prop('checked')) {
        			csvList_.push($(this).val());
        		}
            });  
			removeId = csvList_.toString();
		}

		// make bean
		var stringifyToPush = {
			code : 'removeId',
			val : removeId,
			type : "AJAX_BEAN",
			info : 'na'
		};
		var stringifyToPush1 = {
				code : 'orderNum',
				val : orderNum,
				type : "AJAX_BEAN",
				info : 'na'
			};
		// get all data and add removeIndexId
		var allData = getformDataNoCallBack(1);
		var allData = allData.concat(stringifyToPush);
		var allData = allData.concat(stringifyToPush1);
		// url call
		var urlParam = "?formId=" + $('#formId').val() + "&formCode="
				+ $('#formCode').val() + "&userId=" + $('#userId').val()
				+ "&eventAction=deleteAction&isNew=" + $('#isNew').val();

		var data_ = JSON.stringify({
			action : "doSave",
			data : allData,
			errorMsg : ""
		});
	// call...
		$
				.ajax({
					type : 'POST',
					data : data_,
					url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
					contentType : 'application/json',
					dataType : 'json',

					success : function(obj) {
						hideWaitMessage();
	                   onElementDataTableApiChange('action'); 

						

					},
					error : handleAjaxError
				});
 }
 
 function generateExpAnalysisReport(action) {
	 onElementDataTableApiChange('reportTable', null,null,true);
	 //if at least one experiment characteristic sample has duplicated results without “Main” selection, an error message should display.
	 //get selected experiment
	 var lastMultiValues = $('#experimentTable_value').val(); 
	 var csvList_ = [];
 	if(lastMultiValues.length > 0) {
 		csvList_ = lastMultiValues.split(',');
 	} 
 	if ($.fn.DataTable.isDataTable('#experimentTable')){
 	var table_ = $('#experimentTable').DataTable(); 
	// add selected values that are not in lastMultiValues + remove unselected values that are in this table (=> the user unselected them)
    table_.$('input[Type="checkbox"]').each(function (index) {
    	if((',' + lastMultiValues + ',').indexOf(',' + $(this).val() + ',') <= -1) {
    		if($(this).prop('checked')) {
    			csvList_.push($(this).val());
    		}
    	} else {
    		if(!$(this).prop('checked')) {
    			//remove val from array
    			const index = csvList_.indexOf($(this).val());
    			csvList_.splice(index, 1);
    		}
    	}
    });
 	}
 	var selectedExpCsv =  csvList_.toString();
    var allData = getformDataNoCallBack(1);
    var stringifyToPush = {
			code : 'selectedExpCsv',
			val : selectedExpCsv,
			type : "AJAX_BEAN",
			info : 'na'
		};
    var designIdHolder=getDesignIdOrNameFromSession("ID");
     var stringifyToPush2 = {
			code : 'isSetDesign',
			val : designIdHolder!=null&&designIdHolder!=''&&designIdHolder!="",
			type : "AJAX_BEAN",
			info : 'na'
		};
    
    
    allData = allData.concat(stringifyToPush).concat(stringifyToPush2); 
    
 // url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val()
			+ "&userId=" + $('#userId').val()
			+ "&eventAction=checkExpDuplicatRes";

	var data_ = JSON.stringify({
		action : "checkExpDuplicatRes",
		data : allData,
		errorMsg : ""
	});
    $.ajax({
        type: 'POST',
        data: data_,
        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) 
        {
        	 if (obj.data[0].val.toString() != null && obj.data[0].val.toString()!=""){
        		 //displayAlertDialog(getSpringMessage('EXP_ANALYSIS_MAIN_RES')+" "+obj.data[0].val);
        		 displayAlertDialog(obj.data[0].val);
        	}
        },
        error: handleAjaxError
    });
 }
 
 function generateExpReport(action) {
	 onElementDataTableApiChange('reportTable', null,null,true);
 }
 
 function generateHistoryReport(action) {
	 	var allData = getformDataNoCallBack(1);
		// url call
		var urlParam = "?formId="+ $('#formId').val()
					+ "&formCode="+ $('#formCode').val()
					+ "&userId="+ $('#userId').val()
					+ "&eventAction=generateHistoryReport"
					+ "&isNew=" + $('#isNew').val();

		var data_ = JSON.stringify({
			action : "generateHistoryReport",
			data : allData,
			errorMsg : ""
		});

		// call...
		$.ajax({
			type : 'POST',
			data : data_,
			url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
			contentType : 'application/json',
			dataType : 'json',

			success : function(obj) {
				if(obj.data[0].val == null || obj.data[0].val.length == 0) {
					//displayAlertDialog("Enter formId");
					onElementDataTableApiChange('hstTable', null,null,true); 
				} else {
					onElementDataTableApiChange('hstTable', null,null,true); 
				}
			},
			error : handleAjaxError
		});
 }
 
 function searchAll(domId){
	 
	 	
	 if(domId!==undefined){
		if($('#'+domId).text()=='Search') {
			$('#'+domId).text("Stop Search");
			
		} else {
			stopQuery();
			$('#'+domId).text("Search");
			return;
		}
	 }
	 if(domId!==undefined){
	 var materialChecked = $('#inventoryResults').DataTable().$('input[Type ="checkbox"]').is(':checked');
	    if(materialChecked){
	    	$('#inventoryResults').DataTable().$('input[Type ="checkbox"]').prop('checked', false);//fixed bug 8314
	    }
	 }
	 if($('#isSavedSearch').val()!='1'){//fixed bug 8313
		 $('#searchInDocsOnly2').prop('checked',false);//fix bug 8092
	 }
	 
	 if( $('input[name="searchOrNavigate"]:checked').val() == "Molecule Search"){
		 getChemMaterialIdList();
		// $('#'+domId).text("Search");
		 
	 }else{
        var allData = getformDataNoCallBack(1);
 		// url call
 		var urlParam = "?formId="+ $('#formId').val()
 					+ "&formCode="+ $('#formCode').val()
 					+ "&userId="+ $('#userId').val()
 					+ "&eventAction=setSearchParam"
 					+ "&isNew=" + $('#isNew').val();

 		var data_ = JSON.stringify({
 			action : "setSearchParam",
 			data : allData,
 			errorMsg : ""
 		});

 		// call...
 		$.ajax({
 			type : 'POST',
 			data : data_,
 			url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
 			contentType : 'application/json',
 			dataType : 'json',

 			success : function(obj) {
 				if(obj.data[0].val != undefined && obj.data[0].val != ""){
					var formNumberId = obj.data[0].val;
				    $("#textBox").val(formNumberId);
				}
 				  if( $("#textBox").val().length<3 && $('input[name="searchOrNavigate"]:checked').val() != "Molecule Search"){
 					 $('#generate').text("Search");
 					  displayAlertDialog(getSpringMessage('VALIDATE_SEARCH_CHARACTERS'));
 				 }else{
 					 hideSearchTable(); 
 			     }
 				 // hideWaitMessage();
 				// $('#'+domId).text("Search");
 				
 			},
 			error : handleAjaxError
 		});
	 }
     }
 function hideSearchTable(){
	 if ($('#searchType option:selected').text() == 'Inventory'){
 		 onElementDataTableApiChange('inventoryResults', null,null,true);
 		 $('#inventoryResults_Parent').css('visibility', 'visible');
     	 $('#resentlyResults_Parent').css('visibility', 'hidden');
     	 $('#resentlyResults_Parent').css('display', 'none');// style="background-color:white"
     	 $('#ladvancedSearch2').css('display', '');
     	 $('#parentAdvanced2').css('display', '');
     	 $('#ladvancedSearch2').parent('table').css('display', '');
     	$('#imgAdvSearch2FormSearch').children('img').attr("src", "../skylineFormWebapp/images/arrow_d.png");
     	 if ($('.advanced2').is(':hidden')){
     		 $('.advanced2').slideToggle();
     	 }
 	        
     }else if ($('#searchType option:selected').text() == 'Project'){
    	 onChangeAjax('resentlyResults');
    		
     	onElementDataTableApiChange('resentlyResults', null,null,true);
     	$('#inventoryResults_Parent').css('visibility', 'hidden');
     	$('#inventoryResults_Parent').css('display', 'none'); 
     	if (!($('.advanced2').is(':hidden'))){
	        	$('.advanced2').slideToggle(); 
	        }
     	$('#parentAdvanced2').css('display', 'none');
     	$('#ladvancedSearch2').css('display', 'none');
     	$('#resentlyResults_Parent').css('visibility', 'visible');
     }else{
    	 onChangeAjax('resentlyResults');
    		
    	 onElementDataTableApiChange('inventoryResults', null,null,true);
	     onElementDataTableApiChange('resentlyResults', null,null,true);
	     $('#resentlyResults_Parent').css('visibility', 'visible');
     	 $('#inventoryResults_Parent').css('visibility', 'visible');
     	 $('#parentAdvanced2').css('display', '');
         $('#ladvancedSearch2').css('display', '');
         $('#imgAdvSearch2FormSearch').children('img').attr("src", "../skylineFormWebapp/images/arrow_d.png");
         if ($('.advanced2').is(':hidden')){
     		 $('.advanced2').slideToggle();
     	 }
     }
 }

 function invAdvancedSearch(){
	 // fixed bug 7464
	 onChangeAjax('resentlyResults');
	
	 var materialChecked = $('#inventoryResults').DataTable().$('input[Type ="checkbox"]').is(':checked');
	    if(!materialChecked &&  ($('input[name="searchOrNavigate"]:checked').val() != "Molecule Search"|| $(parent.document).find("#inventoryResults tr:last").text() != "No results were found") && $('input[name="searchOrNavigate"]:checked').val() != "Free Text"){
	    	 displayAlertDialog(getSpringMessage('SEARCH_NO_COMPOUND_SELECTED'));//fixed bug 8303,8104
	    	 return;
	    }
	    
	 if($('#invAdvancedSearchB').text()=='Search') {
			$('#invAdvancedSearchB').text("Stop Search");
			
		} else {
			stopQuery();
			$('#invAdvancedSearchB').text("Search");
			return;
		}

     var allData = getformDataNoCallBack(1);
		// url call
		var urlParam = "?formId="+ $('#formId').val()
					+ "&formCode="+ $('#formCode').val()
					+ "&userId="+ $('#userId').val()
					+ "&eventAction=setSearchParam"
					+ "&isNew=" + $('#isNew').val();

		var data_ = JSON.stringify({
			action : "setSearchParam",
			data : allData,
			errorMsg : ""
		});

		// call...
		$.ajax({
			type : 'POST',
			data : data_,
			url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
			contentType : 'application/json',
			dataType : 'json',

			success : function(obj) {
				 setTimeout(function () {// fixed bug (ConcurrentModificationException)
					 onElementDataTableApiChange('resentlyResults', null,null,true);
					 $('#resentlyResults_Parent').css('visibility', 'visible');
			 	},100);
				 // hideWaitMessage();
				// $('#'+domId).text("Search");
				
			},
			error : handleAjaxError
		});
	 
	 
	 
	
	 // hideWaitMessage();
	 // $('#invAdvancedSearchB').text("Search");
 }
 
 function changeNotifMessageState(action)
 {
	 var smartSelectList = "";
		var toReturn = [];
		$('input[class="dataTableApiSelectInfo"]:checked').each(function(index) {
			toReturn.push($(this).val());
		});
		// console.log(toReturn);
		smartSelectList = toReturn.toString();
		
		// get all data and add removeIndexId
		var allData = getformDataNoCallBack(1);
		allData.push({
			code : 'smartSelectList',
			val : smartSelectList,
			type : "AJAX_BEAN",
			info : 'na'
		});
		allData.push({
			code : 'messageState',
			val : action,
			type : "AJAX_BEAN",
			info : 'na'
		});
		console.log(allData);
		// url call
		var urlParam = "?formId="+ $('#formId').val()
					+ "&formCode="+ $('#formCode').val()
					+ "&userId="+ $('#userId').val()
					+ "&eventAction=changeNotifMessageState"
					+ "&isNew=" + $('#isNew').val();

		var data_ = JSON.stringify({
			action : "doSave",
			data : allData,
			errorMsg : ""
		});

		// call...
		$.ajax({
			type : 'POST',
			data : data_,
			url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
			contentType : 'application/json',
			dataType : 'json',

			success : function(obj) {
				if (obj.errorMsg != null && obj.errorMsg != '') {
					displayAlertDialog(obj.errorMsg);
				} 
				else
				{
					updateMenuItemUI(obj.data[0].val);
					onElementDataTableApiChange("summary");
				}
				/*
				 * else if ((obj.data[0].val == "1")) {
				 * displayAlertDialog(getSpringMessage(action + " was done
				 * successfully!")); } else if (obj.data[0].val == "0") {
				 * displayAlertDialog(getSpringMessage(action + " failed!")); }
				 */

				hideWaitMessage();
			},
			error : handleAjaxError
		});
    
 }
 
 function closeSaveReport(){
	 parent.$('#prevDialog').dialog('close');
 }
 
 /**
  * Opens save report dialog. After closing the dialog, the system saves the report in its existing/new name.
  *  If a new report name/existing one was selected the system navigates to it.
  *  If the save is on the same report name, then the report is saved and the dialog is closed.
  * @returns
  */
 function openSaveReportDialog(){

	 var dialogWidth = "550"// ($(window).width() - 10) * 0.7;
	 var dialogHeight = "550"// ($(window).height()) * 0.8;
	 var reportName = $('#pageTitle').text();
	 var reportDescription = $('#reportDescription').val();
	 var nameId=$('#nameId').val();
	 var parent_formCode = $('#formCode').val();
	 var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=SaveReport&formId=-1&userId="
	 + $('#userId').val()
	 + '&tableType=&PARENT_ID=-1&PARENT_FORMCODE=' + $('#formCode').val()// &REPORT_NAME='ss
	 // +
	 // reportName+'&REPORT_DESCRIPTION='+reportDescription;

	 // open iframe inside dialog
	 var $dialog = $(
	 '<div id="prevDialog" style="overflow-y: hidden;""></div>')
	 .html(
	 '<iframe style="border: 0px;width:100%;height:100%" src="'
	 + page + '"></iframe>')
	 .dialog(
	 {
	 autoOpen : false,
	 modal : true,
	 height : dialogHeight,
	 width : dialogWidth,
	 // title: title,
	 close : function() {
			 $('#prevDialog iframe').attr(
			 'src', 'about:blank');
			 var reportName=$('#prevDialog').data('reportName');
			 var reportDescription=$('#prevDialog').data('reportDescription');
			 var currentReportName = $('#reportName').val();
			 if(reportName !== undefined){
				if(reportName == currentReportName){
						 doSave(saveRulesTable(nameId),'SAVE_FORM_AND_USER_SETTINGS_BY_NAME',reportName,reportDescription,[reportName]); 
				     }
					 else{
						 doSave(navigateToReport,'SAVE_FORM_AND_USER_SETTINGS_BY_NAME',reportName,reportDescription,[reportName]);
					}
			 }
		
			 $('#prevDialog').remove();
		 }
	 });

	 $dialog.dialog('option', 'dialogClass', 'noTitleStuff').data('PARENT_FORMCODE', $('#formCode').val())
		.dialog('open');

	  }
function saveRulesTable(nameId,isNavigateToReport,page){

	 var stringifyNameToPush = {
				code : "nameId",
				val : nameId,
				type : "AJAX_BEAN",
				info : 'na'
			};
	 

		// get all data and add removeNameId
		var allData = getformDataNoCallBack(1);
		var allData = allData.concat(stringifyNameToPush);
		// url call
		var urlParam = "?formId="+ $('#formId').val()
					+ "&formCode="+ $('#formCode').val()
					+ "&userId="+ $('#userId').val()
					+ "&eventAction=saveRulesTable"
					+ "&isNew=" + $('#isNew').val();

		var data_ = JSON.stringify({
			action : "getReportList",
			data : allData,
			errorMsg : ""
		});

		// call...
		$.ajax({
			type : 'POST',
			data : data_,
			
			url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
			contentType : 'application/json',
			dataType : 'json',

			success : function(obj) {
				if (obj.errorMsg != null && obj.errorMsg != '') {
					displayAlertDialog(obj.errorMsg);
				} 
				if(isNavigateToReport){
					if (window.self === window.top){   
						window.location = page;
					}
				}
				hideWaitMessage();
			},
			error : handleAjaxError
		});

}

function saveRulesTableByUserId(){

	// get all data and add removeNameId
		var allData = getformDataNoCallBack(1);
		// url call
		var urlParam = "?formId="+ $('#formId').val()
					+ "&formCode="+ $('#formCode').val()
					+ "&userId="+ $('#userId').val()
					+ "&eventAction=saveRulesTableByUserId"
					+ "&isNew=" + $('#isNew').val();

		var data_ = JSON.stringify({
			action : "getReportList",
			data : allData,
			errorMsg : ""
		});

		// call...
		$.ajax({
			type : 'POST',
			data : data_,
			
			url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
			contentType : 'application/json',
			dataType : 'json',

			success : function(obj) {
				if (obj.errorMsg != null && obj.errorMsg != '') {
					displayAlertDialog(obj.errorMsg);
				}
				hideWaitMessage();
			},
			error : handleAjaxError
		});

}
/**
 * Check if the name of the saved report already exists in the user report list.
 * If so, a message displayed asking for the user confirmation.
 * After save the dialog is closed(see func' openSaveReportDialog- operations that are done after closing the dialog)
 * @param nameId
 * @param reportName
 * @param reportdescription
 * @returns
 */
 function validateOtherReportSameNameExists(nameId,reportName,reportdescription,parentFormCode){
	 var stringifyNameToPush = {
				code : "nameId",
				val : nameId,
				type : "AJAX_BEAN",
				info : 'na'
			};
	 
	 var stringifyToPush = {
				code : "reportName",
				val : reportName,
				type : "AJAX_BEAN",
				info : 'na'
			};
	 var stringifyToPush1 = {
				code : "parentFormCode",
				val : parentFormCode,
				type : "AJAX_BEAN",
				info : 'na'
			}; 

		// get all data and add removeNameId
		var allData = getformDataNoCallBack(1);
		var allData = allData.concat(stringifyToPush);
		var allData = allData.concat(stringifyToPush1);
		var allData = allData.concat(stringifyNameToPush);
		// url call
		var urlParam = "?formId="+ $('#formId').val()
					+ "&formCode="+ $('#formCode').val()
					+ "&userId="+ $('#userId').val()
					+ "&eventAction=validateOtherReportSameNameExists"
					+ "&isNew=" + $('#isNew').val();

		var data_ = JSON.stringify({
			action : "getReportList",
			data : allData,
			errorMsg : ""
		});

		// call...
		$.ajax({
			type : 'POST',
			data : data_,
			
			url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
			contentType : 'application/json',
			dataType : 'json',

			success : function(obj) {
				if (obj.errorMsg != null && obj.errorMsg != '') {
					displayAlertDialog(obj.errorMsg);
					return 0;
				} 
				else if(obj.data[0].val!="1"){// there exsists a report with
												// that name
					openConfirmDialog({
						onConfirm : function(){
							var reportName = $('#reportName').val();
							var reportDescription = $('#description').val();
							parent.$('#prevDialog').data('reportName',
									reportName);
							parent.$('#prevDialog').data('reportDescription',
									reportDescription);
							parent.$("#prevDialog iframe").attr('src','about:blank');
							parent.$('#prevDialog').dialog('close');},
						title : 'Warning',
						message : getSpringMessage(obj.data[0].val)
					});					
				}
				else
				{
					var reportName = $('#reportName').val();
					var reportDescription = $('#description').val();
					parent.$('#prevDialog').data('reportName',
							reportName);
					parent.$('#prevDialog').data('reportDescription',
							reportDescription);
					parent.$("#prevDialog iframe").attr('src','about:blank');
					parent.$('#prevDialog').dialog('close');
					return;
					
				}
				hideWaitMessage();
			},
			error : handleAjaxError
		});
 }
 
function navigateToReport(args){
	var reportName = args[0];
	showWaitMessage(getSpringMessage('pleaseWait'));
	
	var stringifyNameToPush = {
			code : "reportName",
			val : reportName,
			type : "AJAX_BEAN",
			info : 'na'
		};

	// get all data and add removeNameId
	var allData = getformDataNoCallBack(1);
	var allData = allData.concat(stringifyNameToPush);
	// url call
	var urlParam = "?formId="+ $('#formId').val()
				+ "&formCode="+ $('#formCode').val()
				+ "&userId="+ $('#userId').val()
				+ "&eventAction=getReportNameId"
				+ "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "getReportNameId",
		data : allData,
		errorMsg : ""
	});

	//make an ajax call that returns the nameId of the reportName&userId
	$.ajax({
		type : 'POST',
		data : data_,
		
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',

		success : function(obj) {
			if (obj.errorMsg != null && obj.errorMsg != '') {
				displayAlertDialog(obj.errorMsg);
				return 0;
			} 
			else if(obj.data[0].val!=""){
				var nameId = obj.data[0].val;
				var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=" + $('#formCode').val() + "&formId=-1" + "&userId=" + $('#userId').val() + '&PARENT_ID=' + $('#formId').val() + "&nameId=" + nameId;
				saveRulesTable(nameId,true,page)
				/*if (window.self === window.top){   
					window.location = page;
				}*/
			}
			hideWaitMessage();
		},
		error : handleAjaxError
	});
}

 
 function getReportList(elem,isRefresh,formCodeElement){
	 formCodeElement = formCodeElement !=undefined?formCodeElement:"ExpAnalysisReport";
	 	var $elem = $(elem);
		var ulChild = $elem.find('ul');
			
		if(ulChild !== undefined && ulChild.length > 0){// if the submenu has
			if(!isRefresh)											// already been built
				return;
			else $elem.find('ul').empty();
		}
			
		var reportMode = ($elem.hasClass('reportScheme-dropdown-submenu'))?'scheme':'design';
		var action="";
		var fromCodeElem="";
		var fromIdElem="";
		if(reportMode=="scheme")
			{
			action="getReportList";
			fromCodeElem=formCodeElement;
			fromIdElem="-1";
			}
		else 
			{
			action="getDesignReportList";
			fromCodeElem="ReportDesignExp";
			
			}
			
		console.log(reportMode);
		//TODO tamar: get appropriate report list by reportMode
		 var stringifyToPush = {
					code : "formCodeElement",
					val : formCodeElement,
					type : "AJAX_BEAN",
					info : 'na'
				}; 

		// get all data and add removeIndexId
		var allData = getformDataNoCallBack(1);
		var allData = allData.concat(stringifyToPush);
		// url call
		var urlParam = "?formId="+ $('#formId').val()
					+ "&formCode="+ $('#formCode').val()
					+ "&userId="+ $('#userId').val()
					+ "&eventAction="+action
					+ "&isNew=" + $('#isNew').val();

		var data_ = JSON.stringify({
			action : action,
			data : allData,
			errorMsg : ""
		});

		// call...
		$.ajax({
			type : 'POST',
			data : data_,
			async: false,
			url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
			contentType : 'application/json',
			dataType : 'json',

			success : function(obj) {
				if (obj.errorMsg != null && obj.errorMsg != '') {
					displayAlertDialog(obj.errorMsg);
				} 
				else
				{
					if(obj.data[0].length==0){
						//if(reportMode!="scheme"){//design
							//return;
						//}
					}
						
						
					var reportListCsv = JSON.parse(obj.data[0].val);
					if(reportListCsv.length==0){// there are no reports to display
						//if(reportMode!="scheme"){//design
							//return;
						//}
					}
					var toReturn = '<ul class="menu is-dropdown-submenu submenu opens-right vertical" data-submenu role="menu" style="z-index:1000;'+(reportListCsv.length>(reportMode=="scheme"?6:7)?'overflow-y:auto':'')+';max-height:250px">';// onmouseout=removeReportList(this)
					
					for(var i=0;i<reportListCsv.length;i++){
						if(reportMode=="scheme")
						{
						var href = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode="+fromCodeElem+"&formId="+fromIdElem+ "&userId=" + $('#userId').val() + '&PARENT_ID=' + $('#formId').val() + "&nameId=" + reportListCsv[i].Id;
						/*toReturn += '<li role="menuitem" class="is-submenu-item is-dropdown-submenu-item reportItem"><a  onclick="confirmWithOutSaveMainMenu(\''+href+'\')" id="'+reportListCsv[i].Id+'">'
									+reportListCsv[i].name + ' ('+reportListCsv[i].creation_date+')'
									+'</a><i class="fa-trash webix_icon" title="Delete report" style="position:absolute;float: right;right: 100%;left: 90%;top: '
												+(100/(reportListCsv.length+1)/5+(100/(reportListCsv.length+1))*i)+'%;" onclick=deleteReport('+reportListCsv[i].Id+',"'+reportMode+'")></i></li>';*/
						toReturn += '<li role="menuitem" class="is-submenu-item is-dropdown-submenu-item reportItem">'
							+'<a  style="width:85%;float:left" onclick="confirmWithOutSaveMainMenu(\''+href+'\')" id="'+reportListCsv[i].Id+'">'
						+reportListCsv[i].name + ' ('+reportListCsv[i].creation_date+')'
						+'</a>'
						+'<i class="fa-trash webix_icon" title="Delete report" style="position:relative;float: right;transform: translate(0%, 50%);" onclick=deleteReport('+reportListCsv[i].Id+',"'+reportMode+'")></i></li>';
						}
						else{
							/*toReturn += '<li role="menuitem" class="is-submenu-item is-dropdown-submenu-item reportItem"><a  onclick="openReportDesignScreenForEdit('+reportListCsv[i].Id+')" id="'+reportListCsv[i].Id+'">'
							+reportListCsv[i].name
							+'</a><i class="fa-trash webix_icon" title="Delete report" style="position:absolute;float: right;right: 100%;left: 90%;top: '
										+(100/reportListCsv.length/5+(100/reportListCsv.length)*i)+'%;" onclick=deleteReport('+reportListCsv[i].Id+',"'+reportMode+'")></i></li>';
										toReturn += '<li role="menuitem" class="is-submenu-item is-dropdown-submenu-item reportItem"><a  onclick="openReportDesignScreenForEdit('+reportListCsv[i].Id+')" id="'+reportListCsv[i].Id+'">'*/
							/*toReturn += '<li role="menuitem" class="is-submenu-item is-dropdown-submenu-item reportItem">'
								+'<div>'
								+'<a  onclick="openReportDesignScreenForEdit('+reportListCsv[i].Id+')" id="'+reportListCsv[i].Id+'" style="float:left;width:85%">'
								+reportListCsv[i].name
								+'</a>'
								+'<i class="fa-trash webix_icon" title="Delete report" style="position:relative;float: right;padding-top:0.5%"'
								+'onclick=deleteReport('+reportListCsv[i].Id+',"'+reportMode+'")></i>'
								+'</div></li>';*/
							toReturn += '<li role="menuitem" class="is-submenu-item is-dropdown-submenu-item reportItem">'
								+'<a  style="width:85%;float:left" onclick="openReportDesignScreenForEdit('+reportListCsv[i].Id+')" id="'+reportListCsv[i].Id+'">'
							+reportListCsv[i].name
							+'</a><i class="fa-trash webix_icon" title="Delete report" style="position:relative;float: right;transform: translate(0%, 50%);" onclick=deleteReport('+reportListCsv[i].Id+',"'+reportMode+'")></i></li>';
					}
						}
					if(reportMode=="scheme"){
						var href = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=ExpAnalyReportMain&formId="+fromIdElem+ "&userId=" + $('#userId').val() + '&PARENT_ID=' + $('#formId').val();
						toReturn += '<li role="menuitem" class="is-submenu-item is-dropdown-submenu-item reportItem">'
									+'<a style="float:left;width:100%" onclick="confirmWithOutSaveMainMenu(\''+href+'\')">'
									+'Public'
									+'</a>'
									+'</li>';
					}
					else{
						//var href = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=ReportDesignSearch&formId=-1&userId=" + $('#userId').val() + '&PARENT_ID=' + $('#formId').val();
						toReturn += '<li role="menuitem" class="is-submenu-item is-dropdown-submenu-item reportItem">'
									+'<a style="float:left;width:100%" onclick="openLoadDesignPopup(\'public\')">'
									+'Public'
									+'</a>'
									+'</li>';
					}

					toReturn += '</ul>';
					$(elem).addClass('opens-right is-dropdown-submenu-parent');
					// $(elem).parent('ul').addClass('dropdown');//for moving
					// right the next UL
					$(elem).append(toReturn);
					$(elem).mouseover();
				}
				hideWaitMessage();
				if(isRefresh)
					{
					if(reportMode=="scheme")
						{
				parent.$("#prevDialog iframe").attr('src',
				'about:blank');
		parent.$('#prevDialog').dialog('close');
						}
					else{
						  
						   setTimeout(function() {
								var reportDesignExpName_ = $('#reportDesignExpName')
										.val();
								if (reportDesignExpName_ !== 'undefined'&& reportDesignExpName_ != null&& reportDesignExpName_ != ''&& reportDesignExpName_ != '') {
									$('#pageTitle').html(
											'Report Design: ' + reportDesignExpName_);
								}
							}, 100);
					}
					
		
			}
			},
			error : handleAjaxError
		});
}

 function removeReportList(ulChild){
	// var ulChild = $(elem).find('ul');
	if(ulChild!== undefined && ulChild.length>0){// if the submenu has
													// already been built
	// if(!$(elem).hasClass('is-active')||!$(ulChild).hasClass('js-dropdown-active')){
		$(ulChild).parent('li').removeClass('is-dropdown-submenu-parent');
		$(ulChild).parent('li').removeClass('opens-right');
			$(ulChild).remove();// if the submenu is not currently opened,then
								// empties it and prepare it for rebuilding at
								// the next hovering,in order to get updated
								// reports
	// }
	}
}

function deleteReport(nameId,reportMode){
	openConfirmDialog({
		onConfirm : function(){deleteReportAfterConfirm(nameId,reportMode);},
		title : 'Warning',
		message : getSpringMessage('Are you sure you want to delete the report?')
	});
}

function deleteReportAfterConfirm(nameId,reportMode){
	var action="";
	if(reportMode=="scheme")
		action="deleteReport";
	else action="deleteReportDesign";
	var stringifyToPush = {
			code : "nameId",
			val : nameId,
			type : "AJAX_BEAN",
			info : 'na'
		};

	// get all data and add removeNameId
	var allData = getformDataNoCallBack(1);
	var allData = allData.concat(stringifyToPush);
	
	// url call
	var urlParam = "?formId="+ $('#formId').val()
				+ "&formCode="+ $('#formCode').val()
				+ "&userId="+ $('#userId').val()
				+ "&eventAction="+action
				+ "&isNew=" + $('#isNew').val();

	
	
	
	var data_ = JSON.stringify({
		action :action,
		data : allData,
		errorMsg : ""
	});
	showWaitMessage(getSpringMessage('pleaseWait'));

	// call...
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',

		success : function(obj) {
			if (obj.errorMsg != null && obj.errorMsg != '') {
				displayAlertDialog(obj.errorMsg);
			} 
			else
			{
				displayAlertDialog(getSpringMessage('Report was removed successfully'));
				removeReportList($('li #'+nameId).closest('ul'));
				// getReportList($('li #'+nameId).closest('ul').parent('li'));
			}
			hideWaitMessage();
		},
		error : handleAjaxError
	});
}
 
function onClickDepleteBatches(){
	// collect the selected records
	var csvList_ = [];
	$('input[class="dataTableApiSelectInfo"]:checked').each(function (index) {
		csvList_.push($(this).val());
    });
	
	// deplete the selected batches
 	openConfirmDialog({
        onConfirm: function(){
        	// update the quantity to be 0
        	depleteBatches(csvList_.toString());
        },
        title: 'Warning',
        message: getSpringMessage('confirmDepletedBatch')
    });
}

function depleteBatches(batchList){
	var stringifyToPush = {
			code : "batchList",
			val : batchList,
			type : "AJAX_BEAN",
			info : 'na'
		}
	var allData = getformDataNoCallBack(1);
	var allData = allData.concat(stringifyToPush);
	/*
	 * var allData = { code : "batchList", val : batchList, type : "AJAX_BEAN",
	 * info : 'na' }
	 */
	// url call
	var urlParam = "?formId="+ $('#formId').val()
				+ "&formCode="+ $('#formCode').val()
				+ "&userId="+ $('#userId').val()
				+ "&eventAction=depleteBatches"
				+ "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "depleteBatches",
		data : allData,
		errorMsg : ""
	});
	showWaitMessage(getSpringMessage('pleaseWait'));

	// call...
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',

		success : function(obj) {
			if (obj.errorMsg != null && obj.errorMsg != '') {
				displayAlertDialog(obj.errorMsg);
			} 
			else
			{
				$('#Batches_value').val('');
				var table_ = $('#Batches').DataTable(); 
	        	// unselect all the records
				table_.$('input[Type="checkbox"]').each(function (index) {
	        		if($(this).prop('checked')) {
	        			$(this).prop('checked',false);
	        		}
	            }); 
				displayAlertDialog(getSpringMessage('The batches were depleted successfully'));				
				onElementDataTableApiChange('Batches');
			}
			hideWaitMessage();
		},
		error : handleAjaxError
	});
}

function onClickDeleteSteps(){
	// collect the selected records
	var table_ = $('#steps').DataTable(); 
	var csvList_ = $('#steps_value').val()==""?[]:$('#steps_value').val().split(',');
	table_.$('input[Type="checkbox"]').each(function (index) {
    	if((',' + $('#steps_value').val() + ',').indexOf(',' + $(this).val() + ',') <= -1) {
    		if($(this).prop('checked')) {
    			csvList_.push($(this).val());
    		}
    	} else {
    		if(!$(this).prop('checked')) {
    			// remove val from array
    			const index = csvList_.indexOf($(this).val());
    			csvList_.splice(index, 1);
    		}
    	}
    });
	
	// deplete the selected batches
 	openConfirmDialog({
        onConfirm: function(){
        	// update the quantity to be 0
        	deleteSteps(csvList_.toString());
        },
        title: 'Warning',
        message: getSpringMessage('confirmDeletedStep')
    });
}

function deleteSteps(stepList){
	var stringifyToPush = {
			code : "stepList",
			val : stepList,
			type : "AJAX_BEAN",
			info : 'na'
		}
	var allData = getformDataNoCallBack(1);
	var allData = allData.concat(stringifyToPush);
	
	// url call
	var urlParam = "?formId="+ $('#formId').val()
				+ "&formCode="+ $('#formCode').val()
				+ "&userId="+ $('#userId').val()
				+ "&eventAction=deleteSteps"
				+ "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "deleteSteps",
		data : allData,
		errorMsg : ""
	});
	showWaitMessage(getSpringMessage('pleaseWait'));

	// call...
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',

		success : function(obj) {
			if (obj.errorMsg != null && obj.errorMsg != '') {
				displayAlertDialog(obj.errorMsg);
			} 
			else
			{
				$('#steps_value').val('');
				var table_ = $('#steps').DataTable(); 
	        	// unselect all the records
				table_.$('input[Type="checkbox"]').each(function (index) {
	        		if($(this).prop('checked')) {
	        			$(this).prop('checked',false);
	        		}
	            }); 
				displayAlertDialog(getSpringMessage('The steps were deleted successfully.</br>'
						+($('#formCode').val() == 'Experiment'?"Summary ":"Overview ")+ "data will be updated after refresh."));				
				onElementDataTableApiChange('steps');
				if($('#formCode').val() == 'ExperimentCP'){
					onElementDataTableApiChange('expRunPlanningTable');
				}
				/*
				 * if($('#formCode').val() == 'Experimnet'){
				 * onElementDataTableApiChange('reactionTable'); } else
				 * {//formulation
				 * onElementDataTableApiChange('formulantsByStep'); }
				 */
			}
			hideWaitMessage();
		},
		error : handleAjaxError
	});
}


function onClickCancelMaterial(formId){

	var allData = [{
			code : "cancelledId",
			val : formId,
			type : "AJAX_BEAN",
			info : 'na'
		}];
	/*
	 * var allData = getformDataNoCallBack(1); var allData =
	 * allData.concat(stringifyToPush); var allData = { code : "batchList", val :
	 * batchList, type : "AJAX_BEAN", info : 'na' }
	 */
	
	// url call
	var urlParam = "?formId="+ $('#formId').val()
				+ "&formCode="+ $('#formCode').val()
				+ "&userId="+ $('#userId').val()
				+ "&eventAction=cancelMaterial"
				+ "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "cancelMaterial",
		data : allData,
		errorMsg : ""
	});
	showWaitMessage(getSpringMessage('pleaseWait'));

	// call...
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',
		success : function(obj) {
			hideWaitMessage();
			if (obj.errorMsg != null && obj.errorMsg != '') {
				displayAlertDialog(obj.errorMsg);
			} else {
				displayAlertDialog(getSpringMessage('The material was cancelled successfully'));				
				onElementDataTableApiChange("upperTable");
			}
		},
		error : handleAjaxError
	});
}

function openMaterialDuplicatesForm(itemsId)
{
	var dialogWidth = $(window).width() - 8;
    var dialogHeight = $(window).height() - 10;
	var parentId = $('#formId').val();
	var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=MaterialDuplicates&formId=-1&userId="
			+ $('#userId').val() + '&tableType=&PARENT_ID='+ parentId 
			+ "&materialDuplicatesID=" + itemsId;

	// open iframe inside dialog
	var $dialog = $('<div id="prevDialog" style="overflow-y: hidden;""></div>')
			.html('<iframe style="border: 0px;width:100%;height:100%" src="'
					+ page + '"></iframe>')
			.dialog({
				autoOpen : false,
				modal : true,
				height : dialogHeight,
				width : dialogWidth,
				// title: title,
				close : function() {
					$('#prevDialog iframe').attr('src', 'about:blank');
					$('#prevDialog').remove();
				}
			});

	$dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
}

function openMultiStepCopy(){
	var dialogWidth = $(window).width() - 10;
    var dialogHeight = $(window).height();
	var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=MultiStep&formId=-1&userId=" + $('#userId').val() + '&PARENT_ID=' + $('#formId').val();
    // open iframe inside dialog
    var $dialog = $('<div id="prevDialog" style="overflow-y: hidden;""></div>')
        .html('<iframe style="border: 0px;width:100%;height:100%" src="' + page + '"></iframe>')
        .dialog({
            autoOpen: false,
            modal: true,
            height: dialogHeight,
            width: dialogWidth,
            // title: title,
            close: function () {
                $('#prevDialog iframe').attr('src', 'about:blank');
                var cml=$('#prevDialog').data('cml');
                $('#prevDialog').remove();
                onElementDataTableApiChange('reactants');
                onElementDataTableApiChange('solvents');
                if(typeof cml!=='undefined' && cml!="-1"){
	                // sync up the reaction scheme
					MarvinJSUtil.getEditor("#marvin_js").then(
							  function(sketcherReactionInstance) {
							   var source = sketcherReactionInstance.exportStructure("mrv").then(function(source)
									   {
									     marvinSketcherInstance.importStructure("mrv", cml).catch(function(error) {
									    	 alert(error);
										  })
									   });
						   
							  },
							  function(error) {
							   alert("Cannot retrieve sketcher instance from iframe");
							  });
	            }
            }
        });

    $dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
    return;
}

/**
 * csvList_-list if formId's(materialref_id) to copy reaction data from
 * stepId- step formId to copy the data to
 * doCopyFromLastPreparation - true/undefined if the function was invoked by MultiStep form and should close it, else-false.
 */
function copyMultiStep(csvList_,stepId,doCopyFromLastPreparation){
	var allData = [{
			code : "materialref_id_list",
			val : csvList_,
			type : "AJAX_BEAN",
			info : 'na'
		}];
	// url call
	var urlParam = "?formId="+ stepId
				+ "&formCode=Step"
				+ "&userId="+ $('#userId').val()
				+ "&eventAction=copyMultiStep"
				+ "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "copyMultiStep",
		data : allData,
		errorMsg : ""
	});
	showWaitMessage(getSpringMessage('pleaseWait'));

	// call...
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',

		success : function(obj) {
			if (obj.errorMsg != null && obj.errorMsg != '') {
				displayAlertDialog(obj.errorMsg);
			} 
			else
			{
				displayAlertDialog(getSpringMessage('The reaction was overriden by the '+(doCopyFromLastPreparation!=undefined&& doCopyFromLastPreparation==true?"last preparation":'selected')+' materials successfully'));				
				if(doCopyFromLastPreparation!=undefined && doCopyFromLastPreparation==true){
					var cml = obj.data[0].val;
					onElementDataTableApiChange('reactants');
	                onElementDataTableApiChange('solvents');
	                if(cml!="-1"){
		                // sync up the reaction scheme
						MarvinJSUtil.getEditor("#marvin_js").then(
								  function(sketcherReactionInstance) {
								   var source = sketcherReactionInstance.exportStructure("mrv").then(function(source)
										   {
										     marvinSketcherInstance.importStructure("mrv", cml).catch(function(error) {
										    	 alert(error);
											  })
										   });
							   
								  },
								  function(error) {
								   alert("Cannot retrieve sketcher instance from iframe");
								  });
	                }
                } else {
					parent.$('#prevDialog').data('cml',obj.data[0].val);
					parent.$('#prevDialog').dialog('close');
                }
			}
			hideWaitMessage();
		},
		error : handleAjaxError
	});
}

function executeMultiStepCopy(domId){
	// parent.$('#prevDialog').dialog('close');
	csvList_ = [];
	var lastMultiValues = $('#' + domId + '_value').val(); // get the values
															// from
															// lastMultiValues
															// that may contain
															// selection that
															// not appear in
															// this filter
															// (criteria) table
	if(lastMultiValues.length > 0) {
		csvList_ = lastMultiValues.split(',');
	} 
	var table_ = $('#' + domId).DataTable(); 
    
	// add selected values that are not in lastMultiValues + remove unselected
	// values that are in this table (=> the user unselected them)
    table_.$('input[Type="checkbox"]').each(function (index) {
    	if((',' + lastMultiValues + ',').indexOf(',' + $(this).val() + ',') <= -1) {
    		if($(this).prop('checked')) {
    			csvList_.push($(this).val());
    		}
    	} else {
    		if(!$(this).prop('checked')) {
    			// remove val from array
    			const index = csvList_.indexOf($(this).val());
    			csvList_.splice(index, 1);
    		}
    	}
    });
    if(csvList_.toString()!=''){
    	openConfirmDialog({
			onConfirm : function(){copyMultiStep(csvList_.toString(),$('#parentId').val());},
			title : 'Warning',
			message : getSpringMessage('confirmCopyMultiStep'),
			onCancel : function(){parent.$('#prevDialog').dialog('close');}
		});
    } else {// close the dialog if no material was chosen
    	parent.$('#prevDialog').data('cml',"-1");
    	parent.$('#prevDialog').dialog('close');
    }
}

function stopQuery(){
	var allData = [];
	// url call
	var urlParam = "?formId="+ $('#formId').val()
				+ "&formCode="+$('#formCode').val()
				+ "&userId="+ $('#userId').val()
				+ "&eventAction=stopQuery"
				+ "&isNew=" + $('#isNew').val();
	
	var data_ = JSON.stringify({
		action : "stopQuery",
		data : allData,
		errorMsg : ""
	});
	showWaitMessage(getSpringMessage('pleaseWait'));
	
	// call...
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',
	
		success : function(obj) {
			if (obj.errorMsg != null && obj.errorMsg != '') {
				displayAlertDialog(obj.errorMsg);
			}
			hideWaitMessage();
		},
		error : handleAjaxError
	});
}

// function clearSearch(){
// location.reload();
// }

function updateSolventTable(){
	var selectedId = "";// the row to be filled with the current material data
	var selectedTable = $('#solvents').DataTable();
	var custid = selectedTable.row('.selected').data();
	if (typeof custid !== 'undefined') {
		selectedId = custid[0];
	}
	var materialSearchReturnJson = $('#materialSearchReturnJson').val();	
	var batchSearchReturnJson = $('#batchSearchReturnJson').val();
	if(materialSearchReturnJson == ''){
		return;
	}
	var materialData = funcParseJSONData(materialSearchReturnJson);
	var matBatchObj = materialData["batch_list"];
	var purity = "";
	var purityuom_id = "";
	if(batchSearchReturnJson != ''){
		var batchData = funcParseJSONData(batchSearchReturnJson);
		matBatchObj["displayName"] = [{"ID":batchData['invitembatch_id'],"displayName":batchData['batch_name']}];
		purity = batchData['purity'];
		purityuom_id = batchData['purityuom_id'];		
	}
	// set default purity
	if (purity == "") {
		purity = '100';
		purityuom_id = '%';
	}
	var _objData =  {
						"material_name":materialData['material_name'],			
						"invitemmaterial_id":materialData['invitemmaterial_id'],		
						"casnamberinf":materialData['casNumber'],
						"casName":materialData['casName'], 					
						"formulainf":materialData['formula'],
						"densityinf":materialData['density'],
						"densityuom_id_inf":materialData['densityuom_id'], 
						"mwinf":materialData['mw'],
						"mwuom_val":materialData['mwuom'],
						"MWUOM_ID_INF":materialData['mwoum_id'], // this
																	// value
																	// update
																	// hidden
																	// row
																	// params
						"iupac_name":materialData['iupac_name'], 
						"MSDS":materialData['MSDS'],
						"batch_list":matBatchObj,
						"purityinf":purity,
						"purityuom_id_inf":purityuom_id
					}
							
	console.log(_objData);
	var newJSONForUpdate = {};
	if(selectedId == "")
	{
		var newRowId = dataTableAddRow("solvents");
		newJSONForUpdate[newRowId] = _objData;
		console.log(JSON.stringify(newJSONForUpdate));
		onMaterialChangeUpdateRowEditTable("solvents", newJSONForUpdate);
	}
	else
	{
		newJSONForUpdate[selectedId] = _objData;
		console.log(JSON.stringify(newJSONForUpdate));
		onMaterialChangeUpdateRowEditTable("solvents", newJSONForUpdate);
	}
	$('#materialSearchReturnJson').val("");
	$('#batchSearchReturnJson').val("");	
}  

function updateProductTable(){
	var selectedId = "";// the row to be filled with the current material data
	var selectedTable = $('#products').DataTable();
	var custid = selectedTable.row('.selected').data();
	if (typeof custid !== 'undefined') {
		selectedId = custid[0];
	}
	
	var materialSearchReturnJson = $('#materialSearchReturnJson').val();	
	console.log(materialSearchReturnJson);
	if(materialSearchReturnJson == ''){
		return;
	}
	
	var materialData = funcParseJSONData(materialSearchReturnJson);	
	var _objData =  {
						"material_name":materialData['material_name'],			
						"invitemmaterial_id":materialData['invitemmaterial_id'],		
						"casnamberinf":materialData['casNumber'],
						"casName":materialData['casName'], 					
						"formulainf":materialData['formula'],
						"densityinf":materialData['density'],
						"densityuom_id_inf":materialData['densityuom_id'], 
						"mwinf":materialData['mw'],
						"mwuom_val":materialData['mwuom'],
						"MWUOM_ID_INF":materialData['mwoum_id'], // this
																	// value
																	// update
																	// hidden
																	// row
																	// params
						"iupac_name":materialData['iupac_name'], 
						"MSDS":materialData['MSDS']
					}
							
	console.log(_objData);
	var newJSONForUpdate = {};
	if(selectedId == "")
	{
		var newRowId = dataTableAddRow("products");
		newJSONForUpdate[newRowId] = _objData;
		console.log(JSON.stringify(newJSONForUpdate));
		onMaterialChangeUpdateRowEditTable("products", newJSONForUpdate);
	}
	else
	{
		newJSONForUpdate[selectedId] = _objData;
		console.log(JSON.stringify(newJSONForUpdate));
		onMaterialChangeUpdateRowEditTable("products", newJSONForUpdate);
	}
	$('#materialSearchReturnJson').val("");		
}
function updateComponentTable(){
	var selectedId = "";// the row to be filled with the current material data
	var selectedTable = $('#components').DataTable();
	var custid = selectedTable.row('.selected').data();
	if (typeof custid !== 'undefined') {
		selectedId = custid[0];
	}
	
	var materialSearchReturnJson = $('#materialSearchReturnJson').val();	
	console.log(materialSearchReturnJson);
	if(materialSearchReturnJson == ''){
		return;
	}
	
	var materialData = funcParseJSONData(materialSearchReturnJson);	
	var _objData =  {
			"material_id":materialData['invitemmaterial_id'],			
						
					}
	 var stringifyToPush1 = {
			code : 'material_id',
			val : materialData['invitemmaterial_id'],
			type : "AJAX_BEAN",
			info : 'na'
		};
 				
	console.log(_objData);
	var newJSONForUpdate = {};
	var allData = getformDataNoCallBack(1);
	allData = allData.concat(stringifyToPush1);	
	
	// url call
	var urlParam = "?formId="+ $('#formId').val()
				+ "&formCode="+ $('#formCode').val()
				+ "&userId="+ $('#userId').val()
				+ "&eventAction=updateComponentTable"
				+ "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "updateComponentTable",
		data : allData,
		errorMsg : ""
	});

	// call...
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',

		success : function(obj) {
			if (obj.errorMsg != null && obj.errorMsg != '') {
				displayAlertDialog(obj.errorMsg);
			} 
			else
			{
				
				onElementDataTableApiChange("components");
			}

			hideWaitMessage();
		},
		error : handleAjaxError
	});
	/*if(selectedId == "")
	{
		var newRowId = dataTableAddRow("components");
		newJSONForUpdate[newRowId] = _objData;
		console.log(JSON.stringify(newJSONForUpdate));
		onMaterialChangeUpdateRowEditTable("components", newJSONForUpdate);
	}
	else
	{
		newJSONForUpdate[selectedId] = _objData;
		console.log(JSON.stringify(newJSONForUpdate));
		onMaterialChangeUpdateRowEditTable("components", newJSONForUpdate);
	}*/
	$('#materialSearchReturnJson').val("");		
}
function updateReactantTable(){
	var selectedId = "";// the row to be filled with the current material data
	var selectedTable = $('#reactants').DataTable();
	var custid = selectedTable.row('.selected').data();
	if (typeof custid !== 'undefined') {
		selectedId = custid[0];
	}
	
	var materialSearchReturnJson = $('#materialSearchReturnJson').val();	
	console.log("data from search popup: ",materialSearchReturnJson);
	var batchSearchReturnJson = $('#batchSearchReturnJson').val();
	if(materialSearchReturnJson == ''){
		return;
	}
	var materialData = funcParseJSONData(materialSearchReturnJson);	
	var matBatchObj = materialData["batch_list"];
	var purity = "";
	var purityuom_id = "";	
	if(batchSearchReturnJson != ''){
		var batchData = funcParseJSONData(batchSearchReturnJson);		
		matBatchObj["displayName"] = [{"ID":batchData['invitembatch_id'],"displayName":batchData['batch_name']}];
		purity = batchData['purity'];
		purityuom_id = batchData['purityuom_id'];		
	}
	// set default purity
	if (purity == "") {
		purity = '100';
		purityuom_id = '%';
	}
	var _objData =  {
						"material_name":materialData['material_name'],			
						"invitemmaterial_id":materialData['invitemmaterial_id'],		
						"casnamberinf":materialData['casNumber'],
						"casName":materialData['casName'], 					
						"formulainf":materialData['formula'],
						"densityinf":materialData['density'],
						"densityuom_id_inf":materialData['densityuom_id'], 
						"mwinf":materialData['mw'],
						"mwuom_val":materialData['mwuom'],
						"MWUOM_ID_INF":materialData['mwoum_id'], // this
																	// value
																	// update
																	// hidden
																	// row
																	// params
						"iupac_name":materialData['iupac_name'], 
						"MSDS":materialData['MSDS'],
						"batch_list":matBatchObj,
						"purityinf":purity,
						"purityuom_id_inf":purityuom_id
					}
							
	var newJSONForUpdate = {};
	if(selectedId == "")
	{
		var newRowId = dataTableAddRow("reactants");
		newJSONForUpdate[newRowId] = _objData;
		console.log(JSON.stringify(newJSONForUpdate));
		onMaterialChangeUpdateRowEditTable("reactants", newJSONForUpdate);
	}
	else
	{
		newJSONForUpdate[selectedId] = _objData;
		console.log(JSON.stringify(newJSONForUpdate));
		onMaterialChangeUpdateRowEditTable("reactants", newJSONForUpdate);
	}
	$('#materialSearchReturnJson').val("");
	$('#batchSearchReturnJson').val("");
}

function onEditableClickRemoveMaterial(input){
	var divId = $(input).parent().attr('id');
    var domId = divId.substring(0, divId.indexOf('_'));
    var struct = $('[id="' + domId + '_structCatalogItem"]').val();
    var selectedTable = $('#' + domId).DataTable();
    var custid = selectedTable.row('.selected').data();
    var formId = "";
    if (typeof custid !== 'undefined') {
        formId = custid[0];
    }
    // var isChecked =
	// $("#"+domId+'_col_limitingagent_row_'+formId).prop('checked');
    if(Number(formId) < 0)// remove row that is exists on client side only
							// (not yet saved in DB)
    {
    	
    	selectedTable.row('.selected').remove().draw( false );
    	bl_elementDatatableEditableCustomFuncHandler(domId, "onTableRowRemoved");
    }
    else
    {
		deleteRowElementDataTableApiImp(input);
    }
}

function notifMessageReadAll(){
		// get all data and add removeIndexId
		var allData = getformDataNoCallBack(1);
		
		// url call
		var urlParam = "?formId="+ $('#formId').val()
					+ "&formCode="+ $('#formCode').val()
					+ "&userId="+ $('#userId').val()
					+ "&eventAction=notifMessageReadAll"
					+ "&isNew=" + $('#isNew').val();

		var data_ = JSON.stringify({
			action : "doSave",
			data : allData,
			errorMsg : ""
		});

		// call...
		$.ajax({
			type : 'POST',
			data : data_,
			url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
			contentType : 'application/json',
			dataType : 'json',

			success : function(obj) {
				if (obj.errorMsg != null && obj.errorMsg != '') {
					displayAlertDialog(obj.errorMsg);
				} 
				else
				{
					updateMenuItemUI(obj.data[0].val);
					onElementDataTableApiChange("summary");
				}

				hideWaitMessage();
			},
			error : handleAjaxError
		});
}
function calcMaterialCasNumber(){
	//	var urlParam =
//        "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + '&userId=' + $('#userId').val() + '&casNumber=' + $('#casNumber').val();
	showWaitMessage(getSpringMessage('pleaseWait'));
	if($('#casNumber').val() == null || $.trim($('#casNumber').val()).length == 0 || $('#casNumber').val().replace('-','').length == 0) {
		hideWaitMessage();
		displayAlertDialog("Please enter CAS Number!");
		return;
		}
	
//	var casUrl = getSysProp("casApiUrl","https://skyline.comply.co.il/cas/substance/pt/");//https://ilhqwtvskyapp/cas/substance/pt/ --> use constant url based on location.host (domain) - needs cas war on the same tomcat!
	var casUrl = location.protocol + '//' + location.host + '/' + 'cas/substance/pt/';
		 
 	$.ajax({
	        type: 'GET',
	        // data: data_,
	        url: casUrl + $('#casNumber').val().replace(/\-/g, ''),
	        contentType: 'application/json',
	        dataType: 'json',
	        success: function (obj1) {
	        	////////kd 25112019
	        	//var obj_ = obj;
	        	/////////////////////////TODO remove this row
	        	/*var obj_ = [{ //TODO remove this row
					code : 'quantityOfRuns',
					val : obj1.count, //2, //quantityOfRuns,
					type : "AJAX_BEAN",
					info : 'na'
				}];*/
	        	//////////////////////////TODO remove this row
			var substance = [{ 
					code : 'quantityOfRuns',
					val : JSON.stringify(obj1.substance), //2, //quantityOfRuns,
					type : "AJAX_BEAN",
					info : 'na'
				}];
	        	var action = 'saveCasObjToLog';
	    		var urlParam = "?formId=" + $('#formId').val()
	    		/*+ "&formCode=" + $('#formCode').val()*/ 
				+ "&userId=" + $('#userId').val()
				+ "&eventAction=" + action;// + "&isNew=" + $('#isNew').val();

				var data_ = JSON.stringify({
					action : "doSave",
					//data : allData,
					data : substance, 
					errorMsg : ""
				});
				$.ajax({
					type : 'POST',
					data : data_,
					url : "./generalEvent.request" + urlParam, // + "&stateKey=" + $('#stateKey').val(),
					contentType : 'application/json; charset=utf-8',
					dataType : 'json',
					success : function(obj) {
			        	/////////end kd 25112019
			            var element, key;
			            if (obj1.errorMsg != null && obj1.errorMsg != '') {
			                displayAlertDialog(obj1.errorMsg);
			            } else {
							if (obj.data[0].val != 1 ) {
								displayAlertDialog(  'There is error on Save to DB Log. Obj is: ' +  obj.toString);
							}
							if(obj1.count <= 0 ) {
								displayAlertDialog("No result found for this request!");
							} else if(obj1.count == 1 ) {
								setMaterialCalcObj(obj1);
							} else {
								console.log('obj1: ' +  obj1.toString);
								console.log('obj: ' +  obj.toString);
								displayAlertDialog(obj1 +  "More then one result found for this request!");
							}
			            }
			            hideWaitMessage();
			        },
			        error: function() {
						hideWaitMessage();
						displayAlertDialog(getSpringMessage('Save to Log Failed'));
					}
			    });
	        },
	        error: getOauth2FromCas
	    });
 }
/*
 * old
 */
/*function calcMaterialCasNumber(){
//	var urlParam =
//        "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + '&userId=' + $('#userId').val() + '&casNumber=' + $('#casNumber').val();
	showWaitMessage(getSpringMessage('pleaseWait'));
	if($('#casNumber').val() == null || $.trim($('#casNumber').val()).length == 0 || $('#casNumber').val().replace('-','').length == 0) {
		hideWaitMessage();
		displayAlertDialog("Please enter CAS Number!");
		return;
		}
	
//	var casUrl = getSysProp("casApiUrl","https://skyline.comply.co.il/cas/substance/pt/");//https://ilhqwtvskyapp/cas/substance/pt/
//		 
// 	$.ajax({
//	        type: 'GET',
//	        // data: data_,
//	        url: casUrl + $('#casNumber').val().replace(/\-/g, ''),
//	        contentType: 'application/json',
//	        dataType: 'json',
//	        success: function (obj) {
//	        	////////kd 25112019
//	        	var obj_ = obj;
	        	/////////////////////////TODO remove this row
	
				if (obj_ != undefined) {
					obj_ = obj_.substance.molfiles;
				}
	        	var obj_ = [{ //TODO remove this row
					code : 'quantityOfRuns',
					val : "{\"count\":1,\"substance\":{\"uri\":\"substance/pt/75092\",\"casRn....}}",
//					val : 2, //quantityOfRuns,
					type : "AJAX_BEAN",
					info : 'na'
				}];
	        	//////////////////////////TODO remove this row
	        	var action = 'saveCasObjToLog';
	    		var urlParam = "?formId=" + $('#formId').val()
	    		+ "&formCode=" + $('#formCode').val() 
				+ "&userId=" + $('#userId').val()
				+ "&eventAction=" + action;// + "&isNew=" + $('#isNew').val();

				var data_ = JSON.stringify({
					action : "doSave",
					//data : allData,
					data : obj_, 
					errorMsg : ""
				});
				$.ajax({
					type : 'POST',
					data : data_,
					url : "./generalEvent.request" + urlParam, // + "&stateKey=" + $('#stateKey').val(),
					contentType : 'application/json; charset=utf-8',
					dataType : 'json',
					success : function(obj) {
			        	/////////end kd 25112019
			            var element, key;
			            if (obj.errorMsg != null && obj.errorMsg != '') {
			                displayAlertDialog(obj.errorMsg);
			            } else {
							if(obj.count <= 0 ) {
								displayAlertDialog("No result found for this requet!");
							} else if(obj.count == 1 ) {
								setMaterialCalcObj(obj);
							} else {
								console.log('obj_: ' +  obj.toString);
								console.log('obj: ' +  obj.toString);
								displayAlertDialog(obj +  "More then one result found for this request!");
							}
			            }
			            hideWaitMessage();
			        },
			        error: function() {
						hideWaitMessage();
						displayAlertDialog(getSpringMessage('Save to Log Failed'));
					}
			    });
//	        },
//	        error: getOauth2FromCas
//	    });
 }
*/

function setMaterialCalcObj(obj){
	var message = "";
	// chem structure
	marvinSketcherInstance.importStructure("mol", obj.substance.molfiles[0]).catch(function(error) {
		   alert(error);
		  });
	// chemicalFormula
	if(obj.substance.molecularFormula != undefined && obj.substance.molecularFormula != null){
		$('#chemicalFormula').val(obj.substance.molecularFormula.replace(/<\/?[^>]+(>|$)/g,''));
	}else{
		message +="- Chemical Formula not found <br>";
	}
	// casName
	if(obj.substance.name != undefined  && obj.substance.name !=null){
		$('#casName').val(obj.substance.name.replace(/<\/?[^>]+(>|$)/g,''));
	}else{
		message +="- Cas Name not found <br>";
	}
	// synonyms
	if(obj.substance.synonyms != undefined && obj.substance.synonyms !=null ){
//		$('#synonyms').val(obj.substance.synonyms.slice(0, 3).join('\r').replace(/<sub>/g,'').replace(/<\/sub>/g,'').replace(/<em>/g,'').replace(/<\/em>/g,'')); // yp 31122019 fix bug - 7712 (remove the limitation of first 3 synonyms) ->
		$('#synonyms').val(obj.substance.synonyms.join('\r').replace(/<\/?[^>]+(>|$)/g,''));
	} else {
		message +="- synonyms not found <br>";
	}
	// mw
	if(obj.substance.molecularWeight != undefined && obj.substance.molecularWeight != null ){
		$('#mw').val(obj.substance.molecularWeight);
		$("#MW_UOM_ID").val($("#MW_UOM_ID option:contains('gr/mole')").val());
	    $("#MW_UOM_ID").trigger('chosen:updated');
	}else{
		message +="- MW not found <br>";
	}
	//boilingPoint
	if(obj.substance.properties.boilingPoint != undefined && obj.substance.properties.boilingPoint.displayValue != null ){
    	var boilingPoint = obj.substance.properties.boilingPoint.displayValue.split(" ");
		var boilingPoint_uom = getUom(boilingPoint[1]);
		if(boilingPoint_uom != ""){
			$('#boilingPoint').val(boilingPoint[0]);
			$("#BoilingPoint_uom").val($("#BoilingPoint_uom option:contains('"+boilingPoint_uom+"')").val());
		    $("#BoilingPoint_uom").trigger('chosen:updated');
		    }
		else{
			message += "- The calculation returned "+obj.substance.properties.boilingPoint.displayValue+". Boiling Point uom not found <br>";
		}
	}else{
		message +="- Boiling Point not found <br>";
	}
	//density
    if(obj.substance.properties.density!= undefined && obj.substance.properties.density.displayValue != null ){
    	var density = obj.substance.properties.density.displayValue.split(" ");
		var density_uom = getUom(density[1]);
		if(density_uom != ""){
			$('#density').val(density[0]);
			$("#DENSITY_UOM_ID").val($("#DENSITY_UOM_ID option:contains('"+density_uom+"')").val());
		    $("#DENSITY_UOM_ID").trigger('chosen:updated');
		    }
		else{
			message += "- The calculation returned "+obj.substance.properties.density+" Density uom not found <br>";
		}
	}else{
		message +="- Density Point not found <br>";
	}
    //meltingPoint
    if(obj.substance.properties.meltingPoint!= undefined && obj.substance.properties.meltingPoint.displayValue != null ){
    	var meltingPoint = obj.substance.properties.meltingPoint.displayValue.split(" ");
    	var meltingPoint_uom = getUom(meltingPoint[1]);
    	if(meltingPoint_uom != ""){
    		$('#meltingPoint').val(meltingPoint[0]);
    		$("#MELTINGPOINT_UOM").val($("#MELTINGPOINT_UOM option:contains('"+meltingPoint_uom+"')").val());
    		$("#MELTINGPOINT_UOM").trigger('chosen:updated');
    		}else{
    			message += "- The calculation returned "+obj.substance.properties.meltingPoint.displayValue+" Melting Point uom not found <br>";
    			}
    	}else{
    		message +="- Melting Point not found <br>";
    	}	
    if(message != ""){
    	displayAlertDialog(getSpringMessage(message));
    }
}

function getUom(uom){
    var returnUOM = "";
    if(uom.trim() =="°C"){
		returnUOM = "oC";
	}else if(uom.trim() == "g/cm<sup>3</sup>"){
		returnUOM = "gr/cm^3";
	}
    return returnUOM;	
}

function getOauth2FromCas(xhr, status, error) {
	hideWaitMessage();
//	var url = getSysProp('casUrlLogin','https://skyline.comply.co.il/cas/substance/loginMsg/You are successfully logged in !');
//	window.open(url); //"https://ilhqwtvskyapp/cas/substance/loginMsg/You are successfully logged in !" -- use constant url based on location.host (domain) - needs cas war on the same tomcat!
	window.open(location.protocol + '//' + location.host + '/' + 'cas/substance/loginMsg/You are successfully logged in!', "_blank", "toolbar=no, resizable=yes,width=500,height=500");
}

function createNewSampleFromSelfTest() {
	 doNew('Sample'); //TODO arrange the new in the system!
}

function insertSelfTestSampleAfterSearch() { 
	if($("#sampleId option:contains('"+$('#sampleNumber').val()+"')").val() == undefined) {
		$("#sampleId").append("<option value=\"" + $('#lastSampleSearchSelect').val() + "\" selected>" + $('#sampleNumber').val() + "</option>");
		
		//sort
		var my_options = $("#sampleId option");
		my_options.sort(function(a,b) {
		    if (a.text > b.text) return 1;
		    else if (a.text < b.text) return -1;
		    else return 0
		})
		$("#sampleId").empty().append(my_options);
		
		//update
		$("#sampleId").trigger('chosen:updated');
	} else {
		$("#sampleId").val($("#sampleId option:contains('"+$('#sampleNumber').val()+"')").val());
		$("#sampleId").trigger('chosen:updated');
	}
	
	//make ajax call after change (to disable wexix autz)
	onChangeAjax('sampleId');
}

function importRecipeToFormulantRef() {
	// not in use
//	//check if Recipe selected
//	if(($('#RECIPE_ID').val() == null || $('#RECIPE_ID').val() == '')) {
//		displayAlertDialog("Please select a recipe!");
//		return;
//	}
//	// get all
//	var allData = getformDataNoCallBack(1);
//	
//	// url call
//	var urlParam = "?formId="+ $('#formId').val()
//				+ "&formCode="+ $('#formCode').val()
//				+ "&userId="+ $('#userId').val()
//				+ "&eventAction=importRecipeToFormulantRef"
//				+ "&isNew=" + $('#isNew').val();
//
//	var data_ = JSON.stringify({
//		action : "doSave",
//		data : allData,
//		errorMsg : ""
//	});
//
//	// call...
//	$.ajax({
//		type : 'POST',
//		data : data_,
//		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
//		contentType : 'application/json',
//		dataType : 'json',
//
//		success : function(obj) { 
//			if (obj.errorMsg != null && obj.errorMsg != '') {
//				displayAlertDialog(obj.errorMsg);
//			} else {
//				onElementDataTableApiChange('formulants'); // refresh the table with the new materials
//				return;// the retun is for having the please wait until the DT refresh is done (it will hide it)
//			}
//			hideWaitMessage();
//		},
//		error : handleAjaxError
//	});
}
function executeSQLGenerator() {
	//check if some query is selected
	if(($('#sqlText').val() == null || $('#sqlText').val() == '')) {
		displayAlertDialog("Please type your query.");
		return;
	}
	// get all
	var allData = getformDataNoCallBack(1);
	
	// url call
	var urlParam = "?formId="+ $('#formId').val()
	+ "&formCode="+ $('#formCode').val()
	+ "&eventAction=executeSQLGenerator";
	
	var data_ = JSON.stringify({
		action : "executeSQLGenerator",
		data : allData,
		errorMsg : ""
	});
	
	// call...
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',

		success : function(obj) {
			if(obj.data[0].val == null || obj.data[0].val.length == 0) {
				onElementDataTableApiChange('sqlResultTable', null,null,true); 
			} else {
				onElementDataTableApiChange('sqlResultTable', null,null,true); 
			}
		},
		error : handleAjaxError
	});
}


function generateDynamicReport() {
	//check if some query is selected
	if(($('#DYNAMICREPORTSQL_ID').val() == null || $('#DYNAMICREPORTSQL_ID').val() == '')) {
		displayAlertDialog("Please select data for the report.");
		return;
	}
	// get all
	var allData = getformDataNoCallBack(1);
	
	// url call
	var urlParam = "?formId="+ $('#formId').val()
	+ "&formCode="+ $('#formCode').val()
	+ "&eventAction=generateDynamicReport";
	
	var data_ = JSON.stringify({
		action : "generateDynamicReport",
		data : allData,
		errorMsg : ""
	});
	
	// call...
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',

		success : function(obj) {
			if(obj.data[0].val == null || obj.data[0].val.length == 0) {
				onElementDataTableApiChange('sqlResultTable', null,null,true); 
			} else {
				onElementDataTableApiChange('sqlResultTable', null,null,true); 
			}
		},
		error : handleAjaxError
	});
}





function updateResultsMassb(tableID,domId,cols){
	var dtable = $$(tableID); 
	showWaitMessage(getSpringMessage('pleaseWait'));
	var allData = getformDataNoCallBack(1);
    var stringifyToPush = {
			code : 'MBTABLE_ID',
			val : tableID,
			type : "AJAX_BEAN",
			info : 'na'
		};
    var stringifyToPush1 = {
			code : 'entityName',
			val : domId,
			type : "AJAX_BEAN",
			info : 'na'
		};
    allData = allData.concat(stringifyToPush);
    allData = allData.concat(stringifyToPush1);
    var urlParam = "?formId=" +$('#formId').val()
    + "&formCode=" 	+ $('#formCode').val() + "&userId=" + $('#userId').val()
	+ "&eventAction=updateResultsMassBalance&isNew="; // + $('#isNew').val();
    var data_ = JSON.stringify({
        action: "updateResultsMassBalance",
        data: allData,
        errorMsg: ""
    });
    $.ajax({
        type: 'POST',
        data: data_,
        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) 
        {
        	hideWaitMessage();
        	if(obj.data[0].val!=null && obj.data[0].val!=undefined && obj.data[0].val!=""){
        	uploadWebixMassBalanceData(domId,obj.data[0].val,cols,tableID);
        	}
        },
        error: handleAjaxError
    });
}

function updateExpResultsMassb(stepId,domId){
	//var dtable = $$(tableID); 
	showWaitMessage(getSpringMessage('pleaseWait'));
	var allData = getformDataNoCallBack(1);
    var stringifyToPush = {
			code : 'STEP_ID',
			val : stepId,
			type : "AJAX_BEAN",
			info : 'na'
		};
    
    allData = allData.concat(stringifyToPush);
    var urlParam = "?formId=" +$('#formId').val()
    + "&formCode=" 	+ $('#formCode').val() + "&userId=" + $('#userId').val()
	+ "&eventAction=updateExpResultsMassBalance&isNew="; // + $('#isNew').val();
    var data_ = JSON.stringify({
        action: "updateResultsMassBalance",
        data: allData,
        errorMsg: ""
    });
    $.ajax({
        type: 'POST',
        data: data_,
        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) 
        {
        	reloadExpWebix(domId);
        	hideWaitMessage();
        	//uploadWebixMassBalanceData(domId,obj.data[0].val,cols,tableID);
        },
        error: handleAjaxError
    });
}

function refreshResultMv(){
	showWaitMessage(getSpringMessage('pleaseWait'));
	var allData = getformDataNoCallBack(1);
    var urlParam = "?formId=" +$('#formId').val()
//    + "&formCode=" 	+ $('#formCode').val() + "&userId=" + $('#userId').val()
	+ "&eventAction=refreshResultToUpdateMv&isNew="; // + $('#isNew').val();
    var data_ = JSON.stringify({
        action: "refreshResultToUpdateMv",
        data: allData,
        errorMsg: ""
    });
    $.ajax({
        type: 'POST',
        data: data_,
        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) 
        {
        	hideWaitMessage();
        	onElementDataTableApiChange('resultsUsingtoUpdate');
        },
        error: handleAjaxError
    });
}    
    
function openSummaryReport(action) {
	showWaitMessage("Please wait...");
    prop.onChangeAjaxFlag = false;
    var allData = getformDataNoCallBack(1);

    var urlParam =
        "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&eventAction=" + action + "&isNew=" + $('#isNew').val();
    
    var newurl = url();
	newurl = newurl.substr(0,newurl.indexOf('/skylineForm'));
    var stringifyToPush = {
			code : 'localHost',
			val : newurl,
			type : "AJAX_BEAN",
			info : 'na'
		};
    var allData = allData.concat(stringifyToPush);

    var data_ = JSON.stringify({
        action: "doSave",
        data: allData,
        errorMsg: ""
    });

    $.ajax({
        type: 'POST',
        data: data_,
        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) {
        	hideWaitMessage();
    		var fileId = obj.data[0].val;
			if(fileId == "-1") {
				displayAlertDialog("Error in creating the report");
			} else {
				smartFileSumaryRep("SummaryReport", fileId);
			}
        },
        error: handleAjaxError
    });
}

/**
 * Download file
 * @returns
 */
function smartFileSumaryRep(domId, fileId) 
{
	//console.log('smartFile() domId: ' + domId + "|fileId:" + fileId);
	if(fileId == null || fileId == "")
	{
		displayAlertDialog("Not enough data to download file.");
	}
	else
	{
		$('[name="' + domId + '_FILE_ID"]').val(fileId);
		$('#' + domId + '_AttachmentForm').submit();
	}
}

function deleteSpreadsheetTemplate(spreadsheetTemplate_id){
	var stringifyToPush = {
			code : "spreadsheet_id",
			val : spreadsheetTemplate_id,
			type : "AJAX_BEAN",
			info : 'na'
		}
	var allData = getformDataNoCallBack(1);
	var allData = allData.concat(stringifyToPush);
	
	// url call
	var urlParam = "?formId="+ $('#formId').val()
				+ "&formCode="+ $('#formCode').val()
				+ "&userId="+ $('#userId').val()
				+ "&eventAction=deleteSpreadsheetTemplate"
				+ "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "deleteSpreadsheetTemplate",
		data : allData,
		errorMsg : ""
	});
	showWaitMessage(getSpringMessage('pleaseWait'));

	// call...
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',

		success : function(obj) {
			if (obj.errorMsg != null && obj.errorMsg != '') {
				displayAlertDialog(obj.errorMsg);
			}
			onElementDataTableApiChange('spreadsheetTable');
			hideWaitMessage();
		},
		error : handleAjaxError
	});
}

/**
 * Opens save report dialog. After closing the dialog, the system saves the report in its existing/new name.
 *  If a new report name/existing one was selected the system navigates to it.
 *  If the save is on the same report name, then the report is saved and the dialog is closed.
 * @returns
 */
function openSaveSpreadsheetDialog(){

	 var dialogWidth = "550"// ($(window).width() - 10) * 0.7;
	 var dialogHeight = "550"// ($(window).height()) * 0.8;
	// var reportName = $('#pageTitle').text();
	// var reportDescription = $('#reportDescription').val();
	// var nameId=$('#nameId').val();
	 var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=SaveSpreadsheetAs&formId=-1&userId="
	 + $('#userId').val()
	 + '&tableType=&PARENT_ID='+ $('#formId').val()+'&PARENT_FORMCODE=' + $('#formCode').val()// &REPORT_NAME='ss
	 // +
	 // reportName+'&REPORT_DESCRIPTION='+reportDescription;

	 // open iframe inside dialog
	 var $dialog = $(
	 '<div id="prevDialog" style="overflow-y: hidden;""></div>')
	 .html(
	 '<iframe style="border: 0px;width:100%;height:100%" src="'
	 + page + '"></iframe>')
	 .dialog(
	 {
	 autoOpen : false,
	 modal : true,
	 height : dialogHeight,
	 width : dialogWidth,
	 // title: title,
	 close : function() {
		 if ($('#saveAsFunc').val() =='do_save_and_navigate'){
			 
			 doSave((function(){navigateToForm($('#formId').val(),'SpreadsheetTempla')}), '');
		 }else if($('#saveAsFunc').val() =='do_save'){
			 doSave('Reload', '');
		 }else if($('#saveAsFunc').val() =='clone'){
			cloneSpreadsheet();
		 }
		 $('#prevDialog').remove();
		 }
	 });

	 $dialog.dialog('option', 'dialogClass', 'noTitleStuff')
	 .dialog('open');

	  }

function cloneSpreadsheet(){
	showWaitMessage("Please wait...");

	var allData = getformDataNoCallBack(1);
	
	//url call
    var urlParam =
        "?formId=" +$('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&eventAction=" + 'cloneSpreadsheet' + "&isNew=" + $('#isNew').val();

    var data_ = JSON.stringify({
        action: "cloneSpreadsheet",
        data: allData,
        errorMsg: ""
    });

    //call...
    $.ajax({
        type: 'POST',
        data: data_,
        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) {
        	hideWaitMessage();
        	if(obj.data[0].val!= null && obj.data[0].val!=""){
        		var formId = obj.data[0].val;
        		checkAndNavigate([formId ,'SpreadsheetTempla','','false']);
        	}
        	
        },
        error: handleAjaxError
    });	
}

function viewInSpreadsheet(){
	/*var elem = $('#spreadsheet');
	var $elem = $(elem);
	var spread = $('#spreadsheet').data('workbook');*/
	if(!isSpreadsheetEmpty("spreadsheet"))//check if the spreadsheet has data
	{
		openConfirmDialog({
			onConfirm : function (){
				loadfileIntoSpreadsheet();
			},
			title : 'Warning',
			message : getSpringMessage('Would you like to clear the spreadsheet below?'),
			onCancel: function (){
			}
		});
	} else{
		loadfileIntoSpreadsheet();
	}

}



function clearSpreadsheetTemplate(){

	/*var elem = $('#spreadsheet');
	var $elem = $(elem);
	var spread = $('#spreadsheet').data('workbook');*/
	if(!isSpreadsheetEmpty("spreadsheet"))//check if the spreadsheet has data
	{
		openConfirmDialog({
			onConfirm : function (){
				clearSpreadsheet("spreadsheet");
			},
			title : 'Warning',
			message : getSpringMessage('CLEAR_SPREADSHEET'),
			onCancel: function (){}
		});
	} 
}
function updateExperimentGroupDDL(){
	var groupNumber="";
	var experimentGroupId=$('#prevDialog').data("experimentGroupId");
    var experimentGroupDesc=$('#prevDialog').data("experimentGroupDesc");
    //getExperimentGroupNumber
	var stringifyToPush = {
			code : 'experimentGroup_id',
			val : experimentGroupId,
			type : "AJAX_BEAN",
			info : 'na'
	};
	var allData = getformDataNoCallBack(1);
	allData = allData.concat(stringifyToPush);
	
    // url call
    var urlParam =
        "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&eventAction=" + 'getExperimentGroupNumber' + "&isNew=" + $('#isNew').val()+ "&parentId=" + $('#formId').val();
    var data_ = JSON.stringify({
        action: "getExperimentGroupNumber",
        data: allData,
        errorMsg: ""
    });
    // call...
    $.ajax({
        type: 'POST',
        data: data_,
        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) {
        	groupNumber=obj.data[0].val;
        	if($("#EXPERIMENTGROUP_ID option:contains('"+experimentGroupId+"')").val() == undefined) {
        		$('#EXPERIMENTGROUP_ID').append("<option value=\"" + experimentGroupId + "\" selected>" + groupNumber+'-'+experimentGroupDesc + "</option>");
        		
        		$('#EXPERIMENTGROUP_ID').trigger('chosen:updated');
        	}else{
        		$('#EXPERIMENTGROUP_ID').val($("#EXPERIMENTGROUP_ID option:contains('"+experimentGroupId+"')").val());
        		 $('#EXPERIMENTGROUP_ID').trigger('chosen:updated');
        	}
        },
        error: handleAjaxError
    });
}

function onClickdeleteCube(domId,formId){
	
	if(domId == 'stepIframes') {
		showWaitMessage("Please wait...");
		var allData = getformDataNoCallBack(1);
		var action = "checkBalanceBeforeDeleteStep";
		var selectedTable = $('#' + domId).DataTable();
		var custid = selectedTable.row('.selected').data();
		var selectedRowId = '';
		if (typeof custid !== 'undefined') {
	        selectedRowId = custid[0];
	    }
		var formIdToDelete = selectedRowId;
		// url call
		var urlParam = "?formId=" + formId + "&formCode=StepMinFr" + '&userId=' + $('#userId').val()
				+ "&eventAction=" + action + "&isNew=1";
		
		var stringifyToPush = {
			code : "doCheckBalance",
			val : "0",
			type : "AJAX_BEAN",
			info : 'na'
		};
		allData = allData.concat(stringifyToPush);
		
		var stringifyToPush = {
			code : "parentId",
			val : $('#formId').val(),
			type : "AJAX_BEAN",
			info : 'na'
		};
		allData = allData.concat(stringifyToPush);
		
		var data_ = JSON.stringify({
			action : "doSave",
			data : allData,
			errorMsg : ""
		});
		// call...
		$.ajax({
			type : 'POST',
			// async: false,
			data : data_,
			url : "./generalEvent.request" + urlParam ,
			contentType : 'application/json',
			dataType : 'json',
			success : function(obj) {
				hideWaitMessage();
				var warningToDisplay = "";
				var fullObj = funcParseJSONData(obj.data[0].val); 
				if(fullObj!=null && Object.keys(fullObj).length > 0)
				{
					for(key in fullObj)
					{
						var _valObj = fullObj[key];
						if(_valObj.hasOwnProperty("warningMsg"))
						{
							warningToDisplay = _valObj["warningMsg"];
						}
					}
					if(warningToDisplay.length > 0)
					{
						displayAlertDialog(warningToDisplay+"</br>Deleting the step is not allowed until the quantity is increased");
						return false;
					}
					
				}
				//if(doSave == '0'){
				if(warningToDisplay==""){//if the difference is invalid then the save process can't continue

					// deplete the selected batches
				 	openConfirmDialog({
				        onConfirm: function(){
				        	// update the quantity to be 0
				        	deleteStepCubes(formId);
				        },
				        title: 'Warning',
				        message: getSpringMessage('confirmDeletedStep')
				    });
				}
			},
			error : function() {
				hideWaitMessage();
			} 
		});
	} else { //only remove cube
		$('#parent_' + stepId + '_' + formId).remove();
	}
}

function deleteStepCubes(stepId){
	var stringifyToPush = {
			code : "stepList",
			val : stepId,
			type : "AJAX_BEAN",
			info : 'na'
		}
	var allData = getformDataNoCallBack(1);
	var allData = allData.concat(stringifyToPush);
	
	// url call
	var urlParam = "?formId="+ $('#formId').val()
				+ "&formCode="+ $('#formCode').val()
				+ "&userId="+ $('#userId').val()
				+ "&eventAction=deleteSteps"
				+ "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "deleteSteps",
		data : allData,
		errorMsg : ""
	});
	showWaitMessage(getSpringMessage('pleaseWait'));

	// call...
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',

		success : function(obj) {
			if (obj.errorMsg != null && obj.errorMsg != '') {
				displayAlertDialog(obj.errorMsg);
			} 
			else
			{
				$('#parent_stepIframes_' + stepId).remove();
				generalBL_generalClickEvent('checkBalance','0');
				showHideCollapsExpandAll();
			}
			hideWaitMessage();
		},
		error : handleAjaxError
	});
}
function editExperimentGroup(domId){
	selectedTable = $('#' + domId).DataTable();
    custid = selectedTable.row('.selected').data();
    if(custid != undefined ){
    	rowId = custid[1];
	openExperimentGroupPopUp(rowId);
    }
}
function onClickCancelExperimentGroup(domId){
	 var struct = $('[id="' + domId + '_structCatalogItem"]').val();
	    var selectedTable = $('#' + domId).DataTable();
	    var custid = selectedTable.row('.selected').data();
	    var formId = "";
	    if (typeof custid !== 'undefined') {
	        formId = custid[1];
	    }
	var allData = [{
			code : "cancelledId",
			val : formId,
			type : "AJAX_BEAN",
			info : 'na'
		}];
	/*
	 * var allData = getformDataNoCallBack(1); var allData =
	 * allData.concat(stringifyToPush); var allData = { code : "batchList", val :
	 * batchList, type : "AJAX_BEAN", info : 'na' }
	 */
	
	// url call
	var urlParam = "?formId="+ $('#formId').val()
				+ "&formCode="+ $('#formCode').val()
				+ "&userId="+ $('#userId').val()
				+ "&eventAction=cancelExperimentGroup"
				+ "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "cancelExperimentGroup",
		data : allData,
		errorMsg : ""
	});
	showWaitMessage(getSpringMessage('pleaseWait'));

	// call...
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',

		success : function(obj) {
			if (obj.errorMsg != null && obj.errorMsg != '') {
				displayAlertDialog(obj.errorMsg);
			} 
			
			else
			{
				displayAlertDialog(getSpringMessage('The Experiment Group was cancelled successfully'));				
				onElementDataTableApiChange(domId);
			}
			hideWaitMessage();
		},
		error : handleAjaxError
	});
}


function openCompositionDetails(){
	/*var stringifyToPush = {
			code : "stepList",
			val : stepId,
			type : "AJAX_BEAN",
			info : 'na'
		}*/
	var allData = getformDataNoCallBack(1);
	//var allData = allData.concat(stringifyToPush);
	
	// url call
	var urlParam = "?formId="+ $('#formId').val()
				+ "&formCode="+ $('#formCode').val()
				+ "&userId="+ $('#userId').val()
				+ "&eventAction=openCompositionDetails"
				+ "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "openCompositionDetails",
		data : allData,
		errorMsg : ""
	});
	showWaitMessage(getSpringMessage('pleaseWait'));

	// call...
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',

		success : function(obj) {
			if (obj.errorMsg != null && obj.errorMsg != '') {
				displayAlertDialog(obj.errorMsg+'.</br>Composition details cannot be open.');
			} 
			else
			{
				//open the popup composition details
				var formCode = 'CompositionDetails';
				var dialogWidth = $(window).width()*0.9;
				var dialogHeight = $(window).height()*0.9;
				var page = "./init.request?stateKey=" + $('#stateKey').val()
						+ "&formCode=" + formCode + "&formId=-1" + "&userId="
						+ $('#userId').val()+ "&parentId=" + $('#formId').val();
				// open iframe inside dialog
				var $dialog = $(
						'<div id="prevDialog" style="overflow-y: hidden;""></div>')
						.html(
								'<iframe id="formIframeId" style="border: 0px;width:100%;height:100%" src="'
										+ page /* + urlPermissionDisabled */
										+ '"></iframe>').dialog({
							autoOpen : false,
							modal : true,
							height : dialogHeight,
							width : dialogWidth,

							close : function() {
								$('#prevDialog iframe').attr('src', 'about:blank');
								$('#prevDialog').remove();
								// $('#EXPERIMENTGROUP_ID').trigger('chosen:updated');
							}
						});

				$dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
				
			}
			hideWaitMessage();
		},
		error : handleAjaxError
	});
}

function updateTestedComponentTable(){
	var selectedId = "";// the row to be filled with the current material data
	var selectedTable = $('#testedComponents').DataTable();
	var custid = selectedTable.row('.selected').data();
	if (typeof custid !== 'undefined') {
		selectedId = custid[0];
	}
	
	var search_material_id = $('#search_material_id').val();	
	if(search_material_id == ''){
		return;
	}
	
	 var stringifyToPush1 = {
			code : '_MATERIAL_ID',
			val : search_material_id,
			type : "AJAX_BEAN",
			info : 'na'
		};
 				
	var allData = getformDataNoCallBack(1);
	allData = allData.concat(stringifyToPush1);	
	
	// url call
	var urlParam = "?formId="+ $('#formId').val()
				+ "&formCode="+ $('#formCode').val()
				+ "&userId="+ $('#userId').val()
				+ "&eventAction=updateTestedComponentTable"
				+ "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "updateTestedComponentTable",
		data : allData,
		errorMsg : ""
	});

	// call...
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',

		success : function(obj) {
			if (obj.errorMsg != null && obj.errorMsg != '') {
				displayAlertDialog(obj.errorMsg);
			} 
			else
			{
				
				onElementDataTableApiChange("testedComponents");
			}

			hideWaitMessage();
		},
		error : handleAjaxError
	});
	$('#search_material_id').val("");		
}
function updateInstrumentTable(){
	var selectedId = "";// the row to be filled with the current material data
	var selectedTable = $('#instrumentsTable').DataTable();
	var custid = selectedTable.row('.selected').data();
	if (typeof custid !== 'undefined') {
		selectedId = custid[0];
	}
	
	var instrument_id = $('#search_insrument_id').val();	
	if(instrument_id == ''){
		return;
	}
	
	 var stringifyToPush1 = {
			code : '_INSTRUMENT_ID',
			val : instrument_id,
			type : "AJAX_BEAN",
			info : 'na'
		};
 				
	var allData = getformDataNoCallBack(1);
	allData = allData.concat(stringifyToPush1);	
	
	// url call
	var urlParam = "?formId="+ $('#formId').val()
				+ "&formCode="+ $('#formCode').val()
				+ "&userId="+ $('#userId').val()
				+ "&eventAction=updateInstrumentTable"
				+ "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "updateInstrumentTable",
		data : allData,
		errorMsg : ""
	});

	// call...
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',

		success : function(obj) {
			if (obj.errorMsg != null && obj.errorMsg != '') {
				displayAlertDialog(obj.errorMsg);
			} 
			else
			{
				
				onElementDataTableApiChange("instrumentsTable");
			}

			hideWaitMessage();
		},
		error : handleAjaxError
	});
	$('#search_insrument_id').val("");		
}
