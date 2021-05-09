
try
{
	//window.history.go(1);
}
catch(e){}

var currColor = '';

/*** prevent BACKSPACE key press propagation 
**   12012014 a.b 
***/
$(document).unbind('keydown').bind('keydown', function (event) 
{
    if (event.keyCode === 8) 
    {
        var d = event.srcElement || event.target;
        if ((!$(d).is('input:not([readonly],[disabled]), text') && !$(d).is('input:not([readonly],[disabled]), password') && !$(d).is('textarea:not([readonly],[disabled])'))
            || ($(d).is('input:radio') || $(d).is('input:checkbox')))      
        {
            event.preventDefault();
        }
    }
});

/*** prevent html  text selection, except for input fields and textarea
**   16122014 a.b 
***/

$(document).on('selectstart', function(event)
{
    var el = event.srcElement || event.target;
    //if($(el).is(':not(input, textarea)')) // yp 13032016 add allowCopy attribute to allow copy on specific fields (fix bug 3405) -> 
    if($(el).is(':not(input, textarea)[allowCopy!=1]'))
    {
        event.preventDefault();
    }
});

/** customized jQuery :selector. 
  * as :selector are always extension it's mostly efficient to use them as in
  *					$(element).filter(':selector') 
  *				rather than - $(element:selector).
  * tutorials about expr[':'] extension can be found here: http://www.websanova.com/blog/jquery/12-awesome-jquery-selector-extensions#.U7563rGcDcY
  * which explains, for instance, why we're using m[3] and the function parameters (el, i, m)
**/
$.extend( $.expr[':'], {
	/* exclude elements whose direct parent answers to certain selector
	 * usage: $('select').filter(':excludeParent(.exClass)') will return all <select> whose direct parent is NOT of class exClass */
	excludeParent: 	function(el, i, m) {
						return $(el).parent(m[3]).length < 1;
					},
	/* inverted version of excludeParent: returns only elements whose direct parent answer to certain selector 
	 * usage: $('span').filter(':parentAttr(li)') will return only <span>s within <li>s */
	parentAttr:	function(el, i, m) {
					return $(el).parent(m[3]).length > 0;
				}, 
	/* as excludeParent but checks ALL parents above element. Currently used to check if element is disabled (through any of its parents) */
	excludeParents: function(el, i, m) {
						return $(el).parents(m[3]).length < 1;
					}
});

function validateDecimalNumberWithMinus(intKeyCode)
{
	
	if (intKeyCode != 45 && intKeyCode !=46)
	{
		if ((intKeyCode < 48) || (intKeyCode > 57)  )
			return false;
		else
			return true;
	}
	else
	{
		return true;
	}
}


function setMandatoryField(definitionType) {
//-------------------------------------
//25/09/08 Ilia Bulaevski
//-------------------------------------
      var alltags = document.getElementsByTagName('SPAN');

      for (var i = 0; i < alltags.length ; i++){
            var spanValue =alltags[i].innerText;
            var mandatorytype= alltags[i].MandatoryType;
                   
            if(spanValue=="*" ){
                  if(definitionType==mandatorytype || mandatorytype=="Constant"){
                         alltags[i].style.visibility =  "visible";
                  }else{
                        alltags[i].style.visibility = "hidden";
                 }       
            }
      }
}

function exitPageWarning(obj)
{
	var _message = "Click <b>Confirm</b> to exit without saving your changes",
		_title = "Warning",
		_isChanged = bOnChange,
		_onConfirm = null;
	
	if( obj != null && typeof obj == 'object' )
	{
		_message = (obj.message == null) ? _message : obj.message;
		_title = (obj.title == null) ? _title : obj.title;
		_isChanged = (obj.isChanged == null) ? _isChanged : obj.isChanged;
		_onConfirm = (obj.onConfirm == null || typeof obj.onConfirm != 'function') ? null : obj.onConfirm;
	}
	
	if( _isChanged )
	{
		if( $('#divConfirmDialog').length == 0 )
		{
			initConfirmDialogDiv();
		}
		
		openConfirmDialog({ title:_title, message:_message, onConfirm:_onConfirm });
	}
	else if( _onConfirm )
	{
		_onConfirm();
	}
}

function setParametrOfChange()
{
   if(document.getElementById("actionid"))
   {
        if(document.getElementById("actionid").value == "edit")
            bOnChange = true;
   }
   else
   {
       if($('#action_id').val() == "edit")
            bOnChange = true;
    }
}

function SetRowGrid(CntrValue)
//------------------------------
// This function sets grid row
// Update Ilia Bulaevski
//------------------------------
{ 
    with(document.all)
    {
        if (CntrValue != 'null' && CntrValue != '')
        {
			var cntr = document.all('TR' + CntrValue);
            SelectTR(cntr);
        }
    }
}

function focusInput(currEvent)
{       
	
	var event = window.event || currEvent;
	var e = event.target || event.srcElement;
	//alert(window.event + "  :  " + e);
	if (e == null)
	{
		return;
	}
	//alert(event.type);
	if (event.type == 'focus')
	{
		currColor = e.style.borderColor;
		e.style.border = '1px solid';
		e.style.borderColor = '#5897fb';
	}
	else if(event.type == 'blur') //for support to IE11(cause Event Object may return other event.types)
	{
		e.style.borderColor = currColor;
		e.style.border = '#aaa 1px solid';
	}
	
}

function GetCopyright(stLang)
{
	var d = new Date();
	var stCopyright = "";

	if(stLang.toString().toUpperCase() == "HEB")
	{
		stCopyright = "<FONT COLOR=\"#FF4500\">○</FONT> פותח ע&quot;י <FONT COLOR=\"#F4A460\">??</FONT> קו?טק בע&quot;? <FONT COLOR=\"#FF4500\">○</FONT>";
	}
	else
	{
		stCopyright = "developed by <FONT COLOR=\"#FF4500\">©</FONT> Comtec";
	}

	return stCopyright;
}

function isLeapYear(iYear)
{
   return((((!(iYear % 4)) && (iYear % 100) ) || !(iYear % 400)) ? true : false);
}

function CheckDate(oDate)
{
	var sValue = oDate.value.toString();
	var len = sValue.length;
	var DateError = false;

	try
	{
		if(len == 0)
		{
			return true;
		}

		if( len == 0 || len > 10 || len < 10 || isNaN(sValue.substr(0, 2)) || sValue.substr(2, 1) != "/" || isNaN(sValue.substr(3, 2)) || sValue.substr(5, 1) != "/" || isNaN(sValue.substr(6, 4)) )
		{
			DateError = true;
		}
		else
		{
			var stDay, stMonth, stYear;

			stDay = sValue.substr(0, 2);
			stMonth = sValue.substr(3, 2);
			stYear = sValue.substr(6, 4);

			if(stDay<1 || stDay > 31)
			{
				DateError = true;
			}

			if(stMonth < 1 || stMonth > 12)
			{
				DateError = true;
			}

			if(stYear < 1000)
			{
				DateError = true;
			}

			switch(stMonth)
			{
				case  4  :
				case  6  :
				case  9  :
				case  11 :  if(stDay > 30)
							{
								DateError = true;
							}

							break;
				case  2  :  if( isLeapYear(stYear) )
							{
								if(stDay > 29)
								{
									DateError = true;
								}
							}
							else
							{
								if(stDay > 28)
								{
									DateError = true;
								}
							}
			}

			if(!DateError)
			{
				if(stDay.length == 1)
				{
				  stDay = "0" + stDay;
				}

				if(stMonth.length == 1)
				{
				  stMonth = "0" + stMonth;
				}

				oDate.value = "" + stDay + "/" + stMonth + "/" + stYear;
			}
		}
	}
	catch(e)
	{
		DateError = true;
	}

	if(DateError)
	{
		oDate.value = "";
		alert("ת?רי? ?? חוקי" + "\n" + "ת?רי? ?דוג?ה" + "\n" + "01/12/2000");
	}
}

function validateNumber(intKeyCode, e)
{
	var event = e ? e : window.event;
	var retVal = false;
	if ((intKeyCode < 48) || (intKeyCode > 57))
	{
		retVal = false;
	}
	else
	{
		retVal = true;
	}
	
	if(!retVal)
	{
		event.preventDefault ? event.preventDefault() : (event.returnValue = false);
	}
	else
	{
		return true;
	}
}

function validatePositiveNumber (intKeyCode, value, e)
{
	var event = e ? e : window.event;
	var retVal = false;
	if ((intKeyCode < 48) || (intKeyCode > 57) || (intKeyCode == 48 && value.length == 0))
		retVal = false;
	else
		retVal = true;
	
	if(!retVal)
	{
		event.preventDefault ? event.preventDefault() : (event.returnValue = false);
	}
	else
	{
		return true;
	}
}

function fnValidatePositiveNumber (inStr, inFieldName, isRequired, focusElemId)
{
	var id = (focusElemId == null) ? '' : focusElemId;
	var reg = /[^0-9]/;
	if ( inStr != null && (reg.test(inStr) || inStr.indexOf('0') == 0))
	{
		displayAlertDialog(inFieldName + " contains illegal characters or format", { focusOn:id, title:"Invalid Number" });
		return false;
	}
	
	if (isRequired && ( inStr == null || inStr == "" ))
	{	
	    displayAlertDialog(inFieldName + " is a required field", { focusOn:id, title:"Required Field Missing" });
		return false;
	}
	
	return true;
}


function validateDecimal(value, key, e)
{	
	var event = e ? e : window.event;
	var retVal = false;
	if(key == 45 && value.length == 0) /*45='-'*/
	{
		retVal = true;	
	}
	
	if((key >= 48 && key <=57) || (key == 46 && value.indexOf(".") == -1))
	{
		retVal = true;
	}
	
	if(!retVal)
	{ // t.s. 170814; this code might be irrelevant. came before bug fix when (!retVal) didn't return retVal at the end (now retVal is ALWAYS returned)
		event.preventDefault ? event.preventDefault() : (event.returnValue = false);		
	}
	
	return retVal;        
}
function validateDecimalWithComma(value, key)
{	
	if(key == 45 || key == 44)
	{
		return true;	
	}
	if(key >= 48 && key <=57)
	{
		return true;
	}
	else
	{
		return false;
	}
}
function validateDecimalNoSign(value, key, e)
{
	var event = e ? e : window.event;
        var retVal = false;
	if((key >= 48 && key <=57) || (key == 46 && value.indexOf(".") == -1))
	{
		retVal = true;	
	}
	if(!retVal)
            event.preventDefault ? event.preventDefault() : (event.returnValue = false);
        else
            return true;
}
function replaceMinus(value)
{
   var r, re;   
   var minusExist = false;
   var result;	

   re = /-/g;             //Create regular expression pattern.

   if(value.indexOf("-") == 0)
   {
	minusExist = true; 
   }

   if(minusExist)
   {
	result = "-" + value.replace(re, "");
   }
   else
   {
	result = value.replace(re, "");
   }

   return result;
	
}

function isPromtYes()
{
	var result, winStats;

	winStats = "dialogHeight: 150px; dialogWidth: 300px;edge: sunken; scroll: No; center: Yes; help: No; resizable: No; status: No;unadorned: Yes;";

	try
	{
		result = window.showModalDialog( "PromtYesNo.htm", '', winStats);
	}
	catch(e)
	{
		result = false;
	}

	return result;
}

function isPromptYesMessage(msg)
{
	var result, winStats;
	var strMessage = (msg == null) ? "":"?msg=" + msg;
		
	winStats = "dialogHeight: 150px; dialogWidth: 300px;edge: sunken; scroll: No; center: Yes; help: No; resizable: No; status: No;unadorned: Yes;";

	try
	{
		result = window.showModalDialog( "PromtYesNoCustom.html" + strMessage, '', winStats);
	}
	catch(e)
	{
		result = false;
	}

	return result;
}

function fnTrimString (stringToTrim) 
{
	if (stringToTrim != null)
		return stringToTrim.replace(/^\s+|\s+$/g,"");
		
	return '';
}

// For Grid Lines Selection
var TRSelected;
var prevClassName;
function SelectTR(oTR)
{
	if(oTR != TRSelected)
	{		
		try
		{
			var objR = oTR.getElementsByTagName("INPUT");
			

			if(objR[0].type != "button" && !objR[0].checked)
			{
				objR[0].checked = true;
				objR[0].click();
			}
		}
		catch(e){}

		if(TRSelected)
		{
			try
			{
				TRSelected.className = prevClassName;
			}
			catch(e){}
		}
		
		prevClassName = oTR.className;
		
		try
		{
			oTR.className = "row_light";
		}
		catch(e){}
		
		TRSelected = oTR;
	}
}

function UnselectTR()
{
		if(TRSelected)
		{
			try
			{
				TRSelected.className = prevClassName;
			}
			catch(e){}
			
			TRSelected = null;
		}

}

function SelectTRMulti(oTR)
{
	if(oTR != TRSelected)
	{
		try
		{
			var objCheck = oTR.getElementsByTagName("INPUT");

			if(!objCheck[0].checked)
			{
				objCheck[0].checked = true;
				objCheck[0].click();
				oTR.className = "TRVSelected";
			}
			else
			{
				objCheck[0].checked = false;
				objCheck[0].click();
				oTR.className = oTR.savedclass;
			}
		}
		catch(e){}

	}
}


function GetDefaultDate()
{
	var d, day, month, stDate;

	d = new Date();

	day = d.getDate();
	if(day < 10)
	{
		day = "0" + day;
	}

	month = d.getMonth() + 1;
	if(month < 10)
	{
		month = "0" + month;
	}

	stDate = "" + day + month + d.getYear();

	return stDate;
}

function GetDefaultDateSlashed()
{
	var d, day, month, stDate;

	d = new Date();

	day = d.getDate();
	if(day < 10)
	{
		day = "0" + day;
	}

	month = d.getMonth() + 1;
	if(month < 10)
	{
		month = "0" + month;
	}

	stDate = "" + day + "/" + month + "/" + d.getFullYear();

	return stDate;
}

function MoveFromTo(src,dest)
{
	try
	{
		var i;
		var oOption;
		var oSrcList=document.all.item(src);
		var oDestList=document.all.item(dest);

		for (i=0;i < oSrcList.options.length;i++)
		{
			if (oSrcList.options(i).selected)
			{
				oOption = document.createElement("OPTION");
				oOption.text=oSrcList.options(i).text;
				oOption.value=oSrcList.options(i).value;
				oDestList.options.add(oOption);
			}
		}

		i=0
		while (i < oSrcList.options.length)
		{
			if (oSrcList.options(i).selected)
			{
				oSrcList.options.remove(i);
			}
			else
			{
				i++;
			}
		}
	}
	catch(e){}
}

/*******************  Sorting *****************************/

function SetImgSrc(oImg, arrIndex)
{
    if(arrIndex < arrIMG.length)
    {
        $(oImg).attr('src', $(arrIMG[arrIndex]).attr('src'));
    }
}

function InitSortSettings()
{
    var stCurrFld = $('#txtSortField').val();
    var stCurrOrder = $('#txtSortOrder').val();
    var iOrder = 1;

    var currColumn = $('#Clmn' + stCurrFld);

    $(currColumn).addClass( "TDHSort" );
           
    try
    {
        iOrder = parseInt(stCurrOrder);
    }
    catch(e)
    {
        iOrder = 1;
    }

    SetImgSrc( $('#Img' + iOrder, currColumn), iOrder );
    $('#Img' + iOrder, currColumn).css('cursor', 'default');
}

function fnPerformSort(fld, iOrder)
{
    var stOrder = iOrder.toString(); 
	
    var stCurrFld = document.all.txtSortField.value.toString();
    var stCurrOrder = document.all.txtSortOrder.value.toString();
	
    if(fld != stCurrFld || stOrder != stCurrOrder)
    { 
        document.all.txtSortField.value = fld;
        document.all.txtSortOrder.value = stOrder;
		
	try
	{
        	document.all.oForm.submit();
			
	}
	catch(e)
	{
		/*txtAction.value = "combotestbysamplepointid";
        	document.all.cmdSave.disabled = true;
        	document.all.cmdNew.disabled = false;
   
        	action = "iframesuportservlet";
        	target= "WinToSubmit";
        	submit(); 

        	target=targetOld;
        	action=actionOld;*/
		OnChangeDSPID();
	}

    }
}


// function for Electronic Sign.
// return false if we did not sign

function ESign()
{

    with (document.all)
    {
        if (esign_number == null || esign_number.value == "0" || esign_number.value == "" || esign_number == "null")
        {
            return true;
        }
    }

	var winStatus, winArguments, rValue;
	var arrArgs = new Array();
    
    arrArgs[0] = document.forms(0);
	arrArgs[1] = "esignservet";

	winArguments = arrArgs;
	winStatus = "dialogHeight: 390px; dialogWidth: 400px;edge: sunken; scroll: No; center: YES; help: No; resizable: No; status: No;unadorned: Yes;";

	var es = window.showModalDialog("WinModalSubmit.htm", winArguments, winStatus);

    with (document.all)
    {

        if (es == null || es == "" || es == "0")
        {
            return false;
        }
        else
        {
            esign_id.value = es + "";            
            return true;
        }
    }
    
}  // ESign()

//This function to manipulation duplicate insertion or updation on equipment type
function showEquipmentExistMsg()
{
	var result, winStats;

	winStats = "dialogHeight: 190px; dialogWidth:340px;edge: sunken; scroll: No; center: Yes; help: No; resizable: No; status: No;unadorned: Yes;";

	try
	{
		result = window.showModalDialog( "EquipmentTypeExist.html", '', winStats);
	}
	catch(e)
	{
		result = false;
	}

	return result;
}

function showMaterialExistMsg(errorNum)
{
	var result, winStats, winArguments;	
	
	var arrArgs = new Array();
    
    arrArgs[0] = errorNum;
	

	winArguments = arrArgs;

	winStats = "dialogHeight: 180px; dialogWidth: 350px;edge: sunken; scroll: No; center: Yes; help: No; resizable: No; status: No;unadorned: Yes;";

	try
	{
		result = window.showModalDialog( "MaterialExistMsg.htm", winArguments, winStats);
	}
	catch(e)
	{
		result = false;
	}

	return result;
}


function UniqueSensorMsg()
{
 var result, winStats;

 winStats = "dialogHeight: 150px; dialogWidth: 410px;edge: sunken; scroll: No; center: Yes; help: No; resizable: No; status: No;unadorned: Yes;";

 try
 {
  result = window.showModalDialog( "CheckIsUniqueSensor.htm", '', winStats);
 }
 catch(e)
 {
  result = false;
 }

 return result;
}

// added by sawsan murshed, 21-1-2010 to cinfirm run data
function confirmRunData(time)
{

	var winStatus, winArguments,result, rValue;
	var arrArgs = new Array();
    
    arrArgs[0] = time;
	

	winArguments = arrArgs;
        winStatus = "dialogHeight: 240px; dialogWidth: 500px;edge: sunken; scroll: No; center: YES; help: No; resizable: No; status: No;unadorned: Yes;";       
       
      try
	{
		result = window.showModalDialog("ConfirmRunData.html", winArguments, winStatus);
     
	}
	catch(e)
	{
		result = false;
	}
	return result;

}
function checkIsPromtYes()
{
	var result, winStats;

	winStats = "dialogHeight: 150px; dialogWidth: 300px;edge: sunken; scroll: No; center: Yes; help: No; resizable: No; status: No;unadorned: Yes;";

	try
	{
		result = window.showModalDialog( "CheckIsPromtYesNo.htm", '', winStats);
	}
	catch(e)
	{
		result = false;
	}

	return result;
}

function showWarningMsg()
{
	var result, winStats;

	winStats = "dialogHeight: 150px; dialogWidth: 300px;edge: sunken; scroll: No; center: Yes; help: No; resizable: No; status: No;unadorned: Yes;";

	try
	{
		result = window.showModalDialog( "ShowWarningMsg.htm", '', winStats);
	}
	catch(e)
	{
		result = false;
	}

	return result;
}
//this fuction to confirm delete run data ( Run Delation Tab)
function confirmDeleteRun(runName)
{

   var winStatus, winArguments, result;

    	var arrArgs = new Array();
       arrArgs[0] = runName;
    	winArguments = arrArgs;
	winStats = "dialogHeight: 175px; dialogWidth: 300px;edge: sunken; scroll: No; center: Yes; help: No; resizable: No; status: No;unadorned: Yes;";
        
	try
	{
		result = window.showModalDialog( "confirmDeleteRun.html", winArguments, winStats);
     
	}
	catch(e)
	{
		result = false;
	}
	return result;
}

function isNumeric(input)
{
	var regexp = /^[0-9]+$/;
	if(regexp.test(input))
	{
		return true;
	}

	return false
}

function getDaysDiff(start, end)
{
	var day, month, year;
	var startDate, endDate;
	var daysCount;    
          
	day = parseInt(start.substring(0, 2), 10);
	month = parseInt(start.substring(2, 4), 10) - 1;
	year = parseInt(start.substr(4), 10);
	startDate = new Date(year, month, day);

	day = parseInt(end.substring(0, 2), 10);
	month = parseInt(end.substring(2, 4), 10) - 1;
	year = parseInt(end.substr(4), 10);
	endDate = new Date(year, month, day);    
  
	var diff = endDate.getTime() - startDate.getTime();
	daysCount = Math.floor (diff / (1000 * 60 * 60 * 24));

	return daysCount;
}      

function showDeleteCommentcategoryErrorMsg()
{
	var result, winStats;

	winStats = "dialogHeight: 200px; dialogWidth: 330px;edge: sunken; scroll: No; center: Yes; help: No; resizable: No; status: No;unadorned: Yes;";

	try
	{
		result = window.showModalDialog( "deleteCommentcategoryErrorMsg.html", '', winStats);
	}
	catch(e)
	{
		result = false;
	}

	return result;
}

function GetCurrentDate()
{
	var d = new Date();

	var curr_date = d.getDate();
	var curr_month = d.getMonth();
	curr_month++;
	var curr_year = d.getFullYear();
	
	return curr_date + "" + curr_month + "" + curr_year;
	
}

function FindAndReplaceAll(text, strA, strB)
{
	return text.replace( new RegExp(strA,"g"), strB );
}

function ConvertIligalChars(invalue)
{
	var retVal = invalue;	
    retVal = FindAndReplaceAll(retVal,"%23","#");	
    retVal = FindAndReplaceAll(retVal,"%20"," ");	
    return retVal;
}

function ValidateLigalChars(inParam)
{
	if(inParam.indexOf("<") > -1)
	{
		return false;
	}
	if(inParam.indexOf(">") > -1)
	{
		return false;
	}
	if(inParam.indexOf("=") > -1)
	{
		return false;
	}
	if(inParam.indexOf("&") > -1)
	{
		return false;
	}
	if(inParam.indexOf("%") > -1)
	{
		return false;
	}
	if(inParam.indexOf("|") > -1)
	{
		return false;
	}
	if(inParam.indexOf(";") > -1)
	{
		return false;
	}
	if(inParam.indexOf("\'") > -1)
	{
		return false;
	}
	if(inParam.indexOf(",") > -1)
	{
		return false;
	}
	if(inParam.indexOf("[") > -1)
	{
		return false;
	}
	if(inParam.indexOf("]") > -1)
	{
		return false;
	}
	if(inParam.indexOf("?") > -1)
	{
		return false;
	}
 
	if(inParam.indexOf('\"') > -1)
	{
		return false;
	}
	
	if(inParam.indexOf('@') > -1)
	{
		return false;
	}
	
	if(inParam.indexOf('\\') > -1)
	{
		return false;
	}
	return true;
}

function validateLegalChar(inKeyCode, e)
{
        var event = e ? e : window.event;
        var retVal = true;
        if (inKeyCode == 34 ||							// "
		(inKeyCode >= 38 && inKeyCode <= 39) ||		// & '
		 inKeyCode == 44 ||							// ,
		(inKeyCode >= 59 && inKeyCode <= 64) ||		// ; < = > ? @
		 inKeyCode == 92  ||						// \ 
		 inKeyCode == 124)							// |
		 retVal = false;
	if(!retVal)
            event.preventDefault ? event.preventDefault() : (event.returnValue = false);
        else
            return true;
}

/* "overload" to validateLegalChar() that allows for set any parameters 
 * legals parameter - must be array of keyCode, which are an exception from the usual list of illegal chars. 
 * */
function validateLegalCharSelectiveObj(obj)
{
	var event = obj.event ? obj.event : window.event;
	var inKeyCode = obj.event.keyCode;
    var retVal = true;
    var legals = obj.legals;
    var illegalCharsArr = [34,38,39,44,59,60,61,62,63,64,92,124];
    /*
    if (inKeyCode == 34 ||							// "
	(inKeyCode >= 38 && inKeyCode <= 39) ||		// & '
	 inKeyCode == 44 ||							// ,
	(inKeyCode >= 59 && inKeyCode <= 64) ||		// ; < = > ? @
	 inKeyCode == 92  ||						// \ 
	 inKeyCode == 124)							// |
	 */
    
    for (var i=0;i<illegalCharsArr.length;i++)
	{		
    	if (inKeyCode == illegalCharsArr[i])
		{
    		//alert($.inArray(inKeyCode, legals) != -1);
    		if(legals != null && ($.inArray(inKeyCode, legals) != -1))
			{
				retVal = true;				
			}
			else
			{
				retVal = false;
			}
			break;
		}
		else
		{
			retVal = true;
		}
		
	} 
    if(!retVal)
        event.preventDefault ? event.preventDefault() : (event.returnValue = false);
        return true;
}

function validateFilterReportsLegalChar(inKeyCode)
{
	if ( inKeyCode == 34 ||							// " 
		 inKeyCode == 44 ||							// ,
		 inKeyCode == 59 ||							// ;   
		 inKeyCode == 124)							// |
		 return false;
	return true;
}

function validateLegalString (inStr)
{
	var illegalChars = ['\"', '&', '\'', ',', ';', '<', '=', '>', '?', '@', '\\', '|'];
	if (inStr == null)
	{
		return true; // might be required, yet legal
	}
		
	for (var i in illegalChars)
	{
		if (inStr.indexOf(illegalChars[i]) > -1)
			return false;
	}
	return true;	
}

/* "overload" to validateLegalString() that allows for a pre-set list of illegal chars (inIllegals), 
 * or exceptions to illegal chars (inLegals).
 * inLegals is used only if inIllegals is null.
 * If neither was sent (only inStr used) the else clause will run and call validateLegalString() */
function validateLegalStringSelective (inStr, inIllegals, inLegals)
{	
	var currentIllegals = inIllegals; 
	if (inIllegals == null)
	{	// removes inLegals from default illegal chars list, or return the full list if inLegals is null
		currentIllegals = createIllegalCharString(inLegals);
	}
	
	if ( inStr != null && (inIllegals != null || inLegals != null) )
	{
		for (var i = 0; i < currentIllegals.length; i++)
		{ 
			if (inStr.indexOf(currentIllegals.charAt(i)) > -1)
			{ 
				return false;
			}
		}
	}
	else // both are null/undefined: execute basic validation
	{
		return validateLegalString(inStr);
	}
	
	return true;
}

function createIllegalCharString (inLegals)
{
	var defStr = '\"\'\\&,;<=>?@|';
	if (inLegals == null) // covers a case where value was not sent
	{
		return defStr;
	}
	var reg = new RegExp('[' + inLegals + ']', 'g');
	return (defStr).replace(reg, '');
}

/* Besides alerting if a required field is empty, it also allows for several values considered "empty", 
 * and for exceptions of the usual illegal chars (or a pre-set list sent to the function).
 * inStr - string to be evaluated. Should be sent trimmed already.
 * forbiddenValues - values separated by | (pipe) sign (e.g, "0|1|choose") that will alert that the field is required if inStr is any of those.
 * 		(Originally for indexes of DDL, or any value which is not "" but is still considered illegitimate/empty).  
 * illegals - A string of concatenated chars replacing the "usual" illegal chars.
 * legals - A string of concatenated chars, which are an exception from the usual list of illegal chars.
 * focusOn - The id of the element that should get focus after alert-dialog is close.
 * frameId - If focusOn field is within an iFrame and called from the parents send frame elementId by this parameter
 * note: @legals is considered ONLY if @illegals is null */ 
function fnValidateString (obj) /* inStr, fieldName, isRequired, forbiddenValues, illegals, legals, focusOn, frameId */
{	
	// set defaults if no values sent
	var _inFieldName = (obj.fieldName == null) ? '' : obj.fieldName,
		_isRequired = (obj.isRequired == null) ? false : obj.isRequired,
		_inForbiddenValues = (obj.forbiddenValues == null) ? '' : obj.forbiddenValues, 
		_inIllegals = obj.illegals,
		_inLegals = obj.legals,
		_hasFocus = (obj.focusOn == null) ? '' : obj.focusOn,
		_frameId = (obj.frameId == null) ? '' : obj.frameId,
		_inStr = obj.inStr; 			
	var alertMsg = '';
	
	if ( !validateLegalStringSelective(_inStr, _inIllegals, _inLegals) )
	{		
        alertMsg = (_inFieldName + " contains illegal characters" + 
			       //(_inLegals != null ? "" : ("<br/>Please do not use " + (_inIllegals != null ? displayStringOfCharsWithSpace(_inIllegals) : "\" & \' , ; < = > ? @ \\ |" ))));
        			"<br/>Please do not use " + displayStringOfCharsWithSpace(_inIllegals, _inLegals));
		displayAlertDialog(alertMsg, { title:"Invalid Value", focusOn:_hasFocus, frameId:_frameId });
		return false;
	}
		
	if (_isRequired) 
	{		
		alertMsg = (_inFieldName == '') ? "" : (_inFieldName + " is a required field");
		
		if (_inStr == null || _inStr == "")
		{			
			displayAlertDialog(alertMsg, { title:"Required Field Missing", focusOn:_hasFocus, frameId:_frameId });
			return false;
		}
		
		if (("|" + _inForbiddenValues + "|").indexOf("|" + _inStr + "|") != -1)
		{
			displayAlertDialog(alertMsg, { title:"Required Field Missing", focusOn:_hasFocus, frameId:_frameId });
			return false;
		}
	}
    return true;
}
/*
  function separates string with spaces for comfort display
*/
    function displayStringOfCharsWithSpace(illegals, legals)
    {        
        var strWithSpace = " ";
        var tmpIllegals = illegals;
        
        if(illegals == null)
    	{
        	// removes inLegals from default illegal chars list, or return the full list if inLegals is null
        	tmpIllegals = createIllegalCharString(legals);        	
    	}
        for (var i = 0; i < tmpIllegals.length; i++)
        {
                strWithSpace += tmpIllegals.charAt(i) + " ";                    
        }
        return strWithSpace;
    }

/* validates a numeric whole field for illegal chars or format
	isDecimal: if True allows for a single '.' 
	isSigned: if True allows a '-' at the first index only
	any illegal format or character returns false   */
function validateNumeric (inStr, isDecimal, isSigned)
{
	var reg = /[^0-9.-]/	
	if (!isSigned && inStr.indexOf('-') > -1)
		return false;
		
	if (!isDecimal && inStr.indexOf('.') > -1)
		return false;
	
	if (reg.test(inStr))
		return false;
		
	if (inStr.indexOf('.') != inStr.lastIndexOf('.'))
		return false;
		
	if (inStr.lastIndexOf('-') > 0)
		return false;
	
	return true;
}

/* uses validateNumeric to alert for illegal chars or format in a numeric string
   if illegal an alert pops up using the relevant field name and false is returned;
   if legal returns true 
   note: to use isRequired properly, inStr must be already trimmed */
function fnValidateNumeric (inStr, inFieldName, isDecimal, isSigned, isRequired, focusElemId)
{
	var id = (focusElemId == null) ? '' : focusElemId;
	
	if ( inStr != null && !validateNumeric(inStr, isDecimal, isSigned))
	{
		displayAlertDialog(inFieldName + " contains illegal characters or format", { focusOn:id, title:"Invalid Number" });
		return false;
	}
	
	if (isRequired && ( inStr == null || inStr == "" ))
	{	
	    displayAlertDialog(inFieldName + " is a required field", { focusOn:id, title:"Required Field Missing" });
		return false;
	}
	
	return true;
}
/* function fnTrimIt(stringToTrim) 
{
	return stringToTrim.replace(/^\s+|\s+$/g,"");
} */

/*
oElm: Mandatory. This is element in whose children you will look for the attribute.
strTagName: Mandatory. This is the name of the HTML elements you want to look in. Use wildcard (*) if you want to look in all elements.
strAttributeName:  Mandatory. The name of the attribute you’re looking for.
strAttributeValue: Optional. If you want the attribute you’re looking for to have a certain value as well. 
*/
function getElementsByAttribute(oElm, strTagName, strAttributeName, strAttributeValue){
	var arrElements = (strTagName == "*" && oElm.all)? oElm.all : oElm.getElementsByTagName(strTagName);
	var arrReturnElements = new Array();
	var oAttributeValue = (typeof strAttributeValue != "undefined")? new RegExp("(^|\\s)" + strAttributeValue + "(\\s|$)") : null;
	var oCurrent;
	var oAttribute;
	for(var i=0; i<arrElements.length; i++){
		oCurrent = arrElements[i];
		oAttribute = oCurrent.getAttribute && oCurrent.getAttribute(strAttributeName);
		if(typeof oAttribute == "string" && oAttribute.length > 0){
			if(typeof strAttributeValue == "undefined" || (oAttributeValue && oAttributeValue.test(oAttribute))){
				arrReturnElements.push(oCurrent);
			}
		}
	}
	return arrReturnElements;
}

function verifyTimeGeneral(objtimeBox, msg)
{
	var isShowMessage = (msg != null) ? true:false;
	//4 digit check
	var _timeBox = (objtimeBox.value).replace(':','');
	if (!/\d{4}/.test(_timeBox))
	{
		if (isShowMessage)
		{
			alert(msg);
		}
		objtimeBox.value="00:00";
		return
	}

	//  var _timeBox = objtimeBox.value.replace(':',''); // duplicate instantiation (t.s. - commented out 02012014)
	var hour =  fnTrimString(_timeBox).substring(0,2);
	var min = fnTrimString(_timeBox).substring(2,4); 

	//check valid
	if ( parseInt(hour) > 23  ||  parseInt(min) > 59 )
	{
		if (isShowMessage)
		{
			alert(msg);
		}
		objtimeBox.value="00:00";
		return
	}
	//objtimeBox.value = hour + ":" + min;
}

function OpenESWindow(callingform,commentsRequiredFlag,selectionCategoryName)
// get the from, commentsOptionalFlag ( 1 - comments is required / 0 - optional)  and selectionCategoryName as parameters (for not using the category pass "NA" for the selectionCategoryName).
// the function return result array ( result[0] - "PASS"/"FAIL" / result[1] - comments (can be empty) / result[2] - selected category item from the ddl that build dynamically according to the selectionCategoryName )
{ 
    var winStatus, winArguments;
    var result=new Array(); 
    var arrArgs = new Array();
    
    if(selectionCategoryName == null)
    {
       selectionCategoryName = "NA";
    }
     
     if(navigator.userAgent.indexOf("Windows NT 5.1") > -1 && window.clientInformation.appMinorVersion.indexOf("SP2") > -1 )
    {
        winStatus = "dialogHeight: 300px; dialogWidth: 350px;edge: sunken; scroll: No; center: Yes; help: Yes; resizable: No; status: YES;unadorned: Yes;";
    }
    else
    {
       winStatus = "dialogHeight: 300px; dialogWidth:350px;edge: sunken; scroll: No; center: YES; help: No; resizable: No; status: Yes;unadorned: Yes;";
    }               
    
    arrArgs[0] = callingform;
    arrArgs[1] = "esignservlet?txtCommentsRequiredFlag=" + commentsRequiredFlag + "&txtEsignSelectionCategoryName=" + selectionCategoryName; 
    
    result = window.showModalDialog("WinModalSubmit.htm",arrArgs, winStatus); 

    if(result == null)
    {
	result=new Array(); 
	result[0] = "FAIL";
	result[1] = null;
	result[2] = null;
    } 
    return result;
}

function OpenApproversList(callingform)
// get the from as parameters.
// the function return the aprrover user_id in the result[0]. in case of no user selected it returns "-1"
{
    var winStatus, winArguments;
    var result=new Array(); 
    var arrArgs = new Array(); 
    
     if(navigator.userAgent.indexOf("Windows NT 5.1") > -1 && window.clientInformation.appMinorVersion.indexOf("SP2") > -1 )
    {
	winStatus = "dialogHeight: 150px; dialogWidth: 300px;edge: sunken; scroll: Yes; center: Yes; help: Yes; resizable: No; status: YES;unadorned: Yes;";
    }
    else
    {
	winStatus = "dialogHeight: 150px; dialogWidth:300px;edge: sunken; scroll: Yes; center: YES; help: No; resizable: No; status: Yes;unadorned: Yes;";
    }               
    
    arrArgs[0] = callingform;
    arrArgs[1] = "frmActivityLogApproversList.jsp";
    
    result = window.showModalDialog("WinModalSubmit.htm",arrArgs, winStatus); 
    
    if(result == null)
    {
	result=new Array(); 
	result[0] = "-1";
    }
    return result;
}

function resizeFrame(iframe_) 
{ 
    iframe_.style.height = iframe_.contentWindow.document.body.scrollHeight + "px"; 
}

function strStartsWith(str, prefix) 
{    
	return str.indexOf(prefix) === 0;
}

function strEndsWith(str, suffix) 
{     
	return str.match(suffix+"$")==suffix;
}

/* assumption is that chart-defaultive radio buttons are stored in hidden fields id'd with "checked" + radio-group name ("rd"... something)
 * the function iterates on those hidden fields' values and if there are relevant radio-buttons it checks them */
function setChartDefaultiveRadioButtons ()
{
	$('input[type=hidden][name^=checkedrd]').each( function() {
		if( $(this).val() != "" )
		{
			$('#' + $(this).val()).prop('checked', true).attr('checked', 'checked');
		}
	});	
}

/* this function is used for filter screens to disable all filter combo-lists while a doPost is called, so no selections are made when out of focus.
 * An element should have the <SELECT> tag and an attribute toLock (="true", yet the value is irrelevant) in order to be locked 
 * The function also calls submit(), so it replaces the submit() that is usually in the calling function. Fields are enabled on reload */
function lockAll()
{
	var oComboArray = document.getElementsByTagName("select");
	
	for (var i = 0; i < oComboArray.length; i++)
	{
		if (oComboArray[i].toLock != null) // == "true" actually
		{
			oComboArray[i].disabled = true;
		}
	}
	 main.submit();
}

/* works with comboChange() function in filter screens. It receives the same parameters as the comboChange, plus event.keyCode and is called upon
 * onkeyup() event. If the CTRL is up, the original onchange() event is invoked */
function checkCtrlUp (keyCode, oSelect, iValue, includeAll)
{
	 if (keyCode == 17) // ctrl
    { 
        comboChange(oSelect, iValue, includeAll);
    }
}

/* works with comboChange() function in comparison filter screens. It receives the same parameters as the comboChange, plus event.keyCode and is called upon
 * onkeyup() event. If the CTRL is up, the original onchange() event is invoked */
function checkCtrlUpComp (keyCode, oSelect, iValue, includeAll, filter_numbering)
{
	 if (keyCode == 17) // ctrl
    { 
        comboChange(oSelect, iValue, includeAll, filter_numbering);
    }
}

// This function makes validation on filter fields by their input type
function validateFilter(inputType, filterValue)
{
    var forbiddens;
    var result;
    var numOnly = true;
    result = ""; 
	// lower case filter to find easily key-words
	filterValue = filterValue.toLowerCase();
    
    if (filterValue.indexOf("or") > -1)
    {
        filterValue = filterValue.replace("or", "");
    }
    if (filterValue.indexOf("and") > -1)
    {
        filterValue = filterValue.replace("and", "");
    }
    if (filterValue.indexOf("between") > -1)
    {
        filterValue = filterValue.replace("between", "");
        numOnly = false;
    }
	
    if (filterValue.indexOf(">=") > -1 || filterValue.indexOf("<=") > -1 || filterValue.indexOf("> =") > -1 || filterValue.indexOf("< =") > -1)
    {
        alert("Using >= or <= is not allowed.\n Please Use  'OR' Instead");
        return "";
    }
    if (filterValue.indexOf("is between") > -1)
    {
        alert("'is between' is not allowed.\n Please Use  'between' Instead");
        return "";
    }

    var nullVal = "is null";

    if (filterValue.indexOf(nullVal) > -1)
    {
        return result;
    }

    nullVal = "is not null";

    if (filterValue.indexOf(nullVal) > -1)
    {
        return result;
    }

    if (inputType == "N")
    {

        forbiddens = "<>=-."; // forbiddens here stores the legal characters

        if ((filterValue != "") && (numOnly) && (filterValue.indexOf("<") == -1 && filterValue.indexOf("=") == -1 && filterValue.indexOf(">") == -1))
        {
            result = "DispAlDial_Msg_Short_Name_V_50_1"; // kd "Please Use <>=,\"and\",\"Or\",\"between\" And Numric values.";
            return result;
        }
        for (var i = 0; i < filterValue.length; i++)
        { 
            var new_key = filterValue.charAt(i);
            if (((new_key < "0") || (new_key > "9")) && !(new_key == "") && forbiddens.indexOf(new_key) < 0 && !(new_key == " "))
            {
                // alert("wrong:"+new_key);
                result = "DispAlDial_Msg_Short_Name_V_50_2"; // kd 'Wrong Filter Provided.\n Please Enter Numeric Limitations (Use numbers and <>= Signs)\n Or use "is null", "is not null","between".'
                return result;
            }
        }
    }

    if (inputType == "T")
    {
        forbiddens = "#\\@&';=|<>?`\""; // # is parsed as date-value, but is usually a legal char for text-value

        for (var i = 0; i < filterValue.length; i++)
        {
            var new_key = filterValue.charAt(i); //cycle through characters
            if (!(new_key == "") && forbiddens.indexOf(new_key) >= 0)
            {
                result = "DispAlDial_Msg_Short_Name_V_50_3"; // kd 'Illegal characters used in filter. Please Enter Text Limitations Only.\n(For Example,Parts of Requested Names like \"%abc%\" , for phrases containing ..abc..,\n,\"abc%\" - for phrases that starts with abc..,  ';
                return result;
            }
        }
    }

    if (inputType == "D" && filterValue != "")
    {
        forbiddens = "\#<>= /0123456789"; // forbiddens here stores the legal characters
//		var baseMsg = 'Wrong Filter Provided.\n Please Enter Date In The Following Format: \n #DD/MM/YYYY#\n (Surrounded by #, and prefixed by a relational operator, e.g. =, < )\n';

        if (filterValue.indexOf("#") == -1)
        {

            result = "DispAlDial_Msg_Short_Name_V_50_4"; // kd baseMsg + 'Or use "is null", "is not null"';
            return result;
        }
        for (var i = 0; i < filterValue.length; i++)
        {
            var new_key = filterValue.charAt(i); //cycle through characters
            if (((new_key < "0") || (new_key > "9")) && (new_key != "") && (forbiddens.indexOf(new_key) < 0) && (new_key != " "))
            {

                result = "DispAlDial_Msg_Short_Name_V_50_5"; // kd baseMsg + 'Or use "is null", "is not null"';
                return result;
            }
        }
        //code removed here...
        if (filterValue.split(' ').join('').indexOf("/#") > -1)
        {
            result = "DispAlDial_Msg_Short_Name_V_50_6"; // kd baseMsg + '(Remove last slash "/")';
            return result;
        }
    }
    return result;
}

/* clicks a row by id */
function SetSelectedRow (id)
{
	var oRow = document.getElementById("TR" + id); 

	if (oRow)
	{
		oRow.click();
	}
}

//This function gets Array of objects ( objArr = document.getElementsByTagName('input') ) and returns all the hidden parameters as in url
function hiddenParamToString(objArr)
{
   var paramStr = ""; 
	
	for(var i=0; i<objArr.length; i++) 
	{
	  if(objArr[i].type=="hidden") 
	  {
		if(paramStr=='')
		{
		paramStr += objArr[i].name + "=" + objArr[i].value;
		}
		else
		{
		paramStr += "&" + objArr[i].name + "=" + objArr[i].value;
		} 
	  }
	} 
	return paramStr;
}

/* select/unselect all checkboxes according to parameters. 
 * All parameters can be null (isSelectAll default is 'true'), in such a case all checkboxes in the form are manipulated
 * isSelectAll: if 'true' (or null) all relevant checkboxes will be checked, otherwise unchecked (unselect).
 * attName: the value of the to-be-checked check-boxes' name attribute
 * attField: the name of an attribute to be verified for existance (if attValue is null) or value for the check-boxes to be checked
 * attValue: the value of the attribute given in attField to qualify in order for the checkbox to be checked
*/
function selectAll(isSelectAll, attName, attField, attValue)
{
	var hasName = (attName != null) ? true:false;
	var hasField = (attField != null) ? true:false;
	var hasValue = (attValue != null) ? true:false;
	if (isSelectAll == null)
	{ // default value
		isSelectAll = true;
	}
	
	var arrInputs = document.getElementsByTagName("INPUT");
	
	for (var i = 0; i < arrInputs.length; i++)
	{	
		if (arrInputs[i].attributes["type"].value == "checkbox") 
		{ 
			if (hasName && arrInputs[i].attributes["name"].value != attName)
			{
				continue; // doesn't correspond with conditions
			}	
			
			if (hasField && arrInputs[i].attributes[attField] == null)
			{
				continue; 
			}
			
			if (hasValue && hasField && arrInputs[i].attributes[attField].value != attValue)
			{
				continue; 
			}
			
			arrInputs[i].checked = isSelectAll; // true/false
		}		
	}
}

/* opens popup comment window for audit trail, and returns the comment
	maxLen: optional. the maximum chars allowed to be type. send null for unlimited
	the function returns "" (empty string) if the window is closed*/
function setComments(maxLen)
{
	if(navigator.userAgent.indexOf("Windows NT 5.1") > -1 && window.clientInformation.appMinorVersion.indexOf("SP2") > -1 )
	{
		winStatus = "dialogHeight: 370px; dialogWidth: 400px;edge: sunken; scroll: No; center: YES; help: No; resizable: No; status: No;unadorned: Yes;";
	}
	else
	{
	winStatus = "dialogHeight: 340px; dialogWidth: 400px;edge: sunken; scroll: No; center: YES; help: No; resizable: No; status: No;unadorned: Yes;";
	}
	
	var strLen = (maxLen == null) ? "":"?length=" + maxLen; 
	vTextData = window.showModalDialog("MsgBoxCommentRestricted.html" + strLen, null, winStatus);

	if(vTextData != -1)
	{
	   return vTextData;
	}    
	else
	{ 
		return "";
	}   	
}

function authenticateUser(isCommentRequired, formComments, formTitle,obj) // the obj Parameter is for Chrome, The object contain parameters that should passed on to the Callback function
{
	
	showModalDialogForChrome(); //Define showModalDialog for none IE browsers
	var winStatus, winArguments;
	var arrArgs = new Array();
	var title = (formTitle != null) ? formTitle : ""; 
	var showComments = (formComments != null) ? formComments : true; //the default is to display the comment
	
	arrArgs[0] = document.getElementById('main');
	arrArgs[1] = "userauthenticationservlet?commentRequired=" + (isCommentRequired ? 1 : 0) + "&formComments=" + (showComments ? 1 : 0) + "&formTitle=" + title;
	
	winArguments = arrArgs; 
	//winStatus = "dialogHeight: " + (showComments ? "330px" : "200px") + " dialogWidth: 360px;edge: sunken; scroll: No; center: YES; help: No; resizable: No; status: No;unadorned: Yes;"; // old Code - not working on Chrome
	winStatus = "dialogHeight: 300px;dialogWidth: 360px;edge: sunken; scroll: No; center: YES; help: No; resizable: No; status: No;unadorned: Yes;";
		
	var returnshowModalDialogValue= window.showModalDialog("WinModalSubmitWithScrollIE.html", winArguments, winStatus,{onClose:onFinishAuthenticateUserDialog},obj);
	 //If using Internet Explorer 
	if(isOriginShowModalDialog())
    {           
		return returnshowModalDialogValue;
    }
}
//callback function for window.showModalDialog 
function onFinishAuthenticateUserDialog(returnshowModalDialogValue,obj)
{        
	//onFinishAuthenticateUser(returnshowModalDialogValue,obj.firstElement,obj.secondElement);
	onFinishAuthenticateUser(returnshowModalDialogValue,obj);
}

/*  This function invokes e-sign pop-up and returns its value (either null or an array). 
	commentRequired is a boolean parameter for the comment field.
	if workflowId is not sent, it is assumed that the relevant workflow-id is stored in hidden field "txtWorkflowID".
    note: return value is an Array */
function authenticateUserForWorkflowUpdate(isCommentRequired, workflowId)
{
	var winStatus, winArguments;
	var arrArgs = new Array();
	
	if(workflowId != null)
	{
		$("#txtWorkflowID").val(workflowId);
	}	
	
	arrArgs[0] = document.getElementById('main');
	arrArgs[1] = "esignservlet_?commentRequired=" + (isCommentRequired ? 1 : 0);
	
	winArguments = arrArgs;
	winStatus = "dialogHeight: 350px; dialogWidth: 360px;edge: sunken; scroll: No; center: YES; help: No; resizable: No; status: No;unadorned: Yes;";
	
	return window.showModalDialog("WinModalSubmitWithScrollIE.html", winArguments, winStatus);
}

/* FOR FUTURE USE: tries to open modalDialog (IE only), otherwise opens normal pop-up with modal=yes (Mozilla only) */
function modalWin(winName, winArguments, winStatus)
{
	if (window.showModalDialog)
	{
		window.showModalDialog(winName, winArguments, winStatus); // winName = "WinModalSubmitWithScroll.htm"   
	}
	else
	{
		if (winStatus != "")
		{
			winStatus += ",";
		}
		window.open(winName, winArguments, winStatus + "modal=yes");
	}
} 

// Display a messgae in a new window with and 
function popupMessage(message,height,width)
{  
    if(height != null && width != null)
	{
		winStatus = "dialogHeight: " + height + "px; dialogWidth: " + width + "px;edge: sunken; scroll: No; center: YES; help: No; resizable: No; status: No;unadorned: Yes;";
	}
	else if(navigator.userAgent.indexOf("Windows NT 5.1") > -1 && window.clientInformation.appMinorVersion.indexOf("SP2") > -1 )
	{
		winStatus = "dialogHeight: 370px; dialogWidth: 400px;edge: sunken; scroll: No; center: YES; help: No; resizable: No; status: No;unadorned: Yes;";
	}
	else
	{
		winStatus = "dialogHeight: 340px; dialogWidth: 400px;edge: sunken; scroll: No; center: YES; help: No; resizable: No; status: No;unadorned: Yes;";
	}
	  
	window.showModalDialog('MsgBoxMessage.html?message=' + message, null, winStatus); 
}

/* returns true if the value string fits an E-notated number criteria */
function validateENotatedNumber(value)
{
	var reg = /\d+\.?\d*(E{1}-?\d+|\d*)/i; // {at least 1 digit}{maximum 1 comma}{any number of digits}{case-insensitive "E" with at least 1 signed/unsigned digit OR any number of digits}
	var result = reg.exec(value);
	
	if (result == null)
		return false;

	return result.input == result[0]; // check that the match is all of the original string
}

//the function gets filter line info and parse it. 

//parameters:
//parseType - for future use (in case that we will want to parse it in differnt ways)
//tableName - the alias used in the target SQL (if non then use empty string)
//prmType - [N-numeric,T-text,D-date,L-list,B-boolean]
//prmNameTableCol- the column in the DB for the prmName
//prmName - parameter name
//prmIgnoreName - param name that dosn't need filtering 
//prmSign - parameter sign
//prmValTableCol -  the column in the DB for the prmVal
//prmVal - parameter value
//prmDateFormat - the date format incase the prmType is D
//appendToFilter - free text that the function will append to the parse result

//return:
//return parse expression with prefix 1; if succeeded, 0; - with the error if failed

function parseFilter (parseType, tableName, prmType, prmNameTableCol, prmName, prmIgnoreName, prmSign, prmValTableCol, prmVal, prmDateFormat, appendToFilter)
{  
	var parseExpression = "0;"; 
	try
	{
		if(parseType == 1)
		{
			//preparations...
			if(tableName != null && tableName != '')
			{
				tableName += ".";
			} 
			prmVal = prmVal.replace(/\'/g,"_").replace(/\"/g,"_");// not support quotes in the value string
			
			//parse it according to the prmType
			if(prmName == prmIgnoreName || prmVal == null || prmVal == '')
			{
				parseExpression += "( 1=1 )";
			}
			else if(prmType=='N')
			{
				//prmName..
				parseExpression += "(" + tableName + prmNameTableCol + " = '" + prmName + "'";
				//prmVal..
				parseExpression += " and "  + tableName + prmValTableCol + " " + prmSign + " " + prmVal + " " + appendToFilter + ")";
			}
			else if(prmType=='L' || prmType=='T' || prmType=='B')
			{
				//prmName..
				parseExpression += "(" + tableName + prmNameTableCol + " = '" + prmName + "'";
				//prmVal..
				parseExpression += " and "  + tableName + prmValTableCol + " like '" + prmVal + "' " + appendToFilter + ")";
			}
			else if(prmType=='D')
			{
				//prmName..
				parseExpression += "(" + tableName + prmNameTableCol + " = '" + prmName + "'";
				//prmVal..
				parseExpression += " and "  + " to_date( " + tableName + prmValTableCol + ",'" + prmDateFormat + "') " + prmSign + " to_date('" + prmVal + "','" + prmDateFormat + "') " + appendToFilter + ")";
			}
			else
			{
				parseExpression = "1;Error during filter parsing!";
			}
		} 
	}
	catch(e)
	{
		parseExpression = "1;Error during filter parsing!";
	}
	
	return parseExpression;
}

/* Returns a filtered Array (by specific attribute and value) of elements, from a NodeList source. 
 * If no value is given, the existence of the given attribute is sufficient to qualify.
 * @sourceNodeList {NodeList/Array}- a nodeList of elements, probably created with getElementsByName or getElementsByTagName
 * @attrName - the name of the attribute to be verified (by value or existence)
 * @attrValue - the value of the attrName (or a part of it; see below). If null (or ""), the existence of attrName attribute is verified
 * @isPartOfValue {boolean} - if false, attrName.value should be equal to attrValue; otherwise, it's enough if attrName.value contains attrValue.
 * @whichPartOfValue {String} - possible options: "start","end", "contain" (default); 
 * 				if "start" then attrName.value must start with attrValue to qualify, 
 * 				if "end" then attrName.value must end with attrValue to qualify; 
 *				otherwise, attrValue can be any part of attrName.value.
 * NOTE: invoker should deal with null that is returned if sourceNodeList is null.
*/
function filterElementsByAttribute (sourceNodeList, attrName, attrValue, isPartOfValue, whichPartOfValue)
{
	var hasValue = (attrValue != null && attrValue != "") ? true:false;
	var partOfVal = (whichPartOfValue == null || (whichPartOfValue != "end" && whichPartOfValue != "start")) ? "contain":whichPartOfValue;
	var filteredNodeList = new Array(); 
	if (sourceNodeList == null) return null;
	
	for (var i = 0; i < sourceNodeList.length; i++)
	{
		if (sourceNodeList[i].attributes[attrName] != null)
		{
			if (!hasValue)
			{
				filteredNodeList.push(sourceNodeList[i]);
			}
			else if (!isPartOfValue && sourceNodeList[i].getAttribute(attrName) == attrValue)
			{ 
				filteredNodeList.push(sourceNodeList[i]);			
			}
			else if (whichPartOfValue == "contain" && sourceNodeList[i].attributes[attrName].value.indexOf(attrValue) > -1)
			{
				filteredNodeList.push(sourceNodeList[i]);			
			}
			else // whichPartOfValue == ("end" || "start")
			{ 
				var len = attrValue.length;
				if ((whichPartOfValue == "end" && sourceNodeList[i].attributes[attrName].value.slice(-1 * len) == attrValue) ||
					(whichPartOfValue == "start" && sourceNodeList[i].attributes[attrName].value.slice(0, len) == attrValue))
				{
					filteredNodeList.push(sourceNodeList[i]);			
				}				
			}
		}
	}

	return filteredNodeList;
}

/* allows to "parse" nulls as ints, parsing the defVal instead. Useful with comparison when one of the fields might be omitted
 * inStr - string to parse (might be null or NaN)
 * defVal - default value if inStr is unparsable; the function defaults defVal to 0 if it's unparsable
 * defOnNaN - if true a NaN is treated as null; otherwise the function returns NaN as the original parseInt */
function parseIntExtended (inStr, defVal, defOnNaN)
{
	if (defVal == null || isNaN(defVal))
	{
		defVal = 0;
	}
	
	if (inStr == null || inStr.length == 0)
	{
		inStr = defVal;
	}
	
	if (defOnNaN && isNaN(inStr))
	{
		inStr = defVal;
	}
	
	return parseInt(inStr);
}

/* used on paste(also from Edit menu, or mouse click) */
function imposePasteMaxLength(Object, MaxLen)
{ 
	setTimeout(function () {
	 $(Object).val($(Object).val().substr(0, MaxLen));
	  }, 0);
}

/* used on keydown */
function imposeTypeMaxLength(Object, MaxLen)
{
	if ( $(Object).val().length < MaxLen)
	{
		return true;
	}
	return false;
}

function imposeMaxLength(limitField,  limitNum) 
{
    if ($(limitField).val().length > limitNum)
    {
        var text = $(limitField).val().substring(0, limitNum);
        $(limitField).val(text);
    }
}

/* flexible version of validateLegalChar() that is usually invoked on keydown.
 * inKeyCode: key-code to validate, usually sent as event.keyCode 
 * strIllegalChars: a string of concatenated characters that are dimmed illegal and so won't be type if pressed 
 * strLegalChars: used ONLY if strIllegalChars is null. A string of concatenated often-illegal chars exceptioned
 * from the basic validateLegalChar() check */
function validateLegalCharSelective(inKeyCode, strIllegalChars, strLegalChars, e)
{
        var event = e ? e : window.event; 
	var retVal = true;
        
        
        if (strIllegalChars !== null)
	{
		for (var i = 0; i < strIllegalChars.length; i++)
		{
			
                        if (strIllegalChars.charCodeAt(i) == inKeyCode)
			{	// illegal char
				
                                retVal = false;
			}
		}
	}
	else if (strLegalChars != null) // includes != undefined
	{
		for (var i = 0; i < strLegalChars.length; i++)
		{	// check if current key is considered legal
			if (strLegalChars.charCodeAt(i) == inKeyCode)
			{
				retVal = true;
			}
		}
		return validateLegalChar(inKeyCode); // use basic list of illegal chars for validation after legals were exceptioned
	}
        
        if(!retVal)
	{
		event.preventDefault ? event.preventDefault() : (event.returnValue = false);
	}

        return retVal;
}

/* This function is used for jQuery's datepicker, when the format is dd/mm/yyyy, and date field is not "readonly".
 * The datepicker accepts any legal date with slashes (e.g, d/m/yyyy, dd/n/yyyy, etc.) so we only have to insert slashes
 * in the most reasonable places and fill in with 0s. Still, we later have to validate the date (for example, 30/2/2012 is "legal" format, yet illegal date)
 * Practically, the count starts from the end, and assume 4-digit year. 
 * If day+month are given only 3 digits, the month will "receive" 2 if possible (i.e 10 - 12), otherwise the day receives 2 digits.
 * The function both updates the value of the date-field, and returns it - so we can use it directly */
function fnCorrectDateFormat(oDate)
{
	var date = fnTrimString( $(oDate).val() );
	var correctedDate = "", year, month, day;
	var isSlashExist = (date.indexOf("/") > -1) ? true:false;	
	var parsedDate = "";
	
	if (!isSlashExist)
	{			
		// we use slice() because IE doesn't support substr() with negative index. 	
		year = date.slice(-4, date.length); 
		if (date.length > 7) // assumed: dd/mm
		{
			month = date.slice(-6, -4); // as we sliced, date consists of only dd/mm
			day = date.substr(0, 2); 
		}
		else if (date.length > 6) // assumed: d?d/m?m
		{
			if ( date.charAt(date.length-6) === '0' || (date.charAt(date.length-6) === '1' && date.charAt(date.length-5) <= '2') ) // legitimate for a month
			{ // d/mm
				month = date.slice(-6, -4);	
				day = "0" + date.substr(0, 1);
			}
			else
			{ // dd/m
				month = "0" + date.slice(-5, -4);
				day = date.substr(0, 2);
			}
		}
		else // assumed: d/m
		{
			month = "0" + date.substr(1, 1);
			day = "0" + date.substr(0, 1);
		}
		
		correctedDate = day + "/" + month + "/" + year;
	}
	else // date already includes slashes
	{ 
		correctedDate = date;
	}
	
	try {
		parsedDate = $.datepicker.parseDate('dd/mm/yy', correctedDate); 
		$(oDate).datepicker('setDate', parsedDate);	
		correctedDate = $(oDate).val(); // as picker has corrected format
	} catch (e) { 
		// thrown when parser dims invalid date 
		parsedDate = "00/00/0000";
	}
	
	return {corrected: correctedDate, parsed: parsedDate}; // parsed= "00/00/0000" indicates illegal date
}

/*	gets input text element that suppose to hold time format, verifies its validity and tries to format it as HH24:MM format.
 *	returns an object with the above properties:
 *	isValid: true/false - whether the time is valid. 
 *	corrected: if time is valid, returns it in HH24:MM format, otherwise, return the original text from the field	
 *	note: if time is valid, the actual element text is reformatted (if needed) as corrected time.*/
function fnCorrectTimeFormat (oTime)
{
	var time = $.trim( $(oTime).val() );
	var correctedTime = time;
	var isTimeValid = true; 
	
	if (time == null)
	{
		return {corrected: null, isValid: false};
	}
	
	var isColonExist = (time.indexOf(":") > -1) ? true:false;
   
	if( time.length == 3 && !isColonExist || 
		time.length == 4 && isColonExist )
	{
		time = "0" + time; 
	}
	
	if(time.length == 4 && !isColonExist)
	{
		time = time.substr(0, 2) + ":" + time.substr(2);
	}
	else if (time.length != 5)
	{
		isTimeValid = false;
	}

	if ( isTimeValid )
	{
		var timeRegex = /(([01][0-9])|([2][0-3])):[0-5][0-9]/g; 
		if ( !timeRegex.test(time) )
		{
			isTimeValid = false;
		}
		
		if ( isTimeValid )
		{		
			$(oTime).val(time);
			correctedTime = time;
		}
	}
	
	return {corrected: correctedTime, isValid: isTimeValid};
}

/* the function can receive more than 1 element (comma-separated, as separate parameters) to process.
 * it removes any inactive values (according to DB indication), unless it's the selected value - and then it remains but disabled.
 * it relates only to SELECT elements */
function removeInactiveItems( /* 1 SELECT element or more, comma-separated, e.g. $('#ddl1'), $('#ddl2')... */ )
{ 
	if ( arguments.length <= 0)
		return; 
		
	for ( var i in arguments )
	{
		var oSelect = arguments[i];
		if( oSelect.length && $(oSelect).prop('tagName') == 'SELECT' )
		{
			$('option', oSelect).each( function() {
				var isActive = $( this ).attr('active');
				if ( isActive && isActive == 0 )
				{	
					if( $( this ).is(':selected') )
					{
						$( this ).prop('disabled', true); 
					}
					else
					{
						$( this ).remove();
					}
				}
			});
		}
	}
}

/* configure DatePicker 
 * conditions: [From_Date].id = "txtFromDate", name = "FROM_DATE" (related to how servlets gets parameter value)
 * 			   [To_Date].id = "txtToDate", name = "TO_DATE" ( -"- )
 *			   dateFormat must be according to how the servlet parses the date-string
 *			   JQuery's images folder must be in the folder where its CSS are
 * 			   if a string needs to be stored in a different from display-format, a storeFromDate/storeToDate ID elements should be given
 * needed in JSP: 
 * 		create relevant fields (and storing fields if needed) with the relevant names/ids
 *		remove date/time validation of consecutiveness (JQuery limits it already)
 *		if date is deleted altogether, storing field will NOT update - code should be supplemented before submit
 */
function initDatePicker() 
{ 
	var s = document.createElement("script");// ef task 7078
	s.type = "text/javascript";// ef task 7078
	s.src = "js/moment.js";// ef task 7078
	$("head").append(s);	// ef task 7078
	
	$('.date-picker').datepicker({
		showOn: "button",
		dateFormat: "dd/mm/yy",
		buttonImage: "images/calendar.png",
		buttonImageOnly: true,
		changeMonth: true,
		changeYear: true,
		showButtonPanel: true,   
		altFormat: ['dd-mm-yy','ddmmyy'],// ef task 7078
		constrainInput: false, // ef task 7078
		onSelect: function( selectedDate ) 
		{  
		
		}      
   }).on('change', function() { //yp (validation if we allow user to enter date by remove read only from the source input or make it here) ->
//   	var d = validDP($(this).val());  // triggers the validation test
//   	if(d == 'Invalid Date' || $.datepicker.formatDate('ddmmyy', d) != $(this).val().replace(/\//g,'')) {
//   		displayAlertDialog('Invalid Date');
//   		$(this).val('00/00/0000');
//   	} else {
//   		//do nothing
//   	} 
   	validDateWithMomentJS(this);// ef task 7078
   	
   });
	$('.hasDatepicker').prop('readonly', false); // ef task 7078
   
   $('.date-picker').val('00/00/0000');
}

function initDatePickerWithOptionsByClass(cssClass, opt)
{
	var s = document.createElement("script");// ef task 7078
	s.type = "text/javascript";// ef task 7078
	s.src = "js/moment.js";// ef task 7078
	$("head").append(s);	// ef task 7078
	
    var _showOn = "button";
    var _showBP = true; //show button panel
    var _isShowBP = (opt != null && opt.showButtonPanel != null)? opt.showButtonPanel : _showBP;
    var _dateFormat = "dd/mm/yy";
    var _disabled = (opt != null && opt.disabled != null)? opt.disabled : false;
    var _defaultDate = (opt != null && opt.defaultDate != null) ? opt.defaultDate : null;
    var _handler = (opt != null && opt.onSelect != null)? opt.onSelect : onSelectDateDefault; 
	var _handlerBeforeShow = (opt != null && opt.beforeShow != null)? opt.beforeShow : null; 
	var _isRemoveClear = (opt != null && opt.removeClear != null) ? opt.removeClear : false; 

	$("." + cssClass).datepicker
    ({
        showOn: (opt != null && opt.showOn != null)? opt.showOn : _showOn,
        showButtonPanel: _isShowBP,
        dateFormat: (opt != null && opt.dateFormat != null)? opt.dateFormat : _dateFormat,
        buttonImage: "images/calendar.png",
        buttonImageOnly: true,
        changeMonth: true,
        changeYear: true,
        disabled: _disabled,
        defaultDate: _defaultDate,
        altFormat: ['dd-mm-yy','ddmmyy'],// ef task 7078
		constrainInput: false, // ef task 7078
        beforeShow: function(input) 
        {
            if(_isShowBP)
			{
				var objId = $(input).attr('id');
                overrideDefaultButtonPanel(input, objId, _handler, _isRemoveClear);
			}
				
		  	if( _handlerBeforeShow != null)
			{
				return _handlerBeforeShow(input);
			}  
        },
        onSelect: _handler,
        onChangeMonthYear:function( year, month, inst )
        { 
            if(_isShowBP)
			{
				var objId = $(this).attr('id');
                overrideDefaultButtonPanel(inst.input, objId, _handler, _isRemoveClear);
			}
        }
    }).on('change', function() { //yp (validation if we allow user to enter date by remove read only from the source input or make it here) ->
//    	var d = validDP($(this).val());  // triggers the validation test
//    	if(d == 'Invalid Date' || $.datepicker.formatDate('ddmmyy', d) != $(this).val().replace(/\//g,'')) {
//    		displayAlertDialog('Invalid Date');
//    		$(this).val('00/00/0000');
//    	} else {
//    		//do nothing
//    	} 
    	if(validDateWithMomentJS(this))// ef task 7078 // ef defect 3669
		{
    		_handler.apply(this, [$("#" + objId).val()]);
		}
    });
	$('.hasDatepicker').prop('readonly', false); // ef task 7078
}

/*
    Simple DatePicker(with button panel) initialization.
    objId - ID of text field with datepicker
    opt - javascript-object of custom options (OPTIONAL)
        ex: 
        var opt = {
                    'disabled':(variable==0)? true:false,
                    'onSelect': dateChange
                   };
*/

function initDatePickerWithOptions(objId, opt)
{	
	var s = document.createElement("script");// ef task 7078
	s.type = "text/javascript";// ef task 7078
	s.src = "js/moment.js";// ef task 7078
	$("head").append(s);	// ef task 7078
	
    var _showOn = (opt != null && opt.showOn != null)? opt.showOn : "button";
    var _showBP = true; //show button panel
    var _isShowBP = (opt != null && opt.showButtonPanel != null)? opt.showButtonPanel : _showBP;
    var _dateFormat = (opt != null && opt.dateFormat != null)? opt.dateFormat : 'dd/mm/yy';
    var _disabled = (opt != null && opt.disabled != null)? opt.disabled : false;
    var _defaultDate = (opt != null && opt.defaultDate != null) ? opt.defaultDate : null;
    var _handler = (opt != null && opt.onSelect != null)? opt.onSelect : onSelectDateDefault; 
	var _handlerBeforeShow = (opt != null && opt.beforeShow != null)? opt.beforeShow : null; 
	var _isRemoveClear = (opt != null && opt.removeClear != null) ? opt.removeClear : false; 
	var _altField = (opt != null && opt.altField != null) ? opt.altField : null;
	var _altFormat = (opt != null && opt.altFormat != null) ? opt.altFormat : ['dd-mm-yy','ddmmyy'];

	$("#" + objId).datepicker
    ({
        showOn: _showOn,
        showButtonPanel: _isShowBP,
        dateFormat: _dateFormat,
        buttonImage: "images/calendar.png",
        buttonImageOnly: true,
        changeMonth: true,
        changeYear: true,
        disabled: _disabled,
        defaultDate: _defaultDate,
		altField: _altField,
		altFormat: _altFormat,
		constrainInput: false, // ef task 7078
        beforeShow: function(input) 
        {		
            if(_isShowBP)
			{
                overrideDefaultButtonPanel(input, objId, _handler, _isRemoveClear);
			}
				
		  	if( _handlerBeforeShow != null)
			{
				return _handlerBeforeShow(input);
			}  
        },
        onSelect: _handler,
        onChangeMonthYear:function( year, month, inst )
        { 
            if(_isShowBP)
                overrideDefaultButtonPanel(inst.input, objId, _handler, _isRemoveClear);
        }
//                    onClose:function(){
//                        alert('close');
//                    },
//                    onSelect:function(date,input){
//                        //alert(date);
//                        $(this).datepicker('setDate', date);
//                        addClearButton(input);
//                        overrideButtons(input);
//                        this._showDatepicker(input);
//                        
//                    }
    }).on('change', function() { // yp (validation if we allow user to enter date by remove read only from the source input or make it here) ->
//    	var d = validDP($(this).val());  // triggers the validation test
//    	if(d == 'Invalid Date' || $.datepicker.formatDate('ddmmyy', d) != $(this).val().replace(/\//g,'')) {
//    		displayAlertDialog('Invalid Date');
//    		$(this).val('00/00/0000');
//    	} else {
//    		//do nothing
//    	} 
    	if(validDateWithMomentJS(this))// ef task 7078 // ef defect 3669
		{
    		_handler.apply(this, [$("#" + objId).val()]);
		}
    	
    	
    });
	$('.hasDatepicker').prop('readonly', false); // ef task 7078
}
function validDateWithMomentJS(date)// ef task 7078
{	
	var dateFormat = 0;
	var firstFormat = moment(date.value,"DD/MM/YYYY",true);	
	var secondFormat  = moment(moment(date.value,"DD-MM-YYYY",true).format("DD/MM/YYYY"),"DD/MM/YYYY",true);
	if(firstFormat.isValid())
	{
		dateFormat = firstFormat;
	}
	else if(secondFormat.isValid())
	{
		dateFormat = secondFormat;
	}	
	if ((dateFormat == 0) || (dateFormat.format('YYYY')=="0000"))
	{
		displayAlertDialog('Invalid Date');
		$(date).val('00/00/0000');
		return 0;
	}

	if(parseInt(dateFormat.format('YYYY')  /1000) == 0)
	{
		if(parseInt(dateFormat.format('YYYY')  /100) != 0)
		{
			displayAlertDialog('Invalid Date');
			$(date).val('00/00/0000');
			return 0;
		}
		dateFormat.add(2000,'years')
		$(date).val(dateFormat.format('DD/MM/YYYY'));
	}
	
	return 1;
}

function validDP(value) {
	var date = value.split("/");
    var d = parseInt(date[0], 10),
        m = parseInt(date[1], 10),
        y = parseInt(date[2], 10);
    return new Date(y, m - 1, d); 
}
 

function overrideDefaultButtonPanel(input, objId, handler, isRemoveClear)//input - HTMLInputElement Object
{
	var _handler = (handler == null) ? onSelectDateDefault : handler;
	
    setTimeout(function() 
    {
         var btnWidget = $( input ).datepicker( "widget" ); 
            /* override 'Today' button */ 
             btnWidget.find('.ui-datepicker-current').removeClass('ui-priority-secondary').addClass('ui-priority-primary')
             .text('Today')
             .click(function() 
             {				                
                $(input).datepicker('setDate', new Date()).datepicker('hide');
                $('.ui-datepicker-current-day').click();
             });
             
			if( isRemoveClear )
			{
				btnWidget.find('.ui-datepicker-close').remove();
			}
			else
			{            
				/* override 'Done' button */ 
				btnWidget.find('.ui-datepicker-close')
				.text('Clear')
				.click(function() 
				{
					$.datepicker._clearDate(input);
					$(input).datepicker( "hide" );     
					//$(input).datepicker('option', 'onSelect');			
					$('#' + objId).val("00/00/0000");             
				});
			}
     }, 1);
     
     /* 
     * create and join new button to button panel
     *
     setTimeout(function()
     {
        var buttonPane = $( input ).datepicker( "widget" ).find( ".ui-datepicker-buttonpane" ); 
        
        var btn = $('<button id="clearButton" class="ui-datepicker-current ui-state-default ui-priority-primary ui-corner-all" type="button">Clear<//button>');
        btn
        .unbind("click") 
        .bind("click", function () 
        { 
            $.datepicker._clearDate(input);
            $(input).datepicker( "hide" );
            //alert(objId);
            $('#' + objId).val("00/00/0000"); 
        });                    
        // Check if buttonPane has that button                    
        if(buttonPane.has('#clearButton').length==0)
        btn.appendTo( buttonPane );
        
     }, 1); */
     //return {};
}

function onSelectDateDefault()
{	
    return;
}

/* beta: create and attach div to be used with $.dialog() for user audit-trail comments 
 * the function creates, attaches and initializes the comment pop-up with $.dialog()
 * it returns the JQuery object of the dialog, and it should be kept in a global variable (otherwise, direct call to its id should be used)
 * In order to use the comment-dialog openCommentDialogAndCommit() function should be used, 
 * as it provides a handler to commit on 'OK' */
function initUserCommentDiv()
{	
	var bodyElem = $('body')[0]; //document.getElementsByTagName('body')[0];
	if (bodyElem == null)
		return;
	
	var eDiv = document.createElement('div');
	$(eDiv).attr('id', 'divUserCommentDialog')
		   .attr('title', 'Comment')
		   .data('maxLength', '-1');
	
	var eTable = document.createElement('table');
	$(eTable).css({'text-align':'center', 'width':'100%'})
			.append('<tr><td><br/></td></tr>')
			.append($('<tr>')							
							.append($('<td>')
											.css('text-align','left')
											.append($('<textarea>').attr('rows', 11)
																	.attr('id', 'txaUserComment')
																	.css({'width':'300px', 'margin-left':'10px', 'margin-right':'10px'}))
							)
			);
	
	eDiv.appendChild(eTable); 	
	bodyElem.appendChild(eDiv);
	
	// set dialog properties
	$( '#divUserCommentDialog' ).dialog({
		autoOpen: false,
		height: 330,
		width: 360,
		modal: true,		
		buttons: {
			OK: function() { 
					var _max = $(this).data('maxLength'); 
					var _handler = $(this).data('okHandler');
					var _comment = $.trim( $('#txaUserComment').val() );
					var _mandatory = $(this).data('commentMandatory'); 
					
					if( _max > 0 && _comment.length > _max)
					{
						displayAlertDialog(DispAlDial_Msg_Short_Name_V0_44, {replaceme:[{key:0,val:_max}]}); // kd 'The comment length should be up to ' + _max + ' chars'
						return false;
					}
					
					if (_mandatory && _comment == '' )
					{
 						displayAlertDialog("DispAlDial_Msg_Short_Name_T_146", { title:'Required Field Missing' });						
						return false;
					}
					else if(_comment != '' && $(this).data('validateComment'))
					{
						if( !fnValidateString({inStr:_comment, fieldName:"Comment", legals:$(this).data('legalChars')}) )
						{           
							return false;
						} 
					}
					
					$(this).data('comment', _comment);
					if( _handler != null && typeof(_handler) == 'function' )
					{
						var _params = $(this).data('okHandlerParams');
						if ( $(this).data('incComment') )
						{
							_params.push( $(this).data('comment') );
						}
						
						_handler.apply(null, _params);
					}
					$( this ).dialog( "close" );
				},
			Cancel: function() {
					var _handler = $(this).data('cancelHandler');
					
					if( _handler != null && typeof(_handler) == 'function' )
					{
						_handler.apply(null, $(this).data('cancelHandlerParams'));
					}
					$( this ).dialog( "close" );
				}
			}
	});	
	
	return $( '#divUserCommentDialog' );
}

/* This function MUST be called in order to attach a comment to a process because the handler in case of cancel/close
 * is sent along. The function initiate confirmation handler, max text length and opens the dialog.
 * @Params: objParams - an object containing all/some of the relevant parameters. Used to add cancel-button-handler. USED ALONE without other parameters
 *			maxLength - maximum characters for comment; negative or 0 to indicate no chars limit
 *			handler - the local function to be called if OK was clicked (and comment length is between 1 and maxLength)
 *			params - [optional] an array of parameters to pass the handler function.
 *			isRetry - [optional] boolean. true - keep existing comment (used in case of failure response from server-side), false (default) - clear text. */
function openCommentDialogAndCommit(maxLength, handler, params, isRetry, objParams)
{
	var oDialog = $( '#divUserCommentDialog' );
	// check if dialog refers to the correct DIV
	if(oDialog.length == 0)
		return;
	
	var _passParams = false, 
		_clearComment = false, 
		_okHandler = null, 
		_maxLength = -1,
		_okHandlerParams = [],
		_cancelHandler = null, 
		_cancelHandlerParams = [],
		_includeCommentAsParam = false;
		_validateComment = false;
		_legalChars = null;
		_isMandatory = true;
		
	if(arguments.length == 1 && typeof arguments[0] == 'object') 
	{
		var obj = arguments[0];
		_maxLength = (obj.maxLength == null) ? -1 : obj.maxLength;
		_okHandler = (obj.okHandler == null) ? null : obj.okHandler; 
		_includeCommentAsParam = (obj.includeComment == null) ? false : obj.includeComment;
		_okHandlerParams = (obj.okHandlerParams == null) ? [] : obj.okHandlerParams;		
		_cancelHandler = (obj.cancelHandler == null) ? null : obj.cancelHandler; 
		_cancelHandlerParams = (obj.cancelHandlerParams == null) ? [] : obj.cancelHandlerParams;
		_clearComment = (obj.clear == null) ? true : obj.clear;
		_validateComment = (obj.validateComment == null) ? false : obj.validateComment;
		_legalChars = (obj.legalChars == null) ? _legalChars : obj.legalChars;
		_isMandatory = (obj.isMandatory == null) ? _isMandatory : obj.isMandatory;
	}
	else 
	{ // more than only an object sent (so object should be omitted!)
		_maxLength = maxLength;
		_okHandler = handler;
		
		// check if 3rd parameter is an array (used as parameters for handler call)
		if( arguments.length >= 3 && (arguments[2] instanceof Array) )
		{ 
			_okHandlerParams = params;
			_passParams = true;
		}
		
		if( arguments.length == 2 || // isRetry is omitted
			(((arguments.length == 3 && _passParams == false) || arguments.length == 4 ) // isRetry parameter was sent (3rd/4th place)
			&& ( arguments[arguments.length-1] == null || !arguments[arguments.length-1] ))) // isRetry == null OR == false
		{
			_clearComment = true;
		}
	}
	
	if( _clearComment )
	{
		$('#txaUserComment').val('');
		oDialog.data('comment', '');
	}
	$('#txaUserComment').attr('onkeyup', 'imposeMaxLength(this,'+_maxLength+')')
                                           .attr('onpaste', 'imposeMaxLength(this,'+_maxLength+')');
        
	oDialog.data('maxLength', _maxLength)
		   .data('okHandler', _okHandler)
		   .data('okHandlerParams', _okHandlerParams)
		   .data('cancelHandler', _cancelHandler)
		   .data('cancelHandlerParams', _cancelHandlerParams)
		   .data('incComment', _includeCommentAsParam)
		   .data('validateComment', _validateComment)
		   .data('legalChars', _legalChars)
		   .data('commentMandatory', _isMandatory)
		   .dialog('open');
}

/* beta: create and attach div to be used with $.dialog() as alert */
var alertDialog; 
$(function ()
{	
	var bodyElem = $('body')[0]; //document.getElementsByTagName('body')[0];
	if (bodyElem == null)
		return;
	
	var eDiv = document.createElement('div');
	$(eDiv).attr('id', 'divAlertDialog')
		   .attr('title', 'Alert');		   
	
	var eInnerDiv = document.createElement('div');
	$(eInnerDiv).attr('id', 'divAlertText').css('padding','7px')
	
	eDiv.appendChild(eInnerDiv); 	
	bodyElem.appendChild(eDiv);
	
	// set dialog properties
	$( '#divAlertDialog' ).dialog({
		autoOpen: false,
		show: 0,		
		modal: true,		
		close: function( e, ui ) {
			var focusId = $(this).data('hasFocus');
			var iFrame = $(this).data('focusInFrame');
			
			if( focusId )
			{
				if( iFrame && iFrame != '' )
				{
					$('#' + iFrame).contents().find('#' + focusId).focus();
				}
				else
				{
					$('#' + focusId).focus();
				}
			}
			// clear data so next alert won't focus on the wrong element
			$(this).data('hasFocus', '');
			$(this).data('focusInFrame', '');
		}		
	});	
	
	$( '#divAlertDialog' ).parent().on('keydown', 'button', function(e) {	
		if( e.which == 13 )
		{ 			
			$( '#divAlertDialog' ).dialog('close');
			return false;
		}
	});
	
	alertDialog = $( '#divAlertDialog' );
});

function displayAlertDialog(message, title, obj)
{
	var _title = null, _obj = null,
	_message = "", // kd 18052016 added multilingual
	dict = {}; // kd 06072016 added for using with variables in message lables
	
	if( arguments.length === 3 )
	{
		_title = title;
		_obj = obj;
	}
	else if( arguments.length === 2 )
	{
		if( typeof arguments[1] == 'object' )
		{
			_obj = arguments[1];
		}
		else
		{
			_title = arguments[1];
		}
	}
	
	if( _obj != null )
	{
		$( '#divAlertDialog' ).data('hasFocus', (_obj.focusOn != null ? _obj.focusOn : ''));
		$( '#divAlertDialog' ).data('focusInFrame', (_obj.frameId != null ? _obj.frameId : ''));
		if( _title == null && _obj.title != null )
		{
			_title = _obj.title;
		}
	}
	if (typeof jsonData != "undefined") {
		_message = jsonData[message]; // kd 18052016
		if( _title != null ) // kd 28082016
		{
			_title = jsonData[_title]; 
		}
	} 
	if ( _message != "" && _message != null )
		{
			// kd 06072016  
			if ( _obj != null &&  _obj.replaceme != null )
			{
				dict = _obj.replaceme;
				for ( i = 0; i < dict.length; i++ ) 
				{
					_message = _message.replace( "{" + dict[i].key + "}", dict[i].val);
				}
			}
			// kd end
			$( '#divAlertDialog div' ).html(_message);
		}
	else
		{
			$( '#divAlertDialog div' ).html(message);
		}
	$( '#divAlertDialog' ).dialog('option', 'title', (_title != null) ? _title : 'Alert').dialog('open');
}

function clearCommentDialog()
{
	$('#txaUserComment').val('');
	$('#divUserCommentDialog').data('comment', '');
}

function initConfirmDialogDiv()
{	
	var bodyElem = $('body')[0]; //document.getElementsByTagName('body')[0];
	if (bodyElem == null)
		return;
	
	var eDiv = document.createElement('div');
	$(eDiv).attr('id', 'divConfirmDialog')
		   .attr('title', 'Please Confirm');		   
	
	var eInnerDiv = document.createElement('div');
	$(eInnerDiv).attr('id', 'divConfirmMessage').css('padding','7px');
	
	eDiv.appendChild(eInnerDiv); 	
	bodyElem.appendChild(eDiv);
	
	// set dialog properties
	$( '#divConfirmDialog' ).dialog({
		autoOpen: false,
		show: 0,		
		modal: true, 
		buttons: {
			Confirm: function() { 					
					var _handler = $(this).data('confirmHandler');					
						 
					$( this ).dialog( "close" );	
					 
					if( _handler != null && typeof(_handler) == 'function' )
					{
						_handler.apply(null, $(this).data('confirmHandlerParams'));
					}		
				},
			Cancel: function() {
					var _handler = $(this).data('cancelHandler');					
							
					$( this ).dialog( "close" );
					
					if( _handler != null && typeof(_handler) == 'function' )
					{
						_handler.apply(null, $(this).data('cancelHandlerParams'));
					}  
				}
			}		
	});	
	
	return $( '#divConfirmDialog' );
}

function replace(errMsg) // kd 08082016 for multilanguage DisplayError 
{
	if ( typeof jsonData != "undefined" && errMsg != null )
	{
		 errMsg = jsonData[errMsg]; 
		 return errMsg;
	}
}

function openConfirmDialog(obj)
{
	var oDialog = $( '#divConfirmDialog' );
	// check if dialog was instantiated
	if(oDialog.length == 0)
		return;
		
	var _message = (obj.message == null) ? "Are you sure?" : obj.message, 
		_okHandler = (obj.onConfirm == null) ? null : obj.onConfirm, 
		_okHandlerParams = (obj.onConfirmParams == null) ? [] : obj.onConfirmParams, 
		_cancelHandler = (obj.onCancel == null) ? null : obj.onCancel, 
		_cancelHandlerParams = (obj.onCancelParams == null) ? [] : obj.onCancelParams, 
		_title = (obj.title == null) ? "Please Confirm" : obj.title,
		_replaceme = (obj.replaceme == null) ? null : obj.replaceme, // kd 08082016 for using with variables in labels of message
		msg; // for use multilanguage
		
		// kd 08082016  
		if (typeof jsonData != "undefined") {
			msg = jsonData[_message]; 
			// kd 22112016
			if (_title != null) {
				tempVar = _title;
				_title = jsonData[_title];
				if (typeof(_title) == "undefined") {
					_title = tempVar;
				}
			} // kd end
		}
		
		if ( msg != "" && msg != null )
		{
			if ( _replaceme != null )
			{
				for ( i = 0; i < _replaceme.length; i++ ) 
				{
					msg = msg.replace( "{" + _replaceme[i].key + "}", _replaceme[i].val);
				}
			}
			$( '#divConfirmDialog div' ).html(msg);
		}
		// kd end
		else
		{
			// set message
			$( '#divConfirmDialog div' ).html(_message);
		}
		
	// set parameters and open dialog
	oDialog.data('confirmHandler', _okHandler)
		   .data('confirmHandlerParams', _okHandlerParams)
		   .data('cancelHandler', _cancelHandler)
		   .data('cancelHandlerParams', _cancelHandlerParams)
		   .dialog('option', 'title', _title)
		   .dialog('open');
}

/*  converts plain text (e.g. result comment from db) into html assimilate-able string to show, for instance, in new alert pop-up 
	using MAP object to traverse the string only once 
	' is replaced with \\' otherwise jQuery formats &#39; (previously &apos;) back to ' within HTML */
var MAP = { '<': '&lt;',
			'>': '&gt;',
			'"': '&quot;',
			"'": "\\'", 
			'\n': '<br/>'
		};

function convertTextToHtml( text )
{ 
	return text.replace(/["'<>\n]/g, 
						function(m) {							
							return MAP[m];
						});
}

function slashDate( date )
{
	if( date.indexOf("/") > -1 || date.length != 8 )
	{
		return date;
	}
	
	return [date.slice(0, 2), date.slice(2, 4), date.slice(4)].join('/');
}

function validateEmailAddress(email)
{
	/* 	code is taken from (and explained) here: http://www.w3resource.com/javascript/form/email-validation.php
		with the addition of the legal (according to RFC) chars: ! # $ % & ' * + – / = ? ^ ` { | } ~
	*/
	if( !/^[\w\$\*\+\/\?\^\|\{\}!#%&'`=\-~]+([\.]?[\w\$\*\+\/\?\^\|\{\}!#%&'`=\-~]+)*@\w+([\.-]?\w+)*(\.\w{2,6})+$/.test(email) )
	{
		return false;
	}
	
	var parts = email.split('@');
	// validate personal info and domain lengths
	if( parts[0].length > 64 || parts[1].length > 255 )
	{
		return false;
	}
	
	return true; 
}

//"
 /*
    used by Skyline Charts & Reports Windows for open charts/reports in a new window
    jsp must import CommonDBFuncs.js also.
*/
    var chart_show_count = 0;
    var globalLimit = 0;
    
    function openReportChartWindow(paramsObj)
    {
            var _winHeight = (paramsObj != null && paramsObj.winHeight != null) ? paramsObj.winHeight : 700;
            var _winWidth = (paramsObj != null && paramsObj.winWidth != null) ? paramsObj.winWidth : screen.availWidth - 100;
            var _winLeft = (paramsObj != null && paramsObj.winLeft != null) ? paramsObj.winLeft : 10;
            var _winTop = (paramsObj != null && paramsObj.winTop != null) ? paramsObj.winTop : 10;            
            var _childAction = (paramsObj != null && paramsObj.childAction != null) ? paramsObj.childAction : "";
            var _parentAction = (paramsObj != null && paramsObj.parentAction != null) ? paramsObj.parentAction : "";
            var _windowName = "WinToSubmitChart";            
            var _type = (paramsObj != null && paramsObj.type != null) ? paramsObj.type : "chart";                        
            if(_type == "report") // limit = 0, should be opened in the same window
            {
                _windowName = "WinToSubmit"+_parentAction;
            }
            else
            {
                globalLimit =  (globalLimit == 0) ? getInSession("CHARTS_OPEN_WINDOWS_LIMIT") : globalLimit;
            }
            
            var winStats, oNewWindow;
            winStats  = "toolbar=no,location=no,directories=no,menubar=no,resizable=yes,status=yes,edge=sunken,scrollbars=1";
            winStats += ",width=" + _winWidth;
            winStats += ",height=" + _winHeight;
            winStats += ",left=" + _winLeft;
            winStats += ",top=" + _winTop;
            
            if(globalLimit != 0)
            {
                
                chart_show_count = (chart_show_count == 0)?getInSession("CHARTS_OPEN_WINDOWS_COUNT") : chart_show_count;
                
                if(chart_show_count >= globalLimit)
                {
                    chart_show_count = 1;
                }
                else
                {
                    chart_show_count ++;
                }
                saveInSession("CHARTS_OPEN_WINDOWS_COUNT", chart_show_count);
            }
            //alert(chart_show_count);
            
            if(isAlive() == 1) 
            {
                    oNewWindow = window.open("", _windowName+"_"+chart_show_count, winStats);
                    $('#main').attr('target', _windowName+"_"+chart_show_count);
                    $('#main').attr('action', _childAction);			
                    $('#main').submit();
                    oNewWindow.focus();
                    $('#main').attr('target', "");
                    $('#main').attr('action', _parentAction);
            }
            else
            {
                    $('#main').attr('action', _parentAction);
                    $('#main').submit();
            }
    }
    
    
/* the function creates and attaches div to be used as wait message pop up with background(mask) 
* 
*/
    function initWaitMessageDiv(obj)
    {
        var bodyElem = $('body')[0]; 
	if (bodyElem == null)
		return;
        
        var _obj = null;
        if(arguments != null && typeof arguments[0] == 'object' )
        {
                _obj = arguments[0];
        }
        var _width =  (_obj != null && _obj.width != null) ? _obj.width :300;
        var _height =  (_obj != null && _obj.height != null) ? _obj.height :100;
        
        var _left_default = ($(document).width()/2) - (_width/2);
        var _top_default = ($(window).height()/2) - (_height);
        //alert($(window).height() + " : "+ $(document).height());
        var _left = (_obj != null && _obj.left != null) ? _obj.left :_left_default;
        var _top = (_obj != null && _obj.top != null) ? _obj.top : _top_default ;
            
	var firstDiv = document.createElement('div');
	$(firstDiv).attr({'id':'mask', 'class':'pageDefaultMask'}).append();
        bodyElem.appendChild(firstDiv);
        
        var secDiv = document.createElement('div');
        $(secDiv).attr({'id':'hiddenformMessage', 'class':'cssWaitMessageDiv'})
                 .on('click', hideWaitMessage).css({'top':_top,'left':_left, 'width':_width, 'height':_height});

        $(secDiv).append($('<h2>').attr({'id':'waitMessage','class':'cssUserMessage'}))
                                  //.text('Please wait....'))
                 .append($('<img>').attr('src', '../skylineFormWebapp/images/circular.gif'));	 	
	bodyElem.appendChild(secDiv);
    }
    
    function showWaitMessage(obj)
    {
        var _obj = null;
        var _text = null;
        
        if( typeof arguments[0] == 'object' )
        {
                _obj = arguments[0];
        }
        else
        {
        	if (typeof jsonData != "undefined") {
                _text = (arguments[0] != null && arguments[0].length != "") ? jsonData["Processing"] : jsonData["Please_Wait"]; // kd 1072016 for multilingual purpose
        	}
        	else
        	{
        		_text = (arguments[0] != null && arguments[0].length != "") ? arguments[0] : "Please wait..."; // kd 1072016 only this string was before adding if..else condition
        	}
        }
        
        $('#waitMessage').text(_text);
        
        $("#mask").show();
        $("#hiddenformMessage").show();
    }
    
    function hideWaitMessage()
    {
        $("#hiddenformMessage").hide();
        $("#mask").hide();
    }    
	
	/*
		return seriesLists separated by ';' based on obj and current SeriesSettingList (if the series exists in crntSeriesSettingList we keep the series definition from there)
		valType - if 'val' we use .val() as the series name from the object element
				  else if 'text' we use .text() as the series name from the object element
				  else the value from the object iterator is been used
	*/
	function getGenericSeriesSetting(obj, crntSeriesSettingList, valType)
    { 
		var sSeriesSettingList = ""; 
        var crntColor,crntShape,crntOrder,crntLinewidth,crntCurve,crtRangeMin,crtRangeMax,crtRangeAuto,crntPrecision,crntTickSize,crntAxisTypeLog, crntRegression, crntAvg, crntStd, minX, maxX, displayName, spec_alert, spec_action;
	    var crntSeriesarray;
        var newSeriesSettingList;
        var index;
        var beginIndex;
        var endIndex
        var i;
		var val_;
        
        $(obj).each( function(i ,val)
        {
				if(valType == 'val')
				{
					val_ = $(this).val();
				}
				else if(valType == 'text') 
				{
					val_ = $(this).text();
				}
				else
				{
					val_ = val;
				}
				 
                if(sSeriesSettingList != "")
                {                    
                    sSeriesSettingList = sSeriesSettingList + ";"
                }
                index = (";" + crntSeriesSettingList).indexOf(";" + val_ + ",", index + 1); //add index + 1 for skip last match (to deal with duplication in crntSeriesSettingList)
				
                if(index > -1)
                {
                   endIndex = crntSeriesSettingList.indexOf(";" , index)
                   if (endIndex > -1 )
                   {
                        crntSeriesarray = crntSeriesSettingList.substring(index,endIndex).split(",");
                   }
                   else
                   {
                         crntSeriesarray = crntSeriesSettingList.substring(index).split(",");
                   }
                   
				   crntOrder = crntSeriesarray[1];
                   crntColor = crntSeriesarray[2];
                   crntShape = crntSeriesarray[3];
                   crntLinewidth = crntSeriesarray[4];
                   crntCurve = crntSeriesarray[5];
                   crtRangeMin = crntSeriesarray[6];
                   crtRangeMax = crntSeriesarray[7];
                   crtRangeAuto = crntSeriesarray[8];
                   crntPrecision = crntSeriesarray[9];
                   crntTickSize = crntSeriesarray[10];
				   crntAxisTypeLog = crntSeriesarray[11];
				   crntRegression = crntSeriesarray[12];
				   crntAvg = crntSeriesarray[13];
				   crntStd = crntSeriesarray[14];  
				   minX = crntSeriesarray[15];  
				   maxX = crntSeriesarray[16];  
			       displayName = crntSeriesarray[17];  
			       spec_alert = crntSeriesarray[18];  
			       spec_action = crntSeriesarray[19];   
                }
                else
                { 
					crntOrder = "";
                    crntColor = "undefined";
                    crntShape = "0";
                    crntLinewidth = $('#txtDefaultSeriesLineWidth').val();
                    crntCurve = "unchecked";
                    crtRangeMin ="";
                    crtRangeMax ="";
                    crtRangeAuto = "checked";
                    crntPrecision = "";
                    crntTickSize = ""; 
					crntAxisTypeLog = "";
					crntRegression = "0";
					crntAvg = "unchecked";
				    crntStd = "unchecked";
					minX = ""; 
				    maxX = ""; 
			        displayName = ""; 
			        spec_alert = "unchecked"; 
			        spec_action = "unchecked";  
                }
                  sSeriesSettingList = sSeriesSettingList + val_ + "," + crntOrder + "," + crntColor + "," + crntShape + "," + crntLinewidth + "," + crntCurve 
                                            + "," + crtRangeMin + "," + crtRangeMax + "," + crtRangeAuto + "," + crntPrecision + "," +crntTickSize + "," + crntAxisTypeLog + "," + crntRegression
											+ "," + crntAvg + "," + crntStd + "," + minX + "," + maxX + "," + displayName + "," + spec_alert + "," + spec_action;
        });
          
		return sSeriesSettingList;
    }
	
	// return 1 if we there is general series with regression in crntSeriesSettingList
	function isGenericSeriesWithRegressionExists(crntSeriesSettingList)
    { 
		var serArray = crntSeriesSettingList.split(';');
		for (var i = 0; i < serArray.length; i++)
		{ 
			if(crntSeriesSettingList.split(',')[12] != 0)
			{
				return true;
			}
		} 
		return false; 
	}
	
	function checkDirection (str, objId)
    {   
        
        if(str != null || str != "")
        {
            if( checkRtl(str) ) 
            {                         
                $('#' + objId).removeClass("cssTableCellLTR").addClass("cssTableCellRTL");
            } 
            else 
            {
                $('#' + objId).removeClass("cssTableCellRTL").addClass("cssTableCellLTR");
            }; 
        }
    }
    
    function checkRtl( character ) 
    {
        var ltrChars            = 'A-Za-z\u00C0-\u00D6\u00D8-\u00F6\u00F8-\u02B8\u0300-\u0590\u0800-\u1FFF'+'\u2C00-\uFB1C\uFDFE-\uFE6F\uFEFD-\uFFFF';
        var rtlChars            = '\u0591-\u07FF\uFB1D-\uFDFD\uFE70-\uFEFC';
        //var ltrDirCheckRe       = new RegExp('^[^'+rtlChars+']*['+ltrChars+']');
        var rtlDirCheckRe       = new RegExp('^[^'+ltrChars+']*['+rtlChars+']');
    
        return rtlDirCheckRe.test(character);
    };
	
	/* show BACK icon in the header of the page (from PageHeaderNew.inc) */
	function showBackIcon(href)
	{
		$('#headerShowBackIcon').attr('href', href);
		$('#headerShowBackIcon').show();
	}
	
	// Check if user using IE
	function isOriginShowModalDialog() 
	{
	    var ua = window.navigator.userAgent;
	
	    var msie = ua.indexOf('MSIE ');
	    if (msie > 0) {
	        // IE 10 or older => return version number
	        return parseInt(ua.substring(msie + 5, ua.indexOf('.', msie)), 10);
	    }
	
	    var trident = ua.indexOf('Trident/');
	    if (trident > 0) {
	        // IE 11 => return version number
	        var rv = ua.indexOf('rv:');
	        return parseInt(ua.substring(rv + 3, ua.indexOf('.', rv)), 10);
	    }
	
	    var edge = ua.indexOf('Edge/');
	    if (edge > 0) {
	       // Edge (IE 12+) => return version number
	       return parseInt(ua.substring(edge + 5, ua.indexOf('.', edge)), 10);
	    }
	
	    // other browser
	    return false;
	}
	
	
	//Define showModalDialog for none IE browsers
	function showModalDialogForChrome()
	{	
	    window.showModalDialog = window.showModalDialog || function(url, arg, opt, objOnClose,obj) {
	        url = url || ''; //URL of a dialog
	        arg = arg || null; //arguments to a dialog	       
	        opt = opt || 'dialogWidth:300px;dialogHeight:100px'; //options: dialogTop;dialogLeft;dialogWidth;dialogHeight or CSS styles	       
	        var caller = showModalDialog.caller.toString();	        
	        if (parent.parent.document.getElementById('dialog1')!= null ) {	// IF Dialog opened in an exist Dialog	  
		        parent.parent.document.getElementById('dialog-close').style.visibility = "hidden";
	        }
	        var dialog = parent.document.body.appendChild(document.createElement('dialog'));
	        dialog.setAttribute("id", "dialog1");
	        dialog.setAttribute('style', opt.replace(/dialog/gi, ''));
	        var myHtml='<a href="#" id="dialog-close" style="position: absolute; top: 0; right: 4px; font-size: 20px; color: #000; text-decoration: none; outline: none;">&times;</a><iframe id="dialog-body" src="' + url + '" style="border: 0; width: 100%; height: 100%;"></iframe>';
	      
	        dialog.innerHTML = myHtml;
	        parent.document.getElementById('dialog-body').contentWindow.dialogArguments = arg;
	        dialog.style.border="solid 4px #2779aa";	  
	       
	       parent.document.getElementById('dialog-close').addEventListener('click', function(e) {
	            e.preventDefault();	
	            dialog.close();
	        });	
	        dialog.showModal();	        
	       
	        dialog.addEventListener('close', function() {
	        	 if (parent.parent.document.getElementById('dialog1')!= null ) {	// IF Dialog opened in an exist Dialog	  		  
	 		        parent.parent.document.getElementById('dialog-close').style.visibility = "visible";
	 	        }
	        	var returnValue = parent.document.getElementById('dialog-body').contentWindow.returnValue;
        		parent.document.body.removeChild(dialog);
        		if(objOnClose != null)//Callback function
        			objOnClose.onClose.apply(this, [returnValue,obj]);
        		else return returnValue;
	        });	     
	    };
	}
	 //Closing dialog for Chrome
	function closeDialog()
    {		
		 parent.parent.document.getElementById('dialog1').close();
    }	
	//FixHeaders for none 'dataTables' tables
	function fixHeaderForTables(TableId)
	{	//TASK-6698
		//Before using this function you should append to your jsp:
		//<script src="js/comply_js/jquery.floatThead.js" type="text/javascript"></script>	
		//and
		//<meta http-equiv="X-UA-Compatible" content="IE=10; IE=9; IE=8; IE=7; IE=EDGE" />
		//and 
		//add class 'wrapper' to the container div with the overflow
		
		
		
		//use when thead is missing
		var  str = $('#' + TableId + ' tr:first').html();
		var res;
		if( typeof str === 'undefined' || str === null ){
		    res="";
		}
		else
		{
			res= str.replace(/td/gi, "th");
		}		
		res = '<thead>' + res;
		res+='</thead>';		
		$('#' + TableId + ' tr:first').remove();
		$('#' + TableId).prepend(res);			
		
		
		
		var $demoTable = $('#' + TableId);
		   $demoTable.floatThead({                
               scrollContainer: function($table){
                  return $table.closest('.wrapper');                	                     
               }
           });		
	}	
	
	
	/* ----------------------------------------------------------
 	 *  used for debug purpose only!
	 *  for output need to add placeholder to the page like this: <tr><td><b id="executionDebugPlaceholder"></b></td></tr>
	 */
    
    function executionDebugFunc(clear_before, text) 
    {
        if(clear_before)
        {
        	$("#executionDebugPlaceholder").html('');
        }
        if(text != "")
        {
	    	var d = new Date();
	        var x = $("#executionDebugPlaceholder");
	        var h = addZero(d.getHours(), 2);
	        var m = addZero(d.getMinutes(), 2);
	        var s = addZero(d.getSeconds(), 2);
	        var ms = addZero(d.getMilliseconds(), 3);
	        x.append("&nbsp;&nbsp;&nbsp;&nbsp;" + text + " - " + h + ":" + m + ":" + s + ":" + ms);
        }
    }
    
    function addZero(x,n) {
        if (x.toString().length < n) {
            x = "0" + x;
        }
        return x;
    }
    
    /*  ---------------------------------------------------------  */
	 
    