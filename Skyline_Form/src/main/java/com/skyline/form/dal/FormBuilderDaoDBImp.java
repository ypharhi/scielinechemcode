package com.skyline.form.dal;
// FormDaoDBImp

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.skyline.form.bean.BeanType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.ElementInfoAuditTrailMeta;
import com.skyline.form.bean.Form;
import com.skyline.form.bean.FormEntity;
import com.skyline.form.bean.InfData;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilLogger;

@Repository("FormBuilderDao")
public class FormBuilderDaoDBImp extends BasicDao implements FormBuilderDao {

	//	private JdbcTemplate jdbcTemplate;

	@Autowired
	public GeneralUtil generalUtil;

	@Autowired
	public GeneralUtilLogger generalUtilLogger;

	@Autowired
	private GeneralDao generalDao;

	@Value("${HtmlPath}")
	private String Htmlpath;

	@Value("${jspPath}")
	private String jspPath;

	//	@Autowired
	//	public void setDataSource(DataSource dataSource) {
	//		this.jdbcTemplate = new JdbcTemplate(dataSource);
	//	}

	private static final Logger logger = LoggerFactory.getLogger(FormBuilderDaoDBImp.class);

	public class DataBeanMapper implements RowMapper<DataBean> {
		public DataBean mapRow(ResultSet rs, int rowNum) throws SQLException {
			DataBean DataBean = new DataBean(Long.parseLong(rs.getString("ID")), BeanType.valueOf(rs.getString("type")),
					rs.getString("Code"), rs.getString("value"), rs.getString("info"));
			return DataBean;
		}
	}

	//	public class FormLastSaveValueMapper implements RowMapper<FormLastSaveValue> {
	//		public FormLastSaveValue mapRow(ResultSet rs, int rowNum) throws SQLException {
	//			FormLastSaveValue formLastSaveValue = new FormLastSaveValue(Long.parseLong(rs.getString("ID")),
	//					rs.getString("FORMID"), rs.getString("FORMCODE"), rs.getString("ENTITYIMPCODE"),
	//					rs.getString("ENTITYIMPVALUE"), rs.getString("USERID"));
	//			return formLastSaveValue;
	//		}
	//	}

	@Override
	public List<FormEntity> getFormEntity(String formCode, String type) {
		List<FormEntity> formEntityList;
		try {
			String sql = "select * from FG_FORMENTITY where ENTITYTYPE LIKE '" + type
			+ "' and FORMCODE LIKE '" + formCode + "'";
			logger.info("/* SQL getFormEntity sql=*/ " + sql);
			formEntityList = jdbcTemplate.query(sql, new FormEntityMapper());
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			formEntityList = null;
			e.printStackTrace();
		}
		return formEntityList;
	}

	public class FormEntityMapper implements RowMapper<FormEntity> {
		public FormEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
			FormEntity formEntity = new FormEntity(Long.parseLong(rs.getString("ID")), rs.getString("FORMCODE"),
					Integer.parseInt(rs.getString("NUMBEROFORDER")), rs.getString("ENTITYTYPE"),
					rs.getString("ENTITYIMPCODE"), rs.getString("ENTITYIMPCLASS"), rs.getString("ENTITYIMPINIT"));
			return formEntity;
		}
	}

	@Override
	public List<FormEntity> getFormEntityByView(String formCode, String type) {
		List<FormEntity> formEntityList;
		try {
			formEntityList = jdbcTemplate.query("select * from FG_FORMENTITY_V where ENTITYTYPE LIKE '" + type
					+ "' and FORMCODE LIKE '" + formCode + "'", new FormEntityMapper());
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			formEntityList = null;
			e.printStackTrace();
		}
		return formEntityList;
	}

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

	@Override
	public String newFormEntity(FormEntity formEntity) {
		String sql = "merge into FG_FORMENTITY p using (" + "select '" + formEntity.getFormCode() + "' FORMCODE,'"
				+ formEntity.getEntityImpCode() + "' ENTITYIMPCODE,'" + formEntity.getEntityType() + "' ENTITYTYPE,'"
				+ String.valueOf(formEntity.getOrder()) + "' NUMBEROFORDER,'" + formEntity.getEntityImpClass()
				+ "' ENTITYIMPCLASS,'" + formEntity.getEntityImpInit().replaceAll("'", "''") + "' ENTITYIMPINIT "
				+ "from dual ) t1"
				+ " on( p.FORMCODE=t1.FORMCODE and p.ENTITYIMPCODE = t1.ENTITYIMPCODE and p.ENTITYTYPE = t1.ENTITYTYPE ) "
				+ "when not matched then insert (FORMCODE,NUMBEROFORDER,ENTITYTYPE,ENTITYIMPCODE,ENTITYIMPCLASS,ENTITYIMPINIT) values (t1.FORMCODE,t1.NUMBEROFORDER,t1.ENTITYTYPE,t1.ENTITYIMPCODE,t1.ENTITYIMPCLASS,t1.ENTITYIMPINIT) "
				+ "when matched then update set p.ENTITYIMPINIT = t1.ENTITYIMPINIT";

		String update = generalDao.updateSingleString(sql);
		if (!update.equals("-1")) {//formStateManager will not refresh if the sql failed
			//TODO key - this is not needed;
			//			//yp init formStateManager on DB event TODO? aspect / parent implementation or other solution
			//			generalUtilFormState.setFormBeanAndInit(formEntity.getFormCode(), formEntity.getEntityImpClass(),
			//					formEntity.getEntityImpCode(), formEntity.getEntityImpInit());
		}
		return update;
	}

	@Override
	public String deleteFormEntity(String formCode, String entityImpCode) {

		String sql = "DELETE FROM FG_FORMENTITY WHERE FORMCODE='" + formCode + "' and ENTITYIMPCODE='" + entityImpCode
				+ "'";
		String update = generalDao.updateSingleString(sql);
		if (!update.equals("-1")) {//formStateManager will not refresh if the sql failed
			//TODO key - this is not needed;
			//			//yp init formStateManager on DB event TODO? aspect / parent implementation or other solution
			//			generalUtilFormState.removeFormBean(formCode, entityImpCode);
		}
		return update;
	}

	@Override
	public String newForm(String formCode, String description, String active, String form_type, String title,
			String subtitle, String useAsTemplate, String order, String groupName, String formCodeEntity,
			String ignoreNav, String useCache) {
		String sql = "", formCodeRef;
		boolean doMerge = true;
		if (useAsTemplate.equals("1")) {
			sql = "UPDATE FG_FORM SET USE_AS_TEMPLATE=0 where FORM_TYPE = '" + form_type + "'";
			generalDao.updateSingleString(sql);
		}

		if (form_type.equals("REF") && useAsTemplate.equals("0")) {
			if (generalDao.selectSingleString("select count(*) from FG_FORM t where formcode = '" + formCode + "'")
					.equals("0")) {
				formCodeRef = generalDao.selectSingleString(
						"select formcode from FG_FORM t where USE_AS_TEMPLATE = '1' and FORM_TYPE = 'REF'");
				if (!formCodeRef.equals("")) {
					createLike(formCode, formCodeRef);
					sql = "update fg_formentity t set t.ENTITYIMPCODE = '" + Character.toLowerCase(formCode.charAt(0))
							+ formCode.substring(1) + "Name' where t.FORMCODE = '" + formCode
							+ "' and t.ENTITYIMPCODE = 'Name'";
					doMerge = false;
				}
			}
		}
		if (doMerge) {
			sql = "merge into FG_FORM p using (" + "select '" + formCode + "' FORMCODE,'" + description
					+ "' DESCRIPTION,'" + active + "' ACTIVE,'" + form_type + "' FORM_TYPE,'" + title + "' TITLE,'"
					+ subtitle + "' SUBTITLE,'" + useAsTemplate + "' USE_AS_TEMPLATE,'" + order + "' NUMBEROFORDER,'"
					+ groupName + "' GROUP_NAME, '" + formCodeEntity + "' FORMCODE_ENTITY, '" + ignoreNav
					+ "' IGNORE_NAV, '" + useCache + "' USECACHE from dual ) t1" + " on( p.FORMCODE=t1.FORMCODE ) "
					+ "when not matched then insert (FORMCODE,DESCRIPTION,ACTIVE,FORM_TYPE,TITLE,SUBTITLE,USE_AS_TEMPLATE,NUMBEROFORDER,GROUP_NAME, FORMCODE_ENTITY, IGNORE_NAV, USECACHE) values (t1.FORMCODE,t1.DESCRIPTION,t1.ACTIVE,t1.FORM_TYPE,t1.TITLE,t1.SUBTITLE,t1.USE_AS_TEMPLATE,t1.NUMBEROFORDER,t1.GROUP_NAME, t1.FORMCODE_ENTITY, t1.IGNORE_NAV, t1.USECACHE) "
					+ "when matched then update set p.DESCRIPTION = t1.DESCRIPTION,p.ACTIVE = t1.ACTIVE,p.FORM_TYPE = t1.FORM_TYPE,p.TITLE = t1.TITLE,p.SUBTITLE = t1.SUBTITLE,p.USE_AS_TEMPLATE = t1.USE_AS_TEMPLATE,p.NUMBEROFORDER = t1.NUMBEROFORDER,p.GROUP_NAME = t1.GROUP_NAME,p.FORMCODE_ENTITY = t1.FORMCODE_ENTITY,p.IGNORE_NAV = t1.IGNORE_NAV, p.USECACHE = t1.USECACHE ";
		}
		return generalDao.updateSingleString(sql);
	}

	public class FormMapper implements RowMapper<Form> {
		public Form mapRow(ResultSet rs, int rowNum) throws SQLException {
			Form form = new Form(rs.getString("FORMCODE"), rs.getString("DESCRIPTION"), rs.getString("ACTIVE"),
					rs.getString("FORM_TYPE"), rs.getString("TITLE"), rs.getString("SUBTITLE"),
					rs.getString("USE_AS_TEMPLATE"), rs.getString("GROUP_NAME"), rs.getInt("NUMBEROFORDER"),
					rs.getString("FORMCODE_ENTITY"), rs.getString("IGNORE_NAV"), rs.getString("USECACHE"));
			return form;
		}
	}

	@Override
	public String createLike(String newFormCode, String oldFormCode) {
		String sql;
		sql = "INSERT INTO FG_FORM (FORMCODE,DESCRIPTION,ACTIVE,FORM_TYPE,TITLE,SUBTITLE,USE_AS_TEMPLATE,NUMBEROFORDER,GROUP_NAME,FORMCODE_ENTITY,IGNORE_NAV,USECACHE)\n"
				+ "		SELECT '" + newFormCode
				+ "',DESCRIPTION,ACTIVE,FORM_TYPE,TITLE,SUBTITLE,0,NUMBEROFORDER,GROUP_NAME,'" + newFormCode
				+ "',IGNORE_NAV,USECACHE\n" + "		FROM FG_FORM where FORMCODE = '" + oldFormCode + "'";
		if (generalDao.updateSingleString(sql).equals("-1")) {
			return "-1";
		}
		sql = "INSERT INTO FG_FORMENTITY (FORMCODE,NUMBEROFORDER,ENTITYTYPE,ENTITYIMPCODE,ENTITYIMPCLASS,ENTITYIMPINIT)\n"
				+ "SELECT '" + newFormCode + "',NUMBEROFORDER,ENTITYTYPE,ENTITYIMPCODE,ENTITYIMPCLASS,ENTITYIMPINIT\n"
				+ "FROM FG_FORMENTITY where FORMCODE = '" + oldFormCode + "'";
		return generalDao.updateSingleString(sql);
	}

	@Override
	public String updateCatalogForNewForm(String formCode, String formType) {
		String sql;
		sql = "INSERT INTO FG_FORMENTITY (FORMCODE,NUMBEROFORDER,ENTITYTYPE,ENTITYIMPCODE,ENTITYIMPCLASS,ENTITYIMPINIT)\n"
				+ "        SELECT '" + formCode
				+ "',t1.NUMBEROFORDER,t1.ENTITYTYPE,t1.ENTITYIMPCODE,t1.ENTITYIMPCLASS,t1.ENTITYIMPINIT\n"
				+ "        FROM FG_FORMENTITY t1,FG_FORM t2 where t1.ENTITYTYPE = 'Catalog' and t1.FORMCODE = t2.FORMCODE and USE_AS_TEMPLATE = 1 and t2.FORM_TYPE = '"
				+ formType + "'";
		return generalDao.updateSingleString(sql);
	}

	@Override
	public List<Form> getForm(String formCode, String formType, boolean includeInactiveForms) {
		List<Form> formList;
		String sql = "select * from FG_FORM where UPPER(FORMCODE) like UPPER('" + formCode + "') and FORM_TYPE like '"
				+ formType + "'" + ((includeInactiveForms) ? "" : " and active = 1 ")
				+ " order by FORM_TYPE,NVL(NUMBEROFORDER,0), FORMCODE ";
		try {
			logger.info("/* SQL getForm sql=*/ " + sql);
			formList = jdbcTemplate.query(sql, new FormMapper());
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			formList = null;
			e.printStackTrace();
		}
		return formList;
	}

	@Override
	public String canDeleteFormEntity(String formCode, String formEntity) {
		return generalDao.getCSVBySql("select ENTITYIMPINIT from FG_FORMENTITY t where FORMCODE = '" + formCode
				+ "' and ENTITYIMPCODE <> '" + formEntity + "' and ENTITYTYPE='Element' ", false);
	}

	@Override
	public String getFormEntityInit(String formCode, String entityCode, String type) {
		return generalDao.selectSingleString("select json_ENTITYIMPINIT from fg_formentity_v where ENTITYTYPE LIKE '"
				+ type + "' and FORMCODE='" + formCode + "' and ENTITYIMPCODE='" + entityCode + "'");
	}

	@Override
	public List<InfData> getFromInfDataList(String fromCode) {
		// TODO Auto-generated method stub
		return generalDao.sqlToInfDataObjList("select t.* from FG_S_" + fromCode + "_INF_V t");
	}

	@Override
	public void createBookmarks(String formCode, String bookmarkPrefix, String noEntityimpcodeList_in) {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("formCode_in", formCode);
		parameters.put("bookmarkPrefix_in", bookmarkPrefix);
		parameters.put("noEntityimpcodeList_in", noEntityimpcodeList_in);

		generalDao.callPackageFunction("FORM_TOOL", "addFormLabel", parameters);

	}

	@Override
	public void updateElementInfoAuditTrailMetaTmpTable(Map<String, ElementInfoAuditTrailMeta> formElementDisplayMap) {
		generalDao.updateSingleStringNoTryCatch("delete from FG_FORMELEMENTINFOATMETA_TMP");

		//		Map<String, ElementInfoAuditTrailMeta> formElementDisplayMap = cacheService
		//				.getFormElementInfoAuditTrailMetaMap();

		StringBuilder sbInsert = new StringBuilder(
				"insert into FG_FORMELEMENTINFOATMETA_TMP(FORMCODE,ENTITYIMPCODE,ELEMENTCLASS,DISPLAYLABEL,ISPARENTPATHID,ADDITIONALDATA,ISHIDDEN,ISSEARCHIDHOLDER,FORMCODEENTITYLABEL,FORMCODETYPLABEL,ISLISTID,ISSEARCHELEMENT,DATATYPE,DATATYPE_INFO)\n");

		for (Map.Entry<String, ElementInfoAuditTrailMeta> entry : formElementDisplayMap.entrySet()) {
			ElementInfoAuditTrailMeta eInf_ = entry.getValue();
			String formCode_ = eInf_.getFormCode();
			String impCode_ = eInf_.getEntityImpCode();
			String class_ = eInf_.getElementClass();
			String label_ = eInf_.getLabel();
			String isParent = eInf_.isParentPathId() ? "1" : "0";
			String isAdditionalData = eInf_.isAdditionalData() ? "1" : "0";
			String isHidden = eInf_.isHidden() ? "1" : "0";
			String isSearchIdHolder = eInf_.isSearchIdHolder() ? "1" : "0";
			String formcodeLabel = generalUtil.getSpringMessagesByKey(formCode_, formCode_);
			String formCodeEntityDisplay = formcodeLabel;
			String formCodeTypeLabel = "";
			String isListId = eInf_.isIdList() ? "1" : "0";
			String isSearchElement = eInf_.isSearchElement() ? "1" : "0";
			String dataType_ = eInf_.getDataType();
			String dayaTyepInfo = ""; //TODO
			try {
				if (formcodeLabel.contains("(")) {
					int indx = formcodeLabel.lastIndexOf("(");
					formCodeEntityDisplay = formcodeLabel.substring(0, indx - 1).trim();
					formCodeTypeLabel = generalUtil.replaceLast(formcodeLabel.substring(indx + 1), ")", "").trim();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sbInsert.append("select '" + formCode_ + "','" + impCode_ + "','" + class_ + "','" + label_ + "','"
					+ isParent + "','" + isAdditionalData + "','" + isHidden + "','" + isSearchIdHolder + "','"
					+ formCodeEntityDisplay + "','" + formCodeTypeLabel + "','" + isListId + "','" + isSearchElement
					+ "','" + dataType_ + "','" + dayaTyepInfo + "' from dual union all\n");
		}

		generalDao.updateSingleStringNoTryCatch(generalUtil.replaceLast(sbInsert.toString(), "union all", ""));

		generalDao.updateSingleString("begin dbms_mview.refresh('FG_FORMELEMENTINFOATMETA_MV'); end;");
	}
}
