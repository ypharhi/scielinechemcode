package com.skyline.form.service;

import java.util.Map;

public interface IntegrationWF
{

	/**
	 * 
	 * @param formCode
	 * @param userId
	 * @param formId
	 * @param isNewFormId
	 * @param formParam
	 * @return map of various parameters and there values (every implementation is free to return a map according the form needs)
	 * Note: in case of failure the function will return the expected parameters with default value that will not effect the rest of the screen / from
	 */
	Map<String, String> getFormWFStateGeneral(String formCode, String userId, String formId, boolean isNewFormId, Map<String, String> formParam);
 
	String getNewAvailableFormList(long stateKey, String formCode, String formId, Map<String, String> formParamMap,
			String targetFormCode);	
 
	
}