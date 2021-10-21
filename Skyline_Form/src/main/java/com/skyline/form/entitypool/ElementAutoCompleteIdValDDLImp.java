package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.ElementInfoAuditTrailDisplay;
import com.skyline.form.bean.LevelType;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.entity.Element;

/**
 * 
 * AutoCompleteIdVal
 *
 * AutoComplete drop down list, can holds different text and value at the same time.
 *
 * The data is taken from catalog (view with OBJIDVAL column type)
 * 
 * if singleRowParamVal is defined (contains parameter from init sql) we avoid SQL (performance improvement) and render single select list. 
 * Note: 1) if the user can not change the data (hidden or disable always) so no value can be selected we render empty.
 * 		 2) if selectFirstValue is true we ignore singleRowParamVal configuration
 * 		 3) Generally the single row render will happened when there is value (id from last save or default value) and singleRowParamVal found
 * 		 4) do not use this configuration in case java script code (like in search element(? TODO check if this is the case)) is used to update the list by choosing value from the list)
 * 		 5) possibility to join additional attributes to option by providing 'ATTRIBUTE':{key:"value"} inside OBJIDVAL object
 */
public class ElementAutoCompleteIdValDDLImp extends Element {
	
	@Autowired
	private GeneralDao generalDao;
 
	private String placeHolder, catalogItem, width, defaultName,maxWidth;
	
	private Boolean isMultiple;
	
	private boolean valueFound = false;
	
	private String lastCurrentId = ""; // holds last id selection
	
	private String lastCurrentVal = ""; // holds last val (name) selection
	
	private static final Logger logger = LoggerFactory.getLogger(ElementAutoCompleteIdValDDLImp.class);
	
	private boolean selectFirstValue = false;
	
	private String useSpecificFormCodeInf = "";
	
	private String singleRowParamVal = "";
	
	private final String FIRST_VALUE_FLAG = "@FG_FIRST_VALUE_FLAG@";
	
	private boolean isSingleRowParamVal;
	
	private boolean isUsedAsLink = false;
	
	private String linkId;
	
	private String FirstOption;
	
	private boolean ADD_ALL_ON_EMPTY_DATA = true;	
	
	private boolean SELECT_ALL_ON_EMPTY_DATA = true;
	
	private boolean REMOVE_AND_SAVE_ALL = false; 
	
	private boolean removeDeselectOption = false;
	
	private boolean removeSearchOption = false;
	
	private String allLabel = "";

	private String multipleSelectedValues;
  
	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try {
			if (super.init(stateKey, formCode, impCode, initVal).equals("")) {
				if(formCode != null && (formCode.equalsIgnoreCase("ExperimentReport")|| formCode.equalsIgnoreCase("MP"))) { // TODO add it as configuration default true and test it more...
					ADD_ALL_ON_EMPTY_DATA = false;
					SELECT_ALL_ON_EMPTY_DATA = false;
					if(formCode.equalsIgnoreCase("MP")) {
						REMOVE_AND_SAVE_ALL = true;
					}
				}
				
				placeHolder = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "placeHolder");
				isMultiple = Boolean.valueOf(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "multiple"));
				catalogItem = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "catalogItem");
				width = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "width");
				maxWidth = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "maxWidth");
				
				defaultName = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "defaultName");
				selectFirstValue = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "selectFirstValue"),false);
				FirstOption = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "FirstOption");
				singleRowParamVal = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "singleRowParamVal");
				if(FirstOption != null && FirstOption.equalsIgnoreCase("all")) {
					selectFirstValue = false;
					singleRowParamVal = "";
				}
				if(isMultiple) {
					multipleSelectedValues=generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "multipleSelectedValues");
					
				}
				allLabel = generalUtil.getEmpty(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "allLabel"),"All");
				useSpecificFormCodeInf = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "useSpecificFormCodeInf");
				//isSingleRowParamVal config ->
				isSingleRowParamVal = false;
				if(!selectFirstValue && (disableAlways || hideAlways) && !generalUtil.getJsonValById(jsonInit, "", "singleRowParamVal").isEmpty()) {//!generalUtil.getNull(singleRowParamVal).toUpperCase().contains("$P{")-adib 12092019 replaced the last condition
					isSingleRowParamVal = true;
				}
				isUsedAsLink = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "useAsLink"),false);
				linkId = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "linkParameter");
				removeDeselectOption = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "removeDeselectOption"),false);
				removeSearchOption = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "removeSearchOption"),false);
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
//		String elementSQLInfo = "";
		String lastvalue = "lastvalue=\"" + inputVal + "\"";
		width = (width.equals("")) ? "300px" : (width.indexOf("%") != -1) || (width.indexOf("px") != -1) ? width :  width + "px";
		String catalogItemData = "";
		if(disableAlways && !defaultName.equals("")){
			catalogItemData = "{\"VAL\":\"" + defaultName + "\",\"ID\":\"" + inputVal + "\"}";
		}
		else{
			if(!renderEmpty && !isEmptyDDLByValue(inputVal) && !isSingleDDLByValue(inputVal)) {
				StringBuilder info =  new StringBuilder();
				catalogItemData = generalUtilFormState.getFormCatalogItem(stateKey, formCode, catalogItem.split("\\.")[0], catalogItem.split("\\.")[1], impCode, info);
				//for element info ->
//				elementSQLInfo = generalUtilFormState.getFormCatalogDBSql(stateKey, formCode, catalogItem.split("\\.")[0], "1", impCode, "All");
				if(info != null) {
					html.put(domId + "_elementSQLInfo", info.toString());
				}
			}
		}
		String setCatalogItemAsOption = getCatalogItemAsOption(stateKey, formId, catalogItemData, inputVal);
		
		String linkHtml = "";
		
		if (isUsedAsLink){
			if(!linkId.isEmpty()){//if the field should be linked to any other formid rathar than the one in the value, then takes the parameter
				String formCode = formDao.getFormCodeBySeqId(linkId);
				if(!formCode.isEmpty()){
					linkHtml += "$('[id=\"" + domId + "_chosen\"]>>span').click(function(){checkAndNavigate(['"+linkId+"' ,'"+ formCode +"','', true]);});\n";
					linkHtml += "$('[id=\"" + domId + "_chosen\"]>>span').addClass('linkElement');\n";
				}
			} else if(!lastCurrentId.isEmpty()){
				String formCode = formDao.getFormCodeBySeqId(lastCurrentId);
				if(!formCode.isEmpty()){
					linkHtml += "$('[id=\"" + domId + "_chosen\"]>>span').click(function(){checkAndNavigate(['"+lastCurrentId+"' ,'"+ formCode +"','', true]);});\n";
					linkHtml += "$('[id=\"" + domId + "_chosen\"]>>span').addClass('linkElement');\n";
				}
			}
		}
		
		html.put(layoutBookMark, isLabel(isHidden) +
						"<select id=\"" + domId + "\"  valueType=\"id\" " + inputAttribute + " " + lastvalue + " style=\"" + getHidden(isHidden) + "width:" + width + ";"+(maxWidth.isEmpty()?"":"max-width: "+maxWidth+";")+"\" data-placeholder=\" " 
						+ placeHolder + "\" class=\"chosen-select\" " + getAttributes(isDisabled, isMandatory, isHidden) + " element=\"ElementAutoCompleteDDLImp\" "
						+ " lastSelectedName=\""+lastCurrentVal+"\" "
						+ "><option></option>" + setCatalogItemAsOption + " </select>" + isLabelEnd());
		html.put(layoutBookMark + "_ready",
				"$('[id=\"" + domId + "\"]').chosen({allow_single_deselect:"+ ((isMandatory || removeDeselectOption)?"false":"true") + ", search_contains:true, disable_search:" + removeSearchOption + ", width: '" + width + "'}).on('change', function(){"
				+ ((isMultiple) ? " handleMultipleSelection('"+multipleSelectedValues+"',"+FirstOption.equals("ALL")+",this," + ADD_ALL_ON_EMPTY_DATA + "); \n" : "") 
				+ doOnChangeJSCall + "});"
				+ ((isHidden) ? " $('[id=\"" + domId + "_chosen\"]').css('visibility','hidden');":"")
				+ (!maxWidth.isEmpty()? " $('[id=\"" + domId + "_chosen\"]').css('max-width','"+maxWidth+"');":"")
				+ linkHtml
				);
		
		return html;
	}	

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String inputVal, String domId, String inputAttribute, String doOnChangeJSCall,
			boolean isHidden, boolean isDisabled, boolean isMandatory) { // TODO return js eval see demo jsp
		String htmlBody;
		 
		lastCurrentId = "";
		lastCurrentVal = "";
		valueFound = false;
		if(generalUtil.getNull(inputVal).equals(FIRST_VALUE_FLAG)) {
			generalUtilFormState.setFormValue(stateKey, formCode, getImpCode(), "");
		}
		
		String catalogItemData = "";
		String elementSQLInfo = "";
		
		if(disableAlways && !defaultName.equals("")){
			catalogItemData = "{\"VAL\":\"" + defaultName + "\",\"ID\":\"" + inputVal + "\"}";
		} else {
			if(!renderEmpty && !isEmptyDDLByValue(inputVal) && !isSingleDDLByValue(inputVal)) {
				StringBuilder info =  new StringBuilder();
				catalogItemData = generalUtilFormState.getFormCatalogItem(stateKey, formCode, catalogItem.split("\\.")[0], catalogItem.split("\\.")[1], impCode, info);
				if(info != null) {
					elementSQLInfo = info.toString();
				}
			}
		}
		
		/*htmlBody = "upDateElement({'isHidden':'" + isHidden + "','isDisabled':'" + isDisabled + "','isMandatory':'"
				+ isMandatory + "','val':'<option></option>" + getCatalogItemAsOption(catalogItemData, inputVal) + "','domId':'"
				+ domId + "','type':'chosen','sqlInfo':'"+elementSQLInfo+"'});";*/
		
		JSONObject fullObj = new JSONObject();
		fullObj.put("isHidden", ""+isHidden+"").put("isDisabled", ""+isDisabled+"").put("isMandatory", "" + isMandatory + "").
				put("val", "<option></option>" + getCatalogItemAsOption(stateKey, formId, catalogItemData, inputVal) + "").
				put("domId", domId).put("type", "chosen").put("sqlInfo", elementSQLInfo);

		htmlBody = "upDateElement("+fullObj.toString()+");";		
		return htmlBody;
	}

	private String getCatalogItemAsOption(long stateKey, String formId, String data, String value) 
	{		
		String toReturn = "";
		StringBuilder sb = new StringBuilder();
		JSONArray arr = null;
		try {
			
			if(isEmptyDDLByValue(value)) {
				toReturn = "<option selected=\"selected\" value=\"\"></option>";
			} else {
				if(isSingleDDLByValue(value)) {
					
					//ab 18032019: fix display issue of sign '<'(less than)
					sb.append("<option selected=\"selected\" value=\"" + value + "\">" + singleRowParamVal.replace("<", "&lt;") + "</option>");
					lastCurrentId = value;
					lastCurrentVal = singleRowParamVal;
					valueFound = true;
					
				} else {
					String id_ = "";
					String val_ = "";
					String active_ ="";
					boolean isSelected = false;
					String[] valuesArray = value.split(",");		
					if(!generalUtil.getEmpty(data, "").equals("")) {
						arr = new JSONArray("[" + data + "]"); 
						for(int i = 0; i < arr.length(); i++){					
							if(!arr.get(i).toString().equals("null")){
								JSONObject currObj = arr.getJSONObject(i);
								StringBuilder attributes = new StringBuilder();
								id_ = currObj.getString("ID");
								val_ = currObj.getString("VAL");
								active_ = (!currObj.has("ACTIVE")) ? ""
										: (currObj.getString("ACTIVE").equals("1")) ? "" : "disabled";
								if(currObj.has("ATTRIBUTE"))
								{
									try {
										JSONObject attrObj = currObj.optJSONObject("ATTRIBUTE");
										JSONArray keys = attrObj.names ();
										for (int j = 0; j < keys.length (); ++j) {
											String key = keys.getString(j);
											String val = generalUtil.getEmpty(attrObj.getString(key),"");
											attributes.append(" ").append(key).append("=").append("\""+val+"\"").append(" ");
										}
									} catch (Exception e) {										
										e.printStackTrace();
										generalUtilLogger.logWrite(LevelType.ERROR, "Error in ElementAutoCompleteIdValDDLImp attribute data perparation!", "-1",ActivitylogType.SQLError, null, e);
									}
								}
								if(selectFirstValue && (value.equals("") || value.equals(FIRST_VALUE_FLAG)) && i==0)
								{
									valuesArray = new String[]{id_};
									generalUtilFormState.setFormValue(stateKey, formCode, getImpCode(), id_); //workaround in order to have data flow in case FIRST_VALUE_FLAG -> WE IT BACK and update map
								}
								for (String valueStr : valuesArray) {
									if (generalUtil.getNull(valueStr).equals(id_)
											|| (valueStr.equals(getDefaultValue(stateKey, formId, formCode)) && valueStr.equals(val_) && !val_.isEmpty())) {
										lastCurrentId = id_;
										lastCurrentVal = val_;
										valueFound = true;
										isSelected = true;
										break;
									} 
								}
								//ab 18032019: fix display issue of sign '<'(less than)
								val_ = generalUtil.getNull(val_).replace("<", "&lt;");
								if(isSelected) {
									sb.append("<option selected=\"selected\" value=\"" + id_ + "\" " + active_ + " "+attributes.toString()+">" + val_ + "</option>");
								} else if(active_.equals("")) { // fix bug - add only active unselected option
									sb.append("<option value=\"" + id_ + "\" " + active_ + " "+attributes.toString()+">" + val_ + "</option>");
								}
								isSelected = false;
							}
						}
					}
				}
			}
			
			toReturn = sb.toString();
			if (FirstOption.equals("ALL")) { // =ALL
				if (REMOVE_AND_SAVE_ALL && (arr != null && arr.length() > 0) && value.equalsIgnoreCase("all")) {
					toReturn = "<option " + " selected=\"selected\" " + " value=\"ALL\">All</option>" + sb.toString();
					valueFound = true;
				}
			else if (ADD_ALL_ON_EMPTY_DATA || (arr != null && arr.length() > 0)) {
					toReturn = "<option " + ((!valueFound && SELECT_ALL_ON_EMPTY_DATA)?" selected=\"selected\" ":"") + " value=\"ALL\">" + allLabel + "</option>" + toReturn;
					valueFound = valueFound || SELECT_ALL_ON_EMPTY_DATA;;
				}
			}
		} catch (JSONException e) {
			logger.error("getCatalogItemAsOption error in parsing json data=" + data);
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
				"	maxWidth:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Max Width',\r\n" +
				"   },\r\n" + 
				"	defaultName:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Default Name (instead of catalog)'\r\n" +
				"   },\r\n" + 
				"	catalogItem:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Catalog Column',\r\n" + 
				"      'enum':getResourceValueByType('OBJIDVAL')\r\n" + 
				"   },\r\n" + 
				"		multiple:{  \n" + 
				"		      type:'string',\n" + 
				"		      title:'Multiple',\n" + 
				"		      'enum':['FALSE','TRUE']\n" + 
				"   },\r\n" +
				"	multipleSelectedValues:{  \n" + 
				"		type:'string',\n" + 
				"		title:'Multiple Selected Values',\n" + 
				"   },\r\n" +
				"	FirstOption:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'First Option (if ALL then the selectFirstValue and single Row Param will be ignored (All will be selected) defualt value will be ignored (ALL will be the default to continue the data flow))',\r\n" + 
				"      'enum':['','ALL']\r\n" + 
				"   },\r\n" +
				"	allLabel:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'override ALL selection text (default is All)'\r\n" +
				"   },\r\n" +
				"	selectFirstValue:{  \r\n" + 
				"		type: 'boolean',\r\n" +  
				"		title:'Select First Value'\r\n" + 
				"   },\n" + 
				"	singleRowParamVal:{  \r\n" + 
				"		type: 'string',\r\n" +  
				"		title:'parameter value for single row display (to avoid SQL call) [do not use on element that may changed by search or js code]'\r\n" + 
				"   },\n" + 
				"	useSpecificFormCodeInf:{  \r\n" + 
				"		type:'string',\r\n" + 
				"		title:'Set formCode for fg_s_[FORMCODE]_inf_v [empty as default (then the fromcode is taken from FG_SEQUENCE by ID) / NA no inf_v_ call]'\r\n" + 
				"   },\n" +
				"   useAsLink:{  \r\n" + 
				"		type:'boolean',\r\n" + 
				"		title:'Use as link (clicking on the element, will navigate to the appropriate form with the formid/ID of the current value)'\r\n" + 
				"   },\r\n" +
				"   linkParameter:{  \r\n" + 
				"		type:'string',\r\n" + 
				"		title:'Link ID (parameter or constant that contains the ID to be navigated to on click event. If it is empty then link to the ID value of the DDL)',\r\n" + 
				"   },\n" + 
				"   removeDeselectOption:{  \r\n" + 
				"		type:'boolean',\r\n" + 
				"		title:'remove deselect option - default false if not mandatory'\r\n" + 
				"   },\n" + 
				"   removeSearchOption:{  \r\n" + 
				"		type:'boolean',\r\n" + 
				"		title:'remove search option'\r\n" + 
				"   }\n" +
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
		if(generalUtil.getNull(inputVal).trim().equals("") || generalUtil.getNull(inputVal).equals(FIRST_VALUE_FLAG) || (!valueFound && !isCurrentElementChange)) {//			filterMap.put(impCode, "");
			filterMap.put(impCode + "." + generalUtil.getEmpty(getFilterCatalogColumn(), impCode), "");
//			currentVal="";
//			currentId ="";
		} else {
//			filterMap.put(impCode, "'" + inputVal.replace(",", "','") + "'");
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
		/*if(isMultiple) {//adib 29072020 removed this in order to get the ID parameter
			return mIdVal;
		}*/
		mIdVal.put("CURRENT_" + impCode, inputVal);
		if(!inputVal.equals("") && !inputVal.isEmpty() && !inputVal.equals(FIRST_VALUE_FLAG) ) {
//			mIdVal.put("CURRENT_" + impCode + "_VAL", generalUtilForm.getCurrrentIdInfo(inputVal));// generalUtilForm.getFormNameValByFormId(catalogItem.split("\\.")[1].replace("_OBJIDVAL", ""), inputVal));
			
			if(inputVal.equals(lastCurrentVal)) {
				inputVal = lastCurrentId;
			}
			
			Map<String,String> infoMap = null;
//			useSpecificFormCodeInf = impCode.equals("FRACTIONMAT_ID")?"Result":"";
			
			if(generalUtil.getNull(useSpecificFormCodeInf).equals("NA")) {
				infoMap = null;
			} else if(!generalUtil.getNull(useSpecificFormCodeInf).equals("")) {
				infoMap = generalUtilForm.getCurrrentInfoById(useSpecificFormCodeInf, inputVal);
				if(infoMap == null || infoMap.size() == 0) {
					infoMap = generalUtilForm.getCurrrentIdInfo(inputVal);
					if(infoMap == null || infoMap.size() == 0) {
						generalUtilLogger.logWrite(LevelType.WARN, "Inf data was not found for impcode=" + impCode + " by useSpecificFormCodeInf=" + useSpecificFormCodeInf + ". But! found for id=" +inputVal, "-1", ActivitylogType.SQLError, new HashMap<String, String>());
					}
				}
			} else {
				if(!isMultiple){
					infoMap = generalUtilForm.getCurrrentIdInfo(inputVal);
				}
			}
			
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
//			mIdVal.put("CURRENT_" + impCode + "_VAL", "");
			mIdVal.put("CURRENT_" + impCode + "_NAME", "");
//			if(impCode.equals("FRACTIONMAT_ID")) {
//				mIdVal.put("CURRENT_" + impCode + "_RESULT_TODO", "");
//			}

		}
		return  mIdVal;
	}
	
	@Override
	public String getDefaultValue(long stateKey, String formId, String formCode) {
		String dv_ = super.getDefaultValue(stateKey, formId, formCode); //TODO version 9.6 find solution to parameter (that are always with the from upper comma CSV)
		if(selectFirstValue) {
			dv_ = FIRST_VALUE_FLAG; // workaround in order to have data flow in case FIRST_VALUE_FLAG -> WE WILL GET IT BACK IN getHtmlBody and ignore it
		} else if(dv_.startsWith("'") && dv_.endsWith("'")) {
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
		try {
			if(postSaveValue.equals(originValue))
			{
				elementValueJobFlag = new ElementInfoAuditTrailDisplay(uiDisplayValue, "0");
			}
			else
			{
				if(!postSaveValue.isEmpty()) {
					List<String> lval = generalDao.getListOfStringBySql(" select t.formidname from fg_sequence t where t.id in (" + postSaveValue + ")");
					//List<String> lval = generalDao.getListOfStringBySql(" select t.formidname from fg_sequence t where t.id in (" + postSaveValue + ")");
					elementValueJobFlag = new ElementInfoAuditTrailDisplay(generalUtil.listToCsv(lval), "0");
				} else {
					elementValueJobFlag = new ElementInfoAuditTrailDisplay("", "0");
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			elementValueJobFlag = new ElementInfoAuditTrailDisplay("", "1");
		}
		return elementValueJobFlag;
	}
	
	private boolean isSingleDDLByValue(String value) {
		return isSingleRowParamVal && !generalUtil.getNull(value).equals("") && !generalUtil.getNull(singleRowParamVal).equals("") && !generalUtil.getNull(singleRowParamVal).toUpperCase().contains("$P{"); //adib 12092019 added the last condition for the case of some error when decoding the parameters
	}

	private boolean isEmptyDDLByValue(String value) {
		return isSingleRowParamVal && generalUtil.getNull(value).equals("") ;//&& !generalUtil.getNull(singleRowParamVal).equals("");//adib 12092019 removed the last condition
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
