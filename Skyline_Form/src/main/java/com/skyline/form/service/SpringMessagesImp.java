package com.skyline.form.service;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import org.json.JSONObject;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

@Service
public class SpringMessagesImp extends ResourceBundleMessageSource implements SpringMessages {

	private String messages; // string of all of the messages in the formMessages.properties
	private JSONObject messagesJSONObject; // JSONObject of all of the messages in the formMessages.properties	and in the form.properties	

	/**
	 * get all messages from messages properties
	 */
	public SpringMessagesImp() {
		initSpringMessages();
	}

	/**
	 * return string of all messages (toSring of JSONObject)
	 */
	@Override
	public String getMessages() {
		return messages;
	}
	
	@Override
	public JSONObject getMessagesJson() {
		// TODO Auto-generated method stub
		return messagesJSONObject;
	}

	/**
	 * return value (message) by key
	 */
	@Override
	public String getSpringMessagesByKey(String key, String defaultValue) {
		if (messagesJSONObject.has(key)) {
			return messagesJSONObject.get(key).toString().replace("//upperComma//", "'");
		}
		return (defaultValue.equals("")) ? key : defaultValue;
	}

	@Override
	public void reloadMessages() {
		initSpringMessages();
	}
	
	private void initSpringMessages() {
		messagesJSONObject = new JSONObject();
		ResourceBundle resource = getResourceBundle("formMessages", Locale.ENGLISH);
		Enumeration<String> keys = resource.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			messagesJSONObject.put(key, resource.getString(key));
		}
		messages = messagesJSONObject.toString();

		resource = getResourceBundle("form", Locale.ENGLISH);
		keys = resource.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			messagesJSONObject.put(key, resource.getString(key));
		}
	}

	

}
