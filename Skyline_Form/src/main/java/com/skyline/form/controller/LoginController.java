package com.skyline.form.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.skyline.form.service.LoginService;


@Controller
//@RequestMapping("/skylineForm")
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(FormController.class);

	@Autowired
	private LoginService loginService;	
	
	@RequestMapping(value = "/loginAction.request", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView loginAction(HttpServletRequest request, HttpServletResponse response) {
		logger.info("loginAction.request call: /code=");	
		return loginService.loginAction(request,response);		
	}

	@RequestMapping(value = "/changePassword.request", method = { RequestMethod.GET, RequestMethod.POST })
	public ModelAndView changePassword(HttpServletRequest request, HttpServletResponse response) {
		logger.info("changePassword.request call: /code=");	
		return loginService.changePassword(request,response);		
	}
}
