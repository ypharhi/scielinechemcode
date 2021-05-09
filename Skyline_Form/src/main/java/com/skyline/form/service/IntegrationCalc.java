package com.skyline.form.service;

import java.util.Map;

import org.json.JSONObject;

public interface IntegrationCalc {

	String doCalc(String aPI, String mainArgCode, String mainArgVal, Map<String, String> elementValueMap,
			String[] apiCodesArray, String[] elementsMatchArray, String formCode, String formId, String userId);

	String doCalcUI(String api, String eventAction, String mainArgCode, String mainArgVal,
			Map<String, String> elementValueMap, String[] apiCodesArray, String[] elementsMatchArray, String formCode,
			String formId, String userId, JSONObject jsonObject);
	
	String doCalcRuns(String formId, String parentId, StringBuilder sbCalcInfo);

	String doCalcComposition(String api, String eventAction, String mainArgCode, String mainArgVal, String mainArgLastVal,
			Map<String, String> elementValueMap, String[] apiCodesArray, String[] elementsMatchArray, String formCode,
			String formId, String userId, JSONObject jsonObject);

}
