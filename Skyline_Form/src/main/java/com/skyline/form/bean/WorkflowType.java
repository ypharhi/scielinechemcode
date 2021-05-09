package com.skyline.form.bean;

public enum WorkflowType {
	NEW("NEW"), STATUS("STATUS");

	private String typeName;

	private WorkflowType(String s) {
		typeName = s;
	}

	public String getWfTypeName() {
		return typeName;
	}
}
