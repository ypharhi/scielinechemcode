package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.skyline.form.bean.ElementInfoAuditTrailDisplay;
import com.skyline.form.entity.Element;
import com.skyline.form.service.GeneralUtilForm; 

/** 
 * 
 * ElementUOMImp
 * 
 * Autocomplete list that holds data of uom (holds id, value, factor, isnormal and precision).
 * 
 * Remarks:
 * 1) the data comes from the view of UOM - fg_s_uom_all_v (hard coded), ( declaration of catalog in the formbuilder is not needed).
 * 
 * 2) we call elementUOMImpInit() function at the loading of the page as complementary function of the element.
 * 
 * 3) render empty return list with default value selection (if exists) else normal UOM
 */
public class ElementUOMImp extends Element {
	
	@Autowired
	private GeneralUtilForm generalUtilForm;
	
	private String uomTypeName, width, elementId, elementsIdString;
	
	private boolean valueFound = false;
	
	private String lastCurrentId = ""; // holds last id selection
	
	private String lastCurrentVal = ""; // holds last val (name) selection	 
  
	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {		
		try{
			if(super.init(stateKey, formCode, impCode, initVal).equals(""))
			{					
				width = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "width");
				uomTypeName = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "uomTypeName");
				elementId = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "elementId");
				elementsIdString = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "elementsIdString");
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
		inputVal = (renderEmpty) ? generalUtil.getNull(getDefaultValue(stateKey, formId, formCode)) : inputVal;	
		String lastvalue = " lastvalue=\"" + inputVal + "\" ";
		String lastUOMValue = " lastuomvalue=\"" + inputVal + "\" ";
		width = (width.equals("")) ? "300px" : (width.indexOf("%") != -1) || (width.indexOf("px") != -1) ? width :  width + "px";	
		html.put(layoutBookMark,
				"<select ElementUOM elementId=\"" + elementId + "\" elementsIdString=\"" + elementsIdString + "\" id=\"" + domId + "\" " + inputAttribute + " " + lastvalue + lastUOMValue +" style=\"" + getHidden(isHidden) + "width:" + width + ";\" data-placeholder=\" " 
				+ "\" class=\"chosen-select\" " + getAttributes(isDisabled, isMandatory, isHidden) + " element=\"ElementAutoCompleteDDLImp\" "
				+ ">" + renderOptions(stateKey, formId, formCode, inputVal) + " </select>");
		html.put(layoutBookMark + "_ready",
				"$('[id=\"" + domId + "\"]').chosen({allow_single_deselect:"+ (isMandatory?"false":"true") + ",search_contains:true, width: '" + width + "'}).on('change', function(){"
				+ doOnChangeJSCall + "});"
				+ ((isHidden) ? " $('[id=\"" + domId + "_chosen\"]').css('visibility','hidden');":""));
		return html;
	}	

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String inputVal, String domId, String inputAttribute, String doOnChangeJSCall,
			boolean isHidden, boolean isDisabled, boolean isMandatory) { // TODO return js eval see demo jsp
		String htmlBody;
		 
		lastCurrentId = "";
		lastCurrentVal = "";
		valueFound = false;		
		inputVal = (renderEmpty) ? generalUtil.getNull(getDefaultValue(stateKey, formId, formCode)) : inputVal;
		
		htmlBody = "upDateElement({'isHidden':'" + isHidden + "','isDisabled':'" + isDisabled + "','isMandatory':'"
				+ isMandatory + "','val':'" + renderOptions(stateKey, formId, formCode, inputVal) + "','domId':'"
				+ domId + "','type':'chosen'});";
		return htmlBody;
	}

	private String renderOptions(long stateKey, String formId, String formCode, String lastSavedValueOrDefault) {
		StringBuilder sb = new StringBuilder();	
//		 uomTypeName=("'"+uomTypeName.replace(" ","").replaceAll("^\\'{2}\\'*|\\'{2}\\'*$", "\\'").replaceAll("^\\'$", "''");
		String selected, uomId, value, factor, isNormal, precision;		
//		List<Map<String,Object>> data = generalDao.getListOfMapsBySql("select * from fg_s_uom_all_v t where t.ACTIVE = '1' " + wherePart);		
		
//		create or replace view fg_s_uom_inf_v as
//		select 'UOM' as formCode,
//		        t.formid as id,
//		        t.UOMName as name,
//		        t.ISNORMAL,
//		        t.FACTOR,
//		        t.PRECISION
//		from FG_S_UOM_ALL_V t;
		List<Map<String,String>> data = generalUtilForm.getCurrrentNameInfoAllContainsId("UOM","%");
		
		if(!data.isEmpty()){
			for(int i = 0; i < data.size(); i++){
				String rsUomTypeName_ = (data.get(i).get("UOMTYPENAME") != null) ? data.get(i).get("UOMTYPENAME").toString() : "";
				if(uomTypeName.equals(rsUomTypeName_)) {
					uomId = data.get(i).get("ID").toString();
					value = (data.get(i).get("NAME") != null) ? data.get(i).get("NAME").toString() : "";	
					factor = (data.get(i).get("FACTOR") != null) ? data.get(i).get("FACTOR").toString() : "";
					isNormal = (data.get(i).get("ISNORMAL") != null) ? data.get(i).get("ISNORMAL").toString() : "";
					precision = (data.get(i).get("PRECISION") != null) ? data.get(i).get("PRECISION").toString() : "";
//					if(lastSavedValue.equals(uomId) || (lastSavedValue.equals("") && (isNormal.equals("1")))){ // yp 22022018 add support for default value (as string) ->
					if(lastSavedValueOrDefault.equals(uomId) || (lastSavedValueOrDefault.equals(getDefaultValue(stateKey, formId, formCode)) && lastSavedValueOrDefault.equals(value))  || (lastSavedValueOrDefault.equals("") && (isNormal.equals("1")))){
						selected = "selected=\"selected\"";
						lastCurrentId = uomId;
						lastCurrentVal = value;
						valueFound = true;
					}
					else {
						selected = "";
					}	
					sb.append("<option " + selected + " value=\"" + uomId + "\" factor=\"" + factor
							+ "\" isnormal=\"" + isNormal + "\" precision=\"" + precision + "\" uomtypename=\""+uomTypeName+"\">" + value + "</option>");
				}
			}
		}
		return sb.toString();
	}

	private String getAttributes(boolean isDisabled, boolean isMandatory, boolean isHidden) {
		StringBuilder sb = new StringBuilder();
		if (isDisabled) {
			sb.append("disabled ");
		}
		if (isMandatory) {
			sb.append("required ");
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
				"	width:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Width',\r\n" +
				"	   'default':'100%'\n" +
				"   },\r\n" + 
				" elementId:{  \r\n" + 
				" 		type:'string',\r\n" + 
				" 		title:'Element ID',\r\n" + 
				" 		'enum':[''].concat(getResourceValueByType('ELEMENT_IMP_CODE'))\r\n" + 
				" },\r\n" +
				" elementsIdString:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'CSV of Elements ID (override Element ID, in case more than one element)'\r\n" +
				" },\r\n" + 
				" uomTypeName:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Uom Type Name (filter the fg_s_uom_all_v with this Uom type [no catalog definition is needed]) get also csv list of type names  ',\r\n" +
				" }\r\n" + 
				(schema.equals("") ? "" : ",\n" + schema) +
				"		}";
		return schema;
	}

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
	
	@Override
	public Map<String, String> getCurrentParameMapByInputVal(String inputVal) {
		Map<String, String> mIdVal = new HashMap<String,String>();
		mIdVal.put("CURRENT_" + impCode, inputVal);
		if(!inputVal.equals("")) {	
			if(inputVal.equals(lastCurrentVal)) {
				inputVal = lastCurrentId;
			}
			Map<String,String> infoMap = generalUtilForm.getCurrrentInfoById("UOM",inputVal);
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
			mIdVal.put("CURRENT_" + impCode + "_NAME", "");

		}
		return  mIdVal;
	}
	
	@Override
	public String getDefaultValue(long stateKey, String formId, String formCode) {
		String dv_ = super.getDefaultValue(stateKey, formId, formCode);
		if(dv_.startsWith("'") && dv_.endsWith("'")) {
			dv_ = dv_.substring(1, dv_.length() - 1);
		}
		return dv_; 
	}
	
	@Override
	public ElementInfoAuditTrailDisplay getAuditTrailValue(String formCode, String key, String postSaveValue, String originValue, String uiDisplayValue)
	{
		ElementInfoAuditTrailDisplay elementValueJobFlag = null;
		try {
			if(postSaveValue.equals(originValue))
			{
				elementValueJobFlag = new ElementInfoAuditTrailDisplay(uiDisplayValue, "0");
			}
			else
			{
				elementValueJobFlag = new ElementInfoAuditTrailDisplay(generalUtilForm.getCurrrentIdSingleStringInfo("UOM", postSaveValue, "Name"), "0");
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
