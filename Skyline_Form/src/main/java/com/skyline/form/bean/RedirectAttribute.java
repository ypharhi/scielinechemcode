package com.skyline.form.bean;

public class RedirectAttribute {
	private String stateKey;
	private String openInEdit;
	private String urlCallParam;
	private String urlPrintParam;
	private Boolean isNew = null;
	public RedirectAttribute(String stateKey, String openInEdit, String urlCallParam, String urlPrintParam, Boolean isNew) {
		super();
		this.stateKey = stateKey;
		this.openInEdit = openInEdit;
		this.urlCallParam = urlCallParam;
		this.urlPrintParam = urlPrintParam;
		this.isNew = isNew;
	}

	public String getStateKey() {
		return stateKey;
	}

	public void setStateKey(String stateKey) {
		this.stateKey = stateKey;
	}

	public String getOpenInEdit() {
		return openInEdit;
	}

	public void setOpenInEdit(String openInEdit) {
		this.openInEdit = openInEdit;
	}

	public String getUrlCallParam() {
		return urlCallParam;
	}

	public void setUrlCallParam(String urlCallParam) {
		this.urlCallParam = urlCallParam;
	}

	public Boolean isNew() {
		return isNew;
	}

	public void setNew(Boolean isNew) {
		this.isNew = isNew;
	}

	public String getUrlPrintParam() {
		return urlPrintParam;
	}

	public void setUrlPrintParam(String urlPrintParam) {
		this.urlPrintParam = urlPrintParam;
	}
}
