package com.skyline.customer.adama;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;

@Component
public class CalcFormulationProperties extends CalcBasic {

	// private static final Logger logger = LoggerFactory.getLogger(GeneralDaoImp.class);
	public String doCalc(String api, String mainArgCode, String mainArgVal, Map<String, String> elementValueMap,
			String[] apiCodesArray, String[] elementsMatchArray, String formCode, String formId, String userId) {
		StringBuilder sb = new StringBuilder();
		StringBuilder sbInfo = new StringBuilder();
		// clean last calc
		elementValueMap.put("calTheoTotalMass", "");

		if (mainArgCode.equals("calTheoTotalMass")) {
			sbInfo.append("TheoTotal Mass Calculation =></br>");
			Double floatOfDensity = getNormalNumber(elementValueMap.get("density"),
					elementValueMap.get("densityUomInf"));
			if (floatOfDensity == null) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"The calculation was not performed on calTheoTotalMass. Density is empty",
						ActivitylogType.Calculation, formId);
			} else {
				sbInfo.append("Formula: Density * 0.001.</br> Normal Args:" + floatOfDensity + "*0.001." + "</br>");
				String calTheoTotalMass = getFromNormalNumber(String.valueOf(floatOfDensity * 0.001),
						elementValueMap.get("UOMTHEOTOTALMASS_ID"), sbInfo);
				if (calTheoTotalMass.isEmpty()) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"The calculation was not performed on calTheoTotalMass. Displaying theo total mass according to UOM failed.",
							ActivitylogType.Calculation, formId);
				}
				elementValueMap.put("calTheoTotalMass", calTheoTotalMass);
				sb.append("{");
				sb.append("\"calTheoTotalMass\":\"" + calTheoTotalMass + "\",");
				sb.append("}");

			}
		} else if (mainArgCode.equals("calTotalMass")) {
			sb = totalMassCalc(elementValueMap, formId);
		} else {
			sb.append("{");
			sb.append("}");
		}
		return sb.toString();
	}

	private StringBuilder totalMassCalc(Map<String, String> elementValueMap, String formId) {
		StringBuilder sbInfo = new StringBuilder();
		sbInfo.append("Total Mass Calculation =></br>");
		Map<String, String> pmValuesMap = new HashMap<String, String>();
		Map<String, String> pmUOMMap = new HashMap<String, String>();
		for (Map.Entry<String, String> e : elementValueMap.entrySet()) {
			if (e.getKey().endsWith("_uom_parameterMonitoring")) {
				String newKey = e.getKey().substring(0, e.getKey().length() - 24);
				pmUOMMap.put(newKey, e.getValue());
			} else if (e.getKey().endsWith("_parameterMonitoring")) {
				String newKey = e.getKey().substring(0, e.getKey().length() - 20);
				pmValuesMap.put(newKey, e.getValue());
			}
		}
		double sumOfQuantities = 0;
		for (Map.Entry<String, String> e : pmValuesMap.entrySet()) {

			String uom = pmUOMMap.get(e.getKey());
			String value = pmValuesMap.get(e.getKey());
			Double normalValue = getNormalNumber(value, uom, (double) 0);
			sumOfQuantities += normalValue;

		}
		String normalSumOfQuantities;
		sbInfo.append("Formula: Sum(Quantity).</br> Normal Args:" + String.valueOf(sumOfQuantities) + "</br>");
		normalSumOfQuantities = getFromNormalNumber(String.valueOf(sumOfQuantities),
				elementValueMap.get("UOMTOTALMASS_ID"), sbInfo);
		if (normalSumOfQuantities.isEmpty()) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"The calculation was not performed on calTheoTotalMass. Displaying sum of quantities according to UOM failed.",
					ActivitylogType.Calculation, formId);
		}
		elementValueMap.put("calTotalMass", normalSumOfQuantities);
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"calTotalMass\":\"" + String.valueOf(normalSumOfQuantities) + "\",");
		sb.append("}");
		return sb;

	}

}
