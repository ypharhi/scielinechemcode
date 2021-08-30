package com.skyline.customer.adama;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.xml.dtm.ref.DTMDefaultBaseIterators.ParentIterator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.BeanType;
import com.skyline.form.bean.CloneType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.Form;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.LookupType;
import com.skyline.form.bean.Result;
import com.skyline.form.bean.ValidationCode;
import com.skyline.form.bean.WorkflowType;
import com.skyline.form.dal.ChemDao;
import com.skyline.form.dal.ChemMatrixTaskDao;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.FormSaveDao;
import com.skyline.form.dal.FormSaveElementDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.dal.GeneralTaskDao;
import com.skyline.form.dal.UploadFileDao;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilCalc;
import com.skyline.form.service.GeneralUtilConfig;
import com.skyline.form.service.GeneralUtilDesignData;
import com.skyline.form.service.GeneralUtilForm;
import com.skyline.form.service.GeneralUtilFormState;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.form.service.IntegrationCalc;
import com.skyline.form.service.IntegrationSaveForm;
import com.skyline.form.service.IntegrationValidation;

@Service
public class IntegrationSaveFormAdamaImp implements IntegrationSaveForm {
	
	@Autowired
	protected GeneralTaskDao generalTaskDao;

	@Autowired
	private CommonFunc commonFunc;

	@Autowired
	private FormIdCalc formIdCalc;
  
	@Autowired
	private FormDao formDao;

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private GeneralUtilConfig generalUtilConfig;

	@Autowired
	private GeneralUtilForm generalUtilForm;
	@Autowired
	private GeneralUtilDesignData generalUtilDesignData;

	@Autowired
	private GeneralUtilCalc generalUtilCalc;

	@Autowired
	private GeneralUtilFormState generalUtilFormState;

	@Autowired
	private GeneralDao generalDao;

	@Autowired
	private FormSaveDao formSaveDao;
	
	@Autowired
	protected FormSaveElementDao formSaveElementDao;

	@Autowired
	protected UploadFileDao uploadFileDao;

	@Autowired
	private IntegrationCalc integrationCalc;

	@Autowired
	private IntegrationValidation integrationValidation;

	@Autowired
	protected GeneralUtilLogger generalUtilLogger;

	@Autowired
	private CloneExperiment cloneExperiment;

	@Autowired
	private ChemDao chemDao;
	
	@Autowired
	private ChemMatrixTaskDao chemMatrixTaskDao; 

	@Value("${firstChemistryProjectNum:8000}")
	private String firstChemistryProjectNum;

	@Value("${removeEmptyStructOnSinchronizashen:0}")
	private String removeEmptyStructOnSinchronizashen;

	@Value("${firstFromulationProjectNum:2500}")
	private String firstFromulationProjectNum;
	
	@Value("${hideStepFromSpreadsheet:1}")
	private int hideStepFromSpreadsheet;

	// Note: the search type in the reaction can be and defined in chem.searchType prop (smiles[default]/inchi/mol[has a problem because of the location coordinates that effects the matrix - don't use it]) that are already exists in the tables we look at as columns
	@Value("${chem.searchType:smiles}")
	private String chemSearchType; //"smiles"

	@Value("${customerSynDelim:\\r?\\n}")
	private String customerSynDelim;

	@Value("${systemSynDelim:@syndlm@}")
	private String systemSynDelim;

	@Value("${validateMaterial:1}")
	private String validateMaterial;
	
	@Value("${updateMoleculeMatrixOnSave:1}")
	private String updateMoleculeMatrixOnSave;

	@Value("${jdbc.url}")
	private String DB_URL;

	@Value("${jdbc.username}")
	private String DB_USER;

	@Value("${jdbc.password}")
	private String DB_PASSWORD;

	private final String idDelimiter = "-";

	@Override
	public int preFormSaveEvent(Long stateKey, String formCode, String formId, Map<String, String> elementValueMap,
			Map<String, String> elementAdditinalDataoMap, String userId, String isNew, String saveAction,
			StringBuilder sbInfo) throws Exception { //, String pivotTable, boolean isStructTable

		int auditTrailChangeType = 0;
		String eventName = "preFormSaveEvent." + formCode;
		Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
		String pivotTable = "FG_S_" + form.getFormCodeEntity() + "_PIVOT";

		///test demo - develop
		if (formCode.equals("CalcDemo")) {
			generalUtilConfig.SaveEventHandler(formCode, formId, elementValueMap, userId, "PreSave");
		}
		///

		/********* Project **********/
		/**
		 * set version (as formNumberId) and validate using INVALID_PROJECT_FORM_NUMBER_ID expression (set auditTrailChangeType)
		 * set start date
		 **/
		/**
		 * set version (as formNumberId) and validate using INVALID_PROJECT_FORM_NUMBER_ID expression (set auditTrailChangeType)
		 * set start date
		 **/
		if (formCode.equals("Project")) {
			if (isNew.equals("1")) {

				generalUtilLogger.logWriter(LevelType.DEBUG,
						eventName
								+ "set version (as formNumberId) and validate using INVALID_PROJECT_FORM_NUMBER_ID expression (set auditTrailChangeType)",
						ActivitylogType.SaveEvent, formId);

				String nextFormNumberId = formIdCalc.getNextFormNumberIdByFormId(formCode, formId, elementValueMap,
						userId, pivotTable);
				String projectTypeName = formDao.getFromInfoLookup("PROJECTTYPE", LookupType.ID,
						elementValueMap.get("PROJECTTYPE_ID"), "name");
				integrationValidation.validate(ValidationCode.INVALID_PROJECT_FORM_NUMBER_ID, formCode, formId,
						nextFormNumberId + "," + projectTypeName, sbInfo);
				/*String projectTypeName = formDao.getFromInfoLookup("PROJECTTYPE", LookupType.ID, elementValueMap.get("PROJECTTYPE_ID"), "name");
				elementValueMap.put("PROJECTTYPENAME", projectTypeName);
				String sqlScript = generalUtilConfig.getSqlByNameAndType(formCode, "", "All", elementValueMap, new ArrayList<String>(),"FormNumberId");
				String nextFormNumberId = generalDao.getCSVBySqlNoException(sqlScript, false);getNextFormNumberIdByFormId(formCode, formId, elementValueMap, userId,
						pivotTable);
				integrationValidation.validate(ValidationCode.PROJECT_NUMBER_DEVIATION, formCode, formId,nextFormNumberId, sbInfo);
				integrationValidation.validate(ValidationCode.INVALID_PROJECT_FORM_NUMBER_ID, formCode, formId,
						nextFormNumberId, sbInfo);*/
				auditTrailChangeType = !nextFormNumberId.isEmpty() ? 1 : 0;
				elementValueMap.put("formNumberId", nextFormNumberId);

				// set start date
				generalUtilLogger.logWriter(LevelType.DEBUG, eventName + "set start date", ActivitylogType.SaveEvent,
						formId);
				setStartDate(elementValueMap, formId);
			}
			
			//271020 task 25969 - move to post save - where we have one row after session handling in the save
//			String projectManager_id = elementValueMap.get("PROJECTMANAGER_ID");
//			String sessionId_ = generalUtilFormState.getSessionId(formId);
//			formDao.insertToSelectTable("USERSCREW", formId, "USER_ID", Arrays.asList(projectManager_id), false, userId, sessionId_);
			
			// validate mcw unique (can be empty)
			integrationValidation.validate(ValidationCode.INVALID_MCW_PROJECT, formCode, formId,
					elementValueMap.get("mcwCodeProject"), sbInfo);

		}
		/********* SubProject **********/
		/**
		 * get version (as formNumberId) and validate using INVALID_SUBPROJECT_FORM_NUMBER_ID expression (set auditTrailChangeType)
		 * update mcw
		 * set start date
		 **/
		else if (formCode.equals("SubProject")) {

			if (isNew.equals("1")) {

				generalUtilLogger.logWriter(LevelType.DEBUG,
						eventName
								+ "set version (as formNumberId) and validate using INVALID_SUBPROJECT_FORM_NUMBER_ID expression (set auditTrailChangeType)",
						ActivitylogType.SaveEvent, formId);

				String nextFormNumberId = formIdCalc.getNextFormNumberIdByFormId(formCode, formId, elementValueMap,
						userId, pivotTable);
				integrationValidation.validate(ValidationCode.INVALID_SUBPROJECT_FORM_NUMBER_ID, formCode, formId,
						nextFormNumberId, sbInfo);
				/*String sqlScript = generalUtilConfig.getSqlByNameAndType(formCode, "", "All", elementValueMap, new ArrayList<String>(),"FormNumberId");
				String nextFormNumberId = generalDao.getCSVBySqlNoException(sqlScript, false);getNextFormNumberIdByFormId(formCode, formId, elementValueMap, userId,
						pivotTable);
				integrationValidation.validate(ValidationCode.SUBPROJECT_NUMBER_DEVIATION, formCode, formId,nextFormNumberId, sbInfo);
				integrationValidation.validate(ValidationCode.INVALID_SUBPROJECT_FORM_NUMBER_ID, formCode, formId,
						nextFormNumberId, sbInfo);*/
				auditTrailChangeType = !nextFormNumberId.isEmpty() ? 1 : 0;
				elementValueMap.put("formNumberId", nextFormNumberId);

				// update mcw if new
				if (auditTrailChangeType == 1) {// it is a new form
					generalUtilLogger.logWriter(LevelType.DEBUG, eventName + "get mcw", ActivitylogType.SaveEvent,
							formId);
					String mcwCode = getMCWcode(nextFormNumberId, elementValueMap.get("PROJECT_ID"));
					generalUtilLogger.logWriter(LevelType.DEBUG, eventName + "update mcw mcwCode=" + mcwCode,
							ActivitylogType.SaveEvent, formId);
					elementValueMap.put("mcwCode", mcwCode);
					// TODO MCW VALIDATION
				}
			}

			integrationValidation.validate(ValidationCode.INVALID_MCW_SUBPROJECT, formCode, formId,
					elementValueMap.get("mcwCode"), sbInfo);

			// set start date
			generalUtilLogger.logWriter(LevelType.DEBUG, eventName + "set start date", ActivitylogType.SaveEvent,
					formId);
			setStartDate(elementValueMap, formId);
		}
		/********* SubSubProject **********/
		/**
		 * set start date,
		 * set version (as formNumberId)
		 **/
		else if (formCode.equals("SubSubProject") && isNew.equals("1")) {

			//set version (as formNumberId) and validate using INVALID_SUBSUBPROJECT_FORM_NUMBER_ID expression (set auditTrailChangeType)
			generalUtilLogger.logWriter(LevelType.DEBUG,
					eventName
							+ "set version (as formNumberId) and validate using INVALID_SUBSUBPROJECT_FORM_NUMBER_ID expression (set auditTrailChangeType)",
					ActivitylogType.SaveEvent, formId);
			String nextFormNumberId = formIdCalc.getNextFormNumberIdByFormId(formCode, formId, elementValueMap, userId,
					pivotTable);
			integrationValidation.validate(ValidationCode.INVALID_SUBSUBPROJECT_FORM_NUMBER_ID, formCode, formId,
					nextFormNumberId, sbInfo);
			auditTrailChangeType = !nextFormNumberId.isEmpty() ? 1 : 0;
			elementValueMap.put("formNumberId", nextFormNumberId);

			// set start date 
			generalUtilLogger.logWriter(LevelType.DEBUG, eventName + "set start date", ActivitylogType.SaveEvent,
					formId);
			setStartDate(elementValueMap, formId);

			/**** InvItemInstrument,InvItemMaterial **********/
			/**
			 * InvItemInstrument,InvItemMaterial event -
			 * update trained users
			 **/
		} else if (formCode.equals("InvItemInstrument") || form.getFormCodeEntity().equals("InvItemMaterial")
				|| formCode.equals("InvItemColumn")) {
			//delete all trained users from training list in case of some change in documents
			//updateTrainedUsers(formId, formCode);
			if (formCode.equals("InvItemColumn")) {
				if (generalUtil.getNull(elementValueMap.get("serialNumber")).isEmpty()
						&& generalUtil.getNull(elementValueMap.get("catalogNumber")).isEmpty()
						&& generalUtil.getNull(elementValueMap.get("batchNumber")).isEmpty()) {
					integrationValidation.validate(ValidationCode.CHECK_MANDATORY_FIELDS_INVITEMCOLUMN, formCode,
							formId, "na", sbInfo);
				}
			}
			if (formCode.equals("InvItemInstrument")) {
				if (elementValueMap.get("lastStatusName").equals("New") && formDao
						.getFromInfoLookup("instrumentstatus", LookupType.ID, elementValueMap.get("STATUS_ID"), "name")
						.equals("Active")) {//turned from new to active

					SimpleDateFormat formatter = new SimpleDateFormat(generalUtil.getConversionDateFormat());
					Calendar calendar = Calendar.getInstance();
					String activationDate = formatter.format(calendar.getTime());
					generalUtilLogger.logWriter(LevelType.DEBUG,
							generalUtil.mapToString(
									"update activation date for notificaiton need- elementValueMap before event:",
									elementValueMap),
							ActivitylogType.SaveEvent, formId);
					elementValueMap.put("activationDate", activationDate);
				}
			}
			if (form.getFormCodeEntity().equals("InvItemMaterial")) {
				
				elementValueMap.put("alternativeGroup", elementValueMap.containsKey("alternativeGroup")? elementValueMap.get("alternativeGroup").trim():"");
				
				String statusName = formDao.getFromInfoLookup("materialStatus", LookupType.ID,
						elementValueMap.get("STATUS_ID"), "name");
				
				//**** validation before Cancelled
				if (statusName.equals("Cancelled")) {
					integrationValidation.validate(ValidationCode.DEPLETE_BEFORE_CANCELL_MATERIAL, formCode, formId,
							elementValueMap.get("STATUS_ID"), sbInfo);
					integrationValidation.validate(ValidationCode.CHEKIFCOMPONENT_BEFORE_CANCELL_MATERIAL, formCode, formId,
							elementValueMap.get("STATUS_ID"), new StringBuilder());
					integrationValidation.validate(ValidationCode.CHECKIFRECIPECOMPOSITION_BEFORE_CANCELL_MATERIAL, formCode, formId,
							elementValueMap.get("STATUS_ID"), new StringBuilder());

				}
				
				//**** set synonymsAdapted
				String fieldVal = elementValueMap.get("synonyms").trim();
				if (!fieldVal.equals("")) {
					/* split value by delimiter with removing  leading and trailing spaces around the delimiter*/
					System.out.println("value to split:|" + fieldVal + "|");
					String[] fieldValArr = fieldVal.split("\\s*" + customerSynDelim + "\\s*");
					/*for (String line : fieldValArr) {
					    System.out.println("line |" + line + "|");
					}*/
					String fieldValAdapted = String.join(systemSynDelim, fieldValArr);
					System.out.println("join:|" + fieldValAdapted + "|");
					elementValueMap.put("synonymsAdapted", fieldValAdapted);
				} else {
					elementValueMap.put("synonymsAdapted", "");
				}
				
				//****  for InvItemMaterial form code only (chemical)
				if(formCode.equals("InvItemMaterial")) {
					
					boolean isChangeFromCoFormulant = false;
					boolean isChangeFromCancelled = false;
					if(!isNew.equals("1")) {
						Map<String,String> dbMaterialData = generalDao.getMapsBySqlSingleRow("select t.MATERIALSTATUSNAME, nvl(t.COFORMULANT,'0') as COFORMULANT from fg_s_invitemmaterial_all_v t where t.invitemmaterial_id = " + formId);
						if(dbMaterialData != null) {
							isChangeFromCoFormulant = generalUtil.getNull(elementValueMap.get("CoFormulant")).equals("0") && generalUtil.getNull(dbMaterialData.get("COFORMULANT")).equals("1");
							isChangeFromCancelled = statusName.equals("Cancelled") && generalUtil.getNull(dbMaterialData.get("MATERIALSTATUSNAME")).equals("Cancelled");
						}
					}
					boolean skipMaterialValidation = (validateMaterial.equals("0") || generalUtil.getNull(elementValueMap.get("CoFormulant")).equals("1") || statusName.equals("Cancelled"));
					boolean doSaveJChemn = generalUtil.getNull(elementValueMap.get("chem_struct_change_flag")).equals("1") || isChangeFromCoFormulant || isChangeFromCancelled;
					
					
					//**** if skipMaterialValidation structure needs to be in JChemDeleted (it should have rename JChemNoDuplication instead of JChemDeleted - this is the purpose of this table - to be used for sub search and allow duplication)
					if(skipMaterialValidation) {
						integrationValidation.validate(ValidationCode.CHECK_COFORMULANT_MATERIAL_NAME_DUPLICATION, formCode, formId, new String(elementValueMap.get("invItemMaterialName")), sbInfo); 
						chemDao.saveJChemDeleted(formId, elementValueMap.get("structure"));
					} 
					//**** else validation is needed...
					else {
						 
						//call integrationValidation.validate
						elementValueMap.put("currentStateKey", String.valueOf(stateKey)); //used for validation purpose 
						integrationValidation.validate(ValidationCode.CHECK_MATERIAL_DUPLICATION, formCode, formId,elementValueMap, sbInfo); 
					
						//saveJChem (and valid duplication)
						try {
							if(doSaveJChemn) {
								chemDao.saveJChem(formId, elementValueMap.get("structure"));
							}
						} catch (Exception e) {
							if (e.getMessage().contains("Inserting reactions is not allowed")) {
								e.printStackTrace();
								throw new Exception("Inserting reactions is not allowed.");
							}
							throw new Exception(e);
						}
						
					}
				}
			}

			/**** InvItemBatch **********/
			/**
			 * InvItemBatch events -
			 * initialize external batch number,
			 * update In Use/Depleted,
			 * insert data into activityLog
			 **/
		} else if (formCode.equals("InvItemBatch")) {

			//initialize external batch number
			String sourceInEx = formDao.getFromInfoLookup("source", LookupType.ID, elementValueMap.get("SOURCE_ID"),
					"name");
			if (sourceInEx.equalsIgnoreCase("External")) {
				String nextFormNumberId = formIdCalc.getNextFormNumberIdByFormId(formCode, formId, elementValueMap,
						userId, pivotTable);
				if (nextFormNumberId == null) {
					return -1;
				}
				auditTrailChangeType = !nextFormNumberId.isEmpty() ? 1 : 0;

				if (!nextFormNumberId.isEmpty()) {
					generalUtilLogger.logWriter(LevelType.DEBUG,
							generalUtil.mapToString("initialize external batch number - elementValueMap before event:",
									elementValueMap),
							ActivitylogType.SaveEvent, formId);
					String batchNumber = "0000-" + nextFormNumberId + "-B1";
					elementValueMap.put("invItemBatchName", batchNumber);
					elementValueMap.put("formNumberId", batchNumber);
				}

				if (!generalUtil.getNull(elementValueMap.get("reactantId")).isEmpty()) {//if the batch was created from the materialSearch form then it should be updated in the reactant form
					//deletes the last reactant with formid=reactantid and session null if the current reactant was not an new one
					/*formSaveDao.deleteStructTableByFormId("delete from fg_s_materialref_pivot"
							+ " where formid = '"+elementValueMap.get("reactantId")+"'"
									+ "and sessionid is null"
							, "fg_s_materialref_pivot", elementValueMap.get("reactantId"));*///removed the code above because of the step is already saved, hence there are no temp records
					//updates the material and batch in the reactant
					formSaveDao
							.updateStructTableByFormId("update fg_s_materialref_pivot" + " set INVITEMMATERIAL_ID = '"
									+ elementValueMap.get("INVITEMMATERIAL_ID") + "'" + ", BATCH_ID = '" + formId + "'"
									//+ ", SESSIONID = NULL"
									+ " where formid = '" + elementValueMap.get("reactantId") + "'",
									"fg_s_materialref_pivot", Arrays.asList("INVITEMMATERIAL_ID", "BATCH_ID"), formId);
				}
				String protocolType = elementValueMap.get("materialProtocolType");
				if(protocolType.equals("Premix")||protocolType.equals("Formulation")){
					String currentRecipeId = elementValueMap.get("RECIPEFORMULATION_ID");
					integrationValidation.validate(ValidationCode.CHECK_BATCH_RECIPE_VALID, formCode, formId,
							currentRecipeId, sbInfo);
				}
			}

			String lastRecipeId = formDao.getFromInfoLookup("INVITEMBATCH", LookupType.ID, formId, "RECIPEFORMULATION_ID");
			String currentRecipeId = elementValueMap.get("RECIPEFORMULATION_ID");
			if(!currentRecipeId.isEmpty() && !lastRecipeId.equals(currentRecipeId)){//insert the batch into the batch table in the recipe
				//first,remove the batch from the batch table of the last recipe 
				String sql = "update fg_s_batchselect_pivot set batch_id = REGEXP_REPLACE(REPLACE(','||batch_id||',',','||"
						+ formId + "||',',','),'^[,]|[,]$','')" + "where parentid = '"
						+ lastRecipeId + "' and active = 1 and sessionid is null";
				formSaveDao.updateStructTable(sql, "fg_s_batchselect_pivot", Arrays.asList("batch_id"), "parentid",
						lastRecipeId);
				//delete the row of the select if no data exists  anymore
				if(generalDao.selectSingleStringNoException("select count(*)\n"
						+ " from fg_s_batchselect_v\n"
						+ " where batch_id is not null\n"
						+ " and active =1 and sessionid is null\n"
						+ " and parentid = '"+lastRecipeId+"'").equals("0")){
					sql = "delete from fg_s_batchselect_pivot where parentid = '"+lastRecipeId+"'\n"
							+ "and active =1 and sessionid is null";
					formSaveDao.deleteStructTable(sql, "fg_s_batchselect_pivot", "parentid", lastRecipeId);
				}
				//second,insert the batch to the batch table in the new selected recipe
				formDao.insertToSelectTable("BatchSelect", currentRecipeId, "BATCH_ID", Arrays.asList(formId), true, userId,null);
				
				
			} else if(currentRecipeId.isEmpty() && !lastRecipeId.isEmpty()){//the connected recipe was removed
				//remove the batch from the batch table of the last recipe
				String sql = "update fg_s_batchselect_pivot set batch_id = REGEXP_REPLACE(REPLACE(','||batch_id||',',','||"
						+ formId + "||',',','),'^[,]|[,]$','')" + "where parentid = '"
						+ lastRecipeId + "' and active = 1 and sessionid is null";
				formSaveDao.updateStructTable(sql, "fg_s_batchselect_pivot", Arrays.asList("batch_id"), "parentid",
						lastRecipeId);
				//delete the row of the select if no data exists  anymore
				if(generalDao.selectSingleStringNoException("select count(*)\n"
						+ " from fg_s_batchselect_v\n"
						+ " where batch_id is not null\n"
						+ " and active =1 and sessionid is null\n"
						+ " and parentid = '"+lastRecipeId+"'").equals("0")){
					sql = "delete from fg_s_batchselect_pivot where parentid = '"+lastRecipeId+"'\n"
							+ "and active =1 and sessionid is null";
					formSaveDao.deleteStructTable(sql, "fg_s_batchselect_pivot", "parentid", lastRecipeId);
				}
			}
			//updateQuantityChangeDate(elementValueMap,formId);
			

			//update In Use/Depleted
			updateDepletionBatch(elementValueMap, formId);

			//insert data into activityLog
			String lastQuantity = generalUtil.getEmpty(
					generalDao.selectSingleStringNoException(String
							.format("select t.quantity from fg_s_InvItemBatch_all_v t where t.formId = %1$s ", formId)),
					"");
			//			String user = formDao.getFromInfoLookup("user", LookupType.ID, userId, "name");
			if (lastQuantity.isEmpty()) {
				//					ActivityLog activityLogEntity = new ActivityLog(ActivitylogType.Registration,formId,"TO_DATE(sysdate)" ,user,elementValueMap.get("quantity"),"","","''","''","","");
				//					insertIntoActivityLog(activityLogEntity);}
				Map<String, String> infoMap = new HashMap<String, String>();
				infoMap.put("quantity", elementValueMap.get("quantity"));
				generalUtilLogger.logWrite(LevelType.Other, "", formId, ActivitylogType.Registration, infoMap);
			} else if (!elementValueMap.get("quantity").equals(lastQuantity)) {
				//				   ActivityLog activityLogEntity = new ActivityLog(ActivitylogType.Depletion,formId,"TO_DATE(sysdate)",user,elementValueMap.get("quantity"),"","","''","''","","");
				//				   insertIntoActivityLog(activityLogEntity);
				Map<String, String> infoMap = new HashMap<String, String>();
				infoMap.put("quantity", elementValueMap.get("quantity"));
				generalUtilLogger.logWrite(LevelType.Other, "", formId, ActivitylogType.Depletion, infoMap);
			}

			String lastSampleId = generalDao.selectSingleStringNoException(
					"select SAMPLE_ID from fg_s_invitembatch_v where formid = '" + formId + "'");
			String selectedSample = generalUtil.getNull(elementValueMap.get("SAMPLE_ID"));
			if (!selectedSample.isEmpty() && !selectedSample.equals(lastSampleId)) {
				/*String sql = "select distinct count(*) "
						+ " from fg_i_sampleresults_v t"
						+ " where t.INVITEMMATERIAL_ID = '"+elementValueMap.get("INVITEMMATERIAL_ID")+"'"
						+ " and t.SAMPLE_ID = '"+selectedSample+"'"
						+ " and t.RESULT_NAME like '%Assay%'";
				String res = generalDao.selectSingleStringNoException(sql);*///check if the selected sample does not have an assay result that matches to the batch material. if so, then the sample does not affect the batch and there is no need to display an alert to the user
				if (elementValueMap.get("isSampleDefAffect").equals("1")) {
					integrationValidation.validate(ValidationCode.RELATED_SAMPLE_BATCH, formCode, formId,
							selectedSample, sbInfo);//message to the user that the chosen sample will affect the batch
				}
			}
			String date = generalDao.selectSingleStringNoException("Select to_char(RESULT_DATE,'dd/MM/yyyy') from FG_I_SELECTEDRESULTS_V r "
					+ "where  r.SAMPLE_ID ='" + elementValueMap.get("SAMPLE_ID")
					+ "' and RESULT_NAME = 'Assay' and r.RESULT_MATERIAL_ID ='"+elementValueMap.get("INVITEMMATERIAL_ID")+"'");
					if(!generalUtil.getNull(date).isEmpty()) {
						elementValueMap.put("approvalDate",date);
					}
			/*********** InvItemCalibration **********/
			/**
			 * InvItemCalibration events -
			 * set previous calibration date,
			 * set calibration id,
			 * set calibration frequency
			 * set next calibration date.
			 * update lastCalibrationDate and calibrationType in INVITEMINSTRUMENT
			 **/
		} else if (formCode.equals("InvItemCalibration")) {
			//set previous calibration date
			//setPrevCalibrationDate(elementValueMap,pivotTable);

			//initialize calibration id
			String nextFormNumberId = formIdCalc.getNextFormNumberIdByFormId(formCode, formId, elementValueMap, userId,
					pivotTable);
			if (nextFormNumberId == null) {
				return -1;
			}
			auditTrailChangeType = !nextFormNumberId.isEmpty() ? 1 : 0;
			if (!nextFormNumberId.isEmpty()) {
				generalUtilLogger.logWriter(LevelType.DEBUG, generalUtil
						.mapToString("initialize calibration id - elementValueMap before event:", elementValueMap),
						ActivitylogType.SaveEvent, formId);
				elementValueMap.put("formNumberId", nextFormNumberId);
			}

			//update calibration frequency
			generalUtilLogger.logWriter(LevelType.DEBUG, generalUtil
					.mapToString("update calibration frequency - elementValueMap before event:", elementValueMap),
					ActivitylogType.SaveEvent, formId);
			if (elementValueMap.get("CALIBRATIONTYPE_ID")
					.equals(formDao.getFromInfoLookup("CalibrationType", LookupType.NAME, "External", "Id"))) {
				elementValueMap.put("calibrationFreq", formDao.getFromInfoLookup("inviteminstrument", LookupType.ID,
						elementValueMap.get("INVITEMINSTRUMENT_ID"), "extcalibrationFreq"));
			} else if (elementValueMap.get("CALIBRATIONTYPE_ID")
					.equals(formDao.getFromInfoLookup("CalibrationType", LookupType.NAME, "Internal", "Id"))) {
				elementValueMap.put("calibrationFreq", formDao.getFromInfoLookup("inviteminstrument", LookupType.ID,
						elementValueMap.get("INVITEMINSTRUMENT_ID"), "inCalibrationFreq"));
			}

			//update next calibration date
			SimpleDateFormat formatter = new SimpleDateFormat(generalUtil.getConversionDateFormat());
			Date calibrationDate = formatter.parse(elementValueMap.get("calibrationDate"));
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(calibrationDate);
			calendar.add(Calendar.MONTH, Integer.parseInt(
					elementValueMap.get("calibrationFreq").isEmpty() ? "0" : elementValueMap.get("calibrationFreq")));
			String nextCalibrtionDate = formatter.format(calendar.getTime());
			generalUtilLogger.logWriter(LevelType.DEBUG, generalUtil
					.mapToString("update next calibration date - elementValueMap before event:", elementValueMap),
					ActivitylogType.SaveEvent, formId);
			elementValueMap.put("nextCalibDate", nextCalibrtionDate);

			//set previous calibration date
			setPrevCalibrationDate(elementValueMap, pivotTable, formId, isNew);

			/*********** InvItemMaintenance **********/
			/**
			 * InvItemMaintenance events -
			 * initialize calibration id,
			 * update instrument status
			 **/
		} else if (formCode.equals("InvItemMaintenance")) {
			//initialize calibration id
			String nextFormNumberId = formIdCalc.getNextFormNumberIdByFormId(formCode, formId, elementValueMap, userId,
					pivotTable);
			if (nextFormNumberId == null) {
				return -1;
			}
			auditTrailChangeType = !nextFormNumberId.isEmpty() ? 1 : 0;
			if (!nextFormNumberId.isEmpty()) {
				elementValueMap.put("formNumberId", nextFormNumberId);
			}
			//update status
			updateStatus(elementValueMap);

			/*********** StepFr **********/
			/**
			 * Step events -
			 * update formcode in pivot
			 * update experiment status,
			 * set actual start date and finish date,
			 * update batch's quantity (in inventory),
			 * insert data into activityLog
			 **/
		} else if (formCode.equals("StepFr")) {
			//get step info (for all Step event procedures)
			formSaveDao.updateStructTableFormCode("Step", "StepFr", formId, true);

			String lastStatus_id = formDao.getFromInfoLookup("Step", LookupType.ID, formId, "STATUS_ID");
			String lastStatusName = formDao.getFromInfoLookup("stepstatus", LookupType.ID, lastStatus_id, "name");
			elementValueMap.put("LASTSTATUS_ID", lastStatus_id);

			String currentStatusName = formDao.getFromInfoLookup("STEPSTATUS", LookupType.ID,
					elementValueMap.get("STATUS_ID"), "name");

			preFormSaveStepStatusChange(formCode, formId, lastStatusName, currentStatusName, elementValueMap, sbInfo);
			/*********** Step **********/
			/**
			 * Step events -
			 * update experiment status,
			 * set actual start date and finish date,
			 * update batch's quantity (in inventory),
			 * insert data into activityLog
			 **/
		} else if (formCode.equals("Step")) {
			//get step info (for all Step event procedures)
			integrationValidation.validate(ValidationCode.CHECK_SAMPLES_INUSE_REMOVED, formCode, formId, "", sbInfo);

			String lastStatus_id = formDao.getFromInfoLookup("Step", LookupType.ID, formId, "STATUS_ID");
			String lastStatusName = formDao.getFromInfoLookup("stepstatus", LookupType.ID, lastStatus_id, "name");
			elementValueMap.put("LASTSTATUS_ID", lastStatus_id);

			String currentStatusName = formDao.getFromInfoLookup("STEPSTATUS", LookupType.ID,
					elementValueMap.get("STATUS_ID"), "name");

			preFormSaveStepStatusChange(formCode, formId, lastStatusName, currentStatusName, elementValueMap, sbInfo);

			//make appropriation between characterizedSample and the existing sample
			List<String> charSampleList = new ArrayList<String>();
			charSampleList.addAll(Arrays.asList(elementValueMap.get("characterizedSample").split(",")));
			String sampleCsv = generalDao
					.selectSingleStringNoException("select sampleTable from fg_s_sampleselect_v where parentid='"
							+ formId + "'" + generalUtilFormState.getWherePartForTmpData("SampleSelect", formId));
			if (sampleCsv.isEmpty()) {
				elementValueMap.put("characterizedSample", "");
			} else {
				List<String> samplesToRemove = new ArrayList<>();
				List<String> sampleSelectList = Arrays.asList(sampleCsv.split(","));
				for (String sample : charSampleList) {
					if (sampleSelectList.contains(sample)) {
						continue;
					}
					samplesToRemove.add(sample);
				}
				charSampleList.removeAll(samplesToRemove);

				elementValueMap.put("characterizedSample", generalUtil.listToCsv(charSampleList));
			}
			// update default mass balance
			if(elementValueMap.get("chkCharacterMassBalance").equals("1"))
			{
				String sql = "select max(formcode||','||mass_balance_id) as character_massbalance \n"
							+" from (\n"
							+" select distinct e.formid as experiment_id, m.massbalancenumber as mass_balance_id, e.formcode      \n" 
							+" from fg_s_experiment_pivot e, fg_s_experiment_massbalance m\n"
							+" where nvl(m.chkcharactermassbalance,0) = 1\n"
							+" and e.formid = m.parentid \n"
							+" and m.massbalancenumber <> '0' \n"
							+" union all\n"
							+" select distinct ep.formid as experiment_id, sp.formid as mass_balance_id, sp.formcode \n"
							+" from fg_s_experiment_pivot ep, fg_s_step_pivot sp \n"
							+" where sp.experiment_id = ep.formid \n"
							+" and nvl(sp.chkcharactermassbalance,0) = 1 \n"
							+" and sp.formid <> '"+formId+"' \n"
							+") \n"
							+" where experiment_id = '"+elementValueMap.get("EXPERIMENT_ID")+"'";
				String defaultMassBalance = generalUtil.getEmpty(generalDao.selectSingleStringNoException(sql),"");
				if (!defaultMassBalance.isEmpty()) 
				{
					// RESET values in case there was another value of default mass balance saved in DB 	
					resetDefaultMassBalanceValue(elementValueMap.get("EXPERIMENT_ID"));
				
					/* if there performance issues it's possible to reset in concrete table with code below */
//					String[] arr = defaultMassBalance.split(",");
//					String massBalanceHolderFC = arr[0];
//					//String massBalanceHolderId = arr[1];
//					
//					if(massBalanceHolderFC.equals("Step"))
//					{
//						String sql_ = "update FG_S_STEP_PIVOT t set t.chkcharactermassbalance = 0 where t.experiment_id = '"+elementValueMap.get("EXPERIMENT_ID")
//										+"' and nvl(t.chkcharactermassbalance,0) = 1";
//						formSaveDao.updateStructTableByFormId(sql_, "FG_S_STEP_PIVOT",Arrays.asList("experiment_id"), formId);
//					}
//					else //Experiment/ExperimentCP
//					{
//						String sql_ = "update FG_FORMADDITIONALDATA t \n"
//									  +" set t.value = '0' \n"
//									  +"where ((t.entityimpcode = 'chkCharacterMassBalance' and t.value = '1') \n"
//									  +"        or (t.entityimpcode = 'chkCharacterMassBalance2' and t.value = '1') \n"
//									  +"        or (t.entityimpcode = 'chkCharacterMassBalance3' and t.value = '1') \n"
//									  +"      ) \n"
//									  +"and t.parentid = '"+elementValueMap.get("EXPERIMENT_ID")+"' ";	
//						formSaveDao.updateAdditinalData(sql_, Arrays.asList("parentid"), formId);
//					}
				} 
				
			}
			

			/*********** Experiment **********/
			/***
			 * Experiment events -
			 * set status,
			 * completion Date, approval Date, actual Start Date,
			 * last modification date,
			 * experiment number and experiment Version
			 **/
		} else if (formCode.equals("ExpParamsCrRef")) {
			generalUtilConfig.SaveEventHandler(formCode, formId, elementValueMap, userId, "PreSave");
		} else if (formCode.equals("Experiment") || formCode.equals("ExperimentAn") || formCode.equals("ExperimentFor")
				|| formCode.equals("ExperimentPr") || formCode.equals("ExperimentPrCR")
				|| formCode.equals("ExperimentPrVS") || formCode.equals("ExperimentPrTS")
				|| formCode.equals("ExperimentPrBT") || formCode.equals("ExperimentPrGn") || formCode.equals("ExperimentStb") || formCode.equals("ExperimentCP")) { // add ExperimentStb for "Taro develop"
			generalUtilConfig.SaveEventHandler(formCode, formId, elementValueMap, userId, "PreSave");
			auditTrailChangeType = preFormSaveExperiment("Experiment", formId, elementValueMap, userId, pivotTable);//, isStructTable	
			if (formCode.equals("Experiment")) {//organic
				String statusName = formDao.getFromInfoLookup("ExperimentStatus", LookupType.ID,
						elementValueMap.get("STATUS_ID"), "name");
				if (!statusName.equals("Completed")) {//when it's completed, the following operation occurs in general event
					//make an appropriation between characterizedSample and the existing sample
					List<String> charSampleList = new ArrayList<String>();
					charSampleList.addAll(Arrays.asList(elementValueMap.get("characterizedSample").split(",")));
					String sampleCsv = generalDao.selectSingleStringNoException(
							"select sampleTable from fg_s_sampleselect_v where parentid='" + formId + "'"
									+ generalUtilFormState.getWherePartForTmpData("SampleSelect", formId));
					if (generalUtil.getNull(sampleCsv).isEmpty()) {
						elementValueMap.put("characterizedSample", "");
					} else {
						List<String> samplesToRemove = new ArrayList<>();
						List<String> sampleSelectList = Arrays.asList(sampleCsv.split(","));
						for (String sample : charSampleList) {
							if (sampleSelectList.contains(sample)) {
								continue;
							}
							samplesToRemove.add(sample);
						}
						charSampleList.removeAll(samplesToRemove);

						elementValueMap.put("characterizedSample", generalUtil.listToCsv(charSampleList));
					}
				}

				// update default mass balance				
				String currCheckedMassBalance = elementValueMap.get("hdnExperimDefaultMassBalance");
				if(currCheckedMassBalance.equals("")) {
					// RESET values in case there was another value of default mass balance saved in DB 	
					resetDefaultMassBalanceValue(formId);
				}
				else
				{
					String[] arr = currCheckedMassBalance.split(",");
					String massBalanceNewFC = arr[0];
					String massBalanceNewId = arr[1];
					String expMassBalanceInx = (massBalanceNewFC.equals("Step"))?"0":massBalanceNewId;
					String currFormId = (massBalanceNewFC.equals("Step"))?massBalanceNewId:formId;
					
					String sql = "select max(formcode||','||mass_balance_id) as character_massbalance \n"
							+" from (\n"
							+" select distinct e.formid as experiment_id, m.massbalancenumber as mass_balance_id, e.formcode      \n" 
							+" from fg_s_experiment_pivot e, fg_s_experiment_massbalance m\n"
							+" where nvl(m.chkcharactermassbalance,0) = 1\n"
							+" and e.formid = m.parentid \n"
							+" and m.massbalancenumber <> '"+expMassBalanceInx+"' \n"
							+" union all\n"
							+" select distinct ep.formid as experiment_id, sp.formid as mass_balance_id, sp.formcode \n"
							+" from fg_s_experiment_pivot ep, fg_s_step_pivot sp \n"
							+" where sp.experiment_id = ep.formid \n"
							+" and nvl(sp.chkcharactermassbalance,0) = 1 \n"
							+" and sp.formid <> '"+currFormId+"' \n"
							+") \n"
							+" where experiment_id = '"+formId+"'";
					String dbDefaultMassBalance = generalUtil.getEmpty(generalDao.selectSingleStringNoException(sql),"");
					if (!dbDefaultMassBalance.isEmpty()) 
					{
						// RESET values in case there was another value of default mass balance saved in DB 							
						resetDefaultMassBalanceValue(formId);
					}
						
					// UPDATE Step in case new characteristic mass balance belong to Step
					if(massBalanceNewFC.equals("Step"))
					{
						sql = "update FG_S_STEP_PIVOT t set t.chkcharactermassbalance = 1 where t.formId = '"+massBalanceNewId 
										+"' and nvl(t.chkcharactermassbalance,0) != 1 and t.experiment_id = '"+formId+"'";
						formSaveDao.updateStructTableByFormId(sql, "FG_S_STEP_PIVOT",Arrays.asList("experiment_id"), massBalanceNewId);
					}
					
					}
			} else if(formCode.equals("ExperimentFor")){
				//checks if a recipe-experiment connection exists. if so- then update the recipe usages in all the recipes that were used in the experiment
				String recipe_id = elementValueMap.get("RECIPEFORMULATION_ID");
				String doClearConnection = generalUtil.getNull(elementValueMap.get("doClearConnection"));
				if(!recipe_id.isEmpty() && (doClearConnection.isEmpty()||doClearConnection.equals("0"))){
					String recipeListCsv = elementValueMap.get("recipeList");
					String[] recipeList = recipeListCsv.isEmpty()?new String[0]:recipeListCsv.split(",");
					for(int i=0;i<recipeList.length;i++){
						String recipeToConnect = recipeList[i];
						formDao.insertToSelectTable("experimentselect", recipeToConnect, "EXPERIMENT_ID", Arrays.asList(formId), true, userId, null);
					}
					formDao.insertToSelectTable("experimentselect", recipe_id, "EXPERIMENT_ID", Arrays.asList(formId), true, userId, null);
					
				} else if(!doClearConnection.isEmpty()&&doClearConnection.equals("1")){
					//clear the external code
					elementValueMap.put("externalCode", "");
					elementValueMap.put("RECIPEFORMULATION_ID","");
					//disconnect the batches that were created by the experiment from the recipe
					String sql = "select formid\n"
							+ "from fg_s_invitembatch_pivot\n"
							+ "where experiment_id = '"+formId+"'\n"
							+ "and recipeformulation_id = '"+recipe_id+"'";
					List<String> batchList = generalDao.getListOfStringBySql(sql);
					for(String batch_id:batchList){
						sql = "update fg_s_invitembatch_pivot\n"
								+ "set recipeformulation_id = null\n"
								+ "where formid = '"+batch_id+"'\n";
						formSaveDao.updateStructTableByFormId(sql, "fg_s_invitembatch_pivot", Arrays.asList("recipeformulation_id"), batch_id);
						/*
						//remove the batch from the experiment batches table
						sql = "update fg_s_batchselect_pivot\n"
								+ "set batch_id = REGEXP_REPLACE(REPLACE(','||batch_id||',',','||"
								+ batch_id + "||',',','),'^[,]|[,]$','')" 
								+ "where parentid = '"
								+ formId + "' and active = 1 and sessionid is null";
						formSaveDao.updateStructTable(sql, "fg_s_batchselect_pivot", Arrays.asList("batch_id"), "parentid",
								formId);*/
											
						//remove the batch from the recipe batches table
						sql = "update fg_s_batchselect_pivot\n"
								+ "set batch_id = REGEXP_REPLACE(REPLACE(','||batch_id||',',','||"
								+ batch_id + "||',',','),'^[,]|[,]$','')\n" 
								+ "where parentid = '"
								+ recipe_id + "' and active = 1 and sessionid is null\n";
						formSaveDao.updateStructTable(sql, "fg_s_batchselect_pivot", Arrays.asList("batch_id"), "parentid",
								recipe_id);
						//delete the row of the select if no data exists  anymore
						if(generalDao.selectSingleStringNoException("select count(*)\n"
								+ " from fg_s_batchselect_v\n"
								+ " where batch_id is not null\n"
								+ " and active =1 and sessionid is null\n"
								+ " and parentid = '"+recipe_id+"'").equals("0")){
							sql = "delete from fg_s_batchselect_pivot where parentid = '"+recipe_id+"'\n"
									+ "and active =1 and sessionid is null";
							formSaveDao.deleteStructTable(sql, "fg_s_batchselect_pivot", "parentid", recipe_id);
						}
					}
					
					//remove the experiment from all the recipes that were used in the experiment
					sql = "select e.experimentselect_id\n"
						+ "from fg_s_recipeformulation_v r,\n"
						+ "fg_s_experimentselect_v e\n"
						+ "where e.parentid = r.formid\n"
						+ "and e.active  =1\n"
						+ "and e.sessionid is null\n"
						+ "and instr(','||e.experiment_id||',',','||"+formId+"||',')>0";
					List<String> experimentselectList = generalDao.getListOfStringBySql(sql);
					for(String experimentselect_id:experimentselectList){
						sql = "update fg_s_experimentselect_pivot\n"
								+ " set experiment_id = REGEXP_REPLACE(REPLACE(','||experiment_id||',',','||"
								+ formId + "||',',','),'^[,]|[,]$','')\n" 
								+ "where formid = '"
								+ experimentselect_id + "' and active = 1 and sessionid is null";
						formSaveDao.updateStructTableByFormId(sql, "fg_s_experimentselect_pivot", Arrays.asList("experiment_id"),
								experimentselect_id);
						//delete the row of the select if no data exists  anymore
						if(generalDao.selectSingleStringNoException("select count(*)\n"
								+ " from fg_s_experimentselect_v\n"
								+ " where batch_id is not null\n"
								+ " and active =1 and sessionid is null\n"
								+ " and formid = '"+experimentselect_id+"'").equals("0")){
							sql = "delete from fg_s_experimentselect_pivot where formid = '"+experimentselect_id+"'\n"
									+ "and active =1 and sessionid is null";
							formSaveDao.deleteStructTable(sql, "fg_s_experimentselect_pivot", "formid", experimentselect_id);
						}
					}
				}
				//the following fields are used as indicators for the save operation. No need to keep their values after it.
				elementValueMap.put("recipeList", "");
				elementValueMap.put("doClearConnection", "");
				
				String enabledspreadsheet = generalUtil.getNull(elementValueMap.get("enableSpreadsheet"),"No");
				//String isSPEnableSpreadsheet = generalDao.selectSingleStringNoException("select isenablespreadsheet from fg_s_subproject_v where formid = '"+elementValueMap.get("SUBPROJECT_ID")+"'");
				if (generalUtil.getNull(enabledspreadsheet).equals("No")) {
					String defaultSpreadsheet = generalDao.selectSingleStringNoException(
							"select spreadsheet from fg_s_spreadsheettempla_v where formId in(select t.DEFAULTSPREADSHEETTEMP from FG_S_FORMULATIONTYPE_V t,fg_s_experiment_v ex where t.formulationtype_id = ex.FORMULATIONTYPE_ID and ex.formid = '"
									+ formId + "')");
					elementValueMap.put("spreadsheetExcel", generalUtil.getNull(defaultSpreadsheet));
				}
			}
//			else if (formCode.equals("ExperimentStb")) { // develop stability not in use				
//				String fileId = elementValueMap.get("experimentResultsTableData");
//				if(fileId != null && !fileId.equals("") )
//				{				
//					JSONObject expResTableData = new JSONObject(generalDao.getSingleStringFromClobNoException("select file_content from fg_clob_files where file_id = '"+ fileId + "'"));
//					String updateField = "";
//					int index = 0;
//					Iterator<String> keys = expResTableData.keys();
//					while(keys.hasNext())
//					{
//						String key = keys.next();
//						JSONObject valueObj = expResTableData.getJSONObject(key);
//						if(valueObj.length() > 0)
//						{
//							if(index == 0)
//							{
//								updateField = "tpactualdate";
//							}
//							else
//							{
//								updateField = "resultvalue";
//							}
//							
//							
//							Iterator<String> ids = valueObj.keys();
//							while(ids.hasNext())
//							{
//								String id = ids.next();
//								String val = valueObj.getString(id);
//								formSaveDao.updateStructTableByFormId(
//										"update FG_S_EXPERSTBRESULT_PIVOT set "+updateField+ " = '" + val + "' where formid = '" + id + "'",
//										"FG_S_EXPERSTBRESULT_PIVOT", Arrays.asList(updateField), id);
//							}
//							index++;
//						}
//					}
//				}
//			}
			String sessionId_ = generalUtilFormState.getSessionId(formId);
			String sql = "select singlereq_id from fg_s_RequestSelect_all_v where PARENTID='"+formId+"'  and active = 1 and sessionid = '"+sessionId_+"' and singlereq_id not in (select singlereq_id from fg_s_RequestSelect_all_v where sessionid is null and PARENTID='"+formId+"')";
			List<String> requestList = generalDao.getListOfStringBySql(sql);
			sql = "select sample_id from fg_s_sampleSelect_all_v where PARENTID='"+formId+"'  and active = 1 and sessionid = '"+sessionId_+"' and sample_id not in (select sample_id from fg_s_sampleSelect_all_v where sessionid is null and PARENTID='"+formId+"')";
			List<String> samples = generalDao.getListOfStringBySql(sql);
			
			commonFunc.updateSampleByRequest(requestList,formId,userId);
			commonFunc.updateRequestBySample(samples,elementValueMap.get("EXPERIMENTTYPE_ID"),formId,userId);
			
			/*********** ExperimentMain **********/
			/***
			 * ExperimentMain events -
			 * set status,
			 * completion Date, approval Date, actual Start Date,
			 * last modification date,
			 * experiment number and experiment Version
			 **/
		} else if (formCode.equals("ExperimentMain")) {
			if (!elementValueMap.get("TEMPLATE_ID").isEmpty()) {
				String sourceExperimentId = generalDao
						.selectSingleString("select EXPERIMENT_ID from FG_S_TEMPLATE_V where TEMPLATE_ID = '"
								+ elementValueMap.get("TEMPLATE_ID") + "'");
				/*String sourceTemplateExp = generalDao
						.selectSingleString("select SOURCEEXPNO_ID from FG_S_TEMPLATE_V where TEMPLATE_ID = '"
								+ elementValueMap.get("TEMPLATE_ID") + "'");*/
				String protocolType = formDao.getFromInfoLookup("ProtocolType", LookupType.ID,
						elementValueMap.get("PROTOCOLTYPE_ID"), "name");
				cloneExperiment.cloneExperiment(sourceExperimentId, protocolType, formId, userId, CloneType.FROM_TEMPLATE,elementValueMap.get("TEMPLATE_ID"));

				//fix bug 7704
//				String shortDescription = generalDao.selectSingleString(
//						"select t.DESCRIPTION from fg_s_experiment_v t where t.experiment_id = " + sourceExperimentId);
//				elementValueMap.put("description", shortDescription);

				//put expView from template
				try {
					if (elementValueMap.get("EXPERIMENTVIEW_ID") == null
							|| elementValueMap.get("EXPERIMENTVIEW_ID").equals("")) {
						String expView = generalDao.selectSingleString(
								"select t.EXPERIMENTVIEW_ID from fg_s_experiment_v t where t.experiment_id = "
										+ sourceExperimentId);
						elementValueMap.put("EXPERIMENTVIEW_ID", expView);
					}
				} catch (Exception e) {
					// do nothing
				}
			}
			if(elementValueMap.get("description").isEmpty()) {
				String aim = elementValueMap.get("aim");
				String aimTxt = generalDao.selectSingleStringNoException("select fg_get_richtext_display('"+aim+"') from dual");//get the text only from fg_get richtext
				if(aimTxt.length()>500) {
					aimTxt = aimTxt.substring(0,497)+"...";
				}
				elementValueMap.put("description",aimTxt);
			}
			elementValueMap.put("OWNER_ID", userId);
			elementValueMap.put("CREATOR_ID", userId);

			auditTrailChangeType = preFormSaveExperiment("Experiment", formId, elementValueMap, userId, pivotTable);

			//				String newFormId = formSaveDao.getStructFormId("Experiment");
			//				pivotTable = "FG_S_EXPERIMENT_PIVOT";				
			//				Map<String, String> newElementValueMap = new HashMap<String, String>();
			//				newElementValueMap.put("PROJECT_ID", elementValueMap.get("PROJECT_ID"));
			//				newElementValueMap.put("SUBPROJECT_ID", elementValueMap.get("SUBPROJECT_ID"));
			//				newElementValueMap.put("SUBSUBPROJECT_ID", elementValueMap.get("SUBSUBPROJECT_ID"));
			//				newElementValueMap.put("LABORATORY_ID", elementValueMap.get("LABORATORY_ID"));
			//				auditTrailChangeType = preFormSaveExperiment("Experiment", newFormId, newElementValueMap, userId, pivotTable);//, isStructTable
			//				String cols = "FORMID,TIMESTAMP,CHANGE_BY,ACTIVE,CREATIONDATETIME,LABORATORY_ID,PROTOCOLTYPE_ID,AIM,"
			//						+ "ESTIMATEDSTARTDATE,DESCRIPTION,SUBPROJECT_ID,PROJECT_ID,SUBSUBPROJECT_ID,"
			////						---newElementValueMap---
			//						+ "FORMNUMBERID,EXPERIMENTVERSION,LASTMODIFDATE,EXPERIMENTNAME,STATUS_ID,LASTSTATUS_ID,EXPERIMENTMAIN_ID";
			//				String values = "'" + newFormId + "',sysdate,'" + userId + "',1,to_char(sysdate, 'dd/mm/yyyy')"
			//				+ ",'" + elementValueMap.get("LABORATORY_ID") + "'"
			//				+ ",'" + elementValueMap.get("PROTOCOLTYPE_ID") + "'"
			//				+ ",'" + elementValueMap.get("aim") + "'"
			//				+ ",'" + elementValueMap.get("estimatedStartDate") + "'"
			//				+ ",'" + elementValueMap.get("description") + "'"
			//				+ ",'" + elementValueMap.get("SUBPROJECT_ID") + "'"
			//				+ ",'" + elementValueMap.get("PROJECT_ID") + "'"
			//				+ ",'" + elementValueMap.get("SUBSUBPROJECT_ID") + "'"
			//				+ ",'" + newElementValueMap.get("formNumberId") + "'"
			//				+ ",'" + newElementValueMap.get("experimentVersion") + "'"
			//				+ ",'" + newElementValueMap.get("lastModifDate") + "'"
			//				+ ",'" + newElementValueMap.get("experimentName") + "'"
			//				+ ",'" + newElementValueMap.get("STATUS_ID") + "'"
			//				+ ",'" + newElementValueMap.get("LASTSTATUS_ID") + "'"
			//			    + ",'" + formId + "'";			 
			//				formSaveDao.updateSingleString("insert into FG_S_EXPERIMENT_PIVOT (" + cols + ") values (" + values + ")");

		} else if (formCode.equals("ExpCloneMain")) {
			String sql = String.format(
					"select 1 from fg_s_protocoltype_v t where t.PROTOCOLTYPE_ID = %1$s and "
							+ " instr(','||t.UNITS_ID||',', ','||%2$s||',')>0",
					elementValueMap.get("PROTOCOLTYPE_ID"), elementValueMap.get("UNITS_ID"));
			String userValidation = generalDao.selectSingleStringNoException(sql);
			if (generalUtil.getNull(userValidation).isEmpty()) {
				integrationValidation.validate(ValidationCode.INVALID_USER_CLONE_EXP, formCode, formId, "",
						new StringBuilder());

			}

			cloneExperimentByMain(elementValueMap, elementValueMap.get("expCloneMainName"),
					elementValueMap.get("originExp_id"), userId, formId);
			String protocolTypeName = formDao.getFromInfoLookup("PROTOCOLTYPE", LookupType.ID, elementValueMap.get("PROTOCOLTYPE_ID"), "name") ;
			String experimentTypeName = formDao.getFromInfoLookup("EXPERIMENTTYPE", LookupType.ID, elementValueMap.get("EXPERIMENTTYPE_ID"), "name") ;
			if(protocolTypeName.equals("Analytical") && experimentTypeName.equals("General")) {
				String firstStatusId = formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.NAME, "Active", "id");
				generalUtilLogger.logWriter(LevelType.DEBUG,
						generalUtil.mapToString("update status - elementValueMap before event:", elementValueMap),
						ActivitylogType.SaveEvent, formId);
				elementValueMap.put("STATUS_ID", firstStatusId);
			}
		} else if (formCode.equals("MassBalanceRef")) {

			/** massBalanceCalc **/
			integrationCalc.doCalc("MassBallanceCalc", "OnSave", "", elementValueMap, null, null, formCode, formId,
					userId);

		}
		/*********** Action **********/
		/**
		 * Action events -
		 * initialize order(as formNumberId)
		 **/
		else if (formCode.equals("Action")) {

			//initialize formNumberId(order)

			String nextFormNumberId = formIdCalc.getNextFormNumberIdByFormId(formCode, formId, elementValueMap, userId,
					pivotTable);
			if (nextFormNumberId == null) {
				return -1;
			}
			auditTrailChangeType = !nextFormNumberId.isEmpty() ? 1 : 0;

			if (!nextFormNumberId.isEmpty()) {
				generalUtilLogger.logWriter(LevelType.DEBUG,
						generalUtil.mapToString("initialize order - elementValueMap before event:", elementValueMap),
						ActivitylogType.SaveEvent, formId);
				elementValueMap.put("formNumberId", nextFormNumberId);

			}

			//change formNumberId(order) according to SETBEFORE 
			if (!elementValueMap.get("SETBEFORE_ID").isEmpty()) {
				String fNumberId = commonFunc.actionSetBefore(elementValueMap.get("STEP_ID"),
						elementValueMap.get("SETBEFORE_ID"), elementValueMap.get("formNumberId"));

				generalUtilLogger.logWriter(LevelType.DEBUG,
						generalUtil.mapToString("initialize order - elementValueMap before event:", elementValueMap),
						ActivitylogType.SaveEvent, formId);
				elementValueMap.put("formNumberId", fNumberId);
			}

			//time validation when start date equal to end date
			if (elementValueMap.get("startDate").equals(elementValueMap.get("endDate"))) {
				commonFunc.actionValidationDate(elementValueMap.get("startDate"), elementValueMap.get("endDate"),
						elementValueMap.get("startTime"), elementValueMap.get("endTime"), formId);
			}
		}

		/********** MaterialRef *********/
		else if (formCode.equals("MaterialRef")) {
			//if (!elementValueMap.get("limitingAgent").equals("1") && !elementValueMap.get("tableType").equals("Product"))
			if (!elementValueMap.get("BATCH_ID").isEmpty()) {
				Map<String, String> srcBatchInf = generalDao
						.sqlToHashMap("select QUANTITY,QUANTITYUOM_ID from FG_S_INVITEMBATCH_PIVOT where formId = '"
								+ elementValueMap.get("BATCH_ID") + "'");
				Double normalSrcQuantity = generalUtilCalc.getNormalNumber(
						generalUtil.getEmpty(srcBatchInf.get("QUANTITY"), "0"), srcBatchInf.get("QUANTITYUOM_ID"), 0D);
				Double normalConsumeQuantity = generalUtilCalc.getNormalNumber(
						generalUtil.getEmpty(elementValueMap.get("quantity"), "0"),
						elementValueMap.get("QUANTITYUOM_ID"), 0D);
				integrationValidation.validate(ValidationCode.INVALID_CONSUMED_QUANTITY, "INVITEMBATCH", formId,
						new String(normalConsumeQuantity.toString() + "," + normalSrcQuantity.toString()), sbInfo);
			}
		}

		/********** Component *********/
		else if (formCode.equals("Component")) {
			if (!elementValueMap.get("impurity").equals("1")) {
				integrationCalc.doCalc("ComponentCalc", "OnSave", "", elementValueMap, null, null, formCode, formId,
						userId);
			}
		}
		/********** Request *********/
		/**
		 * Request events -
		 * set Estimated time
		 * set priority
		 * set completion date
		 * 
		 **/
		else if (formCode.equals("Request")) {

			/*
			 * //set formNumberId- request number
			 * String nextFormNumberId = getNextFormNumberIdByFormId(formCode, formId, elementValueMap, userId,
			 * pivotTable);
			 * if (nextFormNumberId == null) {
			 * return -1;
			 * }
			 * auditTrailChangeType = !nextFormNumberId.isEmpty() ? 1 : 0;
			 * if (!nextFormNumberId.isEmpty()) {
			 * integration.LogWriterDB(LevelType.DEBUG, , ActivitylogType.SaveEvent, formId);(
			 * generalUtil.mapToString("set request number - elementValueMap before event:", elementValueMap));
			 * elementValueMap.put("parentRequestNum", nextFormNumberId);
			 * }
			 */

			String nextFormNumberId = formIdCalc.getNextFormNumberIdByFormId(formCode, formId, elementValueMap, userId,
					pivotTable);
			if (nextFormNumberId == null) {
				return -1;
			}
			auditTrailChangeType = !nextFormNumberId.isEmpty() ? 1 : 0;

			if (!nextFormNumberId.isEmpty()) {
				generalUtilLogger.logWriter(LevelType.DEBUG,
						generalUtil.mapToString("set request number - elementValueMap before event:", elementValueMap),
						ActivitylogType.SaveEvent, formId);
				elementValueMap.put("formNumberId", nextFormNumberId);
				elementValueMap.put("requestName", nextFormNumberId);

				//String plannedStatusId = formDao.getFromInfoLookup("REQUESTSTATUS", LookupType.NAME, "Planned", "id");
				//elementValueMap.put("REQUESTSTATUS_ID", plannedStatusId);
			}
		
			String destlab = elementValueMap.get("DESTLAB_ID");
			String lastDestLab = generalDao.selectSingleStringNoException(
					"select t.destlab_id from FG_S_request_pivot t where t.formid ='" + formId + "'");
			if (generalUtil.getNull(lastDestLab).isEmpty() || destlab.equals(generalUtil.getNull(lastDestLab))) {
				elementValueMap.put("laboratoryChanged", "NO");
			} else {
				elementValueMap.put("laboratoryChanged", "YES");
			}
			DateFormat dateFormat = new SimpleDateFormat(generalUtil.getConversionDateFormat());
			Date date = new Date();
			String priorityHighId = formDao.getFromInfoLookup("PRIORITY", LookupType.NAME, "High", "id");
			//When Priority is High, Estimated time changes to 1 day from Creation Date. 
			String dt = elementValueMap.get("creationDate");
			Calendar c = Calendar.getInstance();
			c.setTime(dateFormat.parse(dt));
			c.add(Calendar.DATE, 1);
			dt = dateFormat.format(c.getTime());
			if (elementValueMap.get("PRIORITY_ID").equals(priorityHighId)
					&& !elementValueMap.get("estimatedTime").equals(dateFormat.format(date))) {
				elementValueMap.put("estimatedTime", dt);
			}

			//Change Priority to High If the estimated time is today
			if (elementValueMap.get("estimatedTime").equals(dateFormat.format(date))) {
				elementValueMap.put("PRIORITY_ID", priorityHighId);
			}

			String currentStatusName = formDao.getFromInfoLookup("REQUESTSTATUS", LookupType.ID,
					elementValueMap.get("REQUESTSTATUS_ID"), "name");
			//set completion date
			if (elementValueMap.get("LASTSTATUS_ID") != null && !elementValueMap.get("LASTSTATUS_ID").isEmpty()) {
				String lastStatusName = formDao.getFromInfoLookup("REQUESTSTATUS", LookupType.ID,
						elementValueMap.get("LASTSTATUS_ID"), "name");
				if (!lastStatusName.equals("Approved") && currentStatusName.equals("Approved")) {
					elementValueMap.put("completionDate", dateFormat.format(date));
				}
				//if request cancelled and has experiment update it in activity log
				if (currentStatusName.equals("Cancelled")) {
					List<String> experimentsCreatorsId = generalDao.getListOfStringBySql(
							"select t.creator_id from FG_S_Experiment_ALL_V t where t.ORIGINREQUESTID='" + formId
									+ "'");
					for (String CreatorId : experimentsCreatorsId) {
						Map<String, String> infoMap = new HashMap<String, String>();
						infoMap.put("RequestNumber", elementValueMap.get("requestName"));
						infoMap.put("experimentOwnerId", CreatorId);
						generalUtilLogger.logWrite(LevelType.INFO, "", formId, ActivitylogType.CancelledRequest,
								infoMap);
					}

				}

			}

			elementValueMap.put("LASTSTATUS_ID", elementValueMap.get("REQUESTSTATUS_ID"));
			if(generalUtil.getNull(elementValueMap.get("useAsDefaultData")).equals("1"))
			{
				if(generalUtil.getNull(elementValueMap.get("default_hst")).isEmpty()){
					elementValueMap.put("default_hst", "default");
				}
			}
			String parentId = elementValueMap.get("parentId");
			String parentFormCodeEntity = formDao.getFormCodeEntityBySeqId("", parentId);
			if(parentFormCodeEntity.equals("Action") &&
					currentStatusName.equals("Waiting")){
				elementValueMap.put("SMARTSELECTLIST", generalUtil.getNull(elementValueMap.get("ACTION_SAMPLE_ID")));
			}

		} else if (formCode.equals("RequestMain")) {
			String nextFormNumberId = formIdCalc.getNextFormNumberIdByFormId(formCode, formId, elementValueMap, userId,
					pivotTable);
			if (nextFormNumberId == null) {
				return -1;
			}
			auditTrailChangeType = !nextFormNumberId.isEmpty() ? 1 : 0;

			if (!nextFormNumberId.isEmpty()) {
				generalUtilLogger.logWriter(LevelType.DEBUG,
						generalUtil.mapToString("set request number - elementValueMap before event:", elementValueMap),
						ActivitylogType.SaveEvent, formId);
				elementValueMap.put("formNumberId", nextFormNumberId);
				elementValueMap.put("requestName", nextFormNumberId);

				String plannedStatusId = formDao.getFromInfoLookup("REQUESTSTATUS", LookupType.NAME, "Planned", "id");
				elementValueMap.put("REQUESTSTATUS_ID", plannedStatusId);
			}
		}
		/***************************************************************** Experiment Series *****************************************************************/
		else if (formCode.equals("ExperimentSeries")) {
			DateFormat dateFormat = new SimpleDateFormat(generalUtil.getConversionDateFormat());
			Date date = new Date(); //current date for necessary cases
			//set last modification date
			generalUtilLogger.logWriter(
					LevelType.DEBUG, generalUtil
							.mapToString("set last modification date - elementValueMap before event:", elementValueMap),
					ActivitylogType.SaveEvent, formId);
			elementValueMap.put("lastModiDate", dateFormat.format(date));
		}

		/***************************************************************** Experiment Series Main ************************************************************/

		else if (formCode.equals("ExpSeriesMain")) {
			//String nextFormNumberId = getNextFormNumberIdByFormId(formCode, formId, elementValueMap, userId,pivotTable);

			String nextFormNumberId = "";
			String sql;
			sql = "select nvl(max(fg_get_numeric(t.expseriesplannum)),0) + 1 from fg_s_experimentseries_pivot t";
			//sql="select nvl(max(fg_get_numeric(t.formid)),0) + 1 from fg_s_experimentseries_pivot t";

			nextFormNumberId = generalDao.selectSingleString(sql);

			DateFormat dateFormat = new SimpleDateFormat(generalUtil.getConversionDateFormat());
			Date date = new Date();

			if (!nextFormNumberId.isEmpty()) {
				generalUtilLogger.logWriter(LevelType.DEBUG, generalUtil
						.mapToString("set Experiment Series number - elementValueMap before event:", elementValueMap),
						ActivitylogType.SaveEvent, formId);
				elementValueMap.put("formNumberId", nextFormNumberId);
				elementValueMap.put("expSeriesPlanNum", elementValueMap.get("formNumberId"));
				elementValueMap.put("lastModiDate", dateFormat.format(date));
			}

			//Check if numberOfPlannedExp >0 , if yes- should create the expinseries table

			String parentId = formId;//Here it is experimentSeries
			formId = formSaveDao.getStructFormId("ExpInSeries");

			String numberOfPlannedExp = elementValueMap.get("numberOfPlannedExp");
			if (!numberOfPlannedExp.isEmpty()) {
				for (int i = 0; i < Integer.parseInt(numberOfPlannedExp); i++) {
					//					sql = "insert into FG_S_ExpInSeries_PIVOT (TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE,EXPINSERIESNAME) VALUES (SYSDATE,'" + userId + "',null,1,'"+formId+"','"+parentId+"','ExpInSeries','"+String.valueOf(i+1)+"')";
					//					insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_EXPINSERIES_PIVOT", formId);
					//					sql = "insert into FG_S_FORMULATIONPROPREF_PIVOT (TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE,EXPERIMENTINDEX) VALUES (SYSDATE,'" + userId + "',null,1,'"+formId+"','"+parentId+"','FORMULATIONPROPREF','"+String.valueOf(i+1)+"')";
					//					insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_FORMULATIONPROPREF_PIVOT", formId);
					formId = formSaveDao.getStructFormId("FormulationPropRef");

					sql = "insert into FG_S_FORMULATIONPROPREF_PIVOT (TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE,EXPERIMENTINDEX,CREATED_BY,CREATION_DATE) VALUES (SYSDATE,'"
							+ userId + "',null,1,'" + formId + "','" + parentId + "','FormulationPropRef','"
							+ String.valueOf(i + 1) + "','" + userId + "',SYSDATE)";
					formSaveDao.insertStructTableByFormId(sql, "FG_S_FORMULATIONPROPREF_PIVOT", formId);
				}
			}
		}
		/********** FornulantRef ***********/
		else if (formCode.equals("FornulantRef")) {
			auditTrailChangeType = preFormSaveSample(formCode, formId, elementValueMap, userId, pivotTable, sbInfo);
		}

		/********* WorkupMain ***********/
		else if (formCode.equals("WorkupMain")) {
			String statusName = formDao.getFromInfoLookup("WORKUPSTATUS", LookupType.NAME, "Active", "id");
			String stageName = formDao.getFromInfoLookup("STAGESTATUS", LookupType.NAME, "General Data Input", "id");

			/*
			 * select t.STAGESTATUSNAME, t.STAGESTATUS_ID
			 * from FG_S_STAGESTATUS_ALL_V t
			 * where t.WORKUPTYPE like '%Feeding' and t.STAGESTATUSNAME = 'General Data Input';
			 */
			elementValueMap.put("STATUS_ID", statusName);
			elementValueMap.put("STAGE_ID", stageName);
			if (!elementValueMap.get("ACTION_ID").isEmpty()) {
				String sampleList = generalDao.selectSingleStringNoException(
						"select sampletable" + " from fg_s_sampleselect_v where parentId = '"
								+ elementValueMap.get("ACTION_ID") + "'" + generalUtilFormState
										.getWherePartForTmpData("sampleselect", elementValueMap.get("ACTION_ID")));

				if (!generalUtil.getNull(sampleList).isEmpty()) {
					String firstSampleId = sampleList.split(",")[0];
					elementValueMap.put("sampleNumber",
							formDao.getFromInfoLookup("SAMPLE", LookupType.ID, firstSampleId, "name"));
					elementValueMap.put("sampleId", firstSampleId);
				}
				String stepStatusName = generalDao.selectSingleStringNoException("select stepstatusname"
						+ " from fg_s_action_all_v where action_id = '" + elementValueMap.get("ACTION_ID") + "'");
				if (stepStatusName.equals("Planned") || stepStatusName.isEmpty()) {
					statusName = formDao.getFromInfoLookup("WORKUPSTATUS", LookupType.NAME, "Planned", "id");
					elementValueMap.put("STATUS_ID", statusName);
				}
			}
		}
		/********* Workup ***********/
		else if (formCode.startsWith("Workup") || formCode.startsWith("Wu")) {
			if (formCode.equals("WuFiltraWashingRef") || formCode.equals("WuFiltraSqueezRef")) {
				if (generalUtil.getNull(elementValueMap.get("pressure")).isEmpty()
						&& generalUtil.getNull(elementValueMap.get("centrifugeSpeed")).isEmpty()) {
					integrationValidation.validate(ValidationCode.CHECK_MANDATORY_FIELDS_WUFILTRAWASHINGREF, formCode,
							formId, "na", sbInfo);
				}
			}
			if (formCode.startsWith("Workup")) {
				if (saveAction.equals("RETURN_PREV_STAGE_STATUS")) {
					//					String table = "FG_S_Workup_PIVOT";

					String prevStageStatus = elementValueMap.get("prevStageStatus");
					String firstStageStatus = elementValueMap.get("firstStage");

					String isProcessStart = elementValueMap.get("isProcessStart");
					//when the stage returns to the first one then isProcessStart updated to 0
					if (prevStageStatus.equals(firstStageStatus)) {
						isProcessStart = "0";
					}
					elementValueMap.put("isProcessStart", isProcessStart);
					elementValueMap.put("STAGE_ID", prevStageStatus);
					elementValueMap.put("prevStageStatus", "");
					elementValueMap.put("nextStageStatus", "");
					/*List<String> colList = Arrays.asList("STAGE_ID", "prevStageStatus", "ISPROCESSSTART");
					String sql_ = "update " + table + " set STAGE_ID = '" + prevStageStatus
							+ "', prevStageStatus = NULL, ISPROCESSSTART = " + isProcessStart + " where FORMID = '" + formId
							+ "'";
					formSaveDao.updateStructTableByFormId(sql_, table, colList, formId);*/
				}
			}
			generalUtilConfig.SaveEventHandler(formCode, formId, elementValueMap, userId, "PreSave");

		}
		/********* SelfTest ***********/
		else if (formCode.startsWith("STest")) {
			if (formCode.equals("STestWetSieve")) {
				String wSMass = elementValueMap.get("wetSieveMass");
				String wSPercentage = elementValueMap.get("WetSieve");
				if (!wSMass.isEmpty() && wSPercentage.isEmpty()) {
					elementValueMap.put("disableWetSieve", "perc");
				} else if (wSMass.isEmpty() && !wSPercentage.isEmpty()) {
					elementValueMap.put("disableWetSieve", "mass");
				} else if (wSMass.isEmpty() && wSPercentage.isEmpty()) {
					elementValueMap.put("disableWetSieve", "0");
				} else if (!wSMass.isEmpty() && !wSPercentage.isEmpty()) {
					elementValueMap.put("disableWetSieve", "mass");
					elementValueMap.put("wetSieveMass", "");
				}
			}
			generalUtilConfig.SaveEventHandler(formCode, formId, elementValueMap, userId, "PreSave");
			if (formCode.equals("STestSuspensibilit")) {
				integrationValidation.validate(ValidationCode.CHECK_INVALID_NEGATIVE_RESULT, formCode, formId,
						elementValueMap.get("result"), sbInfo);
			}
		}
		/********* SelfTest ***********/
		else if (formCode.equals("SelfTest")) {//instrument remove validation 
//			//remove fields that not belong to the changed selftest type, the fields cleanDataByNewTypeFlag (in the elementValueMap map) indicated that we are in this scenario 
//			if (generalUtil.getNull(elementValueMap.get("cleanDataByNewTypeFlag")).equals("1")) {.. cleanDataByNewTypeFlag not in use we use code that support temp data as in clean data button in the event -> cleanOnChangeSelfTestTypeToNonNumeric
		}
		/********** Sample ***********/
		else if (formCode.equals("Sample")) {
			auditTrailChangeType = preFormSaveSample(formCode, formId, elementValueMap, userId, pivotTable, sbInfo);
		}
		/********** SampleMain ***********/
		else if (formCode.equals("SampleMain")) {
			auditTrailChangeType = preFormSaveSample(formCode, formId, elementValueMap, userId, pivotTable, sbInfo);
		}
		/*********** BatchMain ***********/
		else if (formCode.equals("BatchMain")) {
			if (!elementValueMap.get("SAMPLE_ID").isEmpty()) {
				//updates the matreial_id of the batch to be the one that referenced to the sample
				String sql = "Select PRODUCTID from FG_S_SAMPLE_ALL_V where FORMID = "
						+ elementValueMap.get("SAMPLE_ID");
				elementValueMap.put("INVITEMMATERIAL_ID", generalDao.selectSingleString(sql));

				//updates the quantity
				sql = "Select AMMOUNT from FG_S_SAMPLE_ALL_V where FORMID = " + elementValueMap.get("SAMPLE_ID");
				elementValueMap.put("quantity", generalDao.selectSingleString(sql));
				//updates the purity
				sql = "Select nvl((Select RESULT_VALUE from FG_I_SELECTEDRESULTS_V r " + "where '"
						+ elementValueMap.get("INVITEMMATERIAL_ID") + "' = r.RESULT_MATERIAL_ID and r.SAMPLE_ID ='"
						+ elementValueMap.get("SAMPLE_ID") + "'" + " and RESULT_NAME = 'Assay'),'100') from DUAL";
				String purity = generalDao.selectSingleString(sql);
				elementValueMap.put("purity", purity);
			}
			//set formNumberId- Batch number
			String nextFormNumberId = formIdCalc.getNextFormNumberIdByFormId(formCode, formId, elementValueMap, userId,
					pivotTable);
			if (nextFormNumberId == null) {
				return -1;
			}
			auditTrailChangeType = !nextFormNumberId.isEmpty() ? 1 : 0;

			if (!nextFormNumberId.isEmpty()) {
				generalUtilLogger.logWriter(LevelType.DEBUG,
						generalUtil.mapToString(
								"initialize internal batch number from experiment "
										+ elementValueMap.get("EXPERIMENT_ID") + "- elementValueMap before event:",
								elementValueMap),
						ActivitylogType.SaveEvent, formId);
				String batchNumber = formDao.getFromInfoLookup("EXPERIMENT", LookupType.ID,
						elementValueMap.get("EXPERIMENT_ID"), "Name") + "-B" + nextFormNumberId;
				elementValueMap.put("formNumberId", batchNumber);
				elementValueMap.put("invItemBatchName", batchNumber);//necessary for future use in the child forms in DDL-id/val
			}
			//sets the material name
			String materialName = formDao.getFromInfoLookup("InvItemMaterial", LookupType.ID,
					elementValueMap.get("INVITEMMATERIAL_ID"), "name");
			elementValueMap.put("materialNameInf", materialName);
			
			if(!elementValueMap.get("lastStep").equals("1")){//Only Batch that was created from the last step will display in the Recipe
				elementValueMap.put("RECIPEFORMULATION_ID","");
			}
		}
		/********** PreperationRef ***********/
		else if (formCode.equals("PreperationRef")) {
			String nextFormNumberId = formIdCalc.getNextFormNumberIdByFormId(formCode, formId, elementValueMap, userId,
					pivotTable);
			if (nextFormNumberId == null) {
				return -1;
			}
			auditTrailChangeType = !nextFormNumberId.isEmpty() ? 1 : 0;

			if (!nextFormNumberId.isEmpty()) {
				generalUtilLogger.logWriter(LevelType.DEBUG,
						generalUtil.mapToString("initialize order - elementValueMap before event:", elementValueMap),
						ActivitylogType.SaveEvent, formId);
				elementValueMap.put("formNumberId", nextFormNumberId);
			}
		} else if (formCode.equals("SpecificationRef")) {
			if (generalUtil.getNull(elementValueMap.get("range")).equals("1")) {
				String criteria1 = elementValueMap.get("criteria1");
				String criteria2 = elementValueMap.get("criteria2");
				String normalValue1 = generalDao
						.selectSingleString("select  fg_get_num_display(" + elementValueMap.get("value1") + ",0,1"
								+ (elementValueMap.get("UOM1_ID").isEmpty() ? "" : "," + elementValueMap.get("UOM1_ID"))
								+ ") from dual");
				String normalValue2 = generalDao
						.selectSingleString("select  fg_get_num_display(" + elementValueMap.get("value2") + ",0,1"
								+ (elementValueMap.get("UOM2_ID").isEmpty() ? "" : "," + elementValueMap.get("UOM2_ID"))
								+ ") from dual");
				integrationValidation.validate(ValidationCode.INVALID_SPECIFICATIONS, formCode, formId,
						new String(normalValue1.toString() + "," + normalValue2.toString() + "," + criteria1.toString()
								+ "," + criteria2.toString()),
						sbInfo);
			}
		}
		/********** Template ***********/
		else if (formCode.equals("Template")) {

			DateFormat dateFormat = new SimpleDateFormat(generalUtil.getConversionDateFormat());
			Date date = new Date();

			//Map<String,String> userData = formDao.getFromInfoLookupAll("USER", LookupType.ID,elementValueMap.get("creator"));
			//String teamLeader = generalUtil.getNull(userData.get("TEAMLEADER_ID"));

			String statusName = formDao.getFromInfoLookup("TEMPLATESTATUS", LookupType.ID,
					elementValueMap.get("STATUS_ID"), "name");

			if (generalUtil.getNull(statusName).equals("Approved")) {
				if (elementValueMap.get("approver").equals(userId))///*teamLeader.equals(userId)*/ 
				{
					elementValueMap.put("approvalDate", dateFormat.format(date));
					//elementValueMap.put("approver", userData.get("TEAMLEADER_ID"));ta fix bug 5889
					elementValueMap.put("lastModificationDate", dateFormat.format(date));
				} else if (!formDao.getFromInfoLookup("template", LookupType.ID, formId, "STATUS_ID")
						.equals(elementValueMap.get("STATUS_ID"))) {//last status is not approved
					//String statusId = formDao.getFromInfoLookup("TEMPLATESTATUS", LookupType.NAME, "Planned", "id");
					//elementValueMap.put("STATUS_ID", statusId);
					integrationValidation.validate(ValidationCode.NOT_TEMPLATE_APPROVER, formCode, formId, "na",
							sbInfo);

				}
			}

			else if (generalUtil.getNull(statusName).equals("Planned")) {
				elementValueMap.put("lastModificationDate", dateFormat.format(date));
			}
		}
		/********* TemplateMain ***********/
		//		else if (formCode.equals("TemplateMain")) {
		//			String statusId = formDao.getFromInfoLookup("TEMPLATESTATUS", LookupType.NAME,
		//					"Planned", "id");		
		//			
		//			elementValueMap.put("STATUS_ID", statusId);
		//			String nextFormNumberId =  getNextId(elementValueMap, "Template", formId, userId, "FG_S_Template_PIVOT");
		//
		//			//String nextFormNumberId = getNextFormNumberIdByFormId(formCode, formId, elementValueMap, userId,
		//				//	pivotTable);
		//			if (nextFormNumberId == null) {
		//				return -1;
		//			}
		//			auditTrailChangeType = !nextFormNumberId.isEmpty() ? 1 : 0;
		//			if (!nextFormNumberId.isEmpty()) {
		//				elementValueMap.put("formNumberId", "T"+nextFormNumberId);
		//			}
		//		}

		else if (formCode.equals("TemplateMain")) {
			String statusId = formDao.getFromInfoLookup("TEMPLATESTATUS", LookupType.NAME, "Planned", "id");

			elementValueMap.put("STATUS_ID", statusId);

			String nextFormNumberId = generalDao
					.selectSingleString(String.format("select count(*) from fg_s_template_v t "));
			Integer.parseInt(nextFormNumberId);
			if (nextFormNumberId.isEmpty() || nextFormNumberId.equals("0")) {
				nextFormNumberId = "1";
			} else {
				int intOfNextFormNumberId = Integer.parseInt(nextFormNumberId);
				intOfNextFormNumberId++;
				nextFormNumberId = String.valueOf(intOfNextFormNumberId);
			}
			elementValueMap.put("formNumberId", "T-00" + nextFormNumberId);
			
			if(!generalUtil.getNull(elementValueMap.get("expDesign")).isEmpty()){
				formSaveDao.updateSingleStringInfoNoTryCatch("update fg_s_exprunplanning_pivot set copytotemplate = '1' where formid in(" + elementValueMap.get("expDesign")+")");
			}

		} else if (formCode.equals("SelfTestType")) {
			integrationValidation.validate(ValidationCode.CHECK_SELEFTESTTYPE_FILE_EXTENSION, formCode, formId,
					elementValueMap.get("instractionsFile"), sbInfo);
		} else if (formCode.equals("SelfTestMain")) {
			if (!elementValueMap.get("ACTION_ID").isEmpty()) {
				//insert all the samples connected to the action to the selftest
				List<String> actionSamplesList = generalDao
						.getListOfStringBySql("select SAMPLE_ID from fg_s_sampleselect_all_v" + " where parentId = '"
								+ elementValueMap.get("ACTION_ID") + "'" + generalUtilFormState
										.getWherePartForTmpData("sampleselect", elementValueMap.get("ACTION_ID")));
				
				String sessionId_ = generalUtilFormState.getSessionId(formId);
				formDao.insertToSelectTable("SampleSelect", formId, "SAMPLETABLE", actionSamplesList, false, userId,sessionId_);
				/*String lastSampleId = generalDao.selectSingleStringNoException(
						"select t.SAMPLE_ID" "select distinct last_value (s.SAMPLE_ID) over (partition by t.PARENTID order by s.CREATION_DATE)" 
								+ " from (select distinct s.SAMPLE_ID,s.CREATION_DATE from "
								+ "(select * from fg_s_sampleselect_all_v t" + " where 1=1 "
								+ generalUtilFormState.getWherePartForTmpData("SAMPLESELECT", elementValueMap.get("ACTION_ID"))
								+ " ) t ,fg_s_sample_v s" + " where t.SAMPLE_ID = s.SAMPLE_ID " + " and t.parentid = '"
								+ elementValueMap.get("ACTION_ID") + "' order by s.CREATION_DATE desc) t "
								+ " where rownum<=1");*/
				String sampleList = generalDao.selectSingleStringNoException(
						"select sampletable" + " from fg_s_sampleselect_v where parentId = '"
								+ elementValueMap.get("ACTION_ID") + "'" + generalUtilFormState
										.getWherePartForTmpData("sampleselect", elementValueMap.get("ACTION_ID")));

				if (!sampleList.isEmpty()) {
					String firstSampleId = sampleList.split(",")[0];
					elementValueMap.put("sampleId", firstSampleId);
					elementValueMap.put("sampleNumber",
							formDao.getFromInfoLookup("SAMPLE", LookupType.ID, firstSampleId, "name"));
					//if added a sample from sample search(not from the table) then add it to the sample table
					formDao.insertToSelectTable("SampleSelect", formId, "SAMPLETABLE", Arrays.asList(firstSampleId),
							true, userId, sessionId_);
				}
			}
		} else if (formCode.equals("ManualResultsMSRef")) {
			integrationValidation.validate(ValidationCode.VALIDATE_MATERIALRESULT_FILLED, formCode, formId,
					elementValueMap, sbInfo);
		} else if (formCode.equals("OperationType")) {
			String thermalDegTo = generalUtil.getNull(elementValueMap.get("therdegStabRangeTo"));
			String thermalfrom = generalUtil.getNull(elementValueMap.get("therDegStabRangeFrom"));
			if ((thermalfrom.isEmpty() && !thermalDegTo.isEmpty())
					|| (!thermalDegTo.isEmpty()) && Integer.parseInt(thermalDegTo) < Integer.parseInt(thermalfrom)) {
				integrationValidation.validate(ValidationCode.ThermalDegToBigger, formCode, formId, "", sbInfo);
			}
			String mpTo = generalUtil.getNull(elementValueMap.get("mpTo"));
			String mpFrom = generalUtil.getNull(elementValueMap.get("mpFrom"));
			if ((mpFrom.isEmpty() && !mpTo.isEmpty())
					|| (!mpTo.isEmpty()) && Integer.parseInt(mpTo) < Integer.parseInt(mpFrom)) {
				integrationValidation.validate(ValidationCode.MpToBigger, formCode, formId, "", sbInfo);
			}
		} else if (formCode.equals("Document")) {
			/**If MSDS record (attachment or link) was added or edited by user (in inventory material/column/instrument), 
			list of trained users in Training tab shall be cleared.
			if document was added or edited contentChange = 1 (used in function updateTrainedUsers) **/
			if (generalUtil.getNull(elementValueMap.get("LINK_ATTACHMENT")).equals("Link")) {
				String lastDocumentName = generalUtil
						.getNull(formDao.getFromInfoLookup("Document", LookupType.ID, formId, "NAME"));
				if (!generalUtil.getNull(elementValueMap.get("documentName")).equals(lastDocumentName)) {
					elementValueMap.put("contentChange", "1");
				}
			} else {// Attachment
				String lastFileId = generalUtil
						.getNull(formDao.getFromInfoLookup("Document", LookupType.ID, formId, "documentUpload"));
				if (!generalUtil.getNull(elementValueMap.get("documentUpload")).equals(lastFileId)) {
					elementValueMap.put("contentChange", "1");
				}
				integrationValidation.validate(ValidationCode.INVALID_FILE_UPLOAD, formCode, formId,
						elementValueMap.get("documentUpload"), sbInfo);
				for (Entry<String, String> elem : elementValueMap.entrySet()) {
					//check if there were uploaded multiple files-> if so, then creates additional records of documents containing the the attached files
					if (elem.getKey().startsWith("documentUpload_")) {
						integrationValidation.validate(ValidationCode.INVALID_FILE_UPLOAD, formCode, formId,
								elem.getValue(), sbInfo);
						String documentId = formSaveDao.getStructFormId("Document");
						String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode,
								elementValueMap.get("parentId"));
						formSaveDao
								.insertStructTableByFormId(
										"insert into fg_s_document_pivot t (t.FORMID,t.TIMESTAMP,t.CHANGE_BY,t.CREATION_DATE,t.CREATED_BY,t.SESSIONID,t.ACTIVE,t.FORMCODE,t.FORMCODE_ENTITY, PARENTID, t.TABLETYPE, t.LINK_ATTACHMENT, t.DOCUMENTUPLOAD, t.DOCUMENTNAME, t.DESCRIPTION, t.SAMPLE_ID)"
												+ " values ('" + documentId + "', sysdate, " + userId + ", sysdate, "
												+ userId + ", " + sessionId + ", 1, 'Document', 'Document', '"
												+ elementValueMap.get("parentId") + "','"
												+ elementValueMap.get("tableType") + "','Attachment','"
												+ elem.getValue() + "','" + elementValueMap.get("documentName") + "','"
												+ elementValueMap.get("description") + "','"
												+ elementValueMap.get("SAMPLE_ID") + "')",
										"fg_s_document_pivot", documentId);
					}
				}
			}
			String parentId = elementValueMap.get("parentId");
			String parentFormCode = formDao.getFormCodeBySeqId(parentId);
			if(parentFormCode.equals("Request")){
				String defaultSample = generalDao.selectSingleStringNoException("select SAMPLEID from FG_S_SAMPLEDATAREF_V where parentid ='"+parentId+"' and parentid in(\r\n" + 
						"select parentid from FG_S_SAMPLEDATAREF_V t group by parentid having count(t.SAMPLEID)<2) "+generalUtilFormState.getWherePartForTmpData("SAMPLEDATAREF", parentId));
				elementValueMap.put("SAMPLE_ID", generalUtil.getNull(defaultSample));
			}

			//add dummy files to unittestuser
			if (generalUtil.getSessionUserName().equalsIgnoreCase("unittestuser")
					&& generalUtil.getNull(elementValueMap.get("LINK_ATTACHMENT")).equals("Attachment")) {
				if (generalUtil.getNull(elementValueMap.get("documentUpload")).equals("")) {
					elementValueMap.put("documentUpload", "100");
				}
			}
		}
		/******************************RecipeFormulation**********************************/
		/**
		 * sets the approval date when approving the recipe
		 * sets the recipe version when the status changed from approved to active
		 */
		else if(formCode.equals("RecipeFormulation")){
			String lastStatus_id = generalUtil.getEmpty(formDao.getFromInfoLookup("RecipeFormulation", LookupType.ID, formId, "STATUS_ID"),elementValueMap.get("LASTSTATUS_ID"));
			String lastStatusName = formDao.getFromInfoLookup("RECIPESTATUS", LookupType.ID, lastStatus_id, "name");
			elementValueMap.put("LASTSTATUS_ID", lastStatus_id);
			String currentStatusName = formDao.getFromInfoLookup("RECIPESTATUS", LookupType.ID,
					elementValueMap.get("STATUS_ID"), "name");
			if(currentStatusName.equals("Approved") && !lastStatusName.equals("Approved")){
				//set Approval Date
				generalUtilLogger.logWriter(LevelType.DEBUG,
						generalUtil.mapToString("set approval date - elementValueMap before event:", elementValueMap),
						ActivitylogType.SaveEvent, formId);
				DateFormat dateFormat = new SimpleDateFormat(generalUtil.getConversionDateFormat());
				String dateTime = generalDao.selectSingleString("select TO_CHAR(sysdate,'dd/MM/yyyy HH24:MI') from dual");
				Date date = new Date(); //current date for necessary cases
				elementValueMap.put("approvalDate", dateFormat.format(date));
				int currentVersion = Integer.parseInt(elementValueMap.get("version"));
				if(currentVersion>1){//this is not the first time approving the recipe
					elementValueMap.put("reasonForChange", "");
				}
			} else if (lastStatusName.equals("Approved") && currentStatusName.equals("Active")) {//experiment is returned from approved to active status
				//set recipe version
				String currentVersion = getNextVersion(formCode, formId);
				if (!currentVersion.isEmpty()) {
					generalUtilLogger.logWriter(LevelType.DEBUG,
							generalUtil.mapToString("set recipe version - elementValueMap before event:", elementValueMap),
							ActivitylogType.SaveEvent, formId);
					elementValueMap.put("version", currentVersion);
				}
			}
		}
		else if(formCode.equals("ExperimentGroup")) {
			if(elementValueMap.get("groupNumber").isEmpty()) {
			elementValueMap.put("groupNumber", formIdCalc.getExperimentGroupNumber("fg_s_experimentgroup_pivot", elementValueMap, formId, elementValueMap.get("parentId"), userId));
			}
		}

		//		integrationValidation.validate(ValidationCode.CHECK_VALID_PROJECTTYPENAME, formCode, formId, elementValueMap);

		return auditTrailChangeType;
	}

	@Override
	/**
	 * elementValueInfATMap - contains the map values before we removed unsaved values
	 */
	public int postFormSaveEvent(Long stateKey, String formCode, String formId, Map<String, String> elementValueMap,
			Map<String, String> elementAdditinalDataoMap, String userId, String isNew, String saveAction,List<DataBean> dataBeanReturnList, Map<String, String> elementValueInfATMap,
			StringBuilder sbInfo) throws Exception {
		int auditTrailChangeType = 0;
		String formCodeEntity = formDao.getFormCodeEntityBySeqId(formCode, formId);
		//		if (formCodeEntity.equals("Project") && userId.equals("100")) {
		//			String dbTransactionId = generalUtil.getDBTransaction();
		//			System.out.println("dbTransactionId=" + dbTransactionId);
		//			formSaveDao.updateStructTableByFormId("update FG_S_PROJECT_PIVOT set STATUS_ID = 1179 where formid = '" + formId + "'"  , "FG_S_PROJECT_PIVOT",
		//				Arrays.asList("STATUS_ID"), formId);
		//			formSaveDao.updateStructTableByFormId("update FG_S_PROJECT_PIVOT set INGREDIENTTYPE_ID = '151778,151792,1099' where formid = '" + formId + "'"  , "FG_S_PROJECT_PIVOT",
		//					Arrays.asList("STATUS_ID"), formId);
		//			formDao.insertToSelectTable("UsersCrew", formId, "USER_ID", Arrays.asList("1133","1112"), false, userId);
		//		}
		
		if (formCodeEntity.equals("Project")) {
			//271020 task 25969
			String projectStatus = formDao.getFromInfoLookup("STATUS", LookupType.ID, elementValueMap.get("STATUS_ID"),
					"name");
		    String projectType=formDao.getFromInfoLookup("ProjectType", LookupType.ID, elementValueMap.get("PROJECTTYPE_ID"), "name");

			//delete empty rows in the component table
			 String sql = "select t.formid \n"
					  + " from FG_S_MaterialComponent_PIVOT t \n"
					  + " where  nvl(material_id,'0') = '0' "
					  + "and parentid = '"+formId+"' and active = 1 and sessionid is null";
			
			List<String> componentId_listForDelete = generalDao.getListOfStringBySql(sql);
			for(String componentId: componentId_listForDelete)
			{
				formSaveDao.deleteStructTableByFormId("delete from FG_S_MaterialComponent_PIVOT t where t.formid = '"+componentId+"'","FG_S_MaterialComponent_PIVOT", componentId);
			}
			if(projectType.equals("Formulation")) {
				integrationValidation.validate(ValidationCode.CHECK_HASCOMPONENT,formCode,formId,elementValueMap,sbInfo);
				if(projectStatus.equals("Active")) {
			    	commonFunc.createDefaultMaterialsPerProject(formId, userId);
			    }
			}
			
		    
		    sql = "delete from FG_S_USERSCREW_PIVOT t WHERE t.parentid = '" + formId + "' and sessionid is not null";
			formSaveDao.updateSingleStringInfo(sql); 
			System.out.println("delete old session usercrew");
			
			String projectManager_id = elementValueMap.get("PROJECTMANAGER_ID");
			formDao.insertToSelectTable("USERSCREW", formId, "USER_ID", Arrays.asList(projectManager_id), false, userId, null);
			
			
		}
		if(formCode.equals("SubProject")) {
			String isFirstSubProject=generalDao.selectSingleString("select count(*) from fg_s_subproject_all_v t where t.PROJECT_ID="+elementValueMap.get("PROJECT_ID"));
			if(isFirstSubProject.equals("1")) {
				Map<String,String> projectDetails=generalDao.sqlToHashMap("select t.status_id ,t.projecttype_id from fg_s_project_all_v t where t.PROJECT_ID="+elementValueMap.get("PROJECT_ID"));
				String projectStatus = formDao.getFromInfoLookup("STATUS", LookupType.ID,projectDetails.get("STATUS_ID"),
						"name");
			    String projectType=formDao.getFromInfoLookup("ProjectType", LookupType.ID,projectDetails.get("PROJECTTYPE_ID") , "name");
			if(projectStatus.equals("Planned")) {
				String activeStatusId=formDao.getFromInfoLookup("STATUS", LookupType.NAME,"Active" , "id");
				String sql_ = "update fg_s_project_pivot set status_id = '"+activeStatusId+"'"
						+" where formId = '" + elementValueMap.get("PROJECT_ID")+"'";
				formSaveDao.updateStructTableByFormId(sql_, "FG_S_PROJECT_PIVOT",
						Arrays.asList("status_id"),elementValueMap.get("PROJECT_ID"));
				if(projectType.equals("Formulation")){
					commonFunc.createDefaultMaterialsPerProject(elementValueMap.get("PROJECT_ID"), userId);	
				}
			}
			
				
			}
		}
		if(formCode.equals("UOM")){
			generalDao.updateSingleString("begin dbms_mview.refresh('FG_I_MATREF_DEFAULT_VALUES_MV'); end;");
		}
		
		if (formCodeEntity.equals("Experiment")) {
			String lastStatusName = formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.ID,
					elementValueMap.get("LASTSTATUS_ID"), "name");
			String currentStatusName = formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.ID,
					elementValueMap.get("STATUS_ID"), "name");
			/** Calc **/
			if (formCode.equals("Experiment")) {
				integrationCalc.doCalc("MassBallanceCalc", "OnSave", "", elementValueMap, null, null, formCode, formId,
						userId);
				
				removeEmptyRowInEditTable(formId, "paramRef", "PARAMETER_ID", "paramRef_id", false);
				if (currentStatusName.equals("Active") && lastStatusName.equals("Planned") ){
					//in the organic experiment- changing the experiment to active- activates all the planned steps
					commonFunc.updateStepsStatusToActive(formId, "", userId, false);
				}
			}

			if(formCode.equals("ExperimentAn")) {
				integrationValidation.validate(ValidationCode.CHECK_TESTED_COMPONENT_MANDATORY, formCode, formId, "",
						new StringBuilder());
			}
			/*String protocolTypeName = formDao.getFromInfoLookup("Experiment", LookupType.ID, formId, "PROTOCOLTYPENAME");
			if(protocolTypeName.equals("Analytical")||protocolTypeName.equals("Parametric")){
				if ( !lastStatusName.equals("Active") && !currentStatusName.equals("Planned")
						&& !formCode.equals("ExpCloneMain")) {//status has been changed to active
					integrationValidation.validate(ValidationCode.INVALID_EXPERIMENT_NONFAMILIAR_STATUS, formCode, formId,
							formId, sbInfo);
				}
			}*/
			if (formCode.equals("ExperimentPrVS") && elementAdditinalDataoMap.get("density").isEmpty()) {//DENSITY_RES_UOM_ID
				/// elementAdditinalDataoMap.put("DENSITY_RES_UOM_ID",null);
				List<String> colList = Arrays.asList("DENSITY_RES_UOM_ID");
				String sql = "update fg_formadditionaldata t" + " set value = '' where t.parentid ='" + formId
						+ "' and t.entityimpcode = 'DENSITY_RES_UOM_ID'";
				formSaveDao.updateAdditinalData(sql, colList, formId);
			}
			if (lastStatusName.equals("Approved") && currentStatusName.equals("Active")) {
				List<String> steps = generalDao.getListOfStringBySql(
						"Select step_id from fg_s_step_v t where t.experiment_id = '" + formId + "'");
				String stepActiveStatus = formDao.getFromInfoLookup("STEPSTATUS", LookupType.NAME, "Active", "id");
				for (String stepId : steps) {
					String sql_ = "update fg_s_step_pivot set laststatus_id = status_id, status_id = '"
							+ stepActiveStatus + "' where formId = '" + stepId + "' and status_id!= '"
							+ stepActiveStatus + "'";
					formSaveDao.updateStructTableByFormId(sql_, "FG_S_STEP_PIVOT",
							Arrays.asList("laststatus_id", "status_id"), stepId);
					/*if (formcode.equals("Step")) {
						generalDao.callPackageFunction("FG_ADAMA", "CREATE_EXPERIMENT_MATERIAL_SS", map);//create a snapshot for displaying the right tables in actual or planned state
					} else {
						generalDao.callPackageFunction("FG_ADAMA", "CREATE_EXPERIMENT_FORMULANT_SS", map);//create a snapshot for displaying the right tables in actual or planned state
					}*/
				}
			} else if(formCode.equals("ExperimentFor")){
				//delete the compositions that have no material
				String sql = "select formid from fg_s_composition_v\n"
						+ " where active = 1 and parentid = '"+formId+"'"
						+ " and sessionid is null"
						+ " and nvl(invitemmaterial_id,'0') = '0'\n"
						+ " union all\n"
						 //delete the compositions that have no material in the experiment's steps
						+" select formid from fg_s_composition_v\n"
						+ " where active = 1 and parentid in (select step_id from fg_s_step_v where experiment_id = '"+formId+"')"
						+ " and sessionid is null"
						+ " and nvl(invitemmaterial_id,'0') = '0'";
				List<String> compositionNoMaterial = generalDao.getListOfStringBySql(sql);
				for(String compositionId:compositionNoMaterial){
					sql ="delete from fg_s_composition_pivot where formid = '"+compositionId+"'";
					formSaveDao.deleteStructTable(sql,"fg_s_composition_pivot" , "formid", compositionId);
				}	
				
				//delete empty rows in the instrument table
				sql = "select t.formid \n"
						  +" from FG_S_INSTRUMENTREF_PIVOT t \n"
						  +" where INVITEMINSTRUMENT_ID is null and parentid = '"+formId+"' and active = 1 and sessionid is null";
				
				List<String> instrumentRefId_listForDelete = generalDao.getListOfStringBySql(sql);
				for(String instrumentRefId: instrumentRefId_listForDelete)
				{
					formSaveDao.deleteStructTableByFormId("delete from FG_S_INSTRUMENTREF_PIVOT t where t.formid = '"+instrumentRefId+"'","FG_S_INSTRUMENTREF_PIVOT", instrumentRefId);
				}
				
				//delete empty rows in the equipment table
				sql = "select t.formid \n"
						  +" from FG_S_EQUIPMENTREF_PIVOT t \n"
						  +" where EQUIPMENTREFNAME is null and fg_get_RichText_display(description) is null and parentid = '"+formId+"' and active = 1 and sessionid is null";
				
				List<String> equipmentRefId_listForDelete = generalDao.getListOfStringBySql(sql);
				for(String equipmentRefId: equipmentRefId_listForDelete)
				{
					formSaveDao.deleteStructTableByFormId("delete from FG_S_EQUIPMENTREF_PIVOT t where t.formid = '"+equipmentRefId+"'","FG_S_EQUIPMENTREF_PIVOT", equipmentRefId);
				}
				if (currentStatusName.equals("Active") && lastStatusName.equals("Planned") ){
					//in the formulation experiment- changing the experiment to active- activates all the planned steps
					commonFunc.updateStepsStatusToActive(formId, "", userId, false);
				}
				if (!currentStatusName.equals("Planned")) {//fixed bug 8923 
					String inventory_unfamiliar  = commonFunc.get_inventory_unfamiliar_list(formId,Arrays.asList("-1"),formCode,null,true);
					if(!inventory_unfamiliar.isEmpty()){
						throw new Exception(inventory_unfamiliar.split("##")[0]);
					}
				}
				if (lastStatusName.equals("Completed") && currentStatusName.equals("Approved") && elementValueMap.get("experimentVersion").equals("01") ) {
					//when changing the status from completed to approved and the version is 1- then the quantities in the batches should be decreased
					sql = "select distinct *\n"
							+ "from fg_s_composition_all_v\n"
							+ "where parentid = '"+formId+"'\n"
							+ "and active=1\n"
							+ "and sessionid is null";
					List<Map<String,Object>> compositionList = generalDao.getListOfMapsBySql(sql);
					for(Map<String, Object> compositionRowData : compositionList) {
						String material_id = compositionRowData.get("INVITEMMATERIAL_ID")!=null?compositionRowData.get("INVITEMMATERIAL_ID").toString():"";
						String batch_id = compositionRowData.get("BATCH_ID")!=null?compositionRowData.get("BATCH_ID").toString():"";
						String actualWeight = compositionRowData.get("ACTUAL")!=null?compositionRowData.get("ACTUAL").toString():"0";
						String actualWeightUomId = compositionRowData.get("ACTUALWEIGHTUOM_ID")!=null?compositionRowData.get("ACTUALWEIGHTUOM_ID").toString():"";
						if(material_id.isEmpty()||batch_id.isEmpty()||actualWeight.equals("0")){
							continue;
						}
						commonFunc.postUseOfBatch(
								actualWeight,actualWeightUomId,
								batch_id, formId, sbInfo);
					}
				}
			}
			

			List<String> ownerList = Arrays.asList(elementValueMap.get("OWNER_ID"));
			//String lastOwnerId = generalUtil.getNull(elementValueMap.get("LAST_OWNER_ID"));
			if (!generalUtil.getNull(elementValueMap.get("OWNER_ID")).isEmpty()
				//	&& !lastOwnerId.equals(elementValueMap.get("OWNER_ID"))
					) {
				//elementValueMap.put("LAST_OWNER_ID", elementValueMap.get("OWNER_ID"));
				formDao.insertToSelectTable("UsersCrew", formId, "USER_ID", ownerList, false, userId, null);//task 17852
			}
			
			/*
			 * String protocolTypeName = formDao.getFromInfoLookup("Experiment", LookupType.ID, formId, "PROTOCOLTYPENAME");
			 * if(protocolTypeName.equals("Analytical")||protocolTypeName.equals("Parametric")){
			 * //insert to the activity log of the instrument the operation of removed instruments
			 * List<String> removedInstruments = generalDao.getListOfStringBySql("Select INVITEMINSTRUMENT_ID from fg_i_instrument_activitylog_v t"
			 * + " where ACTION_FORMID = '"+formId+"'"
			 * + " and 'Instrument Equipment' in "
			 * + "(select first_value(OPERATION_TYPE) over (PARTITION BY INVITEMINSTRUMENT_ID,ACTION_FORMID order by TO_DATE(\"DATE\",'"+generalUtil.getConversionDateTimeFormat()+"') desc)"
			 * + " from fg_i_instrument_activitylog_v al "
			 * + " where al.INVITEMINSTRUMENT_ID = t.INVITEMINSTRUMENT_ID"
			 * + " and '"+formId+"' = al.ACTION_FORMID)"//gets all the records that the last operation on the instrument was equipment and not removed
			 * + " minus "
			 * + " Select INVITEMINSTRUMENT_ID from fg_s_instrumentref_all_v where PARENTID = '"+formId+"' and SESSIONID is NULL");
			 * for(String instrument_id:removedInstruments){
			 * Map<String,String> additionalInfo = new HashMap<>();
			 * additionalInfo.put("experiment_id", formId);
			 * additionalInfo.put("formCode", formCode);
			 * additionalInfo.put("experimentNumber", elementValueMap.get("experimentName"));
			 * additionalInfo.put("laboratoryName", formDao.getFromInfoLookup("LABORATORY", LookupType.ID, elementValueMap.get("LABORATORY_ID"), "NAME") );
			 * generalUtilLogger.logWrite(LevelType.Other, "", instrument_id, ActivitylogType.RemovedFomExperiment, additionalInfo);
			 * }
			 * //insert to activity log of instruments the operation of equipment in experiment
			 * List<String> addedInstruments = generalDao.getListOfStringBySql("Select INVITEMINSTRUMENT_ID from fg_s_instrumentref_all_v where PARENTID = '"+formId+"' and SESSIONID is NULL"
			 * + " MINUS "
			 * + " Select INVITEMINSTRUMENT_ID from fg_i_instrument_activitylog_v t"
			 * + " where ACTION_FORMID = '"+formId+"'"
			 * + " and 'Instrument Equipment' in"
			 * + "(select first_value(OPERATION_TYPE) over (PARTITION BY INVITEMINSTRUMENT_ID,ACTION_FORMID order by TO_DATE(\"DATE\",'"+generalUtil.getConversionDateTimeFormat()+"') desc)"
			 * + " from fg_i_instrument_activitylog_v al "
			 * + " where al.INVITEMINSTRUMENT_ID = t.INVITEMINSTRUMENT_ID"
			 * + " and '"+formId+"' = al.ACTION_FORMID)");//gets all the records that the last operation on the instrument was equipment and not removed
			 * for(String instrument_id:addedInstruments){
			 * Map<String,String> additionalInfo = new HashMap<>();
			 * additionalInfo.put("experiment_id", formId);
			 * additionalInfo.put("formCode", formCode);
			 * additionalInfo.put("experimentNumber", elementValueMap.get("experimentName"));
			 * additionalInfo.put("laboratoryName", formDao.getFromInfoLookup("LABORATORY", LookupType.ID, elementValueMap.get("LABORATORY_ID"), "NAME") );
			 * generalUtilLogger.logWrite(LevelType.Other, "", instrument_id, ActivitylogType.InstrumentEquipment, additionalInfo);
			 * }
			 * }
			 */

			//}
		}

		if (formCodeEntity.equals("Step")) {
			/******** Step *********/
			if (formCode.equals("Step")) {
				String currentStatusName = formDao.getFromInfoLookup("STEPSTATUS", LookupType.ID,
						elementValueMap.get("STATUS_ID"), "name");
				String lastStatusName = formDao.getFromInfoLookup("STEPSTATUS", LookupType.ID,
						elementValueMap.get("LASTSTATUS_ID"), "name");
				String expFormCode = formDao.getFromInfoLookup("STEP", LookupType.ID,
						formId, "EXPFORMCODE");

				//creating/updating the materialref data
				JSONObject reactionTableData = new JSONObject(generalDao
						.getSingleStringFromClobNoException("select file_content from fg_clob_files where file_id = '"
								+ elementValueMap.get("reactionTableData") + "'"));
				List<String> materialRefIdList = generalDao
						.getListOfStringBySql("select formid from fg_s_materialref_v where parentid = '" + formId
								//+ "' and sessionid is null and nvl(active,1)=1");
								+"' "+generalUtilFormState.getWherePartForTmpData("Materialref", formId));//adib 25062020 removed the row above and replaced with this one
				for (String materialRefId : materialRefIdList) {
					if (!reactionTableData.has(materialRefId)) {
						//deleting the materialref_id that does not exist any more in the current materialRef's
						formSaveDao.deleteStructTableByFormId(
								"delete from fg_s_materialref_pivot where formid = '" + materialRefId + "'",
								"fg_s_materialref_pivot", materialRefId);
						continue;
					}
					//update current materialref data
					JSONObject rowData = reactionTableData.getJSONObject(materialRefId);
					//organic experiment
					if(generalUtil.getJsonValById(rowData.toString(), "tableType").equals("Product")){
						if(generalUtil.getJsonValById(rowData.toString(), "SAMPLE_ID").isEmpty()){
							rowData.put("resultid_holder", "");
						}else{
							String assayRes = "";
							String sample_id = generalUtil.getJsonValById(rowData.toString(), "SAMPLE_ID");
							String material_id = generalUtil.getJsonValById(rowData.toString(), "INVITEMMATERIAL_ID");
							String concInReactionMass = generalUtil.getJsonValById(rowData.toString(), "concInReactionMass");
							if(!sample_id.isEmpty() && !material_id.isEmpty()){
							    String sql ="select t.RESULT_ID,t.RESULT_VALUE from FG_I_SELECTEDRESULTS_V t where t.SAMPLE_ID = "+sample_id+" and t.RESULT_NAME = 'Assay' and t.RESULT_MATERIAL_ID = '"+material_id+"'";
							    Map<String,String> assayResMap = generalDao.sqlToHashMap(sql);
							    if(generalUtil.getNull(expFormCode).equals("ExperimentCP") && !concInReactionMass.isEmpty() && concInReactionMass.equals(generalUtil.getNull(assayResMap.get("RESULT_VALUE")))){
							    	assayRes = assayResMap.get("RESULT_ID");
							    }
							    else if (!generalUtil.getNull(expFormCode).equals("ExperimentCP")){
							    assayRes = assayResMap.get("RESULT_ID");
							    rowData.put("concInReactionMass", generalUtil.getNull(assayResMap.get("RESULT_VALUE")));
							    }
							}
							rowData.put("resultid_holder", generalUtil.getNull(assayRes));
							
						}
					}
					if (generalUtil.getJsonValById(rowData.toString(), "INVITEMMATERIAL_ID").equals("0")
							|| generalUtil.getJsonValById(rowData.toString(), "INVITEMMATERIAL_ID").isEmpty()) {
						formSaveDao.deleteStructTableByFormId(
								"delete from fg_s_materialref_pivot where formid = '" + materialRefId + "'",
								"fg_s_materialref_pivot", materialRefId);
						continue;
					}
					List<String> columnList = new ArrayList<>(Arrays.asList("TIMESTAMP", "CHANGE_BY", "SESSIONID"));
					List<String> data = new ArrayList<>(Arrays.asList("sysdate", userId,"null"));
					Iterator<String> rowItr = rowData.keys();
					while (rowItr.hasNext()) {
						String columnName = rowItr.next();
						columnList.add(columnName);
						String columnVal = generalUtil.getJsonValById(rowData.toString(), columnName);
						data.add(columnVal == null ? columnVal : "'" + columnVal.replaceAll("\'", "\'\'") + "'");
					}
					String setSql = "";
					for (int i = 0; i < columnList.size(); i++) {
						setSql += setSql.isEmpty() ? columnList.get(i) + "=" + data.get(i)
								: "," + columnList.get(i) + "=" + data.get(i);
					}
					String sql = "update fg_s_materialref_pivot set " + setSql + " where formid = '" + materialRefId
							+ "'";
					formSaveDao.updateStructTableByFormId(sql, "fg_s_materialref_pivot", columnList, materialRefId);
				}
				//insert the new materialrefId's
				String[] currentFormIds = JSONObject.getNames(reactionTableData);
				if (currentFormIds != null) {
					TreeSet<String> insertFormIdList = new TreeSet<String>();
					insertFormIdList.addAll(Arrays.asList(currentFormIds));
					insertFormIdList.removeAll(materialRefIdList);
					for (String materialRedId : insertFormIdList) {
						JSONObject rowData = reactionTableData.getJSONObject(materialRedId);
						if (generalUtil.getJsonValById(rowData.toString(), "INVITEMMATERIAL_ID").equals("0")
								|| generalUtil.getJsonValById(rowData.toString(), "INVITEMMATERIAL_ID").isEmpty()) {
							continue;
						}
						List<String> columnList = new ArrayList<>(
								Arrays.asList("CREATION_DATE", "CREATED_BY", "TIMESTAMP", "CHANGE_BY", "SESSIONID",
										"ACTIVE", "FORMCODE", "FORMCODE_ENTITY", "FORMID", "PARENTID"));
						String materialref_id = formSaveDao.getStructFormId("MaterialRef");
						List<String> data = new ArrayList<>(Arrays.asList("sysdate", userId, "sysdate", userId, "null",
								"1", "'MATERIALREF'", "'MATERIALREF'", materialref_id, formId));

						Iterator<String> rowItr = rowData.keys();
						while (rowItr.hasNext()) {
							String columnName = rowItr.next();
							if(!columnList.contains(columnName.toUpperCase())){//fixed bug 8347
								columnList.add(columnName);
							    String columnVal = generalUtil.getJsonValById(rowData.toString(), columnName);
							    data.add(columnVal == null ? columnVal : "'" + columnVal.replaceAll("\'", "\'\'") + "'");
							}
						}
						String sql = "insert into fg_s_materialref_pivot (" + generalUtil.listToCsv(columnList)
								+ ")\r\t" + " values (" + generalUtil.listToCsv(data) + ")";
						formSaveDao.insertStructTableByFormId(sql, "fg_s_materialref_pivot", materialref_id);
					}
				}

				//recalculate the rational volume$quantity
				integrationCalc.doCalc("MaterialTripletCalc", "OnSaveStep", "", elementValueMap, null, null,
						formCodeEntity, formId, userId);
				
				
				
				//update limiting agent in the massbalance tab
				if (currentStatusName.equals("Active")) {
					
					if(elementValueMap.get("chkManualUpdate").equals("0"))
					{
						String protocolTypeId = formDao.getFromInfoLookup("Step", LookupType.ID, formId, "PROTOCOLTYPE_ID");
						String protocoltypeName = formDao.getFromInfoLookup("PROTOCOLTYPE", LookupType.ID, protocolTypeId, "name");
						String limitingMole = null;
						if(protocoltypeName.equals("Continuous Process")&& elementValueMap.get("preparation_run").equals("Run")){
							limitingMole = generalDao.selectSingleStringNoException(
									"select fg_get_num_normal(MOLERATE,MOLERATEUOM_ID) from fg_s_materialref_v t where t.PARENTID = '"
											+ formId + "' and t.LIMITINGAGENT = 1 and sessionid is null and active=1");
						} else {
							limitingMole = generalDao.selectSingleStringNoException(
									"select fg_get_num_normal(mole,MOLEUOM_ID) from fg_s_materialref_v t where t.PARENTID = '"
											+ formId + "' and t.LIMITINGAGENT = 1 and sessionid is null and active=1");
						}
	
						if (limitingMole != null && !limitingMole.isEmpty()) {
							limitingMole = new BigDecimal(Double.parseDouble(limitingMole)).toString();
							if (!limitingMole.equals(elementValueMap.get("limitingAgentMole"))) {
								formSaveDao.updateStructTableByFormId(
										"update fg_s_step_pivot set limitingAgentMole ='" + limitingMole
												+ "' where formId = '" + formId + "'",
										"fg_s_step_pivot", Arrays.asList("limitingAgentMole"), formId);
							}
						}
					}
				}

				postFormSaveStepStatusChange(formId, lastStatusName, currentStatusName, elementValueMap, formCode, userId, sbInfo);
				
				///////////////////////////////////////////////////
				if(generalUtil.getNull(expFormCode).equals("ExperimentCP")
						&&
					formDao.getFromInfoLookup("Step", LookupType.ID, formId, "preparation_run").equalsIgnoreCase("run"))
				{
					updateRunsPlanningTable(formDao.getFromInfoLookup("Step", LookupType.ID, formId, "EXPERIMENT_ID"), formId, userId,currentStatusName);
				}
				//////////////////////////////////////////////////
			}
			/******** StepFr *********/
			if (formCode.equals("StepFr")) {
				String currentStatusName = formDao.getFromInfoLookup("STEPSTATUS", LookupType.ID,
						elementValueMap.get("STATUS_ID"), "name");
				String lastStatusName = formDao.getFromInfoLookup("STEPSTATUS", LookupType.ID,
						elementValueMap.get("LASTSTATUS_ID"), "name");

				postFormSaveStepStatusChange(formId, lastStatusName, currentStatusName, elementValueMap, formCode,
						userId, sbInfo);
			}
			/*
			 * //insert to the activity log of the instrument the operation of removed instruments
			 * String experimentFormCode = generalDao.selectSingleString("select formcode from fg_s_experiment_v where formid = '"+elementValueMap.get("EXPERIMENT_ID")+"'");
			 * String experimentNumber = formDao.getFromInfoLookup("experiment", LookupType.ID, elementValueMap.get("EXPERIMENT_ID"), "name");
			 * String lab = formDao.getFromInfoLookup("experiment", LookupType.ID, elementValueMap.get("EXPERIMENT_ID"), "laboratory_id");
			 * List<String> removedInstruments = generalDao.getListOfStringBySql("Select INVITEMINSTRUMENT_ID from fg_i_instrument_activitylog_v t"
			 * + " where ACTION_FORMID = '"+elementValueMap.get("EXPERIMENT_ID")+"'"
			 * + " and 'Instrument Equipment' in "
			 * + "(select first_value(OPERATION_TYPE) over (PARTITION BY INVITEMINSTRUMENT_ID,ACTION_FORMID order by TO_DATE(\"DATE\",'"+generalUtil.getConversionDateTimeFormat()+"') desc)"
			 * + " from fg_i_instrument_activitylog_v al "
			 * + " where al.INVITEMINSTRUMENT_ID = t.INVITEMINSTRUMENT_ID"
			 * + " and '"+elementValueMap.get("EXPERIMENT_ID")+"' = al.ACTION_FORMID)"//gets all the records that the last operation on the instrument was equipment and not removed
			 * + " minus "
			 * + " Select INVITEMINSTRUMENT_ID from fg_s_instrumentref_all_v where PARENTID = '"+formId+"' and SESSIONID is NULL");
			 * for(String instrument_id:removedInstruments){
			 * Map<String,String> additionalInfo = new HashMap<>();
			 * additionalInfo.put("experiment_id", elementValueMap.get("EXPERIMENT_ID"));
			 * additionalInfo.put("formCode", experimentFormCode);
			 * additionalInfo.put("experimentNumber", experimentNumber);
			 * additionalInfo.put("laboratoryName", formDao.getFromInfoLookup("LABORATORY", LookupType.ID, lab, "NAME") );
			 * generalUtilLogger.logWrite(LevelType.Other, "", instrument_id, ActivitylogType.RemovedFomExperiment, additionalInfo);
			 * }
			 * //insert to activity log of instruments the operation of equipment in experiment
			 * List<String> addedInstruments = generalDao.getListOfStringBySql("Select INVITEMINSTRUMENT_ID from fg_s_instrumentref_all_v where PARENTID = '"+formId+"' and SESSIONID is NULL"
			 * + " MINUS "
			 * + " Select INVITEMINSTRUMENT_ID from fg_i_instrument_activitylog_v t"
			 * + " where ACTION_FORMID = '"+elementValueMap.get("EXPERIMENT_ID")+"'"
			 * + " and 'Instrument Equipment' in"
			 * + "(select first_value(OPERATION_TYPE) over (PARTITION BY INVITEMINSTRUMENT_ID,ACTION_FORMID order by TO_DATE(\"DATE\",'"+generalUtil.getConversionDateTimeFormat()+"') desc)"
			 * + " from fg_i_instrument_activitylog_v al "
			 * + " where al.INVITEMINSTRUMENT_ID = t.INVITEMINSTRUMENT_ID"
			 * + " and '"+elementValueMap.get("EXPERIMENT_ID")+"' = al.ACTION_FORMID)");//gets all the records that the last operation on the instrument was equipment and not removed
			 * for(String instrument_id:addedInstruments){
			 * Map<String,String> additionalInfo = new HashMap<>();
			 * additionalInfo.put("experiment_id", elementValueMap.get("EXPERIMENT_ID"));
			 * additionalInfo.put("formCode", experimentFormCode );
			 * additionalInfo.put("experimentNumber", experimentNumber);
			 * additionalInfo.put("laboratoryName", formDao.getFromInfoLookup("LABORATORY", LookupType.ID, lab, "NAME") );
			 * generalUtilLogger.logWrite(LevelType.Other, "", instrument_id, ActivitylogType.InstrumentEquipment, additionalInfo);
			 * }
			 */
		}

		/*********** MaterialRef *****************/
		if (formCode.equals("MaterialRef")) {//if calc update in db
			if (elementValueMap.get("limitingAgent").equals("1")
					&& elementValueMap.get("tableType").equals("Reactant")) {
				integrationCalc.doCalc("MaterialTripletCalc", "OnSaveLimitingAgent", "", elementValueMap, null, null,
						formCode, formId, userId);
			}

			if (!elementValueMap.get("tableType").equals("Product")) {
				integrationCalc.doCalc("MaterialTripletCalc", "OnSave", "", elementValueMap, null, null, formCode,
						formId, userId);
			}
		}
		/******** ExperimentMain *********/
		if (formCode.equals("ExperimentMain")) {
			String protocolTypeName = formDao.getFromInfoLookup("PROTOCOLTYPE", LookupType.ID,
					elementValueMap.get("PROTOCOLTYPE_ID"), "name");
			String experimentTypeName = formDao.getFromInfoLookup("EXPERIMENTTYPE", LookupType.ID,
					elementValueMap.get("EXPERIMENTTYPE_ID"), "name");
			String updateFormCode = "Experiment"; //default
			
			//change formcode according to the protocolTypeName 
			//and create the first step in Formulation ans Orgnic experiments
			if(protocolTypeName.equals("Organic")) {
				String isEnableSpread =  generalUtil.getNull(elementValueMap.get("isEnableSpreadsheet"),"No");
				if (elementValueMap.get("TEMPLATE_ID").isEmpty() && (isEnableSpread.equals("No") || hideStepFromSpreadsheet == 0)) {
					commonFunc.createNewStepOrganicData(formId);
				}
			} else if(protocolTypeName.equals("Analytical")) {
				updateFormCode = "ExperimentAn";
			} else if (protocolTypeName.equals("Formulation")) {
				updateFormCode = "ExperimentFor";
//				commonFunc.createNewStepIframeData(formId); // needs composition data so for this time it is not automatic
			} else if (protocolTypeName.equals("Parametric") && experimentTypeName.equals("Corrosion")) {
				updateFormCode = "ExperimentPrCR";
			} else if (protocolTypeName.equals("Parametric") && experimentTypeName.equals("Viscosity")) {
				updateFormCode = "ExperimentPrVS";
			} else if (protocolTypeName.equals("Parametric") && experimentTypeName.equals("TSU")) {
				updateFormCode = "ExperimentPrTS";
			} else if (protocolTypeName.equals("Parametric") && experimentTypeName.equals("Bottles")) {
				updateFormCode = "ExperimentPrBT";
			} else if (protocolTypeName.equals("Parametric")) {
				updateFormCode = "ExperimentPrGn";
			} else if (protocolTypeName.equals("Stability")) { // add ExperimentStb for "Taro develop"
				updateFormCode = "ExperimentStb"; 
//				String experimentFormNumberId_ = generalUtil.getNull(elementValueMap.get("formNumberId"));
				String estimatedStartDate_ = generalUtil.getNull(elementValueMap.get("estimatedStartDate"));
				String estimatedStartTime_ = "00:00";
				String tpIdList = elementValueInfATMap.get("stbPlanTPSelection");
				createSkylineRun(formId,estimatedStartDate_,estimatedStartTime_, userId, tpIdList);
				
				String expActiveStatus = formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.NAME, "Active",
						"id");
				if(!expActiveStatus.isEmpty()) {
					formSaveDao.updateStructTableByFormId("update fg_s_experiment_pivot t set t.status_id = '" + expActiveStatus + "', t.laststatus_id = '" + expActiveStatus + "' where formid ='" + formId + "'",
							"fg_s_experiment_pivot", Arrays.asList("STATUS_ID","LASTSTATUS_ID"), formId);
				}
			} else if (protocolTypeName.equals("Continuous Process")) { //add ExperimentCP for "Continuous Process"
				updateFormCode = "ExperimentCP";
				String isEnableSpread =  generalUtil.getNull(elementValueMap.get("isEnableSpreadsheet"),"No");
				if (isEnableSpread.equals("No") || hideStepFromSpreadsheet == 0) {
					commonFunc.createNewStepCPData(formId);
				}
			}

			/*formSaveDao.updateSingleString(
					"update FG_SEQUENCE SET FORMCODE = '" + updateFormCode + "' WHERE id = '" + formId + "'");*/
			formSaveDao.updateStructTableFormCode("Experiment", updateFormCode, formId, true);

			List<String> requestList = !generalUtil.getNull(elementValueMap.get("smartSelectList")).isEmpty()//the experiment was created from several requests
					? Arrays.asList(elementValueMap.get("smartSelectList").split(","))
					: (!elementValueMap.get("originRequestId").isEmpty()
							? Arrays.asList(elementValueMap.get("originRequestId")) : new ArrayList<String>());

			//insert to requestselect table 
			String sessionId_ = generalUtilFormState.getSessionId(formId);
			formDao.insertToSelectTable("REQUESTSELECT", formId, "REQUEST_ID", requestList, true, userId, null);
			//insert the requested samples to the samples table in the destination experiment
			List<String> sampleIdList = generalDao
					.getListOfStringBySql("select distinct SAMPLEID from FG_S_SAMPLEDATAREF_ALL_V where PARENTID in ("
							+ (requestList.isEmpty() ? "''" : generalUtil.listToCsv(requestList))
							+ ") and SESSIONID is null and active = 1");
			formDao.insertToSelectTable("SAMPLESELECT", formId, "SAMPLETABLE", sampleIdList, false, userId, null);
			/*
			 * String RequestSelectId = formSaveDao.getStructFormId("RequestSelect");
			 * formSaveDao.insertStructTableByFormId(
			 * "insert into FG_S_REQUESTSELECT_PIVOT (FORMID,TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMCODE,PARENTID,REQUEST_ID) "
			 * + "values ('" + RequestSelectId + "', sysdate, " + userId + ", null,1,'RequestSelect','"
			 * + formId + "','" + elementValueMap.get("originRequestId") + "')",
			 * "FG_S_REQUESTSELECT_PIVOT", RequestSelectId);
			 */
			for (String requestId : requestList) {
				String table = "FG_S_Request_PIVOT";
				String inProgressStatusId = formDao.getFromInfoLookup("REQUESTSTATUS", LookupType.NAME, "In Progress",
						"id");
				String ExternalRequestId = formDao.getFromInfoLookup("UNITS", LookupType.NAME, "External Tasks", "id");
				List<String> colList = Arrays.asList("requeststatus_id");
				generalUtilLogger.logWriter(LevelType.INFO,
						"Update the origin request- formid= " + requestId
								+ ". Set status to 'In Progress'(if the request destination unit is different from 'External Tasks')",
						ActivitylogType.SaveEvent, formId);
				String sql_ = "update " + table + " set requestStatus_id = '" + inProgressStatusId
						+ "' where DESTUNIT_ID!='" + ExternalRequestId + "' and formid = '" + requestId + "' ";
				formSaveDao.updateStructTableByFormId(sql_, table, colList, formId);

				String documentTableType = updateFormCode.equals("Experiment") ? "documents" : "expDocumentsSample";
				String originExperimentId = formDao.getFromInfoLookup("request", LookupType.ID, requestId,
						"originExperimentId");
				String experimentType = formDao.getFromInfoLookup("Experiment", LookupType.ID, originExperimentId,
						"experimenttypename");
				if (experimentType.equals("Impurity Identification")) {
					List<String> msResultStructureList = generalDao.getListOfStringBySql(
							"select structure from fg_s_manualresultsmsref_v" + " where parentId = '"
									+ originExperimentId + "' and sessionid is null and active =1");
					for (String msResultStructure : msResultStructureList) {
						String fileId = generalDao.selectSingleStringNoException(
								"select distinct NVL(t.full_img_file_id,'')" + " from fg_chem_doodle_data t"
										+ " where t.parent_id(+) = '" + msResultStructure + "'"
										+ " and nvl2(t.smiles_data,1,0) = 1" + " and rownum<=1");
						if (!fileId.isEmpty()) {
							String documentId = formSaveDao.getStructFormId("Document");
							formSaveDao.insertStructTableByFormId(
									"insert into fg_s_document_pivot t (t.FORMID,t.TIMESTAMP,t.CHANGE_BY,t.CREATION_DATE,t.CREATED_BY,t.SESSIONID,t.ACTIVE,t.FORMCODE,t.FORMCODE_ENTITY, PARENTID, t.TABLETYPE, t.LINK_ATTACHMENT, t.DOCUMENTUPLOAD)"
											+ " values ('" + documentId + "', sysdate, " + userId + ", sysdate, "
											+ userId + ", NULL, 1, 'Document', 'Document', '" + formId + "','"
											+ documentTableType + "','Attachment','" + fileId + "')",
									"fg_s_document_pivot", documentId);
						}
					}
				}
			}

			if (!requestList.isEmpty()) {
				if (protocolTypeName.equals("Analytical")) {
					updateManualResultsTable(formCode, formId, userId, experimentTypeName,elementValueMap);//addding manual results table to the experiment
				}
			}
			//add creator as default in crew -> yp 31122017 -> igor request 
			//			String usersCrewId = formSaveDao.getStructFormId("UsersCrew");
			formDao.insertToSelectTable("USERSCREW", formId, "USER_ID", Arrays.asList(userId), false, userId, null);//change to inserttoselectTable->adib 111018
			/*formSaveDao.insertStructTableByFormId(
					"insert into FG_S_USERSCREW_PIVOT (FORMID,TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMCODE,PARENTID,USER_ID,CREATED_BY,CREATION_DATE) "
							+ "values ('" + usersCrewId + "', sysdate, " + userId + ", null,1,'UsersCrew','" + formId
							+ "','" + userId + "','" + userId + "',SYSDATE)",
					"FG_S_EXPERIMENTSELECT_PIVOT", usersCrewId);
			*/
		}


		if (formCode.equals("ReportDesignExp")) {
			List<String> currentDesignStepList = new ArrayList<String>();
			for (Map.Entry<String, String> m : elementValueMap.entrySet()) {
				if (m.getKey().toLowerCase().contains("step") && m.getValue().equals("1")&&!m.getKey().equals("numConcentrationImpStep")) {
					currentDesignStepList.add(m.getKey());
				}
				if (m.getKey().equals("numConcentrationImpStep") &&! m.getValue().equals("")) {
					currentDesignStepList.add(m.getKey()+";"+m.getValue());
				}
			}
			
			generalUtilDesignData.clearImpuritiesDesignForStep(elementValueMap.get("STEP_ID"));
			generalUtilDesignData.addImpuritiesListToMap(
					Arrays.asList(elementValueMap.get("stepImpuritiesIdList").split("\\s*,\\s*")),
					elementValueMap.get("STEP_ID"));
			generalUtilDesignData.addStepListDesignToMap(currentDesignStepList, elementValueMap.get("STEP_ID"));
			String StepsDesign = generalUtil.MapOfListToJson(generalUtilDesignData.getStepDesignMapwithList());
			String ImpuritiesStepsDesign = generalUtil.MapOfListToJson(generalUtilDesignData.getStepImpuritesDesignMap());
			String id_ = uploadFileDao.saveStringAsClobRenderId("ReportDesign", StepsDesign);

			String sql_ = "update fg_s_reportdesignexp_pivot set STEPSDESIGN  = '" + id_ + "',impuritiesStepsDesign='"+ImpuritiesStepsDesign+"' where formId = '" + formId + "'";
			formSaveDao.updateStructTableByFormId(sql_, "FG_S_REPORTDESIGNEXP_PIVOT",
					Arrays.asList("stepsDesign,impuritiesStepsDesign"), formId);
			generalUtilDesignData.getStepDesignMapwithList().clear();
			generalUtilDesignData.getStepImpuritesDesignMap().clear();
		}


		if (formCode.equals("ExpCloneMain")) {
			String protocolTypeName = formDao.getFromInfoLookup("PROTOCOLTYPE", LookupType.ID,
					elementValueMap.get("PROTOCOLTYPE_ID"), "name");
			String experimentTypeName = formDao.getFromInfoLookup("EXPERIMENTTYPE", LookupType.ID,
					elementValueMap.get("EXPERIMENTTYPE_ID"), "name");
			String updateFormCode = "Experiment"; //default
			if (protocolTypeName.equals("Analytical")) {
				updateFormCode = "ExperimentAn";
			} else if (protocolTypeName.equals("Formulation")) {
				updateFormCode = "ExperimentFor";
			} else if (protocolTypeName.equals("Parametric") && experimentTypeName.equals("Corrosion")) {
				updateFormCode = "ExperimentPrCR";
			} else if (protocolTypeName.equals("Parametric") && experimentTypeName.equals("Viscosity")) {
				updateFormCode = "ExperimentPrVS";
			} else if (protocolTypeName.equals("Parametric") && experimentTypeName.equals("TSU")) {
				updateFormCode = "ExperimentPrTS";
			} else if (protocolTypeName.equals("Parametric") && experimentTypeName.equals("Bottles")) {
				updateFormCode = "ExperimentPrBT";
			} else if (protocolTypeName.equals("Parametric")) {
				updateFormCode = "ExperimentPrGn";
			} else if (protocolTypeName.equals("Stability")) { // add ExperimentStb for "Taro develop"
				updateFormCode = "ExperimentStb";
			} else if (protocolTypeName.equals("Continuous Process")) { //add ExperimentCP for "Continuous Process"
				updateFormCode = "ExperimentCP";
			}
			

			/*formSaveDao.updateSingleString(
					"update FG_SEQUENCE SET FORMCODE = '" + updateFormCode + "' WHERE id = '" + formId + "'");*/
			formSaveDao.updateStructTableFormCode("Experiment", updateFormCode, formId, true);

			/*cloneExperimentByMain( elementValueMap,  updateFormCode,  elementValueMap.get("originExp_id"),  userId,
					formId);*/
			/*String sql = String.format("update fg_s_experiment_pivot t set t.project_id = '%1$s', t.subproject_id = '%2$s', t.subsubproject_id = '%3$s', t.aim = '%4$s' ,t.description = '%5$s', t.estimatedstartdate = TO_CHAR(TO_DATE('%6$s', '"
								+ generalUtil.getConversionDateFormat() + "'), '"
								+ generalUtil.getConversionDateFormat() + "')  where t.formid = '%7$s'"
					,elementValueMap.get("PROJECT_ID"),elementValueMap.get("SUBPROJECT_ID"),elementValueMap.get("SUBSUBPROJECT_ID"),elementValueMap.get("aim"), elementValueMap.get("description")
					,elementValueMap.get("estimatedStartDate"),formId);
			formSaveDao.updateStructTableByFormId(sql, "fg_s_experiment_pivot",  Arrays.asList("PROJECT_ID","SUBPROJECT_ID","SUBSUBPROJECT_ID","AIM","DESCRIPTION","ESTIMATEDSTARTDATE")  , formId);
			*/
		}
		if (formCode.equals("Action")) {
			if (isNew.equals("1")) {
				formSaveElementDao.addMonitorParam(formId, "Action");//insertParamMonitoring
			}
		}

		/********** Component *********/
		if (formCode.equals("Component")) {
			if (elementValueMap.get("impurity").equals("1")) {
				integrationCalc.doCalc("ComponentCalc", "OnSave", "", elementValueMap, null, null, formCode, formId,
						userId);
			}
		}
		/******** RequestMain *********/
		/**
		 * Events:
		 * update FG_SEQUENCE (Inner BL)
		 * Select experiment origin ID if exists
		 */
		if (formCode.equals("Request")) {

			
			integrationValidation.validate(ValidationCode.DUPLICATE_OPERATIONTYPE, formCode, formId, "", sbInfo);
			integrationValidation.validate(ValidationCode.REQUESTTYPE_OPERATIONTYPE, formCode, formId,
					elementValueMap.get("REQUESTTYPE_ID"), sbInfo);
			integrationValidation.validate(ValidationCode.EMPTY_OPERATIONTYPE, formCode, formId, "", sbInfo);
			integrationValidation.validate(ValidationCode.EMPTY_PROCEDURE_OPT, formCode, formId,
					elementValueMap.get("REQUESTTYPE_ID"), sbInfo);
			if(isNew.equals("1")){
			String originExperimentId = elementValueMap.get("originExperimentId");
			if (!generalUtil.getNull(originExperimentId).equals("")) {
				String parentId = elementValueMap.get("parentId");
				String parentFormCodeEntity = formDao.getFormCodeEntityBySeqId("", parentId);
				String parentFormCode = formDao.getFormCodeBySeqId(parentId);

				List<String> listOfAncestors = new ArrayList<String>();
				if (parentFormCodeEntity.equals("Experiment")&& !parentFormCode.equals("ExperimentAn") && !parentFormCode.startsWith("ExperimentPr")) {
					listOfAncestors.add(originExperimentId);
				} else if (parentFormCodeEntity.equals("Step")) {
					listOfAncestors.add(parentId);
					listOfAncestors.add(originExperimentId);
				} else if (parentFormCodeEntity.endsWith("Action")) {
					listOfAncestors.add(parentId);
					listOfAncestors.add(formDao.getFromInfoLookup("Action", LookupType.ID, parentId, "STEP_ID"));
					listOfAncestors.add(originExperimentId);
				} else if (parentFormCodeEntity.equals("SelfTest")) {
					listOfAncestors.add(parentId);
					listOfAncestors.add(formDao.getFromInfoLookup("SelfTest", LookupType.ID, parentId, "ACTION_ID"));
					listOfAncestors.add(formDao.getFromInfoLookup("SelfTest", LookupType.ID, parentId, "STEP_ID"));
					listOfAncestors.add(originExperimentId);
				} else if (parentFormCodeEntity.equals("Workup")) {
					listOfAncestors.add(parentId);
					String action_id = formDao.getFromInfoLookup("Workup", LookupType.ID, parentId, "ACTION_ID");
					listOfAncestors.add(action_id);
					listOfAncestors.add(formDao.getFromInfoLookup("Action", LookupType.ID, action_id, "STEP_ID"));
					listOfAncestors.add(originExperimentId);
				}

				//Update the sample selection of the ancestors
				for (String ancestor : listOfAncestors) {
					formDao.insertToSelectTable("REQUESTSELECT", ancestor, "REQUEST_ID", Arrays.asList(formId), false,
							userId, null);
				}

				String experimentType = formDao.getFromInfoLookup("Experiment", LookupType.ID, originExperimentId,
						"experimenttypename");
				if (experimentType.equals("Impurity Identification")) {
					List<String> msResultStructureList = generalDao.getListOfStringBySql(
							"select structure from fg_s_manualresultsmsref_v" + " where parentId = '"
									+ originExperimentId + "' and sessionid is null and active =1");
					for (String msResultStructure : msResultStructureList) {
						String fileId = generalDao.selectSingleStringNoException(
								"select distinct NVL(t.full_img_file_id,'')" + " from fg_chem_doodle_data t"
										+ " where t.parent_id(+) = '" + msResultStructure + "'"
										+ " and nvl2(t.smiles_data,1,0) = 1" + " and rownum<=1");
						if (!fileId.isEmpty()) {
							String documentId = formSaveDao.getStructFormId("Document");
							formSaveDao.insertStructTableByFormId(
									"insert into fg_s_document_pivot t (t.FORMID,t.TIMESTAMP,t.CHANGE_BY,t.CREATION_DATE,t.CREATED_BY,t.SESSIONID,t.ACTIVE,t.FORMCODE,t.FORMCODE_ENTITY, PARENTID, t.TABLETYPE, t.LINK_ATTACHMENT, t.DOCUMENTUPLOAD)"
											+ " values ('" + documentId + "', sysdate, " + userId + ", sysdate, "
											+ userId + ", NULL, 1, 'Document', 'Document', '" + formId
											+ "','documents','Attachment','" + fileId + "')",
									"fg_s_document_pivot", documentId);
						}
					}
				}
			}else {
				String parentId = elementValueMap.get("parentId");
				String parentFormCodeEntity = formDao.getFormCodeEntityBySeqId("", parentId);
				List<String> listOfAncestors = new ArrayList<String>();
				
				if (parentFormCodeEntity.equals("Sample")) {
					String parent_sample_id = generalDao.selectSingleStringNoException("select t.parentid from fg_s_sample_v t where formid = '"+parentId+"'");
					String parentSampleFormCodeEntity = formDao.getFormCodeEntityBySeqId("", parent_sample_id);
					String parentSampleFormCode = formDao.getFormCodeBySeqId(parentId);
					if (parentSampleFormCodeEntity.equals("Experiment")&& !parentSampleFormCode.equals("ExperimentAn") && !parentSampleFormCode.startsWith("ExperimentPr")) {
						listOfAncestors.add(parent_sample_id);
					} else if (parentSampleFormCodeEntity.equals("Step")) {
						listOfAncestors.add(parent_sample_id);
						listOfAncestors.add(formDao.getFromInfoLookup("Step", LookupType.ID, parent_sample_id, "EXPERIMENT_ID"));
					} else if (parentSampleFormCodeEntity.endsWith("Action")) {
						listOfAncestors.add(parent_sample_id);
						listOfAncestors.add(formDao.getFromInfoLookup("Action", LookupType.ID, parent_sample_id, "STEP_ID"));
						listOfAncestors.add(formDao.getFromInfoLookup("Action", LookupType.ID, parent_sample_id, "EXPERIMENT_ID"));
					} else if (parentSampleFormCodeEntity.equals("SelfTest")) {
						listOfAncestors.add(parent_sample_id);
						listOfAncestors.add(formDao.getFromInfoLookup("SelfTest", LookupType.ID, parent_sample_id, "ACTION_ID"));
						String step_id = formDao.getFromInfoLookup("SelfTest", LookupType.ID, parent_sample_id, "STEP_ID");
						listOfAncestors.add(step_id);
						listOfAncestors.add(formDao.getFromInfoLookup("Step", LookupType.ID, step_id, "EXPERIMENT_ID"));
					} else if (parentSampleFormCodeEntity.equals("Workup")) {
						listOfAncestors.add(parent_sample_id);
						String action_id = formDao.getFromInfoLookup("Workup", LookupType.ID, parent_sample_id, "ACTION_ID");
						listOfAncestors.add(action_id);
						listOfAncestors.add(formDao.getFromInfoLookup("Action", LookupType.ID, action_id, "STEP_ID"));
						listOfAncestors.add(formDao.getFromInfoLookup("Action", LookupType.ID, action_id, "EXPERIMENT_ID"));
					}
					//Update the request selection of the ancestors
					String sessionId_ = "";
					for (String ancestor : listOfAncestors) {
						formDao.insertToSelectTable("REQUESTSELECT", ancestor, "REQUEST_ID", Arrays.asList(formId), false,
								userId, null);
					}
				}
			}
			}
/*
			//insert the samples connected to the parent entity to the request
			String parentId = elementValueMap.get("parentId");
			String parentFormCodeEntity = formDao.getFormCodeEntityBySeqId("", parentId);
			List<String> sampleList = new ArrayList<>();
			if (parentFormCodeEntity.equals("Experiment") || parentFormCodeEntity.equals("Step")) {
				sampleList = !generalUtil.getNull(elementValueMap.get("SMARTSELECTLIST")).isEmpty()
						? Arrays.asList(elementValueMap.get("SMARTSELECTLIST").split(",")) : sampleList;
			} else if (parentFormCodeEntity.equals("Action")) {
				sampleList = !generalUtil.getNull(elementValueMap.get("ACTION_SAMPLE_ID")).isEmpty()
						? Arrays.asList(elementValueMap.get("ACTION_SAMPLE_ID").split(",")) : sampleList;
			} else {
				sampleList = generalDao.getListOfStringBySql(
						"select distinct SAMPLE_ID from FG_S_SAMPLESELECT_ALL_V where PARENTID = '" + parentId
								+ "' and SESSIONID is null");
			}

			for (String sampleId : sampleList) {
				String samplerefId = formSaveDao.getStructFormId("SAMPLEDATAREF");
				String sql = "insert into FG_S_SAMPLEDATAREF_PIVOT t"
						+ " (t.FORMID,t.TIMESTAMP,t.CHANGE_BY,t.CREATION_DATE,t.CREATED_BY,t.SESSIONID,t.ACTIVE,t.FORMCODE,t.FORMCODE_ENTITY, SAMPLEID, PARENTID)"
						+ " values(" + samplerefId + ", sysdate, " + userId + ", sysdate, " + userId
						+ ", NULL, 1, 'SAMPLEDATAREF', 'SAMPLEDATAREF', " + sampleId + ", " + formId + ")";
				formSaveDao.insertStructTableByFormId(sql, "FG_S_SAMPLEDATAREF_PIVOT", samplerefId);
			}*/
			
			removeEmptyRowInEditTable(formId, "OperationType", "OperationTypeName", "OperationType_id", false);
		
			String currentStatusName = formDao.getFromInfoLookup("REQUESTSTATUS", LookupType.ID,
					elementValueMap.get("REQUESTSTATUS_ID"), "name");
			String operationTypeCount = generalUtil.getEmpty(
					generalDao.selectSingleString("select count(*) from fg_s_operationtype_all_v where parentid = '"
							+ formId + "' and sessionid is null and active = 1"),
					"0");
			if (currentStatusName.equals("Waiting") && elementValueMap.get("isSplit").equals("0")
					&& elementValueMap.get("parentRequestId").isEmpty()) {
				if (Integer.parseInt(operationTypeCount) > 1) {
					List<String> operationTypeIdList = generalDao.getListOfStringBySql(
							"select t.formId from FG_S_OPERATIONTYPE_ALL_V t where t.PARENTID='" + formId + "'");

					List<String> documentsIdList = generalDao.getListOfStringBySql(
							"select t.formId from FG_S_DOCUMENT_ALL_V  t where t.PARENTID='" + formId + "'");
					/*List<String> compoundIdList = generalDao.getListOfStringBySql(
							"select t.formId from Fg_s_Compound_All_v  t where t.PARENTID='" + formId + "'");*/
					List<String> MaterialsPeaksIdList = generalDao.getListOfStringBySql(
							"select t.formId from Fg_s_MaterialsPeaks_All_v t  where t.PARENTID='" + formId + "'");
					List<String> sampleIdList = generalDao.getListOfStringBySql(
							"select distinct t.formId from Fg_s_SampleDataRef_All_v t  where t.PARENTID='" + formId
									+ "' and sessionid is null and nvl(active,'1')='1'");

					String groupsCrewId = generalDao.selectSingleStringNoException(
							"select t.formId from Fg_s_Groupscrew_pivot t where t.PARENTID='" + formId + "'");
					String usersCrewId = generalDao.selectSingleStringNoException(
							"select t.formId from Fg_s_Userscrew_pivot t where t.PARENTID='" + formId + "'");
					String experimentSelectId = generalDao.selectSingleStringNoException(
							"select t.formId from Fg_s_experimentselect_pivot t where t.PARENTID='" + formId + "'");
					commonFunc.splitSaveRequestEvent(formId, operationTypeIdList, userId, documentsIdList,
							MaterialsPeaksIdList, sampleIdList, usersCrewId, groupsCrewId,
							experimentSelectId);

					//runs through all the requestselect in which the splitted request was added and replace them with their descendants
					List<String> requestdescendants = generalDao.getListOfStringBySql(
							"select formid from fg_s_request_pivot where PARENTREQUESTID = '" + formId + "'");
					List<String> requestUsedinSelectIds = generalDao.getListOfStringBySql(
							"select formid from fg_s_requestselect_pivot where instr(','||request_id||',',','||"
									+ formId + "||',')>0");
					for (String selectId : requestUsedinSelectIds) {
						String sql = "update FG_S_REQUESTSELECT_PIVOT  set REQUEST_ID = SUBSTR(REGEXP_REPLACE(','||request_id||',',','||"
								+ formId + "||','," + "',"
								+ generalUtil.replaceLast(requestdescendants.toString(), "]", "")
										.replaceFirst("\\[", "").replaceAll("\\s*", "")
								+ ",'),2," + "LENGTH(SUBSTR(REGEXP_REPLACE(','||request_id||',',','||" + formId
								+ "||','," + "',"
								+ generalUtil.replaceLast(requestdescendants.toString(), "]", "")
										.replaceFirst("\\[", "").replaceAll("\\s*", "")
								+ ",'),2))-1) where formid = '" + selectId + "'";
						formSaveDao.updateStructTableByFormId(sql, "FG_S_REQUESTSELECT_PIVOT",
								Arrays.asList("REQUEST_ID"), selectId);
					}
				}

				String table = "FG_S_Request_PIVOT";
				List<String> colList = new ArrayList<String>();
				colList.add("isSplit");

				String setAdditionColumns = "";
				if (Integer.parseInt(operationTypeCount) == 1) {//if it has a single operation then updates its number to get the suffix 'OP1'
					//Make the whole requestNumber
					String requestNumber = elementValueMap.get("formNumberId") + "-OP1";

					//updates the request columns
					colList.add("requestName");
					setAdditionColumns = ",requestName = '" + requestNumber + "'";
				} else if (Integer.parseInt(operationTypeCount) > 1) {
					colList.add("isInUse");
					setAdditionColumns = ",isInUse = '0'";
				}
				String sql_ = "update " + table + " set isSplit = '1'" + setAdditionColumns + " where FORMID = '"
						+ formId + "'";
				formSaveDao.updateStructTableByFormId(sql_, table, colList, formId);
			}
			
			if(generalUtil.getNull(elementValueMap.get("useAsDefaultData")).equals("1"))
			{
				formSaveDao.updateSingleStringInfoNoTryCatch(String.format("update FG_S_REQUEST_PIVOT t set t.useAsDefaultData=0 where t.useAsDefaultData=1 and t.project_id = '%1$s' and creator_id = '%3$s' and t.formid!='%2$s'", elementValueMap.get("PROJECT_ID"), formId,userId));	
				/*if(generalUtil.getNull(elementValueMap.get("default_hst")).isEmpty()){
					elementValueMap.put("default_hst", "'default'");
				}*/
			}
			//removeEmptyRowInEditTable(formId, "OperationType", "OperationTypeName", "OperationType_id", false);
		}
		/******** SampleSelectHolder *********/
		if (formCode.equals("SampleSelectHolder")){
//			List<String> selectedSamples = generalDao
//					.getListOfStringBySql("Select SAMPLETABLE from FG_S_SAMPLESELECTHOLDER_V where formId = " + formId);
//							//+ " and sessionid is null");
			List<String> selectedSample = new ArrayList<String>(Arrays.asList(elementValueMap.get("sampleTable").split(",")));
			List<String> savedSelectedSample = generalDao.getListOfStringBySql("select distinct  t.SAMPLEID from FG_S_SAMPLEDATAREF_v t where t.parentid='"+elementValueMap.get("parentId")+"' "+generalUtilFormState.getWherePartForTmpData("SampleDataRef", elementValueMap.get("parentId")));
			//List<String> savedSelectedSample = new ArrayList<String>(Arrays.asList(elementValueMap.get("sampleTable").split(",")));  
			String sessionId = generalUtilFormState.checkAndReturnSessionId("SampleDataRef", elementValueMap.get("parentId"));
			String colList = generalDao.getTableColCsv("FG_S_SAMPLEDATAREF_PIVOT");						
			String valList = colList
					.replace("CHANGE_BY", userId)
					.replace(",TIMESTAMP", ",sysdate").replace("CREATION_DATE", "sysdate")
					.replace("CREATED_BY", userId).replace("SESSIONID", sessionId).replace("ACTIVE", "1")
					.replace("PARENTID", elementValueMap.get("parentId")).replace("FORMCODE_ENTITY", "'SampleDataRef'")
					.replace("FORMCODE", "'SampleDataRef'").replace("SAMPLEDATAREFNAME", "NULL").replace("CLONEID","NULL")
					.replace("TEMPLATEFLAG", "NULL").replace("TABLETYPE", "NULL").replace("ACCEPTEDBYLAB", "0").replace("SENTTOLAB", "0")
					.replace("COMMENTS", "NULL").replace("TEXT", "NULL").replace("MATERIALSTAGE_ID", "NULL")
					.replace("EXPERIMENTNAMEINF", "NULL").replace("UOMSENTQUANTITY_ID", "NULL").replace("SENTQUANTITY", "NULL");				
			String sql = "";	
			if(selectedSample.size()==1 && selectedSample.get(0).toString()==""){
				sql = String.format("delete from FG_S_SAMPLESELECTHOLDER_PIVOT where formid = '%1$s' and parentid ='%2$s'",
						 formId, elementValueMap.get("parentId") );
				formSaveDao.deleteStructTableByFormId(sql, "FG_S_SAMPLESELECTHOLDER_PIVOT", formId);	
			}
			else{
				for (String sampleId : selectedSample) {
					//insert check sample 
					if(!savedSelectedSample.contains(sampleId)&& sampleId!=""){
						String sampleName = formDao.getFromInfoLookup("SAMPLE", LookupType.ID,sampleId, "name");
						String currentValList = valList.replace("FORMID", formSaveDao.getStructFormId("SampleDataRef")).replace("SAMPLEID", "'"+sampleId+"'").replace("SAMPLE","'"+ sampleName+"'");
						sql = String.format("insert into FG_S_SampleDataRef_PIVOT (%2$s) VALUES (%3$s)",
								formCode, colList, currentValList);
						formSaveDao.insertStructTableByFormId(sql, "FG_S_SampleDataRef_PIVOT", formId);	
					}
					savedSelectedSample.remove(sampleId);												
				}				
			}
			for (String sampleId : savedSelectedSample) {
				//delete unchecked sample
				if(!selectedSample.contains(sampleId)){					
					/*sql = String.format("delete from FG_S_SampleDataRef_PIVOT where SAMPLEID = '%1$s' and parentid ='%2$s'",
							 sampleId, elementValueMap.get("parentId") );
					formSaveDao.deleteStructTableByFormId(sql, "FG_S_SampleDataRef_PIVOT", "");	*/
					sql = String.format("select formid from FG_S_SampleDataRef_PIVOT where SAMPLEID = '%1$s' and parentid ='%2$s'",
							 sampleId, elementValueMap.get("parentId") );
					String sampleDatarefIdToRemove = generalDao.selectSingleStringNoException(sql);
					commonFunc.doRemove("SampleDataRef", sampleDatarefIdToRemove, userId)	;			
				}
			}
		}
		/******** SelfTest *********/
		if (formCode.equals("SelfTestMain")) {
			String SelfTestTypeName = formDao.getFromInfoLookup("SELFTESTTYPE", LookupType.ID,
					elementValueMap.get("TYPE_ID"), "name");

			String updateFormCode = "SelfTest"; //default

			if (SelfTestTypeName.equals("Appearance")) {
				updateFormCode = "STestAppearance";
			} else if (SelfTestTypeName.equals("Density")) {
				updateFormCode = "STestDensity";
			} else if (SelfTestTypeName.equals("pH")) {
				updateFormCode = "STestpH";
			} else if (SelfTestTypeName.equals("Foaming")) {
				updateFormCode = "STestFoaming";
			} else if (SelfTestTypeName.equals("Flash Point")) {
				updateFormCode = "STestFlashPoint";
			} else if (SelfTestTypeName.equals("Wet Sieve")) {
				updateFormCode = "STestWetSieve";
			} else if (SelfTestTypeName.equals("Pourability")) {
				updateFormCode = "STestPourability";
			} else if (SelfTestTypeName.equals("Suspensibility")) {
				updateFormCode = "STestSuspensibilit";
			} else if (SelfTestTypeName.equals("Particle Size Distribution by Light Difraction")) {
				updateFormCode = "STestParticleSize";
			} else if (SelfTestTypeName.equals("Viscosity")) {
				updateFormCode = "STestViscosity";
			} else if (SelfTestTypeName.equals("Cold Test")) {
				updateFormCode = "STestCold";
			} else if (SelfTestTypeName.equals("Emulsion Stability")) {
				updateFormCode = "STestEmulsionStab";
			} else if (SelfTestTypeName.equals("SprayAbility")) {
				updateFormCode = "STestSprayAbility";
			}

			/*
			 * formSaveDao.updateSingleString(
			 * "update FG_SEQUENCE SET FORMCODE = '" + updateFormCode + "' WHERE id = '" + formId + "'");
			 */
			formSaveDao.updateStructTableFormCode("SelfTest", updateFormCode, formId, true);
			
			//init first selftest status to active - part of 2453 (made also to stepfr - formulation)
			String stActiveStatusId = formDao.getFromInfoLookup("SelfTestStatus", LookupType.NAME, "Active", "ID");
			String sql_ = "update fg_s_SelfTest_pivot t set t.status_id ='" + stActiveStatusId + "' where formid = '" + formId + "'";
			formSaveDao.updateStructTableByFormId(sql_,
					"fg_s_selftest_pivot", Arrays.asList("STATUS_ID"), formId);
	 
			//Clone selftest TODO Yocheved - task 24536
//			String sourceFormId =  generalDao.selectSingleString(String.format("select t.formid from FG_S_SELFTEST_PIVOT t where t.useAsDefaultData=1 and t.experiment_id = '%1$s'", elementValueMap.get("EXPERIMENT_ID")));			
//			cloneExperiment.cloneSelfTestAcrossSteps(sourceFormId, formId, updateFormCode, userId);
		}
		/**************** ExperimentPr *****************/
		else if (formCode.startsWith("ExperimentPr")) {
			List<Result> resultList = new ArrayList<>();
			String sampleId = generalDao.selectSingleStringNoException(
					"select distinct first_value(SAMPLE_ID) over (partition by parentid order by SAMPLE_ID) from FG_S_SAMPLESELECT_ALL_V where parentid = "
							+ formId + " and sessionid is null and active = 1");
			if (formCode.equals("ExperimentPrCR")) {
				List<String> resultElements = generalUtilForm.getResultElementList("ExpParamsCrRef");
				List<String> resultRefIds = generalDao
						.getListOfStringBySql("select distinct FORMID from FG_S_ExpParamsCrRef_ALL_V where PARENTID = "
								+ formId + "and SESSIONID is null and representitiveResult = 1 and nvl(active,'1') = '1'");
				String productId = generalDao.selectSingleStringNoException(
						"select distinct productid from fg_s_sample_inf_v t where t.id ='" + sampleId + "'");
				//runs through the resultRef forms that are referenced to the current selftest
				for (String resultId : resultRefIds) {//.get(i).entrySet()){
					//runs through the result elements in the resultRef entry
					Map<String, String> row = generalDao.getMetaDataRowValues(
							"select distinct * from FG_S_ExpParamsCrRef_ALL_V where FORMID = " + resultId);
					for (String element : resultElements) {
						String elementName = element.split(",")[0].trim();
						String UOMelement = element.split(",")[1].trim();
						resultList.add(new Result(formId//exp_id
								, "Parametric"//test type
								,
								/*
								 * formDao.getFromInfoLookup("EXPERIMENTTYPE", LookupType.ID,
								 * elementValueMap.get("EXPERIMENTTYPE_ID"), "name")
								 *///result type
								generalUtil.getSpringMessagesByKey(elementName,
										formDao.getFromInfoLookup("EXPERIMENTTYPE", LookupType.ID,
												elementValueMap.get("EXPERIMENTTYPE_ID"), "name")),
								sampleId,
								row.get(elementName.toUpperCase()) != null
										? row.get(elementName.toUpperCase()).toString() : "",
								UOMelement.equals("NA") ? ""
										: row.get(UOMelement.toUpperCase()) != null
												? row.get(UOMelement.toUpperCase()).toString() : "",
								row.get(elementName.toUpperCase()) != null
										? row.get(elementName.toUpperCase()).toString().matches("-?\\d+(\\.\\d+)?")
												? "NUMERIC" : "STRING"
										: "",
								//formDao.getFromInfoLookup("Sample", LookupType.ID, sampleId, "PRODUCTID"), ta 190319 fixed bug 7339
								productId,
								elementValueMap.get("resultsComment") != null
										? elementValueMap.get("resultsComment").toString() : "",
								""//selftest_id  ta 180319 fixed bug 7338
								, "", resultId, "0", "", ""));
					}
				}
			} else if (formCode.equals("ExperimentPrGn")) {
				List<Map<String, Object>> manualResults = generalDao.getListOfMapsBySql(
						"Select formid, SAMPLE_ID,MATERIAL_ID,UOM_ID,COMMENTS as \"COMMENTS\",RESULT,PRGNRESULTTYPENAME,REQUEST_ID from fg_s_prmanualResultRef_all_v where result is not null and parentid = '"
								+ formId + "'"
								+ generalUtilFormState.getWherePartForTmpData("prmanualresultref", formId));

				for (Map<String, Object> resultInfo : manualResults) {
					String resultType = resultInfo.get("PRGNRESULTTYPENAME").toString();

					resultList.add(new Result(formId//exp_id
							, "Parametric"//test type
							, resultType//result type
							, resultInfo.get("SAMPLE_ID").toString(), resultInfo.get("RESULT").toString(),
							resultInfo.get("UOM_ID").toString(), "NUMERIC",
							resultInfo.get("MATERIAL_ID") != null ? resultInfo.get("MATERIAL_ID").toString() : "",
							resultInfo.get("COMMENTS") != null ? resultInfo.get("COMMENTS").toString() : "", ""//selftest_id
							, "", resultInfo.get("FORMID").toString(), "0", ""//resultInfo.get("MATERIALNAME").toString()
							, resultInfo.get("REQUEST_ID") != null ? resultInfo.get("REQUEST_ID").toString() : ""));
				}
			}
			String productId = generalDao.selectSingleStringNoException(
					"select distinct productid from fg_s_sample_inf_v t where t.id ='" + sampleId + "'");

			List<String> resultElements = generalUtilForm.getResultElementList(formCode);
			for (String element : resultElements) {
				String elementName = element.split(",")[0].trim();
				String resultEelement = elementAdditinalDataoMap.containsKey(elementName)
						? elementAdditinalDataoMap.get(elementName) : elementValueMap.get(elementName);
				String UOMelement = element.split(",")[1].trim();
				resultList.add(new Result(formId//exp_id
						, "Parametric"//test type
						,
						formDao.getFromInfoLookup("EXPERIMENTTYPE", LookupType.ID,
								elementValueMap.get("EXPERIMENTTYPE_ID"), "name")//result type
						, sampleId, resultEelement != null ? resultEelement.toString() : "",
						UOMelement.equals("NA") ? ""
								: elementAdditinalDataoMap.containsKey(UOMelement)
										? elementAdditinalDataoMap.get(UOMelement)
										: elementValueMap.get(UOMelement) != null
												? elementValueMap.get(UOMelement).toString() : "",
						resultEelement != null
								? resultEelement.toString().matches("-?\\d+(\\.\\d+)?") ? "NUMERIC" : "STRING" : "",
						//formDao.getFromInfoLookup("Sample", LookupType.ID, sampleId, "PRODUCTID"),ta 190319 fixed bug 7339
						productId,
						elementValueMap.get("comments") != null ? elementValueMap.get("comments").toString() : "", ""//selftest_id
						, "", "", "0", "", ""));
			}
			
			String experimentTypeName = formDao.getFromInfoLookup("ExperimentType", LookupType.ID,
					elementValueMap.get("EXPERIMENTTYPE_ID"), "name");
			String experimentStatus = formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.ID,
					elementValueMap.get("STATUS_ID"), "name");
			if(experimentStatus.equals("Approved")){//check if document tab has attachments. if so-then add an empty result
				//get from documents table where tableType is documents
				List<String> documents = generalDao
						.getListOfStringBySql("select distinct SAMPLE_ID from fg_s_document_v where parentid='" + formId
								+ "' and sessionid is null and active=1 and tabletype='expDocumentsSample'");
				List<String> sampleInChrom = new ArrayList<>();
				for (String document_sample : documents){
					sampleInChrom.add(document_sample);
					resultList.add(new Result(formId//exp_id
							, "Parametric"//test type
							, experimentTypeName//result type
							, generalUtil.getNull(document_sample), "", "", "STRING", "", "", ""
							, "", "", "0", ""//resultInfo.get("MATERIALNAME").toString()
							, ""));
				}
				/*
				//adib 27/02/2020 added the following code as a temporary result detailed in the task 24987
				List<String> sampleList = generalDao.getListOfStringBySql("select sample_id from fg_s_sampleselect_all_v where parentid =  '"+formId+"'"
						+ " and sessionid is null and active=1");
				documents = generalDao
						.getListOfStringBySql("select distinct formid from fg_s_document_v where parentid='" + formId
								+ "' and sessionid is null and active=1 and tabletype='expDocumentsSample' and sample_id is null");
				for(String chromatogram_id:documents){
					for(String sample_id:sampleList){
						if(!sampleInChrom.contains(sample_id)){//if sample did not get a chromatogram result then add a result related to the current sample to chromatogram that has no samples
							//all the samples have results related to chromatograms that are not connected to any sample(see implementation in the DB func fg_get_smart_link)
							resultList.add(new Result(formId//exp_id
									, "Parametric"//test type
									, experimentTypeName//result type
									, generalUtil.getNull(sample_id), "", "", "STRING", "", "", ""//selftest_id
									, "", "", "0", ""//resultInfo.get("MATERIALNAME").toString()
									, ""));
								
							
						}
					}
				}//end of temp result

		*/	}

			commonFunc.insertToResults(resultList, formId, elementValueMap,false);
		}
		/******** ExperimentPrBT *********/
		if (formCode.equals("ExperimentPrBT")) {
			Map<String, String> selectedBatchSample = generalDao.sqlToHashMap(
					"select distinct first_value(s.BATCHNAME) over(partition by t.EXPERIMENT_ID) as BATCHNAME,"
							+ "first_value(s.PRODUCTNAME) over(partition by t.EXPERIMENT_ID) as MATERIALNAME"
							+ " from FG_S_EXPERIMENTPRBT_ALL_V t,fg_s_sample_all_v s, fg_s_sampleselect_all_v slct"
							+ " where  slct.PARENTID(+) = " + formId + " and slct.SAMPLE_ID = s.SAMPLE_ID(+)"
							+ " and s.BATCH_ID(+) is not null " + "and t.EXPERIMENT_ID=slct.PARENTID(+)"
							+ " and slct.SESSIONID(+) is null" + " and slct.active =1");
			List<String> colList = Arrays.asList("bmaterialid", "material_id");
			String sql_ = "update fg_formadditionaldata t" + " set value ='" + selectedBatchSample.get("BATCHNAME")
					+ "' where t.parentid ='" + formId + "' and t.entityimpcode = 'batchNumber'";
			formSaveDao.updateAdditinalData(sql_, colList, formId);
			String sql = "update fg_formadditionaldata t" + " set value ='" + selectedBatchSample.get("MATERIALNAME")
					+ "' where t.parentid ='" + formId + "' and t.entityimpcode = 'batchMaterial'";
			formSaveDao.updateAdditinalData(sql, colList, formId);

		}
		/******** SelfTest *********/
		if (formCodeEntity.equals("SelfTest")) {
			
			String status_ = formDao.getFromInfoLookup("selfTestStatus", LookupType.ID, elementValueMap.get("STATUS_ID"),
					"name");  //Cancelled
			
			if (formCode.equals("SelfTest") && !generalUtil.getNull(status_).equals("Cancelled")) {
				integrationValidation.validate(ValidationCode.INVALID_REMOVED_SELFTEST_INSTRUMENT, formCode, formId, "",
						sbInfo);
			}
			//gets the results elements and their UOM
			String ref = formCode.equals("SelfTest") ? "ResultRef"
					: formCode.equals("STestCold") ? "ColdTestResults"
							: formCode.equals("STestpH") ? "pHResults"
									: formCode.equals("STestEmulsionStab") ? "StabilityResults" : "";
			List<Result> resultList = new ArrayList<Result>();
			if (!ref.isEmpty()) {
				List<String> resultElements = null;
				String selfTestTypeName = formDao
						.getFromInfoLookup("selfTestType", LookupType.ID, elementValueMap.get("TYPE_ID"),
								"name");
				if(selfTestTypeName.equals("Internal Analytical")){
					resultElements = Arrays.asList("normalization,NORMALUOM_ID");
				}else if(selfTestTypeName.equals("Non-Numeric")) { // use normalization,NORMALUOM_ID fort non numeric result
					resultElements = Arrays.asList("normalization,NORMALUOM_ID","NONNUMERICRESULT,NA");
				} else {
					resultElements = generalUtilForm.getResultElementList(ref);
				}
				
				List<String> resultRefIds = generalDao.getListOfStringBySql("select distinct FORMID from FG_S_" + ref
						+ "_ALL_V where PARENTID = " + formId + " and SESSIONID is null and active = 1");
				
				
				//runs through the resultRef forms that are referenced to the current selftest
				for (String resultId : resultRefIds) {//.get(i).entrySet()){
					//runs through the result elements in the resultRef entry
					Map<String, String> row = generalDao.getMetaDataRowValues(
							"select distinct * from FG_S_" + ref + "_ALL_V where FORMID = " + resultId);
					for (String element : resultElements) {
						String elementName = element.split(",")[0].trim();
						String UOMelement = element.split(",")[1].trim();
						if (row.get(elementName.toUpperCase()).isEmpty() && !selfTestTypeName.equals("Non-Numeric") && row.get(elementName.toUpperCase()) != null) {
							continue;
						}
						//if the selftest is non numeric-> then takes the material that is connected to the sample - NOTE! cloneSelfTestRefData function has use similar code - check if changes is need also there in case of a change
						String material_id = "";
						if(selfTestTypeName.equals("Non-Numeric")||selfTestTypeName.equals("Internal Analytical")){
							//organic selftests material result can be from the inventory or optional one. If none of them ,then it's taken from the sample product.
							material_id = row.get("MATERIAL_ID") != null ? row.get("MATERIAL_ID").toString() : "";
							if(material_id.isEmpty() && (!row.containsKey("OPTIONALMATERIALNAME")||row.get("OPTIONALMATERIALNAME").isEmpty())){
								material_id = formDao.getFromInfoLookup("Sample", LookupType.ID,
												elementValueMap.get("sampleId"), "PRODUCTID");
							}
						}
						String materialId = selfTestTypeName.equals("Non-Numeric")
										? material_id
										: (row.get("MATERIAL_ID") != null ? row.get("MATERIAL_ID").toString() : "");
						resultList.add(new Result(elementValueMap.get("EXPERIMENT_ID"),
								formDao.getFromInfoLookup("EXPERIMENT", LookupType.ID,
										elementValueMap.get("EXPERIMENT_ID"), "PROTOCOLTYPENAME")//test type
								,
								formCode.equals("SelfTest")
										? formDao.getFromInfoLookup("RESULTTYPE", LookupType.ID,
												row.get("RESULTTYPE_ID"), "name")
										: generalUtil.getSpringMessagesByKey(elementName, elementName).substring(0, 1)
												.toUpperCase()
												+ generalUtil.getSpringMessagesByKey(elementName, elementName)
														.substring(1)
												+ (ref.contains("pH") ? " " + row.get("SOLUTION")
														: ref.contains("ColdTest") || ref.contains("Stability")
																? " after " + row.get("PERIOD") + " "
																		+ formDao.getFromInfoLookup("UOM",
																				LookupType.ID,
																				getOrDefaultImp(row.get("PERIODUOM_ID"),
																						""),
																				"name")
																: "")//result type //kd 05032018 use getOrDefaultImp() for Java 7 instead row.getOrDefault for Java 8
								, elementValueMap.get("sampleId"),
								row.get(elementName.toUpperCase()) != null
										? row.get(elementName.toUpperCase()).toString() : "",
								UOMelement.equals("NA") ? ""
										: row.get(UOMelement.toUpperCase()) != null
												? row.get(UOMelement.toUpperCase()).toString()
												: "",
								/*selfTestTypeName
										.equals("Non-Numeric") 
												? "NUMERIC":*/ // numeric for Non-Numeric normalization
								 (row.get(elementName.toUpperCase()) != null
										? row.get(elementName.toUpperCase()).toString()
												.matches("-?\\d+(\\.\\d+)?") ? "NUMERIC" : "STRING"
										: ""),
								materialId, row.get("COMMENTS") != null ? row.get("COMMENTS").toString() : "", formId//selftest_id
								, "", resultId, "0",
								row.containsKey("OPTIONALMATERIALNAME") ? row.get("OPTIONALMATERIALNAME") : "", ""));
					}
				}
			}
			List<String> resultElements = generalUtilForm.getResultElementList(formCode);
			for (String element : resultElements) {
				String elementName = element.split(",")[0].trim();
				String UOMelement = element.split(",")[1].trim();
				if (formCode.contains("Density")
						&& !formDao.getFromInfoLookup("DENSITYTYPE", LookupType.ID,
								elementValueMap.get("DENSITYTYPE_ID"), "name").equals("Density")
						&& elementName.equals("result")) {// in the density selftest- all types except Density - the calculated result is not rellevant. it gets a copy from result.
					continue;
				}
				resultList.add(new Result(elementValueMap.get("EXPERIMENT_ID"),
						formDao.getFromInfoLookup("EXPERIMENT", LookupType.ID, elementValueMap.get("EXPERIMENT_ID"),
								"PROTOCOLTYPENAME")//test type
						, generalUtil.getSpringMessagesByKey(elementName, elementName).substring(0, 1).toUpperCase()
								+ generalUtil.getSpringMessagesByKey(elementName, elementName).substring(1)
						//+(formCode.contains("Density")?
						//	 " ("+formDao.getFromInfoLookup("densityType", LookupType.ID, elementValueMap.get("DENSITYTYPE_ID"), "name")+")":"")//result type
						, elementValueMap.get("sampleId"),
						elementValueMap.get(elementName) != null ? elementValueMap.get(elementName).toString() : "",
						UOMelement.equals("NA") ? ""
								: elementValueMap.get(UOMelement) != null ? elementValueMap.get(UOMelement).toString()
										: "",
						elementValueMap.get(elementName) != null
								? elementValueMap.get(elementName).toString().matches("-?\\d+(\\.\\d+)?") ? "NUMERIC"
										: "STRING"
								: "",
						elementValueMap.get("MATERIAL_ID") != null ? elementValueMap.get("MATERIAL_ID").toString() : "",
						elementValueMap.get("testcomment") != null ? elementValueMap.get("testcomment").toString()
								: elementValueMap.get("summary") != null ? elementValueMap.get("summary") : "",
						formId//selftest_id
						, "", "", "0", "", ""));
			}

			commonFunc.insertToResults(resultList, formId, elementValueMap,true);

			String sql = "";
			//************* yp 14012020 task - 24937 - cancel the event of copy instrument and  columns from the selected method
			//update instrument and column tables according to the instrumnets&columns that are under the source experiment of the template
//			sql = "Select SOURCEEXPNO_ID from fg_s_AnalytMethodselect_all_v where PARENTID ='" + formId
//					+ "' and sessionid is null and active=1";
//			String sourceExpOfTemplate = "";
//			String lastSourceExpOfTemplate = generalUtil.getNull(elementValueMap.get("LASTMETHOD_ID"));
//			try {
//				sourceExpOfTemplate = generalDao.selectSingleStringNoException(sql);
//
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//
//			if (!generalUtil.getNull(sourceExpOfTemplate).equals("")
//					&& !sourceExpOfTemplate.equals(lastSourceExpOfTemplate)) { // if exists and change
//				// update LASTMETHOD_ID with sourceExpOfTemplate
//				List<String> colList = Arrays.asList("LASTMETHOD_ID");
//				String sql_ = "update FG_S_SELFTEST_PIVOT set LASTMETHOD_ID = '" + sourceExpOfTemplate
//						+ "' where FORMID = '" + formId + "'";
//				formSaveDao.updateStructTableByFormId(sql_, "FG_S_SELFTEST_PIVOT", colList, formId);
//
//				//copy the instruments from the source experiment of the template
//				sql = "Select INVITEMINSTRUMENT_ID from fg_s_instrumentRef_all_v where PARENTID = '"
//						+ sourceExpOfTemplate + "'";//no need to check sessionid because the data of the source exp at this point should be not temporary
//				List<String> instrumentList = generalDao.getListOfStringBySql(sql);
//				sql = "Delete from fg_s_instrumentselect_pivot where parentid= '" + formId + "'";
//				formSaveDao.deleteStructTable(sql, "fg_s_instrumentselect_pivot", "parentid", formId);//first, delete all the current instruments
//				String sessionId_ = generalUtilFormState.getSessionId(formId);
//				formDao.insertToSelectTable("instrumentSelect", formId, "INSTRUMENT_ID", instrumentList, true, userId,sessionId_);//second, insert all the instruments from the source exp to the current instrument selection
//
//				//copy the columns from the source experiment of the template
//				sql = "Delete from fg_s_columnselect_pivot where parentid= '" + formId + "'";
//				formSaveDao.deleteStructTable(sql, "fg_s_columnselect_pivot", "parentid", formId);//first, delete all the current instruments
//				cloneExperiment.insertRef("ColumnSelect", sourceExpOfTemplate, userId, "EXPERIMENT", formId, null, null,
//						"");
//			}

			String sessionId_ = null;
			//if added a sample from sample search(not from the table) then add it to the sample table
			if (elementValueMap.get("sampleId") != null
					&& !generalUtil.getNull(elementValueMap.get("sampleId")).isEmpty()) {
				if (generalUtil.getNull(elementValueMap.get("lastSampleId")).isEmpty()) {//it is the first save
					formDao.insertToSelectTable("SampleSelect", formId, "SAMPLETABLE",
							Arrays.asList(elementValueMap.get("sampleId")), true, userId, null);
				} else if (!elementValueMap.get("lastSampleId").equals(elementValueMap.get("sampleId"))) {
					sql = "update fg_s_sampleselect_pivot set SAMPLETABLE = REGEXP_REPLACE(REPLACE(','||SAMPLETABLE||',',','||"
							+ elementValueMap.get("lastSampleId") + "||',',','||" + elementValueMap.get("sampleId")
							+ "||','),'^[,]|[,]$','')" + "where parentid = '" + formId + "' "+(generalUtil.getNull(sessionId_).isEmpty()?"and sessionid is null":"and sessionid = '"+sessionId_+"'");
					formSaveDao.updateStructTable(sql, "fg_s_sampleselect_pivot", Arrays.asList("SAMPLETABLE"),
							"parentid", formId);
					formSaveDao.updateStructTableByFormId(
							"update fg_s_selftest_pivot set lastSampleId = null where formid = '" + formId + "'",
							"fg_s_selftest_pivot", Arrays.asList("lastSampleId"), formId);
				}
			}
			if (generalUtil.getNull(elementValueMap.get("sampleId")).isEmpty()
					&& !generalUtil.getNull(elementValueMap.get("lastSampleId")).isEmpty()) {//if sampleId was cleaned then should clean the lastsampleid value too
				sql = "update fg_s_sampleselect_pivot set SAMPLETABLE = REGEXP_REPLACE(REPLACE(','||SAMPLETABLE||',',','||"
						+ elementValueMap.get("lastSampleId") + "||',',','),'^[,]|[,]$','')" + "where parentid = '"
						+ formId + "' "+(generalUtil.getNull(sessionId_).isEmpty()?"and sessionid is null":"and sessionid = '"+sessionId_+"'");
				formSaveDao.updateStructTable(sql, "fg_s_sampleselect_pivot", Arrays.asList("SAMPLETABLE"), "parentid",
						formId);
				formSaveDao.updateStructTableByFormId(
						"update fg_s_selftest_pivot set lastSampleId = null where formid = '" + formId + "'",
						"fg_s_selftest_pivot", Arrays.asList("lastSampleId"), formId);

			}
			//Added Instruments in the Self Test will be added also to Step Equipment
			String step = generalDao.selectSingleStringNoException(
					"select s.step_id from fg_s_step_v s ,fg_s_selftest_all_v st where st.STEP_ID = s.step_id and st.SELFTEST_ID = "
							+ formId);
			sql = "Select distinct t.INVITEMINSTRUMENT_ID from fg_s_InstrumentSelect_all_v t, fg_s_selftest_all_v st where t.PARENTID = "
					+ formId + " and st.STEP_ID = " + step + " and t.SESSIONID is null and t.active = 1 " + " minus "
					+ "Select to_number(INVITEMINSTRUMENT_ID) from FG_S_INSTRUMENTREF_PIVOT t where t.PARENTID = "
					+ step + " and t.active = 1";
			List<String> instruments = generalDao.getListOfStringBySql(sql);
			if (!instruments.isEmpty()) {
				Map<String, String> instrumentMap = new HashMap<>();
				for (String instrument : instruments) {
					String instrumentRefId = formSaveDao.getStructFormId("INSTRUMENTREF");
					instrumentMap = generalDao.sqlToHashMap(
							"select inviteminstrumentname, inviteminstrument_id, serialnumber, manufacturer, calibrationdate, model  from fg_s_inviteminstrument_v where inviteminstrument_id = "
									+ instrument);
					sql = "insert into FG_S_INSTRUMENTREF_PIVOT"
							+ " (FORMID, FORMCODE,FORMCODE_ENTITY,TIMESTAMP,CREATION_DATE,CREATED_BY,CHANGE_BY,MANUFACTURER,MODEL ,LASTCALIBRATIONDATE,INVITEMINSTRUMENT_ID,INSTRUMENTNAME,ACTIVE,SERIALNUMBER,PARENTID)"
							+ " VALUES(" + instrumentRefId + ",'INSTRUMENTREF','INSTRUMENTREF',sysdate,sysdate,"
							+ userId + "," + userId + " ,'" + instrumentMap.get("MANUFACTURER") + "', '"
							+ instrumentMap.get("MODEL") + "', '" + instrumentMap.get("CALIBRATIONDATE") + "','"
							+ instrumentMap.get("INVITEMINSTRUMENT_ID") + "','"
							+ instrumentMap.get("INVITEMINSTRUMENTNAME") + "' ,1 ,'" + instrumentMap.get("SERIALNUMBER")
							+ "'," + step + ")";
					formSaveDao.insertStructTableByFormId(sql, "FG_S_INSTRUMENTREF_PIVOT", instrumentRefId);
				}
			}
			
			//useAsDefaultData from self test - if checked clean all other useAsDefaultData in other selftest under the experiment and maintain selftestDefaultDataHolder with the checked (for better performance in action table under step - we need to have this default self test data available in the experiment level)
			String experimentId_ = generalUtil.getNull(elementValueMap.get("EXPERIMENT_ID"));
			if(!experimentId_.equals("")) {
				String experimentSelftestDefaultDataHolder = generalDao.selectSingleString("select NVL(selftestDefaultDataHolder,'-999') from fg_s_experiment_pivot where formid = '" + experimentId_ + "'");
				if(!experimentSelftestDefaultDataHolder.equals(formId) && generalUtil.getNull(elementValueInfATMap.get("useAsDefaultData")).equals("1"))
				{	
					formSaveDao.updateSingleStringInfoNoTryCatch(String.format("update FG_S_SELFTEST_PIVOT t set t.useAsDefaultData=0 where t.useAsDefaultData=1 and t.experiment_id = '%1$s' and t.formid!='%2$s'", experimentId_, formId)); 			
					formSaveDao.updateSingleStringInfoNoTryCatch("update fg_s_experiment_pivot t set t.selftestDefaultDataHolder = '" + formId + "' where formid = '" + experimentId_ + "'");
				} else if(experimentSelftestDefaultDataHolder.equals(formId) && !generalUtil.getNull(elementValueInfATMap.get("useAsDefaultData")).equals("1")) { //the user remove the check default data in this save (not in
					formSaveDao.updateSingleStringInfoNoTryCatch("update fg_s_experiment_pivot t set t.selftestDefaultDataHolder = null where formid = '" + experimentId_ + "'");
				}
			}
			
			//implement select from edit data table - delete empty rows
			removeEmptyRowInEditTable(formId, "InstrumentSelect", "instrument_id", "instrumentselect_id", true);
			removeEmptyRowInEditTable(formId, "ColumnSelect", "column_id", "columnselect_id", true);
			removeEmptyRowInEditTable(formId, "SampleSelect", "SampleTable", "Sampleselect_id", true);
			removeEmptyRowInEditTable(formId, "AnalytMethodSelect", "AnalytMathodTable", "AnalytMethodSelect_id", false);
 
			//insert Summary SelfTet into observation's Action
			if (generalUtil.getNull(elementValueInfATMap.get("copySummaryFieldToObservFlag")).equals("1")) {			
				insertSummaryIntoObservation(elementValueMap.get("ACTION_ID"),elementValueMap.get("summary"));
			}
			
			//update action selftestidholder
			sql  = "update fg_s_action_pivot t set t.selftestidholder = '" + formId + "' where t.formid = '" + elementValueMap.get("ACTION_ID") + "'";
			formSaveDao.updateSingleStringInfo(sql);
		}
		/******** Workup form entity (start with workup) *********/
		/**
		 * Events:
		 * Update stage status if user click to sava & next
		 * 
		 */

		if (formCode.startsWith("Wu")) {
			generalUtilConfig.SaveEventHandler(formCode, formId, elementValueMap, userId, "PostSave");
		}
		if (formCode.startsWith("Workup")) {
			generalUtilConfig.SaveEventHandler(formCode, formId, elementValueMap, userId, "PostSave");
			String stageName = formDao.getFromInfoLookup("STAGESTATUS", LookupType.ID, elementValueMap.get("STAGE_ID"),
					"name");

			String sampleNmber = elementValueMap.get("sampleId");

			String isProcessStart = elementValueMap.get("isProcessStart");
			String nextStageStatus = "";
			String table = "FG_S_Workup_PIVOT";

			if (generalUtil.getNull(saveAction).equals("UPDATE_STAGE_STATUS")) {
				//set materials by sample number
				if (stageName.equals("General Data Input") && !generalUtil.getNull(sampleNmber).isEmpty()
						&& (formCode.equals("WorkupFeeding") || formCode.equals("WorkupDistillation")
								|| formCode.equals("WorkupCrystallize"))) {
					String tableType = "";
					String tableName = "";
					String resultColumn = "";

					if (formCode.equals("WorkupFeeding")) {
						tableName = "wufeedmaterialref";
						resultColumn = "ACTUALPURITY";

					} else if (formCode.equals("WorkupDistillation")) {
						tableName = "wudiststartmixref";
						resultColumn = "CONCENTRATION";
					}

					else if (formCode.equals("WorkupCrystallize")) {
						tableName = "WuCryMixDefineRef";
						tableType = "StartingMixtureDefinition";
						resultColumn = "CONCENTRATION";
					}

					List<Map<String, Object>> results = generalDao.getListOfMapsBySql(
							"select  t.RESULT_NAME, t.RESULT_MATERIAL_ID, t.RESULT_VALUE, t.RESULT_UOM_ID, t.RESULT_ID from FG_I_SELECTEDRESULTS_V t where t.RESULT_TEST_NAME = 'Analytical' and t.SAMPLE_ID ="
									+ sampleNmber);

					if (results != null) {
						commonFunc.setMaterialsInWorkup(results, tableType, tableName, resultColumn, formId, userId);
					}

				}

				String lastStage = formDao.getFromInfoLookup("Workup", LookupType.ID, formId, "STAGE_ID");
				nextStageStatus = elementValueMap.get("nextStageStatus");

				//String isProcessStart = elementValueMap.get("isProcessStart");
				isProcessStart = (isProcessStart.equals("0")) ? "1" : "2";
				/*List<String> colList = Arrays.asList("STAGE_ID", "nextStageStatus","prevStageStatus", "ISPROCESSSTART");
				String sql_ = "update " + table + " set STAGE_ID = '" + nextStageStatus
						+ "', nextStageStatus = NULL,prevStageStatus = NULL, ISPROCESSSTART = " + isProcessStart + " where FORMID = '" + formId
						+ "'";
				formSaveDao.updateStructTableByFormId(sql_, table, colList, formId);*/

				//change status to completed
				if (!generalUtil.getNull(lastStage).isEmpty() && nextStageStatus.equals(lastStage)) {
					String completedStatusId = formDao.getFromInfoLookup("WORKUPSTATUS", LookupType.NAME, "Completed",
							"id");
					List<String> colList = Arrays.asList("STATUS_ID","CHANGE_BY","TIMESTAMP");
					String sql_ = "update " + table + " set STATUS_ID = '" + completedStatusId + "',CHANGE_BY='"+userId+"',TIMESTAMP=sysdate where FORMID = '"
							+ formId + "'";
					formSaveDao.updateStructTableByFormId(sql_, table, colList, formId);
				}
			}

			List<String> colList = Arrays.asList("STAGE_ID", "nextStageStatus", "prevStageStatus", "ISPROCESSSTART","CHANGE_BY","TIMESTAMP");
			String sql_ = "update " + table + " set STAGE_ID = "
					+ (nextStageStatus.isEmpty() ? "STAGE_ID" : "'" + nextStageStatus + "'")
					+ ", nextStageStatus = NULL,prevStageStatus = NULL, ISPROCESSSTART = " + isProcessStart +", CHANGE_BY='"+userId+"', TIMESTAMP=sysdate"
					+ " where FORMID = '" + formId + "'";
			formSaveDao.updateStructTableByFormId(sql_, table, colList, formId);

			/*
			 * if( stageName.equals("Materials Definition") && formCode.equals("WorkupCrystallize"))
			 * {
			 * //TODO: change batch number to batch id
			 * String batchId = elementValueMap.get("batchId");
			 * formSaveDao.updateSingleStringInfo("delete from FG_S_WuCryMixDefineRef_PIVOT t where CREATEDBYUSER = 1  and parentid= "+formId +"and TABLETYPE = 'MotherLiquor'");
			 * if(!generalUtil.getNull(batchId).isEmpty())
			 * {
			 * String sampleId = generalDao.selectSingleString("select b.SAMPLE_ID from fg_s_invitembatch_all_v b where b.INVITEMBATCH_ID ='"+ batchId+"'");
			 * List<Map<String, Object>> results =generalDao.getListOfMapsBySql("select  t.RESULT_NAME, t.RESULT_MATERIAL_ID, t.RESULT_VALUE, t.RESULT_UOM_ID from FG_I_SELECTEDRESULTS_V t where t.RESULT_TEST_NAME = 'Analytical' and t.SAMPLE_ID ="+sampleId);
			 * if(results!=null)
			 * {
			 * setMaterialsInWorkup(results, "MotherLiquor", "WuCryMixDefineRef", "CONCENTRATION", formId, userId);
			 * }
			 * List<String> selectedSamples = generalDao.getListOfStringBySql("select t.sample_id  from FG_s_sample_V t where BATCH_ID = "+batchId+" and sessionid is null");
			 * for(String selectedSample:selectedSamples){
			 * insertToSelectTable("SampleSelect",formId,"SAMPLETABLE",Arrays.asList(selectedSample),true,userId);
			 * }
			 * }
			 * }
			 */

			//if added a sample from sample search(not from the table) then add it to the sample table
			if (elementValueMap.get("sampleId") != null
					&& !generalUtil.getNull(elementValueMap.get("sampleId")).isEmpty()) {
				String sessionId_ = generalUtilFormState.getSessionId(formId);
				formDao.insertToSelectTable("SampleSelect", formId, "SAMPLETABLE",
						Arrays.asList(elementValueMap.get("sampleId")),
						generalUtil.getNull(saveAction).equals("UPDATE_STAGE_STATUS") ? true : false, userId, null);
				//updates workupstage selected sample to the current one
				//formSaveDao.updateStructTableByFormId("update FG_S_SAMPLE_PIVOT set workupStage = "+ elementValueMap.get("STAGE_ID") +" where formId = "+elementValueMap.get("sampleId"), "FG_S_SAMPLE_PIVOT", Arrays.asList("workupStage"), elementValueMap.get("sampleId"));
			}

			//if save&next action then disables all the selected samples
			if (generalUtil.getNull(saveAction).equals("UPDATE_STAGE_STATUS")) {
				List<String> selectedSamples = generalDao
						.getListOfStringBySql("Select SAMPLE_ID from FG_S_SAMPLESELECT_ALL_V where PARENTID = " + formId
								+ " and sessionid is null and active ='1'");
				for (String sampleId : selectedSamples) {
					formDao.insertToSelectTable("SampleSelect", formId, "SAMPLETABLE", Arrays.asList(sampleId), true,
							userId, null);
				}
			}

		}

		/******** WorkupMain *********/
		if (formCode.equals("WorkupMain")) {
			String workupTypeName = formDao.getFromInfoLookup("WORKUPTYPE", LookupType.ID,
					elementValueMap.get("WORKUPTYPE_ID"), "name");

			String updateFormCode = "Workup"; //default

			if (workupTypeName.equals("Feeding")) {
				updateFormCode = "WorkupFeeding";
			} else if (workupTypeName.equals("Filtration")) {
				updateFormCode = "WorkupFiltration";
			} else if (workupTypeName.equals("Drying")) {
				updateFormCode = "WorkupDrying";
			} else if (workupTypeName.equals("Washing")) {
				updateFormCode = "WorkupWashExtract";
			} else if (workupTypeName.equals("Extraction")) {
				updateFormCode = "WorkupWashExtract";
			} else if (workupTypeName.equals("Distillation")) {
				updateFormCode = "WorkupDistillation";
			} else if (workupTypeName.equals("Crystallization")) {
				updateFormCode = "WorkupCrystallize";
			}

			/*
			 * formSaveDao.updateSingleString(
			 * "update FG_SEQUENCE SET FORMCODE = '" + updateFormCode + "' WHERE id = '" + formId + "'");
			 */
			formSaveDao.updateStructTableFormCode("Workup", updateFormCode, formId, true);

		}

		else if (formCode.equals("InvItemCalibration")) {
			//update lastCalibrationDate and calibrationType in INVITEMINSTRUMENT
			Date maxCalibD;
			String conversionDateFormat = generalUtil.getConversionDateFormat();
			DateFormat df = new SimpleDateFormat(conversionDateFormat);
			String parentId = elementValueMap.get("INVITEMINSTRUMENT_ID");
			/*
			 * if (!generalDao.selectSingleStringNoException(String.format(
			 * " select formid"
			 * + " from fg_s_invitemcalibration_pivot t"
			 * + " where INVITEMINSTRUMENT_ID = %1$s"
			 * + " and trunc(to_date(t.CALIBRATIONDATE,'" + conversionDateFormat + "')) <= trunc(sysdate)",
			 * parentId, elementValueMap.get("calibrationDate"))).isEmpty()) {
			 */
			//ef fix bug 4169->ab changed ef fixing 19.03.18
			//maxCalibD = df.parse(generalDao.selectSingleString(String.format(" select TO_CHAR(CALIBRATIONDATE,'dd/MM/yyyy') from( select max (TO_DATE(CALIBRATIONDATE,'dd/MM/yyyy')) as CALIBRATIONDATE from (select CALIBRATIONDATE from fg_s_invitemcalibration_all_v where INVITEMINSTRUMENT_ID = %1$s union all select TO_CHAR(TO_DATE('%2$s','dd/MM/yyyy'),'dd/MM/yyyy')  from dual)t where t.CALIBRATIONDATE <= to_char(sysdate,'DD/MM/YYYY'))",parentId,elementValueMap.get("calibrationDate"))));

			Map<String, String> maxCalibData = generalDao.getMetaDataRowValues(
					String.format(" select  formid,CALIBRATIONTYPE_ID,to_char(TO_date(CALIBRATIONDATE,'"
							+ conversionDateFormat + "'),'" + conversionDateFormat + "') as LASTCALIBRATIONDATE"
							+ " from fg_s_invitemcalibration_all_v t" + " where formid in"
							+ "(select first_value(formid) over(partition by INVITEMINSTRUMENT_ID order by trunc(to_date(CALIBRATIONDATE,'"
							+ conversionDateFormat + "')) desc)" + " as formid from" + " fg_s_invitemcalibration_all_v"
							+ " where INVITEMINSTRUMENT_ID = '%1$s'" + " and trunc(to_date(CALIBRATIONDATE,'"
							+ conversionDateFormat + "')) <= trunc(sysdate))" + " and rownum<=1", parentId));
			String maxCalibDate = null;
			String type = null;
			String nextCalibDate = null;
			if (maxCalibData != null && !maxCalibData.isEmpty()) {
				maxCalibD = df.parse(maxCalibData.get("LASTCALIBRATIONDATE"));
				maxCalibDate = df.format(maxCalibD);
				type = maxCalibData.get("CALIBRATIONTYPE_ID");/*
																 * generalDao.selectSingleString(String.format(
																 * "  select CALIBRATIONTYPE_ID from (select CALIBRATIONDATE,CALIBRATIONTYPE_ID from fg_s_invitemcalibration_all_v where INVITEMINSTRUMENT_ID = %1$s union all select TO_CHAR(TO_DATE('%4$s','"
																 * + generalUtil.getConversionDateFormat() + "'),'" + generalUtil.getConversionDateFormat()
																 * + "') as CALIBRATIONDATE , '%3$s' as CALIBRATIONTYPE_ID from dual) t where  t.CALIBRATIONDATE = '%2$s'and rownum <= 1 ORDER BY rownum",
																 * parentId, maxCalibDate, elementValueMap.get("CALIBRATIONTYPE_ID"),
																 * elementValueMap.get("calibrationDate")));
																 */

				nextCalibDate = generalDao.selectSingleString(String.format(
						/*
						 * "  select TO_CHAR(NEXTCALIBDATE,'" + conversionDateFormat
						 * + "') from"
						 * + " (select min(NEXTCALIBDATE) as NEXTCALIBDATE from"
						 * +" (select TO_DATE(NEXTCALIBDATE,'dd/MM/yyyy') as NEXTCALIBDATE"
						 * + " from fg_s_invitemcalibration_all_v where INVITEMINSTRUMENT_ID = %1$s and INVITEMCALIBRATION_ID <> %3$s and CALIBRATIONTYPE_ID = '"+maxCalibData.get("CALIBRATIONTYPE_ID")
						 * + "' union all select (TO_DATE('%4$s','dd/MM/yyyy')) from dual where "+elementValueMap.get("CALIBRATIONTYPE_ID").equals(maxCalibData.get("CALIBRATIONTYPE_ID"))+")"
						 */ /*
							 * + " union all"
							 * + " select TO_DATE(CALIBRATIONDATE,'dd/MM/yyyy') as NEXTCALIBDATE from fg_s_invitemcalibration_all_v where INVITEMINSTRUMENT_ID = %1$s and INVITEMCALIBRATION_ID <> %3$s)"
							 * + " union all select (TO_DATE('%2$s','dd/MM/yyyy')) from dual)
							 */
						"select TO_CHAR(max(to_date(NEXTCALIBDATE,'" + conversionDateFormat + "')),'"
								+ conversionDateFormat + "') as NEXTCALIBRATION from "
								+ " fg_s_invitemcalibration_all_v" + " where INVITEMINSTRUMENT_ID = %1$s"
								+ " and CALIBRATIONTYPE_ID = '%2$s'"
								+ " and trunc(sysdate-1) < trunc(to_date(NEXTCALIBDATE,'" + conversionDateFormat
								+ "'))",
						parentId, maxCalibData.get("CALIBRATIONTYPE_ID")));
			}

			formSaveDao.updateStructTableByFormId(
					String.format(
							" update FG_S_INVITEMINSTRUMENT_PIVOT set CALIBRATIONDATE = '%1$s' , CALIBTYPE_ID = %2$s , NEXTCALIBDATE = '%4$s' where formId =  %3$s",
							generalUtil.getNull(maxCalibDate), generalUtil.getNull(type), parentId,
							generalUtil.getNull(nextCalibDate)),
					"FG_S_INVITEMINSTRUMENT_PIVOT", Arrays.asList("CALIBRATIONDATE", "CALIBTYPE_ID", "NEXTCALIBDATE"),
					parentId);
			//}
		}

		/********** ExperimentAn ***********/
		else if (formCode.equals("ExperimentAn")) {
			String experimentTypeName = formDao.getFromInfoLookup("ExperimentType", LookupType.ID,
					elementValueMap.get("EXPERIMENTTYPE_ID"), "name");
			updateManualResultsTable(formCode, formId, userId, experimentTypeName,elementValueMap);
			//String isWebixDataExist = generalDao.selectSingleString("select max(RESULT_IS_WEBIX) from fg_results where RESULT_IS_ACTIVE = '1' and EXPERIMENT_ID = '"+formId+"'");
			//if(generalUtil.getNull(isWebixDataExist).trim().equals("1")){//there are results taken from the webix table
			//formSaveDao.updateSingleStringInfo("update FG_RESULTS set RESULT_IS_ACTIVE = '0' where EXPERIMENT_ID = '"+formId+"' and RESULT_IS_WEBIX = 0");//deplete the manual results
			//adib- 21052018 task 16035
			//}
			//else{//insert manual results to the result table; adib- removed if-else statement 21052018 task 16035
			List<Result> resultList = new ArrayList<>();

			List<Map<String, Object>> manualResults = generalDao.getListOfMapsBySql(
					"Select distinct formid, SAMPLE_ID,MATERIAL_ID,UOM_ID,COMMENTS as \"COMMENTS\",RESULT,ANALYTICRESULTTYPENAME,REQUEST_ID from fg_s_manualResultsRef_all_v where result is not null and parentid = '"
							+ formId + "'" + generalUtilFormState.getWherePartForTmpData("manualresultsref", formId));

			for (Map<String, Object> resultInfo : manualResults) {
				String resultType = resultInfo.get("ANALYTICRESULTTYPENAME").toString();/*
																						 * experimentTypeName
																						 * .contains("Impurity")
																						 * ? generalUtil.getSpringMessagesByKey(generalDao
																						 * .selectSingleString("select TESTEDCOMPTYPENAME from fg_s_component_all_v"
																						 * + " where parentid = '" + formId + "'" + " and materialId = '"
																						 * + (resultInfo.get("MATERIAL_ID") != null
																						 * ? resultInfo.get("MATERIAL_ID").toString() : "")
																						 * + "' and sessionid is null and nvl(active,'1') = '1' and rownum<=1")
																						 * .replace(" ", ""), "")
																						 * : (experimentTypeName.contains("Assay") ? experimentTypeName
																						 * : resultInfo.get("ANALYTICRESULTTYPENAME").toString());
																						 *///removed the code due to task 20201

				resultList.add(new Result(formId//exp_id
						, "Analytical"//test type
						, resultType//result type
						, resultInfo.get("SAMPLE_ID").toString(), resultInfo.get("RESULT").toString(),
						resultInfo.get("UOM_ID").toString(), "NUMERIC",
						resultInfo.get("MATERIAL_ID") != null ? resultInfo.get("MATERIAL_ID").toString() : "",
						resultInfo.get("COMMENTS") != null ? resultInfo.get("COMMENTS").toString() : "", ""//selftest_id
						, "", resultInfo.get("FORMID").toString(), "0", ""//resultInfo.get("MATERIALNAME").toString()
						, resultInfo.get("REQUEST_ID") != null ? resultInfo.get("REQUEST_ID").toString() : ""));

				/*
				 * if (experimentTypeName.equals("General")) {
				 * String sql = "update FG_RESULTS set RESULT_NAME = '"
				 * + resultInfo.get("ANALYTICRESULTTYPENAME").toString() + "'" + " where EXPERIMENT_ID = '"
				 * + formId + "'" + " and RESULT_IS_WEBIX = '1'" + " and RESULT_MATERIAL_ID = '"
				 * + resultInfo.get("MATERIAL_ID").toString() + "'" + " and SAMPLE_ID = '"
				 * + resultInfo.get("SAMPLE_ID").toString() + "'";
				 * formSaveDao.updateSingleString(sql);
				 * }
				 *///removed the code due to task 20201
			}

			String experimentStatus = formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.ID,
					elementValueMap.get("STATUS_ID"), "name");
			if (experimentStatus.equals("Approved")) {//check if chromatograms tab has attachments. if so-then add an empty result
				//get from documents table where tableType is chromatorgrams
				List<String> sampleList = generalDao.getListOfStringBySql("select sample_id from fg_s_sampleselect_all_v where parentid =  '"+formId+"'"
						+ " and sessionid is null and active=1");
				List<String> sampleInChrom = new ArrayList<>();
				List<String> chromatograms = generalDao
						.getListOfStringBySql("select distinct SAMPLE_ID from fg_s_document_v where parentid='" + formId
								+ "' and sessionid is null and active=1 and tabletype='expChromatogramsSample' and sample_id is not null");
				for (String chromatogram_sample : chromatograms){
					sampleInChrom.add(chromatogram_sample);
					resultList.add(new Result(formId//exp_id
							, "Analytical"//test type
							, experimentTypeName//result type
							, generalUtil.getNull(chromatogram_sample), "", "", "STRING", "", "", ""//selftest_id
							, "", "", "0", ""//resultInfo.get("MATERIALNAME").toString()
							, ""));
				}
				
				//adib 27/02/2020 added the following code as a temporary result detailed in the task 24987
				chromatograms = generalDao
						.getListOfStringBySql("select distinct formid from fg_s_document_v where parentid='" + formId
								+ "' and sessionid is null and active=1 and tabletype='expChromatogramsSample' and sample_id is null");
				for(String chromatogram_id:chromatograms){
					for(String sample_id:sampleList){
						if(!sampleInChrom.contains(sample_id)){//if sample did not get a chromatogram result then add a result related to the current sample to chromatogram that has no samples
							//all the samples have results related to chromatograms that are not connected to any sample(see implementation in the DB func fg_get_smart_link)
							resultList.add(new Result(formId//exp_id
									, "Analytical"//test type
									, experimentTypeName//result type
									, generalUtil.getNull(sample_id), "", "", "STRING", "", "", ""//selftest_id
									, "", "", "0", ""//resultInfo.get("MATERIALNAME").toString()
									, ""));
								
							
						}
					}
				}//end of temp result
			}

			if (experimentTypeName.equals("Impurity Identification")) {
				List<Map<String, Object>> manualResultsMS = generalDao.getListOfMapsBySql(
						"Select distinct formid, SAMPLE_ID,materialname,COMMENTS as \"COMMENTS\",REQUEST_ID from fg_s_manualResultsMSRef_all_v where parentid = '"
								+ formId + "' and sessionid is null and nvl(active,'1')='1'");

				for (Map<String, Object> resultInfo : manualResultsMS) {
					resultList.add(new Result(formId//exp_id
							, "Analytical"//test type
							, experimentTypeName//result type
							, resultInfo.get("SAMPLE_ID").toString(), "", "", "STRING", "",
							resultInfo.get("COMMENTS") != null ? resultInfo.get("COMMENTS").toString() : "", ""//selftest_id
							, "", resultInfo.get("FORMID").toString(), "0",
							resultInfo.get("MATERIALNAME") != null ? resultInfo.get("MATERIALNAME").toString() : ""//resultInfo.get("MATERIALNAME").toString()
							, resultInfo.get("REQUEST_ID") != null ? resultInfo.get("REQUEST_ID").toString() : ""));

					/*
					 * if (experimentTypeName.equals("General")) {
					 * String sql = "update FG_RESULTS set RESULT_NAME = '"
					 * + resultInfo.get("ANALYTICRESULTTYPENAME").toString() + "'" + " where EXPERIMENT_ID = '"
					 * + formId + "'" + " and RESULT_IS_WEBIX = '1'" + " and RESULT_MATERIAL_ID = '"
					 * + resultInfo.get("MATERIAL_ID").toString() + "'" + " and SAMPLE_ID = '"
					 * + resultInfo.get("SAMPLE_ID").toString() + "'";
					 * formSaveDao.updateSingleString(sql);
					 * }
					 *///removed the code due to task 20201
				}

			}

			if (!resultList.isEmpty()) {
				boolean doUpdateMainResults = Integer.parseInt(elementValueMap.get("experimentVersion"))>1?true:false;//when the experiment version is larger than 1 , the user may delete results that were already assign as main. If so, it is necessary to remove it from the main results of the sample
				commonFunc.insertToResults(resultList, formId, elementValueMap,doUpdateMainResults);
			}
			//}
		}

		/********** SampleMain ***********/
		else if (formCode.equals("SampleMain")) {
			//			String updateFormCode = "Sample";
			/*
			 * formSaveDao.updateSingleString(
			 * "update FG_SEQUENCE SET FORMCODE  = '" + updateFormCode + "' WHERE id = '" + formId + "'");
			 */
			formSaveDao.updateStructTableFormCode("Sample", "Sample", formId, true);
			int numOfSamples = Integer.parseInt(elementValueMap.get("numOfSamples"));
			List<String> listOfSamples = new ArrayList<String>();
			listOfSamples.add(formId);
			if (numOfSamples > 1) {
				doMultiSamples(formId, numOfSamples, elementValueMap, userId, listOfSamples);
			}
			
			//printLableOnCreation check and return PRINT_ON_LOAD bean if checked
			try {
				String printLableOnCreation_ = elementValueInfATMap.get("printLableOnCreation");
				if(generalUtil.getNull(printLableOnCreation_).equals("1")) {
					dataBeanReturnList.add(new DataBean("sampleGeneral", generalUtil.listToCsv(listOfSamples), BeanType.PRINT_ON_LOAD, ""));
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.WARN,
						"printLableOnCreation_ check failed!",
						ActivitylogType.SaveEvent, formId);
			}
 

			List<String> listOfAncestors = new ArrayList<String>();
			if (!elementValueMap.get("originExperimentId").isEmpty()) {
				listOfAncestors.add("EXPERIMENT_ID");
			} else if (!elementValueMap.get("originStepId").isEmpty()) {
				listOfAncestors.add("EXPERIMENT_ID");
				listOfAncestors.add("STEP_ID");
			} else if (!elementValueMap.get("originActionId").isEmpty()) {
				listOfAncestors.add("EXPERIMENT_ID");
				listOfAncestors.add("STEP_ID");
				listOfAncestors.add("ACTION_ID");
			} else if (!elementValueMap.get("originWorkupId").isEmpty()
					|| !elementValueMap.get("originSelfTestId").isEmpty()) {
				listOfAncestors.add("EXPERIMENT_ID");
				listOfAncestors.add("STEP_ID");
				listOfAncestors.add("ACTION_ID");
				listOfAncestors.add("parentId");

				//insert the sampleid to the selftest/workup sampleid
				String parentFormCode = formDao.getFormCodeEntityBySeqId("", elementValueMap.get("parentId"));
				String parentTable = "fg_s_" + parentFormCode + "_pivot";
				if (generalUtil.getEmpty(generalDao.selectSingleStringNoException("select sampleId from " + parentTable
						+ " where formId = '" + elementValueMap.get("parentId") + "'"), "").isEmpty()) {
					//if parent form has no sample number in the general tab then add this one that was created from
					formSaveDao.updateStructTableByFormId(
							"update " + parentTable + "" + " set sampleId = '" + formId + "', sampleNumber = '"
									+ elementValueMap.get("sampleName") + "'" + " where formId = '"
									+ elementValueMap.get("parentId") + "' ",
							parentTable, Arrays.asList("sampleId", "sampleNumber"), elementValueMap.get("parentId"));
				}
			}

			//Update the sample selection of the ancestors
			String sessionId_  = "";
			for (String ancestor : listOfAncestors) {
				formDao.insertToSelectTable("SampleSelect", elementValueMap.get(ancestor), "SAMPLETABLE", listOfSamples,
						true, userId, null);
			}

			if (!elementValueMap.get("BATCH_ID").isEmpty()) {
				formDao.insertToSelectTable("SAMPLESELECT", elementValueMap.get("BATCH_ID"), "SAMPLETABLE",
						listOfSamples, true, userId, null);//add the sample to the selection in the referenced batch
			}
		}
		/************************** BatchMain *************************/
		else if (formCode.equals("BatchMain")) {
			/*
			 * formSaveDao
			 * .updateSingleString("update FG_SEQUENCE SET FORMCODE = 'InvItemBatch' WHERE id = '" + formId + "'");
			 */
			formSaveDao.updateStructTableFormCode("InvItemBatch", "InvItemBatch", formId, true);
			if (!elementValueMap.get("SAMPLE_ID").isEmpty()) {
				//first, update the batch element in the related sample form
				String sqlString = "update FG_S_SAMPLE_PIVOT set BATCH_ID = '" + formId
						+ "', IS_BATCHDEF_DISABLED = '1',BATCHDEFINITION = '1', BATCHNAME = '"
						+ elementValueMap.get("formNumberId") + "' where FORMID = '" + elementValueMap.get("SAMPLE_ID")
						+ "'";
				formSaveDao.updateStructTableByFormId(sqlString, "FG_S_SAMPLE_ALL_V",
						Arrays.asList("BATCH_ID", "IS_BATCHDEF_DISABLED", "BATCHNAME", "BATCHDEFINITION"),
						elementValueMap.get("SAMPLE_ID"));
				//second, insert the related sample to the batch sampleselect table in the tab samples 
				formDao.insertToSelectTable("SampleSelect", formId, "SAMPLETABLE",
						Arrays.asList(elementValueMap.get("SAMPLE_ID")), true, userId, null);
			}
			//update the batches table in the connected recipe that defined in the experiment
			String recipeFormulationId = elementValueMap.get("RECIPEFORMULATION_ID");
			if(!recipeFormulationId.isEmpty()){
				formDao.insertToSelectTable("BatchSelect", recipeFormulationId, "BATCH_ID", Arrays.asList(formId), true, userId,null);
			}
			//insert the compositions into the components table.
			//if the batch was created from the experiment or from a step that defined as the 'last step'-copy the planned compositions table, else- copy the material table of the step that created the batch
			String parentId = elementValueMap.get("parentId");
			String parentFormcode = formDao.getFormCodeBySeqId(parentId);
			if(elementValueMap.get("lastStep").equals("1")){
				String sql = "select DISTINCT INVITEMMATERIAL_ID,WW_P,BATCH_ID,MaterialFunction_id AS FUNCTION_ID,FILLER\n"
						+ " from fg_s_composition_ALL_v\n"
						+ " where parentid = '"+elementValueMap.get("EXPERIMENT_ID")+"'\n"
						+ " and tableType = 'expComposition'\n"
						+ " and sessionid is null\n"
						+ " and active = '1'";
				List<Map<String,Object>> compositionList = generalDao.getListOfMapsBySql(sql);
				for(Map<String,Object> compositionData:compositionList){
					String materialId = compositionData.get("INVITEMMATERIAL_ID").toString();
					String ww_p = compositionData.get("WW_P")!=null?compositionData.get("WW_P").toString():"";
					String componentId = formSaveDao.getStructFormId("MaterialComponent");
					String batch_id = compositionData.get("BATCH_ID")!=null?compositionData.get("BATCH_ID").toString():"";
					String function_id = compositionData.get("FUNCTION_ID")!=null?compositionData.get("FUNCTION_ID").toString():"";
					String filler = compositionData.get("FILLER")!=null?compositionData.get("FILLER").toString():"0";
					sql = "insert into FG_S_MATERIALCOMPONENT_pivot T\n"
						+ " (FORMID,t.TIMESTAMP,t.CHANGE_BY,t.CREATION_DATE,t.CREATED_BY,t.SESSIONID,t.ACTIVE,t.FORMCODE,t.FORMCODE_ENTITY,MATERIAL_ID,CONCENTRATION,PARENTID,BATCH_ID,FUNCTION_ID,FILLER)\n"
						+ " values("+componentId+",sysdate,'"+userId+"',sysdate,'"+userId+"',null,1,'MATERIALCOMPONENT','MATERIALCOMPONENT','"+materialId+"','"+ww_p+"','"+formId+"','"+batch_id+"','"+function_id+"','"+filler+"')";
					formSaveDao.insertStructTableByFormId(sql, "FG_S_MATERIALCOMPONENT_pivot", componentId);
				}
			} else if(parentFormcode.equals("StepFr")){
				String sql = "select DISTINCT INVITEMMATERIAL_ID,ww_p,SUM(ww_p)  over (partition by parentid) SUM_WW_P,MaterialFunction_id AS FUNCTION_ID,FILLER\n"
						+ " from fg_s_composition_ALL_v\n"
						+ " where parentid = '"+parentId+"'\n"
						+ " and tableType = 'stepComposition'\n"
						+ " and sessionid is null\n"
						+ " and active = '1'";
				List<Map<String,Object>> compositionList = generalDao.getListOfMapsBySql(sql);
				for(Map<String,Object> compositionData:compositionList){
					String materialId = compositionData.get("INVITEMMATERIAL_ID").toString();
					String ww_p = compositionData.get("WW_P")!=null?compositionData.get("WW_P").toString():"0";
					String function_id = compositionData.get("MATERIALFUNCTION_ID")!=null?compositionData.get("MATERIALFUNCTION_ID").toString():"";
					String filler = compositionData.get("FILLER")!=null?compositionData.get("FILLER").toString():"0";
					String componentId = formSaveDao.getStructFormId("MaterialComponent");
					String sumWW_P = compositionData.get("SUM_WW_P")!=null?compositionData.get("SUM_WW_P").toString():"0";
					String experiment_id  = formDao.getFromInfoLookup("Step", LookupType.ID, parentId, "EXPERIMENT_ID");
					String batch_id = generalDao.selectSingleStringNoException("select invitembatch_id from fg_s_composition_v\n"
							+ " where parentid = '"+experiment_id+"'\n"
							+ " and tableType = 'expComposition'\n"
							+ " and active =1\n"
							+ " and invitemmaterial_id = '"+materialId+"'\n"
							+generalUtilFormState.getWherePartForTmpData("composition", experiment_id));
					double ratio = Double.parseDouble(ww_p)/Double.parseDouble(sumWW_P)*100;//when copying from the materials table(the sum of all the materials is not 100%) then should normal the values so that the sum of them would be 100%
					sql = "insert into FG_S_MATERIALCOMPONENT_pivot T\n"
						+ " (FORMID,t.TIMESTAMP,t.CHANGE_BY,t.CREATION_DATE,t.CREATED_BY,t.SESSIONID,t.ACTIVE,t.FORMCODE,t.FORMCODE_ENTITY,MATERIAL_ID,CONCENTRATION,PARENTID,BATCH_ID,FUNCTION_ID,FILLER)\n"
						+ " values("+componentId+",sysdate,'"+userId+"',sysdate,'"+userId+"',null,1,'MATERIALCOMPONENT','MATERIALCOMPONENT','"+materialId+"','"+ratio+"','"+formId+"','"+batch_id+"','"+function_id+"','"+filler+"')";
					formSaveDao.insertStructTableByFormId(sql, "FG_S_MATERIALCOMPONENT_pivot", componentId);
				}
			}

		} else if (formCode.equals("ExpTemplateSelect")) {
			String[] selectedExpData = elementValueMap.get("EXPERIMENT_ID").split(",");
			if (selectedExpData.length < 8 || selectedExpData[7].isEmpty()) {
				return auditTrailChangeType;
			}

			//clone Experiment	
			String selectExperimentId = elementValueMap.get("EXPERIMENT_ID").split(",")[7].toString();
			String protocolTypeId = generalDao.selectSingleString(
					"select PROTOCOLTYPE_ID from FG_S_EXPERIMENT_V where EXPERIMENT_ID = '" + selectExperimentId + "'");
			String protocolTypeName = formDao.getFromInfoLookup("ProtocolType", LookupType.ID, protocolTypeId, "name");

			String cloneExperimentId = formSaveDao.getStructFormId("EXPERIMENT");
			cloneExperiment.cloneExperiment(selectExperimentId, protocolTypeName, cloneExperimentId, userId, CloneType.TO_TEMPLATE,null);

			formSaveDao.deleteStructTable(
					String.format("delete from %1$s t where t.PARENTID = %2$s", "FG_S_EXPTEMPLATESELECT_PIVOT",
							elementValueMap.get("parentId")),
					"FG_S_EXPTEMPLATESELECT_PIVOT", "PARENTID", elementValueMap.get("parentId"));
			formDao.insertToSelectTable("ExpTemplateSelect", elementValueMap.get("parentId"), "EXPERIMENT_ID",
					Arrays.asList(cloneExperimentId), false, userId, null);

			//formSaveDao.updateSingleString("update FG_S_TEMPLATE_PIVOT SET SOURCEEXPNO_ID = "+cloneExperimentId+", EXPERIMENT_ID = "+cloneExperimentId+", PARENTID = "+cloneExperimentId+" WHERE formid  = " + elementValueMap.get("parentId") );
			String sql = "update FG_S_TEMPLATE_PIVOT SET SOURCEEXPNO_ID = " + cloneExperimentId + ", EXPERIMENT_ID = "
					+ selectExperimentId + ", PARENTID = " + selectExperimentId + " WHERE formid  = "
					+ elementValueMap.get("parentId");
			//formSaveDao.updateSingleString(sql);//ta 290718 changed for search
			formSaveDao.updateStructTableByFormId(sql, "FG_S_TEMPLATE_PIVOT",
					Arrays.asList("SOURCEEXPNO_ID", "EXPERIMENT_ID", "PARENTID"), elementValueMap.get("parentId"));

			String experimentTypeId = generalDao
					.selectSingleString("select EXPERIMENTTYPE_ID from FG_S_EXPERIMENT_V where EXPERIMENT_ID = '"
							+ selectExperimentId + "'");
			String experimentTypeName = formDao.getFromInfoLookup("EXPERIMENTTYPE", LookupType.ID, experimentTypeId,
					"name");
			String updateFormCode = "Experiment"; //default
			if (protocolTypeName.equals("Analytical")) {
				updateFormCode = "ExperimentAn";
			} else if (protocolTypeName.equals("Formulation")) {
				updateFormCode = "ExperimentFor";
			} else if (protocolTypeName.equals("Parametric") && experimentTypeName.equals("Corrosion")) {
				updateFormCode = "ExperimentPrCR";
			} else if (protocolTypeName.equals("Parametric") && experimentTypeName.equals("Viscosity")) {
				updateFormCode = "ExperimentPrVS";
			} else if (protocolTypeName.equals("Parametric") && experimentTypeName.equals("TSU")) {
				updateFormCode = "ExperimentPrTS";
			} else if (protocolTypeName.equals("Parametric") && experimentTypeName.equals("Bottles")) {
				updateFormCode = "ExperimentPrBT";
			} else if (protocolTypeName.equals("Parametric")) {
				updateFormCode = "ExperimentPrGn";
			} else if (protocolTypeName.equals("Stability")) { // add ExperimentStb for "Taro develop"
				updateFormCode = "ExperimentStb";
			} else if (protocolTypeName.equals("Continuous Process")) { //add ExperimentCP for "Continuous Process"
				updateFormCode = "ExperimentCP";
			}
			
			/*
			 * formSaveDao.updateSingleString("update FG_SEQUENCE SET FORMCODE = '" + updateFormCode + "' WHERE id = '"
			 * + cloneExperimentId + "'");
			 */
			formSaveDao.updateStructTableFormCode("Experiment", updateFormCode, cloneExperimentId, true);

		} else if (formCode.equals("TemplateMain")) {

			//formSaveDao.updateSingleString("update FG_SEQUENCE SET FORMCODE = 'Template' WHERE id = '" + formId + "'");
			formSaveDao.updateStructTableFormCode("Template", "Template", formId, true);
 
			/*if(!generalUtil.getNull(elementValueMap.get("expDesign")).isEmpty()){
				formSaveDao.updateSingleStringInfoNoTryCatch("update fg_s_exprunplanning_pivot set copytotemplate = '1' where formid in(" + elementValueMap.get("expDesign")+")");
			}*/
			
			//clone Experiment	
			String cloneExperimentId = formSaveDao.getStructFormId("EXPERIMENT");
			cloneExperiment.cloneExperiment(elementValueMap.get("SOURCEEXPNO_ID"), elementValueMap.get("protocolType"),
					cloneExperimentId, userId, CloneType.TO_TEMPLATE,null);
			/*if(!generalUtil.getNull(elementValueMap.get("expDesign")).isEmpty()){
				*/formSaveDao.updateSingleStringInfoNoTryCatch("update fg_s_exprunplanning_pivot set copytotemplate = '' where copytotemplate = '1'");
			//}
			/*
			 * formSaveDao.updateSingleString("update FG_S_TEMPLATE_PIVOT SET SOURCEEXPNO_ID = " + cloneExperimentId
			 * + " WHERE formid  = " + formId);
			 *///ta 290718 changed for search
			formSaveDao.updateStructTableByFormId("update FG_S_TEMPLATE_PIVOT SET SOURCEEXPNO_ID = " + cloneExperimentId
					+ " WHERE formid  = " + formId, "FG_S_TEMPLATE_PIVOT", Arrays.asList("SOURCEEXPNO_ID"), formId);

			formDao.insertToSelectTable("ExpTemplateSelect", formId, "EXPERIMENT_ID", Arrays.asList(cloneExperimentId),
					false, userId, null);

			String protocolTypeName = elementValueMap.get("protocolType");
			String experimentTypeId = generalDao
					.selectSingleString("select EXPERIMENTTYPE_ID from FG_S_EXPERIMENT_V where EXPERIMENT_ID = '"
							+ elementValueMap.get("SOURCEEXPNO_ID") + "'");
			String experimentTypeName = formDao.getFromInfoLookup("EXPERIMENTTYPE", LookupType.ID, experimentTypeId,
					"name");
			String updateFormCode = "Experiment"; //default
			if (protocolTypeName.equals("Analytical")) {
				updateFormCode = "ExperimentAn";
			} else if (protocolTypeName.equals("Formulation")) {
				updateFormCode = "ExperimentFor";
			} else if (protocolTypeName.equals("Parametric") && experimentTypeName.equals("Corrosion")) {
				updateFormCode = "ExperimentPrCR";
			} else if (protocolTypeName.equals("Parametric") && experimentTypeName.equals("Viscosity")) {
				updateFormCode = "ExperimentPrVS";
			} else if (protocolTypeName.equals("Parametric") && experimentTypeName.equals("TSU")) {
				updateFormCode = "ExperimentPrTS";
			} else if (protocolTypeName.equals("Parametric") && experimentTypeName.equals("Bottles")) {
				updateFormCode = "ExperimentPrBT";
			} else if (protocolTypeName.equals("Parametric")) {
				updateFormCode = "ExperimentPrGn";
			} else if (protocolTypeName.equals("Stability")) { // add ExperimentStb for "Taro develop"
				updateFormCode = "ExperimentStb";
			} else if (protocolTypeName.equals("Continuous Process")) { //add ExperimentCP for "Continuous Process"
				updateFormCode = "ExperimentCP";
			}

			/*
			 * formSaveDao.updateSingleString("update FG_SEQUENCE SET FORMCODE = '" + updateFormCode + "' WHERE id = '"
			 * + cloneExperimentId + "'");
			 */
			formSaveDao.updateStructTableFormCode("Experiment", updateFormCode, cloneExperimentId, true);

		}
		/******************************************************** Template *********************************************************/
		else if (formCode.equals("Template")) {
			String statusName = formDao.getFromInfoLookup("TEMPLATESTATUS", LookupType.ID,
					elementValueMap.get("STATUS_ID"), "name");

			if (statusName.equals("Approved")) {

				//get table name
				Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
				formCodeEntity = generalUtil.getNull(form.getFormCodeEntity(), formCode);
				String table = "FG_S_" + formCodeEntity + "_PIVOT";

				//Update status to Obsolete
				Map<String, String> formVal = formDao.getFromInfoLookupAll(formCodeEntity, LookupType.ID, formId);
				//List<String> templates = generalDao.getListOfStringBySql("select t.TEMPLATE_ID  from FG_S_Template_ALL_V t where t.FORMID <> '"+formId+"' and t.TEMPLATENAME ='"+formVal.get("TEMPLATENAME")+"' and t.TEMPLATEVERSION = '"+formVal.get("TEMPLATEVERSION")+"'");
				List<String> templates = generalDao
						.getListOfStringBySql("select t.TEMPLATE_ID  from FG_S_Template_ALL_V t where t.FORMID <> '"
								+ formId + "' and t.TEMPLATENAME ='" + formVal.get("TEMPLATENAME") + "'");
				String statusId = "";
				if (templates.size() > 0) {
					statusId = formDao.getFromInfoLookup("TEMPLATESTATUS", LookupType.NAME, "Obsolete", "id");

					for (String tempId : templates) {
						String sql_ = String.format(" update %1$s set  STATUS_ID = '%2$s'  where FORMID =  %3$s", table,
								statusId, tempId);
						List<String> colList = Arrays.asList("STATUS_ID");
						formSaveDao.updateStructTableByFormId(sql_, table, colList, tempId);
					}
				}
			}
		}

		else if (formCode.equals("FeedbackRef")) {

			String sql_ = "update FG_S_FeedbackRef_PIVOT SET SESSIONID = null WHERE formid = '" + formId + "'";
			List<String> colList = Arrays.asList("SESSIONID");
			formSaveDao.updateStructTableByFormId(sql_, "FG_S_FeedbackRef_PIVOT", colList, formId);
		}

		/********** Sample ***********/
		else if (formCode.equals("Sample")) {
			if (!elementValueMap.get("BATCH_ID").isEmpty()) {
				if (elementValueMap.get("batchDefinition").equals("1")) {//current batch was chosen
					//first cancels the previous defined batch and the chosen
					/*
					 * String sqlString = "Select FORMID from FG_S_INVITEMBATCH_PIVOT where (SAMPLE_ID = '" + formId
					 * + "' and FORMID != '" + elementValueMap.get("BATCH_ID") + "')";//checks if the batch is different, for the case it's a checked batch that the sample data just was changed
					 * //+" or (SAMPLE_ID is not NULL and SAMPLE_ID !='"+ formId +"' and FORMID = '"+elementValueMap.get("BATCH_ID")+"')";//checks if the batch was defined in another sample- if so removes its definition from that one"
					 * List<String> formIds = generalDao.getListOfStringBySql(sqlString);//note: this should always return empty list because the batch cannot be changed
					 * for (String currentFormId : formIds) {
					 * sqlString = "update FG_S_INVITEMBATCH_PIVOT t set SAMPLE_ID = NULL, PURITY = 0 where FORMID = "
					 * + currentFormId;
					 * formSaveDao.updateStructTableByFormId(sqlString, "FG_S_INVITEMBATCH_PIVOT",
					 * Arrays.asList("SAMPLE_ID", "PURITY"), currentFormId);
					 * List<String> tempFormId = generalDao
					 * .getListOfStringBySql("select formid from FG_S_SAMPLESELECT_PIVOT  where parentid = '"
					 * + currentFormId + "' and sessionid is null");
					 * if (!tempFormId.isEmpty()) {
					 * sqlString = "update FG_S_SAMPLESELECT_PIVOT set SAMPLETABLE = regexp_replace(SAMPLETABLE,',"
					 * + formId + "|" + formId + ",'), DISABLED = regexp_replace(DISABLED,'," + formId
					 * + "|" + formId + ",') where formid = '" + tempFormId.get(0) + "'";//removes the current sample from the batches that are not relevant anymore
					 * formSaveDao.updateStructTableByFormId(sqlString, "FG_S_SAMPLESELECT_PIVOT",
					 * Arrays.asList("SAMPLETABLE", "DISABLED"), tempFormId.get(0));
					 * }
					 * }
					 */
					//second, updates the chosen batch to be referenced to the current sample
					/*
					 * sqlString = "Select FORMID from FG_S_INVITEMBATCH_PIVOT where formid  = '"
					 * + elementValueMap.get("BATCH_ID") + "'";
					 * String updateFormId = generalDao.selectSingleString(sqlString);
					 */
					String updateFormId = elementValueMap.get("BATCH_ID");
					String sqlString = "update FG_S_INVITEMBATCH_PIVOT t set SAMPLE_ID = " + formId
							+ ",PURITY = nvl((Select RESULT_VALUE from FG_I_SELECTEDRESULTS_V r "
							+ "where t.INVITEMMATERIAL_ID = r.RESULT_MATERIAL_ID and r.SAMPLE_ID ='" + formId
							+ "' and RESULT_NAME = 'Assay'),t.PURITY) "
							+ "where formid  = " + updateFormId;
					formSaveDao.updateStructTableByFormId(sqlString, "FG_S_INVITEMBATCH_PIVOT",
							Arrays.asList("SAMPLE_ID", "PURITY"), updateFormId);

					sqlString = "update fg_s_materialcomponent_pivot t\n"
							+ "set concentration = nvl((Select RESULT_VALUE from FG_I_SELECTEDRESULTS_V r "
							+ "where t.MATERIAL_ID = r.RESULT_MATERIAL_ID and r.SAMPLE_ID ='" + formId
							+ "' and RESULT_NAME = 'Assay'),t.concentration)\n"
							+ ", approvalDate = nvl((Select to_char(RESULT_DATE,'dd/MM/yyyy HH24:MI') from FG_I_SELECTEDRESULTS_V r "
							+ "where t.MATERIAL_ID = r.RESULT_MATERIAL_ID and r.SAMPLE_ID ='" + formId
							+ "' and RESULT_NAME = 'Assay'),t.approvaldate)\n"
							+ " where parentid = '"+elementValueMap.get("BATCH_ID")+"'";
					formSaveDao.updateStructTable(sqlString, "fg_s_materialcomponent_pivot",
							Arrays.asList("concentration","approvaldate"),"parentid", elementValueMap.get("BATCH_ID"));
					
					Map<String, String> batchMap = new HashMap<>();
					batchMap = generalDao
							.sqlToHashMap("select purity,PURITYUOM_ID from fg_s_invitembatch_v where formid = '"
									+ elementValueMap.get("BATCH_ID") + "'");
					commonFunc.onChangeInventoryEvent("InvItemBatch", elementValueMap.get("BATCH_ID"), batchMap, userId,
							sbInfo);

					formDao.insertToSelectTable("SAMPLESELECT", updateFormId, "SAMPLETABLE", Arrays.asList(formId),
							true, userId, null);
					//}
				}
				//if it was unchecked or has been referenced to another batch, then removes the previous referenced/defined batch
				else {
					//not relevant anymore
					/*
					 * String sqlString = "update FG_S_INVITEMBATCH_PIVOT t set SAMPLE_ID = NULL where SAMPLE_ID = '"
					 * + formId + "'";
					 * formSaveDao.updateStructTable(sqlString, "FG_S_INVITEMBATCH_PIVOT",
					 * Arrays.asList("SAMPLE_ID"), "SAMPLE_ID", formId);
					 * List<String> formIds = generalDao.getListOfStringBySql(
					 * "select distinct t.formid from FG_S_SAMPLESELECT_ALL_V t,FG_S_INVITEMBATCH_ALL_V tt "
					 * + "where t.parentid = tt.FORMID and t.SAMPLE_ID = '" + formId + "' and tt.FORMID<>'"
					 * + elementValueMap.get("BATCH_ID") + "' and t.sessionid is null");
					 * for (String currentFormId : formIds) {//removes the current sample from the batches that were referenced to it
					 * sqlString = "update FG_S_SAMPLESELECT_PIVOT set SAMPLETABLE = regexp_replace(SAMPLETABLE,',"
					 * + formId + "|" + formId + ",'), DISABLED = regexp_replace(DISABLED,'," + formId + "|"
					 * + formId + ",') where formid = '" + currentFormId + "'";
					 * formSaveDao.updateStructTableByFormId(sqlString, "FG_S_SAMPLESELECT_PIVOT",
					 * Arrays.asList("SAMPLETABLE", "DISABLED"), currentFormId);
					 * }
					 */
					/*
					 * if (!elementValueMap.get("BATCH_ID").isEmpty()) {
					 * insertToSelectTable("SAMPLESELECT", elementValueMap.get("BATCH_ID"), "SAMPLETABLE",
					 * Arrays.asList(formId), true, userId);//add the sample to the selection in the referenced batch
					 * }
					 */
				}
			}

			//renumerating the preparations
			String sqlString = "select t.FORMID, dense_rank() over (partition by t.PARENTID order by t.FORMNUMBERID) as \"FORMNUMBERID\" from fg_s_preperationref_all_v t"
					+ " where t.FORMNUMBERID is not null and parentid is not null and parentid=" + formId
					+ " and t.ACTIVE = 1 order by parentid, t.FORMNUMBERID";
			List<Map<String, Object>> listOfMaps = generalDao.getListOfMapsBySql(sqlString);
			for (Map<String, Object> entry : listOfMaps) {//.get(i).entrySet()){
				sqlString = "update FG_S_PreperationRef_PIVOT set FORMNUMBERID = '"
						+ (entry.get("FORMNUMBERID") != null
								? String.format("%02d", Integer.parseInt(entry.get("FORMNUMBERID").toString())) : "")
						+ "' where FORMID = " + entry.get("FORMID");
				formSaveDao.updateStructTableByFormId(sqlString, "FG_S_PreperationRef_PIVOT",
						Arrays.asList("FORMNUMBERID"), entry.get("FORMID").toString());

			}

			/*
			 * if(Integer.parseInt(generalUtil.getNull(generalDao.selectSingleStringNoException("select count(*) from FG_I_SAMPLERESULTS_V where SAMPLE_ID="+formId),"0"))>1){//there is more than single result
			 * //main results that have the same material will be depleted except for the last one
			 * sqlString = "Select last_value(RESULT_ID) over (partition by RESULT_MATERIAL_ID) from FG_I_SELECTEDRESULTS_V where SAMPLE_ID = "+formId;
			 * List<String> results = generalDao.getListOfStringBySql(sqlString);
			 * sqlString = "Update FG_S_SAMPLE_PIVOT set SAMPLERESULTS = '"+results.toString().replaceAll("\\[|\\]", "").replaceAll("\\s*", "")+"' where FORMID = "+formId;
			 * formSaveDao.updateStructTableByFormId(sqlString, "FG_S_SAMPLE_PIVOT", Arrays.asList("SAMPLERESULTS"), formId);
			 * }
			 */
		}

		/********** InvItemBatch ******************/
		else if (formCode.equals("InvItemBatch")) {
			//delete empty rows in the component table
			 String	sql = "select t.formid \n"
						  + " from FG_S_MaterialComponent_PIVOT t \n"
						  + " where nvl(material_id,'0') = '0'  "
					      +" and parentid = '"+formId+"' and active = 1 and sessionid is null";
				
			List<String> componentId_listForDelete = generalDao.getListOfStringBySql(sql);
			for(String componentId: componentId_listForDelete)
			{
				formSaveDao.deleteStructTableByFormId("delete from FG_S_MaterialComponent_PIVOT t where t.formid = '"+componentId+"'","FG_S_MaterialComponent_PIVOT", componentId);
			}
			
			List<String> selectedSamples = generalDao
					.getListOfStringBySql("Select distinct SAMPLE_ID from FG_S_SAMPLESELECT_ALL_V where PARENTID = "
							+ formId + generalUtilFormState.getWherePartForTmpData("SAMPLESELECT", formId));
			String selectedDefinedSample = generalUtil.getNull(elementValueMap.get("SAMPLE_ID"));
			for (String sampleId : selectedSamples) {
				String batchdef = sampleId.equals(selectedDefinedSample) ? "1" : "0";
				//connect the sample list to the batch and define the batch if the sample was chosen
				formSaveDao.updateStructTableByFormId(
						"update FG_S_SAMPLE_PIVOT set batchName = '" + elementValueMap.get("formNumberId")
								+ "', BATCHDEFINITION = '" + batchdef + "', BATCH_ID = '" + formId
								+ "', productName = '"
								+ generalUtil.replaceDBUpdateVal(formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID,
										elementValueMap.get("INVITEMMATERIAL_ID"), "name"))
								+ "', productId ='" + elementValueMap.get("INVITEMMATERIAL_ID") + "' where FORMID = "
								+ sampleId,
						"FG_S_SAMPLE_PIVOT",
						Arrays.asList("batchName", "BATCH_ID", "productName", "productId", "BATCHDEFINITION"),
						sampleId);

				sql = "select distinct s.formid" + " from fg_s_sampleselect_all_v s" + ",fg_s_invitembatch_v b"
						+ " where s. SAMPLE_ID ='" + sampleId + "'" + " and s.parentid= b.formid" + " and b.formid <> '"
						+ formId + "'" + " and nvl(s.active,'1')='1'" + " and s.sessionid is null";
				List<String> smplslc = generalDao.getListOfStringBySql(sql);
				for (String currentFormId : smplslc) {
					String sqlString = "update FG_S_SAMPLESELECT_PIVOT set SAMPLETABLE = regexp_replace(SAMPLETABLE,',"
							+ sampleId + "|" + sampleId + ",|^" + sampleId
							+ "$'), DISABLED = regexp_replace(DISABLED,'," + sampleId + sampleId + "|" + sampleId
							+ ",|^" + sampleId + "$') where formid = '" + currentFormId + "'";//removes the current sample from the batches that are not relevant anymore
					formSaveDao.updateStructTableByFormId(sqlString, "FG_S_SAMPLESELECT_PIVOT",
							Arrays.asList("SAMPLETABLE", "DISABLED"), currentFormId);
				}

			}
				
			//if(!elementValueMap.get("SAMPLE_ID").equals(selectedDefinedSample)) {
			if (!generalUtil.getNull(elementValueMap.get("SAMPLE_ID")).isEmpty()) {
				//update related defined sample_id and the purity
				formSaveDao.updateStructTableByFormId(
						"update fg_s_invitembatch_pivot t" + " set "//SAMPLE_ID = '"+selectedDefinedSample+"',
								+ "PURITY = nvl((Select RESULT_VALUE from FG_I_SELECTEDRESULTS_V r"
								+ " where t.INVITEMMATERIAL_ID = r.RESULT_MATERIAL_ID" + " and r.SAMPLE_ID = '"
								+ selectedDefinedSample + "'" + " and RESULT_NAME = 'Assay'),t.PURITY)"
								+ " where formid = '" + formId + "'",
						"fg_s_invitembatch_pivot", Arrays.asList("PURITY"), formId);
				//removes the definition relation from the previous sample that defined this current batch
				formSaveDao.updateStructTable(
						"update FG_S_SAMPLE_PIVOT set BATCHDEFINITION = '0'" + "where BATCH_ID = '" + formId
								+ "' and BATCHDEFINITION = '1' and formid <> '" + selectedDefinedSample + "'",
						"FG_S_SAMPLE_PIVOT", Arrays.asList("BATCHDEFINITION"), "BATCH_ID", formId);

				
				//update the results in the component table
				String sqlString = "update fg_s_materialcomponent_pivot t\n"
						+ "set concentration = nvl(t.concentration,nvl((Select RESULT_VALUE from FG_I_SELECTEDRESULTS_V r "
						+ "where t.MATERIAL_ID = r.RESULT_MATERIAL_ID and r.SAMPLE_ID ='" + selectedDefinedSample
						+ "' and RESULT_NAME = 'Assay'),t.concentration))\n"
						+ ", approvalDate = nvl2(t.concentration,t.approvaldate,nvl((Select to_char(RESULT_DATE,'dd/MM/yyyy HH24:MI') from FG_I_SELECTEDRESULTS_V r "
						+ "where t.MATERIAL_ID = r.RESULT_MATERIAL_ID and r.SAMPLE_ID ='" + selectedDefinedSample
						+ "' and RESULT_NAME = 'Assay'),t.approvaldate))\n"
						+ " where parentid = '"+formId+"'";
				formSaveDao.updateStructTable(sqlString, "fg_s_materialcomponent_pivot",
						Arrays.asList("concentration","approvaldate"),"parentid", formId);
				
				/*//updates the material component concentration(gets the assay main result for each material row)
				String sqlString = "update fg_s_materialcomponent_pivot t\n"
						+ "set concentration = nvl((Select RESULT_VALUE from FG_I_SELECTEDRESULTS_V r "
						+ "where t.MATERIAL_ID = r.RESULT_MATERIAL_ID and r.SAMPLE_ID ='" + selectedDefinedSample
						+ "' and RESULT_NAME = 'Assay'),t.concentration)\n"
						+ ", approvalDate = nvl((Select to_char(RESULT_DATE,'dd/MM/yyyy HH24:MI') from FG_I_SELECTEDRESULTS_V r "
						+ "where t.MATERIAL_ID = r.RESULT_MATERIAL_ID and r.SAMPLE_ID ='" + selectedDefinedSample
						+ "' and RESULT_NAME = 'Assay'),t.approvaldate)\n"
						+ " where parentid = '"+formId+"'";
				formSaveDao.updateStructTable(sqlString, "fg_s_materialcomponent_pivot",
						Arrays.asList("concentration","approvaldate"),"parentid", formId);*/
				
				
				formSaveDao.updateStructTable(
						"update FG_S_INVITEMBATCH_PIVOT set SAMPLE_ID = NULL" + " where SAMPLE_ID = '"
								+ elementValueMap.get("SAMPLE_ID") + "' and formid <>'" + formId + "'",
						"FG_S_INVITEMBATCH_PIVOT", Arrays.asList("SAMPLE_ID"), "SAMPLE_ID",
						elementValueMap.get("SAMPLE_ID"));
			}
			commonFunc.onChangeInventoryEvent(formCode, formId, elementValueMap, userId, sbInfo);
		}

		/********************** SampleSelect *********************/
		/*
		 * if(formCode.equalsIgnoreCase("SampleSelect") && formDao.getFormCodeEntityBySeqId(elementValueMap.get("parentId")).equalsIgnoreCase("WorkUp")){
		 * List<String> selectedSamples = generalDao.getListOfStringBySql("Select SAMPLE_ID from FG_S_SAMPLESELECT_ALL_V where PARENTID = "+elementValueMap.get("parentId"));
		 * //updates workupstage in all samples to the current one
		 * for(String sampleId:selectedSamples){
		 * formSaveDao.updateStructTableByFormId("update FG_S_SAMPLE_PIVOT set workupStage = (select STAGE_ID from fg_s_workup_all_v where formid = "
		 * +elementValueMap.get("parentId")+") where formId = "+sampleId, "FG_S_SAMPLE_PIVOT", Arrays.asList("workupStage"), sampleId);
		 * }
		 * }
		 */

		/******************************************************** ExperimentSeriesMain *********************************************************/
		else if (formCode.equals("ExpSeriesMain")) {

			/*
			 * formSaveDao.updateSingleString(
			 * "update FG_SEQUENCE SET FORMCODE = 'ExperimentSeries' WHERE id = '" + formId + "'");
			 */
			formSaveDao.updateStructTableFormCode("ExperimentSeries", "ExperimentSeries", formId, true);

		}
		/***************************************************************** Experiment Series *****************************************************************/
		else if (formCode.equals("ExperimentSeries")) {
			//Validates that all the AI materials quantities >0 	
			integrationValidation.validate(ValidationCode.INVALID_AI_QUANTITY, formCode, formId, "", sbInfo);

		}

		/******************************************************** ExpSeriesCreation *********************************************************/

		else if (formCode.equals("ExpSeriesCreation")) {

			// TODO: Create experiments according to FDS - section 9

			int numOfExperiment = 1;
			int fromIndex = 0;
			int toIndex = 0;
			List<String> expIndexes = generalDao.getListOfStringBySql(
					"select t.EXPERIMENTINDEX from FG_S_FORMULATIONPROPREF_ALL_V t where t.parentid = '"
							+ elementValueMap.get("parentId")
							+ "' and t.ACTIVE = '1' and t.SESSIONID is null  order by t.EXPERIMENTINDEX asc");

			if (elementValueMap.get("rcreateExperiments").equals("Create All")) {
				toIndex = expIndexes.size();
				for (; fromIndex < toIndex; fromIndex++) {
					for (int i = 0; i < numOfExperiment; i++) {
						createExperimentFromSerias(elementValueMap, expIndexes.get(fromIndex), userId);

					}
				}
			} else if (elementValueMap.get("rcreateExperiments").equals("Create experiments for")) {
				numOfExperiment = Integer.valueOf(elementValueMap.get("numOfExperiment"));
				fromIndex = Integer.valueOf(formDao.getFromInfoLookup("FormulationPropRef", LookupType.ID,
						elementValueMap.get("FROMINDEX_ID"), "experimentindex"));
				toIndex = Integer.valueOf(formDao.getFromInfoLookup("FormulationPropRef", LookupType.ID,
						elementValueMap.get("TOINDEX_ID"), "experimentindex"));

				for (; fromIndex <= toIndex; fromIndex++) {
					if (expIndexes.contains(String.valueOf(fromIndex))) {
						for (int i = 0; i < numOfExperiment; i++) {
							createExperimentFromSerias(elementValueMap, String.valueOf(fromIndex), userId);
						}
					}

				}
			}
		}
		/******************************************************** FormulationPropRef *********************************************************/
		else if (formCode.equals("FormulationPropRef")) {

			// TODO:
			// 1) CALCULATION performed on each click on the calculator icon.(not here, written here just to remind)
			// 2) Complete mp and the batch field next to it.

		} else if (formCode.equals("FormulantRef")) {
			String sql = "delete from FG_FORMMONITOPARAM_DATA t where 1=1\r\n"
					+ "and t.parent_id in (select r.FORMULATIONPROPREF_ID from fg_s_FormulationPropRef_all_v r)\r\n"
					+ "and t.config_id not in (select i.MP_ID from fg_i_formulant_mp_all_v i)";
			formSaveDao.updateSingleStringInfoNoTryCatch(sql);

			//clean previous batch selection (in case of new form - in edit form change material is disabled)
			//			String rowCount = "";
			String materialId = elementValueMap.get("materialId");

			sql = "delete from fg_s_batchselect_pivot t \r\n" + "			where t.parentid = '" + formId + "' \r\n"
					+ "			and   t.formid not in (select t1.BATCHSELECT_ID from FG_S_BATCHSELECT_ALL_V t1 where t1.INVITEMMATERIAL_ID = '"
					+ materialId + "' and t1.PARENTID ='" + formId + "')";
			formSaveDao.updateSingleStringInfoNoTryCatch(sql);
		} else if (formCode.equals("User")) // kd 11/02/2018 fixed bug - Update Lastpassworddate when create user or change password
		{
			if (elementValueMap.get("password") != null) {
				//Update date
				DateFormat dateFormat = new SimpleDateFormat(generalUtil.getConversionDateFormat());
				Date date = new Date(); //current date for necessary cases
				try {
					formSaveDao.updateStructTableByFormId(
							"update FG_S_USER_PIVOT set lastpassworddate = '" + dateFormat.format(date)
									+ "' where FORMID = '" + formId + "'",
							"FG_S_USER_PIVOT", Arrays.asList("LASTPASSWORDDATE"), formId);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			//			if (elementValueMap.get("deleted") != null && elementValueMap.get("deleted").equals("1")) {
			//				formSaveDao.updateStructTableByFormId(
			//						"update FG_S_USER_PIVOT set ACTIVE = 0 where FORMID = '" + formId + "'",
			//						"FG_S_USER_PIVOT", Arrays.asList("ACTIVE"), formId);
			//			}
		} else if (formCode.equals("InvItemInstrument") || formCodeEntity.equals("InvItemMaterial")
				|| formCode.equals("InvItemColumn")) {
			// delete all trained users from training list in case of some
			// change in documents
			updateTrainedUsers(formId, formCode);
			// events for InvItemMaterial fromcode (chemical form code) ....
			if (formCode.equals("InvItemMaterial")) { 
				Double density = generalUtilCalc.getNormalNumber(elementValueMap.get("density"),
						elementValueMap.get("DENSITY_UOM_ID"));
				String normalDensity = density == null ? "" : String.valueOf(density);
				if (!normalDensity.equals(elementValueMap.get("lastNormalDensity"))) {
					commonFunc.onChangeInventoryEvent("InvItemMaterial", formId, elementValueMap, userId, sbInfo);
				}
				
				//Updating the molecule in the matrix
				if(updateMoleculeMatrixOnSave.equals("1")) {
					String struct = elementValueMap.get("structure");
					String x = elementValueMap.get("xCoordinateInMatrix");
					String y = elementValueMap.get("yCoordinateInMatrix");
					String changeFlag = elementValueInfATMap.get("chem_struct_change_flag");
					if(changeFlag != null && changeFlag.equals("1")) {
						generalUtilLogger.logWriter(LevelType.INFO,"Start preFormSaveEvent-updateMoleculeMatrix formCode=" + formCode + ", formId=" + formId,
								ActivitylogType.PerformanceJava, formId);
						chemMatrixTaskDao.updateMoleculeMatrix(formId, struct, x, y, userId);
					}
				}
			}
			if(formCode.equals("InvItemMaterialFr")||formCode.equals("InvItemMaterialPr")) {
				//delete empty rows in the component table
			 String	sql = "select t.formid \n"
						  + " from FG_S_MaterialComponent_PIVOT t \n"
						  + " where nvl(material_id,'0') = '0'  "
					      +" and parentid = '"+formId+"' and active = 1 and sessionid is null";
				
				List<String> componentId_listForDelete = generalDao.getListOfStringBySql(sql);
				for(String componentId: componentId_listForDelete)
				{
					formSaveDao.deleteStructTableByFormId("delete from FG_S_MaterialComponent_PIVOT t where t.formid = '"+componentId+"'","FG_S_MaterialComponent_PIVOT", componentId);
				}
				integrationValidation.validate(ValidationCode.CHECK_HASCOMPONENT,formCode,formId,elementValueMap,sbInfo);
			}
		} else if (formCode.equals("SampleLink")) { //"Taro develop"
			String linkexperimentid = generalUtil.getNull(elementValueInfATMap.get("linkExperimentId"));
			String linkstorage = generalUtil.getNull(elementValueInfATMap.get("linkStorage"));
			String linktimepoint = generalUtil.getNull(elementValueInfATMap.get("linkTimePoint"));
			//NOTE!!! the is in ExperStbResult is really stbResult id //TODO taro develop (2) consider to change it to have better clear code 
			String linktest = generalUtil.getNull(elementValueInfATMap.get("linkTest"));
			
			//set linktest with all Id's case it's empty
			if (!linktimepoint.equals("") && linktest.equals("")) {
				linktest = generalDao.getCSVBySql(
						"select EXPERSTBR_TEST_RESULT_ID from fg_i_sample_storage_tp_test_v t where t.PARENTID = '"
								+ linkexperimentid + "' and t.link_storage = '" + linkstorage + "' and t.link_tp = '"
								+ linktimepoint + "'",
						false);
			} 
			
			//delete result not linked (TODO find the linitation for this BL)
			String sql = "delete from fg_results t where t.sample_id = '" + formId + "' and instr('," + linktest
					+ ",',',' || t.resultref_id || ',') = 0";
			String deleteRows = formSaveDao.updateSingleStringInfo(sql); 
			System.out.println("SampleLink number of deleteRows=" + deleteRows);

			//insert new result by the test id list	- for result that are not exists in fg_results for this sample	
			String linktestArray[] = null;
			if (!generalUtil.getNull(linktest).equals("")) {
				linktestArray = linktest.split(",", -1);
			}
			
			//insert Result that arn't exists in fg_result to resultList
			sql = "select distinct T.RESULTREF_ID from FG_RESULTS t WHERE T.SAMPLE_ID = '" + formId + "' and t.experiment_id = '" + linkexperimentid + "' and t.result_test_name = 'Stability'";
			List<String> existsTestList = generalDao.getListOfStringBySql(sql);
			List<Result> resultList = new ArrayList<>();
			for (String ltest_ : linktestArray) {
				if(existsTestList == null || !existsTestList.contains(ltest_)) {
					Result r_ = new Result();
					r_.setExperimentId(linkexperimentid);
					r_.setSampleId(formId);
					r_.setResultrefId(ltest_);
					r_.setResultTestName("Stability");
					r_.setResultIsWebix("0");
					r_.setResultUomId(""); //TODO taro develop (1) set the result with accurate values
					r_.setUomType("");
					r_.setResultName(""); 
					r_.setResultValue("");
					r_.setResultMaterialName("");
					r_.setResultMaterialId("");
					r_.setResultRequestId("");
					r_.setResultType("");
					r_.setResultComment("");
					r_.setSelfTestId("");
					resultList.add(r_);
				}
			}
			commonFunc.insertToResults(resultList, formId, elementValueMap,false);
			
		 
			String sampleName = formDao.getFromInfoLookup("Sample", LookupType.ID,formId, "name");
			generalUtilFormState.setFormParam(stateKey, "ExperimentStb", "FILTER_LINK_SAMPLE_NAME",sampleName);
			
			//this sqls can be use in development for cleaning link results:
//			delete from FG_RESULTS t WHERE T.RESULT_TEST_NAME = 'Stability';
//			update fg_s_sample_pivot t set t.linkexperimentid = null, t.linkstorage = null, t.linktimepoint = null, t.linktest = null;
		} else if(formCode.equals("NumStepDesign")) {
			integrationValidation.validate(ValidationCode.MAINTENANCE_NUMSTEPDESIGN_SINGLE_ROW, formCode, formId, "", sbInfo);
		}
		else if(formCode.equals("RecipeFormulation")){
			//delete the compositions that have no material
			String sql = "select formid from fg_s_composition_v\n"
					+ " where active = 1 and parentid = '"+formId+"'"
					+ " and sessionid is null"
					+ " and nvl(invitemmaterial_id,'0') ='0'";
			List<String> compositionNoMaterial = generalDao.getListOfStringBySql(sql);
			for(String compositionId:compositionNoMaterial){
				sql ="delete from fg_s_composition_pivot where formid = '"+compositionId+"'";
				formSaveDao.deleteStructTable(sql,"fg_s_composition_pivot" , "formid", compositionId);
			}
			
			String recipeStatus = formDao.getFromInfoLookup("RECIPESTATUS", LookupType.ID, elementValueMap.get("STATUS_ID"), "name");
			if(!recipeStatus.endsWith("Planned")){
				//check that filler exist
				integrationValidation.validate(ValidationCode.CHECK_COMPOSITION_HAS_FILLER, formCode, formId, "", new StringBuilder());
			}
			
			//update the recipe in the experiment if the recipe was created from an experiment
			String origin = elementValueMap.get("origin");
			String experimentId = elementValueMap.get("EXPERIMENT_ID");
			if(origin.equals("Experiment") && isNew.equals("1")){
				sql = "update fg_s_experiment_pivot\n"
						+ "set RECIPEFORMULATION_ID = '"+formId+"'\n"
						+ "where formid = '"+experimentId+"'";
				
				formSaveDao.updateStructTableByFormId(sql, "fg_s_experiment_pivot", Arrays.asList("RECIPEFORMULATION_ID"), experimentId);
				formDao.insertToSelectTable("experimentselect", formId, "EXPERIMENT_ID", Arrays.asList(experimentId), true, userId, null);
			}
			
			//insert the recipe to the selected batches
			sql = "select distinct batch_id from fg_s_batchselect_v\n"
				+ " where parentid = '"+formId+"'\n"
				+ " and active = 1"
				+ " and sessionid is null";
			String batchCsv = generalUtil.getNull(generalDao.selectSingleStringNoException(sql));
			String[] batchArr = batchCsv.split(",");
			for(int i=0;i<batchArr.length;i++){
				String batch_id = batchArr[i];
				sql = "update fg_s_invitembatch_pivot\n"
					+ " set recipeformulation_id = '"+formId+"'"
					+ " where formid = '"+batch_id+"'";
				formSaveDao.updateStructTableByFormId(sql, "fg_s_invitembatch_pivot", Arrays.asList("recipeformulation_id"), batch_id);
			}
			//remove the current recipe from the batches that are not in the batches table
			sql = "select formid from fg_s_invitembatch_v\n"
				+ " where recipeformulation_id = '"+formId+"'\n"
				+ " and instr(','||'"+batchCsv+"'||',',','||formid||',')<=0";
			List<String> batchListtoDisconnect = generalDao.getListOfStringBySql(sql);
			for(String batch_id:batchListtoDisconnect){
				sql = "update fg_s_invitembatch_pivot\n"
					+ " set recipeformulation_id = null\n"
					+ " where formid = '"+batch_id+"'";
				formSaveDao.updateStructTableByFormId(sql, "fg_s_invitembatch_pivot", Arrays.asList("recipeformulation_id"), batch_id);
			}
		}
		else if(formCode.equals("ExperimentGroup")) {
		    String projectId=generalDao.selectSingleString("select t.project_id from fg_s_experiment_pivot t where t.FORMID="+elementValueMap.get("parentId"));
			formDao.insertToSelectTable("ExpGroupSelect", projectId, "GROUP_ID",Arrays.asList(formId) , false, userId, null);//task 17852
		}
		//update active to be 1 in cloned material
		String table = "FG_S_" + formCodeEntity + "_PIVOT";
		String sql_ = String.format(" update %1$s set ACTIVE = '1'  where FORMID =  '%2$s' and active = '-%2$s'", table,
				formId);
		formSaveDao.updateSingleStringInfoNoTryCatch(sql_);
		//generalUtilNotificationEvent.exeNotificationEvent(formId, formCodeEntity);

		//Handle mol files (for documents under all main forms)
		convertMolFilesDocument(formId, formCode);
		return auditTrailChangeType;
	}
	
	@Override
	public ActionBean doSaveOnException(Exception e, String formId, String formCode) {
		return commonFunc.doSaveOnException(e,formId,formCode);
	}
	
	@Override
	public String doRemove(String formCode, String formId, String userId) { 
		return commonFunc.doRemove(formCode,formId,userId);
	}

	private String getMCWcode(String nextFormNumberId, String projectId) {
		String mcwCode = "";
		Map<String, String> projectMapInfo = formDao.getFromInfoLookupAll("Project", LookupType.ID, projectId);
		if (projectMapInfo != null && projectMapInfo.size() > 0) {
			if (projectMapInfo.get("PROJECTTYPENAME").equals("Formulation")) {
				if (projectMapInfo.get("SITENAME").equals("Agan")) {
					mcwCode = "AG" + nextFormNumberId.replace(idDelimiter, "");
				} else if (projectMapInfo.get("SITENAME").equals("Makhteshim"))//Makhteshim 
				{
					mcwCode = "MCW" + nextFormNumberId.replace(idDelimiter, "");
				}
			}
		}
		return mcwCode;
	}

	private void setStartDate(Map<String, String> elementValueMap, String formId) {
		String statusName = formDao.getFromInfoLookup("STATUS", LookupType.ID, elementValueMap.get("STATUS_ID"),
				"name");
		if ((elementValueMap.get("startDate") != null) && (elementValueMap.get("STATUS_ID") != null)) {
			//if((elementValueMap.get("startDate").equals("")) && (elementValueMap.get("STATUS_ID").equals("886"))){
			if ((elementValueMap.get("startDate").equals("")) && (statusName.equals("Active"))) {
				DateFormat dateFormat = new SimpleDateFormat(generalUtil.getConversionDateFormat());
				Date date = new Date();
				generalUtilLogger.logWriter(LevelType.DEBUG,
						generalUtil.mapToString("set start date - elementValueMap before event:", elementValueMap),
						ActivitylogType.SaveEvent, formId);
				elementValueMap.put("startDate", dateFormat.format(date));
			}
		}
	}

	/**
	 * updates In Use/Depleted according to the quantity in the batch
	 * 
	 * @param formId
	 *            the formId of the updated batch
	 */
	private void updateDepletionBatch(Map<String, String> elementValueMap, String formId) {
		if (elementValueMap != null) {
			if (elementValueMap.get("quantity").equals("0")) {
				generalUtilLogger.logWriter(LevelType.DEBUG,
						generalUtil.mapToString("update inUseDepleted to be depleted - elementValueMap before event:",
								elementValueMap),
						ActivitylogType.SaveEvent, formId);
				elementValueMap.put("inUseDepleted", "Depleted");
			}
		} else {//fired by clone or split
			String sql_ = "update FG_S_INVITEMBATCH_PIVOT set INUSEDEPLETED = case when QUANTITY = 0 then 'Depleted' else INUSEDEPLETED end where FORMID = '"
					+ formId + "'";
			formSaveDao.updateStructTableByFormId(sql_, "FG_S_INVITEMBATCH_PIVOT", Arrays.asList("INUSEDEPLETED"),
					formId);
		}
	}

	private void setPrevCalibrationDate(Map<String, String> elementValueMap, String pivotTable, String formId,
			String isNew) {
		//String prevCalibrationDate = generalUtil.getEmpty(elementValueMap.get("prevCalibDate"), generalDao.selectSingleString("select max(CALIBRATIONDATE) from "+pivotTable+ " where INVITEMINSTRUMENT_ID = '"+elementValueMap.get("INVITEMINSTRUMENT_ID")+"'"));
		String prevCalibrationDate = generalUtil.getEmpty(elementValueMap.get("prevCalibDate"),
				generalDao.selectSingleString("select CALIBRATIONDATE from " + "(select * from " + pivotTable
						+ " where INVITEMINSTRUMENT_ID = '" + elementValueMap.get("INVITEMINSTRUMENT_ID")
						+ "' and formid <> '" + formId + "'"
						+ " and trunc(TO_DATE(CALIBRATIONDATE, 'DD/MM/YYYY')) <  trunc" + "("
						+ (isNew.equals("1") ? "sysdate"
								: "(select max(TO_DATE(CALIBRATIONDATE, '" + generalUtil.getConversionDateFormat()
										+ "'))" + " from fg_s_invitemcalibration_pivot"
										+ " where INVITEMINSTRUMENT_ID = '"
										+ elementValueMap.get("INVITEMINSTRUMENT_ID")
										+ "' and trunc(TO_DATE(CALIBRATIONDATE, '"
										+ generalUtil.getConversionDateFormat() + "'))<= trunc(sysdate))")
						+ ")" + " order by TO_DATE(CALIBRATIONDATE, '" + generalUtil.getConversionDateFormat()
						+ "') desc)" + " where rownum = 1"));
		if (prevCalibrationDate != null) {
			generalUtilLogger.logWriter(LevelType.DEBUG, generalUtil
					.mapToString("set previous calibration date - elementValueMap before event:", elementValueMap),
					ActivitylogType.SaveEvent, formId);
			elementValueMap.put("prevCalibDate", prevCalibrationDate);
		}
	}

	private void updateStatus(Map<String, String> elementValueMap) {
		if (!isStatusCancelled(elementValueMap)) {
			String instrumentId = elementValueMap.get("INVITEMINSTRUMENT_ID");
			if (isCloseBreakdownChecked(elementValueMap)) {//in case of Done status and close breakdown is checked
				String statusId = formDao.getFromInfoLookup("INSTRUMENTSTATUS", LookupType.NAME, "Active", "id");
				//formSaveDao.updateSingleString(String.format(" update FG_S_INVITEMINSTRUMENT_PIVOT set STATUS_ID= %1$s where formId =  %2$s",statusId,instrumentId));//change the instrument status to active
				formSaveDao.updateStructTableByFormId(
						String.format(
								" update FG_S_INVITEMINSTRUMENT_PIVOT t set laststatusname = (select instrumentstatusname from fg_s_instrumentstatus_v where formid = t.STATUS_ID), STATUS_ID= %1$s where formId =  %2$s",
								statusId, instrumentId),
						"FG_S_INVITEMINSTRUMENT_PIVOT", Arrays.asList("STATUS_ID", "laststatusname", "activationDate"),
						instrumentId);
			} else if (isNewBreakdownReported(elementValueMap)) {
				if (!instrumentId.isEmpty()) {
					String statusId = formDao.getFromInfoLookup("INSTRUMENTSTATUS", LookupType.NAME, "Malfunction",
							"id");
					formSaveDao.updateStructTableByFormId(
							String.format(
									" update FG_S_INVITEMINSTRUMENT_PIVOT t set laststatusname = (select instrumentstatusname from fg_s_instrumentstatus_v where formid = t.STATUS_ID), STATUS_ID= %1$s  where formId =  %2$s",
									statusId, instrumentId),
							"FG_S_INVITEMINSTRUMENT_PIVOT", Arrays.asList("STATUS_ID", "laststatusname"), instrumentId);//change the instrument status to mulfunction
				}
			}
		}
	}

	private boolean isStatusCancelled(Map<String, String> elementValueMap) {
		String statusId = formDao.getFromInfoLookup("MAINTCALIBSTATUS", LookupType.NAME, "Cancelled", "id");
		if (elementValueMap.get("STATUS_ID").equals(statusId)) {
			return true;
		}
		return false;
	}

	private boolean isNewBreakdownReported(Map<String, String> elementValueMap) {
		String maintenanceTypeId = formDao.getFromInfoLookup("MAINTENANCETYPE", LookupType.NAME, "Breakdown", "id");
		if (elementValueMap.get("MAINTENANCETYPE_ID").equals(maintenanceTypeId)) {
			return true;
		}
		return false;
	}

	private boolean isCloseBreakdownChecked(Map<String, String> elementValueMap) {
		//		String maintenanceTypeId = formDao.getFromInfoLookup("MAINTENANCETYPE", LookupType.NAME, "Breakdown", "id");
		String statusId = formDao.getFromInfoLookup("MAINTCALIBSTATUS", LookupType.NAME, "Done", "id");
		if (elementValueMap.get("closeBreakdown").equalsIgnoreCase("1")
				&& elementValueMap.get("STATUS_ID").equals(statusId)) {
			return true;
		}
		return false;
	}

	/**
	 * deletes all trained users from <formId> form if document has been changed
	 * 
	 * @param formId
	 */
	private void updateTrainedUsers(String formId, String formCode) {
		if (isDocumentChanged(formId, formCode)) {//all trained users should be deleted
			String table = "FG_S_TRAINING_PIVOT";
			//			formSaveDao.updateSingleString(String.format("delete from %1$s t where t.PARENTID = %2$s",table,formId));
			formSaveDao.deleteStructTable(String.format("delete from %1$s t where t.PARENTID = %2$s", table, formId),
					table, "PARENTID", formId);
			//update document flag 
			String sql = "update fg_s_document_pivot set contentChange = '0' where parentid = '" + formId + "'";
			formSaveDao.updateStructTable(sql, "fg_s_document_pivot", Arrays.asList("contentChange"), "parentid",
					formId);
		}
	}

	private void preFormSaveStepStatusChange(String formCode, String formId, String lastStatusName, String currentStatusName,
			Map<String, String> elementValueMap, StringBuilder sbInfo) throws Exception {
		DateFormat dateFormat = new SimpleDateFormat(generalUtil.getConversionDateFormat());
		Date date = new Date();
		//		String userId = generalUtil.getSessionUserId();
		//		String user = formDao.getFromInfoLookup("user", LookupType.ID, userId, "name");
		if (currentStatusName.equals("Active") && !lastStatusName.equals("Active")) {//status has been changed to active
			String protocolTypeName = formDao.getFromInfoLookup("Experiment", LookupType.ID, elementValueMap.get("EXPERIMENT_ID"), "PROTOCOLTYPENAME");
			if(protocolTypeName.equals("Continuous Process")){
				integrationValidation.validate(ValidationCode.VALIDATE_RUN_TURNS_ACTIVE, formCode, formId, elementValueMap.get("preparation_run"), sbInfo);
			}
			generalUtilLogger.logWriter(LevelType.DEBUG,
					generalUtil.mapToString("set actual start date - elementValueMap before event:", elementValueMap),
					ActivitylogType.SaveEvent, formId);
			elementValueMap.put("actualStartDate", dateFormat.format(date));

			List<String> descendingWorkups = generalDao.getListOfStringBySql("select distinct workup_id"
					+ " from fg_s_workup_v t," + " fg_s_workupstatus_v s," + " fg_s_action_v a"
					+ " where t.action_id = a.action_id" + " and t.status_id = s.workupstatus_id"
					+ " and s.workupstatusname = 'Planned'" + " and a.step_id = '" + formId + "'");
			String activeStatus = formDao.getFromInfoLookup("WORKUPSTATUS", LookupType.NAME, "Active", "ID");
			for (String workup_id : descendingWorkups) {
				formSaveDao.updateStructTableByFormId("update fg_s_workup_pivot" + " set status_id = '" + activeStatus
						+ "' where formid = '" + workup_id + "'", "fg_s_workup_pivot", Arrays.asList("status_id"),
						workup_id);
			}

		} else if (currentStatusName.equals("Finished") && !lastStatusName.equals("Finished")
				|| currentStatusName.equals("Cancelled") && !lastStatusName.equals("Cancelled")) {//status has been changed to Finished or Cancelled
			if (currentStatusName.equals("Finished")) {
				generalUtilLogger.logWriter(LevelType.DEBUG,
						generalUtil.mapToString("set finish date - elementValueMap before event:", elementValueMap),
						ActivitylogType.SaveEvent, formId);
				elementValueMap.put("finishDate", dateFormat.format(date));
			}
			//step not save on finished status if not all mandetory fields in self test under it were  filled
			if (formCode.equals("StepFr") && currentStatusName.equals("Finished") && !lastStatusName.equals("Finished")) {
				integrationValidation.validate(ValidationCode.CHECK_SELEFTEST_VALIDATION, "", formId, "", sbInfo);//TODO: update the validation to display the invalid selftests or fields
			}
		}
		//elementValueMap.put("LASTSTATUS_ID", elementValueMap.get("STATUS_ID"));//update last status
	}

	private int preFormSaveExperiment(String formCode, String formId, Map<String, String> elementValueMap,
			String userId, String pivotTable) { //, boolean isStructTable
		String currentVersion = "";
		DateFormat dateFormat = new SimpleDateFormat(generalUtil.getConversionDateFormat());
		String dateTime = generalDao.selectSingleString("select TO_CHAR(sysdate,'dd/MM/yyyy HH24:MI') from dual");
		Date date = new Date(); //current date for necessary cases

		String lastStatus_id = formDao.getFromInfoLookup("Experiment", LookupType.ID, formId, "STATUS_ID");
		String lastStatusName = formDao.getFromInfoLookup("experimentstatus", LookupType.ID, lastStatus_id, "name");
		elementValueMap.put("LASTSTATUS_ID", lastStatus_id);
		if (elementValueMap.get("LASTSTATUS_ID") == null || elementValueMap.get("LASTSTATUS_ID").isEmpty()) {//it's the first new save
			//first version
			currentVersion = "01";
			String protocolTypeName = formDao.getFromInfoLookup("PROTOCOLTYPE", LookupType.ID, elementValueMap.get("PROTOCOLTYPE_ID"), "name") ;
			String experimentTypeName = formDao.getFromInfoLookup("EXPERIMENTTYPE", LookupType.ID, elementValueMap.get("EXPERIMENTTYPE_ID"), "name") ;
			if(protocolTypeName.equals("Analytical") && experimentTypeName.equals("General")) {
				String firstStatusId = formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.NAME, "Active", "id");
				generalUtilLogger.logWriter(LevelType.DEBUG,
						generalUtil.mapToString("update status - elementValueMap before event:", elementValueMap),
						ActivitylogType.SaveEvent, formId);
				elementValueMap.put("STATUS_ID", firstStatusId);
			} else {
				//first status
				List<String> wfNames = generalUtil.getWfAvailableState("experiment_status.json", WorkflowType.STATUS, "");
				if (wfNames != null && wfNames.size() > 0) {
					String firstStatusId = formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.NAME, wfNames.get(0),
							"id");
					//				elementValueMap.put("LASTSTATUS_ID", firstStatusId);
					generalUtilLogger.logWriter(LevelType.DEBUG,
							generalUtil.mapToString("update status - elementValueMap before event:", elementValueMap),
							ActivitylogType.SaveEvent, formId);
					elementValueMap.put("STATUS_ID", firstStatusId);
				}
			}
		} else {
			/*String lastStatusName = formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.ID,
					elementValueMap.get("LASTSTATUS_ID"), "name");*/
			//String lastStatusName = formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.ID,
			//	lastStatusId, "name");//ta 0602 fixed bug (task 24005)

			String currentStatusName = formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.ID,
					elementValueMap.get("STATUS_ID"), "name");
			if (lastStatusName.equals("Approved") && currentStatusName.equals("Finished")) {//an approved experiment is reopened 
				currentVersion = getNextVersion("Experiment", formId);
			} else if (lastStatusName.equals("Approved") && currentStatusName.equals("Active")) {//experiment is returned from approved to active status
				currentVersion = getNextVersion("Experiment", formId);
			} else if (currentStatusName.equals("Completed") && !lastStatusName.equals("Completed")) {//status has been changed to completed
				generalUtilLogger.logWriter(LevelType.DEBUG,
						generalUtil.mapToString("set completion date - elementValueMap before event:", elementValueMap),
						ActivitylogType.SaveEvent, formId);
				elementValueMap.put("completionDate", dateFormat.format(date));
				elementValueMap.put("completionTimeStamp", dateTime);
				if (lastStatusName.equals("Finished")) {
					elementValueMap.put("reasonForChange", "");
				}
			} else if (currentStatusName.equals("Approved") && !lastStatusName.equals("Approved")) {//status has been changed to approved
				generalUtilLogger.logWriter(LevelType.DEBUG,
						generalUtil.mapToString("set approval date - elementValueMap before event:", elementValueMap),
						ActivitylogType.SaveEvent, formId);
				elementValueMap.put("approvalDate", dateFormat.format(date));
				elementValueMap.put("approvalTimeStamp", dateTime);
			} else if (currentStatusName.equals("Active") && !lastStatusName.equals("Active")) {//status has been changed to active
				generalUtilLogger.logWriter(
						LevelType.DEBUG, generalUtil
								.mapToString("set actual start date - elementValueMap before event:", elementValueMap),
						ActivitylogType.SaveEvent, formId);
				if (elementValueMap.get("actualStartDate").isEmpty()) {
					elementValueMap.put("actualStartDate", dateFormat.format(date));
					elementValueMap.put("actualStartTimeStamp", dateTime);
				}
			}

			/*if (elementValueMap.get("LASTSTATUS_ID") != null
					&& !elementValueMap.get("LASTSTATUS_ID").equals(lastStatusId)) {
				elementValueMap.put("LASTSTATUS_ID", lastStatusId);
			}*/
		}
		//set last modification date
		generalUtilLogger.logWriter(LevelType.DEBUG,
				generalUtil.mapToString("set last modification date - elementValueMap before event:", elementValueMap),
				ActivitylogType.SaveEvent, formId);
		elementValueMap.put("lastModifDate", dateFormat.format(date));
		//elementValueMap.put("LASTSTATUS_ID", elementValueMap.get("STATUS_ID"));
		//set experiment Version
		if (!currentVersion.isEmpty()) {
			generalUtilLogger.logWriter(LevelType.DEBUG,
					generalUtil.mapToString("set experiment version - elementValueMap before event:", elementValueMap),
					ActivitylogType.SaveEvent, formId);
			elementValueMap.put("experimentVersion", currentVersion);
			String sql_ = "update fg_s_step_pivot set experimentversion = '" + currentVersion
					+ "' where experiment_id='" + formId + "' ";
			formSaveDao.updateStructTable(sql_, "fg_s_step_pivot", Arrays.asList("experimentversion"), "experiment_id",
					formId);
		}

		//set formNumberId- Experiment number
		String nextFormNumberId = formIdCalc.getNextFormNumberIdByFormId(formCode, formId, elementValueMap, userId,
				pivotTable);
		if (nextFormNumberId == null) {
			return -1;
		}
		int auditTrailChangeType = !nextFormNumberId.isEmpty() ? 1 : 0;

		if (!nextFormNumberId.isEmpty()) {
			generalUtilLogger.logWriter(LevelType.DEBUG,
					generalUtil.mapToString("set experiment name - elementValueMap before event:", elementValueMap),
					ActivitylogType.SaveEvent, formId);
			elementValueMap.put("formNumberId", nextFormNumberId);
			elementValueMap.put("experimentName", nextFormNumberId);//necessary for future use in the child forms in DDL-id/val
		}

		// yp fix bug 5836 - this code wull remove the EXPERIMENTVIEW_ID is we will not put it in the screen (so I delete it - the reason its here is to support old data)
		//add default EXPERIMENTVIEW_ID
		//		try {
		//			if (elementValueMap.get("EXPERIMENTVIEW_ID") == null || elementValueMap.get("EXPERIMENTVIEW_ID").equals("")) {
		//				String defaultViewId = formDao.getFromInfoLookup("EXPERIMENTTYPE", LookupType.ID,
		//						elementValueMap.get("EXPERIMENTTYPE_ID"), "DEFAULT_VIEW_ID");
		//				elementValueMap.put("EXPERIMENTVIEW_ID",defaultViewId);
		//			}
		//		} catch (Exception e) {
		//			// do nothing
		//		}

		return auditTrailChangeType;
	}

	private void cloneExperimentByMain(/*long stateKey,*/ Map<String, String> elementValueMap, String formCode,
			String formId, String userId, String cloneExperimentId) {
		//String parentId = formId;
		//String cloneExperimentId = formSaveDao.getStructFormId("EXPERIMENT");
		String protocolType = formDao.getFromInfoLookup("ProtocolType", LookupType.ID,
				elementValueMap.get("PROTOCOLTYPE_ID"), "name");
		cloneExperiment.cloneExperiment(formId, protocolType, cloneExperimentId, userId, CloneType.GENERAL,null);

		/*
		 * formSaveDao.updateSingleString(
		 * "update FG_SEQUENCE SET FORMCODE = '" + formCode + "' WHERE id = '" + cloneExperimentId + "'");
		 */
		//formSaveDao.updateStructTableFormCode("Experiment", formCode, cloneExperimentId);
		String expSeries = "", originformulantpropref = "";
		if (formCode.equals("ExperimentFor")) {
			Map<String, String> projectMap = generalDao.sqlToHashMap(
					"Select project_id, subproject_id,EXPERIMENTSERIES,originformulantpropref from fg_s_experiment_v where formid = '"
							+ formId + "'");
			if (projectMap.get("PROJECT_ID").equals(elementValueMap.get("PROJECT_ID"))
					&& projectMap.get("SUBPROJECT_ID").equals(elementValueMap.get("SUBPROJECT_ID"))) {
				expSeries = generalUtil.getNull(projectMap.get("EXPERIMENTSERIES"));
				originformulantpropref = generalUtil.getNull(projectMap.get("ORIGINFORMULANTPROPREF"));
			}
		}

		/*String sql = String.format(
				"update fg_s_experiment_pivot t set t.project_id = '%1$s', t.subproject_id = '%2$s', t.subsubproject_id = '%3$s',t.experimentseries='%8$s',originformulantpropref='%9$s', t.aim = '%4$s' ,t.description = '%5$s', t.estimatedstartdate = TO_CHAR(TO_DATE('%6$s', '"
						+ generalUtil.getConversionDateFormat() + "'), '" + generalUtil.getConversionDateFormat()
						+ "')  where t.formid = '%7$s'",
				elementValueMap.get("PROJECT_ID"), elementValueMap.get("SUBPROJECT_ID"),
				elementValueMap.get("SUBSUBPROJECT_ID"), elementValueMap.get("aim"), elementValueMap.get("description"),
				elementValueMap.get("estimatedStartDate"), cloneExperimentId,expSeries,originformulantpropref);
		formSaveDao.updateStructTableByFormId(sql, "fg_s_experiment_pivot", Arrays.asList("PROJECT_ID", "SUBPROJECT_ID",
				"SUBSUBPROJECT_ID", "AIM", "DESCRIPTION", "ESTIMATEDSTARTDATE","EXPERIMENTSERIES"), cloneExperimentId);*/

		Map<String, String> experimentValueMap = generalDao.sqlToHashMap(
				String.format("select * from FG_S_EXPERIMENT_PIVOT where formid = '%1$s'", cloneExperimentId));
		experimentValueMap.put("SUBPROJECT_ID", elementValueMap.get("SUBPROJECT_ID"));
		String nextFormNumberId = formIdCalc.getNextId(experimentValueMap, "Experiment", cloneExperimentId, userId,
				"FG_S_EXPERIMENT_PIVOT");
		if (!nextFormNumberId.isEmpty()) {
			generalUtilLogger.logWriter(LevelType.DEBUG,
					generalUtil.mapToString("set experiment name - elementValueMap before event:", experimentValueMap),
					ActivitylogType.SaveEvent, cloneExperimentId);

			String sql = String.format(
					"update  FG_S_EXPERIMENT_PIVOT t set formNumberId = '%1$s', experimentName = '%1$s',lastModifDate = TO_CHAR(sysdate,'"
							+ generalUtil.getConversionDateFormat() + "') ,creationdatetime = TO_CHAR(sysdate,'"
							+ generalUtil.getConversionDateFormat() + "')"
							+ ",experimentVersion = '01', Active = 1,t.project_id = '%3$s', t.subproject_id = '%4$s', t.subsubproject_id = '%5$s',t.experimentseries='%9$s',originformulantpropref='%10$s', t.aim = '%6$s' ,t.description = '%7$s', t.estimatedstartdate = TO_CHAR(TO_DATE('%8$s', '"
							+ generalUtil.getConversionDateFormat() + "'), '" + generalUtil.getConversionDateFormat()
							+ "'), timestamp = sysdate, change_by='"+userId+"' where formid = %2$s",
					nextFormNumberId, cloneExperimentId, elementValueMap.get("PROJECT_ID"),
					elementValueMap.get("SUBPROJECT_ID"), elementValueMap.get("SUBSUBPROJECT_ID"),
					elementValueMap.get("aim"), elementValueMap.get("description"),
					elementValueMap.get("estimatedStartDate"), expSeries, originformulantpropref);
			formSaveDao.updateStructTableByFormId(sql, "FG_S_EXPERIMENT_PIVOT",
					Arrays.asList("FORMNUMBERID", "experimentName", "estimatedStartDate", "lastModifDate",
							"creationdatetime", "Active", "PROJECT_ID", "SUBPROJECT_ID", "SUBSUBPROJECT_ID", "AIM",
							"DESCRIPTION", "ESTIMATEDSTARTDATE", "EXPERIMENTSERIES","TIMESTAMP","CHANGE_BY"),
					cloneExperimentId);
		}

		List<String> actionIdList = generalDao.getListOfStringBySql(
				"SELECT formid from FG_S_ACTION_PIVOT t where experiment_id = '" + cloneExperimentId + "'");
		for (String actionId : actionIdList) {
			formSaveElementDao.addMonitorParam(actionId, "Action");//insertParamMonitoring
		}

		String usersCrewId = formSaveDao.getStructFormId("UsersCrew");
		formSaveDao.insertStructTableByFormId(
				"insert into FG_S_USERSCREW_PIVOT (FORMID,TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMCODE,PARENTID,USER_ID,CREATED_BY,CREATION_DATE) "
						+ "values ('" + usersCrewId + "', sysdate, " + userId + ", null,1,'UsersCrew','"
						+ cloneExperimentId + "','" + userId + "','" + userId + "',SYSDATE)",
				"FG_S_USERSCREW_PIVOT", usersCrewId);

		/*
		 * parentId = elementValueMap.get("SUBPROJECT_ID");
		 * String url = "init.request?stateKey=" + stateKey + "&formCode=" + formCode + "&formId="
		 * + cloneExperimentId + "&userId=" + userId + "&avoidBreadCrumb=1" //TODO innerinitid check
		 * + "&PARENT_ID=" + parentId;
		 * generalUtilFormState.pushIntoBackNavigationStack(stateKey, cloneExperimentId, formCode, "", "", url, userId);
		 */

		//return cloneExperimentId;
	}

	private int preFormSaveSample(String formCode, String formId, Map<String, String> elementValueMap, String userId,
			String pivotTable, StringBuilder sbInfo) throws Exception {
		if (formCode.equals("SampleMain")) {
			//updates originId
			String originId = elementValueMap.get("parentId");
			String formEntityCode = formDao.getFormCodeEntityBySeqId("", originId);
			if (formEntityCode.equals("Experiment")) {
				elementValueMap.put("originExperimentId", originId);
			} else if (formEntityCode.equals("Step")) {
				elementValueMap.put("originStepId", originId);
			} else if (formEntityCode.equals("Action")) {
				elementValueMap.put("originActionId", originId);
			} else if (formEntityCode.equals("SelfTest")) {
				elementValueMap.put("originSelfTestId", originId);
			} else if (formEntityCode.equals("Workup")) {
				elementValueMap.put("originWorkupId", originId);
			}
			//update product values
			if (!elementValueMap.get("PRODUCT_ID").isEmpty()) {
				//elementValueMap.put("productId", elementValueMap.get("PRODUCT_ID"));
				//String productName = formDao.getFromInfoLookup("MaterialRef", LookupType.ID, elementValueMap.get("PRODUCT_ID"), "Name");
				//elementValueMap.put("productName", productName);
				elementValueMap.put("is_batchDef_disabled", "1");//product is defined then batch is null. so the checkbox should be disabled
			}
			if (formDao.getFormCodeBySeqId(elementValueMap.get("parentId")).equals("InvItemBatch")
					&& !elementValueMap.get("BATCH_ID").isEmpty()) {
				String materialId = formDao.getFromInfoLookup("InvItemBatch", LookupType.ID,
						elementValueMap.get("BATCH_ID"), "INVITEMMATERIAL_ID");
				String materialName = formDao.getFromInfoLookup("InvItemMaterial", LookupType.ID, materialId, "name");
				elementValueMap.put("productId", materialId);
				//String productName = formDao.getFromInfoLookup("MaterialRef", LookupType.ID, elementValueMap.get("PRODUCT_ID"), "Name");
				elementValueMap.put("productName", materialName);
			}
			if (formEntityCode.equals("Action")) {//task 26720
				String comments = generalUtil.getNull(elementValueMap.get("commentsForCoa"));
				String richtextContent;
				richtextContent = generalDao.selectSingleStringNoException("select fg_get_richtext_display('"+comments+"') from dual");//get the text only from fg_get richtext
						//	generalDao.getSingleStringFromClob("select t.file_content_text from fg_richtext t where t.file_id = '" + comments + "'");
				String desc = generalUtil.getNull(elementValueMap.get("sampleDesc"));
				String commentsId = formSaveDao.getStructFileId("SampleMain.commentsForCoa");
				if(generalUtil.getNull(richtextContent).isEmpty() && !desc.isEmpty()) {
					uploadFileDao.saveRichText(commentsId, desc, desc,
							true);
					elementValueMap.put("commentsForCoa",commentsId);	
				}
			}
		} else if (formCode.equals("Sample")) {
			if (elementValueMap.get("batchDefinition").equals("1")) {
				integrationValidation.validate(ValidationCode.INVALID_BATCHDEFINITION_DUPLICATION, formCode, formId,
						generalUtil.getEmpty(elementValueMap.get("BATCH_ID"), ""), sbInfo);
			}

			String sampleResCsv = elementValueMap.get("sampleResults");
			if (!sampleResCsv.isEmpty()) {
				//gets the last selected result that the combination of material&result type appears more than once(user selection-not automatically by default-see sample initiation)
				String sql = "Select LISTAGG(RESULT_ID,',') WITHIN GROUP (order by 1)" + " from "
						+ "(select distinct last_value(RESULT_ID) over (partition by sample_id,invitemmaterial_id,result_name) as  RESULT_ID from"
						//gets the sample results that the combination of material&result type are similar, means it appears more than once
						+ "(select distinct  RESULT_ID,sample_id,invitemmaterial_id,result_name from"
						+ "(select distinct result_id,sample_id,invitemmaterial_id,result_name,"
						+ "count(*) over (partition by sample_id,invitemmaterial_id,result_name) as material_count"
						+ " from fg_i_sampleresults_v)" + " where TO_NUMBER(material_count) > 1" + " and sample_id = '"
						+ formId + "'" + " and TO_NUMBER(result_id) in (" + sampleResCsv + ")))";
				elementValueMap.put("sampleResults", generalDao.selectSingleString(sql));
			}
		}
		//set formNumberId- Sample number
		String nextFormNumberId = formIdCalc.getNextFormNumberIdByFormId(formCode, formId, elementValueMap, userId,
				pivotTable);
		if (nextFormNumberId == null) {
			return -1;
		}
		int auditTrailChangeType = !nextFormNumberId.isEmpty() ? 1 : 0;

		if (!nextFormNumberId.isEmpty()) {
			generalUtilLogger.logWriter(LevelType.DEBUG,
					generalUtil.mapToString("set Sample name - elementValueMap before event:", elementValueMap),
					ActivitylogType.SaveEvent, formId);
			elementValueMap.put("formNumberId", nextFormNumberId);
			elementValueMap.put("sampleName", nextFormNumberId);//necessary for future use in the child forms in DDL-id/val
		}

		return auditTrailChangeType;
	}

	private void postFormSaveStepStatusChange(String formId, String lastStatusName, String currentStatusName,
			Map<String, String> elementValueMap, String formcode, String userId, StringBuilder sbInfo)
			throws Exception {
		String expVersion = formDao.getFromInfoLookup("EXPERIMENT", LookupType.ID, elementValueMap.get("EXPERIMENT_ID"),"EXPERIMENTVERSION");

		/*if (!currentStatusName.equals("Planned")) {
			integrationValidation.validate(ValidationCode.INVALID_EXPERIMENT_NONFAMILIAR_STATUS, formcode, formId,
					elementValueMap.get("EXPERIMENT_ID"), sbInfo);
		}*/

		List<String> instrumentRefIds = generalDao
				.getListOfStringBySql("Select formid from FG_S_STEP_PIVOT where EXPERIMENT_ID = '"
						+ elementValueMap.get("EXPERIMENT_ID") + "'");
		for (String id : instrumentRefIds) {
			cloneExperiment.insertRefUniqueColumn("INSTRUMENTREF", formId, userId, formcode, id, null,
					"and includeInAllSteps = '1' and cloneid is null", "INVITEMINSTRUMENT_ID");
		}
		/********* ACTIVE STATUS ***********/
		if (!lastStatusName.equals("Active") && currentStatusName.equals("Active")) {//status has been changed to active
			/*if(generalUtil.getNull(expVersion).isEmpty() || generalUtil.getNull(expVersion).equals("01"))
			integrationValidation.validate(formcode.equalsIgnoreCase("STEPFR")
					? ValidationCode.INVALID_STEPSFRTATUS_EMPTYBATCH : ValidationCode.INVALID_STEPSTATUS_EMPTYBATCH,
					formcode, formId, "", sbInfo);*/

			// check if last status is not active in the DB function CREATE_EXPERIMENT_MATERIAL_SS
			Map<String, String> map = new HashMap<String, String>();
			//map.put("experimentId_in", elementValueMap.get("EXPERIMENT_ID"));
			map.put("stepId_in", formId);

			//changes experiment status to active. Note: we used LASTSTATUS_ID for the notification code (that comes latter) to indicate that there was a change - this is why they not both active!)
			//if two users changed the exp status in parallel- one from the exp form and another from the step(in the current code or in the cloneExperiment-see comment below), the change from the step will not be experiment if the exp status is different from planned
			//important! every change here should be done also in the CloneExperiment.updateNewRunStepsStatusToActive func
			String sql_ = String.format(
					" update %1$s set ACTUALSTARTDATE = decode(ACTUALSTARTDATE,null,TO_CHAR(sysdate,'" + generalUtil.getConversionDateFormat() + "'),ACTUALSTARTDATE),"
							+ "ACTUALSTARTTIMESTAMP = decode(ACTUALSTARTDATE,null,TO_CHAR(sysdate,'" + generalUtil.getConversionDateTimeFormat() + "'),ACTUALSTARTTIMESTAMP),"
							+  "LASTSTATUS_ID = STATUS_ID , STATUS_ID =%2$s, LASTMODIFDATE = TO_CHAR(sysdate,'"
							+ generalUtil.getConversionDateFormat() + "'),timestamp=sysdate,change_by = '"+userId+"'\n"
							+ " where FORMID =  %3$s and nvl(STATUS_ID,'%4$s') = '%4$s'",
					"FG_S_EXPERIMENT_PIVOT",
					formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.NAME, "Active", "id"),
					elementValueMap.get("EXPERIMENT_ID"),
					formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.NAME, "Planned", "id"));
			List<String> colList = Arrays.asList("ACTUALSTARTDATE", "ACTUALSTARTTIMESTAMP", "LASTSTATUS_ID",
					"STATUS_ID", "LASTMODIFDATE","TIMESTAMP","CHANGE_BY");
			formSaveDao.updateStructTableByFormId(sql_, "FG_S_EXPERIMENT_PIVOT", colList,
					elementValueMap.get("EXPERIMENT_ID"));

			//update flag(changeable) in action to 0
			/*formSaveDao.updateStructTable(
					String.format(" update fg_s_action_pivot set changeable = 0 where  STEP_ID = %1$s", formId),
					"fg_s_action_pivot", Arrays.asList("changeable"), "STEP_ID", formId);*/

			if (formcode.equals("Step")) {
				generalDao.callPackageFunction("FG_ADAMA", "CREATE_EXPERIMENT_MATERIAL_SS", map);//create a snapshot for displaying the right tables in actual or planned state
			} else {
				generalDao.callPackageFunction("FG_ADAMA", "CREATE_EXPERIMENT_FORMULANT_SS", map);//create a snapshot for displaying the right tables in actual or planned state
			}

			/********* FINISH STATUS ***********/
		} else if (currentStatusName.equals("Finished") && !lastStatusName.equals("Finished")
				|| currentStatusName.equals("Cancelled") && !lastStatusName.equals("Cancelled")) {//status has been changed to Finished or Cancelled
			if (currentStatusName.equals("Finished")) { //Finished status
				//any change here should be match to the code in generalevent changeStepStatusToFinish
				if (formcode.equalsIgnoreCase("Step")) {					
					//runs through all the batches that were used in this step and update their quantity in the inventory
					commonFunc.validateUsedQuantityOnStepStatusChangeToFinished(formId, expVersion, sbInfo);
				} 
				else {//stepFr
					List<Map<String, Object>> ascendingBatchInfo = generalDao.getListOfMapsBySql(
							"Select distinct BATCH_ID,RESULT_VALUE,RESULT_UOM_ID from fg_i_webix_output_all_v  where step_id = '"
									+ formId + "' and batch_id is not null and isstandart = 0");//TODO:get the batches that are not temporary under step
					if (ascendingBatchInfo.isEmpty()) {
						generalUtilLogger.logWriter(LevelType.DEBUG,
								"There is no batch in the step or the batches are standard(batch quantity is not depleted from batches defined as standard)",
								ActivitylogType.SaveEvent, formId);
					}
					for (Map<String, Object> batchInfo : ascendingBatchInfo) {
						//						String binfo = "";
						//						try {
						//							binfo = (String) batchInfo.get("BATCH_ID");
						//						} catch (Exception e) {
						//							// TODO Auto-generated catch block
						//							e.printStackTrace();
						//						}
						Map<String, String> srcBatchInf = generalDao.sqlToHashMap(
								"select QUANTITY,QUANTITYUOM_ID from FG_S_INVITEMBATCH_PIVOT where formId = '"
										+ batchInfo.get("BATCH_ID") + "'");
						Double normalSrcQuantity = generalUtilCalc.getNormalNumber(
								generalUtil.getEmpty(srcBatchInf.get("QUANTITY"), "0"),
								srcBatchInf.get("QUANTITYUOM_ID"), 0D);
						Double normalConsumeQuantity = generalUtilCalc.getNormalNumber(
								batchInfo.get("RESULT_VALUE") == null ? "0" : batchInfo.get("RESULT_VALUE").toString(),
								batchInfo.get("RESULT_UOM_ID").toString(), 0D);

						if (generalUtil.getNull(expVersion).isEmpty() || generalUtil.getNull(expVersion).equals("01")) {
							/*integrationValidation.validate(ValidationCode.INVALID_CONSUMED_QUANTITY, "INVITEMBATCH",
									formId,
									new String(normalConsumeQuantity.toString() + "," + normalSrcQuantity.toString()),
									sbInfo);*/
							commonFunc.postUseOfBatch(
									batchInfo.get("RESULT_VALUE") == null ? "0"
											: batchInfo.get("RESULT_VALUE").toString(),
									batchInfo.get("RESULT_UOM_ID").toString(),
									batchInfo.get("BATCH_ID").toString(), formId, sbInfo);
						}

						//insert data into activityLog
						/*
						 * Double quantity = normalSrcQuantity - normalConsumeQuantity;
						 * String lab = generalDao.selectSingleString(
						 * String.format(" select t.LABORATORY_ID from FG_S_INVITEMBATCH_PIVOT t where formId = %1$s",batchInfo.get("BATCH_ID")));
						 * String site = generalDao.selectSingleString(
						 * String.format(" select t.SITE_ID from FG_S_INVITEMBATCH_PIVOT t where formId = %1$s",batchInfo.get("BATCH_ID")));
						 * String location = formDao.getFromInfoLookup("LABORATORY", LookupType.ID, lab, "name") + '/'
						 * + formDao.getFromInfoLookup("SITE", LookupType.ID, site, "name");
						 * // ActivityLog activityLogEntity = new ActivityLog(ActivitylogType.Consumption,formId, "TO_DATE(sysdate)",user,Integer.toString(quantity),"",location,"TO_DATE(sysdate)","''","","");
						 * // insertIntoActivityLog(activityLogEntity);
						 * Map<String, String> infoMap = new HashMap<String, String>();
						 * infoMap.put("quantity", Double.toString(quantity));
						 * infoMap.put("location", location);
						 * infoMap.put("step_id", formId);
						 * generalUtilLogger.logWrite(LevelType.Other, "",binfo, ActivitylogType.Consumption, infoMap);
						 */
					}
				}
				
				//update all the self test under the steps to Completed (that are not cancelled) when the step / stepfr  changing to finish (on experiment version 1 only) - part of task 2453
//				if(generalUtil.getNull(expVersion).equals("01")) { // fix bug 7896 - remove the expVersion condition
				String stCompletedStatus = formDao.getFromInfoLookup("SELFTESTSTATUS", LookupType.NAME, "Completed", "id");
				String stCancelledStatus = formDao.getFromInfoLookup("SELFTESTSTATUS", LookupType.NAME, "Cancelled", "id");
				formSaveDao.updateStructTable("update fg_s_selftest_pivot t set t.status_id ='" + stCompletedStatus + "' where  t.action_id in (select a.action_id from fg_s_action_v a where a.STEP_ID ='" + formId + "') and t.status_id <> '" + stCancelledStatus + "'", "FG_S_SELFTEST_PIVOT",  Arrays.asList("STATUS_ID"), "ACTION_ID","(select a.action_id from fg_s_action_v a where a.STEP_ID ='" + formId + "')");
//				}
			}

			//changes experiment status to finished
			String parentTable = "FG_S_EXPERIMENT_PIVOT";
			String finishedStatusId = formDao.getFromInfoLookup("STEPSTATUS", LookupType.NAME, "Finished", "id");
			String cancelledStatusId = formDao.getFromInfoLookup("STEPSTATUS", LookupType.NAME, "Cancelled", "id");
			//countByStatus = count all status (not this that are not cancel or finish
			String countByStatus = generalDao
					.selectSingleString("select nvl(count(*),0) from FG_S_STEP_PIVOT t where t.EXPERIMENT_ID = '"
							+ elementValueMap.get("EXPERIMENT_ID") + "' and formId <> '" + formId
							+ "' and t.STATUS_ID not in ('" + finishedStatusId + "','" + cancelledStatusId + "')");
			if (generalUtil.getNullInt(countByStatus, -1) == 0) { //all steps under specific experiment are Finished or cancelled,then updates the experiment status to finished
				String sql_ = String.format(
						" update %1$s set LASTSTATUS_ID = STATUS_ID , STATUS_ID =%2$s, LASTMODIFDATE = TO_CHAR(sysdate,'"
								+ generalUtil.getConversionDateFormat() + "'),CHANGE_BY = '"+userId+"',TIMESTAMP = sysdate\n"
								+ " where FORMID =  %3$s and not exists (select step_id from fg_s_step_v where experiment_id = '"+elementValueMap.get("EXPERIMENT_ID")+"'\n"
										+ " and formid<>'"+formId+"' and STATUS_ID not in ('" + finishedStatusId + "','" + cancelledStatusId + "'))",
						parentTable, formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.NAME, "Finished", "id"),
						elementValueMap.get("EXPERIMENT_ID"));
				List<String> colList = Arrays.asList("LASTSTATUS_ID", "STATUS_ID", "LASTMODIFDATE","CHANGE_BY","TIMESTAMP");
				formSaveDao.updateStructTableByFormId(sql_, parentTable, colList, elementValueMap.get("EXPERIMENT_ID"));
				/*
				 * //runs through all the batches in the experiment formulation and consume the quantity from the inventory batch
				 * List<Map<String, Object>> ascendingBatchInfo = generalDao.getListOfMapsBySql("Select BATCH_ID,MASS from fg_i_webix_output_all_v where experiment_id = '"+formId+"'");//TODO:get the batches that are not temporary under step
				 * for (Map<String, Object> batchInfo : ascendingBatchInfo) {
				 * String currentQuantity = generalDao
				 * .selectSingleString("select QUANTITY from FG_S_INVITEMBATCH_PIVOT where formId = '"
				 * + batchInfo.get("BATCH_ID") + "'");
				 * postUseOfBatch(generalUtil.getEmpty(batchInfo.get("MASS").toString(),"0"), generalUtil.getEmpty(currentQuantity,"0"),batchInfo.get("BATCH_ID").toString());
				 * //insert data into activityLog
				 * int quantity = Integer.parseInt(currentQuantity)
				 * - Integer.parseInt(batchInfo.get("MASS").toString());
				 * String lab = generalDao.selectSingleString(
				 * String.format(" select t.LABORATORY_ID from FG_S_INVITEMBATCH_PIVOT t where formId = %1$s",batchInfo.get("BATCH_ID")));
				 * String site = generalDao.selectSingleString(
				 * String.format(" select t.SITE_ID from FG_S_INVITEMBATCH_PIVOT t where formId = %1$s",batchInfo.get("BATCH_ID")));
				 * String location = formDao.getFromInfoLookup("LABORATORY", LookupType.ID, lab, "name") + '/'
				 * + formDao.getFromInfoLookup("SITE", LookupType.ID, site, "name");
				 * // ActivityLog activityLogEntity = new ActivityLog(ActivitylogType.Consumption,formId, "TO_DATE(sysdate)",user,Integer.toString(quantity),"",location,"TO_DATE(sysdate)","''","","");
				 * // insertIntoActivityLog(activityLogEntity);
				 * Map<String, String> infoMap = new HashMap<String, String>();
				 * infoMap.put("quantity", Integer.toString(quantity));
				 * infoMap.put("location", location);
				 * generalUtilLogger.logWrite(LevelType.DEBUG, "",formId, ActivitylogType.Consumption, infoMap);
				 * }
				 */
			}

			//update flag(changeable) in action to 1
			/*formSaveDao.updateStructTable(
					String.format(" update fg_s_action_pivot set changeable = 1 where  STEP_ID = %1$s", formId),
					"fg_s_action_pivot", Arrays.asList("changeable"), "STEP_ID", formId);*/
		}
	}

	/**
	 * Adds records to the manual results table up to the sample-request combinations in the experiment
	 * 
	 * @param formId
	 * @param userId
	 * @param elementValueMap 
	 * @throws Exception 
	 */
	private void updateManualResultsTable(String formCode, String formId, String userId, String experimentTypeName, Map<String, String> elementValueMap) throws Exception {
		//deletes the manual results that some of their building components were change (sample/material/request)
		//		String sql = "select formid " + " from FG_S_MANUALRESULTSREF_V t" + " where t.PARENTID = '" + formId + "'"
		//				+ " and  SAMPLE_ID||','||COMPONENT_ID||','||REQUEST_ID not in" + "("
		//				+ "	select SAMPLE_ID||','||COMPONENT_ID||','||REQUEST_ID" + " from ("
		//				+ "select distinct smpl.SAMPLE_ID,smplReq.REQUEST_ID,c.COMPONENT_ID"
		//				+ " from FG_I_CONNECTION_REQSMPLDEXP_V smplReq," + " fG_S_SAMPLESELECT_ALL_V smpl,"
		//				+ " FG_S_COMPONENT_ALL_V c" + " where smpl.PARENTID = smplReq.EXPERIMENTDEST_ID(+)"
		//				+ " and smpl.SAMPLE_ID = smplReq.SAMPLE_ID(+)" + " and smpl.PARENTID = '" + formId + "'"
		//				+ " and c.PARENTID = '" + formId + "'" + ")" + ") ";
		String experimentStatusName = formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.ID, elementValueMap.get("STATUS_ID"), "name");
		if(experimentTypeName.equals("General") && !formCode.equals("ExperimentMain")) {
			//IMPORTANT: if some validation is added then it should be added in the FormApiExcelService.saveSpreadsheet. it should also be considered in case that the excel would be editable in the completed status(the validations should also be added or change the private save of full screen to the general save)
			//1. checks if there was an error that found on the client size
			String spreadsheetResultsData = generalUtilFormState.getStringContent(elementValueMap.get("spreadsheetResults"), "ExperimentAn", "spreadsheetResults", formId);
			JSONObject js = new JSONObject();
			if(generalUtil.getNull(spreadsheetResultsData).isEmpty()){
				return;
			}
			js = new JSONObject(spreadsheetResultsData);
			String validationMessage = (String) js.get("validationMessage");
			if(!validationMessage.isEmpty()) {
				throw new Exception(validationMessage);
			}
			
			//2. checks if the result type is missing
			JSONObject jsspreadsheetData = (JSONObject)js.get("output");
			
			if(!jsspreadsheetData.has("0")){
				return;
			}
			JSONArray arr = new JSONArray(jsspreadsheetData.get("0").toString());
			for(int i = 0;i<arr.length();i++) {
				JSONObject sampleMaterialPair = arr.getJSONObject(i);
				String material = generalUtil.getNull(sampleMaterialPair.getString("Material"));
				String manualMaterial = generalUtil.getNull(sampleMaterialPair.getString("Unknown Materials"));
				String resultType = generalUtil.getNull(sampleMaterialPair.getString("Results Type")) ;
				String rt = generalUtil.getNull(sampleMaterialPair.getString("RT")) ;
				if(material.isEmpty() && manualMaterial.isEmpty()) {
					continue;
				}
				if(resultType.isEmpty()) {
					integrationValidation.validate(ValidationCode.INVALID_SPREADSHEETRESULT_MISSING_DATA, formCode, formId, "", new StringBuilder());
				}
			}
			//IMPORTANT!! if the results would be updated also on the approved status->the query should be changed and instead of deleting all the results before re-updating them, we will have to find the resultsref_id of the combination of material&sample and just update the result value&comment.
			// this should be done because the results are stored in the sample results and may be checked as main since the experiment was completed,and while it is implemented as it is now, the results_id may be changed if they would be updated on the approved status
			if(experimentStatusName.equals("Completed")) {//in the general analytical experiment the manual results are deleted and re-built
				//delete the manual results
				String sql = "delete from FG_S_MANUALRESULTSREF_PIVOT\n"
						+ "where parentid = '"+formId+"'";
				generalUtilLogger.logWriter(LevelType.DEBUG,
					"Delete the manual results in the general experiment. Sql Query=></br>" + sql,
					ActivitylogType.ManualResultsUpdate, formId);
				formSaveDao.deleteStructTable(sql, "FG_S_MANUALRESULTSREF_PIVOT", "parentid", formId);
				
				//insert into the manual results table all data from the spreadsheet results
				
				jsspreadsheetData = (JSONObject)js.get("output");
				
				
				//collect the materials,manual materials and the samples
				Set<String> materialList = new LinkedHashSet<String>();
				Set<String> manualMaterialList = new LinkedHashSet<String>();
				Set<String> sampleList = new LinkedHashSet<String>();
				Set<String> resultTypeList = new LinkedHashSet<String>();
				Set<String> uomList = new LinkedHashSet<String>();
				Map<String,String> manualMaterialMwPairs = new HashMap();
				if(!jsspreadsheetData.has("0")){
					return;
				}
				arr = new JSONArray(jsspreadsheetData.get("0").toString());
				for(int i = 0;i<arr.length();i++) {
					JSONObject sampleMaterialPair = arr.getJSONObject(i);
					String sample = generalUtil.getNull(sampleMaterialPair.getString("Sample"));
					String material = generalUtil.getNull(sampleMaterialPair.getString("Material"));
					String manualMaterial = generalUtil.getNull(sampleMaterialPair.getString("Unknown Materials"));
					String resultValue = generalUtil.getNull(sampleMaterialPair.getString("value"));
					String resultType = generalUtil.getNull(sampleMaterialPair.getString("Results Type")) ;
					String uom = generalUtil.getNull(sampleMaterialPair.getString("Uom")) ;
					String mw = generalUtil.getNull(sampleMaterialPair.getString("MW")) ;
					if(material.isEmpty() && manualMaterial.isEmpty() || !material.isEmpty() && (sample.isEmpty() || resultValue.isEmpty() || resultType.isEmpty())) {
						continue;
					}
					if(!resultValue.isEmpty()) {
						if(!material.isEmpty()) {
							materialList.add(material);
						}
						sampleList.add(sample);
						resultTypeList.add(resultType);
						uomList.add(uom);
					}
					if(material.isEmpty() && !manualMaterial.isEmpty()) {
						manualMaterialList.add(manualMaterial);
						manualMaterialMwPairs.put(manualMaterial, mw);
					}
				}
				
				
				  //3. checks that the materials are valid, ie. all of them exist in the inventory
				  integrationValidation.validate(ValidationCode.INVALID_MATERIAL_NAME,
				  formCode, formId, materialList, new StringBuilder());
				  
				  //4.checks that the result types are valid, ie. all of them exist in the result type maintenance
				  integrationValidation.validate(ValidationCode.INVALID_RESULT_TYPE, formCode,
				  formId, resultTypeList, new StringBuilder());
				  
				 //5.checks that the samples are valid, ie. all of them exist in the sample select of the experiment
				  integrationValidation.validate(ValidationCode.INVALID_RESULT_SAMPLE,
				  formCode, formId, sampleList, new StringBuilder());
				  
				 //6.checks whether any of the unknown materials already exists in the inventory
				 integrationValidation.validate(ValidationCode.INVALID_UNKNOWN_MATERIAL,
				  formCode, formId, manualMaterialList, new StringBuilder());
				 
				 //7.checks whether any of the unknown materials already exists in the inventory
				 integrationValidation.validate(ValidationCode.INVALID_UOM,
				  formCode, formId, uomList, new StringBuilder());
				
				//8. creates the temporary materials that has entered as unknown 
				String project_id = elementValueMap.get("PROJECT_ID");
				for(Entry<String,String> manualMaterialMwPair:manualMaterialMwPairs.entrySet()) {
					String manualMaterial = manualMaterialMwPair.getKey();
					if(!manualMaterial.isEmpty()) {
						String mw = manualMaterialMwPair.getValue();
						createTemporaryMaterials(manualMaterial,project_id,mw,userId);
					}
				}
				
				//9. update the cells that represent the temporary materials and insert the new material_id
				for(int i = 0;i<arr.length();i++) {
					JSONObject sampleMaterialPair = arr.getJSONObject(i);
					String sample = generalUtil.getNull(sampleMaterialPair.getString("Sample"));
					String material = generalUtil.getNull(sampleMaterialPair.getString("Material"));
					String manualMaterial = generalUtil.getNull(sampleMaterialPair.getString("Unknown Materials"));
					String resultValue = generalUtil.getNull(sampleMaterialPair.getString("value"));
					String resultType = generalUtil.getNull(sampleMaterialPair.getString("Results Type")) ;
					if(material.isEmpty() && manualMaterial.isEmpty() || !material.isEmpty() && (sample.isEmpty() || resultValue.isEmpty() || resultType.isEmpty())) {
						continue;
					}
					//if(!resultValue.isEmpty()) {
						if(!manualMaterial.isEmpty()) {
							JSONObject currentNewCell = new JSONObject(sampleMaterialPair.toString());
							String material_id = generalDao.selectSingleString("select distinct invitemmaterial_id from fg_s_invitemmaterial_all_v where invitemmaterialname = '"+manualMaterial+"' and MaterialStatusName <> 'Cancelled'");
							currentNewCell.put("material_id", material_id);
							arr.put(i, currentNewCell);
						}
					//}
				}
			
				//10. insert the data into the manual results 
				List<String> sampleselectList = generalDao.getListOfStringBySql("select sample_id\n"
						+ " from fg_s_sampleselect_all_v\n"
						+ "where parentid = '"+formId+"'\n"
						+ "and sessionid is null\n"
						+ "and active=1");
				
				for(int i = 0;i<arr.length();i++) {
					JSONObject sampleMaterialPair = arr.getJSONObject(i);
					String sample_id = formDao.getFromInfoLookup("sample", LookupType.NAME, sampleMaterialPair.getString("Sample") , "id");
					if(!sampleselectList.contains(sample_id)) {//some samples may be removed by the user in the sampleselect but not removed from the excel
						//however, those ones that have removed from the sample select will not displayed in the excel anymore(handled on the load of the element)
						continue;
					}
					String manualMaterial = generalUtil.getNull(sampleMaterialPair.getString("Unknown Materials"));
					String selectedMaterial = generalUtil.getNull(sampleMaterialPair.getString("Material"));					
					String materialName = manualMaterial.isEmpty()?selectedMaterial:manualMaterial;
					String material_id = generalDao.selectSingleString("select distinct invitemmaterial_id from fg_s_invitemmaterial_all_v where invitemmaterialname = '"+materialName+"' and MaterialStatusName <> 'Cancelled'");//in this part of code, the temporary material probably created, thus the id already exists
					String resultValue = generalUtil.getNull(sampleMaterialPair.getString("value") );
					String uom = generalUtil.getNull(sampleMaterialPair.getString("Uom"));
					String resultCommnent = generalUtil.getNull(sampleMaterialPair.getString("comment"));
					String currentResultType = generalUtil.getNull(sampleMaterialPair.getString("Results Type"));
					String resultType = generalUtil.getSpringMessagesByKey(currentResultType.equals("AI Concentration")||currentResultType.equals("Active Ingredient")?"Assay"
							:(currentResultType.equals("Impurity")?"Impurity Percent":currentResultType),"");
					
					//checks if one of the mandatory fields is missing.
					if(sample_id.isEmpty() || material_id.isEmpty() || resultValue.isEmpty() || resultType.isEmpty()) {
						continue;
					}
					
					String manualResId = formSaveDao.getStructFormId("MANUALRESULTSREF");
					generalUtilLogger
							.logWriter(LevelType.DEBUG,
									"Add manual result formId = " + manualResId + ".</br>" 
											+ ";</br>Sample_id:" + sample_id
											+ ";</br>material_id:" + material_id
											,
									ActivitylogType.ManualResultsUpdate, formId);
					String resultTypeId = formDao.getFromInfoLookup("ANALYTICRESULTTYPE", LookupType.NAME, resultType,
							"ID");
					String uomId = "";
					if(uom.isEmpty()) {
						uomId = formDao.getFromInfoLookup("UOM", LookupType.NAME, "%", "ID");
					} else {
						List<String> uomSelectedList = formDao.getFromInfoLookupElementData("UOM", LookupType.NAME, uom, "ID");
						for (String uomSelectedId : uomSelectedList) {
							Map<String, String> uomData = formDao.getFromInfoLookupAll("UOM", LookupType.ID, uomSelectedId);
							if (uomData.get("UOMTYPENAME").equalsIgnoreCase("weight")||uomData.get("UOMTYPENAME").equalsIgnoreCase("percentage")) {
								uomId = uomSelectedId;
								break;
							}
						}
					}
					/*
					 * sql = String.format( "insert into fg_s_manualResultsRef_pivot t" +
					 * " (t.FORMID,t.TIMESTAMP,t.CHANGE_BY,t.CREATION_DATE,t.CREATED_BY,t.SESSIONID,t.ACTIVE,t.FORMCODE, t.FORMCODE_ENTITY, REQUEST_ID, SAMPLE_ID,PARENTID,MATERIAL_ID,RESULT_TYPE_ID,COMPONENT_ID,COMMENTS,RESULT,UOM_ID)"
					 * +
					 * " values(%1$s, sysdate, %2$s, sysdate, %2$s, NULL, 1, 'MANUALRESULTSREF', 'MANUALRESULTSREF', %3$s, %4$s, %5$s, %6$s,'%7$s','%8$s','"
					 * +resultCommnent+"','"+resultValue+"','"+uomId+"')", manualResId, userId,
					 * "NULL", sample_id, formId, material_id, resultTypeId, "NULL");
					 */

					sql = "insert into fg_s_manualResultsRef_pivot t"
									+ " (t.FORMID,t.TIMESTAMP,t.CHANGE_BY,t.CREATION_DATE,t.CREATED_BY,t.SESSIONID,t.ACTIVE,t.FORMCODE, t.FORMCODE_ENTITY, REQUEST_ID, SAMPLE_ID,PARENTID,MATERIAL_ID,RESULT_TYPE_ID,COMPONENT_ID,COMMENTS,RESULT,UOM_ID)"
									+ " values('"+manualResId+"', sysdate, '"+userId+"', sysdate, '"+userId+"', NULL, 1, 'MANUALRESULTSREF', 'MANUALRESULTSREF', NULL , '"+sample_id+"', '"+formId+"', '"+material_id+"','"+resultTypeId+"',NULL,'"+resultCommnent+"','"+resultValue+"','"+uomId+"')";
					formSaveDao.insertStructTableByFormId(sql, "fg_s_manualresultsref_pivot", manualResId);
				}
				jsspreadsheetData = new JSONObject();
				jsspreadsheetData.put("0", new JSONArray(arr.toString()));
				js.put("output", jsspreadsheetData);
				uploadFileDao.saveStringAsClob(elementValueMap.get("spreadsheetResults"), js.toString());
			}
			return;
		}
		
		
		String sql = "with FILT_IN AS (\r\n"
				+ " select /*+ MATERIALIZE */ SAMPLE_ID||','||COMPONENT_ID||','||REQUEST_ID AS FILT_IN_DATA\r\n"
				+ "    from (\r\n" + "      select DISTINCT  smpl.SAMPLE_ID,smplReq.REQUEST_ID,c.COMPONENT_ID \r\n"
				+ "      from FG_I_CONNECTION_REQSMPLDEXP_V smplReq, fG_S_SAMPLESELECT_ALL_V smpl, FG_S_COMPONENT_ALL_V c\r\n"
				+ "     where smpl.PARENTID = smplReq.EXPERIMENTDEST_ID(+) and smpl.SAMPLE_ID = smplReq.SAMPLE_ID(+) \r\n"
				+ "     and smpl.PARENTID = '" + formId + "' and c.PARENTID = '" + formId + "'\r\n"
				+ "		and smpl.sessionid is null and smpl.active=1 and c.sessionid is null and c.active=1\r\n"
				+ "     ) ) select formid \r\n" + "from FG_S_MANUALRESULTSREF_V t \r\n" + "where t.PARENTID = '"
				+ formId + "' and SAMPLE_ID||','||COMPONENT_ID||','||REQUEST_ID \r\n"
				+ "not in (SELECT FILT_IN_DATA FROM FILT_IN) and nvl(IS_USER_OWN,'0') = '0'";
		generalUtilLogger.logWriter(LevelType.DEBUG,
				"Check if there are manual results to delete. Sql Query=></br>" + sql,
				ActivitylogType.ManualResultsUpdate, formId);
		//deletes the manual results that contains non-relevant combinations
		List<String> deletedManualResults = generalDao.getListOfStringBySql(sql);
		for (String resultId : deletedManualResults) {
			Map<String, String> data = generalDao.sqlToHashMap("select Sample_id,component_id,request_id,IS_USER_OWN"
					+ " from FG_S_MANUALRESULTSREF_V where formid = '" + resultId + "' and rownum<=1");
			generalUtilLogger.logWriter(LevelType.DEBUG,
					"Delete manual result formId = " + resultId + ".</br>" + "component_id:"
							+ generalUtil.getNull(data.get("COMPONENT_ID")) + ";</br>sample_id:"
							+ generalUtil.getNull(data.get("SAMPLE_ID")) + ";</br>request_id:"
							+ generalUtil.getNull(data.get("REQUEST_ID")) + ";</br>IS_USER_OWN:"
							+ generalUtil.getNull(data.get("IS_OWNER_OWN")),
					ActivitylogType.ManualResultsUpdate, formId);
			formSaveDao.deleteStructTable(
					"delete from FG_S_MANUALRESULTSREF_PIVOT" + " where formid = '" + resultId + "'",
					"FG_S_MANUALRESULTSREF_PIVOT", "formid", resultId);
		}

		//second, add to the manual results table the new ones
		List<Map<String, Object>> sampleRequestPairs = generalDao.getListOfMapsBySql(
				"select distinct smpl.SAMPLE_ID,smplReq.REQUEST_ID from FG_I_CONNECTION_REQSMPLDEXP_V smplReq, fG_S_SAMPLESELECT_ALL_V smpl"
						+ " where smpl.PARENTID = smplReq.EXPERIMENTDEST_ID(+)"
						+ " and smpl.SAMPLE_ID = smplReq.SAMPLE_ID(+)" + " and smpl.PARENTID = '" + formId + "'"
						+ " and smpl.sessionid is null and nvl(smpl.active,'1')='1'");
		List<String> testedComponentsMaterialList = generalDao
				.getListOfStringBySql("select distinct component_id from fg_s_component_all_v where parentId = '"
						+ formId + "' and sessionid is null and nvl(active,'1') = '1'");// gets all the components under the cureent experiment)
		//pass through the sampleRequestPairs and add them to the manual result table
		for (Map<String, Object> smplReqPair : sampleRequestPairs) {
			for (String componentId : testedComponentsMaterialList) {
				String isRecordExist = generalDao.selectSingleStringNoException(String.format(
						"select 0 from dual where not exists  "
								+ " (select * from fg_s_manualresultsRef_v where SAMPLE_ID =  %1$s and COMPONENT_ID = %4$s  and (REQUEST_ID = %2$s or REQUEST_ID is NULL and %2$s is NULL) and parentid = %3$s)",
						smplReqPair.get("SAMPLE_ID").toString(),
						smplReqPair.get("REQUEST_ID") == null || smplReqPair.get("REQUEST_ID").toString().isEmpty()
								? "NULL" : smplReqPair.get("REQUEST_ID").toString(),
						formId, componentId));
				if (!isRecordExist.isEmpty()) {//a record with the specific sample and the request and component does not exist yet
					String manualResId = formSaveDao.getStructFormId("MANUALRESULTSREF");
					generalUtilLogger
							.logWriter(LevelType.DEBUG,
									"Add manual result formId = " + manualResId + ".</br>" + "Request_id:"
											+ (smplReqPair.get("REQUEST_ID") == null
													|| smplReqPair.get("REQUEST_ID").toString().isEmpty() ? "NULL"
															: smplReqPair.get("REQUEST_ID").toString())
											+ ";</br>Sample_id:" + smplReqPair.get("SAMPLE_ID").toString()
											+ ";</br>component_id:" + componentId + ";</br>material_id:"
											+ generalDao.selectSingleString("select materialId from fg_s_component_v"
													+ " where formid = '" + componentId
													+ "' and sessionid is null and nvl(active,'1')='1'"),
									ActivitylogType.ManualResultsUpdate, formId);
					String resultType = generalUtil.getSpringMessagesByKey(generalDao
							.selectSingleString("select distinct TESTEDCOMPTYPENAME from fg_s_component_all_v"
									+ " where formid = '" + componentId + "'"
									+ " and sessionid is null and nvl(active,'1') = '1' and rownum<=1")
							.replace(" ", ""), "");
					/*
					 * experimentTypeName.contains("Impurity")
					 * ? generalUtil.getSpringMessagesByKey(generalDao
					 * .selectSingleString("select distinct TESTEDCOMPTYPENAME from fg_s_component_all_v"
					 * + " where formid = '" + componentId + "'"
					 * + " and sessionid is null and nvl(active,'1') = '1' and rownum<=1")
					 * .replace(" ", ""), "")
					 * : (experimentTypeName.contains("Assay") ? experimentTypeName : "");
					 *///removed the code due to task 20201
					String resultTypeId = formDao.getFromInfoLookup("ANALYTICRESULTTYPE", LookupType.NAME, resultType,
							"ID");
					sql = String.format(
							"insert into fg_s_manualResultsRef_pivot t"
									+ " (t.FORMID,t.TIMESTAMP,t.CHANGE_BY,t.CREATION_DATE,t.CREATED_BY,t.SESSIONID,t.ACTIVE,t.FORMCODE, t.FORMCODE_ENTITY, REQUEST_ID, SAMPLE_ID,PARENTID,MATERIAL_ID,RESULT_TYPE_ID,COMPONENT_ID)"
									+ " values(%1$s, sysdate, %2$s, sysdate, %2$s, NULL, 1, 'MANUALRESULTSREF', 'MANUALRESULTSREF', %3$s, %4$s, %5$s, %6$s,'%7$s','%8$s')",
							manualResId, userId,
							smplReqPair.get("REQUEST_ID") == null || smplReqPair.get("REQUEST_ID").toString().isEmpty()
									? "NULL" : smplReqPair.get("REQUEST_ID").toString(),
							smplReqPair.get("SAMPLE_ID").toString(), formId,
							generalDao
									.selectSingleString("select materialId from fg_s_component_v" + " where formid = '"
											+ componentId + "' and sessionid is null and nvl(active,'1')='1'"),
							resultTypeId, componentId);
					formSaveDao.insertStructTableByFormId(sql, "fg_s_manualresultsref_pivot", manualResId);
				} else {
					String resultType = generalUtil.getSpringMessagesByKey(generalDao
							.selectSingleString("select distinct TESTEDCOMPTYPENAME from fg_s_component_all_v"
									+ " where formid = '" + componentId + "'"
									+ " and sessionid is null and nvl(active,'1') = '1' and rownum<=1")
							.replace(" ", ""), "");
					/*
					 * experimentTypeName.contains("Impurity")
					 * ? generalUtil.getSpringMessagesByKey(generalDao
					 * .selectSingleString("select distinct TESTEDCOMPTYPENAME from fg_s_component_all_v"
					 * + " where formid = '" + componentId + "'"
					 * + " and sessionid is null and nvl(active,'1') = '1' and rownum<=1")
					 * .replace(" ", ""), "")
					 * : (experimentTypeName.contains("Assay") ? experimentTypeName : "");
					 *///removed the code due to task 20201
					String resultTypeId = formDao.getFromInfoLookup("ANALYTICRESULTTYPE", LookupType.NAME, resultType,
							"ID");
					String materialId = generalDao
							.selectSingleStringNoException("select materialid from fg_s_component_v where formid = '"
									+ componentId + "' and sessionid is null and nvl(active,'1') ='1'\r\n");
					sql = "update fg_s_manualresultsref_pivot t\r\n" + " set MATERIAL_ID = '" + materialId + "',\r\n"
							+ " RESULT_TYPE_ID = '" + resultTypeId + "'\r\n" + " where active = 1 and component_id ='"
							+ componentId + "'\r\n" + "and  decode(MATERIAL_ID,'" + materialId + "',\r\n"
							+ "decode(RESULT_TYPE_ID,'" + resultTypeId + "',1,0),0) = 0";
					formSaveDao.updateStructTable(sql, "fg_s_manualresultsref_pivot",
							Arrays.asList("MATERIAL_ID", "RESULT_TYPE_ID"), "component_id", componentId);
				}
			}
		}
	}

	private String createTemporaryMaterials(String manualMaterialName, String project_id, String mw, String userId) {
			String materialtype_id=formDao.getFromInfoLookup("MaterialType",LookupType.NAME,"Impurity","id");
			String materialTempStatus_id =formDao.getFromInfoLookup("MaterialStatus",LookupType.NAME,"Temporary","id");
			String defaultMwUomId = "";
			List<String> uomSelectedList = formDao.getFromInfoLookupElementData("UOM", LookupType.NAME, "gr/mole", "ID");
			for (String uomSelectedId : uomSelectedList) {
				Map<String, String> uomData = formDao.getFromInfoLookupAll("UOM", LookupType.ID, uomSelectedId);
				if (uomData.get("UOMTYPENAME").equalsIgnoreCase("molar weight")) {
					defaultMwUomId = uomSelectedId;
					break;
				}
			}
			String newFormId = formSaveDao.getStructFormId("InvItemMaterial");
			String sql_ = "insert into FG_S_INVITEMMATERIAL_PIVOT"
					+ " (FORMID,TIMESTAMP,CREATION_DATE,CLONEID,TEMPLATEFLAG,CHANGE_BY,CREATED_BY,SESSIONID,ACTIVE,FORMCODE_ENTITY,FORMCODE,"
					+ "invitemmaterialname,project_id,materialprotocoltype,materialtype_id,sourceProjectId,status_id,mw,MW_UOM_ID)\n"
					+ " VALUES ('" + newFormId + "',SYSDATE,SYSDATE,null,null,'" + userId + "','" + userId
					+ "',null,1,'InvItemMaterial','InvItemMaterial','"+manualMaterialName+"','"+project_id+"','Chemical Material','"+materialtype_id+"',NULL,'" + materialTempStatus_id + "','"+mw+"','"+defaultMwUomId+"')";

			formSaveDao.insertStructTableByFormId(sql_, "FG_S_InvItemMaterial_PIVOT", newFormId);
			return newFormId;
	}

	//kd 05032018 use this instead method for Map getOrDefault from Java 8 
	private String getOrDefaultImp(String val, String defaultValue) {
		if (val == null) {
			return defaultValue;
		}
		return val;
	}

	private int doMultiSamples(String formId, int sampleQuantity, Map<String, String> elementValueMap, String userId,
			List<String> listOfSamples) {
		generalUtilLogger.logWriter(LevelType.DEBUG, "Executing a Multi Sample Creation", ActivitylogType.SaveEvent,
				formId);
		String formCode = formDao.getFormCodeBySeqId(formId);
		//get table name
		Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
		String formCodeEntity = generalUtil.getNull(form.getFormCodeEntity(), formCode);
		String table = "FG_S_" + formCodeEntity + "_PIVOT";

		for (int i = 0; i < sampleQuantity - 1; i++) {//the first one is created by the save event
			String cloneFormId = formSaveDao.cloneStructTable(formId);
			String nextFormNumberId = formIdCalc.getNextId(elementValueMap, formCode, formId, userId, table);
			generalUtilLogger.logWriter(LevelType.DEBUG, "Implicitly Creating Sample Number: " + (i + 1) + ". FORMID:"
					+ cloneFormId + "; FORMNUMBERID: " + nextFormNumberId, ActivitylogType.SaveEvent, formId);
			if (nextFormNumberId.equals("")) {
				return -1;
			}
			String sql_ = String.format(
					" update %1$s set ACTIVE = 1, FORMNUMBERID = '%2$s', SAMPLENAME ='%2$s'  where FORMID =  %3$s",
					table, nextFormNumberId, cloneFormId);
			List<String> colList = Arrays.asList("ACTIVE", "FORMNUMBERID", "SAMPLENAME");
			formSaveDao.updateStructTableByFormId(sql_, table, colList, cloneFormId);
			formId = cloneFormId;
			listOfSamples.add(formId);
		}
		return 1;
	}

	private void createExperimentFromSerias(Map<String, String> elementValueMap, String idx, String userId) {
		String protocolTypeId = generalDao
				.selectSingleString("select PROTOCOL_ID from FG_S_ExperimentSeries_V where EXPERIMENTSERIES_ID = '"
						+ elementValueMap.get("parentId") + "'");
		String protocolType = formDao.getFromInfoLookup("ProtocolType", LookupType.ID, protocolTypeId, "name");

		if (protocolType.equals("Formulation")) {
			String newExpId = creteBaseExperiment(elementValueMap.get("parentId"), idx, userId);

			String expIndexeFormId = generalDao.selectSingleString(
					"select ss.EXPINDEXINSERIES_ID from FG_S_ExpParameterRef_ALL_V ss where ss.EXPERIMENTINDEX = " + idx
							+ " and ss.parentid = '" + elementValueMap.get("parentId")
							+ "' and ss.ACTIVE = '1' and ss.SESSIONID is null");

			String wherePart = "and r.EXPINDEXINSERIES_ID = '" + expIndexeFormId + "'";

			cloneExperiment.insertRef("ExpParameterRef", elementValueMap.get("parentId"), userId, "EXPERIMENTSERIES",
					newExpId, null, null, wherePart);
			wherePart = " and r.formid in (select ss.formulantref_id from FG_I_SERIES_INDX_DATA_V_SS ss where ss.experiment_id = "
					+ newExpId + ")";
			cloneExperiment.insertRef("formulantref", elementValueMap.get("parentId"), userId, "EXPERIMENTSERIES",
					newExpId, "expFr", "fg_s_batchSelect_pivot", wherePart);

		}
	}

	private String creteBaseExperiment(String expSeriesId, String idx, String userId) {
		Map<String, String> map = new HashMap<String, String>();
		//map.put("experimentId_in", elementValueMap.get("EXPERIMENT_ID"));
		map.put("series_id_in", expSeriesId);
		map.put("index_in", String.valueOf(idx));
		map.put("userid_in", userId);
		String experimentId = generalDao.callPackageFunction("FG_ADAMA", "CREATE_SERIES_INDX_DATA_SS", map);

		//set formNumberId- Experiment number
		Map<String, String> experimentValueMap = generalDao
				.sqlToHashMap(String.format("select * from FG_S_EXPERIMENT_PIVOT where formid = '%1$s'", experimentId));
		String nextFormNumberId = formIdCalc.getNextId(experimentValueMap, "Experiment", experimentId, userId,
				"FG_S_EXPERIMENT_PIVOT");

		if (!nextFormNumberId.isEmpty()) {
			generalUtilLogger.logWriter(LevelType.DEBUG,
					generalUtil.mapToString("set experiment name - elementValueMap before event:", experimentValueMap),
					ActivitylogType.SaveEvent, experimentId);
			String statusId = formDao.getFromInfoLookup("ExperimentStatus", LookupType.NAME, "Planned", "id");

			String sql = String.format(
					"update  FG_S_EXPERIMENT_PIVOT set formNumberId = '%1$s', experimentName = '%1$s',estimatedStartDate = TO_CHAR(sysdate,'"
							+ generalUtil.getConversionDateFormat() + "') ,lastModifDate = TO_CHAR(sysdate,'"
							+ generalUtil.getConversionDateFormat() + "') ,creationdatetime = TO_CHAR(sysdate,'"
							+ generalUtil.getConversionDateFormat()
							+ "'), status_id = %2$s , Active = 1, created_by = %4$s, creator_id =  %4$s, creation_date = sysdate, experimentversion = '01',timestamp=sysdate,change_by='"+userId+"' where formid = %3$s",
					nextFormNumberId, statusId, experimentId, userId);
			formSaveDao.updateStructTableByFormId(sql, "FG_S_EXPERIMENT_PIVOT",
					Arrays.asList("formNumberId", "experimentName", "estimatedStartDate", "lastModifDate",
							"creationdatetime", "status_id", "Active", "created_by", "creation_date",
							"experimentversion","timestamp","change_by"),
					experimentId);
			//formSaveDao.updateSingleString(sql);//ta 290718 changed for search
		}
		return experimentId;
	}

	private void convertMolFilesDocument(String formId, String formCode) {
		if (generalUtilFormState.isOpenTransaction(formId)) {
			List<String> formCodeList = generalUtilFormState.getFormCodeTransactionList(formId, formCode);
			if (formCodeList.contains("Document")) {
				//System.out.println("lets convert");
				String sql_ = "select t.DOCUMENTUPLOAD from fg_s_document_all_v t where t.PARENTID = '" + formId
						+ "' and lower(t.FILE_NAME) like '%.mol' and (t.file_display_id is null or t.file_chem_id is null)";
				List<String> fileIdList = generalDao.getListOfStringBySql(sql_);
				for (String fileId : fileIdList) {
					try {
						String molData = generalDao.getSingleStringFromBlob(
								"select file_content file_id from fg_files where file_id = '" + fileId + "'");
						JSONObject jo = chemDao.saveDocData(molData);
						String displayId_ = (String) jo.get("imgId");
						String checmId = (String) jo.get("chemId");
						formSaveDao.updateSingleStringInfoNoTryCatch("update fg_files t set t.file_display_id = '" + displayId_
								+ "', t.file_chem_id = '" + checmId + "' where t.file_id = '" + fileId + "'");
					} catch (Exception e) {
						// do nothing if failure we need to fix it in sched job
						generalUtilLogger.logWriter(LevelType.WARN,
								"Failure in creating img/search to fileId=" + fileId, ActivitylogType.SaveEvent,
								formId);
					}
				}
			}
		}

	}

	private boolean isDocumentChanged(String formId, String formCode) {
		boolean toReturn = false;
		if (generalUtilFormState.isOpenTransaction(formId)) {
			List<String> formCodeList = generalUtilFormState.getFormCodeTransactionList(formId, formCode);
			if (formCodeList.contains("Document")) {
				//if contentChange=1 the document has been changed (else contentChange= 0)
				String sql = "select distinct max(t.contentchange) from fg_s_document_pivot t where t.parentid = '"
						+ formId + "' and nvl(active,1) = 1";
				String contentChange = generalDao.selectSingleStringNoException(sql);
				if (generalUtil.getNull(contentChange).equals("1")) {
					toReturn = true;
				}
			}
		}
		return toReturn;
	}

	private String getNextVersion(String formCode, String formId) {
		String currentVersion = formDao.getFromInfoLookup(formCode, LookupType.ID, formId, formCode + "Version");
		if (currentVersion != null && !currentVersion.isEmpty()) {
			return String.format("%02d", Integer.parseInt(currentVersion) + 1);
		}
		return "01";
	}
	
	private void insertSummaryIntoObservation(String actionId, String summaryId)
	{
		//get summary and observation
		String sourceObservID = generalDao.selectSingleString(
				"select a.observation from fg_s_action_pivot a where a.formid='"+ actionId + "' ");
		String htmlSourceObserData = generalDao.getSingleStringFromClob(
				"select file_content from FG_RICHTEXT where file_id ='" + sourceObservID + "'");
		String htmlData = generalDao.getSingleStringFromClob(
				"select file_content from FG_RICHTEXT where file_id ='" + summaryId + "'");
		
		String newhtmlData = htmlSourceObserData + "<br/>" + getMinimalHTML(htmlData);		
		String plainText = newhtmlData.replaceAll("\\<.*?\\>", " ");
		
		//create rich text clob
		String newFileId = formSaveDao.getStructFileId("Action.observation from SelfTest");
		uploadFileDao.saveRichText(newFileId, newhtmlData, plainText, true);
		
		//update action
		System.out.println("newFileId=" + newFileId);
		String sql = "update fg_s_action_pivot t set t.observation='" + newFileId + "' where formid = '"
				+ actionId + "'";

		formSaveDao.updateSingleStringInfoNoTryCatch(sql);
	}
	
	private String getMinimalHTML(String htmlData) {

		System.out.println(htmlData);
		String text = htmlData;
		// text = text.replaceAll("\\<.*?\\>", "");
		// text = text.replaceAll("<[^>]*>", " ");
		text = text.replaceAll("\t", "");
		text = text.replaceAll("\n", "");
		text = text.replaceAll("<table.*?</table>", "");
		text = text.replaceAll("<[/]?img[^>]*>", "");

		System.out.println(text);
		return text;
	}
	
	/**
	 * "Taro develop" 
	 * This function call skyline_api_sp_create_new_run (DB function) that:
	 * 1) create run in skyline.
	 * 2) update fg_s_ExperStbResult_pivot with skyline result
	 * @param formId
	 * @param startDate - in dd/MM/yyyy format or ddMMyyyy
	 * @param startTime - in hh24:mi format
	 * TODO kosta this function need also sp_id list / stage_id list for the run creation
	 */
	private void createSkylineRun(String formId, String startDate, String startTime, String userId, String spList) {
		Map<String,String> map = new HashMap<String,String>();
		map.put("formId_in", formId);
		map.put("start_date_in",generalUtil.getNull(startDate).replace("/", ""));
		map.put("start_time_in", startTime);
		map.put("userId_in", startTime);
		map.put("sp_list_in", spList);
		
		String outVal = generalDao.callPackageFunction("", "skyline_api_sp_create_new_run", map);
		System.out.println("skyline_api_sp_create_new_run: outVal=" + outVal);
	}
	
	private String removeEmptyRowInEditTable(String formId, String selectFormCode, String csvColName, String selectFormIdColName, boolean isSharedTable)
	{		
		String update = "";
		String sql = "";
		
		if(isSharedTable){
			String selectFormId= generalDao.selectSingleStringNoException(
			String.format("select %1$s from fg_s_%2$s_v where parentid ='%3$s' and sessionid is null and active = 1"
					,selectFormIdColName, selectFormCode, formId));				
			String csvIds = generalDao.selectSingleStringNoException(
				String.format("select %1$s from fg_s_%2$s_v where parentid ='%3$s' and %4$s = '%5$s' and sessionid is null and active = 1",
						csvColName, selectFormCode, formId, selectFormIdColName, selectFormId));
			csvIds= generalUtil.getNull(csvIds).equals("")?"": csvIds.replace(",-1", "").replace("-1,", "").replace("-1", "");
			sql = String.format("update FG_S_%1$s_PIVOT  set %2$s = '%3$s' where formId = '%4$s' and parentid ='%5$s' and active=1 and sessionid is null",
					selectFormCode,csvColName, csvIds, selectFormId, formId );
			update = formSaveDao.updateSingleStringInfoNoTryCatch(sql);
		}else{	
			List<String> selectFormIds= generalDao.getListOfStringBySql(
					String.format("select %1$s from fg_s_%2$s_v where parentid ='%3$s' and sessionid is null and active = 1 and (%4$s ='-1' or %4$s is null)"
							,selectFormIdColName, selectFormCode, formId, csvColName));
			for(String selectFormId : selectFormIds){
				sql = String.format("delete from FG_S_%1$s_PIVOT where formId = '%2$s' and parentid ='%3$s'",
						selectFormCode, selectFormId, formId );
				update = formSaveDao.updateSingleStringInfoNoTryCatch(sql);
			}

		}
		return update;
	}
	
	private String resetDefaultMassBalanceValue(String experimentId)
	{
		String update = "";
		String sql = "";
		
		//Step
		sql = "update FG_S_STEP_PIVOT t set t.chkcharactermassbalance = 0 where t.experiment_id = '"+experimentId
						+"' and nvl(t.chkcharactermassbalance,0) = 1";
		update = formSaveDao.updateStructTableByFormId(sql, "FG_S_STEP_PIVOT",Arrays.asList("experiment_id"), experimentId);
		
		//Experiment
		sql = "update FG_FORMADDITIONALDATA t \n"
				  +" set t.value = '0' \n"
				  +"where ((t.entityimpcode = 'chkCharacterMassBalance' and t.value = '1') \n"
				  +"        or (t.entityimpcode = 'chkCharacterMassBalance2' and t.value = '1') \n"
				  +"        or (t.entityimpcode = 'chkCharacterMassBalance3' and t.value = '1') \n"
				  +"      ) \n"
				  +"and t.parentid = '"+experimentId+"' ";	
		update = formSaveDao.updateAdditinalData(sql, Arrays.asList("parentid"), experimentId);
		
		return update;
	}
	
	private  void updateRunsPlanningTable(String experimentID, String stepId, String userId,String currentStatusName)
	{
		boolean updated = false;
		String sql = "";
		
		long start = System.currentTimeMillis();
		
		/* update RunsPlanning table in case there is at least one run exists for current experiment */ 
		sql = "select count(*) as isExists from FG_S_EXPRUNPLANNING_PIVOT t where t.experimentid = '"+experimentID+"'";
		String expRunCount = generalDao.selectSingleString(sql);
		
		/* check if there is data of current step exists in the RunsPlanning table */
		sql = "select count(*) as isExists from FG_S_EXPRUNPLANNING_PIVOT t where t.stepid = '"+stepId+"'\n and t.experimentid = '"+experimentID+"'";
		String stepRunCount = generalDao.selectSingleString(sql);
		if(currentStatusName.equals("Planned")){
			if(!expRunCount.equals("0"))
			{
				String defaultRateType = "volume";
				/* in case there is no data for current step, then insert data */
				if(stepRunCount.equals("0"))
				{				
					sql = "insert into FG_S_EXPRUNPLANNING_PIVOT (FORMID, TIMESTAMP, CREATION_DATE, CHANGE_BY, CREATED_BY, ACTIVE, FORMCODE_ENTITY, FORMCODE, \n" 
						 +" 										MATERIALREFID, PARAMETERREFID, STEPID, EXPERIMENTID, RUNNUMBER, RUNSTATUSID, INVITEMMATERIALID, \n"
						 + "										PARAMETERID, LIMITINGAGENT, MATERIALRATETYPE, MATERIALRATEUOM, PARAMETERUOM, RETENTIONTIMEUOM)  \n"
						 +" select distinct  r.FORMID, sysdate, sysdate, '" + userId + "', '" + userId + "', '1' ,'ExpRunPlanning', 'ExpRunPlanning', \n"
						 +"  		t2.MATERIALREF_ID, t2.PARAMREF_ID, t2.STEP_ID, r.EXPERIMENTID, r.RUNNUMBER, r.RUNSTATUSID \n"
						 + "		,t2.INVITEMMATERIAL_ID, t2.PARAMETER_ID, t2.LIMITINGAGENT, '"+defaultRateType+"' \n"
						 + "		,t2.MATERIALRATEUOM \n"
						 + "        ,t2.PARAMETERUOM \n"
						 + " 		,t2.RETENTIONTIMEUOM \n"
	 					 +" from (  \n"
	 					 +" select s.STEP_ID,  s.EXPERIMENT_ID ,t.MATERIALREF_ID, null as PARAMREF_ID \n"
	 					 +"        ,t.INVITEMMATERIAL_ID, null as PARAMETER_ID, nvl(t.LIMITINGAGENT,0) as LIMITINGAGENT \n"
	 					 + "	   ,fg_get_uom_by_uomtype(fg_get_uomtype_by_ratetype('"+defaultRateType+"')) as MATERIALRATEUOM \n"
	 					 + "	   ,null as PARAMETERUOM \n"
						 + " 	   ,fg_get_uom_by_uomtype('time') as RETENTIONTIMEUOM \n"
	 					 +" from fg_s_step_v s, fg_s_materialref_v t \n"
	 					 +" where t.PARENTID = s.STEP_ID  \n"
	 					 +" and t.ACTIVE = 1 \n"
	 					 +" and t.sessionId is null \n"
	 					 +" and lower(t.TABLETYPE) in ('reactant','solvent')  \n"
	 					 +" and lower(nvl(s.PREPARATION_RUN,'na')) = lower('run')  \n"
	 					 +" and s.step_id = '"+stepId+"' \n"
	 					 +" and s.EXPERIMENT_ID = '"+experimentID+"'   \n"
	 					 +" union all  \n"
	 					 +" select s1.STEP_ID, s1.EXPERIMENT_ID, null as MATERIALREF_ID, t1.PARAMREF_ID \n"
	 					 +"		  ,null as INVITEMMATERIAL_ID, t1.PARAMETER_ID, null as LIMITINGAGENT, null as MATERIALRATEUOM \n"
	 					 +"	      ,fg_get_Uom_by_uomtype(t1.UOMTYPENAME) as PARAMETERUOM \n"
						 +" 	  ,fg_get_Uom_by_uomtype('time') as RETENTIONTIMEUOM \n"
	 					 +" from fg_s_step_v s1, fg_s_paramref_all_v t1 \n"
	 					 +" where t1.PARENTID = s1.STEP_ID  \n"
	 					 +" and t1.ACTIVE = 1 \n"
	 					 +" and t1.sessionId is null \n"
	 					 +" and lower(nvl(s1.PREPARATION_RUN,'na')) = lower('run')  \n"
	 					 +" and s1.step_id = '"+stepId+"' \n"
	 					 +" and s1.EXPERIMENT_ID = '"+experimentID+"'   \n"
	 					 +" )t2, fg_s_exprunplanning_v r  \n"
	 					 +" where t2.EXPERIMENT_ID = r.EXPERIMENTID";
						
					formSaveDao.insertStructTableByFormId(sql,"FG_S_EXPRUNPLANNING_PIVOT", stepId);
				}
				else // else update exists step
				{
					
					/* UPDATE MATERIAL REF */
					//delete materialRef from run 
					sql = "select t.materialrefid from FG_S_EXPRUNPLANNING_PIVOT t \n"
									   +" where not exists (select t1.materialref_id \n"
			          								   +" from fg_s_materialref_v t1 \n"
			          								   +" where t1.materialref_id = t.materialrefid \n"
			          								   +" and t1.TABLETYPE != 'Product' \n"
			          								   +" and t1.active = 1 \n"
			          								   +" and t1.sessionId is null \n"
			          								   +" and t1.PARENTID = '"+stepId+"' \n"
			          								   +" ) \n"
									   +" and t.materialrefid is not null  \n"
									   +" and t.sessionId is null \n"
									   +" and t.stepid = '"+stepId+"'";
					List<String> matRefId_listForDelete = generalDao.getListOfStringBySql(sql);
					for(String matrefid: matRefId_listForDelete)
					{
						formSaveDao.deleteStructTableByFormId("delete from FG_S_EXPRUNPLANNING_PIVOT t where t.materialrefid = '"+matrefid+"'","FG_S_EXPRUNPLANNING_PIVOT", stepId);
						updated = true;
					}
				
					//insert materialRef into run
					sql = "select t.materialref_id \n"
						   + " from fg_s_materialref_v t \n"
						   + " where not exists (select t1.materialrefid \n"
						   					  + " from FG_S_EXPRUNPLANNING_PIVOT t1 \n"
	      								      + " where t1.materialrefid = t.materialref_id \n"
	      								      + " and t1.materialrefid is not null \n"
	      								      + " and t1.stepid = '"+stepId+"' \n"
	      								      +" and t1.sessionId is null \n"
	      								      + " ) \n"
						   + " and t.TABLETYPE != 'Product' \n"
						   + " and t.active = 1 \n"
						   + " and t.sessionId is null \n"
						   + " and t.PARENTID = '"+stepId+"'";	
					List<String> matRefId_listForInsert = generalDao.getListOfStringBySql(sql);
					for(String matrefid: matRefId_listForInsert)
					{
						String sqlInsert = "insert into FG_S_EXPRUNPLANNING_PIVOT "
								+ "(FORMID, TIMESTAMP, CREATION_DATE, CHANGE_BY, CREATED_BY, ACTIVE, FORMCODE_ENTITY, FORMCODE, MATERIALREFID, STEPID, EXPERIMENTID, RUNNUMBER, RETENTIONTIME, RUNSTATUSID,"
								+ " INVITEMMATERIALID, MATERIALRATETYPE, MATERIALRATEUOM, RETENTIONTIMEUOM"
								//+ ", LIMITINGAGENT" // LA will be updated later down in this function
								+ ") \n"
								+ " select distinct t.formid, sysdate, sysdate, '" + userId + "', '" + userId + "', '1' ,t.FORMCODE_ENTITY, t.FORMCODE, \n"
								+ " 		'"+matrefid+"', t.STEPID, t.EXPERIMENTID, t.RUNNUMBER, t.RETENTIONTIME, t.RUNSTATUSID \n"
								+ "			,m.INVITEMMATERIAL_ID, '"+defaultRateType+"' \n"
								+ "	       ,fg_get_uom_by_uomtype(fg_get_uomtype_by_ratetype('"+defaultRateType+"')) as MATERIALRATEUOM \n"
								+ "        ,t.RETENTIONTIMEUOM \n"
								+ " from FG_S_EXPRUNPLANNING_PIVOT t, fg_s_materialref_v m \n"
								+ " where t.stepid = m.PARENTID \n"
								+ " and m.materialref_id = '"+matrefid+"' \n"
								+ " and t.stepid = '"+stepId+"'";
			
						formSaveDao.insertStructTableByFormId(sqlInsert,"FG_S_EXPRUNPLANNING_PIVOT", stepId);
						updated = true;
					}
					
					// if there was insertion/deletion in Reactions tables - reset previously calculated values(rates)
					if(updated)
					{
						String sqlUpdate = "update FG_S_EXPRUNPLANNING_PIVOT t set t.MATERIALREFVALUE = '' \n"
								+" where  t.STEPID = '"+stepId+"'\n"
								+" and t.materialrefid is not null \n"
								+" and t.sessionid is null and t.active = 1";
						formSaveDao.updateStructTableByFormId(sqlUpdate, "FG_S_EXPRUNPLANNING_PIVOT",Arrays.asList("MATERIALREFVALUE"), stepId); 
					}
					
					//update invItemMaterialID, reset rate and equivalent when material is updated to another one in already exists row
					sql = "select t.materialref_id, t.INVITEMMATERIAL_ID \n"
							   + " from fg_s_materialref_v t \n"
							   + " where not exists (select t1.materialrefid \n"
							   					  + " from FG_S_EXPRUNPLANNING_PIVOT t1 \n"
		      								      + " where t1.materialrefid = t.materialref_id \n"
							   					  + " and t1.invitemmaterialid = t.INVITEMMATERIAL_ID \n"
		      								      + " and t1.materialrefid is not null \n"
		      								      + " and t1.stepid = '"+stepId+"' \n"
		      								      +" and t1.sessionId is null \n"
		      								      + " ) \n"
							   + " and t.TABLETYPE != 'Product' \n"
							   + " and t.active = 1 \n"
							   + " and t.sessionId is null \n"
							   + " and t.PARENTID = '"+stepId+"'";	
					List<Map<String, Object>> matRefId_mapForUpdate = generalDao.getListOfMapsBySql(sql);
					for (Map<String, Object> obj : matRefId_mapForUpdate) 
					{
						String sqlUpdate = "update FG_S_EXPRUNPLANNING_PIVOT t \n"
								+ " set t.materialrefequivalent = '', \n"
								+ "     t.materialrefvalue = '', \n"
								+ " 	t.invitemmaterialid = '"+obj.get("INVITEMMATERIAL_ID")+"' \n"
								+ " where t.materialrefid = '"+obj.get("materialref_id")+"' \n"
								+ "and t.stepid = '"+stepId+"'";
						formSaveDao.updateStructTableByFormId(sqlUpdate, "FG_S_EXPRUNPLANNING_PIVOT",Arrays.asList("MATERIALREFVALUE"), stepId); 
					}
					
					// update Limiting Agent if it changed
					sql = "select max(t.materialref_id) as materialref_id \n"
							   + " from fg_s_materialref_v t \n"
							   + " where not exists (select t1.materialrefid \n"
							   					  + " from FG_S_EXPRUNPLANNING_PIVOT t1 \n"
		      								      + " where t1.materialrefid = t.materialref_id \n"
		      								      + " and t1.materialrefid is not null \n"
		      								      + " and nvl(t1.limitingagent,0) = 1 \n"
		      								      + " and t1.stepid = '"+stepId+"' \n"
		      								      +" and t1.sessionId is null \n"
		      								      + " ) \n"
							   + " and nvl(t.limitingagent,0) = 1 \n"
		      				   + " and t.TABLETYPE != 'Product' \n"
							   + " and t.active = 1 \n"
							   + " and t.sessionId is null \n"
							   + " and t.PARENTID = '"+stepId+"'";	
					String newLimitingAgentMaterial = generalUtil.getNull(generalDao.selectSingleString(sql));
					if(!newLimitingAgentMaterial.equals(""))
					{
						// reset calculated rates and LA 
						String sqlUpdate = "update FG_S_EXPRUNPLANNING_PIVOT t \n"
								+ " set t.MATERIALREFVALUE = '', \n"
								+ " 	t.limitingagent = 0 \n"
								+"  where t.materialrefid is not null \n"
								+ " and t.stepid = '"+stepId+"'";
						formSaveDao.updateStructTableByFormId(sqlUpdate, "FG_S_EXPRUNPLANNING_PIVOT",Arrays.asList("MATERIALREFVALUE","limitingagent"), stepId);
						
						// reset equivalent of new limited material
						String sqlUpdateLA = "update FG_S_EXPRUNPLANNING_PIVOT t \n"
								+ " set t.materialrefequivalent = '', \n"
								+ " 	t.limitingagent = 1 \n"
								+ " where t.materialrefid = '"+newLimitingAgentMaterial+"' \n"
								+ " and t.stepid = '"+stepId+"'";
						formSaveDao.updateStructTableByFormId(sqlUpdateLA, "FG_S_EXPRUNPLANNING_PIVOT",Arrays.asList("materialrefequivalent","limitingagent"), stepId);
					}
					else
					{
						// in case LA was removed/unchecked at all from reactants - update Runs table also
						sql = "select max(t1.materialrefid) as materialrefid \n"
								   + " from FG_S_EXPRUNPLANNING_PIVOT t1 \n"
							   + " where not exists (select t.materialref_id \n"
	                  						   + " from fg_s_materialref_v t \n"
	                  						   + " where t1.materialrefid = t.materialref_id \n"
	                  						   + " and  nvl(t.limitingagent,0) = 1 \n"
	                  						   + " and t.TABLETYPE != 'Product' \n"
	                  						   + " and t.sessionId is null \n"
	                  						   + " and t.active = 1 \n"
	                  						   + " and t.PARENTID = '"+stepId+"' \n"
	                  						   + " ) \n"
							   + " and nvl(t1.limitingagent,0) = 1 \n"
							   + " and t1.materialrefid is not null \n"
							   + " and t1.stepid = '"+stepId+"'";	
						String oldLimitingAgentMaterial = generalUtil.getNull(generalDao.selectSingleString(sql));
						if(!oldLimitingAgentMaterial.equals(""))
						{
							// reset calculated rates and LA 
							String sqlUpdate = "update FG_S_EXPRUNPLANNING_PIVOT t \n"
									+ " set t.MATERIALREFVALUE = '', \n"
									+ " 	t.limitingagent = 0 \n"
									+"  where t.materialrefid is not null \n"
									+ " and t.stepid = '"+stepId+"'";
							formSaveDao.updateStructTableByFormId(sqlUpdate, "FG_S_EXPRUNPLANNING_PIVOT",Arrays.asList("MATERIALREFVALUE","limitingagent"), stepId);						
						}
					}
				}
			}
		}
		
		/* ********************** UPDATE PARAMETER REF ********************** */
		if(currentStatusName.equals("Planned")||currentStatusName.equals("Active")){
			/* in case there is data for current step, then update data */
			if(!expRunCount.equals("0") && !stepRunCount.equals("0")){
				//delete from run
				sql = "select t.parameterrefid \n"
						  +" from FG_S_EXPRUNPLANNING_PIVOT t \n"
						  +" where not exists (select t1.paramref_id \n"
						  				  +" from fg_s_paramref_v t1 \n"
		          						  +" where t1.paramref_id = t.parameterrefid \n"
		          						  +" and t1.active = 1 \n"
		          						  +" and t1.sessionId is null \n"
		          						  +" and t1.PARENTID = '"+stepId+"' \n"
		          						  +" ) \n"
						  +" and t.parameterrefid is not null  \n"
						  +" and t.stepid = '"+stepId+"'";
				
				List<String> paramRefId_listForDelete = generalDao.getListOfStringBySql(sql);
				for(String paramrefid: paramRefId_listForDelete)
				{
					formSaveDao.deleteStructTableByFormId("delete from FG_S_EXPRUNPLANNING_PIVOT t where t.parameterrefid = '"+paramrefid+"'","FG_S_EXPRUNPLANNING_PIVOT", stepId);
				}
				
				//insert into run 
				sql = "select t.paramref_id \n"
						  +" from fg_s_paramref_v t \n"
						  +" where not exists (select t1.parameterrefid \n"
		                  						  +" from FG_S_EXPRUNPLANNING_PIVOT t1 \n"
		                  						  +" where t1.parameterrefid = t.paramref_id \n"
		                  						  +" and t1.parameterrefid is not null \n"
		                  						  +" and t1.sessionId is null \n"
		                  						  +" and t1.stepid = '"+stepId+"' \n"
		                  						  +" ) \n"
						  +" and t.sessionId is null \n"
						  +" and t.active = 1 \n"
						  +" and t.PARENTID = '"+stepId+"'";	
				
				List<String> paramRefId_listForInsert = generalDao.getListOfStringBySql(sql);
				for(String paramrefid: paramRefId_listForInsert)
				{
					String sqlInsert = "insert into FG_S_EXPRUNPLANNING_PIVOT "
							+ "(FORMID, TIMESTAMP, CREATION_DATE, CHANGE_BY, CREATED_BY, ACTIVE, FORMCODE_ENTITY, FORMCODE, PARAMETERREFID, STEPID, EXPERIMENTID, RUNNUMBER, RETENTIONTIME, RUNSTATUSID, PARAMETERID, PARAMETERUOM, RETENTIONTIMEUOM) \n"
							+ " select distinct t.formid, sysdate, sysdate, '" + userId + "', '" + userId + "', '1' ,t.FORMCODE_ENTITY, t.FORMCODE, \n"
							+ " 		'"+paramrefid+"', t.STEPID, t.EXPERIMENTID, t.RUNNUMBER, t.RETENTIONTIME, t.RUNSTATUSID, p.PARAMETER_ID \n"
							+"	       ,fg_get_Uom_by_uomtype(p.UOMTYPENAME) as PARAMETERUOM \n"
							+" 	  	   ,t.RETENTIONTIMEUOM \n"
							+ " from FG_S_EXPRUNPLANNING_PIVOT t, fg_s_paramref_all_v p"
							+ " where t.stepid = p.PARENTID \n"
							+ " and p.paramref_id = '"+paramrefid+"' \n"
							+ " and t.stepid = '"+stepId+"'";
		
					formSaveDao.insertStructTableByFormId(sqlInsert,"FG_S_EXPRUNPLANNING_PIVOT", stepId);
				}
				
				//update parameterID when parameter is updated to another one in already exists row
				sql = "select t.paramref_id, t.PARAMETER_ID \n"
						  +" from fg_s_paramref_v t \n"
						  +" where not exists (select t1.parameterrefid \n"
		                  						  +" from FG_S_EXPRUNPLANNING_PIVOT t1 \n"
		                  						  +" where t1.parameterrefid = t.paramref_id \n"
		                  						  +" and t1.parameterid = t.PARAMETER_ID \n"
		                  						  +" and t1.sessionId is null \n"
		                  						  +" and t1.stepid = '"+stepId+"' \n"
		                  						  +" ) \n"
						  +" and t.sessionId is null \n"
						  +" and t.active = 1 \n"
						  +" and t.PARENTID = '"+stepId+"'";
				List<Map<String, Object>> paramRefId_listForUpdate = generalDao.getListOfMapsBySql(sql);
				for (Map<String, Object> obj : paramRefId_listForUpdate) 
				{
					String sqlUpdate = "update FG_S_EXPRUNPLANNING_PIVOT t \n"
							+ " set t.parameterrefvalue = '', \n"
							+ " 	t.parameterid = '"+obj.get("PARAMETER_ID")+"' \n"
							+ " where t.parameterrefid = '"+obj.get("paramref_id")+"' \n"
							+ "and t.stepid = '"+stepId+"'";
					formSaveDao.updateStructTableByFormId(sqlUpdate, "FG_S_EXPRUNPLANNING_PIVOT",Arrays.asList("parameterrefvalue"), stepId); 
				}
			}
		}
		long time = System.currentTimeMillis() - start;
		System.out.println("TIME OF UPDATE RUNS PLANNING TABLE: " + time + " milliseconds");
	}

}
