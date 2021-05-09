package com.skyline.form.bean;

import org.json.JSONObject;

public class SearchTreeCurrentData {

	String id;
	String text;
	String formcode_entity;
	JSONObject adhoc_json;
	
	public SearchTreeCurrentData(String id, String text, String formcode_entity, JSONObject adhoc_json) {
		super();
		this.id = id;
		this.text = text;
		this.formcode_entity = formcode_entity;
		this.adhoc_json = adhoc_json;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getFormcode_entity() {
		return formcode_entity;
	}
	public void setFormcode_entity(String formcode_entity) {
		this.formcode_entity = formcode_entity;
	}
	public JSONObject getAdhoc_json() {
		return adhoc_json;
	}
	public void setAdhoc_json(JSONObject adhoc_json) {
		this.adhoc_json = adhoc_json;
	}
 
}
