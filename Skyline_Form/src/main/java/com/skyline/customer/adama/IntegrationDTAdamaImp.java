package com.skyline.customer.adama;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.FormType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.SqlPermissionListObj;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.dal.SearchSqlDao;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilFormState;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.form.service.GeneralUtilPermission;
import com.skyline.form.service.IntegrationDT;

@Service
public class IntegrationDTAdamaImp implements IntegrationDT {
	
	@Autowired
	private GeneralUtil generalUtil;
	
	@Autowired
	private GeneralUtilFormState generalUtilFormState;
	
	@Autowired
	private GeneralDao generalDao;
	
	@Autowired
	private GeneralUtilPermission generalUtilPermission;
	
	@Autowired
	protected GeneralUtilLogger generalUtilLogger;
	
	@Autowired
	private SearchSqlDao searchSqlDao;
	
	@Value("${dataTableWithHint:MATERIALIZE}")
	private String dataTableWithHint;

	@Override
	public JSONObject onElementDataTableApiChange(String formId, long stateKey, String struct, FormType structFormType,
			String criteria, String display, String linkToLastSelection, String formCode, String tableType,
			String sourceElementImpCode, String hideEmptyColumns, String permissionSqlList, StringBuilder sqlInfo,
			List<String> unfilteredList, String lastMultiValues, boolean updateMultiValues, String followingHiddenCol) {
		// TODO Auto-generated method stub
		String sql = "";
		JSONObject toReturn = null;
		String formIdForSharedTables = "-1";
		String topRowsNum = "-1";
		String parentFormCode = "";
		String optionalAttributes = "";
		String optionalAttributes1 = "";
		String additionalOrder = "";
		StringBuilder sqlAdditionalInfoSb = new StringBuilder();
		try {
			String table = "";
			if (struct.equals("NA")) {
				table = display;
			} else {
				table = "fg_s_" + struct + "_" + display + "_v";
			}
			String userId = generalUtil.getSessionUserId();

			String idName = (struct.equals("NA") ? "ID" : struct + "_ID");

			sql = "select * from " + table + " where 1=1";
			String dateRange = generalUtilFormState.getFormParam(stateKey, formCode, "$P{CURRENT_DATERANGE}");
			 
			// by table
			if (generalUtil.getNull(table).equalsIgnoreCase("fg_r_general_dummy_sql_v")) { //Query Generator dummy sql - SQLGenerator screen
				topRowsNum = "1000000";
				sql = generalUtilFormState.getFormParam(stateKey, formCode)
						.get("$P{QUERY_TEXT}");
				if(sql == null) {
					sql = "fg_r_general_dummy_sql_v";
				}
				
				int numofcol = generalDao.getMetaData(sql).size();
				
				if(numofcol <= 1) {
					sql = "select -1 as hidden_col, tr.* from (" + sql + ") tr";
				} else {
					sql = "select tr.* from (" + sql + ") tr";
				}
			}

			// by form
			if (formCode.equals("Maintenance") && criteria.equals("Active") && !sql.isEmpty()) {
				if (generalDao.getMetaData(sql).containsKey("Active")) {
					sql += " and nvl(\"Active\",'Yes')= 'Yes' ";
				}
			}

			//			sql += citeriaWherePart;// + permissionScema;
			//			String citeriaWherePart = getrCiteriaWherePart((struct.equals("NA")?display:struct), (struct.equals("NA")?"ID":struct + "_ID"), criteria, userId, formCode, unfilteredList, lastMultiValues);
			String fullSql = "";
			String sqlScript = getrCiteriaWherePart(stateKey, (struct.equals("NA") ? display : struct), idName,
					criteria, userId, formCode, unfilteredList, lastMultiValues);

			SqlPermissionListObj permissionListSql = generalUtilPermission.getPermissionListSql(formCode,struct,table,userId);
			if (!generalUtil.getNull(sqlScript).isEmpty()) {
				//				if (!generalUtil.getNull(lastMultiValues).isEmpty()) {
				//					sqlScript = " and (" + idName + " in (" + sqlScript + ") ) ";
				if (!generalUtil.getNull(permissionListSql.getSql()).isEmpty()) {
					fullSql = "select * from (WITH CRITERIA_SQL AS ("
							+ sqlScript.replaceFirst("select ", "select /*+ " + dataTableWithHint + " */") + " )" + ", "
							+ " PERM_SQL_ALL AS ("+permissionListSql.getSql()+") " + sql 
							+ " and " + permissionListSql.getObjectId() + " in (SELECT * FROM PERM_SQL_ALL)" 
							+ " and " + idName + " in (SELECT * FROM CRITERIA_SQL)" + ") where 1=1";// +
																							// citeriaWherePart
				}
				else{
					fullSql = "select * from (WITH CRITERIA_SQL AS ("
							+ sqlScript.replaceFirst("select ", "select /*+ " + dataTableWithHint + " */") + " ) " + sql
							+ " and " + idName + " in (SELECT * FROM CRITERIA_SQL)) where 1=1";//+ citeriaWherePart
				}
				//				}  else {
				//					sqlScript = " and " + idName + " in (" + sqlScript + ")";
				//				}
			} else {
				if(!generalUtil.getNull(permissionListSql.getSql()).isEmpty()){
					fullSql = "select * from (WITH "
							+ " PERM_SQL_ALL AS ("+permissionListSql.getSql()+") " + sql 
							+ " and " + permissionListSql.getObjectId() + " in (SELECT * FROM PERM_SQL_ALL)"  + ") where 1=1";
				}else{
					fullSql = sql;
				}
			}

			if (!generalUtil.getNull(lastMultiValues).isEmpty()) {
				if (lastMultiValues.length() < 3990) { // because oracle instr varchar 4000 length limitation
					fullSql += " order by decode(instr('," + lastMultiValues + ",' , ',' || " + idName
							+ " || ','),0,0,1) desc "+(additionalOrder.isEmpty()?"":","+additionalOrder);
				}
			}

			// for element info ->
			sqlInfo.append(fullSql);
			if (table.equals("fg_r_formsearchInv_v") || table.equals("FG_R_FORMSEARCH_V")) {
				toReturn = searchSqlDao.getJSONObjectOfDataTable(fullSql, hideEmptyColumns, topRowsNum,
						table.equals("fg_r_formsearchInv_v") ? 0 : 1);
				if (toReturn.toString().contains("SearchFilesFailed")) {//search in files has failed
					generalUtilFormState.setFormParam(stateKey, formCode, "SEARCHINFILES", "0");
					return onElementDataTableApiChange(formId, stateKey, struct, structFormType, criteria, display,
							linkToLastSelection, formCode, tableType, sourceElementImpCode, hideEmptyColumns,
							permissionSqlList, sqlInfo, unfilteredList, lastMultiValues, updateMultiValues,
							followingHiddenCol);//show other result that are not from files
				}
			} else {
				toReturn = generalDao.getJSONObjectOfDateTable(fullSql, hideEmptyColumns, topRowsNum, sqlAdditionalInfoSb, optionalAttributes,optionalAttributes1);
			}

			//update the last selected rows to unselect if they are not in the full sql return data(with no criteria)
			if (updateMultiValues) {
				//JSONArray arr = new JSONArray(generalUtil.getJsonValById(toReturn.toString(), "columns"));
				//JSONArray data = new JSONArray(generalUtil.getJsonValById(toReturn.toString(), "data"));
				//String sqlNoCriteria = "select * from ("+sql+")";
				JSONObject dataNocriteria = generalDao.getJSONObjectOfDateTable(sql, hideEmptyColumns, topRowsNum,null);
				JSONArray arr = new JSONArray(generalUtil.getJsonValById(dataNocriteria.toString(), "columns"));
				JSONArray data = new JSONArray(generalUtil.getJsonValById(dataNocriteria.toString(), "data"));
				if (data.length() > 0) {
					String columnNameOfMultiValues = generalUtil
							.getJsonValById(arr.get(Integer.parseInt(followingHiddenCol)).toString(), "uniqueTitle");
					if (!columnNameOfMultiValues.isEmpty()) {
						String sqlNoCriteria = "select " + columnNameOfMultiValues + " from (" + sql + ")";
						List<String> fullData = generalDao.getListOfStringBySql(sqlNoCriteria);
						if (fullData != null) {
							List<String> similar = new ArrayList<String>();
							similar.addAll(Arrays.asList(lastMultiValues.split(",")));
							List<String> different = new ArrayList<String>();
							different.addAll(similar);
							similar.retainAll(fullData);
							lastMultiValues = generalUtil.listToCsv(similar);
						}
					} else {
						lastMultiValues = "";
					}
				} else {
					lastMultiValues = "";
				}
			}

			sqlInfo.append(sqlAdditionalInfoSb==null?"":sqlAdditionalInfoSb);
			
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.ERROR,
					"DT table error! sourceElementImpCode=" + sourceElementImpCode + ", struct=" + struct + ", display=" + display + ", e=" + e + " sql=" + sql,
					ActivitylogType.DataTable, formId, e, null);
			e.printStackTrace();
			String errMessage = "General error in data table";
			// toReturn.put("columns", getJSONArrayOfColumns(null));

			toReturn = generalDao.getJSONObjectOfDateTable(generalDao.jsonSqlErrorMsg(errMessage), "", "-1", null);
		}
		toReturn.put("formIdForShared", formIdForSharedTables);
		toReturn.put("lastMultiValues", lastMultiValues);
		return toReturn;
	}

	@Override
	public JSONObject onLevelSelectedChange(String struct, String formCode, String displayCatalog, String elementCode) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getrCiteriaWherePart(long stateKey, String struct, String idName, String criteria, String userId,
			String formCode, List<String> unfilteredList, String lastMultiValues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getUserInfoMap(String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String checkRemove(String struct, String formId, String userId, String rowId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String checkRemoveConfirm(String struct, String formId, String userId, String action,
			StringBuilder errorMessage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String onChangeDataTableCell(long stateKey, String parentFormCode, String formId, String formCode,
			String onChangeFormId, String userId, String onChangeColumnName, String onChangeColumnVal, String saveType,
			String formNumberId, String oldVal) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String dataTableAddRow(long stateKey, String formCode, String formId, String userId, String parentFormCode,
			String domId, Map<String, String> elementValueMap, int rowNumToAdd, String tableType) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRichTextContent(String parentID, String formCode, String dbColName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, String>> customCriteriaList(String elementCode, String struct, long stateKey,
			List<Map<String, String>> sqlPoolMapList, Map<String, String> parentMap,
			Map<String, String> currentFormMap) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JSONObject customerDTDisplayViewList(String fromcode, String elementCode, String struct,
			String displayCatalogItemDefaulValue, String lastDTView, boolean isLevelChange) {
		// TODO Auto-generated method stub
		JSONObject returnObj = new JSONObject();
		String selectHtml = "";
		String selectVal = "";
		String options = null;
		StringBuilder sb = new StringBuilder();
		
		//System.out.println("customerDTViewList formCode = " + fromCode + ", elementCode = " + elementCode + ", struct=" + struct + ",lasdDTView=" +  lastDTView + ", displayCatalogItemDefaulValue=" + displayCatalogItemDefaulValue );
		// **** set list options by form code ****
		if(fromcode.equals("xxxx")) {
			if(elementCode.equals("upperTable")) {
				options = "DT,DT Nmae;DTM,DTM Name";
			}
		}
		//**** set list options DONE! ****
		
		if(options != null) {
			String[] optionsArray  = options.split(";");
			for(String option : optionsArray){	
				String id = option.split(",")[0];
				String text = option.split(",")[1];
				if(lastDTView.equals(id)) {
					selectVal = lastDTView;
					sb.append("<option selected value=\""+id+"\">"+text+"</option>\n");
				} else {
					sb.append("<option value=\""+id+"\">"+text+"</option>\n");
				}
			}
			
			if(selectVal.isEmpty()) { // if lastDTView is not part of the list we take displayCatalogItemDefaulValue defined in the form builder and add as the first selection
				selectVal = displayCatalogItemDefaulValue;
				selectHtml = "<option selected value=\""+displayCatalogItemDefaulValue+"\">Default</option>\n" + sb.toString();
			} else {
				selectHtml = sb.toString();
			}
		} else {
			selectVal = displayCatalogItemDefaulValue;
			selectHtml = "<option selected value=\""+displayCatalogItemDefaulValue+"\">Default</option>\n";
		}
		
		returnObj.put("selectHtml", selectHtml);
		returnObj.put("selectVal", selectVal);
		
		return returnObj;
	}

	@Override
	public String customerDTDefaultHiddenColumns(String formCode, String impCode, String struct) {
		// TODO Auto-generated method stub
		return "";
	}

	
}
