try
{
	//window.history.go(1);
}
catch(e){}

var currColor = '';



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
function getMsgOfChange()
{
   var bReturn = true;
   
   if(bOnChange == true)
   {
      if(confirm("Click OK to exit without updating your changes"))
         bReturn = true;
      else
         bReturn = false;
   }   

   return(bReturn);
}

function setParametrOfChange()
{
	var actionField = document.getElementById("actionid"); 
	if(actionField)
	{
		if(actionField.value == "edit")
		{
			bOnChange = true;
		}
	}
	else
	{
		actionField = document.getElementById("action_id");
		if( actionField && actionField.value == "edit" )
		{
			bOnChange = true;
		}
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

	if (e == null)
	{
		return;
	}
	
	if (event.type == 'focus')
	{
		currColor = e.style.borderColor;
		e.style.borderColor = '#FF1818';
	}
	else
	{
		e.style.borderColor = currColor;
	}
        
//	var e = event.srcElement;
//	if (event.type == 'focus')
//	{
//		currColor	= e.currentStyle.borderColor
//		e.style.borderColor = '#FF1818';
//	}
//	else
//	{
//		e.style.borderColor = currColor;
//	}
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

function validateNumber(intKeyCode)
{
	if ((intKeyCode < 48) || (intKeyCode > 57))
		return false;
	else
		return true;
}

function validatePositiveNumber (intKeyCode, value)
{
	if ((intKeyCode < 48) || (intKeyCode > 57) || (intKeyCode == 48 && value.length == 0))
		return false;
	else
		return true;
}

function validateDecimal(value, key)
{	
        if(key == 45 && value.length == 0)
	{
                return true;	
	}
	if((key >= 48 && key <=57) || (key == 46 && value.indexOf(".") == -1))
	{
                return true;
	}
	else
	{
                return false;
	}
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
function validateDecimalNoSign(value, key)
{
	
	if((key >= 48 && key <=57) || (key == 46 && value.indexOf(".") == -1))
	{
		return true;
	}
	else
	{
		return false;
	}
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

/* function fnTrimString(sValue)
{
	var stResult = sValue.toString();

	if(stResult.length > 0)
	{
		while(stResult.substr(0,1) == " ")
		{
			stResult = stResult.substr(1);
		}

		var len = stResult.length;
		while(stResult.substr(--len) == " ")
		{
			stResult = stResult.substr(0,len);
			len = stResult.length;
		}
	}
	else
	{
		stResult = "";
	}

	return stResult;
} */

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
			oTR.className = "TRVSelected";
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
        oImg.src = arrIMG[arrIndex].src;
    }
}

function InitSortSettings()
{
    var stCurrFld = document.all.txtSortField.value.toString();
    var stCurrOrder = document.all.txtSortOrder.value.toString();
    var iOrder = 1;

    var currColumn = document.all.item("Clmn" + stCurrFld);

    currColumn.className = "TDHSort";
           
    try
    {
        iOrder = parseInt(stCurrOrder);
    }
    catch(e)
    {
        iOrder = 1;
    }

    SetImgSrc(currColumn.all.item("Img" + iOrder), iOrder);
    currColumn.all.item("Img" + iOrder).style.cursor = "default";
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

function validateLegalChar(inKeyCode)
{
	if (inKeyCode == 34 ||							// "
		(inKeyCode >= 38 && inKeyCode <= 39) ||		// & '
		 inKeyCode == 44 ||							// ,
		(inKeyCode >= 59 && inKeyCode <= 64) ||		// ; < = > ? @
		 inKeyCode == 92  ||						// \ 
		 inKeyCode == 124)							// |
		 return false;
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
	{
		currentIllegals = createIllegalCharString(inLegals);
	}
	
	if (inIllegals != null || inLegals != null)
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
	if (inLegals == null) // covers a case where value was not sent
	{
		return '';
	}
	
	var reg = new RegExp('[' + inLegals + ']', 'g');
	return ('\"\'\\&,;<=>?@|').replace(reg, '');
}

// function fnValidateString (inStr, inFieldName, isRequired)
// {
	// if (!validateLegalString(inStr))
	// {
		// alert(inFieldName + " contains illegal characters\nplease do not use \" & \' , ; < = > ? @ \\ |");
		// return false;
	// }
		
	// if (isRequired && inStr == "")
	// {	
	    // alert(inFieldName + " is a required field");
		// return false;
	// }
	
	// return true;
// }

/* Allows for several values considered "empty".
 * inStr - string to be evalurate. should be sent trimmed already.
 * Originally for indexes of DDL, or any value which is not "" but is still considered illegitimate. 
 * Several values can be sent, separated by | (pipe) sign (e.g, "0|1" will alert that field is required if inStr is any of those values) */ 
// function fnValidateString (inStr, inFieldName, isRequired, inForbiddenValues)
// { 
	// if (!validateLegalString(inStr))
	// {
		// alert(inFieldName + " contains illegal characters\nplease do not use \" & \' , ; < = > ? @ \\ |");
		// return false;
	// }
		
	// if (inForbiddenValues == null) // covers a case where value wasn't sent at all
	// {
		// inForbiddenValues = "";
	// }
	
	// if (isRequired) 
	// {			
		// if (inStr == "")
		// {
			// alert(inFieldName + " is a required field");
			// return false;
		// }
		
		// if (("|" + inForbiddenValues + "|").indexOf("|" + inStr + "|") != -1)
		// {
			// alert(inFieldName + " is a required field");
			// return false;
		// }
	// }
	
	// return true;
// }

/* Besides alerting if a required field is empty, it also allows for several values considered "empty", 
 * and for exceptions of the usual illegal chars (or a pre-set list sent to the function).
 * inStr - string to be evaluated. Should be sent trimmed already.
 * inForbiddenValues - values separated by | (pipe) sign (e.g, "0|1|choose") that will alert that the field is required if inStr is any of those.
 * 		(Originally for indexes of DDL, or any value which is not "" but is still considered illegitimate/empty).  
 * inIllegals - A string of concatenated chars replacing the "usual" illegal chars.
 * inLegals - A string of concatenated chars, which are exceptioned from the usual list of illegal chars.
 * note: inLegals is considered ONLY if inIllegals is null */ 
function fnValidateString (inStr, inFieldName, isRequired, inForbiddenValues, inIllegals, inLegals)
{	
	if (!validateLegalStringSelective(inStr, inIllegals, inLegals))
	{
		alert(inFieldName + " contains illegal characters" + 
							(inLegals != null ? "":
												("\nplease do not use " + (inIllegals != null ? inIllegals:
																								 "\" & \' , ; < = > ? @ \\ |")
			  )));
		return false;
	}
		
	if (inForbiddenValues == null)
	{
		inForbiddenValues = "";
	}
	
	if (isRequired) 
	{			
		if (inStr == "")
		{
			alert(inFieldName + " is a required field");
			return false;
		}
		
		if (("|" + inForbiddenValues + "|").indexOf("|" + inStr + "|") != -1)
		{
			alert(inFieldName + " is a required field");
			return false;
		}
	}
	
	return true;
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
function fnValidateNumeric (inStr, inFieldName, isDecimal, isSigned, isRequired)
{
	if (!validateNumeric(inStr, isDecimal, isSigned))
	{
		alert (inFieldName + " contains illegal characters or format");
		return false;
	}
	
	if (isRequired && inStr == "")
	{	
	    alert(inFieldName + " is a required field");
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

function strStartsWith(str, prefix) 
{    
	return str.indexOf(prefix) === 0;
}

function strEndsWith(str, suffix) 
{     
	return str.match(suffix+"$")==suffix;
}

/* gets an nodeList of radio-buttons (should be sent while using getElementsByName([radio_buttons' name])
	and returns the checked radio button object; returns null if no elements in the array or none is checked in that group */ 
function getSelectedRadioButton (oRadioGroup)
{
	if (oRadioGroup == null)
	{
		return null;
	}
	
	for (var i = 0; i < oRadioGroup.length; i++)
	{
		if (oRadioGroup[i].checked == true)
			return oRadioGroup[i]; 
	}
	
	return null;
}

/* assumption is that chart-defaultive radio buttons are stored in hidden fields id'd with "checked" + radio-group name ("rd"... something)
 * the function works onclick receiving (this.name) and stores the radio_id in the relevant fields, to be checked after refresh again */
 function onChangeChartDefaultRadioButton (eName)
{
	var oRadio = getSelectedRadioButton(document.getElementsByName(eName));
	var hiddenField = document.getElementById("checked"+eName);
	if (hiddenField != null)
	{
		hiddenField.value = oRadio.id;
	}	
}

/* assumption is that chart-defaultive radio buttons are stored in hidden fields id'd with "checked" + radio-group name ("rd"... something)
 * the function collects those hidden fields' values and if there are relevant radio-buttons it checks them */
function setChartDefaultiveRadioButtons ()
{
	var inputArray = document.getElementsByTagName("input");
	var cRadioIDs = new Array();
	
	for (var i = 0; i < inputArray.length; i++)
	{
		if (inputArray[i].id.indexOf("checkedrd") == 0 && inputArray[i].type == "hidden")
		{
			if (inputArray[i].value != null)
			{
				cRadioIDs.push (inputArray[i].value);
			}
		}
	}
	
	for (var i = 0; i < cRadioIDs.length; i++)
	{
		var oRadio = document.getElementById (cRadioIDs[i]);
		if (oRadio != null && oRadio.type == "radio")
		{	
			oRadio.checked = true;
		}
	}	
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

// This function makes validation on filter fields by there input type
function validateFilter(inputType, filterValue)
{

    var forrbiddens;
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

        forrbiddens = "<>=-.";

        if ((filterValue != "") && (numOnly) && (filterValue.indexOf("<") == -1 && filterValue.indexOf("=") == -1 && filterValue.indexOf(">") == -1))
        {
            result = "DispAlDial_Msg_Short_Name_V_53_1"; // kd "Please Use <>=,\"and\",\"Or\",\"between\" And Numric values."
            return result;
        }
        for (var i = 0; i < filterValue.length; i++)
        { 
            var new_key = filterValue.charAt(i);
            if (((new_key < "0") || (new_key > "9")) && !(new_key == "") && forrbiddens.indexOf(new_key) < 0 && !(new_key == " "))
            {
                // alert("wrong:"+new_key);
            	result = "DispAlDial_Msg_Short_Name_V_53_2"; // kd 'Wrong Filter Provided.\n Please Enter Numeric Limitations (Use numbers and <>= Signs)\n Or use "is null", "is not null","between".'
                return result;
            }
        }
    }

    if (inputType == "T")
    {
        forrbiddens = "#\+$*()!@^&`~;=[]{}|\\<>=";

        for (var i = 0; i < filterValue.length; i++)
        {
            var new_key = filterValue.charAt(i); //cycle through characters
            if (!(new_key == "") && forrbiddens.indexOf(new_key) >= 0)
            {
                result = "DispAlDial_Msg_Short_Name_V_53_3"; // kd 'Wrong Filter Provided. Please Enter Text Limitations Only.\n(For Example,Parts of Requested Names like \"%abc%\" , for phrases containing ..abc..,\n,\"abc%\" - for phrases that starts with abc..,  ';
                return result;
            }
        }
    }

    if (inputType == "D" && filterValue != "")
    {
        forrbiddens = "\#<>= /0123456789";

        if (filterValue.indexOf("#") == -1)
        {

            result = "DispAlDial_Msg_Short_Name_V_53_4"; // kd 'Wrong Filter Provided.\n Please Enter Date In The Following Format: \n #DD/MM/YYYY#\n (Surround the date With #..#)\n Or use "is null", "is not null"';
            return result;
        }
        for (var i = 0; i < filterValue.length; i++)
        {
            var new_key = filterValue.charAt(i); //cycle through characters
            if (((new_key < "0") || (new_key > "9")) && (new_key != "") && (forrbiddens.indexOf(new_key) < 0) && (new_key != " "))
            {

            	result = "DispAlDial_Msg_Short_Name_V_53_4"; // kd 'Wrong Filter Provided.\n Please Enter Date In The Following Format: \n #DD/MM/YYYY#\n (Surround the date With #..#)\n Or use "is null", "is not null"';
                return result;
            }
        }
        //code removed here...
        if (filterValue.split(' ').join('').indexOf("/#") > -1)
        {
        	result = "DispAlDial_Msg_Short_Name_V_53_5"; // kd 'Wrong Filter Provided.\n Please Enter Date In The Following Format: \n #DD/MM/YYYY#\n (Surround the date With #...#)\n  (Remove last slash "/")';
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

/*  This function invokes e-sign pop-up and returns its value (either null or an array). 
	commentRequired is a boolean parameter for the comment field.
    note: return value is an Array */
function authenticateUser (commentRequired)
{
	var winStatus, winArguments;
	var arrArgs = new Array();
	
	if (commentRequired)
	{
		document.all.txtCommentsRequiredFlag.value = "1";
	}	
	else
	{
		document.all.txtCommentsRequiredFlag.value = "0";
	}
	
	arrArgs[0] = document.all.main;
	arrArgs[1] = "esignservlet";
	
	winArguments = arrArgs;
	winStatus = "dialogHeight: 340px; dialogWidth: 400px;edge: sunken; scroll: No; center: YES; help: No; resizable: No; status: No;unadorned: Yes;";
	
	return window.showModalDialog("WinModalSubmitWithScroll.htm"   , winArguments, winStatus);
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
			else if (!isPartOfValue && sourceNodeList[i].attributes[attrName].value == attrValue)
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

/* flexible version of validateLegalChar() that is usually invoked on keydown.
 * inKeyCode: key-code to validate, usually sent as event.keyCode 
 * strIllegalChars: a string of concatenated characters that are dimmed illegal and so won't be type if pressed 
 * strLegalChars: used ONLY if strIllegalChars is null. A string of concatenated often-illegal chars exceptioned
 * from the basic validateLegalChar() check */
function validateLegalCharSelective(inKeyCode, strIllegalChars, strLegalChars)
{
	if (strIllegalChars !== null)
	{
		for (var i = 0; i < strIllegalChars.length; i++)
		{
			if (strIllegalChars.charCodeAt(i) == inKeyCode)
			{	// illegal char
				return false;
			}
		}
	}
	else if (strLegalChars != null) // includes != undefined
	{
		for (var i = 0; i < strLegalChars.length; i++)
		{	// check if current key is considered legal
			if (strLegalChars.charCodeAt(i) == inKeyCode)
			{
				return true;
			}
		}
		return validateLegalChar(inKeyCode); // use basic list of illegal chars for validation after legals were exceptioned
	}
	
	return true;
}