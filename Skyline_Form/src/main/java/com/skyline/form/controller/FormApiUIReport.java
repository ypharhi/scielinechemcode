package com.skyline.form.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.service.FormApiUIReportService;
import com.skyline.form.service.FormService;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilFormState;
import com.skyline.form.service.GeneralUtilLogger;

@Controller
@RequestMapping("/skylineForm")
public class FormApiUIReport {

	private static final Logger logger = LoggerFactory.getLogger(FormController.class);

	@Autowired
	private FormApiUIReportService formApiUIReportService;
	
	@Autowired
	private GeneralUtil generalUtil;
	
	@Autowired
	public GeneralUtilLogger generalUtilLogger;
	
	@Autowired
	private FormService formService;
	
	@Autowired
	private GeneralUtilFormState generalUtilFormState;
	
	@RequestMapping(value = "/uireportInit.request", method = { RequestMethod.GET, RequestMethod.POST }, produces = "text/html; charset=UTF-8")
	public void uireportInit(@RequestParam("formCode") String formCode, @RequestParam("formId") String formId,
			@RequestParam("userId") String user, @RequestParam("stateKey") String stateKey, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		logger.info("FormApiUIReport call: /uireportInit.request");	
		String url = "init.request?stateKey=" + stateKey + "&formCode=" + formCode + "&formId=" + formId + "&userId=" + user + "&avoidBreadCrumb=1"; //TODO innerinitid check
		generalUtilFormState.pushIntoBackNavigationStack(generalUtil.getNullLong(request.getParameter(stateKey)), formId, formCode, "", "", url,user); //TODO key pass stateKey
		formApiUIReportService.uireportInit(request,response);
	}	

	@RequestMapping(value = "/getUIReportData.request", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String getUIReportData(HttpServletRequest request, HttpServletResponse response, @RequestParam("requestType") String requestType) { //, @RequestParam("reportName") String reportName) {
		String requestData = null, reportSettings = null, reportId = null;
		try 
		{
			response.setContentType("charset=UTF-8");
			requestType = URLDecoder.decode(requestType, "UTF-8");
			if (requestType.equals("DELETE_REPORT"))
			{
				requestData = request.getParameter("DELETE_REPORT");
			};
			
			reportId = request.getParameter("REPORT_ID"); 
			
			
			if (request.getParameter("data") != null)
			{
				requestData = request.getParameter("data");
			}
			
			Map<String,String> additionalInfo = new HashMap<>(); 
			
			logger.info(request.getParameter("requestType"));
			logger.info(request.getParameter("data"));
			additionalInfo.put("param_requestType", request.getParameter("requestType"));
			
			additionalInfo.put("param_REPORT_ID", reportId);
			
			generalUtilLogger.logWrite(LevelType.DEBUG, request.getParameter("requestType"),"-1", ActivitylogType.UiReport,additionalInfo);
			if  (request.getParameter("data") != null)
			{
				additionalInfo.put("param_data", request.getParameter("data"));
				generalUtilLogger.logWrite(LevelType.DEBUG, request.getParameter("data"),"-1", ActivitylogType.UiReport,additionalInfo);
			} else
			{
				if (requestData != null)
				{
					additionalInfo.put("delete_report: param_requestData", requestData);
					generalUtilLogger.logWrite(LevelType.DEBUG, request.getParameter("data"),"-1", ActivitylogType.UiReport, additionalInfo);
				}
			}
			if  ((reportSettings = request.getParameter("reportSettings")) != null)
			{
				additionalInfo.put("param_reportSettings", request.getParameter("reportSettings"));
				generalUtilLogger.logWrite(LevelType.DEBUG, request.getParameter("reportSettings"),"-1", ActivitylogType.UiReport,additionalInfo);
			} 
			System.out.println("requestData: " + requestData);
			
		} catch (UnsupportedEncodingException e) {
			logger.error("getUIReportData Exception!");
			generalUtilLogger.logWrite(LevelType.ERROR, "Exception", "-1",ActivitylogType.UiReport,null,e);
			e.printStackTrace();
		}
		
		return formApiUIReportService.getUIReportData(requestType, requestData, reportSettings, reportId).toString();
	}
	
	@RequestMapping(value = "/onUIReportRuleListChange.request", method = { RequestMethod.GET,
			RequestMethod.POST })
	public @ResponseBody ActionBean onUIReportRuleListChange(@RequestParam("reportName") String reportName, @RequestParam("fieldName") String fieldName) {
		//logger.info("onUIReportRuleListChange call: /ActionBean=" + actionBean);
		/*String reportName = actionBean.getData().get(0).getVal();
		String fieldName = actionBean.getData().get(0).getVal();*/
		String toReturn = formApiUIReportService.onUIReportRuleListChange(reportName,fieldName);
		return new ActionBean("no action needed", generalUtil.StringToList(toReturn), "");
	}
	
}