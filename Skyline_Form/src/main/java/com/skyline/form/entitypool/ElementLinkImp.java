package com.skyline.form.entitypool;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.skyline.form.entity.Element;

/**
 * 
 * ElementLinkImp: link to another form, the element is not in use

 */
public class ElementLinkImp extends Element {

	private String form;

	private Boolean isPopUp;

	private String text;

	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try{
			if(super.init(stateKey, formCode, impCode, initVal).equals(""))
			{
				form = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "form");
				isPopUp = Boolean.valueOf(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "isPopUp"));
				text = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "text");
				return "";
			}
			return "Creation failed";
		}
		catch(Exception e)
		{
			generalUtilLogger.logWrite(e);
			return "Creation failed";
		}
	}

	@Override
	public Map<String, String> getInitHtml(long stateKey, String formId, boolean renderEmpty, String value, String userLastSaveVal, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) {
		Map<String, String> html = new HashMap<String, String>();
		html.put(layoutBookMark, "<label style='color:white;'>*</label><a id=\"" + domId + "\" " + inputAttribute + " "
				+ getAttributes(stateKey, isDisabled) + ">" + text + "</a>");
		html.put(layoutBookMark + "_ready", "$('#" + domId + "').on('change', function(){" + doOnChangeJSCall + "});");
		return html;
	}

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute, String doOnChangeJSCall,
			boolean isHidden, boolean isDisabled, boolean isMandatory) { // TODO return js eval see demo jsp
		String htmlBody;
		htmlBody = "upDateElement({'isDisabled':'" + isDisabled + "','val':'" + text + "','domId':'" + domId
				+ "','type':'regular'});";
		return htmlBody;
	}

	private String getAttributes(long stateKey, boolean isDisabled) {
		StringBuilder sb = new StringBuilder();
		if (isDisabled) {
			sb.append("class='disabledclass' ");
		}
		if (isPopUp) {
			sb.append("href=\"#\" onclick=\"window.open('/form/init.request?stateKey=" + stateKey + "&formCode=" + form + "&formId&user','"
					+ (new Date().getTime()) + "','width=500,height=400')\"");
		} else {
			sb.append("href=\"/form/init.request?stateKey=" + stateKey + "&formCode=" + form + "&formId&user\"");
		}
		return sb.toString();
	}
 
	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema = "schema:{ \r\n" + "text:{  \r\n" + "      type:'string',\r\n" + "      title:'Text'     \r\n"
				+ "   },\r\n" + "   form:{  \r\n" + "      type:'string',\r\n" + "      title:'Form',\r\n"
				+ "      'enum': getResourceValueByType('FORM_CODE_%')\r\n" + "   }, \r\n" + "   isPopUp:{  \r\n"
				+ "      type:'string',\r\n" + "      title:'PopUp',\r\n" + "      'enum':[  \r\n"
				+ "         'False',\r\n" + "         'True'\r\n" + "      ]\r\n" + "   }"
				+ (schema.equals("") ? "" : ",\n" + schema) + "\r\n" + "}";
		return schema;
	}
	
}
