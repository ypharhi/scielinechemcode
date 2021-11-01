package com.skyline.form.dal;

import java.util.List;
import java.util.Map;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.Form;

public interface FormSaveDao {

	String doSaveStruct(Form form, String userId, String formId, Map<String, String> elementValueMap, String table,
			String sessionId, String isNew, String lastChangeUserId, String lastChangeDate);

//	String doSaveFormLastSaveValues(String formCode, String formId, String userId, Map<String, String> elementValueMap,
//			String sessionId, String saveName, String useLoginsessionidScopeFlag, String description);
	
	String doSaveDropAndCreatePivot(String formCode, String formId, String userId, Map<String, String> elementValueMap,
			String sessionId, String saveName, String useLoginsessionidScopeFlag, String description);

	String doSaveFormLastSaveValues(String formCode, String formId, String userId, Map<String, String> elementValueMap, String saveName,
			String useLoginsessionidScopeFlag, String description);

	String createStructPivotTable(String formCode, String formId); //, String pivotTableName

	String doSaveTmpDataProduction(String formCode, String formId, String sessionId, String userId);

	String doRemoveProduction(String formCode, String formId, String userId);

	String doRemoveTmpDataProduction(String formCode, String formId, String sessionId, String userId);

	public String cloneStructTable(String formId);
	
	public String cloneStructTable(String formId,String cloneWherepart);

	public String cloneStructTable(String formId, Map<String, String> replaceFieldsMap,String cloneWherepart);
	
	public String cloneStructTable(String formId, Map<String, String> replaceFieldsMap, Map<String, String> columns,String cloneWherepart);

	String getStructFormId(String formCode);
	
	String getStructFormId(String formCode, String parentFormId);

	String getStructFileId(String formCode, String formId);

	String updateSingleStringInfo(String sql);

	String updateSingleStringInfoNoTryCatch(String sql);

	String updateStructTableByFormId(String sql, String table, List<String> colList, String formId);

	String updateStructTable(String sql, String table, List<String> colList, String whereCol, String WhereId);

	String deleteStructTable(String sql, String table, String delCol, String delColId);

	String deleteStructTableByFormId(String sql, String table, String whereFormId);

	String insertStructTableByFormId(String sql, String table, String whereFormId);

	String updateStructTableFormCode(String formCodeEntity, String formCode, String formId, boolean updatePivotTable);

	String saveAdditionalData(Map<String, String> elementValueMap, List<DataBean> additinalDataSaveList,
			String formCode, String formId);

	String updateAdditinalData(String sql, List<String> colList, String whereFormId);
	
	ActionBean doSaveOnException(Exception e, String formId, String formCode);

	String doRemove(String formCode, String formId, String userId);
}
