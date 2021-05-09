// JavaScript Document

var xmlData;
var languageSel;
var jsonData = new Object();

function LoadLabelsXML(selectedLang)
{       
    var xmlPath = "Langs/languages.xml";
    
    
    $.ajax({
        url: xmlPath,
        dataType: "xml",
        success:function(xml){
            xmlData = xml;
            languageSel = selectedLang;
            //alert("1 " + xmlData);
                        
            ParsePageData("xml");
            $(xml).find('element').each(function(){
                var id = $(this).attr('id');
                var text = $(this).find(selectedLang).text();
                //alert(id);
                ReplaceText(id,text);
                //$('#'+id).html(text);
            });
        },        
       error: handleAjaxError
   });
    
}

function ParsePageData(sourceType)
{
    var keyVal;
    var text;
    $("[lang_key]").each(function()
    {
        text = "";        
        keyVal = $(this).attr('lang_key');
        if(sourceType == "xml")
        {
            text = ParseXMLData(keyVal);
        }
        else
        {
            text = ParseJsonData(keyVal);
        }

        if(text != "")
        {
            $(this).html(text);
        }
		else //use keyVal as val 
		{
			$(this).html(keyVal);
		}
    });
}

function ParseXMLData(keyval)
{
    var text ="";
    $(xmlData).find('element').each(function(){
        var id = $(this).attr('id');
        
        if(id == keyval)
        {            
            text = $(this).find(languageSel).text();
            return text;
        }
        
    });
    return text;
}

function ParseJsonData(keyVal)
{
    var text = "";
    if(jsonData[keyVal] != null)
    {   
        text = jsonData[keyVal];
    }
    return text;
}


function HandleSpecialChars(originalText)
{
    var retVal;
    retVal = originalText.replace(/@BR@/g, '&nbsp;');
    return originalText;
}


function ReplaceText(attName,replaceText)
{
    $("[key=" + attName+ "]").each(function(){
        //alert($(this).attr('id'));
        $(this).html(replaceText);
    });
}


function handleAjaxError( xhr, textStatus, error ) 
{      
    var a = 1;
}

function LoadLabelsJson()
{
    var toReturn = {};
    $.ajax 
    ({ 
         type: "POST",
         contentType: "application/x-www-form-urlencoded; charset=utf-8",
         url: "helperservlet",
         data: "actionid=getLangSettings",
         dataType: "json",
         success: function(data)
         {
            jsonData = data;            
            toReturn = data;
            ParsePageData("json");
         },
         async: false
    });
    return toReturn;
}

/* return the value for the key_ if exists in jason_ else return return key_ */ 
function getLabelValJson(jason_, key_)
{
	var return_ = key_; 
	try
	{
		return_ = jason_.hasOwnProperty(key_) ? jason_[key_] :  key_;
	}  catch(err){}
	
	return return_;
}
