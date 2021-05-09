package com.skyline.form.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.BeanType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.Form;
import com.skyline.form.bean.FormType;
import com.skyline.form.dal.FormDao;

@Service
public class FormApiDataTableService {
	
	private static final Logger logger = LoggerFactory.getLogger(FormApiDataTableService.class);
 
	@Autowired
	private IntegrationDT integrationDT;
	
	@Autowired
	private GeneralUtil generalUtil;
	
	@Autowired
	private FormDao formDao;
	
	@Autowired
	private IntegrationSaveForm integrationSaveForm;
	  
	public ActionBean onLevelSelectedChange(ActionBean requestAction) {
		String struct = requestAction.getData().get(0).getVal();
		String formCode = requestAction.getData().get(1).getVal();
		String displayCatalog = requestAction.getData().get(2).getVal();
		String elementCode = requestAction.getData().get(3).getVal();
		String toReturn = integrationDT.onLevelSelectedChange(struct, formCode, displayCatalog, elementCode).toString();

		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}
	
	public ActionBean onElementDataTableApiChange(ActionBean requestAction) {
		String struct = requestAction.getData().get(0).getVal();
		String criteria = requestAction.getData().get(1).getVal();
		String display = requestAction.getData().get(2).getVal();
		String linkToLastSelection = requestAction.getData().get(3).getVal();
		String formCode = requestAction.getData().get(4).getVal();
		String tableType = requestAction.getData().get(5).getVal();
		String sourceElementImpCode = requestAction.getData().get(6).getVal();
		String hideEmptyColumns = requestAction.getData().get(7).getVal();
		String lastMultiValues = requestAction.getData().get(8).getVal();
		String formId = requestAction.getData().get(9).getVal(); //TODO key!!!!!!!!!!! formId !!!!!!!!!!
		long stateKey = generalUtil.getNullLong((String) requestAction.getData().get(10).getVal());
		boolean updateMultiValues = generalUtil.getNullBoolean(requestAction.getData().get(11).getVal(), false);
		String followingHiddenCol = generalUtil.getNull(requestAction.getData().get(12).getVal());
		//get structFormType
		FormType structFormType = null;
		try {
			Form structForm = formDao.getFormInfoLookup(struct, "%", true).get(0);
			structFormType = FormType.valueOf(structForm.getFormType());
		} catch (Exception e) {
			logger.warn("no structFormType found for struct=" + struct);
		}

		// bouncer call (consider put it in controller to make less actions in case of ping attack)
		//		String AUTHZ_TABLE_CODE = "";
		//		try {
		//			//TODO call with 3arg userid / struct - the result (some string with letters that represent authorization or SQL where part OR LIST OF IDs (?))
		//			//we add it to the onElementDataTableApiChange as arg (return the table data according to permissions)
		//			String userId = generalUtil.getSessionUserId();
		//			if (formCode.equals("Main"))
		//			{
		//			    //AUTHZ_TABLE_CODE = bouncer.approve(generalUtil.getNullInt(userId,-1), "-1").toString(); //struct if the entity (level) selected in the table ("Project", "SubProject"...)	
		//			}
		//			else
		//			{
		//			    //AUTHZ_TABLE_CODE = bouncer.approve(generalUtil.getNullInt(userId,-1), generalUtilFormState.getFormId(formCode)).toString();
		//			}
		//			
		//		} catch (Exception e) {
		//			logger.error("Bouncer Exception on table!"); //logout?
		//		}
		// bouncer DONE!
		String permissionSqlList = "";
		//		String AUTHZ_JSON = "";
		//		Map<String,String> permissionMap = new HashMap<String,String>();
		//		try {
		//			if(generalUtil.getNull(struct).equals("Project")) {
		//				//TODO call with 3arg userid / struct - the result (some string with letters that represent authorization or SQL where part OR LIST OF IDs (?))
		//				//we add it to the onElementDataTableApiChange as arg (return the table data according to permissions)
		//				String userId = generalUtil.getSessionUserId();
		//				permissionMap = generalUtil.getPermissionMap(AUTHZ_JSON, "generalResponse");
		//				permissionSqlList = generalUtil.getNull(permissionMap.get("PERMISSION_SQL"));
		//			}
		//		} catch (Exception e) {
		//			logger.error("Bouncer Exception on tabel!, AUTHZ_JSON=" + AUTHZ_JSON + ", e=" + e.toString()); //logout?
		//		}
		// bouncer DONE!
		StringBuilder sqlInfo = new StringBuilder();
		List<String> unfilteredList = new ArrayList<String>();
		JSONObject toReturn = integrationDT.onElementDataTableApiChange(formId, stateKey, struct, structFormType,
				criteria, display, linkToLastSelection, formCode, tableType, sourceElementImpCode, hideEmptyColumns,
				permissionSqlList, sqlInfo, unfilteredList, lastMultiValues, updateMultiValues, followingHiddenCol);
		Object formIdForShared = toReturn.remove("formIdForShared");//removes the info of formId of form opened by shared
		Object updatedLastMultiValues = toReturn.remove("lastMultiValues");
		Object displayTopRows = toReturn.remove("displayTopRows");
		String criteriaMessage = "";
		if (unfilteredList != null && unfilteredList.size() > 0) {
			Object[] criteriaInfoArray = new String[3];
			criteriaInfoArray[0] = (struct.equals("InvItemMaterial") ? "Material" : struct); // kd 02042019 fixed bug-7366. Changed <criteriaInfoArray[0] = struct;> on current
			criteriaInfoArray[1] = criteria;
			criteriaInfoArray[2] = generalUtil.listToCsv(unfilteredList);
			criteriaMessage = generalUtil.getSpringMessagesByKey("NO_FILTER_WARNING", criteriaInfoArray,
					"Criteria was not performed for " + struct);
		}
		DataBean dtbean_sql = new DataBean("", sqlInfo.toString(), BeanType.NA, criteriaMessage);
		DataBean dtbean_formIdForShared = new DataBean("", formIdForShared != null ? formIdForShared.toString() : "-1",
				BeanType.NA, "");
		DataBean dtbean_displayTopRows = new DataBean("", displayTopRows.toString(), BeanType.NA, "");
		DataBean dtbean_lastMultiValues = new DataBean("", updatedLastMultiValues.toString(), BeanType.NA, "");
		List<DataBean> dtbean_main_list = generalUtil.StringToList(toReturn.toString());
		dtbean_main_list.add(dtbean_sql);
		dtbean_main_list.add(dtbean_formIdForShared);
		dtbean_main_list.add(dtbean_displayTopRows);
		dtbean_main_list.add(dtbean_lastMultiValues);
		return new ActionBean("no action needed", dtbean_main_list, "");
	}
	
	public ActionBean deleteRowElementDataTableApiImp(ActionBean requestAction) {
		String toReturn = "";
		String struct = requestAction.getData().get(0).getVal();
		String formId = requestAction.getData().get(1).getVal();
		String rowId = requestAction.getData().get(3).getVal();// only in shared table
		//		String table = "FG_S_" + struct + "_PIVOT";
		//		String sql = "delete from " + table + " where FORMID='" + formId + "'";
		//		String toReturn = generalDao.updateSingleString(sql);		
		//		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
		String userId = generalUtil.getSessionUserId();
		String errorMessage = integrationDT.checkRemove(struct, formId, userId, rowId);
		if (generalUtil.getNull(errorMessage).equals("") && !generalUtil.getNull(errorMessage).equalsIgnoreCase("NA")) {
			toReturn = integrationSaveForm.doRemove(struct, formId, generalUtil.getSessionUserId());
		}
		
		if(generalUtil.getNull(errorMessage).equals("NA")) {
			errorMessage = "";
		}
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn),
				generalUtil.getNull(errorMessage));
	}
	
	public ActionBean confirmDeleteRowElementDataTableApiImp(ActionBean requestAction) {
		String struct = requestAction.getData().get(0).getVal();
		String formId = requestAction.getData().get(1).getVal();
		String action = requestAction.getData().get(2).getVal();
		//		String table = "FG_S_" + struct + "_PIVOT";
		//		String sql = "delete from " + table + " where FORMID='" + formId + "'";
		//		String toReturn = generalDao.updateSingleString(sql);		
		//		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
		String userId = generalUtil.getSessionUserId();
		StringBuilder errorMessage = new StringBuilder();
		String message = integrationDT.checkRemoveConfirm(struct, formId, userId, action, errorMessage);
		return new ActionBean("no action needed", generalUtil.StringToList(generalUtil.getNull(message, "-1")),
				generalUtil.getNull(errorMessage.toString()));
	}
	
	public ActionBean dataTableAddRow(long stateKey, List<DataBean> dataBeanList, ActionBean requestAction) {

		String formId = "", formCode = "";
		try {
			formId = requestAction.getData().get(0).getVal();
			formCode = requestAction.getData().get(1).getVal();
			String parentFormCode = requestAction.getData().get(2).getVal();
			String userId = requestAction.getData().get(3).getVal();
			String domId = requestAction.getData().get(4).getVal();
			int rowNumToAdd = Integer.parseInt(requestAction.getData().get(5).getVal());
			String tableType = requestAction.getData().get(6).getVal();

			Map<String, String> elementValueMap = initElementValueMapByBeanList(dataBeanList);

			String retVal = integrationDT.dataTableAddRow(stateKey, formCode, formId, userId, parentFormCode, domId,
					elementValueMap,rowNumToAdd,tableType);
			return new ActionBean("no action needed", generalUtil.StringToList(retVal), "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return integrationSaveForm.doSaveOnException(e, formId, formCode);
		}
	}
	
	private Map<String, String> initElementValueMapByBeanList(List<DataBean> dataBeanList) {
		Map<String, String> elementValueMap = new HashMap<String, String>();
		for (DataBean dataBean : dataBeanList) {
			elementValueMap.put(dataBean.getCode(), generalUtil.getNull(dataBean.getVal()));
		}
		return elementValueMap;
	}
	
	public ActionBean onChangeDataTableCell(long stateKey, ActionBean requestAction) {
		String formId = "", formCode = "";
		try {
			formId = requestAction.getData().get(0).getVal();
			formCode = requestAction.getData().get(1).getVal();
			String parentFormCode = requestAction.getData().get(2).getVal();
			String userId = requestAction.getData().get(3).getVal();
			String formNumberID = generalUtil.getNull(requestAction.getData().get(4).getVal());
			String saveType = generalUtil.getNull(requestAction.getData().get(5).getVal());
			String onChangeFormId = requestAction.getData().get(6).getVal();
			String onChangeColumnName = requestAction.getData().get(7).getVal();
			String onChangeColumnVal = requestAction.getData().get(8).getVal();
			String oldVal = generalUtil.getNull(requestAction.getData().get(9).getVal());

			String retVal = integrationDT.onChangeDataTableCell(stateKey, parentFormCode, formId, formCode,
					onChangeFormId, userId, onChangeColumnName, onChangeColumnVal, saveType, formNumberID, oldVal);

			return new ActionBean("no action needed", generalUtil.StringToList(retVal), "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return integrationSaveForm.doSaveOnException(e, formId, formCode);
		}
	}
	
	public ActionBean getRichTextContent(String formID, String formCode, String dbColName) {
		return new ActionBean("no action needed",
				generalUtil.StringToList(integrationDT.getRichTextContent(formID, formCode, dbColName)), "");
	}

}
