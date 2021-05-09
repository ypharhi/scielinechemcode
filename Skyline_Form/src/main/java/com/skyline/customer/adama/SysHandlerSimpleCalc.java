package com.skyline.customer.adama;

import java.text.ParseException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;


public class SysHandlerSimpleCalc extends SysHandler {
	
	private ScriptEngine engine; 
	
	public SysHandlerSimpleCalc() {
		ScriptEngineManager factory = new ScriptEngineManager();
		this.engine = factory.getEngineByName("JavaScript");
	}
  
	@Override
	public String doHandler (Map<String,String> handlerMap, String formCode, String formId, Map<String, String> elementValueMap , String userId) throws ScriptException, ParseException  {
		// TODO Auto-generated method stub
		
		super.doHandler(handlerMap, formCode, formId, elementValueMap, userId);
		StringBuilder sbInfo = new StringBuilder();
		
		String formula = handlerMap.get("CALCFORMULA");
		String formulaExpression_ = formula; //Expression
		String calcArgName = StringUtils.substringBetween(handlerMap.get("CALCARG"), "M[", "]");
		String elementResultName = calcArgName;
	    String uomResultName = "NA";
		if(calcArgName.contains(";")){
			elementResultName = calcArgName.split(Pattern.quote(";"))[0];
			uomResultName = calcArgName.split(Pattern.quote(";"))[1];
		} 
		
		generalUtilLogger.logWriter(LevelType.DEBUG,
				"SysHandler.doHandler formCode=" + formCode + ", formId=" + formId + ", userId=" + userId + ", origin formula=" + formula + ", elementValueMap=" + generalUtil.mapToString("elementValueMap list", elementValueMap) 
				+ ", handlerMap=" + generalUtil.mapToString("handlerMap list", handlerMap) ,
				ActivitylogType.Calculation, formId);
		
		
		if(!generalUtil.getNull(formula).isEmpty())
		{
			Pattern p = Pattern.compile("\\$M\\[(.*?)\\]");
			Matcher m = p.matcher(formula);
			while(m.find())
			{
			    String param = m.group(1); 
			    
			    if(param.contains(";"))
			    {
			    	String[] paramParts = param.split(Pattern.quote(";"));
			    	String elementName = paramParts[0];
			    	String uomName = paramParts[1];
			    	Double defaultValFloat = 0d; // 0 was the value during Adama tests (yp next version -> ability to add default value as 3th arg with null or empty as null)
//			    	if(paramParts.length > 2) {
//			    		String defaultVal = paramParts[2];
//			    		if(generalUtil.getNull(defaultVal).equals("") || generalUtil.getNull(defaultVal).trim().equalsIgnoreCase("null")) {
//			    			defaultValFloat = Float.valueOf(defaultVal);
//			    		}
//			    	}
			    		
			    	if(generalUtil.getNull(elementValueMap.get(elementName)).isEmpty() && !formula.contains("?"))
			    	{
			    		if(generalUtil.getNull(elementValueMap.get(elementName)).isEmpty()) {
			    			generalUtilLogger.logWriter(LevelType.DEBUG,
			    					"DEBUG! doHandler elementName = " + elementName + " not found! formCode=" + formCode + ", elementValueMap=" + generalUtil.mapToString("", elementValueMap),
			    					ActivitylogType.Calculation, formId);
			    		}
				    	sbInfo.append("Final Result: missing arg -> " + elementName + ". " + elementResultName + " will be cleaned from previous calculation [in formula without condition]");
				    	elementValueMap.put(elementResultName, ""); 
				    	generalUtilLogger.logWriter(LevelType.INFO,sbInfo.toString(), ActivitylogType.Calculation, formId);
					    
			    		return "";
			    	} 
			    	String val_ = getNormalNumber(elementValueMap.get(elementName),elementValueMap.get(uomName), defaultValFloat, sbInfo.append("</br>" + elementName + "-> ")).toString();		
			    	formula = formula.replace("$M["+elementName+";"+ uomName +"]",val_);
			    	formulaExpression_ = formulaExpression_.replace("$M["+elementName+";"+ uomName +"]",elementName);
			    }
			    else
			    {
			    	sbInfo.append("</br>" + param + "-> " + elementValueMap.get(param) + "[NA]");
			    	formula = formula.replace("$M["+param +"]", elementValueMap.get(param));
			    	formulaExpression_ = formulaExpression_.replace("$M["+param +"]",param);
			    	
			    }
			}
			
			Object result = 0;
			result = engine.eval(formula);
			
			String displayInfo = "Formula: " + elementResultName + "=" + formulaExpression_ + "</br>" + sbInfo.toString() + "</br>";
			if(result.equals("SKIP")) {
				displayInfo += "</br>Result: SKIP calculation</br>";
		    	generalUtilLogger.logWriter(LevelType.INFO, displayInfo, ActivitylogType.Calculation, formId);
				return "";
			}
			displayInfo += "</br>Result: " + formula + "=" + result + "</br>";
			
			if(result.toString().trim().isEmpty())
			{
				generalUtilLogger.logWriter(LevelType.DEBUG,
    					"DEBUG! doHandler elementName = " + result + " is empty! formCode=" + formCode + ", elementValueMap=" + generalUtil.mapToString("", elementValueMap),
    					ActivitylogType.Calculation, formId);
				result = "";
			}
			
			if(result.toString().equals("NaN")){
				generalUtilLogger.logWriter(LevelType.ERROR,
    					"Error! doHandler elementName = \"result \" was not calculated! formCode=" + formCode + ", formId=" + formId + ", userId=" + userId + ", eval formula=" + formula
    					+ ", elementValueMap=" + generalUtil.mapToString("", elementValueMap),
    					ActivitylogType.Calculation, formId);
				//result = "";
			}
			
			String finalResult_ = result.toString();
			
			if(calcArgName.contains(";")) { // There is UOM -> we need to re-factor back from normal using getFromNormalNumber that also write to the log
			    finalResult_ = getFromNormalNumber( result.toString(),elementValueMap.get(uomResultName), new StringBuilder(displayInfo)); 
			    //hold finalResultInfo_ and put it into the sbInfo
			} else {
				displayInfo += "</br>No UOM to refactor";
		    	generalUtilLogger.logWriter(LevelType.INFO, displayInfo, ActivitylogType.Calculation, formId);
			}
	    	elementValueMap.put(elementResultName, finalResult_);  
	    	
		} 
		 
		 
		return "";

	}
	
//	@Autowired
//	private GeneralUtil generalUtil;

//	@Override
//	public String doHandler(Map<String, String> handlerMap, String formCode, String formId,
//			Map<String, String> elementValueMap, String userId) {
//				return ""; 
//		
//	}

}
