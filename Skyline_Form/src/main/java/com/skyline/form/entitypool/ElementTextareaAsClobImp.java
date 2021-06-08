package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.Map;

import com.skyline.form.bean.ElementInfoAuditTrailDisplay;
import com.skyline.form.entity.Element;

/**
 * 
 * Textarea element Imp
 *
 */
public class ElementTextareaAsClobImp extends Element {

	private String placeHolder, width, height;	

	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try {
			if (super.init(stateKey, formCode, impCode, initVal).equals("")) {
				placeHolder = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "placeHolder");
				width = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "width");
				height = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "height");
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
		if(renderEmpty) {
			value="";
		}
		
		String fullData = "";
		
		if(!generalUtil.getNull(value).equals("")) {
			fullData = generalUtilFormState.getStringContent(value, formCode, domId, "-1");
		}
		 
		
		String width_ = (width.equals("")) ? "" : (width.indexOf("%") != -1) || (width.indexOf("px") != -1) ? "width:"+width+";" :  "width:"+width+"px;";
		String height_ = (height.equals("")) ? "" : (height.indexOf("px") != -1) ? "height:"+height+";" : "height:"+height+"px;";
		String hidden = (isHidden)? "visibility:hidden;":"";
		String disabled = (isDisabled) ? " disabledclass " : "";
		
		html.put(layoutBookMark,
				isLabel(isHidden) + 
				"<textarea class=\"" + disabled + "\" autocomplete=\"off\"  id=\"" + domId + "\"  placeholder=\""+placeHolder+"\" style=\"" + hidden + "border-radius: 5px;vertical-align: middle;" + width_ + height_ + "\" "
						+ inputAttribute + getAttributes(isDisabled, isMandatory, isHidden)
						+" element=\"ElementGeneral\">" + fullData + "</textarea>"+ isLabelEnd());
		
		
		//html.put(layoutBookMark + "_ready","");
		return html;
	}

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute, String doOnChangeJSCall,
			boolean isHidden, boolean isDisabled, boolean isMandatory) { // TODO return js eval see demo jsp
		String htmlBody;
		if(renderEmpty) {
			value="";
		}
		htmlBody = "upDateElement({'isHidden':'" + isHidden + "','isDisabled':'" + isDisabled + "','isMandatory':'"
				+ isMandatory + "','val':'','domId':'" + domId + "','type':'general','value_':'" + value + "'});";
		return htmlBody;
	}
	
	private String getAttributes(boolean isDisabled, boolean isMandatory, boolean isHidden) {
		StringBuilder sb = new StringBuilder();
//		if (isDisabled)
//			sb.append(" disabled ");
		if (isMandatory) {
			sb.append(" required ");
		}	
		return sb.toString();
	}
	
	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema= "schema:{ \n" + 
				"	placeHolder:{  \n" + 
				"		      type:'string',\n" + 
				"		      title:'PlaceHolder',\n" + 
				"		      'default':'Choose:'\n" + 
				"		   },\n" + 	
				"	width:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Width',\r\n" +
				"	   'default':'100%'\n" +
				"   },\r\n" + 
				"	height:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Height',\r\n" +
				"   }\r\n" + 
			
				(schema.equals("") ? "" : ",\n" + schema) +
				"		}";
		return schema;
	}
	
	@Override
	public ElementInfoAuditTrailDisplay getAuditTrailValue(String formCode, String key, String postSaveValue, String originValue, String uiDisplayValue)
	{
		ElementInfoAuditTrailDisplay elementValueJobFlag = null;
		String uiDisplayValueUpdated = "";

		try {
			if (postSaveValue.equals(originValue)) {
				elementValueJobFlag = new ElementInfoAuditTrailDisplay(uiDisplayValue, "0");
			} else {
				uiDisplayValueUpdated = postSaveValue;
				elementValueJobFlag = new ElementInfoAuditTrailDisplay(uiDisplayValueUpdated, "0");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			elementValueJobFlag = new ElementInfoAuditTrailDisplay("", "1");
		}

		return elementValueJobFlag;
	}
}
