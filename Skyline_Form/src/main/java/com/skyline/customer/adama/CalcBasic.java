package com.skyline.customer.adama;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.util.JSONPObject;
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

// @Component
public class CalcBasic {
	// private static final Logger logger = LoggerFactory.getLogger(GeneralDaoImp.class);

	@Autowired
	protected GeneralDao generalDao;

	@Autowired
	protected FormSaveDao formSaveDao;

	@Autowired
	protected FormDao formDao;

	@Autowired
	protected GeneralUtilLogger generalUtilLogger;

	@Autowired
	public GeneralUtilForm generalUtilForm;

	@Autowired
	public GeneralUtilFormState generalUtilFormState;

	@Autowired
	public GeneralUtil generalUtil;

	@Autowired
	public GeneralUtilCalc generalUtilCalc;

	public String doCalc(String api, String mainArgCode, String mainArgVal, Map<String, String> elementValueMap,
			String[] apiCodesArray, String[] elementsMatchArray, String formCode, String formId, String userId) {
		// TODO Auto-generated method stub

		generalUtilLogger.logWriter(LevelType.DEBUG,
				"CalcBasic.doCalc formCode=" + formCode + ", formId=" + formId + ", userId=" + userId + ", api=" + api
						+ ", mainArgCode=" + mainArgCode + ", mainArgVal=" + mainArgVal + ",  elementValueMap="
						+ generalUtil.mapToString("elementValueMap list", elementValueMap),
				ActivitylogType.Calculation, formId);
		return null; // TODO write to log
	}

	/**
	 * return normal number according to UOM normal value (null on error)
	 * 
	 * @param valueToCalc
	 * @param UOMId
	 * @return
	 */
	public Double getNormalNumber(String valueToCalc, String UOMId) {
		return generalUtilCalc.getNormalNumber(valueToCalc, UOMId);
	}

	/**
	 * return normal number according to UOM normal value (defaultValOnEmptyOrException on error)
	 * 
	 * @param valueToCalc
	 * @param UOMId
	 * @param defaultValOnEmptyOrException
	 * @return
	 */
	public Double getNormalNumber(String valueToCalc, String UOMId, Double defaultValOnEmptyOrException) {
		return generalUtilCalc.getNormalNumber(valueToCalc, UOMId, defaultValOnEmptyOrException, null);
	}

	/**
	 * return original number according to UOM factor
	 * 
	 * @param valueToCalc
	 * @param UOMId
	 * @return
	 */
	public String getFromNormalNumber(String valueToCalc, String UOMId, StringBuilder sb) {
		return generalUtilCalc.getFromNormalNumber(valueToCalc, UOMId, sb);
	}
	
	/**
	 * return number according to UOM factor
	 * 
	 * @param valueToCalc
	 * @param valueUOMId
	 * @param customUOMId
	 * @param sb
	 * @return
	 */
	public Double getCustomNormalNumber(String valueToCalc, String valueUOMId, String customUOMId, StringBuilder sb) {
		return generalUtilCalc.getCustomNormalNumber(valueToCalc, valueUOMId, customUOMId, sb);
	}

	public String doCalc(String api, String mainArgCode, String mainArgVal, Map<String, String> elementValueMap,
			String[] apiCodesArray, String[] elementsMatchArray, String formCode, String formId, String userId,
			JSONPObject elementValueJson) {

		generalUtilLogger.logWriter(LevelType.DEBUG,
				"CalcBasic.doCalc formCode=" + formCode + ", formId=" + formId + ", userId=" + userId + ", api=" + api
						+ ", mainArgCode=" + mainArgCode + ", mainArgVal=" + mainArgVal + ",  elementValueMap="
						+ generalUtil.mapToString("elementValueMap list", elementValueMap),
				ActivitylogType.Calculation, formId);
		return null;
	}

	/*public String doCalcUI(String api, String mainArgCode, String mainArgVal, Map<String, String> elementValueMap,
			String[] apiCodesArray, String[] elementsMatchArray, String formCode, String formId, String userId,
			JSONPObject elementValueJson) {
		// TODO Auto-generated method stub
		return null;
	}
	*/
	public String doCalcUI(String api, String eventAction, String mainArgCode, String mainArgVal,
			Map<String, String> elementValueMap, String[] apiCodesArray, String[] elementsMatchArray, String formCode,
			String formId, String userId, JSONObject elementValueJson) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String doCalcRuns(String formId, String parentId, StringBuilder sbCalcInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	public String doCalcComposition(String api, String eventAction, String mainArgCode, String mainArgVal,
			String mainArgLastVal, Map<String, String> originElementValueMap, String[] apiCodesArray,
			String[] elementsMatchArray, String formCode, String formId, String userId, JSONObject elementValueJson) {
		// TODO Auto-generated method stub
		return null;
	}
}
