package com.skyline.form.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.DataBean;
import com.skyline.form.service.FormApiExcelService;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilLogger;

@Controller
@RequestMapping("/skylineForm")
public class FormApiExcel {

	private static final Logger logger = LoggerFactory.getLogger(FormApiExcel.class);

	@Autowired
	private FormApiExcelService formApiExcelService; 

	@Autowired
	public GeneralUtilLogger generalUtilLogger;

	@Autowired
	private GeneralUtil generalUtil;

	@RequestMapping(value = "/getExcelComponentList.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getExcelComponentList(@RequestBody ActionBean actionBean, HttpServletRequest request) {
		logger.info("getExcelComponentList call: /actionBean=" + actionBean);
		String formId = request.getParameter("formId");
		return formApiExcelService.getExcelComponentList(formId,actionBean);
	}
	

	@RequestMapping(value = "/saveSpreadsheet.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean saveSpreadsheet(@RequestBody ActionBean actionBean, HttpServletRequest request) {

		logger.info("saveSpreadsheet call");
		String formId = request.getParameter("formId");
		String formCode = request.getParameter("formCode");
		String userId = request.getParameter("userId");
		String isNew = generalUtil.getNull(request.getParameter("isNew"));
		 
		List<DataBean> dataBeanList = actionBean.getData();

		try{
			String elementIdReturnVal =  formApiExcelService.saveSpreadsheet(dataBeanList.get(0), isNew, formCode, formId);
			return new ActionBean("no action needed", generalUtil.StringToList(elementIdReturnVal),"");
		} catch (Exception ex){
			String errMsg = ex.getMessage();
			return new ActionBean("no action needed", generalUtil.StringToList("-1"), errMsg);
		}
	}
	
	@RequestMapping(value = "/setSpreadsheetUserData.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean setSpreadsheetUserData(@RequestBody ActionBean actionBean, HttpServletRequest request) {

		logger.info("saveSpreadsheet call");
		String formId = request.getParameter("formId");
		String userId = request.getParameter("userId");
		 
		List<DataBean> dataBeanList = actionBean.getData();

		try{
			formApiExcelService.setSpreadsheetUserData(dataBeanList, formId,userId);
			return new ActionBean("no action needed", generalUtil.StringToList("1"),"");
		} catch (Exception ex){
			String errMsg = ex.getMessage();
			return new ActionBean("no action needed", generalUtil.StringToList("-1"), errMsg);
		}
	}
	
	@RequestMapping(value = "/getExcelDataById.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ActionBean getExcelDataById(@RequestParam("fileId") String fileId,
			@RequestParam("defaultfileId") String defaultfileId, @RequestParam("domId") String domId,
			@RequestParam("stateKey") Long stateKey, @RequestParam("formId") String formId,
			@RequestParam("formCode") String formCode) {

		return formApiExcelService.getExcelDataById(stateKey, formId, formCode, domId, fileId, defaultfileId);
	}
}
