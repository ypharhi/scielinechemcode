package com.skyline.form.bean;

import org.json.JSONObject;

public class InfData {
	String name;
	JSONObject attributes;
	String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public JSONObject getAttributes() {
		return attributes;
	}

	public void setAttributes(JSONObject attributes) {
		this.attributes = attributes;
	}

	public InfData() {
	};

	public InfData(String id, String name, JSONObject attributes) {
		this.id = id;
		this.name = name;
		this.attributes = attributes;
	}
}
