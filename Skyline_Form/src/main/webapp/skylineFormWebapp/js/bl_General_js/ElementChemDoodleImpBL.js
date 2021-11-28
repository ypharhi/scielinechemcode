/**
 * chemDoodleReactionTabEvent
 * 
 * @returns
 */
function chemDoodleReactionTabEvent() {

 MarvinJSUtil.getEditor("#marvin_js").then(
  function(sketcherReactionInstance) {
   var source = sketcherReactionInstance.exportStructure("mrv").then(function(source)
		   {
	   // ajax call to the api service
	   $.ajax({
	    type: 'POST',
	    data: '{"action" : "chemDoodleReactionTabEvent","' +
	     'data":[{"code":"reactionMrv","val":' +
	     JSON.stringify(source) + '},{"code":"formId","val":"' +
	     $('#formId').val() + '"}],' + '"errorMsg":""}',
	    url: "./chemDoodleReactionTabEvent.request",
	    contentType: 'application/json',
	    dataType: 'json',
	    success: function(obj) {
	     hideWaitMessage();
	     if (obj.errorMsg != null && obj.errorMsg != '') {
	      displayAlertDialog(obj.errorMsg);
	     } else {
	    	 var resultObj = JSON.parse(obj.data[0].val);
	    	 if (resultObj.result == "-1") {
	   	      displayAlertDialog("Error");
	   	     } else if (resultObj.result == "noMaterialFound") {
	   	      openConfirmDialog({
	   	       onConfirm: doSaveWithConfirm,
	   	       onConfirmParams: 'urlredirect:InvItemMaterial?cml_copy_link=cml_copy_link' + resultObj.cml_copy_link,
	   	       title: 'Requested Material Was Not Found',
	   	       // message: 'Do You Want To Create a Material?'
	   	       message: getSpringMessage('createMaterialMessage')
	   	      });
	   	     } else if (resultObj.result == "noUniqueValue") {
	   	      displayAlertDialog(getSpringMessage('alertDialogMaterialMessage'));
	   	     } else {
	   	    	 
	   	    	openConfirmDialog({
        	        onConfirm: function(){	
        	        	deleteAndInsertMaterial(resultObj.listMaterialToDelete,resultObj.insertMaterial);
        	        	
        	        },
        	        
        	        title: 'Warning',
        	        message:resultObj.insert + " materials were added.<br>"+ resultObj.removed +" materials  were deleted.",
        	        onCancel: function(){
        	        }
        	    }); 
	   	     }
	     }
	    },
	    error: handleAjaxError
	   });
		   });
  },
  function(error) {
   alert("Cannot retrieve sketcher instance from iframe");
  });
}

function chemDoodleReactionTabUp() {

	MarvinJSUtil.getEditor("#marvin_js").then(
			  function(sketcherReactionInstance) {
			   var source = sketcherReactionInstance.exportStructure("mrv").then(function(source)
					   {
				   // ajax call to the api service
				   $.ajax({
				    type: 'POST',
				    data: '{"action" : "chemDoodleReactionTabUp","' +
				     'data":[{"code":"reactionMrv","val":' +
				     JSON.stringify(source) + '},{"code":"formId","val":"' +
				     $('#formId').val() + '"}],' + '"errorMsg":""}',
				    url: "./chemDoodleReactionTabUp.request",
		    contentType: 'application/json',
		    dataType: 'json',
		    success: function(obj) {
		     hideWaitMessage();
		     var cml = obj.data[0].val;
		     marvinSketcherInstance.importStructure("mrv", cml).catch(function(error) {
				   alert(error);
				  })
		    },
		    error: handleAjaxError
				   });
					   });
			  },
			  function(error) {
			   alert("Cannot retrieve sketcher instance from iframe");
			  });
}

function deleteAndInsertMaterial(listMaterialToDelete,insertMaterial)
{
	var formcode = "MaterialRef";
	var urlParam = "?formId=" + $('#formId').val() + "&formCode="
					+ formcode + '&userId=' + $('#userId').val()
					+ "&eventAction=deleteAndInsertMaterial&isNew=" + $('#isNew').val();
	var stringifyToPush = {
			code : 'listMaterialToDelete',
			val : listMaterialToDelete,
			type : "AJAX_BEAN",
			info : 'na'
		};
	var stringifyToPush1 = {
			code : 'insertMaterial',
			val : insertMaterial,
			type : "AJAX_BEAN",
			info : 'na'
		};
	var allData = getformDataNoCallBack(1);
	var allData = allData.concat(stringifyToPush);
	var allData = allData.concat(stringifyToPush1);

	var data_ = JSON.stringify({ 
		action : "doSave",
		data : allData,
		errorMsg : ""
	});
	
	// call...
	$.ajax({
		type : 'POST',
		// async: false,
		data : data_,
		url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
		contentType : 'application/json',
		dataType : 'json',
		success : function(obj) {
			onElementDataTableApiChange('reactants');
			onElementDataTableApiChange('products');
			hideWaitMessage();
		},
		error: handleAjaxError
	});
}

function chemDoodleBL_chemDoodleCanvasUpdate(structID) 
{

	MarvinJSUtil.getEditor("#marvin_js").then(
			  function(sketcherReactionInstance) {
				// ajax call to the api service
				   $.ajax({
					   		type: 'POST',
					   		data: '{"action" : "chemDoodleCanvasUpdateData","' +
					   				'data":[{"code":"structureID","val":' + structID + '}],'
				   						+ '"errorMsg":""}',
					   		url: "./chemDoodleCanvasUpdateData.request",
					   		contentType: 'application/json',
					   		dataType: 'json',
					   		success: function(obj) {
								    hideWaitMessage();
								    var structure = obj.data[0].val;								    
									if (structure === undefined || structure == "") 
									{
									   structure = "<cml><MDocument></MDocument></cml>";
									}
									console.log("structure", structure);
								     marvinSketcherInstance.importStructure("mrv", structure).catch(function(error) {
										   alert(error);
										  });
					   		},
					   		error: handleAjaxError
				   		});
			  },
			  function(error) {
			   alert("Cannot retrieve sketcher instance from iframe");
			  });
}

function getChemMaterialIdList() {

	MarvinJSUtil.getEditor("#marvin_js").then(
			  function(sketcherReactionInstance) {
			   var source = sketcherReactionInstance.exportStructure("mrv").then(function(source)
					   {
			 		var stringifyToPush = {
			 				code : "mrv",
			 				val : source,
			 				type : "AJAX_BEAN",
			 				info : 'na'
			 			};

			 		// get all data and add removeNameId
			 		var allData = getformDataNoCallBack(1);
			 		var allData = allData.concat(stringifyToPush);
			 		
			 	   // url call
			 		var urlParam = "?formId="+ $('#formId').val()
 					+ "&formCode="+ $('#formCode').val()
 					+ "&userId="+ $('#userId').val()
 					+ "&eventAction=getChemMaterialIdList"
 					+ "&isNew=" + $('#isNew').val();
			 		
			 		var data_ = JSON.stringify({
						action : "getChemMaterialIdList",
						data : allData,
						errorMsg : ""
					});
				   // ajax call to the api service
				   $.ajax({
			 			type : 'POST',
			 			data : data_,
			 			url : "./generalEvent.request" + urlParam + "&stateKey=" + $('#stateKey').val(),
			 			contentType : 'application/json',
			 			dataType : 'json',

			 			success : function(obj) {
			 				hideSearchTable();
			 				
			 			},
			 			error : handleAjaxError});
					   });
			  },
			  function(error) {
			   alert("Cannot retrieve sketcher instance from iframe");
			  });
}

	
