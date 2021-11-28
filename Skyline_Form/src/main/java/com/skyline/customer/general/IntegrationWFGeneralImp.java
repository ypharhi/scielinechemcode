package com.skyline.customer.general;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.skyline.form.service.IntegrationWF;

@Service
public class IntegrationWFGeneralImp implements IntegrationWF {

	@Override
	public Map<String, String> getFormWFStateGeneral(String formCode, String userId, String formId, boolean isNewFormId,
			Map<String, String> formParam) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNewAvailableFormList(long stateKey, String formCode, String formId,
			Map<String, String> formParamMap, String targetFormCode) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
