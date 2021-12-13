/**
 * 
 * @returns form path info
 */
function getFormPathInfo(scopeElements) {
	if(scopeElements!==undefined && scopeElements.length>0){
		return  scopeElements.find('#formPathInfo').val();
	}
	return  $('#formPathInfo').val();
}

/**
	If STATUS_ID field is completed than disable all other inputs
 */
function generalBL_statusIdCompleted() {
    if ($('#STATUS_ID option:selected').text() == 'Completed' || $('#STATUS_ID option:selected').text() == 'Approved') {
    	if ($("#PROTOCOLTYPE_ID option:selected").text() == 'Parametric' && $('#STATUS_ID option:selected').text() == 'Completed'){
    		generalBL_disablePage(['STATUS_ID','reasonForChange','steps','action','selfTests','workups','feedbackHistory','updateVersion','conclussion'], true);
    	} else {
    		generalBL_disablePage(['STATUS_ID','reasonForChange','steps','action','selfTests','workups','feedbackHistory','updateVersion'], true);
    	}
    
    }
}

/**
If INSTRUMENTSTATUS_ID field is disabled then disable all other inputs
*/
function generalBL_statusIdDisabled() {
    if ($('#STATUS_ID option:selected').text() == 'Disabled') {
        generalBL_disablePage();
    }
}



/**
If MAINTCALSTATUS_ID field is disabled then disable all other inputs
*/
function generalBL_statusIdDoneCanceled() {
    if ($('#STATUS_ID option:selected').text() == 'Done' || $('#STATUS_ID option:selected').text() == 'Cancelled' || $('#STATUS_ID option:selected').text() == 'Obsolete') {
        generalBL_disablePage();
    }
}

/**
If there are some inventories under experiment that are not familiar then disable all other inputs

function generalBL_inventoryNotFamiliar() {
    if ($('#familiarity').val() == '0') {
        generalBL_disablePage();
    }
}*/

/**
 * tabId - tabId string
 * isFlagUp - if true and the flag is up this function return true and change flag to down
 */
function checkTabClickFlag(tabId,isFlagUp) { // this function add for fixing bug - 7908
	var formUpToDown = false;
	try { 
		if($('[aria-controls="' + tabId + '"]').length > 0) {
			var $tab_ = $('[aria-controls="' + tabId + '"]');
			if(isFlagUp) {
				if($tab_.attr('RENDER_ON_CLICK_FLAG') == '1') {
					$tab_.attr('RENDER_ON_CLICK_FLAG','0');
					formUpToDown = true;
					console.log("checkTabClickFlag flag was up now it is down formUpToDown=" + formUpToDown);
				}
			} else {
				$tab_.attr('RENDER_ON_CLICK_FLAG','1');
				console.log("checkTabClickFlag flag up");
			}
		} else {
			console.log("checkTabClickFlag tabId " + tabId + " not found");
		}
	} catch(e) {
		console.log("checkTabClickFlag error");
	}
	return formUpToDown;
} 


function initFormSaveDisplayButtons() {
	 // show/hide save/remove display - by customer
    if($('#formCode').val() == 'Maintenance') {
    	$('.mainSaveFormAndDefinitionBtn').css('display', 'none');
    	$('.mainSaveDefinitionBtn').css('display', 'none');
    }if($('#formCode').val() == 'Document' || $('#formCode').val() == 'InstrumentRef' 
    	|| $('#formCode').val() == 'EquipmentRef' || $('#formCode').val() == 'Component'
    	|| $('#formCode').val() == 'OperationType' || $('#formCode').val() == 'Compound'
    	|| $('#formCode').val() == 'PeakOfInterest' || $('#formCode').val() == 'SampleDataRef'
    		|| $('#formCode').val() == 'ExtendedParamMonit'
    		|| ($('#formCode').val().indexOf('Wu') == 0 
    				&& $('#formCode').val().lastIndexOf('Ref') == $('#formCode').val().length - 'Ref'.length)){
    	$('.popupSaveDefinitionBtn').css('display', 'none'); 
    } if($('#formCode').val() == 'SaveReport') {
    	$('.mainSaveFormAndDefinitionBtn').css('display', 'none');
    	$('.mainSaveDefinitionBtn').css('display', 'none');
    	$('.popupSaveDefinitionBtn').css('display', 'none');
    	//$('#saveButton').css('width', '100px');
    	//$('#closeButton').css('width', '100px');
    	$("#reportNameList").attr("onchange", "doChosenChange();");
    	$('#save_').removeAttr('onclick');
		$("#save_").attr("onclick", "saveReport();");
    	//$('#saveAsButon').css('width', '100px');
    	setTimeout(function () {
    		$('#pageTitle').css('font-size', '18px');
    		$('#pageTitle').parent().css('width', '100%');
    	},100);
    }
    if($('#formCode').val() == 'ExpAnalysisReport' || $('#formCode').val() == 'ExperimentReport') {
    	if($('#nameId').val()!="-1" && $('#nameId').val()!=""){
    		$('.mainSaveFormAndDefinitionBtn').css('display', 'none');
    	}
    	else{
    		$('.mainSaveFormAndDefinitionBtn').removeAttr('onclick');
    		$(".mainSaveFormAndDefinitionBtn").attr("onclick", "doSave(saveRulesTableByUserId(),'SAVE_FORM_AND_USER_SETTINGS');");
    	}
    	$('.mainSaveDefinitionBtn').css('display', 'none');
    	$('#searchDesign').css('width', '152px');
    	$('#bSaveDesign').css('width', '152px');
    	if($('#formCode').val() == 'ExpAnalysisReport'){
    		setReportTitle();
    		}
    	else{
    		var repName = $('#reportName').val();
       		if(repName != null && repName != ""){
       			$('#pageTitle').html("Reaction and Results Analysis: "+$('#reportName').val());
       		
       		}
       		else{
       			$('#pageTitle').html("Reaction and Results Analysis");
       		}
    	}
    }
    if($('#formCode').val() == 'SearchReport') {
	    $('.mainSaveFormAndDefinitionBtn').css('display', 'none');
		$('.mainClearSearchBtn').css('display', 'inline');
		$('#useLoginsessionidScopeFlag').val("1");
    }
    if($('#formCode').val() == 'SampleMain'){
    	$('.mainSaveDefinitionBtn').css('display', 'none');
    }
    if($('#formCode').val() == 'MultiClone'){
    	$('.popupSaveDefinitionBtn').css('display', 'none');
    }
    if($('#formCode').val() == 'Split'){
    	$('.popupSaveDefinitionBtn').css('display', 'none');
    }
    if($('#formCode').val() == 'MultiAddRows'){
    	$('.popupSaveDefinitionBtn').css('display', 'none');
    }
	if ($('#formCode').val() == 'ColumnsDefinition') {
		$('.popupSaveDefinitionBtn').css('display', 'none');
	}
	if ($('#formCode').val() == 'CopyDesignStep') {
		$('.popupSaveDefinitionBtn').css('display', 'none');
	}
	if ($('#formCode').val() == 'ReportTable') {
		$('.popupSaveDefinitionBtn').css('display', 'none');
	}
	if ($('#formCode').val() == 'ReportDesignExp') {
		initDesignbuttons();
	}
	if($('#formCode').val() == 'SaveSpreadsheetAs') {
    	$('.popupSaveDefinitionBtn').css('display', 'none');
	}
	if($('#formCode').val() == 'NoPermission'){
		$('.mainSaveFormAndDefinitionBtn').css('display', 'none');
		$('.breadcrumbs-container > *').css('display', 'none');
	}
	if($('#formCode').val() == 'SampleMain'){
		var url = window.location.href;
	    var isStructAsPopup =url.indexOf('&isStructAsPopup=1') > 0;
	    if(isStructAsPopup){
	    	//$('.text-right').after($('.submit-button-row'));
	    	$('#popupButton').append($('.submit-button-row'));
	    	$('#createRequestButton').css('margin-left','50px');
	    	$('#saveAndOpen_').css('margin-left','auto');
	    	$('#saveAndClose_').css('margin-left','auto');
	    	$('#close_back').css('margin-left','auto');
	    	
	    	var calcRemoveHigh = 0;
	    	calcRemoveHigh = $('.top-bar').height() + $('#dropDownMenuBar').height() + $('.breadcrumbs-container').height();
	    	
	    	$('.top-bar').css('display', 'none');
		    $('#dropDownMenuBar').css('display', 'none');
		    $('.breadcrumbs-container').css('visibility', 'hidden');
		    
		    if($('.innerTD div:first').length == 1) {
		    	 calcRemoveHigh = (calcRemoveHigh > 100)?100:calcRemoveHigh; // some how system user with all the info need this line in order to see the save and close buttons
		    	 $('.innerTD div:first').css('height', $('.innerTD div:first').height() + calcRemoveHigh + "px");
				 console.log("isStructAsPopup - remove top-bar,dropDownMenuBar,breadcrumbs-container and add their height to innerTDCalcDiv");
		    }
		 }
	}
	if($('#formCode').val() == 'ViewSpreadsheet'){
		$('.popupSaveDefinitionBtn').css('display', 'none');
	}
    if($('#formCode').val() == 'CopyFunctionTable'){
		$('.popupSaveDefinitionBtn').css('display', 'none');
	}
    if($('#formCode').val() == 'ExperimentGroup'){
		$('.popupSaveDefinitionBtn').css('display', 'none');
	}
    // done!
}

function initNavigationTreeButton() {
	if (($('#formCode').val() == "Project" && $('#isNew').val() != '1') || $('#formCode').val() == "SubProject" || $('#formCode').val() == "SubSubProject"
	|| $('#formCode').val() == "ExperimentAn" || $('#formCode').val() == "Experiment" || $('#formCode').val() == "ExperimentCP" ||  // develop ExperimentCP "Continuous Process"
    		$('#formCode').val() == "ExperimentFor" || $('#formCode').val().slice(0,12) =='ExperimentPr'
		|| $('#formCode').val() == "Step"||$('#formCode').val() == "StepFr"
			||$('#formCode').val() == "Action"
				|| $('#formCode').val().indexOf('STest')==0||$('#formCode').val()=='SelfTest'||$('#formCode').val().indexOf('Workup')==0)
	{
		$('.mainNavigationTreeBtn').css('display', 'block');
		}
}

/**
 * do on init of form
 * 
 * @returns
 */
function initForm() {
	console.log("start initForm");
	
	var _formCode = "";
	if($('#formCode').length > 0) {
		_formCode = $('#formCode').val();
	}
	
	
	if(_formCode == 'Document' || 
	   _formCode == 'UserGuidePool' || 
	   _formCode == 'Component' || 
	   _formCode == 'Component' || 
	   _formCode == 'OperationType' || 
	   _formCode == 'SelfTestType' || 
	   _formCode == 'PrManualResultRef') {
	   $('#save_').attr('onclick', 'doSaveUpload();');
	}
	
	if(_formCode == "Main")
	{
		$('.mainPlusMinusBtn').css('display', 'inline');
	}
 
	if(_formCode != 'SelfTest' && _formCode != 'ExperimentAn' && _formCode != 'ExperimentFor') { // workaround for bug when download the label after creating samples and back to the self test -> because of the download we not getting to "webix.ui({..." code and the table is not displayed in the screen (without error in the log)- we move this code to ElementWebixGeneralImp.js &  ElementWebixAnalytCalcImp.js & ElementWebixFormulCalcImp.js after the webix element is load
		//print label on load if we have data in _labelCode / _labelData (passed from previous form) //23022020 kd added "&& _formCode != 'ExperimentAn'" as workaround for fix bug-7963. See onAfterLoad in ElementWebixAnalytCalcImp.js 
		if ( $('#_global_labelCode').length > 0 && $('#_global_labelCode').val() != "" && $('#_global_labelCode').val() != 'undefined' && $('#_global_labelData').length > 0 && $('#_global_labelData').val() != "" && $('#_global_labelData').val() != 'undefined')
		{
			outPutLabel("_global",$('#_global_labelCode').val(),$('#_global_labelData').val());
		}
	}

    if ((_formCode == "Project") || (_formCode == "SubProject") || (_formCode == "SubSubProject")) {
        generalBL_statusIdCompleted();
        if(_formCode == "SubSubProject"){
        	var targetId = $('[aria-controls="SpecificationTab"]').attr('aria-labelledby');
        	$('[id="' + targetId + '"]').parent().css('display', 'none');
        }
        if(_formCode == "SubProject"){
        	if($('#project_Type_Name').val()=="Formulation"){
        		$('#SUBPROJECTTYPE_ID').attr("onchange","onChangeTypeFormulSP();");
        	}
        }
    }
    if ((_formCode == "InvItemInstrument")) {
        generalBL_statusIdDisabled();
        $('#STATUS_ID').change(function(){
			 var laststatusId = $('#STATUS_ID').attr('lastvalue');
			 $('#lastStatusName').val($('#STATUS_ID option[value='+laststatusId+']').text());
		});
    }
    if ((_formCode == "InvItemMaintenance") || (_formCode == "InvItemCalibration")) {//||(_formCode == "Request")
        generalBL_statusIdDoneCanceled();
        $('#newButton').css('display', 'none'); // hide the 'new' button
        $('#saveButton').css('margin-left', ''); // fix style after the hide of  the 'new' button
    }if ((_formCode == "Component")) {
    	
        $('#impurity').change(function () {
            if (this.checked) {
               $('#standardIncluded').prop('checked', true);
            }
        });
     }else if (_formCode == "templateSearch") {
    	
        if( $('#templateType').val() == 'Procedure (Organic)'){
        	$('#templates_criteriaCatalogItem').prop("disabled", true);
        }
  	  } else if (_formCode == "Training") {
        
  		 //TODO: ab 26/02/2020 check for improve
  		$('input').css('display', 'none');
        var $table = $('.ui-widget-content table:last');
        var $closestDiv = $table.closest('div');
        
        var msgDiv = '<div style="text-align:center;color: grey;font-size: 20px;font-family: Sans-Serif;margin-top: 30px;margin-bottom: 30px;">' +
				        'I have been instructed on this ' +
				        $('#type').val() + '<br>' +
				        'and I am familiar with it.' +
				        '</div>';
        $closestDiv.append(msgDiv);
        $('#save_').html('Approve');
        $('#close_back').html('Cancel');

        $('#pageTitle').closest('table').closest('tr').remove();
        $table.css('display','none');
        $closestDiv.css('height', '');
    } else if (_formCode == "InvItemColumn") {
    	$('input[name="inUse_disabled"]').change(function(){
            if ($('input[name="inUse_disabled"]:checked').val() == 'Disabled'){
            	validateColumnInUse();
            }
    	   });
        $('#newButton').css('display', 'none');
        $('#saveButton').css('margin-left', '');
    } else if (_formCode == "InvItemBatch") {
        $('input[name="inUseDepleted"]').change(function () {
            if ($('input[name="inUseDepleted"]:checked').val() == 'Depleted') {
            	depletedBatch();
            }
        });
        var materialProtocolType=$('#materialProtocolType').val();
        if(materialProtocolType!="Chemical Material"){
     	   $('.ChemicalMaterial').css('display','none');
        }
        document.getElementById("quantity").onblur = function() {depletedBatch()};
      
        //script to enable only one selection
        /*setTimeout(function () {*/
	        /*
        },10);*/
        //$('#newButton').css('display', 'none');
        $('#saveButton').css('margin-left', '');
        $("#saveButton").removeAttr('onclick');
		$("#saveButton").attr("onclick", "doSaveBatch('Reload');");
		
    } 
    else if (_formCode == "InvItemMaterial"){
    	$('#PROJECT_ID_chosen .default').css("width","100%");
    	$('#MANUFACTURER_ID_chosen').css("width","150px");
    	//onChangeAjax('alternativeGroup');
    	document.getElementById("alternativeGroup").onblur = function(e) {
    		onChangeAjax('alternativeGroup',true); // call onChangeAjax with avoidPleaseWait true because without this the click of the the buutons (save for example) will not fire (probably because of the mask inside and the focus issue (?)) 
    	};
    }
    else if (_formCode == "InvItemMaterialFr"){
    	//avoid removing Formulation (also set as default in the form builder)
    	forceMandatoryDDLValues('MATERIALTYPE_ID', ['FORMULATION']);
    }
    else if (_formCode == "InvItemMaterialPr"){
    	//avoid removing Premix (also set as default in the form builder)
    	forceMandatoryDDLValues('MATERIALTYPE_ID', ['PREMIX']);
    }
    else if (_formCode == "InvItemMaterialsMain") 
    {
    	//hide lowerTable_Parent
    	//$('#upperTable_Parent').css('display', 'none');
//        $('#lowerTable_Parent').css('display', 'none');
        
    	$('#upperFilters').css('display', 'none');
        //move upperFilters into upperTable_wfDiv
        $('#upperTable_wfDiv').append($('#upperFilters'));
        
    /*    $('#upperTable_structCatalogItem option').each(function () {
            //Add the string 'InvItem' to the value of the upper datatable's select value
            // because forms which are inventory type we declare the form as InvItemForm
            $(this).text($(this).text().replace("InvItem", ""));
        });
     */   
        //hide lowerTable_Parent if Column
     /*  $('#upperTable_structCatalogItem').change(function () {
           if($('#upperTable_structCatalogItem option:selected').text() =='Column'){
        	   $('#lowerTable_Parent').css('display', 'none');
        	   $('#lowerTable_Parent').css('visibility', 'hidden');
           }else{
        	   $('#lowerTable_Parent').css('display', '');
        	   $('#lowerTable_Parent').css('visibility', '');
           }
        });
       */
    } else if (_formCode == "RecipeFormulation") {
   	 
   
        $('#newButton').css('display', 'none');
    	$('#compositions_Parent').css('display', 'none');
    	//move upperFilters into upperTable_wfDiv
        $('#compositions_wfDiv').append($('#upperFilters'));
       
 } else if (_formCode == "CompositionSearch") {
   	 
   
    	
    	$('#recipes_Parent').css('display', 'none');
    	//move upperFilters into upperTable_wfDiv
        $('#recipes_wfDiv').append($('#upperFilters'));

  	
  } else if (_formCode == "InvItemRecipesMain") {
   	 
   
    	
    	$('#recipeFormulation_Parent').css('display', 'none');
    	//move upperFilters into upperTable_wfDiv
        $('#recipeFormulation_wfDiv').append($('#upperFilters'));

  	
    } else if (_formCode == "InvItemInstrumentsMain") {
    	 
    	//hide lowerTable_Parent
    	$('#upperTable_Parent').css('display', 'none');
    	
    	//move upperFilters into upperTable_wfDiv
        $('#upperTable_wfDiv').append($('#upperFilters'));

    } else if (_formCode == "InvItemSamplesMain") {
    	// $('.elementrange input').find('input:first').prevObject.attr('style', 'width:10% !important;');
    	// $('.elementrange input').find('input:last').prevObject.attr('style', 'width:10% !important;');
    } else if (_formCode == 'Split') {
        $('#save_').html('OK');
        $('#save_').attr('onclick', 'doSplit();');
        $('#close_back').html('Cancel');
        $('#pageTitle').closest('table').closest('tr').remove();
        //$('.ui-widget-content table:last').closest('div').css('height', ''); //ab 07052019 cause to y-scroll to be displayed
    } else if (_formCode == 'MultiClone') {
        $('#save_').html('OK');
        $('#save_').attr('onclick', 'doMultiClone();');
        $('#close_back').html('Cancel');
        $('#pageTitle').closest('table').closest('tr').remove();
        //hide the in process label (it has no id so we use font[color=red] to get this dom element)
        $("font[color=red]").css('display', 'none');
        $('.ui-widget-content table:last').closest('div').css('height', '');
    } else if (_formCode == 'MultiAddRows') {
        $('#save_').html('OK');
        $('#save_').attr('onclick',"dataTableAddRow('"+$('#tableId').val()+"',true);");
        $('#close_back').html('Cancel');
        $('#pageTitle').closest('table').closest('tr').remove();
        //hide the in process label (it has no id so we use font[color=red] to get this dom element)
        //$("font[color=red]").css('display', 'none');
        $('.ui-widget-content table:last').closest('div').css('height', '');
    } else if (_formCode == 'ParamMonitoring') {
        document.getElementById("ltime").childNodes[0].style.display = 'none'; // hide the asterisk of the 'Time' label
    } else if (_formCode == 'MaterialRef') {
        var tableType = $('#tableType').val();
        if (tableType == 'Reactant') {
            setTimeout(function () {
                $('#pageTitle').html('Add\/Edit Reactant');
            }, 100);
            $(parent.document).find('#prevDialog')
                .parent().find('span.ui-dialog-title').html('Reactant');
            $('#row42,#row43,#row44,#row45,#row46,#row47').css('display', 'none');
        } else if (tableType == 'Solvent') {
            setTimeout(function () {
                $('#pageTitle').html('Add\/Edit Solvent');
            }, 100);
            $(parent.document).find('#prevDialog')
                .parent().find('span.ui-dialog-title').html('Solvent');
            $('#row24,#row25,#row26,#row27,#row32,#row33,#row36,#row37,#row44,#row45,#row46,#row47').css('display', 'none');
        } else if (tableType == 'Product') {
            setTimeout(function () {
                $('#pageTitle').html('Add\/Edit Product');
            }, 100);
            $(parent.document).find('#prevDialog')
                .parent().find('span.ui-dialog-title').html('Product');
            $('#row10,#row11,#row12,#row13,#row14,#row15,#row16,#row17,#row18,#row19,#row20,#row21,#row22,#row23').css('display', 'none');
            $('#row24,#row25,#row26,#row27,#row32,#row33,#row36,#row37,#row38,#row39,#row40,#row41,#row43,#row44').css('display', 'none');
        }      
        
        $('#limitingAgent').change(function () {
            if (this.checked) {
                $('#equivalent').val('1');
            }
        });
    } 
    else if(_formCode =='Request'){
    	$("#newButton").removeAttr('onclick');
    	$("#newButton").attr("onclick", "createNewExpFromRequest();");
        $('#newButton').html('New Experiment');
        $("#saveAndSendButton").attr("onclick", "doSaveBaseRequestNoConfirm('save_and_close');");
		/*if($("#REQUESTSTATUS_ID option:selected").text()=="Planned"){//it's the parent/basic request
			$("#saveButton").removeAttr('onclick');
			$("#saveButton").attr("onclick", "doSaveBaseRequestNoConfirm('Reload');");
		}*/
    }
    else if(_formCode =='RequestMain'){
		$("#saveAndClose_").removeAttr('onclick');
	    $("#saveAndClose_").attr("onclick", "doSaveMainRequest('save_and_close');");
    }
    else if(_formCode =='SampleSelect'){
		if($(parent.document).find('#formCode').val()=="InvItemBatch"){//it's a sampleselect opened from batch
			$("#save_").removeAttr('onclick');
			$("#save_").attr("onclick", "doSaveSampleSelectBatchCheck('Close');");
		}
		else if($(parent.document).find(_formCode.slice(0,10) =='Experiment')){//it's a sampleselect opened from experiment
			$("#save_").removeAttr('onclick');
			$("#save_").attr("onclick", "doSaveSampleSelectDestLabCheck('Close');");
		}
    }
    else if (_formCode === 'MassBalanceRef') {
    	// TODO: check if form in use
    	$("#productMW").val($(parent.document).find('#productMW').val());
//    	$("#PRODUCTMWUOM_ID").val($(parent.document).find('#PRODUCTMWUOM_ID').val());
    	$("#equivalentPerMole").val($(parent.document).find('#equivalentPerMole').val());
//    	$("#PERMOLEUOM_ID").val($(parent.document).find('#PERMOLEUOM_ID').val());
    	$("#limitingReactantMoles").val($(parent.document).find('#limitingReactantMoles').val());
//    	$("#REACTANTMOLESUOM_ID").val($(parent.document).find('#REACTANTMOLESUOM_ID').val());
    } else if (_formCode === 'ExpParamsCrRef') {
    	$("#parentInitialWeight").val($(parent.document).find('#initialWeight').val());
    	$("#parentArea").val($(parent.document).find('#area').val());
    	$("#parentDensity").val($(parent.document).find('#literatureDensity').val());
        $("#parentCalDensity").val($(parent.document).find('#calculatedDensity').val());
    } else if (_formCode == 'WuCryMixDefineRef') {
    	disableWorkupRes();
    	var tableType = $('#tableType').val();
        if (tableType == 'MotherLiquor') {
        	$("#initialAmount").val($(parent.document).find('#quantity').val());
        	$("#INITIALAMOUNT_UOM").val($(parent.document).find('#QUANTITY_UOM').val());
        	
            setTimeout(function () {
                $('#pageTitle').html('Add\/Edit Mother Liquor');
            }, 100);
            $(parent.document).find('#prevDialog')
                .parent().find('span.ui-dialog-title').html('Mother Liquor');
          
        } else{
        	$("#initialAmount").val($(parent.document).find('#initialAmount').val());
        	$("#INITIALAMOUNT_UOM").val($(parent.document).find('#INITIALAMOUNT_UOM').val());
    	}
    }
    else if (_formCode == 'WuFiltraSqueezRef') {
        var tableType = $('#tableType').val();
        if (tableType == 'LastSqueezing') {
            setTimeout(function () {
                $('#pageTitle').html('Add\/Edit Last Squeezing');
            }, 100);
            $(parent.document).find('#prevDialog')
                .parent().find('span.ui-dialog-title').html('Last Squeezing');
          
        } 
    }
    else if(_formCode == 'WuFiltraFeedRef'){
    	getSequentialNumber('feeding','Cycle', 'cycle');
    }
    
    else if(_formCode == 'WuFiltraWashingRef'){
    	getSequentialNumber('washing','Cycle','washingNumber');
    }
    else if (_formCode == 'WorkupWashExtract') {
        var workupType = $('#workupType').val();
        if (workupType == 'Washing') {
            /*setTimeout(function () {
                $('#pageTitle').html('Workup Washing');
            }, 100);*/
            $(parent.document).find('#prevDialog')
                .parent().find('span.ui-dialog-title').html('Workup Washing');
          
        } 
        else if (workupType == 'Extraction') {
            /*setTimeout(function () {
                $('#pageTitle').html('Workup Extraction');
            }, 100);*/
            $(parent.document).find('#prevDialog')
                .parent().find('span.ui-dialog-title').html('Workup Extraction');
          
        } 
        
    }else if (_formCode == 'WuDistStartMixRef') {
    	disableWorkupRes();
        $("#initialAmount").val($(parent.document).find('#initialAmount').val());
        $("#INITIALAMOUNT_UOM").val($(parent.document).find('#INITIALAMOUNT_UOM').val());
        
    }else if(_formCode == 'WuFeedMaterialRef'){
    	disableWorkupRes();
    	//$("#finalTemperature").val($(parent.document).find('#finalTemperature').val());
	}
    else if(_formCode == 'Template' || _formCode == 'TemplateMain')
    {
    	if(_formCode == 'Template'){
    		$('#STATUS_ID').change(function(){
    			var laststatusId = $('#STATUS_ID').attr('lastvalue');
    			$("#LASTSTATUS_ID").val(laststatusId);
    		});
     		  
    	}
    	generalBL_statusIdDoneCanceled();
    	generalBL_statusIdCompleted();
    	//$('#newButton').addClass('disablePage');
    }
    else if(_formCode == 'FormulationPropRef')
    {    	
		$("#save_").removeAttr('onclick');
		$("#save_").attr("onclick", "doSaveFormulationPropRef('Close');");
    }
   
    else if ((_formCode == "ExperimentAn") || (_formCode == "Experiment") || (_formCode == "ExperimentStb") || //add ExperimentStb for "Taro develop"
    		(_formCode == "ExperimentFor") || (_formCode.slice(0,12) =='ExperimentPr') || (_formCode == "ExperimentCP")) // develop ExperimentCP "Continuous Process"
    {
    	/*if((_formCode != "ExperimentAn")){
    		generalBL_statusIdCompleted();
    	}*/
    	
    	
    	
        $('#STATUS_ID').change(function(){
			$("#LASTSTATUS_ID").val( $('#STATUS_ID').attr('lastvalue'));
			var laststatusId = $('#STATUS_ID').attr('lastvalue');
			 var laststatusName = $('#STATUS_ID option[value='+laststatusId+']').text();
			 if( $("#STATUS_ID option:selected").text()=="Active" && laststatusName=="Approved"){
				 $('#reasonForChange').val('');
			 }
		});
        if((_formCode == "Experiment") || (_formCode == "ExperimentCP")){// develop ExperimentCP "Continuous Process"
        	$("#saveButton").removeAttr('onclick');
        	$("#saveButton").attr("onclick", "checkSpreadsheetFullScreenInExperiment(doSaveWithConfirmCharSample,'Reload')");
        	
    		initExperimentMassBalanceTab("webixMassBalanceTable","");
        	initExperimentMassBalanceTab("webixMassBalanceTable2","2");
        	initExperimentMassBalanceTab("webixMassBalanceTable3","3");
        	
        	var oneIsChecked = $('.experimentDefaultMassBalanceClass').is(':checked');
        	if(oneIsChecked)
        	{
        		$('.experimentDefaultMassBalanceClass:not(:checked)').prop('disabled',true);
        	}
        	
        	//lower the spreadsheet tab flag so that when clicking it, the spreadsheet would be reloaded
        	if(getActiveTabID() != "SpreadsheetTab") {
        		checkTabClickFlag('SpreadsheetTab');
        	}
        	
        	
        	$('#SPREADSHEETTEMPLATE_ID').attr("onchange","onChangeTemplate(this,$(this).attr('lastvalue'))");
        } 
        else if(_formCode == "ExperimentAn"){
        	$("#saveButton").removeAttr('onclick');
        	$("#saveButton").attr("onclick", "checkSpreadsheetFullScreenInExperiment(doSaveWithConfirmManualResults,'Reload')");
        	//lower the spreadsheet tab flag so that when clicking it, the spreadsheet would be reloaded
        	if(getActiveTabID() != "SpreadsheetTab") {
        		checkTabClickFlag('SpreadsheetTab');
        	}
        	if(getActiveTabID() != "SpreadsheetResultsTab") {
        		checkTabClickFlag('SpreadsheetResultsTab');
        	}
        	
        	$('#SPREADSHEETTEMPLATE_ID').attr("onchange","onChangeTemplate(this,$(this).attr('lastvalue'))");
        }
        else if(_formCode == "ExperimentFor")
        {
        	$("#saveButton").removeAttr('onclick');
        	$("#saveButton").attr("onclick", "checkSpreadsheetFullScreenInExperiment(doSaveExperimentFr,'Reload')");
        	initEditableTableOnReadyScript();
        	
        	if(getActiveTabID() != "SpreadsheetTab") {
        		checkTabClickFlag('SpreadsheetTab');
        	}
        	
        	$('#SPREADSHEETTEMPLATE_ID').attr("onchange","onChangeTemplate(this,$(this).attr('lastvalue'))");
        	
        	////Handle the overview tab
        	
        	//manage OverviewTab collapsible (open if we are OverviewTab else check flag for -> initFloatingButtonsPanel - on TABs click )
        	if(getActiveTabID() == "OverviewTab") {
        		openCollapsibleIframes();
        	} else {
        		checkTabClickFlag('OverviewTab');
        	}
        	
        	$('#searchCompositionVal').attr('data-placeholder','Choose '+$('#searchBy').val()+":");
        	
        	$('#searchBy').change(function(){
        		var tableType = $(this).val()=='Recipe / External Code'?"recipeSearch":"experimentSearch";;
        		$('#searchCompositionIcon_tableType').val(tableType);
        		$('#searchCompositionVal').attr('data-placeholder','Choose '+$(this).val()+":");
        	});
        	$('#lImportedComposition label').css('display','');
        	$('#lPlannedComposition label').css('display','');
        	/**
    		 * If the density/batch size value is being changed, the fields in the composition table should be calculated
    		 * */
    		var density = $('#density').val();
    		 $('#density').attr('oldvalue',density);
    		 document.getElementById("density").onblur = function(e) {
    			 if($('#density').val()!=""){
    				 $('#plannedCompositions').attr('lastChangeVal', $('#density').attr('oldvalue'));
    				 generalBL_elementDataTableClickEvent("plannedCompositions","calcComposition",[$('#formId').val(),"Composition","density","calcDensityOnChange"],$(this));
    			 } else {
    				 validateDensityRequired("1",'plannedCompositions');
    				 $('#density').attr('oldvalue',$('#density').val());
    				 return;
    			 }
    			 var countDecimals = 0;
    			 var inputVal = e.target.value;
    			 if(inputVal.toString().indexOf('.')<=0){
    				 countDecimals = 0;
    			 } else {
    				 countDecimals = inputVal.toString().split(".")[1].length || 0; 
    			 }
    			 if(countDecimals<2){
    				 inputVal = Number.parseFloat(inputVal).toFixed(2);
    				 $('#density').attr('realvalue',inputVal);
    				 $('#density').val(inputVal);
    			 }
				 $('#density').attr('oldvalue',$('#density').val());
    		 };
    		 document.getElementById("density").onkeypress = function(e) {
    			 var countDecimals = 0;
    			 var inputVal = $('#density').val();//e.target.value;
    			 if(inputVal == '') return;
    			 if(inputVal.toString().indexOf('.')<=0){
    				 countDecimals = 0;
    			 } else {
    				 countDecimals = inputVal.toString().split(".")[1].length || 0; 
    			 }
    			 if(countDecimals==3){//not allow to enter more than 3 digits after the decimal point
    				 /*inputVal = 
    				 $('#density').attr('realvalue',inputVal);
    				 $('#density').val(inputVal);*/
    				 return false;
    			 }
    		 };
    		 
    		 var batchSize = $('#batchSize').val();
    		 $('#batchSize').attr('oldvalue',batchSize);
    		 document.getElementById("batchSize").onblur = function() {
    			 if($('#batchSize').val()!=""){
    				 $('#plannedCompositions').attr('lastChangeVal', $('#batchSize').attr('oldvalue'));
    				 //calculate the 'planned for X batch' column and the 'planned for x formulation batch' column for each step
    				 generalBL_elementDataTableClickEvent("plannedCompositions","calcComposition",[$('#formId').val(),"Composition","batchSize","calcBatchSizeOnChange"],$(this));
    			 } else {
    				 setBatchSizeMandatory('1','plannedCompositions');
    			 }
    			 $('#batchSize').attr('oldvalue',$('#batchSize').val());
    		 };
    		 $('#BATCHSIZE_UOM').change(function(){
    			//calculate the  planned for x atch column in the planned comp and in the materials in each step and update the column title
    			generalBL_elementDataTableClickEvent("plannedCompositions","calcComposition",[$('#formId').val(),"Composition","batchSize","calcBatchSizeOnChange"],$('#batchSize').val());
    		 });
    				
    		 // open planned composition as default if isComposition = yes (if there are at least one row)
    		 if( $('#isComposition').length > 0 && $('#isComposition').val() == 'yes' ) {
    			 setTimeout(function () {
        			 $('.plannedCompositionsPane').find('.collapsible_button').click();
        		 },100);
    		 }
            
             $('#importedCompositionTable_Parent').css('overflow', 'auto hidden');
             $('#importedCompositionTable_Parent').css('min-width', '0'); 
             
             showHideDeleteStepIcon();
    	}else if(_formCode.slice(0,12) =='ExperimentPr'){
        	$("#saveButton").removeAttr('onclick');
        	$("#saveButton").attr("onclick", "checkNonFamiliarAndSave(doSave,'Reload');");
        }
        if(_formCode == 'ExperimentStb') { //"Taro develop"
//        	$("#saveButton").removeAttr('onclick'); // this was made for the pivot table it is commented for now
//    		$("#saveButton").attr("onclick", "doSaveExperimentStb('Reload');");
        	setTimeout(function () {
    	    	$("#LinkSample").focus();
    	    	$("#LinkSample").keyup(function(e) {
    	    		if(e.which == 13){ // qrcode scan will include enter as last char
    	    			searchScanExpStb(this);
    	    		}
    	    	});
        	},100);
        }
        
        
        
        //arrange the title
        $('#pageTitle').css('white-space','pre');
		
    }  
    else if (_formCode == "Project"){
    	$('#PROJECTTYPE_ID').change(function(){
    		if ($('#PROJECTTYPE_ID option:selected').text() == "Formulation"){
    			$('#planningRequired').prop("checked",true);
    			}
    		})
    }
    else if(_formCode == "StepFr")
    {
    	$("#saveButton").removeAttr('onclick');
    	//$("#saveButton").attr("onclick", "doSaveStepFr('Reload');");
    	$("#saveButton").attr("onclick", "checkNonFamiliarAndSave(doSaveStepFr,'Reload');");
    }
    else if(_formCode == "Action")
    {
    	$('[id="conditionsTable"] tbody tr:first td').click();
    }
    else if(_formCode == "FGSearchNNavigate") // kd 12082018
    {
    	$("#findQR").focus();
    	$('[name="searchOrNavigate"]').attr("onchange", "focusOnThis(this, 'findQR')");
    } 
    else if(_formCode == "SearchReport") 
    {
    	$('#imgAdvSearchFormSearch').append("<img src=\"../skylineFormWebapp/images/arrow_r.png\" class=\"arrows-search\"> "); //class=\"arrows-search2\"//   style=\"background-position:0px 0px; width:17px;left:0;height:12px;\"
    	$('#imgAdvSearch2FormSearch').append("<img src=\"../skylineFormWebapp/images/arrow_r.png\" class=\"arrows-search\"> ");
    	if($("#textBox").val() != "" || $('input[name="searchOrNavigate"]:checked').val() == "Molecule Search"){//search saved - the textBox is not empty 
    	 if($('input[name="searchOrNavigate"]:checked').val() == "Molecule Search"){
    		$('#chemdoodle').closest('tr').css('display', '');
    		 var topText = $("#textBox").closest('td').position().top;
    		 var topChem = $('#chemdoodle').closest('td').position().top;
    		 $('#chemdoodle').css('margin-top', (topText-topChem));
    		 }
    	 else{
    		 $('#chemdoodle').closest('tr').css('display', 'none');
    	 }
    	 searchAll();
    	 if($('#inventoryResults_value').val()!=''){//there are checked rows in the inventory result table
	   		 onChangeAjax('resentlyResults');
	   		 } 
    	}
    	else{
    		$("#searchType").val($("#searchType option:contains('Inventory')").val());
    		$("#searchType").trigger('chosen:updated');
    		//in case of a new search,search tables should be hidden 
    		$('#resentlyResults_Parent').css('visibility', 'hidden');
     	    $('#resentlyResults_Parent').css('display', 'none');
            $('#inventoryResults_Parent').css('visibility', 'hidden');
     	    $('#inventoryResults_Parent').css('display', 'none');
     	    $('#parentAdvanced2').css('display', 'none');
     	    $('#ladvancedSearch2').css('display', 'none'); 
    	    $('#chemdoodle').closest('tr').css('display', 'none');
    	}
    	
    	$('.content').css('display','none');
    	$("#ladvancedSearch").click(function () {
    		//if($("#searchType").val() != "Inventory"){
    			$('.content').slideToggle('slow', function() {
    	        if ($('.content').is(':hidden'))
    	        {
    	        	$('#imgAdvSearchFormSearch').children('img').attr("src", "../skylineFormWebapp/images/arrow_r.png");
    	        	//if ($('.advanced2').is(':hidden')){
						$('#generate').prop("disabled", false);
					//}
    	        }
    	        else
    	        {
    	        	$('#imgAdvSearchFormSearch').children('img').attr("src", "../skylineFormWebapp/images/arrow_d.png");
    	        	$('#generate').prop("disabled", true);
    	        }
    	    });
    		//}
    	});
    	$("#imgAdvSearchFormSearch").click(function () {
    		//if($("#searchType").val() != "Inventory"){
    			$('.content').slideToggle('fast', function() {
    				if ($('.content').is(':hidden'))
    				{
    					$('#imgAdvSearchFormSearch').children('img').attr("src", "../skylineFormWebapp/images/arrow_r.png");
    					//if ($('.advanced2').is(':hidden')){
    						$('#generate').prop("disabled", false);
    					//}
    	        	    }
    				else
    				{
    					$('#imgAdvSearchFormSearch').children('img').attr("src", "../skylineFormWebapp/images/arrow_d.png");
    					$('#generate').prop("disabled", true);
    					}
    				});
    	//	}	
    	});
    	
    	$('.advanced2').css('display','none');
    	$("#ladvancedSearch2").click(function () {
    		$('.advanced2').slideToggle('fast', function() {
    	        if ($('.advanced2').is(':hidden'))
    	        {
    	        	$('#imgAdvSearch2FormSearch').children('img').attr("src", "../skylineFormWebapp/images/arrow_r.png");
    	        	/*if ($('.content').is(':hidden')){
						$('#generate').prop("disabled", false);
					}*/
    	        }
    	        else
    	        {
    	        	$('#imgAdvSearch2FormSearch').children('img').attr("src", "../skylineFormWebapp/images/arrow_d.png");
    	        	//$('#generate').prop("disabled", true);
    	        }
    	    });
    	});
    	$("#imgAdvSearch2FormSearch").click(function () {
    		$('.advanced2').slideToggle('fast', function() {
    	        if ($('.advanced2').is(':hidden'))
    	        {
    	        	$('#imgAdvSearch2FormSearch').children('img').attr("src", "../skylineFormWebapp/images/arrow_r.png");
    	        	/*if ($('.content').is(':hidden')){
						$('#generate').prop("disabled", false);
					}*/
    	        }
    	        else
    	        {
    	        	$('#imgAdvSearch2FormSearch').children('img').attr("src", "../skylineFormWebapp/images/arrow_d.png");
    	        	//$('#generate').prop("disabled", true);
    	        }
    	    });
    	});
    	/*$('#searchType').change(function(){
    		if($("#searchType").val() == "Inventory"){
    			$('.content').slideUp();
    			$('#imgAdvSearchFormSearch').children('img').attr("src", "../skylineFormWebapp/images/arrow_r.png");
    			if ($('.advanced2').is(':hidden')){
					$('#generate').prop("disabled", false);
				}
    		}
    	});*/
    	
    	$("#textBox").keyup(function(e) {
    		if(e.which == 13 && $('input[name="searchOrNavigate"]:checked').val() != "Batch / Sample / Barcode Scan"){ // qrcode scan will include enter as last char
    			searchAll('generate');
    		}
    	});
    	$("#textBox").focus();
    	if($('input[name="searchOrNavigate"]:checked').val() != "Free Text"){
    		$("#advancedBorder").css('display', 'none');
		}
    	
    	$('input[name="searchOrNavigate"]').change(function(){
    		if ($('input[name="searchOrNavigate"]:checked').val() == 'Compound Search'){
    	    	 setTimeout(function () {$("#textBox").attr("placeholder", "Insert Material Name/ CAS Number/ External Code/ Synonyms").focus();},100);
    	    	$("#searchType").val($("#searchType option:contains('Inventory')").val());
    	    	//$("#searchType").prop("disabled", true);
    	        $("#searchType").trigger('chosen:updated');
    	        $('#chemdoodle').closest('tr').css('display', 'none');
    	        $("#advancedBorder").css('display', 'none');
    	    }else if($('input[name="searchOrNavigate"]:checked').val() == "Molecule Search"){
    	    	//$("#searchType").prop("disabled", true);
    	    	$("#searchType").val($("#searchType option:contains('Inventory')").val());
    	        $("#searchType").trigger('chosen:updated');
    	        $('#chemdoodle').closest('tr').css('display', '');
    	        var topText = $("#textBox").closest('td').position().top;
    	        var topChem = $('#chemdoodle').closest('td').position().top;
    	        $('#chemdoodle').css('margin-top', (topText-topChem));
    	        $("#advancedBorder").css('display', 'none');
    		}else if($('input[name="searchOrNavigate"]:checked').val() == "Batch / Sample / Barcode Scan"){
    			//$("#searchType").prop("disabled", true);
    			searchScan();	
    			$('#chemdoodle').closest('tr').css('display', 'none');
    			$("#advancedBorder").css('display', 'none');
    		}else if($('input[name="searchOrNavigate"]:checked').val() == "Free Text"){
    			$("#textBox").attr("placeholder", "Insert text").focus();
    			$("#searchType").val($("#searchType option:contains('All')").val());
    			//$("#searchType").prop("disabled", false);
    	        $("#searchType").trigger('chosen:updated');
    	        $('#chemdoodle').closest('tr').css('display', 'none');
    	        $("#advancedBorder").css('display', '');
    		}
    		if ($('.content').is(':hidden')){
				$('#generate').prop("disabled", false);
			}else{
				$('#generate').prop("disabled", true);
			}
    		onChangeAjax('searchType');
    		/*if($("#searchType").val() == "Inventory"){
    			$('.content').slideUp();
    			$('#imgAdvSearchFormSearch').children('img').attr("src", "../skylineFormWebapp/images/arrow_r.png");
    			if ($('.advanced2').is(':hidden')){
					$('#generate').prop("disabled", false);
				}
    		}*/
    		
    		//fix bug 8082
    		$('#resentlyResults_Parent').css('visibility', 'hidden');
     	    $('#resentlyResults_Parent').css('display', 'none');
            $('#inventoryResults_Parent').css('visibility', 'hidden');
     	    $('#inventoryResults_Parent').css('display', 'none');
     	    $('#parentAdvanced2').css('display', 'none');
     	    $('#ladvancedSearch2').css('display', 'none'); 
        		
    	});
    } 
    else if(_formCode == "MaterialDuplicates")
    {
    	$("#save_").css('display', 'none');
    }
    

    if(_formCode == 'RecipeFormulation'){
    	$("#saveButton").removeAttr('onclick');
    	//$("#saveButton").attr("onclick", "doSaveWithConfirmCharSample('Reload');");
    	$("#saveButton").attr("onclick", "checkCancelledStatus('Reload');");
    }
    
    if(_formCode == "Step" || _formCode == "StepFr" || _formCode == "StepMinFr")
    {
    	if($('#isNew').val() == '1') {
    		$("#quickAction").addClass('authorizationDisabled');
    	}
    	//init script used for editable tables in "Step"
		initEditableTableOnReadyScript();
		
		//arrange the title
		$('#pageTitle').css('white-space','pre');
		
		if(_formCode == "StepMinFr") {
			//auto save on form input blur
			$("#stepName").blur(function(){
				 saveStepMinFrData(this);
			});
			
			var batchSize = $('#batchSizeMaterial').val();
			$('#batchSizeMaterial').attr('oldvalue',density);
	   		 document.getElementById("batchSizeMaterial").onblur = function() {
	   			 if($('#batchSizeMaterial').val()!=""){
	   				 $('#compositions').attr('lastChangeVal', $('#batchSizeMaterial').attr('oldvalue'));
	   				 //calculate the planned for X step batch
	   				 generalBL_elementDataTableClickEvent("compositions","calcComposition",[$('#formId').val(),"Composition","batchSize","calcBatchSizeOnChange"],$(this));
	   			 }
	   			 $('#batchSizeMaterial').attr('oldvalue',$('#batchSizeMaterial').val());
	   		 };
	   		 
	   		// hidden element - not in used
//			var batchSize = $('#batchSizeProd').val();
//			$('#batchSizeProd').attr('oldvalue',density);
//	   		 document.getElementById("batchSizeProd").onblur = function() {
//	   			 if($('#batchSizeProd').val()!=""){
//	   				 $('#products').attr('lastChangeVal', $('#batchSizeProd').attr('oldvalue'));
//	   				 generalBL_elementDataTableClickEvent("products","calcComposition",[$('#formId').val(),"Composition","batchSize","calcBatchSizeOnChange"],$(this));
//	   			 }
//	   			 $('#batchSizeProd').attr('oldvalue',$('#batchSizeProd').val());
//	   		 };
	   		 
			$("#batchSizeMaterial").blur(function(){
				 saveStepMinFrData(this);
			});
			
			// hidden element not in used
//			$("#batchSizeProd").blur(function(){
//				 saveStepMinFrData(this);
//			});
			
			$('#compositions_Parent').css('display', 'none');
			$('#products_Parent').css('display', 'none');
	        
	        //move upperFilters into upperTable_wfDiv
			$('#compositions_wfDiv').append($('#upperFiltersMaterials'));
			$('#products_wfDiv').append($('#upperFiltersProducts'));
		}
    }
    if(_formCode == 'Experiment' || _formCode == "ExperimentCP"     // develop ExperimentCP "Continuous Process"
    	|| _formCode == 'Step' || _formCode.slice(0,6) =='Workup') { 
		
    	/*
    	 * kd 16012019
    	 * Made for task: 24608 
    	 * call commented. Need requirement: list of document (Experiment, what kinds of experiment, what else, what is message type, what buttons on message
    	 */
    	
    	getFormTitleWarningMessage(); //TODO: uncomment
    	
    }
    if(_formCode == "Step") {
    	$("#preparation_run").change(function(){
    		if($(this).val()=="Run"){//if select the value as Run,then do copy multistep of the last preparation
    			getLastPreaparationProducts($("#formId").val(),$("#EXPERIMENT_ID").val());
    		}
    		checkTabClickFlag('ReactionTab');
    	});
    	var $limitAgent = $("#limitingAgentMole");
    	$limitAgent.val(convertDecimalToExponential($limitAgent.val()));
    	$limitAgent.removeAttr('onchange');
    	$limitAgent.change(function(){
    		var $this = $(this);
    		$this.attr('realvalue', $this.val());
    		setWebixMassBalanceLimitingAgentMole("webixMassBalanceTable");
    		calculateMassBalanceFields("webixMassBalanceTable","");
    	});
    	
    	// calc mass balance table and fields on load
    	calculateMassBalanceFields("webixMassBalanceTable","");
    	
    	toggleDisableStepLAMole();
    	$("#chkManualUpdate").removeAttr('onclick');
    	$("#chkManualUpdate").attr("onclick", "toggleDisableStepLAMole()");
    	
    	$("#addStreamButton").attr("name", "webixContainerButtons");
    	$("#calculateMassBalFieldsButton").attr("name", "webixContainerButtons");
    	$("#saveButton").removeAttr('onclick');
		//$("#saveButton").attr("onclick", "doSaveWithConfirm('Reload');");
    	$("#saveButton").attr("onclick", "checkNonFamiliarAndSave(doSaveWithConfirm,'Reload');");
    }
    if(_formCode=='QuickAction'){
    	$('.displayOnLoad div').css('overflow-y','hidden');
    }
    if(_formCode.slice(0,6) =='Workup'){
		
    	$('.page-header').css({"padding":"10px"}); //23012019 kd temp solution for possibility to view buttons, 27102019 kd changed 0px on 10px - fixed defect 7514
    	$('#STATUS_ID').attr("onchange", "confirmCancelledWorkup(openWFDialog,[this]);");
		$("#saveButton").removeAttr('onclick');
		$("#saveButton").attr("onclick", "doSaveWorkup('Reload');");
		
		$("#saveNextButton").removeAttr('onclick');
		$("#saveNextButton").attr("onclick", "confirmNextStage(doSaveWorkup,'Reload', 'UPDATE_STAGE_STATUS');");
		
		if($('#STAGE_ID').val()==$('#firstStage').val() || $('#firstStage').val()==$('#prevStageStatus').val() ){
			$("#backStageButton").addClass('disablePage');
		}
		
		if(!$('#isSaveNextEnabled').is(":checked")){
			$("#saveNextButton").addClass('disablePage');
		}
		
		generalBL_statusIdDoneCanceled();
		generalBL_statusIdCompleted();
		
    }
    if(_formCode=='ExpImpSpec'){
    	if($('#tableType').val()=='import'){
    		$('#lexpImpSpec label:not(.asterisk)').text('Import from Sub-Project:');
    		setTimeout(function () {
    			$('#pageTitle').text('Import Specifications');
            }, 100);
    	}else{
    		setTimeout(function () {
    			$('#pageTitle').text('Export Specifications');
            }, 100);
    	}
    }
    
    if(_formCode == 'Iframe') {
    	$("div[id*='AsyncIframeParent']" ).css('height', '100%');
    	
    	
    	$("div[id*='iframe_Parent']" ).css('height', '70%');
    	$("div[id*='iframe_Parent']" ).css('width', '50%');
    	$("div[id*='iframe_Parent']" ).css('position', 'fixed');
    	$("div[id*='iframe_Parent']" ).css('left', '25%');
    	
    }
    if(_formCode == 'SearchLabel') {
    	$('#close_back').hide();
    	$('.expanded.page-header').hide();
    	$('#pageTitle').closest('table').closest('tr').remove();
        $('.ui-widget-content table:last').closest('div').css('height', '');
        //hide the loading label (it has no id so we use font[color=red] to get this dom element)
        $("font[color=red]").css('display', 'none');
    	setTimeout(function () {
	    	$("#SeachLabelName").focus();
	    	$("#SeachLabelName").keyup(function(e) {
	    		if(e.which == 13){ // qrcode scan will include enter as last char
	    			searchLabel();
	    		}
	    	});
    	},100);
    }
    if(_formCode == 'ScanQrCode') {
    	$('#close_back').hide();
    	$('.expanded.page-header').hide();
    	$('#pageTitle').closest('table').closest('tr').remove();
        $('.ui-widget-content table:last').closest('div').css('height', '')
        var savestructformcode = $('#scanQrCodeName').attr('savestructformcode');
        $('#save_').removeAttr('onclick');
		$('#save_').attr('onclick','doSaveQrCode("scanQrCodeName","'+savestructformcode+'")');
		setTimeout(function () {
	    	$("#scanQrCodeName").focus();
    	},100);
    }
    if(_formCode == 'BatchMain'){
    	if($('#lastStep').is(":checked")){//checking it twice: first - for the initiation and the other -for the laststep changed point
    		$("font[color=red]").text('The batch will contain the components from "Planned Composition" table');
		} else {
			$("font[color=red]").text('The batch will contain the components from materials table');
		}
    	$("font[color=red]").closest('td').attr('colspan',"2");
    	$('#lastStep').change(function(){
    		if($(this).is(":checked")){
    			$("font[color=red]").text('The batch will contain the components from "Planned Composition" table');
    		} else {
    			$("font[color=red]").text('The batch will contain the components from materials table');
    		}
    	});
    }
    if(_formCode == 'ExperimentStb') { 
    	$("#lLinkSample").text('');
    }
    if(_formCode.indexOf('STest')==0||_formCode=='SelfTest'||_formCode.indexOf('Workup')==0){//formcode starts with stest
    	//$('#tdIncludeBreadcrumbJsp >> div.breadcrumbs-container').css('width','fit-content');
    	//$('#tdIncludeBreadcrumbJsp >> div.text-center').css('transform','translateX(50%)');//put the head title in the middle of the page
    	$('.page-header').css({"padding":"10px"}); //23012019 kd temp solution for possibility to view buttons, 27102019 kd changed 0px on 10px - fixed defect 7514
    	//$('#pageTitle').css('white-space','pre');
    }
    if(_formCode=='SelfTest' || _formCode=='Request')
	{
    	$('.dragAndDrop label').css({"font-size":"inherit"});
    	$('.dragAndDrop label').css({"margin-right":"10px"});
    	$('.dragAndDrop label').css({"margin-left":"10px"});
    	$('.dragAndDrop label').css({"width":"185px"});
    	$('.dragAndDrop').css({"height":"30px"});
    	$('.dragAndDrop').css({"margin-bottom":"0px"});
    	$('.fileUploadElementForm').each(function(){
    		$(this).css({"display":"none"});
    	});
    		$('div[id*="_destinationDocUpload"]').css({"float":"right"});    	    	
	}
    if(_formCode=='Request'){    
		initEditableTableOnReadyScript();
		//replace the change event of self test type
    	$('#REQUESTTYPE_ID').off('change'); //without if we will have onChangeAjax('REQUESTTYPE_ID'); anyway  
    	$('#REQUESTTYPE_ID').attr("onchange", "onChangeRequestType(this);");
	}
    if(_formCode=='SelfTest'){    	
    	
    	//replace the change event of self test type
    	$('#TYPE_ID').off('change'); //without if we will have onChangeAjax('TYPE_ID'); anyway  
    	$('#TYPE_ID').attr("onchange", "confirmChangeSelfTestType(this);");
    	
    	$("#saveButton").attr("onclick", "doSaveSelfTest('Reload');");
    	
//    	//disable new entity in selftest is not saved //fix bug 7810 - move to authz function disabledButtonOnNewSelfTest
//    	if($('#isNew').val() == '1') {
//    		$("#newButton").addClass('authorizationDisabled');
//    		$("#newFloatingButton").addClass('authorizationDisabled');
//    		$("#newSampleFloatingButton").addClass('authorizationDisabled');
//    	}
	}

    //make no body x scroll for the following forms (the datatables in this forms should have overflow-x auto)
    if(_formCode == 'ExpAnalysisReport' || _formCode == 'ExperimentReport') {
    	 $('.dataTableParent').css({"overflow-x": "auto", "overflow-y": "hidden", "max-width":$('body')[0].clientWidth - 50 + 'px'}); //, "padding-bottom":"95px" padding-bottom because of ddl in edit tables
    	 $('.tab-container').css("padding-right", "0px"); // fix bug 7180 - if overflow-x is hidden the padding-right "hide" the y scroll of the tab content
    	 //$('body').css('overflow-x', 'hidden'); //YP 27122018 fix bug 14967 - open for step form (body scroll)
    	 $('#characteristicMassBalan_ddl_chosen .default').css("width","225px");
    	 $('#resulttype_ddl_chosen .default').css("width","100%");
    	 $('#IMPURITY_MATERIAL_ID_VAL_chosen .default').css("width","190px");
    }
    
    if(_formCode == 'MultiStep'){
    	$("#save_").removeAttr('onclick');
		$("#save_").attr("onclick", "executeMultiStepCopy('multiStepTable');");
    }
    
    if(_formCode == 'RequestSelect'){
    	if($(parent.document).find(_formCode.slice(0,10) =='Experiment')){//it's a requestselect opened from experiment
			$("#save_").removeAttr('onclick');
			$("#save_").attr("onclick", "doSaveRequestSelect('Close');");
		}
	}

	if (_formCode == 'ColumnsDefinition') 
	{
		var colObjArr = parent.$('#prevDialog').data('colArray');
		var removedArr = parent.$('#prevDialog').data('removedArray');
		var tableId = $('#tableId').val();
		$('#close_back').html('OK');
		$('#close_back').attr('onclick', 'doReset();');
		
		
		var ulAll = "<ul>";
		var ulElem = "<ul id=\"reorderable\">";
		var mainContainer_ = $('#cblist');
		var id = 0, checked_counter = 0;
		var showResetDefault = (parent.$('#formCode').length > 0 && (
				parent.$('#formCode').val() == 'Main' || 
				parent.$('#formCode').val() == 'StepMinFr' 
		    )
		);
		
		for (var j = 0; j < colObjArr.length; j++) {
			if (colObjArr[j] != undefined) 
			{
				var obj = colObjArr[j];
				var isColRemoveEnabled = obj.isColRemoveEnabled;
				var isColReorderEnabled = obj.isColReorderEnabled;
				var name = obj.title;
			    var val = name;
			    if(name.indexOf(';') != '-1'){
				   val = name.split(';')[0];
				   name = name.split(';')[1];
			    }
			    if(id == 0)
			    {
			    	ulAll += "<td>";
			    	ulAll += "<input type='checkbox' id='chbAllNone'></>" + "<label>All</label>";
			    	if(showResetDefault) {
			    		ulAll += "<span> <img src='../skylineFormWebapp/images/settings.png' style='cursor:pointer;width: 26px;float: right;margin-right: 25px;' onclick='restoreColumnsByDefault()'> </span>" //"+domId+".id
			    	}
			    	ulAll += "</td>";
			    	ulAll += "</ul>";
			    	mainContainer_.append($(ulAll));
			    }
			    id++;			    
			    var checked = "", disabled = "", 
			    	reorderDisabledClass = "", 
			    	colIndex="colIndex="+obj.colIndex+"";
			    
			    if (removedArr.indexOf(val) == '-1') {
			    	checked = "checked";
			    	checked_counter++;
				}
			    if(!isColReorderEnabled) {
			    	reorderDisabledClass = "ui-state-disabled";
				}
			    
			    if(!isColRemoveEnabled) {
			    	disabled = "disabled";
		    	}
			    
			    ulElem += "<li class='ui-state-default "+reorderDisabledClass+"'>";
			    ulElem += "<input type='checkbox' id='cb"+id+"' value='"+val+"' "+checked+" "+disabled+" "+colIndex+"></>";
			    ulElem += "<label>"+name+"</label>";
			    ulElem += "</li>";	
			}
		}
		ulElem += "</ul>";
		mainContainer_.append($(ulElem));
		
		var checkbox_counter = $( "#reorderable" ).find('input[type="checkbox"]').length;
		if(checkbox_counter > 0 && checkbox_counter == checked_counter) 
		{
			$('#chbAllNone').prop('checked',true);
		}
		
		$( function() {
		    $( "#reorderable" ).sortable({
		      items: "li:not(.ui-state-disabled)"
		    });
		});
		
		$('ul#reorderable input[type="checkbox"]').on('click',function(){
			var isChecked = $(this).is(':checked');
			if(isChecked) {				
				var unchecked_counter = $( "#reorderable" ).find('input[type="checkbox"]:not(:checked)').length;
				if(checkbox_counter > 0 && unchecked_counter == 0) 
				{
					$('#chbAllNone').prop('checked',true);
					}
				}
			else {
				$('#chbAllNone').prop('checked',false)
			}
		});
		
		$('#chbAllNone').change(function() {
			var chk = false;
			if ($('#chbAllNone').is(":checked")) {
				chk = true;
			}
			$('ul#reorderable input[type="checkbox"]:not(:disabled)').each(function(){
				var $el = $(this);
				$el.prop("checked", chk);
			});
		});

	}
	if (_formCode == 'CopyDesignStep') {
		//designBL.js
		initCopyDesignStep();
	}
	
    if(_formCode == 'NavigationTree') {
    	var parentFormId = parent.$('#prevDialog').data('parentFormId');
    	var parentFormCode = parent.$('#prevDialog').data('parentFormCode');
    	
    	var elementTree='<div id="tree" style="font-size: 11px;element=\"ElementTreeImp\"></div>'
    	+ '<input type="hidden" id="tree_catalog_hidden" value="FG_I_TREE_CONNECTION_V">'
    	+ '<input type="hidden" id="tree_tree_lastValue" value="">'
    	+ '<input type="hidden" id="tree_selected" value="">'
    	+ '<input type="hidden" id="tree_firstTime" value="0">'
    	+ '<input type="hidden" id="tree_doOnChangeJSCall" value=" onChangeAjax(\'tree\'); ">';
    		
    	var urlParam = "?formId="+ parentFormId
		+ "&formCode="+ _formCode
		+ "&userId="+ $('#userId').val()
		+ "&eventAction=getNavigationProject"
		+ "&isNew=" + $('#isNew').val();
    	
    	var allData = getformDataNoCallBack(1);
    	
    	var data_ = JSON.stringify({
    		action : "getNavigationProject",
    		data : [],
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
    			else{
    				if(obj.data[0].val!=undefined && obj.data[0].val!=null){
    					var res = obj.data[0].val.split('@');
        				var projectId = res[0];
        				var projectName = res[1];
        				var lastValue = res[2];
        				$('#navigationTree_').append(elementTree);
        				initTree('tree',' onChangeAjax(\'tree\'); ','{"_tree_lastValue":"'+lastValue+'","_selected":"'+parentFormId+','+parentFormCode+'"}','Project',projectId,projectName,'Project',projectId,parentFormId);
    				}else{
    					displayAlertDialog(getSpringMessage('alertError'));
    				}
    			}
    			},
    			error : handleAjaxError
    			});
    	

	}
	if (_formCode == 'ReportDesignExp') {
		initDesignDataSession();
	}
	if (_formCode == 'SaveReportDesign') {
		initSaveReportDesign();
	}
	if (_formCode == 'ReportDesignSearch') {
		initReportDesignSearch();
	}
	if (_formCode == 'MaterialSelect') {
		$("#save_").removeAttr('onclick');
		$("#save_").attr("onclick", "doSaveMaterialSelect('multiStepTable');");
	}
	if(_formCode == 'ExpAnalyReportMain'){
		$('#close_back').css('display','inline');
		$('#newButton').css('display','none');
		$('#saveButton').css('display','none');
		var url=new URL(window.location);
		var reportName = url.searchParams.get("PARENT_REPORT");
		
		if(reportName != undefined && reportName == "ExpAnalysisReport"){
   			$('#pageTitle').html("Experiment Analysis Reports");
		}
		else if(reportName != undefined && reportName == "ExperimentReport"){
			$('#pageTitle').html("Reaction and Results Analysis");
		}
	}
	if(_formCode == 'MaterialSlctSearch'){
		//$('#materialTable_value').val(parent.$('#INVITEMMATERIAL_ID').val());
		//onElementDataTableApiChange('materialTable');
		$("#save_").removeAttr('onclick');
		$("#save_").attr("onclick", "doSaveMaterialSlctSearch(this);");
	}
	if(_formCode == 'RecipeFormulation'){
		/**
		 * If the density value is being changed, the fields in the composition table should be calculated
		 * */
		var density = $('#density').val();
		 $('#density').attr('oldvalue',density);
		 document.getElementById("density").onblur = function() {
			 if($('#density').val()!=""){
				 $('#compositions').attr('lastChangeVal', $('#density').attr('oldvalue'));
				 generalBL_elementDataTableClickEvent("compositions","calcComposition",[$('#formId').val(),"Composition","density","calcDensityOnChange"],$(this));
			 } else{
				 validateDensityRequired("1",'compositions');
			 }
			 $('#density').attr('oldvalue',$('#density').val());
		 };
	}if(_formCode == 'SpreadsheetTempla'){
		if ($('#isNew').val() == "1") {
			setTimeout(function() {
				$('#pageTitle').html('New Spreadsheet Template');
			}, 100);
		}
//		$('#favorite').addClass('ignor_data_change');//fixed bug 8553
		
		// only the creator can edit the spreadsheet
		if($('#userId').val() != $('#CREATOR_ID').val()){
			generalBL_disablePage(['favorite']);
			$('#saveAsButton').removeClass('disablePage');
		}
		$('#newButton').css('display', 'none'); // hide the 'new' button
		
		//
	    $("#saveButton").removeAttr('onclick');
		//$("#saveButton").attr("onclick", "doSaveSpreadsheetTemplate('Reload');");
	    $("#saveButton").attr("onclick", "confirmSpreadNameChanged('Reload');");
		/**
		 * If the spreadsheet name is being changed, when leaving the ‘spreadsheet name’ field,
		 * a popup message should display, warning the user that the changes will override the existing template
		 * */
		var spreadsheet_name = $('#spreadsheetTemplaName').val();
		 $('#spreadsheetTemplaName').attr('lastvalue',spreadsheet_name);
		 document.getElementById("spreadsheetTemplaName").onblur = function() {
			 if($('#spreadsheetTemplaName').val()!= $('#spreadsheetTemplaName').attr('lastvalue') && $('#spreadsheetTemplaName').attr('lastvalue')!= ""){
				 openConfirmDialog({
						onConfirm : function (){
							$('#spreadsheetTemplaName').attr('confirmChangedValue','1');
						},
						title : 'Warning',
						message : getSpringMessage('The spreadsheet name: \''+$('#spreadsheetTemplaName').attr('lastvalue')+'\' will be overridden with the new name.'
									+'</br> are you sure you want to continue?'),
						onCancel: function (){
							$('#spreadsheetTemplaName').val($('#spreadsheetTemplaName').attr('lastvalue'));
							$('#spreadsheetTemplaName').removeAttr('confirmChangedValue');
						}
					});
			 }
		 };
		 //$("#fileUploadElementForm_document").attr("onchange", "onChangeSpreadsheetFile();");
		 //$("#documentdragAndDropHandler").attr("onchange", "onChangeSpreadsheetFile();");
		 
//		 $("#favorite").attr("onchange", "onChangefavorite("+ $("#formId").val()+");");
	}
	if(_formCode == 'SaveSpreadsheetAs') {
    	$("#spreadsheetNameList").attr("onchange", "doSpreadsheetTemplChosenChange();");
    	$('#save_').removeAttr('onclick');
		$("#save_").attr("onclick", "saveSpreadsheetTemplateAs('Reload');");
    	setTimeout(function () {
    		$('#pageTitle').css('font-size', '18px');
    		$('#pageTitle').parent().css('width', '100%');
    	},100);
    }
	if(_formCode == 'HistoricalData'||_formCode == 'HistoricalDataMain'){
		$('#generateButton').css('margin-left', '10px');
		$('.dataTableParent').css('margin-left', '10px');	
		$('.ui-datepicker-trigger').css('margin-right', '2%');
		$('#close_back').attr("onclick","closeViewSpreadSheetPopup();parent.$('#prevDialog').dialog('close');");

	}
	if(_formCode == 'ViewSpreadsheet'){
		//$('#close_back').removeAttr("onclick");
		$('#close_back').attr("onclick","parent.$('#prevDialog1').dialog('close');");
		
	}
	if(_formCode == 'CopyFunctionTable'){
		//$('#close_back').removeAttr("onclick");
		$('#save_').removeAttr('onclick');
		$('#save_').html('OK');
		$("#save_").attr("onclick", "copyFunctionTable();");
		$("#MATERIALFUNC_ID_chosen").css('position', 'absolute');
		$("#MATERIALFUNC_ID_chosen").css('width', '33%');
	}
	if(_formCode == 'ExperimentGroup'){
		//$('#close_back').removeAttr("onclick");
		$('#save_').removeAttr('onclick');
		$("#save_").attr("onclick", "doSaveExperimentGroup()");
	}
	if(_formCode == 'PurityList'){
		//$('#close_back').removeAttr("onclick");
		$('#save_').removeAttr('onclick');
		$("#save_").attr("onclick", "doSavePurityList()");
	}
	//ab 22122020: code for Demo floating tabs
	if(_formCode == 'StepFloatingTabs')
	{
		$('#floatingVerticalTabs >ul>li').each(function(){
			var linkElem = this;
			var href = $(this).find('a').attr('href');
			if(href == "#ReactionTab")
			{
				$(this).find('a').html("<i class='fa fa-flask'></i>");
			}
			else if(href == "#ResultsTab")
			{
				$(this).find('a').html("<i class='fa fa-line-chart'></i>");
			}
			else if(href == "#MassBalanceTab")
			{
				$(this).find('a').html("<i class='fa fa-balance-scale'></i>");
			}
		});
	}
//	if(_formCode == 'DynamicReport') { // made for prigat demo that passes demoCall=1 in the URL
//		var url_ = url();
//		if(url_.indexOf("&demoCall=1") > 0) {
////			window.open("http://localhost:8080/Adama/skylineForm/initid.request?formCode=DynamicReport&formId=-1&userId=288758&stateKey=1616410528906&demoCall=1&skylineUserName=Irit");
//			$('#dropDownMenuBar').css('display','none');
//			$('#tdIncludeBreadcrumbJsp').css('visibility','hidden');
//			var urlParams = new URLSearchParams( window.location.search);
//			$('.user-name').html(urlParams.get('skylineUserName'));
//		}
//	}
	if(_formCode == 'MultiAddRows'){
		$('#rowQuantity').focus();
	}
	initStaticPageHeader();
	initFloatingButtonsPanelMain();
    initFloatingButtonsPanel();
//    initFloatingTabsPanel(); //ab 22122020: code for Demo floating tabs
    displayHeaderDdl();
    $( window ).resize(function() {
    	if ($("#prevDialog").hasClass("ui-dialog-content ui-widget-content")) {
    		 $("#prevDialog").css('width','100%');
    		 var height_ = $('#prevDialog').closest('.ui-dialog').height()*0.85;//$(window).height() *0.7;
    		 $("#prevDialog").css('height',height_);
    	 }
    });
    
    console.log("end initForm");
}


//specific function for the experiment forms
function checkSpreadsheetFullScreenInExperiment(callBackFunc,doAfterSave){
	if(isSpreadsheetFullScreen()){
		saveSpreadsheet(getSpreadsheetElementFullScreen()[0]);
	}
	else{
		checkNonFamiliarAndSave(callBackFunc,doAfterSave);
	}
}

function onChangeSpreadsheetFile(){
	var name = $('[name="uploadFile"]').val();
	var res = name.split(".");
	if(res[1] ==undefined || res[1].toLowerCase() !="xlsx" || res[2]!=undefined){
		displayAlertDialog(getSpringMessage('ONLY_SPREADJS_SUPPORTED'),null,{button:"OK"});
	}
}

function doSaveUpload() {
	$("#save_").text("Upload & Save...");
	setTimeout(function() {
		doSave('Close');
	}, 100);
}

function getLastPreaparationProducts(stepId,experimentId){
	var allData = getformDataNoCallBack(1);
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + "&userId=" + $('#userId').val()
			+ "&eventAction=getLastPreparationId&isNew=" + $('#isNew').val();

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
			if(obj.data[0].val!=null && obj.data[0].val!=''){//there exists a preparation to copy data from
				openConfirmDialog({
					onConfirm : function (){
						//copy the data from the last preparation
						var prepStepToCopyFrom = obj.data[0].val;
						var doCopyFromLastPreparation = true;
						copyMultiStep(prepStepToCopyFrom,$('#formId').val(),doCopyFromLastPreparation);
					},
					title : '',
					message : getSpringMessage('confirmCopyMultiStep'),
				});
			}
		},
		error : handleAjaxError
	});
}

function displayHeaderDdl(){
	if (($('#formCode').val() == 'Step'  && $('#runNumber').val()!= '') ||$('#formCode').val() == 'ExperimentCP'){//Step CP
		var runNumber = '';
		if($('#formCode').val() == 'ExperimentCP'){
			var cName = $('#userId').val()+':'+$('#formId').val()+':'+'$RUNNUMBER';
			var run = getCookie(cName);
			if(run != ""){
				runNumber = run;
			}
		}	
		var allData = getformDataNoCallBack(1);
			// url call
			var urlParam = "?formId=" + $('#formId').val() + "&formCode="
					+ $('#formCode').val() + "&userId=" + $('#userId').val()
					+ "&eventAction=AddOptionsToDDl&isNew=" + $('#isNew').val();
		
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
					if(obj.data[0].val!='' && obj.data[0].val!='{}'){
						$('#headerSelect').css('display','inline');
						var selectValues =  JSON.parse(obj.data[0].val);
					
					$.each(selectValues, function(value, key) { 
						if($('#runNumber').val() == value || runNumber == value){
					     $('#headerSelect')
					         .append($("<option></option>")
					                    .attr("value",key+";"+value).attr("selected","selected")
					                    .text("run "+value));
					     $('#headerSelect').attr('lastvalue', key+";"+value);
						}else{
							$('#headerSelect')
					         .append($("<option></option>")
					                    .attr("value",key+";"+value)
					                    .text("run "+value)); 
						}
					     
					});
					$('[id="headerSelect"]').chosen({width:'auto',allow_single_deselect:true ,search_contains:true});
					
					$('#headerSelect').attr("onchange", "onChangeHeaderSelect(this);");
					if($('#formCode').val() == 'ExperimentCP'){
						if(runNumber!="" && $("#headerSelect option:contains('"+runNumber+"')").length > 0){
							$("#headerSelect").val($("#headerSelect option:contains('"+runNumber+"')").val());
							$("#headerSelect").trigger('chosen:updated');
							$("#headerSelect").change();
						} else {
							$("#headerSelect").val($("#headerSelect option:contains('undefined')").val());
							$("#headerSelect").trigger('chosen:updated');
							disableSampleTableExperimentCp(runNumber);
						}
					}
				}
			},
			error : handleAjaxError
		});
	}
}

function removeRunFromHeaderSelect(runNumber){
	$("#headerSelect option:contains('run "+runNumber+"')").remove();
	$("#headerSelect").trigger("chosen:updated");
	if($("#headerSelect option").length==0){
		$('[id="headerSelect"]').chosen('destroy');
		$("#headerSelect").css('display','none');
	}
}

function addNewRunToheaderSelect(runNumber,runStatusName){
	if(runNumber=="0"){
		return;
	}
	var isValueChosen = true;
	if($("#headerSelect_chosen").length==0){
		$('#headerSelect').css('display','inline');
		$('[id="headerSelect"]').chosen({width:'auto',allow_single_deselect:true ,search_contains:true});
		$('#headerSelect').attr("onchange", "onChangeHeaderSelect(this);");
		isValueChosen = false;
	}
	
	var $selectedOption = $("#headerSelect option:selected");
	$("#headerSelect").append($('<option>', {
	    value: runStatusName+";"+runNumber,
	    text: 'run '+runNumber
	})).trigger("chosen:updated");
	if(!isValueChosen || $selectedOption.length==0){
		$('#headerSelect').val('undefined').trigger("chosen:updated");
	}
}

function updateRunFromPlannedToActive(runNumber){
	var value = $("#headerSelect option[value='Planned;"+runNumber+"']").val();
	$("#headerSelect option[value='Planned;"+runNumber+"']").attr('value',value.replace('Planned','Active'));
	$("#headerSelect").trigger("chosen:updated");
	//update the mass balance name and the limiting agent
	var maxRun=0;
	var $option = $("#headerSelect option:contains('run 0')");
	//get the maximum run number
	$("#headerSelect option:not(:contains('run "+runNumber+"'),[value*='Planned'])").each(function(){
		var run = $(this).val().split(";")[1];
		if(parseInt(run)>maxRun){		
			maxRun= parseInt(run);
			$option = $(this);
		}
	});
	var dataCsv = "";
	if($("#massBalanceDataPerRun").val()){
		var dataAllRuns = JSON.parse($("#massBalanceDataPerRun").val());
		lastRunData = dataAllRuns[maxRun];
		
		var lastMassBalanceName = lastRunData == undefined || lastRunData["massBalanceName"] == ""?"":"Run "+runNumber+" "+lastRunData["massBalanceName"];
		var lastLimitingAgent = lastRunData == undefined?"":lastRunData["limitingAgent_id"];
		var currentData = {massBalanceName:lastMassBalanceName,limitingAgent_id:lastLimitingAgent,
				limitingAgentMole:"",summary:"",conversion:"",
				chemicalYield:"",isolatedYield:""};
		
		dataAllRuns[runNumber] = currentData;
		dataCsv = JSON.stringify(dataAllRuns);
		$("#massBalanceDataPerRun").val(dataCsv);
	}
	
	if($("#massBalance2DataPerRun").val()){
		var dataAllRuns = JSON.parse($("#massBalance2DataPerRun").val());
		lastRunData = dataAllRuns[maxRun];
		
		var lastMassBalanceName = lastRunData == undefined || lastRunData["massBalanceName"] == ""?"":"Run "+runNumber+" "+lastRunData["massBalanceName"];
		var lastLimitingAgent = lastRunData == undefined?"":lastRunData["limitingAgent_id"];
		var currentData = {massBalanceName:lastMassBalanceName,limitingAgent_id:lastLimitingAgent,
				limitingAgentMole:"",summary:"",conversion:"",
				chemicalYield:"",isolatedYield:""};
		
		dataAllRuns[runNumber] = currentData;
		dataCsv = JSON.stringify(dataAllRuns);
		$("#massBalance2DataPerRun").val(dataCsv);
	}
	
	if($("#massBalance3DataPerRun").val()){
		var dataAllRuns = JSON.parse($("#massBalance3DataPerRun").val());
		lastRunData = dataAllRuns[maxRun];
		
		var lastMassBalanceName = lastRunData == undefined || lastRunData["massBalanceName"] == ""?"":"Run "+runNumber+" "+lastRunData["massBalanceName"];
		var lastLimitingAgent = lastRunData == undefined?"":lastRunData["limitingAgent_id"];
		var currentData = {massBalanceName:lastMassBalanceName,limitingAgent_id:lastLimitingAgent,
				limitingAgentMole:"",summary:"",conversion:"",
				chemicalYield:"",isolatedYield:""};
		
		dataAllRuns[runNumber] = currentData;
		dataCsv = JSON.stringify(dataAllRuns);
		$("#massBalance3DataPerRun").val(dataCsv);
	}
}

function onChangeHeaderSelect(this_){
	if ($('#formCode').val() == 'Step'){
		var runStatus = this_.value.split(";")[0];
		var runNumber = this_.value.split(";")[1];
		if(runStatus.toLowerCase().indexOf("planned") > -1){// the run doesn't exist
			/*openConfirmDialog({
				onConfirm : function (){
					//create run and navigate
					
				},
				title : '',
				message : getSpringMessage('CONFIRM_CREATE_RUN'),
				onCancel: function (){
					var lastValue = $("#headerSelect").attr('lastvalue');
					$("#headerSelect").val($("#headerSelect option[value='"+(lastValue!=""?lastValue:'undefined')+"']").val());
					$("#headerSelect").trigger('chosen:updated');
					$("#headerSelect").val($("#headerSelect option:contains('"+$('#runNumber').val()+"')").val());
					$("#headerSelect").trigger('chosen:updated');
				}
				});*/
			checkStepStatusAndCreateRun(2,runNumber);
			}else {
				navigateToRun(runNumber);
			}
	}else if($('#formCode').val() == 'ExperimentCP'){
		var runStatus = this_.value.split(";")[0];
		var runNumber = this_.value.split(";")[1];
		disableSampleTableExperimentCp(runNumber);
		updateMassbalanceDataperRun();//save the current MB data before the changing event done
		if(runStatus.toLowerCase().indexOf("planned") > -1){// the run doesn't exist
			/*openConfirmDialog({
				onConfirm : function (){
					updateMassbalanceDataperRun();
					createRunFromDDl(runNumber);
				},
				title : '',
				message : getSpringMessage('CONFIRM_CREATE_RUN'),
				onCancel: function (){//return default or last selected value
					var lastValue = $("#headerSelect").attr('lastvalue');
					$("#headerSelect").val($("#headerSelect option[value='"+(lastValue!=""?lastValue:'undefined')+"']").val());
					$("#headerSelect").trigger('chosen:updated');
					 disableSampleTableExperimentCp(lastValue);
					}
				});*/
				checkStepStatusAndCreateRun(1,runNumber);
			}
		else{
			$('#headerSelect').attr('lastvalue', $('#headerSelect option:selected').val());
			setFormParamMap('ExperimentCP', $('#formId').val(),"RUNNUMBER_PARAM",runNumber,filterExpTablesAfterSelectRun,[runNumber]);
			var cName = $('#userId').val()+':'+$('#formId').val()+':'+'$RUNNUMBER';
			setCookie(cName,runNumber,365);
		}
	}
}

function updateMassbalanceDataperRun(fieldIndex){
	if(fieldIndex==undefined){
		updateMassBalanceDataPerRunSpecificTab("");
		updateMassBalanceDataPerRunSpecificTab("2");
		updateMassBalanceDataPerRunSpecificTab("3");
	}
	else{
		updateMassBalanceDataPerRunSpecificTab(fieldIndex);
	}
}

function updateMassBalanceDataPerRunSpecificTab(fieldIndex){
	var lastRun = $('#headerSelect').attr('lastvalue')==""?"0":($('#headerSelect').attr('lastvalue')!=""?$('#headerSelect').attr('lastvalue').split(";")[1]:"");
	var dataCsv = "";
	if($("#massBalance"+fieldIndex+"DataPerRun").val()== undefined || $("#massBalance"+fieldIndex+"DataPerRun").val()==""){
		var dataAllRuns = {};
		var currentData = {massBalanceName:$("#massBalanceName"+fieldIndex).val(),limitingAgent_id:$("#MATERIALREF_ID"+fieldIndex).val(),
				limitingAgentMole:$("#limitingAgentMole"+fieldIndex).val(),summary:$("#summary"+fieldIndex).val(),conversion:$("#conversion"+fieldIndex).val(),
				chemicalYield:$("#chemicalYield"+fieldIndex).val(),isolatedYield:$("#isolatedYield"+fieldIndex).val()};
		dataAllRuns[lastRun] = currentData;
		dataCsv = JSON.stringify(dataAllRuns);
	} else {
		var dataAllRuns = JSON.parse($("#massBalance"+fieldIndex+"DataPerRun").val());
		var currentData = {massBalanceName:$("#massBalanceName"+fieldIndex).val(),limitingAgent_id:$("#MATERIALREF_ID"+fieldIndex).val(),
				limitingAgentMole:$("#limitingAgentMole"+fieldIndex).val(),summary:$("#summary"+fieldIndex).val(),conversion:$("#conversion"+fieldIndex).val(),
				chemicalYield:$("#chemicalYield"+fieldIndex).val(),isolatedYield:$("#isolatedYield"+fieldIndex).val()};
		
		dataAllRuns[lastRun] = currentData;
		dataCsv = JSON.stringify(dataAllRuns);
	}
	
	$("#massBalance"+fieldIndex+"DataPerRun").val(dataCsv);
}

function filterExpTablesAfterSelectRun(args){
	onElementDataTableApiChange('samples');
	onElementDataTableApiChange('experimentResults');
	onElementDataTableApiChange('selfTestResults');
	onElementDataTableApiChange('steps');
	setMassBalanceGeneralFileds(args,"");//has to be invoked before updateSampleListAndShowOrHideStream,so that the automatic calculations consider the limiting agent
	setMassBalanceGeneralFileds(args,"2");
	setMassBalanceGeneralFileds(args,"3");
	updateSampleListAndShowOrHideStream(args);//this function filters the webix tables and then recalculate the upper fields and call updateMassBalancePerRun(save the values in the massbalanceDataPerRun field)
	getAllTablesDataForMassBalanceInfoForEachTable(args);//step mass balance information
}

function setMassBalanceGeneralFileds(args,fieldIndex){
	var runNumber = args[0];
	var massBalanceDataPerRun = $("#massBalance"+fieldIndex+"DataPerRun").val();
	if(massBalanceDataPerRun!= undefined && massBalanceDataPerRun!=""){
		var dataAllRuns = JSON.parse(massBalanceDataPerRun);
		var ddlValue = $('#headerSelect option:contains("run '+runNumber+'")').val();
		ddlValue = ddlValue != undefined? ddlValue.split(";")[1]:"";
		if(dataAllRuns[ddlValue]!=undefined){
			$("#massBalanceName"+fieldIndex).val(dataAllRuns[ddlValue].massBalanceName);
			$("#massBalanceName"+fieldIndex).attr('realvalue',dataAllRuns[ddlValue].massBalanceName);
			setMassBalanceNameAsTabName(fieldIndex,false);
			$("#MATERIALREF_ID"+fieldIndex).val(dataAllRuns[ddlValue].limitingAgent_id);
			$("#MATERIALREF_ID"+fieldIndex).trigger("chosen:updated");
		} else {
			$("#massBalanceName"+fieldIndex).val("");
			$("#massBalanceName"+fieldIndex).attr('realvalue',"");
			setMassBalanceNameAsTabName(fieldIndex,false);
			$("#MATERIALREF_ID"+fieldIndex).val("");
			$("#MATERIALREF_ID"+fieldIndex).trigger("chosen:updated");
		}
	}
}

function navigateToRun(runNumber){

	
	var stringifyToPush = {
			code : 'RUNNUMBER',
			val : runNumber,
			type : "AJAX_BEAN",
			info : 'na'
	};
	var allData = getformDataNoCallBack(1);
	allData = allData.concat(stringifyToPush);
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + "&userId=" + $('#userId').val()
			+ "&eventAction=getFormIdByRunNumber&isNew=" + $('#isNew').val();

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
			if(obj.data[0].val != '' && obj.data[0].val != '-1'){
				var formId = obj.data[0].val;
				checkAndNavigate([formId ,'Step','']);
			}else{
				//error
				var lastValue = $("#headerSelect").attr('lastvalue');
				$("#headerSelect").val($("#headerSelect option[value='"+(lastValue!=""?lastValue:'undefined')+"']").val());
				$("#headerSelect").trigger('chosen:updated');
				/*$("#headerSelect").val($("#headerSelect option:contains('"+$('#runNumber').val()+"')").val());
				 $("#headerSelect").trigger('chosen:updated');*/
			}
			},
		error : handleAjaxError
	});
	

}

function onChangeTemplate(elem,lastvalue){
	var $elem = $(elem);
	if(!isSpreadsheetEmpty("spreadsheetExcel"))//check if the spreadsheet has data
	{
		openConfirmDialog({
			onConfirm : function (){setDataIntoSpreadsheet(elem);},
			title : 'Warning',
			message : getSpringMessage('The values entered in the spreadsheet will be lost.'+
					'</br> Are you sure you want to load the selected template?'),
			onCancel: function (){
				$elem.val(lastvalue);
				$elem.trigger("chosen:updated");
				return;
			}
		});
	} else{
		setDataIntoSpreadsheet(elem);
	}
}

function setDataIntoSpreadsheet(elem){
	var $elem = $(elem);
	//var spread = $('#spreadsheetExcel').data('workbook');
	showWaitMessage("Please wait...");
	
	var allData = [{
			code : 'SPREADSHEETTEMPLATE_ID',
			val : $elem.val(),
			type : "AJAX_BEAN",
			info : 'na'
		}];
	
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + "&userId=" + $('#userId').val()
			+ "&eventAction=getSpreadsheetOfTemplate&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "getSpreadsheetOfTemplate",
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

			var spreadSheetData = obj.data[0].val;
			if (spreadSheetData!=null && spreadSheetData != "") {
				setValueToSpreadSheet('spreadsheetExcel',spreadSheetData);
			}

		},
		error : handleAjaxError
	});
}

function initFloatingTabsPanel()
{
	var formsArr = ['Step'];
	var _formCode = $('#formCode').val();
	
	if($.inArray(_formCode, formsArr) != -1)
	{
		var $tabsContainer = $('#divFloatingTabsPanelContainer');
		$tabsContainer.css('display','inline-block');
				
		var $window = $(window);
		var panelLeftPos = window.outerWidth - $tabsContainer.width() - (window.outerWidth - $window.width())  + $window.scrollLeft();	
		$tabsContainer.offset({'left':panelLeftPos});
		
		$tabsContainer.resizable({
			  handles: "s, w"
		});
		
		var formCode = "StepFloatingTabs";
		var src = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=" + formCode + "&formId=-1" + "&userId=" + $('#userId').val(); 
		$('#divFloatingTabsViewPanel >iframe').attr('src',src);
	}
}

function floatingTabsPanelToggleClick(elem)
{
	var $window = $(window);
	
	if($(elem).hasClass('fa-angle-left')) // show buttons
	{
		$(elem).removeClass('fa-angle-left').addClass('fa-angle-right');

		$('#divFloatingTabsViewPanel').css('display','block');		
		var $container = $('#divFloatingTabsPanelContainer');		
		var panelLeftPos = window.outerWidth - $container.width() - (window.outerWidth - $window.width()) + $window.scrollLeft();		
		$container.offset({'left':panelLeftPos});
		$container.css('height','500px');
	}
	else // hide buttons
	{
		$(elem).removeClass('fa-angle-right').addClass('fa-angle-left');
	
		$('#divFloatingTabsViewPanel').css('display','none');	
		$('#divFloatingTabsShowHideIcon').css('width','30px');
		var $container = $('#divFloatingTabsPanelContainer');
		var panelLeftPos = $window.width() - $(elem).parent().width() + $window.scrollLeft();		
		$container.offset({'left':panelLeftPos});
		$container.css('height','50px');
	}

}

function initFloatingButtonsPanel()
{
	
	var formsArr = ['Step','SelfTest','Request','ExperimentCP','SpreadsheetTempla','ExperimentFor','Sample','Experiment','ExperimentAn','Template'];
	var _formCode = $('#formCode').val();
	
	if($.inArray(_formCode, formsArr) != -1)
	{
		$('.mainSaveDefinitionBtn').css('display','none');    	
		var addToContainerWidth = 8;// in 'em'
		
    	if(_formCode == 'Step')
    	{	
    		// add optional button for Action table
    		var visibility = 'hidden';
    		if(getActiveTabID() == "ActionsTab") visibility = 'visible';
    		var $originBtn = $('#action_dataTableStructButtons').find('button.dataTableApiNew.dataTableAddRowButton');
    		
    		var button = document.createElement('button');
    		var $button = $(button);
    		$button.attr('type','button')
    				.attr('id','dataTableAddRowFloatingButton')
					.attr('class','button dataTableApiButton dataTableApiNew optionalFloatingButton')
					.attr('style','visibility:'+visibility+'')
					.attr('title','Add Action Row')
					.attr('onclick',$originBtn.attr('onclick'))
					.text($originBtn.text());
    		if($('#isNew').val()=="1"){
    			$button.addClass("disabledclass");
    		}
    		$button.prepend('<i class="fa fa-plus" aria-hidden="true"></i>');
    		$('.floatingButtonsPanel').prepend($button);    		
    	}
    	else if(_formCode == 'SelfTest')
    	{
    		var button = document.createElement('button');
    		$(button)
				.attr('type','button')
				.attr('class','button optionalFloatingButton')
	    		.attr('id','newSampleFloatingButton')
	    		.attr('onclick',"$('#btnNewSample')[0].click()")
	    		.text($('#btnNewSample').text());
    		$(button).prepend('<i class="fa fa-plus" aria-hidden="true"></i>');
    		$('.floatingButtonsPanel').prepend($(button));				    		
    	}
    	else if(_formCode == 'ExperimentFor')
    	{
    		var button = document.createElement('button');
    		$(button)
				.attr('type','button')
				.attr('class','button optionalFloatingButton')
	    		.attr('id','newStepFloatingButton')
	    		.attr('onclick',"newStepFloatingButton();")
	    		.text("New Step");
    		if($('#isNewStepAllowHolder').length > 0 && $('#isNewStepAllowHolder').val() == 'no'){
    			$(button).addClass("disabledclass");
    		}
    		$(button).prepend('<i class="fa fa-plus" aria-hidden="true"></i>');
    		$('.floatingButtonsPanel').prepend($(button));
    		/*button = document.createElement('button');
    		$(button)
				.attr('type','button')
				.attr('class','button optionalFloatingButton')
	    		.attr('id','checkBalanceButton')
	    		.attr('onclick',"generalBL_generalClickEvent('checkBalance','0')")
	    		.text("Check Balance");
    		$('.floatingButtonsPanel').prepend($(button));
    		addToContainerWidth = 18;*/			    		
    	}
    	else if(_formCode == 'Request')
    	{
    		addToContainerWidth = 9;// in 'em'
    		$('.new-main-button.floating-button').attr('title',$('#newButton').text());
    		var button = document.createElement('button');
    		$(button)
				.attr('type','button')
				.attr('class','button optionalFloatingButton')
	    		.attr('id','saveAndSendFloatingButton')
	    		.attr('onclick',"$('#saveAndSendButton')[0].click()")
	    		.text($('#saveAndSendButton').text());
    		$(button).prepend('<i class="fa fa-save" aria-hidden="true"></i>');
    		
    		$('.floatingButtonsPanel').prepend($(button));				    		
    	}
    	else if(_formCode == 'ExperimentCP')
    	{   		    
    		// add optional button for Runs Planning table
    		var visibility = 'hidden';
    		if(getActiveTabID() == "ExperimentDesignTab") visibility = 'visible';
    		var $originBtn = $('.dataTableStructButtons[id="expRunPlanningTable_dataTableStructButtons"]').find('.dataTableAddRowButton');
    		
    		var button = document.createElement('button');
    		var $button = $(button);
    		$button.attr('type','button')
    				.attr('id','dataTableAddRowFloatingButton')
					.attr('class','button dataTableApiButton dataTableApiNew optionalFloatingButton')
					.attr('style','visibility:'+visibility+'')
					.attr('title','Add Runs Row')
					.attr('onclick',$originBtn.attr('onclick'))
					.text($originBtn.text());
    		$button.prepend('<i class="fa fa-plus" aria-hidden="true"></i>');
    		$('.floatingButtonsPanel').prepend($button);    				
    	}
    	else if(_formCode == 'SpreadsheetTempla')
    	{
    		addToContainerWidth = 0;
    		$('#newFloatingButton').css('visibility','hidden');
    	}
    	else if(_formCode == 'Sample')
    	{
    		var button = document.createElement('button');
    		$(button)
				.attr('type','button')
				.attr('class','button optionalFloatingButton')
	    		.attr('id','newRequestFloatingButton')
	    		//.attr('onclick',"newRequestFloatingButton();")
	    		.attr('onclick',"$('#newButton')[0].click();")
	    		.attr('title','New Request')
	    		.text("New Request");
    		if( $("#STATUS_ID option:selected").text()!="Active"){
    			$(button).addClass("disabledclass");
    		}
    		$('#newFloatingButton').css('display','none');
    		$('.floatingButtonsPanel').prepend($(button));	    		
    	} else  if(_formCode == 'Template')
    	{
    		var button = document.createElement('button');
    		$(button)
				.attr('type','button')
				.attr('class','button optionalFloatingButton')
	    		.attr('id','updateVersionFloatingButton')
	    		.attr('onclick',"$('#updateVersion')[0].click();")
	    		.attr('title','Update Version')
	    		.text("Update Version");
    		if( $("#updateVersion").hasClass("disabledclass")){
    			$(button).addClass("disabledclass");
    		}
    		$('#newFloatingButton').css('display','none');
    		$('.floatingButtonsPanel').prepend($(button));	    		
    	}
    	
    	var uiTabs = $("#tempalteTabs ul.ui-tabs-nav > li a");
    	if(uiTabs.length > 0)
    	{
	    	// initFloatingButtonsPanel - on TABs click
    		uiTabs.on("click", function() {
				var $this = $(this);
				var href = $this.attr('href');
				var activeTabId = href.substr(1,href.length);
				
				if(_formCode == 'Step')
				{
					if(activeTabId == "ActionsTab")
	    	        {
		            	$('#dataTableAddRowFloatingButton').css('visibility','visible');
		            }
	    			else
	    			{
	    				$('#dataTableAddRowFloatingButton').css('visibility','hidden');
	    				if(activeTabId == "ResultsTab")
		    	        { 	    				
		    				if(checkTabClickFlag("ResultsTab",true)) { //fix bug 7908 - check if result changed using checkTabClickFlag (using the new function checkTabClickFlag)
								onElementDataTableApiChange('selftestresults');
							}
			            }
			            else if(activeTabId == "ReactionTab")
			            {
		            		if(checkTabClickFlag("ReactionTab",true)){//fixed bugs 8174+8179- check if prep_run state was changed
		            			displayAlertDialog('Notice!</br>Step type has been changed but not saved yet.');
		            		} 
			            }
	    			}
				}
				else if(_formCode == 'ExperimentCP')
				{
					if(activeTabId == "ExperimentDesignTab")
			        {
		            	$('#dataTableAddRowFloatingButton').css('visibility','visible');
		            }
		            else
		            {
		            	$('#dataTableAddRowFloatingButton').css('visibility','hidden');
		            }
					if(activeTabId == "SpreadsheetTab" && checkTabClickFlag("SpreadsheetTab",true)){//if click on the spreadsheetTab and flag is up(because the active tab in the init was not spreadsheet)-> then reload the iframe
						//document.getElementById('spreadsheetExcel_spreadIframe').contentWindow.location.reload();
						reloadExcelSheet('spreadsheetExcel');
					}
				}
				else if(_formCode == 'ExperimentFor') { 
					if(activeTabId == "OverviewTab" && checkTabClickFlag("OverviewTab",true)) { //if click on OverviewTab and flag is up (becuse the init tab was not OverviewTab) -> checkTabClickFlag (isFlagUp = true) - will return true and remove the flag => for openCollapsibleIframes just in the first click.
						openCollapsibleIframes();
					}
					if(activeTabId == "SpreadsheetTab" && checkTabClickFlag("SpreadsheetTab",true)){//if click on the spreadsheetTab and flag is up(because the active tab in the init was not spreadsheet)-> then reload the iframe
						//document.getElementById('spreadsheetExcel_spreadIframe').contentWindow.location.reload();
						reloadExcelSheet('spreadsheetExcel');
					}
					if(activeTabId == "SafetyTab" && checkTabClickFlag("SafetyTab",true)){
						onElementDataTableApiChange('materials');
					}
				} else if (_formCode == 'Experiment'){
					if(activeTabId == "SpreadsheetTab" && checkTabClickFlag("SpreadsheetTab",true)){//if click on the spreadsheetTab and flag is up(because the active tab in the init was not spreadsheet)-> then reload the iframe
						//document.getElementById('spreadsheetExcel_spreadIframe').contentWindow.location.reload();
						reloadExcelSheet('spreadsheetExcel');
					}
				} else if (_formCode == 'ExperimentAn'){
					if(activeTabId == "SpreadsheetTab" && checkTabClickFlag("SpreadsheetTab",true)){//if click on the spreadsheetTab and flag is up(because the active tab in the init was not spreadsheet)-> then reload the iframe
						//document.getElementById('spreadsheetExcel_spreadIframe').contentWindow.location.reload();
						reloadExcelSheet('spreadsheetExcel');
					}
					
					if(activeTabId == "SpreadsheetResultsTab" && checkTabClickFlag("SpreadsheetResultsTab",true)){//if click on the spreadsheetTab and flag is up(because the active tab in the init was not spreadsheet)-> then reload the iframe
						//document.getElementById('spreadsheetExcel_spreadIframe').contentWindow.location.reload();
						reloadExcelSheet('spreadsheetResults');
					}
				}
				
				reinitFloatingButtonPanel();
			});
    	}
    	// init Floating Buttons panel
    	initFloatingButtonPanel(addToContainerWidth);
    	
    	$( window ).resize(function() {
    		reinitFloatingButtonPanel();
    	});
	}
}


function initFloatingButtonsPanelMain()
{
	var formsArr = ['Main'];
	var _formCode = $('#formCode').val();
	
	if($.inArray(_formCode, formsArr) != -1)
	{
		$('.mainSaveDefinitionBtn').css('display','none');    	
		var addToContainerWidth = 4;// in 'em'
		
    	// init Floating Buttons panel
    	initFloatingButtonPanelMain(addToContainerWidth);
    	
    	$( window ).resize(function() {
    		reinitFloatingButtonPanelMain();
    	});
	}
}


function initFloatingButtonPanel(addToContainerWidth)
{
	var $buttonsContainer = $('#divFloatingButtonsPanelContainer');
	$buttonsContainer.css('display','inline-block');
	$('.floatingButtonsPanel').css('display','block');
	
	try {
		if(parseFloat(addToContainerWidth) > 1.0)
		{
			// 'em' units used for scalability and responsivness of floating buttons panel.
			var containerWidthEM = parseFloat($buttonsContainer.css('width').match(/(\d+(\.\d*)?)px/)[1]) / parseFloat(getComputedStyle($buttonsContainer[0], "").fontSize.match(/(\d+(\.\d*)?)px/)[1]);
			$buttonsContainer.css('width',containerWidthEM + addToContainerWidth + 'em');
		}
	}
	catch(e) {
		console.error(e);
		$buttonsContainer.css('width','40em');
	}
	
	var $window = $(window);
	var panelLeftPos = window.outerWidth - $buttonsContainer.width() - (window.outerWidth - $window.width())  + $window.scrollLeft();	
	$buttonsContainer.offset({'left':panelLeftPos});
}

function initFloatingButtonPanelMain(addToContainerWidth)
{
	var $buttonsContainer = $('#divFloatingButtonsPanelContainerMain');
	$buttonsContainer.css('display','inline-block');
	$('.floatingButtonsPanel').css('display','block');
	
	try {
		if(parseFloat(addToContainerWidth) > 1.0)
		{
			// 'em' units used for scalability and responsivness of floating buttons panel.
			var containerWidthEM = parseFloat($buttonsContainer.css('width').match(/(\d+(\.\d*)?)px/)[1]) / parseFloat(getComputedStyle($buttonsContainer[0], "").fontSize.match(/(\d+(\.\d*)?)px/)[1]) - 8;
			$buttonsContainer.css('width',containerWidthEM + addToContainerWidth + 'em');
		}
	}
	catch(e) {
		console.error(e);
		$buttonsContainer.css('width','40em');
	}
	
	var $window = $(window);
	var panelLeftPos = window.outerWidth - $buttonsContainer.width() - (window.outerWidth - $window.width())  + $window.scrollLeft();	
	$buttonsContainer.offset({'left':panelLeftPos});
}

function reinitFloatingButtonPanel()
{
	var $window = $(window);
	var $container = $('#divFloatingButtonsPanelContainer');
	var $leftPanel = $('#divFloatingButtonsShowHideIcon');
	
	if($leftPanel.find('.fa-angle-right').length > 0)
	{
		var panelLeftPos = window.outerWidth - $container.width() - (window.outerWidth - $window.width())  + $window.scrollLeft();
//		console.log("window width : " + $(window).width() + " window.outerWidth: "+window.outerWidth, " panel.outerWidth: " + $container.outerWidth() + " panelLeftPos: "+panelLeftPos);		
		$container.offset({'left':panelLeftPos});
	}
	else
	{
		var panelLeftPos = $window.width() - $leftPanel.width()  + $window.scrollLeft();
		$container.offset({'left':panelLeftPos});
	}
}

function reinitFloatingButtonPanelMain()
{
	var $window = $(window);
	var $container = $('#divFloatingButtonsPanelContainerMain');
	var $leftPanel = $('#divFloatingButtonsShowHideIconMain');
	
	if($leftPanel.find('.fa-angle-right').length > 0)
	{
		var panelLeftPos = window.outerWidth - $container.width() - (window.outerWidth - $window.width())  + $window.scrollLeft();
//		console.log("window width : " + $(window).width() + " window.outerWidth: "+window.outerWidth, " panel.outerWidth: " + $container.outerWidth() + " panelLeftPos: "+panelLeftPos);		
		$container.offset({'left':panelLeftPos});
	}
	else
	{
		var panelLeftPos = $window.width() - $leftPanel.width()  + $window.scrollLeft();
		$container.offset({'left':panelLeftPos});
	}
}

function floatingButtonPanelToggleClick(elem)
{
	var $window = $(window);
	
	if($(elem).hasClass('fa-angle-left')) // show buttons
	{
		$(elem).removeClass('fa-angle-left').addClass('fa-angle-right');

		$('.floatingButtonsPanel').css('display','block');		
		var $container = $('#divFloatingButtonsPanelContainer');		
		var panelLeftPos = window.outerWidth - $container.width() - (window.outerWidth - $window.width()) + $window.scrollLeft();		
//		console.log("window width : " + $(window).width() + " window.outerWidth: "+window.outerWidth, " panel.outerWidth: " + $container.outerWidth() + " panelLeftPos: "+panelLeftPos);		
		$container.offset({'left':panelLeftPos});
	}
	else // hide buttons
	{
		$(elem).removeClass('fa-angle-right').addClass('fa-angle-left');
	
		$('.floatingButtonsPanel').css('display','none');		
		var $container = $('#divFloatingButtonsPanelContainer');
		var panelLeftPos = $window.width() - $(elem).parent().width() + $window.scrollLeft();		
		$container.offset({'left':panelLeftPos});
	}

}

function floatingButtonPanelMainToggleClick(elem)
{
	var $window = $(window);
	
	if($(elem).hasClass('fa-angle-left')) // show buttons
	{
		$(elem).removeClass('fa-angle-left').addClass('fa-angle-right');
		
		$('.floatingButtonsPanel').css('display','block');		
		var $container = $('#divFloatingButtonsPanelContainerMain');		
		var panelLeftPos = window.outerWidth - $container.width() - (window.outerWidth - $window.width()) + $window.scrollLeft();		
//		console.log("window width : " + $(window).width() + " window.outerWidth: "+window.outerWidth, " panel.outerWidth: " + $container.outerWidth() + " panelLeftPos: "+panelLeftPos);		
		$container.offset({'left':panelLeftPos});
	}
	else // hide buttons
	{
		$(elem).removeClass('fa-angle-right').addClass('fa-angle-left');
		
		$('.floatingButtonsPanel').css('display','none');		
		var $container = $('#divFloatingButtonsPanelContainerMain');
		var panelLeftPos = $window.width() - $(elem).parent().width() + $window.scrollLeft();		
		$container.offset({'left':panelLeftPos});
	}
	
}


function initStaticPageHeader()
{
	var _formCode = $('#formCode').val();
	if(_formCode == "Step" || _formCode == "ExperimentCP")
	{
		$('.page-header').addClass('page-header-static');
	}
}

function getSequentialNumber(tableName, columnName, elementId)
{
	if($(parent.document).find("#"+tableName+" tr:last").text()!="No data available in table"){
    	var dt = $(parent.document).find("table#"+tableName).DataTable();
    	var lastRow = dt.row(':last').data();
    	
    	//var iCol = dt.column(':contains(Cycle)').index();
    	
    	$(parent.document).find("#"+tableName+" tr:first th.sorting.ui-state-default").each(function(index){
    		if($(this).text()==columnName){	    		
    			 $("#"+elementId).val(parseInt(lastRow[index])+1);
    			 return;
			}
    	});
    	
    	
	} else{
		$("#"+elementId).val(1);
		}
	}
/**
 * open new page (from wf dialog)
 * @param formCode
 * @returns
 */
function doNew(formCode, appendUrl) {
	showWaitMessage(getSpringMessage('pleaseWait'));
	// formCode = $('#newAddress').val(); // temporary
	formCode = onNewButtonIntegration(formCode); // change formCode if needed
	var parentFormCode=	getFormCodeBySeqId($('#formId').val());
	var parentId=$('#formId').val();
	var isTopWindow = '1';
	// collect all smartselect from the source form
	var smartSelectList = "";
	var toReturn = [];  
	if($('input[class="dataTableApiSelectInfo"]:checked').parents('table').length>0){
		var tableDomId = $('input[class="dataTableApiSelectInfo"]:checked').parents('table')[0].id;
	
		var table = $("#"+tableDomId).DataTable();
		var columnIndx = getColumnIndexByColHeader(tableDomId,"_SMARTSELECTALLNONE");
		var column = table.column(columnIndx,{search:'applied'}).nodes();
	    $(column).find("input[type='checkbox'][class='dataTableApiSelectInfo']:checked").each(function (index) {
		    toReturn.push($(this).val());
	    });
		/*$('input[class="dataTableApiSelectInfo"]:checked').each(function (index) {
		    toReturn.push($(this).val());
		});*/
    }
	smartSelectList = toReturn.toString();
	
	var appendUrl_ = '';
	if (typeof appendUrl !== 'undefined' && appendUrl != null && $.trim(appendUrl) != '') {
		appendUrl_ = "&" + appendUrl;
	}
	
	// user special bl
	if(formCode == 'InvItemMaintenancePreventive') {
		formCode = "InvItemMaintenance";
		appendUrl_ = appendUrl_ + "&defaultType=Preventive";
	}
	
	if(formCode == 'Request (Copy Default)') {
		formCode = "Request";
		appendUrl_ = appendUrl_ + "&useDefaultData=1";
	}
	
	if(formCode == 'InvItemMaintenanceBreakdown') {
		formCode = "InvItemMaintenance";
		appendUrl_ = appendUrl_ + "&defaultType=Breakdown&formTab=BreakdownDetails";
	}
	if(formCode == 'Step'&&(parentFormCode.indexOf('Step'))>=0) {
		parentId= $('#EXPERIMENT_ID').val();
	}
	if(formCode == 'StepFr'&&(parentFormCode.indexOf('ExperimentFor'))>=0) {
		addFrameCube('stepIframes','createNewStepIframeData');
		return;
	}
	if($('#formCode').val()=='StepMinFr'){
		isTopWindow = '0';
	}
	
	
	if(formCode == 'Request' && parentFormCode == 'InvItemBatch'){
		var toReturn = [];  
		$('input[class="dataTableApiSelectInfoLabel"]:checked').each(function (index) {
		    toReturn.push($(this).val());
		});
		smartSelectList = toReturn.toString();
	}
	
	if(formCode == 'Request' && parentFormCode == 'Sample'){
		var toReturn = [];  
		smartSelectList = $("#_pass_labelData").val();		
	}
	
	var url = window.location.href;
	if (url.indexOf('&isStructAsPopup=1') > 0){//if the new form is opened from a popup form then the new form will be opened in full screen
		isTopWindow = '0';
	}
	// Done! user special bl
	
	var page = "./init.request?stateKey=" + parent.$('#stateKey').val() + "&formCode=" + formCode + "&formId=-1" + "&userId=" + $('#userId').val() + '&PARENT_ID=' +parentId + "&smartSelectList=" + smartSelectList + appendUrl_;
	
	if(isTopWindow == '1'){
		window.location = page;
	} else {
		window.top.location.href = page;
	}
	return;
}

function generalBL_postAddFrameCube(newFormId,eventName){
	if(eventName == "createNewStepIframeData"){//no warning
		var stepIframeList = $('[id*="AsyncIframe_stepIframes_"]');
		for(var i=0;i<stepIframeList.length-1;i++){
			var stepId = stepIframeList[i].id;
			var iframe = stepIframeList[i];//document.getElementById('AsyncIframe_stepIframes_'+stepId);
			iframe.contentWindow.onElementDataTableApiChange('products');
			//iframe.contentWindow.onElementDataTableApiChange('compositions');
		}
	}
}

function getNewFormCodeDisplay(displayDefault) {
	if(displayDefault == 'StepFr' || displayDefault == 'StepMinFr') {
		return "Step";
	}
	else if(displayDefault == 'InvItemMaintenancePreventive') {
		return "Preventive";
	}
	else if(displayDefault == 'InvItemMaintenanceBreakdown') {
		return "Breakdown";
	}
	else if(displayDefault == 'SubProject') {
		return "Sub Project";
	}
	else if(displayDefault == 'SubSubProject') {
		return "Sub Sub Project";
	}
	else if(displayDefault == 'SelfTest') {
		return "Self-Test";
	}
	else if(displayDefault == "ExperimentSeries") {
		return "Experiment Series";
	}
	else if(displayDefault == "Request") {
		return "Request (New)";
	}
	else if(displayDefault == "RecipeFormulation") {
		return "Recipe";
	}
	
	return displayDefault.replace("InvItem", "");
}

function getEntityIconByFormCode(formCode)
{
	var arr;
	
	var entity_json_obj = {
			"Project":["project","P"],
			"Sub Project":["sub-project","S1"],
			"Sub Sub Project":["sub2-project","S2"],
			"Experiment":["experiment","E"],
			"Request":["request","R"],
			"Request (New)":["request","R"],
			"Self-Test":["self-test","ST"],
			"Embedded Process":["embedded-process","EP"],
			"Action":["action","A"],
			"Sample":["sample","S"],
			"Batch":["batch","B"],
			"Step":["step","S"],
			"Copy Request":["copy-request","R"],
			"Request (Copy Default)":["copy-request","R"],
			"Experiment Series":["experiment-series","ES"],
			"Template":["template","T"]
	}
	
	if(entity_json_obj.hasOwnProperty(formCode)){
		arr = entity_json_obj[formCode];
	} else {
		if(formCode.length > 1) {
			arr = ["",formCode.substr(0,2).toUpperCase()];
		} else if (formCode.length > 0) {
			arr = ["",formCode.substr(0,1).toUpperCase()];
		} else {
			return "";
		}
	}
	return "<span class=\"badge "+arr[0]+"\">"+arr[1]+"</span>";
}


/**
 * return to last page
 * @returns
 */
function doBack() {
	// remove tab selection cookie
	$.removeCookie($('#formCode').val() + '_' + $('#formId').val()  + '_activetab');
    showWaitMessage(getSpringMessage('pleaseWait'));
    $('#doBackForm').submit();
    return;
}

/**
 * Save event of Sample select that is opened from batch form->show a message that validates if the user wants to disconnect the selected samples from the batches they are associated with
 * if so- save event continues as usual, else the samples that are already related to some batch are not selected
 * @param afterSave
 * @param saveAction
 * @returns
 */
function doSaveSampleSelectBatchCheck(afterSave,saveAction){
	var selectedSampleCsv = $('#sampleTable_value').val();
	if($('[id="sampleTable"] thead').length){
		var csvList_ = [];
    	if(selectedSampleCsv.length > 0) {
    		csvList_ = selectedSampleCsv.split(',');
    	} 
		var table_ = $('#sampleTable').DataTable(); 
		// add selected values that are not in lastMultiValues + remove unselected values that are in this table (=> the user unselected them)
	    table_.$('input[Type="checkbox"]').each(function (index) {
	    	if((',' + selectedSampleCsv + ',').indexOf(',' + $(this).val() + ',') <= -1) {
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
	    selectedSampleCsv =  csvList_.toString();
	}
	if(selectedSampleCsv!= null && selectedSampleCsv!=""){
		// make bean
		var stringifyToPush = {
			code : 'selectedSampleCsv',
			val : selectedSampleCsv,
			type : "AJAX_BEAN",
			info : 'na'
		};
	
		// get all data and add removeIndexId
		var allData = getformDataNoCallBack(1);
		var allData = allData.concat(stringifyToPush);
	
		// url call
		var urlParam = "?formId=" + $('#formId').val() + "&formCode="
				+ $('#formCode').val() + "&userId=" + $('#userId').val()
				+ "&eventAction=BatchAssociatedSamples&isNew=" + $('#isNew').val();
	
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

				var batchAssociatedSamples = obj.data[0].val;
				if (batchAssociatedSamples!=null && batchAssociatedSamples != "") {//there are some samples that are associated with batch
					
					openConfirmDialog({
						onConfirm : function (){doSave(afterSave, saveAction);},
						title : 'Warning',
						message : getSpringMessage('Some of the chosen samples have already been associated with some other batches.'
									+'</br> Are you sure you want to disconnect them from the related batch and connect them to the current one?'),
						onCancel: function (){removeBatchAssociatedSample(selectedSampleCsv,batchAssociatedSamples);}
					});
					// if yes- refresh...

				} else {
					doSave(afterSave, saveAction);
				}

			},
			error : handleAjaxError
		});
	} else{
		doSave(afterSave, saveAction);
	}
}

/**
 * Save event of Sample select that is opened from experiment form
 * If request connected to selected sample has another “Destination lab” a warning message should be displayed to user
 * @param afterSave
 * @param saveAction
 * @returns
 */
function doSaveSampleSelectDestLabCheck(afterSave,saveAction){
	showWaitMessage("Please wait...");
    var allData = getformDataNoCallBack(1);

    var urlParam = "?formId=" + $('#formId').val() + "&formCode="
	+ $('#formCode').val() + "&userId=" + $('#userId').val()
	+ "&eventAction=checkSampleRequestDestLab&isNew=" + $('#isNew').val();
    var data_ = JSON.stringify({
        action: "doSave",
        data: allData,
        errorMsg: ""
    });
    $.ajax({
        type: 'POST',
        data:data_,
        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) 
        {
        	hideWaitMessage();
            var message = obj.data[0].val;
			if (message!=null && message != "") {
				
				openConfirmDialog({
					onConfirm : function (){doSave(afterSave, saveAction);},
					title : 'Warning',
					message : getSpringMessage(message),
					onCancel: function (){}
				});
				// if yes- refresh...

			} else {
				doSave(afterSave, saveAction);
			}

		},
        error: handleAjaxError
    });
}

function removeBatchAssociatedSample(selectedSampleCsv,batchAssociatedSamples){
	var csvList_ = selectedSampleCsv.split(',');
	var selectedCsvList = batchAssociatedSamples.split(',');
	for(var i=0;i<selectedCsvList.length;i++){
		csvList_.pop(selectedCsvList[i]);
	}
	$('#sampleTable_value').val(csvList_.toString());
	
	var table_ = $('#sampleTable').DataTable(); 
    
    table_.$('input[Type="checkbox"]').each(function (index) {
		if($(this).prop('checked')) {
			if(csvList_.indexOf($(this).val()) <= -1){//it was removed
				$(this).prop('checked',false);
			}
		}	
	});
}

/**
 * Save event of basic Request->show a message that validates if the user wants to transfer the request to the destination lab
 * if so- the request status is changed automatically to Waiting
 * @param afterSave
 * @param saveAction
 * @returns
 */
function doSaveBaseRequest(afterSave,saveAction){
	if( $("#REQUESTSTATUS_ID option:selected").text()=="Planned" && $('#userState').val()=='source'){
		openConfirmDialog({
	        onConfirm: function(){
	        	$("#REQUESTSTATUS_ID").val($("#REQUESTSTATUS_ID option:contains('Waiting')").val());
	        	 $("#REQUESTSTATUS_ID").trigger('chosen:updated');
	        	 doSaveBaseRequestContinue(afterSave, saveAction);
	        },
	        title: 'Warning',
	        message: getSpringMessage('confirmTransferRequest'),
	        onCancel: function(){
	        	doSave(afterSave, saveAction);
	        }
	    });
	}
	else
	{
		doSaveBaseRequestContinue(afterSave, saveAction);
	}
}

/**
 * Save event of basic Request
 * The request status is changed automatically to Waiting
 * @param afterSave
 * @param saveAction
 * @returns
 */
function doSaveBaseRequestNoConfirm(afterSave,saveAction){
	if( $("#REQUESTSTATUS_ID option:selected").text()=="Planned" && ($('#userState').val()=='source'||  $('#isNew').val() =='1')){
		$("#REQUESTSTATUS_ID").val($("#REQUESTSTATUS_ID option:contains('Waiting')").val());
	    $("#REQUESTSTATUS_ID").trigger('chosen:updated');
	    doSaveBaseRequestContinue(afterSave, saveAction);
	}
	else
	{
		doSaveBaseRequestContinue(afterSave, saveAction);
	}
}

function doSaveMainRequest(afterSave, saveAction) {
	openConfirmDialog({
		onConfirm : function() {
			var requestType = $('#REQUESTTYPE_ID option:selected').text();
			if(requestType == 'Analytical Standards'||requestType=='Analytical Method'||requestType=='Pilot'){//analytical standards & Analytical method usually don’t have samples , thus this message should not display when sending the request.
				setRequiredByElementId('testPurpose',true);
        		doSave(updateRequestStatus, saveAction);
				return;
			}
			showWaitMessage("Please wait...");
		    var allData = getformDataNoCallBack(1);

		    var urlParam =
		        "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&eventAction=requestSaveAndClose" + "&isNew=" + $('#isNew').val();
		    var data_ = JSON.stringify({
		        action: "doSave",
		        data: allData,
		        errorMsg: ""
		    });
		    $.ajax({
		        type: 'POST',
		        data:data_,
		        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		        contentType: 'application/json',
		        dataType: 'json',
		        success: function (obj) 
		        {
		        	hideWaitMessage();
		        	var cnt = obj.data[0].val;
		        	console.log("samples count: " + cnt);
		        	if (cnt == "0")
		        	{     
		        		openConfirmDialog({
		        	        onConfirm: function(){	  
		        	        	setRequiredByElementId('testPurpose',true);
		        	        	doSave(updateRequestStatus, saveAction);//
		        	        	
		        	        },
		        	        title: 'Warning',
		        	        message: getSpringMessage('confirmNoSampleAttachedToRequest'),
		        	        onCancel: function(){
		        	        	doSave(afterSave, saveAction);
		        	        }
		        	    });     
		        	}
		        	else
		        	{
		        		setRequiredByElementId('testPurpose',true);
		        		doSave(updateRequestStatus, saveAction);//
		        	}
		        },
		        error: handleAjaxError
		    });
		},
		title : 'Warning',
		message : getSpringMessage('confirmTransferRequest'),
		onCancel : function() {
			setRequiredByElementId('testPurpose',false);
			doSave(afterSave, saveAction);
		}
	});
}

function updateRequestStatus(){
	showWaitMessage("Please wait...");
    var allData = getformDataNoCallBack(1);

    var urlParam =
        "?formId=" + $('#formId').val() + "&formCode=Request"+"&userId=" + $('#userId').val() + "&eventAction=updateRequestStatus" + "&isNew=" + $('#isNew').val();
    var data_ = JSON.stringify({
        action: "doSave",
        data: allData,
        errorMsg: ""
    });
    $.ajax({
        type: 'POST',
        data:data_,
        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) 
        {
        	hideWaitMessage();
        	 if($('#backUrl').val() != "") { 
        	   		//window.location.href = $('#backUrl').val();
        	   		$("#doBackForm").submit();
        	   		return;	
        	 }
        },
        error: handleAjaxError
    });
}

/**
 * Save event of basic Request->show a message that validates if the user wants
 * to transfer the request to the destination lab even there is no sample
 * attached to request if so- the request status is changed to Waiting, in case
 * Cancel -> request status changed to 'Planned' and request proceed to save
 * 
 * @param afterSave
 * @param saveAction
 * @returns
 */
function doSaveBaseRequestContinue(afterSave,saveAction)
{
	var obj = $('#REQUESTSTATUS_ID option:selected');
	var savedStatus = $('#REQUESTSTATUS_ID').attr("lastselectedname");
	var requestType = $('#REQUESTTYPE_ID option:selected').text();
	if(obj.text() == "Waiting" && savedStatus != "Waiting" && requestType != 'Pilot' 
		&& requestType != 'Analytical Standards' && requestType != 'Analytical Method')//when the request type is the mentioned ones then the samples table is disabled
	{
		showWaitMessage("Please wait...");
	    var allData = getformDataNoCallBack(1);

	    var urlParam =
	        "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&eventAction=checkIfRequestHasSample" + "&isNew=" + $('#isNew').val();
	    var data_ = JSON.stringify({
	        action: "doSave",
	        data: allData,
	        errorMsg: ""
	    });
	    $.ajax({
	        type: 'POST',
	        data:data_,
	        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
	        contentType: 'application/json',
	        dataType: 'json',
	        success: function (obj) 
	        {
	        	hideWaitMessage();
	        	var res = obj.data[0].val;
	        	var parentFormCode = res.split(";")[0];
	        	var cnt = res.split(";")[1];
	        	console.log("samples count: " + cnt);
	        	if (cnt == "0")
	        	{     
	        		openConfirmDialog({
	        	        onConfirm: function(){	        	        	
	        	        	doSave(afterSave, saveAction);
	        	        },
	        	        title: 'Warning',
	        	        message: getSpringMessage('confirmNoSampleAttachedToRequest'),
	        	        onCancel: function(){
	        	        	$("#REQUESTSTATUS_ID").val($("#REQUESTSTATUS_ID option:contains('Planned')").val());
	       	        	 	$("#REQUESTSTATUS_ID").trigger('chosen:updated');	
	       	        	 	doSave(afterSave, saveAction);
	        	        }
	        	    });     
	        	}
	        	else if(cnt == "1")
	        	{
	        		if(parentFormCode == "Action"){
	        			var sample_id = res.split(";")[2];
	        			if(sample_id != undefined && sample_id != null){
	        				$('#ACTION_SAMPLE_ID').val(sample_id);
	        			}
	        		}
	        		doSave(afterSave, saveAction);
	        	}
	        	else
	        	{
	        		if(parentFormCode == "Action"){
	        			openConfirmDialog({
		        	        onConfirm: function(){	  
		        	        	deleteSampleDataRef(afterSave, saveAction);
		        	        },
		        	        title: 'Warning',
		        	        confirmButtonHtml:'send without a sample',
		        	        message: getSpringMessage('please select one of the samples in the table below. otherwise, the request will be sent without a sample'),
		        	        onCancel: function(){
		        	        }
		        	    });   
	        		}
	        		else{
	        			doSave(afterSave, saveAction);
	        		}
	        	}
	        },
	        error: handleAjaxError
	    });
	}
	else
		doSave(afterSave, saveAction);
}

function deleteSampleDataRef(afterSave, saveAction){
	showWaitMessage("Please wait...");
    var allData = getformDataNoCallBack(1);

    var urlParam =
        "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&eventAction=deleteSampleFromRequest" + "&isNew=" + $('#isNew').val();
    var data_ = JSON.stringify({
        action: "doSave",
        data: allData,
        errorMsg: ""
    });
    $.ajax({
        type: 'POST',
        data:data_,
        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) 
        { 
        	hideWaitMessage();
        	doSave(afterSave, saveAction);
        },
        error: handleAjaxError
    });
}
function doSaveFormulationPropRef(afterSave, saveAction)
{
	
    	openConfirmDialog({	
            title: 'Message ',	           
            message: getSpringMessage('saveFormulationPropRef'),
            onConfirm:function(){
	        	doSave(afterSave, saveAction);
            }
        });    	
		            
		
	}

function doSaveBatch(afterSave,saveAction){
	var lastSelectedSample = $('#SAMPLE_ID').val();
	var smartSelectList = "";
    var toReturn = [];  
	$('input[class="dataTableApiSelectInfo"]:checked').each(function (index) {
        toReturn.push($(this).val());
    });
    smartSelectList = toReturn.toString();
    $('#SAMPLE_ID').val(smartSelectList);
    
    if($('#SAMPLE_ID').val()=='' || $('#SAMPLE_ID').val()==lastSelectedSample){
    	doSave(afterSave,saveAction);
    	return;
    }
  //get all data
    var allData = getformDataNoCallBack(1);
     
    //url call
    var urlParam =
        "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&eventAction=" + 'checkSampleHasAssayResult' + "&isNew=" + $('#isNew').val();

    var data_ = JSON.stringify({
        action: "checkSampleHasAssayResult",
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
        	if(obj.data[0].val == '0'){//the selected sample for batch definition does not have an assay result that matches the batch material
        		openConfirmDialog({	
    	            title: 'Warning',	           
    	            message: getSpringMessage('confirmSampleWithNoAssay'),
    	            onCancel: function(){
    	            	$('#SAMPLE_ID').val('');
    	            	doSave(afterSave, saveAction);
        	        },
        	        onConfirm: function(){
        	        	$('#isSampleDefAffect').val('0');
        	        	doSave(afterSave, saveAction);
        	        }
    	        });
        	} else {
        		$('#isSampleDefAffect').val('1');
        		doSave(afterSave, saveAction);
        	}
        },
        error: handleAjaxError
    });	
}
	
/**
 * Save Workup form
 * @returns
 */
function doSaveWorkup(afterSave, saveAction)
{
	var val = $('#STATUS_ID option:selected').text();
	if(val=="Cancelled")
	{
		removeRequiredAttribute();
	}
	var flagResult = true;
	if(saveAction =='UPDATE_STAGE_STATUS'){//fixed bug 7452
	if($('#formCode').val() == 'WorkupFeeding' && $('#STAGE_ID').text() =='Monitoring ')
	{
		var domId = 'materials';
		var struct = $('#' + domId + '_structCatalogItem').val();
	    if (struct == 'WuFeedMaterialRef') {
	        var selectedTable = $('#' + domId).DataTable();
	        var iFeedTimeColumns = selectedTable.column(':contains(Actual Feed Time)').index();
	        var iFinalWeightColumns = selectedTable.column(':contains(Final Weight)').index();
	        
	        $.each(selectedTable.rows().eq(0).data(), function (i, rowData) {
	        	if (typeof rowData !== 'undefined') {
		            if (rowData[iFeedTimeColumns] == "" || rowData[iFinalWeightColumns] == "") 
		            {
		            	openConfirmDialog({	
		    	            title: 'Warning',	           
		    	            message: getSpringMessage('PleaseFillTheRequiredWUTimeAndWeight')
		    	        });
		            	flagResult = false;
		            	return false;
		            }
		            	
		        }
        	});	   
	    } 
	}
	}
	if(flagResult){
		doSave(afterSave, saveAction);
	}
	
}

function validateColumnInUse(){
   
	//get all data
    var allData = getformDataNoCallBack(1);
     
    //url call
    var urlParam =
        "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&eventAction=" + 'isColumnInUse' + "&isNew=" + $('#isNew').val();

    var data_ = JSON.stringify({
        action: "isColumnInUse",
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
        	if(obj.data[0].val == '0'){//column not in use
        		openConfirmDialog({	
    	            title: 'Warning',	           
    	            message: getSpringMessage('confirmDisableColumnMessage'),
    	            onCancel: function(){
        	        	$("input[name='inUse_disabled'][value='In Use']").prop("checked", true);
        	        }
    	        });
        	}
        	else {
        		displayAlertDialog(getSpringMessage('columnInUseMessage'));
        		$('input[name="inUse_disabled"][value="In Use"]').prop('checked', true);
        	}
        },
        error: handleAjaxError
    });
}

/**
 * Check permission before Confirm message before exit form page without save
 * @param functionName
 * @param params
 * @returns
 */
function confirmWithOutSaveWithPermissions(functionName, functionParams, permissParams) 
{
	var formCode = permissParams[0];
	var formId = permissParams[1];
	var isCheckDataChanged = '';
	if(functionParams.length>3){
		isCheckDataChanged = functionParams[3];
	}
	//start permission	
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
//            canRead = "1"; // !!! develop until we set permission
            if(canRead != "1") 
            {
            	displayAlertDialog("Navigation is not allowed");
            } 
            else 
            {
            	//in popups - when confirmWithOutSavePopupMessage is different from NA then when navigating from popup to some other form, the confirmation message will be displayed with no refer to the datachanged flag
            	if(window.self !== window.top&&!isGeneralPopup()){
            		if(isCheckDataChanged!='false') {//its is a popup, and it's necessary to display the message
	        			if(getSpringMessage('confirmWithOutSavePopupMessage')!='NA'){//if the message is NA then do not display the confirm massage
	            			openConfirmDialog({
	            	            onConfirm: functionName,
	            	            title: 'Warning',
	            	            onConfirmParams: functionParams,
	            	            message: getSpringMessage('confirmWithOutSavePopupMessage')
	            	        });
	            			return;
	        			}
            		} else {
            			functionName(functionParams);
            			return; //fix bug 8022 - add return to avoid double tabs when navigate from popup tree
            		}
        		}
            	//console.log("confirmWithOutSave: " + prop.dataChanged);
            	if((isCheckDataChanged=='false')//if should not check the dataChanged, then displaying the confirmation message with no additional check
            			|| skipConfirmSave()) 
            	{ 
            		saveForm(functionName, functionParams);
            		//functionName(functionParams);
            	} else {//data was changed
            		$('#formCode_doBack').val($('#formCode').val());
            		
            		//if (prop.dataChanged)//the dataChanged flag is being already checked in the skipconfirmSave function
        	    	openConfirmDialog({
        	            onConfirm: functionName,
        	            title: 'Warning',
        	            onConfirmParams: functionParams,
        	            message: getSpringMessage('confirmWithOutSaveMessage')
        	        });
               }
            }
        },
        error: handleAjaxError
    });
    //////////////// end permission	
}

function saveForm(functionName, functionParams){
	if($('#formCode').val() == 'SearchReport'){
		$('#isSavedSearch').val("1");//flag- use in ElementDataTableApiImpBL
		doSave((function(){functionName(functionParams);}),'SAVE_FORM_AND_USER_SETTINGS');
		//functionName(functionParams);
	}
	else{
		functionName(functionParams);
	}
}

/**
 * Confirm message before exit form page without save
 * @param functionName
 * @param params
 * @returns
 */
function confirmWithOutSave(functionName, params) {
	var cssName = $("#saveButton").attr("class");
	//if($('#formCode').val() != 'Template' ||  $('#STATUS_ID').val()!='Approved')
	$('#formCode_doBack').val($('#formCode').val());
	if(skipConfirmSave())
	{
		 functionName(params);
	}
	else {		 
		    if (prop.dataChanged || ($('#formCode').val()=='StepMinFr'?parent.prop.dataChanged:0)) {//checks the experiment change status
		        //$('iframe').attr('src', ''); // code cause the bug: embedded iframe of richText(ckeditor) is reseted too.
		        openConfirmDialog({
		            onConfirm: functionName,
		            title: 'Warning',
		            onConfirmParams: params,
		            message: getSpringMessage('confirmWithOutSaveMessage')
		        });
		    } else {
		        functionName(params);
		    }
       
    }
}
/**
 * Confirm message before exit form page without save by click on bread crumb link
 * @param functionName
 * @param params
 * @returns
 */
function confirmWithOutSaveLink(functionName, params) {
    $('#formCode_doBack').val($(params).attr('name'));
    if (skipConfirmSave()) {
    	functionName($('#formCode_doBack'));
    } else {
        //$('iframe').attr('src', ''); // code cause the bug: embedded iframe of richText(ckeditor) is reseted too.
        openConfirmDialog({
            onConfirm: functionName,
            title: 'Warning',
            onConfirmParams: $('#formCode_doBack'),
            message: $('#confirmWithOutSaveMessage').val()
        });
    }
}

/**
 * Confirm message before exit form page without save by click on maim menu
 * @param functionName
 * @param params
 * @returns
 */
function confirmWithOutSaveMainMenu(href) {
	  
    if ((skipConfirmSave()) || (href == "#") || (href == "http://192.168.10.72/Skyline_Dev/wizstabproductlistservlet_")) { // patch "Taro develop" navigation
    	window.location.href=href; 
    	return true;
    } else {
       return openConfirmDialog({
        	onConfirm:function(){
		        	  //$(elem).find('a').attr('href',href);
        		window.location.href=href;
        		return true;
  		        },
            title: 'Warning',
            message: getSpringMessage('confirmWithOutSaveMessage'),//$('#confirmWithOutSaveMessage').val(),
            onCancel:function(){
	        	  return false;
	        }
        });
    }
}
/**
 * check if confirm message is needed. 
 * Note! in the popups the dataChanged prop of the parent is not evaluated (so this function will return true if there is no change in the popup only in the parent)
 *   [ We can pass the parent prop.dataChanged to the popup in the next version, until then we can change the default behaviour by always return true in popup (form with #save_ or ok buttons) or by put the specific form code the confirm is always needed - before this commit there where no confirm message in navigation from tables)] 
 * @returns
 */
function skipConfirmSave() {
	
	//scpecific BL
	if($('#formCode').val() == 'Template' &&  $('#STATUS_ID').val() =='Approved') {
		return true;
	} 
	if($('#formCode').val() == "MaterialDuplicates")
	{
		prop.dataChanged = true;
		return false;
	}
	if($('#isNew').val()=="1"){//fixed bug 7708
		return false;
	}
	
	//check dataChanged (if no change we can skip the message)
	var isChanged_ = false;
	try {
		isChanged_ = prop.dataChanged;
		if($('#formCode').val()=='StepMinFr'){
			isChanged_ = isChanged_|| parent.prop.dataChanged;
		}
	} catch(e) {}
	if(!isChanged_) {
		return true;
	}
 
	//check if can be saved (if not we need to skeep the confirm message)
	var cssName = "";
	if($("#saveButton").length > 0) {
		cssName = $("#saveButton").attr("class"); // full screen
	}  else if($("#save_").length > 0) {
		cssName = $("#save_").attr("class"); //popup 
	}
	if(cssName.toLowerCase().indexOf("disabled") > -1) {
		return true;
	}
	
	return false;
}

function confirmStatusChanged(){
	var val = $('#STATUS_ID option:selected').text();
	var lastval = $('#STATUS_ID option[value = "'+$('#STATUS_ID').attr('lastvalue')+'"]').text();
	if(val=="Disabled"){
		openConfirmDialog({
			onCancel:function(){
	        	$('#STATUS_ID').val($("#STATUS_ID option:contains('"+lastval+"')").val());
        		$("#STATUS_ID").trigger('chosen:updated');},
            title: 'Warning',	           
            message: getSpringMessage('confirmStatusDisabled')
        });
	}
}

/**
 * Confirm message before status changes to cancelled 
 * @param functionName
 * @param params
 * @returns
 */
function confirmCancelledWorkup(functionName, params) {
	
	var val = $('#STATUS_ID option:selected').text();
	if(val=="Cancelled"){
	
	   $('#formCode_doBack').val($('#formCode').val());
	    //if (prop.dataChanged) {
	       
	        openConfirmDialog({
	        	onCancel:resetStatus,
	            title: 'Warning',	           
	            message: getSpringMessage('confirmCancelledWorkupMessage')
	        });
	    //} else {
	    //    functionName(params);
	    //}
	}
}



function resetStatus ()
{
	$("#STATUS_ID_chosen a span").text("Active");
	$("#STATUS_ID_chosen .chosen-results li").each(function(){
		var txt = $(this).text();
		if(txt == 'Active'){$(this).addClass('result-selected');}
		else if(txt == 'Cancelled'){$(this).removeClass('result-selected');}
	});
	//$("#STATUS_ID option:contains('Active')").attr('selected', 'selected');
	$("#STATUS_ID option").each(function(){
		var txt = $(this).text();
		if(txt == 'Active'){
			$(this).attr('selected', 'selected');
			$("#STATUS_ID").val($(this).attr('value'));
			}
		else if(txt == 'Cancelled'){
			$(this).removeAttr('selected', 'selected');}
	});
	
	
	}
	
/**
 * Disable all elements in the page
 * @param enableElements- list of element to be editable
 * @param enableEditButton
 * @returns
 */
function generalBL_disablePage(enableElements, enableEditButton) { 
	console.log('generalBL_disablePage(): enableElements = ',enableElements);
	var startTime = new Date().getTime();
	
	disableAllDataTableLements(enableElements);
	$('input:not([type="search"],.firstString,[id*="_LinkToLastSelection"],.dataTableApiSelectInfo,[name="checkCharSample"]),select:not(#selectWfFormCode,[name*="_length"], .datatableapiselect)~div:not(:has(.linkElement)),textarea:not(.ckeditor),i.fa-calculator,i.fa-search,i.fa-trash,div[chemdoodle],.excelSheet,.dataTableApiAddMultiRows').addClass('disablePage');
    $('button:not(#newButton,#close_back,#cloneButton,.floating-button,.ui-dialog-titlebar-close,:contains("Confirm"),:contains("Continue"),:contains("Cancel"),:contains("Save"):not(.dataTableApiAdd),.dataTableApiEditShared,.dataTableApiButtonTools,.dataTableApiView,.dataTableApiEdit,.ireport,.dataTableApiLabel,.collapsible_iframes,.collapsible_button,.note-btn)').addClass('disablePage');//,
    $('select.disablePage,input[type="radio"].disablePage,input[type="checkbox"]:not(.dataTableApiSelectInfo,[name="checkCharSample"]).disablePage').prop("disabled", true);
    $('div.dateInput').addClass('disablePage'); 
    $('.fileUpload').addClass('disablePage');
    
    var ckeditorEnableList = getCkeditorEnableList(enableElements);
    
    $('[name=parentDiagramContainer]').each(function(){
		var elemObj = $(this);
    	var elementDomId_ = $(elemObj).attr('id');
    	setDisabledDiagram(true,elemObj);
	});
    $('div.ckeditor').each(function(){
    	var elemObj = $(this);
    	var elementDomId_ = $(elemObj).attr('id');
    	if( (ckeditorEnableList == "" || ("," + ckeditorEnableList + ",").indexOf("," + elementDomId_ + ",") == -1) ) {
    		setDisabledByElementId(elementDomId_, true);
    	}
    });   
    
    if(enableElements != undefined && enableElements != null)
    {
	    for(var i = 0; i<enableElements.length; i++) 
		{
	    	if ($('#' + enableElements[i]).length) {
		        $('#' + enableElements[i]).removeClass('disablePage');
		        if($('#' + enableElements[i]).is("select")){
		        	$('#' + enableElements[i]+"_chosen").prop("disabled",false);
		        	$('#' + enableElements[i]+"_chosen").removeClass('disablePage');
		        }
		        else if($('#' + enableElements[i]).is("div.ckeditor")){
		        	setDisabledByElementId(enableElements[i], false);
		        }
		        else if($('#' + enableElements[i]).is("textarea:not(.ckeditor)")){
		        	//$('#' + enableElements[i]+"_parent").prop("disabled",false);
		        	$('#' + enableElements[i]+"_parent").removeClass('disablePage');
		        }
		        else if($('#' + enableElements[i]).is('[name=parentDiagramContainer]')){
		        	setDisabledDiagram(false,$('#'+enableElements[i]));
		    	}
		        else if($('#' + enableElements[i]).is("table")){
		        	//$('#' + enableElements[i]+"_parent").prop("disabled",false);
		        	$('#' + enableElements[i]+"_dataTableStructButtons").find('.fileUpload').removeClass('disablePage');
		        	//$('#' + enableElements[i]+"_dataTableStructButtons button:not(.dataTableApiRemove,.dataTableApiNew)").removeClass('disablePage'); // yp 16122019 add :not(.dataTableApiRemove,.dataTableApiNew) to fix bug (remove and add row button was enabled in edit table)
		        	$('#' + enableElements[i]+"_dataTableStructButtons button").removeClass('disablePage'); // adib 24062020 removed :not(.dataTableApiRemove,.dataTableApiNew) to fix bug when there are tables that actually should be enabled. the case of the edit tables that are enabled occurs in the template->it was fixed by changing the func' setEmptyContentAuthzFunc
		        	changeSingleDTLabelByDisabledState(enableElements[i], false);//+"_dataTableStructButtons"
		        	
		        }
		        else if($('#' + enableElements[i]).is("input[type='checkbox']")){
		        	$('#' + enableElements[i]).prop('disabled',false);
		        }
		        else if($('#' + enableElements[i]).hasClass("date-picker")){
		        	$('#' + enableElements[i]).parent().removeClass('disablePage');
		        }
		    }
		}
    }
    if (enableEditButton == undefined || enableEditButton == null || !enableEditButton) { // yp 13112018 before the "remove edit button" we enabled the edit - because there is no edit button we prevent save when the edit was originally disabled
    	$('.submit-button-row').find('button:contains("Save")').addClass('disablePage');
    	$('#saveFloatingButton').addClass('disablePage');
    	$('#generalDisabledFlagParam').val('1'); // yp add this part of the "open forms in edit" task (and remove edit button) it uses the rich text and data table that load after the screen render (from this version it will happen only if no edit permission on the page)
    	disableAllDataTableLements(enableElements);
    }
    $('div.divAdditCustomInfo').removeClass('disablePage'); 
    $('select.disablePage').trigger('chosen:updated');
    $('.date-picker.disablePage').datepicker('disable');
    
    console.log( 'generalBL_disablePage took at: '+(new Date().getTime()-startTime)+'mS' );
//    var enableElementsString = "";
//    if(enableElements != null && enableElements != undefined)
//    {
//	    for(var i = 0; i<enableElements.length; i++) 
//		{
//	    	if(enableElementsString == "")enableElementsString="#";
//	    	enableElementsString += enableElements[i]+(i + 1 < enableElements.length?",#":"");
//		}
//    }
}

function getCkeditorEnableList(enableElements) {
	 var eList = "";
	 if(enableElements != undefined && enableElements != null) {
	    for(var i = 0; i<enableElements.length; i++) {
	    	 if($('#' + enableElements[i]).is("div.ckeditor")) {
	    		 if(eList == "") {
	    			 eList = enableElements[i];
	    		 } else {
	    			 eList = eList + "," + enableElements[i];
	    		 }
		     }
		}
	 }
	 return eList;
}

/**
 * Disable all elements in the tab
 * 
 */
function generalBL_disableTab(divID,enableElements) {
	console.log('generalBL_disableTab(): divID = ',divID);
	var startTime = new Date().getTime();
	//disableAllDataTableLements(enableElements);//ta 240220 - 
	$('#' +divID).find('input:not([type="search"],.firstString,[id*="_LinkToLastSelection"], .dataTableApiSelectInfo,[name="checkCharSample"]),select:not(#selectWfFormCode,[name*="_length"], .datatableapiselect)~div:not(:has(.linkElement)),textarea:not(.ckeditor),i.fa-calculator,i.fa-search,i.fa-trash,div[chemdoodle],.excelSheet,.dataTableApiAddMultiRows').addClass('disablePage');
    $('#' +divID).find('button:not(#newButton,#close_back,#cloneButton,.floating-button,.ui-dialog-titlebar-close,:contains("Confirm"),:contains("Continue"),:contains("Cancel"),:contains("Save"),.dataTableApiEditShared,.dataTableApiButtonTools,.dataTableApiView,.dataTableApiEdit,.ireport,.dataTableApiLabel,.collapsible_iframes,.collapsible_button,.note-btn)').addClass('disablePage');//,
    $('#' +divID).find('select.disablePage,input[type="radio"].disablePage,input[type="checkbox"]:not(.dataTableApiSelectInfo,[name="checkCharSample"]).disablePage').prop("disabled", true);
    $('#' +divID).find('div.dateInput').addClass('disablePage');   
    $('#' +divID).find('div[ckeditor]').addClass('disablePage'); 
    $('div.divAdditCustomInfo').removeClass('disablePage'); 
    $('select.disablePage').trigger('chosen:updated'); 

    $('#' +divID).find('table[formElement=1]').each(function(){
    	var elemObj = $(this);
    	if(enableElements != undefined && enableElements != null){
	    	if(enableElements.indexOf($(elemObj).attr('id'))==-1){
	    		changeSingleDTLabelByDisabledState($(elemObj).attr('id'), true);//+"_dataTableStructButtons"
	    	}
    	}
    });
    
    //disabling the richtext
    $('#' +divID).find('div.ckeditor[formElement=1]').each(function(){
    	var elemObj = $(this);
    	setDisabledByElementId($(elemObj).attr('id'), true);
    });
    
    $('#' +divID).find('[name=parentDiagramContainer]').each(function(){
		var elemObj = $(this);
    	var elementDomId_ = $(elemObj).attr('id');
    	setDisabledDiagram(true,elemObj);
	});
    
    if(enableElements != undefined && enableElements != null)
    {
	    for(var i = 0; i<enableElements.length; i++) 
		{
	    	if ($('#' + enableElements[i]).length) {
		        $('#' + enableElements[i]).removeClass('disablePage');
		        if($('#' + enableElements[i]).is("select")){
		        	$('#' + enableElements[i]+"_chosen").prop("disabled",false);
		        	$('#' + enableElements[i]+"_chosen").removeClass('disablePage');
		        }
		        else if($('#' + enableElements[i]).is("div.ckeditor")){
		        	setDisabledByElementId(enableElements[i], false);
		        }
		        else if($('#' + enableElements[i]).is("textarea:not(.ckeditor)")){
		        	//$('#' + enableElements[i]+"_parent").prop("disabled",false);
		        	$('#' + enableElements[i]+"_parent").removeClass('disablePage');
		        }
		        else if($('#' + enableElements[i]).is("table")){
		        	//$('#' + enableElements[i]+"_parent").prop("disabled",false);
		        	$('#' + enableElements[i]+"_dataTableStructButtons button").removeClass('disablePage');
		        	changeSingleDTLabelByDisabledState(enableElements[i], false);//+"_dataTableStructButtons"
		        }
		        else if($('#' + enableElements[i]).is("input[type='checkbox']")){
		        	$('#' + enableElements[i]).prop('disabled',false);
		        }
		        else if($('#' + enableElements[i]).is('[name=parentDiagramContainer]')){
		        	setDisabledDiagram(false,$('#'+enableElements[i]));
		    	}
		        else if($('#' + enableElements[i]).hasClass("date-picker")){
		        	$('#' + enableElements[i]).parent().removeClass('disablePage');
		        }
		    }
		}
    }
    console.log( 'generalBL_disableTab took at: '+(new Date().getTime()-startTime)+'mS' );
}
function generalBL_enableTab(divID) {
	console.log('generalBL_enableTab(): divID = ', divID);
	var startTime = new Date().getTime();
	
	 var tabId = $('[aria-controls=' + divID + ']').attr('aria-labelledby');
	$('#'+tabId).removeClass('authorizationDisabled');
	
	 $('#' +divID +' table tr td:has(input)').removeClass('disablePage');
	 $('#' +divID +' table tr td:has(select)').removeClass('disablePage');
	 $('#' +divID +' table tr td:has(button)').removeClass('disablePage');
	 $('select.disablePage,input[type="radio"].disablePage,input[type="checkbox"].disablePage').prop("disabled", false);
	 //$('#' +divID).find('select.editableSmartCell').prop("disabled", false).trigger('chosen:updated'); 
	 
	 $('#' +divID).find('[formElement = 1], button').each(function() {
		 var elem = $(this);
		 var id = $(elem).attr('id');
	        $('#' + id).removeClass('disablePage');
	        if($('#' + id).is("select")){
	        	$('#' + id +"_chosen").prop("disabled",false);
	        	$('#' + id +"_chosen").removeClass('disablePage');
	        }
	        else if($('#' + id).is("textarea:not(.ckeditor)")) {
	        	//$('#' + enableElements[i]+"_parent").prop("disabled",false);
	        	$('#' + id +"_parent").removeClass('disablePage');
	        }
	        else if($('#' + id).is("div.ckeditor")) {
	        	//$('#' + enableElements[i]+"_parent").prop("disabled",false);
	        	setDisabledByElementId(id, false);
	        }
	        else if($('#' + id).is("table")){
	        	//$('#' + enableElements[i]+"_parent").prop("disabled",false);
	        	$('#' + id +"_dataTableStructButtons button").removeClass('disablePage');
	        	changeSingleDTLabelByDisabledState(id , false);//+"_dataTableStructButtons"
	        }
	    });
	
	 console.log( 'generalBL_enableTab took at: '+(new Date().getTime()-startTime)+'mS' );
}


/**
 * change landing page on new button and do some operations according to the client requirements
 * @param formCode
 * @returns -1 in case of operating some actions according to the client requirements
 * else returns the formCode of the landing page 
 */
function onNewButtonIntegration(formCode,currentFormCode,formId,parentId) {
    formCode = (formCode == 'Experiment') ? "ExperimentMain" : formCode;
    //formCode = (formCode == 'Request') ? "RequestMain" : formCode;
    formCode = (formCode == 'Workup') ? "WorkupMain" : formCode;
    formCode = (formCode == 'ExperimentSeries') ? "ExpSeriesMain" : formCode;
    formCode = (formCode == 'SelfTest') ? "SelfTestMain" : formCode;
    formCode = (formCode == 'Sample') ? "SampleMain" : formCode;
    formCode = (formCode == 'Template') ? "TemplateMain" : formCode;
    formCode = (formCode == 'InvItemBatch') ? $("#formCode").val().indexOf("Material")!=-1 || $("#formCode").val().indexOf("Sample")!=-1? formCode:"BatchMain" : formCode;
    
    if(formCode == 'InvItemBatch'&& currentFormCode == 'MaterialSearch'){
    	var reactantId = parent.$('#formId').val();
    	var stepId = window.top.$('#formId').val();
    	
    	var additionalInfAfterSave = {arg_materialId:parentId,arg_reactantId:reactantId};
    	window.top.doSaveAfterValidation(saveReactant, "NA", 0, "", "","",additionalInfAfterSave);//save parent step
    	
    	//doSaveAfterValidation("", "NA", 0, "", "", parent.$('*'));//save reactant
    	//
    	//doSaveAfterValidation(navigateToNewBatch, "NA", 0, "", "", window.top.$('*'), additionalInfAfterSave);//save step
    	//var retVal = saveParentReactant(reactantId,formCode,formId,stepId,parentId);
    	return -1;
    }
    return formCode;
}

function saveReactant(additionalInfAfterSave){
	parent.$('#INVITEMMATERIAL_ID').val(additionalInfAfterSave.arg_materialId);
	parent.doSaveAfterValidation(navigateToNewBatch, "NA", 0, "", "","",additionalInfAfterSave);//save parent reactant
}

function navigateToNewBatch(additionalInfObj){
	var additionalUrlParams = '&reactantId=' + additionalInfObj.arg_reactantId;//$('#formId').val();
	var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=InvItemBatch&formId=-1&userId=" + $('#userId').val() + '&PARENT_ID=' + additionalInfObj.arg_materialId + additionalUrlParams;
	if(window.self !== window.top) {    
  		window.top.location.href = page;  
	} else{
		window.location = page;  
	}
}

function saveStepBeforeNavigation(reactantId,formCode,formId,stepId,parentId){
	var additionalUrlParams = '&reactantId=' + reactantId;
	var allData = getformDataNoCallBack(2,window.top.$('*')); 
	var urlParam =
         "?formId=" + stepId + "&formCode=Step&userId=" + $('#userId').val() + "&saveAction=&isNew=" + window.top.$('#isNew').val() + "&stateKey=" + $('#stateKey').val()
         +"&saveName=&useLoginsessionidScopeFlag=" + window.top.$('#useLoginsessionidScopeFlag').val()  + "&description=&formPathInfo=" + encodeURIComponent(window.top.$('#formPathInfo').val());
	
     var data_ = JSON.stringify({
         action: "doSave",
         data: allData,
         errorMsg: ""
     });
     showWaitMessage();
     $.ajax({
         type: 'POST',
         data: data_,
         url: "doSave.request" + urlParam,
         contentType: 'application/json',
         dataType: 'json',
         success: function (obj) {
             if (obj.errorMsg != null && obj.errorMsg != '') {
                 hideWaitMessage();
                 displayAlertDialog(obj.errorMsg);
             } else if (obj.data[0].val == "-1") {
                 hideWaitMessage();
                 displayAlertDialog(getSpringMessage('updateFailed'));
             } else if (obj.data[0].val.toString().indexOf(',') != '-1' && obj.data[0].val.toString().substring(0, 2) == '-2') {
                 doSaveMessage = getSpringMessage(obj.data[0].val.split(',')[1]);/*.split("_").join(" ").toLowerCase();*/
                 hideWaitMessage();
                 displayAlertDialog(doSaveMessage/*.charAt(0).toUpperCase() + doSaveMessage.slice(1)*/ + " " + getSpringMessage('alreadyExistsInSystem'));//ab 22/03/18 fixed bug 4161    
             } else if (obj.data[0].val.toString().indexOf(',') != '-1' && obj.data[0].val.toString().substring(0, 2) == '-3') {
                 doSaveMessage = obj.data[0].val.split(',')[1].split("_").join(" ").toLowerCase();
                 hideWaitMessage();
                 displayAlertDialog(doSaveMessage.charAt(0).toUpperCase() + doSaveMessage.slice(1) + " " + getSpringMessage('invalidInSystem'));  
             } else {    
            	 var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=" + formCode + "&formId=" + formId + "&userId=" + $('#userId').val() + '&PARENT_ID=' + parentId + additionalUrlParams;
            	 if (obj.data[0].info.toString() !='') {//there's is a message to display
            		 //displayAlertDialog(getSpringMessage(obj.data[0].info.toString()));
            		 hideWaitMessage();
            		 openConfirmDialog({
          		        onConfirm: function(){
          		        	showWaitMessage();
	          		    	if(window.self !== window.top) {    
	          		    		window.top.location.href = page;  
	          		    		return -1;
	          		    	}
          		        },
          		        title: 'Alert',
          		        message: getSpringMessage(getSpringMessage(obj.data[0].info.toString())),
          		        onCancel: function(){
          		        	showWaitMessage();
	          		    	if(window.self !== window.top) {    
	          		    		window.top.location.href = page;  
	          		    		return -1;
	          		    	}
          		        }
          		    },true);
            	 } else {
            		 	showWaitMessage();
            	    	if(window.self !== window.top) {    
            	    		window.top.location.href = page; 
            	    	}
            	 }
             }
         },
         error: handleAjaxError
     });
}

/**
 * get Forward Page
 * 
 * @returns
 */
function getForwardPage() {
    var formCode = $('#formCode').val();
    if (formCode == 'ExperimentMain' || formCode == 'ExpCloneMain') {
        // detect if the next page is ExperimentAn or Experiment by 'protocol type'
        if ($("#PROTOCOLTYPE_ID option:selected").text() == 'Analytical') {
            formCode = 'ExperimentAn';
        } 
        else if ($("#PROTOCOLTYPE_ID option:selected").text() == 'Formulation') {
            formCode = 'ExperimentFor';
        }
        else if ($("#PROTOCOLTYPE_ID option:selected").text() == 'Parametric' && $("#EXPERIMENTTYPE_ID option:selected").text() == 'Corrosion' )  {
            formCode = 'ExperimentPrCR';
        }
        else if ($("#PROTOCOLTYPE_ID option:selected").text() == 'Parametric' && $("#EXPERIMENTTYPE_ID option:selected").text() == 'Viscosity' )  {
            formCode = 'ExperimentPrVS';
        }
        else if ($("#PROTOCOLTYPE_ID option:selected").text() == 'Parametric' && $("#EXPERIMENTTYPE_ID option:selected").text() == 'TSU' )  {
            formCode = 'ExperimentPrTS';
        }
        else if ($("#PROTOCOLTYPE_ID option:selected").text() == 'Parametric' && $("#EXPERIMENTTYPE_ID option:selected").text() == 'Bottles' )  {
            formCode = 'ExperimentPrBT';
        }
//        else if ($("#PROTOCOLTYPE_ID option:selected").text() == 'Parametric' && $("#EXPERIMENTTYPE_ID option:selected").text() == 'General' ) {
//            formCode = 'ExperimentPrGn';
//        } -> default parameteric experiment type is general! ->
        else if ($("#PROTOCOLTYPE_ID option:selected").text() == 'Parametric') {
        	formCode = 'ExperimentPrGn';
        }
        else if ($("#PROTOCOLTYPE_ID option:selected").text() == 'Continuous Process') { //add ExperimentCP for "Continuous Process"
            formCode = 'ExperimentCP';
        }
        else {
            formCode = 'Experiment';
        }
    }
    /*if (formCode == 'RequestMain') {
            formCode = 'Request';
        }*/
    if (formCode == 'WorkupMain') {
        formCode = 'Workup';
    }
    if (formCode == 'ExpSeriesMain') {
        formCode = 'ExperimentSeries';
    }
    if (formCode == 'SampleMain') {
        formCode = 'Sample';
    }
    if (formCode == 'TemplateMain') {
        formCode = 'Template';
    }
    
   
    if (formCode == 'SelfTestMain') {
        if ($("#TYPE_ID option:selected").text() == 'Appearance') {
            formCode = 'STestAppearance';
        } 
        else if ($("#TYPE_ID option:selected").text() == 'Density') {
            formCode = 'STestDensity';
        } 
        else if ($("#TYPE_ID option:selected").text() == 'pH') {
            formCode = 'STestpH';
        } 
        else if ($("#TYPE_ID option:selected").text() == 'Foaming') {
            formCode = 'STestFoaming';
        } 
        else if ($("#TYPE_ID option:selected").text() == 'Flash Point') {
            formCode = 'STestFlashPoint';
        } 
        else if ($("#TYPE_ID option:selected").text() == 'Wet Sieve') {
            formCode = 'STestWetSieve';
        } 
        else if ($("#TYPE_ID option:selected").text() == 'Pourability') {
            formCode = 'STestPourability';
        } 
        else if ($("#TYPE_ID option:selected").text() == 'Suspensibility') {
            formCode = 'STestSuspensibilit';
        } 
        else if ($("#TYPE_ID option:selected").text() == 'Particle Size Distribution by Light Difraction') {
            formCode = 'STestParticleSize';
        } 
        else if ($("#TYPE_ID option:selected").text() == 'Cold Test') {
            formCode = 'STestCold';
        }
        else if ($("#TYPE_ID option:selected").text() == 'Emulsion Stability') {
            formCode = 'STestEmulsionStab';
        }
        else if ($("#TYPE_ID option:selected").text() == 'SprayAbility') {
            formCode = 'STestSprayAbility';
        }
        
        else {
            formCode = 'SelfTest';
        }
    }
    if(formCode == 'BatchMain'){
    	formCode = 'InvItemBatch';
    }

 
    return formCode;
}

/**
 * hideUnnecessaryFormTypesFormBuilder
 * @returns
 */
function hideUnnecessaryFormTypesFormBuilder() {

}

/**
 * hideUnnecessaryElementsFormBuilder
 * @param selectArray
 * @returns
 */
function hideUnnecessaryElementsFormBuilder(selectArray) {
	return selectArray;
}


/**
 * Save event of Formulation Step:
 *   When the last step of formulation experiment (excluding these that were canceled) is about to became at status FINISHED/CANCELLED ->  
 *   	show confirm message that validates if the Experiment has all SelfTests according to experiment project formulation type
 * @param afterSave
 * @param saveAction
 * @returns
 */
function doSaveStepFr(afterSave, saveAction)
{ 
	var current_status = $('#STATUS_ID option:selected').text();
	var savedStatus = $('#STATUS_ID').attr("lastselectedname");
	
	try
	{
		if((current_status=='Completed'||current_status=='Finished') && savedStatus=='Active'){//status has been changed to Completed
			doChangeStatusToCompleted(savedStatus,afterSave,saveAction,'Formulation')
		} else {
			doSave(afterSave,saveAction);
		}
	}
	catch(e){
		console.log(e);
	}
}


function doSaveExperimentFr(afterSave, saveAction)
{
	var isEnableSpreadsheet = $('#isEnableSpreadsheet').val();
	//if the user works on the spreadsheet, then the validations that are executed on the overview tab are unnecessary
	if(isEnableSpreadsheet == 'Yes'){
		doSave(afterSave,saveAction);
	} else {
		var allData = getformDataNoCallBack(1);		
		// url call
		var urlParam = "?formId="+ $('#formId').val()
					+ "&formCode="+ $('#formCode').val()
					+ "&userId="+ $('#userId').val()
					+ "&eventAction=buildLogAndValidateRecipeExperimentConnection"
					+ "&isNew=" + $('#isNew').val();
	
		var data_ = JSON.stringify({
			action : "buildLogAndValidateRecipeExperimentConnection",
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
					hideWaitMessage();
					displayAlertDialog(obj.errorMsg);
				} 
				else
				{
					hideWaitMessage();
					var fullObj = funcParseJSONData(obj.data[0].val); 
					if(fullObj!=null && Object.keys(fullObj).length > 0)
					{
						if(fullObj.hasOwnProperty("0")){
							displayAlertDialog(fullObj["0"]);
						}
						else if(fullObj.hasOwnProperty("1")){//contains a confirmation of whether to continue the save and clear connection or not
							openConfirmDialog({
						        onConfirm: function(){//update an indicator to clear the connection
						        	$('#doClearConnection').val('1');
						        	doSave(afterSave,saveAction);
						        	if(fullObj.hasOwnProperty("recipeList")){
						        		$('#recipeList').val(fullObj["recipeList"]);
						        	}
						        },
						        title: 'Warning',
						        message: getSpringMessage(fullObj["1"])
						    });
						} else if(fullObj.hasOwnProperty("2")){//confirmation of keep or clear the connection
							openConfirmDialog({
						        onConfirm: function(){//update an indicator to not clear the connection
						        	if(fullObj.hasOwnProperty("recipeList")){
						        		$('#recipeList').val(fullObj["recipeList"]);
						        	}
					        		$('#doClearConnection').val('0');
					        		doSave(afterSave,saveAction);
						        },
						        title: 'Warning',
						        message: getSpringMessage(fullObj["2"]),
						        onCancel: function(){//update an indicator not clear the connection
						        	if(fullObj.hasOwnProperty("recipeList")){
						        		$('#recipeList').val(fullObj["recipeList"]);
						        	}
						        	$('#doClearConnection').val('1');
						        	doSave(afterSave,saveAction);
						        },
						        confirmButtonHtml:'Keep',
						        cancelButtonHtml: 'Clear',
						        hideCloseIcon: true
						    });
						} else {
							if(fullObj.hasOwnProperty("recipeList")){
				        		$('#recipeList').val(fullObj["recipeList"]);
				        	}
							doSave(afterSave,saveAction);
						}
					} else {
						doSave(afterSave,saveAction);
					}
				}
			},
			error : handleAjaxError
		});
	}
}


function doSaveWithConfirmCharSample(afterSave,saveAction){
	var obj = $('#STATUS_ID option:selected');
	var savedStatus = $('#STATUS_ID').attr("lastselectedname");
	if (obj.text() == "Completed")//the experiment is Completed
	{
		var urlParam = "?formId="+ $('#formId').val()
					+ "&formCode="+ $('#formCode').val()
					+ "&userId="+ $('#userId').val()
					+ "&eventAction=checkExperimentCompletedCharSample"
					+ "&isNew=" + $('#isNew').val();
		
		var data_ = JSON.stringify({
			action : "checkExperimentCompletedCharSample",
			data : [{
				code : "characterizedSample",
				val : $('#characterizedSample').val(),
				type : "AJAX_BEAN",
				info : 'na'
			}],
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
				else if(obj.data[0].val.indexOf(";")!="-1")//There is a confirmation message
				{
					$('#characterizedSample').val(obj.data[0].val.substr(0,obj.data[0].val.indexOf(";")));
					if(obj.data[0].val.substr(obj.data[0].val.indexOf(";")+1)!=""){
							openConfirmDialog({
					        onConfirm: function(){
					        	doSaveWithConfirm(afterSave, saveAction);
					        },
					        title: 'Warning',
					        message: getSpringMessage(obj.data[0].val.substr(obj.data[0].val.indexOf(";")+1))
					    });
					} else {
						doSaveWithConfirm(afterSave, saveAction);
					}
					hideWaitMessage();
				} else {
					doSaveWithConfirm(afterSave, saveAction);
				}
				
			},
			error : handleAjaxError
		});
	} else {
		doSaveWithConfirm(afterSave, saveAction);
	}
		
}

function doChangeStatusToCompleted(savedStatus,afterSave,saveAction,protocol){
	var doChangeStatus = true;
	showWaitMessage("Please wait...");
    var allData = [];

    var urlParam =
        "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&eventAction=checkIfExpHasActiveSteps" + "&isNew=" + $('#isNew').val();
    var data_ = JSON.stringify({
        action: "doSave",
        data: allData,
        errorMsg: ""
    });
    $.ajax({
        type: 'POST',
        data:data_,
        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) 
        {
        	hideWaitMessage();
        	var activeStepsCount = obj.data[0].val;
        	if(activeStepsCount > 0){
        		openConfirmDialog({
			        onConfirm: function(){
			        	if(protocol=='Organic'){
			        		onSaveExperimentCheckForParameters(savedStatus,afterSave,saveAction);
			        	} else {//formulation
			        		onSaveExperimentFrCheckSelfTestPerformed(savedStatus,afterSave,saveAction)
			        	}
			        },
			        title: 'Warning',
			        message: getSpringMessage('changeExperimentToCompleted'),
			        onCancel: function(){
			        	$('#STATUS_ID').val($("#STATUS_ID option:contains('"+savedStatus+"')").val());
			    		$("#STATUS_ID").trigger('chosen:updated');
			    		onChangeAjax('planned_actual');
			    		hideWaitMessage();
			    		doChangeStatus = false;
			        }
			    });
        	} else{
        		if(protocol=='Organic'){
	        		onSaveExperimentCheckForParameters(savedStatus,afterSave,saveAction);
	        	} else {//formulation - if the experiment has no active steps
	        		//then don't display a warning messge of steps that are finished
	        		//and don't check the selftests(that are probably not exist)
	        		doSave(afterSave,saveAction);
	        	}
        	}
        },
        error: handleAjaxError
    });
	
	hideWaitMessage();
}

function onSaveExperimentFrCheckSelfTestPerformed(savedStatus,afterSave,saveAction){
	showWaitMessage("Please wait...");
    var allData = getformDataNoCallBack(1);

    var urlParam =
        "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&eventAction=checkIfExperimentHasAllSelfTests" + "&isNew=" + $('#isNew').val();
    var data_ = JSON.stringify({
        action: "doSave",
        data: allData,
        errorMsg: ""
    });
    $.ajax({
        type: 'POST',
        data:data_,
        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) 
        {
        	hideWaitMessage();
        	var msg = obj.data[0].val;
        	console.log(msg);
        	if (msg != "")
        	{     
        		openConfirmDialog({
						onConfirm:function(){	
							changeStepStatusToFinish(afterSave,saveAction);
    					},
    					title: 'Warning',
    					message: msg ,
    					onCancel: function(){
    						$('#STATUS_ID').val($("#STATUS_ID option:contains('"+savedStatus+"')").val());
			        		$("#STATUS_ID").trigger('chosen:updated');
			        		onChangeAjax('planned_actual');
			        		hideWaitMessage();
    					}
    				});	        		
        	}
        	else
        	{
        		changeStepStatusToFinish(afterSave,saveAction);
        	}
        },
        error: handleAjaxError
    });
}

function onSaveExperimentCheckForCharacterMassBalance(afterSave,saveAction)
{
	var parentId = "", formId = "", activeInx = "0", chkFormCode = "";
	var doContinue = false;

	var $checkbox = $('.experimentDefaultMassBalanceClass:checked');
	if($checkbox.length > 0) 
	{
		if($checkbox[0].hasAttribute('chkParentIndex')) // Experiment/ExperimentCP // develop ExperimentCP "Continuous Process"
		{
			parentId = $('#formId').val();
			formId = $('#formId').val();          
			activeInx = $checkbox.attr('chkParentIndex');
			//chkFormCode = "Experiment";
		}
		else if($checkbox[0].hasAttribute('chkParentId')) // Step
		{
			parentId = $('#formId').val();
			formId = $checkbox.attr('chkParentId');
			//chkFormCode = "Step";
		}
	
		var urlParam = "?formId=" + formId
						+ "&formCode=" + $('#formCode').val()
						+ "&userId=" + $('#userId').val()
						+ "&eventAction=checkForCharacterMassBalance";
		var data_ = JSON.stringify({
	        action: "checkForCharacterMassBalance",
	        data : [{
				code : "parentID",
				val : parentId,
				type : "AJAX_BEAN",
				info : 'na'
					},
					{
						code : "massBalanceActiveInx",
						val : activeInx,
						type : "AJAX_BEAN",
						info : 'na'
			}],
	        errorMsg: ""
	    });
	    $.ajax({
	        type: 'POST',
	        data:data_,
	        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
	        contentType: 'application/json',
	        dataType: 'json',
	        success: function (obj) 
	        {
	        	var showMsg = false;
	        	var msg = "", msgPart = "";
	        	var dbFC = "", dbMBId = "";
	        	var retval = obj.data[0].val;
	        	console.log(retval);
	        	if (retval != "" && retval.indexOf('@,@') > 0)
	        	{     	        		
	        		
		        		// get values from DB
	        			var arr = retval.split('@,@');
	        			msgPart = arr[0];
	        			dbFC = arr[1];
	        			dbMBId = arr[2];
	        			var dbvalue = dbFC+""+dbMBId;
	        			
	        			// get values the screen was loaded with
	        			var onLoadMBData = $('#hdnOldDefaultMassBalance').val();
	        			var delim = onLoadMBData.indexOf(',');
	        			var loadedvalue = "";
	        			if(delim > 0)
	        			{
		        			var onLoadFC = onLoadMBData.substr(0,delim);
		        			var onLoadMB = onLoadMBData.substr(delim+1,onLoadMBData.length);
		        			loadedvalue = onLoadFC+""+onLoadMB;   
	        			}
	        			if(dbvalue != loadedvalue)
	        			{
	        				showMsg = true;
	        			}	
        			
		        		if(showMsg) 
		        		{	        			
		        			msg = msgPart + " is already selected as \"Characterization Mass Balance\". "
		        					+" <div style=\"margin-top: 10px;\"> Are you sure you want to continue with the new selection of 'Characterization Mass Balance'? </div>";
			        		openConfirmDialog({onCancel:function(){	
			        								//$('.experimentDefaultMassBalanceClass:checked').prop('checked',false).prop('disabled',false);
			        								$('.experimentDefaultMassBalanceClass').prop('checked',false).prop('disabled',false);
			        								if(dbFC == "Step")
			        								{
			        									$('.experimentDefaultMassBalanceClass[name="chkStepCharacterMassBalance_'+dbMBId+'"]').prop('checked',true);
			        									
			        								}
			        								else
			        								{
			        									$('.experimentDefaultMassBalanceClass[id="chkCharacterMassBalance'+dbMBId+'"]').prop('checked',true);
			        								}
			        								$('.experimentDefaultMassBalanceClass:not(:checked)').prop('disabled',true);
			        								onSaveExpCheckForCharacterMassBalanceContinue(afterSave,saveAction);
			                					},
			                					onConfirm:function() {
			                						onSaveExpCheckForCharacterMassBalanceContinue(afterSave,saveAction);
			                					},
			                					title: 'Warning',
			                					message: msg });	
		        		}
		        		else
		        			doContinue = true;
	        	}
	        	else
	        		doContinue = true;
	        	
	        	if(doContinue)
	        	{
	        		onSaveExpCheckForCharacterMassBalanceContinue(afterSave,saveAction);
	        	}
	        },
	        error: handleAjaxError
	    });
	}
	else
	{
		onSaveExpCheckForCharacterMassBalanceContinue(afterSave,saveAction);
	}
}

function onSaveExpCheckForCharacterMassBalanceContinue(afterSave,saveAction)
{
	var newval = "";
	var $checkbox = $('.experimentDefaultMassBalanceClass:checked');
	if($checkbox.length > 0) 
	{
		if($checkbox[0].hasAttribute('chkParentIndex')) // Experiment/ExperimentCP   //develop ExperimentCP "Continuous Process"
		{
			newval = $('#formCode').val()+","+$checkbox.attr('chkParentIndex')+"";
		}
		else if($checkbox[0].hasAttribute('chkParentId')) // Step
		{
			newval = "Step,"+$checkbox.attr('chkParentId')+"";
		}
	}
	$('#hdnExperimDefaultMassBalance').val(newval);

	var obj = $('#STATUS_ID option:selected');
	var savedStatus = $('#STATUS_ID').attr("lastselectedname");
	if($('#formCode').val()=='Experiment'&& obj.text() == 'Completed' && savedStatus=='Active'){
		doChangeStatusToCompleted(savedStatus,afterSave,saveAction,'Organic');
	} else {
		// go save now
		doSave(afterSave,saveAction);
	}
	
}


function doSaveWithConfirm(afterSave,saveAction){
	
	var obj = $('#STATUS_ID option:selected');
	var savedStatus = $('#STATUS_ID').attr("lastselectedname");
	var version = $('#experimentVersion').val();
	if(($('#formCode').val()=="Step" && version !=1) || (($('#formCode').val()=="Experiment" || $('#formCode').val()=="ExperimentCP") && version !='01' )){ //add ExperimentCP for "Continuous Process"
		if($('#formCode').val()=="Step"){
			saveStepWithReactionUpdate(afterSave,saveAction);
		} 
		else if($('#formCode').val()=="Experiment" || $('#formCode').val()=="ExperimentCP")  //develop ExperimentCP "Continuous Process"
		{
			onSaveExperimentCheckForCharacterMassBalance(afterSave,saveAction);
		}
		else{
			doSave(afterSave, saveAction);
		}
	}
	else if((obj.text() == "Finished" && savedStatus != "Finished")||//the step changed to Finished
			(obj.text() == "Completed" && savedStatus != "Completed"))//the experiment changed to Completed
	{
		var urlParam = "?formId="+ $('#formId').val()
					+ "&formCode="+ $('#formCode').val()
					+ "&userId="+ $('#userId').val()
					+ "&eventAction=checkStepPlannedActualValues"
					+ "&isNew=" + $('#isNew').val();
		
		var data_ = JSON.stringify({
			action : "checkStepPlannedActualValues",
			data : [],
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
				else if(obj.data[0].val!='1')//There is a confirmation message
				{
					openConfirmDialog({
				        onConfirm: function(){
				        	hideWaitMessage();
				        	if($('#formCode').val()=="Step"){
				        		onSaveStepCheckForParameters(savedStatus, afterSave,saveAction);
				        	} 
				        	else if($('#formCode').val()=="Experiment" || $('#formCode').val()=="ExperimentCP") //add ExperimentCP for "Continuous Process"
				    		{
				    			onSaveExperimentCheckForCharacterMassBalance(afterSave,saveAction);
				    		}
				        	else {
				        		doSave(afterSave, saveAction);
				        	}
				        },
				        title: 'Warning',
				        message: getSpringMessage(obj.data[0].val),
				        onCancel: function(){
				        	$('#STATUS_ID').val($("#STATUS_ID option:contains('"+savedStatus+"')").val());
			        		$("#STATUS_ID").trigger('chosen:updated');
			        		if($('#formCode').val()=="Experiment" || $('#formCode').val()=="ExperimentCP"){ //add ExperimentCP for "Continuous Process"
			        			onChangeAjax('planned_actual');
			        		}
			        		hideWaitMessage();
				        }
				    });
					hideWaitMessage();
				} else {
					hideWaitMessage();
					if($('#formCode').val()=="Step"){
						onSaveStepCheckForParameters(savedStatus, afterSave,saveAction);
					} 
					else if($('#formCode').val()=="Experiment" || $('#formCode').val()=="ExperimentCP") //add ExperimentCP for "Continuous Process"
					{
						onSaveExperimentCheckForCharacterMassBalance(afterSave,saveAction);
					}
					else{
						doSave(afterSave, saveAction);
					}
				}
				
			},
			error : handleAjaxError
		});
	} 
	else 
	{
		if($('#formCode').val()=="Step")
		{
			saveStepWithReactionUpdate(afterSave,saveAction);
		} 
		else if($('#formCode').val()=="Experiment" || $('#formCode').val()=="ExperimentCP") //add ExperimentCP for "Continuous Process"
		{
			onSaveExperimentCheckForCharacterMassBalance(afterSave,saveAction);
		}
		else
		{
			doSave(afterSave, saveAction);
		}
	}
		
}

function onSaveExperimentCheckForParameters(savedStatus,afterSave,saveAction){
	var urlParam = "?formId="+ $('#formId').val()
	+ "&formCode="+ $('#formCode').val()
	+ "&userId="+ $('#userId').val()
	+ "&eventAction=checkParametersStepFinished"
	+ "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "checkParametersStepFinished",
		data : [],
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
			} else if(obj.data[0].val!=""){
				openConfirmDialog({
			        onConfirm: function(){
			        	hideWaitMessage();
			        	changeStepStatusToFinish(afterSave,saveAction);
			        },
			        title: 'Warning',
			        message: getSpringMessage(obj.data[0].val),
			        onCancel: function(){
			        	$('#STATUS_ID').val($("#STATUS_ID option:contains('"+savedStatus+"')").val());
			    		$("#STATUS_ID").trigger('chosen:updated');
			    		onChangeAjax('planned_actual');
		        		hideWaitMessage();
		        		return;
			        }
				});
			} else{
				//changeStepStatusToFinish occured in the server
				doSave(afterSave,saveAction);
			}
		},
		error : handleAjaxError
	});     
	hideWaitMessage(); 
}

function changeStepStatusToFinish(afterSave,saveAction){
	var urlParam = "?formId="+ $('#formId').val()
	+ "&formCode="+ $('#formCode').val()
	+ "&userId="+ $('#userId').val()
	+ "&eventAction=changeStepStatusToFinish"
	+ "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "changeStepStatusToFinish",
		data : [],
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
			} else {
				doSave(afterSave,saveAction);
			}
		},
		error : handleAjaxError
	});     
	hideWaitMessage(); 
}

function onSaveStepCheckForParameters(savedStatus, afterSave,saveAction)
{
	var _objParameters = getDataFromEditableTable(["Parameters"], -1);
//	console.log(_objParameters);
	var isMinParamsDefined = false;
	
	if((_objParameters instanceof Object) && !$.isEmptyObject(_objParameters)) // if is object and not empty
	{
		for(key in _objParameters)
		{
			var rowObj = _objParameters[key];
			if(rowObj.PARAMETER_ID != "")
			{
				isMinParamsDefined = true;
				break;
			}
		}
	}
	
	if(!isMinParamsDefined){
		var msg = getSpringMessage("Please note: no parameters were defined.Do you want to continue?");
		openConfirmDialog({
	        onConfirm: function(){	        	
	        	saveStepWithReactionUpdate(afterSave,saveAction);
	        	},
	        title: 'Warning',
	        message: msg,
	        onCancel: function(){
	        	$('#STATUS_ID').val($("#STATUS_ID option:contains('"+savedStatus+"')").val());
        		$("#STATUS_ID").trigger('chosen:updated');		        		
        		hideWaitMessage();
        		
	        }
	    });
		
	}
	else
	{
		saveStepWithReactionUpdate(afterSave,saveAction);
	}
}

function saveStepWithReactionUpdate(afterSave,saveAction){
	var _obj = getDataFromEditableTable(["reactants","solvents","products"], -1);
	if(!(_obj instanceof Object))
	{
		hideWaitMessage();
		displayAlertDialog(_obj + "\n Save was not performed.");
		return;
	}
	var urlParam = "?formId="+ $('#formId').val()
					+ "&formCode="+ $('#formCode').val()
					+ "&userId="+ $('#userId').val()
					+ "&eventAction=insertReactionTableDataToClob"
					+ "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
					action : "insertReactionTableDataToClob",
					data : [{
							code : "reactionTableData",
							val : JSON.stringify(_obj),
							type : "AJAX_BEAN",
							info : 'na'
						}],
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
				hideWaitMessage();
			} 
			else if(obj.data[0].val!='')//There is a confirmation message
			{
				$('#reactionTableData').val(obj.data[0].val);
				hideWaitMessage();
				onSaveStepCheckForCharacterMassBalance(afterSave,saveAction);				
			}
		},
		error : handleAjaxError
	});
	
}

function onSaveStepCheckForCharacterMassBalance(afterSave,saveAction)
{
	var $checkbox = $('#chkCharacterMassBalance');
	var isChecked = $checkbox.is(':checked');
	if(isChecked) 
	{
		var urlParam = "?formId=" + $('#formId').val()
						+ "&formCode=" + $('#formCode').val()
						+ "&userId=" + $('#userId').val()
						+ "&eventAction=checkForCharacterMassBalance";
		var data_ = JSON.stringify({
	        action: "checkForCharacterMassBalance",
	        data : [{
						code : "parentID",
						val : $('#EXPERIMENT_ID').val(),
						type : "AJAX_BEAN",
						info : 'na'
					},
					{
						code : "massBalanceActiveInx",
						val : "0",
						type : "AJAX_BEAN",
						info : 'na'
			}],
	        errorMsg: ""
	    });
	    $.ajax({
	        type: 'POST',
	        data:data_,
	        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
	        contentType: 'application/json',
	        dataType: 'json',
	        success: function (obj) 
	        {
	        	var msg = "";
	        	var msgPart = "";
	        	var retval = obj.data[0].val;
	        	console.log(retval);
	        	if (retval != "" && retval.indexOf('@,@') > 0)
	        	{     	        	
	        		var arr = retval.split('@,@');
        			msgPart = arr[0];
        			
        			msg = msgPart + " is already selected as \"Characterization Mass Balance\". "
									+" <div style=\"margin-top: 10px;\"> Are you sure you want to continue with the new selection of 'Characterization Mass Balance'? </div>";
					openConfirmDialog({onCancel:function(){	
											$checkbox.prop('checked',false);
											doSave(afterSave,saveAction);
				    					},
				    					onConfirm:function() {
				    						doSave(afterSave,saveAction);
				    					},
				    					title: 'Warning',
				    					message: msg });	
	        		
	        	}
	        	else
	        	{
	        		doSave(afterSave,saveAction);
	        	}
	        },
	        error: handleAjaxError
	    });
	}
	else
	{
		doSave(afterSave,saveAction);
	}
}


function doSaveWithConfirmManualResults(afterSave,saveAction){
	var experimentType = $('#EXPERIMENTTYPE_ID option:selected').text();
	if(experimentType == 'General'){
		CheckSampleResult(afterSave,saveAction);
	} else {
		var urlParam = "?formId="+ $('#formId').val()
						+ "&formCode="+ $('#formCode').val()
						+ "&userId="+ $('#userId').val()
						+ "&eventAction=checkManualResultsToDelete"
						+ "&isNew=" + $('#isNew').val();
			
		var data_ = JSON.stringify({
			action : "checkManualResultsToDelete",
			data : [],
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
				else if(obj.data[0].val!='')//There is a confirmation message
				{
					openConfirmDialog({
				        onConfirm: function(){
				        	hideWaitMessage();
				        	CheckSampleResult(afterSave,saveAction);
				        	//doSave(afterSave, saveAction);
				        },
				        title: 'Warning',
				        message: getSpringMessage("The following manual results are about to delete:</br> "+obj.data[0].val+"</br>Are you sure?"),
				        onCancel: function(){
			        		hideWaitMessage();
				        }
				    });
					hideWaitMessage();
				} else {
					hideWaitMessage();
					CheckSampleResult(afterSave,saveAction);
		        	//doSave(afterSave, saveAction);
				}
				
			},
			error : handleAjaxError
		});
	}
}

function CheckSampleResult(afterSave,saveAction){
	var urlParam = "?formId="+ $('#formId').val()
					+ "&formCode="+ $('#formCode').val()
					+ "&userId="+ $('#userId').val()
					+ "&eventAction=checkSampleResult"
					+ "&isNew=" + $('#isNew').val();
	var allData = getformDataNoCallBack(1);
	var data_ = JSON.stringify({
		action : "checkSampleResult",
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
			}else if (obj.data[0].val.toString() != null && obj.data[0].val.toString()!=""){//There is a confirmation message
				if(obj.data[0].val.startsWith("-1")){//2 results or more with the same sample, material and result type
					obj.data[0].val = obj.data[0].val.substr(2);
					 openConfirmDialog({
				            onConfirm: function(){
				            	if(obj.data[0].val.toString() != null && obj.data[0].val.toString()!=""){
				            		openConfirmDialog({
				    			        onConfirm: function(){
				    			        	hideWaitMessage();
				    			        	var formId = $('#formId').val();
				    			        	checkMainResults(formId,afterSave,saveAction);
				    			        },
				    			        title: 'Warning',
				    			        //message: getSpringMessage("The following samples:</br>"+obj.data[0].val+"</br>have more than one result from the same type.</br><b>Please notice that there is no main result for the sample.</b>"),
				    			        message: getSpringMessage("You are about to save an additional result set for sample</br>"+obj.data[0].val+"</br>Would you like to select these results as the main results set of the sample?"),
				    			        onCancel: function(){
				    		        		hideWaitMessage();
				    		        		var formId = $('#formId').val();
				    		        		sendNotifExpOwner(formId,afterSave,saveAction);
				    			        }
				    			    });
				            	}else{
				            		hideWaitMessage();
				    				doSave(afterSave, saveAction);
				            	}
				            },
				            title: 'Warning',
				            message: getSpringMessage('EXPAN_MULTIPLE_RESULT')
				        });
				}else{
				openConfirmDialog({
			        onConfirm: function(){
			        	hideWaitMessage();
			        	var formId = $('#formId').val();
			        	checkMainResults(formId,afterSave,saveAction);
			        },
			        title: 'Warning',
			        //message: getSpringMessage("The following samples:</br>"+obj.data[0].val+"</br>have more than one result from the same type.</br><b>Please notice that there is no main result for the sample.</b>"),
			        message: getSpringMessage("You are about to save an additional result set for sample</br>"+obj.data[0].val+"</br>Would you like to select these results as the main results set of the sample?"),
			        onCancel: function(){
		        		hideWaitMessage();
		        		var formId = $('#formId').val();
		        		sendNotifExpOwner(formId,afterSave,saveAction);
			        }
			    });
				}
			} else {
				hideWaitMessage();
				doSave(afterSave, saveAction);
			}
			
		},
		error : handleAjaxError
	});
}

//function doSaveExperimentStb (afterSave, saveAction) // taro develop not in use
//{
//	var _obj = getDataFromEditableTable("experimentResults", -1);
//	if(!(_obj instanceof Object))
//	{
//		displayAlertDialog(_obj + "\n Save was not performed.");
//		return;
//	}
//	if (Object.keys(_obj).length == 0)return;
//	
//	var urlParam = "?formId="+ $('#formId').val()
//					+ "&formCode="+ $('#formCode').val()
//					+ "&userId="+ $('#userId').val()
//					+ "&eventAction=insertExperimentResultsTableDataToClob"
//					+ "&isNew=" + $('#isNew').val();
//
//	var data_ = JSON.stringify({
//					action : "insertExperimentResultsTableDataToClob",
//					data : [{
//							code : "experimentResultsTableData",
//							val : JSON.stringify(_obj),
//							type : "AJAX_BEAN",
//							info : 'na'
//						}],
//					errorMsg : ""
//					});
//	showWaitMessage(getSpringMessage('pleaseWait'));
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
//				hideWaitMessage();
//			} 
//			else if(obj.data[0].val!='')//There is a confirmation message
//			{
//				$('#experimentResultsTableData').val(obj.data[0].val);
//				hideWaitMessage();
//				doSave(afterSave,saveAction);
//			}
//		},
//		error : handleAjaxError
//	});
//	
//}

function doChosenChange(){
	$("#reportName").val($("#reportNameList").val());
}

function doSpreadsheetTemplChosenChange(){
	$("#spreadsheetName").val($("#spreadsheetNameList").val());
}


function doNavigateNext(){
	showWaitMessage("Please wait...");
	var nextPageUrl =  "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=" + $('#formCode').val()
					    + "&formId=" + $('#nextEntityFormId').val()
					    + "&userId=" + $('#userId').val()
					    + "&PARENT_ID=" +$('#parentId').val();
	window.location = nextPageUrl
}

function focusOnThis(ths,param){ //kd 12082018
	if (ths.value == 'By Text / Scan code') {
		$('#'+param).focus();
	}
}


function doNavigatePrevious(){
	showWaitMessage("Please wait...");
	var previousPageUrl = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=" + $('#formCode').val()
						    + "&formId=" + $('#previousEntityFormId').val()
						    + "&userId=" + $('#userId').val()
						    + "&PARENT_ID=" +$('#parentId').val();
	window.location = previousPageUrl
}


function confirmNextStage(functionName, afterSave, saveAction) {
        openConfirmDialog({
            onConfirm: function(){
            	functionName(afterSave,saveAction);
            },
            title: 'Warning',
            message: getSpringMessage('confirmNextStage')
        });
}

function confirmPrevStage(functionName, afterSave, saveAction) {
    openConfirmDialog({
        onConfirm: function(){
        	functionName(afterSave,saveAction);
        },
        title: 'Warning',
        message: getSpringMessage('confirmPrevStage')
    });
}

function doSaveWorkupnoMandatoryValidation(afterSave,saveAction){
	var boolAttachment = 0;
	if ($('[name="uploadFile"]').length > 0) {
	        if ($('[name="uploadFile"]').val() != "") {
	            boolAttachment = 1;
	        }
	}
	 if (!checkDateMinMaxValidity() || !checkNumberMinMaxValidity() || !checkTimeValidity() || !checkEmailValidity() 
     		|| !elementDynamicParamsImpValidation() || !elementRichTextEditorValidation() || !elementWebixValidation()) {
     		prop.onChangeAjaxFlag = false;
     		return;
     } 
     
     doSaveAfterValidation(afterSave, saveAction, boolAttachment);
}

function openReport(domId, paramsArr)
{
	var formId = paramsArr[0];
	var formCode = paramsArr[1];
	if (formCode.indexOf("Step")>-1)
	{
		//alert("paramArr= "+ paramsArr + "savedStatus= " + savedStatus);
	
//		if((obj.text() == "Finished" && savedStatus != "Finished")
//			|| (obj.text() == "Cancelled" && savedStatus != "Cancelled"))
//		{
			showWaitMessage("Please wait...");
//		    var allData = getformDataNoCallBack(1);
			var allData = getformDataNoCallBack(1);
			var newurl = url();
			newurl = newurl.substr(0,newurl.indexOf('/skylineForm'));
			// url call
			var urlParam = "?formId=" + formId + "&formCode="
					+ formCode + '&userId=' + $('#userId').val()
					+ "&eventAction=StepSummaryReport&isNew=" + $('#isNew').val();
			var stringifyToPush = {
								code : 'localHost',
								val : newurl,
								type : "AJAX_BEAN",
								info : 'na'
							};
			var allData = allData.concat(stringifyToPush);

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
		        	var fileId = obj.data[0].val;
		        	if(fileId == "-1") {
		        		displayAlertDialog("Error in creating the report");
		        	} else {
		        		smartFile(domId, fileId);
		        	}
		        },
		        error: handleAjaxError
		    });
	} else if (formCode.indexOf("Experiment")>-1)
	{
		//alert("paramArr= "+ paramsArr + "savedStatus= " + savedStatus);
		
//		if((obj.text() == "Finished" && savedStatus != "Finished")
//			|| (obj.text() == "Cancelled" && savedStatus != "Cancelled"))
//		{
		showWaitMessage("Please wait...");
//		    var allData = getformDataNoCallBack(1);
		var allData = getformDataNoCallBack(1);
		var newurl = url();
		newurl = newurl.substr(0,newurl.indexOf('/skylineForm'));
		// url call
		var urlParam = "?formId=" + formId + "&formCode="
		+ formCode + '&userId=' + $('#userId').val()
		+ "&eventAction=ExperimentSummaryReport&isNew=" + $('#isNew').val();
		var stringifyToPush = {
				code : 'localHost',
				val : newurl,
				type : "AJAX_BEAN",
				info : 'na'
		};
		var allData = allData.concat(stringifyToPush);
		
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
				var fileId = obj.data[0].val;
				if(fileId == "-1") {
					displayAlertDialog("Error in creating the report");
				} else {
					smartFile(domId, fileId);
				}
			},
			error: handleAjaxError
		});
	}
//TODO: call server side to open report	(paramsArr contain formId=stepId formCode=step/stepfr)
}

function openSearchLabelDialog(clickedObj)
{
	if($('#searchDialog iframe').length !=0)//whether the searchDialog is already opened
		return;
	//console.log("clickedObj",clickedObj);
	var $this = $(clickedObj);	
	var left, top;
	console.log("$this",$this);
	dialogHeight = 220;
    dialogWidth = 450;   
    if($this.length > 0)
    {
    	left = $this.offset().left - dialogWidth;
        top = $this.offset().top + $this.height() + 10;
        console.log("left",left);
        console.log("top",top);
    }
    else
    {
    	left = $(document).width() - dialogWidth - 100;
    	top = $(document).height()/2 - dialogHeight - 200;
    	console.log("left",left);
        console.log("top",top);
    }    
    
	var parentId = $('#formId').val();
	var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=SearchLabel&formId=-1&userId="
			+ $('#userId').val()
			+ '&tableType=&PARENT_ID=';

	// open iframe inside dialog
	var $dialog = $(
			'<div id="searchDialog" style="overflow-y: hidden;""></div>')//prevDialog
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
							$('#searchDialog iframe').attr('src', 'about:blank');
							$('#searchDialog').remove();
						},
						open: function(event, ui) 
						{
			                $(this).parent().css({'top': top,'left':left});
			            }
					});

	$dialog.dialog('option', 'dialogClass', 'noTitleStuff')
			.dialog('open');
}

function openLinkSampleDialog(clickedObj, formId) //"Taro develop"
{
	if($('#prevDialog iframe').length !=0)//whether the searchDialog is already opened
		return;
 
	var $this = $(clickedObj);	
	var left, top;
	console.log("$this",$this);
	dialogHeight = 450;
    dialogWidth = 500;   
    if($this.length > 0)
    {
    	left = $this.offset().left + 1;
        top = $this.offset().top + $this.height() + 19;
        console.log("left",left);
        console.log("top",top);
    }
    else
    {
    	left = $(document).width() - dialogWidth - 100;
    	top = $(document).height()/2 - dialogHeight - 200;
    	console.log("left",left);
        console.log("top",top);
    }    
    
    //var sampleFormId = $('#LinkSample').val(); 
    var sampleFormId = formId;
    
    var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=SampleLink"
    + "&formId=" + sampleFormId
    + "&userId=" + $('#userId').val();
    
	// open iframe inside dialog
	var $dialog = $(
			'<div id="prevDialog" style="overflow-y: hidden;""></div>')//prevDialog
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
							$('#prevDialog iframe').attr('src', 'about:blank');
							$('#prevDialog').remove();
							//refresh the storage
							onChangeAjax('storageLastSelection');
							
						},
						open: function(event, ui) 
						{
			                $(this).parent().css({'top': top,'left':left});
			            }
					});

	$dialog.dialog('option', 'dialogClass', 'noTitleStuff')
			.dialog('open');
}

function saveParentReactant(reactantId,formCode,formId,stepId,parentId){
	var allData = getformDataNoCallBack(2,parent.$('*')); 
	var urlParam =
         "?formId=" + reactantId + "&formCode=MaterialRef&userId=" + $('#userId').val() + "&saveAction=&isNew=" + parent.$('#isNew').val() + "&stateKey=" + $('#stateKey').val()
         +"&saveName=&useLoginsessionidScopeFlag=" + parent.$('#useLoginsessionidScopeFlag').val()  + "&description=&formPathInfo=" + encodeURIComponent(parent.$('#formPathInfo').val());

     var data_ = JSON.stringify({
         action: "doSave",
         data: allData,
         errorMsg: ""
     });
     showWaitMessage();
     $.ajax({
         type: 'POST',
         data: data_,
         url: "doSave.request" + urlParam,
         contentType: 'application/json',
         dataType: 'json',
         success: function (obj) {
             if (obj.errorMsg != null && obj.errorMsg != '') {
                 displayAlertDialog(obj.errorMsg);
                 hideWaitMessage();
             } else if (obj.data[0].val == "-1") {
                 displayAlertDialog(getSpringMessage('updateFailed'));
                 hideWaitMessage();
             } else if (obj.data[0].val.toString().indexOf(',') != '-1' && obj.data[0].val.toString().substring(0, 2) == '-2') {
                 doSaveMessage = getSpringMessage(obj.data[0].val.split(',')[1]);/*.split("_").join(" ").toLowerCase();*/
                 displayAlertDialog(doSaveMessage/*.charAt(0).toUpperCase() + doSaveMessage.slice(1)*/ + " " + getSpringMessage('alreadyExistsInSystem'));//ab 22/03/18 fixed bug 4161
                 hideWaitMessage();
             } else if (obj.data[0].val.toString().indexOf(',') != '-1' && obj.data[0].val.toString().substring(0, 2) == '-3') {
                 doSaveMessage = obj.data[0].val.split(',')[1].split("_").join(" ").toLowerCase();
                 hideWaitMessage();
                 displayAlertDialog(doSaveMessage.charAt(0).toUpperCase() + doSaveMessage.slice(1) + " " + getSpringMessage('invalidInSystem'));  
             } else {    
            	 if (obj.data[0].info.toString() !='') {//there's is a message to display
            		 //displayAlertDialog(getSpringMessage(obj.data[0].info.toString()));
            		 hideWaitMessage();
            		 openConfirmDialog({
         		        onConfirm: function(){
         		        	showWaitMessage();
         		        	saveStepBeforeNavigation(reactantId,formCode,formId,stepId,parentId);
         		        },
         		        title: 'Alert',
         		        message: getSpringMessage(getSpringMessage(obj.data[0].info.toString())),
         		        onCancel: function(){
         		        	showWaitMessage();
         		        	saveStepBeforeNavigation(reactantId,formCode,formId,stepId,parentId);
     		        	}
         		    },true);
            	 } else {
            		 saveStepBeforeNavigation(reactantId,formCode,formId,stepId,parentId);
            	 }
             }
         },
         error: handleAjaxError
     });
}

function restoreUserBreadcrumbsFromLastSession()
{
	window.location = "./restore.request?stateKey=" + $('#stateKey').val() + "&userId=" + $('#userId').val();
}

function searchScan()
{
	$("#textBox").attr("placeholder", "Insert Batch / Sample / Scan Barcode").focus();
	$("#searchType").val($("#searchType option:contains('Inventory')").val());
    $("#searchType").trigger('chosen:updated');
    setTimeout(function () {
    	$("#textBox").focus();
    	$("#textBox").keyup(function(e) {
    		if(e.which == 13){ // qrcode scan will include enter as last char
    			var formId =$("#textBox").val().trim();
    			var allData = getformDataNoCallBack(1);
    				var urlParam = '?formId=' + formId + '&formCode=' +
    				$('#formCode').val() + '&userId=' + $('#userId').val()
    				+ "&eventAction=getformNumberID&isNew=" + $('#isNew').val();
    				 var data_ = JSON.stringify({
    			         action: "doSave",
    			         data: allData,
    			         errorMsg: ""
    			     });
    			     showWaitMessage();
    			     
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
    						if(obj.data[0].val !="-1" && obj.data[0].val != ""){
    							var formNumberId = obj.data[0].val;
    						    $("#textBox").val(formNumberId);
    						}
    						searchAll('generate');
    					},
    					error: handleAjaxError
    				});
    				
    		}
    	});
	},100);
}


function showMaterialValidationErrorMsg(oMsg)
{
	console.log("InvItemMaterial validation error: " + oMsg);
	 var jObj = funcParseJSONData(oMsg);	                 
	 var $dialog = $('<div id="validationConfirmDialog" style="overflow-y: auto;""></div>')
    			     .html('<div style="padding: 7px;">'+jObj.eMsg+'</div>')
                    .dialog({
                        autoOpen: false,
                        modal: true,
                        width: 500,
                        title: 'Validation error',
                        buttons: {
                            View: function () {
                           	 $(this).dialog("close");
                           	 generalBL_generalClickEvent("openMaterialDuplicatesForm", jObj.itemsId);				                                 
                            },
                            OK: function () {                                 
                                $(this).dialog("close");
                            }
                        }
                    });
	 $dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
}

function addNewDocument(elementID,rowId,fileName,tableName,docTableType){
	var parentId = rowId;
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
					+ $('#formCode').val() + "&userId=" + $('#userId').val()
					+ "&eventAction=addNewDocument&isNew=" + $('#isNew').val();

	var allData = [{
					code : 'elementId',
					val : elementID,
					type : "AJAX_BEAN",
					info : 'na'
					},
					{
					code : 'parentId',
					val : parentId,
					type : "AJAX_BEAN",
					info : 'na'
					},
					{
					code : 'documentName',
					val : fileName,
					type : "AJAX_BEAN",
					info : 'na'
					},
					{code : 'docTableType',
					val : docTableType,
					type : "AJAX_BEAN",
					info : 'na'
					}
				];
	
	var data_ = JSON.stringify({
		action : "doSave",
		data : allData,
		errorMsg : ""
	});
	
	showWaitMessage();
	
	$.ajax({
	type : 'POST',
	data : data_,
	url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
	contentType : 'application/json',
	dataType : 'json',
	success : function(obj) {
		onElementDataTableApiChange(tableName);
		hideWaitMessage();		
	},
	error : handleAjaxError
});
}

function searchScanExpStb(clickedObj)
{
	var formId =$("#LinkSample").val().trim();
	formCode = getFormCodeBySeqId(formId);
	if((formCode == null || formCode == "") || (formId == null || formId == ""))
	{
		displayAlertDialog("No data found");
	}
	else if(formCode != 'Sample')
	{
		displayAlertDialog("This code is not represent a Sample!");
	}
	else{
		
		//search([formId ,formCode,'', true]);
		var allData = getformDataNoCallBack(1);
		var urlParam = "?formId=" + formId + "&formCode="
		+ formCode + '&userId=' + $('#userId').val()
		+ "&eventAction=getSampleLinkState&isNew=" + $('#isNew').val();
		 var data_ = JSON.stringify({
	         action: "doSave",
	         data: allData,
	         errorMsg: ""
	     });
	     showWaitMessage();
	     
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
				var objectReturn = JSON.parse(obj.data[0].val);
				if(objectReturn.stateCode == '2') {
					$("#LinkSample").val('');
					displayAlertDialog(objectReturn.message);
				} else if(objectReturn.stateCode == '1') {
					$("#LinkSample").val('');
					$("#lLinkSample").text(objectReturn.sampleName);
					openConfirmDialog({
						onConfirm : function (){openLinkSampleDialog(clickedObj, formId);},
						title : 'Warning',
						message : objectReturn.message
					});
				} else {
					$("#LinkSample").val('');
					$("#lLinkSample").text(objectReturn.sampleName);
					openLinkSampleDialog(clickedObj, formId);
				}
				
				//searchAll();
				
			},
			error: handleAjaxError
		});
		//searchAll();
	}
}

//TODO: write code in isDataInWebixAnalyticalSelfTest, in IntegrationSaveFormAdamaImp in preFormSaveEvent add code for delete data in webix
function confirmChangeSelfTestType(obj) {
	var confirmMessage = "";
	
	if ($('#TYPE_ID option:selected').text() == "Non-Numeric" && ($('#AnalytMethods').DataTable().data().count() > 0 || $('#columns').DataTable().data().count() > 0 || isDataInWebixAnalyticalSelfTest())) {
		confirmMessage = getSpringMessage('confirmSelfTestTypeToNonNumeric');
	} else if ($('#instrumentExt').val().trim() != '') {
		confirmMessage = getSpringMessage('confirmSelfTestTypeToInternalAnalytical');
	}
	if (confirmMessage != ""){
		openConfirmDialog({
	        onConfirm: function(){
	        	$('#cleanDataByNewTypeFlag').val(1);
	        	//make default ddl action event
//	        	removeRequiredMarkInSelfTest();
//	        	doSave('Reload');
//	        	onChangeAjax('TYPE_ID');
	        	updateResultTypeComboList();
	        	onConfirmChangeSelfTestType($('#TYPE_ID option:selected').text());
	        },
	        title: 'Warning',
	        message: confirmMessage,
	        onCancel: function(){
        		//return Self-Test Type to previous value (cancel change it) 
	        	var deselectedID =   $('#TYPE_ID').find('option').not(':selected').map(function(){ 
        			return $(this).prop('value')  
        	       }).get()[1];
        		$("#TYPE_ID").val(deselectedID);
    			$('#TYPE_ID').trigger("chosen:updated");
	        }
	    });
	} else { 
		/*
		 * if TYPE_ID (Self-Test Type) is changed, but no data in the tables (means data we can see on tha screen) 
		 * anyway it's need to delete data from the tables because it possible that there are data in DB and no data 
		 * on screen - it's happenes wheh data has been deleted from the table on the screen but 
		 * these chages NOT saved in database - data from table are still in database)    
		 *
		$('#cleanDataByNewTypeFlag').val(1);
    	//make default ddl action event
    	doSave('Reload');*/
		//make default ddl action event
		updateResultTypeComboList();
    	onChangeAjax('TYPE_ID');
	}
}

function onConfirmChangeSelfTestType(typeName) {
	if(typeName == "Non-Numeric") {
		clearWebixTableData([], 'tableID_webixAnalyticalSelfTest');
		addNewEmptyRowToWebixTable('tableID_webixAnalyticalSelfTest');
		
		var action = 'cleanOnChangeSelfTestTypeToNonNumeric';
		showWaitMessage("Please wait...");
		prop.onChangeAjaxFlag = false; 
		var allData = getformDataNoCallBack(1); 
//		$('#instrumentExt').val('');

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
				onChangeAjax('TYPE_ID');
				hideWaitMessage();
				onElementDataTableApiChange('AnalytMethods');
				onElementDataTableApiChange('columns');
				onChangeAjax('TYPE_ID'); //to have the right data table disabled button by type
			},
			error : function() {
				hideWaitMessage();
				displayAlertDialog(getSpringMessage('Change Type Failure'));
			} 
		});
	} else {
		$('#instrumentExt').val('');
		onChangeAjax('TYPE_ID'); //to have the right data table disabled button by type
	}
}

function updateResultTypeComboList() {

	/*if(typeName == "Non-Numeric") {
		var action = 'updateResultTypeComboListNonNumeric';
	} else if(typeName == "Non-Numeric") {
		
	}*/
	showWaitMessage("Please wait...");
//		prop.onChangeAjaxFlag = false; 
		var allData = getformDataNoCallBack(1); 
//		$('#instrumentExt').val('');

	// url call
//		var urlParam = "?formId=" + $('#formId').val() + "&formCode="
//				+ $('#formCode').val() + '&userId=' + $('#userId').val()
//				+ "&eventAction=" + action + "&isNew=" + $('#isNew').val();
	
	//var urlParam = "&eventAction=" + action;
	
	var data_ = JSON.stringify({
		//action : "doSave",
		data : allData,
		errorMsg : ""
	});

	// call...
	$.ajax({
		type : 'POST',
		// async: false,
		data : data_,
		url : "./updateWebixResultTypeList.request",// + urlParam ,
		contentType : 'application/json',
		dataType : 'json',
		success : function(obj) {
//			onChangeAjax('TYPE_ID');
			hideWaitMessage();
			/*onElementDataTableApiChange('AnalytMethods');
			onElementDataTableApiChange('columns');
			onChangeAjax('TYPE_ID'); */ //to have the right data table disabled button by type
			var dataList = JSON.parse(obj.data[0].val);
			clearComboListSelfTestResultWebixElement('result_type');
			updateWebixElementListByAjaxCall('webixAnalyticalSelfTest', 'result_type', dataList);
			console.log('______-----updateResultTypeComboList-----______ object: ' + dataList);
		},
		error : function() {
			hideWaitMessage();
			displayAlertDialog(getSpringMessage('Change Type Failure'));
		} 
	});
}

function doSaveSelfTest(obj) {
	var doContinue = true;
	
	if(($('#sampleId').val() == null || $('#sampleId').val() == '') && isDataInWebixAnalyticalSelfTest()) {
		displayAlertDialog("Please select a sample before saving results!");
		return;
	}
	if ($('#STATUS_ID option:selected').text() == "Cancelled") {
		openConfirmDialog({
			onConfirm: function(){
				removeRequiredMarkInSelfTest();
				doSave('Reload');
			},
			title: 'Warning',
			message: getSpringMessage('confirmCancel'),
			onCancel: function(){}
		});
	} else { 
		if(isDataInWebixAnalyticalSelfTest()) {
			doContinue = isWebixFieldNotEmpty("tableID_webixAnalyticalSelfTest","result_type");
			if($('#TYPE_ID option:selected').text() == "Internal Analytical" && doContinue) {
				doContinue = isWebixFieldNotEmpty("tableID_webixAnalyticalSelfTest","invitem_material");
				if(doContinue) {
					doContinue = isWebixFieldNotEmpty("tableID_webixAnalyticalSelfTest","normalization");
				}
			} 
		}
		
		
		if (getValueForElementRichText($('#summary')) != "" && doContinue) {
			openConfirmDialog({
				onConfirm: function(){
					$('#copySummaryFieldToObservFlag').val(1);
					doSave('Reload');
				},
				title: 'Warning',
				message: getSpringMessage('confirmCopySummaryToObservation'),
				onCancel: function(){
					doSave('Reload');
				}
			});
		} else if (doContinue){
			doSave('Reload');
		}
	}
}

function isWebixFieldNotEmpty(tableId, columnId) {
	var dtable = $$(tableId);
	dtable.editStop();
	var tableData = dtable.serialize();
	var columnId = columnId;
	var _config = dtable.getColumnConfig(columnId);
	var _isRequired = true; //_config.mandatory;
	var emptyRow = [];
	var result = false;
	try {
		var valFound = false;
		//check empty row
		dtable.eachRow(function (row) { 
			$.each(dtable.Aj, function (key, value) {
				var rowObj = dtable.getItem(row);
				rowIndx = rowObj.index;
				if(!valFound && !checkIsEmpty(rowObj[key])) {
					valFound = true;
				}  
			});
			
			if(!valFound) {
				emptyRow.push(rowIndx);
			}
			
			valFound = false;
			rowIndx = -1;
		});
	} catch(e) {
		emptyRow = [];
    }
	if(_config.editor == "text" && (_config.cellType == "decimal" || _config.cellType == "numeric"))
	{
		//console.log(columnId);
		var _isDecimal = (_config.cellType == "decimal")?true:false;
		for(var j=0;j<tableData.length;j++)
		{
			var _val = tableData[j][columnId];								
			if(!isEmptyRow(emptyRow, j + 1) && !fnValidateNumeric(_val, _config.header[0].text + " column ", _isDecimal, false,_isRequired))
			{
				console.log("row index: " + j + " val: " + _val);
				result = false;
			} else {
				result = true;
			}
		}
	}
	else if((_config.editor == "select" || _config.editor == "combo") && _isRequired)
	{
		dtable.eachRow(function (row)
		{
			var rowObj = dtable.getItem(row);
			var _curCellVal = rowObj[columnId];
			var _optionalCellVal = (_config.mandatoryOptionalField == "")?"":rowObj[_config.mandatoryOptionalField];
			if(!isEmptyRow(emptyRow, rowObj.index) && checkIsEmpty(_curCellVal) && checkIsEmpty(_optionalCellVal))
			{
				displayAlertDialog(_config.header[0].text + " column " + " is required", {title:"Required Data Missing"});
				//doContinue = false;
				result = false;
			} else {
				result = true;
			}
		});
	}
	return result;
}

/*
 * 
 * kd 10122019 added for posibility to save with empty mandatory (required) fields 
 * after save fields will be required again (or disabled if it should be according to prossess)
 * 
 */
function removeRequiredMarkInSelfTest() {
	removeRequiredAttribute();
	setDisableWebixTables('1','');
}

function isDataInWebixAnalyticalSelfTest() {
	if ($('#webixAnalyticalSelfTest .webix_container .webix_view .webix_ss_body').text() != "") {
		return true;
	} else 	{
		return false;
	}
}

function createNewForm(newFormCode) {

	var allData = getformDataNoCallBack(1);
	var stringifyToPush = {
		code : "elementFormCode",
		val : newFormCode,
		type : "AJAX_BEAN",
		info : 'na'
	};

	allData = allData.concat(stringifyToPush);

	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val()
			+ "&userId=" + $('#userId').val()
			+ "&eventAction=createNewForm&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "doSave",
		data : allData,
		errorMsg : ""
	});
	// call...
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey="
				+ $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',

		success : function(obj) {
			if (obj.errorMsg != null && obj.errorMsg != '') {
				displayAlertDialog(obj.errorMsg);
				hideWaitMessage();
			} else if(obj.data[0].val.toString().indexOf(',') != '-1' && obj.data[0].val.toString().indexOf("WF") !== -1) 
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
			} else if (obj.data[0].val != null) {
				var formCode = onNewButtonIntegration(newFormCode); // change formCode name
				// if needed
				var page = "./init.request?stateKey=" + $('#stateKey').val()
						+ "&formCode=" + formCode + "&formId=-1" + "&userId="
						+ $('#userId').val() + '&PARENT_ID=' + obj.data[0].val;
				showWaitMessage(getSpringMessage('pleaseWait'));
				window.location = page;
				return;
			} else {
				console.log("return data val is null", obj);
			}
		},
		error : handleAjaxError
	});
}

function createNewFormFromMain(newFormCode,parentFormCode,parentFormId) {
	if(newFormCode== 'Request (Copy Default)'){
		var availableDataStructures = dataTableApiDataStructure("Request", parentFormCode, false);
	}else{
		var availableDataStructures = dataTableApiDataStructure(newFormCode, parentFormCode, false);
	}
	var availableDataStructureArray = availableDataStructures.split(' or ');
	 if ($.inArray(parentFormCode, availableDataStructureArray) == -1&&parentFormCode!="InvItemMaterialsMain") {
	        displayAlertDialog(getSpringMessage('pleaseSelect') + " " + availableDataStructures + " " + getSpringMessage('inTheTableAbove'));                       
	        return;
     } 
    else{

	var allData = getformDataNoCallBack(1);
	var stringifyToPush = {
		code : "elementFormCode",
		val : newFormCode,
		type : "AJAX_BEAN",
		info : 'na'
	};
	if(parentFormCode==undefined){
		parentFormCode =$('#formCode').val();
		parentFormId = $('#formId').val();
	}

	allData = allData.concat(stringifyToPush);

	// url call
	var urlParam = "?formId=" + parentFormId + "&formCode=" + parentFormCode
			+ "&userId=" + $('#userId').val()
			+ "&eventAction=createNewForm&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "doSave",
		data : allData,
		errorMsg : ""
	});
	// call...
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey="
				+ $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',

		success : function(obj) {
			if (obj.errorMsg != null && obj.errorMsg != '') {
				displayAlertDialog(obj.errorMsg);
				hideWaitMessage();
			} else if (obj.data[0].val != null) {
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
				} else{
				var formCode = newFormCode=="Request (Copy Default)"?"Request":onNewButtonIntegration(newFormCode); // change formCode name if needed
				
				var page = "./init.request?stateKey=" + $('#stateKey').val()
						+ "&formCode=" + formCode + "&formId=-1" + "&userId="
						+ $('#userId').val() + '&PARENT_ID=' + obj.data[0].val;
				
				if(newFormCode=="Request (Copy Default)"){
					page = page +"&useDefaultData=1";
				}
				
				showWaitMessage(getSpringMessage('pleaseWait'));
				window.location = page;
				return;
			}
				} else {
				console.log("return data val is null", obj);
			}
		},
		error : handleAjaxError
	});
    }
}

/**
 * Save event of request select that is opened from experiment form
 * If request connected to selected sample has another “Destination lab” a warning message should be displayed to user
 * @param afterSave
 * @param saveAction
 * @returns
 */
function doSaveRequestSelect(afterSave,saveAction){
	showWaitMessage("Please wait...");
    var allData = getformDataNoCallBack(1);

    var urlParam = "?formId=" + $('#formId').val() + "&formCode="
	+ $('#formCode').val() + "&userId=" + $('#userId').val()
	+ "&eventAction=checkRequestDestLab&isNew=" + $('#isNew').val();
    var data_ = JSON.stringify({
        action: "doSave",
        data: allData,
        errorMsg: ""
    });
    $.ajax({
        type: 'POST',
        data:data_,
        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) 
        {
        	hideWaitMessage();
            var message = obj.data[0].val;
			if (message!=null && message != "") {
				
				openConfirmDialog({
					onConfirm : function (){doSave(afterSave, saveAction);},
					title : 'Warning',
					message : getSpringMessage(message),
					onCancel: function (){}
				});
				// if yes- refresh...

			} else {
				doSave(afterSave, saveAction);
			}

		},
        error: handleAjaxError
    });
}

function createNewExpFromRequest(){
    var allData = getformDataNoCallBack(1);
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val()
			+ "&userId=" + $('#userId').val()
			+ "&eventAction=createNewExpFromRequest&isNew=1" ;

	var data_ = JSON.stringify({
		action : "doSave",
		data : allData,
		errorMsg : ""
	});
	// call...
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey="
				+ $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',

		success : function(obj) {
			if (obj.errorMsg != null && obj.errorMsg != '') {
				displayAlertDialog(obj.errorMsg);
				hideWaitMessage();
			} else if (obj.data[0].val != null && obj.data[0].val != '') {
				openConfirmDialog({
		            onConfirm: function(){
		            	doNew('Experiment');
		            },
		            title: 'Warning',
		            message: getSpringMessage(obj.data[0].val)
		        });
			} else {
				doNew('Experiment'); //TODO arrange the new in the system!
			}
		},
		error : handleAjaxError
	});

}

function onChangeRequestType(obj)
{
	if(($('#REQUESTTYPE_ID').val() == null || $('#REQUESTTYPE_ID').val() == '')) {		
		return;
	}	
	showWaitMessage("Please wait...");
    var allData = getformDataNoCallBack(1);
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + "&userId=" + $('#userId').val()
			+ "&eventAction=deleteOperationType&isNew=" + $('#isNew').val();

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
			onChangeAjax('REQUESTTYPE_ID');
			hideWaitMessage();
			onElementDataTableApiChange('operartinTypeTable');
			onChangeAjax('REQUESTTYPE_ID'); 			
		},
		error : handleAjaxError
	});
	
}

function getFormTitleWarningMessage(formid) {
	
    var urlParam = "?formId=" + $('#formId').val()
    + "&formCode=" 	+ $('#formCode').val() + "&userId=" + $('#userId').val()
	+ "&eventAction=getFormTitleWarningMessage&isNew="; // + $('#isNew').val();
    var data_ = JSON.stringify({
        action: "doSave",
        data: [],
        errorMsg: ""
    });
    $.ajax({
        type: 'POST',
        data:data_,
        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) 
        {
        	hideWaitMessage();
            var message = obj.data[0].val;
            var expStatus = obj.data[0].val.split(';')[0];
            message =  obj.data[0].val.split(';')[1];
			if (message!=null && message != "" && expStatus=="Approved" ) {
				$('<div title="' + message + '"><img src="../skylineFormWebapp/images/warning.png\" / style="float:left; padding-top: 6px;"  onclick=showWarningMessage()></div>').insertBefore("#pageTitle");
				//$('<div id="warning-mess-div" style="display:none";>' + message + '></div>').prependTo('body');;
			}else if (message!=null && message != "") {
				$('<div title="' + message + '"><img src="../skylineFormWebapp/images/warning.png\" / style="float:left; padding-top: 6px;" ></div>').insertBefore("#pageTitle");
				//$('<div id="warning-mess-div" style="display:none";>' + message + '></div>').prependTo('body');;
			}
		},
        error: handleAjaxError
    });
}

function showWarningMessage(){
	/*var message = '';
	for(var i = 0; i < $("#warning-mess-div p").size(); i++) {
		message += $("#warning-mess-div p")[i].innerText + '<br>';
	}*/
	
	openConfirmDialog({
		onConfirm : function (){
			changeExpStatusToActive();
		},
		title : 'Warning',
		message : 'You are about to change the status from Approved to ‘Active’. Are you sure you want to continue?',
		onCancel: function (){}
	});
}

function checkMainResults(formId,afterSave,saveAction){
	try{
	var allData = getformDataNoCallBack(1);
    var urlParam = "?formId=" +formId
//    + "&formCode=" 	+ $('#formCode').val() + "&userId=" + $('#userId').val()
	+ "&eventAction=checkMainResults&isNew="; // + $('#isNew').val();
    var data_ = JSON.stringify({
        action: "doSave",
        data: allData,
        errorMsg: ""
    });
    $.ajax({
        type: 'POST',
        data:data_,
        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) 
        {
        	doSave(afterSave, saveAction);
        },
        error: handleAjaxError
    });
	}catch(e) {
		doSave(afterSave, saveAction);
	}
}

function sendNotifExpOwner(formId, afterSave, saveAction){
	try{
	var allData = getformDataNoCallBack(1);
    var urlParam = "?formId=" +formId
//    + "&formCode=" 	+ $('#formCode').val() + "&userId=" + $('#userId').val()
	+ "&eventAction=sendNotifExpOwner&isNew="; // + $('#isNew').val();
    var data_ = JSON.stringify({
        action: "doSave",
        data: allData,
        errorMsg: ""
    });
    $.ajax({
        type: 'POST',
        data:data_,
        url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) 
        {
        	doSave(afterSave, saveAction);
        },
        error: handleAjaxError
    });
	}catch(e) {
		doSave(afterSave, saveAction);
	}
}

function checkForCharacterMassBalanceOnClick(checkboxObj, inx)
{
	var $selCheckbox = $(checkboxObj);
	var isChecked = $selCheckbox.is(':checked');
	if(inx != undefined && inx != null && inx == "0") {
		var chkGroupName = $selCheckbox.attr('name');
		$selCheckboxGroup = $('.experimentDefaultMassBalanceClass[name="'+chkGroupName+'"]');
		$selCheckboxGroup.prop('checked',isChecked);
	}
	$('.experimentDefaultMassBalanceClass:not(:checked)').prop('disabled',isChecked);	
}

function toggleDisableStepLAMole()
{
	if($('#chkManualUpdate').is(':checked'))
		$("#limitingAgentMole").removeClass('disabledclass');
	else
		$("#limitingAgentMole").addClass('disabledclass');
}

function initExperimentMassBalanceTab(domId,fInx)
{
	$('#massBalanceName'+fInx).removeAttr('onchange');
	$('#massBalanceName'+fInx).attr("onchange", "setMassBalanceNameAsTabName('"+fInx+"',true)");
	setMassBalanceNameAsTabName(fInx,false);
	
	var $checkboxMB = $('#chkCharacterMassBalance'+fInx);
	$checkboxMB.removeAttr('onclick');
	$checkboxMB.attr("onclick", "checkForCharacterMassBalanceOnClick(this)");
	$checkboxMB.attr('chkParentIndex',((fInx=="")?"1":fInx));
	$checkboxMB.addClass('experimentDefaultMassBalanceClass');
	if($checkboxMB.is(':checked'))
	{
		$('#hdnOldDefaultMassBalance').val($('#formCode').val()+","+((fInx=="")?"1":fInx)+""); // Experiment/ExperimentCP  //add ExperimentCP for "Continuous Process"
	}
	
	var $ddl = $('#MATERIALREF_ID'+fInx);
	var $limitAgentMole = $("#limitingAgentMole"+fInx);
	var currLAvalue = "";
	
	/* TODO: alex: Set through settings in form builder (possibility to hide 'remove' icon in case ddl is not mandatory) */
	//remove possibility to unselect value
	$('#MATERIALREF_ID'+fInx+'_chosen').find('abbr.search-choice-close').remove();
	
	$ddl.removeAttr('onchange');
	$ddl.change(function() {
		
		var $input = $('#limitingAgentMole'+fInx);
		var la = $ddl.find('option:selected').attr('limAgentMole');
		$input.attr('realvalue',la).attr('title',la);
		$input.val(convertDecimalToExponential(la));
		//remove possibility to unselect value
		$('#MATERIALREF_ID'+fInx+'_chosen').find('abbr.search-choice-close').remove();	
		updateMassbalanceDataperRun(fInx);
	});
	if($ddl.prop('options').length > 0 && $ddl.prop('selectedIndex') == 0 )
	{
		// select first non empty value
		$ddl.find('option:eq(1)').prop('selected', true).trigger('chosen:updated');
		//remove possibility to unselect value
		$('#MATERIALREF_ID'+fInx+'_chosen').find('abbr.search-choice-close').remove();	
		
		currLAvalue = $ddl.find('option:selected').attr('limAgentMole');
	}
	else
	{
		currLAvalue = $limitAgentMole.val();
	}
	$limitAgentMole.attr('realvalue',currLAvalue).attr('title',currLAvalue);
	$limitAgentMole.val(convertDecimalToExponential(currLAvalue));	
	updateMassbalanceDataperRun(fInx);
	
	$limitAgentMole.removeAttr('onchange');
	$limitAgentMole.change(function(){
		var $this = $(this);
		$this.attr('realvalue', $this.val());
		setWebixMassBalanceLimitingAgentMole(domId);
		calculateMassBalanceFields(domId, fInx);
	});
	
	
	// calc mass balance table and fields on load
	calculateMassBalanceFields(domId, fInx);
}

function setMassBalanceNameAsTabName(fInx,onlyActive)
{
	var name = $.trim($('#massBalanceName'+fInx).val());
	var tabInx = (fInx == "")?"1":fInx;
	name = (name == "")?"Mass Balance "+tabInx:name;

	if(onlyActive)
	{
		var currTabIndex = $("#tempalteTabs").tabs('option', 'active');
		$("#tempalteTabs ul>li a").eq(currTabIndex).html(name);
	}
	else
		$("#tempalteTabs ul>li a[href='#MassBallance"+tabInx+"Tab']").html(name);
	updateMassbalanceDataperRun(fInx);
	
}

function disableWorkupRes(){
	var resultid_holder = $('#resultid_holder').val();
	if(resultid_holder!=''){
		generalBL_disablePage();
	}
}
function updateResults(){
	var allData = getformDataNoCallBack(1);
    var urlParam = "?formId=" +$('#formId').val()
    + "&formCode=" 	+ $('#formCode').val() + "&userId=" + $('#userId').val()
	+ "&eventAction=updateResults&isNew="; // + $('#isNew').val();
    var data_ = JSON.stringify({
        action: "updateResults",
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
        	 if($('#formCode').val() == "Step"){
        		 onElementDataTableApiChange('products');
        	 }
        	 else if($('#formCode').val() == "WorkupFeeding"){
        		 onElementDataTableApiChange('materials');
        		 doSaveWorkup('Reload');
        	 }
        	 else if($('#formCode').val() == "WorkupCrystallize" ||$('#formCode').val() == "WorkupDistillation"){
        		onElementDataTableApiChange('startingMixtureDefinition');
        		doSaveWorkup('Reload');
        	}
        },
        error: handleAjaxError
    });
}

function bl_isActualMode()
{
	var obj =  $('input[name="planned_actual"]:checked');
	if(obj!=null || obj!=undefined ){
		return  obj.val()=='Actual';
	}
	return null;
		
}

function open_Warning_noResults_dialog(sample_id,samplename){
	displayAlertDialog("No Main results were selected for sample <a onclick=checkAndNavigate(['"+sample_id+"','Sample',''])><span>"+samplename+"</span></a>");
}

function open_Warning_AdditionalResults_dialog(sample_id,samplename){
	displayAlertDialog("Sample <a onclick=checkAndNavigate(['"+sample_id+"','Sample',''])><span>"+samplename+"</span></a> has additional results set");
}

function changeExpStatusToActive(){
	var allData = getformDataNoCallBack(1);
    var urlParam = "?formId=" +$('#formId').val()
    + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val()
+ "&eventAction=changeExpStatusToActive&isNew="; // + $('#isNew').val();
    var data_ = JSON.stringify({
        action: "copyDesignStep",
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
        	if( obj.data[0].val == "-1"){
        		displayAlertDialog('Only Approver can change the status to ‘Active’. Process is cancelled');
        	}else{
        		$("#STATUS_ID").val($("#STATUS_ID option:contains('Active')").val());
	        	 $("#STATUS_ID").trigger('chosen:updated');
	        	 $("#reasonForChange").val("results update");
        		doSaveWithConfirmCharSample('Reload');
        	}
       
        },
        error: handleAjaxError
    });
}

/**
 * override materialselec save - instead of saving the data it will update ReportDesignExp stepImpuritiesIdList, uncheck chkImpuritiesStep and reload the impurities table (all in the parent form)
 * @returns
 */
function doSaveMaterialSelect() {
	try {
		if(parent.$('#formCode').val() == 'ReportDesignExp') {
			var allData = getformDataNoCallBack(1);
			$.each( allData , function() {
				if(this.code == 'INVITEMMATERIAL_ID') {
					parent.$('#stepImpuritiesIdList').val(this.val);
				}
			});
			//loadMaterialSelect from material select form
			var formId_ = parent.$('#formId').val();
			var stepImpuritiesIdList_ = parent.$('#stepImpuritiesIdList').val();
			loadMaterialSelectCore(formId_, stepImpuritiesIdList_, true);
			//checked chkImpuritiesStep
			parent.$('#chkImpuritiesStep').prop('checked', false);
			parent.$('#numConcentrationImpStep').val('');
			//close popup
			parent.$("iframe").attr('src', 'about:blank'); // fix ie bug cannot enter text to input
            parent.$('#prevDialog').dialog('close');
		}
	} catch(e) {
		
	}
}

/**
 * 
 * @param formId_ ReportDesignExp fromid
 * @param stepImpuritiesIdList_ list of material id's (for example '11,222,33')
 * @param isPopup true if the call is from the popup to the parent form
 * @returns
 */
function loadMaterialSelectCore(formId_, stepImpuritiesIdList_, isPopup) {
	var callBack_;
	if(isPopup) {
		callBack_ = function(){parent.onElementDataTableApiChange('impurities');};
	} else {
		callBack_ = function(){onElementDataTableApiChange('impurities');};
	}
	setFormParamMap('ReportDesignExp', formId_,"stepImpuritiesIdList",stepImpuritiesIdList_,callBack_);
}

//TODO alex: temporary workaround, need to get legals trough form builder settings
function resetLegalsForRichtextElem(elemID) {
	
	if(($('#formCode').val() == "Action" && elemID == "observation" )
			||
	   ($('#formCode').val() == "SelfTest" && elemID == "summary" )
	) {
		return true;
	}
	return false;
}


function setReportTitle(){
	var ReportDesignExpName = getDesignIdOrNameFromSession("NAME");
	 var designId=getDesignIdOrNameFromSession("ID")
	//check design exists
	if(designId==null&&ReportDesignExpName==null)
		$('#bSaveDesign').prop('disabled', true);
	else 	$('#bSaveDesign').prop('disabled', false);
	//$('#designNameHolder').val("");
	//$('.mainSaveFormAndDefinitionByNameBtn').css('display', 'inline');
	//$('.mainSaveFormAndDefinitionByNameBtn').attr('title', 'Save data and display Report');
	setTimeout(function () 
	{
		var url=new URL(window.location);
		var isTemp = url.searchParams.get("isTemp");
		
		// load page withou design
		//if(isTemp==null&&ReportDesignExpName == ""&&designId!="-1"||isTemp==null&&ReportDesignExpName == null&&designId!="-1"){
		if(designId==null&&ReportDesignExpName==null){
   		var repName = $('#reportName').val();
   		if(repName != null && repName != ""){
   			$('#pageTitle').html("Scheme Name: "+$('#reportName').val());
   		
   		}
   		else{
   			$('#pageTitle').html("<b>Experiment Analysis Report</b>");
   		}
		} else {
			//load report with  design 
			//if(isTemp==='True')
			//updateSpesificFieldInDesignSession("FORMID","-1")
					var designName = ReportDesignExpName;
					var reportName = $('#reportName').val();
            if(designId=="-1"){
					if (reportName == null || reportName == "") {
						if (designName == "")
							$('#pageTitle')
									.html(
											"<p><b>Experiment Analysis Report</b></p><p style=\"color:Grey\"><h4>Design Name: Temporary design</h4></p>");
						else  if (designName!=null)
							$('#pageTitle')
									.html(
											"<p><b> Experiment Analysis Report</b></p><p style=\"color:Grey\"><h4>Design Name:"
													+ designName
													+ "-modified</h4></p>");
					} else if (designName != ""&&designName != null) {
						$('#pageTitle')
								.html(
										"<p><b>Scheme Name: "
												+ reportName
												+ "</b></p><p style=\"color:Grey\"><h4>Design Name: "
												+ designName
												+ "-modified</h4></p>");
					} else {
						$('#pageTitle')
								.html(
										"<p><b>Scheme Name: "
												+ reportName
												+ "</b></p><p style=\"color:Grey\"><h4>Design Name: Temporary design</h4></p>");

	                }
            }
            else{
           	 if (reportName == null || reportName == "") {
						
							$('#pageTitle')
							.html(
									"<p><b>Experiment Analysis Report"
											+ "</b></p><p style=\"color:Grey\"><h4>Design Name: "
											+ designName+
											"</h4></p>");
					
					
					
					} else if (designName != ""&&designName != null) {
						$('#pageTitle')
								.html(
										"<p><b>Scheme Name: "
												+ reportName
												+ "</b></p><p style=\"color:Grey\"><h4>Design Name: "
												+ designName+
												"</h4></p>");
					
					}
            }
		}
		
		
	},100);
	
}

function depletedBatch(){
	if ($('input[name="inUseDepleted"]:checked').val() == 'Depleted' || $('#quantity').val() == '0') {
    	openConfirmDialog({
	        onConfirm: function(){
	        	if ($('input[name="inUseDepleted"]:checked').val() == 'Depleted'){
	        		$('#quantity').val(0);
	        	    $('#quantity').attr('realvalue',0);
	        	}else{//$('#quantity').val() == '0'
	        		$("input[name=inUseDepleted][value='Depleted']").prop("checked", true);
		            onChangeAjax('inUseDepleted');
	        	}
	        },
	        title: 'Warning',
	        message: getSpringMessage('confirmDepletedBatch'),
	        onCancel: function(){
	        	if ($('input[name="inUseDepleted"]:checked').val() == 'Depleted'){
	        		$("input[name=inUseDepleted][value='In Use']").prop("checked", true);
	        	    onChangeAjax('inUseDepleted');
	        	}else{//$('#quantity').val() == '0'
	        		$('#quantity').val('');
	        	    $('#quantity').attr('realvalue','');
	        	}
	        }
	    });
        
    }
}

function doSaveMaterialSlctSearch(save_){
	var table_ = $('#materialTable').DataTable(); 
    var csvList_=[];
	// add selected values that are not in lastMultiValues + remove unselected values that are in this table (=> the user unselected them)
    table_.$('input[Type="checkbox"]').each(function (index) {
		if($(this).prop('checked')) {
			csvList_.push($(this).val());
		}
    });  
    
    var toReturn = {};
    toReturn["INVITEMMATERIAL_ID"] = csvList_;
    $('#toReturn').val(JSON.stringify(toReturn));
    save_.setAttribute('flag', '1');
    parent.$('#prevDialog').dialog('close');
}
function confirmSpreadNameChanged(afterSave,saveAction){
	if($('#spreadsheetTemplaName').val()!= $('#spreadsheetTemplaName').attr('lastvalue') && $('#spreadsheetTemplaName').attr('lastvalue')!= "" && $('#spreadsheetTemplaName').attr('confirmChangedValue')!="1"){
	 openConfirmDialog({
			onConfirm : function (){
				doSaveSpreadsheetTemplate('Reload');
			},
			title : 'Warning',
			message : getSpringMessage('The spreadsheet name: \''+$('#spreadsheetTemplaName').attr('lastvalue')+'\' will be overridden with the new name.'
						+'</br> are you sure you want to continue?'),
			onCancel: function (){
				$('#spreadsheetTemplaName').val($('#spreadsheetTemplaName').attr('lastvalue'));
			}
		});
	}else{
		doSaveSpreadsheetTemplate('Reload');
	}
}

function doSaveSpreadsheetTemplate(afterSave,saveAction){
	//get all data
    var allData = getformDataNoCallBack(1);
     
    //url call
    var urlParam =
        "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&eventAction=" + 'checkDuplicateSpreadsheetName' + "&isNew=" + $('#isNew').val();

    var data_ = JSON.stringify({
        action: "saveSpreadsheetTemplate",
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
        		openConfirmDialog({
					onConfirm : function (){
						var res = obj.data[0].val.split(',');
						var form_id = res[0];
						var changeDate = res[1];
						var changeBy = res[2];
						$('#formId').val(form_id);
						if(changeBy != undefined && changeDate!= undefined){
							$('#lastChangeUserId').val(changeBy);
							$('#lastChangeDate').val(changeDate);
						}
						doSave((function(){navigateToForm(form_id,'SpreadsheetTempla')}), saveAction);
					},
					title : 'Warning',
					message : getSpringMessage('SPREADSHEET_TEMPLATE_SAME_NAME'),
					onCancel: function (){}
				});
        	}
        	else{
        		doSave(afterSave, saveAction);
        	}
        },
        error: handleAjaxError
    });	
}


function saveSpreadsheetTemplateAs(afterSave,saveAction){
	var mandatoryIndicator= isMandatoryFieldsRequired();
    if ((mandatoryIndicator.setRequired == '1'&& checkRequired()) || (mandatoryIndicator.setRequired == '0' && checkRequiredByList(mandatoryIndicator.mandatoryList))) {
        if (!checkDateMinMaxValidity() || !checkNumberMinMaxValidity() || !checkTimeValidity() || !checkEmailValidity() 
        		|| !elementRichTextEditorValidation()
            ) 
        {
        		prop.onChangeAjaxFlag = false;
        		return;
        } 
    
		var stringifyToPush = {
				code : 'spreadsheetTemplaName',
				val : $('#spreadsheetName').val(),
				type : "AJAX_BEAN",
				info : 'na'
		};
		var allData = getformDataNoCallBack(1);
		allData = allData.concat(stringifyToPush);
	     
	    //url call
	    var urlParam =
	        "?formId=" + parent.$('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&eventAction=" + 'checkDuplicateSpreadsheetName' + "&isNew=" + $('#isNew').val();
	
	    var data_ = JSON.stringify({
	        action: "saveSpreadsheetTemplate",
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
	        		openConfirmDialog({
						onConfirm : function (){
							var res = obj.data[0].val.split(',');
							var form_id = res[0];//obj.data[0].val;
							var changeDate = res[1];
							var changeBy = res[2];
							parent.$('#formId').val(form_id);
							if(changeBy != undefined && changeDate!= undefined){
								parent.$('#lastChangeUserId').val(changeBy);
								parent.$('#lastChangeDate').val(changeDate);
							}
							parent.$('#spreadsheetTemplaName').val( $('#spreadsheetName').val());
							parent.$('#CREATOR_ID').val($('#userId').val());
							parent.$('#saveAsFunc').val('do_save_and_navigate');
							//doSave((function(){navigateToForm(form_id,'SpreadsheetTempla')}), saveAction);
							parent.$('#prevDialog').dialog('close');
						},
						title : 'Warning',
						message : getSpringMessage('SPREADSHEET_TEMPLATE_SAME_NAME'),
						onCancel: function (){
							parent.$("#prevDialog iframe").attr('src','about:blank');
							parent.$('#saveAsFunc').val('');
							parent.$('#prevDialog').dialog('close');
						}
					});
	        	}
	        	else{
	        		if(parent.$('#spreadsheetTemplaName').val() == $('#spreadsheetName').val() && parent.$('#CREATOR_ID').val() == $('#userId').val() ){
	        			/*parent.$('#saveAsFunc').val('do_save');
	        			parent.$('#prevDialog').dialog('close');*/
	        			openConfirmDialog({//fixed bug 8495
	    					onConfirm : function (){
	    						parent.$('#saveAsFunc').val('do_save');
	    	        			parent.$('#prevDialog').dialog('close');
	    					},
	    					title : 'Warning',
	    					message : getSpringMessage('SPREADSHEET_TEMPLATE_SAME_NAME'),
	    					onCancel: function (){
	    						/*parent.$("#prevDialog iframe").attr('src','about:blank');
	    						parent.$('#prevDialog').dialog('close');*/
	    					}
	    				});
	        		}else{
	        			parent.$('#spreadsheetTemplaName').val( $('#spreadsheetName').val());
	        			parent.$('#saveAsFunc').val('clone');
	        			parent.$('#prevDialog').dialog('close');
	        		}
	        	}
	        },
	        error: handleAjaxError
	    });	
    }
}
function navigateToForm(formId,formCode){
   checkAndNavigate([formId ,formCode,'','false']);
}

function checkNonFamiliarAndSave(doSaveFunc,additionalInfAfterSave){
	/*if(doSaveFunc.constructor === Array){
		additionalInfAfterSave = doSaveFunc.slice(1);
		doSaveFunc = doSaveFunc[0];
		
	}*/
	var obj = $('#STATUS_ID option:selected');
	if(obj.text() != "Planned" && $('#formCode').val() != 'ExperimentFor'){
	//get all data
    var allData = getformDataNoCallBack(1);
    if($('#formCode').val()=='Step'  ){//
    	var _obj = getDataFromEditableTable(["reactants","solvents","products"], -1);
    	if(!(_obj instanceof Object))
    	{
    		hideWaitMessage();
    		displayAlertDialog(_obj + "\n Save was not performed.");
    		return;
    	}
    	var reactionData = {
					code : "reactionData",
					val : JSON.stringify(_obj),
					type : "AJAX_BEAN",
					info : 'na'
				};
    	allData = allData.concat(reactionData);
    }
    
    //url call
    var urlParam =
        "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&eventAction=" + 'checkNonFamiliar' + "&isNew=" + $('#isNew').val();

    var data_ = JSON.stringify({
        action: "checkNonFamiliar",
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
        		var res = obj.data[0].val;
        		var msgPart = res.split('##');
        		var message_ = msgPart[0];
        		var materialList = msgPart[1];
        		var isAletMsg = msgPart[2];
        		var isFamiliarAfterConfirm = msgPart[3];
        		if(isAletMsg =="false"){
	        		openConfirmDialog({//show an alert when the untrained users not contain the logged in one, else-show a confirm message
						onConfirm : function (){
							//if the user would be trained and is the only one that was not trained before-the step continues saving operation, else-save is failed
							var callback = isFamiliarAfterConfirm == "true"?doSaveFunc:function(){displayAlertDialog("Please realize that there are still some unfamiliar users");};
							trainedUser(materialList,callback,additionalInfAfterSave);
						},
						title : 'Warning',
						message : message_,
						onCancel: function (){
							
						}
					});
        		} else {
        			displayAlertDialog(message_);
        		}
        	}
        	else{
        		doSaveFunc(additionalInfAfterSave);
        	}
        },
        error: handleAjaxError
    });	
	}
	else{
		doSaveFunc(additionalInfAfterSave);
	}
}

function trainedUser(materialList,doSaveFunc,additionalInfAfterSave){
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
	        	doSaveFunc(additionalInfAfterSave);
	        },
	        error: handleAjaxError
	    });	
	}else{
		doSaveFunc(additionalInfAfterSave);
	}
}
function isGeneralPopup()
{
	 //TODO check the popup Kind
	if (window.self.$('#saveButton').val()==undefined)
		return true;
	else return false;
}
function closeViewSpreadSheetPopup()
{

	if(window.self !== window.top){
		if(parent.$('#prevDialog1').length>0)
			parent.$('#prevDialog1').dialog('close');
	}
	else{
		if($('#prevDialog1').length>0)
			$('#prevDialog1').dialog('close');
	}
	
	
}

function checkCancelledStatus(afterSave){
	var laststatusId = $('#LASTSTATUS_ID').val();
	var laststatusName = $('#STATUS_ID option[value='+laststatusId+']').text();
	var currentStatusName = $('#STATUS_ID option:selected').text();
	if(currentStatusName =='Cancelled'){
		openConfirmDialog({
			onConfirm : function (){
				doSave(afterSave);
			},
			title : 'Warning',
			message : getSpringMessage('Are you sure you want to cancel this Recipe?'),
			onCancel: function (){
				$('#STATUS_ID').val(laststatusId);
				$('#STATUS_ID').trigger('chosen:updated');
			}
		});
	} else{
		doSave(afterSave);
	}
}
function doSaveExperimentGroup(){
	parent.$('#prevDialog').data("experimentGroupDesc",$('#ExperimentGroupName').val());
	parent.$('#prevDialog').data("experimentGroupId",$('#formId').val());
	doSave('close');
}

function getExpectedFormCodeByStructSelection(selectionFormCode){
	var toReturn = selectionFormCode;
	if(selectionFormCode == 'SampleSelect'){
		toReturn = "Sample";
	}
	return toReturn;
}

function doSavePurityList(){
	var allData = getformDataNoCallBack(1);	
	showWaitMessage("Please wait..."); 
    // url call
    var urlParam =
        "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&eventAction=" + 'doSavePurityList' + "&isNew=" + $('#isNew').val();
    var data_ = JSON.stringify({
        action: "doSavePurityList",
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
        	hideWaitMessage();
    		parent.$('#prevDialog').dialog('close');
        	
        },
        error: handleAjaxError
    });
}

function saveStepMinFrData(obj) {
	if(obj.id == 'stepName') {
	var newStepName = $("#stepName").val();
	if(newStepName == null || $("#stepName").val().trim() == '') {
		displayAlertDialog("Step Name is a required field!");
		 $("#stepName").val("STEP " +  $("#formNumberId").val());
	} else {
		var stepName="stepName"+';'+$("#stepName").val();
		var stringifyToPush = {
				code : 'objToSave',
				val : stepName,
				type : "AJAX_BEAN",
				info : 'na'
		};
		var allData = getformDataNoCallBack(1);
		allData = allData.concat(stringifyToPush);
		
	    
	    // url call
	    var urlParam =
	        "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&eventAction=" + 'saveStepMinFrData' + "&isNew=" + $('#isNew').val();
	    var data_ = JSON.stringify({
	        action: "saveStepMinFrData",
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
	        	if(obj.data[0].val == '0'){
	        		displayAlertDialog("Update Failed!");
	        		 $("#stepName").val(parent.$("#button_stepIframes_"+$('#formId').val()).text());
	        	}
	        	
	        },
	        error: handleAjaxError
	    });	
	
		
	}
}
if(obj.id == 'batchSizeMaterial') {
	
	var batchSizeMaterial ="batchSizeMaterial"+';'+ $("#batchSizeMaterial").val();
	var stringifyToPush = {
			code : 'objToSave',
			val : batchSizeMaterial,
			type : "AJAX_BEAN",
			info : 'na'
	};


	var allData = getformDataNoCallBack(1);
	allData = allData.concat(stringifyToPush);
		
	    
	    // url call
	    var urlParam =
	        "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&eventAction=" + 'saveStepMinFrData' + "&isNew=" + $('#isNew').val();
	    var data_ = JSON.stringify({
	        action: "saveStepMinFrData",
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
	        	if(obj.data[0].val == '0'){
	        		displayAlertDialog("Update Failed!");
	        		
	        	}
	        	
	        },
	        error: handleAjaxError
	    });	
	
		
	}
if(obj.id == 'batchSizeProd') {
	
	var batchSizeProd ="batchSizeProd"+';'+ $("#batchSizeProd").val();
	var stringifyToPush = {
			code : 'objToSave',
			val : batchSizeProd,
			type : "AJAX_BEAN",
			info : 'na'
	};
	var allData = getformDataNoCallBack(1);
	allData = allData.concat(stringifyToPush);
		
	    
	    // url call
	    var urlParam =
	        "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&eventAction=" + 'saveStepMinFrData' + "&isNew=" + $('#isNew').val();
	    var data_ = JSON.stringify({
	        action: "saveStepMinFrData",
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
	        	if(obj.data[0].val == '0'){
	        		displayAlertDialog("Update Failed!");
	        		
	        	}
	        	
	        },
	        error: handleAjaxError
	    });	
	
		
	}
}

function showHideDeleteStepIcon() {
	try {
		var dbstatusId = $('#LASTSTATUS_ID').val();
		var dbstatusname_ = $('#STATUS_ID option[value='+dbstatusId+']').text();
		if(dbstatusname_ != null && dbstatusname_ != 'Planned') {
			console.log("hide step cube delete icon on non planned status experiment");	
			$('.collapsible_iframes .fa-trash').css('visibility','hidden');
		}
	} catch(e) {
		console.log("error in showHideDeleteStepIcon");
	}
}

function newStepFloatingButton()
{
	showWaitMessage(getSpringMessage('pleaseWait'));
	 $.ajax({
         type: 'POST',
         data: '{"action" : "getNewAvailableFormList","' + 'data":[' + '{"code":"formCode","val":"' + $('#formCode').val() + '"},' + '{"code":"formId","val":"' + $('#formId').val() + '"},' + '{"code":"stateKey","val":"' + $('#stateKey').val() + '"}' + '],' + '"errorMsg":""}', //TODO key check
         url: "./getNewAvailableFormList.request",
         contentType: 'application/json',
         dataType: 'json',
         success: function (obj) {
        	  if (obj.errorMsg != null && obj.errorMsg != '') {
        		  hideWaitMessage();
                  displayAlertDialog(obj.errorMsg);
              } else {
	                if(obj.data[0].val != ""){
	                    optionsArray = obj.data[0].val.split(',');
	                    if(optionsArray.indexOf("StepFr")> -1)
	                        doNew('StepFr');
	                    else{
	                    	 hideWaitMessage();
	                    	 displayAlertDialog("Creating new step is not allowed.");
	                    }
	                } else {
	                	 hideWaitMessage();
	                	 displayAlertDialog("Creating new step is not allowed!");
	                }
              }
        },
        error: handleAjaxError
    });
}

function newRequestFloatingButton()
{
	showWaitMessage(getSpringMessage('pleaseWait'));
	 $.ajax({
         type: 'POST',
         data: '{"action" : "getNewAvailableFormList","' + 'data":[' + '{"code":"formCode","val":"' + $('#formCode').val() + '"},' + '{"code":"formId","val":"' + $('#formId').val() + '"},' + '{"code":"stateKey","val":"' + $('#stateKey').val() + '"}' + '],' + '"errorMsg":""}', //TODO key check
         url: "./getNewAvailableFormList.request",
         contentType: 'application/json',
         dataType: 'json',
         success: function (obj) {
        	  if (obj.errorMsg != null && obj.errorMsg != '') {
        		  hideWaitMessage();
                  displayAlertDialog(obj.errorMsg);
              } else {
	                if(obj.data[0].val != ""){
	                    optionsArray = obj.data[0].val.split(',');
	                    if(optionsArray.indexOf("Request")> -1)
	                        doNew('Request');
	                    else{
	                    	 hideWaitMessage();
	                    	 displayAlertDialog("Creating new request is not allowed.");
	                    }
	                } else {
	                	 hideWaitMessage();
	                	 displayAlertDialog("Creating new request is not allowed!");
	                }
              }
        },
        error: handleAjaxError
    });
}

//***************** favorit code ***********************
function onChangefavorite (idList,isFavoriteChecked, isStar, domId) {
	try {
		if(idList === undefined || $.trim(idList).length == 0) {
			return;
		}
	} catch(e) {
		displayAlertDialog("Favorite update failed!");
	}
	
	//star in title 
	var className = $("#favoriteTitle").prop('class');
	if(className !== undefined ){	
		isFavoriteChecked = (className == "fa fa-star")?0:1;
	}
	
	var stringifyToPush = {
			code : 'obj_id_csv',
			val : idList,
			type : "AJAX_BEAN",
			info : 'na'
	};
	var stringifyToPush1 = {
			code : 'isFavoriteChecked',
			val : isFavoriteChecked,
			type : "AJAX_BEAN",
			info : 'na'
	};
	var allData = [];
	allData = allData.concat(stringifyToPush);
	allData = allData.concat(stringifyToPush1);
     
    //url call
    var urlParam =
        "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&eventAction=" + 'updateSpreadsheetFavorite' + "&isNew=" + $('#isNew').val();

    var data_ = JSON.stringify({
        action: "updateSpreadsheetFavorite",
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
//        	 hideWaitMessage();
        	 if (obj.errorMsg != null && obj.errorMsg != '') {
                 hideWaitMessage();
                 displayAlertDialog(obj.errorMsg);
             } else if(obj.data[0].val=="-1"){
            	 displayAlertDialog("Favorite update failed");//TODO change back to empty star or checkbox according isStar
             }
        	 
        	 //call favoriteHeaderMng
        	 if(domId !== undefined) {
            	 favoriteHeaderMng(domId);
             }
        	 
        	 if(className !== undefined ){	// clored the star in title (move before call?)
         		if (className == 'fa fa-star') {
         			$("#favoriteTitle").removeClass('fa fa-star');
         			$("#favoriteTitle").addClass('fa fa-star-o');
         			$("#favoriteTitle").css("color","black");
         		} else if (className == 'fa fa-star-o') {
         			$("#favoriteTitle").removeClass('fa fa-star-o');
         			$("#favoriteTitle").addClass('fa fa-star');
         			$("#favoriteTitle").css("color","#62B2DB");
         		}
         	}
        },
        error: handleAjaxError
    });	
}

function onChangeHeaderFavorite(obj,table) {
	var headerObj=$(obj);
	var domId=$(table).attr('id');
	var favoritesArrayId=[];
	var isFavoriteHeaderChecked = -1;
	if (headerObj.hasClass('fa fa-star')) {
		isFavoriteHeaderChecked = 0;
		headerObj.removeClass('fa fa-star');
		headerObj.addClass('fa fa-star-o');
		headerObj.css("color","black");
//		headerObj.attr('value','no');
	} else if (headerObj.hasClass('fa fa-star-o')) {
		isFavoriteHeaderChecked = 1;
		headerObj.removeClass('fa fa-star-o');
		headerObj.addClass('fa fa-star');
		headerObj.css("color","#62B2DB");
//		headerObj.attr('value','yes');
	}
	
	$("#"+domId+" tr td i[name='chb_favorite']").each(function() {
	    var obj = $(this);
	    var rowId = obj.attr('rowId');
		if (isFavoriteHeaderChecked == 1) {
			if(obj.hasClass('fa fa-star-o')) {
				favoritesArrayId.push(rowId);
				obj.removeClass('fa fa-star-o');
				obj.addClass('fa fa-star');	
				obj.css("color","#62B2DB");
				obj.attr('value','yes');
			}
		} else if (isFavoriteHeaderChecked == 0) {
			if(obj.hasClass('fa fa-star')) {
				favoritesArrayId.push(rowId);
				obj.removeClass('fa fa-star');
				obj.addClass('fa fa-star-o');	
				obj.css("color","black");
				obj.attr('value','no');
			}
		}
	});
	onChangefavorite(favoritesArrayId.join(","), isFavoriteHeaderChecked,true,domId);
}

function favoriteHeaderMng(domId) {
	try {
		if($("#"+domId+" th i[name='chb_favorite']").length > 0) {
			if(checkIfAllFavorites(domId)) {
				var headerObj=$("#"+domId+" tr th i[name='chb_favorite']");
				if(headerObj.hasClass('fa fa-star-o')) {
					headerObj.removeClass('fa fa-star-o');
					headerObj.addClass('fa fa-star');
					headerObj.css("color","#62B2DB");
				}
			} else {
				var headerObj=$("#"+domId+" tr th i[name='chb_favorite']");
				if(headerObj.hasClass('fa fa-star')) {
					headerObj.removeClass('fa fa-star');
					headerObj.addClass('fa fa-star-o');
					headerObj.css("color","black");
				}
			}
		}
	} catch(e) {
		console.log("favoriteHeaderMng error!");
	}
}

function checkIfAllFavorites(domId) {
	if($("#"+domId+" tr td i[name='chb_favorite'][class='fa fa-star']").length==$("#"+domId+" tbody tr").length 
			&& $("#"+domId+" tbody tr").length != 0) { // != 0 -> if there are records
		return true;
	}
	else return false;
}
//***************** favorit code  done! ***********************

function restoreColumnsByDefault(level) {
	var domId = $('#tableId').val();
	var level = parent.$('#' + domId + '_structCatalogItem').children('option:selected').val();
	var formCodeMain = parent.$('#formCode').val();
	var stringifyToPush = {
			code : 'level',
			val : level,
			type : "AJAX_BEAN",
			info : 'na'
	};
	var allData = getformDataNoCallBack(1);
	allData = allData.concat(stringifyToPush);
	
	stringifyToPush = {
			code : 'formCodeMain',
			val : formCodeMain,
			type : "AJAX_BEAN",
			info : 'na'
	};
	
	allData = allData.concat(stringifyToPush);
	
    // url call
    var urlParam =
    	"?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val() + "&eventAction=" + 'getColumnByDefault' + "&isNew=" + $('#isNew').val()+ "&parentId=" + $('#formId').val();
    var data_ = JSON.stringify({
        action: "getColumnByDefault", //getExperimentGroupNumber
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
        	var removedColNameArr=obj.data[0].val.split('@');
//        	if($("#EXPERIMENTGROUP_ID option:contains('"+experimentGroupId+"')").val() == undefined) {
//        		$('#EXPERIMENTGROUP_ID').append("<option value=\"" + experimentGroupId + "\" selected>" + groupNumber+'-'+experimentGroupDesc + "</option>");
//        		
//        		$('#EXPERIMENTGROUP_ID').trigger('chosen:updated');
//        	}else{
//        		$('#EXPERIMENTGROUP_ID').val($("#EXPERIMENTGROUP_ID option:contains('"+experimentGroupId+"')").val());
//        		 $('#EXPERIMENTGROUP_ID').trigger('chosen:updated');
//        	}
        	
//        	var removedColNameArr =''.split('@');
        	//var removedColNameArr = $('[id="' + domId + '_colsArray"]').val().split('@');
        	$( "#reorderable li" ).each(function( index ) {
        		for (i=0; i < removedColNameArr.length; i++) {
        			//console.log( index + ": " + $( this ).text() );
        			if( $.inArray($( this ).find('input').val(), removedColNameArr) !== -1 ) {
        				$(this).find("input[type='checkbox']").prop('checked', false);
        			} else {
        				$(this).find("input[type='checkbox']").prop('checked', true);
        			}
        		 }
        	});
        	
        	
        },
        error: handleAjaxError
    });	
}


function onChangeTypeFormulSP(){
	if($('#subProjectName').val()==""){
		$('#subProjectName').val($('#SUBPROJECTTYPE_ID option:selected').text());
	}
	else if($('#SUBPROJECTTYPE_ID').val()!=""){
	 openConfirmDialog({
			onConfirm : function (){
				$('#subProjectName').val($('#SUBPROJECTTYPE_ID option:selected').text());
			},
			title : 'Warning',
			message : getSpringMessage('Subproject name will be change to "'+$('#SUBPROJECTTYPE_ID option:selected').text()+'". Do you confirm?')
		});
	}
}
	