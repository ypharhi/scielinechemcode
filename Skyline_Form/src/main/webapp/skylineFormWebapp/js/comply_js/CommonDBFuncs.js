/**
	check if the run name exists in the system.
	see isRunNameExist in java.
	note: this function also returns when -2 time out.
**/
function isRunExists(productId, runName, sourceRunName, stageIDList, stageSourceCode)
{ 
	 var toReturn = -1;
	 $.ajax
	({ 
		type: "POST",
		contentType: "application/x-www-form-urlencoded; charset=utf-8",
		url: "helperservlet",
		data: "actionid=isRunExists&productId=" + productId + "&runName=" + runName + "&sourceRunName=" + sourceRunName +"&stageIDList=" + stageIDList + "&stageSourceCode=" + stageSourceCode,
		dataType: "text",
		success: function( data ) 
		{  
			if(data.match(/TIME_IS_OUT/))
			{
				toReturn = -2;
			}
			else
			{
				toReturn = data;
			}
		},
		async: false,
		error: commanDBAjaxError
	});	
	
	return toReturn;
}

function isAlive()
{ 
	 var toReturn = 1;
	 $.ajax
	({ 
		type: "POST",
		contentType: "application/x-www-form-urlencoded; charset=utf-8",
		url: "helperservlet",
		data: "actionid=isAlive",
		dataType: "text",
		success: function( data ) 
		{  
			if(data.match(/TIME_IS_OUT/))
			{
				toReturn = 0;
			}
			else
			{
				toReturn = 1;
			}
		},
		async: false,
		error: commanDBAjaxError
	});	
	
	return toReturn;
}

function getTasksCount()
{ 
	 var toReturn = "0";
	 $.ajax
	({ 
		type: "POST",
		contentType: "application/x-www-form-urlencoded; charset=utf-8",
		url: "helperservlet",
		data: "actionid=getTasksCount",
		dataType: "text",
		success: function( data ) 
		{  
			if(data.match(/TIME_IS_OUT/))
			{
				commanDBAjaxError();
			}
			else
			{
				toReturn = data;
			}
		},
		async: false,
		error: commanDBAjaxError
	});	
	
	return toReturn;
}
 
function commanDBAjaxError( xhr, textStatus, error ) 
{
	if (xhr.responseText.match(/TIME_IS_OUT/)) 
    {   
		top.location.href = 'Login.jsp';
		return;
    } 
	else
	{
		return -1;
	}
	
}

function fnDivCalc()
{
	var toReturn = '';
	$.ajax
	({ 
		type: "POST",
		contentType: "application/x-www-form-urlencoded; charset=utf-8",
		url: "helperservlet",
		data: "actionid=divCalcRetObject",
		dataType: "json",
		success: function( data ) 
		{  
			if ( data instanceof Object )
			{
				toReturn = data;
			}
			else
			{
				if(data.match(/TIME_IS_OUT/))
				{
					commanDBAjaxError();
				}
			}
		},
		async: false,
		error: commanDBAjaxError
	});	
	
	return toReturn;
}

function saveInSession(key, val)
{
	var toReturn = '';
	 $.ajax
	({ 
		type: "POST",
		contentType: "application/x-www-form-urlencoded; charset=utf-8",
		url: "helperservlet",
		data: "actionid=saveInSession&key=" +  encodeURIComponent(key) + "&val=" + encodeURIComponent(val),
		dataType: "text",
		success: function( data ) 
		{  
			//do nothing
		},
		async: false
	});	
	
	return toReturn;
}

function getInSession(key)
{
	var toReturn = '';
	 $.ajax
	({ 
		type: "POST",
		contentType: "application/x-www-form-urlencoded; charset=utf-8",
		url: "helperservlet",
		data: "actionid=getInSession&key=" +  encodeURIComponent(key),
		dataType: "text",
		success: function( data ) 
		{  
			if(data == null || data.match(/TIME_IS_OUT/))
			{
				toReturn = '';
			}
			else
			{
				toReturn = data;
			}
		},
		async: false
	});	
	
	return toReturn;
}

/* used by Inventory List General for check if reference id with same value not exist for the same inventory type  */
function fnCheckInvRefID(invTypeID, invID, invRefID)
{
	var toReturn = '';
	 $.ajax
	({ 
		type: "POST",
		contentType: "application/x-www-form-urlencoded; charset=utf-8",
		url: "helperservlet",
		data: "actionid=checkInvRefID&invTypeID=" + invTypeID + "&invID=" + invID + "&invRefID=" + invRefID,
		dataType: "text",
		success: function( data ) 
		{  
			if(data.match(/TIME_IS_OUT/))
			{
				toReturn = 'TIME_IS_OUT';
			}
			else
			{
				toReturn = data;
			}
		},
		async: false,
		error: commanDBAjaxError
	});	
	
	return toReturn;
}

function getSystemParameter(paramName)
{
	var toReturn = '';
	 $.ajax
	({ 
		type: "POST",
		contentType: "application/x-www-form-urlencoded; charset=utf-8",
		url: "helperservlet",
		data: "actionid=getSystemParameter&paramName=" +  encodeURIComponent(paramName),
		dataType: "text",
		success: function( data ) 
		{  
			if(data == null || data.match(/TIME_IS_OUT/))
			{
				toReturn = '';
			}
			else
			{
				toReturn = data;
			}
		},
		async: false
	});	
	
	return toReturn;
}

function getRespGrByUser()
{
	var toReturn = '';
	 $.ajax
	({ 
		type: "POST",
		contentType: "application/x-www-form-urlencoded; charset=utf-8",
		url: "helperservlet",
		data: "actionid=getRespGrList",
		dataType: "text",
		success: function( data ) 
		{  
			if(data.match(/TIME_IS_OUT/))
			{
				commanDBAjaxError();
			}
			else
			{
				toReturn = data;
			}
		},
		async: false
	});	
	
	return toReturn;
}

function getObiSessionNoWsdlClass()
{
	var toReturn = '';
	 $.ajax
	({ 
		type: "POST",
		contentType: "application/x-www-form-urlencoded; charset=utf-8",
		url: "helperservlet",
		data: "actionid=getObiSessionNoWsdlClass",
		dataType: "text",
		success: function( data ) 
		{  
			if(data == null || data.match(/TIME_IS_OUT/))
			{
				toReturn = '';
			}
			else
			{
				toReturn = data;
			}
		},
		async: false
	});	
	return toReturn;
}
/* 
--------------------------------
-- need OBI wsdl classes ...
--------------------------------
function getObiSession()
{
	var toReturn = '';
	 $.ajax
	({ 
		type: "POST",
		contentType: "application/x-www-form-urlencoded; charset=utf-8",
		url: "helperservlet",
		data: "actionid=getObiSession",
		dataType: "text",
		success: function( data ) 
		{  
			if(data == null || data.match(/TIME_IS_OUT/))
			{
				toReturn = '';
			}
			else
			{
				toReturn = data;
			}
		},
		async: false
	});	
	return toReturn;
} 
function getObiHtml()
{
	var toReturn = '';
	 $.ajax
	({ 
		type: "POST",
		contentType: "application/x-www-form-urlencoded; charset=utf-8",
		url: "helperservlet",
		data: "actionid=getObiHtml",
		dataType: "text",
		success: function( data ) 
		{  
			if(data == null || data.match(/TIME_IS_OUT/))
			{
				toReturn = '';
			}
			else
			{
				toReturn = data;
			}
		},
		async: false
	});	
	return toReturn;
}
*/