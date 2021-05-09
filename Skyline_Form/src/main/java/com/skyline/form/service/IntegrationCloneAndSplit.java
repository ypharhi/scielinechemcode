package com.skyline.form.service;

import java.util.Map;

public interface IntegrationCloneAndSplit {

	void postCloneSaveEvent(String formId, String cloneFormId);

	String MultiCloneSaveEvent(String formId, String cloneQuantity);

	String splitSaveEvent(String formId, String currentQuantity, String splitQuantity, String splitQuantityUom)
			throws Exception;

	Map<String, String> cloneRemoveFields(String formId, String cloneFormId);

	String postSplitSaveEvent(String formId, String cloneFormId, String currentQuantity, String splitQuantity,
			String splitQuantityUom) throws Exception;

	/**
	 * 
	 * @param formCode
	 * @param parentId
	 * @return the new formId cloning or "-1" if no cloning was made (because of USEASDEFAULTDATA rules or error) in that case the form will treated as new form
	 */
	String doCloneBySaveDefaultData(String formCode, String parentId);

	/**
	 * 
	 * @param formCode
	 * @param parentFormCode
	 * @param parentId
	 * @return the Default form id under the parentId (parent formid) that is used as default data (has USEASDEFAULTDATA element marked under the parentId scope) or empty string in case there is not such a form) 
	 */
	String getDefaultDataFormId(String formCode, String parentFormCode, String parentId);

}