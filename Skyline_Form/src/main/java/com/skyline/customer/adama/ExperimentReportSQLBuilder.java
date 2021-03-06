package com.skyline.customer.adama;

import java.util.ArrayList;
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
	
	private String uomPlaceHolder = "$U";
	
	/**
	 * This function returns SQLObj object with all of the SQL parts (with,select,from and where) to have the following result:
	 * 
	 * The table shows (SQL - Cartesian product): 
	 * 					experiment (number, description ONE ROW MAX!!! for each experiment) x 
	 * 					combine rules data (usually it will be one row for each experiment but, it can be more) x 
	 * 					displayData (ONE ROW MAX!!! for each experiment  - using SMARTPIVOT) x
	 * 				    samples and result (could be more then one sample for an experiment the result data should be in one row - using SMARTPIVOT)
	 * 
	 * - The displayData SMARTPIVOT will be insert into the  FG_P_EXPREPORT_DATA_TMP table abd be defined by the stateKey
	 * - The data of the result will also be insert into FG_P_EXPREPORT_DATA_TMP by FG_ADAMA.GET_UPDATE_P_EXPREPORT_DATA DB function.
	 */
	SQLObj getExpReportRulesFieldsSQL(long stateKey, String expIds, String stepIds, String sampleIds, Map<String,String> materialTypeTableMap,
			String imputityMatIds, String resulttype, String characteristicMassBalan, String sampleComments, String sampleCreator,String sampleAmount) {
		
		StringBuilder sbWithSql = new StringBuilder();
		StringBuilder sbSelectSql = new StringBuilder();
		StringBuilder sbSelectHiddebSql = new StringBuilder();
		StringBuilder sbWhereSql = new StringBuilder();
		StringBuilder sbFromSql = new StringBuilder();
		StringBuilder sbPivotSql = new StringBuilder();
		
		String stepNumberNamesCsv = "";

		int index = 0;
		
		//prepare - (delete old data on the same stateKey) ...
		generalDao.updateSingleString("delete from FG_P_EXPREPORT_DATA_TMP where statekey ='" + stateKey + "'");
		generalDao.updateSingleString("delete from FG_P_EXPREPORT_SAMPLE_TMP where statekey ='" + stateKey + "'");
		generalDao.updateSingleString("delete from FG_EXPREPORT_MATERIALREF_TMP where statekey ='" + stateKey + "'");
		
		String sqlMaterialRefInsert = "insert into FG_EXPREPORT_MATERIALREF_TMP (MATERIALREF_ID,FORM_TEMP_ID,MATERIALREF_OBJIDVAL,FORMID,TIMESTAMP,CREATION_DATE,CLONEID,TEMPLATEFLAG,CHANGE_BY,CREATED_BY,SESSIONID,ACTIVE,FORMCODE_ENTITY,FORMCODE,YIELDUOM_ID,MASS,MOLE,LIMITINGAGENT,QUANTITYUOM_ID,COMMENTS,ACTUALPURITY,CONCINREACTIONMASS,VOLUMERATE,MASSRATEPPUOM_ID,YIELD,VOLUME,MATERIALREFNAME,BATCH_ID,CATALYST,CHEMDOODLE,MATERIALNAMEINF,DENSITYUOM_ID_INF,REACTANTMATERIAL_ID,AUTH,INVITEMMATERIAL_ID,EQUIVALENT,QUANTRATIOTOTAL,VOLUOM_ID,FORMULAINF,PARENTID,BATCHINF,MWINF,WATERCONTENT,RESULTID_HOLDER,QUANTITYRATE,MOLERATE,ALIAS_,CONCENTRATIONMOLE,PURITYUOM_ID_INF,TOTALVOLUME,SMILESINF,CASNAMBERINF,RATIO,PRODUCTDENSITY,QUANTITYRATE_UOM,RATIOTYPE_ID,IUPACNAMEINF,ACTPURITYUOM_ID,SAMPLE_ID,MASSRATEFORPP,MWUOM_ID_INF,DENSITYINF,WATERCONTUOM_ID,MOLEUOM_ID,MOLERATEUOM_ID,VOLRATEUOM_ID,QUANTITY,PURITYINF,TABLETYPE,MASSUOM_ID,ISLIMITED,TOTALQUANTITY,VOLRATIOTOTAL,CASNAMEINF,SYNONYMSINF,ORIGINFORMID,WATERCONUOMNAME,MWUOMNAME,DENSITYUOMNAME,ACTPURITYUOMNAME,MOLEUOMNAME,PURITYUOMNAME,VOLUOMNAME,QUANTITYUOMNAME,MASSUOMNAME,YIELDUOMNAME,INVITEMMATERIALNAME,STRUCTURE,CASNUMBER,CASNAME,SYNONYMS,DENSITY,IUPACNAME,MW,MW_UOM_ID,DENSITY_UOM_ID,CHEMICALFORMULA,SMILES,INVITEMBATCHNAME,PURITY,PURITYUOM_ID,ISSTANDART,EXPERIMENT_ID,STEP_ID,EXPERIMENTSTATUSNAME,STEPSTATUSNAME,ISPLANNEDSNAPSHOUT,INCHI,MOL,PROJECT_ID,MATERIAL_OBJIDVAL,STEP_OBJIDVAL,STEPNUMBER,STEPNAME,PROTOCOLTYPENAME,PREPARATION_RUN,RUNNUMBERDISPLAY,STEPFORMNUMBERID,RUNNUMBER,EXPFORMCODE,STATEKEY)\r\n" + 
				"SELECT \"MATERIALREF_ID\",\"FORM_TEMP_ID\",\"MATERIALREF_OBJIDVAL\",\"FORMID\",\"TIMESTAMP\",\"CREATION_DATE\",\"CLONEID\",\"TEMPLATEFLAG\",\"CHANGE_BY\",\"CREATED_BY\",\"SESSIONID\",\"ACTIVE\",\"FORMCODE_ENTITY\",\"FORMCODE\",\"YIELDUOM_ID\",\"MASS\",\"MOLE\",\"LIMITINGAGENT\",\"QUANTITYUOM_ID\",\"COMMENTS\",\"ACTUALPURITY\",\"CONCINREACTIONMASS\",\"VOLUMERATE\",\"MASSRATEPPUOM_ID\",\"YIELD\",\"VOLUME\",\"MATERIALREFNAME\",\"BATCH_ID\",\"CATALYST\",\"CHEMDOODLE\",\"MATERIALNAMEINF\",\"DENSITYUOM_ID_INF\",\"REACTANTMATERIAL_ID\",\"AUTH\",\"INVITEMMATERIAL_ID\",\"EQUIVALENT\",\"QUANTRATIOTOTAL\",\"VOLUOM_ID\",\"FORMULAINF\",\"PARENTID\",\"BATCHINF\",\"MWINF\",\"WATERCONTENT\",\"RESULTID_HOLDER\",\"QUANTITYRATE\",\"MOLERATE\",\"ALIAS_\",\"CONCENTRATIONMOLE\",\"PURITYUOM_ID_INF\",\"TOTALVOLUME\",\"SMILESINF\",\"CASNAMBERINF\",\"RATIO\",\"PRODUCTDENSITY\",\"QUANTITYRATE_UOM\",\"RATIOTYPE_ID\",\"IUPACNAMEINF\",\"ACTPURITYUOM_ID\",\"SAMPLE_ID\",\"MASSRATEFORPP\",\"MWUOM_ID_INF\",\"DENSITYINF\",\"WATERCONTUOM_ID\",\"MOLEUOM_ID\",\"MOLERATEUOM_ID\",\"VOLRATEUOM_ID\",\"QUANTITY\",\"PURITYINF\",\"TABLETYPE\",\"MASSUOM_ID\",\"ISLIMITED\",\"TOTALQUANTITY\",\"VOLRATIOTOTAL\",\"CASNAMEINF\",\"SYNONYMSINF\",\"ORIGINFORMID\",\"WATERCONUOMNAME\",\"MWUOMNAME\",\"DENSITYUOMNAME\",\"ACTPURITYUOMNAME\",\"MOLEUOMNAME\",\"PURITYUOMNAME\",\"VOLUOMNAME\",\"QUANTITYUOMNAME\",\"MASSUOMNAME\",\"YIELDUOMNAME\",\"INVITEMMATERIALNAME\",\"STRUCTURE\",\"CASNUMBER\",\"CASNAME\",\"SYNONYMS\",\"DENSITY\",\"IUPACNAME\",\"MW\",\"MW_UOM_ID\",\"DENSITY_UOM_ID\",\"CHEMICALFORMULA\",\"SMILES\",\"INVITEMBATCHNAME\",\"PURITY\",\"PURITYUOM_ID\",\"ISSTANDART\",\"EXPERIMENT_ID\",\"STEP_ID\",\"EXPERIMENTSTATUSNAME\",\"STEPSTATUSNAME\",\"ISPLANNEDSNAPSHOUT\",\"INCHI\",\"MOL\",\"PROJECT_ID\",\"MATERIAL_OBJIDVAL\",\"STEP_OBJIDVAL\",\"STEPNUMBER\",\"STEPNAME\",\"PROTOCOLTYPENAME\",\"PREPARATION_RUN\",\"RUNNUMBERDISPLAY\",\"STEPFORMNUMBERID\",\"RUNNUMBER\",\"EXPFORMCODE\", \r\n" + 
				"'" + stateKey + "' AS STATAKEY\r\n" + 
				"from fg_s_materialref_all_v \r\n" + 
				"WHERE STEP_ID in (" + (stepIds.isEmpty()?"-1":stepIds) + ")";
		generalDao.updateSingleString(sqlMaterialRefInsert);
		
		String sqlSampleTmp =
				"INSERT INTO FG_P_EXPREPORT_SAMPLE_TMP(STATEKEY,SAMPLE_ID,SAMPLENAME,SAMPLEDESC,COMMENTSFORCOA,EXPERIMENT_ID,CREATOR_ID,AMMOUNT)\r\n" + 
				"SELECT '" + stateKey + "' AS STATEKEY, T.SAMPLE_ID, T.SAMPLENAME, T.SAMPLEDESC, T.COMMENTSFORCOA, T.EXPERIMENT_ID, T.CREATOR_ID, T.AMMOUNT\r\n" + 
				"FROM FG_S_SAMPLE_ALL_V T \r\n" + 
				"WHERE T.sample_id(+) in (" + (sampleIds.isEmpty()?"-1":sampleIds) + ")\r\n" + 
				"union all\r\n" + 
				"SELECT '" + stateKey + "' AS STATEKEY, -1 as SAMPLE_ID, null SAMPLENAME, null SAMPLEDESC, null COMMENTSFORCOA, null EXPERIMENT_ID, null CREATOR_ID, null AMMOUNT\r\n" + 
				"FROM dual";
		generalDao.updateSingleString(sqlSampleTmp);

		
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
		boolean isCombineRulseExist = false;
		
		// MAIN SQL - REPORT combineRules AND displayData table selection - for each filter row (filter row represent the "combine rules" and "display data" selection table)
		for (Map<String, Object> filterRefMap : filterRefList) { 
			
			String tableType = generalUtil.getNull((String)filterRefMap.get("TABLETYPE")); // <combineRules / displayData>
			String columnsSelection = generalUtil.getNull((String)filterRefMap.get("COLUMNSSELECTION"));
			String columnName = generalUtil.getNull((String)filterRefMap.get("COLUMNNAME"));

			//-----------------------------------------------------------------------------
			//----- combineRules: SQL - SHOULD DUPLICATE THE EXPERIMENT ROW IN CASE MORE THEN ONE VALUE FIT (=> implement as with)
			//-----------------------------------------------------------------------------
			if(tableType.equalsIgnoreCase("combineRules")) {
				
				isCombineRulseExist = true;
				String stepName = generalUtil.getNull((String)filterRefMap.get("STEPNAME")); // List of step Names (for example STEP 01,STEP 02)
				String ruleName = generalUtil.getNull((String)filterRefMap.get("RULENAME")); // <Main Solvent /Limiting Agent/Material Type>
				String ruleCondition = generalUtil.getNull((String)filterRefMap.get("RULECONDITION")); // <Main Solvent /Limiting Agent/Material Type>
				String defaultColName = ruleCondition.isEmpty()?ruleName:ruleCondition;
				
				//check stepNames exists
				if(stepIds.isEmpty()) {
					continue;
				}
				
				//prepare colnames_....
				Map<String,String> colMap = prepareColMap(ruleName, columnsSelection);
				if(colMap == null || colMap.isEmpty()) {
					continue;
				}
				
				//removed steps from stepName  when step was selected in the rules tables but no longer exists in the list of checked steps (in the table above)
				List<String> stepNamesList_select = new ArrayList<>(Arrays.asList(stepName.split(",")));// List of selected step Names (for example STEP 01,STEP 02)
				List<String> stepNamesList_ =  new ArrayList<>(Arrays.asList(stepName.split(",")));
				List<String> stepNameList_all = generalDao.getListOfStringBySql("select distinct  'Step ' || FORMNUMBERID from fg_s_step_v where step_id in (" + stepIds + ") order by 'Step ' ||FORMNUMBERID");//// List of checked steps (steps table)
				for(String s : stepNamesList_select) {
					if(!stepNameList_all.contains(s)) {
						stepNamesList_.remove(s);
					}
				}
				stepName = generalUtil.listToCsv(stepNamesList_);
				
				//get steps names from stepIds if contains all .. or no selection
				if(stepName.isEmpty() || stepName.toLowerCase().contains("all")) {
					if(stepNumberNamesCsv.isEmpty()) {
						stepNumberNamesCsv = getStepNumberNamesCsv(stepIds);
					}
					stepName = stepNumberNamesCsv;
				}

				
				// ******************************************
				// **** Limiting Agent 
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
								(colMap.containsKey("QUANTITY") ? "   ,max(fg_get_num_display(t.QUANTITY,3,3) || '[' || t.QUANTITYUOMNAME || ']') keep (dense_rank first order by t.MATERIALREF_ID desc nulls last) over (partition by t.STEP_ID) as QUANTITY\r\n":"") +
								(colMap.containsKey("MOLE") ? "   ,max(fg_get_num_display(t.MOLE,3,3) || '[' || t.MOLEUOMNAME || ']') keep (dense_rank first order by t.MATERIALREF_ID desc nulls last) over (partition by t.STEP_ID) as MOLE\r\n":"") +
								(colMap.containsKey("VOLUME") ? "   ,max(fg_get_num_display(t.VOLUME,3,3)|| '[' || t.VOLUOMNAME || ']') keep (dense_rank first order by t.MATERIALREF_ID desc nulls last) over (partition by t.STEP_ID) as VOLUME\r\n":"") +
								(colMap.containsKey("PURITY") ? "   ,max(fg_get_num_display(nvl(nvl(t.PURITYINF,t.PURITY),100),3,3)) keep (dense_rank first order by t.MATERIALREF_ID desc nulls last) over (partition by t.STEP_ID) as PURITY\r\n":"") +
								(colMap.containsKey("EQUIVALENT") ? "   ,max(fg_get_num_display(t.EQUIVALENT,3,3)) keep (dense_rank first order by t.MATERIALREF_ID desc nulls last) over (partition by t.STEP_ID) as EQUIVALENT\r\n":"") +
								(colMap.containsKey("INVITEMBATCHNAME") ? "   ,max(t.INVITEMBATCHNAME) keep (dense_rank first order by t.MATERIALREF_ID desc nulls last) over (partition by t.STEP_ID) as INVITEMBATCHNAME\r\n":"") +
								
								"  FROM FG_EXPREPORT_MATERIALREF_TMP t \r\n" + 
								"  WHERE t.sessionid is null and t.active=1 \r\n" + 
								"  AND t.STEP_ID in (" + stepIds + ")\r\n" + 
								"  AND lower('Step '|| t.STEPFORMNUMBERID) = lower('"  + singleStepName + "')\r\n" + 
								"  AND t.TABLETYPE = 'Reactant'\r\n" + //
								"  AND t.LIMITINGAGENT = 1\r\n" + 
								")"); // and experiment id where part (or on temp table we create in the beginning for performance)
						
						//select
						sbSelectSql.append(
								"," + aliasName + ".MAINNAME as \"" + getValidOracleColumnName("{" + index + "}" + singleStepName + " - " + colName_) + "\"" +
										(colMap.containsKey("QUANTITY") ? "," + aliasName +  ".QUANTITY as \"{" + index + "}" + singleStepName + " - " + colMap.get("QUANTITY") + uomPlaceHolder + "\"":"") +
										(colMap.containsKey("MOLE")   ? "," + aliasName +  ".MOLE as \"{" + index + "}" + singleStepName + " - " + colMap.get("MOLE") + uomPlaceHolder + "\"":"") +
										(colMap.containsKey("VOLUME")   ? "," + aliasName +  ".VOLUME as \"{" + index + "}" + singleStepName + " - " + colMap.get("VOLUME") + uomPlaceHolder + "\"":"") +
										(colMap.containsKey("PURITY")   ? "," + aliasName +  ".PURITY as \"{" + index + "}" + singleStepName + " - " + colMap.get("PURITY") + " [%]" + "\"":"") +
										(colMap.containsKey("EQUIVALENT")   ? "," + aliasName +  ".EQUIVALENT as \"{" + index + "}" + singleStepName + " - " + colMap.get("EQUIVALENT") + "\"":"") +
										(colMap.containsKey("INVITEMBATCHNAME")   ? "," + aliasName +  ".INVITEMBATCHNAME as \"{" + index + "}" + singleStepName + " - " + colMap.get("INVITEMBATCHNAME") + "\"":"")
								);
						
						//from
						sbFromSql.append("," + aliasName);
						
						//where
						sbWhereSql.append(" AND t.EXPERIMENT_ID = " + aliasName + ".EXPID(+)");
						
						index++;
					}
					
				}
				// **** Limiting Agent END!
				
				// ****************************************
				// **** Main Solvent 
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
								(colMap.containsKey("QUANTITY") ? "   ,max(fg_get_num_display(t.QUANTITY,3,3) || '[' || t.QUANTITYUOMNAME || ']') keep (dense_rank first order by to_number(fg_get_num_normal(t.QUANTITY,t.QUANTITYUOM_ID)) desc nulls last) over (partition by t.STEP_ID) QUANTITY\r\n":"") +
								(colMap.containsKey("MOLE") ? "   ,max(fg_get_num_display(t.MOLE,3,3) || '[' || t.MOLEUOMNAME || ']') keep (dense_rank first order by to_number(fg_get_num_normal(t.QUANTITY,t.QUANTITYUOM_ID)) desc nulls last) over (partition by t.STEP_ID) as MOLE\r\n":"") +
								(colMap.containsKey("VOLUME") ? "   ,max(fg_get_num_display(t.VOLUME,3,3) || '[' || t.VOLUOMNAME || ']') keep (dense_rank first order by to_number(fg_get_num_normal(t.QUANTITY,t.QUANTITYUOM_ID)) desc nulls last) over (partition by t.STEP_ID) as VOLUME\r\n":"") +
								(colMap.containsKey("PURITY") ? "   ,max(fg_get_num_display(nvl(nvl(t.PURITYINF,t.PURITY),100),3,3)) keep (dense_rank first order by to_number(fg_get_num_normal(t.QUANTITY,t.QUANTITYUOM_ID)) desc nulls last) over (partition by t.STEP_ID) as PURITY\r\n":"") +
								(colMap.containsKey("EQUIVALENT") ? "   ,max(fg_get_num_display(t.EQUIVALENT,3,3)) keep (dense_rank first order by to_number(fg_get_num_normal(t.QUANTITY,t.QUANTITYUOM_ID)) desc nulls last) over (partition by t.STEP_ID) as EQUIVALENT\r\n":"") +
								//(colMap.containsKey("INVITEMBATCHNAME") ? "   ,max(t.INVITEMBATCHNAME) keep (dense_rank first order by to_number(fg_get_num_normal(t.QUANTITY,t.QUANTITYUOM_ID)) desc nulls last) over (partition by t.STEP_ID) as INVITEMBATCHNAME\r\n":"") +
								
								"  FROM FG_EXPREPORT_MATERIALREF_TMP t \r\n" + 
								"  WHERE t.sessionid is null and t.active=1\r\n" + 
								"  AND t.STEP_ID in (" + stepIds + ")\r\n" + 
								"  AND lower('Step '|| t.STEPFORMNUMBERID) = lower('"  + singleStepName + "')\r\n" + 
								"  AND t.TABLETYPE = 'Solvent'\r\n" + 
								")"); // and experiment id where part (or on temp table we create in the beginning for performance)
						
						//select
						sbSelectSql.append(
								"," + aliasName + ".MAINNAME as \"" + getValidOracleColumnName("{" + index + "}" + singleStepName + " - " + colName_) + "\"" +
										(colMap.containsKey("QUANTITY") ? "," + aliasName +  ".QUANTITY as \"{" + index + "}" + singleStepName + " - " + colMap.get("QUANTITY") + uomPlaceHolder + "\"":"") +
										(colMap.containsKey("MOLE")   ? "," + aliasName +  ".MOLE as \"{" + index + "}" + singleStepName + " - " + colMap.get("MOLE") + uomPlaceHolder + "\"":"") +
										(colMap.containsKey("VOLUME")   ? "," + aliasName +  ".VOLUME as \"{" + index + "}" + singleStepName + " - " + colMap.get("VOLUME") + uomPlaceHolder + "\"":"") +
										(colMap.containsKey("PURITY")   ? "," + aliasName +  ".PURITY as \"{" + index + "}" + singleStepName + " - " + colMap.get("PURITY") + " [%]" + "\"":"") +
										(colMap.containsKey("EQUIVALENT")   ? "," + aliasName +  ".EQUIVALENT as \"{" + index + "}" + singleStepName + " - " + colMap.get("EQUIVALENT") + "\"":"")
										//(colMap.containsKey("INVITEMBATCHNAME")   ? "," + aliasName +  ".INVITEMBATCHNAME as \"{" + index + "}" + colMap.get("INVITEMBATCHNAME") + "\"":"")
								);
						
						//from
						sbFromSql.append("," + aliasName);
						
						//where
						sbWhereSql.append(" AND t.EXPERIMENT_ID = " + aliasName + ".EXPID(+)");
						
						index++;
					}
				}
				// **** Main Solvent END!
				
				// *****************************************
				// **** Material Type 
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
								(colMap.containsKey("QUANTITY") ? "   ,fg_get_num_display(t.QUANTITY,3,3) || '[' || t.QUANTITYUOMNAME || ']' as QUANTITY\r\n":"") +
								(colMap.containsKey("MOLE") ? "   ,fg_get_num_display(t.MOLE,3,3) || '[' || t.MOLEUOMNAME || ']' as MOLE\r\n":"") +
								(colMap.containsKey("VOLUME") ? "   ,fg_get_num_display(t.VOLUME,3,3) || '[' || t.VOLUOMNAME || ']' as VOLUME\r\n":"") +
								(colMap.containsKey("PURITY") ? "   ,fg_get_num_display(nvl(nvl(t.PURITYINF,t.PURITY),100),3,3) as PURITY\r\n":"") +
								(colMap.containsKey("EQUIVALENT") ? "   ,fg_get_num_display(t.EQUIVALENT,3,3) as EQUIVALENT\r\n":"") +
								(colMap.containsKey("INVITEMBATCHNAME") ? "   ,t.INVITEMBATCHNAME as INVITEMBATCHNAME\r\n":"") +
								
								"  FROM FG_EXPREPORT_MATERIALREF_TMP t, FG_I_CONN_MATERIAL_TYPE_V mt \r\n" + 
								"  WHERE t.sessionid is null and t.active=1 \r\n" + 
								"  AND t.STEP_ID in (" + stepIds + ")\r\n" + 
								"  AND lower('Step '|| t.STEPFORMNUMBERID) = lower('"  + singleStepName + "')\r\n" + 
								"  AND t.INVITEMMATERIAL_ID = mt.INVITEMMATERIAL_ID\r\n" + 
								"  AND instr(',' || '" + ruleCondition + "' || ',', ','||mt.MATERIALTYPE_ID||',') > 0\r\n" +
								")"); // and experiment id where part (or on temp table we create in the beginning for performance)
						
						//select
						sbSelectSql.append(
								"," + aliasName + ".MAINNAME as \"" + getValidOracleColumnName("{" + index + "}" + singleStepName + " - " + colName_) + "\"" +
										(colMap.containsKey("QUANTITY") ? "," + aliasName +  ".QUANTITY as \"{" + index + "}" + singleStepName + " - " + colMap.get("QUANTITY") + uomPlaceHolder + "\"":"") +
										(colMap.containsKey("MOLE")   ? "," + aliasName +  ".MOLE as \"{" + index + "}" + singleStepName + " - " + colMap.get("MOLE") + uomPlaceHolder + "\"":"") +
										(colMap.containsKey("VOLUME")   ? "," + aliasName +  ".VOLUME as \"{" + index + "}" + singleStepName + " - " + colMap.get("VOLUME") + uomPlaceHolder + "\"":"") +
										(colMap.containsKey("PURITY")   ? "," + aliasName +  ".PURITY as \"{" + index + "}" + singleStepName + " - " + colMap.get("PURITY") + " [%]" + "\"":"") +
										(colMap.containsKey("EQUIVALENT")   ? "," + aliasName +  ".EQUIVALENT as \"{" + index + "}" + singleStepName + " - " + colMap.get("EQUIVALENT") + "\"":"") +
										(colMap.containsKey("INVITEMBATCHNAME")   ? "," + aliasName +  ".INVITEMBATCHNAME as \"{" + index + "}" + singleStepName + " - " + colMap.get("INVITEMBATCHNAME") + "\"":"")
								);
						
						//from
						sbFromSql.append("," + aliasName);
						
						//where
						sbWhereSql.append(" AND t.EXPERIMENT_ID = " + aliasName + ".EXPID(+)");
						
						index++;
					}
					
				}
				// **** Material Type END!
				
				// ******************************************
				// **** Experiment Materials 
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
								(colMap.containsKey("QUANTITY") ? "   ,fg_get_num_display(t.QUANTITY,3,3) || '[' || t.QUANTITYUOMNAME || ']' as QUANTITY\r\n":"") +
								(colMap.containsKey("MOLE") ? "   ,fg_get_num_display(t.MOLE,3,3) || '[' || t.MOLEUOMNAME || ']' as MOLE\r\n":"") +
								(colMap.containsKey("VOLUME") ? "   ,fg_get_num_display(t.VOLUME,3,3) || '[' || t.VOLUOMNAME || ']' as VOLUME\r\n":"") +
								(colMap.containsKey("PURITY") ? "   ,fg_get_num_display(nvl(nvl(t.PURITYINF,t.PURITY),100),3,3) as PURITY\r\n":"") +
								(colMap.containsKey("EQUIVALENT") ? "   ,fg_get_num_display(t.EQUIVALENT,3,3) as EQUIVALENT\r\n":"") +
								(colMap.containsKey("INVITEMBATCHNAME") ? "   ,t.INVITEMBATCHNAME as INVITEMBATCHNAME\r\n":"") +
								
								"  FROM FG_EXPREPORT_MATERIALREF_TMP t\r\n" + 
								"  WHERE t.sessionid is null and t.active=1\r\n" + 
								"  AND t.STEP_ID in (" + stepIds + ")\r\n" + 
								"  AND lower('Step '|| t.STEPFORMNUMBERID) = lower('"  + singleStepName + "')\r\n" +
								"  AND instr(',' || '" + ruleCondition + "' || ',', ','||t.INVITEMMATERIAL_ID||',') > 0\r\n" +
								")"); // and experiment id where part (or on temp table we create in the beginning for performance)
						
						//select
						sbSelectSql.append(
								"," + aliasName + ".MAINNAME as \"" + getValidOracleColumnName("{" + index + "}" + singleStepName + " - " + colName_) + "\"" +
										(colMap.containsKey("QUANTITY") ? "," + aliasName +  ".QUANTITY as \"{" + index + "}" + singleStepName + " - " + colMap.get("QUANTITY") + uomPlaceHolder + "\"":"") +
										(colMap.containsKey("MOLE")   ? "," + aliasName +  ".MOLE as \"{" + index + "}" + singleStepName + " - " + colMap.get("MOLE") + uomPlaceHolder + "\"":"") +
										(colMap.containsKey("VOLUME")   ? "," + aliasName +  ".VOLUME as \"{" + index + "}" + singleStepName + " - " + colMap.get("VOLUME") + uomPlaceHolder + "\"":"") +
										(colMap.containsKey("PURITY")   ? "," + aliasName +  ".PURITY as \"{" + index + "}" + singleStepName + " - " + colMap.get("PURITY") + "[%]" + "\"":"") +
										(colMap.containsKey("EQUIVALENT")   ? "," + aliasName +  ".EQUIVALENT as \"{" + index + "}" + singleStepName + " - " + colMap.get("EQUIVALENT") + "\"":"") +
										(colMap.containsKey("INVITEMBATCHNAME")   ? "," + aliasName +  ".INVITEMBATCHNAME as \"{" + index + "}" + singleStepName + " - " + colMap.get("INVITEMBATCHNAME") + "\"":"")
								);
						
						//from
						sbFromSql.append("," + aliasName);
						
						//where
						sbWhereSql.append(" AND t.EXPERIMENT_ID = " + aliasName + ".EXPID(+)");
						
						index++;
					}
					
				}
				// **** Experiment Materials 
			}
			//----- combineRules END!
			
			//-----------------------------------------------------------------------------
			//----- displayData - SHOULD DUPLICATE THE COLUMNS IN CASE OF DUPLICATION (=> implement using the SMARTPIVOT mechanism)
			//-----------------------------------------------------------------------------
			if(tableType.equalsIgnoreCase("displayData")) {
				
				String displayType = generalUtil.getNull((String)filterRefMap.get("TYPE_")); // <Material/Parameter>
				String displayObjId = generalUtil.getNull((String)filterRefMap.get("REPORTFILTERREFNAME")); //Material id or Parameter id
				String displayLevel = generalUtil.getNull((String)filterRefMap.get("LEVEL_")); // Material => List of step Names (for example STEP 01,STEP 02) / Parameter => List of step Names and Experiment numbers
				
				//check stepNames exists
				if (/* displayLevel.isEmpty() || */displayObjId.isEmpty()) { //  || stepIds.isEmpty() -> not use this condition because of parameters in experiment
					continue;
				}
				
				//prepare colnames_....
				Map<String,String> colMap = prepareColMap(displayType, columnsSelection);
				if(colMap == null || colMap.isEmpty()) {
					continue;
				}
				
				//get steps names from stepIds if contains all .. or no selection 
				if(!stepIds.isEmpty() && (displayLevel.toLowerCase().contains("all") || displayLevel.isEmpty())) {
					if(stepNumberNamesCsv.isEmpty()) {
						stepNumberNamesCsv = getStepNumberNamesCsv(stepIds);
					}
					displayLevel = stepNumberNamesCsv;
				}

				
				// #####################################
				// Material (pivot data) using UNIQUEROW as key that is experiment_id (in case no sample) or UNIQUEROW as in FG_R_EXPREPORT_PIVOT_DT_V 
				// #####################################
				if(displayType.equalsIgnoreCase("Material")) {
					String colName_ = (columnName == null || columnName.isEmpty())? getDisplayDataName(displayObjId, displayType): columnName;
					String[] displayLevelArray = displayLevel.split(",", -1);
					
					// for each step in the user selection row
					for (String singleDisplayName : displayLevelArray) {

						String col_ =  //"\"" + ("{" + displayObjId + "-" + singleDisplayName + "}" + singleDisplayName + " - " + colName_) + "\"" +
								(colMap.containsKey("QUANTITY") ? "," + "\"" + ("{" + displayObjId + "}" + singleDisplayName + " - " + colName_ + " " + colMap.get("QUANTITY") + uomPlaceHolder) + "\"": "") +
								(colMap.containsKey("MOLE") ? "," + "\"" + ("{" + displayObjId + "}" + singleDisplayName + " - " + colName_ + " " + colMap.get("MOLE") + uomPlaceHolder) + "\"": "") +
								(colMap.containsKey("VOLUME") ? "," + "\"" + ("{" + displayObjId + "}" + singleDisplayName + " - " + colName_ + " " + colMap.get("VOLUME") + uomPlaceHolder) + "\"": "") +
								(colMap.containsKey("PURITY") ? "," + "\"" + ("{" + displayObjId + "}" + singleDisplayName + " - " + colName_ + " " + colMap.get("PURITY") + " [%]") + "\"": "") +
								(colMap.containsKey("EQUIVALENT") ? "," + "\"" + ("{" + displayObjId + "}" + singleDisplayName + " - " + colName_ + " " + colMap.get("EQUIVALENT") ) + "\"": "") +
								(colMap.containsKey("INVITEMBATCHNAME") ? "," + "\"" + ("{" + displayObjId + "}" + singleDisplayName + " - " + colName_ + " " + colMap.get("INVITEMBATCHNAME")) + "\"": "") +
								(colMap.containsKey("MASS") ? "," + "\"" + ("{" + displayObjId + "}" + singleDisplayName + " - " + colName_ + " " +  colMap.get("MASS")+ uomPlaceHolder) + "\"": "") +
								(colMap.containsKey("MW") ? "," + "\"" + ("{" + displayObjId + "}" + singleDisplayName + " - " + colName_ + " " + colMap.get("MW")+ uomPlaceHolder) + "\"": "");
						
						String val_ = //"\" ' || (invitemmaterialname) || '\"" +
								(colMap.containsKey("QUANTITY") ? ",\" ' || fg_get_num_display(t.QUANTITY,3,3) || '[' || t.QUANTITYUOMNAME || ']' || '\"" : "") +
								(colMap.containsKey("MOLE") ? ",\" ' || fg_get_num_display(t.MOLE,3,3) || '[' || t.MOLEUOMNAME || ']' || '\"" : "") +
								(colMap.containsKey("VOLUME") ? ",\" ' || fg_get_num_display(t.VOLUME,3,3) || '[' || t.VOLUOMNAME || ']' || '\"" : "") +
								(colMap.containsKey("PURITY") ? ",\" ' || fg_get_num_display(nvl(nvl(t.PURITYINF,t.PURITY),100),3,3) || '\"" : "") +
								(colMap.containsKey("EQUIVALENT") ? ",\" ' || fg_get_num_display(t.EQUIVALENT,3,3) || '\"" : "") +
								(colMap.containsKey("INVITEMBATCHNAME") ? ",\" ' || t.INVITEMBATCHNAME || '\"" : "") +
								(colMap.containsKey("MASS") ? ",\" ' || fg_get_num_display(t.MASS,3,3)|| '[' || t.MASSUOMNAME || ']' || '\"" : "") +
								(colMap.containsKey("MW") ? ",\" ' || fg_get_num_display(t.MW,3,3) || '[' || t.MWUOMNAME || ']' || '\"" : "");
						
						if(col_ != null && col_.startsWith(",")) {
							col_ = col_.substring(1);
						}
						
						if(val_ != null && val_.startsWith(",")) {
							val_ = val_.substring(1);
						}
						
						
						String pivotFormat = "'{pivotkey:\"'|| decode(s.SAMPLE_ID, null,'',s.SAMPLE_ID || '_') || t.experiment_id ||'\",pivotkeyname:\"UNIQUEROW\",column:[" + col_ + "],val:[" + val_ + "]}'";
						
						if(sbPivotSql.length() > 0) {
							sbPivotSql.append("\n union all \n");
						}
						
						sbPivotSql.append(" Select distinct " + stateKey + " as stateKey ," + index + " as order_, null as order2," + pivotFormat + " as result_SMARTPIVOT\n" +
								"  FROM FG_EXPREPORT_MATERIALREF_TMP t, FG_P_EXPREPORT_SAMPLE_TMP s\r\n" + 
								"  WHERE t.experiment_id = s.experiment_id(+) and t.sessionid is null and t.active=1\r\n" + 
								"  AND t.STEP_ID in (" + (stepIds.isEmpty()?"-1":stepIds) + ")\r\n" + 
								//"  AND t.TABLETYPE = 'Reactant'\r\n" +  
								"  AND s.sample_id(+) in (" + (sampleIds.isEmpty()?"-1":sampleIds) + ") \r\n" +
								"  AND instr(',' || '" + displayObjId + "' || ',', ','||t.INVITEMMATERIAL_ID||',') > 0\r\n" +
								"  AND lower('Step '|| t.STEPFORMNUMBERID) = lower('"  + singleDisplayName + "')");
						
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
						
						String col_ = //"\"" + ("{" + displayObjId + "}" + singleDisplayName + " - " + singleDisplayName + " - " + colName_) + "\"" +
								(colMap.containsKey("VAL1") ? "," + "\"" + ("{" + displayObjId + "}" + singleDisplayName + " - " + colName_ + " " + colMap.get("VAL1") + uomPlaceHolder) + "\"": "") +
								(colMap.containsKey("VAL2") ? "," + "\"" + ("{" + displayObjId  + "-" + singleDisplayName + "}" + colName_ + " " + colMap.get("VAL2") + uomPlaceHolder) + "\"": "");
						
						
						String val_ = //"\" ' || (t.parametername) || '\"" +
								(colMap.containsKey("VAL1") ? ",\" ' || t.PARAMETERSCRITERIANAME || t.VAL1 || '[' || t.UOMNAME || ']' || '\"" : "") +
								(colMap.containsKey("VAL2") ? ",\" ' || t.PARAMETERSCRITERIANAME || t.VAL2 || '[' || t.UOMNAME || ']' || '\"" : "");
//						(colMap.containsKey("VAL1") ? ",\" ' || t.PLANNEDPARAMETERSCRITERIANAME || t.PLANNEDVAL1 || '\"" : "") +
//						(colMap.containsKey("VAL2") ? ",\" ' || t.PLANNEDPARAMETERSCRITERIANAME || t.PLANNEDVAL2 || '\"" : "");
						
						if(col_ != null && col_.startsWith(",")) {
							col_ = col_.substring(1);
						}
						
						if(val_ != null && val_.startsWith(",")) {
							val_ = val_.substring(1);
						}
						
						String pivotFormat = "'{pivotkey:\"'|| decode(s.SAMPLE_ID, null,'',s.SAMPLE_ID || '_') || t.experiment_id ||'\",pivotkeyname:\"UNIQUEROW\",column:[" + col_ + "],val:[" + val_ + "]}'";
						
						if(sbPivotSql.length() > 0) {
							sbPivotSql.append("\n union all \n");
						}
						
						sbPivotSql.append(" Select distinct " + stateKey + " as stateKey ," + index + " as order_, null as order2," + pivotFormat + " as result_SMARTPIVOT\n" +
								"  from FG_S_PARAMREF_ALL_V t, FG_P_EXPREPORT_SAMPLE_TMP s\r\n" + 
								"WHERE t.experiment_id = s.experiment_id(+) and t.SESSIONID is null and t.ACTIVE = 1 AND T.VAL1 is not null\r\n" + 
//								"WHERE t.experiment_id = r.experiment_id(+) and  t.SESSIONID is null and t.ACTIVE = 1 AND T.PLANNEDVAL1 is not null\r\n" + 
								"AND instr(',' || '" + displayObjId + "' || ',', ','||t.PARAMETER_ID||',') > 0\r\n" +
								"AND s.sample_id(+) in (" + (sampleIds.isEmpty()?"-1":sampleIds) + ") \r\n" +
								(singleDisplayName.equalsIgnoreCase("experiments")?"and T.experiment_id IN (" + expIds + ")":"and T.step_id IN (" + stepIds + ") AND 'Step '|| t.STEPNUMBER ='" + singleDisplayName + "'"));
						
						index++;
					}
				}
			}
			//----- displayData END!
		}
		// MAIN SQL - REPORT combineRules AND displayData table selection END!

				
		//+++++++++++++++++++++++++++++++++++++++++++++
		// displayData into FG_P_EXPREPORT_DATA_TMP
		//+++++++++++++++++++++++++++++++++++++++++++++
		String numRowsDisplayData = "";
		if(sbPivotSql.length() > 0) {
			String inserSql = "insert into FG_P_EXPREPORT_DATA_TMP (statekey, order_, order2, result_SMARTPIVOT) " + sbPivotSql.toString();
			numRowsDisplayData = generalDao.updateSingleString(inserSql);
		}
		
		//+++++++++++++++++++++++++++++++++++++++++++++
		// result into FG_P_EXPREPORT_DATA_TMP
		//+++++++++++++++++++++++++++++++++++++++++++++
		String numRowsResult = "";
		Map<String, String> map = new HashMap<>();
		map.put("statekey_in", String.valueOf(stateKey));
		map.put("resulttype_in", resulttype);
		map.put("characteristicMassBalan_in", characteristicMassBalan);
		map.put("imputityMatIds_in", imputityMatIds);
		map.put("sampleComments_in", sampleComments);
		map.put("sampleCreator_in", sampleCreator);
		map.put("sampleAmount_in", sampleAmount);
		map.put("expdIds_in", generalUtil.handleClob("," + expIds + ","));
		numRowsResult = generalDao.callPackageFunction("FG_ADAMA_EXP_REPORT", "GET_UPDATE_P_EXPREPORT_DATA", map);

		
		// +++++++ if we have data => add RESULT_SMARTPIVOTSQL to sbSelectSql 
		if((numRowsResult != null && !numRowsResult.equals("0")) || (numRowsDisplayData != null && !numRowsDisplayData.equals("0"))) {
			sbSelectSql.append(",'SELECT result_SMARTPIVOT FROM FG_P_EXPREPORT_DATA_TMP where statekey=''" + stateKey + "'' order by order_, order2' AS RESULT_SMARTPIVOTSQL\n" ); 
		}
		
		//+++++++++++++++++++++++++++++++++++++++++++++
				// step conclusion
				//+++++++++++++++++++++++++++++++++++++++++++++
				String[] displayLevelArray = stepNumberNamesCsv.split(",", -1);

				boolean first_step = true;
				for (String singleDisplayName : displayLevelArray) {
					if (!singleDisplayName.isEmpty()) {
						String aliasName = "CR" + index; // Alias

						//with
						sbWithSql.append(((!isCombineRulseExist && first_step) ? "with " : ", ") + "CR" + index + " as (\r\n"
								+ " SELECT DISTINCT T.EXPERIMENT_ID AS EXPID\r\n"
								+ "   ,fg_get_richtext_display(t.CONCLUSSION) as step_conclusion\r\n"
								+ "  FROM FG_s_step_v t\r\n" + "  WHERE t.sessionid is null and t.active=1\r\n"
								+ "  AND t.STEP_ID in (" + stepIds + ")\r\n" + "  AND lower('Step '|| t.FORMNUMBERID) = lower('"
								+ singleDisplayName + "')\r\n" + ")"); // and experiment id where part (or on temp table we create in the beginning for performance)

						//select
						sbSelectSql.append("," + aliasName + ".step_conclusion as \""
								+ getValidOracleColumnName("{" + index + "}" + singleDisplayName + " - " + "Conclusion")
								+ "\"");

						//from
						sbFromSql.append("," + aliasName);

						//where
						sbWhereSql.append(" AND t.EXPERIMENT_ID = " + aliasName + ".EXPID(+)");

						index++;
						first_step = false;
					}
				} 
		
		// return the sql obj...
		return new SQLObj(sbWithSql.toString(), sbSelectHiddebSql.toString(), sbSelectSql.toString(),sbFromSql.toString(), sbWhereSql.toString(), "");
	}
	
	private String getStepNumberNamesCsv(String stepIds) {
		String toReturn = "";
		toReturn = generalDao.getCSVBySql("select distinct  'Step ' || FORMNUMBERID from fg_s_step_v where step_id in (" + stepIds + ") order by 'Step ' ||FORMNUMBERID", false);
		return toReturn;
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
