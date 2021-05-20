package com.skyline.customer.adama;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.LookupType;
import com.skyline.form.bean.WorkflowType;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilFormState;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.form.service.GeneralUtilPermission;
import com.skyline.form.service.IntegrationWF;

@Service
public class IntegrationWFAdamaImp implements IntegrationWF {

	@Autowired
	private FormDao formDao;

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private GeneralDao generalDao;

	@Autowired
	protected GeneralUtilLogger generalUtilLogger;

	@Autowired
	protected GeneralUtilPermission generalUtilPermission;

	@Autowired
	private GeneralUtilFormState generalUtilFormState;

	@Value("${removeUnuseStates:1}")
	private int removeUnuseStates_;
	
	@Value("${hideStepFromSpreadsheet:1}")
	private int hideStepFromSpreadsheet;

	public Map<String, String> getFormWFStateGeneral(String formCode, String userId, String formId, boolean isNewFormId,
			Map<String, String> outParamMap) {
		Map<String, String> wfMap = new HashMap<String, String>();
		List<String> wfConfigIdList = formDao.getFromInfoLookupElementData("SysConfWFStatus", LookupType.NAME, formCode,
				"ID");
		for (String id : wfConfigIdList) {
			Map<String, String> wfConfigMap = formDao.getFromInfoLookupAll("SysConfWFStatus", LookupType.ID, id);
			//fill wfMap...
			getWFState(formCode, userId, formId, isNewFormId, outParamMap, wfConfigMap.get("STATUSFORMCODE"),
					wfConfigMap.get("STATUSINFCOLUMN"), wfConfigMap.get("JSONNAME"),
					wfConfigMap.get("WHEREPARTPARMNAME"), wfMap);
			//put -1 if no status
			if (wfMap.size() == 0 || generalUtil
					.getNull(wfMap.get(generalUtil.getNull(wfConfigMap.get("WHEREPARTPARMNAME")))).equals("")) {
				wfMap.put(wfConfigMap.get("WHEREPARTPARMNAME"), "-1");
			}
		}
		return wfMap;
	}

	private Map<String, String> getWFState(String formCode, String userId, String formId, boolean isNewFormId,
			Map<String, String> formParam, String tableName, String statusColumnName, String jsonName,
			String statusParamName, Map<String, String> wfMap) {
		try {
			//step 1: Preparing Data
			List<String> statusList = new ArrayList<String>();
			Map<String, String> statusFinalMap = new HashMap<String, String>();
			StringBuilder msgBuilder = new StringBuilder();
			String msg;
			String currentStatusName = "", availableStatusNames = "";

			if (generalUtil.getNull(formId).equals("") || generalUtil.getNull(formId).equals("-1")) {
				wfMap.put(statusParamName, "-1");
				generalUtilLogger.logWriter(LevelType.WARN, "WF warn! no formid", ActivitylogType.WorkFlowStatus,
						formId);
				return wfMap;
			}

			if (!isNewFormId) {
				currentStatusName = formDao.getFromInfoLookup(formCode, LookupType.ID, formId, statusColumnName);
			}

			String statusTableName = tableName;

			statusFinalMap = formDao.getFromInfoLookupAllElementData(statusTableName, LookupType.NAME, "id");
			msg = "1.Current status: " + currentStatusName;
			generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
			generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId, msgBuilder);
			//generalUtilLogger.logWriter(LevelType.DEBUG, statusParamName + " Basic Map (Step 1): " + statusFinalMap.toString(), ActivitylogType.WorkFlow, formId);

			// step 2: Get list of correct WF
			List<String> wfNames = null;
			//if(!isAdditionalWF)

			wfNames = generalUtil.getWfAvailableState(jsonName, WorkflowType.STATUS, currentStatusName);
			if((formCode.equals("StepFr")||formCode.equals("StepMinFr"))&& isNewFormId && !formParam.get("EXPERIMENTSTATUSNAME").equals("Planned")){
				wfNames = generalUtil.getWfAvailableState(jsonName, WorkflowType.STATUS, "Active");
			}
			if(formCode.equals("RecipeFormulation") && isNewFormId){
				if(formParam.get("ORIGIN").equals("Data Bank")){
					wfNames = generalUtil.getWfAvailableState(jsonName, WorkflowType.STATUS, "Approved");
				} else if(formParam.get("ORIGIN").equals("Experiment")){
					wfNames = generalUtil.getWfAvailableState(jsonName, WorkflowType.STATUS, "Active");
				}
				
			}
			if (formCode.equals("Request") && currentStatusName.isEmpty()) {
				wfNames = generalUtil.getWfAvailableState(jsonName, WorkflowType.STATUS,
						generalUtil.replaceLast(wfNames.toString(), "]", "").replaceFirst("\\[", ""));
			}
			if (formCode.startsWith("Workup")) {
				List<String> prevWfNames = generalUtil.getWfPreviousAvailableState(jsonName, WorkflowType.STATUS,
						currentStatusName);
				String lastStatusName = "";
				String nextStatusName = "";
				nextStatusName = wfNames.get((wfNames.size() > 1) ? 1 : 0);
				lastStatusName = prevWfNames.get(0);
				String id = statusFinalMap.get(nextStatusName);
				wfMap.put("NEXT_" + statusColumnName, id);
				wfMap.put("PREV_" + statusColumnName, statusFinalMap.get(lastStatusName));
				wfMap.put("FIRST_" + statusColumnName,
						statusFinalMap.get(generalUtil.getWfFirstState(jsonName, WorkflowType.STATUS)));
			}

			/*
			 * } else if(formCode.equals("Workup")) { String workupType = (!isNewFormId)? formParam.get("WORKUP_TYPE"): "Drying";//"feeding" ; wfNames = generalUtil.getWfAvailableState(workupType, WorkflowType.STATUS, currentProjectStatusName); }
			 */
			if (wfNames != null && wfNames.size() > 0) {

				for (String stateName : wfNames) {
					String id = statusFinalMap.get(stateName);
					if (generalUtil.getNull(id).equals("")) {
						generalUtilLogger.logWriter(LevelType.ERROR, "WF error! " + stateName + " has no id",
								ActivitylogType.WorkFlowStatus, formId);
						wfMap.clear();
						wfMap.put(statusParamName, "-1");
						return wfMap;
					}
					statusList.add(id);
					availableStatusNames += (availableStatusNames.equals("")) ? stateName : "," + stateName;
				}
			} else {
				generalUtilLogger.logWriter(LevelType.WARN, "WF! status list is empty",
						ActivitylogType.WorkFlowStatus, formId);
				wfMap.clear();
				wfMap.put(statusParamName, "-1");
				return wfMap;
			}

			//generalUtilLogger.logWriter(LevelType.DEBUG, statusParamName + " of correct WF (Step 2): " + statusList.toString(), ActivitylogType.WorkFlow, formId);
			msg = "2.List of available statuses: " + availableStatusNames;
			generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
			generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId, msgBuilder);

			// step 3: Remove elements from the list when another rules exists
			String userName = formDao.getFromInfoLookup("USER", LookupType.ID, generalUtil.getNull(userId), "NAME");
			if ((userName.equals("admin") || userName.equals("system")) && removeUnuseStates_ == 0) {
				msg = "3.WF removeUnuseStates_ flag = 0. Unused states have not been removed";
				generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
				generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId, msgBuilder);
			} else {
				removeUnuseStates(formCode, formId, userId, formParam, statusList, WorkflowType.STATUS, msgBuilder);
			}
			// step 3.5: Add back save status if removed 
			if (!isNewFormId && !statusList.contains(statusFinalMap.get(currentStatusName))) {
				statusList.add(statusFinalMap.get(currentStatusName));
				msg = "4.WF put back current = '" + currentStatusName + "' status to the list";
				generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
				generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId, msgBuilder);
			}
			//generalUtilLogger.logWriter(LevelType.DEBUG, statusParamName + " of rules (Step 3): not in use", ActivitylogType.WorkFlow, formId);

			// step 4: Update formParam Map			
			wfMap.put(statusParamName,
					generalUtil.replaceLast(statusList.toString().replaceFirst("\\[", ""), "]", "").replace(", ", ","));
			wfMap.put(statusParamName + "_INFO", msgBuilder.toString());
		} catch (Exception e) {
			wfMap.put(statusParamName, "-1");
			generalUtilLogger.logWriter(LevelType.ERROR, "WF error! exception in " + statusParamName + " evaluation",
					ActivitylogType.WorkFlowStatus, formId, e, null);
		}

		return wfMap;
	}

	/**
	 * Get CSV of new available forms for the 'parent Form'
	 * 
	 * @param formCode
	 * @param formId
	 * @return {String} CSV
	 */
	@Override
	public String getNewAvailableFormList(long stateKey, String formCode, String formId,
			Map<String, String> formParamMap, String createNeFormCode) {
		String newFormListCSV;
		String path;
		String statusLogOrder = "- ";
		List<String> wfNames = null;
		StringBuilder msgBuilder = new StringBuilder();
		String userId = generalUtil.getSessionUserId();
		if (!generalUtil.getNull(createNeFormCode).equals("")) {
			wfNames = new LinkedList<String>();
			wfNames.add(createNeFormCode);
		} else {
			String wfFormCode = getWFFormCodeEntity(formCode, WorkflowType.NEW);
			/*if (formCode.startsWith("STest") || formCode.startsWith("Workup")) {
				path = wfFormCode.toLowerCase() + "_new.json";
			} else {
				path = formCode.toLowerCase() + "_new.json";
			}*/
			path = "new.json";//WF NEW IN ONE JSON 

			wfNames = generalUtil.getWfAvailableState(path, WorkflowType.NEW, wfFormCode);
		}
		generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew,
				statusLogOrder + "List of available states: "
						+ generalUtil.replaceLast(wfNames.toString(), "]", "").replaceFirst("\\[", "").replace("StepFr", "Step"),
				formId, msgBuilder);
		generalUtilLogger.logWriter(LevelType.DEBUG, "NEW_WF_LIST of correct  WF (Step 1): " + wfNames.toString(),
				ActivitylogType.WorkFlowNew, formId);
		if (wfNames != null && wfNames.size() > 0) {
			
			wfNames = correctWFListForPermission(wfNames);

			generalUtilPermission.removeUnPermissionNew(formCode, formId, wfNames,
					msgBuilder);

			// step 2: Remove elements from the list when another rules exists
			removeUnuseStates(formCode, formId, userId, formParamMap, wfNames, WorkflowType.NEW, msgBuilder);

			if(!generalUtil.getNull(createNeFormCode).equals("")){
				wfNames = correctWFListForPermission(wfNames);
			} 
			
			// step 4: Update formParam Map
			newFormListCSV = (generalUtil.replaceLast(wfNames.toString().replaceFirst("\\[", ""), "]", "").replace(", ",
					","));
			
		} else {
			generalUtilLogger.logWriter(LevelType.WARN, "WF! status list is empty", ActivitylogType.WorkFlowNew,
					formId);
			newFormListCSV = "";
			return newFormListCSV;
		}
		generalUtilFormState.setFormParam(stateKey, formCode, "STEPS_WF_LIST_INFO",
				msgBuilder.toString().replace("InvItem", ""));
		return newFormListCSV;

	}

	private List<String> correctWFListForPermission(List<String> wfNames) {
		List<String> correctList_ = new LinkedList<String>();
		for (String wfName_ : wfNames) {
			if (wfName_ != null) {
				if (wfName_.equals("InvItemMaintenancePreventive") || wfName_.equals("InvItemMaintenanceBreakdown")) {
					if (!correctList_.contains("InvItemMaintenance")) {
						correctList_.add("InvItemMaintenance");
					}

				} else if (wfName_.equals("Request (Copy Default)")) {
					if (!correctList_.contains("Request")) {
						correctList_.add("Request");
					}

				} else {
					correctList_.add(wfName_);
				}
			}
		}
		return correctList_;
	}

	private String getWFFormCodeEntity(String formCode, WorkflowType status) {
		String returnFormCode = formCode;
		switch (status) {
			case NEW: {
				if (formCode.startsWith("STest")) {
					returnFormCode = "SelfTest";
				} else if (formCode.startsWith("Workup")) {
					returnFormCode = "Workup";
				}
				break;
			}
			case STATUS: {

				break;
			}
			default:
				break;
		}

		return returnFormCode;
	}

	public String getValidationPartFromJson(String formCode, String NewForm) {
		String path;
		/*String wfFormCode = getWFFormCodeEntity(formCode, WorkflowType.NEW);
		if (formCode.startsWith("STest") || formCode.startsWith("Workup")) {
			path = wfFormCode.toLowerCase() + "_new.json";
		} else {
			path = wfFormCode.toLowerCase() + "_new.json";
		}*/
		path = "new.json";
		String valid = generalUtil.getWfValidation(path, formCode, NewForm);
		return valid;
	}

	private void removeUnuseStates(String formCode, String formId, String userId, Map<String, String> formParam,
			List<String> wfNames, WorkflowType wfType, StringBuilder msgBuilder) {
		String status_ = "";
		String statusLogOrder = "- ";
		String msg;
		String entityFormCode = formDao.getFormCodeEntityBySeqId(formCode, formId);
		switch (wfType) {
			//********** NEW ******************* 
			case NEW:
				//**** formCode: InvItemInstrument 
				if (formCode.equals("InvItemInstrument")) {
					//get status_
					status_ = generalUtil.getNull(formParam.get("STATUSNAME"));
					//check status_ not empty...
					if (!status_.equals("")) {
						//not New and not Active
						if (!status_.equals("New") && !status_.equals("Active")) {
							msg = generalUtil.getSpringMessagesByKey(
									statusLogOrder + "Status of instrument is " + status_ + ".Calibration is removed from the list.",
									"");
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
									msgBuilder);
							generalUtilLogger.logWriter(LevelType.DEBUG,
									"Status of instrument is " + status_ + ".Calibration is removed from the list.",
									ActivitylogType.WorkFlowNew, formId);
							wfNames.remove("InvItemCalibration");
						}
						//not Malfunction and not Active
						if (!status_.equals("Malfunction") && !status_.equals("Active")) {
							msg = generalUtil.getSpringMessagesByKey(
									statusLogOrder + "Status of instrument is " + status_ + ".Maintenance is removed from the list.",
									"");
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
									msgBuilder);
							generalUtilLogger.logWriter(LevelType.DEBUG,
									"Status of instrument is " + status_ + ".Maintenance is removed from the list.",
									ActivitylogType.WorkFlowNew, formId);
							wfNames.remove("InvItemMaintenance");
						}
					}
				}

				if (formCode.equals("SubProject")) {
					// Check if Planning required of the project is not checked or Planning level<1 => remove SubProject
					String projectId = generalUtil.getNull(formParam.get("PROJECT_ID"));
					String sql = String.format(
							"select plnlv.PLANNINGLEVELNAME,t.PLANNINGREQUIRED from FG_S_PROJECT_ALL_V t,fg_s_planninglevel_all_v plnlv where t.PLANNINGLEVEL_ID=plnlv.PLANNINGLEVEL_ID and t.PROJECT_ID = '%1$s' ",
							projectId);
					Map<String, String> metaMap = generalDao.sqlToHashMap(sql);
					if (!metaMap.isEmpty()) {
						int planningRequired = Integer.parseInt(metaMap.get("PLANNINGREQUIRED"));
						//					int planningLevel=Integer.parseInt(metaMap.get("PLANNINGLEVELNAME"));
						String planningLevel = metaMap.get("PLANNINGLEVELNAME");
						if (planningRequired == 0
								|| !generalUtil.getNull(planningLevel).equalsIgnoreCase("Sub Project")) {
							msg = generalUtil.getSpringMessagesByKey(
									statusLogOrder + "Planning required is not checked or Planning level not set to Sub Project. Experiment Series is removed from the list.",
									"");
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
									msgBuilder);
							wfNames.remove("ExperimentSeries");
						}
					} else {
						msg = generalUtil.getSpringMessagesByKey(
								statusLogOrder + "Problem in Planning required and Planning level evaluation. Experiment Series is removed from the list.",
								"");
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
								msgBuilder);
						wfNames.remove("ExperimentSeries");
					}
				}
				if (formCode.equals("SubSubProject")) {
					// Check if Planning required of the project is not checked or Planning level<2 => remove SubSubProject
					String projectId = generalUtil.getNull(formParam.get("PROJECT_ID"));
					String sql = String.format(
							"select plnlv.PLANNINGLEVELNAME,t.PLANNINGREQUIRED from FG_S_PROJECT_ALL_V t,fg_s_planninglevel_all_v plnlv where t.PLANNINGLEVEL_ID=plnlv.PLANNINGLEVEL_ID and t.PROJECT_ID = %1$s ",
							projectId);
					Map<String, String> metaMap = generalDao.sqlToHashMap(sql);
					if (!metaMap.isEmpty()) {
						int planningRequired = Integer.parseInt(metaMap.get("PLANNINGREQUIRED"));
						//					int planningLevel=Integer.parseInt(metaMap.get("PLANNINGLEVELNAME"));
						String planningLevel = metaMap.get("PLANNINGLEVELNAME");
						if (planningRequired == 0
								|| !generalUtil.getNull(planningLevel).equalsIgnoreCase("Sub Sub Project")) {
							msg = generalUtil.getSpringMessagesByKey(
									statusLogOrder + "Planning required is not checked or Planning level not set to Sub Sub Project. Experiment Series is removed from the list.",
									"");
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
									msgBuilder);
							wfNames.remove("ExperimentSeries");
						}
					} else {
						msg = generalUtil.getSpringMessagesByKey(
								statusLogOrder + "Problem in Planning required and Planning level evaluation. Experiment Series is removed from the list.",
								"");
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
								msgBuilder);
						wfNames.remove("ExperimentSeries");
					}

				}
				if (entityFormCode.equals("Experiment")) {
					//get status_
					status_ = generalUtil.getNull(formParam.get("EXPERIMENTSTATUSNAME"));
					//check status_ not empty...
					/*if (!status_.equals("")) {
						// experiment status is Planned => all new forms are removed except for Step
						if(status_.equals("Planned")){
							logger.info("Status of experiment is " + status_ + " sample and request are removed from the list.");
							wfNames.remove("Sample");
							wfNames.remove("Request");
						}
						// status of experiment is Finished, Completed or Failed => step, sample and request are removed
						else if (!status_.equals("active")){//equals("Finished") || status_.equals("Completed") || status_.equals("Failed")) {
							logger.info("Status of experiment is " + status_ + " step, sample and request are removed from the list.");
							wfNames.remove("Step");//for organic
							wfNames.remove("StepFr");//for formulation
							wfNames.remove("Sample");
							wfNames.remove("Request");
						}
					}*/
					if(formCode.equals("ExperimentAn")||formCode.startsWith("ExperimentPr")){
						String protocol = formDao.getFromInfoLookup("EXPERIMENT", LookupType.ID,
								generalUtil.getNull(formParam.get("EXPERIMENT_ID")), "PROTOCOLTYPENAME");
						msg = generalUtil.getSpringMessagesByKey(
								statusLogOrder + "Protocol Type Name is " + protocol + ". Step is removed from the list.",
								"");
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId, msgBuilder);
						generalUtilLogger.logWriter(LevelType.DEBUG,
								" Protocol Type Name is " + protocol + ". Step is removed from the list.",
								ActivitylogType.WorkFlowNew, formId);
						wfNames.remove("Step");
					}
					if(formCode.equals("ExperimentFor")) {
						String allowNewStep = generalUtil.getNull(formParam.get("ALLOWNEWSTEP"),"No");
						if(allowNewStep.equalsIgnoreCase("no")) {
							msg = generalUtil.getSpringMessagesByKey(
									statusLogOrder + "Enabled spread sheet in sub-Project or no planned composition saved row with filler - Step is removed.",
									"");
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId, msgBuilder);
							generalUtilLogger.logWriter(LevelType.DEBUG,
									" Enabled spread sheet in sub-Project or no planned composition saved row - Step is removed.",
									ActivitylogType.WorkFlowNew, formId);
							if(wfNames.contains("Step")) { // from main screen it is step
								wfNames.remove("Step");
							} else if(wfNames.contains("StepFr")) {// from experiment it is stepfr
								wfNames.remove("StepFr");
							}
						}
					}
					if(formCode.equals("Experiment")) {
						String isEnableSpread =  generalUtil.getNull(formParam.get("ISENABLESPREADSHEET"));
						if(isEnableSpread.equalsIgnoreCase("yes") && hideStepFromSpreadsheet == 1) {
							msg = generalUtil.getSpringMessagesByKey(
									statusLogOrder + "Enabled spread sheet in sub-Project - Step is removed.",
									"");
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId, msgBuilder);
							generalUtilLogger.logWriter(LevelType.DEBUG,
									" Enabled spread sheet in sub-Project - Step is removed.",
									ActivitylogType.WorkFlowNew, formId);
							wfNames.remove("Step");
						}
					}
					removeNewEntitiesByExperimentStatus(entityFormCode, formId, formParam, wfNames, msgBuilder);
				}
				if (formCode.equals("Request")) {
					//get status_
					status_ = generalUtil.getNull(formParam.get("REQUESTSTATUSNAME"));
					//check status_ not empty...
					if (!status_.equals("")) {
						// status of Request is Planned or Cancelled => remove experiment 
						if (status_.equals("Planned") || status_.equals("Cancelled") || status_.equals("Declined")
								|| status_.equals("Approved")) {
							msg = generalUtil.getSpringMessagesByKey(
									statusLogOrder + "Status of request is " + status_ + ". Experiment is removed from the list.",
									"");
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
									msgBuilder);
							generalUtilLogger.logWriter(LevelType.DEBUG,
									"Status of request is " + status_ + " experiment is removed from the list.",
									ActivitylogType.WorkFlowNew, formId);
							wfNames.remove("Experiment");
						} else {
							Map<String, String> userInfoMap = formDao.getUserInfoMap(userId);
							String destLabManager = formDao.getFromInfoLookup("Laboratory", LookupType.ID,
									formParam.get("DESTLAB_ID"), "LAB_MANAGER_ID");
							if (formParam.get("ISINUSE").equals("0")) {
								msg = generalUtil.getSpringMessagesByKey(
										statusLogOrder + "The request is a basic request whitch was splitted to some other requests. Experiment cannot be created from basic request. Experiment is removed from the list.",
										"");
								generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
										msgBuilder);
								generalUtilLogger.logWriter(LevelType.DEBUG,
										"The request is a basic request whitch was splitted to some other requests. Experiment cannot be created from basic request. Experiment is removed from the list.",
										ActivitylogType.WorkFlowNew, formId);
								wfNames.remove("Experiment");
							} else if (formParam.get("DESTUNITNAME").equals("External Tasks")) {
								msg = generalUtil.getSpringMessagesByKey(
										statusLogOrder + "The destination unit is External Tasks. Experiment cannot be created from request whitch destination unit is External Tasks. Experiment is removed from the list.",
										"");
								generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
										msgBuilder);
								generalUtilLogger.logWriter(LevelType.DEBUG,
										"The destination unit is External Tasks. Experiment cannot be created from request whitch destination unit is External Tasks. Experiment is removed from the list.",
										ActivitylogType.WorkFlowNew, formId);
								wfNames.remove("Experiment");
							} else if (!userInfoMap.get("USER_INFO_UNIT_ID").equals(formParam.get("DESTUNIT_ID"))
									&& !userInfoMap.get("USER_INFO_USER_ID").equals(destLabManager)){//else if (!formParam.get("USERSTATE").equals("dest")) {
								generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew,
										statusLogOrder + "Status of request is " + status_ + ", but the user state is "
												+ formParam.get("USERSTATE")
												+ ". Experiment can be created from request by the destination unit members only. Experiment is removed from the list.",
										formId, msgBuilder);
								generalUtilLogger.logWriter(LevelType.DEBUG,
										"Status of request is " + status_ + ", but the user state is "
												+ formParam.get("USERSTATE")
												+ ". Experiment can be created from request by the destination unit members only. experiment is removed from the list.",
										ActivitylogType.WorkFlowNew, formId);
								wfNames.remove("Experiment");
							}
						}
					}
				}
				if (formCode.equals("Step") || formCode.equals("StepFr")|| formCode.equals("StepMinFr")) {
					//get status_
					status_ = generalUtil.getNull(formParam.get("STEPSTATUSNAME"));
					//check status_ not empty...
					if (!status_.equals("")) {
						//status of step is Planned => sample,batch and request are removed
						if (status_.equals("Planned")) {
							msg = generalUtil.getSpringMessagesByKey(statusLogOrder + "The Status of step is " + status_
									+ ". Request, Batch and Sample are removed from the list.", "");
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
									msgBuilder);
							generalUtilLogger.logWriter(LevelType.DEBUG,
									"Status of step is " + status_
											+ " Request, Batch and Sample are removed from the list.",
									ActivitylogType.WorkFlowNew, formId);
							wfNames.remove("Sample");
							wfNames.remove("Request");
							wfNames.remove("InvItemBatch");
						}
						//status of step is Cancelled => sample,batch and request are removed
						if (status_.equals("Cancelled")) {
							msg = generalUtil.getSpringMessagesByKey(statusLogOrder + "The Status of step is " + status_
									+ ". Request, Batch, Sample and Action are removed from the list.", "");
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
									msgBuilder);
							generalUtilLogger.logWriter(LevelType.DEBUG,
									"Status of step is " + status_ + " sample and action are removed from the list.",
									ActivitylogType.WorkFlowNew, formId);
							wfNames.remove("Sample");
							wfNames.remove("Action");
							wfNames.remove("Request");
							wfNames.remove("InvItemBatch");
						}
						//status of step is Finished => action and request are removed
						if (status_.equals("Finished")) {
							msg = generalUtil.getSpringMessagesByKey(statusLogOrder + "The Status of step is " + status_
									+ ". Request and Action are removed from the list.", "");
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
									msgBuilder);
							generalUtilLogger.logWriter(LevelType.DEBUG,
									"The Status of step is " + status_ + " action is removed from the list.",
									ActivitylogType.WorkFlowNew, formId);
							wfNames.remove("Action");
							wfNames.remove("Request");
						}

					}
					removeNewEntitiesByExperimentStatus("Step", formId, formParam, wfNames, msgBuilder);
				} else if (formCode.equals("Action") || entityFormCode.equals("SelfTest")) {
					removeNewEntitiesByExperimentStatus(entityFormCode, formId, formParam, wfNames, msgBuilder);
					status_ = generalUtil.getNull(formParam.get("STEPSTATUSNAME"));
					//status of step is Planned => sample,batch is removed
					if (status_.equals("Planned")) {
						msg = generalUtil.getSpringMessagesByKey(statusLogOrder + "Status of parental step is " + status_
								+ ". Sample and Batch are removed from the list.", "");
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
								msgBuilder);
						generalUtilLogger.logWriter(LevelType.DEBUG,
								"Status of parental step is " + status_
										+ " Sample,Request and Batch are removed from the list.",
								ActivitylogType.WorkFlowNew, formId);
						wfNames.remove("Sample");
						wfNames.remove("Request");
						wfNames.remove("InvItemBatch");
					}
					if (formCode.equals("Action")) {
						String expVersion = formDao.getFromInfoLookup("EXPERIMENT", LookupType.ID,
								generalUtil.getNull(formParam.get("EXPERIMENT_ID")), "EXPERIMENTVERSION");
						status_ = generalUtil.getNull(formParam.get("STEPSTATUSNAME"));
						String selftestCount = generalDao.selectSingleString(
								"select count(*) from fg_s_selftest_v st where  st.ACTION_ID = '" + formId + "'");
						String workupCount = generalDao.selectSingleString(
								"select count(*) from fg_s_workup_v w where  w.ACTION_ID = '" + formId + "'");

						//status of step is not active => selftest is removed
						if (!status_.equals("Active")) {
							msg = generalUtil
									.getSpringMessagesByKey(
											statusLogOrder + "Status of parental step is " + status_ + ". SelfTest"
													+ (!status_.isEmpty() && !status_.equals("Planned")
															? " and Workup are" : " is")
													+ " removed from the list.",
											"");
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
									msgBuilder);
							wfNames.remove("SelfTest");
							wfNames.remove("SelfTestMain");
							if (!status_.isEmpty() && !status_.equals("Planned")) {
								wfNames.remove("Workup");//fixed bug in task 15350
							}

						}
						String protocolTypeId = generalDao.selectSingleString(
								"select PROTOCOLTYPE_ID from FG_S_EXPERIMENT_V where EXPERIMENT_ID = '"
										+ generalUtil.getNull(formParam.get("EXPERIMENT_ID")) + "'");
						String protocolTypeName = formDao.getFromInfoLookup("ProtocolType", LookupType.ID,
								protocolTypeId, "name");
						if (protocolTypeName.equals("Formulation")) {
							msg = generalUtil.getSpringMessagesByKey(
									statusLogOrder + "Protocol Type Name is Formulation. Workup is removed from the list.", "");
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
									msgBuilder);
							wfNames.remove("Workup");
						} else if (!expVersion.equals("01")) {
							msg = generalUtil.getSpringMessagesByKey(
									statusLogOrder + "Workup is removed from the list since approved experiment return to active status.",
									"");
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
									msgBuilder);
							wfNames.remove("Workup");
						} else if (!generalUtil.getNull(selftestCount).equals("0")) {
							msg = generalUtil.getSpringMessagesByKey(
									statusLogOrder + "Self-Test already exists for this Action. Workup can not be created.", "");
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
									msgBuilder);
							wfNames.remove("Workup");
						}
						if (!generalUtil.getNull(selftestCount).equals("0")) {
							msg = generalUtil.getSpringMessagesByKey(
									statusLogOrder + "Self-Test already exists for this Action. Additional Self-Test cannot be created.",
									"");
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
									msgBuilder);
							wfNames.remove("SelfTest");
							wfNames.remove("SelfTestMain");
						} else if (!generalUtil.getNull(workupCount).equals("0")) {
							msg = generalUtil.getSpringMessagesByKey(
									statusLogOrder + "Workup already exists for this Action. Self-Test can not be created.", "");
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
									msgBuilder);
							wfNames.remove("SelfTest");
							wfNames.remove("SelfTestMain");
						}
					}
				} else if (formCode.equals("WorkupDistillation") || formCode.equals("WorkupFeeding")
						|| formCode.equals("WorkupCrystallize") || formCode.equals("WorkupDrying")) {
					String stageName = generalUtil.getNull(formParam.get("STAGESTATUSNAME"));
					if (!stageName.equals("Monitoring")) {
						msg = generalUtil.getSpringMessagesByKey(
								statusLogOrder + "Stage of workup " + generalUtil.getNull(formParam.get("WorkupTypeName")) + " is "
										+ stageName
										+ ".Sample is removed from the list.It can be created from 'Monitoring' stagr only.",
								"");
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
								msgBuilder);
						generalUtilLogger.logWriter(LevelType.DEBUG,
								"Stage of workup " + generalUtil.getNull(formParam.get("WorkupTypeName")) + " is "
										+ stageName
										+ ".Sample is removed from the list. It can be created from 'Monitoring' stage only.",
								ActivitylogType.WorkFlowNew, formId);
						wfNames.remove("Sample");
					}
				} else if (formCode.equals("WorkupFiltration")) {
					String stageName = generalUtil.getNull(formParam.get("STAGESTATUSNAME"));
					if (stageName.equals("General Data Input")) {
						msg = generalUtil.getSpringMessagesByKey(
								statusLogOrder + "Stage of workup " + generalUtil.getNull(formParam.get("WorkupTypeName")) + " is "
										+ stageName + ".Sample is removed from the list.",
								"");
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
								msgBuilder);
						generalUtilLogger.logWriter(LevelType.DEBUG,
								"Stage of workup " + generalUtil.getNull(formParam.get("WorkupTypeName")) + " is "
										+ stageName + ".Sample is removed from the list.",
								ActivitylogType.WorkFlowNew, formId);
						wfNames.remove("Sample");
					}
				} else if (formCode.equals("WorkupWashExtract")) {
					String stageName = generalUtil.getNull(formParam.get("STAGESTATUSNAME"));
					if (!stageName.equals("Separation") && !stageName.equals("Interphase Formation")) {
						msg = generalUtil.getSpringMessagesByKey(
								statusLogOrder + "Stage of workup " + generalUtil.getNull(formParam.get("WorkupTypeName")) + " is "
										+ stageName + ".Sample is removed from the list.",
								"");
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
								msgBuilder);
						generalUtilLogger.logWriter(LevelType.DEBUG,
								"Stage of workup " + generalUtil.getNull(formParam.get("WorkupTypeName")) + " is "
										+ stageName + ".Sample is removed from the list.",
								ActivitylogType.WorkFlowNew, formId);
						wfNames.remove("Sample");
					}
				} else if (formCode.equals("Template")) {
					String protocolName = generalUtil.getNull(formParam.get("PROTOCOLTYPE"));
					String tempStatus = formDao.getFromInfoLookup("templatestatus", LookupType.ID,
							formParam.get("STATUS_ID"), "name");
					if (!protocolName.equals("Organic") && !protocolName.equals("Formulation") && !protocolName.equals("Continuous Process")) {
						msg = generalUtil.getSpringMessagesByKey(
								statusLogOrder + "Protocol of template is " + generalUtil.getNull(formParam.get("protocolType"))
										+ ".Request is removed from the list.",
								"");
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
								msgBuilder);
						generalUtilLogger
								.logWriter(LevelType.DEBUG,
										"Protocol of template is " + generalUtil.getNull(formParam.get("protocolType"))
												+ ".Request is removed from the list.",
										ActivitylogType.WorkFlowNew, formId);
						wfNames.remove("Request");
					} else if (!tempStatus.equals("Approved")) {
						msg = generalUtil.getSpringMessagesByKey(
								statusLogOrder + "Status of template is not Approved. Request is removed from the list.", "");
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
								msgBuilder);
						generalUtilLogger.logWriter(LevelType.DEBUG,
								"Status of template is not Approved. Request is removed from the list.",
								ActivitylogType.WorkFlowNew, formId);
						wfNames.remove("Request");
					}
				}
				if (entityFormCode.equalsIgnoreCase("Workup")) {
					if (!formCode.equals("WorkupWashExtract")) {
						msg = generalUtil.getSpringMessagesByKey(
								statusLogOrder + "Another Washing workup can be created from completed washing workup only. The type of the current workup is "
										+ generalUtil.getNull(formParam.get("WORKUPTYPENAME"))
										+ ". Workup is removed from the list.",
								"");
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
								msgBuilder);
						generalUtilLogger.logWriter(LevelType.DEBUG,
								" Another Washing workup can be created from completed washing workup only. The type of the current workup is "
										+ generalUtil.getNull(formParam.get("WORKUPTYPENAME"))
										+ ". Workup is removed from the list.",
								ActivitylogType.WorkFlowNew, formId);
						wfNames.remove("Workup");
					} else if (formCode.equals("WorkupWashExtract")) {
						if (!generalUtil.getNull(formParam.get("STATUSNAME")).equals("Completed")) {
							msg = generalUtil.getSpringMessagesByKey(
									statusLogOrder + "Another Washing workup can be created from completed washing workup only. The current status is "
											+ generalUtil.getNull(formParam.get("STATUSNAME"))
											+ ". Workup is removed from the list.",
									"");
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
									msgBuilder);
							generalUtilLogger.logWriter(LevelType.DEBUG,
									"Another Washing workup can be created from completed washing workup only. The current status is "
											+ generalUtil.getNull(formParam.get("STATUSNAME"))
											+ ". Workup is removed from the list.",
									ActivitylogType.WorkFlowNew, formId);
							wfNames.remove("Workup");
						}
					}
					status_ = generalDao.selectSingleString(
							"select distinct stepstatusname from fg_s_action_all_v where action_id = '"
									+ formParam.get("ACTION_ID") + "'");
					if (status_.equals("Planned")) {
						msg = generalUtil.getSpringMessagesByKey(statusLogOrder + "Status of ascending step is " + status_
								+ ". Sample and Batch are removed from the list.", "");
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
								msgBuilder);
						generalUtilLogger.logWriter(LevelType.DEBUG,
								"Status of ascending step is " + status_
										+ " Sample and Batch are removed from the list.",
								ActivitylogType.WorkFlowNew, formId);
						wfNames.remove("Sample");
						wfNames.remove("InvItemBatch");
					}
				}if(entityFormCode.equalsIgnoreCase("InvItemMaterial")) {
					status_ = generalDao.selectSingleString(
							"select distinct t.MaterialStatusName from FG_S_"+formCode+"_ALL_V t where formid = '"
									+ formParam.get("INVITEMMATERIAL_ID") + "'");
					if(generalUtil.getNull(status_).equals("Cancelled")){//fixed bug 7650
						msg = generalUtil.getSpringMessagesByKey(statusLogOrder + "Status of Material is " + status_
								+ ". Batch is removed from the list.", "");
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
								msgBuilder);
						generalUtilLogger.logWriter(LevelType.DEBUG,
								"Status of material is " + status_
										+ " Batch is removed from the list.",
								ActivitylogType.WorkFlowNew, formId);
						wfNames.remove("Sample");
						wfNames.remove("InvItemBatch");
					}
				}
				if(formCode.equals("Sample")) {
					status_ = generalDao.selectSingleString(
							"select distinct t.SAMPLESTATUSNAME from FG_S_Sample_ALL_V t where formid = '"
									+ formParam.get("SAMPLE_ID") + "'");
					if(!generalUtil.getNull(status_).equals("Active")){
						msg = generalUtil.getSpringMessagesByKey(statusLogOrder + "Status of Sample is " + status_
								+ ". Request is removed from the list.", "");
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
								msgBuilder);
						generalUtilLogger.logWriter(LevelType.DEBUG,
								"Status of Sample is " + status_
										+ " Request is removed from the list.",
								ActivitylogType.WorkFlowNew, formId);
						wfNames.remove("Request");
					}
				}
				if(wfNames.contains("Request")){
					String project_id = formDao.getFromInfoLookup(entityFormCode, LookupType.ID,
							formId, "PROJECT_ID"); //fix bug 8699 - using entityFormCode instead of formcode
					String defaultRequest = generalDao.selectSingleStringNoException("select max(t.USEASDEFAULTDATA) from FG_S_REQUEST_V t where t.PROJECT_ID = '"+generalUtil.getNull(project_id)+"' and t.CREATOR_ID = '"+userId+"'");
					if(generalUtil.getNull(defaultRequest).equals("1")){
						wfNames.remove("Request");
						wfNames.add("Request");
						wfNames.add("Request (Copy Default)");
					}
					else{
						msg = generalUtil.getSpringMessagesByKey(
								statusLogOrder + "Default request does not exist for specific project and user. Request (Copy Default) is removed from the list.",
								"");
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId,
								msgBuilder);
						generalUtilLogger.logWriter(LevelType.DEBUG,
								"Default request does not exist for specific project and user. Request (Copy Default) is removed from the list.",
								ActivitylogType.WorkFlowNew, formId);
					}
				}
				break;
			//********** STATUS ******************* 
			case STATUS:
				//**** formCode: Experiment 
				if (formCode.equals("Experiment") || formCode.equals("ExperimentCP")) {
					String isEnableSpread =  generalUtil.getNull(formParam.get("ISENABLESPREADSHEET"));
					//get status_
					String experimentstatus = formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.ID,
							generalUtil.getNull(formParam.get("STATUS_ID")), "name");
					//get ids...
					Map<String, String> statusMap = formDao.getFromInfoLookupAllElementData("EXPERIMENTSTATUS",
							LookupType.NAME, "id");
					//check case...
					// -- empty or planned
					if ((experimentstatus.equals("") || experimentstatus.equals("Planned")) && isEnableSpread.equalsIgnoreCase("no")) {
						msg = generalUtil.getSpringMessagesByKey(
								statusLogOrder
										+ "Active is removed from the list since it can be changed to by system only, and not by user.",
								"");
						generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId,
								msgBuilder);
						wfNames.remove(statusMap.get("Active"));
					}
					// -- Active
					else if (experimentstatus.equals("Active")) {
						msg = generalUtil.getSpringMessagesByKey(
								statusLogOrder
										+ "Finished is removed from the list since it can be changed to by system only, and not by user.",
								"");
						generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId,
								msgBuilder);
						wfNames.remove(statusMap.get("Finished"));
						//wfNames.remove(statusMap.get("Failed"));
					}

				}
				if(formCode.equals("ExperimentFor")){
					//get status_
					String experimentstatus = formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.ID,
							generalUtil.getNull(formParam.get("STATUS_ID")), "name");
					//get ids...
					Map<String, String> statusMap = formDao.getFromInfoLookupAllElementData("EXPERIMENTSTATUS",
							LookupType.NAME, "id");
					//check case...
					// -- empty or planned
					if (experimentstatus.equals("") || experimentstatus.equals("Planned")) {
						String ownerId = formParam.get("OWNER_ID");
						String creatorId = formParam.get("CREATOR_ID");
						if(!userId.equals(ownerId) && !userId.equals(creatorId)){
							msg = generalUtil.getSpringMessagesByKey(
									statusLogOrder
											+ "Active is removed from the list since it can be changed to by the owner or the creator only.",
									"");
							generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId,
									msgBuilder);
							wfNames.remove(statusMap.get("Active"));
						}
					} 
					
					//check if the user has reopen permissions (for back to active for non planned and from approved to finished)
					if (!experimentstatus.equals("Planned")) {
						Map<String,String> perMap = generalUtilPermission.getPermissionMap(userId, "Experiment", formId, "specificResponse", "");
						boolean isReopenPermission = perMap != null && perMap.containsKey("PERMISSION_ACCESS")&& perMap.get("PERMISSION_ACCESS").contains("o");
						if(!isReopenPermission) {
							msg = generalUtil.getSpringMessagesByKey(
									statusLogOrder
											+ "Active is removed from the list since the user doesn't have a reopen permission (defined in permission scheme)",
									"");
							generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId,
									msgBuilder);
							wfNames.remove(statusMap.get("Active"));
							
							//if Approved we remove also Finished
							if(experimentstatus.equals("Approved")) {
								msg = generalUtil.getSpringMessagesByKey(
										statusLogOrder
												+ "Finished is removed from the list since the user doesn't have a reopen permission (defined in permission scheme)",
										"");
								generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
								generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId,
										msgBuilder);
								wfNames.remove(statusMap.get("Finished"));
							}
						}
					}
					
				}
				//**** entity: Experiment 
				if (entityFormCode.equals("Experiment")) {
					Map<String, String> statusMap = formDao.getFromInfoLookupAllElementData("EXPERIMENTSTATUS",
							LookupType.NAME, "id");
					String expVersion = formDao.getFromInfoLookup("EXPERIMENT", LookupType.ID,
							generalUtil.getNull(formParam.get("EXPERIMENT_ID")), "EXPERIMENTVERSION");
					String approverId = formParam.get("APPROVER_ID");
					//				String ownerId = formParam.get("OWNER_ID");
					String experimentLab = formParam.get("LABORATORY_ID");
					String userLab = formDao.getFromInfoLookup("USER", LookupType.ID, userId, "LABORATORY_ID");
					//String teamLeaderId = formDao.getFromInfoLookup("USER", LookupType.ID, generalUtil.getNull(ownerId), "TEAMLEADER_ID");
					//String userRole = formDao.getFromInfoLookup("USER", LookupType.ID, generalUtil.getNull(ownerId), "USERROLENAME");
					String experimentstatus = formDao.getFromInfoLookup("EXPERIMENTSTATUS", LookupType.ID,
							generalUtil.getNull(formParam.get("STATUS_ID")), "name");
					if (experimentstatus.equals("Completed") && !userId.equals(generalUtil.getNull(approverId))) {
						msg = generalUtil.getSpringMessagesByKey(
								statusLogOrder
										+ "Approved is removed from the list since the experiment can be approved by the approver only.",
								"");
						generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId,
								msgBuilder);
						wfNames.remove(statusMap.get("Approved"));
					}
					if (experimentstatus.equals("Approved")
							&& !generalUtil.getNull(experimentLab).equals(generalUtil.getNull(userLab))//Each lab member of the experiment can return to active
					/*&& !(userId.equals(generalUtil.getNull(ownerId)) && (userRole.equals("Team Leader")||userRole.equals("Super user")||userRole.equals("Admin")))//the owner itself can return to active-if he is a team leader or above
					&& !userId.equals(generalUtil.getNull(teamLeaderId))*/ ) {//the team leader of the the owner can return to active
						msg = generalUtil.getSpringMessagesByKey(
								statusLogOrder
										+ "Active is removed from the list since it can be changed to by lab member only.",
								"");
						generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId,
								msgBuilder);
						wfNames.remove(statusMap.get("Active"));
					}
					if (!expVersion.equals("01")) {
						msg = generalUtil.getSpringMessagesByKey(
								statusLogOrder
										+ "Cancelled and Failed are removed from the list since experiment version is greater than or equal to 2.",
								"");
						generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId,
								msgBuilder);
						wfNames.remove(statusMap.get("Cancelled"));
						wfNames.remove(statusMap.get("Failed"));
					}
				}
				//**** formCode: Step
				else if (formCode.equals("Step")) {
					if (formParam.get("STEPSTATUSNAME") != null) {
						Map<String, String> statusMap = formDao.getFromInfoLookupAllElementData("STEPSTATUS",
							LookupType.NAME, "id");
						String protocolType = formParam.get("PROTOCOLTYPENAME");
						if(protocolType.equals("Continuous Process")){
							if(formParam.get("STEPSTATUSNAME").equals("Planned")
									&&generalUtil.getEmpty(formParam.get("PREP_RUN_DEFAULT"),formParam.get("PREPARATION_RUN")).equals("Run")){
								msg = statusLogOrder
										+ "Active is removed from the list since the system activates the step when starting the run";
						generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus,
								formId);
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg,
								formId, msgBuilder);
						wfNames.remove(statusMap.get("Active"));
					}
				}
//						
						//step WF - remove active to Finished validation in self test fields (part of task 2453) yp 05122019
//						if (formParam.get("STEPSTATUSNAME").equals("Active")) {
//							String sTestCancalledId = formDao.getFromInfoLookup("SelfTestStatus", LookupType.NAME, "Cancelled", "id");
//							
//							List<String> descendantSelftestList = generalDao.getListOfStringBySql(
//									"select t.formid from fg_s_selftest_all_v t,fg_s_action_all_v a where nvl(t.status_id,'0') <> '" + sTestCancalledId + "' and t.active = 1 and t.ACTION_ID =  a.ACTION_ID and a.STEP_ID = '"
//											+ formParam.get("STEP_ID") + "'");
//							String selftestCsv = generalUtil
//									.replaceLast(descendantSelftestList.toString().replaceFirst("\\[", ""), "]", "")
//									.replace(", ", ",");
//							selftestCsv = selftestCsv.isEmpty() ? "'" + selftestCsv + "'" : selftestCsv;
//
//							String sql = String
//									.format("select  LISTAGG(no_item, ',') WITHIN GROUP (ORDER BY no_item)" + " from ("
//							//check if descendant selftest has at least one instrument
//											+ " select distinct decode(count(t.FORMID) over (partition by s.formid),0,'instrument',null) as no_item"
//											+ " from fg_s_instrumentselect_all_v t," + " fg_s_selftest_pivot s"
//											+ " where t.PARENTID(+) = s.FORMID" + " and t.SESSIONID is null"
//											+ " and   s.formid in (%1$s)" + " and s.active=1" + " union all"
//											//check if descendant selftest has at least one column // yp 17072018 fix bug 6023  - check columns only for 'Internal Analytical' self tests
//											+ " select distinct decode(count(t.FORMID) over (partition by s.formid),0,'column',null)"
//											+ " from fg_s_columnselect_all_v t,"
//											+ " fg_s_selftest_pivot s,  fg_s_selftesttype_all_v selftype"
//											+ " where t.PARENTID(+) = s.FORMID" + " and t.SESSIONID is null"
//											+ " and s.formid in (%1$s)"
//											+ " and s.TYPE_ID = selftype.SELFTESTTYPE_ID and lower(selftype.SELFTESTTYPENAME) = lower('Internal Analytical')"
//											+ " and s.active=1" + " union all"
//											//check if descendant selftest has at least one chromatogram/document 
//											+ " select distinct decode(count(t.FORMID) over (partition by s.formid),0,'document',null)"
//											+ " from fg_s_document_all_v t," + " fg_s_selftest_pivot s"
//											+ " where t.PARENTID(+) = s.FORMID" + " and t.SESSIONID is null"
//											+ " and s.formid in (%1$s)" + " and s.active=1" + " union all"
//											//check if descendant selftest has at least one result
//											+ " select distinct decode(count(t.FORMID) over (partition by s.formid),0,'result',null)"
//											+ " from fg_s_resultref_all_v t," + " fg_s_selftest_pivot s"
//											+ " where t.PARENTID(+) = s.FORMID" + " and t.SESSIONID is null"
//											+ " and   s.formid in (%1$s)" + " and s.active=1" + " union all"
//											//if descendant selftest has at a summary
//											+ " select distinct nvl2(s.summary,null,'summary') from fg_s_selftest_all_v s"
//											+ " where s.formid in (%1$s))", selftestCsv);
//							String missingMandatoryFieldList = generalDao.selectSingleString(sql);
//							if (missingMandatoryFieldList != null && !missingMandatoryFieldList.isEmpty()) {
//								//not all descendant selftests have the mandatory elements to enable activating the step
//								msg = generalUtil.getSpringMessagesByKey(
//										statusLogOrder
//												+ "Finished is removed from the list since not all descendant selftest has instruments,columns,results,documents or summary -\n\tInstruments,Columns,Documents,Results and summary in selftest are mandatory for changing status of parental step to Finished.\n Missing fields are: ",
//										"") + missingMandatoryFieldList;
//								generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus,
//										formId);
//								generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg,
//										formId, msgBuilder);
//								wfNames.remove(statusMap.get("Finished"));
//							}
//						}
						
						/*if (formParam.get("STEPSTATUSNAME").equals("Planned")) {//ta fixed bug 5873
								List<String> descendantActionList = generalDao.getListOfStringBySql(
										"select a.formid from fg_s_action_all_v a where a.STEP_ID = '"
												+ formParam.get("STEP_ID") + "'");
								String actionCsv = generalUtil.replaceLast(descendantActionList.toString().replaceFirst("\\[", ""), "]", "").replace(", ", ",");
								actionCsv = actionCsv.isEmpty()?"'"+actionCsv+"'":actionCsv;//instruction
								String sql = String.format("select  LISTAGG(no_item, ',') WITHIN GROUP (ORDER BY no_item)"
									+" from ("
									//check if descendant action has instruction
							        +" select distinct decode(count(*),0,'','instruction') as no_item"
							        +" from fg_s_action_pivot a"
							        +" where a.instruction is null"
							        +" and a.formid in (%1$s))",actionCsv);
								String missingMandatoryFieldList = generalDao.selectSingleString(sql);
								if (missingMandatoryFieldList!=null && !missingMandatoryFieldList.isEmpty()) {
									//not all descendant actions have the mandatory elements to enable activating the step
									msg = statusLogOrder+"Active is removed from the list since not all descendant actions has instruction -\n\tInstruction in action is mandatory for changing status of parental step to Active.";
									generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
									generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId, msgBuilder);
									wfNames.remove(statusMap.get("Active"));
								}
						}*/
					}
				}
				//**** formCode: StepFr
				else if (formCode.equals("StepFr")||formCode.equals("StepMinFr")) {
					if (formParam.get("STEPSTATUSNAME") != null) {
						Map<String, String> statusMap = formDao.getFromInfoLookupAllElementData("STEPSTATUS",
								LookupType.NAME, "id");
						if (formParam.get("STEPSTATUSNAME").equals("Active")) { 
							String sTestCancalledId = formDao.getFromInfoLookup("SelfTestStatus", LookupType.NAME, "Cancelled", "id");
							
							List<String> descendantSelftestList = generalDao.getListOfStringBySql(
									"select t.formid from fg_s_selftest_all_v t,fg_s_action_all_v a where nvl(t.status_id,'0') <> '" + sTestCancalledId + "' and t.active=1 and t.SELFTESTTYPENAME not like '%Assay%' and t.ACTION_ID = a.ACTION_ID and a.STEP_ID = '"
											+ formParam.get("STEP_ID") + "'");
							if (descendantSelftestList != null && !descendantSelftestList.isEmpty()) {
								String selftestCsv = generalUtil
										.replaceLast(descendantSelftestList.toString().replaceFirst("\\[", ""), "]", "")
										.replace(", ", ",");
								String sql = "select  LISTAGG(no_item, ',') WITHIN GROUP (ORDER BY no_item) from \r\n"
										+ "(select decode(count(*),0,'','comments') as no_item \r\n"
										+ "from fg_s_selftest_all_v s \r\n" + "where s.formid in (" + selftestCsv
										+ ") \r\n" + "and fg_get_richtext_isnull(s.TESTCOMMENT) = 1 " + " union all"
										+ " select distinct decode(count(t.FORMID) over (partition by s.formid),0,'pHResults',null) as no_item"
										+ " from fg_s_phresults_all_v t," + " fg_s_selftest_all_v s"
										+ " where t.PARENTID(+) = s.FORMID" + " and t.SESSIONID is null"
										+ " and s.formid in (" + selftestCsv + ")" + " and s.SELFTESTTYPENAME = 'pH')";
								String missingMandatoryFieldList = generalDao.selectSingleString(sql);
								if (missingMandatoryFieldList != null && !missingMandatoryFieldList.isEmpty()) {
									//not all descendant selftests have comments
									msg = generalUtil.getSpringMessagesByKey(
											statusLogOrder
													+ "Finished is removed from the list since not all descendant selftests have comments -\n\tComments in selftest is mandatory for changing status of parental step to Finished.\n Missing fields are: ",
											"") + missingMandatoryFieldList;
									generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus,
											formId);
									generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg,
											formId, msgBuilder);
									wfNames.remove(statusMap.get("Finished"));
								}
							}
						}
						/*if (formParam.get("STEPSTATUSNAME").equals("Planned")) {//ta fixed bug 5873
							List<String> descendantActionList = generalDao.getListOfStringBySql(
									"select a.formid from fg_s_action_all_v a where a.STEP_ID = '"
											+ formParam.get("STEP_ID") + "'");
							String actionCsv = generalUtil.replaceLast(descendantActionList.toString().replaceFirst("\\[", ""), "]", "").replace(", ", ",");
							actionCsv = actionCsv.isEmpty()?"'"+actionCsv+"'":actionCsv;//instruction
							String sql = String.format("select  LISTAGG(no_item, ',') WITHIN GROUP (ORDER BY no_item)"
								+" from ("
								//check if descendant action has instruction
						    +" select distinct decode(count(*),0,'','instruction') as no_item"
						    +" from fg_s_action_pivot a"
						    +" where a.instruction is null"
						    +" and a.formid in (%1$s))",actionCsv);
							String missingMandatoryFieldList = generalDao.selectSingleString(sql);
							if (missingMandatoryFieldList!=null && !missingMandatoryFieldList.isEmpty()) {
								//not all descendant actions have the mandatory elements to enable activating the step
								msg = statusLogOrder+"Active is removed from the list since not all descendant actions have instructions -\n\tInstruction in action is mandatory for changing status of parental step to Active.";
								generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
								generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId, msgBuilder);
								wfNames.remove(statusMap.get("Active"));
							}
						}*/
					}
				}
				//**** formCode: Request
				else if (formCode.equals("Request")) {
					if (formParam.get("REQUESTSTATUSNAME") != null) {
						Map<String, String> statusMap = formDao.getFromInfoLookupAllElementData("REQUESTSTATUS",
								LookupType.NAME, "id");
						if (formParam.get("REQUESTSTATUSNAME").isEmpty()) {
							formParam.put("REQUESTSTATUSNAME", "Planned");
						}

						if (!formParam.get("USERSTATE").equals("source")
								&& !formParam.get("REQUESTSTATUSNAME").equals("Cancelled")) {
							msg = generalUtil.getSpringMessagesByKey(
									statusLogOrder
											+ "Userstate of request is not the creator or from the source lab team- Waiting /cancelled  is removed",
									"");
							generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId,
									msgBuilder);

							wfNames.remove(statusMap.get("Cancelled"));
							if (formParam.get("REQUESTSTATUSNAME").equals("Planned")) {
								wfNames.remove(statusMap.get("Waiting"));
							}
						}
						if (formParam.get("REQUESTSTATUSNAME").equals("Waiting")) {//ab 19.03.18 changed the conditions to be exact- from the right status
							String labManagerId = formDao.getFromInfoLookup("Laboratory", LookupType.ID,
									formParam.get("DESTLAB_ID"), "LAB_MANAGER_ID");

							if (!userId.equals(labManagerId)) {
								msg = generalUtil.getSpringMessagesByKey(
										statusLogOrder
												+ "The user in the request is not the destination laboratory manager- Declined  is removed",
										"");
								generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus,
										formId);
								generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg,
										formId, msgBuilder);
								wfNames.remove(statusMap.get("Declined"));

							}

							if (!(formParam.get("REQUESTTYPENAME").equals("Analytical Standards")
									&& formParam.get("USERSTATE").equals("dest")
									|| formParam.get("DESTUNITNAME").equals("External Tasks")
											&& formParam.get("USERSTATE").equals("source"))) {
								msg = generalUtil.getSpringMessagesByKey(
										statusLogOrder
												+ "For Analytical Standards Req. and External Req. the status Will be manually changed to ‘In Progress ‘ by destination Lab users Only ",
										"");
								generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus,
										formId);
								generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg,
										formId, msgBuilder);
								wfNames.remove(statusMap.get("In Progress"));
							}
							if (!(formParam.get("DESTUNITNAME").equals("External Tasks"))
									&& formParam.get("USERSTATE").equals("source")) {
								msg = generalUtil.getSpringMessagesByKey(
										statusLogOrder
												+ "There is no option to select Planned status if request is not external. ",
										"");
								generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus,
										formId);
								generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg,
										formId, msgBuilder);
								wfNames.remove(statusMap.get("Planned"));
							}
						}
					}
				} //**** formCode: Template
				else if (formCode.equals("Template")) {
					Map<String, String> statusMap = formDao.getFromInfoLookupAllElementData("TEMPLATESTATUS",
							LookupType.NAME, "id");
					String creatorId = formParam.get("CREATOR");
					String userLab = formDao.getFromInfoLookup("USER", LookupType.ID, userId, "LABORATORY_ID");
					String creatorLab = formDao.getFromInfoLookup("USER", LookupType.ID, creatorId, "LABORATORY_ID");
					if (!generalUtil.getNull(creatorLab).equals(generalUtil.getNull(userLab))) {
						msg = generalUtil.getSpringMessagesByKey(
								statusLogOrder
										+ "Obsolete is removed from the list since it can be changed to by lab member of the template creator only.",
								"");
						generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId,
								msgBuilder);
						wfNames.remove(statusMap.get("Obsolete"));
					}
				} //**** formCode: Self Test
				else if (entityFormCode.equals("SelfTest")) {
					Map<String, String> statusMap = formDao.getFromInfoLookupAllElementData("SELFTESTSTATUS",
							LookupType.NAME, "id");
					status_ = generalUtil.getNull(formParam.get("SELFTESTSTATUSNAME"));
					if(status_ != null && status_.equals("Active") && statusMap.get("Completed") != null && wfNames.contains(statusMap.get("Completed"))) {
						msg = generalUtil.getSpringMessagesByKey(
								statusLogOrder
										+ "Completed is removed from the list since it change by the system when the step become finish (with version 01).",
								"");
						generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId,
								msgBuilder);
						wfNames.remove(statusMap.get("Completed"));
					}
				}
				/**************RECIPE FORMULATION******************/
				else if(formCode.equals("RecipeFormulation")){
					Map<String, String> statusMap = formDao.getFromInfoLookupAllElementData("RecipeSTATUS",
							LookupType.NAME, "id");
					String approverId = formParam.get("APPROVER_ID");
					String creatorId = formParam.get("CREATOR_ID");
					String recipeStatus = formDao.getFromInfoLookup("RECIPESTATUS", LookupType.ID,
							generalUtil.getNull(formParam.get("STATUS_ID")), "name");
					if(userId.equals(approverId)||userId.equals(generalUtil.getNull(creatorId))){
						if(!recipeStatus.equals("Active") && !recipeStatus.equals("Planned") && !recipeStatus.isEmpty()
								&& userId.equals(generalUtil.getNull(creatorId)) && !userId.equals(generalUtil.getNull(approverId))) {
							msg = generalUtil.getSpringMessagesByKey(
									statusLogOrder
											+ "Cancelled is removed from the list since the Recipe creator can cancel the recipe only from statuses planned/Active.",
									"");
							generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId,
									msgBuilder);
							wfNames.remove(statusMap.get("Cancelled"));
						}
						
						if(userId.equals(approverId) && !userId.equals(creatorId) &&
								!recipeStatus.equals("Completed") && !recipeStatus.equals("Approved")){
							msg = generalUtil.getSpringMessagesByKey(
									statusLogOrder
											+ "Cancelled is removed from the list since the Recipe approver can cancel the recipe only from statuses completed/Approved.",
									"");
							generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId,
									msgBuilder);
							wfNames.remove(statusMap.get("Cancelled"));
						}
					} else {
						msg = generalUtil.getSpringMessagesByKey(
									statusLogOrder
											+ "Cancelled is removed from the list since the Recipe creator or approver only can cancel it.",
									"");
							generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId,
									msgBuilder);
							wfNames.remove(statusMap.get("Cancelled"));
					}
					
					if (recipeStatus.equals("Active") && !userId.equals(generalUtil.getNull(approverId))) {
						msg = generalUtil.getSpringMessagesByKey(
								statusLogOrder
										+ "Approved is removed from the list since the recipe can be approved by the approver only.",
								"");
						generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId,
								msgBuilder);
						wfNames.remove(statusMap.get("Approved"));
					} else if (recipeStatus.equals("Completed") && !userId.equals(generalUtil.getNull(approverId))) {//TODO:add the user permitted group
						msg = generalUtil.getSpringMessagesByKey(
								statusLogOrder
										+ "Approved is removed from the list since the recipe can be approved by the approver only.",
								"");
						generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId,
								msgBuilder);
						wfNames.remove(statusMap.get("Approved"));
					} else if((recipeStatus.equals("Approved") || recipeStatus.equals("Completed"))
							&& userId.equals(generalUtil.getNull(approverId))
							&& formParam.get("EXPORTTODATABANK").equals("1")){//the recipe was already exported
						msg = generalUtil.getSpringMessagesByKey(
								statusLogOrder
										+ "Active is removed from the list since the recipe was already exported to the data bank.",
								"");
						generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId,
								msgBuilder);
						wfNames.remove(statusMap.get("Active"));
					}
					
					// remove Active from if Approved / Completed and no reopen permission 
					if((recipeStatus.equals("Approved") || recipeStatus.equals("Completed")) && wfNames.contains(statusMap.get("Active"))) {
						Map<String,String> perMap = generalUtilPermission.getPermissionMap(userId, "RecipeFormulation", formId, "specificResponse", "");
						boolean isReopenPermission = perMap != null && perMap.containsKey("PERMISSION_ACCESS")&& perMap.get("PERMISSION_ACCESS").contains("o");
						if(!isReopenPermission) {
							msg = generalUtil.getSpringMessagesByKey(
									statusLogOrder
											+ "Active is removed from the list since the user doesn't have a reopen permission (defined in permission scheme)",
									"");
							generalUtilLogger.logWriter(LevelType.DEBUG, msg, ActivitylogType.WorkFlowStatus, formId);
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowStatus, msg, formId,
									msgBuilder);
							wfNames.remove(statusMap.get("Active"));
						}
					}
				}
				break;
			default:
				break;
		}
	}

	//	private boolean isWFStatusForm(String formCode) {
	//		// TODO Auto-generated method stub
	//		return (formCode.endsWith("Project") || formCode.equals("InvItemInstrument")
	//				|| formCode.equals("InvItemCalibration") || formCode.equals("InvItemMaintenance")
	//				|| formCode.equals("Experiment") || formCode.equals("Step") || formCode.equals("ExperimentAn")
	//				|| formCode.equals("Request") || formCode.equals("Workup") || formCode.startsWith("Workup"));
	//	}

	//	private Map<String, String> getAdditionalWFList(String formCode, String userId, String formId, boolean isNewFormId,
	//			Map<String, String> formParam) {
	//		Map<String, String> wfMap = new HashMap<String, String>();
	//		if (formCode.startsWith("Workup")) {
	//
	//			String columnName = "STAGESTATUSNAME";
	//			String tableName = "STAGESTATUS";
	//			// TODO Hadasa / Yocheved
	//			// 1) switch by type in map (we will do it in the end.... for example if(formParam.get("type").equals("feeding")) {...} )
	//			// 2) make code like in getFormWFStateParam step 1-4 and return map for "STATUS_WF_WORKUP_STAGE_STATUS_LIST" 
	//			// 3) write me in notes the way WF works ...
	//
	//			//Status wf
	//			try {
	//				if (!formId.equals("-1") && isWFStatusForm(formCode)) { // temporary solution to prevent formBuilder to use this function
	//					getWFState(formCode, userId, formId, isNewFormId, formParam, wfMap, tableName, columnName, true,
	//							"STATUS_WF_WORKUP_STAGE_STATUS_LIST");
	//
	//				} else {
	//					logger.info("WF status message - formCode=" + formCode + " has no status WF");
	//				}
	//			} catch (Exception e) {
	//				generalUtil.LogWriterDB(e);
	//			}
	//
	//		}
	//		return wfMap;
	//	}

	/**
	 * Removes all the new entities that depend on the experiment status
	 * @param formCode
	 * @param formParam
	 * @param wfNames
	 */
	private void removeNewEntitiesByExperimentStatus(String entityFormCode, String formId,
			Map<String, String> formParam, List<String> wfNames, StringBuilder msgBuilder) {
		String msg;
		String statusLogOrder = "- ";
		String experimentStatus = formDao.getFromInfoLookup("EXPERIMENT", LookupType.ID,
				generalUtil.getNull(formParam.get("EXPERIMENT_ID")), "STATUSNAME");
		String lastStatus = formDao.getFromInfoLookup("EXPERIMENT", LookupType.ID,
				generalUtil.getNull(formParam.get("EXPERIMENT_ID")), "LASTSTATUSNAME");
		if (!experimentStatus.equals("Active")) {//equals("Finished") || status_.equals("Completed") || status_.equals("Failed")) {
			// experiment status is Planned => all new forms are removed except for Step
			if (/*entityFormCode.equals("Experiment") && */experimentStatus.equals("Planned")) {
				String protocol = formDao.getFromInfoLookup("EXPERIMENT", LookupType.ID,
						generalUtil.getNull(formParam.get("EXPERIMENT_ID")), "PROTOCOLTYPENAME");
				if (protocol.equalsIgnoreCase("organic") || protocol.equalsIgnoreCase("formulation") || protocol.equalsIgnoreCase("Continuous Process")) {
					msg = generalUtil.getSpringMessagesByKey(
							statusLogOrder + "Status of " + protocol + " experiment is Planned . Sample is removed from the list.",
							"");
					generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId, msgBuilder);
					generalUtilLogger.logWriter(LevelType.DEBUG,
							"Status of " + protocol + " experiment is Planned. Sample is removed from the list.",
							ActivitylogType.WorkFlowNew, formId);
					wfNames.remove("Sample"); //ab 040718- task 14976
				}
				msg = generalUtil.getSpringMessagesByKey(statusLogOrder + "Status of experiment is " + experimentStatus
						+ ". Request and Batch are removed from the list.", "");
				generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId, msgBuilder);
				generalUtilLogger.logWriter(LevelType.DEBUG,
						"Status of experiment is " + experimentStatus
								+ ". Request and Batch are removed from the list.",
						ActivitylogType.WorkFlowNew, formId);
				//wfNames.remove("Sample"); ab 180218- task 14976
				wfNames.remove("Request");
				wfNames.remove("InvItemBatch");
			} else {
				// status of experiment is Finished, Completed or Failed => step, sample and request are removed
				msg = generalUtil.getSpringMessagesByKey(statusLogOrder + "Status of experiment is " + experimentStatus
						+ (entityFormCode.equals("Experiment") || entityFormCode.equals("ExperimentCP") || entityFormCode.equals("Step") ? ". Step, " : ".")
						+ " Request and Batch are removed from the list.", "");
				generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId, msgBuilder);
				generalUtilLogger.logWriter(LevelType.DEBUG,
						"Status of experiment is " + experimentStatus
								+ (entityFormCode.equals("Experiment") || entityFormCode.equals("ExperimentCP") || entityFormCode.equals("Step") ? ". Step, " : ".")
								+ "Request and Batch are removed from the list.",
						ActivitylogType.WorkFlowNew, formId);
				wfNames.remove("Step");//for organic / Continuous Process
				wfNames.remove("StepFr");//for formulation
				//wfNames.remove("Sample");ab 180218- task 14976
				wfNames.remove("Request");
				wfNames.remove("InvItemBatch");
				if (!experimentStatus.equals("Finished")) {
					msg = generalUtil.getSpringMessagesByKey(statusLogOrder + "Status of experiment is " + experimentStatus
							+ " (not Finished). Sample is removed from the list.", "");
					generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId, msgBuilder);
					wfNames.remove("Sample");//ab 250218- task 14976- Irina requirement
				}
			}

		} else {
			String expVersion = formDao.getFromInfoLookup("EXPERIMENT", LookupType.ID,
					generalUtil.getNull(formParam.get("EXPERIMENT_ID")), "EXPERIMENTVERSION");
			String protocol = formDao.getFromInfoLookup("EXPERIMENT", LookupType.ID,
					generalUtil.getNull(formParam.get("EXPERIMENT_ID")), "PROTOCOLTYPENAME");

			if (lastStatus.equals("Approved") || !expVersion.equals("01")) {
				msg = generalUtil.getSpringMessagesByKey(
						statusLogOrder + "Status of experiment is " + experimentStatus
								+ ". Step is removed from the list since approved experiment return to active status.",
						"");
				generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId, msgBuilder);
				wfNames.remove("Step");
				wfNames.remove("StepFr");
			} else if(protocol.equalsIgnoreCase("continuous process") && (entityFormCode.equals("Experiment") || entityFormCode.equals("Step")) && formParam.get("ISRUNSTARTED").equals("1")){
				msg = generalUtil.getSpringMessagesByKey(
						statusLogOrder + "Protocol of experiment is " + protocol + " and the experiment has been started running"
								+ ". Step is removed from the list since step cannot be added in a running experiment.",
						"");
				generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId, msgBuilder);
				wfNames.remove("Step");
			}
			msg = generalUtil.getSpringMessagesByKey(
					statusLogOrder + "Status of experiment is " + experimentStatus + ". Template is removed from the list.", "");
			generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId, msgBuilder);
			wfNames.remove("Template");
		}

		if (!experimentStatus.equals("Approved")) {
			msg = generalUtil.getSpringMessagesByKey(statusLogOrder + "Status of experiment is " + experimentStatus
					+ " (not Approved). Template is removed from the list.", "");
			generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId, msgBuilder);
			wfNames.remove("Template");
			String protocol = formDao.getFromInfoLookup("EXPERIMENT", LookupType.ID,
					generalUtil.getNull(formParam.get("EXPERIMENT_ID")), "PROTOCOLTYPENAME");
			if(protocol.toLowerCase().contains("formulation")){
				msg = generalUtil.getSpringMessagesByKey(statusLogOrder + "Status of experiment is " + experimentStatus
					+ " (not Approved). Recipe is removed from the list.", "");
				generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId, msgBuilder);
				wfNames.remove("RecipeFormulation");
			}
		} else {
			String recipeConnected = formParam.get("RECIPEFORMULATION_ID");
			String sp_EnableSpreadsheet = generalDao.selectSingleStringNoException("select sp.ISENABLESPREADSHEET from fg_s_experiment_v ex, fg_s_subproject_v sp where ex.SUBPROJECT_ID = sp.formid and ex.formid = '" +formId +"'");
			if(!recipeConnected.isEmpty()){
				msg = generalUtil.getSpringMessagesByKey(statusLogOrder + "The experiment is already connected to a recipe. Creating another recipe is not allowed"
					+ " . Recipe is removed from the list.", "");
				generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId, msgBuilder);
				wfNames.remove("RecipeFormulation");
			}
			else if(generalUtil.getNull(sp_EnableSpreadsheet).equalsIgnoreCase("yes")) {//fixed bug 9007
				msg = generalUtil.getSpringMessagesByKey(statusLogOrder + "Enabled spread sheet in sub-Project."
					+ " Recipe is removed from the list.", "");
				generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew, msg, formId, msgBuilder);
				wfNames.remove("RecipeFormulation");
			
			}
		}
	}
}
