package com.skyline.customer.adama;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.FormType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.LookupType;
import com.skyline.form.dal.ChemDao;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.FormSaveDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.dal.GeneralTaskDao;
import com.skyline.form.dal.UploadFileDao;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilCalc;
import com.skyline.form.service.GeneralUtilDesignData;
import com.skyline.form.service.GeneralUtilForm;
import com.skyline.form.service.GeneralUtilFormState;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.form.service.GeneralUtilPermission;
import com.skyline.form.service.IntegrationCalc;
import com.skyline.form.service.IntegrationEvent;
import com.skyline.form.service.IntegrationInitForm;

@Service
@Transactional
public class IntegrationInitFormAdamaImp implements IntegrationInitForm {

	@Autowired
	private GeneralDao generalDao;
  
	@Autowired
	private GeneralUtilFormState generalUtilFormState;
	
	@Autowired
	private GeneralUtilDesignData generalUtilDesignData;
	
	@Autowired
	private IntegrationEvent integrationEvent;

	@Autowired
	private FormDao formDao;

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private GeneralUtilForm generalUtilForm;

	@Autowired
	protected GeneralUtilCalc generalUtilCalc;

	@Autowired
	protected GeneralUtilLogger generalUtilLogger;

	@Autowired
	protected UploadFileDao uploadFileDao;

	@Autowired
	protected FormSaveDao formSaveDao;

	@Autowired
	protected ChemDao chemDao;

	@Autowired
	protected GeneralTaskDao generalTaskDao;

	@Autowired
	private IntegrationCalc integrationCalc;
	
	@Autowired
	private CommonFunc commonFunc;
	
	@Autowired
	private GeneralUtilPermission generalUtilPermission;
	
	@Value("${hideStepFromSpreadsheet:1}")
	private int hideStepFromSpreadsheet; //indicating if the steps should be available when the spreadsheet is enabled- used in the orgnic&CP experiments

	
	@Value("${isTreeRoot:0}")
	private int isTreeRoot; //indicating if the breadcrumb in the title would be opened as a tree

	@Override
	public Map<String, String> getFormParam(String formCode, String userId, String formId, FormType formType,
			boolean isNewFormId, Map<String, String> requestMap) {

		//..init
		Map<String, String> toReturn = new HashMap<String, String>();
		String rowNum = "1000"; // Default row num
		String wherePart = formCode.toUpperCase() + "_ID";
		String structId = formId;
		String struct = "";
		String dbTransactionId = generalUtil.getDBTransaction();
		//		System.out.println("dbTransactionId=" + dbTransactionId);

		//By formCode...
		try {
			//***************************************
			//*************** getFormParam Project 
			//***************************************
			if (formCode.equals("Project")) {

				String sql = "select distinct * from FG_AUTHEN_PROJECT_V t where t.PROJECT_ID = " + formId
						+ " and rownum < 1000 "; 
				toReturn = generalDao.sqlToHashMap(sql);
				//Formulation Type Field – should also be editable in ‘Active’ status for the creator & the crew of the project.
				String sql1="select distinct u.USER_ID from fg_s_project_all_v t,fg_s_userscrew_all_v u where u.parentid="+formId;
				String usersCrew=generalDao.getCSVBySql(sql1, false);
				
				String sql2="select t.CREATED_BY from fg_s_project_all_v t where t.formId="+formId;
				String createdBy=generalDao.selectSingleString(sql2);
				if((usersCrew.contains(userId)||createdBy.equals(userId))&&toReturn.get("PROJECTSTATUSNAME").equals("Active")) {
					toReturn.put("ISEDITABLE", "true");
				}
				else {
					toReturn.put("ISEDITABLE", "false");
				}
				//update default project type param by the user unit type if formulation then formulation else chemistry
				Map<String, String> userInfoMap = formDao.getUserInfoMap(userId);
				String unitName = userInfoMap.get("USER_INFO_UNIT_NAME");
				if(generalUtil.getNull(unitName).equalsIgnoreCase("formulation")) {
					String projectTypeId = formDao.getFromInfoLookup("ProjectType", LookupType.NAME, "Formulation", "id");
					toReturn.put("PARAM_DEFAULT_PROJECT_TYPE_ID", projectTypeId);
				} else {
					String projectTypeId = formDao.getFromInfoLookup("ProjectType", LookupType.NAME, "Chemistry", "id");
					toReturn.put("PARAM_DEFAULT_PROJECT_TYPE_ID", projectTypeId);
				}
				
			}
			//***************************************
			//*************** getFormParam SubProject 
			//***************************************
			else if (formCode.equals("SubProject")) {
				String role = formDao.getFromInfoLookup("USER", LookupType.ID, userId, "UserRoleName");

				if (isNewFormId) {

					String projectIdArray = requestMap.get("PARENT_ID");
					if (projectIdArray != null) {
						structId = projectIdArray;
						wherePart = "PROJECT_ID";
						rowNum = "2"; // < 2 for one row
						if (role.equals("Team Leader")) {
							toReturn.put("ISENABLE_SPECIFICATION", "1");
						} else {
							toReturn.put("ISENABLE_SPECIFICATION", "0");
						}
					}

				} else {
					String CREATEDBY_ID = formDao.getFromInfoLookup("SUBPROJECT", LookupType.ID, formId,
							"CREATEDBY_ID");
					String teamLeaderId = formDao.getFromInfoLookup("USER", LookupType.ID, CREATEDBY_ID,
							"TEAMLEADER_ID");

					if (CREATEDBY_ID.equals(userId) && role.equals("Team Leader") || teamLeaderId.equals(userId)) {
						toReturn.put("ISENABLE_SPECIFICATION", "1");
					} else {
						toReturn.put("ISENABLE_SPECIFICATION", "0");
					}
				}

				String sql = "select distinct "
						+ (rowNum.equals("2") ? getRelevantAuthenColumns(formCode, "SUBPROJECTNAME") : "*")
						+ " from FG_AUTHEN_SUBPROJECT_V t where t." + wherePart + " =" + structId + " and rownum < "
						+ rowNum;

				toReturn = generalDao.sqlToHashMap(sql);

				if (toReturn.get("PROJECTMANAGER_ID").equals(userId)) {
					toReturn.put("ISENABLE_SPECIFICATION", "1");
				} else {
					toReturn.put("ISENABLE_SPECIFICATION", "0");
				}
			}
			//***************************************
			//*************** getFormParam SubSubProject 
			//***************************************
			else if (formCode.equals("SubSubProject")) {

				if (isNewFormId) {
					String projectIdArray = requestMap.get("PARENT_ID");
					if (projectIdArray != null) {
						structId = projectIdArray;
						wherePart = "SUBPROJECT_ID";
						rowNum = "2"; // < 2 for one row
					}
				}
				String sql = "select distinct "
						+ (rowNum.equals("2") ? getRelevantAuthenColumns(formCode, "SUBSUBPROJECTNAME") : "*")
						+ " from FG_AUTHEN_SUBSUBPROJECT_V t where t." + wherePart + " =" + structId + " and rownum < "
						+ rowNum;
				toReturn = generalDao.sqlToHashMap(sql);
			}
			//***************************************
			//*************** getFormParam InvItemBatch 
			//***************************************
			else if (formCode.equals("InvItemBatch")) {
				String batchId=structId;
				if (isNewFormId) {
					String projectIdArray = requestMap.get("PARENT_ID");
					if (projectIdArray != null) {
						structId = projectIdArray;
						wherePart = "INVITEMMATERIAL_ID";
						rowNum = "2"; // < 2 for one row
					
					}
				
				}
				String sql = "select distinct "
						+ (rowNum.equals("2")
								? getRelevantAuthenColumns(formCode,
										"TIMESTAMP,CHANGE_BY,SESSIONID,FORMID,ACTIVE,MATERIALNAME,PURITYUOM_ID,EXPIRYDATE,RECEIPTDATE,SITE_ID,PROJECT_ID,SUBSUBPROJ_ID,SHELF,FORMNUMBERID,SOURCE_ID,MANUFACTURER_ID,PURITY,QUANTITY,QUANTITYUOM_ID,INVITEMBATCHNAME,MINSTOCKLEVEL,MINSTOCKLEVUOM_ID,PREPARATIONDATE,COA,ORDQUANTITYUOM_ID,ORDEREDQUANTITY,LABORATORY_ID,SUBPROJECT_ID,INUSEDEPLETED,COMMENTS,ISSTANDART,INVITEMBATCH_ID,FORM_TEMP_ID,INVITEMBATCH_OBJIDVAL,PARENTID,SAMPLE_ID")
								: "*")
						+ " from fg_authen_invitembatch_v t where t." + wherePart + " =" + structId + " and rownum < "
						+ rowNum;
				toReturn = generalDao.sqlToHashMap(sql);
				if (!isNewFormId) {
					if (toReturn.get("ISSTANDART").equals("0")) {
						sql = "select sum(quantity)" + " from "
								+ " (select fg_get_num_normal(mr.quantity,mr.QUANTITYUOM_ID) as quantity "
								+ " from fg_s_materialref_all_v mr" + " where TABLETYPE not in ('Product','Solvent')"
								+ " and batch_id = '" + formId + "'" + " and STEPSTATUSNAME in ('Planned','Active')"
								+ " union all" + " select fg_get_num_normal(RESULT_VALUE,RESULT_UOM_ID)"
								+ " from fg_i_webix_output_all_v t" + " ,fg_s_step_all_v s"
								+ " , fg_s_experiment_all_v e" + " where batch_id = '" + formId + "'"
								+ " and t.step_id = s.step_id(+)" + " and t.experiment_id = e.experiment_id(+)"
								+ " and nvl(s.stepstatusname, e.experimentstatusname) in ('Planned','Active'))";//ORDQUANTITYUOM_ID
						String orderedQuant = generalUtilCalc.getFromNormalNumber(
								generalUtil.getNull(generalDao.selectSingleStringNoException(sql), "0"),
								toReturn.get("ORDQUANTITYUOM_ID"), null);
						toReturn.put("ORDEREDQUANTITY", orderedQuant);
					}
				} 
				else {
					toReturn.put("ISSAMPLEDEFAFFECT", "1");
//					if(!toReturn.get("MATERIALPROTOCOLTYPE").equals("Chemical Material")) {
//						commonFunc.copyComponentTablefromMaterialToBatch(toReturn.get("INVITEMMATERIAL_ID"),userId,batchId);
//					}
				}
				/*String purity = generalDao.selectSingleStringNoException("select nvl((Select RESULT_VALUE from FG_I_SELECTEDRESULTS_V r "
							+"where r.RESULT_MATERIAL_ID = '"+toReturn.get("INVITEMMATERIAL_ID")+"' and r.SAMPLE_ID ='"+toReturn.get("SAMPLE_ID")+"' and r.RESULT_TEST_NAME = 'Analytical'),100) from dual");
				toReturn.put("PURITY", purity);*/
			}
			//***************************************
			//*************** getFormParam InvItemCalibration 
			//***************************************
			else if (formCode.equals("InvItemCalibration")) {
				if (isNewFormId) {
					String projectIdArray = requestMap.get("PARENT_ID");
					if (projectIdArray != null) {
						structId = projectIdArray;
						wherePart = "INVITEMINSTRUMENT_ID";
						rowNum = "2";
					}
				}
				String sql = "";
				String statusName = formDao.getFromInfoLookup("InvItemCalibration", LookupType.ID, formId,
						"STATUSNAME");
				if (generalUtil.getNull(statusName).equals("Cancelled")) {
					sql = "select distinct formPath" + " from fg_authen_INVITEMCALIBRATION_v t where t." + wherePart
							+ " =" + structId + " and rownum <" + rowNum;
				} else {
					sql = "select distinct " + (rowNum.equals("2") ? getRelevantAuthenColumns(formCode, "") : "*")
							+ " from fg_authen_INVITEMCALIBRATION_v t where t." + wherePart + " =" + structId
							+ " and rownum <" + rowNum;// + "and t.INVITEMCALIBRATION_ID = "+formId;
				}
				toReturn = generalDao.sqlToHashMap(sql);
			}
			//***************************************
			//*************** getFormParam InvItemMaintenance 
			//***************************************
			else if (formCode.equals("InvItemMaintenance")) {
				if (isNewFormId) {
					String projectIdArray = requestMap.get("PARENT_ID");
					if (projectIdArray != null) {
						structId = projectIdArray;
						wherePart = "INVITEMINSTRUMENT_ID";
						rowNum = "2";
					}
				}
				String maintenanceType = generalUtil.getNull(requestMap.get("defaultType"));//.isEmpty()?"Breakdown":requestMap.get("defaultType");//TODO
				String sql = "select distinct "
						+ (rowNum.equals("2")
								? getRelevantAuthenColumns(formCode,
										"INVITEMMAINTENANCE_ID,FORM_TEMP_ID,INVITEMMAINTENANCE_OBJIDVAL,FORMID,TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,BREAKDOWNDATE,REPEATEDFAILURE,DOCUMENTS,FAILURETYPE_ID,CRITICALBREAKDOWN,REPORTEDBY1_ID,REPORTEDBY2_ID,BREAKDOWNDESCRIPTION,MAINTENANCETYPE_ID,DETAILEDDESCGENERAL,STATUS_ID,FORMNUMBERID,MAINTENANCEDATE,DETAILEDDESCRIPTION,TECHNICIAN_ID,INVITEMMAINTENANCENAME,CLOSEBREAKDOWN,MAINTDESCRIPTION,MAINTENANCETYPENAME,MAINTCALIBSTATUSNAME,ISEMPTYSTATUS,TECHNICIAN")
								: "*")
						+ " from fg_authen_INVITEMMAINTENANCE_v t where t." + wherePart + " =" + structId
						+ " and rownum < " + rowNum;//+ "and t.INVITEMMAINTENANCE_ID = "+formId;
				toReturn = generalDao.sqlToHashMap(sql);
				toReturn.put("DEFAULTTTYPE", maintenanceType);
			}
			//***************************************
			//*************** getFormParam InvItemMaterial 
			//***************************************
			else if (formCode.equals("InvItemMaterial") || formCode.equals("InvItemMaterialFr") || formCode.equals("InvItemMaterialPr")) {

				String sql = "select distinct * from fg_authen_" + formCode + "_v t where t.INVITEMMATERIAL_ID = "
						+ formId + " and rownum <= 1000 ";
				toReturn = generalDao.sqlToHashMap(sql);
				
				boolean isCancellationPerm = generalUtilPermission.isUserInSchemeByCrudl(formCode, userId, "Cancellation");
				toReturn.put("IS_CANCELLATION_PERM", isCancellationPerm?"1":"0");
			}
			//***************************************
			//*************** getFormParam InvItemInstrument 
			//***************************************
			else if (formCode.equals("InvItemInstrument")) {

				String sql = "select distinct * from fg_authen_inviteminstrument_v t where t.INVITEMINSTRUMENT_ID = "
						+ formId + " and rownum <= 1000 ";
				toReturn = generalDao.sqlToHashMap(sql);
			}
			//***************************************
			//*************** getFormParam ExperimentMain 
			//***************************************
			else if (formCode.equals("ExperimentMain")) {
				String sql = " select 1 as dummy from dual";
				if (isNewFormId) {
					String projectIdArray = requestMap.get("PARENT_ID"); // parent id can be subproject or project
					if (projectIdArray != null) {
						structId = projectIdArray;
						// check if request
						String parentForm = generalUtilForm.getCurrrentIdInfo(structId).get("FORMCODE");
						if (generalUtil.getNull(parentForm).toLowerCase().endsWith("project")) {
							Map<String, String> userInfoMap = formDao.getUserInfoMap(userId);
							sql = " select distinct t.PROJECT_ID, t.SUBPROJECT_ID, decode(t.SUBSUBPROJECT_ID,"
									+ structId + ",t.SUBSUBPROJECT_ID,null) as SUBSUBPROJECT_ID,"
									+ userInfoMap.get("USER_INFO_LABORATORY_ID") + " as LAB,"
									+ userInfoMap.get("USER_INFO_SITE_ID") + " as SITE,"
									+ userInfoMap.get("USER_INFO_UNIT_ID") + " as UNIT,"
									+ " t.ISENABLESPREADSHEET,\n"
									+ " t.PROJECTTYPENAME"
									+ " from fg_authen_experimentmain_v t where t.subproject_id = " + structId
									+ " or t.subsubproject_id = " + structId;
							toReturn = generalDao.sqlToHashMap(sql);
						}
						//create from request (if experiment created from request the ORIGIN_REQUEST_ID  not null}
						else if (generalUtil.getNull(parentForm).toLowerCase().contains("request")) {
						   /**default experiment type is the protocol type of parent request (“Assay” and “Impurity profile” requests are the same type, in this case “Impurity profile” should be the default)
                              protocol type & experiment instrument(experiment view) are taken from maintenance (System Experiment Connection / experiment type) where experiment type = protocol type of parent request*/
							sql = "  select distinct t.PROJECT_ID, t.SUBPROJECT_ID, t.SUBSUBPROJECT_ID,"
									+ " t.DESTLAB_ID as LAB, t.DESTSITE_ID as SITE, t.DESTUNIT_ID as UNIT, t.ORIGIN_TEMPLATE_ID,"
									+ " RE.EXPERIMENTVIEW_ID, RE.EXPERIMENTTYPE_ID, RE.PROTOCOLTYPE_ID,"
									+ " t.TESTPURPOSE as AIM, RE.EXPERIMENTTYPE_ID_LIST,\n"
									+ " t.ISENABLESPREADSHEET,\n"
									+ " t.PROJECTTYPENAME"
									+ " from fg_s_request_all_v t, FG_I_REQUEST_EXPRIMENTTYPE_V RE"
									+ " where t.REQUEST_ID = RE.REQUEST_ID(+) and t.formid = '" + structId
									+ "' and rownum =1";
							toReturn = generalDao.sqlToHashMap(sql);
							toReturn.put("ORIGIN_REQUEST_ID", structId);
  						   /**where number of requests are selected in main screen 
  						      The list of projects (sub-projects & sub-sub-projects) will display only the projects that related to the selected requests
                             */
							if(generalUtil.getNull(requestMap.get("smartSelectList")).contains(",")){
								String project_count = generalUtil.getNull(generalDao.selectSingleStringNoException("select count(distinct project_id) from fg_s_request_v where request_id in("+requestMap.get("smartSelectList")+")"));
								String subproject_count = generalUtil.getNull(generalDao.selectSingleStringNoException("select count(distinct subproject_id) from fg_s_request_v where request_id in("+requestMap.get("smartSelectList")+")"));
								String subsubproject_count = generalUtil.getNull(generalDao.selectSingleStringNoException("select count(distinct subsubproject_id) from fg_s_request_v where request_id in("+requestMap.get("smartSelectList")+")"));
								toReturn.put("PROJECT_COUNT", project_count);
								toReturn.put("SUBPROJECT_COUNT", subproject_count);
								toReturn.put("SUBSUBPROJECT_COUNT", subsubproject_count);
								
								if(!subsubproject_count.equals("0")){
									toReturn.put("PROJECTLEVEL", "subsubproject_id");
									sql = "select listagg(subsubproject_id,',') within group (order by project_id) from fg_s_request_v where request_id in("+requestMap.get("smartSelectList")+")";
								}else {
									toReturn.put("PROJECTLEVEL", "subproject_id");
									sql = "select listagg(subproject_id,',') within group (order by project_id) from fg_s_request_v where request_id in("+requestMap.get("smartSelectList")+")";
								}
								String projectList = generalDao.selectSingleStringNoException(sql);
								toReturn.put("REQUESTS_PROJECT_ID", projectList);
								
								if(!project_count.equals("1")){
									toReturn.put("PROJECT_ID", "");
								}
								if(!subproject_count.equals("1")){
									toReturn.put("SUBPROJECT_ID", "");
								}
								if(!subsubproject_count.equals("1") && !subsubproject_count.equals("0")){
									toReturn.put("SUBSUBPROJECT_ID", "");
								}
								String protocolTypeName = formDao.getFromInfoLookup("PROTOCOLTYPE", LookupType.ID, toReturn.get("PROTOCOLTYPE_ID"), "name");
								if(protocolTypeName.equals("Analytical")) {//if the experiment based on several operation types then the experiment type should be general
									List<String> operationTypeId = generalDao.getListOfStringBySql("select distinct decode(t.EXPERIMENTTYPENAME,'Assay',(select distinct ex.EXPERIMENTTYPE_ID from fg_s_experimenttype_v ex where EXPERIMENTTYPENAME = 'Impurity Profile'),t.OperationTypeName) " 
											+ " from fg_s_operationtype_all_v t," + " fg_s_request_v r" + " where t.PARENTID = r.request_id"
											+ " and t.sessionId is null" + " and t.active = 1 " + " and r.request_id in ("
											+ requestMap.get("smartSelectList") + ")");
									if(operationTypeId == null || operationTypeId.size() != 1) {
										List<String> generalTypeList = formDao.getFromInfoLookupElementData("EXPERIMENTTYPE", LookupType.NAME, "General",
												"ID");
										for (String generalTypeId : generalTypeList) {
											Map<String, String> generaltypeData = formDao.getFromInfoLookupAll("EXPERIMENTTYPE", LookupType.ID, generalTypeId);
											if (generaltypeData.get("PROTOCOLTYPENAME").equalsIgnoreCase("Analytical")) {
												toReturn.put("EXPERIMENTTYPE_ID",generalTypeId);
												break;
											}
										}
									}
								}
							}
							if(!generalUtil.getNull(requestMap.get("smartSelectList")).isEmpty()){
								//only requests with the same type and operation type should be displayed. “Assay” and “Impurity profile” requests are the same type in this case
								List<String> operationTypeId = generalDao.getListOfStringBySql("select distinct t.OperationTypeName " 
							              + " from fg_s_operationtype_all_v t," + " fg_s_request_v r" + " where t.PARENTID = r.request_id"
							              + " and t.sessionId is null" + " and t.active = 1 " + " and r.request_id in ("
							              + requestMap.get("smartSelectList") + ")");
								if (operationTypeId == null || operationTypeId.size() == 2 && operationTypeId.contains("Impurity Profile") && operationTypeId.contains("Assay")) {
									String impurityProfile = formDao.getFromInfoLookup("EXPERIMENTTYPE", LookupType.NAME, "Impurity Profile",
											"ID"); 
									toReturn.put("EXPERIMENTTYPE_ID", impurityProfile);//impurityProfile should be the default(in case selected assay & impurity profile)
								}
							}
						} else {
							//error! write to log...
						}
					}
				}

				if (!toReturn.containsKey("EXPERIMENTTYPE_ID_LIST")
						|| generalUtil.getNull(toReturn.get("EXPERIMENTTYPE_ID_LIST")).equals("")) {
					toReturn.put("EXPERIMENTTYPE_ID_LIST", "EXPERIMENTTYPE_ID");
				}
				toReturn.put("EXPERIMENT_CRITERIA", "Active Templates");

			} else if (formCode.equals("ExpCloneMain")) {
				String sql = " select 1 as dummy from dual";
				if (isNewFormId) {
					String originExpId = requestMap.get("PARENT_ID");
					if (originExpId != null) {
						// check if request
						//						String parentForm = generalUtilForm.getCurrrentIdInfo(structId).get("FORMCODE");
						Map<String, String> userInfoMap = formDao.getUserInfoMap(userId);
						sql = " select distinct t.PROJECT_ID, t.SUBPROJECT_ID, t.SUBSUBPROJECT_ID,"
								+ userInfoMap.get("USER_INFO_LABORATORY_ID") + " as LAB,"
								+ userInfoMap.get("USER_INFO_SITE_ID") + " as SITE,"
								+ userInfoMap.get("USER_INFO_UNIT_ID") + " as UNIT,"
								+ " t.PROTOCOLTYPE_ID, t.EXPERIMENTTYPE_ID, t.EXPERIMENTVIEW_ID, t.aim, t.DESCRIPTION, t.originFormCode,t.template_id"
								+ " from fg_authen_expClonemain_v t where t.experiment_id = " + originExpId;
						toReturn = generalDao.sqlToHashMap(sql);
					}
				}

				if (!toReturn.containsKey("EXPERIMENTTYPE_ID_LIST")
						|| generalUtil.getNull(toReturn.get("EXPERIMENTTYPE_ID_LIST")).equals("")) {
					toReturn.put("EXPERIMENTTYPE_ID_LIST", "EXPERIMENTTYPE_ID");
				}
				toReturn.put("EXPERIMENT_CRITERIA", "Active Templates");

			}
			//***************************************
			//*************** getFormParam RequestMain 
			//***************************************
			else if (formCode.equals("RequestMain")) {
				String sql = " select 1 as dummy from dual";
				if (isNewFormId) {
					String projectIdArray = requestMap.get("PARENT_ID"); // parent id can be subproject or project 
					if (projectIdArray != null) {
						structId = projectIdArray;
						// check if experiment
						String parentForm = generalUtilForm.getCurrrentIdInfo(structId).get("FORMCODE");
						if (generalUtil.getNull(parentForm).toLowerCase().endsWith("project")) {
							//create from sub/subproject
							sql = " select distinct t.PROJECT_ID, t.SUBPROJECT_ID, decode(t.SUBSUBPROJECT_ID,"
									+ structId
									+ ",t.SUBSUBPROJECT_ID,null) as SUBSUBPROJECT_ID  from fg_authen_requestmain_noexp_v t where t.project_id = "
									+ structId + " or t.subproject_id = " + structId + " or t.subsubproject_id = "
									+ structId;
							toReturn = generalDao.sqlToHashMap(sql);
							// add user info (from the user info)
							Map<String, String> userInfoMap = formDao.getUserInfoMap(userId);
							toReturn.put("LABORATORY_ID", userInfoMap.get("USER_INFO_LABORATORY_ID"));
							toReturn.put("SITE_ID", userInfoMap.get("USER_INFO_SITE_ID"));
							toReturn.put("UNIT_ID", userInfoMap.get("USER_INFO_UNIT_ID"));
						} else if (generalUtil.getNull(parentForm).toLowerCase().contains("experiment")) {
							//create from experiment (if request created from experiment the lab/site/unit are according to the experiment
							sql = " select * from fg_authen_requestmain_exp_v t where t.EXPERIMENT_ID=" + structId;
							toReturn = generalDao.sqlToHashMap(sql);
							toReturn.put("ORIGIN_EXPERIMENT_ID", structId);
							toReturn.put("sampleList", requestMap.get("smartSelectList"));//uses the same element 'SMARTSELECTLIST' ,
																							//because when request is created from action,selftest,workup the sample list is taken from the map and not from the url as it is in the step and the experiment
						} else if (generalUtil.getNull(parentForm).toLowerCase().contains("step")) {
							//create from experiment (if request created from experiment the lab/site/unit are according to the experiment
							sql = " select * from fg_authen_requestmain_exp_v t where t.EXPERIMENT_ID = ( select t1.EXPERIMENT_ID from fg_s_step_all_v t1 where t1.step_ID = '"
									+ structId + "') ";
							toReturn = generalDao.sqlToHashMap(sql);
							toReturn.put("ORIGIN_EXPERIMENT_ID", generalUtil.getNull(toReturn.get("EXPERIMENT_ID")));
							toReturn.put("sampleList", requestMap.get("smartSelectList"));//uses the same element 'SMARTSELECTLIST' ,
							//because when request is created from action,selftest,workup the sample list is taken from the map and not from the url as it is in the step and the experiment
						} else if (generalUtil.getNull(parentForm).toLowerCase().contains("action")) {
							//create from experiment (if request created from experiment the lab/site/unit are according to the experiment
							sql = " select * from fg_authen_requestmain_exp_v t where t.EXPERIMENT_ID = ( select t1.EXPERIMENT_ID from fg_s_action_all_v t1 where t1.ACTION_ID = '"
									+ structId + "') ";
							toReturn = generalDao.sqlToHashMap(sql);
							toReturn.put("ORIGIN_EXPERIMENT_ID", generalUtil.getNull(toReturn.get("EXPERIMENT_ID")));
							List<String> sampleList = generalDao.getListOfStringBySql(
									"select distinct SAMPLE_ID from FG_S_SAMPLESELECT_ALL_V where PARENTID = '"
											+ structId + "' and active=1 and SESSIONID is null");
							toReturn.put("sampleList", generalUtil.listToCsv(sampleList));
							toReturn.put("parent_formcode", "Action");
							toReturn.put("ACTION_SAMPLE_ID", sampleList.isEmpty() ? "" : sampleList.get(0));
						} else if (generalUtil.getNull(parentForm).toLowerCase().contains("test")) {//selftest
							//create from experiment (if request created from experiment the lab/site/unit are according to the experiment
							sql = " select * from fg_authen_requestmain_exp_v t where t.EXPERIMENT_ID = ( select t1.EXPERIMENT_ID from fg_s_selftest_all_v t1 where t1.SELFTEST_ID = '"
									+ structId + "') ";
							toReturn = generalDao.sqlToHashMap(sql);
							toReturn.put("ORIGIN_EXPERIMENT_ID", generalUtil.getNull(toReturn.get("EXPERIMENT_ID")));
							List<String> sampleList = generalDao.getListOfStringBySql(
									"select distinct SAMPLE_ID from FG_S_SAMPLESELECT_ALL_V where PARENTID = '"
											+ structId + "' and active=1 and SESSIONID is null");
							toReturn.put("sampleList", generalUtil.listToCsv(sampleList));
						} else if (generalUtil.getNull(parentForm).toLowerCase().contains("workup")) {
							//create from experiment (if request created from experiment the lab/site/unit are according to the experiment
							sql = " select * from fg_authen_requestmain_exp_v t where t.EXPERIMENT_ID = ( select t1.EXPERIMENT_ID from fg_s_workup_all_v t1 where t1.WORKUP_ID = '"
									+ structId + "') ";
							toReturn = generalDao.sqlToHashMap(sql);
							toReturn.put("ORIGIN_EXPERIMENT_ID", generalUtil.getNull(toReturn.get("EXPERIMENT_ID")));
							List<String> sampleList = generalDao.getListOfStringBySql(
									"select distinct SAMPLE_ID from FG_S_SAMPLESELECT_ALL_V where PARENTID = '"
											+ structId + "' and active=1 and SESSIONID is null");
							toReturn.put("sampleList", generalUtil.listToCsv(sampleList));
						} else if (generalUtil.getNull(parentForm).toLowerCase().contains("template")) {
							//create from experiment (if request created from experiment the lab/site/unit are according to the experiment
							sql = " select * from fg_authen_requestmain_exp_v t where t.TEMPLATE_ID = '" + structId
									+ "' ";
							toReturn = generalDao.sqlToHashMap(sql);
							String testPurposeId = formSaveDao.getStructFileId("RequestMain.testPurpose");
							uploadFileDao.saveRichText(testPurposeId, "Test the Proocedure", "Test the Proocedure",
									false);
							String destUnit = generalDao
									.selectSingleStringNoException("select case lower(temp.PROTOCOLTYPENAME)"
											+ "  when 'organic' then (select u.UNITS_ID from fg_s_units_all_v u where u.UNITSNAME = 'Organic Pilot')"
											+ "  when 'formulation' then (select u.UNITS_ID from fg_s_units_all_v u where u.UNITSNAME = 'Formulation Pilot')"
											+ " end as DESTUNIT_ID" + " from FG_S_TEMPLATE_ALL_V temp"
											+ " where TEMPLATE_ID = '" + structId + "'");
							toReturn.put("ORIGIN_TEMPLATE_ID", generalUtil.getNull(toReturn.get("TEMPLATE_ID")));
							toReturn.put("TESTPURPOSE", testPurposeId);
							toReturn.put("DESTUNIT_ID", generalUtil.getNull(destUnit));
						}
					}
				}

			}
			//***************************************
			//*************** getFormParam SampleDataRef
			//***************************************
			else if (formCode.equals("SampleDataRef")) {
				String sql = "select distinct * from fg_authen_sampleDataRef_v  t where t." + wherePart + " = "
						+ structId;
				toReturn = generalDao.sqlToHashMap(sql);
			}
			//***************************************
			//*************** getFormParam Experiment 
			//***************************************
			else if (formCode.equals("Experiment")) {
				String sql = "select distinct * from fg_authen_experiment_v t where t." + wherePart + " = " + structId;
				toReturn = generalDao.sqlToHashMap(sql);
				String disabledStepstoDelete = generalDao.selectSingleStringNoException(
						"select distinct LISTAGG(t.step_id,',') within group(order by t.step_id) from fg_s_step_v t,fg_s_stepstatus_v s where t.experiment_id = '"
								+ structId
								+ "' and t.STATUS_ID = s.STEPSTATUS_ID(+) and s.STEPSTATUSNAME <> 'Planned'");
				toReturn.put("DISABLED_STEPS", disabledStepstoDelete);
				String isStepExist = generalDao.selectSingleStringNoException("select count(*)\n"
						+ " from fg_s_step_v\n"
						+ " where experiment_id = '"+structId+"'");
				String enabledspreadsheet = generalUtil.getNull(toReturn.get("ISENABLESPREADSHEET"),"No");
				boolean isStepAvailable = !isStepExist.equals("0") || enabledspreadsheet.equals("No") || hideStepFromSpreadsheet == 0;
				toReturn.put("IS_STEP_AVAILABLE", isStepAvailable?"True":"False");
			}
			//***************************************
			//*************** getFormParam ExperimentAn 
			//***************************************
			else if (formCode.equals("ExperimentAn")) {
				String sql = "select distinct * from fg_authen_experiment_v t where t.experiment_id = " + structId;
				toReturn = generalDao.sqlToHashMap(sql);
				toReturn.put("IS_CALC_EMPTY",
						generalDao.selectSingleString(
								"select decode(count(*),0,1,0) from fg_i_result_an_v where experiment_id = '" + structId
										+ "' and RESULT_IS_ACTIVE ='1' and  result_is_webix = '1' "));
			}
			//***************************************
			//*************** getFormParam ExperimentPrCr 
			//***************************************
			else if (formCode.equals("ExperimentPrCR")) {
				String sql = "select distinct * from fg_authen_experimentprcr_v t where t.experiment_id = " + structId;
				toReturn = generalDao.sqlToHashMap(sql);
			}
			//*************** getFormParam ExperimentPrBt 
			//***************************************
			else if (formCode.equals("ExperimentPrBT")) {
				String sql = "select distinct * from fg_authen_experimentprbt_v t where t.experiment_id = " + structId;
				toReturn = generalDao.sqlToHashMap(sql);
			}
			//*************** getFormParam ExperimentPrVs 
			//***************************************
			else if (formCode.equals("ExperimentPrVS")) {
				String sql = "select distinct * from fg_authen_experimentprvs_v t where t.experiment_id = " + structId;
				toReturn = generalDao.sqlToHashMap(sql);
			}
			//*************** getFormParam ExperimentPrTs 
			//***************************************
			else if (formCode.equals("ExperimentPrTS")) {
				String sql = "select distinct * from fg_authen_experimentprts_v t where t.experiment_id = " + structId;
				toReturn = generalDao.sqlToHashMap(sql);
			}
			//*************** getFormParam ExperimentPrGn 
			//***************************************
			else if (formCode.equals("ExperimentPrGn")) {
				String sql = "select distinct * from fg_authen_experimentprgn_v t where t.experiment_id = " + structId;
				toReturn = generalDao.sqlToHashMap(sql);
			}
			//*************** getFormParam ExperimentPr 
			//***************************************
			else if (formCode.equals("ExperimentPr")) {
				String sql = "select distinct * from fg_authen_experimentpr_v t where t.experiment_id = " + structId;
				toReturn = generalDao.sqlToHashMap(sql);
			}
			//*************** getFormParam ExperimentFor 
			//***************************************
			else if (formCode.equals("ExperimentFor")) {
				String sql = "select distinct t.*,decode(description,null,SUBSTR(fg_get_richtext_display(aim), 1, 100),description) as shortaim from fg_authen_experiment_v t where t.experiment_id = " + structId;
				toReturn = generalDao.sqlToHashMap(sql);
				String disabledStepstoDelete = generalDao.selectSingleStringNoException(
						"select distinct LISTAGG(t.step_id,',') within group(order by t.step_id) from fg_s_step_v t,fg_s_stepstatus_v s where t.experiment_id = '"
								+ structId
								+ "' and t.STATUS_ID = s.STEPSTATUS_ID(+) and s.STEPSTATUSNAME <> 'Planned'");
				toReturn.put("DISABLED_STEPS", disabledStepstoDelete);
				toReturn.put("SEARCH_TABLETYPE", generalUtil.getEmpty(toReturn.get("SEARCHBY"), "Recipe / External Code").equals("Recipe / External Code")?"recipeSearch":"experimentSearch");
			
				//eval allowNewStep
				String sql_ = "select count(*) from fg_s_Composition_v t where 1=1 and t.PARENTID = '" + formId + "' and t.sessionId is null and t.active = 1";//"select max(nvl(t.FILLER,'0')) as maxFiler from fg_s_Composition_v t where 1=1 and t.PARENTID = '" + formId + "' and t.sessionId is null and t.active = 1";
				String compositionCount = generalUtil.getNull(generalDao.selectSingleString(sql_));
				String enabledspreadsheet = generalUtil.getNull(toReturn.get("ISENABLESPREADSHEET"),"No");
				String allowNewStep = enabledspreadsheet.equalsIgnoreCase("no") && !compositionCount.equals("0")?"yes":"no"; //&& maxFiler.equalsIgnoreCase("1")
				toReturn.put("ALLOWNEWSTEP", allowNewStep);
				String isComposition = compositionCount.equals("0")?"no":"yes";//maxFiler.isEmpty()?"no":"yes";
				toReturn.put("IS_COMPOSITION", isComposition);
			}
			//***************************************
			//*************** getFormParam ExperimentStb
			//***************************************
			else if (formCode.equals("ExperimentStb")) { // add ExperimentStb for "Taro develop"
				String sql = "select distinct * from fg_authen_experimentstb_v t where t.experiment_id = " + structId;
				toReturn = generalDao.sqlToHashMap(sql);
			}
			//***************************************
			//*************** getFormParam Continuous Process
			//***************************************
			else if (formCode.equals("ExperimentCP")) { // add ExperimentCP for "Continuous Process"
				String sql = "select distinct * from fg_authen_ExperimentCP_v t where t.experiment_id = " + structId;
				toReturn = generalDao.sqlToHashMap(sql);
				String disabledStepstoDelete = generalDao.selectSingleStringNoException(
						"select distinct LISTAGG(t.step_id,',') within group(order by t.step_id) from fg_s_step_v t,fg_s_stepstatus_v s where t.experiment_id = '"
								+ structId
								+ "' and t.STATUS_ID = s.STEPSTATUS_ID(+) and s.STEPSTATUSNAME <> 'Planned'");
				toReturn.put("DISABLED_STEPS", disabledStepstoDelete);
				String isStepExist = generalDao.selectSingleStringNoException("select count(*)\n"
						+ " from fg_s_step_v\n"
						+ " where experiment_id = '"+structId+"'");
				String enabledspreadsheet = generalUtil.getNull(toReturn.get("ISENABLESPREADSHEET"),"No");
				boolean isStepAvailable = !isStepExist.equals("0") || enabledspreadsheet.equals("No") || hideStepFromSpreadsheet == 0;
				toReturn.put("IS_STEP_AVAILABLE", isStepAvailable?"True":"False");
			}
			//***************************************
			//***************************************
			//*************** getFormParam Step 
			//***************************************
			else if (formCode.equals("Step")) {
				// note: steps has DB functions (in fg_adama.INIT_STEP<>_DATA) that insert init step data and related table
				if (isNewFormId) {
					String parentId = requestMap.get("PARENT_ID");
					if (parentId != null) {
						structId = parentId;
						wherePart = "EXPERIMENT_ID";
						rowNum = "2";
					}
				}

				String sql = "select distinct "
						+ (rowNum.equals("2") ? "'0' as SNAPSHOT_FLAG, " + getRelevantAuthenColumns(formCode,
								"ACTIVE,ACTUALSTARTDATE,AIM,CHANGE_BY,CONCLUSSION,CREATIONDATETIME,CREATOR_ID,DESCRIPTION,DOCUMENTS,ESTIMSTARTDATE,FINISHDATE,FORMID,FORM_TEMP_ID,REACTFINISHTIME,REACTSTARTTIME,SELFTESTINSTRUCTIONS,SESSIONID,STATUS_ID,STEP_ID,STEP_OBJIDVAL,TIMESTAMP,SNAPSHOT_FLAG,COMPVOL_ID,MASSUOM,REACTORVOL_ID,VOLFACTOR,STEPNAME,PREPARATION_RUN,ISSTEPRUNSTARTED")//PROTOCOLTYPE_ID
								: "*")
						+ " from fg_authen_step_v t where t." + wherePart + " = " + structId + " and rownum < "
						+ rowNum;//+" and t.STEP_ID ="+ formId;	
				toReturn = generalDao.sqlToHashMap(sql);
				if (isNewFormId) {
					String expFormCode = formDao.getFormCodeBySeqId(structId);
					if(expFormCode.equals("ExperimentCP")){//fixed bug 8068
						sql = " select PREPARATION_RUN " + 
								" from (select PREPARATION_RUN, FORMNUMBERID " + 
								" from fg_s_step_v " + 
								" where EXPERIMENT_ID = '" + structId + "' order by FORMNUMBERID desc) where rownum <= 1";
						String prep_run_default = generalUtil.getEmpty(generalDao.selectSingleStringNoException(sql), "Preparation");
						toReturn.put("PREP_RUN_DEFAULT", prep_run_default);
					}
					sql = "select distinct lpad(nvl(max(t.FORMNUMBERID)over(partition by t.EXPERIMENT_ID)+1,'1'),2,0) as STEPSEQUENCE"
							+ " from fg_s_step_v t"
							+ " where experiment_id = '"+structId+"'";
					toReturn.put("STEPSEQUENCE", generalUtil.getEmpty(generalDao.selectSingleStringNoException(sql),"01"));
					toReturn.put("STEPSTATUSNAME", "");
					toReturn.put("PLANNED_ACTUAL_STATUS_DEFAULT", "Planned");
					sql = "select distinct last_value(formid) over (partition by experiment_id)" + " from fg_s_step_v"
							+ " where experiment_id = '" + structId + "'";
					String prevStepFormId = generalDao.selectSingleStringNoException(sql);
					if (!prevStepFormId.isEmpty()) {//if it's empty, then it is the first step and there's no need to execute the copy
						//prepare the reaction draw up to the the materials in the table
						String xml = integrationEvent.chemDoodleReactionTabUp("", formId, "MaterialRef");
						String formCodeFull = "Step.chemDoodleAct";
						String elementId = formSaveDao.getStructFileId("Step.chemDoodleAct");//always get new elementID
						String fullArray = xml;
						String value = chemDao.saveChemData(formCode, formId, elementId, fullArray, formCodeFull, "1");
						toReturn.put("CHEMDOODLEACT", value);
						toReturn.put("volFactor", "10");
						integrationCalc.doCalc("StepCalc", "OnSave", "", toReturn, null, null, formCode, formId,
								userId);
					}
				} else {
					toReturn.put("volFactor", toReturn.get("VOLFACTOR"));
					toReturn.put("reactorVolume", toReturn.get("REACTORVOLUME"));
					toReturn.put("retentionTime", toReturn.get("RETENTIONTIME"));
					String currentStatusName = toReturn.get("STEPSTATUSNAME");
					if (currentStatusName.equals("Planned")) {
						integrationCalc.doCalc("StepCalc", "OnSave", "", toReturn, null, null, formCode, formId,
								userId);
					} else {
						integrationCalc.doCalc("StepCalc", "OnSaveSS", "", toReturn, null, null, formCode, formId,
								userId);
					}

				}
				toReturn.put("REACTANT_CRITERIA", "Current Experiment");
				toReturn.put("PRODUCT_CRITERIA", "Current Project");
				toReturn.put("SOLVENT_CRITERIA", "ALL");
				String resToUpdate = generalDao.selectSingleStringNoException("select distinct 1 from fg_i_resusingtoupdate_mv t where t.parentid = '"+formId+"' and t.type_ = 'Reaction'");
				toReturn.put("RES_TO_UPDATE", generalUtil.getNull(resToUpdate));

			}
			//*************** getFormParam StepFr 
			//***************************************
			else if (formCode.equals("StepFr") || formCode.equals("StepMinFr")) {
				// note: steps has DB functions (in fg_adama.INIT_STEP<>_DATA) that insert init step data and related table
				if (isNewFormId) {
					String projectIdArray = requestMap.get("PARENT_ID");
					if (projectIdArray != null) {
						structId = projectIdArray;
						wherePart = "EXPERIMENT_ID";
						rowNum = "2";
					}
				} else {
					wherePart = "STEP_ID";
				}
				String sql = "select distinct "
						+ (rowNum.equals("2")
								? " '0' as SNAPSHOT_FLAG, " + getRelevantAuthenColumns("StepFr",
										"ACTIVE,ACTUALSTARTDATE,AIM,CHANGE_BY,CONCLUSSION,CREATIONDATETIME,CREATOR_ID,DESCRIPTION,DOCUMENTS,ESTIMSTARTDATE,FINISHDATE,FORMID,FORM_TEMP_ID,REACTFINISHTIME,REACTSTARTTIME,SELFTESTINSTRUCTIONS,SESSIONID,STATUS_ID,STEP_ID,STEP_OBJIDVAL,TIMESTAMP,PROTOCOLTYPE_ID,SNAPSHOT_FLAG,STEPNAME")
								: "*")
						+ " from fg_authen_stepFr_v t where t." + wherePart + " = " + structId + " and rownum < "
						+ rowNum;//+" and t.STEP_ID ="+ formId;	
				toReturn = generalDao.sqlToHashMap(sql);
				if (isNewFormId) {
					toReturn.put("PLANNED_ACTUAL_STATUS_DEFAULT", "Planned");
					if(!toReturn.get("EXPERIMENTSTATUSNAME").equals("Planned")){
						toReturn.put("DEFAULT_STATUS", "Active");
					} else{
						toReturn.put("DEFAULT_STATUS", "Planned");
					}
				}
				if(formCode.equals("StepMinFr")) {
					String experimentId = toReturn.get("EXPERIMENT_ID");
					String formulationType = formDao.getFromInfoLookup("Experiment", LookupType.ID, experimentId, "FORMULATIONTYPE_ID");
				    String compositionType = formDao.getFromInfoLookup("FORMULATIONTYPE", LookupType.ID, formulationType, "COMPOSITIONTYPE") ;
				    if(compositionType.equals("Solid")){
				    	toReturn.put("DEFAULT_UOM", "Kg");
				    }
				    if(compositionType.equals("Liquid")){
				    	toReturn.put("DEFAULT_UOM", "Liter");
				    }
				}
			}
			//***************************************
			//*************** getFormParam Action 
			//***************************************
			else if (formCode.equals("Action")) {
				if (isNewFormId) {
					String projectIdArray = requestMap.get("PARENT_ID");
					if (projectIdArray != null) {
						structId = projectIdArray;
						wherePart = "STEP_ID";
						rowNum = "2";
						String parentForm = generalUtilForm.getCurrrentIdInfo(structId).get("FORMCODE");
						if (generalUtil.getNull(parentForm).toLowerCase().contains("action")) {
							structId = generalDao.selectSingleStringNoException(
									"select t.STEP_ID from FG_AUTHEN_ACTION_V t where t.action_id = " + structId);
						}
					}
				}
				String sql = "select distinct "
						+ (rowNum.equals("2")
								? getRelevantAuthenColumns(formCode,
										"ACTION_ID,FORM_TEMP_ID,ACTION_OBJIDVAL,FORMID,TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMNUMBERID,ENDTIME,INSTRUCTION,OBSERVATION,STARTTIME,DOCUMENTS,ACTIONNAME,SETBEFORE_ID,SELFTEST,CHANGEABLE,previousEntityFormId,nextEntityFormId")
								: "*")
						+ " from fg_authen_action_v t where t." + wherePart + " = " + structId + " and rownum <"
						+ rowNum;//+" and t.ACTION_ID ="+ formId ;	
				toReturn = generalDao.sqlToHashMap(sql);
				if (isNewFormId) {
					toReturn.put("nextEntityFormId", "-1");
					toReturn.put(
							"previousEntityFormId", generalUtil.getEmpty(
									generalDao.selectSingleStringNoException(
											"select distinct first_value(tt.action_id)over(partition by tt.STEP_ID order by tt.FORMNUMBERID desc)"
													+ " from fg_s_action_v tt" + " where tt.STEP_ID = '"
													+ toReturn.get("STEP_ID") + "' and tt.active=1"),
									"-1"));
				}
			}
			//***************************************
			//*************** getFormParam SelfTest 
			//***************************************
			else if (formCode.startsWith("STest")) {
				if (isNewFormId) {
					String projectIdArray = requestMap.get("PARENT_ID");
					if (projectIdArray != null) {
						structId = projectIdArray;
						wherePart = "ACTION_ID";
						rowNum = "2";
					}
				} 
				wherePart = "SELFTEST_ID";
				formCode = "SelfTest";
				  
				String sql = "select distinct  "
						+ (rowNum.equals("2")
								? getRelevantAuthenColumns(formCode,
										"SELFTEST_ID,FORM_TEMP_ID,SELFTEST_OBJIDVAL,FORMID,TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,TYPE_ID,CREATIONDATE,STARTDATE,UNIT_ID,CHROMATOGRAMS,SELFTESTNAME,SUMMARY,FINISHDATE,DESCRIPTION,SITE_ID,LABORATORY_ID,INSTRUMENTS,SAMPLENUMBER,COLUMNS,SELFTESTTYPE")
								: "*")
						+ " from fg_authen_selftest_v t where t." + wherePart + " = " + structId + " and rownum <"
						+ rowNum;//+" and t.ACTION_ID ="+ formId ;	
				toReturn = generalDao.sqlToHashMap(sql);
			} 
			else if (formCode.equals("SelfTest")) {
				String sql = "";
				if (isNewFormId) {
					String projectIdArray = requestMap.get("PARENT_ID");
					if (projectIdArray != null) {
						structId = projectIdArray;
						wherePart = "ACTION_ID"; 
					}
					
					sql = "select distinct * from fg_authen_SelfTestMain_v t where " + wherePart + " = "
							+ structId;
					toReturn = generalDao.sqlToHashMap(sql);
					toReturn.put("SELFTESTTYPENAME_WITH_DEFAULT", "Internal Analytical"); 
				} else {
					sql = "select distinct * \n" 
							+ " from fg_authen_selftest_v t where t." + wherePart + " = " + structId + " and rownum <"
							+ rowNum;//+" and t.ACTION_ID ="+ formId ;	
					toReturn = generalDao.sqlToHashMap(sql);
				}
			}
			//***************************************
			//*************** getFormParam SelfTestMain
			//***************************************
			else if (formCode.equals("SelfTestMain")) {
				if (isNewFormId) {
					String projectIdArray = requestMap.get("PARENT_ID");
					if (projectIdArray != null) {
						structId = projectIdArray;
						wherePart = "ACTION_ID";
						rowNum = "2";

						String sql = "select distinct * from fg_authen_SelfTestMain_v t where " + wherePart + " = "
								+ structId;
						toReturn = generalDao.sqlToHashMap(sql);
					}
				}
			}
			//***************************************
			//*************** getFormParam InstrumentRef 
			//***************************************
			else if (formCode.equals("InstrumentRef")) {
				String parentStatusName = "";
				if (isNewFormId) {
					String projectIdArray = requestMap.get("PARENT_ID");

					if (projectIdArray != null) {
						structId = projectIdArray;
						wherePart = "PARENTID";
						rowNum = "2";
					}
					String parentFormCodeEntity = formDao.getFormCodeEntityBySeqId("", structId);
					if (parentFormCodeEntity.contains("Experiment")) {
						parentStatusName = formDao.getFromInfoLookup("Experiment", LookupType.ID, projectIdArray,
								"STATUSNAME");
					} else if (parentFormCodeEntity.contains("Step")) {
						parentStatusName = formDao.getFromInfoLookup("Step", LookupType.ID, projectIdArray,
								"STATUSNAME");
					}

				}
				String sql = "select distinct "
						+ (rowNum.equals("2")
								? getRelevantAuthenColumns(formCode,
										"FORM_TEMP_ID,INSTRUMENTREF_OBJIDVAL,FORMID,TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,USAGES,USAGES_UOM,INCLUDEINALLSTEPS,DESCRIPTION,PARENTID,INSTRUMENTREFNAME,INVITEMINSTRUMENTNAME,MODEL,MANUFACTURER,SERIALNUMBER,INSTRUMENTREF_ID,MANUFACTURERNAME,INVITEMINSTRUMENT_ID")
								: "*")
						+ " from fg_authen_instrumentref_v t where t." + wherePart + " = " + structId + " and rownum <"
						+ rowNum + generalUtilFormState.getWherePartForTmpData(formCode, formId) + " and rownum < "
						+ rowNum;
				toReturn = generalDao.sqlToHashMap(sql);
				if (isNewFormId) {
					toReturn.put("PARENTSTATUSNAME", parentStatusName);
				}
			}
			//***************************************
			//*************** getFormParam ParamMonitoring 
			//***************************************
			else if (formCode.equals("ParamMonitoring")) {
				String sql = "select distinct * from fg_authen_parammonitoring_v t where param_monitoring_parent_id = '"
						+ requestMap.get("PARENT_ID") + "'";
				toReturn = generalDao.sqlToHashMap(sql);
			}
			//***************************************
			//*************** getFormParam Component 
			//***************************************
			else if (formCode.equals("Component")) {
				if (isNewFormId) {
					String projectIdArray = requestMap.get("PARENT_ID");
					if (projectIdArray != null) {
						structId = projectIdArray;
						wherePart = "PARENTID";
						rowNum = "2";
					}
				}
				String sql = "select distinct "
						+ (rowNum.equals("2")
								? getRelevantAuthenColumns(formCode,
										"COMPONENT_ID,FORM_TEMP_ID,COMPONENT_OBJIDVAL,FORMID,TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMCODE,RT,STANDARDINCLUDED,COMPONENTNAME,RRT,SPECTRUM,OUM_ID,IMPURITY,COEFFICIENT,")
								: "*")
						+ "  from fg_authen_component_v t where t." + wherePart + " = " + structId
						+ generalUtilFormState.getWherePartForTmpData(formCode, formId) + " and rownum < " + rowNum;
				toReturn = generalDao.sqlToHashMap(sql);
				sql = " select nvl((select distinct max(comp.FORMID) over (partition by comp.PARENTID)"
						+ " from FG_S_COMPONENT_ALL_V comp where comp.PARENTID = '" + requestMap.get("PARENT_ID")
						+ "' and nvl(comp.IMPURITY,0) = 1 "
						+ generalUtilFormState.getWherePartForTmpData(formCode, requestMap.get("PARENT_ID")) + " ),0)"
						+ " from dual t";
				toReturn.put("ISIMPURITY", generalDao.selectSingleString(sql));
			}
			//***************************************
			//*************** getFormParam Request 
			//***************************************
			else if (formCode.equals("Request")) {
				String sql = " select 1 as dummy from dual";
				if (isNewFormId) {
					String projectIdArray = requestMap.get("PARENT_ID"); // parent id can be subproject or project 
					if (projectIdArray != null) {
						structId = projectIdArray;
						// check if experiment
						String parentForm = generalUtilForm.getCurrrentIdInfo(structId).get("FORMCODE");
						if (generalUtil.getNull(parentForm).toLowerCase().endsWith("project")) {
							//create from sub/subproject
							sql = " select distinct t.PROJECT_ID, t.SUBPROJECT_ID, decode(t.SUBSUBPROJECT_ID,"
									+ structId
									+ ",t.SUBSUBPROJECT_ID,null) as SUBSUBPROJECT_ID  from fg_authen_requestmain_noexp_v t where t.project_id = "
									+ structId + " or t.subproject_id = " + structId + " or t.subsubproject_id = "
									+ structId;
							toReturn = generalDao.sqlToHashMap(sql);
							// add user info (from the user info)
							Map<String, String> userInfoMap = formDao.getUserInfoMap(userId);
							toReturn.put("LABORATORY_ID", userInfoMap.get("USER_INFO_LABORATORY_ID"));
							toReturn.put("SITE_ID", userInfoMap.get("USER_INFO_SITE_ID"));
							toReturn.put("UNIT_ID", userInfoMap.get("USER_INFO_UNIT_ID"));
						} else if (generalUtil.getNull(parentForm).toLowerCase().contains("sample")) {
							//create from sample/sampleMain
							sql = " select distinct t.PROJECT_ID, t.SUBPROJECT_ID, t.SUBSUBPROJECT_ID,t.experiment_id as SAMPLE_EXP_ID"
									+ " from fg_s_sample_v t where t.sample_id = "
									+ structId;
							toReturn = generalDao.sqlToHashMap(sql);
							if(generalUtil.getNull(toReturn.get("PROJECT_ID")).isEmpty() || generalUtil.getNull(toReturn.get("SUBPROJECT_ID")).isEmpty() || generalUtil.getNull(toReturn.get("SAMPLE_EXP_ID")).isEmpty()) {//fix bug 9106
								toReturn.put("ENABLE_PROJECT_FIELDS", "1");
							}
							// add user info (from the user info)
							Map<String, String> userInfoMap = formDao.getUserInfoMap(userId);
							toReturn.put("LABORATORY_ID", userInfoMap.get("USER_INFO_LABORATORY_ID"));
							toReturn.put("SITE_ID", userInfoMap.get("USER_INFO_SITE_ID"));
							toReturn.put("UNIT_ID", userInfoMap.get("USER_INFO_UNIT_ID"));
							toReturn.put("sampleList", structId);
							toReturn.put("parent_formcode", "Sample");
						} else if (generalUtil.getNull(parentForm).equalsIgnoreCase("InvItemBatch")) {
							sql = " select distinct t.PROJECT_ID, t.SUBPROJECT_ID, t.SUBSUBPROJ_ID "
									+ " from fg_s_invitembatch_v t where t.formid = "
									+ structId;
							toReturn = generalDao.sqlToHashMap(sql);
							if(generalUtil.getNull(toReturn.get("PROJECT_ID")).isEmpty() || generalUtil.getNull(toReturn.get("SUBPROJECT_ID")).isEmpty()) {
								toReturn.put("ENABLE_PROJECT_FIELDS", "1");
							}
							// add user info (from the user info)
							Map<String, String> userInfoMap = formDao.getUserInfoMap(userId);
							toReturn.put("LABORATORY_ID", userInfoMap.get("USER_INFO_LABORATORY_ID"));
							toReturn.put("SITE_ID", userInfoMap.get("USER_INFO_SITE_ID"));
							toReturn.put("UNIT_ID", userInfoMap.get("USER_INFO_UNIT_ID"));
							toReturn.put("parent_formcode", "InvItemBatch");
						} else if (generalUtil.getNull(parentForm).toLowerCase().contains("experiment")) {
							//create from experiment (if request created from experiment the lab/site/unit are according to the experiment
							sql = " select * from fg_authen_requestmain_exp_v t where t.EXPERIMENT_ID=" + structId;
							toReturn = generalDao.sqlToHashMap(sql);
							toReturn.put("ORIGIN_EXPERIMENT_ID", structId);
							toReturn.put("sampleList", requestMap.get("smartSelectList"));//uses the same element 'SMARTSELECTLIST' ,
																							//because when request is created from action,selftest,workup the sample list is taken from the map and not from the url as it is in the step and the experiment
						} else if (generalUtil.getNull(parentForm).toLowerCase().contains("step")) {
							//create from experiment (if request created from experiment the lab/site/unit are according to the experiment
							sql = " select * from fg_authen_requestmain_exp_v t where t.EXPERIMENT_ID = ( select t1.EXPERIMENT_ID from fg_s_step_all_v t1 where t1.step_ID = '"
									+ structId + "') ";
							toReturn = generalDao.sqlToHashMap(sql);
							toReturn.put("ORIGIN_EXPERIMENT_ID", generalUtil.getNull(toReturn.get("EXPERIMENT_ID")));
							toReturn.put("sampleList", requestMap.get("smartSelectList"));//uses the same element 'SMARTSELECTLIST' ,
							//because when request is created from action,selftest,workup the sample list is taken from the map and not from the url as it is in the step and the experiment
						} else if (generalUtil.getNull(parentForm).toLowerCase().contains("action")) {
							//create from experiment (if request created from experiment the lab/site/unit are according to the experiment
							sql = " select * from fg_authen_requestmain_exp_v t where t.EXPERIMENT_ID = ( select t1.EXPERIMENT_ID from fg_s_action_all_v t1 where t1.ACTION_ID = '"
									+ structId + "') ";
							toReturn = generalDao.sqlToHashMap(sql);
							toReturn.put("ORIGIN_EXPERIMENT_ID", generalUtil.getNull(toReturn.get("EXPERIMENT_ID")));
							String sampleListCsv = generalDao.selectSingleStringNoException(
									"select distinct sampletable from FG_S_SAMPLESELECT_ALL_V where PARENTID = '"
											+ structId + "' and active=1 and SESSIONID is null");
							/*List<String> sampleList = generalDao.getListOfStringBySql(
									"select distinct SAMPLE_ID from FG_S_SAMPLESELECT_ALL_V where PARENTID = '"
											+ structId + "' and active=1 and SESSIONID is null");*/
							List<String> sampleList =  Arrays.asList(sampleListCsv.split(","));
							toReturn.put("sampleList", generalUtil.listToCsv(sampleList));
							toReturn.put("parent_formcode", "Action");
							toReturn.put("ACTION_SAMPLE_ID", sampleList.isEmpty() ? "" : sampleList.get(0));
						} else if (generalUtil.getNull(parentForm).toLowerCase().contains("test")) {//selftest
							//create from experiment (if request created from experiment the lab/site/unit are according to the experiment
							sql = " select * from fg_authen_requestmain_exp_v t where t.EXPERIMENT_ID = ( select t1.EXPERIMENT_ID from fg_s_selftest_all_v t1 where t1.SELFTEST_ID = '"
									+ structId + "') ";
							toReturn = generalDao.sqlToHashMap(sql);
							toReturn.put("ORIGIN_EXPERIMENT_ID", generalUtil.getNull(toReturn.get("EXPERIMENT_ID")));
							List<String> sampleList = generalDao.getListOfStringBySql(
									"select distinct SAMPLE_ID from FG_S_SAMPLESELECT_ALL_V where PARENTID = '"
											+ structId + "' and active=1 and SESSIONID is null");
							toReturn.put("sampleList", generalUtil.listToCsv(sampleList));
						} else if (generalUtil.getNull(parentForm).toLowerCase().contains("workup")) {
							//create from experiment (if request created from experiment the lab/site/unit are according to the experiment
							sql = " select * from fg_authen_requestmain_exp_v t where t.EXPERIMENT_ID = ( select t1.EXPERIMENT_ID from fg_s_workup_all_v t1 where t1.WORKUP_ID = '"
									+ structId + "') ";
							toReturn = generalDao.sqlToHashMap(sql);
							toReturn.put("ORIGIN_EXPERIMENT_ID", generalUtil.getNull(toReturn.get("EXPERIMENT_ID")));
							List<String> sampleList = generalDao.getListOfStringBySql(
									"select distinct SAMPLE_ID from FG_S_SAMPLESELECT_ALL_V where PARENTID = '"
											+ structId + "' and active=1 and SESSIONID is null");
							toReturn.put("sampleList", generalUtil.listToCsv(sampleList));
						} else if (generalUtil.getNull(parentForm).toLowerCase().contains("template")) {
							//create from experiment (if request created from experiment the lab/site/unit are according to the experiment
							sql = " select * from fg_authen_requestmain_exp_v t where t.TEMPLATE_ID = '" + structId
									+ "' ";
							toReturn = generalDao.sqlToHashMap(sql);
							String testPurposeId = formSaveDao.getStructFileId("RequestMain.testPurpose");
							uploadFileDao.saveRichText(testPurposeId, "Test the Proocedure", "Test the Proocedure",
									false);
							String destUnit = generalDao
									.selectSingleStringNoException("select case lower(temp.PROTOCOLTYPENAME)"
											+ "  when 'organic' then (select u.UNITS_ID from fg_s_units_all_v u where u.UNITSNAME = 'Organic Pilot')"
											+ "  when 'formulation' then (select u.UNITS_ID from fg_s_units_all_v u where u.UNITSNAME = 'Formulation Pilot')"
											+ " end as DESTUNIT_ID" + " from FG_S_TEMPLATE_ALL_V temp"
											+ " where TEMPLATE_ID = '" + structId + "'");
							toReturn.put("ORIGIN_TEMPLATE_ID", generalUtil.getNull(toReturn.get("TEMPLATE_ID")));
							toReturn.put("TESTPURPOSE", testPurposeId);
							toReturn.put("DESTUNIT_ID", generalUtil.getNull(destUnit));
						}
						String defaultRequest = generalDao.selectSingleStringNoException("select decode(max(t.USEASDEFAULTDATA),'1','0','1') from FG_S_REQUEST_V t where t.PROJECT_ID = '"+toReturn.get("PROJECT_ID")+"' and t.CREATOR_ID = '"+userId+"'");
						toReturn.put("DEFAULTREQUEST", generalUtil.getNull(defaultRequest));
					}
					toReturn.put("CREATOR_ID", userId);//fixed bug 7744
				}
				else{
					sql = "select distinct * from fg_authen_request_v t where t." + wherePart + " = " + structId;
			        toReturn = generalDao.sqlToHashMap(sql);
				
				Map<String, String> userInfoMap = formDao.getUserInfoMap(userId);
				//set user state if destination user or source user
				String destLabManager = formDao.getFromInfoLookup("Laboratory", LookupType.ID,
						toReturn.get("DESTLAB_ID"), "LAB_MANAGER_ID");
				if (userInfoMap.get("USER_INFO_LABORATORY_ID").equals(toReturn.get("SOURCELAB_ID"))
						|| userInfoMap.get("USER_INFO_USER_ID").equals(toReturn.get("CREATOR_ID"))) {
					toReturn.put("USERSTATE", "source");
				}
				 else if (userInfoMap.get("USER_INFO_LABORATORY_ID").equals(toReturn.get("DESTLAB_ID"))
						|| userInfoMap.get("USER_INFO_USER_ID").equals(destLabManager)){
					toReturn.put("USERSTATE", "dest");
				}else {
					toReturn.put("USERSTATE", "other");
				}
				if (userInfoMap.get("USER_INFO_UNIT_ID").equals(toReturn.get("DESTUNIT_ID"))
						|| userInfoMap.get("USER_INFO_USER_ID").equals(destLabManager)){
					toReturn.put("CREATEEXPSTATE", "dest");
				}else{
					toReturn.put("CREATEEXPSTATE", "other");
				}
				}
			}
			//***************************************
			//*************** getFormParam Training 
			//***************************************
			else if (formCode.equals("Training")) {
				//				if (requestMap.get("source").equals("InvItemInstrument")) {
				//					// continue title
				//					String sql = "select ' - Instrument ' || INVITEMINSTRUMENTNAME  as \"NAME_TITLE\" from FG_AUTHEN_INVITEMINSTRUMENT_V t where INVITEMINSTRUMENT_ID = '"
				//							+ requestMap.get("PARENT_ID") + "' and rownum < 1000 ";
				//					toReturn = generalDao.sqlToHashMap(sql);
				//				}
				//				if (requestMap.get("source").equals("InvItemMaterial")) {
				//					// continue title
				//					String sql = "select ' - Compound ' || INVITEMMATERIALNAME  as \"NAME_TITLE\" from fg_authen_invitemmaterial_v t where INVITEMMATERIAL_ID = '"
				//							+ requestMap.get("PARENT_ID") + "' and rownum < 1000 ";
				//					toReturn = generalDao.sqlToHashMap(sql);
				//				}
				//				if (requestMap.get("source").equals("InvItemColumn")) {
				//					// continue title
				//					String sql = "select ' - Column ' || INVITEMCOLUMNNAME  as \"NAME_TITLE\" from fg_authen_invitemcolumn_v t where INVITEMCOLUMN_ID = '"
				//							+ requestMap.get("PARENT_ID") + "' and rownum < 1000 ";
				//					toReturn = generalDao.sqlToHashMap(sql);
				//				} // do nothing the title is passed as tabletype to type element in training (in adama bl we add this to the message ) // it should be called tabletype instead of type like in all REF forms
			}
			//***************************************
			//*************** getFormParam MaterialRef 
			//***************************************
			else if (formCode.equals("MaterialRef")) {
				if (isNewFormId) {
					String projectIdArray = requestMap.get("PARENT_ID");
					if (projectIdArray != null) {
						structId = projectIdArray;
						wherePart = "PARENTID";
						rowNum = "2";
					}
				}
				String sql = "select distinct "
						+ (rowNum.equals("2")
								? getRelevantAuthenColumns(formCode,
										"IUPACNAME,CASNAME,CASNUMBER,MATERIALNAME,FORMULA,SMILES,STRUCTURE,MATERIALREFNAME,PARENTID,WATERCONTUOM_ID,ACTPURITYUOM_ID,MOLEUOM_ID,VOLUOM_ID,QUANTITYUOM_ID,VOLRATIOTOTAL,VOLUME,QUANTITY,QUANTRATIOTOTAL,WATERCONTENT,EQUIVALENT,LIMITINGAGENT,ACTUALPURITY,ACTIVE,SESSIONID,CHANGE_BY,TIMESTAMP,FORMID,MATERIALREF_OBJIDVAL,FORM_TEMP_ID,MATERIALREF_ID,BATCH,PURITYUOM_ID,PURITY,SYNONYMS,MW_UOM_ID,MW,DENSITY_UOM_ID,DENSITY")
								: "*")
						+ " from FG_AUTHEN_MaterialRef_V t where t." + wherePart + " = " + structId
						+ generalUtilFormState.getWherePartForTmpData(formCode, requestMap.get("PARENT_ID"))
						+ " and rownum < " + rowNum;
				toReturn = generalDao.sqlToHashMap(sql);

				if (isNewFormId) {
					String defaultDensity = formDao.getFromInfoLookup("UOM", LookupType.NAME, "gr/cm^3", "id");
					toReturn.put("DENSITY_UOM_ID", defaultDensity);
					toReturn.put("PURITY", "100");
				}

				//set calculation parameters by tableType
				String thisTableType = requestMap.get("tableType");
				if (generalUtil.getNull(thisTableType).equals("Reactant")
						|| generalUtil.getNull(thisTableType).equals("Product")) {

					String limitingInfo = generalDao.selectSingleStringNoException(
							"select distinct first_value(fg_get_num_normal(mr.mole,mr.MOLEUOM_ID)) over (partition by mr.PARENTID) as MOLE "
									+ " from FG_S_MATERIALREF_V mr" + " where mr.PARENTID = "
									+ requestMap.get("PARENT_ID") + " and mr.LIMITINGAGENT ='1'"
									+ (!isNewFormId ? " and " + wherePart + " not in ('" + structId + "')" : "")
									+ generalUtilFormState.getWherePartForTmpData(formCode,
											requestMap.get("PARENT_ID")));
					if (limitingInfo.isEmpty()) {
						toReturn.put("MOLE", "");
						toReturn.put("ISLIMITED", "0");
					} else {
						String uomIdDefaultMole = "";
						List<String> uomList = formDao.getFromInfoLookupElementData("UOM", LookupType.NAME, "mole",
								"ID");
						for (String uomId : uomList) {
							Map<String, String> uomData = formDao.getFromInfoLookupAll("UOM", LookupType.ID, uomId);
							if (uomData.get("UOMTYPENAME").equalsIgnoreCase("mole")) {
								uomIdDefaultMole = uomId;
								break;
							}
						}
						String equivalent = isNewFormId ? "1"
								: generalUtil.getEmpty(generalDao.selectSingleStringNoException(
										"select distinct equivalent from fg_s_materialref_v where formid = '" + structId
												+ "'"),
										"1");
						toReturn.put("MOLE",
								generalUtilCalc.getFromNormalNumber(
										String.valueOf(
												Double.parseDouble(limitingInfo)
														* Double.parseDouble(equivalent)),
										isNewFormId ? uomIdDefaultMole
												: generalUtil.getEmpty(generalDao.selectSingleStringNoException(
														"select distinct MOLEUOM_ID from fg_s_materialref_v where formid = '"
																+ structId + "'"),
														uomIdDefaultMole),
										new StringBuilder()));
						toReturn.put("ISLIMITED", "1");
					}
					if (isNewFormId) {
						toReturn.put("LIMITINGAGENT", "0");
					}
				}

				//set display urlCall DT display parameters by tableType
				if (generalUtil.getNull(thisTableType).equals("Reactant")) {
					toReturn.put("MATERIALREF_CRITERIA", "Current Experiment");
				} else if (generalUtil.getNull(thisTableType).equals("Product")) {
					toReturn.put("MATERIALREF_CRITERIA", "Current Project");
				} else {
					toReturn.put("MATERIALREF_CRITERIA", "ALL");
				}

			}
			//***************************************
			//*************** getFormParam ExpParamsCrRef
			//***************************************
			else if (formCode.equals(("ExpParamsCrRef"))) {
				String sql = "select distinct  *" + " from FG_AUTHEN_ExpParamsCrRef_V t where t." + wherePart + " = "
						+ structId + generalUtilFormState.getWherePartForTmpData(formCode, requestMap.get("PARENT_ID"))
						+ " and rownum < " + rowNum;
				toReturn = generalDao.sqlToHashMap(sql);
				String hasRepresentativeResult = generalDao.selectSingleStringNoException(
						"select distinct 1 " + " from FG_S_ExpParamsCrRef_V t" + " where t.PARENTID = "
								+ requestMap.get("PARENT_ID") + " and t.representitiveresult ='1'"
								+ (!isNewFormId ? " and " + wherePart + " not in ('" + structId + "')" : "")
								+ generalUtilFormState.getWherePartForTmpData(formCode, requestMap.get("PARENT_ID")));
				if (hasRepresentativeResult.isEmpty()) {
					toReturn.put("HASREPRESULT", "0");
				} else {
					toReturn.put("HASREPRESULT", "1");
				}
			}
			//***************************************
			//*************** getFormParam WuDistFractionref 
			//***************************************
			else if (formCode.equals("WuDistFractionRef")) {
				String sql = "";
				String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, requestMap.get("PARENT_ID"));
				if (isNewFormId) {
					sql = "select distinct step_id,ACTION_ID from fg_s_WORKUP_ALL_v where formid='"
							+ requestMap.get("PARENT_ID") + "'";
				} else {
					sql = "select distinct  *" + " from FG_AUTHEN_WuDistFractionref_V t where t." + wherePart + " = "
							+ structId
							+ generalUtilFormState.getWherePartForTmpData(formCode, requestMap.get("PARENT_ID"))
							+ " and rownum < " + rowNum;
				}
				toReturn = generalDao.sqlToHashMap(sql);
				toReturn.put("SESSION", sessionId);
			}
			//			//***************************************
			//			//*************** getFormParam WuCrystMonitorRef 
			//			//***************************************
			//			else if (formCode.equals("WuCrystMonitorRef")) {
			//				
			//				toReturn.put("WORKUPTYPE", "Crystallization");
			//			}
			//***************************************
			//*************** getFormParam OperationType 
			//***************************************
			else if (formCode.equals("OperationType")) {
				if (isNewFormId) {
					String projectIdArray = requestMap.get("PARENT_ID");
					if (projectIdArray != null) {
						structId = projectIdArray;
						wherePart = "PARENTID";
						rowNum = "2";
					}
				}
				String sql = "select distinct * from FG_AUTHEN_OperationType_V t where t." + wherePart + " = "
						+ structId + generalUtilFormState.getWherePartForTmpData(formCode, formId) + " and rownum < "
						+ rowNum;
				;
				toReturn = generalDao.sqlToHashMap(sql);
			}
			//***************************************
			else if (formCode.equals("ExpParameterRef")) {
				//				if (isNewFormId) {
				//					String projectIdArray = requestMap.get("PARENT_ID");
				//					if (projectIdArray != null) {
				//						structId = projectIdArray;
				//						wherePart = "PARENTID";
				//						rowNum = "2";
				//					}
				//				}
				//				String sql = "select distinct * from FG_AUTHEN_ExpParameterRef_V t where t." + wherePart + " = "
				//						+ structId + generalUtilFormState.getWherePartForTmpData(formCode, formId);
				//				toReturn = generalDao.sqlToHashMap(sql);
			}
			//********************ExpSeriesMain*******************
			else if (formCode.equals("ExpSeriesMain")) {
				if (isNewFormId) {
					String projectIdArray = requestMap.get("PARENT_ID");
					if (projectIdArray != null) {
						structId = projectIdArray;
						wherePart = "PARENTID";
						rowNum = "2";
					}
				}
				String sql;
				sql = " select distinct t.PROJECT_ID, t.SUBPROJECT_ID, decode(t.SUBSUBPROJECT_ID," + structId
						+ ",t.SUBSUBPROJECT_ID,null) as SUBSUBPROJECT_ID,aim  from FG_I_ID_CONNECTION_SSPROJ_V t where t.subproject_id = "
						+ structId + " or t.subsubproject_id = " + structId;
				toReturn = generalDao.sqlToHashMap(sql);
			}

			//***************************************
			else if (formCode.equals("FormulantRef")) {
				if (isNewFormId) {

					String sql = "select distinct t.parentid from Fg_Authen_Formulantref_v t where t." + wherePart
							+ " = " + structId + generalUtilFormState.getWherePartForTmpData(formCode, formId);
					toReturn = generalDao.sqlToHashMap(sql);
				} else {
					String sql = "select distinct * from Fg_Authen_Formulantref_v t where t." + wherePart + " = "
							+ structId + generalUtilFormState.getWherePartForTmpData(formCode, formId);
					toReturn = generalDao.sqlToHashMap(sql);
				}

				String parentId = requestMap.get("PARENT_ID");
				String parentFormCodeEntity = formDao.getFormCodeEntityBySeqId("", parentId);

				//set display urlCall DT display parameters by parentFormCode
				if (generalUtil.getNull(parentFormCodeEntity).equals("Step")) {
					toReturn.put("FORMULANTREF_CRITERIA", "Current Experiment");
				} else if (generalUtil.getNull(parentFormCodeEntity).equals("Experiment")) {
					toReturn.put("FORMULANTREF_CRITERIA", "Current Project");
				} else if (generalUtil.getNull(parentFormCodeEntity).equals("ExperimentSeries")) {
					toReturn.put("FORMULANTREF_CRITERIA", "Current Project");
				} else {
					toReturn.put("FORMULANTREF_CRITERIA", "ALL");
				}

			}
			//***************************************
			else if (formCode.equals("FormulationPropRef")) {
				//				if (isNewFormId) {
				//					String projectIdArray = requestMap.get("PARENT_ID");
				//					if (projectIdArray != null) {
				//						structId = projectIdArray;
				//						wherePart = "PARENTID";
				//						rowNum = "2";
				//					}
				//				}
				//				String sql = "select distinct * from FG_AUTHEN_FormulationPropRef_V t where t." + wherePart + " = "
				//						+ structId + generalUtilFormState.getWherePartForTmpData(formCode, formId);
				//				toReturn = generalDao.sqlToHashMap(sql);
			}
			//***************************************
			else if (formCode.equals("ExpSeriesCreation")) {
				if (isNewFormId) {
					String projectIdArray = requestMap.get("PARENT_ID");
					if (projectIdArray != null) {
						structId = projectIdArray;
						wherePart = "PARENTID";
						rowNum = "2";
					}
				}
				String sql = "select distinct * from FG_AUTHEN_ExpSeriesCreation_V t where t." + wherePart + " = "
						+ structId + generalUtilFormState.getWherePartForTmpData(formCode, formId);
				toReturn = generalDao.sqlToHashMap(sql);
			}

			else if (formCode.equals("ExperimentSeries")) {
				String sql = "select distinct * from fg_authen_ExperimentSeries_v t where t.experimentseries_id = "
						+ structId;
				toReturn = generalDao.sqlToHashMap(sql);
			}
			//***************************************
			//*************** getFormParam Split 
			//***************************************
			else if (formCode.equals("Split")) {
				wherePart = "";
				String structArray = requestMap.get("struct");
				String formIdArray = requestMap.get("PARENT_ID");
				if (structArray != null) {
					struct = structArray;
				}
				if (struct.equals("InvItemBatch")) {
					String invItemBatchId = "";
					if (formIdArray != null) {
						invItemBatchId = formIdArray;
						wherePart = "INVITEMBATCH_ID";
					}
					String sql = "select distinct t.*,t.INVITEMBATCHNAME as \"Batch Number\", 'Batch ' || t.INVITEMBATCHNAME || ' Split' as MESSAGE from FG_S_INVITEMBATCH_ALL_V t where t."
							+ wherePart + " = " + invItemBatchId + " and rownum < 1000 ";
					toReturn = generalDao.sqlToHashMap(sql);
				}
			}
			//***************************************
			//*************** getFormParam MultiClone 
			//***************************************
			else if (formCode.equals("MultiClone")) {
				wherePart = "";
				String structArray = requestMap.get("struct");
				String formIdArray = requestMap.get("PARENT_ID");
				if (structArray != null) {
					struct = structArray;
				}
				if (struct.equals("InvItemBatch")) {
					String invItemBatchId = "";
					if (formIdArray != null) {
						invItemBatchId = formIdArray;
						wherePart = "INVITEMBATCH_ID";
					}
					String sql = "select distinct t.*,t.INVITEMBATCHNAME as \"Batch Number\" from FG_S_INVITEMBATCH_ALL_V t where t."
							+ wherePart + " = " + invItemBatchId + " and rownum < 1000 ";
					toReturn = generalDao.sqlToHashMap(sql);
				}
			}

			//***************************************
			//*************** getFormParam all Workup type 
			//***************************************
			else if (formCode.startsWith("Workup")) {

				if (formCode.equals("WorkupMain")) { // yp 15022018 remove " && isNewFormId) {" isNewFormId not needed WorkupMain is always new + change fg_authen_WorkupMain_v to show only action / experiment info

					String parentId = requestMap.get("PARENT_ID");
					if (parentId != null) {
						structId = parentId;
						wherePart = "ACTION_ID";
						rowNum = "2";
						String parentFormCodeEntity = formDao.getFormCodeEntityBySeqId("", structId);
						String sql;
						if (parentFormCodeEntity.equals("Workup")) {
							sql = "select distinct * from fg_authen_WorkupMain_v t where " + wherePart
									+ " = (select distinct action_id from fg_s_workup_v where formid = '" + structId
									+ "')";
						} else {
							sql = "select distinct * from fg_authen_WorkupMain_v t where " + wherePart + " = "
									+ structId;
						}
						toReturn = generalDao.sqlToHashMap(sql);
					}

				} else {
					String sql = "select distinct * from fg_authen_" + formCode + "_v t where t.FORMID = " + formId;
					toReturn = generalDao.sqlToHashMap(sql);
					toReturn.put("ISSAVENEXTENABLED", "1");
					String resToUpdate = generalDao.selectSingleStringNoException("select distinct 1 from fg_i_resusingtoupdate_mv t where t.parentid = '"+formId+"'");
					toReturn.put("RES_TO_UPDATE", generalUtil.getNull(resToUpdate));
					if (toReturn.get("STEPSTATUSNAME").equals("Planned")) {
						if (!toReturn.get("WORKUPTYPENAME").equals("Feeding")
								&& !toReturn.get("WORKUPTYPENAME").equals("Crystallization")) {
							toReturn.put("ISSAVENEXTENABLED", "0");
						} else {
							if (toReturn.get("STAGESTATUSNAME").equals("Materials Definition")//||toReturn.get("STAGESTATUSNAME").equals("General Data Input")
									|| toReturn.get("STAGESTATUSNAME").equals("Preparation")) {
								toReturn.put("ISSAVENEXTENABLED", "0");
							}
						}
					}
				}

				toReturn.put("WORKUPTYPE_ID", generalDao.selectSingleStringNoException(
						"select WORKUPTYPE_ID from fg_s_workup_v where formid = '" + structId + "'"));

			} else if (formCode.equals("WuFeedMaterialRef") || formCode.equals("WuCryMixDefineRef")
					|| formCode.equals("WuCrystSolventRef")) {
				String parentId = requestMap.get("PARENT_ID");
				if (parentId != null) {
					structId = parentId;
					wherePart = "WORKUP_ID";
					rowNum = "2";
					String sql = "select distinct * from fg_authen_" + formCode + "_v t where " + wherePart + " = "
							+ structId + " and rownum<2";
					toReturn = generalDao.sqlToHashMap(sql);
				} else {
					String sql = "select distinct * from fg_authen_" + formCode + "_v t where " + wherePart + " = "
							+ structId + " and rownum<2";
					toReturn = generalDao.sqlToHashMap(sql);
				}
			}
			//***************************************
			//*************** getFormParam Sample 
			//***************************************
			else if (formCode.equals("SampleMain")) {
				//TODO:add parent_code that contains the STEPFR value when the sample was created from the formulation protocol, so that the protocol will be filtered as expected
				String sql = " select * from FG_AUTHEN_SAMPLEMAIN_V where formId=" + structId;
				String stepId = "";
				String sampleBase = "Product";
				if (isNewFormId) {
					String parentFormCodeEntity;
					String parentId = requestMap.get("PARENT_ID");
					if (parentId != null) {
						parentFormCodeEntity = formDao.getFormCodeEntityBySeqId("", parentId);
						structId = parentId;
						wherePart = parentFormCodeEntity + "_id";
						rowNum = "2";
						if (parentFormCodeEntity.equals("InvItemBatch")) {
							sql = "select t.* from fg_authen_sampleMain_v t where invitembatch_id = '" + structId
									+ "' and rownum<" + rowNum;
							sampleBase = "Batch";
						} else {
							String table = "FG_I_ID_CONNECTION_"
									+ (parentFormCodeEntity.equalsIgnoreCase("Experiment") ? "expr" : parentFormCodeEntity) + "_V";
							sql = "select distinct * from " + table + " t where t." + wherePart + " = " + structId
									+ " and rownum <" + rowNum;
							//stepId = generalDao.selectSingleStringNoException("select distinct step_id from " + table + " t where t." + wherePart + " = " + structId + " and rownum <2");
						}
					}
					toReturn = generalDao.sqlToHashMap(sql);
					toReturn.put("DEFAULT_BASE", sampleBase);
					if (sampleBase.equals("Product")) {
						toReturn.put("INVITEMBATCH_ID", "");
					}
					
					String protocolTypeName = toReturn.get("PROTOCOLTYPENAME");
					stepId = toReturn.get("STEP_ID");
					if (!protocolTypeName.toLowerCase().equals("formulation")
							&& !generalUtil.getNull(stepId).isEmpty()) {
						sql = "with FILT_IN as " + "( select distinct min(creation_date) "
							+ "  FROM fg_s_products_all_v " + "  where stepid = " + stepId + " )"
							+ "SELECT invitemmaterial_id " + " FROM fg_s_products_all_v"
							+ " WHERE creation_date IN " + " (select * from FILT_IN)" + " and stepid = " + stepId
							+ " and rownum<=1";
						String firstStepProduct = generalDao.selectSingleStringNoException(sql);
						toReturn.put("firstStepProduct", firstStepProduct);
					}
					
					String subproject_id = generalUtil.getNull(toReturn.get("SUBPROJECT_ID"));
					String isSubProjEnableSpreadsheet = subproject_id.isEmpty()?"":formDao.getFromInfoLookup("SUBPROJECT", LookupType.ID,
							subproject_id, "ISENABLESPREADSHEET");
					toReturn.put("enableSpreadsheet",isSubProjEnableSpreadsheet);
					if(protocolTypeName.toLowerCase().equals("formulation") && generalUtil.getNull(isSubProjEnableSpreadsheet).equalsIgnoreCase("yes")){
						sql = "select distinct first_value(m.INVITEMMATERIAL_ID) over (partition by p.project_id order by m.INVITEMMATERIALNAME) \n"
								+ " from fg_s_project_v p,\n"
								+ " fg_s_invitemmaterial_v m\n"
								+ " where p.project_id = '"+toReturn.get("PROJECT_ID")+"'\n"
								+ " and m.sourceProjectId = '"+toReturn.get("PROJECT_ID")+"' and m.FORMCODE != 'InvItemMaterialPr'\n";
								//+ " and instr(','||p.INVITEMMATERIAL_ID||',',','||m.INVITEMMATERIAL_ID||',')>0\n";
						String firstStepProduct = generalDao.selectSingleStringNoException(sql);
						toReturn.put("firstStepProduct", firstStepProduct);
					}
				}
			}
			//***************************************
			//*************** getFormParam QuickAction 
			//***************************************
			else if (formCode.equals("QuickAction")) {
				String parentFormCodeEntity;
				String parentId = requestMap.get("PARENT_ID");
				if (parentId != null) {
					parentFormCodeEntity = formDao.getFormCodeEntityBySeqId("", parentId);
					structId = parentId;
					wherePart = parentFormCodeEntity + "_id";
					rowNum = "2";
					String table = "FG_I_ID_CONNECTION_STEP_V";
					String sql = "select distinct * from " + table + " t where t." + wherePart + " = " + structId
							+ " and rownum <" + rowNum;
					toReturn = generalDao.sqlToHashMap(sql);
				}
			}
			//***************************************
			//*************** getFormParam BatchMain 
			//***************************************
			else if (formCode.equals("BatchMain")) {
				String sql = " select * from FG_AUTHEN_BATCHMAIN_V where formId=" + structId;
				if (isNewFormId) {
					String parentFormCodeEntity = "";
					String parentId = requestMap.get("PARENT_ID");
					if (parentId != null) {
						parentFormCodeEntity = formDao.getFormCodeEntityBySeqId("", parentId);
						structId = parentId;
						wherePart = parentFormCodeEntity + "_id";
						rowNum = "2";
						String table = "FG_I_ID_CONNECTION_"
								+ (parentFormCodeEntity.equalsIgnoreCase("Experiment") ? "expr" : parentFormCodeEntity) + "_V";
						sql = "select distinct * from " + table + " t where t." + wherePart + " = " + structId
								+ " and rownum <" + rowNum;
					}
					toReturn = generalDao.sqlToHashMap(sql);
					toReturn.put("LASTSTEP", "0");
					String protocolTypeName = toReturn.get("PROTOCOLTYPENAME")!=null?toReturn.get("PROTOCOLTYPENAME").toString():"";
					if(protocolTypeName.equals("Formulation")){
						String experimentId = toReturn.get("EXPERIMENT_ID")!=null?toReturn.get("EXPERIMENT_ID").toString():"";
						String lastStepId = generalDao.selectSingleStringNoException("select distinct first_value(step_id)over (partition by experiment_id order by to_number(step_id) desc)\n"
								+ " from fg_s_step_v \n"
								+ " where experiment_id = '"+experimentId+"'");
						if(lastStepId.equals(parentId)||parentFormCodeEntity.equals("Experiment")){//created from the last step or from the experiment
							toReturn.put("LASTSTEP", "1");
						}
						String recipeFormulationId = generalDao.selectSingleStringNoException("select recipeformulation_id from fg_s_experiment_v where experiment_id = '"+experimentId+"'");
						toReturn.put("RECIPEFORMULATION_ID", generalUtil.getNull(recipeFormulationId));
						//The Product field displayed the Formulation material that was created automatically for the current Project
						String subproject_id = generalUtil.getNull(toReturn.get("SUBPROJECT_ID"));
						String isSubProjEnableSpreadsheet = subproject_id.isEmpty()?"":formDao.getFromInfoLookup("SUBPROJECT", LookupType.ID,
								subproject_id, "ISENABLESPREADSHEET");
						if(generalUtil.getNull(isSubProjEnableSpreadsheet).equalsIgnoreCase("yes")) {

							sql = "select distinct first_value(m.INVITEMMATERIAL_ID) over (partition by p.project_id order by m.INVITEMMATERIALNAME) \n"
									+ " from fg_s_project_v p,\n"
									+ " fg_s_invitemmaterial_v m\n"
									+ " where p.project_id = '"+toReturn.get("PROJECT_ID")+"'\n"
									+ " and m.sourceProjectId = '"+toReturn.get("PROJECT_ID")+"' and m.FORMCODE != 'InvItemMaterialFr'\n";
									//+ " and instr(','||p.INVITEMMATERIAL_ID||',',','||m.INVITEMMATERIAL_ID||',')>0\n";
							String defaultProduct = generalDao.selectSingleStringNoException(sql);
							toReturn.put("defaultProduct", defaultProduct);
						
						}
					}
					
				}
			}

			//***************************************
			//*************** getFormParam Sample 
			//***************************************
			else if (formCode.equals("Sample")) {
				String sql = "select distinct * from fg_authen_" + formCode + "_v t where t.FORMID = " + formId;
				toReturn = generalDao.sqlToHashMap(sql);
				/*sql = "select LISTAGG(result_id, ',') WITHIN GROUP (ORDER BY 1) from"
						+ "(select distinct result_id,sample_id,"
						+ "count(*) over (partition by sample_id,invitemmaterial_id,result_name) as material_count"
						+ " from fg_i_sampleresults_v)"
						+ " where material_count = 1"
						+ " and sample_id = '"+formId+"'";
				String defaultRows = generalUtil.getNull(generalDao.selectSingleStringNoException(sql));
				toReturn.put("DEFAULT_ROWS_TO_SELECT", defaultRows.replace(",", "-"));*/
				
				String sql_stest_Cancelled = "select t.result_id \r\n" + 
						"from fg_results t, fg_s_selftest_all_v s \r\n" + 
						"where t.sample_id = '" + formId + "' \r\n" + 
						"and s.selftest_id = t.selftest_id\r\n" + 
						"and s.SELFTESTSTATUSNAME = 'Cancelled'";
				String resultCancelledSelfTestCsv = generalDao.getCSVBySql(sql_stest_Cancelled,false);
				toReturn.put("ADHOC_RESULTCANCELLEDSELFTESTCSV", resultCancelledSelfTestCsv);
			}
			
			//***************************************
			//*************** getFormParam Sample 
			//***************************************
			else if (formCode.equals("SampleLink")) {
				String sql = "select distinct * from fg_authen_" + formCode + "_v t where t.FORMID = " + formId;
				toReturn = generalDao.sqlToHashMap(sql);
//				String linkResultValueWarnMessage = "";
//				if(generalUtil.getNull(toReturn.get("IS_RESULT")).equals("1")) {
//					linkResultValueWarnMessage = generalUtil.getSpringMessagesByKey("linkResultValueWarnMessage", "The sample has result");			
//				}
//				toReturn.put("SAMPLE_LINK_STATUS_MESSAGE",linkResultValueWarnMessage);
			}

			//***************************************
			//*************** getFormParam PreparationRef 
			//***************************************
			else if (formCode.equals("PreperationRef")) {
				if (isNewFormId) {
					String parentId = requestMap.get("PARENT_ID");
					if (parentId != null) {
						structId = parentId;
						wherePart = "SAMPLE_ID";
						rowNum = "2";
					}
				}
				String sql = "select distinct * from fg_authen_preperationref_v t where t." + wherePart + " = "
						+ structId + " and rownum <" + rowNum;
				toReturn = generalDao.sqlToHashMap(sql);
				Map<String, String> data = generalDao.getMetaDataRowValues("select t.SOLVENT as \"SOLVENT\","
						+ "decode(count(t.SAMPLE_ID) over (partition by t.SAMPLE_ID),0,'1','0') as \"ISFIRSTPREP\""
						+ "  from FG_S_PREPERATIONREF_ALL_V t " + "where t.PARENTID = '" + structId
						+ "' and SAMESOLVENT = '1'" + generalUtilFormState.getWherePartForTmpData(formCode, structId));
				if (!data.isEmpty()) {
					toReturn.put("SOLVENT", generalUtil.getNull(data.get("SOLVENT")));
					toReturn.put("ISFIRSTPREP", generalUtil.getNull(data.get("ISFIRSTPREP")));
				} else {
					toReturn.put("ISFIRSTPREP", "1");
				}

			}
			//***************************************
			//*************** getFormParam Document
			//***************************************
			else if (formCode.equals("Document")) {
				if (isNewFormId) {
					String projectIdArray = requestMap.get("PARENT_ID");
					if (projectIdArray != null) {
						structId = projectIdArray;
						wherePart = "EXPERIMENT_ID";
						rowNum = "2";
					}
				}
				String sql = "select  distinct *" + " from fg_authen_document_v t where t." + wherePart + " = "
						+ structId + generalUtilFormState.getWherePartForTmpData(formCode, formId) + " and rownum < "
						+ rowNum;
				toReturn = generalDao.sqlToHashMap(sql);
			} //***************************************
				//*************** getFormParam all Template type 
				//***************************************
			else if (formCode.equals("TemplateMain")) {
				if (isNewFormId) {
					String parentId = requestMap.get("PARENT_ID");
					if (parentId != null) {
						structId = parentId;
						wherePart = "EXPERIMENT_ID";
						rowNum = "2";
					}
				}
				String sql = "select  distinct * from fg_authen_TemplateMain_v t where t." + wherePart + " = "
						+ structId + " and rownum <" + rowNum;
				toReturn = generalDao.sqlToHashMap(sql);

			} //***************************************
				//*************** getFormParam all Template type 
				//***************************************
			else if (formCode.equals("Template")) {

				String sql = "select distinct * from fg_authen_" + formCode + "_v t where t.FORMID = " + formId;
				toReturn = generalDao.sqlToHashMap(sql);

				String experimentType = toReturn.get("EXPERIMENTTYPENAMEINFO");
				if (experimentType.contains("HPLC") || experimentType.contains("GC")) {
					toReturn.put("hplc_gc", (experimentType.contains("HPLC")) ? "HPLC" : "GC");
				}

			} else if (formCode.equals("ExpAnalysisReport")||formCode.equals("ExperimentReport")) {
				if (!generalUtil.getNull(requestMap.get("nameId")).isEmpty()) {
					String sql = "select distinct * from fg_formlastsavevalue_name t where t.save_name_id = "
							+ requestMap.get("nameId");
					toReturn = generalDao.sqlToHashMap(sql);
				}
			} else if (formCode.equals("ManualResultsRef")) {
				if (isNewFormId) {
					toReturn.put("IS_USER_OWN", "1");
					String experimentType_id = generalDao.selectSingleStringNoException(
							"select experimenttype_id from fg_s_experiment_v where experiment_id = '"
									+ requestMap.get("PARENT_ID") + "'");
					String experimenttypeName = formDao.getFromInfoLookup("experimenttype", LookupType.ID,
							experimentType_id, "name");
					if (experimenttypeName.equals("Water Content by KF")) {
						String resultTypeId = formDao.getFromInfoLookup("AnalyticResultType", LookupType.NAME,
								"Water Content by KF", "id");
						toReturn.put("RESULTTYPE_ID", resultTypeId);
						String materialId = formDao.getFromInfoLookup("invitemmaterial", LookupType.NAME, "Water",
								"id");
						toReturn.put("MATERIAL_ID", materialId);
						toReturn.put("MATERIAL_FILTER",
								"select invitemmaterial_id from fg_s_invitemmaterial_v where invitemmaterialname = 'Water'");
						toReturn.put("REQUEST_FILTER",
								"select SINGLEREQ_ID from fg_s_requestselect_all_v where parentid = '"
										+ requestMap.get("PARENT_ID") + "' and active=1 and sessionid is null");
					} else {
						toReturn.put("MATERIAL_FILTER", "select materialid from fg_s_component_v where parentid='"
								+ requestMap.get("PARENT_ID") + "' and active = 1 and sessionid is null");
						toReturn.put("REQUEST_FILTER", "select request_id from fg_s_request_v");
					}
				} else {
					String sql = "select distinct * from  fg_authen_ManualResultsRef_v t where t.formId = '" + formId
							+ "' " + generalUtilFormState.getWherePartForTmpData(formCode, requestMap.get("PARENT_ID"));
					toReturn = generalDao.sqlToHashMap(sql);
					String experimenttypeName = formDao.getFromInfoLookup("experimenttype", LookupType.ID,
							toReturn.get("EXPERIMENTTYPE_ID"), "name");
					if (experimenttypeName.equals("Water Content by KF")) {
						toReturn.put("MATERIAL_FILTER",
								"select invitemmaterial_id from fg_s_invitemmaterial_v where invitemmaterialname = 'Water'");
						toReturn.put("REQUEST_FILTER",
								"select SINGLEREQ_ID from fg_s_requestselect_all_v where parentid = '"
										+ requestMap.get("PARENT_ID") + "' and active=1 and sessionid is null");
					} else {
						toReturn.put("MATERIAL_FILTER", "select materialid from fg_s_component_v where parentid='"
								+ requestMap.get("PARENT_ID") + "' and active = 1 and sessionid is null");
						toReturn.put("REQUEST_FILTER", "select request_id from fg_s_request_v");
					}
				}
			} else if (formCode.equals("SampleSelect")) {
				String sql = "select distinct * from  fg_authen_sampleselect_v t where t.formId = '" + formId + "' "
						+ generalUtilFormState.getWherePartForTmpData(formCode, requestMap.get("PARENT_ID"));
				toReturn = generalDao.sqlToHashMap(sql);
			} else if (formCode.equals("UsersCrew")) {
				String sql = "select distinct * from  fg_authen_userscrew_v t where t.formId = '" + formId + "' "
						+ generalUtilFormState.getWherePartForTmpData(formCode, requestMap.get("PARENT_ID"));
				toReturn = generalDao.sqlToHashMap(sql);
			} else if (formCode.equals("PrManualResultRef")) {
				if (isNewFormId) {
					String parentId = requestMap.get("PARENT_ID");
					if (parentId != null) {
						structId = parentId;
						wherePart = "EXPERIMENT_ID";
						rowNum = "2";
						toReturn.put("SAMPLE_ID",
						generalDao.selectSingleStringNoException("select SAMPLE_ID from fg_s_sampleselect_all_v where parentid = '"
								+ requestMap.get("PARENT_ID") + "' and active=1 and sessionid is null"));
					}
				}
				/*String sql = "select  distinct * from fg_authen_prmanualresultref_v t where t." + wherePart + " = "
						+ structId + " and rownum <" + rowNum;*///the view is not in use
				//toReturn = generalDao.sqlToHashMap(sql);
				
				
				toReturn.put("REQUEST_ID",
						generalDao.selectSingleStringNoException("select SINGLEREQ_ID from fg_s_requestselect_all_v where parentid = '"
								+ requestMap.get("PARENT_ID") + "' and active=1 and sessionid is null"));

			} else if (formCode.equals("MultiStep")) {
				String parentId = requestMap.get("PARENT_ID");
				String sql = "select  distinct listagg(step_id,',') WITHIN GROUP (ORDER BY step_id) as STEP_ID  from fg_s_step_all_v t"
						+ " where t.experiment_id = '"
						+ formDao.getFromInfoLookup("Step", LookupType.ID, parentId, "EXPERIMENT_ID")
						+ "' and sourceSteps = '1' "
						+ " and formid <>'" + parentId + "' and rownum <" + rowNum;
				toReturn = generalDao.sqlToHashMap(sql);
				toReturn.put("FORMNUMBERID",
						formDao.getFromInfoLookup("Step", LookupType.ID, parentId, "FORMNUMBERID"));
				sql = "select distinct LISTAGG(mr.formid,',') WITHIN GROUP (ORDER BY mr.formid)"
						+ " from fg_s_step_all_v s," + " fg_s_materialref_v mr" + " where s.experiment_id = '"
						+ formDao.getFromInfoLookup("Step", LookupType.ID, parentId, "EXPERIMENT_ID") + "'"
						+ " and mr.parentid = s.step_id" + " and mr.tabletype in ('Solvent','Product')"
						+ " and mr.sessionid is null and nvl(mr.active,'1') = '1'" + ""
						+ " and s.sourceSteps = '1' and s.formnumberid = "
						+ "(select distinct max(to_number(formnumberId))" + " from fg_s_step_all_v"
						+ " where experiment_id = '"
						+ formDao.getFromInfoLookup("Step", LookupType.ID, parentId, "EXPERIMENT_ID") + "'"
						+ " and sourceSteps = '1' and formId <> '" + parentId + "')";
				toReturn.put("DEFAULTSELECT", generalUtil.getNull(generalDao.selectSingleStringNoException(sql)));
			} else if (formCode.equals("SearchReport")) {
				//				String allSearch_comments= generalDao.selectSingleStringNoException("select comments as all_data_comments from fg_sys_sched where sched_name = 'schedCorrectAllData' and rownum<=1");
				String sql = "select decode(status,'S',to_char(LAST_END_DATE,'DD/MON/YYYY hh24:mi'),'F',to_char(LAST_END_DATE,'DD/MON/YYYY hh24:mi')||' failed!') as end_date, comments  from fg_sys_sched where sched_name = 'correctRecentSearchData' and rownum<=1";
				toReturn = generalDao.sqlToHashMap(sql);
				if (toReturn == null || toReturn.size() == 0) {
					toReturn.put("END_DATE", "NA");
					toReturn.put("COMMENTS", "NA");
				}
				//				toReturn.put("allSearch_comments", ""); // will be present in system log
			}else if (formCode.equals("MaterialsPeaks")) {
				String parentId = requestMap.get("PARENT_ID");
				String sql = "select t.EXPERIMENTTYPENAME from FG_I_CONN_REQUEST_OPTYPE_V t where t.REQUEST_ID = '"
						+ parentId+"'";
				String defaultSample = generalDao.selectSingleStringNoException("select SAMPLEID from FG_S_SAMPLEDATAREF_V where parentid ='"+parentId+"' and parentid in(\r\n" + 
						"select parentid from FG_S_SAMPLEDATAREF_V t group by parentid having count(t.SAMPLEID)<2)");
				toReturn = generalDao.sqlToHashMap(sql);
				toReturn.put("DEFAULTSAMPLE", generalUtil.getNull(defaultSample));
			}
			else if (formCode.equals("ReportDesignExp")) {
				//generalUtilDesignData.getStepDesignMap().clear();
				/*generalUtilDesignData.getStepDesignMapwithList().clear();
				generalUtilDesignData.getStepImpuritesDesignMap().clear();
				generalUtilDesignData.getExperimentDataList().clear();
				generalUtilDesignData.getParametersDataMap().clear();*/
				String sql;
				
				if (!isNewFormId) {
					/*//sql = "select distinct * from fg_s_stepdesignexp_v t where t.STEP_ID=199212";
					sql = "select STEPSDESIGN from Fg_s_Reportdesignexp_All_v t where t.formId=" + formId;
					String stepDesignId = generalDao.selectSingleString(sql);
					sql = "select IMPURITIESSTEPSDESIGN from Fg_s_Reportdesignexp_All_v t where t.formId=" + formId;
					String impuritiesDesignSteps = generalDao.selectSingleString(sql);
					Map<String, List<String>> stepsDesignMap = new HashMap<String, List<String>>();
					Map<String, List<String>> impuritiesDesignStepMap = new HashMap<String, List<String>>();
					String stepsDesign=generalDao.selectSingleString("select t.file_content from fg_clob_files t where t.file_id = '" + stepDesignId + "'");
					if(stepsDesign!=null)
					stepsDesignMap = generalUtil.jsonStringToMapList(stepsDesign);
					generalUtilDesignData.setStepDesignMapwithList(stepsDesignMap);
					if(impuritiesDesignSteps!=null)
					impuritiesDesignStepMap = generalUtil.jsonStringToMapList(impuritiesDesignSteps);
					generalUtilDesignData.setStepImpuritesDesignMap(impuritiesDesignStepMap);
					String sql_ = "select * from FG_AUTHEN_" + formCode.toUpperCase() + "_V t where t.FORMID = "
							+ formId;
					toReturn = generalDao.sqlToHashMap(sql_);
					*/
					
					if(!formId.equals("-1"))
					generalUtilDesignData.fillDesignDataSession(formId);
				

				}
			
			}else if (formCode.equals("ReportTable")) {
				String tableType = generalUtil.getNull(requestMap.get("DOMID"));
				if(tableType.equals("actions")){
					String exp_id = generalUtil.getNull(requestMap.get("EXPERIMENT_ID"));
					String stepNum = generalUtil.getNull(requestMap.get("STEPSEQ"));
					String expNum = formDao.getFromInfoLookup("Experiment", LookupType.ID, exp_id, "formNumberId");
					String title = "All RUN Actions - Exp. "+expNum+", Step "+stepNum;
					toReturn.put("TITLE", title);
				}else if(tableType.equals("experimentResults")){
					String exp_id = generalUtil.getNull(requestMap.get("PARENT_ID"));
					String expNum = formDao.getFromInfoLookup("Experiment", LookupType.ID, exp_id, "formNumberId");
					String title = "All experiment results - Exp No. "+expNum;
					toReturn.put("TITLE", title);
				}
			}else if (formCode.equals("SpreadsheetTempla")){
				String sql = "select * from FG_AUTHEN_" + formCode.toUpperCase() + "_V t where t.FORMID = "
						+ formId;
				toReturn = generalDao.sqlToHashMap(sql);
				sql = "select 1 from FG_FAVORITE where object_id = '"+formId+"' and creator_id = '"+userId+"'";
				String isFavorite = generalDao.selectSingleStringNoException(sql);
				toReturn.put("IS_FAVORITE", generalUtil.getEmpty(isFavorite, "0"));
			} else if(formCode.equals("HistoricalData") || formCode.equals("HistoricalDataMain")){
				String parentId = requestMap.get("PARENT_ID");
				SimpleDateFormat formatter = new SimpleDateFormat(generalUtil.getConversionDateFormat());
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(formatter.parse(formatter.format(new Date())));
				String toDate = formatter.format(calendar.getTime());
				calendar.add(Calendar.MONTH, -3);
				String fromDate = formatter.format(calendar.getTime());
				toReturn.put("EXPERIMENT_ID", generalUtil.getEmpty(parentId,"ALL"));
				toReturn.put("FROMDATE", fromDate);
				toReturn.put("TODATE", toDate);//necessary for the historical data table when the page is firstly initiated
			}
			else if (formCode.equals("InvItemSamplesMain")) {

				String sql="select  to_char(sysdate -30,'DD/MM/YYYY') || ';' || to_char(sysdate,'DD/MM/YYYY') || ';CREATION_DATE' as defaultDate FROM DUAL";
				toReturn = generalDao.sqlToHashMap(sql);
			}
			else if (formCode.equals("ViewSpreadsheet")) {
				String spreadsheet_id =requestMap.get("PARENT_ID");/* generalDao.selectSingleStringNoException("select t.spreadsheetExcel \n"
						+ " from fg_s_experiment_v t\n"
						+ " where t.formid = '"+requestMap.get("PARENT_ID")+"'");*/
				toReturn.put("SPREAD_ID",generalUtil.getNull(spreadsheet_id));
			}
			else if(formCode.equals("RecipeFormulation")){
				String origin = "";
				String defaultStatus = "";
				String approvalDate="";
				String formulationTypeId="";//APPROVAL_DATE
				if (isNewFormId) {
					String parentId = requestMap.get("PARENT_ID");
					if (parentId != null) {
						structId = parentId;
						if(!structId.equals("-1")){
							String parentFormCode = formDao.getFormCodeEntityBySeqId("",structId);
							if(parentFormCode.equals("Experiment")){
								String formulationType=generalDao.selectSingleString("select formulationType_id from fg_s_experiment_pivot t where t.formid="+structId);
								wherePart = "EXPERIMENT_ID";
								rowNum = "2";
								origin = "Experiment";
								defaultStatus = "Active";
								formulationTypeId=formulationType;
							}
						}else {//created from the inventory
							origin =  "Manual";
							defaultStatus = "Planned";
						}
					} else {
						origin = "Data Bank";
						defaultStatus = "Approved";
						DateFormat dateFormat = new SimpleDateFormat(generalUtil.getConversionDateFormat());
						Date date = new Date(); //current date for necessary cases
						approvalDate = dateFormat.format(date);
					}
				}
				String sql = "select distinct "
						+ (rowNum.equals("2")
								? getRelevantAuthenColumns(formCode,
										"RECIPESTATUSNAME,RECIPEFORMULATIONNAME,STATUS_ID,PROJECT_ID,EXPERIMENT_ID,CREATOR_ID,RECIPESTATUSNAME")
								: "*")
						+ " from fg_authen_RECIPEFORMULATION_v t where t." + wherePart + " =" + structId
						+ " and rownum < " + rowNum;
				toReturn = generalDao.sqlToHashMap(sql);
				if(isNewFormId){
					toReturn.put("ORIGIN", origin);
					toReturn.put("DEFAULT_STATUS", defaultStatus);
					toReturn.put("STATUS_ID", formDao.getFromInfoLookup("recipestatus", LookupType.NAME, defaultStatus, "id"));
					toReturn.put("APPROVAL_DATE", approvalDate);
					toReturn.put("RECIPESTATUSNAME", defaultStatus);
					if(origin.equals("Experiment")){
						sql = "select experimentname,density from fg_s_experiment_v  where experiment_id = '"+structId+"'";
						Map<String,String> expData = generalDao.sqlToHashMap(sql);
						String experimentname = expData.get("EXPERIMENTNAME");
						String expDensity = expData.get("DENSITY");
						toReturn.put("EXPERIMENT_ID", structId);
						toReturn.put("EXPERIMENTNAME", experimentname);
						toReturn.put("FORMULATIONTYPE_ID", formulationTypeId);
						toReturn.put("DENSITY", expDensity);
					}
					if(!origin.equals("Data Bank")){
						toReturn.put("CREATOR_ID", userId);
					}
				}
			}
			
			//***************************************
			//*************** ELSE getFormParam by form type (GENERAL TO ALL FORMS)
			//***************************************
			else {
				switch (formType) {
					//--------------------------------
					//---- Form Type struct
					//--------------------------------	
					case STRUCT: {
						String sql = "select * from FG_AUTHEN_" + formCode.toUpperCase() + "_V t where t.FORMID = "
								+ formId;
						toReturn = generalDao.sqlToHashMap(sql); //TODO Check skyline imp
					}
						break;
					case INVITEM: {
						String sql = "select * from FG_AUTHEN_" + formCode.toUpperCase() + "_V t where t.FORMID = "
								+ formId;
						toReturn = generalDao.sqlToHashMap(sql); //TODO Check skyline imp
					}
						break;
					case MAINTENANCE: {
						String sql = "select * from FG_AUTHEN_" + formCode.toUpperCase() + "_V t where t.FORMID = "
								+ formId;
						toReturn = generalDao.sqlToHashMap(sql); //TODO Check skyline imp
					}
						break;

					//--------------------------------
					//---- Form Type ref/ attach
					//--------------------------------	
					case ATTACHMENT: {
						String sql = "select * from FG_AUTHEN_" + formCode.toUpperCase() + "_V t where t.FORMID = "
								+ formId;
						toReturn = generalDao.sqlToHashMap(sql); //TODO Check skyline imp
					}
						break;
					case REF: {
						String sql = "select * from FG_AUTHEN_" + formCode.toUpperCase() + "_V t where t.FORMID = "
								+ formId;
						toReturn = generalDao.sqlToHashMap(sql); //TODO Check skyline imp
					}
						break;
					//--------------------------------
					//---- Form Type - getFormParam REPORT:
					//--------------------------------	
					case REPORT: {
						String sql = "select * from FG_AUTHEN_REPORT_V t where t.user_id = " + userId
								+ " and rownum < 1000 ";
						toReturn = generalDao.sqlToHashMap(sql); //TODO Check skyline imp
					}
						break;
					//--------------------------------
					//---- DEFAULT
					//--------------------------------	
					default:
						break;
				}
			}
			//			generalTask.postDBTransactionHandler(dbTransactionId); // only from job
		} catch (Exception e) {
			generalTaskDao.onTransactionFailure(formCode, formId, dbTransactionId);
			generalUtilLogger.logWriter(LevelType.ERROR,
					"Error in init Query! formCode=" + formCode + ", formId=" + formId + ", formType= " + formType,
					ActivitylogType.GeneralError, formId, e, null);
		}
		//return map...
		return toReturn;
	}

	/**
	 * 
	 * @param formcode
	 *            name of the form
	 * @param columnsToDrop
	 *            columns that are not relevant
	 * @return a csv list of column names from authentication table
	 */
	private String getRelevantAuthenColumns(String formcode, String columnsToDrop) {
		String tableName = "FG_AUTHEN_" + formcode + "_V";
		String colList = "NULL," + generalDao.getTableColCsv(tableName) + ",NULL";
		if (!columnsToDrop.isEmpty()) {
			List<String> colToDropList = Arrays.asList(columnsToDrop.split(","));
			for (String colToDrop : colToDropList) {
				colList = colList.replaceAll("," + colToDrop + ",", ",NULL,");
			}
		}
		return colList;
	}
 
	@Override
	public String getFormPathInfo(long stateKey, String formCode, String userId, String formId, FormType formType,
			boolean isNewFormId) {

		String formPath = "";
		try {
			if (formType.name().equals("REF") || formType.name().equals("SELECT") || formCode.equals("Document")) {
				String parentId = generalUtilFormState.getFormParam(stateKey, formCode, "$P{PARENT_ID}");
				if (!generalUtil.getNull(parentId).isEmpty()) {
					String parentFormCode = formDao.getFormCodeBySeqId(parentId);
					formPath = generalUtilFormState.getFormParam(stateKey, parentFormCode, "$P{FORMPATH}");
				}
			} /*else if(formCode.equals("Sample") || formCode.equals("Request")){
				//TODO
				}*/else if (formType.name().equals("STRUCT") || formType.name().equals("INVITEM")) {
				formPath = generalUtilFormState.getFormParam(stateKey, formCode, "$P{FORMPATH}");
			}
		} catch (Exception ex) {
		}
		return formPath == null ? "" : formPath;
	}

	@Override
	public Map<String, String> onIntegrationEvent(String formCode, String userId, String formId, FormType formType,
			boolean isNewFormId, Map<String, String> requestMap, long stateKey) {
		Map<String,String> paramMapReturn = new HashMap<String,String>();
		if (formCode.equals("Step")) {
			// note: steps has DB functions (in fg_adama.INIT_STEP<>_DATA) that insert init step data and related table
			if (isNewFormId) {
				String parentId = requestMap.get("PARENT_ID");
				String protocolTypeName = formDao.getFromInfoLookup("Experiment", LookupType.ID, parentId, "PROTOCOLTYPENAME");
				String sql = "select formid" + " from fg_s_step_v" + " where experiment_id = '" + parentId + "'"
						+ " and formnumberid = " + "(select distinct max(to_number(formnumberId))"

						+ " from fg_s_step_v" + " where experiment_id = '" + parentId + "'" + " and formId <> '"
						+ formId + "'"
						+ (protocolTypeName.equals("Continuous Process")?" and PREPARATION_RUN = 'Run'":"")
						+")";//gets the previous step
				String prevStepFormId = generalDao.selectSingleStringNoException(sql);
				if (!prevStepFormId.isEmpty()) {//if it's empty, then it is the first step and there's no need to execute the copy
					try {
						sql = "select distinct count(*) from fg_s_materialref_v where parentid = '" + formId + "'";
						String refreshBeforeSave = generalDao.selectSingleStringNoException(sql);
						if (!refreshBeforeSave.isEmpty() && !refreshBeforeSave.equals("0")) {
							return paramMapReturn;
						}
						integrationEvent.copyReactionFromPrevStep(formId, prevStepFormId, userId);
					} catch (Exception e) {
						generalUtilLogger.logWriter(
								LevelType.ERROR, "Error in onNewIntegrationEvent Query! formCode=" + formCode
										+ ", formId=" + formId + ", formType= " + formType,
								ActivitylogType.GeneralError, formId, e, null);
						e.printStackTrace();
					}
				}
				sql = "select distinct inviteminstrument_id from fg_s_instrumentref_pivot where includeInAllSteps = '1' and parentid in (select formid from fg_s_step_v where experiment_id = '"
						+ parentId + "') and sessionid is null and active='1'";
				List<String> instrumentRefToCopy = generalDao.getListOfStringBySql(sql);
				for (String instrumentId : instrumentRefToCopy) {
					String instrumentRefId = formSaveDao.getStructFormId("InstrumentRef");
					sql = "insert into fg_s_instrumentref_pivot (formid,INVITEMINSTRUMENT_ID,parentId,formCode,formcode_entity,timestamp,change_by,created_by,creation_date,active)"
							+ " values ('" + instrumentRefId + "','" + instrumentId + "','" + formId
							+ "','InstrumentRef','InstrumentRef',sysdate,'" + userId + "','" + userId
							+ "',sysdate,'1')";
					formSaveDao.insertStructTableByFormId(sql, "fg_s_instrumentref_pivot", instrumentRefId);
				}
			}
		} else if (formCode.equals("SelfTest")) {
			if (isNewFormId) {
				//*** from pre-save selftestmain event...
				commonFunc.onSelfTestNewEvent(userId, formId, requestMap.get("PARENT_ID"), paramMapReturn);
			}
		}else if (formCode.equals("Request")) {
			if (isNewFormId) {
				String parentId = requestMap.get("PARENT_ID");
				String parentForm = formDao.getFormCodeBySeqId(parentId);
				// Select experiment origin ID if exists
				String sql = "";
				String originExperimentId = "";
				String sessionId_ = "";
				if (generalUtil.getNull(parentForm).toLowerCase().contains("experiment")) {
					originExperimentId = parentId;
				} else if (generalUtil.getNull(parentForm).toLowerCase().contains("step")) {
					sql = " select EXPERIMENT_ID from fg_authen_requestmain_exp_v t where t.EXPERIMENT_ID = ( select t1.EXPERIMENT_ID from fg_s_step_all_v t1 where t1.step_ID = '"
							+ parentId + "') ";
					originExperimentId = generalDao.selectSingleStringNoException(sql);
				} else if (generalUtil.getNull(parentForm).toLowerCase().contains("action")) {
					sql = " select EXPERIMENT_ID from fg_authen_requestmain_exp_v t where t.EXPERIMENT_ID = ( select t1.EXPERIMENT_ID from fg_s_action_all_v t1 where t1.ACTION_ID = '"
							+ parentId + "') ";
					originExperimentId = generalDao.selectSingleStringNoException(sql);
					String sampleListCsv = generalDao.selectSingleStringNoException(
							"select distinct sampletable from FG_S_SAMPLESELECT_ALL_V where PARENTID = '"
									+ parentId + "' and active=1 and SESSIONID is null");
					List<String> sampleList =  Arrays.asList(sampleListCsv.split(","));
					/*List<String> sampleList = generalDao.getListOfStringBySql(
							"select distinct SAMPLE_ID from FG_S_SAMPLESELECT_ALL_V where PARENTID = '"
									+ parentId + "' and active=1 and SESSIONID is null");*/
					requestMap.put("ACTION_SAMPLE_ID", sampleList.isEmpty() ? "" : generalUtil.listToCsv(sampleList));
				} else if (generalUtil.getNull(parentForm).toLowerCase().contains("test")) {// selftest
					sql = " select EXPERIMENT_ID from fg_authen_requestmain_exp_v t where t.EXPERIMENT_ID = ( select t1.EXPERIMENT_ID from fg_s_selftest_all_v t1 where t1.SELFTEST_ID = '"
							+ parentId + "') ";
					originExperimentId = generalDao.selectSingleStringNoException(sql);
				} else if (generalUtil.getNull(parentForm).toLowerCase().contains("workup")) {
					sql = " select EXPERIMENT_ID from fg_authen_requestmain_exp_v t where t.EXPERIMENT_ID = ( select t1.EXPERIMENT_ID from fg_s_workup_all_v t1 where t1.WORKUP_ID = '"
							+ parentId + "') ";
					originExperimentId = generalDao.selectSingleStringNoException(sql);
				}
				sessionId_ = generalUtilFormState.getSessionId(formId);
				if (!generalUtil.getNull(originExperimentId).equals("")) {
					String ExperimentSelectId = formSaveDao.getStructFormId("ExperimentSelect");
					formSaveDao
							.insertStructTableByFormId(
									"insert into FG_S_EXPERIMENTSELECT_PIVOT (FORMID,TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMCODE,PARENTID,EXPERIMENT_ID,CREATED_BY,CREATION_DATE) "
											+ "values ('" + ExperimentSelectId + "', sysdate, " + userId
											+ "," +sessionId_+",1,'ExperimentSelect','" + formId + "','" + originExperimentId
											+ "','" + userId + "',SYSDATE)",
									"FG_S_EXPERIMENTSELECT_PIVOT", ExperimentSelectId);
				}

				// insert the samples connected to the parent entity to the
				// request
				String parentFormCodeEntity = formDao.getFormCodeEntityBySeqId("", parentId);

				List<String> sampleList = new ArrayList<>();
				if (parentFormCodeEntity.equals("Experiment") || parentFormCodeEntity.equals("Step") 
						|| parentFormCodeEntity.equals("InvItemBatch")) {
					sampleList = !generalUtil.getNull(requestMap.get("smartSelectList")).isEmpty()
							? Arrays.asList(requestMap.get("smartSelectList").split(",")) : sampleList;
				} else if (parentFormCodeEntity.equals("Action")) {
					sampleList = !generalUtil.getNull(requestMap.get("ACTION_SAMPLE_ID")).isEmpty()
							? Arrays.asList(requestMap.get("ACTION_SAMPLE_ID").split(",")) : sampleList;
				} else if(parentFormCodeEntity.equals("Sample")){
					sampleList = Arrays.asList(parentId);
				} else {
					sampleList = generalDao.getListOfStringBySql(
							"select distinct SAMPLE_ID from FG_S_SAMPLESELECT_ALL_V where PARENTID = '" + parentId
									+ "' and SESSIONID is null");
				}

				if(sampleList!= null && !sampleList.isEmpty()){
					/*String SampleSelectHolderId = formSaveDao.getStructFormId("SampleSelectHolder");
					String sql_ = "insert into FG_S_SampleSelectHolder_PIVOT t"
					    + " (t.FORMID,t.TIMESTAMP,t.CHANGE_BY,t.CREATION_DATE,t.CREATED_BY,t.SESSIONID,t.ACTIVE,t.FORMCODE,t.FORMCODE_ENTITY, sampleTable, PARENTID)"
						+ " values(" + SampleSelectHolderId + ", sysdate, " + userId + ", sysdate, " + userId
						+ ","+ sessionId_+", 1, 'SampleSelectHolder', 'SampleSelectHolder', '" + generalUtil.listToCsv(sampleList) + "', " + formId + ")";
						 formSaveDao.insertStructTableByFormId(sql_, "FG_S_SampleSelectHolder_PIVOT", SampleSelectHolderId);
				  */ 
					formDao.insertToSelectTable("SampleSelectHolder", formId, "SAMPLETABLE", sampleList,
							false, userId, sessionId_);
				}
				for (String sampleId : sampleList) {
					String samplerefId = formSaveDao.getStructFormId("SAMPLEDATAREF");
					sql = "insert into FG_S_SAMPLEDATAREF_PIVOT t"
							+ " (t.FORMID,t.TIMESTAMP,t.CHANGE_BY,t.CREATION_DATE,t.CREATED_BY,t.SESSIONID,t.ACTIVE,t.FORMCODE,t.FORMCODE_ENTITY, SAMPLEID, PARENTID)"
							+ " values(" + samplerefId + ", sysdate, " + userId + ", sysdate, " + userId
							+ ","+ sessionId_+", 1, 'SAMPLEDATAREF', 'SAMPLEDATAREF', " + sampleId + ", " + formId + ")";
					formSaveDao.insertStructTableByFormId(sql, "FG_S_SAMPLEDATAREF_PIVOT", samplerefId);

				}
				
			}
		}
		else if(formCode.equals("ExperimentCP")) {
			// calculate all runs data on load
			integrationCalc.doCalcRuns("-1", formId, new StringBuilder());
		}
		else if(formCode.equals("RecipeFormulation")){
			if(isNewFormId){
				//copy the planned composition data to the new recipe
				String parentId = requestMap.get("PARENT_ID");
				String sql = "select composition_id from fg_S_composition_v\n"
						+ " where parentid = '"+parentId+"'\n"
						+ " and tableType = 'expComposition'\n"
						+ " and sessionid is null\n"
						+ " and active = 1";
				List<String> compositionList = generalDao.getListOfStringBySql(sql);
				for(String composition_id:compositionList){
	        		String cloneWherepart = " and sessionid is null and active = 1";
	        		String cloneCompositionFormId = formSaveDao.cloneStructTable(composition_id,cloneWherepart);
	        		List<String> colList = Arrays.asList("parentId", "ACTIVE","SESSIONID","TABLETYPE","ORIGIN");
					sql = "update FG_S_composition_PIVOT set parentId = '" + formId + "',ACTIVE = 1, SESSIONID = null,TABLETYPE='recipeComposition',ORIGIN='Experiment' where FORMID='"
							+ cloneCompositionFormId + "' ";
					formSaveDao.updateStructTableByFormId(sql, "FG_S_composition_PIVOT", colList,
							cloneCompositionFormId);
				}
			}
		} else if (formCode.equals("InvItemBatch")) {
			if (isNewFormId) {
				String materialId = requestMap.get("PARENT_ID");
				String materialProtocolType = formDao.getFromInfoLookup("InvItemMaterial", LookupType.ID, materialId, "MATERIALPROTOCOLTYPE");
				
				if(!materialProtocolType.equals("Chemical Material")) {
					commonFunc.copyComponentTablefromMaterialToBatch(materialId,userId,formId);
				}
			}
		} else if (formCode.equals("ExperimentReport")) {
			// on loading the form DELETE AND insert the rows with parentid to this fg_s_ReportFilterRef_pivot with parentid null and state key to rowstatekey for having the saved data (in the save scheme we need to save with this concept) - maybe in save display (with no nameid) parentid will be -1 with userid  (because we do not have name id)
			// need to clean up fg_s_ReportFilterRef_pivot rows with t.timestamp < sysdate -1 and rowstatekey not null
			String nameId = requestMap.get("nameId");
			String del_sql = " delete from fg_s_reportfilterref_pivot\n"
					+ "where rowstatekey = '"+stateKey+"'\n";
				generalDao.updateSingleStringNoTryCatch(del_sql);
			if(!generalUtil.getNull(nameId).isEmpty() && !nameId.equals("-1")) {
				//String newformId = formSaveDao.getStructFormId(formCode);
				String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, formId);
                String nameIdasParentId = "-1";
			    String sql = "insert into FG_S_ReportFilterRef_PIVOT "
						+ "(TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE,FORMCODE_ENTITY,CREATED_BY,CREATION_DATE,ROWSTATEKEY,stepname,rulename,RULECONDITION,columnsSelection,ColumnName,tabletype)"
						+ " select sysdate,'" + userId + "'," + sessionId + ",'1',fg_get_struct_form_id('ReportFilterRef')," + nameIdasParentId + ",'"
						+ "ReportFilterRef" + "','" + "ReportFilterRef" + "','" + userId + "',sysdate,'" + stateKey + "',stepname,rulename,RULECONDITION,columnsSelection,ColumnName,tabletype "
								+ " from fg_s_reportfilterref_pivot where parentid = '"+nameId+"'";
				//String insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_" + "ReportFilterRef" + "_PIVOT", newformId);
				formSaveDao.updateSingleStringInfoNoTryCatch(sql);
				
			}else {
				String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, formId);
                String nameIdasParentId = "-1";
			    String sql = "insert into FG_S_ReportFilterRef_PIVOT "
						+ "(TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE,FORMCODE_ENTITY,CREATED_BY,CREATION_DATE,ROWSTATEKEY,stepname,rulename,RULECONDITION,columnsSelection,ColumnName,tabletype)"
						+ " select sysdate,'" + userId + "'," + sessionId + ",'1',fg_get_struct_form_id('ReportFilterRef')," + nameIdasParentId + ",'"
						+ "ReportFilterRef" + "','" + "ReportFilterRef" + "','" + userId + "',sysdate,'" + stateKey + "',stepname,rulename,RULECONDITION,columnsSelection,ColumnName,tabletype "
								+ " from fg_s_reportfilterref_pivot where parentid = '"+userId+"'";
				//String insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_" + "ReportFilterRef" + "_PIVOT", newformId);
				formSaveDao.updateSingleStringInfoNoTryCatch(sql);
			}
		    System.out.println("delete and insert data to fg_s_ReportFilterRef_pivot with save schem display values, nameId=" + nameId + ", userid=" + userId);
		}
		
		
		
		return paramMapReturn;
	}
	
	@Override
	public String showFormPathDisplayHtml(String path) {
		String arrowRightIcon = "";
		if(isTreeRoot == 1){
			arrowRightIcon = "<span class=\"jstree-closed\"><i style=\"background-image:url(../skylineFormWebapp/dist/themes/default/32px.png); "
					+ "background-position: -100px -4px;"
					+ "height: 15px;"
					+ " width: 24px; "
					+ "background-repeat: no-repeat;"
					+ "display: inline-block;\""
					+ " onclick=\"openFloatTree(this,'%1$s','%2$s','%3$s')\"></i> </span> \n";//style=\"margin-bottom: 2px;margin-left: 6px;\"
		} else {
			arrowRightIcon = "<span><img src=\"../skylineFormWebapp/images/arrow_right.png\" class=\"arrow_right\"  style=\"margin-bottom: 2px;margin-left: 6px;\"> </span> \n";
		}
		String toReturn = "";
		try {
			if(path.isEmpty()){
				return toReturn;
			}
			JSONObject json = new JSONObject(path);
			JSONArray pathList = json.getJSONArray("path");
			String currentFormCode= generalUtil.getJsonValById(pathList.get(pathList.length()-1).toString(),"name").split(":")[0];

			for(int i= 0;i<pathList.length()-1;i++){
				String p = pathList.get(i).toString();
				String detailsToDisplay = generalUtil.getJsonValById(p,"name");
				String formCode = detailsToDisplay.substring(0,detailsToDisplay.indexOf(":")); //fix bug 8996
				String name = detailsToDisplay.substring(detailsToDisplay.indexOf(":")+1);
				String id = generalUtil.getJsonValById(p,"id");
				//String []detailsToDisplay = generalUtil.getJsonValById(p,"name").split(":");
				//String formCode = detailsToDisplay.length>0?generalUtil.getJsonValById(p,"name").split(":")[0]:"";
				//String name = detailsToDisplay.length>1?generalUtil.getJsonValById(p,"name").split(":")[1]:"";
					
				String hrefStr="";
				if (currentFormCode.startsWith("Step")&&formCode.startsWith("Experiment")) {
					String currentStepName=generalUtil.getJsonValById(pathList.get(pathList.length()-1).toString(),"name").split(":")[1];
					List<String> steps=new ArrayList<String>();
					steps=getStepsByExperimentId(id,currentStepName);
					String navigationPopUpDiv = " <div class=\"dropdown-content\" style=\"width:100px;\">\n";
					String stepsLinkList="";
					for (String stepInfo : steps) {
						String stepName=stepInfo.split(",")[0];
						String stepId=stepInfo.split(",")[1];
						String stepFormCode=stepInfo.split(",")[2];
						stepsLinkList=stepsLinkList+ "<a tabindex=\"0\" aria-controls=\"action\" href=\"#\" onclick=\"checkAndNavigate(['"+stepId+"','"+stepFormCode+"'])\"><span>"+stepName+"</span></a>";
					}
					hrefStr ="<div class=\"navigation-dropdown\"  style=\"display:inline-block;\">"
							    + "<div class=\"dropdown \">	\n"
								+ "<a onClick=\"checkAndNavigate(['"+id+"','"+formCode+"'])\" >" +name+"</a> " 
								+ navigationPopUpDiv
								+ stepsLinkList
								+ "</div>"
								+ "</div>"
								+ "</div>";
					} else {
						hrefStr	="<a href='#' onClick=\"checkAndNavigate(['"+id+"','"+formCode+"'])\" >" +name+ "</a>";//class=\"breadcrumb_link\"				}
				    }
				if(isTreeRoot == 1){
					toReturn +=  String.format(arrowRightIcon,id,formCode,name)+hrefStr;
				} else {
					if(i == pathList.length()-2 ){
						toReturn += hrefStr;
					} else {
						toReturn +=  hrefStr + String.format(arrowRightIcon,id,formCode,name);
					}
				}
				//
			}
		}
		catch(Exception ex){
			generalUtilLogger.logWriter(LevelType.WARN,
					"Error in eval path =" + path,
					ActivitylogType.GeneralError, "-1", ex, null);
			ex.printStackTrace();
			toReturn = "";
		}
		return toReturn;
	}
	
	private List<String> getStepsByExperimentId(String id,String stepName) {
		
		String sql="select t.STEPNAME ||',' || STEP_ID || ',' || FORMCODE as stepInfo from fg_s_step_all_v  t where t.EXPERIMENT_ID='"+id+"' and STEPNAME <> '"+stepName+"'  order by t.STEPNAME";
		List<String> stepsList=generalDao.getListOfStringBySql(sql);
		
		return stepsList;
		
	}
}
