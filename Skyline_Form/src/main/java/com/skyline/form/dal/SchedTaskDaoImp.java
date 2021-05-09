package com.skyline.form.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.skyline.form.entity.Element;
import com.skyline.form.entity.Entity;
import com.skyline.form.service.CacheService;
import com.skyline.form.service.GeneralUtil;

@Repository("SchedTaskDao")
public class SchedTaskDaoImp implements SchedTaskDao {

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private CacheService cacheService;

	@Autowired
	private GeneralDao generalDao;

	@Autowired
	private ChemDao chemDao;

	@Value("${jdbc.driverClassName}")
	private String driverClassName;
	@Value("${jdbc.username}")
	private String username;
	@Value("${jdbc.url}")
	private String url;
	@Value("${jdbc.password}")
	private String password;

	@Override
	public void updateResultSearch(String dbTransactionId) {
		//********** result
		String sql = "delete from FG_S_RESULTAUDITTRAIL_PIVOT where not exists (select 1 from FG_RESULTS where FG_RESULTS.result_id = FG_S_RESULTAUDITTRAIL_PIVOT.Result_Id and FG_RESULTS.RESULT_IS_ACTIVE = '1')";
		generalDao.updateSingleStringNoTryCatch(sql);

		sql = "merge into FG_S_RESULTAUDITTRAIL_PIVOT t using "
				+ " (select * from FG_RESULTS where RESULT_IS_ACTIVE = '1') r" + " on (t.RESULT_ID = r.RESULT_ID)"
				+ " when not matched then insert (ACTIVE,FORMID,FORMCODE,FORMCODE_ENTITY,CHANGE_BY,CREATED_BY,CREATION_DATE,TIMESTAMP,RESULT_ID,EXPERIMENT_ID,MATERIAL_ID,SAMPLE_ID,VALUE,UOM_ID,ResultAuditTrailName,ResultTestName,PARENTID)"
				+ " values ('1',fg_get_struct_form_id('RESULTAUDITTRAIL'),'RESULTAUDITTRAIL','RESULTAUDITTRAIL',r.RESULT_CHANGE_BY,r.RESULT_CHANGE_BY,r.RESULT_DATE,r.RESULT_DATE,r.RESULT_ID,r.EXPERIMENT_ID,r.RESULT_MATERIAL_ID,r.SAMPLE_ID,r.RESULT_VALUE,r.RESULT_UOM_ID,r.RESULT_NAME,r.RESULT_TEST_NAME,r.EXPERIMENT_ID)"
				+ " when matched then update set t.MATERIAL_ID = r.RESULT_MATERIAL_ID,t.SAMPLE_ID = r.SAMPLE_ID, t.VALUE = r.RESULT_VALUE, t.UOM_ID = r.RESULT_UOM_ID, t.ResultAuditTrailName = r.RESULT_NAME, t.ResultTestName = r.RESULT_TEST_NAME, t.PARENTID = r.EXPERIMENT_ID, "
				+ " t.CHANGE_BY = r.RESULT_CHANGE_BY, t.TIMESTAMP = r.RESULT_DATE";
		generalDao.updateSingleStringNoTryCatch(sql);

		//update formidname in the fg_sequence
		sql = "update FG_SEQUENCE t SET FORMIDNAME = (select FG_S_RESULTAUDITTRAIL_PIVOT.ResultAuditTrailName from FG_S_RESULTAUDITTRAIL_PIVOT where FG_S_RESULTAUDITTRAIL_PIVOT.formid = t.id) \r\n" + 
				"where exists (select 1 from FG_S_RESULTAUDITTRAIL_PIVOT where FG_S_RESULTAUDITTRAIL_PIVOT.formid = t.id and nvl(t.FORMIDNAME,',') <> FG_S_RESULTAUDITTRAIL_PIVOT.ResultAuditTrailName)\r\n" + 
				"and upper(t.formcode) = 'RESULTAUDITTRAIL'";
		generalDao.updateSingleStringNoTryCatch(sql);
	}

	@Override
	public void updateWebixSearch(String dbTransactionId) {
		//********** webix
		String sql = "delete from FG_S_WEBIXAUDITTRAIL_PIVOT where not exists (select 1 from FG_WEBIX_OUTPUT where FG_WEBIX_OUTPUT.result_id = FG_S_WEBIXAUDITTRAIL_PIVOT.result_id and RESULT_IS_ACTIVE = '1')";
		generalDao.updateSingleStringNoTryCatch(sql);

		sql = "merge into FG_S_WEBIXAUDITTRAIL_PIVOT t using "
				+ " (select * from FG_WEBIX_OUTPUT where RESULT_IS_ACTIVE = '1') r" + " on (t.RESULT_ID = r.RESULT_ID)"
				+ " when not matched then insert (ACTIVE,FORMID,FORMCODE,FORMCODE_ENTITY,CHANGE_BY,CREATED_BY,CREATION_DATE,TIMESTAMP,RESULT_ID,EXPERIMENT_ID,MATERIAL_ID,SAMPLE_ID,VALUE,UOM_ID,WebixAuditTrailName,ResultTestName,PARENTID)"
				+ " values ('1',fg_get_struct_form_id('WEBIXAUDITTRAIL'),'WEBIXAUDITTRAIL','WEBIXAUDITTRAIL',r.WEBIX_CHANGE_BY,r.WEBIX_CHANGE_BY,r.RESULT_DATE,r.RESULT_DATE,r.RESULT_ID,r.EXPERIMENT_ID,r.MATERIAL_ID,r.SAMPLE_ID,r.RESULT_VALUE,r.RESULT_UOM_ID,r.RESULT_NAME,r.RESULT_TEST_NAME,r.EXPERIMENT_ID)"
				+ " when matched then update set t.MATERIAL_ID = r.MATERIAL_ID,t.SAMPLE_ID = r.SAMPLE_ID, t.VALUE = r.RESULT_VALUE, t.UOM_ID = r.RESULT_UOM_ID, t.WebixAuditTrailName = r.RESULT_NAME, t.ResultTestName = r.RESULT_TEST_NAME, t.PARENTID = r.EXPERIMENT_ID, "
				+ " t.CHANGE_BY = r.WEBIX_CHANGE_BY, t.TIMESTAMP = r.RESULT_DATE";
		generalDao.updateSingleStringNoTryCatch(sql);

		//update formidname in the fg_sequence
		sql = "update FG_SEQUENCE t SET FORMIDNAME = (select FG_S_WEBIXAUDITTRAIL_PIVOT.WebixAuditTrailName from FG_S_WEBIXAUDITTRAIL_PIVOT where FG_S_WEBIXAUDITTRAIL_PIVOT.formid = t.id) \r\n" + 
				"where exists (select 1 from FG_S_WEBIXAUDITTRAIL_PIVOT where FG_S_WEBIXAUDITTRAIL_PIVOT.formid = t.id and nvl(t.FORMIDNAME,',') <> FG_S_WEBIXAUDITTRAIL_PIVOT.WebixAuditTrailName)\r\n" + 
				"and upper(t.formcode) = 'WEBIXAUDITTRAIL'";
		generalDao.updateSingleStringNoTryCatch(sql);
	}

	@Override
	public void updateFilesSrcTable(String dbTransactionId) {
		
		//refresh FG_I_FILESOBJECT_CONNECTION_MV
		generalDao.updateSingleString("begin dbms_mview.refresh('FG_I_FILESOBJECT_CONNECTION_MV'); end;");
		
		//Remove unused files
		String sql = "update fg_formlastsavevalue_inf i set i.entityimpvalue = (\r\n" + "       select f.FILE_ID\r\n"
				+ "       from FG_I_FILESOBJECT_CONNECTION_MV f\r\n" + "       where f.parentid = i.formid\r\n" + ")\r\n"
				+ "where 1=1\r\n" + "and i.formcode_entity = 'Document'\r\n" + "and i.entityimpcode = 'documentUpload'";
		generalDao.updateSingleStringNoTryCatch(sql);

		sql = "update FG_FORMLASTSAVEVALUE_INF t1 set t1.is_file = 1\r\n" + "where t1.id in (\r\n" + " select t.id\r\n"
				+ " from  FG_FORMLASTSAVEVALUE_inf t,\r\n" + "       FG_FORMELEMENTINFOATMETA_MV m,\r\n"
				+ "       fg_sequence s_element\r\n" + "  where t1.id = t.id\r\n" + "  and t.formid = s_element.id\r\n"
				+ "  and upper(m.formcode) = upper(s_element.formcode)\r\n"
				+ "  and upper(m.entityimpcode) = upper(t.entityimpcode)\r\n"
				+ "  and m.elementclass = 'ElementUploadFileImp'\r\n" + ")";
		generalDao.updateSingleStringNoTryCatch(sql);

		//convertMolFilesDocument 
		try {
			String sql_ = "select t.DOCUMENTUPLOAD from fg_s_document_all_v t where  lower(t.FILE_NAME) like '%.mol' and (t.file_display_id is null or t.file_chem_id is null)";
			List<String> fileIdList = generalDao.getListOfStringBySql(sql_);
			for (String fileId : fileIdList) {
				String molData = generalDao.getSingleStringFromBlob(
						"select file_content file_id from fg_files where file_id = '" + fileId + "'");
				JSONObject jo;
				jo = chemDao.saveDocData(molData);
				String displayId_ = (String) jo.get("imgId");
				String checmId = (String) jo.get("chemId");
				generalDao.updateSingleString("update fg_files t set t.file_display_id = '" + displayId_
						+ "', t.file_chem_id = '" + checmId + "' where t.file_id = '" + fileId + "'");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		generalDao.updateSingleStringNoTryCatch(
				"delete from fg_files_src where file_id not in (select file_id from FG_I_FILESOBJECT_CONNECTION_MV)");
		sql = "insert into fg_files_src(file_id,file_name,file_content,content_type,timestamp,parentid,cd_smiles) "
				+ " select f.file_id,f.file_name,f.file_content,f.content_type,f.timestamp,c.parentid,t.cd_smiles "
				+ " from fg_files f,FG_I_FILESOBJECT_CONNECTION_MV c , FG_CHEM_DOC_SEARCH t "
				+ " where t.cd_id(+) = f.file_chem_id and f.file_id = c.file_id and f.file_id not in(select file_id from fg_files_src) and (lower(file_name) like '%.mol' or content_type like '%.doc%' or content_type like '%pdf%' or(content_type like 'application/octet-stream' and file_name like '%.docx'))";

		//insert missing files
		generalDao.updateSingleStringNoTryCatch(sql);
		generalDao.updateSingleStringNoTryCatch("alter index search_idx rebuild parameters( 'sync' )");
	}

	@Override
	public void updateFgSeqSearchMatch(String formCode, String fromFormId) {
		String sql = "SELECT T.ID || ',' || T.FORMPATH FROM FG_SEQUENCE T WHERE T.FORMPATH IS NOT NULL AND ( nvl(T.SEARCH_MATCH_ID1,T.SEARCH_MATCH_ID4) is null)"; // path_id has parentid in popup forms
		if (formCode != null && !formCode.isEmpty()) {
			sql += " and upper(nvl(formcode,'no_data')) = upper('" + formCode + "') ";
		}

		if (fromFormId != null && !fromFormId.isEmpty() && !fromFormId.equals("-1")) {
			sql += " and T.ID >= " + fromFormId + " ";
		}
		try {
			List<String> idPathList = generalDao.getListOfStringBySql(sql);
			//update name in FG_SEQUENCE
			for (String idPath_ : idPathList) {
				int indx = idPath_.indexOf(",");
				String id_ = idPath_.substring(0, indx);
				String path_ = idPath_.substring(indx + 1);
				String searchMatchSet = getSearchMatchSet(path_);
				try {
					sql = "update FG_SEQUENCE SET CHANGEDATE = sysdate, comments='update by GeneralTask.updateFgSeqSearchMatch' "
							+ generalUtil.getEmpty(searchMatchSet, ", SEARCH_MATCH_ID1 = -1") + " where id ='" + id_
							+ "'";
					generalDao.updateSingleStringNoTryCatch(sql);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
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

	@Override
	/** TODO
	 * clean from FG_FORMLASTSAVE_TRANSACT_FAIL transaction numbers that are not exists in fg_formlastsavevalue_inf
	 * from version 1.520 it is not needed because we merge inf data only when that transaction completed successfully  
	 */
	public void handelTransactionFailure() {

		generalDao.updateSingleString("begin truncate_it('FG_FORMLASTSAVE_TRANSACT_FAIL'); end;");

		//		String sql =
		//				"		 update FG_FORMLASTSAVE_TRANSACT_FAIL set delete_flag=1 ";
		//		generalDao.updateSingleStringNoTryCatch(sql);
		//		
		//		sql =
		//				"		update fg_formlastsavevalue_inf t\r\n" + 
		//				"		set t.entityimpvalue = t.tmp_entityimpvalue, \r\n" + 
		//				"		    t.DISPLAYVALUE = t.TMP_DISPLAYVALUE, \r\n" + 
		//				"		    t.change_date = sysdate, \r\n" + 
		//				"		    t.db_transaction_id = null, \r\n" + 
		//				"		    t.change_comment = 'system: rollback on transaction failure'\r\n" + 
		//				"		where t.db_transaction_id is not null and t.db_transaction_id in (\r\n" + 
		//				"		      select f.transaction_failure_number from FG_FORMLASTSAVE_TRANSACT_FAIL f where nvl(f.delete_flag,0)=1 \r\n" + 
		//				"		)  ";
		//		generalDao.updateSingleStringNoTryCatch(sql);
		//		
		//		sql =
		//				"		 delete from FG_FORMLASTSAVE_TRANSACT_FAIL where nvl(delete_flag,0) = 1 ";
		//		generalDao.updateSingleStringNoTryCatch(sql); 
	}

	//	DB_TRANSACTION_ID_IN VARCHAR2,
	//    exe_missing_row_in number default 0,
	//    exe_complete_data_in number default 0,
	//    fromFormIdNumInput_in number default -1,
	//    correct_all_path_obj_in number default 0

	@Override
	/** 
	 * correct fg_sequance and fg_formlastsavevalue_inf data
	 */
	public void correctInfTable(boolean completeRows, boolean completeData, boolean onLastChangesOnly,
			boolean correctAllPathObj) throws Exception {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("DB_TRANSACTION_ID_IN", "999");
		parameters.put("exe_missing_row_in", completeRows ? "1" : "0");
		parameters.put("exe_complete_data_in", completeData ? "1" : "0");
		//parameters.put("formCodeCsvInput_in", formcodeList);
		parameters.put("onLastChangesFlag_in", onLastChangesOnly ? "1" : "0");
		parameters.put("correct_all_name_path_obj_in", correctAllPathObj ? "1" : "0");
		generalDao.callPackageFunction("FG_ADAMA_TASK_BY_DATE", "FG_SET_INF_INIT_DATA_ALL", parameters);
	}

	@Override
	/**
	 * display values with id as value (other than DDL which taken from FG_SEQUANCE). Here we can make also manipulation on the display values
	 */
	public void fixInfTableDisplayValues(String dbTransactionId, boolean onLastChangesOnly) throws Exception {
		//		Map<String, ElementInfoAuditTrailMeta> eMap = cacheService.getFormElementInfoAuditTrailMetaMap();

		Class.forName(driverClassName);
		Connection conn = DriverManager.getConnection(url, username, password);
		Statement stmt = null;
		ResultSet rs = null;
		String optionalWherePart = "";
		if (onLastChangesOnly) {
			optionalWherePart = " and change_date > sysdate - 1/24 ";
		}
		
		try {
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("onLastChangesFlag_in", onLastChangesOnly ? "1" : "0");
			generalDao.callPackageFunction("FG_ADAMA_TASK_BY_DATE", "FG_SET_DIPLAY_VALUE", parameters);
		} catch (Exception e1) {
			// do noting because this is not ready yet in the db TODO imp in the DB
		}

		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(
					"select id_,class_,val_ FROM FG_FORMLASTSAVEVALUE_FIXDISP_V where 1=1 " + optionalWherePart);
			final int batchSize = 100;
			int count = 0;
			//		String displayVal_ = "";

			PreparedStatement u = conn.prepareStatement(
					"update FG_FORMLASTSAVEVALUE_inf t set t.displayvalue = ?, Change_Comment = 'system: display value update by task', t.Change_Date = sysdate where t.id = ?");
			while (rs.next()) {
				String displayVal_ = null;
				String id_ = rs.getString("id_");
				String class_ = rs.getString("class_");
				String val_ = rs.getString("val_");
				Entity e = cacheService.getFormEntityClassSingleToneMap().get(class_);
				if (e instanceof Element) {
					displayVal_ = ((Element) e).getAuditTrailValue("", "", val_, "", "").getValue();
				}
				u.setString(1, displayVal_);
				u.setString(2, id_);
				u.addBatch();

				++count;

				if (count % batchSize == 0) {
					System.out.println("----- fixInfTableDisplayValues exe batch count rows: " + batchSize);
					u.executeBatch();
					u.clearBatch();
				}

			}
			u.executeBatch();
			u.clearBatch();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
					rs.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	/**
	 * If the DB func (FG_ADAMA_TASK.FG_SET_SERACH_HANDEL_INF_ID) not set (and it is not set when writing this lines) we use the java code to be able to implement is from both (DB imp can be use to override this java Imp))
	 */
	public void handelInfId(String dbTransactionId) throws Exception {

		try {
			Map<String, String> parameters = new HashMap<String, String>();
			parameters.put("DB_TRANSACTION_ID_IN", "999");
			generalDao.callPackageFunction("FG_ADAMA_TASK_BY_DATE", "FG_SET_SERACH_HANDEL_INF_ID", parameters);
		} catch (Exception e) {
			// delete old
			generalDao.updateSingleStringNoTryCatch(
					"delete from FG_FORMLASTSAVEVALUE_INF t where nvl(t.IS_IDLIST,0) = 2 ");

			// insert new
			String sql = "insert into FG_FORMLASTSAVEVALUE_INF(id,  \r\n"
					+ "                                      formid,  \r\n"
					+ "                                      formcode_entity,  \r\n"
					+ "                                      entityimpcode,  \r\n"
					+ "                                      entityimpvalue,  \r\n"
					+ "                                      userid,  \r\n"
					+ "                                      change_comment,  \r\n"
					+ "                                      change_id,  \r\n"
					+ "                                      change_by,  \r\n"
					+ "                                      change_type,  \r\n"
					+ "                                      change_date,  \r\n"
					+ "                                      sessionid,  \r\n"
					+ "                                      active,  \r\n"
					+ "                                      displayvalue,  \r\n"
					+ "                                      updatejobflag,  \r\n"
					+ "                                      displaylabel,  \r\n"
					+ "                                      path_id,  \r\n"
					+ "                                      is_file,  \r\n"
					+ "                                      is_idlist,  \r\n"
					+ "                                      DB_TRANSACTION_ID)\r\n"
					+ " select                               t.id,  \r\n"
					+ "                                      t.formid,  \r\n"
					+ "                                      t.formcode_entity,  \r\n"
					+ "                                      t.entityimpcode,  \r\n"
					+ "                                      S.ID AS entityimpvalue,  \r\n"
					+ "                                      t.userid,  \r\n"
					+ "                                      t.change_comment,  \r\n"
					+ "                                      t.change_id,  \r\n"
					+ "                                      t.change_by,  \r\n"
					+ "                                      t.change_type,  \r\n"
					+ "                                      t.change_date,  \r\n"
					+ "                                      t.sessionid,  \r\n"
					+ "                                      t.active,  \r\n"
					+ "                                      null as displayvalue,  \r\n"
					+ "                                      t.updatejobflag,  \r\n"
					+ "                                      t.displaylabel,  \r\n"
					+ "                                      t.path_id,  \r\n"
					+ "                                      t.is_file,  \r\n"
					+ "                                      2 is_idlist,  \r\n"
					+ "                                      t.DB_TRANSACTION_ID  \r\n"
					+ "  from FG_FORMLASTSAVEVALUE_INF t, FG_SEQUENCE S\r\n" + "  where nvl(t.is_idlist,0) = 1 \r\n"
					+ "  and   t.entityimpvalue is not null \r\n" + "  AND  T.ENTITYIMPVALUE LIKE '%,%'\r\n"
					+ "  and   instr(',' || t.entityimpvalue || ',',',' || s.id || ',')  > 0 ";
			generalDao.updateSingleStringNoTryCatch(sql);
		}
	}

	public void inserIntoFgSysSched(String name, String startDate, String endDate, String status, String interval) {
		String comments = name + ": start date is " + startDate + ", end date is " + endDate + ", status=" + status;
		String startDateSuccessHolderExp = status != null && status.equalsIgnoreCase("S")? "s.start_date":"s.start_date_success_holder";
		String sql = "merge into FG_SYS_SCHED  s " + " using  (select '" + name + "' as name, '" + startDate
				+ "' as startDate, '" + endDate + "' as endDate, '" + status + "' as status, '" + interval
				+ "' as interval, '" + comments + "' as comments from dual) d " + " on (d.name = s.sched_name) "
				+ " when matched then "
				+ "   UPDATE SET s.start_date = to_date(d.startDate,'DD/MM/YYYY HH24:MI:SS'), s.end_date = to_date(d.endDate,'DD/MM/YYYY HH24:MI:SS'), s.status = d.status, s.interval_time  = d.interval, s.comments=d.comments, s.start_date_success_holder = " + startDateSuccessHolderExp
				+ " when not matched  then "
				+ "   INSERT     (sched_name, start_date, end_date, status, interval_time,comments) "
				+ "       VALUES (d.name,to_date(d.startDate,'DD/MM/YYYY HH24:MI:SS'),to_date(d.endDate,'DD/MM/YYYY HH24:MI:SS'),  d.status, d.interval, d.comments)";
		generalDao.updateSingleStringNoTryCatch(sql);
		generalDao.updateSingleStringNoTryCatch(
				"update FG_SYS_SCHED s set s.LAST_END_DATE = s.END_DATE where s.END_DATE is not null and s.sched_name='"
						+ name + "'");
	}

	@Override
	public void dbCleanup() {
		Map<String, String> parameters = new HashMap<String, String>();
		generalDao.callPackageFunction("FG_ADAMA_TASK_BY_DATE", "DB_CLEANUP", parameters);
	}
}
