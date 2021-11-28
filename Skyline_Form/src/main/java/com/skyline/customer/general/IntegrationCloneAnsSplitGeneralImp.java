package com.skyline.customer.general;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.skyline.form.service.IntegrationCloneAndSplit;

@Service
public class IntegrationCloneAnsSplitGeneralImp implements IntegrationCloneAndSplit {

	@Override
	public void postCloneSaveEvent(String formId, String cloneFormId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String MultiCloneSaveEvent(String formId, String cloneQuantity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String splitSaveEvent(String formId, String currentQuantity, String splitQuantity, String splitQuantityUom)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> cloneRemoveFields(String formId, String cloneFormId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String postSplitSaveEvent(String formId, String cloneFormId, String currentQuantity, String splitQuantity,
			String splitQuantityUom) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String doCloneBySaveDefaultData(String formCode, String parentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDefaultDataFormId(String formCode, String parentFormCode, String parentId) {
		// TODO Auto-generated method stub
		return null;
	}

	 

}
