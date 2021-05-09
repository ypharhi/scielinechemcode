package com.skyline.form.bean;

import java.io.Serializable;

public class NavigationBean implements Serializable {

	@Override
	public String toString() {
		return "NavigationBean [formId=" + formId + ", formCode=" + formCode + ", tab=" + tab + ", url=" + url
				+ ", info=" + info + ", stateKey=" + stateKey + "]";
	}

	private String formId;
	private String formCode;
	private String tab;
	private String url;
	private String info;
	private long stateKey;

	public NavigationBean() {

	}

	public NavigationBean(long stateKey, String formId, String formCode, String tab, String info, String url) {
		this.formId = formId;
		this.formCode = formCode;
		this.tab = tab;
		this.setUrl(url);
		this.info = info;
		this.stateKey = stateKey;
	}

	public long getStateKey() {
		return stateKey;
	}

	public void setStateKey(long stateKey) {
		this.stateKey = stateKey;
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

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
