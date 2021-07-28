(function ($) 
{
	$.fn.tableContextMenu = function(opts) {
//		var _type = opts.type;
		var _type = "td";
		var _tid = opts.tableID;
		var _tDT = opts.tableDT;
		var uniqueClass = _tid + "-contextmenu";
		var $target = $("#" + _tid);	
		var hasLink = false;
		var _delegate = "";	
//		if($target.find("a.smartlink-contextmenu").length > 0) {
//			_delegate = "a.smartlink-contextmenu";
//			_type = "link";
//			hasLink = true;
//		}
		// kd 08022021 for table with ddl like Step -> Reactants left old logic: if there is "span.linkElement" (ddl) then put popupmenu only for ddl column, for other column no popup
		// for tables without "span.linkElement" (without ddl) put different popup menu for different type columns (before 08022021 could be popup only for one type, usually smartlink if table contented it)
		if($target.find("span.linkElement").length > 0) {
			var elem = "span.linkElement";
			_delegate = (_delegate != "")?_delegate+","+elem:elem;
			_type = "link";
			hasLink = true;
		}
		if(!hasLink)
		{
			_delegate = "td";
		}
		
		if ($('.' + uniqueClass).length > 0) {
			$target.contextmenu("destroy");
		}
		
		$target.contextmenu({
			delegate : _delegate,
			 addClass:"ui-contextmenu " + uniqueClass,
			autoFocus : true,
			preventContextMenuForPopup : true,
			preventSelect : false,//false in order to enable to select/sign a text in a table
			closeOnWindowBlur : true,
			menu : [ 
				{title : "Open in a new tab",cmd : "open"},
				{title: "Clone", cmd: "clone"}
			 ],
			select : function(event, ui) {
				var $this = ui.target.parent();				
//				console.log("row",$this);
				try
				{	
					if(_type == "link"){
						try
						{						
							var data = $this.context.parentNode.attributes.contextmenu_data.value;
							var dataArr = JSON.parse(data);
							var formId = dataArr[0];
							var formCode = dataArr[1];
							var tab = dataArr[2];
							console.log("Go to ->", formCode + " ->" + formId);
							checkAndNavigate([''+formId+'',''+formCode+'',''+tab+'','false',true,'newTab']);
						}catch(e){
							console.log("open link in a new tab  error");
						}
					} else { 
						if($this[0].tagName == 'A'){
							try
							{						
								var data = $this.context.parentNode.attributes.contextmenu_data.value;
								var dataArr = JSON.parse(data);
								var formId = dataArr[0];
								var formCode = dataArr[1];
								var tab = dataArr[2];
								console.log("Go to ->", formCode + " ->" + formId);
								checkAndNavigate([''+formId+'',''+formCode+'',''+tab+'','false',true,'newTab']);
							}catch(e){
								console.log("open link in a new tab  error");
							}
						}
						else if ($this[0].tagName == 'TR' || $this[0].tagName == 'SPAN'){ //tagName == 'SPAN' - favorite column
							var data;
							if($this[0].tagName == 'SPAN'){
								data = _tDT.row($this.closest('td').parent()).data();
							} else {
								data = _tDT.row($this).data();
							}
							var rowId = data[0];
							
							if (ui.cmd == 'open') {
								/////var data = _tDT.row($this).data();
								// console.log("row data",data);
								/////var rowId = data[0];
								var formCode = encodeURIComponent($('[id="' + _tid + '_structCatalogItem"]').val());
								console.log("Go to ->", formCode + " ->" + rowId);	
								openFormInNewWindow(_tid, rowId);
							} else if (ui.cmd == 'clone'){
								/////var data = _tDT.row($this).data();
								/////var formId = data[0];
								
								CloneExperimentByFormId(rowId)
							}
						}
					}
				}catch(e){
					console.log("open popup menu in the table error");
				}
			},
			beforeOpen: function(event, ui) {
				var formCode = encodeURIComponent($('[id="' + _tid + '_structCatalogItem"]').val());
				
				if (formCode == 'Experiment' && $('#formCode').val() == 'Main' && (event.toElement.tagName == 'TD' || event.toElement.tagName == 'I')) { //tagName == 'I' - favorite column
					$('.ui-helper-hidden.ui-contextmenu.'+this.id+'-contextmenu').children('li').eq(1).show();
				} else {
					$('.ui-helper-hidden.ui-contextmenu.'+this.id+'-contextmenu').children('li').eq(1).hide();
				}
			}
		});
	};
})(jQuery);
(function ($) 
{
	$.fn.fixedtableheader = function (options) {
			var settings = jQuery.extend({
				originTableID:""
		    }, options);
			
			this.each(function (i) {
				var prevClonedTable = $('[id="'+settings.originTableID+'_fixedtableheader"]');
				//console.log('table cloned header is exist: ' + prevClonedTable.length);
				if(prevClonedTable.length > 0)
				{
					prevClonedTable.remove();
				}
				var $tbl = $(this);
		        var $tblhfixed = $tbl.find("tr:lt(1)");
				var $tblhfixedCloned = null;
		        var headerelement = "th";
						
		        if ($tblhfixed.find(headerelement).length == 0)
		            headerelement = "td";
		        if ($tblhfixed.find(headerelement).length > 0) {          			
		        	
		        	console.log("----fixedtableheader start");
		        	
		        	/** not necessary to explicitly set width to columns, when the plugin used parallel with 'Column Resize' plugin   */
					/*$tblhfixed.find(headerelement).each(function () 
					{
						$(this).css("width", $(this).outerWidth());
		            });*/
		            /*******/
		        	
					$tblhfixedCloned = $tblhfixed.clone();
					$tblhfixedCloned.find(headerelement).each(function () 
					{
		                var _title = $(this)[0].innerText;
						
						$(this).css("width", $(this).width());
						$(this).empty().html(_title);
						
		            });

					var sctop = $(window).scrollTop();
		            var elmtop = $tblhfixed.offset().top;
					var _displayClonedTable = "none";
					if (sctop > elmtop && sctop <= (elmtop + $tbl.height() - $tblhfixed.height()))
					{
						_displayClonedTable = "inline-block";
					}
					var $newTable = $('<table></table>')
									.append('<thead></thead>');
					var $clonedTable = $newTable;
		            var tblwidth = getTblWidth($tbl);
		            $clonedTable.attr("id", settings.originTableID+"_fixedtableheader")
					.addClass('fixedTableHeaderClass')
					.css({
		                "position": "fixed",
		                "top": "0",
		                "left": $tbl.offset().left - $(window).scrollLeft(),
						"border-spacing": "inherit",
						"border-collapse": "collapse",
						"display":_displayClonedTable
		            });
					$clonedTable.find('thead').append($tblhfixedCloned);
					$clonedTable.width(tblwidth).appendTo($("body"));		            
		            $(window).scroll(function () {
						
						/* console.log('on scroll start');
						console.log('clonedTable outerWidth', $clonedTable.outerWidth());
						console.log('tbl outerWidth', $tbl.outerWidth()); */

						$clonedTable.css({
		                    "position": "fixed",
		                    "top": "0",
		                    "left": $tbl.offset().left - $(window).scrollLeft(),
							"border-spacing": "inherit",
							"border-collapse": "collapse"
		                });
		                var sctop = $(window).scrollTop();
		                var elmtop = $tblhfixed.offset().top;
						//console.log('sctop: ' + sctop + ' elmtop: '+ elmtop + ' $tbl height: ' + $tbl.height() + ' $tblhfixed height: '+$tblhfixed.height());
		                if ($tblhfixed.height() != 0 && sctop > elmtop && sctop <= (elmtop + $tbl.height() - $tblhfixed.height()))
						{
		                    //console.log('sctop: ' + sctop + ' elmtop: '+ elmtop + ' $tbl height: ' + $tbl.height() + ' $tblhfixed height: ' + $tblhfixed.height());
							if ($clonedTable.outerWidth() != $tbl.outerWidth()) {
								$tblhfixed.find(headerelement).each(function (index) {
									var w = $(this).outerWidth();
									$(this).css("width", w);
									$clonedTable.find(headerelement).eq(index).css("width", w);
								});
								$clonedTable.width($tbl.outerWidth());
							}
							$clonedTable.show();
						}
		                else
						{
							$clonedTable.hide();
						}
		            });
		            $(window).resize(function () {
		                /* console.log('on resize start');
						console.log('clonedTable outerWidth', $clonedTable.outerWidth());
						console.log('tbl outerWidth', $tbl.outerWidth()); */
						if ($clonedTable.outerWidth() != $tbl.outerWidth()) {
		                    $tblhfixed.find(headerelement).each(function (index) {
		                        var w = $(this).outerWidth();
		                        $(this).css("width", w);
		                        $clonedTable.find(headerelement).eq(index).css("width", w);
		                    });
		                    $clonedTable.width($tbl.outerWidth());
		                }
		                $clonedTable.css("left", $tbl.offset().left);
		            });
		        }
		    });

		    function getTblWidth($tbl) {
		        var tblwidth = $tbl.outerWidth();
				//console.log('getTblWidth() outerWidth', tblwidth);
		        return tblwidth;
		    }
			
	    };
})(jQuery);

(function($){    
        //pass in just the context as a $(obj) or a settings JS object  
	$.fn.autogrow = function(opts) {
        //console.log(" on autogrow start ");    
		var that = $(this).css({overflow: 'hidden', resize: 'none'}) //prevent scrollies
                , selector = that.selector
                , defaults = {
                    context: $(document) //what to wire events to
                    , animate: true //if you want the size change to animate
                    , speed: 200 //speed of animation
                    , fixMinHeight: true //if you don't want the box to shrink below its initial size
                    , cloneClass: 'autogrowclone' //helper CSS class for clone if you need to add special rules
                    , onInitialize: false //resizes the textareas when the plugin is initialized
                }
            ;
            opts = $.isPlainObject(opts) ? opts : {context: opts ? opts : $(document)};
            opts = $.extend({}, defaults, opts);
            that.each(function(i, elem){
                var min, clone;
                elem = $(elem);
                //if the element is "invisible", we get an incorrect height value
                //to get correct value, clone and append to the body. 
                if (elem.is(':visible') && parseInt(elem.css('height'), 10) > 0) {
                    min = parseInt(elem.css('height'), 10) || elem.innerHeight();
                } else {
                    clone = elem.clone()
                        .addClass(opts.cloneClass)
                        .val(elem.val())
                        .css({
                            position: 'absolute'
                            , visibility: 'hidden'
                            , display: 'block'
                        })
                    ;
                    $('body').append(clone);
                    min = clone.innerHeight();
                    elem.data('autogrow-cloned-scrollHeight', clone[0].scrollHeight);
                    clone.remove();
                }
                if (opts.fixMinHeight) {
                    elem.data('autogrow-start-height', min); //set min height                                
                }
                elem.css('height', min);

                if (opts.onInitialize && elem.length) {
                	//delayedResize(elem[0]);
                	resize(elem[0]);
                }
            });
            opts.context.on('keyup paste cut change', selector, resize);
            
            /*function delayedResize (e) {
               //window.setTimeout(function(){resize (e)}, 0);
               var timer = 0;
               timer = setTimeout(function() 
               {
            	   resize (e)
           	    	clearTimeout(timer);
           	    
               }, 0);
            }*/
            
            function resize (e)
            {
            	//console.log("< resize on autogrow >");
            	// event or initialize element
            	var _this = e.target || e;
            	var box = $(_this)
                    , oldHeight = box.innerHeight()
                    , newHeight = _this.scrollHeight || box.data('autogrow-cloned-scrollHeight') || 0
                    , minHeight = box.data('autogrow-start-height') || 0
                    , maxHeight = 10000 // used large number for cancel 'maxHeight' functionality 
                    , clone
                ;
            	if(newHeight <= minHeight) return;
                if (oldHeight < newHeight) { //user is typing
                	if(newHeight <= maxHeight)
                	{	                		
                		//this.scrollTop = 0; //try to reduce the top of the content hiding for a second
                		box.css({overflow: 'hidden'});
                		opts.animate ? box.stop().animate({height: newHeight}, opts.speed) : box.innerHeight(newHeight);	                		
                	}
                	else { //just set to the maxHeight
                        box.innerHeight(maxHeight);
                        box.css({overflow: 'auto'});
                    }
                } 
                else if (!e || e.which == 8 || e.which == 46 || (e.ctrlKey && e.which == 88)) { //user is deleting, backspacing, or cutting
                    if (oldHeight > minHeight) { //shrink!
                    	if(oldHeight >= maxHeight)
                        {
                        	box.css({overflow: 'auto'});
                        }
                        else
                        {
                        	box.css({overflow: 'hidden'});
                        }
                    	//this cloning part is not particularly necessary. however, it helps with animation
                        //since the only way to cleanly calculate where to shrink the box to is to incrementally
                        //reduce the height of the box until the $.innerHeight() and the scrollHeight differ.
                        //doing this on an exact clone to figure out the height first and then applying it to the
                        //actual box makes it look cleaner to the user
                        clone = box.clone()
                            //add clone class for extra css rules
                            .addClass(opts.cloneClass)
                            //make "invisible", remove height restriction potentially imposed by existing CSS
                            .css({position: 'absolute', zIndex:-10, height: ''}) 
                            //populate with content for consistent measuring
                            .val(box.val()) 
                        ;
                        box.after(clone); //append as close to the box as possible for best CSS matching for clone
                        do { //reduce height until they don't match
                            newHeight = clone[0].scrollHeight - 1;
                            clone.innerHeight(newHeight);
                        } while (newHeight === clone[0].scrollHeight);
                        newHeight++; //adding one back eliminates a wiggle on deletion 
                        clone.remove();
                        box.focus(); // Fix issue with Chrome losing focus from the textarea.
                        
                        //if user selects all and deletes or holds down delete til beginning
                        //user could get here and shrink whole box
                        newHeight < minHeight && (newHeight = minHeight);
                        oldHeight > newHeight && opts.animate ? box.stop().animate({height: newHeight}, opts.speed) : box.innerHeight(newHeight);
                        
                    } else { //just set to the minHeight
                        box.innerHeight(minHeight);
                        box.css({overflow: 'hidden'});
                    }
                } 
            }
            return that;
        }
    })(jQuery);

(function ($) 
{
	
	$.fn.tableNavigation = function () {
	    	    $(this).each(function () 
	    	    {	    	    	
	    	    	// Events triggered on keyup    			    	    
	    	    	$(this).find('.editableSmartCell').on('keyup', function(e)
		    	    {
	    	    		dtExt_manageTableNavigation($(this), e);   		    	    		
		    	    });
	    	      
		    	      // Events triggered on keydown (repeatable when holding the key)    			    	      
	    	    	$(this).find('.editableSmartCell').on('keydown', function(e)
	    	    	{
	    	    		dtExt_manageTableNavigation($(this), e); 
		    	    });
	    	      });
	    	  };
})(jQuery);

(function ($) 
		{			
			$.fn.tableEditableDivEvents = function () {	 
				
				var $elm = $(this).find(".editableSmartCell.contentEditableMarker"); 
				$elm.off('paste').on('paste', function(e) {
				    e.preventDefault();
				    var text = e.originalEvent.clipboardData ? e.originalEvent.clipboardData.getData('text/plain') : window.clipboardData.getData('Text');
				 // use insertText command if supported
				    if (document.queryCommandSupported('insertText')) {
				        document.execCommand('insertText', false, text);
				    }
				    // or insert the text content at the caret's current position
				    // replacing eventually selected content
				    else {
				        var range = document.getSelection().getRangeAt(0);
				        range.deleteContents();
				        var textNode = document.createTextNode(text);
				        range.insertNode(textNode);
				        range.selectNodeContents(textNode);
				        range.collapse(false);

				        var selection = window.getSelection();
				        selection.removeAllRanges();
				        selection.addRange(range);
				    }
				});
				
				$elm.on('keydown', function(e) {
    	    	    var _illegals = ['"','\\','<','\''];
    	    	    var character = e.key;
    	    	    if($.inArray(character, _illegals)  != -1)
    	    	    {
    	    	    	e.preventDefault ? e.preventDefault() : (e.returnValue = false);
    	    	    }
    	    	    // If Enter    
//			    	if (key === 13) { 
//    	    	    	dtExt_editableDivMoveCaretDownOnEnter(e);
//    	    	    }
    	    	});
    	  };
		})(jQuery);

function searchDatatable(domId, dtTable) 
{
	 $('#'+domId+' thead input[class="firstString"]').on( 'keyup', function () {
		var _this = this;
     	deleteGlobalDataTableFilterColumn(domId,$(_this).parent().index()+':visible');
		var that = dtTable.column( $(_this).parent().index()+':visible' );
	
        showWaitMessage(getSpringMessage('pleaseWait'));
        var input = $(_this);
        var _input_value = $.trim($(input).val());
        var formCode = $(_this).attr('formCode');

        setTimeout(function () {
            if (_input_value == "") {
                that.search(_input_value).draw();
                hideWaitMessage();
                return;
            }
            var option = $(input).siblings('select').val();
            if (option == "cn") {
                var strVal = _input_value.replace(/[*()?\[\]^\\$|_=+]/g, "\\$&")
                that.search("^((?!" + strVal + ").)*$", true, false).draw();
            } 
            else if (option == "co") 
            {
            	that.search(_input_value, false, true).draw();
            } 
            else {
                var i, str = "";
                var type, _metaColName, _timeFormat = "", isJSONObjContains = false;
                var colName = $(input).parent().closest('table').find('thead tr:first th').eq($(input).parent().index()).text();
                var length = $('[name="' + formCode + '_metaData"]').val().length;
                var columnsArray = $('[name="' + formCode + '_metaData"]').val().substring(1, length - 1).split(",");
                for (var k = 0; k < columnsArray.length; k++) 
                {
                    var columnArray = columnsArray[k].trim().split(":");
                    _metaColName = columnArray[0];
                    if (_metaColName == colName
                    	|| _metaColName.replace('_SMARTNUM', '') == colName
                    	|| _metaColName.replace('_SMARTTIME', '') == colName
                    	|| _metaColName.replace('_SMARTLINK', '') == colName
                    	|| _metaColName.replace('_SMARTELLIPSIS', '') == colName
                    	|| _metaColName.replace('_SMARTICON', '') == colName
                    	|| _metaColName.replace('_SMARTDATE', '') == colName
                    	|| _metaColName.replace('_SMARTEDIT', '') == colName)
                    {                    	                  		
                    	//console.log(columnArray[1], _metaColName);
                    	if (columnArray[1] == "DATE")
                    	{
                            type = "date";
                            _timeFormat = (_metaColName.indexOf('_SMARTTIME') !== -1)?" HH:mm":"";
                            isJSONObjContains = false;
                    	}
                        else if (columnArray[1] == "NUMBER")
                        {
                            type = "number";
                        }
                        else if (_metaColName.indexOf('_SMARTNUM') !== -1)
                        {
                            type = "number";
                            isJSONObjContains = true;
                        }
                        else if(_metaColName.indexOf('_SMARTDATE') !== -1)
                        {
                        	type = "date";
                        	isJSONObjContains = true;
                        }
                        else if(_metaColName.indexOf('_SMARTTIME') !== -1)
                        {
                        	type = "time";
                        	_timeFormat = "HH:mm";
                        	isJSONObjContains = true;
                        }
                        else
                        {
                            type = "string";
                            //isJSONObjContains = (_metaColName.indexOf('_SMARTLINK') != -1 || _metaColName.indexOf('_SMARTICON') != -1)?true:false;  
                            isJSONObjContains = (_metaColName.indexOf('_SMARTELLIPSIS') > -1)?false:true;
                        }
                        break;
                    }
                }
                if (type == "date") 
                {
                	// TODO: get property before tables load
                	var _userDateFormatClient;
                	var _dateFormatLength = 8;
                	var validFormats = ["DD-MM-YY","DD/MM/YY","DD.MM.YY","DD-MM-YYYY","DD/MM/YYYY","DD.MM.YYYY","DD-MMM-YY","DD/MMM/YY","DD.MMM.YY","DD-MMM-YYYY","DD/MMM/YYYY","DD.MMM.YYYY"
                	                    ,"DD-MM-YY HH:mm","DD/MM/YYHH:mm","DD.MM.YY HH:mm","DD-MM-YYYY HH:mm","DD/MM/YYYY HH:mm","DD.MM.YYYY HH:mm","DD-MMM-YY HH:mm","DD/MMM/YY HH:mm","DD.MMM.YY HH:mm","DD-MMM-YYYY HH:mm","DD/MMM/YYYY HH:mm","DD.MMM.YYYY HH:mm"];
                	if(prop.dateFormat != null && prop.dateFormat != undefined && prop.dateFormat != "undefined")
                	{
                		var _dateFormat = prop.dateFormat.userDateFormatClient;
                		_userDateFormatClient = _dateFormat + _timeFormat;
                		/*if(_timeFormat.length > 0)
                		{
                			var _input_value_moment = moment(_input_value);
                			var is_valid = _input_value_moment.isValid();
                			if(!is_valid)
                			{
                				//var _dateFormatLength = _dateFormat.length;
                				_input_value = _input_value.substr(0,_dateFormatLength) + " 00:00";
                			}
                		}*/
                	}
                	else
            		{
                		_userDateFormatClient = "DD/MMM/YYYY";
            		}
                	if( moment(_input_value,validFormats).isValid &&_input_value.length>=_dateFormatLength){
                	
	                	that.data().each(function(value){
	                		var currData = (isJSONObjContains)?getDisplayValueFromSmarts(value):value;
	                		if (option == "eq") {
	                            if (moment(currData, _userDateFormatClient, true).isSame(moment(_input_value,validFormats)))
	                                str += currData + ';';
	                        } else if (option == "ne") {
	                            if (!(moment(currData, _userDateFormatClient, true).isSame(moment(_input_value,validFormats))))
	                                str += currData + ';';
	                        } else
	                        /*if (prop.operators[option](moment(currData, _userDateFormatClient, true), moment(_input_value, _userDateFormatClient, true)))
	                            str += currData + ';';*/
	                        	if (prop.operators[option](moment(currData, _userDateFormatClient, true), moment(_input_value,validFormats)))
	                            str += currData + ';';
	                	});
                	}
                } 
                else if (type == "time") 
                {
                	var _userDateFormatClient = _timeFormat;
                	var _dateFormatLength = 4;
                	var validFormats = ["HH:mm"];
                	if( moment(_input_value,validFormats).isValid &&_input_value.length>=_dateFormatLength){
                    	that.data().each(function(value){
	                		var currData = (isJSONObjContains)?getDisplayValueFromSmarts(value):value;
	                		if (option == "eq") {
	                            if (moment(currData, _userDateFormatClient, true).isSame(moment(_input_value,validFormats)))
	                                str += currData + ';';
	                        } else if (option == "ne") {
	                            if (!(moment(currData, _userDateFormatClient, true).isSame(moment(_input_value,validFormats))))
	                                str += currData + ';';
	                        } else
	                        if (prop.operators[option](moment(currData, _userDateFormatClient, true), moment(_input_value,validFormats)))
	                            str += currData + ';';
	                    });
                	}
                } 
                else if (type == "string") {
                    /*for (i = 0; i < that.data().length; i++) 
                    {
                        var currData = (isJSONObjContains)?getDisplayValueFromSmarts(that.data()[i]):that.data()[i];
                    	if (prop.operators[option](currData.toLowerCase(), _input_value.toLowerCase()))
                            str += currData + ';';
                    }*/
                	that.data().each(function(value){
                        var currData = (isJSONObjContains)?getDisplayValueFromSmarts(value):value;
                    	if (prop.operators[option](currData.toLowerCase(), _input_value.toLowerCase()))
                            str += currData + ';';
                    });
                } else if (type == "number") {
                	that.data().each(function(value){
                    	 var currData = (isJSONObjContains)?getDisplayValueFromSmarts(value):value;
                    	if (prop.operators[option](parseFloat(currData), parseFloat(_input_value)))
                            str += currData + ';';
                    });
                }
                if (str == "") {
                    that.search("expressionNotFound").draw();
                    hideWaitMessage();
                    return;
                }
                str = str.substring(0, str.length - 1);
                //var arr = str.split(';');
                var arr = str.replace(/[*()?\[\]^\\$|_=+]/g, "\\$&").split(';');
                //var pattern = ("\\b\^" + arr.join('\$\\b|\\b\^') + '\$\\b');                
                var pattern = ("\^" + arr.join('\$|\^') + '\$')
                that.search(pattern, true, false).draw();

                if ($(that.tables().body()).find('td').hasClass('dataTables_empty')) // if search failed
                    displayAlertDialog(getSpringMessage('tableNotShowAllData'));
            }

            hideWaitMessage();
        }, 0);
    });
}

function dtExt_manageTableNavigation($this, event)
{
	var $td = $this.closest('td');
	var $tr = $this.closest('tr');
	// Events triggered on keyup
	if(event.type == 'keyup') 
	{
		switch (event.which) 
	    {		    	          				
	      	// arrow bottom
		      case 40:
		    	  if(!$this.hasClass('chosen-container'))
		    	  {
		    		  var doContinue = true;
		    		  var $tr_next = $tr.next();
		    		  while(doContinue)
			          {
		    			  try
		    			  {
		    				  if($tr_next.length > 0)
				    		  {
					    		  var _elem = $tr_next.children().eq($this.closest('td').index()).find('.editableSmartCell');
				        		  if(_elem.length > 0 && !_elem.prop('disabled'))
				        		  {
							    	  if(_elem.prop('tagName').toLowerCase() == 'select')
							    	  {   			    	        		  
							    		  _elem.next().find('input').focus();
							    	  }
							    	  else
							    	  {
							    		  _elem.focus();
							    	  }
							    	  doContinue = false;
				        		  }
				        		  else
				        		  {
				        			  $tr_next = $tr_next.next();
				        		  }
				    		  }
				    		  else
				    		  {
				    			  doContinue = false;
			      				  console.log("last row reached");
				    		  }
		    			  }
		    			  catch(e)
		    			  {
		    				  doContinue = false;
		    				  console.log("error in editableSmartCell on keyup(arrow bottom)", e);
		    			  }
			          }
		    	  }
		    	  else
		    		  event.preventDefault();
		        break;
	
		      // arrow top
		      case 38:
		    	  if(!$this.hasClass('chosen-container'))
		    	  {
		    		  var doContinue = true;
		    		  var $tr_prev = $tr.prev();
		    		  while(doContinue)
			          {
		    			  try
		    			  {
		    				  if($tr_prev.length > 0)
				    		  {
					    		  var _elem = $tr_prev.children().eq($this.closest('td').index()).find('.editableSmartCell');
					    		  if(_elem.length > 0 && !_elem.prop('disabled'))
				        		  {
						    		  if(_elem.prop('tagName').toLowerCase() == 'select')
							    	  {   			    	        		  
							    		  _elem.next().find('input').focus();
							    	  }
							    	  else
							    	  {
							    		  _elem.focus();
							    	  }
						    		  doContinue = false;
				        		  }
					    		  else
				        		  {
					    			  $tr_prev = $tr_prev.prev();
				        		  }
				    		  }
				    		  else
				    		  {
				    			  doContinue = false;
			      				  console.log("first row reached");
				    		  }
		    			  }
		    			  catch(e)
		    			  {
		    				  doContinue = false;
		    				  console.log("error in editableSmartCell on keyup(arrow top)", e);
		    			  }
			          }
		    	  }
		    	  else
		    		  event.preventDefault();
		        break;
	    }
	}
	else if(event.type == 'keydown')// Events triggered on keydown
	{
		//console.log("editableSmartCell on keydown");
		// Horizontal navigation using tab
        if (event.which === 9 && !event.shiftKey) 
        {        	
        	// navigate forward
        	var doContinue = true;
        	var loopCounter = 0;
        	var $td_next = $td.next();
        	while(doContinue)
        	{
	        	try
	        	{
        			loopCounter ++;
	        		//console.log("loopCounter",loopCounter);
	        		
	        		//check if there is another column on right
	        		if($td_next.length > 0)
	        		{        			
	        			//check if column contains html element to focus on it
	        			var _elem = $td_next.find('.editableSmartCell');
	        			if(_elem.length > 0 && !_elem.prop('disabled'))
			    		{	        			
		        			if(_elem.prop('tagName').toLowerCase() == 'select')
		        			{   			    	        		  
				    		  _elem.next().find('input').focus();
		        			}
		        			else
		        			{
				    		  _elem.focus();
		        			}
		        			doContinue = false;
		        			//console.log("find element on loopCounter",loopCounter);
			    		}
		        		else
		        		{
		        			$td_next = $td_next.next();
		        		}
	        		}
	        		else
	        		{
	        			console.log("last column reached");
	        			
	        			//check if there is another row below
	        			if($tr.next().length > 0)
	        			{
	        				$td_next = $tr.next().children().eq(0);
	        			}
	        			else
	        			{
	        				doContinue = false;
	        				console.log("last row reached");
	        			}
	        		}
	        		
	        		if(loopCounter == 30)
	        		{
	        			doContinue = false;
	        			console.log("break loop forcefully, because not find any element up to loopCounter",loopCounter);
	        		}
	        	}
	        	catch(e)
	        	{
	        		doContinue = false;
	        		console.log("error in editableSmartCell on keydown(forward)", e);
	        	}
        	}
        	
        } 
        else if (event.which === 9 && event.shiftKey) 
        {
        	// navigate backward
        	var doContinue = true;
        	var loopCounter = 0;
        	var $td_prev = $td.prev();
        	while(doContinue)
        	{
        		try
        		{
	        		loopCounter ++;
	        		//console.log("loopCounter",loopCounter);
	        		
	        		//check if there is another column on left
	        		if($td_prev.length > 0)
	        		{
	        			var _elem = $td_prev.find('.editableSmartCell');
	        			if(_elem.length > 0 && !_elem.prop('disabled'))
			    		{	        			
		        			if(_elem.prop('tagName').toLowerCase() == 'select')
		        			{   			    	        		  
				    		  _elem.next().find('input').focus();
		        			}
		        			else
		        			{
				    		  _elem.focus();
		        			}
		        			doContinue = false;
		        			//console.log("find element on loopCounter",loopCounter);
			    		}
		        		else
		        		{
		        			$td_prev = $td_prev.prev();
		        		}
	        		}
	        		else
	        		{
	        			console.log("last column reached");
	        			
	        			//check if there is another row above
	        			if($tr.prev().length > 0)
	        			{
	        				$td_prev = $tr.prev().children().eq(-1);
	        			}
	        			else
	        			{
	        				doContinue = false;
	        				console.log("last row reached");
	        			}
	        		}
	        		
	        		if(loopCounter == 30)
	        		{
	        			doContinue = false;
	        			console.log("break loop forcefully, because not find any element up to loopCounter",loopCounter);
	        		}
        		}
        		catch(e)
	        	{
        			doContinue = false;
        			console.log("error in editableSmartCell on keydown(backward)", e);
	        	}
        	}       	
        }
	}
}


function dtExt_updateCellFilterData(domId, rowInd, colInd, newVal)
{
	var _table = $('#' + domId).dataTable();
	var _settings = _table.fnSettings();	
	
	if(_settings.aoData[rowInd]._aFilterData != null) {
	_settings.aoData[rowInd]._aFilterData[colInd] = newVal;
	}
	else
	{
		_settings.aoData[rowInd]._aFilterData = [];
		_settings.aoData[rowInd]._aFilterData[colInd] = newVal;
	}
	if(_table.api().context[0]._colReorder.s.enable) {
	/*
	 * ab 10082020 workaround for 'ColReorder' library: 
	 * when column order changed by using 'ColReorder' library, table data cache(data for filter/sort) is nulled out and re-read of original data ([aData]-data loaded on table init) occurred, 
	 * so that filter/sort data is reseted to original data too.
	 * Because of this we need to update original data object/array (_aData) after each cell change too to have possibility use updated data for filter/sort.
	 * ***note: using cell.data() to update cell data cause invalidation of cell content: html content removed and original data(json object) is displayed
	 */
		var jsonData, jsonObjArray;
		try 
		{
			var origData = _settings.aoData[rowInd]._aData[colInd];
			if (checkIfJSON(origData))
			{
				
					jsonData = funcParseJSONData(origData);		        
					jsonData.displayName = newVal;	
					// update aData with updated object
			        _settings.aoData[rowInd]._aData[colInd] = JSON.stringify(jsonData);
				
			}
			else
			{
				_settings.aoData[rowInd]._aData[colInd] = newVal;
			}
		}
		catch (e) 
		{
			console.log("error in dtExt_updateCellFilterData() -> update table original data: " + origData);
			console.log("new value: " + newVal);
			console.error(e);
		}
	}
}

/**
 * **Function call original function from 'ColReorder' library**
 * 
 * Convert from the original column index, to the original
 * 
 * @param current table : $('#example').DataTable()
 * @param  {int|array} idx Index(es) to convert
 * @param  {int} dir Transpose direction - 'fromOriginal' / 'toCurrent' (default)
 *  or 'toOriginal' / 'fromCurrent'
 *  toCurrent - the input value is an original index and you wish to know its current index
 *  toOriginal - the input value is the current index and you wish to know its original index
 * @return {int|array}     Converted values
 */
function dtExt_convertColumnInx(dtTable, idx, direction) {
	
	var idx_int = parseInt(idx);
	if(!dtTable.context[0]._colReorder.s.enable) {
		return idx_int;
	}
	if(isNaN(idx_int)) {
		return idx; // if not a number return the same index value
	}
	
	if ( ! direction ) {
		direction = 'toCurrent';
	}
	else if (direction == '-1') {
		direction = 'toOriginal';
	}

	return dtTable.colReorder.transpose(idx_int, direction);
}

function dtExt_htmlEscapeEntities ( d ) {
	return typeof d === 'string' && $.trim(d) != ""?
		d.replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;') :
		d;
};

function disableEditableTables(domId)
{
	console.log("disableEditableTables() domId: ", domId);
	var domIdArr = [];
	if(domId instanceof Array)
	{
		domIdArr = domId;
	}
	else
	{
		domIdArr[0] = domId;
	}
	for(var j=0; j<domIdArr.length;j++)
	{		
		var curDomId = domIdArr[j];
		//console.log(curDomId);
		$('#'+domId).attr('disableEditable',"1");
		if ($.fn.DataTable.isDataTable('#' + curDomId)) {//if the table is not ready in the current timestamp, it will be re-fired on the func elementDataTableApiImpGeneralDisabled
			console.log("disableEditableTables() domId: "+domId +" is Ready!!!");
		
			var table = $('#'+curDomId).DataTable();	
		    var cells = table.cells().nodes();
		    $(cells).find('textarea,i.fa-calculator,i.fa.fa-bars,input:not(.dataTableApiSelectAllNone,.dataTableApiSelectInfoLabel)').addClass('disablePage');//fixed bug 7859	    
		    $(cells).find('select.editableSmartCell').prop("disabled", true).trigger('chosen:updated');  
			$(cells).find('input[type="checkbox"].disablePage').prop("disabled", true);	
			$(cells).find('div.contentEditableMarker').addClass('disablePage').attr("contenteditable", false);
			   
		}
	}
}

function dtExt_getTimeValue(a) {     

	if (a == '')
        return null;

    var time = a.match(/(\d+)(?::(\d\d))?\s*(P?)/); 
    if (time == null)
        return null;

    var hours = parseInt(time[1], 10);
    if (hours == 24 && !time[3]) {
        hours = 0;
    }
    else {
        hours += (hours < 24 && time[3]) ? 24 : 0; // ???
    }

    var d = new Date();
    d.setHours(hours);
    d.setMinutes(parseInt(time[2], 10) || 0);
    d.setSeconds(0,0);
    return d;
}

function initFixedHeaders(domId)
{
	if(domId == "action")
	{
		$('#'+domId).fixedtableheader({originTableID:domId}); 		
	}
}

/*Function prevents from default inserting <div> on Enter press in 'div[contenteditable=true]' element; 
 * now 'display:inline-block' do the work
 */
function dtExt_editableDivMoveCaretDownOnEnter(e)
{    
	e.preventDefault(); // Prevent the <div /> creation.
    var selection = window.getSelection(),
    	range = selection.getRangeAt(0),
    	br = document.createElement('br');	        
    // workaround: insert '&nbsp;' before 'br' because insert only 'br' doesn't work properly: caret does not moved to a new row for the first time
    var nnode = document.createTextNode('\u00A0'); //&nbsp;
	range.deleteContents(); 
	//TODO: insert node '&nbsp;' only when caret is on the end of the row
	range.insertNode(nnode);			
	range.insertNode(br);
	range.setStartAfter(br);
	range.setEndAfter(br);
    range.collapse(false);
    selection.removeAllRanges();
    selection.addRange(range);
}


function addSummaryLine(domId) {
	try {
		var bl_obj = bl_getSummaryLineData(domId);
		if (bl_obj != "") {
			var table = $('#' + domId).DataTable();
			var isTableEmpty = $(table.table().body()).find('td').hasClass(
					'dataTables_empty');

			if (!isTableEmpty) {//If the table is empty, the summary row will not be displayed
				var type_ = bl_obj.type;//Summary type: sum, average, etc. (currently only sum is developed)
				var colsArr = bl_obj.columnList;//List of columns for summary
				var precision = (bl_obj.precision !== undefined && bl_obj.precision != "") ?  bl_obj.precision : "3";
				
				var isFirstVisibleCol = false;
				
				var newRow = $('[id="' + domId + '"] tbody')[0].insertRow();//insert summary row
				newRow.id = "summaryLine";
				
				table.columns().iterator(
						'column',
						function(ctx, idx) {

							var headerObj = table.column(idx).header();
							var _title = getColumnUniqueName($(headerObj));
							var visible = table.column(idx).visible();//
							
							if (visible) {//Check if the column is displayed (not hidden or removed)
								if (colsArr.indexOf(_title) != "-1") {//The column header is in the colsArr list
									var res = calcColoumnByIndex(domId, idx, type_);
									if (res != undefined) {
										res = res.toFixed(precision);
									}
									var newCell = $(newRow.insertCell());//insert new cell 
									newCell.text(res);
									newCell.css("background-color","#e5e5e5");
									isFirstVisibleCol = true;
								} else {
									var ncell = $(newRow.insertCell());//insert new cell 
									ncell.css("background-color","#e5e5e5");
									if (table.column(idx).visible()
											&& !isFirstVisibleCol) {//"Total" is displayed in the first column
										isFirstVisibleCol = true;
										ncell.text("Total");
										ncell.css('font-weight', 'bold');
									}
								}

							}

						});
			}
		}
	} catch (e) {
		try {
			newRow.parentNode.removeChild(newRow);
		} catch (e) {
		}
		console.log("error in addTotalLine. e = " + e);
	}
}
function calcColoumnByIndex(domId, columnInd,type) {
	var table = $('#' + domId).DataTable();
	if(type == "sum"){//summarize the column
		var pageTotal = table
	    .column( columnInd, { search: 'applied'} )
	    .data()
	    .reduce( function (a, cdata) {
	    	var value = 0;
	    	if (cdata!= undefined && checkIfJSON(cdata)) // check if json or not
			{
				value = parseFloat(getDisplayValueFromSmarts(cdata));
				if(!isNaN(value)){
					return parseFloat(a) + parseFloat(value);
				}
				else{
					return parseFloat(a);
				}
			}else{
				value = parseFloat(cdata);
				if(!isNaN(value)){
					return parseFloat(a) + parseFloat(value);
				}
				else{
					return parseFloat(a);
				}
			}
	    }, 0 );
	}
	return pageTotal;
}

function filterColumn(domId,_title){
	var index = getColumnIndexByColHeader(domId, _title);
	if(index == undefined || index == ""){
		return;
	}
	if ($('#filterDialog').dialog('isOpen')===true) {
		$('#filterDialog iframe').attr('src', 'about:blank');
		$('#filterDialog').remove();
		//$("#mask").hide();
		
		return;
	}
	 try{ 
		 var selectedTable = $('#' + domId).DataTable();
         var position_ = $(selectedTable.column(index).footer()).find('.firstString').offset();
         var left = position_.left;
	     var top = position_.top;
	      
	     var input = $(selectedTable.column(index).footer()).find('input[class="firstString"]');
	     var filter_input_val =  $(input).val();
	     $(input).val(''); 
		var selectEmptyValues = false;//The flag is added to check if the OK button is clicked without selecting values or if an empty value was selected
		 var $dialog = $('<div id="filterDialog" class="ui-dialog-content ui-widget-content" style="padding:5px;"></div>')
         .html('<iframe style="border: 0px;width:100%;height:100%;" ></iframe>')//position:fixed;
         .dialog({
             autoOpen: false,
             modal: true,
             height: 300,
            // width: 250,
             position: {
                 my: "left top",
                 at: "left bottom",
                 of:  $(selectedTable.column(index).footer()).find('#filterIcon')
             },
             close: function () {
            	 $('#filterDialog iframe').attr('src', 'about:blank');
                 $('#filterDialog').remove();
                 //$("#mask").hide();
                 },
				open: function(event, ui) 
				{
					$('#filterDialog').siblings( ".ui-dialog-titlebar" ).css( "display", "none" );//remove the dialog title
					$('.ui-dialog-buttonset').css( "padding", "10px" );
					
					 selectedTable.columns(index, { search: 'applied'}).every( function () {
						 if(filter_input_val!=""){
							 this .search( '', true, false )
						        .draw();
						 }
						
						 //create multiple checkbox in ddl
						 var ulElem = "<div id=\"listCB\" class=\"dropdown-check-list\"  style=\"max-height:100%;overflow:auto\">"
				    		 +"<ul id=\"checkList"+index+"\" style=\"list-style:none;padding-top:10px;\"></ul></div>";
						 var _header = $(this.header())[0];
					     var _title = getColumnUniqueName($(_header))
					     var checkedArr = [];
					     try{
					    	 checkedArr = globalDataTableFilterColumn!=undefined?globalDataTableFilterColumn[domId][_title]:[];
					     }catch(e){
					    	 checkedArr =[];
					     }
				    		
						 var column = this;
				    	 $('#filterDialog').html(ulElem);
				    	 var emptyVal ="";
				    	 column.data().unique().sort(function (a,b) {//userDateFormatClient=DD/MMM/YYYY
				             return moment(a, prop.dateFormat.userDateFormatClient).unix() - moment(b, prop.dateFormat.userDateFormatClient).unix();
				         }).each( function ( val, idx ) {
				    		var checked = "";
				    		 if(val==""){
				    			 if (checkedArr != undefined && checkedArr.indexOf(val) != '-1') {
				    				 checked = "checked";
				    				 }
				    			 var label_ = "(Blanks)";
				                 var elem = "<li >";
				                 emptyVal += "<input type='checkbox' id='cb"+idx+"' value='"+val+"'"+checked+" ></>";
				                 emptyVal += "<label title='"+label_+"'>"+label_+"</label>";
				                 emptyVal += "</li>";
				    		 }
				    		 else if (val!= undefined && checkIfJSON(val)) // check if json or not
				 			{
				 				var value = getDisplayValueFromSmarts(val);
				 				if (checkedArr != undefined && (checkedArr.indexOf(value) != '-1'|| checkedArr.indexOf(value.replace(/\\/g, "\\").replace(/\n/g, "\\n")
					        			.replace(/\r/g, "\\r")
					        			.replace(/\t/g, "\\t") 
					        			.replace(/\f/g, "\\f"))!='-1')) {
							    	checked = "checked";
								}
				 				
				 				if(value!= undefined && value!=""){
				 					var label_ = value.length>30?value.trim().slice(0,27)+"...":value;
					                var elem = "<li >";
					                elem += "<input type='checkbox' id='cb"+idx+"' value='"+value+"'"+checked+" ></>";
					                elem += "<label title='"+value+"'>"+label_.replace(/\n/g, "")+"</label>";
					                elem += "</li>";	
					                $('#checkList'+index).append(elem);
				 				}
				 			}else{
				 			//val = val.replace(/<\/?[^>]+(>|$)/g, "");//remove special characters and html tags
				 			    if (checkedArr != undefined && (checkedArr.indexOf(val) != '-1'||checkedArr.indexOf(val.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&")) != '-1'
				 			    		||checkedArr.indexOf(val.replace(/\\/g, "\\").replace(/\n/g, "\\n")
							        			.replace(/\r/g, "\\r")
							        			.replace(/\t/g, "\\t") 
							        			.replace(/\f/g, "\\f"))!='-1')) {
				 			    	checked = "checked";
				 			    	}
				    		    var label_ = val.length>30?val.slice(0,27)+"...":val;
				    		    var elem = "<li >";
			                	elem += "<input type='checkbox' id='cb"+idx+"' value='"+val/*.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&")*/+"'"+checked+" ></>";
			                	elem += "<label title='"+val+"'>"+label_.replace(/\n/g, "")+"</label>";
			                	elem += "</li>";	
			                	$('#checkList'+index).append(elem);
				 			}
			                } );
				    	 if(emptyVal!=""){//Empty values should be at the bottom of the list
				    		 $('#checkList'+index).append(emptyVal);
				    	 }
			            } );
					
				},
				buttons: { "OK": function() {
					//$("#mask").hide();
					try{
					var selectedTable = $('#' + domId).DataTable();
					 selectedTable.columns(index).every( function () {
						 var data = [];
						 var arr = [];
			    		 $('#checkList'+index+'  input[type="checkbox"]:checked').each(function(){//select all checked values
			    			 if(this.value ==""){
			    				 selectEmptyValues = true;//Blank value selected
			    			 }
			    			 parseVal = this.value.replace(/\\/g, "\\") // backslash
								.replace(/\n/g, "\\n")   //new line
			        			.replace(/\r/g, "\\r")  // carriage return
			        			.replace(/\t/g, "\\t")  // tab
			        			.replace(/\f/g, "\\f"); //form-feed char
								data.push(parseVal);// data for save display
			    			 arr.push(this.value.replace(/[*()?\[\]^\\$|_=+-]/g, "\\$&"));//data for search
			    			});
			    		
			    		 var val = data.join('|');
						 var _header = $(this.header())[0];
						 var _title = getColumnUniqueName($(_header));
						 var column = this;
						 setGlobalDataTableFilter(domId,_title,val,selectEmptyValues);
						if(!selectEmptyValues && val ==""){
							column
							 .search( val ? '^'+val+'$' : '', true, false )
							 .draw();
						}else{
						 val = val.replace(/\n/g, " ");//remove html tags
							var pattern = ("\^" + arr.join('\$|\^') + '\$');
				                column.search(pattern, true, false).draw();
						 }
						
						 if(val!="" || selectEmptyValues){
							  $(selectedTable.column(index).footer()).find('#filterIcon').attr("src", "../skylineFormWebapp/images/filter.png");
						 }else if(!selectEmptyValues){
							 $(selectedTable.column(index).footer()).find('#filterIcon').attr("src", "../skylineFormWebapp/images/filter_empty.png");
						 }
					 });
					
					 $(this).dialog("close");
					}catch(e){
						 $(this).dialog("close");
						console.log("search filter error",e);
						//console.error(e);
					}} } 
         });
		 
		 $dialog.dialog('option', 'dialogClass', 'noTitleStuff').dialog('open');
		 
         }catch(e){
     		//$("#mask").hide();
        	 console.log("open filter error",e);
        	 //console.error(e);
         }
}

function searchSaveDisplay(domId){
	try {
		if (bl_initFilterColumnDatatable() && isSameStructTable(domId)) {
			var colNames = globalDataTableFilterColumn[domId];
			var selectedTable = $('#' + domId).DataTable();
			$.each(colNames, function(_title, data) {
				var index = getColumnIndexByColHeader(domId, _title);
				var that = selectedTable.column(index);
				data = data.map(function(item) {
					return item.replace(/[*()?\[\]^\\$|_=+-]/g, "\\$&");
				});
				var pattern = ("\^" + data.join('\$|\^') + '\$');
				that.search(pattern, true, false).draw();
			});
		}}
	catch(e){
		console.log("searchSaveDisplay error",e);
	}
}