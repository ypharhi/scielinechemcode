package com.skyline.form.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.service.FormApiElementsGeneralService;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilLogger;

@Controller
@RequestMapping("/skylineForm")
public class FormApiElementsGeneralCode {

	@Autowired
	private FormApiElementsGeneralService formApiElementsGeneralService;

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	public GeneralUtilLogger generalUtilLogger;
	
	private static final Logger logger = LoggerFactory.getLogger(FormApiElementsGeneralCode.class);

	@RequestMapping(value = "/getStabValuesFromApi.request", method = { RequestMethod.GET, RequestMethod.POST })
	public void getStabValuesFromApi(/*@RequestParam Map<String, String> allRequestParams, */HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.info("getStabValuesFromApi call");

		int userId = generalUtil.getUserId(request);
		String product = request.getParameter("ajProduct");
		//        String spIdList = request.getParameter("ajSpIdList");
		if (product != null) {
			formApiElementsGeneralService.getStabValuesFromApi(userId, /*allRequestParams*/product, response);
		}
	}
	
	@RequestMapping(value = "/getQrCodeLabel.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getQrCodeLabel(HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.info("getQrCodeLabel call");
		String formId = request.getParameter("formId");
		String structFormCode = request.getParameter("structFormCode");
		try {
			if (formId != null) {
				String labelData = formApiElementsGeneralService.getQrCodeLabel(formId,structFormCode);
				return new ActionBean("no action needed", generalUtil.StringToList(labelData),"");
			}
		} catch (Exception ex){
			String errMsg = ex.getMessage();
			return new ActionBean("no action needed", generalUtil.StringToList("-1"), errMsg);
		}
		return null;
	}
	
	@RequestMapping(value = "/doSaveQrcode.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean doSaveQrcode(@RequestBody ActionBean actionBean, HttpServletRequest request) {
		
		logger.info("doSaveQrCode call");
		String saveStructFormCode = request.getParameter("saveStructFormCode");
		String parentId = request.getParameter("PARENT_ID");
		String userId = request.getParameter("userId");
		String qrCodeCsv = actionBean.getData().get(0).getVal();
		try {
			if (saveStructFormCode != null && !qrCodeCsv.isEmpty()) {
				String[] qrCodeList = qrCodeCsv.split(",");
				formApiElementsGeneralService.doSaveQrcode(saveStructFormCode,qrCodeList,parentId,userId);
				return new ActionBean("no action needed", generalUtil.StringToList("1"),"");
			}
		} catch (Exception ex){
			String errMsg = ex.getMessage();
			return new ActionBean("no action needed", generalUtil.StringToList("-1"), errMsg);
		}
		return new ActionBean("no action needed", generalUtil.StringToList("1"),"");
	}
}
