package com.skyline.customer.adama;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 * for each row in the FG_S_REPORTFILTERREF_V (combineRules and displayData):
	 * 1) combineRules: append "with" sql part - because multi rows is needed in case of duplication (multiple experiment rows)
	 * 2) displayData: append SMARTPIVOT data into FG_P_EXPREPORT_DATA_TMP - because multi columns needed in case of duplication as the SMARTPIVOT mechanism provides
	 * TODO - result
	 * @param stateKey - to identify the data in FG_S_REPORTFILTERREF_V (the user selection)
	 * @return
	 */
	SQLObj getExpReportRulesFieldsSQL(long stateKey, String expIds, String stepIds, String sampleIds, Map<String,String> materialTypeTableMap) {
		
		StringBuilder sbWithSql = new StringBuilder();
		StringBuilder sbSelectSql = new StringBuilder();
		StringBuilder sbSelectHiddebSql = new StringBuilder();
		StringBuilder sbWhereSql = new StringBuilder();
		StringBuilder sbFromSql = new StringBuilder();
		StringBuilder sbPivotSql = new StringBuilder();

		int index = 0;
		
		//prepare...
		generalDao.updateSingleString("delete from FG_P_EXPREPORT_DATA_TMP where statekey ='" + stateKey + "'");
		
		String sqlFilterRef = "select T.ROWSTATEKEY,\n" + 
				"        T.TABLETYPE , \n" + 
				"        T.STEPNAME, \n" + 
				"        T.RULENAME, \n" + 
				"        T.RULECONDITION, \n" + 
				"        T.COLUMNSSELECTION,\n" + 
				"        T.COLUMNNAME,\n" + 
				"        t.TYPE_, t.REPORTFILTERREFNAME,t.LEVEL_, t.TABLETYPE \n" +
				" from FG_S_REPORTFILTERREF_V T \n" +
				" where t.active = 1 \n" +
				" and t.TABLETYPE in ('combineRules','displayData') \n" +
				" and t.ROWSTATEKEY='" + stateKey + "' \n" +
				" order by t.TABLETYPE, to_number(t.formid) ";
		
		List<Map<String,Object>> filterRefList = generalDao.getListOfMapsBySql(sqlFilterRef);
		
		// make SQL for each filter row (filter row represent the "combine rules" and "display data" selection table)
		for (Map<String, Object> filterRefMap : filterRefList) { 
			
			String tableType = generalUtil.getNull((String)filterRefMap.get("TABLETYPE")); // <combineRules / displayData>
			String columnsSelection = generalUtil.getNull((String)filterRefMap.get("COLUMNSSELECTION"));
			String columnName = generalUtil.getNull((String)filterRefMap.get("COLUMNNAME"));

			//-----------------------------------------------------------------------------
			//-------------------------- combineRules: SQL - SHOULD DUPLICATE THE EXPERIMENT ROW IN CASE MORE THEN ONE VALUE FIT (=> implement as with)
			//-----------------------------------------------------------------------------
			if(tableType.equalsIgnoreCase("combineRules")) {
				
				String stepName = generalUtil.getNull((String)filterRefMap.get("STEPNAME")); // List of step Names (for example STEP 01,STEP 02)
				String ruleName = generalUtil.getNull((String)filterRefMap.get("RULENAME")); // <Main Solvent /Limiting Agent/Material Type>
				String ruleCondition = generalUtil.getNull((String)filterRefMap.get("RULECONDITION")); // <Main Solvent /Limiting Agent/Material Type>
				String defaultColName = ruleCondition.isEmpty()?ruleName:ruleCondition;
				
				//check stepNames exists
				if(stepName.isEmpty()) {
					continue;
				}
				
				//prepare colnames_....
				Map<String,String> colMap = prepareColMap(ruleName, columnsSelection);
				if(colMap == null || colMap.isEmpty()) {
					continue;
				}
				
				// ******************************************
				// ************* Limiting Agent *************
				// ******************************************
				if(ruleName.equalsIgnoreCase("Limiting Agent")) {
					String colName_ = (columnName == null || columnName.isEmpty())? defaultColName: columnName; 
					String[] stepNameArray = stepName.split(",", -1);
					
					// for each step in the user selection row
					for (String singleStepName : stepNameArray) {
						String aliasName = "CR" + index; // Alias
						
						//with
						sbWithSql.append(((index == 0) ? "with ":", ") + "CR" + index + " as (\r\n" +
								" SELECT DISTINCT T.EXPERIMENT_ID AS EXPID\r\n" + 
								"   ,max(t.INVITEMMATERIALNAME) keep (dense_rank first order by t.MATERIALREF_ID desc nulls last) over (partition by t.STEP_ID) as MAINNAME\r\n" + 
								(colMap.containsKey("QUANTITY") ? "   ,max(fg_get_num_display(t.QUANTITY,0,3)) keep (dense_rank first order by t.MATERIALREF_ID desc nulls last) over (partition by t.STEP_ID) as QUANTITY\r\n":"") +
								(colMap.containsKey("MOLE") ? "   ,max(fg_get_num_display(t.MOLE,0,3)) keep (dense_rank first order by t.MATERIALREF_ID desc nulls last) over (partition by t.STEP_ID) as MOLE\r\n":"") +
								(colMap.containsKey("VOLUME") ? "   ,max(fg_get_num_display(t.VOLUME,0,3)) keep (dense_rank first order by t.MATERIALREF_ID desc nulls last) over (partition by t.STEP_ID) as VOLUME\r\n":"") +
								(colMap.containsKey("PURITY") ? "   ,max(fg_get_num_display(nvl(nvl(t.PURITYINF,t.PURITY),100),0,3)) keep (dense_rank first order by t.MATERIALREF_ID desc nulls last) over (partition by t.STEP_ID) as PURITY\r\n":"") +
								(colMap.containsKey("EQUIVALENT") ? "   ,max(fg_get_num_display(t.EQUIVALENT,0,3)) keep (dense_rank first order by t.MATERIALREF_ID desc nulls last) over (partition by t.STEP_ID) as EQUIVALENT\r\n":"") +
								(colMap.containsKey("INVITEMBATCHNAME") ? "   ,max(t.INVITEMBATCHNAME) keep (dense_rank first order by t.MATERIALREF_ID desc nulls last) over (partition by t.STEP_ID) as INVITEMBATCHNAME\r\n":"") +
								
								"  FROM Fg_s_Materialref_All_v t \r\n" + 
								"  WHERE t.sessionid is null and t.active=1 \r\n" + 
								"  AND t.STEP_ID in (" + stepIds + ")\r\n" + 
								"  AND lower(t.STEPNAME) = lower('"  + singleStepName + "')\r\n" + 
								"  AND t.TABLETYPE = 'Reactant'\r\n" + //
								"  AND t.LIMITINGAGENT = 1\r\n" + 
								")"); // and experiment id where part (or on temp table we create in the beginning for performance)
						
						//select
						sbSelectSql.append(
								"," + aliasName + ".MAINNAME as \"" + getValidOracleColumnName("{" + index + "}" + singleStepName + " - " + colName_) + "\"" +
										(colMap.containsKey("QUANTITY") ? "," + aliasName +  ".QUANTITY as \"{" + index + "} " + colMap.get("QUANTITY") + "\"":"") +
										(colMap.containsKey("MOLE")   ? "," + aliasName +  ".MOLE as \"{" + index + "} " + colMap.get("MOLE") + "\"":"") +
										(colMap.containsKey("VOLUME")   ? "," + aliasName +  ".VOLUME as \"{" + index + "} " + colMap.get("VOLUME") + "\"":"") +
										(colMap.containsKey("PURITY")   ? "," + aliasName +  ".PURITY as \"{" + index + "} " + colMap.get("PURITY") + "\"":"") +
										(colMap.containsKey("EQUIVALENT")   ? "," + aliasName +  ".EQUIVALENT as \"{" + index + "} " + colMap.get("EQUIVALENT") + "\"":"") +
										(colMap.containsKey("INVITEMBATCHNAME")   ? "," + aliasName +  ".INVITEMBATCHNAME as \"{" + index + "} " + colMap.get("INVITEMBATCHNAME") + "\"":"")
								);
						
						//from
						sbFromSql.append("," + aliasName);
						
						//where
						sbWhereSql.append(" AND t.EXPERIMENT_ID = " + aliasName + ".EXPID(+)");
						
						index++;
					}
					
				}
				
				// ****************************************
				// ************* Main Solvent *************
				// ****************************************
				if(ruleName.equalsIgnoreCase("Main Solvent")) { 
					String colName_ = (columnName == null || columnName.isEmpty())? defaultColName: columnName; 
					String[] stepNameArray = stepName.split(",", -1);
					
					// for each step in the user selection row
					for (String singleStepName : stepNameArray) {
						String aliasName = "CR" + index; // Alias
						
						//with
						sbWithSql.append(((index == 0) ? "with ":", ") + "CR" + index + " as (\r\n" +
								" SELECT DISTINCT T.EXPERIMENT_ID AS EXPID\r\n" + 
								"   ,max(t.INVITEMMATERIALNAME) keep (dense_rank first order by to_number(fg_get_num_normal(t.QUANTITY,t.QUANTITYUOM_ID)) desc nulls last) over (partition by t.STEP_ID) as MAINNAME\r\n" + 
								(colMap.containsKey("QUANTITY") ? "   ,max(fg_get_num_display(t.QUANTITY,0,3)) keep (dense_rank first order by to_number(fg_get_num_normal(t.QUANTITY,t.QUANTITYUOM_ID)) desc nulls last) over (partition by t.STEP_ID) QUANTITY\r\n":"") +
								(colMap.containsKey("MOLE") ? "   ,max(fg_get_num_display(t.MOLE,0,3)) keep (dense_rank first order by to_number(fg_get_num_normal(t.QUANTITY,t.QUANTITYUOM_ID)) desc nulls last) over (partition by t.STEP_ID) as MOLE\r\n":"") +
								(colMap.containsKey("VOLUME") ? "   ,max(fg_get_num_display(t.VOLUME,0,3)) keep (dense_rank first order by to_number(fg_get_num_normal(t.QUANTITY,t.QUANTITYUOM_ID)) desc nulls last) over (partition by t.STEP_ID) as VOLUME\r\n":"") +
								(colMap.containsKey("PURITY") ? "   ,max(fg_get_num_display(nvl(nvl(t.PURITYINF,t.PURITY),100),0,3)) keep (dense_rank first order by to_number(fg_get_num_normal(t.QUANTITY,t.QUANTITYUOM_ID)) desc nulls last) over (partition by t.STEP_ID) as PURITY\r\n":"") +
								(colMap.containsKey("EQUIVALENT") ? "   ,max(fg_get_num_display(t.EQUIVALENT,0,3)) keep (dense_rank first order by to_number(fg_get_num_normal(t.QUANTITY,t.QUANTITYUOM_ID)) desc nulls last) over (partition by t.STEP_ID) as EQUIVALENT\r\n":"") +
								//(colMap.containsKey("INVITEMBATCHNAME") ? "   ,max(t.INVITEMBATCHNAME) keep (dense_rank first order by to_number(fg_get_num_normal(t.QUANTITY,t.QUANTITYUOM_ID)) desc nulls last) over (partition by t.STEP_ID) as INVITEMBATCHNAME\r\n":"") +
								
								"  FROM Fg_s_Materialref_All_v t \r\n" + 
								"  WHERE t.sessionid is null and t.active=1\r\n" + 
								"  AND t.STEP_ID in (" + stepIds + ")\r\n" + 
								"  AND lower(t.STEPNAME) = lower('"  + singleStepName + "')\r\n" + 
								"  AND t.TABLETYPE = 'Solvent'\r\n" + 
								")"); // and experiment id where part (or on temp table we create in the beginning for performance)
						
						//select
						sbSelectSql.append(
								"," + aliasName + ".MAINNAME as \"" + getValidOracleColumnName("{" + index + "}" + singleStepName + " - " + colName_) + "\"" +
										(colMap.containsKey("QUANTITY") ? "," + aliasName +  ".QUANTITY as \"{" + index + "} " + colMap.get("QUANTITY") + "\"":"") +
										(colMap.containsKey("MOLE")   ? "," + aliasName +  ".MOLE as \"{" + index + "} " + colMap.get("MOLE") + "\"":"") +
										(colMap.containsKey("VOLUME")   ? "," + aliasName +  ".VOLUME as \"{" + index + "} " + colMap.get("VOLUME") + "\"":"") +
										(colMap.containsKey("PURITY")   ? "," + aliasName +  ".PURITY as \"{" + index + "} " + colMap.get("PURITY") + "\"":"") +
										(colMap.containsKey("EQUIVALENT")   ? "," + aliasName +  ".EQUIVALENT as \"{" + index + "} " + colMap.get("EQUIVALENT") + "\"":"")
										//(colMap.containsKey("INVITEMBATCHNAME")   ? "," + aliasName +  ".INVITEMBATCHNAME as \"{" + index + "} " + colMap.get("INVITEMBATCHNAME") + "\"":"")
								);
						
						//from
						sbFromSql.append("," + aliasName);
						
						//where
						sbWhereSql.append(" AND t.EXPERIMENT_ID = " + aliasName + ".EXPID(+)");
						
						index++;
					}
				}
				
				// *****************************************
				// ************* Material Type *************
				// *****************************************
				if(ruleName.equalsIgnoreCase("Material Type")) { 
					
					String colName_ = (columnName == null || columnName.isEmpty())? getMaterialTypeNames(defaultColName, materialTypeTableMap): columnName; 
					String[] stepNameArray = stepName.split(",", -1);
					
					// for each step in the user selection row
					for (String singleStepName : stepNameArray) {
						String aliasName = "CR" + index; // Alias
						
						//with
						sbWithSql.append(((index == 0) ? "with ":", ") + "CR" + index + " as (\r\n" +
								" SELECT DISTINCT T.EXPERIMENT_ID AS EXPID\r\n" + 
								"   ,t.INVITEMMATERIALNAME as MAINNAME\r\n" + 
								(colMap.containsKey("QUANTITY") ? "   ,fg_get_num_display(t.QUANTITY,0,3) as QUANTITY\r\n":"") +
								(colMap.containsKey("MOLE") ? "   ,fg_get_num_display(t.MOLE,0,3) as MOLE\r\n":"") +
								(colMap.containsKey("VOLUME") ? "   ,fg_get_num_display(t.VOLUME,0,3) as VOLUME\r\n":"") +
								(colMap.containsKey("PURITY") ? "   ,fg_get_num_display(nvl(nvl(t.PURITYINF,t.PURITY),100),0,3) as PURITY\r\n":"") +
								(colMap.containsKey("EQUIVALENT") ? "   ,fg_get_num_display(t.EQUIVALENT,0,3) as EQUIVALENT\r\n":"") +
								(colMap.containsKey("INVITEMBATCHNAME") ? "   ,t.INVITEMBATCHNAME as INVITEMBATCHNAME\r\n":"") +
								
								"  FROM Fg_s_Materialref_All_v t, FG_I_CONN_MATERIAL_TYPE_V mt \r\n" + 
								"  WHERE 1=1\r\n" + 
								"  AND t.STEP_ID in (" + stepIds + ")\r\n" + 
								"  AND lower(t.STEPNAME) = lower('"  + singleStepName + "')\r\n" + 
								"  AND t.INVITEMMATERIAL_ID = mt.INVITEMMATERIAL_ID\r\n" + 
								"  AND instr(',' || '" + ruleCondition + "' || ',', ','||mt.MATERIALTYPE_ID||',') > 0\r\n" +
								")"); // and experiment id where part (or on temp table we create in the beginning for performance)
						
						//select
						sbSelectSql.append(
								"," + aliasName + ".MAINNAME as \"" + getValidOracleColumnName("{" + index + "}" + singleStepName + " - " + colName_) + "\"" +
										(colMap.containsKey("QUANTITY") ? "," + aliasName +  ".QUANTITY as \"{" + index + "} " + colMap.get("QUANTITY") + "\"":"") +
										(colMap.containsKey("MOLE")   ? "," + aliasName +  ".MOLE as \"{" + index + "} " + colMap.get("MOLE") + "\"":"") +
										(colMap.containsKey("VOLUME")   ? "," + aliasName +  ".VOLUME as \"{" + index + "} " + colMap.get("VOLUME") + "\"":"") +
										(colMap.containsKey("PURITY")   ? "," + aliasName +  ".PURITY as \"{" + index + "} " + colMap.get("PURITY") + "\"":"") +
										(colMap.containsKey("EQUIVALENT")   ? "," + aliasName +  ".EQUIVALENT as \"{" + index + "} " + colMap.get("EQUIVALENT") + "\"":"") +
										(colMap.containsKey("INVITEMBATCHNAME")   ? "," + aliasName +  ".INVITEMBATCHNAME as \"{" + index + "} " + colMap.get("INVITEMBATCHNAME") + "\"":"")
								);
						
						//from
						sbFromSql.append("," + aliasName);
						
						//where
						sbWhereSql.append(" AND t.EXPERIMENT_ID = " + aliasName + ".EXPID(+)");
						
						index++;
					}
					
				}
				
				// ******************************************
				// ************* Limiting Agent *************
				// ******************************************
				if(ruleName.equalsIgnoreCase("Experiment Materials")) {
					
					String colName_ = (columnName == null || columnName.isEmpty())? getMaterialNames(defaultColName, materialTypeTableMap): columnName; 
					String[] stepNameArray = stepName.split(",", -1);
					
					// for each step in the user selection row
					for (String singleStepName : stepNameArray) {
						String aliasName = "CR" + index; // Alias
						
						//with
						sbWithSql.append(((index == 0) ? "with ":", ") + "CR" + index + " as (\r\n" +
								" SELECT DISTINCT T.EXPERIMENT_ID AS EXPID\r\n" + 
								"   ,t.INVITEMMATERIALNAME as MAINNAME\r\n" + 
								(colMap.containsKey("QUANTITY") ? "   ,fg_get_num_display(t.QUANTITY,0,3) as QUANTITY\r\n":"") +
								(colMap.containsKey("MOLE") ? "   ,fg_get_num_display(t.MOLE,0,3) as MOLE\r\n":"") +
								(colMap.containsKey("VOLUME") ? "   ,fg_get_num_display(t.VOLUME,0,3) as VOLUME\r\n":"") +
								(colMap.containsKey("PURITY") ? "   ,fg_get_num_display(nvl(nvl(t.PURITYINF,t.PURITY),100),0,3) as PURITY\r\n":"") +
								(colMap.containsKey("EQUIVALENT") ? "   ,fg_get_num_display(t.EQUIVALENT,0,3) as EQUIVALENT\r\n":"") +
								(colMap.containsKey("INVITEMBATCHNAME") ? "   ,t.INVITEMBATCHNAME as INVITEMBATCHNAME\r\n":"") +
								
								"  FROM Fg_s_Materialref_All_v t\r\n" + 
								"  WHERE t.sessionid is null and t.active=1\r\n" + 
								"  AND t.STEP_ID in (" + stepIds + ")\r\n" + 
								"  AND lower(t.STEPNAME) = lower('"  + singleStepName + "')\r\n" +
								"  AND instr(',' || '" + ruleCondition + "' || ',', ','||t.INVITEMMATERIAL_ID||',') > 0\r\n" +
								")"); // and experiment id where part (or on temp table we create in the beginning for performance)
						
						//select
						sbSelectSql.append(
								"," + aliasName + ".MAINNAME as \"" + getValidOracleColumnName("{" + index + "}" + singleStepName + " - " + colName_) + "\"" +
										(colMap.containsKey("QUANTITY") ? "," + aliasName +  ".QUANTITY as \"{" + index + "} " + colMap.get("QUANTITY") + "\"":"") +
										(colMap.containsKey("MOLE")   ? "," + aliasName +  ".MOLE as \"{" + index + "} " + colMap.get("MOLE") + "\"":"") +
										(colMap.containsKey("VOLUME")   ? "," + aliasName +  ".VOLUME as \"{" + index + "} " + colMap.get("VOLUME") + "\"":"") +
										(colMap.containsKey("PURITY")   ? "," + aliasName +  ".PURITY as \"{" + index + "} " + colMap.get("PURITY") + "\"":"") +
										(colMap.containsKey("EQUIVALENT")   ? "," + aliasName +  ".EQUIVALENT as \"{" + index + "} " + colMap.get("EQUIVALENT") + "\"":"") +
										(colMap.containsKey("INVITEMBATCHNAME")   ? "," + aliasName +  ".INVITEMBATCHNAME as \"{" + index + "} " + colMap.get("INVITEMBATCHNAME") + "\"":"")
								);
						
						//from
						sbFromSql.append("," + aliasName);
						
						//where
						sbWhereSql.append(" AND t.EXPERIMENT_ID = " + aliasName + ".EXPID(+)");
						
						index++;
					}
					
				}
			}
			
			//-----------------------------------------------------------------------------
			//-------------------------- displayData - SHOULD DUPLICATE THE COLUMNS IN CASE OF DUPLICATION (=> implement using the SMARTPIVOT mechanism)
			//-----------------------------------------------------------------------------
			if(tableType.equalsIgnoreCase("displayData")) {
				
				String displayType = generalUtil.getNull((String)filterRefMap.get("TYPE_")); // <Material/Parameter>
				String displayObjId = generalUtil.getNull((String)filterRefMap.get("REPORTFILTERREFNAME")); //Material id or Parameter id
				String displayLevel = generalUtil.getNull((String)filterRefMap.get("LEVEL_")); // Material => List of step Names (for example STEP 01,STEP 02) / Parameter => List of step Names and Experiment numbers
				
				//check stepNames exists
				if(displayLevel.isEmpty() || displayObjId.isEmpty()) {
					continue;
				}
				
				//prepare colnames_....
				Map<String,String> colMap = prepareColMap(displayType, columnsSelection);
				if(colMap == null || colMap.isEmpty()) {
					continue;
				}

				
				// #####################################
				// Material (pivot data) using UNIQUEROW as key that is experiment_id (in case no sample) or UNIQUEROW as in FG_R_EXPREPORT_PIVOT_DT_V 
				// #####################################
				if(displayType.equalsIgnoreCase("Material")) {
					String colName_ = (columnName == null || columnName.isEmpty())? getDisplayDataName(displayObjId, displayType): columnName;
					String[] displayLevelArray = displayLevel.split(",", -1);
					
					// for each step in the user selection row
					for (String singleDisplayName : displayLevelArray) {

						String col_ =  "\"" + ("{" + displayObjId + "-" + singleDisplayName + "}" + singleDisplayName + " - " + colName_) + "\"" +
								(colMap.containsKey("QUANTITY") ? "," + "\"" + ("{" + displayObjId + "}" + singleDisplayName + "-" + colMap.get("QUANTITY")) + "\"": "") +
								(colMap.containsKey("MOLE") ? "," + "\"" + ("{" + displayObjId + "}" + singleDisplayName + "-" + colMap.get("MOLE")) + "\"": "") +
								(colMap.containsKey("VOLUME") ? "," + "\"" + ("{" + displayObjId + "}" + singleDisplayName + "-" + colMap.get("VOLUME")) + "\"": "") +
								(colMap.containsKey("PURITY") ? "," + "\"" + ("{" + displayObjId + "}" + singleDisplayName + "-" + colMap.get("PURITY")) + "\"": "") +
								(colMap.containsKey("EQUIVALENT") ? "," + "\"" + ("{" + displayObjId + "}" + singleDisplayName + "-" + colMap.get("EQUIVALENT")) + "\"": "") +
								(colMap.containsKey("INVITEMBATCHNAME") ? "," + "\"" + ("{" + displayObjId + "}" + singleDisplayName + "-" + colMap.get("INVITEMBATCHNAME")) + "\"": "") +
								(colMap.containsKey("MASS") ? "," + "\"" + ("{" + displayObjId + "}" + singleDisplayName + "-" +  colMap.get("MASS")) + "\"": "") +
								(colMap.containsKey("MW") ? "," + "\"" + ("{" + displayObjId + "}" + singleDisplayName + "-" +  colMap.get("MW")) + "\"": "");
						
						String val_ = "\" ' || (invitemmaterialname) || '\"" +
								(colMap.containsKey("QUANTITY") ? ",\" ' || fg_get_num_display(t.QUANTITY,0,3) || '\"" : "") +
								(colMap.containsKey("MOLE") ? ",\" ' || fg_get_num_display(t.MOLE,0,3) || '\"" : "") +
								(colMap.containsKey("VOLUME") ? ",\" ' || fg_get_num_display(t.VOLUME,0,3) || '\"" : "") +
								(colMap.containsKey("PURITY") ? ",\" ' || fg_get_num_display(nvl(nvl(t.PURITYINF,t.PURITY),100),0,3) || '\"" : "") +
								(colMap.containsKey("EQUIVALENT") ? ",\" ' || fg_get_num_display(t.EQUIVALENT,0,3) || '\"" : "") +
								(colMap.containsKey("INVITEMBATCHNAME") ? ",\" ' || t.INVITEMBATCHNAME || '\"" : "") +
								(colMap.containsKey("MASS") ? ",\" ' || fg_get_num_display(t.MASS,0,3) || '\"" : "") +
								(colMap.containsKey("MW") ? ",\" ' || fg_get_num_display(t.MW,0,3) || '\"" : "");
						
						
						String pivotFormat = "'{pivotkey:\"'|| nvl(s.SAMPLE_ID || '_' || t.experiment_id,t.experiment_id) ||'\",pivotkeyname:\"UNIQUEROW\",column:[" + col_ + "],val:[" + val_ + "]}'";
						
						if(sbPivotSql.length() > 0) {
							sbPivotSql.append("\n union all \n");
						}
						
						sbPivotSql.append(" Select distinct " + stateKey + " as stateKey ," + index + " as order_, null as order2," + pivotFormat + " as result_SMARTPIVOT\n" +
								"  FROM Fg_s_Materialref_All_v t, fg_s_sample_v s\r\n" + 
								"  WHERE t.experiment_id = s.experiment_id(+) and t.sessionid is null and t.active=1\r\n" + 
								"  AND t.STEP_ID in (" + stepIds + ")\r\n" + 
								//"  AND t.TABLETYPE = 'Reactant'\r\n" +  
								"  AND s.sample_id(+) in (" + (sampleIds.isEmpty()?"-1":sampleIds) + ") \r\n" +
								"  AND instr(',' || '" + displayObjId + "' || ',', ','||t.INVITEMMATERIAL_ID||',') > 0\r\n" +
								"  AND lower(t.STEPNAME) = lower('"  + singleDisplayName + "')");
						
						index++;
					}
				}
				
				// #####################################
				// Parameter (pivot data) using UNIQUEROW as key that is experiment_id (in case no sample) or UNIQUEROW as in FG_R_EXPREPORT_PIVOT_DT_V 
				// #####################################
				if(displayType.equalsIgnoreCase("Parameter")) { // TODO connect also to UNIQUEROW
					String colName_ = (columnName == null || columnName.isEmpty())? getDisplayDataName(displayObjId, displayType): columnName;
					String[] displayLevelArray = displayLevel.split(",", -1);

					// for each step in the user selection row
					for (String singleDisplayName : displayLevelArray) {
						
						String col_ = "\"" + ("{" + displayObjId + "-" + singleDisplayName + "}" + singleDisplayName + " - " + colName_) + "\"" +
								(colMap.containsKey("VAL1") ? "," + "\"" + ("{" + displayObjId + "-" + singleDisplayName + "}" + colMap.get("VAL1")) + "\"": "") +
								(colMap.containsKey("VAL2") ? "," + "\"" + ("{" + displayObjId  + "-" + singleDisplayName + "}" + colMap.get("VAL2")) + "\"": "");
						
						
						String val_ = "\" ' || (t.parametername) || '\"" +
								(colMap.containsKey("VAL1") ? ",\" ' || t.PARAMETERSCRITERIANAME || t.VAL1 || '\"" : "") +
								(colMap.containsKey("VAL2") ? ",\" ' || t.PARAMETERSCRITERIANAME || t.VAL2 || '\"" : "");
//						(colMap.containsKey("VAL1") ? ",\" ' || t.PLANNEDPARAMETERSCRITERIANAME || t.PLANNEDVAL1 || '\"" : "") +
//						(colMap.containsKey("VAL2") ? ",\" ' || t.PLANNEDPARAMETERSCRITERIANAME || t.PLANNEDVAL2 || '\"" : "");
						
						String pivotFormat = "'{pivotkey:\"'|| nvl(s.SAMPLE_ID || '_' || t.experiment_id,t.experiment_id) ||'\",pivotkeyname:\"UNIQUEROW\",column:[" + col_ + "],val:[" + val_ + "]}'";
						
						if(sbPivotSql.length() > 0) {
							sbPivotSql.append("\n union all \n");
						}
						
						sbPivotSql.append(" Select distinct " + stateKey + " as stateKey ," + index + " as order_, null as order2," + pivotFormat + " as result_SMARTPIVOT\n" +
								"  from FG_S_PARAMREF_ALL_V t, fg_s_sample_v s\r\n" + 
								"WHERE t.experiment_id = s.experiment_id(+) and t.SESSIONID is null and t.ACTIVE = 1 AND T.VAL1 is not null\r\n" + 
//								"WHERE t.experiment_id = r.experiment_id(+) and  t.SESSIONID is null and t.ACTIVE = 1 AND T.PLANNEDVAL1 is not null\r\n" + 
								"AND instr(',' || '" + displayObjId + "' || ',', ','||t.PARAMETER_ID||',') > 0\r\n" +
								"AND s.sample_id(+) in (" + (sampleIds.isEmpty()?"-1":sampleIds) + ") \r\n" +
								"AND ((T.step_id IN (" + stepIds + ") AND T.STEPNAME ='" + singleDisplayName + "') OR (T.experiment_id IN (" + expIds + ") AND T.EXP_FORMNUMBERID = '" + singleDisplayName + "'))");
						
						index++;
					}
				}
			}
		}
		
		// #####################################
		// results (pivot data) - using FG_P_EXPREPORT_RESULT_V (made from FG_P_EXPERIMENTRESULTS_V as in experiment organic result tab - with the changes relevant to this report)
		// #####################################
		if(sampleIds != null && !sampleIds.isEmpty()) {
			if(sbPivotSql.length() > 0) {
				sbPivotSql.append("\n union all \n");
			} 
			sbPivotSql.append("SELECT distinct " + stateKey + " as stateKey , order_, order2,  result_SMARTPIVOT \n FROM FG_P_EXPREPORT_RESULT_V \n where SAMPLE_ID in (" + sampleIds + ") ");
		}
		
		// *********** pivot data
		if(sbPivotSql.length() > 0) {
			String inserSql = "insert into FG_P_EXPREPORT_DATA_TMP (statekey, order_, order2, result_SMARTPIVOT) " + sbPivotSql.toString();
			String numRows = generalDao.updateSingleString(inserSql);
			if(numRows != null && !numRows.equals("0")) {
				sbSelectSql.append(",'SELECT result_SMARTPIVOT FROM FG_P_EXPREPORT_DATA_TMP where statekey=''" + stateKey + "'' order by order_, order2' AS RESULT_SMARTPIVOTSQL\n" ); 
			}
		}
		
		
		// return the sql obj...
		return new SQLObj(sbWithSql.toString(), sbSelectHiddebSql.toString(), sbSelectSql.toString(),sbFromSql.toString(), sbWhereSql.toString(), "");
	}
	
	private String getDisplayDataName(String displayObjId, String displayType) {
		String toReturn = "[" + displayObjId + "]";
		if(displayType.equalsIgnoreCase("Material")) {
			try {
				List <String> matList = generalDao.getListOfStringBySql("select distinct invitemmaterialname from fg_s_invitemmaterial_v where invitemmaterial_id in (" + displayObjId + ")");
				toReturn = generalUtil.listToCsv(matList);
			} catch (Exception e) {
				toReturn = "Material";
			}
		}
		else if(displayType.equalsIgnoreCase("Parameter")) {
			try {
				List <String> matList = generalDao.getListOfStringBySql("SELECT t.\"Name\" FROM FG_S_MP_DT_V t WHERE t.MP_ID in (" + displayObjId + ")");
				toReturn = generalUtil.listToCsv(matList);
			} catch (Exception e) {
				toReturn = "Parameter";
			}
		}
		
		return toReturn;
	}

	private String getMaterialNames(String matIds, Map<String, String> materialTypeTableMap) {
		String toReturn = ""; 
		try {
			List <String> matList = generalDao.getListOfStringBySql("select distinct invitemmaterialname from fg_s_invitemmaterial_v where invitemmaterial_id in (" + matIds + ")");
			toReturn = generalUtil.listToCsv(matList);
		} catch (Exception e) {
			toReturn = "Exp. Materials";
		}
		return toReturn;
	}

	private String getMaterialTypeNames(String matTypeIds, Map<String,String> materialTypeTableMap) {
		// TODO Auto-generated method stub
		String toReturn = ""; 
		try {
			String dlmtr = ",";
			String[] ruleConditionArry = matTypeIds.split(",",-1);
			
			for (String materialTypeId : ruleConditionArry) {
				toReturn += dlmtr + generalUtil.getNull(materialTypeTableMap.get(materialTypeId));
			}
			
			toReturn = toReturn.replaceFirst(dlmtr, "");
		} catch (Exception e) {
			toReturn = "Material Type";
		}
		
		return toReturn;
	}

	private String getValidOracleColumnName(String colname) { 
		String col_ = colname.replace("\"","");
		if(col_.length() > 30) {
			col_= col_.substring(0, 27) + "...";
		}
		return col_;
	}

	private Map<String, String> prepareColMap(String ruleName, String columnsSelection) {
		Map<String, String> colsMap = new HashMap<>();
		Map<String, String> colsSelectionMap = new HashMap<>();
		String colProp = "ALL:All,QUANTITY:Qty,MOLE:Mole,VOLUME:Volume,PURITY:Purity,EQUIVALENT:Equivalent,INVITEMBATCHNAME:Batch,VAL1:Actual Value 1,VAL2:Actual Value 2,MASS:Mass,MW:Mw";
		 
		List<String> colList = Arrays.asList(colProp.split(","));
		if (!colList.isEmpty()) {
			for (String c : colList) {
				String id = c.split(":")[0];
				String val_ = c.split(":")[1];
				colsMap.put(id,val_);
			}
		}
		
		if(!columnsSelection.isEmpty() && !columnsSelection.toLowerCase().startsWith("all")) {
			String[]columnsSelectionArray = columnsSelection.split(",",-1);
			for (String colSelect : columnsSelectionArray) {
				if(colsMap.containsKey(colSelect)) {
					colsSelectionMap.put(colSelect, colsMap.get(colSelect));
				}
			}
		} else {
			return colsMap; // return the map with all the options
		}
		
		
		return colsSelectionMap;
	}
}
