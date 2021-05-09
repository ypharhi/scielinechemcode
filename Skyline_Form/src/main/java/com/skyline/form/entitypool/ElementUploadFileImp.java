package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

import com.skyline.form.entity.Element;

/**
 * 
 * ElementUploadFileImp: saves files / excel sheets as CLOB
 * 
 * currently work on Document form only
 *
 */
public class ElementUploadFileImp extends Element {

	private boolean isExcelSheetUpload;
	private boolean isMultipleFiles;
	private String parentElement;
	private String formCode;
	private String executeFunctionBeforeChange;
	private String executeFunctionAfterChange;
	
	@Value("${ignoreBrowserName:0}")//Used for the case that 'new DataTransfer()' command would be supported in the advanced versions of non-chrome browsers
	private int ignoreBrowserName;
	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) 
	{
		this.formCode = formCode;
		try {
			if (super.init(stateKey, formCode, impCode, initVal).equals("")) {
				isExcelSheetUpload = generalUtil
						.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "isExcelSheetUpload"), false);
				isMultipleFiles = generalUtil
						.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "isMultipleFiles"), false);
				parentElement = generalUtil
						.getNull(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "parentElement"), "");
				executeFunctionBeforeChange = generalUtil
						.getNull(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "executeFunctionBeforeChange"), "");
				executeFunctionAfterChange = generalUtil
						.getNull(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "executeFunctionAfterChange"), "");
				return "";
			}
			return "Creation failed";
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			return "Creation failed";
		}
	}

	@Override
	/**
	 * Method initialize 'Upload file' element.
	 * Only one Upload element is supported per page.
	 * Performance: If a file was attached before there is 'Remove' button displayed along with attached file.
	 * 				It possible to update file by removing attached file and upload new file, or
	 * 				just remove file and save(if itsn't mandatory field).
	 * On submit, fileID is retrieved from LASTSAVEVALUE table by domID and a file is saved along with that fileID.
	 * In Chrome browser there is a possibility to drag&drop files and add multiple files at once. As soon as the files have been being uploaded, there's an option to remove or change each one of them.
	 * 			The drag&drop area and the file type domelement attaching the files to a hidden form. The list of files are attached to several dynamic forms(the first one is static and invisible as long as no file was attached).
	 * 			Setting the files in the forms is made by using new DataTransfer() that is supported in chrome only.
	 * https://stackoverflow.com/questions/16943605/remove-a-filelist-item-from-a-multiple-inputfile/47642446#47642446
	 * https://github.com/jsdom/jsdom/issues/1272
	 * TODO:add possibility for multiple upload forms on one page.
	 */
	public Map<String, String> getInitHtml(long stateKey, String formId, boolean renderEmpty, String value, String userLastSaveVal, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) {
		if(ignoreBrowserName == 0){
			if(generalUtil.getSessionBrowserName().equalsIgnoreCase("chrome")){
				return getInitHtmlChrome(stateKey, formId, renderEmpty, value, userLastSaveVal, domId, inputAttribute,
						doOnChangeJSCall, isHidden, isDisabled, isMandatory);
			}
			return getInitHtmlAllBrowsers(stateKey, formId, renderEmpty, value, userLastSaveVal, domId, inputAttribute,
						doOnChangeJSCall, isHidden, isDisabled, isMandatory);
		} else {
			return getInitHtmlChrome(stateKey, formId, renderEmpty, value, userLastSaveVal, domId, inputAttribute,
						doOnChangeJSCall, isHidden, isDisabled, isMandatory);
		}
	}

	private Map<String, String> getInitHtmlAllBrowsers(long stateKey, String formId, boolean renderEmpty, String value,
			String userLastSaveVal, String domId, String inputAttribute, String doOnChangeJSCall, boolean isHidden,
			boolean isDisabled, boolean isMandatory) {
		Map<String, String> html = new HashMap<String, String>();
		String hidden = (isHidden)? "display:none;":"";
		String mandatory = (isMandatory)? " required ":"";
		String action = (isExcelSheetUpload)?"saveFileAsClob.request":"saveFile.request";
		value = (value.equals("-1")) ? "" : value;
		String fileName = "";
		String disableUploadBtn = "false";
		String displayRemoveBtn = "display:none";
		String displayUploadBtn = "display:block";
		String attachment  = 
				  "<form id=\"" + domId + "_AttachmentForm\" method=\"post\" action=\"getAttachment.request\"  style=\"display:none;\"  target=\"" + domId + "_Iframe\">\n"
				//+ "<input name=\"" + domId + "_\" type=\"hidden\">\n"
				+ "<input name=\"" + domId + "_FILE_ID\" type=\"hidden\">\n"
				+ "<input name=\"" + domId + "_ContentDisposition\" type=\"hidden\">\n"
				+ "</form>\n";
		String iframe  = "<iframe name=\"" + domId + "_Iframe\" style=\"width: calc(100% - 900px);height: calc(100vh - 400px);display:none;margin-left: 10px;\" src=\"about:blank\"></iframe>\n";
		if(!value.equals(""))
		{
			fileName = generalUtilFormState.getFileName(value);
			disableUploadBtn = "true";
			displayRemoveBtn = "display:block";
			displayUploadBtn = "display:none";
		}
		
		String innerHTML = "<form class=\"fileUploadElementForm\" id=\"fileUploadElementForm_" + domId +"\" " +
				" 				method=\"post\" action=\"" + action + "\"  enctype=\"multipart/form-data\" style=\"width:100%;" + hidden + "\">\n" + 
				"	 <input id=\"" + domId + "\" elementID=\"" + value + "\" type=\"hidden\" name=\"fileFormId\" "
										+ "value=\"\" " + inputAttribute + mandatory + " element=\"" + this.getClass().getSimpleName() + "\">\n" +
				"	 <input type=\"hidden\" id=\"formCodeFull_" + domId + "\" name=\"formCodeFull\" value=\"" + formCode+"."+domId + "\">\n" + 
				"	<div> \n" + 
				"	 	<input type=\"text\" style = \"display:none;\" disabled=\"disabled\" placeholder=\"Choose File\"  id=\"uploadFile_"+domId+"\"  name=\"uploadFile\">\n" + 
				"	 	<div class=\"fileUpload fileBtn fileBtn-primary\"  >\n" +
/*				"			<span>Upload</span>\n" +
*/				"			<input  type=\"file\" class=\"upload\" id=\"uploadBtn_"+domId+"\" name=\"uploadBtn\" \" style=\""+displayUploadBtn+"\"/>\n" +
				"	 	</div>	\n" + 	
				"	 </div>	\n" + 	
				"    <div id=\"removeFileDiv_"+domId+"\" style=\"padding-top:0;margin-top:0;"+displayRemoveBtn+"\">\n" +	
				"   	<a style=\"color: rgb(81, 155, 205); cursor: pointer;\" onclick=\"smartFile('"+domId+"',$('#"+domId+"').attr('elementID'))\"> <span id=\"savedFile_"+domId+"\">"+fileName+"</span></a>   \n" +
				"		<button  type=\"button\" class=\"button fileUpload fileBtn fileBtn-primary\" id=\"removeFile_"+domId+"\">Remove file</button>\n" +
				"    </div>\n" +							
				"	</form>\n"	+
				attachment +
				iframe
				+ "<script>\n" +
				"		document.getElementById(\"uploadBtn_"+domId+"\").disabled = "+disableUploadBtn+"; \n" +
				" 		document.getElementById(\"uploadBtn_"+domId+"\").onchange = function () {\n" +
				(!executeFunctionBeforeChange.isEmpty()?
		 				executeFunctionBeforeChange+"({event:e,elem:this,callback:\n"+
		 				"function(args){\n"
		 				+ "document.getElementById(\"uploadFile_"+domId +"_"+"\").value = $(args.elem).get(0).files[0].name;\n"
		 						+ (!executeFunctionAfterChange.isEmpty()?executeFunctionAfterChange+"("+domId+");":"")+"}});":
				"    	 	document.getElementById(\"uploadFile_"+domId+"\").value = $(this).get(0).files[0].name;\n"
				+ (!executeFunctionAfterChange.isEmpty()?executeFunctionAfterChange+"("+domId+");":"")) +				
				"      	}; \n"	+
				" 		document.getElementById(\"removeFile_"+domId+"\").onclick = function () {\n" +
				"        	$('#savedFile_"+domId+"').html('');\n" + 
				"       	$('#removeFileDiv_"+domId+"').css('display','none');\n" + 
				"			document.getElementById(\"uploadBtn_"+domId+"\").disabled = false; \n" +	
				"			$('#uploadBtn_"+domId+"').css('display','block'); \n" +
				"        	$('.fileUploadElementForm input[id=\""+domId+"\"]').attr('elementID',''); \n" +
				"      }; \n" +
				"<" + "/" + "script>";
		html.put(layoutBookMark,innerHTML);
		return html;
	}

	private Map<String, String> getInitHtmlChrome(long stateKey, String formId, boolean renderEmpty, String value,
			String userLastSaveVal, String domId, String inputAttribute, String doOnChangeJSCall, boolean isHidden,
			boolean isDisabled, boolean isMandatory) {
		Map<String, String> html = new HashMap<String, String>();
		String hidden = (isHidden)? "display:none;":"";
		String mandatory = (isMandatory)? " required ":"";
		String action = (isExcelSheetUpload)?"saveFileAsClob.request":"saveFile.request";
		value = (value.equals("-1")) ? "" : value;
		String fileName = "";
		String disableUploadBtn = "false";
		String displayRemoveBtn = "display:none";
		String displayUploadBtn = "display:block";
		String attachment  = 
				  "<form id=\"" + domId + "_AttachmentForm\" method=\"post\" action=\"getAttachment.request\"  style=\"display:none;\"  target=\"" + domId + "_Iframe\">\n"
				+ "<input name=\"" + domId + "_FILE_ID\" type=\"hidden\">\n"
				+ "<input name=\"" + domId + "_ContentDisposition\" type=\"hidden\">\n"
				+ "</form>\n";
		String iframe  = "<iframe name=\"" + domId + "_Iframe\" style=\"width: calc(100% - 900px);height: calc(100vh - 400px);display:none;margin-left: 10px;\" src=\"about:blank\"></iframe>\n";
		if(!value.equals(""))
		{
			fileName = generalUtilFormState.getFileName(value);
			disableUploadBtn = "true";
			displayRemoveBtn = "display:block";
			displayUploadBtn = "display:none";
		}
		
		
		String innerHTML = "<div data-parentelemet=\""+parentElement+"\" class=\"fileUpload fileBtn fileBtn-primary dragAndDrop\" style = \""+displayUploadBtn+";margin-bottom:10px;"+hidden+"\"  id = \""+ domId +"dragAndDropHandler\">\n" +
				"			<label for=\"uploadBtnMain_"+domId+"\" >"+
			    "				<i class=\"fa fa-cloud-upload\"></i> Upload | Drag and Drop File"+(isMultipleFiles?"s":"")+
			    "			</label>"+
				"			<input  type=\"file\" class=\"upload\" id=\"uploadBtnMain_"+domId+"\" name=\"uploadBtnMain\" style=\"display:none\" "+(isMultipleFiles?"multiple":"")+"/>\n" +
			    "			<input type=\"hidden\" id=\"uploadBtnMain_count\" value=\""+(value.isEmpty()?"0":"1")+"\"/>"+
				"	 		<input type=\"text\" style = \"display:none;background-color:white;color:gray\" disabled=\"disabled\" placeholder=\"Choose File\"  id=\"uploadFileMain_"+domId+"\"  name=\"uploadFileMain\">\n" +
				"	 	</div>	\n" + 
				
				
				"<form class=\"fileUploadElementForm\" id=\"fileUploadElementForm_" + domId +"\" " +
				" 				method=\"post\" action=\"" + action + "\"  enctype=\"multipart/form-data\" style=\"width:100%;" + hidden + "\">\n" + 
				"	 <input id=\"" + domId + "\" elementID=\"" + value + "\" type=\"hidden\" name=\"fileFormId\" "
										+ "value=\"\" " + inputAttribute + mandatory + " element=\"" + this.getClass().getSimpleName() + "\">\n" +
				"	 <input type=\"hidden\" id=\"formCodeFull_" + domId + "\" name=\"formCodeFull\" value=\"" + formCode+"."+domId + "\">\n" + 
				"	<div> \n" + 
				//"	 	<input type=\"text\" style = \"display:none;\" disabled=\"disabled\" placeholder=\"Choose File\"  id=\"uploadFile_"+domId+"\"  name=\"uploadFile\">\n" + 
				"	 	<div class=\"uploadFileUnit fileUpload fileBtn fileBtn-primary\" style = \"display:none\" >\n" +
				"			<label onclick=\"removeFile('"+domId+"')\" class=\"uploadItem\"><i class=\"fa fa-times\" title=\"Remove\"></i></label>"+
				"			<label for=\"uploadBtn_"+domId+"\" class=\"uploadItem\">"+
			    "				<i class=\"fa fa-cloud-upload\" title=\"Change\"></i>"+
			    "			</label>"+
				"			<input  type=\"file\" class=\"upload\" id=\"uploadBtn_"+domId+"\" name=\"uploadBtn\" style=\"display:none\"/>\n" +
				"	 		<input type=\"text\" style = \"display:none;\" disabled=\"disabled\" placeholder=\"Choose File\" class=\"uploadItem\" id=\"uploadFile_"+domId+"\"  name=\"uploadFile\">\n" +
				"	 	</div>	\n" + 	
				"	 </div>	\n" + 	
				"    <div id=\"removeFileDiv_"+domId+"\" class=\"uploadFileUnit\" style=\"padding-top:0;margin-top:0;"+displayRemoveBtn+"\">\n" +	
				"   	<input id=\"savedFile_"+domId+"\" class=\"uploadItem\" type=\"text\" style = \"display:block;color: rgb(81, 155, 205); cursor: pointer;\" value=\""+fileName+"\" onclick=\"smartFile('"+domId+"',$('#"+domId+"').attr('elementID'))\"/>\n"+//<a style=\"float:left;color: rgb(81, 155, 205); cursor: pointer;\" onclick=\"smartFile('"+domId+"',$('#"+domId+"').attr('elementID'))\"> "+
				//"		<span id=\"savedFile_"+domId+"\">"+fileName+"</span></input>   \n" +
				//"		<button  type=\"button\" class=\"button fileUpload fileBtn fileBtn-primary\" id=\"removeFile_"+domId+"\">Remove file</button>\n" +
				"		<label id=\"removeFile_"+domId+"\" class=\"uploadItem\"><i class=\"fa fa-times\" title=\"Remove\"></i></label>"+
				"		<label for=\"uploadBtnRemove_"+domId+"\" class=\"uploadItem\">"+
				"			<i class=\"fa fa-cloud-upload\" title=\"Change\"></i>"+
				"		</label>"+
				"		<input  type=\"file\" class=\"upload\" id=\"uploadBtnRemove_"+domId+"\" name=\"uploadBtnRemove\" style=\"display:none\"/>\n" +
				"	 	<input type=\"text\" style = \"display:none;\" disabled=\"disabled\" placeholder=\"Choose File\" class=\"uploadItem\" id=\"uploadFileRemove_"+domId+"\"  name=\"uploadFileRemove\">\n" +
				"    </div>\n" +							
				"	</form>\n"	+
				attachment +
				iframe
				+ "<script>\n" +
				"		buildScriptFile('"+domId+"',"+disableUploadBtn+",'"+this.getClass().getSimpleName() +"','"+inputAttribute+"',{executeFunctionBeforeChange:"+(executeFunctionBeforeChange.isEmpty()?"''":executeFunctionBeforeChange)+",executeFunctionAfterChange:"+(executeFunctionAfterChange.isEmpty()?"''":executeFunctionAfterChange)+"});\n"+	
				"		$(document).ready(initDragAndDropHadle($('#"+domId +"dragAndDropHandler')));\n"	+
				/*"		document.getElementById(\"uploadBtn_"+domId+"\").disabled = "+disableUploadBtn+"; \n" +*/
				"		document.getElementById(\"uploadBtn_"+domId+"\").onchange = function (e) {\n" +
				 		(!executeFunctionBeforeChange.isEmpty()?
		 				executeFunctionBeforeChange+"({event:e,elem:this,callback:\n"+
		 				"function(args){document.getElementById(\"uploadFile_"+domId +"\").value = $(args.elem).get(0).files[0].name;\n"
		 				+ (!executeFunctionAfterChange.isEmpty()?executeFunctionAfterChange+"("+domId+");":"")+"}});":
				"    	 	document.getElementById(\"uploadFile_"+domId+"\").value = $(this).get(0).files[0].name;\n"
						+ (!executeFunctionAfterChange.isEmpty()?executeFunctionAfterChange+"("+domId+");":"")) +
				"		}\n"+
				/*"		var names = [];"+
			    "		for (var i = 0; i < $(this).get(0).files.length; ++i) {"+
			    "		   names.push($(this).get(0).files[i].name);"+
			    "		}"+
			    "		document.getElementById(\"uploadFile_"+domId+"\").value=names;"+
				"      	}; \n"	+
				" 		document.getElementById(\"removeFile_"+domId+"\").onclick = function () {\n" +
				"        	$('#savedFile_"+domId+"').html('');\n" + 
				"       	$('#removeFileDiv_"+domId+"').css('display','none');\n" + 
				"			document.getElementById(\"uploadBtn_"+domId+"\").disabled = false; \n" +	
				"			$('#uploadBtn_"+domId+"').css('display','block'); \n" +
				"        	$('.fileUploadElementForm input[id=\""+domId+"\"]').attr('elementID',''); \n" +
				"      }; \n" +*/
				"<" + "/" + "script>";
		html.put(layoutBookMark,innerHTML);
		//html.put(layoutBookMark + "_ready","");
		return html;
	}

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute, String doOnChangeJSCall,
			boolean isHidden, boolean isDisabled, boolean isMandatory) { // TODO return js eval see demo jsp
		String htmlBody;
		htmlBody = "upDateElement({'domId':'" + domId + "','type':'upload','isHidden':'" + isHidden + "','isMandatory':'" + isMandatory + "'});";
		return htmlBody;
	}
	
	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema= "schema:{ \n" + 
				"isExcelSheetUpload:{\r\n" + 
				" type: 'boolean',\r\n" + 
				" title: 'Is Excel Sheet Upload'\r\n" + 
				"   },\r\n" +	
				"isMultipleFiles:{\r\n" + 
				" type: 'string',\r\n" + 
				" title: 'Is Multiple files Upload enabled (expects to get a parameter/constant evaluated as true/false)'\r\n" + 
				"   },\r\n" +
				"executeFunctionBeforeChange:{\r\n" + 
				" type: 'string',\r\n" + 
				" title: 'Function to be executed before changing the file'\r\n" 
				+ "   },\r\n" +
				"executeFunctionAfterChange:{\r\n" + 
				" type: 'string',\r\n" + 
				" title: 'Function to be executed after changing the file'\r\n" + 
				"   },\r\n" +
				(schema.equals("") ? "" : "\n" + schema) +
				"		}";
		return schema;
	}
}
