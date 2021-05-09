package com.skyline.form.service;

import org.json.JSONObject;

/**
 * SpringNameDictionary holds name adjustments for various purposes (originally added for report design name settings and table column adjustment) 
 * @author YPharhi
 *
 */
public interface SpringNameDictionary {
	JSONObject getDictionaryJson();
	String getDictionary();
	void reloadDictionary();
}
