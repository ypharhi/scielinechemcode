package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.skyline.form.bean.ElementInfoAuditTrailDisplay;
import com.skyline.form.entity.Element;

/**
 * ElementParamMonitoringImp: used for dynamic inputs and UOM, can be declared in the maintenance(MP form).
 * 
 * the ElementParamMonitoringImp saves all data in JSON
 * 
 * the catalog 'catalogObjParamsItem' filtered by param monitoring type (used as table type)
 * 
 * in order to display the JSON in the Data Table use "_MONPARAM" as column name
 * 
 * marginLeftInput
 *
 * widthLabel
 */
public class ElementParamMonitoringImp extends Element {

	private String catalogUomItem, catalogObjParamsItem, marginLeftInput, widthLabel;
	 
	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try {
			if (super.init(stateKey, formCode, impCode, initVal).equals("")) {
				catalogUomItem = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "catalogUomItem");
				catalogObjParamsItem = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "catalogObjParamsItem");
				marginLeftInput = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "marginLeftInput");
				widthLabel = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "widthLabel");
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
		html.put(layoutBookMark, getFieldsFromJsonArray(stateKey, domId, value, inputAttribute, isHidden));
		return html;
	}

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) { // TODO return js eval see demo jsp
		String htmlBody = "";
		if (renderEmpty) {
			value = "";
		}
		htmlBody = "upDateElement({'isHidden':'" + isHidden + "','val':'','domId':'" + domId
				+ "','type':'ParamMonitoring'});";
		return htmlBody;
	}

	/**
	 * HTML Fields From JsonArray
	 * 
	 * @param objParams
	 * @param domId
	 * @param lastSaveValue
	 * @return Html Fields
	 */
	private String getFieldsFromJsonArray(long stateKey, String domId, String lastSaveValue, String inputAttribute, boolean isHidden) {

		String maintenanceObjParams = generalUtilFormState.getFormCatalogItem(stateKey, formCode,
				catalogObjParamsItem.split("\\.")[0], catalogObjParamsItem.split("\\.")[1], impCode, null);
		String hidden = (isHidden) ? "visibility:hidden;" : "";
		JSONObject lastSaveValueJSONObject = (lastSaveValue.equals("")) ? null : new JSONObject(lastSaveValue);
		JSONArray catalogUomItemDataJSONArray,
				maintenanceObjParamsJSONArray = new JSONArray("[" + maintenanceObjParams + "]");
		StringBuilder labelSB = new StringBuilder();
		StringBuilder inputSB = new StringBuilder();
		StringBuilder uomLabelSB = new StringBuilder();
		StringBuilder uomSB = new StringBuilder();
		JSONObject maintenanceObjParamsJSONObject, maintenanceObjParamValueJSONObject;
		String maintenanceObjParamName, maintenanceObjParamValue, maintenanceObjFormId, maintenanceObjPrecision, maintenanceObjDefaultUom;		
		String realValue, lastUOMValue, lastSaveUomValue = "";
		String catalogUomItemData = generalUtilFormState.getFormCatalogItem(stateKey, formCode, catalogUomItem.split("\\.")[0],
				catalogUomItem.split("\\.")[1], impCode, null);
		if (!generalUtil.getEmpty(catalogUomItemData, "").equals("")) {
			catalogUomItemDataJSONArray = new JSONArray("[" + catalogUomItemData + "]");
		} else{
			return "";
		}
		marginLeftInput=(generalUtil.getNull(marginLeftInput).isEmpty()||!marginLeftInput.contains("px"))?"20px":marginLeftInput;
		widthLabel=(generalUtil.getNull(widthLabel).isEmpty()||(!widthLabel.contains("px")&&!widthLabel.contains("%")))?"":widthLabel;
		
		labelSB.append("<div style=\"float: left;width:"+widthLabel+"\">");
		inputSB.append("<div style=\"float: left;margin-left:"+marginLeftInput+";\">");
		uomLabelSB.append("<div style=\"float: left;margin-left:30px;\">");
		uomSB.append("<div style=\"float: left;margin-left:20px;\">");

		for (int i = 0; i < maintenanceObjParamsJSONArray.length(); i++) {
			maintenanceObjParamsJSONObject = maintenanceObjParamsJSONArray.getJSONObject(i);
			maintenanceObjParamName = maintenanceObjParamsJSONObject.getString("name");
			maintenanceObjFormId = maintenanceObjParamsJSONObject.getString("formId");
			maintenanceObjPrecision = maintenanceObjParamsJSONObject.getString("precision");
			maintenanceObjDefaultUom = maintenanceObjParamsJSONObject.optString("default_uom", "");//("default_uom");
			if ((lastSaveValueJSONObject != null) && (lastSaveValueJSONObject.has(maintenanceObjParamName))) {
				maintenanceObjParamValue = lastSaveValueJSONObject.getString(maintenanceObjParamName);
				maintenanceObjParamValueJSONObject = new JSONObject(maintenanceObjParamValue);
				maintenanceObjParamValue = maintenanceObjParamValueJSONObject.getString("val");
			} else {
				maintenanceObjParamValue = "";
			}
			realValue = maintenanceObjParamValue;
			try {
				if (!maintenanceObjParamValue.equals("") && !maintenanceObjPrecision.equals("")) {
					int integerPlaces = maintenanceObjParamValue.indexOf('.');
					int decimalPlaces = maintenanceObjParamValue.length() - integerPlaces - 1;
					if (decimalPlaces > Integer.valueOf(maintenanceObjPrecision)) {
						maintenanceObjParamValue = String.format("%." + maintenanceObjPrecision + "f",
								Double.valueOf(maintenanceObjParamValue));
					}
				}
			} catch (Exception e) {

			}
			
			if ((lastSaveValueJSONObject != null) && (lastSaveValueJSONObject.has(maintenanceObjParamName + "_uom"))) {
				lastSaveUomValue = new JSONObject(lastSaveValueJSONObject.getString(maintenanceObjParamName + "_uom"))
						.getString("val");
			}
			lastUOMValue = " lastuomvalue=\"" + lastSaveUomValue + "\" ";			
			
			String labeldemo = "<label class=\"asterisk\" style='visibility:hidden'>*</label>";//"<label class=\"asterisk\" for=\"" + elementName + "\" style='visibility:"+(isMandatory?"visible":"hidden")+";'>*</label>";
			labelSB.append("<div>"+
					labeldemo+"<label class=\"cssStaticData\" style=\"display: block;text-align: left;margin-bottom: 12px;padding-top: 3px;\">"
							+ maintenanceObjParamName + "</label>\n</div>\n");
			inputSB.append("<input id=\"" + maintenanceObjParamName + "_" + domId + "\" type=\"number\" realvalue=\""
					+ realValue + "\" precision=\"" + maintenanceObjPrecision + "\" value=\"" + maintenanceObjParamValue
					+ "\" " + " style=\"display: block;margin-bottom: 12px;padding-top: 3px;\" formId=\""
					+ maintenanceObjFormId + "\" onkeypress=\"if(event.keyCode == 46 || event.keyCode== 101 ||(event.keyCode > 47 && event.keyCode < 58)){ }else return false;\">\n");
			uomLabelSB.append(
					"<label class=\"cssStaticData\" style=\"display: block;text-align: left;margin-bottom: 12px;padding-top: 3px;\">UOM:</label>\n");
			uomSB.append(
					"<select ElementUOM elementId=\"" + maintenanceObjParamName + "_" + domId + "\" id=\"" + maintenanceObjParamName + "_uom_" + domId
							+ "\" style=\"display: block;margin-bottom: 12px;padding-top: 3px;\" " + lastUOMValue + ">"
							+ getUomOptions(maintenanceObjParamsJSONObject.getString("uom"),
									catalogUomItemDataJSONArray, lastSaveValueJSONObject, maintenanceObjParamName, maintenanceObjDefaultUom)
							+ "</select>\n");
		}

		labelSB.append("</div>\n");
		inputSB.append("</div>\n");
		uomLabelSB.append("</div>\n");
		uomSB.append("</div>\n");

		return "<div id=\"" + domId + "\" style=\"white-space: nowrap;" + hidden + "\" " + inputAttribute
				+ " element=\"" + this.getClass().getSimpleName() + "\" type=\"paramMonitoring\">" + labelSB.toString()
				+ inputSB.toString() + uomLabelSB.toString() + uomSB.toString() + "</div>";
	}

	/**
	 *
	 * @param objParamsUomCsv
	 * @param catalogItemDataJSONArray
	 * @param lastSaveValueJSONObject
	 * @param maintenanceObjParamName
	 * @return UOM Options
	 */
	private String getUomOptions(String objParamsUomCsv, JSONArray catalogUomItemDataJSONArray,
			JSONObject lastSaveValueJSONObject, String maintenanceObjParamName, String maintenanceObjDefaultUom) {
		StringBuilder sb = new StringBuilder();
		Map<String ,String> uomInfoMap;
		String id_ = "";
		String val_ = "";
		String[] objParamsUomArray = objParamsUomCsv.split(",");
		String lastSaveUomValue = "", factor, precision, isNormal;
		if ((lastSaveValueJSONObject != null) && (lastSaveValueJSONObject.has(maintenanceObjParamName + "_uom"))) {
			lastSaveUomValue = new JSONObject(lastSaveValueJSONObject.getString(maintenanceObjParamName + "_uom"))
					.getString("val");
		}
		for (int i = 0; i < objParamsUomArray.length; i++) {
			for (int j = 0; j < catalogUomItemDataJSONArray.length(); j++) {
				if (!catalogUomItemDataJSONArray.get(j).toString().equals("null")) {
					id_ = catalogUomItemDataJSONArray.getJSONObject(j).getString("ID");
					if (objParamsUomArray[i].equals(id_)) {
						val_ = catalogUomItemDataJSONArray.getJSONObject(j).getString("VAL");					
						uomInfoMap = generalUtilForm.getCurrrentIdInfo(id_);
						factor = uomInfoMap.get("FACTOR");
						precision = uomInfoMap.get("PRECISION");
						isNormal = uomInfoMap.get("ISNORMAL");
						String selected = "";
						if(!lastSaveUomValue.isEmpty()) {
							selected = (lastSaveUomValue.equals(id_)) ? "selected" : "";
						} else if(!maintenanceObjDefaultUom.isEmpty()) {
							selected = (maintenanceObjDefaultUom.equals(id_)? "selected" : "");
						} else if(isNormal != null && !isNormal.isEmpty() && isNormal.equals("1")) {
							selected = "selected";
						}  
						//selected = (lastSaveUomValue.equals(id_)) ? "selected" : (maintenanceObjDefaultUom.equals(id_)? "selected" : "");
						sb.append("<option value=\"" + id_ + "\" " + selected + " factor=\"" + factor + "\" isnormal=\""
								+ isNormal + "\" precision=\"" + precision + "\">" + val_ + "</option>");					
						break;
					}
				}
			}
		}
		return sb.toString();
	}

	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema = "schema:{ \n" + "	width:{  \r\n" + "      type:'string',\r\n" + "      title:'Width',\r\n"
				+ "	   'default':'100%'\n" + "   },\r\n" 
				+ "	marginLeftInput:{  \r\n" + "      type:'string',\r\n" + "      title:'Margin left input field',\r\n"
				+ "	      },\r\n" 
				+ "	widthLabel:{  \r\n" 
				+ "      type:'string',\r\n" 
				+ "      title:'Label width (in % or px)',\r\n"
				+ "	      },\r\n" 
				+ "	catalogObjParamsItem:{  \r\n"
				+ "      type:'string',\r\n"
				+ "      title:'Catalog ObjParams Column (filtered by param monitoring type)',\r\n"
				+ "      'enum':getResourceValueByType('OBJPARAM')\r\n" + "   },\r\n" + "	catalogUomItem:{  \r\n"
				+ "      type:'string',\r\n" + "      title:'Catalog Uom Column',\r\n"
				+ "      'enum':getResourceValueByType('OBJIDVAL')\r\n" + "   }\r\n"
				+ (schema.equals("") ? "" : ",\n" + schema) + "		}";
		return schema;
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
	 * kd 19022019
	 * method get JSONObject and return string like this: name: value [uom],name: value2 [uom]... 1 [cm], 2 [%]... 
	 */
	private String jsonToDisplay(String postSaveValue) {
		JSONObject jsonIn = new JSONObject(postSaveValue);
		Iterator<String> keys = jsonIn.keys();
		String val, uom;
		StringBuilder result = new StringBuilder();
		boolean first = true;
		while(keys.hasNext()) {
		    String key = keys.next();
		    if (jsonIn.get(key) instanceof JSONObject) {
		    	if (!key.endsWith("_uom"))
		    	{
			    	JSONObject objForGetValue = (JSONObject) jsonIn.get(key);
			    	val = objForGetValue.getString("val");
			    	
			    	JSONObject objForGetUOM = (JSONObject) jsonIn.get(key+"_uom");
			    	uom = objForGetUOM.getString("text");
			    	
			    	if (!generalUtil.getNull(val).equals("")) {
				    	if (first) {
				    		result.append("</br>");
				    		if (!uom.equals("")){
				    			result.append(key + ": " + val + " [" + uom + "]");
				    		} else {
				    			result.append(key + ": "+ val);
				    		}
				    	  first = false;
				    	} else
				    	{
				    		if (!uom.equals("")){
				    			result.append(", " + key + ": " + val + " [" + uom + "]");
				    		} else {
				    			result.append(", " + key + ": " + val);
				    		}
				    	}
			    	}
		    	}
		    }
		}
		return result.toString();
	}
	
	
//	@Override
//	public String getAuditTrailValue(String inputVal) {
//		StringBuilder toReturn = new StringBuilder();
//		if (!inputVal.equals("")) {
//			JSONObject innerJO, uomJO, jo = new JSONObject(inputVal);
//			Iterator<?> keys = jo.keys();
//			String val;
//			while (keys.hasNext()) {
//				String key = (String) keys.next();
//				if (key.indexOf("_uom") == -1) {
//					if (jo.get(key) instanceof JSONObject) {
//						innerJO = jo.getJSONObject(key);
//						toReturn.append(", ");
//						toReturn.append(key);
//						toReturn.append(": ");
//						val = jo.getJSONObject(key).getString("val");
//						if(!val.equals("")){
//							toReturn.append(val);
//							uomJO = jo.getJSONObject(key + "_uom");
//							toReturn.append(uomJO.getString("text"));
//						}
//					}
//				}
//			}
//		}
//		return (toReturn.toString().equals("")) ? "" : toReturn.toString().substring(2);
//	}
}
