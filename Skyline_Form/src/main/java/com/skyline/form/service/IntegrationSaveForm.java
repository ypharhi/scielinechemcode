package com.skyline.form.service;

import java.util.List;
import java.util.Map;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.DataBean;

public interface IntegrationSaveForm
{
	int preFormSaveEvent(Long stateKey, String formCode, String formId, Map<String, String> elementValueMap, Map<String,String> elementAdditinalDataoMap, String userId, String isNew, String saveAction, StringBuilder sbInfo) throws Exception;
	
	int postFormSaveEvent(Long stateKey, String formCode, String formId, Map<String, String> elementValueMap, Map<String,String> elementAdditinalDataoMap, String userId, String isNew, String saveAction, List<DataBean> dataBeanReturnList, Map<String,String> elementValueInfATMap, StringBuilder sbInfo)
			throws Exception;

	ActionBean doSaveOnException(Exception e, String formId, String formCode); 
	
	String doRemove(String formCode, String formId, String userId);
}