package com.skyline.form.bean;

public enum CatalogInfoType {

	COUNT("COUNT"), FIRST_VALUE("FIRST_VALUE"), LAST_VALUE("LAST_VALUE"), EXISTS("EXISTS");

	private String typeName;

	private CatalogInfoType(String s) {
		typeName = s;
	}

	public String getTypeName() {
		return typeName;
	}

}
