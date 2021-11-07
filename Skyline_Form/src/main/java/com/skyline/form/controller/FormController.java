package com.skyline.form.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.FormSaveTaskInfo;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.RedirectAttribute;
import com.skyline.form.bean.RedirectInfo;
import com.skyline.form.service.FormSaveService;
import com.skyline.form.service.FormService;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilFormState;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.form.service.GeneralUtilNotificationEvent;
import com.skyline.form.service.IntegrationSaveForm;


@Controller
@RequestMapping("/skylineForm")
public class FormController {

	private static final Logger logger = LoggerFactory.getLogger(FormController.class);

	@Autowired
	private FormService formService;
	  
	@Autowired
	private FormSaveService formSaveService;
	
	@Autowired
	private GeneralUtil generalUtil;
	
	@Autowired
	private GeneralUtilFormState generalUtilFormState;
	
	@Autowired
	private GeneralUtilNotificationEvent generalUtilNotificationEvent; 
	
	@Autowired
	protected GeneralUtilLogger generalUtilLogger;
	
	@Autowired
	protected IntegrationSaveForm integrationSaveForm;
	
	/**
	 * This function is the gate to a form. It replace the formId in real id from fg_sequance in struct forms.
	 * The basic URL parameters are being pass to initid.request using RedirectAttribute (a FlashAttribute). This way we can distinguish between real calls in the system and call made from copy url into a new tabs.
	 * Note: we set isNew with boolean value in this function only if we know that the user didn't refresh the screen. In case of refresh this value will be null and the evaluation will be made inside formService.initForm (the purpose is to avoid the SQL in formService.initForm to improve performance)  
	 * @param code:
	 *            form code
	 * @param id:
	 *            context id
	 * @param request:
	 *            list of optional parameters
	 * @return ModelAndView - screen form data
	 */
	@RequestMapping(value = "/init.request", method = { RequestMethod.GET, RequestMethod.POST }) //id = result id = 3358017//TODO validation + bug long time where there is no match
	public RedirectView doFormInit(@RequestParam("formCode") String formCode, @RequestParam("formId") String formId,
			@RequestParam("userId") String user, HttpServletRequest request, RedirectAttributes redir) {

		logger.info("doFormInit call: /code=" + formCode + ", formId=" + formId + ", user=" + user);
		@SuppressWarnings("unchecked")
		Map<String, String[]> requestMap = request.getParameterMap();
		  
		Boolean isNew = null;
//		boolean isCloneFlag = false;
		String originFormId = formId;
		String tabSelection = "";
		String parentId = "";
		
		if(formId.equals("-1")) {
			//get real formcode
			formCode = formService.getCorrectNewFormCode(formId, formCode, requestMap);
			
			//check and make used default data from defined form.
			String cloneFormId = "-1";
			try {
				if(requestMap.get("PARENT_ID") != null && requestMap.get("useDefaultData") != null) {
					parentId  = requestMap.get("PARENT_ID")[0];
					cloneFormId = formSaveService.doCloneBySaveDefaultData(formCode, parentId); //TODO CLONE FOR USE AS DEFAULT DATA!!!
				}
				//isClone
			} catch (Exception e) {
				generalUtilLogger.logWrite(e);
			}
			
			if(generalUtil.getNull(cloneFormId).equals("-1") || generalUtil.getNull(cloneFormId).equals("")) { //generalUtil.getNull(cloneFormId).equals("") TO BE ON SAFE SIDE
				if(requestMap.get("PARENT_ID") != null) {
					parentId = generalUtil.getNull(requestMap.get("PARENT_ID")[0]);
				}
				formId = formSaveService.getNextFormId(formCode, parentId);
				if(!formId.equals("-1")) {
					isNew = true;
				}
			} else {
				formId = cloneFormId;
//				isNew = false;
//				isCloneFlag = true;
			}
		}
		else {
			if(!requestMap.containsKey("refreshFlag") || !requestMap.get("refreshFlag")[0].equals("1")) { // on refresh we will eval new in init form state not here
				 isNew = false;
			 }
			formCode = formService.getCorrectFormCode(formId, formCode);
		}
		
		RedirectInfo redirectInfo = formService.redirectRules(formId, formCode, parentId, isNew);
		formCode = redirectInfo.getFormCode();
		formId = redirectInfo.getFormId();
		originFormId = formId; // for the navigation
		isNew = redirectInfo.isNew(); // it can be change by the redirectRules from new to form
		String tab = redirectInfo.getTab();
		if(tab != null && !tab.isEmpty()) {
			tabSelection = "&formTab=" + tab + redirectInfo.getAppendInfo();
		}
		
		String stateKey = "-1";
		String urlCallParam = "";
		String urlPrintParam = "";
		StringBuilder sbUrl = new StringBuilder();
//		if(isCloneFlag) {
//			sbUrl.append("&isClone=1");
//		}
		
		for (Map.Entry<String, String[]> entry : requestMap.entrySet()) {
			String key = entry.getKey();
			String valArray[] = entry.getValue(); 
			if(!key.equals("formId") && !key.equals("formCode") && !key.equals("userId")) {
				try {
					if(key.equals("stateKey")) {
						stateKey = URLEncoder.encode(valArray[0], "UTF-8");
						sbUrl.append("&" + key + "=" + stateKey);
					} else if(key.equals("urlCallParam")) {
						urlCallParam = valArray[0];
					} else if(key.equals("urlPrintParam")) {
						urlPrintParam = valArray[0];
					} else if(key.equals("REPORT_NAME")) {
						sbUrl.append("&" + key + "=" + URLEncoder.encode(valArray[0], "UTF-8"));
					} else if(key.equals("useDefaultData")) { //Note!!: we change useDefaultData to isClone to use the clone mechanism (used in inventory table in previous versions) that remove from the DB the record on load (the clone records has -<formid> in the active column)
						sbUrl.append("&isClone=" + valArray[0]);
					} else if(key.equals("formTab")) {
						if(tabSelection == null || tabSelection.isEmpty()) {
							sbUrl.append("&" + key + "=" + valArray[0]);
						}
					}
//					else if(key.equals("refreshFlag")) {
//						// refreshFlag stay for breadcrumb to eval isNew
//					}
					else {
						sbUrl.append("&" + key + "=" + valArray[0]);//URLEncoder.encode(valArray[0], "UTF-8")
					}
				} catch (Exception e) {
					sbUrl.append("&" + key + "=" + "");
				}
			}
		}
		
		redir.addFlashAttribute("stateKeyFlash", new RedirectAttribute(stateKey,"", urlCallParam, urlPrintParam, isNew));
		
		String url = "initid.request?formCode=" + formCode + "&formId=" + formId + "&userId=" + user + tabSelection + sbUrl.toString();
		String urlNavigation_= "initid.request?formCode=" + formCode + "&formId=" + originFormId + "&userId=" + user + sbUrl.toString();
		if(!requestMap.containsKey("avoidBreadCrumb") || !requestMap.get("avoidBreadCrumb")[0].equals("1")) {
			generalUtilFormState.pushIntoBackNavigationStack(generalUtil.getNullLong(stateKey), originFormId, formCode, "", "", urlNavigation_.replace("initid.request", "init.request"),user + sbUrl.toString());
		}
		RedirectView rd = new RedirectView();
		rd.setUrl(url);
 
		return rd;
	     
	}
	
	@RequestMapping(value = "/initid.request", method = { RequestMethod.GET, RequestMethod.POST }) //id = result id = 3358017//TODO validation + bug long time where there is no match
	public ModelAndView doFormInitId(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redir) throws IOException {

		logger.info("doFormInit call: /code=");
		@SuppressWarnings("unchecked")
		Map<String, String[]> requestMap = request.getParameterMap(); 
		String nameId = generalUtil.getNull(request.getParameter("nameId"));
		
		String stateKey = "-1";
		String urlCallParam = "";
		String urlPrintParam = "";
		Boolean isNew = null;
		try {
//			if(request.) {
//				stateKey = generalUtil.getNullLong((String)request.getParameter("stateKey"));
//			}
			Map<String, ?> inputFlashMap = RequestContextUtils.getInputFlashMap(request);
			  if (inputFlashMap != null) {
				  RedirectAttribute redirectAttribute =  (RedirectAttribute)inputFlashMap.get("stateKeyFlash");
				  stateKey = redirectAttribute.getStateKey();
				  urlCallParam = generalUtil.getNull(redirectAttribute.getUrlCallParam());
				  urlPrintParam = generalUtil.getNull(redirectAttribute.getUrlPrintParam());
				  isNew = redirectAttribute.isNew();
			    // do the job
			  }
		} catch (Exception e1) {
			// TODO Auto-generated catch block
		}
		
		if(stateKey == null || stateKey.equals("-1")) {
			// handel copied URL .... 
			String formcode = requestMap.get("formCode")[0];
			String formId = requestMap.get("formId")[0];
			String userId = requestMap.get("userId")[0];
			stateKey = String.valueOf(generalUtil.generateStateKey(userId));
			if(formcode != null && formId != null && userId != null) {
				
				String url = getFullURL(request).replace("initid.request", "init.request");
				if(requestMap.get("stateKey") != null) {
					url = url.replace(requestMap.get("stateKey")[0],stateKey);
				} else if(!url.contains("stateKey")) {
					url += "&stateKey=" + stateKey;
				}
				
				if(requestMap.get("refreshFlag") == null && !url.contains("refreshFlag")) {
					url += "&refreshFlag=1";
				}

				//String url = "init.request?stateKey=" + stateKey + "&formCode=" + formcode + "&formId=" + formId + "&userId=" + userId + "&refreshFlag=1";
				response.sendRedirect(url);
			} else {
				response.sendRedirect(request.getContextPath() + "/?PERMISSION_DENIED_TAB=1");
			}
			return null;
//			response.sendRedirect(request.getContextPath() + "/?PERMISSION_DENIED_TAB=1");
//			return null;
		}
		
		ModelAndView mv = formService.initForm(isNew, generalUtil.getNullLong(stateKey), requestMap.get("formCode")[0], requestMap.get("formId")[0], requestMap.get("userId")[0], nameId, urlCallParam, urlPrintParam, requestMap);
		if(mv.getViewName().startsWith("Login")) {
			if(mv.getModel()!=null && mv.getModel().containsKey("DBError")){
				try { // violation
					String ns = generalUtilFormState.popFromBackNavigationStack(generalUtil.getNullLong(stateKey), request, requestMap.get("formCode")[0]);
					response.sendRedirect((new RedirectView(ns)).getUrl() + "&DBError=1");
				} catch (Exception e) {
					response.sendRedirect(request.getContextPath() + "/?DBError=1");
				}
			} else {
				try { // violation
					String ns = generalUtilFormState.popFromBackNavigationStack(generalUtil.getNullLong(stateKey), request, requestMap.get("formCode")[0]);
					response.sendRedirect((new RedirectView(ns)).getUrl() + "&PERMISSION_DENIED=1");
				} catch (Exception e) {
					response.sendRedirect(request.getContextPath() + "/?PERMISSION_DENIED=1");
				}
			}
			return null;
		} 
		return mv;
	}
	
	private String getFullURL(HttpServletRequest request) {
	    StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
	    String queryString = request.getQueryString();

	    if (queryString == null) {
	        return requestURL.toString();
	    } else {
	        return requestURL.append('?').append(queryString).toString();
	    }
	}
	
	@RequestMapping(value = "/restore.request", method = { RequestMethod.GET, RequestMethod.POST })
	public RedirectView doFormRestoreInit(@RequestParam("stateKey") String stateKey, @RequestParam("userId") String user, 
											HttpServletRequest request, RedirectAttributes redir) {
		logger.info("doFormRestoreInit call: /user=" + user);
				
		String url = "";
		try 
		{
			request.getSession().setAttribute("SHOW_RESTORE_BREADCRUMB_BUTTON", "0");
			url = generalUtilFormState.restoreURLFromLastSavedBreadcrumb(user, generalUtil.getNullLong(stateKey));
		} 
		catch (Exception e) 
		{
			url = String.valueOf("../" + request.getSession().getAttribute("homePage"));
			e.printStackTrace();			
		}
		
		RedirectView rd = new RedirectView();
		rd.setUrl(url);
 
		return rd;
	     
	}

	/**
	 * 
	 * @param request:
	 *            actionBean
	 * @return response: actionBean
	 */
	@RequestMapping(value = "/action.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean doFormAction(@RequestBody ActionBean actionBean, HttpServletRequest request) {

		logger.info("doFormAction call: /actionBean=" + actionBean);
		
		String action = actionBean.getAction();
		String formId = request.getParameter("formId");
		String formCode = request.getParameter("formCode");
		String userId = request.getParameter("userId");
		
		
		List<DataBean> dataBeanList = actionBean.getData();
		long stateKey = generalUtil.getNullLong(request.getParameter("stateKey")); //TODO key CHECK
		return formService.getAction(stateKey, action, dataBeanList, formCode, formId, userId);
	}

	@RequestMapping(value = "/doSave.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean doSave(@RequestBody ActionBean actionBean, HttpServletRequest request) {

 		logger.info("doSave call: /actionBean=" + actionBean);
		
		String formId = request.getParameter("formId");
		String formCode = request.getParameter("formCode");
		String userId = request.getParameter("userId");
		String saveAction = request.getParameter("saveAction");
		String isNew = generalUtil.getNull(request.getParameter("isNew"));
		String saveName = generalUtil.getNull(request.getParameter("saveName"));
		String useLoginsessionidScopeFlag = generalUtil.getNull(request.getParameter("useLoginsessionidScopeFlag"));
		String description = generalUtil.getNull(request.getParameter("description"));
		String formPathInfo = generalUtil.getNull(request.getParameter("formPathInfo"));
		long stateKey = generalUtil.getNullLong(request.getParameter("stateKey"));
		String lastChangeUserId = generalUtil.getNull(request.getParameter("lastChangeUserId"));
		String lastChangeDate = generalUtil.getNull(request.getParameter("lastChangeDate"));
		FormSaveTaskInfo formSaveTaskInfo = new FormSaveTaskInfo();
		boolean errorFlag = false;
		
		 
		ActionBean doSaveResult = null;
		List<DataBean> dataBeanList = actionBean.getData();
		if(formCode.equalsIgnoreCase("User")){		
			String beanInfo;
			for (DataBean dataBean : dataBeanList) {				
				beanInfo = dataBean.getInfo();
				if(generalUtil.getNull(generalUtil.getJsonValById(beanInfo, "type")).equalsIgnoreCase("password")) {
					if(dataBean.getVal().equals("")){
						dataBeanList.remove(dataBean);
						break;
					}
					else{
						String md5 = generalUtil.getMd5(dataBean.getVal());
						dataBean.setVal(md5);
						break;
					}					
				}
			} 
		}

		try {
			doSaveResult = formSaveService.doSave(stateKey, dataBeanList, formCode, formId, userId, isNew, saveAction, saveName, useLoginsessionidScopeFlag, description, formPathInfo, formSaveTaskInfo, lastChangeUserId, lastChangeDate);
		} catch (Exception e) {
			errorFlag = true;
			doSaveResult = integrationSaveForm.doSaveOnException(e, formId, formCode);
		}
		
		if(!errorFlag) {
			formSaveService.doSaveTask(formSaveTaskInfo);
		}
		 
		return doSaveResult;
	}
	
	@RequestMapping(value = "/getSqlByCatalogDBBean.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getSql(@RequestBody ActionBean actionBean) {

		logger.info("getSql call: /actionBean=" + actionBean);

		return formService.getSqlByCatalogDBBean(actionBean);
	}
	
	/*@RequestMapping(value = "/saveFile.request", method = RequestMethod.POST)
	public String saveFile(@RequestParam("uploadBtn") MultipartFile file,@RequestParam("fileFormId") String FORM_ID, @RequestParam("formCode") String formCode, 
							@RequestParam("domID") String domID) 
	{
		
		logger.info("getSql call: /saveFile=" + file.getName() + " FORM_ID = " + FORM_ID + " formCode = " + formCode + " DOM_ID = " + domID);
		return formSaveService.saveFile(file, FORM_ID, formCode, domID);
	}*/
	
	@RequestMapping(value = "/saveFile.request", method = RequestMethod.POST)
	public  void saveFile(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
	{		
		logger.info("getSql call: /saveFile.request");
		try 
		{
			String formCodeFull = multipartRequest.getParameter("formCodeFull");		
			Iterator<String> itrator = multipartRequest.getFileNames();
	        MultipartFile multiFile = multipartRequest.getFile(itrator.next());              
        	
			response.getWriter().write(formSaveService.saveFile(multiFile, formCodeFull, "-1")); //TODO YARON
		} 
			catch (IOException e) 
		{
				try {
					response.getWriter().write("-1");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
        return;
	}
	
	@RequestMapping(value = "/saveFileAsClob.request", method = RequestMethod.POST)
	public String saveFileAsClob(@RequestParam("uploadBtn") MultipartFile file,@RequestParam("FILE_ID") String FORM_ID) {
		
		logger.info("getSql call: /saveFileAsClob=" + file.getName());
		
		return formSaveService.saveFileAsClob(file,FORM_ID);
	}
	
	@RequestMapping(value = "/getFileContent.request", method = RequestMethod.POST)
	public void getFileContent(MultipartHttpServletRequest multipartRequest, HttpServletResponse response)
	{		
		logger.info("getSql call: /getFileContent.request");
		String formCodeFull = multipartRequest.getParameter("formCodeFull");		
		Iterator<String> itrator = multipartRequest.getFileNames();
		MultipartFile multiFile = multipartRequest.getFile(itrator.next());
		String elementId = multipartRequest.getParameter("elementId");
		
		try {
			response.getWriter().write(formService.getFileContent(multiFile, formCodeFull,elementId));
		} 
		catch (IOException e) 
		{
			try {
				response.getWriter().write("-1");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
	}
    return;
	}
	
	@RequestMapping(value = "/doSplit.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean doSplit(@RequestBody ActionBean actionBean) {
		logger.info("doSplit call: /actionBean=" + actionBean);	
		String formId = actionBean.getData().get(0).getVal();
		String currentQuantity = actionBean.getData().get(1).getVal();
		String splitQuantity = actionBean.getData().get(2).getVal();
		String splitQuantityUom = actionBean.getData().get(3).getVal();
		String toReturn = "";
		try {
			toReturn = formSaveService.doSplit(formId, currentQuantity,splitQuantity,splitQuantityUom);		
		} catch (Exception e) {
			if(!generalUtil.getNull(e.getMessage()).contains("check constraint")){
				generalUtilLogger.logWrite(LevelType.ERROR, "Error in Split event of formId",formId, ActivitylogType.SaveException,null,e);
				toReturn = "-1";
			} else{
				String uniqueConstraint = e.getMessage().substring(e.getMessage().lastIndexOf(".") + 1,
						e.getMessage().lastIndexOf(")"));
				toReturn = "-3," + uniqueConstraint;
				generalUtilLogger.logWrite(LevelType.ERROR,  generalUtil.getSpringMessagesByKey(uniqueConstraint,uniqueConstraint),formId, ActivitylogType.SaveException,null,e);
			}
			e.printStackTrace();
			
		}
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}
	
	@RequestMapping(value = "/doClone.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean doClone(@RequestBody ActionBean actionBean) {
		logger.info("doClone call: /actionBean=" + actionBean);		
		String formId = actionBean.getData().get(0).getVal();
		String toReturn = "";		
		try {
			toReturn = formSaveService.doClone(formId);	
		} catch (Exception e) {
			if(!generalUtil.getNull(e.getMessage()).contains("check constraint")){
				generalUtilLogger.logWrite(LevelType.ERROR, "Error in Clone event of formId",formId, ActivitylogType.SaveException, null,e);
				toReturn = "-1";
			} else {
				String uniqueConstraint = e.getMessage().substring(e.getMessage().lastIndexOf(".") + 1,
						e.getMessage().lastIndexOf(")"));
				toReturn = "-3," + uniqueConstraint;
				generalUtilLogger.logWrite(LevelType.ERROR,  generalUtil.getSpringMessagesByKey(uniqueConstraint,uniqueConstraint),formId, ActivitylogType.SaveException,null,e);
			}
			e.printStackTrace();
			
		}
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}
	
	@RequestMapping(value = "/doMultiClone.request", method = { RequestMethod.GET, RequestMethod.POST })
	@Transactional
	public @ResponseBody ActionBean doMultiClone(@RequestBody ActionBean actionBean) {
		logger.info("doMultiClone call: /actionBean=" + actionBean);
		String formId = actionBean.getData().get(0).getVal();
		String cloneQuantity = actionBean.getData().get(1).getVal();
		String toReturn = "";
		try {
			toReturn = formSaveService.doMultiClone(formId,cloneQuantity);
		} catch (Exception e) {
			if(!generalUtil.getNull(e.getMessage()).contains("check constraint")){
				generalUtilLogger.logWrite(LevelType.ERROR, "Error in MultiClone event of formId",formId, ActivitylogType.SaveException, null,e);
				toReturn = "-1";
			} else {
				String uniqueConstraint = e.getMessage().substring(e.getMessage().lastIndexOf(".") + 1,
						e.getMessage().lastIndexOf(")"));
				toReturn = "-3," + uniqueConstraint;
				generalUtilLogger.logWrite(LevelType.ERROR,  generalUtil.getSpringMessagesByKey(uniqueConstraint,uniqueConstraint),formId, ActivitylogType.SaveException,null,e);
			}
			e.printStackTrace();
		}
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}
	
	@RequestMapping(value = "/doBack.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody RedirectView doBack(@RequestParam("formCode_doBack") String formCode_doBack,
			@RequestParam("formCode_request") String formCode_request,
			@RequestParam("stateKey_request") String stateKey_request, HttpServletRequest request) {
		logger.info("doBack call");
		long stateKey = generalUtil.getNullLong(stateKey_request); 
		formService.cleanForm(stateKey, formCode_request); //TODO key check clean
		if(formCode_doBack.isEmpty()){
			formCode_doBack = formCode_request;//ta 03102019 - Allows to navigate from a form marked as "ignore navigation" to the previous form(by clicking on close button)
		}

		String urlPrintParam = request.getParameter("urlPrintParam");
		String urlPrintParamEnc = null;
		if(urlPrintParam != null) {
			try {
				urlPrintParamEnc = URLEncoder.encode(urlPrintParam, "UTF-8");
			} catch (Exception e) {
				// do nothing
			}
		}
		String url = generalUtilFormState.popFromBackNavigationStack(generalUtil.getNullLong(request.getParameter("stateKey_request")), request, formCode_doBack,formCode_request);
		return new RedirectView(url + ((urlPrintParam!=null)?"&urlPrintParam=" + urlPrintParamEnc:""));
	}
	
	@RequestMapping(value = "/getPrecision.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getPrecision(@RequestBody ActionBean actionBean) {
		logger.info("getPrecision call: /actionBean=" + actionBean);		
		return new ActionBean("no action needed", generalUtil.StringToList(generalUtil.getPrecision()), "");
	} 
	
	@RequestMapping(value = "/isSessionAlive.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean isSessionAlive() 
	{
		logger.info("isSessionAlive() called");		
		return new ActionBean("no action needed", generalUtil.StringToList("1"), "");
	}
	
	@RequestMapping(value = "/generateFromId.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean generateFromId(@RequestParam("formCode") String formCode) 
	{
		logger.info("getPrecision call: /formCode=" + formCode);
		String toReturn =  formSaveService.generateFromId(formCode);
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}
	
	@RequestMapping(value = "/getMessageCount.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getMessageCount(@RequestParam("userId") String userId) 
	{
		logger.info("getMessageCount call: /userId=" + userId);
		String toReturn =  generalUtilNotificationEvent.getMessageCount(userId, false);
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}
	
//	@RequestMapping(value = "/getTitleAndSubtitleForm.request", method = { RequestMethod.GET, RequestMethod.POST })
//	public @ResponseBody ActionBean getTitleAndSubtitleForm(@RequestBody ActionBean actionBean) {
//
//		logger.info("getTitleAndSubtitleForm call: /actionBean=" + actionBean);
//		return formService.getTitleAndSubtitleForm(actionBean);
//	}
	
	@RequestMapping(value = "/getFormType.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getFormType(@RequestBody ActionBean actionBean) {

		logger.info("getFormType call: /actionBean=" + actionBean);
		String formCode = actionBean.getData().get(0).getVal();
		return new ActionBean("no action needed", generalUtil.StringToList(formService.getFormType(formCode)), "");
	}
	
	
	@RequestMapping(value = "/getPropByName.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getPropByName(@RequestBody ActionBean actionBean) {
		logger.info("getPropByName call: /actionBean=" + actionBean);	
		String porpName = actionBean.getData().get(0).getVal();
		String defaultValue = actionBean.getData().get(1).getVal();
		String toReturn = generalUtil.getPropByName(porpName,defaultValue);
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}
	
	@RequestMapping(value = "/setParameterMap.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean setParameterMap(@RequestBody ActionBean actionBean,  HttpServletRequest request) {
		logger.info("setParameterMap call: /actionBean=" + actionBean);	
		String paramCode = actionBean.getData().get(0).getCode();
		String paramVal = actionBean.getData().get(0).getVal();
		String formCode =  request.getParameter("formCode");
		long stateKey = generalUtil.getNullLong(request.getParameter("stateKey"));
		generalUtilFormState.setFormParam(stateKey, formCode, paramCode, paramVal);
		return new ActionBean("no action needed", generalUtil.StringToList("1"), "");
	}
	
	@RequestMapping(value = "/updateWFState.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean updateWFState(@RequestBody ActionBean actionBean, HttpServletRequest request) {

		logger.info("updateWFState call: /actionBean=" + actionBean);

		String formId = request.getParameter("formId");
		String formCode = request.getParameter("formCode");
		String userId = request.getParameter("userId");
		String isNew = generalUtil.getNull(request.getParameter("isNew"));

		ActionBean doUpdateWFState = null;
		List<DataBean> dataBeanList = actionBean.getData();

		long stateKey = generalUtil.getNullLong(request.getParameter("stateKey")); //TODO key check
		doUpdateWFState = formService.updateWFState(stateKey, dataBeanList, formCode, formId, userId, isNew);

		return doUpdateWFState;
	}
	
}
