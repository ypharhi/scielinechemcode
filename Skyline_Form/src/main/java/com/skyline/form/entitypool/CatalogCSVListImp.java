package com.skyline.form.entitypool;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.skyline.form.bean.BeanType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.entity.Catalog;
import com.skyline.form.entity.CatalogDBInterface;
import com.skyline.form.service.FormTempData;

public class CatalogCSVListImp extends Catalog implements CatalogDBInterface {

	@Autowired
	private GeneralDao generalDao;
	
//	@Autowired
//	private FormStateManager formStateManager;

//	@Autowired
//	FormState formState;

	private String tableName;
	private String sqlFilter;
	private boolean isOrderBy;
	private HashMap<String, BeanType> itemMap; // TODO init with spring + scope!!!	 
	
	private static final Logger logger = LoggerFactory.getLogger(CatalogCSVListImp.class);


	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try {
			if (super.init(stateKey, formCode, impCode, initVal).equals("")) {
				this.tableName = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "tableName");
				this.sqlFilter = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "sqlFilter");				
				isOrderBy = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "isOrderBy"),true);
				setItemMap();				
				return "";
			}
			return "Creation failed";
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			return "Creation failed";
		}
	}

	@Override
	public String getItem(long stateKey, Map<String,String> formCatalogMap, FormTempData formTempDataMap, String item, StringBuilder info) {	
			String orderBy = (!isOrderBy) ? "":" ORDER BY \"" + item + "\"";
			String sql = "select distinct \"" + item + "\" from (select * from " + tableName + " where ("
					+ generalUtil.getEmpty(sqlFilter, " 1=1 ") + ")) where 1=1" + getWherePartByFilter(formCatalogMap) + orderBy;
		return generalDao.getCSVBySql(sql, false);
	}
	
	@Override
	public List<String> getItemArray(long stateKey, Map<String,String> formCatalogMap, FormTempData formTempDataMap, String item, StringBuilder info) {
			String sql = "select distinct \"" + item + "\" from (select * from " + tableName + " where ("
					+ generalUtil.getEmpty(sqlFilter, " 1=1 ") + ")) where 1=1" + getWherePartByFilter(formCatalogMap) + " ORDER BY \""
					+ item + "\"";
		return generalDao.getListOfStringBySql(sql);
	}


//	@Override
//	public String getItem(Map<String,String> formCatalogMap, FormTempData formTempDataMap, String item, String sourceElementImpCode, String info) {
//		String sql = "";
//		CatalogInfoType catalogInfoType = CatalogInfoType.valueOf(info);
//
//		switch (catalogInfoType) {
//		case COUNT: {
//			sql = "select count(*) from (select * from " + tableName + " where ("
//					+ generalUtil.getEmpty(sqlFilter, " 1=1 ") + ")) where 1=1" + getWherePartByFilter(sourceElementImpCode);
//		}
//			break;
//		case FIRST_VALUE: {
//			sql = "select * from (select  " + item + " from (select * from " + tableName + " where ("
//					+ generalUtil.getEmpty(sqlFilter, " 1=1 ") + ")) where 1=1" + getWherePartByFilter(sourceElementImpCode)
//					+ " ORDER BY " + item + ") where ROWNUM=1";
//		}
//			break;
//		case LAST_VALUE: {
//			sql = "select * from (select  " + item + " from (select * from " + tableName + " where ("
//					+ generalUtil.getEmpty(sqlFilter, " 1=1 ") + ")) where 1=1" + getWherePartByFilter(sourceElementImpCode)
//					+ " ORDER BY " + item + " DESC) where ROWNUM=1";
//		}
//			break;
//		default:
//			break;
//		}
//
//		return generalDao.selectSingleString(sql);
//	}

	@Override
	public String getSql(long stateKey, Map<String,String> formCatalogMap, FormTempData formTempDataMap, String isDistinct, String dataType) {
		String whereDataType = "";
		if(dataType.equals("meta")) {
			whereDataType = " where 1=2 ";
		} else if (dataType.equals("row")) {
			whereDataType = " where rownum <= 1 ";
		} else {
			whereDataType = " where 1=1 " + getWherePartByFilter(formCatalogMap);
		}
		
		String sqlReturn = "select " + (Boolean.valueOf(isDistinct) ? "distinct" : "") + " * from (select * from " + tableName + " where ("
				+ generalUtil.getEmpty(sqlFilter, " 1=1 ") + ")) " + whereDataType;
		logger.info("CatalogDBTableImp,getSql SQL: " + sqlReturn);
		return sqlReturn;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return tableName;
	}

	private String getWherePartByFilter(Map<String,String> formCatalogMap) {
		StringBuilder where = new StringBuilder();
		for (Map.Entry<String, String> entry : formCatalogMap.entrySet()) {
			if(!entry.getKey().contains(".")) { // invalid key
				logger.info("CatalogCSVListImp - " + entry.getKey() + " is invalid catalog item !!!!");
				where.append(" and 1=2 ");
				break;
			}
			BeanType beanType = getBeanTypeByItem(entry.getKey().split("\\.")[0]);
			if (!generalUtil.getNull(entry.getValue()).equals("") && beanType != null) {
				where.append(" and \"" + entry.getKey().split("\\.")[1] + "\" in (" + entry.getValue() + ")\n "); //TODO 9.6> make it according the rules in FormStateManager.setFormCatalog (in 9.6 it use only auto complete on dll that uses varchar columns from the catalog so only this if is in use)
			}
		}
		return where.toString();
	}

	private BeanType getBeanTypeByItem(String item) {
		BeanType beanType = null;
		if (itemMap != null) {
			beanType = itemMap.get(item);
		} else {
			Map<String, String> returnMap;
			returnMap = generalDao.getMetaData(tableName);
			if (returnMap != null) {
				for (Map.Entry<String, String> entry : returnMap.entrySet()) {
					if (entry.getKey().equals(item)) {
						beanType = BeanType.valueOf(entry.getValue());
						break;
					}
				}
			}
		}
		return beanType;
	}

	private void setItemMap() {
		Map<String, String> returnMap;
		itemMap = new HashMap<String, BeanType>();
		returnMap = generalDao.getMetaData(tableName);
		if (returnMap != null) {
			for (Map.Entry<String, String> entry : returnMap.entrySet()) {
				itemMap.put(entry.getKey(), BeanType.valueOf(entry.getValue()));
			}
		}
	}

	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema = "schema:{ \r\n" +
				"	tableName:{  \r\n" +
				"      type:'string',\r\n" +
				"      title:'Table',\r\n" +
				"      'enum':getResourceValueByType('CATALOG_ORACLE_CSV_COL_TABLE')\r\n" +
				"   },\r\n" +
				"isOrderBy : {\n" +
				"	type : 'string',\n" +
				"	title : 'Order By',\n" +		
				"	enum : ['','False'],\n" +
				"},\n" +
				"   sqlFilter:{  \r\n" +
				"      type:'string',\r\n" +
				"      title:'Where (optional filter)'\r\n" +
				"   },\r\n" +				
				(schema.equals("") ? "" : ",\n" + schema) +
				"\r\n" +
				"}";
		return schema;
	}
	
	//single tone class functions
	
	@Override
	public List<DataBean> getImpResourceForFormBuilder(String formCode, String impCode, String initVal) {
		List<DataBean> dataBeanList = super.getImpResourceForFormBuilder( formCode,  impCode,  initVal); //super return the bean
		dataBeanList.add(new DataBean(impCode, impCode, BeanType.CATALOGDB_IMP_CODE, "Info: " + impCode));
		Map<String, String> returnMap;
		String tableName = generalUtil.getJsonValById(initVal, "tableName");
		returnMap = generalDao.getMetaData(tableName);
		if (returnMap != null) {
			for (Map.Entry<String, String> entry : returnMap.entrySet()) {
				dataBeanList.add(new DataBean(impCode + "." + entry.getKey(), impCode + "." + entry.getKey(),
						BeanType.valueOf(entry.getValue()), "Info: " + entry.getKey()));
			}
		}
		return dataBeanList;
	}
}
