/**
 * return from onAjaxCall()
 * @param obj
 * @returns
 */

function authzCheckOnDonReady(newurl) {
	if(newurl.indexOf("PERMISSION_DENIED=1") > 0) {
		parent.displayAlertDialog("PERMISSION_DENIED");
		
		if(window.self !== window.top && parent.$("#prevDialog iframe").length > 0) { // if we in popup display close 
			parent.$("#prevDialog iframe").attr('src', 'about:blank');
			parent.$('#prevDialog').dialog('close');
			return;
		}
		
		//remove from URL without refresh
		if (history.pushState) {
			
			//clean PERMISSION_DENIED param from url
            if(newurl.indexOf("&PERMISSION_DENIED=1") > 0) {
            	newurl = newurl.replace("&PERMISSION_DENIED=1","");
            } else {
            	newurl = newurl.replace("PERMISSION_DENIED=1","");
            }
            
			//push new URL
            window.history.pushState({
                path: newurl
            }, '', newurl);
        }
	}
	
	if( newurl.indexOf("PERMISSION_DISABLED=1") > 0) {
		if($('#save_').length > 0) {
			 generalDisabledAuthzFunc(true);
		}
	}
	
	if(newurl.indexOf("DBError=1") > 0) {
		displayAlertDialog("DBError");
		
		//remove from URL without refresh
		if (history.pushState) {
			
			//clean PERMISSION_DENIED param from url
            if(newurl.indexOf("&DBError=1") > 0) {
            	newurl = newurl.replace("&DBError=1","");
            } else {
            	newurl = newurl.replace("DBError=1","");
            }
            
			//push new URL
            window.history.pushState({
                path: newurl
            }, '', newurl);
        }
	}
}
  	
function upDateElementAuthorizationImp(obj) { // on every ajax change
	var targetParentId, targetId, domElementType, scriptType, scriptCondition, additionalInfo;
	var elements = JSON.parse(obj.val);
    var key;
    if($('#formCode').val() == 'Request'){
    	$('[element="ElementAuthorizationImp"] input').each(function () {
    		targetId = $(this).attr('targetid');
            domElementType = $(this).attr('domElementType');
            scriptType = $(this).attr('scriptType');
            scriptCondition = $(this).attr('scriptCondition');
            additionalInfo = $(this).attr('additionalInfo'); 
            expressionInfo = $(this).attr('expressionInfo');
            
            setAuthorizationImp(targetId, domElementType, scriptType, scriptCondition, additionalInfo,expressionInfo);
     });
    }else{
      for (key in elements) {
        if (elements.hasOwnProperty(key)) {
        	 targetId = elements[key]['targetId'];
        	 domElementType = elements[key]['domElementType'];
        	 scriptType = elements[key]['scriptType'];
        	 scriptCondition = elements[key]['scriptCondition'];
        	 additionalInfo = elements[key]['additionalInfo'];
        	 expressionInfo = elements[key]['expressionInfo'];
             
             setAuthorizationImp(targetId, domElementType, scriptType, scriptCondition, additionalInfo, expressionInfo); 
        }
        } 
    }
    
    setGeneralInfiIconDisplay();
}

/**
 * init ElementAuthorizationImp
 * @returns
 */
function renderElementAuthorizationImp() { // on init form
	//permissionsAccess settings
    var perm = $('#permissionsAccess').val();
    var isNewForm = $('#isNew').val();
    console.log("permissionsAccess: " + perm);
    generalDisabledAuthzFuncFalg = false;
    if(perm != null && perm != 'undefined')
    {
		$("[perm_attr]").each(function()
		{
			var p = $(this).attr('perm_attr');
			for(var i = 0; i < p.length; i++) {
				if(isNewForm == '1' && p.charAt(i) == 'c') {
					if(perm.indexOf(p.charAt(i)) == -1) {
						$(this).addClass('authorizationDisabled');
						continue;
					}
				} else if(p.charAt(i) != 'c') {
					//TODO IS SAVE DISABLE ALL USING GENERAL DISABLE
					if(perm.indexOf(p.charAt(i)) == -1) {
							
					    if(!generalDisabledAuthzFuncFalg && p.charAt(i) == 'u' && ($(this).attr('id') == 'saveButton' || $(this).attr('id') == 'saveFloatingButton' || $(this).attr('id') == 'save_')) {
					    	generalDisabledAuthzFunc(true);
					    	generalDisabledAuthzFuncFalg = true;
					    } else {
					    	$(this).addClass('authorizationDisabled');
					    }
						continue;
					}
				}
				
			}
		});
    }
    
    //permissions Sensitivitylevel order (on tabs in general)
    var perm_sens_level_order = $('#permissionsSensLevelOrder').val();
    console.log("perm_sens_level_order: " + perm_sens_level_order);
    
    $("[sensitivitylevel_order]").each(function() {
    	var tab_sens_level_order = $(this).attr('sensitivitylevel_order');
    	if(Number(tab_sens_level_order) > Number(perm_sens_level_order)) {
    		$(this).parent().css('display', 'none');
    	} 
    });
     
    //specific settings
	var targetParentId, targetId, domElementType, scriptType, scriptCondition, additionalInfo, expressionInfo;
    $('[element="ElementAuthorizationImp"] input').each(function () {
    	
        targetId = $(this).attr('targetid');
        domElementType = $(this).attr('domElementType');
        scriptType = $(this).attr('scriptType');
        scriptCondition = $(this).attr('scriptCondition');
        additionalInfo = $(this).attr('additionalInfo'); 
        expressionInfo = $(this).attr('expressionInfo');
        
        setAuthorizationImp(targetId, domElementType, scriptType, scriptCondition, additionalInfo);
            
    });
	setGeneralInfiIconDisplay();
	
	navigationTabSelection();
    
	//yp 10032019 - fix patch for version 1.428.3 - general prod - slow analytical experiment load ->
//	setWebixTablesDisabled(false);
}

/**
 * display the info icon only if there is info to display
 * @returns
 */
function setGeneralInfiIconDisplay() {
	var inf_ = insertAuthzGenralInfo();
	try {
		if(inf_ == null || inf_ == '') {
	    	var spn_ = $('span.iconAdditCustomInfo');
	    	if(spn_.length > 0) {
	    		spn_.css('visibility', 'hidden');
	    	}
	    } else {
	    	var spn_ = $('span.iconAdditCustomInfo');
	    	if(spn_.length > 0) {
	    		spn_.css('visibility', 'visible');
	    	}
	    }
	} catch(e) {
		// do noting
	}
}
 
function setAuthorizationImp(targetId, domElementType, scriptType, scriptCondition, additinalInfo) {
	var targetParentId;
	
//	var skipScript = "targetId to skip";
//	if(targetId.indexOf(skipScript) > -1) {
//		console.log("setAuthorizationImp SKIP! on targetId=" + targetId + ", domElementType=" + domElementType + ", scriptType=" + scriptType + ", scriptCondition=" + scriptCondition + ", additinalInfo=" + additinalInfo);
//		return;
//	} else {
////		console.log("setAuthorizationImp on targetId=" + targetId + ", domElementType=" + domElementType + ", scriptType=" + scriptType + ", scriptCondition=" + scriptCondition + ", additinalInfo=" + "");
//	}
  
	if(domElementType == 'ID') {
		//function 
		if($.isFunction(window[targetId])) {
			 window[targetId](scriptCondition, additinalInfo);
		} else {
			// tab or domid...
			//init when id represent tab
			var isTabpanel = false 
			if ($('#' + targetId).attr('role') == "tabpanel") {
				isTabpanel = true;
				targetId = $('[aria-controls=' + targetId + ']').attr('aria-labelledby');
	        }
			//DISABLE
			if(scriptType == 'DISABLE') {
				setDisabledByElementId(targetId, (scriptCondition == '1'));
			} 
			//HIDDEN
			else if (scriptType == 'HIDDEN') {
				
				//handle chosen selection  (add _chosen to id) 
				var selectAddition = "";
				if($('[id="' + targetId + '"]').is("select") && $('[id="' + targetId + '_chosen"]').length > 0) {
					selectAddition = "_chosen";
				} 
				
				if (scriptCondition == '1') {
			    	if(isTabpanel){
			    		$('[id="' + targetId + selectAddition + '"]').parent().css('display', 'none');
			    	} else {
			    		$('[id="' + targetId + selectAddition + '"]').css('display', 'none');
			    	}        
			    } else if (scriptCondition == '0') {       
			        if(isTabpanel){
			    		$('[id="' + targetId + selectAddition + '"]').parent().css('visibility', 'visible');
			    	} else{
			    		 $('[id="' + targetId + selectAddition + '"]').css('visibility', 'visible');
			    		 //TODO check if  $('[id="' + targetId + selectAddition + '"]').css('display', 'inline'); is better (I fix it just for a bug found in request (new was above other buttons)
			    		 try {
			    			 if($('#formCode').val() == 'Request' && targetId == 'newButton') {
				    			 $('[id="' + targetId + selectAddition + '"]').css('display', 'inline');
				    		 } else {
				    			 $('[id="' + targetId + selectAddition + '"]').css('display', 'block');
				    		 }
			    		 } catch(e) {
			    			 $('[id="' + targetId + selectAddition + '"]').css('display', 'block');
			    		 }
			    	}  
			    }
			}
			//MANDATORY
			else if (scriptType == 'MANDATORY') { 
				if (scriptCondition == '1') {
			    		$('[id="' + targetId + '"]').attr('required',true);
			    		  if ($('label[for="' + targetId + '"]').length > 0) {
			    			  $('label[for="' + targetId + '"]').css('visibility', 'visible');
				            }
			    } else if (scriptCondition == '0') {       

			    		 $('[id="' + targetId + '"]').attr('required',false);
			    		 if ($('label[for="' + targetId + '"]').length > 0) {
				                $('label[for="' + targetId + '"]').css('visibility', 'hidden');
				            }
			    }
			}
		}
	} 
}

function insertAuthzGenralInfo() {
	var authzInfo = "";
	 $('[element="ElementAuthorizationImp"] input').each(function () {
	    	 
	        scriptCondition = $(this).attr('scriptCondition');
	        additionalInfo = $(this).attr('additionalInfo');
	        displayCommentsInInfo = $(this).attr('displayCommentsInInfo');
			if(additionalInfo != "NA" && displayCommentsInInfo == "true"  && scriptCondition == "1"){
				authzInfo = authzInfo  + getSpringMessage(additionalInfo) + "<br />";
			}
	            
	    });
	 return authzInfo;
}

function navigationTabSelection() {
	try {
		if ($('#formTab').length > 0 && $('#formTab').val() != "") {
			var tabVal = $('#formTab').val();
			var $hrefTab = $('a[href="#' + tabVal + 'Tab"]');
			var makeClick = true;

			// ignore tab navigation in case the tab is hidden or disabled
			if ($hrefTab.is('.authorizationDisabled') || $hrefTab.is(':disabled')
					|| $hrefTab.is(":hidden")) {
				makeClick = false;
			}
			
			//remove it from the url
			var newurl = url();
			if (newurl.indexOf("&formTab=" + tabVal) > 0) {
				if (history.pushState) {
					// clean PERMISSION_DENIED param from url
					newurl = newurl.replace("&formTab=" + tabVal, "").replace("&cube=","&no_cube=");

					// push new URL
					window.history.pushState({
						path : newurl
					}, '', newurl);
				}
			}
			
			if(makeClick) {
				$hrefTab.click();
			}
		}
	} catch(e) {
		alert(1);
	}
	
}
 