//**************************************************
// ************* trait / default values functions
//**************************************************

setDefaultValueForUnitTest_ = function(trait, input) {
	trait = this[trait];
	var o = Object.create(Object.prototype, Trait(trait));
	o.setDefaultValueForUnitTest_(input);
}

function setDefaultValueForUnitTest() {
	var formId = $('#formId').val();
	// $('[formElement=1]').each(function () {,:hidden
	$(
			'[formElement=1]:not(.disablePage,.authorizationDisabled,.disabledclass,:disabled)')
			.each(
					function() {
						var elementImpCode = $(this).attr('element');
						var elementId = $(this).attr('id');
						var currDisplayValue = getDisplayValue_(elementImpCode,
								this);
						if (currDisplayValue == null
								|| currDisplayValue == ''
								|| ($(this).attr('type') == 'Checkbox' && currDisplayValue == 'No')) {
							setDefaultValueForUnitTest_(elementImpCode, this);
						}
					});
}

//*************************************************************
//********************* helper functions: *********************
//*************************************************************

function unitTestHelper_clickWhenReady(selector_) {
	if($('#' + selector_).length > 0 && !($(selector_).hasClass('disabledclass'))) {
		$('#' + selector_).click();
	} else {
		console.log("sleep and try - unitTestHelper_clickWhenReady - " + selector_);
		window.setTimeout(unitTestHelper_clickWhenReady, 300, selector_);
		return;
	}
}

function unitTestHelper_NavigationByUrl(url) {
	var page = url;// "./init.request?formCode=InvItemMaterial&formId=-1&userId=100&stateKey=99999999&tableType=&PARENT_ID=-1&smartSelectList=&refreshFlag=1";
	if (window.self !== window.top) {
		window.top.location.href = page;
		return;
	}
	fgReloadForm(page);// window.location.href = page;
}

function unitTestHelper_SetRichText(domId, val_) {
	loadRichtextContentById($('#'+domId),val_);
}

function unitTestHelper_SetChosenDDL(domId, val_) {
	$("#" + domId).val(
			$("#" + domId + " option:contains('" + val_ + "')").val());
	$("#" + domId).trigger('chosen:updated');
}

function unitTestHelper_OpenSaveAction_OpenInFrame(page, domId) {
	var dialogWidth = ($(window).width() - 10) * 0.5;
	var dialogHeight = ($(window).height());
	var $dialog = $('<div id="prevDialog" style="overflow-y: hidden;""></div>')
			.html(
					'<iframe id="formIframeId" style="border: 0px;width:100%;height:100%" src="'
							+ page /* + urlPermissionDisabled */+ '"></iframe>')
			.dialog({
				autoOpen : false,
				modal : true,
				height : dialogHeight,
				width : dialogWidth,
				// title: title,
				close : function() {
					$('#prevDialog iframe').attr('src', 'about:blank');
					$('#prevDialog').remove();
					onElementDataTableApiChange(domId);
				}
			});
	$dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
}

function unitTestHelper_OpenSaveAction_StepReactionClickCalcByIndex(targetIndex) {
	// navigate to reaction tab
	if ($('a[href="#ReactionTab"]').length > 0) {
		$('a[href="#ReactionTab"]').click();
	}

	// make calculation
	$('.fa-calculator').each(function(index) {
		if (targetIndex == index) {
			$(this).click();
		}
	});
}

function unitTestHelper_ClickOnDTRow(tableId, rowNum) {
	setTimeout(function() {
		if ($('#' + tableId + ' tbody tr:eq(' + rowNum + ')').length > 0) {
			console.log("unitTestHelper_ClickOnDTRow CLICK!");
			$('#' + tableId + ' tbody tr:eq(' + rowNum + ')').click();
		} else {
			console.log("unitTestHelper_ClickOnDTRow wait");
			window
					.setTimeout(unitTestHelper_ClickOnDTRow, 300, tableId,
							rowNum);
			return;
		}
	}, 100);
}

function unitTestHelper_selectAllDTMaterialColumns() {
	if($("#upperTable_structCatalogItem").length > 0 && $("#upperTable_criteriaCatalogItem").length > 0) {
		$("#upperTable_structCatalogItem").val('InvItemColumn');
		$("#upperTable_structCatalogItem").trigger('change');
		utSleepCall(2000);
		$("#upperTable_criteriaCatalogItem").val('ALL');
		$("#upperTable_criteriaCatalogItem").trigger('change');
		utSleepCall(2000);
	} else {
		console.log("unitTestHelper_selectAllDTMaterialColumns wait");
		window.setTimeout(unitTestHelper_selectAllDTMaterialColumns, 300);
		//return;
	}
}

function utSleepCall(sleepTimeMS) {
	setTimeout(utSleepLog,sleepTimeMS,sleepTimeMS);
}

function utSleepLog(sleepTimeMS) {
	console.log("utSleep (" + sleepTimeMS + ") call ... ");
}

//	To pass a parameter to setTimeout() callback, use the following syntax:
//
//	setTimeout(functionname, milliseconds, arg1, arg2, arg3...)
//	The following are the parameters:
//
//	functionname: The function name for the function to be executed.
//	milliseconds: The number of milliseconds.
//	arg1, arg2, arg3: These are the arguments passed to the function.
//	You can try to run the following code to pass a parameter to a setTimeout() callback
//
//	Example
//	Live Demo
//
//	<!DOCTYPE html>
//	<html>
//	   <body>
//	      <button onclick="timeFunction()">Submit</button>
//	      <script>
//	         function timeFunction() {
//	            setTimeout(function(){ alert("After 5 seconds!"); }, 5000);
//	         }
//	      </script>
//	   <p>Click the above button and wait for 5 seconds.</p>
//	   </body>
//	</html> 

