package com.skyline.form.bean;

public enum LevelType {
	
	DEBUG("Debug"), INFO("Info"), ERROR("Error"), WARN("Warn"), SYSTEM_ERROR("System Error"), Other("Other"), ASPECT_EXCEPTION("Aspect Exception");
	
	private String typeName; 
	
	private LevelType(String s) {
		typeName = s;
	}

	public String getTypeName() {
		return typeName;
	}

}