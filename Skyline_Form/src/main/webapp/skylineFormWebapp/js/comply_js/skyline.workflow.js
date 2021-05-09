
var _objectsPool = {};
var _fullStepsConfigObj = {};
var _fullStepActionsConfigObj = {};
var _currStepID = 0;
var _dataForLoadWF = "";
var _ddlObjID = "ddlRunWFStatus";
var _permissionsArr = [];

//Define showModalDialog for none IE browsers
showModalDialogForChrome();

function _getWFConfig(arr)
{
	var params = [];
	params[0] = _objectsPool[arr[0]];
	params[1] = arr[1];
	params[2] = arr[2];
	_dataForLoadWF = JSON.stringify(params);
	
    $.ajax 
    ({ 
         type: "POST",
         contentType: "application/x-www-form-urlencoded; charset=utf-8",
         url: "workflowmanageservlet",
         data: "actionid=getWFConfig&wfParams=" + _dataForLoadWF,
         dataType: "json",
         success: function(data)
         {
        	if(data._wf_steps != null)
        	{
        		_fullStepsConfigObj = data._wf_steps;
        	}
        	if(data._wf_steps_actions != null)
        	{
        		_fullStepActionsConfigObj = data._wf_steps_actions;
        	}	
        	if(data._wf_curr_step_id != null)
        	{
        		_currStepID = data._wf_curr_step_id;
        	}
        	
        	//alert(JSON.stringify(_fullStepsConfigObj));
         },
         async: false
    });
    _permissionsArr = $.parseJSON(getRespGrByUser()); // from CommonDBFuncs to WF
}

function _setStepActions()
{
	var stepActionsConfigObj = _fullStepActionsConfigObj[_currStepID]; //actions
	var stepConfigObj = _fullStepsConfigObj[_currStepID]; //current step
	
	$('#'+_ddlObjID).append($('<option can_edit='+stepConfigObj.is_can_edit+'>').val(_currStepID).text(stepConfigObj.step_name));
	//alert(JSON.stringify(stepActionsConfigObj));
	
	for (var key in stepActionsConfigObj) 
	{
		  if (stepActionsConfigObj.hasOwnProperty(key)) 
		  {
			  
			  var prop = stepActionsConfigObj[key];
			  var permArr = (prop.permissions_list).split(',');
			  var is_shown = true;
			  for(var i=0;i<permArr.length;i++)
			  {
				  is_shown = ($.inArray(permArr[i], _permissionsArr) == -1)?false:true;
				  if(is_shown)
					  break;
			  }
			  
			  //alert(JSON.stringify(prop));
			  if(is_shown)
			  {
				  $('#'+_ddlObjID).append($('<option is_esign='+prop.is_esign_step+' is_comment_req='+prop.is_comment_required+'>').val(key).text(prop.next_step_name));
			  }
		  }
	}	
}

function _getSettingsByStatusID(id)
{
	var returnObj = {};
	var stepConfigObj = _fullStepActionsConfigObj[_currStepID][id];
	if(stepConfigObj != null)
	{
		//alert(JSON.stringify(stepConfigObj));
		//alert(_permissionsArr);
		var permArr = (stepConfigObj.permissions_list).split(',');
		var is_available = true;
		for(var i=0;i<permArr.length;i++)
		{
			is_available = ($.inArray(permArr[i], _permissionsArr) == -1)?false:true;
			if(is_available)
			{			
				break;
			}
		}
		returnObj = {'action_is_available':(is_available)?1:0, 'esign_required':stepConfigObj.is_esign_step, 'comment_required':stepConfigObj.is_comment_required};
	}
	return returnObj;
}

function _updateWFStatus(mode)
{
	var currActions = "";//JSON.stringify(_stepActionConfigObj[currStepId]);
	var selObj = $('#'+_ddlObjID+' option:selected');
	
	var isCommentReq = selObj.attr('is_comment_req'); 
	var isEsignStep = selObj.attr('is_esign');
	var selStatusId = selObj.val();
	
	_showWFManager(mode, currActions, isCommentReq, isEsignStep, selStatusId);
}

/*function _autoUpdateWFStatus(arr, isCommentReq, isEsignStep)
{
	var params = [];
	params[0] = _objectsPool[arr[0]];
	params[1] = arr[1];
	params[2] = arr[2];
	_dataForLoadWF = JSON.stringify(params);

	_showWFManager("0", "", isCommentReq, isEsignStep, "0");
}*/

function _showWFManager(mode, currActions, isCommentReq, isEsignStep, selStatusId)
{
	var winStatus, winArguments, rValue, winHeight;
    var arrArgs = new Array();
    
    
    arrArgs[0] = document.getElementById('main');
    arrArgs[1] = "workflowmanageservlet?showDDL="+mode+"&availableActionsList="+currActions+"&commentRequired=" + 
    			  isCommentReq + "&esignStep=" + isEsignStep + "&selectedStatus=" + selStatusId + "&dataForLoadWF=" + _dataForLoadWF;
    
    winHeight = 200;
    if(isEsignStep == "1")
	{
    	winHeight += 100;
	}
    
    if(mode == "1")
	{
    	winHeight += 100;
	}
    winArguments = arrArgs;
    winStatus = "dialogHeight: "+winHeight+"px; dialogWidth: 380px;edge: sunken; scroll: No; center: YES; help: No; resizable: No; status: No;unadorned: Yes;";                         
    rValue = window.showModalDialog("WinModalSubmitWithScrollIE.html"   , winArguments, winStatus, {onClose:onFinishShowModalDialog});     
    if(isOriginShowModalDialog())
    {           
   		onFinishShowModalDialog(rValue);
    } 
} 

function onFinishShowModalDialog(rValue)
{
	if(rValue != null && rValue != "-1")
	{
		
		_onSuccess();
	}
	
}

function _onSuccess()
{
	
}

_objectsPool = {'wfrunmanagement':'RUN'};


