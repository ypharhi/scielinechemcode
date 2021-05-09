package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.skyline.form.dal.GeneralDao;
import com.skyline.form.entity.Element;
import com.skyline.form.service.GeneralUtilForm;

/**
 * ElementFormTestImp: dynamic elements declared in the maintenance, intended to be used for calculations
 * 
 * 
 * How ElementFormTestImp works:
 * ElementFormTestImp use catalog which contains view with 'OBJFORMTESTCON' column's type (example of view 'FG_S_FORMTESTCONFIG_ALL_V').
 * OBJFORMTESTCON is JSON that contains CSV of UOM_ID, order, TESTEDENTITYNAME (location) and more information.
	 * example of OBJFORMTESTCON: 
		{"FORMTESTCONFIG_ID":"103842","SH":"","WL":"111","WH":"2","SL":"3","ORDER_":"1","DATATYPENAME":"number",
		"FORMTESTNAME":"ph","TESTEDENTITYNAME":"Project.IP","UOM_ID":"2825,2887,5442,5443,103711"}
 * The catalog above has wherePart on TESTEDENTITYNAME (the location of the element)    example: TESTEDENTITYNAME = 'Project.IP'
 * 
 * Last save value come from 'FG_FORMTEST_DATA' (hard coded).
 * 
 * we use fgeneralUtilFormImp.getCurrrentIdInfo to get UOM information
 * 
 * use prevent save in the schema (we do not want to save the JSON)
 *
 */
public class ElementFormTestImp extends Element {
	
	@Autowired
	private GeneralDao generalDao;	
	
	@Autowired
	private GeneralUtilForm generalUtilForm;
	
	private String catalogFormTestConfig;	

	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try {
			if (super.init(stateKey, formCode, impCode, initVal).equals("")) {
				catalogFormTestConfig = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "catalogFormTestConfig");
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
		html.put(layoutBookMark, getFields(stateKey, domId, value, inputAttribute, isHidden));
		return html;
	}

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) {
		String htmlBody = "";
		if (renderEmpty) {
			value = "";
		}	
		htmlBody = "upDateElement({'isHidden':'" + isHidden + "','val':'','domId':'" + domId + "'});";
		return htmlBody;
	}
	
	/**
	 * get HTML Fields
	 * @param domId
	 * @param lastSaveValue
	 * @param inputAttribute
	 * @param isHidden
	 * @return
	 */
	private String getFields(long stateKey, String domId, String lastSaveValue,
			String inputAttribute, boolean isHidden) {
		StringBuilder labelSB = new StringBuilder();
		StringBuilder inputSB = new StringBuilder();
		StringBuilder uomLabelSB = new StringBuilder();
		StringBuilder uomSB = new StringBuilder();
		String hidden = (isHidden) ? "visibility:hidden;" : "";
		JSONArray catalogFormTestConfigItemDataJSONArray;
		JSONObject catalogFormTestConfigItemDataJSONObject;
		String formTestName, dataType, uomCSV, formTestConfigId, lastSavedValue, lastSavedUOM_ID;
		List<Map<String,Object>> lastSavedValues;		
		
		String catalogFormTestConfigItemData = generalUtilFormState.getFormCatalogItem(stateKey, formCode, catalogFormTestConfig.split("\\.")[0],
				catalogFormTestConfig.split("\\.")[1], impCode, null);
		if (!generalUtil.getEmpty(catalogFormTestConfigItemData, "").equals("")) {
			catalogFormTestConfigItemDataJSONArray = new JSONArray("[" + catalogFormTestConfigItemData + "]");
		} else {
			return "";
		}
		
		labelSB.append("<div style=\"float: left;margin-top: 2px;\">");
		inputSB.append("<div style=\"float: left;margin-left:20px;\">");
		uomLabelSB.append("<div style=\"float: left;margin-left:30px;margin-top: 2px;\">");
		uomSB.append("<div style=\"float: left;\">");		
		for (int i = 0; i < catalogFormTestConfigItemDataJSONArray.length(); i++) {		
			catalogFormTestConfigItemDataJSONObject = catalogFormTestConfigItemDataJSONArray.getJSONObject(i);
			formTestName = catalogFormTestConfigItemDataJSONObject.getString("FORMTESTNAME");
			dataType = catalogFormTestConfigItemDataJSONObject.getString("DATATYPENAME").toString();
			uomCSV = catalogFormTestConfigItemDataJSONObject.getString("UOM_ID").toString();
			formTestConfigId = catalogFormTestConfigItemDataJSONObject.getString("FORMTESTCONFIG_ID").toString();
			lastSavedValues = generalDao.getListOfMapsBySql("select * from FG_FORMTEST_DATA t where t.parent_id = '"
					+ generalUtilFormState.getFormId(stateKey, formCode) + "' and t.config_id='" + formTestConfigId + "'");
			lastSavedValue = (lastSavedValues.isEmpty() || lastSavedValues.get(0).get("VALUE") == null) ? ""
					: lastSavedValues.get(0).get("VALUE").toString();
			lastSavedUOM_ID = (lastSavedValues.isEmpty() || lastSavedValues.get(0).get("UOM_ID") == null) ? ""
					: lastSavedValues.get(0).get("UOM_ID").toString();
			labelSB.append(
					"<label class=\"cssStaticData\" style=\"display: block;text-align: left;height: 20px;margin-bottom: 15px;\">"
							+ formTestName + "</label>\n");
			inputSB.append("<input id=\"" + formTestName + "_" + formTestConfigId + "_" + domId + "\" "
					+ " type=\"" + dataType + "\" value=\"" + lastSavedValue + "\" formtestconfig_id=\"" + formTestConfigId + "\""
					+ " style=\"display: block;height: 20px;margin-bottom: 15px;\">\n");
			uomLabelSB.append(
					"<label class=\"cssStaticData\" style=\"display: block;text-align: left;height: 20px;margin-bottom: 15px;\">UOM:</label>\n");
			uomSB.append(
					"<select id=\"" + formTestName + "_" + formTestConfigId + "_" + domId + "_uom"
							+ "\" style=\"display: block;height: 20px;margin-bottom: 15px;width: 50px;\">"
							+ getUomOptions(uomCSV, lastSavedUOM_ID)
							+ "</select>\n");
		}

		labelSB.append("</div>\n");
		inputSB.append("</div>\n");
		uomLabelSB.append("</div>\n");
		uomSB.append("</div>\n");

		return "<div id=\"" + domId + "\" style=\"white-space: nowrap;" + hidden + "\" " + inputAttribute + " element=\""
				+ this.getClass().getSimpleName() + "\" type=\"FORMTESTCONFIG\">" + labelSB.toString() + inputSB.toString()
				+ uomLabelSB.toString() + uomSB.toString() + "</div>";
	}

	
	/**
	 * get Uom Options
	 * @param uomCsv
	 * @param lastSaveUomValue
	 * @return
	 */
	private String getUomOptions(String uomCsv, String lastSaveUomValue) {
		StringBuilder sb = new StringBuilder();
		String selected = "";
		String[] uomArray = uomCsv.split(",");
		Map<String, String> lookupMap;
		for (int i = 0; i < uomArray.length; i++) {		
			lookupMap = generalUtilForm.getCurrrentIdInfo(uomArray[i]);
			selected = lastSaveUomValue.equals(uomArray[i]) ? "selected" : "";
			sb.append("<option value=\"" + uomArray[i] + "\" " + selected + " isnormal=\"" + lookupMap.get("NAME")
					+ "\" factor=\"" + lookupMap.get("FACTOR") + "\" precision=\"" + lookupMap.get("PRECISION") + "\">"
					+ lookupMap.get("NAME") + "</option>");
		}
		return sb.toString();
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
				"	catalogFormTestConfig:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Catalog Form Test Config',\r\n" + 
				"      'enum':getResourceValueByType('OBJFORMTESTCONF')\r\n" + 
				"   }\r\n" + 				
				(schema.equals("") ? "" : ",\n" + schema) +
				"		}";
		return schema;
	}
}
