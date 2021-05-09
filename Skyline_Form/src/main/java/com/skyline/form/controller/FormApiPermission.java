package com.skyline.form.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.service.FormApiPermissionService;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilLogger;

@Controller
@RequestMapping("/skylineForm")
public class FormApiPermission {

	private static final Logger logger = LoggerFactory.getLogger(FormController.class);

	@Autowired
	private FormApiPermissionService formApiPermissionService;

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	public GeneralUtilLogger generalUtilLogger;
 
	
	@RequestMapping(value = "/getNewAvailableFormList.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getNewAvailableFormList(@RequestBody ActionBean actionBean) {
		logger.info("getNewAvailableFormList call: /actionBean=" + actionBean);
		String formId = "-1";
		try {
			try {
				formId = actionBean.getData().get(1).getVal();
			} catch (Exception e) {
				// do nothing
			}
			return formApiPermissionService.getNewAvailableFormList(actionBean);
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.ERROR, "WF error! exception in STATUS_WF_LIST evaluation",
					ActivitylogType.WorkFlowNew, formId, e, null);
			return new ActionBean("no action needed", generalUtil.StringToList(""), "Error in evaluating new states");
		}

	}

	@RequestMapping(value = "/getNewAvailableFormListById.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getNewAvailableFormListById(@RequestBody ActionBean actionBean) {
		logger.info("getNewAvailableFormListById call: /actionBean=" + actionBean);
		return formApiPermissionService.getNewAvailableFormListById(actionBean);
	}

	@RequestMapping(value = "/getReadPermissionById.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getReadPermissionById(@RequestBody ActionBean actionBean) {
		logger.info("getReadPermissionById call: /actionBean=" + actionBean);
		return formApiPermissionService.getReadPermissionById(actionBean);
	}

	@RequestMapping(value = "/getCreatePermissionFormCode.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getCreatePermissionFormCode(@RequestBody ActionBean actionBean) {
		logger.info("getCreatePermissionFormCode call: /actionBean=" + actionBean);
		return formApiPermissionService.getCreatePermissionFormCode(actionBean);
	}
}
