package com.skyline.form.bean;

public class FormAdditionalData {
	
	public FormAdditionalData(Long id, String parentId, String entityImpCode, String value, String config_id,
			String formcode, String info) {
		super();
		this.id = id;
		this.parentId = parentId;
		this.entityImpCode = entityImpCode;
		this.value = value;
		this.config_id = config_id;
		this.formcode = formcode;
		this.info = info;
	}
	
	private Long id;
	private String parentId;
	private String entityImpCode;
	private String value;
	private String config_id;
	private String formcode;
	private String info; 
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getParentId() {
		return parentId;
	}
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	public String getEntityImpCode() {
		return entityImpCode;
	}
	public void setEntityImpCode(String entityImpCode) {
		this.entityImpCode = entityImpCode;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getConfig_id() {
		return config_id;
	}
	public void setConfig_id(String config_id) {
		this.config_id = config_id;
	}
	public String getFormcode() {
		return formcode;
	}
	public void setFormcode(String formcode) {
		this.formcode = formcode;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	} 
}
