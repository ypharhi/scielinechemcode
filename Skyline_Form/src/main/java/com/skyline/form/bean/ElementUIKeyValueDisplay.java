package com.skyline.form.bean;

public class ElementUIKeyValueDisplay {
	private String key;
	private String originValue;
	private String uiDisplayValue; 
	
	public ElementUIKeyValueDisplay(String key, String originValue, String uiDisplayValue) {
		super();
		this.key = key;
		this.originValue = originValue;
		this.uiDisplayValue = uiDisplayValue;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getOriginValue() {
		return originValue;
	}
	public void setOriginValue(String originValue) {
		this.originValue = originValue;
	}
	public String getUiDisplayValue() {
		return uiDisplayValue;
	}
	public void setUiDisplayValue(String uiDisplayValue) {
		this.uiDisplayValue = uiDisplayValue;
	}

}
