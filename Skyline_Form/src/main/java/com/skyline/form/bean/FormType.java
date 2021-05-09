package com.skyline.form.bean;


/**
 * The models in Skyline in which the form is being used)
 * @author YPharhi
 *
 */
public enum FormType {

	GENERAL("GENERAL", false), 
//	LABEL("LABEL"),
	
	REPORT("REPORT", false),
	STRUCT("STRUCT", true),
	ATTACHMENT("ATTACHMENT", true),
	MAINTENANCE("MAINTENANCE", true),
	SMARTSEARCH("SMARTSEARCH", false),
	SELECT("SELECT", true),
	REF("REF", true),
	INVITEM("INVITEM", true);
	

	private String typeName;
	private boolean structureForm;

	private FormType(String typeName, boolean structureForm) {
		this.typeName = typeName;
		this.structureForm = structureForm;
	}

	public String getTypeName() {
		return typeName;
	}
	
	public boolean getStructureForm() {
		return structureForm;
	}

}
