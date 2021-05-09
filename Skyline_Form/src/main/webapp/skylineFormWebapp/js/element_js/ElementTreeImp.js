var ElementTreeImp = {
    value_: function (val_) {
    	var searchInput = $('#' + $(val_).attr("id") + '_input').val().length < 3 ? "" : $('#' + $(val_).attr("id") + '_input').val();
        var o = '{"_tree_lastValue":"' + $("#" + $(val_).attr("id") + "_tree_lastValue").val() +
            '","_selected":"' + $("#" + $(val_).attr("id") + "_selected").val() + 
            '","_filter":"' + $("#" + $(val_).attr("id") + "_ddlFilterProject").val() +
            '","_searchRow":"' + searchInput + '"}';
        return o;
    },
    setvalue_: function (val_) {
        //important: should add attribute 'lastvalue' to the element imp in the poll (like in ElementAutoCompleteIdValDDLImp)
    },
    setDefaultValueForUnitTest_: function (val_) {
    }
};

function loadLastSaveTree(tree, formCode, data) {
	try
	{
	    $('#' + tree).off("loaded");
	    var arr = [];
	    if (data != "") {
	    	arr = JSON.parse(data)._tree_lastValue.split(',');
	    }
	    if (arr == ""){  
	    	arr = ["#","-1"]; // kd 11082020 task-24778 
	    }
	    loadTree(arr, arr.length, tree, 1, "");
	} catch(e){}
}

function loadTree(arr, arrayLength, tree, i, e) {
    //load the tree by last save value
	try 
	{
	    if (i == arrayLength - 1) {
	        var str;
	        if (i == 1) {
	        	// kd 10082020 remove previous code and added 5 for solve task-24778
	            if(arr[i]== undefined || arr[i].indexOf('_node_')=='-1'){
	                $('#' + tree).jstree('open_node', '[id="' + arr[i] + '"]', function (e, data) {
	                    loadTree(arr, arrayLength, tree, i + 1, e.id);
	                }, false);
	            }
	        } else {
	            str = $('a[id*="' + arr[arrayLength - 1].substr(0, arr[arrayLength - 1].indexOf('_node_')) + '"]:last').closest('div').attr('id') + '_firstTime';
	            $('[id="' + str + '"]').val("1");
	            $('a[id*="' + arr[arrayLength - 1].substr(0, arr[arrayLength - 1].indexOf('_node_')) + '"]:last').click();
	        }
	        return;
	    } else if (i == 1) {
	    	if(arr[i]== undefined || arr[i].indexOf('_node_')=='-1'){
	        $('#' + tree).jstree('open_node', '[id="' + arr[i] + '"]', function (e, data) {
	            loadTree(arr, arrayLength, tree, i + 1, e.id);
	        }, false);
	    	}
	    	else{
	    		$('#' + tree).jstree('open_node', '[id*="' + arr[i].substr(0, arr[i].indexOf('_node_')) + '"]', function (e, data) {
	                loadTree(arr, arrayLength, tree, i + 1, e.id);
	            }, false);
	    	}
	    } else { 
	    	if (i < arr.length) {
		        $('#' + tree).jstree('open_node', '[id*="' +  arr[i].substr(arr[i].lastIndexOf('_')) + '"]', function (e, data) {
		            loadTree(arr, arrayLength, tree, i + 1, e.id);
		        }, false);//fixed bug 8004
	    	}
	    }
	} catch(e){}
}

/**
 * init jsTree
 * @param impCode
 * @param doOnChangeJSCall
 * @param value
 * @param formCode
 * @returns
 */
function initTree(impCode, doOnChangeJSCall, value, formCode,formId,name,rootMetaData,rootWherePart,parentFormId) {
    $('#' + impCode).on('loaded.jstree', function (e, data) {
        loadLastSaveTree(impCode, formCode, value);
    }).jstree({
        'core': {
            'themes': {
                'icons': false
            },
            'data': {
                'cache': false,
                'url': function (node) {
                	if(rootMetaData!= undefined && node.id === '#'){
                		return './getSpecificTreeRoot.request' + '?view=' + $('#' + impCode + '_catalog_hidden').val()+ "&metaData=" + rootMetaData + "&wherePart="
            			+ rootWherePart;
                	}
                    return node.id === '#' && formId == undefined ? 
                        './getTreeRoot.request' + '?view=' + $('#' + impCode + '_catalog_hidden').val() :
                        './getTreeChildren.request' + '?view=' + $('#' + impCode + '_catalog_hidden').val();
                },
                'contentType': "application/json; charset=utf-8",
                'dataType': 'json',
                'data': function (node) {
                    var str = '';
                    var id_ = node.id;
                    var structs = [];
                    var parentStr = '', i;
                    var strParentsId_ = [];
                    
                    if(id_ == '#' && formId != undefined){//open the tree from specific entity
                    	/*str = node.parents.toString().split(',');
                    	node.id = name+"_node_";
                    	node.a_attr = {"formId":formId,"struct":formCode,"child":""};*/
                    	//node.children = true;
                    }
                    if (id_ != '#') {
                        str = node.parents.toString().split(',');
                        if ((str.length > 1) && (str[0] == '#')) {
                            str.reverse();
                        }
                        str.splice(str.length - 1, 1); // remove '#'
                        while (id_ != '-1' && id_!=undefined) {
                            id_ = $('[id="' + id_ + '"]').parents('li').attr('id');
                            var formId_ = $('[id="' + id_ + '"] a:first').attr('formId')
                            if(id_!=undefined){
                            	structs.push($('[id="' + id_ + '"] a:first').attr('struct'));
                            	strParentsId_.push(formId_);
                            }
                        }
                    }
                    return {
                        'id': encodeURIComponent((id_ == '#' || id_ == '-1')? (formId != undefined ? formId:node.id):node.a_attr.formId ),//encodeURIComponent((id_ == '#' && formId != undefined) ? name+"_node_":node.id ),
                        'parents': encodeURIComponent(strParentsId_.toString()),//encodeURIComponent(str.toString()),
                        'column': (id_ != '#') ? node.a_attr.child : '',
                        'structs': encodeURIComponent(structs.toString()),
                        'formId': parentFormId!=undefined&& parentFormId!=null?parentFormId:"",
                        'stateKey': $('#stateKey').val(),
                        'criteria': $('#' + impCode + '_ddlFilterProject').length > 0 ?$('#' + impCode + '_ddlFilterProject').val():"ALL",
                        'currentstruct': (id_ != '#') ? node.a_attr.struct : (formId != undefined?formCode:'') //|| formId != undefined
                    };
                },
                'success':function(data){
                	if(data.length==1 && data[0].hasOwnProperty('error')){
                		displayAlertDialog(data[0].error);
                		return [];
                	}
                	data = addIconsToLevelsOfTree(data,''); // add icons
                	return data;
                }
            }
        }
    });
//    .bind("search.jstree", function (e, data) {
//        alert("Found " + data.nodes.length + " nodes matching '" + data.str + "'.");
//    });
    $('#' + impCode).on('changed.jstree', function (e, data) {
        if (data.action != 'ready') {
            var str = data.node.parents.toString();
            if (str != '') {
                if (str.indexOf('#') != 0) {
                    str = str.split(',').reverse().toString();
                }
            }
            $('#' + impCode + '_tree_lastValue').val(str + ',' + data.node.id);
            var selectedFormid = $('[id="' + data.node.id + '"] a:first').attr('formid');
            var selectedStruct = $('[id="' + data.node.id + '"] a:first').attr('struct');
            if (typeof selectedFormid === 'undefined') {
                selectedFormid = '';
                selectedStruct = '';
            }
            var selected = selectedFormid + ',' + selectedStruct;
            $('#' + impCode + '_selected').val(selected);
            if ($('#' + impCode + '_firstTime').val() == '0') {
                // eval(doOnChangeJSCall);
            } else {
                $('#' + impCode + '_firstTime').val('0');
            }
        }
    });

    $('#' + impCode).on("dblclick.jstree", function (event) {
        var node, formId, formCode, page;
        //showWaitMessage(getSpringMessage('pleaseWait'));
        node = $(event.target).closest("li");
        formId = $('a', node).attr('formId');
        formCode = $('a', node).attr('struct');
        
        if(formCode != null && formCode == 'Run') {
        	var runNumber = node[0].innerText.split('Run ')[1];
        	formId = formId.substring(0,formId.indexOf('#'));
        	var cName = $('#userId').val()+':'+formId+':'+'$RUNNUMBER';
			setCookie(cName,runNumber,365);
            formCode = 'Experiment';
        }

        if (!formCode || formCode === 'Root') {
            //hideWaitMessage();
            return;
        }

        checkAndNavigate([''+formId+'',''+formCode+'','','false']);
    });
    
    $('#' + impCode).on('ready.jstree', function (e, data) { 
        console.log("ready.jstree");
        initTreeContextMenu();
    });
}

function openFloatTree(elementObj,id,formCode,name){
	if($('#tree').length != 0){
		var countNodes = $('#tree').jstree()._cnt;
		$('#tree').parent().remove();
		$(document.body).off('click');
		var $openedTreeRootElem = $('[isTreeRootBreadcrumb="true"]');
		$openedTreeRootElem.attr('isTreeRootBreadcrumb','false');
		if($(elementObj).attr('encapsulated')!= undefined && $(elementObj).attr('encapsulated')=='false'){
			$(elementObj).attr('encapsulated','true');
			return;
		}
		$openedTreeRootElem.attr('encapsulated','true');
	}
	var elementHtml='<div style="margin-top:5px; overflow: auto; max-height:'+$('body').height()*0.80+'px;position:absolute;box-shadow: 0px 4px 13px 0px rgba(0,0,0,0.48); border: 1px solid #cacaca; background: #fefefe;">'
		+ '<div id="tree" style=""></div>'
		+ '<input type="hidden" id="tree_catalog_hidden" value="FG_I_TREE_CONNECTION_V">'
		+ '<input type="hidden" id="tree_tree_lastValue" value="">'
		+ '<input type="hidden" id="tree_selected" value="">'
		+ '<input type="hidden" id="tree_filter" value="">'
		+ '<input type="hidden" id="tree_firstTime" value="0">'
		+ '<input type="hidden" id="tree_doOnChangeJSCall" value=" onChangeAjax(\'tree\'); ">';
		$('body').append(elementHtml);
		$( document.body  ).click(function(event) {
			if(event.target.id == "tree")
		          return;
			//for the root icon being clicked-handled in the beginning of the function
			if(event.target==$(elementObj)[0])
				return;
			if($('#tree').length != 0){
				//For descendants of tree being clicked
				if($(event.target).closest('#tree').length)
					return;
				$('#tree').parent().remove();
				$(this).off('click');
				var $openedTreeRootElem = $('[isTreeRootBreadcrumb="true"]');
				$openedTreeRootElem.attr('isTreeRootBreadcrumb','false');
				$openedTreeRootElem.attr('encapsulated','true');
		    }
		});
		/*$('#tree').blur(function() {//on('mouseleave',function() {parent()
		    if($('#tree').length != 0){
				$('#tree').parent().remove();
				return;
		    }
		});*/
		var locationX = $(elementObj).offset().left;
		var locationY = $(elementObj).offset().top;
		var width = $(elementObj).width();
		var height = $(elementObj).height();
		
		$('#tree').parent().css({top:(locationY+height),left:locationX});
		$('#tree').parent().css('z-index','130');
		
		initTree('tree',' onChangeAjax(\'tree\'); ','{"_tree_lastValue":"","_selected":"","_filter":"","_searchRow":""}',formCode,id,name);
		$(elementObj).attr('encapsulated','false');
		$(elementObj).attr('isTreeRootBreadcrumb','true');
}

function openNavigationTree(){
	var formCode = 'NavigationTree';
	var dialogHeight = 700;
	var dialogWidth = 1000;
	var page = "./init.request?stateKey=" + $('#stateKey').val()
			+ "&formCode=" + formCode + "&formId=-1" + "&userId="
			+ $('#userId').val(); 
	// open iframe inside dialog
	var $dialog = $('<div id="prevDialog" style="overflow-y: hidden;""></div>')
			.html(
					'<iframe id="formIframeId" style="border: 0px;width:100%;height:100%" src="'
							+ page /* + urlPermissionDisabled */
							+ '"></iframe>').dialog({
				autoOpen : false,
				modal : true,
				height : dialogHeight,
				width : dialogWidth,
				// title: title,
				close : function() {
					$('#prevDialog iframe').attr('src', 'about:blank');
					$('#prevDialog').remove();
				}
			});

	$dialog.dialog('option', 'dialogClass', 'noTitleStuff').data(
			'parentFormId', $('#formId').val()).data('parentFormCode', $('#formCode').val())
			.dialog('open');
}

function initTreeContextMenu()
{
	var $target = $(".jstree-container-ul"); 
	$target.contextmenu(
	{
		delegate: "a.jstree-anchor[struct!='Root']",
		autoFocus: true,
	    preventContextMenuForPopup: true,
	    preventSelect: true,
	    closeOnWindowBlur:true,
		menu: [
				{title: "Open in a new tab", cmd: "open"},
				{title: "Clone", cmd: "clone"}
			],
		select: function(event, ui) {
			if (ui.cmd == 'open') {
				var $this;
//				if (ui.target.prop("tagName") == 'SPAN') {
//					$this = ui.target.parent();
//				} else {
//					$this = ui.target;
//				} -> // fix bug 8911 use the closest as $this object in jtree context menu (because if the click is on the bold / italic / underline it is not the right selector)
				$this = (ui.target.closest("a")); 
				var formId = $this.attr('formid');
				var formCode = $this.attr('struct');
				console.log("Go to ->",formCode+" ->"+formId);
				checkAndNavigate([''+formId+'',''+formCode+'','','false',true,'newTab']);
			} else if (ui.cmd == 'clone'){
				var $this;
//				if (ui.target.prop("tagName") == 'SPAN') {
//					$this = ui.target.parent();
//				} else {
//					$this = ui.target;
//				} -> // fix bug 8911 use the closest as $this object in jtree context menu (because if the click is on the bold / italic / underline it is not the right selector)
				$this = (ui.target.closest("a")); 
				var formId = $this.attr('formid');
				CloneExperimentByFormId(formId)
			}
		},
		beforeOpen: function(event, ui) {
			var $this;
//			if (ui.target.prop("tagName") == 'SPAN') {
//				$this = ui.target.parent();
//			} else {
//				$this = ui.target;
//			} -> // fix bug 8911 use the closest as $this object in jtree context menu (because if the click is on the bold / italic / underline it is not the right selector)
			$this = (ui.target.closest("a")); 
			var formId = $this.attr('formid');
			var formCode = $this.attr('struct');
			
			if (formCode == 'Experiment' && $('#formCode').val() == 'Main') {
				$('.ui-helper-hidden.ui-contextmenu.ui-menu:not([class*=Table-contextmenu]').children('li').eq(1).show();
			} else {
				$('.ui-helper-hidden.ui-contextmenu.ui-menu:not([class*=Table-contextmenu]').children('li').eq(1).hide();
			}
		}
	});
}

/*
 * filter by categories of projects call on change ddl Filter projects on the tree on Main form
 * @param domId - domId.id is 'tree'
 */
function onChangeProjectFilter(domId, name) {
	$('#tree').jstree("destroy");
	$('#tree_tree_lastValue').val("");
	$('#tree_firstTime').val("0");
	$('#tree_selected').val("");
	$('#formCode').val();
	// ** The last parameter is $('#formCode').val(). It is 'Main' for 11012021 
	var searchInput = $('#' + domId + '_input').val().trim();
	var criteria = $('#' + domId + '_ddlFilterProject').length > 0 ?$('#'+ domId + '_ddlFilterProject').val():"ALL";
	if (searchInput.trim().length >= 0 && searchInput.trim().length < 3) {
		$('#' + domId + '_input').val("");
		initTree(domId," onChangeAjax('tree'); ",'{"_tree_lastValue":"","_selected":"","_filter":"","_searchRow":""}',$('#formCode').val());
	} else {
		getSearchResult(searchInput, criteria, domId);
	}
}

function onClickFindButton(domId, name, value) {
		var searchInput = $('#' + domId + '_input').val().trim();
		//hideSearchTable(); 
		var criteria = $('#' + domId + '_ddlFilterProject').length > 0 ?$('#'+ domId + '_ddlFilterProject').val():"ALL";
		$('#tree').jstree("destroy");
		$('#tree_tree_lastValue').val("");
		$('#tree_firstTime').val("0");
		$('#tree_selected').val("");
		$('#formCode').val();
		if ( searchInput.length < 3) {
//			$('#generate').text("Search");
//			if (searchInput != '') {
				displayAlertDialog(getSpringMessage('VALIDATE_SEARCH_CHARACTERS'));
//			}
			$('#' + domId + '_input').val("");
			initTree(domId," onChangeAjax('tree'); ",'{"_tree_lastValue":"","_selected":"","_filter":"","_searchRow":""}',$('#formCode').val());
		} else {
			getSearchResult(searchInput, criteria, domId, value);
	    }
}

function onClickDeleteSearch(domId) {
	var searchInput = $('#' + domId + '_input').val().trim();
	if (searchInput.trim().length > 0) {
		$('#' + domId + '_input').val("");
		onChangeProjectFilter(domId);
	}
}

/*
 * This initTreeNew for cases when search row is not empty: 
 * 		**after click search button 
 * 		**load screen with saved search row
 * 		**change Filter projects ddl
 */
function initTreeNew(impCode, doOnChangeJSCall, value, formCode, searchResult_) {
    $('#' + impCode).on('loaded.jstree', function (e, data) {
        loadLastSaveTree(impCode, formCode, value);
        $("#tree").jstree("open_all");
    }).jstree({ 
    	'core' : {
            'themes': {
                'icons': false
            },
        	'data' : searchResult_
    	},
	    'plugins' : ['sort'],
	    'sort' : function(a, b) {
	    	a1 = this.get_node(a);
	        b1 = this.get_node(b);
	        return (a1.text > b1.text) ? 1 : -1;
	    }
    });
    
    $('#' + impCode).on('changed.jstree', function (e, data) {
        if (data.action != 'ready') {
            var str = data.node.parents.toString();
            if (str != '') {
                if (str.indexOf('#') != 0) {
                    str = str.split(',').reverse().toString();
                }
            }
            $('#' + impCode + '_tree_lastValue').val(str + ',' + data.node.id);
            var selectedFormid = $('[id="' + data.node.id + '"] a:first').attr('formid');
            var selectedStruct = $('[id="' + data.node.id + '"] a:first').attr('struct');
            if (typeof selectedFormid === 'undefined') {
                selectedFormid = '';
                selectedStruct = '';
            }
            var selected = selectedFormid + ',' + selectedStruct;
            $('#' + impCode + '_selected').val(selected);
            if ($('#' + impCode + '_firstTime').val() == '0') {
                // eval(doOnChangeJSCall);
            } else {
                $('#' + impCode + '_firstTime').val('0');
            }
        }
    });

    $('#' + impCode).on("dblclick.jstree", function (event) {
        var node, formId, formCode, page;
        //showWaitMessage(getSpringMessage('pleaseWait'));
        node = $(event.target).closest("li");
        formId = $('a', node).attr('formId');
        formCode = $('a', node).attr('struct');
        
        if(formCode != null && formCode == 'Run') {
        	var runNumber = node[0].innerText.split('Run ')[1];
        	formId = formId.substring(0,formId.indexOf('#'));
        	var cName = $('#userId').val()+':'+formId+':'+'$RUNNUMBER';
			setCookie(cName,runNumber,365);
            formCode = 'Experiment';
        }

        if (!formCode || formCode === 'Root') {
            //hideWaitMessage();
            return;
        }

        checkAndNavigate([''+formId+'',''+formCode+'','','false']);
    });
    
    $('#' + impCode).on('ready.jstree', function (e, data) { 
        console.log("ready.jstree");
        initTreeContextMenu();
    });
}

/*
 * This function is called from event which is called on search button click
 */
function getSearchResult(searchText, searchCriteria, domId, value) {
	
	showWaitMessage("Please wait...");
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
			+ $('#formCode').val() + "&userId=" + $('#userId').val() + "&stateKey=" + $('#stateKey').val();
	var allData = [
		{code : "searchText", val:searchText},
		{code : "searchCriteria", val:searchCriteria}
	];
	var data_ = JSON.stringify({
		action : "",
		data : allData,
		errorMsg : ""
	});
	
	$.ajax({
		type : 'POST',
		data : data_,
		url : "./getTreeSearchResult.request" + urlParam,
		contentType : 'application/json',
		dataType : 'json',
		async: false,
		success : function(obj) {
			hideWaitMessage();
			if (obj.errorMsg != null && obj.errorMsg != '') {
				displayAlertDialog(obj.errorMsg);
			}
			else {
				var jsonResult = obj.data[0].val;
				var data = JSON.parse(jsonResult);
				
				data = addIconsToLevelsOfTree(data, searchText); // add icons
				
				initTreeNew(domId ," onChangeAjax('tree'); ",value,'filter', data); //$('#formCode').val());
				//alert(data.length);
			}
		},
		error : handleAjaxError
	});
}

/*
 * add icons to the levels of the tree and for mark bold found search strings
 */
function addIconsToLevelsOfTree(data, searchText) {
	try {
		if ((data.length==1 || searchText != '') && data[0].a_attr.struct == 'Root'){
			data[0].a_attr.style = "width:238px;";
		}
		for (i=0; i < data.length; i++){
			if (data[i].id != '-1' && data[i].id != undefined) {
			    
				if (~data[i].text.toLowerCase().indexOf(searchText.toLowerCase()) && searchText != '' && searchText != undefined){
					data[i].text = getEntityIconByFormCode(data[i].a_attr.struct) + ' ' + replaceAllNew(data[i].text, searchText);
				} else {
					data[i].text = getEntityIconByFormCode(data[i].a_attr.struct) + ' ' + data[i].text;
				}
			}
		}
	} catch (e){} 
	return data;
}

/*
 * recursion function for case-save replacement 
 */
function replaceAllNew(data_, searchText) {
	var ind = data_.toLowerCase().indexOf(searchText.toLowerCase());
	var searchTextCaseSens = data_.substring(ind,ind+searchText.length);
	if  (~data_.substring(ind+searchText.length).toLowerCase().indexOf(searchText.toLowerCase())) {
		return data_.substring(0,ind+searchText.length).replace(searchTextCaseSens,"<b>" + searchTextCaseSens + "</b>") + replaceAllNew(data_.substring(ind+searchText.length), searchText);
	} else {
		return data_.replace(searchTextCaseSens,"<b>" + searchTextCaseSens + "</b>");
	}
}