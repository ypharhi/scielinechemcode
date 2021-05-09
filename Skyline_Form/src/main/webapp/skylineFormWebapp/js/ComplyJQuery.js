/* ComplyJQuery.js */
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
 *		needed sources: jquery-ui.js, jquery.js 
 * 	    needed links: jquery.ui.datepicker.css, jquery.ui.theme.css
 */
function initDatePicker() 
{ 
	$('.date-picker').datepicker({
		showOn: "button",
		dateFormat: "dd/mm/yy",
		buttonImage: "images/calendar.png",
		buttonImageOnly: true,
		changeMonth: true,
		changeYear: true,
		showButtonPanel: true,   
		onSelect: function( selectedDate ) 
		{  
		
		}                        
   });
   
   $('.date-picker').val('00/00/0000');
}

/* the function can receive more than 1 element (comma-separated) to process 
 * it relates only to SELECT elements */
function removeInactiveItems( /* 1 SELECT element or more, comma-separated */ )
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
	var time = fnTrimString( $(oTime).val() );
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

function imposePasteMaxLength(Object, MaxLen)
{ 
	setTimeout(function () {
	 $(Object).val($(Object).val().substr(0, MaxLen));
	  }, 0);
}