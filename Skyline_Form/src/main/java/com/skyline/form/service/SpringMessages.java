package com.skyline.form.service;

import org.json.JSONObject;

public interface SpringMessages {
	String getMessages();	
	String getSpringMessagesByKey(String key, String defaultValue);
	JSONObject getMessagesJson();	
	void reloadMessages();
}
