package com.skyline.form.service;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.CatalogInfoType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.Form;
import com.skyline.form.bean.FormEntity;
import com.skyline.form.bean.FormType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.dal.FormDao;
import com.skyline.form.entity.Catalog;
import com.skyline.form.entity.Element;
import com.skyline.form.entity.Entity;
import com.skyline.form.entity.EntityFactory;
import com.skyline.form.entity.Layout;

public class FormState {

	private Form form;
	
//	private String formCodeEntity;

	@Autowired
	private FormDao formDao;

	@Autowired
	private GeneralUtil generalUtil;
	
//	@Autowired
//	private GeneralUtilForm generalUtilForm;
	
	@Autowired
	public GeneralUtilLogger generalUtilLogger;

	@Autowired
	private EntityFactory entityFactory;

//	@Autowired
//	private Integration integration;
	
	@Autowired
	private IntegrationInitForm integrationInitForm;
	
	@Autowired
	private IntegrationDT integrationDT;
	
	@Autowired
	private IntegrationWF integrationWF;

	@Autowired
	private GeneralUtilFormState generalUtilFormState;
	
//	@Autowired
//	private IntegrationEvent integrationEvent;

	private String formCode;
	
//	private String sessionId;

	// -- Bean
	//	@Resource(name = "formBeanMap")
	private Map<String, Object> formBeanMap;

	// -- Catalog
	//	@Resource(name = "catalogMap")
	private Map<String, String> formCatalogMap;

	// -- Value selection
	//	@Resource(name = "formValueMap")
	private Map<String, String> formValueMap;

	// -- Parameters
	private Map<String, String> formParamMap;
	
	@Value("${appCompany:Adama}")
	private String appCompany;
	
	@Value("${Env:1.0}")
	private String appEnvVersion;
	
	@Value("${isTreeRoot:0}")
	private int isTreeRoot; //indicating if the breadcrumb in the title would be opened as a tree
	
	@Value("${isCatalogDataFilterByParents:0}")
	private int isCatalogDataFilterByParents; // vs filter the element by all the elements with higher level (not necessary one of the parents)
	 
//	@Autowired
//	private FormTempData formTempData;
	
	// -- MapTree
//	private Map<Integer, List<Element>> mapTree;
	private Map<Integer, List<Element>> elementMapTree;
	private List<String> rootParentPath;
	private List<Element> rootElementParentPath; 
	
	private Element AuthorizationElement = null;
	
	private static final Logger logger = LoggerFactory.getLogger(FormState.class);
	 
	//init
	public void initFormState(boolean isNewFormId, Map<String,String> lastSaveValMap, long stateKey, String formCode, String userId, String formId, String nameId, String urlCallParam, Map<String, String[]> requestMap, Map<String, String> outParamMap) {

		logger.info("********* FormState - initFormState: formCode=" + formCode + ", userId=" + userId + ", formId=" + formId);
		
		//set formCode 
		this.formCode = formCode;  
		//set form
		List<Form> formList = formDao.getFormInfoLookup(formCode, "%", true);
		this.form = formList.get(0);
//		this.formCodeEntity = generalUtil.getNull(form.getFormCodeEntity(),formCode);
		FormType formType = FormType.valueOf(form.getFormType());
		
		//init maps
		formBeanMap = new HashMap<String, Object>();
		formCatalogMap = new HashMap<String, String>();
		formValueMap = new HashMap<String, String>();
		formParamMap = new HashMap<String, String>();
		Set<String> duplicateKey = new HashSet<String>();
		StringBuilder toPrint = new StringBuilder(); 
		String paramTitle = "";
		
		//**** from Parameters basic
		paramTitle = "from Parameters basic";
		toPrint.append("\n----Param [" + paramTitle + "] ----\n");
		 
		//init isStruct 
		boolean isStructFormCode = false;
		isStructFormCode = formType.getStructureForm();
		outParamMap.put("isStruct", isStructFormCode?"1":"0");
		setFormParam( "isStruct", isStructFormCode?"1":"0");
		duplicateKey.add("ISSTRUCT");
		toPrint.append("\nitem: " + "$P{ISSTRUCT}" + ", value: " +  (isStructFormCode?"1":"0") + "\n");
		
		//init isNew
//		boolean isNewFormId = false;
//		if(isStructFormCode) {
//			isNewFormId = formDao.isNewFormId(formCode,formId); 
//		}
		outParamMap.put("isNew", isNewFormId?"1":"0");
		setFormParam( "isNew", isNewFormId?"1":"0");
		duplicateKey.add("ISNEW");
		toPrint.append("\nitem: " + "$P{ISNEW}" + ", value: " +  (isNewFormId?"1":"0") + "\n");
		
		//Add creation Date to the map		
		DateFormat dateFormat = new SimpleDateFormat(generalUtil.getConversionDateFormat());
		Date date = new Date();	
		outParamMap.put("SYS_DATE", dateFormat.format(date));
		setFormParam("SYS_DATE", dateFormat.format(date));
		duplicateKey.add("SYS_DATE"); 
		toPrint.append("\nitem: " + "$P{SYS_DATE}" + ", value: " +  (dateFormat.format(date)) + "\n");
		
		 
		//Add creation Time to the map	$P{SYS_TIME}	
	    dateFormat = new SimpleDateFormat(generalUtil.getConversionDateFormat());
		date = new Date();	
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append((date.getHours()<10)?"0" +date.getHours():date.getHours());
		stringBuilder.append(":");
		stringBuilder.append((date.getMinutes()<10)?"0" +date.getMinutes():date.getMinutes());
		outParamMap.put("SYS_TIME", stringBuilder.toString());
		setFormParam("SYS_TIME", stringBuilder.toString());
		duplicateKey.add("SYS_TIME"); 
		toPrint.append("\nitem: " + "$P{SYS_TIME}" + ", value: " +  stringBuilder.toString() + "\n");
		
		//Add app prop company and version the the map
		outParamMap.put("APP_COMPANY", appCompany);
		setFormParam("APP_COMPANY", appCompany);
		duplicateKey.add("APP_COMPANY"); 
		toPrint.append("\nitem: " + "$P{APP_COMPANY}" + ", value: " + appCompany + "\n");
		
		outParamMap.put("APP_ENV_VERSION", appEnvVersion);
		setFormParam("APP_ENV_VERSION", appEnvVersion);
		duplicateKey.add("APP_ENV_VERSION"); 
		toPrint.append("\nitem: " + "$P{APP_ENV_VERSION}" + ", value: " + appEnvVersion + "\n");
		
		//**** from Parameters requestMap
		paramTitle = "from Paramfrom Parameters requestMap";
		toPrint.append("\n----Param [" + paramTitle + "] ----\n");
		
		for (Map.Entry<String, String[]> entry : requestMap.entrySet()) { 
			String key = entry.getKey();
			String[] valArray = entry.getValue(); 
			if(duplicateKey.contains(generalUtil.getNull(key.toUpperCase()))) {
				key += "(requestMap)";
			}
			//param
			setFormParam( key, generalUtil.getNull(valArray[0],key));
			toPrint.append("\nitem: " + "$P{" + key.toUpperCase() + "}" + ", value: " + generalUtil.getNull(valArray[0],key) + "\n");
			//output
			if(outParamMap != null) {
				outParamMap.put( key, generalUtil.getNull(valArray[0],key));
			}
			duplicateKey.add(generalUtil.getNull(key.toUpperCase()));
		}
		
		//do some operations on creating new form
		Map<String, String> integrationMap = integrationInitForm.onIntegrationEvent(formCode, userId, formId, formType, isNewFormId, outParamMap);
				 
		//**** from Parameters integration from param (AUTHEN SQL)
		paramTitle = "from Parameters integration from param (AUTHEN SQL)";
		toPrint.append("\n----Param [" + paramTitle + "] ----\n");
		
		integrationMap.putAll(integrationInitForm.getFormParam(formCode, userId, formId, formType, isNewFormId, outParamMap));
		if(integrationMap != null){
			for (Map.Entry<String, String> entry : integrationMap.entrySet()) {
				String key = entry.getKey();
				String val = entry.getValue();
				if(duplicateKey.contains(generalUtil.getNull(key.toUpperCase()))) {
					key += "(integrationMap)";
				}
				//param
				setFormParam( key, generalUtil.getNull(val,key));
				toPrint.append("\nitem: " + "$P{" + key.toUpperCase() + "}" + ", value: " + generalUtil.getNull(val,key) + "\n");
				//output
				if(outParamMap != null && val != null) {
					outParamMap.put( key, generalUtil.getNull(val,key));
				}
				duplicateKey.add(generalUtil.getNull(key.toUpperCase()));
			} 
		}
				
		//**** formPathInfo //TODO Tehila
		String formPathInfo  = integrationInitForm.getFormPathInfo(stateKey, formCode, userId, formId, formType, isNewFormId).replace("'","&#39;");
		
		outParamMap.put("formPathInfo", formPathInfo);
		setFormParam( "formPathInfo", formPathInfo);
		duplicateKey.add("FORMPATHINFO");
		toPrint.append("\nitem: " + "$P{FORMPATHINFO}" + ", value: " +  formPathInfo + "\n");
		
		
		//**** from Parameters general user info
		paramTitle = "from Parameters general user info";
		toPrint.append("\n----Param [" + paramTitle + "] ----\n");
				
		Map<String, String> userInfoMap = formDao.getUserInfoMap(userId);
		if(userInfoMap != null){	
			for (Map.Entry<String, String> entry : userInfoMap.entrySet()) {
				String key = entry.getKey();
				String val = entry.getValue();
				if(duplicateKey.contains(generalUtil.getNull(key.toUpperCase()))) {
					key += "(userInfoMap)";
				}
				//param
				setFormParam( key, generalUtil.getNull(val,key));
				toPrint.append("\nitem: " + "$P{" + key.toUpperCase() + "}" + ", value: " + generalUtil.getNull(val,key) + "\n");
				//output
				if(duplicateKey.contains(generalUtil.getNull(key.toUpperCase()))) {
					outParamMap.put( key, generalUtil.getNull(val,key));
				}
				duplicateKey.add(generalUtil.getNull(key.toUpperCase()));
			} 
		}
		

		//these values are used in the struct forms only.
		//Popups dont use them since each save of them is saved as a temporary form until the final save of the parent form, in which the last data will anyway be validated
		if(formType.name().equals("STRUCT") || formType.name().equals("INVITEM")){
			//**** from Parameters last userId and timestamp values for validating simultaneously work of some users on the same form
			paramTitle = "form Parameters userId and timestamp of the last save";
			toPrint.append("\n----Param [" + paramTitle + "] ----\n");
					
			Map<String, String> lastFormSaveInfoMap = formDao.getLastFormDataMap(form.getFormCodeEntity(),formId,formType);
			if(lastFormSaveInfoMap != null){	
				for (Map.Entry<String, String> entry : lastFormSaveInfoMap.entrySet()) {
					String key = entry.getKey();
					String val = entry.getValue();
					if(duplicateKey.contains(generalUtil.getNull(key.toUpperCase()))) {
						key += "(lastSaveInfo)";
					}
					//param
					setFormParam( key, generalUtil.getNull(val,key));
					toPrint.append("\nitem: " + "$P{" + key.toUpperCase() + "}" + ", value: " + generalUtil.getNull(val,key) + "\n");
					//output					
					outParamMap.put( key, generalUtil.getNull(val,key));
					duplicateKey.add(generalUtil.getNull(key.toUpperCase()));
				} 
			}
		}
		
		//**** from Parameters integrationWFMap
		paramTitle = "from Parameters integrationWFMap";
		toPrint.append("\n----Param [" + paramTitle + "] ----\n");
				
		Map<String, String> integrationWFMap = integrationWF.getFormWFStateGeneral(formCode, userId, formId, isNewFormId, outParamMap);
		
		if(integrationWFMap != null){	 
			for (Map.Entry<String, String> entry : integrationWFMap.entrySet()) {
				String key = entry.getKey();
				String val = entry.getValue();
				if(duplicateKey.contains(generalUtil.getNull(key.toUpperCase()))) {
					key += "(integrationWFMap)";
				}
				//param
				setFormParam( key, generalUtil.getNull(val,key));
				toPrint.append("\nitem: " + "$P{" + key.toUpperCase() + "}" + ", value: " + generalUtil.getNull(val,key) + "\n");
				//output
				if(outParamMap != null && val != null) {
					outParamMap.put( key, generalUtil.getNull(val,key));
				}
				duplicateKey.add(generalUtil.getNull(key.toUpperCase()));
			} 
		}
		
		if(formType.getTypeName().equals("SELECT") || formType.getTypeName().equals("SMARTSEARCH") || formType.getTypeName().equals("REF")){
			//**** from parent map
			paramTitle = "from parent map";
			toPrint.append("\n----Param [" + paramTitle + "] ----\n");
			Map<String,String> parentFormMap = null;
			String parentFormCode = outParamMap.get("PARENT_FORMCODE");
			String parentId = outParamMap.get("PARENT_ID");
			if(generalUtil.getNull(parentFormCode).isEmpty() && !generalUtil.getNull(parentId).isEmpty() && !parentId.equals("-1")){
				parentFormCode = formDao.getFormCodeBySeqId(parentId);
			}
			if(parentFormCode != null) {
				parentFormMap = generalUtilFormState.getFormParam(stateKey, parentFormCode);
			}
			
			if(parentFormMap != null){	 
				for (Map.Entry<String, String> entry : parentFormMap.entrySet()) {
					String p_key = entry.getKey().replace("$P{", "").replace("}", "");
					String key = "STRUCT_PARAM_"+p_key;
					if(p_key.startsWith("STRUCT_PARAM_")){
						key = p_key;
					}
					String val = entry.getValue();
					if (!duplicateKey.contains(generalUtil.getNull(key.toUpperCase()))) {

						// param
						setFormParam(key, generalUtil.getNull(val, key));
						toPrint.append("\nitem: " + "$P{" + key.toUpperCase() + "}" + ", value: "
								+ generalUtil.getNull(val, key) + "\n");
						// output
						if (outParamMap != null && val != null) {
							outParamMap.put(key, generalUtil.getNull(val, key));
						}
						duplicateKey.add(generalUtil.getNull(key.toUpperCase()));
					}
				}
			}
		}
		
		logger.info(toPrint.toString()); 
		// urlCallParam handling
//		String urlCallParam = "";
		try {
//			urlCallParam = outParamMap.get("urlCallParam");
			if(!generalUtil.getNull(urlCallParam).equals("")) {
//				Map<String, String> urlCallParamMap = generalUtil.jsonSimpleToMap("CALL_PARAM",urlCallParam);
				
					String keyPrefix = "CALL_PARAM"; 
					if(!generalUtil.getNull(urlCallParam).equals("")) {
						
						JSONObject jObject = new JSONObject(urlCallParam.trim());
						Iterator<?> jsKeys = jObject.keys();
						
						while (jsKeys.hasNext()) {
							try {
								String jsKey = (String) jsKeys.next();
								String k_ = generalUtil.getNull(keyPrefix).toUpperCase() + "_" + jsKey;
								String v_ = replaceFormParam(jObject.getString(jsKey)); //generalUtilForm.getJsonVal(formCode, urlCallParam, jsKey); //TODO key
								//to param
								setFormParam( k_, generalUtil.getNull(v_,k_));
								// toPrint
								toPrint.append("\nitem: " + "$P{" + k_.toUpperCase() + "}" + ", value: " + generalUtil.getNull(v_,k_) + "\n");
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
			}  
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//set from beans
		setFormBeanByType(stateKey, "%");
		
		//setFormValue
		for (Element e : getElementList()) {
//			if (!formValueMap.containsKey(e.getImpCode())) {
//				setFormValue(e.getImpCode(), "");
//			}
			setFormValue(e.getImpCode(), "");
		}
		
		//*** using this in getLastFormSaveValue because isNew is true on clone ***//
//		String isClone  = outParamMap.get("isClone");
//		isClone = ((isClone != null) && (isClone.equals("1"))) ? "1" : "0";	
				
				
		//set form values
//		setFormValue(formDao.getLastFormSaveValue(sessionId, formCode, formId, userId, nameId, isNewFormId,isClone));
		setFormValue(lastSaveValMap);
		
		//set tree
		rootParentPath = new ArrayList<String>();
		rootElementParentPath = new ArrayList<Element>();
		setElementMapTree();
		
		StringBuilder sbOutParam = new StringBuilder();
		sbOutParam.append("\n----OUT Param (avaliable in model and view & and hidden input----\n");
		for (Map.Entry<String, String> entry : formParamMap.entrySet()) {

			String key = entry.getKey();
			String val = entry.getValue();

			sbOutParam.append("\nitem: " + key + ", value: " + val + "\n");
		}
	}
	
	
	private String replaceFormParam(String value) { 
		String toReturn = value;
		try { 
			Pattern pattern = Pattern.compile("(\\$P\\{)(.*?)(\\})");
			Matcher matcher = pattern.matcher(value);
			while (matcher.find()) { 
				//log before
				logger.debug("replaceFormParam before replace:" + value);
				
				String paramCode = matcher.group(0);
				String paramValue = ""; 
				 
				paramValue = getFormParam(paramCode);
				 
				if(paramValue == null) {
//					if(cleanParam) {
//						toReturn = "";
//					} else {
						toReturn = value;
//					}
					//log break
					logger.debug("replaceFormParam NO replace. return: " + toReturn);
					break;
				} else {
					toReturn = toReturn.replace(paramCode, paramValue);
				}
				//log after
				logger.debug("replaceFormParam after replace. return: " + toReturn);
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return toReturn;
	}
	

	// -- Param
	public Map<String, String> getFormParam() {
		return formParamMap;
	}

	public String getFormParam(String key) {
		return formParamMap.get(key);
	}

	public void setFormParam(String key, String val) {
		formParamMap.put( "$P{" + key.toUpperCase() + "}", val);
	}

	public void setFormParam(Map<String, String> map) {
		for (Map.Entry<String, String> entry : map.entrySet()) { 
			formParamMap.put( "$P{" + entry.getKey().toUpperCase() + "}", entry.getValue()); 
		} 
	}
	
	// -- Bean
	public Map<String, Object> getFormBean() {
		return formBeanMap;
	}

//	private void setFormBean(Map<String, Object> map) {
//		if (formBeanMap == null) {
//			formBeanMap = new HashMap<String, Object>();
//		}
//		this.formBeanMap.putAll(map);
//	}

	private void setFormBean(String key, Object val) {
		if (formBeanMap == null) {
			formBeanMap = new HashMap<String, Object>();
		}
		this.formBeanMap.put(key, val);
	}

	public Object getFormBean(String key) {
		return this.formBeanMap.get(key);
	}
	
	public void removeFormBean(String key) {
		if(formBeanMap.containsKey(key)) {
			formBeanMap.remove(key);
		}
	}

	// -- Catalog
	public Map<String, String> getFormCatalog(String sourceElementImpCode) {
		Map<String, String> formCatalogMapByMapTree = new HashMap<String, String>();
		String key;
		for (Map.Entry<String, String> entry : formCatalogMap.entrySet()) {  
			key = entry.getKey();
			formCatalogMapByMapTree.put(key, getFormCatalog(key, sourceElementImpCode));  
		}
		return formCatalogMapByMapTree;
	}
 
	public String getFormCatalog(String key, String sourceElementImpCode) {
		String toReturn = null;
//		Map<String, String> formCatalogMapByMapTree = new HashMap<String, String>();
//		String key;
//		String val;
//		for (Map.Entry<String, String> entry : formCatalogMap.entrySet()) { 
//			key = entry.getKey();
//			val = entry.getValue();
//			if(isParent(sourceElementImpCode,))
//			formCatalogMapByMapTree.put(key, val);  
//		}
//		return formCatalogMapByMapTree;
		try {
			if(generalUtil.getNull(sourceElementImpCode).isEmpty() || (isLevelBelow(key.split("\\.")[0], sourceElementImpCode) && isLinkedBetween(key.split("\\.")[0],sourceElementImpCode))) {
				toReturn = formCatalogMap.get(key);
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
		}
		return toReturn;
	}
  
	/**
	 * TODO find better solution - here we fix bug in main screen - it return true if there is unlinked table between the parentImpCode and the sourceElementImpCode or the sourceElementImpCode is unlinked 
	 * @param parentImpCode
	 * @param sourceElementImpCode
	 * @return
	 */
	public boolean isLinkedBetween(String parentImpCode, String sourceElementImpCode) {
		// TODO Auto-generated method stub
		boolean linked = true;
	 
		if(formCode.equals("Main")) {
			String tableList = "firstTable,secondTable,thirdTable,fourthTable,fifthTable,sixthTable,seventhTable,eightthTable,ninethTable";
			String [] tableListArray = tableList.split(",");	
			boolean startCheck = false;
			 
			for (String tableName : tableListArray) {
				
				if(!startCheck && tableName.equals(parentImpCode)) {
					startCheck = true; // we flag first
				} else if (startCheck) { // after first check if table linked or we got to the source table
					
					boolean isLinkCheck = true;
					String dtVal = getFormValue(tableName);
					
					//set isLinkCheck to user selection or default ->
					if(generalUtil.getNull(dtVal).split(",").length > 3 && dtVal.split(",")[3].equals("0")) {  //val example: Experiment,ALL,DTM,1,Main,1,10,19135,
						isLinkCheck = false;
					}  
					 
					if(!isLinkCheck) {
						linked = false;
					} 
					
					if(!isLinkCheck || tableName.equals(sourceElementImpCode)) {
						break;
					}
					
					
				} else if(tableName.equals(sourceElementImpCode)) {
					break;
				}
 
			}
		}
//		System.out.println("link info: parentImpCode=" + parentImpCode + ", sourceElementImpCode=" + sourceElementImpCode + ", linked=" + linked + ", parent val=" +  getFormValue(parentImpCode) + ", source val=" + getFormValue(sourceElementImpCode));
	 
		return linked;
	}



	public void cleanFormCatalog() {
		formCatalogMap.clear();
		
	}



	/**
	 * 
	 * @param key
	 * @param val
	 *            - holds the catalog filter information / selection - the catalog implementation need to parse it & the element java-script need to pass it - according the following format: single selection - 'val1' multiple selection - 'val1','val2' all selection - ALL (the string ALL) no selection - empty string or null (or the null string) TODO ... > comparable - in version 9.7 - generally it should be like the concept we designed the dataTable filter element
	 */
	public void setFormCatalog(String key, String val) {
		formCatalogMap.put(key, val);
	}

	public void setFormCatalog(Map<String, String> map) {
		formCatalogMap.putAll(map);
	}

	public String getFilterItemInfo(String item, String sourceElementImpCode, String info) {

		String intfoReturn = "";
		String value = formCatalogMap.get(item);

		CatalogInfoType catalogInfoType = CatalogInfoType.valueOf(info);

		switch (catalogInfoType) {
		case COUNT: {
			intfoReturn = generalUtil.getNull(value).equals("") ? "0"
					: String.valueOf(StringUtils.countOccurrencesOf(value, ",") + 1);
		}
			break;
		case FIRST_VALUE: {
			if (value.indexOf(",") != -1) {
				intfoReturn = value.substring(0, value.indexOf(","));
			} else {
				intfoReturn = value;
			}
		}
			break;
		case LAST_VALUE: {
			intfoReturn = value.substring(value.lastIndexOf(",") + 1);
		}
			break;
		case EXISTS: {
			intfoReturn = generalUtil.getNull(value).equals("") ? "false" : "true";
		}
			break;
		default:
			break;
		}

		return intfoReturn;
	}

	// -- Value selection  
	public Map<String, String> getFormValue() {
		return formValueMap;
	}

	public String getFormValue(String key) {
		return formValueMap.get(key);
	}

	public void setFormValue(String key, String val) {
		formValueMap.put(key, val);
	}

	public void setFormValue(Map<String, String> map) {
		formValueMap.putAll(map);
	}
	
	// -- mapTree
	public Map<Integer, List<Element>> getElementMapTree() {
		return elementMapTree;
	}
	
	//map: key - data tree level (start from 0 for elements with no parent), value - element list under the same level 
	private void setElementMapTree() { //TODO improve + check circles 
		elementMapTree = new HashMap<Integer, List<Element>>();
		List<Element> elementList = getElementList();

		Map<String, String> elementMapParentTmp = new HashMap<String, String>();
		Map<String, Element> elementMapIndexTmp = new HashMap<String, Element>();
		for (Element element : elementList) {
//			if (!(element instanceof ElementLabelImp)) {
				elementMapParentTmp.put(element.getImpCode(), element.getParentElement());
				elementMapIndexTmp.put(element.getImpCode(), element);
//			}
		}

		for (Map.Entry<String, String> entry : elementMapParentTmp.entrySet()) {

			rootParentPath.clear();
			Integer key = getLevel(elementMapParentTmp, entry.getKey());
			String elementCode = entry.getKey();

			List<Element> elementList_;
			if (elementMapTree.get(key) == null) {
				elementList_ = new ArrayList<Element>();
			} else {
				elementList_ = (List<Element>) elementMapTree.get(key);
			}
			elementList_.add(elementMapIndexTmp.get(elementCode));
			elementMapTree.put(key, elementList_); //entry.getKey()
		} 
	}
	
	private Integer getLevel(Map<String, String> elementMapTreeTmp, String key) {
		if (generalUtil.getNull(elementMapTreeTmp.get(key)).equals("") || rootParentPath.contains(key)) { // no parent or circle in rootParentPath -> top level
			return 0;
		} else { // elementMapTreeTmp.get(key) = parentCode
			rootParentPath.add(key);
			return 1 + getLevel(elementMapTreeTmp, elementMapTreeTmp.get(key));
		}
	}
  
	public boolean isParenteElement(Element element, String potentialParentElement) { 
		rootElementParentPath.clear();
		return isParenteElementCall(element, potentialParentElement) && isLinkedBetween(potentialParentElement, element.getImpCode());
	}
	
	private boolean isParenteElementCall(Element element, String potentialParentElement) { 
		if(generalUtil.getNull(element.getImpCode()).equals(potentialParentElement)) {
			return true;
		} else {
			if(generalUtil.getNull(element.getParentElement()).equals("") || rootElementParentPath.contains(element)) {
				return false;
			} else {  
				Element e = (Element)getFormBean(element.getParentElement());
				rootElementParentPath.add(element);
				return isParenteElementCall(e , potentialParentElement);
			}
		} 
	}
	
	private boolean isParenteElement(String sourceElementImpCode, String potentialParentElement) { 
		Element element = (Element)getFormBean(sourceElementImpCode);
		return isParenteElementCall(element, potentialParentElement);
	}
	
	public boolean isLevelBelow(String potentialParentElement, String sourceElementImpCode) {
		boolean toReturn = getCurrentLevelByElementCode(potentialParentElement) < getCurrentLevelByElementCode(sourceElementImpCode);
		if(toReturn) {
			if(isCatalogDataFilterByParents == 1) {
				toReturn = isParenteElement(sourceElementImpCode, potentialParentElement);
			}
			
//			if(toReturn) {
//				System.out.println("it is true");
//			}
		}
		return toReturn;
	}
	 
	public int getCurrentLevelByElementCode(String currentElementCode) {
		int currentLevel = 0;
		Iterator<Entry<Integer, List<Element>>> mapTreeIterator = elementMapTree.entrySet().iterator();
		
		while (mapTreeIterator.hasNext()) { //level loop 
			Map.Entry<Integer, List<Element>> pairs = (Map.Entry<Integer, List<Element>>) mapTreeIterator.next();
			List<Element> elementLevelList = pairs.getValue();
			for (Element elementFlow : elementLevelList) {
				if(elementFlow.getImpCode().equals(currentElementCode)) {
					currentLevel = pairs.getKey();
				} 
			}
			
		}
		return currentLevel;
	}
	
	private String mapTreeToString() {
		StringBuilder toReturn = new StringBuilder();
		Iterator<Entry<Integer, List<Element>>> it = elementMapTree.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, List<Element>> pairs = (Map.Entry<Integer, List<Element>>) it.next();
			toReturn.append("\nLevel: " + pairs.getKey() + ", Elements: " + elementListToString(pairs.getValue()));
		}
		return toReturn.toString();
	}

	private String elementListToString(List<Element> elementList) {
		StringBuilder toReturn = new StringBuilder();

		for (Element e : elementList) {
			toReturn.append(e.getImpCode() + ",");
		}

		return toReturn.toString().length() > 0 ? toReturn.toString().substring(0, toReturn.toString().length() - 1)
				: toReturn.toString();
	}  
	// -- end mapTree
 
	//PRIVATE FUNC...
	private List<Entity> setFormBeanByType(long stateKey, String type) {
		List<Entity> entityList = new ArrayList<Entity>();
		long startTime = System.currentTimeMillis();
		long endTime = -1l;  
		int initIndex = 0;
		Map<String, String> inf_ = new HashMap<String, String>();
		
		try {
			List<FormEntity> formEntityList = formDao.getFormEntityInfoLookup(formCode, type);
			//add to log
			endTime = System.currentTimeMillis();
			inf_.put("(" + initIndex++ + ")formEntityList", String.valueOf(endTime - startTime)); 
			startTime = System.currentTimeMillis(); 
			
			for (FormEntity formEntity : formEntityList) {
				Entity entity = null;
				if(formEntity.isGlobalInit()) {
					entity = formEntity.getEntity();
				} 

				if(entity == null || !formEntity.isGlobalInit()) {
					entity = entityFactory.getEntity(formEntity.getEntityImpClass()); // instance
				}
				
				if(!formEntity.isGlobalInit() && !entity.init(stateKey, formCode, formEntity.getEntityImpCode(), formEntity.getEntityImpInit()).equals("")) {
					generalUtilLogger.logWrite(LevelType.WARN, "init element failed! enetitycode=" + formEntity.getEntityImpCode(), "", ActivitylogType.FormInitiation, null);
				}
//				Entity entity = formEntity.getEntity();
//				if(!formEntity.isSessionInit() && !entity.init(formCode, formEntity.getEntityImpCode(), formEntity.getEntityImpInit()).equals("")) {
//					generalUtilLogger.logWrite(LevelType.WARN, "init element failed! enetitycode=" + formEntity.getEntityImpCode(), "", ActivitylogType.FormInitiation, null);					 
//				}
				entityList.add(entity);
				logger.info("set bean: " + formEntity.getEntityImpCode());
				setFormBean(formEntity.getEntityImpCode(), entity);
				//add to log
				endTime = System.currentTimeMillis();
				if((endTime - startTime > 100l)) {
					inf_.put("(" + initIndex++ + ")" + formEntity.getEntityImpCode() + ((endTime - startTime > 200l)?"!!!":""), String.valueOf(endTime - startTime));
				}
				startTime = System.currentTimeMillis(); 
				
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
//			System.out.println("getEntityListByCodeAndType - Error! " + e.toString());
		}
		return entityList;
	}

	public List<Catalog> getCatalogList() {
		List<Catalog> entityList = new ArrayList<Catalog>();
		for (Map.Entry<String, Object> entry : formBeanMap.entrySet()) {
			Object val = entry.getValue();
			if (val instanceof Catalog) {
				entityList.add((Catalog) val);
			}
		}
		return entityList;
	}

	public List<Layout> getLayoutList() {
		List<Layout> entityList = new ArrayList<Layout>();
		for (Map.Entry<String, Object> entry : formBeanMap.entrySet()) {
			Object val = entry.getValue();
			if (val instanceof Layout) {
				entityList.add((Layout) val);
			}
		}
		return entityList;
	}

	public List<Element> getElementList() {
		List<Element> entityList = new ArrayList<Element>();
		for (Map.Entry<String, Object> entry : formBeanMap.entrySet()) {
			Object val = entry.getValue();
			if (val instanceof Element) {
				if(((Element)val).getImpName().equals("Authorization")) {
					setAuthorizationElement((Element)val);
				} else {
					entityList.add((Element) val);
				} 
			}
		}
		return entityList;
	}
	
	public String getFormStateSummary(boolean showParam) {
		
		StringBuilder toReturn = new StringBuilder("from summary for formCode=" + formCode + "\n"); 
		String key = "";
		Object val = null;

		if(showParam) {
			toReturn.append("\n----Param----\n");
			for (Map.Entry<String, String> entry : formParamMap.entrySet()) {
	
				key = entry.getKey();
				val = entry.getValue();
	
				toReturn.append("\nitem: " + key + ", value: " + val + "\n");
			}
		}

		toReturn.append("\n----Bean----\n");
		for (Map.Entry<String, Object> entry : formBeanMap.entrySet()) {

			key = entry.getKey();
			val = entry.getValue();

			toReturn.append("\nbean: " + key + ", bean info: " + val + "\n");
		}

		toReturn.append("\n----Catalog----\n");
		for (Map.Entry<String, String> entry : formCatalogMap.entrySet()) {

			key = entry.getKey();
			val = entry.getValue();

			toReturn.append("\nitem: " + key + ", value: " + val + "\n");
		}

		toReturn.append("\n----Value----\n");
		for (Map.Entry<String, String> entry : formValueMap.entrySet()) {

			key = entry.getKey();
			val = entry.getValue();

			toReturn.append("\nelement: " + key + ", value: " + val + "\n");
		}
		
		toReturn.append("\n----Element tree----\n");
		toReturn.append(mapTreeToString());
		 
		return toReturn.toString();
	}
 
	public String getSummary(boolean showParam) {
		return getFormStateSummary(showParam);
		
	} 
	  
	public String getFormId() {
		return formParamMap.get("$P{FORMID}");
	} 

	public String getUserId() {
		return formParamMap.get("$P{USERID}");
	}



	public Element getAuthorizationElement() {
		return AuthorizationElement;
	}



	public void setAuthorizationElement(Element authorizationElement) {
		AuthorizationElement = authorizationElement;
	}



	public Map<String, String> getFormCatalogMap() {
		// TODO Auto-generated method stub
		return formCatalogMap;
	}
	
	public String setWFState(long stateKey, String formCode,String userId,String formId,boolean isNewFormId,List<DataBean> dataBeanList){
		
		String paramTitle = "form Parameters integrationWFMap";
		StringBuilder toPrint = new StringBuilder();
		toPrint.append("\n---- Update WF States ----\n----Param [" + paramTitle + "] ----\n");
		
		logger.info(toPrint.toString());
				
		Map<String, String> integrationWFMap = integrationWF.getFormWFStateGeneral(formCode, userId, formId, isNewFormId, generalUtil.initElementValueMapByBeanList(dataBeanList));
		if(integrationWFMap != null){	 
			for (Map.Entry<String, String> entry : integrationWFMap.entrySet()) {
				String key = entry.getKey();
				String val = entry.getValue();
				//param
				generalUtilFormState.setFormParam(stateKey, formCode, key, val);
				toPrint.append("\nitem: " + "$P{" + key.toUpperCase() + "}" + ", value: " + generalUtil.getNull(val,key) + "\n");
			} 
		}
		logger.info(toPrint.toString());
		return "1";
	}
	
	
	public List<Map<String, String>> customCriteriaList (String elementCode, String struct, long stateKey, List<Map<String, String>> sqlPoolMapList, Map<String, String> parentMap, Map<String, String> currentFormMap) {
		return integrationDT.customCriteriaList(elementCode, struct, stateKey, sqlPoolMapList, parentMap, currentFormMap);
	}
	
	public JSONObject customerDTDisplayViewList (String formCode, String impcode, String struct, String displayCatalogItemDefaulValue, String lasdDTView, boolean isLevelChange) {
		return integrationDT.customerDTDisplayViewList(formCode, impcode, struct, displayCatalogItemDefaulValue, lasdDTView, isLevelChange);
	}
	
	public String customerDTDefaultHiddenColumns (String formCode, String impcode, String struct) {
		return integrationDT.customerDTDefaultHiddenColumns(formCode, impcode, struct);
	}
	
	


	public String showFormPathDisplayHtml(String path) {
//		String arrowRightIcon = "";
//		if(isTreeRoot == 1){
//			arrowRightIcon = "<span class=\"jstree-closed\"><i style=\"background-image:url(../skylineFormWebapp/dist/themes/default/32px.png); "
//					+ "background-position: -100px -4px;"
//					+ "height: 15px;"
//					+ " width: 24px; "
//					+ "background-repeat: no-repeat;"
//					+ "display: inline-block;\""
//					+ " onclick=\"openFloatTree(this,'%1$s','%2$s','%3$s')\"></i> </span> \n";//style=\"margin-bottom: 2px;margin-left: 6px;\"
//		} else {
//			arrowRightIcon = "<span><img src=\"../skylineFormWebapp/images/arrow_right.png\" class=\"arrow_right\"  style=\"margin-bottom: 2px;margin-left: 6px;\"> </span> \n";
//		}
//		String toReturn = "";
//		try {
//			if(path.isEmpty()){
//				return toReturn;
//			}
//			JSONObject json = new JSONObject(path);
//			JSONArray pathList = json.getJSONArray("path");
//			String currentFormCode= generalUtil.getJsonValById(pathList.get(pathList.length()-1).toString(),"name").split(":")[0];
//
//			for(int i= 0;i<pathList.length()-1;i++){
//				String p = pathList.get(i).toString();
//				String []detailsToDisplay = generalUtil.getJsonValById(p,"name").split(":");
//				String id = generalUtil.getJsonValById(p,"id");
//				String formCode = detailsToDisplay.length>0?generalUtil.getJsonValById(p,"name").split(":")[0]:"";
//				String name = detailsToDisplay.length>1?generalUtil.getJsonValById(p,"name").split(":")[1]:"";
//					
//				String hrefStr ="<a href='#' onClick=\"checkAndNavigate(['"+id+"','"+formCode+"'])\" >" +name+ "</a>";//class=\"breadcrumb_link\"				}
//				   
//				if(isTreeRoot == 1){
//					toReturn +=  String.format(arrowRightIcon,id,formCode,name)+hrefStr;
//				} else {
//					if(i == pathList.length()-2 ){
//						toReturn += hrefStr;
//					} else {
//						toReturn +=  hrefStr + String.format(arrowRightIcon,id,formCode,name);
//					}
//				}
//			}
//		}
//		catch(Exception ex){
//			generalUtilLogger.logWrite(ex);
//			ex.printStackTrace();
//			toReturn = "";
//		}
//		return toReturn;
		// because we need a manipulation in adama on step under experiment we make it in the customer init class...
		return integrationInitForm.showFormPathDisplayHtml(path);
	}
}
