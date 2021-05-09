package com.skyline.customer.adama;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.LookupType;

public class SysHandlerCodeCalcRate extends SysHandler {
	 
	@Override
	public String doHandler (Map<String,String> handlerMap, String formCode, String formId, Map<String, String> elementValueMap , String userId) throws ScriptException, ParseException {
		// TODO Auto-generated method stub
		
		super.doHandler(handlerMap, formCode, formId, elementValueMap, userId);
		
		//List<String> prevFormsId = generalDao.getListOfStringBySql("select lag(t.formid) over (partition by t.PARENTID order by t.formid)  from Fg_s_WuFeedMonitgRef_ALL_V t where t.PARENTID='" + elementValueMap.get("parentId") + "'");
		String sqll = "select QUANTITY, QUANTITY_UOM, prevFormId, prevQuantity, prevQuantity_uom, DIFF_MI from( select t.formid, t.QUANTITY, t.QUANTITY_UOM, lag(t.formid) over (partition by t.PARENTID order by t.formid) as prevFormId," 
				+"lag(t.QUANTITY) over (partition by t.PARENTID order by t.formid) as prevQuantity," 
				+"lag(t.QUANTITY_UOM) over (partition by t.PARENTID order by t.formid) as prevQuantity_uom  ,"
				+"fg_getdatediff('MI', to_date(t.time ,'HH24:MI'),to_date(NVL(lag(t.time) over (partition by t.PARENTID order by t.formid ),'00:00') ,'HH24:MI')) AS DIFF_MI "
				+"from Fg_s_WuFeedMonitgRef_ALL_V t where t.parentid = '" + elementValueMap.get("parentId") + "' " + generalUtilFormState.getWherePartForTmpData(formCode, elementValueMap.get("parentId")) + " )  where formid = '"+ formId +"'" ;
		Map<String, String> prevFormMap = generalDao.sqlToHashMap(sqll);
		StringBuilder sb = new StringBuilder();
		sb.append("Rate Calculation =></br>");
		String sResult = "''";
		try {
			Double result = (double) 0;
			if(!generalUtil.getNull(prevFormMap.get("PREVFORMID")).isEmpty()) {
				Double prevQuantity = getNormalNumber(prevFormMap.get("PREVQUANTITY"), prevFormMap.get("PREVQUANTITY_UOM"), 0d);	
				Double quantity = getNormalNumber(elementValueMap.get("quantity"), elementValueMap.get("QUANTITY_UOM"), 0d);
				String minUomId = "";
				List<String> uomList = formDao.getFromInfoLookupElementData("UOM", LookupType.NAME, "min", "ID");
				for(String uomId:uomList){
					Map<String,String> uomData= formDao.getFromInfoLookupAll("UOM", LookupType.ID, uomId);				
					if(uomData.get("UOMTYPENAME").equalsIgnoreCase("time")){
						minUomId = uomId;
						break;
					}
				}
				Double diff_time = getNormalNumber(prevFormMap.get("DIFF_MI"),minUomId,null);
				
				if(prevQuantity != null && quantity != null && diff_time != null && diff_time != 0f) {
					result = (quantity - prevQuantity)/(diff_time);		
					sb.append("Formula: (Quantity-Previuos_Quantity)/Difference_time.</br> Normal Args:("+quantity+"-"+prevQuantity+")/"+diff_time+"."+"</br>");
					sResult = "'" + getFromNormalNumber(result.toString(), elementValueMap.get("CALCRATE_UOM"),sb) + "'";
				}
			}
		} catch (Exception e) {
			// sResult = "''"
			generalUtilLogger.logWriter(LevelType.WARN,
					"WARN! SysHandlerCodeCalcRate sResult set to null! formCode=" + formCode + ", elementValueMap=" + generalUtil.mapToString("", elementValueMap),
					ActivitylogType.Calculation, formId, e, userId);
		}
		
		 List<String> colList = Arrays.asList("calcRate");
		 String sql_ = "update Fg_s_WuFeedMonitgRef_PIVOT set calcRate = " + sResult + " where FORMID = '"+formId+"'";
		 formSaveDao.updateStructTableByFormId(sql_, "Fg_s_WuFeedMonitgRef_PIVOT", colList, formId);

		return "";

	}

}
