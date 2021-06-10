package com.skyline.form.dal;
// FormDaoDBImp

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.BeanType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.Form;
import com.skyline.form.bean.FormAdditionalData;
import com.skyline.form.bean.FormEntity;
import com.skyline.form.bean.FormLastSaveValue;
import com.skyline.form.bean.FormType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.LookupType;
import com.skyline.form.bean.SqlPermissionListObj;
import com.skyline.form.service.CacheService;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilConfig;
import com.skyline.form.service.GeneralUtilFormState;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.form.service.GeneralUtilPermission;

@Repository("FormDao")
public class FormDaoDBImp extends BasicDao implements FormDao {

	//	private JdbcTemplate jdbcTemplate;

	//	private SimpleJdbcCall func_fg_get_struct_form_id;

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private GeneralDao generalDao;

	@Autowired
	private FormSaveDao formSaveDao;

	@Autowired
	private FormBuilderDao formBuilderDao;

	@Autowired
	private CacheService cachService;

//	@Autowired
//	private IntegrationInitForm integrationInitForm;

	@Autowired
	public GeneralUtilLogger generalUtilLogger;
	
	@Autowired
	private GeneralUtilPermission generalUtilPermission;
	
	@Autowired
	private GeneralUtilConfig generalUtilConfig;
	
	@Autowired
	private GeneralUtilFormState generalUtilFormState;

	//	@Autowired
	//	private FormDao formDao;

	@Value("${HtmlPath}")
	private String Htmlpath;

	@Value("${jspPath}")
	private String jspPath;

	@Value("${synchronizeFromData:0}")
	private int synchronizeFromData;
	
	private static final Logger logger = LoggerFactory.getLogger(FormBuilderDaoDBImp.class);

	//	@Autowired
	//	public void setDataSource(DataSource dataSource) {
	//		this.jdbcTemplate = new JdbcTemplate(dataSource);
	//	}

	public class DataBeanMapper implements RowMapper<DataBean> {
		public DataBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			DataBean DataBean = new DataBean(Long.parseLong(rs.getString("ID")), BeanType.valueOf(rs.getString("type")),
					rs.getString("Code"), rs.getString("value"), rs.getString("info"));
			return DataBean;
		}
	}

	public class FormLastSaveValueMapper implements RowMapper<FormLastSaveValue> {
		public FormLastSaveValue mapRow(ResultSet rs, int rowNum) throws SQLException {
			FormLastSaveValue formLastSaveValue = new FormLastSaveValue(Long.parseLong(rs.getString("ID")),
					rs.getString("FORMID"), rs.getString("FORMCODE_ENTITY"), rs.getString("ENTITYIMPCODE"),
					rs.getString("ENTITYIMPVALUE"), rs.getString("USERID"));
			return formLastSaveValue;
		}
	}

	public class FormAdditionalDataMapper implements RowMapper<FormAdditionalData> {
		public FormAdditionalData mapRow(ResultSet rs, int rowNum) throws SQLException {
			FormAdditionalData formAdditionalData = new FormAdditionalData(Long.parseLong(rs.getString("ID")),
					rs.getString("PARENTID"), rs.getString("ENTITYIMPCODE"), rs.getString("VALUE"),
					rs.getString("CONFIG_ID"), rs.getString("FORMCODE"), rs.getString("INFO"));
			return formAdditionalData;
		}
	}

	@Override
	public List<FormEntity> getFormEntityInfoLookup(String formCode, String type) {
		List<FormEntity> formEntityList = new ArrayList<FormEntity>();
		try {
			List<FormEntity> formEntityList_ = cachService.getFormEntityList();
			//			formEntityList = jdbcTemplate.query(
			//					"select * from FG_FORMENTITY where ENTITYTYPE LIKE '" + type + "' and FORMCODE='" + formCode + "'",
			//					new FormEntityMapper()); 
			if (!formEntityList_.isEmpty()) {
				if (synchronizeFromData == 1) {
					synchronized (formEntityList_) {
						Iterator<FormEntity> it = formEntityList_.iterator();

						while (it.hasNext()) {
							FormEntity formEntity = it.next();
							if (generalUtil.isSqlLike(formEntity.getFormCode(), formCode)
									&& generalUtil.isSqlLike(formEntity.getEntityType(), type)) {
								formEntityList.add(formEntity);
							}
						}
					}
				} else {
					Iterator<FormEntity> it = formEntityList_.iterator();

					while (it.hasNext()) {
						FormEntity formEntity = it.next();
						if (generalUtil.isSqlLike(formEntity.getFormCode(), formCode)
								&& generalUtil.isSqlLike(formEntity.getEntityType(), type)) {
							formEntityList.add(formEntity);
						}
					}
				}
			} else {
				formEntityList = formBuilderDao.getFormEntity(formCode, type);
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();

			formEntityList = formBuilderDao.getFormEntity(formCode, type);
		}
		return formEntityList;
	}

	//	public class FormEntityMapper implements RowMapper<FormEntity> {
	//		public FormEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
	//			FormEntity formEntity = new FormEntity(Long.parseLong(rs.getString("ID")), rs.getString("FORMCODE"),
	//					Integer.parseInt(rs.getString("NUMBEROFORDER")), rs.getString("ENTITYTYPE"),
	//					rs.getString("ENTITYIMPCODE"), rs.getString("ENTITYIMPCLASS"), rs.getString("ENTITYIMPINIT"));
	//			return formEntity;
	//		}
	//	}

	public class DataBeanMapperForResourceLookUp implements RowMapper<DataBean> {
		public DataBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			DataBean DataBean = new DataBean(Long.parseLong(rs.getString("ID")), BeanType.valueOf(rs.getString("type")),
					rs.getString("Code"), rs.getString("value"), rs.getString("info"));
			return DataBean;
		}
	}

	public String checkIfFormCodeExist(String sql) {
		return generalDao.selectSingleString(sql);
	}

	//	public class FormMapper implements RowMapper<Form> {
	//		public Form mapRow(ResultSet rs, int rowNum) throws SQLException {
	//			Form form = new Form(rs.getString("FORMCODE"), rs.getString("DESCRIPTION"), rs.getString("ACTIVE"),
	//					rs.getString("FORM_TYPE"), rs.getString("TITLE"), rs.getString("SUBTITLE"),
	//					rs.getString("USE_AS_TEMPLATE"), rs.getString("GROUP_NAME"), rs.getInt("NUMBEROFORDER"), rs.getString("FORMCODE_ENTITY"), rs.getString("IGNORE_NAV"), rs.getString("USECACHE"));
	//			return form;
	//		}
	//	}

	@Override
	public List<Form> getFormInfoLookup(String formCode, String formType, boolean includeInactiveForms) {
		List<Form> formList = new ArrayList<Form>();
		//		String sql = "select * from FG_FORM where UPPER(FORMCODE) like UPPER('" + formCode + "') and FORM_TYPE like '" + formType
		//				+ "'" + ((includeInactiveForms) ? "" : " and active = 1 ")
		//				+ " order by FORM_TYPE,NVL(NUMBEROFORDER,0), FORMCODE ";
		try {
			//			formList = jdbcTemplate.query(sql, new FormMapper());
			List<Form> formList_ = cachService.getFormList();
			if (!formList_.isEmpty()) {
				if (synchronizeFromData == 1) {
					synchronized (formList_) {
						Iterator<Form> it = formList_.iterator();
						while (it.hasNext()) {
							Form form = it.next();
							if (generalUtil.isSqlLike(form.getFormCode(), formCode)
									&& generalUtil.isSqlLike(form.getFormType(), formType)
									&& (includeInactiveForms || generalUtil.getNull(form.getActive()).equals("1"))) {
								formList.add(form);
							}
						}
					}
				} else {
					Iterator<Form> it = formList_.iterator();
					while (it.hasNext()) {
						Form form = it.next();
						if (generalUtil.isSqlLike(form.getFormCode(), formCode)
								&& generalUtil.isSqlLike(form.getFormType(), formType)
								&& (includeInactiveForms || generalUtil.getNull(form.getActive()).equals("1"))) {
							formList.add(form);
						}
					}
				}
			} else {
				formList = formBuilderDao.getForm(formCode, formType, includeInactiveForms);
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();

			formList = formBuilderDao.getForm(formCode, formType, includeInactiveForms);
		}
		return formList;
	}

	//	@Override
	//	public String getFormEntityInit(String formCode, String entityCode, String type) {
	//		return generalDao.selectSingleString("select json_ENTITYIMPINIT from fg_formentity_v where ENTITYTYPE LIKE '"
	//				+ type + "' and FORMCODE='" + formCode + "' and ENTITYIMPCODE='" + entityCode + "'");
	//	}

	@Override
	public List<String> getTreeData(String metaData, String table, String wherePart, String path) {
		String userId = generalUtil.getSessionUserId();
		String addQuotes = metaData;
		addQuotes = ((addQuotes.indexOf(" ") != -1) && (addQuotes.indexOf("\"") != 0)) ? "\"" + addQuotes + "\""
				: addQuotes;
		List<String> toReturn = new ArrayList<String>();
		try {
			SqlPermissionListObj permissionListSql = generalUtilPermission.getPermissionListSql("NavigationTree",metaData,table,userId);
			if (!generalUtil.getNull(permissionListSql.getSql()).isEmpty() && !metaData.equals("NA")) {
				String unionPart = "";
				if (!generalUtil.getNull(path).isEmpty()) {
					JSONObject json = new JSONObject(path);
					JSONArray pathList = json.getJSONArray("path");
					for (int i = 0; i < pathList.length(); i++) {
						String p = pathList.get(i).toString();
						String[] detailsToDisplay = generalUtil.getJsonValById(p, "name").split(":");
						String id = generalUtil.getJsonValById(p, "id");
						String formC = detailsToDisplay.length > 1 ? generalUtil.getJsonValById(p, "name").split(":")[0]
								: "";
						if(formC.equalsIgnoreCase(metaData) || ((formC.equalsIgnoreCase("SubSubProject")||formC.equalsIgnoreCase("Experiment")) && metaData.equals("SE"))){
							unionPart += " union all select '"+id+"' from dual";
							break;
						}
					}
				}
				String sql = "select distinct " + addQuotes + " from " + table + " t where 1=1 " + wherePart;
				String fullSql = "select * from (WITH "
						+ " PERM_SQL_ALL AS ("+permissionListSql.getSql()+ unionPart +" ) " + sql 
						+ " and " + permissionListSql.getObjectId() + " in (SELECT * FROM PERM_SQL_ALL)"  + ") where 1=1";
				/*toReturn = generalDao.getCSVBySql(fullSql,
						false);*/
				toReturn = generalDao.getListOfStringBySql(fullSql);
			}else if(!metaData.equals("NA")){
				/*toReturn = generalDao.getCSVBySql("select distinct " + addQuotes + " from " + table + " t where 1=1 " + wherePart,
						false);*/
				toReturn = generalDao.getListOfStringBySql("select distinct " + addQuotes + " from " + table + " t where 1=1 " + wherePart);
			}
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toReturn;
		
		
	}
	
	@Override
	public String getCurrentFormNumberId(String table, String wherePart) {
		String currentFormNumberId = generalDao.selectSingleString(" select max(formNumberId) from " + table
				+ " where formNumberId is not null and formNumberId not like '-%' and nvl(active,'1')='1'" + wherePart);
		return currentFormNumberId;
	}

	@Override
	public String getFormsId(String table) {
		// get list of the forms' id's for the formBuilder
		return generalDao.getCSVBySqlNoException("select FORMID from ( select distinct FORMID from " + table
				+ " order by FORMID desc ) where rownum <= 10", false);
	}

	@Override
	public String getFormNumberIdByFormId(String table, String formId) {
		String toRetun = "-1";
		//		if(!isStructTable) { // 
		//			toRetun = "-1";
		//		} else {
		try {
			toRetun = generalDao.getCSVBySqlNoException(
					"select FORMNUMBERID from " + table + " where formId  = " + formId + " and nvl(active,'1')='1'",
					false);
		} catch (Exception e) {
			toRetun = "";
		}
		//		}

		return toRetun;
	}

	/**
	 * The function return true when
	 * formId not exists (new form)	
	 * or
	 * when formId exists with active 'minus formId' (cloned form)
	 */
	@Override
	public boolean isNewFormId(String formCode, String formId) {
		Form form = getFormInfoLookup(formCode, "%", true).get(0);
		boolean toReturn = true;
		try {
			String count = generalDao.getCSVBySqlNoException(
					"select count(*) from FG_S_" + generalUtil.getNull(form.getFormCodeEntity(), formCode)
							+ "_PIVOT where active <> '-" + formId + "' and formId  = '" + formId + "'",
					false);
			if (Integer.parseInt(count) > 0) {
				toReturn = false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			toReturn = true;
		}
		return toReturn;
	}

	//	/**
	//	 * get site of project which his type is formulation
	//	 */
	//	@Override
	//	public List<Map<String,Object>> getListOfMapsBySql(String formCode, String formId,String table) {			
	//		return generalDao.getListOfMapsBySql("select * from FG_S_" + table + "_PIVOT");
	//	}

	@Override
	public String getFormCodeBySeqId(String formSeqId) {

		String formCode = null;
		formCode = generalDao.selectSingleString("select t.formcode from FG_SEQUENCE t where t.id = " + formSeqId);
		return formCode;
	}

	@Override
	public String getFormCodeBySeqIdNoException(String formSeqId) {

		String formCode = null;
		formCode = generalDao
				.selectSingleStringNoException("select t.formcode from FG_SEQUENCE t where t.id = " + formSeqId);
		return formCode;
	}

	@Override
	public String getFormParentId(String formCode, String formId) {
		Form form = getFormInfoLookup(formCode, "%", true).get(0);

		// TODO Auto-generated method stub
		return generalDao.selectSingleString("select distinct t.parentid from fg_s_"
				+ generalUtil.getNull(form.getFormCodeEntity(), formCode) + "_v t where t.formId = " + formId);
	}

	@Override
	public Map<String, String> getUserInfoMap(String userId) {
		Map<String, String> tmpUserInfo = getFromInfoLookupAll("user", LookupType.ID, userId);
		Map<String, String> userInfo = new HashMap<String, String>();
		userInfo.put("USER_INFO_USER_ID", tmpUserInfo.get("USER_ID"));
		userInfo.put("USER_INFO_USERNAME", tmpUserInfo.get("NAME"));
		userInfo.put("USER_INFO_FIRSTNAME", tmpUserInfo.get("FIRSTNAME"));
		userInfo.put("USER_INFO_LASTNAME", tmpUserInfo.get("LASTNAME"));
		userInfo.put("USER_INFO_LABORATORY_ID", tmpUserInfo.get("LABORATORY_ID"));
		userInfo.put("USER_INFO_LABORATORY_NAME", tmpUserInfo.get("LABORATORYNAME"));
		userInfo.put("USER_INFO_UNIT_ID", tmpUserInfo.get("UNIT_ID"));
		userInfo.put("USER_INFO_UNIT_NAME", tmpUserInfo.get("UNITSNAME"));
		userInfo.put("USER_INFO_SITE_ID", tmpUserInfo.get("SITE_ID"));
		userInfo.put("USER_INFO_SITE_NAME", tmpUserInfo.get("SITENAME"));
		userInfo.put("USER_INFO_TEAMLEADER_ID", tmpUserInfo.get("TEAMLEADER_ID")); 
		return userInfo;
	}

	@Override
	public String getFromInfoLookup(String formCode, LookupType lookupType, String lookupval, String elementName) {
		String elementVal;
		try {
			if (generalUtil.getNull(formCode).isEmpty()) {
				if (lookupType.equals(LookupType.ID) && !generalUtil.getEmpty(lookupval, "-1").equals("-1") && !generalUtil.getEmpty(lookupval, "ALL").equalsIgnoreCase("ALL")) {
					formCode = getFormCodeBySeqIdNoException(lookupval);
					if (generalUtil.getNull(formCode).isEmpty()) {
						generalUtilLogger.logWriter(
								LevelType.WARN, "WARN! formCode=" + formCode + ", lookupType=" + lookupType
										+ ", lookupval=" + lookupval + ", elementName=" + elementName,
								ActivitylogType.InfoLookUp, "");
						return "";
					}
				} else if (lookupType.equals(LookupType.NAME)) {
					return "";
				}
			}
			if (cachService.isCaching(formCode)) {
				try {
					elementVal = cachService.getInfDataStringFromCachMap(elementName, formCode, lookupType, lookupval);
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"WARN error! formCode=" + formCode + ", lookupType=" + lookupType + ", lookupval="
									+ lookupval + ", elementName=" + elementName,
							ActivitylogType.InfoLookUp, "", e, null);
					elementVal = generalDao.selectSingleStringNoException(" select " + elementName + " from FG_S_"
							+ formCode + "_INF_V  t where t." + lookupType + "= '" + lookupval + "'");
				}
			} else {
				elementVal = generalDao.selectSingleStringNoException(" select " + elementName + " from FG_S_"
						+ formCode + "_INF_V  t where t." + lookupType + "= '" + lookupval + "'");
			}

		} catch (Exception e) {
			generalUtilLogger.logWriter(
					LevelType.WARN, "WARN error(1)! formCode=" + formCode + ", lookupType=" + lookupType
							+ ", lookupval=" + lookupval + ", elementName=" + elementName,
					ActivitylogType.InfoLookUp, "", e, null);
			elementVal = "";
		}
		return elementVal == null ? "" : elementVal;
	}

	@Override
	public Map<String, String> getFromInfoLookupAll(String formCode, LookupType lookupType, String lookupval) {
		Map<String, String> allElements = new HashMap<String, String>();

		try {
			if (generalUtil.getNull(formCode).isEmpty()) {
				if (lookupType.equals(LookupType.ID) && !generalUtil.getEmpty(lookupval, "-1").equals("-1") && !generalUtil.getEmpty(lookupval, "ALL").equalsIgnoreCase("ALL")) {
					formCode = getFormCodeBySeqIdNoException(lookupval);
					if (generalUtil.getNull(formCode).isEmpty()) {
						generalUtilLogger.logWriter(LevelType.WARN, "WARN! formCode=" + formCode + ", lookupType="
								+ lookupType + ", lookupval=" + lookupval, ActivitylogType.InfoLookUp, "");
						return allElements = new HashMap<String, String>();
					}
				} else if (lookupType.equals(LookupType.NAME)) {
					return allElements = new HashMap<String, String>();
				}
			}
			//if form is caching get the data from cach instead of db 
			if (!generalUtil.getNull(formCode).isEmpty()) {
				if (cachService.isCaching(formCode)) {
					try {
						allElements = cachService.getInfDataFromCachMap(formCode, lookupType, lookupval);
					} catch (Exception e) {
						//		generalUtil.LogWriterDB(e);
						generalUtilLogger.logWriter(LevelType.WARN, "WARN error! formCode=" + formCode + ", lookupType="
								+ lookupType + ", lookupval=" + lookupval, ActivitylogType.InfoLookUp, "", e, null);
						List<Map<String, Object>> listOfMaps = generalDao.getListOfMapsBySql(" select * from fg_s_"
								+ formCode + "_inf_v  t where t." + lookupType + "= '" + lookupval + "'");
						if (!listOfMaps.isEmpty()) {
							for (Map.Entry<String, Object> entry : listOfMaps.get(0).entrySet()) {
								//								if (entry.getValue() instanceof String) {
								allElements.put(entry.getKey(),
										(entry.getValue() != null) ? entry.getValue().toString() : "");
								//								}
							}
						}
					}
				} else {
					List<Map<String, Object>> listOfMaps = generalDao.getListOfMapsBySql(" select * from fg_s_"
							+ formCode + "_inf_v  t where t." + lookupType + "= '" + lookupval + "'");
					if (!listOfMaps.isEmpty()) {
						for (Map.Entry<String, Object> entry : listOfMaps.get(0).entrySet()) {
							//							if (entry.getValue() instanceof String) {
							allElements.put(entry.getKey(),
									(entry.getValue() != null) ? entry.getValue().toString() : "");
							//							}
						}
					}
				}
			}

		} catch (Exception e) {
			allElements = new HashMap<String, String>();
			generalUtilLogger.logWriter(LevelType.WARN,
					"WARN error(1)! formCode=" + formCode + ", lookupType=" + lookupType + ", lookupval=" + lookupval,
					ActivitylogType.InfoLookUp, "", e, null);
		}
		return allElements;
	}

	public List<Map<String, String>> getFromInfoLookupAllContainsVal(String formCode, LookupType lookupType,
			String lookupval) {
		List<Map<String, String>> allElements = new ArrayList<Map<String, String>>();

		try {
			if (generalUtil.getNull(formCode).isEmpty()) {
				if (lookupType.equals(LookupType.ID) && !generalUtil.getEmpty(lookupval, "-1").equals("-1")) {
					formCode = getFormCodeBySeqIdNoException(lookupval);
					if (generalUtil.getNull(formCode).isEmpty()) {
						generalUtilLogger.logWriter(LevelType.WARN, "WARN! formCode=" + formCode + ", lookupType="
								+ lookupType + ", lookupval=" + lookupval, ActivitylogType.InfoLookUp, "");
						return allElements;
					}
				} else if (lookupType.equals(LookupType.NAME)) {
					return allElements;
				}
			}
			//if form is caching get the data from cach instead of db 
			if (!generalUtil.getNull(formCode).isEmpty()) {
				if (cachService.isCaching(formCode)) {
					try {

						allElements = cachService.getInfDataFromCachMapLikeVal(formCode, lookupType, lookupval);
					} catch (Exception e) {
						//		generalUtil.LogWriterDB(e);
						generalUtilLogger.logWriter(LevelType.WARN, "WARN error! formCode=" + formCode + ", lookupType="
								+ lookupType + ", lookupval=" + lookupval, ActivitylogType.InfoLookUp, "", e, null);
						List<Map<String, Object>> listOfMaps = generalDao.getListOfMapsBySql(" select * from fg_s_"
								+ formCode + "_inf_v  t where t." + lookupType + " like '" + lookupval + "'");
						if (!listOfMaps.isEmpty()) {
							for (int i = 0; i < listOfMaps.size(); i++) {
								Map<String, String> mapData = new HashMap<String, String>();
								for (Map.Entry<String, Object> entry : listOfMaps.get(i).entrySet()) {
									//									if (entry.getValue() instanceof String) {
									mapData.put(entry.getKey(),
											(entry.getValue() != null) ? entry.getValue().toString() : "");
									//									}
								}
								allElements.add(mapData);
							}
						}
					}
				} else {
					List<Map<String, Object>> listOfMaps = generalDao.getListOfMapsBySql(" select * from fg_s_"
							+ formCode + "_inf_v  t where t." + lookupType + " like '" + lookupval + "'");
					if (!listOfMaps.isEmpty()) {
						for (int i = 0; i < listOfMaps.size(); i++) {
							Map<String, String> mapData = new HashMap<String, String>();
							for (Map.Entry<String, Object> entry : listOfMaps.get(i).entrySet()) {
								//								if (entry.getValue() instanceof String) {
								mapData.put(entry.getKey(),
										(entry.getValue() != null) ? entry.getValue().toString() : "");
								//								}
							}
							allElements.add(mapData);
						}
					}
				}
			}

		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN,
					"WARN error(1)! formCode=" + formCode + ", lookupType=" + lookupType + ", lookupval=" + lookupval,
					ActivitylogType.InfoLookUp, "", e, null);
			allElements = new ArrayList<Map<String, String>>();
		}
		return allElements;
	}

	@Override
	public List<String> getFromInfoLookupElementData(String formCode, LookupType lookupType, String lookupval,
			String elementName) {
		List<String> returnList = new ArrayList<String>();
		try {
			if (generalUtil.getNull(formCode).isEmpty()) {
				if (lookupType.equals(LookupType.ID) && !generalUtil.getEmpty(lookupval, "-1").equals("-1") && !generalUtil.getEmpty(lookupval, "ALL").equalsIgnoreCase("ALL")) {
					formCode = getFormCodeBySeqIdNoException(lookupval);
					if (generalUtil.getNull(formCode).isEmpty()) {
						generalUtilLogger.logWriter(
								LevelType.WARN, "WARN! formCode=" + formCode + ", lookupType=" + lookupType
										+ ", lookupval=" + lookupval + ", elementName=" + elementName,
								ActivitylogType.InfoLookUp, "");
						return returnList;
					}
				} else if (lookupType.equals(LookupType.NAME)) {
					return returnList;
				}
			}
			if (cachService.isCaching(formCode)) {
				try {
					returnList = cachService.getInfDataListFromCachMap(elementName, formCode, lookupType, lookupval);
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN,
							"WARN error! formCode=" + formCode + ", lookupType=" + lookupType + ", lookupval="
									+ lookupval + ", elementName=" + elementName,
							ActivitylogType.InfoLookUp, "", e, null);
					returnList = generalDao.getListOfStringBySql(" select " + elementName + " from FG_S_" + formCode
							+ "_INF_V  t where t." + lookupType + "= '" + lookupval + "'");
				}
			} else {
				returnList = generalDao.getListOfStringBySql(" select " + elementName + " from FG_S_" + formCode
						+ "_INF_V  t where t." + lookupType + "= '" + lookupval + "'");
			}

		} catch (Exception e) {
			generalUtilLogger.logWriter(
					LevelType.WARN, "WARN error(1)! formCode=" + formCode + ", lookupType=" + lookupType
							+ ", lookupval=" + lookupval + ", elementName=" + elementName,
					ActivitylogType.InfoLookUp, "", e, null);
			returnList = new ArrayList<String>();
		}
		return returnList == null ? new ArrayList<String>() : returnList;
	}

	@Override
	public Map<String, String> getFromInfoLookupAllElementData(String formCode, LookupType lookupType,
			String elementName) {
		Map<String, String> allElementData = new HashMap<String, String>();

		List<String> lookupTypeList = new ArrayList<String>();
		List<String> elementList = new ArrayList<String>();
		try {
			//if form is caching get the data from cach instead of db 
			if (cachService.isCaching(formCode)) {
				try {
					lookupTypeList = cachService.getColumnsList(formCode, lookupType.getTypeName());
					elementList = cachService.getColumnsList(formCode, elementName);
				} catch (Exception e) {
					generalUtilLogger.logWriter(LevelType.WARN, "WARN error! formCode=" + formCode + ", lookupType="
							+ lookupType + ", elementName=" + elementName, ActivitylogType.InfoLookUp, "", e, null);

					lookupTypeList = (generalDao
							.getListOfStringBySql("select " + lookupType + " from FG_S_" + formCode + "_INF_V "));
					elementList = generalDao
							.getListOfStringBySql("select " + elementName + " from FG_S_" + formCode + "_INF_V ");
				}

			} else {
				lookupTypeList = (generalDao
						.getListOfStringBySql("select " + lookupType + " from FG_S_" + formCode + "_INF_V "));
				elementList = generalDao
						.getListOfStringBySql("select " + elementName + " from FG_S_" + formCode + "_INF_V ");
			}
			for (int i = 0; i < lookupTypeList.size(); i++) {
				allElementData.put(lookupTypeList.get(i), elementList.get(i));
			}
		} catch (Exception e1) {
			generalUtilLogger.logWriter(LevelType.WARN, "WARN error(1)! formCode=" + formCode + ", lookupType="
					+ lookupType + ", elementName=" + elementName, ActivitylogType.InfoLookUp, "", e1, null);
			allElementData = new HashMap<String, String>();
		}
		return allElementData;
	}

	@Override
	public String getWherePartForTmpData(String sessionId, String formCode, String parentId) {
		StringBuilder where = new StringBuilder();
		Form form = getFormInfoLookup(formCode, "%", true).get(0);

		//		String sessionId = generalUtilFormState.getSessionId(parentId);
		//note1: decode(ts.active,0,1,1,0,1) - we switch between active and not active in order to use it in the same max expression (avoid 2 partition in select) - it will get 1 if not not 1 or 0 as not active
		if (!generalUtil.getNull(sessionId).equals("")) {
			//			where.append(" and (form_temp_id || '-0') in (select max(ts.form_temp_id || '-' || decode(ts.active,0,1,1,0,1)) over (partition by ts.formid) from FG_S_" + generalUtil.getNull(form.getFormCodeEntity(),formCode)  + "_V ts where 1=decode (ts.form_temp_id,ts.FORMID,1,ts.FORMID || '-' || nvl(ts.sessionid,'" + sessionId + "'),1,0) ) \n ");
			//(select max(ts.formid || '-' || nvl(ts.sessionid,'1511935360168') || '-' || decode(ts.active,0,1,1,0,1)) over (partition by ts.formid,nvl(ts.sessionid,'1511935360168')) from FG_S_Document_V ts where ts.formid > 78308 /*and 1=decode (ts.form_temp_id,ts.FORMID,1,ts.FORMID || '-' || nvl(ts.sessionid,'1511935360168'),1,0)*/ ) 
			where.append(" and (form_temp_id) in ("
					+ getTmpId(generalUtil.getNull(form.getFormCodeEntity(), formCode), parentId, sessionId)
					+ ")  \n ");

		}
		return where.toString();
		//END tmp data handling!
	}
	
	@Override
	public String getWherePartForTmpDataByFormId(String sessionId, String formCode, String parentId) {
		StringBuilder where = new StringBuilder();
		Form form = getFormInfoLookup(formCode, "%", true).get(0);

		//		String sessionId = generalUtilFormState.getSessionId(parentId);
		//note1: decode(ts.active,0,1,1,0,1) - we switch between active and not active in order to use it in the same max expression (avoid 2 partition in select) - it will get 1 if not not 1 or 0 as not active
		if (!generalUtil.getNull(sessionId).equals("")) {
			//			where.append(" and (form_temp_id || '-0') in (select max(ts.form_temp_id || '-' || decode(ts.active,0,1,1,0,1)) over (partition by ts.formid) from FG_S_" + generalUtil.getNull(form.getFormCodeEntity(),formCode)  + "_V ts where 1=decode (ts.form_temp_id,ts.FORMID,1,ts.FORMID || '-' || nvl(ts.sessionid,'" + sessionId + "'),1,0) ) \n ");
			//(select max(ts.formid || '-' || nvl(ts.sessionid,'1511935360168') || '-' || decode(ts.active,0,1,1,0,1)) over (partition by ts.formid,nvl(ts.sessionid,'1511935360168')) from FG_S_Document_V ts where ts.formid > 78308 /*and 1=decode (ts.form_temp_id,ts.FORMID,1,ts.FORMID || '-' || nvl(ts.sessionid,'1511935360168'),1,0)*/ ) 
			where.append(" and (formid) not in ("
					+ getTmpId(generalUtil.getNull(form.getFormCodeEntity(), formCode), parentId, sessionId,"formid")
					+ ")  \n ");

		}
		return where.toString();
		//END tmp data handling!
	}
	//	

	//	(
	//	       select t.form_temp_id, decode(count(rowid) over (partition by t.formid, nvl(t.sessionid,'1512025928445')),2,decode(t.sessionid,null,'show','hide'),'show') as showFlag 
	//	       from FG_S_DOCUMENT_V t
	//	) where showFlag = 'show'

	private String getTmpId(String formCodeEntity, String parentId, String sessionId) {
		// TODO Auto-generated method stub
		return "select form_temp_id from \r\n" + "(\r\n" + "       select t.form_temp_id, \r\n" + //  form_temp_id is: <formid> is DB record / <formid>-<sessionid> tmp record with sessionid (should be call transaction id because its scope is per save) / deleted session data <formid>-<sessionid>-<0> / deleted data <formid>-<0>
				"              decode(\r\n"
				+ "                      count(rowid) over (partition by t.formid, nvl(t.sessionid,'" + sessionId
				+ "')), \r\n" + // for session + formid we could have 0-2 records with different form_temp_id (we deleted record that are being edit in the same transaction). Here we bring all records that can shown under this session, which are the TMP and the DB RECORDS (NOTE: using nvl(t.sessionid,'" + sessionId + "') we consider both options after remove other sessions in the wrapper query)
				"                      2,\r\n" + // if "count(rowid)...." = 2 it means that we should -> ..
				"                      decode(nvl(t.sessionid,'-1'),\r\n" + // .. check if DB record -> ...
				"                             '-1',\r\n" + "                             'hide',\r\n" + // ... if DB record hide
				"                              decode(nvl(t.active,1),\r\n" + // ... if DB record hide ( !!! note I had some problem in removing this part and let the wrapper query handle the non active so its here)
				"                                     0,\r\n" + "                                    'hide',\r\n"
				+ "                                    'show')\r\n" + "                             ),\r\n" +
				//				"                            'show'),\r\n" + 
				"                      'show'\r\n" + // else ["count(rowid)...." <> 2] it can be show (if not active the wrapper query will remove it)
				"                    ) as showFlag, \r\n" + "             t.active \r\n" + "       from FG_S_"
				+ formCodeEntity.toUpperCase() + "_V t where parentId='" + parentId + "' and nvl(t.sessionid,'"
				+ sessionId + "') = '" + sessionId + "' \r\n" + ") where showFlag = 'show' and active = 1 ";
	}

	private String getTmpId(String formCodeEntity, String parentId, String sessionId, String select) {
		// TODO Auto-generated method stub
		return "select "+ select+" from \r\n" + "(\r\n" + "       select t."+ select+", \r\n" + //  form_temp_id is: <formid> is DB record / <formid>-<sessionid> tmp record with sessionid (should be call transaction id because its scope is per save) / deleted session data <formid>-<sessionid>-<0> / deleted data <formid>-<0>
				"              decode(\r\n"
				+ "                      count(rowid) over (partition by t.formid, nvl(t.sessionid,'" + sessionId
				+ "')), \r\n" + // for session + formid we could have 0-2 records with different form_temp_id (we deleted record that are being edit in the same transaction). Here we bring all records that can shown under this session, which are the TMP and the DB RECORDS (NOTE: using nvl(t.sessionid,'" + sessionId + "') we consider both options after remove other sessions in the wrapper query)
				"                      2,\r\n" + // if "count(rowid)...." = 2 it means that we should -> ..
				"                      decode(nvl(t.sessionid,'-1'),\r\n" + // .. check if DB record -> ...
				"                             '-1',\r\n" + "                             'hide',\r\n" + // ... if DB record hide
				"                              decode(nvl(t.active,1),\r\n" + // ... if DB record hide ( !!! note I had some problem in removing this part and let the wrapper query handle the non active so its here)
				"                                     0,\r\n" + "                                    'hide',\r\n"
				+ "                                    'show')\r\n" + "                             ),\r\n" +
				//				"                            'show'),\r\n" + 
				"                      'show'\r\n" + // else ["count(rowid)...." <> 2] it can be show (if not active the wrapper query will remove it)
				"                    ) as showFlag, \r\n" + "             t.active \r\n" + "       from FG_S_"
				+ formCodeEntity.toUpperCase() + "_V t where parentId='" + parentId + "' and nvl(t.sessionid,'"
				+ sessionId + "') = '" + sessionId + "' \r\n" + ") where showFlag = 'show' and active = 1 ";
	}
	
	@Override
	public String getFormCodeEntityBySeqId(String formCode, String SeqId) {
		//		String formCode = generalDao.selectSingleString("select t.formcode from FG_SEQUENCE t where t.id = " + SeqId);
		if(formCode.isEmpty()) {
			formCode = getFormCodeBySeqId(SeqId);
		}
		List<Form> formList = getFormInfoLookup(formCode, "%", true);
		if(formList.size()!=0){
			Form form = formList.get(0);
			return form.getFormCodeEntity();
		} else {
			return "";
		}
	}

	@Override
	public Map<String, String> getFormElementCaseSensitiveName(String formCode) {
		Map<String, String> nameMap = new HashMap<String, String>();
		List<FormEntity> formEntityList = getFormEntityInfoLookup(formCode, "%");
		for (FormEntity formEntity : formEntityList) {
			nameMap.put(formEntity.getEntityImpCode().toUpperCase(), formEntity.getEntityImpCode());
		}
		return nameMap;
	}

	@Override
	public String removeFromDB(String formId, String formCodeEntity) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("formId_in", formId);
		parameters.put("formCodeEntity_in", formCodeEntity);
		return generalDao.callPackageFunction("FORM_TOOL", "removeFromIdFromDB", parameters);
	}

	@Override
	public Map<String, String> getFormElementValuesMap(String formId, String formCode) {
		String tableName = "FG_S_" + formCode + "_PIVOT";
		Map<String, String> reactantMap = generalDao
				.sqlToHashMap("select * from " + tableName + " where formid = '" + formId + "'");
		Map<String, String> reactantElementValueMap = getMapCaseSensitiveKeys(formCode, reactantMap);//get the reactant map with case sensitive keys names
		return reactantElementValueMap;
	}

	private Map<String, String> getMapCaseSensitiveKeys(String formCode, Map<String, String> reactantMap) {
		Map<String, String> formElementNames = getFormElementCaseSensitiveName(formCode);
		Map<String, String> formElementValueMap = new HashMap<>();
		for (Map.Entry<String, String> entry : reactantMap.entrySet()) {
			if (formElementNames.containsKey(entry.getKey())) {
				formElementValueMap.put(formElementNames.get(entry.getKey()), entry.getValue());
			} else {
				formElementValueMap.put(entry.getKey(), entry.getValue());
			}
		}
		return formElementValueMap;
	}

	@Override
	public List<FormAdditionalData> getFormLastSaveAdditionalDataList(String sql_) {
		//		jdbcTemplate.query(formId,
		//				"select * from FG_FORMADDITIONALDATA where PARENTID='" + formId + "'",
		//				new FormAdditionalDataMapper());
		logger.info("/* SQL getFormLastSaveAdditionalDataList sql=*/ " + sql_);
		return jdbcTemplate.query(sql_, new FormAdditionalDataMapper());
	}

	@Override
	public List<FormLastSaveValue> getFormLastSaveValueList(String sql_) {

		logger.info("/* SQL getFormLastSaveValueList sql=*/ " + sql_);
		return jdbcTemplate.query(sql_, new FormLastSaveValueMapper());
	}

	/**
	 * Inserts data to selection table
	 * 
	 * @param selectFormCode
	 *            the formcode of the selection
	 * @param selectParentId
	 *            formid of the parent entity of the selection
	 * @param selectColumn
	 *            column name of the selected items
	 * @param itemsToSelect
	 *            a list of items to insert to the selection
	 * @param isListDisabled
	 *            indicates whether the selected items should be constant
	 * @param userId
	 *            userId
	 */
	@Override
	public void insertToSelectTable(String selectFormCode, String selectParentId, String selectColumn,
			List<String> itemsToSelect, boolean isListDisabled, String userId, String sessionId) {
		if (itemsToSelect.isEmpty()) {
			return;
		}
		String table = "FG_S_" + selectFormCode + "_PIVOT";
		List<String> selectionId = generalDao.getListOfStringBySql(
				"select FORMID from FG_S_" + selectFormCode + "_V" + " tt where tt.PARENTID = '" + selectParentId + "' "
						+ getWherePartForTmpData(sessionId, selectFormCode, selectParentId));

		if (selectionId.isEmpty()) {//selection table for this entity does not exist yet
			String selectId = formSaveDao.getStructFormId(selectFormCode);
			String sql = String.format(
					"INSERT INTO %1$s t (t.FORMID,t.TIMESTAMP,t.CHANGE_BY,t.CREATION_DATE,t.CREATED_BY,t.SESSIONID,t.ACTIVE,t.FORMCODE,t.FORMCODE_ENTITY,t.PARENTID,%3$s,DISABLED)"
							+ " VALUES ('%5$s' , sysdate, '%6$s', sysdate, '%6$s',null,1,'%7$s','%7$s','%2$s','%4$s','"
							+ (isListDisabled ? itemsToSelect.toString().replaceAll("\\[|\\]|\\s*", "") : "") + "')",
					table, selectParentId, selectColumn, itemsToSelect.toString().replaceAll("\\[|\\]|\\s*", ""),
					selectId, userId, selectFormCode);
			formSaveDao.insertStructTableByFormId(sql, "FG_S_" + selectFormCode + "_PIVOT", selectId);
		} else {//concatenates the current itemToSelect to the list of selected items in the referenced selection table of the entity
			Map<String, String> selectionData = generalDao.getMetaDataRowValues("select " + selectColumn
					+ ",DISABLED from " + table + " where formid = '" + selectionId.get(0) + "' and sessionid = '"+sessionId+"'");
			List<String> similar = new ArrayList<String>();
			if(!generalUtil.getNull(selectionData.get(selectColumn)).equals("")) { //yp 25032020 make this check to avoid insert empty string to similar (that cause leading comma in the csv)
				similar.addAll(Arrays.asList(selectionData.get(selectColumn).split(",")));
			}
			List<String> different = new ArrayList<String>();
			different.addAll(similar);
			similar.retainAll(itemsToSelect);
			different.removeAll(similar);
			different.addAll(itemsToSelect);
			String itemsToSelectCsv = generalUtil.listToCsv(different);

			similar.clear();
			if(!generalUtil.getNull(selectionData.get("DISABLED")).equals("")) { //yp 25032020 make this check to avoid insert empty string to similar (that cause leading comma in the csv)
				similar.addAll(Arrays.asList(selectionData.get("DISABLED").split(",")));
			}
			different = new ArrayList<String>();
			different.addAll(similar);
			if (isListDisabled) {
				similar.retainAll(itemsToSelect);
				different.removeAll(similar);
				different.addAll(itemsToSelect);
			}
			String disabledItemsToSelectCsv = generalUtil.listToCsv(different);
			/*
			 * String sql = String.format(
			 * "UPDATE %1$s t set %3$s = nvl2(%3$s ,DECODE(instr(','||%3$s||',',','||'%4$s'||','),0, %3$s||',"
			 * + "%4$s',%3$s),'%4$s'),DISABLED = nvl2(DISABLED,DECODE(instr(','||DISABLED||',',','||'%4$s'||','),0,DISABLED || ',"
			 * + (isListDisabled ? itemsToSelect.toString().replaceAll("\\[|\\]|\\s*", "") : "")
			 * + "',DISABLED),'"
			 * + (isListDisabled ? itemsToSelect.toString().replaceAll("\\[|\\]|\\s*", "") : "")
			 * + "') where t.FORMID = %5$s",
			 * table, selectParentId, selectColumn, itemsToSelect.toString().replaceAll("\\[|\\]|\\s*", ""),
			 * selectionId.get(0), userId, selectFormCode);
			 */
			String sql = String.format(
					"UPDATE %1$s t set %3$s = '" + itemsToSelectCsv + "',DISABLED = '" + disabledItemsToSelectCsv
							+ "' where t.FORMID = %5$s and sessionid = '"+sessionId+"'",
					table, selectParentId, selectColumn, itemsToSelect.toString().replaceAll("\\[|\\]|\\s*", ""),
					selectionId.get(0), userId, selectFormCode);
			formSaveDao.updateStructTableByFormId(sql, table, Arrays.asList(selectColumn, "DISABLED"),
					selectionId.get(0));
		}

	}

	@Override
	public Map<String, String> getLastFormDataMap(String formCode, String formId, FormType formType) {
		Map<String,String> lastFormSaveInfoMap = new HashMap<>();
		try {
			String sql = "select CHANGE_BY,TO_CHAR(TIMESTAMP,'"+generalUtil.getConversionDateTimeSecondsFormat()+"') TIMESTAMP from fg_s_"+formCode+"_pivot where formid = '"+formId+"'";
			Map<String,String> data = generalDao.sqlToHashMap(sql);
			lastFormSaveInfoMap.put("lastChangeUserId", data.get("CHANGE_BY"));
			lastFormSaveInfoMap.put("lastChangeDate", data.get("TIMESTAMP"));
		} catch (Exception e) {
			lastFormSaveInfoMap = null;
			e.printStackTrace();
		}
		return lastFormSaveInfoMap;
	}
}