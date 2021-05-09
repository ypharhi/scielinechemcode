package com.skyline.form.service;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.skyline.form.bean.FormType;

public interface IntegrationDT {

	JSONObject onElementDataTableApiChange(String formId, long stateKey, String struct, FormType structFormType,
			String criteria, String display, String linkToLastSelection, String formCode, String tableType,
			String sourceElementImpCode, String hideEmptyColumns, String permissionSqlList, StringBuilder sqlInfo,
			List<String> unfilteredList, String lastMultiValues, boolean updateMultiValues, String followingHiddenCol);

	JSONObject onLevelSelectedChange(String struct, String formCode, String displayCatalog, String elementCode);

	String getrCiteriaWherePart(long stateKey, String struct, String idName, String criteria, String userId,
			String formCode, List<String> unfilteredList, String lastMultiValues);

	Map<String, String> getUserInfoMap(String userId);

	/**
	 * 
	 * @param struct
	 * @param formId
	 * @param userId
	 * @param rowId
	 * @return empty string or NA if no error, in case of NA we mark to the call function that we need to avoid DB remove (only remove the line in UI) 
	 */
	String checkRemove(String struct, String formId, String userId, String rowId);

	String checkRemoveConfirm(String struct, String formId, String userId, String action, StringBuilder errorMessage);

	String onChangeDataTableCell(long stateKey, String parentFormCode, String formId, String formCode,
			String onChangeFormId, String userId, String onChangeColumnName, String onChangeColumnVal, String saveType,
			String formNumberId, String oldVal) throws Exception;

	String dataTableAddRow(long stateKey, String formCode, String formId, String userId, String parentFormCode,
			String domId, Map<String, String> elementValueMap, int rowNumToAdd, String tableType) throws Exception;

	String getRichTextContent(String parentID, String formCode, String dbColName);
	
	List<Map<String, String>> customCriteriaList(String elementCode, String struct, long stateKey, List<Map<String, String>> sqlPoolMapList, Map<String, String> parentMap, Map<String, String> currentFormMap);

	JSONObject customerDTDisplayViewList(String fromcode, String elementCode, String struct, String displayCatalogItemDefaulValue, String lastDTView, boolean isLevelChange);

	String customerDTDefaultHiddenColumns(String formCode, String impCode, String struct);
}