package com.skyline.customer.adama;

import java.text.ParseException;
import java.util.Map;

import javax.script.ScriptException;

import org.springframework.beans.factory.annotation.Autowired;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.FormSaveDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilCalc;
import com.skyline.form.service.GeneralUtilForm;
import com.skyline.form.service.GeneralUtilFormState;
import com.skyline.form.service.GeneralUtilLogger;

public class SysHandler {

// private static final Logger logger = LoggerFactory.getLogger(GeneralDaoImp.class);

	@Autowired
	public GeneralUtilForm generalUtilForm;

	@Autowired
	protected GeneralUtil generalUtil;

	@Autowired
	protected FormDao formDao;

	@Autowired
	protected GeneralDao generalDao;

	@Autowired
	protected FormSaveDao formSaveDao;

	@Autowired
	protected GeneralUtilLogger generalUtilLogger;

	@Autowired
	public GeneralUtilCalc generalUtilCalc;
	
	@Autowired
	public GeneralUtilFormState generalUtilFormState;
	
	public String doHandler(Map<String, String> handlerMap, String formCode, String formId,
			Map<String, String> elementValueMap, String userId) throws ScriptException, ParseException {
		generalUtilLogger.logWriter(LevelType.DEBUG,
				"SysHandler.doHandler formCode=" + formCode + ", formId=" + formId + ", userId=" + userId
						+ ", elementValueMap=" + generalUtil.mapToString("elementValueMap list", elementValueMap)
						+ ", handlerMap=" + generalUtil.mapToString("handlerMap list", handlerMap),
				ActivitylogType.Calculation, formId);
// System.out.println("write to log");
		return null;
	}
	
	public Double getNormalNumber(String valueToCalc, String UOMId, Double defaultValOnEmptyOrException) {
		return generalUtilCalc.getNormalNumber(valueToCalc, UOMId, defaultValOnEmptyOrException);
	}

	/**
	 * return normal number according to UOM normal value
	 * 
	 * @param valueToCalc
	 * @param UOMId
	 * @return
	 */
	public Double getNormalNumber(String valueToCalc, String UOMId, Double defaultValOnEmptyOrException,StringBuilder sb) {
		return generalUtilCalc.getNormalNumber(valueToCalc, UOMId, defaultValOnEmptyOrException, sb);
	}
	
//	public String getNormalNumberInfo(String valueToCalc, String UOMId, Double defaultValOnEmptyOrException) {
//		return generalUtilCalc.getNormalNumberInfo(valueToCalc, UOMId, defaultValOnEmptyOrException);
//	}
	 
	/**
	 * return original number according to UOM factor
	 * 
	 * @param valueToCalc
	 * @param UOMId
	 * @return
	 */
	public String getFromNormalNumber(String valueToCalc, String UOMId,StringBuilder sb) {
		return generalUtilCalc.getFromNormalNumber(valueToCalc, UOMId,sb);
	} 
	
//	public String getFromNormalNumberInfo(String valueToCalc, String UOMId) {
//		return generalUtilCalc.getFromNormalNumberInfo(valueToCalc, UOMId);
//	}
}
