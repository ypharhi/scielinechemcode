package com.skyline.form.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.Form;
import com.skyline.form.bean.FormEntity;
import com.skyline.form.dal.FormBuilderDao;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.FormResourceLookupDao;
import com.skyline.form.entity.Entity;
import com.skyline.form.entity.EntityFactory;

@Service
public class FormBuilderService {

	@Autowired
	private FormBuilderDao formBuilderDao;

	@Autowired
	private FormDao formDao;

	@Autowired
	private GeneralUtil generalUtil;
	
	@Autowired
	private CacheService cacheService;

	@Autowired
	private EntityFactory entityFactory;

	@Value("${HtmlPath}")
	private String Htmlpath;

	@Value("${jspPath}")
	private String jspPath;
	
	@Value("${logPath}")
	private String logPath;

	@Autowired
	private GeneralUtilForm generalUtilForm;
	
	@Autowired
	public GeneralUtilLogger generalUtilLogger;

	//	@Autowired
	//	FormState formState;

	@Autowired
	private FormResourceLookupDao formResourceLookupDao;

	public ModelAndView demoFormBuilderMainInit(HttpServletRequest request) {
		ModelAndView mv =  new ModelAndView("demoFormBuilderMain");
		mv.addObject("stateKey", request.getParameter("stateKey"));
		return mv;
	}

	public ModelAndView demoFormBuilderInit(HttpServletRequest request) {
		return new ModelAndView("demoFormBuilder");
	}

	private static final Logger logger = LoggerFactory.getLogger(FormBuilderService.class);

	//ajax call
	public ActionBean newFormEntity(ActionBean requestAction) {
		if (requestAction.getData().get(1).getVal().equals("")) {
			return new ActionBean("no action needed", generalUtil.StringToList("-1"), "Num of order cannot be null");
		}
		String formCode = requestAction.getData().get(0).getVal();
		FormEntity formentity = new FormEntity(formCode,
				Integer.valueOf(requestAction.getData().get(1).getVal()), requestAction.getData().get(2).getVal(),
				requestAction.getData().get(3).getVal(), requestAction.getData().get(4).getVal(),
				requestAction.getData().get(5).getVal());
		String insert = formBuilderDao.newFormEntity(formentity);

		//		Entity entity = entityFactory.getEntity(formentity.getEntityImpClass());
		//		if (entity.getActive()) {
		//			entity.init(formentity.getFormCode(), formentity.getEntityImpCode(), formentity.getEntityImpInit());
		//		}
		if(!insert.equals("-1")) {
			cacheService.setCacheOnFormBuilderChange(formCode);
		}

		return new ActionBean("no action needed", generalUtil.StringToList(insert), "");
	}

	//ajax call
	public ActionBean deleteFormEntity(ActionBean requestAction) {
		String formCode =  requestAction.getData().get(0).getVal();
		String entityImpCode = requestAction.getData().get(1).getVal();
		String delete = formBuilderDao.deleteFormEntity(formCode, entityImpCode);
		 
		if(!delete.equals("-1")) {
			cacheService.setCacheOnFormBuilderChange(formCode);
		}
			 
		return new ActionBean("no action needed", generalUtil.StringToList(delete), "");
	}

	public ActionBean getFormEntityListByFormCodeAndType(ActionBean requestAction) {
		List<FormEntity> formEntityList = formBuilderDao.getFormEntityByView(
				requestAction.getData().get(0).getCode(), requestAction.getData().get(0).getVal());
		List<String> StringList = new ArrayList<String>();

		try {
			for (FormEntity formEntity : formEntityList) {
				StringBuilder str = new StringBuilder();
				str.append(formEntity.getEntityImpCode() + ";");
				//				str.append(formEntity.getOrder() + ";");
				Class<?> cls = Class.forName("com.skyline.form.entitypool." + formEntity.getEntityImpClass());
				Entity entity = entityFactory.getEntity(cls.getSimpleName());
				str.append(entity.getImpName() + ";");
				str.append(formEntity.getEntityImpInit());
				StringList.add(str.toString());
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		JSONArray jsonList = new JSONArray(StringList);
		return new ActionBean("no action needed", generalUtil.StringToList(jsonList.toString()), "");
	}

	//ajax call
	public ActionBean newForm(ActionBean requestAction) {
		String formCode = requestAction.getData().get(0).getVal();
		String description = requestAction.getData().get(1).getVal();
		String form_type = requestAction.getData().get(2).getVal();
		String title = requestAction.getData().get(3).getVal();
		String subtitle = requestAction.getData().get(4).getVal();
		String useAsTemplate = requestAction.getData().get(5).getVal();
		String active = requestAction.getData().get(6).getVal();
		String order = requestAction.getData().get(7).getVal();
		String groupName = requestAction.getData().get(8).getVal();
		String formCodeEntity = requestAction.getData().get(9).getVal();
		String ignoreNav = requestAction.getData().get(10).getVal();
		String useCache = requestAction.getData().get(11).getVal();
		String insert = formBuilderDao.newForm(formCode, description, active, form_type, title, subtitle, useAsTemplate,
				order, groupName, formCodeEntity, ignoreNav, useCache);
		if(!insert.equals("-1")) {
			cacheService.setCacheOnFormBuilderChange(formCode);
		}
		return new ActionBean("no action needed", generalUtil.StringToList(insert), "");
	}

	//ajax call
	public ActionBean getForms(ActionBean requestAction) {
		//List<Form> formList = formDao.getForms("select * from FG_FORM");
		List<Form> formList = formBuilderDao.getForm("%", "%", true);

		List<String> StringList = new ArrayList<String>();
		try {
			for (Form form : formList) {
				StringBuilder str = new StringBuilder();
				str.append(generalUtil.getNull(form.getFormCode()) + ";");
				str.append(generalUtil.getNull(form.getTitle()) + ";");
				str.append(generalUtil.getNull(form.getSubtitle()) + ";");
				str.append(generalUtil.getNull(form.getDescription()) + ";");
				if ((generalUtil.getNull(form.getActive()).equals("1"))) {
					str.append("Yes;");
				} else {
					str.append("No;");
				}
				str.append(generalUtil.getNull(form.getFormType()) + ";");
				if ((generalUtil.getNull(form.getUseAsTemplate()).equals("1"))) {
					str.append("Yes");
				} else {
					str.append("No");
				}
				StringList.add(str.toString());
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
			StringList = null;
		}
		JSONArray jsonList = new JSONArray(StringList);
		return new ActionBean("no action needed", generalUtil.StringToList(jsonList.toString()), "");
	}

	//ajax call
	public ActionBean getFormTypeValues(ActionBean requestAction) {
		return new ActionBean("no action needed", generalUtil.StringToList(generalUtilForm.getFormTypeValues()), "");
	}

	public String getTableByCatalogDBBean(long stateKey, String formCode, String catalogBean) {

		String sql = "";
		//		String sql = generalUtil.getReflactionString(formCode, "$C{" + catalogBean + ".getTableName}");
//		String sql = generalUtilFormState.getFormCatalogDBTable(stateKey, formCode, catalogBean);
		if(!generalUtil.getNull(catalogBean).equals("")) {
			String initStr = formBuilderDao.getFormEntityInit(formCode, catalogBean, "%"); // TODO check
			sql = generalUtil.getJsonValById(initStr, "tableName");
		}
		return sql;
	}

	//ajax call
	public ActionBean getFormEntityInit(ActionBean requestAction) {
		String formCode = requestAction.getData().get(0).getVal();
		String entityCode = requestAction.getData().get(2).getVal();
		String type = requestAction.getData().get(1).getVal();
		String toReturn = formBuilderDao.getFormEntityInit(formCode, entityCode, type);
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}

	//ajax call
	public ActionBean getFormInformation(ActionBean requestAction) {
		//String sql = "select * from FG_FORM where FORMCODE='" + requestAction.getData().get(0).getVal() + "'";
		//List<Form> formList = formDao.getForms(sql);	
		List<Form> formList = formBuilderDao.getForm(requestAction.getData().get(0).getVal(), "%", true);
		if (formList.isEmpty()) {
			return new ActionBean("no action needed", generalUtil.StringToList(""), "");
		}
		Form form = formList.get(0);
		StringBuilder str = new StringBuilder();
		str.append(generalUtil.getNull(form.getFormCode()) + ";");
		str.append(generalUtil.getNull(form.getTitle()) + ";");
		str.append(generalUtil.getNull(form.getSubtitle()) + ";");
		str.append(generalUtil.getNull(form.getDescription()) + ";");
		if ((generalUtil.getNull(form.getActive()).equals("1"))) {
			str.append("Yes;");
		} else {
			str.append("No;");
		}
		str.append(generalUtil.getNull(form.getFormType()) + ";");
		if ((generalUtil.getNull(form.getUseAsTemplate()).equals("1"))) {
			str.append("Yes;");
		} else {
			str.append("No;");
		}
		str.append(generalUtil.getNull(String.valueOf(form.getNumerOfOrder())) + ";");
		str.append(generalUtil.getNull(form.getGroupName()) + ";");
		str.append(generalUtil.getNull(form.getFormCodeEntity()) + ";");
		if ((generalUtil.getNull(form.getIgnoreNav()).equals("1"))) {
			str.append("Yes;");
		} else {
			str.append("No;");
		}
		
		if ((generalUtil.getNull(form.getUseCache()).equals("1"))) {
			str.append("Yes");
		} else {
			str.append("No");
		}
		return new ActionBean("no action needed", generalUtil.StringToList(str.toString()), "");
	}

	//ajax call
	public ActionBean createLike(ActionBean requestAction) {
		String newFormCode = requestAction.getData().get(0).getVal();
		String oldFormCode = requestAction.getData().get(1).getVal();
		String toReturn = formBuilderDao.createLike(newFormCode, oldFormCode);
		if(!toReturn.equals("-1")) {
			cacheService.setCacheOnFormBuilderChange(newFormCode);
		}
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}

	//ajax call
	public ActionBean updateCatalogForNewForm(ActionBean requestAction) {
		String formCode = requestAction.getData().get(0).getVal();
		String formType = requestAction.getData().get(1).getVal();
		String toReturn = formBuilderDao.updateCatalogForNewForm(formCode, formType);
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}

	//ajax call
	public ActionBean canDeleteFormEntity(ActionBean requestAction) {
		String formCode = requestAction.getData().get(0).getVal();
		String formEntity = requestAction.getData().get(1).getVal();
		String initCSV = formBuilderDao.canDeleteFormEntity(formCode, formEntity);
		String toReturn = "1";
		if (initCSV.matches("(.*):\"" + formEntity + "(\\..*)*\"(.*)")) {
			toReturn = "0";
		}
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}

	//ajax call
	public ActionBean getTableOfBookmarks(ActionBean requestAction) {
		//		System.out.println(formState);		 
		
		String formCode = requestAction.getData().get(0).getCode();
		String formEntity = requestAction.getData().get(0).getVal();
		String firstTime = requestAction.getData().get(1).getVal();
		if (firstTime.equals("true")) {
			formResourceLookupDao.resourceLookUp("", formCode);
		}
		//		System.out.println(generalUtil.getReflactionString(formCode, "$C{" + formEntity + ".getJspName" + "}"));		
		String content = "";
		//		String jspName = generalUtil.getReflactionString(formCode, "$C{" + formEntity + ".getJspName" + "}");
//		long emptyStateKey = 0l;
		String jspName = formEntity; //TODO check first time -> changed from generalUtilFormState
//		String jspName = generalUtilFormState.getLayoutJsp(emptyStateKey, formCode, formEntity);
		
		StringBuilder tabsSB = new StringBuilder();
		try {
			String layoutInit = formBuilderDao.getFormEntityInit(formCode, formEntity, "Layout");
			String tabInitConfig = generalUtil.getJsonValById(layoutInit, "tabsCSV");
			if (tabInitConfig != null && tabInitConfig.contains(",")) {
				String[] tabs = tabInitConfig.split(",");

				for (String tab : tabs) {
					String tabCodeAndSensitivity = tab.replaceAll(" ", "");
					if (!tabCodeAndSensitivity.contains(";")) {
						tabCodeAndSensitivity += ";0";
					}
					String tabCode = tabCodeAndSensitivity.split(";")[0];
					String sensitivityLevel = tabCodeAndSensitivity.split(";")[1];

					tabsSB.append("<li><a href=\"#" + tabCode + "Tab\" sensitivitylevel_order=\"" + sensitivityLevel
							+ "\" >"
							+ generalUtil.getSpringMessagesByKey(tabCode.replaceAll(" ", "_"),
									(generalUtil.getNull(tab).contains(";") ? tab.substring(0, tab.indexOf(";")) : tab))
							+ "</a></li>\n");
				}
			} 
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
		}
		
		StringBuffer sb = new StringBuffer();
		try {
			String path = jspPath + "/" + jspName;
			if (!jspName.contains(".jsp")) {
				path += ".jsp";
			}
			Scanner scanner = new Scanner(new File(path));
			content = scanner.useDelimiter("\\Z").next();
			scanner.close();
			content = content.substring(content.indexOf("<!--begin --><div"), content.indexOf("<!--end --></div>"));
			Pattern pattern = Pattern.compile("(\\$\\{)(.*?)(\\})");
			Matcher matcher = pattern.matcher(content);
			while (matcher.find()) {
				matcher.appendReplacement(sb, matcher.group(2));
			}
			matcher.appendTail(sb);
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		
		String toReturn = sb.toString();
		if(toReturn != null && toReturn.length() > 0 && tabsSB.length() > 0) {
			toReturn = "<div id=\"tempalteTabs\"><ul>" + tabsSB.toString() + "</ul>" + toReturn.replace("overflow-y: auto;", "") + "</div>";
		}

		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}

	//ajax call
	public ActionBean initDateFormatter(ActionBean requestAction) {
		String toReturn = generalUtil.getClientDatesFormat();
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}

	//ajax call
	public ActionBean getFormsId(ActionBean requestAction) {

		String formCode = requestAction.getData().get(0).getCode();
		String table = "FG_FORMLASTSAVEVALUE";
		if (generalUtilForm.isStructFromByFormCode(formCode)) {
			table = "FG_S_" + formCode.toUpperCase() + "_ALL_V";
		}
		String toReturn = "";
		try {
			toReturn = formDao.getFormsId(table);
		} catch (Exception e) {
			logger.warn(
					"do nothing. the table probebly not exists if its the first save (if we in FormSaveServiceDevelopImp develop save)");
		}
		if (toReturn.indexOf("-1") == -1) {
			toReturn = (toReturn).equals("") ? "-1" : "-1," + toReturn;
		}
		String[] formsIdArray = toReturn.split(",");
		Arrays.sort(formsIdArray);
		toReturn = generalUtil.replaceLast(Arrays.asList(formsIdArray).toString().replaceFirst("\\[", ""), "]", "")
				.replaceAll(" ", "");
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}

	public void viewConnectionLog(HttpServletResponse response) {
		try {
			response.setContentType("text/plain");
			response.setHeader("Content-Disposition", "attachment;filename=log.txt");
			InputStream is = new FileInputStream(logPath);
			IOUtils.copy(is, response.getOutputStream());
			response.flushBuffer();
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
	}
	
	public void executeOperation(ActionBean requestAction) throws Exception {
		
		String dropDownListValue = requestAction.getData().get(0).getVal();
		String formCode = requestAction.getData().get(1).getVal();
		String bookmarkPrefix = requestAction.getData().get(2).getVal();
		String noEntityimpcodeList_in = requestAction.getData().get(3).getVal();
		
	    if(dropDownListValue.equals("1")){
			formBuilderDao.createBookmarks(formCode, bookmarkPrefix, noEntityimpcodeList_in);
			cacheService.setCacheOnFormBuilderChange(formCode);
		}
	}
	
	

}
