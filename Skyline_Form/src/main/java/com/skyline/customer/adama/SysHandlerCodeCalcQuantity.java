package com.skyline.customer.adama;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;

public class SysHandlerCodeCalcQuantity extends SysHandler {
	 
	@Override
	public String doHandler (Map<String,String> handlerMap, String formCode, String formId, Map<String, String> elementValueMap , String userId) throws ScriptException, ParseException {
		// TODO Auto-generated method stub
		
		super.doHandler(handlerMap, formCode, formId, elementValueMap, userId);
		
		String sql = "";	
		List<Map<String, Object>> formsMap = null;
		/*if(formCode.equals("WorkupFeeding"))
		{
			sql = "select FORMID,initialTemp, INITIALTEMP_UOM, MEANTEMP_UOM from fg_s_WuFeedMaterialRef_all_v where PARENTID='"+formId+"' and SESSIONID is null and active = 1";						
			formsMap = generalDao.getListOfMapsBySql(sql);

			for (Map<String, Object> formMap : formsMap) {
				String meanTemp = "''";//meanTemp= (initialTemp+finalTemperature)/2
				Float finalTemperature = getNormalNumber(elementValueMap.get("finalTemperature"),elementValueMap.get("FINALTEMP_UOM"), null);
				Float initialTemp = getNormalNumber(formMap.get("INITIALTEMP").toString(),formMap.get("INITIALTEMP_UOM").toString(), null);	
					
				try {
					if(initialTemp != null && finalTemperature != null) {
						meanTemp = "'" + getDisplayNumber(String.valueOf((initialTemp + finalTemperature)/2),formMap.get("MEANTEMP_UOM").toString()) + "'";
					}
				} catch (Exception e) {
					// meanTemp = "''"
					generalUtilLogger.logWriter(LevelType.WARN,
							"WARN! SysHandlerCodeCalcQuantity meanTemp set to null! formCode=" + formCode + ", elementValueMap=" + generalUtil.mapToString("", elementValueMap),
							ActivitylogType.Calculation, formId, e);
				}
				 
				List<String> colList = Arrays.asList("meanTemp");
				String sql_ = "update Fg_s_WuFeedMaterialRef_PIVOT set meanTemp = " + meanTemp + " where FORMID = '"+formMap.get("FORMID").toString()+"'";
				formSaveDao.updateStructTableByFormId(sql_, "Fg_s_WuFeedMaterialRef_PIVOT", colList, formMap.get("FORMID").toString());
			}
		}
		else */if(formCode.equals("WorkupCrystallize"))
		{			
			sql = "select FORMID,concentration, CONCENTRATION_UOM, QUANTITY_UOM from fg_s_WuCryMixDefineRef_all_v where PARENTID='"+formId+"' and SESSIONID is null and active = 1";						
			formsMap = generalDao.getListOfMapsBySql(sql);

			for (Map<String, Object> formMap : formsMap) {
				StringBuilder sb = new StringBuilder();
				sb.append("Quantity Calculation =></br>");
				String quantity = "''"; //quantity = initialAmount * concentration/100
				String quantity_uom = formMap.get("QUANTITY_UOM")==null?elementValueMap.get("INITIALAMOUNT_UOM"):formMap.get("QUANTITY_UOM").toString();
				Double initialAmount = getNormalNumber(elementValueMap.get("initialAmount"),elementValueMap.get("INITIALAMOUNT_UOM"), null);
				Double concentration = getNormalNumber(formMap.get("CONCENTRATION")==null?null:formMap.get("CONCENTRATION").toString(),formMap.get("CONCENTRATION_UOM").toString(), null);	
			
				try {
					if(initialAmount != null && concentration != null) {
						sb.append("Formula: (Initial_Amount*Concentration)/100.</br> Normal Args:("+initialAmount+"*"+concentration+")/100."+"</br>");
						quantity = "'" + getFromNormalNumber(String.valueOf((initialAmount * concentration)/100),quantity_uom,sb) + "'";
					}
				} catch (Exception e) {
					// quantity = "''"
					generalUtilLogger.logWriter(LevelType.WARN,
							"WARN! SysHandlerCodeCalcQuantity quantity set to null! formCode=" + formCode + ", elementValueMap=" + generalUtil.mapToString("", elementValueMap),
							ActivitylogType.Calculation, formId, e, null);
				}   
				
				List<String> colList = Arrays.asList("quantity,QUANTITY_UOM");
				String sql_ = "update Fg_s_WuCryMixDefineRef_PIVOT set quantity = " + quantity +",QUANTITY_UOM= '"+generalUtil.getNull(quantity_uom)+"' where FORMID = '"+formMap.get("FORMID").toString()+"'";
				formSaveDao.updateStructTableByFormId(sql_, "Fg_s_WuCryMixDefineRef_PIVOT", colList, formMap.get("FORMID").toString());
			}								
			 
		}
		else if(formCode.equals("WorkupDistillation"))
		{
			sql = "select FORMID,concentration, CONCENTRATION_UOM, concentration, QUANTITY_UOM from fg_s_WuDistStartMixRef_all_v where PARENTID='"+formId+"' and SESSIONID is null and active = 1";						
			formsMap = generalDao.getListOfMapsBySql(sql);

			for (Map<String, Object> formMap : formsMap) {
				StringBuilder sb = new StringBuilder();
				sb.append("Quantitys Calculation =></br>");
				String quantity = "''"; //quantity = initialAmount * concentration/100
				String quantity_uom = formMap.get("QUANTITY_UOM")==null?elementValueMap.get("INITIALAMOUNT_UOM"):formMap.get("QUANTITY_UOM").toString();
				Double initialAmount = getNormalNumber(elementValueMap.get("initialAmount"),elementValueMap.get("INITIALAMOUNT_UOM"), null);
				Double concentration = getNormalNumber(formMap.get("CONCENTRATION").toString(),formMap.get("CONCENTRATION_UOM").toString(), null);	
					 
				try {
					if(initialAmount != null && concentration != null) {
						sb.append("</br>Formula: (Initial_Amount*Concentraion)/100.</br> Normal Args:("+initialAmount+"*"+concentration+")/100."+"</br>");
						quantity = "'" + getFromNormalNumber(String.valueOf((initialAmount * concentration)/100),quantity_uom,sb) + "'";
					}
				} catch (Exception e) {
					// quantity = "''"
					generalUtilLogger.logWriter(LevelType.WARN,
							"WARN! SysHandlerCodeCalcQuantity quantity set to null! formCode=" + formCode + ", elementValueMap=" + generalUtil.mapToString("", elementValueMap),
							ActivitylogType.Calculation, formId, e, null);
				}  
				
				List<String> colList = Arrays.asList("quantity,QUANTITY_UOM");
				String sql_ = "update Fg_s_WuDistStartMixRef_PIVOT set quantity = " + quantity +",QUANTITY_UOM= '"+generalUtil.getNull(quantity_uom)+"' where FORMID = '"+formMap.get("FORMID").toString()+"'";
				formSaveDao.updateStructTableByFormId(sql_, "Fg_s_WuDistStartMixRef_PIVOT", colList, formMap.get("FORMID").toString());
			}	
		}
		
		return "";

	}
	
	
}
