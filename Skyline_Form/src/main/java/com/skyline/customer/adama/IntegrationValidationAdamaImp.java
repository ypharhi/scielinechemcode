package com.skyline.customer.adama;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

//import com.ibm.db2.jcc.t4.sb;
import com.skyline.form.bean.LookupType;
import com.skyline.form.bean.ValidationCode;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilFormState;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.form.service.IntegrationValidation;

import oracle.jdbc.OracleTypes;

@Service
public class IntegrationValidationAdamaImp implements IntegrationValidation {

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private GeneralDao generalDao;

	@Autowired
	private FormDao formDao;
	
	@Autowired
	private CommonFunc commonFunc;

	@Autowired
	protected GeneralUtilLogger generalUtilLogger;

	@Autowired
	private GeneralUtilFormState generalUtilFormState;

	@Value("${systemSynDelim:@syndlm@}")
	private String systemSynonymDelim;

	@Value("${checkMatValidFieldIsChanged:0}")
	private String checkMatValidFieldIsChanged;

	@Value("${jdbc.username}")
	private String DB_USER;

	

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(IntegrationValidationAdamaImp.class);

	/**
	 * Validate obj according to the validationCode throw exception with message match the validationCode 
	 * @param validationCode
	 * @param formCode
	 * @param formId
	 * @param obj
	 * @throws Exception
	 */
	@Override
	public void validate(ValidationCode validationCode, String formCode, String formId, Object validateValueObject,
			StringBuilder sbInfoMessage) throws Exception {

		switch (validationCode) {
			case INVALID_PROJECT_FORM_NUMBER_ID: {
				String REGEX = "[2][56789]\\d{2}|[34567]\\d{3}";
				String formnumberId = validateValueObject.toString().split(",")[0];
				String projectType = validateValueObject.toString().split(",").length > 1
						? validateValueObject.toString().split(",")[1] : "";
				if (projectType.equals("Formulation")) {
					REGEX = "[234567]\\d{3}";//"[2][56789]\\d{2}|[34567]\\d{3}";
				} else {
					REGEX = "[23456789]\\d{3}";//"[89]\\d{3}";
				}
				if (!((String) formnumberId).matches(REGEX)) {
					throw new Exception(getMessage(validationCode, new Object[] { formnumberId, projectType },
							validateValueObject));
				}
				break;
			}
			case PROJECT_NUMBER_DEVIATION: {
				if (((String) validateValueObject).isEmpty()) {
					throw new Exception(
							getMessage(validationCode, new Object[] { validateValueObject }, validateValueObject));
				}
				break;
			}
			case INVALID_SUBPROJECT_FORM_NUMBER_ID: {
				final String REGEX = "[258]\\d{3}[-]\\d{2}";
				if (!((String) validateValueObject).matches(REGEX)) {
					throw new Exception(getMessage(validationCode, validateValueObject));
				}
				break;
			}
			case SUBPROJECT_NUMBER_DEVIATION: {
				if (((String) validateValueObject).isEmpty()) {
					throw new Exception(
							getMessage(validationCode, new Object[] { validateValueObject }, validateValueObject));
				}
				break;
			}
			case INVALID_SUBSUBPROJECT_FORM_NUMBER_ID: {
				final String REGEX = "[258]\\d{3}[-]\\d{2}[-]\\d{2}";
				if (!((String) validateValueObject).matches(REGEX)) {
					throw new Exception(getMessage(validationCode, validateValueObject));
				}
				break;
			}
			case INVALID_MCW_PROJECT: {
				if (!generalUtil.getNull((String) validateValueObject).equals("")) {

					String query = "Select count(*) from FG_S_PROJECT_PIVOT where upper(mcwCodeProject) = upper('"
							+ ((String) validateValueObject) + "') and FORMID!=" + formId;
					String counter_ = generalDao.selectSingleString(query);
					if (!counter_.equals("0")) {
						throw new Exception(getMessage(validationCode, validateValueObject));
					}
				}

				break;
			}
			case INVALID_MCW_SUBPROJECT: {

				if (!generalUtil.getNull((String) validateValueObject).equals("")) {

					String query = "Select count(*) from FG_S_SUBPROJECT_PIVOT where upper(mcwCode) = upper('"
							+ ((String) validateValueObject) + "') and FORMID!=" + formId;
					String counter_ = generalDao.selectSingleString(query);
					if (!counter_.equals("0")) {
						throw new Exception(getMessage(validationCode, validateValueObject));
					}
				}

				break;
			}
			case INVALID_MATERIAL: {
				throw new Exception(getMessage(validationCode, validateValueObject));
				//			break; 
			}
			case INVALID_BATCHDEFINITION_DUPLICATION: {
				String query = "Select SAMPLENAME,BATCHNAME from FG_S_SAMPLE_ALL_V where BATCH_ID = '"
						+ ((String) validateValueObject) + "' and BATCHDEFINITION = 1 and FORMID!=" + formId;
				List<Map<String, Object>> duplicatedDefinedSample = generalDao.getListOfMapsBySql(query);
				if (!duplicatedDefinedSample.isEmpty()) {
					Map<String, String> allElements = new HashMap<>();
					for (Map.Entry<String, Object> entry : duplicatedDefinedSample.get(0).entrySet()) {
						if (entry.getValue() instanceof String) {
							allElements.put(entry.getKey(),
									(entry.getValue() != null) ? entry.getValue().toString() : "");
						}
					}
					throw new Exception(getMessage(validationCode,
							new Object[] { allElements.get("BATCHNAME"), allElements.get("SAMPLENAME") },
							validateValueObject));
				}
				break;
			}
			case INVALID_CONSUMED_QUANTITY: {
				/*String srcQuantitytableFormCode = formDao.getFormCodeEntityBySeqId((String)validateValueObject);
				String query = "Select QUANTITY from FG_S"+srcQuantitytableFormCode+"ALL_V where FORMID = "+(String)validateValueObject;
				String srcQuantity = generalDao.selectSingleString(query);
				query = "Select QUANTITY from FG_S_"+formCode+"ALL_V where FORMID = "+formId;
				String consumedQuantity =*/
				String[] quantities = ((String) validateValueObject).split(",");
				String srcQuantity = quantities[1];
				String consumedQuantity = quantities[0];
				if (Double.parseDouble(consumedQuantity) > Double.parseDouble(srcQuantity)) {
					String materialName="";
					if(quantities.length==3){
						String matereialFormCode = formDao.getFormCodeBySeqId(quantities[2]);
						materialName = formDao.getFromInfoLookup(matereialFormCode, LookupType.ID, quantities[2], "name");
					}
					throw new Exception(getMessage(validationCode,new Object[]{materialName.isEmpty()?materialName:"Material "+materialName+"</br>"}, validateValueObject));
				}
				break;
			}

			case SPLITED_FAILED: {
				/*String srcQuantitytableFormCode = formDao.getFormCodeEntityBySeqId((String)validateValueObject);
				String query = "Select QUANTITY from FG_S"+srcQuantitytableFormCode+"ALL_V where FORMID = "+(String)validateValueObject;
				String srcQuantity = generalDao.selectSingleString(query);
				query = "Select QUANTITY from FG_S_"+formCode+"ALL_V where FORMID = "+formId;
				String consumedQuantity =*/
				String[] quantities = ((String) validateValueObject).split(",");
				String srcQuantity = quantities[1];
				String consumedQuantity = quantities[0];
				if (Double.parseDouble(consumedQuantity) > Double.parseDouble(srcQuantity)
						|| Double.parseDouble(consumedQuantity) <= (double) 0) {
					throw new Exception(getMessage(validationCode, validateValueObject));
				}
				break;
			}

			case INVALID_EXPERIMENT_NONFAMILIAR_STATUS: {

				List<String> currentUserMaterialIdList = new ArrayList<String>();
				String userName = generalUtil.getSessionUserName();
				if (!userName.equals("system") && !userName.equals("admin")) {// yp 23012018 for ignore IS_INVENTORY_FAMILIAR check
                    Map<String, String> map = new HashMap<>();
					map.put("experimentId_in", (String) validateValueObject);
					map.put("stepId_in", !formId.equals((String) validateValueObject) ? formId : "-1");//if it fired by the step save event the sends also the step id.
					map.put("formCode_in", formCode);
					map.put("materialList_in", "");
					//String isFamiliar = generalDao.callPackageFunction("FG_ADAMA", "IS_INVENTORY_FAMILIAR", map);
					String isFamiliar = generalDao.callPackageFunction("FG_ADAMA", "GET_INVENTORY_UNFAMILIAR_LIST", map);
					if (!generalUtil.getNull(isFamiliar).equals("")) {
						StringBuilder finalRes = new StringBuilder();
						Map<String, List<String>> resMap = new HashMap<String, List<String>>();
						String[] materials = isFamiliar.split("<end>");//80670<user_list>Test Ami<end>197346<user_list>Test Ami,yulyag<end>
						for (String m : materials) {
							String material_id = m.split("<user_list>")[0];
							String materialFormCode = formDao.getFormCodeBySeqId(material_id);
							String materialName = formDao.getFromInfoLookup(materialFormCode, LookupType.ID, material_id, "name");
							String[] userList = m.split("<user_list>")[1].split(",");
							for (String user : userList) {
								if(!resMap.containsKey(user)){
									resMap.put(user,new ArrayList<String>());
								}
								resMap.get(user).add("<a href='#' onClick=\"checkAndNavigate(['" + material_id + "','"+materialFormCode+"'])\" >" + materialName
										+ "</a>");  
								if(resMap.containsKey(userName)){
									currentUserMaterialIdList.add(material_id);
								}
							}
						}
						//always display the logged-in user, the first in the list. 
						if(resMap.containsKey(userName)){
							finalRes.append("</br>");
							finalRes.append(userName);
						    finalRes.append(": ");
						    String value = generalUtil.replaceLast(resMap.get(userName).toString().replaceFirst("\\[", ""), "]", "");
						    finalRes.append(value);
						    resMap.remove(userName);
						}
						for (Map.Entry<String, List<String>> m : resMap.entrySet()) {
							finalRes.append("</br>");
							finalRes.append(m.getKey());
						    finalRes.append(": ");
						    String value = generalUtil.replaceLast(m.getValue().toString().replaceFirst("\\[", ""), "]", "");
						    finalRes.append(value);
						}
						/*sbInfoMessage.append(
								getMessage(validationCode, new Object[] { finalRes }, validateValueObject));*/
						throw new Exception(getMessage(validationCode,
						  new Object[] { finalRes },
						   validateValueObject));
						//throw new Exception(getMessage(validationCode, validateValueObject));
					}
				}
				break;
			}
			case INVALID_STEPSTATUS_EMPTYBATCH: {
				//			Map<String,String> map = new HashMap<>();
				String batchCount = generalDao.selectSingleString(
						"select count(*) from FG_S_MATERIALREF_PIVOT where BATCH_ID is null and TABLETYPE not in ('Product','Solvent') and sessionid is null and active = 1 and PARENTID="
								+ formId);
				if (!generalUtil.getNull(batchCount).isEmpty() && !batchCount.equals("0")) {
					throw new Exception(getMessage(validationCode, new Object[] { "reactants" }, validateValueObject));
				}
				break;
			}
			case INVALID_STEPSFRTATUS_EMPTYBATCH: {
				//			Map<String,String> map = new HashMap<>();
				String table = "select count(*) \r\n" + "from FG_S_FORMULANTREF_PIVOT F,\r\n"
						+ "     FG_S_BATCHSELECT_V B\r\n" + "WHERE F.FORMID = B.PARENTID(+)\r\n"
						+ "and B.BATCH_ID is null \r\n" + "and TABLETYPE!='productMixture' \r\n"
						+ "and f.active = 1 \r\n" + "and f.sessionid is null\r\n" + "and F.Parentid = '" + formId + "'";
				String batchCount = generalDao.selectSingleString(table);
				if (!generalUtil.getNull(batchCount).isEmpty() && !batchCount.equals("0")) {
					throw new Exception(getMessage(validationCode, new Object[] { "formulants" }, validateValueObject));
				}
				break;
			}
			case CHECK_SELEFTEST_VALIDATION: {
				Map<String, String> map = new HashMap<String, String>();
				map.put("stepId", formId);
				String isValid = generalDao.callPackageFunction("FG_ADAMA", "CHECK_SELFTEST_VALIDATION", map);
				if (!generalUtil.getNull(isValid).isEmpty()) {
					throw new Exception(getMessage(validationCode, new Object[] { isValid }, validateValueObject));
				}
				break;
			}
			case NOT_TEMPLATE_APPROVER: {
				throw new Exception(getMessage(validationCode, validateValueObject));
			}
			case INVALID_AI_QUANTITY: {

				int intOfResult = 0;
				String result = generalDao.selectSingleString(String
						.format("select count(*) " + "from fg_i_series_indx_datasession_v t " + "where t.PARENTID=%1$s "
								+ "and t.AI='1' " + "and fg_get_numeric(NVL(t.VALUE, '0'))< =0", formId));

				try {
					intOfResult = Integer.parseInt(result);
				} catch (Exception e) {
					intOfResult = 0;
				}

				if (intOfResult > 0) {
					throw new Exception(getMessage(validationCode, validateValueObject));
				}
				break;
			}

			case CHECK_VALID_PROJECTTYPENAME: {
				if (validateValueObject instanceof Map<?, ?>) {
					Map<String, String> checkMap = new HashMap<String, String>();
					/*
					 ******************************************************************************** This query are used for get data for fill next Map ***************** select distinct 'checkMap.put("' || t.formcode || 'Name' || '", "' || LISTAGG(t.entityimpvalue, ',')  WITHIN GROUP (ORDER BY t.entityimpvalue) over (partition by t.formcode) || '");' as "Name List" from fg_formlastsavevalue t where t.formcode in (select t1.formcode from fg_form t1 where t1.form_type = 'MAINTENANCE' and t1.group_name = 'System') and lower(T.ENTITYIMPCODE) = lower(t.formcode || 'Name');
					 */
					checkMap.put("ProjectTypeName".toLowerCase(), "Chemistry,Formulation");
					checkMap.put("ProtocolTypeName".toLowerCase(), "Analytical,Formulation,Organic,Parametric");
					checkMap.put("StageStatusName".toLowerCase(),
							"Data General Input,Drying Data Definition,Feeding,General Data Input,General Data Input,General Data Input,General Data Input,General Data Input,Interphase Formation,Last Squeezing,Materials Definition,Monitoring,Monitoring,Monitoring,Preparation,Separation,Squeezing,Starting Mixture Definition,Washing,Wet Cake");
					checkMap.put("FeedingStatusName".toLowerCase(), "General Data Input,Monitoring,Preparation");
					checkMap.put("InstrumentStatusName".toLowerCase(), "Active,Disabled,Malfunction,New");
					checkMap.put("MaintenanceTypeName".toLowerCase(), "Breakdown,Preventive");
					//				checkMap.put("UserRoleName".toLowerCase(), "Admin,Dumm22,Dumm22,Dummy1,Dummy3,Dummy34,R22,Super user,Team Leader,User,Viewer,r4444");
					checkMap.put("CalibrationTypeName".toLowerCase(), "External,Internal");
					//				checkMap.put("FailureTypeName".toLowerCase(), "type 1,type 2");
					checkMap.put("StatusName".toLowerCase(), "Active,Completed,Completed,Planned");
					checkMap.put("WorkupStatusName".toLowerCase(), "Active,Cancelled,Completed");
					checkMap.put("MaintCalibStatusName".toLowerCase(), "Cancelled,Done,Open");
					checkMap.put("RequestViewName".toLowerCase(), "Analysis,DSC TGA,Formulation,General,Ms,Pilot");
					checkMap.put("DataTypeName".toLowerCase(), "number,text");
					checkMap.put("ExperimentStatusName".toLowerCase(),
							"Active,Approved,Cancelled,Completed,Failed,Finished,Planned");
					checkMap.put("StepStatusName".toLowerCase(), "Active,Cancelled,Finished,Planned");
					//				checkMap.put("TestedEntityName".toLowerCase(), "ExperimentAn.ConditionsGC,ExperimentAn.conditionsHPLC,Project.IP,a13231");
					checkMap.put("SourceName".toLowerCase(), "External,Internal");
					checkMap.put("ExperimentViewName".toLowerCase(), "General");
					checkMap.put("RequestStatusName".toLowerCase(), "Approved,Cancelled,Declined,Planned,Waiting");

					// check value of key "formCode + Name" from object "validateValueObject" for compliance of values in the Map is filled above 
					String key_in = Character.toLowerCase(formCode.charAt(0))
							+ (formCode.length() > 1 ? formCode.substring(1) : "") + "Name";
					String value_in = ((Map<String, String>) validateValueObject).get(key_in);
					String value = checkMap.get(key_in.toLowerCase());
					if (value != null) {
						Boolean findEquals = false;
						if (value != null) {
							String[] arr = value.split(",");
							for (String ar : arr) {
								if (ar.equals(value_in)) {
									findEquals = true;
								}
							}
							if (!findEquals) {
								throw new Exception(getMessage(validationCode, validateValueObject));
							}
						}
					}
				}
				break;
			}
			case CHECK_MANDATORY_FIELDS_WUFILTRAWASHINGREF: {
				throw new Exception(getMessage(validationCode, validateValueObject));
			}
			case INVALID_USER_CLONE_EXP: {
				throw new Exception(getMessage(validationCode, validateValueObject));
			}
			case CHECK_MANDATORY_FIELDS_INVITEMCOLUMN: {
				throw new Exception(getMessage(validationCode, validateValueObject));
			}
			case CHECK_SELEFTESTTYPE_FILE_EXTENSION: {
				if (!generalUtil.getNull((String) validateValueObject).equals("")) {

					String query = "select t.content_type from FG_FILES t where t.file_id = " + validateValueObject;
					String _contentType = generalDao.selectSingleString(query);
					if (!_contentType.equals("application/pdf")) {
						throw new Exception(getMessage(validationCode, validateValueObject));
					}
				}
				break;
			}
			case CHECK_INVALID_NEGATIVE_RESULT: {
				String result = validateValueObject != null ? (String) validateValueObject : "";
				if (!result.isEmpty()) {
					if (Double.parseDouble(result) < (double) 0) {
						throw new Exception(getMessage(validationCode, validateValueObject));
					}
				}
				break;
			}
			case INVALID_REMOVED_SELFTEST_INSTRUMENT: {
				String sql =  "SELECT LISTAGG(INST.INVITEMINSTRUMENTNAME, ',') WITHIN GROUP (ORDER BY INST.INVITEMINSTRUMENTNAME) AS instrumentConnected\r\n" + 
								"FROM\r\n" + 
								"  (SELECT TO_CHAR(INSTRUMENT_ID) AS INSTRUMENT_ID\r\n" + 
								"   FROM FG_S_RESULTREF_ALL_V t\r\n" + 
								"   WHERE t.PARENTID = '" + formId + "'\r\n" + 
								"     AND t.active = 1\r\n" + 
								"     AND t.sessionid IS NULL MINUS\r\n" + 
								"     SELECT TO_CHAR(INVITEMINSTRUMENT_ID)\r\n" + 
								"     FROM FG_S_INSTRUMENTSELECT_ALL_V WHERE PARENTID = '" + formId + "'\r\n" + 
								"     AND sessionid IS NULL ) tt,\r\n" + 
								"     FG_S_INVITEMINSTRUMENT_V INST\r\n" + 
								"WHERE tt.INSTRUMENT_ID = INST.INVITEMINSTRUMENT_ID";
				String invalidRemovedInstrument = generalDao.selectSingleStringNoException(sql);
				if (!generalUtil.getNull(invalidRemovedInstrument).isEmpty()) {
					throw new Exception(
							getMessage(validationCode, new Object[] { invalidRemovedInstrument }, validateValueObject));
				}
				break;
			}
			case INVALID_ACTION_ENDTINE: {
				throw new Exception(getMessage(validationCode, validateValueObject));
			}
			case DEPLETE_BEFORE_CANCELL_MATERIAL: {
				String status_id = (String) validateValueObject;
				String statusName = formDao.getFromInfoLookup("materialStatus", LookupType.ID, status_id, "name");
				if (statusName.equals("Cancelled")) {
					String sql = "select count(*) from fg_s_invitembatch_v where invitemmaterial_id = '" + formId
							+ "' and INUSEDEPLETED = 'In Use'";
					if (!generalDao.selectSingleStringNoException(sql).equals("0")) {//there is at least one or more active batches
						throw new Exception(getMessage(validationCode, validateValueObject));
					}
				}
				break;
			}
			/*case CHEKIFCOMPONENT_BEFORE_CANCELL_MATERIAL: {
				
				
				
				String status_id = (String) validateValueObject;
				String statusName = formDao.getFromInfoLookup("materialStatus", LookupType.ID, status_id, "name");
				String componentName = formDao.getFromInfoLookup("InvItemMaterial", LookupType.ID, formId, "name");
				String sql1 = "select m.invitemmaterialname , m.formid from fg_s_MaterialComponent_pivot t,fg_s_invitemmaterial_pivot m where t.parentid(+)=m.formid and t.material_id="+ formId;
				List<Map<String, Object>>  materials=generalDao.getListOfMapsBySql(sql1);
				String materialListLink="";
			    for (Map<String,Object> materiallist : materials) {
			    	materialListLink= materialListLink+","+"<a href='#' onClick=\"checkAndNavigate(['" +materiallist.get("FORMID") + "','InvItemMaterial'])\" >" + materiallist.get("INVITEMMATERIALNAME")
							+ "</a>";
				}
				if (statusName.equals("Cancelled")) {
					String sql = "select count(*) from fg_s_MaterialComponent_v where MATERIAL_ID = '" + formId
							+ "' and sessionid is null and active=1 ";
					if (!generalDao.selectSingleStringNoException(sql).equals("0")) {
						throw new Exception(getMessage(validationCode, new Object[] {componentName,materialListLink},validateValueObject));
						
					}
				}
				break;
			}*/
			// check if material is component in batch
            case CHEKIFCOMPONENT_BEFORE_CANCELL_MATERIAL: {
				String status_id = (String) validateValueObject;
				String statusName = formDao.getFromInfoLookup("materialStatus", LookupType.ID, status_id, "name");
				String componentName = formDao.getFromInfoLookup("InvItemMaterial", LookupType.ID, formId, "name");
				String sql1 = "select  distinct b.invitembatchname, b.formid from fg_s_MaterialComponent_pivot t,fg_s_invitembatch_pivot b where t.parentid=b.formid   and b.inusedepleted = 'In Use' and t.material_id="+ formId +" and t.sessionid is null and t.active=1 " ; 
				List<Map<String, Object>>  materials=generalDao.getListOfMapsBySql(sql1);
				String materialListLink="";
			    for (Map<String,Object> materiallist : materials) {
			    	materialListLink= materialListLink+"<a href='#' onClick=\"checkAndNavigate(['" +materiallist.get("FORMID") + "','InvItemMaterial'])\" >" + materiallist.get("INVITEMBATCHNAME")+","
							+ "</a>";
				}
			    materialListLink=generalUtil.replaceLast(materialListLink, ",", "");
				if (statusName.equals("Cancelled")) {
		
					if (!materials.isEmpty()) {
						throw new Exception(getMessage(validationCode, new Object[] {componentName,materialListLink},validateValueObject));
						
					}
				}
				break;
            }
         // check if material is in composition table in recipe
            case CHECKIFRECIPECOMPOSITION_BEFORE_CANCELL_MATERIAL: {
				String status_id = (String) validateValueObject;
				String statusName = formDao.getFromInfoLookup("materialStatus", LookupType.ID, status_id, "name");
				String componentName = formDao.getFromInfoLookup("InvItemMaterial", LookupType.ID, formId, "name");
				String sql1="select  distinct r.recipeformulationname, r.formid from fg_s_composition_pivot t,fg_s_recipeformulation_pivot r ,fg_s_recipestatus_all_v s  where t.parentid=r.formid   and r.status_id(+) = s.RECIPESTATUS_ID "+
						"and s.RECIPESTATUSNAME<>'Cancelled'  and t.invitemmaterial_id="+ formId +" and t.sessionid is null and t.active=1";	
				List<Map<String, Object>>  materials=generalDao.getListOfMapsBySql(sql1);
				String materialListLink="";
			    for (Map<String,Object> materiallist : materials) {
			    	materialListLink= materialListLink+"<a href='#' onClick=\"checkAndNavigate(['" +materiallist.get("FORMID") + "','InvItemMaterial'])\" >" + materiallist.get("recipeformulationname")+","
							+ "</a>";
				}
			    materialListLink=generalUtil.replaceLast(materialListLink, ",", "");
				if (statusName.equals("Cancelled")) {
		
					if (!materials.isEmpty()) {
						throw new Exception(getMessage(validationCode, new Object[] {componentName,materialListLink},validateValueObject));
						
					}
				}
				break;
            }
			case CONFIRM_NEW_EXPERIMENT: {
				int intOfResult = 0;
				String message ="";
				//List<String> samples=new ArrayList<String>();
				String parentIdListCsv = (String) validateValueObject;
				if (!parentIdListCsv.isEmpty()) {//the experiment is about to be created from several requests
					List<String> operationTypeId = generalDao.getListOfStringBySql("select distinct decode(t.EXPERIMENTTYPENAME,'Assay',(select distinct ex.EXPERIMENTTYPE_ID from fg_s_experimenttype_v ex where EXPERIMENTTYPENAME = 'Impurity Profile'),t.OperationTypeName) " 
							+ " from fg_s_operationtype_all_v t," + " fg_s_request_v r" + " where t.PARENTID = r.request_id"
							+ " and t.sessionId is null" + " and t.active = 1 " + " and r.request_id in ("
							+ parentIdListCsv + ")");

					/*List<String> destUnitId = generalDao.getListOfStringBySql("select distinct t.DESTUNIT_ID"
							+ " from fg_s_request_v t" + " where t.request_id in (" + parentIdListCsv + ")");*/

					/*List<Map<String, Object>> projectId = generalDao
							.getListOfMapsBySql("select distinct t.Project_id,t.SUBPROJECT_ID,t.SUBSUBPROJECT_ID"
									+ " from fg_s_request_v t" + " where t.request_id in (" + parentIdListCsv + ")");
					if (projectId == null || projectId.size() != 1) {
						throw new Exception(getMessage(ValidationCode.INVALID_EXPERIMENT_FROM_SEVERAL_REQUESTS_PROJ,
								validateValueObject));
					}*/

					/*if (destUnitId == null || destUnitId.size() != 1) {
						throw new Exception(getMessage(ValidationCode.INVALID_EXPERIMENT_FROM_SEVERAL_REQUESTS,
								validateValueObject));
					}*/

					if (operationTypeId == null || operationTypeId.size() != 1) {
						throw new Exception(getMessage(ValidationCode.INVALID_EXPERIMENT_FROM_SEVERAL_REQUESTS_OPT,
								validateValueObject));
					}
				}
				String result = generalDao.selectSingleString(String.format(
						"select distinct count(*) " + "from fg_i_request_destexp_v t " + "where t.request_id=%1$s ",
						formId));
				
				try {
					intOfResult = Integer.parseInt(result);
					if (!parentIdListCsv.isEmpty()){
						String userId = generalUtil.getSessionUserId();
						String userLab = formDao.getFromInfoLookup("USER", LookupType.ID, userId, "LABORATORY_ID");
						List<String> destLabId = generalDao.getListOfStringBySql("select distinct t.DESTLAB_ID"
								+ " from fg_s_request_v t, fg_s_sampledataref_v sr" + " where t.request_id in (" + parentIdListCsv + ") and sr.PARENTID(+) = t.REQUEST_ID and t.DESTLAB_ID<>"+userLab);
						if (destLabId!= null && !destLabId.isEmpty()) {
							List<String> samples= generalDao.getListOfStringBySql("select distinct s.samplename from fg_s_request_v t, fg_s_sampledataref_v sr, fg_s_sample_v s where sr.PARENTID = t.REQUEST_ID "
									+ "and sr.sessionid is null and sr.active = 1 and s.sample_id = sr.SAMPLEID and t.REQUEST_ID in("+parentIdListCsv
									+") and t.destlab_id in("+generalUtil.listToCsv(destLabId)+")");
							List<String> destLabName = generalDao.getListOfStringBySql("select distinct laboratoryname from fg_s_laboratory_v where formid in("+generalUtil.listToCsv(destLabId)+")");
							if(samples!= null && !samples.isEmpty() && destLabName!=null && !destLabName.isEmpty()){
								if (intOfResult > 0){
									sbInfoMessage.append("Please Notice- you have 2 warning messages:</br> 1. you will test sample no. "+ generalUtil.listToCsv(samples) +" that was sent to destination lab: "+ generalUtil.listToCsv(destLabName)+". \r\n" +
											"Are you sure you want to continue?</br> 2. ") ;
								}
								else{
									sbInfoMessage.append("Please notice- you will test sample no. "+ generalUtil.listToCsv(samples) +" that was sent to destination lab: "+ generalUtil.listToCsv(destLabName)+". \r\n" +
										"Are you sure you want to continue?</br>") ;
								}
							}else if(destLabName!=null && !destLabName.isEmpty()){
								if (intOfResult > 0){
									sbInfoMessage.append("Please Notice- you have 2 warning messages:</br> 1.you will test Request that was sent to destination lab: "+ generalUtil.listToCsv(destLabName)+". \r\n" +
											"Are you sure you want to continue?</br> 2. ") ;
								}
								else{
									sbInfoMessage.append("Please notice- you will test Request that was sent to destination lab: "+ generalUtil.listToCsv(destLabName)+". \r\n" +
										"Are you sure you want to continue?</br>") ;
								}
							}
									
						}
					}
				} catch (Exception e) {
					intOfResult = 0;
				}

				if (intOfResult > 0) {
					sbInfoMessage.append(getMessage(validationCode, validateValueObject));
				}
				break;
			}
			case CHECK_SAMPLES_INUSE_REMOVED: {
				/*String sql = "select LISTAGG(s.SAMPLENAME,',') WITHIN GROUP (order by s.SAMPLENAME) as SAMPLELIST"
						+ " from "
						+ " ( select w.SAMPLE_ID from "
							+ " (select regexp_substr(col, '[^,]+', 1, level) SAMPLE_ID"
							+ "    from (select distinct SAMPLES col from fg_i_webix_output_all_v where STEP_ID = '"+formId+"')"
							+ "    connect by level <= length(regexp_replace(col, '[^,]+')) + 1 ) w"
							+ " , fg_s_sampleselect_all_v s"
						+ " where w.SAMPLE_ID = s.sample_id"
						+ " and s.PARENTID = '"+formId+"'"
						+ " and s.ACTIVE = 1"
						+ " and s.SESSIONID is null"
						+ " MINUS"
						+ " Select distinct TO_CHAR(SAMPLE_ID) as SAMPLE_ID from FG_S_SAMPLESELECT_ALL_V where PARENTID = '"+ formId +"'"
							+ formDao.getWherePartForTmpData("SAMPLESELECT", formId)+") t"
						+ ",fg_s_sample_v s"
						+ " where t.SAMPLE_ID = s.SAMPLE_ID";*/
				String sql = "select w.SAMPLE_ID from " + " (select regexp_substr(col, '[^,]+', 1, level) SAMPLE_ID"
						+ "    from (select distinct SAMPLES col from fg_i_webix_output_all_v where STEP_ID = '"
						+ formId + "')" + "    connect by level <= length(regexp_replace(col, '[^,]+')) + 1 ) w"
						+ " , fg_s_sampleselect_all_v s" + " where w.SAMPLE_ID = s.sample_id" + " and s.PARENTID = '"
						+ formId + "'" + " and s.ACTIVE = 1" + " and s.SESSIONID is null";
				List<String> listOne = generalDao.getListOfStringBySql(sql);
				sql = " Select distinct TO_CHAR(SAMPLE_ID) as SAMPLE_ID from FG_S_SAMPLESELECT_ALL_V where PARENTID = '"
						+ formId + "'" + generalUtilFormState.getWherePartForTmpData("SAMPLESELECT", formId);
				List<String> listTwo = generalDao.getListOfStringBySql(sql);
				List<String> similar = listOne;
				List<String> different = new ArrayList<String>();
				different.addAll(listOne);

				similar.retainAll(listTwo);
				different.removeAll(similar);

				sql = "select LISTAGG(s.SAMPLENAME,',') WITHIN GROUP (order by s.SAMPLENAME) as SAMPLELIST"
						+ " from fg_s_sample_v s" + " where sample_id in (" + generalUtil.listToCsv(different) + ")";

				String sampleList = generalDao.selectSingleStringNoException(sql);
				if (!generalUtil.getNull(sampleList).isEmpty()) {
					throw new Exception(getMessage(validationCode, new Object[] { sampleList }, validateValueObject));
				}
				break;
			}
			case INVALID_CONSUMED_QUANTITY_COMPLETED_EXP: {
				//String errorMessagePart = (String)validateValueObject;
				throw new Exception(getMessage(validationCode, new Object[]{validateValueObject}, validateValueObject));
			}
			case CHECK_CONSUMED_QUANTITY: {
				/*String srcQuantitytableFormCode = formDao.getFormCodeEntityBySeqId((String)validateValueObject);
				String query = "Select QUANTITY from FG_S"+srcQuantitytableFormCode+"ALL_V where FORMID = "+(String)validateValueObject;
				String srcQuantity = generalDao.selectSingleString(query);
				query = "Select QUANTITY from FG_S_"+formCode+"ALL_V where FORMID = "+formId;
				String consumedQuantity =*/
				String[] info = ((String) validateValueObject).split(",");
				String srcQuantity = info[1];
				String consumedQuantity = info[0];
				String batchNumber = formDao.getFromInfoLookup("InvItemBatch", LookupType.ID, info[2], "name");
				String normalUomName = info[4];
				String experimentNumber = formDao.getFromInfoLookup("Step", LookupType.ID, info[3], "experimentname");
				String experimentId = formDao.getFromInfoLookup("Step", LookupType.ID, info[3], "experiment_id");
				Boolean isGetExperimentViolationlst = Boolean.parseBoolean(info[5]);
				if (Double.parseDouble(consumedQuantity) > Double.parseDouble(srcQuantity)) {
					if (isGetExperimentViolationlst) {
						if (!sbInfoMessage.toString().isEmpty()) {
							sbInfoMessage.append(",");
						}
						sbInfoMessage.append(experimentId);
					} else {
						if (!sbInfoMessage.toString().isEmpty()) {
							sbInfoMessage.append("</br>");
						}
						sbInfoMessage.append(getMessage(validationCode, new Object[] { consumedQuantity, srcQuantity,
								batchNumber, experimentNumber, normalUomName }, validateValueObject));
					}
				}
				break;
			}
			case RELATED_SAMPLE_BATCH: {
				String selectedSample = ((String) validateValueObject);
				String sampleName = formDao.getFromInfoLookup("Sample", LookupType.ID, selectedSample, "name");
				sbInfoMessage.append(getMessage(validationCode, new Object[] { sampleName }, validateValueObject));
				break;
			}
			case INVALID_SPECIFICATIONS: {
				Map<String, String> map = new HashMap<String, String>();
				String[] elements = ((String) validateValueObject).split(",");
				map.put("value1_in", elements[0]);
				map.put("value2_in", elements[1]);
				map.put("criteria1_in", elements[2]);
				map.put("criteria2_in", elements[3]);

				if (!generalUtil.getNull(map.get("value2_in")).isEmpty()) {
					String isValid = generalDao.callPackageFunction("FG_ADAMA", "CHECK_SPECIFICATION_VALIDATION", map);
					if (isValid.equals("0")) {
						throw new Exception(getMessage(validationCode, validateValueObject));
					}
				}
				break;
			}
			case VALIDATE_MATERIALRESULT_FILLED: {
				Map<String, String> map = new HashMap<>();
				map.putAll((Map<String, String>) validateValueObject);
				String s = generalUtil.getNull(map.get("structure"));
				String smiles = generalDao
						.selectSingleString("select SMILES_DATA from FG_CHEM_DOODLE_DATA where parent_id='" + s + "'");
				String m = map.get("materialName");
				if (m.isEmpty() && generalUtil.getNull(smiles).isEmpty()) {
					throw new Exception(getMessage(validationCode, validateValueObject));
				}
				break;
			}
			case DUPLICATE_OPERATIONTYPE: {
				List<String> countOpType = generalDao.getListOfStringBySql(
						"select count(operationTypeName) over (partition by operationTypeName) from fg_s_operationType_v where parentid='"
								+ formId
								+ "' and sessionid is null and active=1 group by operationTypeName having count(operationTypeName)>1");
				if (!countOpType.isEmpty()) {
					throw new Exception(getMessage(validationCode, validateValueObject));
				}
				break;
			}
			case EMPTY_OPERATIONTYPE: {
				String isOpTypeEmpty = generalDao
						.selectSingleStringNoException("select distinct 1 from fg_s_operationType_v where parentid='"
								+ formId + "' and sessionid is null and active=1 and operationTypeName is null");
				if (!isOpTypeEmpty.isEmpty()) {
					throw new Exception(getMessage(validationCode, validateValueObject));
				}
				break;
			}
			case EMPTY_PROCEDURE_OPT: {
				List<Map<String, String>> procedureOptIdList = formDao.getFromInfoLookupAllContainsVal("ExperimentType",
						LookupType.NAME, "Procedure Test");
				String procedureOptId = "";
				for (Map<String, String> procedureOpt : procedureOptIdList) {
					if (procedureOpt.get("REQUESTTYPE_ID").equals(validateValueObject.toString())) {
						procedureOptId = procedureOpt.get("ID");
						break;
					}
				}
				//String form = generalDao.selectSingleStringNoException("select distinct OperationTypeName from fg_s_operationType_v where parentid='"+formId+"'");// and OperationTypeName = '"+procedureOptId+"'"+generalUtilFormState.getWherePartForTmpData("OperationType", formId));
				String isProcedureEmpty = generalDao
						.selectSingleStringNoException("select distinct 1 from fg_s_operationType_v where parentid='"
								+ formId + "' and OperationTypeName = '" + procedureOptId + "' and PROCEDURE_ID is null"
								+ generalUtilFormState.getWherePartForTmpData("OperationType", formId));
				if (!isProcedureEmpty.isEmpty()) {
					throw new Exception(getMessage(validationCode, validateValueObject));
				}
				break;
			}
			case REQUESTTYPE_OPERATIONTYPE: {
				String isOpType = generalDao.selectSingleStringNoException(
						"select distinct 1 from fg_s_operationType_v o ,fg_s_experimenttype_v t where o.parentid='"
								+ formId + "' and o.OperationTypeName = t.experimenttype_id and t.REQUESTTYPE_ID !='"
								+ ((String) validateValueObject) + "' and o.active= 1");
				if (!isOpType.isEmpty()) {
					throw new Exception(getMessage(validationCode, validateValueObject));
				}
				break;
			}
			case VALIDATE_REPORTNAME_EXIST: {
				String nameId = formId;
				String reportName = validateValueObject.toString().split(",")[0];
				String userId = validateValueObject.toString().split(",")[1];
				String parentFormCode = validateValueObject.toString().split(",")[2];
				String isOtherReportExist = generalDao
						.selectSingleStringNoException("select distinct 1 from fg_formlastsavevalue_name t"
								+ " where save_name = '" + reportName + "' and userid = '" + userId + "' and formcode_name='"+parentFormCode+"'"
								+ (!nameId.isEmpty() ? " and save_name_id != '" + nameId + "'" : ""));
				if (!isOtherReportExist.isEmpty()) {
					sbInfoMessage.append(getMessage(validationCode, validateValueObject));
				}
				break;
			}
			case VALIDATE_REPORTDESIGNNAME_EXIST: {
				String nameId = formId;
				String reportName = validateValueObject.toString().split(",")[0];
				String userId = validateValueObject.toString().split(",")[1];
				 String isOtherReportExist = generalDao.selectSingleStringNoException(
						"select distinct 1 from fg_s_reportdesignexp_all_v t where ReportDesignExpName = '"
								+ reportName+ "' and t.CREATED_BY = '" + userId + "'");
														
				if (!isOtherReportExist.isEmpty()) {
					sbInfoMessage.append(getMessage(validationCode, validateValueObject));
				}
				break;
			}
			case VALIDATE_PLANNED_ACTUAL_SIMILAR: {
				String sql = "";
				if (formCode.contains("Step")) {
					sql = " with FILT_IN1 as (select * from fg_s_materialref_all_v where 1=1 and tabletype in ('Reactant','Solvent')"
							+ generalUtilFormState.getWherePartForTmpData("MaterialRef", formId) + ")" + ",FILT_IN as"
							+ " ( select distinct t.tableType,t.INVITEMMATERIALNAME"
							+ " from FG_S_MATERIALREF_ALL_V_PLAN t," + " FILT_IN1 mr" + " where t.step_id = '" + formId
							+ "'" + " and mr.step_id = '" + formId + "'"
							+ " and  t.tabletype in ('Reactant','Solvent')"
							+ " and t.INVITEMMATERIAL_ID = mr.INVITEMMATERIAL_ID"
							+ " and t.materialref_id = mr.materialref_id"
							+ " and decode(fg_get_num_normal(t.quantity,t.QUANTITYUOM_ID),fg_get_num_normal(mr.quantity,mr.QUANTITYUOM_ID),1,decode(fg_get_num_normal(t.VOLUME,t.voluom_id),fg_get_num_normal(mr.VOLUME,mr.voluom_id),1,0)) = 1"
							+ ")"
							+ "select distinct LISTAGG(t.tableType||' '||t.INVITEMMATERIALNAME,',') WITHIN GROUP (order by t.tableType,t.INVITEMMATERIALNAME)"
							+ " from FILT_IN t ";
				} else if (formCode.contains("Experiment")) {
					String stepsRunNumber = "";
					if(!validateValueObject.toString().equals(""))
					{
						stepsRunNumber = " and mr.RUNNUMBER = '"+validateValueObject.toString()+"' \n";
					}
					sql = " with FILT_IN as" + " ( select distinct t.step_id, t.tableType,t.INVITEMMATERIALNAME"
							+ " from FG_S_MATERIALREF_ALL_V_PLAN t," + " FG_S_MATERIALREF_ALL_V mr"
							+ " where t.step_id = mr.step_id" + " and  t.experiment_id = '" + formId + "'"
							+ " and mr.STEPSTATUSNAME in ('Active','Finished') \n"
							+ stepsRunNumber
							+ " and mr.active = '1' and mr.sessionid is null"
							+ " and  t.tabletype in ('Reactant','Solvent')"
							+ " and mr.tabletype in ('Reactant','Solvent')"
							+ " and t.INVITEMMATERIAL_ID = mr.INVITEMMATERIAL_ID"
							+ " and t.materialref_id = mr.materialref_id \n"
							+ " and decode(fg_get_num_normal(t.quantity,t.QUANTITYUOM_ID),fg_get_num_normal(mr.quantity,mr.QUANTITYUOM_ID),1,decode(fg_get_num_normal(t.VOLUME,t.voluom_id),fg_get_num_normal(mr.VOLUME,mr.voluom_id),1,0)) = 1"
							+ ") \n"
							+ "select distinct LISTAGG('Step '||s.FORMNUMBERID||':'||l.r_list,',</br>') WITHIN GROUP (order by TO_NUMBER(s.FORMNUMBERID)) "
							+ " from"
							+ "(select distinct t.step_id, LISTAGG(t.tableType||' '||t.INVITEMMATERIALNAME,',') WITHIN GROUP (order by t.tableType,t.INVITEMMATERIALNAME) over (partition by t.step_id) r_list"
							+ " from FILT_IN t) l," + " fg_s_step_v s" + " where s.step_id = l.step_id";
					
					
				}
				String isPlannedActualSimilar = generalDao.selectSingleStringNoException(sql);
				if (!generalUtil.getNull(isPlannedActualSimilar).isEmpty()) {
					sbInfoMessage.append(
							getMessage(validationCode, new Object[] { isPlannedActualSimilar }, validateValueObject));
				}
				break;
			}
			case CHARACTERIZED_SAMPLE_EMPTY: {
				sbInfoMessage.append(getMessage(validationCode, validateValueObject));
				break;
			}
			case INVALID_DATE: {
				throw new Exception(getMessage(validationCode, validateValueObject));
			}
			case MpToBigger: {
				throw new Exception(getMessage(validationCode, validateValueObject));
			}
			case ThermalDegToBigger: {
				throw new Exception(getMessage(validationCode, validateValueObject));
			}
			case ActionNameMandatory: {
				throw new Exception(getMessage(validationCode, validateValueObject));
			}
			case CHECK_PARAMETERS_EXIST:{
				String sql = "select distinct listagg('Step '||st.FORMNUMBERID,',') within group(order by to_number(st.FORMNUMBERID))"
						+ " from fg_s_step_v st"
						+ " where st.EXPERIMENT_ID = '"+formId+"'"
						+ " and not exists(select s.step_id"
						+ "  from fg_s_paramref_all_v t,"
						+ "  fg_s_step_v s"
						+ "  where t.parentid = s.step_id"
						+ "  and s.EXPERIMENT_ID = '"+formId+"'"
						+ "  and t.ACTIVE = 1"
						+ "  and t.SESSIONID is null"
						+ "  and s.step_id = st.step_id)";
				String stepListWithNoParam = generalDao.selectSingleStringNoException(sql);
				if(!stepListWithNoParam.isEmpty()){
					sbInfoMessage.append(getMessage(validationCode, new Object[] { " in the following steps:</br>"+stepListWithNoParam }, validateValueObject));
				}
				break;
			} case CHECK_COFORMULANT_MATERIAL_NAME_DUPLICATION:{
				
				String crrentMaterialName = validateValueObject.toString();
				String sql = "select t.invitemmaterial_id || ',' || t.invitemmaterialname from fg_s_invitemmaterial_v t where t.invitemmaterial_id <> '" 
						+ formId + "' and trim(upper(t.invitemmaterialname)) = '" + generalUtil.getNull(crrentMaterialName).trim().toUpperCase() + "'"
						+ " and upper(t.MATERIALPROTOCOLTYPE) = 'CHEMICAL MATERIAL' and COFORMULANT = 1 and active = 1";
				
				String matIdName = generalDao.selectSingleString(sql);
				if(matIdName != null && !matIdName.isEmpty()) {
					String matId = matIdName.split(",")[0];
					String matName = matIdName.split(",")[1];
					
					String duplicationMaterialLink = "<a href='#' onClick=\"checkAndNavigate(['" + matId+ "','InvItemMaterial'])\" >" + matName
					+ "</a>";
					
					throw new Exception(getMessage(validationCode, new Object[] { duplicationMaterialLink }, validateValueObject));
				}
				
				break;
			}case CHECK_MATERIAL_DUPLICATION: {

				if (validateValueObject instanceof Map<?, ?>) {
					@SuppressWarnings("unchecked")
					Map<String, String> newValuesMap = (Map<String, String>) validateValueObject;
					String newMaterialName = generalUtil.getNull(newValuesMap.get("invItemMaterialName")).trim();
					String newCasNumber = generalUtil.getNull(newValuesMap.get("casNumber")).trim();
					String newItemId = generalUtil.getNull(newValuesMap.get("itemId")).trim();
					String newSynonymsAdapted = newValuesMap.get("synonymsAdapted").toLowerCase();
					int countUnchangedFields = 0;
					
					/* reset fields values if there were NOT changes */
					if (checkMatValidFieldIsChanged.equals("1")) {
						Map<String, String> oldValuesMap = generalUtilFormState.getFormParam(Long.valueOf(newValuesMap.get("currentStateKey")), formCode);
						String oldMaterialName = generalUtil.getNull(oldValuesMap.get("$P{INVITEMMATERIALNAME}")).trim();
						String oldCasNumber = generalUtil.getNull(oldValuesMap.get("$P{CASNUMBER}")).trim();
						String oldItemId = generalUtil.getNull(oldValuesMap.get("$P{ITEMID}")).trim();
						String oldSynonymsAdapted = generalUtil.getNull(oldValuesMap.get("$P{SYNONYMSADAPTED}")).toLowerCase();

						if (oldMaterialName.toLowerCase().equals(newMaterialName.toLowerCase())) {
							newMaterialName = "";
							++countUnchangedFields;
						}
						if (oldCasNumber.equals(newCasNumber)) {
							newCasNumber = "";
							++countUnchangedFields;
						}
						if (oldItemId.equals(newItemId)) {
							newItemId = "";
							++countUnchangedFields;
						}
						if (oldSynonymsAdapted.equals(newSynonymsAdapted)) {
							newSynonymsAdapted = "";
							++countUnchangedFields;
						}
					}
					// continue validation code in case minimum one field was changed
					if(countUnchangedFields < 4)
					{
						Map<String, String> simpleParameters = new HashMap<>();
						simpleParameters.put("material_name_in", newMaterialName);
						simpleParameters.put("cas_number_in", newCasNumber);
						simpleParameters.put("item_id_in", newItemId);
						simpleParameters.put("syn_delim_in", systemSynonymDelim);
						simpleParameters.put("formid_in", formId);
	
						Map<String, String> outParameters = new HashMap<>();
						outParameters.put("return_code_out", null);
						outParameters.put("return_values_out", null);

						Map<String, Object> map = generalDao.callProcedureReturnsOutObject("", "sp_check_material_is_valid",
								simpleParameters, outParameters, OracleTypes.VARCHAR);
						String retCode = (String) map.get("return_code_out");
						String retItemsId = (String) map.get("return_values_out");
						System.out.println("ids: " + retItemsId);
						String errorMsg = "";
						if (retCode.equals("-1")) {
							errorMsg = "The Material " + newValuesMap.get("invItemMaterialName")
									+ " you are trying to add already exists in the system";
						} else if (retCode.equals("-2")) {
							errorMsg = "The CAS Number " + newValuesMap.get("casNumber")
									+ " you are trying to add already exists in the system";
						} else if (retCode.equals("-3")) {
							errorMsg = "The Item Id " + newValuesMap.get("itemId")
									+ " you are trying to add already exists in the system";
						} else if (retCode.equals("1")) //continue to check synonym in java code 
						{
							if (!newSynonymsAdapted.equals("")) {
								String[] newSynonymsAdaptedArr = newSynonymsAdapted.split(systemSynonymDelim);
								String sql = "select distinct t.formid, t.synonymsadapted, t.invitemmaterialname, t.active, t.sessionid \n"
										+ " from fg_s_invitemmaterial_pivot t, fg_s_materialstatus_v st \n"
										+ " where 1=1 \n" + " and nvl(st.\"MATERIALSTATUSNAME\",'Active') != 'Cancelled' \n"
										+ " and t.status_id = st.materialstatus_id(+) \n" + " and t.sessionid is null \n"
										+ " and lower(t.materialprotocoltype) = 'chemical material' and nvl(t.coformulant,'0') <> '1' and t.active = 1 and t.formid <> '" + formId + "'";
	
								List<Map<String, Object>> resultObj = null;
								resultObj = generalDao.getListOfMapsBySql(sql);
	
								StringBuilder formidStr = new StringBuilder();
								//////// start loop through results /////
								for (Map<String, Object> listOfMap : resultObj) {
									String currFormId = listOfMap.get("FORMID").toString();
									Object tmpvalue = listOfMap.get("SYNONYMSADAPTED");
									String currSynonymAdapted = (tmpvalue == null) ? ""
											: tmpvalue.toString().trim().toLowerCase();
									String materialName = listOfMap.get("INVITEMMATERIALNAME") == null ? ""
											: listOfMap.get("INVITEMMATERIALNAME").toString();
									String currMaterialName = generalUtil.getNull(materialName).trim().toLowerCase();
	
									//1: compare whole new synonymsAdapted with current synonymsAdapted and with materialName
									if (!currSynonymAdapted.equals("")) {
										if (newSynonymsAdapted.equals(currSynonymAdapted)
												|| newSynonymsAdapted.equals(currMaterialName)) {
											formidStr.append(currFormId).append(",");
											continue;
										}
									}
									//2: check if a new synonym contains current material name
									//3: check if each part of a new synonym is equal to part of current synonym
									List<String> currSynonymAdaptedArr = Arrays
											.asList(currSynonymAdapted.split(systemSynonymDelim));
									for (String newsynonymPart : newSynonymsAdaptedArr) {
										if (newsynonymPart.equals(currMaterialName)) {
											formidStr.append(currFormId).append(",");
											break;
										}
										if (currSynonymAdaptedArr.contains(newsynonymPart)) {
											formidStr.append(currFormId).append(",");
											break;
										}
									}
								}
								///// end loop through results /////
	
								if (formidStr.length() > 0) {
									retItemsId = formidStr.substring(0, formidStr.length() - 1);
									errorMsg = "A Material with the same synonym ("
											+ newValuesMap.get("synonymsAdapted").replaceAll(systemSynonymDelim, " ")
											+ ") already exists";
								}
							}
						}
						if (!errorMsg.equals("")) {
							throw new Exception("{\"eMsg\":\"" + errorMsg + "\",\"itemsId\":\"" + retItemsId + "\"}");
						}
					}
				}
				break;
			} case MAINTENANCE_NUMSTEPDESIGN_SINGLE_ROW: {
				String isMultiRows = generalDao.selectSingleStringNoException("select decode(count(*),1,0,1) as isMultiRows from FG_S_NUMSTEPDESIGN_PIVOT where active = 1");
				if (isMultiRows.equals("1")) {
					throw new Exception(getMessage(validationCode, validateValueObject));
				}
				break;
			} case VALIDATE_RUN_TURNS_ACTIVE:{
				if(validateValueObject.equals("Run")){
					throw new Exception(getMessage(validationCode, validateValueObject));
				} break;
			} case INVALID_FILE_UPLOAD:{
				//validate the the file has been saved with file_content,else- throws an exception
				String sql = "select t.file_name\n"
						+ " from fg_files t\n"
						+ " where t.file_id  = '"+validateValueObject+"'";
				String file_name = generalDao.selectSingleStringNoException(sql);
				if(file_name.isEmpty()){
					throw new Exception(getMessage(validationCode, validateValueObject));
				}
				break;
			} case ALERT_NEW_STEPFR_ACTION:{
				String stepFromCode = generalDao.selectSingleString("select formcode from fg_sequence where id = " + formId);
				if(generalUtil.getNull(stepFromCode).equalsIgnoreCase("stepfr")) {
					throw new Exception(getMessage(validationCode, validateValueObject));
				}
				break;
			} case ALERT_NEW_FORMULATION_STEP:{
				//check that filler exist
				validate(ValidationCode.CHECK_COMPOSITION_HAS_FILLER, formDao.getFormCodeBySeqId(formId), formId, "", new StringBuilder());
				//building the planned composition details log in order to get validation of missing data(such as missing batch etc)
				String compositiontype = "Solid";//elementValueMap.get("COMPOSITIONTYPENAME");
				commonFunc.buildRecipeMaterialFuncLog(formId, generalUtil.getSessionUserId(),compositiontype,"0",null);
				//check if  add empty row to component table 
				break;
			} case CHECK_HASCOMPONENT:{
				String hasComponent=generalDao.selectSingleString("select count(*) from FG_S_MaterialComponent_PIVOT t where parentid = '"+formId+"' and active = 1 and sessionid is null");
				if(hasComponent.equals("0")) {
					throw new Exception(getMessage(validationCode, validateValueObject));
				}
				break;
			} case CHECK_COMPOSITION_HAS_FILLER:{
				String sql = "select max(nvl(filler,'0'))\n"
						+ "from fg_s_composition_v\n"
						+ "where parentid = '"+formId+"'\n"
						+ "and active = '1'\n"
						+ generalUtilFormState.getWherePartForTmpData("Composition", formId);
				String isCompositionFillerExist = generalDao.selectSingleStringNoException(sql);
				if(isCompositionFillerExist!=null && isCompositionFillerExist.equals("0")){//isCompositionFillerExist is null when there's no composition
					throw new Exception(getMessage(validationCode, new Object[] { formCode.equals("ExperimentFor")?"Experiment":(formCode.equals("StepMinFr")?"Step":"Recipe") }, validateValueObject));
				}
				break;
			} case CHECK_COMPOSITION_HAS_CANCELLED_MATERIALS:{
				String sql = "select distinct listagg(m.invitemmaterial_id,',')within group(order by m.invitemmaterialname)\n"
						+ "from fg_s_invitemmaterial_v m," + " fg_S_materialstatus_v ms\n"
						+ "where m.status_id = ms.materialstatus_id(+)\n" + "and ms.materialstatusname = 'Cancelled'\n"
						+ " and invitemmaterial_id in \n" + "(select invitemmaterial_id\n"
						+ "from fg_s_composition_v t\n" + " where t.tabletype = 'expComposition'\n"
						+ "and t.parentid = '" + formId + "'\n" + "and t.active = 1\n"
						+ generalUtilFormState.getWherePartForTmpData("composition", formId) + "\n)";
				String cancelledMaterials = generalDao.selectSingleStringNoException(sql);

				if (!generalUtil.getNull(cancelledMaterials).isEmpty()) {
					//throw new Exception("Material"+(cancelledMaterials.contains(",")?"s:":" ")+cancelledMaterials+" "+(cancelledMaterials.contains(",")?"s:":"")+" canceled.</br> Please select alternative material");
					if (cancelledMaterials.contains(",")) {
						String[] materialIDs = cancelledMaterials.split(",");
						String cancelledMaterialsLink = "";
						for (String materialId : materialIDs) {
							String materialName = formDao.getFromInfoLookup("InvItemMaterial", LookupType.ID,
									materialId, "name");
							if (cancelledMaterialsLink.isEmpty()) {
								cancelledMaterialsLink += "<a href='#' onClick=\"checkAndNavigate(['" + materialId
										+ "','InvItemMaterial'])\" >" + materialName + "</a>";
							} else {
								cancelledMaterialsLink += "<a href='#' onClick=\"checkAndNavigate(['" + materialId
										+ "','InvItemMaterial'])\" >" + ", " + materialName + "</a>";
							}
						}
						throw new Exception("Materials:" + cancelledMaterialsLink
								+ " canceled.</br> Please select different materials");
					} else {
						String alternativeGroup = formDao.getFromInfoLookup("InvItemMaterial", LookupType.ID,
								cancelledMaterials, "ALTERNATIVEGROUP");
						String isAlternativeExist = generalUtil.getNull(generalDao.selectSingleStringNoException("select distinct 1 from FG_S_INVITEMMATERIAL_ALL_V t\r\n" + 
								"WHERE T.ACTIVE = 1\r\n" + 
								"and   t.MATERIALSTATUSNAME <>  'Cancelled' \r\n" + 
								"and upper(t.ALTERNATIVEGROUP) = upper('"+alternativeGroup+"')"+ 
								"and t.INVITEMMATERIAL_ID <>'"+cancelledMaterials+"'"));
						String materialName = formDao.getFromInfoLookup("InvItemMaterial", LookupType.ID,
								cancelledMaterials, "name");
						throw new Exception("Material " + "<a href='#' onClick=\"checkAndNavigate(['"
								+ cancelledMaterials + "','InvItemMaterial'])\" >" + materialName + "</a>" + " "
								+ " canceled.</br> Please select "+(isAlternativeExist.equals("1")?"alternative":"a different")+" material");
					}
				}
				break;
			}
			case CHECK_BATCH_RECIPE_VALID:{
				String recipe_id = validateValueObject.toString();
				if(recipe_id.isEmpty()){
					throw new Exception("Recipe is mandatory");
				}
				//check if all the AI's in the recipe are in the components table
				Map<String,String> materialResultPair = new HashMap<>();
				Deque<String> materialNestedList = new ArrayDeque<String>();
				String compositionIdHasNoConc = "";
				commonFunc.getAIMaterialList(recipe_id, materialNestedList, 0, materialResultPair);
				String sql = "select t.MATERIAL_ID,t.concentration,formid"
						+ " from fg_s_materialcomponent_v t\n"
						+ " where t.parentid = '"+formId+"'\n"
						+ " and t.active = 1\n"
						+ " and t.MATERIAL_ID in(\n"
						+ " select  m.invitemmaterial_id\n"
						+ " from fg_s_invitemmaterial_v m\n"
						+ " ,fg_s_materialtype_v mt\n"
						+ " where instr(','||m.MATERIALTYPE_ID||',',','||mt.materialtype_id||',')>0\n"
						+ " and lower(mt.MaterialTypeName) in lower('Active Ingredient'))\n"
						+ generalUtilFormState.getWherePartForTmpData("materialcomponent", formId);
					List<Map<String,Object>> materialList = generalDao.getListOfMapsBySql(sql);
					for(Map<String,Object> materialData:materialList){
						String material_id = materialData.get("MATERIAL_ID")!= null?materialData.get("MATERIAL_ID").toString():"";
						if(material_id.isEmpty()){
							continue;
						}
						String conc = materialData.get("CONCENTRATION")!= null?materialData.get("CONCENTRATION").toString():"";
						materialResultPair.remove(material_id);
						if(conc.isEmpty() && compositionIdHasNoConc.isEmpty()){//GETS THE FIRST ROW THAT HAS NO CONCENTRATION
							compositionIdHasNoConc = materialData.get("FORMID")!= null?materialData.get("FORMID").toString():"";
						}
					}
					if(!materialResultPair.isEmpty()){
						List<String> missingMaterials = new ArrayList<>();
						for(Entry<String, String> materialPair: materialResultPair.entrySet()){
							String materialId = materialPair.getKey();
							String materialName = formDao.getFromInfoLookup("invitemmaterial", LookupType.ID, materialId, "name");
							missingMaterials.add(materialName);
						}
						throw new Exception("Some AI materials are missing: "+generalUtil.listToCsv(missingMaterials));
					}
					if(!compositionIdHasNoConc.isEmpty()){
						JSONObject json = new JSONObject();
						json.put(compositionIdHasNoConc, "'Concentration %' value is missing");
						throw new Exception("'Concentration %' value is missing");
					}
				break;
			} case INVALID_MATERIAL_NAME:{
				break;
			} case INVALID_RESULT_TYPE:{
				break;
			} case INVALID_RESULT_SAMPLE:{
				break;
			} case INVALID_UNKNOWN_MATERIAL:{
				break;
			} 
			case CHECK_TESTED_COMPONENT_MANDATORY:{
				commonFunc.checkTestedComponentMandatoryfields(formId, generalUtil.getSessionUserId());
				break;
			}
			default:
				break;

		}
	}

	private String getMessage(ValidationCode errCode, Object[] errorParameterArray, Object obj) {
		String msg = generalUtil.getSpringMessagesByKey(String.valueOf(errCode), errorParameterArray,
				String.valueOf(errCode));
		return msg;
	}

	private String getMessage(ValidationCode errCode, Object obj) {
		String msg = generalUtil.getSpringMessagesByKey(String.valueOf(errCode), String.valueOf(errCode));
		return msg;
	}
}
