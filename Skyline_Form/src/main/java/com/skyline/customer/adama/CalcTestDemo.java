package com.skyline.customer.adama;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class CalcTestDemo extends CalcBasic {

	@Override
	public String doCalc(String api, String mainArgCode, String mainArgVal, Map<String, String> elementValueMap,
			String[] apiCodesArray, String[] elementsMatchArray, String formCode, String formId, String userId) {

		StringBuilder sb = new StringBuilder();

		if (mainArgCode.equals("calcNumberSetOne")) {
			sb.append("{");
			sb.append("\"calcNumberSetTwo\":\"2.2222222222\",");
			sb.append("\"volume\":\"" + elementValueMap.get("volume") + "\"");
			sb.append("}");
		} else {
			sb.append("{");
			sb.append("\"calcNumberSetOne\":\"1.999999999\"");
			sb.append("}");
		}
		return sb.toString();
	}

}
