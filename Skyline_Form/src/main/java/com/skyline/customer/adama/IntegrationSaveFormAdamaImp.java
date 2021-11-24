package com.skyline.customer.adama;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.DataBean;
import com.skyline.form.service.IntegrationSaveForm;

@Service
public class IntegrationSaveFormAdamaImp implements IntegrationSaveForm {

	@Autowired
	CommonFunc commonFunc;
	
	@Override
	public int preFormSaveEvent(Long stateKey, String formCode, String formId, Map<String, String> elementValueMap,
			Map<String, String> elementAdditinalDataoMap, String userId, String isNew, String saveAction,
			StringBuilder sbInfo) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int postFormSaveEvent(Long stateKey, String formCode, String formId, Map<String, String> elementValueMap,
			Map<String, String> elementAdditinalDataoMap, String userId, String isNew, String saveAction,
			List<DataBean> dataBeanReturnList, Map<String, String> elementValueInfATMap, StringBuilder sbInfo)
			throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ActionBean doSaveOnException(Exception e, String formId, String formCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String doRemove(String formCode, String formId, String userId) {
		return commonFunc.doRemove(formCode,formId,userId);
	}
	
	

}
