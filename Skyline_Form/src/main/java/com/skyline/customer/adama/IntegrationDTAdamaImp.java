package com.skyline.customer.adama;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.BeanType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.FormType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.LookupType;
import com.skyline.form.bean.SqlPermissionListObj;
import com.skyline.form.bean.ValidationCode;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.FormSaveDao;
import com.skyline.form.dal.FormSaveElementDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.dal.GeneralTaskDao;
import com.skyline.form.dal.SearchSqlDao;
import com.skyline.form.service.GeneralChemLocatorUtil;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilConfig;
import com.skyline.form.service.GeneralUtilFormState;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.form.service.GeneralUtilPermission;
import com.skyline.form.service.IntegrationCalc;
import com.skyline.form.service.IntegrationDT;
import com.skyline.form.service.IntegrationValidation;

@Service
public class IntegrationDTAdamaImp implements IntegrationDT {

	@Autowired
	private GeneralChemLocatorUtil generalChemLocatorUtil;
	
	@Autowired
	private CommonFunc commonFunc;

	@Autowired
	private FormIdCalc formIdCalc;

	@Autowired
	protected GeneralTaskDao generalTaskDao;

	@Autowired
	private GeneralDao generalDao;

	@Autowired
	private FormDao formDao;

	@Autowired
	private FormSaveDao formSaveDao;
	
	@Autowired
	private FormSaveElementDao formSaveElementDao;

	@Autowired
	private GeneralUtil generalUtil;
	
	@Autowired
	private GeneralUtilPermission generalUtilPermission;
  
	@Autowired
	private IntegrationValidation integrationValidation;

	@Autowired
	private GeneralUtilConfig generalUtilConfig;

	@Autowired
	private GeneralUtilFormState generalUtilFormState;

	@Autowired
	protected GeneralUtilLogger generalUtilLogger;

	//	@Value("${dataTableWithHint:OPTIMIZER}")
	@Value("${dataTableWithHint:MATERIALIZE}")
	private String dataTableWithHint;
	
	@Value("${viewsAndDefaultColumns:{}}")
//	@Value("${viewsAndDefaultColumns:{'FG_S_PROJECT_DTM_V':'Favorite,Project Name,Project Manager,Site,Creation Date,Project Type','FG_S_SUBPROJECT_DTM_V':'Favorite,Sub Project Name','FG_S_SUBSUBPROJECT_DTM_V':'Favorite,Sub Sub Project Name,Creation Date,Site','FG_S_EXPERIMENT_DTM_V':'Favorite,Experiment Number,Steps,Description,Owner,Experiment Type,Creation Date','FG_S_STEP_DTM_V':'Favorite,Experiment Number,Protocol type,Step Name,Status','FG_S_ACTION_DTM_V':'Favorite,Action,Instruction,Observation,Start Date,Start Time','FG_S_SELFTEST_DTM_V':'Favorite,Test Type,Description,Creation Date,Experiment Number,Default Self-Test','FG_S_WORKUP_DTM_V':'Favorite,Workup,Stage Status,Status'}}")
	private String viewsAndDefaultColumns_;
 
	@Autowired
	private SearchSqlDao searchSqlDao;

	@Autowired
	private IntegrationCalc integrationCalc;

	//	optimizer: Let the optimizer choose – the default mode
	//	materialize: Always materialize
	//	inline: Always inline

	@Override
	public JSONObject onElementDataTableApiChange(String formId, long stateKey, String struct, FormType structFormType,
			String criteria, String display, String linkToLastSelection, String formCode, String tableType,
			String sourceElementImpCode, String hideEmptyColumns, String permissionSqlList, StringBuilder sqlInfo,
			List<String> unfilteredList, String lastMultiValues, boolean updateMultiValues, String followingHiddenCol) {
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
			//get SQL by specific form code
			// ***************************
			// *** onElementDataTableApiChange formCode Main
			// ***************************
			if (formCode.equals("Main") || formCode.equals("ExpAnalysisReport") || formCode.equals("ExperimentReport")) {
				// reports FG_R_EXPANALYSIS_PIVOT_DT_V or FG_R_EXPREPORT_PIVOT_DT_V
				if (generalUtil.getNull(table).equalsIgnoreCase("FG_R_EXPANALYSIS_PIVOT_DT_V") || generalUtil.getNull(table).equalsIgnoreCase("FG_R_EXPREPORT_PIVOT_DT_V")) {
					// FG_R_EXPANALYSIS_PIVOT_DT_V report...
					if(generalUtil.getNull(table).equalsIgnoreCase("FG_R_EXPANALYSIS_PIVOT_DT_V")) {
						String wherePart = (linkToLastSelection.equals("1")
								? getWherePartByFilterForDataTableApi(stateKey, formCode, sourceElementImpCode, table)
								: "");
						
						sql = "select experiment_id,\"Experiment Number_SMARTLINK\",\"Experiment Status\", \"Experiment Aim\""
								+ ",\"Experiment Conclusions\",\"Experiment Description\","
								+ "\"Final Product\",\"Quantity\",\"Quantity UOM\",\"Moles\",\"Moles UOM\","
								+ generalUtil.handleClob(
										"SELECT result_SMARTPIVOT FROM FG_P_EXPERIMENTANALYSIS_V where 1=1 " + wherePart)
								+ " AS RESULT_SMARTPIVOTSQL" + " from " + table + " where 1=1 "
								+ (wherePart.isEmpty() ? " and 1=2" : wherePart);//+ citeriaWherePart;
					}
					// FG_R_EXPREPORT_PIVOT_DT_V report...
					if(generalUtil.getNull(table).equalsIgnoreCase("FG_R_EXPREPORT_PIVOT_DT_V")) {
						String wherePart = (linkToLastSelection.equals("1")
								? getWherePartByFilterForDataTableApi(stateKey, formCode, sourceElementImpCode, table)
								: "");
						
						// step list and where part for pivot table...
						String stepidList = generalUtilFormState.getFormParam(stateKey, "ExperimentReport","$P{CURRENT_ROW_STEPTABLE}");
						String stepWherePart = (stepidList != null && !stepidList.isEmpty()?" and step_id in (" + stepidList.replace("@", ",") + ")" : " AND 1=2");
						
						// set the SQL - DEVELOP!....
						sql = "select experiment_id,\"Experiment Number_SMARTLINK\",\"Experiment Description\","
								+ generalUtil.handleClob(
										"SELECT result_SMARTPIVOT FROM FG_P_EXPREPORT_V where 1=1 " + wherePart + stepWherePart)
								+ " AS RESULT_SMARTPIVOTSQL" + " from " + table + " where 1=1 "
								+ (wherePart.isEmpty() ? " and 1=2" : wherePart);//+ citeriaWherePart;
					}
					
				} else if(generalUtil.getNull(table).equalsIgnoreCase("fg_s_ReportFilterRef_DTE_v")) {
					// on loading the form to insert the rows with parentid to this fg_s_ReportFilterRef_pivot with parentid null and state key to rowstatekey for having the saved data (in the save scheme we need to save as this concept) - maybe save display is -1 with userid and rowstatkey null (because we do not have name id)

//					String xxx = generalUtilFormState.getFormValue(stateKey,"ExperimentReport", "stepTable");
//					Map<String,String> xxMap = generalUtilFormState.getFormParam(stateKey, "ExperimentReport");
					String stepidList = generalUtilFormState.getFormParam(stateKey, "ExperimentReport","$P{CURRENT_ROW_STEPTABLE}");
					System.out.println("-----------stepidList=" + stepidList);
					sql = "select * from " + table + " where 1=1 and active = 1 and ROWSTATEKEY = '" + stateKey + "'";
					optionalAttributes = stepidList.replace("@", ",");
				} else {
					String wherePart = "";
					if(linkToLastSelection.equals("1")) {
						//yp 27022020 fix main screen filtering workaround - the filter is not performed in the lower table if the link to last selection is off and than on click the we do not have filter on the table  (because there is wrong value in the map (it didn't update yet in the map) so we remove the value from the map as a workaround) - this value effects isLinkedBetween function using for the main screen
						String dtVal = generalUtilFormState.getFormValue(stateKey, formCode, sourceElementImpCode);
						if(generalUtil.getNull(dtVal).split(",").length > 3 && dtVal.split(",")[3].equals("0")) {
							 generalUtilFormState.setFormValue(stateKey, formCode, sourceElementImpCode,"");
						}
						wherePart =  getWherePartByFilterForDataTableApi(stateKey,
								formCode, sourceElementImpCode, getFilterTableByStruct(struct));
						if(generalUtil.getNull(dtVal).split(",").length > 3 && dtVal.split(",")[3].equals("0")) {
							 generalUtilFormState.setFormValue(stateKey, formCode, sourceElementImpCode,dtVal);
						}
					}

					if (wherePart.equals("")) {
						sql = "select * from " + table + " where 1=1 ";

					} else {
						sql = "select * from (WITH FILTER_SQL AS (select /*+ " + dataTableWithHint + " */ "
								+ (struct.startsWith("Workup") ? "Workup" : struct) + "_id AS FILTER_SQL_ID from "
								+ getFilterTableByStruct(struct) + " where 1=1 " + wherePart + " ) select * from "
								+ table + " where 1=1  and " + (struct.startsWith("Workup") ? "Workup" : struct)
								+ "_id  in (SELECT FILTER_SQL_ID FROM FILTER_SQL)) where 1=1 ";//+ citeriaWherePart
					}
				}
			} else {
				if (Arrays.asList("Project", "SubProject", "SubSubProject").contains(formCode)
						&& Arrays.asList("SubProject", "SubSubProject", "Experiment").contains(struct)) {
					String wherePart = "";
					if (formCode.equals("Project") && struct.equals("SubProject")
							|| formCode.equals("SubProject") && struct.equals("SubSubProject")
							|| formCode.equals("SubSubProject") && struct.equals("Experiment")) {
						wherePart = " and " + formCode + "_ID='" + formId + "'";
					} else {
						wherePart = (linkToLastSelection.equals("1") ? getWherePartByFilterForDataTableApi(stateKey,
								formCode, sourceElementImpCode, getFilterTableByStruct(struct)) : "");
					}
					if (wherePart.equals("")) {
						sql = "select * from " + table + " where 1=2";
					} else {
						sql = "select * from (WITH FILTER_SQL AS (select /*+ " + dataTableWithHint + " */ " + struct
								+ "_id AS FILTER_SQL_ID from " + getFilterTableByStruct(struct) + " where 1=1 "
								+ wherePart + " ) select * from " + table + " where 1=1  and "
								+ (struct.startsWith("Workup") ? "Workup" : struct)
								+ "_id  in (SELECT FILTER_SQL_ID FROM FILTER_SQL)) where 1=1 ";//+ citeriaWherePart
					}
				}
				//get SQL by specific struct
				// ***************************
				// *** onElementDataTableApiChange struct InvItemBatch
				// ***************************
				
				else if (struct.equalsIgnoreCase("InvItemBatch")) {
					//String formId = generalUtilFormState.getFormId(formCode);
					if (!formId.equals("-1")) {
						if (display.equalsIgnoreCase("DTSET")) {
							String wherePart = (linkToLastSelection.equals("1") ? getWherePartByFilterForDataTableApi(
									stateKey, formCode, sourceElementImpCode, table) : "");
							sql = "select * from " + table + " where 1=1 " + wherePart;//generalUtil.getEmpty(wherePart, " and 1=2 ");
						} else if(display.equals("DTFOR")){
							sql = "select * from " + table + " where EXPERIMENT_ID='" + formId + "' ";
						} else {
							sql = "select * from " + table + " where INVITEMMATERIAL_ID='" + formId + "' ";
						}
					} else {
						String wherePart = (linkToLastSelection.equals("1")
								? getWherePartByFilterForDataTableApi(stateKey, formCode, sourceElementImpCode, table)
								: " and 1 = 1");

						//note [for the code: generalUtil.getEmpty(wherePart, " and 1=2 ")]: if wherePart is empty => no filter made by material on batch => column selection => we display nothing
						sql = "select * from " + table + " where 1=1  " + generalUtil.getEmpty(wherePart, " and 1=2 ");//ab 09042018 task 15294- if wherePart is empty=>means that it was linkedtoLastselection but the selection was not matched.
					}
				}
				// ***************************
				// *** onElementDataTableApiChange struct InvItemMaintenance/ struct InvItemCalibration 
				// ***************************
				else if (struct.equals("InvItemMaintenance") || struct.equals("InvItemCalibration")) {
					//String formId = generalUtilFormState.getFormId(formCode);
					if (!formId.equals("-1")) {
						sql = "select * from " + table + " where INVITEMINSTRUMENT_ID='" + formId + "' ";
					}
				}
				// ***************************
				// *** onElementDataTableApiChange struct Sample
				// ***************************
				else if (struct.equals("Sample")) {
					topRowsNum = "10000";
					String dateFilter="";
					if(dateRange!=null && !dateRange.equals("00/00/0000;00/00/0000")){
					 dateFilter=dateRange.split(";")[2];
					String minDate=dateRange.split(";")[0];
					String maxDate=dateRange.split(";")[1];
				
					if(!dateRange.contains("00/00/0000;00/00/0000")&&!dateFilter.equals("NA")&&!dateFilter.equals("")&&dateFilter!=null) {
						
						if(minDate.equals("00/00/0000")) {
							sql = "select * from " + table + " where "+dateFilter+" is not null and  trunc("+dateFilter+") <= TO_DATE('"+maxDate+"','dd/mm/yyyy')";	
						}
						else {
							sql = "select * from " + table + " where "+dateFilter+" is not null and trunc("+dateFilter+") >= TO_DATE('"+minDate+"','dd/mm/yyyy') AND trunc("+dateFilter+") <= TO_DATE('"+maxDate+"','dd/mm/yyyy')";					
						}
						}
					}
										
				}
				// ***************************
				// *** onElementDataTableApiChange struct InvItemMaterial
				// ***************************
				else if (struct.equals("InvItemMaterial")) {
					String extraWherePart = "";
					String templateFlag = "";
					String wherePart = getWherePartByFilterForDataTableApi(stateKey, formCode, sourceElementImpCode,
							getFilterTableByStruct(struct));
					if (!formId.equals("-1") && (display.equalsIgnoreCase("dtproj"))) {
						extraWherePart += " and instr(','||PROJECT_ID||',', ','||" + formId + "||',')>0";
					}
					//String formId = generalUtilFormState.getFormId(formCode);
					if (!formId.equals("-1") && (display.equalsIgnoreCase("dtexp")
							|| display.equalsIgnoreCase("dtexpan") || display.equalsIgnoreCase("dtexpfr")
							|| display.equalsIgnoreCase("dtexpcr") || display.equalsIgnoreCase("dtexpts")
							|| display.equalsIgnoreCase("dtexpvs") || display.equalsIgnoreCase("dtexpbt"))) {//table located in experiment form
						extraWherePart += " and EXPERIMENT_ID = " + formId;
						templateFlag = generalUtilFormState.getFormParam(stateKey, formCode, "$P{TEMPLATEFLAG}");
					}
					if(display.equalsIgnoreCase("dtexpfr")) {//fixed bug 9061
						String sessionId_ = generalUtilFormState.getSessionId(formId);
						extraWherePart += " and EXPERIMENT_ID = " + formId + " and nvl(sessionid ,'"+sessionId_+"') =  '"+sessionId_+"'";
						sql = "select t.*, decode('" + generalUtil.getNull(templateFlag)
														+ "','1','',decode(nvl(FG_ADAMA.IS_CREW_TRAINED(t." + struct
														+ "_ID,t.EXPERIMENT_ID),1),1,'Yes','No')) as \"Familiarity\""
								+ " from (select decode( count(*) over (partition by tt.invitemmaterial_id, nvl(tt.sessionid,'"+sessionId_+"')), 2, decode(nvl(tt.sessionid,'-1'), '-1', 'hide','show' ), 'show' ) as showFlag"
								+ ",tt.* from " + table + " tt where 1=1 "  +wherePart + extraWherePart
								+ ") t where 1=1 and showflag='show'";
						
						templateFlag = generalUtilFormState.getFormParam(stateKey, formCode, "$P{TEMPLATEFLAG}");
					}
					if(formId.equals("-1") && generalUtil.getNull(formCode).equalsIgnoreCase("InvItemMaterialsMain") && struct.equals("InvItemMaterial")) { // main inventory material screen screen
						String siteId = generalUtilFormState.getFormParam(stateKey, formCode, "$P{CURRENT_SITE_ID}");
						String labId = generalUtilFormState.getFormParam(stateKey, formCode, "$P{CURRENT_LABORATORY_ID}");
						System.out.println("siteId=" + siteId + ", labId=" + labId);
						if(!generalUtil.getNull(siteId).equalsIgnoreCase("ALL") || !generalUtil.getNull(labId).equalsIgnoreCase("ALL")) {
							String siteWherePart = "";
							String labWherePart = "";
							if(!generalUtil.getNull(siteId).equalsIgnoreCase("ALL")) {
								siteWherePart = "and t1.site_id in ('" + siteId + "')";
							}
							if(!generalUtil.getNull(labId).equalsIgnoreCase("ALL")) {
								labWherePart = "and t1.LABORATORY_ID in ('" + labId + "')";
							}
							wherePart += " and exists (select 1 from fg_s_InvItemBatch_v t1 where 1=1 " + siteWherePart + " " + labWherePart + " and t1.INVITEMMATERIAL_ID = tt.INVITEMMATERIAL_ID ) \r\n";
						}
					}
					
					//filter Alternative co-formulants in InvItemMaterial (only by alternativeGroup)
					if(display.equalsIgnoreCase("dtcof")) {
						String materialWherePart = "";
						String alternativeGroup = generalUtilFormState.getFormParam(stateKey, formCode, "$P{CURRENT_ALTERNATIVEGROUP}");
						String currentMaterial_id = formCode.equals("InvItemMaterial")?formId:generalUtilFormState.getFormParam(stateKey, formCode, "$P{CURRENT_MATERIAL_ID}");
						if(alternativeGroup != null && !alternativeGroup.isEmpty()) {
							materialWherePart = " and upper(trim(alternativeGroup)) = upper(trim('" + alternativeGroup + "'))"
									+ " and material_id<> '"+currentMaterial_id+"' ";
						}
						wherePart=generalUtil.getEmpty(materialWherePart, " and 1=2 ");
					}
					
					// filter material table - materialprotocoltype....
					// if open by search / select it should be filtered by chemical material
					if(formCode.equals("MaterialSlctSearch") || formCode.equals("MaterialSearch") || formCode.equals("MaterialSelect")) {
						String parentExperimentProtocol = generalUtilFormState.getFormParam(stateKey, formCode, "$P{STRUCT_PARAM_PROTOCOLTYPENAME}");
						
						if(parentExperimentProtocol == null || !parentExperimentProtocol.equals("Formulation")) {
							extraWherePart = " and exists (select 1 from fg_s_InvItemMaterial_v t1 where t1.INVITEMMATERIAL_ID = tt.INVITEMMATERIAL_ID and lower(t1.materialprotocoltype) = 'chemical material') ";
						}
					}
					if(!display.equalsIgnoreCase("dtexpfr")) {
					
					sql = "select t.*"
							+ (display.equalsIgnoreCase("dtexp") || display.equalsIgnoreCase("dtexpan")
									|| display.equalsIgnoreCase("dtexpfr") || display.equalsIgnoreCase("dtexpcr")
									|| display.equalsIgnoreCase("") || display.equalsIgnoreCase("dtexpvs")
									|| display.equalsIgnoreCase("dtexpbt")
											? ", decode('" + generalUtil.getNull(templateFlag)
													+ "','1','',decode(nvl(FG_ADAMA.IS_CREW_TRAINED(t." + struct
													+ "_ID,t.EXPERIMENT_ID),1),1,'Yes','No')) as \"Familiarity\""
											: "")
							+ " from (select tt.* from " + table + " tt where 1=1 "  +wherePart + extraWherePart
							+ ") t where 1=1";//+ citeriaWherePart;
					}
					
					
					

				}
				// ***************************
				// *** onElementDataTableApiChange struct InvItemColumn/ struct InvItemInstrument 
				// ***************************dtexpts
				else if (struct.equals("InvItemInstrument")) {
					String extraWherePart = "";
					String templateFlag = "";
					String wherePart = (linkToLastSelection.equals("1") ? getWherePartByFilterForDataTableApi(stateKey,
							formCode, sourceElementImpCode, getFilterTableByStruct(struct)) : "");
					//String formId = generalUtilFormState.getFormId(formCode);
					if (!formId.equals("-1")
							&& ((display.equalsIgnoreCase("dtexp") || (display.equalsIgnoreCase("dtexa")
								 ||  display.equalsIgnoreCase("dtfr") || display.equalsIgnoreCase("dtpr") || display.equalsIgnoreCase("dtan"))))) {//table located in experiment form
						extraWherePart += " and EXPERIMENT_ID = " + formId;
						templateFlag = generalUtilFormState.getFormParam(stateKey, formCode, "$P{TEMPLATEFLAG}");
					}
					sql = "select t.*"
							+ (display.equalsIgnoreCase("dtexp") || display.equalsIgnoreCase("dtexa")
									||  display.equalsIgnoreCase("dtfr") || display.equalsIgnoreCase("dtpr") || display.equalsIgnoreCase("dtan")
											? ", decode('" + generalUtil.getNull(templateFlag)
													+ "','1','',decode(nvl(FG_ADAMA.IS_CREW_TRAINED(t." + struct
													+ "_ID,t.EXPERIMENT_ID),1),1,'Yes','No')) as \"Familiarity\""
											: "")
							+ " from (select tt.* from " + table + " tt where 1=1 and " + struct + "_ID in ( select "
							+ struct + "_ID" + "  from FG_S_" + struct + "_ALL_V where 1=1  " + wherePart + " )"
							+ extraWherePart + " ) t where 1=1";//+citeriaWherePart;
				}
				// ***************************
				// *** onElementDataTableApiChange struct InvItemColumn/ struct InvItemInstrument 
				// ***************************dtexpts
				else if (struct.equals("InvItemColumn")) {
					String templateFlag = generalUtilFormState.getFormParam(stateKey, formCode, "$P{TEMPLATEFLAG}");
					String extraWherePart = "";
					String wherePart = (linkToLastSelection.equals("1") ? getWherePartByFilterForDataTableApi(stateKey,
							formCode, sourceElementImpCode, getFilterTableByStruct(struct)) : "");
					//String formId = generalUtilFormState.getFormId(formCode);
					if (!formId.equals("-1")
							&& ((display.equalsIgnoreCase("dtexp") || (display.equalsIgnoreCase("dtexa")
									|| display.equalsIgnoreCase("dtpr") || display.equalsIgnoreCase("dtan"))))) {//table located in experiment form
						extraWherePart += " and EXPERIMENT_ID = " + formId;
					}
					sql = "select t.*" 
					+ (display.equalsIgnoreCase("dtexp")?", decode('" + generalUtil.getNull(templateFlag)
					+ "','1','',decode(nvl(FG_ADAMA.IS_CREW_TRAINED(t." + struct
					+ "_ID,t.EXPERIMENT_ID),1),1,'Yes','No')) as \"Familiarity\"": "")
					+ " from (select tt.* from " + table + " tt where 1=1 and " + struct
							+ "_ID in ( select " + struct + "_ID" + "  from FG_S_" + struct + "_ALL_V where 1=1  "
							+ wherePart + " )" + extraWherePart + " ) t where 1=1";//+citeriaWherePart;
				}
				else if (struct.equals("RecipeFormulation")) {
					String wherePart = (linkToLastSelection.equals("1") ? getWherePartByFilterForDataTableApi(stateKey,
							formCode, sourceElementImpCode, getFilterTableByStruct(struct)) : "");
					if(formId.equals("-1") && generalUtil.getNull(formCode).equalsIgnoreCase("InvItemRecipesMain")||generalUtil.getNull(formCode).equalsIgnoreCase("CompositionSearch")) { // main inventory material screen screen
						String categoryId = generalUtilFormState.getFormParam(stateKey, formCode, "$P{CURRENT_CATEGORY_ID}");
						
						if(!generalUtil.getNull(categoryId).equalsIgnoreCase("ALL") ) {
							String categoryWherePart = "";
							
							if(!generalUtil.getNull(categoryId).equalsIgnoreCase("ALL")) {
								categoryWherePart = "and t1.category_id in ('" + categoryId + "')";
							}
							
							wherePart +=  categoryWherePart+" \r\n";
						}
					}
					sql = "select t.*" + " from (select tt.* from " + table + " tt where 1=1 and " + struct
							+ "_ID in ( select " + struct + "_ID" + "  from FG_S_" + struct + "_ALL_V t1 where 1=1  "
							+ wherePart + " )) t where 1=1";//+citeriaWherePart;
				}
			
				// ***************************
				// *** onElementDataTableApiChange struct Experiment
				// ***************************
				else if (struct.equalsIgnoreCase("Experiment")) {
					
					if (formCode.equals("Template")) {
						//String formId = generalUtilFormState.getFormId(formCode);
						String expId = generalUtilFormState.getFormValue(stateKey, formCode, "SOURCEEXPNO_ID");
						generalUtilLogger.logWriter(LevelType.DEBUG, "formId=" + formId, ActivitylogType.DataTable,
								formId);

						sql = "select * from " + table + " where EXPERIMENT_ID='" + expId + "' ";
					} else {
						//String formId = generalUtilFormState.getFormId(formCode);
						generalUtilLogger.logWriter(LevelType.DEBUG, "formId=" + formId, ActivitylogType.DataTable,
								formId);
						sql = "select * from " + table;//
						if(display.equalsIgnoreCase("dtpgroup")) {
							String groupId = generalUtilFormState.getFormParam(stateKey, "Project", "$P{GROUPID}");
							sql = "select * from " + table + " where experimentgroup_id='" + groupId + "' ";

						}
						if (!formId.equals("-1")) {
							if (tableType.equalsIgnoreCase("column")) {
								sql = sql + " where INVITEMCOLUMN_ID = '" + formId + "'";//+formDao.getWherePartForTmpData("ColumnSelect", formId);//+ " where TABLETYPE='" + tableType + "' and PARENTID='" + formId + "' ";
							} else if (tableType.equalsIgnoreCase("inst")) {
								sql = sql + " where TO_CHAR(INVITEMINSTRUMENT_ID) = '" + formId + "'";//adib-28052019 removed distinct instead of adding here the ordering by date
							} else if (tableType.equalsIgnoreCase("batch")) {
								sql = sql + " where TO_CHAR(FORMID) = '" + formId + "'"; //in (select '" + formId+"' from dual union select PARENTID from FG_S_MATERIALREF_ALL_V where BATCH_ID = '"+formId+"' and sessionid is null)";
							} else if (tableType.equalsIgnoreCase("originExp")) {

								sql = sql
										+ " t where (select r.PARENTID  from fg_s_request_all_v r where r.REQUEST_ID='"
										+ formId + "')=t.EXPERIMENT_ID";
							} else if (tableType.equalsIgnoreCase("destExp")) {

								sql = sql + " where TO_CHAR(REQUEST_ID) = '" + formId + "'";
							}
						}
					}
				}
				// ***************************
				// *** onElementDataTableApiChange struct Step
				// ***************************
				else if (struct.equalsIgnoreCase("Step")){
					//String formId = generalUtilFormState.getFormId(formCode);
					if (!formId.equals("-1")) {
						String runNumber = generalUtilFormState.getFormParam(stateKey, formCode, "$P{RUNNUMBER_PARAM}");
						sql = "select * from " + table + " where EXPERIMENT_ID='" + formId + "' "
								+(!generalUtil.getNull(runNumber).isEmpty()? " and RUNNUMBER = '"+runNumber+"'":"");
						if(display.toLowerCase().equals("dtmultistep")){
							idName = "MATERIALREF_ID";
							additionalOrder = "\"Step\"";
						}

					} else {
						String wherePart = (linkToLastSelection.equals("1")
								? getWherePartByFilterForDataTableApi(stateKey, formCode, sourceElementImpCode, table)
								: "");

						sql = "select * from " + table + " where 1=1 " + generalUtil.getEmpty(wherePart, " and 1=2 ");//+ citeriaWherePart ;
						if(display.toLowerCase().equals("dtmultistep")){
							idName = "MATERIALREF_ID";
							additionalOrder = "\"Step\"";
						}
					}
				} else if (struct.equalsIgnoreCase("StepFr")) {
					//String formId = generalUtilFormState.getFormId(formCode);
					if (!formId.equals("-1")) {
						sql = "select * from " + table + " where EXPERIMENT_ID='" + formId + "' ";

					} else {
						String wherePart = (linkToLastSelection.equals("1")
								? getWherePartByFilterForDataTableApi(stateKey, formCode, sourceElementImpCode, table)
								: "");

						sql = "select * from " + table + " where 1=1 " + generalUtil.getEmpty(wherePart, " and 1=2 ");//+ citeriaWherePart ;
					}
				}
				// ***************************
				// *** onElementDataTableApiChange struct Action
				// ***************************
				else if (struct.equalsIgnoreCase("Action")) {
					//String formId = generalUtilFormState.getFormId(formCode);
					String templateFlag = "";
					if (formCode.equals("Step")) {
						templateFlag = generalUtilFormState.getFormParam(stateKey, "Step", "$P{TEMPLATEFLAG}");
					}
					if (generalUtil.getNull(templateFlag).equals("1")
							&& table.toUpperCase().equals("FG_S_ACTION_DTE_V")) {
						sql = "select formid, \"ACTION_ID\", 'h1','h2',experiment_id, templateflag, step_id, \"Action Number\", \"Action\", \"Instruction\" from FG_R_ACTIONSUMMARY_DTT_V where  step_id = "
								+ formId;
					} else if (!formId.equals("-1") && (formCode.equals("Step") || formCode.equals("StepFr") || formCode.equals("StepMinFr"))) {
						sql = "select * from " + table + " where STEP_ID='" + formId + "' ";
					} else if(formCode.equals("ReportTable")){
						String formNumberId = generalUtilFormState.getFormParam(stateKey, formCode, "$P{STEPSEQ}");
						String exp_id = generalUtilFormState.getFormParam(stateKey, formCode, "$P{EXPERIMENT_ID}");
						sql = "select * from " + table + " where experiment_id='" + generalUtil.getNull(exp_id) + "' and stepsequence = '"+generalUtil.getNull(formNumberId) +"' order by runnumber,\"Action\"";
					}else {
						String wherePart = (linkToLastSelection.equals("1")
								? getWherePartByFilterForDataTableApi(stateKey, formCode, sourceElementImpCode, table)
								: "");

						sql = "select * from " + table + " where 1=1 " + generalUtil.getEmpty(wherePart, " and 1=2 ");//+ citeriaWherePart ;
					}
				} 
				else if(struct.equalsIgnoreCase("ExpRunPlanning")) {
					
					if (!formId.equals("-1")&& display.equalsIgnoreCase("dte")) {
						sql = "select * from " + table + " where EXPERIMENTID='" + formId + "' ";

					}else if(display.equalsIgnoreCase("dt")){
						String parentId = generalUtilFormState.getFormParam(stateKey, formCode, "$P{PARENT_ID}");
						sql = "select * from " + table + " where EXPERIMENTID='" + parentId + "' ";
					}
				}
				else if (struct.startsWith("ExperimentPr")) {
					//String formId = generalUtilFormState.getFormId(formCode);

					generalUtilLogger.logWriter(LevelType.DEBUG, "formId=" + formId, ActivitylogType.DataTable, formId);//OK
					sql = "select * from " + table + " where EXPERIMENT_ID='" + formId + "' "
							+ generalUtilFormState.getWherePartForTmpData(struct, formId)
							+ getWherePartByFilterForDataTableApi(stateKey, formCode, sourceElementImpCode,
									table.toUpperCase());

				} else if (struct.startsWith("ExperimentSeries")) {
					//String formId = generalUtilFormState.getFormId(formCode);

					generalUtilLogger.logWriter(LevelType.DEBUG, "formId=" + formId, ActivitylogType.DataTable, formId);//OK
					sql = "select * from " + table + " where project_id='" + formId + "' ";

				}
				// ***************************
				// *** onElementDataTableApiChange struct SelfTest
				// ***************************
				else if (struct.equalsIgnoreCase("SelfTest") || struct.equalsIgnoreCase("Workup")) { // TODO
					//String formId = generalUtilFormState.getFormId(formCode);
					if (!formId.equals("-1") && (formCode.equals("Action"))) {
						sql = "select * from " + table + " where ACTION_ID='" + formId + "' ";
					} else {
						String wherePart = (linkToLastSelection.equals("1")
								? getWherePartByFilterForDataTableApi(stateKey, formCode, sourceElementImpCode, table)
								: "");

						sql = "select * from " + table + " where 1=1 " + generalUtil.getEmpty(wherePart, " and 1=2 ");//+ citeriaWherePart ;
					}
				}
				// ***************************
				// *** onElementDataTableApiChange struct Template
				// ***************************
				else if (struct.equalsIgnoreCase("Template")) {
					//String formId = generalUtilFormState.getFormId(formCode);
					if (formId.equals("-1") || display.equalsIgnoreCase("dtslct")) {
						String wherePart = getWherePartByFilterForDataTableApi(stateKey, formCode, sourceElementImpCode,
								table);

						sql = "select * from " + table + " where 1=1 " + wherePart;
					} else if (display.equalsIgnoreCase("dtproj")) {

						sql = "select * from " + table + " where project_id = " + formId;
					} else {
						sql = "select distinct * from " + table + " t where  t.EXPERIMENT_ID = " + formId;
					}
				}

				// ***************************
				// *** onElementDataTableApiChange struct ExpTemplateSelect in Template
				// ***************************
				else if (struct.equalsIgnoreCase("ExpTemplateSelect")) {

					//String formId = generalUtilFormState.getFormId(formCode);
					//					String expId = generalUtilFormState.getFormValue(stateKey, formCode,"SOURCEEXPNO_ID");
					generalUtilLogger.logWriter(LevelType.DEBUG, "formId=" + formId, ActivitylogType.DataTable, formId);

					sql = "select * from " + table + " where  parentid = '" + formId + "'"; //singleExp_id='" +  expId+ "' and																
				}
				// ***************************
				// *** onElementDataTableApiChange struct Request
				// ***************************
				else if (struct.equalsIgnoreCase("Request")) {
					//String formId = generalUtilFormState.getFormId(formCode);
					if (formCode.equals("Request")) {
						String ParentRequestId = generalDao.selectSingleStringNoException(
								"select t.PARENTREQUESTID from FG_S_REQUEST_ALL_V  t where t.Formid ='" + formId + "'");

						if (generalUtil.getNull(ParentRequestId).equals("-1")
								|| generalUtil.getNull(ParentRequestId).isEmpty()) {
							//select the related request for the parent request
							sql = "select distinct * from " + table + " where (PARENTREQUESTID='" + formId + "') ";//  or Formid='" + formId and  Formid !='" + formId + "'
						} else {
							//select the related request for the siblings requests
							sql = "select distinct * from " + table + " where (PARENTREQUESTID='" + ParentRequestId
									+ /* "' or Formid='"
										+ ParentRequestId +*/ "') and Formid !='" + formId + "' ";
						}

					} else if(formCode.equals("RequestSelect")){
						String parentId = generalUtilFormState.getFormParam(stateKey, formCode, "$P{PARENT_ID}");
						parentFormCode = formDao.getFormCodeEntityBySeqId("", parentId);
						if(parentFormCode.equals("Experiment")){
							
							String sql_ = "";
							Map<String,String> experimentMap = formDao.getFromInfoLookupAll("Experiment",LookupType.ID,parentId);
							String experimentUnitId = formDao.getFromInfoLookup("LABORATORY",LookupType.ID,generalUtil.getNull(experimentMap.get("LABORATORY_ID")),"UNITS_ID");
							//list of requests with operation type that is the same as the experiment type
							//"Assay" and "Impurity profile" requests are the same type
							if(generalUtil.getNull(experimentMap.get("EXPERIMENTTYPENAME")).equals("Assay") || experimentMap.get("EXPERIMENTTYPENAME").equals("Impurity Profile")){
								sql_ = "select distinct t.REQUEST_ID from FG_I_CONN_REQUEST_OPTYPE_V t where (t.EXPERIMENTTYPENAME  = 'Assay' or t.EXPERIMENTTYPENAME  = 'Impurity Profile') \r\n" + 
										"and t.requesttype_id in\r\n" + 
										"(select distinct t.REQUESTTYPE_ID from FG_S_EXPERIMENTTYPE_V t \r\n" + 
										"where t.PROTOCOLTYPE_ID='"+generalUtil.getNull(experimentMap.get("PROTOCOLTYPE_ID"))+"' and (t.EXPERIMENTTYPENAME  = 'Assay' or t.EXPERIMENTTYPENAME  = 'Impurity Profile'))";
							}else{
							//list of requests with operation type that is the same as the experiment type
							sql_ = "select distinct t.REQUEST_ID from FG_I_CONN_REQUEST_OPTYPE_V t where t.EXPERIMENTTYPE_ID  = '"+generalUtil.getNull(experimentMap.get("EXPERIMENTTYPE_ID"))+"' \r\n" + 
									"and t.requesttype_id in\r\n" + 
									"(select distinct t.REQUESTTYPE_ID from FG_S_EXPERIMENTTYPE_V t \r\n" + 
									"where t.PROTOCOLTYPE_ID='"+generalUtil.getNull(experimentMap.get("PROTOCOLTYPE_ID"))+"' and t.EXPERIMENTTYPE_ID ='"+generalUtil.getNull(experimentMap.get("EXPERIMENTTYPE_ID"))+"')";
							}
							//list of requests from 1.status "In Progress" or "waiting"  2.Requests from the same unit as the current experiment 3.requests with operation type that is the same as the experiment type
							sql = " select request_id from fg_s_request_all_v\r\n" + 
									" where (REQUESTSTATUSNAME='In Progress' or REQUESTSTATUSNAME='Waiting') and DESTUNIT_ID = '"+experimentUnitId+"'\r\n" + 
									"									and request_id in("+sql_+")\r\n" + 
									" union all\r\n" + 
									"  select r.request_id from fg_s_request_v r,fg_s_RequestSelect_pivot t where instr(',' ||t.REQUEST_ID|| ',',',' || r.REQUEST_ID || ',') > 0 and t.parentid ='"+ parentId+"' and t.sessionid is null";
							List<String> requestId = generalDao.getListOfStringBySql(sql);
							if(requestId!= null && !requestId.isEmpty()){
								if(requestId.size() < 1000) {
									sql = "select distinct * from " + table + " where 1=1 and request_id in("+generalUtil.listToCsv(requestId)+")";
								} else {
									sql = "select distinct * from " + table + " where 1=1 and request_id in("+sql+")";
								}
							}
							else{
								sql = "select distinct * from " + table + " where 1=2";
							}
						}
						else{
							sql = "select * from " + table + " where 1=1";
						}
					} 
					else if(formCode.equals("InvItemBatch")) {
						sql = "select * from " + table + " where  parentid = '" + formId + "'"; 
						String wherePart = (linkToLastSelection.equals("1")
								? getWherePartByFilterForDataTableApi(stateKey, formCode,
										sourceElementImpCode, "FG_I_CONN_REQUESTSELECT_SMPL_V")
								: "");
						if (!wherePart.equals("")) {
							sql = sql
									+ " and request_id in(select request_id from FG_I_CONN_REQUESTSELECT_SMPL_V where  1 = 1 "
									+ wherePart + ")";
						}
					}
					else if(formCode.equals("Sample")) {
						sql = "select * from "+table+" where 1=1 and sampleid = '"+formId+"'";
					}
					else {
						sql = "select * from " + table + " where 1=1";
					}
				} else if(struct.equalsIgnoreCase("Sample")){
					if(formCode.equals("SampleSelect")){
						String parentId = generalUtilFormState.getFormParam(stateKey, formCode, "$P{PARENT_ID}");
						parentFormCode = formDao.getFormCodeEntityBySeqId("", parentId);
						if(parentFormCode.equals("Experiment")){
							sql = "select * from " + table + " where 1=1 and \"Status\" = 'Active'";
						}
						else{
							sql = "select * from " + table + " where 1=1";
						}
					}else{
						String wherePart = "";
						String formCodeEntity = formDao.getFormCodeEntityBySeqId(formCode,formId);
						if(formCodeEntity.equals("Experiment")&&display.toLowerCase().equals("dte")){
							wherePart = " and EXPERIMENT_ID = '"+formId+"'";
						}
						sql = "select * from " + table + " where 1=1"+wherePart;
					}
					
					if(table != null && table.equalsIgnoreCase("fg_s_Sample_DT_v")) {
						additionalOrder = "\"Creation Date_SMARTTIME\" desc";
					}
				}// ***************************
					// *** onElementDataTableApiChange struct specifications
					// ***************************
				else if (struct.equalsIgnoreCase("Specifications")) {
					String wherePart = (linkToLastSelection.equals("1") ? getWherePartByFilterForDataTableApi(stateKey,
							formCode, sourceElementImpCode, table.toUpperCase()) : "");
					sql = "select * from " + table + " where  1=1 " + wherePart;//+ citeriaWherePart;
				}

				// ***************************
				// *** onElementDataTableApiChange struct MaterialRef
				// ***************************
				else if (struct.equalsIgnoreCase("MaterialRef")) {
					//String formId = generalUtilFormState.getFormId(formCode);
					String exp_id = generalUtilFormState.getFormParam(stateKey, "Step", "$P{EXPERIMENT_ID}");
					if(!generalUtil.getNull(exp_id).isEmpty() && !exp_id.equals("-1")){
					 optionalAttributes = formDao.getFormCodeEntityBySeqId("", exp_id);
					 String isNew = generalUtilFormState.getFormParam(stateKey, "Step", "$P{ISNEW}");
					 if(!isNew.equals("1")){
						 optionalAttributes1 = exp_id+","+formId;
					 }else{//is new
						 optionalAttributes1 = exp_id;
					 }
					}
					String wherePart = getWherePartByFilterForDataTableApi(stateKey, formCode, sourceElementImpCode,
							table.toUpperCase());
					if (wherePart.contains("Actual") && !tableType.equals("Product")) {
						table = "FG_S_" + struct + "_" + display + "_ACT_V";
						optionalAttributes1 = formId;
					}else

					if (tableType.equals("Product")) {
						//String parent_id = generalUtilFormState.getFormParam(stateKey, "Step", "$P{EXPERIMENT_ID}");
						if(!generalUtil.getNull(exp_id).isEmpty() && !exp_id.equals("-1")){
							  // optionalAttributes = formDao.getFormCodeEntityBySeqId("", parent_id);
							   //optionalAttributes1 = optionalAttributes1.isEmpty()?exp_id:formId+","+exp_id;
							}
					}
					
					String templateFlag = generalUtilFormState.getFormParam(stateKey, "Step", "$P{TEMPLATEFLAG}");
					if (generalUtil.getNull(templateFlag).equals("1")) {
						if (tableType.equals("Reactant")) {
							table = "FG_S_MATERIALREF_dtreact_V";
						} else if (tableType.equals("Solvent")) {
							table = "fg_s_materialref_dtsolv_v";
						} else if (tableType.equals("Product")) {
							table = "fg_s_materialref_dtprod_v";
						}
						sql = "select distinct * from " + table + " where TABLETYPE='" + tableType + "' and PARENTID='"
								+ formId + "' " + generalUtilFormState.getWherePartForTmpData(struct, formId);
					} else {
						generalUtilLogger.logWriter(LevelType.DEBUG, "formId=" + formId, ActivitylogType.DataTable,
								formId);
						sql = "select * from " + table + " where TABLETYPE='" + tableType + "' and PARENTID='" + formId
								+ "' " + generalUtilFormState.getWherePartForTmpData(struct, formId) + wherePart;
					}
				}
				// ***************************
				// *** onElementDataTableApiChange struct SampleDataRef
				// ***************************
				else if (struct.equalsIgnoreCase("SampleDataRef")) {
					String parent_id = generalUtil.getNull(generalUtilFormState.getFormParam(stateKey, "Request", "$P{PARENTID}"),generalUtilFormState.getFormParam(stateKey, "Request", "$P{PARENT_ID}"));//when creating the request from the action then getting parent_d, else-getting parentid
					if(!generalUtil.getNull(parent_id).isEmpty() && !parent_id.equals("-1")){
					   parentFormCode = formDao.getFormCodeEntityBySeqId("", parent_id);
					}
					if(generalUtil.getNull(parentFormCode).equals("Action")){
						sql = "select * from " + table + " where  PARENTID='" + formId
								+ "' " + generalUtilFormState.getWherePartForTmpData(struct, formId);
					}
					else
					{ 
						String cols = "SAMPLE_ID, FORMID, PARENTID, SAMPLEDATAREFNAME, FORM_TEMP_ID, sampleselectholder_id, \"Sample # _SMARTLINK\"" //10022020 removed fixed bug-7859\"_SMARTSELECTALLNONE\", 230120 kd changed _SMARTSELECT on _SMARTSELECTALLNONE
								+",\"Sample Description_SMARTLINK\",\"Source Experiment\",\"Sender\",\"Comments_SMARTEDIT\",\"Sent Qty_SMARTEDIT\",\"Source Lab\","
								+"\"Project\",\"Subproject\",\"Sent to Lab_SMARTEDIT\",\"Accepted by Lab_SMARTEDIT\",\"Request Type\",\"_SMARTSELECTALLNONELABEL\" ";
						sql = "select "+ cols +" from " + table + " where  PARENTID='" + formId
								+ "' " + generalUtilFormState.getWherePartForTmpData(struct, formId);
					}
					
					
				}
				// ***************************
				// *** onElementDataTableApiChange struct FormulantRef
				// ***************************
				else if (struct.equalsIgnoreCase("FormulantRef")) {
					//String formId = generalUtilFormState.getFormId(formCode);
					generalUtilLogger.logWriter(LevelType.DEBUG, "formId=" + formId, ActivitylogType.DataTable, formId);
					sql = "select * from " + table + " where 1=1 "
							+ (generalUtil.getEmpty(tableType, "").equals("") ? ""
									: " and TABLETYPE='" + tableType + "'")
							+ " and PARENTID='" + formId + "' "
							+ generalUtilFormState.getWherePartForTmpData(struct, formId)
							+ getWherePartByFilterForDataTableApi(stateKey, formCode, sourceElementImpCode,
									table.toUpperCase());
				}

				// ***************************
				// *** onElementDataTableApiChange struct PreparationRef
				// ***************************
				else if (struct.equalsIgnoreCase("PreperationRef") || struct.equalsIgnoreCase("PermissionSRef")) {
					//String formId = generalUtilFormState.getFormId(formCode);
					generalUtilLogger.logWriter(LevelType.DEBUG, "formId=" + formId, ActivitylogType.DataTable, formId);
					sql = "select * from " + table + " where PARENTID='" + formId + "' "
							+ generalUtilFormState.getWherePartForTmpData(struct, formId);
				}

				// ***************************
				// *** onElementDataTableApiChange struct NA (used by reports,...)
				// ***************************
				else if (struct.equals("NA")) {
					//where part -
					String wherePart = (linkToLastSelection.equals("1") ? getWherePartByFilterForDataTableApi(stateKey,
							formCode, sourceElementImpCode, table.toUpperCase()) : "");
					if(generalUtil.getNull(table).equalsIgnoreCase("FG_I_SAMPLERESULTS_DT_V")){
						sql = "select t.UNIQUEROW,\n" + 
								"SAMPLE_EXPERIMENT_ID,\n" + 
								"t.SAMPLE_ID,\n" + 
								"t.\"Sample #_SMARTLINK\",\n" + 
								"t.\"Comments\",\n" + 
								"t.\"Sample Description\",\n" +
								"t.\"Sample Origin_SMARTLINK\",\n" + 
								"t.\"Sample Type\",\n" + 
								"t.\"Sample Amount\",\n" + 
								"t.\"Batch_SMARTLINK\",\n" + 
								"t.\"Experiment #_SMARTLINK\",\n" + 
								"t.\"Protocol\",\n" + 
								"t.\"Experiment Type\",\n" + 
								"t.\"Experiment Description\",\n" +
								"'SELECT result_SMARTPIVOT FROM FG_P_EXPERIMENTRESULTS_V where 1=1"+wherePart.replace("\'", "")+"' AS RESULT_SMARTPIVOTSQL"
								+ " \nfrom " + table + " t where 1=1" + wherePart;
					}
					else if(generalUtil.getNull(table).equalsIgnoreCase("FG_I_RECIPEUSAGES_DT_V")){
						sql = "select * from " + table + " where recipeformulation_id = '"+formId+"'";
					}
					else if (generalUtil.getNull(table).equalsIgnoreCase("FG_R_EXPANALYSIS_PIVOT_DT_V")) {
						sql = "select * from " + table + wherePart;//+ citeriaWherePart;
					}
					else if(generalUtil.getNull(table).equalsIgnoreCase("FG_COMPOSITION_DETAILS_DT_V")){
						sql = "select *\n"
							+ "from FG_COMPOSITION_DETAILS_DT_V\n"
							+ "where sessionid = '"+generalUtil.getSessionId()+"'\n"
							+ wherePart;
						
					}
					// by table
					// FG_R_REACTIONSUMMARY_V
					else if (generalUtil.getNull(table).equalsIgnoreCase("FG_R_REACTIONSUMMARY_V")) {
						if (!formId.equals("-1") && (formCode.equals("Experiment")||formCode.equals("ExperimentCP"))) {
							if (wherePart.contains("Actual")) {
								table = "FG_R_REACTIONSUMMARY_ACT_V";
							}
							sql = "select * from " + table + " where  experiment_id = " + formId + wherePart;//+ citeriaWherePart;
						}

					} else if(generalUtil.getNull(table).equalsIgnoreCase("FG_I_SUMMARY_RUN_STEPS_DT_V")){
						sql = "select * from " + table + " where experiment_id = '"+formId+"'";
					} else if (generalUtil.getNull(table).equalsIgnoreCase("fg_s_sampledata_dtPlanned_v")
							|| generalUtil.getNull(table).equalsIgnoreCase("fg_s_sampledata_dtpln_slct_v")) {
						sql = "select * from " + table + " where 1=1 " + wherePart;//+ citeriaWherePart;
					} else if (generalUtil.getNull(table).equalsIgnoreCase("FG_R_MESSAGES_V")) {
						String userName = generalUtil.getSessionUserName();
						if (!userName.equals("system")) {
							sql = "select * from " + table + " where  user_id = " + userId + wherePart;//+ citeriaWherePart;	
						}
					} else if (generalUtil.getNull(table).equalsIgnoreCase("FG_R_FORMULANTSBYSTEPS_V")) {
						//String formId = generalUtilFormState.getFormId(formCode);
						if (!formId.equals("-1") && (formCode.equals("ExperimentFor"))) {
							sql = "select * from " + table + " where  experiment_id = " + formId + " and TABLETYPE='"
									+ tableType + "'" + wherePart;//+ citeriaWherePart;
						}
					} else if (generalUtil.getNull(table).equalsIgnoreCase("FG_R_ACTIONSUMMARY_DT_V")) {
						String templateFlag = generalUtilFormState.getFormParam(stateKey, "Step", "$P{TEMPLATEFLAG}");
						if (generalUtil.getNull(templateFlag).equals("1")) {
							sql = "select * from FG_R_ACTIONSUMMARY_DTT_V where  step_id = " + formId;
						} else if (!formId.equals("-1") && (formCode.equals("Step"))) {
							sql = "select * from table(FN_GET_R_ACTIONSUMMARY('" + formId + "')) where 1=1 " + wherePart;//+ citeriaWherePart;
						}
					} else if (generalUtil.getNull(table).equalsIgnoreCase("FG_R_MATERIALSUMMARY_V")) {
						//String formId = generalUtilFormState.getFormId(formCode);
						String stepId = generalDao.selectSingleStringNoException(
								"select t.STEP_ID from fg_s_action_v t where t.action_id = " + formId
										+ " and t.active=1");
						if (!formId.equals("-1") && (formCode.equals("Action"))) {
							sql = "select * from " + table + " where  TABLETYPE='" + tableType + "' and PARENTID= nvl('"
									+ stepId + "',0)" + wherePart;//+ citeriaWherePart;
						}
					} else if (generalUtil.getNull(table).equalsIgnoreCase("FG_R_PERMISSIONUSER_V")) {
						//String formId = generalUtilFormState.getFormId(formCode);
						if (!formId.equals("-1")) {
							sql = "select * from " + table + " where  user_id_single = " + formId + wherePart;//+ citeriaWherePart;
						}
					} else if (generalUtil.getNull(table).toUpperCase().equals("FG_I_STEP_EXPERMNTRESULTS_DT_V")){						//String formId = generalUtilFormState.getFormId(formCode);
						if (!formId.equals("-1")) {
							sql = "select distinct * from " + table + " where  sample_step_id = " + formId + wherePart
									+" order by \"Sample #_SMARTLINK\"";//+ citeriaWherePart;
						}
					} else if (generalUtil.getNull(table).toUpperCase().equals("FG_I_STEP_SELFTESTRESULTS_DT_V")) {
						//String formId = generalUtilFormState.getFormId(formCode);
						if (!formId.equals("-1")) {
							sql = "select distinct * from " + table + ""
									+ " where  sample_experiment_id = (select experiment_id from fg_s_step_v where formid ='" + formId + "')"
									+ " and step_id = '"+formId+"'"+wherePart
									+ " order by \"Sample #_SMARTLINK\",to_number(ACTIONNUMBER)";//+ citeriaWherePart;
						}
					} else if (generalUtil.getNull(table).toUpperCase().contains("SELFTESTRESULTS")) {
						//String formId = generalUtilFormState.getFormId(formCode);
						if (!formId.equals("-1")) {
							if(table.toUpperCase().equals("FG_R_SELFTESTRESULTS_DTOR_V")){
								if(formCode.equals("ExperimentCP")){
									String runNumber = generalUtilFormState.getFormParam(stateKey, formCode, "$P{RUNNUMBER_PARAM}");
									wherePart +=(!generalUtil.getNull(runNumber).isEmpty()?(runNumber.equals("0")?"and nullif(RUNNUMBER,'0') is null":" and RUNNUMBER = '"+runNumber+"'"):"");
								}
								sql = "select distinct * from " + table + " where  sample_experiment_id = " + formId + wherePart
										+ " order by \"Sample #_SMARTLINK\", STEP_ID,to_number(ACTIONNUMBER)";//+ citeriaWherePart;
							} else{
								sql = "select * from " + table + " where  experiment_id = " + formId + wherePart;//+ citeriaWherePart;
							}
						}

					}else if(generalUtil.getNull(table).equalsIgnoreCase("FG_R_SELFTESTRESULT_ALLRUN_V")){
						String exp_id = generalUtilFormState.getFormParam(stateKey, formCode, "$P{PARENT_ID}");
						sql = "select distinct * from " + table + " where  sample_experiment_id = " + exp_id
								+ " order by \"RUN No\", \"Step No\",to_number(ACTIONNUMBER)";
					} else if (generalUtil.getNull(table).equalsIgnoreCase("FG_R_EXPERIMENTRESULTS_DT_V")
							|| generalUtil.getNull(table).equalsIgnoreCase("FG_R_EXPERIMENTRESULTS_DTFOR_V")
							||generalUtil.getNull(table).equalsIgnoreCase("FG_R_EXPERIMENTRESULTS_DTPR_V")) {
						//String formId = generalUtilFormState.getFormId(formCode);
						if (!formId.equals("-1")) {
							if(generalUtil.getNull(table).equalsIgnoreCase("FG_R_EXPERIMENTRESULTS_DT_V")){
								if(formCode.equals("ExperimentCP")){
									String runNumber = generalUtilFormState.getFormParam(stateKey, formCode, "$P{RUNNUMBER_PARAM}");
									sql = "select distinct t.* "
										+ "from FG_S_EXPERIMENTRES_DT_V t "
										+ "where t.SAMPLE_EXPERIMENT_ID = '"+formId+"'"
										+(!generalUtil.getNull(runNumber).isEmpty()?(runNumber.equals("0")?"and nullif(RUNNUMBER,'0') is null":" and RUNNUMBER = '"+runNumber+"'"):"")
										+ " order by \"Sample #_SMARTLINK\"";
								} else {
									sql = "select distinct t.* "
										+ "from FG_S_EXPERIMENTRES_DT_V t "
										+ "where t.SAMPLE_EXPERIMENT_ID = '"+formId+"'"
										+ " order by \"Sample #_SMARTLINK\"";
								}
								 
							} else{
								sql = "select distinct t.\"Batch_SMARTLINK\",t.\"Sample origin\",t.\"Sample amount\",t.\"Sample type\""
										+ ",t.\"Sample_SMARTLINK\",t.\"Request #_SMARTLINK\",t.\"Protocol Type\",t.\"Experiment #_SMARTLINK\""
										+ ",t.\"Result Type\",t.\"Material_SMARTLINK\",t.\"Value_SMARTNUM\",t.\"Value (UOM)\""
										+ (generalUtil.getNull(table).equalsIgnoreCase("FG_R_EXPERIMENTRESULTS_DTFOR_V")
												? ",t.\"Adjusted Value\",t.\"Adjusted Value (UOM)\"" : "")
										+ ",FG_ADAMA.GET_OOS_SMARTICON_OBJ(specificationId_in => s.SPECIFICATIONREF_ID"
										+ ",resultvalue_in => t.RESULT_VALUE" + ",resultuom_id_in => t.RESULT_UOM_ID"
										+ ",subproject_id_in => s.SUBPROJECT_ID) as \"OOS_SMARTICON\""
										+ (!generalUtil.getNull(table).equalsIgnoreCase("FG_R_EXPERIMENTRESULTS_DTFOR_V")
												? ",t.\"Chromatogram_SMARTFILE\"" : ",t.\"File_SMARTFILE\"")
										/*+",decode(t.\"Result Type\",'Impurity Identification'"
										+ ",FG_GET_SMART_LINK_OBJECT('' ,t.desExFormCode "
										+",(select structure from fg_s_manualresultsmsref_v where formid = t.resultref_id and sessionid is null and active =1) "
										          +" ,'','chemdoodle')"
										+",FG_GET_SMART_LINK_OBJECT('' ,t.desExFormCode ,t.EXPERIMENTDEST_ID ,'Documents' ))AS \"File_SMARTFILE\""
										+ (generalUtil.getNull(table).equalsIgnoreCase("FG_R_EXPERIMENTRESULTS_DT_V")?","
												+"decode(t.\"Result Type\",'Impurity Identification'"
														+ ",FG_GET_SMART_LINK_OBJECT ('' ,t.desExFormCode ,t.EXPERIMENTDEST_ID ,'Chromatograms' )"
												+",'')AS \"Chromatogram_SMARTFILE\"":"")
										*/ + (!(generalUtil.getNull(table).equalsIgnoreCase("FG_R_EXPERIMENTRESULTS_DTFOR_V"))? ",t.\"MS manual result_SMARTFILE\"" : "") + ",t.\"Comments\"" + " from (" + "with FILT_IN as" + "(select SAMPLE_ID from fg_s_sample_v where EXPERIMENT_ID = '" + formId + "')" + ", FILT_IN2 as" + "(select distinct dd.*,sss.SAMPLE_ID as sampledest_id"//sampledest_id contains the samples that are in the destination experiment
										+ " from " + table + " dd" + ",fg_s_sampleselect_all_v sss"
										+ " where dd.EXPERIMENTDEST_ID = sss.PARENTID(+))"
										+ " select distinct * from (select * from FILT_IN2) t"
										/*+ " where  experimentorigin_id = '" + formId +"'"
										+ " or SAMPLE_ID in (select SAMPLE_ID from FILT_IN)"+wherePart+") t"*/
										+ " where  decode(experimentorigin_id,'" + formId + "',1"
										+ " ,decode(nvl(SAMPLE_ID,sampledest_id),null,0,instr(','||(select listagg(SAMPLE_ID,',') within group(order by SAMPLE_ID) from FILT_IN)||',',','||nvl(SAMPLE_ID,sampledest_id)||',')))!=0 "
										+ wherePart + ") t" + ",fg_s_specificationref_v s" + " where nvl(t.experiment_id,'"
										+ formId + "') = '" + formId + "'"
										+ " and t.SPECIFICATIONREF_ID = s.SPECIFICATIONREF_ID(+)".replaceAll("'OOS'", "")
										+ " order by \"Sample_SMARTLINK\"";//+ citeriaWherePart;
							}
						}
					} else if(generalUtil.getNull(table).equalsIgnoreCase("FG_S_EXPERIMENTRES_DTRECIPE_V")){
						sql = "select distinct t.* "
								+ "from FG_S_EXPERIMENTRES_DTRECIPE_V t where 1=1 ";
						sql+= generalUtil.getEmpty(getWherePartByFilterForDataTableApi(stateKey, formCode, sourceElementImpCode,
								table.toUpperCase())," and 1=2");
					} else if( generalUtil.getNull(table).equalsIgnoreCase("FG_R_EXPERIMENTRES_ALLRUN_V")){
						String exp_id = generalUtilFormState.getFormParam(stateKey, formCode, "$P{PARENT_ID}");
						
						sql = "select distinct t.* "
								+ "from " + table + " t "
								+ "where t.SAMPLE_EXPERIMENT_ID = '"+exp_id+"'"
								+ " order by \"RUN No\",\"Step No\"";
					} else if (generalUtil.getNull(table).equalsIgnoreCase("FG_R_SAMPLERESULTS_DT_V")
							|| generalUtil.getNull(table).equalsIgnoreCase("FG_R_SAMPLEUSAGES_DT_V")) {
						//String formId = generalUtilFormState.getFormId(formCode);
						if (!formId.equals("-1")) {
							sql = "select distinct * from " + table + " where SAMPLE_ID =" + formId + " " + wherePart;//+ citeriaWherePart;
						} else if (table.equalsIgnoreCase("FG_R_SAMPLERESULTS_DT_V")) {
							sql = "select * from " + table + " where 1=1 " + wherePart;
						}
					} else if (generalUtil.getNull(table).equalsIgnoreCase("FG_R_EXPERIMENTRESULTS_DTAN_V")) {
						//String formId = generalUtilFormState.getFormId(formCode);
						if (!formId.equals("-1")) {
							sql = "select distinct * from " + table + " where  experimentdest_id = '" + formId + "'"
									+ wherePart;//+ citeriaWherePart;
						}
					} /*else if (generalUtil.getNull(table).equalsIgnoreCase("FG_S_SPECIFICATIONS_DT_V")){
						sql = "select * from " + table + " where 1=1 " + wherePart 
								+ "and SPECIFICATIONREF_ID in (select SPECIFICATIONREF_ID from fg_s_SPECIFICATIONREF_V where sessionid is null and nvl(active,'1') ='1')";
						
						} */// default
					else if (generalUtil.getNull(table).equalsIgnoreCase("fg_ur_exp_analysis_v")) {

						wherePart = getWherePartByFilterForDataTableApi(stateKey, formCode, sourceElementImpCode,
								table.toUpperCase());
						String wherePartCopy = "";
						String materialWherePart = "";

						wherePartCopy = wherePart;
						Pattern pattern = Pattern.compile("and \"REACTANT_\" in \\(.*?\\)");
						Matcher matcher = pattern.matcher(wherePartCopy);
						while (matcher.find()) {
							String found_ = matcher.group(0);
							System.out.println(found_);
							wherePart = wherePartCopy.replace(found_, "");
							materialWherePart += found_.replaceAll("and \"REACTANT_\" in", " or \"REACTANT_\" in");
							//							found_ = getTmpDataFilter(found_.replace("getTmpDataFilter(", "").replace(")",""));
							//							sqlFilterToReturn = sqlFilter.replaceFirst("getTmpDataFilter\\(.*?\\)", found_);
							//							matcher.appendReplacement(sqlFilterToReturn, found_);
						}

						wherePartCopy = wherePart;
						pattern = Pattern.compile("and \"PRODUCT_\" in \\(.*?\\)");
						matcher = pattern.matcher(wherePartCopy);
						while (matcher.find()) {
							String found_ = matcher.group(0);
							System.out.println(found_);
							wherePart = wherePartCopy.replace(found_, "");
							materialWherePart += found_.replaceAll("and \"PRODUCT_\" in", " or \"PRODUCT_\" in");
							//							found_ = getTmpDataFilter(found_.replace("getTmpDataFilter(", "").replace(")",""));
							//							sqlFilterToReturn = sqlFilter.replaceFirst("getTmpDataFilter\\(.*?\\)", found_);
							//							matcher.appendReplacement(sqlFilterToReturn, found_);
						}

						wherePartCopy = wherePart;
						pattern = Pattern.compile("and \"SOLVENT_\" in \\(.*?\\)");
						matcher = pattern.matcher(wherePartCopy);
						while (matcher.find()) {
							String found_ = matcher.group(0);
							System.out.println(found_);
							wherePart = wherePartCopy.replace(found_, "");
							materialWherePart += found_.replaceAll("and \"SOLVENT_\" in", " or \"SOLVENT_\" in");
							//							found_ = getTmpDataFilter(found_.replace("getTmpDataFilter(", "").replace(")",""));
							//							sqlFilterToReturn = sqlFilter.replaceFirst("getTmpDataFilter\\(.*?\\)", found_);
							//							matcher.appendReplacement(sqlFilterToReturn, found_);
						}

						if (wherePart.contains("and \"NOSTEPSEQ\" in")) {
							wherePart = wherePart.replaceAll("and \"NOSTEPSEQ\" in", "and to_number(\"NOSTEPSEQ\") <=");
						}

						if (!materialWherePart.isEmpty()) {
							wherePart += " and ( 1=2 " + materialWherePart + ") ";
						}

						sql = "select * from " + table + " where 1=1 " + wherePart;//+ citeriaWherePart;
					} else if (generalUtil.getNull(table).equalsIgnoreCase("FG_R_FORMSEARCHINV_V")) {
						topRowsNum = "1000";
						String searchWherePart = "";
						String searchType = generalUtilFormState.getFormParam(stateKey, "SearchReport",
								"$P{SEARCHORNAVIGATE}");//the type of search
						searchWherePart = getInventorySearchWherePart(stateKey, table.toUpperCase(), "", searchType);
						sql = "select distinct * from " + table + " where 1=1 " + searchWherePart;
					} else if (generalUtil.getNull(table).equalsIgnoreCase("FG_R_FORMSEARCH_V")) {
						topRowsNum = "1000";
						String searchWherePart = "";
						String searchInFiles = generalUtil.getNull(
								generalUtilFormState.getFormParam(stateKey, "SearchReport", "$P{SEARCHINFILES}"));//0- search in files has failed. 1 - Succeeded
						String searchType = generalUtilFormState.getFormParam(stateKey, "SearchReport",
								"$P{SEARCHORNAVIGATE}");//the type of search
						Map<String, String> map_ = generalUtilFormState.getFormCatalog(stateKey, formCode,
								sourceElementImpCode);

						//No material was selected in the inventory results table
						if (generalUtil.getNull(map_.get("inventoryResults.ID")).isEmpty()) {
							searchWherePart = getSearchWherePart(stateKey, table.toUpperCase(), searchInFiles, searchType);
						}

						String advancedSearch = getAdvancedSearch(stateKey, formCode, sourceElementImpCode,
								searchInFiles);

						sql = "select distinct formid,\"Project\",\"Entity\",\"Entity Type\",\"Name\",\"Value\",\"File_SMARTFILE\",\"Path_SMARTPATH\" from "
								+ table + " where 1=1 " + searchWherePart + advancedSearch;
						if (searchInFiles.equals("0")) {//search in files has failed
							String searchFailed = " union all "
									+ " select '','{\"displayName\":\"' || '' || '\" ,\"icon\":\"' || 'fa fa-warning' || '\", \"tooltip\":\"'||'Warning: Search in files has failed!'||'\"}'"
									+ ",'','','','','','','' from dual " + " order by \" _SMARTICON\"";
							sql = "select distinct formid," + "'' as \" _SMARTICON\", "
									+ "\"Project\",\"Entity\",\"Entity Type\",\"Name\",\"Value\",\"File_SMARTFILE\",\"Path_SMARTPATH\" from "
									+ table + " where 1=1 " + searchWherePart + advancedSearch + searchFailed;
						}
						generalUtilFormState.setFormParam(stateKey, formCode, "SEARCHINFILES", "");// reset "SEARCHINFILES" flag 
					} else if (generalUtil.getNull(table).equalsIgnoreCase("fg_s_invitemmaterial_dupl_v")) {

						String itemsId = generalUtilFormState.getFormParam(stateKey, formCode)
								.get("$P{MATERIALDUPLICATESID}");
						wherePart = " where formid in (" + itemsId + ")";
						sql = "select * from " + table + wherePart;
					} else if (generalUtil.getNull(table).equalsIgnoreCase("fg_r_experiment_at_v")) {

						String itemsId = generalUtilFormState.getFormParam(stateKey, formCode)
								.get("$P{CURRENT_EXPERIMENT}");
						wherePart = " where formnumberid = '" + itemsId + "'";
						sql = "select * from " + table + wherePart;
					} else if (generalUtil.getNull(table).equalsIgnoreCase("fg_r_src_hst_v")) {
						String formIdFilter = "";
						String formIdSearch = generalUtilFormState.getFormParam(stateKey, formCode)
								.get("$P{ADHOC_FORMIDSEARCH}");
						if (!generalUtil.getNull(formIdSearch).equals("")) {
							formIdFilter += " and formId = '" + formIdSearch + "'";
						}
						sql = "select * from " + table + " where 1=1 " + wherePart + formIdFilter;
					} else if (generalUtil.getNull(table).equalsIgnoreCase("FG_S_API_STB_RESULT_DTE_V")) { //"Taro Develop"
						sql = "select * from " + table + " where 1=1 AND EXPERIMENT_ID='" + formId + "'" + wherePart;
					} else if (generalUtil.getNull(table).equalsIgnoreCase("FG_S_API_STB_RESULT_PVT_DTE_V")) { //"Taro Develop" 
						sql = "select * from " + table + " where 1=1 AND EXPERIMENT_ID='" + formId + "'" + wherePart;
					} else if (generalUtil.getNull(table).equalsIgnoreCase("fg_r_requestsubseq_v")){
						
						String subWherePart = getWherePartByFilterForDataTableApi(stateKey, formCode,
								sourceElementImpCode, "fg_i_conn_request_smpl_v");
						if(subWherePart.isEmpty()){
							subWherePart=" and 1=2 ";
						}
						wherePart = (linkToLastSelection.equals("1")
									? " and request_id in(select request_id from fg_i_conn_request_smpl_v where 1=1 "+subWherePart +")"
									: "");
							sql = "select * from " + table + " where 1=1 and experiment_id='"+formId+"' "+ wherePart;;
					} else if (generalUtil.getNull(table).equalsIgnoreCase("fg_r_general_dummy_sql_v")) { //Query Generator dummy sql - SQLGenerator screen
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
					} else if (generalUtil.getNull(table).equalsIgnoreCase("FG_R_RESUSINGTOUPDATE_V")) {
						sql = "select * from " + table + " where sampleid = '"+formId+"'";//+ citeriaWherePart;
					} else if (generalUtil.getNull(table).equalsIgnoreCase("FG_I_HISTORICALDATA_DT_V")) {
						String dateFilter="";
						if(dateRange!=null && !dateRange.isEmpty() && !dateRange.equals("00/00/0000;00/00/0000")){
							 dateFilter=dateRange.split(";")[2];
							String minDate=dateRange.split(";")[0];
							String maxDate=dateRange.split(";")[1];
						
							if(!dateRange.contains("00/00/0000;00/00/0000")&&!dateFilter.equals("NA")&&!dateFilter.equals("")&&dateFilter!=null) {
								
								if(minDate.equals("00/00/0000")) {
									sql = "select * from " + table + " where "+dateFilter+" is not null and  trunc("+dateFilter+") <= TO_DATE('"+maxDate+"','dd/mm/yyyy')";	
								}
								else {
									sql = "select * from " + table + " where "+dateFilter+" is not null and trunc("+dateFilter+") between TO_DATE('"+minDate+"','dd/mm/yyyy') AND TO_DATE('"+maxDate+"','dd/mm/yyyy')";					
								}
								sql+= getWherePartByFilterForDataTableApi(stateKey, formCode, sourceElementImpCode,
										table.toUpperCase());
							}
						}
					} else if (generalUtil.getNull(table).equalsIgnoreCase("FG_R_PROJECTSUMMARY_DTDOC_V")) {
						sql = "select   t.\"FORMID\",t.\"FILE_ID\",t.\"TABLETYPE\",t.\"PARENTID\",t.\"FORM_TEMP_ID\",t.\"ACTIVE\",t.\"Title\",\r\n" + 
								"       --\"SEARCH_MATCH_ID1\",\r\n" + 
								"       t.\"Type\",t.\"Description\",t.\"Entity Type\",t.\"Entity Path_SMARTPATH\",\r\n" + // Path_SMARTPATH or Entity Name_SMARTLINK
								"       d.FORMDESC as \"Entity Description_SMARTHTML\"\r\n" + 
								"from FG_R_PROJECTSUMMARY_DTDOC_V t, FG_FORM_ID_DESC d \r\n" + 
								"where t.SEARCH_MATCH_ID1 = '" + formId + "' and t.Entity_ID = d.formid(+) and t.sensitivity_permisiion <= " + generalUtilPermission.getSensitivitylevelOrder(userId);
					}
					else {
						sql = "select * from " + table + " where 1=1 " + wherePart;//+ citeriaWherePart;
					}
				}

				// ***************************
				// *** onElementDataTableApiChange struct WorkupFiltration - WuFiltraSqueezRef
				// ***************************
				else if (struct.equalsIgnoreCase("WuFiltraSqueezRef")) {
					//String formId = generalUtilFormState.getFormId(formCode);
					generalUtilLogger.logWriter(LevelType.DEBUG, "formId=" + formId, ActivitylogType.DataTable, formId);//OK
					sql = "select * from " + table + " where TABLETYPE='" + tableType + "'  and PARENTID='" + formId
							+ "' " + generalUtilFormState.getWherePartForTmpData(struct, formId)
							+ getWherePartByFilterForDataTableApi(stateKey, formCode, sourceElementImpCode,
									table.toUpperCase());
				}

				// ***************************
				// *** onElementDataTableApiChange struct WorkupCrystallize - WuCryMixDefineRef
				// ***************************
				else if (struct.equalsIgnoreCase("WuCryMixDefineRef")) {
					//String formId = generalUtilFormState.getFormId(formCode);
					generalUtilLogger.logWriter(LevelType.DEBUG, "formId=" + formId, ActivitylogType.DataTable, formId);//OK
					sql = "select * from " + table + " where TABLETYPE='" + tableType + "'  and PARENTID='" + formId
							+ "' " + generalUtilFormState.getWherePartForTmpData(struct, formId)
							+ getWherePartByFilterForDataTableApi(stateKey, formCode, sourceElementImpCode,
									table.toUpperCase());
				} else if (struct.equalsIgnoreCase("Impurities")) {
					//String formId = generalUtilFormState.getFormId(formCode);
					generalUtilLogger.logWriter(LevelType.DEBUG, "formId=" + formId, ActivitylogType.DataTable, formId);//OK
					sql = "select * from " + table + " where PARENTID='" + formId + "'"; // yp 14052108 - I make "Impurities" FROM as struct in the DB in order to have new parent id when opening the form
				} else if (struct.equalsIgnoreCase("TempGradientRef")) {
					//String formId = generalUtilFormState.getFormId(formCode);
					String tableTypeWherePart = "1=1";
					sql = "select distinct * from " + table + " where " + tableTypeWherePart + " and PARENTID='"
							+ formId + "' " + generalUtilFormState.getWherePartForTmpData(struct, formId)
							+ " order by CREATION_DATE";
				}else if (struct.equalsIgnoreCase("BatchSelect")) {
					if(formCode.equals("ExperimentAn")){
					String wherePart = (linkToLastSelection.equals("1")
							? getWherePartByFilterForDataTableApi(stateKey, formCode, sourceElementImpCode, getFilterTableByStruct(struct))
							: "");
					sql = "select * from (WITH FILTER_SQL AS (select "
							+ "invitemmaterial_id AS FILTER_SQL_ID from "
							+ getFilterTableByStruct(struct) + " where 1=1 " + wherePart + " ) select * from "
							+ table + " where 1=1  and invitemmaterial_id"
							+ " in (SELECT FILTER_SQL_ID FROM FILTER_SQL)) where 1=1 and PARENTID='" + formId + "'"
							+ generalUtilFormState.getWherePartForTmpData(struct, formId);//+ citeriaWherePart
					} else {
						String wherePart = (linkToLastSelection.equals("1")
							? getWherePartByFilterForDataTableApi(stateKey, formCode, sourceElementImpCode, getFilterTableByStruct(struct))
							: "");
						sql = "select * from " + table + " where PARENTID='" + formId + "' "
							+ wherePart
							+ generalUtilFormState.getWherePartForTmpData(struct, formId);
					}
					
				}else if (struct.equalsIgnoreCase("MaterialsPeaks")) {
					String sql_ = "select t.EXPERIMENTTYPENAME from FG_I_CONN_REQUEST_OPTYPE_V t where t.REQUEST_ID = '"
							+ formId+"'";
					List<String> ExperimentType = generalDao.getListOfStringBySql(sql_);
					String columns = "";
					// If the request operation type is "Standard supply", the name of the field will be "Request Quantity". 
					//In all other cases, the name of the field will be "Estimated concentration".
					if(ExperimentType!=null && ExperimentType.contains("Standard Supply")){
						columns = " t.\"MATERIALSPEAKS_ID\",t.\"FORM_TEMP_ID\",t.\"FORMID\",t.\"PARENTID\",\"No.\",\"Name_SMARTEDIT\",\"Sample_SMARTEDIT\",\"Request Quantity_SMARTEDIT\",\"Comments_SMARTEDIT\" ";
						
					}else{
						columns = " t.\"MATERIALSPEAKS_ID\",t.\"FORM_TEMP_ID\",t.\"FORMID\",t.\"PARENTID\",\"No.\",\"Name_SMARTEDIT\",\"Sample_SMARTEDIT\",\"Estimated Conc._SMARTEDIT\",\"Comments_SMARTEDIT\" ";
					}
					if(columns.contains("\"No.\"")) {
						columns = columns.replaceFirst("\"No.\"", "rownum as \"No._SMARTNUM\"");
					}
					sql = "select "+columns+" from " + table + " t where PARENTID='" + formId
							+ "' " + generalUtilFormState.getWherePartForTmpData(struct, formId);
				} 
				else if(struct.equalsIgnoreCase("Composition")){
					if(formCode.equalsIgnoreCase("ExperimentFor") || formCode.equalsIgnoreCase("StepMinFr")){
						String recipeFormulation_id = generalUtilFormState.getFormParam(stateKey, formCode, "$P{RECIPEFORMULATION_ID}");
						sql = "select * from " + table + "\n"
							+ " where 1=1 "
							/*+ (generalUtil.getNull(recipeFormulation_id).isEmpty()?
									generalUtil.getEmpty(getWherePartByFilterForDataTableApi(stateKey, formCode,
											sourceElementImpCode, table)," and 1=2")
									:" and parentid = '"+recipeFormulation_id+"'");/*/
							+(linkToLastSelection.equals("1")?generalUtil.getEmpty(getWherePartByFilterForDataTableApi(stateKey, formCode,
											sourceElementImpCode, table)," and 1=2")
									:" and PARENTID = '"+formId+"'" + generalUtilFormState.getWherePartForTmpData(struct, formId));
					} else {//dte
						optionalAttributes1 = "RecipeFormulation";//not necessary
						sql = "select * from " + table + " where 1=1 and PARENTID='" + formId
								+ "' " + generalUtilFormState.getWherePartForTmpData(struct, formId);
					}
				}

				// ***************************
				// *** onElementDataTableApiChange struct SysEventHandlerSet
				// ***************************
				//			else if (struct.equalsIgnoreCase("SysEventHandlerSet")) {
				//				//String formId = generalUtilFormState.getFormId(formCode);
				//				logger.debug("formId=" + formId); //OK
				//				sql = "select * from " + table + " where SYSEVENTMANAGER_ID='" + formId + "' "
				//						+ getWherePartByFilterForDataTableApi(stateKey, formCode, sourceElementImpCode, table.toUpperCase());
				//			}		
				else {
					//get SQL by structFormType
					switch (structFormType) {
						//--------------------------------
						//---- structFormType - onElementDataTableApiChange ATTACHMENT / onElementDataTableApiChange REF
						//--------------------------------	
						case ATTACHMENT:
						case REF: {
							//String formId = generalUtilFormState.getFormId(formCode);
							String tableTypeWherePart = "1=1";
							if (struct.equalsIgnoreCase("Document") || struct.equalsIgnoreCase("ResultRef")) { // TODO all REF should have TABLETYPE
								tableTypeWherePart = "TABLETYPE='" + tableType + "'";
							}
							generalUtilLogger.logWriter(LevelType.DEBUG, "formId=" + formId, ActivitylogType.DataTable,
									formId);
                             if(struct.equalsIgnoreCase("functionRuleRef") && display.equalsIgnoreCase("dt")) {
								
								sql = "select * from " + table + " where " + tableTypeWherePart + " and PARENTID='" + formId
										+ "' and sessionid is null  "+generalUtilFormState.getWherePartForTmpDataByFormId(struct, formId)
										+ " union all  select * from " + table + " where " + tableTypeWherePart + " and PARENTID='" + formId 
										+ "' "+ generalUtilFormState.getWherePartForTmpData(struct, formId);
							}
                             if(struct.equalsIgnoreCase("MaterialComponent") && display.equalsIgnoreCase("dtm")) {
 								
 								sql = "select * from " + table + " where " + tableTypeWherePart + " and PARENTID='" + formId
 										+ "' and sessionid is null  "+generalUtilFormState.getWherePartForTmpDataByFormId(struct, formId)
 										+ " union all  select * from " + table + " where " + tableTypeWherePart + " and PARENTID='" + formId 
 										+ "' "+ generalUtilFormState.getWherePartForTmpData(struct, formId);
 							}
                             if(struct.equalsIgnoreCase("MaterialComponent") && display.equalsIgnoreCase("dtb")) {
  								sql = "select * from " + table + " where " + tableTypeWherePart + " and PARENTID='" + formId
  										+ "' and sessionid is null  "+generalUtilFormState.getWherePartForTmpDataByFormId(struct, formId)
  										+ " union all  select * from " + table + " where " + tableTypeWherePart + " and PARENTID='" + formId 
  										+ "' "+ generalUtilFormState.getWherePartForTmpData(struct, formId);
  							}
							if(struct.equalsIgnoreCase("paramref") && display.equalsIgnoreCase("dteExp")) {
								optionalAttributes = generalUtil.getEmpty(formDao.getFromInfoLookup("Experiment", LookupType.ID, formId, "STATUSNAME"), "Planned");
								if(!optionalAttributes.equals("Planned"))
								{
									table = "fg_s_paramref_dteexp_act_v";
								}
								sql = "select * from " + table + " where " + tableTypeWherePart + " and PARENTID='" + formId
										+ "' and sessionid is null  "+generalUtilFormState.getWherePartForTmpDataByFormId(struct, formId)
										+ " union all  select * from " + table + " where " + tableTypeWherePart + " and PARENTID='" + formId 
										+ "' "+ generalUtilFormState.getWherePartForTmpData(struct, formId);
							}
							else if(struct.equalsIgnoreCase("paramref"))
							{
								optionalAttributes = generalUtil.getEmpty(formDao.getFromInfoLookup("Step", LookupType.ID, formId, "STATUSNAME"), "Planned");
								if(!optionalAttributes.equals("Planned"))
								{
									if(display.equalsIgnoreCase("dte"))
									{
										table = "fg_s_paramref_dte_act_v";
									}
									else
									{
										table = "fg_s_paramref_dt_act_v";
									}
								}

								sql = "select * from " + table + " where " + tableTypeWherePart + " and PARENTID='" + formId
										+ "' " + generalUtilFormState.getWherePartForTmpData(struct, formId);
							}
							else if(struct.equalsIgnoreCase("instrumentref") && display.equalsIgnoreCase("dte")){
								String userLab_id = formDao.getFromInfoLookup("user",LookupType.ID,userId,"LABORATORY_ID");
								sql = "select t.\"FORMID\",t.\"PARENTID\",t.\"INVITEMINSTRUMENT_ID\",t.FORM_TEMP_ID\r\n" + 
										",t.\"Serial #\","
										+ "fg_get_instrument_userLab_list(t.INVITEMINSTRUMENT_ID,'"+userLab_id+"',t.INVITEMINSTRUMENT_ID)  as \"Instrument Name_SMARTEDIT\""
										+ ",t.\"Model\"\r\n" + 
										",t.\"Manufacturer\"\r\n" + 
										",t.\"Last Calibration Date\" from " + table + " t where " + tableTypeWherePart + " and PARENTID='" + formId
										+ "' " + generalUtilFormState.getWherePartForTmpData(struct, formId);
							}
							else {
								sql = "select * from " + table + " where " + tableTypeWherePart + " and PARENTID='" + formId
										+ "' " + generalUtilFormState.getWherePartForTmpData(struct, formId);
							}
							
						}
							break;
						//--------------------------------
						//---- structFormType - onElementDataTableApiChange SELECT 
						//--------------------------------	
						case SELECT: {
							//String formId = generalUtilFormState.getFormId(formCode);
							sql = "select * from " + table + " where PARENTID='" + formId + "' "
									+ generalUtilFormState.getWherePartForTmpData(struct, formId); // TODO Tehila sample filter request task use  getWherePartByFilterForDataTableApi(stateKey, formCode, sourceElementImpCode, table.toUpperCase());

							/*if(linkToLastSelection.equals("1")){*/
							List<Map<String, Object>> toReturnWithNoFilter = null;
							toReturnWithNoFilter = generalDao.getListOfMapsBySql(sql);
							formIdForSharedTables = toReturnWithNoFilter != null && !toReturnWithNoFilter.isEmpty()
									? toReturnWithNoFilter.get(0).entrySet().iterator().next().getValue().toString()
									: "-1";
							/*} */

							if (struct.equalsIgnoreCase("SampleSelect")) {
								if (!formId.equals("-1")) {
									if (formCode.equals("Request")) {
										sql += " and SAMPLE_ID in (select sr.sampleid" + " from fg_s_request_pivot r,"
												+ " fg_s_sampledataref_pivot sr," + " where sr.PARENTID = " + formId
												+ ") ";
									} else if (formCode.equals("ExperimentAn") || formCode.startsWith("ExperimentPr")) {
										String wherePart = (linkToLastSelection.equals("1")
												? getWherePartByFilterForDataTableApi(stateKey, formCode,
														sourceElementImpCode, getFilterTableByStruct(struct))
												: "");
										if (!wherePart.equals("")) {
											sql = sql + " and " + "\"_SMARTSELECTALLNONE"
													+ "\" in (select sampleid from FG_I_CONN_REQUESTSELECT_SMPL_V where 1=1 "
													+ wherePart + ")";
										}
									} else if(formCode.equals("ExperimentCP")){
										String runNumber = generalUtilFormState.getFormParam(stateKey, formCode, "$P{RUNNUMBER_PARAM}");
										sql+= (!generalUtil.getNull(runNumber).isEmpty()?(runNumber.equals("0")?"and (nullif(RUNNUMBER,'0') is null or experiment_id!='"+formId+"')":" and RUNNUMBER = '"+runNumber+"' and experiment_id='"+formId+"'"):"");
									} else if (formCode.equals("ExperimentFor")) {

										String wherePart = (linkToLastSelection.equals("1")
												? getWherePartByFilterForDataTableApi(stateKey, formCode,
														sourceElementImpCode, "FG_I_CONN_BATCH_SAMPLE_V")
												: "");
										sql = sql + wherePart;
									
									}
								}
							} else if (struct.equalsIgnoreCase("RequestSelect")) {
								if (!formId.equals("-1")) {
									if (formCode.equals("Sample")) {
										sql += " and singleReq_id in (select r.formid" + " from fg_s_request_pivot r,"
												+ " fg_s_sampledataref_pivot sr," + " where sr.PARENTID = r.formid"
												+ " and sr.SAMPLE_ID = " + formId + ") ";
									}
									if (formCode.startsWith("Experiment") || formCode.startsWith("Step")) {
										String wherePart = (linkToLastSelection.equals("1")
												? getWherePartByFilterForDataTableApi(stateKey, formCode,
														sourceElementImpCode, getFilterTableByStruct(struct))
												: "");
										if (!wherePart.equals("")) {
											sql = sql
													+ " and request_id in(select request_id from FG_I_CONN_REQUESTSELECT_SMPL_V where  1 = 1 "
													+ wherePart + ")";
										}
									}
								}
							} else if (struct.equalsIgnoreCase("BatchFrSelect")) {
								sql += getWherePartByFilterForDataTableApi(stateKey, formCode, sourceElementImpCode,
										table.toUpperCase());
							} else if (struct.equalsIgnoreCase("MaterialSelect")) {
								if (formCode.equals("ReportDesignExp")) {
									String matrilaList = generalUtilFormState.getFormParam(stateKey, formCode, "$P{STEPIMPURITIESIDLIST}");
									if(matrilaList == null || matrilaList.isEmpty()) {
										matrilaList = "-1";
									}
									sql = "select * from " + table + " where invitemmaterial_id in (" + matrilaList + ")";
								}
								 
							}
						}
						//--------------------------------
						//---- Form Type - getFormParam default:
						//--------------------------------	
						default:
							break;
					}
				}
			}

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

	/**
	 * adds an sql script that filters the displayed data up to the criteria
	 * 
	 * @param struct
	 * @param criteria
	 * @param userId
	 * @return
	 */
	@Override
	public String getrCiteriaWherePart(long stateKey, String struct, String idName, String criteria, String userId,
			String formCode, List<String> unfilteredList, String lastMultiValues) {
		String sqlScript = "";
		Map<String, String> sqlParam = generalUtilFormState.getFormParam(stateKey, formCode);

		sqlParam.put("$P{STRUCT}", struct);
		sqlParam.put("$P{USERID}", userId);
		sqlScript = generalUtilConfig.getCriterialSql(struct, criteria, formCode, sqlParam, unfilteredList);

		return sqlScript;
	}
	
	/**
	 * 
	 * @param struct: Project,SubProject,SubSubProject,Experiment,Request,Step,Action,SelfTest,Workup
	 * @return for the main objects:
	 * 		FG_I_ID_CONNECTION_<struct with adjustments>_V;
	 * else:   FG_S_<struct>"_ALL_V  
	 */
	private String getFilterTableByStruct(String struct) {
		String returnTable = "FG_S_" + struct + "_ALL_V"; //Request use it

		if (struct.equalsIgnoreCase("Project") || struct.equalsIgnoreCase("SubProject")
				|| struct.equalsIgnoreCase("SubSubProject")) {
			returnTable = "FG_I_ID_CONNECTION_SSPROJ_M_V";
		} else if (struct.equalsIgnoreCase("Experiment")) {
			returnTable = "FG_I_ID_CONNECTION_EXPR_M_V";
		} else if (struct.equalsIgnoreCase("Step")) {
			returnTable = "FG_I_ID_CONNECTION_STEP_M_V";
		} else if (struct.equalsIgnoreCase("Action")) {
			returnTable = "FG_I_ID_CONNECTION_ACTION_M_V";
		} else if (struct.equalsIgnoreCase("SelfTest")) {
			returnTable = "FG_I_ID_CONNECTION_SELFT_M_V";
		} else if (struct.equalsIgnoreCase("SelfTest")) {
			returnTable = "FG_I_ID_CONNECTION_SELFT_M_V";
		} else if (struct.equalsIgnoreCase("Workup")) {
			returnTable = "FG_I_ID_CONNECTION_WORKUP_M_V";
		} else if (struct.equalsIgnoreCase("Request")) {
			returnTable = "FG_I_CONNECTION_REQUEST_EXPR_V";
		} else if (struct.equalsIgnoreCase("RequestSelect") || struct.equalsIgnoreCase("SampleSelect")) {
			returnTable = "FG_I_CONN_REQUESTSELECT_SMPL_V";
		} else if (struct.equalsIgnoreCase("BatchSelect")){
			returnTable = "FG_I_CONN_BATCH_COMPONENTS_V";
		}
		return returnTable;
	}

	private String getWherePartByFilterForDataTableApi(long stateKey, String formCode, String sourceElementImpCode,
			String tableName) {
		StringBuilder where = new StringBuilder();
		Map<String, String> tableMetaData_ = null;
		Map<String, String> map_ = generalUtilFormState.getFormCatalog(stateKey, formCode, sourceElementImpCode);
		for (Map.Entry<String, String> entry : map_.entrySet()) {
			if (!entry.getKey().contains(".")) { // invalid key
				//				System.out.println("CatalogDBTableImp - " + entry.getKey() + " is invalid catalog item !!!!");
				where.append(" and 1=2 ");
				break;
			}
			//			BeanType beanType = getBeanTypeByItem(entry.getKey().split("\\.")[0]);		
			if (!generalUtil.getNull(entry.getValue()).equals("") && entry.getKey().contains(".")
					&& !generalUtil.getNull(entry.getValue()).equals("'ALL'")) {
				if (tableMetaData_ == null) {
					tableMetaData_ = getTableMetaData(tableName);
				}
				String colName = colInTable(tableMetaData_, entry.getKey().split("\\.")[1]);
				if (!colName.equals("")) {
					//where.append(" and \"" + entry.getKey().split("\\.")[1] + "\" in (" + entry.getValue() + ")\n ");
					//					if(colName.endsWith("OBJDATERANGE")) {
					//						where.append(" and TO_DATE(\"" + colName + "\",'" + generalUtil.getConversionDateFormat() + "') between " + entry.getValue().split(";")[0] + " and " + entry.getValue().split("\\;")[1]);
					//					} else {
					//						where.append(" and \"" + colName + "\" in (" + entry.getValue() + ")\n "); 
					//					}
					if (colName.endsWith("OBJDATERANGE")) {
						if (!entry.getValue().split(";", -1)[0].equals("")) {
							where.append(" and TO_DATE(\"" + colName + "\",'" + generalUtil.getConversionDateFormat()
									+ "') >= " + entry.getValue().split(";")[0]);
						}
						if (!entry.getValue().split(";", -1)[1].equals("")) {
							where.append(" and TO_DATE(\"" + colName + "\",'" + generalUtil.getConversionDateFormat()
									+ "') <= " + entry.getValue().split(";")[1]);
						}
					} else {
						where.append(" and \"" + colName + "\" in (" + entry.getValue() + ")\n ");
					}
				}
			}
		}
		return where.toString();
	}

	private Map<String, String> getTableMetaData(String tableName) {
		return generalDao.getMetaData(tableName.toUpperCase());
	}

	private String colInTable(Map<String, String> meteDataMap, String colName) {
		if (meteDataMap.containsKey(colName)) {
			return colName;
		} else if (meteDataMap.containsKey(colName.toUpperCase())) {
			return colName.toUpperCase();
		}
		return "";
	}

	private String getSearchWherePart(long stateKey, String tableName, String searchInFiles, String searchType) {
		StringBuilder where = new StringBuilder();
		String textForSearch = generalUtilFormState.getFormParam(stateKey, "SearchReport", "$P{TEXTBOX}").replace("'",
				"''");
		String searchInDocsOnly = generalUtil.getNull(generalUtilFormState.getFormParam(stateKey, "SearchReport", "$P{SEARCHINDOCSONLY}"));
		String searchInDocsOnlyAdvanced2 = generalUtil.getNull(generalUtilFormState.getFormParam(stateKey, "SearchReport", "$P{SEARCHINDOCSONLYADVANCE2}"));
		if(generalUtil.getNull(searchType).equals("Free Text") && searchInDocsOnlyAdvanced2.equals("1") && !searchInDocsOnly.equals("1")){
			searchInDocsOnly ="1";//fixed bug 8303
		}
		try {
			if (generalUtil.getNull(searchType).equals("Molecule Search")) {
				return searchByChemdoodle(stateKey, tableName);
			}else{
				if (generalUtil.getNull(textForSearch).contains("\"")) {//A few words are entered in the text box each in quotation marks (eg "A", "B", "C").
					String[] tokens = textForSearch.trim().split("\\s*" + "[,]" + "\\s*");
					String searchFile = searchInFiles.equals("0") || !searchInDocsOnly.equals("1") ? ""
							: "%" + replaceSpecialChar(tokens[0].substring(1, tokens[0].length() - 1).trim()) + "%";
					where.append(" nvl(instr(upper(search_display_value),upper('"
							+ tokens[0].substring(1, tokens[0].length() - 1) + "')),0) ");

					for (int i = 1; i < tokens.length; i++) {
						if (tokens[i].length() < 2) {
							continue;
						}
						searchFile += searchInFiles.equals("0") || !searchInDocsOnly.equals("1") ? ""
								: "|%" + replaceSpecialChar(tokens[i].substring(1, tokens[i].length() - 1).trim())
										+ "%";
						where.append(" + nvl(instr(upper(search_display_value),upper('"
								+ tokens[i].substring(1, tokens[i].length() - 1) + "')),0) ");
					}

					if (!searchInFiles.equals("0") && searchInDocsOnly.equals("1")) {
						where.append("+ decode(file_id,null,0, contains(search_file_value,'" + searchFile + "',1)) ");
					}
					//if(searchInDocsOnly.equals("1")){
						where = new StringBuilder();
						if (searchInFiles.equals("0") || !searchInDocsOnly.equals("1")) {//search in files has failed
							where.append(" 1=2");
						}else{
							where.append(" decode(file_id,null,0, contains(search_file_value,'" + searchFile + "',1)) ");
						}
					//}
				} else {
					if (searchInFiles.equals("0") || !searchInDocsOnly.equals("1") ) {//search in files has failed
						where.append("nvl(instr(upper(search_display_value),upper('" + textForSearch + "')),0) ");
					}/*else if(searchInDocsOnly.equals("1")){
						where = new StringBuilder();
						if (searchInFiles.equals("0")) {//search in files has failed
							where.append(" 1=2");
						}else{
							String searchFile = "%" + replaceSpecialChar(textForSearch.trim()) + "%";
							where.append("+ decode(file_id,null,0, contains(search_file_value,'" + searchFile + "',1)) ");
						}
					}*/ else {
						String searchFile = "%" + replaceSpecialChar(textForSearch.trim()) + "%";
						where.append(" nvl(instr(upper(search_display_value),upper('" + textForSearch
								+ "')),0) + decode(file_id,null,0, contains(search_file_value,'" + searchFile
								+ "',1)) ");
					}
				}
			}
		} catch (Exception e) {
			where.append("and 1=2");
		}
		return " and (" + where.toString() + ") > 0  "; //yp04082019 test this
	}
	
	private String getInventorySearchWherePart(long stateKey, String tableName, String searchInFiles, String searchType) {
		StringBuilder where = new StringBuilder();
		String inventoryFields = "";
		String inventoryFormCode = "";
		String textForSearch = generalUtilFormState.getFormParam(stateKey, "SearchReport", "$P{TEXTBOX}").replace("'",
				"''");
		String inventoryList = generalUtil.getNull(generalUtilFormState.getFormParam(stateKey, "SearchReport",
				"$P{INVENTORYLIST}"));
		try {
			String searchInDocsOnly = generalUtilFormState.getFormParam(stateKey, "SearchReport", "$P{SEARCHINDOCSONLY}");
			/*if(generalUtil.getNull(searchInDocsOnly).equals("1")){//fix bug 8105
				return " and 1 = 2 ";
			}*/
			if(generalUtil.getNull(searchType).equals("Molecule Search")){
				return searchByChemdoodle(stateKey, tableName);
			}else{
				if (generalUtil.getNull(searchType).equals("Compound Search")) {//bug 7566, For "Compound Search" - the search is performed in specific fields
					inventoryFields = " and entityimpcode in ('invItemMaterialName','casNumber','synonyms','itemId','casName','iupacName')";
				}
				else if (generalUtil.getNull(searchType).equals("Batch / Sample / Barcode Scan")) {
					inventoryFields = " and entityimpcode in ('invItemBatchName','externalBatchNumber','formNumberId')";
				}
				else if(generalUtil.getNull(searchType).equals("Free Text") && !inventoryList.isEmpty() && !inventoryList.equals("ALL")){
					inventoryList = inventoryList.replaceAll("Instruments", "Instrument").replaceAll("Samples", "Sample").replaceAll("Materials", "Material").replace(",", "','");
					inventoryFormCode = " and \"Entity\" in ('"+inventoryList+"')";
				}
				
				
				if (generalUtil.getNull(textForSearch).contains("\"")) {//A few words are entered in the text box each in quotation marks (eg "A", "B", "C").
					String[] tokens = textForSearch.split("\\s*" + "[,]" + "\\s*");
					where.append(" instr(upper(search_display_value),upper('"
							+ tokens[0].substring(1, tokens[0].length() - 1) + "')) ");
					for (int i = 1; i < tokens.length; i++) {
						where.append(" + instr(upper(search_display_value),upper('"
								+ tokens[i].substring(1, tokens[i].length() - 1) + "'))");
					}
				} else {
					where.append(" instr(upper(search_display_value),upper('" + textForSearch + "')) ");
				}
			}
		} catch (Exception e) {
			where.append("and 1=2");
		}
		return " and (" + where.toString() + ") > 0  " + inventoryFields + inventoryFormCode; //yp04082019 test this
	}

	private String replaceSpecialChar(String var) {
		return var.replace("[", "\\[").replace("]", "\\]").replace("*", "\\*").replace(";", "\\;").replace("?", "\\?")
				.replace("{", "\\{").replace("}", "\\}").replace("(", "\\(").replace(")", "\\)").replace(",", "\\,")
				.replace("&", "\\&").replace("^", "\\^").replace("'", "''").replace("%", "\\%").replace("$", "\\$")
				.replace("#", "\\#").replace("@", "\\@").replace("!", "\\!").replace("+", "\\+").replace("_", "\\_")
				.replace("=", "\\=").replace("-", "\\-").replace(":", "\\:").replace("~", "\\~").replace(">", "\\>")
				.replace("<", "\\<").replace("|", "\\|");
		//return var;
	}

	private String getAdvancedSearch(long stateKey, String formCode, String sourceElementImpCode,
			String searchInFiles) {
		StringBuilder where = new StringBuilder();
		String searchType = generalUtilFormState.getFormParam(stateKey, "SearchReport",
				"$P{SEARCHORNAVIGATE}");//the type of search
		Map<String, String> map_ = generalUtilFormState.getFormCatalog(stateKey, formCode, sourceElementImpCode);
		boolean materialChecked = false;
		if (!generalUtil.getNull(map_.get("inventoryResults.ID")).isEmpty()) {
			materialChecked = true;//At least one material is selected in the inventory results table
		}

		String lowLevel = returnAdvancedLowLevel(materialChecked, map_);
		String column = lowLevel.replaceAll("2", "");
		String val = generalUtil.getNull(map_.get(lowLevel + "." + lowLevel));
		if (!(val.isEmpty() || val.equals("'ALL'"))) {
			where.append(" and " + column + " in (" + val + ")");
		}
		if (materialChecked) {
			String fileIdList = generalChemLocatorUtil.getFileListByMaterilIdList( map_.get("inventoryResults.ID"));
			String searchInDocsOnly = generalUtilFormState.getFormParam(stateKey, "SearchReport", "$P{SEARCHINDOCSONLYADVANCE2}");
			
			String materialList = "'" + map_.get("inventoryResults.ID").replace(",", "','") + "'";
			String inventory_fields_files = "";
			String smiles_ = "";
			if(generalUtil.getNull(searchType).equals("Compound Search") || generalUtil.getNull(searchType).equals("Molecule Search"))
			{
				String materialName_ = generalUtil.getNull(generalDao.selectSingleString(
						"select listagg(trim(t.InvItemMaterialName),' or ') WITHIN GROUP (ORDER BY t.invitemmaterialname)  from fg_s_invitemmaterial_v t where t.formid in ("
								+ materialList + ") and t.InvItemMaterialName is not null "));
				String casNum_ = generalUtil.getNull(generalDao.selectSingleString(
						"select listagg(trim(t.casNumber),' or ') WITHIN GROUP (ORDER BY t.casnumber)  from fg_s_invitemmaterial_v t where t.formid in ("
								+ materialList + ") and t.casnumber is not null"));
				String casName = generalUtil.getNull(generalDao.selectSingleString(
						"select listagg(trim(t.casName),' or ') WITHIN GROUP (ORDER BY t.casname)  from fg_s_invitemmaterial_v t where t.formid in ("
								+ materialList + ") and t.casname is not null"));
				String itemId_ = generalUtil.getNull(generalDao.selectSingleString(
						"select listagg(trim(t.itemid),' or ') WITHIN GROUP (ORDER BY t.itemid)  from fg_s_invitemmaterial_v t where t.formid in ("
								+ materialList + ") and t.itemid is not null"));
				String synonyms_ = generalUtil.getNull(generalDao.selectSingleString(
						"select listagg(trim(t.SYNONYMS),' or ') WITHIN GROUP (ORDER BY t.SYNONYMS)  from fg_s_invitemmaterial_v t where t.formid in ("
								+ materialList + ") and t.itemid is not null"));
				smiles_ = generalUtil.getNull(generalDao.selectSingleString(
						"select listagg(trim(t.smiles),',') WITHIN GROUP (ORDER BY t.smiles)  from fg_s_invitemmaterial_all_v t where t.formid in ("
								+ materialList + ") and t.smiles is not null"));
				String iupacName = generalUtil.getNull(generalDao.selectSingleString(
						"select listagg(trim(t.iupacName),',') WITHIN GROUP (ORDER BY t.iupacName)  from fg_s_invitemmaterial_all_v t where t.formid in ("
								+ materialList + ") and t.iupacName is not null"));
				if (!casNum_.isEmpty()) {
					casNum_ = " or " + casNum_;
				}
				if (!itemId_.isEmpty()) {
					itemId_ = " or " + itemId_;
				}
				if (!synonyms_.isEmpty()) {
					synonyms_ = " or " + synonyms_;
				}
				if (!casName.isEmpty()) {
					casName = " or " + casName;
				}
				if (!iupacName.isEmpty()) {
					iupacName = " or " + iupacName;
				}
				inventory_fields_files = materialName_ + casNum_ + itemId_ + synonyms_ + iupacName + casName;
			}else if(generalUtil.getNull(searchType).equals("Batch / Sample / Barcode Scan")){
				String internalNumber = generalUtil.getNull(generalDao.selectSingleString(
						"select listagg(trim(t.invItemBatchName),' or ') WITHIN GROUP (ORDER BY t.invItemBatchName)  from fg_s_invitembatch_v t where t.formid in ("
								+ materialList + ") and t.invItemBatchName is not null "));
				String externalNum = generalUtil.getNull(generalDao.selectSingleString(
						"select listagg(trim(t.externalBatchNumber),' or ') WITHIN GROUP (ORDER BY t.externalBatchNumber)  from fg_s_invitembatch_v t where t.formid in ("
								+ materialList + ") and t.externalBatchNumber is not null"));
				String sampleNum = generalUtil.getNull(generalDao.selectSingleString(
						"select listagg(trim(t.formNumberId),' or ') WITHIN GROUP (ORDER BY t.formNumberId)  from fg_s_sample_v t where t.formid in ("
								+ materialList + ") and t.formNumberId is not null"));
				if (!internalNumber.isEmpty() && !externalNum.isEmpty()) {
					externalNum = " or " + externalNum;
				}
				
				inventory_fields_files = internalNumber + externalNum + sampleNum;
			}
			//Elementclass<>'ElementRichTextEditorImp - 210519 fixing bug that happens when the material ID is equal to richtext id 
			if (searchInFiles.equals("0") || !generalUtil.getNull(searchInDocsOnly).equals("1")) {//search in files has failed
				where.append(
						" and search_value in (" + materialList + ") and Elementclass<>'ElementRichTextEditorImp' ");
			} else {
				String files_src = replaceSpecialChar(inventory_fields_files);
				if (!generalUtil.getNull(files_src).isEmpty()) {
					where.append(" and ((search_value in (" + materialList
							+ ") and Elementclass<>'ElementRichTextEditorImp') or exists (select file_id from fg_files_src s where (contains(s.file_content,'"
							+ files_src + "',1)  > 0 or s.cd_smiles in ('" + smiles_.replace(",", "','")
							+ "') or file_id in (" + fileIdList + ")) and s.file_id = fg_r_formsearch_v.file_id)) ");
				} else {
					where.append(" and (search_value in (" + materialList
							+ ") and Elementclass<>'ElementRichTextEditorImp' )");
					where.append(" or file_id in ('" + fileIdList.replace(",", "','") + "'" + ") ");
				}
			}
			}

		return where.toString();
	}

	private String returnAdvancedLowLevel(boolean ismMterialChecked, Map<String, String> map_) {
		String toReturn = "";
		if (ismMterialChecked) {
			toReturn = "SUBSUBPROJECT2_ID";
			if (generalUtil.getNull(map_.get(toReturn + "." + toReturn)).isEmpty()
					|| map_.get(toReturn + "." + toReturn).equals("'ALL'")) {
				toReturn = "SUBPROJECT2_ID";
				if (generalUtil.getNull(map_.get(toReturn + "." + toReturn)).isEmpty()
						|| map_.get(toReturn + "." + toReturn).equals("'ALL'")) {
					toReturn = "PROJECT2_ID";
				}
			}
		} else {
			toReturn = "SUBSUBPROJECT_ID";
			if (generalUtil.getNull(map_.get(toReturn + "." + toReturn)).isEmpty()
					|| map_.get(toReturn + "." + toReturn).equals("'ALL'")) {
				toReturn = "SUBPROJECT_ID";
				if (generalUtil.getNull(map_.get(toReturn + "." + toReturn)).isEmpty()
						|| map_.get(toReturn + "." + toReturn).equals("'ALL'")) {
					toReturn = "PROJECT_ID";
				}
			}
		}
		return toReturn;
	}

	private String searchByChemdoodle(long stateKey, String tableName) {
		String where = "";
		String sql = "";
		String materialList = "";
		String mList_ = generalUtilFormState.getFormParam(stateKey, "SearchReport", "$P{MATERIL_ID_LIST}");//list of materials whose molecular structure (or fragment structure) were painted in a chemical structure box
		String fileIdList = generalUtil
				.getNull(generalUtilFormState.getFormParam(stateKey, "SearchReport", "$P{FILE_ID_LIST}"));//list of files containing the structure of the molecule
		if (tableName.equals("FG_R_FORMSEARCH_V")) {// project result
			if (generalUtil.getNull(mList_).isEmpty() || generalUtil.getNull(mList_).equals("-1")) {// No matching material for search results
				where = " and 1=2";
			} else {
				materialList = "'" + mList_.replace(",", "','") + "'";
				where = " and search_value in (" + materialList + ") ";
			}
			if (!generalUtil.getNull(fileIdList).isEmpty() && !generalUtil.getNull(fileIdList).equals("-1")) {
				where += " or file_id in ('" + fileIdList.replace(",", "','") + "'" + ") ";
			}
		} else {//inventory result
			if (generalUtil.getNull(mList_).isEmpty() || generalUtil.getNull(mList_).equals("-1")) {//No matching material for search results
				where = " and 1=2";
			} else {
				/*sql = "select listagg('\"'||trim(t.invitemmaterialname)||'\"',',') WITHIN GROUP (ORDER BY invitemmaterialname) from fg_s_invitemmaterial_pivot t where t.formid in ("
						+ mList_ + ")";
				List<String> material = generalDao.getListOfStringBySql(sql);
				materialList = generalUtil.listToCsv(material).replace("'", "''").replace("\"", "'");*/
				where = " and formid in (" + mList_
						+ ") and entityimpcode in ('invItemMaterialName') ";
				//where = " and trim(search_display_value) in (" + materialList + ") ";
			}
		}
		//}
		return where;
	}

	@Override
	public Map<String, String> getUserInfoMap(String userId) {
		Map<String, String> tmpUserInfo = formDao.getFromInfoLookupAll("user", LookupType.ID, userId);
		Map<String, String> userInfo = new HashMap<String, String>();
		userInfo.put("USER_INFO_USER_ID", tmpUserInfo.get("USER_ID"));
		userInfo.put("USER_INFO_USERNAME", tmpUserInfo.get("NAME"));
		userInfo.put("USER_INFO_FIRSTNAME", tmpUserInfo.get("FIRSTNAME"));
		userInfo.put("USER_INFO_LASTNAME", tmpUserInfo.get("LASTNAME"));
		userInfo.put("USER_INFO_LABORATORY_ID", tmpUserInfo.get("LABORATORY_ID"));
		userInfo.put("USER_INFO_UNIT_ID", tmpUserInfo.get("UNIT_ID"));
		userInfo.put("USER_INFO_SITE_ID", tmpUserInfo.get("SITE_ID"));
		userInfo.put("USER_INFO_TEAMLEADER_ID", tmpUserInfo.get("TEAMLEADER_ID"));
		/*String sql = "select USER_ID as \"USER_INFO_USER_ID\","
				+ "USERNAME as \"USER_INFO_USERNAME\","
				+ "FIRSTNAME as \"USER_INFO_FIRSTNAME\","
				+ "LASTNAME as \"USER_INFO_LASTNAME\","
				+ "LABORATORY_ID as \"USER_INFO_LABORATORY_ID\","
				+ "UNIT_ID as \"USER_INFO_UNIT_ID\","
				+ "SITE_ID as \"USER_INFO_SITE_ID\","
				+ "TEAMLEADER_ID as \"USER_INFO_TEAMLEADER_ID\" "
				+ "from FG_S_USER_ALL_V t where t.user_id = " + userId + " and rownum < 1000 ";
		return generalDao.sqlToHashMap(sql);*/
		return userInfo;
	}

	//yk
	@Override
	public JSONObject onLevelSelectedChange(String struct, String formCode, String displayCatalog, String elementCode) {
		// TODO Auto-generated method stub

		List<Map<String, String>> sqlPoolMapList = formDao.getFromInfoLookupAllContainsVal("SysConfSQLCriteria",
				LookupType.NAME, "%." + (struct.equals("NA") ? displayCatalog : struct) + "." + formCode); //TODO improve % in next version			
		//String sql= "sselect * from fg_s_SysConfSQLCriteria_inf_v  t where   (t.NAME) like  ('%.Template.AnalytMethodSelect');

		JSONObject jsonToReturn = new JSONObject();
		JSONArray array = new JSONArray();
		for (int i = 0; i < sqlPoolMapList.size(); i++) {
			Map<String, String> sqlPoolMap = sqlPoolMapList.get(i);
			if (!sqlPoolMap.get("IGNORE").equals("1")) {
				JSONObject item = new JSONObject();
				item.put("SYSCONFSQLCRITERIANAME", sqlPoolMap.get("SYSCONFSQLCRITERIANAME"));
				item.put("ISDEFAULT", sqlPoolMap.get("ISDEFAULT"));

				array.put(item);
				item = null;
				sqlPoolMap = null;
			}
		}
		
//		String dtList = customerDTDisplayViewList(formCode, elementCode, struct, displayCatalog, true); //TODO kosta (BUT NOT IN USE YET)
		
		JSONArray colArray = new JSONArray();
		JSONObject colItem = new JSONObject();
		colArray.put(customerDTDefaultHiddenColumns(formCode, elementCode, struct)); // TODO KOSTA add this to the return and update the col array (using as the hidden value list)

		jsonToReturn.put("data", array);
		jsonToReturn.put("colArray", colArray);
		return jsonToReturn;
	}

	@Override
	public String checkRemove(String struct, String formId, String userId, String rowId) {
		//			int parameter=0;
		//Series - parameters table:
		if (struct.equals("ExpParameterRef")) {
			// Get the parent-current experiment series
			String parentid = generalDao
					.selectSingleString(String.format("select t.parentid " + "from fg_s_expparameterref_all_v t "
							+ "where t.EXPPARAMETERREF_ID=%1$s" + "and t.sessionid is null", formId));

			//if parameter is not saved- possible to remove - return ""
			if (parentid.equals("")) {
				return "";
			} else {
				//if there is an actual experiment for this series: 
				//impossible to remove the parameter - return message to user and not allow to do remove
				//else- possible to remove - return ""
				String result = generalDao.selectSingleString(String.format(
						"select count(*) " + "from fg_s_experiment_all_v t " + "where t.experimentseries_id=%1$s",
						parentid));
				//Return 1 if possible to remove , 0 if not possible.
				if (result.isEmpty() || result.equals("0")) {
					return "";
				}
				return "This parameter cannot be removed from the list as actual experiments created for this series.";
			}

		}
		//Maintenance - Users
		else if (struct.equals("User")) {
			// check for condition to remove user if needed
			String result = generalDao.selectSingleString("select count(*) from fg_s_user_pivot t where t.formid = '"
					+ formId + "' and lower(t.username) not in ('admin','system')");
			//Return 1 if possible to remove , 0 if not possible.
			if (!result.isEmpty() && result.equals("1")) {
				String update = formSaveDao.updateStructTableByFormId(
						"update fg_s_user_pivot t set t.deleted = 1 where t.formid = '" + formId + "'",
						"FG_S_USER_PIVOT", Arrays.asList("deleted"), formId);
				if (update != null && update.equals("1")) {
					return "";
				}
			}
			return "The User can not be deleted!";
		}
		//Series - formulants table:
		else if (struct.equals("FormulantRef")) {

		}
		//implement select from edit data table remove row
		else if (struct.equals("InstrumentSelect")){			
			return removeEditTableSharedRow(userId, struct, formId, rowId, "InstrumentSelect", "instrument_id", "instrumentselect_id");
		}else if(struct.equals("ColumnSelect")){
			return removeEditTableSharedRow(userId, struct, formId, rowId, "ColumnSelect", "column_id", "columnselect_id");
		}else if(struct.equals("SampleSelect")){
			return removeEditTableSharedRow(userId, struct, formId, rowId, "SampleSelect", "SampleTable", "sampleselect_id");
		}
	     else if(struct.equals("FunctionRuleRef")){
		return removeEditTableSharedRow(userId, struct, formId, rowId, "FunctionRuleRef", "functions", "FunctionRuleRef_id");
	   }
	     else if(struct.equals("MaterialComponent")){
	 		return removeEditTableSharedRow(userId, struct, formId, rowId, "MaterialComponent", "functions", "MaterialComponent_id");
	 	   }
		return null;

	}

	public String removeEditTableSharedRow(String userId, String formCode, String formId, String rowId, String selectFormCode, String csvColName, String selectFormIdColName)
	{
		String update = "";
		try{
			//using parent id to get sessionId
			String parentId = formDao.getFormParentId(formCode, formId);
			String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, parentId);

			String sql = "";
			String csvIds = generalDao.selectSingleStringNoException(
					String.format("select %1$s from fg_s_%2$s_v where %3$s= '%4$s' and sessionid='%5$s'"
							,csvColName, formCode, selectFormIdColName, formId, sessionId));	
			
			if (csvIds != null && !generalUtil.getNull(csvIds).isEmpty()){		
				List<String> ids = new ArrayList<String>(Arrays.asList(csvIds.split(",")));
				ids.remove(rowId.toString());				
				sql = String.format("update FG_S_%1$s_PIVOT set %2$s = '%3$s' where formId = '%4$s' and sessionid='%5$s' and active=1"
					, formCode, csvColName, generalUtil.listToCsv(ids), formId, sessionId);
			
				update = formSaveDao.updateSingleStringInfoNoTryCatch(sql);
			}
			if(update.equals("")){
				csvIds = generalDao.selectSingleStringNoException(
						String.format("select %1$s from fg_s_%2$s_v where %3$s= '%4$s' and sessionid is null"
								,csvColName, formCode, selectFormIdColName, formId));	
				//csvIds =  csvIds.replace(","+rowId,"").replace(rowId+",","").replace(rowId,"");
				if (csvIds != null && !generalUtil.getNull(csvIds).isEmpty()){
					List<String> ids = new ArrayList<String>(Arrays.asList(csvIds.split(",")));
					ids.remove(rowId.toString());
				
					String colList = generalDao.getTableColCsv("FG_S_" + formCode + "_PIVOT");						
					String valList = colList
							.replace("CHANGE_BY", userId)
							.replace(",TIMESTAMP", ",sysdate").replace("CREATION_DATE", "sysdate")
							.replace("CREATED_BY", userId).replace("SESSIONID", sessionId).replace(csvColName.toUpperCase(), "'"+generalUtil.listToCsv(ids) +"'");
		
					sql = String.format(
							"insert into FG_S_%1$s_PIVOT (%2$s) select %3$s from FG_S_%4$s_PIVOT t where formid = %5$s and sessionid is null",
							formCode, colList, valList, formCode, formId);
					
					update = formSaveDao.updateSingleStringInfoNoTryCatch(sql);
				}
			}
		}
		catch(Exception e){
			
		}
	
		if (update != null && update.equals("1")) {
			return "NA";
		}
		return "";
	}
	@Override
	/**
	 * return message in case confirm is needed else empty string
	 * Fill errorMessage in failure or if the row can not be deleted
	 */
	public String checkRemoveConfirm(String struct, String formId, String userId, String action,
			StringBuilder errorMessage) {
		String toReturn = "";
		try {
			if (generalUtil.getNull(action).equals("confirmRemoveFormulant")) {
				// Get the material_id
				String materialId = generalDao.selectSingleString(
						String.format("select t.INVITEMMATERIAL_ID " + "from fg_s_formulantref_all_v t "
								+ "where t.FORMULANTREF_ID=%1$s" + "and t.sessionid is null", formId));
				//check also session id- if null:user did not save the material and it possible to remove it
				if (materialId.equals("")) {
					return "";
				}
				//else:continue to check if there is an actual experiment to current series

				// Get the parent-current experiment series
				String parentid = generalDao.selectSingleString(String.format(
						"select t.parentid " + "from fg_s_formulantref_dt_v t " + "where t.FORMULANTREF_ID=%1$s",
						formId));

				//Check if the is an actual experiment to this series
				String result = generalDao.selectSingleString(String.format(
						"select count(*) " + "from fg_s_experiment_all_v t " + "where t.experimentseries_id=%1$s",
						parentid));

				//Return 0 if possible to remove.
				if (result.isEmpty() || result.equals("0")) {

					//Check if there is a planned experiment index that use this formulants
					String experimentIndex = generalDao
							.selectSingleString(String.format("select t.FORMULATIONPROPREF_ID "
									+ "from fg_i_series_indx_datasession_v t " + "where t.parentid=%1$s "
									+ "and t.invitemmaterial_id=%2$s " + "and fg_get_numeric(NVL(t.VALUE, '0'))>0"
									+ "and ROWNUM = 1" + "order by t.CALCIDENTIFIER desc", parentid, materialId));

					//there is no index using this formulant-possible to remove it
					if (experimentIndex.equals("")) {
						return "";
					} else {
						return "There are planned experiments using the removed material. Calculations are no longer relevant. Please edit material quantities in the planned experiments.";
					}

				} else {
					errorMessage.append(
							"This material cannot be removed from the list as actual experiments created for this series.");
				}
			} else if (generalUtil.getNull(action).equals("confirmRemoveInstrument")) {
				String isInstrumentInUseSelfTest = "";
				Map<String, String> instrumentRefData = generalDao.sqlToHashMap(
						"select INVITEMINSTRUMENT_ID,parentId from fg_s_InstrumentRef_pivot where formid='" + formId
								+ "'");
				String protocolType = generalDao
						.selectSingleStringNoException("select PROTOCOLTYPENAME from fg_S_step_all_v where formid='"
								+ instrumentRefData.get("PARENTID") + "'");
				if (protocolType.equals("Organic") || protocolType.equals("Formulation") || protocolType.equals("Continuous Process")) {
					isInstrumentInUseSelfTest = generalDao.selectSingleStringNoException("select 1 from dual "
							+ "where '" + instrumentRefData.get("INVITEMINSTRUMENT_ID") + "' in "
							+ "(select TO_CHAR(INSTRUMENT_ID) as INSTRUMENT_ID" + " from fg_s_resultref_v r"
							+ ",fg_s_selftest_all_v s " + "where r.parentid=s.formid" + " and s.step_id = '"
							+ instrumentRefData.get("PARENTID") + "'" + " union all"
							+ " select distinct TO_CHAR(INVITEMINSTRUMENT_ID)"
							+ " from fg_s_instrumentselect_all_v ins," + " fg_s_selftest_all_v s"
							+ " where ins.parentid = s.formid" + " and s.step_id ='" + instrumentRefData.get("PARENTID")
							+ "'" + " and ins.sessionid is null and nvl(ins.active,'1') ='1'" + ")");
				}
				if (!isInstrumentInUseSelfTest.isEmpty()) {
					errorMessage.append(
							"This instrument cannot be removed, since it is used in some of the corresponding self-tests");
				}
			} else if (generalUtil.getNull(action).equals("confirmRemoveManualRes")) {
				String result = generalDao.selectSingleStringNoException(
						"select  distinct m.result from fg_s_manualresultsref_v m,fg_s_experiment_v e"
						+ " where m.formid = '" + formId + "'"
								+ " and m.parentid = e.experiment_id"
								+ " and to_number(nvl(e.experimentversion,'1')) = '1' ");
				if (!generalUtil.getNull(result).isEmpty()) {
					errorMessage.append("The selected result cannot be removed since it is not empty");
				}
			}
		} catch (Exception e) {
			toReturn = "-1";
			errorMessage.append("CheckRemoveConfirmError");
			generalUtilLogger.logWriter(LevelType.ERROR, "DT table checkRemoveConfirm error! e=" + e,
					ActivitylogType.DataTable, formId, e, null);

		}
		return toReturn;
	}

	@Override
	public String dataTableAddRow(long stateKey, String formCode, String formId, String userId, String parentFormCode,
			String domId, Map<String, String> elementValueMap, int rowNumToAdd,String tableType) throws Exception {
		//general case. following code was added for operatinType table in the requestMain form
		String newformId = formSaveDao.getStructFormId(formCode);
		String insert = "";
		if(formCode.equalsIgnoreCase("Composition")){
			String rowType = "";
			if(tableType.equals("expComposition")||tableType.equals("recipeComposition")){
				String sqlrowType = "select distinct first_value(rowtype)over(partition by parentid order by to_number(formid) desc)\n"
					+ " from fg_s_composition_v where parentid = '"+formId+"'\n"
					+ generalUtilFormState.getWherePartForTmpData(formCode, formId);
				rowType = generalUtil.getEmpty(generalDao.selectSingleStringNoException(sqlrowType),"Active Ingredient");
			} else  if(tableType.equals("stepComposition")){//materials table in the step
				rowType = "Allowed Additive";
			} else if(tableType.equals("productComposition")){
				String stepNumber = elementValueMap.get("formNumberId");
				if(stepNumber.equals("01")){
					rowType = "Premix Material";
				} else {
					rowType = "Formulation";
				}
			}
			String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, formId);
			for(int i=0;i<rowNumToAdd;i++){
				String sql = "insert into FG_S_" + formCode + "_PIVOT "
						+ "(TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE,FORMCODE_ENTITY,CREATED_BY,CREATION_DATE,ROWTYPE,ORIGIN,TABLETYPE)"
						+ " values (sysdate,'" + userId + "'," + sessionId + ",'1'," + newformId + "," + formId + ",'"
						+ formCode + "','" + formCode + "','" + userId + "',sysdate,'"+rowType+"','"+elementValueMap.get("origin")+"','"+tableType+"')";
				insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_" + formCode + "_PIVOT", newformId);
				if(i+1<rowNumToAdd){
					newformId = formSaveDao.getStructFormId(formCode);
				}
			}
			if(tableType.equals("productComposition")){
				elementValueMap.put("parentId", formId);
				integrationCalc.doCalcComposition("MaterialTripletCalc", "calcWWpProduct", "", "0", "", elementValueMap, null, null, "Composition", newformId, userId, null);
			}
			
		}else if (formCode.toUpperCase().equals("ACTION")) { 
			List<String> newFormIdList = new ArrayList<>();
			boolean canNewByList = false;
			List<String> newAvailableList = Arrays
					.asList(commonFunc.getNewAvailableFormListById(stateKey, formId, "Action"));
			for (String newCode : newAvailableList) {
				if (newCode.equals("Action")) {
					canNewByList = true;
					break;
				}
			}
			
			if (!canNewByList) {//cannot create the current action because of permission/WF
				//throws an error message
				//					String stepFormCode = formDao.getFormCodeBySeqId(parentId);
				//					String wfList = generalUtil.getNull(
				//							generalUtilFormState.getFormParam(stateKey, stepFormCode).get("$P{STEPS_WF_LIST_INFO}"));
				String message = generalUtil.getSpringMessagesByKey("Warning,Creation of Action is not allowed",
						"");
				throw new Exception(message);
			}
			
			String experimentId = generalDao
					.selectSingleString("select experiment_id from fg_s_step_v where formid ='" + formId + "'");
			for(int i=0;i<rowNumToAdd;i++){
				newFormIdList.add(newformId);
				String sql = "insert into FG_S_ACTION_PIVOT "
						+ "(TIMESTAMP,CHANGE_BY,ACTIVE,FORMID,STEP_ID,FORMCODE,FORMCODE_ENTITY,CREATED_BY,CREATION_DATE,EXPERIMENT_ID,STARTDATE,ENDDATE,STARTTIME)"
						+ " values (sysdate,'" + userId + "','1'," + newformId + "," + formId + ",'" + formCode + "','"
						+ formCode + "','" + userId + "',sysdate,'" + experimentId + "',to_char( sysdate, '"
						+ generalUtil.getConversionDateFormat() + "'),to_char( sysdate, '"
						+ generalUtil.getConversionDateFormat() + "'),to_char( sysdate, 'HH24:MI' ))";
				insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_ACTION_PIVOT", newformId);
				formSaveElementDao.addMonitorParam(newformId, "Action");//insertParamMonitoring
				if(i+1<rowNumToAdd){
					newformId = formSaveDao.getStructFormId(formCode);
				}
			}
			//The following code is for the case of adding row with no need to reload the table- removed because it seems to take a longer time
			/*JSONArray jsonArrayOfData = null;JSONObject jsonRows=null;
			try 
			{
				String sql = "select *"
						+ " from fg_s_action_dten_v"
						+ " where action_id in ("+generalUtil.listToCsv(newFormIdList)+")";
				jsonRows = generalDao.getJSONObjectOfDateTable(sql,"","10000",null,new String());
			} 
			catch (Exception e) {
				generalUtilLogger.logWrite(e);
				e.printStackTrace();
			}
			return jsonRows.get("data").toString();//jsonArrayOfData.toString();*/
		} else if (formCode.toUpperCase().equals("MATERIALREF")) {
			JSONArray jsonArrayOfData = null;
			String sql = "";
			String experimentFormCode = "";
			String parent_id = generalUtilFormState.getFormParam(stateKey, "Step", "$P{EXPERIMENT_ID}");
			experimentFormCode = formDao.getFormCodeEntityBySeqId("", parent_id);
			//String currStepNumber = generalUtil.getEmpty(formDao.getFromInfoLookup("Step", LookupType.ID, formId, "FORMNUMBERID"), generalUtilFormState.getFormParam(stateKey, "Step", "$P{STEPSEQUENCE}")); //generalUtilFormState.getFormParam(stateKey, "Step", "$P{STEPSEQUENCE}");//generalUtil.getEmpty(formDao.getFromInfoLookup("Step", LookupType.ID, formId, "FORMNUMBERID"), "");
			String currStatusName = generalUtil.getEmpty(formDao.getFromInfoLookup("Step", LookupType.ID, formId, "STATUSNAME"), "Planned");
			String stepId = "";
			//String productCount = "";
			 String isNew = generalUtilFormState.getFormParam(stateKey, "Step", "$P{ISNEW}");
			 if(!isNew.equals("1")){
				 stepId = formId;
			 }
			if (domId.equals("reactants")) {
				if (currStatusName.equals("Planned")) {
					sql = "select * from fg_s_materialref_dten_react_v"
							+ " where 1=1"
							+ (!generalUtil.getNull(parent_id).isEmpty() && !parent_id.equals("-1")?" and experiment_id = '"+parent_id+"'":"")
							+ " and parentId = '"+generalUtil.getEmpty(stepId, "-1")+"'";
				} else {
					sql = "select * from fg_s_matref_dten_reac_act_v"
							+ " where 1=1"
							+ (!generalUtil.getNull(parent_id).isEmpty() && !parent_id.equals("-1")?" and experiment_id = '"+parent_id+"'":"")
							+ " and parentId = '"+generalUtil.getEmpty(stepId, "-1")+"'";
				}
			} else if (domId.equals("solvents")) {
				//String experimentId = generalDao.selectSingleStringNoException("select experiment_id from fg_s_step_v where formid ='" + formId + "'");
				if (currStatusName.equals("Planned")) {
					sql = "select * from fg_s_materialref_dten_solv_v"// t where t.experiment_id = '" + generalUtil.getEmpty(experimentId, "-1") + "' and t.parentid = '"+generalUtil.getEmpty(stepId, "-1")+"'";
							+ " where 1=1"
							+ (!generalUtil.getNull(parent_id).isEmpty() && !parent_id.equals("-1")?" and experiment_id = '"+parent_id+"'":"")
							+ " and parentId = '"+generalUtil.getEmpty(stepId, "-1")+"'";		
				} else {
					sql = "select * from fg_s_matref_dten_solv_act_v t"// where t.experiment_id = '" + generalUtil.getEmpty(experimentId, "-1") + "' and t.parentid = '"+generalUtil.getEmpty(stepId, "-1")+"'";
							+ " where 1=1"
							+ (!generalUtil.getNull(parent_id).isEmpty() && !parent_id.equals("-1")?" and experiment_id = '"+parent_id+"'":"")
							+ " and parentId = '"+generalUtil.getEmpty(stepId, "-1")+"'";
				}
			} else if (domId.equals("products")) {
				//sql = "select * from fg_s_materialref_dten_prod_v";
				sql = "select * from fg_s_materialref_dten_prod_v where rownum<=1";
				
				//String parent_id = generalUtilFormState.getFormParam(stateKey, "Step", "$P{EXPERIMENT_ID}");
				if(!generalUtil.getNull(parent_id).isEmpty() && !parent_id.equals("-1")){
					//experimentFormCode = formDao.getFormCodeEntityBySeqId("", parent_id);
					
					sql = "select * from fg_s_materialref_dten_prod_v where experiment_id = '" + parent_id +"' and parentid = '"+generalUtil.getEmpty(stepId, "-1")+"'";
					}
			}

			try 
			{
				List<Map<String, Object>> rows = generalDao.getListOfMapsForDataTableBySql(sql);
				
				if(!experimentFormCode.equals("")) {
					rows = generalDao.removeColumnsBeforeDisplay(rows,experimentFormCode);
				}
				List<List<String>> ListOfList = new ArrayList<List<String>>();
				List<String> rowList;
				for (Map<String, Object> row : rows) {
					rowList = new ArrayList<String>();
					for (Entry<String, Object> entry : row.entrySet()) {
						String value = (entry.getValue() == null) ? "" : entry.getValue().toString();
						//rowList.add(value.replaceAll("@step number@", currStepNumber));
						rowList.add(value);
					}
					ListOfList.add(rowList);
				}
				jsonArrayOfData = new JSONArray(ListOfList);
				
//				List<Map<String, String>> rows = generalDao.getListOfMapsWithClob(sql);
//
//				List<List<String>> ListOfList = new ArrayList<List<String>>();
//				List<String> rowList;
//				for (Map<String, String> row : rows) {
//					rowList = new ArrayList<String>();
//					for (Entry<String, String> entry : row.entrySet()) {
//						String value = (entry.getValue() == null) ? "" : entry.getValue().toString();
//						rowList.add(value);
//					}
//					ListOfList.add(rowList);
//				}
//				jsonArrayOfData = new JSONArray(ListOfList);
			} 
			catch (Exception e) {
				generalUtilLogger.logWrite(e);
				e.printStackTrace();
			}
			return jsonArrayOfData.toString();
		} else if (formCode.equals("OperationType")) {
			String experimentView_id = elementValueMap.get("REQUESTTYPE_ID");
			String requesttypename = formDao.getFromInfoLookup("REQUESTTYPE", LookupType.ID, experimentView_id, "name");
			String procedure_id = "";
			if (requesttypename.equals("Pilot")) {
				procedure_id = elementValueMap.get("ORIGIN_TEMPLATE_ID");
			}
			String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, formId);
			String ids = generalDao.getCSVBySqlNoException(
					String.format("select %1$s from fg_s_%2$s_v where parentid ='%3$s' and sessionid ='%4$s'",
							"OPERATIONTYPENAME", formCode, formId, sessionId),false);
			if(!ids.contains("-1")){
				String sql = "insert into FG_S_" + formCode + "_PIVOT "
						+ "(TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE,FORMCODE_ENTITY,CREATED_BY,CREATION_DATE,PROCEDURE_ID,OPERATIONTYPENAME)"
						+ " values (sysdate,'" + userId + "'," + sessionId + ",'1'," + newformId + "," + formId + ",'"
						+ formCode + "','" + formCode + "','" + userId + "',sysdate,'" + procedure_id + "','-1')";
				insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_" + formCode + "_PIVOT", newformId);
			}
			
		}
		else if (formCode.equals("MaterialsPeaks")) {			
			String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, formId);
			String ids = generalDao.getCSVBySqlNoException(
					String.format("select %1$s from fg_s_%2$s_v where parentid ='%3$s' and sessionid ='%4$s'",
							"SAMPLE_ID", formCode, formId, sessionId),false);
			//if(!ids.contains("-1"))fix bug 7830
			//{
				String defaultSample = generalDao.selectSingleStringNoException("select SAMPLEID from FG_S_SAMPLEDATAREF_V where parentid ='"+formId+"' and parentid in(\r\n" + 
						"select parentid from FG_S_SAMPLEDATAREF_V t group by parentid having count(t.SAMPLEID)<2)");
				if(generalUtil.getNull(defaultSample).isEmpty()){
					defaultSample = "-1";
				}
				String sql = "insert into FG_S_" + formCode + "_PIVOT "
						+ "(TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE,FORMCODE_ENTITY,CREATED_BY,CREATION_DATE,SAMPLE_ID)"
						+ " values (sysdate,'" + userId + "'," + sessionId + ",'1'," + newformId + "," + formId + ",'"
						+ formCode + "','" + formCode + "','" + userId + "',sysdate,'"+defaultSample+"')";
				insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_" + formCode + "_PIVOT", newformId);
			//}
			
		}
		else if (formCode.equalsIgnoreCase("paramref")) {			
			String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, formId);
			String wherePart = "PARAMETER_ID is null and CRITERIA_ID is null and VAL1 is null and VAL2 is null and UOM_ID is null";
			String formEmptyId = generalDao.getCSVBySqlNoException(
					String.format("select %1$s from fg_s_%2$s_v where parentid ='%3$s' and sessionid ='%4$s' and %5$s",
							"paramref_id", formCode, formId, sessionId, wherePart),false);
			if(formEmptyId == null || formEmptyId.isEmpty())
			{
				String sql = "insert into FG_S_" + formCode + "_PIVOT "
						+ "(TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE,FORMCODE_ENTITY,CREATED_BY,CREATION_DATE)"
						+ " values (sysdate,'" + userId + "'," + sessionId + ",'1'," + newformId + "," + formId + ",'"
						+ formCode + "','" + formCode + "','" + userId + "',sysdate)";
				insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_" + formCode + "_PIVOT", newformId);
			}
			
		}else if (formCode.equalsIgnoreCase("FunctionRuleRef")) {			
			String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, formId);
			String wherePart = "CONDITION is null and VALUE1 is null \r\n" + 
					"and VALUE2 is null \r\n" + 
					"and SIGN is null \r\n" + 
					"and ACTIVITY is null";
			String formEmptyId = generalDao.getCSVBySqlNoException(
					String.format("select %1$s from fg_s_%2$s_v where parentid ='%3$s' and sessionid ='%4$s' and %5$s",
							"functionRuleRef_id", formCode, formId, sessionId,wherePart),false);
			if(formEmptyId == null || formEmptyId.isEmpty()) // this condition is to avoid insert empty row if there is one for the parentid
			{
				String sql = "insert into FG_S_" + formCode + "_PIVOT "
						+ "(TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE,FORMCODE_ENTITY,CREATED_BY,CREATION_DATE)"
						+ " values (sysdate,'" + userId + "'," + sessionId + ",'1'," + newformId + "," + formId + ",'"
						+ formCode + "','" + formCode + "','" + userId + "',sysdate)";
				insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_" + formCode + "_PIVOT", newformId);
			}
			
		
	}else if (formCode.equalsIgnoreCase("MaterialComponent")) {			
		String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, formId);
		String wherePart = "APPROVALDATE is null and MATERIAL_ID is null \r\n" + 
				"and MATERIALCOMPONENTNAME is null \r\n" + 
				"and CONCENTRATION is null \r\n" + 
				"and FUNCTION_ID is null ";
				
		String formEmptyId = generalDao.getCSVBySqlNoException(
				String.format("select %1$s from fg_s_%2$s_v where parentid ='%3$s' and sessionid ='%4$s' and %5$s",
						"MaterialComponent_id", formCode, formId, sessionId,wherePart),false);
		//if(formEmptyId == null || formEmptyId.isEmpty()) // this condition is to avoid insert empty row if there is one for the parentid
		//{
			String sql = "insert into FG_S_" + formCode + "_PIVOT "
					+ "(TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE,FORMCODE_ENTITY,CREATED_BY,CREATION_DATE)"
					+ " values (sysdate,'" + userId + "'," + sessionId + ",'1'," + newformId + "," + formId + ",'"
					+ formCode + "','" + formCode + "','" + userId + "',sysdate)";
			insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_" + formCode + "_PIVOT", newformId);
		//}
		
	}
		//implement select from edit data table add row
		else if (formCode.equals("InstrumentSelect")){
			editTableSharedAddRow(stateKey, formCode, formId, userId, parentFormCode,
					domId, elementValueMap, "instrumentselect_id", "instrument_id", newformId, true);
		}else if (formCode.equals("ColumnSelect")){
			editTableSharedAddRow(stateKey, formCode, formId, userId, parentFormCode,
					domId, elementValueMap, "ColumnSelect_id", "column_id", newformId, true);
		}else if (formCode.equals("SampleSelect")){
			editTableSharedAddRow(stateKey, formCode, formId, userId, parentFormCode,
					domId, elementValueMap, "SampleSelect_id", "SampleTable", newformId, true);
		}else if (formCode.equals("AnalytMethodSelect")){
			editTableSharedAddRow(stateKey, formCode, formId, userId, parentFormCode,
					domId, elementValueMap, "AnalytMethodSelect_id", "AnalytMathodTable", newformId, false);
		}
		else if(formCode.equalsIgnoreCase("ExpRunPlanning")) {
			String expRunStarted = generalDao.selectSingleStringNoException("select decode(count(*),0,0,1) from FG_S_EXPRUNPLANNING_all_v where experimentID = '"+formId+"' and runstatusname <> 'Planned'");
			int nextNumber = 1;
			List<String> nums = generalDao.getListOfStringBySql("select distinct RUNNUMBER from FG_S_EXPRUNPLANNING_PIVOT where EXPERIMENTID='"+formId+"' order by to_number(RUNNUMBER)");			
			for(int i=0;i<nums.size();i++)
			{
				int curNum = Integer.parseInt(nums.get(i));
				if(curNum == nextNumber) {
					nextNumber ++;
				}
				else {
					break;
				}
			}
			int prevNumber = nextNumber - 1;
			String runStatusId = formDao.getFromInfoLookup("RunStatus", LookupType.NAME, "Planned", "id");
			String sql = "insert into FG_S_EXPRUNPLANNING_PIVOT "
					+ "(FORMID, TIMESTAMP, CREATION_DATE, CHANGE_BY, CREATED_BY, ACTIVE, FORMCODE_ENTITY, FORMCODE, MATERIALREFID, PARAMETERREFID, STEPID, EXPERIMENTID, RUNNUMBER, MATERIALREFEQUIVALENT, RUNSTATUSID,"
					+ " INVITEMMATERIALID, PARAMETERID, LIMITINGAGENT, MATERIALRATETYPE, MATERIALRATEUOM, PARAMETERUOM, RETENTIONTIMEUOM, APSS) \n"
					+ " select distinct "+newformId+", sysdate, sysdate, '" + userId + "', '" + userId + "', '1' ,'"+formCode+"', '"+formCode+"' \n"
					+ " 		,t2.MATERIALREF_ID, t2.PARAMREF_ID, t2.STEP_ID, " + formId + ", "+String.valueOf(nextNumber)
					+ "			,null as MATERIALREFEQUIVALENT \n"
					+ " 		, "+runStatusId+" \n"
					+ "			,t2.INVITEMMATERIAL_ID, t2.PARAMETER_ID, t2.LIMITINGAGENT \n"
					+ "			,t2.MATERIALRATETYPE \n"		
					+ "			,t2.MATERIALRATEUOM \n"
					+ "         ,t2.PARAMETERUOM \n"
					+ " 		,t2.RETENTIONTIMEUOM \n"
					+ "         ,'1' \n"
					+ " from ( \n" 
					+ " select s.STEP_ID,  s.EXPERIMENT_ID ,t.MATERIALREF_ID, null as PARAMREF_ID \n"
					//+ " 	  ,decode(nvl(t.LIMITINGAGENT,0),0,null,1) as MATERIALREFEQUIVALENT \n" // equivalent of limiting agent material equals "1" by default
					+ " 	  ,t.INVITEMMATERIAL_ID, null as PARAMETER_ID, nvl(t.LIMITINGAGENT,0) as LIMITINGAGENT \n"
					+ "		  ,nvl(r.MATERIALRATETYPE,'volume') as MATERIALRATETYPE \n"		
					+ "		  ,nvl(r.MATERIALRATEUOM, decode(nvl(r.rateTypeMaintName,'rateVolume'),'rateVolume',fg_get_uom_by_uomtype(nvl(r.rateTypeMaintName,'rateVolume'),'ml/min'),fg_get_uom_by_uomtype(nvl(r.rateTypeMaintName,'rateVolume')))) as MATERIALRATEUOM \n"
					+ "		  ,null as PARAMETERUOM \n"
					+ " 	  ,nvl(r.RETENTIONTIMEUOM, fg_get_Uom_by_uomtype('time')) as RETENTIONTIMEUOM \n"
					+ " from fg_s_step_all_v s, fg_s_materialref_v t \n"
					// select data from previous run
					+ "	,(select run.MATERIALREFID, run.MATERIALRATETYPE, run.MATERIALRATEUOM, run.rateTypeMaintName, run.RETENTIONTIMEUOM from fg_s_exprunplanning_all_v run \n"
					+ "          where run.RUNNUMBER = "+String.valueOf(prevNumber)+" and run.EXPERIMENTID = " + formId + " and run.MATERIALREFID is not null)r  \n"
					+ " where t.PARENTID = s.STEP_ID \n"
					+ " and t.materialref_id = r.MATERIALREFID(+) \n"
					+ " and t.ACTIVE = 1 \n"
					+ " and s.ACTIVE = 1 \n"
					+ " and t.sessionId is null \n"
					+ " and lower(t.TABLETYPE) in ('reactant','solvent') \n"
					+ " and lower(nvl(s.PREPARATION_RUN,'na')) = lower('run') \n"
					+"  and s.sourceRunSteps = 1 \n"
					+ " and s.EXPERIMENT_ID = " + formId + " \n"
					+ " union all \n"
					+ " select s1.STEP_ID, s1.EXPERIMENT_ID, null as MATERIALREF_ID, t1.PARAMREF_ID \n"
					//+ "  		,null as MATERIALREFEQUIVALENT \n"
					+ " 		,null as INVITEMMATERIAL_ID, t1.PARAMETER_ID, null as LIMITINGAGENT, null as MATERIALRATETYPE, null as MATERIALRATEUOM \n"
					+ "			,nvl(r.PARAMETERUOM,fg_get_Uom_by_uomtype(t1.UOMTYPENAME)) as PARAMETERUOM \n"
					+ " 		,nvl(r.RETENTIONTIMEUOM, fg_get_Uom_by_uomtype('time')) as RETENTIONTIMEUOM \n"
					+ " from fg_s_step_all_v s1, fg_s_paramref_all_v t1 \n"
					// select data from previous run
					+ " ,(select run.PARAMETERREFID, run.PARAMETERUOM, run.RETENTIONTIMEUOM from fg_s_exprunplanning_all_v run \n"
					+ "    where run.RUNNUMBER = "+String.valueOf(prevNumber)+" and run.EXPERIMENTID = " + formId + " and run.PARAMETERREFID is not null)r \n"
					+ " where t1.PARENTID = s1.STEP_ID \n"
					+ " and t1.PARAMREF_ID = r.PARAMETERREFID(+)\n"//+(expRunStarted.equals("0")?"(+)":"" )+"\n"
					+ " and t1.ACTIVE = 1 \n"
					+ " and s1.ACTIVE = 1 \n"
					+ " and t1.sessionId is null \n"
					+ " and lower(nvl(s1.PREPARATION_RUN,'na')) = lower('run') \n"
					+"  and s1.sourceRunSteps = 1 \n"
					+ " and s1.EXPERIMENT_ID = " + formId + " "
					+ " )t2 ";
			insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_EXPRUNPLANNING_PIVOT", newformId);
			return String.valueOf(!insert.equals("0")?nextNumber:"0");
		}
		
		  else if(formCode.equalsIgnoreCase("InstrumentRef")) {
			  String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, formId);
				String wherePart = " INVITEMINSTRUMENT_ID is null ";
						
				String formEmptyId = generalDao.getCSVBySqlNoException(
						String.format("select %1$s from fg_s_%2$s_v where parentid ='%3$s' and sessionid ='%4$s' and %5$s",
								"INSTRUMENTREF_ID", formCode, formId, sessionId,wherePart),false);
				if(formEmptyId == null || formEmptyId.isEmpty()) // this condition is to avoid insert empty row if there is one for the parentid
				{
					String sql = "insert into FG_S_" + formCode + "_PIVOT "
							+ "(TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE,FORMCODE_ENTITY,CREATED_BY,CREATION_DATE)"
							+ " values (sysdate,'" + userId + "'," + sessionId + ",'1'," + newformId + "," + formId + ",'"
							+ formCode + "','" + formCode + "','" + userId + "',sysdate)";
					insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_" + formCode + "_PIVOT", newformId);
				}
		  }
		  else if(formCode.equalsIgnoreCase("EquipmentRef")) {
			  String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, formId);
				String wherePart = " EQUIPMENTREFNAME is null and fg_get_RichText_display(description) is null ";
						
				String formEmptyId = generalDao.getCSVBySqlNoException(
						String.format("select %1$s from fg_s_%2$s_v where parentid ='%3$s' and sessionid ='%4$s' and %5$s",
								"EquipmentRef_id", formCode, formId, sessionId,wherePart),false);
				if(formEmptyId == null || formEmptyId.isEmpty()) // this condition is to avoid insert empty row if there is one for the parentid
				{
					String sql = "insert into FG_S_" + formCode + "_PIVOT "
							+ "(TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE,FORMCODE_ENTITY,CREATED_BY,CREATION_DATE)"
							+ " values (sysdate,'" + userId + "'," + sessionId + ",'1'," + newformId + "," + formId + ",'"
							+ formCode + "','" + formCode + "','" + userId + "',sysdate)";
					insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_" + formCode + "_PIVOT", newformId);
				}
		  }
		  else if(formCode.equals("Component")){
				String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, formId);
				String sql = "insert into FG_S_" + formCode + "_PIVOT "
						+ "(TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE,FORMCODE_ENTITY,CREATED_BY,CREATION_DATE,OUM_ID,coefficient,numOfStandardRows)"
						+ " values (sysdate,'" + userId + "'," + sessionId + ",'1'," + newformId + "," + formId + ",'"
						+ formCode + "','" + formCode + "','" + userId + "',sysdate,fg_get_Uom_by_uomtype('time','min'),'1','1')";
				insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_" + formCode + "_PIVOT", newformId);
			}
		  else if(formCode.equalsIgnoreCase("ReportFilterRef")){
				String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, formId);
//				String nameIdasParentId = generalUtilFormState.getFormParam(stateKey, "ExperimentReport", "$P{NAMEID}");
			    String nameIdasParentId = "-1";
			    String sql = "insert into FG_S_" + formCode + "_PIVOT "
						+ "(TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE,FORMCODE_ENTITY,CREATED_BY,CREATION_DATE,ROWSTATEKEY)"
						+ " values (sysdate,'" + userId + "'," + sessionId + ",'1'," + newformId + "," + nameIdasParentId + ",'"
						+ formCode + "','" + formCode + "','" + userId + "',sysdate,'" + stateKey + "')";
				insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_" + formCode + "_PIVOT", newformId);
			}
		else {
			String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, formId);
			String sql = "insert into FG_S_" + formCode + "_PIVOT "
					+ "(TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE,FORMCODE_ENTITY,CREATED_BY,CREATION_DATE)"
					+ " values (sysdate,'" + userId + "'," + sessionId + ",'1'," + newformId + "," + formId + ",'"
					+ formCode + "','" + formCode + "','" + userId + "',sysdate)";
			insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_" + formCode + "_PIVOT", newformId);
		}
		return insert;
	}

	private String editTableSharedAddRow(long stateKey, String formCode, String formId, String userId, String parentFormCode,
			String domId, Map<String, String> elementValueMap, String selectFormIdColName, String csvColName, String newformId, boolean isShared)
	{
		String update = "";
		
		String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, formId);
		String wherePart = "sessionid='"+sessionId+"'";
		String selectFormId= generalDao.selectSingleStringNoException(
				String.format("select %1$s from fg_s_%2$s_v where parentid ='%3$s' and %4$s and active='1'", 
						selectFormIdColName,formCode, formId, wherePart));
		
		String csvIds = "";
		String sql = "";
		String csvIdsVal = "";
		
		if(!generalUtil.getNull(selectFormId).isEmpty()){	
			csvIds = generalDao.selectSingleStringNoException(
					String.format("select %1$s from fg_s_%2$s_v where parentid ='%3$s' and %4$s = '%5$s' and %6$s",
							csvColName, formCode, formId, selectFormIdColName,selectFormId, wherePart));
			if (csvIds != null && !generalUtil.getNull(csvIds).isEmpty()){
				csvIdsVal = (csvIds.contains("-1"))?csvIds:csvIds+",-1";
			}else{
				csvIdsVal = "-1";
			}			
			sql = String.format("update FG_S_%1$s_PIVOT set %2$s = '%3$s' where formId = '%4$s'" + " and %5$s and "
					+ "parentid ='%6$s' and active=1",
					formCode, csvColName, csvIdsVal, selectFormId, wherePart,formId);
			
			update =formSaveDao.updateSingleStringInfoNoTryCatch(sql);	
		}
		else if(generalUtil.getNull(selectFormId).isEmpty()){
			
			selectFormId= generalDao.selectSingleStringNoException(
					String.format("select %1$s from fg_s_%2$s_v where parentid ='%3$s' and sessionid is null", 
							selectFormIdColName,formCode, formId));
			
			String colList = generalDao.getTableColCsv("FG_S_" + formCode + "_PIVOT");						
			String valList = colList
					.replace("CHANGE_BY", userId)
					.replace(",TIMESTAMP", ",sysdate").replace("CREATION_DATE", "sysdate")
					.replace("CREATED_BY", userId).replace("SESSIONID", sessionId).replace("ACTIVE","1");
			
			if(!generalUtil.getNull(selectFormId).isEmpty()){					

				csvIds = generalDao.selectSingleStringNoException(
						String.format("select %1$s from fg_s_%2$s_v where parentid ='%3$s' and %4$s = '%5$s' and sessionid is null and active ='1'",
								csvColName, formCode, formId, selectFormIdColName,selectFormId));
				if(isShared)				
					csvIdsVal = (generalUtil.getNull(csvIds).equals(""))? "-1":(csvIds.contains("-1"))?csvIds:csvIds+",-1";
				else
					csvIdsVal = (generalUtil.getNull(csvIds).equals(""))? "-1":(csvIds.contains("-1"))?csvIds:"-1";
				valList = valList.replace(csvColName.toUpperCase(),"'"+csvIdsVal+"'");
				sql = String.format(
						"insert into FG_S_%1$s_PIVOT (%2$s) select %3$s from FG_S_%4$s_PIVOT t where formid = %5$s and sessionid is null ",
						formCode, colList, valList, formCode, selectFormId);
				
			}else{
				selectFormId = newformId;
				valList = valList.replace("FORMID", selectFormId).replace("PARENTID", formId).replace(csvColName.toUpperCase(), "'-1'")
						.replace("FORMCODE_ENTITY", "'"+formCode+"'").replace("FORMCODE", "'"+formCode+"'")
						.replace(formCode.toUpperCase()+"NAME", "NULL").replace("CLONEID","NULL").replace("TEMPLATEFLAG", "NULL")
						.replace("DISABLED", "NULL").replace("TEMPLATETYPE", "NULL");
				sql = String.format(
						"insert into FG_S_%1$s_PIVOT (%2$s) values (%3$s)",
						formCode, colList, valList);
			}
			update = formSaveDao.updateSingleStringInfoNoTryCatch(sql);
		} 
		return update;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String onChangeDataTableCell(long stateKey, String parentFormCode, String formId, String formCode,
			String onChangeFormId, String userId, String onChangeColumnName, String onChangeColumnVal, String saveType,
			String formNumberId, String oldVal) throws Exception {
		String toReturn = "";
		String dbTransactionId = generalUtil.getDBTransaction();
		try {
			toReturn = onChangeDataTableCellCore(stateKey, parentFormCode, formId, formCode, onChangeFormId,
					userId, onChangeColumnName, onChangeColumnVal, saveType, formNumberId, oldVal);
			// generalTask.postDBTransactionHandler(dbTransactionId); // only from job
		} catch (Throwable e) {
			generalTaskDao.onTransactionFailure(formCode, formId, dbTransactionId);
			generalUtilLogger.logWriter(LevelType.WARN,
					"WARN - error in onChangeDataTableCell. Additional info: parentFormCode=" + parentFormCode + ", formId="
							+ formId + ", formCode=" + formCode + ", onChangeColumnName=" + onChangeColumnName
							+ ", onChangeColumnVal=" + onChangeColumnVal + ", formNumberId=" + formNumberId
							+ ", oldVal=" + oldVal,
					ActivitylogType.SaveException, formId, e, userId);
			throw new Exception(e.getMessage());
		}
		return toReturn;
	}

	private String onChangeDataTableCellCore(long stateKey, String parentFormCode, String formId, String formCode,
			String onChangeFormId, String userId, String onChangeColumnName, String onChangeColumnVal, String saveType,
			String formNumberId, String oldVal) throws Exception {
		String update = "";
		//**** Switch by parentFormCode....
		//*****composition in the recipe
		if((parentFormCode.equals("RecipeFormulation")||parentFormCode.equals("ExperimentFor")||parentFormCode.equals("StepMinFr")  )
				&& (onChangeColumnName.equals("invitemmaterial_id")||onChangeColumnName.equals("batch_id")
				||formCode.equalsIgnoreCase("Composition"))){
			formCode = "Composition";//TODO:a workaround for a change that should be done on the attribute formCode in the json-now refers to both "displayAsLink" and"autoSave"-
									//should separate to another attribute such as  linkFormCode or saveFormCode
			onChangeEditTableCellCore(formCode, formId, saveType, onChangeColumnName, onChangeColumnVal,
					onChangeFormId, userId);
		}
		//**** ExperimentFor
		if ((parentFormCode.equals("ExperimentFor") || parentFormCode.equals("Experiment") || parentFormCode.equals("ExperimentCP"))
				&& formCode.equals("Sample")) {
			String sql = "update FG_S_" + formCode + "_PIVOT set " + onChangeColumnName + "='" + onChangeColumnVal + "'"
					+ " where FORMID = '" + formNumberId + "'";
			update = formSaveDao.updateStructTableByFormId(sql, "FG_S_" + formCode + "_PIVOT",
					Arrays.asList(onChangeColumnName), formNumberId);
		}else if (parentFormCode.equals("ExperimentFor") && formCode.equalsIgnoreCase("equipmentref")) {
			try {
				if (onChangeColumnName.equals("description")){
					JSONObject json = new JSONObject();
					json.put("value", onChangeColumnVal);
					onChangeColumnVal = formSaveElementDao.saveRichText(formCode, new
					  DataBean(onChangeColumnName, json.toString(), BeanType.CLOB, ""), true);
					 
				}
				update = onChangeEditTableCellCore(formCode, formId, saveType, onChangeColumnName, onChangeColumnVal,
						onChangeFormId, userId);
			} catch (Exception e) {
				generalUtilLogger.logWrite(e);
				return "-3";
			}
		}else if ((parentFormCode.equals("ExperimentFor")||parentFormCode.equals("ExperimentAn")) && onChangeColumnName.equals("inviteminstrument_id")) {
			try {
				formCode = "InstrumentRef";//TODO:a workaround for a change that should be done on the attribute formCode in the json-now refers to both "displayAsLink" and"autoSave"-
				//should separate to another attribute such as  linkFormCode or saveFormCode
				
				update = onChangeEditTableCellCore(formCode, formId, saveType, onChangeColumnName, onChangeColumnVal,
						onChangeFormId, userId);
			} catch (Exception e) {
				generalUtilLogger.logWrite(e);
				return "-3";
			}
		}//**** RequestMain
		 else if (parentFormCode.equals("RequestMain")) {
			String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, formId);
			if (formCode.equals("OperationType")) {
				if (onChangeColumnName.equals("OperationTypeName")) {
					String sql = "update FG_S_" + formCode + "_PIVOT " + " set operationTypeName = '"
							+ onChangeColumnVal + "'" + " where formId = '" + onChangeFormId + "'" + " and sessionid='"
							+ sessionId + "'" + " and active=1";
					update = formSaveDao.updateStructTableByFormId(sql, "FG_S_" + formCode + "_PIVOT",
							Arrays.asList("operationTypeName"), onChangeFormId);
				}
			}
		//**** Request
		} else if (parentFormCode.equals("Request")) {
			if (formCode.equals("Document") || formCode.equals("MaterialsPeaks")
					|| formCode.toUpperCase().equals("SAMPLEDATAREF")) {
				onChangeEditTableCellCore(formCode, formId, saveType, onChangeColumnName, onChangeColumnVal,
						onChangeFormId, userId);
			} else if (formCode.equals("OperationType")) {
				try {
					onChangeEditTableCellCore(formCode, formId, saveType, onChangeColumnName, onChangeColumnVal,
							onChangeFormId, userId);
				} catch (Exception e) {
					generalUtilLogger.logWrite(e);
					return "-3";
				}
			}
		//****SelfTest
		} else if (parentFormCode.equals("SelfTest")) {
			if (formCode.equals("Document")) {
				try {
					onChangeEditTableCellCore(formCode, formId, saveType, onChangeColumnName, onChangeColumnVal,
							onChangeFormId, userId);
				} catch (Exception e) {
					generalUtilLogger.logWrite(e);
					return "-3";
				}
			} else if (onChangeColumnName.equals("inviteminstrument_id")) { // implement select from edit data table on change row
				try {
					onChangeEditTableSharedCellCore(stateKey, parentFormCode, formId, formCode, onChangeFormId, userId,
							onChangeColumnName, onChangeColumnVal, saveType, formNumberId, oldVal,
							"instrumentselect_id", "instrument_id", "InstrumentSelect");
				} catch (Exception e) {
					generalUtilLogger.logWrite(e);
					return "-3";
				}
			} else if (onChangeColumnName.equals("invitemcolumn_id")) {
				try {
					onChangeEditTableSharedCellCore(stateKey, parentFormCode, formId, formCode, onChangeFormId, userId,
							onChangeColumnName, onChangeColumnVal, saveType, formNumberId, oldVal, "columnselect_id",
							"column_id", "ColumnSelect");
				} catch (Exception e) {
					generalUtilLogger.logWrite(e);
					return "-3";
				}
			} else if (onChangeColumnName.equals("SAMPLE_ID")) {
				try {
					onChangeEditTableSharedCellCore(stateKey, parentFormCode, formId, formCode, onChangeFormId, userId,
							onChangeColumnName, onChangeColumnVal, saveType, formNumberId, oldVal, "sampleselect_id",
							"SampleTable", "SampleSelect");
				} catch (Exception e) {
					generalUtilLogger.logWrite(e);
					return "-3";
				}
			} else if (onChangeColumnName.equals("template_id")) {
				try {
					onChangeEditTableSharedCellCore(stateKey, parentFormCode, formId, formCode, onChangeFormId, userId,
							onChangeColumnName, onChangeColumnVal, saveType, formNumberId, oldVal,
							"AnalytMethodSelect_id", "AnalytMathodTable", "AnalytMethodSelect");
				} catch (Exception e) {
					generalUtilLogger.logWrite(e);
					return "-3";
				}
			}
		//**** Experiment / ExperimentCP
		} else if (parentFormCode.equals("Experiment") || parentFormCode.equals("ExperimentCP")) {

			if (formCode.equals("paramref")) {
				try {
					update = onChangeEditTableCellCore(formCode, formId, saveType, onChangeColumnName,
							onChangeColumnVal, onChangeFormId, userId);
					if (onChangeColumnName.equalsIgnoreCase("PARAMETER_ID")) {
						String toReturn = "{}";
						if (update.equals("1")) {
							String _sql = "select  fg_get_Uom_key_val_list_byname(ut.UOMTYPENAME) as param_uom_obj \n"
									+ " ,nvl(t.UOM,fg_get_Uom_by_uomtype(ut.UOMTYPENAME)) as uom_id_default \n"
									+ " ,t.precision\n" + " from FG_S_MP_V t,FG_S_UOMTYPE_ALL_V UT \n"
									+ " where t.UOMTYPE_ID = ut.UOMTYPE_ID(+)\n" + " and t.mp_id = '"
									+ onChangeColumnVal + "'";
							List<Map<String, Object>> listOfMap = generalDao.getListOfMapsBySql(_sql);
							Map<String, Object> data = listOfMap.get(0);
							toReturn = "{\"UOM_OBJ\":" + data.get("param_uom_obj") + "," + "\"UOM_ID_DEFAULT\":\""
									+ data.get("uom_id_default") + "\"," + "\"PRECISION\":\"" + data.get("precision")
									+ "\"" + "}";
						}
						return toReturn;
					}
				} catch (Exception e) {
					generalUtilLogger.logWrite(e);
					return "-3";
				}
			} else if (formCode.equalsIgnoreCase("ExpRunPlanning")) {
				try {
					String pivotFormId = generalUtil.getJsonValById(onChangeColumnVal, "pivotFormId");
					String newVal = generalUtil.getJsonValById(onChangeColumnVal, "value");
					String wherePart = "1=1";
					if (onChangeColumnName.equalsIgnoreCase("MATERIALREFEQUIVALENT")
							|| onChangeColumnName.equalsIgnoreCase("MATERIALRATEUOM")) {
						wherePart = "MATERIALREFID = '" + pivotFormId + "'";
					} else if (onChangeColumnName.equalsIgnoreCase("PARAMETERREFVALUE")
							|| onChangeColumnName.equalsIgnoreCase("PARAMETERUOM")) {
						wherePart = "PARAMETERREFID = '" + pivotFormId + "'";
					} else if (onChangeColumnName.equalsIgnoreCase("RETENTIONTIME")
							|| onChangeColumnName.equalsIgnoreCase("RETENTIONTIMEUOM")
							|| onChangeColumnName.equalsIgnoreCase("APSS")) {
						wherePart = "STEPID = '" + pivotFormId + "'";
					}
					String sql = "update FG_S_EXPRUNPLANNING_PIVOT t set t." + onChangeColumnName + " = '" + newVal
							+ "', t.change_by = '" + userId + "', t.timestamp = sysdate \n" + " where  " + wherePart
							+ "\n" + " and t.formid = '" + onChangeFormId + "' \n"
							+ " and t.sessionid is null and t.active = 1";
					update = formSaveDao.updateStructTableByFormId(sql, "FG_S_EXPRUNPLANNING_PIVOT",
							Arrays.asList(onChangeColumnName), onChangeFormId);

					return update;
				} catch (Exception e) {
					generalUtilLogger.logWrite(e);
					return "-3";
				}
			}
		//**** Step/StepFr
		} else if ((parentFormCode.equals("Step") || parentFormCode.equals("StepFr") || parentFormCode.equals("StepMinFr"))
				&& !onChangeColumnName.isEmpty()) {
			String sql = "";
			if (formCode.equals("Action") || formCode.equals("Document") || formCode.startsWith("RESULTREFID")) {
				/*
				 * String actionName = generalDao.
				 * selectSingleStringNoException("select actionname from fg_s_action_pivot where formid = '"
				 * +onChangeFormId+"'"); if(generalUtil.getNull(actionName).isEmpty()){
				 * integrationValidation.validate(ValidationCode.ActionNameMandatory, formCode,
				 * formId, "", new StringBuilder()); }
				 */
				if (formCode.startsWith("RESULTREFID")) { // we expect getting RESULTID,<result_id> in the call FOR THE
															// SELFTEST - NORMALIZATION (mandatory)/NONNUMERICRESULT
															// result update
					if (formCode.contains(",")) { // if no comma we do not have result id so we return erreo "-3"
						// arrange data
//						String resultId = formCode.split(",",-1)[1];
//						Map<String, Object> resultMap = generalDao.getMapsBySql("select t.resultref_id, t.result_value from fg_results t where t.result_id = '" +resultId + "'");
//						String resultRefId = (String)resultMap.get("RESULTREF_ID");
						String resultRefId = formCode.split(",", -1)[1];

						String newVal = onChangeColumnName.equals("NORMALIZATION")
								? getFiestDouble(onChangeColumnVal, false)
								: onChangeColumnVal; // if NORMALIZATION we get the first number we found in the string
														// and update it (should not get negative values as in the webix
														// table)

						// return alert if no newval is empty for NORMALIZATION because it is mandatory)
						if (onChangeColumnName.equals("NORMALIZATION")
								&& generalUtil.getNull(newVal).trim().equals("")) {
							return "0"; // return 0 for indicate that no update was made
						}

						// update fg_s_resultref_pivot
						sql = "update fg_s_resultref_pivot t set t." + onChangeColumnName + " = '" + newVal
								+ "', t.change_by = '" + userId + "', t.timestamp = sysdate where t.formid = '"
								+ resultRefId + "' and t.sessionid is null and t.active = 1";
						update = formSaveDao.updateStructTableByFormId(sql, "fg_s_resultref_pivot",
								Arrays.asList(onChangeColumnName), resultRefId);

						// check if the row has update and update fg_result if NORMALIZATION the
						// NONNUMERICRESULT in this point was not defined and result and we hold it only
						// in fg_s_resultref_pivot
						if (generalUtil.getNull(update).equals("1")) {
							if (onChangeColumnName.equals("NORMALIZATION")) {
								sql = "update fg_results t set t.result_change_by = '" + userId
										+ "', t.result_value = '" + newVal + "' where t.RESULTREF_ID = '" + resultRefId
										+ "'";
								update = formSaveDao.updateSingleStringInfo(sql);
							}
						} else {
							return "0"; // return 0 for indicate that no update was made
						}

						return update;
					} else {
						return "-3";
					}
				} else if (onChangeColumnName.equals("SetBefore")) {
					String setBefore_id = generalDao
							.selectSingleString("select t.action_id from fg_s_action_v t where t.FORMNUMBERID = '"
									+ onChangeColumnVal + "' and t.STEP_ID = '" + formId + "'");
					onChangeColumnVal = commonFunc.actionSetBefore(formId, setBefore_id, formNumberId);
					onChangeColumnName = "FORMNUMBERID";
					oldVal = formNumberId;
					sql = "update FG_S_" + formCode + "_PIVOT " + " set " + onChangeColumnName + " = '"
							+ generalUtil.replaceDBUpdateVal(onChangeColumnVal) + "'" + " where formId = '" + onChangeFormId
							+ "'";
					update = formSaveDao.updateStructTableByFormId(sql, "FG_S_" + formCode + "_PIVOT",
							Arrays.asList(onChangeColumnName), onChangeFormId);
					return update;
				} else if (saveType.equals("richtext")) {
					// 280119 task 24013
					sql = "update FG_S_ACTION_PIVOT set startdate= to_char( sysdate, '"
							+ generalUtil.getConversionDateFormat() + "'),enddate =  to_char( sysdate, '"
							+ generalUtil.getConversionDateFormat()
							+ "'),starttime= to_char( sysdate, 'HH24:MI' )where formId = '" + onChangeFormId
							+ "' and startdate is null and enddate is null and starttime is null";
					update = formSaveDao.updateStructTableByFormId(sql, "FG_S_Action_PIVOT",
							Arrays.asList("startdate", "enddate", "starttime"), onChangeFormId);

					String toReturn = "{}";
					if (update.equals("1")) {
						// task: return updated dates
						List<Map<String, Object>> listOfMap = generalDao.getListOfMapsBySql(
								"select t.startdate, t.enddate, t.starttime from  FG_S_ACTION_PIVOT t where t.formId = '"
										+ onChangeFormId + "'");
						Map<String, Object> data = listOfMap.get(0);
						toReturn = "{\"STARTDATE\":\"" + data.get("STARTDATE") + "\"," + "\"ENDDATE\":\""
								+ data.get("ENDDATE") + "\"," + "\"STARTTIME\":\"" + data.get("STARTTIME") + "\" "
								+ "}";
					}

					onChangeColumnVal = formSaveElementDao.saveRichText(formCode,
							new DataBean(onChangeColumnName, onChangeColumnVal, BeanType.CLOB, ""), true);
					// update action table with new value
					sql = "update FG_S_" + formCode + "_PIVOT " + " set " + onChangeColumnName + " = '"
							+ onChangeColumnVal + "'" + " where formId = '" + onChangeFormId + "'";
					try {
						update = formSaveDao.updateStructTableByFormId(sql, "FG_S_" + formCode + "_PIVOT",
								Arrays.asList(onChangeColumnName), onChangeFormId);
					} catch (Exception e) {
						generalUtilLogger.logWrite(e);
						return "-3";
					}

					return toReturn;

				} else if (saveType.equals("monitoringParams")) {

					String mpFormId = generalUtil.getJsonValById(onChangeColumnVal, "mpFormid");
					String newVal = generalUtil.getJsonValById(onChangeColumnVal, "value");
					String mpName = generalUtil.getJsonValById(onChangeColumnVal, "mpName");
					boolean isUom = (generalUtil.getJsonValById(onChangeColumnVal, "isUOM").equals("0")) ? false : true;
					update = formSaveElementDao.saveMonitoringParam(mpFormId, newVal, mpName, oldVal, isUom);
					return update;
				} else if (onChangeColumnVal.equals("00/00/0000")) {
					return "-4";
				} else if (!saveType.equals("select")) {

					String change_by = generalDao.selectSingleStringNoException("select change_by from fg_s_" + formCode
							+ "_v where formid = '" + onChangeFormId + "' and rownum <=1");
					if (!generalUtil.getNull(change_by).equals(userId)) {
						// Checks if another user has changed the column value
						String whereOldVal = saveType.equals("date")
								? " = TO_CHAR(TO_DATE('" + oldVal + "', '" + generalUtil.getConversionDateFormat()
										+ "'), '" + generalUtil.getConversionDateFormat() + "')"
								: " = trim('" + oldVal + "')";
						String whereFormNumberId = " = '" + formNumberId + "'";
						if (oldVal.isEmpty()) {
							whereOldVal = " is null ";
						}
						if (formNumberId.isEmpty()) {
							whereFormNumberId = " is null ";
						}
						sql = "select 1 from fg_s_" + formCode + "_v where trim(" + onChangeColumnName + ")"
								+ whereOldVal + " and formnumberid " + whereFormNumberId + " and formid ='"
								+ onChangeFormId + "'";
						String validToChange = generalDao.selectSingleStringNoException(sql);
						if (generalUtil.getNull(validToChange).isEmpty()) {
							sql = "select distinct t." + onChangeColumnName + " as val,u.username from fg_s_" + formCode
									+ "_v t, fg_s_user_v u where  t.formid ='" + onChangeFormId
									+ "' and t.change_by = u.user_id and rownum<=1";
							Map<String, String> dbVal = generalDao.sqlToHashMap(sql);
							return " The last saved value is " + dbVal.get("VAL") + ". changed by "
									+ dbVal.get("USERNAME") + ".";
							// return dbVal;
						}
					}
				}
				if (onChangeColumnName.equals("ACTIONNAME") && formNumberId.isEmpty()) {// elementValueMap.get("STEP_ID")
					/*
					 * Map<String, String> actionValueMap = generalDao.sqlToHashMap(
					 * String.format("select * from FG_S_ACTION_PIVOT where formid = '%1$s'",
					 * onChangeFormId));
					 */
					Map<String, String> actionValueMap = new HashMap<String, String>();
					actionValueMap.put("STEP_ID", formId);
					String nextFormNumberId = formIdCalc.getNextId(actionValueMap, "Action", onChangeFormId, userId,
							"FG_S_ACTION_PIVOT");
					if (nextFormNumberId == null) {
						return "0";
					}

					if (!nextFormNumberId.isEmpty()) {
						sql = "update FG_S_" + formCode + "_PIVOT " + " set " + onChangeColumnName + " = '"
								+ generalUtil.replaceDBUpdateVal(onChangeColumnVal) + "',formNumberId ='" + nextFormNumberId
								+ "' where formId = '" + onChangeFormId + "'";
						update = formSaveDao.updateStructTableByFormId(sql, "FG_S_ACTION_PIVOT",
								Arrays.asList(onChangeColumnName, "formNumberId"), onChangeFormId);
						return "{\"FORMNUMBERID\":\"" + nextFormNumberId + "\"}";
					}
				} else if (saveType.equals("time") || saveType.equals("date")) {
					if (saveType.equals("time") && !onChangeColumnVal.matches("(?:[0-1][0-9]|2[0-4]):[0-5]\\d")) {
						return "-5";
					}
					Map<String, String> actionMap = new HashMap<String, String>();
					actionMap = generalDao.sqlToHashMap(String.format(
							"select t.startDate, t.enddate,t.starttime,t.endtime from FG_S_action_v t where formid = '%1$s'",
							onChangeFormId));
					if (onChangeColumnName.equals("STARTDATE")) {
						actionMap.put("STARTDATE", onChangeColumnVal);
					} else if (onChangeColumnName.equals("ENDDATE")) {
						actionMap.put("ENDDATE", onChangeColumnVal);
					} else if (onChangeColumnName.equals("STARTTIME")) {
						actionMap.put("STARTTIME", onChangeColumnVal);
					} else if (onChangeColumnName.equals("ENDTIME")) {
						actionMap.put("ENDTIME", onChangeColumnVal);
					}
					commonFunc.actionValidationDate(actionMap.get("STARTDATE"), actionMap.get("ENDDATE"),
							actionMap.get("STARTTIME"), actionMap.get("ENDTIME"), onChangeFormId);
					validationDate(actionMap.get("STARTDATE"), actionMap.get("ENDDATE"), onChangeFormId, "Action");

				}
				if (onChangeColumnName.equals("Sample")) {
					if (!onChangeColumnVal.isEmpty()) {
						String[] parts = onChangeColumnVal.split(",");
						String val = parts[0];
						String toAdd = parts[1].replace(";", "");
						if (toAdd.equals("1")) {
							String sessionId_ = generalUtilFormState.getSessionId(onChangeFormId);
							formDao.insertToSelectTable("SampleSelect", onChangeFormId, "SAMPLETABLE",
									Arrays.asList(val), false, userId, null);
						} else {
							Map<String, String> selectionData = generalDao
									.getMetaDataRowValues("select SAMPLETABLE,DISABLED,FORMID from "
											+ "fg_s_sampleselect_v  where parentid = '" + onChangeFormId + "'");
							if (selectionData.get("DISABLED").contains(val)) {
								return "-2";// sample can't be deleted
							}
							List<String> similar = new ArrayList<String>();
							similar.addAll(Arrays.asList(selectionData.get("SAMPLETABLE").split(",")));
							similar.remove(val);
							String itemsToSelectCsv = generalUtil.listToCsv(similar);
							sql = String.format("UPDATE fg_s_sampleselect_pivot t set SAMPLETABLE = '"
									+ itemsToSelectCsv + "' where t.parentid = %1$s", onChangeFormId);
							update = formSaveDao.updateStructTableByFormId(sql, "fg_s_sampleselect_pivot",
									Arrays.asList("SAMPLETABLE"), selectionData.get("FORMID"));
						}
					}
				} else {
					if (saveType.equals("date")) {
						sql = "update FG_S_" + formCode + "_PIVOT " + " set " + onChangeColumnName
								+ " = TO_CHAR(TO_DATE('" + onChangeColumnVal + "', '"
								+ generalUtil.getConversionDateFormat() + "'), '"
								+ generalUtil.getConversionDateFormat() + "') where formId = '" + onChangeFormId + "'";
					} else {
						sql = "update FG_S_" + formCode + "_PIVOT " + " set " + onChangeColumnName + " = '"
								+ generalUtil.replaceDBUpdateVal(onChangeColumnVal) + "'" + " where formId = '" + onChangeFormId
								+ "'";
					}
					try {
						update = formSaveDao.updateStructTableByFormId(sql, "FG_S_" + formCode + "_PIVOT",
								Arrays.asList(onChangeColumnName), onChangeFormId);
						
						if(generalUtil.getNull(formCode).equals("Action") && onChangeColumnName.equalsIgnoreCase("ACTIONNAME")) {
							// todo action
							updateFgSeqPathAndSearchMatch("Action", "Action", onChangeFormId, onChangeColumnVal);
						}
					} catch (Exception e) {
						generalUtilLogger.logWrite(e);
						return "-3";
					}
				}
			} else if (formCode.startsWith("paramref")) {
				if (formCode.equals("paramref")) {
					try {
						update = onChangeEditTableCellCore(formCode, formId, saveType, onChangeColumnName,
								onChangeColumnVal, onChangeFormId, userId);
						if (onChangeColumnName.equalsIgnoreCase("PARAMETER_ID")) {
							String toReturn = "{}";
							if (update.equals("1")) {
								// String _sql = "select
								// t.param_uom_obj,t.UOM_ID_SELECT_DEFAULT,t.UOM_ID_TYPE_DEFAULT from
								// FG_S_MP_ALL_V t where t.MP_ID = '"+onChangeColumnVal+"'";
								String _sql = "select  fg_get_Uom_key_val_list_byname(ut.UOMTYPENAME) as param_uom_obj \n"
										+ " ,nvl(t.UOM,fg_get_Uom_by_uomtype(ut.UOMTYPENAME)) as uom_id_default \n"
										+ " ,t.precision\n" + " from FG_S_MP_V t,FG_S_UOMTYPE_ALL_V UT \n"
										+ " where t.UOMTYPE_ID = ut.UOMTYPE_ID(+)\n" + " and t.mp_id = '"
										+ onChangeColumnVal + "'";
								List<Map<String, Object>> listOfMap = generalDao.getListOfMapsBySql(_sql);
								Map<String, Object> data = listOfMap.get(0);
								toReturn = "{\"UOM_OBJ\":" + data.get("param_uom_obj") + "," + "\"UOM_ID_DEFAULT\":\""
										+ data.get("uom_id_default") + "\"," + "\"PRECISION\":\""
										+ data.get("precision") + "\"" + "}";
							}
							return toReturn;
						}

					} catch (Exception e) {
						generalUtilLogger.logWrite(e);
						return "-3";
					}
				}
			}
		} else if (formCode.equals("FunctionRuleRef")) {
			
				try {
					update = onChangeEditTableCellCore(formCode, formId, saveType, onChangeColumnName,
							onChangeColumnVal, onChangeFormId, userId);
					/*if (onChangeColumnName.equalsIgnoreCase("PARAMETER_ID")) {
						String toReturn = "{}";
						if (update.equals("1")) {
							// String _sql = "select
							// t.param_uom_obj,t.UOM_ID_SELECT_DEFAULT,t.UOM_ID_TYPE_DEFAULT from
							// FG_S_MP_ALL_V t where t.MP_ID = '"+onChangeColumnVal+"'";
							String _sql = "select  fg_get_Uom_key_val_list_byname(ut.UOMTYPENAME) as param_uom_obj \n"
									+ " ,nvl(t.UOM,fg_get_Uom_by_uomtype(ut.UOMTYPENAME)) as uom_id_default \n"
									+ " ,t.precision\n" + " from FG_S_MP_V t,FG_S_UOMTYPE_ALL_V UT \n"
									+ " where t.UOMTYPE_ID = ut.UOMTYPE_ID(+)\n" + " and t.mp_id = '"
									+ onChangeColumnVal + "'";
							List<Map<String, Object>> listOfMap = generalDao.getListOfMapsBySql(_sql);
							Map<String, Object> data = listOfMap.get(0);
							toReturn = "{\"UOM_OBJ\":" + data.get("param_uom_obj") + "," + "\"UOM_ID_DEFAULT\":\""
									+ data.get("uom_id_default") + "\"," + "\"PRECISION\":\""
									+ data.get("precision") + "\"" + "}";
						}
						return toReturn;
					}
					*/

				} catch (Exception e) {
					generalUtilLogger.logWrite(e);
					return "-3";
				}
			
		
		} else if (parentFormCode.equals("InvItemMaterialFr")||parentFormCode.equals("InvItemMaterialPr")||parentFormCode.equals("Project")||parentFormCode.equals("InvItemBatch")) {
			formCode = "MaterialComponent";// TODO:a workaround for a change that should be done on the attribute
			if(onChangeColumnName.equals("invitemmaterial_id"))
				onChangeColumnName="material_id";							// formCode in the json-now refers to both "displayAsLink" and"autoSave"-
			// should separate to another attribute such as linkFormCode or saveFormCode
			try {
				if(parentFormCode.equals("InvItemBatch") && formCode.equals("MaterialComponent")) {
					onChangeEditTableCellCore(formCode, formId, saveType, "approvalDate", "",
						onChangeFormId, userId);
				}
				update = onChangeEditTableCellCore(formCode, formId, saveType, onChangeColumnName, onChangeColumnVal,
						onChangeFormId, userId);

			} catch (Exception e) {
				generalUtilLogger.logWrite(e);
				return "-3";
			}

			// **** _RESULT
		} else if (formCode.equals("_RESULT")) { // TODO taro develop (4) insert into fg_resualts (value and UOM)
			String resultId = onChangeFormId;
			String resultVal = onChangeColumnVal;
//			System.out.println("_RESULT onChangeFormId(resultId)=" + resultId + ", onChangeColumnVal(resultVal)=" + resultVal);

			if (!generalUtil.getNull(resultId).equals("")
					&& !generalUtil.getNull(resultId).trim().toLowerCase().equals("result_id")) {
				String sql = "";
				if (onChangeColumnName.equals("_COMMENT")) {
					sql = "update fg_results t set t.result_change_by = '" + userId + "', t.result_comment = '"
							+ resultVal + "' where t.result_id = '" + resultId + "'";
				} else if (onChangeColumnName.equals("STARTDATE")) {
					if (onChangeColumnVal.equals("00/00/0000")) {
						return "-4";
					}
					sql = "update fg_results t set t.result_change_by = '" + userId + "', t.result_date = '" + resultVal
							+ "' where t.result_id = '" + resultId + "'";
				} else if (onChangeColumnName.equals("STARTTIME")) {
					if (!resultVal.matches("(?:[0-1][0-9]|2[0-4]):[0-5]\\d")) {
						return "-5";
					}
					sql = "update fg_results t set t.result_change_by = '" + userId + "', t.result_time = '" + resultVal
							+ "' where t.result_id = '" + resultId + "'";
				} else if (onChangeColumnName.equals("NA")) {
					sql = "update fg_results t set t.result_change_by = '" + userId + "', t.result_value = '"
							+ resultVal + "' where t.result_id = '" + resultId + "'";
				}
				if (!sql.equals("")) {
					update = formSaveDao.updateSingleStringInfo(sql);
				}
			}
		} else if (parentFormCode.equals("ExperimentAn")) {
			if (formCode.equals("InvItemColumn") && onChangeColumnName.equalsIgnoreCase("invitemcolumn_id")) {
				try {
					onChangeEditTableSharedCellCore(stateKey, parentFormCode, formId, formCode, onChangeFormId, userId,
							onChangeColumnName, onChangeColumnVal, saveType, formNumberId, oldVal, "columnselect_id",
							"column_id", "ColumnSelect");
				} catch (Exception e) {
					generalUtilLogger.logWrite(e);
					return "-3";
				}
			}
			else if(formCode.equals("InvItemMaterial") && onChangeColumnName.equalsIgnoreCase("INVITEMMATERIAL_ID")) {
				
				formCode = "Component";
				onChangeColumnName = "materialId";
				String componentName = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID, onChangeColumnVal, "name");
				update = onChangeEditTableCellCore(formCode, formId, saveType, onChangeColumnName,
						onChangeColumnVal, onChangeFormId, userId);
				update = onChangeEditTableCellCore(formCode, formId, saveType, "componentName",
						componentName, onChangeFormId, userId);
			}else if(formCode.equals("Component") && onChangeColumnName.equalsIgnoreCase("numOfStandardRows")){
				int idx = onChangeColumnVal.indexOf(".");
				onChangeColumnVal = idx!=-1?onChangeColumnVal.substring(0 , idx):onChangeColumnVal;
				update = onChangeEditTableCellCore(formCode, formId, saveType, onChangeColumnName,
						onChangeColumnVal, onChangeFormId, userId);
			}else if(formCode.equals("Component")) {
				update = onChangeEditTableCellCore(formCode, formId, saveType, onChangeColumnName,
						onChangeColumnVal, onChangeFormId, userId);
				if(onChangeColumnName.equals("impurity")&& onChangeColumnVal.equals("1")) {
					onChangeEditTableCellCore(formCode, formId, saveType, "standardIncluded",
							onChangeColumnVal, onChangeFormId, userId);
					onChangeEditTableCellCore(formCode, formId, saveType, "coefficient",
							onChangeColumnVal, onChangeFormId, userId);
				}
				if(onChangeColumnName.equals("impurity") ||onChangeColumnName.equals("OUM_ID")||onChangeColumnName.equals("rt")) {
					Map<String, String> elementValueMap = new HashMap<String, String>();
					String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, formId);
					
					elementValueMap.put("sessionid", sessionId);
					String rrt = integrationCalc.doCalc("doCalcEditableTable", "OnSave", "", elementValueMap, null, null, formCode, onChangeFormId,
							userId);
					if(!generalUtil.getNull(rrt).isEmpty()) {
						onChangeEditTableCellCore(formCode, formId, saveType, "rrt",
								rrt, onChangeFormId, userId);
					}
				}
			}
		}
		else if(formCode.equals("Document") && onChangeColumnName.equalsIgnoreCase("EXPORTTOREPORT")) {
			update = onChangeEditTableCellCore(formCode, formId, saveType, onChangeColumnName,
					onChangeColumnVal, onChangeFormId, userId);
		}
		else if(formCode.equals("ReportFilterRef") && (onChangeColumnName.equalsIgnoreCase("RULENAME") || onChangeColumnName.equalsIgnoreCase("STEPNAME"))) {
			update = onChangeEditTableCellCore(formCode, formId, saveType, onChangeColumnName,
					onChangeColumnVal, onChangeFormId, userId);
		}
		return update;
	}

	private String onChangeEditTableCellCore(String formCode, String formId,String saveType, String onChangeColumnName, String onChangeColumnVal
			, String onChangeFormId, String userId){
		String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, formId);
		String sql = "";
		String update = "";
		if (saveType.equals("richtext")) {
			
			
			onChangeColumnVal = formSaveElementDao.saveRichText(formCode,
					new DataBean(onChangeColumnName, onChangeColumnVal, BeanType.CLOB, ""), true);
		}
		//if (saveType.equals("text")) 
		{
			sql = "update FG_S_" + formCode + "_PIVOT " + " set "+ onChangeColumnName +"= '"
					+ generalUtil.replaceDBUpdateVal(onChangeColumnVal) + "'" + " where formId = '" + onChangeFormId + "' "
					+ ((sessionId == null)?"and sessionid is null":"and sessionid='" + sessionId + "'") + " and active=1";
			update = formSaveDao.updateSingleStringInfoNoTryCatch(sql);
			if(update.equals("0")){
				String colList = generalDao.getTableColCsv("FG_S_" + formCode + "_PIVOT");						
				String valList = "," +colList+",";
				valList = valList.replace(",CHANGE_BY,", ","+userId+",")
						.replace(",TIMESTAMP,", ",sysdate,").replace(",CREATION_DATE,", ",sysdate,")
						.replace(",CREATED_BY,", ","+userId+",").replace(",SESSIONID,", ","+sessionId+",")
						.replace(","+onChangeColumnName.toUpperCase()+",", ",'"+ generalUtil.replaceDBUpdateVal(onChangeColumnVal) +"',");
				valList = valList.substring(1, valList.length()-1);
				sql = String.format(
						"insert into FG_S_" + formCode + "_PIVOT (%1$s) select %2$s from FG_S_%3$s_PIVOT t where formid = %4$s and sessionid is null and active=1",
						colList, valList, formCode, onChangeFormId);
				update = formSaveDao.updateSingleStringInfoNoTryCatch(sql);
			}
		}
		return update;
	}
	private String onChangeEditTableSharedCellCore(long stateKey, String parentFormCode, String formId, String formCode,
			String onChangeFormId, String userId, String onChangeColumnName, String onChangeColumnVal, String saveType,
			String formNumberId, String oldVal, String selectFormIdColName ,String csvColName, String selectFormCode) throws Exception {
	
		String update = "";
		String sessionId = generalUtilFormState.checkAndReturnSessionId(selectFormCode, formId);
		String csvIds = generalDao.selectSingleStringNoException(
				String.format("select %1$s from fg_s_%2$s_v t where t.FORMID = '%3$s' and PARENTID='%4$s' and sessionid='%5$s'",
						csvColName, selectFormCode, onChangeFormId, formId, sessionId));
		oldVal = (oldVal.equals(""))?"-1":oldVal;
		csvIds = csvIds.replace(oldVal, onChangeColumnVal);//"-1"						
		String sql = String.format( "update FG_S_%1$s_PIVOT set %2$s = '%3$s' where formId = '%4$s' and PARENTID='%5$s' and sessionid='%6$s'",
				selectFormCode, csvColName, csvIds, onChangeFormId, formId, sessionId);
		 
		update = formSaveDao.updateSingleStringInfoNoTryCatch(sql);

		if(update.equals("0")){
			csvIds = generalDao.selectSingleString(
						String.format("select %1$s from fg_s_%2$s_v where %3$s = '%4$s' and sessionid is null", 
								csvColName, selectFormCode, selectFormIdColName, onChangeFormId));
			csvIds = csvIds.replace(oldVal, onChangeColumnVal);//"-1"
				
			String colList = generalDao.getTableColCsv("FG_S_"+selectFormCode+"_PIVOT");						
			String valList = colList
					.replace("CHANGE_BY", userId)
					.replace(",TIMESTAMP", ",sysdate").replace("CREATION_DATE", "sysdate")
					.replace("CREATED_BY", userId).replace("SESSIONID", sessionId).replace(csvColName.toUpperCase(), "'"+csvIds+"'");

			sql = String.format(
					"insert into FG_S_%1$s_PIVOT (%2$s) select %3$s from FG_S_%4$s_PIVOT t where formid = %5$s and sessionid is null",
					selectFormCode, colList, valList, selectFormCode, onChangeFormId);
			
			update = formSaveDao.updateSingleStringInfoNoTryCatch(sql);
		}
		 return update;
	}
	
	private void validationDate(String startDate, String endDate, String formId, String formCode) throws Exception {
		String sql = String.format(
				"select 1 from fg_s_" + formCode + "_v t where to_date('" + startDate + "', 'dd/MM/yyyy') > to_date('"
						+ endDate + "', 'dd/MM/yyyy') and  t.formid = " + formId + " and t.active=1");
		String invalidDate = generalDao.selectSingleStringNoException(sql);
		if (!generalUtil.getNull(invalidDate).isEmpty()) {
			integrationValidation.validate(ValidationCode.INVALID_DATE, formCode, formId, "", new StringBuilder());
		}
	}

	@Override
	public String getRichTextContent(String formID, String formCode, String dbColName) {
		String richtextID = "";
		String richtextContent = "";
		try {
			richtextID = generalDao.selectSingleString("select nvl(" + dbColName + ",0) from FG_S_" + formCode
					+ "_PIVOT t  where t.formid = '" + formID + "'");
			if (!richtextID.equals("0")) {
				richtextContent = generalUtilFormState.getRichTextContent(richtextID);
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return richtextContent;
	}
	
	private String getFiestDouble(String str, boolean allowNegative) {
		String toReturn = null;
		try {
			Pattern p = Pattern.compile(allowNegative?"(^-?\\d*\\.{0,1}\\d+)":"(\\d*\\.{0,1}\\d+)");
			Matcher m = p.matcher(str);
			while(m.find()) {
			    double d = Double.parseDouble(m.group(1));
			    toReturn = String.valueOf(d);
			    break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		return toReturn;
	}
	
	@Override
	public List<Map<String, String>> customCriteriaList(String elementCode, String struct, long stateKey, List<Map<String, String>> sqlPoolMapList,
			Map<String, String> parentMap, Map<String, String> currentFormMap) {
		List<Map<String, String>> returnListMap = new ArrayList<Map<String, String>>();

		try {
			String parentFormCode = generalUtil.getNull(parentMap.get("$P{FORMCODE}"));
			String formCode = generalUtil.getNull(currentFormMap.get("$P{FORMCODE}"));
			String tableType = generalUtil.getNull(currentFormMap.get("$P{TABLETYPE}"));
			String materialType = generalUtil.getNull(currentFormMap.get("$P{STRUCT_PARAM_CURRENT_TYPE_ID_NAME}"));
			String fce = formDao.getFormCodeEntityBySeqId("", generalUtil.getNull(parentMap.get("$P{FORMID}")));
			String parentParentId = "";
			String parentParentFormCodeEntity = "";
			if (!generalUtil.getNull(parentMap.get("$P{PARENT_ID}")).equals("-1")
					&& !generalUtil.getNull(parentMap.get("$P{PARENT_ID}")).equals("")) {
				parentParentId = generalUtil.getNull(parentMap.get("$P{PARENT_ID}"));
				parentParentFormCodeEntity = formDao.getFormCodeEntityBySeqId("", parentParentId);
			}
			String default_ = "";

			List<String> removeList = new ArrayList<String>();
			if (formCode.equals("SampleSelect") || formCode.equals("SampleSearch")
					|| formCode.equals("SampleSelectHolder")) {
				if ((parentFormCode.equals("InvItemBatch"))) {
					default_ = "Same product";
					removeList.add("Same Project");
					removeList.add("Same Experiment");
				}
				if ((parentFormCode.equals("SelfTest"))) {
					default_ = "Same step";
				}

				if ((parentFormCode.equals("ExperimentAn"))) {
					default_ = "Same subproject";
				}
				if ((parentFormCode.startsWith("ExperimentPr"))) {
					default_ = "Same subproject";
				}
				if ((parentFormCode.equals("Action"))) {
					default_ = "Same Experiment";
				}
				if ((fce.equals("Workup"))) {
					default_ = "Same step";
				}

				else if (parentFormCode.equals("Request")) {
					String parentParam = "";
					if (generalUtil.getNull(parentMap.get("$P{PARENT_ID}")).equals("")
							|| generalUtil.getNull(parentMap.get("$P{PARENT_ID}")).equals("-1")) {
						parentParam = "PARENTID";
					} else
						parentParam = "PARENT_ID";
					String requestParentId = generalUtil.getNull(parentMap.get("$P{" + parentParam + "}"));
					String requestParentFormCode = generalUtil.getNull(formDao.getFormCodeBySeqId(requestParentId));
					if (requestParentFormCode.startsWith("Step")) {
						default_ = "Same step";
						generalUtilFormState.setFormParam(stateKey, "SampleSelectHolder", "STRUCT_PARAM_STEP_ID",
								generalUtil.getNull(currentFormMap.get("$P{STRUCT_PARAM_" + parentParam + "}")));

					}
					if (requestParentFormCode.equals("Experiment") || requestParentFormCode.equals("ExperimentAn")
							|| requestParentFormCode.equals("ExperimentCP")
							|| requestParentFormCode.equals("ExperimentFor")
							|| requestParentFormCode.startsWith("ExperimentPr")) {
						default_ = "Same Experiment";
					}
					if (requestParentFormCode.equals("SubProject")) {
						default_ = "Same subproject";
					}
					if (requestParentFormCode.equals("SubSubProject")) {
						default_ = "Same subsubproject";
					}
					if (requestParentFormCode.equals("InvItemBatch")) {
						default_ = "Same batch";
					}
				}

			}
			///// materials select/search
			if (formCode.equals("MaterialSearch")) {
				if (parentFormCode.startsWith("Step") && tableType.equals("Reactant")) {
					default_ = "Reactants";
				}
				if (parentFormCode.startsWith("Step") && tableType.equals("Solvent")) {
					default_ = "Solvents";
				}
				if (parentFormCode.startsWith("Step") && tableType.equals("Product")) {
					default_ = "Products";
				}

				if (parentFormCode.equals("SelfTest")) {
					default_ = "Current Step";
				}

				if (parentParentFormCodeEntity.equals("Workup") || fce.equals("Workup")) {
					default_ = "Current Step";
				}
				if (parentFormCode.equals("ExperimentPrCR")) {
					default_ = "Alloys";
				}
				if (parentFormCode.equals("ExperimentPrVS")) {
					default_ = "Standard";
				}
				if (parentFormCode.equals("Component")) {
					String ComponentParentId = generalUtil.getNull(parentMap.get("$P{PARENT_ID}"));
					String ComponentParentFormCode = generalUtil.getNull(formDao.getFormCodeBySeqId(ComponentParentId));
					if (ComponentParentFormCode.equals("ExperimentAn") && materialType.equals("Active Ingredient")) {
						default_ = "Products";
					}
					if (ComponentParentFormCode.equals("ExperimentAn") && materialType.equals("Impurity")) {
						default_ = "Impurities";
					}

				}

				if (parentFormCode.equals("FormulantRef")) {
					String formulantParentId = generalUtil.getNull(parentMap.get("$P{PARENT_ID}"));
					String FormulantRefParentFormCodeEntity = generalUtil
							.getNull(formDao.getFormCodeEntityBySeqId("", formulantParentId));
					if (generalUtil.getNull(FormulantRefParentFormCodeEntity).equals("Step")) {
						default_ = "Current Experiment";
					} else if (generalUtil.getNull(FormulantRefParentFormCodeEntity).equals("Experiment")) {
						default_ = "Current Project";
					} else if (generalUtil.getNull(FormulantRefParentFormCodeEntity).equals("ExperimentSeries")) {
						default_ = "Current Project";
					} else {
						default_ = "ALL";
					}
				}
			}
			
			if(formCode.equals("MaterialSlctSearch")) {
				if (elementCode.equals("materialTable") && parentFormCode.equals("Project")) {
					default_ = "Active Ingredient";
				}
			}
			
			for (int i = 0; i < sqlPoolMapList.size(); i++) {
				Map<String, String> sqlPoolMap = sqlPoolMapList.get(i);
				String option = sqlPoolMap.get("SYSCONFSQLCRITERIANAME");

				// get parameter from sql to check after if exists in parameters map
				String param = "";
				if (sqlPoolMap.get("SQLTEXT").contains("'$P{"))
					param = sqlPoolMap.get("SQLTEXT").substring(sqlPoolMap.get("SQLTEXT").lastIndexOf("'$P{"),
							sqlPoolMap.get("SQLTEXT").indexOf("}'") + 2);

				// if parameter not exsits in parameter map or the value is empty remove from
				// criteria list
				if (!param.equals("")) {
					if (!isCriteriaParameterExists(param, currentFormMap) && !removeList.contains(option))
						removeList.add(option);
				}

				if (!removeList.contains(option)) {
					if (!sqlPoolMap.get("IGNORE").equals("1")) {
						if (!default_.isEmpty() && option.equals(default_)) {
							sqlPoolMap.put("ISDEFAULT", "1");
						} else if (!default_.isEmpty()) {
							sqlPoolMap.put("ISDEFAULT", "0");
						}
					}
					returnListMap.add(sqlPoolMap);
				}
			}
		} catch (Exception e) {
			returnListMap = sqlPoolMapList;
		}
		return returnListMap;
	}
	
	private boolean isCriteriaParameterExists(String parameter, Map<String, String> currentFormMap) {
		boolean isExists = false;
		for (Map.Entry<String, String> m : currentFormMap.entrySet()) {
			if (m.getKey().toLowerCase().equals(parameter.toLowerCase().replaceAll("'", "")) && m.getValue() != null
					&& !m.getValue().equals(""))
				isExists = true;
		}
		return isExists;
	}
 
	@Override
	public JSONObject customerDTDisplayViewList(String fromCode, String elementCode, String struct, String displayCatalogItemDefaulValue, String lastDTView, boolean isLevelChange) {
		JSONObject returnObj = new JSONObject();
		String selectHtml = "";
		String selectVal = "";
		String options = null;
		StringBuilder sb = new StringBuilder();
		
		//System.out.println("customerDTViewList formCode = " + fromCode + ", elementCode = " + elementCode + ", struct=" + struct + ",lasdDTView=" +  lastDTView + ", displayCatalogItemDefaulValue=" + displayCatalogItemDefaulValue );
		// **** set list options by form code ****
		if(fromCode.equals("InvItemMaterialsMain")) {
			if(elementCode.equals("upperTable")) {
				options = "DTMC,Chemical Mateirial;DTMF,Formulation;DTMP,Premix";
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
	
	/**
	 * return @-separated string with column should be hided from the tables
	 * @param formCode
	 * @param impCode
	 * @param struct
	 * @return
	 */
	@Override
	public String customerDTDefaultHiddenColumns(String formCode, String impCode, String struct) {
		StringBuilder result = new StringBuilder("");
		//***** Main (main screen - project management -> using app prop for default)
		if (formCode.equals("Main")){
			//firstColumn
			String firstColumn = "FAVORITE"; //name of the column from which begin fetch
			if (struct.toUpperCase().equals("REQUEST")){ //in the Request view form there is "_SMARTSELECT" field. From sql it's got as "CHECKBOX". Also it need to start from Checkbos field on Request table (not Favorite) 
				firstColumn = "CHECKBOX";
			}
			boolean beginFetch = false; //if true then fetch can be begin
			boolean isFirst = true;
			
			//init "columns" list from viewsAndDefaultColumns_ property, using the struct to recognize the table name (used as key)
			JSONObject defaultColumnsJson = new JSONObject(viewsAndDefaultColumns_);
			List<String> columns;
			try {
				if(!defaultColumnsJson.has("FG_S_"+struct.toUpperCase()+"_DTM_V")) {
					return "";
				} else {
					columns = Arrays.asList(defaultColumnsJson.getString("FG_S_"+struct.toUpperCase()+"_DTM_V").split(","));
				}
			} catch (JSONException e) {
				e.printStackTrace();
				return "";
			}
			
			//init "select" columns as the view meta data
			String select = "select nvl(regexp_replace(T.COLUMN_NAME,'_SMART.*'),'CHECKBOX') AS COLUMN_NAME from user_tab_columns t  where t.TABLE_NAME = 'FG_S_"+struct.toUpperCase()+"_DTM_V' order by t.COLUMN_ID";
			List<String> listOfAllColumns = generalDao.getListOfStringBySql(select);
			if (struct.toUpperCase().equals("STEP")){ //special case for fg_s_stem_dtm_v where is column "Step Name{RUN_TYPE}" -> replace it on "Step Name"
				int ind = listOfAllColumns.indexOf("Step Name{RUN_TYPE}");
				if (ind!=-1) {
					listOfAllColumns.set(ind,"Step Name");
				}
			}
			
			//set result with the hidden columns (in "select" but no in "columns")
			for (int i = 0; i < listOfAllColumns.size(); i++){
				if (listOfAllColumns.get(i).toUpperCase().equals(firstColumn)){
					beginFetch = true;
				}
				if (!beginFetch){
					continue;
				}
				boolean toAdd = true;
				for (int j = 0; j < columns.size(); j++) {
					if (columns.get(j).toUpperCase().equals(listOfAllColumns.get(i).toUpperCase())){
						toAdd = false;
						break;
					}
				}
				if (toAdd){
					if (isFirst) {
						result.append(listOfAllColumns.get(i));
						isFirst = false;
					} else {
						result.append("@"+listOfAllColumns.get(i));
					}
				}
			}
		}
		
		//***** StepMinFr
		if (formCode.equals("StepMinFr")){
			if(impCode.equals("action")) {
				result.append("Set Before@Start Date@Finish Date@Start Time@Finish Time");
			}
			
			if(impCode.equals("products")) {
				result.append("Manufacturer@Function@Description");
			}
		}
		return result.toString();
		
		//if we add new columns and the client wants them to be hidden we can use this script...
//		update FG_FORMLASTSAVEVALUE t set t.entityimpvalue = to_char(t.entityimpvalue) || '@' || '<column list split by @>'
//		where t.formcode_entity = <formcode_entity> 
//		and t.entityimpcode like '%Table%'
//		and to_char(t.entityimpvalue) like 'Experiment%'
//		and t.entityimpvalue not like '%<column list split by @>%';
//
//		update FG_FORMLASTSAVEVALUE t set t.entityimpvalue = REPLACE(t.entityimpvalue,'","settings"','@' || '<column list split by @>' || '","settings"')
//		where t.formcode_entity = <formcode_entity> 
//		and t.entityimpcode like '%Table%'
//		and to_char(t.entityimpvalue) like '{"value":"Experiment,%'
//		and t.entityimpvalue not like '%<column list split by @>%';
//
//		update FG_FORMLASTSAVEVALUE t set t.entityimpvalue = REPLACE(t.entityimpvalue,',@',',') -- remove @ if first column
//		where t.formcode_entity = <formcode_entity> 
//		and t.entityimpcode like '%Table%'
//		and t.entityimpvalue like '%,@' || '<column list split by @>';
		 
	}
	
	private void updateFgSeqPathAndSearchMatch(String formCode, String formCodeEntity, String formId, String name) {
		String sql = "SELECT distinct A.formPath FROM FG_AUTHEN_" + formCode + "_V A WHERE TO_CHAR(A." + formCodeEntity
				+ "_ID) = '" + formId + "'";
		String formPathInfo = generalDao.selectSingleStringNoException(sql);

		if (formPathInfo != null && !formPathInfo.isEmpty()) {
			sql = "update FG_SEQUENCE SET FORMIDNAME = '" + generalUtil.replaceDBUpdateVal(name) + "', CHANGEDATE = sysdate, formpath='" + generalUtil.replaceDBUpdateVal(formPathInfo) + "' "
					+ generalUtil.getEmpty(getSearchMatchSet(formPathInfo), ", SEARCH_MATCH_ID1 = -1") + " where id='"
					+ formId + "'";
			generalDao.selectSingleStringNoException(sql);
		}
	}
	
	private String getSearchMatchSet(String formPathInfo) {
		Map<String, String> searchMatchColumnName = new LinkedCaseInsensitiveMap<>();
		searchMatchColumnName.put("Project", "SEARCH_MATCH_ID1");
		searchMatchColumnName.put("SubProject", "SEARCH_MATCH_ID2");
		searchMatchColumnName.put("SubSubProject", "SEARCH_MATCH_ID3");
		searchMatchColumnName.put("InvItemMaterial", "SEARCH_MATCH_ID4");

		String toReturn = "";
		try {
			if (formPathInfo.isEmpty()) {
				return toReturn;
			}
			JSONObject json = new JSONObject(formPathInfo);
			JSONArray pathList = json.getJSONArray("path");
			for (int i = 0; i < pathList.length(); i++) {
				String p = pathList.get(i).toString();
				String[] detailsToDisplay = generalUtil.getJsonValById(p, "name").split(":");
				String id = generalUtil.getJsonValById(p, "id");
				String formCode = detailsToDisplay.length > 0 ? detailsToDisplay[0] : "";
				//String name = detailsToDisplay.length>1?detailsToDisplay[1]:"";
				if (searchMatchColumnName.containsKey(formCode)) {
					toReturn += "," + searchMatchColumnName.get(formCode) + "='" + id + "'";//set the material_id
				}

			}
		} catch (Exception ex) {
			//			generalUtilLogger.logWrite(ex);
			//			ex.printStackTrace();
			toReturn = "";
		}
		return toReturn;

	}
}
