package com.skyline.customer.adama;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.xml.dtm.ref.DTMDefaultBaseIterators.ParentIterator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.BeanType;
import com.skyline.form.bean.CloneType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.Form;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.LookupType;
import com.skyline.form.bean.Result;
import com.skyline.form.bean.ValidationCode;
import com.skyline.form.bean.WorkflowType;
import com.skyline.form.dal.ChemDao;
import com.skyline.form.dal.ChemMatrixTaskDao;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.FormSaveDao;
import com.skyline.form.dal.FormSaveElementDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.dal.GeneralTaskDao;
import com.skyline.form.dal.UploadFileDao;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilCalc;
import com.skyline.form.service.GeneralUtilConfig;
import com.skyline.form.service.GeneralUtilDesignData;
import com.skyline.form.service.GeneralUtilForm;
import com.skyline.form.service.GeneralUtilFormState;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.form.service.IntegrationCalc;
import com.skyline.form.service.IntegrationSaveForm;
import com.skyline.form.service.IntegrationValidation;

@Service
public class IntegrationSaveFormAdamaImp implements IntegrationSaveForm {

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
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
