package com.skyline.form.entitypool;


import java.util.HashMap;
import java.util.Map;

import com.skyline.form.bean.ElementInfoAuditTrailDisplay;
import com.skyline.form.entity.Element;

/**
 * 
 * Element Radio
 * 
 * the options can decalre in the schema (Values CSV) * 
 * 
 * the catalog is only necessary for the filter (getCatalogItemFilterMapByInputVal)
 *
 */
public class ElementRadioImp extends Element {
	
	private String csvValues, layout, disableCss, catalogItem;	
	
	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try{
			if (super.init(stateKey, formCode, impCode, initVal).equals("")) {
				csvValues = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "csvValues");
				layout = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "layout");
				disableCss = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "disableCss");
				catalogItem = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "catalogItem");
				return "";
			}
			return "Creation failed";
		}
		catch (Exception e) {
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
		String hidden = (isHidden)? "visibility:hidden;":"";
		String disabledClass  = (isDisabled) ? " disabledclass ":"";
		String disabled  = (isDisabled) ? " disabled ":"";
		String[] valuesArray = csvValues.split(",");
		StringBuilder sb = new StringBuilder();			
		String whiteSpace = (layout.equals("Horizontal")) ? "white-space:nowrap;":"";
		String br = (layout.equals("Horizontal")) ? "":"<br>";
		String cssClass = (disableCss.equals("True")) ? "" : "cssStaticData";
		sb.append("<div style=\"" + whiteSpace + hidden + "\"><input type=\"hidden\" id=\"" + domId + "\" "+ inputAttribute 
				+ " element=\"" + this.getClass().getSimpleName() + "\">");
		String checked;
		for(String val : valuesArray){
			if(value.equals(val)) {
				checked = "checked";
			} else {
				checked = "";
			}			
			sb.append("<input type=\"radio\" name=\"" + domId + "\" class=\"" + disabledClass + "\" value=\"" + val + "\" style=\"margin-left: 20px;\" " 
		    + checked + disabled +"><span style=\"vertical-align: top;\" class=\"" + cssClass + "\"> " + val + "</span>" + br + "\n");
		}
		String toReturn = sb.toString();
		if(!(br.equals("")) && (valuesArray != null) && (valuesArray.length != 0)){
			toReturn = sb.delete(sb.lastIndexOf(br), sb.lastIndexOf("\n")).toString();
		}
		html.put(layoutBookMark,toReturn);		
		html.put(layoutBookMark + "_ready","$('#" + domId + "').siblings('input[type=\"radio\"]').on('change', function(){" + doOnChangeJSCall + "});\n");
		return html;
	}

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute, String doOnChangeJSCall,
			boolean isHidden, boolean isDisabled, boolean isMandatory) { 
		String htmlBody ="";
		if (renderEmpty) {			
			value = "";
		}
		htmlBody = "upDateElement({'isHidden':'" + isHidden + "','isDisabled':'" + isDisabled + "'"
				+ ",'val':'','domId':'" + domId + "','type':'radio'});";
		return htmlBody;
	}
 
	@Override
	public Map<String, String> getCatalogItemFilterMapByInputVal(String inputVal, boolean isCurrentElementChange) {
		Map<String, String> filterMap = new HashMap<String, String>();
		if(!generalUtil.getNull(catalogItem).equals("")) {
			if(generalUtil.getNull(inputVal).trim().equals("")) {
//				filterMap.put(catalogItem.split("\\.")[1], "");
				filterMap.put(impCode + "." + generalUtil.getEmpty(getFilterCatalogColumn(), catalogItem.split("\\.")[1]), "");
			} else {
//				filterMap.put(catalogItem.split("\\.")[1], "'" + inputVal.replace(",", "','") + "'");
				filterMap.put(impCode + "." + generalUtil.getEmpty(getFilterCatalogColumn(), catalogItem.split("\\.")[1]), "'" + inputVal.replace(",", "','") + "'");
			}
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
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema= "schema:{ \n" +
				"	layout:{  \r\n" + 			
				"      type:'string',\r\n" + 
				"      title:'Layout',\r\n" +
				"	   'enum':['Horizontal','Vertical'] \n" + 
				"   },\r\n" +
				"	disableCss:{  \r\n" + 			
				"      type:'string',\r\n" + 
				"      title:'Disable CSS',\r\n" +
				"	   'enum':['','True'] \n" + 
				"   },\r\n" +
				"	catalogItem:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Catalog Column',\r\n" + 
				"      'enum':[''].concat(getResourceValueByType('CATALOG_ITEM_TEXT'))\r\n" + 
				"   },\r\n" + 
				"	csvValues:{  \n" + 
				"		      type:'string',\n" + 
				"		      title:'Values CSV',\n" + 
				"		   }\n" + 
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
