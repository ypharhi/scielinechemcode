package com.skyline.form.bean;

public class ElementInfoAuditTrailDisplay {
	
	private String value;
	private String updateJobFlag;
	private String label;
	private String pathId;
	private String tableType;
	private boolean isIdList;
	
	public ElementInfoAuditTrailDisplay(String value, String updateJobFlag) {
		this.value = value;
		this.updateJobFlag = updateJobFlag;
	}

	public String getUpdateJobFlag() {
		return updateJobFlag;
	}
	public void setUpdateJobFlag(String updateJobFlag) {
		this.updateJobFlag = updateJobFlag;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getPathId() {
		return pathId;
	}

	public void setPathId(String pathId) {
		this.pathId = pathId;
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public boolean isIdList() {
		return isIdList;
	}

	public void setIdList(boolean isIdList) {
		this.isIdList = isIdList;
	} 
}
