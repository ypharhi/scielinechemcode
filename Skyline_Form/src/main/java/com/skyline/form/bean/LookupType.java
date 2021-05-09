package com.skyline.form.bean;

public enum LookupType {
	
	ID("ID"), NAME("NAME");
	
	private String typeName; 
	
	private LookupType(String s) {
		typeName = s;
	}

	public String getTypeName() {
		return typeName;
	}

}