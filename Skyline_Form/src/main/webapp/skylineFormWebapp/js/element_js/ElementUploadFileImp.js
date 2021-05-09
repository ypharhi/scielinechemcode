var ElementUploadFileImp = 
{		
		value_: function (val_, changeType) 
		{
			var id = $(val_).attr('id');
			var elementID = $(val_).attr('elementID');
						
			//alert(changeType);
			if(changeType == 2 && $('#uploadFile_'+id).val() != '')
			{
				if(!validateUploadFile(id)){//if the size of the file exceeds the configured maximum size,then don't execute the following ajax call
					return elementID;
				}
	        
		      	var form = document.getElementById('fileUploadElementForm_'+id);    		      	
		      	var formData = new FormData(form);
		      	
		      	$.ajax({
		            url : 'saveFile.request',
		            data : formData,
		            processData : false,
		            contentType : false,
		            async:false,
		            type : 'POST',
		            dataType: 'text',
		            success : function(data) {
		                //alert("success:" + data);
		                elementID = data;
		                $('#'+id).attr('elementID',elementID);
		            },
		            error : function(err) {
		                console.log(err);
		            }
		        });
			}
	        return elementID;
	    },
	    setvalue_: function (val_) {
	        //important: should add attribute 'lastvalue' to the element imp in the poll (like in ElementAutoCompleteIdValDDLImp)
	    },
	    setDefaultValueForUnitTest_: function (val_) {
	    }
};

/**
 * return from onAjaxCall()
 * @param obj
 * @returns
 */
function upDateElementUpload(obj) {
    if (typeof obj.isHidden !== 'undefined') {
        if (obj.isHidden.toLowerCase() == "false") {
            $('[id="' + obj.domId + '"]').parent().css('display', '');
            $('[id="' + obj.domId + 'dragAndDropHandler"]').css('display', '');
            $('*').filter(function(){
            	var regex = new RegExp('^'+obj.domId+"_\\d+\$");
            	return this.id.match(regex);
            }).each(function(){
            	$(this).parent().css('display', '');
            });
        } else {
            $('[id="' + obj.domId + '"]').parent().css('display', 'none');
            $('[id="' + obj.domId + 'dragAndDropHandler"]').css('display', 'none');
            $('*').filter(function(){
            	var regex = new RegExp('\^'+obj.domId+"_\\d+\$");
            	return this.id.match(regex);
            }).each(function(){
            	$(this).parent().css('display', 'none');
            });
        }
    }
    if (typeof obj.isMandatory !== 'undefined') {
        if (obj.isMandatory.toLowerCase() == "false") {
            $('[id="' + obj.domId + '"]').attr('required',false);
        } else {
            $('[id="' + obj.domId + '"]').attr('required',true);
        }
    }
}

function validateUploadFile(domId)
{
	var bool = true;
	var mbSize = 10;
	var maxFileSize = getSysProp('file.maxUploadSize','10485760');
	var maxFileSizeMB = maxFileSize/1024/1024; //mbSize=10 => 10mb
	
	if(domId!==undefined){
		if(domId instanceof File){
			if(domId.size > maxFileSize){
				displayAlertDialog("File " + domId.name + " size must be under " + mbSize + " MB");
				bool = false;
			}
			return bool;
		}
		var oFile = $('#'+domId).parent('.fileUploadElementForm').find('input[type="file"]')[0].files[0];
		if (oFile != undefined && oFile.size > maxFileSize)
	    {
	        bool = false;
	        return false;
	    }
	} else {
		$('.fileUploadElementForm').each(function()
		{
			var _form = this;
			var _inputFile = $(_form).find('input[type="file"]');
			var _uploadFileName = $(_form).find('input[name="uploadFile"]');
			
			var oFile = _inputFile[0].files[0];
			if (oFile != undefined && oFile.size > maxFileSize)
		    {
				displayAlertDialog("File " + oFile.name + " size must be under " + mbSize + " MB");
				var input = $(_inputFile);
				input.wrap('<form></form>').parent().trigger('reset').children().unwrap('<form></form>'); // reset file: replace input field with one's cloned but empty
				$(_uploadFileName).val(''); // reset 'uploadFile' hidden field also, because is used in save process
				$(_uploadFileName).css('outline', '1px solid #a94442').css('border','1px solid #a94442');
				bool = false;
		        
		    } else {
		    	$(_uploadFileName).css('outline', '').css('border','');
		    }
		});
		return bool;
	}
	return bool;
}

function buildScriptFile(domId,disableUploadBtn,elementTypeName,inputAttribute,executeFunctionsJson){
			document.getElementById("uploadBtnMain_"+domId).disabled = disableUploadBtn;
			$('[id*="dragAndDropHandler"]').parent('td').css('padding-top',"10px").css('padding-bottom',"10px");
			
	 		document.getElementById("uploadBtnMain_"+domId).onchange = function (e) {
	//"    	 	document.getElementById(\"uploadFile_"+domId+"\").value = this.value;\n" +
	 			//if tried to select another list of files, then removes all the dynamic forms
//	 			if($(this).attr('multiple')!=undefined && $(this).attr('multiple')!=false){
//		 			$('[id*="fileUploadElementForm_' + domId + '"]').each(function(){
//		 				if($(this).attr("id") != ("fileUploadElementForm_" + domId)){//it is the dynamic ones
//		 					$(this).remove();
//		 				}
//		 			});
//	 			}
	 			
	 			
	 			/*if($(this).get(0).files.length==0){//if no file was chosen then empties the list
	 				document.getElementById("uploadFile_"+domId).value = this.value;
	 			}*/
	 			if(typeof executeFunctionsJson.executeFunctionBeforeChange==='function'){
	 				executeFunctionsJson.executeFunctionBeforeChange({event:e,elem:this,callback:uploadBtnMainOnchange});
	 			}else{
	 				uploadBtnMainOnchange({event:e,elem:this});
	 			}
	 		};
	 			
 			function uploadBtnMainOnchange(args){
	 			var $this = $(args.elem);
 				if(Number($('#uploadBtnMain_count').val())<2){
	 				var lastFileIframe = "fileUploadElementForm_"+domId ;
	 			} else {
	 				var lastFileIframe = "fileUploadElementForm_"+(domId +"_"+(Number($('#uploadBtnMain_count').val())-1)) ;
	 			}
				var currentCountFiles = $('#uploadBtnMain_count').val();
				var parentElement = $('[id*="dragAndDropHandler"]').attr("data-parentelemet");
				var tableType = $('#' + parentElement + '_tableType').val();
				if(parentElement!=undefined &&parentElement!="" && tableType!=undefined && tableType !="") {	    		 			
					handleFileUpload($this.get(0).files,$this.parent());									
				}
				else{
				//loop through all the files attached
	    		for (var i = currentCountFiles,j=0; i < Number(currentCountFiles) + $this.get(0).files.length; ++i,j++) {
	    			if(i==0){//it's the 1st file -> then puts its value in the current form
	    				document.getElementById("uploadFile_"+domId).value = $this.get(0).files[i].name;//this.value;
	    				if($('#uploadFile_'+domId).val()!=''){
	    					const dt = new DataTransfer();
	    					dt.items.add($this.get(0).files[i]);
	    					$("#uploadBtn_"+domId)[0].files = dt.files;
	    					$('#uploadFile_'+domId).css('display','block');
	    					$('#uploadFile_'+domId).parent('div').css('display','block');
	    				}
	    				
	    			} else {//adding form for each uploaded file dynamically
	    				
	    				
	    				if($('[name="uploadBtnMain"]').attr('multiple')==undefined || $('[name="uploadBtnMain"]').attr('multiple')== false){
	    					//if multiple is not allowed -> then replace the current file in the 1st form
	    					document.getElementById("uploadFile_"+domId).value = $this.get(0).files[j].name;
	    					$("#uploadBtn_"+domId)[0].files = $this.get(0).files;//$("#uploadBtn_"+domId)[0].files[0].files
	    				} 
	    				
	    				else {
			    			var innerHTML = '<form class="fileUploadElementForm" id="fileUploadElementForm_' + (domId +"_"+i) +'" ' +
			    					' 				method="post" action="saveFile.request"  enctype="multipart/form-data" style="width:100%;">\n' + 
			    					'	 <input id="' + (domId +"_"+i) + '" type="hidden" name="fileFormId" '
			    											+ inputAttribute + 'required element="' + elementTypeName + '">\n' +
			    					'	 <input type="hidden" id="formCodeFull_' + (domId +"_"+i) + '" name="formCodeFull" value="' + $('#formCode').val()+"."+(domId +"_"+i) + '">\n' + 
			    					'	<div> \n' + 
			    					'	 	<div class="uploadFileUnit fileUpload fileBtn fileBtn-primary" style = "display:block"  >\n' +
			    					'			<label onclick="removeFile(\''+domId +"_"+i+'\')" class="uploadItem"><i class="fa fa-times" title="Remove"></i></label>'+
			    					'			<label for="uploadBtn_'+(domId +"_"+i)+'" class="uploadItem">'+
			    				    '				<i class="fa fa-cloud-upload" title="Change"></i>'+
			    				    '			</label>'+
			    					'			<input  type="file" class="upload" id="uploadBtn_'+(domId +"_"+i)+'" name="uploadBtn"  style="display:none" />\n' +
			    					'	 		<input type="text" style = "display:block;" disabled="disabled" placeholder="Choose File"  id="uploadFile_'+(domId +"_"+i)+'" class="uploadItem" name="uploadFile" value="'+$this.get(0).files[j].name+'"/>\n' +
			    					'	 	</div>	\n' + 						
			    					'	</form>\n'	+
			    					//attachment +
			    					//iframe
			    					'<script>\n' +
			    					'		document.getElementById("uploadBtn_'+(domId +"_"+i)+'").onchange = function (e) {'+
			    						(typeof executeFunctionsJson.executeFunctionBeforeChange==='function'?
			    								executeFunctionsJson.executeFunctionBeforeChange+'({event:e,elem:this,callback:\n'+
			    			 				'   function(args){\n'+
			    			 				'           document.getElementById("uploadFile_'+(domId +"_"+i)+'").value = (args.elem).value;\n'+
			    			 				+ (typeof executeFunctionsJson.executeFunctionAfterChange==='function'?executeFunctionsJson.executeFunctionAfterChange+'('+domId+');':"")+'}});':
			    					'			document.getElementById("uploadFile_'+(domId +"_"+i)+'").value = this.value;\n'+
			    					+ (typeof executeFunctionsJson.executeFunctionAfterChange ==='function'?executeFunctionsJson.executeFunctionAfterChange+"("+domId+");":""))+
			    					'       }'+
			    					"<" + "/" + "script>";
			    					$('[id="' + lastFileIframe +'"]').after(innerHTML);
			    					/*var dt = [$(this).get(0).files[j]];
			    					dt._proto_ = Object.create(FileList.prototype);
			    					 Object.defineProperty($("#uploadBtn_"+(domId +"_"+i))[0], 'files', {
			    						    value: dt
			    						  });*/
			    					const dt = new DataTransfer();
			    					 dt.items.add($this.get(0).files[j]);
			    					$("#uploadBtn_"+(domId +"_"+i))[0].files=dt.files;
			    					lastFileIframe="fileUploadElementForm_"+(domId +"_"+i);
	    				}
	    			}
	    			$('#uploadBtnMain_count').val(Number($('#uploadBtnMain_count').val())+1);
	    		}
				}
				if(typeof executeFunctionsJson.executeFunctionAfterChange === 'function'){
					executeFunctionsJson.executeFunctionAfterChange(domId);
				}
	      	}
	      	
	      	document.getElementById("uploadBtnRemove_"+domId).onchange = function(e){
	      		if(typeof executeFunctionsJson.executeFunctionBeforeChange==='function'){
	      			executeFunctionsJson.executeFunctionBeforeChange({event:e,elem:this,callback:uploadBtnRemoveOnchange});
	 			}else{
	 				uploadBtnRemoveOnchange({event:e,elem:this});
	 			}
	      	};
	      	
	      	function uploadBtnRemoveOnchange(args){
	      		var $this = $(args.elem);
		      		if($("#removeFile_"+domId).css('display')=='block'){
		      			if($this.get(0).files.length!=0){
			      			document.getElementById("uploadFileRemove_"+domId).value = $this.get(0).files[0].name;
			      			document.getElementById("uploadFile_"+domId).value = $this.get(0).files[0].name;
			      			$('#savedFile_'+domId).css('display','none');
			      			$("#uploadFileRemove_"+domId).css('display','block');
				      		//$('#removeFileDiv_'+domId).css('display','none');
		   					$("#uploadBtn_"+domId)[0].files=$this.get(0).files;
		   					$('.fileUploadElementForm input[id=\"'+domId+'\"]').attr('elementID','');
		   					$('#uploadBtnMain_count').val("1");
		   					if(typeof executeFunctionsJson.executeFunctionAfterChange==='function'){
		   						executeFunctionsJson.executeFunctionAfterChange(domId);
		   					}
			      		}
			      		
			      		//get(0).files[i].name;
		      		}
	      	}
	      	
	      	if($("#removeFile_"+domId).length>0){
		 		document.getElementById("removeFile_"+domId).onclick = function (){
			       	$('#savedFile_'+domId).val('');
			       	//$('#removeFileDiv_'+domId).css('display','none');
			       	$('#savedFile_'+domId).css('display','none');
			       	$('#uploadFileRemove_'+domId).css('display','block');
					//document.getElementById("uploadBtn_"+domId).disabled = false;
					//clear the file list
			       	/*var dt = [];
					dt._proto_ = Object.create(FileList.prototype);
					 Object.defineProperty($("#uploadBtn_"+domId)[0], 'files', {
					    value: dt,
					    writeable: t
					  });*/
		       		const dt = new DataTransfer();
					$("#uploadBtn_"+domId)[0].files = dt.files;
					//$('#uploadBtn_'+domId).css('display','block');
					//$('div.fileUpload label[for = "uploadBtn_'+domId+'"]').parent().css('display','block');
		        	$('.fileUploadElementForm input[id=\"'+domId+'\"]').attr('elementID','');
		        	document.getElementById("uploadFileRemove_"+domId).value = "";
		        	document.getElementById("uploadFile_"+domId).value = "";
		        	$('#uploadBtnMain_count').val("0");
			      };
	      	}
}
function removeFile(domId){
	if(domId.match(/.*\d/)==null){//trying to remove the first file
		var lastFormIndex =Number($('#uploadBtnMain_count').val());
		if(lastFormIndex==1){//the 1st file is the only one that exist
			$('#uploadFile_'+domId).parent('div').css('display','none');
			//clear the file list
			const dt = new DataTransfer();
			$("#uploadBtn_"+domId)[0].files = dt.files;
			$('#uploadFile_'+domId).val('');
		} else {
			
			$("#uploadBtn_"+domId)[0].files = $('#uploadBtn_'+domId +"_"+(lastFormIndex-1))[0].files;
			$('#uploadFile_'+domId).val($('#uploadFile_'+domId +"_"+(lastFormIndex-1)).val());
			$parentForm = $('#fileUploadElementForm_' + domId+"_"+(lastFormIndex-1));
			$parentForm.remove();
		}
		//reduce the count
		$('#uploadBtnMain_count').val(lastFormIndex-1);
		/*$parentForm = $('#fileUploadElementForm_' + domId);
		$parentForm.remove();
		$('#fileUploadElementForm_'+domId +"_"+($('#uploadBtnMain_count').val()-1)).attr("id",'fileUploadElementForm_'+domId);
		$(domId +"_"+($('#uploadBtnMain_count').val()-1)).attr("id",domId);
		$('#formCodeFull_'+domId +"_"+($('#uploadBtnMain_count').val()-1)).attr("id",'formCodeFull_'+domId);
		$('#formCodeFull_'+domId).val($('#formCode').val+"."+domId);
		$('#uploadBtn_'+domId +"_"+($('#uploadBtnMain_count').val()-1)).prevAll('label').get(0).attr("onclick","removeFile('"+domId+"')");
		$('#uploadBtn_'+domId +"_"+($('#uploadBtnMain_count').val()-1)).prevAll('label').get(1).attr("for","uploadBtn_"+domId);
		$('#uploadBtn_'+domId +"_"+($('#uploadBtnMain_count').val()-1)).attr("id","uploadBtn_"+domId);
		$('#uploadFile_'+domId +"_"+($('#uploadBtnMain_count').val()-1)).attr("id","uploadFile_"+domId);
		$('#uploadBtnMain_count').val($('#uploadBtnMain_count').val()-1);*/
	} else {
		var re = new RegExp(".*"+$('#uploadBtnMain_count').val()+"$");
		if(re.test(domId)==true){//if this is the last file
			$('#uploadBtnMain_count').val(Number($('#uploadBtnMain_count').val())-1);
			$parentForm = $('#fileUploadElementForm_' + domId);
			$parentForm.remove();
		} else {
			var removedFormIndex = domId.slice(domId.lastIndexOf("_")+1);
			var domElem = domId.slice(0,domId.lastIndexOf("_"));
			//recounting the forms
			for(var i=removedFormIndex;i<Number($('#uploadBtnMain_count').val())-1;i++){
				$("#uploadBtn_"+domElem+"_"+i)[0].files = $('#uploadBtn_'+domElem +"_"+(Number(i)+1))[0].files;
				$('#uploadFile_'+domElem+"_"+i).val($('#uploadFile_'+domElem +"_"+(Number(i)+1)).val());
			}
			$parentForm = $('#fileUploadElementForm_' + domElem +"_"+i);
			$parentForm.remove();
			$('#uploadBtnMain_count').val(Number($('#uploadBtnMain_count').val())-1);
		}
	}
}

function initDragAndDropHadle(elem){
	//var obj = $('.dragAndDrop');//$('[id*="dragAndDropHandler"]');
	/*obj.on('click', function (e) {
        AddAttachmentPopup()
    });*/
	if($(elem).attr("dragenterFlag") != "1") { // fix bug 8838 - prevent adding listener using dragenterFlag
		var obj = $(elem);
	    obj.on('dragenter', function (e) {
	        e.stopPropagation();
	        e.preventDefault();
	        //$(this).css('border', '2px solid #3477B1');
	        $(this).css('border-style', 'solid');
	        $(this).css('background-color', '#EEF3F9');
	    });
	    obj.on('dragover', function (e) {
	        e.stopPropagation();
	        e.preventDefault();
	    });
	    obj.on('drop', function (e) {

	        $(this).css('border-style', 'dashed');
	        $(this).css('background-color', '#F9F9F9');
	        $(this).find('label.watermark').css('display','none');
	        e.preventDefault();
	        var files = e.originalEvent.dataTransfer.files;
	        //We need to create forms for the dropped files
	        handleFileUpload(files,obj);
	        
	    });        
	    
	    $(document).on('dragenter', function (e) {
	        e.stopPropagation();
	        e.preventDefault();
	    });
	    $(document).on('dragover', function (e) {
	        e.stopPropagation();
	        e.preventDefault();
	        obj.css('border-style', 'dashed');
	        obj.css('background-color', '#F9F9F9');
	    });
	    $(document).on('drop', function (e) {
	        e.stopPropagation();
	        e.preventDefault();
	    });
	    
	    $(elem).attr("dragenterFlag","1");
	}
}

function handleFileUpload(files,obj){
	var parentElement = $(obj).attr("data-parentelemet");
	var tableType = $('#' + parentElement + '_tableType').val();
	if($(obj).attr("rowId")!=undefined && $(obj).attr("rowId") != ""){//there was a dropping to an editable table cell
	var tableName = $(obj).parents('table.editable').eq(0).attr('id');
		if (files.length > 0) {
			for(var i=0;i<files.length;i++){//(let file of files){
	            var fd = new FormData();
	            fd.append('file', files[i]);
	            fd.append('formCodeFull', 'Document.documentUpload');
	            /*if(!validateUploadFile(files[i])){//check if the size exceeds the maximum size
	            	continue;
	            }*/
	            if(validateUploadFile(files[i])){//check if the size exceeds the maximum size
	            	var tableType_ = "documents";
	            	if($(obj).attr("docTableType") != undefined && $(obj).attr("docTableType") != null && $(obj).attr("docTableType") != '') {
	            		tableType_ = $(obj).attr("docTableType");
	            	}
	            	SendAttachmentToServer(fd,$(obj).attr("rowId"),files[i].name,tableName,tableType_);
	            
	            }
			}
        }
		 if($(obj).children('a').length == 0){
        	$(obj).find('label.watermark').css('display','block');
        }
	}else if(parentElement!=undefined &&parentElement!="" && tableType!=undefined && tableType !=""){

		if (files.length > 0) {
			for(var i=0;i<files.length;i++){//(let file of files){
	            var fd = new FormData();
	            fd.append('file', files[i]);
	            fd.append('formCodeFull', 'Document.documentUpload');
	            /*if(!validateUploadFile(files[i])){//check if the size exceeds the maximum size
	            	continue;
	            }*/
	            if(validateUploadFile(files[i])){//check if the size exceeds the maximum size
	            	SendAttachmentToServer(fd,$('#formId').val(),files[i].name,$(obj).attr("data-parentelemet"),tableType);
	            }
			}
	        
		}
	}else {
		//if multiple is not allowed then takes the first file only/replace the existing file
		var dt = new DataTransfer();
		
	    if($('[name="uploadBtnMain"]').attr('multiple')==undefined || $('[name="uploadBtnMain"]').attr('multiple')== false){
	    	dt.items.add(files[0]);
	    } else {	    	
	    	for(var i=0;i<files.length;i++){//(let file of files){//add the additional files
	    		
	    			dt.items.add(files[i]);
	    		
	    	} 
	    }
	    
		$(obj).find('[name="uploadBtnMain"]')[0].files=dt.files;// $(this).find('[name="uploadBtnMain"]')[0].files=dt.files;  
	    $(obj).find('[name="uploadBtnMain"]').change();
	}
}

function SendAttachmentToServer(formData,rowId,fileName,tableName,docTableType){
	debugger;
	$.ajax({
        url : 'saveFile.request',
        data : formData,
        processData : false,
        contentType : false,
        async:false,
        type : 'POST',
        dataType: 'text',
        success : function(data) {
            elementID = data;
            addNewDocument(elementID,rowId,fileName,tableName,docTableType);
        },
        error : function(err) {
            console.log(err);
        }
    });
}