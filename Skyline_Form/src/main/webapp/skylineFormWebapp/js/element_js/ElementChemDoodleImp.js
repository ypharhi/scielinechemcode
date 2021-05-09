var marvinSketcherInstance = {};
var marvinValue = {};
var marvinResultArray_ = [];
var marvinCallback = {};

//var pln_marvinSketcherInstance = {};
//var pln_marvinValue = {};
//var pln_marvinResultArray_ = [];
//var pln_marvinCallback = {};



/*
 *  Element use chemDoodle web css and js files.
 *  On save getFullData() function gather from the canvas next data to save in DB:
 *  	- jsonObject: to save full state of the canvas;
 *  	- MOL data: for each molecule;
 *  	- SMILES: for each molecule;
 *      - attributes for each molecule: numbers of atoms, numbers of bonds, molecular formula, molecular mass;
 *      - molecule type: reactant - 'R', product - 'P', single - 'S', agent - 'A';
 */
var ElementChemDoodleImp = {
 value_: function(val_) {
  // defined as callback - no need for returning values
  return null;
 },
 setvalue_: function(val_) {
  //important: should add attribute 'lastvalue' to the element imp in the poll (like in ElementAutoCompleteIdValDDLImp)
 },
 setDefaultValueForUnitTest_: function (val_) {
}
};

function getMarvinData(elementObj_, resultArray_, callback) {
 marvinResultArray_ = resultArray_.slice();
 marvinCallback = callback;
 marvinValue = elementObj_;

 MarvinJSUtil.getEditor("#marvin_js").then(
 	function(sketcherInstance) {
	  marvinSketcherInstance = sketcherInstance;

	  marvinSketcherInstance.exportStructure("mrv").then(
	  	pushMarvinData,
	   function(error) {
	    alert("Molecule export failed: " + error);
	   });
 	},
 	function(error) {
  		alert("Cannot retrieve sketcher instance from iframe");
 	});
}

//function getMarvinData_pln(elementObj_, resultArray_, callback) {
//	 pln_marvinResultArray_ = resultArray_.slice();
//	 pln_marvinCallback = callback;
//	 pln_marvinValue = elementObj_;
//
//	 MarvinJSUtil.getEditor("#marvin_js_pln").then(
//	 	function(sketcherInstance) {
//	 		pln_marvinSketcherInstance = sketcherInstance;
//
//	 		pln_marvinSketcherInstance.exportStructure("mrv").then(
//	 				pushMarvinData_pln,
//		   function(error) {
//		    alert("Molecule export failed: " + error);
//		   });
//	 	},
//	 	function(error) {
//	  		alert("Cannot retrieve sketcher instance from iframe");
//	 	});
//	}

/*
element = $(this).attr('element');
//if(element != 'ElementAutoCompleteDDLImp') { //if(element == 'ElementAutoCompleteDDLImp')  its only for CHECK callback it should be remove from here
	stringifyInfo = '{"formPreventSave":"' + $(this).attr("formPreventSave") +
	'", "type":"' + $(this).attr("type") +
	'", "saveType":"' + $(this).attr("saveType") +
	'"}';
	stringifyToPush = {
			code: $(this).attr('id'),
			val: getValue_(element, this, changeType),
			type: "AJAX_BEAN",
			info: stringifyInfo
	};
	values.push(stringifyToPush);
//}
});
*/
var pushMarvinData = function(source) {

 var stringifyInfo, stringifyToPush, isChangedflag;
 
 if($("#marvin_js").attr("is_changed_flag") == "1") {
	 isChangedflag = 1;
 } else {
	 isChangedflag = 0;
 }

 stringifyInfo = '{"formPreventSave":"' +
  $(marvinValue).attr("formPreventSave") + '", "type":"' +
  $(marvinValue).attr("type") + '", "saveType":"' +
  $(marvinValue).attr("saveType") + '", "isChangedflag":"' + isChangedflag +'"}';

 var domId = $(marvinValue).attr('id');
 var elementID = $(marvinValue).attr('elementID');
 var formCode = $('#formCode').val();

 value_source = {
  formCodeFull: formCode + "." + domId,
  elementId: elementID,
  fullArray: source
 };

 stringifyToPush = {
  code: $(marvinValue).attr('id'),
  val: JSON.stringify(value_source),
  type: "AJAX_BEAN",
  info: stringifyInfo
 };

 //return value;
 marvinResultArray_.push(stringifyToPush);
 //MUST CALL ->
 fireNextCallback(marvinCallback, marvinResultArray_);
};

//var pushMarvinData_pln = function(source) {
//
//	 var stringifyInfo, stringifyToPush;
//
//	 stringifyInfo = '{"formPreventSave":"' +
//	  $(pln_marvinValue).attr("formPreventSave") + '", "type":"' +
//	  $(pln_marvinValue).attr("type") + '", "saveType":"' +
//	  $(pln_marvinValue).attr("saveType") + '"}';
//
//	 var domId = $(pln_marvinValue).attr('id');
//	 var elementID = $(pln_marvinValue).attr('elementID');
//	 var formCode = $('#formCode').val();
//
//	 value_source = {
//	  formCodeFull: formCode + "." + domId,
//	  elementId: elementID,
//	  fullArray: source
//	 };
//
//	 stringifyToPush = {
//	  code: $(pln_marvinValue).attr('id'),
//	  val: JSON.stringify(value_source),
//	  type: "AJAX_BEAN",
//	  info: stringifyInfo
//	 };
//
//	 //return value;
//	 pln_marvinResultArray_.push(stringifyToPush);
//	 //MUST CALL ->
//	 fireNextCallback(pln_marvinCallback, pln_marvinResultArray_);
//	};

/**
 * return from onAjaxCall()
 * 
 * @param obj
 * @returns
 */
function upDateElementChemDoodle(obj) {
 if (typeof obj.isHidden !== 'undefined') {
  if (obj.isHidden.toLowerCase() == "false") {
   $('[id="' + obj.domId + '"]').css('visibility', 'visible');
   $('[id="' + obj.domId + '"]').css('display', 'block');
  } else {
   $('[id="' + obj.domId + '"]').css('display', 'none');
  }
 }
 if ((typeof obj.isDisabled !== 'undefined')) {
  if (obj.isDisabled.toLowerCase() == "false") {
   $('[id="' + obj.domId + '"]').removeClass('disabledChemDoodle');
  } else {
   $('[id="' + obj.domId + '"]').addClass('disabledChemDoodle');
   $('[id="' + obj.domId + '"]').css('border-color', '');
   $('[id="' + obj.domId + '"]').css('outline', '');
  }
 }
}

function getChemDoodleStringContent(domId, structure) {
	alert('todo');
    /*showWaitMessage(getSpringMessage('pleaseWait'));
    if ((structure == '') || (structure == '-1')) {
        hideWaitMessage();
        return;
    }
    sketcherObjectChemDoodle[domId].clear();
    $.ajax({
        type: 'POST',
        data: '{"action" : "getFileStringContent","' + 'data":[{"code":"value","val":"' + structure + '"}],' + '"errorMsg":""}',
        url: "./getFileStringContent.request",
        contentType: 'application/json',
        dataType: 'json',
        success: function (obj) {
            if (obj.errorMsg != null && obj.errorMsg != '') {
                displayAlertDialog(obj.errorMsg);
            } else if (obj.data[0].val == "-1") {
                displayAlertDialog("Error");
            } else {
                reconstructedChemDoodle[domId] = new ChemDoodle.io.JSONInterpreter().contentFrom(JSON.parse(obj.data[0].val)); // obj.data[0].val is jsonObj
                sketcherObjectChemDoodle[domId].loadContent(reconstructedChemDoodle[domId].molecules, reconstructedChemDoodle[domId].shapes);
                sketcherObjectChemDoodle[domId].repaint();
            }
            hideWaitMessage();
        },
        error: handleAjaxError
    });*/
	
//	 MarvinJSUtil.getEditor("#marvin_js").then(function(sketcherInstance) {
//		  marvinSketcherInstance = sketcherInstance;
//
//		  var structure = $("#marvin_js").attr("canvas");
//		  if (structure === undefined) {
//		   structure = "<cml><MDocument><MChemicalStruct><molecule molID=&quot;m1&quot;><atomArray><atom id=&quot;a1&quot; elementType=&quot;C&quot; x2=&quot;-2.6663208781719643&quot; y2=&quot;1.6866666666666665&quot;/><atom id=&quot;a2&quot; elementType=&quot;C&quot; x2=&quot;-4&quot; y2=&quot;0.9166666666666666&quot;/></atomArray><bondArray><bond atomRefs2=&quot;a2 a1&quot; order=&quot;1&quot; id=&quot;b1&quot;/></bondArray></molecule></MChemicalStruct></MDocument></cml>";
//		  }
//		  marvinSketcherInstance.importStructure("mrv", structure).catch(function(error) {
//		   alert(error);
//		  });
//		 }, function(error) {
//		  alert("Cannot retrieve sketcher instance from iframe:" + error);
//		 });
}

var handleMolChangeEvent = function () {
	$("#marvin_js").attr("is_changed_flag","1");
};

$(document).ready(function handleDocumentReady(e) {
	if (document.getElementById("marvin_js") != null) {
		MarvinJSUtil.getEditor("#marvin_js").then(function(sketcherInstance) {
			  marvinSketcherInstance = sketcherInstance;

			  var structure = $("#marvin_js").attr("canvas");
			  if (structure === undefined) {
			   structure = "<cml><MDocument></MDocument></cml>";
			  }
			  marvinSketcherInstance.importStructure("mrv", structure).catch(function(error) {
			   alert(error);
			  });
			  
			  marvinSketcherInstance.on("molchange", handleMolChangeEvent);
			  
			  //ab 10042019: fixed bug -> prevent from scroll inside canvas	
			  var iframe = document.getElementById('marvin_js');
			  var innerDoc = iframe.contentDocument || iframe.contentWindow.document;
			  var $canvas = $(innerDoc).find('canvas');
			  // add event listener to canvas on scroll
			  $canvas[0].addEventListener('wheel',function(event){
			        // delegate event to parent
			    	$(window).trigger("scroll");
			  }, false);			  
			  
			 }, function(error) {
			  alert("Cannot retrieve sketcher instance from iframe:" + error);
			 });
	} 
	
//	if (document.getElementById("marvin_js_pln") != null) {
//		MarvinJSUtil.getEditor("#marvin_js_pln").then(function(sketcherInstance) {
//			  pln_marvinSketcherInstance = sketcherInstance;
//
//			  var structure = $("#marvin_js_pln").attr("canvas");
//			  if (structure === undefined) {
//			   structure = "<cml><MDocument></MDocument></cml>";
//			  }
//			  pln_marvinSketcherInstance.importStructure("mrv", structure).catch(function(error) {
//			   alert(error);
//			  });
//			 }, function(error) {
//			  alert("Cannot retrieve sketcher instance from iframe:" + error);
//			 });
//	} 
});