package com.skyline.form.service;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;

@Service
public class GeneralUtilCalc {
 
	@Autowired
	public GeneralUtilForm generalUtilForm;

	@Autowired
	protected GeneralUtilLogger generalUtilLogger;
	
	@Autowired
	public GeneralUtil generalUtil;

	@Value("${precision}")
	private String defaultPrecision; // precision
	 
	private DecimalFormat decimalFormatPrecision = null;

	/**
	 * return normal number according to UOM normal value
	 * 
	 * @param valueToCalc
	 * @param UOMId
	 * @return
	 */
	public Double getNormalNumber(String valueToCalc, String UOMId) {
		return this.getNormalNumber(valueToCalc, UOMId, null, null);
	}
	
	public Double getNormalNumber(String valueToCalc, String UOMId, Double defaultValOnEmptyOrException) {
		return this.getNormalNumber(valueToCalc, UOMId, defaultValOnEmptyOrException, null);
	}

	/**
	 * return normal number according to UOM normal value (defaultValOnEmptyOrException on error)
	 * 
	 * @param valueToCalc
	 * @param UOMId
	 * @param defaultValOnEmptyOrException
	 * @return
	 */
	public Double getNormalNumber(String valueToCalc, String UOMId, Double defaultValOnEmptyOrException, StringBuilder sb) {
		String factor = "";
		Double returnNum;
		
		if(sb == null) {
			sb = new StringBuilder();
		}

		if (valueToCalc == null || valueToCalc.isEmpty()) {
			generalUtilLogger.logWriter(LevelType.DEBUG,
					"The normalization was not performed :valueToCalc =" + valueToCalc + ",UOMId=" + UOMId
							+ ", defaultValOnEmptyOrException=" + defaultValOnEmptyOrException,
					ActivitylogType.Calculation, "");
			return defaultValOnEmptyOrException;
		}

		try {
			Map<String, String> UOMValues = generalUtilForm.getCurrrentInfoById("UOM", UOMId);
			factor = UOMValues.get("FACTOR");

			if (factor == null || factor.isEmpty()) {
				factor = "1";
			}
			returnNum = getNumberPrecisionLimit(Double.parseDouble(valueToCalc) * Double.parseDouble(factor));
			
			if(factor == null || factor.isEmpty()) {
				sb.append(returnNum + "=" + valueToCalc + "[NA]\n");
				sb.append("Warning: factor not found\n");
			} else {
				sb.append(returnNum + "=" + valueToCalc + "[" + UOMValues.get("NAME") + "] * " + factor);
			}
			
//			generalUtilLogger.logWriter(LevelType.DEBUG,
//					sb.toString() ,ActivitylogType.Calculation, "");
			
			return returnNum;
		} 
		catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.ERROR,
					"ERROR! The normalization was not performed :valueToCalc =" + valueToCalc + ",UOMId=" + UOMId
							+ ", defaultValOnEmptyOrException=" + defaultValOnEmptyOrException,
					ActivitylogType.Calculation, "", e,null);
			return defaultValOnEmptyOrException;
		}

	}
  
	public String getNumberRoundedDispaly(String valueToRound, String precision) {  
		String toReturn = "";
		try { 
			double d = Double.parseDouble(valueToRound);
			DecimalFormat df = new DecimalFormat(getFormByPrecision(precision));
			df.setRoundingMode(RoundingMode.HALF_UP);
			toReturn = df.format(d); 
		} catch (NumberFormatException e) {
			toReturn = valueToRound;
		}
		return toReturn;
	}
	
	public String getFromNormalNumber(String valueToCalc, String UOMId,StringBuilder sb) {
		
		String toReturn = "";

		if (valueToCalc == null) {
			return "";
		}
		if(sb==null){
			sb = new StringBuilder();
		}

		try {
			String factor = "";
			Map<String, String> UOMValues = generalUtilForm.getCurrrentInfoById("UOM", UOMId);
			factor = UOMValues.get("FACTOR");

			if (factor == null || factor.isEmpty()) {
				factor = "1";
			}
			
			toReturn = getNumberPrecisionLimit(Double.parseDouble(valueToCalc) / Double.parseDouble(factor)).toString();
			
			if(factor == null || factor.isEmpty()) {
				sb.append("</br>Result: " + toReturn + "[NA]");
				sb.append("Warning: factor not found\n");
			} else {
				sb.append("</br>Result (refactor): " + toReturn + "[" + UOMValues.get("NAME") + "] =" + valueToCalc + "/" + factor);
			}
			
			generalUtilLogger.logWriter(LevelType.INFO,
					sb.toString() ,ActivitylogType.Calculation, "");
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.ERROR,
					"The normalization function getFromNormalNumber (refactor) failed! :valueToCalc =" + valueToCalc + ",UOMId="
							+ UOMId,
					ActivitylogType.Calculation, "", e, null);
		}

		return toReturn;
	}
	
	public Double getCustomNormalNumber(String valueToCalc, String valueUOMId, String customUOMId, StringBuilder sb) {
		String factor = "";
		Double returnValue;
		Double normalValue;
		
		if(sb == null) {
			sb = new StringBuilder();
		}

		if (valueToCalc == null || valueToCalc.isEmpty()) {
			generalUtilLogger.logWriter(LevelType.DEBUG,
					"The normalization was not performed :valueToCalc =" + valueToCalc + ",UOMId=" + valueUOMId+ ", customUOMId=" + customUOMId,ActivitylogType.Calculation, "");
			return null;
		}

		try 
		{
			/* First convert to normal value according to default UOM defined in Maintenance */
			Map<String, String> uomValues = generalUtilForm.getCurrrentInfoById("UOM", valueUOMId);
			factor = uomValues.get("FACTOR");

			if (factor == null || factor.isEmpty()) {
				factor = "1";
			}
			if(valueUOMId.equals(customUOMId)){
				sb.append("Result (refactor): " + valueToCalc + "[" + uomValues.get("NAME") + "] " + "\n</br>");
				return Double.parseDouble(valueToCalc);
			}
			normalValue = getNumberPrecisionLimit(Double.parseDouble(valueToCalc) * Double.parseDouble(factor));
			
			if(factor == null || factor.isEmpty()) {
				sb.append(normalValue + "=" + valueToCalc + "[NA]\n</br>");
				sb.append("Warning: factor not found\n");
			} else {
				sb.append(normalValue + "=" + valueToCalc + "[" + uomValues.get("NAME") + "] * " + factor + "\n</br>");
			}
			
			/* Second convert to value according to custom UOM defined by call function */
			Map<String, String> customUOMValues = generalUtilForm.getCurrrentInfoById("UOM", customUOMId);
			factor = customUOMValues.get("FACTOR");

			if (factor == null || factor.isEmpty()) {
				factor = "1";
			}
			returnValue = getNumberPrecisionLimit(normalValue / Double.parseDouble(factor));
			
			if(factor == null || factor.isEmpty()) {
				sb.append("Result: " + returnValue + "[NA]\n</br>");
			} else {
				sb.append("Result (refactor): " + returnValue + "[" + customUOMValues.get("NAME") + "] =" + normalValue + "/" + factor + "\n</br>");
			}
			
			return returnValue;
		} 
		catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.ERROR,
					"ERROR! The normalization was not performed :valueToCalc =" + valueToCalc + ",UOMId=" + valueUOMId+ ", customUOMId=" + customUOMId,ActivitylogType.Calculation, "", e,null);
			return null;
		}

	}
	 
	/**
	 * round number according to precision parmeter
	 * 
	 * @param valueToRound
	 * @param precision
	 * @return
	 */
	
	/**
	 * 
	 * @param valueToRound
	 * @param precision is not in use instead we use the defaultPrecision (in app properties to limit number that is used as the realvalue in the calculated element)
	 * @return valueToRound in case of Exception
	 */
	
	private Double getNumberPrecisionLimit(Double valueToRound) {
		double d = valueToRound;
		Double number = valueToRound;
		try {
			if(decimalFormatPrecision == null) {
				decimalFormatPrecision = new DecimalFormat(getFormByPrecision(defaultPrecision));
			}
			decimalFormatPrecision.setRoundingMode(RoundingMode.HALF_UP);

			number = Double.parseDouble(decimalFormatPrecision.format(d));
		} catch (Exception e) {
			// TODO Auto-generated catch block
            //e.printStackTrace();
		}
		return number;
	}
	
	private String getFormByPrecision(String defaultPrecision) {
		String toReturn = "";
		try {
			int num_ = generalUtil.getNullInt(defaultPrecision, 5);
			for(int i=0 ; i < num_; i++) {
				toReturn += "0";
			}
			toReturn = "#." + toReturn;
		} catch (Exception e) {
			toReturn = "#.00000";
		}
		return toReturn;
	}

	

}