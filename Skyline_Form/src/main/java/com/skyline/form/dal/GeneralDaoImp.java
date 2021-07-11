package com.skyline.form.dal;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.InfData;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.LookupType;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilCalc;
import com.skyline.form.service.GeneralUtilForm;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.general.bean.DataTableParamModel;

import oracle.jdbc.OracleTypes;
import oracle.sql.CLOB;

@Repository("GeneralDao")
public class GeneralDaoImp extends BasicDao implements GeneralDao {

	private static final Logger logger = LoggerFactory.getLogger(GeneralDaoImp.class);

	//	private JdbcTemplate jdbcTemplate;

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private GeneralUtilForm generalUtilForm;

	@Autowired
	private GeneralUtilCalc generalUtilCalc;

	@Value("${jdbc.driverClassName}")
	private String driverClassName;
	@Value("${jdbc.username}")
	private String username;
	@Value("${jdbc.url}")
	private String url;
	@Value("${jdbc.password}")
	private String password;

	@Value("${dataTableTopRowsNum:10000}")
	private String dataTableTopRowsNumDefault;

	@Value("${reactionAndResultsAnalysisRuleName:Main Solvent,Limiting Agent,Material Type,Experiment Materials}")
    private String reactionAndResultsAnalysisRuleName_;
	
	@Value("${reactionAndResultsAnalysisColsSelection:All,Qty,Mole,Volume,Purity,Equivalent,Batch}")
    private String reactionAndResultsAnalysisColsSelection_;
	
	@Autowired
	public GeneralUtilLogger generalUtilLogger;

	@Autowired
	private FormDao formDao;

	//	@Autowired
	//	public void setDataSource(DataSource dataSource) {
	//		this.jdbcTemplate = new JdbcTemplate(dataSource);
	//	}

	//	@PostConstruct
	//    public void init(){
	//		cahceViewMetaData = new HashMap<String,Map<String,String>>();
	//		String sql = "select t.TABLE_NAME || ',' || t.COLUMN_NAME || ',' || t.DATA_TYPE as col_info\r\n" + 
	//				"from user_tab_cols t\r\n" + 
	//				"where 1=1 order by t.TABLE_NAME";
	//		List<String>List<String> colInfo = getListOfStringBySql(sql);
	//		
	//		String lastViewName = "";
	//		HashMap<String,String> colInfoMap = new HashMap<String,String>();
	//		
	//		for (String ci_ : colInfo) {
	//			if(!ci_.split(",")[0].equals(lastViewName)) {
	//				if(!lastViewName.equals("")) {
	//					cahceViewMetaData.put(lastViewName, colInfoMap);
	//					colInfoMap = new HashMap<String,String>();
	//				}
	//				colInfoMap.put(ci_.split(",")[1],ci_.split(",")[2]);
	//				
	//			} else {
	//				colInfoMap.put(ci_.split(",")[1],ci_.split(",")[2]);
	//			} 
	//			lastViewName = ci_.split(",")[0];
	//		}
	//		cahceViewMetaData.put(lastViewName, colInfoMap);
	//	}

	@Override
	public String getCSVBySql(String sql, boolean useUpperComma) {
		logger.info("/* SQL getCSVBySql sql=*/ " + sql);
		String CSVString = "";
		try {
			List<String> o = jdbcTemplate.queryForList(sql, String.class);
			if (useUpperComma) {
				CSVString = (generalUtil.replaceLast(o.toString().replaceFirst("\\[", "'"), "]", "'").replace(", ",
						"','"));
			} else {
				CSVString = (generalUtil.replaceLast(o.toString().replaceFirst("\\[", ""), "]", "").replace(", ", ","));
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(LevelType.ERROR, "SQL=[" + sql + "] error in sql!", "", ActivitylogType.SQLError,
					null, e);
			CSVString = "-1";
		}
		return CSVString;
	}

	@Override
	public String getCSVBySqlNoException(String sql, boolean useUpperComma) {
		logger.info("/* SQL getCSVBySqlNoexception sql=*/ " + sql);
		String CSVString = "";
		List<String> o = jdbcTemplate.queryForList(sql, String.class);
		if (useUpperComma) {
			CSVString = (generalUtil.replaceLast(o.toString().replaceFirst("\\[", "'"), "]", "'").replace(", ", "','"));
		} else {
			CSVString = (generalUtil.replaceLast(o.toString().replaceFirst("\\[", ""), "]", "").replace(", ", ","));
		}

		return CSVString;
	}

	@Override
	public List<Map<String, String>> getListOfMapsWithClob(String sql) {
		logger.info("/* SQL getListOfMapsWithClob sql=*/ " + sql);
		List<Map<String, String>> rows = new ArrayList<>();
		try {
			rows = jdbcTemplate.query(sql, new ResultSetExtractor<List<Map<String, String>>>() {
				@Override
				public List<Map<String, String>> extractData(ResultSet rs) throws SQLException, DataAccessException {
					List<Map<String, String>> currentRows = new ArrayList<Map<String, String>>();
					ResultSetMetaData rsmd = rs.getMetaData();
					int columnCount = rsmd.getColumnCount();
					List<String> colTypes = new ArrayList<>(columnCount);
					for (int i = 1; i <= columnCount; i++) {
						colTypes.add(rsmd.getColumnTypeName(i));
					}
					while (rs.next()) {
						Map<String, String> metaDataMap = new LinkedCaseInsensitiveMap<String>(columnCount);
						for (int i = 1; i <= columnCount; i++) {

							//metaDataMap.put(rsmd.getColumnName(i), generalUtil.getNull(getColumnValue(colTypes.get(i-1), i ,rs).toString()));
							Object colValObj = getColumnValue(colTypes.get(i - 1), i, rs);
							String colVal = "";
							if (colValObj != null) {
								colVal = generalUtil.getNull(colValObj.toString());
							}
							metaDataMap.put(rsmd.getColumnName(i), colVal);
						}
						currentRows.add(metaDataMap);
					}
					return currentRows;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			rows.clear();
		}
		return rows;
	}
	
	@Override
	public List<Map<String, Object>> getListOfMapsForDataTableBySql(String sql) {
		logger.info("/* SQL getListOfMapsForDataTableBySql sql=*/ " + sql);
		List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
		rows = jdbcTemplate.query(sql,
				new ResultSetExtractor<List<Map<String, Object>>>() {
					@Override
					public List<Map<String, Object>> extractData(ResultSet rs)
							throws SQLException, DataAccessException {
						List<Map<String, Object>> currentRows = new ArrayList<Map<String, Object>>();
						ResultSetMetaData rsmd = rs.getMetaData();
						int columnCount = rsmd.getColumnCount();
						List<String> colTypes = new ArrayList<>(columnCount);
						for (int i = 1; i <= columnCount; i++) {
							colTypes.add(rsmd.getColumnTypeName(i));
						}
						while (rs.next()) {
							Map<String, Object> metaDataMap = new LinkedCaseInsensitiveMap<Object>(columnCount);
							for (int i = 1; i <= columnCount; i++) {
								metaDataMap.put(rsmd.getColumnName(i), getColumnValue(colTypes.get(i - 1), i, rs));
							}
							currentRows.add(metaDataMap);
						}
						return currentRows;
					}
				});
		return rows;
	}

	@Override
	public List<Map<String, Object>> getListOfMapsBySql(String sql) {
		logger.info("/* SQL getListOfMapsBySql sql=*/ " + sql);
		List<Map<String, Object>> returnListOfFMap;
		try {
			returnListOfFMap = jdbcTemplate.queryForList(sql);

		} catch (Exception e) {
			generalUtilLogger.logWrite(LevelType.ERROR, "SQL=[" + sql + "] error in sql!", "", ActivitylogType.SQLError,
					null, e);
			returnListOfFMap = null;
			e.printStackTrace();
		}
		return returnListOfFMap;
	}

	@Override
	public Map<String, String> getMetaData(String viewName) {
		Map<String, String> returnMap;
		if (viewName.toLowerCase().trim().startsWith("select")) {
			try {
				logger.info("/* SQL getMetaData viewName=*/ select * from (" + viewName + ") where 1 = 2");
				returnMap = jdbcTemplate.query("select * from (" + viewName + ") where 1 = 2",
						new ResultSetExtractor<Map<String, String>>() {
							@Override
							public Map<String, String> extractData(ResultSet rs)
									throws SQLException, DataAccessException {
								Map<String, String> MetaDataMap = new HashMap<String, String>();
								ResultSetMetaData rsmd = rs.getMetaData();
								int columnCount = rsmd.getColumnCount();
								for (int i = 1; i <= columnCount; i++) {
									MetaDataMap.put(rsmd.getColumnName(i), rsmd.getColumnTypeName(i));
								}
								return MetaDataMap;
							}
						});
			} catch (Exception e) {
				generalUtilLogger.logWrite(LevelType.ERROR, "viewName=" + viewName + " error in sql!", "",
						ActivitylogType.SQLError, null, e);
				returnMap = null;
			}
		} else {
			returnMap = new HashMap<String, String>();
			String sql = "select t.COLUMN_NAME || ',' || t.DATA_TYPE as col_info\r\n" + "from user_tab_cols t\r\n"
					+ "where t.TABLE_NAME ='" + viewName + "'";
			List<String> colInfo = getListOfStringBySql(sql);
			for (String ci_ : colInfo) {
				returnMap.put(ci_.split(",")[0], ci_.split(",")[1]);
			}
		}

		return returnMap;
	}

	@Override
	public Map<String, String> getMetaDataOrdered(String viewName) {
		logger.info("/* SQL getMetaDataOrdered viewName=*/ " + viewName);
		Map<String, String> returnMap = new LinkedHashMap<String, String>();
		if (viewName.toLowerCase().trim().startsWith("select")) {
			try {
				returnMap = jdbcTemplate.query("select * from (" + viewName + ") where 1 = 2",
						new ResultSetExtractor<Map<String, String>>() {
							@Override
							public Map<String, String> extractData(ResultSet rs)
									throws SQLException, DataAccessException {
								Map<String, String> metaDataMap = new LinkedHashMap<String, String>();
								ResultSetMetaData rsmd = rs.getMetaData();
								int columnCount = rsmd.getColumnCount();
								for (int i = 1; i <= columnCount; i++) {
									metaDataMap.put(rsmd.getColumnName(i), rsmd.getColumnTypeName(i));
								}
								return metaDataMap;
							}
						});
			} catch (Exception e) {
				generalUtilLogger.logWrite(LevelType.ERROR, "viewName=[" + viewName + "] error in sql!", "",
						ActivitylogType.SQLError, null, e);
				returnMap = null;
			}
		} else {
			/*returnMap = new HashMap<String,String>();
			String sql = "select t.COLUMN_NAME || ',' || t.DATA_TYPE as col_info\r\n" + 
					"from user_tab_cols t\r\n" + 
					"where t.TABLE_NAME ='" + viewName + "'";
			List<String> colInfo = getListOfStringBySql(sql);
			for (String ci_ : colInfo) {
				returnMap.put(ci_.split(",")[0],ci_.split(",")[1]);
			}*/

		}
		return returnMap;
	}

	@Override
	public String getTableColCsv(String tableName) {
		logger.info(
				"getMetaDataCsv sql=*/  select t.COLUMN_NAME from user_tab_columns t where upper(t.TABLE_NAME) = upper('"
						+ tableName + "') ");
		String sql = " select t.COLUMN_NAME from user_tab_columns t where upper(t.TABLE_NAME) = upper('" + tableName
				+ "') ";
		return getCSVBySql(sql, false);
	}

	@Override
	public Map<String, String> getMetaDataRowValues(String sql) {
		logger.info("/* SQL getMetaDataRowValues sql=*/ " + sql);
		Map<String, String> returnMap;
		try {
			returnMap = jdbcTemplate.query(sql, new ResultSetExtractor<Map<String, String>>() {
				@Override
				public Map<String, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
					Map<String, String> MetaDataValMap = new HashMap<String, String>();
					ResultSetMetaData rsmd = rs.getMetaData();
					int columnCount = rsmd.getColumnCount();
					if (rs.next()) {
						for (int i = 1; i <= columnCount; i++) {
							MetaDataValMap.put(rsmd.getColumnName(i), generalUtil.getNull(rs.getString(i)));
						}
					}
					return MetaDataValMap;
				}
			});
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			returnMap = null;
			e.printStackTrace();
		}
		return returnMap;
	}

	@Override
	public List<String> getListOfNameAndTypeFromMetaData(Map<String, String> metaData) {
		List<String> toReturn = new ArrayList<String>();
		if (metaData != null) {
			for (Map.Entry<String, String> entry : metaData.entrySet()) {
				toReturn.add(entry.getKey() + ":" + entry.getValue());
			}
		}
		return toReturn;
	}

	@Override
	public List<String> getDateListFromMetaData(Map<String, String> metaData) {
		List<String> dateList = new ArrayList<String>();
		for (Map.Entry<String, String> entry : metaData.entrySet()) {
			if (entry.getValue().equals("DATE")) {
				dateList.add(entry.getKey());
			}
		}
		return dateList;
	}

	@Override // TODO YARON RENAME updateSql
	public String updateSingleString(String sql) {
		logger.info("/* SQL updateSingleString sql=*/ " + sql);
		String update;
		try {
			update = String.valueOf(jdbcTemplate.update(sql));
		} catch (Exception e) {
			generalUtilLogger.logWrite(LevelType.ERROR, "SQL=[" + sql + "] error in sql!", "", ActivitylogType.SQLError,
					null, e);
			e.printStackTrace();
			update = "-1";
		}
		return update;
	}

	@Override //TODO yaron rename updateSqlThrowsException
	public int updateSingleStringNoTryCatch(String sql) {
		logger.info("/* SQL updateSingleStringNoTryCatch sql=*/ " + sql);
		int update = jdbcTemplate.update(sql);
		return update;
	}

	@Override
	public String selectSingleString(String sql) {
		logger.info("/* SQL selectSingleString sql=*/ " + sql);
		String singleString = "";
		try {
			Object o = (String) jdbcTemplate.queryForObject(sql, String.class);
			singleString = (String) o;

		} catch (Exception e) {
//			StringWriter errors = new StringWriter();
//			e.printStackTrace(new PrintWriter(errors));
//			logger.info("selectSingleString exception!!! sql=[" + sql + "]\n" + errors.toString());
			logger.info("/* selectSingleStringNoException exception!!! sql=[" + sql + "] */");
		}
		return singleString;
	}

	@Override //TODO yaron rename selectSingleLogException and make both DEBUG
	public String selectSingleStringNoException(String sql) {
		logger.info("/* SQL selectSingleStringNoException sql=*/ " + sql);
		String singleString = "";
		try {
			Object o = (String) jdbcTemplate.queryForObject(sql, String.class);
			singleString = (String) o;

		} catch (Exception e) {
			logger.info("/*  selectSingleStringNoException exception!!! sql=[" + sql + "] */");
			generalUtilLogger.logWrite(LevelType.DEBUG,
					"Warn! (selectSingleStringNoException) sql=" + sql + " error in sql!", "", ActivitylogType.SQLError,
					null, e);
		}
		return singleString;
	}

	@Override
	public String getSingleStringFromClob(String sql) {
		logger.info("/* SQL getSingleStringFromClob sql=*/ " + sql);
		String singleString = "";
		try {
			Object obj = jdbcTemplate.queryForObject(sql, Clob.class);
			if (obj instanceof Clob) {
				Clob clob = (Clob) obj;
				singleString = clob.getSubString(1, (int) clob.length());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			generalUtilLogger.logWrite(LevelType.ERROR, "SQL=[" + sql + "] error in sql!", "", ActivitylogType.SQLError,
					null, e);
			singleString = "";
		}
		return singleString;
	}

	@Override
	public String getSingleStringFromBlob(String sql) {
		logger.info("/* SQL getSingleStringFromClob sql=*/ " + sql);
		String singleString = "";
		try {
			Object obj = jdbcTemplate.queryForObject(sql, Blob.class);
			if (obj instanceof Blob) {
				Blob blob = (Blob) obj;
				byte[] bVal = blob.getBytes(1, (int) blob.length());
				singleString = new String(bVal, "UTF8");
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			generalUtilLogger.logWrite(LevelType.ERROR, "SQL=[" + sql + "] error in sql!", "", ActivitylogType.SQLError,
					null, e);
			singleString = "";
			e.printStackTrace();
		}
		return singleString;
	}

	@Override
	public InputStream getInputStreamFromBlob(String sql) {
		logger.info("/* SQL getInputStreamFromBlob sql=*/ " + sql);
		InputStream blobStream = null;
		try {
			Object obj = jdbcTemplate.queryForObject(sql, Blob.class);
			if (obj instanceof Blob) {
				Blob blob = (Blob) obj;
				blobStream = blob.getBinaryStream();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			generalUtilLogger.logWrite(LevelType.ERROR, "SQL=[" + sql + "] error in sql!", "", ActivitylogType.SQLError,
					null, e);
			e.printStackTrace();
		}
		return blobStream;
	}

	@Override
	public byte[] getBytesFromBlob(String sql) {
		logger.info("/* SQL getBytesFromBlob sql=*/ " + sql);
		InputStream blobStream = null;
		try {
			Object obj = jdbcTemplate.queryForObject(sql, Blob.class);
			if (obj instanceof Blob) {
				Blob blob = (Blob) obj;
				blobStream = blob.getBinaryStream();
				int len = (int) blob.length(); //read as long	    
				long pos = 1; //indexing starts from 1
				byte[] bytes = blob.getBytes(pos, len);
				blobStream.close();
				return bytes;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			generalUtilLogger.logWrite(LevelType.ERROR, "SQL=[" + sql + "] error in sql!", "", ActivitylogType.SQLError,
					null, e);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getSingleStringFromClobNoException(String sql) {
		logger.info("/* SQL getSingleStringFromClob sql=*/ " + sql);
		String singleString = "";
		try {
			Object obj = jdbcTemplate.queryForObject(sql, Clob.class);
			if (obj instanceof Clob) {
				Clob clob = (Clob) obj;
				singleString = clob.getSubString(1, (int) clob.length());
			}

		} catch (Exception e) {
			// Do Nothing
		}
		return singleString;
	}

	@Override
	public JSONObject getJSONObjectForDTBySql(String sql, List<String> dateList) {
		logger.info("/* SQL getJSONObjectForDTBySql sql=*/ " + sql + ", dateList=" + dateList); //TODO dateList
		JSONObject json = null;
		String mapValue;
		List<List<String>> toReturn = new ArrayList<List<String>>();
		List<String> rowList;
		try {
			List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
			for (Map<String, Object> row : rows) {
				rowList = new ArrayList<String>();
				for (Entry<String, Object> entry : row.entrySet()) {
					if (entry.getValue() == null) {
						mapValue = "";
					} else {
						mapValue = entry.getValue().toString();
					}
					for (String dateCol : dateList) {
						if (entry.getKey().equals(dateCol)) {
							SimpleDateFormat fromUser = new SimpleDateFormat(
									generalUtil.getSelectDateQueryDateFormat());
							SimpleDateFormat myFormat = new SimpleDateFormat(generalUtil.getUserDateFormatServer());
							try {
								mapValue = myFormat.format(fromUser.parse(entry.getValue().toString()));
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;
						}
					}
					rowList.add(mapValue);
				}
				toReturn.add(rowList);
			}
			JSONArray jsonDataList = new JSONArray(toReturn);
			JSONArray jsonColumnsList = new JSONArray();
			if (rows != null && !rows.isEmpty()) {
				Set<String> colSet = rows.get(0).keySet();
				for (String col : colSet) {
					json = new JSONObject();
					json.put("showPageFilter", "0");
					json.put("title", col);
					jsonColumnsList.put(json);
				}
				json = new JSONObject();
				json.put("columns", jsonColumnsList);
				json.put("data", jsonDataList);
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(LevelType.ERROR, "sql=*/ [" + sql + "] error in sql!", "", ActivitylogType.SQLError,
					null, e);
		}
		return json;
	}

	@Override
	public Map<String, String> sqlToHashMap(String sql) {
		logger.info("/* SQL sqlToHashMap sql=*/ " + sql); //TODO dateList
//		String numberOfRows = selectSingleString("select count(*) from ( " + sql + " ) "); // yp 16092020 - use getMetaDataMultiRowValues (instead of getMetaDataRowValues) to reduce sql calls (improve performance - this sql count not needed because getMetaDataMultiRowValues covers both cases) 
		Map<String, String> returnMap = new HashMap<String, String>();
//		if (generalUtil.getNullInt(numberOfRows, 0) <= 1) {
			returnMap.putAll(getMetaDataMultiRowValues(sql));
//		} else {
//			//List<DataBean> toReturn = new ArrayList<DataBean>();
//			Map<String, String> metaMap = getMetaData("select * from ( " + sql + " ) where 1=2 ");
//			for (Map.Entry<String, String> entry : metaMap.entrySet()) {
//				returnMap.put(entry.getKey(),
//						getCSVBySql("select distinct " + entry.getKey() + " from ( " + sql + " ) ", false));
//			}
//		}
		return returnMap;
	}
	
	private Map<String, String> getMetaDataMultiRowValues(String sql) {
		logger.info("/* SQL getMetaDataMultiRowValues sql=*/ " + sql);
		Map<String, String> returnMap;
		try {
			returnMap = jdbcTemplate.query(sql, new ResultSetExtractor<Map<String, String>>() {
				@Override
				public Map<String, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
					Map<String,  List<String>> MetaDataValMap = new HashMap<String, List<String>>();
					Map<String, String> MetaDataValMapReturn = new HashMap<String, String>();
					ResultSetMetaData rsmd = rs.getMetaData();
					int columnCount = rsmd.getColumnCount();
					int rowCount = 0;
					while (rs.next()) {
						for (int i = 1; i <= columnCount; i++) {
							String colName_ = rsmd.getColumnName(i);
							String val_ = generalUtil.getNull(rs.getString(i));
							String valNullString_ = generalUtil.getNull(rs.getString(i),"null"); //yp 16092020 - use "null" to make the same value in the map as it was before using this function (and using getCSVBySql)
							if(rowCount == 0) {
								MetaDataValMap.put(colName_, new ArrayList<String>(Arrays.asList(valNullString_)));
								MetaDataValMapReturn.put(colName_, val_);
							} else {
								if(!MetaDataValMap.get(colName_).contains(valNullString_)) {
									MetaDataValMap.get(colName_).add(valNullString_);
								}
							}
						}
						rowCount++;
					}
					if(rowCount > 1) {
						for (Map.Entry<String, List<String>> entry : MetaDataValMap.entrySet()) {
							String csvVal_ = generalUtil.replaceLast(entry.getValue().toString().replaceFirst("\\[", ""), "]", "").replace(", ", ",");
							MetaDataValMapReturn.put(entry.getKey(),csvVal_);
						}
					}
					return MetaDataValMapReturn;
				}
			});
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			returnMap = null;
			e.printStackTrace();
		}
		return returnMap;
	}

	@Override
	public List<String> getListOfStringBySql(String sql) {
		List<String> o;
		try {
			logger.info("/* SQL getListOfStringBySql sql=*/ " + sql);
			o = jdbcTemplate.queryForList(sql, String.class);
		} catch (Exception e) {
			generalUtilLogger.logWrite(LevelType.ERROR, "SQL=[" + sql + "] error in sql!", "", ActivitylogType.SQLError,
					null, e);
			o = null;
			e.printStackTrace();
		}
		return o;
	}

	@Override
	public JSONObject getJSONObjectOfDateTableServer(final DataTableParamModel param, String sql) {
		logger.info("/* SQL getJSONObjectOfDateTableServer sql=*/ " + sql);
		JSONObject jsonResponse = new JSONObject();
		List<JSONArray> data = new LinkedList<JSONArray>();
		JSONArray rowData = null;
		List<Map<String, Object>> rows;
		List<String> dateList;
		boolean isDateExists;
		try {
			rows = jdbcTemplate.queryForList(sql);
			dateList = getDateListFromMetaData(getMetaData(sql));

			isDateExists = (dateList.isEmpty()) ? false : true;

			for (Map<String, Object> row : rows) {
				rowData = new JSONArray();
				for (Map.Entry<String, Object> entry : row.entrySet()) {
					String value = (entry.getValue() == null) ? "" : entry.getValue().toString();
					if ((isDateExists) && (!value.equals(""))) {
						for (String date : dateList) {
							if (entry.getKey().equals(date)) {
								SimpleDateFormat fromUser = new SimpleDateFormat(
										generalUtil.getSelectDateQueryDateFormat());
								SimpleDateFormat myFormat = new SimpleDateFormat(generalUtil.getUserDateFormatServer());
								value = myFormat.format(fromUser.parse(value));
								break;
							}
						}
					}
					rowData.put(value);
				}
				data.add(rowData);
			}
			jsonResponse = generalUtil.getTableModel(data, param);
		} catch (DataAccessException e1) {
			// TODO Auto-generated catch block
			generalUtilLogger.logWrite(LevelType.ERROR, "SQL=[" + sql + "] error in sql!", "", ActivitylogType.SQLError,
					null, e1);
			e1.printStackTrace();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			generalUtilLogger.logWrite(LevelType.ERROR, "SQL=[" + sql + "] error in sql!", "", ActivitylogType.SQLError,
					null, e1);
			e1.printStackTrace();
		} catch (Exception e) {
			generalUtilLogger.logWrite(LevelType.ERROR, "SQL=[" + sql + "] error in sql!", "", ActivitylogType.SQLError,
					null, e);
			e.printStackTrace();
		}

		return jsonResponse;
	}

	@Override
	public JSONObject getJSONObjectOfDateTable(final String sql, String hideEmptyColumns, String topRowsNum, StringBuilder sqlInfoSb, String ... args) {
//		logger.info("/* SQL getJSONObjectOfDateTable sql=*/ " + sql);
		//		if(sql.contains("select * from fg_s_SAMPLE_DTMAIN_v where 1=1")) {
		//			sql = "select * from fg_s_yaron_pivot_v ";
		//		}

		String dataTableTopRowsNum = dataTableTopRowsNumDefault;
		if (topRowsNum != null && !topRowsNum.equals("-1")) {
			dataTableTopRowsNum = topRowsNum;
		}
		JSONObject toReturn = new JSONObject();
		List<String> listOfMetaData;
		JSONArray jsonArrayOfColumns;
		
		boolean paramColFlag = false;
		try {
			StringBuilder internalQuery = new StringBuilder();
			
			//***** crete replace maps *******
			//***** action tables
			Map <String,String>actionRequestIconMap = null;
			Map<String, List<String>> actionSampleMap = null;
			List<String> experimentSampleList = new ArrayList<>();
			
			if(sql.toLowerCase().contains("select * from fg_s_action_dte_v")
					||sql.toLowerCase().contains("select * from fg_s_action_dtfe_v")
					||sql.toLowerCase().contains("select * from fg_s_action_dten_v")) {
				actionRequestIconMap = new HashMap<String,String>();
			//call function that create hash map - key actionid , value the json needed by the user (that will replace @<action_id>@) in later loop..
				//select * from fg_s_Action_DTE_v where STEP_ID='79098' 
				String step_id = sql.replaceAll(".*STEP_ID=", "").replace("'", "").trim();
				actionRequestIconMap = getActionRequestIconMap(step_id);
				experimentSampleList = expSampleList(step_id, internalQuery);
				actionSampleMap = actSampleList(step_id, internalQuery); 
			}
			
			//***** Experiment report rules display table (fg_s_ReportFilterRef_DTE_v)
			List<String> stepNameList = new ArrayList<>();
			if(sql.toLowerCase().contains("fg_s_reportfilterref_dte_v")) {
				paramColFlag = true;
				if(args.length > 0){
					String stepNameCSV = args[0];
					if(stepNameCSV != null && !stepNameCSV.isEmpty()) {
						stepNameList = getListOfStringBySql("select distinct stepname from fg_s_step_v where step_id in (" + stepNameCSV + ") order by stepname");
					}
				}
			}
			
			
			List<Map<String, Object>> rows = getListOfMapsForDataTableBySql(sql);
			
			/*  ab 12032019 Patch for display columns with metadata when rows is empty */
			//TODO: change it.  Note: to ElementDataTableApiImp added to dataTableOptions new option - displayColumnsWhenNoRows
			boolean displayEmptyTable = false;
			String newsql = "";
			JSONArray jsonArrayOfData = new JSONArray();
			if ((rows == null) || (rows.isEmpty())) {
				if (sql.toLowerCase().contains("fg_s_materialref_dtereac_v"))// REACTANTS PLANNED
				{
					String stepId = "";
					String experimentId = "";
					if(args.length > 1){
						stepId = "";
						experimentId = args[1];
						if(args[1].contains(",")){
							stepId = args[1].split(",")[1];
							experimentId = args[1].split(",")[0];
						}
					}
					displayEmptyTable = true;
					newsql = "select * from fg_s_materialref_dten_react_v where parentid = '"+ generalUtil.getEmpty(stepId, "-1") +"' and experiment_id= '"+ generalUtil.getEmpty(experimentId, "-1") +"'";

				} else if (sql.toLowerCase().contains("fg_s_materialref_dtereac_act_v"))// REACTANTS ACTUAL
				{
					String stepId = "";
					if(args.length > 1){
						stepId = args[1];
					}
					displayEmptyTable = true;
					newsql = "select * from fg_s_matref_dten_reac_act_v where parentid = '"+ generalUtil.getEmpty(stepId, "-1") +"'";

				} else if (sql.toLowerCase().contains("fg_s_materialref_dtesolv_v"))// SOLVENTS PLANNED
				{
					String stepId = "";
					String experimentId = "";
					if(args.length > 1){
						stepId = "";
						experimentId = args[1];
						if(args[1].contains(",")){
							stepId = args[1].split(",")[1];
							experimentId = args[1].split(",")[0];
						}
					}
					displayEmptyTable = true;
					newsql = "select * from fg_s_materialref_dten_solv_v t where parentid = '"+ generalUtil.getEmpty(stepId, "-1") +"' and experiment_id= '"+ generalUtil.getEmpty(experimentId, "-1") +"'";

				} else if (sql.toLowerCase().contains("fg_s_materialref_dtesolv_act_v")) //SOLVENTS ACTUAL
				{
					String stepId = "";
					if(args.length > 1){
						stepId = args[1];
					}
					displayEmptyTable = true;
					newsql = "select * from fg_s_matref_dten_solv_act_v t where parentid = '"+ generalUtil.getEmpty(stepId, "-1") +"'";

				} else if (sql.toLowerCase().contains("fg_s_materialref_dteprod_v"))// PRODUCTS
				{
					if(args.length > 1){
						String stepId = "";
						String experimentId = args[1];
						if(args[1].contains(",")){
							stepId = args[1].split(",")[1];
						    experimentId = args[1].split(",")[0];
						}
						newsql = "select * from fg_s_materialref_dten_prod_v where experiment_id = '"+experimentId+"' and parentid = '"+ generalUtil.getEmpty(stepId, "-1") +"'";
					}else{
						newsql = "select * from fg_s_materialref_dten_prod_v where rownum<=1";
					}
					displayEmptyTable = true;

				}
				if (!newsql.equals("")) {
					if(sqlInfoSb != null){
						sqlInfoSb.append("</br></br>new sql (display columns with metadata when rows is empty): "+newsql); 
					}
					try {
						rows = getListOfMapsForDataTableBySql(newsql);
					} catch (Exception e) {
						generalUtilLogger.logWrite(e);
						e.printStackTrace();
					}
				}
			}
			/* ******************************************************************************************************/
						
			List<String> colAfterParm = new ArrayList<String>();//12122018 ab: used for "_SMARTMONPARAM" for now

			//12122018 ab: paramCols changed to Map object, because there may be more than one special SMARTs in the table
			Map<String, List<String>> paramColMap = getSmartsCol(rows, paramColFlag);

			for (Map.Entry<String, List<String>> smartsEntry : paramColMap.entrySet()) {
				String paramCol = smartsEntry.getKey();
				colAfterParm = smartsEntry.getValue();
				/******** SMARTSPLIT handler *****************************************************************/
				if (!paramCol.isEmpty() && paramCol.endsWith("_SMARTSPLIT")) {
					JSONArray maxRowColumnArray = null;
					JSONArray currentRowColumnArray = null;
					JSONArray valArray = null;
					
					//get maxRowColumnArray (requires extra loop:()
					for (int i = 0; i < rows.size(); i++) {
						try {
							String objVal = (String) rows.get(i).get(paramCol);
							JSONObject jo_ = new JSONObject(objVal);
							currentRowColumnArray = jo_.getJSONArray("column");
							if (maxRowColumnArray == null || maxRowColumnArray.length() < currentRowColumnArray.length()) {
								maxRowColumnArray = jo_.getJSONArray("column");
							}
						} catch (Exception e) {
							// do nothing
						}
					}
					
					if(maxRowColumnArray != null) { // if there is at least one record with data
						//split the objects to columns according maxRowColumnArray, if we have an object with array size smaller then maxRowColumnArray size we complete the values according to the valDefault data (or empty if we missed valDefault in the sql)
						for (int i = 0; i < rows.size(); i++) {
							Map<String, Object> currRow = rows.get(i);
							try {
								valArray = null;
								String objVal = (String) rows.get(i).get(paramCol);
								if(objVal != null) {
									JSONObject jo_ = new JSONObject(objVal);
									if(jo_.has("val")) {
										valArray = jo_.getJSONArray("val");
									} else if(jo_.has("valDefault")) {
										valArray = jo_.getJSONArray("valDefault");
									}
								}
							} catch (Exception e) {
//								System.out.println("smartsplit exception e=" + e.toString());
							}

							if (maxRowColumnArray != null && maxRowColumnArray.length() > 0) {
								// remove the pivot column
								rows.get(i).remove(paramCol);
								// put data from maxRowColumnArray and values arrays
								for (int m = 0; m < maxRowColumnArray.length(); m++) {
									String colSingle_ = maxRowColumnArray.getString(m);
									String valSingle_ = (valArray != null && m < valArray.length()) ? valArray.getString(m) : "";
									rows.get(i).put(colSingle_, valSingle_);
								}
							}

							// move colAfterParm after the paramCol (their original position in the sql)
							if (maxRowColumnArray != null && maxRowColumnArray.length() > 0) {
								for (String col_ : colAfterParm) {
									Object o_ = currRow.get(col_);
									currRow.remove(col_);
									currRow.put(col_, o_);
								}
							}
						}
					}
				}
				/********Param Monitoring handler *****************************************************************/
				else if (!paramCol.isEmpty() && paramCol.endsWith("MONPARAM")) {
					List<String> paramsArray = new ArrayList<String>();
					JSONArray paramMonitoringNamesArray;
					JSONObject paramMonitoringNameJSONObject, tempObject, tempUomObject;
					String paramMonitoringValue;
					boolean flag;

					for (int i = 0; i < rows.size(); i++) {
						String currRowMPVal = (rows.get(i).get(paramCol) == null) ? "{}"
								: rows.get(i).get(paramCol).toString();
						paramMonitoringNameJSONObject = new JSONObject(currRowMPVal);
						paramMonitoringNamesArray = paramMonitoringNameJSONObject.names(); // get jsonObject keys as array
						if (paramMonitoringNamesArray == null) {
							continue;
						}
						for (int j = 0; j < paramMonitoringNamesArray.length(); j++) {
							paramMonitoringValue = paramMonitoringNamesArray.get(j).toString(); // paramMon current name/key
							if (paramMonitoringValue.contains("_uom")) {
								continue;
							}
							tempObject = new JSONObject(
									paramMonitoringNameJSONObject.get(paramMonitoringValue).toString()); //paramMon value of current name/key
							if (tempObject.get("val").equals("")) {
								continue;
							}

							//kd 22032018 this check remove Parameter from table Monitoring Parameters of workup if parameter was delete from Parameters in Maintenance 	
							if (generalUtilForm.getCurrrentNameInfoAllContainsId("MP", tempObject.getString("formid"))
									.size() > 0) {
								if (generalUtilForm
										.getCurrrentNameInfoAllContainsId("MP", tempObject.getString("formid")).get(0)
										.get("ID") == null /*(sequence != null) && !sequence.contains(tempObject.getString("formid"))*/) {
									continue;
								}
							} else {
								continue;
							}

							flag = true;
							// prevent from duplicates
							for (int k = 0; k < paramsArray.size(); k++) {
								if (paramsArray.get(k).equals(paramMonitoringNamesArray.get(j))) {
									flag = false;
									break;
								}
							}
							if (flag) {
								paramsArray.add(paramMonitoringNamesArray.get(j).toString()); // add parameters to display as column names
							}
						}
					}
					if (paramsArray.size() > 0) {
						//sort parameter names alphabetically
						Collections.sort(paramsArray, new Comparator<String>() {
							@Override
							public int compare(String s1, String s2) {
								return s1.toLowerCase().compareTo(s2.toLowerCase());
							}
						});
					}

					for (int i = 0; i < rows.size(); i++) {
						Map<String, Object> currRow = rows.get(i);
						String currRowMPVal = (currRow.get(paramCol) == null) ? "{}" : currRow.get(paramCol).toString();
						paramMonitoringNameJSONObject = new JSONObject(currRowMPVal);
						currRow.remove(paramCol); // remove '_SMARTMONPARAM' column from current row
						for (int j = 0; j < paramsArray.size(); j++) {
							String currColName = paramsArray.get(j);
							String currColNameUOM = "{" + currColName + "}_uom"; //add unique part to UOM column 
							try {
								// 13122018 ab: used 'optString' instead 'getString' to display empty value in case there isn't such parameter in current row (avoid from falling into a catch())
								tempObject = new JSONObject(paramMonitoringNameJSONObject.optString(currColName, "{}")); // value of current key/name
								tempUomObject = new JSONObject(
										paramMonitoringNameJSONObject.optString(currColName + "_uom", "{}")); // uom value of current key/name
								// param value
								String currValue = tempObject.optString("val");
								// param id
								String currValueId = tempObject.optString("formid");
								// param UOM id
								String currUOMVal = tempUomObject.optString("val");
								// param UOM value
								String currUOMText = tempUomObject.optString("text");

								//define SMARTMONPARAM as editable cells
								if (paramCol.equals("MPE_SMARTMONPARAM")) {
									if (currRow.containsKey("MPFORMID")) {
										String currMPFormID = (currRow.get("MPFORMID") == null) ? ""
												: currRow.get("MPFORMID").toString();
										if (!currMPFormID.equals("")) {
											JSONObject editableTextObj = new JSONObject();
											JSONObject editableUOMObj = new JSONObject();
											editableTextObj.put("displayName", currValue);
											editableTextObj.put("mp_formid", currMPFormID);
											editableTextObj.put("mp_name", currColName);
											editableTextObj.put("is_uom", "0");
											editableTextObj.put("formNumberID", currRow.get("FORMNUMBERID"));
											editableTextObj.put("htmlType", "text");
											editableTextObj.put("autoSave", "true");
											editableTextObj.put("saveType", "monitoringParams");

											JSONObject selUomObj = new JSONObject();
											StringBuilder defaultSelUomStr = new StringBuilder();
											JSONArray uomFullList = getMPUOMArr(currValueId, currColName,
													defaultSelUomStr);
											if (!currUOMVal.equals("") && !currUOMText.equals("")) {
												selUomObj.put("ID", currUOMVal);
												selUomObj.put("displayName", currUOMText);
											} else {
												try {
													JSONObject defaultSelUomObj = new JSONObject(
															defaultSelUomStr.toString());
													selUomObj.put("ID", defaultSelUomObj.optString("ID"));
													selUomObj.put("displayName", defaultSelUomObj.optString("VAL"));
												} catch (Exception e) {
													selUomObj.put("ID", uomFullList.optJSONObject(0).optString("ID"));
													selUomObj.put("displayName",
															uomFullList.optJSONObject(0).optString("VAL"));
													generalUtilLogger.logWrite(LevelType.WARN,
															"SQL=[" + sql + "] parse defaultSelUomStr="
																	+ defaultSelUomStr,
															"", ActivitylogType.SQLError, null, e);
												}
											}

											editableUOMObj.put("displayName", new JSONArray().put(selUomObj));
											//editableUOMObj.put("defaultUOMVal", currUOMVal);
											editableUOMObj.put("mp_formid", currMPFormID);
											editableUOMObj.put("mp_name", currColName);
											editableUOMObj.put("is_uom", "1");
											editableUOMObj.put("fullList", uomFullList);
											editableUOMObj.put("htmlType", "select");
											editableUOMObj.put("autoSave", "true");
											editableUOMObj.put("saveType", "monitoringParams");
											editableUOMObj.put("formNumberID", currRow.get("FORMNUMBERID"));

											currRow.put(currColName + "_SMARTEDIT", editableTextObj.toString());
											currRow.put(currColNameUOM + "_SMARTEDIT", editableUOMObj.toString());
										} else {
											currRow.put(currColName + "_SMARTEDIT", "");
											currRow.put(currColNameUOM + "_SMARTEDIT", "");
										}
									} else {
										currRow.put(currColName, currValue);
										currRow.put(currColNameUOM, ((currValue.equals("")) ? "" : currUOMText));
										generalUtilLogger.logWriter(LevelType.ERROR,
												"Column 'MPFORMID' is not defined in sql view",
												ActivitylogType.GeneralError, "-1");
									}
								} else {
									currRow.put(currColName, currValue);
									currRow.put(currColNameUOM, ((currValue.equals("")) ? "" : currUOMText));
								}
							} catch (Exception e) {
								//kd 22032018 added this catch to avoid show data in wrong column (avoid shifting data left in the row)
								currRow.put(currColName, "");
								currRow.put(currColName + "_uom", "");
								System.out.println("Failed " + e);
								generalUtilLogger.logWrite(e);
							}
						}

						if (paramsArray.size() > 0) // check if there are parameters to display
						{
							// move colAfterParm after the paramCol (their original position in the sql) yp 02052018
							for (String col_ : colAfterParm) {
								Object o_ = currRow.get(col_);
								currRow.remove(col_);
								currRow.put(col_, o_);
							}
						}
					}

				}
				/**************************************************************************************************/

				/******** DyanamicParams handler *****************************************************************/

				else if (!paramCol.isEmpty() && paramCol.endsWith("DYNPARAM")) {
					JSONObject mobilePhaseComposNameJSONObject, jo;
					String val, uom, parentId = rows.get(0).get("PARENTID").toString();
					List<Map<String, Object>> lastDynamicParamsListOfMaps = getListOfMapsBySql(
							"select * from FG_DYNAMICPARAMS t where PARENT_ID = '" + parentId + "' order by ORDER_");
					internalQuery.append(" - DYNPARAM: select * from FG_DYNAMICPARAMS t where PARENT_ID = '" + parentId + "' order by ORDER_</br>");
					Map<String, String> columnsMap = new HashMap<String, String>();
					for (int i = 0; i < lastDynamicParamsListOfMaps.size(); i++) {
						if (lastDynamicParamsListOfMaps.get(i).get("ACTIVE").toString().equals("1")) {
							columnsMap.put(lastDynamicParamsListOfMaps.get(i).get("ORDER_").toString(),
									lastDynamicParamsListOfMaps.get(i).get("LABEL").toString());
						}
					}
					for (int i = 0; i < rows.size(); i++) {
						mobilePhaseComposNameJSONObject = new JSONObject(rows.get(i).get(paramCol).toString());
						rows.get(i).remove(paramCol);
						for (Map.Entry<String, String> entry : columnsMap.entrySet()) {
							if (mobilePhaseComposNameJSONObject.has(String.valueOf(entry.getKey()))) {
								jo = mobilePhaseComposNameJSONObject.getJSONObject(entry.getKey());
								val = jo.getString("val");
								uom = jo.getString("uom");
							} else {
								val = "";
								uom = "";
							}
							if (entry.getKey().equals("0")) {
								rows.get(i).put(entry.getValue(), generalUtilCalc.getNumberRoundedDispaly(val, "3"));
								rows.get(i).put("UOM", generalUtilForm.getCurrrentIdInfo(uom).get("NAME"));
							} else {
								rows.get(i).put(entry.getValue(),
										(val.equals("")) ? ""
												: generalUtilCalc.getNumberRoundedDispaly(val, "3") + " ["
														+ generalUtilForm.getCurrrentIdInfo(uom).get("NAME") + "]");
							}
						}
					}

					//sort by Time Point
					Collections.sort(rows, new Comparator<Map<String, Object>>() {
						public int compare(final Map<String, Object> o1, final Map<String, Object> o2) {
							int compReturn = 0;
							try {
								compReturn = Double.valueOf(o1.get("Time Point").toString())
										.compareTo(Double.valueOf(o2.get("Time Point").toString()));
							} catch (Exception e) {
								generalUtilLogger.logWrite(e);
								e.printStackTrace();
							}
							return compReturn;
						}
					});

				}

				/******** SMARTPIVOT / SMARTPIVOTSQL handler *****************************************************************/
				else if (!paramCol.isEmpty()
						&& (paramCol.endsWith("SMARTPIVOT") || paramCol.endsWith("SMARTPIVOTSQL"))) {
					long start = System.currentTimeMillis();
					
					List<String> columnNames = new ArrayList<>();
					Map<String, JSONArray> pivotDataMap = new HashMap<String, JSONArray>();
					String smartPivotSql = "";
					String smartPivotSqlIdName = "";
					boolean prepareFailureFlag = false;

					try {
						// prepare data and columns
						List<String> objListColTmp = null;
						List<String> objListCol = null;
						if (paramCol.endsWith("SMARTPIVOTSQL")) { // SMARTPIVOTSQL the items are in the SQL (the column order is made in the SQL)
																		// Example: select result_SMARTPIVOT from fg_p_action_result_v where step_id = 117945
																	// contains all the pivot item under this SQL scope with the same format as SMARTPIVOT
																	// the column order by is inside the SQL (in this example inside fg_p_action_result_v view)
																	// duplicate values (more then one value under the sane column nmae in the same row) will be represent under additional column <column name>.<index>
																	// get the sql from the first row (the SQL is the same in every row)
							if (rows.get(0).get(paramCol) != null) {
								smartPivotSql = rows.get(0).get(paramCol).toString();
//								System.out.println("SMARTPIVOTSQL=" + smartPivotSql);
								
								internalQuery.append(" - pivot sql: "+smartPivotSql+"</br>");
								
								//init lists
								if (objListColTmp == null) {
									objListCol = new ArrayList<String>();
									objListColTmp = getListOfStringBySql(smartPivotSql);

									for (int k = 0; k < objListColTmp.size(); k++) {

										JSONObject jo_ = new JSONObject(objListColTmp.get(k).replaceAll("\\n", "<br>"));
										try {
											JSONArray colArray = jo_.getJSONArray("column");
											JSONArray valArray = jo_.getJSONArray("val");
											JSONArray groupArray =  null;
											try {
												groupArray = jo_.getJSONArray("group");
											} catch (Exception e) {
											}

											for (int m = 0; m < colArray.length(); m++) {
												JSONObject joSingle_ = new JSONObject(objListColTmp.get(k).replaceAll("\\n", "<br>"));
												String colSingle_ = colArray.getString(m);
												String valSingle_ = valArray.getString(m);
												String valGroup_ = "";
												if (groupArray != null) {
													valGroup_ = (String) groupArray.get(m);
												}
												joSingle_.put("column", colSingle_);
												joSingle_.put("val", valSingle_);
												if (groupArray != null) {
													joSingle_.put("group", valGroup_);
												}
												objListCol.add(joSingle_.toString());
											}
										} catch (Exception e) {
											objListCol.add(objListColTmp.get(k).replaceAll("\\n", "<br>"));
										}
									}
								}

								for (int k = 0; k < objListCol.size(); k++) {
									JSONArray ja_ = null;//replace!!!!!!
									JSONObject jo_ = new JSONObject(objListCol.get(k).replaceAll("\\n", "<br>"));
									String id_ = (String) jo_.get("pivotkey");
									String name_ = (String) jo_.get("column");
									String group_ = "@PIVOT_GROUP@";
									if (jo_.has("group")) {
										group_ = (String) jo_.get("group") + group_;
									}
									name_ = group_ + name_;
									jo_.put("column", name_);
									jo_.put("origin_column", name_);
									if (smartPivotSqlIdName.equals("")) {
										smartPivotSqlIdName = (String) jo_.get("pivotkeyname");
									}
									if (!pivotDataMap.containsKey(id_)) {
										ja_ = new JSONArray();
										if (!columnNames.contains(name_)) { // first item in the pivot row - we check only if the column name is not in columnNames list from previous rows
											columnNames.add(name_);
										}
									} else {
										ja_ = pivotDataMap.get(id_);
										if (!columnNames.contains(name_)) {
											columnNames.add(name_);
										} else { // handle duplication using evalPivotColumnName
											name_ = evalPivotColumnName(name_, ja_);
											jo_.put("column", name_);
											if (!columnNames.contains(name_)) {
												columnNames.add(name_);
											}
										}
									}
									ja_.put(jo_);
									pivotDataMap.put(id_, ja_);
								}
							}
						}
					} catch (Exception e) {
						prepareFailureFlag = true;
						generalUtilLogger.logWrite(LevelType.ERROR, "Error in DataTable pivot data perparation!", "-1",
								ActivitylogType.SQLError, null, e);
					}

					// Handle data (if no prepareFailureFlag)
					if (!prepareFailureFlag) {
						try {
							for (int i = 0; i < rows.size(); i++) {
								// remove the pivot column
								rows.get(i).remove(paramCol);
								if (columnNames == null || columnNames.isEmpty()) {
									// do nothing
								} else {
									String id_ = null;
									JSONArray ja_ = null;
									if (!generalUtil.getNull(smartPivotSqlIdName).trim().equals("")) {
										id_ = rows.get(i).get(smartPivotSqlIdName).toString();
										ja_ = pivotDataMap.get(id_);
									}
									
									////adib reducing runtime-added the following code///
									for (String col : columnNames) {
										if (!rows.get(i).containsKey(col)) {
											rows.get(i).put(col, "");
										}
									}
									//////////////////////////
									if (ja_ != null) {
										for (int k = 0; k < ja_.length(); k++) {

											JSONObject o_ = ja_.getJSONObject(k);

											String val = ((JSONObject) o_).getString("val");
											String column = ((JSONObject) o_).getString("column");
											
											////adib reduce the runtime-added the following code///
											if(columnNames.contains(column)){
												rows.get(i).put(column, val);
											}
											/////////////////////////////
											
										////adib reducing runtime-removed the following code///
											/*for (String col : columnNames) {
												if (col.equals(column)) {
													rows.get(i).put(col, val);
												} else if (!rows.get(i).containsKey(col)) {
													rows.get(i).put(col, "");
												}
											}*/
										}
									} else {
									////adib reducing runtime-removed the following code///
										/*for (String col : columnNames) {
											rows.get(i).put(col, "");
										}*/
									}

								}

								// 12122018 ab: prevent from relocate pivot, must be the latest column in the table
								// move colAfterParm after the paramCol (their original position in the sql)
								/*for (String col_ : colAfterParm) {
								Object o_ = rows.get(i).get(col_);
								rows.get(i).remove(col_);
								rows.get(i).put(col_, o_);
								}*/
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							generalUtilLogger.logWrite(LevelType.ERROR, "Error in DataTable pivot data!", "-1",
									ActivitylogType.SQLError, null, e);
							return getJSONObjectOfDateTable(
									jsonSqlErrorMsg("Error in data table pivot SQL! [" + sql + "]"), "", "-1",null);
						}
					}
					long time = System.currentTimeMillis() - start;
					Map<String,String> map = new HashMap<>();
					map.put("TIME_PIVOT_PARSE", String.valueOf(time));
					generalUtilLogger.logWrite(LevelType.DEBUG,
							" Parsering SMARTPIVOTSQL=" + smartPivotSql, "-1",
							ActivitylogType.PerformanceJava, map);
				}
				/******** SMARTPATH handler *****************************************************************/
				else if (!paramCol.isEmpty() && paramCol.endsWith("SMARTPATH")) {
					String delimiter = ">";
					String smartPath = "";
					ArrayList<Integer> al = new ArrayList<>();//kd 14042019 fixed bug-7395 Use this var for identify rows which has wrong path and shoild't be show in search result
					for (int i = 0; i < rows.size(); i++) {
						try {
							if (rows.get(i).get(paramCol) != null) {
								smartPath = rows.get(i).get(paramCol).toString();
								boolean flag = true; // kd 12042019 This flag
														// show if path not
														// right
								JSONObject json = new JSONObject(smartPath);
								// JSONArray jarr = new JSONArray();
								JSONArray pathList = json.getJSONArray("path");
								/*
								 * String formC =
								 * generalUtil.getJsonValById(pathList.get(0).
								 * toString(), "name").split(":")[0]; int j=0;
								 * if (formC.equals("Project")){ j=1; }
								 */
								List<String> pathSmartLink = new ArrayList<String>();
								for (int j = 0; j < pathList.length(); j++) {
									String p = pathList.get(j).toString();
									String id = generalUtil.getNull(generalUtil.getJsonValById(p, "id"));
									if (id.equals("")) {
										continue;
									}
									String checkName = generalUtil.getNull(generalUtil.getJsonValById(p, "name")
											.substring(generalUtil.getJsonValById(p, "name").indexOf(":") + 1));
									if (checkName.equals("")) {
										flag = false;
										continue;
									}
									String name = generalUtil.getJsonValById(p, "name").split(":")[1];
									String formCode = generalUtil.getJsonValById(p, "name").split(":")[0];
									pathSmartLink.add("{\"displayName\":\"" + name
											+ "\",\"icon\":\"\",\"fileId\":\"\",\"formCode\":\"" + formCode
											+ "\",\"formId\":\"" + id + "\",\"tab\":\"\" ,\"delimiter\":\"" + delimiter
											+ "\"}");
								}
								if (flag) {
									rows.get(i).remove(paramCol);
									rows.get(i).put(paramCol.replace("SMARTPATH", "SMARTLINK"),
											pathSmartLink.isEmpty() ? "" : pathSmartLink.toString());
								} else {
									al.add(i);
								}
							}

						} catch (Exception e) {
							System.out.println(e);
							generalUtilLogger.logWrite(LevelType.WARN,
									"Error in search sql DataTable smartpath data perparation. smartPath=" + smartPath + ", sql=" + sql, "-1",
									ActivitylogType.SQLError, null, e);
						}
					}
					// kd 14042019 iterate rows and check if row has wrong path then it should be removed from result
					int i = 0;
					for (Iterator<Map<String, Object>> iter = rows.listIterator(); iter.hasNext();) {
						iter.next();
						if (al.contains(i)) { //It is checking current line (from rows) with array, which have wrong lines and shouldn't be in result 
							iter.remove();
						}
						i++;
					}
				}
				/******** SMARTACTIONS handler *****************************************************************/
				else if (!paramCol.isEmpty() && paramCol.endsWith("SMARTACTIONS") 
						&& (sql.toLowerCase().contains("select * from fg_s_action_dte_v") 
								|| sql.toLowerCase().contains("select * from fg_s_action_dtfe_v")
								)) {
					try{
					for (int i = 0; i < rows.size(); i++) {
						JSONObject  defaultRequest = new JSONObject();
						JSONArray defaultRequestArr = new JSONArray();
						if (rows.get(i).get(paramCol) != null) {
							String smartActions = rows.get(i).get(paramCol).toString();
							JSONObject json = new JSONObject(smartActions);
							if(!json.isNull("request")){
							String action_id = json.getString("request").replaceAll("@", "");
							if (action_id.matches("[0-9]+") ) {
								String requestIcon = actionRequestIconMap.get(action_id);
								List<String> sampleList = actionSampleMap.get(action_id);
								if(sampleList == null || sampleList.isEmpty()){
									String json_ = json.toString();
									json = new JSONObject(json_.replaceAll("@COPY_SELFTEST_TOOLTIP_AND_MESSAGE@", generalUtil.getSpringMessagesByKey("NO_SAMPLE_IN_ACTION_COPY","Copy allowed only if there is at least one sample in the action")));
								}
								else{
									String json_ = json.toString();
									json = new JSONObject(json_.replaceAll("@COPY_SELFTEST_TOOLTIP_AND_MESSAGE@", generalUtil.getSpringMessagesByKey("SELFTEST_ACTION_COPY","Copy from default Self-Test")));
								}
								if(!generalUtil.getNull(requestIcon).isEmpty()){
									if(requestIcon.startsWith("[")){
										defaultRequestArr =  new JSONArray(requestIcon);
									}else{
									defaultRequest = new JSONObject (requestIcon);
									}
								}
								else{
									defaultRequest = new JSONObject ("{\"icon\":\"fa fa-plus-square\",\"funcName\":\"createNewRequest\",\"tooltip\":\"Create Request(" + i + ")\",\"cellType\":\"link\",\"width\":\"100%\",\"params\":[\""+action_id+"\", \"Action\"]}");
								}
							json.put("request",defaultRequestArr.length()==0?defaultRequest:defaultRequestArr);
							rows.get(i).put(paramCol, json.toString());
							}
						}
						}
					}
					}
					catch(Exception e){
						generalUtilLogger.logWrite(LevelType.ERROR, "error in smartAction", "", ActivitylogType.GeneralError,null, e);
					}
				}
				else if (!paramCol.isEmpty() && paramCol.endsWith("SMARTACTIONS") && args.length > 0)
				{
					if((newsql.toLowerCase().contains("fg_s_materialref_dten") || sql.toLowerCase().contains("fg_s_materialref_dte")
							|| sql.toLowerCase().contains("fg_s_composition_dte"))
						//(sql.toLowerCase().contains("fg_s_paramref_dte_v") || sql.toLowerCase().contains("fg_s_paramref_dt_v") || sql.toLowerCase().contains("fg_s_paramref_dteexp_v"))
					)
					{
						rows = removeColumnsBeforeDisplay(rows,args[0]);
					}
				}
				/******** SMARTSAMPLELIST handler *****************************************************************/
				else if (!paramCol.isEmpty() && paramCol.endsWith("SMARTSAMPLELIST")) {
					for (int i = 0; i < rows.size(); i++) {
						try{
						String sampleList = "";
						if (rows.get(i).get(paramCol) != null) {
							String action_id =  rows.get(i).get(paramCol).toString().replaceAll("@", "");
							if (action_id.matches("[0-9]+") ) {
								String formnumberid_ = "";
								List<String>  actionList_ = new ArrayList<>();
								if(actionSampleMap.get(action_id)!=null){
									actionList_ = actionSampleMap.get(action_id);
								}
								sampleList = "{\"displayName\":"+ actionList_+ ",\"htmlType\":\"select\", \"multiple\":\"true\",\"autoSave\":\"true\",\"width\":\"105%\"," + 
										"\"excludeID\":\"\",\"dbColumnName\":\"Sample\",\"formCode\":\"Action\",\"formNumberID\":\""+ formnumberid_ +"\",\"fullList\":" + 
										experimentSampleList+"}";
							}
							else{
								sampleList = "{\"displayName\":[],\"htmlType\":\"select\", \"multiple\":\"true\",\"autoSave\":\"true\",\"width\":\"105%\"," + 
										"\"excludeID\":\"\",\"dbColumnName\":\"Sample\",\"formCode\":\"Action\",\"formNumberID\":\"\",\"fullList\":" + 
										"[]}";
							}
						}
						rows.get(i).put(paramCol , sampleList);
						}catch(Exception e){
							String emptyJson = "{\"displayName\":[],\"htmlType\":\"select\", \"multiple\":\"true\",\"autoSave\":\"true\",\"width\":\"105%\"," + 
									"\"excludeID\":\"\",\"dbColumnName\":\"Sample\",\"formCode\":\"Action\",\"formNumberID\":\"\",\"fullList\":" + 
									"[]}";
							rows.get(i).put(paramCol , emptyJson);
						}
					}
				}
				/******** Favorite_SMARTEDIT handler *****************************************************************/
				else if (!paramCol.isEmpty() && paramCol.equalsIgnoreCase("Favorite_SMARTEDIT")) {
					
					for (int i = 0; i < rows.size(); i++) {
						if (rows.get(i).get(paramCol) != null) {
							JSONObject jsonObject = new JSONObject(rows.get(i).get(paramCol).toString());
							String formId_ = Integer.toString(jsonObject.getInt("displayName"));
							jsonObject.remove("displayName");
							jsonObject.put("displayName",generalUtil.getFavoritById(formId_));
							rows.get(i).put(paramCol, jsonObject);  //rows.get(i).get(paramCol);
						}
					}
				}
				/******** Step Name_SMARTEDIT handler *****************************************************************/
				else if (!paramCol.isEmpty()
						&& sql.toLowerCase().contains("fg_s_reportfilterref_dte_v")) {
					if (paramCol.equalsIgnoreCase("Step Name_SMARTEDIT")) {
						for (int i = 0; i < rows.size(); i++) {
							Object colvalObj = rows.get(i).get(paramCol);

							System.out.println(
									"use stepNameList and maybe make function that get and the json now this is hard coded");
							String stepObj = "{}";
							stepObj = getJsonDisplayObj(stepNameList,null,
									colvalObj == null ? null : colvalObj.toString(), "STEPNAME", "true","");
							rows.get(i).put(paramCol, stepObj); // rows.get(i).get(paramCol);

						}
					}
					else if(paramCol.equalsIgnoreCase("Rule Name_SMARTEDIT")) {
						//init "RuleName" list from reactionAndResultsAnalysisRuleName property
						String ruleName = reactionAndResultsAnalysisRuleName_;
						List<String> ruleNameList = Arrays.asList(ruleName.split(","));
						for (int i = 0; i < rows.size(); i++) {
							Object colvalObj = rows.get(i).get(paramCol);
							String ruleObj = "{}";
							ruleObj = getJsonDisplayObj(ruleNameList,null,colvalObj == null ? null : colvalObj.toString(), "RULENAME","false",",\"renderTableAfterSave\":\"true\"");
							rows.get(i).put(paramCol, ruleObj);
						}
					}
					else if(paramCol.equalsIgnoreCase("Columns Selection_SMARTEDIT")) {
						//init "Columns Selection" list from reactionAndResultsAnalysisRuleName property
						String cols_s = reactionAndResultsAnalysisColsSelection_;
						List<String> cols_sList = Arrays.asList(cols_s.split(","));
						for (int i = 0; i < rows.size(); i++) {
							Object colvalObj = rows.get(i).get(paramCol);
							String ruleObj = "{}";
							ruleObj = getJsonDisplayObj(cols_sList,null,colvalObj == null ? null : colvalObj.toString(), "COLUMNSSELECTION","true","");
							rows.get(i).put(paramCol, ruleObj);
						}
					}
					else if(paramCol.equalsIgnoreCase("Rule Condition_SMARTEDIT")) {
						List<Map<String, Object>> materialData = null;
						String stepNameCSV = args[0];
						List<Map<String, Object>> materialTypes = null;
						List<Map<String, String>> expMaterialData = new ArrayList<>();
						List<Map<String, String>> materialTypeData = new ArrayList<>();
						
						//If ‘Main Solvent’ or ‘Limiting Agent’ are selected, the Rule condition is disabled and display the ‘rule name’.
						String ruleName_json ="",ruleName;
						//{"displayName":[{"ID":"Limiting Agent","displayName":"Limiting Agent"}],"htmlType":"select", "multiple":"false","dbColumnName":"RULENAME", "colCalcId":"RULENAME", "allowSingleDeselect":"false", "autoSave":"true", "fullList":[{"VAL":"Main Solvent","ID":"Main Solvent"},{"VAL":"Limiting Agent","ID":"Limiting Agent"},{"VAL":"Material Type","ID":"Material Type"},{"VAL":"Experiment Materials","ID":"Experiment Materials"}]}
						for (int i = 0; i < rows.size(); i++) {
							ruleName_json = rows.get(i).get("Rule Name_SMARTEDIT")==null?"{}": rows.get(i).get("Rule Name_SMARTEDIT").toString();
							JSONArray displayNameArr = new JSONArray(generalUtil.getJsonValById(ruleName_json, "displayName"));
							if(displayNameArr== null || displayNameArr.length() == 0) {
								ruleName ="";
							}else {
								ruleName = generalUtil.getJsonValById(displayNameArr.get(0).toString(), "displayName");
							}
							if(ruleName.equalsIgnoreCase("Main Solvent")) {
								rows.get(i).put(paramCol, "Main Solvent");
							}
							else if(ruleName.equalsIgnoreCase("Limiting Agent")) {
								rows.get(i).put(paramCol, "Limiting Agent");
							}else if(ruleName.equalsIgnoreCase("Material Type")) {
								//If Material Type is selected, the rule condition is a dropdown list that contains list of all material types that exists in the selected steps .
								//The user can select number of ‘Material Type’ in each row. 
								if(materialTypes == null) {
									materialTypes = getListOfMapsBySql("select distinct mt.MATERIALTYPE_ID,mt.MATERIALTYPENAME from \n" + 
											"fg_s_materialtype_all_v mt,fg_s_materialref_v m,fg_s_invitemmaterial_v t\n" + 
											"where instr(','||t.MATERIALTYPE_ID||',', ','||mt.MATERIALTYPE_ID||',')>0 and m.parentid in ("+stepNameCSV+") and m.active = 1 and m.sessionid is null\n" + 
											"and m.INVITEMMATERIAL_ID = t.invitemmaterial_id");
								}
								if(materialTypes!= null && materialTypeData.isEmpty() ) {
									for (int j = 0; j < materialTypes.size(); j++) {
										if (materialTypes.get(j).get("MATERIALTYPE_ID")!= null && materialTypes.get(j).get("MATERIALTYPENAME")!= null) {
											Map<String, String> materialMap = new HashMap<>();
											materialMap.put("ID", materialTypes.get(j).get("MATERIALTYPE_ID").toString());
											materialMap.put("VAL", materialTypes.get(j).get("MATERIALTYPENAME").toString());
											materialTypeData.add(materialMap);
										}
									}
								}
								String obj = "{}";
								Object colvalObj = rows.get(i).get(paramCol);
								obj = getJsonDisplayObj(null,materialTypeData,colvalObj == null ? null : colvalObj.toString(), "RULECONDITION","true","");
								rows.get(i).put(paramCol, obj);
							}
							else if(ruleName.equalsIgnoreCase("Experiment Materials")) {
								if(materialData == null) {
									materialData = getListOfMapsBySql("select distinct t.INVITEMMATERIAL_ID,t.INVITEMMATERIALNAME,m.MATERIALTYPE_ID,m.MATERIALTYPENAME from fg_s_materialref_all_v t,fg_s_invitemmaterial_all_v m where t.INVITEMMATERIAL_ID=m.INVITEMMATERIAL_ID and t.STEP_ID in (" + stepNameCSV + ") and t.active = 1 and t.sessionid is null");
								}
								if(materialData!= null && expMaterialData.isEmpty() ) {
									for (int j = 0; j < materialData.size(); j++) {
										if (materialData.get(j).get("INVITEMMATERIAL_ID")!= null && materialData.get(j).get("INVITEMMATERIALNAME")!= null) {
											Map<String, String> materialMap = new HashMap<>();
											materialMap.put("ID", materialData.get(j).get("INVITEMMATERIAL_ID").toString());
											materialMap.put("VAL", materialData.get(j).get("INVITEMMATERIALNAME").toString());
											expMaterialData.add(materialMap);
										}
									}
								}
								String obj = "{}";
								Object colvalObj = rows.get(i).get(paramCol);
								obj = getJsonDisplayObj(null,expMaterialData,colvalObj == null ? null : colvalObj.toString(), "RULECONDITION","false","");
								rows.get(i).put(paramCol, obj);
							}
						}
					}
					else if(paramCol.equalsIgnoreCase("Column Name_SMARTEDIT")) {
						for (int i = 0; i < rows.size(); i++) {
							Object colvalObj = rows.get(i).get(paramCol);
							String displayName = colvalObj==null?"":colvalObj.toString();
							String ruleObj = "{\"displayName\":\""+displayName+"\",\"saveType\":\"text\",\"htmlType\":\"editableDiv\",\"autoSave\":\"true\",\"dbColumnName\":\"COLUMNNAME\"}";
							rows.get(i).put(paramCol, ruleObj);
						}
						
					}
				}
				/**************************************************************************************************/
			}
			if (hideEmptyColumns.equals("true")) {
				// remove columns with empty data
				removeEmptyColumnsFromRows(rows, dataTableTopRowsNum);
			}

			//kd 20032018 add check and call new method instead getJSONArrayOfColumns. Use this for creating column not only by first row as was before
			/*if(hasColMonParam)
			{
				//12122018 ab: need to clarify importance of next method: 
				//			  - works fine with getJSONArrayOfColumns when: deleting monParameter from DB, changing monParameter type; 
				jsonArrayOfColumns = getJSONArrayOfColumnsMonPar(rows,colAfterParm);
			} 
			else {
				jsonArrayOfColumns = getJSONArrayOfColumns(rows);
			}*/

			jsonArrayOfColumns = getJSONArrayOfColumns(rows);

			Map<String, String> metaData = getMetaData(sql); //tableName
			metaData = getMetaDataFromJSONArrayOfColumns(metaData, jsonArrayOfColumns);
			listOfMetaData = getListOfNameAndTypeFromMetaData(metaData);
			List<String> dateList = getDateListFromMetaData(metaData);
			if (!displayEmptyTable) {
				jsonArrayOfData = getJSONArrayOfData(rows, dateList, dataTableTopRowsNum);
			}

			if (Integer.parseInt(dataTableTopRowsNum) != -1 && rows.size() > Integer.parseInt(dataTableTopRowsNum)) {
				toReturn.put("displayTopRows", dataTableTopRowsNum);
			} else if (rows.isEmpty()) {
				toReturn.put("displayTopRows", "-2");
			} else {
				toReturn.put("displayTopRows", "-1");
			}

			if(internalQuery.length()!=0 && sqlInfoSb != null){
				sqlInfoSb.append("</br>Note: in this query we use also the following sqls:</br>");
				sqlInfoSb.append(internalQuery);
				
			}
			
			
			toReturn.put("columns", jsonArrayOfColumns);
			toReturn.put("data", jsonArrayOfData);
			toReturn.put("metaData", listOfMetaData);
			
		} catch (Exception e) {
			int errCode = 0;
			String errMessage = "General error in data table SQL";
			try {
				if (e.getCause() instanceof SQLException) {
					errCode = ((SQLException) e.getCause()).getErrorCode();
					if (errCode == 1795) {// "ORA-01795"
						errMessage = "There where chosen more than 1000 rows in the upper table. Please uncheck some rows in order to get data";
					}
				}
			} catch (Exception e1) {
				generalUtilLogger.logWrite(e1);
			}
			generalUtilLogger.logWriter(LevelType.ERROR,
					"SQL (sql error code=" + errCode + ") or general error! e=" + e + ", sql=" + sql,
					ActivitylogType.SQLError, "-1", e, null);

			if (sql != null && !sql.equals(jsonSqlErrorMsg(errMessage))) { // to avoid endless recalls
				return getJSONObjectOfDateTable(jsonSqlErrorMsg(errMessage), "", "-1",null);
			} else {
				// we not suppose to get to this part but just in case... ->
				toReturn.put("displayTopRows", "-1");
				toReturn.put("columns", "");
				toReturn.put("data", "");
				toReturn.put("metaData", "");
			}
		}
		return toReturn;
	}

	//TODO: remove not in use
	private Map<String, String> addImageToMatrix(BufferedImage overlay, String type, List<BufferedImage> source,
			String x, String y, int numOfMaterialinImage) throws IOException {
		Map<String, String> returnmap = new HashMap<>();
		//overlay = resize(overlay,600,168);
		int imageType = "png".equalsIgnoreCase(type) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
		if (numOfMaterialinImage == 0) {
			//ImageIO.write( overlay, "png", source );
			source.add(overlay);
			numOfMaterialinImage++;
			returnmap.put("x", "0");
			returnmap.put("y", "0");
			return returnmap;
		}

		BufferedImage image = source.get(0);
		// BufferedImage overlay = ImageIO.read(watermark);

		// determine image type and handle correct transparency
		int newWidth = overlay.getWidth() * 10;//x!=null?image.getWidth():(image.getWidth()/overlay.getWidth()<10?image.getWidth()+overlay.getWidth():image.getWidth());
		int newHeight = y != null ? image.getHeight()
				: (numOfMaterialinImage % 10 > 0 ? image.getHeight() : image.getHeight() + overlay.getHeight());
		newHeight = newHeight == 0 ? overlay.getHeight() : newHeight;//it is the first write
		BufferedImage concatImage = new BufferedImage(newWidth, newHeight, imageType);

		// initializes necessary graphic properties
		Graphics2D w = (Graphics2D) concatImage.getGraphics();
		w.drawImage(image, 0, 0, null);
		// calculates the coordinate where the String is painted
		int xPos = generalUtil.getNullInt(x, numOfMaterialinImage % 10 * overlay.getWidth());
		int yPos = generalUtil.getNullInt(y, newHeight - overlay.getHeight());
		//int centerY = image.getHeight();

		// add text watermark to the image
		w.drawImage(overlay, xPos, yPos, null);
		//ImageIO.write(concatImage, type, source);
		source.set(0, concatImage);
		if (x == null && y == null) {
			numOfMaterialinImage++;
		}
		w.dispose();
		returnmap.put("x", String.valueOf(xPos * 0.7));
		returnmap.put("y", String.valueOf(yPos * 0.7));
		return returnmap;
	}
	//TODO: remove not in use
	private static BufferedImage resize(BufferedImage img, int width, int height) {
		Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resized.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return resized;
	}

	/**
	 * 
	 * @param columnType - the column type of the current value
	 * @param index - the column index
	 * @param rs - the result set of the current row
	 * @return the value of the column. If the column type is a Clob then it's converted into a plain text.
	 * @throws SQLException
	 */
	private Object getColumnValue(String columnType, int index, ResultSet rs) throws SQLException {
		Object toReturn;
		if (columnType.equals("CLOB")) {
			CLOB clob = (CLOB) rs.getClob(index);
			if (clob != null) {
				toReturn = clob.getSubString(1, (int) clob.length());
			} else {
				toReturn = "";
			}
		} else {
			toReturn = rs.getString(index);
		}
		return toReturn;
	}

	private JSONArray getMPUOMArr(String mpID, String mpName, StringBuilder defaultSelUomStr) {
		JSONArray retval;
		try {
			String arrString = "";
			/*if(!generalUtil.getNull(mpID).equals("")) {
				arrString = generalUtil.getNull(generalUtilForm.getCurrrentNameInfoAllContainsId("MP", mpID).get(0).get("PARAM_UOM_OBJ"), "");
				
			} else {
				List<Map<String, String>> mpListMap = generalUtilForm.getCurrrentNameInfoAllContainsName("MP", mpName);
				arrString = generalUtil.getNull(mpListMap.get(0).get("PARAM_UOM_OBJ"), "");
				defaultSelUomStr.append(generalUtil.getNull(mpListMap.get(0).get("DEFAULT_UOM_OBJ"), "{}"));
				System.out.println("defaultUom=" + defaultSelUomStr.toString());
			}*/

			List<Map<String, String>> mpListMap = generalUtilForm.getCurrrentNameInfoAllContainsName("MP", mpName);
			arrString = generalUtil.getNull(mpListMap.get(0).get("PARAM_UOM_OBJ"), "");
			defaultSelUomStr.append(generalUtil.getNull(mpListMap.get(0).get("DEFAULT_UOM_OBJ"), "{}"));
//			System.out.println("defaultUom=" + defaultSelUomStr.toString());

			if (!arrString.equals("")) {
				retval = new JSONArray(arrString);
			} else {
				retval = new JSONArray();
			}
		} catch (JSONException e) {
			retval = new JSONArray();
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}

		return retval;
	}


	private String getParamCol(List<Map<String, Object>> rows) {
		String toReturn = "";
		if ((!rows.isEmpty())) {
			Set<String> rowKeySet = rows.get(0).keySet();
			for (String k : rowKeySet) {
				if (toReturn.equals("") && (k.endsWith("_SMARTMONPARAM") || k.endsWith("_SMARTDYNPARAM"))) {
					toReturn = k;
				}
			}
		}
		return toReturn;
	}

	private Map<String, List<String>> getSmartsCol(List<Map<String, Object>> rows, boolean paramColFlag) {
		Map<String, List<String>> returnMap = new LinkedHashMap<String, List<String>>();
		List<String> colAfterParm = new ArrayList<String>();
		List<String> smartsList = new ArrayList<String>();
		String hasPivot = "";
		int arrInx = 0;

		if ((!rows.isEmpty())) {
			Set<String> rowKeySet = rows.get(0).keySet(); // get all column names
			for (String k : rowKeySet) {
				if (paramColFlag || k.endsWith("_SMARTMONPARAM") || k.endsWith("_SMARTDYNPARAM") || k.endsWith("_SMARTPATH") || k.endsWith("SMARTACTIONS")|| k.endsWith("SMARTSAMPLELIST") || k.endsWith("SMARTSPLIT") || k.endsWith("Favorite_SMARTEDIT")) {
					smartsList.add(arrInx++, k);
				} else if (k.endsWith("_SMARTPIVOT") || k.endsWith("_SMARTPIVOTSQL")) {
					hasPivot = k;
				}
			}
			boolean isFind = false;
			for (String s : smartsList) {
				for (String k : rowKeySet) {
					if (k.equals(s)) {
						isFind = true;
					} else if (isFind) {
						colAfterParm.add(k);
					}
				}
				isFind = false;
				returnMap.put(s, colAfterParm);
				colAfterParm = new ArrayList<String>();
			}
			// 12122018 ab: pivot must be displayed as latest columns in the table and ignore position of given sql view 
			if (!hasPivot.equals("")) {
				returnMap.put(hasPivot, new ArrayList<String>());
			}
		}
		return returnMap;
	}

	private Map<String, String> getMetaDataFromJSONArrayOfColumns(Map<String, String> metaData,
			JSONArray JSONArrayOfColumns) {
		Map<String, String> toReturn = new HashMap<String, String>();
		try {
//			if (metaData != null) {
//				for (int i = 0; i < JSONArrayOfColumns.length(); i++) {
//					if (JSONArrayOfColumns.getJSONObject(i).has("title")) {
//						for (Map.Entry<String, String> entry : metaData.entrySet()) {
//							if (entry.getKey().equals(JSONArrayOfColumns.getJSONObject(i).get("title"))) {
//								toReturn.put(entry.getKey(), entry.getValue());
//								break;
//							}
//						}
//					}
//				}
//			} yp 21042020 - add all (not just the one in the meta data) to the  return list (if it is not exists in the metadata because it was added in smartpivot or smartsplit) we will add it as VARCHAR2  
			if (metaData != null) {
				for (int i = 0; i < JSONArrayOfColumns.length(); i++) {
					if (JSONArrayOfColumns.getJSONObject(i).has("title")) {
						String title_ = (String) JSONArrayOfColumns.getJSONObject(i).get("title");
						if(metaData.containsKey(title_)) {
							toReturn.put(title_, metaData.get(title_));
						} else {
							toReturn.put(title_, "VARCHAR2");
						}
					}
				}
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return toReturn;
	}

	/**
	 * Remove Empty Columns
	 * @param rows
	 */
	private void removeEmptyColumnsFromRows(List<Map<String, Object>> rows, String dataTableTopRowsNum) {
		if (!rows.isEmpty()) {
			boolean indexFlagArray[] = new boolean[rows.get(0).size()];
			int count;
			List<Integer> indexFlagArrayList = new ArrayList<Integer>();
			List<String> removeList = new ArrayList<String>();
			//fill a boolean array of empty columns
			int indexRow = 0;
			for (Map<String, Object> row : rows) {
				count = 0;
				for (Entry<String, Object> entry : row.entrySet()) {
					if (entry.getValue() != null && !entry.getValue().equals("")) {
						indexFlagArray[count] = true;
					}
					count++;
				}
				if (Integer.parseInt(dataTableTopRowsNum) != -1
						&& ++indexRow >= Integer.parseInt(dataTableTopRowsNum)) {
					break;
				}
			}
			for (int i = 0; i < indexFlagArray.length; i++) {
				if (indexFlagArray[i] == false) {
					indexFlagArrayList.add(i);
				}
			}
			//remove empty columns
			if (!indexFlagArrayList.isEmpty()) {
				count = 0;
				for (Entry<String, Object> entry : rows.get(0).entrySet()) {
					for (int index : indexFlagArrayList) {
						if (index == count) {
							removeList.add(entry.getKey());
						}
					}
					if (removeList.size() == indexFlagArrayList.size()) {
						break;
					}
					count++;
				}
				for (Map<String, Object> row : rows) {
					for (String remove : removeList) {
						row.remove(remove);
					}
				}
			}
		}
	}

	private JSONArray getJSONArrayOfColumns(List<Map<String, Object>> rows) {
		JSONArray JSONArrayOfColumns = new JSONArray();
		JSONObject json = null;
		JSONObject colWidthObj = null;
		String colDefaultWidth = null;
		long start = System.currentTimeMillis();

		try {
			if ((rows != null) && (!rows.isEmpty())) {
				Set<String> colSet = rows.get(0).keySet();
				
				if(colSet.contains("SMARTWIDTH"))
				{
					try {
						
						String str = rows.get(0).get("SMARTWIDTH").toString();
//						System.out.println(str);
						colWidthObj = new JSONObject(str);
						if(colWidthObj.has("default_initial_width")) colDefaultWidth = colWidthObj.optString("default_initial_width");
					} 
					catch (Exception e) {
						colWidthObj = new JSONObject();
						generalUtilLogger.logWrite(e);
						e.printStackTrace();
					}
				}
				
				for (String col : colSet) {
					json = new JSONObject();
					json.put("showPageFilter", "0");

					// check if column name has suffix "_SMART" and remove it. 
					int _ind = col.indexOf("_SMART");
					String _colNameWithoutSmart = (_ind > 0) ? col.substring(0, _ind) : col;
					//check if column name contains "{additional_title}" to set uniqueness for title
					int _bInd = _colNameWithoutSmart.indexOf("{");
					int _eInd = _colNameWithoutSmart.indexOf("}");
					String _unqTitle = _colNameWithoutSmart;
					if (_bInd > -1 && _eInd > 0) {
						String _unqPart = _colNameWithoutSmart.substring(_bInd + 1, _eInd);
						_unqTitle = _colNameWithoutSmart.replace("{" + _unqPart + "}", _unqPart);
						col = col.replace("{" + _unqPart + "}", "");
//						System.out.println(_unqTitle);
					}
					
					if(col.indexOf("@PIVOT_GROUP@") > -1)
					{
						String groupVal = col.substring(0,col.indexOf("@PIVOT_GROUP@"));
						json.put("headerGroupVal", groupVal);
					}
					col = col.replaceAll(".*@PIVOT_GROUP@", "");
					_unqTitle = _unqTitle.replaceAll(".*@PIVOT_GROUP@", "");
					json.put("title", col);
					json.put("uniqueTitle", _unqTitle);
					try {
						if(colWidthObj != null)
						{ 
							if(colWidthObj.has(_unqTitle))						
							{
								json.put("initialWidth", colWidthObj.optString(_unqTitle));
							}
							else if(colDefaultWidth != null && !colDefaultWidth.equals(""))
							{
								json.put("initialWidth",colDefaultWidth);
							}
						}
					} catch (Exception e) {
						generalUtilLogger.logWrite(e);
						e.printStackTrace();
					}
					JSONArrayOfColumns.put(json);
				}
			} else {
				json = new JSONObject();
				json.put("showPageFilter", "0");
				json.put("title", "");
				json.put("uniqueTitle", "");
				JSONArrayOfColumns.put(json);
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		long time = System.currentTimeMillis() - start;
		Map<String,String> map = new HashMap<>();
		map.put("TIME_PIVOT_PARSE", String.valueOf(time));
		generalUtilLogger.logWrite(LevelType.DEBUG,
				" getJsonArrayOfColumns", "-1",
				ActivitylogType.PerformanceJava, map);
		return JSONArrayOfColumns;
	}

	private JSONArray getJSONArrayOfData(List<Map<String, Object>> rows, List<String> dateList, String dataTableTopRowsNum) {
		boolean isDateExists = (dateList.isEmpty()) ? false : true;
		JSONArray JSONArrayOfData = null;
		List<List<String>> ListOfList = new ArrayList<List<String>>();
		/* pattern to find '<' sign with non-digit signs combination and replace it with his html code '&lt;';
		 * code added because of issue in datatable to render '<' sign as cell value
		 */
		Pattern r = Pattern.compile("<[^0-9]+");
		Matcher m = null;
		List<String> rowList;
		String value;
		int rowIndex = 0;
		try {
			for (Map<String, Object> row : rows) {
				rowList = new ArrayList<String>();
				for (Entry<String, Object> entry : row.entrySet()) {
					value = (entry.getValue() == null) ? "" : entry.getValue().toString();
					if ((isDateExists) && (!value.equals(""))) {
						for (String date : dateList) {
							if (entry.getKey().equals(date)) {
								SimpleDateFormat fromUser, myFormat;
								if (entry.getKey().contains("_SMARTTIME")) {
									fromUser = new SimpleDateFormat(
											generalUtil.getSelectDateQueryDateFormat() + " HH:mm", Locale.ENGLISH);
									myFormat = new SimpleDateFormat(generalUtil.getUserDateFormatServer() + " HH:mm",
											Locale.ENGLISH);
								} else if (entry.getKey().contains("_SMARTDATETIME")) {
									fromUser = new SimpleDateFormat(
											generalUtil.getSelectDateQueryDateFormat() + " hh:mm:ss", Locale.ENGLISH);
									myFormat = new SimpleDateFormat(generalUtil.getUserDateFormatServer(),
											Locale.ENGLISH);
								} else {
									fromUser = new SimpleDateFormat(generalUtil.getSelectDateQueryDateFormat(),
											Locale.ENGLISH);
									myFormat = new SimpleDateFormat(generalUtil.getUserDateFormatServer(),
											Locale.ENGLISH);
								}

								if (entry.getKey().contains("_SMARTDATETIME")) {
									//creates a json object with real and displayd values
									String convertedValue = myFormat.format(fromUser.parse(value));
									JSONObject json = new JSONObject();
									json.put("displayName", convertedValue);
									json.put("realvalue", String.valueOf(fromUser.parse(value).getTime()));//sets the timestamp in the real value
									value = json.toString();
								} else {
									value = myFormat.format(fromUser.parse(value));
								}

								break;
							}
						}
					}
					if (entry.getKey().endsWith("_SMARTDATE")) {
						String displayName = generalUtil.getJsonValById(value, "displayName");
						if (!displayName.equals("")) {
							SimpleDateFormat fromUser = new SimpleDateFormat(generalUtil.getConversionDateFormat(),
									Locale.ENGLISH);
							SimpleDateFormat myFormat = new SimpleDateFormat(generalUtil.getUserDateFormatServer(),
									Locale.ENGLISH);
							String convertedValue = myFormat.format(fromUser.parse(displayName));

							value = generalUtil.updateJsonValById(value, "displayName", convertedValue);
						}

					}
					if (entry.getKey().contains("_SMARTRANGE") || entry.getKey().contains("_SMARTICON")
							|| entry.getKey().contains("_SMARTELLIPSIS") || entry.getKey().contains("_SMARTLINK")) {
						m = r.matcher(value);
						while (m.find()) {
							value = value.replace("<", "&lt;");
						}
					}
					if (value.contains("displayPath")) {
						String delimiter = "/";
						/*String path = generalUtil.getJsonValById(value, "displayPath");
						String displayName = generalUtil.getJsonValById(value, "displayName");*/
						String path = value.substring(value.indexOf("{[") - 1, value.indexOf("]}") + 3);
						value = value.substring(0, value.indexOf("displayPath") - 2)
								+ value.substring(value.indexOf("]}") + 3, value.length());
						String displayName = generalUtil.getJsonValById(value, "displayName");/**/
						String pathToDisplay = displayName + delimiter
								+ generalUtil.showFormPathDisplay(path, delimiter);
						value = value.replace(displayName, pathToDisplay);
					}
					rowList.add(value);
				}
				ListOfList.add(rowList);

				if (Integer.parseInt(dataTableTopRowsNum) != -1
						&& ++rowIndex >= Integer.parseInt(dataTableTopRowsNum)) {
					break;
				}
			}

			JSONArrayOfData = new JSONArray(ListOfList);
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return JSONArrayOfData;
	}

	@Override
	public boolean isTableExists(String tableName) {
		String sql = "select count(*) as counter_\r\n" + "from USER_TABLES \r\n" + "where TABLE_NAME = upper('"
				+ tableName + "')";
		String counter_ = selectSingleString(sql);
		return counter_.equals("0") ? false : true;
	}

	@Override
	public boolean isViewExists(String tableName) {
		String sql = "select count(*) as counter_\r\n" + "from user_tab_cols \r\n" + "where TABLE_NAME = upper('"
				+ tableName + "')";
		String counter_ = selectSingleString(sql);
		return counter_.equals("0") ? false : true;
	}

	@Override
	public String callPackageFunction(String pack, String function, Map<String, String> parameters) {
		MapSqlParameterSource in = new MapSqlParameterSource();
		logger.info("/* SQL CALL callPackageFunction function=" + pack + "." + function + "*/");
		SimpleJdbcCall packageFunction = new SimpleJdbcCall(jdbcTemplate).withFunctionName(function)
				.withSchemaName(username);
		packageFunction = pack.equals("") ? packageFunction : packageFunction.withCatalogName(pack);
		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			in.addValue(entry.getKey(), entry.getValue());
		}
		in.addValue("ts_in", String.valueOf(new Date().getTime()));
		return String.valueOf(packageFunction.executeFunction(String.class, (SqlParameterSource) in));
	}

	@Override
	public Map<String, Object> callProcedureReturnsOutObject(String pack, String procedureName,
			Map<String, String> simpleParameters, Map<String, String> outParameters, int outType) {
		Map<String, String> inParams = new HashMap<>();
		logger.info("/* SQ CALLL callProcedureReturnsOutObject procedureName=" + pack + "." + procedureName + "*/");
		SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withSchemaName(username)
				.withProcedureName(procedureName);
		jdbcCall = pack.equals("") ? jdbcCall : jdbcCall.withCatalogName(pack);
		for (Map.Entry<String, String> entry : simpleParameters.entrySet()) {
			jdbcCall.addDeclaredParameter(new SqlParameter(entry.getKey(), OracleTypes.VARCHAR));
			inParams.put(entry.getKey(), entry.getValue());
		}
		for (Map.Entry<String, String> entry : outParameters.entrySet()) {
			if (outType == OracleTypes.CURSOR) {
				jdbcCall.addDeclaredParameter(
						new SqlOutParameter(entry.getKey(), outType, new ColumnMapRowMapper()/*RowMapper()
																								{
																								public Object mapRow(ResultSet rs, int rowNum) throws SQLException{
																								return rs.getObject(1); 
																								}
																								}*/));
			} else {
				jdbcCall.addDeclaredParameter(new SqlOutParameter(entry.getKey(), outType));
			}
			inParams.put(entry.getKey(), entry.getValue());

		}

		Map<String, Object> result = jdbcCall.execute(inParams);
		return result;
	}

	@Override
	public List<Map<String, Object>> callProcedureReturnsOutObject(String procedureName,
			List<String> parametersInCallOrder) {
		List<Map<String, Object>> MetaDataValMap = new ArrayList<>();
		Map<String, Object> recordMap;
		CallableStatement stmt = null;
		Connection conn = null;
		ResultSet rs = null;
		String sql = "";
		try {
			/*Class.forName(driverClassName);
			conn = DriverManager.getConnection(url, username, password); */

			conn = getConnectionFromDataSurce();

			sql = "call " + procedureName + "(" + generalUtil.formatParamInSigns(parametersInCallOrder.size() + 1)
					+ ")";//+1->for the out parameter
			stmt = conn.prepareCall(sql);
			int i = 0;
			for (; i < parametersInCallOrder.size(); i++) {
				stmt.setString(i + 1, parametersInCallOrder.get(i));
			}
			stmt.registerOutParameter(i + 1, OracleTypes.CURSOR);

			stmt.execute();
			rs = (ResultSet) stmt.getObject(i + 1);

			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			while (rs.next()) {
				recordMap = new HashMap<>();
				for (i = 1; i <= columnCount; i++) {
					recordMap.put(rsmd.getColumnName(i), generalUtil.getNull(rs.getString(i)));
				}
				MetaDataValMap.add(recordMap);
			}
		} catch (Exception sqlEx) {
//			System.out.println(sqlEx.toString());
			generalUtilLogger.logWrite(LevelType.ERROR, "SQL=[" + sql + "] error in sql!", "", ActivitylogType.SQLError,
					null, sqlEx);
			try {
				conn.rollback();
			} catch (Exception ex) {
				generalUtilLogger.logWrite(ex);
			}
		} finally {
			try {
				if (stmt != null) {

					stmt.close();
					rs.close();
				}
				/*if (conn != null) {
					conn.close();
				}*/
				releaseConnectionFromDataSurce(conn);
			} catch (Exception e) {
				generalUtilLogger.logWrite(e);
				e.printStackTrace();
			}
		}
		return MetaDataValMap;
	}

	/*
	 * Return list of Inf table data object
	 * @see com.skyline.form.dal.GeneralDao#sqlToInfDataObjList(java.lang.String)
	 */
	@Override
	public List<InfData> sqlToInfDataObjList(String sql) {
		logger.info("/* SQL sqlToInfDataObjList sql=*/ " + sql);
		List<InfData> infDataList = jdbcTemplate.query(sql, new RowMapper<InfData>() {
			public InfData mapRow(ResultSet rs, int rowNum) throws SQLException {
				ResultSetMetaData rsmd = rs.getMetaData();
				InfData infData = new InfData();
				infData.setId(rs.getString("id"));
				infData.setName(rs.getString("name"));
				JSONObject attributes = new JSONObject();
				for (int i = 1; i < rsmd.getColumnCount() + 1; i++) {
					if ((!(rsmd.getColumnName(i).equals("ID"))) && (!(rsmd.getColumnName(i).equals("NAME")))) {
						String colTypeName = rsmd.getColumnTypeName(i);
						attributes.put(rsmd.getColumnName(i).toUpperCase(),
								(rs.getObject(i) != null) ? getColumnValue(colTypeName,i,rs) : "");
					}
				}
				infData.setAttributes(attributes);

				return infData;
			}
		});
		return infDataList;
	}

	@Override
	public boolean checkIfColumnExists(String view, String column) {
		Map<String, String> metaDataMap = getMetaData("select * from " + view);
		for (Map.Entry<String, String> entry : metaDataMap.entrySet()) {
			if (entry.getKey().equalsIgnoreCase(column)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public JSONObject getJSONObjectOfDateTableUIReport(String sql, String hideEmptyColumns,
			Map<String, String> titleMap) {
//		logger.info("/* SQL getJSONObjectOfDateTable sql=*/ " + sql);
		String dataTableTopRowsNum = dataTableTopRowsNumDefault;
		JSONObject toReturn = new JSONObject();
		List<String> listOfMetaData;
		//		String paramMonitoring;
		//		int endIndexOf;
		try {
			List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

			/********Param Monitoring workaround*****************************************************************/

			//			endIndexOf = sql.indexOf(" ", sql.indexOf("from ") + "from ".length());
			//			paramMonitoring = (endIndexOf != -1) ? sql.substring(sql.indexOf("from ") + "from ".length(), endIndexOf)
			//					: sql.substring(sql.indexOf("from ") + "from ".length()); // get table from sql

			String paramCol = getParamCol(rows);
			if (!paramCol.isEmpty() && paramCol.endsWith("MONPARAM")) {
				JSONArray paramsArray = new JSONArray(), paramMonitoringNamesArray;
				JSONObject paramMonitoringNameJSONObject, tempObject, tempUomObject;
				String paramMonitoringValue;
				boolean flag;
				for (int i = 0; i < rows.size(); i++) {
					paramMonitoringNameJSONObject = new JSONObject(rows.get(i).get(paramCol).toString());
					paramMonitoringNamesArray = paramMonitoringNameJSONObject.names();
					if (paramMonitoringNamesArray == null) {
						continue;
					}
					for (int j = 0; j < paramMonitoringNamesArray.length(); j++) {
						paramMonitoringValue = paramMonitoringNamesArray.get(j).toString();
						if (paramMonitoringValue.contains("_uom")) {
							continue;
						}
						tempObject = new JSONObject(paramMonitoringNameJSONObject.get(paramMonitoringValue).toString());
						if (tempObject.get("val").equals("")) {
							continue;
						}
						flag = true;
						for (int k = 0; k < paramsArray.length(); k++) {
							if (paramsArray.get(k).equals(paramMonitoringNamesArray.get(j))) {
								flag = false;
								break;
							}
						}
						if (flag) {
							paramsArray.put(paramMonitoringNamesArray.get(j));
						}
					}
				}

				for (int i = 0; i < rows.size(); i++) {
					paramMonitoringNameJSONObject = new JSONObject(rows.get(i).get(paramCol).toString());
					rows.get(i).remove(paramCol);
					for (int j = 0; j < paramsArray.length(); j++) {
						tempObject = new JSONObject(paramMonitoringNameJSONObject.getString(paramsArray.getString(j)));
						tempUomObject = new JSONObject(
								paramMonitoringNameJSONObject.getString(paramsArray.getString(j) + "_uom"));
						if (tempObject.get("val").equals("")) {
							rows.get(i).put(paramsArray.getString(j), "");
							rows.get(i).put(paramsArray.getString(j) + "_uom", "");
						} else {
							rows.get(i).put(paramsArray.getString(j), tempObject.getString("val"));
							rows.get(i).put(paramsArray.getString(j) + "_uom", tempUomObject.getString("text"));
						}
					}
				}

			}
			/**************************************************************************************************/

			/******** DyanamicParams workaround *****************************************************************/

			else if (!paramCol.isEmpty() && paramCol.endsWith("DYNPARAM")) {
				JSONObject mobilePhaseComposNameJSONObject, jo;
				String val, uom, parentId = rows.get(0).get("PARENTID").toString();
				List<Map<String, Object>> lastDynamicParamsListOfMaps = getListOfMapsBySql(
						"select * from FG_DYNAMICPARAMS t where PARENT_ID = '" + parentId + "' order by ORDER_");
				Map<String, String> columnsMap = new HashMap<String, String>();
				for (int i = 0; i < lastDynamicParamsListOfMaps.size(); i++) {
					if (lastDynamicParamsListOfMaps.get(i).get("ACTIVE").toString().equals("1")) {
						columnsMap.put(lastDynamicParamsListOfMaps.get(i).get("ORDER_").toString(),
								lastDynamicParamsListOfMaps.get(i).get("LABEL").toString());
					}
				}
				for (int i = 0; i < rows.size(); i++) {
					mobilePhaseComposNameJSONObject = new JSONObject(rows.get(i).get(paramCol).toString());
					rows.get(i).remove(paramCol);
					for (Map.Entry<String, String> entry : columnsMap.entrySet()) {
						if (mobilePhaseComposNameJSONObject.has(String.valueOf(entry.getKey()))) {
							jo = mobilePhaseComposNameJSONObject.getJSONObject(entry.getKey());
							val = jo.getString("val");
							uom = jo.getString("uom");
						} else {
							val = "";
							uom = "";
						}
						if (entry.getKey().equals("0")) {
							rows.get(i).put(entry.getValue(), val);
							rows.get(i).put("UOM", uom);
						} else {
							rows.get(i).put(entry.getValue(),
									(val.equals("")) ? "" : val + generalUtilForm.getCurrrentIdInfo(uom).get("NAME"));
						}
					}
				}

				//sort by Time Point
				Collections.sort(rows, new Comparator<Map<String, Object>>() {
					public int compare(final Map<String, Object> o1, final Map<String, Object> o2) {
						return Integer.valueOf(o1.get("Time Point").toString())
								.compareTo(Integer.valueOf(o2.get("Time Point").toString()));
					}
				});

			}
			/**************************************************************************************************/
			if (hideEmptyColumns.equals("true")) {
				// remove columns with empty data
				removeEmptyColumnsFromRows(rows, dataTableTopRowsNum);
			}
			JSONArray jsonArrayOfColumns = getJSONArrayOfColumns(rows);

			Map<String, String> metaData = getMetaData(sql); //tableName
			metaData = getMetaDataFromJSONArrayOfColumns(metaData, jsonArrayOfColumns);
			listOfMetaData = getListOfNameAndTypeFromMetaData(metaData);
			List<String> dateList = getDateListFromMetaData(metaData);

			// replace names of columns to display names of columns
			jsonArrayOfColumns = convertJSONArrayOfColumnsToLabelNames(jsonArrayOfColumns, titleMap);

			JSONArray jsonArrayOfData = getJSONArrayOfData(rows, dateList, dataTableTopRowsNum);
			String t = "1";
			toReturn.put("columns", jsonArrayOfColumns);
			toReturn.put("data", jsonArrayOfData);
			toReturn.put("metaData", listOfMetaData);
		} catch (Exception e) {
			generalUtilLogger.logWrite(LevelType.ERROR, "SQL=[" + sql + "] error in sql!", "", ActivitylogType.SQLError,
					null, e);
			toReturn.put("columns", getJSONArrayOfColumns(null));
		}
		return toReturn;
	}

	private JSONArray convertJSONArrayOfColumnsToLabelNames(JSONArray columnsName, Map<String, String> titleMap) {
		JSONArray JSONArrayOfColumns = new JSONArray();
		JSONObject json = null;
		try {
			if ((columnsName != null) && (titleMap != null)) {
				for (int i = 0; i < columnsName.length(); i++) {
					json = new JSONObject();
					json.put("showPageFilter", columnsName.getJSONObject(i).get("showPageFilter"));
					json.put("title", titleMap.get(columnsName.getJSONObject(i).get("title")));
					json.put("id", columnsName.getJSONObject(i).get("title"));
					JSONArrayOfColumns.put(json);
				}
			} else {
				JSONArrayOfColumns = columnsName;
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return JSONArrayOfColumns;
	}

	//TODO:26122018 ab -> remove if not in use
	//kd 20032018 add this for use in case MONPARAM instead getJSONArrayOfColumns
	private JSONArray getJSONArrayOfColumnsMonPar(List<Map<String, Object>> rows, List<String> colAfterParm) {
		JSONArray JSONArrayOfColumns = new JSONArray();
		JSONObject json = null;
		HashSet<String> hs = new HashSet<String>();
		List<String> colNames = new ArrayList<>();
		int indexOfmonParamNull = -1;
		try {
			if ((rows != null) && (!rows.isEmpty())) {
				Set<String> colSet;
				for (int i = 0; i < rows.size(); i++) {
					colSet = rows.get(i).keySet();
					for (String col : colSet) {
						json = new JSONObject();
						json.put("showPageFilter", "0");
						/* 
						 * int _ind = col.indexOf("_SMART");
						String _suffix = (_ind > 0)?col.substring(_ind, col.length() -1):"";
						String _colTmp = (_ind > 0)?col.substring(0,_ind):col;
						col = generalUtil.getSpringMessagesByKey(_colTmp.replaceAll(" ", "_"), _colTmp);
						json.put("title", col+_suffix);
						** Also need to add code for change metaData for SMARTS columns
						*/
						col = col.replaceAll(".*@PIVOT_GROUP@", "");
						json.put("title", col);
						if (!col.endsWith("MONPARAM") && hs.add(col)) {//adib 030718 case of monparam column is null
							JSONArrayOfColumns.put(json);
							colNames.add(col);
						} else if (col.endsWith("MONPARAM")) {//adib 03/07/18 monparam column is null
							indexOfmonParamNull = i;
						}
					}
				}
				//adib 03/07/18 monparam column is null
				if (indexOfmonParamNull != -1) {
					Map<String, Object> tempRow = new HashMap<>();
					colSet = rows.get(indexOfmonParamNull).keySet();
					String colMonParam = "";
					boolean loopForMonParamExtention = false;
					int i = 0;
					for (String col : colSet) {
						if (loopForMonParamExtention) {
							//loop through the displayed columns until the col name is the same
							for (; !colNames.get(i).equals(col); i++) {
								rows.get(indexOfmonParamNull).put(colNames.get(i), "");
							}
							loopForMonParamExtention = false;
							break;
						} else {
							if (!colNames.get(i).equals(col)) {//there exist a column that is different from the displayed columns
								loopForMonParamExtention = true;//in the next loop of colSet , there is a need to loop through the displayed column internally
								colMonParam = col;
								i--;
							}
							i++;
						}
					}
					rows.get(indexOfmonParamNull).remove(colMonParam);
					/*for(Map.Entry<String,Object> entry :tempRow.entrySet()){
						rows.get(indexOfmonParamNull).put(entry.getKey(), entry.getValue());
					}*/
					for (String col_ : colAfterParm) {
						Object o_ = rows.get(indexOfmonParamNull).get(col_);
						rows.get(indexOfmonParamNull).remove(col_);
						rows.get(indexOfmonParamNull).put(col_, o_);
					}
				}
			} else {
				json = new JSONObject();
				json.put("showPageFilter", "0");
				json.put("title", "");
				JSONArrayOfColumns.put(json);
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return JSONArrayOfColumns;
	}

	@Override
	public int cloneTable(String table, String wherepart, Map<String, String> replaceFieldsMap) {
		int toReturn = 0;
		String sql_ = "";
		try {
			String colList = getTableColCsv(table);
			//changes the permanent columns to their new values 
			String colListVal = colList;
			if (replaceFieldsMap != null) {
				for (Map.Entry<String, String> entry : replaceFieldsMap.entrySet()) {
					String key = entry.getKey().toUpperCase();
					String value = entry.getValue();
					colListVal = colListVal.replace(key, value);
				}
			}

			//insert the cloned record to the table
			sql_ = String.format(" insert into %1$s (%2$s)  select %3$s from %1$s  t where %4$s", table, colList,
					colListVal, wherepart);
			updateSingleString(sql_);

			toReturn = 1;
		} catch (Exception e) {
			generalUtilLogger.logWrite(LevelType.ERROR, "SQL=[" + sql_ + "] error in sql!", "",
					ActivitylogType.SQLError, null, e);
		}

		return toReturn;

	}

	//return sql with error message for the data table
	@Override
	public String jsonSqlErrorMsg(String msg) {
		// TODO Auto-generated method stub
		return "select -1 as ID, '" + msg + "' as \"SQL Message\" from dual";
	}

	/**
	 * 
	 * @param name_ colum name
	 * @param ja_ JSONArray contains previous column name in the pivot data row
	 * @return unique column name 
	 */
	private String evalPivotColumnName(String name_, JSONArray ja_) {
		int counter = 0;
		for (int i = 0; i < ja_.length(); i++) {
			String colName_ = ja_.getJSONObject(i).get("origin_column").toString();
			if (colName_.equals(name_)) {
				counter++;
			}
		}
		if (counter != 0) {
			if (name_ != null && name_.contains("_SMART")) {
				name_ = name_.replace("_SMART", "." + counter + "_SMART");
			} else {
				name_ += "." + counter;
			}
		}
		return name_;
	}

	@Override
	public void useScheSQLTimeOut() {
		super.setScheSQLTimeOut();

	}

	@Override
	public void setDefaultSQLTimeOut() {
		super.setDefaultSQLTimeOut();
	}

	@Override
	public ResultSet getResultSet(Connection connection, String sql) {
		logger.info("/* SQL getResultSet sql=*/ " + sql);
		ResultSet rs = null;
		try {
			Statement stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
		} catch (Exception e) {
			System.out.println("Error getResultSet: " + e.toString());
		}

		return rs;
	}
	
	private Map<String, String> getActionRequestIconMap(String step_id) {
		Map<String, String> actionRequestIconMap = new HashMap<String, String>();
		try {
			Map<String, List<String>> requestDefaultHistory = getRequestDefaultHistory(step_id);

			String project_id = selectSingleStringNoException(
					"select t.PROJECT_ID from FG_S_step_V t where t.step_id ='" + step_id + "'");

			if (!project_id.equals("")) { // we will get empty project_id in new step - in this case the map is not needed
				String userId = generalUtil.getSessionUserId();
				String useAsDefault = selectSingleStringNoException(
						"select max(t.USEASDEFAULTDATA) from FG_S_REQUEST_V t where t.PROJECT_ID = '"
								+ generalUtil.getNull(project_id) + "' and t.CREATOR_ID = '" + userId + "'");
				
				List<String> actions = getListOfStringBySql(
						"select action_id from fg_s_action_v where step_id ='" + step_id + "'");
				
				for (String action_id : actions) {
					// +
					String defaultRequest = "{\"icon\":\"fa fa-plus-square\",\"funcName\":\"createNewRequest\",\"tooltip\":\"Create Request\",\"cellType\":\"link\",\"width\":\"100%\",\"params\":[\""
							+ action_id + "\", \"Action\"]}";
					List<String> default_hst = requestDefaultHistory.get(action_id);
					if (default_hst != null && default_hst.contains("default")) {
						// +
						defaultRequest = "{\"icon\":\"fa fa-plus-square\",\"funcName\":\"createNewRequest\",\"tooltip\":\"Create Request\",\"cellType\":\"link\",\"width\":\"100%\",\"params\":[\""
								+ action_id + "\", \"Action\"]}";
					} else if (default_hst != null && default_hst.contains("copy")) {
						// copy
						defaultRequest = "{\"icon\":\"fa fa-copy\",\"funcName\":\"createNewRequestWithData\",\"tooltip\":\"Copy Request\",\"cellType\":\"link\",\"width\":\"100%\",\"params\":[\""
								+ action_id + "\", \"Action\"]}";
					} else {
						if (generalUtil.getNull(useAsDefault).equals("1")) {
							// + & copy
							defaultRequest = "[{\"icon\":\"fa fa-plus-square\",\"funcName\":\"createNewRequest\",\"tooltip\":\"Create Request\",\"cellType\":\"link\",\"width\":\"100%\",\"params\":[\""
									+ action_id + "\", \"Action\""
									+ "]},{\"icon\":\"fa fa-copy\",\"funcName\":\"createNewRequestWithData\",\"tooltip\":\"Copy Request\",\"cellType\":\"link\",\"width\":\"100%\",\"params\":[\""
									+ action_id + "\", \"Action\"]}]";
						}
					}
					actionRequestIconMap.put(action_id, defaultRequest);
				}
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(LevelType.WARN,
					"Warn! (getActionRequestIconMap) for step_id=" + step_id, "", ActivitylogType.SQLError,
					null, e);
		}
		return actionRequestIconMap;
	}
	
	private Map<String, List<String>> getRequestDefaultHistory(String step_id) {
		Map<String, List<String>> requestDefaultHistory = new HashMap<String, List<String>>();
		String sql="select distinct listagg(t.DEFAULT_HST, ',') WITHIN GROUP (order by DEFAULT_HST) OVER (partition by t.PARENTID)as val,t.PARENTID as key_ from fg_s_request_v t,fg_s_action_v a where t.DEFAULT_HST is not null \r\n" + 
				"and t.PARENTID =a.action_id and a.STEP_ID ='"+step_id + "'";
		List<Map<String, String>> actionaData = getListOfMapsWithClob(sql);
		for(Map<String, String> data: actionaData){
			List<String> dataList = Arrays.asList(data.get("VAL").split(","));
			requestDefaultHistory.put(data.get("KEY_"),dataList);
		}
		return requestDefaultHistory;
	}

	@Override
	public Map<String, String> getMapsBySqlSingleRow(String sql) {
		logger.info("/* SQL getMapsBySqlSingleRow sql=*/ " + sql);
		Map<String, Object> sqlResultMap;
		Map<String, String> returnMap = new HashMap<String,String>();
		try {
			sqlResultMap = jdbcTemplate.queryForMap(sql);
			for (Map.Entry<String, Object> entry : sqlResultMap.entrySet()) {
				returnMap.put(entry.getKey(),entry.getValue() == null ? null : entry.getValue().toString());
			}

		} catch (Exception e) {
			generalUtilLogger.logWrite(LevelType.ERROR, "SQL=[" + sql + "] error in sql!", "", ActivitylogType.SQLError,
					null, e);
			returnMap = null;
			e.printStackTrace();
		}
		return returnMap;
	}
	
	@Override
	public JSONObject getJsonObjectBySqlSingleRow(String sql) {
		JSONObject toReturn = null;
		Map<String, String> sqlResultMap = getMapsBySqlSingleRow(sql);
		if(sqlResultMap != null) {
			toReturn = generalUtil.mapToJsonObject(sqlResultMap);
		}
		return toReturn;
	}
	
	private List<String> expSampleList(String step_id, StringBuilder internalQuery){
		List<String> dataList = new ArrayList<>();
		try{
			String protocolTypeId = formDao.getFromInfoLookup("Step", LookupType.ID	, step_id, "PROTOCOLTYPE_ID");
			String protocolTypeName = formDao.getFromInfoLookup("PROTOCOLTYPE", LookupType.ID, protocolTypeId, "name");
			String prep_run = formDao.getFromInfoLookup("Step", LookupType.ID, step_id, "PREPARATION_RUN");
			String exp_id=generalUtil.getNull(selectSingleStringNoException(" select experiment_id  from fg_s_step_v where formid='"+step_id + "'"));
			String sql = "";
			if(protocolTypeName.equals("Continuous Process") && prep_run.equals("Run")){
				String runnumber = selectSingleStringNoException("select runnumber from fg_s_step_v where step_id = '"+step_id+"'");
				sql = "select distinct sample_id,samplename\r\n "+
						" from fg_s_sample_v s\r\n"+
						" where step_id in \r\n"+
						" (select step_id from fg_s_step_v\r\n"+
						" where runnumber = '"+runnumber+"'\r\n"+
						" and experiment_id = '"+exp_id+"')";
			} else {	
				sql = "select distinct * from( \r\n" + 
						"  select m.SAMPLE_ID, m.SAMPLENAME\r\n" + 
						"  from fg_s_sampleselect_exp_mv m\r\n" + 
						"  where m.experiment_id  = '"+exp_id+"'\r\n" + 
						"  union all \r\n" + //-- YP FIX BUG 5919 - ADD THE ACTION THAT JUST CREATED FROM THE TABLE AND NOT EXIST YET IN THE fg_s_sampleselect_exp_mv MATERIAL VIEW
						"  select a.SAMPLE_ID, a.SAMPLENAME\r\n" + 
						"  from  fg_conn_sampleselect_step_act a\r\n" + 
						"  where a.experiment_ID = '"+exp_id+"') s\r\n" + 
						"  order by  s.SAMPLENAME";
				}
			List<Map<String, Object>> expData = getListOfMapsBySql(sql);
			internalQuery.append(" - sample mapping sql (full list): "+sql+"</br>");
			
			//[{"ID":"235679","VAL":"2085-02-005-0031-01"},{"ID":"235680","VAL":"2085-02-005-0031-02"},{"ID":"235681","VAL":"2085-02-005-0031-03"}]
			for (int i = 0; i < expData.size(); i++) {
				dataList.add("{\"ID\":\""+expData.get(i).get("SAMPLE_ID")+"\",\"VAL\":\""+expData.get(i).get("SAMPLENAME")+"\"}");
			}
		}
		catch(Exception e){
			generalUtilLogger.logWrite(LevelType.ERROR, "error in expSampleList(). sample list in action table", step_id.replace("'", ""), ActivitylogType.GeneralError,
					null, e);
			dataList = new ArrayList<>();
		}
		return dataList;
	}

	private Map<String, List<String>> actSampleList(String step_id, StringBuilder internalQuery){
		Map<String, List<String>> sample_action = new HashMap<String, List<String>>();
		try{
			String action_id = "";
			String sql = "select distinct * from\r\n"
					+ "(select t.sample_id,t.SAMPLENAME,t.PARENTID as ACTION_ID,a.FORMNUMBERID\r\n" + 
					" from fg_s_sampleselect_all_v t\r\n" + 
					" ,fg_s_action_v a\r\n" + 
					" where t.PARENTID = a.action_id\r\n" + 
					" and t.ACTIVE = 1\r\n" + 
					" and t.SESSIONID is null\r\n" + 
					" and a.step_id = "+ step_id+
					" \r\nunion all\r\n"
					+ "select t.sample_id,t.SAMPLENAME,to_char(a.action_id) as ACTION_ID,a.FORMNUMBERID\r\n" 
					+" from fg_s_sampleselect_all_v t\r\n" 
					+" ,fg_s_selftest_v s\r\n"
					+ ",fg_s_action_v a\r\n"
					+ " where t.PARENTID = s.selftest_id\r\n" 
					+ " and t.ACTIVE = 1\r\n" 
					+ " and t.SESSIONID is null\r\n"
					+ " and s.action_id = a.action_id\r\n"  
					+ " and a.step_id = "+ step_id
					+ "\r\n)order by  SAMPLENAME";
			List<String> dataList = new ArrayList<>();
			
			List<Map<String, Object>> actionRows = null;
			actionRows = getListOfMapsBySql(sql);
			
			internalQuery.append(" - sample mapping sql(selected sample): "+sql+"</br>"); 
			
			for (int i = 0; i < actionRows.size(); i++) {
				action_id = actionRows.get(i).get("ACTION_ID").toString();
				if(sample_action.get(action_id) == null){
					dataList.add("{\"ID\":\""+actionRows.get(i).get("SAMPLE_ID")+"\",\"displayName\":\""+actionRows.get(i).get("SAMPLENAME")+"\"}");
					sample_action.put(action_id, dataList);
				}
				else{
					dataList.add(generalUtil.listToCsv(sample_action.get(action_id)));
					dataList.add("{\"ID\":\""+actionRows.get(i).get("SAMPLE_ID")+"\",\"displayName\":\""+actionRows.get(i).get("SAMPLENAME")+"\"}");
					sample_action.put(action_id, dataList);
				}
				dataList = new ArrayList<>();
			}
		}catch(Exception e){
			generalUtilLogger.logWrite(LevelType.ERROR, "error in actSampleList(). sample list in action table", step_id.replace("'", ""), ActivitylogType.GeneralError,
					null, e);
			sample_action = new HashMap<String, List<String>>();
		}
		return sample_action;
	}
	
	/**
	 * Remove columns from display by condition
	 * @param rows
	 * @param condition
	 */
	@Override
	public List<Map<String, Object>> removeColumnsBeforeDisplay (List<Map<String, Object>> rows, String condition) {
		
		try {
			
			String smartActions = rows.get(0).get("SMARTACTIONS").toString();
			JSONObject json = new JSONObject(smartActions);
			if(!json.isNull("hideColumns")){
				JSONArray columnNames = new JSONArray();
				JSONObject hideColumnsObj = new JSONObject(json.getString("hideColumns"));
				if(!hideColumnsObj.isNull("condition") && hideColumnsObj.getString("condition").equalsIgnoreCase(condition)) {
					columnNames = hideColumnsObj.getJSONArray("cols");
				}
				if(columnNames.length() > 0)
				{
					for (int i = 0; i < rows.size(); i++) {
						
						for (int j = 0; j < columnNames.length(); j++) {
							String col = columnNames.getString(j);
							rows.get(i).remove(col);
						}
					}
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			generalUtilLogger.logWrite(LevelType.ERROR, "error in smartAction", "", ActivitylogType.GeneralError,null, e);
		}
		return rows;
	}

	@Override
	public void logMessage(String msg) {
		logger.info(msg);
	}
	
	private String getJsonDisplayObj(List<String> fullListVal,List<Map<String, String>> fullListIDVal,String colvalObj, String dbColName,String multiple,String additional) {
		JSONObject json = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			if (fullListVal != null) {
				for (String arg : fullListVal) {
					json = new JSONObject();
					json.put("ID", arg);
					json.put("VAL", arg);
					jsonArray.put(json);
				}
			}
			else if(fullListIDVal!= null) {
				for (Map<String, String> arg : fullListIDVal) {
					json = new JSONObject();
					json.put("ID", arg.get("ID"));
					json.put("VAL", arg.get("VAL"));
					jsonArray.put(json);
				}
			}
		} catch (Exception e) {
			jsonArray = new JSONArray();
		}
		String returnObj = "{}";
		List<String> dataList = new ArrayList<>();
		if (colvalObj != null) {
			String[] val = colvalObj.toString().split(",");
			List<String> colVals = Arrays.asList(val);
			for (String colVal : colVals) {
				dataList.add("{\"ID\":\"" + colVal + "\",\"displayName\":\"" + colVal + "\"}");
			}
			returnObj = "{\"displayName\":"+dataList.toString()+",\"htmlType\":\"select\", \"multiple\":\""+multiple+"\",\"dbColumnName\":\""+dbColName+"\", \"colCalcId\":\""+dbColName+"\", \"allowSingleDeselect\":\"false\""+generalUtil.getNull(additional)+", \"autoSave\":\"true\", \"fullList\":"+jsonArray.toString()+"}";
		}
		 else {
			 returnObj = "{\"displayName\":[],\"htmlType\":\"select\", \"multiple\":\""+multiple+"\",\"dbColumnName\":\""+dbColName+"\", \"colCalcId\":\""+dbColName+"\", \"allowSingleDeselect\":\"false\""+generalUtil.getNull(additional)+", \"autoSave\":\"true\", \"fullList\":"+jsonArray.toString()+"}";
		}
		return returnObj;
	}
	//	@Override
	//	public String updateSingleStringTask(String sql) {
	//		// TODO Auto-generated method stub
	//		return String.valueOf(jdbcTemplate.update(sql));
	//	}

	//	@Override
	//	public String selectSingleStringTask(String sql) {
	//		Object o = (String) jdbcTemplate.queryForObject(sql, String.class);
	//		return ((String)o);
	//	}
}