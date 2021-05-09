package com.skyline.form.dal;

import java.util.List;
import java.util.Map;

import com.skyline.form.bean.Form;
import com.skyline.form.bean.FormAdditionalData;
import com.skyline.form.bean.FormEntity;
import com.skyline.form.bean.FormLastSaveValue;
import com.skyline.form.bean.FormType;
import com.skyline.form.bean.LookupType;

public interface FormDao { //TODO add CacheImp implementation
 
//	public Map<String, String> getLastFormSaveValue(String formCode, String formId, String userId, String nameId,
//			String paraentId, boolean isNewFormId, boolean isClone, boolean isStruct, String formCodeEntity, String sessionId); 
	
	public List<String> getTreeData(String metaData, String table, String wherePart, String path);
	
	public String getFormsId(String table); 
   
	public String getCurrentFormNumberId(String table, String wherePart);
 
	public String getFormNumberIdByFormId(String table, String formId);
 
	boolean isNewFormId(String formCodeEntity, String formId); 
	
	public String getFormCodeBySeqId(String SeqId);
	
	public String getFormCodeBySeqIdNoException(String formSeqId);
	
	public String getFormCodeEntityBySeqId(String formCode, String SeqId);
  
	public String getFormParentId(String formCode, String formId);
	
	public Map<String,String> getFormElementCaseSensitiveName(String formCode);

	Map<String, String> getUserInfoMap(String userId);
	
	public String getWherePartForTmpData(String sessionId, String formCode, String parentId);
	
	public String getWherePartForTmpDataByFormId(String sessionId, String formCode, String parentId);
	
	/**
	 * 
	 * @param formId - remove the formId from the DB - can be only use by system in develop mode
	 */
	public String removeFromDB(String formId, String formCodeEntity);
	
	/**
	 * 
	 * @param formCode - can be empty
	 * @param lookupType - "id","name"
	 * @param lookupval - the value of the lookupType type
	 * @param elementName - the element that we look for
	 * @return elementName value
	 * in case of exception or more than one row return empty string
	 */
	public String getFromInfoLookup(String formCode, LookupType lookupType, String lookupval, String elementName);
	
	/**
	 * 
	 * @param formCode - can be empty
	 * @param lookupType - "id","name"
	 * @param lookupval - the value of the lookupType type
	 * @return all elements from *_inf_v
	 * in case of exception or more than one row return empty string
	 */
	public Map<String, String> getFromInfoLookupAll(String formCode, LookupType lookupType, String lookupval);
	
	public List<Map<String, String>> getFromInfoLookupAllContainsVal(String formCode, LookupType lookupType, String lookupval);
	/**
	 * 
	 * @param formCode - not empty
	 * @param lookupType - "id","name"
	 * @param elementName - the element that we look for
	 * @return map with lookupType as key and element as value
	 */
	public Map<String, String>  getFromInfoLookupAllElementData(String formCode, LookupType lookupType, String elementName);
	
	public List<String> getFromInfoLookupElementData(String formCode, LookupType lookupType, String lookupval, String elementName);
 
	public List<FormEntity> getFormEntityInfoLookup(String formCode, String type);

	public List<Form> getFormInfoLookup(String formCode, String string, boolean b);

	Map<String, String> getFormElementValuesMap(String formId, String formCode);

//	public Map<String,String> getStructFormLastSaveValueMap(String sql_,
//			Map<String, String> formEntityImpcodeInsensitiveMap);

	public List<FormAdditionalData> getFormLastSaveAdditionalDataList(String sql_);

	public List<FormLastSaveValue> getFormLastSaveValueList(String sql_);
	
	public void insertToSelectTable(String selectFormCode, String selectParentId, String selectColumn,
			List<String> itemsToSelect, boolean isListDisabled, String userId, String sessionId);

	public Map<String, String> getLastFormDataMap(String formCode, String formId, FormType formType);

//	Map<String, String> getFormEntityImpcodeCaseInsensitiveMap(String formCode);
	
}


