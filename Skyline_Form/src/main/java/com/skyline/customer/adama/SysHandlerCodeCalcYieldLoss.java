package com.skyline.customer.adama;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;

public class SysHandlerCodeCalcYieldLoss extends SysHandler {
 
	@Override
	public String doHandler (Map<String,String> handlerMap, String formCode, String formId, Map<String, String> elementValueMap , String userId) throws ScriptException, ParseException {
		// TODO Auto-generated method stub
		
		super.doHandler(handlerMap, formCode, formId, elementValueMap, userId);	
		 String table = "WuDistilYieldRef"; //
		List<String> yieldLossFormIds = generalDao.getListOfStringBySql("select t.formid from Fg_s_"+table+"_ALL_V t where t.PARENTID='" + formId+ "' and t.SESSIONID is null");
		
		for (String yieldLossFormId : yieldLossFormIds) {
			StringBuilder sb = new StringBuilder();
			sb.append("Yield Loss Calculation =></br>");
			Map<String, String> yieldLossMap= generalDao.getMetaDataRowValues("select t.* from Fg_s_"+table+"_ALL_V t where t.formid='" + yieldLossFormId + "'");
			//String weightInProductSum= generalDao.selectSingleString("select SUM (t.WEIGHTINPRODUCT) from  fg_s_wudistfractionref_all_v t where t.MATERIAL_ID = '"+yieldLossMap.get("MATERIAL_ID")+"' and t.PARENTID = '"+formId+"' and t.SESSIONID is null");
			String weightInProductSum= generalDao.selectSingleString("select SUM (t.WEIGHT_IN_PROD) from  fg_s_wudistfractionref_all_v t where t.MATERIAL_ID = '"+yieldLossMap.get("MATERIAL_ID")+"' and t.PARENTID = '"+formId+"' and t.ACTIVE = 1 and t.SESSIONID is null");
			if(weightInProductSum != null) {
				Double quantityInFractions = getNormalNumber(weightInProductSum,yieldLossMap.get("QUANTITYINFRACTIONS_UOM"), 0d);
				Double initialQuantity = getNormalNumber(yieldLossMap.get("INITIALQUANTITY"),yieldLossMap.get("INITIALQUANTITY_UOM"), null);
				String yieldLossResult = "''";
				if(initialQuantity != null && initialQuantity != 0f) {
					try {
						sb.append("Formula: 1-(SUM(Weight_in_Product)/Initial_Quantity).</br> Normal Args: 1-("+quantityInFractions+"/"+initialQuantity+")."+"</br>");
						yieldLossResult = "'" + getFromNormalNumber(String.valueOf((1-(quantityInFractions / initialQuantity))), yieldLossMap.get("YIELDLOSS_UOM"),sb) + "'"; // * (100/100);
					} catch (Exception e) {
						// yieldLossResult = '' // quantityInFractions = 0
						generalUtilLogger.logWriter(LevelType.WARN,
								"WARN! SysHandlerCodeCalcYieldLoss yieldLossResult set to null! formCode=" + formCode + ", elementValueMap=" + generalUtil.mapToString("", elementValueMap),
								ActivitylogType.Calculation, formId, e,userId);
					}
				}
				
				List<String> colList = Arrays.asList("QUANTITYINFRACTIONS","YIELDLOSS");
				String sql_ = "update Fg_s_" + table + "_PIVOT set QUANTITYINFRACTIONS = '" + quantityInFractions + "', YIELDLOSS = " + yieldLossResult + " where FORMID = '"+ yieldLossMap.get("FORMID")+"'";
				formSaveDao.updateStructTableByFormId(sql_, "Fg_s_"+table+"_PIVOT", colList, yieldLossFormId);
			}
		}
		
		return "";
	}
}
