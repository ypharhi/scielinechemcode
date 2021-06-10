package com.skyline.customer.adama;

import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.FormSaveDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.service.IntegrationCalc;

@Service
public class IntegrationCalcAdamaImp extends CalcBasic implements IntegrationCalc {

	@Autowired
	public GeneralDao generalDao;

	@Autowired
	public FormSaveDao formSaveDao;

	@Autowired
	public FormDao formDao;

	@Autowired
	public CalcMaterialTriplet calcMaterialTriplet;

	@Autowired
	public CalcFormulationProperties calcFormulationProperties;

	@Autowired
	public CalcComponent calcComponent;

	@Autowired
	public CalcStep calcStep;

	@Autowired
	public CalcMassBallance calcMassBallance;

	@Autowired
	public CalcTestDemo calcTestDemo;

	@Override
	public String doCalc(String api, String mainArgCode, String mainArgVal, Map<String, String> elementValueMap,
			String[] apiCodesArray, String[] elementsMatchArray, String formCode, String formId, String userId) {
		super.doCalc(api, mainArgCode, mainArgVal, elementValueMap, apiCodesArray, elementsMatchArray, formCode, formId,
				userId);
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();

		try {
			// ************** MaterialTripletCalc ************** 
			if (api.equals("MaterialTripletCalc")) {
				sb.append(calcMaterialTriplet.doCalc(api, mainArgCode, mainArgVal, elementValueMap, apiCodesArray,
						elementsMatchArray, formCode, formId, userId));

			}

			else if (api.equals("CalcFormulationProperties")) {
				sb.append(calcFormulationProperties.doCalc(api, mainArgCode, mainArgVal, elementValueMap, apiCodesArray,
						elementsMatchArray, formCode, formId, userId));

			}

			// ************** StepCalc ************** 
			else if (api.equals("StepCalc")) {
				sb.append(calcStep.doCalc(api, mainArgCode, mainArgVal, elementValueMap, apiCodesArray,
						elementsMatchArray, formCode, formId, userId));
			}
			// ************** ComponentCalc ************** 
			else if (api.equals("ComponentCalc")) {
				sb.append(calcComponent.doCalc(api, mainArgCode, mainArgVal, elementValueMap, apiCodesArray,
						elementsMatchArray, formCode, formId, userId));
			}
			// ************** ComponentCalc ************** 
						else if (api.equals("doCalcEditableTable")) {
							sb.append(calcComponent.doCalcEditableTable(api, mainArgCode, mainArgVal, elementValueMap, apiCodesArray,
									elementsMatchArray, formCode, formId, userId));
						}
			// ************** MassBallanceCalc **************
			else if (api.equals("MassBallanceCalc")) {
				sb.append(calcMassBallance.doCalc(api, mainArgCode, mainArgVal, elementValueMap, apiCodesArray,
						elementsMatchArray, formCode, formId, userId));
			}
			///
			///
			///
			// ************** TestCalcDemo ************** //TestCalcDemo - > DEVELOP DEMO NOT IN REAL USE ->
			else if (api.equals("TestCalcDemo")) {
				sb.append(calcTestDemo.doCalc(api, mainArgCode, mainArgVal, elementValueMap, apiCodesArray,
						elementsMatchArray, formCode, formId, userId));
			}
		} catch (Exception ex) {
 			generalUtilLogger.logWriter(LevelType.ERROR,
					"CalcBasic.doCalc Calculation of " + api + " was not performed. formCode=" + formCode + ", formId="
							+ formId + ", userId=" + userId + ", api=" + api + ", mainArgCode=" + mainArgCode
							+ ", mainArgVal=" + mainArgVal + ",  elementValueMap="
							+ generalUtil.mapToString("elementValueMap list", elementValueMap),
					ActivitylogType.Calculation, formId, ex, null);
			return null;
		}
		return sb.toString().replace(",}", "}");
	}

	@Override
	public String doCalcUI(String api, String eventAction, String mainArgCode, String mainArgVal,
			Map<String, String> elementValueMap, String[] apiCodesArray, String[] elementsMatchArray, String formCode,
			String formId, String userId, JSONObject elementValueJson) {
		super.doCalc(api, mainArgCode, mainArgVal, elementValueMap, apiCodesArray, elementsMatchArray, formCode, formId,
				userId);
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();

		try {
			if (api.equals("MaterialTripletCalc")) {
				sb.append(calcMaterialTriplet.doCalcUI(api, eventAction, mainArgCode, mainArgVal, elementValueMap,
						apiCodesArray, elementsMatchArray, formCode, formId, userId, elementValueJson));
			}
		} catch (Exception ex) {
			generalUtilLogger.logWriter(LevelType.ERROR,
					"CalcBasic.doCalcUI Calculation of " + api + " was not performed. formCode=" + formCode
							+ ", formId=" + formId + ", userId=" + userId + ", api=" + api + ", mainArgCode="
							+ mainArgCode + ", mainArgVal=" + mainArgVal,
					ActivitylogType.Calculation, formId, ex, null);
			return null;
		}
		return sb.toString().replace(",}", "}");
	}
	
	@Override
	public String doCalcComposition(String api, String eventAction, String mainArgCode, String mainArgVal, String mainArgLastVal,
			Map<String, String> elementValueMap, String[] apiCodesArray, String[] elementsMatchArray, String formCode,
			String formId, String userId, JSONObject elementValueJson) {
		super.doCalc(api, mainArgCode, mainArgVal, elementValueMap, apiCodesArray, elementsMatchArray, formCode, formId,
				userId);
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();

		try {
			if (api.equals("MaterialTripletCalc")) {
				sb.append(calcMaterialTriplet.doCalcComposition(api, eventAction, mainArgCode, mainArgVal,mainArgLastVal, elementValueMap,
						apiCodesArray, elementsMatchArray, formCode, formId, userId, elementValueJson));
			}
		} catch (Exception ex) {
			generalUtilLogger.logWriter(LevelType.ERROR,
					"CalcBasic.doCalcUI Calculation of " + api + " was not performed. formCode=" + formCode
							+ ", formId=" + formId + ", userId=" + userId + ", api=" + api + ", mainArgCode="
							+ mainArgCode + ", mainArgVal=" + mainArgVal,
					ActivitylogType.Calculation, formId, ex, null);
			return null;
		}
		return sb.toString().replace(",}", "}");
	}
	
	@Override
	public String doCalcRuns(String formId, String parentId, StringBuilder sbCalcInfo) {
		return calcMaterialTriplet.doCalcRuns(formId, parentId, sbCalcInfo);
	}
}
