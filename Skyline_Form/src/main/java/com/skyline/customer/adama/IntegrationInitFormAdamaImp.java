package com.skyline.customer.adama;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skyline.form.bean.FormType;
import com.skyline.form.service.IntegrationInitForm;

@Service
@Transactional
public class IntegrationInitFormAdamaImp implements IntegrationInitForm {

	@Override
	public Map<String, String> getFormParam(String formCode, String userId, String formId, FormType formType,
			boolean isNewFormId, Map<String, String> requestMap) {
		// TODO Auto-generated method stub
		return new HashMap<String,String>();
	}

	@Override
	public String getFormPathInfo(long statKey, String formCode, String userId, String formId, FormType formType,
			boolean isNewFormId) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public Map<String, String> onIntegrationEvent(String formCode, String userId, String formId, FormType formType,
			boolean isNewFormId, Map<String, String> outParamMap, long stateKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String showFormPathDisplayHtml(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
