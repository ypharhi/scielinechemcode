/**
 * fix Iframe Content: set width and height to 100%.
 * @param domId
 * @returns
 */
function fixIframeContent(AsyncIframe, svg) 
{
    $('#' + AsyncIframe).contents().find('img').css({
        'width': '100%',
        'height': '100%'
    });
    setTimeout(function () {
        $('#' + svg).css('display', 'none');
        $('#' + AsyncIframe).css('display', '');
    }, 1500);
    if($('#formCode').val() != 'Iframe') {
    $('#' + AsyncIframe).contents().find('img').on('click', function(event) {/*alert('test');*/ openMarvinInNewForm(AsyncIframe);} );
    }
    
}

function fixIframeContentIE(AsyncIframe, svg)
{
	if (GetIEVersion() > 0)
	{
		fixIframeContent(AsyncIframe, svg);
	}
		
}

function GetIEVersion() 
{
	  var sAgent = window.navigator.userAgent;
	  var Idx = sAgent.indexOf("MSIE");

	  // If IE, return version number.
	  if (Idx > 0) 
	    return parseInt(sAgent.substring(Idx+ 5, sAgent.indexOf(".", Idx)));

	  // If IE 11 then look for Updated user agent string.
	  else if (!!navigator.userAgent.match(/Trident\/7\./)) 
	    return 11;

	  else
	    return 0; //It is not IE
}

/**
 * return from onAjaxCall()
 * @param obj
 * @returns
 */
function upDateElementAsyncIframe(obj) {
    if (typeof obj.isHidden !== 'undefined') {
        if (obj.isHidden.toLowerCase() == "false") {
            $('[id="' + obj.domId + '_Parent"]').css('display', '');
        } else {
            $('[id="' + obj.domId + '_Parent"]').css('display', 'none');
        }
    }
}

function openMarvinInNewForm(targetElement){
	var dialogWidth = ($(window).width() - 10);
    var dialogHeight = ($(window).height());
	var parentId = $('#formId').val();
	var fileId= $('#'+targetElement+'_FILE_ID').val();
	var page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=Iframe&formId=-1&userId=" + $('#userId').val() + '&tableType=&PARENT_ID=' + parentId+'&FILE_ID='+fileId;
	targetElement
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
	            $('#prevDialog iframe').attr('src', 'about:blank');
	            $('#prevDialog').remove();
	        }
	    });

	$dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
	
}

/** CollapsibleIframes **/
function makeCollapsibleIframes(defaultCube) {
	var coll = document.getElementsByClassName("collapsible_iframes");
	var i;
	for (i = 0; i < coll.length; i++) {
		coll[i].addEventListener("click", function(e) {
			if(e.srcElement.tagName == 'I') { //ignore icon on button clicks
				return;
			}
			this.classList.toggle("active");
			var content = this.nextElementSibling;
			if (content.style.maxHeight) {
				content.style.maxHeight = null;
			} else {
				content.style.maxHeight = content.scrollHeight + "px";
			}
		});
	}
	
	showHideCollapsExpandAll();
}

function openCollapsibleIframes() {
	setTimeout(function() {
		if($("[DEFAULT_CUBE_ATTR]").length == 1) {
			$("[DEFAULT_CUBE_ATTR]").click();
		} else {
			$('.collapsible_iframes').closest(':not(.active)').click();
		}
	}, 100);
}

function openAllIframes() {
	setTimeout(function() {
			$('.collapsible_iframes').closest(':not(.active)').click();
	}, 100);
}

function closeCollapsibleIframes() {
	setTimeout(function() {
		$('.collapsible_iframes').closest('.active').click();
	}, 100);
}

function openSingleIframeById(buttenId) {
	setTimeout(function() {
		$('#' + buttenId).click();
	}, 100);
}

/** --- **/

//var stepCube_global =  '<div id="stepIframes999_Parent" style="text-align:center;">' +
//	'<button id="div_step999" class="collapsible_iframes">STEP 01</button>' +
//'<div class="asyn_iframe_content">' +
//'<iframe id="stepIframes_AsyncIframe_1" name="stepIframes_AsyncIframe_999" src="init.request?formCode=StepMinFr&formId=276589&userId=28991&stateKey=9991606726924098" class="asyncIframe" style="overflow:hidden;width:100%;height:800px;""></iframe>' +
//'</div>' +
//'<p>&nbsp</p></div>';

function addFrameCube(domId,eventName) {
	//addFrameCube('stepIframes','createNewStepIframeData');
	
	var allData = getformDataNoCallBack(1);
	// url call
	var urlParam = "?formId=" + $('#formId').val() + "&formCode=" + $('#formCode').val()
			+ "&userId=" + $('#userId').val()
			+ "&eventAction=" + eventName + "&isNew=1" ;

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
				var objIdName = funcParseJSONData(obj.data[0].val);
				var newFormId = objIdName.ID;
				var newName = objIdName.NAME;
				if(objIdName.hasOwnProperty("warningToDisplay")){
					displayAlertDialog(objIdName.warningToDisplay);
				}
				postAddFrameCube(domId,newFormId,newName,eventName);
			}
		},
		error : handleAjaxError
	});
}

function postAddFrameCube(domId, newFormId, newName,eventName) {
	var index_ = $('.collapsible_iframes').length;
	var formId = newFormId;
	var userId = $('#userId').val();
	var titleName = newName;
	var frameState =$('#stateKey').val() + "" + index_;
	
	//clean old DEFAULT_CUBE_ATTR
	$("[DEFAULT_CUBE_ATTR]").removeAttr("DEFAULT_CUBE_ATTR");
	var deleteIcon = "<i class=\"fa fa-trash ignor_data_change\" title=\"Remove " + titleName + "\" style=\"cursor:pointer;font-size:1.5em;\" onclick=\"onClickdeleteCube('" + domId + "','" + formId + "')\"></i>";

	
	var newCube =  '<div id="parent_'+ domId+ '_' + formId + '" style="text-align:center;">' +
          			'<button id="button_'+ domId+ '_' + formId + '" DEFAULT_CUBE_ATTR=1 class="collapsible_iframes">' + deleteIcon + titleName + '</button>' +
					'<div class="asyn_iframe_content">' +
					'<iframe id="AsyncIframe_'+ domId+ '_' + formId + '" name="AsyncIframe_'+ domId+ '_' + formId + '" src="init.request?formCode=StepMinFr&formId=' + formId + '&userId=' + userId + '&stateKey=' + frameState + '" class="asyncIframe" style="overflow:hidden;width:100%;height:800px;""></iframe>' +
					'</div>' +
					'<p>&nbsp</p></div>';
	$('#'+ domId+ '_asyncPlaceHolder').before(newCube);
	
	//add listener
	var $lastButton = document.getElementById('button_'+ domId+ '_' + formId);
	$lastButton.addEventListener("click", function(e) {
		if(e.srcElement.tagName == 'I') { //ignore icon on button clicks
			return;
		}
		this.classList.toggle("active");
		var content = this.nextElementSibling;
		if (content.style.maxHeight) {
			content.style.maxHeight = null;
		} else {
			content.style.maxHeight = content.scrollHeight + "px";
		}
	});
	showHideCollapsExpandAll();
	showHideDeleteStepIcon();
	hideWaitMessage();
	closeCollapsibleIframes();
	openSingleIframeById('button_'+ domId+ '_' + formId);
	generalBL_postAddFrameCube(newFormId,eventName);
}

function showHideCollapsExpandAll() {
	 var coll = document.getElementsByClassName("collapsible_iframes");
	 if(coll.length > 0) {
		 $('.asyn_iframe_CollapseExpandButtonsDiv').css('visibility','visible');
	 } else {
		 $('.asyn_iframe_CollapseExpandButtonsDiv').css('visibility','hidden');
	 }
}


