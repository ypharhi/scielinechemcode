package com.skyline.customer.adama;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.Form;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.LookupType;
import com.skyline.form.bean.Result;
import com.skyline.form.bean.ValidationCode;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.FormSaveDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.dal.GeneralTaskDao;
import com.skyline.form.dal.UploadFileDao;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilCalc;
import com.skyline.form.service.GeneralUtilFormState;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.form.service.GeneralUtilPermission;
import com.skyline.form.service.IntegrationCalc;
import com.skyline.form.service.IntegrationEvent;
import com.skyline.form.service.IntegrationValidation;
import com.skyline.form.service.IntegrationWF;

@Service
public class CommonFunc {
 
	@Autowired
	private FormDao formDao;

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private GeneralUtilCalc generalUtilCalc;

	//	@Autowired
	//	private FormTempData formTempData;
	
	@Autowired
	private GeneralUtilFormState generalUtilFormState;

	@Autowired
	private GeneralDao generalDao;

	@Autowired
	private FormSaveDao formSaveDao;

	@Autowired
	protected UploadFileDao uploadFileDao;

	@Autowired
	private IntegrationCalc integrationCalc;

	@Autowired
	private IntegrationEvent integrationEvent;
	
	@Autowired
	private IntegrationValidation integrationValidation;
	
	@Autowired
	private GeneralTaskDao generalTaskDao;

	//	@Autowired
	//	private ChemDao marvinService;

	@Autowired
	protected GeneralUtilLogger generalUtilLogger;
	
	@Autowired
	private GeneralUtilPermission generalUtilPermission;
	
//	@Autowired
//	private FormService formService;
 
	@Autowired
	private IntegrationWF integrationWF;
	
	@Value("${savedConventionDbDateFormat}")
	private String savedConventionDbDateFormat; 
	
	
	public void validateUsedQuantityOnStepStatusChangeToFinished(String formId, String expVersion, StringBuilder sbInfo) throws Exception {
		
		//runs through all the batches that were used in this step and update their quantity in the inventory
		List<Map<String, Object>> ascendingBatchInfo = generalDao.getListOfMapsBySql(
				"select distinct MR.BATCH_ID,sum(FG_GET_NUM_NORMAL(MR.QUANTITY,MR.QUANTITYUOM_ID)) over (PARTITION BY step_id,mr.batch_id) QUANTITY"
				+ ",FG_GET_UOM_NORAML(MR.QUANTITYUOM_ID) QUANTITYUOM_ID"
				+ " from FG_S_MATERIALREF_ALL_V MR where MR.STEP_ID = '"
						+ formId
						+ "' and MR.BATCH_ID is not null and nvl(MR.ISSTANDART,0) = 0  and MR.SESSIONID is null and nvl(MR.active,'1')='1'");//TODO:get the batches that are not temporary under step
		if (ascendingBatchInfo.isEmpty()) {
			generalUtilLogger.logWriter(LevelType.DEBUG,
					"There is no batch in the step or the batches are standard(batch quantity is not depleted from batches defined as standard)",
					ActivitylogType.SaveEvent, formId);
		}
		for (Map<String, Object> batchInfo : ascendingBatchInfo) {
			String batchQuantity = batchInfo.get("QUANTITY") == null ? "0"
								: generalUtil.getEmpty(batchInfo.get("QUANTITY").toString(), "0");
			String quantityUom = batchInfo.get("QUANTITYUOM_ID") == null ? ""
								: batchInfo.get("QUANTITYUOM_ID").toString();
			if (batchQuantity.equals("0")) {
				continue;
			}
			if (generalUtil.getNull(expVersion).isEmpty() || generalUtil.getNull(expVersion).equals("01")) {
				postUseOfBatch(batchQuantity, quantityUom,
								batchInfo.get("BATCH_ID").toString(),
								formId, sbInfo);
			}
			//insert data into activityLog
			/*
			 * Double quantity = Double.parseDouble(currentQuantity) - Double.parseDouble(batchInfo.get("QUANTITY").toString());
			 * String lab = generalDao.selectSingleString(
			 * String.format(" select t.LABORATORY_ID from FG_S_INVITEMBATCH_PIVOT t where formId = %1$s",batchInfo.get("BATCH_ID")));
			 * String site = generalDao.selectSingleString(
			 * String.format(" select t.SITE_ID from FG_S_INVITEMBATCH_PIVOT t where formId = %1$s",batchInfo.get("BATCH_ID")));
			 * String location = formDao.getFromInfoLookup("LABORATORY", LookupType.ID, lab, "name") + '/'
			 * + formDao.getFromInfoLookup("SITE", LookupType.ID, site, "name");
			 * // ActivityLog activityLogEntity = new ActivityLog(ActivitylogType.Consumption,formId, "TO_DATE(sysdate)",user,Integer.toString(quantity),"",location,"TO_DATE(sysdate)","''","","");
			 * // insertIntoActivityLog(activityLogEntity);
			 * Map<String, String> infoMap = new HashMap<String, String>();
			 * infoMap.put("quantity", Double.toString(quantity));
			 * infoMap.put("location", location);
			 * infoMap.put("step_id", formId);
			 * generalUtilLogger.logWrite(LevelType.Other, "",binfo, ActivitylogType.Consumption, infoMap);
			 */
		}
	}
	
	/*
	 * updates the quantity and the depletion of a batch that was used in some inventory or some experiment
	 * @param usedQuantity
	 * @param usedQuantityUom - the UOM of the used quantity
	 * @param currentQuantity
	 * @param formId
	 * @throws Exception
	 */
	public void postUseOfBatch(String usedQuantity, String usedQuantityUom, String formId,
			String sourceId, StringBuilder sbInfo) throws Exception {
		String table = "FG_S_INVITEMBATCH_PIVOT";
		StringBuilder sb = new StringBuilder();

		Map<String, String> srcBatchInfo = generalDao.sqlToHashMap(
				"select INVITEMMATERIAL_ID,QUANTITY,QUANTITYUOM_ID,LABORATORY_ID,SITE_ID from FG_S_INVITEMBATCH_PIVOT where formId = '"
						+ formId + "'");
		//the update firstly occurred before validating the exceeded quantity in order to lock the record when some users simultaneously update the record
		String sql ="update FG_S_INVITEMBATCH_PIVOT"
				+ " set QUANTITY = fg_get_num_display(num_in => (fg_get_num_normal(QUANTITY,QUANTITYUOM_ID)-fg_get_num_normal("+usedQuantity+","+usedQuantityUom+"))"
						+ ", fixed_precision_in => 16"
						+ ", uom_id_in => fg_get_uom_noraml("+usedQuantityUom+")"
						+ ", convert_uom_id_in => QUANTITYUOM_ID"
						+ ", display_type_in => 2)"
				+ ", INUSEDEPLETED = case when (fg_get_num_display(num_in => (fg_get_num_normal(QUANTITY,QUANTITYUOM_ID)-fg_get_num_normal("+usedQuantity+","+usedQuantityUom+"))"
						+ ", fixed_precision_in => 16"
						+ ", uom_id_in => fg_get_uom_noraml("+usedQuantityUom+")"
						+ ", convert_uom_id_in => QUANTITYUOM_ID"
						+ ", display_type_in => 2)) = 0 then 'Depleted' else INUSEDEPLETED end"
				+ " where FORMID = '"+ formId +"'" ;
		formSaveDao.updateStructTableByFormId(sql, table, Arrays.asList("QUANTITY", "INUSEDEPLETED"), formId);

		Double normalUsedQuantity = generalUtilCalc.getNormalNumber(usedQuantity, usedQuantityUom, 0D);
		sb.append("Normal args: Consumed_Quantity = '" + normalUsedQuantity + "'." + "</br>");
		Double normalSrcQuantity = generalUtilCalc.getNormalNumber(srcBatchInfo.get("QUANTITY"),
				srcBatchInfo.get("QUANTITYUOM_ID"), 0D);
		sb.append("Normal args: Batch[formId=" + formId + "] Quantity = '" + normalSrcQuantity + "'." + "</br>");
		String usedQuantityUpToSrcUOM = generalUtilCalc.getFromNormalNumber(normalUsedQuantity.toString(),
				srcBatchInfo.get("QUANTITYUOM_ID"), sb);//the used quantity normalized to UOM of the source quantity UOM

		integrationValidation.validate(ValidationCode.INVALID_CONSUMED_QUANTITY, "INVITEMBATCH", formId,
				new String(normalUsedQuantity.toString() + "," + normalSrcQuantity.toString()+","+srcBatchInfo.get("INVITEMMATERIAL_ID")), sbInfo);

		/*String sql_ = String.format(
				" update %1$s set QUANTITY ='%2$.2f', INUSEDEPLETED = case when %2$.2f = 0.00 then 'Depleted' else INUSEDEPLETED end where FORMID =  '%3$s'",
				table, (Double.parseDouble(srcBatchInfo.get("QUANTITY")) - Double.parseDouble(usedQuantityUpToSrcUOM)),
				formId);
		formSaveDao.updateStructTableByFormId(sql_, table, Arrays.asList("QUANTITY", "INUSEDEPLETED"), formId);*/
		generalUtilLogger.logWriter(LevelType.INFO, "Quantity Consumption is performed from batch formid = '" + formId
				+ "'. Batch Source Quantity=" + srcBatchInfo.get("QUANTITY") + ". Consumed Quntity="
				+ usedQuantityUpToSrcUOM + ". Batch new quantity = " + srcBatchInfo.get("QUANTITY") + "-"
				+ usedQuantityUpToSrcUOM + "="
				+ String.valueOf(
						Double.parseDouble(srcBatchInfo.get("QUANTITY")) - Double.parseDouble(usedQuantityUpToSrcUOM)),
				ActivitylogType.Calculation, formId);

		String location = formDao.getFromInfoLookup("LABORATORY", LookupType.ID, srcBatchInfo.get("LABORATORY_ID"),
				"name") + '/' + formDao.getFromInfoLookup("SITE", LookupType.ID, srcBatchInfo.get("SITE_ID"), "name");

		Map<String, String> infoMap = new HashMap<String, String>();
		infoMap.put("quantity", usedQuantityUpToSrcUOM);
		infoMap.put("location", location);
		if (!generalUtil.getNull(sourceId).isEmpty()) {
			String[] stepIdList =  sourceId.split(",");
			for(String stepId:stepIdList){
				infoMap.put("step_id", stepId);
				generalUtilLogger.logWrite(LevelType.Other, "", formId, ActivitylogType.Consumption, infoMap);
			}
		} else {
			generalUtilLogger.logWrite(LevelType.Other, "", formId, ActivitylogType.Consumption, infoMap);
		}
	}

	public void actionValidationDate(String startDate, String endDate, String startTime, String endTime, String formId)
			throws Exception {
		if (!generalUtil.getNull(startDate).isEmpty() && !generalUtil.getNull(endDate).isEmpty()
				&& !generalUtil.getNull(startTime).isEmpty() && !generalUtil.getNull(endTime).isEmpty()) {
			String sql = String.format("select 1 " + "from fg_s_action_v t " + "where to_char(to_date('" + startTime
					+ "', 'hh24:mi'), 'hh24:mi')> " + "to_char(to_date('" + endTime
					+ "', 'hh24:mi'), 'hh24:mi') and to_char(to_date('" + startDate
					+ "', 'dd/MM/yyyy'), 'dd/MM/yyyy') = to_char(to_date('" + endDate
					+ "', 'dd/MM/yyyy'),'dd/MM/yyyy') and  t.formid = " + formId + " and t.active=1");
			String invalidActionEndTime = generalDao.selectSingleStringNoException(sql);
			if (!generalUtil.getNull(invalidActionEndTime).isEmpty()) {
				integrationValidation.validate(ValidationCode.INVALID_ACTION_ENDTINE, "Action", formId, "",
						new StringBuilder());

			}
		}
	}

	public String actionSetBefore(String stepId, String setBefore_id, String formNumberId) {
		String fNumberId = "";

		//if (!setBefore_id.isEmpty()) {
		fNumberId = formDao.getFromInfoLookup("Action", LookupType.ID, setBefore_id, "FORMNUMBERID");//formNumberId of action that selected in setBefore 
		String wherePart = "";
		String setPart = "";
		String currentNumberId = formNumberId;// formNumberId of current action
		boolean isCurrentBigger = (Integer.parseInt(fNumberId)) < (Integer.parseInt(currentNumberId));
		if (isCurrentBigger) { //the current formNumberId bigger than formNumberId of setBefore action
			wherePart = " formNumberId >= '" + fNumberId + "' and formNumberId < '" + currentNumberId
					+ "' and STEP_ID = '" + stepId + "'";
			setPart = " REPLACE( TO_CHAR(FORMNUMBERID+1,'00'),' ','') ";
		} else {
			wherePart = " formNumberId < '" + fNumberId + "' and formNumberId > '" + currentNumberId
					+ "' and STEP_ID = '" + stepId + "'";
			setPart = " REPLACE( TO_CHAR(FORMNUMBERID-1,'00'),' ','')";
			fNumberId = String.format("%02d", Integer.parseInt(fNumberId) - 1);

		}

		formSaveDao.updateStructTable(
				String.format(" update FG_S_ACTION_PIVOT set FORMNUMBERID = %1$s  where %2$s ", setPart, wherePart),
				"FG_S_ACTION_PIVOT", Arrays.asList("FORMNUMBERID"), "STEP_ID", stepId);
		//}
		return fNumberId;
	}

	public String splitSaveRequestEvent(String formId, List<String> operationTypeId, String userId,
			List<String> documentsIdList, List<String> materialsPeaksIdList,
			List<String> sampleIdList, String usersCrewId, String groupsCrewId, String experimentSelectId) {
		//		String formCode = formDao.getFormCodeBySeqId(formId);
		//		String table="FG_S_"+formCode+"_PIVOT";
		//get table name
		//		Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
		//		String formCodeEntity = generalUtil.getNull(form.getFormCodeEntity(), formCode);
		//		String table = "FG_S_" + formCodeEntity + "_PIVOT";

		String cloneFormId = "1";
		for (int i = 0; i < operationTypeId.size(); i++) {
			cloneFormId = formSaveDao.cloneStructTable(formId);
			postSplitRequestSaveEvent(formId, cloneFormId, operationTypeId.get(i), i + 1, userId, documentsIdList,
					materialsPeaksIdList, sampleIdList, usersCrewId, groupsCrewId, experimentSelectId);
		}
		return cloneFormId;
	}

	private String postSplitRequestSaveEvent(String formId, String cloneFormId, String operationTypeId,
			int numberOfOperationType, String userId, List<String> documentsIdList,
			List<String> materialsPeaksIdList, List<String> sampleIdList, String usersCrewId, String groupsCrewId,
			String experimentSelectId) {
		//String userId = generalUtil.getSessionUserId();
		String formCode = formDao.getFormCodeBySeqId(formId);
		//		String table="FG_S_"+formCode+"_PIVOT";
		//get table name
		Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
		String formCodeEntity = generalUtil.getNull(form.getFormCodeEntity(), formCode);
		String table = "FG_S_" + formCodeEntity + "_PIVOT";

		//Get formnumberid from request table by form id
		String formnumberid = formDao.getFromInfoLookup(formCode, LookupType.ID, formId, "formnumberid");
		//Make the whole requestNumber
		String requestNumber = formnumberid + "-OP" + Integer.valueOf(numberOfOperationType);

		//updates the request columns
		List<String> colList = Arrays.asList("parentRequestId", "ACTIVE", "requestName");
		String sql_ = "update " + table + " set parentRequestId = '" + formId + "',ACTIVE = 1 ,requestName ='"
				+ requestNumber + "' where FORMID='" + cloneFormId + "' ";
		formSaveDao.updateStructTableByFormId(sql_, table, colList, cloneFormId);

		/*
		 * //Add sub-Request to select request table
		 * String RequestSelectId = formSaveDao.getStructFormId("RequestSelect");
		 * formSaveDao.insertStructTableByFormId( "insert into FG_S_REQUESTSELECT_PIVOT (FORMID,TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMCODE,PARENTID,REQUEST_ID) " +
		 * "values ('"+ RequestSelectId + "', sysdate, " + userId + ", null,1,'RequestSelect','" +formId + "','" + cloneFormId + "')","FG_S_REQUESTSELECT_PIVOT",RequestSelectId);
		 */
		//update operation type with the correct sub request parent id
		String cloneOperationFormId = formSaveDao.cloneStructTable(operationTypeId);
		List<String> colListOperationType = Arrays.asList("parentId", "ACTIVE");
		String sql = "update FG_S_OperationType_PIVOT set parentId = '" + cloneFormId + "',ACTIVE = 1 where FORMID='"
				+ cloneOperationFormId + "' ";
		formSaveDao.updateStructTableByFormId(sql, "FG_S_OperationType_PIVOT", colListOperationType,
				cloneOperationFormId);

		// clone  documents table with the correct sub request parent id
		for (String docId : documentsIdList) {
			String cloneDocumentFormId = formSaveDao.cloneStructTable(docId);
			List<String> colListdocumentType = Arrays.asList("parentId", "ACTIVE");
			sql = "update FG_S_document_PIVOT set parentId = '" + cloneFormId + "',ACTIVE = 1 where FORMID='"
					+ cloneDocumentFormId + "' ";
			formSaveDao.updateStructTableByFormId(sql, "FG_S_document_PIVOT", colListdocumentType, cloneDocumentFormId);
		}
	
		// clone  MaterialsPeaks table with the correct sub request parent id
		for (String materialsPeaksId : materialsPeaksIdList) {
			String clonecompoundFormId = formSaveDao.cloneStructTable(materialsPeaksId);
			List<String> colListcompound = Arrays.asList("parentId", "ACTIVE");
			sql = "update FG_S_MaterialsPeaks_PIVOT set parentId = '" + cloneFormId + "',ACTIVE = 1 where FORMID='"
					+ clonecompoundFormId + "' ";
			formSaveDao.updateStructTableByFormId(sql, "FG_S_MaterialsPeaks_PIVOT", colListcompound, clonecompoundFormId);
		}

		// clone  samples table with the correct sub request parent id
		for (String sampleDataId : sampleIdList) {
			String cloneSampleDataFormId = formSaveDao.cloneStructTable(sampleDataId);
			List<String> colListSample = Arrays.asList("parentId", "ACTIVE");
			sql = "update FG_S_SampleDataRef_PIVOT set parentId = '" + cloneFormId + "',ACTIVE = 1 where FORMID='"
					+ cloneSampleDataFormId + "' ";
			formSaveDao.updateStructTableByFormId(sql, "FG_S_SampleDataRef_PIVOT", colListSample,
					cloneSampleDataFormId);
		}
		// clone  usersCrew table with the correct sub request parent id

		if (!generalUtil.getNull(usersCrewId).equals("")) {
			String cloneUsersCrewFormId = formSaveDao.cloneStructTable(usersCrewId);
			List<String> colListUsersCrew = Arrays.asList("parentId", "ACTIVE");
			sql = "update FG_S_usersCrew_PIVOT set parentId = '" + cloneFormId + "',ACTIVE = 1 where FORMID='"
					+ cloneUsersCrewFormId + "' ";
			formSaveDao.updateStructTableByFormId(sql, "FG_S_usersCrew_PIVOT", colListUsersCrew, cloneUsersCrewFormId);
		}

		if (!generalUtil.getNull(groupsCrewId).equals("")) {
			String cloneGroupsCrewIdFormId = formSaveDao.cloneStructTable(groupsCrewId);
			List<String> colListGroupsCrew = Arrays.asList("parentId", "ACTIVE");
			sql = "update FG_S_GroupsCrew_PIVOT set parentId = '" + cloneFormId + "',ACTIVE = 1 where FORMID='"
					+ cloneGroupsCrewIdFormId + "' ";
			formSaveDao.updateStructTableByFormId(sql, "FG_S_GroupsCrew_PIVOT", colListGroupsCrew,
					cloneGroupsCrewIdFormId);
		}

		if (!generalUtil.getNull(experimentSelectId).equals("")) {
			String experimentSelectIdFormId = formSaveDao.cloneStructTable(experimentSelectId);
			List<String> colListexperimentselect = Arrays.asList("parentId", "ACTIVE");
			sql = "update FG_S_experimentSelect_PIVOT set parentId = '" + cloneFormId + "',ACTIVE = 1 where FORMID='"
					+ experimentSelectIdFormId + "' ";
			formSaveDao.updateStructTableByFormId(sql, "FG_S_experimentSelect_PIVOT", colListexperimentselect,
					experimentSelectIdFormId);
		}

		//insert data into activityLog
		//		activityLogEntity = new ActivityLog(ActivitylogType.Registration,cloneFormId,"TO_DATE(sysdate)",user,splitQuantity,"","","''","''","","");
		//		insertIntoActivityLog(activityLogEntity);
		//infoMap = new HashMap<String, String>();
		//infoMap.put("splitQuantity", splitQuantity);
		//generalUtilLogger.insertActivityLog(cloneFormId, ActivitylogType.Registration, "", infoMap);

		return "1";
	}

	public void insertToResults(List<Result> resultList, String parent_id, Map<String, String> elementValueMap,boolean doUpdateMainResults) {
		//first,updates the active flags of all the certain experiment id/selfTest_id to 0, for future information about the  deleted results
		formSaveDao.updateSingleStringInfoNoTryCatch("update FG_RESULTS set RESULT_IS_ACTIVE = '0' where SELFTEST_ID = '"
				+ parent_id + "'" + " or (EXPERIMENT_ID = '" + parent_id + "' and SELFTEST_ID is null"
				+ (resultList != null && !resultList.isEmpty() && resultList.get(0).getresultIsWebix().equals("0")
						? " and RESULT_IS_WEBIX ='0'" : "")//checks if it's manual results, then deletes the manual ones only,because the calculated results deleted before when the calculated ones were inserted
				+ ")");
		String userId = generalUtil.getSessionUserId();
		generalUtilLogger.logWriter(LevelType.DEBUG,
				"Insert to results table! </br>"
						+ " first, update result_is_active=>query: update FG_RESULTS set RESULT_IS_ACTIVE = '0' where SELFTEST_ID = '"
						+ parent_id + "'" + " or (EXPERIMENT_ID = '" + parent_id + "' and SELFTEST_ID is null"
						+ (resultList != null && !resultList.isEmpty()
								&& resultList.get(0).getresultIsWebix().equals("0") ? " and RESULT_IS_WEBIX ='0'" : "")
						+ ")",
				ActivitylogType.SaveEvent, parent_id);

		if (resultList != null && isResultRelevant(parent_id, elementValueMap)) {
			//second, updates or insert the results to the result table
			for (Result result : resultList) {
				String sql = "merge into FG_RESULTS r using ( select '" + result.getExperimentId() + "' EXPERIMENT_ID,'"
						+ result.getResultTestName() + "' RESULT_TEST_NAME,'" + result.getResultName()
						+ "' RESULT_NAME,'" + result.getSampleId() + "' SAMPLE_ID,'" + result.getSelfTestId()
						+ "' SELFTEST_ID,'" + result.getResultRefId() + "' RESULTREF_ID,'"
						+ result.getResultMaterialId() + "' RESULT_MATERIAL_ID,'" + result.getResultType()
						+ "' RESULT_TYPE,"
						+ (!result.getUomType().isEmpty() ? "fg_get_Uom_by_uomtype('" + result.getUomType() + "')"
								: "'" + result.getResultUomId() + "'")
						+ " RESULT_UOM_ID,'" + result.getResultValue() + "' RESULT_VALUE,'" + result.getResultComment()
						+ "' RESULT_COMMENT,'" + result.getresultIsWebix() + "' RESULT_IS_WEBIX,'"
						+ result.getresultMaterialName() + "' RESULT_MATERIALNAME,'"// ab 07022018- manual results update
						+ result.getResultRequestId() + "' RESULT_REQUEST_ID"//TODO: remove this field from the table- ab 07022018- manual results update
						+ " from dual)  t "
						+ " on( (r.SAMPLE_ID = t.SAMPLE_ID and r.RESULT_MATERIALNAME = t.RESULT_MATERIALNAME and nvl(t.RESULT_REQUEST_ID,'') = nvl(r.RESULT_REQUEST_ID,'') and r.RESULTREF_ID = t.RESULTREF_ID and t.SELFTEST_ID is null and r.SELFTEST_ID is null) "//for manual results analytical
						+ " or (r.SAMPLE_ID = t.SAMPLE_ID and r.RESULT_MATERIAL_ID = t.RESULT_MATERIAL_ID and t.RESULT_REQUEST_ID is null and r.RESULT_REQUEST_ID is null and r.RESULTREF_ID = t.RESULTREF_ID and t.SELFTEST_ID is null and r.SELFTEST_ID is null) "//for manual results analytical
						+ " or (r.SAMPLE_ID = t.SAMPLE_ID and r.RESULT_MATERIAL_ID = t.RESULT_MATERIAL_ID and r.RESULT_REQUEST_ID = t.RESULT_REQUEST_ID and r.RESULTREF_ID = t.RESULTREF_ID and t.SELFTEST_ID is null and r.SELFTEST_ID is null) "//for manual results analytical
						+ " or (r.SAMPLE_ID = t.SAMPLE_ID and t.SELFTEST_ID is not null and t.RESULTREF_ID is null and r.SELFTEST_ID = t.SELFTEST_ID and t.RESULT_NAME = r.RESULT_NAME) "//for selftest results
						+ " or (r.SAMPLE_ID = t.SAMPLE_ID and t.SELFTEST_ID is not null and r.RESULTREF_ID = t.RESULTREF_ID and r.SELFTEST_ID = t.SELFTEST_ID and nvl(t.RESULT_MATERIAL_ID,t.RESULT_MATERIALNAME) = nvl(r.RESULT_MATERIAL_ID,t.RESULT_MATERIALNAME) and t.RESULT_TYPE = r.RESULT_TYPE) "//for selftest results
						+ " or (r.SAMPLE_ID = t.SAMPLE_ID and r.RESULT_MATERIAL_ID = t.RESULT_MATERIAL_ID and r.EXPERIMENT_ID = t.EXPERIMENT_ID and t.SELFTEST_ID is null and r.SELFTEST_ID is null and t.RESULTREF_ID is null and r.RESULTREF_ID is NULL) "
						+ " or (r.SAMPLE_ID = t.SAMPLE_ID and t.RESULT_MATERIAL_ID is null and r.RESULT_MATERIAL_ID is null and r.EXPERIMENT_ID = t.EXPERIMENT_ID and t.SELFTEST_ID is null and r.SELFTEST_ID is null and t.RESULTREF_ID is null and r.RESULTREF_ID is NULL) )"//for chromatogram result
						+ "when not matched then insert (EXPERIMENT_ID,RESULT_TEST_NAME,RESULT_NAME,SAMPLE_ID,RESULT_VALUE,RESULT_UOM_ID,RESULT_TYPE,RESULT_MATERIAL_ID,RESULT_DATE,RESULT_CHANGE_BY,RESULT_TIME,RESULT_COMMENT,RESULT_IS_ACTIVE,SELFTEST_ID,RESULT_IS_WEBIX,RESULT_MATERIALNAME,RESULT_REQUEST_ID,RESULTREF_ID)"
						+ " values ("
						+ "t.EXPERIMENT_ID,t.RESULT_TEST_NAME,t.RESULT_NAME,t.SAMPLE_ID,t.RESULT_VALUE,t.RESULT_UOM_ID,t.RESULT_TYPE,t.RESULT_MATERIAL_ID,sysdate,'"
						+ userId
						+ "',to_char( sysdate, 'HH24:MI:SS' ),t.RESULT_COMMENT,1,t.SELFTEST_ID,t.RESULT_IS_WEBIX,t.RESULT_MATERIALNAME,t.RESULT_REQUEST_ID,t.RESULTREF_ID) "
						+ "when matched then update set r.RESULT_VALUE = t.RESULT_VALUE, r.RESULT_TEST_NAME = t.RESULT_TEST_NAME, r.RESULT_DATE = sysdate,r.RESULT_CHANGE_BY='"
						+ userId
						+ "', r.RESULT_TIME = to_char( sysdate, 'HH24:MI:SS' ), r.RESULT_COMMENT = t.RESULT_COMMENT , r.RESULT_UOM_ID = t.RESULT_UOM_ID,r.RESULT_NAME = t.RESULT_NAME,r.SAMPLE_ID = t.SAMPLE_ID , r.RESULT_MATERIAL_ID = t.RESULT_MATERIAL_ID, r.RESULT_MATERIALNAME = t.RESULT_MATERIALNAME, r.RESULT_REQUEST_ID = t.RESULT_REQUEST_ID, r.RESULTREF_ID = t.RESULTREF_ID, r.RESULT_IS_ACTIVE = 1, r.RESULT_IS_WEBIX = t.RESULT_IS_WEBIX";

				generalUtilLogger.logWriter(LevelType.DEBUG,
						"Insert to results table! </br>" + " Merging query: </br>" + sql, ActivitylogType.SaveEvent,
						parent_id);
				formSaveDao.updateSingleStringInfoNoTryCatch(sql);
				if (result.getResultTestName().equals("Analytical")) {
					updateBatchPurity(result.getExperimentId(), result.getSampleId());
				}
			}
		}
		//update result for search (async outside the trnsaction)
		//		String dbTransactionId = generalUtil.getDBTransaction();
		//		generalTask.updateResultSearch(dbTransactionId); // only from job
		
		//update main results when saving results in the selftest/experiment when its version is larger than 1-particularly used for results that have been erased
		if(doUpdateMainResults){
			//select the results that are signed as main but where deleted by the user in the source experiment
			String sql = "select distinct s.sample_id,s.sampleresults,listagg(r.result_id,',')within group(order by r.result_id)over(partition by s.sample_id) as result_id_list \n"
					+ " from fg_results r,\n"
					+ " fg_s_sample_v s\n"
					+ " where r.result_is_active='0'\n"
					+ " and instr(','||s.sampleresults||',',','||r.result_id||',')>0\n"
					+ " and (r.SELFTEST_ID = '"	+ parent_id + "'\n" 
					+ " or (r.EXPERIMENT_ID = '" + parent_id + "' and r.SELFTEST_ID is null\n"
					+ (resultList != null && !resultList.isEmpty() && resultList.get(0).getresultIsWebix().equals("0")
							? " and r.RESULT_IS_WEBIX ='0'" : "")//checks if it's manual results, then deletes the manual ones only,because the calculated results deleted before when the calculated ones were inserted
					+ "))";
			List<Map<String,Object>> sampleDatalist = generalDao.getListOfMapsBySql(sql);
			for(Map<String,Object> sampleData:sampleDatalist){
				List<String> similar = new ArrayList<>();
				List<String> existingResults = sampleData.get("sampleresults")!=null?Arrays.asList(sampleData.get("sampleresults").toString().split(",")):null;
				List<String> resultsToRemove =  sampleData.get("result_id_list")!=null?Arrays.asList(sampleData.get("result_id_list").toString().split(",")):null;
				if(resultsToRemove!=null&&!resultsToRemove.isEmpty()){
					similar.addAll(existingResults);
					List<String> different = new ArrayList<String>();
					different.addAll(similar);
					similar.retainAll(resultsToRemove);//gets all the results that should be removed
					different.removeAll(similar);//remove the results that are not relevant anymore
					//different.addAll(itemsToSelect);
					
					String itemsToSelectCsv = generalUtil.listToCsv(different);
					formSaveDao.updateStructTableByFormId("update fg_s_sample_pivot\n"
							+ " set sampleresults = '"+itemsToSelectCsv+"'\n"
							+ " where formid = '"+sampleData.get("sample_id").toString()+"'",
							"fg_s_sample_pivot", Arrays.asList("sampleresults"), sampleData.get("sample_id").toString());
					
				}
				
			}
		}
	}

	/**
	 * 
	 * @param formId-
	 *            formId of the form that calculated results
	 * @param elementValueMap
	 *            - map of the current elements
	 * @return boolean value that indicates if the results are relevant for displaying in the source experiments and sample forms
	 */
	private Boolean isResultRelevant(String formId, Map<String, String> elementValueMap) {
		String formCodeEntity = formDao.getFormCodeEntityBySeqId("", formId);
		if (formCodeEntity.equals("Experiment")) {
			String expStatus = formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.ID,
					elementValueMap.get("STATUS_ID"), "name");
			if (expStatus != null && expStatus.equals("Planned")) {
				return false;
			}
		}
		return true;
	}

	private void updateBatchPurity(String experimentId, String sampleId) {
		try {
			String experimentStatusName = formDao.getFromInfoLookup("Experiment", LookupType.ID, experimentId,
					"STATUSNAME");
			if (experimentStatusName.equals("Approved")) {//the results are transfered to the sample when the experiment is Approved
				String relatedBatchDefined = generalDao
						.selectSingleStringNoException("select BATCH_ID from fg_s_sample_v where SAMPLE_ID = '"
								+ sampleId + "' and BATCHDEFINITION = '1'");
				if (!relatedBatchDefined.isEmpty()) {//assuming that there are some results that selected automatically, then update the batch defined purity
					
					String sqlString = "update fg_s_materialcomponent_pivot t\n"
							+ "set concentration = nvl((Select RESULT_VALUE from FG_I_SELECTEDRESULTS_V r "
							+ "where t.MATERIAL_ID = r.RESULT_MATERIAL_ID and r.SAMPLE_ID ='" + sampleId
							+ "' and RESULT_NAME = 'Assay'),t.concentration)\n"
							+ ", approvalDate = nvl((Select to_char(RESULT_DATE,'dd/MM/yyyy') from FG_I_SELECTEDRESULTS_V r "
							+ "where t.MATERIAL_ID = r.RESULT_MATERIAL_ID and r.SAMPLE_ID ='" + sampleId
							+ "' and RESULT_NAME = 'Assay'),t.approvaldate)\n"
							+ " where parentid = '"+relatedBatchDefined+"'";
					formSaveDao.updateStructTable(sqlString, "fg_s_materialcomponent_pivot",
							Arrays.asList("concentration","approvaldate"),"parentid", relatedBatchDefined);
					
					String sql = "Select nvl((Select RESULT_VALUE from FG_I_SELECTEDRESULTS_V r " + "where '"
							+ formDao.getFromInfoLookup("InvItemBatch", LookupType.ID, relatedBatchDefined,
									"INVITEMMATERIAL_ID")
							+ "' = r.RESULT_MATERIAL_ID" + " and r.SAMPLE_ID ='" + sampleId + "'"
							+ " and RESULT_NAME = 'Assay'),'-1') from DUAL";
					String purity = generalDao.selectSingleStringNoException(sql);
					if (purity.equals("-1")) {
						return;
					}
					sql = "select invitembatch_id,PURITYUOM_ID from fg_s_invitembatch_all_v where sample_id = '"
							+ sampleId + "' and purity<>'" + purity + "'";
					Map<String, String> batchMap = generalDao.sqlToHashMap(sql);
					if (!batchMap.isEmpty()) {
						formSaveDao.updateStructTableByFormId(
								"update fg_s_invitembatch_pivot" + " set purity = '" + purity + "'"
										+ " where formid = '" + batchMap.get("INVITEMBATCH_ID") + "'",
								"fg_s_invitembatch_pivot", Arrays.asList("PURITY"), batchMap.get("INVITEMBATCH_ID"));
						batchMap.put("PURITY", purity);
						onChangeInventoryEvent("InvItemBatch", batchMap.get("INVITEMBATCH_ID"), batchMap,
								generalUtil.getSessionUserId(), null);
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			generalUtilLogger.logWrite(e);
		}
	}

	public void onChangeInventoryEvent(String formCode, String formId, Map<String, String> elementValueMap,
			String userId, StringBuilder sbInfo) throws Exception {
		try {
			switch (formCode) {
				case "InvItemMaterial":
					updateReactantFieldsByMaterial(formId, elementValueMap, userId, sbInfo);
					break;
				case "InvItemBatch":
					updateReactantFieldsByBatch(formId, elementValueMap, userId, sbInfo);
					break;
				default:
					break;

			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.DEBUG, "The recalculation was not performed well for some reason",
					ActivitylogType.SaveEvent, formId);
		}
	}

	private void updateReactantFieldsByBatch(String formId, Map<String, String> elementValueMap, String userId,
			StringBuilder sbInfo) throws Exception {

		//select the reactants related with the current material formid
		List<String> reactantIdList = generalDao.getListOfStringBySql(String.format(
				"select formid from FG_S_MATERIALREF_V" + " where BATCH_ID = '" + formId + "'"
						+ " and sessionid is null and active =1"
						+ " and (purityInf <> '%1$s' or purityuom_id_inf <> '%2$s')" + " order by LIMITINGAGENT desc",
				elementValueMap.get("purity"), elementValueMap.get("PURITYUOM_ID")));//the ordering is for updating the limiting agent reactants first, cause the others depends on its values

		if (!reactantIdList.isEmpty()) {
			generalUtilLogger.logWriter(LevelType.INFO,
					"Batch '" + formDao.getFromInfoLookup("InvItemBatch", LookupType.ID, formId, "name")
							+ "' has been changed.</br>" + "Purity is being updated in the referenced reactants.</br> "
							+ "Recalculation of the affected fileds, quantity and volume, is beeing executed according to the mole field.",
					ActivitylogType.Calculation, formId, userId);
		}

		for (String reactantId : reactantIdList) {
			//get reactant step
			String parentId = generalDao.selectSingleStringNoException(
					"select parentid from fg_s_materialref_v where formid = '" + reactantId + "'");

			//reactant in step which status is Finished should not be affected anymore of any change
			String stepStatus = formDao.getFromInfoLookup("Step", LookupType.ID, parentId, "STATUSNAME");
			if (generalUtil.getNull(stepStatus).equals("Finished")) {
				continue;
			}

			//first, update the values that were changed
			String sql = String.format(
					"update FG_S_MATERIALREF_PIVOT set purityInf = '%1$s', purityuom_id_inf = '%2$s' where formid = '%3$s'",
					elementValueMap.get("purity"), elementValueMap.get("PURITYUOM_ID"), reactantId);
			formSaveDao.updateStructTableByFormId(sql, "FG_S_MATERIALREF_PIVOT", Arrays.asList("purityInf"),
					reactantId);
		}

		for (String reactantId : reactantIdList) {
			//get reactant map
			Map<String, String> reactantElementValueMap = formDao.getFormElementValuesMap(reactantId, "MaterialRef");

			//reactant in step which status is Finished should not be affected anymore of any change
			String stepStatus = formDao.getFromInfoLookup("Step", LookupType.ID,
					reactantElementValueMap.get("parentId"), "STATUSNAME");
			if (generalUtil.getNull(stepStatus).equals("Finished")) {
				continue;
			}

			generalUtilLogger.logWriter(LevelType.INFO, "Recalculate reactant " + reactantId + ".",
					ActivitylogType.Calculation, reactantId, userId);

			//second, recalculate the calculated fields
			recalculateReactant(reactantId, userId, reactantIdList, reactantElementValueMap, "mole", sbInfo, false);
		}
	}

	private void updateReactantFieldsByMaterial(String formId, Map<String, String> elementValueMap, String userId,
			StringBuilder sbInfo) throws Exception {

		//select the reactants related with the current material formid
		List<String> reactantIdList = generalDao.getListOfStringBySql(String.format(
				"select formid from FG_S_MATERIALREF_V" + " where INVITEMMATERIAL_ID = '" + formId + "'"
						+ " and sessionid is null and active =1"
						+ " and (densityInf <> '%1$s' or mwInf <> '%2$s' or DENSITYUOM_ID_INF <> '%3$s' or mwuom_id_inf <> '%4$s')"
						+ " order by LIMITINGAGENT desc",
				elementValueMap.get("density"), elementValueMap.get("mw"), elementValueMap.get("DENSITY_UOM_ID"),
				elementValueMap.get("MW_UOM_ID")));//the ordering is for updating the limiting agent reactants first, cause the others depends on its values

		if (!reactantIdList.isEmpty()) {
			generalUtilLogger.logWriter(LevelType.INFO,
					"Material '" + elementValueMap.get("invItemMaterialName") + "' has been changed.</br>"
							+ "Density and MW are being updated in the referenced reactants.</br> "
							+ "Recalculation of the affected fileds, quantity and volume, is beeing executed according to the mole field.",
					ActivitylogType.Calculation, formId, userId);
		}

		for (String reactantId : reactantIdList) {
			//get reactant step
			String parentId = generalDao.selectSingleStringNoException(
					"select parentid from fg_s_materialref_v where formid = '" + reactantId + "'");

			//reactant in step which status is Finished should not be affected anymore of any change
			String stepStatus = formDao.getFromInfoLookup("Step", LookupType.ID, parentId, "STATUSNAME");
			if (generalUtil.getNull(stepStatus).equals("Finished")) {
				continue;
			}

			//first, update the values that were changed
			String sql = String.format(
					"update FG_S_MATERIALREF_PIVOT set densityInf = '%1$s', mwInf = '%2$s', DENSITYUOM_ID_INF = '%3$s', mwuom_id_inf = '%4$s' where formid = '%5$s'",
					elementValueMap.get("density"), elementValueMap.get("mw"), elementValueMap.get("DENSITY_UOM_ID"),
					elementValueMap.get("MW_UOM_ID"), reactantId);
			formSaveDao.updateStructTableByFormId(sql, "FG_S_MATERIALREF_PIVOT",
					Arrays.asList("densityInf", "mwInf", "DENSITYUOM_ID_INF", "mwuom_id_inf"), reactantId);
		}

		for (String reactantId : reactantIdList) {
			//get reactant map
			Map<String, String> reactantElementValueMap = formDao.getFormElementValuesMap(reactantId, "MaterialRef");

			//reactant in step which status is Finished should not be affected anymore of any change
			String stepStatus = formDao.getFromInfoLookup("Step", LookupType.ID,
					reactantElementValueMap.get("parentId"), "STATUSNAME");
			if (generalUtil.getNull(stepStatus).equals("Finished")) {
				continue;
			}

			generalUtilLogger.logWriter(LevelType.INFO, "Recalculate reactant " + reactantId + ".",
					ActivitylogType.Calculation, reactantId, userId);

			//second, recalculate the calculated fields
			recalculateReactant(reactantId, userId, reactantIdList, reactantElementValueMap, "mole", sbInfo, true);
		}
		if (!sbInfo.toString().isEmpty()) {
			String[] experimentLst = sbInfo.toString().split(",");
			List<String> experimentLstNoDuplicates = new ArrayList<>();
			for (String expId : experimentLst) {
				if (!experimentLstNoDuplicates.contains(expId)) {
					experimentLstNoDuplicates.add(expId);
				}
			}

			StringBuilder sbMsg = new StringBuilder();
			for (String expId : experimentLstNoDuplicates) {
				String experimentNum = formDao.getFromInfoLookup("Experiment", LookupType.ID, expId, "name");
				String expFormCode = formDao.getFormCodeBySeqId(expId);
				sbMsg.append(
						"</br><a id='" + expId + "';formCode='" + expFormCode + "';val='" + experimentNum + "'></a>");
			}
			String msg = generalUtil.getSpringMessagesByKey(
					String.valueOf(ValidationCode.INVALID_CONSUMED_QUANTITY_MATERIAL), new Object[] { sbMsg },
					String.valueOf(ValidationCode.INVALID_CONSUMED_QUANTITY_MATERIAL));
			sbInfo.delete(0, sbInfo.length());
			sbInfo = sbInfo.append(msg);
		}

	}

	public void recalculateReactant(String formId, String userId, List<String> reactantIdList,
			Map<String, String> reactantElementValueMap, String mainArg, StringBuilder sbInfo,
			Boolean isGetExperimentViolationlst) throws Exception {
		//make calculations
		String[] matchArray = new String[10];

		String limitingAgentFormId = generalDao
				.selectSingleStringNoException("select formid from fg_s_materialref_v t where t.PARENTID = '"
						+ reactantElementValueMap.get("parentId") + "' and t.LIMITINGAGENT = 1 and formid<>'" + formId
						+ "' and sessionId is null and active = 1");
		if (!limitingAgentFormId.isEmpty()) {
			reactantElementValueMap.put("isLimited", "1");
		}

		if (limitingAgentFormId.isEmpty() || !reactantElementValueMap.get("limitingAgent").equals("0")
				|| !reactantIdList.contains(limitingAgentFormId)
				|| reactantElementValueMap.get("tableType").equals("Solvent")) {//if the limiting reactant is in the updated reactant list, it means that the limiting reactant has already updated its siblings(also the current reactant)

			String calcFieldsJson = integrationCalc
					.doCalc("MaterialTripletCalc", mainArg, reactantElementValueMap.get(mainArg),
							reactantElementValueMap, null,
							mainArg.equals("mole") ? Arrays.asList("quantity", "volume").toArray(matchArray)
									: Arrays.asList("mole", "volume").toArray(matchArray),
							"MaterialRef", formId, userId);
			Map<String, String> updatedElementValueMapp = generalUtil.jsonSimpleToMap("", calcFieldsJson);
			String setUpdateString = "", updatedElementNames = "";
			for (Map.Entry<String, String> entry : updatedElementValueMapp.entrySet()) {
				if(entry.getKey().replace("_", "").equals("warningMsg")){
					continue;
				}
				setUpdateString = (setUpdateString + "," + entry.getKey().replace("_", "") + "= '" + entry.getValue()
						+ "'");
				updatedElementNames = updatedElementNames + "," + entry.getKey().replace("_", "");
			}
			if (!setUpdateString.replaceFirst(",", "").isEmpty()
					&& !updatedElementNames.replaceFirst(",", "").isEmpty()) {
				formSaveDao.updateStructTableByFormId(
						"update FG_S_MATERIALREF_PIVOT" + " set " + setUpdateString.replaceFirst(",", "")
								+ " where formid = '" + formId + "'",
						"FG_S_MATERIALREF_PIVOT", Arrays.asList(updatedElementNames.replaceFirst(",", "")), formId);
			}

			if (reactantElementValueMap.get("limitingAgent").equals("1")
					&& reactantElementValueMap.get("tableType").equals("Reactant")) {
				integrationCalc.doCalc("MaterialTripletCalc", "OnSaveLimitingAgent", "", reactantElementValueMap, null,
						null, "MaterialRef", formId, userId);
			}

			if (!generalUtil.getNull(reactantElementValueMap.get("BATCH_ID")).isEmpty()) {
				Map<String, String> srcBatchInf = generalDao
						.sqlToHashMap("select QUANTITY,QUANTITYUOM_ID from FG_S_INVITEMBATCH_PIVOT where formId = '"
								+ reactantElementValueMap.get("BATCH_ID") + "'");
				Double normalSrcQuantity = generalUtilCalc.getNormalNumber(
						generalUtil.getEmpty(srcBatchInf.get("QUANTITY"), "0"), srcBatchInf.get("QUANTITYUOM_ID"), 0D);
				Double normalConsumeQuantity = generalUtilCalc.getNormalNumber(
						generalUtil.getEmpty(reactantElementValueMap.get("quantity"), "0"),
						reactantElementValueMap.get("QUANTITYUOM_ID"), 0D);

				String uomType = formDao.getFromInfoLookup("uom", LookupType.ID, srcBatchInf.get("QUANTITYUOM_ID"),
						"UOMTYPENAME");
				String normalUomId = generalDao
						.selectSingleStringNoException("select fg_get_Uom_by_uomtype('" + uomType + "') from dual");
				String normalUomName = formDao.getFromInfoLookup("uom", LookupType.ID, normalUomId, "name");
				integrationValidation.validate(ValidationCode.CHECK_CONSUMED_QUANTITY, "INVITEMBATCH", formId,
						new String(normalConsumeQuantity.toString() + "," + normalSrcQuantity.toString() + ","
								+ reactantElementValueMap.get("BATCH_ID") + ","
								+ reactantElementValueMap.get("parentId") + ","
								+ generalUtil.getEmpty(normalUomName, "kg") + "," + isGetExperimentViolationlst),
						sbInfo);
			}
		}

		if (!reactantElementValueMap.get("tableType").equals("Product")) {
			integrationCalc.doCalc("MaterialTripletCalc", "OnSave", "", reactantElementValueMap, null, null,
					"MaterialRef", formId, userId);
		}
	}

	public void setMaterialsInWorkup(List<Map<String, Object>> results, String tableType, String tableName,
			String resultColumn, String formId, String userId) {
		for (Map<String, Object> result : results) {

			String newFormId = formSaveDao.getStructFormId(tableName);
			String materialId = ((String) result.get("RESULT_MATERIAL_ID"));
			String materialName = generalUtil.getNull(generalDao.selectSingleStringNoException(
					"select t.INVITEMMATERIALNAME from FG_S_INVITEMMATERIAL_ALL_V t where t.formId = '" + materialId
							+ "'")).replace("'", "''");
			if (!materialName.isEmpty()) {
				String sql_ = "insert into FG_S_" + tableName + "_PIVOT"
						+ " (CREATION_DATE,CREATED_BY,TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE,CREATEDBYUSER,TABLETYPE,RESULTID_HOLDER,MATERIAL_ID, MATERIALNAME, "
						+ resultColumn + ", " + resultColumn + "_UOM"
						+ ((tableName.equals("wufeedmaterialref")) ? "" : ",ACTIVEINGREDIENT") + ")"
						+ " VALUES (SYSDATE,'" + userId + "',SYSDATE,'" + userId + "',null,1,'" + newFormId + "','"
						+ formId + "','" + tableName + "', 1, '" + tableType + "','" + result.get("RESULT_ID") + "','" + materialId + "','" + materialName
						+ "', '" + result.get("RESULT_VALUE") + "', '" + result.get("RESULT_UOM_ID") + "'"
						+ ((tableName.equals("wufeedmaterialref")) ? ""
								: (result.get("RESULT_NAME").toString().contains("Assay")) ? ",1" : ",0")
						+ " )";
				formSaveDao.insertStructTableByFormId(sql_, "FG_S_" + tableName + "_PIVOT", newFormId);
			}
			//formSaveDao.updateSingleStringInfo(sql_);
		}
	}
	 
	public ActionBean doSaveOnException(Exception e, String formId, String formCode) {
		String update = "";
		String errMsg = "";
		generalUtilLogger.logWriter(LevelType.WARN,
				"WARN - error in Save event of formId=" + formId + ",formCode = " + formCode, ActivitylogType.SaveException,
				formId, e, null);
		e.printStackTrace();
		if (generalUtil.getNull(e.getMessage()).contains("unique constraint")
				|| generalUtil.getNull(e.getMessage()).contains("check constraint")) {
			// uniqueConstraint is the constraint name in the db and the beginning of the dialog message
			// example 'PROJECT_NAME'
			String uniqueConstraint = e.getMessage().substring(e.getMessage().lastIndexOf(".") + 1,
					e.getMessage().lastIndexOf(")"));
			update = (e.getMessage().contains("unique constraint") ? "-2," : "-3,") + uniqueConstraint;
		} else {
			update = "-1";
			errMsg = e.getMessage();
		}
		return new ActionBean("no action needed", generalUtil.StringToList(update), errMsg);
	}
	 
	public String doRemove(String formCode, String formId, String userId) {

		String toReturn = "";
		Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
		// system USER will remove it totally from the DB
		if (formCode.equals("FormulantRef")) { // system // TODO fix in adama > 1.X - workaround because FormulantRef can not be tmp (if temp it was to complicated to implement it)
			toReturn = formDao.removeFromDB(formId, form.getFormCodeEntity());
		} else {

			if (generalDao.checkIfColumnExists("fg_s_" + form.getFormCodeEntity() + "_v", "parentid")) {
				String parentId = formDao.getFormParentId(formCode, formId);
				String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, parentId);
				if (sessionId != null) {
					toReturn = formSaveDao.doRemoveTmpDataProduction(formCode, formId, sessionId, userId);
				} else {
					formSaveDao.doRemoveProduction(formCode, formId, userId);
				}
			} else {
				formSaveDao.doRemoveProduction(formCode, formId, userId);
			}
		}

		//update cache
		try {
			//generalTask.updateCach(integrationEvent.getUpdateCacheFormList(formCode));
			generalTaskDao.updateCach(form);
			
			generalTaskDao.updateMVByPivotTable(form.getFormType(), "FG_S_" + form.getFormCodeEntity().toUpperCase() + "_PIVOT", formCode, 0, "", "D");
		} catch (Exception e) {
			// DO NOTHING
		}

		return toReturn;
	}

	// save sample
	public void updateRequestBySample(List<String> samplesList, String operationTypeId, String formId, String userId) {
		String inProgressStatusId = formDao.getFromInfoLookup("RequestStatus", LookupType.NAME, "In Progress", "id");
		String waitingStatusId = formDao.getFromInfoLookup("RequestStatus", LookupType.NAME, "Waiting", "id");

		if (samplesList != null && !samplesList.isEmpty()) {
			String sql = "select t.request_id from fg_i_conn_request_smpl_v t,fg_s_operationtype_all_v op where t.sample_id in("
					+ generalUtil.listToCsv(samplesList) + ") and t.REQUEST_ID=op.PARENTID and op.EXPERIMENTTYPE_ID='"
					+ operationTypeId + "' and (t.REQUESTSTATUS_ID='" + waitingStatusId + "' or t.REQUESTSTATUS_ID='"
					+ inProgressStatusId + "')";
			List<String> requestList = generalDao.getListOfStringBySql(sql);
			if (requestList != null && !requestList.isEmpty()) {
				for (String requestId : requestList) {
					String table = "FG_S_Request_PIVOT";
					String ExternalRequestId = formDao.getFromInfoLookup("UNITS", LookupType.NAME, "External Tasks",
							"id");
					List<String> colList = Arrays.asList("requeststatus_id");
					String sql_ = "update " + table + " set requestStatus_id = '" + inProgressStatusId
							+ "' where DESTUNIT_ID!='" + ExternalRequestId + "' and formid = '" + requestId
							+ "' and requestStatus_id= '" + waitingStatusId + "'";
					String res = formSaveDao.updateStructTableByFormId(sql_, table, colList, formId);
					if (res.equals("1")) {
						String expNumberId = formDao.getFromInfoLookup("Experiment", LookupType.ID, formId, "name");
						String reqNumberId = formDao.getFromInfoLookup("Request", LookupType.ID, requestId, "name");
						generalUtilLogger.logWriter(LevelType.Other,
								"The status of request " + reqNumberId
										+ " changed from 'Waiting' to 'In Progress' because it was related to experiment "
										+ expNumberId,
								ActivitylogType.RequestStatusChanged, requestId);
					}
				}
				// insert to requestselect table
				String sessionId_ = generalUtilFormState.getSessionId(formId);
				formDao.insertToSelectTable("REQUESTSELECT", formId, "REQUEST_ID", requestList, true, userId,
						sessionId_);
			}
		}
	}
	
	//save request
		public void updateSampleByRequest(List<String> requestList,String formId,String userId){
			//all new selected requests status is automatically changed to “In progress” (if it was “Waiting”)
			String inProgressStatusId = formDao.getFromInfoLookup("RequestStatus", LookupType.NAME,
					"In Progress", "id");
			String waitingStatusId = formDao.getFromInfoLookup("RequestStatus", LookupType.NAME,
					"Waiting", "id");
			if(requestList!=null && !requestList.isEmpty()){
			for (String requestId : requestList) {
				String table = "FG_S_Request_PIVOT";
				String ExternalRequestId = formDao.getFromInfoLookup("UNITS", LookupType.NAME, "External Tasks", "id");
				List<String> colList = Arrays.asList("requeststatus_id");
				String sql_ = "update " + table + " set requestStatus_id = '" + inProgressStatusId
						+ "' where DESTUNIT_ID!='" + ExternalRequestId + "' and formid = '" + requestId + "' and requestStatus_id= '"+waitingStatusId+"'";
				String res = formSaveDao.updateStructTableByFormId(sql_, table, colList, formId);
				if(res.equals("1")){
					String expNumberId =  formDao.getFromInfoLookup("Experiment", LookupType.ID,
							formId, "name");
					String reqNumberId=formDao.getFromInfoLookup("Request", LookupType.ID,
							requestId, "name");
					generalUtilLogger.logWriter(LevelType.Other,
							"The status of request " +reqNumberId+ " changed from 'Waiting' to 'In Progress' because it was related to experiment "+expNumberId ,
							ActivitylogType.RequestStatusChanged, requestId);
				}
			}
			
			//insert the requested samples to the samples table in the destination experiment
			String sessionId_ = generalUtilFormState.getSessionId(formId);
			List<String> sampleIdList = generalDao
					.getListOfStringBySql("select distinct SAMPLEID from FG_S_SAMPLEDATAREF_ALL_V where PARENTID in ("
							+ (requestList.isEmpty() ? "''" : generalUtil.listToCsv(requestList))
							+ ") and SESSIONID is null");
			if(sampleIdList!= null && !sampleIdList.isEmpty()){
			    formDao.insertToSelectTable("SAMPLESELECT", formId, "SAMPLETABLE", sampleIdList, false, userId, sessionId_);
			}
			}
		}

		public void insertSelfTestWebixToResulRef(String resultValue, String parent_id) {
			List<Map<String, String>> resultList = saveValuesAsResultMap(resultValue, parent_id);
			insertToResulRef(resultList, parent_id);
		}
		
		private List<Map<String, String>> saveValuesAsResultMap(String resultValue, String parentId) {
			// TODO Auto-generated method stub
			if (generalUtil.getNull(generalUtil.getJsonValById(resultValue, "rowsData")).replaceAll("\\{|\\}|\\[|\\]", "")
					.trim().isEmpty()) {
				return null;
			}
			List<Map<String, String>> resultList = new ArrayList<>();
			JSONArray resultsList = new JSONArray(generalUtil.getJsonValById(resultValue, "rowsData"));//data that will be shown in the table
			int size = resultsList.length();
			for (int i = 0; i < size; i++) {
				Map<String, String> resultMap = new HashMap<>();
				String resultRow = resultsList.get(i).toString();
				if (resultRow.replaceAll("\\{|\\}", "").trim().isEmpty()) {
					continue;
				}
				resultMap.put("MATERIAL_ID", generalUtil.getJsonValById(resultRow, "invitem_material"));
				resultMap.put("INSTRUMENT_ID", generalUtil.getJsonValById(resultRow, "instrument"));
				resultMap.put("optionalMaterialName", generalUtil.getJsonValById(resultRow, "optional_material_name"));
				resultMap.put("formId", generalUtil.getJsonValById(resultRow, "resultref_id"));
				resultMap.put("RESULTTYPE_ID", generalUtil.getJsonValById(resultRow, "result_type"));
				resultMap.put("value", generalUtil.getJsonValById(resultRow, "result_value")); // from non numeric self test type (not in use) in the new selftest screen (with one webix table)
				resultMap.put("NONNUMERICRESULT", generalUtil.getJsonValById(resultRow, "non_numeric_result"));
				resultMap.put("area", generalUtil.getJsonValById(resultRow, "area"));
				resultMap.put("retentionTime", generalUtil.getJsonValById(resultRow, "retention_time"));
				resultMap.put("normalization", generalUtil.getJsonValById(resultRow, "normalization"));
				resultMap.put("comments", generalUtil.getJsonValById(resultRow, "comments"));
				resultMap.put("tableType", generalUtil.getJsonValById(resultRow, "selftest_type"));
				resultMap.put("parentId", parentId);

				if (resultMap.get("RESULTTYPE_ID").isEmpty()) {
					continue;
				}

				resultList.add(resultMap);
			}
			return resultList;
		}
		
		private void insertToResulRef(List<Map<String, String>> resultList, String parentId) {
			String formIdCsv = "";
			if (resultList == null) {
				return;
			}
			for (Map<String, String> resultMap : resultList) {
				if (!resultMap.get("formId").isEmpty()) {
					formIdCsv = formIdCsv + "," + resultMap.get("formId");
				}
			}
			//concatenates the formId's of the result records
			formIdCsv = formIdCsv.replaceFirst(",", "");
			if (formIdCsv.isEmpty()) {
				return;
			}

			//delete the rows in the resultref that were deleted from the webix
			String sql = "Select FORMID from fg_s_resultref_all_v where PARENTID = '" + parentId
					+ "' and SESSIONID is null " + "MINUS" + "    select regexp_substr(col, '[^,]+', 1, level) result"
					+ "    from (select '" + formIdCsv + "' col from dual)"
					+ "    connect by level <= length(regexp_replace(col, '[^,]+')) + 1 ";
			List<String> resultRefIdToDelete = generalDao.getListOfStringBySql(sql);
			String resultRefIdToDeleteCsv = resultRefIdToDelete.toString().replaceAll("\\[|\\]|\\s*", "");
			if (!resultRefIdToDelete.isEmpty()) {
				formSaveDao.updateSingleStringInfoNoTryCatch(
						"delete from FG_S_RESULTREF_PIVOT where FORMID in (" + resultRefIdToDeleteCsv + ")");//TODO: override the delete with auditrail to delete several rows simultaniously
			}
			//add the rows that were added in the webix to the resultRef table
			sql = "Select regexp_substr(col, '[^,]+', 1, level) result" + "    from (select '" + formIdCsv
					+ "' col from dual)" + "    connect by level <= length(regexp_replace(col, '[^,]+')) + 1 " + "MINUS"
					+ "    select FORMID from fg_s_resultref_all_v where PARENTID = '" + parentId
					+ "' and SESSIONID is null ";
			List<String> resultRefIdToAdd = generalDao.getListOfStringBySql(sql);
			String userId = generalUtil.getSessionUserId();
			if (!resultRefIdToAdd.isEmpty()) {
				String sessionId = generalUtilFormState.checkAndReturnSessionId("ResultRef", parentId);

				for (Map<String, String> resultMap : resultList) {
					if (resultRefIdToAdd.contains(resultMap.get("formId"))) {
						//insert the current webix row to the resultref Table
						String elementNamesCSV = "";
						String elementValuesCSV = "";
						for (Map.Entry<String, String> entry : resultMap.entrySet()) {
							elementNamesCSV += ("," + entry.getKey());
							elementValuesCSV += (","
									+ (entry.getValue().isEmpty() ? "null" : "'" + entry.getValue() + "'"));
						}
						sql = "insert into FG_S_RESULTREF_PIVOT"
								+ " (CREATION_DATE,CREATED_BY,TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMCODE,FORMCODE_ENTITY"
								+ elementNamesCSV + ",AREAUOM_ID,RETENTIONUOM_ID,NORMALUOM_ID)" + " VALUES (SYSDATE,'"
								+ userId + "',SYSDATE,'" + userId + "'," + sessionId + ",1,'ResultRef','ResultRef'"
								+ elementValuesCSV
								+ ",fg_get_Uom_by_uomtype('area'),fg_get_Uom_by_uomtype('time','min'),fg_get_Uom_by_uomtype('percentage','%'))";
						formSaveDao.insertStructTableByFormId(sql, "FG_S_RESULTREF_PIVOT", resultMap.get("formId"));
					}
				}
			}
			//updates the records that were not added or deleted
			for (Map<String, String> resultMap : resultList) {
				if (!resultMap.get("formId").isEmpty() && !resultRefIdToAdd.contains(resultMap.get("formId"))
						&& !resultRefIdToDelete.contains(resultMap.get("formId"))) {//the record should be updated
					//update the current webix row in the resultref Table
					String setElementNameValue = "";
					String elementNamesCSV = "";
					for (Map.Entry<String, String> entry : resultMap.entrySet()) {
						if (!entry.getKey().equals("formId") && !entry.getKey().equals("parentId")
								&& !entry.getKey().equals("tableType")) {//update the relevant element
							setElementNameValue += ("," + entry.getKey() + "="
									+ (entry.getKey().isEmpty() ? "null" : "'" + entry.getValue() + "'"));
							elementNamesCSV += ("," + entry.getKey());
						}
					}
					elementNamesCSV = elementNamesCSV.replaceFirst(",", "");
					sql = "update FG_S_RESULTREF_PIVOT" + " SET TIMESTAMP = SYSDATE,CHANGE_BY = '" + userId + "'"
							+ setElementNameValue + "where FORMID = '" + resultMap.get("formId") + "'";
					formSaveDao.updateStructTableByFormId(sql, "FG_S_RESULTREF_PIVOT",
							Arrays.asList("Timestamp", "change_by", elementNamesCSV), resultMap.get("formId"));
				}
			}
		}

		public void onSelfTestNewEvent(String userId, String formId, String parentId, Map<String, String> paramMapReturn) {
				if(!generalUtil.getNull(parentId).equals("")) {
					if(paramMapReturn == null) {
						paramMapReturn =  new HashMap<String,String>();
					}
					paramMapReturn.put("SELFTESTTYPENAME_WITH_DEFAULT", "Internal Analytical");
					
					//insert all the samples connected to the action to the selftest
					List<String> actionSamplesList = generalDao
							.getListOfStringBySql("select SAMPLE_ID from fg_s_sampleselect_all_v" + " where parentId = '"
									+ parentId + "'" + generalUtilFormState
									.getWherePartForTmpData("sampleselect", parentId));
					
					String sessionId_ = generalUtilFormState.getSessionId(formId);
					formDao.insertToSelectTable("SampleSelect", formId, "SAMPLETABLE", actionSamplesList, false, userId,sessionId_);
					/*String lastSampleId = generalDao.selectSingleStringNoException(
						"select t.SAMPLE_ID" "select distinct last_value (s.SAMPLE_ID) over (partition by t.PARENTID order by s.CREATION_DATE)" 
								+ " from (select distinct s.SAMPLE_ID,s.CREATION_DATE from "
								+ "(select * from fg_s_sampleselect_all_v t" + " where 1=1 "
								+ generalUtilFormState.getWherePartForTmpData("SAMPLESELECT", elementValueMap.get("ACTION_ID"))
								+ " ) t ,fg_s_sample_v s" + " where t.SAMPLE_ID = s.SAMPLE_ID " + " and t.parentid = '"
								+ elementValueMap.get("ACTION_ID") + "' order by s.CREATION_DATE desc) t "
								+ " where rownum<=1");*/
					String sampleList = generalDao.selectSingleStringNoException(
							"select sampletable" + " from fg_s_sampleselect_v where parentId = '"
									+ parentId + "'" + generalUtilFormState
									.getWherePartForTmpData("sampleselect", parentId));
					
					if (sampleList != null && !sampleList.isEmpty()) {
//								Map<String,String> elementValueMap = new HashMap<String,String>();
						String firstSampleId = sampleList.split(",")[0];
						String firstSampleNumber = formDao.getFromInfoLookup("SAMPLE", LookupType.ID, firstSampleId, "name");
//								elementValueMap.put("sampleId", firstSampleId);
//								elementValueMap.put("sampleNumber", firstSampleNumber);
//								System.out.println("sampleId=" + firstSampleId + ", sampleNumber=" + firstSampleNumber); 
						paramMapReturn.put("ONINIT_FIRSTSAMPLEID", firstSampleId);
						paramMapReturn.put("ONINIT_FIRSTSAMPLENUMBER", firstSampleNumber);
						
						
						//if added a sample from sample search(not from the table) then add it to the sample table
						formDao.insertToSelectTable("SampleSelect", formId, "SAMPLETABLE", Arrays.asList(firstSampleId),
								true, userId, sessionId_);
					}
					
					//*** from post-save selftestmain event...
					//init first selftest status to active - part of 2453 (made also to stepfr - formulation)
//						String stActiveStatusId = formDao.getFromInfoLookup("SelfTestStatus", LookupType.NAME, "Active", "ID");
//						String sql_ = "update fg_s_SelfTest_pivot t set t.status_id ='" + stActiveStatusId + "' where formid = '" + formId + "'";
//						formSaveDao.updateStructTableByFormId(sql_,
//								"fg_s_selftest_pivot", Arrays.asList("STATUS_ID"), formId);
				} else {
					//TODO THROW EXCEPTION????
				}
			
		}

	/**
	 * 
	 * @param formCode form code of the ref / select / attach popup form
	 * @param parentId - the form id of the main form
	 * @param clearColumnName editable select csv column or null in other tables
	 * @param userId
	 */
	public void cleanRefTableByFormCode(String formCode, String parentId, String userId, String clearColumnName) {
		String sql = "";
		String sessionId = "";
		String colList = "";
		String colListVal = "";
		String tableName = "fg_s_" + formCode + "_pivot";
		String update = "";

		//get session
		sessionId = generalUtil.getNull(generalUtilFormState.checkAndReturnSessionId(formCode, parentId));
		//delete rows entered in this session (by this user between saves)
		if (!sessionId.equals("")) {
			sql = "delete from " + tableName + " where PARENTID = '" + parentId + "' and sessionid ='" + sessionId + "'";
		}
		update = formSaveDao.updateSingleStringInfo(sql);
		
		//change columns for insert sql - we copy last saved data with active = 0 and session = this session id and the (the form save will make it constant if the user will click on save)
		colList = "," + generalDao.getTableColCsv(tableName) + ",";
		if(clearColumnName != null) {
			colListVal = colList.replace("ACTIVE", "1").replace("CHANGE_BY", userId).replace("TIMESTAMP", "sysdate")
					.replace("CREATION_DATE", "sysdate").replace("CREATED_BY", userId).replace("SESSIONID", "'" + sessionId + "'").replace(clearColumnName, "null");
		} else {
			colListVal = colList.replace("ACTIVE", "0").replace("CHANGE_BY", userId).replace("TIMESTAMP", "sysdate")
					.replace("CREATION_DATE", "sysdate").replace("CREATED_BY", userId).replace("SESSIONID", "'" + sessionId + "'");
		}
		
		sql = "insert into " + tableName + " (" + colList.substring(1, colList.length() - 1) + ") select "
				+ colListVal.substring(1, colListVal.length() - 1)
				+ " from " + tableName + " where parentid = '" + parentId + "' and sessionid is null and active = 1";
		formSaveDao.insertStructTableByFormId(sql, tableName, parentId); 
		
	}
	
	/**
	 * copy from FormApiService
	 * @param stateKey
	 * @param parentFormId
	 * @param formCode
	 * @return
	 */
	public String getNewAvailableFormListById(long stateKey, String parentFormId, String formCode) {
		String parentFormCode = "";
		String userId = "";
		String toReturn = "";

		userId = generalUtil.getSessionUserId();
		if (generalUtil.getNull(parentFormId).equals("") || generalUtil.getNull(parentFormId).equals("-1")) {
			if (!generalUtilPermission.isUserInSchemeByCrudl(formCode, userId, "Create")) {
				return ""; //create not allowed
			} else {
				toReturn = formCode;
				return toReturn;
			}
		}

		Map<String, String> hmReportParameterList = new HashMap<String, String>();
		parentFormCode = formDao.getFormCodeBySeqId(parentFormId);

		//formService.initFormParam(stateKey, parentFormCode, parentFormId, userId, new HashMap<String, String[]>()); ...copy also this part from formService (to avoid circular reference) ->
		long start = System.currentTimeMillis();
		generalUtilLogger.logWriter(LevelType.DEBUG,"Start initFormParam formCode=" + parentFormCode + ", formId=" + parentFormId,
				ActivitylogType.PerformanceJava, parentFormId);
		
		boolean isNewFormId = false;
		Map<String, String> outParamMap = new HashMap<String, String>();
		Map<String, String> lastSaveValMap = new HashMap<String, String>();
		generalUtilFormState.initForm(isNewFormId, lastSaveValMap, stateKey, parentFormCode, userId, parentFormId, "", "",
				new HashMap<String, String[]>(), outParamMap); 
		
		generalUtilLogger.logWriter(LevelType.DEBUG,"End initFormParam formCode=" + parentFormCode + ", formId=" + parentFormId,
				ActivitylogType.PerformanceJava, parentFormId);
		long time = System.currentTimeMillis() - start;
		generalDao.logMessage("################ INIT FORM PARAM TIME = " + time + "################ ");
		//....End!
		
		for (Map.Entry<String, String> entry : generalUtilFormState.getFormParam(stateKey, parentFormCode).entrySet()) {
			String rKey = entry.getKey().replace("$P{", "").replace("}", "");
			String rVal = entry.getValue();
			hmReportParameterList.put(rKey, rVal);
		}

		toReturn = integrationWF.getNewAvailableFormList(stateKey, parentFormCode, parentFormId, hmReportParameterList,
				formCode);
		return toReturn;
	}
	
	public void refreshResultMv(){
		generalDao.updateSingleString("begin dbms_mview.refresh('FG_I_RESUSINGTOUPDATE_MV'); end;");
	}
	
	public String getformNumberIDSearchReport(String formId){
		String formCodeEntity = formDao.getFormCodeEntityBySeqId("", formId);
		if (formCodeEntity.equals("InvItemBatch") || formCodeEntity.equals("Sample")) {
			String sql = "select formnumberid from fg_s_" + formCodeEntity + "_v where formId='" + formId + "'";
			String formNumberId = generalDao.selectSingleStringNoException(sql);
			return formNumberId;
		}
		
		return "";
	}
	
	/**
	 * Changes the status of all the steps under the experimentId from planned to active
	 * @param experimentId
	 * @param runNumber
	 * @param userId
	 * @param isCalledBystep- indicates whether the function is called by the experiment save or the step save 
	 * @return a map of fields to change in the callback
	 * @throws Exception
	 */
	public Map<String,String> updateStepsStatusToActive(String experimentId, String runNumber, String userId, boolean isCalledBystep) throws Exception
	{
		Map<String,String> toReturn = new HashMap<>();//stores the data that is changed in the current form and sets the in the callback of the client request
 		String stepActiveStatusId = formDao.getFromInfoLookup("STEPSTATUS", LookupType.NAME, "Active","id");
		String plannedRunStepsSql = "select t.STEP_ID from fg_s_step_all_v t where t.STEPSTATUSNAME = 'Planned' and nullif('" + runNumber + "',t.runnumber) is null  and t.EXPERIMENT_ID = '"+experimentId+"' order by t.FORMNUMBERID asc";
		List<String> listOfPlannedRunSteps = generalDao.getListOfStringBySql(plannedRunStepsSql);
		DateFormat dateFormat = new SimpleDateFormat(generalUtil.getUserDateFormatServer(), Locale.ENGLISH);
		Date date = new Date();
		String formCode = formDao.getFormCodeBySeqId(experimentId);
		String inventory_unfamiliar = get_inventory_unfamiliar_list(experimentId,listOfPlannedRunSteps, formCode,null,true);//experimentId,stepId, "Step",null
		if(!inventory_unfamiliar.isEmpty()){
			throw new Exception(inventory_unfamiliar.split("##")[0]);
		}
		for(String stepId:listOfPlannedRunSteps) {
			/*integrationValidation.validate(ValidationCode.INVALID_EXPERIMENT_NONFAMILIAR_STATUS, "Step", stepId,
					experimentId, new StringBuilder());*/
			/*String inventory_unfamiliar = get_inventory_unfamiliar_list(experimentId,stepId, "Step",null);
			if(!inventory_unfamiliar.isEmpty()){
				throw new Exception(inventory_unfamiliar);
			}*/
			
			//update step status to 'Active'
			generalUtilLogger.logWriter(LevelType.DEBUG,"set status to active and update actual start date of step "+stepId+"",
						!runNumber.isEmpty()?ActivitylogType.StartRun:ActivitylogType.ActivateStep, stepId);
			String sql_ = "update fg_s_step_pivot set laststatus_id = status_id, status_id = '"
					+ stepActiveStatusId + "',actualStartDate =to_char(sysdate,'"+generalUtil.getConversionDateFormat()+"'),CHANGE_BY='"+userId+"',TIMESTAMP = sysdate where formId = '" + stepId + "' and status_id!= '"
					+ stepActiveStatusId + "'";
			formSaveDao.updateSingleStringInfoNoTryCatch(sql_);

			List<String> descendingWorkups = generalDao.getListOfStringBySql("select distinct workup_id"
					+ " from fg_s_workup_v t," + " fg_s_workupstatus_v s," + " fg_s_action_v a"
					+ " where t.action_id = a.action_id" + " and t.status_id = s.workupstatus_id"
					+ " and s.workupstatusname = 'Planned'" + " and a.step_id = '" + stepId + "'");
			String activeStatus = formDao.getFromInfoLookup("WORKUPSTATUS", LookupType.NAME, "Active", "ID");
			for (String workup_id : descendingWorkups) {
				formSaveDao.updateStructTableByFormId("update fg_s_workup_pivot" + " set status_id = '" + activeStatus
						+ "' where formid = '" + workup_id + "'", "fg_s_workup_pivot", Arrays.asList("status_id"),
						workup_id);
			}
			
			if(isCalledBystep){
				//changes the experiment status to active
				Map<String,String> experimentData = generalDao.sqlToHashMap("select LASTSTATUS_ID,STATUS_ID from fg_s_experiment_v where formid = '"+experimentId+"'");
				
				sql_ = String.format(
						" update %1$s set ACTUALSTARTDATE = decode(ACTUALSTARTDATE,null,TO_CHAR(sysdate,'" + generalUtil.getConversionDateFormat() + "'),ACTUALSTARTDATE),"
						+"ACTUALSTARTTIMESTAMP = decode(ACTUALSTARTDATE,null,TO_CHAR(sysdate,'" + generalUtil.getConversionDateTimeFormat() + "'),ACTUALSTARTTIMESTAMP),"
						+"LASTSTATUS_ID = STATUS_ID , STATUS_ID =%2$s, LASTMODIFDATE = TO_CHAR(sysdate,'"
								+ generalUtil.getConversionDateFormat() + "'),timestamp=sysdate,change_by='"+userId+"'\n"
										+ " where FORMID =  %3$s and nvl(STATUS_ID,'%4$s') = '%4$s'",
						"FG_S_EXPERIMENT_PIVOT",
						formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.NAME, "Active", "id"),
						experimentId,
						formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.NAME, "Planned", "id"));
				List<String> colList = Arrays.asList("ACTUALSTARTDATE", "ACTUALSTARTTIMESTAMP", "LASTSTATUS_ID",
						"STATUS_ID", "LASTMODIFDATE","TIMESTAMP","CHANGE_BY");
				formSaveDao.updateStructTableByFormId(sql_, "FG_S_EXPERIMENT_PIVOT", colList,
						experimentId);
				
				if(experimentData!=null && !experimentData.isEmpty()){
					if(!experimentData.get("STATUS_ID").equals(formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.NAME, "Active", "id"))){
						toReturn.put("actualStartDate", dateFormat.format(date));
						//toReturn.put("actualStartTimeStamp", dateFormat.format(date));
						toReturn.put("lastChangeUserId", userId);
						toReturn.put("lastChangeDate", generalDao.selectSingleStringNoException("select  to_char(TIMESTAMP,'"+generalUtil.getConversionDateTimeSecondsFormat()+"') from fg_s_experiment_v where formid = '"+experimentId+"'"));
						toReturn.put("LASTSTATUS_ID", experimentData.get("STATUS_ID"));
						toReturn.put("STATUS_ID", formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.NAME, "Active", "id"));
						toReturn.put("lastModifDate", dateFormat.format(date));
						
					}
				}
			}
			
			if(!runNumber.isEmpty()){
				calculateRunStep(stepId,userId,experimentId,runNumber);
				
				//update mass balance limiting agent mole of the currently running step
				String 	limitingMole = generalDao.selectSingleStringNoException(
								"select fg_get_num_normal(MOLERATE,nvl(MOLERATEUOM_ID,fg_get_uom_by_uomtype('molRate','mole/min'))) from fg_s_materialref_v t where t.PARENTID = '"
										+ stepId + "' and t.LIMITINGAGENT = 1 and sessionid is null and active=1 and rownum = 1");
				if (limitingMole != null && !limitingMole.isEmpty()) {
					limitingMole = new BigDecimal(Double.parseDouble(limitingMole)).toString();
						formSaveDao.updateStructTableByFormId(
								"update fg_s_step_pivot set limitingAgentMole ='" + limitingMole
										+ "' where formId = '" + stepId + "'"
										+ " and nvl(chkManualUpdate,0) = 0",
								"fg_s_step_pivot", Arrays.asList("limitingAgentMole"), stepId);
				}
			}
			// check if last status is not active in the DB function CREATE_EXPERIMENT_MATERIAL_SS
			Map<String, String> map = new HashMap<String, String>();
			map.put("stepId_in", stepId);			
			generalDao.callPackageFunction("FG_ADAMA", "CREATE_EXPERIMENT_MATERIAL_SS", map);//create a snapshot for displaying the right tables in actual or planned state
		}
		return toReturn;
	}
	
	public void calculateRunStep(String stepId,String userId, String experimentId, String runNumber) throws Exception {
		
		//fixed bug 8278 - Conc.% of previous step should be copied to Purity of the Reactant
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
	    	    	sql = "update fg_s_materialref_pivot set PURITYINF = '"+concentration+"' where formid = '" + materilalRef_formId
							+ "'";
					formSaveDao.updateStructTableByFormId(sql, "fg_s_materialref_pivot", Arrays.asList("PURITYINF", "PURITYUOM_ID_INF"), materilalRef_formId);
	    	    }
			}
		}
		
		//LA
		String mainArg = "volumeRate";
		String materialref_id_LA = generalDao.selectSingleStringNoException("select distinct formId"
				+ " from fg_s_materialref_v m" + " where m.PARENTID in ("
				+ stepId + ")" + " and nvl(active,'1')='1' and sessionid is null and nvl(limitingagent,0) =1");
		Map<String, String> reactantLAElementValueMap = formDao.getFormElementValuesMap(materialref_id_LA,
				"MaterialRef");
		if(!generalUtil.getNull(reactantLAElementValueMap.get("volumeRate")).isEmpty()){
			mainArg = "volumeRate";
		}
		else if(!generalUtil.getNull(reactantLAElementValueMap.get("quantityRate")).isEmpty()){
			mainArg = "quantityRate";
		}else if(!generalUtil.getNull(reactantLAElementValueMap.get("moleRate")).isEmpty()){
			mainArg = "moleRate";
		}
		reactantLAElementValueMap.put("isRun", "1");
			recalculateReactant(materialref_id_LA, userId, new ArrayList<String>(),
					reactantLAElementValueMap,mainArg, new StringBuilder(), false);
		
        //reactant & solvent
		List<String> materialref_id_list = generalDao.getListOfStringBySql("select distinct formId"
				+ " from fg_s_materialref_v m" + " where m.PARENTID in ("
				+ stepId + ")" + " and nvl(active,'1')='1' and sessionid is null and nvl(limitingagent,0)=0 and tabletype<>'Product'");
		for (String materilalRef_formId : materialref_id_list) {
			Map<String, String> reactantElementValueMap = formDao.getFormElementValuesMap(materilalRef_formId,
					"MaterialRef");
			if(!generalUtil.getNull(reactantElementValueMap.get("volumeRate")).isEmpty()){
				mainArg = "volumeRate";
			}
			else if(!generalUtil.getNull(reactantElementValueMap.get("quantityRate")).isEmpty()){
				mainArg = "quantityRate";
			}else if(!generalUtil.getNull(reactantElementValueMap.get("moleRate")).isEmpty()){
				mainArg = "moleRate";
			}
			//if(reactantElementValueMap.get("quantityRate").isEmpty() || reactantElementValueMap.get("moleRate").isEmpty() || reactantElementValueMap.get("volumeRate").isEmpty()){
			reactantElementValueMap.put("isRun", "1");
				recalculateReactant(materilalRef_formId, userId, new ArrayList<String>(),
						reactantElementValueMap,mainArg, new StringBuilder(), false);
			}
			//}
		
		 //product
		materialref_id_list = generalDao.getListOfStringBySql("select distinct formId"
				+ " from fg_s_materialref_v m" + " where m.PARENTID in ("
				+ stepId + ")" + " and nvl(active,'1')='1' and sessionid is null and nvl(limitingagent,0)=0 and tabletype='Product'");
		for (String materilalRef_formId : materialref_id_list) {
			Map<String, String> reactantElementValueMap = formDao.getFormElementValuesMap(materilalRef_formId,
					"MaterialRef");
			reactantElementValueMap.put("isRun", "1");
				recalculateReactant(materilalRef_formId, userId, new ArrayList<String>(),
						reactantElementValueMap,mainArg, new StringBuilder(), false);
			}
	}
	
	public String get_inventory_unfamiliar_list(String experimentId, List<String> stepIdList,String formCode, String materialList,boolean isAlert){
       List<String> currentUserMaterialIdList = new ArrayList<String>();
		boolean isFamiliarAfterConfirm = false;
		boolean isCurrentUserTrained = true;
        String message = "";
		if (/* formCode.equals("ExperimentFor")|| */formCode.equals("StepFr")||formCode.equals("StepMinFr")){//TODO:remove it when the formulation familiarity validation would be defined
        	return "";
        }
		String userName = generalUtil.getSessionUserName();
		if (!userName.equals("system") && !userName.equals("admin")) {// yp 23012018 for ignore IS_INVENTORY_FAMILIAR check
			StringBuilder finalRes = new StringBuilder();
			Map<String, List<String>> resMap = new HashMap<String, List<String>>();
			for(String stepId:stepIdList){
				Map<String, String> map = new HashMap<>();
				map.put("experimentId_in", experimentId);
				map.put("stepId_in", stepId);//if it fired by the step save event the sends also the step id.
				map.put("formCode_in", formCode);
				//if(materialList!=null && !materialList.isEmpty()){
				map.put("materialList_in", materialList);
				//}
				//String isFamiliar = generalDao.callPackageFunction("FG_ADAMA", "IS_INVENTORY_FAMILIAR", map);
				String isFamiliar = generalDao.callPackageFunction("FG_ADAMA", "GET_INVENTORY_UNFAMILIAR_LIST", map);
				if (!generalUtil.getNull(isFamiliar).equals("")) {
					String[] materials = isFamiliar.split("<end>");//80670<user_list>Test Ami<end>197346<user_list>Test Ami,yulyag<end>
					for (String m : materials) {
						String material_id = m.split("<user_list>")[0];
						if(material_id.isEmpty()){
							continue;
						}
						String materialFormCode = formDao.getFormCodeEntityBySeqId("",material_id);
						String materialName = formDao.getFromInfoLookup(materialFormCode, LookupType.ID, material_id, "name");
						String[] userList = m.split("<user_list>")[1].split(",");
						for (String user : userList) {
							if(!resMap.containsKey(user)){
								resMap.put(user,new ArrayList<String>());
							}
							String materialLink = "<a href='#' onClick=\"checkAndNavigate(['" + material_id + "','"+materialFormCode+"'])\" >" + materialName
									+ "</a>";
							if(!resMap.get(user).contains(materialLink)) {
								resMap.get(user).add(materialLink);  
							}
							if(resMap.containsKey(userName)){
								currentUserMaterialIdList.add(material_id);
							}
						}
					}
				}
			}
			if(resMap.isEmpty()){
				return "";
			}

			//always display the logged-in user, the first in the list. 
			if(resMap.containsKey(userName)){
				isCurrentUserTrained = false;//adding the confirmation part only if the current user has not been trained
				if(resMap.size()==1){//the logged-in user is the only one that is not trained->after confirming the experiment/step should be changed to active
					isFamiliarAfterConfirm = true;
				}
				finalRes.append("</br>");
				finalRes.append(userName);
			    finalRes.append(": ");
			    String value = generalUtil.replaceLast(resMap.get(userName).toString().replaceFirst("\\[", ""), "]", "");
			    finalRes.append(value);
			    resMap.remove(userName);
			}
			for (Map.Entry<String, List<String>> m : resMap.entrySet()) {
				finalRes.append("</br>");
				finalRes.append(m.getKey());
			    finalRes.append(": ");
			    String value = generalUtil.replaceLast(m.getValue().toString().replaceFirst("\\[", ""), "]", "");
			    finalRes.append(value);
			}
			message = generalUtil.getSpringMessagesByKey("INVALID_EXPERIMENT_NONFAMILIAR_STATUS",  new Object[] { finalRes },
					"INVALID_EXPERIMENT_NONFAMILIAR_STATUS");
			if(!isCurrentUserTrained && !isAlert){//if the message comes due to experiment changing,then the message should appear as an alert and not a confirmation 
				message+="</br>Would you like to add your name automatically, to the \"Trained\" list for all the above materials/instruments?";
			}
		
			return message + "##" + generalUtil.listToCsv(currentUserMaterialIdList) + "##" +isCurrentUserTrained+ "##" + isFamiliarAfterConfirm;
		}
		return "";
	}
	
	public void trainedUser(String materialIds, String userId) {
		String[] materialIdList = materialIds.split(",");
		String type = "";

		for (String material_id : materialIdList) {
			String isUserTrained = generalDao
					.selectSingleStringNoException("select formid from FG_S_TRAINING_PIVOT t where t.userid = '"
							+ userId + "' and t.parentid = '" + material_id + "' and rownum<=1");
			if (generalUtil.getNull(isUserTrained).isEmpty()) {

				String newFormId = formSaveDao.getStructFormId("FG_S_TRAINING_PIVOT");
				String materialFormCode = formDao.getFormCodeBySeqId(material_id);
				if (materialFormCode.equals("InvItemMaterial") || materialFormCode.equals("InvItemMaterialFr")
						|| materialFormCode.equals("InvItemMaterialPr")) {
					type = "compound";
				} else if (materialFormCode.equals("InvItemInstrument")) {
					type = "equipments";
				} else if (materialFormCode.equals("InvItemColumn")) {
					type = "column";
				}
				DateFormat dateFormat = new SimpleDateFormat(generalUtil.getConversionDateFormat());
				Date date = new Date();
				String sql_ = "INSERT INTO FG_S_TRAINING_PIVOT "
						+ "(formid,timestamp,active,formCode,parentId,status,userid,dateofapproval,type,created_by,creation_date,formCode_entity ) "
						+ "VALUES " + "('" + newFormId + "',sysdate,'1','Training','" + material_id + "','Trained','"
						+ userId + "','" + dateFormat.format(date) + "','" + type + "','" + userId
						+ "',sysdate,'Training')";
				formSaveDao.insertStructTableByFormId(sql_, "FG_S_TRAINING_PIVOT", newFormId);
			}
		}
	}

	public JSONObject createNewStepIframeData(String experimentId, Map<String, String> elementValueMap) throws Exception {		
		String userId = generalUtil.getSessionUserId();
		Map<String, String> map = new HashMap<String, String>();
		
		//check that filler exist and check the data validity
		integrationValidation.validate(ValidationCode.ALERT_NEW_FORMULATION_STEP, formDao.getFormCodeBySeqId(experimentId), experimentId, "", new StringBuilder());			
			try {
			map.put("EXPERIMENT_ID_IN", experimentId);	
			map.put("USER_ID_IN", userId);	
			map.put("DB_DATE_FORMAT_IN",savedConventionDbDateFormat);
			String newStepFormId = generalDao.callPackageFunction("FG_ADAMA", "INIT_STEPFR_DATA", map);
			//copy the products table of the last step into the material table of the new step	
			String sql = "select formid" 
						+ " from fg_s_step_v" 
						+ " where experiment_id = '" + experimentId + "'"
						+ " and formnumberid = " + "(select distinct max(to_number(formnumberId))"
												+ " from fg_s_step_v" + " where experiment_id = '" + experimentId + "'" 
												+ " and formId <> '"+ newStepFormId + "'"
												+")";//gets the previous step
			String prevStepFormId = generalDao.selectSingleStringNoException(sql);
			if(!prevStepFormId.isEmpty()){
				sql = "select composition_id from fg_s_composition_v\n"
						+ " where parentid = '"+prevStepFormId+"'"
						+ " and tabletype = 'productComposition'"
						+ " and rowtype <> 'Formulation'"
						+ " and active = 1"
						+ " and sessionid is null"
						+ " order by to_number(formid)";
				List<String> compositionsToClone = generalDao.getListOfStringBySql(sql);
	        	for(String composition_id:compositionsToClone){
	        		String cloneCompositionFormId = formSaveDao.cloneStructTable(composition_id);
	        		List<String> colList = Arrays.asList("parentId", "ACTIVE","SESSIONID","TABLETYPE","plannedToBatch","rowType","actual");
					sql = "update FG_S_composition_PIVOT set parentId = '" + newStepFormId + "',ACTIVE = 1, SESSIONID = null,TABLETYPE='stepComposition',plannedToBatch=null,rowtype = 'Step (Premix) material',actual=null where FORMID='"
							+ cloneCompositionFormId + "' ";
					formSaveDao.updateStructTableByFormId(sql, "FG_S_composition_PIVOT", colList,
							cloneCompositionFormId);
	        	}
			}
			
			String formulationType_id = formDao.getFromInfoLookup("Experiment", LookupType.ID, experimentId, "FORMULATIONTYPE_ID");
			String compositionType = formDao.getFromInfoLookup("FORMULATIONTYPE", LookupType.ID, formulationType_id, "COMPOSITIONTYPE") ;
			elementValueMap.put("EXPERIMENT_ID_IN", experimentId);	
			elementValueMap.put("COMPOSITIONTYPENAME", compositionType);
			elementValueMap.put("parentId", newStepFormId);
			elementValueMap.put("doCheckBalance", "1");
			String retVal = integrationCalc.doCalcComposition("MaterialTripletCalc", "checkBalance", "", "0", "", elementValueMap, null, null, "Composition", newStepFormId, userId, null);
			String warningToDisplay = "";
			if(retVal != null) {
				JSONObject retJson = new JSONObject(retVal);
				Iterator<String> keysItr = retJson.keys();
				while (keysItr.hasNext()) {
					String key = keysItr.next();
					JSONObject rowData = retJson.getJSONObject(key);
					if(rowData.has("warningMsg")){
						warningToDisplay = rowData.getString("warningMsg");
						break;
					}
				}
				if(!warningToDisplay.isEmpty()){
					integrationEvent.generalEvent(0, map, "StepFr", newStepFormId, userId, "0", "clearCompositionArgs");
				}
			}
			
			
			sql = "select step_id as ID ,stepname AS NAME from fg_s_step_v where step_id = '" + newStepFormId + "'";
			JSONObject returnJson =  generalDao.getJsonObjectBySqlSingleRow(sql);
			if(!warningToDisplay.isEmpty()){
				returnJson.put("warningToDisplay", warningToDisplay+"</br>The values are being zeroed");
			}
			if(returnJson == null) {
				throw new Exception("Error in step creation.");
			}
			return returnJson;
		} catch (Exception e) {
			throw new Exception("Error in step creation!");
		}
	}
	
	public String createNewStepOrganicData(String experimentId) {
		String userId = generalUtil.getSessionUserId();
		Map<String, String> map = new HashMap<String, String>();
		map.put("EXPERIMENT_ID_IN", experimentId);	
		map.put("USER_ID_IN", userId);	
		map.put("DB_DATE_FORMAT_IN",savedConventionDbDateFormat);
		String newStepFormId = generalDao.callPackageFunction("FG_ADAMA", "INIT_STEP_DATA", map);
		return newStepFormId;

	}

	public String createNewStepCPData(String experimentId) {
		String userId = generalUtil.getSessionUserId();
		Map<String, String> map = new HashMap<String, String>();
		map.put("EXPERIMENT_ID_IN", experimentId);	
		map.put("USER_ID_IN", userId);	
		map.put("DB_DATE_FORMAT_IN",savedConventionDbDateFormat);
		String newStepFormId = generalDao.callPackageFunction("FG_ADAMA", "INIT_STEPCP_DATA", map);
		return newStepFormId;
	}

public void buildRecipeMaterialFuncLog(String experiment_id,String userId,String compositionType,String density, String compositionsToValidate) throws Exception{
	generalDao.updateSingleStringNoTryCatch(" delete from fg_RECIPE_MATERIAL_FUNC_log\n");
	
	//enter the new data into the fg_RECIPE_MATERIAL_FUNC_LOG
	String dateTime = generalDao.selectSingleString("select to_char(sysdate,'"+generalUtil.getConversionDateTimeFormat()+"') from dual");
	String sql = "select distinct *"
			+ " from fg_s_composition_all_v\n"
			+ " where parentid = '"+experiment_id+"'\n"
			+ " and tabletype = 'expComposition'\n"
			+ " and active = 1\n"
			+(generalUtil.getNull(compositionsToValidate).isEmpty()?"": "and composition_id in ("+compositionsToValidate+")\n")
			+ generalUtilFormState.getWherePartForTmpData("composition", experiment_id);
	List<Map<String,Object>> compositionList = generalDao.getListOfMapsBySql(sql);
	for(Map<String,Object> compositionData:compositionList){
		String composition_id = compositionData.get("FORMID")!= null?compositionData.get("FORMID").toString():"";
		String invitemmaterial_id = compositionData.get("INVITEMMATERIAL_ID")!= null?compositionData.get("INVITEMMATERIAL_ID").toString():"";
		if(invitemmaterial_id.isEmpty()){
			continue;
		}
		String invitembatch_id = compositionData.get("BATCH_ID")!= null?compositionData.get("BATCH_ID").toString():"";
		invitembatch_id = invitembatch_id.equals("0")?"":invitembatch_id;
		String function_id = compositionData.get("MATERIALFUNCTION_ID")!= null?compositionData.get("MATERIALFUNCTION_ID").toString():"";
		String WW_P = compositionData.get("WW_P")!= null?compositionData.get("WW_P").toString():"";
		String rowType = compositionData.get("ROWTYPE")!= null?compositionData.get("ROWTYPE").toString():"";
		String filler = compositionData.get("FILLER")!= null?compositionData.get("FILLER").toString():"0";
		String is_activeIngredient = generalDao.selectSingleStringNoException("select distinct count(*) \n"
				+ "from fg_s_invitemmaterial_v t,\n"
				+ " fg_s_materialtype_v m "
				+ " where instr(','||t.MATERIALTYPE_ID||',',','||m.materialtype_id||',')>0\n"
				+ " and lower(m.MaterialTypeName) in lower('Active Ingredient')\n"
				+ " and t.invitemmaterial_id = '"+invitemmaterial_id+"'").equals("0")?"0":"1";//rowType.equals("Active Ingredient")?"1":"0";
		String purity = compositionData.get("PURITYINFO")!= null?compositionData.get("PURITYINFO").toString():"100";
		Deque<String> materialNestedList = new ArrayDeque<String>();
		
		if(WW_P.isEmpty()){
			String entityFormCode = formDao.getFormCodeBySeqId(invitemmaterial_id);
			String entityName = formDao.getFromInfoLookup(entityFormCode, LookupType.ID, invitemmaterial_id, "name");
			throw new Exception("The "+(rowType.contains("Recipe")?"Recipe ":"Material ")+entityName+" in the planned composition table has no w/w% value");
		}
		if(is_activeIngredient.equals("1")&&invitembatch_id.isEmpty()){
			String entityFormCode = formDao.getFormCodeBySeqId(invitemmaterial_id);
			String entityName = formDao.getFromInfoLookup(entityFormCode, LookupType.ID, invitemmaterial_id, "name");
			throw new Exception("No batch was selected for the AI material "+entityName+"");
		}
		if(is_activeIngredient.equals("1")){
			WW_P = String.valueOf(Double.parseDouble(WW_P)*Double.parseDouble(purity)/100);
		}
		sql = "insert into FG_RECIPE_MATERIAL_FUNC_LOG\n"
				+ " (root_id ,invitemmaterial_id ,function_id,ww_p ,general_relation_val\n"
				+ " ,composition_id ,root_composition_id ,invitembatch_id,is_active_ingredient\n"
				+ ",user_id ,timestamp ,tableType,rowType,filler,parentid)\n"
				+ " values('"+experiment_id+"','"+invitemmaterial_id+"','"+function_id+"'\n"
				+ ",'"+WW_P+"','100','"+composition_id+"','"+composition_id+"'\n"
				+ ",'"+invitembatch_id+"','"+is_activeIngredient+"','"+userId+"',to_date('"+dateTime+"','"+generalUtil.getConversionDateTimeSecondsFormat()+"')\n"
				+ ",'plannedComposition','"+rowType+"','"+filler+"','"+experiment_id+"')";
		
		
		switch (rowType){
			case "Step (Premix) material":
				generalDao.updateSingleStringNoTryCatch(sql);
				if(invitembatch_id.isEmpty()){//the premix material has to be composed of recipe or a batch. If no batch was chosen then it drills down to the recipe that connected to the material
					String recipe_id = formDao.getFromInfoLookup("invitemmaterial", LookupType.ID, invitemmaterial_id, "RECIPEFORMULATION_ID");
					if(recipe_id.isEmpty()){
						String materialName = formDao.getFromInfoLookup("invitemmaterial", LookupType.ID, invitemmaterial_id, "name");
						throw new Exception("Premix material "+materialName+" is not composed of a batch or a recipe,while a Premiix material has to be composed of one of them");
					} else {
						sql = "insert into FG_RECIPE_MATERIAL_FUNC_LOG\n"
							+ " (root_id ,invitemmaterial_id ,function_id,ww_p ,general_relation_val\n"
							+ " ,composition_id ,root_composition_id ,invitembatch_id,is_active_ingredient\n"
							+ ",user_id ,timestamp ,tableType,rowType,parentid,additionalinfo)\n"
							+ " values('"+experiment_id+"','"+recipe_id+"','"+function_id+"'\n"
							+ ",'"+WW_P+"','100','"+composition_id+"','"+composition_id+"'\n"
							+ ",'"+invitembatch_id+"','"+is_activeIngredient+"','"+userId+"',to_date('"+dateTime+"','"+generalUtil.getConversionDateTimeSecondsFormat()+"')\n"
							+ ",'plannedComposition','Recipe','"+invitemmaterial_id+"','the premix material "+invitemmaterial_id+" in the planned compositions has no connected batch.Then it drills down to the connected recipe')";
						generalDao.updateSingleStringNoTryCatch(sql);
						buildDeepRecipeLog(recipe_id,WW_P,composition_id,experiment_id,dateTime,materialNestedList,userId,1,"plannedComposition");
					}
				} else {
					buildDeepMaterialLog(invitembatch_id,WW_P,composition_id,experiment_id,dateTime,materialNestedList,userId,1,"plannedComposition");
				}
				break;
			case "Premix Recipe":
			case "Recipe":
				generalDao.updateSingleStringNoTryCatch(sql);
				buildDeepRecipeLog(invitemmaterial_id,WW_P,composition_id,experiment_id,dateTime,materialNestedList,userId,1,"plannedComposition");
				break;
			default:
				generalDao.updateSingleStringNoTryCatch(sql);
		}
	}
	
	if(compositionList.isEmpty()){
		return;
	}
	//update the filler according to the planned composition
	////get the filler material from the planned composition
	sql = "select distinct invitemmaterial_id\n"
		+ "		from fg_s_composition_v\n"
		+ "				where parentid = '"+experiment_id+"'\n"
		+ "  			and tabletype = 'expComposition'\n"
		+ " 			and active = 1\n"
		+ "				and nvl(filler,'0') = '1'\n"
		+ generalUtilFormState.getWherePartForTmpData("composition", experiment_id);
	String filler_materialId = generalDao.selectSingleStringNoException(sql);
	//update the materials in the log as filler or not,and calculate the rest arguments
	sql = "update FG_RECIPE_MATERIAL_FUNC_LOG t\n"
		+ " set FILLER = decode(t.invitemmaterial_id,'"+filler_materialId+"','1','0')\n"
		+ ",Ww_Grk = ww_p*10\n"
		+ (compositionType.equals("Liquid")?",wv_grl = ww_p*10*"+density:"")
		+ " where tabletype = 'plannedComposition'";
	generalDao.updateSingleStringNoTryCatch(sql);
}


public void buildImportedCompositionLog(String experiment_id, String compositionParent_id, String userId) throws Exception {	
	String dateTime = generalDao.selectSingleString("select to_char(sysdate,'"+generalUtil.getConversionDateTimeFormat()+"') from dual");
	String sql = "select distinct *"
			+ " from fg_s_composition_all_v\n"
			+ " where parentid = '"+compositionParent_id+"'\n"
			+ " and active = 1\n"
			+ " and sessionid is null";
	List<Map<String,Object>> compositionList = generalDao.getListOfMapsBySql(sql);
	for(Map<String,Object> compositionData:compositionList){
		String composition_id = compositionData.get("FORMID")!= null?compositionData.get("FORMID").toString():"";
		String invitemmaterial_id = compositionData.get("INVITEMMATERIAL_ID")!= null?compositionData.get("INVITEMMATERIAL_ID").toString():"";
		if(invitemmaterial_id.isEmpty()){
			continue;
		}
		String function_id = compositionData.get("MATERIALFUNCTION_ID")!= null?compositionData.get("MATERIALFUNCTION_ID").toString():"";
		String WW_P = compositionData.get("WW_P")!= null?compositionData.get("WW_P").toString():"";
		String rowType = compositionData.get("ROWTYPE")!= null?compositionData.get("ROWTYPE").toString():"";
		String purity = compositionData.get("PURITYINFO")!= null?compositionData.get("PURITYINFO").toString():"100";
		String assay = compositionData.get("ASSAY")!= null?compositionData.get("ASSAY").toString():"100";
		//String filler = compositionData.get("FILLER")!= null?compositionData.get("FILLER").toString():"0";
		String is_activeIngredient  =  generalDao.selectSingleStringNoException("select distinct count(*) \n"
				+ "from fg_s_invitemmaterial_v t,\n"
				+ " fg_s_materialtype_v m "
				+ " where instr(','||t.MATERIALTYPE_ID||',',','||m.materialtype_id||',')>0\n"
				+ " and lower(m.MaterialTypeName) in lower('Active Ingredient')\n"
				+ " and t.invitemmaterial_id = '"+invitemmaterial_id+"'").equals("0")?"0":"1";//rowType.equals("Active Ingredient")?"1":"0";
		String compositionParentFormCode = formDao.getFormCodeBySeqId(compositionParent_id);
		String relativePart = WW_P;
		if(WW_P.isEmpty()){
			String entityFormCode = formDao.getFormCodeBySeqId(invitemmaterial_id);
			String entityName = formDao.getFromInfoLookup(entityFormCode, LookupType.ID, invitemmaterial_id, "name");
			throw new Exception("The "+(rowType.contains("Recipe")?"Recipe ":"Material ")+entityName+" in the imported composition table has no w/w% value");
		}
		Double assayCalc = Double.parseDouble(compositionParentFormCode.equals("ExperimentFor")?purity:assay)*Double.parseDouble(WW_P)/100;
		WW_P = is_activeIngredient.equals("1")?
				String.valueOf(assayCalc)
						:WW_P;//in the imported composition spread- the assay value for the AI materials is taken from the assay column in the recipe(if the row is of the experiment composition-it's taken from the purity)
		Deque<String> materialNestedList = new ArrayDeque<String>();
		sql = "insert into FG_RECIPE_MATERIAL_FUNC_LOG\n"
				+ " (root_id ,invitemmaterial_id ,ww_p ,general_relation_val\n"
				+ " ,composition_id ,root_composition_id ,is_active_ingredient\n"
				+ ",user_id ,timestamp ,tableType,rowType,parentid)\n"
				+ " values('"+experiment_id+"','"+invitemmaterial_id+"'\n"
				+ ",'"+WW_P+"','100','"+composition_id+"','"+composition_id+"'\n"
				+ ",'"+is_activeIngredient+"','"+userId+"',to_date('"+dateTime+"','"+generalUtil.getConversionDateTimeSecondsFormat()+"')\n"
				+ ",'importedComposition','"+rowType+"','"+compositionParent_id+"')";
		
		switch (rowType){
			case "Step (Premix) material":
			case "Premix Material":
				generalDao.updateSingleStringNoTryCatch(sql);
				String recipe_id = formDao.getFromInfoLookup("invitemmaterial", LookupType.ID, invitemmaterial_id, "RECIPEFORMULATION_ID");//in the imported composition always drills down to the recipe that connected to the material
				if(recipe_id.isEmpty()){
					String materialName = formDao.getFromInfoLookup("invitemmaterial", LookupType.ID, invitemmaterial_id, "name");
					throw new Exception("Premix material "+materialName+" is not composed of a recipe,while a Premix material in the imported composition has to be composed of a material");
				} else {
					sql = "insert into FG_RECIPE_MATERIAL_FUNC_LOG\n"
						+ " (root_id ,invitemmaterial_id ,ww_p ,general_relation_val\n"
						+ " ,composition_id ,root_composition_id ,is_active_ingredient\n"
						+ ",user_id ,timestamp ,tableType,rowType,parentid,additionalinfo)\n"
						+ " values('"+experiment_id+"','"+recipe_id+"'\n"
						+ ",'"+WW_P+"','100','"+composition_id+"','"+composition_id+"'\n"
						+ ",'"+is_activeIngredient+"','"+userId+"',to_date('"+dateTime+"','"+generalUtil.getConversionDateTimeSecondsFormat()+"')\n"
						+ ",'importedComposition','Recipe','"+invitemmaterial_id+"','the premix material "+invitemmaterial_id+" in the imported compositions is composed of a recipe.Then it drills down to the connected recipe')";
					generalDao.updateSingleStringNoTryCatch(sql);
					buildDeepRecipeLog(recipe_id,relativePart,composition_id,experiment_id,dateTime,materialNestedList,userId,1,"importedComposition");
				}
				break;
			case "Premix Recipe":
			case "Recipe":
				generalDao.updateSingleStringNoTryCatch(sql);
				buildDeepRecipeLog(invitemmaterial_id,relativePart,composition_id,experiment_id,dateTime,materialNestedList,userId,1,"importedComposition");
				break;
			default:
				generalDao.updateSingleStringNoTryCatch(sql);
		}
	}
}
	
/**
 * if an external batch exists in the upper iterations then the AI are not taken in the current iteration
 * @param node_id
 * @param relativePart
 * @param root_composition_id
 * @param experiment_id
 * @param dateTime
 * @param materialNestedList
 * @param userId
 * @throws Exception
 */
private void buildDeepRecipeLog(String node_id,String relativePart, String root_composition_id, String experiment_id, String dateTime, Deque<String> materialNestedList, String userId,int level,String tableType) throws Exception {
	String externalBatchesCount = materialNestedList.isEmpty()?"0"
			:generalDao.selectSingleStringNoException("select count(*)\n"
		+ " from fg_s_invitembatch_all_v\n"
		+ " where sourcename = 'External'\n"
		+ " and formid in ("+generalUtil.listToCsv(new ArrayList(materialNestedList))+")");
	if(!materialNestedList.contains(node_id)){//this stack holds the parents of each iteration in the recursive function,in order to avoid a deadlock when the same node entered twice
		materialNestedList.push(node_id);
	} else {
		String recipeName = formDao.getFromInfoLookup("recipeformulation", LookupType.ID, node_id, "name");
		throw new Exception("Recipe "+recipeName+" has been defined recursively. Infinite loop might be executed");
	}
	String sql = "select *"
			+ " from fg_s_composition_all_v\n"
			+ " where parentid = '"+node_id+"'\n"
			+ " and tabletype = 'recipeComposition'\n"
			+ " and active = 1\n"
			+ " and sessionid is null";
	/*if(!externalBatchesCount.equals("0")){
		sql = "select distinct t.*"
				+ " from fg_s_composition_v t\n"
				+ " ,fg_s_invitemmaterial_v m\n"
				+ " ,fg_s_materialtype_v mt "
				+ " where t.parentid = '"+node_id+"'\n"
				+ " and tabletype = 'recipeComposition'\n"
				+ " and t.active = 1\n"
				+ " and instr(','||m.MATERIALTYPE_ID||',',','||mt.materialtype_id||',')>0\n"
				+ " and lower(mt.MaterialTypeName) not in lower('Active Ingredient')"
				+ " and t.invitemmaterial_id = m.invitemmaterial_id(+)"
				+ generalUtilFormState.getWherePartForTmpData("composition", node_id);
	}*/
	List<Map<String,Object>> compositionList = generalDao.getListOfMapsBySql(sql);
	for(Map<String,Object> compositionData:compositionList){
		String composition_id = compositionData.get("FORMID")!= null?compositionData.get("FORMID").toString():"";
		String invitemmaterial_id = compositionData.get("INVITEMMATERIAL_ID")!= null?compositionData.get("INVITEMMATERIAL_ID").toString():"";
		String invitembatch_id = compositionData.get("BATCH_ID")!= null?compositionData.get("BATCH_ID").toString():"";//if the recipe was created in the eln then the batch holds the batch that selected in the planned composition table(in the premix material row) of the experiment that created the recipe
		String function_id = compositionData.get("MATERIALFUNCTION_ID")!= null?compositionData.get("MATERIALFUNCTION_ID").toString():"";
		String WW_P = compositionData.get("WW_P")!= null?compositionData.get("WW_P").toString():"";
		String rowType = compositionData.get("ROWTYPE")!= null?compositionData.get("ROWTYPE").toString():"";
		String is_activeIngredient  =  generalDao.selectSingleStringNoException("select distinct count(*) \n"
				+ "from fg_s_invitemmaterial_v t,\n"
				+ " fg_s_materialtype_v m "
				+ " where instr(','||t.MATERIALTYPE_ID||',',','||m.materialtype_id||',')>0\n"
				+ " and lower(m.MaterialTypeName) in lower('Active Ingredient')\n"
				+ " and t.invitemmaterial_id = '"+invitemmaterial_id+"'").equals("0")?"0":"1";//rowType.equals("Active Ingredient")?"1":"0";
		String filler = level!=1?"0":(compositionData.get("FILLER")!= null?compositionData.get("FILLER").toString():"0");
		String assay = compositionData.get("ASSAY")!= null?compositionData.get("ASSAY").toString():"100";
		/*if(is_activeIngredient.equals("1")){//when the material is active ingredient then its value is taken from the assay result of the material(or from the purity value that has entered manually in the planned composition)
			String sqlString = "Select RESULT_VALUE\n"
					+ " from FG_S_sample_v t,\n"
					+ " FG_I_SELECTEDRESULTS_V r\n"
					+ " fg_s_invitembatch_v b "
					+ " where t.INVITEMMATERIAL_ID = r.RESULT_MATERIAL_ID"
					+ " and r.SAMPLE_ID ='" + frmId
					+ "' and RESULT_NAME = 'Assay'),t.PURITY) " 
					+ " from "
					+ "where  BATCH_ID = '" + invitembatch_id+"'";
		}*/
		if((!is_activeIngredient.equals("1") && !tableType.equals("importedComposition")
				|| tableType.equals("importedComposition")) && WW_P.isEmpty()){
			String entityFormCode = formDao.getFormCodeBySeqId(invitemmaterial_id);
			String entityName = formDao.getFromInfoLookup(entityFormCode, LookupType.ID, invitemmaterial_id, "name");
			String recipeName = formDao.getFromInfoLookup("recipeformulation", LookupType.ID, node_id, "name");
			throw new Exception("The "+(rowType.contains("Recipe")?"Recipe ":"Material ")+entityName+" in the recipe "+recipeName+" has no w/w% value");
		}
		

		String actualRelativePart = relativePart;
		if(is_activeIngredient.equals("1") && !tableType.equals("importedComposition")){
			if(!externalBatchesCount.equals("0")){
				continue;
			} else {
				WW_P="";
				//relativePart = "";
			}
		}else if(tableType.equals("importedComposition")){
			Double assayCalc = Double.parseDouble(assay)*Double.parseDouble(WW_P)/100;
			WW_P = is_activeIngredient.equals("1")?
					String.valueOf(assayCalc)
							:WW_P;//in the imported composition spread- the assay value for the AI materials is taken from the assay column in the recipe(if the row is of the experiment composition-it's taken from the purity)
		}
		
		sql = "insert into FG_RECIPE_MATERIAL_FUNC_LOG\n"
				+ " (root_id ,invitemmaterial_id ,function_id,ww_p ,general_relation_val\n"
				+ " ,composition_id ,root_composition_id ,invitembatch_id,is_active_ingredient\n"
				+ ",user_id ,timestamp ,tableType,rowType,filler,parentid)\n"
				+ " values('"+experiment_id+"','"+invitemmaterial_id+"','"+function_id+"'\n"
				+ ",'"+WW_P+"','"+relativePart+"','"+composition_id+"','"+root_composition_id+"'\n"
				+ ",'"+invitembatch_id+"','"+is_activeIngredient+"','"+userId+"',to_date('"+dateTime+"','"+generalUtil.getConversionDateTimeSecondsFormat()+"')\n"
				+ ",'"+tableType+"','"+rowType+"','"+filler+"','"+node_id+"')";
		
		switch (rowType){
		case "Step (Premix) material":
		case "Premix Material":
			actualRelativePart = String.valueOf(Double.parseDouble(WW_P)*Double.parseDouble(relativePart)/100);
			generalDao.updateSingleStringNoTryCatch(sql);
			if(invitembatch_id.isEmpty()||tableType.equals("importedComposition")){//the premix material has to be composed of recipe or a batch. If no batch was chosen then it drills down to the recipe that connected to the material
				String recipe_id = formDao.getFromInfoLookup("invitemmaterial", LookupType.ID, invitemmaterial_id, "RECIPEFORMULATION_ID");
				if(recipe_id.isEmpty()){
					String materialName = formDao.getFromInfoLookup("invitemmaterial", LookupType.ID, invitemmaterial_id, "name");
					throw new Exception("Premix material "+materialName+" is not composed of a batch or a recipe,while a Premiix material has to be composed of one of them");
				} else {
					sql = "insert into FG_RECIPE_MATERIAL_FUNC_LOG\n"
							+ " (root_id ,invitemmaterial_id ,function_id,ww_p ,general_relation_val\n"
							+ " ,composition_id ,root_composition_id ,invitembatch_id,is_active_ingredient\n"
							+ ",user_id ,timestamp ,tableType,rowType,parentid,additionalinfo)\n"
							+ " values('"+experiment_id+"','"+recipe_id+"','"+function_id+"'\n"
							+ ",'"+WW_P+"','"+relativePart+"','"+composition_id+"','"+root_composition_id+"'\n"
							+ ",'"+invitembatch_id+"','"+is_activeIngredient+"','"+userId+"',to_date('"+dateTime+"','"+generalUtil.getConversionDateTimeSecondsFormat()+"')\n"
							+ ",'"+tableType+"','Recipe','"+invitemmaterial_id+"','the premix material "+invitemmaterial_id+" in the recipe "+node_id+" has no connected batch.Then it drills down to the connected recipe')";
					generalDao.updateSingleStringNoTryCatch(sql);
					buildDeepRecipeLog(recipe_id,actualRelativePart,root_composition_id,experiment_id,dateTime,materialNestedList,userId,level+1,tableType);
				}
			} else {
				buildDeepMaterialLog(invitembatch_id,actualRelativePart,root_composition_id,experiment_id,dateTime,materialNestedList,userId,level+1,tableType);
			}
			break;
		case "Premix Recipe":
		case "Recipe":
			generalDao.updateSingleStringNoTryCatch(sql);
			actualRelativePart = String.valueOf(Double.parseDouble(WW_P)*Double.parseDouble(relativePart)/100);
			buildDeepRecipeLog(invitemmaterial_id,actualRelativePart,root_composition_id,experiment_id,dateTime,materialNestedList,userId,level+1,tableType);
			break;
		default:
			generalDao.updateSingleStringNoTryCatch(sql);
		}
	}
	if(level == 1 && tableType.equals("plannedComposition")){//in the spread of the imported composition , there's no need to update AI values, since it was already taken from the assay of the recipe composition
		updatePurityAndCorrectFiller(tableType, root_composition_id, experiment_id, relativePart);
	}
	materialNestedList.pop();
}

/**
 * The function loops through the component table.
 * In External batch - no premix is allowed. The components first taken and then the func loops through the compositions of the recipe that connected to the batch
 * In Internal batch - premixes may be in the components table. If it's a recipe then it drills down into the recipe. If it's a premix material, then it drills down into the batch of the material that appears in the experiment planned comppositions that created the batch(it is also stored in the component table) 
 * @param node_id
 * @param relativePart
 * @param root_composition_id
 * @param experiment_id
 * @param dateTime
 * @param materialNestedList
 * @param userId
 * @throws Exception
 */
private void buildDeepMaterialLog(String node_id,String relativePart, String root_composition_id, String experiment_id, String dateTime, Deque<String> materialNestedList, String userId,int level,String tableType) throws Exception {
	String externalBatchesCount = materialNestedList.isEmpty()?"0"
				:generalDao.selectSingleStringNoException("select count(*)\n"
			+ " from fg_s_invitembatch_all_v\n"
			+ " where sourcename = 'External'\n"
			+ " and formid in ("+generalUtil.listToCsv(new ArrayList(materialNestedList))+")");
	if(!materialNestedList.contains(node_id)){
		materialNestedList.push(node_id);
	} else {
		String invitemmaterial_id = formDao.getFromInfoLookup("invitembatch", LookupType.ID, node_id, "INVITEMMATERIAL_ID");
		String materialName = formDao.getFromInfoLookup("invitemmaterial", LookupType.ID, invitemmaterial_id, "name");
		throw new Exception("Premix material "+materialName+" has been defined recursively. Infinite loop might be executed");
	}
	String sourceName = formDao.getFromInfoLookup("invitembatch", LookupType.ID, node_id, "SOURCENAME");
	String recipe_id = formDao.getFromInfoLookup("invitembatch", LookupType.ID, node_id, "RECIPEFORMULATION_ID");
	if(!recipe_id.isEmpty()){//if the batch has a recipe then drills down into it,else spread its components
		if(tableType.equals("plannedComposition") && sourceName.equals("External") && externalBatchesCount.equals("0")){//if an external batch was already in the upper iterations then the AI materials not taken anymore(only in the first iteration where the external batch was found)
			String sql = "select distinct t.*"
						+ " from fg_s_materialcomponent_all_v t\n"
						+ " where t.parentid = '"+node_id+"'\n"
						+ " and t.active = 1\n"
						+ generalUtilFormState.getWherePartForTmpData("materialcomponent", node_id)
						+ " and t.MATERIAL_ID in\n"
						+ "(select m.invitemmaterial_id \n"
						+ " from fg_s_invitemmaterial_v m\n"
						+ " ,fg_s_materialtype_v mt \n"
						+ " where instr(','||m.MATERIALTYPE_ID||',',','||mt.materialtype_id||',')>0\n"
						+ " and lower(mt.MaterialTypeName) in lower('Active Ingredient'))";
						
			List<Map<String,Object>> componentList = generalDao.getListOfMapsBySql(sql);
			for(Map<String,Object> componentnData:componentList){
				String composition_id = componentnData.get("FORMID")!= null?componentnData.get("FORMID").toString():"";
				String invitemmaterial_id = componentnData.get("MATERIAL_ID")!= null?componentnData.get("MATERIAL_ID").toString():"";
				String invitembatch_id = componentnData.get("BATCH_ID")!= null?componentnData.get("BATCH_ID").toString():"";
				String function_id = componentnData.get("MATERIALFUNCTION_ID")!= null?componentnData.get("MATERIALFUNCTION_ID").toString():"";
				String WW_P = "";
				if(invitemmaterial_id.isEmpty()){
					continue;
				}
				String filler = level!=1?"0":(componentnData.get("FILLER")!= null?componentnData.get("FILLER").toString():"0");
				sql = "insert into FG_RECIPE_MATERIAL_FUNC_LOG\n"
						+ " (root_id ,invitemmaterial_id ,function_id,ww_p ,general_relation_val\n"
						+ " ,composition_id ,root_composition_id ,invitembatch_id,is_active_ingredient\n"
						+ ",user_id ,timestamp ,tableType,rowType,filler,parentid)\n"
						+ " values('"+experiment_id+"','"+invitemmaterial_id+"','"+function_id+"'\n"
						+ ",'"+WW_P+"','"+relativePart+"','"+composition_id+"','"+root_composition_id+"'\n"
						+ ",'"+invitembatch_id+"','1','"+userId+"',to_date('"+dateTime+"','"+generalUtil.getConversionDateTimeSecondsFormat()+"')\n"
						+ ",'"+tableType+"','Active Ingredient','"+filler+"','"+node_id+"')";
				generalDao.updateSingleStringNoTryCatch(sql);
			}
		}
		String sql = "insert into FG_RECIPE_MATERIAL_FUNC_LOG\n"
				+ " (root_id ,invitemmaterial_id ,function_id,ww_p ,general_relation_val\n"
				+ " ,composition_id ,root_composition_id ,invitembatch_id,is_active_ingredient\n"
				+ ",user_id ,timestamp ,tableType,rowType,parentid,additionalinfo)\n"
				+ " values('"+experiment_id+"','"+recipe_id+"',''\n"
				+ ",'"+relativePart+"','"+relativePart+"','"+node_id+"','"+root_composition_id+"'\n"
				+ ",'"+node_id+"','0','"+userId+"',to_date('"+dateTime+"','"+generalUtil.getConversionDateTimeSecondsFormat()+"')\n"
				+ ",'"+tableType+"','Recipe','"+node_id+"','the recipe is connected to the batch "+node_id+"')";
		generalDao.updateSingleStringNoTryCatch(sql);
		buildDeepRecipeLog(recipe_id,relativePart,root_composition_id,experiment_id,dateTime,materialNestedList,userId,level,tableType);//the level is not increased because the recipe here is the actual components of the batch
	} else {
	//if(sourceName.equals("Internal")){
		String sql = "select distinct *"
			+ " from fg_s_materialcomponent_all_v\n"
			+ " where parentid = '"+node_id+"'\n"
			+ " and active = 1\n"
			+ " and sessionid is null";
		/*if(!externalBatchesCount.equals("0")){//if an external batch was already in the upper iterations then the AI materials not taken anymore(only in the first iteration where the external batch was found)
			sql = "select distinct t.*"
			+ " from fg_s_materialcomponent_v t\n"
			+ " ,fg_s_invitemmaterial_v m\n"
			+ " ,fg_s_materialtype_v mt "
			+ " where t.parentid = '"+node_id+"'\n"
			+ " and t.active = 1\n"
			+ " and instr(','||m.MATERIALTYPE_ID||',',','||mt.materialtype_id||',')>0\n"
			+ " and lower(mt.MaterialTypeName) not in lower('Active Ingredient')"
			+ " and t.MATERIAL_ID = m.invitemmaterial_id(+)"
			+ generalUtilFormState.getWherePartForTmpData("materialcomponent", node_id);
		}*/
	//}
	/*else if(sourceName.equals("External") && externalBatchesCount.equals("0")){
		//when the batch is external takes the AI materials only and the rest materials are taken from the recipe that connected to the batch
		sql = "select t.*"
			+ " from fg_s_materialcomponent_v t\n"
			+ " ,fg_s_invitemmaterial_v m\n"
			+ " ,fg_s_materialtype_v mt "
			+ " where t.parentid = '"+node_id+"'\n"
			+ " and t.active = 1\n"
			+ " and instr(','||m.MATERIALTYPE_ID||',',','||mt.materialtype_id||',')>0\n"
			+ " and lower(mt.MaterialTypeName) in lower('Active Ingredient')"
			+ " and t.MATERIAL_ID = m.invitemmaterial_id"
			+ generalUtilFormState.getWherePartForTmpData("materialcomponent", node_id);
	}*/
	List<Map<String,Object>> componentList = generalDao.getListOfMapsBySql(sql);
	for(Map<String,Object> componentnData:componentList){
		String composition_id = componentnData.get("FORMID")!= null?componentnData.get("FORMID").toString():"";
		String invitemmaterial_id = componentnData.get("MATERIAL_ID")!= null?componentnData.get("MATERIAL_ID").toString():"";
		String invitembatch_id = componentnData.get("BATCH_ID")!= null?componentnData.get("BATCH_ID").toString():"";
		String function_id = componentnData.get("MATERIALFUNCTION_ID")!= null?componentnData.get("MATERIALFUNCTION_ID").toString():"";
		String WW_P = componentnData.get("CONCENTRATION")!= null?componentnData.get("CONCENTRATION").toString():"";
		if(invitemmaterial_id.isEmpty()){
			continue;
		}
		String filler = level!=1?"0":(componentnData.get("FILLER")!= null?componentnData.get("FILLER").toString():"0");
		//String WW_GRK = componentnData.get("WW_GRK")!= null?componentnData.get("WW_GRK").toString():"";
		//String WV_GRL = componentnData.get("WV_GRL")!= null?componentnData.get("WV_GRL").toString():"";
		//String rowType = componentnData.get("ROWTYPE")!= null?componentnData.get("ROWTYPE").toString():"";
		String is_activeIngredient  =  generalDao.selectSingleStringNoException("select distinct count(*) \n"
				+ "from fg_s_invitemmaterial_v t,\n"
				+ " fg_s_materialtype_v m "
				+ " where instr(','||t.MATERIALTYPE_ID||',',','||m.materialtype_id||',')>0\n"
				+ " and lower(m.MaterialTypeName) in lower('Active Ingredient')\n"
				+ " and t.invitemmaterial_id = '"+invitemmaterial_id+"'").equals("0")?"0":"1";//rowType.equals("Active Ingredient")?"1":"0";
		if(is_activeIngredient.equals("1")&&tableType.equals("plannedComposition")){//don't take the AI's in the deep iterations, but take them at the end of the spread from the purity column in the planned composition row
			if(!externalBatchesCount.equals("0")){
				continue;
			} else {
				WW_P="";
				//relativePart = "";
			}
		}
		String componentFormCode = formDao.getFormCodeEntityBySeqId("",invitemmaterial_id);
		String rowType = "";
		if(componentFormCode.equalsIgnoreCase("INVITEMMATERIAL")){
			String materialProtocol = formDao.getFromInfoLookup("invitemmaterial", LookupType.ID, invitemmaterial_id, "MATERIALPROTOCOLTYPE");
			if(materialProtocol.equals("Premix")){
				rowType = "Premix Material";
			}
		} else {//recipe
			rowType = "Recipe";//may be recipe or premix recipe. The difference has no meaning here
		}
		String actualRelativePart = WW_P.isEmpty()?"":String.valueOf(Double.parseDouble(WW_P)*Double.parseDouble(relativePart)/100);
		sql = "insert into FG_RECIPE_MATERIAL_FUNC_LOG\n"
			+ " (root_id ,invitemmaterial_id ,function_id,ww_p ,general_relation_val\n"
			+ " ,composition_id ,root_composition_id ,invitembatch_id,is_active_ingredient\n"
			+ ",user_id ,timestamp ,tableType,rowType,filler,parentid)\n"
			+ " values('"+experiment_id+"','"+invitemmaterial_id+"','"+function_id+"'\n"
			+ ",'"+WW_P+"','"+relativePart+"','"+composition_id+"','"+root_composition_id+"'\n"
			+ ",'"+invitembatch_id+"','"+is_activeIngredient+"','"+userId+"',to_date('"+dateTime+"','"+generalUtil.getConversionDateTimeSecondsFormat()+"')\n"
			+ ",'"+tableType+"','"+rowType+"','"+filler+"','"+node_id+"')";
		if(!is_activeIngredient.equals("1") && actualRelativePart.isEmpty()){
			String entityFormCode = formDao.getFormCodeBySeqId(invitemmaterial_id);
			String entityName = formDao.getFromInfoLookup(entityFormCode, LookupType.ID, invitemmaterial_id, "name");
			String batchName = formDao.getFromInfoLookup("invitembatch", LookupType.ID, node_id, "name");
			throw new Exception("The "+(rowType.contains("Recipe")?"Recipe ":"Material ")+entityName+" in the batch "+batchName+" has no concentraion value");
		}
		switch (rowType){
		case "Step (Premix) material":
		case "Premix Material":
			generalDao.updateSingleStringNoTryCatch(sql);
			if(invitembatch_id.isEmpty()){//the premix material has to be composed of recipe or a batch. If no batch was chosen then it drills down to the recipe that connected to the material
				recipe_id = formDao.getFromInfoLookup("invitemmaterial", LookupType.ID, invitemmaterial_id, "RECIPEFORMULATION_ID");
				if(recipe_id.isEmpty()){
					String materialName = formDao.getFromInfoLookup("invitemmaterial", LookupType.ID, invitemmaterial_id, "name");
					throw new Exception("Premix material "+materialName+" is not composed of a batch or a recipe,while a Premiix material has to be composed of one of them");
				} else {
					sql = "insert into FG_RECIPE_MATERIAL_FUNC_LOG\n"
						+ " (root_id ,invitemmaterial_id ,function_id,ww_p ,general_relation_val\n"
						+ " ,composition_id ,root_composition_id ,invitembatch_id,is_active_ingredient\n"
						+ ",user_id ,timestamp ,tableType,rowType,parentid,additionalinfo)\n"
						+ " values('"+experiment_id+"','"+recipe_id+"','"+function_id+"'\n"
						+ ",'"+WW_P+"','"+relativePart+"','"+composition_id+"','"+root_composition_id+"'\n"
						+ ",'"+invitembatch_id+"','"+is_activeIngredient+"','"+userId+"',to_date('"+dateTime+"','"+generalUtil.getConversionDateTimeSecondsFormat()+"')\n"
						+ ",'"+tableType+"','Recipe','"+invitemmaterial_id+"','the premix material "+invitemmaterial_id+" in the batch "+node_id+" has no connected batch.Then it drills down to the connected recipe')";
					generalDao.updateSingleStringNoTryCatch(sql);
					buildDeepRecipeLog(recipe_id,actualRelativePart,root_composition_id,experiment_id,dateTime,materialNestedList,userId,level+1,tableType);
				}
			} else {
				buildDeepMaterialLog(invitembatch_id,actualRelativePart,root_composition_id,experiment_id,dateTime,materialNestedList,userId,level+1,tableType);
			}
			break;
		case "Premix Recipe":
		case "Recipe":
			generalDao.updateSingleStringNoTryCatch(sql);
			buildDeepRecipeLog(invitemmaterial_id,actualRelativePart,root_composition_id,experiment_id,dateTime,materialNestedList,userId,level+1,tableType);
			break;
		default:
			generalDao.updateSingleStringNoTryCatch(sql);
		}
	}
	if(level == 1){
		updatePurityAndCorrectFiller(tableType, root_composition_id, experiment_id, relativePart);
	}
	/*if(sourceName.equals("External")){
		String recipe_id = formDao.getFromInfoLookup("invitembatch", LookupType.ID, node_id, "RECIPEFORMULATION_ID");
		if(materialNestedList.contains(recipe_id)){
			String recipeName = formDao.getFromInfoLookup("recipeformulation", LookupType.ID, recipe_id, "name");
			throw new Exception("Recipe "+recipeName+" has been defined recursively. Infinite loop might be executed");
		}
		sql = "insert into FG_RECIPE_MATERIAL_FUNC_LOG\n"
				+ " set root_id = '"+experiment_id+"',invitemmaterial_id = '"+recipe_id+"'\n"
				+ ",function_id = '',ww_p = '"+relativePart+"',WW_GRK = null,WV_GRL = null,general_relation_val = '"+relativePart+"'"
				+ ",composition_id = '"+node_id+"',root_composition_id = '"+root_composition_id+"'"
				+ ",invitembatch_id = '"+node_id+"',is_active_ingredient = '0'"
				+ ",user_id ='"+userId+"',timestamp = '"+dateTime+"'";
		generalDao.updateSingleStringNoTryCatch(sql);
		buildDeepRecipeLog(recipe_id,relativePart,root_composition_id,experiment_id,dateTime,materialNestedList,userId,level+1,tableType);
	}*/
	}
	materialNestedList.pop();
}

public void updatePurityAndCorrectFiller(String tableType,String root_composition_id,String experiment_id,String relativePart) throws Exception{
	//important!! a premix/recipe that contains AI materials(in the deep levels) has to be connected to a batch(in the composition row)
	String sql;
	//update the assay values of the Active Ingredient materials
	String purity = generalDao.selectSingleStringNoException("select  nvl(purity,100) from fg_s_composition_v\n"
			+ " where composition_id = '"+root_composition_id+"'\n"
			+ " and active =1"
			+ generalUtilFormState.getWherePartForTmpData("composition", experiment_id));
	if(purity.contains("{")){//supposed it's a json
		JSONObject purityJson = new JSONObject(purity);
		Iterator<String> materialResultPair = purityJson.keys();
		while (materialResultPair.hasNext()) {
			String materialId = materialResultPair.next();
		    String value = generalUtil.getJsonValById(purity, materialId);
		    sql = "update FG_RECIPE_MATERIAL_FUNC_LOG\n"
				+ " set ww_p = '"+value+"',general_relation_val = '"+relativePart+"'\n"
				+ " where invitemmaterial_id = '"+materialId+"'\n"
				+ " and is_active_ingredient = '1'\n"
				+ " and root_composition_id = '"+root_composition_id+"'\n"
				+ " and tableType = '"+tableType+"'";
		    generalDao.updateSingleStringNoTryCatch(sql);
		}
	}  else {
			sql = "select count(*)\n"
				+ "from FG_RECIPE_MATERIAL_FUNC_LOG\n"
				+ "where is_active_ingredient = '1'\n"
				+ "and root_composition_id = '"+root_composition_id+"'\n";
			String isCompositionContainsAI = generalDao.selectSingleStringNoException(sql);
			//if the current composition row has AI's but no purity has been entered-then checks if a batch been chosen
			if(!isCompositionContainsAI.equals("0")){
				Map<String,String> compositionData = generalDao.sqlToHashMap("select distinct invitembatch_id,invitemmaterial_id,rowType\n"
						+ "from FG_RECIPE_MATERIAL_FUNC_LOG\n"
						+ "where composition_id = '"+root_composition_id+"'\n"
						+ "and rownum<2");//gets the first root_composition_id that has been entered since there may be two rows of the same composition when a premix material drills down to its recipe
				String invitembatch_id = compositionData.get("INVITEMBATCH_ID").equals("0")?"":compositionData.get("INVITEMBATCH_ID");
				String invitemmaterial_id = compositionData.get("INVITEMMATERIAL_ID").equals("0")?"":compositionData.get("INVITEMMATERIAL_ID");
				String rowType = compositionData.get("ROWTYPE").equals("0")?"":compositionData.get("ROWTYPE");
				if(invitembatch_id.isEmpty()){
					String entityFormCode = formDao.getFormCodeBySeqId(invitemmaterial_id);
					String entityName = formDao.getFromInfoLookup(entityFormCode, LookupType.ID, invitemmaterial_id, "name");
					throw new Exception("No batch was selected for "+rowType+" "+entityName+".");
				}
			}
		}
	
	//checks if all the AI's have assay results
	String AI_noPurity = generalDao.selectSingleStringNoException(
			"select distinct listagg(m.invitemmaterialname,',')within group(order by invitemmaterialname)\n"
			+ " from FG_RECIPE_MATERIAL_FUNC_LOG t,\n"
			+ " fg_s_invitemmaterial_v m\n"
			+ " where t.is_active_ingredient = '1'\n"
			+ " and root_composition_id = '"+root_composition_id+"'\n"
			+ " and t.tableType = '"+tableType+"'\n"
			+ " and ww_p is null"
			+ " and t.invitemmaterial_id = m.invitemmaterial_id(+)");
	if(!generalUtil.getNull(AI_noPurity).isEmpty()){
		String invitembatch_id = generalDao.selectSingleStringNoException("select distinct invitembatch_id\n"
				+ "from FG_RECIPE_MATERIAL_FUNC_LOG\n"
				+ "where composition_id = '"+root_composition_id+"'");
		String batchName = formDao.getFromInfoLookup("invitembatch", LookupType.ID, invitembatch_id, "name");
		throw new Exception("Assay results are missing for batch NO. "+batchName+".");
	}
	
	//correct the filler and multiply in the relative part
	sql = "select distinct sum(ww_p*general_relation_val/100/("+relativePart+"/100))\n"//divide in the relative part in order to not take in a count the part in the planned composition, but the deeper ones only
		+ " from FG_RECIPE_MATERIAL_FUNC_LOG\n"
		+ " where root_composition_id = '"+root_composition_id+"'\n"
		+ " and tableType = '"+tableType+"'\n"
		+ " and nvl(filler,'0') <> '1'\n"
		+ " and nvl(rowType,'chemical') not in ('Premix Material','Recipe','Premix Recipe','Step (Premix) material')";
	String sumComponents = generalUtil.getEmpty(generalDao.selectSingleStringNoException(sql),"0");
	sql = "update FG_RECIPE_MATERIAL_FUNC_LOG\n"
		+ " set ww_p = 100-"+sumComponents+"\n"
		+ " where root_composition_id = '"+root_composition_id+"'\n"
		+ " and tableType = '"+tableType+"'\n"
		+ " and nvl(filler,'0') = '1'\n";
	generalDao.updateSingleStringNoTryCatch(sql);
}

/**
 * The function gets on its first iteration a batch that was chosen in the planned composition table(in the formulation experiment)
 * @param node_id
 * @param materialNestedList
 * @param level
 * @param materialResultPair
 * @throws Exception
 */
public void getMaterialResultList(String node_id,Deque<String> materialNestedList,int level,Map<String,String> materialResultPair) throws Exception{
	if(node_id.isEmpty()){
		return;
	}
	materialNestedList.push(node_id);
	String sourceName = formDao.getFromInfoLookup("invitembatch", LookupType.ID, node_id, "SOURCENAME");
	String componentFormCode = formDao.getFormCodeEntityBySeqId("",node_id);
	String sql = "";
	if(sourceName.equals("External")){
		//NOTE: the external batch component can't include premixes
		//when the batch is external takes the AI materials right from the components table(all the AI's in the recipe of the batch and in the premixes should be in the components,that's why there's no need to drill down for getting the AI's)
		sql = "select t.MATERIAL_ID,t.concentration"
			+ " from fg_s_materialcomponent_v t\n"
			+ " ,fg_s_invitemmaterial_v m\n"
			+ " ,fg_s_materialtype_v mt "
			+ " where t.parentid = '"+node_id+"'\n"
			+ " and t.active = 1\n"
			+ " and instr(','||m.MATERIALTYPE_ID||',',','||mt.materialtype_id||',')>0\n"
			+ " and lower(mt.MaterialTypeName) in lower('Active Ingredient')"
			+ " and t.MATERIAL_ID = m.invitemmaterial_id"
			+ " and t.active = '1'\n"
			+ " and t.sessionid is null";
		List<Map<String,Object>> materialList = generalDao.getListOfMapsBySql(sql);
		for(Map<String,Object> materialData:materialList){
			String material_id = materialData.get("MATERIAL_ID")!= null?materialData.get("MATERIAL_ID").toString():"";
			if(material_id.isEmpty()){
				continue;
			}
			String conc = materialData.get("CONCENTRATION")!= null?materialData.get("CONCENTRATION").toString():"";
			materialResultPair.put(material_id,conc);
		}
	}
	else if(sourceName.equals("Internal") || componentFormCode.equals("RecipeFormulation")){
		String recipe_id = formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, node_id, "RECIPEFORMULATION_ID");
		//if the node is a recipe-then gets its compositions
		//if the node is an internal batch and has no recipe - then gets its components
		if(recipe_id.isEmpty() || componentFormCode.equals("RecipeFormulation")){
			String view = componentFormCode.equals("RecipeFormulation")?"fg_S_composition_v":"fg_s_materialcomponent_v";			
			String materialColumnName = componentFormCode.equals("RecipeFormulation")?"INVITEMMATERIAL_ID":"MATERIAL_ID";
			String batchColumnName = componentFormCode.equals("RecipeFormulation")?"INVITEMBATCH_ID":"BATCH_ID";
		
			sql = "select *\n"
				+ " from "+view+"\n"
				+ " where parentid = '"+node_id+"'\n"
				+ " and active = '1'\n"
				+ " and sessionid is null";
			List<Map<String,Object>> componentList = generalDao.getListOfMapsBySql(sql);
			for(Map<String,Object> componentnData:componentList){
				String invitemmaterial_id = componentnData.get(materialColumnName)!= null?componentnData.get(materialColumnName).toString():"";
				String invitembatch_id = componentnData.get(batchColumnName)!= null?componentnData.get(batchColumnName).toString():"";
				if(invitemmaterial_id.isEmpty()){
					continue;
				}
				componentFormCode = formDao.getFormCodeEntityBySeqId("",invitemmaterial_id);
				if(componentFormCode.equalsIgnoreCase("INVITEMMATERIAL")){
					String is_activeIngredient  =  generalDao.selectSingleStringNoException("select distinct count(*) \n"
						+ "from fg_s_invitemmaterial_v t,\n"
						+ " fg_s_materialtype_v m "
						+ " where instr(','||t.MATERIALTYPE_ID||',',','||m.materialtype_id||',')>0\n"
						+ " and lower(m.MaterialTypeName) in lower('Active Ingredient')\n"
						+ " and t.invitemmaterial_id = '"+invitemmaterial_id+"'").equals("0")?"0":"1";//rowType.equals("Active Ingredient")?"1":"0";
					if(is_activeIngredient.equals("1")){
						materialResultPair.put(invitemmaterial_id, "");
					} else {
						String materialProtocol = formDao.getFromInfoLookup("invitemmaterial", LookupType.ID, invitemmaterial_id, "MATERIALPROTOCOLTYPE");
						if(materialProtocol.equals("Premix")){
							if(invitembatch_id.isEmpty()){
								//a premix has to be connected to some composition
								String materialRecipe = formDao.getFromInfoLookup("invitemmaterial", LookupType.ID, invitemmaterial_id, "RECIPEFORMULATION_ID");
								if(materialRecipe.isEmpty()){
									String materialName = formDao.getFromInfoLookup("invitemmaterial", LookupType.ID, invitemmaterial_id, "name");
									throw new Exception("No batch or recipe was selected for the premix material "+materialName);
								} else {
									if(materialNestedList.contains(materialRecipe)){
										String recipeName = formDao.getFromInfoLookup("recipeformulation", LookupType.ID, materialRecipe, "name");
										throw new Exception("Recipe "+recipeName+" has been defined recursively. Infinite loop might be executed");
									}
									getMaterialResultList(materialRecipe,materialNestedList,level+1,materialResultPair);
								}
							} else {
								if(materialNestedList.contains(invitembatch_id)){
									String batchName = formDao.getFromInfoLookup("invitembatch", LookupType.ID, invitembatch_id, "name");
									throw new Exception("Batch "+batchName+" has been defined recursively. Infinite loop might be executed");
								}
								getMaterialResultList(invitembatch_id,materialNestedList,level+1,materialResultPair);
							}
						}
					}
				} else {//recipe
					if(materialNestedList.contains(invitemmaterial_id)){
						String recipeName = formDao.getFromInfoLookup("recipeformulation", LookupType.ID, invitemmaterial_id, "name");
						throw new Exception("Recipe "+recipeName+" has been defined recursively. Infinite loop might be executed");
					}
					getMaterialResultList(invitemmaterial_id,materialNestedList,level+1,materialResultPair);
				}
			}
		} else {
			//the node is an internal batch that connected to a recipe->drills down into the recipe
			if(materialNestedList.contains(recipe_id)){
				String recipeName = formDao.getFromInfoLookup("recipeformulation", LookupType.ID, recipe_id, "name");
				throw new Exception("Recipe "+recipeName+" has been defined recursively. Infinite loop might be executed");
			}
			getMaterialResultList(recipe_id,materialNestedList,level+1,materialResultPair);
		}
	}
	if(level == 0 && sourceName.equals("Internal")){//in the external batch the value is taken from the concentration
		sql = "Select RESULT_MATERIAL_ID,RESULT_VALUE\n"
				+ " from FG_I_SELECTEDRESULTS_V r,\n"
				+ " FG_S_SAMPLE_V S\n " 
				+ "where S.batchdefinition = '1'\n"
				+ " and s.BATCH_ID = '"+node_id+"'\n"
				+ " and s.SAMPLE_ID = r.SAMPLE_ID"
				 + " and RESULT_NAME = 'Assay'";
		List<Map<String,Object>> materialResultList = generalDao.getListOfMapsBySql(sql);
		for(Map<String,Object> resultData:materialResultList){
			String materialId = resultData.get("RESULT_MATERIAL_ID")!=null?resultData.get("RESULT_MATERIAL_ID").toString():"";
			if(materialId.isEmpty()){
				continue;
			}
			String resultVal = resultData.get("RESULT_VALUE")!=null?resultData.get("RESULT_VALUE").toString():"";
			
			materialResultPair.put(materialId, resultVal);
		}
	}
	materialNestedList.pop();
}

public void getAIMaterialList(String node_id,Deque<String> materialNestedList,int level,Map<String,String> materialResultPair) throws Exception{
	if(node_id.isEmpty()){
		return;
	}
	materialNestedList.push(node_id);
	String sourceName = formDao.getFromInfoLookup("invitembatch", LookupType.ID, node_id, "SOURCENAME");
	String componentFormCode = formDao.getFormCodeEntityBySeqId("",node_id);
	String sql = "";
	if(sourceName.equals("External")){
		//NOTE: the external batch component can't include premixes
		//when the batch is external takes the AI materials right from the components table(all the AI's in the recipe of the batch and in the premixes should be in the components,that's why there's no need to drill down for getting the AI's)
		sql = "select t.MATERIAL_ID,t.concentration"
			+ " from fg_s_materialcomponent_v t\n"
			+ " ,fg_s_invitemmaterial_v m\n"
			+ " ,fg_s_materialtype_v mt "
			+ " where t.parentid = '"+node_id+"'\n"
			+ " and t.active = 1\n"
			+ " and instr(','||m.MATERIALTYPE_ID||',',','||mt.materialtype_id||',')>0\n"
			+ " and lower(mt.MaterialTypeName) in lower('Active Ingredient')"
			+ " and t.MATERIAL_ID = m.invitemmaterial_id"
			+ " and t.active = '1'\n"
			+ " and t.sessionid is null";
		List<Map<String,Object>> materialList = generalDao.getListOfMapsBySql(sql);
		for(Map<String,Object> materialData:materialList){
			String material_id = materialData.get("MATERIAL_ID")!= null?materialData.get("MATERIAL_ID").toString():"";
			if(material_id.isEmpty()){
				continue;
			}
			String conc = materialData.get("CONCENTRATION")!= null?materialData.get("CONCENTRATION").toString():"";
			materialResultPair.put(material_id,conc);
		}
	}
	else if(sourceName.equals("Internal") || componentFormCode.equals("RecipeFormulation")){
		String recipe_id = formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, node_id, "RECIPEFORMULATION_ID");
		//if the node is a recipe-then gets its compositions
		//if the node is an internal batch and has no recipe - then gets its components
		if(recipe_id.isEmpty() || componentFormCode.equals("RecipeFormulation")){
			String view = componentFormCode.equals("RecipeFormulation")?"fg_S_composition_v":"fg_s_materialcomponent_v";			
			String materialColumnName = componentFormCode.equals("RecipeFormulation")?"INVITEMMATERIAL_ID":"MATERIAL_ID";
			String batchColumnName = componentFormCode.equals("RecipeFormulation")?"INVITEMBATCH_ID":"BATCH_ID";
		
			sql = "select *\n"
				+ " from "+view+"\n"
				+ " where parentid = '"+node_id+"'\n"
				+ " and active = '1'\n"
				+ " and sessionid is null";
			List<Map<String,Object>> componentList = generalDao.getListOfMapsBySql(sql);
			for(Map<String,Object> componentnData:componentList){
				String invitemmaterial_id = componentnData.get(materialColumnName)!= null?componentnData.get(materialColumnName).toString():"";
				String invitembatch_id = componentnData.get(batchColumnName)!= null?componentnData.get(batchColumnName).toString():"";
				if(invitemmaterial_id.isEmpty()){
					continue;
				}
				componentFormCode = formDao.getFormCodeEntityBySeqId("",invitemmaterial_id);
				if(componentFormCode.equalsIgnoreCase("INVITEMMATERIAL")){
					String is_activeIngredient  =  generalDao.selectSingleStringNoException("select distinct count(*) \n"
						+ "from fg_s_invitemmaterial_v t,\n"
						+ " fg_s_materialtype_v m "
						+ " where instr(','||t.MATERIALTYPE_ID||',',','||m.materialtype_id||',')>0\n"
						+ " and lower(m.MaterialTypeName) in lower('Active Ingredient')\n"
						+ " and t.invitemmaterial_id = '"+invitemmaterial_id+"'").equals("0")?"0":"1";//rowType.equals("Active Ingredient")?"1":"0";
					if(is_activeIngredient.equals("1")){
						materialResultPair.put(invitemmaterial_id, "");
					} else {
						String materialProtocol = formDao.getFromInfoLookup("invitemmaterial", LookupType.ID, invitemmaterial_id, "MATERIALPROTOCOLTYPE");
						if(materialProtocol.equals("Premix")){
							if(invitembatch_id.isEmpty()){
								//a premix has to be connected to some composition
								String materialRecipe = formDao.getFromInfoLookup("invitemmaterial", LookupType.ID, invitemmaterial_id, "RECIPEFORMULATION_ID");
								if(materialRecipe.isEmpty()){
									String materialName = formDao.getFromInfoLookup("invitemmaterial", LookupType.ID, invitemmaterial_id, "name");
									throw new Exception("No batch or recipe was selected for the premix material "+materialName);
								} else {
									if(materialNestedList.contains(materialRecipe)){
										String recipeName = formDao.getFromInfoLookup("recipeformulation", LookupType.ID, materialRecipe, "name");
										throw new Exception("Recipe "+recipeName+" has been defined recursively. Infinite loop might be executed");
									}
									getAIMaterialList(materialRecipe,materialNestedList,level+1,materialResultPair);
								}
							} else {
								if(materialNestedList.contains(invitembatch_id)){
									String batchName = formDao.getFromInfoLookup("invitembatch", LookupType.ID, invitembatch_id, "name");
									throw new Exception("Batch "+batchName+" has been defined recursively. Infinite loop might be executed");
								}
								getAIMaterialList(invitembatch_id,materialNestedList,level+1,materialResultPair);
							}
						}
					}
				} else {//recipe
					if(materialNestedList.contains(invitemmaterial_id)){
						String recipeName = formDao.getFromInfoLookup("recipeformulation", LookupType.ID, invitemmaterial_id, "name");
						throw new Exception("Recipe "+recipeName+" has been defined recursively. Infinite loop might be executed");
					}
					getAIMaterialList(invitemmaterial_id,materialNestedList,level+1,materialResultPair);
				}
			}
		} else {
			//the node is an internal batch that connected to a recipe->drills down into the recipe
			if(materialNestedList.contains(recipe_id)){
				String recipeName = formDao.getFromInfoLookup("recipeformulation", LookupType.ID, recipe_id, "name");
				throw new Exception("Recipe "+recipeName+" has been defined recursively. Infinite loop might be executed");
			}
			getAIMaterialList(recipe_id,materialNestedList,level+1,materialResultPair);
		}
	}
	materialNestedList.pop();
}

public void copyComponentTablefromMaterialToBatch(String materialId,String userId ,String batchId) {

	String sql = "";
	String colList = "";
	// insert
	colList = "," + generalDao.getTableColCsv("fg_s_materialComponent_pivot") + ",";
	colList = colList.replace(",FORMID,", ",").replace(",PARENTID,", ",").replace(",CLONEID,", ",");
	colList = colList.substring(1, colList.length() - 1);
	
	//copy the records from the material (the cloneid is for the order by of the DT - to have it as in material (order by formid))
	sql = "insert into fg_s_materialComponent_pivot r (" + colList + ",FORMID, PARENTID,CLONEID)\n" 
			+ " select " + colList + ", fg_get_struct_form_id(r.formcode),'" + batchId + "',FORMID \n"
			+ " from fg_s_materialComponent_pivot r \n"
			+ " where parentid='"+materialId+"' and sessionid is null and nvl(active,'1')='1' \n"
			// to prevent duplication on refreshing new batch form ->
			+ " and not exists (select 1 from fg_s_materialComponent_pivot t where t.parentid = '" + batchId + "')\n"
			+ " order by to_number(r.formid) ";
	formSaveDao.updateSingleStringInfoNoTryCatch(sql);		

	// update change by / date info
	sql = "update fg_s_materialComponent_pivot r set r.TIMESTAMP=sysdate,r.creation_date=sysdate,r.CHANGE_BY='" + userId
			+ "',r.created_by='" + userId + "'" + " where r.parentid='" + batchId + "'";
	formSaveDao.updateSingleStringInfoNoTryCatch(sql);
}

public void createDefaultMaterial(String materialProtocolType,String projectId ,String userId,String formCode) {
	List<Map<String, Object>> components=generalDao.getListOfMapsBySql("select m.InvItemMaterialName,t.CONCENTRATION from" + 
			" Fg_s_Materialcomponent_All_v t,Fg_s_Invitemmaterial_v m " + 
			"where t.MATERIAL_ID =m.formid(+) and parentId='"+projectId+"'  order by t.CONCENTRATION desc");
	String materialName=materialProtocolType.substring(0, 1).toUpperCase()+":";
	for (Map<String, Object> component : components) {
		 String concentration=component.get("CONCENTRATION")==null?"":"("+component.get("CONCENTRATION")+"%)";
		 materialName=materialName+" "+component.get("INVITEMMATERIALNAME")+concentration+"+";
       }
	 String materialtype_id=formDao.getFromInfoLookup("MaterialType",LookupType.NAME,materialProtocolType,"id");
	 String materialActiveStatus_id =formDao.getFromInfoLookup("MaterialStatus",LookupType.NAME,"Active","id");
	    materialName=generalUtil.replaceLast(materialName, "+", "");
		String newFormId = formSaveDao.getStructFormId(formCode);
		String sql_ = "insert into FG_S_INVITEMMATERIAL_PIVOT"
				+ " (FORMID,TIMESTAMP,CREATION_DATE,CLONEID,TEMPLATEFLAG,CHANGE_BY,CREATED_BY,SESSIONID,ACTIVE,FORMCODE_ENTITY,FORMCODE,"
				+ "invitemmaterialname,project_id,materialprotocoltype,materialtype_id,sourceProjectId,status_id)" + " VALUES ('" + newFormId + "',SYSDATE,SYSDATE,null,null,'" + userId + "','" + userId
				+ "',null,1,'InvItemMaterial','"+formCode+"','"+materialName+"','"+projectId+"','"+materialProtocolType+"','"+materialtype_id+"','"+projectId+"','" + materialActiveStatus_id + "')";

		formSaveDao.insertStructTableByFormId(sql_, "FG_S_InvItemMaterial_PIVOT", newFormId);
		copyComponentTablefromMaterialToBatch(projectId,userId,newFormId);
}
public void createDefaultMaterialsPerProject(String projectId ,String userId) {
	String sourceProjectId=generalDao.selectSingleString("select count(*) from fg_s_invitemmaterial_pivot where sourceProjectId="+projectId);
	if((sourceProjectId).equals("0")) {
		createDefaultMaterial("Formulation",projectId,userId,"InvItemMaterialFr");
		createDefaultMaterial("Premix",projectId,userId,"InvItemMaterialPr");
	}
	
	
	
}

public Map<String,String> validateRecipeExperimentconnection(String formId,String importedCompositionParent_id) {
	Map<String,String> toReturn = new HashMap<>();
	//Map<String,Integer> resultsMap = new HashMap<>();
	//Map<String,Integer> functionMap = new HashMap<>();
	//checks if the planned composition has been removing rows
	String sql = "select count(*)\n"
			+ "from (select nvl(upper(m.alternativegroup),t.invitemmaterial_id)\n"
			+ "from fg_s_composition_v t,\n"
			+ "fg_s_invitemmaterial_v m\n"
			+ "where t.parentid = '"+importedCompositionParent_id+"'\n"
			+ "and t.sessionid is null\n"
			+ " and t.active =1\n"
			+ " and t.invitemmaterial_id = m.invitemmaterial_id\n"
			+ "minus\n"
			+ "select nvl(upper(m.alternativegroup),m.invitemmaterial_id)\n"
			+ "from fg_s_invitemmaterial_v m\n"
			+ "where m.invitemmaterial_id in\n"
			+ "(select t.invitemmaterial_id\n"
			+ " from fg_s_composition_v t\n"
			+ " where t.parentid = '"+formId+"'\n"
			+ "and t.active = 1\n"
			+ generalUtilFormState.getWherePartForTmpData("Composition", formId)
			+"\n)\n)";
	String is_planned_different = generalDao.selectSingleStringNoException(sql);
	if(!is_planned_different.equals("0")){
		toReturn.put("1", "The selected Recipe is now different from the planned composition.</br>Saving the experiment will disconnect it from the selected Recipe.</br> Are you sure you want to continue?</br></br>Notice! Disconnecting the experiment is irreversible.");
		return toReturn;
	}
	//checks if the planned composition has been adding rows or changed materials
	sql = "select count(*)\n"
		+ "from (select nvl(upper(m.alternativegroup),m.invitemmaterial_id)\n"
		+ "from fg_s_invitemmaterial_v m\n"
		+ "where m.invitemmaterial_id in\n"
		+ "(select t.invitemmaterial_id\n"
		+ " from fg_s_composition_v t\n"
		+ " where t.parentid = '"+formId+"'\n"
		+ "and t.active = 1\n"
		+ generalUtilFormState.getWherePartForTmpData("Composition", formId)
		+"\n)"
		+ "\nminus\n"
		+"select nvl(upper(m.alternativegroup),t.invitemmaterial_id)\n"
		+ "from fg_s_composition_v t,\n"
		+ "fg_s_invitemmaterial_v m\n"
		+ "where parentid = '"+importedCompositionParent_id+"'\n"
		+ " and t.invitemmaterial_id = m.invitemmaterial_id\n"
		+ "and t.sessionid is null\n"
		+ " and t.active =1\n)";
	is_planned_different = generalDao.selectSingleStringNoException(sql);
	if(!is_planned_different.equals("0")){
		toReturn.put("1", "The selected Recipe is now different from the planned composition.</br>Saving the experiment will disconnect it from the selected Recipe.</br> Are you sure you want to continue?</br></br>Notice! Disconnecting the experiment is irreversible.");
		return toReturn;
	}
	//partition-check if it has an alternative-if not- then partition by invitemmaterial_id
	//common alternative group for some different materials-consider them as the same material
	sql = "select first_value(m.invitemmaterial_id)over(partition by nvl(upper(m.ALTERNATIVEGROUP),t.invitemmaterial_id)) invitemmaterial_id,\n"
		+ " first_value(t.function_id) over (partition by nvl(upper(m.ALTERNATIVEGROUP),t.invitemmaterial_id) order by to_number(nvl(t.filler,'0')||t.ww_p*t.general_relation_val/100) desc) function_id,\n"//get the function that its weight is the biggest one
		+ " sum(t.ww_p*t.general_relation_val/100) over (partition by nvl(upper(m.ALTERNATIVEGROUP),t.invitemmaterial_id)) ww_p\n"
		+ "from FG_RECIPE_MATERIAL_FUNC_LOG t,\n"
		+ "fg_s_invitemmaterial_v m"
		+ " where t.tableType = 'plannedComposition'\n"
		+ " and t.invitemmaterial_id = m.invitemmaterial_id\n"
		+ " and nvl(t.rowtype,'chemical') not in ('Premix Material','Recipe','Premix Recipe','Step (Premix) material')";
	List<Map<String,Object>> compositionDetails = generalDao.getListOfMapsBySql(sql);
	for(Map<String,Object> materialCompData:compositionDetails){
		String material_id = materialCompData.get("INVITEMMATERIAL_ID")!=null ?materialCompData.get("INVITEMMATERIAL_ID").toString():"";
		if(material_id.isEmpty()){
			continue;
		}
		String function_id = materialCompData.get("FUNCTION_ID")!=null ?materialCompData.get("FUNCTION_ID").toString():"";
		String plannedCompWW_P = materialCompData.get("WW_P")!=null ?materialCompData.get("WW_P").toString():"0";
		if(function_id.isEmpty()){//if no function has selected then continue to the next material
			continue;
		}
		
		String alternativeGroup = formDao.getFromInfoLookup("invitemmateriel", LookupType.ID, material_id, "alternativeGroup");
		//gets the data from the imported composition
		sql = "select distinct sum(t.ww_p*t.general_relation_val/100)\n" //over (partition by nvl(m.ALTERNATIVEGROUP,t.invitemmaterial_id)) ww_p"
			+ "from FG_RECIPE_MATERIAL_FUNC_LOG t,\n"
			+ "fg_s_invitemmaterial_v m"
			+ " where t.tableType = 'importedComposition'\n"
			+ " and t.invitemmaterial_id = m.invitemmaterial_id\n"
			+ (alternativeGroup.isEmpty()?" and m.invitemmaterial_id = '"+material_id+"'\n":"and upper(m.alternativegroup) = upper('"+alternativeGroup+"')")
			+ " and nvl(t.rowtype,'chemical') not in ('Premix Material','Recipe','Premix Recipe','Step (Premix) material')";
		String importedCompWW_P = generalDao.selectSingleStringNoException(sql);
		double d_importedCompWW_P = importedCompWW_P.isEmpty()?0:Double.parseDouble(importedCompWW_P);
		
		//pass through all the conditions in the function
		sql = "select t.*\n"
			+ "from fg_s_materialfunction_v mf,\n"
			+ "Fg_s_Functionruleref_all_v t\n"
			+ "where t.PARENTID = mf.materialfunction_id\n"
			+ "and mf.MaterialFunction_id='"+function_id+"'\n"
			+ "and t.active =1\n"
			+ "and t.sessionid is null";
		List<Map<String,Object>> funcionConditionList = generalDao.getListOfMapsBySql(sql);
		for(Map<String,Object> functionCondition:funcionConditionList){
			String criteria = functionCondition.get("PARAMETERSCRITERIANAME")!=null?functionCondition.get("PARAMETERSCRITERIANAME").toString():"";
			String value1 = functionCondition.get("VALUE1")!=null?functionCondition.get("VALUE1").toString():"0";
			String value2 = functionCondition.get("VALUE2")!=null?functionCondition.get("VALUE2").toString():"0";
			String activity = functionCondition.get("ACTIVITY")!=null?functionCondition.get("ACTIVITY").toString():"";
			
			double importedCompVal1 = d_importedCompWW_P*(100+Double.parseDouble(value1))/100;
			double importedCompVal2 = d_importedCompWW_P*(100+Double.parseDouble(value2))/100;
			switch (criteria){
			case "<":
				if(Double.parseDouble(plannedCompWW_P)<importedCompVal1){
					return getActivityMessage(activity,function_id,"smaller than "+value1+"%");
				}
				break;
			case ">":
				if(Double.parseDouble(plannedCompWW_P)>importedCompVal1){
					return getActivityMessage(activity,function_id,"bigger than "+value1+"%");
				}
				break;
			case "><":
				if(Double.parseDouble(plannedCompWW_P)>importedCompVal1 && Double.parseDouble(plannedCompWW_P)<importedCompVal2){
					return getActivityMessage(activity,function_id,"between "+value1+"% and "+value2+"%");
				}
				break;
			case "=":
				if(Double.parseDouble(plannedCompWW_P)==importedCompVal1){
					return getActivityMessage(activity,function_id,"equals to "+value1+"%");
				}
				break;
			case "<=":
				if(Double.parseDouble(plannedCompWW_P)<=importedCompVal1){
					return getActivityMessage(activity,function_id,"smaller-equals to "+value1+"%");
				}
				break;
			case ">=":
				if(Double.parseDouble(plannedCompWW_P)>=importedCompVal1){
					return getActivityMessage(activity,function_id,"bigger-equals to "+value1+"%");
				}
				break;
			default:
				continue;
			}
		}
	}
	return toReturn;
}

private Map<String, String> getActivityMessage(String activity, String function_id,String conditionPhrase) {
	Map<String,String> toRet = new HashMap<>();
	if(activity.equals("No Activity")){
		return toRet;
	} else if(activity.equals("Display Alert")){
		String functionName = formDao.getFromInfoLookup("materialfunction", LookupType.ID, function_id, "name");
		toRet.put("2", "The change in the w/w% for Function ‘"+functionName+"’ is "+conditionPhrase+".</br>Would you like to keep the external code?");
	} else if(activity.equals("Clear External Code")){
		toRet.put("1", "The selected Recipe is now different from the planned composition.</br>Saving the experiment will disconnect it from the selected Recipe.</br> Are you sure you want to continue?");
	}
	return toRet;
}
public void checkTestedComponentMandatoryfields(String experiment_id,String userId) throws Exception {
	String sql = "select distinct case when TYPE_ID is null or t.MATERIALID is null or t.COEFFICIENT is null \n" + 
			"  or NUMOFSTANDARDROWS is null or t.RT is null or t.OUM_ID is null then \n" + 
			"   nvl2(TYPE_ID,null,'Type, ')|| nvl2(t.MATERIALID,null,'Component Name, ')|| nvl2(t.COEFFICIENT,null,'Coefficient, ')||\n" + 
			"    nvl2(t.RT,null,'RT, ')|| nvl2(t.OUM_ID,null,'RT UOM, ')|| nvl2(t.NUMOFSTANDARDROWS,null,'Number of Rows, ') end  missingFields "
			+ " from fg_s_component_v t\n"
			+ " where parentid = '"+experiment_id+"'\n"
			+ " and active = 1\n"
			+" and sessionid is null";
	List<String> missingFields = generalDao.getListOfStringBySql(sql);
	String data = "";
	for(int i = 0;i<missingFields.size();i++) {
		if(missingFields.get(i)!= null) {
			data+= "</br>"+missingFields.get(i) ;
		}
	}
	if(!data.isEmpty()) {
		data = data.substring(0, data.length() - 2);
		throw new Exception("Some mandatory fields in the Tested Component table are empty.</br>Missing fields are:"+data+"<br> Please fill them in order to save the experiment.");
	}
	/*for (String m : missingFields) {
	if (!generalUtil.getNull(m).isEmpty()) {
		throw new Exception("Some mandatory fields in the Tested Component table are empty.</br>Missing fields are:</br>"+missingFields+"<br> Please fill them in order to save the experiment.");
	}*/
		
		/*List<Map<String, Object>> componentList = generalDao.getListOfMapsBySql(sql);
		for (Map<String, Object> componentData : componentList) {
			if (componentData.get("TYPE_ID") == null || componentData.get("MATERIALID") == null
					|| componentData.get("COEFFICIENT") == null || componentData.get("NUMOFSTANDARDROWS") == null
					|| componentData.get("RT") == null || componentData.get("OUM_ID") == null)
				throw new Exception(
						"Some mandatory fields in the Tested Component table are empty.</br>Missing fields are:</br>"
								+ missingFields + "<br> Please fill them in order to save the experiment.");
		}*/

}
}