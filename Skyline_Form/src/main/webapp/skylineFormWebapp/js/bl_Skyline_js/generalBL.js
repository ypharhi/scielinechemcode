/**
 * 
 * @returns path info
 */
function getFormPathInfo() {
	return  $('#formPathInfo').val();
}

function initForm() {
    // TODO
}

/**
 * Confirm message before exit form page without save
 * 
 * @param functionName
 * @param params
 * @returns
 */
function confirmWithOutSave(functionName, params) {
    functionName(params);
}

/**
 * If STATUS_ID field is completed than disable all other inputs
 */
function generalBL_statusIdCompleted() {

}

/**
 * If INSTRUMENTSTATUS_ID field is disabled then disable all other inputs
 */
function generalBL_statusIdDisabled() {

}
/**
 * If MAINTCALSTATUS_ID field is disabled then disable all other inputs
 */
function generalBL_statusIdDoneCanceled() {

}

/**
 * Disable all elements in the page
 * 
 * @param enableElement
 * @param enableEditButton
 * @returns
 */
function generalBL_disablePage(enableElement, enableEditButton) {

}

/**
 * 
 * @param formCode
 * @returns
 */
function doNew(formCode) {}

/**
 * 
 * @returns
 */
function doBack() {}

/**
 * change landing page on new button
 * 
 * @param formCode
 * @returns
 */
function onNewButtonIntegration(formCode) {
    return formCode;
}

/**
 * get Forward Page
 * 
 * @returns
 */
function getForwardPage() {

}

/**
 * customer click event
 * 
 * @param customerFunction
 * @param action
 * @returns
 */
function generalBL_generalClickEvent (customerFunction, action) {   //customerClickEvent

}

/**
 * hideUnnecessaryFormTypesFormBuilder
 * @returns
 */
function hideUnnecessaryFormTypesFormBuilder() {
	$('[name="formType"] option[value!="REPORT"]').remove();
}

/**
 * hideUnnecessaryElementsFormBuilder
 * @param selectArray
 * @returns
 */
function hideUnnecessaryElementsFormBuilder(selectArray) {
	var i, j, toReturn = [];	
	var elementsToShow = ['ElementDataTableImp', 'ElementRangeFilterImp', 'ElementAutoCompleteDDLImp', 'ElementLabelImp', 'ElementIreportImp'];
	var elementsToShowLength = elementsToShow.length;
	var selectArrayLength = selectArray.length;
	
	for(i=0; i<selectArrayLength; i++){
		for(j=0; j<elementsToShowLength; j++){
			if(selectArray[i].code === elementsToShow[j]){
				toReturn.push(selectArray[i]);
				break;
			}
		}
	}
	
	return toReturn;
}