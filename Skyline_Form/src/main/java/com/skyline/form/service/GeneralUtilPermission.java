package com.skyline.form.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.FormType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.SqlPermissionListObj;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.GeneralDao;

@Service
public class GeneralUtilPermission {

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private GeneralUtilForm generalUtilForm;

	@Autowired
	private GeneralUtilLogger generalUtilLogger;

	@Autowired
	private FormDao formDao;

	@Autowired
	private GeneralDao generalDao;

	@Value("${ignorePermissions:0}")
	private String ignorePermissions;
	
	@Value("${ignoreSqlPermissions:0}")
	private String ignoreSqlPermissions;
	
	@Value("${ignorePermissionsOnFormList:na}")
	private String ignorePermissionsOnFormList;
	
	@Value("${helpUrl:na}")
	private String helpUrl;
	
	@Value("${fullPermissionCrudl:crudao}")
	private String fullPermissionCrudl; //c(Create),r(Read),u(Update),d(delete for Cancellation),a(Approval),o(Reopen) ... this terms match fg_i_permissioncrud_v (in the view it is just for information and not been used)

	private final String MAX_PERMISSION_SENSITIVITYLEVEL_ORDER = "100000";

	/**
	 *   Permissions used for:
	 * - menu screens
	 * - data tables edit create and navigation (list is not used in General verion 1.0)
	 * - forms new / edit / save
	 * - Sensitivity level - effects tab visibility
	 * 
	 * crudl - originally represent C-create / r- read / u - update / d - delete / l - list
	 * this term is still in used in this class BUT! we have made changes in the crudl code letters.
	 * the variable fullPermissionCrudl holds the up to date codes 
	 */

	/**
	 * 
	 * @param userId
	 * @param formCode
	 * @param formId
	 * @param permissionType - "specificResponse" for from init / "generalResponse" for Data table
	 * @return map contains PERMISSION_ACCESS with permissions charts code (crudl see fullPermissionCrudl variable for details), PERMISSION_SENSITIVITYLEVEL_ORDER with SENSITIVITYLEVEL ORDER number. 
	 * Note: PERMISSION_ACCESS and PERMISSION_SENSITIVITYLEVEL_ORDER (PERMISSION_ACCESS effects the edit and save buttons, PERMISSION_SENSITIVITYLEVEL_ORDER effects the tabs visibility) passed as hidden vars to the client side
			 In the client side the code that disable the HTML elements is in the ElementAuthorizationImp element (see code in ElementAuthorizationImp.js).
			 The ElementAuthorizationImp.js is also handle authzCheckOnDonReady call in page load that log out in case no permission and more (see description in ElementAuthorizationImp.js)
	 * Note: in includeJS.jsp 
	 * return empty map in case of failure and write to the log
	 * In General the PERMISSION_SENSITIVITYLEVEL_ORDER is used in the view FG_R_PROJECTSUMMARY_DTDOC_V (hard-coded (as in the tab definitions) for filtering the records under the sensitive Tabs) 
	 */
	public Map<String, String> getPermissionMap(String userId, String formCode, String formId, String permissionType,
			String callContext) {

		//			AUTHZ_JSON = getDemoJson(); //just for demo
		String response = "";
		///////
		Map<String, String> permissionsMap = new HashMap<String, String>();
//		Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
		String formCodeEntity = getPermFormCodeEntity(formCode);
		try {
			StringBuilder sbInfo = new StringBuilder();
			String userSensitivitylevelOrder = generalUtilForm.getCurrrentIdSingleStringInfo("User", userId,
					"SENSITIVITYLEVELORDER");
			try{
			if (!isFullPermission() && isMenuScreen(formCodeEntity) && !isScreenInUserScheme(formCodeEntity)){
				permissionsMap.put("PERMISSION_ACCESS", "");
				return permissionsMap;
			}
			}catch(Exception e){
				generalUtilLogger.logWriter(LevelType.ERROR, "Error in getting the permissions (menu screen)! " + e,
						ActivitylogType.Permission, null, e, null);
			}
			//if ->
			// Full Permission [isFullPermission = true] OR 
			// Permission not needed [isPermissionNeededByForm  = true] OR 
			// new and there is no possibility that the user has permission [formId -1 and isUserInSchemeByCrudl on create] 
			//  -> then not need bouncer -> return full crudl permission and the max PERMISSION_SENSITIVITYLEVEL_ORDER if isFullPermission (userSensitivitylevelOrder otherwise) 
			if (isFullPermission() || !isPermissionNeededByForm(formCode) || (generalUtil.getNull(formId).equals("-1")
					&& isUserInSchemeByCrudl(formCode, userId, "Create"))) {
				permissionsMap.put("PERMISSION_ACCESS", fullPermissionCrudl);
				permissionsMap.put("PERMISSION_SENSITIVITYLEVEL_ORDER",
						isFullPermission() ? MAX_PERMISSION_SENSITIVITYLEVEL_ORDER : userSensitivitylevelOrder);
				generalUtilLogger.logWriter(LevelType.DEBUG,
						"No need to check permission. isFullPermission=" + isFullPermission()
								+ ", isPermissionNeededByForm=" + isPermissionNeededByForm(formCode)
								+ ", posible to create="
								+ (generalUtil.getNull(formId).equals("-1")
										&& isUserInSchemeByCrudl(formCode, userId, "Create")),
						ActivitylogType.Permission, formId);

				return permissionsMap;
			}

			// call approve
			if (permissionType.equalsIgnoreCase("specificResponse")) {
				response = approve(userId, formCode, formId, sbInfo, "");
				permissionsMap.put("PERMISSION_ACCESS", response);
				permissionsMap.put("PERMISSION_SENSITIVITYLEVEL_ORDER", userSensitivitylevelOrder);
				generalUtilLogger.logWriter(LevelType.DEBUG,
						"[call from " + callContext + "] permission details: response=" + response
								+ ", userSensitivitylevelOrder=" + userSensitivitylevelOrder + ", approve func info="
								+ sbInfo.toString(),
						ActivitylogType.Permission, formId);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			permissionsMap = new HashMap<String, String>();
			generalUtilLogger.logWriter(LevelType.ERROR, "Error in getting the permissions! " + e,
					ActivitylogType.Permission, formId, e, null);
		}

		return permissionsMap;
	}
	
	public String getSensitivitylevelOrder(String userId) {
		String userSensitivitylevelOrder = "0";
		try {
			userSensitivitylevelOrder = generalUtilForm.getCurrrentIdSingleStringInfo("User", userId,
					"SENSITIVITYLEVELORDER");
			if(userSensitivitylevelOrder == null || userSensitivitylevelOrder.isEmpty()) {
				userSensitivitylevelOrder = "0";
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.ERROR, "Error in getSensitivitylevelOrder! " + e,
					ActivitylogType.Permission, "-1", e, null);
		}
		return userSensitivitylevelOrder;
	}

	/**
	 * 
	 * @param formCode - parent formCode of the object we evaluate 
	 * @param formId_ - parent formId of the object we evaluate 
	 * @param wfNames remove form list code that can not be used because it has no permission
	 **/
	public void removeUnPermissionNew(String formCode, String formId_, List<String> wfNames, StringBuilder msgBuilder) {
		// todo remove from wfNames list according the permission

		try {
			//			String formCode = (formDao.getFormInfoLookup(formCode_, "%", true).get(0)).getFormCodeEntity();
			if (isFullPermission()) {
				return; // remove nothing
			}
			String userId = generalUtil.getSessionUserId();
			List<String> wfNamesCopy = new ArrayList<String>();
			for (String wfName_ : wfNames) {
				wfNamesCopy.add(wfName_);
			}

			for (String wfName_ : wfNamesCopy) {
				if (isPermissionNeededByForm(wfName_)) {
					if (!isUserInSchemeByCrudl(wfName_, userId, "Create")) { //check if there is no possibility to create the form because there is no schema definition (in this case we remove the fromcode from the list). Note: Data permissions (appendix A in permission FS) is not refer to creation so if no scheme permission with 'CREATE' creation can't be made. 
						wfNames.remove(wfName_); //add in production
						generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew,
								" - WF states permission: the object " + wfName_
										+ " removed from the list because it has no creating settings in permission scheme",
								formId_, msgBuilder);
						generalUtilLogger.logWriter(LevelType.DEBUG,
								"NEW_WF_LIST permission: the object " + wfName_
										+ " removed from the list because it has no creating settings in permission scheme",
								ActivitylogType.Permission, formId_);
					} else { //there is possibility ...(call approve)
						if (!generalUtil.getNull(formCode).isEmpty()) {
							StringBuilder sbInfo = new StringBuilder();
							if (!isUserInSchemeByCrudl(formCode, userId, "Update")) {
								wfNames.remove(wfName_);
								generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew,
										" - WF states permission: the object " + wfName_
												+ " removed from the list because his parent object can not get update permissiom",
										formId_, msgBuilder);
								generalUtilLogger.logWriter(LevelType.DEBUG,
										"NEW_WF_LIST permission: the object " + wfName_
												+ " removed from the list because his parent object can not get update permissiom",
										ActivitylogType.Permission, formId_);
							} else {
								String parent_response = approve(userId, formCode, formId_, sbInfo, "");
								if (!generalUtil.getNull(parent_response).toLowerCase().contains("u")) {
									wfNames.remove(wfName_); 
									generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew,
											" - WF states permission: the object " + wfName_
													+ " removed from the list because his parent object can not get update permissiom",
											formId_, msgBuilder);
									generalUtilLogger.logWriter(LevelType.DEBUG,
											"NEW_WF_LIST permission: the object " + wfName_
													+ " removed from the list because his parent object can not get update permissiom",
											ActivitylogType.Permission, formId_);
								}
							}
						}
						StringBuilder sbInfo = new StringBuilder();
						String response = approve(userId, wfName_, formId_, sbInfo, formCode);
						generalUtilLogger
								.logWriter(LevelType.DEBUG,
										"permission removeUnPermissionNew details: response=" + response
												+ ", approve func info=" + sbInfo.toString() + ". NOTE: if response not contains c creation of a new form will not be allowed!",
										ActivitylogType.Permission, formId_);
						if (!generalUtil.getNull(response).toLowerCase().contains("c")) {
							wfNames.remove(wfName_); //add in production
							generalUtilLogger.logWriter(LevelType.DEBUG, ActivitylogType.WorkFlowNew,
									"WF states permission: the object " + wfName_
											+ " removed from the list because it can not get create permission by his parent object settings",
									formId_, msgBuilder);
							generalUtilLogger.logWriter(LevelType.DEBUG,
									"NEW_WF_LIST permission: the object " + wfName_
											+ " removed from the list because it can not get create permissiom by his parent object setiings",
									ActivitylogType.Permission, formId_);
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			generalUtilLogger.logWriter(LevelType.ERROR,
					"NEW_WF_LIST - removeUnPermissionNew - permission ERROR parentFormCode:" + formCode
							+ ", parentFormId=" + formId_ + ", wfNames=" + wfNames.toString(),
					ActivitylogType.WorkFlowNew, formId_);

		}
	}

	/**
	 * 
	 * @param userId
	 * @param username
	 * @return map with list of screens
	 */
	public Map<String, List<JSONObject>> getMenuScreenListByUserId(String userId, String username, String mainScreen, boolean includeMaintenance) {
		List<String> list = new ArrayList<String>();
		String userPerm = "";
		JSONObject obj = new JSONObject();
		List<JSONObject> objList = new LinkedList<JSONObject>();
		Map<String, List<JSONObject>> map = new LinkedHashMap<String, List<JSONObject>>();
		ArrayList<String> aList = new ArrayList<String>();
		boolean displayFullMenu = false;

		try {
			if (isFullPermission()) {
				displayFullMenu = true;
			} else {
				userPerm = generalDao.selectSingleStringNoException(
						"select t.screen_list from fg_i_user_screen_v t where userid = " + userId);
				if (userPerm != null && !("," + userPerm + ",").contains("," + mainScreen + ",")) {
					userPerm = userPerm + "," + mainScreen;
				}
				if (includeMaintenance && userPerm != null && !("," + userPerm + ",").contains("," + "Maintenance" + ",")) {
					userPerm = userPerm + "," + "Maintenance";
				}
			}

			aList = new ArrayList<String>(Arrays.asList(userPerm.split(",")));

			list = generalDao.getListOfStringBySql("select t.screen_info from fg_i_screens_v t");
			Map<String, JSONObject> mains = new HashMap<>();

			for (String str : list) {
				try {
					obj = new JSONObject(str);
					String sub = obj.optString("sub_category");
					String subOrder = obj.optString("sub_category_order");
					String key = obj.optString("category");
					//					String categoryOrder = obj.optString("category_order");
					int systemLevel = generalUtil.getNullInt(obj.optString("system_level"), 0);

					//remove sensitive screens from the game (no matter the maintenance configuration)
					if (systemLevel > 0) {
						while (aList.contains(sub)) {
							aList.remove(sub);
						}
					}

					//add them back if..
					if (systemLevel == 2 && (generalUtil.getSessionUserName().equalsIgnoreCase("system"))) { // system
						aList.add(sub);
					} else if (systemLevel == 1 && (generalUtil.getSessionUserName().equalsIgnoreCase("system")
							|| generalUtil.getSessionUserName().equalsIgnoreCase("admin"))) { // admin in medium systemLevel (=1)
						aList.add(sub);
					} else if (systemLevel == 0 && displayFullMenu) { //displayFullMenu (it will be true for admin and system) on not sensitive
						aList.add(sub);
					}

					if (subOrder.equals("0")) {
						mains.put(key, obj); // note sensitive screens will enter to mains map but not to the return map so we OK
					}

					if (aList.contains(sub)) {
						if (!map.containsKey(key)) {
							objList = new LinkedList<JSONObject>();
							if (!subOrder.equals("0")) {
								objList.add(mains.get(key));//add category from mains map if it not exist in permissions list
							}
							objList.add(obj);
							map.put(key, objList);
						} else {
							map.get(key).add(obj);
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.ERROR, "Error in permissions screens list!" + e,
					ActivitylogType.Permission, "-1", e, null);
			map = new LinkedHashMap<String, List<JSONObject>>();
		}
		return map;
	}

	//	public boolean isUserInSchemeByCrudl(String formCode, String userId, String crudlType) {
	//		if(isFullPermission()) {
	//			return true;
	//		}
	//		String sql = "select decode(count(*),0,0,1) as isNewAllowed \r\n" + 
	//				"from FG_S_PERMISSIONSREF_INF_V t \r\n" + 
	//				"where t.ACTIVE = 1 AND instr(',' || t.PERMISSIONOBJECTNAME_GROUP || ',' ,',' || '" + formCode + "' || ',') > 0\r\n" + 
	//				"and   instr(',' || t.user_crew_list || ',' ,',' || '" + userId + "' || ',') > 0  \r\n" + 
	//				"and   upper(t.PERMISSION) = upper('" + crudlType + "')";
	//		String isNewAllowed = generalDao.selectSingleString(sql);
	//		return generalUtil.getNull(isNewAllowed,"0").equals("1");
	//	} --> WORK BUT NOT IN CAHCE ->

	/**
	 * 
	 * @param formCode - the formCode (this function handle inherit definition)
	 * @param userId
	 * @param crudlType - Create/ Read/ Update/ Cancellation/ Approval/ Reopen
	 * @return true if there possibility to have a permission in permission schema (maintenance) definition
	 */
	public boolean isUserInSchemeByCrudl(String formCode, String userId, String crudlType) {
		if (isFullPermission() || !isPermissionNeededByForm(formCode)) {
			generalUtilLogger.logWriter(LevelType.DEBUG,
					"isUserInSchemeByCrudl return TRUE because the user or the screen not needed permission. formCode="+ formCode+ ", userId=" + userId,
					ActivitylogType.Permission, "-1");
			return true;
		}

		String formCodeObject = getPermFormCodeEntity(formCode);
		List<Map<String, String>> permissionsSchemaInfo = generalUtilForm
				.getCurrrentNameInfoAllContainsName("PermissionSRef", "%");
		for (Map<String, String> map : permissionsSchemaInfo) {
			String permissionObjectList = map.get("PERMISSIONOBJECTNAME_GROUP"); // hold list of object based on inherit
			//			if (map.get("IS_ACTIVE", "1").equals("1")
			if (//getOrDefaultImp(map.get("IS_ACTIVE"), "1").equals("1") yp 08092020 make it in the view
					isInCsvList(getOrDefaultImp(map.get("PERMISSION"), "na").toLowerCase(), crudlType.toLowerCase())
					&& isFormCodeObjectInList(permissionObjectList, formCodeObject)
					&& isInCsvList(getOrDefaultImp(map.get("USER_CREW_LIST"), "-1"), userId)) {
				return true;
			}
		}
		generalUtilLogger.logWriter(LevelType.DEBUG,
				"isUserInSchemeByCrudl return FALSE because the user not in the permission schema for this form. formCode="+ formCode+ ", userId=" + userId,
				ActivitylogType.Permission, "-1");
		return false;
	}
	
	/**
	 * evaluate permissions
	 * @param formCode
	 * @param formId
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public boolean isCreatePermissionPostSave(String formCode, String formId, String userId) throws Exception {
		if (isFullPermission() || !isPermissionNeededByForm(formCode)) {
			generalUtilLogger.logWriter(LevelType.DEBUG,
					"isUserInSchemeByCrudl return TRUE because the user or the screen not needed permission. formCode="+ formCode+ ", userId=" + userId,
					ActivitylogType.Permission, "-1");
			return true;
		}
		StringBuilder sbInfo = new StringBuilder();
		//		String formCodeObject = (formDao.getFormInfoLookup(formCode, "%", true).get(0)).getFormCodeEntity();
		String response = approve(userId, formCode, formId, sbInfo, "");
		generalUtilLogger.logWriter(LevelType.DEBUG,
				"permission post save details: response=" + response + ", approve func info=" + sbInfo.toString(),
				ActivitylogType.Permission, formId);

		return generalUtil.getNull(response).toLowerCase().contains("c");
	}
	
	/**
	 * 
	 * @param formCode - screen form code
	 * @param struct - name of the object (project, experiment ...)
	 * @param table - name of the SQL table (for future use)
	 * @param userId - 
	 * @return SQL String that returns id for filtering the table/list records (only the id's that this SQL returns are allowed to watch) or null if there are no limitations
	 * @throws Exception
	 */
	public SqlPermissionListObj getPermissionListSql(String formCode, String struct, String table, String userId) throws Exception {
		SqlPermissionListObj toReturn = new SqlPermissionListObj();
		String obj_id = (struct.equals("NA") ? "ID" : struct + "_ID");
		if (isFullSqlPermission() || !isSQLPermissionNeededByForm(formCode, struct, table)) {
			generalUtilLogger.logWriter(LevelType.DEBUG,
					"getPermissionListSql return null because the user or the screen not needed SQL permission. formCode="+ formCode+ ", userId=" + userId,
					ActivitylogType.Permission, "-1");
			return toReturn; //null
		}
		
		String sql = "";
		if(generalUtil.getNull(struct).equalsIgnoreCase("Action") || generalUtil.getNull(struct).equalsIgnoreCase("SelfTest") || generalUtil.getNull(struct).equalsIgnoreCase("Workup") || generalUtil.getNull(struct).equalsIgnoreCase("sw")){
			struct = "EXP_inherit";
			obj_id = "EXPERIMENT_ID";
		}else if(generalUtil.getNull(struct).equalsIgnoreCase("Step")){
			obj_id = "EXPERIMENT_ID";
		}else if(generalUtil.getNull(struct).equalsIgnoreCase("se")){
			struct = "subsubproject";
			sql =  " select P.ID from fg_s_experiment_permlist_v P WHERE p.user_id = '"+userId +"' union all ";
		}else if(generalUtil.getNull(struct).equalsIgnoreCase("root")){
			struct = "project";
			obj_id = "project_id";
		}else if(generalUtil.getNull(struct).startsWith("InvItem")){
			struct = struct.replace("InvItem", "");
		}else if(generalUtil.getNull(struct).equalsIgnoreCase("SpreadsheetTempla")){
			struct = "SPREADSTMPLT";
		}else if(generalUtil.getNull(struct).equalsIgnoreCase("RecipeFormulation")){
			struct = "RECIPEFOR";
		}
		
		sql +=  " select P.ID from fg_s_"+struct+"_permlist_v P WHERE p.user_id = '"+userId +"' ";
		toReturn= new SqlPermissionListObj(sql,obj_id);
		return toReturn;
	}

	/**
	 * returns crudls by:
	 * 1) Data Defined - according to the "PERMISSION_<crudl>" in formCodeObject inf_v
	 * 2) Schema permissions scope (form id data) is taken from the fg_s_<formCodeEntity>_inf_v in case of inherit the inf_v should contains PERMISSION_PARENT_ID with the id of formid of the parent permission record
	 * @param userId
	 * @param formCode - (it is possible that the formcode is not the same entity as in formid  - used only to filter the right schema definitions)
	 * @param scopeId - formId
	 * @param sbInfo - map the fill with the evaluation info
	 * @return permission string (crudl)
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private String approve(String userId, String formCode, String scopeId, StringBuilder sbInfo, String parentFormCode)
			throws SQLException, ClassNotFoundException, IOException {
		String FormCodeEntity = getPermFormCodeEntity(formCode);
		String toReturnCrudl = "";
		// Getting all the information ready
		Map<String, String> userInfo = generalUtilForm.getCurrrentIdInfo(userId);
		Map<String, String> scopeInfo = new HashMap<String, String>();
		if (generalUtil.getNull(parentFormCode).equals("")) {
			scopeInfo = generalUtilForm.getCurrrentInfoById(FormCodeEntity, scopeId);
		} else {
			//handle scopeInfo on create inharit ->
			String log_ = "";
			String parentForm = getParentFormCodeInherit(FormCodeEntity, parentFormCode); // getParentFormCodeInherit returns the formcode for the scope  - if empty create is approved [the eval will be made in the post save because the result is according to the user data]
			if (!generalUtil.getNull(parentForm).equals("")) {
				scopeInfo = generalUtilForm.getCurrrentInfoById(parentForm, scopeId);
				log_ = ((scopeInfo == null || scopeInfo.size() == 0) ? ". no scope id found by inherit on create object"
						: ". scope id is evaluate by parent form " + parentForm);
			} else {
				toReturnCrudl = "c";
				log_ = ". scope c (create) is allow and the evaluate again on save.";
			}
			sbInfo.append(" scope Info is empty on formCode = " + formCode + ", scopeId = " + scopeId + log_);
		}

		List<Map<String, String>> permissionsSchemaInfo = generalUtilForm
				.getCurrrentNameInfoAllContainsName("PermissionSRef", "%");

		String userSiteId = getOrDefaultImp(userInfo.get("SITE_ID"), "-999");
		String userUnitId = getOrDefaultImp(userInfo.get("UNIT_ID"), "-999");
		String userLabId = getOrDefaultImp(userInfo.get("LABORATORY_ID"), "-999");

		String scopeSiteId = "";
		String scopeUnitId = "";
		String scopeLabId = "";
		String scopeDataU = getOrDefaultImp(scopeInfo.get("PERMISSION_U"), "-99");
		String scopeDataR = getOrDefaultImp(scopeInfo.get("PERMISSION_R"), "-99");
//		String scopeDataL = getOrDefaultImp(scopeInfo.get("PERMISSION_L"), "-99");

		String parentScopeId = getOrDefaultImp(scopeInfo.get("PERMISSION_PARENT_ID"), null);
		if (parentScopeId == null || parentScopeId.equals("") || parentScopeId.equals("-1")) {
			scopeSiteId = getOrDefaultImp(scopeInfo.get("SITE_ID"), "-99");
			scopeUnitId = getOrDefaultImp(scopeInfo.get("UNITS_ID"), "-99");
			scopeLabId = getOrDefaultImp(scopeInfo.get("LABORATORY_ID"), "-99");
			sbInfo.append(" scopeSiteId: SITE_ID=" + scopeSiteId + " UNITS_ID=" + scopeUnitId + ", LABORATORY_ID="
					+ scopeLabId);
		} else {
			scopeInfo = generalUtilForm.getCurrrentIdInfo(parentScopeId);
			scopeSiteId = getOrDefaultImp(scopeInfo.get("SITE_ID"), "-99");
			scopeUnitId = getOrDefaultImp(scopeInfo.get("UNITS_ID"), "-99");
			scopeLabId = getOrDefaultImp(scopeInfo.get("LABORATORY_ID"), "-99");
			sbInfo.append(" scopeSiteId by parent: parentScopeId=" + parentScopeId + ", SITE_ID=" + scopeSiteId
					+ ", UNITS_ID=" + scopeUnitId + ", LABORATORY_ID=" + scopeLabId);
		}

		//Data Defined  
		if (isInCsvList(scopeDataU, userId)) {
			sbInfo.append(" userId=" + userId + " is in Update list=" + scopeDataU + ", scopeid=" + scopeId
					+ " [this list is taken from the inf_v view]");
			toReturnCrudl += "u";
		}

		if (isInCsvList(scopeDataR, userId)) {
			sbInfo.append(" userId=" + userId + " is in Read list=" + scopeDataR + ", scopeid=" + scopeId
					+ " [this list is taken from the inf_v view]");
			toReturnCrudl += "r";
		}

//		if (isInCsvList(scopeDataL, userId)) {
//			sbInfo.append(" userId=" + userId + " is in List list=" + scopeDataL + ", scopeid=" + scopeId
//					+ " [this list is taken from the inf_v view]");
//			toReturnCrudl += "l";
//		}

		// Schema Defined
		String crudlChar = "";
		for (Map<String, String> map : permissionsSchemaInfo) {
			String permissionObjectList = map.get("PERMISSIONOBJECTNAME_GROUP"); // hold list of object based on inherit
			if (getOrDefaultImp(map.get("IS_ACTIVE"), "1").equals("1")
					&& isFormCodeObjectInList(permissionObjectList, FormCodeEntity)
					&& isInCsvList(map.get("USER_CREW_LIST"), userId)) {
				String permissionCsv = generalUtil.getNull(map.get("PERMISSION")).toLowerCase();
				if (!permissionCsv.equals("")) {
					String siteId = getOrDefaultImp(map.get("SITE_ID"), null);
					String unitId = getOrDefaultImp(map.get("UNIT_ID"), null);
					String labId = getOrDefaultImp(map.get("LAB_ID"), null);
					String[] permissionArray = permissionCsv.split(",", -1);
					for (String permissionItem : permissionArray) {
						crudlChar = getCrudlByPermissionItem(permissionItem);
						if (!crudlChar.equals("") && !toReturnCrudl.contains(crudlChar)) {
							if (isEntityCompatible(siteId, scopeSiteId, userSiteId)
									&& isEntityCompatible(unitId, scopeUnitId, userUnitId)
									&& isEntityCompatible(labId, scopeLabId, userLabId)) {
								sbInfo.append(" \"SchemaID\"=\"" + map.get("ID") + "\", \"permission\"=\"" + crudlChar
										+ "\", \"scopeSiteId\"=\"" + scopeSiteId + "\", \"scopeUnitId=\"" + scopeUnitId
										+ "\", \"scopeLabId\"=\"" + scopeLabId + "\"\n");
								toReturnCrudl += crudlChar;
							}
						}
					}
				}
			}
		}

		return toReturnCrudl;
	}
	
	/**
	 * 
	 * @param permissionItem
	 * @return crudl code (see fullPermissionCrudl variable for details)
	 */
	private String getCrudlByPermissionItem (String permissionItem) {
		if(permissionItem != null && permissionItem.equalsIgnoreCase("reopen")) {
			permissionItem = "open";
		} else if(permissionItem != null && permissionItem.equalsIgnoreCase("cancellation")) {
			permissionItem = "delete";
		}
		return permissionItem.substring(0, 1); 
	}

	/*
	 * returns the formcode for the scope  - if empty no eval should be made for this formCodeObject [the eval will be made in the post save because the result is according to the user data]
	 */
	private String getParentFormCodeInherit(String formCodeObject, String parentFormCode) { // the inherit defined as =OBJECTSINHERITONCREATE in the maintenance - I did it hard coded and didn't distinguish between OBJECTSINHERITONCREATE and OBJECTSINHERIT we concatenate them to one PERMISSIONOBJECTNAME_GROUP list (it was part of old FS that isn't relevant)
		String toReturn = "";
		try {
			String formCodeObject_ = generalUtil.getNull(formCodeObject).toLowerCase();
//			if (isInCsvList(("SubProject,SubSubProject").toLowerCase(), formCodeObject_)) {
//				toReturn = "Project";
//			} else 
			if (isInCsvList(("Step").toLowerCase(), formCodeObject_)) {
				toReturn = "Experiment";
			} else if (isInCsvList(("Action").toLowerCase(), formCodeObject_)) {
				toReturn = "Step";
			} else if (isInCsvList(("SelfTest,Workup").toLowerCase(), formCodeObject_)) {
				toReturn = "Action";
			} else if (isInCsvList(("InvItemCalibration,InvItemMaintenance").toLowerCase(), formCodeObject_)) {
				toReturn = "InvItemInstrument";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//				e.printStackTrace();
		}
		return toReturn;
	}

	/**
	 * 
	 * @param formCode
	 * @return true is the formCode participate in permissions
	 */
	private boolean isPermissionNeededByForm(String formCode) {
		boolean toReturn = false;
		FormType fromType = generalUtilForm.getFromType(formCode);
		if (fromType.equals(FormType.STRUCT) || fromType.equals(FormType.INVITEM)) {
			toReturn = !isInCsvList(ignorePermissionsOnFormList, formCode);
		}

		return toReturn;
	}
	
	private boolean isMenuScreen(String formCodeEntity) {//TODO:workaround for HistoricalData screen- replaced the entity to HistoricalDataMain- maybe the isScreenInUserScheme is sufficient now
		boolean toReturn = false;
		if (formCodeEntity != null && (formCodeEntity.equals("HistoricalDataMain"))){
			toReturn = true;
		}

		return toReturn;
	}
	
	private boolean isSQLPermissionNeededByForm(String formCode, String struct, String table) {
		boolean toReturn = false;
		//TODO use list of formCode/struct from app properties (!?)
		if (formCode != null && (formCode.equalsIgnoreCase("Main") || formCode.equalsIgnoreCase("NavigationTree")
				|| formCode.equalsIgnoreCase("TemplateMenuMain")
				|| formCode.equalsIgnoreCase("SpreadsheetMain")
				|| formCode.equalsIgnoreCase("InvItemRecipesMain")
				|| formCode.equalsIgnoreCase("InvItemMaterialsMain")
				|| formCode.equalsIgnoreCase("InvItemColumnsMain")
				|| (formCode.equalsIgnoreCase("InvItemMaterial") && struct.equals("InvItemBatch"))
				|| formCode.equalsIgnoreCase("InvItemInstrumentsMain")
				|| (formCode.equalsIgnoreCase("InvItemSamplesMain") && struct.equals("Sample"))
				|| (formCode.endsWith("Project") && (struct.equalsIgnoreCase("SubProject") || struct.equalsIgnoreCase("subSubProject") || struct.equalsIgnoreCase("experiment")))
				|| (formCode.equals("ExpAnalysisReport") && (struct.equalsIgnoreCase("Project") || struct.equalsIgnoreCase("SubProject") || struct.equalsIgnoreCase("experiment"))
			    || (formCode.equals("ExperimentReport") && (struct.equalsIgnoreCase("Project") || struct.equalsIgnoreCase("SubProject") || struct.equalsIgnoreCase("experiment")))))) {
			toReturn = true;
		}
		return toReturn;
	}
	
	

	/**
	 * 
	 * @return true is full permission (no limits on screens, tabs, and crudl operations)
	 */
	private boolean isFullPermission() {
		// TODO Auto-generated method stub
		String userPerm = generalUtil.getSessionUserName();
		return ignorePermissions.equals("1") || userPerm.equals("") || userPerm.equalsIgnoreCase("Admin")
				|| userPerm.equalsIgnoreCase("System") || userPerm.equalsIgnoreCase("unittestuser");
		//			return true;
	}
	
	private boolean isFullSqlPermission() {
		// TODO Auto-generated method stub
		String userPerm = generalUtil.getSessionUserName();
		return ignoreSqlPermissions.equals("1") || userPerm.equals("") || userPerm.equalsIgnoreCase("Admin")
				|| userPerm.equalsIgnoreCase("System") || userPerm.equalsIgnoreCase("unittestuser");
		//			return true;
	}

	private boolean isFormCodeObjectInList(String permissionObjectList, String objectName) {
		// TODO Auto-generated method stub
		return ("," + permissionObjectList + ",").toLowerCase().contains("," + objectName.toLowerCase() + ",");
	}

	private boolean isInCsvList(String source, String val) {
		// TODO Auto-generated method stub
		if(source == null || val == null || source.isEmpty() || val.isEmpty()) {
			return false;
		}
		return ("," + source + ",").contains("," + val + ",");
	}

	//		private String getCrudlChar(Map<String, String> map) {
	//			// TODO Auto-generated method stub
	//			String toReturn = "";
	//			try {
	//				toReturn = map.get("PERMISSION").toLowerCase().substring(0, 1);
	//			} catch (Exception e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//			return toReturn;
	//		}

//	private boolean isAllPermissionIn(String toReturnCrudl) {
//		// TODO Auto-generated method stub
//		return toReturnCrudl.contains("c") && toReturnCrudl.contains("a") && toReturnCrudl.contains("u")
//				&& toReturnCrudl.contains("r") && toReturnCrudl.contains("o") && toReturnCrudl.contains("d");
//	}

	private boolean isEntityCompatible(String permissionId, String scopeId, String userInfId) {
		if (permissionId != null && !permissionId.equals("")) {
			// ALL
			if (permissionId.equals("10")) {
				return true;
			}
			// OWN
			else if (permissionId.equals("11")) {
				return isInCsvList(scopeId,userInfId);
			}
			// NOT OWN
			else if (permissionId.equals("12")) {
				return !isInCsvList(scopeId,userInfId);
			}
			//by match permissionId with scope 
			else if (isInCsvList(scopeId,permissionId)) {
				return true;//scopeId.equals(userInfId);
			} else {
				return false;
			}
		}
		return true;
	}

	//kd 23012018 use this instead method for Map getOrDefault from Java 8 
	private String getOrDefaultImp(String val, String defaultValue) {
		if (val == null) {
			return defaultValue;
		}
		return val;
	}

	public String appendUserGuide(String userId, String username) {
		StringBuilder sbToReturn = new StringBuilder();

		// **** Layer3 - videos and Docs Items (according to maintenance form UserGuidePool)
		StringBuilder sbVideo = new StringBuilder();
		StringBuilder sbDocs = new StringBuilder();
		Map<String,String> mapVideoOrder = new TreeMap<String,String>();
		Map<String,String> mapDocOrder = new TreeMap<String,String>();
		String liItemTemplate = "<li><a href='#' onclick=\" confirmWithOutSaveMainMenu('../skylineForm/init.request?formCode=UserGuideViewer&formId=-1&userId=@@USERID@@&stateKey=@@STATEKEY_HOLDER@@&userGuideId=@@USERGUIDEID@@');\" >@@NAME@@</a></li>";
		try {
			List<Map<String, String>> userGuideInfo = generalUtilForm
					.getCurrrentNameInfoAllContainsName("UserGuidePool", "%");
			for (Map<String, String> map : userGuideInfo) {
				if (getOrDefaultImp(map.get("IS_ACTIVE"), "1").equals("1")) {
					String liHtml = "";
					String id = map.get("ID");
					String name = map.get("NAME");
					String order = ((generalUtil.getNullInt(map.get("ITEMORDER"),0) + 100000)) + "_" + id; //make it order by string and unique using this expression
					String contentType = map.get("CONTENT_CODE_UG");
					liHtml = liItemTemplate.replace("@@USERGUIDEID@@", id).replace("@@USERID@@", userId)
							.replace("@@NAME@@", name);
					if (contentType.equals("VIDEO_UG")) {
						mapVideoOrder.put(order,liHtml); // add to video map 
					} else {
						mapDocOrder.put(order,liHtml); // add to Doc map 
					}
				}
			}
		} catch (Exception e) {
			// Do Nothing
		}
		
		//add in the write order (ITEMORDER) by iterate the TreeMap maps...
		for (Map.Entry<String, String> entry : mapVideoOrder.entrySet()) {
			sbVideo.append(entry.getValue()); 
		}
		
		for (Map.Entry<String, String> entry : mapDocOrder.entrySet()) {
			sbDocs.append(entry.getValue()); 
		}
		
		
		// **** Layer2 - help, videos, docs 
		// Add help Url if defined in app.prop
		String helpUrlWrapper = "";
		String userGuideHelpClick = "";
		if (!generalUtil.getNull(helpUrl, "na").equals("na")) { // a
			userGuideHelpClick = " onclick=\" openNewTab('" + helpUrl + "');\"";
			helpUrlWrapper = "<li class=\"is-submenu-item is-dropdown-submenu-item\" role=\"menuitem\" ><a href='#' "
			+ userGuideHelpClick + " >Get Help</a></li>";
		}
				
		// videos
		String videoWrapper = "";
		if(sbVideo.length() > 0) {
			videoWrapper = "<li class=\"ugVideo-dropdown-submenu\"><a href='#' >Training Videos</a><ul style=\"overflow-y:auto;max-height:250px;\">" + sbVideo.toString() + "</ul></li>";
		}
		
		// Docs
		String docWrapper = "";
		if(sbDocs.length() > 0) {
			docWrapper = "<li class=\"ugVideo-dropdown-submenu\"><a href='#' >Training Files</a><ul style=\"overflow-y:auto;max-height:250px;\">" + sbDocs.toString() + "</ul></li>";
		}
		
		// **** Layer1: put all together in sbToReturn ...
		sbToReturn.append("<ul class=\"menu is-dropdown-submenu\" style=\"z-index:500\">").append(helpUrlWrapper)
				.append(videoWrapper).append(docWrapper).append("</ul>");
		
		//****  return
		return sbToReturn.toString();
	}

	public boolean isPermissionExists(String userId) {
		String inSchema = "0";
		if(isFullPermission()) {
			inSchema = "1";
		} else if(userId!= null && !userId.isEmpty()) {
			inSchema = generalDao.selectSingleString("select decode(count(id),0,0,1) as inSchema from Fg_s_Permissionsref_Inf_v t where ',' || t.user_crew_list || ',' like '%," + userId + ",%'  ");
		}
		return inSchema.equals("1");
	}
	
	private boolean isScreenInUserScheme(String formCodeEntity) {
		boolean toReturn = false;
		String userScreen = generalUtil.getSessionScreenList();
		if (!generalUtil.getNull(userScreen).isEmpty()) {
			ArrayList<String> aList = new ArrayList<String>(Arrays.asList(userScreen.split(",")));
			if (aList.contains(formCodeEntity)) {
				toReturn = true;
			}
		}
		return toReturn;
	}
	
	private String getPermFormCodeEntity(String formCode) {
		String permFormCodeEntity = formCode;
		if(!permFormCodeEntity.equalsIgnoreCase("InvItemMaterialFr") && !permFormCodeEntity.equalsIgnoreCase("InvItemMaterialPr")) {
			permFormCodeEntity = (formDao.getFormInfoLookup(formCode, "%", true).get(0)).getFormCodeEntity();
		}
		return permFormCodeEntity;
	}
	
	public String getMaintenanceScreenListByUserId(String userId) {
		//List<String> list = new ArrayList<String>();
		String userPerm="";
		try {
			if (!isFullPermission()) {
				userPerm = generalDao.selectSingleStringNoException(
						"select t.maintenance_screen_list from fg_i_user_maint_screen_v t where userid = " + userId);
			}
			else {
				return "ALL";
			}
			//list = new ArrayList<String>(Arrays.asList(userPerm.split(",")));
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.ERROR, "Error in permissions maintenance screens list!" + e,
					ActivitylogType.Permission, "-1", e, null);
			userPerm="";
		}
		return userPerm;
	}
}
