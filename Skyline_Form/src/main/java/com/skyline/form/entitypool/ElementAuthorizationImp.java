package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.skyline.form.entity.Element;

/**
 * ElementAuthorizationImp: set authorization for elements by evaluate of scripts
 * 
 * The elements are selected in the schema(Doms' Id) [/ or function name in customer BL] with their scripts (Disabled / Hidden / mandatory Scripts) and comment as additional info (TODO show it to the user) [/ or function call condition with the script result as true / false and additional info as second arg]
 * The order is important because we update the elements by the order of scripts
 * 
 * the ElementAuthorizationImp evaluates the scripts either in the Init of the page and on AJAX change
 * 
 * 
 * Note: do not enter comma in the input values
 * Note: isMandatoryFieldsRequired() is fired by the save event right before the required fields validation.
 * 	isMandatoryFieldsRequired() returns if the mandatory fields should be validated.
 *  In case they are not necessary, it returns an array of other required fields, if there are some ones.
 *
 * Important: 
 * 1) ElementAuthorizationImp.js must be implementation for authzCheckOnDonReady(newurl) in every page load.
 * authzCheckOnDonReady gets the url. Incase PERMISSION_DENIED=1 is part of the redirect to login screen. in case PERMISSION_DISABLED=1 is part of the URL save button in the popup screen need to be disabled.
 *
 * displayCommentsInInfo - Displays the comments by clicking the Info icon (in addition to seeing the comments should mark "display additional info" and add "getGeneralInfo" into the customer function field).
 */
public class ElementAuthorizationImp extends Element {
	
	private String domIds, domElementType, scriptType, scriptCondition;//,additionalInfo;
//	disabledScripts, hiddenScripts, 
	
	private String[] domIdArray, domElementTypeArray, scriptConditionArray, scriptTypeArray;//,additionalInfoArray;
//	disabledScriptsArray, hiddenScriptsArray, 

	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try{
			if(super.init(stateKey, formCode, impCode, initVal).equals(""))
			{
				domIds = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "domIds");
				domIdArray = domIds.split(",");
				int arraysize = domIdArray.length;
				
				domElementType= generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "domElementType");	
				scriptCondition= generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "scriptCondition");	
				scriptType= generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "scriptType");	
//				additionalInfo = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "additionalInfo");
				 
				domElementTypeArray = domElementType.split(",");
				scriptConditionArray = scriptCondition.split(",");
				scriptTypeArray = scriptType.split(",");
//				additionalInfoArray = additionalInfo.split(",");
				
				if(domElementTypeArray.length != arraysize || scriptConditionArray.length != arraysize ||scriptTypeArray.length != arraysize) {
					return "Creation failed - different size of elements";
				}
				 
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
		StringBuilder innerHtml = new StringBuilder();
		boolean isScriptConditionTrue;
		String exp_ = "";
		String scriptCondition;
//		String hidden;
		String additionalInfo = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "additionalInfo");
		String displayCommentsInInfo = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "displayCommentsInInfo");
		if (renderEmpty) {
			value = "";
		}
		innerHtml.append("<div id=\"" + domId + "\" style=\"display:none;\" element=\""
				+ this.getClass().getSimpleName() + "\">\n");
		for (int i = 0; i < domIdArray.length; i++) {	 
			
			//script
			if(scriptConditionArray.length > i){
				exp_ = (generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "scriptCondition").split(",")[i]);
				if(exp_!= null && exp_.equals("1")) {
					exp_ = "true";
				}
				isScriptConditionTrue = generalUtil.getNullBoolean(exp_, false, impCode);
				scriptCondition = (isScriptConditionTrue) ? "1" : "0";
				
				//system sees all
				if(generalUtil.getSessionUserName().equals("system?")) { //change to system in order to pass condition
					scriptCondition = "0";
				}
			}
			else {
				scriptCondition = "0";
			}	
			
			String additionalInfoIndex = "NA";
			String commentsAsInfo = "";
			try {
				additionalInfoIndex = (i < (additionalInfo.split(",")).length ? additionalInfo.split(",")[i]: "NA");
				commentsAsInfo = (i < (displayCommentsInInfo.split(",")).length ? displayCommentsInInfo.split(",")[i]: "false");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			innerHtml.append("<input type=\"hidden\" id=\"" + domId + "_" + domIdArray[i] + "_" + String.valueOf(i) + "\" domElementType=\""
					+ domElementTypeArray[i] + "\" scriptType=\"" + scriptTypeArray[i] + "\" scriptCondition=\"" + scriptCondition + "\" targetid=\"" + domIdArray[i] + "\"additionalInfo=\"" + additionalInfoIndex + "\"displayCommentsInInfo=\"" + commentsAsInfo + "\" expressionInfo=\"" + exp_ + "\" >\n");
		}
		innerHtml.append("</div>\n");
		html.put(layoutBookMark, innerHtml.toString());

		//html.put(layoutBookMark + "_ready", "");
		return html;
	}

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) {
		String htmlBody;
		String scriptCondition;
		String exp_ = "";
		boolean isScriptConditionTrue;
		String additionalInfo = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "additionalInfo");
		JSONObject jo1 = new JSONObject(); 
		
		//disable scripts
		for (int i = 0; i < domIdArray.length; i++) {
			JSONObject jo = new JSONObject();
			  
//			JSONObject joInfExp = new JSONObject();
			exp_ = (generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "scriptCondition").split(",")[i]);
			isScriptConditionTrue = generalUtil.getNullBoolean(exp_, false, impCode);
			scriptCondition = (isScriptConditionTrue) ? "1" : "0";
			
			//admin system sees all
			//system sees all
			if(generalUtil.getSessionUserName().equals("system?")) { //change to system in order to pass condition
				scriptCondition = "0";
			}
			
			
			jo.put("scriptCondition", scriptCondition);
			jo.put("scriptType", scriptTypeArray[i]);
			jo.put("domElementType", domElementTypeArray[i]);
			
			String additionalInfoIndex = "NA";
			try {
				additionalInfoIndex = (i < (additionalInfo.split(",")).length ? additionalInfo.split(",")[i]: "NA");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			jo.put("additionalInfo",additionalInfoIndex);
			jo.put("expressionInfo",exp_.replace("'", "")); 
			jo.put("targetId", domIdArray[i]);
			/*if(jo1.isNull(domId + "_" + domIdArray[i])){//ab 18022018 - let the option to assign some scripts to the same element
				jo1.put(domId + "_" + domIdArray[i], jo);
			}
			else{*/
			jo1.put(domId + "_" + domIdArray[i] + "_" +String.valueOf(i), jo);
			//}
		}
		
		htmlBody = "upDateElement({'isHidden':'" + isHidden + "','isDisabled':'" + isDisabled + "','val':'" + jo1.toString() + "','domId':'"
				+ domId + "','type':'ElementAuthorizationImp'});";
		return htmlBody;
	}

	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema= "schema:{ \n" +
				"	domIds : {\r\n" +
				"		type : 'array',\r\n" +
				"		title : 'Dom Element [domid, function or tab]',\r\n" +
				"		items : {\r\n" +
				"			type : 'text',\r\n" +
				"			title : 'Element:'\r\n" +
				"		}\r\n" +
				"	},\r\n" +
				"	domElementType : {\r\n" +
				"		type : 'array',\r\n" +
				"		title : 'Dom Element Type [ID as default can be domid, function or tab]',\r\n" +
				"		items : {\r\n" +
				"			type : 'string',\r\n" +
				"			title : 'Type:',\r\n" +
				"           'enum':['ID']\r\n" + 
				"		}\r\n" +
				"	},\r\n" +
				"	scriptType : {\r\n" +
				"		type : 'array',\r\n" +
				"		title : 'Scripts Type',\r\n" +
				"		items : {\r\n" +
				"			type : 'string',\r\n" +
				"			title : 'Scrip Type:',\r\n" +
				"           'enum':['HIDDEN','DISABLE','MANDATORY','NA']\r\n" + 
				"		}\r\n" +
				"	},\r\n" +
				"	scriptCondition : {\r\n" +
				"		type : 'array',\r\n" +
				"		title : 'Scripts condition (arg1 in case of function with boolean result of the script)',\r\n" +
				"		items : {\r\n" +
				"			type : 'text',\r\n" +
				"			title : 'script:'\r\n" +
				"		}\r\n" +
				"	},\r\n" +
				"	additionalInfo : {\r\n" +
				"		type : 'array',\r\n" +
				"		title : 'Additinal Info (arg2 if function else general comment [do not use comma!])',\r\n" +
				"		items : {\r\n" +
				"			type : 'text',\r\n" +
				"			title : 'Additinal Info:'\r\n" +
				"		}\r\n" +
				"	},\r\n" +
				"	displayCommentsInInfo : {\r\n" + 
				"		type : 'array',\r\n" +
				"		title : 'Display Additional Info by clicking the info icon (display additional info checkbox should be checked)',\r\n" +
				"		items : {\r\n" +
				"			type : 'string',\r\n" +
				"			title : 'Show Additional Info:',\r\n" +
				"           'enum':['false','true']\r\n" + 
				"		}\r\n" +
				"	}\r\n" +
//				"	preventImmediateUpdate : {\r\n" +
//				"		type : 'array',\r\n" +
//				"		title : 'Prevent Immediate Update',\r\n" +
//				"		items : {\r\n" +
//				"			enum : ['False','True'],\r\n" +
//				"			title : 'Code:'\r\n" +
//				"		}\r\n" +
//				"	}\r\n" +
				(schema.equals("") ? "" : ",\n" + schema) +
				"		}";
		return schema;
	}
}
