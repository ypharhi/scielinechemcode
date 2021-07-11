package com.skyline.customer.adama;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyline.form.dal.GeneralDao;
import com.skyline.form.service.GeneralUtil;

@Service
public class ExperimentReportSQLBuilder {
	
	@Autowired
	private GeneralDao generalDao;
	
	@Autowired
	private GeneralUtil generalUtil;
	
	/**
	 * for each row in the combine rules this function will create "with" sql part. 
	 * this will cause multiple rows (in case case more then one value found for each rule) for each experiment
	 * @param stateKey
	 * @return
	 */
	SQLObj getExpReportRulesFieldsSQL(long stateKey, String expIds) {
		
		StringBuilder sbWithSql = new StringBuilder();
		StringBuilder sbSelectSql = new StringBuilder();
		StringBuilder sbWhereSql = new StringBuilder();
		StringBuilder sbFromSql = new StringBuilder();
		int index = 0;
		
		List<Map<String,Object>> filterRefList = generalDao.getListOfMapsBySql("select T.ROWSTATEKEY,\n" + 
				"        T.TABLETYPE , \n" + 
				"        T.STEPNAME, \n" + 
				"        T.RULENAME, \n" + 
				"        T.RULECONDITION, \n" + 
				"        T.COLUMNSSELECTION,\n" + 
				"        T.COLUMNNAME\n" + 
				"from FG_S_REPORTFILTERREF_V T where 1=1 /*t.TABLETYPE in ('combineRules')*/ and t.ROWSTATEKEY='" + stateKey + "' order by to_number(t.formid) ");
		
		// make SQL for each filter row (filter row represent the "combine rules" and "display data" selection table)
		for (Map<String, Object> filterRefMap : filterRefList) { 
			String tableType = generalUtil.getNull((String)filterRefMap.get("TABLETYPE")); // <combineRules/displayData>
			String stepName = generalUtil.getNull((String)filterRefMap.get("STEPNAME")); // List of step Names (for example STEP 01,STEP 02)
			String ruleName = generalUtil.getNull((String)filterRefMap.get("RULENAME")); // <Main Solvent /Limiting Agent/Material Type>
			String columnName = generalUtil.getNull((String)filterRefMap.get("COLUMNNAME"));
			String columnsSelection = generalUtil.getNull((String)filterRefMap.get("COLUMNSSELECTION"));
			
			//check stepNames exists
			if(stepName == null || stepName.isEmpty()) {
				continue;
			}
			//prepare colnames_....
			Map<String,String> colMap = prepareColMap(ruleName, columnsSelection);
			if(colMap == null || colMap.isEmpty()) {
				continue;
			}
			
			
			System.out.println("tableType=" + tableType + ", stepName=" + stepName + ", ruleName=" + ruleName + ", columnName=" + columnName + ", columnsSelection=" + columnsSelection );
			//Limiting Agent
			if(ruleName.equalsIgnoreCase("Limiting Agent")) {
			}
			
			//Main Solvent
			if(ruleName.equalsIgnoreCase("Main Solvent")) { 
				String colName_ = (columnName == null || columnName.isEmpty())? ruleName: columnName; //TODO oracle limitation
				String[] stepNameArray = stepName.split(",", -1);
				
				for (String singleStepName : stepNameArray) {
					String aliasName = "CR" + index; // Alias 
					
					//with
					sbWithSql.append(((index == 0) ? "with ":", ") + "CR" + index + " as (\r\n" +
							" SELECT DISTINCT T.EXPERIMENT_ID AS EXPID\r\n" + 
							"   ,max(t.INVITEMMATERIALNAME) keep (dense_rank first order by to_number(fg_get_num_normal(t.QUANTITY,t.QUANTITYUOM_ID)) desc nulls last) over (partition by t.STEP_ID) as MAINNAME\r\n" + 
							(colMap.containsKey("QUANTITY") ? "   ,max(fg_get_num_display(t.QUANTITY,0,3)) keep (dense_rank first order by to_number(fg_get_num_normal(t.QUANTITY,t.QUANTITYUOM_ID)) desc nulls last) over (partition by t.STEP_ID) QUANTITY\r\n":"") +
							(colMap.containsKey("VOLUME") ? "   ,max(fg_get_num_display(t.VOLUME,0,3)) keep (dense_rank first order by to_number(fg_get_num_normal(t.QUANTITY,t.QUANTITYUOM_ID)) desc nulls last) over (partition by t.STEP_ID) as VOLUME\r\n":"") +
							"  FROM Fg_s_Materialref_All_v t \r\n" + 
							"  WHERE 1=1\r\n" + 
							"  AND T.EXPERIMENT_ID in (" + expIds + ")\r\n" + 
							"  AND   t.STEPNAME = '"  + singleStepName + "'\r\n" + 
							"  AND   t.TABLETYPE = 'Solvent'\r\n" + 
							")"); // and experiment id where part (or on temp table we create in the beginning for performance)
					
					//select
					sbSelectSql.append(
							"," + aliasName + ".MAINNAME as \"{" + index + "}" + colName_ + " " + singleStepName + "\"" +
							(colMap.containsKey("QUANTITY") ? "," + aliasName +  ".QUANTITY as \"{" + index + "} " + colMap.get("QUANTITY") + "\"":"") +
							(colMap.containsKey("VOLUME")   ? "," + aliasName +  ".VOLUME as \"{" + index + "} " + colMap.get("VOLUME") + "\"":"")
					);
					
					//from
					sbFromSql.append("," + aliasName);
					
					//where
					sbWhereSql.append(" AND EXPERIMENT_ID = " + aliasName + ".EXPID ");
					
					index++;
				}
				
			}
			
			//Material Type
			if(ruleName.equalsIgnoreCase("Material Type")) {
				
			}
			
			
		}
		
		
		return new SQLObj(sbWithSql.toString(),sbSelectSql.toString(),sbFromSql.toString(), sbWhereSql.toString());
		
	}
	
//	+	
//	+	/**
//	+	 * get the select SQL part from fg_s_ReportFilterRef_pivot, that holds the user select ion data in form the combine rules table.
//	+	 * @param stateKey - the number that defined the relevant rows (the fg_s_ReportFilterRef_pivot should be filtered by rowstatekey = stateKey, active = 1, and table type - TBD)
//	+	 * @return the SQL select part expression or empty (in case of exception or nothing to show) - the return value should end with comma in case there is at least one field
//	+	 */
//	+	private String getExpReportRulesFieldsSQL(long stateKey) {
//	+		// "/*1 as Dummy1, 2 as Dummy2,*/";
//	+		String toReturn  = "";
//	+		return toReturn;
//	+	}
	
	/**
	 * Insert the pivot data by stateKey for the report
	 * @param stateKey - the number that defined the relevant rows 
	 */
	void setExpReportPivotData(long stateKey, String stepIdCSV) {
		
//		generalDao.updateSingleString("delete from FG_P_EXPREPORT_DATA_TMP where statekey ='" + stateKey + "'");
//		generalDao.updateSingleString("insert into FG_P_EXPREPORT_DATA_TMP select tmp1.*,'" + stateKey + "' from (" + "sqlbuilder" + ") tmp1 where 1 = 1");

	}

	private Map<String, String> prepareColMap(String ruleName, String columnsSelection) {
		Map<String, String> colsMap = new HashMap<>();
		//TODO get colProp from getReactionAndResultsAnalysisColsSelection (NEW FUNC IN DAO RETURN THE CLASS VAL)
		String colProp = "ALL:All,QUANTITY:Qty,MOLE:Mole,VOLUME:Volume,PURITY:Purity,EQUIVALENT:Equivalent,INVITEMBATCHNAME:Batch";
		List<String> colList = Arrays.asList(colProp.split(","));
		if (!colList.isEmpty()) {
			for (String c : colList) {
				String id = c.split(":")[0];
				String val_ = c.split(":")[1];
				colsMap.put(id,val_);
			}
		}
		return colsMap;
	}


//	String getAllColumnsByType(String ruleName) {
//		generalUtil.getPropByName("reactionAndResultsAnalysisColsSelection", "NA");
//		// TODO Auto-generated method stub
//		return "QUANTITY,MOLE,VOLUME,PURITY,EQUIVALENT,INVITEMBATCHNAME"; //TODO get from prop
//	}
	
//	JSONObject getResultSamrtPivot(String ruleName, String columnsSelection) {
//		JSONObject smartPivoObj = new JSONObject();
//		//JSONObject groupObj = new JSONObject();
//		JSONArray columnObj = new JSONArray();
//		JSONArray valArray = new JSONArray();
//		
//		String colSelection_ = columnsSelection;
//		
//		// check if all (or empty)
//		if(colSelection_.isEmpty() || colSelection_.toLowerCase().startsWith("all")) { // TODO change according to data in case of all
//			colSelection_ = getAllColumnsByType(ruleName);
//		}
//		
//		String[] colSelectionArray = colSelection_.split(",", -1);
//		for (String col : colSelectionArray) {
////			columnObj.put(value)
//		}
//		
////		smartPivoObj.
//		
//		return null;
//		
//		
//	}

}
