package com.skyline.customer.adama;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.Form;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.LookupType;
import com.skyline.form.bean.ValidationCode;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.FormSaveDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.dal.UploadFileDao;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilCalc;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.form.service.IntegrationCloneAndSplit;
import com.skyline.form.service.IntegrationValidation;

@Service
public class IntegrationCloneAnsSplitAdamaImp implements IntegrationCloneAndSplit {

	@Autowired
	private CommonFunc commonFunc;

	@Autowired
	private FormIdCalc formIdCalc;

	@Autowired
	private FormDao formDao;

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private GeneralUtilCalc generalUtilCalc;

	//	@Autowired
	//	private FormTempData formTempData;

	@Autowired
	private GeneralDao generalDao;

	@Autowired
	private FormSaveDao formSaveDao;

	//	@Autowired
	//	private CacheService cachService;

	@Autowired
	protected UploadFileDao uploadFileDao;

	@Autowired
	private IntegrationValidation integrationValidation;

	//	@Autowired
	//	private ChemDao marvinService;

	@Autowired
	protected GeneralUtilLogger generalUtilLogger;

	@Autowired
	private CloneExperiment cloneExperiment;

	@Override
	public void postCloneSaveEvent(String formId, String cloneFormId) {
		String formCode = formDao.getFormCodeBySeqId(formId);
		//		String table="FG_S_"+formCode+"_PIVOT";
		//get table name
		Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
		String formCodeEntity = generalUtil.getNull(form.getFormCodeEntity(), formCode);
		String table = "FG_S_" + formCodeEntity + "_PIVOT";

		if (formCode.equals("InvItemBatch")) {
			//update Quantity,MinStock,Comments to be empty in the cloned record
			String sql_ = String.format(
					" update %1$s set QUANTITY =%2$s, MINSTOCKLEVEL= %2$s, COMMENTS= %2$s, QUANTUPDATEDATE = TO_CHAR(sysdate,'"
							+ generalUtil.getConversionDateFormat() + "'),INUSEDEPLETED = '%4$s'  where FORMID =  %3$s",
					table, "NULL", cloneFormId,"In Use");
			List<String> colList = Arrays.asList("QUANTITY", "MINSTOCKLEVEL", "COMMENTS", "QUANTUPDATEDATE","INUSEDEPLETED");
			formSaveDao.updateStructTableByFormId(sql_, table, colList, cloneFormId);
			//updateQuantityChangeDate(null, cloneFormId);
		} else if (formCode.equals("InvItemMaterial")) {
			String sql_ = String.format(
					" update %1$s set invitemmaterialname=%2$s , CASNUMBER = %2$s, xcoordinateinmatrix = %2$s, ycoordinateinmatrix = %2$s where FORMID =  %3$s",
					table, "NULL", cloneFormId);
			List<String> colList = Arrays.asList("invitemmaterialname", "CASNUMBER");
			formSaveDao.updateStructTableByFormId(sql_, table, colList, cloneFormId);
		} else if (formCode.equals("InvItemMaterialPr")) {
			String sql_ = String.format(
					" update %1$s set invitemmaterialname=%2$s  where FORMID =  %3$s",
					table, "NULL", cloneFormId);
			List<String> colList = Arrays.asList("invitemmaterialname");
			formSaveDao.updateStructTableByFormId(sql_, table, colList, cloneFormId);
			
			// insert
						String userId = generalUtil.getSessionUserId();
						String[] tablesRefList= {"MaterialComponent"};
						for (int i = 0; i < tablesRefList.length; i++) {
							String tableRef="fg_s_"+tablesRefList[i]+"_pivot";
							String sql = "";
							String colListRef = "";
							
							// insert
							colListRef = "," + generalDao.getTableColCsv(tableRef) + ",";
							colListRef = colListRef.replace(",CLONEID,", ",").replace(",PARENTID,", ",");
							colListRef = colListRef.substring(1, colListRef.length() - 1);
							//valList = valList.substring(1, valList.length() - 1);
							sql = String.format(
									"insert into %1$s r (%2$s ,CLONEID, PARENTID )  select %3$s, fg_get_struct_form_id(r.formcode), %5$s "
											+ " from %6$s r where parentid="+formId+" and  sessionid is null and nvl(active,'1')='1' "
											,
									tableRef, colList, colList, tableRef, cloneFormId, tableRef);
							formSaveDao.updateSingleStringInfoNoTryCatch(sql);		

							// update formId to clone Id
							sql = String.format("update  %1$s r set r.formid = r.cloneid ,r.TIMESTAMP=sysdate,r.creation_date=sysdate,r.CHANGE_BY=%2$s,r.created_by=%3$s"
									+ " where r.cloneid <> r.formid and r.cloneid is not null", tableRef,userId,userId);
							formSaveDao.updateSingleStringInfoNoTryCatch(sql);
						}
				
		
		} else if (formCode.equals("InvItemMaterialFr")||formCode.equals("InvItemMaterialPr")) {
			String sql_ = String.format(
					" update %1$s set invitemmaterialname=%2$s  where FORMID =  %3$s",
					table, "NULL", cloneFormId);
			List<String> colList = Arrays.asList("invitemmaterialname");
			formSaveDao.updateStructTableByFormId(sql_, table, colList, cloneFormId);
			
			// insert
						String userId = generalUtil.getSessionUserId();
						String[] tablesRefList= {"MaterialComponent"};
						for (int i = 0; i < tablesRefList.length; i++) {
							String tableRef="fg_s_"+tablesRefList[i]+"_pivot";
							String sql = "";
							String colListRef = "";
							
							// insert
							colListRef = "," + generalDao.getTableColCsv(tableRef) + ",";
							colListRef = colListRef.replace(",CLONEID,", ",").replace(",PARENTID,", ",");
							colListRef = colListRef.substring(1, colListRef.length() - 1);
							//valList = valList.substring(1, valList.length() - 1);
							sql = String.format(
									"insert into %1$s r (%2$s ,CLONEID, PARENTID )  select %3$s, fg_get_struct_form_id(r.formcode), %5$s "
											+ " from %6$s r where parentid="+formId+" and  sessionid is null and nvl(active,'1')='1' "
											,
									tableRef, colListRef, colListRef, tableRef, cloneFormId, tableRef);
							formSaveDao.updateSingleStringInfoNoTryCatch(sql);		

							// update formId to clone Id
							sql = String.format("update  %1$s r set r.formid = r.cloneid ,r.TIMESTAMP=sysdate,r.creation_date=sysdate,r.CHANGE_BY=%2$s,r.created_by=%3$s"
									+ " where r.cloneid <> r.formid and r.cloneid is not null", tableRef,userId,userId);
							formSaveDao.updateSingleStringInfoNoTryCatch(sql);
						}
				
	} else if (formCode.equals("RecipeFormulation")) {
		String sql_ = String.format(
				" update %1$s set STATUS_ID=%2$s , LASTSTATUS_ID = %2$s, origin = %3$s, creationDate = TO_CHAR(sysdate,'" +  generalUtil.getConversionDateFormat() + "') where FORMID =  %4$s",
				table, formDao.getFromInfoLookup("RecipeStatus", LookupType.NAME, "Planned", "id"),"'Manual'", cloneFormId);
		List<String> colList = Arrays.asList("STATUS_ID", "LASTSTATUS_ID","origin","creationDate");
		formSaveDao.updateStructTableByFormId(sql_, table, colList, cloneFormId);
		
		String userId = generalUtil.getSessionUserId();
		String[] tablesRefList= {"composition"};
		for (int i = 0; i < tablesRefList.length; i++) {
			String tableRef="fg_s_"+tablesRefList[i]+"_pivot";
			String sql = "";
			String colListRef = "";
			
			// insert
			colListRef = "," + generalDao.getTableColCsv(tableRef) + ",";
			colListRef = colListRef.replace(",CLONEID,", ",").replace(",PARENTID,", ",");
			colListRef = colListRef.substring(1, colListRef.length() - 1);
			//valList = valList.substring(1, valList.length() - 1);
			sql = String.format(
					"insert into %1$s r (%2$s ,CLONEID, PARENTID )  select %3$s, fg_get_struct_form_id(r.formcode), %5$s "
							+ " from %6$s r where parentid="+formId+" and  sessionid is null and nvl(active,'1')='1' "
							,
					tableRef, colListRef, colListRef, tableRef, cloneFormId, tableRef);
			formSaveDao.updateSingleStringInfoNoTryCatch(sql);		

			// update formId to clone Id
			sql = String.format("update  %1$s r set r.formid = r.cloneid ,r.TIMESTAMP=sysdate,r.creation_date=sysdate,r.CHANGE_BY=%2$s,r.created_by=%3$s"
					+ " where r.cloneid <> r.formid and r.cloneid is not null", tableRef,userId,userId);
			formSaveDao.updateSingleStringInfoNoTryCatch(sql);
		}
	}
		else if (formCode.equals("InvItemColumn")) {
			String sql_ = String.format(
					" update %1$s set serialNumber= %2$s,batchNumber=%2$s,inUse_disabled='In Use', INVITEMCOLUMNNAME = %2$s where FORMID =  %3$s",
					table, "NULL", cloneFormId);
			List<String> colList = Arrays.asList("INVITEMCOLUMNNAME", "serialNumber", "batchNumber", "inUse_disabled");
			String userId = generalUtil.getSessionUserId();
			cloneExperiment.insertRef("Document", formId, userId, "InvItemColumn", cloneFormId, "MSDS", null, "");
			formSaveDao.updateStructTableByFormId(sql_, table, colList, cloneFormId);
		} else if (formCode.equals("InvItemInstrument")) {
			String sql_ = String.format(
					" update %1$s set SERIALNUMBER= %2$s, CALIBRATIONDATE= %2$s, CALIBTYPE_ID= %2$s where FORMID =  %3$s",
					table, "NULL", cloneFormId);
			List<String> colList = Arrays.asList("SERIALNUMBER", "CALIBRATIONDATE", "CALIBTYPE_ID");
			formSaveDao.updateStructTableByFormId(sql_, table, colList, cloneFormId);
		} else if (formCode.equals("FormulantRef")) {
			String sql_ = String.format(" update %1$s set batchnumber=%2$s  where FORMID =  %3$s", table, "NULL",
					cloneFormId);
			List<String> colList = Arrays.asList("batchnumber");
			formSaveDao.updateStructTableByFormId(sql_, table, colList, cloneFormId);
		}
		else if (formCode.equals("PermissionScheme")) {
			
			// insert
			String userId = generalUtil.getSessionUserId();
			String[] tablesRefList= {"permissionsref","usersCrew","GroupsCrew"};
			for (int i = 0; i < tablesRefList.length; i++) {
				String tableRef="fg_s_"+tablesRefList[i]+"_pivot";
				String sql = "";
				String colList = "";
				
				// insert
				colList = "," + generalDao.getTableColCsv(tableRef) + ",";
				colList = colList.replace(",CLONEID,", ",").replace(",PARENTID,", ",");
				colList = colList.substring(1, colList.length() - 1);
				//valList = valList.substring(1, valList.length() - 1);
				sql = String.format(
						"insert into %1$s r (%2$s ,CLONEID, PARENTID )  select %3$s, fg_get_struct_form_id(r.formcode), %5$s "
								+ " from %6$s r where parentid="+formId+" and  sessionid is null and nvl(active,'1')='1' "
								,
						tableRef, colList, colList, tableRef, cloneFormId, tableRef);
				formSaveDao.updateSingleStringInfoNoTryCatch(sql);		

				// update formId to clone Id
				sql = String.format("update  %1$s r set r.formid = r.cloneid ,r.TIMESTAMP=sysdate,r.creation_date=sysdate,r.CHANGE_BY=%2$s,r.created_by=%3$s"
						+ " where r.cloneid <> r.formid and r.cloneid is not null", tableRef,userId,userId);
				formSaveDao.updateSingleStringInfoNoTryCatch(sql);
			}
		}
	else if (formCode.equals("MaterialFunction")) {
			
			// insert
			String userId = generalUtil.getSessionUserId();
			String[] tablesRefList= {"FunctionRuleRef"};
			for (int i = 0; i < tablesRefList.length; i++) {
				String tableRef="fg_s_"+tablesRefList[i]+"_pivot";
				String sql = "";
				String colList = "";
				
				// insert
				colList = "," + generalDao.getTableColCsv(tableRef) + ",";
				colList = colList.replace(",CLONEID,", ",").replace(",PARENTID,", ",");
				colList = colList.substring(1, colList.length() - 1);
				//valList = valList.substring(1, valList.length() - 1);
				sql = String.format(
						"insert into %1$s r (%2$s ,CLONEID, PARENTID )  select %3$s, fg_get_struct_form_id(r.formcode), %5$s "
								+ " from %6$s r where parentid="+formId+" and  sessionid is null and nvl(active,'1')='1' "
								,
						tableRef, colList, colList, tableRef, cloneFormId, tableRef);
				formSaveDao.updateSingleStringInfoNoTryCatch(sql);		

				// update formId to clone Id
				sql = String.format("update  %1$s r set r.formid = r.cloneid ,r.TIMESTAMP=sysdate,r.creation_date=sysdate,r.CHANGE_BY=%2$s,r.created_by=%3$s"
						+ " where r.cloneid <> r.formid and r.cloneid is not null", tableRef,userId,userId);
				formSaveDao.updateSingleStringInfoNoTryCatch(sql);
			}
		}
	}

	@Override
	public String MultiCloneSaveEvent(String formId, String cloneQuantity) {
		String formCode = formDao.getFormCodeBySeqId(formId);
		//		String table = "FG_S_"+formCode+"_PIVOT";
		//get table name
		Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
		String formCodeEntity = generalUtil.getNull(form.getFormCodeEntity(), formCode);
		String table = "FG_S_" + formCodeEntity + "_PIVOT";

		Map<String, String> mapResult = generalDao.getMetaDataRowValues(
				String.format("select INVITEMBATCHNAME,SOURCE_ID from %1$s where FORMID =  %2$s", table, formId));//FORMNUMBERID
		String sourceInEx = formDao.getFromInfoLookup("source", LookupType.ID, mapResult.get("SOURCE_ID"), "name");
		if (sourceInEx.equals("External") && mapResult.get("INVITEMBATCHNAME").endsWith("B1")) {//if it's external number batch and end with B1, the can be cloned
			for (int i = 0; i < Integer.parseInt(cloneQuantity); i++) {
				String batchNumber = getNextClonedBatchNumber(formCode, mapResult.get("INVITEMBATCHNAME"), formId,
						"External");
				String cloneFormId = formSaveDao.cloneStructTable(formId);
				String sql_ = String.format(
						" update %1$s set ACTIVE = 1, INVITEMBATCHNAME = '%4$s', FORMNUMBERID = '%4$s' , QUANTITY =%2$s, MINSTOCKLEVEL= %2$s, COMMENTS= %2$s, QUANTUPDATEDATE = TO_CHAR(sysdate,'"
								+ generalUtil.getConversionDateFormat() + "'),INUSEDEPLETED='%5$s'  where FORMID =  %3$s",
						table, "NULL", cloneFormId, batchNumber,"In Use");
				List<String> colList = Arrays.asList("ACTIVE", "INVITEMBATCHNAME", "FORMNUMBERID", "QUANTITY",
						"MINSTOCKLEVEL", "COMMENTS", "QUANTUPDATEDATE","INUSEDEPLETED");
				formSaveDao.updateStructTableByFormId(sql_, table, colList, cloneFormId);
				formId = cloneFormId;
			}
			return "1";
		} else {
			generalUtilLogger.logWriter(LevelType.WARN,
					"User tried to clone invalid batch. Cloned batch has to be external type and with suffix B1",
					ActivitylogType.SaveEvent, formId);
			return "-1";
		}
	}

	@Override
	public String splitSaveEvent(String formId, String currentQuantity, String splitQuantity, String splitQuantityUom)
			throws Exception {
		String formCode = formDao.getFormCodeBySeqId(formId);
		//		String table="FG_S_"+formCode+"_PIVOT";
		//get table name
		Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
		String formCodeEntity = generalUtil.getNull(form.getFormCodeEntity(), formCode);
		String table = "FG_S_" + formCodeEntity + "_PIVOT";

		Map<String, String> mapResult = generalDao.getMetaDataRowValues(String.format(
				"select INVITEMBATCHNAME, SOURCE_ID, INUSEDEPLETED from %1$s where FORMID =  %2$s", table, formId));//FORMNUMBERID
		//String sourceInEx = formDao.getFromInfoLookup("source", LookupType.ID, mapResult.get("SOURCE_ID"), "name");

		String cloneFormId = "1";
		if (mapResult.get("INUSEDEPLETED").equals("In Use")) {
			Map<String, String> srcBatchInf = generalDao.sqlToHashMap(
					"select QUANTITY,QUANTITYUOM_ID from FG_S_INVITEMBATCH_PIVOT where formId = '" + formId + "'");
			Double normalSrcQuantity = generalUtilCalc.getNormalNumber(
					generalUtil.getEmpty(srcBatchInf.get("QUANTITY"), "0"), srcBatchInf.get("QUANTITYUOM_ID"), 0D);
			Double normalConsumeQuantity = generalUtilCalc.getNormalNumber(generalUtil.getEmpty(splitQuantity, "0"),
					splitQuantityUom, 0D);
			integrationValidation.validate(ValidationCode.SPLITED_FAILED, "INVITEMBATCH", formId,
					new String(normalConsumeQuantity.toString() + "," + normalSrcQuantity.toString()),
					new StringBuilder());
			cloneFormId = formSaveDao.cloneStructTable(formId);
			postSplitSaveEvent(formId, cloneFormId, currentQuantity, splitQuantity, splitQuantityUom);
		} else {
			generalUtilLogger.logWriter(LevelType.DEBUG,
					"User tried to split a depleted batch. Split batch has to be an existing batch only",
					ActivitylogType.SaveEvent, formId);
			return "-1";
		}
		return cloneFormId;

	}

	@Override
	public Map<String, String> cloneRemoveFields(String formId, String cloneFormId) {
		// TODO Auto-generated method stub
		Map<String, String> cloneRemoveFieldsMap;
		String formCode = formDao.getFormCodeBySeqId(formId);
		switch (formCode) {
			case "InvItemInstrument":
				cloneRemoveFieldsMap = new HashMap<>();
				cloneRemoveFieldsMap.put("SERIALNUMBER", "NULL");
				cloneRemoveFieldsMap.put("STATUS_ID", "NULL");
				break;
			case "InvItemMaterial":
				cloneRemoveFieldsMap = new HashMap<>();
				cloneRemoveFieldsMap.put("InvItemMaterialName", "NULL");
				break;
			case "InvItemMaterialFr":
				cloneRemoveFieldsMap = new HashMap<>();
				cloneRemoveFieldsMap.put("InvItemMaterialName", "NULL");
				break;
			case "InvItemMaterialPr":
				cloneRemoveFieldsMap = new HashMap<>();
				cloneRemoveFieldsMap.put("InvItemMaterialName", "NULL");
				break;
			case "InvItemColumn":
				cloneRemoveFieldsMap = new HashMap<>();
				cloneRemoveFieldsMap.put("InvItemColumnName", "NULL");
				break;
			case "PermissionScheme":
				cloneRemoveFieldsMap = new HashMap<>();
				cloneRemoveFieldsMap.put("permissionSchemeName", "NULL");
				break;
			case "MaterialFunction":
				cloneRemoveFieldsMap = new HashMap<>();
				cloneRemoveFieldsMap.put("MaterialFunctionName", "NULL");
				break;
			case "RecipeFormulation":
				cloneRemoveFieldsMap = new HashMap<>();
				cloneRemoveFieldsMap.put("EXPERIMENT_ID", "NULL");
				cloneRemoveFieldsMap.put("approvalDate", "NULL");
			    cloneRemoveFieldsMap.put("STATUS_ID","NULL");
				cloneRemoveFieldsMap.put("LASTSTATUS_ID","NULL");
				cloneRemoveFieldsMap.put("creationDate","NULL");
				cloneRemoveFieldsMap.put("origin", "NULL");
				cloneRemoveFieldsMap.put("APPROVER_ID", "NULL");
				cloneRemoveFieldsMap.put("LABORATORY_ID", "NULL");
				cloneRemoveFieldsMap.put("version", "NULL");
				cloneRemoveFieldsMap.put("externalCode", "NULL");
				cloneRemoveFieldsMap.put("exportToDataBank", "NULL");
				cloneRemoveFieldsMap.put("db_recipeName", "NULL");
				cloneRemoveFieldsMap.put("description", "NULL");
				cloneRemoveFieldsMap.put("reasonForChange", "NULL");
				cloneRemoveFieldsMap.put("CREATOR_ID", "NULL");
			
				
				break;
			default:
				cloneRemoveFieldsMap = null;
		}
		return cloneRemoveFieldsMap;
	}

	@Override
	/**
	 * Note: update Quantity in formId & cloneFormId, update the cloneFormId record to active = 1 (from -<cloneFormId>)
	 */
	public String postSplitSaveEvent(String formId, String cloneFormId, String currentQuantity, String splitQuantity,
			String splitQuantityUom) throws Exception {
		String userId = generalUtil.getSessionUserId();
		String formCode = formDao.getFormCodeBySeqId(formId);
		//		String table="FG_S_"+formCode+"_PIVOT";
		//get table name
		Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
		String formCodeEntity = generalUtil.getNull(form.getFormCodeEntity(), formCode);
		String table = "FG_S_" + formCodeEntity + "_PIVOT";

		if (formCode.equals("InvItemBatch")) {
			String sourceInEx = formDao.getFromInfoLookup(formCode, LookupType.ID, formId, "SOURCE_ID");
			String sourceName = formDao.getFromInfoLookup("Source", LookupType.ID, sourceInEx, "name");
			String batchNumber = "";
			Map<String, String> elementValueMap = new HashMap<String, String>();
			elementValueMap.put("SOURCE_ID", sourceInEx);
			batchNumber = formDao.getFromInfoLookup(formCode, LookupType.ID, formId, "NAME");

			if (sourceName.equals("External")) {
				String nextFormNumberId = formIdCalc.getNextId(elementValueMap, formCode, formId, userId,
						"fg_s_invitembatch_pivot");
				if (nextFormNumberId.equals("")) {
					return "-1";
				}
				if (!nextFormNumberId.isEmpty()) {
					batchNumber = "0000-" + nextFormNumberId + "-B" + batchNumber.split("B")[1];
				}
			} else {
				batchNumber = batchNumber.split("B")[0] + "B"
						+ String.valueOf(Integer.parseInt(batchNumber.split("B")[1]) + 1);
			}

			//update Quantity in the original record(formId)
			commonFunc.postUseOfBatch(splitQuantity, splitQuantityUom, formId, "",
					new StringBuilder());
			//updateQuantityChangeDate(null, formId);

			//			String user = formDao.getFromInfoLookup("user", LookupType.ID, userId, "name");

			//insert data into activityLog
			Double quantity = Double.parseDouble(currentQuantity) - Double.parseDouble(splitQuantity);
			//ActivityLog activityLogEntity = new ActivityLog(ActivitylogType.Depletion,formId,dateFormat.format(date),user,Integer.toString(quantity),null,null,null,null,null,null);
			//		ActivityLog activityLogEntity = new ActivityLog(ActivitylogType.Depletion,formId,"TO_DATE(sysdate)",user,Integer.toString(quantity),"","","''","''","","");
			//		insertIntoActivityLog(activityLogEntity);
			Map<String, String> infoMap = new HashMap<String, String>();
			infoMap.put("quantity", Double.toString(quantity));
			generalUtilLogger.logWrite(LevelType.Other, "", cloneFormId, ActivitylogType.Depletion, infoMap);

			//update Quantity in the cloned record(cloneFormId)
			String sql_ = String.format(
					" update %1$s set %4$s = '%5$s',FORMNUMBERID = '%5$s', QUANTITY ='%2$f',ACTIVE = 1,QUANTUPDATEDATE = TO_CHAR(sysdate,'"
							+ generalUtil.getConversionDateFormat() + "') where FORMID =  %3$s",
					table, Double.parseDouble(splitQuantity), cloneFormId, "INVITEMBATCHNAME", batchNumber);
			List<String> colList = Arrays.asList("INVITEMBATCHNAME", "QUANTITY", "FORMNUMBERID", "ACTIVE",
					"QUANTUPDATEDATE");
			formSaveDao.updateStructTableByFormId(sql_, table, colList, cloneFormId);

			//insert data into activityLog
			//		activityLogEntity = new ActivityLog(ActivitylogType.Registration,cloneFormId,"TO_DATE(sysdate)",user,splitQuantity,"","","''","''","","");
			//		insertIntoActivityLog(activityLogEntity);
			infoMap = new HashMap<String, String>();
			infoMap.put("splitQuantity", splitQuantity);
			generalUtilLogger.logWrite(LevelType.Other, "", cloneFormId, ActivitylogType.Registration, infoMap);
		}

		return "1";
	}

	private String getNextClonedBatchNumber(String formCode, String clonedBatchNumber, String formId, String source) {
		String table = "FG_S_" + formCode + "_PIVOT";
		String columnName = "INVITEMBATCHNAME";
		String currentBatchNumber = generalDao.selectSingleString(String.format("select * from (select " + columnName
				+ " from  %1$s where " + columnName + " like '%2$sB%%' order by TO_NUMBER(substr(" + columnName
				+ ",instr(" + columnName + ",'B')+1)) desc) where rownum=1", table, clonedBatchNumber.split("B")[0]));//FORMNUMBERID
		String nextFormNumber = "";
		if (formCode.equals("InvItemBatch")) {
			if (currentBatchNumber != null) {
				String[] formNumberParts = currentBatchNumber.split("B");
				if (formNumberParts.length > 1) {//the formNumber can be numbered automatically
					nextFormNumber = formNumberParts[0] + "B"
							+ String.valueOf((Integer.parseInt(formNumberParts[1]) + 1));
				} else {
					generalUtilLogger.logWriter(LevelType.DEBUG,
							"Batch Number Format Error! There was found batch number value: " + currentBatchNumber,
							ActivitylogType.SaveEvent, formId);
				}
			}
		}
		return nextFormNumber;
	}

	@Override
	public String doCloneBySaveDefaultData(String formCode, String parentId) {
		String cloneFormId = "-1";
		try {
			String userId = generalUtil.getSessionUserId();
//			if(formCode.equals("SelfTest")) { // yp 12092019 should be created in the background in step-> action table
//				String defaultDataFormId = getDefaultDataFormId(formCode, "Action", parentId);
//				if(!generalUtil.getNull(defaultDataFormId).equals("")) {
//					Map<String,String> replaceFieldsMap = new HashMap<String,String>();
//					replaceFieldsMap.put("ACTION_ID", parentId);
//					replaceFieldsMap.put("USEASDEFAULTDATA", "null");
//					cloneFormId = formSaveDao.cloneStructTable(defaultDataFormId, replaceFieldsMap);
//					cloneSelfTestAcrossExperiments(defaultDataFormId, cloneFormId, formCode, userId);
//				}
//			} else 
			if(formCode.equals("Request")){
				String parentFormCode = formDao.getFormCodeEntityBySeqId("", parentId);
				String defaultDataFormId = getDefaultDataFormId(formCode, parentFormCode, parentId);
				if(!generalUtil.getNull(defaultDataFormId).equals("")) {
					Map<String,String> columnsMap = new HashMap<String,String>();
					columnsMap.put("DESTSITE_ID", "DESTSITE_ID");
					columnsMap.put("DESTUNIT_ID", "DESTUNIT_ID");
					columnsMap.put("DESTLAB_ID", "DESTLAB_ID");
					columnsMap.put("REQUESTTYPE_ID", "REQUESTTYPE_ID");
					columnsMap.put("TESTPURPOSE", "TESTPURPOSE");
					columnsMap.put("DEFAULT_HST", "'copy'");
					cloneFormId = formSaveDao.cloneStructTable(defaultDataFormId, null,columnsMap,null);
					cloneExperiment.insertRef("GroupsCrew", defaultDataFormId, userId, "REQUEST", cloneFormId, null, null, "");
					// UsersCrew clone
					cloneExperiment.insertRef("UsersCrew", defaultDataFormId, userId, "REQUEST", cloneFormId, null, null, "");
					// OperationType
					cloneExperiment.insertRef("OperationType", defaultDataFormId, userId, "REQUEST", cloneFormId, null, null, "");

				
				}
			}
		} catch (Exception e) {
			cloneFormId = "-1";
			generalUtilLogger.logWrite(e);
		}
		return cloneFormId;
	}
	
	@Override
	public String getDefaultDataFormId(String formCode, String parentFormCode, String parentId) {
		// TODO Auto-generated method stub
		String toReturn = "";
		String scope = "";
		boolean errorInScopeEval = false;
		try {
			Map<String,String> parentFormInfo = formDao.getFromInfoLookupAll(parentFormCode, LookupType.ID, parentId);
//			if(formCode.equals("SelfTest")) { 
//				//look for the experiment ID
//				String experimentId = parentFormInfo.get("EXPERIMENT_ID");
//				//set the scope for self-test (in this case it is by experiment)
//				if(!generalUtil.getNull(experimentId).equals("")) {
//					scope = " and t.experiment_id = '" + experimentId + "'";
//				} else {
//					errorInScopeEval = true;
//				}
//			}
//			else 
			if(formCode.equals("Request")) { 
				//look for the project ID
				String projectId = parentFormInfo.get("PROJECT_ID");
				String userId = generalUtil.getSessionUserId();
				//set the scope for request (in this case it is by project&user)
				if(!generalUtil.getNull(projectId).equals("")) {
					scope = " and t.project_id = '" + projectId + "' and creator_id = '"+userId+"'";
				} else {
					errorInScopeEval = true;
				}
			}
			
			//
			if(!errorInScopeEval) {
				String sql = "select  t.formid from fg_s_" + formCode + "_pivot t where 1=1 and nvl(t.useasdefaultdata,0) = 1 " + scope;
				toReturn = generalDao.selectSingleString(sql);
			}
			 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toReturn;
	}

	

}
