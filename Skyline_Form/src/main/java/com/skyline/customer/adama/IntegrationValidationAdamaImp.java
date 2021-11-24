package com.skyline.customer.adama;

import org.springframework.stereotype.Service;

import com.skyline.form.bean.ValidationCode;
import com.skyline.form.service.IntegrationValidation;

@Service
public class IntegrationValidationAdamaImp implements IntegrationValidation {

	@Override
	public void validate(ValidationCode validationCode, String formCode, String formId, Object validateValueObject,
			StringBuilder sbInfoMessage) throws Exception {
		// TODO Auto-generated method stub
		
	}

	
}
