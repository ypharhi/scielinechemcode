package com.skyline.form.dal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.ElementInfoAuditTrailDisplay;
import com.skyline.form.bean.ElementInfoAuditTrailMeta;
import com.skyline.form.bean.ElementUIKeyValueDisplay;
import com.skyline.form.bean.Form;
import com.skyline.form.bean.LevelType;
import com.skyline.form.entity.Element;
import com.skyline.form.entity.Entity;
import com.skyline.form.service.CacheService;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.form.service.GeneralUtilNotificationEvent;

@Repository("GeneralTaskDao")
@Configuration
@EnableAsync
public class GeneralTaskDaoImp implements GeneralTaskDao {

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private GeneralDao generalDao;

	@Autowired
	private CacheService cacheService;

	@Autowired
	private GeneralUtilNotificationEvent generalUtilNotificationEvent;

	private List<String> formPathFormCodeList = null;

	@Autowired
	protected ChemDao chemDao;

	@Autowired
	private GeneralUtilLogger generalUtilLogger;

	@PostConstruct
	void initformPathFormCodeList() {
		try {
			formPathFormCodeList = generalDao.getListOfStringBySql(
					"select upper(replace(replace(t.TABLE_NAME,'FG_AUTHEN_',''),'_V','')) as form_code \r\n"
							+ "from user_tab_columns t \r\n"
							+ "where t.TABLE_NAME like 'FG_AUTHEN_%_V' AND T.COLUMN_NAME = 'FORMPATH'");
		} catch (Exception e) {
			// do nothing
		}
	}

	/**
	 * DB table
	 * formCode 
	 * contextType - 0 - form save, 1 - form save transaction data (tmp popup become constant), 2 - event
	 * eventContextCode - to recognized specific event
	 * auditTrailChangeType I insert / U update / D delete
	 */
	@Async
	@Override
	public void updateMVByPivotTable(String formType, String table, String formCode, int contextType, String eventContextCode,
			String auditTrailChangeType) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("formType_in", formType);
		parameters.put("pivot_table_in", table);
		parameters.put("formCode_in", formCode);
		parameters.put("contextType_in", String.valueOf(contextType));
		parameters.put("eventContextCode_in", String.valueOf(eventContextCode));
		parameters.put("auditTrailChangeType_in", String.valueOf(auditTrailChangeType));
		generalDao.callPackageFunction("FG_ADAMA", "REFRESH_DATA_TABLES", parameters);
	}

	@Async
	@Override
	public String doSaveInfoAndAuditTrail(Form form, String formId, String userId, Map<String, String> elementValueMap,
			Map<String, ElementUIKeyValueDisplay> elementUIKeyValueDisplayMap, String auditTrailChangeType,
			String dbTransactionId) {
		if (elementValueMap.isEmpty()) {
			return "0";
		}

		//add this form in fg_form_change_list for the task that update the audit trail on latest changes
		generalDao.updateSingleString("insert into fg_form_change_list (formcode) values('"
				+ form.getFormCode().toUpperCase().replace("FG_S_", "").replace("_PIVOT", "") + "')");

		//		boolean isPathUpdate = elementValueMap.containsKey("parentId") && auditTrailChangeType.equals("U");
		//		System.out.println("******* formId=" + formId + ",formCode=" + form.getFormCode() + ", auditTrailChangeType="
		//				+ auditTrailChangeType + ", elementValueMap=" + generalUtil.mapToString("map values", elementValueMap));

		String update = "0";
		String sql = "";
		try {
			String innerUnionSql = getValuesForInfoAndAuditTrail(form.getFormCode(), form.getFormCodeEntity(), formId,
					userId, elementValueMap, elementUIKeyValueDisplayMap, auditTrailChangeType);
			if (innerUnionSql.isEmpty()) {
				System.out.println("innerUnionSql is empty: formId=" + formId + ",formCode=" + form.getFormCode()
						+ ", auditTrailChangeType=" + auditTrailChangeType);
			}
			sql = "merge into FG_FORMLASTSAVEVALUE_INF p using ( " + innerUnionSql + " ) t1"
					+ " on( p.formid=t1.formid and p.entityimpcode = t1.entityimpcode ) "
					+ "when not matched then insert (FORMID,FORMCODE_ENTITY,ENTITYIMPCODE,ENTITYIMPVALUE,USERID,CHANGE_COMMENT,CHANGE_ID,CHANGE_BY,CHANGE_TYPE,CHANGE_DATE,SESSIONID,ACTIVE,DISPLAYVALUE,UPDATEJOBFLAG,DISPLAYLABEL,PATH_ID,IS_IDLIST)"
					+ " values (t1.FORMID,t1.FORMCODE_ENTITY,t1.ENTITYIMPCODE,t1.ENTITYIMPVALUE,t1.USERID,t1.CHANGE_COMMENT,t1.CHANGE_ID,t1.CHANGE_BY,'I',t1.CHANGE_DATE,t1.SESSIONID,t1.ACTIVE,t1.DISPLAYVALUE,t1.UPDATEJOBFLAG,t1.DISPLAYLABEL,t1.PATH_ID,t1.IS_IDLIST) "
					+ "when matched then update set \r\n" + " p.entityimpvalue = t1.entityimpvalue, \r\n"
					+ " p.DISPLAYVALUE = t1.DISPLAYVALUE, \r\n"
					+ " p.UPDATEJOBFLAG = t1.UPDATEJOBFLAG, p.ACTIVE = t1.ACTIVE, p.CHANGE_TYPE = 'U', p.CHANGE_DATE = t1.CHANGE_DATE, "
					+ " p.change_by = t1.userid, p.PATH_ID = t1.PATH_ID, p.IS_IDLIST = t1.IS_IDLIST, \r\n"
					+ " p.DB_TRANSACTION_ID = null \r\n" + " where nvl(p.IS_IDLIST,0) <> 2"; // IS_IDLIST contains row created from rows with IS_IDLIST 1 we will handled in handleIdlIst...

			update = String.valueOf(generalDao.updateSingleString(sql));

			//update FG_SEQUENCE FORMIDNAME from tmp values (it will be for tmp popup forms that we make in the else block below)
			sql = "update FG_SEQUENCE set DB_SEQ_TRANSACTION_ID = null, FORMIDNAME = TMP_FORMIDNAME where DB_SEQ_TRANSACTION_ID = '"
					+ dbTransactionId + "'";
			String.valueOf(generalDao.updateSingleString(sql));

			//			//update FG_FORMLASTSAVEVALUE_INF tmp values / display for this transaction (we will handle the failures in the night job and use FG_FORMLASTSAVEVALUE_INF_T_V to see the right values for failed records between the jobs)
			//			sql = "update FG_FORMLASTSAVEVALUE_INF t set t.tmp_entityimpvalue = t.entityimpvalue, t.TMP_DISPLAYVALUE = t.DISPLAYVALUE, t.db_transaction_id = '', t.CHANGE_COMMENT = 'system: backup last completed values' where t.DB_TRANSACTION_ID = '"
			//					+ dbTransactionId + "'";
			//			String.valueOf(generalDao.updateSingleString(sql));

			String formCodeById_ = form.getFormCode(); // note: in the above merge the formcode is used to get the label for the element here we need the updated formcode for the path objects (in case in of <form>Main scenario where the real formcode is updated during the save event)
			try {
				if (!form.getFormCode().equals(form.getFormCodeEntity())) {
					formCodeById_ = generalDao
							.selectSingleString("select formcode from fg_sequence where id = '" + formId + "'");
				}
			} catch (Exception e1) {
				// DO nothing
			}

			try {
				if (formPathFormCodeList != null && formPathFormCodeList.contains(formCodeById_.toUpperCase())) { //This made to save DB calls
					updateFgSeqPathAndSearchMatch(formCodeById_, form.getFormCodeEntity(), formId);
				}
			} catch (Exception e) {
				// do nothing - the sched task will complete it in the worst case
			}

			//if the page contains excel sheet then updates the historical spread data 
			if(elementValueMap.containsKey("spreadsheetExcel")){
				updateHistoricalSpreadData(formId, formCodeById_, userId,elementValueMap.get("spreadsheetExcel"));
			}
			
//			if (auditTrailChangeType.equals("I")) { //This made to save DB calls because in this point the POST_SAVE_AT in Adama make experiment version change - if we need more flexibility the  BLconditions will be eval in the procedure
				//call POST_SAVE_AT
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("formCode_in", formCodeById_);
			parameters.put("formCodeEntity_in", form.getFormCodeEntity());
			parameters.put("formId_in", formId);
			parameters.put("userId_in", userId);
			parameters.put("dbTransactionId_in", dbTransactionId);
			parameters.put("auditTrailChangeType_in", auditTrailChangeType);
			generalDao.callPackageFunction("FG_ADAMA_TASK_BY_DATE", "POST_SAVE_AT", parameters);
//			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return update;
	}

	private void updateHistoricalSpreadData(String formId, String formCode, String userId, String spreadsheetId) {
		String sql = "merge into FG_HISTORICAL_SPREAD_DATA H using\n"
				+ " ( select '" + userId + "' USER_ID,\n'"
				+ formId + "' FORMID,'" + formCode
				+ "' FORMCODE, TO_DATE(SYSDATE) AS TIMESTAMP\n"
				+ ", (select t.file_content from fg_clob_files t where t.file_id ='"+spreadsheetId+"') as SPREAD_CONTENT\n"
				+ " from dual)  t\n "
				+ " on\n( (H.USER_ID = t.USER_ID and H.FORMID = t.FORMID"
				+ " and TO_DATE(H.TIMESTAMP) = T.TIMESTAMP)\n"//USER&FORMID&DATE ARE EQUAL TO THE CURRENT ONES
				+ " or (H.USER_ID = t.USER_ID and H.FORMID = t.FORMID"
				+ " and TO_DATE(H.TIMESTAMP) <> T.TIMESTAMP"
				+ " and dbms_lob.compare(T.SPREAD_CONTENT ,(select t.file_content from fg_clob_files t where t.file_id = h.SPREADSHEET_ID ))=0 ))\n"//USER&FORMID&SPREADSHEET ARE EQUAL TO THE CURRENT ONES BUT THE DATE IS DIFFERENT FROM THE CURRENT ONE-added this condition in order to get it out from the  case of inserting the record
				+ " when not matched then insert (FORMID,FORMCODE,USER_ID,TIMESTAMP,SPREADSHEET_ID,ACTIVE)"
				+ " values (T.FORMID,T.FORMCODE,T.USER_ID,SYSDATE,'"+spreadsheetId+"',1)"
				+ "when matched then update set h.SPREADSHEET_ID = '"+spreadsheetId+"'";
		generalUtilLogger.logWriter(LevelType.DEBUG,
				"Insert to historical spread data table! </br>" + " Merging query: </br>" + sql, ActivitylogType.SaveEvent,
				formId);
		generalDao.updateSingleStringNoTryCatch(sql);
	}
	
	private String getValuesForInfoAndAuditTrail(String formCode, String formCodeEntity, String formId, String userId,
			Map<String, String> elementValueMap, Map<String, ElementUIKeyValueDisplay> elementUIKeyValueDisplayMap,
			String auditTrailChangeType) {
		StringBuilder using = new StringBuilder();
		String displayValue = "";
		String updateJobFlag = "0";
		String displayLabel = "";
		String pathId = "";
		String temp = "";
		boolean isIdList = false;
		ElementInfoAuditTrailDisplay elementDisplayValueInfo = null;
		for (Map.Entry<String, String> entry : elementValueMap.entrySet()) {
			if (!entry.getKey().equals("formId")) {
				try {
					if (elementUIKeyValueDisplayMap != null
							&& elementUIKeyValueDisplayMap.get(entry.getKey()) != null) {
						String originValue = elementUIKeyValueDisplayMap.get(entry.getKey()).getOriginValue();
						String uiDisplayValue = elementUIKeyValueDisplayMap.get(entry.getKey()).getUiDisplayValue();
						elementDisplayValueInfo = getElementInfoAuditTrailValue(formCode, entry.getKey(),
								entry.getValue(), originValue, uiDisplayValue, elementValueMap);
					} else {
						elementDisplayValueInfo = getElementInfoAuditTrailValue(formCode, entry.getKey(),
								entry.getValue(), "", "", elementValueMap);
					}

					if (elementDisplayValueInfo.getUpdateJobFlag().equals("-1")) {
						continue;
					}

					displayValue = elementDisplayValueInfo.getValue();
					updateJobFlag = elementDisplayValueInfo.getUpdateJobFlag();
					displayLabel = elementDisplayValueInfo.getLabel();
					pathId = elementDisplayValueInfo.getPathId();
					isIdList = elementDisplayValueInfo.isIdList();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					displayValue = entry.getValue(); //TODO JOB FLAG ON
					updateJobFlag = "1";
					displayLabel = "na";
				}

				//TODO ADD JOB FLAG
				using.append("select '" + formId + "' formid,'" + formCodeEntity + "' formcode_entity,'"
						+ entry.getKey() + "' entityimpcode,'"
						+ generalUtil.replaceDBUpdateVal(entry.getValue()) + "' entityimpvalue,'" + userId
						+ "' userid, null sessionid, null change_comment,null CHANGE_ID, '" + userId + "' change_by,'"
						+ auditTrailChangeType + "' change_type, sysdate change_date,1 active, '"
						+ generalUtil.replaceDBUpdateVal(displayValue) + "' displayvalue, '" + updateJobFlag
						+ "' as updateJobFlag,'" + generalUtil.getNull(displayLabel).replace("null", "")
						+ "' as DISPLAYLABEL, '" + generalUtil.getNull(pathId).replace("null", "") + "' as PATH_ID,"
						+ (isIdList ? "1" : "0") + " as IS_IDLIST "
						//						+ generalUtil.getNull(dbTransactionId).trim().replace("'", "''") + "' as DB_TRANSACTION_ID "
						//+ generalUtil.getNull(entry.getValue()).replaceAll("'", "''") + "' as TMP_ENTITYIMPVALUE, '" 
						//+ generalUtil.getNull(displayValue).replace("'", "''") + "' as TMP_DISPLAYVALUE " 
						+ " from dual union all\n");
			}
		}
		if (!elementValueMap.isEmpty() && using.length() > 0) {
			temp = using.delete(using.lastIndexOf(" union all"), using.lastIndexOf("l") + 1).toString();
		}
		return temp;
	}

	private ElementInfoAuditTrailDisplay getElementInfoAuditTrailValue(String formCode, String key,
			String postSaveValue, String originValue, String uiDisplayValue, Map<String, String> elementMap) {
		ElementInfoAuditTrailDisplay elementInfoAuditTrailDisplay = null;
		try {
			ElementInfoAuditTrailMeta elementDisplayValueMeta = null;
			Element element = null;
			Map<String, ElementInfoAuditTrailMeta> formElementDisplayMap = cacheService
					.getFormElementInfoAuditTrailMetaMap();
			elementDisplayValueMeta = formElementDisplayMap.get((formCode + "." + key).toUpperCase());
			if (elementDisplayValueMeta != null) { // TODO check
				Entity e = cacheService.getFormEntityClassSingleToneMap()
						.get(elementDisplayValueMeta.getElementClass());
				if (e instanceof Element) {
					element = (Element) e;
				}
			}

			if (element == null) {
				return new ElementInfoAuditTrailDisplay(postSaveValue, "-1");
			}

			elementInfoAuditTrailDisplay = element.getAuditTrailValue(formCode, key, postSaveValue, originValue,
					uiDisplayValue);

			//set parentPathId
			String parentPathId = "";
			if (elementDisplayValueMeta.isParentPathId()) {
				if (elementMap != null) {
					parentPathId = generalUtil.getNull(elementMap.get("parentId"), "1");
				} else {
					parentPathId = "1";
				}
			}
			elementInfoAuditTrailDisplay.setPathId(parentPathId);

			//set tableType
			String tableType = "";
			if (elementDisplayValueMeta.isParentPathId()) {
				if (elementMap != null) {
					tableType = generalUtil.getNull(elementMap.get("tableType"));
				} else {
					tableType = "1";
				}
			}
			elementInfoAuditTrailDisplay.setTableType(tableType);

			//set label
			String label = elementDisplayValueMeta.getLabel();
			elementInfoAuditTrailDisplay.setLabel(generalUtil.getNull(label));

			//set isIdList
			boolean isIdList = elementDisplayValueMeta.isIdList();
			elementInfoAuditTrailDisplay.setIdList(isIdList);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return elementInfoAuditTrailDisplay;
	}

	@Async
	@Override
	public void updateCach(Form form) {
		// We use cache only in the maintenance forms. on maintenance form change we update all other maintenance forms that has columns from other forms (that has more columns rather then 'NAME','ID', 'FORMCODE')
		// NOTE! The assumption is that in the maintenance fg_s_ <formcode>_inf_v there is no data taken from non maintenance forms.
		List<String> updateFormList = new ArrayList<String>();
		if(form.getUseCache() != null && form.getUseCache().equals("1")) {
			String sql = "select DISTINCT f.formcode from user_tab_columns t, FG_FORM F where t.TABLE_NAME like 'FG_S_' || UPPER(F.FORMCODE) || '_INF_V' AND T.COLUMN_NAME NOT IN ('NAME','ID', 'FORMCODE') AND (F.USECACHE = 1)";
			updateFormList = generalDao.getListOfStringBySql(sql);
			updateFormList.add(form.getFormCode());
			for (String formCode_ : updateFormList) {
				cacheService.setCacheOnFormDataChange(formCode_);
			}
		}
		
//		if(form.getFormType().equalsIgnoreCase("Maintenance")) {
//			try {
//				// refresh REFRESH_DATA_PERM_MAINTENANCE
//				Map<String, String> parameters = new HashMap<String, String>();
//				generalDao.callPackageFunction("FG_ADAMA", "REFRESH_DATA_PERM_MAINTENANCE", parameters);
//			} catch (Exception e) {
//				System.out.println("failed");
//				// do nothing
//			}
//		}
	}

	@Async
	@Override
	public String correctFgSeqTableFormCode(Form form, String formId) {
		String formCode = generalUtil.getNull(form.getFormCode());
		String formCodeEntity = generalUtil.getNull(form.getFormCodeEntity());
		String rslt = generalDao.updateSingleString(
				"update FG_S_" + formCodeEntity + "_PIVOT t set t.formcode = " + getUpdatedFromCode(formCode, formId)
						+ ", formCode_entity = '" + formCodeEntity + "' where t.formid = '" + formId + "' ");
		return rslt;
	}

	/**
	 * In Main screens the form id should not be <formcode>Main - here its a correction that should be fixed in next version - in this case we take the form code from fg_sequance that holds the real fromCode
	 * @param formCode
	 * @param formId
	 * @return
	 */
	private String getUpdatedFromCode(String formCode, String formId) {
		// TODO Auto-generated method stub
		String formCodeToReturn = "'" + formCode + "'";
		if (formCode.toLowerCase().endsWith("main") || formCode.toLowerCase().startsWith("sys")) {
			formCodeToReturn = "(select formcode from fg_sequence where id = '" + formId + "')";
		}
		return formCodeToReturn;
	}

	@Async
	@Override
	public void onTransactionFailure(String formCode, String formId, String dbTransaction) {
		if (!generalUtil.getNull(dbTransaction).equals("")) {
			String sql = "insert into FG_FORMLASTSAVE_TRANSACT_FAIL(TRANSACTION_FAILURE_NUMBER) values('"
					+ dbTransaction + "')";
			String.valueOf(generalDao.updateSingleString(sql));
		}

		//clean chem search table not connected to material on transaction failure (because the insert into FG_CHEM_SEARCH is not part of the transaction)
		if (generalUtil.getNull(formCode).equals("InvItemMaterial")) {
			chemDao.deleteRowJChemSearchTableNoMaterial(formId);
		}
	}

	private void updateFgSeqPathAndSearchMatch(String formCode, String formCodeEntity, String formId) {
		String sql = "SELECT distinct A.formPath FROM FG_AUTHEN_" + formCode + "_V A WHERE TO_CHAR(A." + formCodeEntity
				+ "_ID) = '" + formId + "'";
		String formPathInfo = generalDao.selectSingleStringNoException(sql);

		if (formPathInfo != null && !formPathInfo.isEmpty()) {
			sql = "update FG_SEQUENCE SET CHANGEDATE = sysdate, formpath='" + generalUtil.replaceDBUpdateVal(formPathInfo) + "' "
					+ generalUtil.getEmpty(getSearchMatchSet(formPathInfo), ", SEARCH_MATCH_ID1 = -1") + " where id='"
					+ formId + "'";
			generalDao.selectSingleStringNoException(sql);
		}
	}

	private String getSearchMatchSet(String formPathInfo) {
		Map<String, String> searchMatchColumnName = new LinkedCaseInsensitiveMap<>();
		searchMatchColumnName.put("Project", "SEARCH_MATCH_ID1");
		searchMatchColumnName.put("SubProject", "SEARCH_MATCH_ID2");
		searchMatchColumnName.put("SubSubProject", "SEARCH_MATCH_ID3");
		searchMatchColumnName.put("InvItemMaterial", "SEARCH_MATCH_ID4");

		String toReturn = "";
		try {
			if (formPathInfo.isEmpty()) {
				return toReturn;
			}
			JSONObject json = new JSONObject(formPathInfo);
			JSONArray pathList = json.getJSONArray("path");
			for (int i = 0; i < pathList.length(); i++) {
				String p = pathList.get(i).toString();
				String[] detailsToDisplay = generalUtil.getJsonValById(p, "name").split(":");
				String id = generalUtil.getJsonValById(p, "id");
				String formCode = detailsToDisplay.length > 0 ? detailsToDisplay[0] : "";
				//String name = detailsToDisplay.length>1?detailsToDisplay[1]:"";
				if (searchMatchColumnName.containsKey(formCode)) {
					toReturn += "," + searchMatchColumnName.get(formCode) + "='" + id + "'";//set the material_id
				}

			}
		} catch (Exception ex) {
			//			generalUtilLogger.logWrite(ex);
			//			ex.printStackTrace();
			toReturn = "";
		}
		return toReturn;

	}

	@Async
	@Override
	public void exeNotificationEvent(String formId, String formCodeEntity) {
		generalUtilNotificationEvent.exeNotificationEvent(formId, formCodeEntity);

	}

	@Async
	@Override
	public void updateInfoAndAuditTrailDeletion(List<String> deleteFormIdList) {
		try {
			if (deleteFormIdList != null && deleteFormIdList.size() > 0) {
				StringBuilder toDeleteCsv = new StringBuilder("");
				for (String id_ : deleteFormIdList) {
					toDeleteCsv.append(",'" + generalUtil.getNull(id_, "-99999") + "'");
				}
				String sql = "update fg_formlastsavevalue_inf set active = 0 where formId in ("
						+ toDeleteCsv.toString().substring(1) + ")";
				generalDao.selectSingleStringNoException(sql);
			}
		} catch (Exception e) {
			// Do nothing
			e.printStackTrace();
		}
	}

	@Async
	@Override
	public String updateSingleString(String sql) {
		return generalDao.updateSingleString(sql);

	}
}
