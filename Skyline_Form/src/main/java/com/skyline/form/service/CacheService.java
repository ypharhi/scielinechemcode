package com.skyline.form.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
// import org.springframework.core.env.MutablePropertySources;
import org.springframework.stereotype.Service;

import com.skyline.form.bean.DataType;
import com.skyline.form.bean.ElementInfoAuditTrailMeta;
import com.skyline.form.bean.Form;
import com.skyline.form.bean.FormEntity;
import com.skyline.form.bean.FormType;
import com.skyline.form.bean.InfData;
import com.skyline.form.bean.LookupType;
import com.skyline.form.dal.FormBuilderDao;
import com.skyline.form.entity.Entity;
import com.skyline.form.entity.EntityFactory;

@Service
public class CacheService {

	@Autowired
	private EntityFactory entityFactory;

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private FormBuilderDao formBuilderDao;
	
	@Value("${useGlobalInitOnStart:1}")
	private int useGlobalInitOnStart;
	 
	private Map<String, List<InfData>> cacheDataMap = null;
	private List<FormEntity> formEntityList = null;
	private List<Form> formList = null;
	private Map<String, ElementInfoAuditTrailMeta> formElementInfoAuditTrailMetaMap = null;
//	private Map<String, String> formElementLabelMap = null;
	private Map<String, Entity> formEntityClassSingleToneMap = null;
	private Map<String, Form> formMap = null;
	private Map<String, Form> formDBLookupMap = null;
	private Map<String, List<FormEntity>> formEntityDBLookupMap = null;
 
	public Map<String, Form> getFormMap() {
		return formMap;
	}
	
	public Map<String, Form> getFormDBLookupMap() {
		return formDBLookupMap;
	}
	
	public Map<String, List<FormEntity>> getFormEntityDBLookupMap() {
		return formEntityDBLookupMap;
	}
	
	public List<Form> getFormList() {
		return formList;
	}

	public List<FormEntity> getFormEntityList() {
		return formEntityList;
	}

	public CacheService() {
		cacheDataMap = new Hashtable<String, List<InfData>>();
		formEntityList = Collections.synchronizedList(new ArrayList<FormEntity>());
		formList = Collections.synchronizedList(new ArrayList<Form>());
		formElementInfoAuditTrailMetaMap = new HashMap<String, ElementInfoAuditTrailMeta>();
//		formElementLabelMap = new HashMap<String, String>();
		formEntityClassSingleToneMap = new HashMap<String, Entity>();
		formMap = new HashMap<String, Form>();
		formDBLookupMap = new HashMap<String, Form>();
		formEntityDBLookupMap = new HashMap<String, List<FormEntity>>();
	}

	/**
	 * constructor to init inf data tabels to cach map
	 */
	@PostConstruct
	public void setInfDataInCachMap() {

		initFormList("%");

		initFormEntityList("%");

		initCacheDataMap();		
	}

	private void initCacheDataMap() {
		for (Form form : formList) {
			if (form.getActive() != null && form.getUseCache() != null && form.getActive().equals("1")
					&& form.getUseCache().equals("1")) {
				cacheDataMap.put(form.getFormCode().toUpperCase(),
						formBuilderDao.getFromInfDataList(form.getFormCode()));
			}
		}
	}

	private void initFormList(String formCode) {
//		formList.clear();
//		formList.addAll(formBuilderDao.getForm("%", "%", true)); ->
		if(formCode.equals("%")) {
			formList.clear();
			formDBLookupMap.clear();
		} else {
			Collection<Form> formToDelete = new ArrayList<Form>();
			for (Form form : formList) {
				if(form.getFormCode().equals(formCode)) {
					formToDelete.add(form);
					break;
				}
			}
			formList.removeAll(formToDelete);
		}
		formList.addAll(formBuilderDao.getForm(formCode, "%", true));

		
		for (Form form : formList) {
			formMap.put(form.getFormCode(), form);
			formDBLookupMap.put(generalUtil.getNull(form.getFormCode()).toUpperCase(), form);
		}
	}

	private void initFormEntityList(String formCode) {
		long emptyStateKey = 0l;
//		formEntityList.clear();
//		formEntityList.addAll(formBuilderDao.getFormEntity("%", "%")); ->
		if(formCode.equals("%")) {
			formEntityList.clear();
			formEntityDBLookupMap.clear();
		} else {
			Collection<FormEntity> formEntityToDelete = new ArrayList<FormEntity>();
			for (FormEntity formEntity : formEntityList) {
				if(formEntity.getFormCode().equals(formCode)) {
					formEntityToDelete.add(formEntity);
				}
			}
			formEntityList.removeAll(formEntityToDelete);
		}
		formEntityList.addAll(formBuilderDao.getFormEntity(formCode, "%"));
		
		for (FormEntity formEntity : formEntityList) {
			//update formEntityDBLookupMap
			String formCodeUpperCase = generalUtil.getNull(formEntity.getFormCode()).toUpperCase();
			List<FormEntity> list_ = formEntityDBLookupMap.get(formCodeUpperCase);
			if(list_ == null) {
				list_ = new ArrayList<FormEntity>();
			}
			list_.add(formEntity);
			formEntityDBLookupMap.put(formCodeUpperCase, list_);
			
			if (formCode.equals("%") || formEntity.getFormCode().equals(formCode)) {
				@SuppressWarnings("unused")
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(formEntity.getEntityImpInit());
				} catch (JSONException e) {
					System.out.println("Error!!! the init json of the element=" + formEntity.toString() + " is invalid!");
				}
			}
		}
		
		// first iteration
		// * validate
		// * cache global element
		// * cache singleTone element  
		// * cache label index map 
		for (FormEntity formEntity : formEntityList) {
			try {
				if(!formCode.equals("%") && !formEntity.getFormCode().equals(formCode)) {
					continue;
				}
				
				//cache global element
				if(isGlobalInit(formEntity)) {
					try {
						Entity e = entityFactory.getEntity(formEntity.getEntityImpClass());
						e.init(emptyStateKey, formEntity.getFormCode(), formEntity.getEntityImpCode(), formEntity.getEntityImpInit());
						formEntity.setEntity(e);
						formEntity.setGlobalInit(true);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
				
				//cache singleTone Entity class
				if(!formCode.equals("%") || !formEntityClassSingleToneMap.containsKey(formEntity.getEntityImpClass())) {
					Entity e = entityFactory.getEntity(formEntity.getEntityImpClass());
					formEntityClassSingleToneMap.put(formEntity.getEntityImpClass(), e);
				}
				
				
				 

//				if(formEntity.getEntityImpClass().equals("ElementLabelImp")) {
//					 
//					String text = "";
//					try {
//						text = (String)jsonObject.get("text");
//					} catch (Exception e) {
//						// do nothing
//						
//					}
//					 
//					String elementName = "";
//					try {
//						elementName = (String)jsonObject.get("elementName");
//					} catch (Exception e) {
//						// do nothing
//					}
//					
////					if(!text.equals("") && !text.contains("$P{)") && !elementName.equals("") && !elementName.contains("$P{)")) {
////						formElementLabelMap.put((formEntity.getFormCode() + "." + elementName).toUpperCase(), generalUtil.getSpringMessagesByKey(text.replaceAll(" ", "_"), text));
////					}
//				}
				 
				//log it...
				StringBuilder sbFormCodeRef = new StringBuilder();
				String initString = formEntity.getEntityImpInit(); //\$P\{)(.*?)(\
				Pattern pattern = Pattern.compile("(\\$P\\{)(.*?)(\\})");
				Matcher matcher = pattern.matcher(initString);
				while (matcher.find()) { 
					String paramCode = matcher.group(0);
					if (paramCode.contains(".")) {
						String overrideFormCode = paramCode.split("\\.")[0].replace("$P{", "");
						String overrideParamValue = paramCode.replace(overrideFormCode +  ".","");
						sbFormCodeRef.append(overrideParamValue + ",");
					}
				}
//				System.out.println(jsonObject.toString() + (sbFormCodeRef.length() > 0 ? "sbFormCodeRef =" + sbFormCodeRef.toString() : ""));
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// second iteration
		// * clean and cache element display map
		// ...clean
		if(formCode.equals("%")) {
			formElementInfoAuditTrailMetaMap = new HashMap<String, ElementInfoAuditTrailMeta>();
		} else {
			List<String> removeList = new ArrayList<>();
			for (Map.Entry<String, ElementInfoAuditTrailMeta> entry : formElementInfoAuditTrailMetaMap.entrySet()) {
				if(entry.getKey().startsWith(formCode + ".") && !removeList.contains(entry.getKey())) {
					removeList.add(entry.getKey());
				}
			}
			for (String key : removeList) {
				formElementInfoAuditTrailMetaMap.remove(key);
			}
		}
		//...update
		for (FormEntity formEntity : formEntityList) {
			
			JSONObject jsonObject = new JSONObject(formEntity.getEntityImpInit());
			
			if(!formCode.equals("%") && !formEntity.getFormCode().equals(formCode)) {
				continue;
			}
			
			//cache element display map
			try {
				if(isInfoAuditTrailElement(formEntity) && (!formCode.equals("%") || !formElementInfoAuditTrailMetaMap.containsKey((formEntity.getFormCode() + "." + formEntity.getEntityImpCode()).toUpperCase()))) {
//					String label = formElementLabelMap.get((formEntity.getFormCode() + "." + formEntity.getEntityImpCode()).toUpperCase());
					ElementInfoAuditTrailMeta elementInfoAuditTrailMeta = new ElementInfoAuditTrailMeta(formEntity.getFormCode(), formEntity.getEntityImpCode(), formEntity.getEntityImpClass(), getDataType(jsonObject, formEntity), formEntity.getEntityLabel(), isParentPathId(formEntity.getFormCode()), isAdditionalData(jsonObject, formEntity), isHidden(jsonObject, formEntity), isSearchIdHolder(jsonObject, formEntity), isIdList(jsonObject, formEntity));
					formElementInfoAuditTrailMetaMap.put((formEntity.getFormCode() + "." + formEntity.getEntityImpCode()).toUpperCase(), elementInfoAuditTrailMeta);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		updateElementInfoAuditTrailMetaTmpTable();
	}

	private DataType getDataType(JSONObject jsonObject, FormEntity formEntity) {
		// TODO Auto-generated method stub
		DataType dataType = DataType.OTHER;
		String class_ = formEntity.getEntityImpClass();
		
		if(class_.equals("ElementInputImp")) {
			String type_ = (String)jsonObject.get("type");
			if(type_.equals("number")) {
				dataType = DataType.NUMBER;
			}
		}
		return dataType;
	}

	private boolean isInfoAuditTrailElement(FormEntity formEntity) {
		boolean toReturn  = false;
		try {
//			long emptyStateKey = 0l;
			Form form = formMap.get(formEntity.getFormCode());
//			if(!form.getFormCode().equals("Project")) {
//				return false;
//			}
			String formTypeName = form.getFormType();
			FormType fromType = FormType.valueOf(formTypeName);
			boolean isStructForm = fromType.getStructureForm();
			boolean isElement = formEntity.getEntityType().equals("Element");
			String class_ = formEntity.getEntityImpClass();
			boolean noInfElement =  (   
				class_.equals("ElementChemDoodleSearchImp") 
				|| class_.equals("ElementChemDoodleImp") 
				|| class_.startsWith("ElementWebix") // all ElementWebix elements
				|| class_.equals("ElementIreportImp")
				|| class_.equals("ElementLinkImp")
				|| class_.equals("ElementButtonImp") 
				|| class_.equals("ElementAuthorizationImp")
				|| class_.equals("ElementLabelImp"));
						
			if(isStructForm && isElement && !noInfElement) {
					JSONObject jsonObject = new JSONObject(formEntity.getEntityImpInit());
					boolean preventSave = false;
					try {
						preventSave = (boolean)jsonObject.get("preventSave");
					} catch (Exception e1) {
//						e1.printStackTrace();
					} 
					
					try {
						if(formEntity.getEntityImpClass().equals("ElementDataTableApiImp")) {
							if(jsonObject.has("role")) {
								String role = (String)jsonObject.get("role");
								preventSave = !generalUtil.getNull(role).equals("Multiple");
							} else {
								preventSave = true;
							}
						}
					} catch (Exception e1) {
//						e1.printStackTrace();
					} 
					
					if(!preventSave) {
						toReturn = true;
					}
//				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return toReturn;
	}

	private boolean isParentPathId(String formCode) {
		boolean toReturn = false;
		try {
			Form form = formMap.get(formCode);
			String formTypeName = form.getFormType();
			FormType fromType = FormType.valueOf(formTypeName);
			if(fromType.equals(FormType.ATTACHMENT) || fromType.equals(FormType.REF) || fromType.equals(FormType.SELECT)) {
				toReturn = true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toReturn;
	}
	
	private boolean isAdditionalData(JSONObject jsonObject, FormEntity formEntity) {
//		JSONObject jsonObject = new JSONObject(formEntity.getEntityImpInit());
		boolean additionalData = false;
		try {
			additionalData = (boolean)jsonObject.get("additionalData");
		} catch (Exception e1) {
//			e1.printStackTrace();
		} 
		return additionalData;
	}
	
	
	private boolean isHidden(JSONObject jsonObject, FormEntity formEntity) {
//		JSONObject jsonObject = new JSONObject(formEntity.getEntityImpInit());
		boolean isHidden = false;
		try {
			if(jsonObject.has("hideAlways")) {
				isHidden = (boolean)jsonObject.get("hideAlways");
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} 
		return isHidden;
	}
	
	private boolean isSearchIdHolder(JSONObject jsonObject, FormEntity formEntity) {
//		JSONObject jsonObject = new JSONObject(formEntity.getEntityImpInit());
		boolean searchId = false;
		try {
			if(jsonObject.has("searchId")) {
				searchId = (boolean)jsonObject.get("searchId");
			}
		} catch (Exception e1) {
//			e1.printStackTrace();
		} 
		return searchId;
	}
	
	private boolean isIdList(JSONObject jsonObject, FormEntity formEntity) {
//		JSONObject jsonObject = new JSONObject(formEntity.getEntityImpInit());
		boolean isIdList = false;
		
		try {
			boolean tablelistId = formEntity.getEntityImpClass().equals("ElementDataTableApiImp") && jsonObject.has("role") && ((String)jsonObject.get("role")).equals("Multiple");
			boolean ddlListId = formEntity.getEntityImpClass().equals("ElementAutoCompleteIdValDDLImp");
			boolean uomId = formEntity.getEntityImpClass().equals("ElementUOMImp");
			boolean searchId = jsonObject.has("searchId") && (boolean)jsonObject.get("searchId");
			if(tablelistId || ddlListId || uomId || searchId) {
				isIdList = true;
			}
		} catch (Exception e1) {
//			e1.printStackTrace();
		} 
		return isIdList;
	}


	private boolean isGlobalInit(FormEntity formEntity) {
		String class_ = formEntity.getEntityImpClass();
		return  useGlobalInitOnStart == 1 &&
				(class_.equals("ElementRichTextEditorImp")
				|| class_.equals("ElementTextareaImp")
				|| class_.equals("ElementTextareaAsClobImp")
				|| class_.equals("ElementAsyncIframeImp")
				|| class_.equals("ElementApiElementSetterImp")
				|| class_.equals("ElementChemDoodleImp")
				|| class_.equals("ElementChemDoodleSearchImp")
				|| class_.equals("ElementDynamicParamsImp")
//				|| class_.equals("ElementExcelSheetImp")
				|| class_.equals("ElementInputImp")
//				|| TODO ddl ElementIreportImp
				// TODO ddl elements solve last value that is class var
				|| class_.equals("ElementLinkImp")
				|| class_.equals("ElementParamMonitoringImp")
				|| class_.equals("ElementButtonImp")
				|| class_.equals("ElementAsyncIframeImp")
				|| class_.equals("ElementDataTableApiImp")
				|| class_.equals("CatalogDBTableImp")
				|| class_.equals("CatalogCSVListImp")
				|| class_.equals("LayoutDesignHtmlImp")
				|| class_.equals("ElementRadioImp")
				|| class_.equals("ElementAuthorizationImp")
				|| class_.equals("ElementLabelImp"))
				&& !formEntity.getEntityImpInit().contains("$P{");
	}

	/**
	 * Get all data from cach map that has the conditions parameters like(select * from ...where)
	 * 
	 * @param formCode
	 * @param lookupType
	 * @param lookupval
	 * @return
	 */
	public Map<String, String> getInfDataFromCachMap(String formCode, LookupType lookupType, String lookupval) {
		formCode = formCode.toUpperCase();

		List<InfData> form = cacheDataMap.get(formCode);
		String id = "";
		String name = "";
		String[] attributes = new String[form.get(0).getAttributes().names().length()];
		Map<String, String> returnData = new HashMap<String, String>();

		for (InfData infData : form) { 
			if (lookupType.getTypeName().equals("ID") && !generalUtil.getNull(infData.getId()).equals("") && generalUtil.getNull(infData.getId()).equals(lookupval)) {
				returnData = convertObjectToMap(id, name, attributes, infData);
			}
			if (lookupType.getTypeName().equals("NAME") && !generalUtil.getNull(infData.getName()).equals("") && generalUtil.getNull(infData.getName()).equals(lookupval)) {
				returnData = convertObjectToMap(id, name, attributes, infData);
			} 
		}

		return returnData;
	}

	/**
	 * Get all data from cach map that has the conditions parameters like(select * from ...where)
	 * 
	 * @param formCode
	 * @param lookupType
	 * @param lookupval
	 * @return
	 */
	public List<Map<String, String>> getInfDataFromCachMapLikeVal(String formCode, LookupType lookupType,
			String lookupval) {
		formCode = formCode.toUpperCase();

		List<InfData> form = cacheDataMap.get(formCode);
		String id = "";
		String name = "";
		List<Map<String, String>> returnAllData = new ArrayList<Map<String, String>>();

		for (InfData infData : form) {
			String[] attributes = new String[form.get(0).getAttributes().names().length()];
			if (lookupType.getTypeName().equals("ID")
					&& (lookupval.equals("%") || generalUtil.getNull(infData.getId()).endsWith(lookupval.replace("%", "")))) {//fixed bug in lookup.replaced contains with endsWith

				returnAllData.add(convertObjectToMap(id, name, attributes, infData));
			}
			if (lookupType.getTypeName().equals("NAME")
					&& (lookupval.equals("%") || generalUtil.getNull(infData.getName()).endsWith(lookupval.replace("%", "")))) {//fixed bug in lookup.replaced contains with endsWith

				returnAllData.add(convertObjectToMap(id, name, attributes, infData));
			}
		}

		return returnAllData;
	}

	public Map<String, String> convertObjectToMap(String id, String name, String[] attributes, InfData infData) {
		Map<String, String> infDataMap = new HashMap<String, String>();
		infDataMap.put("ID", id += id.equals("") ? infData.getId() : "," + infData.getId());
		infDataMap.put("NAME", name += name.equals("") ? infData.getName() : "," + infData.getName());
		for (int i = 0; i < infData.getAttributes().names().length(); i++) {
			if (infData.getAttributes().names().getString(i).equals("FORMCODE") || attributes[i] == null) {
				infDataMap.put(infData.getAttributes().names().getString(i).toUpperCase(), attributes[i] = infData
						.getAttributes().get(infData.getAttributes().names().getString(i)).toString());
			} else {
				infDataMap.put(infData.getAttributes().names().getString(i).toUpperCase(), attributes[i] += ","
						+ infData.getAttributes().get(infData.getAttributes().names().getString(i)).toString());
			}
		}
		return infDataMap;
	}

	/**
	 * Check if form is caching
	 * 
	 * @param formCode
	 * @return
	 */
	public boolean isCaching(String formCode) {
		formCode = formCode.toUpperCase();
		return cacheDataMap.containsKey(formCode);
	}

	/**
	 * Get list of data inf according to column parameter like(select "column" form ...)
	 * 
	 * @param formCode
	 * @param column
	 * @return
	 */
	public List<String> getColumnsList(String formCode, String column) throws Exception {
		formCode = formCode.toUpperCase();
		String columnTofunctionName = column.substring(0, 1).toUpperCase() + column.substring(1).toLowerCase();

		List<String> ColumnsData = new ArrayList<String>();
		List<InfData> form = cacheDataMap.get(formCode);
		for (InfData infData : form) {

			if (column.equalsIgnoreCase("ID") || column.equalsIgnoreCase("NAME")) {
				ColumnsData.add((InfData.class.getMethod("get" + columnTofunctionName).invoke(infData)).toString());
			} else {
				ColumnsData.add(infData.getAttributes().getString(column.toUpperCase()));
			}

		}
		return ColumnsData;
	}

	/**
	 * Get single element from cach map according to parameters like(select "elementName" from...where...)
	 * 
	 * @param elementName
	 * @param formCode
	 * @param lookupType
	 * @param lookupval
	 * @return
	 * @throws Exception
	 */
	public String getInfDataStringFromCachMap(String elementName, String formCode, LookupType lookupType,
			String lookupval) throws Exception {
		formCode = formCode.toUpperCase();

		String returnData = "";
		String columnTofunctionName = lookupType.getTypeName().substring(0, 1).toUpperCase()
				+ lookupType.getTypeName().substring(1).toLowerCase();
		List<InfData> form = cacheDataMap.get(formCode);
		for (InfData infData : form) {

			if ((InfData.class.getMethod("get" + columnTofunctionName).invoke(infData)).toString().equals(lookupval)) {
				if (elementName.equalsIgnoreCase("ID") || elementName.equalsIgnoreCase("NAME")) {
					String elementNameToFunction = elementName.substring(0, 1).toUpperCase()
							+ elementName.substring(1).toLowerCase();
					returnData = (InfData.class.getMethod("get" + elementNameToFunction).invoke(infData)).toString();
				} else {
					returnData = infData.getAttributes().getString(elementName.toUpperCase());
				}

			}
		}
		return returnData;
	}

	/**
	 * Get List of element from according to parameters like(select "elementName" from...where...)
	 * 
	 * @param elementName
	 * @param formCode
	 * @param lookupType
	 * @param lookupval
	 * @return
	 * @throws Exception
	 */
	public List<String> getInfDataListFromCachMap(String elementName, String formCode, LookupType lookupType,
			String lookupval) throws Exception {
		formCode = formCode.toUpperCase();
		elementName = elementName.toUpperCase();

		List<String> returnData = new ArrayList<String>();
		String returnDataString = "";
		String columnTofunctionName = lookupType.getTypeName().substring(0, 1).toUpperCase()
				+ lookupType.getTypeName().substring(1).toLowerCase();
		List<InfData> form = cacheDataMap.get(formCode);
		for (InfData infData : form) {

			if ((InfData.class.getMethod("get" + columnTofunctionName).invoke(infData)).toString().equals(lookupval)) {
				if (elementName.equalsIgnoreCase("ID") || elementName.equalsIgnoreCase("NAME")) {
					String elementNameToFunction = elementName.substring(0, 1).toUpperCase()
							+ elementName.substring(1).toLowerCase();
					returnDataString = (InfData.class.getMethod("get" + elementNameToFunction).invoke(infData))
							.toString();
				} else {
					returnDataString = infData.getAttributes().getString(elementName.toUpperCase());
				}
				returnData.add(returnDataString);

			}
		}
		return returnData;
	}

	/**
	 * 
	 * update cache map if there is change in db
	 * 
	 * @param formCode
	 */
	public void setCacheOnFormDataChange(String formCode) {
		formCode = formCode.toUpperCase();
		if (isCaching(formCode)) {
			cacheDataMap.remove(formCode);
			cacheDataMap.put(formCode,formBuilderDao.getFromInfDataList(formCode));
		}
	}

	public void setCacheOnFormBuilderChange(String formCode) {
		initFormList(formCode);
		initFormEntityList(formCode);
		setCacheOnFormDataChange(formCode.toUpperCase());
	}
	
	public Map<String, Entity> getFormEntityClassSingleToneMap() {
		return formEntityClassSingleToneMap;
	}
	
	public Map<String, ElementInfoAuditTrailMeta> getFormElementInfoAuditTrailMetaMap() {
		return formElementInfoAuditTrailMetaMap;
	}

	public void updateElementInfoAuditTrailMetaTmpTable() {
		Map<String, ElementInfoAuditTrailMeta> formElementDisplayMap =  getFormElementInfoAuditTrailMetaMap();
		formBuilderDao.updateElementInfoAuditTrailMetaTmpTable(formElementDisplayMap);
	}
}