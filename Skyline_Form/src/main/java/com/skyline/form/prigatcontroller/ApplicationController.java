package com.skyline.form.prigatcontroller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.DataBean;
import com.skyline.form.prigatservice.ApplicationService;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.form.service.IntegrationSaveForm;


@Controller
@RequestMapping("/skylineForm")
public class ApplicationController {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationController.class);
 
	@Autowired
	private GeneralUtil generalUtil;
	 
	@Autowired
	protected GeneralUtilLogger generalUtilLogger;
	
	@Autowired
	protected IntegrationSaveForm integrationSaveForm;
	
	@Autowired
	ApplicationService applicationService;
	
 
	@RequestMapping(value = "/getappitems.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getappitems(@RequestBody ActionBean actionBean) {
		logger.info("getappitems call: /actionBean=" + actionBean);	
		String appId = actionBean.getData().get(0).getVal(); 
		String errorMsg = "";
		List<DataBean> appItemList = null;
		try {
			appItemList = applicationService.getAppItems(appId);
		} catch (Exception e) {
			errorMsg = "get items error";
		}
		return new ActionBean("na", appItemList, errorMsg);
	}
	
	@RequestMapping(value = "/insertappitems.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean insertappitem(@RequestBody ActionBean actionBean) {
		logger.info("insertappitems call: /actionBean=" + actionBean);	
		String appId = actionBean.getData().get(0).getVal(); 
		String formCode = actionBean.getData().get(1).getVal(); 
		String errorMsg = "";
		List<DataBean> appItemList = null;
		try {
			appItemList = applicationService.insertappitems(appId, formCode);
		} catch (Exception e) {
			errorMsg = "insert items error";
		}
		return new ActionBean("na", appItemList, errorMsg);
	}
	
	
	
	
}
