package com.skyline.form.service;

import com.skyline.form.bean.ValidationCode;

public interface IntegrationValidation {
 
	void validate(ValidationCode validationCode, String formCode, String formId, Object validateValueObject, StringBuilder sbInfoMessage) throws Exception; 
}
