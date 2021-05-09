package com.skyline.notification.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.skyline.form.service.GeneralUtil;
import com.skyline.notification.service.NotificationService;
 
@Controller
@RequestMapping("/skylineForm")
public class NotificationController {
	private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private GeneralUtil generalUtil;
	
	@RequestMapping(value = "/notification.request", method = { RequestMethod.GET, RequestMethod.POST }, produces = "text/html; charset=UTF-8")
	public void demoNotificationMainInit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("demoNotificationMainInit call: /notification.request");//request.toString());
		generalUtil.getUserId(request);
		generalUtil.canEdit(request);
		generalUtil.getUserName(request);
		
		notificationService.demoNotificationMainInit(request,response);
	}	
		
	@RequestMapping(value = "/notificationModule.request", method = { RequestMethod.GET, RequestMethod.POST }, produces = "text/html; charset=UTF-8")
	public void demoNotificationModuleMainInit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.info("demoNotificationModuleMainInit call: /notificationModule.request");//		generalUtil.getUserId(request);
		generalUtil.canEdit(request);
		generalUtil.getUserName(request);
		
		notificationService.demoNotificationModuleMainInit(request,response);	
	}
	
}