package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.Map;

import com.skyline.form.entity.Element;

/**
 * 
 *  Element button - used to invoke custom function
 *  
 *  The button's click event invoke the 
 *  general function: elementButtonClickEvent(action, customerEvent) which can be found in the ElmentButtonImp.js. 
 *  routing between functions by the value of blCustomerFunc 
 *  Using action as main argument
 *  
 */
public class ElementButtonImp extends Element {
	
	private String action, text,isIcon,iconClass;
	private String blCustomerFunc;
	private String appendclass;
	private String appendstyle;
	private boolean appendAttachmentForm = false;

	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try{
			if(super.init(stateKey, formCode, impCode, initVal).equals(""))
			{
				isIcon = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "isIcon");
				iconClass = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "iconClass");
				action = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "action");
				text = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "text");
				blCustomerFunc = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "blCustomerFunc");
				appendclass = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "appendclass");
				appendstyle = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "appendstyle");
				appendAttachmentForm = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "appendAttachmentForm"),false); 
				return "";
			}
			return "Creation failed";
		}
		catch(Exception e){
			generalUtilLogger.logWrite(e);
			return "Creation failed";
		}
	}

	@Override
	public Map<String, String> getInitHtml(long stateKey, String formId, boolean renderEmpty, String value, String userLastSaveVal, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) {
		Map<String, String> html = new HashMap<String, String>();
		if (renderEmpty) {
				value = "";			
		}		
		String hidden = (isHidden) ? "visibility:hidden;" : "";
		String disabled = (isDisabled) ? " disabledclass " : "";
		
		String attachment = new String();
		if (appendAttachmentForm){
			String partOfButtonName = "SummaryReport"; //use the same name in ElementButtonImpBL.js --> function openSummaryReport as 1st parameter when call smartFileSumaryRep()
			attachment  = 
				  "<form id=\"" + partOfButtonName + "_AttachmentForm\" method=\"post\" action=\"getAttachment.request\"  style=\"display:none;\" >\n"
				+ "<input name=\"" + partOfButtonName + "_FILE_ID\" type=\"hidden\">\n"
				+ "<input name=\"" + partOfButtonName + "_ContentDisposition\" type=\"hidden\">\n"
				+ "</form>\n";
		}
		
		String button = (generalUtil.getNull(isIcon).equals("") ? "<button type=\"button\" id=\"" + domId + "\" class=\"button " + appendclass + " " + disabled + "\" element=\"" + this.getClass().getSimpleName()
				+ "\" style=\""+ appendstyle + " " + hidden + "\" onclick=\"elementButtonClickEvent('" + blCustomerFunc + "','" + action + "')\">"
				+ generalUtil.getSpringMessagesByKey(text.replaceAll(" ", "_"), text) + "</button>"
				+ attachment
				: "<i title=\""+label+"\" id=\"" + domId + "\"  onclick=\"elementButtonClickEvent('" + blCustomerFunc + "','" + action + "')\" style=\"margin-left: 5px;cursor: pointer;color: #2779aa;font-size: larger;\r\n" + 
						"margin-top: 5px;" + hidden + "\" class=\""+iconClass+" "+disabled + "\"></i>\n");
		html.put(layoutBookMark,
				button);

		//html.put(layoutBookMark + "_ready","");
		return html;
	}

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) {
		String htmlBody;
		htmlBody = "upDateElement({'isHidden':'" + isHidden + "','isDisabled':'" + isDisabled + "','val':'','domId':'"
				+ domId + "','type':'NA'});";
		return htmlBody;
	}
 
	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema= "schema:{ \n" + 				
				" blCustomerFunc:{  \r\n" + 
				" 	type:'string',\r\n" + 
				" 	title:'Customer function (if empty then generalClickEvent is invoke as default)',\r\n" + 		
				" },\r\n" +	 
				" action:{  \r\n" + 
				" 	type:'string',\r\n" + 
				" 	title:'Action (main switch parameter)',\r\n" + 		
				" },\r\n" +	 
				" appendclass:{  \r\n" + 
				" 	type:'string',\r\n" + 
				" 	title:'Append css class',\r\n" + 		
				" },\r\n" +	
				"isIcon : {\n" +
				"	type : 'string',\n" +
				"	title : 'Is Icon',\n" +
				"	enum : ['','True']\n" +
				"},\n" +
				" iconClass:{  \r\n" + 
				" 	type:'string',\r\n" + 
				" 	title:'Icon Class',\r\n" + 		
				" },\r\n" +
				" appendstyle:{  \r\n" + 
				" 	type:'string',\r\n" + 
				" 	title:'Append style',\r\n" + 		
				" },\r\n" +
				" text:{  \r\n" + 
				" 	type:'string',\r\n" + 
				" 	title:'Text (Text of the button)'\r\n" + 			
				" },\r\n" +	
				" appendAttachmentForm:{\r\n" + 
				" 	type: 'boolean',\r\n" + 
				" 	title: 'Append Attachment Form. Check it for Summary Reports button. It will add hidden form for downloading .xls file of Summary Report'\r\n" + 
				" }\r\n" +
				(schema.equals("") ? "" : ",\n" + schema) +
				"		}";
		return schema;
	}
}
