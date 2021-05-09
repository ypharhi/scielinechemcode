package com.skyline.form.bean;

public class RedirectInfo {
	private String formId;
	private String formCode;
	private String tab;
	private String appendInfo;
	private Boolean isNew;

	public RedirectInfo(String formId, String formCode, String tab, Boolean isNew) {
		this.setFormCode(formCode);
		this.setFormId(formId);
		this.setTab(tab);
		this.setAppendInfo("");
		this.isNew = isNew;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getFormCode() {
		return formCode;
	}

	public void setFormCode(String formCode) {
		this.formCode = formCode;
	}

	public String getTab() {
		return tab;
	}

	public void setTab(String tab) {
		this.tab = tab;
	}

	public String getAppendInfo() {
		return appendInfo;
	}

	public void setAppendInfo(String appendInfo) {
		this.appendInfo = appendInfo;
	}

	public Boolean isNew() {
		return isNew;
	}

	public void setNew(Boolean isNew) {
		this.isNew = isNew;
	}
}
