package com.skyline.form.entity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.skyline.form.bean.BeanType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.dal.FormDao;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilForm;
import com.skyline.form.service.GeneralUtilFormState;
import com.skyline.form.service.GeneralUtilLogger;

public class Entity implements EntityInterface { //Entity abstract with basic functionality every entity needs

//	@Autowired
//	FormState formState;

	@Autowired
	public GeneralUtil generalUtil;
	
	@Autowired
	public FormDao formDao;
	
	@Autowired
	public GeneralUtilLogger generalUtilLogger;
	
	@Autowired
	public GeneralUtilForm generalUtilForm;
	
	@Autowired
	public GeneralUtilFormState generalUtilFormState;

	private boolean active;
	
	private String impName;
	
	public String impCode;

	public String initVal;
	
	public String formCode;
	
//	public long stateKey;
	
	public JSONObject jsonInit;
	
	private static final Logger logger = LoggerFactory.getLogger(Entity.class);

//	public Map<String, String> catalogMap;
 
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try{
			this.jsonInit = new JSONObject(initVal);
			this.initVal = initVal;
			this.impCode = impCode;
			this.formCode = formCode;
//			this.stateKey = stateKey;
//			this.catalogMap = formState.getCatalogMap();
//			formState.setFormBean(impCode, this);
//			logger.debug("Entity initiation: impCode=" + impCode + ", initVal=" + initVal
//					+ ". The implementation should continue from here...");
			return "";
		}
		catch(Exception e)
		{
			generalUtilLogger.logWrite(e);
			return "Creation failed";
		}
	}

	public boolean getActive() {
		// TODO Auto-generated method stub
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getInitVal() {
		// TODO Auto-generated method stub
		return initVal;
	}

	public String getImpCode() {
		// TODO Auto-generated method stub
		return impCode;
	}

	public String getInitSchemaVal() {
		return "";
	}	
	
	public String getImpName() {
		return impName;
	}

	public void setImpName(String impName) {
		this.impName = impName;
	}
	
	public String getFormCode() {
		return this.formCode;
	}
	
	public void setFormCode(String formCode) {
		this.formCode = formCode;
	} 
	
	//for en singletone classs
	public List<DataBean> getImpResourceForFormBuilder(String formCode, String impCode, String initVal) {
		List<DataBean> dataBeanList = new ArrayList<DataBean>();
		dataBeanList.add(new DataBean(impCode, impCode, BeanType.ENTETY_IMP_CODE, impCode));
//		System.out.println("We can get all we need in reflection. If not override this function in the implementation");
		return dataBeanList;
	}
}
