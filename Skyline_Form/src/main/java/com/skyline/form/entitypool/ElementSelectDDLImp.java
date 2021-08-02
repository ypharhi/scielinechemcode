package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;

import com.skyline.form.entity.Element;

/**
 * 
 * ElementSelectDDLImp: regular dropdownlist (list)
 * 
 * data is taken from catalog
 *
 */
public class ElementSelectDDLImp extends Element {

//	@Autowired
//	private GeneralUtil generalUtil;
 
	private String placeHolder;
	 
	private String catalogItem;
	
	private String lastCurrentId = ""; // holds last id selection
	
	private String lastCurrentVal = ""; // holds last val (name) selection
	  
	private String width;
	
	private String FirstOption;
	
	private boolean valueFound;
	 
  
	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {		
		try{
			if(super.init(stateKey, formCode, impCode, initVal).equals(""))
			{
				placeHolder = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "placeHolder");				
				catalogItem = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "catalogItem");
				width = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "width");
				FirstOption = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "FirstOption");
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
	public Map<String, String> getInitHtml(long stateKey, String formId, boolean renderEmpty, String inputVal, String userLastSaveVal, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) {
		Map<String, String> html = new HashMap<String, String>();
		valueFound = false;
		String lastvalue = "lastvalue=\"" + inputVal + "\"";
		width = (width.equals("")) ? "" : (width.indexOf("%") != -1) || (width.indexOf("px") != -1) ? width :  width + "px";
		String catalogItemData = "";
		String disabled = (isDisabled) ? "" : "";
		if(!renderEmpty) {
			catalogItemData = generalUtilFormState.getFormCatalogItem(stateKey, formCode, catalogItem.split("\\.")[0], catalogItem.split("\\.")[1], impCode, null);
		} 
		
		html.put(layoutBookMark, isLabel(isHidden) +
						"<select id=\"" + domId + "\" " + inputAttribute + " " + lastvalue + " class=\"" + disabled + "\" style=\"" 
						+ getHidden(isHidden) + "width:" + width + ";\" data-placeholder=\" " 
						+ placeHolder + "\" " + getAttributes(isDisabled, isMandatory, isHidden) + "element=\"ElementGeneral\" "
						+ ">" + getFirstOption() + getCatalogItemAsOption(stateKey, formId, formCode, catalogItemData, inputVal) + " </select>" + isLabelEnd());
		html.put(layoutBookMark + "_ready",
				"$('[id=\"" + domId + "\"]').on('change', function(){"
						+ doOnChangeJSCall + "});" + ((isHidden) ? " $('[id=\"" + domId + "\"]').css('visibility','hidden');":""));
		return html;
	}
	
	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String inputVal, String domId, String inputAttribute, String doOnChangeJSCall,
			boolean isHidden, boolean isDisabled, boolean isMandatory) { // TODO return js eval see demo jsp
		String htmlBody;
		
		lastCurrentId = "";
		lastCurrentVal = "";
		valueFound = false;
		 
		String catalogItemData = "";
		if(!renderEmpty) {
//			catalogItemData = generalUtil
//					.getReflactionString(formCode, "$C{" + catalogItem.split("\\.")[0] + ".getItem."
//							+ catalogItem.split("\\.")[1] + "}");
			catalogItemData = generalUtilFormState.getFormCatalogItem(stateKey, formCode, catalogItem.split("\\.")[0], catalogItem.split("\\.")[1], impCode, null);
		} 
		
		//htmlBody = "upDateChosenElement('" + domId + "','<option></option>" + getCatalogItemAsOption(data, value) + "');";		
		htmlBody = "upDateElement({'isHidden':'" + isHidden + "','isDisabled':'" + isDisabled + "','isMandatory':'"
				+ isMandatory + "','val':'','domId':'"
				+ domId + "','type':'general','value_':'" + getFirstOption() + getCatalogItemAsOption(stateKey, formId, formCode, catalogItemData, inputVal) + "'});";
		return htmlBody;
	}
	
	/**	  
	 * @return the first option of the select by jsonForm declaration
	 */
	private String getFirstOption(){
		if(FirstOption.equals("All")){
			return "<option value=\"ALL\">ALL</option>";//07102019
		}
		else if(FirstOption.equals("Choose")){
			return "<option value=\"\">Choose</option>";
		}
		return "";
	}

	private String getCatalogItemAsOption(long stateKey, String formId, String formCode, String data, String value) {
		 
		StringBuilder sb = new StringBuilder();
		String id_ = "";
		String val_ = "";
		String[] valuesArray = value.split(",");		
		if(!generalUtil.getEmpty(data, "").equals("")) {
			JSONArray arr = new JSONArray("[" + data + "]"); 
			for(int i = 0; i < arr.length(); i++){					
				if(!arr.get(i).toString().equals("null")){
					id_ = arr.getJSONObject(i).getString("ID");
					val_ = arr.getJSONObject(i).getString("VAL");
					for (String valueStr : valuesArray) {
						if (generalUtil.getNull(valueStr).equals(id_)
								|| (valueStr.equals(getDefaultValue(stateKey, formId, formCode)) && valueStr.equals(val_))) {
							sb.append("<option selected=\"selected\" value=\"" + id_ + "\">" + val_ + "</option>");
							lastCurrentId = id_;
							lastCurrentVal = val_;
							valueFound = true;
						} else {
							sb.append("<option value=\"" + id_ + "\">" + val_ + "</option>");
						}
					}									
				}
			}
		}
		 
//		if(!generalUtil.getNull(value).equals("") && !valueFound ) {
//			lastCallValueMissingFlag = true;
//		}
		return sb.toString();
	}

	private String getAttributes(boolean isDisabled, boolean isMandatory, boolean isHidden) {
		StringBuilder sb = new StringBuilder();
//		if (isDisabled)
//			sb.append("disabled ");
		if (isMandatory)
		 {
			sb.append("required ");
//		if (isHidden)
//			sb.append("style='visibility:hidden;'");	
		}
	
		return sb.toString();
	}
	
	private String  getHidden( boolean isHidden){
		if (isHidden) {
			return "visibility:hidden;";
		}
		return "";
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
				"	FirstOption:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'First Option',\r\n" + 
				"      'enum':['','All','Choose']\r\n" + 
				"   },\r\n" +  
				"	catalogItem:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Catalog Column',\r\n" + 
				"      'enum':getResourceValueByType('OBJIDVAL')\r\n" + 
				"   }\r\n" + 		
				(schema.equals("") ? "" : ",\n" + schema) +
				"		}";
		return schema;
	}

//	@Override
//	public String getCatalogItem() {
//		return catalogItem;
//	}

	@Override
	public Map<String, String> getCatalogItemFilterMapByInputVal(String inputVal, boolean isCurrentElementChange) {
		Map<String, String> filterMap = new HashMap<String, String>();
		
		if(generalUtil.getNull(inputVal).trim().equals("") || (!valueFound && !isCurrentElementChange)) {	
			filterMap.put(impCode + "." + generalUtil.getEmpty(getFilterCatalogColumn(), impCode), "");		
		} else {
			filterMap.put(impCode + "." + generalUtil.getEmpty(getFilterCatalogColumn(), impCode), "'" + inputVal.replace(",", "','") + "'");
		}
		
		return filterMap;
	}

//	@Override
//	public boolean isCatalogFlowElement() {
//		return true;
//	}
	
	@Override
	public Map<String, String> getCurrentParameMapByInputVal(String inputVal) {
		Map<String, String> mIdVal = new HashMap<String,String>();
		mIdVal.put("CURRENT_" + impCode, inputVal);
		if(!inputVal.equals("ALL") && !inputVal.equals("")) {
//			mIdVal.put("CURRENT_" + impCode + "_VAL", generalUtilForm.getCurrrentIdInfo(inputVal));// generalUtilForm.getFormNameValByFormId(catalogItem.split("\\.")[1].replace("_OBJIDVAL", ""), inputVal));
			
			if(inputVal.equals(lastCurrentVal)) {
				inputVal = lastCurrentId;
			}
			Map<String,String> infoMap = generalUtilForm.getCurrrentIdInfo(inputVal);
			if(infoMap != null) {
				for (Map.Entry<String, String> entry : infoMap.entrySet()) {
					String key = entry.getKey();
					String val = entry.getValue();
					if(key.equals("ID")) {
						//DO NOTHING
					} else {
						mIdVal.put("CURRENT_" + impCode + "_" + key.toUpperCase(),val);
					}
				}
			}
		} else {
			mIdVal.put("CURRENT_" + impCode + "_VAL", inputVal);

		}
		return  mIdVal;
	}
	
	@Override
	public String getDefaultValue(long stateKey, String formId, String formCode) {
		String dv_ = super.getDefaultValue(stateKey, formId, formCode); //TODO version 9.6 find solution to parameter (that are always with the from upper comma CSV)
		if(dv_.startsWith("'") && dv_.endsWith("'")) {
			dv_ = dv_.substring(1, dv_.length() - 1);
		}
		return dv_; 
	}
	
//	@Override
//	public String getAuditTrailValue(String inputVal) {
//		if (!inputVal.equals("")) {
//			StringBuilder toReturn = new StringBuilder();
//			String[] inputValArray = inputVal.split(",");
//			if (inputValArray.length > 0) {
//				boolean isMultiple = (inputVal.indexOf(",") != -1) ? true : false;
//				String catalogItemData = generalUtilFormState.getFormCatalogItem(stateKey, formCode, catalogItem.split("\\.")[0],
//						catalogItem.split("\\.")[1], impCode);
//				if (!catalogItemData.equals("")) {
//					JSONArray ja = new JSONArray("[" + catalogItemData + "]");
//					for (int i = 0; i < ja.length(); i++) {
//						JSONObject jo = new JSONObject(ja.getJSONObject(i).toString());
//						for (int j = 0; j < inputValArray.length; j++) {
//							if (inputValArray[j].equals(jo.get("ID").toString())) {
//								if(!isMultiple){
//									return jo.get("VAL").toString();
//								}
//								toReturn.append(",");
//								toReturn.append(jo.get("VAL").toString());
//							}
//						}						
//					}
//					return  toReturn.substring(1);
//				}
//			}
//		}
//		return "";
//	}
}
