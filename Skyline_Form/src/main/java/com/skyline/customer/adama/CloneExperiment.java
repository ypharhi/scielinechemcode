package com.skyline.customer.adama;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.skyline.form.bean.CloneType;
import com.skyline.form.bean.LookupType;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.FormSaveDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilForm;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.form.service.IntegrationEvent;
import com.skyline.form.service.IntegrationValidation;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
/**
 * contains clone methods for copy Adama experiment data, by copy all rows of
 * each table under experiment hierarchy (from top to bottom) in single insert
 * sql and update the id column of the upper level (like step_id in action
 * ect..) Note: we don't user formSaveDao methods that update the
 * fg_formlastsavevalue_inf (for for audit-trail and serach) - this is made by
 * sched task schedCorrectRecentSearchData
 * 
 * @author YPharhi
 *
 */
public class CloneExperiment {

	@Autowired
	private CommonFunc commonFunc;
	
	@Autowired
	protected GeneralDao generalDao;

	@Autowired
	protected FormDao formDao; // for lookups

	@Autowired
	protected FormSaveDao formSaveDao; // for updateStructTableFormCode (code to be on the safe side can be replace
										// after tests if needed)

	@Autowired
	private GeneralUtil generalUtil;
	
	@Autowired
	private GeneralUtilForm generalUtilForm;

	@Autowired
	protected IntegrationEvent integrationEvent;

	@Autowired
	protected IntegrationValidation integrationValidation;
	
	@Autowired
	protected GeneralUtilLogger generalUtilLogger;

	// hold the maps ->
	@Value("${holdExperimentCloneMap:0}")
	private int holdExperimentCloneMap;
	private String userIdHolder;
	private Map<String, String> columnDataToTemplate;
	private Map<String, String> columnDataFromTemplate;
	private Map<String, String> columnDataFromExperiment;
	private Map<String, String> columnDataFromRun;

	/**
	 * 
	 * @param sourceExperimentId
	 * @param protocolType
	 * @param experimentCloneId- the new experiment ID
	 * @param userId
	 * @param cloneType
	 * @param templateId- CloneType.FROM_TEMPLATE: gets the source template that holds the template data when creating an experiment based on a template, in order to know which runs are copied.
	 *  In other cases it should be null or empty
	 * @return
	 */
	public String cloneExperiment(String sourceExperimentId, String protocolType, String experimentCloneId,
			String userId, CloneType cloneType, String templateId){
		if (protocolType.equals("Formulation")) {
			// Formulants and ProductMixture clone
//			if (cloneType.equals(CloneType.GENERAL)) {
//				insertRef("formulantref", sourceExperimentId, userId, "EXPERIMENT", experimentCloneId, null,
//						"FG_S_BATCHSELECT_PIVOT", "", cloneType);
//			} else {
//				insertRef("formulantref", sourceExperimentId, userId, "EXPERIMENT", experimentCloneId, null, null, "",
//						cloneType);
//			} formulation experiment change clone composition ->
			insertRef("composition", sourceExperimentId, userId, "EXPERIMENT", experimentCloneId, null,
					null, "", cloneType);
			
			// clone Step/Action/SelfTest
			cloneStep(sourceExperimentId, experimentCloneId, userId, protocolType, cloneType);
		} 
		else if (protocolType.equals("Organic") || protocolType.equals("Continuous Process")) {
			cloneStep(sourceExperimentId, experimentCloneId, userId, protocolType, cloneType);
			// paremeters
			insertRef("ParamRef", sourceExperimentId, userId, "EXPERIMENT", experimentCloneId, null, null, "",cloneType);
			
			//runs
			if(protocolType.equals("Continuous Process"))
			{
				insertRun(sourceExperimentId, experimentCloneId, userId, cloneType,templateId);
			}
			
		} else if (protocolType.equals("Analytical")) {
			if (!cloneType.equals(CloneType.GENERAL)) {
				insertRef("AnalytMethodSelect", sourceExperimentId, userId, "EXPERIMENT", experimentCloneId, null, null,
						"", cloneType);
				insertRef("fg_formmonitoparam_data", sourceExperimentId, userId, "EXPERIMENT", experimentCloneId, null,
						null, "", cloneType);
			}
			insertRef("TempGradientRef", sourceExperimentId, userId, "EXPERIMENT", experimentCloneId, null, null, "",
					cloneType);
			insertRef("Component", sourceExperimentId, userId, "EXPERIMENT", experimentCloneId, null, null, "",
					cloneType);
			insertRef("EquipmentRef", sourceExperimentId, userId, "EXPERIMENT", experimentCloneId, null, null, "",
					cloneType);
			insertRef("ColumnSelect", sourceExperimentId, userId, "EXPERIMENT", experimentCloneId, null, null, "",
					cloneType);
			insertRef("InstrumentRef", sourceExperimentId, userId, "EXPERIMENT", experimentCloneId, null, null, "",
					cloneType);
			insertRef("MobilePhaseCompos", sourceExperimentId, userId, "EXPERIMENT", experimentCloneId, null, null, "",
					cloneType);
			insertRef("FG_DYNAMICPARAMS", sourceExperimentId, userId, "EXPERIMENT", experimentCloneId, null, null, "",
					cloneType);
			insertRef("fg_formadditionaldata", sourceExperimentId, userId, "EXPERIMENT", experimentCloneId, null, null,
					"", cloneType);

		} else if (protocolType.equals("Parametric")) {

			insertRef("InstrumentRef", sourceExperimentId, userId, "EXPERIMENT", experimentCloneId, null, null, "",
					cloneType);
			insertRef("fg_formadditionaldata", sourceExperimentId, userId, "EXPERIMENT", experimentCloneId, null, null,
					"", cloneType);
			insertRef("EquipmentRef", sourceExperimentId, userId, "EXPERIMENT", experimentCloneId, null, null, "",
					cloneType);

		} else if (protocolType.equals("Stability")) {
			// TODO (?) "Taro develop"
		}

		// Documents clone
		if (cloneType.equals(CloneType.TO_TEMPLATE)) {
			insertRef("document", sourceExperimentId, userId, "EXPERIMENT", experimentCloneId, null, null, "",
					cloneType);
		}

		// No need to clone Samples tab Crew tab and Results tab

		// Experiment clone
		String colList = generalDao.getTableColCsv("FG_S_EXPERIMENT_PIVOT");
		/*
		 * temporary workaround: added comma sign to replace 'TIMESTAMP'. TODO: do all
		 * replaces in func getColumnDataToClone()
		 */
		String valList = getColumnDataToClone("Experiment", colList, userId, cloneType).replace("CHANGE_BY", userId)
				.replace(",TIMESTAMP", ",sysdate").replace("CLONEID", experimentCloneId)
				.replace("CREATION_DATE", "sysdate").replace("CREATED_BY", userId).replace("FORMID", experimentCloneId);
		String sql = String.format(
				"insert into FG_S_EXPERIMENT_PIVOT (%1$s) select %2$s from FG_S_EXPERIMENT_PIVOT t where formid = %3$s",
				colList, valList, sourceExperimentId);
		formSaveDao.updateSingleStringInfoNoTryCatch(sql);// , "FG_S_EXPERIMENT_PIVOT", experimentCloneId);
		
		return "";

	}

	/**
	 * 
	 * @param experimentId
	 * @param exprunplanningId
	 * @param runNumber
	 * @param userId
	 * @return -1 - there is no data to clone or update , 0 - update steps without clone, 1 - clone and update steps
	 * @throws Exception
	 */
	public Map<String,String> createRun(String experimentId, String exprunplanningId, String runNumber, String userId) throws Exception {
		//check if run exists
		String sql = "select count(*) from fg_s_step_pivot t where t.EXPERIMENT_ID = '" + experimentId
				+ "' and nvl(runnumber,'NA') = '" + runNumber + "'";
		int isRunExists = generalUtil.getNullInt(generalDao.selectSingleString(sql),-1);
		if(isRunExists != 0) {
			throw new Exception("createRun - the run already exists!");
		}
				
		//get number of run step
		sql = "select count(*) from fg_s_step_all_v t where t.EXPERIMENT_ID = '" + experimentId
				+ "' and sourceRunSteps = '1'";
		int numberOfRunStep = generalUtil.getNullInt(generalDao.selectSingleString(sql),-1);
		if(numberOfRunStep == -1) {
			throw new Exception("createRun - error in number of steps evaluation");
		}

		// get sourceStepIdList
		sql = "select step_id from fg_s_step_all_v t where t.EXPERIMENT_ID = '" + experimentId
				+ "' and t.sourceRunSteps = '1'";
		String sourceStepIdCsv = generalDao.getCSVBySql(sql, true);
		if(sourceStepIdCsv.equals("") || sourceStepIdCsv.equals("-1")) {
			return null;
		}

		//check if first run in the experiment
		sql = "select decode(count(*),0,1,0) as isFirstRun from fg_s_step_pivot t where t.runnumber is not null and t.EXPERIMENT_ID = '" + experimentId + "'";
		int isFirstRun = generalUtil.getNullInt(generalDao.selectSingleString(sql),-1);
		if(numberOfRunStep == -1) {
			throw new Exception("createRun - error in first run evaluation");
		}
		
		// clone step if NOT first run else update the steps on first run
		if (isFirstRun == 1) {
			sql = "update fg_s_step_pivot t set t.runnumber = '" + runNumber + "' where t.formid in (" +  sourceStepIdCsv + ") and t.runnumber is null";
			int numberOfUpdateSteps = generalUtil.getNullInt(generalDao.updateSingleString(sql),-1);
			if(numberOfUpdateSteps != numberOfRunStep) {
				throw new Exception("createRun - number of update steps in first run(" + numberOfUpdateSteps + ") different from number of run steps(" + numberOfRunStep + ")");
			}
		} else {
			int numberOfCloneSteps = cloneStepRun(experimentId, userId, CloneType.RUN_CREATION, sourceStepIdCsv, runNumber);
			if(numberOfCloneSteps != numberOfRunStep) {
				throw new Exception("createRun - cloneStepRun number of clone steps(" + numberOfCloneSteps + ") different from number of run steps(" + numberOfRunStep + ")");
			}
		}
		//update Reactants and Parameters table by Run data. Also update currently started Run status to 'Active'
		String dataUpdated = updateTablesByRunsPlanningData(exprunplanningId, isFirstRun);		
		if(dataUpdated.equals("-1"))
		{
			throw new Exception("createRun - error in update tables by Runs Planning table data.\n Error in change status of new Run from 'Planned' to 'Active'");
		}
		//*** At this point:
		// New Run Status = 'Active'
		// New Run Steps statuses = 'Planned'
		// Previous Run Status = 'Active'
		// Previous Run Steps != 'Planned' ('Active','Finished', 'Canceled')
		
		//if (isFirstRun != 1) {
			
			//change new (currently started) Run Steps status to 'Active' - check familiarity&update experiment status to active&create snapshot
			return updateNewRunStepsStatusToActive(experimentId, runNumber, userId);
			
			
			/*//update status of previous Run Steps from 'Active' to 'Finished'
			String runActiveStatusId = formDao.getFromInfoLookup("RunStatus", LookupType.NAME, "Active", "id");
			String stepFinishedStatusId = formDao.getFromInfoLookup("STEPSTATUS", LookupType.NAME, "Finished","id");
			String stepActiveStatusId = formDao.getFromInfoLookup("STEPSTATUS", LookupType.NAME, "Active","id");
			
			sql = "update fg_s_step_pivot t \n"
				 +" set laststatus_id = status_id"
				 + "   ,t.status_id = '"+stepFinishedStatusId+"'  \n"
				 +" where t.status_id = '"+stepActiveStatusId+"'  \n"
				 +" and t.runnumber = (select distinct t1.runnumber \n"
				                 +" from fg_s_exprunplanning_pivot t1 \n"
				                 +" where t1.runstatusid = '"+runActiveStatusId+"' \n"
				                 +" and t1.formid <> '"+exprunplanningId+"' \n" 
				                 +" and t1.experimentid = t.experiment_id)  \n"
				 +" and t.experiment_id = '"+experimentId+"'";
			int numberOfUpdateSteps = generalUtil.getNullInt(formSaveDao.updateSingleStringInfoNoTryCatch(sql),-1);
			if(numberOfUpdateSteps < 0) {
				throw new Exception("createRun - error in update Steps table data.\n Error in change status of previous Run Steps from 'Active' to 'Finished'.");
			}*/
			
			/*//previous started Run status set from Active to Finished  			
			String runFinishedStatusId = formDao.getFromInfoLookup("RunStatus", LookupType.NAME, "Finished", "id");
			sql = "update fg_s_exprunplanning_pivot t \n"
				+" set t.runstatusid = '"+runFinishedStatusId+"' \n"
				+" where t.runstatusid = '"+runActiveStatusId+"' \n"
				+" and t.formid <> '"+exprunplanningId+"' \n"
				+" and t.experimentid = '"+experimentId+"'";
			int numberOfUpdateRuns = generalUtil.getNullInt(formSaveDao.updateSingleStringInfoNoTryCatch(sql),-1); 
			if(numberOfUpdateRuns <= 0) {
				throw new Exception("createRun - error in update Runs Planning table data.\n Error in change status of previous Run from 'Active' to 'Finished'.");
			}*/
		//}
		
		// return..
		//return isFirstRun == 1 ? 0 : 1;
	}
	
	private void cloneStep(String sourceExperimentId, String experimentCloneId, String userId, String protocolType,
			CloneType cloneType) {

		String sql = "";
		String colList = "";
		String valList = "";
		String stepIdConditionByCloneAndProtocolType = "";
		String actionIdConditionByCloneAndProtocolType = "";
		String selfTestIdConditionByCloneAndProtocolType = "";
		String workupIdConditionByCloneAndProtocolType = "";
		
		if(generalUtil.getNull(protocolType).equalsIgnoreCase("Continuous Process")) { // && we will do it on all CloneType 
			//get source step list...
			sql = "select step_id from fg_s_step_all_v t where t.EXPERIMENT_ID = '" + sourceExperimentId
					+ "' and t.sourceSteps = '1'";
			String sourceStepIdCsv = generalDao.getCSVBySql(sql, true);
			if(sourceStepIdCsv.equals("") || sourceStepIdCsv.equals("-1")) {
				sourceStepIdCsv = "'-1'";
			}
			
			//stepIdConditionByCloneAndProtocolType
			stepIdConditionByCloneAndProtocolType = " and t.formid in (" + sourceStepIdCsv + ")";
			
			//stepIdConditionByCloneAndProtocolType
			actionIdConditionByCloneAndProtocolType = " and t.step_id in (" + sourceStepIdCsv + ")";
			
			//selfTestIdConditionByCloneAndProtocolType
			selfTestIdConditionByCloneAndProtocolType = " and exists (select 1 from fg_s_selftest_all_v where fg_s_selftest_all_v.formid = t.formid and fg_s_selftest_all_v.step_id in (" + sourceStepIdCsv + "))";
			
			//workupIdConditionByCloneAndProtocolType
			workupIdConditionByCloneAndProtocolType = " and exists (select 1 from fg_s_workup_all_v where fg_s_workup_all_v.formid = t.formid and fg_s_workup_all_v.step_id in (" + sourceStepIdCsv + "))";
		}

		// Step clone
		colList = generalDao.getTableColCsv("FG_S_STEP_PIVOT").replace(",CLONEID", "");
		valList = getColumnDataToClone("Step", colList, userId, cloneType).replace("CHANGE_BY", userId)
				.replace("TIMESTAMP", "sysdate").replace("CREATION_DATE", "sysdate").replace("CREATED_BY", userId);
		sql = String.format(
				"insert into FG_S_STEP_PIVOT (%1$s, CLONEID) select  %2$s, fg_get_struct_form_id(t.formcode) from FG_S_STEP_PIVOT t where t.experiment_id = %3$s %4$s",
				colList, valList, sourceExperimentId, stepIdConditionByCloneAndProtocolType);
		formSaveDao.updateSingleStringInfoNoTryCatch(sql);// , "FG_S_STEP_PIVOT", "experiment_id", sourceExperimentId);

		// Formulants and ProductMixture clone
//		if (cloneType.equals(CloneType.GENERAL)) {
//			insertRef("formulantref", sourceExperimentId, userId, "STEP", "", null, "FG_S_BATCHSELECT_PIVOT", "",
//					cloneType);
//		} else {
//			insertRef("formulantref", sourceExperimentId, userId, "STEP", "", null, null, "", cloneType);
//		} // formulation experiment change clone composition ->
		insertRef("composition", sourceExperimentId, userId, "STEP", "", null, null, "", cloneType);

		// Reactants/Solvets/Products
		insertRef("MaterialRef", sourceExperimentId, userId, "STEP", "", null, null, "", cloneType);

		// paremeters
		insertRef("ParamRef", sourceExperimentId, userId, "STEP", "", null, null, "", cloneType);

		// Instruments clone
		insertRef("InstrumentRef", sourceExperimentId, userId, "STEP", "", null, null, "", cloneType);

		// Additional Equipment clone
		insertRef("EquipmentRef", sourceExperimentId, userId, "STEP", "", null, null, "", cloneType);

		// Additional Equipment clone
		// insertRef("Requestselect",sourceExperimentId,userId,"STEP", "", null, null);

		// Documents clone
		if (cloneType.equals(CloneType.TO_TEMPLATE) || cloneType.equals(CloneType.GENERAL)) {
			insertRef("document", sourceExperimentId, userId, "STEP", "", null, null, "", cloneType);
		}

		// Action clone
		colList = generalDao.getTableColCsv("FG_S_ACTION_PIVOT").replace(",CLONEID", "");
		valList = getColumnDataToClone("Action", colList, userId, cloneType).replace("CHANGE_BY", userId)
				.replace("TIMESTAMP", "sysdate").replace("CREATION_DATE", "sysdate").replace("CREATED_BY", userId)
				.replace("SELFTESTIDHOLDER", "null");
		sql = String.format(
				"insert into FG_S_ACTION_PIVOT (%1$s, CLONEID) select  %2$s,  fg_get_struct_form_id(t.formcode) from FG_S_ACTION_PIVOT t where t.experiment_id = %3$s %4$s",
				colList, valList, sourceExperimentId, actionIdConditionByCloneAndProtocolType);
		formSaveDao.updateSingleStringInfoNoTryCatch(sql);// , "FG_S_ACTION_PIVOT", "experiment_id",
															// sourceExperimentId);

		// Documents clone
		if (cloneType.equals(CloneType.TO_TEMPLATE)) {
			insertRef("document", sourceExperimentId, userId, "ACTION", "", null, null, "", cloneType);
		}

		String sqlString = "";
		List<String> formIds;

		if (cloneType.equals(CloneType.TO_TEMPLATE)) {
			// SelfTest clone
			String stCancelledStatusId = formDao.getFromInfoLookup("SelfTestStatus", LookupType.NAME, "Cancelled",
					"ID");
			colList = generalDao.getTableColCsv("FG_S_SELFTEST_PIVOT").replace(",CLONEID", "");
			valList = getColumnDataToClone("SelfTest", colList, userId, cloneType).replace("CHANGE_BY", userId)
					.replace("TIMESTAMP", "sysdate").replace("CREATION_DATE", "sysdate")
					.replace("CREATED_BY", userId);
			sql = String.format(
					"insert into FG_S_SELFTEST_PIVOT (%1$s, CLONEID) select  %2$s, fg_get_struct_form_id(t.formcode) from FG_S_SELFTEST_PIVOT t where experiment_id = %3$s and status_id <> %4$s %5$s",
					colList, valList, sourceExperimentId, stCancelledStatusId, selfTestIdConditionByCloneAndProtocolType);
			formSaveDao.updateSingleStringInfoNoTryCatch(sql);// , "FG_S_SELFTEST_PIVOT", "experiment_id",
																// sourceExperimentId);

			// Instrument clone
			// insertRef("InstrumentSelect",sourceExperimentId,userId, "SELFTEST", "", null,
			// null,"");
			// AnalytMethodSelect
			insertRef("AnalytMethodSelect", sourceExperimentId, userId, "SELFTEST", "", null, null, "", cloneType);
			// ColumnSelect
			insertRef("ColumnSelect", sourceExperimentId, userId, "SELFTEST", "", null, null, "", cloneType);
			/*
			 * //SampleSelect insertRef("SampleSelect",sourceExperimentId,userId,
			 * "SELFTEST", "", null, null,"");
			 */
			// Documents clone
			insertRef("document", sourceExperimentId, userId, "SELFTEST", "", null, null, "", cloneType);
			// yp 16122019 - fot task 24536 we need this fields if the selftest is used as
			// default data
			// (1)result ref
			insertRef("resultref", sourceExperimentId, userId, "SELFTEST", "", null, null, "", cloneType);
			// (2)instrumentselect
			insertRef("instrumentselect", sourceExperimentId, userId, "SELFTEST", "", null, null, "", cloneType);

			if (protocolType.equals("Organic") || protocolType.equals("Continuous Process")) {
				// Workup clone
				cloneWorkup(sourceExperimentId, experimentCloneId, userId, cloneType, workupIdConditionByCloneAndProtocolType);

			}
			// update SelfTest table : formid to clone id and action_id
			sql = String.format("update  FG_S_SELFTEST_PIVOT st set formid = st.cloneid, st.action_id = "
					+ "(select  a.cloneid  from FG_S_ACTION_PIVOT a where 1=1 and  a.FORMID  <> a.Cloneid and st.action_id = a.formid and a.active=1)"
					+ ", st.experiment_id = %1$s where st.cloneid <> st.formid and   experiment_id = %2$s",
					experimentCloneId, sourceExperimentId);
			formSaveDao.updateSingleStringInfoNoTryCatch(sql);// "FG_S_SELFTEST_PIVOT",
																// Arrays.asList("formid","action_id","experiment_id"),
																// "experiment_id", sourceExperimentId);

			sqlString = "Select t.FORMID || ',' || nvl(t.TYPE_ID,'NA') || ',' || NVL(t.formCode,'NA') from FG_S_SELFTEST_PIVOT t, fg_sequence s where t.experiment_id = "
					+ experimentCloneId
					+ " and t.cloneid is not null and t.active=1 and to_char(s.id) = t.formid and nvl(t.formcode,'1') <> nvl(s.formcode,'2')";
			formIds = generalDao.getListOfStringBySql(sqlString);
			if (formIds != null && formIds.size() > 0) {
				for (String currentFormId : formIds) {
					updateSelfTestFormCode(currentFormId);
				}
			}
		}

		if (cloneType.equals(CloneType.GENERAL)) {
			if (protocolType.equals("Organic") || protocolType.equals("Continuous Process")) {
				// Workup clone
				cloneWorkup(sourceExperimentId, experimentCloneId, userId, cloneType, workupIdConditionByCloneAndProtocolType);
			}
		}

		// update Action table: formid to clone id and step_id
		sql = String.format("update  FG_S_ACTION_PIVOT a set formid = a.cloneid, a.step_id = "
				+ "(select  s.cloneid  from FG_S_STEP_PIVOT s where 1=1 and  s.FORMID  <> s.Cloneid and a.step_id = s.formid)"
				+ ", a.experiment_id = %1$s where a.cloneid <> a.formid and   experiment_id = %2$s", experimentCloneId,
				sourceExperimentId);
		formSaveDao.updateSingleStringInfoNoTryCatch(sql);// , "FG_S_ACTION_PIVOT",
															// Arrays.asList("formid","step_id","experiment_id"),
															// "experiment_id", sourceExperimentId);

		// update Step table: formid to clone id and experiment_id
		sql = String.format(
				"update  FG_S_STEP_PIVOT s set formid = s.cloneid, experiment_id =  %1$s"
						+ " where s.cloneid <> s.formid and   experiment_id = %2$s",
				experimentCloneId, sourceExperimentId);
		formSaveDao.updateSingleStringInfoNoTryCatch(sql);// "FG_S_STEP_PIVOT", Arrays.asList("formid","experiment_id"),
															// "experiment_id", sourceExperimentId);

		sqlString = "Select t.FORMID from FG_S_STEP_PIVOT t, fg_sequence s where experiment_id = " + experimentCloneId
				+ " and t.cloneid is not null and t.active=1 and to_char(s.id) = t.formid and nvl(t.formcode,'1') <> nvl(s.formcode,'2')";
		formIds = generalDao.getListOfStringBySql(sqlString);
		if (formIds != null && formIds.size() > 0) {
			for (String currentFormId : formIds) {
				if (protocolType.equals("Formulation")) {
					formSaveDao.updateStructTableFormCode("Step", "StepFr", currentFormId, true);
				} else if (protocolType.equals("Organic") || protocolType.equals("Continuous Process")) {
					formSaveDao.updateStructTableFormCode("Step", "Step", currentFormId, true);
				}
			}
		}
	}
	
	private int cloneStepRun(String sourceExperimentId, String userId,
			CloneType cloneType, String sourceStepIdCsv, String runNumber) {

		String sql = "";
		String colList = "";
		String valList = "";
		String updateRow = "";

		// Step clone NOTE getColumnDataToClone make CLONERUNFLAG = 1 AND runnumber = NULL
		colList = generalDao.getTableColCsv("FG_S_STEP_PIVOT").replace(",CLONEID", "");
		valList = getColumnDataToClone("Step", colList, userId, cloneType).replace("CHANGE_BY", userId)
				.replace("TIMESTAMP", "sysdate").replace("CREATION_DATE", "sysdate").replace("CREATED_BY", userId);
		sql = String.format(
				"insert into FG_S_STEP_PIVOT (%1$s, CLONEID) select  %2$s, fg_get_struct_form_id(t.formcode) from FG_S_STEP_PIVOT t where t.experiment_id = %3$s and t.formid in (%4$s)",
				colList, valList, sourceExperimentId, sourceStepIdCsv);
		updateRow = formSaveDao.updateSingleStringInfoNoTryCatch(sql);// , "FG_S_STEP_PIVOT", "experiment_id", sourceExperimentId);

		// Reactants/Solvets/Products
		insertRef("MaterialRef", sourceExperimentId, userId, "STEP", "", null, null, "", cloneType);

		// paremeters
		insertRef("ParamRef", sourceExperimentId, userId, "STEP", "", null, null, "", cloneType);

		// Instruments clone
		insertRef("InstrumentRef", sourceExperimentId, userId, "STEP", "", null, null, "", cloneType);

		// Additional Equipment clone
		insertRef("EquipmentRef", sourceExperimentId, userId, "STEP", "", null, null, "", cloneType);

		// Additional Equipment clone
		// insertRef("Requestselect",sourceExperimentId,userId,"STEP", "", null, null);

		// Documents clone
		//insertRef("document", sourceExperimentId, userId, "STEP", "", null, null, "", cloneType);
		 

		// Action clone
		colList = generalDao.getTableColCsv("FG_S_ACTION_PIVOT").replace(",CLONEID", "");
		valList = getColumnDataToClone("Action", colList, userId, cloneType).replace("CHANGE_BY", userId)
				.replace("TIMESTAMP", "sysdate").replace("CREATION_DATE", "sysdate").replace("CREATED_BY", userId)
				.replace("SELFTESTIDHOLDER", "null");
		sql = String.format(
				"insert into FG_S_ACTION_PIVOT (%1$s, CLONEID) select  %2$s,  fg_get_struct_form_id(t.formcode) from FG_S_ACTION_PIVOT t where t.experiment_id = %3$s and t.step_id in (%4$s)",
				colList, valList, sourceExperimentId, sourceStepIdCsv);
		updateRow = formSaveDao.updateSingleStringInfoNoTryCatch(sql);// , "FG_S_ACTION_PIVOT", "experiment_id",
															// sourceExperimentId);

		// Workup clone todo if needed
		//String workupIdCondition = " and exists (select 1 from fg_s_workup_all_v where fg_s_workup_all_v.formid = t.formid and fg_s_workup_all_v.step_id in (" + sourceStepIdCsv + "))";
		//cloneWorkup(sourceExperimentId, sourceExperimentId, userId, cloneType, workupIdCondition);
			 
		// update Action table: formid to clone id and step_id
		sql = String.format("update  FG_S_ACTION_PIVOT a set formid = a.cloneid, a.step_id = "
				+ "(select  s.cloneid  from FG_S_STEP_PIVOT s where 1=1 and  s.FORMID  <> s.Cloneid and a.step_id = s.formid and s.experiment_id = %1$s)"
				+ " where a.cloneid <> a.formid and  experiment_id = %1$s", sourceExperimentId);
		updateRow = formSaveDao.updateSingleStringInfoNoTryCatch(sql);// , "FG_S_ACTION_PIVOT",
															// Arrays.asList("formid","step_id","experiment_id"),
															// "experiment_id", sourceExperimentId);

		// update Step table: formid to clone id and experiment_id
		sql = String.format(
				"update FG_S_STEP_PIVOT s set formid = s.cloneid, s.runnumber = '%1$s' where s.cloneid <> s.formid and experiment_id = %2$s",
				runNumber, sourceExperimentId);
		updateRow = formSaveDao.updateSingleStringInfoNoTryCatch(sql);// "FG_S_STEP_PIVOT", Arrays.asList("formid","experiment_id"),
															// "experiment_id", sourceExperimentId);
		return generalUtil.getNullInt(updateRow, -1);
	}

	private void cloneWorkup(String sourceExperimentId, String experimentCloneId, String userId, CloneType cloneType, String workupIdCondition) {
		String colList = generalDao.getTableColCsv("FG_S_WORKUP_PIVOT").replace(",CLONEID", "");
		String valList = getColumnDataToClone("Workup", colList, userId, cloneType).replace("CHANGE_BY", userId)
				.replace("TIMESTAMP", "sysdate").replace("CREATION_DATE", "sysdate").replace("CREATED_BY", userId);
		String sql = String.format(
				"insert into FG_S_WORKUP_PIVOT (%1$s, CLONEID) select  %2$s, fg_get_struct_form_id(t.formcode) from FG_S_WORKUP_PIVOT t where experiment_id = '%3$s' %4$s",
				colList, valList, sourceExperimentId, workupIdCondition);
		formSaveDao.updateSingleStringInfoNoTryCatch(sql);// "FG_S_WORKUP_PIVOT", "experiment_id", sourceExperimentId);

		if (cloneType.equals(CloneType.GENERAL) || cloneType.equals(CloneType.RUN_CREATION)) {
			insertRef("WuCryMixDefineRef", sourceExperimentId, userId, "WORKUP", "", null, null,
					"and tableType = 'MotherLiquor'", cloneType);
			insertRef("WuCrystSolventRef", sourceExperimentId, userId, "WORKUP", "", null, null, "", cloneType);
			insertRef("WuFeedMaterialRef", sourceExperimentId, userId, "WORKUP", "", null, null, "", cloneType);
		} else {// from template
			insertRef("WuCryMixDefineRef", sourceExperimentId, userId, "WORKUP", "", null, null, "", cloneType);
			// insertRef("WuCryMotherLiqRef",sourceExperimentId,userId, "WORKUP", "");
			insertRef("WuCrystMonitorRef", sourceExperimentId, userId, "WORKUP", "", null, null, "", cloneType);
			insertRef("WuCrystSolventRef", sourceExperimentId, userId, "WORKUP", "", null, null, "", cloneType);
			insertRef("WuDistFractionRef", sourceExperimentId, userId, "WORKUP", "", null, null, "", cloneType);
			insertRef("WuDistStartMixRef", sourceExperimentId, userId, "WORKUP", "", null, null, "", cloneType);
			insertRef("WuDistilMonitorRef", sourceExperimentId, userId, "WORKUP", "", null, null, "", cloneType);
			insertRef("WuDistilYieldRef", sourceExperimentId, userId, "WORKUP", "", null, null, "", cloneType);
			insertRef("WuDryingRef", sourceExperimentId, userId, "WORKUP", "", null, null, "", cloneType);
			insertRef("WuFeedMaterialRef", sourceExperimentId, userId, "WORKUP", "", null, null, "", cloneType);
			insertRef("WuFeedMonitgRef", sourceExperimentId, userId, "WORKUP", "", null, null, "", cloneType);
			insertRef("WuFiltraFeedRef", sourceExperimentId, userId, "WORKUP", "", null, null, "", cloneType);
			insertRef("WuFiltraSqueezRef", sourceExperimentId, userId, "WORKUP", "", null, null, "", cloneType);
			insertRef("WuFiltraWashingRef", sourceExperimentId, userId, "WORKUP", "", null, null, "", cloneType);
			insertRef("WuFiltraWetCakeRef", sourceExperimentId, userId, "WORKUP", "", null, null, "", cloneType);
			insertRef("WuWashExtInterRef", sourceExperimentId, userId, "WORKUP", "", null, null, "", cloneType);
			insertRef("WuWashExtSeparRef", sourceExperimentId, userId, "WORKUP", "", null, null, "", cloneType);
			insertRef("WuWashLiquidAddRef", sourceExperimentId, userId, "WORKUP", "", null, null, "", cloneType);
		}

		// update Workup table : formid to clone id and selftest_id
		sql = String.format("update  FG_S_WORKUP_PIVOT st set formid = st.cloneid, st.action_id = "
				+ "(select  a.cloneid  from FG_S_ACTION_PIVOT a where 1=1 and  a.FORMID  <> a.Cloneid and st.action_id = a.formid and a.active=1)"
				+ ", st.experiment_id = %1$s , templateflag = " + ((cloneType.equals(CloneType.GENERAL) || cloneType.equals(CloneType.RUN_CREATION)) ? "0" : "1")
				+ " where st.cloneid <> st.formid and   experiment_id = '%2$s' ", experimentCloneId,
				sourceExperimentId);
		formSaveDao.updateSingleStringInfoNoTryCatch(sql);// "FG_S_WORKUP_PIVOT",
															// Arrays.asList("formid","action_id","experiment_id","templateflag"),
															// "experiment_id", sourceExperimentId);

		String sqlString = "Select t.FORMID || ',' || nvl(t.WORKUPTYPE_ID,'NA') || ',' || NVL(t.formCode,'NA') from FG_S_WORKUP_PIVOT t, fg_sequence s where t.experiment_id = '"
				+ experimentCloneId
				+ "' and t.cloneid is not null and t.active=1 and to_char(s.id) = t.formid and nvl(t.formcode,'1') <> nvl(s.formcode,'2')";
		List<String> formIds = generalDao.getListOfStringBySql(sqlString);
		if (formIds != null && formIds.size() > 0) {
			for (String currentFormId : formIds) {
				updateWorkupFormCode(currentFormId);
			}
		}

	}

	public void insertRef(String formCode, String sourceExperimentId, String userId, String parentFormCode,
			String cloneParentId, String tableType, String subRefTable, String wherePart) {
		insertRef(formCode, sourceExperimentId, userId, parentFormCode, cloneParentId, tableType, subRefTable,
				wherePart, CloneType.GENERAL);
	}

	public void insertRef(String formCode, String sourceExperimentId, String userId, String parentFormCode,
			String cloneParentId, String tableType, String subRefTable, String wherePart, CloneType cloneType) {

		String parentId = (!cloneParentId.isEmpty()) ? "r.@parentid@ =" + sourceExperimentId
				: String.format(
						" exists (select 1 from FG_S_%1$s_PIVOT s where s.experiment_id= '%2$s' and s.formId = r.PARENTID and s.cloneid <> s.formId and r.@parentid@ = s.formid)",
						parentFormCode, sourceExperimentId);
		cloneParentId = (!cloneParentId.isEmpty()) ? cloneParentId
				: String.format(
						"(select  s.cloneid  from FG_S_%1$s_PIVOT s where s.experiment_id= '%2$s' and s.formId = r.PARENTID and s.cloneid <> s.formId )",
						parentFormCode, sourceExperimentId);

		String table = "fg_s_" + formCode + "_pivot";

		// insert
		String sql = "";
		String colList = "";
		String valList = "";

		if (formCode.equals("fg_formadditionaldata") || formCode.equals("FG_DYNAMICPARAMS")
				|| formCode.equals("fg_formmonitoparam_data")) {
			table = formCode;
			String parentIdColumn = (formCode.equals("FG_DYNAMICPARAMS") || formCode.equals("fg_formmonitoparam_data"))
					? "PARENT_ID"
					: "PARENTID";

			if (formCode.equals("fg_formmonitoparam_data")) {
				colList = generalDao.getTableColCsv(formCode).replace(parentIdColumn + ",", "").replace(",ID", "")
						.replace("," + parentIdColumn, "");

				valList = colList;
			} else {
				colList = ","
						+ generalDao.getTableColCsv(formCode).replace("," + parentIdColumn, "").replace(",ID", "");

				valList = colList;
				colList = colList.substring(1, colList.length());
				valList = valList.substring(1, valList.length());
			}

			sql = String.format("insert into %1$s r (%2$s, " + parentIdColumn + " ) select %3$s,  %4$s "
					+ " from %5$s r where %6$s", table, colList, valList, cloneParentId,
					table, parentId.replace("@parentid@", parentIdColumn));
			formSaveDao.updateSingleStringInfoNoTryCatch(sql);

		} else {
			// String selectedListCsv = "";

			colList = "," + generalDao.getTableColCsv(table) + ",";
			colList = colList.replace(",CLONEID,", ",").replace(",PARENTID,", ",");

			if (formCode.equalsIgnoreCase("ParamRef")) {
				// t.plannedval1, t.plannedval2, t.planned_criteria_id
				valList = getColumnDataToClone(formCode, colList, userId, cloneType).replace("CHANGE_BY", userId)
						.replace(",TIMESTAMP,", ",sysdate,").replace(",CREATION_DATE,", ",sysdate,")
						.replace(",CREATED_BY,", "," + userId + ",")
						.replace(",TABLETYPE,", tableType == null ? ",TABLETYPE," : ",'" + tableType + "',")
						.replace(",AREA,", ",null,").replace(",NORMALIZATION,", ",null,")
						.replace(",RETENTIONTIME,", ",null,").replace(",NONNUMERICRESULT,", ",null,")
						.replace(",INSTRUMENT_ID,", ",null,").replace(",COMMENTS,", ",null,")
						.replace(",PLANNEDVAL1,", ",null,").replace(",PLANNEDVAL2,", ",null,")
						.replace(",PLANNED_CRITERIA_ID,", ",null,");
			} else {
				valList = getColumnDataToClone(formCode, colList, userId, cloneType).replace("CHANGE_BY", userId)
						.replace(",TIMESTAMP,", ",sysdate,").replace(",CREATION_DATE,", ",sysdate,")
						.replace(",CREATED_BY,", "," + userId + ",")
						.replace(",TABLETYPE,", tableType == null ? ",TABLETYPE," : ",'" + tableType + "',")
						.replace(",AREA,", ",null,").replace(",NORMALIZATION,", ",null,")
						.replace(",RETENTIONTIME,", ",null,").replace(",NONNUMERICRESULT,", ",null,")
						.replace(",INSTRUMENT_ID,", ",null,").replace(",COMMENTS,", ",null,");
			}

			colList = colList.substring(1, colList.length() - 1);
			valList = valList.substring(1, valList.length() - 1);

			sql = String.format(
					"insert into %1$s r (%2$s ,CLONEID, PARENTID )  select %3$s, fg_get_struct_form_id(r.formcode), %5$s "
							+ " from %6$s r where %7$s and sessionid is null and nvl(active,'1')='1' "
							+ wherePart,
					table, colList, valList, table, cloneParentId, table, parentId.replace("@parentid@", "parentid"));
			formSaveDao.updateSingleStringInfoNoTryCatch(sql);

			if (subRefTable != null) {
				if (!isNumeric(cloneParentId)) {
					cloneParentId = String.format(
							"(select  s.cloneid  from FG_S_%1$s_PIVOT s where s.experiment_id= '%2$s' and s.cloneId = p.PARENTID and s.cloneid <> s.formId )",
							parentFormCode, sourceExperimentId);
				}
				String parentIdRef = String.format(
						"(select  p.formid  from %1$s p where p.PARENTID= %2$s and p.formId = r.PARENTID)", table,
						cloneParentId);

				String cloneParentIdRef = String.format(
						"(select  p.cloneid  from %1$s p where p.PARENTID= %2$s and p.formId = r.PARENTID)", table,
						cloneParentId);

				colList = "," + generalDao.getTableColCsv(subRefTable);
				colList = colList.replace(",CLONEID", "").replace(",PARENTID", "");
				valList = getColumnDataToClone(formCode, colList, userId, cloneType).replace("CHANGE_BY", userId)
						.replace("TIMESTAMP", "sysdate").replace("CREATION_DATE", "sysdate")
						.replace("CREATED_BY", userId)
						.replace("TABLETYPE", tableType == null ? "TABLETYPE" : "'" + tableType + "'");

				colList = colList.substring(1, colList.length());
				valList = valList.substring(1, valList.length());

				sql = String.format(
						"insert into %1$s r (%2$s ,CLONEID, PARENTID ) select %3$s, fg_get_struct_form_id(r.formcode), %4$s "
								+ " from %1$s r where r.parentid = %5$s",
						subRefTable, colList, valList, cloneParentIdRef, parentIdRef);
				formSaveDao.updateSingleStringInfoNoTryCatch(sql);
				// update formId to clone Id
				sql = String.format("update  %1$s r set r.formid = r.cloneid"
						+ " where r.cloneid <> r.formid and r.cloneid is not null", subRefTable);
				formSaveDao.updateSingleStringInfoNoTryCatch(sql);

			}
			
			// update formId to clone Id 210530
			sql = String.format("update  %1$s r set r.formid = r.cloneid"
					+ " where r.cloneid <> r.formid and r.cloneid is not null", table);
			formSaveDao.updateSingleStringInfoNoTryCatch(sql);

			// change MobilePhaseCompos phase parent Id
			if (formCode.equals("MobilePhaseCompos")) {
				sql = String.format(
						"update fg_s_MobilePhaseCompos_pivot t set t.mobilephasecomposobj = REGEXP_REPLACE (t.mobilephasecomposobj,'\"parentId\":\"(.*?)\"','\"parentId\":\"' || t.parentId || '\"')"
								+ " where t.parentId='" + cloneParentId + "'");
				formSaveDao.updateSingleStringInfoNoTryCatch(sql);
			}

		}
	}

	public void insertRefUniqueColumn(String formCode, String sourceExperimentId, String userId, String parentFormCode,
			String cloneParentId, String tableType, String wherePart,
			String uniqueColumnIndestTable) {

		String parentId = (!cloneParentId.isEmpty()) ? "r.parentid =" + sourceExperimentId
				: String.format(
						"exists (select 1 from FG_S_%1$s_PIVOT s where s.experiment_id= %2$s and s.formId = r.PARENTID and s.cloneid <> s.formId and r.parentid = s.formid)",
						parentFormCode, sourceExperimentId);
		cloneParentId = (!cloneParentId.isEmpty()) ? cloneParentId
				: String.format(
						"(select  s.cloneid  from FG_S_%1$s_PIVOT s where s.experiment_id= %2$s and s.formId = r.PARENTID and s.cloneid <> s.formId )",
						parentFormCode, sourceExperimentId);

		String table = "fg_s_" + formCode + "_pivot";

		// insert
		String sql = "";
		String colList = "";
		String valList = "";

		// String selectedListCsv="";
		colList = "," + generalDao.getTableColCsv(table);
		colList = colList.replace(",CLONEID", "").replace(",PARENTID", "");
		valList = getColumnDataToClone(formCode, colList, userId, CloneType.GENERAL).replace("CHANGE_BY", userId)
				.replace("TIMESTAMP", "sysdate").replace("CREATION_DATE", "sysdate").replace("CREATED_BY", userId)
				.replace("TABLETYPE", tableType == null ? "TABLETYPE" : "'" + tableType + "'");

		colList = colList.substring(1, colList.length());
		valList = valList.substring(1, valList.length());

		sql = String.format(
				"insert into %1$s r (%2$s ,CLONEID, PARENTID )" + " with FILT_IN as (select " + uniqueColumnIndestTable
						+ " from %1$s where parentid = %5$s) select %3$s, fg_get_struct_form_id(r.formcode), %5$s "
						+ " from %6$s r where %7$s " + wherePart + " and r." + uniqueColumnIndestTable
						+ " not in (select " + uniqueColumnIndestTable + " from FILT_IN)",
				table, colList, valList, table, cloneParentId, table, parentId);
		formSaveDao.updateSingleStringInfoNoTryCatch(sql);

		// update formId to clone Id
		sql = String.format(
				"update  %1$s r set r.formid = r.cloneid" + " where r.cloneid <> r.formid and r.cloneid is not null",
				table);
		formSaveDao.updateSingleStringInfoNoTryCatch(sql);
	}

	private String getColumnDataToClone(String keyToSearch, String valList, String userId, CloneType cloneType) {

		valList = "," + valList + ",";
		Map<String, String> columnData = null;

		switch (cloneType) {
		case GENERAL: {
			columnData = setColumnDataToCloneFromExperiment(userId);
			break;
		}
		case TO_TEMPLATE: {
			columnData = setColumnDataToTemplate(userId);
			break;
		}
		case FROM_TEMPLATE: {
			columnData = setColumnDataFromTemplate(userId);
			break;
		}
		case RUN_CREATION: {
			columnData = setColumnDataToCloneFromRun(userId);
			break;
		}
		}

		for (Map.Entry<String, String> entry : columnData.entrySet()) {
			if (entry.getKey().startsWith(keyToSearch)) {
				String columnName = entry.getKey().replace(keyToSearch + ".", "").toUpperCase();
				valList = valList.replace("," + columnName + ",", "," + entry.getValue().toString() + ",");
			}
		}
		valList = valList.substring(1, valList.length() - 1);
		return valList;
	}
	
	private void updateSelfTestFormCode(String formIdAndType) {
		String formId = formIdAndType.split(",")[0];
		String typeId = formIdAndType.split(",")[1]; // generalDao.selectSingleString("Select TYPE_ID from
														// FG_S_SELFTEST_PIVOT where formId= "+formId +" and active=1");
		String formCode = formIdAndType.split(",")[2];
		String SelfTestTypeName = formDao.getFromInfoLookup("SELFTESTTYPE", LookupType.ID, typeId, "name");
		String updateFormCode = "SelfTest"; // default

		if (SelfTestTypeName.equals("Appearance")) {
			updateFormCode = "STestAppearance";
		} else if (SelfTestTypeName.equals("Density")) {
			updateFormCode = "STestDensity";
		} else if (SelfTestTypeName.equals("pH")) {
			updateFormCode = "STestpH";
		} else if (SelfTestTypeName.equals("Foaming")) {
			updateFormCode = "STestFoaming";
		} else if (SelfTestTypeName.equals("Flash Point")) {
			updateFormCode = "STestFlashPoint";
		} else if (SelfTestTypeName.equals("Wet Sieve")) {
			updateFormCode = "STestWetSieve";
		} else if (SelfTestTypeName.equals("Pourability")) {
			updateFormCode = "STestPourability";
		} else if (SelfTestTypeName.equals("Suspensibility")) {
			updateFormCode = "STestSuspensibilit";
		} else if (SelfTestTypeName.equals("Particle Size Distribution by Light Difraction")) {
			updateFormCode = "STestParticleSize";
		} else if (SelfTestTypeName.equals("Viscosity")) {
			updateFormCode = "STestViscosity";
		} else if (SelfTestTypeName.equals("Cold Test")) {
			updateFormCode = "STestCold";
		} else if (SelfTestTypeName.equals("Emulsion Stability")) {
			updateFormCode = "STestEmulsionStab";
		} else if (SelfTestTypeName.equals("SprayAbility")) {
			updateFormCode = "STestSprayAbility";
		}

		formSaveDao.updateStructTableFormCode("SelfTest", updateFormCode, formId,
				!generalUtil.getNull(formCode).equals(updateFormCode));
	}

	private void updateWorkupFormCode(String formIdAndType) {
		String formId = formIdAndType.split(",")[0];
		String typeId = formIdAndType.split(",")[1]; // generalDao.selectSingleString("Select WORKUPTYPE_ID from
														// FG_S_WORKUP_PIVOT where formId= "+formId+ " and active=1");
		String formCode = formIdAndType.split(",")[2];
		String workupTypeName = formDao.getFromInfoLookup("WORKUPTYPE", LookupType.ID, typeId, "name");
		String updateFormCode = "Workup"; // default

		if (workupTypeName.equals("Feeding")) {
			updateFormCode = "WorkupFeeding";
		} else if (workupTypeName.equals("Filtration")) {
			updateFormCode = "WorkupFiltration";
		} else if (workupTypeName.equals("Drying")) {
			updateFormCode = "WorkupDrying";
		} else if (workupTypeName.equals("Washing")) {
			updateFormCode = "WorkupWashExtract";
		} else if (workupTypeName.equals("Extraction")) {
			updateFormCode = "WorkupWashExtract";
		} else if (workupTypeName.equals("Distillation")) {
			updateFormCode = "WorkupDistillation";
		} else if (workupTypeName.equals("Crystallization")) {
			updateFormCode = "WorkupCrystallize";
		}

		formSaveDao.updateStructTableFormCode("Workup", updateFormCode, formId,
				!generalUtil.getNull(formCode).equals(updateFormCode));
	}

//	private String getResultTabEntities(String operator) {
//		List<String> entities = generalDao.getListOfStringBySql(
//				"select t.entityimpcode from fg_formentity t where t.formcode like 'ExperimentPr%' and"
//						+ " t.entityimpinit like '%layoutBookMarkItem\":\"%Results%\"additionalData\":true%'");
//
//		String wherePart = "";
//		if (entities != null && entities.size() > 0) {
//			for (String entityImpCode : entities) {
//				wherePart += " and entityimpcode " + operator + " '" + entityImpCode + "'";
//			}
//		}
//
//		return wherePart;
//	}

	private boolean isNumeric(String str) {
		try {
			Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}
	
	private String updateTablesByRunsPlanningData(String exprunplanningId, int isFirstRunForExperiment)
	{
		String toReturn = "1";
		boolean isFirstRun = (isFirstRunForExperiment == 1);
		// First update RUN to status 'Active' 
		String activeStatusId = formDao.getFromInfoLookup("RunStatus", LookupType.NAME, "Active", "id");
		String plannedStatusId = formDao.getFromInfoLookup("RunStatus", LookupType.NAME, "Planned", "id");
		String sql = "update FG_S_EXPRUNPLANNING_PIVOT set RUNSTATUSID = "+activeStatusId+" where FORMID = "+exprunplanningId+" and RUNSTATUSID = "+plannedStatusId+"";
		int updateRow = generalUtil.getNullInt(generalDao.updateSingleString(sql), -1);
		// throw error when there is no at least one row to update -> current run already changed from 'Planned' to 'Active' by concurrent thread
		if(updateRow <= 0)
		{
			return "-1";
		}
		
		//update MATERIALREF and PARAMREF tables
		String formIdField = (isFirstRun)?"formid":"originformid";
		
		// in case of first run, the Run Steps in status 'Active' already, meaning that each Step ParamRef table has planned and actual columns.
		// therefore in this case need to update parameters planned values only.
		//String paramValField = (isFirstRun)?"plannedval1":"val1";
		String paramValField = "val1";
		
		// get all data of currently started Run
		String runDataSql = "select t.formid, t.runnumber, t.stepid, t.materialrefid, t.materialratetype, t.materialrefvalue, t.materialrateuom \n"
							+" ,decode(t.LIMITINGAGENT,1,1, t.materialrefequivalent) as materialrefequivalent \n" 
							+" ,t.parameterrefid, t.parameterrefvalue, t.parameteruom, t.dencity, t.dencityuom \n"
							+" from fg_s_exprunplanning_pivot t \n" 
							+" where t.formid = '"+exprunplanningId+"' \n" 
							+" order by t.materialrefid, t.parameterrefid";
		List<Map<String, Object>> runData_mapForUpdate = generalDao.getListOfMapsBySql(runDataSql);
		for (Map<String, Object> runData : runData_mapForUpdate) 
		{
			String materialRefId = generalUtil.getEmpty((runData.get("materialrefid") != null?runData.get("materialrefid"):"").toString(),"");
			String parameterRefId = generalUtil.getEmpty((runData.get("parameterrefid") != null?runData.get("parameterrefid"):"").toString(),"");
			if(!materialRefId.equals(""))
			{
				String rateVal = generalUtil.getEmpty((runData.get("materialrefvalue") != null?runData.get("materialrefvalue"):"").toString(),"");
				String rateUom = generalUtil.getEmpty((runData.get("materialrateuom") != null?runData.get("materialrateuom"):"").toString(),"");
				String equivalent = generalUtil.getEmpty((runData.get("materialrefequivalent") != null?runData.get("materialrefequivalent"):"").toString(),"");
				String density = generalUtil.getEmpty((runData.get("dencity") != null?runData.get("dencity"):"").toString(),"");
				String densityUom = generalUtil.getEmpty((runData.get("dencityuom") != null?runData.get("dencityuom"):"").toString(),"");
				String rateType = (runData.get("materialratetype") != null?runData.get("materialratetype"):"").toString();
				String matRefField = "volumerate";
				String matRefFieldUOM = "volrateuom_id";
				switch(rateType) {
					case "quantity":
						matRefField = "quantityrate";
						matRefFieldUOM = "quantityrate_uom";
						break;
					case "mole":
						matRefField = "molerate";
						matRefFieldUOM = "molerateuom_id";
						break;
				}
				String sqlUpdate = "update fg_s_materialref_pivot \n"
								 +" set "+matRefField+" = '"+rateVal+"', \n"
								 +" 	"+matRefFieldUOM+" = '"+rateUom+"', \n"
								 +" 	densityinf = '"+density+"', \n"
							     +" 	DENSITYUOM_ID_INF = '"+densityUom+"', \n"
								 +" 	equivalent = '"+equivalent+"' \n"
								 +" where "+formIdField+" = "+materialRefId+"";
				formSaveDao.updateSingleStringInfoNoTryCatch(sqlUpdate); 
				
				if(isFirstRun)
				{
					// in case of first run, the Run Steps in status 'Active' already, meaning that each Step MaterialRef table has Snapshot table.
					// therefore need to update snapshot(planned) values also.
					String sqlUpdateSnapshot = "update FG_S_MATERIALREF_ALL_V_PLAN \n"
							 +" set "+matRefField+" = '"+rateVal+"', \n"
							 +" 	"+matRefFieldUOM+" = '"+rateUom+"', \n"
							 +" 	densityinf = '"+density+"', \n"
							 +" 	DENSITYUOM_ID_INF = '"+densityUom+"', \n"
							 +" 	equivalent = '"+equivalent+"' \n"
							 +" where formid = "+materialRefId+"";
					formSaveDao.updateSingleStringInfoNoTryCatch(sqlUpdateSnapshot); 
				}
			}
			else if(!parameterRefId.equals(""))
			{
				String paramVal = generalUtil.getEmpty((runData.get("parameterrefvalue") != null?runData.get("parameterrefvalue"):"").toString(),"");
				String paramUom = generalUtil.getEmpty((runData.get("parameteruom") != null?runData.get("parameteruom"):"").toString(),"");
				
				String sqlUpdate = "update fg_s_paramref_pivot \n"
						 +" set "+paramValField+" = '"+paramVal+"', \n"
						 +" 	uom_id = '"+paramUom+"' \n"
						 +" where "+formIdField+" = "+parameterRefId+"";
				formSaveDao.updateSingleStringInfoNoTryCatch(sqlUpdate); 
			}
		}
		
		// UPDATE STEP TABLE by Retention Time data
		String retentionTimeSql = "select distinct t.stepid, t.retentiontime, t.retentiontimeuom \n"
								+ "from fg_s_exprunplanning_pivot t \n"
								+ "where t.formid = '"+exprunplanningId+"'";
		List<Map<String, Object>> mapForUpdate = generalDao.getListOfMapsBySql(retentionTimeSql);
		for (Map<String, Object> data : mapForUpdate) 
		{
			String stepId = (data.get("stepid") != null?data.get("stepid"):"").toString();
			String val = generalUtil.getEmpty((data.get("retentiontime") != null?data.get("retentiontime"):"").toString(),"");
			String uom = generalUtil.getEmpty((data.get("retentiontimeuom") != null?data.get("retentiontimeuom"):"").toString(),"");
			
			String sqlUpdate = "update FG_S_STEP_PIVOT \n"
					 +" set retentiontime = '"+val+"', \n"
					 +" 	retentiontimeuom_id = '"+uom+"' \n"
					 +" where "+formIdField+" = '"+stepId+"'";
			formSaveDao.updateSingleStringInfoNoTryCatch(sqlUpdate); 
		}
		
		return toReturn;
	}
	
	private Map<String,String> updateNewRunStepsStatusToActive(String experimentId, String runNumber, String userId) throws Exception
	{
		Map<String,String> toReturn = commonFunc.updateStepsStatusToActive(experimentId, runNumber, userId,true);
		//fixed bug 8278 - Conc.% of previous step should be copied to Purity of the Reactant
		/*for(String stepId:listOfPlannedRunSteps) {
			String stepNumber = formDao.getFromInfoLookup("Step", LookupType.ID, stepId, "FORMNUMBERID");
			String sql = "select formid from fg_s_step_v where experiment_id = '" + experimentId + "'"
					+ " and formnumberid = (select distinct max(formnumberId)"
					+ " from fg_s_step_v where experiment_id = '" + experimentId + "'" + " and formnumberid < '"
					+ stepNumber + "'"
					+") and runNumber = '" + runNumber+"' and preparation_run = 'Run'";//gets the previous step
			String prevStepFormId = generalDao.selectSingleStringNoException(sql);
			if (!prevStepFormId.isEmpty()) {//if it's empty, then it is the first step and there's no need to execute the copy
				List<String> materialref_id_list = generalDao.getListOfStringBySql("select distinct formId"
						+ " from fg_s_materialref_v m" + " where m.PARENTID in ("
						+ stepId + ")" + " and nvl(active,'1')='1' and sessionid is null and tableType = 'Reactant'");
				for (String materilalRef_formId : materialref_id_list) {
					String material_id = formDao.getFromInfoLookup("MaterialRef", LookupType.ID, materilalRef_formId, "INVITEMMATERIAL_ID");
					String concentration = generalDao.selectSingleStringNoException("select distinct concinreactionmass from fg_s_materialref_v t where t.INVITEMMATERIAL_ID = '"+material_id+"' and t.PARENTID = '"+prevStepFormId+"' and t.tabletype = 'Product' and nvl(active,'1') = '1' and sessionid is null and rownum<=1");
		    	    if(!generalUtil.getNull(concentration).isEmpty()){
		    	    	sql = "update fg_s_materialref_pivot set PURITYINF = '"+concentration+"', PURITYUOM_ID_INF = '"+generalUtilForm.getDefaultValueOfElement("MaterialRef", "PURITY_UOM_ID")+"' where formid = '" + materilalRef_formId
								+ "'";
						formSaveDao.updateStructTableByFormId(sql, "fg_s_materialref_pivot", Arrays.asList("PURITYINF", "PURITYUOM_ID_INF"), materilalRef_formId);
		    	    }
				}
			}
			
		}*/
		return toReturn;
	}
	
	private void insertRun(String sourceExperimentId, String experimentCloneId, String userId, CloneType cloneType, String templateId) {
		
		StringBuilder sqlBuilder = new StringBuilder();
		String formCode = "ExpRunPlanning";
		
		String runStatusId = formDao.getFromInfoLookup("RunStatus", LookupType.NAME, "Planned", "id");
		String runNumberSql = "";
		int newRunNumber = 1;
		boolean isCopyFullData = (cloneType.equals(CloneType.TO_TEMPLATE)||cloneType.equals(CloneType.FROM_TEMPLATE))?true:false;
		if (cloneType.equals(CloneType.GENERAL)) 
		{			
			runNumberSql = "select min(t1.runnumber) as first_run \n"
					+ " from fg_s_exprunplanning_pivot t1 \n"
					+ " where t1.experimentid = "+sourceExperimentId;
		}
		else if (cloneType.equals(CloneType.TO_TEMPLATE)) 
		{			
			runNumberSql = "select distinct t1.runnumber \n"
					+ " from fg_s_exprunplanning_pivot t1 \n"
					+ " where nvl(t1.copytotemplate,0) = 1 \n"
					+ " and t1.experimentid = "+sourceExperimentId+" \n"
					+ " order by to_number(t1.runnumber)";
		}
		else if(cloneType.equals(CloneType.FROM_TEMPLATE)){
			runNumberSql = "select distinct t1.runnumber \n"
					+ " from fg_s_exprunplanning_pivot t1 \n"
					+ " where instr(','||(select expDesign from fg_s_template_v where template_id = '"+templateId+"')||',',','||t1.formid||',')>0\n"
					+ " order by to_number(t1.runnumber)";
		}
		
		List<String> runNumberList = generalDao.getListOfStringBySql(runNumberSql);
		for(String runNumberToCopyFrom:runNumberList) {		
		
			sqlBuilder = new StringBuilder();
			String newformId = formSaveDao.getStructFormId(formCode);
			sqlBuilder.append("insert into FG_S_EXPRUNPLANNING_PIVOT ")
					.append("(FORMID, TIMESTAMP, CREATION_DATE, CHANGE_BY, CREATED_BY, ACTIVE, FORMCODE_ENTITY, FORMCODE, MATERIALREFID, PARAMETERREFID, STEPID, EXPERIMENTID, RUNNUMBER, RUNSTATUSID,")
					.append(" INVITEMMATERIALID, PARAMETERID, LIMITINGAGENT, MATERIALRATETYPE, MATERIALRATEUOM, PARAMETERUOM, RETENTIONTIMEUOM")
					.append(((isCopyFullData)?", MATERIALREFVALUE, MATERIALREFEQUIVALENT, RETENTIONTIME, PARAMETERREFVALUE":""))
					.append(") \n")
					.append(" select distinct "+newformId+", sysdate, sysdate, '" + userId + "', '" + userId + "', '1' ,'"+formCode+"', '"+formCode+"' \n")
					.append(" 		,m.MATERIALREF_ID, m.PARAMREF_ID, m.STEP_ID, " + experimentCloneId + ", "+newRunNumber+" \n")
					.append( " 		, "+runStatusId+" ,m.INVITEMMATERIAL_ID, m.PARAMETER_ID, m.LIMITINGAGENT \n")
					.append( "			,m.MATERIALRATETYPE ,m.MATERIALRATEUOM ,m.PARAMETERUOM ,m.RETENTIONTIMEUOM \n")
					.append( ((isCopyFullData)?", m.MATERIALREFVALUE, m.MATERIALREFEQUIVALENT, m.RETENTIONTIME, m.PARAMETERREFVALUE \n":""))
					.append( " from ( \n")
					.append( " 	 	select distinct s.STEP_ID,  s.EXPERIMENT_ID ,t.MATERIALREF_ID \n")
					.append( " 			     ,t.INVITEMMATERIAL_ID, nvl(t.LIMITINGAGENT,0) as LIMITINGAGENT  \n")
					.append( " 			     ,r.materialratetype, r.materialrateuom, r.retentiontimeuom \n")
					.append( " 			      , null as PARAMREF_ID, null as PARAMETER_ID, null as PARAMETERUOM \n")
					.append( ((isCopyFullData)?", r.materialrefvalue, r.materialrefequivalent, r.retentiontime, null as PARAMETERREFVALUE \n":""))
					.append( " 		from fg_s_step_v s, fg_s_materialref_v t  \n")
					.append( " 			  ,(select distinct t2.materialrefid, t2.materialratetype, t2.materialrateuom,t2.retentiontimeuom \n")
					.append( ((isCopyFullData)?", t2.materialrefvalue, t2.materialrefequivalent, t2.retentiontime \n":""))
					.append( " 			      from fg_s_exprunplanning_pivot t2 \n")
					.append( " 			      where t2.runnumber in ('"+runNumberToCopyFrom+"') \n")
					.append(" 			      and t2.experimentid = "+sourceExperimentId+"")
					.append("                )r \n")
					.append(" 		where t.PARENTID = s.STEP_ID  \n")
					.append(" 		and t.ORIGINFORMID = r.MATERIALREFID \n")
					.append(" 		and t.ACTIVE = 1  \n")
					.append(" 		and s.ACTIVE = 1 \n")
					.append(" 		and t.sessionId is null \n" )
					.append(" 		and lower(t.TABLETYPE) in ('reactant','solvent') \n" )
					.append(" 		and lower(nvl(s.PREPARATION_RUN,'na')) = lower('run') \n" )
					.append(" 		and s.EXPERIMENT_ID = "+experimentCloneId+" \n")
					.append(" 		union all \n" )
					.append(" 		select s1.STEP_ID, s1.EXPERIMENT_ID, null as MATERIALREF_ID \n")
					.append(" 			     , null as INVITEMMATERIAL_ID, null as LIMITINGAGENT \n")
					.append(" 			     , null as materialratetype, null as materialrateuom, r.retentiontimeuom \n")
					.append(" 			     , t1.PARAMREF_ID , t1.PARAMETER_ID, r.parameteruom  \n")
					.append(((isCopyFullData)?", null as materialrefvalue, null as materialrefequivalent, r.retentiontime, r.parameterrefvalue \n":""))
					.append(" 		from fg_s_step_v s1, fg_s_paramref_v t1  \n")
					.append(" 			 ,(select distinct t2.parameterrefid, t2.parameteruom, t2.retentiontimeuom \n")
					.append(((isCopyFullData)?", t2.parameterrefvalue, t2.retentiontime \n":""))
					.append(" 			      from fg_s_exprunplanning_pivot t2 \n")
					.append(" 			      where t2.runnumber in ('"+runNumberToCopyFrom+"') \n")
					.append(" 			      and t2.experimentid = "+sourceExperimentId+"")
					.append("               )r \n")
					.append(" 		where t1.PARENTID = s1.STEP_ID  \n")
					.append(" 		and t1.ORIGINFORMID = r.PARAMETERREFID \n" )
					.append(" 		and t1.ACTIVE = 1  \n")
					.append(" 		and s1.ACTIVE = 1 \n")
					.append(" 		and t1.sessionId is null \n" )
					.append(" 		and lower(nvl(s1.PREPARATION_RUN,'na')) = lower('run') \n" )
					.append(" 		and s1.EXPERIMENT_ID = "+experimentCloneId+"   \n")
					.append(" ) m ");

					formSaveDao.insertStructTableByFormId(sqlBuilder.toString(), "FG_S_EXPRUNPLANNING_PIVOT", newformId);
					newRunNumber++;					
		}
	}

	private Map<String, String> setColumnDataToTemplate(String userId) {
		if(columnDataToTemplate != null && holdExperimentCloneMap == 1 && userId != null && userId.equals(generalUtil.getNull(userIdHolder))) {
			return columnDataToTemplate;
		}
		userIdHolder = userId;
		Map<String, String> columnDataToTemplate_ = new HashMap<String, String>();
		boolean fromTemplate = true;

		// Experiment
		columnDataToTemplate_.put("Experiment.TEMPLATEFLAG", (fromTemplate) ? "1" : "NULL");
		// String statusId = formDao.getFromInfoLookup("ExperimentStatus",
		// LookupType.NAME, "Planned", "id");
		columnDataToTemplate_.put("Experiment.STATUS_ID", (fromTemplate) ? "NULL" : "NULL");
		columnDataToTemplate_.put("Experiment.LASTSTATUS_ID","NULL");
		columnDataToTemplate_.put("Experiment.experimentName", (fromTemplate) ? "experimentName" : "NULL");
		columnDataToTemplate_.put("Experiment.formNumberId", (fromTemplate) ? "formNumberId" : "NULL");
		columnDataToTemplate_.put("Experiment.experimentVersion", (fromTemplate) ? "experimentVersion" : "NULL");
		columnDataToTemplate_.put("Experiment.Laboratory_id", (fromTemplate) ? "Laboratory_id" : "NULL");
		columnDataToTemplate_.put("Experiment.ExperimentSeries", "NULL");
		columnDataToTemplate_.put("Experiment.ExperimentGroup", "NULL");
		columnDataToTemplate_.put("Experiment.CREATIONDATETIME", "NULL");
		columnDataToTemplate_.put("Experiment.EstimatedStartDate", "NULL");
		columnDataToTemplate_.put("Experiment.ActualStartDate", "NULL");
		columnDataToTemplate_.put("Experiment.ACTUALSTARTTIMESTAMP", "NULL");
		columnDataToTemplate_.put("Experiment.APPROVALTIMESTAMP", "NULL");
		columnDataToTemplate_.put("Experiment.COMPLETIONTIMESTAMP", "NULL");
		columnDataToTemplate_.put("Experiment.CompletionDate", "NULL");
		columnDataToTemplate_.put("Experiment.ApprovalDate", "NULL");
		columnDataToTemplate_.put("Experiment.LASTMODIFDATE", "NULL");
		columnDataToTemplate_.put("Experiment.Creator_id", "'" + userId + "'");
		columnDataToTemplate_.put("Experiment.Owner_id", "'" + userId + "'");
		columnDataToTemplate_.put("Experiment.Approver_id", "NULL");
		columnDataToTemplate_.put("Experiment.TEMPLATENAME", (fromTemplate) ? "NULL" : "TEMPLATENAME");
		// columnData.put("Experiment.Description", "NULL");
		columnDataToTemplate_.put("Experiment.CONCLUSSION", (fromTemplate) ? "CONCLUSSION" : "NULL");
		// columnData.put("Experiment.SAFETYCOMMENTS", "SAFETYCOMMENTS");
		columnDataToTemplate_.put("Experiment.PLANNED_ACTUAL", (fromTemplate) ? "NULL" : "NULL");// adib changed from actual to
		columnDataToTemplate_.put("Experiment.spreadsheetExcel", "(select spreadsheet from fg_s_spreadsheettempla_v where formid = SPREADSHEETTEMPLATE_ID)");

		
		/*//calculation tab
		columnDataToTemplate_.put("Experiment.compVolumeRate", "NULL");
		columnDataToTemplate_.put("Experiment.actCompVolumeRate", "NULL");
		columnDataToTemplate_.put("Experiment.COMPVOLUMERATE_UOM_ID", "NULL");
		columnDataToTemplate_.put("Experiment.compMassRate", "NULL");
		columnDataToTemplate_.put("Experiment.actCompMassRate", "NULL");
		columnDataToTemplate_.put("Experiment.COMPMASSRATE_UOM_ID", "NULL");
		columnDataToTemplate_.put("Experiment.retentionTime", "NULL");
		columnDataToTemplate_.put("Experiment.retentionTimeActual", "NULL");
		columnDataToTemplate_.put("Experiment.RETENTIONTIMEUOM_ID", "NULL");
		*/
		// null
		columnDataToTemplate_.put("Experiment.WEBIXANALYTTABLE", "NULL");
		// Mass Balance Tab in Organic
		columnDataToTemplate_.put("Experiment.WEBIXMASSBALANCETABLE", "NULL");
		columnDataToTemplate_.put("Experiment.WEBIXMASSBALANCETABLE2", "NULL");
		columnDataToTemplate_.put("Experiment.WEBIXMASSBALANCETABLE3", "NULL");

		// Step
		columnDataToTemplate_.put("Step.TEMPLATEFLAG", (fromTemplate) ? "1" : "NULL");
		String statusId = formDao.getFromInfoLookup("StepStatus", LookupType.NAME, "Planned", "id");
		columnDataToTemplate_.put("Step.Status_ID", (fromTemplate) ? "NULL" : "'" + statusId + "'");
		// columnData.put("Step.Status_ID", "NULL" );
		columnDataToTemplate_.put("Step.SHORTDESCRIPTION", (fromTemplate) ? "SHORTDESCRIPTION" : "NULL");
		columnDataToTemplate_.put("Step.ExperimentName", (fromTemplate) ? "ExperimentName" : "NULL");
		columnDataToTemplate_.put("Step.ExperimentVersion", (fromTemplate) ? "ExperimentVersion" : "NULL");
		columnDataToTemplate_.put("Step.CreationDateTime", "NULL");
		columnDataToTemplate_.put("Step.ESTIMSTARTDATE", "NULL");
		columnDataToTemplate_.put("Step.ACTUALSTARTDATE", "NULL");
		columnDataToTemplate_.put("Step.FINISHDATE", "NULL");
		columnDataToTemplate_.put("Step.CREATOR_ID", "NULL");
		columnDataToTemplate_.put("Step.REACTSTARTTIME", "NULL");
		columnDataToTemplate_.put("Step.REACTFINISHTIME", "NULL");
		columnDataToTemplate_.put("Step.CONCLUSSION", (fromTemplate) ? "CONCLUSSION" : "NULL");
		columnDataToTemplate_.put("Step.PLANNED_ACTUAL", (fromTemplate) ? "NULL" : "NULL");// adib changed from actual to null
		columnDataToTemplate_.put("Step.webixMassBalanceTable", "NULL");
		columnDataToTemplate_.put("Step.summary", "NULL");
		columnDataToTemplate_.put("Step.conversion", "NULL");
		columnDataToTemplate_.put("Step.chemicalYield", "NULL");
		columnDataToTemplate_.put("Step.isolatedYield", "NULL");
		columnDataToTemplate_.put("Step.limitingAgentMole", "NULL");
		columnDataToTemplate_.put("Step.chkManualUpdate", "NULL");
		columnDataToTemplate_.put("Step.chkCharacterMassBalance", "NULL");
		columnDataToTemplate_.put("Step.samplingTime", "NULL");
		columnDataToTemplate_.put("Step.SAMPLINGTIMEUOM_ID", "NULL");
		columnDataToTemplate_.put("Step.retentionTime", "NULL");
		columnDataToTemplate_.put("Step.retentionTimeActual", "NULL");
		columnDataToTemplate_.put("Step.RETENTIONTIMEUOM_ID", "NULL");
		columnDataToTemplate_.put("Step.CLONERUNFLAG", "'0'"); // IMPORTANT!!!!!
		columnDataToTemplate_.put("Step.RUNNUMBER", "NULL"); // IMPORTANT!!!!!

		// Action
		columnDataToTemplate_.put("Action.TEMPLATEFLAG", (fromTemplate) ? "1" : "NULL");
		columnDataToTemplate_.put("Action.SETBEFORE_ID", "NULL");
		columnDataToTemplate_.put("Action.Observation", "NULL");
		columnDataToTemplate_.put("Action.STARTTIME", "NULL");
		columnDataToTemplate_.put("Action.ENDTIME", "NULL");
		columnDataToTemplate_.put("Action.startDate", (fromTemplate) ? "startDate" : "NULL");
		columnDataToTemplate_.put("Action.endDate", (fromTemplate) ? "endDate" : "NULL");
		columnDataToTemplate_.put("Action.startTime", (fromTemplate) ? "startTime" : "NULL");
		columnDataToTemplate_.put("Action.endTime", (fromTemplate) ? "endTime" : "NULL");

		// SeltTest
		columnDataToTemplate_.put("SelfTest.STATUS_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.TEMPLATEFLAG", (fromTemplate) ? "1" : "NULL");
		columnDataToTemplate_.put("SelfTest.Site_id", (fromTemplate) ? "Site_id" : "NULL");
		columnDataToTemplate_.put("SelfTest.Unit_id", (fromTemplate) ? "Unit_id" : "NULL");
		columnDataToTemplate_.put("SelfTest.LABORATORY_ID", (fromTemplate) ? "LABORATORY_ID" : "NULL");
		columnDataToTemplate_.put("SelfTest.SAMPLENUMBER", "NULL");
		columnDataToTemplate_.put("SelfTest.SAMPLEID", "NULL");
		columnDataToTemplate_.put("SelfTest.CREATIONDATE", "NULL");
		columnDataToTemplate_.put("SelfTest.STARTDATE", "NULL");
		columnDataToTemplate_.put("SelfTest.FINISHDATE", "NULL");
		columnDataToTemplate_.put("SelfTest.SUMMARY", (fromTemplate) ? "SUMMARY" : "NULL");
		columnDataToTemplate_.put("SelfTest.TESTCOMMENT", (fromTemplate) ? "TESTCOMMENT" : "NULL");
		columnDataToTemplate_.put("SelfTest.color", "NULL");
		columnDataToTemplate_.put("SelfTest.odor", "NULL");
		columnDataToTemplate_.put("SelfTest.clarity", "NULL");
		columnDataToTemplate_.put("SelfTest.creaming", "NULL");
		columnDataToTemplate_.put("SelfTest.freezing", "NULL");
		columnDataToTemplate_.put("SelfTest.phaseSeparation", "NULL");
		columnDataToTemplate_.put("SelfTest.crystallization", "NULL");
		columnDataToTemplate_.put("SelfTest.residue", "NULL");
		columnDataToTemplate_.put("SelfTest.PHASESEPARUOM_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.CRYSTALLIZUOM_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.RESIDUEUOM_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.DENSITYTYPE_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.INSTRUMENTTYPE_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.dilutionFactor", "NULL");
		columnDataToTemplate_.put("SelfTest.result", "NULL");
		columnDataToTemplate_.put("SelfTest.RESULTUOM_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.calculationRes", "NULL");
		columnDataToTemplate_.put("SelfTest.CALCULATIONRESUOM_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.WATERTYPE_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.resultZero", "NULL");
		columnDataToTemplate_.put("SelfTest.RESULTZEROUOM_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.resultMinute", "NULL");
		columnDataToTemplate_.put("SelfTest.RESULTMINUTEUOM_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.temperature", "NULL");
		columnDataToTemplate_.put("SelfTest.TEMPERATUREUOM_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.flashPointTemperature", "NULL");
		columnDataToTemplate_.put("SelfTest.FlASHPOINTTEMPUOM_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.WATERTYPE_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.residue", "NULL");
		columnDataToTemplate_.put("SelfTest.RESIDUEUOM_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.WetSieve", "NULL");
		columnDataToTemplate_.put("SelfTest.WETSIEVEUOM_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.wetSieveMass", "NULL");
		columnDataToTemplate_.put("SelfTest.WSMASSUOM", "NULL");
		columnDataToTemplate_.put("SelfTest.startTime", "NULL");
		columnDataToTemplate_.put("SelfTest.finishTime", "NULL");
		columnDataToTemplate_.put("SelfTest.cylinderWeight", "NULL");
		columnDataToTemplate_.put("SelfTest.CYLINDERWEGHTUOM_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.weightBStorage", "NULL");
		columnDataToTemplate_.put("SelfTest.WEIGHTBSTORAGEUOM_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.weightAStorage", "NULL");
		columnDataToTemplate_.put("SelfTest.WEIGHTASTORAGEUOM_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.weightARinsing", "NULL");
		columnDataToTemplate_.put("SelfTest.WEIGHTARINSINGUOM_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.resultAFeeding", "NULL");
		columnDataToTemplate_.put("SelfTest.RESULTAFEEDUOM_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.resultAWashing", "NULL");
		columnDataToTemplate_.put("SelfTest.RESULTAWASHUOM_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.INSTRUMENTTYPE_ID", "NULL");
		// yp removed from DB 15032018 - >
		// columnData.put("SelfTest.usedPercent", "NULL");
		// columnData.put("SelfTest.USEDPERCENTUOM_ID", "NULL");
		// columnData.put("SelfTest.Mass25ml", "NULL");
		// columnData.put("SelfTest.MASS25MLUOM_ID", "NULL");
		// columnData.put("SelfTest.massFrActuallyCylinder", "NULL");
		// columnData.put("SelfTest.MASSFRACTCYLINDERUOM_ID", "NULL");
		//
		columnDataToTemplate_.put("SelfTest.result", "NULL");
		columnDataToTemplate_.put("SelfTest.RESULTUOM_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.INSTRUMENTTYPE_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.microscope", "NULL");
		columnDataToTemplate_.put("SelfTest.result", "NULL");
		columnDataToTemplate_.put("SelfTest.RESULTUOM_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.spindle", "NULL");
		columnDataToTemplate_.put("SelfTest.RPM", "NULL");
		columnDataToTemplate_.put("SelfTest.RPMUOM_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.result", "NULL");
		columnDataToTemplate_.put("SelfTest.RESULTUOM_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.crystal", "NULL");
		columnDataToTemplate_.put("SelfTest.freezing", "NULL");
		columnDataToTemplate_.put("SelfTest.crystallization", "NULL");
		columnDataToTemplate_.put("SelfTest.CRYSTALLIZUOM_ID", "NULL");
		columnDataToTemplate_.put("SelfTest.glassware", "NULL");
		columnDataToTemplate_.put("SelfTest.initialEmulsification", "NULL");
		columnDataToTemplate_.put("SelfTest.concentration", "NULL");
		columnDataToTemplate_.put("SelfTest.conical", "NULL");
		columnDataToTemplate_.put("SelfTest.CONCENTRATIONUOM_ID", "NULL");

		// invitemcolumn
		columnDataToTemplate_.put("InvItemColumn.serialNumber", "NULL");
		columnDataToTemplate_.put("InvItemColumn.batchNumber", "NULL");
		columnDataToTemplate_.put("InvItemColumn.inUse_disabled", "\'In Use\'");
		
		//composition
		columnDataToTemplate_.put("composition.ACTUAL", "NULL");
		columnDataToTemplate_.put("composition.DELTA", "NULL");
		
		/////////////////////
		// hold origin from  id
		/////////////////////
		columnDataToTemplate_.put("MaterialRef.ORIGINFORMID", "FORMID");
		columnDataToTemplate_.put("ParamRef.ORIGINFORMID", "FORMID");
		columnDataToTemplate_.put("Step.ORIGINFORMID", "FORMID");

		//update and return class map
		columnDataToTemplate = new HashMap<String,String>();
		columnDataToTemplate.putAll(columnDataToTemplate_);
		return columnDataToTemplate;
	}

	private Map<String, String> setColumnDataFromTemplate(String userId) {
		if(columnDataFromTemplate != null && holdExperimentCloneMap == 1 && userId != null && userId.equals(generalUtil.getNull(userIdHolder))) {
			return columnDataFromTemplate;
		}
		userIdHolder = userId;
		Map<String, String> columnDataFromTemplate_ = new HashMap<String, String>();
		boolean fromTemplate = false;

		// Experiment
		columnDataFromTemplate_.put("Experiment.TEMPLATEFLAG", (fromTemplate) ? "1" : "NULL");
		// String statusId = formDao.getFromInfoLookup("ExperimentStatus",
		// LookupType.NAME, "Planned", "id");
		columnDataFromTemplate_.put("Experiment.STATUS_ID", (fromTemplate) ? "NULL" : "NULL");
		columnDataFromTemplate_.put("Experiment.LASTSTATUS_ID","NULL");
		columnDataFromTemplate_.put("Experiment.experimentName", (fromTemplate) ? "experimentName" : "NULL");
		columnDataFromTemplate_.put("Experiment.formNumberId", (fromTemplate) ? "formNumberId" : "NULL");
		columnDataFromTemplate_.put("Experiment.experimentVersion", (fromTemplate) ? "experimentVersion" : "NULL");
		columnDataFromTemplate_.put("Experiment.Laboratory_id", (fromTemplate) ? "Laboratory_id" : "NULL");
		columnDataFromTemplate_.put("Experiment.ExperimentSeries", "NULL");
		columnDataFromTemplate_.put("Experiment.ExperimentGroup", "NULL");
		columnDataFromTemplate_.put("Experiment.CREATIONDATETIME", "NULL");
		columnDataFromTemplate_.put("Experiment.EstimatedStartDate", "NULL");
		columnDataFromTemplate_.put("Experiment.ActualStartDate", "NULL");
		columnDataFromTemplate_.put("Experiment.ACTUALSTARTTIMESTAMP", "NULL");
		columnDataFromTemplate_.put("Experiment.APPROVALTIMESTAMP", "NULL");
		columnDataFromTemplate_.put("Experiment.COMPLETIONTIMESTAMP", "NULL");
		columnDataFromTemplate_.put("Experiment.CompletionDate", "NULL");
		columnDataFromTemplate_.put("Experiment.ApprovalDate", "NULL");
		columnDataFromTemplate_.put("Experiment.LASTMODIFDATE", "NULL");
		columnDataFromTemplate_.put("Experiment.Creator_id", "'" + userId + "'");
		columnDataFromTemplate_.put("Experiment.Owner_id", "'" + userId + "'");
		columnDataFromTemplate_.put("Experiment.Approver_id", "NULL");
		columnDataFromTemplate_.put("Experiment.TEMPLATENAME", (fromTemplate) ? "NULL" : "TEMPLATENAME");
		// columnData.put("Experiment.Description", "NULL");
		columnDataFromTemplate_.put("Experiment.CONCLUSSION", (fromTemplate) ? "CONCLUSSION" : "NULL");
		// columnData.put("Experiment.SAFETYCOMMENTS", "SAFETYCOMMENTS");
		columnDataFromTemplate_.put("Experiment.PLANNED_ACTUAL", (fromTemplate) ? "NULL" : "NULL");// adib changed from actual to
		
		/*//calculation tab
		columnDataFromTemplate_.put("Experiment.compVolumeRate", "NULL");
		columnDataFromTemplate_.put("Experiment.actCompVolumeRate", "NULL");
		columnDataFromTemplate_.put("Experiment.COMPVOLUMERATE_UOM_ID", "NULL");
		columnDataFromTemplate_.put("Experiment.compMassRate", "NULL");
		columnDataFromTemplate_.put("Experiment.actCompMassRate", "NULL");
		columnDataFromTemplate_.put("Experiment.COMPMASSRATE_UOM_ID", "NULL");
		columnDataFromTemplate_.put("Experiment.retentionTime", "NULL");
		columnDataFromTemplate_.put("Experiment.retentionTimeActual", "NULL");
		columnDataFromTemplate_.put("Experiment.RETENTIONTIMEUOM_ID", "NULL");
		*/
		// null
		columnDataFromTemplate_.put("Experiment.WEBIXANALYTTABLE", "NULL");
		// Mass Balance Tab in Organic
		columnDataFromTemplate_.put("Experiment.WEBIXMASSBALANCETABLE", "NULL");
		columnDataFromTemplate_.put("Experiment.WEBIXMASSBALANCETABLE2", "NULL");
		columnDataFromTemplate_.put("Experiment.WEBIXMASSBALANCETABLE3", "NULL");

		// Step
		columnDataFromTemplate_.put("Step.TEMPLATEFLAG", (fromTemplate) ? "1" : "NULL");
		String statusId = formDao.getFromInfoLookup("StepStatus", LookupType.NAME, "Planned", "id");
		columnDataFromTemplate_.put("Step.Status_ID", (fromTemplate) ? "NULL" : "'" + statusId + "'");
		// columnData.put("Step.Status_ID", "NULL" );
		columnDataFromTemplate_.put("Step.SHORTDESCRIPTION", (fromTemplate) ? "SHORTDESCRIPTION" : "NULL");
		columnDataFromTemplate_.put("Step.ExperimentName", (fromTemplate) ? "ExperimentName" : "NULL");
		columnDataFromTemplate_.put("Step.ExperimentVersion", (fromTemplate) ? "ExperimentVersion" : "NULL");
		columnDataFromTemplate_.put("Step.CreationDateTime", "NULL");
		columnDataFromTemplate_.put("Step.ESTIMSTARTDATE", "NULL");
		columnDataFromTemplate_.put("Step.ACTUALSTARTDATE", "NULL");
		columnDataFromTemplate_.put("Step.FINISHDATE", "NULL");
		columnDataFromTemplate_.put("Step.CREATOR_ID", "NULL");
		columnDataFromTemplate_.put("Step.REACTSTARTTIME", "NULL");
		columnDataFromTemplate_.put("Step.REACTFINISHTIME", "NULL");
		columnDataFromTemplate_.put("Step.CONCLUSSION", (fromTemplate) ? "CONCLUSSION" : "NULL");
		columnDataFromTemplate_.put("Step.PLANNED_ACTUAL", (fromTemplate) ? "NULL" : "NULL");// adib changed from actual to null
		columnDataFromTemplate_.put("Step.webixMassBalanceTable", "NULL");
		columnDataFromTemplate_.put("Step.summary", "NULL");
		columnDataFromTemplate_.put("Step.conversion", "NULL");
		columnDataFromTemplate_.put("Step.chemicalYield", "NULL");
		columnDataFromTemplate_.put("Step.isolatedYield", "NULL");
		columnDataFromTemplate_.put("Step.limitingAgentMole", "NULL");
		columnDataFromTemplate_.put("Step.chkManualUpdate", "NULL");
		columnDataFromTemplate_.put("Step.chkCharacterMassBalance", "NULL");
		columnDataFromTemplate_.put("Step.CLONERUNFLAG", "'0'"); // IMPORTANT!!!!!
		columnDataFromTemplate_.put("Step.RUNNUMBER", "NULL"); // IMPORTANT!!!!!
		// Action
		columnDataFromTemplate_.put("Action.TEMPLATEFLAG", (fromTemplate) ? "1" : "NULL");
		columnDataFromTemplate_.put("Action.SETBEFORE_ID", "NULL");
		columnDataFromTemplate_.put("Action.Observation", "NULL");
		columnDataFromTemplate_.put("Action.STARTTIME", "NULL");
		columnDataFromTemplate_.put("Action.ENDTIME", "NULL");
		columnDataFromTemplate_.put("Action.startDate", (fromTemplate) ? "startDate" : "NULL");
		columnDataFromTemplate_.put("Action.endDate", (fromTemplate) ? "endDate" : "NULL");
		columnDataFromTemplate_.put("Action.startTime", (fromTemplate) ? "startTime" : "NULL");
		columnDataFromTemplate_.put("Action.endTime", (fromTemplate) ? "endTime" : "NULL");

		// invitemcolumn
		columnDataFromTemplate_.put("InvItemColumn.serialNumber", "NULL");
		columnDataFromTemplate_.put("InvItemColumn.batchNumber", "NULL");
		columnDataFromTemplate_.put("InvItemColumn.inUse_disabled", "\'In Use\'");
		
		//composition
		columnDataFromTemplate_.put("composition.ACTUAL", "NULL");
		columnDataFromTemplate_.put("composition.DELTA", "NULL");
		
		/////////////////////
		// hold origin from  id
		/////////////////////
		columnDataFromTemplate_.put("MaterialRef.ORIGINFORMID", "FORMID");
		columnDataFromTemplate_.put("ParamRef.ORIGINFORMID", "FORMID");
		columnDataFromTemplate_.put("Step.ORIGINFORMID", "FORMID");


		//update and return class map
		columnDataFromTemplate = new HashMap<String,String>();
		columnDataFromTemplate.putAll(columnDataFromTemplate_);
		return columnDataFromTemplate;
	}

	private Map<String, String> setColumnDataToCloneFromExperiment(String userId) {
		if(columnDataFromExperiment != null && holdExperimentCloneMap == 1 && userId != null && userId.equals(generalUtil.getNull(userIdHolder))) {
			return columnDataFromExperiment;
		}
		userIdHolder = userId;
		Map <String,String> columnDataFromExperiment_ = new HashMap<String, String>();

		// Experiment
		columnDataFromExperiment_.put("Experiment.TEMPLATEFLAG", "NULL");
		String expStatusId = formDao.getFromInfoLookup("ExperimentStatus", LookupType.NAME, "Planned", "id");
		columnDataFromExperiment_.put("Experiment.STATUS_ID", "'" + expStatusId + "'");
		columnDataFromExperiment_.put("Experiment.LASTSTATUS_ID","NULL");
		String labOfUser = generalDao
				.selectSingleString("Select t.LABORATORY_ID from fg_s_user_v t where t.user_id = " + userId);
		columnDataFromExperiment_.put("Experiment.Laboratory_id", "'" + labOfUser + "'");
		columnDataFromExperiment_.put("Experiment.CREATIONDATETIME", "NULL");
		columnDataFromExperiment_.put("Experiment.EstimatedStartDate", "NULL");
		columnDataFromExperiment_.put("Experiment.ActualStartDate", "NULL");
		columnDataFromExperiment_.put("Experiment.ACTUALSTARTTIMESTAMP", "NULL");
		columnDataFromExperiment_.put("Experiment.APPROVALTIMESTAMP", "NULL");
		columnDataFromExperiment_.put("Experiment.COMPLETIONTIMESTAMP", "NULL");
		columnDataFromExperiment_.put("Experiment.CompletionDate", "NULL");
		columnDataFromExperiment_.put("Experiment.ApprovalDate", "NULL");
		columnDataFromExperiment_.put("Experiment.LASTMODIFDATE", "NULL");
		columnDataFromExperiment_.put("Experiment.experimentName", "NULL");
		columnDataFromExperiment_.put("Experiment.formNumberId", "NULL");
		columnDataFromExperiment_.put("Experiment.PLANNED_ACTUAL", "NULL");
		columnDataFromExperiment_.put("Experiment.CONCLUSSION", "NULL");
		// columnData.put("Experiment.PLANNED_ACTUAL", "PLANNED_ACTUAL");
		columnDataFromExperiment_.put("Experiment.WEBIXANALYTTABLE", "NULL");
		columnDataFromExperiment_.put("Experiment.Creator_id", "'" + userId + "'");
		columnDataFromExperiment_.put("Experiment.Owner_id", "'" + userId + "'");
		columnDataFromExperiment_.put("Experiment.PROJECT_ID", "NULL");
		columnDataFromExperiment_.put("Experiment.SUBPROJECT_ID", "NULL");
		columnDataFromExperiment_.put("Experiment.SUBSUBPROJECT_ID", "NULL");
		columnDataFromExperiment_.put("Experiment.experimentVersion", "NULL");
		columnDataFromExperiment_.put("Experiment.reasonForChange", "NULL");
		columnDataFromExperiment_.put("Experiment.spreadsheetExcel", "(select spreadsheet from fg_s_spreadsheettempla_v where formid = SPREADSHEETTEMPLATE_ID)");
		
		//calculation tab
		columnDataFromExperiment_.put("Experiment.compVolumeRate", "NULL");
		columnDataFromExperiment_.put("Experiment.actCompVolumeRate", "NULL");
		columnDataFromExperiment_.put("Experiment.COMPVOLUMERATE_UOM_ID", "NULL");
		columnDataFromExperiment_.put("Experiment.compMassRate", "NULL");
		columnDataFromExperiment_.put("Experiment.actCompMassRate", "NULL");
		columnDataFromExperiment_.put("Experiment.COMPMASSRATE_UOM_ID", "NULL");
		columnDataFromExperiment_.put("Experiment.retentionTime", "NULL");
		columnDataFromExperiment_.put("Experiment.retentionTimeActual", "NULL");
		columnDataFromExperiment_.put("Experiment.RETENTIONTIMEUOM_ID", "NULL");
		
		// Mass Balance Tab in Organic
		columnDataFromExperiment_.put("Experiment.WEBIXMASSBALANCETABLE", "NULL");
		columnDataFromExperiment_.put("Experiment.WEBIXMASSBALANCETABLE2", "NULL");
		columnDataFromExperiment_.put("Experiment.WEBIXMASSBALANCETABLE3", "NULL");

		// Step
		columnDataFromExperiment_.put("Step.TEMPLATEFLAG", "NULL");
		String statusId = formDao.getFromInfoLookup("StepStatus", LookupType.NAME, "Planned", "id");
		columnDataFromExperiment_.put("Step.Status_ID", "'" + statusId + "'");
		columnDataFromExperiment_.put("Step.CreationDateTime", "NULL");
		columnDataFromExperiment_.put("Step.ESTIMSTARTDATE", "NULL");
		columnDataFromExperiment_.put("Step.ACTUALSTARTDATE", "NULL");
		columnDataFromExperiment_.put("Step.FINISHDATE", "NULL");
		columnDataFromExperiment_.put("Step.CREATOR_ID", "NULL");
		columnDataFromExperiment_.put("Step.REACTSTARTTIME", "NULL");
		columnDataFromExperiment_.put("Step.REACTFINISHTIME", "NULL");
		// columnData.put("Step.CONCLUSSION", "NULL");
		columnDataFromExperiment_.put("Step.PLANNED_ACTUAL", "NULL");
		columnDataFromExperiment_.put("Step.webixMassBalanceTable", "NULL");
		columnDataFromExperiment_.put("Step.summary", "NULL");
		columnDataFromExperiment_.put("Step.conversion", "NULL");
		columnDataFromExperiment_.put("Step.chemicalYield", "NULL");
		columnDataFromExperiment_.put("Step.isolatedYield", "NULL");
		columnDataFromExperiment_.put("Step.limitingAgentMole", "NULL");
		columnDataFromExperiment_.put("Step.conclussion", "NULL");
		columnDataFromExperiment_.put("Step.chkManualUpdate", "NULL");
		columnDataFromExperiment_.put("Step.chkCharacterMassBalance", "NULL");
		columnDataFromExperiment_.put("Step.compVolumeRate", "NULL");
		columnDataFromExperiment_.put("Step.compMassRate", "NULL");
		columnDataFromExperiment_.put("Step.retentionTime", "NULL");
		columnDataFromExperiment_.put("Step.COMPVOLUMERATE_UOM_ID", "NULL");
		columnDataFromExperiment_.put("Step.COMPMASSRATE_UOM_ID", "NULL");
		columnDataFromExperiment_.put("Step.RETENTIONTIMEUOM_ID", "NULL");
		columnDataFromExperiment_.put("Step.retentionTimeActual", "NULL");
		columnDataFromExperiment_.put("Step.CLONERUNFLAG", "'0'"); // IMPORTANT!!!!!
		columnDataFromExperiment_.put("Step.RUNNUMBER", "NULL"); // IMPORTANT!!!!!


		// Action
		columnDataFromExperiment_.put("Action.TEMPLATEFLAG", "NULL");
		columnDataFromExperiment_.put("Action.Observation", "NULL");
		columnDataFromExperiment_.put("Action.SETBEFORE_ID", "NULL");
		columnDataFromExperiment_.put("Action.startDate", "NULL");
		columnDataFromExperiment_.put("Action.endDate", "NULL");
		columnDataFromExperiment_.put("Action.startTime", "NULL");
		columnDataFromExperiment_.put("Action.endTime", "NULL");

		// Workup
		columnDataFromExperiment_.put("Workup.creationDate", "TO_CHAR(sysdate,'" + generalUtil.getConversionDateTimeFormat() + "')");
		columnDataFromExperiment_.put("Workup.sampleNumber", "NULL");
		columnDataFromExperiment_.put("Workup.sampleId", "NULL");
		statusId = formDao.getFromInfoLookup("WorkupStatus", LookupType.NAME, "Planned", "id");
		columnDataFromExperiment_.put("Workup.STATUS_ID", "'" + statusId + "'");
		String workupTypeCristalize = formDao.getFromInfoLookup("WorkupType", LookupType.NAME, "Crystallization", "id");
		String workupTypeFeeding = formDao.getFromInfoLookup("WorkupType", LookupType.NAME, "Fedding", "id");
		// in crystallization&feeding the planning state can be defined as long as the
		// stage is the first one or the second one
		columnDataFromExperiment_.put("Workup.ISPROCESSSTART", "DECODE(ISPROCESSSTART,0,0,DECODE(WORKUPTYPE_ID,'"
				+ workupTypeCristalize + "','1','" + workupTypeFeeding + "','1','0'))");
		String materialsDefStage = formDao.getFromInfoLookup("StageStatus", LookupType.NAME, "Materials Definition",
				"id");
		String preparationStage = formDao.getFromInfoLookup("StageStatus", LookupType.NAME, "Preparation", "id");
		String generalInputStage = formDao.getFromInfoLookup("StageStatus", LookupType.NAME, "Data General Input",
				"id");
		columnDataFromExperiment_.put("Workup.STAGE_ID",
				"DECODE(ISPROCESSSTART,0,STAGE_ID,DECODE(WORKUPTYPE_ID,'" + workupTypeCristalize + "','"
						+ materialsDefStage + "'" + ",'" + workupTypeFeeding + "','" + preparationStage + "'" + ",'"
						+ generalInputStage + "'))");
		columnDataFromExperiment_.put("Workup.startDate", "NULL");
		columnDataFromExperiment_.put("Workup.finishDate", "NULL");
		columnDataFromExperiment_.put("Workup.comments", "NULL");
		columnDataFromExperiment_.put("Workup.initialAmount", "NULL");
		columnDataFromExperiment_.put("Workup.distillateAmount", "NULL");
		columnDataFromExperiment_.put("Workup.residue", "NULL");
		columnDataFromExperiment_.put("Workup.INITIALAMOUNT_UOM", "NULL");
		columnDataFromExperiment_.put("Workup.DISTILLATEAMOUNT_UOM", "NULL");
		columnDataFromExperiment_.put("Workup.RESIDUE_UOM", "NULL");
		columnDataFromExperiment_.put("Workup.solvent", "NULL");
		columnDataFromExperiment_.put("Workup.materialSolventId", "NULL");
		columnDataFromExperiment_.put("Workup.qty", "NULL");
		columnDataFromExperiment_.put("Workup.QTYUOM", "NULL");
		columnDataFromExperiment_.put("Workup.quantity", "NULL");
		columnDataFromExperiment_.put("Workup.QUANTITY_UOM", "NULL");
		columnDataFromExperiment_.put("Workup.materialSolventId", "NULL");
		columnDataFromExperiment_.put("Workup.batchNumber", "NULL");
		columnDataFromExperiment_.put("Workup.batchId", "NULL");
		columnDataFromExperiment_.put("Workup.dryCakeAmount", "NULL");
		columnDataFromExperiment_.put("Workup.DRYCAKEAMOUNT_UOM", "NULL");
		columnDataFromExperiment_.put("Workup.appearance", "NULL");
		columnDataFromExperiment_.put("Workup.nextStageStatus", "NULL");
		columnDataFromExperiment_.put("Workup.prevStageStatus", "NULL");

		columnDataFromExperiment_.put("WuCryMixDefineRef.initialAmount", "NULL");
		columnDataFromExperiment_.put("WuCryMixDefineRef.activeIngredient", "NULL");
		columnDataFromExperiment_.put("WuCryMixDefineRef.INITIALAMOUNT_UOM", "NULL");
		columnDataFromExperiment_.put("WuCryMixDefineRef.quantity", "NULL");
		columnDataFromExperiment_.put("WuCryMixDefineRef.QUANTITY_UOM", "NULL");
		columnDataFromExperiment_.put("WuCryMixDefineRef.concentration", "NULL");
		columnDataFromExperiment_.put("WuCryMixDefineRef.CONCENTRATION_UOM", "NULL");

		columnDataFromExperiment_.put("WuFeedMaterialRef.actualPurity", "NULL");
		columnDataFromExperiment_.put("WuFeedMaterialRef.ACTUALPURITY_UOM", "NULL");
		columnDataFromExperiment_.put("WuFeedMaterialRef.initialTemp", "NULL");
		columnDataFromExperiment_.put("WuFeedMaterialRef.INITIALTEMP_UOM", "NULL");
		columnDataFromExperiment_.put("WuFeedMaterialRef.meanTemp", "NULL");
		columnDataFromExperiment_.put("WuFeedMaterialRef.MEANTEMP_UOM", "NULL");
		columnDataFromExperiment_.put("WuFeedMaterialRef.finalTemperature", "NULL");
		columnDataFromExperiment_.put("WuFeedMaterialRef.FINALTEMP_UOM", "NULL");
		columnDataFromExperiment_.put("WuFeedMaterialRef.actualFeedTime", "NULL");
		columnDataFromExperiment_.put("WuFeedMaterialRef.ACTUALFEEDTIME_UOM", "NULL");
		columnDataFromExperiment_.put("WuFeedMaterialRef.initialWeight", "NULL");
		columnDataFromExperiment_.put("WuFeedMaterialRef.INITIALWEIGHT_UOM", "NULL");
		columnDataFromExperiment_.put("WuFeedMaterialRef.finalWeight", "NULL");
		columnDataFromExperiment_.put("WuFeedMaterialRef.FINALWEIGHT_UOM", "NULL");
		columnDataFromExperiment_.put("WuFeedMaterialRef.calculatedWeight", "NULL");
		columnDataFromExperiment_.put("WuFeedMaterialRef.CALCWEIGHT_UOM", "NULL");
		columnDataFromExperiment_.put("WuFeedMaterialRef.correctionWeight", "NULL");
		columnDataFromExperiment_.put("WuFeedMaterialRef.CORRECTWEIGHT_UOM", "NULL");
		columnDataFromExperiment_.put("WuFeedMaterialRef.comments", "NULL");

		columnDataFromExperiment_.put("WuCrystSolventRef.quantity", "NULL");
		columnDataFromExperiment_.put("WuCrystSolventRef.QUANTITY_UOM", "NULL");
		columnDataFromExperiment_.put("WuCrystSolventRef.comments", "NULL");

		// invitemcolumn
		columnDataFromExperiment_.put("InvItemColumn.serialNumber", "NULL");
		columnDataFromExperiment_.put("InvItemColumn.batchNumber", "NULL");
		columnDataFromExperiment_.put("InvItemColumn.inUse_disabled", "\'In Use\'");

		// Formulants
		// columnData.put("formulantref.CASNUMBER", "NULL");

		// document
		// columnData.put("document.SAMPLE_ID", "NULL");
		
		//composition
		columnDataFromExperiment_.put("composition.ACTUAL", "NULL");
		columnDataFromExperiment_.put("composition.DELTA", "NULL");
		
		/////////////////////
		// hold origin from  id
		/////////////////////
		columnDataFromExperiment_.put("MaterialRef.ORIGINFORMID", "FORMID");
		columnDataFromExperiment_.put("ParamRef.ORIGINFORMID", "FORMID");
		columnDataFromExperiment_.put("Step.ORIGINFORMID", "FORMID");
		
		//update and return class map
		columnDataFromExperiment = new HashMap<String,String>();
		columnDataFromExperiment.putAll(columnDataFromExperiment_);
		return columnDataFromExperiment;
	}
	
	private Map<String, String> setColumnDataToCloneFromRun(String userId) {
		if(columnDataFromRun != null && holdExperimentCloneMap == 1 && userId != null && userId.equals(generalUtil.getNull(userIdHolder))) {
			return columnDataFromRun;
		}
		userIdHolder = userId;
		Map<String, String> columnDataFromRun_ = new HashMap<String, String>();

		// Experiment
		columnDataFromRun_.put("Experiment.TEMPLATEFLAG", "NULL");
		String expStatusId = formDao.getFromInfoLookup("ExperimentStatus", LookupType.NAME, "Planned", "id");
		columnDataFromRun_.put("Experiment.STATUS_ID", "'" + expStatusId + "'");
		columnDataFromRun_.put("Experiment.LASTSTATUS_ID","NULL");
		String labOfUser = generalDao
				.selectSingleString("Select t.LABORATORY_ID from fg_s_user_v t where t.user_id = " + userId);
		columnDataFromRun_.put("Experiment.Laboratory_id", "'" + labOfUser + "'");
		columnDataFromRun_.put("Experiment.CREATIONDATETIME", "NULL");
		columnDataFromRun_.put("Experiment.EstimatedStartDate", "NULL");
		columnDataFromRun_.put("Experiment.ActualStartDate", "NULL");
		columnDataFromRun_.put("Experiment.ACTUALSTARTTIMESTAMP", "NULL");
		columnDataFromRun_.put("Experiment.APPROVALTIMESTAMP", "NULL");
		columnDataFromRun_.put("Experiment.COMPLETIONTIMESTAMP", "NULL");
		columnDataFromRun_.put("Experiment.CompletionDate", "NULL");
		columnDataFromRun_.put("Experiment.ApprovalDate", "NULL");
		columnDataFromRun_.put("Experiment.LASTMODIFDATE", "NULL");
		columnDataFromRun_.put("Experiment.experimentName", "NULL");
		columnDataFromRun_.put("Experiment.formNumberId", "NULL");
		columnDataFromRun_.put("Experiment.PLANNED_ACTUAL", "NULL");
		columnDataFromRun_.put("Experiment.CONCLUSSION", "NULL");
		// columnData.put("Experiment.PLANNED_ACTUAL", "PLANNED_ACTUAL");
		columnDataFromRun_.put("Experiment.WEBIXANALYTTABLE", "NULL");
		columnDataFromRun_.put("Experiment.Creator_id", "'" + userId + "'");
		columnDataFromRun_.put("Experiment.Owner_id", "'" + userId + "'");
		columnDataFromRun_.put("Experiment.PROJECT_ID", "NULL");
		columnDataFromRun_.put("Experiment.SUBPROJECT_ID", "NULL");
		columnDataFromRun_.put("Experiment.SUBSUBPROJECT_ID", "NULL");
		columnDataFromRun_.put("Experiment.experimentVersion", "NULL");
		columnDataFromRun_.put("Experiment.reasonForChange", "NULL");
		// Mass Balance Tab in Organic
		columnDataFromRun_.put("Experiment.WEBIXMASSBALANCETABLE", "NULL");
		columnDataFromRun_.put("Experiment.WEBIXMASSBALANCETABLE2", "NULL");
		columnDataFromRun_.put("Experiment.WEBIXMASSBALANCETABLE3", "NULL");

		// Step
		columnDataFromRun_.put("Step.CLONERUNFLAG", "'1'"); // IMPORTANT!!!!!
		columnDataFromRun_.put("Step.RUNNUMBER", "NULL"); // IMPORTANT!!!!!
		columnDataFromRun_.put("Step.TEMPLATEFLAG", "NULL");
		String statusId = formDao.getFromInfoLookup("StepStatus", LookupType.NAME, "Planned", "id");
		columnDataFromRun_.put("Step.Status_ID", "'" + statusId + "'");
		columnDataFromRun_.put("Step.CreationDateTime", "NULL");
		columnDataFromRun_.put("Step.ESTIMSTARTDATE", "NULL");
		columnDataFromRun_.put("Step.ACTUALSTARTDATE", "NULL");
		columnDataFromRun_.put("Step.FINISHDATE", "NULL");
		columnDataFromRun_.put("Step.CREATOR_ID", "NULL");
		columnDataFromRun_.put("Step.REACTSTARTTIME", "NULL");
		columnDataFromRun_.put("Step.REACTFINISHTIME", "NULL");
		// columnData.put("Step.CONCLUSSION", "NULL");
		columnDataFromRun_.put("Step.PLANNED_ACTUAL", "NULL");
		columnDataFromRun_.put("Step.webixMassBalanceTable", "NULL");
		columnDataFromRun_.put("Step.summary", "NULL");
		columnDataFromRun_.put("Step.conversion", "NULL");
		columnDataFromRun_.put("Step.chemicalYield", "NULL");
		columnDataFromRun_.put("Step.isolatedYield", "NULL");
		columnDataFromRun_.put("Step.limitingAgentMole", "NULL");
		columnDataFromRun_.put("Step.conclussion", "NULL");
		columnDataFromRun_.put("Step.chkManualUpdate", "NULL");
		columnDataFromRun_.put("Step.chkCharacterMassBalance", "NULL");

		// Action
		columnDataFromRun_.put("Action.TEMPLATEFLAG", "NULL");
		columnDataFromRun_.put("Action.Observation", "NULL");
		columnDataFromRun_.put("Action.SETBEFORE_ID", "NULL");
		columnDataFromRun_.put("Action.startDate", "NULL");
		columnDataFromRun_.put("Action.endDate", "NULL");
		columnDataFromRun_.put("Action.startTime", "NULL");
		columnDataFromRun_.put("Action.endTime", "NULL");

		// Workup
		columnDataFromRun_.put("Workup.creationDate", "TO_CHAR(sysdate,'" + generalUtil.getConversionDateTimeFormat() + "')");
		columnDataFromRun_.put("Workup.sampleNumber", "NULL");
		columnDataFromRun_.put("Workup.sampleId", "NULL");
		statusId = formDao.getFromInfoLookup("WorkupStatus", LookupType.NAME, "Planned", "id");
		columnDataFromRun_.put("Workup.STATUS_ID", "'" + statusId + "'");
		String workupTypeCristalize = formDao.getFromInfoLookup("WorkupType", LookupType.NAME, "Crystallization", "id");
		String workupTypeFeeding = formDao.getFromInfoLookup("WorkupType", LookupType.NAME, "Fedding", "id");
		// in crystallization&feeding the planning state can be defined as long as the
		// stage is the first one or the second one
		columnDataFromRun_.put("Workup.ISPROCESSSTART", "DECODE(ISPROCESSSTART,0,0,DECODE(WORKUPTYPE_ID,'"
				+ workupTypeCristalize + "','1','" + workupTypeFeeding + "','1','0'))");
		String materialsDefStage = formDao.getFromInfoLookup("StageStatus", LookupType.NAME, "Materials Definition",
				"id");
		String preparationStage = formDao.getFromInfoLookup("StageStatus", LookupType.NAME, "Preparation", "id");
		String generalInputStage = formDao.getFromInfoLookup("StageStatus", LookupType.NAME, "Data General Input",
				"id");
		columnDataFromRun_.put("Workup.STAGE_ID",
				"DECODE(ISPROCESSSTART,0,STAGE_ID,DECODE(WORKUPTYPE_ID,'" + workupTypeCristalize + "','"
						+ materialsDefStage + "'" + ",'" + workupTypeFeeding + "','" + preparationStage + "'" + ",'"
						+ generalInputStage + "'))");
		columnDataFromRun_.put("Workup.startDate", "NULL");
		columnDataFromRun_.put("Workup.finishDate", "NULL");
		columnDataFromRun_.put("Workup.comments", "NULL");
		columnDataFromRun_.put("Workup.initialAmount", "NULL");
		columnDataFromRun_.put("Workup.distillateAmount", "NULL");
		columnDataFromRun_.put("Workup.residue", "NULL");
		columnDataFromRun_.put("Workup.INITIALAMOUNT_UOM", "NULL");
		columnDataFromRun_.put("Workup.DISTILLATEAMOUNT_UOM", "NULL");
		columnDataFromRun_.put("Workup.RESIDUE_UOM", "NULL");
		columnDataFromRun_.put("Workup.solvent", "NULL");
		columnDataFromRun_.put("Workup.materialSolventId", "NULL");
		columnDataFromRun_.put("Workup.qty", "NULL");
		columnDataFromRun_.put("Workup.QTYUOM", "NULL");
		columnDataFromRun_.put("Workup.quantity", "NULL");
		columnDataFromRun_.put("Workup.QUANTITY_UOM", "NULL");
		columnDataFromRun_.put("Workup.materialSolventId", "NULL");
		columnDataFromRun_.put("Workup.batchNumber", "NULL");
		columnDataFromRun_.put("Workup.batchId", "NULL");
		columnDataFromRun_.put("Workup.dryCakeAmount", "NULL");
		columnDataFromRun_.put("Workup.DRYCAKEAMOUNT_UOM", "NULL");
		columnDataFromRun_.put("Workup.appearance", "NULL");
		columnDataFromRun_.put("Workup.nextStageStatus", "NULL");
		columnDataFromRun_.put("Workup.prevStageStatus", "NULL");

		columnDataFromRun_.put("WuCryMixDefineRef.initialAmount", "NULL");
		columnDataFromRun_.put("WuCryMixDefineRef.activeIngredient", "NULL");
		columnDataFromRun_.put("WuCryMixDefineRef.INITIALAMOUNT_UOM", "NULL");
		columnDataFromRun_.put("WuCryMixDefineRef.quantity", "NULL");
		columnDataFromRun_.put("WuCryMixDefineRef.QUANTITY_UOM", "NULL");
		columnDataFromRun_.put("WuCryMixDefineRef.concentration", "NULL");
		columnDataFromRun_.put("WuCryMixDefineRef.CONCENTRATION_UOM", "NULL");

		columnDataFromRun_.put("WuFeedMaterialRef.actualPurity", "NULL");
		columnDataFromRun_.put("WuFeedMaterialRef.ACTUALPURITY_UOM", "NULL");
		columnDataFromRun_.put("WuFeedMaterialRef.initialTemp", "NULL");
		columnDataFromRun_.put("WuFeedMaterialRef.INITIALTEMP_UOM", "NULL");
		columnDataFromRun_.put("WuFeedMaterialRef.meanTemp", "NULL");
		columnDataFromRun_.put("WuFeedMaterialRef.MEANTEMP_UOM", "NULL");
		columnDataFromRun_.put("WuFeedMaterialRef.finalTemperature", "NULL");
		columnDataFromRun_.put("WuFeedMaterialRef.FINALTEMP_UOM", "NULL");
		columnDataFromRun_.put("WuFeedMaterialRef.actualFeedTime", "NULL");
		columnDataFromRun_.put("WuFeedMaterialRef.ACTUALFEEDTIME_UOM", "NULL");
		columnDataFromRun_.put("WuFeedMaterialRef.initialWeight", "NULL");
		columnDataFromRun_.put("WuFeedMaterialRef.INITIALWEIGHT_UOM", "NULL");
		columnDataFromRun_.put("WuFeedMaterialRef.finalWeight", "NULL");
		columnDataFromRun_.put("WuFeedMaterialRef.FINALWEIGHT_UOM", "NULL");
		columnDataFromRun_.put("WuFeedMaterialRef.calculatedWeight", "NULL");
		columnDataFromRun_.put("WuFeedMaterialRef.CALCWEIGHT_UOM", "NULL");
		columnDataFromRun_.put("WuFeedMaterialRef.correctionWeight", "NULL");
		columnDataFromRun_.put("WuFeedMaterialRef.CORRECTWEIGHT_UOM", "NULL");
		columnDataFromRun_.put("WuFeedMaterialRef.comments", "NULL");

		columnDataFromRun_.put("WuCrystSolventRef.quantity", "NULL");
		columnDataFromRun_.put("WuCrystSolventRef.QUANTITY_UOM", "NULL");
		columnDataFromRun_.put("WuCrystSolventRef.comments", "NULL");

		// invitemcolumn
		columnDataFromRun_.put("InvItemColumn.serialNumber", "NULL");
		columnDataFromRun_.put("InvItemColumn.batchNumber", "NULL");
		columnDataFromRun_.put("InvItemColumn.inUse_disabled", "\'In Use\'");

		// Formulants
		// columnData.put("formulantref.CASNUMBER", "NULL");

		// document
		// columnData.put("document.SAMPLE_ID", "NULL");
		
		/////////////////////
		// hold origin from  id
		/////////////////////
		columnDataFromRun_.put("MaterialRef.ORIGINFORMID", "FORMID");
		columnDataFromRun_.put("ParamRef.ORIGINFORMID", "FORMID");
		columnDataFromRun_.put("Step.ORIGINFORMID", "FORMID");
//		columnDataFromRun.put("formulantref.FORMULANTREF_ORIGINID", "NULL");
		
		//update and return class map
		columnDataFromRun = new HashMap<String,String>();
		columnDataFromRun.putAll(columnDataFromRun_);
		return columnDataFromRun;
	}
}
