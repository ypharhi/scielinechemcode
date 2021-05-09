package com.skyline.form.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.dal.FormDao;

@Service
public class FormApiPermissionService {
	
	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private FormDao formDao;
	
	@Autowired
	private GeneralUtilFormState generalUtilFormState;
	
	@Autowired
	private IntegrationWF integrationWF;
	
	@Autowired
	private GeneralUtilLogger generalUtilLogger;
	
	@Autowired
	private GeneralUtilPermission generalUtilPermission;
	
	@Autowired
	private FormService formService;
	 
	/**	 
	 * Get CSV of new available forms for the 'parent Form'
	 * @param requestAction	
	 * @return ActionBean
	 */
	public ActionBean getNewAvailableFormList(ActionBean requestAction) {
		String formCode = "";
		String formId = "-1";
		Map<String, String> hmReportParameterList = new HashMap<String, String>();
		String toReturn = "";
		String errMsg = "";
		try {
			formCode = requestAction.getData().get(0).getVal();
			formId = requestAction.getData().get(1).getVal();
			long stateKey = generalUtil.getNullLong((String) requestAction.getData().get(2).getVal());
			if (generalUtil.getNull(formCode).equals("")) {
				formCode = formDao.getFormCodeBySeqId(formId);
			}
			for (Map.Entry<String, String> entry : generalUtilFormState.getFormParam(stateKey, formCode).entrySet()) {
				String rKey = entry.getKey().replace("$P{", "").replace("}", "");
				String rVal = entry.getValue();
				hmReportParameterList.put(rKey, rVal);
			}
			toReturn = integrationWF.getNewAvailableFormList(stateKey, formCode, formId, hmReportParameterList, "");
		} catch (Exception e) {
			toReturn = "";
			generalUtilLogger.logWriter(LevelType.ERROR,
					"WF error! exception in STATUS_WF_LIST evaluation formCode=" + formCode,
					ActivitylogType.WorkFlowNew, formId, e, null);
			errMsg = "Error in evaluating new states";
		}
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), errMsg);
	}
	
	/**	 
	 * Get CSV of new available forms for id (using by data table)
	 * @param requestAction	
	 * @return ActionBean
	 * Note: make the initiation of the form without render (to have in the maps data for the getNewAvailableFormList as in the forms)
	 */
	public ActionBean getNewAvailableFormListById(ActionBean requestAction) {
		String toReturn = "";
		String errMsg = "";
		String formCode = "";
		String parentFormId = "-1";
		String formIdListCsv = "";
		int i = 1;
		try {
			formCode = requestAction.getData().get(0).getVal();
			parentFormId = requestAction.getData().get(1).getVal();
			formIdListCsv = requestAction.getData().get(2).getVal();
			long stateKey = generalUtil.getNullLong((String) requestAction.getData().get(3).getVal());

			if (formIdListCsv.isEmpty()) {
				toReturn = getNewAvailableFormListById(stateKey, parentFormId, formCode);
			} else { //there was sent a list of id's. The returned list will contain the intersection of the available forms 
				List<String> commonList = new ArrayList<String>();
				String availableListCsv = getNewAvailableFormListById(stateKey, parentFormId, formCode);
				commonList.addAll(availableListCsv.isEmpty() ? new ArrayList<String>()
						: Arrays.asList(availableListCsv.split(",")));
				String[] parentIdList = formIdListCsv.split(",");
				for (i = 1; i < parentIdList.length; i++) {
					if (commonList.isEmpty()) {
						break;
					}
					availableListCsv = getNewAvailableFormListById(stateKey, parentIdList[i], formCode);
					commonList.retainAll(availableListCsv.isEmpty() ? new ArrayList<String>()
							: Arrays.asList(availableListCsv.split(",")));
				}
				toReturn = generalUtil.listToCsv(commonList);
			}
		} catch (Exception e) {
			toReturn = "";
			generalUtilLogger.logWriter(LevelType.ERROR,
					"WF error! exception in STATUS_WF_LIST evaluation formCode=" + formCode,
					ActivitylogType.WorkFlowNew, formIdListCsv.isEmpty() ? parentFormId : formIdListCsv.split(",")[i],
					e, null);
			errMsg = "Error in evaluating new states";
		}
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), errMsg);
	}
	
	public ActionBean getReadPermissionById(ActionBean requestAction) {
		String toReturn = "0";
		String formCode = requestAction.getData().get(0).getVal();
		String formId = requestAction.getData().get(1).getVal();
		String userId = generalUtil.getSessionUserId();
		formCode = getPermFormCode(formCode, formId);

		Map<String, String> permissionMap = generalUtilPermission.getPermissionMap(userId, formCode, formId,
				"specificResponse", "getReadPermissionById");
		if (generalUtil.getNull(permissionMap.get("PERMISSION_ACCESS")).toLowerCase()
				.contains("r") /*&& generalUtilForm.isActiveForm(formId,formCode)*/) {//If the form is deleted, navigation is not allowed
			toReturn = "1";
		}
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}
	
	private String getPermFormCode(String formCode, String formId) {
		if (formCode.equalsIgnoreCase("InvItemMaterial")) {
			return formDao.getFormCodeBySeqId(formId);
		}
		return formCode;
	}
	
	public ActionBean getCreatePermissionFormCode(ActionBean requestAction) {
		String formCode = requestAction.getData().get(0).getVal();
		String userId = generalUtil.getSessionUserId();
		String toReturn = "0";
		try {
			if (generalUtilPermission.isUserInSchemeByCrudl(formCode, userId, "Create")) {
				toReturn = "1";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
		}
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}
	
	/**	 
	 * Get CSV of new available forms for id (using by data table)
	 * @param parentFormId from which the new formCode is to be created
	 * @param formCode the new formCode to be created
	 * @return String
	 * Note: make the initiation of the form without render (to have in the maps data for the getNewAvailableFormList as in the forms)
	 */
	public String getNewAvailableFormListById(long stateKey, String parentFormId, String formCode) {
		String parentFormCode = "";
		String userId = "";
		String toReturn = "";

		userId = generalUtil.getSessionUserId();
		if (generalUtil.getNull(parentFormId).equals("") || generalUtil.getNull(parentFormId).equals("-1")) {
			if (!generalUtilPermission.isUserInSchemeByCrudl(formCode, userId, "Create")) {
				return ""; //create not allowed
			} else {
				toReturn = formCode;
				return toReturn;
			}
		}

		Map<String, String> hmReportParameterList = new HashMap<String, String>();
		parentFormCode = formDao.getFormCodeBySeqId(parentFormId);

		formService.initFormParam(stateKey, parentFormCode, parentFormId, userId, new HashMap<String, String[]>());
		for (Map.Entry<String, String> entry : generalUtilFormState.getFormParam(stateKey, parentFormCode).entrySet()) {
			String rKey = entry.getKey().replace("$P{", "").replace("}", "");
			String rVal = entry.getValue();
			hmReportParameterList.put(rKey, rVal);
		}

		toReturn = integrationWF.getNewAvailableFormList(stateKey, parentFormCode, parentFormId, hmReportParameterList,
				formCode);
		return toReturn;
	}

}
