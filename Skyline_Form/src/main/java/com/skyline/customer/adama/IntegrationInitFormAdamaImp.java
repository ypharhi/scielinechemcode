package com.skyline.customer.adama;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.FormType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.LookupType;
import com.skyline.form.dal.ChemDao;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.FormSaveDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.dal.GeneralTaskDao;
import com.skyline.form.dal.UploadFileDao;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilCalc;
import com.skyline.form.service.GeneralUtilDesignData;
import com.skyline.form.service.GeneralUtilForm;
import com.skyline.form.service.GeneralUtilFormState;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.form.service.GeneralUtilPermission;
import com.skyline.form.service.IntegrationCalc;
import com.skyline.form.service.IntegrationEvent;
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
