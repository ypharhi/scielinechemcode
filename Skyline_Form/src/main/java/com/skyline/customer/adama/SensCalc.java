package com.skyline.customer.adama;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class SensCalc {

	private ScriptEngine engine;

	public SensCalc() {
		ScriptEngineManager factory = new ScriptEngineManager();
		this.engine = factory.getEngineByName("js");
	}

	public Map<String, String> doSensCalc(Map<String, String> handlerMap, String formCode, String formId,
			Map<String, String> elementValueMap, String userId) {

		String formula = handlerMap.get("CALCFORMULA");

		Pattern p = Pattern.compile("M\\[(.*?)\\]");
		Matcher m = p.matcher(formula);
		while (m.find()) {
			String param = m.group(1);

			if (param.contains(".")) { //M[parentFormCode.element]
				//parentId=20469
				//	    	String parentName = param.split(Pattern.quote("."))[0];
				//	    	String parentId =  elementValueMap.get("parentId");
				//	    	Map<String, String> parentMap = formDao.getFromInfoLookupAll(parentName, LookupType.ID ,parentId);
				//	    	String t = parentMap.get(param.split(Pattern.quote("."))[1]);
			} else { //M[element]
				formula = formula.replace("M[" + param + "]", elementValueMap.get(param));
			}
		}

		/*p = Pattern.compile("P\\[(.*?)\\]");
		m = p.matcher(formula);
		while(m.find())
		{
		String param = m.group(1); //is your string. do what you want
		  
		formula = formula.replace("M["+param+"]",elementValueMap.get(param));
		}*/
		//String formulaArgs = StringUtils.substringBetween(formula, "M[", "]");

		Object result = null;
		//	ScriptEngineManager manager = new ScriptEngineManager();
		//	ScriptEngine engine = manager.getEngineByName("js");

		try {
			result = engine.eval(formula);
		} catch (javax.script.ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String calcArgName = StringUtils.substringBetween(handlerMap.get("CALCARG"), "M[", "]");
		elementValueMap.put(calcArgName, result.toString());
		return elementValueMap;
	}

}
