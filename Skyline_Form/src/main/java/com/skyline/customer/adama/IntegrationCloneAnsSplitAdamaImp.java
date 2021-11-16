package com.skyline.customer.adama;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.Form;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.LookupType;
import com.skyline.form.bean.ValidationCode;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.FormSaveDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.dal.UploadFileDao;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilCalc;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.form.service.IntegrationCloneAndSplit;
import com.skyline.form.service.IntegrationValidation;

@Service
public class IntegrationCloneAnsSplitAdamaImp implements IntegrationCloneAndSplit {

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
