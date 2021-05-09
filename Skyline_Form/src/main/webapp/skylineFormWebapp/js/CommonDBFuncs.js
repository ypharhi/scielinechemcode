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
 
function commanDBAjaxError( xhr, textStatus, error ) 
{
	return -1;
}