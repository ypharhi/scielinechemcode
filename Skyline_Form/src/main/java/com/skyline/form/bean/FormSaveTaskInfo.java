package com.skyline.form.bean;

import java.util.List;
import java.util.Map;

public class FormSaveTaskInfo {

	private Form form;
	private String formId;
	private String sessionId;
	private String pivotTable;
	private String isNew;
	private List<String> tempPivotTableHolder;
	private String userId;
	private Map<String, String> elementValueInfATMap;
	private Map<String, ElementUIKeyValueDisplay> elementUIKeyValueDisplayMap;
	private String auditTrailChangeType;
	private String dbTransactionId;

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getPivotTable() {
		return pivotTable;
	}

	public void setPivotTable(String pivotTable) {
		this.pivotTable = pivotTable;
	}

	public String getIsNew() {
		return isNew;
	}

	public void setIsNew(String isNew) {
		this.isNew = isNew;
	}

	public List<String> getTempPivotTableHolder() {
		return tempPivotTableHolder;
	}

	public void setTempPivotTableHolder(List<String> tempPivotTableHolder) {
		this.tempPivotTableHolder = tempPivotTableHolder;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Map<String, String> getElementValueInfATMap() {
		return elementValueInfATMap;
	}

	public void setElementValueInfATMap(Map<String, String> elementValueInfATMap) {
		this.elementValueInfATMap = elementValueInfATMap;
	}

	public Map<String, ElementUIKeyValueDisplay> getElementUIKeyValueDisplayMap() {
		return elementUIKeyValueDisplayMap;
	}

	public void setElementUIKeyValueDisplayMap(Map<String, ElementUIKeyValueDisplay> elementUIKeyValueDisplayMap) {
		this.elementUIKeyValueDisplayMap = elementUIKeyValueDisplayMap;
	}

	public String getAuditTrailChangeType() {
		return auditTrailChangeType;
	}

	public void setAuditTrailChangeType(String auditTrailChangeType) {
		this.auditTrailChangeType = auditTrailChangeType;
	}

	public String getDbTransactionId() {
		return dbTransactionId;
	}

	public void setDbTransactionId(String dbTransactionId) {
		this.dbTransactionId = dbTransactionId;
	}
}
