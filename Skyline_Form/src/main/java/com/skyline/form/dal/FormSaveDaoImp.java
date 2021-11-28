package com.skyline.form.dal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.Form;
import com.skyline.form.bean.FormType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilForm;
import com.skyline.form.service.GeneralUtilFormState;
import com.skyline.form.service.GeneralUtilLogger;

@Repository("FormSaveDao")
public class FormSaveDaoImp extends BasicDao implements FormSaveDao {

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private GeneralUtilLogger generalUtilLogger;

	@Autowired
	private FormDao formDao;

	@Autowired
	private GeneralDao generalDao;

	@Autowired
	protected GeneralTaskDao generalTaskDao;
	
	@Autowired
	protected GeneralUtilFormState generalUtilFormState;

	@Autowired
	private GeneralUtilForm generalUtilForm;
	 

	//	@Value("${savePropImp}")
	//	private String serviceClass; // ServiceProduction/ServiceDevelop

	//	private JdbcTemplate jdbcTemplate;

	private static final Logger logger = LoggerFactory.getLogger(FormSaveDaoImp.class);

	//	@Autowired
	//	public void setDataSource(DataSource dataSource) {
	//		this.jdbcTemplate = new JdbcTemplate(dataSource); 
	//	}

	@Override
	public String doSaveStruct(Form form, String formId, String userId, Map<String, String> elementValueMap,
			String table, String sessionId, String isNew, String lastChangeUserId, String lastChangeDate) {
		int intUpdate = 0;
		//int seqtUpdate = 0;
		if (elementValueMap.isEmpty()) {
			return "0";
		}
		String sql = "merge into " + table + " p using "
				+ getUsingFordoSaveStruct(form.getFormCode(), userId, sessionId, elementValueMap, lastChangeUserId, lastChangeDate, isNew, table);
		logger.info("doSaveStruct sql=" + sql);

		intUpdate = generalDao.updateSingleStringNoTryCatch(sql);
		String formNameId = elementValueMap.get(generalUtil.changeFirstChar(form.getFormCodeEntity(), true) + "Name");
		String formTableType = generalUtil.changeFirstChar(generalUtil.getNull(elementValueMap.get("tableType")),
				false);

		if (intUpdate > 0) {

			sql = "update FG_SEQUENCE SET CHANGEDATE = sysdate"
					+ ((sessionId == null)
							? ", FORMIDNAME = '" + generalUtil.replaceDBUpdateVal(formNameId) + "'" : "")
					+ ", FORMTABLETYPE = '" + formTableType + "' where id = '" + formId + "' ";
			logger.info("update FG_SEQUENCE formidname sql=" + sql);
			intUpdate = generalDao.updateSingleStringNoTryCatch(sql);

		}
		
//		if (intUpdate == 0) {
//			generalUtilLogger.logWriter(LevelType.ERROR,
//					"doSaveStruct() updated = " + intUpdate + " rows by next sql: " + sql, ActivitylogType.SaveEvent,
//					formId);
//		}
		return String.valueOf(intUpdate);
	}

	@Override
	public String getStructFormId(String formCode) {
		String userId = "-1";
		try {
			userId = generalUtil.getSessionUserId();
		} catch(Exception e) {
			// do nothing
		}
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("form_code_in", formCode);
		parameters.put("userid_in", userId);
		parameters.put("parentformid_in", "-1");
		//		parameters.put("ts_in", String.valueOf(new Date().getTime()));
		return String.valueOf(generalDao.callPackageFunction("", "FG_GET_STRUCT_FORM_ID", parameters));
	}
	
	@Override
	public String getStructFormId(String formCode, String parentFormId) {
		String userId = "-1";
		try {
			userId = generalUtil.getSessionUserId();
		} catch(Exception e) {
			// do nothing
		}
		
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("form_code_in", formCode);
		parameters.put("userid_in", userId);
		parameters.put("parentformid_in", parentFormId);
		//		parameters.put("ts_in", String.valueOf(new Date().getTime()));
		return String.valueOf(generalDao.callPackageFunction("", "FG_GET_STRUCT_FORM_ID", parameters));
	}

	@Override
	public String getStructFileId(String formCode, String formId) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("form_code_in", formCode);
		parameters.put("formid_in", formId);
		//		parameters.put("ts_in", String.valueOf(new Date().getTime()));
		return String.valueOf(generalDao.callPackageFunction("", "FG_GET_STRUCT_FILE_ID", parameters));
	}
	
	@Override
	public String doSaveFormLastSaveValues(String formCode, String formId, String userId, Map<String, String> elementValueMap,
			String saveName, String useLoginsessionidScopeFlag, String description) {
		return doSaveFormLastSaveValues_(formCode, formId, userId, elementValueMap,"", saveName,
				useLoginsessionidScopeFlag, description);
	}
	
	private String doSaveFormLastSaveValues_(String formCode, String formId, String userId, Map<String, String> elementValueMap,
			String sessionId, String saveName, String useLoginsessionidScopeFlag, String description) {

		int intUpdate = 0;
		Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);

		if (elementValueMap.isEmpty()) {
			return "0";
		}

		String saveNameId = "";
		if (generalUtil.getNull(formId).equals("-1") && !generalUtil.getNull(saveName).isEmpty()) {
			saveNameId = generalUtil.getEmpty(
					generalDao.selectSingleStringNoException("select save_name_id from FG_FORMLASTSAVEVALUE_NAME"
							+ " where save_name = '" + saveName + "' and formcode_name = '" + formCode
							+ "' and userid='" + userId + "' and nvl(active,'1') = '1'"),
					getStructFormId("FG_FORMLASTSAVEVALUE_NAME"));
			String sql = "merge into FG_FORMLASTSAVEVALUE_NAME p using ( " + "select '" + saveNameId + "' save_name_id,'"
					+ saveName + "' as save_name,'" + formCode + "' as formcode_name, '" + userId + "' USerID, '"
					+ description + "' as save_description, '1' as active from dual) t1"
					+ " on( p.save_name=t1.save_name and p.formcode_name = t1.formcode_name and nvl(p.active,'1') = nvl(t1.active,'1') and p.userid = t1.userid) "
					+ "when not matched then insert (save_name_id,formcode_name,save_name,save_description,userid,active,created_by) "
					+ "values (t1.save_name_id,t1.formcode_name,t1.save_name,t1.save_description,t1.userid,t1.active,t1.userid) "
					+ "when matched then update set p.save_description = t1.save_description, p.timestamp = sysdate";// p.userid = t1.userid,
			logger.info("doSave sql=" + sql);

			intUpdate = generalDao.updateSingleStringNoTryCatch(sql);
		}

		String sql = "merge into FG_FORMLASTSAVEVALUE p using ( "
				+ getUsingFordoSave(formCode, form.getFormCodeEntity(), formId, userId, sessionId, elementValueMap,
						saveNameId, useLoginsessionidScopeFlag)
				+ " ) t1"
				+ " on( p.formid=t1.formid and p.formcode_name = t1.formcode_name and p.entityimpcode = t1.entityimpcode and nvl(p.sessionId,'-1') = nvl(t1.sessionId,'-1') and nvl(p.active,'1') = nvl(t1.active,'1') "
				+ (generalUtil.getNull(formId).equals("-1")
						? (!saveName.isEmpty() ? "and p.save_name_id = t1.save_name_id "
								: "and p.save_name_id is null and p.userid = t1.userid ")
						: "")
				+ ") "
				+ "when not matched then insert (formid,formcode_name,formcode_entity,entityimpcode,entityimpvalue,userid,sessionId,active,created_by,save_name_id,login_sessionid)"
				+ " values (t1.formid,t1.formcode_name,t1.formcode_entity,t1.entityimpcode,entityimpvalue,t1.userid, t1.sessionId, t1.active, t1.userid, t1.save_name_id, t1.login_sessionid) "
				+ "when matched then update set p.entityimpvalue = t1.entityimpvalue, p.change_by = t1.userid, p.login_sessionid = t1.login_sessionid";
		logger.info("doSave sql=" + sql);

		intUpdate = generalDao.updateSingleStringNoTryCatch(sql);
		if (intUpdate > 0) {
			//update name in FG_SEQUENCE
			sql = "update FG_SEQUENCE SET FORMIDNAME = '"
					+ elementValueMap.get(generalUtil.changeFirstChar(form.getFormCodeEntity(), true) + "Name")
					+ "'  where id = '" + formId + "' ";
			logger.info("update FG_SEQUENCE formidname sql=" + sql);
			intUpdate = generalDao.updateSingleStringNoTryCatch(sql);
		}

		return String.valueOf(intUpdate);
	}
	
	@Override
	/**
	 * this is use in develop as part of recreating the pivot table for struct form based of the form element settings
	 */
	public String doSaveDropAndCreatePivot(String formCode, String formId, String userId, Map<String, String> elementValueMap,
			String sessionId, String saveName, String useLoginsessionidScopeFlag, String description) {

		int intUpdate = 0;
		Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);

		if (elementValueMap.isEmpty()) {
			return "0";
		}

		String sql = "merge into FG_FORMLASTSAVEVALUE_UNPIVOT p using ( "
				+ getUsingFordoSave(formCode, form.getFormCodeEntity(), formId, userId, sessionId, elementValueMap,
						"", useLoginsessionidScopeFlag)
				+ " ) t1"
				+ " on( p.formid=t1.formid and p.formcode_name = t1.formcode_name and p.entityimpcode = t1.entityimpcode and nvl(p.sessionId,'-1') = nvl(t1.sessionId,'-1') and nvl(p.active,'1') = nvl(t1.active,'1') "
				+ (generalUtil.getNull(formId).equals("-1")
						? (!saveName.isEmpty() ? "and p.save_name_id = t1.save_name_id "
								: "and p.save_name_id is null and p.userid = t1.userid ")
						: "")
				+ ") "
				+ "when not matched then insert (formid,formcode_name,formcode_entity,entityimpcode,entityimpvalue,userid,sessionId,active,created_by,save_name_id,login_sessionid)"
				+ " values (t1.formid,t1.formcode_name,t1.formcode_entity,t1.entityimpcode,entityimpvalue,t1.userid, t1.sessionId, t1.active, t1.userid, t1.save_name_id, t1.login_sessionid) "
				+ "when matched then update set p.entityimpvalue = t1.entityimpvalue, p.change_by = t1.userid, p.login_sessionid = t1.login_sessionid";
		logger.info("doSave sql=" + sql);

		intUpdate = generalDao.updateSingleStringNoTryCatch(sql);
		if (intUpdate > 0) {
			//update name in FG_SEQUENCE
			sql = "update FG_SEQUENCE SET FORMIDNAME = '"
					+ elementValueMap.get(generalUtil.changeFirstChar(form.getFormCodeEntity(), true) + "Name")
					+ "'  where id = '" + formId + "' ";
			logger.info("update FG_SEQUENCE formidname sql=" + sql);
			intUpdate = generalDao.updateSingleStringNoTryCatch(sql);
		}

		return String.valueOf(intUpdate);
	}

	private String getUsingFordoSave(String formCode, String formCodeEntity, String formId, String userId,
			String sessionId, Map<String, String> elementValueMap, String saveNameId,
			String useLoginsessionidScopeFlag) {
		StringBuilder using = new StringBuilder();
		String temp = "";
		String loginSessionId = "";
		if (generalUtil.getNull(useLoginsessionidScopeFlag).equals("1")) {
			loginSessionId = generalUtil.getSessionIdNoException("");
		}
		for (Map.Entry<String, String> entry : elementValueMap.entrySet()) {
			using.append("select '" + formId + "' formid,'" + formCode + "' as formcode_name, '" + saveNameId
					+ "' as save_name_id, '" + formCodeEntity + "' formcode_entity,'" + entry.getKey()
					+ "' entityimpcode,"
					+ generalUtil.handleClob(generalUtil.replaceDBUpdateVal(entry.getValue()))
					+ " entityimpvalue,'" + userId + "' USerID, " + generalUtil.surroundUpperCommaOnVal(sessionId)
					+ " as sessionId, '1' as active, " + generalUtil.surroundUpperCommaOnVal(loginSessionId)
					+ " as login_sessionid from dual union all\n");
		}
		if (!elementValueMap.isEmpty()) {
			temp = using.delete(using.lastIndexOf(" union all"), using.lastIndexOf("l") + 1).toString();
		}
		return temp;
	}

	private String getUsingFordoSaveStruct(String formCode, String userId, String sessionId,
			Map<String, String> elementValueMap, String lastChangeUserId, String lastChangeDate, String isNew, String table) {
		StringBuilder using = new StringBuilder();
		StringBuilder insertCSV = new StringBuilder();
		StringBuilder insertValues = new StringBuilder();
		StringBuilder updateValues = new StringBuilder();
		using.append("(select " + generalUtil.surroundUpperCommaOnVal(formCode)
				+ " as formcode, sysdate TIMESTAMP, 1 ACTIVE," + generalUtil.surroundUpperCommaOnVal(sessionId)
				+ " AS SESSIONID, " + userId + " CHANGED_BY ");
		insertCSV.append("(FORMCODE,TIMESTAMP,CREATED_BY,ACTIVE,SESSIONID,CREATION_DATE,CHANGE_BY");
		insertValues
				.append("(t1.FORMCODE, t1.TIMESTAMP,t1.CHANGED_BY,t1.ACTIVE,t1.SESSIONID,t1.TIMESTAMP,t1.CHANGED_BY");
		//updateValues.append("p.TIMESTAMP = sysdate");
		updateValues.append("p.FORMCODE = t1.FORMCODE,p.TIMESTAMP = sysdate, p.CHANGE_BY = t1.CHANGED_BY");

		for (Map.Entry<String, String> entry : elementValueMap.entrySet()) {
			using.append(",'" + generalUtil.replaceDBUpdateVal(entry.getValue()) + "' " + entry.getKey());
			insertCSV.append("," + entry.getKey());
			insertValues.append(",t1." + entry.getKey());
			if (!entry.getKey().equalsIgnoreCase("formId")) {
				updateValues.append(",p." + entry.getKey() + " = " + "t1." + entry.getKey());
			}
		}

		insertCSV.append(")");
		insertValues.append(")\n");
		FormType formType = generalUtilForm.getFromType(formCode);
		if((formType.equals(FormType.STRUCT)||formType.equals(FormType.INVITEM)) && isNew.equals("0")){
			using.append(" from "+table+" e where e.formid = '"+elementValueMap.get("formId")+"' and e.CHANGE_BY = '"+lastChangeUserId+"' and (to_char(e.TIMESTAMP,'"+generalUtil.getConversionDateTimeSecondsFormat()+"') = '"+lastChangeDate+"' or e.TIMESTAMP is null)) t1\n");
		}else{
			using.append(" from dual) t1\n");
			//using.append("on( p.formid=t1.formid and nvl(p.sessionId,'-1') = nvl(t1.sessionId,'-1') and nvl(p.active,'1') = nvl(t1.active,'1') )\n");
		}
		using.append(
				"on( p.formid=t1.formid and nvl(p.sessionId,'-1') = nvl(t1.sessionId,'-1') and ( ( nvl(p.active,'1') = 0 ) or ( nvl(p.active,'1') = nvl(t1.active,'1') ) or ( nvl(p.active,'1') = '-' || t1.formId ) ) )\n");
		using.append("when not matched then insert " + insertCSV.toString() + " values " + insertValues.toString());
		using.append("when matched then update set " + updateValues.toString() + "\n");

		return using.toString();
	}

	@Override
	public String createStructPivotTable(String formCode, String formId) { //, String pivotTableName
		//   formId_in varchar2, table_in varchar2, table_pivot_in varchar2	
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("formCode_in", formCode);
		parameters.put("dropAndCreateTable_in", "1");
		parameters.put("formid_in", formId);
		return String.valueOf(generalDao.callPackageFunction("", "FG_SET_STRUCT_PIVOT_TABLE", parameters));
	}

	@Override
	public String doSaveTmpDataProduction(String formCode, String formId, String sessionId, String userId) {
		String toReturn = "";
		Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
		String formCodeEntity = generalUtil.getNull(form.getFormCodeEntity(), formCode);

		//		toReturn = generalDao.updateSingleString("update FG_FORMLASTSAVEVALUE_INF t set t.active = 0 where upper(t.formcode_entity) = upper('" + formCodeEntity + "')  and nvl(t.PATH_ID,'-1') = '" + formId + "' ");

		//delete old data with session in null that will be update in next sql
		toReturn = generalDao.updateSingleString(
				" delete from FG_S_" + formCodeEntity + "_PIVOT t where t.sessionId is null and t.PARENTID='" + formId
						+ "' and t.formid in ( select t1.formId from FG_S_" + formCodeEntity
						+ "_PIVOT t1 where t1.sessionId = '" + sessionId + "' and t1.PARENTID='" + formId + "' )  \n ");

		toReturn = updateStructTable(
				" update FG_S_" + formCodeEntity + "_PIVOT t set t.sessionId = null, t.CHANGE_BY = '" + userId
						+ "' where t.sessionId = '" + sessionId + "' and t.PARENTID='" + formId + "'  \n ",
				"FG_S_" + formCodeEntity + "_PIVOT", Arrays.asList("*"), "PARENTID", formId);

		return toReturn;

	}

	@Override
	public String doRemoveProduction(String formCode, String formId, String userId) {
		Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
		String formCodeEntity = generalUtil.getNull(form.getFormCodeEntity(), formCode);

		return generalDao.updateSingleString(" update FG_S_" + formCodeEntity
				+ "_PIVOT t set t.active = 0, t.Change_By = '" + userId + "' where t.formId='" + formId + "' ");
		//TODO update audit trail
	}

	@Override
	public String doRemoveTmpDataProduction(String formCode, String formId, String sessionId, String userId) {
		String r = "";
		String colList = generalDao.getTableColCsv("FG_S_" + formCode + "_PIVOT");
		String colListVal = colList.replace("SESSIONID", "'" + sessionId + "'").replaceAll("ACTIVE", "0");
		Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
		String formCodeEntity = generalUtil.getNull(form.getFormCodeEntity(), formCode);

		//delete rows added in the same transacrion
		r = generalDao.updateSingleString(" delete from FG_S_" + formCodeEntity + "_PIVOT t where t.formId='" + formId
				+ "' and t.sessionId='" + sessionId + "'  ");
		if (generalUtil.getNullInt(r, -1) == 0) {
			//if the record is not part of this session we insert a copy
			r = generalDao.updateSingleString(" insert into FG_S_" + formCodeEntity + "_PIVOT (" + colList
					+ ")  select " + colListVal + " from FG_S_" + formCodeEntity + "_PIVOT  t where t.formId='" + formId
					+ "' and t.sessionId is null  ");
		}
		return r;

	}
	
	@Override
	/**
	 * clones a record 
	 * return new form id
	 * Note: the active number must be -<formId> in order to make it tmp data,it's important to change the active to 1 in the post event!!
	 * Note: the timestamp column must be the current datetime
	 * Note: the user name must be the current one 			
	 */
	public String cloneStructTable(String formId) {
		return cloneStructTable(formId, null);
	}

	@Override
	/**
	 * clones a record 
	 * return new form id
	 * Note: the active number must be -<formId> in order to make it tmp data,it's important to change the active to 1 in the post event!!
	 * Note: the timestamp column must be the current datetime
	 * Note: the user name must be the current one 			
	 */
	public String cloneStructTable(String formId,String cloneWherepart) {
		return cloneStructTable(formId, null,cloneWherepart);
	}
	
	@Override
	/**
	 * clones a record 
	 * return new form id
	 * Note: the active number must be -<formId> in order to make it tmp data
	 * Note: the timestamp column must be the current datetime
	 * Note: the user name must be the current one 			
	 */
	public String cloneStructTable(String formId,Map<String, String> replaceFieldsMap,String cloneWherepart) {
		return cloneStructTable(formId, replaceFieldsMap,null,cloneWherepart);
	}

	@Override
	/**
	 * clones a record 
	 * return new form id
	 * Note: the active number must be -<formId> in order to make it tmp data
	 * Note: the timestamp column must be the current datetime
	 * Note: the user name must be the current one 			
	 */
	public String cloneStructTable(String formId, Map<String, String> replaceFieldsMap, Map<String, String> columns,String cloneWherepart) {
		String formCode = formDao.getFormCodeBySeqId(formId);
		String clonedFormId = getStructFormId(formCode);// gets a new form id

		// get table name
		Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
		String formCodeEntity = generalUtil.getNull(form.getFormCodeEntity(), formCode);
		String table = "FG_S_" + formCodeEntity + "_PIVOT";

		String colListVal = "";
		String colList = "";
		String userId = generalUtil.getSessionUserId();

		if (columns == null || columns.isEmpty()) {
			colList = generalDao.getTableColCsv(table);
			// changes the permanent columns to their new values
			colListVal = "," + colList.replace("ACTIVE", "'-" + clonedFormId + "'").replace("CHANGE_BY", userId)
					.replace("TIMESTAMP", "sysdate").replace("CREATION_DATE", "sysdate").replace("CREATED_BY", userId)
					.replace("FORMID", clonedFormId) + ",";
			if (replaceFieldsMap != null) {
				for (Map.Entry<String, String> entry : replaceFieldsMap.entrySet()) {
					String key = entry.getKey().toUpperCase();
					String value = entry.getValue();
					colListVal = colListVal.replace("," + key + ",",  "," + value + ",");
				}
			}
			colListVal = colListVal.substring(1, colListVal.length() - 1);
		} else {
			//String col[] = colList.split(",");
			Map<String, String> map = new HashMap<String, String>();
			/*for (int i = 1; i < col.length; i++) {
				map.put(col[i], null);
			}*/
			for (Map.Entry<String, String> entry : columns.entrySet()) {
				String key = entry.getKey().toUpperCase();
				String value = entry.getValue();
				map.put(key, value);
			}
			if (replaceFieldsMap != null) {
				for (Map.Entry<String, String> entry : replaceFieldsMap.entrySet()) {
					String key = entry.getKey().toUpperCase();
					String value = entry.getValue();
					map.put(key, value);
				}
			}
			map.put("ACTIVE", "'-" + clonedFormId + "'");
			map.put("CHANGE_BY", userId);
			map.put("TIMESTAMP", "sysdate");
			map.put("CREATION_DATE", "sysdate");
			map.put("CREATED_BY", userId);
			map.put("FORMID", clonedFormId);
			List<String> keys = new ArrayList<>(map.keySet());
			List<String> vals = new ArrayList<>(map.values());
			colList = generalUtil.listToCsv(keys);
			colListVal = generalUtil.listToCsv(vals);
		}
		// insert the cloned record to the table
		// generalDao.updateSingleString(String.format(" insert into %1$s (%2$s)
		// select %3$s from %1$s t where
		// t.formId='%4$s'",table,colList,colListVal,formId)); -> to support
		// also develop annotation - >
		String sql_ = String.format(" insert into %1$s (%2$s)  select %3$s from %1$s  t where t.formId='%4$s'"
									+(generalUtil.getNull(cloneWherepart).isEmpty()?"":" "+cloneWherepart)
				, table,colList, colListVal, formId);
		insertStructTableByFormId(sql_, table, clonedFormId);
		return clonedFormId;
	}

	@Override
	public String updateSingleStringInfo(String sql) {
		String toReturn =  String.valueOf(generalDao.updateSingleString(sql)); //TODO doc + move all calls to updateStructTable / updateStructTableByFormId / deleteStructTable
	
		generalUtilLogger.logWriter(LevelType.INFO,
				"updateSingleStringInfo-SQLevent: [" + sql + "] update " + toReturn + " rows" , ActivitylogType.SQLEvent,
				"-1");
		System.out.println("******** SQLEvent-updateSingleStringInfo: [" + sql + "] return " + toReturn + " rows");
		
		return toReturn;
	}

	@Override
	public String updateSingleStringInfoNoTryCatch(String sql) {
		String toReturn = "";
		toReturn =  String.valueOf(generalDao.updateSingleStringNoTryCatch(sql));
		
		generalUtilLogger.logWriter(LevelType.INFO,
				"updateSingleStringInfo-SQLevent: [" + sql + "] update " + toReturn + " rows" , ActivitylogType.SQLEvent,
				"-1");
		System.out.println("******** SQLEvent-updateSingleStringInfo: [" + sql +  "] update " + toReturn + " rows");
		
		return toReturn;
	}

	@Override
	public String updateAdditinalData(String sql, List<String> colList, String whereFormId) {
		return updateAdditinalData(sql, colList, whereFormId, "", "U");
	}

	private String updateAdditinalData(String sql, List<String> colList, String whereFormId, String string,
			String string2) {
		generalUpdateFormAdditianalData(sql, colList, whereFormId, "", "U");
		return null;
	}

	@Override
	public String updateStructTableByFormId(String sql, String table, List<String> colList, String whereFormId) {
		return generalUpdateSingleString(sql, table, colList, whereFormId, "", "U");
	}

	@Override
	public String updateStructTable(String sql, String table, List<String> colList, String whereCol, String whereId) {
		return generalUpdateSingleString(sql, table, colList, whereId, whereCol, "U");
	}

	@Override
	public String deleteStructTable(String sql, String table, String delCol, String delColId) {
		List<String> colList = new ArrayList<String>();
		colList.add("*");
		return generalUpdateSingleString(sql, table, colList, delColId, delCol, "D");
	}

	@Override
	public String deleteStructTableByFormId(String sql, String table, String delFormId) {
		List<String> colList = new ArrayList<String>();
		colList.add("*");
		return generalUpdateSingleString(sql, table, colList, delFormId, "", "D");
	}

	@Override
	public String insertStructTableByFormId(String sql, String table, String whereFormId) {
		List<String> colList = new ArrayList<String>();
		colList.add("*");
		return generalUpdateSingleString(sql, table, colList, whereFormId, "", "I");
	}

	//	@Override
	//	public String insertStructTable(String sql, String table, String whereCol, String whereId) {
	//		List<String> colList = new ArrayList<String>();
	//		colList.add("*");
	//		return generalUpdateSingleString(sql, table, colList, whereId, whereCol, "I");
	//	}

	private String generalUpdateSingleString(String sql, String table, List<String> colList, String whereFormId,
			String whereCol, String auditTrailChangeType) {
		String formCode = "", update = "";
		String userId = generalUtil.getSessionUserId();
		String sqlOrigin = sql;
		boolean isSqlOrigin = false;

		if (!auditTrailChangeType.equals("D")) {
			try {
				if (auditTrailChangeType.equals("U") && sql.toLowerCase().contains(" set ")) {
					if (!sql.toLowerCase().contains("change_by")) {
						sql = sql.replaceFirst(" (?i)set ", " set CHANGE_BY = '" + userId + "', ");
					}

					if (!sql.toLowerCase().contains("timestamp")) {
						sql = sql.replaceFirst(" (?i)set ", " set timestamp = sysdate, ");
					}
				}

				update = String.valueOf(generalDao.updateSingleStringNoTryCatch(sql));
				logger.info("generalUpdateSingleString sql=" + sql);

			} catch (Exception e) {
				isSqlOrigin = true;
				update = String.valueOf(generalDao.updateSingleStringNoTryCatch(sqlOrigin));
				logger.info("generalUpdateSingleString sqlOrigin=" + sqlOrigin);
			}
			if (update.equals("0")) {
				return update;
			}
		} else {
			update = String.valueOf(generalDao.updateSingleStringNoTryCatch(sql));
			logger.info("generalUpdateSingleString sql=" + sql);
		}

		generalUtilLogger.logWriter(LevelType.INFO,
				"generalUpdateSingleString-SQLevent: [" + (isSqlOrigin?sqlOrigin:sql) + "] update " + update + " rows" , ActivitylogType.SQLEvent,
				"-1");
		System.out.println("******** SQLEvent-generalUpdateSingleString: [" + (isSqlOrigin?sqlOrigin:sql) + "] update " + update + " rows");

		//add from to fg_form_change_list (used by the sched task to complete inf data for this form)
		generalTaskDao.updateSingleString("insert into fg_form_change_list (formcode) values('"
				+ generalUtil.getNull(table, "NA").toUpperCase().replace("FG_S_", "").replace("_PIVOT", "") + "')");

		//TODO find solution for this issue in next versions - by make sure all the mv is always return the current data with the last mv refresh and then we can run it every x minutes (now the problem seems to be only with FG_I_CONNECTION_REQUEST_EXPR_V - effects on the main screen)
		generalTaskDao.updateMVByPivotTable("NA", table, formCode, 2, "", auditTrailChangeType);
		
		generalUtilLogger.logWriter(LevelType.INFO,
				"updateMVByPivotTable-SQLEvent: refresh mv " + table , ActivitylogType.SQLEvent,
				"-1");
		
		System.out.println("******** SQLEvent-updateMVByPivotTable refresh mv " + table);

		return update;
	}

	//	@Override
	//	public String updateSingleStringEvent(String sql, String tableName, Map<String,String> colValMap, String wherePart, String updateType) {
	//		return generalDao.updateSingleString(sql);
	//	} 

	//	

	
	@Override
	public String updateStructTableFormCode(String formCodeEntity, String formCode, String formId,
			boolean updatePivotTable) {
		String updateRows = "";
		updateRows = updateSingleStringInfoNoTryCatch(
				"update FG_SEQUENCE SET FORMCODE = '" + formCode + "' WHERE id = '" + formId + "'");
		if (updatePivotTable) {
			updateRows = updateSingleStringInfoNoTryCatch("update FG_S_" + formCodeEntity.toUpperCase() + "_PIVOT set formcode = '"
					+ formCode + "' where formid = '" + formId + "' ");
		}
		return updateRows;
	}

	@Override
	public String saveAdditionalData(Map<String, String> elementValueMap, List<DataBean> additinalDataSaveList,
			String formCode, String formId) {
		StringBuilder using = new StringBuilder();

		for (DataBean dataBean : additinalDataSaveList) {
			//			System.out.println(dataBean.toString());

			using.append("select '");
			using.append(formId + "' PARENTID,'");
			using.append(dataBean.getCode() + "' ENTITYIMPCODE,'");
			using.append((generalUtil.replaceDBUpdateVal(elementValueMap.get(dataBean.getCode()))) + "' VALUE,'");
			using.append(formCode + "' FORMCODE");
			using.append(" from dual union all\n");
		}

		if (using.length() > 0) {
			using.delete(using.lastIndexOf(" union all"), using.lastIndexOf("l") + 1).toString();

			String sql = "merge into FG_FORMADDITIONALDATA p using ( " + using.toString() + " ) t1"
					+ " on( p.PARENTID=t1.PARENTID and p.ENTITYIMPCODE=t1.ENTITYIMPCODE ) "
					+ "when not matched then insert (PARENTID, ENTITYIMPCODE, VALUE, FORMCODE) values (t1.PARENTID, t1.ENTITYIMPCODE, t1.VALUE, t1.FORMCODE) "
					+ "when matched then update set p.VALUE = t1.VALUE";
			logger.info("savFormTestConfigData sql=" + sql);
			return String.valueOf(generalDao.updateSingleStringNoTryCatch(sql));
		} else {
			return "";
		}
	}

	//	@Override
	//	public String saveAdditionalData(List<DataBean> additinalDataSaveList, String formCode, String formId) {
	//		StringBuilder using = new StringBuilder();	
	// 		
	//		for (DataBean dataBean : additinalDataSaveList) {
	//			System.out.println(dataBean.toString());
	//			
	//			using.append("select '");
	//			using.append(formId + "' PARENTID,'");
	//			using.append(dataBean.getCode() + "' ENTITYIMPCODE,'");
	//			using.append(dataBean.getVal() + "' VALUE,'");
	//			using.append(formCode + "' FORMCODE"); 
	//			using.append(" from dual union all\n");
	//		}
	//		 
	//		if (using.length() > 0) {
	//			using.delete(using.lastIndexOf(" union all"), using.lastIndexOf("l") + 1).toString();				
	//			
	//			String sql = "merge into FG_FORMADDITIONALDATA p using ( " + using.toString() + " ) t1"
	//					+ " on( p.PARENTID=t1.PARENTID and p.ENTITYIMPCODE=t1.ENTITYIMPCODE ) "
	//					+ "when not matched then insert (PARENTID, ENTITYIMPCODE, VALUE, FORMCODE) values (t1.PARENTID, t1.ENTITYIMPCODE, t1.VALUE, t1.FORMCODE) "
	//					+ "when matched then update set p.VALUE = t1.VALUE";
	//			logger.info("savFormTestConfigData sql=" + sql);
	//			return String.valueOf(generalDao.updateSingleStringNoTryCatch(sql)); 
	//		} else {
	//			return "";
	//		}
	//	}
	private String generalUpdateFormAdditianalData(String sql, List<String> colList, String whereFormId,
			String whereCol, String auditTrailChangeType) {
		String update = "";
		String sqlOrigin = sql;
		boolean isOriginSql = false;

		if (!auditTrailChangeType.equals("D")) {

			//			update = String.valueOf(generalDao.updateSingleStringNoTryCatch(sql));
			//			logger.info("generalUpdateSingleString sql=" + sql);  -> yp 26022018->
			//			add change_by / timestamp if not in the sql TODO next version use colList to build the sql string ->
			try {
				/*if(auditTrailChangeType.equals("U") && sql.toLowerCase().contains(" set ")) {
					if(!sql.toLowerCase().contains("change_by")) {
						sql = sql.replaceFirst(" (?i)set ", " set CHANGE_BY = '" + userId + "', ");
					}
					
					if(!sql.toLowerCase().contains("timestamp")) {
						sql = sql.replaceFirst(" (?i)set ", " set timestamp = sysdate, ");
					}
				}*/

				//				update = String.valueOf(generalDao.updateSingleStringNoTryCatch(sql));
				//				logger.info("generalUpdateSingleString sql=" + sql);
				update = String.valueOf(generalDao.updateSingleStringNoTryCatch(sqlOrigin));
				logger.info("generalUpdateSingleString sqlOrigin=" + sqlOrigin);
			} catch (Exception e) {
				isOriginSql = true;
				update = String.valueOf(generalDao.updateSingleStringNoTryCatch(sqlOrigin));
				logger.info("generalUpdateSingleString sqlOrigin=" + sqlOrigin);
			}
			if (update.equals("0")) {
				return update;
			}
		}

		/*String cols = (generalUtil.replaceLast(colList.toString().replaceFirst("\\[", ""), "]", "").replace(", ", ","));
		String formCodeAndId = (auditTrailChangeType.equals("U")) ? " s.FORMCODE_ENTITY as FORMCODE,t.FORMID," : "";
		whereCol = whereCol.equals("") ? "PARENTID" : whereCol;
		String select = "select " + formCodeAndId + cols + " from " + table + " t, FG_SEQUENCE_V s where t.FORMID=s.ID and t." + whereCol + " = '"
				+ whereFormId + "'";
		List<Map<String, Object>> elementValueMapList = generalDao.getListOfMapsBySql(select);
		for (int i = 0; i < elementValueMapList.size(); i++) {
			elementValueMap = elementValueMapList.get(i);
			formCode = (String)elementValueMap.get("FORMCODE");
			if(colMap.size() == 0) {
				colMap = formDao.getFormElementCaseSensitiveName(formCode);
			} 
			
			elementValueMapString = new HashMap<String, String>();
			for (Map.Entry<String, Object> entry : elementValueMap.entrySet()) {
				elementValueMapString.put( generalUtil.getNull(colMap.get(entry.getKey()),entry.getKey()), ((String.valueOf(entry.getValue()).equals("null"))?"":String.valueOf(entry.getValue())));
			}
			formId = elementValueMapString.get("FORMID");
			elementValueMapString.remove("FORMCODE");
			if (auditTrailChangeType.equals("U")) {				
				elementValueMapString.remove("FORMID");
			}
			
			update = doSaveInfoAndAuditTrail(formCode, formId, generalUtil.getSessionUserId(), elementValueMapString, null,
					auditTrailChangeType, false);
		}
		*/
		if (auditTrailChangeType.equals("D")) {
			update = String.valueOf(generalDao.updateSingleStringNoTryCatch(sql));
			logger.info("generalUpdateSingleString sql=" + sql);
		}
		
		generalUtilLogger.logWriter(LevelType.INFO,
				"generalUpdateFormAdditianalData-SQLevent: [" + (isOriginSql?sqlOrigin:sql) + "] update " + update + " rows" , ActivitylogType.SQLEvent,
				"-1");
		System.out.println("******** SQLEvent-generalUpdateFormAdditianalData: [" + (isOriginSql?sqlOrigin:sql) + "] update " + update + " rows");

		return update;
	}

	@Override
	public ActionBean doSaveOnException(Exception e, String formId, String formCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String doRemove(String formCode, String formId, String userId) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public ActionBean doSaveOnException(Exception e, String formId, String formCode) {
//		String update = "";
//		String errMsg = "";
//		generalUtilLogger.logWriter(LevelType.ERROR,
//				"Error in Save event of formId=" + formId + ",formCode = " + formCode, ActivitylogType.SaveException,
//				formId, e, null);
//		e.printStackTrace();
//		if (generalUtil.getNull(e.getMessage()).contains("unique constraint")
//				|| generalUtil.getNull(e.getMessage()).contains("check constraint")) {
//			// uniqueConstraint is the constraint name in the db and the beginning of the dialog message
//			// example 'PROJECT_NAME'
//			String uniqueConstraint = e.getMessage().substring(e.getMessage().lastIndexOf(".") + 1,
//					e.getMessage().lastIndexOf(")"));
//			update = (e.getMessage().contains("unique constraint") ? "-2," : "-3,") + uniqueConstraint;
//		} else {
//			update = "-1";
//			errMsg = e.getMessage();
//		}
//		return new ActionBean("no action needed", generalUtil.StringToList(update), errMsg);
//	}
//	
//	@Override
//	public String doRemove(String formCode, String formId, String userId) {
//
//		String toReturn = "";
//		Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
//		// system USER will remove it totally from the DB
//		if (generalUtil.getSessionUserName().equals("system") || formCode.equals("FormulantRef")) { // system // TODO fix in general > 1.X - workaround because FormulantRef can not be tmp (if temp it was to complicated to implement it)
//			toReturn = formDao.removeFromDB(formId, form.getFormCodeEntity());
//		} else {
//
//			if (generalDao.checkIfColumnExists("fg_s_" + form.getFormCodeEntity() + "_v", "parentid")) {
//				String parentId = formDao.getFormParentId(formCode, formId);
//				String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, parentId);
//				if (sessionId != null) {
//					toReturn = doRemoveTmpDataProduction(formCode, formId, sessionId, userId);
//				} else {
//					doRemoveProduction(formCode, formId, userId);
//				}
//			} else {
//				doRemoveProduction(formCode, formId, userId);
//			}
//		}
//
//		//update cache
//		try {
//			//generalTask.updateCach(integrationEvent.getUpdateCacheFormList(formCode));
//			generalTask.updateCach(formCode);
//		} catch (Exception e) {
//			// DO NOTHING
//		}
//
//		return toReturn;
//	}
}