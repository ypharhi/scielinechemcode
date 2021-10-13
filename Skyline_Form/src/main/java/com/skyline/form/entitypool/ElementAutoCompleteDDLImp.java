package com.skyline.form.entitypool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.skyline.form.bean.ElementInfoAuditTrailDisplay;
import com.skyline.form.entity.Element;

/**
 * 
 * AutoCompleteDDLImp
 * 
 * AutoComplete drop down list, gets the options from catalog
 *
 */
public class ElementAutoCompleteDDLImp extends Element {
	
	private String placeHolder;
	
	private Boolean isMultiple;
	
	private String multipleSelectedValues;
	
	private String catalogItem;
	
	private String width;
	
	private boolean valueFound = false;
	
	private String FirstOption;
	
	private boolean ADD_ALL_ON_EMPTY_DATA = true;	
	
	private boolean SELECT_ALL_ON_EMPTY_DATA = true;
	
	private boolean saveAsJSON = false;
	
	private boolean selectFirstValue = false; //TODO in next version
	
	private boolean REMOVE_AND_SAVE_ALL = false; 
	
	private boolean removeDeselectOption = false;
	
	private String csvValues;
	
	private final String FIRST_VALUE_FLAG = "@FG_FIRST_VALUE_FLAG@";
  
	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try {
			if (super.init(stateKey, formCode, impCode, initVal).equals("")) {
				if(formCode != null && (formCode.equalsIgnoreCase("ExperimentReport")|| formCode.equalsIgnoreCase("PermissionScheme"))) { // TODO add it as configuration default true and test it more...
					ADD_ALL_ON_EMPTY_DATA = false;
					SELECT_ALL_ON_EMPTY_DATA = false;
					if(formCode.equalsIgnoreCase("PermissionScheme")) {
						REMOVE_AND_SAVE_ALL = true;
					}
				}
				placeHolder = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "placeHolder");
				isMultiple = Boolean.valueOf(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "multiple"));
				catalogItem = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "catalogItem");
				width = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "width");
				FirstOption = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "FirstOption");
				saveAsJSON = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "saveAsJSON"),false);
				selectFirstValue = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "selectFirstValue"),false);
				if(FirstOption != null && FirstOption.equalsIgnoreCase("all")) {
					selectFirstValue = false;
				}
				if(isMultiple) {
					multipleSelectedValues=generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "multipleSelectedValues");
					
				}
					
				removeDeselectOption = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "removeDeselectOption"),false);
				csvValues = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "csvValues");
				return "";
			}
			return "Creation failed";
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			return "Creation failed";
		}
	}

	@Override
	public Map<String, String> getInitHtml(long stateKey, String formId, boolean renderEmpty, String inputVal, String userLastSaveVal, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) {
		Map<String, String> html = new HashMap<String, String>();
		valueFound = false;
		String lastvalue = " lastvalue=\"" + inputVal + "\" ";
		//String multipleSelectedValuesAttr = " multipleSelectedValues=\"" + multipleSelectedValues + "\" ";
		width = (width.equals("")) ? "300px" : (width.indexOf("%") != -1) || (width.indexOf("px") != -1) ? width : width+"px";
		List<String> catalogItemList = new ArrayList<String>();
		if(!renderEmpty) {
			StringBuilder info = new StringBuilder();
			if (generalUtil.getNull(csvValues).isEmpty()) {
				catalogItemList = generalUtilFormState.getFormCatalogItemList(stateKey, formCode,
						catalogItem.split("\\.")[0], catalogItem.split("\\.")[1], impCode, info);
				if (info != null) {
					html.put(domId + "_elementSQLInfo", info.toString());
				}
			}else{
				catalogItemList = Arrays.asList(csvValues.split(","));
			}
		} 	
		html.put(layoutBookMark, isLabel(isHidden) +
				 		"<select id=\"" + domId + "\" valueType=\"text\" " + inputAttribute + " " + lastvalue +" saveValueAsJSON=\""+saveAsJSON+"\" style=\"" + getHidden(isHidden) + "width:" + width + ";\" data-placeholder=\""
						+ placeHolder + "\" class=\"chosen-select\" " + getAttributes(isDisabled, isMandatory, isHidden) + " element=\"" + this.getClass().getSimpleName() + "\" "
						+ ">" + getCatalogItemAsOption(stateKey, catalogItemList, inputVal, saveAsJSON) + " </select>" + isLabelEnd());
		html.put(layoutBookMark + "_ready",
				"$('[id=\"" + domId + "\"]').chosen({allow_single_deselect:"+ ((isMandatory || removeDeselectOption)?"false":"true") + " ,search_contains:true, width: '" + width + "'}).on('change', function(e){"
//						+ cleanOtherSelectionWhenAllSelected(domId)
						+ ((isMultiple) ? " handleMultipleSelection('"+multipleSelectedValues+"',"+FirstOption.equals("ALL")+",this," + ADD_ALL_ON_EMPTY_DATA + "); \n" : "") 
						+ doOnChangeJSCall + "});" + ((isHidden) ? " $('[id=\"" + domId + "_chosen\"]').css('visibility','hidden');\n": "\n")
						//+	"$('.chosen-drop active-result').click(function(){setMultipleSelectedValues(\""+multipleSelectedValues+"\",this,this)});"
					
);
	
		return html;
	}	

//	private String cleanOtherSelectionWhenAllSelected(String domId) {
//		String toReturn = "";
//		if(isMultiple && FirstOption.equals("ALL")) {
//			toReturn = "\n if($('[id=\"" + domId + "\"]').find('option:nth-child(1)').prop('selected') ==  true)\r\n" + 
//					"                    {\r\n" + 
//					"                        $('[id=\"" + domId + "\"]').val('').find('option:nth-child(1)').prop('selected', true).end().trigger('chosen:updated');\r\n" + 
//					"                    };";
//		} 
//		return toReturn;
//	}

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String inputVal, String domId, String inputAttribute, String doOnChangeJSCall,
			boolean isHidden, boolean isDisabled, boolean isMandatory) { // TODO return js eval see demo jsp
		String htmlBody;
		valueFound = false;
		 
		List<String> catalogItemList = new ArrayList<String>();
		String elementSQLInfo = "";
		if(!renderEmpty) {
			StringBuilder info = new StringBuilder();
			if(generalUtil.getNull(csvValues).isEmpty()){
			catalogItemList = generalUtilFormState.getFormCatalogItemList(stateKey, formCode, catalogItem.split("\\.")[0], catalogItem.split("\\.")[1], impCode, info);
			if(info != null) {
				elementSQLInfo = info.toString();
			}
			}
			else{
				catalogItemList = Arrays.asList(csvValues.split(","));
			}
		} 
		/*htmlBody = "upDateElement({'isHidden':'" + isHidden + "','isDisabled':'" + isDisabled + "','isMandatory':'"
				+ isMandatory + "','val':'" + getCatalogItemAsOption(catalogItemList, inputVal, saveAsJSON) + "','domId':'"
				+ domId + "','type':'chosen'});";*/
		
		JSONObject fullObj = new JSONObject();
		fullObj.put("isHidden", ""+isHidden+"").put("isDisabled", ""+isDisabled+"").put("isMandatory", "" + isMandatory + "").
				put("val", "<option></option>" + getCatalogItemAsOption(stateKey, catalogItemList, inputVal, saveAsJSON) + "").
				put("domId", domId).put("type", "chosen").put("sqlInfo", elementSQLInfo);

		htmlBody = "upDateElement("+fullObj.toString()+");";			
		return htmlBody;
	}
	
	private String getCatalogItemAsOption(long stateKey, List<String> data, String value, boolean isArray) {
		String toReturn = "", selected;		
		StringBuilder sb = new StringBuilder();
		String[] vList = {};
		if(!value.equals(""))
		{
			if(isArray)
			{
				try 
				{
					JSONArray arr = new JSONArray(value);
					vList = new String[arr.length()];
					for(int i=0;i<arr.length();i++)
					{
						vList[i] = arr.optString(i);
					}
				} 
				catch (JSONException e) 
				{
					vList = value.split(",");
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else
			{
				vList = value.split(",");
			}
		}
		for(int i = 0; i < data.size(); i++)
		{
			String currData = data.get(i);
			selected = "";
			if(selectFirstValue && (value.equals("") || value.equals(FIRST_VALUE_FLAG))  && i==0)
			{
				vList = new String[]{currData};
				generalUtilFormState.setFormValue(stateKey, formCode, getImpCode(),currData);
			}
			for (String v : vList) 
			{
				if (generalUtil.getNull(v).equals(currData)) {
					selected = " selected=\"selected\" ";
					valueFound = true;
					break;
				}
			}
			//ab 18032019: fix display issue of sign '<'(less than)
			currData = generalUtil.getNull(currData).replace("<", "&lt;");
			sb.append("<option  value=\"" + currData + "\" " + selected + ">" + currData + "</option>");
		}
		
		if (FirstOption.equals("ALL")) { // =ALL
			if (REMOVE_AND_SAVE_ALL && data.size() > 0 && value.equalsIgnoreCase("all")) {
				toReturn = "<option " + " selected=\"selected\" " + " value=\"ALL\">All</option>" + sb.toString();
				valueFound = true;
			}
			else if (ADD_ALL_ON_EMPTY_DATA || data.size() > 0) {
				toReturn = "<option " + ((!valueFound && SELECT_ALL_ON_EMPTY_DATA)?" selected=\"selected\" ":"") + " value=\"ALL\">All</option>" + sb.toString();
				valueFound = valueFound || SELECT_ALL_ON_EMPTY_DATA;
			}
		} else {
			toReturn = "<option></option>" + sb.toString();
		}
		
		if(!valueFound) {
			generalUtilFormState.setFormValue(stateKey, formCode, getImpCode(),"");
		}

		return toReturn;
	}

	private String getAttributes(boolean isDisabled, boolean isMandatory, boolean isHidden) {
		StringBuilder sb = new StringBuilder();
		if (isDisabled) {
			sb.append("disabled ");
		}
		if (isMandatory) {
			sb.append("required ");
		}		
		if(isMultiple) {
			sb.append("multiple ");
		}
		return sb.toString();
	}
	
	private String getHidden( boolean isHidden){
		if (isHidden){
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
				"      title:'First Option (if ALL then the defualt value will be ignored (ALL will be the default to continue the data flow))',\r\n" + 
				"      'enum':['','ALL']\r\n" + 
				"   },\r\n" + 
				"	selectFirstValue:{  \r\n" + 
				"		type: 'boolean',\r\n" +  
				"		title:'Select First Value'\r\n" + 
				"   },\n" + 
				"	catalogItem:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Catalog Column',\r\n" + 
				"      'enum':getResourceValueByType('CATALOG_ITEM_TEXT')\r\n" + 
				"   },\r\n" + 
				"	saveAsJSON:{\r\n" + 
				" 		type: 'boolean',\r\n" + 
				" 		title: 'Save value as JSON array(by default save as csv)'\r\n" + 
				"   },\r\n" +
				"	multiple:{  \n" + 
				"		type:'string',\n" + 
				"		title:'Multiple',\n" + 
				"		'enum':['FALSE','TRUE']\n" + 
				"   },\r\n" +
				"	multipleSelectedValues:{  \n" + 
				"		type:'string',\n" + 
				"		title:'Multiple Selected Values',\n" + 
				"   },\r\n" + 
				"	csvValues:{  \n" + 
				"		      type:'string',\n" + 
				"		      title:'Values CSV (if empty will be taken from catalog item)',\n" +
				"   },\r\n" + 
				"   removeDeselectOption:{  \r\n" + 
				"		type:'boolean',\r\n" + 
				"		title:'remove deselect option - default false if not mandatory'\r\n" + 
				"   }\r\n" + 
				(schema.equals("") ? "" : ",\n" + schema) +
				"		}";
		return schema;
	}

	@Override
	public Map<String, String> getCatalogItemFilterMapByInputVal(String inputVal, boolean isCurrentElementChange) {
		Map<String, String> filterMap = new HashMap<String, String>();
		if((generalUtil.getNull(inputVal).trim().equals("") || (!valueFound && !isCurrentElementChange))&& generalUtil.getNull(csvValues).isEmpty()) {
			filterMap.put(impCode + "." + generalUtil.getEmpty(getFilterCatalogColumn(), catalogItem.split("\\.")[1]), "");
		} else if(generalUtil.getNull(csvValues).isEmpty()) {
			filterMap.put(impCode + "." + generalUtil.getEmpty(getFilterCatalogColumn(), catalogItem.split("\\.")[1]), "'" + inputVal.replace(",", "','") + "'");
		}		
		return filterMap;
	}
	
	@Override
	public Map<String, String> getCurrentParameMapByInputVal(String inputVal) {
		Map<String, String> mIdVal = new HashMap<String,String>();
		mIdVal.put("CURRENT_" + impCode, inputVal);
		return  mIdVal;
	}
	
	@Override
	public String getDefaultValue(long stateKey, String formId, String formCode) {
		String dv_ = super.getDefaultValue(stateKey, formId, formCode); //TODO version 9.6 find solution to parameter (that are always with the from upper comma CSV)
		if(selectFirstValue) {
			dv_ = FIRST_VALUE_FLAG; // workaround in order to have data flow in case FIRST_VALUE_FLAG -> WE WILL GET IT BACK IN getHtmlBody and ignore it
		} 
		else 
			if(dv_.startsWith("'") && dv_.endsWith("'")) {
			dv_ = dv_.substring(1, dv_.length() - 1);
		}
			
		if(dv_.isEmpty() && (FirstOption.equals("ALL") && SELECT_ALL_ON_EMPTY_DATA)) {
			dv_ = "ALL";
		}
		return dv_; 
	}
	
	@Override
	public ElementInfoAuditTrailDisplay getAuditTrailValue(String formCode, String key, String postSaveValue, String originValue, String uiDisplayValue)
	{
		ElementInfoAuditTrailDisplay elementValueJobFlag = null;
		String uiDisplayValueUpdated = "";
		 
		try {
			if(postSaveValue.equals(originValue))
			{
				elementValueJobFlag = new ElementInfoAuditTrailDisplay(uiDisplayValue, "0");
			}
			else
			{
				uiDisplayValueUpdated = postSaveValue;
				elementValueJobFlag = new ElementInfoAuditTrailDisplay(uiDisplayValueUpdated, "1");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			elementValueJobFlag = new ElementInfoAuditTrailDisplay("", "1");
		}
		 
		return elementValueJobFlag;
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
