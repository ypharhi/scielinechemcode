var ElementRichTextEditorImp = {
    value_: function (val_) {
        var id = $(val_).attr('id');    
        var elementID = $(val_).attr('elementID');
        var isChangedflag=0;
        if($('#'+id).attr("is_changed_flag") == "1") {
        	isChangedflag = 1;
        } else {
        	isChangedflag = 0;
        }
        var data_ = {
            "elementID": elementID,
        	"value": getValueForElementRichText($(val_)),
            "plainText": getPlainTextForElementRichText($(val_)),
            "isChangedflag": isChangedflag 
        };
        return JSON.stringify(data_);
    },
    setvalue_: function (val_) {
        //important: should add attribute 'lastvalue' to the element imp in the poll (like in ElementAutoCompleteIdValDDLImp)
    },
    setDefaultValueForUnitTest_: function (val_) {
    },
    displayValue_: function (val_) {
    	try
    	{
	        var domId = $(val_).attr('id');
	        var elementID = $(val_).attr('elementID');
	        var value = getAuditTrailValueForElementRichText($(val_));
	        return value;
    	} 
    	catch(err) 
    	{
    		console.log("ERROR in displayValue_ in ElementRichTextEditorImp domID = " + domId);
    		console.log(err);
    		return "";
    	}
    }
};
(function ($)
{
	$.fn.richtext = function customRichtextEditor (param1, param2) {
		
		var $element = $(this);
		var arg_len = arguments.length;
		
		var _get_setHtmlValue = function(value) {
			if(arg_len == 1) {
				return $element.summernote('code');
			}
			else {
				$element.summernote('code', value);
				return true;
			}
		}
		var _getTextValue = function() {
			var _html = $element.summernote('code');
			var _text = _html.replace(/<\/p>/gi, "\n")
                			.replace(/<br\/?>/gi, "\n")
                			.replace(/<\/?[^>]+(>|$)/g, "");
			var plainText= $("<div />").html(_text).text().trim();
			return plainText;
		}
		var _destroyEditor = function() {
			$element.summernote('destroy');
			return true;
		}
		var _initRichtextEditor = function(options) {
			var opts = (options)?options:{};
			var _height = (opts.height)?opts.height:250;
			var _removeButtonList = (opts.removeButtons)? opts.removeButtons : [];
			var _toolBar = [];
			if(_removeButtonList.indexOf('style')==-1){
				_toolBar.push(['style', ['style']]);
			}
			if(_removeButtonList.indexOf('font')==-1){
				_toolBar.push(['font', ['bold', 'italic', 'underline', 'strikethrough', 'superscript', 'subscript', 'clear']]);
			}
			if(_removeButtonList.indexOf('fontname')==-1){
				_toolBar.push(['fontname', ['fontname']]);
			}
			if(_removeButtonList.indexOf('fontsize')==-1){
				_toolBar.push(['fontsize', ['fontsize']]);
			}
			if(_removeButtonList.indexOf('color')==-1){
				_toolBar.push(['color', ['color']]);
			}
			if(_removeButtonList.indexOf('para')==-1){
				_toolBar.push(['para', ['ul', 'ol', 'paragraph','height']]);
			}
			if(_removeButtonList.indexOf('table')==-1){
				_toolBar.push(['table', ['table']]);
			}
			if(_removeButtonList.indexOf('insert')==-1){
				_toolBar.push(['insert', ['link', 'picture']]);
			}
			if(_removeButtonList.indexOf('view')==-1){
				_toolBar.push(['view', [, 'undo', 'redo', 'codeview']]);//'fullscreen'
			}
			if(_removeButtonList.indexOf('help')==-1){
				_toolBar.push(['help', ['help']]);
			}
				/*[
					['style', ['style']],
					['font', ['bold', 'italic', 'underline', 'strikethrough', 'superscript', 'subscript', 'clear']],
					['fontname', ['fontname']],
					['fontsize', ['fontsize']],
					['color', ['color']],
					['para', ['ul', 'ol', 'paragraph','height']],
					['table', ['table']],
					['insert', ['link', 'picture']],
					['view', ['fullscreen', 'undo', 'redo', 'codeview', 'help']] // for develop use
				 ];*/
				
			$element.summernote({
					tabsize: 2,
					height:_height,
					spellCheck: false,
					disableDragAndDrop: true,
					// focus: true,
					toolbar: _toolBar,
					colorButton: {
						display: 'none'
					},
					minHeight: null,             // set minimum height of editor
					maxHeight: null,             // set maximum height of editor
					callbacks: {
					    onChange: function(contents, $editable) {
					    	prop.dataChanged = true;
					    	$element.attr('is_changed_flag','1');
					    	console.log('onChange:', contents, $editable);
					    },
					    onKeydown: function(e) {
					        console.log('Key is downed:', e.keyCode);
					        validateRichtextOnKeydown(e);
					    }
					}
				});
				// hidden possibility to upload files from local machine, development necessary for treat uploaded file/image 
				$('div.note-group-select-from-files').remove();
				
		}

		if(arg_len <= 1 && (param1 == null || typeof arguments[0] == 'object'))
		{
			_initRichtextEditor(param1);
		}
		else
		{			
			switch(param1) {
				case 'html': return _get_setHtmlValue(param2);
				case 'text': return _getTextValue();
				case 'destroy': return _destroyEditor();
				default: return null;
			}
		}

	}
})(jQuery)

/**
 * return from onAjaxCall()
 * @param obj
 * @returns
 */
function upDateElementRichtext(obj) {
    if (typeof obj.isDisabled !== 'undefined') {
        if (obj.isDisabled.toLowerCase() == "false") {
            $('[id="' + obj.domId + '_parent"]').removeClass('disabledclass');
        } else {
            $('[id="' + obj.domId + '_parent"]').addClass('disabledclass');
        }
    }
    if (typeof obj.isHidden !== 'undefined') {
        if (obj.isHidden.toLowerCase() == "false") {
            $('[id="' + obj.domId + '_parent"]').css('visibility', 'visible');
        } else {
            $('[id="' + obj.domId + '_parent"]').css('visibility', 'hidden');
        }
    }
}

/**
 * Sets the summernote element disabled after it loaded on screen 
 **/
function setRichTextEditorDisabled(domID, isDisabled, callType)
{
	//console.log("arguments.length = " + arguments.length);
	try
	{
		if(callType == 'AUTHEZ') // authz
		{
			if(domID != null && domID != "undefined" && domID != "")
			{
				var richtextEnable = isDisabled?'disable':'enable';
				$('#' + domID).prop("disabled", isDisabled);
				$('#'+domID).summernote(richtextEnable);
			}
		}
		else if(callType == 'INSTANCE_READY') //domID on ready
		{
			if(isGeneralDisabledStateForLateRender()) {
				$('#' + domID).prop("disabled", true);
				$('#'+domID).summernote('disable');
			} 
		}
	}
	catch(e)
	{
		console.log("CKEDITOR not ready in setRichTextEditorDisabled() isDisabled: " + isDisabled + "  elementID: " + domID + " in this case we disabled the textarea and this should make it disabled to (before the instance is ready)");
//		console.log(e);
	}
}

function getValueForElementRichText($element)
{
	var content = $element.richtext('html');
	console.log("code",content);

	// var text = $element.richtext('text');
	// console.log('text', text);
	// console.log('isEmpty', $element.summernote('isEmpty'));
	
	return content;
}	

function isRichTextEmpty($element){
	return $element.summernote('isEmpty');
}

function getPlainTextForElementRichText($element)
{
	var content = $element.richtext('text');
	console.log("code",content);
	return content;
}

function getAuditTrailValueForElementRichText($element){
	return getPlainTextForElementRichText($element);
}

function focusRichtext($element){
	$element.summernote('focus');
}

function loadRichtextContentById($element, content)
{	
	$element.richtext('html', content);
}	

function clearRichTextContent($element){
	try{
		$($element).summernote('reset');
	}
	catch(Err) 
	{
		console.log("----Rich Text Editor function clearRichTextContent [domID="+domId+"]---");
		console.log(Err);
	} 
}

function elementRichTextEditorValidation()
{
	var LIMIT_SIZE = 4000;
	  var bool = true, isError = false,
    thisLabel, thisDisplay;
	  $('[formElement=1][element="ElementRichTextEditorImp"]').each(function () { 
		  var id_ = $(this).attr('id');
		  var elementID_ = $(this).attr('elementID');
		  var txt_;
		  thisLabel = $('[for="' + this.id + '"]').siblings('label');
        thisDisplay = (thisLabel.length) ? thisLabel.text().slice(0, -1) : (this.id.charAt(0).toUpperCase() + this.id.slice(1)); 
		  try 
		  {
			  var legals = "quotmarks,apostrophe";
			  if(resetLegalsForRichtextElem(this.id)) legals = "apostrophe"; //TODO alex: temporary workaround, need to get legals trough form builder settings
			  txt_ = getPlainTextForElementRichText($('#'+id_));
			  if (!fnValidateString({ inStr:$.trim(txt_), fieldName:thisDisplay, focusOn:id_, inLegals: legals}))
			  {
				  bool = false;
				  return false;
			  }
		  }  
		  catch(Err) 
		  {
			  console.log("----Rich Text Editor[domID="+id_+"][elementID="+elementID_+"] Validation Error---");
			  console.log(Err);	
			  return true;//adib 19072020- removed the alert and returned true since we assume that if the instance was not ready , then it means the user has not changed the richtext yet.
			  //what says that the validation has been valid in the previous save
		  }
		  if(!isError && txt_.length > LIMIT_SIZE) {
			  
	          displayAlertDialog(thisDisplay + " " + getSpringMessage('richTextLimitMessage'));
	          bool = false;
	          return false;
		  }
		  isError = false;
	  });
	  return bool;
}
/* used in richtext of Editable table */
function validateRichtextOnKeydown(e)
{
	var _illegals = ['"','\\','<']; //not allow: " \ <
    var character = null;
    var charCode = e.charCode;
    if(charCode != 0)
    	character = String.fromCharCode(charCode);
    else
    	character = e.key;

    //restriction logic
    if($.inArray(character, _illegals)  != -1)
    {
       e.preventDefault(); // To prevent insert
    }
}