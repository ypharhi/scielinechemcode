/**
 * general click event
 * @param action
 * @returns
 */
function elementIreportButtonClickEvent(customerFunction, formIdToSubmit) {
	$('input[type="hidden"][name="irUrl"]').val(url());  //kd 09072018 put url for hyperlink inside report
	//general event call
	if (customerFunction == null || customerFunction.length == 0) {
		setDisplayValuesForIreport(formIdToSubmit); 
	} 
	else
	{
		var fn = window[customerFunction];
		var fnparams = [formIdToSubmit];
		if (typeof fn === "function")
		{
			//fn();
			fn.apply(null, fnparams);
		}
		else
		{
		    console.log("ERROR Function " + customerFunction + " does not exist.");
		}
	}
}

function setDisplayValuesForIreport(formIdToSubmit)
{
	var valObject = collectElementsDisplayValues();	
	//console.log("setDisplayValuesForIreport(): " + JSON.stringify(valObject));
	$('input[type="hidden"][name="irElementsDisplayValues"]').val(JSON.stringify(valObject));
	$('#' + formIdToSubmit).submit();
}

function getImageSelectionAndRenderIreport(uid) {
	var fileIdList = ['-1'];
	$('#documents input[class="dataTableApiSelectInfo"]:checked').each(function (index) {
		fileIdList.push(($(this).val()));
    });
	setFormParamMap($('#formCode').val(), $('#formId').val(),"FILTER_FILE_ID",fileIdList,setDisplayValuesForIreport,[uid]);
}

/**
* kd 09062018
* set var to Url 
* @param can be use for identification current report, and window
*/
/*function getUrl(uniqueId) {
    $('#irUrl').val(url());
}*/