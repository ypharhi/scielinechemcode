package com.skyline.customer.adama;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.LookupType;
import com.skyline.form.bean.WorkflowType;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilFormState;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.form.service.GeneralUtilPermission;
import com.skyline.form.service.IntegrationWF;

@Service
public class IntegrationWFAdamaImp implements IntegrationWF {

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
