package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.Map;

import com.skyline.form.entity.Element;

/**
 * 
 *  Element Label Imp
 *  
 *  label corresponding to an element  (the element can be declared in the schema). *  
 *  when element is mandatory, red asterisk appears in front of the label
 *
 */
public class ElementLabelImp extends Element {

	private String text, title, elementName, removeColon, renderType, appendstyle;
	
	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try {
			if (super.init(stateKey, formCode, impCode, initVal).equals("")) {
				text = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "text");
				title = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "title");
				elementName = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "elementName"); 
				removeColon = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "removeColon"); 
				renderType = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "renderType"); 
				appendstyle = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "appendstyle"); 

				return "";
			}
			return "Creation failed";
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			return "Creation failed";
		}
	}

	@Override
	public Map<String, String> getInitHtml(long stateKey, String formId, boolean renderEmpty, String value, String userLastSaveVal, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) {
		Map<String, String> html = new HashMap<String, String>();
		String display = "";
		try {
			if (appendstyle.equals(null)) {
				appendstyle = "";
			}
			else if (!appendstyle.equals("")) {
				appendstyle = appendstyle.replace("{{", ";");
			}
			display = (isHidden) ? "display:none;" : "";
		} catch (Exception e){}
		
		String cssRenderType = renderType.equals("ALERT") ? "class=\"alert-box\"":"";
		
		if(renderType.equals("ALERT")) {
			html.put(layoutBookMark,"<div style=\"" + display + "\"><font color=\"red\">" + generalUtil.getSpringMessagesByKey(text.replaceAll(" ", "_"), text) + (removeColon.equals("True") ? "" : ":") + "</font></div>");
		} else {
			String asterisk = (title.equals("True")) ? "" : "<label class=\"asterisk\" for=\"" + elementName + "\" style='visibility:"+(isMandatory?"visible":"hidden")+";'>*</label>";
			html.put(layoutBookMark,
					"<div formLabelElement=1 " + cssRenderType + " id=\"" + domId + "\" style=\"" + display + "\">"
							+ asterisk + "<label class=\"" + cssRenderType + "\" style =\"display: table-cell; " + appendstyle +"\">"
							+ generalUtil.getSpringMessagesByKey(text.replaceAll(" ", "_"), text) + (removeColon.equals("True") ? "" : ":")
							+ "</label></div>");
		}
		
	    
		//html.put(layoutBookMark + "_ready", "");
		return html;
	}

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) { // TODO return js eval see demo jsp
		String htmlBody;
		htmlBody = "upDateElement({'domId':'" + domId + "','isHidden':'" + isHidden + "','type':'label'});";
		return htmlBody;
	}
 
	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema= "schema:{ \n" + 
				"	text:{  \n" + 
				"		      type:'string',\n" + 
				"		      title:'Text (should match the Label in the linked element name)',\n" + 
				"		   },\n" + 
				"	title : {\r\n" +
				"		type : 'string',\r\n" +
				"		title : 'Is Title?',\r\n" +		
				"		enum :  ['','True'],\r\n" +
				"	},\r\n" +
				"	removeColon : {\r\n" +
				"		type : 'string',\r\n" +
				"		title : 'Remove colon',\r\n" +		
				"		enum :  ['','True'],\r\n" +
				"	},\r\n" +
				"	renderType : {\r\n" +
				"		type : 'string',\r\n" +
				"		title : 'Render type',\r\n" +		
				"		enum :  ['','ALERT'],\r\n" +
				"	},\r\n" +
				" appendstyle:{  \r\n" + 
				" 	type:'string',\r\n" + 
				" 	title:'Append style (For divide properties use {{ instead semicolon)',\r\n" + 		
				" },\r\n" +
				
				"	elementName:{  \n" + 
				"		      type:'string',\n" + 
				"		      title:'Element Name (linked element)'\n" +
				"		   }\n" + 
				(schema.equals("") ? "" : ",\n" + schema) +
				"		}";
		return schema;
	}
}
