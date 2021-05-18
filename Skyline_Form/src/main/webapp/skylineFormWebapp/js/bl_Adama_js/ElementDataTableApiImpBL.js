/**
 * ElementDataTableApiImp BL Adama integration
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
			if($(this).prop('checked'))
	        {
				$('#samples_dataTableStructButtons button.dataTableApiOptional1').removeClass('disabledclass');
	        }else{
	        	 $('#samples_dataTableStructButtons button.dataTableApiOptional1').addClass('disabledclass');
	        }
	   });
   } else if(domId == 'specificationTable' && $('#formCode').val() == 'ExpImpSpec'){
	   renderElementAuthorizationImp();
   } else if(domId == 'experimentTable' && $('#formCode').val() == 'ExpAnalysisReport'){
	   renderElementAuthorizationImp();
   } else if(domId == 'reportTable' && $('#formCode').val() == 'ExpAnalysisReport'){
	   //ab 20082020: removed nowrap because the table have the ability to column resize now
	   $('#reportTable th').css('min-width','200px');
	   //kd 29022020 fixed bug-7922: The option to export the report table to PDF should be removed 
	   $('#reportTable_wrapper').find('div.dropdown-content div.dt-buttons a').eq( 1 )  //remove PDF
	   			.remove();
	   			//.('<a class="" tabindex="0" aria-controls="reportTable" href="#"><span>PDF</span></a>');
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
	              modal: true,
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
  	              modal: true,
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
				
				
				var selectedTable = $('#' + domId).DataTable();
	            var custid = selectedTable.row($this).data();
				
					var spread_id = custid[1];
					
	           
	            	   var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode="
			                    + "ViewSpreadsheet" + "&formId=-1" + "&userId="
		                    	+ $('#userId').val() + '&PARENT_ID='
		                    	+ spread_id;
	            		openNewTab(page);
				
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
   if($('#formCode').val() == 'ExperimentFor' && domId == 'batches'){
	   $('#' + domId + '_dataTableStructButtons button.dataTableApiNew').css('display','none');
   }
   if($('#formCode').val()=='Request' && domId == 'operartinTypeTable'){
	   $('#' + domId + '_dataTableStructButtons button.dataTableApiAdd').removeAttr('onclick');
	   $('#' + domId + '_dataTableStructButtons button.dataTableApiAdd').attr('onclick','validateMandatoryFilledAndAddRow("'+$('#formCode').val()+'","'+domId+'")');
   }
   if(($('#formCode').val()=='InvItemMaterialFr'||$('#formCode').val()=='InvItemMaterialPr'||$('#formCode').val()=='InvItemBatch'||$('#formCode').val()=='Project') && domId == 'components'){
	  if($('#formCode').val().indexOf('InvItemMaterial')>-1){
		  $('#components_Parent').css('min-width','550px');
	  }
	  else  $('#components_Parent').css('min-width','400px');
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
   return true;
}

function createBatch(){
	$.ajax({
        type: 'POST',
        data: '{"action" : "getNewAvailableFormList","' + 'data":[' + '{"code":"formCode","val":"' + $('#formCode').val() + '"},' + '{"code":"formId","val":"' + $('#formId').val() + '"},' + '{"code":"stateKey","val":"' + $('#stateKey').val() + '"}' + '],' + '"errorMsg":""}', //TODO key check
        url: "./getNewAvailableFormList.request",
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) {
       	  if (obj.errorMsg != null && obj.errorMsg != '') {
                 displayAlertDialog(obj.errorMsg);
             } else if ((obj.data[0].val == "-1") /*|| (obj.data[0].val == "")*/) {

             } else {
	                if(obj.data[0].val != ""){
	                    optionsArray = obj.data[0].val.split(',');
	                    if(optionsArray.indexOf("InvItemBatch")> -1)
	                        doNew('InvItemBatch');
	                    else{
	                    	 displayAlertDialog("Creating new batch is not allowed");
	                    }
	                }
        }}
	 ,
        error: handleAjaxError
   });
}

function validateMandatoryFilledAndAddRow(formCode,domId){
	//var mandatoryIndicator= isMandatoryFieldsRequired();
	var mandatoryList = ['#DESTUNIT_ID','#DESTLAB_ID','#REQUESTTYPE_ID'];
	if (!checkRequiredByList(mandatoryList))    		
    {
		displayAlertDialog("The Operation type depends on the Request Type.</br></br>Please fill the required fields");
		return;
    }
    dataTableAddRow('operartinTypeTable');
}

function checkBalanceAndRemoveRow(domId,$elem){
	showWaitMessage("Please wait...");
	var allData = getformDataNoCallBack(1);
	var action = "checkBalance";
	var selectedTable = $('#' + domId).DataTable();
	var custid = selectedTable.row('.selected').data();
	var selectedRowId = '';
	if (typeof custid !== 'undefined') {
        selectedRowId = custid[0];
    }
	var formIdToDelete = selectedRowId;
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + '&userId=' + $('#userId').val()
			+ "&eventAction=" + action + "&isNew=" + $('#isNew').val();
	
	var stringifyToPush = {
		code : "formIdToDelete",
		val : formIdToDelete!=undefined?formIdToDelete:"",
		type : "AJAX_BEAN",
		info : 'na'
	};
	allData = allData.concat(stringifyToPush);
	
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
					displayAlertDialog(warningToDisplay);
					return false;
				}
				
			}
			//if(doSave == '0'){
			if(warningToDisplay==""){//if the difference is invalid then the save process can't continue
				deleteRowElementDataTableApiImp($elem);
			}
		},
		error : function() {
			hideWaitMessage();
		} 
	});
}

function checkMaterialAndRemoveRow(domId,$elem){
	showWaitMessage("Please wait...");
	var allData = getformDataNoCallBack(1);
	var action = "checkMaterialCopiedToStep";
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + '&userId=' + $('#userId').val()
			+ "&eventAction=" + action + "&isNew=" + $('#isNew').val();
	var selectedTable = $('#' + domId).DataTable();
	var custid = selectedTable.row('.selected').data();
	var selectedRowId = '';
	if (typeof custid !== 'undefined') {
        selectedRowId = custid[0];
    }
	allData = allData.concat({
		code : "selectedRowId",
		val  : selectedRowId ,
	    type : "AJAX_BEAN",
		info : 'na'
	});

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
			var message = '';
        	var stepListHaveMaterials = obj.data[0].val;
			if(stepListHaveMaterials == ''){
				message = 'Are you sure?'
			} else {
				message = "Removing this material will also remove it from the step.</br> Are you sure you want to continue?"
			}
			openConfirmDialog({     		
				onConfirm: function(){removeMaterialsFromStep(selectedRowId,stepListHaveMaterials,$elem);},
				onConfirmParams: $elem,
				message: message
		   });
		},
		error : function() {
			hideWaitMessage();
		} 
	});
}

function removeMaterialsFromStep(selectedRowId,stepListHaveMaterials,$elem){
	var allData = getformDataNoCallBack(1);
	allData = allData.concat({
		code : "selectedRowId",
		val  :  selectedRowId,
	    type : "AJAX_BEAN",
		info : 'na'
	});
	var data_ = JSON.stringify({
		action : "doSave",
		data : allData,
		errorMsg : ""
	});
	var urlParam = "?formId=" + $('#formId').val()
	// + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val()
	+ "&eventAction=removeMaterialsFromStep&isNew="; // + $('#isNew').val();
	
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey="
				+ $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',
		success : function(obj) {
			deleteRowElementDataTableApiImp($elem);
			if(stepListHaveMaterials == ''){
				return;
			}
			var stepListArr = stepListHaveMaterials.split(',');
			for(var i = 0;i<stepListArr.length;i++){
				var stepId = stepListArr[i];
				var iframe = document.getElementById('AsyncIframe_stepIframes_'+stepId);
				iframe.contentWindow.onElementDataTableApiChange("compositions");
			}
		},
		error : handleAjaxError
	});
}

function cloneCompositionFromExpToStep(){
	showWaitMessage("Please wait...");
	var allData = getformDataNoCallBack(1);
	var action = "cloneCompositionFromExpToStep"
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + '&userId=' + $('#userId').val()
			+ "&eventAction=" + action + "&isNew=" + $('#isNew').val();
	
	// collect all smartselect from the source form
	var smartSelectList = "";
	var toReturn = [];  
	 parent.$('#plannedCompositions > tbody > tr input[class="dataTableApiSelectInfo"]:checked').each(function (index) {
	    toReturn.push($(this).val());
	});
	smartSelectList = toReturn.toString();
	
	allData = allData.concat({
		code : "compositionsToClone",
		val  : smartSelectList ,
	    type : "AJAX_BEAN",
		info : 'na'
	}); 
	allData = allData.concat({
		code : "EXPERIMENT_ID",
		val  : parent.$('#formId').val() ,
	    type : "AJAX_BEAN",
		info : 'na'
	});
	
	var data_ = JSON.stringify({
		action : "doSave",
		data : allData,
		errorMsg : ""
	});

	var newCompositionsList = '';
	var alertMessage= ''
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
			if (obj.errorMsg != null && obj.errorMsg != '') {
				displayAlertDialog(obj.errorMsg);
			} 
			else if(obj.data[0].val==""){
				displayAlertDialog("Please select at least one row in the planned composition table");
			}
			else {
				var data = funcParseJSONData(obj.data[0].val);
				alertMessage = data.alertMessage;
				newCompositionsList = data.newCompositionsList;
				showWaitMessage("Please wait...");
				var allData = getformDataNoCallBack(1);
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
				
				var stringifyToPush = {
					code : "density",
					val : parent.$('#density').val(),
					type : "AJAX_BEAN",
					info : 'na'
				};
				allData = allData.concat(stringifyToPush);
					
				var action = "checkBalance";
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
								displayAlertDialog(warningToDisplay+"</br>The values are being zeroed.</br>"+alertMessage);
							}
						}
						parent.onElementDataTableApiChange('plannedCompositions')
						if(warningToDisplay == ""){//no warning
							if(alertMessage!=''){
								displayAlertDialog(alertMessage);
							}
							var stepIframeList = parent.$('[id*="AsyncIframe_stepIframes_"]');
							for(var i=0;i<stepIframeList.length;i++){
								var stepId = stepIframeList[i].id;
								var iframe = stepIframeList[i];//document.getElementById('AsyncIframe_stepIframes_'+stepId);
								iframe.contentWindow.onElementDataTableApiChange('products');
								iframe.contentWindow.onElementDataTableApiChange('compositions');
							}
						} else {//there was a warning message when copying the products to the materials(difference value diviation) - then should clear the values of the new rows
							clearCompositionWwGrk(newCompositionsList);
						}
						
					},
					error : function() {
						hideWaitMessage();
					} 
				});
			}
		},
		error : function() {
			hideWaitMessage();
		} 
	});
}

function clearCompositionWwGrk(compositionIdList){
	showWaitMessage("Please wait...");
	var allData = getformDataNoCallBack(1);
	var stringifyToPush = {
			code : "parentId",
			val : $('#formId').val(),
			type : "AJAX_BEAN",
			info : 'na'
		};
	allData = allData.concat(stringifyToPush);
	var action = "clearCompositionArgs";
	if(compositionIdList!=undefined){
		allData = allData.concat({
			code : "compositionIdList",
			val  :  compositionIdList,
		    type : "AJAX_BEAN",
			info : 'na'
		});
	}
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
			var stepIframeList = parent.$('[id*="AsyncIframe_stepIframes_"]');
			for(var i=0;i<stepIframeList.length;i++){
				var stepId = stepIframeList[i].id;
				var iframe = stepIframeList[i];//document.getElementById('AsyncIframe_stepIframes_'+stepId);
				iframe.contentWindow.onElementDataTableApiChange('products');
				iframe.contentWindow.onElementDataTableApiChange('compositions');
			}
		},
		error : function() {
			hideWaitMessage();
		} 
	});
}

function validateFormulationTypeAndAddRow(formCode,domId,elem){
	if($('#FORMULATIONTYPE_ID').val()==''
			|| ($('#COMPOSITIONTYPENAME').val()=='Liquid' && $('#density').val()=='')
			|| $('#batchSize').val()==''){
		if($('#FORMULATIONTYPE_ID').val()==''){
			displayAlertDialog("Please select a Formulation Type");//mark the field with red color
			$('#FORMULATIONTYPE_ID_chosen').css('outline', '1px solid #a94442').find(':first').css('border-color', '#a94442');
			$('#FORMULATIONTYPE_ID').trigger('chosen:activate');
			if ($('label[for="FORMULATIONTYPE_ID"]').length > 0) {
	            $('label[for="FORMULATIONTYPE_ID"]').css('visibility', 'visible');
	        }
		}
		if($('#COMPOSITIONTYPENAME').val()=='Liquid' && $('#density').val()==''){
			if($('#FORMULATIONTYPE_ID').val()!=''){//ensure that the message waring on the missing formulation type is not displayed
				openConfirmDialog({
					onConfirm : function(){$('#density').focus();},
					title : 'Warning',
					message : getSpringMessage("Density cannot be empty"),
					onCancel : function(){$('#density').focus();}
				},true);				
			}
			$('#density').css('border-color', '#a94442').css('outline', 'transparent');
			if ($('label[for="density"]').length > 0) {
	            $('label[for="density"]').css('visibility', 'visible');
	        }
		}
		if($('#batchSize').val()=='' && $('#FORMULATIONTYPE_ID').val()!=''
			&& ($('#COMPOSITIONTYPENAME').val()=='Liquid' && $('#density').val()!=''
				|| $('#COMPOSITIONTYPENAME').val()!='Liquid')){//ensure that no other message displayed
			openConfirmDialog({
					onConfirm : function(){$('#batchSize').focus();},
					title : 'Warning',
					message : getSpringMessage("Batch size cannot be empty"),
					onCancel : function(){$('#batchSize').focus();}
				},true);
			$('#batchSize').css('border-color', '#a94442').css('outline', 'transparent');
			if ($('label[for="batchSize"]').length > 0) {
	            $('label[for="batchSize"]').css('visibility', 'visible');
	        }
		}
		return;
	} else {
		if(elem !=undefined){
			if($(elem).hasClass('dataTableApiAddMultiRows')){
				openInsetRowsDialog(domId);
			} else {
				dataTableAddRow(domId);
			}
		} else {
			dataTableAddRow(domId);
		}
	}
}
function openTableInNewTab(domId){
	var page = "";
	if(domId == 'experimentResults'){
		page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=" + "ReportTable" + "&formId=" +"-1" + "&userId=" + $('#userId').val() + '&PARENT_ID=' + $('#formId').val()+ '&DOMID=' + "experimentResults";
	}else if(domId == 'selfTestResults'){
		page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=" + "ReportTable" + "&formId=" +"-1" + "&userId=" + $('#userId').val() + '&PARENT_ID=' + $('#formId').val()+ '&DOMID=' + "selfTestResults";
	}else if(domId == 'action'){
		page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=" + "ReportTable" + "&formId=" +"-1" + "&userId=" + $('#userId').val() + '&DOMID=' + "actions" + '&EXPERIMENT_ID=' +$('#EXPERIMENT_ID').val()+ '&STEPSEQ=' +$('#formNumberId').val();
	}
	//var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=" + "ReportTable" + "&formId=-1&userId=" + $('#userId').val() + '&PARENT_ID=' + $('#formId').val();
	if(page!=""){
		openNewTab(page);
	}
}
function smartSelectStateMngBL(domId){
	if(($("#formCode").val() == "InvItemMaterial" ||$("#formCode").val() == "InvItemMaterialFr"||$("#formCode").val() == "InvItemMaterialPr")&& domId == "Batches")
	{
		var counter = 0;		
	    $("#"+domId+" > tbody > tr input[type='checkbox']").each(function(i)
	    {
	        if($(this).prop('checked'))
	        {
	            ++counter;
	        }
	    });
	    var isChecked = counter==0?false:true;
		if(isChecked == false){
			$('#Batches_dataTableStructButtons button.dataTableApiOptional1').addClass("disabledclass");
		} else {
			$('#Batches_dataTableStructButtons button.dataTableApiOptional1').removeClass("disabledclass");
		}
	}
	
	if(($("#formCode").val() == "Experiment"||$("#formCode").val() == "ExperimentFor"||$("#formCode").val() == "ExperimentCP") && domId == "steps") //add ExperimentCP for "Continuous Process"
	{
		var counter = 0;		
	    $("#"+domId+" > tbody > tr input[type='checkbox']").each(function(i)//$('input[class="dataTableApiSelectInfo"]:checked')
	    {
	        if($(this).prop('checked'))
	        {
	            ++counter;
	        }
	    });
	    var isChecked = counter==0?false:true;
		if(isChecked == false){
			$('#steps_dataTableStructButtons button.dataTableApiOptional1').addClass("disabledclass");
		} else {
			$('#steps_dataTableStructButtons button.dataTableApiOptional1').removeClass("disabledclass");
		}
	}
	if(($("#formCode").val() == "Step" || $("#formCode").val() == "StepFr" || $("#formCode").val() == "StepMinFr") && domId == "action") //add StepFr and StepFr //add ExperimentCP for "Continuous Process"
	{
		var counter = 0;		
	    $("#"+domId+" > tbody > tr input[class='dataTableApiSelectInfo']").each(function(i)//$('input[class="dataTableApiSelectInfo"]:checked')
	    {
	        if($(this).prop('checked'))
	        {
	            ++counter;
	        }
	    });
	    var isChecked = counter==0?false:true;
		if(isChecked == false){
			$('#action_dataTableStructButtons button.dataTableApiRemove').addClass("disabledclass");
		} else {
			$('#action_dataTableStructButtons button.dataTableApiRemove').removeClass("disabledclass");
		}
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
    var captionText = "";
    var $captionElem = $('#' + domId + '_Caption');
    var str = $('#' + domId + '_structCatalogItem').val();
    if ((str == "SubProject") || (str == "SubSubProject")|| (str == "SelfTest")) {
        // Add hyphen to caption when the struct is SubProject or SubSubProject
        str = str.replace(/([A-Z])/g, "-$1");
        captionText = str.slice(1);
    } 
    else if (str == "Choose"){
    	captionText = "";
    }
    else {
    	captionText = str;
    }
//    console.log("_Caption: ", captionText);
    $captionElem.html(captionText);
    
    if(captionText == "")
    {
    	$captionElem.addClass('display-none');
    }
    else if($captionElem.hasClass('display-none'))
    {
    	$captionElem.removeClass('display-none');
    }
    
    if($('#formCode').val() == 'Main') { // remove DivCount and correct header
    	if(captionText == "") {
        	$captionElem.css('display','none');
    	} else {
        	$captionElem.css('display','block');
    	}
    }
}

/**
 * Disable datatableApi's buttons after loading. 
 * Note! - this function should cover the scenario in which the table is render after the authz. 
 *         the isGeneralDisabledStateForLateRender return true if this scenario happened (mostly when no permission on the page)
 *		   in Adama we only put the disabled page on all of the 
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
    var custid, selectedTable, struct = $('#' + domId + '_structCatalogItem').val();
    if (struct == 'InvItemBatch') {
        selectedTable = $('#' + domId).DataTable();
        custid = selectedTable.row('.selected').data();
        if (typeof custid !== 'undefined') {        	
            if (custid[2] == "1") {
                $('#' + domId + '_dataTableStructButtons button.dataTableApiMultiClone ').removeClass('disabledclass');
            } else {
                $('#' + domId + '_dataTableStructButtons button.dataTableApiMultiClone').addClass('disabledclass');
            }
        }
    } else if(struct == "WuFeedMaterialRef" || struct == "WuCryMixDefineRef" || struct == "WuDistStartMixRef"){
    	selectedTable = $('#' + domId).DataTable();
        custid = selectedTable.row('.selected').data();
        var iColumns = selectedTable.column(':contains(CREATEDBYUSER)').index();
    	
        if (typeof custid !== 'undefined') {
            if (custid[iColumns] == "0") {            	
            	// $('#' + domId + '_dataTableStructButtons
				// button.dataTableApiRemove ').removeClass('disabledclass');
            } else {
                $('#' + domId + '_dataTableStructButtons button.dataTableApiRemove').addClass('disabledclass');
            }
        }
        if(struct == "WuFeedMaterialRef" && $('#formCode').val() == 'WorkupFeeding' && $('#STAGE_ID option:selected').text() == 'Monitoring' ){
        	
     	   $('#materials_dataTableStructButtons button.dataTableApiEdit').removeClass('disabledclass');
     	}
         
    } 
}

/**
 * 
 * @param struct
 * @param parentTable
 * @param isParentSelected
 * @returns Available DataStructure
 */
function dataTableApiDataStructure(struct, parentTable, isParentSelected) {
    var parentStruct, availableDataStructureArray;
    var object = {
    	'SubProject': 'Project',
        'SubSubProject': 'SubProject',
        'Experiment': 'SubProject or SubSubProject or Request',
        'Request': 'SubProject or SubSubProject or Experiment or Step or Action or Self-Test or Workup',
        'Step': 'Experiment',
        'Action': 'Step',
        'SelfTest': 'Action',
// 'Workup': 'Action', -> display for testing (it should be only Workup)
        /*
		 * 'WorkupCrystallize': 'Action', 'WorkupDistillation': 'Action',
		 * 'WorkupDrying': 'Action', 'WorkupFeeding': 'Action',
		 * 'WorkupFiltration': 'Action', 'WorkupWashExtract': 'Action',
		 */
        'Workup': 'Action or Workup',
        'InvItemBatch': 'Material',
        
    };

    
    // handle first table (parentTable.length == 0) new click that is not
	// project (not allow)
    if(parentTable.length == 0 &&  $('#formCode').val() == 'Main' && struct != 'Project') {
        if (typeof object[struct] !== 'undefined') {
            return object[struct];
        } else {
            return '';
        }
    } else if (!$('#' + parentTable).is('table')) {
        if ($('#formCode').val() == 'Main') {
            if (typeof object[struct] !== 'undefined') {
                return object[struct];
            }
        }
        return '';
    }

    parentStruct = $('#' + parentTable + '_structCatalogItem').val().replace('InvItem', '');
    if (!isParentSelected) {
        if (typeof object[struct] === 'undefined') {
            return '';
        } else return object[struct];
    } else {
        if (typeof object[struct] !== 'undefined') {
            availableDataStructureArray = object[struct].split(' or ');
            if ($.inArray(parentStruct, availableDataStructureArray) == -1) {
                return object[struct];
            } else {
                return '';
            }
        } else {
            return '';
        }
    }
}

/**
 * show Extras by formCode when needed
 * 
 * @returns
 */
function showExtras(domId) {
    if ($('#formCode').val() == 'SelfTest') {
        if ($('#' + domId + '_structCatalogItem').val() == "AnalytMethodSelect") {
            // AnalytMethodSelect workaround:
            // because the role of the table (AnalytMethodSelect) is 'shared',
			// we dont see the criteria filter.
            $('#AnalytMethods_selectDiv').css('display', '');
            $('#AnalytMethods_criteriaCatalogItem').prop('disabled', false);
        }
    }
}

function generalBL_elementDataTableClickEvent(domId, customerFunction, params, $htmlObj)
{
	// TODO : get rid of getformDataNoCallBack() func inside
	
	console.log("customerFunction", customerFunction);
	console.log("params", params);
	
	var newFormCode ="";
	var tableType ="";
	var isPopup = false;
	var isShowWaitMsg = true;
	var eventActionName = "";
	var isStructAsPopup = false;
	var rowId ="";
	// get all data and add removeIndexId
	var allData = getformDataNoCallBack(1); 
	//yp 07122020 note: using parent to support also iframe step in formulation ->
	var dialogWidth = $(parent.window).width() - 100; 
	var dialogHeight = $(parent.window).height() - 100; 
	
	if(customerFunction == "createNewSample")
	{
		newFormCode = "Sample";
		eventActionName = "createNewForm";
		isPopup = true;
		isStructAsPopup = true;
	}
	else if(customerFunction == "createNewRequest")
	{
		newFormCode = "Request";
		eventActionName = "createNewForm";
	}
	else if(customerFunction == "createNewSelfTest")
	{
		newFormCode = "SelfTestMain";
		eventActionName = "createNewForm";
	}
	else if(customerFunction == "createNewSelfTestWithData")
	{
		newFormCode = "SelfTestMain";
		eventActionName = "createNewSelfTestWithData";
		
		allData = allData.concat({
			code : "parmCurrentStepID",
			val  :  $("#formId").val(),
		    type : "AJAX_BEAN",
			info : 'na'
		}); 
		
		allData = allData.concat({
			code : "parmDefaultSelfTestID",
			val  : params[2],
		    type : "AJAX_BEAN",
			info : 'na'
		}); 
		
		if(params.length > 3) { 
			allData = allData.concat({
				code : "parmActionSampleMessage",
				val  :  params[3],
			    type : "AJAX_BEAN",
				info : 'na'
			}); 
		}
	}
	else if(customerFunction == "showAlert")
	{
		isShowWaitMsg = false;
		displayAlertDialog(params[0]); 
		return;
	}
	else if(customerFunction == "createNewRequestWithData")
	{
		newFormCode = "Request";
		eventActionName = "createNewForm";
	}
	else if(customerFunction == "attachNewFile")
	{ 
		var overrideRowIdTableType = "documents";
		if(params != null && params.length > 2) {// 3rd param is the tabletype (optional, the default documents)
			overrideRowIdTableType = params[2];
		}
		newFormCode = "Document";
		tableType = overrideRowIdTableType;
		isPopup = true;
		isShowWaitMsg = false;
		eventActionName = "createNewForm";
	}
	else if(customerFunction == "addNewPreventive" ||customerFunction == "addNewBreakdown")
	{
		newFormCode = "InvItemMaintenance";
		eventActionName = "createNewForm";
	}
	else if(customerFunction == 'openAlternativeMaterials'){
		newFormCode = "AltMaterialSearch";
		eventActionName = "openSearchForm";
		isPopup = true;
		isStructAsPopup = true;
	}
	else if(customerFunction == 'openPurityList'){
		newFormCode = "PurityList";
		eventActionName = "openPopupForm";
		isPopup = true;
		isStructAsPopup = true;
		dialogWidth = $(parent.window).width() *0.7; 
		dialogHeight = $(parent.window).height() *0.7; 
	}
	else if(customerFunction == "deleteAction")
	{
		eventActionName = "deleteTableRow";
	}
	else if(customerFunction == "calcMaterial")
	{
		isShowWaitMsg = false;
		var _data = getDataFromEditableTable(["reactants","solvents","products"], -1);
		if(!(_data instanceof Object))
		{
			displayAlertDialog(_data + "\n Calculation was not performed.");
			return;
		}
		eventActionName = params[3];
		var colId = params[2];
		rowId = params[0];
		var cellId = domId+'_col_'+colId+'_row_'+rowId;
		var cellVal = $("#"+cellId).val(); 
		if(colId == "limitingagent")
		{
			cellVal = $("#"+cellId).prop('checked')?1:0;
		}

		var _obj = JSON.stringify({
				mainArg:colId,
				mainArgVal:cellVal,
				selectedFormId:rowId,
				data:_data
		});
		allData = allData.concat({
								code : eventActionName,
								val  : _obj,
							    type : "AJAX_BEAN",
								info : 'na'
							});
		
		allData = allData.concat({
			code : "parentId",
			val  :$('#formId').val(),
		    type : "AJAX_BEAN",
			info : 'na'
		});
	}
	else if(customerFunction == "onChangeRowType")
	{
		isShowWaitMsg = false;		
		eventActionName = "getRowTypeData";
		
		allData = allData.concat({
			code : "rowTypeName",
			val  : params[2],
		    type : "AJAX_BEAN",
			info : 'na'
		});
		allData = allData.concat({
			code : "form_id",
			val  : params[0],
		    type : "AJAX_BEAN",
			info : 'na'
		});
		allData = allData.concat({
			code : "PARENT_ID",
			val  :$('#formId').val(),
		    type : "AJAX_BEAN",
			info : 'na'
		});
	}

	else if(customerFunction == "onChangeFiller")
	{//this checkbox should always be single
		rowId = $htmlObj.attr('rowId');
		var name = $htmlObj.attr('name');
		var currCellId = $htmlObj.attr('id');
		var isChked = $htmlObj.prop('checked');

		if(isChked){
			var table = $('#'+domId).DataTable();
			var WW_P = getColumnIndexByColHeader(domId,"w/w%");
			var WW_GRK = getColumnIndexByColHeader(domId,"w/w [gr/Kg]");
			var WV_GRL = getColumnIndexByColHeader(domId,"w/v [gr/L]");
			var purity = getColumnIndexByColHeader(domId,"Purity %");
			table.rows().eq(0).each( function ( index ) 
			{//disables the calculated values of the filler
				cell = table.cell({row: index, column: WW_P}); 
			    node = cell.node();			    
			    $input = $(node).find('input');
			    if($input.attr('rowid')==rowId){
				    $input.attr('disabled',true);
				    cell = table.cell({row: index, column: WW_GRK}); 
				    node = cell.node();			    
				    $input = $(node).find('input');
				    $input.attr('disabled',true);
				    cell = table.cell({row: index, column: WV_GRL}); 
				    node = cell.node();			    
				    $input = $(node).find('input');
				    $input.attr('disabled',true);
				    cell = table.cell({row: index, column: purity}); 
				    node = cell.node();			    
				    $input = $(node).find('input');
				    $input.attr('disabled',true);
				    return;
			    }
			});
			doSingleCheck(domId,'Filler',currCellId,params,$htmlObj);//remove the checks from the other rows
			return;
		} else {
			var table = $('#'+domId).DataTable();
			var WW_P = getColumnIndexByColHeader(domId,"w/w%");
			var WW_GRK = getColumnIndexByColHeader(domId,"w/w [gr/Kg]");
			var WV_GRL = getColumnIndexByColHeader(domId,"w/v [gr/L]");
			var purity = getColumnIndexByColHeader(domId,"Purity %");
			table.rows().eq(0).each( function ( index ) 
			{//enables the calculated values of the unchecked filler
				cell = table.cell({row: index, column: WW_P}); 
			    node = cell.node();			    
			    $input = $(node).find('input');
			    if($input.attr('rowid')==rowId){
				    $input.attr('disabled',false);
				    cell = table.cell({row: index, column: WW_GRK}); 
				    node = cell.node();			    
				    $input = $(node).find('input');
				    $input.attr('disabled',false);
				    cell = table.cell({row: index, column: WV_GRL}); 
				    node = cell.node();			    
				    $input = $(node).find('input');
				    $input.attr('disabled',false);
				    cell = table.cell({row: index, column: purity}); 
				    node = cell.node();			    
				    $input = $(node).find('input');
				    $input.attr('disabled',false);
				    return;
			    }
			});
		}
	}
	else if(customerFunction == "calcComposition")
	{
		isShowWaitMsg = false;
		eventActionName = params[3];
		var colId = params[2];
		rowId = params[0];
		var cellId = domId+'_col_'+colId+'_row_'+rowId;
		var cellVal = $("#"+cellId).val(); 
		if(colId == "filler")
		{
			cellVal = $("#"+cellId).prop('checked')?1:0;
		}
		if(colId == 'density'){
			cellVal = $('#density').val();
		}
		if(colId == 'batchSize'){
			cellVal = $('#'+$('#'+domId).attr('batchSizeElement')).val();
		}

		var oldValue = $htmlObj!=undefined?$('#'+domId).attr('lastChangeVal'):"";
		var _obj = JSON.stringify({
				mainArg:colId,
				mainArgVal:cellVal,
				selectedFormId:rowId,
				data:"",
				mainArgLastVal:oldValue
		});
		allData = allData.concat({
								code : eventActionName,
								val  : _obj,
							    type : "AJAX_BEAN",
								info : 'na'
							});
		
		allData = allData.concat({
			code : "parentId",
			val  :$('#formId').val(),
		    type : "AJAX_BEAN",
			info : 'na'
		});
		
		allData = allData.concat({
			code : "batchSize",
			val  : $('#'+domId).attr('batchSizeElement')?$('#'+$('#'+domId).attr('batchSizeElement')).val():"",
		    type : "AJAX_BEAN",
			info : 'na'
		});
		
		allData = allData.concat({
			code : "plannedBatchSize",
			val  : parent.$('#plannedCompositions')&&parent.$('#plannedCompositions').attr('batchSizeElement')?parent.$('#'+parent.$('#plannedCompositions').attr('batchSizeElement')).val():"",
		    type : "AJAX_BEAN",
			info : 'na'
		});
		
		allData = allData.concat({
			code : "plannedBatchSizeUOM",
			val  : parent.$('#plannedCompositions')?parent.$('#BATCHSIZE_UOM').val():"",
		    type : "AJAX_BEAN",
			info : 'na'
		});
		
		allData = allData.concat({
			code : "tableType",
			val  : $('#'+domId+"_tableType").val(),
		    type : "AJAX_BEAN",
			info : 'na'
		});
		allData = allData.concat({
			code : "density",
			val  : parent.$('#density').val(),
		    type : "AJAX_BEAN",
			info : 'na'
		});
	}
	else if(customerFunction == "onChangeMaterial")
	{
		isShowWaitMsg = false;		
		eventActionName = "getMaterialData";
		
		allData = allData.concat({
			code : "invitemmaterial_id",
			val  : params[2],
		    type : "AJAX_BEAN",
			info : 'na'
		});
		allData = allData.concat({
			code : "form_id",
			val  : params[0],
		    type : "AJAX_BEAN",
			info : 'na'
		});
		allData = allData.concat({
			code : "PARENT_ID",
			val  :$('#formId').val(),
		    type : "AJAX_BEAN",
			info : 'na'
		});
		if($('#formCode').val()=='InvItemBatch'){
			allData = allData.concat({
				code : "SAMPLE_ID",
				val  :$('#SAMPLE_ID').val(),
			    type : "AJAX_BEAN",
				info : 'na'
			});	
		}
	}
	else if(customerFunction == "onChangeBatch")
	{
		isShowWaitMsg = false;
		if($('#formCode').val()=='ExperimentFor'){
			eventActionName = "getPurityAndCalcComposition";
			rowId = params[0];
			var colId= "batch_id";
			var cellId = domId+'_col_'+colId+'_row_'+rowId;
			var cellVal = $("#"+cellId).val(); 
			var oldValue = $('#'+domId).attr('lastChangeVal');
			
			var _obj = JSON.stringify({
					mainArg:colId,
					mainArgVal:cellVal,
					selectedFormId:rowId,
					data:"",
					mainArgLastVal:oldValue
			});
			allData = allData.concat({
									code : eventActionName,
									val  : _obj,
								    type : "AJAX_BEAN",
									info : 'na'
								});
			
			allData = allData.concat({
				code : "parentId",
				val  :$('#formId').val(),
			    type : "AJAX_BEAN",
				info : 'na'
			});
			
			allData = allData.concat({
				code : "batchSize",
				val  : $('#'+domId).attr('batchSizeElement')?$('#'+$('#'+domId).attr('batchSizeElement')).val():"",
			    type : "AJAX_BEAN",
				info : 'na'
			});
			
			allData = allData.concat({
				code : "plannedBatchSize",
				val  : parent.$('#plannedCompositions')&&parent.$('#plannedCompositions').attr('batchSizeElement')?parent.$('#'+parent.$('#plannedCompositions').attr('batchSizeElement')).val():"",
			    type : "AJAX_BEAN",
				info : 'na'
			});
			
			allData = allData.concat({
				code : "plannedBatchSizeUOM",
				val  : parent.$('#plannedCompositions')?parent.$('#BATCHSIZE_UOM').val():"",
			    type : "AJAX_BEAN",
				info : 'na'
			});
			
			allData = allData.concat({
				code : "tableType",
				val  : $('#'+domId+"_tableType").val(),
			    type : "AJAX_BEAN",
				info : 'na'
			});
			allData = allData.concat({
				code : "density",
				val  : parent.$('#density').val(),
			    type : "AJAX_BEAN",
				info : 'na'
			});
		} else {
			eventActionName = "getBatchData";
		}
			
		allData = allData.concat({
			code : "invitembatch_id",
			val  : params[2],
		    type : "AJAX_BEAN",
			info : 'na'
		});
		allData = allData.concat({
			code : "form_id",
			val  : params[0],
		    type : "AJAX_BEAN",
			info : 'na'
		});
		allData = allData.concat({
			code : "PARENT_ID",
			val  : $('#formId').val(),
		    type : "AJAX_BEAN",
			info : 'na'
		});
	}
	else if(customerFunction == "calculateRate")
	{
		eventActionName = "calcMaterialRates";
		allData = allData.concat({
			code : "PARENT_ID",
			val  :$('#formId').val(),
		    type : "AJAX_BEAN",
			info : 'na'
		});
	}
	
	if (isShowWaitMsg) {
		showWaitMessage(getSpringMessage('pleaseWait'));
	}
	
	
	var formId = params[0];
	var parentFormCode = params[1];
	var stringifyToPush = {
			code : "elementFormCode",
			val  : newFormCode,
		    type : "AJAX_BEAN",
			info : 'na'
		};


		allData = allData.concat(stringifyToPush);

		// url call
		var urlParam = "?formId=" + formId + "&formCode=" + parentFormCode + "&userId=" + $('#userId').val()
				+ "&eventAction="+eventActionName+"&isNew=" + $('#isNew').val();

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
						} 
						else if (obj.data[0].val != null)
						{
							if(obj.data[0].val.toString().indexOf(',') != '-1' && obj.data[0].val.toString().indexOf("WF") !== -1) 
							{
								doSaveMessage = obj.data[0].val.split(',')[1];
								var warningCode = obj.data[0].val.split(',')[0];
								displayAlertDialog("<\span><\i  onclick=\"customInfoClickEvent('getWFStatusInfo','STEPS_WF_LIST_INFO','"
										+ warningCode.split('_')[2]
										+ "')\""
										+ " style=\"cursor: pointer;margin-right: 5px;\" title=\"WF Info\" class=\"fa fa-info\"><\/i><\/span>\n"
										+ doSaveMessage.charAt(0).toUpperCase()
										+ doSaveMessage.slice(1));
								hideWaitMessage();
							}   
							else  if(eventActionName == "createNewForm")
							{
								var formCode = onNewButtonIntegration(newFormCode); // change formCode name
								// if needed
		                        var page = "./init.request?stateKey=" + parent.$('#stateKey').val() + "&formCode=" //yp 07122020 note: using parent to support also iframe step in formulation (for navigate beck to experiment formulation)
				                    + formCode + "&formId=-1" + "&userId="
			                    	+ $('#userId').val() + '&PARENT_ID='
			                    	+ obj.data[0].val+'&tableType='+tableType;
		                        if(newFormCode == "Document"){
		                        	page = page +'&defaultLinkAttachment=Attachment'
		                        }
//		                        else if(newFormCode == "SelfTestMain"){
//		                        	page = page +'&defaultType=Internal Analytical'
//		                        }
		                        if(customerFunction == "addNewPreventive"){
		                        	page = page +"&defaultType=Preventive";
		                        }else if(customerFunction == "addNewBreakdown"){
		                        	page = page +"&defaultType=Breakdown&formTab=BreakdownDetails";
		                        } else if(customerFunction == "createNewRequestWithData") {
		                        	customerFunction = "createNewRequest";
		                        	page = page +"&useDefaultData=1";
		                        } 
//		                        else if(customerFunction == "createNewSelfTestWithData") {
//		                        	customerFunction = "createNewSelfTest";
//		                        	page = page +"&useDefaultData=1";
//		                        }
								if (!isPopup) {
									//yp 07122020 note: using parent to support also iframe step in formulation 
									var func_ = function(){ parent.showWaitMessage(getSpringMessage('pleaseWait')); parent.window.location = page};
									if (parent.prop.dataChanged) {
										hideWaitMessage();
										parent.openConfirmDialog({
								            onConfirm: func_,
								            title: 'Warning',
								            message: getSpringMessage('confirmWithOutSaveMessage')
								        });
									} else {
										parent.window.location = page
									}
				                    return;
				                }
								if(isStructAsPopup){
									page = page +"&isStructAsPopup=1";
								}
								//yp 07122020 note: using parent to support also iframe step in formulation 
								 var $dialog = parent.$('<div id="prevDialog" style="overflow-y: hidden;""></div>')
				                    .html('<iframe style="border: 0px;width:100%;height:100%" src="' + page + '"></iframe>')
				                    .dialog({
				                        autoOpen: false,
				                        modal: true,
				                        height: dialogHeight,
				                        width: dialogWidth,
				                        //  title: title,
				                        close: function () {
				                            if(customerFunction == "createNewSample"){
				                            	var pass_labelCode = parent.$(this).data('_pass_labelCode');
			                            		var pass_labelData = parent.$(this).data('_pass_labelData');
				                            	if(_pass_labelCode !=undefined && _pass_labelCode !="" && pass_labelData !=undefined && pass_labelData !=""){
				                            		parent.outPutLabel('_global',pass_labelCode, pass_labelData);
				                                }
				                            	parent.$('#prevDialog iframe').attr('src', 'about:blank');
				                            	parent.$('#prevDialog').remove();
				                            	onElementDataTableApiChange(domId);
					                            parent.onElementDataTableApiChange('samples');
				                            }else{
				                            	parent.$('#prevDialog iframe').attr('src', 'about:blank');
				                            	parent.$('#prevDialog').remove();
				                            	onElementDataTableApiChange(domId);
				                            }
				                        }
				                    });
	
				                $dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
							}
							else  if(eventActionName == "deleteTableRow")
							{
								onElementDataTableApiChange(domId);
							}
							else if(customerFunction == "calcMaterial")
							{
								console.log("calcMaterial return data",obj.data[0].val);
								updateEditableTableData(domId, obj.data[0].val);
								
								/* display warning message */
								var warningToDisplay = "";
								var fullObj = funcParseJSONData(obj.data[0].val); 
								if(Object.keys(fullObj).length > 0)
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
									}
								}
								/* ******** */
							}
							else if(customerFunction == "calcComposition")
							{
								console.log("calcComposition return data",obj.data[0].val);
								updateEditableTableData(domId, obj.data[0].val);
								if(eventActionName=='calcBatchSizeOnChange'){
									var stepIframeList = $('[id*="AsyncIframe_stepIframes_"]');
									for(var i=0;i<stepIframeList.length;i++){
										var stepId = stepIframeList[i].id;
										var iframe = stepIframeList[i];//document.getElementById('AsyncIframe_stepIframes_'+stepId);
										iframe.contentWindow.onElementDataTableApiChange('compositions');
										iframe.contentWindow.onElementDataTableApiChange('products');
									}
									onElementDataTableApiChange(domId);
								}
								else if(eventActionName=='calcDensityOnChange'){
									var stepIframeList = $('[id*="AsyncIframe_stepIframes_"]');
									for(var i=0;i<stepIframeList.length;i++){
										var stepId = stepIframeList[i].id;
										var iframe = stepIframeList[i];//document.getElementById('AsyncIframe_stepIframes_'+stepId);
										iframe.contentWindow.updateEditableTableData('compositions', obj.data[0].val);
										iframe.contentWindow.updateEditableTableData('products', obj.data[0].val);
									}
								} else if(eventActionName=="calcActualOnChange"){
									parent.updateEditableTableData('plannedCompositions', obj.data[0].val);
								} else{
									if($('#formCode').val()=='StepMinFr'){
										parent.updateEditableTableData('plannedCompositions', obj.data[0].val)
										var stepIframeList = parent.$('[id*="AsyncIframe_stepIframes_"]');
										for(var i=0;i<stepIframeList.length;i++){
											var stepId = stepIframeList[i].id;
											var iframe = stepIframeList[i];//document.getElementById('AsyncIframe_stepIframes_'+stepId);
											iframe.contentWindow.updateEditableTableData('compositions', obj.data[0].val);
											iframe.contentWindow.updateEditableTableData('products', obj.data[0].val);
										}
									}
								}
								//fixed bug 9150
								$('#summaryLine').remove();
								addSummaryLine(domId);
								/* display warning message */
								var warningToDisplay = "";
								var fullObj = funcParseJSONData(obj.data[0].val);
								if(Object.keys(fullObj).length > 0)
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
										if(eventActionName=='calcDensityOnChange'){
											$('#density').val($('#'+domId).attr('lastChangeVal'));
											$('#density').attr('oldvalue',$('#'+domId).attr('lastChangeVal'));
										}
										else if(eventActionName=='calcFillerOnChange'){
											if($htmlObj !=undefined){
											$htmlObj.prop("checked",$('#'+domId).attr('lastChangeVal')=="0"?false:true);
												var table = $('#'+domId).DataTable();
												var WW_P = getColumnIndexByColHeader(domId,"w/w%");
												var WW_GRK = getColumnIndexByColHeader(domId,"w/w [gr/Kg]");
												var WV_GRL = getColumnIndexByColHeader(domId,"w/v [gr/L]");
												var purity = getColumnIndexByColHeader(domId,"Purity %");
												table.rows().eq(0).each( function ( index ) 
												{//disables the calculated values of the filler
													cell = table.cell({row: index, column: WW_P}); 
												    node = cell.node();			    
												    $input = $(node).find('input');
												    if($input.attr('rowid')==rowId){
													    $input.attr('disabled',false);
													    cell = table.cell({row: index, column: WW_GRK}); 
													    node = cell.node();			    
													    $input = $(node).find('input');
													    $input.attr('disabled',false);
													    cell = table.cell({row: index, column: WV_GRL}); 
													    node = cell.node();			    
													    $input = $(node).find('input');
													    $input.attr('disabled',false);
													    cell = table.cell({row: index, column: purity}); 
													    node = cell.node();			    
													    $input = $(node).find('input');
													    $input.attr('disabled',false);
													    return;
												    }
												});
											}
										}
									} else {//if there was no warning message that indicating that the calculation went wrong then paint the main argument
										if(eventActionName=='calcFillerOnChange'){
											var table = $('#'+domId).DataTable();
											var WW_P = getColumnIndexByColHeader(domId,"w/w%");
											table.rows().eq(0).each( function ( index ) 
											{
												cell = table.cell({row: index, column: WW_P}); 
											    node = cell.node();			    
											    $input = $(node).find('input');
											    if($input.attr('rowid')==rowId){
											    	$htmlObj = $input;
											    	return;
											    }
											});
										}
										if($htmlObj!=undefined 
												&& eventActionName!='calcDensityOnChange' && eventActionName!='calcBatchSizeOnChange'
													&& eventActionName!='calcActualOnChange'){
											var table = $('#'+domId).DataTable();
											var WW_P = getColumnIndexByColHeader(domId,"w/w%");
											var WW_GRK = getColumnIndexByColHeader(domId,"w/w [gr/Kg]");
											var WV_GRL = getColumnIndexByColHeader(domId,"w/v [gr/L]");
											table.rows().eq(0).each( function ( index ) 
											{//init the style of all the columns to the basic one
												cell = table.cell({row: index, column: WW_P}); 
											    node = cell.node();			    
											    $input = $(node).find('input');
											    if($input.attr('rowid')==rowId){
											    	if(eventActionName!='calcPurityOnChange'){
											    		$input.css('font-weight','').css('background', '').css('height','');
											    	} else {//when the purity has been changed ->the main argument should change to w/w%
											    		$input.css('font-weight','bold').css('background', 'lightgreen').css('height','100%');
											    	}
												    cell = table.cell({row: index, column: WW_GRK}); 
												    node = cell.node();			    
												    $input = $(node).find('input');
												    $input.css('font-weight','').css('background', '').css('height','');
												    cell = table.cell({row: index, column: WV_GRL}); 
												    node = cell.node();			    
												    $input = $(node).find('input');
												    $input.css('font-weight','').css('background', '').css('height','');
												    return;
											    }
											});
											if(eventActionName!='calcFillerOnChange'&&eventActionName!='calcPurityOnChange'){
												//after returning the basic style, now bolding the main argument column
											    $htmlObj.css('font-weight','bold').css('background', 'lightgreen').css('height','100%');//important! if the color is changed here, then should be changed also in the view fg_S_composition_dte_v
											}
										}
									}
								}
								/* ******** */
							}
							else if(customerFunction == "onChangeRowType"){
								console.log("onChangeRowType return data",obj.data[0].val);
								onRowTypeChangeUpdateRowEditTable(domId, $.parseJSON(obj.data[0].val),$htmlObj);
							}
							else if(customerFunction == "onChangeMaterial")
							{
								console.log("onChangeMaterial return data",obj.data[0].val);
								onMaterialChangeUpdateRowEditTable(domId, $.parseJSON(obj.data[0].val));
							}
							else if(customerFunction == "onChangeBatch")
							{
								console.log("onChangeBatch return data",obj.data[0].val);
								if(domId == 'plannedCompositions'){
									/* display warning message */
									var warningToDisplay = "";
									var fullObj = funcParseJSONData(obj.data[0].val); 
									if(Object.keys(fullObj).length > 0)
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
										}
									}
									onElementDataTableApiChange('plannedCompositions');
								} else {
									onBatchChangeUpdateRowEditTable(domId, obj.data[0].val);
								}
							}
							else if(customerFunction == "createNewSelfTestWithData")
							{
								onElementDataTableApiChange("action");
							}
							else if(customerFunction == "calculateRate")
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
							else if(eventActionName == "openSearchForm" || eventActionName == "openPopupForm"){
								var formCode = newFormCode; // change formCode name
								var colId = params[2];
								 rowId = params[0];
								
		                        var page = "./init.request?stateKey=" + parent.$('#stateKey').val() + "&formCode=" //yp 07122020 note: using parent to support also iframe step in formulation (for navigate beck to experiment formulation)
				                    + formCode + "&formId=-1" + "&userId="
			                    	+ $('#userId').val() + '&tableType='+tableType;
		                        if(newFormCode == "AltMaterialSearch"){
		                        	if(colId == ''){
										var table = $('#'+domId).DataTable();
										var materialIndex = getColumnIndexByColHeader(domId,"Material");
	                                    table.rows().eq(0).each( function ( index ) 
										{//init the style of all the columns o the basic one
											var cell = table.cell({row: index, column: materialIndex}); 
										    var node = cell.node();			    
										    var $select = $(node).find('select');
										    if($select.attr('rowid')==rowId){
										    	colId = $select.val();
										    	return;
										    }
										});
									}
		                        	page = page +'&parentId='+colId;
		                        } else if(newFormCode == "PurityList"){
		                        	page = page +'&parentId='+rowId;
		                        }
								 var $dialog = parent.$('<div id="prevDialog" style="overflow-y: hidden;""></div>')
				                    .html('<iframe style="border: 0px;width:100%;height:100%" src="' + page + '"></iframe>')
				                    .dialog({
				                        autoOpen: false,
				                        modal: true,
				                        height: dialogHeight,
				                        width: dialogWidth,
				                        //  title: title,
				                        close: function () {
				                        	if (eventActionName == "openSearchForm"){
				                            	var iframeContents = $(this).find('iframe').contents();
				                                if (iframeContents.find('#save_').attr('flag')) { // detect if save button was clicked.
				                                    toReturn = JSON.parse(iframeContents.find('#toReturn').val());
				                                    var table = $('#'+domId).DataTable();
						                            if(customerFunction == "openAlternativeMaterials"){
					                                    var materialIndex = getColumnIndexByColHeader(domId,"Material");
														var alternativeIndex = getColumnIndexByColHeader(domId,"Alternative");
					                                    table.rows().eq(0).each( function ( index ) 
														{//init the style of all the columns o the basic one
															var cell = table.cell({row: index, column: materialIndex}); 
														    var node = cell.node();			    
														    var $select = $(node).find('select');
														    if($select.attr('rowid')==rowId){
															    cell = table.cell({row: index, column: alternativeIndex}); 
															    node = cell.node();	
															    var $altInput = $(node).find('div[rowId = "'+rowId+'"]');
														    	var originVal = $select.find('option:selected').text();
														    	//insert the origin data into the alternative in the DB
														    	var allData = [
																	{code:"formId",val:$('#formId').val()}, 
																	{code:"formCode",val:"Composition"},
																	{code:"parentFormCode",val:$('#formCode').val()},
																	{code:"userId",val:$('#userId').val()},
																	{code:"formNumberID",val:""},
																	{code:"saveType",val:""},
																	{code:"onChangeFormId",val:rowId},
																	{code:"onChangecolumnName",val:"alternative"},
																	{code:"onChangecolumnVal",val:originVal},
																	{code:"oldVal","val":'1'}
																];	
																var data_ = JSON.stringify({
																	action : "onChangeDataTableCell",
																	data : allData,
																	errorMsg : ""
																});	
																console.log("onChangeDataTableCell AJAX call data_", data_);
															
																$.ajax({
																	type : 'POST',
																	data : data_,
																	url : "./onChangeDataTableCell.request?stateKey=" + $('#stateKey').val(),
																	contentType : 'application/json',
																	dataType : 'json',
																	success : function(obj) {
															
																		if (obj.errorMsg != null && obj.errorMsg != '') 
																		{
																			displayAlertDialog(obj.errorMsg);
																			console.log("---ERROR onChangeDataTableCell on SUCCESS :",obj.errorMsg);
																			//return;
																		} 
																		else 
																		{
																			$altInput.text(originVal);
																	    	$select.val(toReturn['MATERIAL_ID']).trigger('chosen:updated');	    
																	    	$select.change();
																		   // return;
																		}
																		hideWaitMessage();
																	},
																	error : handleAjaxError
																});	
														    }		
														});
														
					                                }
					                            	/*parent.$('#prevDialog iframe').attr('src', 'about:blank');
					                            	parent.$('#prevDialog').remove();*/
					                            	
					                            }
				                                $(this).find('iframe').attr('src', 'about:blank');
					                            $(this).remove();
				                        	} else if(eventActionName == "openPopupForm"){
				                            	parent.$('#prevDialog iframe').attr('src', 'about:blank');
				                            	parent.$('#prevDialog').remove();
				                        		onElementDataTableApiChange(domId);
				                        	}
			                            	//onElementDataTableApiChange(domId);
				                            //parent.onElementDataTableApiChange('samples');
				                        }
				                    });
	
				                $dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
							}
							hideWaitMessage();
						}
						else
						{
							console.log("return data val is null",obj);
						}
					},
					error : handleAjaxError
				});

}


function generalBL_DTRowClickEvent(domId, rowDataArr, paramsObj)
{
	console.log("domId", domId);
	console.log("rowData", rowDataArr);
	if(domId == "materialDuplicatesTable")
	{
		var structID = "-1";
		if(rowDataArr !== undefined && rowDataArr.length > 0 )
		{
			structID = rowDataArr[1];
		}
		else
		{
			structID = "-1";
		}
		chemDoodleBL_chemDoodleCanvasUpdate(structID);
	}
	else if($('#formCode').val() == "ExperimentCP" && domId == "expRunPlanningTable") {
		if(rowDataArr !== undefined && rowDataArr.length > 0 )
		{
			/*var expHasStartedRuns = rowDataArr[2];
			if(expHasStartedRuns == "0")
			{
				checkRunStepsStatus();
			}*/
		}
	}
}

function topRowsToDisplay_BL(domId,displayTopRows){
	 if ($('#formCode').val() == 'SearchReport'){
		 if(displayTopRows !="-2"){//the table is not empty
			 $('#' + domId + '_displayTopRows').html('');
			 $('#' + domId + '_displayTopRows').prepend('<label class="cssStaticData">Top '+displayTopRows+' Results</label>');
		     $('#' + domId + '_displayTopRows').css('display','block');
		     displayAlertDialog(getSpringMessage('searchNotspecified'));
		     }/*else{
		    	//displayAlertDialog(getSpringMessage('noResultFound'));
		     }*/
	 }else{
		 if(displayTopRows !="-2"){//the table is not empty
			 $('#' + domId + '_displayTopRows').html('');
			 $('#' + domId + '_displayTopRows').prepend('<label class="cssStaticData">Top '+displayTopRows+' Results</label>');
			 $('#' + domId + '_displayTopRows').css('display','block');		
		 }
	 }
}

function doClearWhenEmpty(domId,formCode){
	if(domId == 'experiments' && formCode == 'Project'
		|| domId == 'experiments' && formCode == 'SubProject'){
		return false;
	}
	return true;
}

function onRowTypeChangeUpdateRowEditTable(domId, data,$htmlObj)
{	
	
	try 
	{
		var dataObj = data; 
		console.log(dataObj);
		if(Object.keys(dataObj).length > 0)
		{					
			var table = $('#'+domId).DataTable();
			table.rows().eq(0).each( function ( index ) 
			{
				var row = table.row( index );	 
			    var rowData = row.data();
			    var rowID = rowData[0];
			    var updatedRowDataObj = [];
	
		    	if(dataObj.hasOwnProperty(rowID))
				{
		    		updatedRowDataObj = dataObj[rowID];
		    		
		    		var dataLength = rowData.length;
				    for(var i=0;i<dataLength;i++)
				    {	    				    	
				    	var cell = table.cell({row: index, column: i});	 
					    var cdata = cell.data();
					    var cnode = cell.node();
					    var cellObj = {};
					    if (checkIfJSON(cdata)) // check if json or not
					    { 
					    	cellObj = funcParseJSONData(cdata,true);					    	
					    	if(cellObj.hasOwnProperty("hiddenRowData"))
		    				{
		    					var _tObj = cellObj["hiddenRowData"];
		    					var _nObj = {};
		    					for(var key in _tObj)
		    					{
		    						if(updatedRowDataObj.hasOwnProperty(key))   
		    						{
		    							_nObj[key] = updatedRowDataObj[key];
		    						}
		    						else
		    						{
		    							_nObj[key] = _tObj[key];
		    						}
		    					}
		    					cellObj["hiddenRowData"] = _nObj;
		    					
		    					cell.data(JSON.stringify(cellObj)).draw(false);
		    					continue;
		    				}
					    	var colName = cellObj.dbColumnName;
					    	if(colName == "invitemmaterial_id" && updatedRowDataObj.hasOwnProperty("material_list"))
					    	{
					    		var _newVal = updatedRowDataObj["material_list"];
					    		var $select = $(cnode).find('select');
					    		if(_newVal instanceof Object)
				    			{					    			
					    			if($select.length > 0)
					    			{
					    				$select.attr('formCode',_newVal.formCode);
					    				$select.html('<option value="0"></>').trigger('chosen:updated');
					    				var selectOptionList = '<option value="0"></>';
					    				var selectedOption = _newVal.displayName;					    
					    				$(_newVal.fullList).each(function ()  
	                        			{
	                        				if(this.ID != "")
	                        				{
	                        					var selected = "";
	                        					if(selectedOption.length > 0 && selectedOption[0].ID && selectedOption[0].ID == this.ID)
	                        					{
	                        						selected = 'selected = "selected"';
	                        					}
	                        					var tooltipOption="";
	                        					if (typeof this.TOOLTIP === "undefined"){
	                        						tooltipOption = dtExt_htmlEscapeEntities(this.VAL);
	                        					}else{
	                        						try {
	                        							tooltipOption = dtExt_htmlEscapeEntities(this.TOOLTIP);
													} catch (e) {
														tooltipOption = "NA";
													}
	                        					}
	                        					selectOptionList = selectOptionList + '<option title="'+tooltipOption+'" '+selected+' value="'+this.ID+'">'+this.VAL+'</>';			                        				
	                        				}	                        				
	                        			});
					    				$select.append(selectOptionList).trigger('chosen:updated');	
					    				if($select[0].hasAttribute('isLink') && $select.prop('selectedIndex') > 0) 
					    	    	    {
					    	    	    	var attr = [''+$select.val()+'' ,''+$select.attr('formCode')+'','', true];
					    	    	    	$($select.next('div.chosen-container')).find('span')
					    	    	    		.addClass('linkElement')
					    	    	    	.off('click')
					    	    	    	.on('click', function(){
					    	    	    		checkAndNavigate(attr);
					    	    	    	});
					    	    	    } else {
					    	    	    	$($select.next('div.chosen-container')).find('span')
			    	    	    			.removeClass('linkElement')
			    	    	    			.off('click');
					    	    	    }
					    				//$select.change();
					    			}
				    			}
					    		else
				    			{
					    			if($select.length > 0)
					    			{
					    				$select.attr('formCode',_newVal.formCode);
					    				$select.html('<option value="0"></>').trigger('chosen:updated');
					    				$($select.next('div.chosen-container')).find('span')
			    	    	    			.removeClass('linkElement')
			    	    	    			.off('click');
					    			}
				    			}
					    		//important: update column filter with new data
			    				var updateCellFilterValue = $(cnode).find('select >option:selected').text();
					    		dtExt_updateCellFilterData(domId, index, i, updateCellFilterValue);
					    	} 
					    	else if(colName == "batch_id" && updatedRowDataObj.hasOwnProperty("batch_list")
					    			|| colName == "FUNCTION_ID" && updatedRowDataObj.hasOwnProperty("function_list"))
					    	{
					    		var _newVal = colName == "batch_id"?updatedRowDataObj["batch_list"]:updatedRowDataObj["function_list"];
					    		var $select = $(cnode).find('select');
					    		if(_newVal instanceof Object)
				    			{					    			
					    			if($select.length > 0)
					    			{
					    				$select.html('<option value="0"></>').trigger('chosen:updated');
					    				var selectOptionList = '<option value="0"></>';
					    				var selectedOption = _newVal.displayName;					    
					    				$(_newVal.fullList).each(function ()  
	                        			{
	                        				if(this.ID != "")
	                        				{
	                        					var selected = "";
	                        					if(selectedOption.length > 0 && selectedOption[0].ID && selectedOption[0].ID == this.ID)
	                        					{
	                        						selected = 'selected = "selected"';
	                        					}
	                        					var tooltipOption="";
	                        					if (typeof this.TOOLTIP === "undefined"){
	                        						tooltipOption = dtExt_htmlEscapeEntities(this.VAL);
	                        					}else{
	                        						try {
	                        							tooltipOption = dtExt_htmlEscapeEntities(this.TOOLTIP);
													} catch (e) {
														tooltipOption = "NA";
													}
	                        					}
	                        					selectOptionList = selectOptionList + '<option title="'+tooltipOption+'" '+selected+' value="'+this.ID+'">'+this.VAL+'</>';			                        				
	                        				}	                        				
	                        			});
					    				$select.append(selectOptionList).trigger('chosen:updated');	
					    				if($select[0].hasAttribute('isLink') && $select.prop('selectedIndex') > 0) 
					    	    	    {
					    	    	    	var attr = [''+$select.val()+'' ,''+$select.attr('formCode')+'','', true];
					    	    	    	$($select.next('div.chosen-container')).find('span')
					    	    	    		.addClass('linkElement')
					    	    	    	.off('click')
					    	    	    	.on('click', function(){
					    	    	    		checkAndNavigate(attr);
					    	    	    	});
					    	    	    }
					    			}
				    			}
					    		else
				    			{
					    			if($select.length > 0)
					    			{
					    				$select.html('<option value="0"></>').trigger('chosen:updated');
					    				$($select.next('div.chosen-container')).find('span')
			    	    	    			.removeClass('linkElement')
			    	    	    			.off('click');
					    			}
				    			}
					    		//important: update column filter with new data
			    				var updateCellFilterValue = $(cnode).find('select >option:selected').text();
					    		dtExt_updateCellFilterData(domId, index, i, updateCellFilterValue);
					    	} 
					    	else if(updatedRowDataObj.hasOwnProperty(colName))
					    	{
					    		var _newVal = updatedRowDataObj[colName];
					    		if($(cnode).find('select').length > 0)
					    		{
					    			$(cnode).find('select').val(_newVal).trigger('chosen:updated');
					    		}
					    		else if($(cnode).find('input').length > 0)
					    		{
					    			var $input = $(cnode).find('input');
					    			if($input.attr('type') == "number")
					    			{
					    				$input.attr('originalValue',_newVal);
					    				$input.attr('title',_newVal);
					    				$input.val(_newVal);
					    			}
					    			else
					    			{
					    				$input.val(_newVal);
					    			}	
					    		}
					    		else
					    		{					    			
					    			$(cnode).text(_newVal);
					    		}
					    		//important: update column filter with new data
					    		dtExt_updateCellFilterData(domId, index, i, _newVal);
					    	}
			            }
				    }
				}
			});			
		}
		
		//disables the function column in case the row type is co-formulant in the planned composition table
		var table = $('#'+domId).DataTable();
		var tableType = $('#'+domId+"_tableType").val();
		var rowId = $htmlObj.attr('rowId');
		var selectedVal = $.trim($htmlObj.find('option:selected').val());
		if(tableType == 'expComposition')
		{
			var funcColumn = getColumnIndexByColHeader(domId,"Function");
			var alternative = getColumnIndexByColHeader(domId,"Alternative");
			if(selectedVal == 'Co-Formulants'){
				table.rows().eq(0).each( function ( index ) 
				{//enables the function selection when the row type is co-forulant only
					cell = table.cell({row: index, column: funcColumn}); 
				    node = cell.node();			    
				    var $select = $(node).find('select');
				    cell = table.cell({row: index, column: alternative}); 
				    node = cell.node();	
				    var $input = $(node).find('i');
				    if($select.attr('rowid')==rowId){
				    	$select.prop('disabled',false).trigger('chosen:updated');
				    	$input.css('display','initial');
					    return;
				    }
				});
			} else {
				table.rows().eq(0).each( function ( index ) 
				{
					cell = table.cell({row: index, column: funcColumn}); 
				    node = cell.node();			    
				    $select = $(node).find('select');
				    cell = table.cell({row: index, column: alternative}); 
				    node = cell.node();	
				    var $input = $(node).find('i');
				    if($select.attr('rowid')==rowId){
				    	$select.prop('disabled',true).trigger('chosen:updated');
				    	$input.css('display','none')
					    return;
				    }
				});
			}
		}
		enableCompositionDetailsButton(domId);
	}
	catch(e)
	{
		console.log("error in onRowTypeChangeUpdateRowEditTable() for domId", domId)
		console.log("error in onRowTypeChangeUpdateRowEditTable() error", e);
	}
}

function onMaterialChangeUpdateRowEditTable(domId, data)
{	
	
	try 
	{
		var dataObj = data; 
		console.log(dataObj);
		if(Object.keys(dataObj).length > 0)
		{					
			var table = $('#'+domId).DataTable();
			table.rows().eq(0).each( function ( index ) 
			{
				var row = table.row( index );	 
			    var rowData = row.data();
			    var rowID = rowData[0];
			    var updatedRowDataObj = [];
	
		    	if(dataObj.hasOwnProperty(rowID))
				{
		    		updatedRowDataObj = dataObj[rowID];
		    		
		    		var dataLength = rowData.length;
				    for(var i=0;i<dataLength;i++)
				    {	    				    	
				    	var cell = table.cell({row: index, column: i});	 
					    var cdata = cell.data();
					    var cnode = cell.node();
					    var cellObj = {};
					    if (checkIfJSON(cdata)) // check if json or not
					    { 
					    	cellObj = funcParseJSONData(cdata,true);					    	
					    	if(cellObj.hasOwnProperty("hiddenRowData"))
		    				{
		    					var _tObj = cellObj["hiddenRowData"];
		    					var _nObj = {};
		    					for(var key in _tObj)
		    					{
		    						if(updatedRowDataObj.hasOwnProperty(key))   
		    						{
		    							_nObj[key] = updatedRowDataObj[key];
		    						}
		    						else
		    						{
		    							_nObj[key] = _tObj[key];
		    						}
		    					}
		    					cellObj["hiddenRowData"] = _nObj;
		    					
		    					cell.data(JSON.stringify(cellObj)).draw(false);
		    					continue;
		    				}
					    	
					    	var colName = cellObj.dbColumnName;
					    	if(colName == "MSDS" && updatedRowDataObj.hasOwnProperty("MSDS"))
					    	{
					    		var _newVal = updatedRowDataObj["MSDS"];
					    		
					    		if(_newVal instanceof Object)
				    			{
					    			var fileHtml = "";
					    			
					    			if(_newVal.fileId || _newVal.formId)
					    			{
						    			if(_newVal.fileId == null || $.trim(_newVal.fileId) == '' || $.trim(_newVal.fileId) == "")
						    			{
						    				fileHtml = '<a onclick="checkAndNavigate([\'' + _newVal.formId + '\',\'' + _newVal.formCode + '\',\'' + _newVal.tab + '\'])">'
			    							+'<span style="margin:0px;">'
			    							+'<i style="cursor: pointer;color: #2779aa;font-size: 13pt;" class="'+_newVal.icon+'">'
			    							+'</i></span></a>';
						    			}
						    			else
						    			{
						    				fileHtml = '<a onclick="smartFile(\'' + domId + '\',\'' + _newVal.fileId + '\')">'
						    							+'<span style="margin:0px;">'
						    							+'<i style="cursor: pointer;color: #2779aa;font-size: 13pt;" class="'+_newVal.icon+'">'
						    							+'</i></span></a>';
						    			}
					    			}
					    			$(cnode).html(fileHtml);
				    			}
					    		else
					    		{
					    			$(cnode).html("");
					    		}
					    	}
					    	else if(colName == "batch_id" && updatedRowDataObj.hasOwnProperty("batch_list")
					    			|| colName == "FUNCTION_ID" && updatedRowDataObj.hasOwnProperty("function_list"))
					    	{
					    		var _newVal = colName == "batch_id"?updatedRowDataObj["batch_list"]:updatedRowDataObj["function_list"];
					    		var $select = $(cnode).find('select');
					    		if(_newVal instanceof Object)
				    			{					    			
					    			if($select.length > 0)
					    			{
					    				$select.html('<option value="0"></>').trigger('chosen:updated');
					    				var selectOptionList = '<option value="0"></>';
					    				var selectedOption = _newVal.displayName;					    
					    				$(_newVal.fullList).each(function ()  
	                        			{
	                        				if(this.ID != "")
	                        				{
	                        					var selected = "";
	                        					if(selectedOption.length > 0 && selectedOption[0].ID && selectedOption[0].ID == this.ID)
	                        					{
	                        						selected = 'selected = "selected"';
	                        					}
	                        					var tooltipOption="";
	                        					if (typeof this.TOOLTIP === "undefined"){
	                        						tooltipOption = dtExt_htmlEscapeEntities(this.VAL);
	                        					}else{
	                        						try {
	                        							tooltipOption = dtExt_htmlEscapeEntities(this.TOOLTIP);
													} catch (e) {
														tooltipOption = "NA";
													}
	                        					}
	                        					selectOptionList = selectOptionList + '<option title="'+tooltipOption+'" '+selected+' value="'+this.ID+'">'+this.VAL+'</>';			                        				
	                        				}	                        				
	                        			});
					    				$select.append(selectOptionList).trigger('chosen:updated');	
					    				if($select[0].hasAttribute('isLink') && $select.prop('selectedIndex') > 0) 
					    	    	    {
					    	    	    	var attr = [''+$select.val()+'' ,''+$select.attr('formCode')+'','', true];
					    	    	    	$($select.next('div.chosen-container')).find('span')
					    	    	    		.addClass('linkElement')
					    	    	    	.off('click')
					    	    	    	.on('click', function(){
					    	    	    		checkAndNavigate(attr);
					    	    	    	});
					    	    	    } else {
						    				$($select.next('div.chosen-container')).find('span')
				    	    	    			.removeClass('linkElement')
				    	    	    			.off('click');
					    	    	    }
					    				$select.change();
					    			}
				    			}
					    		else
				    			{
					    			if($select.length > 0)
					    			{
					    				$select.html('<option value="0"></>').trigger('chosen:updated');
					    				$($select.next('div.chosen-container')).find('span')
			    	    	    			.removeClass('linkElement')
			    	    	    			.off('click');
					    			}
				    			}
					    		//important: update column filter with new data
			    				var updateCellFilterValue = $(cnode).find('select >option:selected').text();
					    		dtExt_updateCellFilterData(domId, index, i, updateCellFilterValue);
					    	}
					    	else if(colName == "SAMPLE_ID" && updatedRowDataObj.hasOwnProperty("sample_list"))
					    	{
					    		var _newVal = updatedRowDataObj["sample_list"];
					    		var $select = $(cnode).find('select');
					    		if(_newVal instanceof Object)
				    			{					    			
					    			if($select.length > 0)
					    			{
					    				$select.html('<option value="0"></>').trigger('chosen:updated');
					    				var selectOptionList = '<option value="0"></>';
					    				var selectedOption = _newVal.displayName;					    
					    				$(_newVal.fullList).each(function ()  
	                        			{
	                        				if(this.ID != "")
	                        				{
	                        					var selected = "";
	                        					if(selectedOption.length > 0 && selectedOption[0].ID && selectedOption[0].ID == this.ID)
	                        					{
	                        						selected = 'selected = "selected"';
	                        					}
	                        					var tooltipOption="";
	                        					if (typeof this.TOOLTIP === "undefined"){
	                        						tooltipOption = dtExt_htmlEscapeEntities(this.VAL);
	                        					}else{
	                        						try {
	                        							tooltipOption = dtExt_htmlEscapeEntities(this.TOOLTIP);
													} catch (e) {
														tooltipOption = "NA";
													}
	                        					}
	                        					selectOptionList = selectOptionList + '<option title="'+tooltipOption+'" '+selected+' value="'+this.ID+'">'+this.VAL+'</>';			                        				
	                        				}	                        				
	                        			});
					    				$select.append(selectOptionList).trigger('chosen:updated');	
					    				if($select[0].hasAttribute('isLink') && $select.prop('selectedIndex') > 0) 
					    	    	    {
					    	    	    	var attr = [''+$select.val()+'' ,''+$select.attr('formCode')+'','', true];
					    	    	    	$($select.next('div.chosen-container')).find('span')
					    	    	    		.addClass('linkElement')
					    	    	    	.off('click')
					    	    	    	.on('click', function(){
					    	    	    		checkAndNavigate(attr);
					    	    	    	});
					    	    	    }
					    			}
				    			}
					    		else
				    			{
					    			if($select.length > 0)
					    			{
					    				$select.html('<option value="0"></>').trigger('chosen:updated');
					    				$($select.next('div.chosen-container')).find('span')
			    	    	    			.removeClass('linkElement')
			    	    	    			.off('click');
					    			}
				    			}
					    		//important: update column filter with new data
			    				var updateCellFilterValue = $(cnode).find('select >option:selected').text();
					    		dtExt_updateCellFilterData(domId, index, i, updateCellFilterValue);
					    	}
					    	else if(updatedRowDataObj.hasOwnProperty(colName))
					    	{
					    		var updateCellFilterValue = "";
					    		var _newVal = updatedRowDataObj[colName];
					    		var $select = $(cnode).find('select');
					    		if($select.length > 0)
					    		{
					    			$select.val(_newVal).trigger('chosen:updated');
					    			//append selected material to options, in case material added through "Search" icon but not exists in material ddl
					    			if(colName == "invitemmaterial_id" && $(cnode).find('select >option:selected').length == 0)
					    			{
					    				$select.append('<option value="'+_newVal+'" selected="selected">'+updatedRowDataObj["material_name"]+'</option>').trigger('chosen:updated');		    				
					    			}
					    			if($select[0].hasAttribute('isLink') && $select.prop('selectedIndex') > 0) 
				    	    	    {
				    	    	    	var attr = [''+$select.val()+'' ,''+$select.attr('formCode')+'','', true];
				    	    	    	$($select.next('div.chosen-container')).find('span')
					    	    	    	.addClass('linkElement')
				    	    	    	.off('click')
				    	    	    	.on('click', function(){
				    	    	    		checkAndNavigate(attr);
				    	    	    	});
				    	    	    }
					    			updateCellFilterValue = $(cnode).find('select >option:selected').text();
					    		}
					    		else if($(cnode).find('input').length > 0)
					    		{
					    			var $input = $(cnode).find('input');
					    			if($input.attr('type') == "number")
					    			{
					    				$input.attr('originalValue',_newVal);
					    				$input.attr('title',_newVal);
					    				$input.val(_newVal);
					    			}
					    			else
					    			{
					    				$input.val(_newVal);
					    			}	
					    			
					    			updateCellFilterValue = _newVal;
					    		}
					    		else
					    		{					    			
					    			$(cnode).text(_newVal);
					    			updateCellFilterValue = _newVal;
					    		}
					    		//important: update column filter with new data
					    		dtExt_updateCellFilterData(domId, index, i, updateCellFilterValue);
					    	}
					    	
					    	if(colName == "invitemmaterial_id")
					    	{
					    		var updateCellFilterValue = $(cnode).find('select >option:selected').text();
					    		//important: update column filter with new data
					    		dtExt_updateCellFilterData(domId, index, i, updateCellFilterValue);
					    	}
					    	
			            }
				    }// END of for loop dataLength
				}
			});	// END table.rows()	
		}
		
		if(domId == "reactants") 
		{
			updateSolventsMaterialColumn();
		}
	}
	catch(e)
	{
		console.log("error in onMaterialChangeUpdateRowEditTable() for domId", domId)
		console.log("error in onMaterialChangeUpdateRowEditTable() error", e);
	}
}

function onBatchChangeUpdateRowEditTable(domId, data)
{	
	
	try 
	{
		var dataObj = JSON.parse(data); 
		console.log(dataObj);
		if(Object.keys(dataObj).length > 0)
		{					
			var table = $('#'+domId).DataTable();
			table.rows().eq(0).each( function ( index ) 
			{
				var row = table.row( index );	 
			    var rowData = row.data();
			    var rowID = rowData[0];
			    var updatedRowDataObj = [];
	
		    	if(dataObj.hasOwnProperty(rowID))
				{
		    		updatedRowDataObj = dataObj[rowID];
		    		
		    		var dataLength = rowData.length;
				    for(var i=0;i<dataLength;i++)
				    {	    				    	
				    	var cell = table.cell({row: index, column: i});	 
					    var cdata = cell.data();
					    var cnode = cell.node();
					    var cellObj = {};
					    if (checkIfJSON(cdata)) { // check if json or not
					    	cellObj = funcParseJSONData(cdata,true);
					    	var colName = cellObj.dbColumnName;
					    	if(updatedRowDataObj.hasOwnProperty(colName))
					    	{
					    		var _newVal = updatedRowDataObj[colName];
					    		if($(cnode).find('select').length > 0)
					    		{
					    			$(cnode).find('select').val(_newVal).trigger('chosen:updated');
					    		}
					    		else if($(cnode).find('input').length > 0)
					    		{
					    			var $input = $(cnode).find('input');
					    			if($input.attr('type') == "number")
					    			{
					    				$input.attr('originalValue',_newVal);
					    				$input.attr('title',_newVal);
					    				$input.val(_newVal);
					    			}
					    			else
					    			{
					    				$input.val(_newVal);
					    			}	
					    		}
					    		else
					    		{					    			
					    			$(cnode).text(_newVal);
					    		}
					    		//important: update column filter with new data
					    		dtExt_updateCellFilterData(domId, index, i, _newVal);
					    	}
			            }
				    }
				}
			});			
		}
		if(domId == "reactants") 
		{
			updateSolventsMaterialColumn();
		}
	}
	catch(e)
	{
		console.log("error in onBatchChangeUpdateRowEditTable() for domId", domId)
		console.log("error in onBatchChangeUpdateRowEditTable() error", e);
	}
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
	
	//by formCode ...
	if(formCode == "Step")
	{	
		if(domId == "reactants" || domId == "solvents" || domId == "products")
		{
			try
			{
				if(customerFunction == "onChangeLimitingAgent")
				{
					var rowId = $htmlObj.attr('rowId');
					var name = $htmlObj.attr('name');
					var currCellId = $htmlObj.attr('id');
					var isChked = $htmlObj.prop('checked');
					
					//console.log("name: "+ name + " fieldId: " + currCellId + " isChked: " + isChked);
					var table = $('#'+domId).DataTable();
					var columnInd = getColumnIndexByColHeader(domId,"Limiting Agent");
					
					table.rows().eq(0).each( function ( index ) 
					{
						var row = table.row( index );	
					    var cell = table.cell({row: index, column: columnInd}); 
					    var node = cell.node();			    
					    var $input = $(node).find('input');
					    $("#"+domId+'_col_equivalent_row_'+rowId).prop('disabled', isChked);
		    			if($input.attr('id') != currCellId)
						{
		    				$input.prop('disabled', isChked);				
							//$("#"+domId+'_col_equivalent_row_'+rowId).prop('disabled', isChked);
						}
					});
					
					if(isChked && $.trim($("#"+domId+'_col_mole_row_'+rowId).val()) != "")
					{
						generalBL_elementDataTableClickEvent(domId, "calcMaterial", params);
					}
				}
				else if(customerFunction == "onChangeCalcField")
				{
					var rowId = $htmlObj.attr('rowId');
					if(domId == "reactants" && params[3] == "calcMoleOnChange" && !$("#"+domId+'_col_limitingagent_row_'+rowId).prop('checked'))
					{
						console.log("event calcMoleOnChange not allowed because Limiting Agent field is not checked");
						return;
					}
					generalBL_elementDataTableClickEvent(domId, "calcMaterial", params);
				}
				else if(customerFunction == "onChangeMaterial")
				{
					var rowId = $htmlObj.attr('rowId');
					generalBL_elementDataTableClickEvent(domId, customerFunction, [rowId, 'MaterialRef', $.trim($htmlObj.find('option:selected').val())]);
				}
				else if(customerFunction == "onChangeBatch")
				{
					var rowId = $htmlObj.attr('rowId');
					generalBL_elementDataTableClickEvent(domId, customerFunction, [rowId, 'MaterialRef', $.trim($htmlObj.find('option:selected').val())]);
				}
				else if(customerFunction == "onTableRowAdded")//TODO: update column filter
				{
					if(domId == "reactants")
					{
						var redrawRow = false;
						var rowId = params[0];	//new row id
						var table = $('#'+domId).DataTable();	
						var columnInd = getColumnIndexByColHeader(domId,"Limiting Agent");
						table.rows().eq(0).each( function ( index ) 
						{
							var row = table.row( index );							
						    var cell = table.cell({row: index, column: columnInd});	 
						    var cnode = cell.node();			    
						    var $input = $(cnode).find('input');
			    			if($input.prop('checked'))
							{	
			    				$('#'+domId+'_col_limitingagent_row_'+rowId).prop('disabled',true);
			    				var la_mole = "", la_mole_uom = "", la_equiv = "";
			    				
			    				// update just added row cells: "mole" and "mole uom" with "limiting agent"(checked) row data 
			    				var rowData = row.data();
			    				var dataLength = rowData.length;
							    for(var i=0;i<dataLength;i++)
							    {
							    	var cell = table.cell({row: index, column: i});	 
								    var cdata = cell.data();
								    var cnode = cell.node();
								    var cellObj = {};
								    if (checkIfJSON(cdata)) 
								    {
								    	cellObj = funcParseJSONData(cdata,true);
								    	var colName = cellObj.dbColumnName;
								    	if(colName == "mole" && $(cnode).find('input').length > 0)
								    	{
								    		la_mole =  $.trim($(cnode).find('input').val());
								    	}
								    	else if(colName == "moleuom_id" && $(cnode).find('select').length > 0)
								    	{
								    		la_mole_uom =  $(cnode).find('select >option:selected').val();
								    	}
								    	else if(colName == "equivalent" && $(cnode).find('input').length > 0)
								    	{
								    		la_equiv = $(cnode).find('input').val();
								    	}
								    }
							    }		    				
			    				if((la_mole !== undefined && la_mole != "") && (la_equiv !== undefined && la_equiv != ""))
			    				{
			    					var _newVal = Number(la_mole)*Number(la_equiv);
			    					$('#'+domId+'_col_mole_row_'+rowId).val(_newVal);
			    				}
			    				$('#'+domId+'_col_moleuom_id_row_'+rowId).val(la_mole_uom).trigger('chosen:updated');
			    				return false;
							}
						});
					}
					else if(domId == "solvents")
					{						
						updateSolventsMaterialColumn(params[1]); // sent new added row index						
					}
					else if(domId == "products")
					{						
						updateProductAliasColumn(params[0], params[1]); // sent new added row index						
					}
					
				}
				else if(customerFunction == "onTableRowRemoved")
				{
					if(domId == "reactants")
					{
						var checkedCellId = "";
						var table = $('#'+domId).DataTable();	
						var columnInd = getColumnIndexByColHeader(domId,"Limiting Agent");
						table.rows().eq(0).each( function ( index ) 
						{
							var row = table.row( index );	
						    var cell = table.cell({row: index, column: columnInd}); 
						    var cnode = cell.node();	
						    var $input = $(cnode).find('input');
						    if($input.prop('checked'))
						    {
						    	checkedCellId = $input.attr('id');
						    	return false;
						    }
						});
						if(checkedCellId == "")
						{
							table.rows().eq(0).each( function ( index ) 
							{
								var row = table.row( index );	
							    var cell = table.cell({row: index, column: columnInd});
							    var cnode = cell.node();	
							    
							    $(cnode).find('input').prop('disabled',false);
							});
						}
						updateSolventsMaterialColumn();
					}
				}
			} 
			catch(e)
			{
				console.log("error in bl_elementDatatableEditableCustomFuncHandler:",e);
				console.log("customerFunction: " + customerFunction + " domId: " + domId + " params: " + params);
			}
		}
		else if(domId == "action")
		{
			if(customerFunction == "handleSelfTestResults") handleSelfTestResults($htmlObj, true);
		}
	}
	else if(formCode == 'SelfTest' && domId == 'instruments') { //TODO check for location correctness of this part of code (maybe should be handled by bl_elementDatatableEditableAfterSaveHandler())
		try {
			onElementDataTableApiChange('columns');
		} catch(e) {
			console.log("selftest - error in column table render after instrument change(2)");
		}
		
	}
	else if((formCode == 'Project'||formCode == 'InvItemMaterialFr'||formCode == 'InvItemMaterialPr'||formCode == 'InvItemBatch') && domId == 'components') { //TODO check for location correctness of this part of code (maybe should be handled by bl_elementDatatableEditableAfterSaveHandler())
		try {
			 if(customerFunction == "onTableRowAdded"){
			
					var table = $('#'+domId).DataTable();	
					var columnInd = getColumnIndexByColHeader(domId,"Material");
					table.rows().eq(0).each( function ( index ) 
					{
						var row = table.row( index );	
					    var cell = table.cell({row: index, column: columnInd}); 
					    var cnode = cell.node();	
					    var $select = $(cnode).find('select');
					    $select.val(params[0]);
					    $select.trigger('chosen:updated');
					});
			 } 
			 else if(customerFunction == "onChangeMaterial" && formCode == 'InvItemBatch')
			{
				var rowId = $htmlObj.attr('rowId');
				generalBL_elementDataTableClickEvent(domId, customerFunction, [rowId, 'MaterialComponent', $.trim($htmlObj.find('option:selected').val())]);
			}
			
		} catch(e) {
			console.log(e);
		}
		
	}
	else if(formCode == "ExperimentCP"){
		if(customerFunction == "onTableRowAdded" && domId == "expRunPlanningTable"){
			var newRunNumber = params[0];
			addNewRunToheaderSelect(newRunNumber,"Planned");
		}
	} else if((formCode == "RecipeFormulation"||formCode == "ExperimentFor"||formCode == "StepMinFr")
		&& $('#'+domId+'_lastStruct').val().toLowerCase()=='composition'){
		if(customerFunction == "onChangeCalcField" || customerFunction == "onChangeFiller"|| customerFunction == "onChangeBatch"){
			$('#'+domId).attr('lastChangeVal',$htmlObj.attr('oldValue'));//gets the last value in order to rollback the real value when the calculation went wrong
		}
		/*else if(customerFunction == "onChangeRowType")
		{
			var rowId = $htmlObj.attr('rowId');
			generalBL_elementDataTableClickEvent(domId, customerFunction, [rowId, 'Composition', $.trim($htmlObj.find('option:selected').val())],$htmlObj);
		}
		else if(customerFunction == "onChangeMaterial" || customerFunction == "onChangeBatch")
		{
			var rowId = $htmlObj.attr('rowId');
			generalBL_elementDataTableClickEvent(domId, customerFunction, [rowId, 'Composition', $.trim($htmlObj.find('option:selected').val())]);
		}*/
		else if(customerFunction == "onTableRowRemoved"){
			//calc the filler if another row was removed 
			var checkedCellId = "";
			var table = $('#'+domId).DataTable();	
			var columnInd = getColumnIndexByColHeader(domId,"Filler");
			table.rows().eq(0).each( function ( index ) 
			{
				var row = table.row( index );	
			    var cell = table.cell({row: index, column: columnInd}); 
			    var cnode = cell.node();	
			    var $input = $(cnode).find('input');
			    if($input.prop('checked'))
			    {
			    	checkedCellId = $input.attr('rowid');
			    	return false;
			    }
			});
			if(checkedCellId != "")
			{
				//if there was removed an unchecked row' then it should be removed from participating the calculations;
				generalBL_elementDataTableClickEvent(domId, "calcComposition", [checkedCellId,"Composition",'filler','calcFillerOnChange']);
			}
			if(formCode == "RecipeFormulation"||formCode == "ExperimentFor"){
				setDisableMandatoryCompositionsFields(domId);
			} else if(formCode == "StepMinFr"){
				generalBL_generalClickEvent('checkBalance','0');
				createBatchVisibility();
			}
		}
		else if(customerFunction =='onTableBuilt'){
			setDisableMandatoryCompositionsFields(domId);
		}
		else if(customerFunction =='onTableRowAddad'){
			if(formCode == "StepMinFr" && domId == 'products'){
				
			}
		}
	}
}

function doSingleCheck(domId,dbColumnName,currCellId,params,$htmlObj){
	var table = $('#'+domId).DataTable();
	var columnInd = getColumnIndexByColHeader(domId,dbColumnName);
	var rowsCount = table.rows().count();
	table.rows().eq(0).each( function ( index ) 
	{
		var row = table.row( index );	
	    var cell = table.cell({row: index, column: columnInd}); 
	    var node = cell.node();			    
	    var $input = $(node).find('input');
		if($input.attr('id') != currCellId
				&& $input.prop('checked'))
		{ //remove the checkbox from the previous filler
			$input.prop('checked',false);
			var allData = [
				{code:"formId",val:$('#formId').val()}, 
				{code:"formCode",val:"Composition"},
				{code:"parentFormCode",val:$('#formCode').val()},
				{code:"userId",val:$('#userId').val()},
				{code:"formNumberID",val:""},
				{code:"saveType",val:""},
				{code:"onChangeFormId",val:$input.attr('rowid')},
				{code:"onChangecolumnName",val:dbColumnName},
				{code:"onChangecolumnVal",val:'0'},
				{code:"oldVal","val":'1'}
			];	
			var data_ = JSON.stringify({
				action : "onChangeDataTableCell",
				data : allData,
				errorMsg : ""
			});	
			console.log("onChangeDataTableCell AJAX call data_", data_);
		
			$.ajax({
				type : 'POST',
				data : data_,
				url : "./onChangeDataTableCell.request?stateKey=" + $('#stateKey').val(),
				contentType : 'application/json',
				dataType : 'json',
				success : function(obj) {
		
					if (obj.errorMsg != null && obj.errorMsg != '') 
					{
						displayAlertDialog(obj.errorMsg);
						console.log("---ERROR onChangeDataTableCell on SUCCESS :",obj.errorMsg);
					} 
					else 
					{
						//enables the unchecked row
						var _rVal = obj.data[0].val;
						console.log("--- onChangeDataTableCell uncheckFiller on SUCCESS :",_rVal);
						var WW_P = getColumnIndexByColHeader(domId,"w/w%");
						var WW_GRK = getColumnIndexByColHeader(domId,"w/w [gr/Kg]");
						var WV_GRL = getColumnIndexByColHeader(domId,"w/v [gr/L]");
						var purity = getColumnIndexByColHeader(domId,"Purity %");
						cell = table.cell({row: index, column: WW_P}); 
					    node = cell.node();			    
					    $input = $(node).find('input');
					    $input.attr('disabled',false);
					    cell = table.cell({row: index, column: WW_GRK}); 
					    node = cell.node();			    
					    $input = $(node).find('input');
					    $input.attr('disabled',false);
					    cell = table.cell({row: index, column: WV_GRL}); 
					    node = cell.node();			    
					    $input = $(node).find('input');
					    $input.attr('disabled',false);
					    cell = table.cell({row: index, column: purity}); 
					    node = cell.node();			    
					    $input = $(node).find('input');
					    $input.attr('disabled',false);
					    console.log("--- call calcComposition after unchecking the previous filler");
						generalBL_elementDataTableClickEvent(domId, "calcComposition", params);
						return;
						
					}
					hideWaitMessage();
				},
				error : handleAjaxError
			});	
		}
		rowsCount--;
	});
	if(rowsCount == 0){//do the following code if no other filler was checked before
	    console.log("--- call calcComposition with no need to uncheck any filler");
		generalBL_elementDataTableClickEvent(domId, "calcComposition", params,$htmlObj);
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
		
		if(formCode == "Step" || formCode == "StepFr" || formCode == "StepMinFr") // add StepFr and StepMinFr
		{
			if(domId == "action")
			{
				if(paramsObj.dbColumnName == "ACTIONNAME" && paramsObj.formNumberID == "")
				{			
					var onChangeFormId = $htmlObj.attr('rowId');
					if(checkIfJSON(afterSave_retVal))
		    		{
		    			var _obj = JSON.parse(afterSave_retVal);
		    			if(Object.keys(_obj).length > 0)
		    			{
							var newFormNumID = _obj["FORMNUMBERID"];
							var table = $('#'+domId).DataTable();
							table.rows().eq(0).each( function ( index ) 
							{
								var row = table.row( index );	 
							    var rowData = row.data();
							    var rowID = rowData[0];
							    
							    var dataLength = rowData.length;
							    if(rowID == onChangeFormId){//
							    	if($('#STATUS_ID').attr("lastselectedname") != "Planned"){
							    		row.nodes().to$().find('.dataTableApiSelectInfo').prop('disabled',true);
							    	}
							    }
							    for(var i=0;i<dataLength;i++)
							    {	    				    	
							    	var cell = table.cell({row: index, column: i});	 
								    var cdata = cell.data();
								    var cnode = cell.node();
								    var cellObj = {};
								    
								    if (checkIfJSON(cdata)) 
								    {
								    	cellObj = funcParseJSONData(cdata,true);
								    	var colName = cellObj.dbColumnName;
								    	if(rowID == onChangeFormId && colName == "FORMNUMBERID")
								    	{
								    		//update formNumberID in the row hidden column that used as parameter for delete and update just added row
								    		row.data()[5] = newFormNumID;
								    		// update formNumberID in table displayed cell
								    		$(cnode).text(newFormNumID);
							    			updateCellFilterValue = newFormNumID;					    		
							    			//important: update column filter with new data
							    			dtExt_updateCellFilterData(domId, index, i, updateCellFilterValue);
									    }
								    	if(rowID != onChangeFormId && colName == "SetBefore")
								    	{
								    		var $select = $(cnode).find('select');
								    		$select.append('<option value="'+newFormNumID+'">'+newFormNumID+'</option>').trigger('chosen:updated');
								    	}
								    }
							    }
							    
							});
		    			}
					}			
				}
				else if(paramsObj.dbColumnName == "OBSERVATION")
				{
					//console.log("afterSave_retVal",afterSave_retVal);
					if(checkIfJSON(afterSave_retVal))
		    		{
		    			var _obj = JSON.parse(afterSave_retVal);
		    			if(Object.keys(_obj).length > 0)
		    			{
		    				var table = $('#'+domId).DataTable();
		    				// get row that currently changed
		    				var row = table.row( rowIndex );	    				
		    				var rowData = row.data();
						    //var rowID = rowData[0];					    
						    var dataLength = rowData.length;
						    for(var i=0;i<dataLength;i++)
						    {	    				    	
						    	var cell = table.cell({row: rowIndex, column: i});	 
							    var cdata = cell.data();
							    var cnode = cell.node();
							    var cellObj = {};
							    if (checkIfJSON(cdata)) 
							    {
							    	cellObj = funcParseJSONData(cdata,true);
							    	var colName = cellObj.dbColumnName;
							    	if(_obj.hasOwnProperty(colName))
							    	{
							    		var newVal = _obj[colName];						    		
							    		var $input = $(cnode).find('input');
							    		if(colName == "STARTTIME")
							    		{
							    			$input.val(newVal);
							    			$input.attr('oldvalue',newVal);
							    		}
							    		else
							    		{						    			
							    			try {
							    				//prop.dateFormat.datepickerFormat
							    				var date = $.datepicker.parseDate('dd/mm/yy', newVal);
								    			$input.datepicker('setDate', date);
								    			
								    			newVal = $input.val();
								    			$input.attr('oldvalue',newVal);
											} catch (e) {
												newVal = $input.val();
												console.log("error on parsing date",e);
											}
							    		}
							    		
						    			//important: update column filter with new data
						    			dtExt_updateCellFilterData(domId, rowIndex, i, newVal);
							    	}
							    }
						    }
		    			}
		    		}
				}
			}			
		}
		if((formCode == "Step" || formCode == "Experiment" || formCode == "ExperimentCP") && domId == "Parameters")
		{
			if(paramsObj.customFuncName == "onChangeDDL")
			{				
				var params = paramsObj.customFuncParams;
				var rowId = params[0];
				var currValue = $htmlObj.find(':selected').text();
				var $colToUpdate = $("#"+domId+"_col_"+params[1]+"_row_"+rowId);
				var isMatchCondition = (currValue.toLowerCase() === params[2])?true:false; // == 'between'
				$colToUpdate.prop('disabled', !isMatchCondition); 
				if(!isMatchCondition && $colToUpdate.length != 0 && $colToUpdate.val() != "")
				{
					$colToUpdate.val("").trigger('onblur');
				}
			}
			else if(paramsObj.dbColumnName == "PARAMETER_ID")
			{				
				if(checkIfJSON(afterSave_retVal))
	    		{
	    			console.log(afterSave_retVal);
					var _obj = JSON.parse(afterSave_retVal);
	    			if(Object.keys(_obj).length > 0)
	    			{
						var table = $('#'+domId).DataTable();
						// get row that currently changed
	    				var row = table.row( rowIndex );
	    				var rowData = row.data();	    				
//					    var rowID = rowData[0];					    
					    var dataLength = rowData.length;
					    for(var i=0;i<dataLength;i++)
					    {	    				    	
					    	var cell = table.cell({row: rowIndex, column: i});	 
						    var cdata = cell.data();
						    var cnode = cell.node();
						    var cellObj = {};
						    if (checkIfJSON(cdata)) 
						    {
						    	cellObj = funcParseJSONData(cdata,true);
						    	var colName = cellObj.dbColumnName;
						    	if(colName == "UOM_ID")
						    	{
						    		var newList = _obj["UOM_OBJ"];
						    		var $select = $(cnode).find('select');
						    		if(newList instanceof Object)
					    			{					    			
						    			if($select.length > 0)
						    			{
						    				$select.html('');//.trigger('chosen:updated');
						    				var optionList = '<option value="0"></>';
						    				var selectedOption = _obj["UOM_ID_DEFAULT"];					    
						    				$(newList).each(function ()  
		                        			{
		                        				if(this.ID != "")
		                        				{
		                        					var selected = "";
		                        					if(selectedOption.length > 0 && selectedOption == this.ID)
		                        					{
		                        						selected = 'selected = "selected"';
		                        					}
		                        					optionList = optionList + '<option '+selected+' value="'+this.ID+'">'+this.VAL+'</>';		                        				
		                        				}	                        				
		                        			});
						    				$select.append(optionList).trigger('chosen:updated');
						    				$select.val(selectedOption).trigger('onchange');
						    			}
					    			}
						    		else
					    			{
						    			if($select.length > 0)
						    			{
						    				$select.html('<option value="0"></>').trigger('chosen:updated');
						    				$select.val('0').trigger('onchange');
						    			}
					    			}
						    		//important: update column filter with new data
				    				var updateCellFilterValue = $(cnode).find('select >option:selected').text();
						    		dtExt_updateCellFilterData(domId, rowIndex, i, updateCellFilterValue);
							    }
						    	else if(colName == "CRITERIA_ID")
						    	{
						    		var $select = $(cnode).find('select');
				    				$select.val('0').trigger('chosen:updated').trigger('onchange');
						    	}
						    	else if(colName == "VAL1" || colName == "VAL1")
						    	{
						    		var $input = $(cnode).find('input');
						    		$input.val('').trigger('onblur');
						    	}
						    }
					    }
	    			}
				}	
			}
		}
		
		if(formCode == "MaterialFunction" && domId=="functions"){
			if(paramsObj.customFuncName == "onChangeDDL")
			{				
				var params = paramsObj.customFuncParams;
				var rowId = params[0];
				var currValue = $htmlObj.find(':selected').text();
				var $colToUpdate = $("#"+domId+"_col_"+params[1]+"_row_"+rowId);
				var isMatchCondition = (currValue.toLowerCase() === params[2])?true:false; // == 'between'
				$colToUpdate.prop('disabled', !isMatchCondition); 
				if(!isMatchCondition && $colToUpdate.length != 0 && $colToUpdate.val() != "")
				{
					$colToUpdate.val("").trigger('onblur');
				}
			}
		}
		if(((formCode == "RecipeFormulation"||formCode == "StepMinFr") && domId=='compositions')
				||(formCode == "ExperimentFor" && domId=='plannedCompositions')
				|| (formCode == "StepMinFr" && domId == 'products')){

			if(customFuncName == "onChangeCalcField")
			{
				generalBL_elementDataTableClickEvent(domId, "calcComposition", customFuncParams, $htmlObj);
			}
			else if(customFuncName == "onChangeFiller"){
				generalBL_elementDataTableClickEvent(domId, customFuncName, customFuncParams, $htmlObj);
			}
			else if(customFuncName == "onChangeRowType")
			{
				var rowId = $htmlObj.attr('rowId');
				generalBL_elementDataTableClickEvent(domId, customFuncName, [rowId, 'Composition', $.trim($htmlObj.find('option:selected').val())],$htmlObj);
			}
			else if(customFuncName == "onChangeMaterial" || customFuncName == "onChangeBatch")
			{
				if(formCode == "StepMinFr"){
					parent.checkTabClickFlag("SafetyTab",false);
				}
				if(formCode == 'ExperimentFor'){
					checkTabClickFlag("SafetyTab",false);
				}
				var rowId = $htmlObj.attr('rowId');
				generalBL_elementDataTableClickEvent(domId, customFuncName, [rowId, 'Composition', $.trim($htmlObj.find('option:selected').val())]);
			}
		}
	}
}

function editableBL_getAdditParams(domId,rowInx,paramName)
{
	var returnData = "";
	if(domId == "action" && paramName == "formNumberID")
	{
		var table = $('#'+domId).DataTable();		
		returnData = table.row(rowInx).data()[5];
	}
	return returnData;
}

function bl_isDeferRender(domId)
{
	var toReturn = false;
	if($('#formCode').val() == "Main" || $('#formCode').val() == "InvItemSamplesMain" || $('#formCode').val() == 'SampleSelectHolder'  || $('#formCode').val() == 'SampleSelect' || $('#formCode').val() == 'RequestSelect')
	{
		toReturn = true;
	}
	return toReturn;
}

function bl_isTableResizable(domId)
{
	var toReturn = false;
	var formCode = $('#formCode').val();
	
	if(formCode == "Step" && $('table#'+domId).hasClass('editable'))
	{
		toReturn = true;
	}
	else if(formCode == "ExpAnalysisReport" && domId == "reportTable") 
	{
		toReturn = true;
	}
	else if(formCode == "Main") 
	{
		toReturn = true;
	}
	else if(formCode == "ExperimentFor"&&domId=="importedCompositionTable"||formCode == "ExperimentFor"&&domId=="plannedCompositions" )
	{
		toReturn = true;
	}
	else if(formCode == "RecipeFormulation"&&domId=="compositions")
	{
		toReturn = true;
	}
	else if(formCode == "SystemLogReport")
	{
		toReturn = true;
	}
	return toReturn;
	
}

function bl_getResizableTables()
{
	var formCode = $('#formCode').val();
	if(formCode == "Step")
	{
		return $('table.dataTable.editable');
	}
	else if(formCode == "ExpAnalysisReport") 
	{
		return $('table#reportTable');
	}
	else if(formCode == "Main") 
	{
		return $('table.dataTable');
	}
	else if(formCode == "SystemLogReport") 
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
	
	if(formCode == 'Main') {
		winWidth = winWidth - 150; // 150 = width of side bar contains tree icon
	} else {
		winWidth = winWidth - 50; // to be on safe side
	}
		
	resizableColDefaultWidth = winWidth/actualDisplayedColsCount; 
	
	if( (formCode == 'ExpAnalysisReport' && domId == 'reportTable') ||
		(formCode == 'Main') ||
		(formCode.startsWith('Step') && (domId=='action' || domId=='stepSummary')) ) {
			if(resizableColDefaultWidth < 100) {
				resizableColDefaultWidth = 120;
			}
	}
	if (formCode == 'ExperimentFor' && domId == 'importedCompositionTable') {
		$('#importedCompositionTable_Parent').css('padding-left','1px'); // remove padding left
    	winWidth=$('.importedCompositionPane').outerWidth(); //according overview_leftpane 47% (-2 to prevent scroll in the left) ->
		//winWidth=winWidth*0.45;
		resizableColDefaultWidth = winWidth/actualDisplayedColsCount; 
		if(resizableColDefaultWidth < 90) {
			resizableColDefaultWidth = 90;
		}
		$colWidth['Manufacturer'] = 110; 
		$colWidth['Description'] = 140;
	}
	if(formCode == 'ExperimentFor' && domId == 'plannedCompositions') {
//		$('#plannedCompositions_Parent').css('padding-left','1px');  // remove padding left
//		alert($('#plannedCompositions_Parent').css('padding-right'));
//		$('#plannedCompositions_Parent').css('padding-right','1px');  // remove padding right

//	    winWidth=$('.plannedCompositionsPane').outerWidth();// according overview_rightpane 50% ->
//		//winWidth=winWidth*0.50;
//		resizableColDefaultWidth = winWidth/actualDisplayedColsCount; 
//		if(resizableColDefaultWidth < 90) {
//			resizableColDefaultWidth = 90;
//		}
		resizableColDefaultWidth = 110;
		
		//more adjustments...
		$colWidth['Manufacturer'] = 110; 
		$colWidth['Row Type'] = 140; 
		$colWidth['Material'] = 160; 
		$colWidth['CAS Number'] = 80;
		$colWidth['Function'] = 90;
		$colWidth['Batch'] = 180; 
		$colWidth['Description'] = 95; 
		$colWidth['Alternative'] = 95; 
		$colWidth['Filler'] = 63; 
		$colWidth['Purity'] = 75; 
		$colWidth['w/w% '] = 63;
		$colWidth['w/v [gr/L]'] = 63;
		$colWidth['w/w [gr/Kg]'] = 70;
		$colWidth['Actual'] = 68;
		$colWidth['Delta w/v'] = 63;
		$colWidth['Planned for X batch'] = 68;
		$colWidth['Uom'] = 60;
	}
	
//	console.log(domId, "winWidth: "+ winWidth + "; actualDisplayedColsCount: "+actualDisplayedColsCount+"; resizableColDefaultWidth: "+resizableColDefaultWidth);
	//set DefaultWidth_
	$colWidth.DefaultWidth_ = resizableColDefaultWidth;
	return $colWidth;
}

function bl_isTableMergeable(domId)
{
	var toReturn = false;
	if((($('#formCode').val() == 'Experiment' || $('#formCode').val() == "ExperimentCP") && (domId == 'experimentResults' || domId == 'selfTestResults'))  // develop ExperimentCP "Continuous Process"
    		||($('#formCode').val() == "Step" && (domId == 'results' || domId == 'selftestresults') ))
	{
		toReturn = true;
	}
	return toReturn;
}

function bl_isTableHasContextMenu(domId, tableRole)
{
	var toReturn = true;	
	var formCode = $('#formCode').val();
//	var formCodeArr = ["Main","SearchReport","InvItemMaterialsMain","InvItemInstrumentsMain","Step","Experiment","SpreadsheetMain","ExpAnalysisReport","ExperimentFor","ExperimentCP"];	
//	if($.inArray(formCode, formCodeArr) > -1)
//	{
//		toReturn = true;
//	}
	if((formCode == "Step" && (domId == "action"||domId == "reactants" ||domId == "solvents" ||domId == "products" ))
			||((formCode == "StepFr" || $('#formCode').val() == 'StepMinFr') && domId == "action"))
	{
		toReturn = false;
	}
	else if(tableRole == 'Attachment')
	{
		toReturn = false;
	}
	
	return toReturn;
}

function bl_isTableDrawUpgraded(domId)
{
	var toReturn = false;
	var formCode = $('#formCode').val();
	if( (formCode == "Main")
			||
		(formCode == "Step" /*&& domId == "action"*/)
			||
	    (formCode == "ExperimentCP" && (domId == "expRunPlanningTable"||domId == 'runStepSummaryTable'))
			/*||
	   ($('#formCode').val() == "ExpAnalysisReport" && domId == "reportTable")*/
	)
	{
		toReturn = true;
	}
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
	var formCode = $('#formCode').val();
	
	if(($('table#'+domId).hasClass('editable') && !((formCode == "Step" || formCode == "StepFr" || formCode == "StepMinFr"||formCode == "ExperimentFor"||formCode=="RecipeFormulation") && (domId == "action"||domId=="plannedCompositions"||domId=="compositions")))
		||
		(formCode == "Step" && domId == "selftestresults")
		||
		(formCode == "Experiment" && domId == "selfTestResults")
		||
		(formCode == "ExperimentCP" && domId == "expRunPlanningTable")
	)
	{
		return false;
	}
	
	return toReturn;
}

function bl_isColumnReorderable(domId, colName) 
{
	var toReturn = true;
	var formCode = $('#formCode').val();
	if((formCode == "Step" || formCode == "StepFr" || formCode == "StepMinFr") && domId == "action" && colName.toUpperCase() == "FORMNUMBER")
	{
		toReturn = false;
	}
	return toReturn;
}

function bl_getSummaryLineData(domId){
	var formCode = $('#formCode').val();
	var obj = "";
	if((formCode == "ExperimentFor" && domId == "plannedCompositions"))
	{
		obj = {
				columnList:  [ "w/v [gr/L]", "w/w [gr/Kg]", "w/w%", "Planned for X batch"],
		        type: "sum",
		        precision: "3"
		    };
	}
	else if(formCode == "CompositionDetails"){
		obj =  {
				columnList: [ "W/W%", "W/V [gr/l]", "W/W [gr/kg]"],
				type: "sum",
				precision: "3"
	    };
	}
	return obj;
}

function bl_removeLabelFromColName(domId) 
{
	var toReturn = false;
	var formCode = $('#formCode').val();
	if(formCode == "InvItemBatch" && domId == "samples")
	{
		toReturn = true;
	}
	return toReturn;
}

function outPutLabelDTWrapper(obj) {
	var domId = $(obj).parent().attr('tableid');
	var formCode = $('#formCode').val();
	
	var smartSelectList = "";
	try {
		var toReturn = [];  
		var checked;
		var oTable = $('#'+domId).dataTable();      
		
		
		var dataTableApiSelectInfo = '';
		
		if ((formCode == 'InvItemBatch' && domId == 'samples') || (formCode == 'Request' && domId == 'samples')) {
			dataTableApiSelectInfo = 'dataTableApiSelectInfoLabel';
		} else {
			dataTableApiSelectInfo = 'dataTableApiSelectInfo';
		}
		
		/*
		 * 24122019 kd fixed bugs 7713 and 7714: 
		 * this code for get id's of checked rows from all table -> 
		 * 							visible part and not visible including: 
		 * 											rows are filtered by column filter;
		 * 											rows are not shown because table has many rows and visible part has less rows, for example 10/25/50/100
		 */
		$(oTable.fnGetNodes()).each(function(i)  
        {               
            checked = $(this).find('input[class="' + dataTableApiSelectInfo + '"]:checked')
            if (checked.length != 0) {
            	toReturn.push(checked[0].value);
            }	
        });
		smartSelectList = toReturn.toString(); 
	} catch(e) {
		//do nothing
	}
	
	if(smartSelectList == null || smartSelectList == "") {
		 displayAlertDialog("Please select at least one row");
		 return;
	}
	  
	// for task 24488 it is just one sample label design (as in the sample screen), in future task we will show dialog with that the user will select the labelCode that represent the ireport xml
	var selectIdList = smartSelectList;
	var labelCode = "sampleGeneral";
	
	
	outPutLabel(domId, labelCode, selectIdList);
}

function getReactantsMaterialsList()
{
	var returnObj = {};
	var curDomId = "reactants";
	var table = $('#'+curDomId).DataTable();	
	var columnInds = getColumnIndexByColHeader(curDomId,["Name","Batch","Limiting Agent"]);	
	table.rows().eq(0).each( function ( index ) 
	{
		var row = table.row( index );
	    var cell = table.cell({row: index, column: columnInds["Name"]}); 
	    var $select = $(cell.node()).find('select');
	    if($select.length > 0 && $select.prop('selectedIndex') > -1)
		{	
			var selMaterialId = $.trim($select.find('option:selected').val());
			var selMaterialName = $.trim($select.find('option:selected').text());
			var selBatchName = "";
			var isDefault = "0";
			cell = table.cell({row: index, column: columnInds["Batch"]}); 
		    $select = $(cell.node()).find('select');
		    if($select.length > 0 && $select.prop('selectedIndex') > -1)
			{			
		    	selBatchName = $.trim($select.find('option:selected').text());	
			}
		    cell = table.cell({row: index, column: columnInds["Limiting Agent"]}); 
		    var $input = $(cell.node()).find('input');
			if($input.length > 0 && $input.prop('checked'))
			{
				isDefault = "1";
			}
			returnObj[selMaterialId] = {'name':selMaterialName,'batch':selBatchName,'isDefault':isDefault};
		}
	});
	
	console.log("getReactantsMaterialsList()",JSON.stringify(returnObj));
	return returnObj;
}

function updateSolventsMaterialColumn(rowInx)
{
	var listObj = getReactantsMaterialsList();
	if(Object.keys(listObj).length > 0)
	{
		var table = $('#solvents').DataTable();	
		var columnInds = getColumnIndexByColHeader("solvents",["Material","Ratio Type"]);		
		rowInx = (rowInx == undefined || rowInx == null)?-1:rowInx;
		var isNewRow = (rowInx == -1)?false:true;
		var loopObj = (rowInx == -1)?table.rows():table.row(rowInx);

		loopObj.eq(0).each( function ( index )
		{
			var row = table.row( index );	
			//var ratioType_cell = table.cell({row: index, column: columnInds["Ratio Type"]}); 
			//if($(ratioType_cell.node()).find('select').prop('selectedIndex') > 0)
			//{
				var material_cell = table.cell({row: index, column: columnInds["Material"]}); 
				var $select = $(material_cell.node()).find('select');
				var selMaterialId = $.trim($select.find('option:selected').val());
				$select.html('<option value="0"></>').trigger('chosen:updated');
				var selectOptionList = '<option value="0"></>';
				for(var key in listObj)
				{
					var value = listObj[key];
					var selected = "";
					if((isNewRow && value['isDefault'] == "1") || (selMaterialId != "0" && selMaterialId == key))
					{
						selected = "selected";
					}
					var tooltip = (value['batch'] == "")?"":"title=\""+value['batch']+"\"";
					selectOptionList = selectOptionList + '<option '+tooltip+' '+selected+' value="'+key+'">'+value['name']+'</>';	
				}
				$select.append(selectOptionList).trigger('chosen:updated');	
				if($select[0].hasAttribute('isLink') && $select.prop('selectedIndex') > 0) 
	    	    {
	    	    	var attr = [''+$select.val()+'' ,''+$select.attr('formCode')+'','', true];
	    	    	$($select.next('div.chosen-container')).find('span')
	    	    		.addClass('linkElement')
		    	    	.off('click')
		    	    	.on('click', function(){
		    	    		checkAndNavigate(attr);
		    	    	});
	    	    }
				
				//important: update column filter with new data
				var updateCellFilterValue = $select.find('option:selected').text();
				dtExt_updateCellFilterData("solvents", index, columnInds["Material"], updateCellFilterValue);
			//}
		});
	}
}

function updateProductAliasColumn(rowId, rowInx)
{
	var stepNumber = $('#formNumberId').val();
	if(rowInx == "0"){
		rowInx = "";
	}else{
		rowInx = "."+rowInx;
	}
	if($('#products_col_alias__row_'+rowId).val()!=undefined){
		var _newVal =$('#products_col_alias__row_'+rowId).val().replace("@step number@",stepNumber+rowInx)
	    $('#products_col_alias__row_'+rowId).val(_newVal);
		$('#products_col_alias__row_'+rowId).attr("title",_newVal);
	}
}

function openSampleSelectHolderForRequestSample(domId) {
	
	var dialogHeight = 1000;
    var dialogWidth = 1000;   
	
	var generalDisabledFlagParam_ = $('#generalDisabledFlagParam').val();
	var buttonText_ = '';
	try {
		buttonText_ = $('#' + domId + "_dataTableStructButtons").find("button.dataTableApiEditShared").text();
	} catch(e) {}
	var permissionDisabledVal_ = 0;
	if(generalDisabledFlagParam_ == 1 || buttonText_.toUpperCase() == getSpringMessage("View").toUpperCase()) {
		permissionDisabledVal_ = 1;
	}
	urlPermissionDisabled = '&PERMISSION_DISABLED=' + permissionDisabledVal_;
	
    if ($('[id="' + domId + '"] thead').length) {
        selectedTable = $('#' + domId).DataTable();
        custid = selectedTable.row(':eq(0)').data();
        if (typeof custid !== 'undefined') {
            formId = custid[5];
        } else if($('#' + domId + '_sharedFormId').val()!== undefined && $('#' + domId + '_sharedFormId').val()!="-1"){
        	formId = '-1'; // TODO take by parent $('#' + domId + '_sharedFormId').val();
        }
    }
    
	//var page = "./init.request?stateKey=" + stateKey + "&formCode=" + formCode + "&formId=" + formId + "&userId=" + $('#userId').val() + '&tableType=' + $('[id="' + domId + '_tableType"]').val() + '&urlCallParam=' + replaceDTUrlCallParamVal($('[id="' + domId + '_urlCallParam"]').val(), formId) + '&PARENT_ID=' + parentId + isCloneAttrFlag;
	var page = "./init.request?stateKey=" +  $('#stateKey').val() + "&formCode=SampleSelectHolder&formId=" + formId + "&userId=" + $('#userId').val() + "&tableType=&urlCallParam=&PARENT_ID=" + $('#formId').val()+"&PROJECT_ID=" + $('#PROJECT_ID').val()+"&ORIGINEXPERIMENT_ID="+$('#originExperimentId').val();
//234399
//	"./init.request?stateKey=1576671143486&formCode=SampleDataRef&formId=-1&userId=1109&tableType=&urlCallParam=%7B'call_project_id'%3A'83036'%2C'call_experiment_id'%3A'84169'%7D&PARENT_ID=85167"
	
	var $dialog = $('<div id="prevDialog" style="overflow-y: hidden;""></div>')
    .html('<iframe id="formIframeId" style="border: 0px;width:100%;height:100%" src="' + page + urlPermissionDisabled + '"></iframe>')
    .dialog({
        autoOpen: false,
        modal: true,
        height: dialogHeight,
        width: dialogWidth,
        //  title: title,
        close: function () {
            $('#prevDialog iframe').attr('src', 'about:blank');
            $('#prevDialog').remove();
            onElementDataTableApiChange(domId);
            onElementDataTableApiChange('documents');
        }
    });

$dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
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
	
	if(customFuncName == "handleSelfTestResults") return handleSelfTestResults($htmlObj, false);
	
	return true;
}

function handleSelfTestResults($htmlObj, showResultType)
{
	var fullVal = $htmlObj.val();	
	if(fullVal != "")
	{
		try {
				var floatRegex = '[-+]?([0-9]*.[0-9]+|[0-9]+)'; 
				var matches = fullVal.match(floatRegex); 				
				var float = (matches == null)?"":matches[0];
				
				if(showResultType)
				{
					var type = $htmlObj.attr('result_type');
					if(type != null && type != undefined)
					{
						$htmlObj.val(float + type);
					}
				}
				else
				{
					$htmlObj.val("");
					
					if (float != "") {
						$htmlObj.val(float);
					}
					if(!$htmlObj.attr('result_type'))
						$htmlObj.attr('result_type', fullVal.substr(float.length,fullVal.length));
				}
		} catch (e) {
			console.error(e);
		}
	}
	else
	{
		if(showResultType)
		{
			var type = $htmlObj.attr('result_type');
			if(type != null && type != undefined)
			{
				$htmlObj.val(type);
			}
		}
	}
	checkTabClickFlag("ResultsTab",false); //raise flag to render results (fix bug 7908 - use new function made for this bug - checkTabClickFlag)
	
	return true;
}

function bl_setColVisibilityByColTitleArr(domId, isVisible, context) {
	var isOverride = false;
	
	//*********** set By formcode and domId *************
	//----------- ExpAnalysisReport reportTable
	if($('#formCode').val() == 'ExpAnalysisReport'&&domId == 'reportTable') {
		if( context != 'load_last_save_columns') {
    		return isOverride;
    	}
		// if designIdHolder is not empty there is design in report
		designIdHolder = getDesignIdOrNameFromSession("ID");
    	if ($('#nameId').val()!='-1' && $('#nameId').val()!=''
			&& (designIdHolder==null || designIdHolder=='')) {
    		return isOverride; //false
		}
    	
    	// get current columns
    	var _table = $('#' + domId).DataTable();
    	var _header, _title, _column;
    	var _currentColumnTitile = '';
    	
    	for(var i=0; i < _table.columns().header().length; i++)
		{
			_column = _table.column(i);
			if(_column.visible() == isVisible)
			{
				continue;
			}
			_header = $(_column.header())[0];
			var _uTitle = $(_header).attr('uniqueTitle');
			// _title = columns_settings[i].uniqueTitle;
			if(_uTitle !== undefined && _uTitle != "")
			{
				_title = _uTitle;
			}
			else
			{		
				_title = $(_header).text();
			}
			
			if(_currentColumnTitile == '') {
				_currentColumnTitile = _title;
			} else {
				_currentColumnTitile = _currentColumnTitile + "@" + _title;
			}
		}
    	
    	if(context == 'load_last_save_columns' && ($('#nameId').val()==''||$('#nameId').val()=='-1')
    			 && (designIdHolder==null||designIdHolder=='')){
    		
	    	// set override
	    	isOverride = true;
	    	
	    	//it's a report that is not based on a report design or has not been saved yet
    		var columnList = _currentColumnTitile.split('@');
    		var columnArrToHide = [];
    		for(var i=0;i<columnList.length;i++){
    			if(columnList[i].indexOf('Reactant')!='-1'||columnList[i].indexOf('Solvent')!='-1'){//if the column contains data of reactant or solvent the hide it by default
    				columnArrToHide.push(columnList[i]);
    			}
    		}
    		setColVisibilityByColTitleArrCore(domId, isVisible, columnArrToHide);
    	}
	    
    	else if(context == 'load_last_save_columns'&&designIdHolder!=null&&designIdHolder!=''&&designIdHolder!="")
	    {
	    	// set override
	    	isOverride = true;
	    	
	    	// url call
	    	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
	    	+ $('#formCode').val() + "&userId=" + $('#userId').val()
	    	+ "&eventAction=getColumnDesign";
	    	
	    	var stringifyToPush = {
					code : 'currentColumnTitile',
					val : _currentColumnTitile,
					type : "AJAX_BEAN",
					info : 'na'
			};
	    	
	    	var experimentList = [];
	    	var lastMultiValues = $('#experimentTable_value').val(); // get the values from lastMultiValues that may contain selection that not appear in this filter (criteria) table
        	if(lastMultiValues.length > 0) {
        		experimentList = lastMultiValues.split(',');
        	} 
        												//The table may not be ready on loading of the site(navigating/save/refresh). in this case->there is no need in the following rows, since it select the data that was currently changed, and this can be done only after the table is ready.
        	var table_ = $('#experimentTable').DataTable(); 
        	// add selected values that are not in lastMultiValues + remove unselected values that are in this table (=> the user unselected them)
            table_.$('input[Type="checkbox"]').each(function (index) {
            	if((',' + lastMultiValues + ',').indexOf(',' + $(this).val() + ',') <= -1) {
            		if($(this).prop('checked')) {
            			experimentList.push($(this).val());
            		}
            	} else {
            		if(!$(this).prop('checked')) {
            			//remove val from array
            			const index = experimentList.indexOf($(this).val());
            			experimentList.splice(index, 1);
            		}
            	}
            });  
	    	var stringifyToPush2 = {
					code : 'experimentList',
					val : experimentList.toString(),
					type : "AJAX_BEAN",
					info : 'na'
			};
	    	
	    	var allData = getformDataNoCallBack(1);
	    	allData = allData.concat(stringifyToPush).concat(stringifyToPush2);
	    	var data_ = JSON.stringify({
	    		action : "getColumnDesign",
	    		data : allData,
	    		errorMsg : ""
	    	});
	    	
	    	// call...
	    	$.ajax({
	    		type : 'POST',
	    		data : data_,
	    		async: false,
	    		url : "./generalEvent.request" + urlParam + "&stateKey="
	    		+ $('#stateKey').val(),
	    		contentType : 'application/json',
	    		dataType : 'json',
	    		success : function(obj) {
	    			
	    			var designToHidden = obj.data[0].val;
	    							var array = designToHidden.split("@@@");
	    							var hiddenList=array[1].split('hiddeenList')[1].split('@');
	    							var messages=array[0].split('messages')[1];
	    			             
	    						$('[id="' + domId + '_colsArray"]').val(hiddenList);
	    			  if(messages!="")
	    			    {
	    			    var messagesArray=messages.split('@');
	    			    var messagesToPrint="";
	    			    for (var i = 0; i < messagesArray.length; i++) {
	    			    	if(messagesToPrint=="")
	    			    		messagesToPrint=messagesArray[i];
	    			    	else messagesToPrint=messagesToPrint+'</br></br>'+messagesArray[i];
	    			}
	    			        displayAlertDialog(messagesToPrint);
	    			    }
	    			                 
	    			  				setColVisibilityByColTitleArrCore(domId, isVisible, hiddenList);
	    				
	    		},
	    		error : handleAjaxError
	    	});
		}
	}
	
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
	if($('#formCode').val() == "ExpAnalysisReport" && domId == "reportTable")
	{
		
    	var sheet = xlsx.xl.worksheets['sheet1.xml'];
    	var width
    	//console.log(sheet);
        var col = $('col', sheet);
	    // Loop over the first row
	    $('row:first c', sheet).each( function (i) {
	     
	        var colName = $.trim($('is t', this).text());
	        //set width for the next Experiment&Step columns
	        var width_ = getWidthByNameForExpAnalysisReport(colName);
	        if(width_ != null) {
	        	$(col[i]).attr('width', width_);
	        }
	    });
	    
	    
	    //Examples:
	    // $('c[r=A1] t', sheet).text( 'Custom text' );
	    // $(col[2]).attr('width', 60);
	    //$('row c[r^="B"]', sheet).attr('s', 55);
		//$('row c[r^="C"]', sheet).attr('s', 55);
		/* from excelHtml5#Customization: 's' - style, 55 - Wrapped text */
	    
	    // add title
	    var countTitileLines = 1; // start from 1 to include the original column name header
	    try {
	    	var title_ = $('#pageTitle').text();
	    	var schemaTitle_ = '';
	    	var designTitle_ = '';
	    	
	    	//eval schemaTitle_ and designTitle_
	    	var designIndex_ = title_.indexOf('Design Name:');
	    	if(designIndex_ > 0) {
	    		schemaTitle_ = title_.substr(title_,designIndex_);
	    		designTitle_ = title_.substr(designIndex_);
	    	} else {
	    		schemaTitle_ = title_;
	    	}
	    	
	    	// add titles to excel
	    	if(designTitle_ != null && designTitle_.trim().length > 0) {
	    		addTitle(sheet,designTitle_);
	    		countTitileLines++;
	    	}
	    	
	    	if(schemaTitle_ != null && schemaTitle_.trim().length > 0) {
	    		addTitle(sheet,schemaTitle_);
	    		countTitileLines++;
//	    		$('row:first c', sheet).attr( 's', '2' ); // first row is bold
	    	}
	    } catch(e) {
			console.log("add title error");
		} 
	    
	    //Wrapped text all
	    $('row c', sheet).attr( 's', '55' );
	    
	    //make headers bold 
	    for(i = 1; i <= countTitileLines; i++) {
	    	 $('row[r="' + i + '"] c', sheet).attr( 's', '2' );
	    }
	}
}

function getWidthByNameForExpAnalysisReport(colName) {
	var width_ = null;
	var width60_ = ',aim,conclusion,description';
	var width4_ = ',sign';
	var width11_ = ',parameter';
	var width6_ = ['quantity','volume','purity %','conversion,%','summary,%','chemical yield,%','isolated yield,%','assay,%','value 1','value 2','moles','actual ratio to limiting agent','impurity .+,%','water content,%','equivalent'];
	var colName_ = ',' + colName.toLowerCase();
	//summary columns
	if (width60_.indexOf(colName_) > -1) {
		width_ = 60;
	} else if(width4_.indexOf(colName_) > -1) {
		width_ = 4;
	} else if(width11_.indexOf(colName_) > -1) {
		width_ = 11;
	} else {
		colName_ = colName_.slice(1);
		for(var i=0;i<width6_.length;i++){
			var patt = new RegExp("^"+width6_[i]+"$");
			if(patt.test(colName_)){
				width_ = 6;
				break;
			}
		}
	}
	return width_;
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
	
	var formCode = $('#formCode').val();
	if(formCode == "ExperimentCP" && domId == "expRunPlanningTable") 
	{
		var groupHeaderMap = new Map();
		var ddlDisabled = "";
		dtColumnsApi.iterator('column', function ( settings, columnIndex) {
            
            var column = this.column(columnIndex);	    	
	    	var column_settings = settings.aoColumns[ columnIndex ];
	    	//console.log("column_settings",column_settings);

	    	if(column_settings.bVisible)
	    	{
		    	if (column_settings.headerGroupVal !== undefined)
			    {
			    	var _val = column_settings.headerGroupVal;
			    	$(column.header()).attr('headerGroupVal', _val);
			    	if(groupHeaderMap.has(_val))
			    	{
			    		var prevNum = groupHeaderMap.get(_val);
			    		groupHeaderMap.set(_val,parseInt(prevNum) + 1);
			    	}
			    	else
			    	{
			    		groupHeaderMap.set(_val,1);
			    	}
			    	
			    	if (column_settings.ratetype !== undefined)
			    	{
			    		$(column.header()).attr('ratetype', column_settings.ratetype);
			    	}
			    	if (column_settings.materialrefid !== undefined)
			    	{
			    		$(column.header()).attr('materialrefid', column_settings.materialrefid);
			    	}
			    }
		    	if (column_settings.rateTypeDisabled !== undefined)
		    	{
		    		ddlDisabled = (column_settings.rateTypeDisabled == "1")?"disabled":"";
		    	}
	    	}
        } );
		
		if(groupHeaderMap.size > 0) {
			
			var $firstHeaderRow = $header.find('tr:eq(0)');
			var $searchHeaderRow = $header.find('tr:eq(1)');
			var colLength = $firstHeaderRow.find('th').length;
			var $firstHeaderRowTH = $firstHeaderRow.find('th');
			var rateTypeTH = "";

			/* add additional row to header to display Rate Type */
			$firstHeaderRowTH.each(function(i)
			{
				var $this = $(this);
				var _ratetype = $this.attr("ratetype");
				if(_ratetype !== undefined) {
										
					rateTypeTH += '<th><select '+ddlDisabled+' style="display: block;" class="thRateTypeDDL" onchange="onRateTypeChange(this.value,'+$this.attr("materialrefid")+')">\n' + 
										'<option value=\"volume\" '+((_ratetype=="volume")?'selected':'')+'>Volume</option>\n' + 
	                					'<option value=\"quantity\" '+((_ratetype=="quantity")?'selected':'')+'>Quantity</option>\n' + 
	                					'<option value=\"mole\" '+((_ratetype=="mole")?'selected':'')+'>Mole</option>\n' +
	                				'</select> \n'+
	                			  '</th>';
				}
				else
				{
					rateTypeTH += '<th></th>';
				}
			});
			
			var groupTH = "<th rowspan=2 style='width:10px'></th><th rowspan=2 style='width:10px'>Run #</th>";
			groupHeaderMap.forEach(function(value, key) {
//				  console.log(key + ' = ' + value);
				  groupTH += '<th colspan='+value+'>'+key+'</th>';
			});
	//		$header.find('tr:eq(0) > th:eq(0)').remove();
//			var htr = $header.find('tr:eq(0)');
			$firstHeaderRow.find('th:eq(0)').remove();// remove first column header -> calc column
			$firstHeaderRow.find('th:eq(0)').remove();// remove second column header -> run number column
			$searchHeaderRow.find('th:eq(0)').html('');
			$searchHeaderRow.find('th:eq(1)').html('');
			$header.prepend($('<tr class="dataTable-header-custom">'+groupTH+'</tr>'));
			$firstHeaderRow.after($('<tr class="dataTable-header-search-row">'+rateTypeTH+'</tr>'));
		}
	} 
	else if(formCode == "ExperimentCP" && domId == 'runStepSummaryTable') 
	{
		var groupHeaderMap = new Map();
		dtColumnsApi.iterator('column', function ( settings, columnIndex) {
            
            var column = this.column(columnIndex);	    	
	    	var column_settings = settings.aoColumns[ columnIndex ];
	    	//console.log("column_settings",column_settings);

	    	if(column_settings.bVisible)
	    	{
		    	if (column_settings.headerGroupVal !== undefined)
			    {
			    	var _val = column_settings.headerGroupVal;
			    	$(column.header()).attr('headerGroupVal', _val);
			    	if(groupHeaderMap.has(_val))
			    	{
			    		var prevNum = groupHeaderMap.get(_val);
			    		groupHeaderMap.set(_val,parseInt(prevNum) + 1);
			    	}
			    	else
			    	{
			    		groupHeaderMap.set(_val,1);
			    	}
			    }
	    	}		    
        } );
		
		if(groupHeaderMap.size > 0) {
			
			var $firstHeaderRow = $header.find('tr:eq(0)');
			var colLength = $firstHeaderRow.find('th').length;
			var $firstHeaderRowTH = $firstHeaderRow.find('th');
			
			var groupTH = "<th rowspan=2 style='width:10px'>Run #</th>";
			groupHeaderMap.forEach(function(value, key) {
//				  console.log(key + ' = ' + value);
				  groupTH += '<th colspan='+value+'>'+key+'</th>';
			});
			
			$firstHeaderRow.find('th:eq(0)').remove();// remove second column header -> run number column
			$header.prepend($('<tr class="dataTable-header-custom">'+groupTH+'</tr>'));
		}
	}
}

function bl_dtColumnFeaturesCustom(domId, dtObject, currColumn, colIndex) {
	
	var formCode = $('#formCode').val();
	if((formCode == "ExperimentCP" && domId == "expRunPlanningTable")) 
	{
		var _colData = dtObject.data[0][colIndex];
		var jsonData = funcParseJSONData(_colData,true);
		if(jsonData.hasOwnProperty("columnAttr")) {
			var o = jsonData["columnAttr"];
			if(Object.keys(o).length > 0)
			{
			var nextColumn = dtObject.columns[colIndex+1];
			nextColumn.ratetype = o.rateType;
			nextColumn.materialrefid = o.materialRefId;
				nextColumn.rateTypeDisabled = o.rateTypeDisabled;
			}
		}
	}
}

function onRateTypeChange(value, materialRefId) {
	
	var allData = [
		{code:"materialRefId",val:materialRefId}, 
		{code:"rateType",val:value},
		{code:"PARENT_ID",val:$('#formId').val()}
	];
	
	var data_ = JSON.stringify({
		action : "updateMaterialRateType",
		data : allData,
		errorMsg : ""
	});
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + "&userId=" + $('#userId').val()
			+ "&eventAction=updateMaterialRateType";
	
	$.ajax({
			type : 'POST',
			data : data_,
			url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
			contentType : 'application/json',
			dataType : 'json',
			success : function(obj) {
				hideWaitMessage();
               onElementDataTableApiChange('expRunPlanningTable'); 
			},
			error : handleAjaxError
		});
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
function openCopyFunctionTablePopUp()
{

	try {
		var formCode = 'CopyFunctionTable';
		var dialogHeight = 300;
		var dialogWidth =600;
		var page = "./init.request?stateKey=" + $('#stateKey').val()
				+ "&formCode=" + formCode + "&formId=-1" + "&userId="
				+ $('#userId').val();
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

					close : function(e) {
						onElementDataTableApiChange('functions');
						$('#prevDialog iframe').attr('src', 'about:blank');
						$('#prevDialog').remove();
						parent.$('#prevDialog').dialog('close');
					}
				});

		$dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');

	} catch (e) {
		console.log('resetColumns error in eval change struct condition');
	}
}

function copyFunctionTable() {

	var allData = getformDataNoCallBack(1);

	var urlParam = "?formId=" + $('#formId').val()
	// + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val()
	+ "&eventAction=copyFunctionTable&isNew="; // + $('#isNew').val();
	var data_ = JSON.stringify({
		action : "copyFunctionTable",
		data :[{
			code : "MATERIALFUNC_ID",
			val : $('#MATERIALFUNC_ID').val(),
			type : "AJAX_BEAN",
			info : 'na'
		},{
			code : "PARENTID",
			val : parent.$('#formId').val(),
			type : "AJAX_BEAN",
			info : 'na'
		}],
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
			$('#prevDialog iframe').attr('src', 'about:blank');
			$('#prevDialog').remove();
			parent.$('#prevDialog').dialog('close');
			
		},
		error : handleAjaxError
	});

	//parent.$('#prevDialog').dialog('close');

}

function changeCompositionDetailsTableByCompositionType(domId){
	try {
		   var compositiontypename = parent.$('#COMPOSITIONTYPENAME').val();
		   var _table = $('#'+domId).DataTable();
//		   var wvLFlag = 0;
		   for(var i=0; i < _table.columns().header().length; i++) {
				var col_ = _table.column(i);
				
				col_ = _table.column(i);
				var _$header = $(col_.header());
				
				//hide w/v [gr/L] if not Liquid
				if(_$header.text() == 'W/V [gr/l]') {
					if(compositiontypename != 'Liquid') {
//						wvLFlag = 1;
						removeColumnDatatable(domId, _$header.text());
					} else {
						// remove from hidden list if Liquid (it will be displayed even if it was last saved value is hidden!)
						$("#"+domId+"_colsArray").val($("#"+domId+"_colsArray").val().replace('W/V [gr/l]','NA_'));
					}
					_$header.addClass('sorting_disabled'); // need to separate logic of sorting and removing on table creation
			    }
		   }
	   } catch(e) {
		   console.log("error in changeCompositionDetailsTableByCompositionType");
	   }
}

function changePlannedCompositionsTableByCompositionType(domId) {
	
   try {
	   var compositiontypename = $('#COMPOSITIONTYPENAME').val();
	   var _table = $('#'+domId).DataTable();
	   var wvLFlag = 0;
	   var statusName = $('#STATUS_ID').attr("lastselectedname");
	   for(var i=0; i < _table.columns().header().length; i++) {
			var col_ = _table.column(i);
			
			col_ = _table.column(i);
			var _$header = $(col_.header());
//				var _uTitle = _$header.attr('uniqueTitle').trim();
//				console.log("_uTitle=" + _uTitle);
			
			//hide w/v [gr/L] if not Liquid
			if(_$header.text() == 'w/v [gr/L]') {
				if(compositiontypename != 'Liquid') {
					removeColumnDatatable(domId, _$header.text());
				} else {
					// remove from hidden list if Liquid (it will be displayed even if it was last saved value is hidden!)
					$("#"+domId+"_colsArray").val($("#"+domId+"_colsArray").val().replace('w/v [gr/L]','NA_'));
				}
				_$header.addClass('sorting_disabled'); // need to separate logic of sorting and removing on table creation
			}
			
			//hide Actual on planned status
			if(_$header.text() == 'Actual' || _$header.text() == 'Uom') {
				if(statusName == 'Planned') {
					removeColumnDatatable(domId, _$header.text());
				} else {
					// remove from hidden list if Liquid (it will be displayed even if it was last saved value is hidden!)
					$("#"+domId+"_colsArray").val($("#"+domId+"_colsArray").val().replace(_$header.text(),'NA_'));
				}
				_$header.addClass('sorting_disabled'); // need to separate logic of sorting and removing on table creation
			}
			
			//Planned for X batch - replace X with batch
			var batchSize="batchSize";
			var batchSizeUom = 'BATCHSIZE_UOM';
			if(domId == 'compositions' && $('#formCode').val() == 'StepMinFr' && _$header.text() != 'Planned for X Fbatch'){
				batchSize="batchSizeMaterial";
				batchSizeUom = 'BATCHSIZEMATERIALUOM_ID';
			}
				
			if(_$header.text() == 'Planned for X batch'	|| _$header.text() == 'Planned for X Sbatch') {
				var newTitle = _$header.text();
				if(_$header.text() == 'Planned for X Sbatch'){
					newTitle =  _$header.text().replace('Sbatch', 'Step batch');
				}
				
				if($('#'+batchSize).length && $('#'+batchSize).val().trim().length >0 ) {
					newTitle =  newTitle.replace('X',$('#'+batchSize).val().trim()+" "+$("#"+batchSizeUom+" option:selected" ).text());
				}
				$($('#' + domId).DataTable().column(col_).header()).text(newTitle);
			}
			
			if(_$header.text() == 'Planned for X Fbatch') {
				var newTitle = _$header.text();
				newTitle =  _$header.text().replace('Fbatch', 'Formulation batch');
				
				if(parent.$('#'+batchSize).length && parent.$('#'+batchSize).val().trim().length >0 ) {
					newTitle =  newTitle.replace('X',parent.$('#'+batchSize).val().trim()+" "+parent.$("#"+batchSizeUom+" option:selected" ).text());
					
				}
				$($('#' + domId).DataTable().column(col_).header()).text(newTitle);
			}
		}
//	    if(wvLFlag == 1) {
//	    	var indx = getColumnIndexByColHeader(domId, 'w/v [gr/L]'); // it is not the right index we will only hide
//	        $('#' +domId + ' tr').find('td:eq(' + indx + '),th:eq(' + indx + ')').remove();
//	    }
   } catch(e) {
	   console.log("error in changePlannedCompositionsTableByCompositionType");
   }
}
function getExperimentsPerGroup(rowId){
   
  
	var allData = [{
			code : "groupId",
			val : rowId,
			type : "AJAX_BEAN",
			info : 'na'
		}];

	
	// url call
	var urlParam = "?formId="+ $('#formId').val()
				+ "&formCode="+ $('#formCode').val()
				+ "&userId="+ $('#userId').val()
				+ "&eventAction=getExperimentsPerGroup"
				+ "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "getExperimentsPerGroup",
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
						
				onElementDataTableApiChange('experimentsForGroup');
			}
			hideWaitMessage();
		},
		error : handleAjaxError
	});
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

function updateTraining(domId){
	var table_ = $('#'+domId).DataTable();
	var ids = [];//table_.$('input[type="checkbox"]').val();
	table_.$('input[Type="checkbox"]').each(function (index) {
    	if($(this).prop('checked')) {
    			ids.push($(this).val());
    		}
	});
	var materialList = ids.toString();
	if(materialList!=""){

		var stringifyToPush = {
				code : 'materialListNonFamiliar',
				val : materialList,
				type : "AJAX_BEAN",
				info : 'na'
		};
		var allData = getformDataNoCallBack(1);
		allData = allData.concat(stringifyToPush);
	     
	    //url call
	    var urlParam =
	        "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&eventAction=" + 'trainedUser' + "&isNew=" + $('#isNew').val();

	    var data_ = JSON.stringify({
	        action: "trainedUser",
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
	        	onElementDataTableApiChange(domId);
	        },
	        error: handleAjaxError
	    });	
	}
}

function enableCompositionDetailsButton(domId){//fixed bug 8953
	try{
	table = $('#' + domId).DataTable();
	var columnInd = getColumnIndexByColHeader(domId,"Row Type");
	$('#'+domId+'_dataTableStructButtons button.dataTableApiOptional1').addClass('authorizationDisabled');
	table.rows().eq(0).each( function ( index ) 
	{
		cell = table.cell({row: index, column: columnInd}); 
	    node = cell.node();			    
	    $input = $(node).find('input');
	    var cdata = cell.data();
	    if (checkIfJSON(cdata)) // check if json or not
	    { 
	    	cellObj = funcParseJSONData(cdata,true);					    	
	    	if(cellObj.hasOwnProperty("displayName")){
	    		var _tObj = cellObj.displayName;
	    		var val = _tObj[0].ID;
	    		if(val == undefined){
	    			val = _tObj;
	    		}
	    		if(val == 'Premix Recipe' || val == 'Recipe' || val == 'Premix Material' || val == 'Step (Premix) material'){
	    			$('#'+domId+'_dataTableStructButtons button.dataTableApiOptional1').removeClass('authorizationDisabled');
	    			}
	    		}
	    }
	});
	}
	catch(e){
		$('#'+domId+'_dataTableStructButtons button.dataTableApiOptional1').removeClass('authorizationDisabled');
	}
}
/*
 * 04012021 kdvoyashov commented this call because put to yes/no 
	* 						to the favorite field inside the json on fly by using formid in the ..._dtm views and changing it in the map and put to the json
 */
//function favoritCheckBoxHandle(domId) {
//	/*
//	 * kd 14122020 check if formCode equals SpreadsheetMain or if formCode equals Main then check if there is a cell editableSmartCellParent class checkBox
//	 */
//	
//	if ($('#formCode').val() == "SpreadsheetMain" || $('[id="' + domId + '"] tbody tr').find('td.editableSmartCellParent').length > 0 ||
//			$('[id="' + domId + '"] tbody tr').find('i.fa').length > 0) {
//    	var allData = getformDataNoCallBack(1);
//		// url call
//		var urlParam = "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val()
//				+ "&eventAction=getFavoriteSpreadsheet&isNew=" + $('#isNew').val();
//
//		var data_ = JSON.stringify({
//			action : "doSave",
//			data : allData,
//			errorMsg : ""
//		});
//		// call...
//		$.ajax({
//			type : 'POST',
//			data : data_,
//			url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
//			contentType : 'application/json',
//			dataType : 'json',
//
//			success : function(obj) {
//				if (obj.errorMsg != null && obj.errorMsg != '') {
//					displayAlertDialog(obj.errorMsg);
//					hideWaitMessage();
//					} else if (obj.data[0].val.toString() != null && obj.data[0].val.toString()!=""){
//						var results = obj.data[0].val.toString();
//						selectedTable = $('#' + domId).DataTable();
//						var columnInd = getColumnIndexByColHeader(domId,"Favorite");
//						var favRows = results.split("-");
////						for (var i = 0; i < rows.length; i++) {
//							//selectedTable.$('input[type="checkbox"][value="'+resultsArray[i]+'"]').prop('checked', true);
//							//$('#'+domId+'_col_favorite_row_'+rows[i]).prop('checked',true);
//							
//						selectedTable.rows().eq(0).each( function ( index ) 
//						{
//							var row = selectedTable.row( index );	
//						    var rowData = row.data();
//						    var rowID = rowData[0];
//						    for (var i = 0; i < favRows.length; i++) 
//						    {
//							    if(favRows[i] == rowID)
//								{
//									    var cell = selectedTable.cell({row: index, column: columnInd}); 
//								    var cdata = cell.data();
//								    var cnode = cell.node();
//								    if ($('[id="' + domId + '"] tbody tr').find('i.fa').length > 0){
//								    	var $input = $(cnode).find('i');
//									    
//									    if($input.length > 0)
//										{
////						    				$input.prop('checked',true);
////						    				$input.prop('checked',false);
//									    	$(cnode).find('i').removeClass('fa fa-star-o');
//									    	$(cnode).find('i').addClass('fa fa-star');
//									    	$(cnode).find('i').css("color","yellow");
//									    	$(cnode).find('i').attr('value','yes');
//									    	dtExt_updateCellFilterData(domId, index, columnInd, "yes");
//										}
//									    else // node is undefined when deferRender prop used by table, then we need to update data inside json object 
//									    {	    				    	
//										    var cellObj = {};
//										    if (checkIfJSON(cdata)) // check if json or not
//										    {
//										    	cellObj = funcParseJSONData(cdata,true);
//										    	cellObj["displayName"] = "yes";
//										    	cell.context[0].aoData[index]._aData[columnInd] = JSON.stringify(cellObj);
//										    	dtExt_updateCellFilterData(domId, index, columnInd, "yes");
//									    	}
//									    }
//								    } else {
//								    	var $input = $(cnode).find('input');
//								    
//									    if($input.length > 0)
//										{
//						    				$input.prop('checked',true);
//	//					    				$input.prop('checked',false);
//									    	dtExt_updateCellFilterData(domId, index, columnInd, "1");
//										}
//									    else // node is undefined when deferRender prop used by table, then we need to update data inside json object 
//									    {	    				    	
//										    var cellObj = {};
//										    if (checkIfJSON(cdata)) // check if json or not
//										    {
//										    	cellObj = funcParseJSONData(cdata,true);
//										    	cellObj["displayName"] = "1";
//										    	cell.context[0].aoData[index]._aData[columnInd] = JSON.stringify(cellObj);
//										    	dtExt_updateCellFilterData(domId, index, columnInd, "1");
//									    	}
//									    }
//								    }
//								    break;
//							    }
//							}
//						});
//					}
//				},
//				error : handleAjaxError
//			});
//	}
//}