package com.skyline.form.service;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import org.json.JSONObject;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Service;

@Service
public class SpringNameDictionaryImp extends ResourceBundleMessageSource implements SpringNameDictionary {

	private String dictionary; // string of all of the messages in the formMessages.properties
	private JSONObject dictionaryJSONObject;
	
	public SpringNameDictionaryImp() {
		initNameDictionary();
	}
	
	private void initNameDictionary() {
		dictionaryJSONObject = new JSONObject();
		ResourceBundle resource = getResourceBundle("formDictionary", Locale.ENGLISH);
		Enumeration<String> keys = resource.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			dictionaryJSONObject.put(key, resource.getString(key));
		}
		dictionary = dictionaryJSONObject.toString();
	} 
	
	@Override
	public JSONObject getDictionaryJson() {
		// TODO Auto-generated method stub
		return dictionaryJSONObject;
	}

	@Override
	public String getDictionary() {
		// TODO Auto-generated method stub
		return dictionary;
	}
	
	@Override
	public void reloadDictionary() {
		initNameDictionary();
	}

}
