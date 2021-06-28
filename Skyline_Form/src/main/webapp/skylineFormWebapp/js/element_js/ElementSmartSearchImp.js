/**
 * On 'smart search' click.
 * !!Please note that the smart search form must contain an hidden element with the id toReturn.!!
 * @param domId
 * @returns
 */
function elementSmartSearchOnClick(domId) {
    var page, $dialog, dialogWidth, dialogHeight, parentId, i, elementsArray, urlCallParam, colsArray, iframeContents, toReturn, customerFunction, action, DDLelementsToOnChange=[];

    //disable onclick when input is disabled
    if ($('#' + domId).is(':disabled')) {
        return false;
    }

    dialogWidth = $(window).width() - 180;
    dialogHeight = $(window).height() - 180;
    parentId = $('#formId').val(); // maybe unnecessary
    urlCallParam = replaceSmartSearchUrlCallParamVal($('[id="' + domId + '_urlCallParam"]').val(), $('[id="' + domId + '_urlCallParamReplaceIDElement"]').val());

    page = "./init.request?stateKey=" + $('#stateKey').val() + "&formCode=" + $('[id="' + domId + '_formCodeTarget"]').val() + "&formId=-1&userId=" + $('#userId').val() + '&TABLETYPE=' + $('[id="' + domId + '_tableType"]').val() + '&PARENT_ID=' + parentId + '&PARENT_FORMCODE=' + $('#formCode').val() + '&urlCallParam=' + urlCallParam;
    customerFunction = $('[id="' + domId + '_blCustomerFunc"]').val();
    action = $('[id="' + domId + '_action"]').val();
    
    var urlPermissionDisabled = '&PERMISSION_DISABLED=' + $('#generalDisabledFlagParam').val();
    $dialog = $('<div id="prevDialog" style="overflow-y: hidden;""></div>')
        .html('<iframe style="border: 0px;width:100%;height:100%" src="' + page + urlPermissionDisabled + '"></iframe>')
        .dialog({
            autoOpen: false,
            modal: true,
            height: dialogHeight,
            width: dialogWidth,
            //title: 'Search',
            close: function () {
                iframeContents = $(this).find('iframe').contents();
                if (iframeContents.find('#save_').attr('flag')) { // detect if save button was clicked.
                    toReturn = JSON.parse(iframeContents.find('#toReturn').val());
                    elementsArray = $('#' + domId + '_elements').val().split(",");
                    elementsNameArray = $('#' + domId + '_elementsName').val().split(",");
                    
//                    for (i = 0; i < elementsArray.length; i++) {
//                        if ((elementsArray[i] != '') && (elementsNameArray[i] != '')) {
//                            if ($('#' + elementsArray[i]).is('[chemdoodle]')) {
////                                getChemDoodleStringContent(elementsArray[i], toReturn[elementsNameArray[i]]);
//                            } else {
//                                if ($('#' + elementsArray[i]).attr('type') == 'Number') {
//                                    $('#' + elementsArray[i]).attr('realvalue', toReturn[elementsNameArray[i]]);
//                                    $('#' + elementsArray[i]).attr('title', toReturn[elementsNameArray[i]]);
//                                }
//                                $('#' + elementsArray[i]).val(toReturn[elementsNameArray[i]]);
//                                $('#' + elementsArray[i]).trigger('change');
//                                
//                                if($('#' + elementsArray[i]).attr('element') == 'ElementAutoCompleteDDLImp'){
//                                    $('#' + elementsArray[i]).trigger('chosen:updated');
//                                    DDLelementsToOnChange[DDLelementsToOnChange.length] = elementsArray[i];
//                                }
//                            }
//                        }
//                  } // yp 31012018 fix bug 5653 ->
                    //non ElementInputImp Number type (need first update UOM before the UOM connect to it (or remove and return the events I choose to update Numeric Last)) 
                    for (i = 0; i < elementsArray.length; i++) {
                        if ((elementsArray[i] != '') && (elementsNameArray[i] != '')) {
                            if ($('#' + elementsArray[i]).is('[chemdoodle]')) {
//                                getChemDoodleStringContent(elementsArray[i], toReturn[elementsNameArray[i]]);
                            } else if ($('#' + elementsArray[i]).attr('type') != 'Number') {
//                              if ($('#' + elementsArray[i]).attr('type') == 'Number') {
//                                    $('#' + elementsArray[i]).attr('realvalue', toReturn[elementsNameArray[i]]);
//                                    $('#' + elementsArray[i]).attr('title', toReturn[elementsNameArray[i]]);
//                              }
                            	//val update
                                $('#' + elementsArray[i]).val(toReturn[elementsNameArray[i]]);
//                                $('#' + elementsArray[i]).trigger('change'); // yp 31012018   THE CLEAN SOLUTION IS (for next version) TO MAKE ALL THE BL IN "generalBL_generalClickEvent (customerFunction, action);" AFTER THE SET IS DONE WITHOUT ANY CALL TO THE SERVER HERE (without DDLelementsToOnChange[DDLelementsToOnChange.length] and trigger change that invoke ajax call)
//                                 										     // (adi added(?) for "fraction scenario" (it seems DDLelementsToOnChange[DDLelementsToOnChange.length] do the same) -> http://localhost:8080/Adama/skylineForm/initid.request?formCode=WorkupDistillation&formId=76976&userId=1109&tableType=&urlCallParam=&PARENT_ID=-1) )
                                
                                if($('#' + elementsArray[i]).attr('element') == 'ElementAutoCompleteDDLImp'){
                                    $('#' + elementsArray[i]).trigger('chosen:updated');
                                    DDLelementsToOnChange[DDLelementsToOnChange.length] = elementsArray[i];
                                }
                            }
                        }
                    }
                    
                    //ElementInputImp Number type 
                    for (i = 0; i < elementsArray.length; i++) {
                        if ((elementsArray[i] != '') && (elementsNameArray[i] != '')) {
                            if ($('#' + elementsArray[i]).attr('type') == 'Number') {
                            	$('#' + elementsArray[i]).attr('realvalue', toReturn[elementsNameArray[i]]);
                                $('#' + elementsArray[i]).attr('title', toReturn[elementsNameArray[i]]);
                                //val update
                                $('#' + elementsArray[i]).val(toReturn[elementsNameArray[i]]);
                                $('#' + elementsArray[i]).trigger('change');
                                
                            }      
                            
                        }
                    }
                }
                
                $(this).find('iframe').attr('src', 'about:blank');
                $(this).remove();
                
                try {
                	if(customerFunction != null && customerFunction.length > 0) {
                		generalBL_generalClickEvent (customerFunction, action);
                	} else {
                		//fire OnChange function for the DDL elements in order to generate the impact on the ascending elements if not handle by customerFunction
                    	for(i = 0;i<DDLelementsToOnChange.length;i++){  
                    		onChangeAjax(DDLelementsToOnChange[i]);
                    	}
                	}
                } catch(err) {
//                	 alert("search failure");
                }
            }
        });

    $dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
}

/**
 * Smart Search Save
 * @param save_
 * @returns
 */
function elementSmartSearchSave(save_) {
    if (checkRequired()) {
        var selectedTable, custid, k = 0,
            toReturn = {};

        // get all elements key and value (without tables)
        $('input[formelement=1]:not([type="Number"]),select[formelement=1],textarea[formelement=1]')
            .each(function () {
                toReturn[this.id] = this.value;
            });
        $('input[formelement=1][type="Number"]')
            .each(function () {
                toReturn[this.id] = $(this).attr('realvalue');
            });
        // get row key and value from all tabels
        $('table.dataTable').each(function () {
            selectedTable = $('#' + this.id).DataTable();
            custid = selectedTable.row('.selected').data();
            if (typeof custid !== 'undefined') {
                selectedTable.columns().every(function () {
                	 //toReturn[this.header().textContent] = custid[k]; -> try take uniqueTitle if the attr exists (because UOM columns displays as UOM and the column is the uniqueTitle
                	 try {
                		 var _header = this.header();
                         var _uTitle = $(_header).attr('uniqueTitle');
                         var _title;
                         
                         if(_uTitle !== undefined && _uTitle != "") {
                             _title = _uTitle;
                         } else {        
                             _title = $(_header).text();
                         }    
                         toReturn[_title] = custid[k]; 
                	 } catch(e) {
                		 toReturn[this.header().textContent] = custid[k];
                	 }
                     k++;
                });
            }
            k = 0;
        });

        $('#toReturn').val(JSON.stringify(toReturn));
        save_.setAttribute('flag', '1');                        
    	parent.$('#prevDialog').dialog('close');
    } else {
        displayAlertDialog(getSpringMessage('PleaseFillTheRequiredFields'));
    }
   
}

/**
 * return from onAjaxCall()
 * 
 * @param obj
 * @returns
 */
function upDateElementSmartSearch(obj) {
    var element = $('[id="' + obj.domId + '"]');
    if (typeof obj.isDisabled !== 'undefined') {
        if (obj.isDisabled.toLowerCase() == "false") {
            element.removeClass('disabledclass');
            if (element[0].tagName == "I") {
                element.attr('onclick', element.attr('onclickAfterEnable'));
            }
        } else {
            element.addClass('disabledclass');
            element.css('border-color', '');
            element.css('outline', '');
            if (element[0].tagName == "I") {
                element.attr('onclick', '');
            }
        }
    }
    if (typeof obj.isHidden !== 'undefined') {
        if (obj.isHidden.toLowerCase() == "false") {
            element.css('visibility', 'visible');
        } else {
            element.css('visibility', 'hidden');
        }
    }
}

function replaceSmartSearchUrlCallParamVal(objVal, configId) {
	var toReturn = "";
	if( objVal !== 'undefined') {
		var configIdVal = '';
		try {
			if($('#' + configId).val() !== undefined && $('#' + configId).val().length > 0) {
				configIdVal = $('#' + configId).val();
			}
		} catch(e) {}
		toReturn = objVal.replace('@ID@',configIdVal);
	}
	return encodeURIComponent(toReturn);
}