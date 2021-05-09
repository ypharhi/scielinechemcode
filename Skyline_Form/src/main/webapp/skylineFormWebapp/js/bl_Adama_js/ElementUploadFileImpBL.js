/**
 * 
 */

function ValidateSpreadsheetEmty(args){
	if(!isSpreadsheetEmpty("spreadsheet"))//check if the spreadsheet has data
	{
		openConfirmDialog({
			onConfirm : function (){
				args.callback(args);
			},
			title : 'Warning',
			message : getSpringMessage('Would you like to clear the spreadsheet below?'),
			onCancel: function (){
				(args.event).preventDefault();
				return false;
			}
		});
	} else{
		args.callback(args);
	}
}

function loadfileIntoSpreadsheet(){
	var name = $('[name="uploadFile"]').val();
	if(name == ""){
		name = $('#savedFile_document').val();
	}
	
	//var res = name.split(".");//&& res[1].toLowerCase() =="ssjson"
	var pos = name.lastIndexOf('.');
    var fileName = name.substr(0, pos);
    var fileExtension = pos === -1 ? "" : name.substring(pos + 1).toLowerCase();
	if(fileExtension !="" && fileExtension.toLowerCase() =="xlsx"){//Only SpreadJS file (.xlsx) is supported to be used in the Spreadsheet area
		var $fileElement = $('#document').parent('.fileUploadElementForm').find('input[type="file"]')[0];
		if($fileElement.files.length>0){
			importExcel('spreadsheet',$fileElement);
		}else{
			var id = $('#document').attr('id');
			var form = document.getElementById('fileUploadElementForm_'+id);    		      	
		    var formData = new FormData(form);
		    formData.append("elementId", $('#document').attr('elementid'));
	      	$.ajax({
	            url : 'getFileContent.request',
	            data : formData,
	            processData : false,
	            contentType : false,
	            async:false,
	            type : 'POST',
	            dataType: 'text',
	            success : function(data) {
	            	var spreadSheetData = data;
	    			if (spreadSheetData!=null && spreadSheetData != "") {
	    				setValueToSpreadSheet('spreadsheet',spreadSheetData);
	    			}
	            },
	            error : function(err) {
	                console.log(err);
	            }
	        });
		}
		/*var files = $('#document').parent('.fileUploadElementForm').find('input[type="file"]')[0].files;
        var file = files && files[0];
         //var type = getExtension(file.name);
		 var type = "xlsx";
         var reader = new FileReader();
         reader.onload = function () {
             if (type === "xlsx") {
                 importExcel('spreadsheet',this.result, file);
             } else {
                 target.data = this.result;
                 callback(target);
             }
         };
         switch (type) {
             case "dataurl":
                 reader.readAsDataURL(file);
                 break;
             case "xlsx":
                 reader.readAsArrayBuffer(file);
                 break;
             default:
                 reader.readAsText(file);
                 break;
         }*/
	/*var id = $('#document').attr('id');
	var form = document.getElementById('fileUploadElementForm_'+id);    		      	
    var formData = new FormData(form);
    formData.append("elementId", $('#document').attr('elementid'));
      	$.ajax({
            url : 'getFileContent.request',
            data : formData,
            processData : false,
            contentType : false,
            async:false,
            type : 'POST',
            dataType: 'text',
            success : function(data) {
            	var spreadSheetData = data;
    			if (spreadSheetData!=null && spreadSheetData != "") {
    				setValueToSpreadSheet('spreadsheet',spreadSheetData);
    			}
            },
            error : function(err) {
                console.log(err);
            }
        });*/
	}else{
		displayAlertDialog(getSpringMessage('ONLY_SPREADJS_SUPPORTED'),null,{button:"OK"});
	}
}