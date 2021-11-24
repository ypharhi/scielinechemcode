//function disablePage(isActual){
//	if(isActual == '1'){
//		generalBL_disablePage();
//	}
//}

/**
 * returns an object
 * { 
 * 	setRequired: indicates if the mandatory fields have to be required according to some conditions,
 * 	mandatoryList: list of mandatory elements that are required when 'setRequired' is 0
 * }
 */
function isMandatoryFieldsRequired(){
//	if($("#formCode").val()=='XXX'){
//		 //...
//	}
	
	return {
		setRequired:  '1',
		mandatoryList: []
	};
}