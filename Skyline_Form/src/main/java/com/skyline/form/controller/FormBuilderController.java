package com.skyline.form.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.service.FormBuilderService;
import com.skyline.form.service.FormSchedTaskService;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilForm;

@Controller
@RequestMapping("/skylineForm")
public class FormBuilderController {

	private static final Logger logger = LoggerFactory.getLogger(FormBuilderController.class);

	@Autowired
	private FormBuilderService formBuilderService;

	@Autowired
	private GeneralUtil generalUtil;
	  
	@Autowired
	private GeneralUtilForm generalUtilForm;
	
	@Autowired
	private ApplicationContext appContext;

	@RequestMapping(value = "/demoFormBuilderMainInit.request", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView demoFormBuilderMainInit(HttpServletRequest request) { 
		logger.info("demoFormBuilderMainInit call: /request=" + request.toString());
		
		generalUtil.getUserId(request);
		generalUtil.canEdit(request);
		generalUtil.getUserName(request);
		
		return formBuilderService.demoFormBuilderMainInit(request);
	}

	@RequestMapping(value = "/demoFormBuilderInit.request", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView demoFormBuilderInit(@RequestParam("formCode") String formCode, HttpServletRequest request) {
		
		logger.info("demoFormBuilderInit call: /request=" + request.toString());
		
		int userId = generalUtil.getUserId(request);
		generalUtil.canEdit(request);
		generalUtil.getUserName(request);
//		formBuilderService.initFormState(formCode, String.valueOf(userId));
		return formBuilderService.demoFormBuilderInit(request);
	}

	@RequestMapping(value = "/getResourceValueByType.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getResourceValueByType(@RequestBody ActionBean actionBean) {

		logger.info("getResourceValueByType call: /actionBean=" + actionBean);
		return generalUtilForm.getResourceValueByType(actionBean);
	}

	@RequestMapping(value = "/getResourceCodeValueInfoByType.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getResourceCodeValueInfoByType(@RequestBody ActionBean actionBean) {

		logger.info("getResourceCodeValueInfoByType call: /actionBean=" + actionBean);
		return generalUtilForm.getResourceCodeValueInfoByType(actionBean);
	}

	@RequestMapping(value = "/newFormEntity.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean newFormEntity(@RequestBody ActionBean actionBean) {

		logger.info("newFormEntity call: /actionBean=" + actionBean);
		return formBuilderService.newFormEntity(actionBean);
	}

	@RequestMapping(value = "/deleteFormEntity.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean deleteFormEntity(@RequestBody ActionBean actionBean) {

		logger.info("deleteFormEntity call: /actionBean=" + actionBean);
		return formBuilderService.deleteFormEntity(actionBean);
	}

	@RequestMapping(value = "/getFormEntityListByFormCodeAndType.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getFormEntityListByFormCodeAndType(@RequestBody ActionBean actionBean) {

		logger.info("getFormEntityListByFormCodeAndType call: /actionBean=" + actionBean);
		return formBuilderService.getFormEntityListByFormCodeAndType(actionBean);
	}

	@RequestMapping(value = "/newForm.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean newForm(@RequestBody ActionBean actionBean) {
		logger.info("newForm call: /actionBean=" + actionBean);
		return formBuilderService.newForm(actionBean);
	}

	@RequestMapping(value = "/getForms.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getForms(@RequestBody ActionBean actionBean) {

		logger.info("getForms call: /actionBean=" + actionBean);
		return formBuilderService.getForms(actionBean);
	}

	@RequestMapping(value = "/getFormTypeValues.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getTypeValues(@RequestBody ActionBean actionBean) {

		logger.info("getTypeValues call: /actionBean=" + actionBean);
		return formBuilderService.getFormTypeValues(actionBean);
	}

	@RequestMapping(value = "/getFormEntityInit.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getFormEntityInit(@RequestBody ActionBean actionBean) {

		logger.info("getFormEntityInit call: /actionBean=" + actionBean);
		return formBuilderService.getFormEntityInit(actionBean);
	}

	@RequestMapping(value = "/getFormInformation.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getFormInformation(@RequestBody ActionBean actionBean) {

		logger.info("getFormInformation call: /actionBean=" + actionBean);
		return formBuilderService.getFormInformation(actionBean);
	}

	@RequestMapping(value = "/createLike.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean createLike(@RequestBody ActionBean actionBean) {

		logger.info("createLike call: /actionBean=" + actionBean);
		return formBuilderService.createLike(actionBean);
	}

	@RequestMapping(value = "/updateCatalogForNewForm.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean updateCatalogForNewForm(@RequestBody ActionBean actionBean) {

		logger.info("updateCatalogForNewForm call: /actionBean=" + actionBean);
		return formBuilderService.updateCatalogForNewForm(actionBean);
	}

	@RequestMapping(value = "/canDeleteFormEntity.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean canDeleteFormEntity(@RequestBody ActionBean actionBean) {

		logger.info("canDeleteFormEntity call: /actionBean=" + actionBean);
		return formBuilderService.canDeleteFormEntity(actionBean);
	}
	
	@RequestMapping(value = "/getTableOfBookmarks.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getTableOfBookmarks(@RequestBody ActionBean actionBean) {

		logger.info("getTableOfBookmarks call: /actionBean=" + actionBean);
		return formBuilderService.getTableOfBookmarks(actionBean);
	}	
	
	@RequestMapping(value = "/initDateFormatter.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean initDateFormatter(@RequestBody ActionBean actionBean) {

		logger.info("initDateFormatter call: /actionBean=" + actionBean);
		return formBuilderService.initDateFormatter(actionBean);
	}	
	
	@RequestMapping(value = "/getFormsId.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getFormsId(@RequestBody ActionBean actionBean) {

		logger.info("getFormsId call: /actionBean=" + actionBean);
		return formBuilderService.getFormsId(actionBean);
	}
	
	@RequestMapping(value = "/viewConnectionLog.request", method = RequestMethod.GET)
	public void viewConnectionLog(HttpServletResponse response) {
		
		logger.info("getFormsId call: /viewConnectionLog");
		formBuilderService.viewConnectionLog(response);
	}
	
	@RequestMapping(value = "/setCache.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean setCache(HttpServletResponse response, @RequestBody ActionBean actionBean) throws Exception {
		
		logger.info("getFormsId call: /setCache");
		
		FormSchedTaskService formSchedTaskService =
				(FormSchedTaskService) appContext.getBean("FormSchedTaskService");
		formSchedTaskService.setInfDataInCachMapRefresh(actionBean);
		
		return new ActionBean("no action needed", generalUtil.StringToList("OK"), "");
	}
	
	@RequestMapping(value = "/executeOperation.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean executeOperation(HttpServletResponse response, @RequestBody ActionBean actionBean) throws Exception {
		
		logger.info("getFormsId call: /executeOperation");
//		cacheService.setInfDataInCachMapRefresh(actionBean);
		formBuilderService.executeOperation(actionBean);
		
		return new ActionBean("no action needed", generalUtil.StringToList("OK"), "");
	}
	
	
}
