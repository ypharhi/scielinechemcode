package com.skyline.form.dal;

import java.util.List;
import java.util.Map;

import com.skyline.form.bean.ElementUIKeyValueDisplay;
import com.skyline.form.bean.Form;

public interface GeneralTaskDao {

	void updateMVByPivotTable(String formType, String table, String formCode, int contextType, String eventContextCode,
			String auditTrailChangeType);

	String doSaveInfoAndAuditTrail(Form form, String formId, String userId, Map<String, String> elementValueMap,
			Map<String, ElementUIKeyValueDisplay> elementUIKeyValueDisplayMap, String auditTrailChangeType,
			String dbTransactionId);

	void updateCach(Form form);

	void onTransactionFailure(String formCode, String formId, String dbTransaction);

	String correctFgSeqTableFormCode(Form form, String formId);

	void exeNotificationEvent(String formId, String formCodeEntity);

	void updateInfoAndAuditTrailDeletion(List<String> deleteFormIdList);

	String updateSingleString(String string);
}
