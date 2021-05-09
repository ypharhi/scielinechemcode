var marvinSearchSketcherInstance = {};
var marvinSearchResultArray_ = [];
var marvinSearchCallback = {};

var marvinSearchValue = {};

var ElementChemDoodleSearchImp = {
 value_: function(val_) {
  // defined as callback - no need for returning values
  return null;
 },
 setvalue_: function(val_) {
  // important: should add attribute 'lastvalue' to the element imp in the
  // poll (like in ElementAutoCompleteIdValDDLImp)
 },
 setDefaultValueForUnitTest_: function (val_) {
 }

};
 
function getMarvinSearch(elementObj_, resultArray_, callback) {
	
	marvinSearchResultArray_ = resultArray_.slice();
	 marvinSearchCallback = callback;
	 marvinSearchValue = elementObj_;
	 
// if ($(elementObj_).attr('type_') == 'MOL') {
//  return "";
// } else
  MarvinJSUtil.getEditor("#marvin_js").then(
   function(sketcherInstance) {
	   
	marvinSearchSketcherInstance = sketcherInstance;
		
	if(sketcherInstance.isEmpty()) {
		fireNextCallback(marvinSearchCallback, marvinSearchResultArray_);
	} else {

		sketcherInstance.exportStructure("mrv").then(
				pushMarvinSearchData,
	     function(error) {
	      alert(error);
	     });
	}

    
   },
   function(error) {
    alert("Cannot retrieve sketcher instance from iframe:" + error);
   });
}


var pushMarvinSearchData = function(source) { //source smiles or Inchi

	 var domId = $(marvinSearchValue).attr('id');
	 var elementID = $(marvinSearchValue).attr('elementID');
	 var formCode = $('#formCode').val();
	 
	 stringifyToPush = {
			  code: $(marvinSearchValue).attr('id'),
			  val: source,
			  type: "AJAX_BEAN",
			  info: "NA"
			 };

	 marvinSearchResultArray_.push(stringifyToPush);
	 //MUST CALL ->
	 fireNextCallback(marvinSearchCallback, marvinSearchResultArray_);
	};