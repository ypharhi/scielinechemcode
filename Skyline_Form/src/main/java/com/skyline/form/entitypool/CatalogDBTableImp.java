package com.skyline.form.entitypool;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.skyline.form.bean.BeanType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.entity.Catalog;
import com.skyline.form.entity.CatalogDBInterface;
import com.skyline.form.service.FormTempData;
import com.skyline.form.service.GeneralUtilConfig;

/**
 * 
 * @author YPharhi
 * implement Catalog interface 
 * NOTE: the sqlFilter support the following patterns:
 * 1) getTmpDataFilter(<formCode>,<formId>) replaced by formDao.getWherePartForTmpData - tmp data sql expression (as in data tables in Adama)
 * 2) getCriterialSql(<struct>,<criteria>,<userId>) - behaves as in the intagrationDTAdamaImp
 */
public class CatalogDBTableImp extends Catalog implements CatalogDBInterface {

	@Autowired
	private GeneralDao generalDao;
	@Autowired
	private GeneralUtilConfig generalUtilConfig;
//	@Autowired
//	private FormStateManager formStateManager;
	
//	@Autowired
//	private FormStateManager formStateManager;

//	@Autowired
//	FormState formState;
	
	//getWherePartForTmpData
	
	@Autowired
	private FormDao formDao;

	private String tableName;
//	private String sqlFilter;
	private String cacheTableList;
	private HashMap<String, BeanType> itemMap; // TODO init with spring + scope!!!	 
	private HashMap<String, List<String>> tableToItemsMap;
	private boolean isOrderBy;
//	@SuppressWarnings("unused")
//	private String filterExpression1;
//	@SuppressWarnings("unused")
//	private String filterExpressionDesc1;
	private String formCode;
//	private boolean avoidCleanMissingParam;
	private String replaceFilterParamType = "";
	private String orderByExpression,distinctOption;
	
	private static final Logger logger = LoggerFactory.getLogger(CatalogDBTableImp.class);

	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) 
	{
		this.formCode = formCode;
		try {
			if (super.init(stateKey, formCode, impCode, initVal).equals("")) {
				this.tableName = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "tableName");
//				this.avoidCleanMissingParam = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "avoidCleanMissingParam"),false); // yp 030502018 - to have the ability to keep parameters that not found 
				this.replaceFilterParamType = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "replaceFilterParamType");
				this.cacheTableList = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "cacheTableList");
//				this.filterExpression1 = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "filterExpression1");
//				this.filterExpressionDesc1 = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "filterExpressionDesc1");
				isOrderBy = generalUtil.getNullBoolean(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "isOrderBy"),true);
				this.orderByExpression = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "orderByExpression");
				distinctOption = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "distinctOption");

//				setItemMap();
				initTableToItemsMap();
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
		//TODO eyal - try using cacheTableList if the item exists in them (try according the csv order)
		//			- check if item can be also list of columns (csv) if yes change the data table element to list of columns and not entire list (we assume in that we have only one catalog in a form in version 9.6)
		String table = getTable(item);
		String result;
		String orderBy = (isOrderBy) ? " ORDER BY \"" + item + "\"": "";
		String distinct = (isOrderBy) ? "distinct" : ""; // if order by false (the default is true) we don't use distinct (its under the catalog creator responsibility)
		String orderByExp = (orderByExpression == null || orderByExpression.isEmpty())?"":" ORDER BY " + orderByExpression.replaceAll("@ITEM@", item) ;
		while (true) {
			String sql = "";
			if(orderByExp.isEmpty() && (distinctOption == null || distinctOption.isEmpty())) {
				sql = "select " + distinct + " \"" + item + "\" from (select * from " + table + " where ("
					+ generalUtil.getEmpty(getSqlFilter(stateKey, formTempDataMap), " 1=1 ") + ")) where 1=1" + getWherePartByFilter(formCatalogMap) + orderBy;
			} else {
				sql = "select " + (distinctOption.equalsIgnoreCase("true")?"distinct":"") + " \"" + item + "\" from (select * from " + table + " where ("
					+ generalUtil.getEmpty(getSqlFilter(stateKey, formTempDataMap), " 1=1 ") + ")) where 1=1" + getWherePartByFilter(formCatalogMap) + orderByExp ;
			}
//			logger.info("SQL BY CATALOG FOR ELEMENT: " + sourceElementImpCode + ", SQL:" + sql);
			
			if(info != null) {
				info.append(sql + " ");
			}
			
			result = generalDao.getCSVBySql(sql, false);
			if ((result.equals("-1")) && (!table.equals(tableName))) {
				table = updateTable(item);
			} else {
				break;
			}
		}
		return result;
	}
	
	private String getSqlFilter(long stateKey, FormTempData formTempDataMap) {
		String sqlFilter = "";
//		if(avoidCleanMissingParam) {
//			 sqlFilter = generalUtilForm.getJsonValReplaceParamSelectively(stateKey, formCode, jsonInit, "sqlFilter");
//		} else { 
//			 sqlFilter = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "sqlFilter");
//		}
		
		if(replaceFilterParamType.equals("SELECTIVE")) {
			sqlFilter = generalUtilForm.getJsonValReplaceParamSelectively(stateKey, formCode, jsonInit, "sqlFilter");
		} else if(replaceFilterParamType.equals("CLEAN")) {
			sqlFilter = generalUtilForm.getJsonValReplaceParamClean(stateKey, formCode, jsonInit, "sqlFilter");
		} else {
			sqlFilter = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "sqlFilter"); //defualt
		}
		
		
		 
		//set sqlFilterToReturn with sqlFilter as default
		String sqlFilterToReturn = sqlFilter;
		
		//check for known patterns
		if(generalUtil.getNull(sqlFilter).contains("getTmpDataFilter")) {
			// replace all @getTmpDataFilter\\(.*?\\)@ patern with temp data
			Pattern pattern = Pattern.compile("getTmpDataFilter\\(.*?\\)"); 
			Matcher matcher = pattern.matcher(sqlFilter);
			while (matcher.find()) {
				String found_ = matcher.group(0);
				found_ = getTmpDataFilter(formTempDataMap, found_.replace("getTmpDataFilter(", "").replace(")",""));
				sqlFilterToReturn = sqlFilter.replaceFirst("getTmpDataFilter\\(.*?\\)", found_);
//				matcher.appendReplacement(sqlFilterToReturn, found_);
			}
		}
		
		if(generalUtil.getNull(sqlFilter).contains("getCriterialSql")) {
			// replace all @getrCiteriaWherePart\\(.*?\\)@ patern with temp data
			Pattern pattern = Pattern.compile("getCriterialSql\\(.*?\\)"); 
			Matcher matcher = pattern.matcher(sqlFilter);
			while (matcher.find()) {
				String found_ = matcher.group(0);
				found_ = getCriterialSql(stateKey, found_.replace("getCriterialSql(", "").replace(")",""));
				sqlFilterToReturn = sqlFilter.replaceFirst("getCriterialSql\\(.*?\\)", found_);
			}
		}
		
		return sqlFilterToReturn;
	}

	
	private String getCriterialSql(long stateKey, String args) {
		String sqlScript = "";
		Map<String, String> sqlParam = generalUtilFormState.getFormParam(stateKey, formCode);

		String struct ="";
		String userId="";
		String criteria = "";
		
		if(args.contains(",")){
			struct = args.split(",")[0];
			criteria = args.split(",")[1];
			userId = args.split(",")[2];
		}
		
		sqlParam.put("$P{STRUCT}", struct);
		sqlParam.put("$P{USERID}", userId);
		sqlScript = generalUtilConfig.getCriterialSql(struct, criteria, formCode, sqlParam, null);

		return sqlScript;
	}
	
	private String getTable(String item) {	  
		// return the table that the item exist in it
		if ((cacheTableList == null) || (cacheTableList.equals(""))) {
			return tableName;
		}		
		List<String> requiredItemsList = new ArrayList<String>(Arrays.asList(item.split(",")));
		String[] cacheTableArray = cacheTableList.split(",");
		for (int i = 0; i < cacheTableArray.length; i++) {
			List<String> itemsList = (List<String>) tableToItemsMap.get(cacheTableArray[i]);
			if (itemsList.containsAll(requiredItemsList)) {
				return cacheTableArray[i];
			}
		}
		for (int i = 0; i < cacheTableArray.length; i++) {
			if (isExistOnTable(requiredItemsList, cacheTableArray[i])) {
				tableToItemsMap.get(cacheTableArray[i]).addAll(requiredItemsList);
				return cacheTableArray[i];
			}
		}
		return tableName;
	}

	private String updateTable(String item) {
		// return the next table that the item exist in it
		List<String> requiredItemsList = new ArrayList<String>(Arrays.asList(item.split(" , ")));
		String[] cacheTableArray = cacheTableList.split(",");
		int k = cacheTableArray.length;
		for (int i = 0; i < cacheTableArray.length; i++) {
			List<String> itemsList = (List<String>) tableToItemsMap.get(cacheTableArray[i]);
			if (itemsList.containsAll(requiredItemsList)) {
				tableToItemsMap.get(cacheTableArray[i]).removeAll(requiredItemsList);
				k = i;
				break;
			}
		}
		for (int i = k + 1; i < cacheTableArray.length; i++) {
			if (isExistOnTable(requiredItemsList, cacheTableArray[i])) {
				tableToItemsMap.get(cacheTableArray[i]).addAll(requiredItemsList);
				return cacheTableArray[i];
			}
		}
		return tableName;
	}

	private Boolean isExistOnTable(List<String> requiredItemsList, String table) {
		// check item exist in table
		Map<String, String> metaDataMap = generalDao.getMetaData(table);
		if (metaDataMap != null) {
			List<String> columns = new ArrayList<String>();
			for (Map.Entry<String, String> entry : metaDataMap.entrySet()) {
				columns.add(entry.getKey());
			}
			if (columns.containsAll(requiredItemsList)) {
				return true;
			}
		}
		return false;
	}

	private void initTableToItemsMap() {
		if (tableToItemsMap == null) {
			String[] cacheTableArray = cacheTableList.split(",");
			tableToItemsMap = new HashMap<String, List<String>>();
			for (int i = 0; i < cacheTableArray.length; i++) {
				tableToItemsMap.put(cacheTableArray[i], new ArrayList<String>());
			}
		}
	}

//	@Override
//	public String getItem(String item, String sourceElementImpCode, String info) {
//		String sql = "";
//		CatalogInfoType catalogInfoType = CatalogInfoType.valueOf(info);
//
//		switch (catalogInfoType) {
//		case COUNT: {
//			sql = "select count(*) from (select * from " + tableName + " where ("
//					+ generalUtil.getEmpty(getSqlFilter(), " 1=1 ") + ")) where 1=1" + getWherePartByFilter(sourceElementImpCode);
//		}
//			break;
//		case FIRST_VALUE: {
//			sql = "select * from (select  " + item + " from (select * from " + tableName + " where ("
//					+ generalUtil.getEmpty(getSqlFilter(), " 1=1 ") + ")) where 1=1" + getWherePartByFilter(sourceElementImpCode)
//					+ " ORDER BY " + item + ") where ROWNUM=1";
//		}
//			break;
//		case LAST_VALUE: {
//			sql = "select * from (select  " + item + " from (select * from " + tableName + " where ("
//					+ generalUtil.getEmpty(getSqlFilter(), " 1=1 ") + ")) where 1=1" + getWherePartByFilter(sourceElementImpCode)
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
	public String getSql(long stateKey, Map<String,String> formCatalogMap, FormTempData formTempDataMap, String isDistinct, String dataType) 
	{
		String whereDataType = "";
		String sqlReturn = "";
		if(dataType.equals("meta")) {
			whereDataType = " where 1=2 ";
		} else if (dataType.equals("row")) {
			whereDataType = " where rownum <= 1 ";
		} else {
			whereDataType = " where 1=1 " + getWherePartByFilter(formCatalogMap);
		}
		//CODE FOR SKYLINE. ab 31072017 TODO: change code above to be more dynamic. Added in v9.7 to fix bug in Skyline.
		if(formCode.equals("CurrInventoryRep"))
		{
			sqlReturn = "select distinct INVENTORY_TYPE, INVENTORY_TYPE_ID,QUANTITY_UOM,SUM_QNT_MINUS_USGS,Description_Chemical_Name,Retest_Expiry_Date,"
										+ "PROJECT,RECEIVE_DATE,VOLUME ,VOLUME_UOM,Date_Type,INVENTORY_GROUP9_ID,GMP_EXPERIMENTAL,STOCK_ITEM#,site_name" + 
						" from (select * from " + tableName + " where ("
					+ generalUtil.getEmpty(getSqlFilter(stateKey, formTempDataMap), " 1=1 ") + ")) " + whereDataType + 
					" \n order by INVENTORY_GROUP9_ID";
		}
		else
		{
			sqlReturn = "select " + (Boolean.valueOf(isDistinct) ? "distinct" : "") + " * from (select * from " + tableName + " where ("
					+ generalUtil.getEmpty(getSqlFilter(stateKey, formTempDataMap), " 1=1 ") + ")) " + whereDataType;
		}
		logger.info("CatalogDBTableImp,getSql SQL: " + sqlReturn);
		return sqlReturn;
	}

	private String getTmpDataFilter(FormTempData formTempDataMap, String tmpDataFormCodeAndId) {
//		return "1=1";
		String toReturn = "";
		
		try {
//			String tmpDataFormCodeAndId = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "useTmpDataWherePart");
//			useTmpDataWherePart = "getTmpDataFilter(OperationType,129738)";
			 
			String formCode = "";
			String parentId = "";
			if(tmpDataFormCodeAndId.contains(",")) {
				formCode = tmpDataFormCodeAndId.split(",")[0];
				parentId = tmpDataFormCodeAndId.split(",")[1];
				toReturn = formDao.getWherePartForTmpData(formTempDataMap.getSessionId(parentId), formCode, parentId);
//				toReturn = generalUtilFormState.getWherePartForTmpData(formCode, parentId); 
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
		}
		
		return toReturn;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return tableName;
	}

	private String getWherePartByFilter(Map<String, String> formCatalogMap) {
		StringBuilder where = new StringBuilder();
		for (Map.Entry<String, String> entry : formCatalogMap.entrySet()) {
			if (!generalUtil.getNull(entry.getValue()).equals("")) {
				if(!entry.getKey().contains(".")) { // invalid key
					logger.info("CatalogDBTableImp - " + entry.getKey() + " is invalid catalog item !!!!");
					where.append(" and 1=2 ");
					break;
				}
				BeanType beanType = getBeanTypeByItem(entry.getKey().split("\\.")[1]);
				if (beanType != null && !entry.getValue().equals("'ALL'")) {
					String colName = entry.getKey().split("\\.")[1];
					if(colName.endsWith("OBJDATERANGE")) {					
						if(!entry.getValue().split(";", -1)[0].equals("")){
							where.append(" and TO_DATE(\"" + colName + "\",'" + generalUtil.getConversionDateFormat() + "') >= " + entry.getValue().split(";", -1)[0]);
						}
						if(!entry.getValue().split(";", -1)[1].equals("")){
							where.append(" and TO_DATE(\"" + colName + "\",'" + generalUtil.getConversionDateFormat() + "') <= " + entry.getValue().split(";", -1)[1]);
						}
					} else {
						where.append(" and \"" + colName + "\" in (" + entry.getValue() + ")\n "); 
					}
				}
			}
		}
		
		try {
			if(formCode.equalsIgnoreCase("ExpAnalysisReport")||formCode.equalsIgnoreCase("ExperimentReport")) { // patch for ExpAnalysisReport to make <= not in
				return where.toString().replaceAll("and \"NOSTEPSEQ\" in", "and to_number(\"NOSTEPSEQ\") <=");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return where.toString();
	}

	

	private BeanType getBeanTypeByItem(String item) {
		BeanType beanType = null;
		if (itemMap != null) { 
			beanType = itemMap.get(item);
		} else {
//			Map<String, String> returnMap;
//			returnMap = generalDao.getMetaData(tableName);
//			if (returnMap != null) {
//				for (Map.Entry<String, String> entry : returnMap.entrySet()) {
//					if (entry.getKey().equals(item)) {
//						beanType = BeanType.valueOf(entry.getValue());
//						break;
//					}
//				}
//			}
			setItemMap();
			beanType = itemMap.get(item);
		}
		return beanType;
	}

	private void setItemMap() {
		Map<String, String> returnMap;
		itemMap = new HashMap<String, BeanType>();
		returnMap = generalDao.getMetaData(tableName + "");
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
				"      'enum':getResourceValueByType('CATALOG_ORACLE_TABLE')\r\n" +
				"   },\r\n" +
				"   sqlFilter:{  \r\n" +
				"      type:'string',\r\n" +
				"      title:'Where (optional filter)'\r\n" +
				"   },\r\n" +
				"replaceFilterParamType : {\n" +
				"	type : 'string',\n" +
				"	title : 'Where (optional filter) replace param type if missing (SELECTIVE-replace other params, </br>CLEAN-ignor filter, (empty) no replacment will be made)',\r\n" +	
				"	enum : ['','SELECTIVE','CLEAN'],\n" +
				"},\n"  +
//				"   filterExpression1:{  \r\n" +
//				"      type:'string',\r\n" +
//				"      title:'Filter expression (optional)'\r\n" +
//				"   },\r\n" +
//				"   filterExpressionDesc1:{  \r\n" +
//				"      type:'string',\r\n" +
//				"      title:'Filter expression description (optional)'\r\n" +
//				"   },\r\n" +
//				"   useTmpDataWherePart:{  \r\n" +
//				"      type:'string',\r\n" +
//				"      title:'Use tmp data on where part using formcode,parentid '\r\n" +
//				"   },\r\n" +
//				"	avoidCleanMissingParam: { \r\n" +
//				" type: 'boolean',\r\n" + 
//				" title: 'Avoid clean missing parameter in sqlFilter (clean is default)'\r\n" + 
//				"	},\r\n" + 
				"isOrderBy : {\n" +
				"	type : 'string',\n" +
				"	title : 'Use Distinct and Order By Item (default true) [not in use if an optional (distinct or order) setting exists]',\n" +		
				"	enum : ['','False'],\n" +
				"},\n"  +
				"orderByExpression : {\n" +
				"	type : 'string',\n" +
				"	title : 'Set order by (optional - using distinct as in set distinct)',\n" +	
				"},\n" +
				"distinctOption : {\n" +
				"	type : 'string',\n" +
				"	title : 'Set distinct (optional - using order as in set order by)',\n" +	
				"	enum : ['','True','False'],\n" +
				"},\n" +
				"	cacheTableList : {\r\n" +
				"		type : 'array',\r\n" +
				"		title : 'Cache (optional for performance improvements)',\r\n" +
				"		items : {\r\n" +
				"			enum : [''].concat(getResourceValueByType('CATALOG_ORACLE_TABLE')),\r\n" +
				"			title : 'Top level table:'\r\n" +
				"		}\r\n" +
				"	},\r\n" +
				(schema.equals("") ? "" : ",\n" + schema) +
				"\r\n" +
				"}";
		return schema;
	}

	@Override
	public List<String> getItemArray(long stateKey, Map<String,String> formCatalogMap, FormTempData formTempDataMap, String item, StringBuilder info) {
		String table = getTable(item);
		List<String> result;
		String orderBy = (isOrderBy) ? " ORDER BY \"" + item + "\"": "";
		String distinct = (isOrderBy) ? "distinct" : ""; // if order by false (the default is true) we don't use distinct (its under the catalog creator responsibility)
		String orderByExp = (orderByExpression.isEmpty())?"":" ORDER BY " + orderByExpression.replaceAll("@ITEM@", item) ;
		while (true) {
			String sql = "";
			if(orderByExp.isEmpty() && (distinctOption == null || distinctOption.isEmpty())) {
				sql = "select " + distinct + " \"" + item + "\" from (select * from " + table + " where ("
					+ generalUtil.getEmpty(getSqlFilter(stateKey, formTempDataMap), " 1=1 ") + ")) where 1=1" + getWherePartByFilter(formCatalogMap) + orderBy;
			} else{
				sql = "select " + (distinctOption.equalsIgnoreCase("true")?"distinct":"") + " \"" + item + "\" from (select * from " + table + " where ("
					+ generalUtil.getEmpty(getSqlFilter(stateKey, formTempDataMap), " 1=1 ") + ")) where 1=1" + getWherePartByFilter(formCatalogMap) + orderByExp ;
			}
//			logger.info("SQL BY CATALOG FOR ELEMENT: " + sourceElementImpCode + ", SQL:" + sql);
			
			if(table.equalsIgnoreCase("skyline_api_planning_code_v")) { //patch "Taro develop"
				sql = "select distinct t.product_code as planning_code from skyline_develop_server.skyline_template_planning_v t order by t.product_code";
			}
			
			if(info != null) {
				info.append(sql + " ");
			}
			
			result = generalDao.getListOfStringBySql(sql);
			if ((result.equals("-1")) && (!table.equals(tableName))) {
				table = updateTable(item);
			} else {
				break;
			}
		}
		return result;
	}
	
	//single tone class functions
	
	@Override
	public List<DataBean> getImpResourceForFormBuilder(String formCode, String impCode, String initVal) {
		List<DataBean> dataBeanList = super.getImpResourceForFormBuilder(formCode, impCode, initVal); //super return the bean
		dataBeanList.add(new DataBean(impCode, impCode, BeanType.CATALOGDB_IMP_CODE, "Info: " + impCode));
		Map<String, String> returnMap;
		String tableName = generalUtil.getJsonValById(initVal, "tableName");
		returnMap = generalDao.getMetaData(tableName);
		if (returnMap != null) {
			for (Map.Entry<String, String> entry : returnMap.entrySet()) {
				String colName = entry.getKey();
				try {
					if (colName.endsWith("OBJIDVAL")) {
						dataBeanList.add(new DataBean(impCode + "." + entry.getKey(), impCode + "." + entry.getKey(),
								BeanType.valueOf("OBJIDVAL"), "Info: " + entry.getKey()));
					} else if (colName.endsWith("OBJPARAM")) {
						dataBeanList.add(new DataBean(impCode + "." + entry.getKey(), impCode + "." + entry.getKey(),
								BeanType.valueOf("OBJPARAM"), "Info: " + entry.getKey()));
					} else if (colName.endsWith("OBJIMG")) {
						dataBeanList.add(new DataBean(impCode + "." + entry.getKey(), impCode + "." + entry.getKey(),
								BeanType.valueOf("OBJIMG"), "Info: " + entry.getKey()));
					} else if (colName.endsWith("OBJDATERANGE")) {
						dataBeanList.add(new DataBean(impCode + "." + entry.getKey(), impCode + "." + entry.getKey(),
								BeanType.valueOf("OBJDATERANGE"), "Info: " + entry.getKey()));
					} else if (colName.endsWith("OBJFORMTESTCONF")) {
						dataBeanList.add(new DataBean(impCode + "." + entry.getKey(), impCode + "." + entry.getKey(),
								BeanType.valueOf("OBJFORMTESTCONF"), "Info: " + entry.getKey()));
					} else {
						dataBeanList.add(new DataBean(impCode + "." + entry.getKey(), impCode + "." + entry.getKey(),
								BeanType.valueOf(entry.getValue()), "Info: " + entry.getKey()));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return dataBeanList;
	}
}
