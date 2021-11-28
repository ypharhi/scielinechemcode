/**
 * ElementDataTableApiImp BL General integration
 * 
 * @param domId
 * @returns
 */
function elementDataTableApiImpBL(domId) {
    
	onElementDataTableApiIsNew(domId); // Invoke code when isNew = 1.
	// ab 16092020: function is relocated to initElementDataTableApi()
	//    changeElementDataTableApiCaption(domId); // Change caption on special cases.
    elementDataTableApiImpGeneralDisabled(domId); // Disable datatableApi's
													// buttons after loading.
    $('#' + domId + ' th:contains("_uom")').text("UOM"); // change th of
									 						// columns that
															// 'UOM'
    var _formCode = $('#formCode').val();
    /* 'isPageInitFlag' flag is TRUE when table is first loaded on page initialization, 
     *   use this flag when there is not need to run specific code for current domId again (on onElementDataTableApiChange() call) */
    var isPageInitFlag = $('#'+domId+'_isPageInitFlag').val();
    if(isPageInitFlag == 'true') {
    	isPageInitFlag = true;
    	$('#'+domId+'_isPageInitFlag').val('false');
    }
    else {
    	isPageInitFlag = false;
    }
    
    if ($('#formCode').val() == "InvItemMaterialsMain") { // disable batch table (lower table) 'new' button when the upper table's struct is 'column'
        /*  if ($('#' + domId + '_structCatalogItem').val() == "InvItemColumn") {
        $('#lowerTable_dataTableStructButtons button.dataTableApiNew').addClass('disabledclass'); // .button("option","disabled",true);
		$('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').css('display','none');
        $('#' + domId + '_criteriaCatalogItem option[value="Depleted Items"]').toggleOption(false);
        $('#' + domId + '_criteriaCatalogItem option[value="Active Batches"]').toggleOption(false);
        $('#' + domId + '_criteriaCatalogItem option[value="Standards"]').toggleOption(false);
        $('#' + domId + '_criteriaCatalogItem option[value="Items about to expire"]').toggleOption(true);
        
    } else if ($('#' + domId + '_structCatalogItem').val() == "InvItemMaterial") {*/
        $('#lowerTable_dataTableStructButtons button.dataTableApiNew').removeClass('disabledclass'); // .button("option",
																										// "disabled",
																										// false);
       /* $('#' + domId + '_criteriaCatalogItem option[value="Depleted Items"]').toggleOption(true);
        $('#' + domId + '_criteriaCatalogItem option[value="Items about to expire"]').toggleOption(false);
        $('#' + domId + '_criteriaCatalogItem option[value="Active Batches"]').toggleOption(false);
        $('#' + domId + '_criteriaCatalogItem option[value="Standards"]').toggleOption(false);
        */
        $('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').css('display','inline-block');
        $('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').text('Cancel');
      $('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').off('click');
      	$('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').attr("onclick","openConfirmMaterialCancelationDialog('"+domId+"')");
   
  

    if ($('#' + domId + '_structCatalogItem').val() == "InvItemBatch") {
        $('#' + domId + '_criteriaCatalogItem option[value="Depleted Items"]').toggleOption(false);
    }
/*
 * } else if ($('#formCode').val() == "InvItemInstrument") { var str = $('#' +
 * domId + '_structCatalogItem').val(); // disable new button: // 1)
 * Maintenance form cannot be created, when instrument status=new // 2)
 * Calibration cannot be created when instrument status is 'Malfunction' if
 * ((str == 'InvItemCalibration') && ($('#lastStatusName').val() ==
 * "Malfunction")) { $('[id="' + domId + '_Parent"]
 * button.dataTableApiNew').addClass('disabledclass'); } else if ((str ==
 * 'InvItemMaintenance') && ($('#lastStatusName').val() == "New")) {
 * $('[id="' + domId + '_Parent"]
 * button.dataTableApiNew').addClass('disabledclass'); }
 */





   

}else if($('#formCode').val() == 'InvItemColumnsMain'){
	$('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').css('display','none');
}else if($('#formCode').val() == 'TemplateMenuMain'){
    	 $('#upperTable_dataTableStructButtons button.dataTableApiAdd').css('display','none');
    }else if($('#formCode').val() == 'SelfTest' && domId == 'chromatograms'){
    	$('#chromatograms_dataTableStructButtons button.dataTableApiAdd').text('Add Link');
    	$('#AnalytMethods_criteriaCatalogItem').parent().css('display','none');	 
    }else if($('#formCode').val() == 'SelfTest' && domId == 'instruments'){
    	clearComboListSelfTestResultWebixElement('instrument');
//    	for (var i = 0; i < $('#'+domId+' tbody tr').length; i++) {
    		//updateSelfTestResultWebixElementInstrum($('#'+domId+' tbody tr td select:eq('+i+')').val(),$('#'+domId+' tbody tr td select:eq('+i+')').text());
		updateWebixElementList('instrument', domId); //old name and signature id updateSelfTestResultWebixElementInstrum(domId);
		updateInstrumentFromList();
//		if(($('#columns').find('.chosen-results').length) - ($('#columns').find('.linkElement').length) > 0) {
//			dataTableAddRow('columns');
//		} 
//		}		
		try {
			onElementDataTableApiChange('columns');
		} catch(e) {
			console.log("selftest - error in column table render after instrument change(1)");
		}
		
    }else if($('#formCode').val() == 'Request'){
    	if(domId == 'documents'){
    		$('#documents_dataTableStructButtons button.dataTableApiAdd').text('Add Link');
    	}else if(domId == 'operartinTypeTable'){
    		$('#'+domId+'_dataTableStructButtons button.dataTableApiAdd').text('Add Row');
    	}else if(domId == 'materialsPeaksTable'){
    		$('#'+domId+'_dataTableStructButtons button.dataTableApiAdd').css('display','none');
    	}else if(domId == 'samples'){
    		$('#' + domId + '_dataTableStructButtons button.dataTableApiEdit').off('click');
    		$('#' + domId + '_dataTableStructButtons button.dataTableApiEdit').attr("onclick", "openSampleSelectHolderForRequestSample('"+domId+"')");
    		$('#' + domId + '_dataTableStructButtons button.dataTableApiEdit').removeClass('disabledclass');     
    		$('#' + domId + '_dataTableStructButtons button.dataTableApiAdd').css('display','none');
    		$('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').css('display','none');
    	}   	
    } else if($('#formCode').val() == 'AltMaterialSearch'){
    	if($("#"+domId+" tr:last").text() == "No data available in table"){
    			$('.dataTables_empty').text("No alternative material found");
    	}
    } else if($('#formCode').val() == 'SearchReport'){
    	if(domId == 'resentlyResults' || domId == 'inventoryResults'){
    		if($('#isSavedSearch').val()!='1' && $(parent.document).find("#"+domId+" tr:last").text() == "No data available in table"){
    			$('.dataTables_empty').text("No results were found");
    			if(domId == 'inventoryResults' && !$('#'+domId).is(':hidden') && $('input[name="searchOrNavigate"]:checked').val() == "Molecule Search"){
    				displayAlertDialog(getSpringMessage('NO_RESULT_FOUND_MOLECULE_SEARCH'),null,{button:"OK"});//fix bug 8091 - display with OK button
    				$('#searchInDocsOnly2').prop('checked',true);
    			}
    			else if(domId == 'inventoryResults' && !$('#'+domId).is(':hidden') && $('input[name="searchOrNavigate"]:checked').val() != "Free Text"){
    				displayAlertDialog(getSpringMessage('NO_RESULT_FOUND_INVENTORY'),null,{button:"OK"});
    			}
    			else if((!$('#resentlyResults').is(':hidden') && !$('#inventoryResults').is(':hidden') && $(parent.document).find("#resentlyResults tr:last").text() == "No results were found" && $(parent.document).find("#inventoryResults tr:last").text() == "No results were found"
    					|| $("#searchType").val()=="Inventory" && !$('#inventoryResults').is(':hidden') && $(parent.document).find("#inventoryResults tr:last").text() == "No results were found"
						 ||  $("#searchType").val()=="Project" && !$('#resentlyResults').is(':hidden') && $(parent.document).find("#resentlyResults tr:last").text() == "No results were found")
						 && $('input[name="searchOrNavigate"]:checked').val() == "Free Text"){
    				displayAlertDialog(getSpringMessage('NO_RESULT_FOUND'),null,{button:"OK"});
    			}
    		}
    		//if the search is saved and rows are checked in inventoryResults table - resentlyResult table should be display
    		if(domId == 'inventoryResults' && $('#isSavedSearch').val()=='1'){
    			var materialChecked = $('#inventoryResults').DataTable().$('input[Type ="checkbox"]').is(':checked');
    		    if(materialChecked){
    		    	onElementDataTableApiChange('resentlyResults', null,null,true);
       		        $('#resentlyResults_Parent').css('visibility', 'visible');
       		        }
    		    }
		  //  $('#isSavedSearch').val("0");
    	}
    	$('#advSearchButton').text("Search");
    	$('#generate').text("Search");
    	$('#invAdvancedSearchB').text("Search");
    }
    else if($('#formCode').val() == 'Template'){
    	$('#feedbackHistory_dataTableStructButtons button.dataTableApiAdd').text('Add & Save');
   } else if($('#formCode').val() == 'ExperimentSeries'){
 	  $('#formulationPropertiesTable_dataTableStructButtons button.dataTableApiAdd').css('display','none');
   } else if($('#formCode').val() == 'Sample'){
	   	  $('#sampleResults th:first').text("Main");
	   if(domId == 'sampleResults'){
	   	var table_ = $('#sampleResults').DataTable();
    		 
    		var allData = getformDataNoCallBack(1);
    		// url call
    		var urlParam = "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val()
    				+ "&eventAction=getSampleResultId&isNew=" + $('#isNew').val();

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
    					hideWaitMessage();
    					} else if (obj.data[0].val.toString() != null && obj.data[0].val.toString()!=""){
    						var results = obj.data[0].val.toString();
    						var resultsArray = results.split("-");
    						for (var i = 0; i < resultsArray.length; i++) {
    							table_.$('input[type="checkbox"][value="'+resultsArray[i].split(';')[1]+'"]').prop('checked', true);
    							if(resultsArray[i].split(';')[0]==0){//can remove the checkbox only if the the auto result is analytical- and then can choose a selftest result instead
    								table_.$('input[type="checkbox"][value="'+resultsArray[i].split(';')[1]+'"]').addClass('authorizationDisabled');
    							}
    						}
    					}
					},
					error : handleAjaxError
				});
	   }else if (domId =='resultsUsingtoUpdate'){
		   $('#resultsUsingtoUpdate_dataTableStructButtons button.dataTableApiAdd').text('Update');
		   $('#resultsUsingtoUpdate_dataTableStructButtons button.dataTableApiAdd').attr('onclick','');
		   $('#resultsUsingtoUpdate_dataTableStructButtons button.dataTableApiAdd').click(function(){refreshResultMv();});
		   }
   } else if($('#formCode').val() == 'InvItemSamplesMain'){
	   	  $('#lowerTable th:first').text("Main");
	   	if(domId == 'upperTable'){
	   	    $('#' + domId + '_criteriaCatalogItem option[value="ALL"]').text("All by Date"); // rename the ALL option to 'All by Date'
	   		$('#'+domId+'_dataTableStructButtons button.dataTableApiOptional1').text('Show Results');
	   		$('#'+domId+'_dataTableStructButtons button.dataTableApiOptional1').off('click');
	   		$('#'+domId+'_dataTableStructButtons button.dataTableApiOptional1').click(function(){openSampleResults(domId);});
	   	}
	   	if (domId == 'lowerTable'){
	   		
	   		$('#upperTable_dataTableStructButtons button.dataTableApiNew').css('display', 'none'); // hide the 'new' button
	   	    var table_ = $('#lowerTable').DataTable();
	   	    
    		 
    		var allData = getformDataNoCallBack(1);
    		// url call
    		var urlParam = "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val()
    				+ "&eventAction=getSampleResultMain&isNew=" + $('#isNew').val();

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
    					hideWaitMessage();
    					} else if (obj.data[0].val.toString() != null && obj.data[0].val.toString()!=""){
    						var results = obj.data[0].val.toString();
    						var resultsArray = results.split("-");
    						for (var i = 0; i < resultsArray.length; i++) {
    							table_.$('input[type="checkbox"][value="'+resultsArray[i]+'"]').prop('checked', true);
    							}
    							}
    				table_.$('input[type="checkbox"]').prop("disabled", true);
					
    						},
    						error : handleAjaxError
    					});
	   	}
        
   } else if(domId == 'samples' && $('#formCode').val() == 'InvItemBatch'){
	   var table_ = $('#samples').DataTable(); 
       if($('#SAMPLE_ID').val()!=""){
       	table_.$('input[Type ="checkbox"][class="dataTableApiSelectInfo"][value="'+$('#SAMPLE_ID').val()+'"]').prop("checked",true);
       }
       table_.$('input[Type ="checkbox"][class="dataTableApiSelectInfo"]').change(function(){
       	if($(this).is(':checked')){
       		var val = $(this).val();
       		table_.$('input[Type ="checkbox"][class="dataTableApiSelectInfo"]:not([value ="'+val+'"]):checked').each(function(){
   				$(this).prop("checked",false);
       		});
       	}
       });
       $('#samples_dataTableStructButtons button.dataTableApiOptional1').text('New Request');
       $('#samples_dataTableStructButtons').append($('#samples_dataTableStructButtons button.dataTableApiOptional1'));
       $('#samples_dataTableStructButtons button.dataTableApiOptional1').addClass('disabledclass');
	   $('#samples_dataTableStructButtons button.dataTableApiOptional1').off('click');
	   //$('#samples_dataTableStructButtons button.dataTableApiOptional1').removeClass('disabledclass');
	   $('#samples_dataTableStructButtons button.dataTableApiOptional1').click(function(){doNew('Request');});
	   table_.$('input[Type ="checkbox"][class="dataTableApiSelectInfoLabel"]').change(function(){
		    if(table_.$('input[class="dataTableApiSelectInfoLabel"]:checked').length!=0)//if($(this).prop('checked'))
	        {
				$('#samples_dataTableStructButtons button.dataTableApiOptional1').removeClass('disabledclass');
	        }else{
	        	 $('#samples_dataTableStructButtons button.dataTableApiOptional1').addClass('disabledclass');
	        }
	   });
	   table_.$('input[Type ="checkbox"][class="dataTableApiSelectAllNone"]').change(function(){
		    if(table_.$('input[class="dataTableApiSelectInfoLabel"]:checked').length!=0)//if($(this).prop('checked'))
	        {
				$('#samples_dataTableStructButtons button.dataTableApiOptional1').removeClass('disabledclass');
	        }else{
	        	 $('#samples_dataTableStructButtons button.dataTableApiOptional1').addClass('disabledclass');
	        }
	   });
   } else if(domId == 'specificationTable' && $('#formCode').val() == 'ExpImpSpec'){
	   renderElementAuthorizationImp();
   } else if(domId == 'experimentTable' && ($('#formCode').val() == 'ExpAnalysisReport'||$('#formCode').val() == 'ExperimentReport')){
	   renderElementAuthorizationImp();
   } else if(domId == 'reportTable' && ($('#formCode').val() == 'ExpAnalysisReport'||$('#formCode').val() == 'ExperimentReport')){
	   //ab 20082020: removed nowrap because the table have the ability to column resize now
	   $('#reportTable th').css('min-width','200px');
	   //kd 29022020 fixed bug-7922: The option to export the report table to PDF should be removed 
	   $('#reportTable_wrapper').find('div.dropdown-content div.dt-buttons a').eq( 1 )  //remove PDF
	   			.remove();
	   			//.('<a class="" tabindex="0" aria-controls="reportTable" href="#"><span>PDF</span></a>');
	   if($('#formCode').val() == 'ExperimentReport') {
		  collectUOMtoColumnTitle(domId,'$U');
		   //$('#reportTable_wrapper > .dropdown-button').css('float','left');
	   }
   }else if((domId == 'rulesTable' || domId == 'separateColumnsRulesTable') && $('#formCode').val() == 'ExperimentReport'){
	   $('#'+domId+'_Parent').css({"padding-bottom":"105px"});//padding-bottom because of ddl in edit tables
	   $('#'+domId+'_dataTableStructButtons button.dataTableApiNew:not(.dataTableAddRowButton)').css('display', 'none');
   } else if(domId == 'Batches' && ($('#formCode').val() == 'InvItemMaterial'||$('#formCode').val() == 'InvItemMaterialFr'||$('#formCode').val() == 'InvItemMaterialPr')){
	   $('#Batches_dataTableStructButtons button.dataTableApiOptional1').text('Depleted');
	   $('#Batches_dataTableStructButtons button.dataTableApiOptional1').off('click');
	   $('#Batches_dataTableStructButtons button.dataTableApiOptional1').addClass('disabledclass');
	   $('#Batches_dataTableStructButtons button.dataTableApiOptional1').click(function(){onClickDepleteBatches();});
   } else if(domId == "training" ){ // make training popup form constant (not temp from under inventory material/column/instrument from) - this update is from previous commit by alex
	   $('#training_dataTableStructButtons button.dataTableApiAdd').addClass('ignor_data_change');
   } else if(domId == "materialDuplicatesTable"){
	   //first row should be clicked automatically
	   $("#materialDuplicatesTable").find("tbody tr:eq(0)").trigger("click");
   } else if(domId == 'maintenance' && $('#formCode').val() == 'InvItemInstrument'){
	   $('#maintenance_dataTableStructButtons button.dataTableApiNew').text('New Preventive');
	   $('#maintenance_dataTableStructButtons button.dataTableApiNew').attr('onclick','');
	   $('#maintenance_dataTableStructButtons button.dataTableApiNew').click(function(){generalBL_elementDataTableClickEvent(domId, 'addNewPreventive', [$('#formId').val(),$('#formCode').val()]);});
	   $('#maintenance_dataTableStructButtons button.dataTableApiOptional1').text('New Breakdown');
	   $('#maintenance_dataTableStructButtons button.dataTableApiOptional1').off('click'); 
	   $('#maintenance_dataTableStructButtons button.dataTableApiOptional1').click(function(){generalBL_elementDataTableClickEvent(domId, 'addNewBreakdown', [$('#formId').val(),$('#formCode').val()]);});
   } else if(domId == 'steps' && $('#formCode').val().indexOf('Experiment')>-1){
	   $('#steps_dataTableStructButtons button.dataTableApiOptional1').addClass('disabledclass');
	   $('#steps_dataTableStructButtons button.dataTableApiOptional1').text('Delete');
	   $('#steps_dataTableStructButtons button.dataTableApiOptional1').click(function(){onClickDeleteSteps();});
	   var disabledSteps = $('#disabledSteps').val();
	   $('input[class="dataTableApiSelectInfo"]').each(function(){
		   if((',' + disabledSteps + ',').indexOf(',' + $(this).val() + ',') > -1){
			   $(this).attr("disabled", true);
		   }
	   });
   } else if((domId == 'experiments'||domId == 'subProjects'||domId == 'subSubProjects') 
		   && ($('#formCode').val() == 'Project'||$('#formCode').val() == 'SubProject'||$('#formCode').val() == 'SubSubProject')){
	   $('#'+domId+'_dataTableStructButtons button.dataTableApiNew').css('display','none');
	} 
   else if($('#formCode').val() == 'Recipe' && domId == 'compositions'){
	   $('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').click(function(){
    		var $elem = $(this);
    		openConfirmDialog({     		
    			onConfirm: onEditableClickRemoveMaterial,
    			onConfirmParams: $elem
           });
    		
	   });
   }
   else if($('#formCode').val() == 'Step' || $('#formCode').val() == 'StepFr' || $('#formCode').val() == 'StepMinFr'|| $('#formCode').val() == 'ExperimentFor')
   {
	   if(domId == "action")
	   {
		   //$('#action_dataTableStructButtons button.dataTableApiRemove').prop('disabled', true);
		   $('#action_dataTableStructButtons button.dataTableApiRemove').text('Remove Row');
		   $('#action_dataTableStructButtons button.dataTableApiRemove').addClass('ignor_data_change');
		   $('#action_dataTableStructButtons button.dataTableApiNew').addClass('ignor_data_change');
		   $('#action_dataTableStructButtons button.dataTableApiRemove').off('click');
		   $('#action_dataTableStructButtons button.dataTableApiRemove').click(function(){generalBL_generalClickEvent("deleteAction");});
		   
		   if($('#formCode').val() == 'StepMinFr') { //avoid full screen button inside step iframe and double navigation...
			   $('#action_dataTableStructButtons button.fullScreenOpenBtn').css('display','none');
			   $('[id="' + domId + '"] tbody').off('dblclick');
		   }

		   if($('#STATUS_ID').attr("lastselectedname") != "Planned")
		   {// $('#action_dataTableStructButtons button.dataTableApiRemove').prop('disabled', false);
			   //disables the checkbox of all the rows that have formnumberId 
			   var selectedTable = $('#' + domId).DataTable();
			   // Find indexes of rows which have `formnumberid` in the 5th column
			   var indexes = selectedTable.rows().eq( 0 ).filter( function (rowIdx) {
			       return selectedTable.cell( rowIdx, 5 ).data()!='' ? true : false;
			   } );
			   selectedTable.rows(indexes).nodes()
			   		.to$()
			   		.find('.dataTableApiSelectInfo').prop('disabled',true);
		   }
		   
		   if($('#formCode').val() == 'Step' && $('#preparation_run').val() == 'Run' ){
			   $('#allRunsActions').css('display','');
			   $('#action_wfDiv').append($('#allRunsActions'));
			   $('#action_wfDiv').css("left","10%");
			   $('#allRunsActions').click(function(){openTableInNewTab(domId);});
		   }
	   }
	   else if(domId == 'reactants' || domId == "solvents" || domId == "products")
	   {
		   $('#' + domId + '_dataTableStructButtons button.dataTableApiAdd').text('Add Row');
		   $('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').click(function(){
	     		var $elem = $(this);
	     		openConfirmDialog({     		
	     			onConfirm: onEditableClickRemoveMaterial,
	     			onConfirmParams: $elem
	            });
	     		
		   });
		   if(domId == "products"){
			   if($('#formCode').val()=='Step'){
				   $('#products_dataTableStructButtons button.dataTableApiOptional1').text('Update Results');
			 	   $('#products_dataTableStructButtons button.dataTableApiOptional1').click(function(){updateResults();});
			   } else if ($('#formCode').val()=='StepMinFr'){
				   $('#products_dataTableStructButtons button.dataTableApiOptional1').text('Create Batch');
				  // $('#products_dataTableStructButtons button.dataTableApiOptional1').off('click');
				  // $('#products_dataTableStructButtons button.dataTableApiOptional1').addClass('disabledclass');
				   $('#products_dataTableStructButtons button.dataTableApiOptional1').click(function(){confirmWithOutSave(createBatch);});
				   createBatchVisibility();
			   }
		   }
	   }
	   else if(($('#formCode').val() == 'StepFr' || $('#formCode').val() == 'StepMinFr' || $('#formCode').val() == 'ExperimentFor') && domId == 'formulants' && $('#formulants').hasClass('editable'))
	   {
		   $('#formulants_Parent').css('padding-bottom','110px');
		   $('#formulants_dataTableStructButtons button.dataTableApiEdit').text('View'); 
		   $('#formulants_dataTableStructButtons button.dataTableApiRemove').text('Remove row'); 
		   $('#' + domId + '_dataTableStructButtons button.dataTableApiAdd').text('Add Row');
		   /*$('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').click(function(){
	     		var $elem = $(this);
	     		openConfirmDialog({     		
	     			onConfirm: onEditableClickRemoveMaterial,
	     			onConfirmParams: $elem
	            });
	     		
		   });*/
		   var eTable = $('table.editable[id="formulants"]');
	    	if(eTable.length > 0)
	    	{
	    		$(eTable.find('input.editableSmartCell')).css('height','28px');  		    	
	    	}
	   } else if($('#formCode').val() == 'Step' && domId == 'Parameters'){
		   $('#' + domId + '_dataTableStructButtons button.dataTableApiNew:not(.dataTableAddRowButton)').css('display','none');
		   $('#' + domId + '_dataTableStructButtons button.dataTableApiView').css('display','none');
	       $('.row-selection-helper').css('width','40px');
	       $('#Parameters_dataTableStructButtons').append($('#parameterSearch'));
	   }
	   
	   if($('#'+domId).hasClass('editable')){ 
		   // Disable up and down keys on browser's arrows when input type is number.
		   $('#'+domId).on('keydown', 'input[type=number]', function(e) {
		        if ( e.which == 38 || e.which == 40 )
		            e.preventDefault();
		   });
	   }
   }
   else if($('#formCode').val() == 'Main' &&  $('#' + domId + '_structCatalogItem').val() == 'Request'){
	   $('#'+domId+'_dataTableStructButtons button.dataTableApiNew').on('mouseover', function() {
		   if ( $('#newRequestDialog').closest('.ui-dialog').is(':visible')) { //whether the dialog is already opened
				return;
		   }
		   
		   var $this = $(this);	
			var left, top;
			console.log("$this",$this);
			dialogHeight = 220;
		    dialogWidth = 460;   
		    if($this.length > 0)
		    {
		    	left = $this.offset().left;// + dialogWidth/2.8;
		        top = $this.offset().top + $this.height() +15;
		        console.log("left",left);
		        console.log("top",top);
		    }
		    else
		    {
		    	left = $(document).width() - dialogWidth - 50;
		    	top = $(document).height()/2 - dialogHeight - 100;
		    	console.log("left",left);
		        console.log("top",top);
		    }    
		  
		   var this_ =this;
		   var parentId = $('#formId').val();
		    var domId = $(this_).parent().attr('tableid');
		    //var title = $('[id="' + domId + '_structCatalogItem"]').val();
		    var formCode = encodeURIComponent($('[id="' + domId + '_structCatalogItem"]').val());
		    var parentTable = $(this_).parents('div[parentelement]').attr('parentelement');
		    var custid = [''],parentStruct;  
	        if (parentTable != '') {
	            if ($('#' + parentTable).is('table')) {
	                selectedTable = $('[id="' + parentTable + '"]').DataTable();
	                custid = selectedTable.row('.selected').data();
	            }
	            if(custid!=undefined){
		            parentId = custid[0];
		            }
	            parentStruct = parentTable != '' && $('#' + parentTable).is('table') ? $('#' + parentTable + '_structCatalogItem').val():$('#formCode').val();
	        }
	        if(parentStruct==undefined || parentId =='-1'){
	        	parentStruct="";
	        	parentId ="";
	        }
	        
		 if( formCode == 'Request'){
			 var $dialog = $('<div id="newRequestDialog" class="ui-dialog-content ui-widget-content" style="overflow-y: hidden; overflow-x: hidden;""></div>')
	          .html('<iframe style="border: 0px;width:100%;height:100%" ></iframe>')
	          .dialog({
	              autoOpen: false,
	              //modal: true,
	              resizable: false,
	              height: 130,
	              width: 250,
	              //  title: title,
	              close: function () {
	                  $('#newRequestDialog iframe').attr('src', 'about:blank');
	                  $('#newRequestDialog').remove();
	                  //onElementDataTableApiChange(domId);
	              },
					open: function(event, ui) 
					{
						//$('.ui-dialog-titlebar').css("display","none");
						$('#newRequestDialog').siblings( ".ui-dialog-titlebar" ).css( "display", "none" );
						$(this).parent().css({'top': top,'left':left});
						var parentFormCode = parent.$('#newRequestDialog').data('parentFormCode');
						var parentFormId = parent.$('#newRequestDialog').data('parentFormId');
						var optionsHtml =  "<div id=\"selectWfFormCode\" name=\"selectWfFormCode\" class=\"selectionListLink\" style=\"width: 100%;position: relative;float: left;\">\n"+
						        "<div><div>"+getEntityIconByFormCode('Request')+"</div>" +
						        "<div class=\"request\" style=\"cursor: pointer\"><span onclick=\"createNewFormFromMain('Request','"+parentFormCode+"','"+parentFormId+"')\">Request (New)</span></div></div>"+
						        "<div><div>"+getEntityIconByFormCode('Request (Copy Default)')+"</div>" +
								"<div class=\"copy-request\" style=\"cursor: pointer\"><span onclick=\"createNewFormFromMain('Request (Copy Default)','"+parentFormCode+"','"+parentFormId+"')\">Request (Copy Default)</span></div></div>"+
								"</div>";
						$('#newRequestDialog').html(optionsHtml);
						$("#newRequestDialog").mouseleave(function(){
							 $('#newRequestDialog iframe').attr('src', 'about:blank');
			                 $('#newRequestDialog').remove();
						});
						
		            }
	          });
			 $dialog.dialog('option', 'dialogClass', 'noTitleStuff').data('parentFormCode',parentStruct).data('parentFormId',parentId).dialog('open');
		 }
	   });
	   
	   //remove and close dialog also when leaving the new button (if we not in the dialog div id -> newRequestDialog)
	   $('#'+domId+'_wrapper').on('mouseleave',function(e){
		     if(e.relatedTarget != null && e.relatedTarget.id != 'newRequestDialog') {
		    	 $('#newRequestDialog iframe').attr('src', 'about:blank');
	             $('#newRequestDialog').remove();
		     }
	   });
	   
	   /*
	    * 04012021 kdvoyashov commented this call because put to yes/no 
	   	* 						to the favorite field inside the json on fly by using formid in the ..._dtm views and changing it in the map and put to the json
	    */
	   //favoritCheckBoxHandle(domId); // kd 16122020 added Favorite field   
   	}
    if($('#formCode').val() == 'InvItemMaterialsMain'&&$('#' + domId + '_structCatalogItem').val() == 'InvItemMaterial'){
    	$('#upperFilters').css('display', '');
    	$('#'+domId+'_dataTableStructButtons button.dataTableApiNew').on('mouseover', function() {
  		   if ( $('#newMaterialDialog').closest('.ui-dialog').is(':visible')) { 
  				return;
  		   }
  		  
  		   var $this = $(this);	
  			var left, top;
  			console.log("$this",$this);
  			dialogHeight = 220;
  		    dialogWidth = 460;
  		    if($this.length > 0)
  		    {
  		    	left = $this.offset().left;// + dialogWidth/2.8;
  		        top = $this.offset().top + $this.height() +15;
  		        console.log("left",left);
  		        console.log("top",top);
  		    }
  		    else
  		    {
  		    	left = $(document).width() - dialogWidth - 50;
  		    	top = $(document).height()/2 - dialogHeight - 100;
  		    	console.log("left",left);
  		        console.log("top",top);
  		    }
  		 
  		   var this_ =this;
  		   var parentId = $('#formId').val();
  		    var domId = $(this_).parent().attr('tableid');
  		    // var title = $('[id="' + domId + '_structCatalogItem"]').val();
  		    var formCode = encodeURIComponent($('[id="' + domId + '_structCatalogItem"]').val());
  		    var parentTable = $(this_).parents('div[parentelement]').attr('parentelement');
  		    var custid = [''],parentStruct;
  	        if (parentTable != '') {
  	            if ($('#' + parentTable).is('table')) {
  	                selectedTable = $('[id="' + parentTable + '"]').DataTable();
  	                custid = selectedTable.row('.selected').data();
  	            }
  	            if(custid!=undefined){
  		            parentId = custid[0];
  		            }
  	            parentStruct = parentTable != '' && $('#' + parentTable).is('table') ? $('#' + parentTable + '_structCatalogItem').val():$('#formCode').val();
  	        }
  	        if(parentStruct==undefined || parentId =='-1'){
  	        	parentStruct="";
  	        	parentId ="";
  	        }
  	       
  		 if( formCode == 'InvItemMaterial'){
  			 var $dialog = $('<div id="newMaterialDialog" class="ui-dialog-content ui-widget-content" style="overflow-y: hidden; overflow-x: hidden;""></div>')
  	          .html('<iframe style="border: 0px;width:100%;height:100%" ></iframe>')
  	          .dialog({
  	              autoOpen: false,
  	              //modal: true,
  	            resizable: false,
  	              height: 150,
  	              width: 250,
  	              // title: title,
  	              close: function () {
  	                  $('#newMaterialDialog iframe').attr('src', 'about:blank');
  	                  $('#newMaterailDialog').remove();
  	                  // onElementDataTableApiChange(domId);
  	              },
  					open: function(event, ui)
  					{
  						// $('.ui-dialog-titlebar').css("display","none");
  						$('#newMaterialDialog').siblings( ".ui-dialog-titlebar" ).css( "display", "none" );
  						$(this).parent().css({'top': top,'left':left});
  						var parentFormCode = parent.$('#newMaterialDialog').data('parentFormCode');
  						var parentFormId = parent.$('#newMaterialDialog').data('parentFormId');
  						var optionsHtml =  "<div id=\"selectWfFormCode\" name=\"selectWfFormCode\" class=\"selectionListLink\" style=\"width: 100%;position: relative;float: left;\">\n"+
  						        "" +
  						        "<div class=\"request\" style=\"cursor: pointer\"><span onclick=\"createNewFormFromMain('InvItemMaterial','"+parentFormCode+"','"+parentFormId+"')\">Chemical Mateirial</span></div>"+
  						        "" +
  								"<div class=\"requestt\" style=\"cursor: pointer\"><span onclick=\"createNewFormFromMain('InvItemMaterialFr','"+parentFormCode+"','"+parentFormId+"')\">Formulation</span></div>"+
                                "" +
   								"<div class=\"request\" style=\"cursor: pointer\"><span onclick=\"createNewFormFromMain('InvItemMaterialPr','"+parentFormCode+"','"+parentFormId+"')\">Premix</span></div>"+
  								"</div>";
  						$('#newMaterialDialog').html(optionsHtml);
  						$("#newMaterialDialog").mouseleave(function(){
  							 $('#newMaterialDialog iframe').attr('src', 'about:blank');
  			                 $('#newMaterialDialog').remove();
  						});
  						
  		            }
  	          });
  			 $dialog.dialog('option', 'dialogClass', 'noTitleStuff').data('parentFormCode',parentStruct).data('parentFormId',parentId).dialog('open');
  		 }
  	   });
  	  
  	   // remove and close dialog also when leaving the new button (if we not
		// in the dialog div id -> newRequestDialog)
  	   $('#'+domId+'_wrapper').on('mouseleave',function(e) {
  		     if(e.relatedTarget != null && e.relatedTarget.id != 'newMaterialDialog') {
  		    	 $('#newMaterialDialog iframe').attr('src', 'about:blank');
  	             $('#newMaterialDialog').remove();
  		     }
  	   });
  	  
  	   /*
		 * 04012021 kdvoyashov commented this call because put to yes/no to the
		 * favorite field inside the json on fly by using formid in the ..._dtm
		 * views and changing it in the map and put to the json
		 */
  	   // favoritCheckBoxHandle(domId); // kd 16122020 added Favorite field
     	
  	  
     }
   	else if((domId == 'Parameters' || domId == 'ParametersTable')   			
   				&& 
   			(_formCode == 'Step' || _formCode == 'Experiment' || _formCode == "ExperimentCP"))
    {
   	 	$('#'+domId+'_dataTableStructButtons button.dataTableApiView').remove(); //TODO: define display through FormBuilder

	   	if( domId == 'ParametersTable'){
		  	$('#'+domId+'_dataTableStructButtons button.dataTableApiNew').css('display','none');
	   	}
		else
	  	{
		   $('#'+domId+'_dataTableStructButtons button.dataTableApiRemove').text('Remove Row');	
		   $('#'+domId+'_dataTableStructButtons button.dataTableApiNew:not(.dataTableAddRowButton)').css('display','none');
  	  	}
	   	if(_formCode == 'Experiment'){
	   		$('#Parameters_dataTableStructButtons').append($('#parameterSearch'));
	   	}
	} 
   	else if (_formCode == "ExperimentCP") //for ExperimentCP only
	{
   		if(domId == "expRunPlanningTable")
   		{

        	$('#'+domId+'_Parent').css('padding-bottom','110px');//fixed bug 8346
   			// Disable up and down keys on browser's arrows when input type is number.
   			   $('#'+domId).on('keydown', 'input[type=number]', function(e) {//fixed bug 8338 
   			        if ( e.which == 38 || e.which == 40 )
   			            e.preventDefault();
   			   });
   			var $calcButton;
   			if(isPageInitFlag)
   			{
	   			var $structBtnDiv = $('#'+domId+'_dataTableStructButtons');  		

	   			$calcButton = $structBtnDiv.find('button.dataTableApiNew:not(.dataTableAddRowButton)');	   			
	   			$calcButton.attr('id','btnCalculateRunAll').text('Calculate All').removeClass('fullScreenOpenBtn');	
	   			$structBtnDiv.append($calcButton);
	   			
	   			var $startButton = $structBtnDiv.find('button.dataTableApiView');
	   			$startButton.attr('id','btnCreateRun').text('Start').removeClass('fullScreenOpenBtn');	
	   			$structBtnDiv.append($startButton);
	   			
	   			$structBtnDiv.find('button.dataTableApiRemove').text('Remove Row').click(function(){generalBL_generalClickEvent("deleteRun");});
   			}
   			$calcButton = $('#btnCalculateRunAll');
   			var dtRowsCount = $('#'+domId).DataTable().page.info().recordsTotal;
   			var disableClass = "";
   			if(dtRowsCount == 0)
   			{
   				$calcButton.addClass("disabledclass");
   			}
   			else
   			{
   				$calcButton.removeClass("disabledclass");
   			}
   			$calcButton.off('click').removeAttr('onclick').on('click',(function(){generalBL_generalClickEvent("calculateRunsPlanning");}));
   			$('#btnCreateRun').off('click').removeAttr('onclick').on('click',(function(){generalBL_generalClickEvent("checkStepStatusAndCreateRun",0);}));
   		}else if(domId == 'experimentResults'){
   			$('#experimentResults_wfDiv').append($('#allRunsResult'));
   			$('#experimentResults_wfDiv').css("left","10%");
   			$('#allRunsResult').click(function(){openTableInNewTab(domId);});
   		}/*else if(domId == 'selfTestResults'){
   			$('#selfTestResults_wfDiv').append($('#allRunsResultSTest'));
   			$('#selfTestResults_wfDiv').css("left","10%");
   			$('#allRunsResultSTest').click(function(){openTableInNewTab(domId);});
   		}*/
		
   	} 
   	else if($('#formCode').val() == "WorkupFeeding" && domId == 'materials'){
   
	   $('#materials_dataTableStructButtons button.dataTableApiOptional1').text('Update Results');
 	   $('#materials_dataTableStructButtons button.dataTableApiOptional1').click(function(){updateResults();});
 	  
   	} else if($('#formCode').val() == "WorkupCrystallize" && domId == 'startingMixtureDefinition'){
	   $('#startingMixtureDefinition_dataTableStructButtons button.dataTableApiOptional1').text('Update Results');
 	   $('#startingMixtureDefinition_dataTableStructButtons button.dataTableApiOptional1').click(function(){updateResults();});
 	  
   	} else if($('#formCode').val() == "WorkupDistillation" && domId == 'startingMixtureDefinition') {
	   $('#startingMixtureDefinition_dataTableStructButtons button.dataTableApiOptional1').text('Update Results');
 	   $('#startingMixtureDefinition_dataTableStructButtons button.dataTableApiOptional1').click(function(){updateResults();});
 	  
   	} else if(domId == 'expAnReportTable' && 
   			($('#formCode').val() == 'ExpAnalyReportMain'||$('#formCode').val() == 'ExpAnalyReportPop')){
   		$('#expAnReportTable_dataTableStructButtons button.dataTableApiNew').css('display','none');
   		$('#expAnReportTable_dataTableStructButtons button.dataTableApiRemove').css('display','none');
   		$('#expAnReportTable_dataTableStructButtons button.dataTableApiView').css('display','none');
   		$('#expAnReportTable_dataTableStructButtons button.dataTableApiView').off('click').removeAttr('onclick');
   		$('#expAnReportTable_dataTableStructButtons button.dataTableApiView').click(function(){viewExpAnalysisReport();});//reference the event although the button is invisible in order to execute the right action on double clicking the table
	 	/*$('#expAnReportTable_dataTableStructButtons button.dataTableApiOptional1').text('View');
	 	$('#expAnReportTable_dataTableStructButtons button.dataTableApiOptional1').off('click');
	 	$('#expAnReportTable_dataTableStructButtons button.dataTableApiOptional1').addClass('disabledclass');
	 	$('#expAnReportTable_dataTableStructButtons button.dataTableApiOptional1').click(function(){viewExpAnalysisReport();});*/
	 	
    }else if($('#formCode').val() == "ReportDesignSearch" && domId == 'designTable'){
    	$('[id="' + domId + '"] tbody').on('click', 'tr', function (e) {
    		selectedTable = $('#' + domId).DataTable();
            custid = selectedTable.row('.selected').data();
            if(custid != undefined ){
            	rowId = custid[0];
            	$('#designiFrame').remove();
       	  		displayReportDesignPrev(rowId);
            }else{
            	$('#designiFrame').remove();
            }
    		/*var $this = $(this);
  	  		currRowData = $('#designTable').DataTable().row($this).data();
  	  		rowId = currRowData[0];*/
    	});
    } else if($('#formCode').val() == 'ReportDesignExp') { 
    	try {
    		if($('#chkImpuritiesStep').is(':checked')) {
        		$('.dataTables_empty').text("All impurities of the experiment");	
        	}
        	else if($('#numConcentrationImpStep').val()!=""&&$('#numConcentrationImpStep').val()!=null) {
        		$('.dataTables_empty').text($('#numConcentrationImpStep').val()+" impurities with maximal concentration");
        	}
    	} catch(e){}
    } else if(($('#formCode').val() == "SpreadsheetMain" && domId == 'spreadsheetTable')){//} || $('#formCode').val() == "Main") { //TODO make it if for main screen too
    	if($('#formCode').val() == "SpreadsheetMain") {
	    	$('[id="' + domId + '"] tbody').on('click', 'tr', function (e) {
	    		selectedTable = $('#' + domId).DataTable();
	            custid = selectedTable.row('.selected').data();
	            var iColumns = selectedTable.column(':contains(CREATOR_ID)').index();
	            var iName = selectedTable.column(':contains(SPREADSHEETTEMPLANAME)').index();
	           
	            if (typeof custid !== 'undefined') {
	                if (custid[iColumns] == $('#userId').val() || $('#userName').val() == 'admin') {            	
	                	$('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').prop('disabled', false);
	                	$('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').off('click');
	        	      	$('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').click(function(){
	        	      		//var $elem = $(this);
	        	      		openConfirmDialog({
	        				onConfirm : function(){deleteSpreadsheetTemplate(custid[0]);},
	        				title : 'Warning',
	        				message : getSpringMessage('Are you sure you want to remove spreadsheet template \''+custid[iName]+'\'?')
	        			})});
	                } else {
	                    $('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').prop('disabled', true);
	                }
	            }
	    	});
    	}
    	/*
 	    * 04012021 kdvoyashov commented this call because put to yes/no 
 	   	* 						to the favorite field inside the json on fly by using formid in the ..._dtm views and changing it in the map and put to the json
 	    */
//    	favoritCheckBoxHandle(domId);
    	
    }
    else if(($('#formCode').val() == "HistoricalData" ||$('#formCode').val() == "HistoricalDataMain")
    		&& domId == 'historicalDataTable'){
    	
    	$('[id="' + domId + '"] tbody').on('click', 'tr', function (e) {
    		if(e.target.className=="fa fa-search-plus"){
    			return;
    		}
    		
    		//:not(.smartlink-contextmenu)
    		var selectedTable = $('#' + domId).DataTable();
            var custid = selectedTable.row('.selected').data();
            if(custid != undefined ){
            	var experiment_id = custid[0];
            	var spread_id = custid[1];
            	  //$('#prevDialog').remove();
            	   var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode="
		                    + "ViewSpreadsheet" + "&formId=-1" + "&userId="
	                    	+ $('#userId').val() + '&PARENT_ID='
	                    	+ spread_id;
	                    	//+ experiment_id;
            	
            	//setFormParamMap('ViewSpreadsheet', "-1","experiment_id",experiment_id);
            	var dialogWidth;
            	var dialogHeight;
            	var $dialog;
            	var position;
            	if(window.self !== window.top){
            		dialogWidth = $(window.top).width()*0.5;// - 220;
            		dialogHeight = $(window.top).height()// - 320;
            		if(parent.$('#prevDialog1').length>0/* && parent.$('#prevDialog1').dialog('isOpen')*/){
            			parent.$('#prevDialog1').dialog('close');
            			parent.$('#prevDialog1 iframe').attr('src', 'about:blank');
            			parent.$('#prevDialog1').remove();
	            	}
            		position = { my: "left bottom", at: "0 0", of: window.top }//locating it on the top right position
            		$dialog= parent.$('<div id="prevDialog1" style="overflow-y: hidden;right:0;""></div>');
            	}else{
            		dialogWidth = $(window).width()*0.5;// - 220;
            		dialogHeight = $(window).height();// - 320;
            		if($('#prevDialog1').length>0/* && $('#prevDialog1').dialog('isOpen')*/){
	            		$('#prevDialog1').dialog('close');
	            		$('#prevDialog1 iframe').attr('src', 'about:blank');
            			$('#prevDialog1').remove();
	            		
	            	}
            		position = { my: "left bottom", at: "0 0", of: window }//locating it on the top right position
            		$dialog= $('<div id="prevDialog1" style="overflow-y: hidden;right:0;""></div>');
        		}
            	
            	$dialog
                .html('<iframe style="border: 0px;width:100%;height:100%" src="' + page + '"></iframe>')
                .dialog({
                    autoOpen: false,
                    modal: true,
                    height: dialogHeight,
                    width: dialogWidth,
                    //  title: title,
                    position:position,
                    close: function () {
                     $('#prevDialog1 iframe').attr('src', 'about:blank');
                       $('#prevDialog1').remove();
                    
                    }
                });

            	$dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
            }
            else{
            $('#prevDialog1 iframe').attr('src', 'about:blank');
             $('#prevDialog1').remove();
            	//$('#prevDialog').dialog('close');
            }
    	});
  
		var _type = "td";
		var _tid = domId;
		//var _tDT = opts.tableDT;
		var uniqueClass = _tid + "-contextmenu";
		var $target = $("#" + _tid);	
		var hasLink = false;
		var _delegate = "td";	
		if ($('.' + uniqueClass).length > 0) {
			$target.contextmenu("destroy");
		}
		$target.contextmenu({
			delegate : _delegate,
			addClass:"ui-contextmenu " + uniqueClass,
			autoFocus : true,
			preventContextMenuForPopup : true,
			preventSelect : false,//false in order to enable to select/sign a text in a table
			closeOnWindowBlur : true,
			menu : [ {
				title : "Open in a new tab",
				cmd : "open"
			} ],
			select : function(event, ui) {
				var $this = ui.target.parent();				
//				console.log("row",$this);
				if($this.parent()[0].tagName == 'A'){
					try
					{						
						var data = $this.parent()[0].attributes.contextmenu_data.value
						var dataArr = JSON.parse(data);
						var formId = dataArr[0];
						var formCode = dataArr[1];
						var tab = dataArr[2];
						console.log("Go to ->", formCode + " ->" + formId);
						checkAndNavigate([''+formId+'',''+formCode+'',''+tab+'','false',true,'newTab']);
					}catch(e){
						console.log("open link in a new tab  error");
					}
				}else {		
	//				console.log("row",$this);
					
					
					var selectedTable = $('#' + domId).DataTable();
		            var custid = selectedTable.row($this).data();
					
						var spread_id = custid[1];
						
		           
		            	   var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode="
				                    + "ViewSpreadsheet" + "&formId=-1" + "&userId="
			                    	+ $('#userId').val() + '&PARENT_ID='
			                    	+ spread_id;
		            		openNewTab(page);
					
				}
			}
		});
    }
    else if($('#formCode').val() == "MaterialFunction"&&domId=="functions"){
    	 $('#'+domId+'_dataTableStructButtons button.dataTableApiRemove').text('Remove Row');	
		   $('#'+domId+'_dataTableStructButtons button.dataTableApiNew:not(.dataTableAddRowButton)').css('display','none');
		   $('.dataTableApiView').css('display','none');
		   $('#'+domId+'_dataTableStructButtons button.dataTableApiOptional1').text('Copy Table From');
	 	   $('#'+domId+'_dataTableStructButtons button.dataTableApiOptional1').click(function(){openCopyFunctionTablePopUp();});
    }
    
    if($('#formCode').val()=='CompositionDetails'){
    	changeCompositionDetailsTableByCompositionType(domId);
    }
    
   if(($('#formCode').val() == 'RecipeFormulation'|| $('#formCode').val() == "StepMinFr") && domId == 'compositions'
	   || $('#formCode').val() == 'ExperimentFor' && domId == 'plannedCompositions'
		   || $('#formCode').val() == "StepMinFr" && domId == 'products'){
	   var lastChangeVal = $('#'+domId).attr('lastChangeVal');
	   $('#'+domId).attr('lastChangeVal','');
	   if($('#formCode').val() == 'RecipeFormulation'){
		   disableFormulationType('1',domId);
	   }
	   if(domId == 'plannedCompositions'){
		   
		   $('#'+domId+'_dataTableStructButtons button.dataTableApiOptional1').text('Composition Details');
		   $('#'+domId+'_dataTableStructButtons button.dataTableApiOptional1').off('click');
		   $('#'+domId+'_dataTableStructButtons button.dataTableApiOptional1').click(function(){openCompositionDetails();});
		   $('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').text('Remove Row').off('click');
		   $('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').click(function(){
	    		var $elem = $(this);
	    		checkMaterialAndRemoveRow(domId,$elem);
	    		checkTabClickFlag("SafetyTab",false);
	    		enableCompositionDetailsButton(domId);
		   });
		   enableCompositionDetailsButton(domId);//fixed bug 8953
	   } else if($('#formCode').val() == "StepMinFr" 
		   && (domId == 'compositions' || domId == 'products')){
		   $('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').text('Remove Row').off('click');/*.click(function(){
	    		var $elem = $(this);
	    		openConfirmDialog({     		
	    			onConfirm: function(){deleteRowElementDataTableApiImp($elem)},
	    			onConfirmParams: $elem
	           });
		   });*/
		   $('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').click(function(){
	    		var $elem = $(this);
	    		openConfirmDialog({     		
	    			onConfirm: function(){checkBalanceAndRemoveRow(domId,$elem)},
	    			onConfirmParams: $elem
	           });
	    		//checkBalanceAndRemoveRow(domId,$elem);
	    		parent.checkTabClickFlag("SafetyTab",false);
		   });
	   } else if($('#formCode').val() == "RecipeFormulation" && domId == 'compositions'){
		   $('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').text('Remove Row').off('click');
		   $('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').click(function(){
	    		var $elem = $(this);
	    		deleteRowElementDataTableApiImp($elem);//removeMultipleRows(domId,$elem);
		   });
	   }
	   $('#' + domId + '_dataTableStructButtons button.dataTableApiNew:not(.dataTableAddRowButton)').css('display','none');
	   $('#' + domId + '_dataTableStructButtons button.dataTableApiView').css('display','none');
	   if($('#'+domId).hasClass('editable')){ 
		   // Disable up and down keys on browser's arrows when input type is number.
		   $('#'+domId).on('keydown', 'input[type=number]', function(e) {
		        if ( e.which == 38 || e.which == 40 )
		            e.preventDefault();
		   });
	   }
	   if($('#formCode').val() == 'ExperimentFor' && domId == 'plannedCompositions'
		   ||$('#formCode').val() == 'RecipeFormulation' && domId == 'compositions'){
		   $('#'+domId).attr('batchSizeElement','batchSize');
		   $('#' + domId + '_dataTableStructButtons button.dataTableApiNew.dataTableAddRowButton').removeAttr('onclick');
		   $('#' + domId + '_dataTableStructButtons button.dataTableApiNew.dataTableAddRowButton').attr('onclick','validateFormulationTypeAndAddRow("'+$('#formCode').val()+'","'+domId+'")');
		   $('#' + domId + '_dataTableStructButtons a.dataTableApiAddMultiRows').removeAttr('onclick');
		   $('#' + domId + '_dataTableStructButtons a.dataTableApiAddMultiRows').attr('onclick','validateFormulationTypeAndAddRow("'+$('#formCode').val()+'","'+domId+'",this)');
		   changePlannedCompositionsTableByCompositionType(domId);
            
          if($('#' + domId + '_dataTableStructButtons button.dataTableApiNew.dataTableAddRowButton').hasClass("disabledclass") || $('#' + domId + '_dataTableStructButtons button.dataTableApiNew.dataTableAddRowButton').hasClass("disablePage")){
   		   $('#' + domId + '_dataTableStructButtons a.dataTableApiAddMultiRows').addClass('disablePage');
          }

	   }
	   if($('#formCode').val() == 'StepMinFr' && domId == 'compositions'){
		   $('#'+domId).attr('batchSizeElement','batchSizeMaterial');
		   $('#'+domId+'_dataTableStructButtons button.dataTableApiOptional1').text('Copy Materials');
		   $('#'+domId+'_dataTableStructButtons button.dataTableApiOptional1').off('click');
		   $('#'+domId+'_dataTableStructButtons button.dataTableApiOptional1').click(function(){cloneCompositionFromExpToStep();});
		   changePlannedCompositionsTableByCompositionType(domId);
		   
		   // if disable edit table we also disable all the icons (in this case documents add and drag columns) - consider make if in changePlannedCompositionsTableByCompositionType
		   if($('#'+domId).attr('disableEditable') == "1") {
			   $('#'+domId+' .editableSmartCellParent').addClass("disabledclass");
		   }
		   
	   }
	   if($('#formCode').val() == 'StepMinFr' && domId == 'products'){
		   $('#'+domId).attr('batchSizeElement','batchSizeProd');
		   // if disable edit table we also disable all the icons (in this case documents add and drag columns) - consider make if in changePlannedCompositionsTableByCompositionType
		   if($('#'+domId).attr('disableEditable') == "1") {
			   $('#'+domId+' .editableSmartCellParent').addClass("disabledclass");
		   }
	   }
   }
   if($('#formCode').val() == 'RecipeFormulation' && domId == 'recipeUsages'){
	   disableComponentTable('1',domId);
		
   }
   if($('#formCode').val() == 'ExperimentFor' && domId == 'plannedCompositions'){
	   $('#plannedCompositions_length').insertAfter($('#plannedCompositions_filter'));
	   $('#plannedCompositions_length').css('margin-top','10px');
       $('#plannedCompositions_tableFilterControls').css('border','0');
   }
   if($('#formCode').val() == 'ExperimentFor' && domId == 'importedCompositionTable'){
	   $('#importedCompositionTable_length').insertAfter($('#importedCompositionTable_filter'));
	   $('#importedCompositionTable_length').css('margin-top','10px');
       $('#importedCompositionTable_tableFilterControls').css('border','0');
   }
   if($('#formCode').val() == 'ExperimentFor' && (domId == 'instrumentsTable' || domId == 'additionalEquip'))
   {
	   $('#'+domId+'_dataTableStructButtons button.dataTableApiView').css('display','none');
	   $('#' + domId + '_dataTableStructButtons button.dataTableApiNew:not(.dataTableAddRowButton)').css('display','none');
	   if(domId == 'instrumentsTable'){//fixed bug 8934
		   $('#instrumentsTable_dataTableStructButtons').append($('#searchInstrument'));
	   }
   } 
   if($('#formCode').val() == 'ExperimentFor' && (domId == 'materials' || domId == 'instruments')){
	   $('#'+domId+'_dataTableStructButtons button.dataTableApiView').css('display','none');
	   $('#' + domId + '_dataTableStructButtons button.dataTableApiNew').css('display','none');
	   $('#'+domId+'_dataTableStructButtons button.dataTableApiOptional1').text('Update Training');
	   $('#'+domId+'_dataTableStructButtons button.dataTableApiOptional1').removeClass("disabledclass");
	   //When the Familiarity is Yes for the current user - the Trained checkbox of the same row is disabled 
	   var disableUpdateTrainingButton = true;
 	   $('#'+domId+'_dataTableStructButtons button.dataTableApiOptional1').click(function(){updateTraining(domId);});
 	   var table = $('#' + domId).DataTable();
 	   var columnInd = getColumnIndexByColHeader(domId,"Familiarity");
 	   table.rows().eq(0).each( function ( index ) {
 		   var cell = table.cell({row: index, column: columnInd});
 		   var cdata = cell.data();
 		   if(cdata == 'Yes'){
 			  var trainedInd = getColumnIndexByColHeader(domId,"Trained");
 			  trained_cell = table.cell({row: index, column: trainedInd});
 			  node = trained_cell.node();			    
			  $input = $(node).find('input');
			  $input.attr('disabled',true);
 		   }
 		   else{
 			  disableUpdateTrainingButton = false;
 		   }
	       });
 	   if(disableUpdateTrainingButton){//when all rows' Familiarity is Yes - the Update Training button is disabled
 		  $('#'+domId+'_dataTableStructButtons button.dataTableApiOptional1').addClass("disabledclass");
 	   }
   }
   if($('#formCode').val() == 'ExperimentFor' && domId == 'samples'){
	   var statusName = $('#STATUS_ID').attr("lastselectedname");
	   if(statusName != 'Active' && statusName != 'Finished'){
	   var table = $('#'+domId).DataTable();	
	    var cells = table.cells().nodes();
	    $(cells).find('textarea,input').addClass('authorizationDisabled');
	   }
   }
   if(($('#formCode').val() == 'ExperimentFor'||$('#formCode').val() == 'ExperimentAn'||$('#formCode').val() == 'Experiment'||$('#formCode').val() == 'ExperimentCP'||$('#formCode').val() == 'ExperimentStb'
	   ||$('#formCode').val() == 'ExperimentPrGn'||$('#formCode').val() == 'ExperimentPrCR'||$('#formCode').val() == 'ExperimentPrBT'||$('#formCode').val() == 'ExperimentPrTS'||$('#formCode').val() == 'ExperimentPrVS') 
		   && domId == 'samples'){
	   var creator_id = $('#CREATOR_ID').val();
	   var user_id = $('#userId').val();
	   if(creator_id != user_id){
		   var table = $('#'+domId).DataTable();	
		   var sampleDescIndex = getColumnIndexByColHeader(domId,"Sample Description");
	       table.rows().eq(0).each( function ( index ) 
			{//init the style of all the columns o the basic one
				var cell = table.cell({row: index, column: sampleDescIndex}); 
			    var node = cell.node();			    
			    var $div = $(node).find('div');
			    $div.addClass('authorizationDisabled');
			    $div.attr('contenteditable','false');
			});
	   }
   }
   if(($('#formCode').val() == 'ExperimentFor'||$('#formCode').val() == 'ExperimentAn'||$('#formCode').val() == 'Experiment'||$('#formCode').val() == 'ExperimentCP'||$('#formCode').val() == 'ExperimentStb'
	   ||$('#formCode').val() == 'ExperimentPrGn'||$('#formCode').val() == 'ExperimentPrCR'||$('#formCode').val() == 'ExperimentPrBT'||$('#formCode').val() == 'ExperimentPrTS'||$('#formCode').val() == 'ExperimentPrVS') 
		   && domId == 'sampleTableEdit'){
	   var creator_id = $('#CREATOR_ID').val();
	   var user_id = $('#userId').val();
	   if(creator_id != user_id){
		   var table = $('#'+domId).DataTable();	
		   var sampleDescIndex = getColumnIndexByColHeader(domId,"Description");
	       table.rows().eq(0).each( function ( index ) 
			{//init the style of all the columns o the basic one
				var cell = table.cell({row: index, column: sampleDescIndex}); 
			    var node = cell.node();			    
			    var $div = $(node).find('div');
			    $div.addClass('authorizationDisabled');
			    $div.attr('contenteditable','false');
			});
	   }
   }
   if($('#formCode').val() == 'ExperimentAn' && (domId == 'instrumentsTable'||domId == 'columnSelect' ||domId == 'testedComponents')){
	   $('#' + domId + '_dataTableStructButtons button.dataTableApiAdd').css('display','none');
	   if(domId == 'instrumentsTable'){
		   $('#instrumentsTable_dataTableStructButtons').append($('#searchInstrument'));
	   }
	   if(domId == 'columnSelect'){
		   $('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').off('click');
     	   $('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').attr("onclick","removeRowColumnSelect('"+domId+"')");
	   } 
	   if(domId == 'testedComponents'){
		   $('#testedComponents_dataTableStructButtons').append($('#material_search'));
	   }
   }
   if($('#formCode').val() == 'ExperimentFor' && domId == 'batches'){
	   $('#' + domId + '_dataTableStructButtons button.dataTableApiNew').css('display','none');
   }
   if($('#formCode').val()=='Request' && domId == 'operartinTypeTable'){
	   $('#' + domId + '_dataTableStructButtons button.dataTableApiAdd').removeAttr('onclick');
	   $('#' + domId + '_dataTableStructButtons button.dataTableApiAdd').attr('onclick','validateMandatoryFilledAndAddRow("'+$('#formCode').val()+'","'+domId+'")');
   }
   if(($('#formCode').val()=='InvItemMaterialFr'||$('#formCode').val()=='InvItemMaterialPr'||$('#formCode').val()=='InvItemBatch'||$('#formCode').val()=='Project') && domId == 'components'){
	  if($('#formCode').val().indexOf('InvItemMaterial')>-1 && $('#components').is(":visible")){
		  $('#components_Parent').css('min-width','550px');
	  }
	  else  if($('#components').is(":visible")){
		  $('#components_Parent').css('min-width','400px');
	  }
	   $('#' + domId + '_dataTableStructButtons button.dataTableApiNew:not(.dataTableAddRowButton)').css('display','none');
	   $('#' + domId + '_dataTableStructButtons button.dataTableApiView').css('display','none'); 
	 
	   }
   if($('#formCode').val()=='Project' && domId == 'groups'){
	   $('#' + domId + '_dataTableStructButtons button.dataTableApiEditShared').text('Add') ;
   $('#groups_dataTableStructButtons button.dataTableApiOptional1').addClass("disabledclass");
   $('#groups_dataTableStructButtons button.dataTableApiOptional1').text('Edit');
   $('#groups_dataTableStructButtons button.dataTableApiOptional1').click(function(){editExperimentGroup(domId);});
   $('#groups_dataTableStructButtons button.dataTableApiLabel').text('Cancel');
   $('#groups_dataTableStructButtons button.dataTableApiLabel').addClass("disabledclass");
   $('#groups_dataTableStructButtons button.dataTableApiLabel').click(function(){
	
		    openConfirmDialog({
		onConfirm : function(){onClickCancelExperimentGroup(domId);},
		title : 'Warning',
		message : getSpringMessage('Are you sure you want to cancel the experiment group')
	})});	
   $('[id="' + domId + '"] tbody').on('click', 'tr', function (e) {
	   $('#groups_dataTableStructButtons button.dataTableApiLabel').removeClass("disabledclass");
	   $('#groups_dataTableStructButtons button.dataTableApiOptional1').removeClass("disabledclass");
		selectedTable = $('#' + domId).DataTable();
        custid = selectedTable.row('.selected').data();
        if(custid != undefined ){
        	rowId = custid[1];
        	getExperimentsPerGroup(rowId);
        }
   });
}
   if(($('#formCode').val()=='ExperimentPrTS'|| $('#formCode').val()=='ExperimentPrCR') && domId == 'documents'){

	   	var table_ = $('#documents').DataTable();
 		 
 		var allData = getformDataNoCallBack(1);
 		// url call
 		var urlParam = "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val()
 				+ "&eventAction=getExportToReport&isNew=" + $('#isNew').val();

 		var data_ = JSON.stringify({
 			action : "getExportToReport",
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
 					hideWaitMessage();
 					} else if (obj.data[0].val.toString() != null && obj.data[0].val.toString()!=""){
 						var results = obj.data[0].val.toString();
 						var exportToReportArray = results.split("-");
 						for (var i = 0; i < exportToReportArray.length; i++) {
 							table_.$('input[type="checkbox"][value="'+exportToReportArray[i]+'"]').prop('checked', true);
 						}
 					}
					},
					error : handleAjaxError
				});//
	   
 }
   if(($('#formCode').val() == 'ExperimentReport' && domId == 'reportTable')
		   ||($('#formCode').val() == 'Experiment' && (domId == 'experimentResults'|| domId =='selfTestResults'))
		   ||($('#formCode').val() == 'Step' && (domId == 'results'|| domId =='selftestresults'))
		   ||($('#formCode').val() == 'ExperimentFor' && (domId == 'experimentResults'|| domId =='selfTestResults'))
		   ||($('#formCode').val() == 'ExperimentAn' && (domId == 'results' || domId == 'subsequentResults' || domId == 'manualResultsTable'))
		   ||($('#formCode').val().slice(0,12) =='ExperimentPr' && (domId == 'subsequenResults'|| domId =='subsequentResults' || domId == 'resultsTable'))
		   ||($('#formCode').val() == 'ExpAnalyReportMain' )){
	   $('#'+domId+'_wrapper > .dropdown-button').css('float','left');//task 26594
   } 
   if(_formCode == "Maintenance"){//bug 9439
	  var maintFormCode = $('#eMaintenanceTableApi_structCatalogItem').val();
	  if(maintFormCode != undefined && maintFormCode =='MP'){
		  $('[id="' + domId + '_colsArray"]').val('WL@WH@SL@SH');
		  var _table = $('#'+domId).DataTable();
//		   var wvLFlag = 0;
		   for(var i=0; i < _table.columns().header().length; i++) {
				var col_ = _table.column(i);
				
				col_ = _table.column(i);
				var _$header = $(col_.header());
				
				if(_$header.text() == 'WL' ||_$header.text() == 'WH'
					||_$header.text() == 'SL' ||_$header.text() == 'SH') {
					removeColumnDatatable(domId, _$header.text());	
				}
		   }
	  }
   }
   return true;
}  

function openTableInNewTab(domId){
	var page = "";
	if(domId == 'XXX'){
//		page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=" + "XXX" + "&formId=" +"-1" + "&userId=" + $('#userId').val() + '&PARENT_ID=' + $('#formId').val();
	}
	if(page!=""){
		openNewTab(page);
	}
}
 
/**
 * Invoke code when isNew = 1
 * 
 * @param domId
 * @returns
 */
function onElementDataTableApiIsNew(domId) {
    var isNew = $('#isNew');
    if (isNew.length > 0) {
        if (isNew.val() == "1") {
            if ($('#' + domId + '_onActionButtons').val() == 'Full Screen') {
                // Disable New button when isNew = 1 and the page target is on
				// full screen
                $('#' + domId + '_dataTableStructButtons button.dataTableApiNew').addClass('disabledclass');
                var $divMultiAddRows = $('#' + domId + '_dataTableStructButtons button.dataTableApiNew').parent('.multiRowsDiv');
                if( $divMultiAddRows.length = '1'){
                	$divMultiAddRows.addClass('disabledclass')
                }
            }
        }
    }
}

/**
 * Change caption on special cases
 * 
 * @param domId
 * @returns
 */
function changeElementDataTableApiCaption(domId) {
   
}

/**
 * Disable datatableApi's buttons after loading. 
 * Note! - this function should cover the scenario in which the table is render after the authz. 
 *         the isGeneralDisabledStateForLateRender return true if this scenario happened (mostly when no permission on the page)
 *		   in General we only put the disabled page on all of the 
 *       
 * 
 * @param domId
 * @returns
 */
function elementDataTableApiImpGeneralDisabled(domId) {
	//fix bug 8452
	if(domId != null && domId == 'training') {
		return;
	}
	//Edit to view popup in disabledclass (form builder disabled deffinition) 
	if($('#' + domId + '_dataTableStructButtons button.dataTableApiEdit').hasClass('dataTableApiButtonDisabled')) {
		$('#' + domId + '_dataTableStructButtons button.dataTableApiEdit').removeClass('disabledclass');
		$('#' + domId + '_dataTableStructButtons button.dataTableApiEdit').removeClass('dataTableApiButtonDisabled');
    	changeSingleDTLabelByDisabledState(domId, true);//+"_dataTableStructButtons"
	}  
	
	//EditShared stays disabled (form builder disabled deffinition )
	if($('#' + domId + '_dataTableStructButtons button.dataTableApiEditShared').hasClass('dataTableApiButtonDisabled')) {
		$('#' + domId + '_dataTableStructButtons button.dataTableApiEditShared').removeClass('disabledclass');
		$('#' + domId + '_dataTableStructButtons button.dataTableApiEditShared').removeClass('dataTableApiButtonDisabled');
    	changeSingleDTLabelByDisabledState(domId, true);//+"_dataTableStructButtons"
	}
	
    if($('#' + domId).attr("disableEditable")=="1"){//for editable tables, this code will ensure the disabling of them
		console.log("elementDataTableApiImpGeneralDisabled = ", domId);
		changeSingleDTLabelByDisabledState(domId, true);
	}  
	
	//late render scenario
	var isNew = $('#isNew');
    if (isNew.length > 0) {
        if ((isNew.val() == "0") && ($('#isStruct').val() == "1")) {
            if ($('#' + domId + '_Parent').attr('isfirstload') == '1') {
                $('#' + domId + '_Parent').attr('isfirstload', '0');
                if($('#' + domId).attr("disableEditable")==undefined && isGeneralDisabledStateForLateRender()) {
                	console.log("elementDataTableApiImpGeneralDisabled = ", domId);
                	$('#' + domId + '_dataTableStructButtons button.dataTableApiButton:not(.dataTableApiEditShared, .dataTableApiView, .dataTableApiLabel)').addClass('disablePage'); //kd 26112019 added .dataTableApiLabel for fixed bug7681
                	$('#' + domId + '_dataTableStructButtons button.dataTableApiButton:not(.dataTableApiEditShared, .dataTableApiView)').attr('LATE_RENDER','1'); // yp add it for debuging
                	changeSingleDTLabelByDisabledState(domId, true); // yp 17012019 put it to be on the safe side     //+"_dataTableStructButtons"
                }
            }
        }
	}
      
}

/**
 * Allow Clone for Struct with legal data
 * 
 * @param domId
 * @returns
 */
function allowCloneElementDataTableApi(domId) {
   
}

/**
 * 
 * @param struct
 * @param parentTable
 * @param isParentSelected
 * @returns Available DataStructure
 */
function dataTableApiDataStructure(struct, parentTable, isParentSelected)  {
	
}

/**
 * show Extras by formCode when needed
 * 
 * @returns
 */
function showExtras(domId) {
     
}

function generalBL_elementDataTableClickEvent(domId, customerFunction, params, $htmlObj)
{
 
} 

function generalBL_DTRowClickEvent(domId, rowDataArr, paramsObj)
{
 
}

function topRowsToDisplay_BL(domId,displayTopRows){
	if(displayTopRows !="-2"){//the table is not empty
		 $('#' + domId + '_displayTopRows').html('');
		 $('#' + domId + '_displayTopRows').prepend('<label class="cssStaticData">Top '+displayTopRows+' Results</label>');
		 $('#' + domId + '_displayTopRows').css('display','block');		
	 }
}

function doClearWhenEmpty(domId,formCode){
	return true;
}
   
function bl_elementDatatableEditableCustomFuncHandler(domId, customerFunction, $htmlObj, params)
{
	console.log("customerFunction", customerFunction);
	console.log("params", params);
	var formCode = $('#formCode').val();

	//favoritestar
	if (customerFunction == "onChangefavoritestar"){
		var rowId = $htmlObj.attr('rowId');
		var isFavoriteChecked = -1;
		if ($htmlObj.hasClass('fa fa-star')) {
			isFavoriteChecked = 0;
			$htmlObj.removeClass('fa fa-star');
			$htmlObj.addClass('fa fa-star-o');
			$htmlObj.css("color","black");
			$htmlObj.attr('value','no');
		} else if ($htmlObj.hasClass('fa fa-star-o')) {
			isFavoriteChecked = 1;
			$htmlObj.removeClass('fa fa-star-o');
			$htmlObj.addClass('fa fa-star');
			$htmlObj.css("color","#62B2DB");
			$htmlObj.attr('value','yes');
			
		}
		onChangefavorite(rowId,isFavoriteChecked,true,domId);	
		return;
	}
}
 
function bl_elementDatatableEditableAfterSaveHandler(domId, isRenderTable, afterSave_retVal, $htmlObj, paramsObj, rowIndex)
{
	console.log("bl_elementDatatableEditableAfterSaveHandler() domId: ", domId);
	console.log("isRenderTable: "+isRenderTable);
	
	var htmlType = paramsObj.htmlType;
	var dbColumnName = paramsObj.dbColumnName;
	var formCode = paramsObj.formCode;
	var isMandatory = paramsObj.isMandatory;
	var colTitle = paramsObj.colTitle;
	var isRenderTable = paramsObj.isRenderTable;
	var formNumberID = paramsObj.formNumberID;
	var saveType = paramsObj.saveType;
	var rowMandatoryField = paramsObj.rowMandatoryField;
	var rowMandatoryFieldID = paramsObj.rowMandatoryFieldID;
	var rowMandatoryFieldDisplayName = paramsObj.rowMandatoryFieldDisplayName;
	var isCellAutoSaved = paramsObj.isCellAutoSaved;
	var customFuncName = paramsObj.customFuncName;
	var customFuncParams = paramsObj.customFuncParams;
	var customFuncOnBeforeChange = paramsObj.customFuncOnBeforeChange;
	
	
	if(isRenderTable)
	{
		onElementDataTableApiChange(domId);		
	}
	else
	{
		var formCode = $('#formCode').val();
		
		if(formCode == "XXX")
		{
			 
		}
	}
}

function editableBL_getAdditParams(domId,rowInx,paramName)
{
	var returnData = "";
//	if(domId == "xxx" && paramName == "formNumberID")
//	{
//		var table = $('#'+domId).DataTable();		
//		returnData = table.row(rowInx).data()[5];
//	}
	return returnData;
}

function bl_isDeferRender(domId)
{
	var toReturn = false;
	 
	return toReturn;
}

function bl_isTableResizable(domId)
{ 
	var toReturn = false;
	var formCode = $('#formCode').val();
	if(formCode == "SystemLogReport")
	{
		toReturn = true;
	}
	return toReturn;
}

function bl_getResizableTables()
{
	var formCode = $('#formCode').val();
	if(formCode == "SystemLogReport") 
	{
		return $('table.dataTable');
	}
	
	return [];
}

function bl_getColumnDefaultWidth(domId, colsCount, followingHdnColsCount)
{
	var $colWidth = {};
	var formCode = $('#formCode').val();
	var resizableColDefaultWidth = 100;
	var winWidth = $(window).outerWidth();	
//	console.log("init winWidth: "+ winWidth," $(document).outerWidth(): "+$(document).outerWidth());
	var invisibleColsArr = [];
	
    if(isSameStructTable(domId)) {
    	var invisibleCols = $('[id="' + domId + '_colsArray"]').val();
    	invisibleColsArr = (invisibleCols.length > 0)?invisibleCols.split('@'):[];
    }
	var actualDisplayedColsCount = colsCount - (followingHdnColsCount + invisibleColsArr.length);
	
		
	resizableColDefaultWidth = winWidth/actualDisplayedColsCount; 
	
	
	$colWidth.DefaultWidth_ = resizableColDefaultWidth;
	return $colWidth;
}

function bl_isTableMergeable(domId)
{
	var toReturn = false;
	return toReturn;
}

function bl_isTableHasContextMenu(domId, tableRole)
{
	var toReturn = true;	
	var formCode = $('#formCode').val();
    if(tableRole == 'Attachment')
	{
		toReturn = false;
	}
	
	return toReturn;
}

function bl_isTableDrawUpgraded(domId)
{
	var toReturn = false;
	return toReturn;
}

/*
 * TODO: add Column Reorder support for all editable tables, for now only 'Action' table is fully supported.
 * 		Main reason for this, that the other editable tables may have specific behavior, different from editable table default behaviors, 
 * 			like in reactions table for example: if column was reordered and after this row added, the new row columns location not fitted actually columns location in previous rows, 
 * 			the reason is that the data structure object for new row saved on the client side (except for the first added row)
 * 		Each other editable tables should be checked before support for reorder.
 */
function bl_isTableReorderable(domId)
{
	var toReturn = true;
	
	return toReturn;
}

function bl_isColumnReorderable(domId, colName) 
{
	var toReturn = true;
 
	return toReturn;
}
 
function bl_removeLabelFromColName(domId) 
{
	var toReturn = false;
	
	return toReturn;
}

function outPutLabelDTWrapper(obj) {
	
//	outPutLabel(domId, labelCode, selectIdList);
}
    
function convertDecimalToExponential(valueToChange, prec) 
{	
	var valueToReturn = $.trim(valueToChange);
	try
	{
		var floatVal = parseFloat(valueToReturn);
		var precision = (prec !== undefined && prec != "")?prec:"3";
		precision = parseInt(precision);		
		
		if(!isNaN(floatVal) && floatVal != 0)
		{			
			// in case after round by precision, value display only zeros -> convert to Exponential
			var zeros = "";
			while(zeros.length < precision)
			{
				zeros += "0";
			}
			zeros = parseInt("1"+zeros);	
			
			if((floatVal * zeros) < 1)
			{
				valueToReturn = floatVal.toExponential(precision);
			}
			else
			{
				valueToReturn = floatVal.toFixed(precision);
			}
			var str = valueToReturn.toString();
			if (str.indexOf('e') !== -1) 
			{
				valueToReturn = str.toUpperCase();
			}
		}
	}
	catch(e)
	{
		console.log("ERROR in convertDecimalToExponential() for value: "+valueToChange);
		console.error(e);
	}
	return valueToReturn;
}

function bl_editableCellOnFocusCustomFuncHandler($htmlObj, customFuncName, customFuncParams)
{
	
	//if(customFuncName == "XXX") return handleYYY($htmlObj, false);
	
	return true;
}
 
function bl_setColVisibilityByColTitleArr(domId, isVisible, context) {
	var isOverride = false;
	//....
	return isOverride;
}

function openFormInNewWindow (domId,formId)
{
	var formCode = encodeURIComponent($('[id="' + domId + '_structCatalogItem"]').val());
	var stateKey = $('#' + domId + '_selectDiv').attr('stateKey');
	var parentId = $('#formId').val();
	
	var canRead = "0"
       $.ajax({
           type: 'POST',
           data: '{"action" : "getReadPermissionById","' + 'data":[' + '{"code":"formCode","val":"' + formCode + '"},' + '{"code":"formId","val":"' + formId + '"}' + '],' + '"errorMsg":""}',
           url: "./getReadPermissionById.request",
           contentType: 'application/json',
           dataType: 'json',
           success: function (obj) {
           	var canView= false;
               if (obj.errorMsg != null && obj.errorMsg != '') {
                   displayAlertDialog(obj.errorMsg);
                   return;
               } else if ((obj.data[0].val == "-1") || (obj.data[0].val == "")) {
               	//false
               } else {
                   canRead = obj.data[0].val;
               }
               
               if(canRead != "1") {
               		displayAlertDialog("View is not allowed");
               		return;
               } 
               else {
               		// make view code
               		var page = "./init.request?stateKey=" + stateKey + "&formCode=" + formCode + "&formId=" + formId + "&userId=" + $('#userId').val() + '&tableType=' + $('[id="' + domId + '_tableType"]').val()
               					+ '&urlCallParam=' + replaceDTUrlCallParamVal($('[id="' + domId + '_urlCallParam"]').val(), formId) + '&PARENT_ID=' + parentId;

               		openNewTab(page);
               		//return;
               	
               }
           },
           error: handleAjaxError
       }); 
}

function customizeExcelDTReport(xlsx, domId)
{
	 
} 

function addTitle(sheet , title) {
//	 var sheet = xlsx.xl.worksheets['sheet1.xml'];
     var downrows = 1;
     var clRow = $('row', sheet);
     //update Row
     clRow.each(function () {
         var attr = $(this).attr('r');
         var ind = parseInt(attr);
         ind = ind + downrows;
         $(this).attr("r",ind);
     });

     // Update  row > c
     $('row c ', sheet).each(function () {
         var attr = $(this).attr('r');
         var pre = attr.substring(0, 1);
         var ind = parseInt(attr.substring(1, attr.length));
         ind = ind + downrows;
         $(this).attr("r", pre + ind);
     });

     function Addrow(index,data) {
         msg='<row r="'+index+'">'
         for(i=0;i<data.length;i++){
             var key=data[i].k;
             var value=data[i].v;
             msg += '<c t="inlineStr" r="' + key + index + '">';
             msg += '<is>';
             msg +=  '<t>'+value+'</t>';
             msg+=  '</is>';
             msg+='</c>';
         }
         msg += '</row>';
         return msg;
     }

     //insert
     var r1 = Addrow(1, [{ k: 'A', v: title }]);
//     $('row c[r*="1"]', sheet).attr( 's', '2' );
      
     sheet.childNodes[0].childNodes[1].innerHTML = r1 + sheet.childNodes[0].childNodes[1].innerHTML;
}

function bl_dtHeaderRenderCustom(domId, dtColumnsApi, $header) {
	
}

function bl_dtColumnFeaturesCustom(domId, dtObject, currColumn, colIndex) {
	 
}
 
function isSameStructTable(domId)
{
	var _structCatalogItem = $('#' + domId + '_structCatalogItem').val();
    var _lastStruct = $('[id="' + domId + '_lastStruct"]').val();
    try{
    	_structCatalogItem = (_structCatalogItem == null || _structCatalogItem == undefined || _structCatalogItem == "undefined")? "": _structCatalogItem;
        _lastStruct = (_lastStruct == null || _lastStruct == undefined || _lastStruct == "undefined")? "": _lastStruct;
    }
    catch (e) {
    	_structCatalogItem = "";
    	_lastStruct = "";
	}
    if( _structCatalogItem == _lastStruct) {
    	return true;
    }
    return false;
}
 
function openConfirmMaterialCancelationDialog(domId){
    var struct = $('[id="' + domId + '_structCatalogItem"]').val();
    var selectedTable = $('#' + domId).DataTable();
    var custid = selectedTable.row('.selected').data();
    var formId = "";
    if (typeof custid !== 'undefined') {
        formId = custid[0];
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
				+ "&eventAction=checkCancelMaterial"
				+ "&isNew=" + $('#isNew').val();
	var data_ = JSON.stringify({
		action : "checkCancelMaterial",
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
				hideWaitMessage();
				displayAlertDialog(obj.errorMsg);
			} else {
				hideWaitMessage();
				openConfirmDialog({
					onConfirm : function(){onClickCancelMaterial(formId);},
					title : 'Warning',
					message : getSpringMessage('confirmMaterialCancellation')
				});
			}
		},
		error : handleAjaxError
	});
}
  
function bl_initFilterColumnDatatable(domId){
	 
	var toReturn = false;
	//...
	return toReturn;
}

function removeRowColumnSelect(domId){
	selectedTable = $('#' + domId).DataTable();
    custid = selectedTable.row('.selected').data();
    if(custid != undefined ){
    	rowId = custid[1];
    	var allData = [{
			code : "removedId",
			val : rowId,
			type : "AJAX_BEAN",
			info : 'na'
		}];
	// url call
	var urlParam = "?formId="+ $('#formId').val()
				+ "&formCode="+ $('#formCode').val()
				+ "&userId="+ $('#userId').val()
				+ "&eventAction=removeRowColumnSelect"
				+ "&isNew=" + $('#isNew').val();
	var data_ = JSON.stringify({
		action : "removeRowColumnSelect",
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
				hideWaitMessage();
				displayAlertDialog(obj.errorMsg);
			} else if (obj.data[0].val == "-1") {
                hideWaitMessage();
                displayAlertDialog(getSpringMessage('remove failed'));
            } else {
				hideWaitMessage();
				onElementDataTableApiChange('columnSelect');
			}
		},
		error : handleAjaxError
	});
    }
}

/**
 * replace uomPlaceHolder (prop) in the table columns with the UOM of all the rows (as csv). 
 * in each row the UOM is taken from the [] expression and remove the UOM data from the cell value.
 * for example for uomPlaceHolder <$U>:
 * 
 * Col1 $U ... will become ->  Col1 [gr,kg] 
 * 1 [gr]					   1
 * 2 [kg]					   2
 * 						
 * @param domId
 * @returns
 */
function collectUOMtoColumnTitle(domId, uomPlaceHolder) {
   var _table = $('#'+domId).DataTable(); 
   for (var i=0; i < _table.columns().header().length; i++) {
	   try {
		   var col_ = _table.column(i);
			col_ = _table.column(i);
			var _$header = $(col_.header());
			var old_header = _$header.text();
			if(_$header.text().indexOf(uomPlaceHolder) >= 0) { 
				var uomData = [];
				_table.rows().eq(0).each( function ( index ) {
					var cell = _table.cell({row: index, column: i});	 
					var cdata = cell.data();
					var cnode = cell.node();
					if(cdata != null) {
						var uom_match_=cdata.match(/\[([^)]+)\]/); 
						if(uom_match_ != null) {
							var uom_= uom_match_[1];
							if(uomData.indexOf(uom_) == -1) {
								uomData.push(uom_);
							}
							var newCellVal = cdata.replace('[' + uom_ + ']','');
							cell.data(newCellVal).draw(false);
							$(cnode).text(newCellVal);
							dtExt_updateCellFilterData(domId, index, i, newCellVal); // need for the export data (or if calling not from elementDataTableApiImpBL)
						} else if(cdata.indexOf("[]") != -1) { //cover also empty uom (remove it)
							var newCellVal = cdata.replace('[]','').trim();
							
							cell.data(newCellVal).draw(false);
							$(cnode).text(newCellVal);
							dtExt_updateCellFilterData(domId, index, i, newCellVal); // need for the export data (or if calling not from elementDataTableApiImpBL)
						}
					}
				}); 
				var originHtml = _$header.html();
				var newHtml = originHtml.replace(uomPlaceHolder,uomData.length > 0?" [" + uomData.join() + "]":"");
				$(_table.column(col_).header()).html(newHtml); // replace the title in the column html (if using set text we will not have the sort and close icons)
				$('[name="' + domId + '_metaData"]').val($('[name="' + domId + '_metaData"]').val().replace(old_header,_$header.text()+"_SMARTNUM"));//fixed bug in the filter 
		    }
	   } catch(e) {
			console.log("collectUOMtoColumnTitlefailed in column index=" + i);
	   }
   }
}
