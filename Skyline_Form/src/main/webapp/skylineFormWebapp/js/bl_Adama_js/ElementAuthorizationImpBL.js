function disableRemoveRow(isActual,arg){
	if(arg!='' && arg!='NA' && arg.indexOf('$')==-1){
		if($('#STATUS_ID').attr("lastselectedname") != "Planned"){
			if(($.fn.DataTable.isDataTable('#action'))){
				 var selectedTable = $('#action').DataTable();
				 var selRow = selectedTable.row('.selected').data();
				 if(selRow[5]!=''){
					 $('#action_dataTableStructButtons button.dataTableApiRemove').addClass('disabledclass');
				 }else{
					 $('#action_dataTableStructButtons button.dataTableApiRemove').removeClass('disabledclass');
				 }
			}
		}
	}
}

function createBatchVisibility(){
	var statusName = $('#STATUS_ID option:selected').text();
	if(statusName == 'Active'){//the step status is active
		var productsEmptyRows=$('#products td[class="dataTables_empty"]').length;
		if(productsEmptyRows>0){//the table is empty
			$('#products_dataTableStructButtons button.dataTableApiOptional1').addClass('authorizationDisabled');
		} else {
			$('#products_dataTableStructButtons button.dataTableApiOptional1').removeClass('authorizationDisabled');
		}
	} else {
		$('#products_dataTableStructButtons button.dataTableApiOptional1').addClass('authorizationDisabled');
	}
}

function disableReactanctTabStatusFinish(isActual){
//	if(isActual == '1'){ //////////////// not in use
//		generalBL_disableTab('ReactionTab');
//		$('[id="chemDoodleAct"]').addClass('disabledChemDoodle');
//	    $('[id="chemDoodleAct"]').css('border-color', '');
//	    $('[id="chemDoodleAct"]').css('outline', '');
//	}
}

function disableWorkupPlannedStep(isActual,workupType){
	if(isActual == '0'){
		return;
	}
	if(workupType == 'Filtration'){
		generalBL_disableTab('GeneralTab',['FILTERCATEGORY_ID','CENTRIFUGTYPE_ID']);
	} else if(workupType == 'Washing'){
		generalBL_disableTab('GeneralTab',['WASHINGMEDIATYPE_ID']);
	} else if(workupType == 'Extraction'){
		generalBL_disableTab('GeneralTab',['WASHINGMEDIATYPE_ID','extructed']);
	} else if(workupType == 'Drying'){
		generalBL_disableTab('GeneralTab',['DRYERTYPE_ID']);
	} else if(workupType == 'Crystallization'){
		generalBL_disableTab('GeneralTab',['CRYSTALPROCESSTYPE_ID']);
	} else if(workupType == 'Distillation'){
		generalBL_disableTab('GeneralTab',['instrumentName','instrument','additionalEquipment','evaporation']);
	}
}

function fnAuthzDemo(isTrue, obj) {
	alert("isTrue=" + isTrue + ", obj1=" + obj);
}


function disablePage(isActual){
	if(isActual == '1'){
		generalBL_disablePage();
	}
}

function setDefaultProcedure(isActual){
	if(isActual == '1'){
		var procedureName = $('#PROCEDURE_ID option:selected').text();
		if(procedureName == ''){
			$('#PROCEDURE_ID').val($(parent.document).find('#ORIGIN_TEMPLATE_ID').val());
			$('#PROCEDURE_ID').trigger('chosen:updated');
		}
	}
}

function disableSampleTable(isActual){
	if(isActual == '1'){
		$('#samples_dataTableStructButtons button.dataTableApiAdd').addClass('authorizationDisabled'); //.css('display','none');//.addClass('disabledclass');  
		$('#samples_dataTableStructButtons button.dataTableApiRemove').addClass('authorizationDisabled'); //.css('display','none');//.addClass('disabledclass');  
		changeSingleDTLabelByDisabledState("samples", true);//_dataTableStructButtons"
	} else {
		 $('#samples_dataTableStructButtons button.dataTableApiAdd').removeClass('authorizationDisabled'); //.css('display','none');//.addClass('disabledclass');  
		 $('#samples_dataTableStructButtons button.dataTableApiRemove').removeClass('authorizationDisabled'); //.css('display','none');//.addClass('disabledclass');  
	}
}

//not in use for now. same functionality is executed in setExpAnalyticalDisableFields.
function disableExperimentUpToStatus(isActual,statusName){
	if(isActual == '1'){
		if(statusName == 'Finished'){
			generalBL_disablePage(['STATUS_ID','conclussion'],1);
		}
	}
}

function disableRequestUpToUser(isActual, userState){
	if(isActual ==1 ){//status is waiting
		if(userState == 'source'){
			generalBL_disablePage(['REQUESTSTATUS_ID','reasonForChange','operartinTypeTable'],1);
			disableOperationTypeDTButtons(0);
		}
		else if(userState == 'dest'){
			disableOperationTypeDTButtons(1);
		}
		else{
			disableOperationTypeDTButtons(0);
		}
	}
}

function disableRequestInStatProgressUpToUser(isActual, userState){
	if(isActual ==1 ){//status is in process
		if(userState == 'source'){
			generalBL_disablePage(['REQUESTSTATUS_ID','reasonForChange','operartinTypeTable'],1);
			disableOperationTypeDTButtons(0);
		}
		else if(userState == 'dest'){
			generalBL_disablePage(['testPurpose','comments','materialsPeaksTable','documents','groups','users','REQUESTSTATUS_ID','operartinTypeTable'],1);
			disableOperationTypeDTButtons(1);
		} else{
			generalBL_disablePage();
		}
	}
}

function disableRequestInStatApprovedUpToUser(isActual, userState){
	if(isActual ==1 ){//status is Approved
		if(userState == 'dest'){
			generalBL_disablePage(['testPurpose','comments','materialsPeaksTable','documents','groups','users','REQUESTSTATUS_ID','operartinTypeTable'],1);
			disableOperationTypeDTButtons(1);
		} else{
			generalBL_disablePage();
		}
	}
}

function hideManualresultsButtons(isActual){
	if(isActual == '1'){
		$('#manualResultsTable_dataTableStructButtons button.dataTableApiAdd').css('display', 'none');
	}
}

/*function setResultDefaultMain(isActual,defaultResultListCsv){
	setTimeout(function () {	
		var resultsArray = defaultResultListCsv.split("-");
		if ($('[id="sampleResults"] thead').length){
			var table_ = $('#sampleResults').DataTable();
			 for (var i = 0; i < resultsArray.length; i++) {  
				if(table_.$('input[type="checkbox"][value="'+resultsArray[i]+'"]').length!= 0){
				 	//check and disable the result
					table_.$('input[type="checkbox"][value="'+resultsArray[i]+'"]').prop('checked', true);
					table_.$('input[type="checkbox"][value="'+resultsArray[i]+'"]').addClass('authorizationDisabled');
				}
			 }
		}
	},400);
}*/

function hideExportTable(isActual){
	if(isActual == '1'){//hide export table
		$('#specificationSourceTable_Parent').css('display', 'none');
		$('#specificationSourceTable_Parent').css('visibility', 'hidden');
	}
	else{//hide import table
		$('#specificationTable_Parent').css('display', 'none');
		$('#specificationTable_Parent').css('visibility', 'hidden');
	}
}

function generalDisabledAuthzFunc(isDisabled) {
	if(isDisabled == '1') {
		generalBL_disablePage(['training']); //fix bug 8452
	}
}

function setRadioToBatch(isActual,params){
	var materialDataArr = params.split('-');
	if(isActual == 1){
		//$('input[name="productRadiobatch"][value="Batch"]').prop('checked', true);
		//onChangeAjax('productRadiobatch');
		if(materialDataArr.length==2){
			$('#productName').val(materialDataArr[1]);
			$('#productId').val(materialDataArr[0]);
		}
	}
}

function disableNewWorkupSelftest(isActual){
	if(isActual == 1){
		$('#selfTest_dataTableStructButtons button.dataTableApiNew').addClass('authorizationDisabled');
		//$('#workup_dataTableStructButtons button.dataTableApiNew').addClass('authorizationDisabled');
	}
}

function disableActionCondition(isActual){
	if(isActual == 1) {
		$('#conditionsTable_dataTableStructButtons .dataTableApiEdit').addClass('authorizationDisabled');
	}
}
 
function disableMaterialRefDTButtons(isActual, comment) {
	if(isActual == 1) {
		 /*generalBL_enableTab('ReactionTab');
		 setRichTextEditorDisabled('conclussion_coppy', true, 'AUTHEZ');
		 $('[id="chemDoodleAct"]').removeClass('disabledChemDoodle');*/
	} else {//get into the following snippet in finished status
		generalBL_disableTab('ReactionTab');
		/*$('[id="chemDoodleAct"]').addClass('disabledChemDoodle');
	    $('[id="chemDoodleAct"]').css('border-color', '');
	    $('[id="chemDoodleAct"]').css('outline', '');
	    $('#planned_actual').removeClass('disablePage');
	    $('[name="planned_actual"]').removeClass('disablePage');
	    $('[name="planned_actual"]').prop('disabled',false);*/
	    $('#products_dataTableStructButtons button.dataTableApiOptional1').removeClass('disablePage');
	}
	
}

function enableDtailsTabViewButtons(isActual, comment) {
	if(isActual == 1) {
		$('#steps_dataTableStructButtons button.dataTableApiNew').addClass('authorizationDisabled');
		$('#action_dataTableStructButtons button.dataTableApiNew').addClass('authorizationDisabled');
		$('#selfTests_dataTableStructButtons button.dataTableApiNew').addClass('authorizationDisabled');
		$('#workups_dataTableStructButtons button.dataTableApiNew').addClass('authorizationDisabled');
	}
}
function enableDetailsTabViewButtons(isActual, comment) {
	if(isActual == 1) {
		$('#steps_dataTableStructButtons button.dataTableApiView').removeClass('authorizationDisabled');
		$('#action_dataTableStructButtons button.dataTableApiView').removeClass('authorizationDisabled');
		$('#selfTests_dataTableStructButtons button.dataTableApiView').removeClass('authorizationDisabled');
		$('#workups_dataTableStructButtons button.dataTableApiView').removeClass('authorizationDisabled');
	}
}
function disableOperationTypeDTButtons(isActual, comment) {
	if(isActual == 1) {
		
		 $('#operartinTypeTable_dataTableStructButtons button.dataTableApiAdd').addClass('authorizationDisabled'); //.css('display','none');//.addClass('disabledclass');  
		 $('#operartinTypeTable_dataTableStructButtons button.dataTableApiRemove').addClass('authorizationDisabled'); //.css('display','none');//.addClass('disabledclass'); 
		 $('#operartinTypeTable_dataTableStructButtons button.dataTableApiEdit').html('View');
	} else {
		
		$('#operartinTypeTable_dataTableStructButtons button.dataTableApiAdd').addClass('authorizationDisabled'); //.css('display','none');//.addClass('disabledclass');  
		$('#operartinTypeTable_dataTableStructButtons button.dataTableApiRemove').addClass('authorizationDisabled'); //.css('display','none');//.addClass('disabledclass');  
	
	}
	
}

function setEmptyContentAuthzFunc(isTemplate,username ) {
	if(isTemplate == '1') {
		
		$('#newButton').addClass('disablePage');
		$('#saveButton').addClass('disablePage');
		$('#newFloatingButton').addClass('disablePage');
		$('#saveFloatingButton').addClass('disablePage');
		$('#dataTableAddRowFloatingButton').addClass('disablePage');
		$('#cloneButton').addClass('disablePage');
		
		 $('#creationDateTime').val("00/00/0000");
		 $('#creationDate').val("00/00/0000");
		 $('#lastModifDate').val("00/00/0000");
		 
		 /*$('#STATUS_ID').html('');			
		 $('#STATUS_ID').trigger('chosen:updated');*///adib 281018 bug 6103
		 
		 $('#OWNER_ID').html('');			
		 $('#OWNER_ID').trigger('chosen:updated');
		 
		 $('#CREATOR_ID').html('');			
		 $('#CREATOR_ID').trigger('chosen:updated');
		
		 //$('#formNumberId').val("");
		 //$('#experimentVersion').val("");
		 if(username!=undefined && username=='system' || username=='admin'){
			 generalBL_disablePage(['chromatograms']);//adib 10072020 a temporary solution in order to fix the bug of null files
		 } else {
			 generalBL_disablePage();//['steps','action','selfTests','selfTest','workups','workup'],false
		 }
		 setDisableWebixTables('1',''); //kd 10122019 disable webix tables if current functions calls from auth element of the form and the condition in auth element is satisfied
		 
		 
		/*var tableName = 'steps';				
    	$("table#"+tableName+" > tbody > tr").each(function(index, value){
    		$('td:eq(2)', this).text("");
    	});
          
    	var tableName = 'action';				
    	$("table#"+tableName+" > tbody > tr").each(function(index, value){
    		$('td:eq(8)', this).text("");
    	});
    	*/
    	// hide familiarity column
    	/*var tableName = 'instruments';				
    	$("table#"+tableName+" > tbody > tr").each(function(index, value){
    		$('td:eq(4)', this).text("");
    	});
    	
    	var tableName = 'materials';				
    	$("table#"+tableName+" > tbody > tr").each(function(index, value){
    		$('td:eq(3)', this).text("");
    	});
    	    
    	var tableName = 'columns';				
    	$("table#"+tableName+" > tbody > tr").each(function(index, value){
    		$('td:eq(4)', this).text("");
    	});
		*/
		
	}
}


function setExpAnalyticalDisableFields(condition, status) {
	if(status=="Failed")
	{
		var list=['description','APPROVER_ID','OWNER_ID','estimatedStartDate','STATUS_ID','experimentGroup','aim','conclussion'];
		for (i = 0; i < list.length; i++) {
			setDisabledByElementId(list[i],true);
		}
	} else if(status == 'Finished'){
		generalBL_disablePage(['STATUS_ID','conclussion','reasonForChange','documents'],1);
	}
}

function setExpParametricMandatoryDisableFields(condition, status_) {
//	alert(params);
	
//	if(status == 'Active') {
//		alert(status + " - 1");
//		setRequiredByElementId('NECK_ID',true);
//		setRequiredByElementId('grossWeight',true);
//		setRequiredByElementId('APPROVER_ID',true);
//		setRequiredByElementId('tareWeight',true);
//		
//	} else {
//		alert(status + " - 2");
//		setRequiredByElementId('NECK_ID',false);
//		setRequiredByElementId('grossWeight',false);
//		setRequiredByElementId('APPROVER_ID',false);
//		setRequiredByElementId('tareWeight',false);
//		
//	}
	
	//return;
	var arr = status_.split(':');
	var status= arr[0];//current
    var savedStatus = arr[1];// status - not current
    
    if (savedStatus == 'Approved') {
    	generalBL_disablePage(['STATUS_ID','reasonForChange','steps','action','selfTests','workups','feedbackHistory','updateVersion','sender'], true);
    }else if (savedStatus == 'Completed'){
    	generalBL_disablePage(['STATUS_ID','reasonForChange','steps','action','selfTests','workups','feedbackHistory','updateVersion','conclussion','EXPERIMENTGROUP_ID','SerialNumber','sender'], true);
    	}
	

	var shapeVal = $('#SHAPE_ID option:selected').text();
	var elements = [];
	var cancelMandatoryElements = [];
	//	status active plus
	if((status=="Finished")||(status=="Failed")||(status=="Active")||(status=="Completed")||(status=="Approved"))
	{
		elements.push('corrosionSolution','corrosionDescription','PAPER_ID','FLASKTYPE_ID','AGITATION_ID','samples','alloy','BOTTLE_MATERIAL_ID','BOTTLE_PRODUCER_ID',
			'initialWeight','temp','CONDENSER_ID','IMMERSION_ID','SHAPE_ID','densityUom','visosityUom',
			'visosityBatchNumber', 'materialName','conditionDensity', 'conditionViscosity','VOLUME_ID','SEAL_ID',
			'temperature','B_TEMP_UOM_ID'/*,'ISOTime'*/,'TSMaxCondition','TOMaxCondition','PMaxCondition','NECK_ID',
			'CELLTYPE_ID','HEATINGPROGRAM_ID'/*,'TISO'*/,'grossWeight','tareWeight','heatingRate');
	
		
		$('#equiptPreparationInstruction','#safetyComments').addClass('disablePage');
		 var list=['equiptPreparationInstruction','safetyComments'];
 		 for (i = 0; i < list.length; i++) {
 			setDisabledByElementId(list[i],true);
 		 }
		
//	        openConfirmDialog({	
//	            title: 'Warning',	           
//	            message: getSpringMessage('PleaseFillTheRequiredFields')
//	        });
	    
	}
	else
	{
		cancelMandatoryElements.push('corrosionSolution','corrosionDescription','PAPER_ID','FLASKTYPE_ID','AGITATION_ID','samples','alloy','BOTTLE_MATERIAL_ID','BOTTLE_PRODUCER_ID',
				'initialWeight','temp','CONDENSER_ID','IMMERSION_ID','SHAPE_ID','densityUom','VOLUME_ID','SEAL_ID',
				'temperature','B_TEMP_UOM_ID'/*,'ISOTime'*/,'TSMaxCondition','TOMaxCondition','PMaxCondition','NECK_ID',
				'CELLTYPE_ID','HEATINGPROGRAM_ID'/*,'TISO'*/,'grossWeight','tareWeight','heatingRate');
		//,'visosityUom','visosityBatchNumber', 'materialName','conditionDensity', 'conditionViscosity'
	}
	
	
	if((savedStatus=="Finished")||(savedStatus=="Failed")||(savedStatus=="Cancelled")||(savedStatus=="Completed")||(savedStatus=="Approved"))
	{
		var listForDisableElements = ['groupsCrew','usersCrew','samples','instrumentsTable','request','instruments'];
		for (j = 0; j < listForDisableElements.length; j++) {
			//$('[id="' + listForDisableElements[j] + '"]').addClass('disablePage');
			setDisabledByElementId(listForDisableElements[j],true);
		}
		$('#instrumentsTable_dataTableStructButtons button.dataTableApiAdd').addClass('authorizationDisabled');
		$('#instrumentsTable_dataTableStructButtons button.dataTableApiRemove').addClass('authorizationDisabled');
	}
	else{
		$('#instrumentsTable_dataTableStructButtons button.dataTableApiAdd').removeClass('authorizationDisabled');
		$('#instrumentsTable_dataTableStructButtons button.dataTableApiRemove').removeClass('authorizationDisabled');
	}
	if((status=="Finished")||(status=="Completed"))
	{
		elements.push("density","FINAL_RESULT_ID",'number1','temperature1','A1_UOM_ID','B1_UOM_ID','time1','temperatureB1'
				,'C1_UOM_ID','D1_UOM_ID','timeB1','numberB1','initialWeight1'
				,'finalWeight1','weightLoss1'
				,'SEAL_ID_1','SEAMS_ID_1','DISTORTION_ID_1'
				,'finalWeight','resFinalAppearance'
				,'pAfterCooling','pMaxActual','TSmaxActual');
	}
//	{
//		elements.push("density","FINAL_RESULT_ID",'number1','temperature1','A1_UOM_ID','B1_UOM_ID','time1','temperatureB1'
//				,'C1_UOM_ID','D1_UOM_ID','timeB1','numberB1','initialWeight1','initialWeight2','initialWeight3','initialWeight4'
//				,'finalWeight1','finalWeight2','finalWeight3','finalWeight4','weightLoss1','weightLoss2','weightLoss3','weightLoss4'
//				,'SEAL_ID_1','SEAL_ID_2','SEAL_ID_3','SEAL_ID_4','SEAMS_ID_1','SEAMS_ID_2','SEAMS_ID_3','SEAMS_ID_4','DISTORTION_ID_1'
//				,'DISTORTION_ID_2','DISTORTION_ID_3','DISTORTION_ID_4','finalWeight','resFinalAppearance'
//				,'pAfterCooling','pMaxActual','TSmaxActual');
//	}
	else 
	{
		cancelMandatoryElements.push("density","FINAL_RESULT_ID",'number1','temperature1','A1_UOM_ID','B1_UOM_ID','time1','temperatureB1'
				,'C1_UOM_ID','D1_UOM_ID','timeB1','numberB1','initialWeight1','initialWeight2','initialWeight3','initialWeight4'
				,'finalWeight1','finalWeight2','finalWeight3','finalWeight4','weightLoss1','weightLoss2','weightLoss3','weightLoss4'
				,'SEAL_ID_1','SEAL_ID_2','SEAL_ID_3','SEAL_ID_4','SEAMS_ID_1','SEAMS_ID_2','SEAMS_ID_3','SEAMS_ID_4','DISTORTION_ID_1'
				,'DISTORTION_ID_2','DISTORTION_ID_3','DISTORTION_ID_4','finalWeight','resFinalAppearance'
				,'pAfterCooling','pMaxActual','TSmaxActual');
	}
	
	//disable tabs
//	if((status=="Finished")||(status=="Completed")||(status=="Approved"))
//	{
//		generalBL_disableTab('BottelsConditionsTab');
//		generalBL_disableTab('CorrosionConditionsTab');
//		generalBL_disableTab('TSUConditionsTab');
//		generalBL_disableTab('ViscosityConditionsTab');
//		generalBL_disableTab('EquipmentTab');
//		generalBL_disableTab('SafetyTab');
//		generalBL_disableTab('SamplesTab');
//		generalBL_disableTab('CrewTab');
//		generalBL_disableTab('BottlesResultsTab');
//		generalBL_disableTab('CorrosionResultsTab');
//		generalBL_disableTab('TSUResultsTab');
//		generalBL_disableTab('ViscosityResultsTab');
		
//		var tabs = ['BottelsConditionsTab','CorrosionConditionsTab','TSUConditionsTab','ViscosityConditionsTab',
//			        'EquipmentTab','SafetyTab','SamplesTab','CrewTab','BottlesResultsTab','CorrosionResultsTab',
//			        'TSUResultsTab','ViscosityResultsTab'];
//		for (j = 0; j < tabs.length; j++) {
//			generalBL_disableTab(tabs[j]);
//		}
		
	//}
	
	if(status=="Completed")
	{
		elements.push("conclussion");
	}
	else 
	{
		cancelMandatoryElements.push("conclussion");
	}	
	
	if(status=="Active")
	{
		elements.push("APPROVER_ID","description");
	}
	else
	{
		cancelMandatoryElements.push("APPROVER_ID","description");
	}
	
	if(savedStatus=="Approved")
	{
		var list=['OWNER_ID','APPROVER_ID','aim','conclussion'];
		for (i = 0; i < list.length; i++) {
			setDisabledByElementId(list[i],true);
		}
	}
	else if(status=="Active"){//approved experiment return to active status
		setDisabledByElementId('APPROVER_ID',false);
	}
	
	if(status=="Failed")
	{
		generalBL_disablePage(['reasonForChange'], 1);
		var list=['aim','conclussion'];
		for (i = 0; i < list.length; i++) {
			setDisabledByElementId(list[i],true);
		}
	}
	
	
	if(savedStatus=="Planned")
	{
		generalBL_disableTab('BottlesResultsTab');
		generalBL_disableTab('CorrosionResultsTab');
		generalBL_disableTab('TSUResultsTab');
		generalBL_disableTab('ViscosityResultsTab');
		setDisabledByElementId('experimentSeries',false);
	}
	else
	{
		generalBL_enableTab('BottlesResultsTab');
		generalBL_enableTab('CorrosionResultsTab');
		generalBL_enableTab('TSUResultsTab');
		generalBL_enableTab('ViscosityResultsTab');
		setDisabledByElementId('experimentSeries',true);
	}
	
	if(status =="Finished"){
		elements.push("resultsTable");
	}
	else{
		cancelMandatoryElements.push("resultsTable");
	}
	if((status=="Finished")||(status=="Failed")||(status=="Active")||(status=="Cancelled")||(status=="Completed")||(status=="Approved"))
	{
		if(shapeVal=="Irregular")
		{
			elements.push("area","initialVolume");
		}
		else
		{
			cancelMandatoryElements.push("area","initialVolume");
		}
		if(shapeVal=="Cylinder")
		{
			elements.push("length","radius");
		}
		else
		{
			cancelMandatoryElements.push("length","radius");
		}
		if(shapeVal=="Flat Plate")
		{
			elements.push("height","width","length");
		}
		else
		{
			cancelMandatoryElements.push("height","width");
		}
	}
	
	/*"elements" list contains all the fields that need to be mandatory
	 "cancelMandatoryElements" list contains all the fields that not need to be mandatory
	 these loops pass over the lists and make mandatory correspondence*/
	
	for (j = 0; j < cancelMandatoryElements.length; j++) {
		setRequiredByElementId(cancelMandatoryElements[j],false);
	}
	
	for (i = 0; i < elements.length; i++) {
		setRequiredByElementId(elements[i],true);
	}
	
	if(savedStatus == 'Cancelled'){
	   	 generalBL_disablePage(['STATUS_ID'], true);
	    }
	if (status == 'Approved') {
	 		var list = [ 'EXPERIMENTGROUP_ID', 'generatorButton' ];
	 		for (i = 0; i < list.length; i++) {
	 			setDisabledByElementId(list[i], true);
	 		}
	 	} 
	/*if(status=="Cancelled")
	{
		openConfirmDialog({onCancel:resetStatus,title: 'Warning',message: getSpringMessage('confirmCancelledExperimentMessage') });
	}*/// ab 080318 fixed bug 5153
	
}
function setCasNumberByMaterialType(condition, param) {
	var isActive = false;
	$("#MATERIALTYPE_ID option:selected").each(function () {
		   var $this = $(this);
		   if ($this.length) {
			    var selText = $this.text();
			    if(selText.toUpperCase() == 'ACTIVE INGREDIENT') {
			    	isActive = true;
			    }
		   }
	});
	
	setRequiredByElementId('casNumber',isActive);
}
function parametricExpDisableTabs(isDisable,status) 
{
  //disable tabs
	if((status=="Finished")||(status=="Completed")||(status=="Approved"))
		{
			generalBL_disableTab('BottelsConditionsTab');
			generalBL_disableTab('CorrosionConditionsTab');
			generalBL_disableTab('TSUConditionsTab');
			generalBL_disableTab('ViscosityConditionsTab');
			generalBL_disableTab('EquipmentTab');
			generalBL_disableTab('SafetyTab');
			generalBL_disableTab('SamplesTab');
			generalBL_disableTab('CrewTab');
			generalBL_disableTab('BottlesResultsTab');
			generalBL_disableTab('CorrosionResultsTab');
			generalBL_disableTab('TSUResultsTab');
			generalBL_disableTab('ViscosityResultsTab');
		}
	if((status=="Planned"))
	{
		generalBL_disableTab('ViscosityResultsTab');
	}
	
//	var tabs = ['BottelsConditionsTab','CorrosionConditionsTab','TSUConditionsTab','ViscosityConditionsTab',
//		        'EquipmentTab','SafetyTab','SamplesTab','CrewTab','BottlesResultsTab','CorrosionResultsTab',
//		        'TSUResultsTab','ViscosityResultsTab'];
//	for (j = 0; j < tabs.length; j++) {
//		generalBL_disableTab(tabs[j]);
//	}
   
}

function setDisabledByElementId(elementName, isDisabled) {
	var selectAddition = "";
	var hasParentDiv = false;	
	var element = $('[id="' + elementName + '"]');
	
	console.log("setDisabledByElementId() isDisabled: " + isDisabled + ",elementName=" + elementName);
	
	if(element.is("select") && $('[id="' + elementName + '_chosen"]').length > 0) {
		selectAddition = "_chosen";
	} 
	else if(element.hasClass('ckeditor') && $('[id="' + elementName + '_parent"]').length > 0) {
		selectAddition = "_parent";
	} 
	else if(element.is("input") && $('[id="fileUploadElementForm_' + elementName + '"]').length > 0) {
		hasParentDiv = true;
		selectAddition = "dragAndDropHandler";
	} 
	else if(element.is("input") && element.hasClass('date-picker')) {
		hasParentDiv = true;
	}
	if (element.attr("name") == 'parentWebixContainer'){
		setDisableWebixTableId(isDisabled, elementName);
	} 
	else if(element.is("div") && element.attr('name')=='parentDiagramContainer'){
		setDisabledDiagram(isDisabled,element);
	}
	else if(element.hasClass('excelSheet')){
		disableSpreadsheet(elementName,isDisabled);
    } 
	
	if ((isDisabled) && (!$('[id="' + elementName + selectAddition +'"]').hasClass('authorizationDisabled'))) 
	{
		if($('#' + elementName).is("table")){
		    changeSingleDTLabelByDisabledState(elementName, true);
		} else if(element.is("div") && element.hasClass('ckeditor')) {
			$('[id="' + elementName + selectAddition +'"]').addClass('authorizationDisabled');
			setRichTextEditorDisabled(elementName, true, 'AUTHEZ');
		} else {
			$('[id="' + elementName + selectAddition +'"]').addClass('authorizationDisabled');
		}
		
		if(hasParentDiv) 
		{
			// important set 'authorizationDisabled' class to datepicker parent div element  for disable datepicker's icon too
			element.parent().addClass('authorizationDisabled');
		}
    } 
	else if((!isDisabled) && $('#' + elementName).is("table")){
		changeSingleDTLabelByDisabledState(elementName, false);
	}
	else if ((!isDisabled) && ($('[id="' + elementName + selectAddition +'"]').hasClass('authorizationDisabled'))) 
    {
        $('[id="' + elementName + selectAddition +'"]').removeClass('authorizationDisabled');
        if(hasParentDiv)
		{
        	element.parent().removeClass('authorizationDisabled');
		}
        if(element.hasClass('ckeditor'))
		{
        	setRichTextEditorDisabled(elementName, false, 'AUTHEZ');
		}
    }
}

/**
 * returns an object
 * { 
 * 	setRequired: indicates if the mandatory fields have to be required according to some conditions,
 * 	mandatoryList: list of mandatory elements that are required when 'setRequired' is 0
 * }
 */
function isMandatoryFieldsRequired(){
	if($("#formCode").val().indexOf("Experiment")!==-1){
		var status = $('#STATUS_ID option:selected').text();
		var laststatusname =  $('#STATUS_ID').attr('lastselectedname');
		if(status == "Cancelled" || status == "Failed" /*|| status == "Finished"*/){
			return {
				setRequired:  '0',
					mandatoryList: ["#reasonForChange"],//("#estimatedStartDate,#STATUS_ID,#MASSBALLANCETYPE_ID")//[]
					message:'Please record the reason for experiment '
						+(status == "Cancelled"?
								'cancelation':status == "Failed"?
										'failure':status == "Finished" && laststatusname == "Completed"?
												'rejection':status == "Finished" && laststatusname == "Approved"?
														're-opening':'')
		  };
		}else if (status == "Finished" && (laststatusname== "Completed"||laststatusname == "Approved")){
			return {
				setRequired:  '0',
					mandatoryList: ["#reasonForChange"],//("#estimatedStartDate,#STATUS_ID,#MASSBALLANCETYPE_ID")//[]
					message:'Please record the reason for experiment '
						+(status = "Finished" && laststatusname == "Completed"?
												'rejection':status == "Finished" && laststatusname == "Approved"?
														're-opening':'')
		  };
		}
	} else if($("#formCode").val().indexOf("Request")!==-1){
		var status = $('#REQUESTSTATUS_ID option:selected').text();
		if(status == "Cancelled" || status == "Declined"){
			return {
					setRequired:  '0',
					mandatoryList: ["#reasonForChange"],//("#estimatedStartDate,#STATUS_ID,#MASSBALLANCETYPE_ID")//[]
					message:'Please record the reason for request '
						+(status == "Cancelled"? 'cancellation':status == "Declined"?'declination':'')
		  };
		}
	}
	
	return {
		setRequired:  '1',
		mandatoryList: []
	};
}


function setUomSpecificationMandatory(isActual){
	
	if($('#UOM1_ID').children('option').length==1)
		setRequiredByElementId('UOM1_ID',false);
	if($('#UOM2_ID').children('option').length==1)
		setRequiredByElementId('UOM2_ID',false);
}
function disableSpecificationTable(isActual){
	if(isActual == '1'){
		$('#spacifications_dataTableStructButtons button.dataTableApiAdd').addClass('authorizationDisabled'); //.css('display','none');//.addClass('disabledclass');  
		$('#spacifications_dataTableStructButtons button.dataTableApiRemove').addClass('authorizationDisabled'); //.css('display','none');//.addClass('disabledclass');  
		$('#exportSpecification').addClass('authorizationDisabled');
		$('#importSpecification').addClass('authorizationDisabled');
		changeSingleDTLabelByDisabledState("spacifications", true);//_dataTableStructButtons
	} else {
		
		 $('#spacifications_dataTableStructButtons button.dataTableApiAdd').removeClass('authorizationDisabled'); //.css('display','none');//.addClass('disabledclass');  
		 $('#spacifications_dataTableStructButtons button.dataTableApiRemove').removeClass('authorizationDisabled'); //.css('display','none');//.addClass('disabledclass');  
	}
}
function setSingleOption(){
	if($('#LABORATORY_ID option').length == 2 && $('#LABORATORY_ID').val()==''){
		$('#LABORATORY_ID').val($('#LABORATORY_ID option').eq(1).val());
		$('#LABORATORY_ID').trigger('chosen:updated');
		onChangeAjax('LABORATORY_ID');
	}
	
	if($('#PROTOCOLTYPE_ID option').length == 2 && $('#PROTOCOLTYPE_ID').val()==''){
		$('#PROTOCOLTYPE_ID').val($('#PROTOCOLTYPE_ID option').eq(1).val());
		$('#PROTOCOLTYPE_ID').trigger('chosen:updated');
		onChangeAjax('PROTOCOLTYPE_ID');
	}

	if($('#EXPERIMENTTYPE_ID option').length == 2 && $('#EXPERIMENTTYPE_ID').val()==''){
		$('#EXPERIMENTTYPE_ID').val($('#EXPERIMENTTYPE_ID option').eq(1).val());
		$('#EXPERIMENTTYPE_ID').trigger('chosen:updated');
		onChangeAjax('EXPERIMENTTYPE_ID');
	}
	
	if($('#EXPERIMENTVIEW_ID option').length == 2 && $('#EXPERIMENTVIEW_ID').val()==''){
		$('#EXPERIMENTVIEW_ID').val($('#EXPERIMENTVIEW_ID option').eq(1).val());
		$('#EXPERIMENTVIEW_ID').trigger('chosen:updated');
		onChangeAjax('EXPERIMENTVIEW_ID');
	}
}
function disableButtons(isActual){
	if(isActual == '1'){
		$('#bGenerate').addClass('authorizationDisabled');
		$('#bSaveReport').addClass('authorizationDisabled');
	} else {
		$('#bGenerate').removeClass('authorizationDisabled');
		$('#bSaveReport').removeClass('authorizationDisabled');
	}
}
function disableGenerateButton(isActual){
	/*if($('#id_list').val()==''){
		$('#bGenerate').addClass('authorizationDisabled');
	} else {
		$('#bGenerate').removeClass('authorizationDisabled');
	}*/
}
function disableWorkup(isActual){
	if(isActual =='1'){
		generalBL_disablePage();
		$('#newButton').addClass('authorizationDisabled');
	}
}
function disableNewWorkup(isActual){
	if(isActual == '1'){
		$('#workup_dataTableStructButtons button.dataTableApiNew').addClass('authorizationDisabled');
		$('#workups_dataTableStructButtons button.dataTableApiNew').addClass('authorizationDisabled');
	}
}

function experimentReturnToActiveStep(isActual,protocolTypeName){
	if(isActual == '1'){
		//generalBL_disablePage(['conclussion','shortDescription','STATUS_ID','documents','calculateMassBalFieldsButton','addStreamButton','webixMassBalanceTable','planned_actual','quickAction'],true);
		generalBL_disableTab('GeneralTab');
		if(protocolTypeName == 'Continuous Process'){
			$('#reactants_dataTableStructButtons button.dataTableApiAdd').addClass('authorizationDisabled');
		    $('#solvents_dataTableStructButtons button.dataTableApiAdd').addClass('authorizationDisabled');
		    $('#products_dataTableStructButtons button.dataTableApiAdd').addClass('authorizationDisabled');
		    $('[id="chemDoodleAct"]').addClass('disabledChemDoodle');
		    $('[id="chemDoodleAct"]').css('border-color', '');
		    $('[id="chemDoodleAct"]').css('outline', '');
		    $('#chemDoodleButton2').addClass('disablePage');
		    $('#chemDoodleButton').addClass('disablePage');
		    $('#copyMultiStep').addClass('disablePage');
		} else if(protocolTypeName == 'Formulation') {
			generalBL_disablePage(['action']);
			return;
	    } else {
	    	generalBL_disableTab('ReactionTab');
	    }
		generalBL_disableTab('EquipmentTab');
		generalBL_disableTab('SamplesTab');
		generalBL_disableTab('FormulationTab');
//		try {
//			$(deleteButton).addClass('authorizationDisabled'); 
//		} catch(e) {}
		
		
		$("#STATUS_ID_chosen").prop("disabled",false);
    	$("#STATUS_ID_chosen").removeClass('disablePage');
    	
    	setDisabledByElementId('conclussion', false); 
		
		$('#shortDescription').removeClass('disablePage');
		
		$('#planned_actual').removeClass('disablePage');
		$('[name="planned_actual"]').removeClass('disablePage');
		$('[name="planned_actual"]').prop('disabled',false);
		
		$('#workups_dataTableStructButtons button.dataTableApiNew').addClass('authorizationDisabled');
		$('#products_dataTableStructButtons button.dataTableApiOptional1').removeClass('disablePage');
	}
}

function experimentReturnToActive(isActual) {
	 if (isActual == '1') {
		//'MASSBALLANCETYPE_ID', 'PRODUCTNAME_ID', 'equivalentPerMole','PERMOLEUOM_ID', 'massBalancet',
		 generalBL_disablePage(['reasonForChange', 'APPROVER_ID',
				'experimentGroup', 'conclussion', 'description', 'STATUS_ID',
				'documents',
				'action','selfTests',
				'webixAnalytTable', 'manualResultsTable', 'manualResultsMS',
				'calculationButton','calculateButton','testedComponents',
				'chromatograms', 'resultsTable','planned_actual','steps','spreadsheetResults'], true); // yp add steps fix bug 7775
		generalBL_enableTab('BottlesResultsTab');
		generalBL_enableTab('CorrosionResultsTab');
		generalBL_enableTab('TSUResultsTab');
		generalBL_enableTab('ViscosityResultsTab');
		generalBL_enableTab('CalculationsTab');
		generalBL_enableTab('MassBallance1Tab');
		generalBL_enableTab('MassBallance2Tab');
		generalBL_enableTab('MassBallance3Tab');
		
		//setRichTextEditorDisabled('conclussion', false, 'AUTHEZ');
		
		$('button[name="webixContainerButtons"]').removeClass('disablePage');
		
		//setRequiredByElementId('reasonForChange',true);

		$('#steps_dataTableStructButtons button.dataTableApiNew').addClass('authorizationDisabled');
		$('#workups_dataTableStructButtons button.dataTableApiNew').addClass('authorizationDisabled');
		
        $('.btnResToUpdate').removeClass('disablePage');
	}
}

function ExpAnDisableFieldsByStatus(isActual,status_) {
	if(isActual=="1"){//approved experiment not return to active status
	 var arr = status_.split(':');
	 var status= arr[0];
     var currentStatus = arr[1];
     
     /**current status*/
     if(currentStatus=='Finished'||currentStatus=='Failed'||currentStatus=='Cancelled'||currentStatus=='Completed'||currentStatus=='Approved'){
    	if(currentStatus=='Completed'||currentStatus=='Aproved'){
    		generalBL_disableTab('SafetyTab');
    	}
    	else{
    		 generalBL_enableTab('SafetyTab');
    	}
    	generalBL_disableTab('SamplesTab');
    	generalBL_disableTab('TestedComponentsTab');
        generalBL_disableTab('EquipmentTab');
    	generalBL_disableTab('ConditionsHPLCTab');
    	generalBL_disableTab('ConditionsGCTab');
    	setDisableWebixTables('1','');
    	setDisabledByElementId('groupsCrew',true);
     }
     else{
    	 generalBL_enableTab('SamplesTab');
    	 generalBL_enableTab('TestedComponentsTab');
    	 generalBL_enableTab('EquipmentTab');
    	 generalBL_enableTab('ConditionsHPLCTab');
    	 generalBL_enableTab('ConditionsGCTab');
     	setDisableWebixTables('0','');
     	setDisabledByElementId('groupsCrew',false);
     }
     if(currentStatus!='Planned'){
    	 setDisabledByElementId('experimentSeries',true);
     }else{
    	 setDisabledByElementId('experimentSeries',false);
     }
     
     /**status*/
     if(status=="Failed"){
 		var list=['description','APPROVER_ID','OWNER_ID','estimatedStartDate','STATUS_ID','experimentGroup','aim','conclussion'];
 		for (i = 0; i < list.length; i++) {
 			setDisabledByElementId(list[i],true);
 		}
 	 } else if(status == 'Finished'){
 		generalBL_disablePage(['STATUS_ID','conclussion','reasonForChange','documents'],1);
 	 } else if(status == 'Completed' || status == 'Approved') {
 		 if(status == 'Completed'){
 	 		generalBL_disablePage(['STATUS_ID','reasonForChange','steps','action','selfTests','workups','feedbackHistory','updateVersion','EXPERIMENTGROUP_ID'], true);
 		 }
 		 else generalBL_disablePage(['STATUS_ID','reasonForChange','steps','action','selfTests','workups','feedbackHistory','updateVersion'], true);
 	 } else if(status == 'Cancelled'){
    	 generalBL_disablePage(['STATUS_ID'], true);
     }
     if(status!="Planned" && status!="Active"){
    	setDisabledByElementId('usersCrew',true);
     }
     if (status == 'Approved') {
  		var list = [ 'EXPERIMENTGROUP_ID', 'generatorButton' ];
  		for (i = 0; i < list.length; i++) {
  			setDisabledByElementId(list[i], true);
  		}
  	} 
	}    
}

function ExpAnMandatoryFieldsByStatus(isActual,status_) {
	//if(isActual=="1"){
	 var elements = [];
	 var arr = status_.split(':');
	 var status= arr[0];
     var currentStatus = arr[1];
	 var experimentType = $('#EXPERIMENTTYPE_ID option:selected').text();
     
     /**current status*/
     if(currentStatus=='Active'){
    	 elements.push('APPROVER_ID','description');
    	 if(isActual == "1"){//'$P{ISENABLESPREADSHEET}'=='No'
    		 if(experimentType!='General'){
    			 elements.push('instrumentsTable');
    		 }
    	 }
     }else{
    	 setRequiredByElementId('APPROVER_ID',false);
    	 setRequiredByElementId('description',false);
    	 if(experimentType!='General'){
    		 setRequiredByElementId('instrumentsTable',false);
    	 }
     }
     if(currentStatus=='Completed'){
    	 elements.push('conclussion');
     }
     else{
    	 setRequiredByElementId('conclussion',false);
     }
     
     /**status*/
     if(status!='Approved'){
    	 elements.push('estimatedStartDate','aim','OWNER_ID');
     }
     
     for (i = 0; i < elements.length; i++) {
    	 setRequiredByElementId(elements[i],true);
    	 }
	//}
}

function MaintMandatoryGeneralTab(isActual,closeBreakdown){
	if(isActual=="1"){
		var elements = [];
		if(closeBreakdown=="1"){
			elements.push('STATUS_ID','serialNumber','SITE_ID','LABORATORY_ID','maintenanceDate','REPORTEDBY1_ID',
					'TECHNICIAN_ID','maintDescription','MAINTENANCETYPE_ID');
			for (i = 0; i < elements.length; i++) {
				setRequiredByElementId(elements[i],true);
	    	 }
		}else{
			elements.push('STATUS_ID','serialNumber','SITE_ID','LABORATORY_ID','maintenanceDate','REPORTEDBY1_ID',
					'TECHNICIAN_ID','technician','maintDescription','MAINTENANCETYPE_ID');
			for (i = 0; i < elements.length; i++) {
		    	 setRequiredByElementId(elements[i],false);
		    	 }
		}
	}
} 
function StepDisableMandatoryFieldsByStatus(isActual,status_) {
	if(isActual=="1"){
		var arr = status_.split(':');
		 var expStatus= arr[0];
	     var currentStatus = arr[1];
	     var stepStatus= arr[2];
	     
	     /**current status*/
		 if(currentStatus !='Finished'){
			 setDisabledByElementId('reactFinishTime',true);
		 }
		 else{
			 setDisabledByElementId('reactFinishTime',false);
		 }
		 if(currentStatus !='Active'){
			 setDisabledByElementId('reactStartTime',true);
		 }
		 else{
			 setDisabledByElementId('reactStartTime',false);
		 }
		 if(currentStatus !='Planned'&& currentStatus !='Active'){ //kd 05022020 added &&currentStatus !='Active' according to task 24776
			 setDisabledByElementId('stepName',true);
			 setRequiredByElementId('stepName',false);
		 }
		 else{
			 if(currentStatus =='Planned'){
				 setRequiredByElementId('stepName',true);
			 }
			 setDisabledByElementId('stepName',false)
		 }
		 
	     /**status*/
	     if(expStatus != 'Finished' && expStatus != 'Active'){
	    	 setDisableWebixTables("1",'')
	     }
	     if(expStatus != 'Active'){
	    	 //disableNewWorkup("1");
	    	 if(expStatus != 'Planned'){
	    		 var list=['COMPVOL_ID','MASSUOM','REACTORVOL_ID'];
	     		 for (i = 0; i < list.length; i++) {
	     			setDisabledByElementId(list[i],true);
	     		 }
	    	 }
	     }
	     
	     if(stepStatus == 'Cancelled'){
	    	 generalBL_disablePage(['STATUS_ID','action','selfTests','workups',], true);
	     }
	     
	     if(stepStatus == 'Finished'){
	    	 $('#action_dataTableStructButtons button.dataTableApiNew').addClass('authorizationDisabled');
	    	 $('#selfTests_dataTableStructButtons button.dataTableApiNew').addClass('authorizationDisabled');
	    	 $('#workups_dataTableStructButtons button.dataTableApiNew').addClass('authorizationDisabled');
	     }

//		/* prevent from disabling table on change status (currentStatus), because enabling not supported yet*/
//		 TODO: add support to enable editable table
		 
//		 if(currentStatus == 'Planned' || currentStatus == 'Active'){
//			 setDisabledByElementId('Parameters',false);
//		 } 
//		 else
//		 {
//			 setDisabledByElementId('Parameters',true);
//		 }
		 
		 if(!(stepStatus == 'Planned' || stepStatus == 'Active')){
			 setDisabledByElementId('Parameters',true);
		 } 
	}
}

function ExpDisableMandatoryByStatus(isActual,status_) {
	//if(isActual=="1"){
	 var mandatoryElements = [];
	 var arr = status_.split(':');
	 var status= arr[0];
     var currentStatus = arr[1];
     
     if(arr.length>2){
    	 var isRunStarted = arr[2];
    	 if(isRunStarted==1){
    		 //disable step
    		 changeSingleDTLabelByDisabledState('steps', true);
    		 setDisabledByElementId('diagram',true);
    	 }
     }
     
     /**current status*/
     
     if(currentStatus!='Planned'){
    	 mandatoryElements.push('APPROVER_ID');
 	     setDisabledByElementId('experimentSeries',true);
     }
     else{
    	 setRequiredByElementId('APPROVER_ID',false)
    	 setDisabledByElementId('experimentSeries',false);
     }
     if(currentStatus=='Active' && status == 'Active'){
    	// mandatoryElements.push('actualStartDate','completionDate'); 
    	 generalBL_enableTab('ResultsTab');
     }
     if(currentStatus=='Finished'){
		 enableDetailsTabViewButtons('1','');
	 }
     
     if(currentStatus=='Approved' || currentStatus=='Completed'){
    	 mandatoryElements.push('conclussion');
     }
     else{
    	 setRequiredByElementId('conclussion',false);
     }
     
     for (i = 0; i < mandatoryElements.length; i++) {
    	 setRequiredByElementId(mandatoryElements[i],true);
    	 }
	//}
     if(currentStatus !='Planned' && currentStatus !='Finished' && currentStatus !='Active'){
		 $('#Parameters_dataTableStructButtons button.dataTableApiNew').addClass('authorizationDisabled');
		 $('#Parameters_dataTableStructButtons button.dataTableApiRemove').addClass('authorizationDisabled');
	 }
     else{
    	 $('#Parameters_dataTableStructButtons button.dataTableApiNew').removeClass('authorizationDisabled');
		 $('#Parameters_dataTableStructButtons button.dataTableApiRemove').removeClass('authorizationDisabled');
     }
     
     /**status*/
     if(status == 'Completed' || status == 'Approved') {
    	 var enabledList=['STATUS_ID','reasonForChange','steps','action','selfTests','workups','feedbackHistory','updateVersion'];
    	 if(status == 'Completed') { 
    		 enabledList.push('EXPERIMENTGROUP_ID');
    	 }
  		generalBL_disablePage(enabledList, true);
  	
  		
  	 }
     if(status=='Completed' ||status=='Approved' || currentStatus =='Failed' || currentStatus =='Cancelled'){ 
    	 enableDtailsTabViewButtons('1','');
     }
     if(status=='Completed'){
    	 var list=['aim','conclussion'];
 		 for (i = 0; i < list.length; i++) {
 			setDisabledByElementId(list[i],true);
 		 }
     }else{
    	 mandatoryElements.push('aim');
     }
     
     if(status=='Finished' ||status=='Completed' ||status=='Approved' || status =='Failed' || status =='Cancelled'){ 
    	 var list=['request','samples','Parameters'];
 		 for (i = 0; i < list.length; i++) {
 			setDisabledByElementId(list[i],true);
 		 }
     }
     if(status=='Completed' ||status=='Approved' || status =='Failed' || status =='Cancelled'){ 
 		generalBL_disableTab('MassBallance1Tab');
 		generalBL_disableTab('MassBallance2Tab');
 		generalBL_disableTab('MassBallance3Tab');
     }
     
     if(status == 'Cancelled'){
    	 generalBL_disablePage(['STATUS_ID','steps','action','selfTests','workups'], true);
     }
     if(status == 'Finished'){
    	 $('#steps_dataTableStructButtons button.dataTableApiNew').addClass('authorizationDisabled');
     }
     if(status!='Planned'){
    	 var list=['equiptPreparationInstruction','safetyComments'];
 		 for (i = 0; i < list.length; i++) {
 			setDisabledByElementId(list[i],true);
 		 }
     }
     if (status == 'Approved') {
 		var list = [ 'EXPERIMENTGROUP_ID', 'generatorButton' ];
 		for (i = 0; i < list.length; i++) {
 			setDisabledByElementId(list[i], true);
 		}
 	} 
     
}

function ExpForDisableMandatoryByStatus(isActual,status_) {
	//if(isActual=="1"){
	 var mandatoryElements = [];
	 var arr = status_.split(':');
	 var status= arr[0];
     var currentStatus = arr[1];
     
     /**current status*/
     if(currentStatus=='Finished' ||currentStatus=='Completed' ||currentStatus=='Approved' || currentStatus =='Failed' || currentStatus =='Cancelled'){ 
    	 if(status == currentStatus){
	    	 var list=['request','samples','spreadsheetExcel','isLockSpreadsheet','enableSpreadsheet','SPREADSHEETTEMPLATE_ID','show','plannedCompositions','density','batchSize','BATCHSIZE_UOM'];
	 		 for (i = 0; i < list.length; i++) {
	 			setDisabledByElementId(list[i],true);
	 		 }
    	 }
     }
     else{
    	 var list=['request','samples'];
 		 for (i = 0; i < list.length; i++) {
 			setDisabledByElementId(list[i],false);
 		 }
     }
     if(currentStatus!='Planned'){
    	 setRequiredByElementId('APPROVER_ID',true);
    	var list=['equiptPreparationInstruction','experimentSeries','safetyComments'];
 		 for (i = 0; i < list.length; i++) {
 			setDisabledByElementId(list[i],true);
 		 }
     }
     else{
    	 setRequiredByElementId('APPROVER_ID',false);
    	 setDisabledByElementId('equiptPreparationInstruction', false);
    	 setDisabledByElementId('safetyComments', false);
     	 setDisabledByElementId('experimentSeries',false);
     }
     
     if(status!='Planned'){
    	 setDisabledByElementId('FORMULATIONTYPE_ID',true);
     }
     if(currentStatus!='Planned'){
    	 setRequiredByElementId('FORMULATIONTYPE_ID',true);
     } else {
    	 setRequiredByElementId('FORMULATIONTYPE_ID',false);
     }
     disableFormulationType("1","plannedCompositions");
     validateDensityRequired("1","plannedCompositions");
     
     
//   if (currentStatus=='Active'){
//	 mandatoryElements.push('actualStartDate','completionDate');
// } // yp 20022020 it updated by event
 /*if(currentStatus=='Approved'){
	 mandatoryElements.push('approvalDate'); 
 }
 else{
	 setRequiredByElementId('approvalDate',false);
 }*/
     
     /**status*/
     if(status == 'Completed' || status == 'Approved') {
    	 var enabledList=['STATUS_ID','reasonForChange','steps','action','selfTests','workups','feedbackHistory','updateVersion'];
    	 if(status == 'Completed') { // yp 28012021 - fix bug 8806 - make approver_id enabled in completed
    		 enabledList.push('APPROVER_ID');
    		 enabledList.push('EXPERIMENTGROUP_ID');
    	 }
  		generalBL_disablePage(enabledList, true);
  	 }
     
     if(currentStatus=='Approved' || currentStatus=='Completed'){
    	 setRequiredByElementId('conclussion',true);
     }
     else{
    	 setRequiredByElementId('conclussion',false);
     }
     
     if(status == 'Cancelled'){
    	 generalBL_disablePage(['STATUS_ID','steps','action','selfTests','workups'], true);
     }
     
     if(status=='Completed' ||status=='Approved' || currentStatus =='Failed' || currentStatus =='Cancelled'){ 
    	 enableDtailsTabViewButtons('1','');
     }
     if (status == 'Approved') {
  		var list = [ 'EXPERIMENTGROUP_ID', 'generatorButton' ];
  		for (i = 0; i < list.length; i++) {
  			setDisabledByElementId(list[i], true);
  		}
  	} 
	//}
}

function setDisabledOnSelfTestTypeChange(isActual, info_) {
	if($('#TYPE_ID option:selected').text() == "Non-Numeric") {   
		$('#AnalytMethods_dataTableStructButtons button.dataTableApiNew').addClass('authorizationDisabled');
		$('#AnalytMethods_dataTableStructButtons button.dataTableApiAdd').addClass('authorizationDisabled');
		
		$('#columns_dataTableStructButtons button.dataTableApiNew').addClass('authorizationDisabled');
		$('#columns_dataTableStructButtons button.dataTableApiEditShared ').addClass('authorizationDisabled'); 

		setDisabledByElementId('instrumentExt',false);
	} else {
		$('#AnalytMethods_dataTableStructButtons button.dataTableApiNew').removeClass('authorizationDisabled');
		$('#AnalytMethods_dataTableStructButtons button.dataTableApiAdd').removeClass('authorizationDisabled');
		
		$('#columns_dataTableStructButtons button.dataTableApiNew').removeClass('authorizationDisabled');
		$('#columns_dataTableStructButtons button.dataTableApiEditShared ').removeClass('authorizationDisabled');
		 
		setDisabledByElementId('instrumentExt',true);
	}
}

function setDisabledRequestNew(isActual)
{
	if(isActual=="1"){
	$('.new-main-button').addClass('authorizationDisabled');
	}
}

function setDisabledRequestSaveAndSend(isActual)
{
	if(isActual=="1"){//fixed bug 7985
		$('#saveAndSendButton').addClass('authorizationDisabled');
	    $('#saveAndSendFloatingButton').addClass('authorizationDisabled');
	}
}

function hideUpdateResButton(isActual,domId){
	if(isActual=="1"){
		$('#'+domId+'_dataTableStructButtons button.dataTableApiOptional1').css('display', 'none');
	}
}

function disabledButtonOnNewSelfTest(isNew) { //fix bug 7810 
	//disable new entity in selftest is not saved
	if(isNew == '1') {
		$("#newButton").addClass('authorizationDisabled');
		$("#newFloatingButton").addClass('authorizationDisabled');
		$("#newSampleFloatingButton").addClass('authorizationDisabled');
	}
}

function enableRequestSampleTableLabel(){
	$('.dataTableApiSelectInfoLabel').removeClass('disablePage');
	$('.dataTableApiSelectInfoLabel').prop('disabled', false);
	$('#chbSelectAllNone_samples').removeClass('disablePage');
	$('#chbSelectAllNone_samples').prop('disabled', false);
}

function disableSampleTableExperimentCp(runNumber){
	if (runNumber != "0" && runNumber!="") {
		$('#samples_dataTableStructButtons button.dataTableApiEditShared ').addClass('authorizationDisabled');
	}else{
		$('#samples_dataTableStructButtons button.dataTableApiEditShared ').removeClass('authorizationDisabled');
	}
}

function disableReactionTables(isActual){
	if(isActual=="1"){
		$('#reactants_dataTableStructButtons button.dataTableApiAdd').addClass('authorizationDisabled');
	    $('#solvents_dataTableStructButtons button.dataTableApiAdd').addClass('authorizationDisabled');
	    $('#products_dataTableStructButtons button.dataTableApiAdd').addClass('authorizationDisabled');
	    $('[id="chemDoodleAct"]').addClass('disabledChemDoodle');
	    $('[id="chemDoodleAct"]').css('border-color', '');
	    $('[id="chemDoodleAct"]').css('outline', '');
	}
}

function disableRecipe(isActual,userId){
	// most of this logic is written in in bug 8816
	if(isActual=="1"){
		var statusName = $('#STATUS_ID option:selected').text(); // hold the last status selection 
		var laststatusname =  $('#STATUS_ID').attr('lastselectedname'); // contains the original value before the change
		var approverId = $('#APPROVER_ID option:selected').val();
		var isExportedToDatabank = $('#exportToDataBank').val();
		//generalBL_disablePage by status (in Completed/Approved/Cancelled) ...
		if(laststatusname=="Completed" || laststatusname=="Approved"){
			var enabledFields =[];
			if(isExportedToDatabank=="0"){
				enabledFields.push('STATUS_ID','exportRecipe','reasonForChange');
			}
			if(laststatusname=='Completed') {
				//APPROVER_ID disabled if the CREATOR_ID <> userId (=> enabled CREATOR_ID == userId -> pushed to enabledFields)
				if($('#CREATOR_ID').val() == $('#userId').val()) {
					enabledFields.push('APPROVER_ID');
				}
				//APPROVER_ID mandatory in Completed
				setRequiredByElementId('APPROVER_ID', true); 
			}
			generalBL_disablePage(enabledFields,enabledFields.length>0?1:0);
		} else if(laststatusname=="Cancelled"){
			generalBL_disablePage();
		} else if(laststatusname=='Active' || laststatusname=="Planned") {
			//APPROVER_ID disabled if the CREATOR_ID <> userId
			setDisabledByElementId('APPROVER_ID',$('#CREATOR_ID').val() != $('#userId').val());
			//APPROVER_ID mandatory on change Active -> Completed or Approved
			setRequiredByElementId('APPROVER_ID', (laststatusname=='Active' && (statusName=='Completed' || statusName=='Approved')));
		}
	}
}

function setDisableMandatoryCompositionsFields(domId){
	disableFormulationType("1",domId);
	setBatchSizeMandatory("1",domId);
	setDensityMandatory("1",domId);
}

function disableFormulationType(isActual,domId){
	if(!$.fn.DataTable.isDataTable('#'+domId)){
		return;
	}
	var compositionTableRows=$('#'+domId+' td[class="dataTables_empty"]').length;
	if(compositionTableRows>0){
		setDisabledByElementId('FORMULATIONTYPE_ID',false);
		}
	else{
		setDisabledByElementId('FORMULATIONTYPE_ID',true);
	}
	
	
}
function validateDensityRequired(isActual,domId){
	/*if($('#density').val()!=''){
		return;
	}*/
	var compositionTableRows=$('#'+domId+' td[class="dataTables_empty"]').length;
	 var compType = $('#COMPOSITIONTYPENAME').val();
	 if(compType == 'Liquid' && compositionTableRows==0){//the formulation type is liquid and the table is not empty
		 setRequiredByElementId('density',true);
		 if($('#density').val()==''){
			 $('#density').val($('#density').attr('oldvalue'));
		 }
	 } else {
		setRequiredByElementId('density',false); 
		$('#density').css('border-color', '').css('outline', '');
	 }
}

function setBatchSizeMandatory(isActual,domId){
	var isCompositionTableRowsExist=$('#plannedCompositions td[class="dataTables_empty"]').length==0;
	if(isCompositionTableRowsExist){//$('#'+$('#'+domId).attr('batchSizeElement')).val();
		setRequiredByElementId('batchSize',true);
		setRequiredByElementId('BATCHSIZEUOM_ID',true);
		if($('#batchSize').val()==''){
			$('#batchSize').val($('#batchSize').attr('oldvalue'));
		}
	}
	else{
		setRequiredByElementId('batchSize',false);
		setRequiredByElementId('BATCHSIZEUOM_ID',false);
		$('#batchSize').css('border-color', '').css('outline', '');
	}
}
function setDensityMandatory(isActual,domId){
	validateDensityRequired(isActual,domId);
}

function changeProjectByType(isActual,projectType) {
	if(projectType == 'Chemistry'){

		$('[id="lactiveIngredient"]').parent().css('visibility', 'visible');
		$('[id="INVITEMMATERIAL_ID"]').parent().css('visibility', 'visible');
		$('[id="materialSearch"]').parent().css('visibility', 'visible');
		$('[id="materialSearch"]').css('visibility', 'visible'); //display icon
		
		
		$('[id="FORMULATIONSECTOR_ID"]').parent().css('visibility', 'hidden');
		$('[id="lFORMULATIONSECTOR_ID"]').parent().css('visibility', 'hidden');		
		$("#lFORMULATIONSECTOR_ID .asterisk").css('visibility', 'hidden'); // remove asterisk to sector
		$('[id="searchComponent"]').parent().css('visibility', 'hidden');
		$('[id="lsearchComponent"]').parent().css('visibility', 'hidden');
		$('[id="components"]').parent().css('visibility', 'hidden');
		$('[id="components_Parent"]').css('visibility', 'hidden'); //hide the border of the table
		$('#components_Parent').parent().css('display', 'none');
		
		$('[id="lmcwCodeProject"]').parent().css('visibility', 'visible');
		$('[id="mcwCodeProject"]').parent().css('visibility', 'visible');

		setDisabledByElementId('FORMULATIONTYPE_ID', true);
		setRequiredByElementId('FORMULATIONSECTOR_ID', false); 
		setRequiredByElementId('components', false); 
		
	} else { //project type is Formulation
		
		$('[id="lactiveIngredient"]').parent().css('visibility', 'hidden');
		$('[id="INVITEMMATERIAL_ID"]').parent().css('visibility', 'hidden');
		$('[id="materialSearch"]').parent().css('visibility', 'hidden'); //icon not hide - builder? 
		$('[id="materialSearch"]').css('visibility', 'hidden'); //hide icon

		$('[id="FORMULATIONSECTOR_ID"]').parent().css('visibility', 'visible');
		$('[id="lFORMULATIONSECTOR_ID"]').parent().css('visibility', 'visible');
		
		$("#lFORMULATIONSECTOR_ID .asterisk").css('visibility', 'visible'); // add asterisk to sector
		$('[id="searchComponent"]').parent().css('visibility', 'visible');
		$('[id="lsearchComponent"]').parent().css('visibility', 'visible');
		$('[id="components"]').parent().css('visibility', 'visible');//display without border
		$('[id="components_Parent"]').css('visibility', 'visible'); //add the border to the table
		$('#components_Parent').parent().css('display', '');
		
		$('[id="lmcwCodeProject"]').parent().css('visibility', 'hidden');
		$('[id="mcwCodeProject"]').parent().css('visibility', 'hidden');
		
		setDisabledByElementId('FORMULATIONTYPE_ID', false); //additional condition in builder
		setRequiredByElementId('FORMULATIONSECTOR_ID', true); 
		setRequiredByElementId('components', true); 
	}
}
 function setRequiredComponents(){
	 setRequiredByElementId('components', true); 
 }
 
 function disableComponentTable(isActual,domId){
		if(!$.fn.DataTable.isDataTable('#'+domId)){
			return;
		}
		var recipeUsagesTableRows=$('#'+domId+' td[class="dataTables_empty"]').length;
		if(recipeUsagesTableRows>0){
			setDisabledByElementId('compositions',false);
			}
		else{
			setDisabledByElementId('compositions',true);
		}
		
		
	}
 
 function disableStepMinFr(isActual){
		if(isActual == '1'){
			generalBL_disablePage();
			$('#stepName').removeClass('disablePage');
		}
	}
