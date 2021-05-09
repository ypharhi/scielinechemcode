/**
 * 
 * @returns form path info

 */
//////////////// buttons functions/////////////////////

function onLoadExpAnalysisReportDesignForEdit(designId) {
	
	var url = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=ReportDesignExp&formId="+designId+"&userId=" + $('#userId').val() + "&PARENT_ID=-1";
	fgReloadForm(url);
	
}

function onLoadExpAnalysisReportDesignForEditinMenu(designId) {
	
	var url = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=ReportDesignExp&formId="+designId+"&userId=" + $('#userId').val() + "&PARENT_ID=-1";
//	if(window.self !== window.top) {    
//  		window.top.location.href = page;  
//	} else{
//		window.location = page;  
//	}
	fgReloadForm(url);
	
}
function openReportDesignScreen() {
	var designIdHolder=getDesignIdOrNameFromSession("ID");
	if(designIdHolder!=""&&designIdHolder!=null)
		openReportDesignScreenForEdit(designIdHolder);
	else{
	var dialogWidth = ($(window).width() - 10);
	var dialogHeight = ($(window).height());
	var parentId = $('#formId').val();
	var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=ReportDesignExp&formId=-1&userId=" + $('#userId').val() + "&PARENT_ID=-1";
	//var html= $('#'+targetElement).contents().find('img');
	//var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=Experiment&formId=-1&userId=" + $('#userId').val() + '&tableType=&PARENT_ID=' + parentId;
	// open iframe inside dialog
	var $dialog = $('<div id="prevDialog" style="overflow-y: hidden;""></div>')
	    .html('<iframe style="border: 0px;width:100%;height:100%" src="' + page + '"></iframe>')
	    .dialog({
	        autoOpen: false,
	        modal: true,
	        height: dialogHeight,
	        width: dialogWidth,
	        //  title: title,
	        close: function () {
	        
	        	$(this).find('iframe').attr('src', 'about:blank');
	    		$(this).remove();
	    		if(	$('#formCode').val()=='ExpAnalysisReport')
	        		setReportTitle();
	        }
	    });

	$dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
	parent. $('#prevDialog').data("nameId", $('#nameId').val());
	
	
	}
}
function openReportDesignScreenForEdit(formId) {
	var dialogWidth = ($(window).width() - 10);
	var dialogHeight = ($(window).height());
	var parentId = $('#formId').val();
	
	var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=ReportDesignExp&formId="+formId+"&userId=" + $('#userId').val() + "&PARENT_ID="+parentId;
	//var html= $('#'+targetElement).contents().find('img');
	//var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=Experiment&formId=-1&userId=" + $('#userId').val() + '&tableType=&PARENT_ID=' + parentId;
	// open iframe inside dialog
	var $dialog = $('<div id="prevDialog" style="overflow-y: hidden;""></div>')
	    .html('<iframe style="border: 0px;width:100%;height:100%" src="' + page + '"></iframe>')
	    .dialog({
	        autoOpen: false,
	        modal: true,
	        height: dialogHeight,
	        width: dialogWidth,
	        //  title: title,
	        close: function () {

	        	$(this).find('iframe').attr('src', 'about:blank');
	    		$(this).remove();
	    		if(	$('#formCode').val()=='ExpAnalysisReport')
	        		setReportTitle();
	        }
	    });

	$dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
	
	
}
function onLoadExpAnalysisReportDesign() {
	var designId=getDesignIdHolder(); 
	fillDesignDataSession(designId);
	parent.$('#prevDialog').data("event","save");
	parent.$('#prevDialog').dialog('close');
	
	
}
 function validateOtherReportDesignSameNameExists(designName) {

	var stringifyToPush = {
		code : "reportName",
		val : designName,
		type : "AJAX_BEAN",
		info : 'na'
	};

	// get all data and add removeNameId
	var allData = getformDataNoCallBack(1);
	var allData = allData.concat(stringifyToPush);
	
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + "&userId=" + $('#userId').val()
			+ "&eventAction=validateOtherReportDesignSameNameExists"
			+ "&isNew=" + $('#isNew').val();

	var data_ = JSON.stringify({
		action : "getReportDesignList",
		data : allData,
		errorMsg : ""
	});

	// call...
	$
			.ajax({
				type : 'POST',
				data : data_,
				
				url : "./generalEvent.request" + urlParam + "&stateKey="
						+ $('#stateKey').val(),
				contentType : 'application/json',
				dataType : 'json',

				success : function(obj) {
					var reportName = $('#reportName').val();
					var project = $('#PROJECT_ID').val();
					var subProject = $('#SUBPROJECT_ID').val();
					parent.$('#prevDialog').data('reportName',
							reportName);
					parent.$('#prevDialog')
							.data('project', project);
					parent.$('#prevDialog').data('subProject',
							subProject);
					if (obj.errorMsg != null && obj.errorMsg != '') {
						displayAlertDialog(obj.errorMsg);
						return 0;
					} else if (obj.data[0].val != "1") {// there exsists a  design with  that name
														
						if(parent.$('#prevDialog').data('prevName')!=reportName)
							{
						openConfirmDialog({
							onConfirm : function() {
								 parent.$('#prevDialog')
									.data('isNewName', false);
							
								parent.$("#prevDialog iframe").attr('src',
								'about:blank');
						        parent.$('#prevDialog').dialog('close');
						       
								// doSaveReportDesign("RemoveOldAndInsert",reportName,project,subProject); 
								
							},
							title : 'Warning',
							message : getSpringMessage("There is already a design with the same name. Would you like to rewrite?"),
							onCancel: function (){}
					
						});
							}
						else{
							 parent.$('#prevDialog')
								.data('isNewName', false);
						
							parent.$("#prevDialog iframe").attr('src',
							'about:blank');
					        parent.$('#prevDialog').dialog('close');
						}
					} else {
						 parent.$('#prevDialog')
							.data('isNewName', true);
						parent.$("#prevDialog iframe").attr('src',
						'about:blank');
				        parent.$('#prevDialog').dialog('close');
				       
						//doSaveReportDesign("cloneDesignWithNewName",reportName,project,subProject); 
						
						
					

					}
					// $('#prevDialog iframe').attr(
					
					
					
				
					hideWaitMessage();
				},
				error : handleAjaxError
			});
}
 function openSaveReportDesignDialog(reportDesignName){
	 // update parameters field
	 if($('#formCode').val()=="ReportDesignExp"){
		 var isReturn=false;
		 var parameters="";
$("select[id*='ddl']").each(function(){
	
			if($(this).prop('required')) {
				var parameterLabel="l"+$(this).prop("id").replace("ddl","")+"Label";
				var parameterName=$('#'+parameterLabel).text();
				if(parameters!="")
				parameters=parameters+","+parameterName;
				else parameters=parameterName;
				
				isReturn=true;
			}
		 });
if(isReturn){
	displayAlertDialog(getSpringMessage('No selection was done, Parameter '+parameters +'.Please select where to display the selected parameter. '));
	return;
}
	
	
			 $('#parametersDesign').val(getParametersData());
	   
	 }
	
	 var dialogWidth = "550"// ($(window).width() - 10) * 0.7;
	 var dialogHeight = "550"// ($(window).height()) * 0.8;
	 var reportName = reportDesignName;
	 var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=SaveReportDesign&formId=-1&userId="
	 + $('#userId').val()
	 + '&tableType=&PARENT_ID=-1&PARENT_FORMCODE=' + $('#formCode').val()// &REPORT_NAME='
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
		 //on save
		if($('#prevDialog').data('isNewName')!=null&&$('#prevDialog').data('isNewName')!==''&&$('#prevDialog').data('isNewName')!=undefined){
		 var isNewName=$('#prevDialog').data('isNewName');
			var reportName=$('#prevDialog').data('reportName');
			var project=$('#prevDialog').data('project');
			var subProject=$('#prevDialog').data('subProject');
			
		           if(!isNewName)
					 {
				    	 doSaveReportDesign("RemoveOldAndInsert",reportName,project,subProject); 
				     }
				     else 
					 {
				    	 doSaveReportDesign("cloneDesignWithNewName",reportName,project,subProject);
				     }
		           if($('#formCode').val()=="ReportDesignExp") {
		              $('#reportDesignExpName').val($('#prevDialog').data('reportName'));
					  $('#project').val($('#prevDialog').data('project'));
					  $('#subProject').val($('#prevDialog').data('subProject'));
		        	   }
		           else {
		        	   setReportTitle();
		        	   }
		}
		//on close
		//else{
			$(this).find('iframe').attr('src', 'about:blank');
			$(this).remove();
			 
		// }
	 }
	 

	// 
	
	 });

	 $dialog.dialog('option', 'dialogClass', 'noTitleStuff')
	 .dialog('open');

	  }

function saveReportDesign(action){
	
	if($('#reportName').val() == null || $('#reportName').val() == '') {
		displayAlertDialog(getSpringMessage('Please select report name!'));
		return;
	}
  
	
	var designName = $('#reportName').val();
	validateOtherReportDesignSameNameExists(designName);
	/*var isNewName=parent.$('#prevDialog').data('isNewName');
	var reportName=parent.$('#prevDialog').data('reportName');
	var project=parent.$('#prevDialog').data('project');
	var subProject=parent.$('#prevDialog').data('subProject');
           if(!isNewName)
			 {
		    	 doSaveReportDesign("RemoveOldAndInsert",reportName,project,subProject); 
		     }
		     else 
			 {
		    	 doSaveReportDesign("cloneDesignWithNewName",reportName,project,subProject);
		     }
		*/
		
}
function changeColumnsStepDesign(obj) {
	var lastval = $('#STEP_ID').attr('lastvalue');
	if(lastval=="@FG_FIRST_VALUE_FLAG@")
	lastval=" 01";
	$('#lastStepValue').val(lastval);
	$('#STEP_ID').attr('lastvalue', obj.value);

	var allData = getformDataNoCallBack(1);
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + "&userId=" + $('#userId').val()
			+ "&eventAction=getStepDesign";
	var data_ = JSON.stringify({
		action : "getStepDesign",
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
			var usingSteps=	$('#usingSteps').val();
					if (checkFields()) {
						if (usingSteps.indexOf($('#lastStepValue').val()) < 0) {
							$('#usingSteps').val(
									$('#usingSteps').val() + ','
											+ $('#lastStepValue').val());
						}
					}
					else{
					var usingSteps=	$('#usingSteps').val();
					if (usingSteps.indexOf($('#lastStepValue').val()) >= 0)
						$('#usingSteps').val($('#usingSteps').val().replace($('#lastStepValue').val()+ ',',''))
					}
			// var res = JSON.parse(obj.data[0].val);
			var res = obj.data[0].val;
			if (res !== "-1") {
				
				var array = res.split('@');
				if (array[0].indexOf("stepsSimpleDesign") >= 0&&array[0].split(':')[1]!=="-1")
					var stepsSimpleDesign = array[0].split(':')[1].split(',');
				else
					var stepsSimpleDesign = "-1"
				if (array[1].indexOf("impuritiesStepDesign") >= 0&&array[1].split(':')[1]!=="-1")
					var impuritiesStepDesign = array[1].split(':')[1].split(',');
				else
					var impuritiesStepDesign = "-1";
			}
			$('input[type=checkbox]').each(function() {

				var elementId = $(this).attr('id');
				var parentClass = $(this).parent().prop('className');
				if (parentClass.toLowerCase().indexOf("step") >= 0)

					$("#" + elementId).prop("checked", false);

			});
			$('#numConcentrationImpStep').val("");
		
			if (stepsSimpleDesign !== "-1")
				for (var i = 0; i < stepsSimpleDesign.length; i++) {
					
					if(stepsSimpleDesign[i].indexOf("numConcentrationImpStep")>=0)
						$('#numConcentrationImpStep').val(stepsSimpleDesign[i].split(";")[1])
						
					else $("#" + stepsSimpleDesign[i]).prop("checked", true);
				}
			if (impuritiesStepDesign !== "-1")
				$('#stepImpuritiesIdList').val(impuritiesStepDesign);
			else $('#stepImpuritiesIdList').val("");
			reloadMaterialSelect();
		},
		error : handleAjaxError
	});
}
function doSaveReportDesign(actionType,designName,project,subProject) {
	showWaitMessage(getSpringMessage('pleaseWait'));
	 var stringifyToPush = {
				code : "actionType",
				val : actionType,
				type : "AJAX_BEAN",
				info : 'na'
			};
	 var stringifyToPushDesignName = {
				code : "designName",
				val : designName,
				type : "AJAX_BEAN",
				info : 'na'
			};
	 var stringifyToPushProject = {
				code : "designProject",
				val : project,
				type : "AJAX_BEAN",
				info : 'na'
			};
	 var stringifyToPushSubProject = {
				code : "designSubProject",
				val : subProject,
				type : "AJAX_BEAN",
				info : 'na'
			};
	 var stringifyToPushParentFormCode = {
				code : "parentFormCode",
				val : $('#formCode').val(),
				type : "AJAX_BEAN",
				info : 'na'
			};

		
	var allData = getformDataNoCallBack(1/*, parent.$('*')*/);
		var allData = allData.concat(stringifyToPush);
		var allData = allData.concat(stringifyToPushDesignName);
		var allData = allData.concat(stringifyToPushProject);
		var allData = allData.concat(stringifyToPushSubProject);
		var allData = allData.concat(stringifyToPushParentFormCode);
		
		// url call
		var urlParam = "?formId=" + $('#formId').val() + "&formCode="
				+ $('#formCode').val() + "&userId=" + $('#userId').val()
				+ "&eventAction=saveReportDesign"
				+ "&isNew=" + $('#isNew').val();

		var data_ = JSON.stringify({
			action : "saveReportDesign",
			data : allData,
			errorMsg : ""
		});

		// call...
		$
				.ajax({
					type : 'POST',
					data : data_,
					async:false,
					url : "./generalEvent.request" + urlParam + "&stateKey="
							+ $('#stateKey').val(),
					contentType : 'application/json',
					dataType : 'json',

					success : function(obj) {
						var subDesignMenu=".reportDesign-dropdown-submenu";
						 // getReportList(subDesignMenu,true);
						
						if($('#formCode').val()=="ReportDesignExp"){
						getReportList(parent.$('.reportDesign-dropdown-submenu'),true);
						
						}
						else{
							removeReportList($('.reportDesign-dropdown-submenu').find('ul'));
						}

						
						
					
						hideWaitMessage();
					},
					error : handleAjaxError
				});
	/* var stringifyToPush = {
				code : "actionType",
				val : actionType,
				type : "AJAX_BEAN",
				info : 'na'
			};
	 var stringifyToPushDesignName = {
				code : "designName",
				val : designName,
				type : "AJAX_BEAN",
				info : 'na'
			};
	 var stringifyToPushProject = {
				code : "designProject",
				val : project,
				type : "AJAX_BEAN",
				info : 'na'
			};
	 var stringifyToPushSubProject = {
				code : "designSubProject",
				val : subProject,
				type : "AJAX_BEAN",
				info : 'na'
			};
	 var stringifyToPushParentFormCode = {
				code : "parentFormCode",
				val : parent.$('#formCode'),
				type : "AJAX_BEAN",
				info : 'na'
			};

		
		var allData = getformDataNoCallBack(1/*, parent.$('*')*//*);*/
		/*var allData = allData.concat(stringifyToPush);
		var allData = allData.concat(stringifyToPushDesignName);
		var allData = allData.concat(stringifyToPushProject);
		var allData = allData.concat(stringifyToPushSubProject);
		var allData = allData.concat(stringifyToPushParentFormCode);
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
	+ $('#formCode').val() + "&userId=" + $('#userId').val()
	+ "&eventAction=saveReportDesign";
	var data_ = JSON.stringify({
	action : "saveReportDesign",
	data : allData,
	errorMsg : ""
	});
	// call...
	$.ajax({
	contentType : 'application/json',
	dataType : 'json',
	type : 'POST',
	data : data_,
	async: false,
	url : "./generalEvent.request" + urlParam + "&stateKey="
	+ $('#stateKey').val(),
	
	success : function(obj) {
		var subDesignMenu=".reportDesign-dropdown-submenu";
	   getReportList(subDesignMenu,true);
	  
	   
	
	},
	error : handleAjaxError

	});*/
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

function addStepsCheckbox(name, isUsing) {
	var container = $('#cblist');
	var inputs = container.find('input');
	var id = inputs.length;
	var val = name;

	if (id == '0') {
		$('<input />', {
			type : 'checkbox',
			id : 'checkAll'
		}).appendTo(container).before("<br />");
		$('<label/>', {
			'for' : 'checkAll',
			'style' : "font-size: 13px",
			text : 'All'
		}).appendTo(container).before("&nbsp;&nbsp;&nbsp;").after("<br />");
		id = '1';
	}
	if (val !== 'ALL' && val !== "") {
		$('<input />', {
			type : 'checkbox',
			id : 'cb' + id,
			value : val
		}).appendTo(container).before("<br />");
		if (!isUsing)
			$('<label/>', {
				'for' : 'cb' + id,
				'style' : "font-size: 13px",
				text : "step " + name/* ,type:"Label" */
			}).appendTo(container).before("&nbsp;&nbsp;&nbsp;");
		else
			$('<label/>', {
				'for' : 'cb' + id,
				'style' : "font-size: 13px;color:red;",
				text : "step " + name/*,type:"Label"*/
			}).appendTo(container).before("&nbsp;&nbsp;&nbsp;");
	}

}



function copyStepDesign() {
	var arr = [];
	var usingSteps = [];
	//fill array with steps numbers from drop down
	parent.$('#STEP_ID option').each(function() {
		arr.push($(this).val());
	});
	//fill using steps field with step numbers checked in copy dialog
	for (var i = 1; i < arr.length + 1; i++) {
		if ($('#cb' + i).prop('checked')) {
			usingSteps.push($('#cb' + i).val())
		}
	}
	parent.$('#usingSteps').val(usingSteps);
	var allData = getformDataNoCallBack(1, parent.$('*'));

	var urlParam = "?formId=" + $('#formId').val()
	// + "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val()
	+ "&eventAction=copyDesignStep&isNew="; // + $('#isNew').val();
	var data_ = JSON.stringify({
		action : "copyDesignStep",
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

		},
		error : handleAjaxError
	});

	parent.$('#prevDialog').dialog('close');

}

function openCopyStepDesignDialog()
 {

	try {
		var formCode = 'CopyDesignStep';
		var dialogHeight = 620;
		var dialogWidth = 300;
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

					close : function() {

						$('#prevDialog iframe').attr('src', 'about:blank');
						$('#prevDialog').remove();
					}
				});

		$dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');

	} catch (e) {
		console.log('openCopyStepDesignDialog error');
	}
}

function copyStepsDesignToAll() {
	openConfirmDialog({
		onConfirm : function() {
			//fill using steps field with all steps from drop down
			var usingSteps = [];
			$('#STEP_ID option').each(function() {
				usingSteps.push($(this).val());
			});

			$('#usingSteps').val(usingSteps);

			var allData = getformDataNoCallBack(1);
			var urlParam = "?formId=" + $('#formId').val()
			// + "&formCode=" + $('#formCode').val() + "&userId=" +
			// $('#userId').val()
			+ "&eventAction=copyDesignStep&isNew="; // + $('#isNew').val();
			var data_ = JSON.stringify({
				action : "copyDesignStep",
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

				},
				error : handleAjaxError
			});
		},
		title : 'Warning',
		message : getSpringMessage('confirmCloneExperiment'),
		onCancel : function() {
		}
	});

}





/**
 * reload material (impurities) table by stepImpuritiesIdList and chkImpuritiesStep state (if chkImpuritiesStep checked the table will be empty and stepImpuritiesIdList will be cleaned from last selection)
 * @returns
 */
function reloadMaterialSelect() {
	var formId_ = $('#formId').val();
	if($('#chkImpuritiesStep').is(':checked')) {
		$('#stepImpuritiesIdList').val('');
	}
	
	var stepImpuritiesIdList_ = $('#stepImpuritiesIdList').val();
	
	
	loadMaterialSelectCore(formId_, stepImpuritiesIdList_, false);
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

function resetImpuritiesDesignFields(obj,fromId)
 {
	if (obj.id == "numConcentrationImpStep") {
		$('#chkImpuritiesStep').prop("checked", false);
		$('#stepImpuritiesIdList').val('');
		reloadMaterialSelect();

	}
	if (obj.id == "chkImpuritiesStep"
			&& $('#numConcentrationImpStep').val() != '') {
		$('#numConcentrationImpStep').val('');
	}

}
//check if all stepsDesign fields are empty
function checkFields() {
	var stepsElements = $(":input"), stepsElements = stepsElements
			.filter(function() {
				if ($(this).attr('id') != undefined)
					return $(this).attr('id').toLowerCase().indexOf("step") >= 0;
				else
					var i = 1;
			}),
	checks = $("input[id*='Step'][type='checkbox']"), checks1 = $("input[id*='step'][type='checkbox']"),

	inputs = stepsElements
			.not(checks)
			.not(checks1)
			.not(
					'[id="usingSteps"],[id="impuritiesStepsDesign"],[id="lastStepValue"]')
			.not('select').not('textarea').not('button'), checked = checks
			.filter(':checked'), checked1 = checks1.filter(':checked'), filled = inputs
			.filter(function() {
				return $.trim($(this).val()).length > 0;
			});

	if (checked.length + checked1.length + filled.length === 0) {
		return false;
	}

	return true;
}
//get Design data for preview in load design screen
function displayReportDesignPrev(id) {
	var designId = id;
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + "&userId=" + $('#userId').val()
			+ "&eventAction=getReportDesignData&isNew="; // +
	// $('#isNew').val();
	var data_ = JSON.stringify({
		action : "getReportDesignData",
		data : [ {
			code : "designId",
			val : designId,
			type : "AJAX_BEAN",
			info : 'na'
		} ],
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
			var data = obj.data[0].val;
			if (data != "-1") {
				var iframe = document.createElement('iframe');

				document.getElementById("iframe_").appendChild(iframe);

				// provide height and width to it
				iframe.setAttribute("style", "height:"
						//+ $('#designTable_Parent').innerHeight()
						+ parent.$('#prevDialog').innerHeight()
						+ "px;width:98%;");
				iframe.contentWindow.document.open();
				iframe.contentWindow.document.write(data);
				iframe.id = "designiFrame";
				iframe.contentWindow.document.close();
			}

		},
		error : handleAjaxError
	});

}

/////////// initialization //////////////////////
//init design form data from session
function initDesignDataSession() {	
	var allData = getformDataNoCallBack(1);
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + "&userId=" + $('#userId').val()
			+ "&eventAction=getDesignData";
	var data_ = JSON.stringify({
		action : "getDesignData",
		data : allData,
		errorMsg : ""
	});
	// call...
	$
			.ajax({
				type : 'POST',
				data : data_,
				//async : false,
				url : "./generalEvent.request" + urlParam + "&stateKey="
						+ $('#stateKey').val(),
				contentType : 'application/json',
				dataType : 'json',
				success : function(obj) {
					
					var res = obj.data[0].val;
					if (res !== "-1") {
						var array = res.split('@');
						//get steps data
						if (array[0].indexOf("stepsSimpleDesign") >= 0&& array[0].split(':')[1] !== "-1"){
							var stepsSimpleDesign = array[0].split(':')[1].split(',');
						}
						else{
							var stepsSimpleDesign = "-1"
						}
								
						//get impurities data
						if (array[1].indexOf("impuritiesStepDesign") >= 0&& array[1].split(':')[1] !== "-1"){
							var impuritiesStepDesign = array[1].split(':')[1].split(',');
						}
						else{
							var impuritiesStepDesign = "-1";
						}
							

						//get experiment Data
						if (array[2].indexOf("experimentData") >= 0&& array[2].split(':')[1] !== "-1"){
							var experimentData = array[2].split(':')[1].split(',');
						}
							
						else{
							var experimentData = "-1";
						}
							
                        //get ParametersData
						if (array[3].indexOf("parametersData") >= 0&& array[3].split(';')[1] !== "-1"){
							var parametersData = array[3].split(';')[1];

						}
						else{
							var parametersData = "-1";

						}
						
						//get additinalinfo data
						if (array[4].indexOf("additionalInfo") >= 0&& array[4].split(';')[1] !== "-1"){
							var additionalInfo = JSON.parse(array[4].split(';')[1]);

						}
						else{
							var additionalInfo = "-1";

						}

					}
					
					//reset all checkbox
					$('input[type=checkbox]').each(function() {
						var elementId = $(this).attr('id');

						$("#" + elementId).prop("checked", false);

					});
					//reset numConcentrationImpStep field
					$('#numConcentrationImpStep').val("");

					
					//fill fields in form
					if (stepsSimpleDesign !== "-1")
						{
						  for (var i = 0; i < stepsSimpleDesign.length; i++) {
							if (stepsSimpleDesign[i].indexOf("numConcentrationImpStep") >= 0){
								$('#numConcentrationImpStep').val(stepsSimpleDesign[i].split(";")[1])
							}
                            else{
                    	        $("#" + stepsSimpleDesign[i]).prop("checked",true);
                            }
								
						 }
						}
					if (experimentData !== "-1"){
						for (var i = 0; i < experimentData.length; i++) {
							$('input[type=checkbox]').each(function() {
								var elementId = $(this).attr('id');
								if (elementId.toUpperCase() == experimentData[i].toUpperCase())
									$("#" + elementId).prop("checked", true);
								});
						}
					}
					if (impuritiesStepDesign !== "-1")
						$('#stepImpuritiesIdList').val(impuritiesStepDesign);
					else
						$('#stepImpuritiesIdList').val("");
					
					if (parametersData !== "-1")
						$('#parametersDesign').val(parametersData);
					else
						$('#parametersDesign').val("");
					
					
					if (additionalInfo !== "-1") {
						for ( var key in additionalInfo) {
							$('input').each(function() {
                                      var elementId = $(this).attr('id');
										if (elementId != undefined)
											if (elementId.toUpperCase() == key.toUpperCase())
												$('#' + elementId).val(additionalInfo[key]);
									});

						}

						$('#STEP_ID').val(additionalInfo.STEP_ID);
						$('#STEP_ID').attr('lastvalue',additionalInfo.STEP_ID);
						$('#STEP_ID').trigger('chosen:updated');
						
					}

					//reload material table
					reloadMaterialSelect();
					//reload parameters data
					setParametersData();
					
					//set parameters inputs events
				/*	$("input[id*='input']").change(function() {
						var ddlId="ddl"+$(this).attr("id").replace("input","");
					if ($(this).is(":checked")) {
						
					
						$('#'+ddlId).prop('required',true);
					}
					else{
						$('#'+ddlId).prop('required',false);
						$('#'+ddlId+' option:selected').removeAttr("selected");
					}
						
					      });
				
*/
				
					//doing all initiasion form 
					//$('.popupSaveDefinitionBtn').css('display', 'none');
                    
					$('#lastStepValue').val($('#STEP_ID option:selected').text().replace(" ",""));
					// replace the change event of STEP_ID
					$('#STEP_ID').off('change'); // without if we will have
					// onChangeAjax('TYPE_ID'); anyway
					$('#STEP_ID').attr("onchange","changeColumnsStepDesign(this);");
					$('#chkImpuritiesStep').attr("onchange","resetImpuritiesDesignFields(this,'ReportDesignExp')");
					$('#numConcentrationImpStep').attr("onchange","resetImpuritiesDesignFields(this,'ReportDesignExp')");
					$("#save_").attr("onclick", "openSaveReportDesignDialog();");
					
					// checked unchecked all in spesific section when click all checkbox
					$("input[id*='all']").change(function() {
						var className = $(this).parent().prop('className');
							if ($(this).is(":checked")) {
								$('input[type=checkbox]').each(function() {
									if ($(this).parent().prop('className') === className)
										$(this).prop("checked",true);
							      });

							} else {
								   $('input[type=checkbox]').each(function() {
									  if ($(this).parent().prop('className') === className)
										  $(this).prop("checked",false);
							       });
							       }

				    });
					$("input[id*='All']").change(function() {
						var className = $(this).parent().prop('className');
							if ($(this).is(":checked")) {
								$('input[type=checkbox]').each(function() {
									if ($(this).parent().prop('className') === className)
										$(this).prop("checked",true);
							      });

							} else {
								   $('input[type=checkbox]').each(function() {
									  if ($(this).parent().prop('className') === className)
										  $(this).prop("checked",false);
							       });
							       }

				    });
                   var designId=getDesignIdOrNameFromSession("ID");
					//add the design name to the title if exists
					setTimeout(function() {
						if(designId!="-1"){
						var reportDesignExpName_ = $('#reportDesignExpName')
								.val();
						if (reportDesignExpName_ !== 'undefined'&& reportDesignExpName_ != null&& reportDesignExpName_ != ''&& reportDesignExpName_ != '') {
							$('#pageTitle').html(
									'Report Design: ' + reportDesignExpName_);
						}
						}
						else {
							var reportDesignExpName_ = $('#reportDesignExpName')
							.val();
					   if (reportDesignExpName_ !== 'undefined'&& reportDesignExpName_ != null&& reportDesignExpName_ != ''&& reportDesignExpName_ != '') {
						$('#pageTitle').html(
								'Report Design: ' + reportDesignExpName_+"-modified");
					   }
					   else
							$('#pageTitle').html(
									'Report Design: Temporary Design');
						}
					}, 100);

					//override chkImpuritiesStep onclick - call reloadMaterialSelect
					var chkImpuritiesStep = $('#chkImpuritiesStep');
					chkImpuritiesStep.removeAttr('onclick');
					chkImpuritiesStep.attr("onclick", "reloadMaterialSelect()");
				},
				error : handleAjaxError
			});}

function initCopyDesignStep(){
	var arr = [];
	var usingArr = parent.$('#usingSteps').val().split(',');
	parent.$('#STEP_ID option').each(function() {
		arr.push($(this).val());
	});
	// var arr=["01","02","03","04"];

	$('#stepsArray').val(arr);
	$('#save_').html('OK');
	$('#save_').attr('onclick', 'copyStepDesign();');

	var isUsing = false;
	for (var j = 0; j < arr.length; j++) {

		if (jQuery.inArray(arr[j], usingArr) !== -1)
			addStepsCheckbox(arr[j], true);
		else
			addStepsCheckbox(arr[j], false);
	}
	$('#checkAll').change(function() {
		if ($('#checkAll').is(":checked")) {
			for (j = 1; j < arr.length + 1; j++) {
				if ($('#cb' + j) != undefined) {
					$('#cb' + j).prop("checked", true);
				}
			}
		} else {
			for (j = 1; j < arr.length + 1; j++) {
				if ($('#cb' + j) != undefined) {
					$('#cb' + j).prop("checked", false);
				}
			}
		}
	});
}

function initSaveReportDesign(){
	$('.mainSaveFormAndDefinitionBtn').css('display', 'none');
	$('.mainSaveDefinitionBtn').css('display', 'none');
	$('.popupSaveDefinitionBtn').css('display', 'none');
	// $('#saveButton').css('width', '100px');
	// $('#closeButton').css('width', '100px');
	$('#reportNameList').attr("onchange", "doChosenChange();");
	
	if(parent.$('#formCode').val()=="ReportDesignExp"){
		var designName=parent.$('#reportDesignExpName').val();
		var project=parent.$('#project').val();
		var subProject=parent.$('#subProject').val();
	}
	else
		{
		var designName=getDesignIdOrNameFromSession("NAME");
		var project_subProject=getProjectAndSubProjectFromSession();
		var project=project_subProject.split('@')[0];
		var subProject=project_subProject.split('@')[1];
		}
	

	parent.$('#prevDialog').data('prevName',designName);
	if(designName!=null&&designName!=""){
	$('#reportNameList').val(designName);
	$('#reportNameList').trigger('chosen:updated');
	$('#reportName').val(designName);
	$('#PROJECT_ID').val(project);
	$('#PROJECT_ID').trigger('chosen:updated');
	$('#SUBPROJECT_ID').val(subProject);
	$('#SUBPROJECT_ID').trigger('chosen:updated');
	}
	$('#save_').removeAttr('onclick');
	$("#save_").attr("onclick", "saveReportDesign();");
	
	//$('#close_back').removeAttr('onclick');
	//$("#close_back").attr("onclick","$('#prevDialog').dialog('close')");
	
	// $('#saveAsButon').css('width', '100px');
	setTimeout(function() {
		$('#pageTitle').css('font-size', '18px');
		$('#pageTitle').parent().css('width', '100%');
	}, 100);
}

function useDesign(){
	 var isReturn=false;
	 var parameters="";
$("select[id*='ddl']").each(function(){

		if($(this).prop('required')) {
			var parameterLabel="l"+$(this).prop("id").replace("ddl","")+"Label";
			var parameterName=$('#'+parameterLabel).text();
			if(parameters!="")
			parameters=parameters+","+parameterName;
			else parameters=parameterName;
			
			isReturn=true;
		}
	 });
if(isReturn){
displayAlertDialog(getSpringMessage('No selection was done, Parameter '+parameters +'.Please select where to display the selected parameter. '));
return;
}
	 $('#parametersDesign').val(getParametersData());
	var allData = getformDataNoCallBack(1);
  var urlParam = "?formId=" +$('#formId').val()
//  + "&formCode=" 	+ $('#formCode').val() + "&userId=" + $('#userId').val()
	+ "&eventAction=useDesign&isNew="; // + $('#isNew').val();
  var data_ = JSON.stringify({
      action: "useDesign",
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
   	   //navigate to scheme form wuth selected designor tempdesign
    	  updateSpesificFieldInDesignSession("FORMID","-1");
      	 var reportDesignExpName="";
      	 if($('#reportDesignExpName').val()=="")
      		 var reportDesignExpName=undefined;
      	 else reportDesignExpName=$('#reportDesignExpName').val();
      	 if(parent.$('#formCode').val()!="ExpAnalysisReport"){
      	var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=ExpAnalysisReport&formId=-1" + "&userId=" + $('#userId').val() + '&PARENT_ID=' + $('#formId').val() + "&nameId=" +  parent.$('#nameId').val()+"&ReportDesignExpName=" +reportDesignExpName+"&isTemp=True";
        parent.window.location = page;
      	 }

			parent.$("#prevDialog iframe").attr('src',
			'about:blank');
	        parent.$('#prevDialog').dialog('close');
      	
      },
      error: handleAjaxError
  });

}
function fillDesignDataSession(designId)
{
	var urlParam = "?formId=" +$('#formId').val()
	+ "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val()
	+ "&eventAction=loadDesignDataToSession&isNew="; // + $('#isNew').val();
	var data_ = JSON.stringify({
		action : "loadDesignDataToSession",
		data : [{
				code : "designId",
				val : designId,
				type : "AJAX_BEAN",
		     	info : 'na'
			}],
		errorMsg : ""
		});
	$.ajax({
	    type: 'POST',
	    data: data_,
	    async: false,
	    url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
	    contentType: 'application/json',
	    dataType: 'json',
	    success: function (obj)
	    {
	    var data=obj.data[0].val;
	   
	   
	    },
	    error: handleAjaxError
	});

}
function clearDesign()
{
	var designId =$('#formId').val();
	var urlParam = "?formId=" +$('#formId').val()
	+ "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val()
	+ "&eventAction=clearDesign&isNew="; // + $('#isNew').val();
	var data_ = JSON.stringify({
		action : "clearDesign",
		data : [{
				code : "designId",
				val : designId,
				type : "AJAX_BEAN",
		     	info : 'na'
			}],
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
	    	//if(designId=="${reportDesignExpName}")
	    		//designId="-1";
	    	var url = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=ReportDesignExp&formId=-1&userId=" + $('#userId').val() + "&PARENT_ID=-1";
//	    	if(window.self !== window.top) {    
//	      		window.top.location.href = page;  
//	    	} else{
//	    		window.location = page;  
//	    	}
	    
	    	fgReloadForm(url);
	    
	   
	    },
	    error: handleAjaxError
	});

}
function saveCurrentDesign()
{
	
	openSaveReportDesignDialog(getDesignIdOrNameFromSession("NAME"));
	
	
}
function getDesignIdOrNameFromSession(designHolder)
{
	
	var data="";
	var urlParam = "?formId=" +$('#formId').val()
	+ "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val()
	+ "&eventAction=getDesignIdOrNameFromSession&isNew="; // + $('#isNew').val();
	var data_ = JSON.stringify({
		action : "designHolder",
		data : [{
				code : "designHolder",
				val : designHolder,
				type : "AJAX_BEAN",
		     	info : 'na'
			}],
		errorMsg : ""
		});
	$.ajax({
	    type: 'POST',
	    data: data_,
	    async: false,
	    url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
	    contentType: 'application/json',
	    dataType: 'json',
	    success: function (obj)
	    {
	    data=obj.data[0].val;
	    },
	    error: handleAjaxError
	});
	return data;
	
	
}
function getDesignIdHolder()
{
	var rowId="";
	var selectedTable = $('#designTable').DataTable();
    var custid = selectedTable.row('.selected').data();
    if(custid != undefined ){
   	rowId = custid[0];
}
    return rowId;

}

function initReportDesignSearch() {
	$("#save_").text("Select");
	$("#save_").removeAttr('onclick');
	var oldDesignId=getDesignIdOrNameFromSession("ID");
	parent.$("#prevDialog").data("oldDesignId", oldDesignId);
	$("#save_").attr("onclick", "onLoadExpAnalysisReportDesign();");

}


function openLoadDesignPopup(source) {
	    var page, $dialog, dialogWidth, dialogHeight, parentId;

	dialogWidth = $(window).width() - 8;
	dialogHeight = $(window).height() - 10;
	parentId = $('#formId').val();

	page = "./init.request?stateKey=" + $('#stateKey').val()
			+ "&formCode=ReportDesignSearch&formId=-1&userId="
			+ $('#userId').val() + '&TABLETYPE=&PARENT_ID=' + parentId;

	var urlPermissionDisabled = '&PERMISSION_DISABLED='
			+ $('#generalDisabledFlagParam').val();
	$dialog = $('<div id="prevDialog" style="overflow-y: hidden;""></div>')
			.html(
					'<iframe style="border: 0px;width:100%;height:100%" src="'
							+ page + urlPermissionDisabled + '"></iframe>')
			.dialog(
					{
						autoOpen : false,
						modal : true,
						height : dialogHeight,
						width : dialogWidth,
						// title: 'Search',
						close : function() {
							// do nothing
							var designId=getDesignIdOrNameFromSession("ID");
                        if(	$('#prevDialog').data('event')=="save"){
							if ($('#prevDialog').data('oldDesignId') == "-1") {
								openConfirmDialog({
									onConfirm : function() {
										if ($('#formCode').val() == "ExpAnalysisReport") {
											var designName = getDesignIdOrNameFromSession("NAME");

											if (designName != null
													&& designName != "") {
												if ($('#reportName').val() == null
														|| $('#reportName')
																.val() == "") {
													$('#pageTitle')
															.html(
																	"<p><b> Experiment Analysis Report</b></p><p style=\"color:Grey\"><h4>Design Name:"
																			+ designName
																			+ "</h4></p>");
												} else {
													$('#pageTitle')
															.html(
																	"<p><b>Scheme Name: "
																			+ $(
																					'#reportName')
																					.val()
																			+ "</b></p><p style=\"color:Grey\"><h4>Design Name: "
																			+ designName
																			+ "</h4></p>");
												}

											}

											
										}
										if ($('#formCode').val() == "ReportDesignExp"
											|| $('#prevDialog').data(
													"source") == "public") {
										onLoadExpAnalysisReportDesignForEdit(getDesignIdOrNameFromSession("ID"));
									}

									},
									title : 'Warning',
									message : getSpringMessage("Please note that the temporary design will be lost. Do you want to continue? "),
									onCancel : function() {
										 $(this).find('iframe').attr('src', 'about:blank');
											$(this).remove();
										// $('#designNameHolder').val("");
										// $('#designIdHolder').val("-1");
									}
								});

							} else {
								if ($('#formCode').val() == "ExpAnalysisReport") {
									var designName = getDesignIdOrNameFromSession("NAME");
									parent.$('#prevDialog').dialog('close');
									setReportTitle();
									/*
									if (designName != null && designName != "") {
										if ($('#reportName').val() == null
												|| $('#reportName').val() == "") {
											$('#pageTitle')
													.html(
															"<p><b> Experiment Analysis Report</b></p><p style=\"color:Grey\"><h4>Design Name:"
																	+ designName
																	+ "</h4></p>");
										} else {
											$('#pageTitle')
													.html(
															"<p><b>Scheme Name: "
																	+ $(
																			'#reportName')
																			.val()
																	+ "</b></p><p style=\"color:Grey\"><h4>Design Name: "
																	+ designName
																	+ "</h4></p>");
										}

									}
								
								*/
									
								}
								if ($('#formCode').val() == "ReportDesignExp"
										|| $('#prevDialog').data("source") == "public") {
									onLoadExpAnalysisReportDesignForEdit(getDesignIdOrNameFromSession("ID"));
								}
							}
							
							
						}
                        $(this).find('iframe').attr('src', 'about:blank');
						$(this).remove();
					}});

	$dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
	parent.$('#prevDialog').data("source", source);
    
}
function updateSpesificFieldInDesignSession(field,value)
{
	var data="";
	var urlParam = "?formId=" +$('#formId').val()
	+ "&formCode=" + $('#formCode').val() + "&userId=" + $('#userId').val()
	+ "&eventAction=updateFieldInSession&isNew="; // + $('#isNew').val();
	var data_ = JSON.stringify({
		action : "updateFieldInSession",
		data : [{
				code : "field",
				val : field,
				type : "AJAX_BEAN",
		     	info : 'na'
			},{
				code : "value",
				val : value,
				type : "AJAX_BEAN",
		     	info : 'na'
			}],
		errorMsg : ""
		});
	$.ajax({
	    type: 'POST',
	    data: data_,
	   async: false,
	    url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
	    contentType: 'application/json',
	    dataType: 'json',
	    success: function (obj)
	    {
	    data=obj.data[0].val;
	    },
	    error: handleAjaxError
	});
	return data;
	
}
function initDesignbuttons(){
	
	//$('.popupSaveDefinitionBtn').css('display', 'inline');
	$('.popupSaveDefinitionBtn').removeAttr('onclick');
	$('.popupSaveDefinitionBtn').attr("onclick", "clearDesign();");
	$('.popupSaveDefinitionBtn').text("clear");
}
function getProjectAndSubProjectFromSession()
{
	var data="";
	var allData = getformDataNoCallBack(1);
	  var urlParam = "?formId=" +$('#formId').val()
	//  + "&formCode=" 	+ $('#formCode').val() + "&userId=" + $('#userId').val()
		+ "&eventAction=getProjectAndSubProjectFromSession&isNew="; // + $('#isNew').val();
	  var data_ = JSON.stringify({
	      action: "getProjectAndSubProjectFromSession",
	      data: allData,
	      errorMsg: ""
	  });
	  $.ajax({
	      type: 'POST',
	      data: data_,
	      async:false,
	      url: "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
	      contentType: 'application/json',
	      dataType: 'json',
	      success: function (obj) 
	      {
	   	   
	    	  data=obj.data[0].val;
	      },
	      error: handleAjaxError
	  });
	
	
	
	
	
	return data;
}
