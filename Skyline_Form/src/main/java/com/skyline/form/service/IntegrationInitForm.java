package com.skyline.form.service;

import java.util.Map;

import com.skyline.form.bean.FormType;

public interface IntegrationInitForm {	
 
	Map<String, String> getFormParam(String formCode, String userId, String formId, FormType formType, boolean isNewFormId, Map<String, String> requestMap);

	String getFormPathInfo(long statKey, String formCode, String userId, String formId, FormType formType, boolean isNewFormId);

	Map<String, String> onIntegrationEvent(String formCode, String userId, String formId, FormType formType, boolean isNewFormId,
			Map<String, String> outParamMap);

	String showFormPathDisplayHtml(String path);
}