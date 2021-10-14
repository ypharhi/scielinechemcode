package com.skyline.form.dal;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.skyline.form.bean.InfData;
import com.skyline.general.bean.DataTableParamModel;

public interface GeneralDao {

	String getCSVBySql(String sql, boolean useUpperComma);

	Map<String, String> getMetaData(String viewName);

	Map<String, String> getMetaDataOrdered(String viewName);

	/**
	 * 
	 * @param sql
	 * @return number of update rows or -1 in case of exception
	 */
	String updateSingleString(String sql);

	String selectSingleString(String sql);

	JSONObject getJSONObjectForDTBySql(String sql, List<String> dateList);

	Map<String, String> sqlToHashMap(String sql);

	List<String> getListOfStringBySql(String sql);

	List<String> getDateListFromMetaData(Map<String, String> metaData);

	List<String> getListOfNameAndTypeFromMetaData(Map<String, String> metaData);

	JSONObject getJSONObjectOfDateTable(String sql, String hideEmptyColumns, String topRowsNum, StringBuilder sqlInfoSb, String ... args);

	JSONObject getJSONObjectOfDateTableServer(DataTableParamModel param, String sql);

	Map<String, String> getMetaDataRowValues(String sql);

	String getCSVBySqlNoException(String sql, boolean useUpperComma);

	List<Map<String, Object>> getListOfMapsBySql(String sql);

	String getTableColCsv(String tableName);

	boolean isTableExists(String tableName);

	boolean isViewExists(String tableName);

	String getSingleStringFromClob(String sql);

	String getSingleStringFromClobNoException(String sql);

	/**
	 * 
	 * @param pack
	 * @param function
	 * @param parameters- if the value of the parameter starts with "to_clob" => it will be cast to clob type (if in the store procedure it declare as VARCHAR it will be OK)
	 * @return result of the the DB function or "null" String if null
	 */
	String callPackageFunction(String pack, String function, Map<String, String> parameters);

	List<InfData> sqlToInfDataObjList(String sql);

	boolean checkIfColumnExists(String view, String column);

	String selectSingleStringNoException(String sql);

	JSONObject getJSONObjectOfDateTableUIReport(String strForSQL, String hideEmptyColumns,
			Map<String, String> titleMap);

	Map<String, Object> callProcedureReturnsOutObject(String pack, String procedureName,
			Map<String, String> simpleParameters, Map<String, String> outParameters, int oracleType);

	List<Map<String, Object>> callProcedureReturnsOutObject(String procedureName, List<String> parametersInCallOrder);

	int cloneTable(String string, String string2, Map<String, String> replaceFieldsMap);

	String getSingleStringFromBlob(String sql);

	int updateSingleStringNoTryCatch(String sql);

	InputStream getInputStreamFromBlob(String sql);

	byte[] getBytesFromBlob(String sql);

	Connection getConnectionFromDataSurce();

	void releaseConnectionFromDataSurce(Connection con);

	List<Map<String, String>> getListOfMapsWithClob(String sql);
	
	List<Map<String, Object>> getListOfMapsForDataTableBySql(String sql);
	
	List<Map<String, Object>> removeColumnsBeforeDisplay(List<Map<String, Object>> rows, String condition);

	void useScheSQLTimeOut();

	void setDefaultSQLTimeOut();

	String jsonSqlErrorMsg(String msg);

	ResultSet getResultSet(Connection con, String sql);

	Map<String, String> getMapsBySqlSingleRow(String string);

	void logMessage(String msg);

	JSONObject getJsonObjectBySqlSingleRow(String sql);
	
	void exeSql (String sql);

	//	Connection getConnectionFromDataSurceTask();

	//	String updateSingleStringTask(String sql);

	//	String selectSingleStringTask(String sql);

	//	Connection getConnection() throws Exception;
}
