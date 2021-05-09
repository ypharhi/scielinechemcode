package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.skyline.form.bean.ElementInfoAuditTrailDisplay;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.entity.Element;
import com.skyline.form.service.GeneralUtilFormState;

/**
 * ElementDynamicParamsImp: used as dynamic inputs and UOM 
 * 
 * in the formBuilder: 
 * declare a catalog for the UOM of the dynamic inputs,
 * declare a catalog for the UOM of the dynamic TimePoint field,
 * 
 * readonlyScript (declared in the schema) is used on the labels and the buttons(plus and minus) only.
 * 
 * use FG_DYNAMICPARAMS to hold last label selection (The parentId in this table represent the formId of the calling form)
 * 
 * in order to display the JSON in the Data Table use "_SMARTDYNPARAM" as column name standards
 */
public class ElementDynamicParamsImp extends Element {
	
	@Autowired
	private GeneralDao generalDao;
	
	@Autowired
	private GeneralUtilFormState generalUtilFormState;

	private String catalogUomItem, catalogTimePointUomItem, readonlyScript, placeholderLabel, precision, timeUomDefault, uomDefault;

	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try {
			if (super.init(stateKey, formCode, impCode, initVal).equals("")) {
				catalogUomItem = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "catalogUomItem");				
				catalogTimePointUomItem = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "catalogTimePointUomItem");
				readonlyScript = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "readonlyScript");
				placeholderLabel = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "placeholderLabel");
				precision = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "precision");
				uomDefault = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "uomDefault");
				timeUomDefault = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "timeUomDefault");
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
		html.put(layoutBookMark, buildDynamicParams(stateKey, domId, value, inputAttribute, isHidden, isDisabled));
		return html;
	}

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) {
		String htmlBody = "";
		if (renderEmpty) {
			value = "";
		}
		boolean isReadonly = generalUtil.getNullBoolean(readonlyScript, false, impCode);
		htmlBody = "upDateElement({'isHidden':'" + isHidden + "','val':'','domId':'" + domId + "','type':'dynamicParams','isDisabled':'" + isDisabled + "', 'isReadonly':'" + ((isReadonly) ? "1" : "0") + "'}});";
		return htmlBody;
	}
	
	/**
	 * Build DynamicParams
	 * @param domId
	 * @param lastSaveValue
	 * @param inputAttribute
	 * @param isHidden
	 * @return
	 */
	private String buildDynamicParams(long stateKey, String domId, String lastSaveValue,
			String inputAttribute, boolean isHidden, boolean isDisabled) {
		StringBuilder sb = new StringBuilder();
		if (catalogUomItem.equals("") || catalogTimePointUomItem.equals("")){
			//if UOM's catalog was not declared
			return "";
		}
		
		boolean isReadonly = generalUtil.getNullBoolean(readonlyScript, false, impCode);
		String displayPlusButton = ((isDisabled) || (isReadonly)) ? "style=\"display:none;\"" :"";
		
		String catalogUomItemData = generalUtilFormState.getFormCatalogItem(stateKey, formCode, catalogUomItem.split("\\.")[0],
				catalogUomItem.split("\\.")[1], impCode, null);
		String catalogTimePointUomItemData = generalUtilFormState.getFormCatalogItem(stateKey, formCode, catalogTimePointUomItem.split("\\.")[0],
				catalogTimePointUomItem.split("\\.")[1], impCode, null);
		
		sb.append("<div id=\"" + domId + "_parent\">");
			//dynamic fields
			sb.append("<div id=\"" + domId + "\" element=\"" + this.getClass().getSimpleName() + "\" " + inputAttribute + " type=\"DynamicParams\" placeholderlabel=\"" + placeholderLabel + "\" precision=\"" + precision + "\">");
				sb.append(getLastSavedValue(stateKey, lastSaveValue, catalogUomItemData, catalogTimePointUomItemData, isDisabled, isReadonly));
			sb.append("</div>");
			//extras
			sb.append( "<div class=\"ml10 mt10\">\n" +
						   "<a class=\"btn\" onclick=\"elementDynamicParamsImpAppendRow('" + domId + "')\" " + displayPlusButton + ">\n" +
						   		"<i class=\"icon-plus-sign\" title=\"Add a new row\"></i>\n" +
						   "</a>\n" + 
					  "</div>");
			sb.append("<input type=\"hidden\" id=\"" + domId + "_uom\" value='[" + catalogUomItemData + "]'>");
		sb.append("</div>");
		return sb.toString();
	}

	/**
	 * get Last Saved Value
	 * 
	 *  **notice that 'font-size: 0' fix the browser white-space bug**
	 * 
	 * @param lastSaveValue
	 * @param catalogUomItemData
	 * @param catalogTimePointUomItemData
	 * @param isDisabled
	 * @param isReadonly
	 * @return
	 */
	private String getLastSavedValue(long stateKey, String lastSaveValue, String catalogUomItemData,
			String catalogTimePointUomItemData, boolean isDisabled, boolean isReadonly) {
		String label, active, val = "", uom = "";
		StringBuilder sb = new StringBuilder();
		JSONObject row, lastSaveValueJSONObject;
		String parentId = generalUtilFormState.getFormParam(stateKey, formCode, "$P{PARENT_ID}");
		boolean lastSaveValueFlag = (lastSaveValue.equals("")) ? false : true;
		
		lastSaveValueJSONObject = (lastSaveValueFlag) ? new JSONObject(lastSaveValue) : new JSONObject();
		
		List<Map<String, Object>> lastDynamicParamsListOfMaps = generalDao
				.getListOfMapsBySql("select * from FG_DYNAMICPARAMS t where PARENT_ID = '" + parentId + "' order by ORDER_");
		
		//append TimePoint
		if (lastSaveValueJSONObject.has("0")) {
			if (lastSaveValueJSONObject.get("0") instanceof JSONObject) {
				row = (JSONObject) lastSaveValueJSONObject.get("0");
				val = row.getString("val");
				uom = row.getString("uom");
			}
		}
		sb.append(appendDynamicParamsRow("Time Point", val, "1",  uom, catalogTimePointUomItemData, isDisabled, true));

		for (int i = 1; i < lastDynamicParamsListOfMaps.size(); i++) {
			active = lastDynamicParamsListOfMaps.get(i).get("ACTIVE").toString();
			label = (active.equals("0")) ? "" : lastDynamicParamsListOfMaps.get(i).get("LABEL").toString();
			if (lastSaveValueFlag && !active.equals("0")) {
				if (lastSaveValueJSONObject.has(String.valueOf(i))) {
					if (lastSaveValueJSONObject.get(String.valueOf(i)) instanceof JSONObject) {
						row = (JSONObject) lastSaveValueJSONObject.get(String.valueOf(i));
						val = row.getString("val");
						uom = row.getString("uom");
					}
				}
			}
			else{
				val = "";
				uom = "";
			}
			sb.append(appendDynamicParamsRow(label, val, active,  uom, catalogUomItemData, isDisabled, isReadonly));
		}
		return sb.toString();
	}

	/**
	 * Append Dynamic Params Row
	 * 
	 * @param label
	 * @param val
	 * @param active
	 * @param uom
	 * @param catalogUomItemData
	 * @param isDisabled
	 * @param isReadonly
	 * @return
	 */
	private String appendDynamicParamsRow(String label, String val, String active, String uom,
			String catalogUomItemData, boolean isDisabled, boolean isReadonly) {
		StringBuilder sb = new StringBuilder();
		String readonly = "", htmlClass = "", display = "", activeAttribute = "", displayMinusButton = "", disabledclass = "", realvalue = val;
		boolean isTimePoint = false;		
		if(isDisabled){
			displayMinusButton = "style=\"display:none;\"";
			readonly = "readonly";
			disabledclass = "disabledclass";
		}
		else if (isReadonly){
			readonly = "readonly";
			displayMinusButton = "style=\"display:none;\"";
		}
		if (label.equals("Time Point")) {
			htmlClass = "DynamicParamsTimePoint";
			isTimePoint = true;
			readonly = "readonly";
		} else if (active.equals("0")) {
			activeAttribute = "active=\"0\"";
			display = "display:none;";
		}
		
		if(!val.equals("") && !precision.equals("")){
			try {
				int integerPlaces = val.indexOf('.');
				int decimalPlaces = val.length() - integerPlaces - 1;
				if (decimalPlaces > Integer.valueOf(precision)){
					val = String.format("%." + precision + "f", Double.valueOf(val));
				}
			} catch (Exception e) { 
				//display val on invalid data
			}
		}
		
		
		sb.append("<div class=\"elementDynamicParamsRow " + htmlClass + "\" style=\"font-size: 0;" + display + "\" "
				+ activeAttribute + ">\n");
		sb.append("<input type=\"text\" class=\"inputAsLabel ml10\" placeholder=\"" + placeholderLabel + "\" value=\"" + label + "\" " + readonly + ">\n");
		sb.append("<input type=\"number\" class=\"ml10 " + disabledclass + "\" value=\"" + val + "\" realvalue=\"" + realvalue + "\">\n");
		sb.append("<select class=\"ml10 " + disabledclass + "\" onchange=\"onElementDynamicParamsImpUomChange(this)\">" + getUomOptions(catalogUomItemData, uom, isTimePoint) + "</select>\n");
		if (!isTimePoint) {
			sb.append("<a class=\"btn ml10\" " + displayMinusButton + ">\n");//btnDelete
			sb.append(
					"<i class=\"icon-minus-sign\" title=\"Delete\" onclick=\"elementDynamicParamsImpDeleteRow(this)\"></i>\n");
			sb.append("</a>\n");
		}
		sb.append("</div>\n");
		return sb.toString();
	}

	/**
	 * Get UOM Options
	 * 
	 * @param data
	 * @return
	 */
	private String getUomOptions(String data, String lastSavedValue, boolean isTimePoint) {
		String selected, id, value;
		JSONArray catalogJSONArray;
		StringBuilder sb = new StringBuilder();
		
		if (!generalUtil.getEmpty(data, "").equals("")) {
			catalogJSONArray = new JSONArray("[" + data + "]");
		} else {
			return "";
		}
		
		for (int i = 0; i < catalogJSONArray.length(); i++) {
			if (!catalogJSONArray.get(i).toString().equals("null")) {
				id = catalogJSONArray.getJSONObject(i).getString("ID");
				value = catalogJSONArray.getJSONObject(i).getString("VAL");
				selected = "";
				if(isTimePoint) { 
					selected = (!lastSavedValue.equals("") && id.equals(lastSavedValue) || (lastSavedValue.equals("") && generalUtil.getNull(value).equals(timeUomDefault))) ? "selected" : "";
				} else {
					selected = (!lastSavedValue.equals("") && id.equals(lastSavedValue) || (lastSavedValue.equals("") && generalUtil.getNull(value).equals(uomDefault))) ? "selected" : "";
				}
				sb.append("<option value=\"" + id + "\" " + selected + ">" + value + "</option>");
			}
		}
		return sb.toString();
	}
	
	@Override
	public ElementInfoAuditTrailDisplay getAuditTrailValue(String formCode, String key, String postSaveValue, String originValue, String uiDisplayValue)
	{
		ElementInfoAuditTrailDisplay elementValueJobFlag = null;
		try {
			elementValueJobFlag = new ElementInfoAuditTrailDisplay(jsonToDisplay(postSaveValue), "0");
		} catch (Exception e) {
			elementValueJobFlag = new ElementInfoAuditTrailDisplay("", "1");
		}
		return elementValueJobFlag;
	}
	
	/*
	 * adib 14032019
	 * method get JSONObject and return string like this: name: value [uom], name: value2 [uom]... 1 [cm], 2 [%]... 
	 */
	public String jsonToDisplay(String postSaveValue) {
		JSONObject jsonIn = new JSONObject(postSaveValue);
		Iterator<String> keys = jsonIn.keys();
		String val, uom, paramName;
		StringBuilder result = new StringBuilder();
		boolean first = true;
		while(keys.hasNext()) {
		    String key = keys.next();
		    if (jsonIn.get(key) instanceof JSONObject) {
		    	
		    	JSONObject objForGetData = (JSONObject) jsonIn.get(key);
		    	val = objForGetData.getString("val");
		    	uom = objForGetData.getString("uom");
		    	String uom_name = generalUtilForm.getCurrrentIdSingleStringInfo("UOM",uom,"NAME");
		    	paramName = objForGetData.getString("label");
		    	
		    	if (!generalUtil.getNull(val).equals("")) {
			    	if (first) {
			    		result.append("</br>");
			    		if (!uom.equals("")){
			    			result.append(paramName + ": " + val + " [" + uom_name + "]");
			    		} else {
			    			result.append(paramName + ": " + val);
			    		}
			    	  first = false;
			    	} else
			    	{
			    		if (!uom.equals("")){
			    			result.append(", " + paramName + ": " + val + " [" + uom_name + "]");
			    		} else {
			    			result.append(", " + paramName + ": "  + val);
			    		}
			    	}
		    	}
		    }
		}
		return result.toString();
	}
	
	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema= "schema:{ \n" + 			
				"	width:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Width',\r\n" +
				"	   'default':'100%'\n" + 
				"   },\r\n" + 
				"	placeholderLabel:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Placeholder (for label)',\r\n" +	
				"	   'default':'Enter label'\n" + 
				"   },\r\n" + 
				"	precision:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Precision'\r\n" +				
				"   },\r\n" + 
				"	catalogUomItem:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Catalog Uom Column',\r\n" + 
				"      'enum':getResourceValueByType('OBJIDVAL')\r\n" + 
				"   },\r\n" + 
				"	catalogTimePointUomItem:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Catalog Time Point Uom Column',\r\n" + 
				"      'enum':getResourceValueByType('OBJIDVAL')\r\n" + 
				"   },\r\n" +
				"	timeUomDefault:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Time Uom Default (as string)'\r\n" +				
				"   },\r\n" + 
				"	uomDefault:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Uom Default (as string)'\r\n" +				
				"   },\r\n" +
				"	readonlyScript:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Readonly Script'\r\n" +				
				"   }\r\n" + 	
				(schema.equals("") ? "" : ",\n" + schema) +
				"		}";
		return schema;
	}
	
//	@Override
//	public String getAuditTrailValue(String inputVal) {
//		StringBuilder toReturn = new StringBuilder();
//		if (!inputVal.equals("")) {			
//			JSONObject innerJO,jo = new JSONObject(inputVal);
//			Iterator<?> keys = jo.keys();			
//			
//			if (catalogUomItem.equals("") || catalogTimePointUomItem.equals("")){
//				//if UOM's catalog was not declared
//				return "";
//			}
//			String catalogUomItemData = generalUtilFormState.getFormCatalogItem(stateKey, formCode, catalogUomItem.split("\\.")[0],
//					catalogUomItem.split("\\.")[1], impCode);
//			String catalogTimePointUomItemData = generalUtilFormState.getFormCatalogItem(stateKey, formCode, catalogTimePointUomItem.split("\\.")[0],
//					catalogTimePointUomItem.split("\\.")[1], impCode);
//			
//			
//			JSONArray catalogUOMJSONArray = new JSONArray("[" + catalogUomItemData + "]");
//			JSONArray catalogTimePointUomJSONArray = new JSONArray("[" + catalogTimePointUomItemData + "]");
//			  
//			
//			while(keys.hasNext()) {
//				String key = (String)keys.next();
//			    if (jo.get(key) instanceof JSONObject) {
//			    	innerJO = jo.getJSONObject(key);
//			    	if(innerJO.getString("active").equals("1")){
//			    		toReturn.append(", ");
//			    		toReturn.append(innerJO.getString("label"));
//			    		toReturn.append(": ");
//				    	if(!innerJO.getString("val").equals("")){
//				    		toReturn.append(innerJO.getString("val"));
//				    		if(innerJO.getString("label").equals("Time Point")){
//				    			for (int i = 0; i < catalogTimePointUomJSONArray.length(); i++) {
//				    				if (!catalogTimePointUomJSONArray.get(i).toString().equals("null")) {
//				    					if(catalogTimePointUomJSONArray.getJSONObject(i).getString("ID").equals(innerJO.getString("uom"))){
//				    						toReturn.append(catalogTimePointUomJSONArray.getJSONObject(i).getString("VAL"));
//				    						break;
//				    					}
//				    				}
//				    			}
//				    		}
//				    		else{
//				    			for (int i = 0; i < catalogUOMJSONArray.length(); i++) {
//				    				if (!catalogUOMJSONArray.get(i).toString().equals("null")) {
//				    					if(catalogUOMJSONArray.getJSONObject(i).getString("ID").equals(innerJO.getString("uom"))){
//				    						toReturn.append(catalogUOMJSONArray.getJSONObject(i).getString("VAL"));			    						
//				    						break;
//				    					}
//				    				}
//				    			}
//				    		}
//				    		
//				    	}
//			    	
//			    	}	
//			    }
//			}
//		}
//		return (toReturn.toString().equals("")) ? "" : toReturn.toString().substring(2);
//	}
}
