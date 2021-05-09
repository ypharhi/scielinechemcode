package com.skyline.form.bean;


/**
 * TODO arrange it after the implementation in adama
 *
 */
public enum DataType {
	TEXT("TEXT",""),
	NUMBER("STRUCT",""),
	FILE("ATTACHMENT",""),
	ID("MAINTENANCE",""),
	DATE("SMARTSEARCH",""),
	PASSWORD("SELECT",""),
	OTHER("OTHER","");
	
	private String typeName;

	private DataType(String typeName, String info) {
		this.typeName = typeName;
	}

	public String getTypeName() {
		return typeName;
	}
}
