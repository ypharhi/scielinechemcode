
    var _handlerArray;    
    
    function fnDrawTable(currTableID, propertiesArray, source, additOptionsArr, labelsArr)
    {
        _handlerArray = (additOptionsArr != null)?additOptionsArr:{};
        var _colNamesArr = (labelsArr != null)?labelsArr:{};
        /******  Default table properties  *******/
        var _bServerSide = true;                
        var _bProcessing = true;                    
        var _sPaginationType = "full_numbers";     
        var _iDisplayLength = 10;                   
        var _bJQueryUI = true;                     
        var _sDom = "Tlfrtip";      
        var _bPaginate = true;
        var _bFilter = true;
        var _bSort = true;
        var _bInfo = true;
        var _bAutoWidth = true;
        var _sScrollY = "";
        //var _sScrollX = "";      
        var _bDestroy = false;
        var _bLengthChange = true;
        var _bColumnFilter = true;
        var _bFooter = true;
        var _bRowSelected = true;
        var _bClicked = true;
        var _bDblClicked = true; 
		var _bScrollCollapse = false;
        /******************************/
         
         /******  Default column properties  *******/
         var _bVisible = true;
         var _bSearchable = true;
         var _bSortable = true;
         var _sSortDataType = null;
         var _sType = null;
         var _sDateFormat = null;
         var _sWidth = null;
         var _sClass = "";
         var _sTitle = "";         
         /******************************/
              
        _bServerSide = (propertiesArray[0].bServerSide != null)? propertiesArray[0].bServerSide : _bServerSide;  
        _bColumnFilter = (propertiesArray[0].bColumnFilter != null)? propertiesArray[0].bColumnFilter : _bColumnFilter; 
        _bFooter = (propertiesArray[0].bFooter != null)? propertiesArray[0].bFooter : _bFooter; 
        _bRowSelected = (propertiesArray[0].bRowSelected != null)? propertiesArray[0].bRowSelected : _bRowSelected;
        _bClicked = (propertiesArray[0].bClicked != null)? propertiesArray[0].bClicked : _bClicked;
        _bDblClicked = (propertiesArray[0].bDblClicked != null)? propertiesArray[0].bDblClicked : _bDblClicked;
        
         /** set array of column properties **/   
            var colDefinArr = [];   
            var colFilterTypeArr = [];
            for (var i=1; i < propertiesArray.length; i++ ) 
            {       
                colDefinArr.push({                
                    "aTargets": [i-1],
                    "bVisible": (propertiesArray[i].bVisible != null)? propertiesArray[i].bVisible : _bVisible,
                    "bSearchable": (propertiesArray[i].bSearchable != null)? propertiesArray[i].bSearchable : _bSearchable,
                    "bSortable": (propertiesArray[i].bSortable != null)? propertiesArray[i].bSortable : _bSortable, 
                    "sClass": (propertiesArray[i].sClass != null)? propertiesArray[i].sClass : _sClass,
                    "sWidth": (propertiesArray[i].sWidth != null)? propertiesArray[i].sWidth : _sWidth,
                    "sTitle": fnSetTitle(propertiesArray[i], _colNamesArr, _bVisible),
                    "sSortDataType": (propertiesArray[i].sSortDataType != null)? propertiesArray[i].sSortDataType : _sSortDataType,
                    "sType": (propertiesArray[i].sType != null)? propertiesArray[i].sType : _sType,
                    "sDateFormat": (propertiesArray[i].sDateFormat != null)? propertiesArray[i].sDateFormat : _sDateFormat,
                    "fnCreatedCell": function (nTd, sData, oData, iRow, iCol) 
                                    {
                                        if(propertiesArray[0].fnCreatedCell) 
                                        {
                                            if(_handlerArray.fnCreatedCell != null)
                                                _handlerArray.fnCreatedCell.apply(this, [nTd, sData, oData, iRow, iCol, currTableID]);
                                            else
                                                fnCreatedCell(nTd, sData, oData, iRow, iCol, currTableID);
                                        }
                                      //  alert('nTd: '+nTd+', sData: '+sData+', oData: '+oData+', iRow: '+iRow+', iCol: '+iCol);
                                    }
                }); 
                if(_bColumnFilter)
                {   
                    if(propertiesArray[i].sFilterType != null)
                    {
                        if(propertiesArray[i].sFilterType == "")
                        {
                            colFilterTypeArr.push(null);
                        }
                        else if(propertiesArray[i].sFilterType == "select")
                        {
                            colFilterTypeArr.push({type: "select", values: fnGetValuesForColumnFilterTypeSelect()});
                        }
                        else if(propertiesArray[i].sFilterType == "number")
                        {
                            colFilterTypeArr.push({type: "number"});
                        }
                        else
                            colFilterTypeArr.push({type: "text"});
                    }
                    else
                        colFilterTypeArr.push({type: "text"});
                }
            }
        /*******************************************/
        /** add table footer **/
            if(_bFooter)
            {
                var footer = '<tfoot><tr>';
                for(var i=0; i < colDefinArr.length; i++)
                {
                    footer += '<th></\th>';
                }
                footer += '</></>';
                $('#' + currTableID).append(footer);
            }
        /*********************************************/
            
            /** TABLE initializing **/
       var tableObject = $("#" + currTableID).dataTable
                        ({
                            "bServerSide": _bServerSide,
                            "sAjaxSource": (_bServerSide) ? source : null,
                            "aaData": (_bServerSide) ? null : source, 
                            "aaSorting": [], 
                            "bLengthChange": (propertiesArray[0].bLengthChange != null)? propertiesArray[0].bLengthChange : _bLengthChange,
                            "aLengthMenu": (propertiesArray[0].aLengthMenu != null)? propertiesArray[0].aLengthMenu : fnLengthMenu(currTableID),
                            "bProcessing": (propertiesArray[0].bProcessing != null)? propertiesArray[0].bProcessing : _bProcessing,
                            "bFilter": (propertiesArray[0].bFilter != null)? propertiesArray[0].bFilter : _bFilter,
                            "bSort": (propertiesArray[0].bSort != null)? propertiesArray[0].bSort : _bSort,
                            "bInfo": (propertiesArray[0].bInfo != null)? propertiesArray[0].bInfo : _bInfo,
                            "sScrollY": (propertiesArray[0].sScrollY != null)? propertiesArray[0].sScrollY : _sScrollY,
							//"bScrollCollapse": (propertiesArray[0].bScrollCollapse != null)? propertiesArray[0].bScrollCollapse : _bScrollCollapse,
                            //"sScrollX": (propertiesArray[0].sScrollX != null)? propertiesArray[0].sScrollX : _sScrollX,
                            "bPaginate": (propertiesArray[0].bPaginate != null)? propertiesArray[0].bPaginate : _bPaginate,
                            "sPaginationType": (propertiesArray[0].sPaginationType != null)? propertiesArray[0].sPaginationType : _sPaginationType,
                            "iDisplayLength": (propertiesArray[0].iDisplayLength != null)? propertiesArray[0].iDisplayLength : _iDisplayLength,
                            "bJQueryUI": (propertiesArray[0].bJQueryUI != null)? propertiesArray[0].bJQueryUI : _bJQueryUI,
                            "sDom": (propertiesArray[0].sDom != null)? propertiesArray[0].sDom : _sDom,
                            "bDestroy": (propertiesArray[0].bDestroy != null)? propertiesArray[0].bDestroy : _bDestroy,
                            "oLanguage": (propertiesArray[0].oLanguage != null)? propertiesArray[0].oLanguage : fnLanguage(currTableID),
                            "bAutoWidth": (propertiesArray[0].bAutoWidth != null)? propertiesArray[0].bAutoWidth : _bAutoWidth,
                            "aoColumnDefs": colDefinArr,
                            "fnDrawCallback": function( oSettings ) 
                             {          
                                if(propertiesArray[0].fnDrawCallback)
                                {
                                    if(_handlerArray.fnDrawCallback != null)
                                        _handlerArray.fnDrawCallback.apply(this, [oSettings, currTableID]);
                                    else
                                        fnDrawCallback(oSettings, currTableID);      
                                }
                             },
                             "fnCreatedRow": function( nRow, aData, iDataIndex )
                             {             
                                if(propertiesArray[0].fnCreatedRow)
                                {
                                    if(_handlerArray.fnCreatedRow != null)
                                        _handlerArray.fnCreatedRow.apply(this, [nRow, aData, iDataIndex, currTableID]);
                                    else
                                        fnCreatedRow(nRow, aData, iDataIndex, currTableID);
                                }
                             },
                             "fnHeaderCallback": function( nHead ) 
                             {  
                                if(_handlerArray.fnHeaderCallback != null)
                                        _handlerArray.fnHeaderCallback.apply(this, [nHead]);
                             },
                             "fnServerParams": function (oSettings, aoData ) 
                             {
                                //aoData.push( { "name": "", "value": "" } );
                             },
                             "fnServerData": function ( sSource, aoData, fnCallback ) 
                             {     
                                  if(_bServerSide)
                                        fnServerData(sSource, aoData, fnCallback);
                             }, 
                             "fnInitComplete": function(oSettings, json) 
                             {
                                if(propertiesArray[0].fnInitComplete)
                                {
                                    if(_handlerArray.fnInitComplete != null)
                                        _handlerArray.fnInitComplete.apply(this, [oSettings, json, currTableID]);
                                    else
                                        fnInitComplete(oSettings, json, currTableID);
                                }
                             },
                             "fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) 
							 {
									if(propertiesArray[0].fnRowCallback)
									{
										if(_handlerArray.fnRowCallback != null)
											_handlerArray.fnRowCallback.apply(this, [nRow, aData, iDisplayIndex, iDisplayIndexFull, currTableID]);
										else
											fnRowCallback(nRow, aData, iDisplayIndex, iDisplayIndexFull, currTableID);
									}
                             }
                        });
            /****************************************/
            /** add filter to table footer **/
                if(_bFooter && _bColumnFilter)
                {
                    $("#" + currTableID).dataTable().columnFilter
                    ({ 
                        aoColumns: colFilterTypeArr,//fnAddColumnFilterWidget(currTableID, colDefinArr),
                        bUseColVis: true
                    });
                }
            /*********************************/         
                if(_bRowSelected)  
                {
                    if(_handlerArray.fnSelectTableRow != null)
                        _handlerArray.fnSelectTableRow.apply(this, [currTableID]);
                    else
                        fnSelectTableRow(currTableID);
                }
                if(_bClicked || _bDblClicked) 
                    fnRowClicked(currTableID, _bClicked, _bDblClicked);
    
			return tableObject;
    }
    
    /* By http://legacy.datatables.net/usage/callbacks
     * Called when the table has been initialised. 
     */
    function fnInitComplete(oSettings, json, currTableID)
    {       
        return;
    }
	
    /* By http://legacy.datatables.net/usage/callbacks
     * This function allows you to 'post process' each row after it have been generated for each table draw, but before it is rendered on screen. 
     * This function might be used for setting the row class name etc.
     */
	function fnRowCallback(nRow, aData, iDisplayIndex, iDisplayIndexFull, currTableID)
	{
		/*"fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
		      // Bold the grade for all 'A' grade browsers
		      if ( aData[4] == "A" )
		      {
		        $('td:eq(4)', nRow).html( '<b>A</b>' );
		      }
		    }*/
		return;
	}
    
    function fnGetValuesForColumnFilterTypeSelect()
    {
        var arr = [];
        arr = ["Location_A", "Location_B","Location_C","Location_D"];
        return arr;
    }
    
    function fnLanguage(tableID)
    {       
        return {"sSearch": "Global Search:"};
        //return {"sUrl": "Langs/Datatables/english.txt" };
    }
    
    function fnSetTitle(prop, colNamesArr, bVisible)
    {
            var colName = prop.sTitle;
            var visibility = (prop.bVisible != null)? prop.bVisible : bVisible;
            
            if(visibility && colNamesArr[prop.sTitle] != null)
            {                                
                colName = (colNamesArr[prop.sTitle]).toUpperCase();
            }
			else if(visibility && prop.is_image)
			{
				colName = '<img src="images/'+prop.sTitle+'" style="cursor: default;">';
			}

            return colName;
    }
    
    function fnLengthMenu(tableID)
    {       
        return [ 10, 25, 50, 100 ];
    }
    
    function fnAddColumnFilterWidget(tableID, colDefinArr)
    {
       var arr = [];
       for(var i=0; i < colDefinArr.length; i++)
       {
            if(i==0)
                arr.push(null);
            else
                arr.push({type: "text"});
       }
        return arr;
       //return;
    }

    function fnSelectTableRow(tableID)
    {
        //alert("parent: " + tableID);
        $('#' + tableID +' tbody').on('click', 'tr', function(event)
        { 
            var aData = $("#" + tableID).dataTable().fnGetData(this);
            if(aData != null)
            {
                if ($(this).hasClass('row_selected')) 
                {  
                    if(_handlerArray.fnOnRemoveRowSelection != null)
                           _handlerArray.fnOnRemoveRowSelection.apply(this, [tableID]);                 
                   $(this).removeClass('row_selected'); 
                } 
                else 
                {
                    $($("#" + tableID).dataTable().fnSettings().aoData).each(function () {
                        $(this.nTr).removeClass('row_selected');
                    }); 
                    if(_handlerArray.fnOnRowSelection != null)
                           _handlerArray.fnOnRowSelection.apply(this, [aData, tableID]); 
                    $(this).addClass('row_selected');
                } 
            }
        });  
        /*
            in case click was occured on td:
                - with built-in html object:
                        'event.target.parentNode' means - 'object HTMLTableDataCellElement';
                                            'this' means - 'object HTMLTableRowElement';
                - without built-in html object:
                        'event.target.parentNode' and 'this' means the same - 'object HTMLTableRowElement';
            
        */
    }
   
    function fnRowClicked(tableID, isClick, isDblClick)
    {
        $('#' + tableID +' tbody').on('click double', 'tr', function(event)
        { 
            var aData = $("#" + tableID).dataTable().fnGetData(this);
            if(aData != null)
            {
                if(isClick)
                {
                    //console.log('click');
                    if(_handlerArray.fnOnClick != null)
                       _handlerArray.fnOnClick.apply(this, [aData, tableID]); 
                }
                
                $('#' + tableID + ' tbody tr').off('dblclick'); // remove pervious handler                
                if(isDblClick)
                {
                    $('#' + tableID + ' tbody tr').dblclick(function() // assigns hadnler
                    {
                            if(_handlerArray.fnOnDblClick != null)
                                _handlerArray.fnOnDblClick.apply(this, [aData, tableID]);
                            //console.log('dblclick');
                            //$('#' + currTableID + ' tbody tr').off('click');
                    });
                }
            }
        });           
    }
    
    /* By http://legacy.datatables.net/usage/callbacks
     * This function is called on every 'draw' event, and allows you to dynamically modify any aspect you want about the created DOM.
     */
    function fnDrawCallback(settings, tableID)
    {
        $($("#" + tableID).dataTable().fnSettings().aoData).each(function () {
            $(this.nTr).removeClass('row_selected');
        }); 
        if(_handlerArray.fnOnDrawCallback != null)
        {
            _handlerArray.fnOnDrawCallback.apply(this, [settings, tableID]);
        }
    }
    
    function fnCreatedCell(nTd, sData, oData, iRow, iCol, tableID)
    {
        /**
        "fnCreatedCell": function (nTd, sData, oData, iRow, iCol)
            nTd - current td : 
                if ( sData == "1.7" ) {
                  $(nTd).css('color', 'blue')
                }
            sData - current cell data 
            oData - current row data
            iRow - row index
            iCol - column index
        **/
        return;
    }
    
    
    /* By http://legacy.datatables.net/usage/callbacks
     * This function is called when a TR element is created (and all TD child elements have been inserted), 
     * or registered if using a DOM source, allowing manipulation of the TR element (adding classes etc) */
    function fnCreatedRow(nRow, aData, iDataIndex, tableID)
    {
         
        /** 
            nRow - current row object; aData - current row data; iDataIndex - row index                 
            i - curr cell index, o - curr cell data        
            Ex.:
                 if(aData[4] == 'A')
                     $('td:eq(4)', nRow).html( '<b>A</b>' );
             or
                 $.each(aData, function (i, o) 
                 {
                    $("td:eq("+ i +")", nRow).css("color", "red");
                 });
        **/
       return;
    }
    
    /* By http://legacy.datatables.net/usage/callbacks
     * This function is called on every 'draw' event, and allows you to dynamically modify the header row. 
     * This can be used to calculate and display useful information about the table.
     */
    function fnHeaderCallback(nHead, aData, iStart, iEnd, aiDisplay, tableID)
    {
        /**
        "fnHeaderCallback": function( nHead, aData, iStart, iEnd, aiDisplay ) 
        {
		      nHead.getElementsByTagName('th')[0].innerHTML = "Displaying "+(iEnd-iStart)+" records";
		}
        **/
        return;
    }
    
    /* By http://legacy.datatables.net/usage/callbacks
     * This parameter allows you to override the default function which obtains the data from the server
     */
    function fnServerData(sSource, aoData, fnCallback)
    {
        $.ajax
        ({         
            "dataType": 'json',         
            "type": "POST",         
            "url": sSource,         
            "data": aoData,         
            "success": fnCallback,         
            "error": handleAjaxError  // this sets up jQuery to give me errors     
          } ); 
    }
   
    // Override default implementation for date sorting
    $.extend($.fn.dataTableExt.oSort, 
    {        
        "date_c-pre": function (a) 
        {
            var x = 0;
            if ( $.trim(a) != "")
            {
                if(a.indexOf(':') != -1)
                {
                  var tmp = a.split(' ');
                  var datea = tmp[0].split('/');
                  var timea = tmp[1].split(':');
                  x = (datea[2] + datea[1] + datea[0] + timea[0] + timea[1]) * 1;
                }
                else
                {
                    var datea = a.split('/');
                    x = (datea[2] + datea[1] + datea[0]) * 1;
                }
             }
             else
             {
               x = 10000000;
             }
            return x;           
        },
        "date_c-asc": function (a, b) {
            return ((a < b) ? -1 : ((a > b) ? 1 : 0));
        },
        "date_c-desc": function (a, b) {
            return ((a < b) ? 1 : ((a > b) ? -1 : 0));
        }
    });
    
    /** Used in case of bServerSide=false only ** /
    
        /* Create an array with the values of all the input boxes in a column */
        $.fn.dataTableExt.afnSortData['dom-text'] = function  ( oSettings, iColumn )
        {
                return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
                        return $('td:eq('+iColumn+') input', tr).val();
                } );
        }
        
        /* Create an array with the values of all the select options in a column */
        $.fn.dataTableExt.afnSortData['dom-select'] = function  ( oSettings, iColumn )
        {
                return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
                        return $('td:eq('+iColumn+') select', tr).val();
                } );
        }
        
        /* Create an array with the values of all the checkboxes in a column */
        $.fn.dataTableExt.afnSortData['dom-checkbox'] = function  ( oSettings, iColumn )
        {
                return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
                        return $('td:eq('+iColumn+') input', tr).prop('checked') ? '1' : '0';
                } );
        }
    
    /**************************/