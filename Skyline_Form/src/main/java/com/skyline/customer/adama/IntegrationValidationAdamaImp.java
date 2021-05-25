package com.skyline.customer.adama;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

//import com.ibm.db2.jcc.t4.sb;
import com.skyline.form.bean.LookupType;
import com.skyline.form.bean.ValidationCode;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilFormState;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.form.service.IntegrationValidation;

import oracle.jdbc.OracleTypes;

@Service
public class IntegrationValidationAdamaImp implements IntegrationValidation {

	@Override
	public void validate(ValidationCode validationCode, String formCode, String formId, Object validateValueObject,
			StringBuilder sbInfoMessage) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
