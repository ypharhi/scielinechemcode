package com.skyline.customer.adama;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import com.skyline.form.bean.LookupType;

public class SysHandlerCodeCalcSetRecord extends SysHandler {
  
	@Override
	public String doHandler (Map<String,String> handlerMap, String formCode, String formId, Map<String, String> elementValueMap , String userId) throws ScriptException, ParseException {
		// TODO Auto-generated method stub
		
		super.doHandler(handlerMap, formCode, formId, elementValueMap, userId);
		
		List<String> records = generalDao.getListOfStringBySql("select t.formid from FG_S_wudiststartmixref_ALL_V t where t.ParentID='"+formId+"' and t.SESSIONID is null");
		List<String> yieldLossRecords = generalDao.getListOfStringBySql("select t.FORMIDREF from FG_S_WuDistilYieldRef_ALL_V t where t.ParentID='"+formId+"' and t.SESSIONID is null");
		records.removeAll(yieldLossRecords);
		for (String recordId : records) {
				
			Map<String, String> startMixMap= generalDao.getMetaDataRowValues("select t.* from Fg_s_wudiststartmixref_ALL_V t where t.formid='" + recordId + "'");
			
			String table = "WuDistilYieldRef";	
			String YIELDLOSS_UOM  = formDao.getFromInfoLookup("UOM", LookupType.NAME, "%","id") ;
			String newFormId  = formSaveDao.getStructFormId(table);
			String sql_ = "insert into FG_S_"+table+"_PIVOT" ////"WUDISTILYIELDREFNAME"
					+ " (TIMESTAMP,CHANGE_BY,CREATION_DATE,CREATED_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE, FORMIDREF,"
					+ "MATERIAL_ID, MATERIALNAME,ACTIVEINGREDIENT,INITIALQUANTITY,INITIALQUANTITY_UOM, QUANTITYINFRACTIONS_UOM ,YIELDLOSS_UOM)"				
					+ " VALUES (SYSDATE,'" + userId + "',SYSDATE,'" + userId + "', null,1,'"+newFormId+"','"+formId+"','"+table+"','"+recordId+ "','"
					+startMixMap.get("MATERIAL_ID")+ "','"+startMixMap.get("MATERIALNAME").replace("'", "''")+"','"+startMixMap.get("ACTIVEINGREDIENT") +"','"+startMixMap.get("QUANTITY")+"','"+startMixMap.get("QUANTITY_UOM")+"'"
							+ ",'"+startMixMap.get("QUANTITY_UOM")+"','"+YIELDLOSS_UOM+"')";
						
			formSaveDao.insertStructTableByFormId(sql_, "FG_S_"+table+"_PIVOT", formId);							
		}
		return "";

	}

}
