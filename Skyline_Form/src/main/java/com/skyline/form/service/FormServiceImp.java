package com.skyline.form.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.skyline.customer.adama.CommonFunc;
import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.BeanType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.Form;
import com.skyline.form.bean.FormAdditionalData;
import com.skyline.form.bean.FormEntity;
import com.skyline.form.bean.FormLastSaveValue;
import com.skyline.form.bean.FormType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.LookupType;
import com.skyline.form.bean.RedirectInfo;
import com.skyline.form.dal.FormBuilderDao;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.GeneralDao;
//import com.skyline.form.dal.FormDao;
import com.skyline.form.entity.Element;

@Service
//@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class FormServiceImp implements FormService {

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private FormDao formDao;

	@Autowired
	private FormBuilderDao formBuilderDao;

	@Autowired
	private GeneralDao generalDao;

	@Autowired
	public GeneralUtilForm generalUtilForm;

	@Autowired
	private GeneralUtilLogger generalUtilLogger;

	@Autowired
	private GeneralUtilPermission generalUtilPermission;

	@Autowired
	private GeneralUtilNotificationEvent generalUtilNotificationEvent;

	@Autowired
	private FormState formState;
	
	@Autowired
	private GeneralUtilFormState generalUtilFormState;

	private final String FORM_ELEMENT_ATTRIBUTE_ = "formElement=1 ";

	private final String FORM_ELEMENT_SAVE_ATTRIBUTE_ = "formPreventSave=0";

	private final String FORM_ELEMENT_PREVENT_SAVE_ATTRIBUTE_ = "formPreventSave=1";

	private final String FORM_ELEMENT_FORM_ADDITIONALDATA_ATTRIBUTE_ = "formPreventSave=2";

	private final String FORM_ELEMENT_SAVE_TYPE_ATTRIBUTE_ = "saveType=\"none\" ";

	private static final Logger logger = LoggerFactory.getLogger(FormServiceImp.class);
	
	@Autowired
	private CommonFunc commonFunc;
	
	@Value("${favoriteFormList:project,subproject,subsubproject,experiment,request,step,action,selftest,workup,spreadsheettempla}")
	private String favoriteFormList;

	//first call
	@Override
	public ModelAndView initForm(Boolean isNew, long stateKey, String formCode, String formId, String userId,
			String nameId, String urlCallParam, String urlPrintParam, Map<String, String[]> requestMap) {
		
		long start = System.currentTimeMillis();
		generalUtilLogger.logWriter(LevelType.DEBUG,"Start initForm formCode=" + formCode + ", formId=" + formId,
				ActivitylogType.PerformanceJava, formId);
 
		String isCloneString = null;
		String isCloneRequestMap[] = requestMap.get("isClone");
		if (isCloneRequestMap != null) {
			isCloneString = isCloneRequestMap[0];
		}
		boolean isClone = ((isCloneString != null) && (isCloneString.equals("1"))) ? true : false;
 
		boolean isNewFormId = (isNew == null || isClone) ? isNewFormId(formCode, formId) : isNew;
		
		//skipAuthz for admin and system by url parameter
		boolean skipAuthz = false;
		String isSkipAuthz[] = requestMap.get("isSkipAuthz");
		if (isSkipAuthz != null) {
			String userName = generalUtil.getSessionUserName();
			if((userName.equals("system") || userName.equals("admin"))) {
				skipAuthz = isSkipAuthz[0] != null && isSkipAuthz[0].equals("1");
			}
		}
		
		Map<String, String> permissionMap = generalUtilPermission.getPermissionMap(userId, formCode,
				isNewFormId ? "-1" : generalUtil.getNull(formId, "-1"), "specificResponse", "initForm");
		if (!generalUtil.getNull(permissionMap.get("PERMISSION_ACCESS")).toLowerCase().contains("r")) {
			return new ModelAndView("Login");
		}
		
		//get last saved value map (when not clone / new)
		Map<String, String> lastSaveValMap = new HashMap<>();
		try {
			lastSaveValMap = getLastFormSaveValue(formCode, formId, userId, nameId, isNewFormId, isClone);
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
			lastSaveValMap.put("DBError", "1");
			return new ModelAndView("Login", lastSaveValMap);
		}

		//call initForm
		Map<String, String> outParamMap = new HashMap<String, String>();
		generalUtilFormState.initForm(isNewFormId, lastSaveValMap, stateKey, formCode, userId, formId, nameId,
				urlCallParam, requestMap, outParamMap);

		//open openTransaction
		generalUtilFormState.openTransaction(stateKey, formCode, userId, formId);
 
		//set parameters
		generalUtilFormState.setFormParam(stateKey, formCode, permissionMap);
		
		//Render Form
		Map<String, String> formEntityHtmlCodeMap = renderFormInit(stateKey, formCode, formId, skipAuthz);
		
		//stataeKey
		formEntityHtmlCodeMap.put("stateKey", String.valueOf(stateKey));
		
		//formPath
		formEntityHtmlCodeMap.put("formPath", formState.showFormPathDisplayHtml(generalUtil.getNull(outParamMap.get("FORMPATH"))));
		
		//titles
		Map<String, String> titleDetails = getFormTitle(formCode, stateKey, isNewFormId, isClone, userId, formId);
		formEntityHtmlCodeMap.put("formTitle", titleDetails.get("formTitle"));
		formEntityHtmlCodeMap.put("formTitleTooltip", titleDetails.get("tooltip"));
		formEntityHtmlCodeMap.put("formTitleCSS", titleDetails.get("formTitleCss"));
		formEntityHtmlCodeMap.put("formSubTitle", titleDetails.get("formSubTitle"));
		formEntityHtmlCodeMap.put("formSubTitleTooltip", titleDetails.get("subTitleTooltip"));
		formEntityHtmlCodeMap.put("browserTitle", titleDetails.get("formTitle"));
		formEntityHtmlCodeMap.put("favorite", titleDetails.get("favorite"));
		
		//..add to ModelAndView info
		//....requestMap
		for (Map.Entry<String, String> entry : outParamMap.entrySet()) {
			formEntityHtmlCodeMap.put(entry.getKey(), entry.getValue());
		}

		//....user name
		formEntityHtmlCodeMap.put("userName", generalUtilFormState.getCurrentUserName(stateKey, formCode));

		//....spring messages
		formEntityHtmlCodeMap.put("springMessages", generalUtil.getSpringMessages());
		
		//....permissions
		formEntityHtmlCodeMap.putAll(permissionMap);
		
		//....breadCrumbHtml
		formEntityHtmlCodeMap.put("breadCrumbHtml", generalUtilFormState.getBreadCrumbHtml(stateKey));

		//....check notification (it update the session)
		generalUtilNotificationEvent.getMessageCount(userId, false);
		
		//..get ModelAndView
		String formInitInfo = "FormServiceMultiRoot init form state summary:\n"
				+ generalUtilFormState.getSummary(stateKey, formCode, false);
		logger.info(formInitInfo);
		
		if (isClone && (formCode.equalsIgnoreCase("InvitemMaterial") ||formCode.equalsIgnoreCase("InvItemMaterialFr")||formCode.equalsIgnoreCase("InvItemMaterialPr")|| formCode.equalsIgnoreCase("InvitemInstrument")
				|| formCode.equalsIgnoreCase("InvitemColumn") || formCode.equalsIgnoreCase("SelfTest")
				|| formCode.equalsIgnoreCase("Request") || formCode.equalsIgnoreCase("PermissionScheme")
				|| formCode.equalsIgnoreCase("RecipeFormulation") || formCode.equalsIgnoreCase("MaterialFunction"))) {
			generalDao.updateSingleString("delete from fg_s_" + formCode + "_pivot where formid = '" + formId
					+ "' and active = '-" + formId + "'");
		}
		
		// get urlPrintParam from previous form and update PRINT_PARAM_PASSLABELCODE / PRINT_PARAM_PASSLABELDATA (for printing label on loading)
		try {
			if (!generalUtil.getNull(urlPrintParam).equals("")) {
				JSONObject jObject = new JSONObject(urlPrintParam.trim());
				String _pass_labelCode = (String) jObject.get("passLabelCode");
				String _pass_labelData = (String) jObject.get("passLabelData");

				formEntityHtmlCodeMap.put("PRINT_PARAM_PASSLABELCODE", _pass_labelCode);
				formEntityHtmlCodeMap.put("PRINT_PARAM_PASSLABELDATA", _pass_labelData);
			}

		} catch (Exception e) {
			System.out.println("urlPrintParam update global input error!");
			// TODO do nothing
		}
		
		generalUtilLogger.logWriter(LevelType.DEBUG,"End initForm formCode=" + formCode + ", formId=" + formId,
				ActivitylogType.PerformanceJava, formId);
		long time = System.currentTimeMillis() - start;
		
		//log
		generalDao.logMessage("################ INIT FORM TIME = " + time + "################ ");
		generalUtilLogger.logWriter(LevelType.INFO, "Open form " + formCode + " [time="+ time+"]", formId, ActivitylogType.OpenForm, userId,
				null); 

		return new ModelAndView(generalUtilFormState.getLayoutList(stateKey, formCode).get(0).getJspName(),
				formEntityHtmlCodeMap);
	}

	private Map<String, String> getFormTitle(String formCode, long stateKey, boolean isNewFormId, boolean isClone, String userId, String formId) {//TODO->Yaron:transfer the code to a customer package
		Map<String, String> toReturn = new HashMap<>();
		
		Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
		String title = generalUtil.getNull(form.getTitle());
		String subtitle = "";
		String additSymbolsForTitle = "";
		try {
			subtitle = generalUtil.getNull(form.getSubtitle());
			if (!subtitle.equals("") && subtitle.contains("$P{")) {
				// replace all parameter ->
				subtitle = generalUtilFormState.replaceFormParam(stateKey, formCode, subtitle, true);
			}
			// clean expected [] or () when no parameters founds:
			
			subtitle = subtitle.replaceAll("\\[\\s+\\]", "").replaceAll("\\(\\s+\\)", "").trim();
			
			// 18082020 kd fixed bug-8458. Goal is to change " }}" on ", " 
			
			if(subtitle.matches(".*}}step\\s+\\}}")){  // change "}}step }}" on "" in case there is no any text after "}}step" (no 2 last parts of subtitle)"
				subtitle = subtitle.replaceAll("}}step\\s+\\}}", "");
			}
			else if (subtitle.matches(".*}}step\\s+\\}}.*")) { // change "}}step" on "," in case there is no any text between "}}" (no second from the end part of subtitle)
				subtitle = subtitle.replaceAll("}}step\\s+}}", ", ").replaceAll(" ,", ",");
			}
			else if (subtitle.matches(".*}}")) { // change "}}" on "" in case there is no any text after "}}" (no last part of subtitle)
				subtitle = subtitle.replaceAll("}}", ""); 
			}
			else {
				subtitle = subtitle.replaceAll(" }}", ", "); // change " }}" on ", ".
			}
		} catch (Exception e) {
			try {
				subtitle = generalUtil.getNull(form.getSubtitle());
			} catch (Exception e1) {
				// DO NOTHING
			}
		}

		String titleNewFlag = "";
		String userName = generalUtil.getSessionUserName();
		if ((userName.equals("admin?") || userName.equals("system?") || userName.equals("unittestuser?"))
				&& isNewFormId) {
			titleNewFlag = "(new" + (isClone ? " by clone" : "") + ")";
		}
		
		String[] titleStrParts = subtitle.split("\\{\\{");//kd 28072020 task-25483 Screens Header improvements.
		if(titleStrParts.length > 0) 
		{
			if (!titleStrParts[0].trim().equals("")){
				if (formCode.startsWith("Experiment") || formCode.startsWith("Sample")){ 
					additSymbolsForTitle = " #: ";
				} else if (formCode.equals("InvItemMaterialsMain") || formCode.equals("InvItemInstrumentsMain") || formCode.equals("InvItemSamplesMain")||formCode.equals("InvItemColumnsMain")){
					additSymbolsForTitle = " ";
				} else {
					additSymbolsForTitle = ": ";
				}
			}
		}
		title = titleNewFlag + " " + title + additSymbolsForTitle + subtitle;
		
		toReturn.put("formTitle", title);
		toReturn.put("tooltip", "");
		String tooltip = "";
		String subTitleTooltip = "";
		String titleWithNewLine = "";
		String formTitleCss = "";

		titleStrParts = title.split("\\{\\{");
		titleWithNewLine = titleStrParts.length > 0 ? titleStrParts[0] : "";
		if (formCode.equals("ExperimentAn") || formCode.equals("Experiment") || formCode.equals("ExperimentFor") || formCode.equals("ExperimentCP") // add ExperimentCP for "Continuous Process"
				|| formCode.startsWith("ExperimentPr") || formCode.equals("Step") || formCode.equals("StepFr") || formCode.startsWith("ExperimentStb") // add ExperimentStb for "Taro develop"
				|| formCode.equals("Action") || formCode.equals("Template") || formCode.equals("InvItemMaterialFr")  || formCode.equals("InvItemMaterialPr")  || formCode.equals("InvItemMaterial") || formCode.equals("InvItemInstrument") // kd 28072020 added for task-25483 Screens Header improvements
				|| formCode.equals("RecipeFormulation")){
			//arrange the title
			//String titleSuffix = "";
			String titleSubTitle = "";
			
			//if one of the parts or both of them are too long then shortening it and adding a title
			if (titleStrParts[0].length() > 36) {
				titleWithNewLine = (titleStrParts[0]).substring(0, 35) + "...";
				tooltip = titleStrParts[0];
			}
			if (titleStrParts.length > 1) {
				titleSubTitle = generalUtil.trimBrackets(titleStrParts[1]);
				
				if ((generalUtil.trimBrackets(titleStrParts[1])).length() > 55) {
					titleSubTitle = (generalUtil.trimBrackets(titleStrParts[1])).substring(0, 51) + " ...";
					subTitleTooltip = generalUtil.trimBrackets(titleStrParts[1]);
				}
				if ((formCode.equals("Step") || formCode.equals("StepFr")) && !titleSubTitle.equals("")){
					titleSubTitle = "Exp: " + titleSubTitle;
				}
				toReturn.put("formSubTitle", titleSubTitle);
				//titleWithNewLine = titleWithNewLine + titleSuffix;
			}
			//titleWithNewLine = titleWithNewLine + titleSuffix;
			formTitleCss = "white-space:pre";

		} 
		else if (formCode.equals("SelfTest") || formCode.startsWith("Workup") || formCode.startsWith("STest")) {

			//String titleSuffix = "";
			//if the sub title is too long, then shortening it and adding a tooltip
			String titleSubTitle = "";
			if (titleStrParts.length > 1) {
				titleSubTitle = generalUtil.trimBrackets(titleStrParts[1]);
				if ((titleStrParts[1]).length() > 55) {
					titleSubTitle = (titleSubTitle).substring(0, 51) + " ...";
					subTitleTooltip = titleSubTitle;
				}
			}
			toReturn.put("formSubTitle", titleSubTitle);
			//titleWithNewLine = titleWithNewLine + titleSuffix;
		}
		else if(formCode.equals("SpreadsheetTempla")){//fixed bug 8567
			if (titleStrParts[0].length() > 55) {
				titleWithNewLine = (titleStrParts[0]).substring(0, 51) + "...";
				tooltip = titleStrParts[0];
			}
		}

		toReturn.put("formTitleCss", formTitleCss);
		toReturn.put("formTitle", titleWithNewLine);
		if (!tooltip.isEmpty()) {
			toReturn.put("tooltip", "title=\"" + tooltip + "\"");
		}
		if (!subTitleTooltip.isEmpty()) {
			toReturn.put("subTitleTooltip", "title=\"" + subTitleTooltip + "\"");
		}
		
	    //Favorite...
		if(isFavorite(form.getFormCodeEntity())) {
			String countFavorite = "0";
			if(!isNewFormId) {
				countFavorite = generalDao.selectSingleStringNoException("select count(*) from FG_FAVORITE t where t.object_id = '" + formId + "' and t.creator_id = '" + userId + "'");
			}
			
			if(countFavorite.equals("0")) {	
				toReturn.put("favorite","<i class='fa fa-star-o' style='color:black' value='no' id='favoriteTitle' onclick=onChangefavorite('" + formId + "')></i>");
				
			} else {
				toReturn.put("favorite","<i class='fa fa-star' style='color:#62B2DB' value='yes' id='favoriteTitle' onclick=onChangefavorite('" + formId + "')></i>");
			}
		} else {
			toReturn.put("favorite","");
		}
		return toReturn;
	}

	private boolean isFavorite(String formCodeEntity) {
		return ("," + generalUtil.getNull(favoriteFormList) + ",").contains("," + formCodeEntity.toLowerCase() + ",");
	}

	private Map<String, String> getLastFormSaveValue(String formCode, String formId, String userId, String nameId,
			boolean isNewFormId, boolean isClone) {
		Map<String, String> toReturn = new HashMap<String, String>();
		List<FormLastSaveValue> formLastSaveValueList;
		List<FormAdditionalData> formAdditionalDataList;
		boolean isStruct = generalUtilForm.isStructFromByFormCode(formCode);
		if (isStruct) {
			if ((!isNewFormId) || (isClone)) {
				Map<String, String> formEntityImpcodeInsensitiveMap = getFormEntityImpcodeCaseInsensitiveMap(formCode);
				String sql_ = "";
				if (formEntityImpcodeInsensitiveMap.containsKey("PARENTID")) {
					String formCodeEntity = formCode;
					//to be on safe side ->
					try {
						Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
						formCodeEntity = generalUtil.getEmpty(form.getFormCodeEntity(), formCode);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
					String paraentId = formDao.getFormParentId(formCodeEntity, formId);
					sql_ = "select * from ( FG_S_" + formCodeEntity.toUpperCase() + "_V) where formId = '" + formId
							+ "' " + formDao.getWherePartForTmpData(generalUtilFormState.getSessionId(paraentId),
									formCode, paraentId);
				} else {
					sql_ = "select * from ( FG_S_" + formCode.toUpperCase() + "_ALL_V) where formId = '" + formId
							+ "' ";

				}

				logger.info("LAST STRUCT FORM DATA SQL: " + sql_);
				generalUtilLogger.logWrite(LevelType.DEBUG, "SQL lastsave (struct)=" + sql_, formId,
						ActivitylogType.SQLLastSave, null);

				//						toReturn.putAll(formDao.getStructFormLastSaveValueMap(sql_, formEntityImpcodeInsensitiveMap));
				Map<String, String> toReturn_ = generalDao.getMetaDataRowValues(sql_);
				for (Map.Entry<String, String> entry : toReturn_.entrySet()) {
					if (formEntityImpcodeInsensitiveMap.containsKey(entry.getKey())) {
						toReturn.put(formEntityImpcodeInsensitiveMap.get(entry.getKey()), entry.getValue());
					}
				}

				// add additional form data into the map
				sql_ = "select * from FG_FORMADDITIONALDATA where PARENTID='" + formId + "'";
				formAdditionalDataList = formDao.getFormLastSaveAdditionalDataList(sql_);
				//					formAdditionalDataList = jdbcTemplate.query(formId,
				//							"select * from FG_FORMADDITIONALDATA where PARENTID='" + formId + "'",
				//							new FormAdditionalDataMapper());
				for (FormAdditionalData formAdditionalData : formAdditionalDataList) {
					toReturn.put(formAdditionalData.getEntityImpCode(),
							generalUtil.getNull(formAdditionalData.getValue()));
				}
			}
		} else {
			// not in use in Adama
			if (!generalUtil.getNull(formId).equals("-1")) {
				if ((!isNewFormId) || (isClone)) {

					String sql_ = "select * from FG_FORMLASTSAVEVALUE where 1=1 and FORMID='" + formId + "' ";
					//						formLastSaveValueList = jdbcTemplate.query(sql_, new FormLastSaveValueMapper());
					formLastSaveValueList = formDao.getFormLastSaveValueList(sql_);
					for (FormLastSaveValue formLastSaveValue : formLastSaveValueList) {
						toReturn.put(formLastSaveValue.getEntityimpcode(),
								generalUtil.getNull(formLastSaveValue.getEntityimpvalue()));
					}

					//						generalUtilLogger.logWrite(LevelType.DEBUG,"SQL lastsave=" + sql_,formId,ActivitylogType.SQLLastSave,null);
				}
			}
		}

		try {
			//user display data by formCode
			String loginSessionId = generalUtil.getSessionIdNoException("NA");
			String sql_ = "select * from FG_FORMLASTSAVEVALUE where nvl(FORMCODE_NAME, FORMCODE_ENTITY) = '" + formCode
					+ "' and FORMID='-1' and "
					+ (!nameId.isEmpty()
							? (nameId.equals("-1")
									? "SAVE_NAME_ID is null and USERID='" + userId + "' and nvl(login_sessionid,'"
											+ loginSessionId + "') = '" + loginSessionId + "'"
									: ("SAVE_NAME_ID='" + nameId + "'"))
							: ("USERID='" + userId + "' and nvl(login_sessionid,'" + loginSessionId + "') = '"
									+ loginSessionId + "'"));
			//			formLastSaveValueList = jdbcTemplate.query(sql_, new FormLastSaveValueMapper());
			formLastSaveValueList = formDao.getFormLastSaveValueList(sql_);
			List<FormEntity> formEntityList = formBuilderDao.getFormEntity(formCode, "%"); //from formBuilderDao
			for (FormEntity formEntity : formEntityList) {
				for (FormLastSaveValue formLastSaveValue : formLastSaveValueList) {
					if ((formEntity.getEntityImpCode() + "_USER_LAST_SAVE_VALUE")
							.equalsIgnoreCase(formLastSaveValue.getEntityimpcode())) {
						toReturn.put(formLastSaveValue.getEntityimpcode(),
								generalUtil.getNull(formLastSaveValue.getEntityimpvalue()));
					} else if (formEntity.getEntityImpCode().equalsIgnoreCase(formLastSaveValue.getEntityimpcode())
							&& !isPreventSave(formEntity, false)) {
						toReturn.put(formLastSaveValue.getEntityimpcode(),
								generalUtil.getNull(formLastSaveValue.getEntityimpvalue()));
					}
				}
			}
		} catch (Exception e) {
			//not making the map empty
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return toReturn != null ? toReturn : new HashMap<String, String>();
	}

	//
	private Map<String, String> getFormEntityImpcodeCaseInsensitiveMap(String formCode) {
		HashMap<String, String> toReturn = new HashMap<String, String>();
		//	FormType formTyep= generalUtilForm.getFromType(formCode);
		List<FormEntity> formEntityList = formBuilderDao.getFormEntity(formCode, "%");
		for (FormEntity formEntity : formEntityList) {
			//    	if(!formEntity.getEntityImpClass().equals("ElementDataTableApiImp") || formEntity.getEntityImpInit().contains("Multiple")) { //TODO (next version?) remove this workaround (avoid data table save or handel in customer code)
			if (!isPreventSave(formEntity, true)) {
				toReturn.put(formEntity.getEntityImpCode().toUpperCase(), formEntity.getEntityImpCode());
			}
			//    	} else {
			//    		logger.debug("not enetred formEntity=" + formEntity.getEntityImpCode());
			//    	}
		}
		return toReturn;
	}

	private boolean isPreventSave(FormEntity formEntity, boolean isStruct) {
		// TODO this should be in customer pack. we should also remove from the map element marked as prevent save (and in this case we no longer worry old data that has already saved in the DB) - but check for example - template screen and source_exce in adama
		String formCode = formEntity.getFormCode();
		if (isStruct) {
			if (formEntity.getEntityImpClass().equals("ElementDataTableApiImp")
					&& !formEntity.getEntityImpInit().contains("Multiple")) {
				return true;
			}
			
			//"Taro develop" patch - we always need the default values from the experiment screen (last storage selection and experiment id and not the value saved in the DB - we do not use prevent save because we need them after saving sample link)
			if(formCode.equals("SampleLink")) {
				if(formEntity.getEntityImpCode().equals("linkStorage") || formEntity.getEntityImpCode().equals("linkExperimentId")) {
					return true;
				}
			}
		} else {
			if (formEntity.getEntityImpClass().equals("ElementDataTableApiImp")
					&& !formEntity.getEntityImpInit().contains("Multiple")) {
				if (!formCode.equals("Main") && !formCode.equals("InvItemMaterialsMain")
						&& !formCode.equals("InvItemInstrumentsMain")&&!formCode.equals("InvItemColumnsMain")) { // to supprt old version
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public ModelAndView initFormParam(long stateKey, String formCode, String formId, String userId,
			Map<String, String[]> requestMap) {
		//init
		long start = System.currentTimeMillis();
		generalUtilLogger.logWriter(LevelType.DEBUG,"Start initFormParam formCode=" + formCode + ", formId=" + formId,
				ActivitylogType.PerformanceJava, formId);
		
		boolean isNewFormId = false;
		Map<String, String> outParamMap = new HashMap<String, String>();
		Map<String, String> lastSaveValMap = new HashMap<String, String>();
		generalUtilFormState.initForm(isNewFormId, lastSaveValMap, stateKey, formCode, userId, formId, "", "",
				requestMap, outParamMap); 
		
		generalUtilLogger.logWriter(LevelType.DEBUG,"End initFormParam formCode=" + formCode + ", formId=" + formId,
				ActivitylogType.PerformanceJava, formId);
		long time = System.currentTimeMillis() - start;
		generalDao.logMessage("################ INIT FORM PARAM TIME = " + time + "################ ");
		return null;
	}

	private boolean isNewFormId(String formCode, String formId) {
		boolean isNewFormId = false;
		List<Form> formList = formDao.getFormInfoLookup(formCode, "%", true);
		FormType formType = FormType.valueOf(formList.get(0).getFormType());

		//init isStruct 
		boolean isStructFormCode = false;
		isStructFormCode = formType.getStructureForm();

		//init isNew 
		if (isStructFormCode) {
			isNewFormId = formDao.isNewFormId(formCode, formId);
		}
		return isNewFormId;
	}

	//ajax call
	@Override
	public ActionBean getAction(long stateKey, String action, List<DataBean> dataBeanList, String formCode,
			String formId, String userId) {

		//init
		//form catalog -> clean
		generalUtilFormState.cleanFormCatalog(stateKey, formCode);

		//form value -> from user selection
		generalUtilFormState.setFormValue(stateKey, formCode, initElementValueMapByBeanList(dataBeanList));
		
		//skipAuthz for admin and system by url parameter
		boolean skipAuthz = false;
		String isSkipAuthz = generalUtilFormState.getFormParam(stateKey, formCode, "$P{ISSKIPAUTHZ}");
		if (isSkipAuthz != null) {
			String userName = generalUtil.getSessionUserName();
			if(userName.equals("system") || userName.equals("admin")) {
				skipAuthz =isSkipAuthz != null && isSkipAuthz.equals("1");
			}
		}

		Map<String, String> formEntityHtmlCodeMap = renderFormAjax(stateKey, formId, formCode,
				generalUtil.getNull(action), skipAuthz);

		return new ActionBean("no action needed", mapToList(formEntityHtmlCodeMap), "");
	}

	//ajax call 
	@Override
	public ActionBean getSqlByCatalogDBBean(ActionBean requestAction) {
		return null;//TODO V>9.6 IREPORT
	}

	private Map<String, String> renderFormInit(long stateKey, String formCode, String formId, boolean skipAuthz) {

		//init renderForm
		Map<Integer, List<Element>> elementMapTree = generalUtilFormState.getElementMapTree(stateKey, formCode);
		Map<String, String> formEntityHtmlCodeMap = new HashMap<String, String>();
		String paramTitle = "";

		String lastsSelectedValue = null;
		String userLastSaveVal = null;
		boolean isIncreaseLevelWhenDataFound = false;
		int currentLevel = 0;

		Iterator<Entry<Integer, List<Element>>> mapTreeIterator = elementMapTree.entrySet().iterator();

		while (mapTreeIterator.hasNext()) { //level loop 

			//get pairs - key: level_, value: element list
			Map.Entry<Integer, List<Element>> pairs = (Map.Entry<Integer, List<Element>>) mapTreeIterator.next();
			Integer level_ = pairs.getKey();

			//init level loop 
			if (isIncreaseLevelWhenDataFound) {
				isIncreaseLevelWhenDataFound = false;
				currentLevel++;
			}

			if (level_ == currentLevel) {

				//init element loop
				List<Element> elementLevelList = pairs.getValue();
				//logger.debug("start [level_ == currentLevel] level_=" + level_ + ", elements=" + elementListToString(elementLevelList)); 

				//element loop (same level) - get values and update state
				for (Element element : elementLevelList) {
					//logger.debug("element.getImpCode()=" + element.getImpCode()); 

					//String ec_ = element.getImpCode();
					logger.debug("this element=" + element.getImpCode() + ", parent=" + element.getParentElement()
							+ ", parent val=" + generalUtil.getNull(
									generalUtilFormState.getFormValue(stateKey, formCode, element.getParentElement())));
					logger.debug(generalUtil.mapToString("catalog map",
							generalUtilFormState.getFormCatalog(stateKey, formCode, null)));

					userLastSaveVal = generalUtilFormState.getFormValue(stateKey, formCode,
							element.getImpCode() + "_USER_LAST_SAVE_VALUE");
					lastsSelectedValue = generalUtilFormState.getFormValue(stateKey, formCode, element.getImpCode());
					// check if default we take from here
					if (generalUtil.getNull(lastsSelectedValue).equals("")) { // check also default value if empty
						lastsSelectedValue = element.getDefaultValue(stateKey, formId, formCode);
					}
					isIncreaseLevelWhenDataFound = (element.isValExists(lastsSelectedValue)
							|| isIncreaseLevelWhenDataFound); //  || isIncreaseLevelWhenDataFound => because its enough to find value in one of the element in the level 

					//update ValueState
					generalUtilFormState.setFormValue(stateKey, formCode, element.getImpCode(), lastsSelectedValue);

					String parentElementVal = generalUtil
							.getNull(generalUtilFormState.getFormValue(stateKey, formCode, element.getParentElement()));
					Element pElement = generalUtilFormState.getElementBean(stateKey, formCode,
							element.getParentElement());
					//                      logger.info("**** Element data flow (init -> level_ == currentLevel) - [getImpCode=" + element.getImpCode() + "] - level_" + level_ + ",parentElement=" + element.getParentElement() + ", parentElement val=" + parentElementVal + ", isCatalogFlowElement=" + element.getIsAjaxDataFlowElement());

					if (level_ != 0 && !pElement.isValExists(parentElementVal)) { //render empty condition
						boolean renderEmptyElement = true;
						//                          logger.info("**** InitHtml call [getImpCode=" + element.getImpCode() + "] with parameters: renderEmptyElement=" + renderEmptyElement + ", lastsSelectedValue=" + "");
						formEntityHtmlCodeMap.putAll(element.getInitHtml(stateKey, formId, renderEmptyElement, "",
								userLastSaveVal, element.getImpCode(), getFormElementAttr(element),
								" onChangeAjax('" + element.getImpCode() + "'); "));
						generalUtilFormState.setFormCatalog(stateKey, formCode,
								element.getCatalogItemFilterMapByInputVal("", false));
						generalUtilFormState.setFormParam(stateKey, formCode,
								element.getCurrentParameMapByInputVal(""));
					} else {
						boolean renderEmptyElement = false;
						//                          logger.info("**** InitHtml call [getImpCode=" + element.getImpCode() + "] with parameters: renderEmptyElement=" + renderEmptyElement + ", lastsSelectedValue=" + lastsSelectedValue);
						formEntityHtmlCodeMap.putAll(element.getInitHtml(stateKey, formId, renderEmptyElement,
								lastsSelectedValue, userLastSaveVal, element.getImpCode(), getFormElementAttr(element),
								" onChangeAjax('" + element.getImpCode() + "'); "));
						//                          if(element.getIsAjaxDataFlowElement()) {  
						//val
						Map<String, String> mapCurrentVal = element.getCatalogItemFilterMapByInputVal(
								generalUtilFormState.getFormValue(stateKey, formCode, element.getImpCode()), false);
						generalUtilFormState.setFormCatalog(stateKey, formCode, mapCurrentVal);
						if (mapCurrentVal != null && mapCurrentVal.size() > 0) {
							paramTitle = "[Current catalog (init) - getImpCode=" + element.getImpCode() + "]";
							logger.info(generalUtil.mapToString("\n\n---- Catalog [" + paramTitle + "] ----\n",
									mapCurrentVal));
						}
						//param
						Map<String, String> mapCurrentParam = element.getCurrentParameMapByInputVal(
								generalUtilFormState.getFormValue(stateKey, formCode, element.getImpCode()));
						generalUtilFormState.setFormParam(stateKey, formCode, mapCurrentParam);
						if (mapCurrentParam != null && mapCurrentParam.size() > 0) {
							paramTitle = "[Current Parameters (init) - getImpCode=" + element.getImpCode() + "]";
							logger.info(mapParamToString("\n\n----Param [" + paramTitle + "] ----\n", mapCurrentParam));
						}
					}
				}

			} else if (level_ >= currentLevel + 1) {

				//init element loop
				List<Element> elementLevelList = pairs.getValue();
				//logger.debug("start  [level_ >= currentLevel + 1]  level_=" + level_ + ", isIncreaseLevelWhenDataFound (false => renderEmptyElement)=" + isIncreaseLevelWhenDataFound + ", elements=" + elementListToString(elementLevelList) + "\n"); 

				//element loop (same level) 
				for (Element element : elementLevelList) {
					//                      System.out.println("this element=" + element.getImpCode() + ", parent=" + element.getParentElement()  + ", parent val=" + generalUtil.getNull(generalUtilFormState.getFormValue(stateKey, formCode, element.getParentElement())));

					lastsSelectedValue = "";
					boolean renderEmptyElement = true;

					userLastSaveVal = generalUtilFormState.getFormValue(stateKey, formCode,
							element.getImpCode() + "_USER_LAST_SAVE_VALUE");
					lastsSelectedValue = generalUtilFormState.getFormValue(stateKey, formCode, element.getImpCode());

					if (!element.getIsAjaxDataFlowElement() && element.isValExists(lastsSelectedValue)) {
						lastsSelectedValue = generalUtilFormState.getFormValue(stateKey, formCode,
								element.getImpCode());
						if (generalUtil.getNull(lastsSelectedValue).equals("")) { // check also default value if empty
							lastsSelectedValue = element.getDefaultValue(stateKey, formId, formCode);
						}
						renderEmptyElement = false;
					}

					//update ValueState
					generalUtilFormState.setFormValue(stateKey, formCode, element.getImpCode(), lastsSelectedValue);

					//                      logger.info("**** Element data flow (init -> level_ >= currentLevel + 1) - [getImpCode=" + element.getImpCode() + "] - level_" + level_ + ", isCatalogFlowElement=" + element.getIsAjaxDataFlowElement());

					formEntityHtmlCodeMap.putAll(element.getInitHtml(stateKey, formId, renderEmptyElement,
							lastsSelectedValue, userLastSaveVal, element.getImpCode(), getFormElementAttr(element),
							" onChangeAjax('" + element.getImpCode() + "'); "));

					//update catalog state
					if (element.getIsAjaxDataFlowElement()) {
						generalUtilFormState.setFormCatalog(stateKey, formCode,
								element.getCatalogItemFilterMapByInputVal("", false));
						generalUtilFormState.setFormParam(stateKey, formCode,
								element.getCurrentParameMapByInputVal(""));
					}

				}
			}
		}
		//      System.out.println("FormServiceMultiRoot renderForm state summary:\n" + generalUtilFormState.getSummary(stateKey, formCode));

		//      generalUtilFormState.geta

		//invoke authorizationElement in the end
		if(!skipAuthz) {
			Element authorizationElement = generalUtilFormState.getAuthorizationElement(stateKey, formCode);
			if (authorizationElement != null) {
				formEntityHtmlCodeMap.putAll(authorizationElement.getInitHtml(stateKey, formId, false, "", "",
						authorizationElement.getImpCode(), getFormElementAttr(authorizationElement), ""));
			}
		}

		return formEntityHtmlCodeMap;
	}

	/**
	 * 
	 * @param element
	 * @return attributes we add to every element. using supper class functions (with the info of saveType [defined in spring beans] and prevent save / additional data save [defined in the form builder])
	 */
	private String getFormElementAttr(Element element) {
		String retAttr = FORM_ELEMENT_ATTRIBUTE_;
		if (element.isPreventSave()) {
			retAttr += " " + FORM_ELEMENT_PREVENT_SAVE_ATTRIBUTE_;
		} else if (element.isAdditionalData()) {
			retAttr += " " + FORM_ELEMENT_FORM_ADDITIONALDATA_ATTRIBUTE_;
		} else {
			retAttr += " " + FORM_ELEMENT_SAVE_ATTRIBUTE_;
			retAttr += " " + FORM_ELEMENT_SAVE_TYPE_ATTRIBUTE_.replace("none", element.getSaveType());
		}
		return retAttr;
	}

	private Map<String, String> renderFormAjax(long stateKey, String formId, String formCode,
			String currentElementCode, boolean skipAuthz) {

		//init renderForm
		Map<String, String> formEntityHtmlCodeMap = new HashMap<String, String>();
		String paramTitle = "";
		StringBuilder updatedInfo = new StringBuilder();
		String stringFromMap;

		String lastsSelectedValue = null;
		boolean isIncreaseLevelWhenDataFound = false;
		int currentLevel = generalUtilFormState.getCurrentLevelByElementCode(stateKey, formCode, currentElementCode);

		Map<Integer, List<Element>> elementMapTree = generalUtilFormState.getElementMapTree(stateKey, formCode);
		Iterator<Entry<Integer, List<Element>>> mapTreeIterator = elementMapTree.entrySet().iterator();

		while (mapTreeIterator.hasNext()) { //level loop 

			//init level loop 
			if (isIncreaseLevelWhenDataFound) {
				isIncreaseLevelWhenDataFound = false;
				currentLevel++;
			}

			//get pairs - key: level_, value: element list
			Map.Entry<Integer, List<Element>> pairs = (Map.Entry<Integer, List<Element>>) mapTreeIterator.next();
			Integer level_ = pairs.getKey();
			stringFromMap = generalUtil.mapToString("CURRENT - CATALOG MAP",
					generalUtilFormState.getFormCatalog(stateKey, formCode, null));
			logger.debug(stringFromMap);
			updatedInfo.append(stringFromMap).append("<br />");

			if (level_ < currentLevel) {

				//init element loop
				List<Element> elementLevelList = pairs.getValue();
				//logger.debug("start [level_ < currentLevel] level_=" + level_ + ", elements=" + elementListToString(elementLevelList) + "\n"); 

				//element loop (same level) - get values and update state
				for (Element element : elementLevelList) {
					//                  System.out.println("this element=" + element.getImpCode() + ", parent=" + element.getParentElement()  + ", parent val=" + generalUtil.getNull(generalUtilFormState.getFormValue(stateKey, formCode, element.getParentElement())));

					//get value
					lastsSelectedValue = generalUtilFormState.getFormValue(stateKey, formCode, element.getImpCode()); //TODO also default /  user data that can be empty ->
					//                  logger.info("**** Element data flow (ajax -> level_ < currentLevel) - [getImpCode=" + element.getImpCode() + "] - level_" + level_ + ", lastsSelectedValue=" + lastsSelectedValue);

					//update value state
					generalUtilFormState.setFormValue(stateKey, formCode, element.getImpCode(), lastsSelectedValue);

					//update catalog state
					generalUtilFormState.setFormCatalog(stateKey, formCode, element.getCatalogItemFilterMapByInputVal(
							generalUtilFormState.getFormValue(stateKey, formCode, element.getImpCode()), true));
					generalUtilFormState.setFormParam(stateKey, formCode, element.getCurrentParameMapByInputVal(
							generalUtilFormState.getFormValue(stateKey, formCode, element.getImpCode())));
				}

			} else if (level_ == currentLevel) {

				//init element loop
				List<Element> elementLevelList = pairs.getValue();
				//logger.debug("start [level_ == currentLevel] level_=" + level_ + ", elements=" + elementListToString(elementLevelList)); 

				//element loop (same level) - get values and update state
				for (Element element : elementLevelList) {
					//                  System.out.println("this element=" + element.getImpCode() + ", parent=" + element.getParentElement()  + ", parent val=" + generalUtil.getNull(generalUtilFormState.getFormValue(stateKey, formCode, element.getParentElement())));

					if (element.getImpCode().equals(currentElementCode)
							|| generalUtilFormState.isParenteElement(stateKey, formCode, element, currentElementCode)) {

						//update element html if needed
						//get value
						lastsSelectedValue = generalUtilFormState.getFormValue(stateKey, formCode,
								element.getImpCode());
						if (element.getImpCode().equals(currentElementCode) || element.isKeepValueOnParentChange()) {
							lastsSelectedValue = generalUtilFormState.getFormValue(stateKey, formCode,
									element.getImpCode());
						} else { // in init call we check also default value...
							lastsSelectedValue = element.getDefaultValue(stateKey, formId, formCode);
						}
						isIncreaseLevelWhenDataFound = (!generalUtil.getNull(lastsSelectedValue).equals("")
								|| isIncreaseLevelWhenDataFound); //  || isIncreaseLevelWhenDataFound => because its enough to find value in one of the element in the level 

						//update ValueState
						generalUtilFormState.setFormValue(stateKey, formCode, element.getImpCode(), lastsSelectedValue);

						String parentElementVal = generalUtil.getNull(
								generalUtilFormState.getFormValue(stateKey, formCode, element.getParentElement()));
						Element pElement = generalUtilFormState.getElementBean(stateKey, formCode,
								element.getParentElement());
						//                        logger.info("**** Element data flow (ajax -> level_ == currentLevel) - [getImpCode=" + element.getImpCode() + "] - level_=" + level_ + ", isCurrentElement=" + element.getImpCode().equals(currentElementCode) + ", parentElement=" + element.getParentElement() + ", parentElement val=" + parentElementVal + ", isCatalogFlowElement=" + element.getIsAjaxDataFlowElement());

						if (element.getImpCode().equals(currentElementCode)) {
							//val
							Map<String, String> mapCurrentVal = element.getCatalogItemFilterMapByInputVal(
									generalUtilFormState.getFormValue(stateKey, formCode, element.getImpCode()), true);
							generalUtilFormState.setFormCatalog(stateKey, formCode, mapCurrentVal);
							if (mapCurrentVal != null && mapCurrentVal.size() > 0) {
								paramTitle = "[Current catalog (ajax currentElement) - getImpCode="
										+ element.getImpCode() + "]";
								stringFromMap = generalUtil.mapToString(
										"<br />----Catalog [" + paramTitle + "] ----<br />", mapCurrentVal);
								logger.info(generalUtil.mapToString("\n\n----Catalog [" + paramTitle + "] ----\n",
										mapCurrentVal));
								updatedInfo.append(stringFromMap).append("<br />");
							}
							//param
							Map<String, String> mapCurrentParam = element.getCurrentParameMapByInputVal(
									generalUtilFormState.getFormValue(stateKey, formCode, element.getImpCode()));
							generalUtilFormState.setFormParam(stateKey, formCode, mapCurrentParam);
							if (mapCurrentParam != null && mapCurrentParam.size() > 0) {
								paramTitle = "[Current Parameters (ajax currentElement) - getImpCode="
										+ element.getImpCode() + "]";
								stringFromMap = mapParamToString("<br />----Param [" + paramTitle + "] ----<br />",
										mapCurrentParam);
								logger.info(
										mapParamToString("\n\n----Param [" + paramTitle + "] ----\n", mapCurrentParam));
								updatedInfo.append(stringFromMap).append("<br />");
							}

						} else {
							//String ec_ = element.getImpCode();
							//update ValueState  
							if (!pElement.isValExists(parentElementVal)) {
								//                        	if(parentElementVal.equals("")) {
								boolean renderEmptyElement = true;
								formEntityHtmlCodeMap.put(element.getImpCode(),
										element.getHtmlBody(stateKey, formId, renderEmptyElement, "",
												element.getImpCode(), getFormElementAttr(element),
												" onChangeAjax('" + element.getImpCode() + "'); "));
								generalUtilFormState.setFormCatalog(stateKey, formCode,
										element.getCatalogItemFilterMapByInputVal("", false));
								generalUtilFormState.setFormParam(stateKey, formCode,
										element.getCurrentParameMapByInputVal(""));

							} else {
								boolean renderEmptyElement = false;

								//                              logger.info("**** put Html call [getImpCode=" + element.getImpCode() + "] with parameters: renderEmptyElement=" + renderEmptyElement + ", lastsSelectedValue=" + lastsSelectedValue + ", isCatalogFlowElement=" + element.getIsAjaxDataFlowElement());

								formEntityHtmlCodeMap.put(element.getImpCode(),
										element.getHtmlBody(stateKey, formId, renderEmptyElement, lastsSelectedValue,
												element.getImpCode(), getFormElementAttr(element),
												" onChangeAjax('" + element.getImpCode() + "'); "));
								//                              if(element.getIsAjaxDataFlowElement()) { 
								//val
								Map<String, String> mapCurrentVal = element.getCatalogItemFilterMapByInputVal(
										generalUtilFormState.getFormValue(stateKey, formCode, element.getImpCode()),
										false);
								generalUtilFormState.setFormCatalog(stateKey, formCode, mapCurrentVal);
								if (mapCurrentVal != null && mapCurrentVal.size() > 0) {
									paramTitle = "[Current catalog (ajax) - getImpCode=" + element.getImpCode() + "]";
									stringFromMap = generalUtil.mapToString(
											"<br />----Catalog [" + paramTitle + "] ----<br />", mapCurrentVal);
									logger.info(generalUtil.mapToString("\n\n----Catalog [" + paramTitle + "] ----\n",
											mapCurrentVal));
									updatedInfo.append(stringFromMap).append("<br />");
								}
								//param
								Map<String, String> mapCurrentParam = element.getCurrentParameMapByInputVal(
										generalUtilFormState.getFormValue(stateKey, formCode, element.getImpCode()));
								generalUtilFormState.setFormParam(stateKey, formCode, mapCurrentParam);
								if (mapCurrentParam != null && mapCurrentParam.size() > 0) {
									paramTitle = "[Current Parameters (ajax) - getImpCode=" + element.getImpCode()
											+ "]";
									stringFromMap = mapParamToString("<br />----Param [" + paramTitle + "] ----<br />",
											mapCurrentParam);
									logger.info(mapParamToString("\n\n----Param [" + paramTitle + "] ----\n",
											mapCurrentParam));
									updatedInfo.append(stringFromMap).append("<br />");
								}
								//                              }
							}
						}
					}
				}
			} else if (level_ >= currentLevel + 1) {

				//init element loop
				List<Element> elementLevelList = pairs.getValue();
				//logger.debug("start  [level_ >= currentLevel + 1]  level_=" + level_ + ", isIncreaseLevelWhenDataFound (false => renderEmptyElement)=" + ", elements=" + elementListToString(elementLevelList) + "\n"); 

				//element loop (same level) 
				for (Element element : elementLevelList) {
					//                  System.out.println("this element=" + element.getImpCode() + ", parent=" + element.getParentElement()  + ", parent val=" + generalUtil.getNull(generalUtilFormState.getFormValue(stateKey, formCode, element.getParentElement())));

					if ((element.getImpCode().equals(currentElementCode) || generalUtilFormState
							.isParenteElement(stateKey, formCode, element, currentElementCode))) {

						//String ec_ = element.getImpCode();
						lastsSelectedValue = "";
						boolean renderEmptyElement = true;
						String parentElementVal = generalUtil.getNull(
								generalUtilFormState.getFormValue(stateKey, formCode, element.getParentElement()));
						Element pElement = generalUtilFormState.getElementBean(stateKey, formCode,
								element.getParentElement());
						if (!element.getIsAjaxDataFlowElement() && pElement.isValExists(parentElementVal)) {
							//                        if(!element.getIsAjaxDataFlowElement() && !parentElementVal.equals("")) { 
							lastsSelectedValue = generalUtilFormState.getFormValue(stateKey, formCode,
									element.getImpCode());
							if (generalUtil.getNull(lastsSelectedValue).equals("")) { // check also default value if empty
								lastsSelectedValue = element.getDefaultValue(stateKey, formId, formCode);
							}
							renderEmptyElement = false;
						}

						//update ValueState
						generalUtilFormState.setFormValue(stateKey, formCode, element.getImpCode(), "");
						//                  generalUtilFormState.setFormParam(stateKey, formCode, element.getCurrentVal());

						//                      logger.info("**** Element data flow (ajax -> level_ >= currentLevel + 1) - [getImpCode=" + element.getImpCode() + "] - level_" + level_ + ", isCatalogFlowElement=" + element.getIsAjaxDataFlowElement());

						//set element html (putAll in isInitCall / put in ajax)
						//                      boolean renderEmptyElement =  element.getIsAjaxDataFlowElement();//level_ > currentLevel + 1 || generalUtil.getNull(generalUtilFormState.getFormValue(stateKey, formCode, element.getParentElement())).equals("");

						formEntityHtmlCodeMap.put(element.getImpCode(),
								element.getHtmlBody(stateKey, formId, renderEmptyElement, lastsSelectedValue,
										element.getImpCode(), getFormElementAttr(element),
										" onChangeAjax('" + element.getImpCode() + "'); "));

						//update catalog state
						if (element.getIsAjaxDataFlowElement()) {
							generalUtilFormState.setFormCatalog(stateKey, formCode,
									element.getCatalogItemFilterMapByInputVal("", false));
							generalUtilFormState.setFormParam(stateKey, formCode,
									element.getCurrentParameMapByInputVal(""));
						}
					}
				}
			}
		}
		//      System.out.println("FormServiceMultiRoot renderForm state summary currentElementCode=" + currentElementCode + ":\n" + generalUtilFormState.getSummary(stateKey, formCode));

		//invoke guthorizationElement in the end
		if(!skipAuthz) {
			Element authorizationElement = generalUtilFormState.getAuthorizationElement(stateKey, formCode);
			if (authorizationElement != null) {

				formEntityHtmlCodeMap.put(authorizationElement.getImpCode(),
						authorizationElement.getHtmlBody(stateKey, formId, false, lastsSelectedValue,
								authorizationElement.getImpCode(), getFormElementAttr(authorizationElement), ""));
			}
		}
		
		
		
		String userName = generalUtil.getSessionUserName();
		if (userName.equals("system")) {
			formEntityHtmlCodeMap.put("UPDATED_INFO_ONAJAXCHANGE", updatedInfo.toString());
		} else {
			formEntityHtmlCodeMap.put("UPDATED_INFO_ONAJAXCHANGE", "");
		}

		return formEntityHtmlCodeMap;
	}

	//  private Map<String, String> reverse(Map<String, String> formEntityHtmlCodeMap) {
	//      Map <String, String> revMapToReturn = new HashMap<String, String>();
	//      Iterator<Entry<String, String>> formEntityHtmlCodeMapIterator = formEntityHtmlCodeMap.entrySet().iterator();
	//       
	//      while (formEntityHtmlCodeMapIterator.hasNext()) { //level loop 
	//          revMapToReturn
	//      }
	//          
	//  }

	//    private String elementListToString(List<Element> elementList) {
	//        StringBuilder toReturn = new StringBuilder();
	//
	//        for (Element e : elementList) {
	//            toReturn.append(e.getImpCode() + ",");
	//        }
	//
	//        return toReturn.toString().length() > 0 ? toReturn.toString().substring(0, toReturn.toString().length() - 1)
	//                : toReturn.toString();
	//    }  

	private List<DataBean> mapToList(Map<String, String> formEntityHtmlCodeMap) {
		List<DataBean> toReturn = new ArrayList<DataBean>();
		for (Map.Entry<String, String> entry : formEntityHtmlCodeMap.entrySet()) {
			toReturn.add(new DataBean(entry.getKey(), entry.getValue(), BeanType.NA, ""));
		}
		return toReturn;
	}

	private Map<String, String> initElementValueMapByBeanList(List<DataBean> dataBeanList) {
		Map<String, String> elementValueMap = new HashMap<String, String>();
		for (DataBean dataBean : dataBeanList) {
			elementValueMap.put(dataBean.getCode(), generalUtil.getNull(dataBean.getVal()));
		}
		return elementValueMap;
	}

	private String mapParamToString(String title, Map<String, String> map) {
		StringBuilder toReturn = new StringBuilder(title);
		for (Map.Entry<String, String> entry : map.entrySet()) {
			toReturn.append("\nitem: $P{" + generalUtil.getNull(entry.getKey()).toUpperCase() + "}, value: "
					+ entry.getValue());
		}
		return toReturn.toString();
	}

	@Override
	public String getCorrectFormCode(String formId, String formCode) {
		List<Form> formList = formDao.getFormInfoLookup("%", "%", false);
		for (Form form : formList) {
			if (generalUtil.getNull(form.getFormCodeEntity()).equals(formCode)
					&& !generalUtil.getNull(form.getFormCode()).equals(formCode)) {
				try {
					formCode = formDao.getFormCodeBySeqId(formId);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					return formCode;
				}
				break;
			}
		}
		return formCode;
	}

	@Override
	public String getCorrectNewFormCode(String formId, String formCode, Map<String, String[]> requestMap) {
		//workaround - should be in Adama Customer code
		String formCodeToReturn = formCode;

		try {
			String parentId = "";
			if (formCode.equals("Step")) {
				for (Map.Entry<String, String[]> entry : requestMap.entrySet()) {
					String key = entry.getKey();
					String valArray[] = entry.getValue();
					if (key.equalsIgnoreCase("PARENT_ID")) {
						parentId = valArray[0];
						break;
					}

				}

				if (formDao.getFormCodeBySeqId(parentId).equals("ExperimentFor")) {
					formCodeToReturn = "StepFr";
				}

			} else if (formCode.equals("SelfTestMain")) { //TODO develop remove main screen in organic selt test
				
				for (Map.Entry<String, String[]> entry : requestMap.entrySet()) {
					String key = entry.getKey();
					String valArray[] = entry.getValue();
					if (key.equalsIgnoreCase("PARENT_ID")) {
						parentId = valArray[0];
						break;
					}

				}
				
				if (!generalUtil.getNull(parentId).equals("")) {
					String stepFormCode = formDao.getFromInfoLookup("Action", LookupType.ID, parentId, "stepFormCode");
					if(!stepFormCode.equals("StepFr")) {
						formCodeToReturn = "SelfTest";
					}
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Error eval Step type (if its SteFr) e=" + e.toString());
		}
		return formCodeToReturn;
	}

	@Override
	public void cleanForm(long stateKey, String formCode_doBack) {
		generalUtilFormState.cleanForm(stateKey, formCode_doBack);
	}

//	@Override
//	public ActionBean getTitleAndSubtitleForm(ActionBean requestAction) {
//		List<Form> formList = formBuilderDao.getForm(requestAction.getData().get(0).getVal(), "%", true);
//		String title = generalUtil.getNull(formList.get(0).getTitle());
//		String subtitle = "";
//		try {
//			subtitle = generalUtil.getNull(formList.get(0).getSubtitle());
//			if (!subtitle.equals("") && subtitle.contains("$P{")) {
//				// replace all parameter ->
//				long stateKey = generalUtil.getNullLong(requestAction.getData().get(1).getVal());
//				subtitle = generalUtilFormState.replaceFormParam(stateKey, formList.get(0).getFormCode(), subtitle,
//						true);
//			}
//			// clean expected [] or () when no parameters founds:
//			subtitle = subtitle.replaceAll("\\[\\s+\\]", "").replaceAll("\\(\\s+\\)", "");
//		} catch (Exception e) {
//			try {
//				subtitle = generalUtil.getNull(formList.get(0).getSubtitle());
//			} catch (Exception e1) {
//				// DO NOTHING
//			}
//		}
//		String toReturn = title + " " + subtitle;
//		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
//	}

	@Override
	public String getFormType(String formCode) {
		// TODO Auto-generated method stub
		Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
		String formType = form.getFormType();
		return formType;
	}
	
	@Override
	public ActionBean updateWFState(long stateKey,List<DataBean> dataBeanList,String formCode,String formId,String userId,String isNew){
		String retVal = "";
		try {
			retVal = formState.setWFState(stateKey, formCode, userId, formId, Boolean.parseBoolean(isNew), dataBeanList);
			return new ActionBean("no action needed", generalUtil.StringToList(retVal), "");
		} catch (Exception ex) {
			return new ActionBean("no action needed", generalUtil.StringToList(retVal), ex.getMessage());
		}						
	}
	
	@Override
	/**
	 * getFileContent
	 */
	public String getFileContent(MultipartFile file, String formCodeFull, String elementId) {
		StringBuilder sb = new StringBuilder();
		try{
			// Open the sample file as a stream for insertion into the Blob column
			InputStream is = file.getInputStream();
			
			int ch;
			while((ch = is.read()) != -1)
			    sb.append((char)ch);
			
			
// Close the stream
			is.close();
			if(sb.toString().isEmpty()){
				String sql = "select t.file_content from fg_files t where t.file_id = '" + elementId + "'";//saved file
				InputStream blobStream = generalDao.getInputStreamFromBlob(sql);
				int ch1;
				while((ch1 = blobStream.read()) != -1)
				    sb.append((char)ch1);
				
				
	// Close the stream
				blobStream.close();
				
			}
		} catch (Exception ex) { // Trap SQL errors
			generalUtilLogger.logWrite(ex);
			sb = new StringBuilder();
		} return sb.toString();
	}

	@Override
	public RedirectInfo redirectRules(String formId, String formCode, String parentId, Boolean isNew) {
		String sql = "";
		RedirectInfo redirectInfo = new RedirectInfo(formId, formCode, "", isNew);
		try {
			//************** StepFr **************
			if(formCode.equals("StepFr")) {
				JSONObject jsonResult = null;
				//new step - create stepFr and navigate to expeirmentFor + open the new step cube
				if (isNew) {
					try {
						jsonResult = commonFunc.createNewStepIframeData(parentId,new HashMap<String,String>());
					} catch (Exception e) {
						//DD NOTHING
					}
					
					redirectInfo.setFormCode("ExperimentFor");
					redirectInfo.setFormId(parentId);
					redirectInfo.setTab("Overview");
					redirectInfo.setNew(false);
					if(jsonResult != null) {
						redirectInfo.setAppendInfo("&cube=" + jsonResult.getString("ID"));
					}
				} 
				//update step - navigate to expeirmentFor and open the step cube
				else {
					sql = "select distinct t.EXPERIMENT_ID,t.STEP_ID \r\n" + "from FG_S_STEP_V t\r\n"
							+ " where t.FORMID = '" + formId + "'";
					Map<String, String> map = generalDao.getMapsBySqlSingleRow(sql);
					if(map != null) {
						String expId = map.get("EXPERIMENT_ID");
						String stepId = map.get("STEP_ID");
						if (expId != null && !expId.isEmpty()) {
							redirectInfo.setFormCode("ExperimentFor");
							redirectInfo.setFormId(expId);
							redirectInfo.setTab("Overview");
							redirectInfo.setNew(false);
							if (stepId != null) {
								redirectInfo.setAppendInfo("&cube=" + stepId);
							}
						}
					}
				}
			}
			//************** Action (under formulation) **************
			else if(formCode.equals("Action")) {
				if (isNew != null && isNew) {
					// if new we only use the following sql to open the relevant step
					sql = "select distinct t.EXPERIMENT_ID,t.STEP_ID, NVL(lower(p.ProtocolTypeName),'NA') as PROTOCOLTYPE \r\n" + "from FG_S_STEP_ALL_V t, \r\n"
							+ "fg_s_protocoltype_v p \r\n" + "where t.PROTOCOLTYPE_ID = p.PROTOCOLTYPE_ID(+) \r\n"
							+ "and lower(p.ProtocolTypeName(+)) = 'formulation'\r\n" + "and t.FORMID = '" + parentId + "'";
				} else {
					sql = "select distinct t.EXPERIMENT_ID,t.STEP_ID, NVL(lower(p.ProtocolTypeName),'NA') as PROTOCOLTYPE \r\n" + "from FG_S_ACTION_ALL_V t, \r\n"
							+ "fg_s_protocoltype_v p \r\n" + "where t.PROTOCOLTYPE_ID = p.PROTOCOLTYPE_ID(+) \r\n"
							+ "and lower(p.ProtocolTypeName(+)) = 'formulation'\r\n" + "and t.FORMID = '" + formId + "'";
				}
				
				Map<String, String> map = generalDao.getMapsBySqlSingleRow(sql);
				if(map != null && generalUtil.getNull(map.get("PROTOCOLTYPE")).equals("formulation")) {
					String expId = map.get("EXPERIMENT_ID");
					if (expId != null && !expId.isEmpty()) {
						redirectInfo.setFormCode("ExperimentFor");
						redirectInfo.setFormId(expId);
						redirectInfo.setTab("Overview");
						redirectInfo.setNew(false);
						String stepId = map.get("STEP_ID");
						if (stepId != null) {
							redirectInfo.setAppendInfo("&cube=" + stepId);
						}
					}
				}
			}
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.ERROR,
					"redirectRules error wuth formcode =" + formCode + ", formId=" + formId,
					ActivitylogType.GeneralError, formId, e, null);
		}
		return redirectInfo;
	}
	
	
}
