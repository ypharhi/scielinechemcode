package com.skyline.form.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.service.GeneralUtilLogger;

@Aspect
public class LoggingAspect {	
	private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

//	@Autowired
//	private Integration integration;
	
	@Autowired
	private GeneralUtilLogger generalUtilLogger;

	@AfterThrowing(
			//  pointcut = "execution(* com.skyline.form.service.FormBuilderService.demoFormBuilderMainInit(..))",
			//			 pointcut = "execution(* com.skyline.form.bean..*(..)) || " +
			//			  		 "execution(* com.skyline.form.service..*(..)) || " +
			//			  	 	 "execution(* com.skyline.form.entitypool..*(..)) || " + 
			//			  		 "execution(* com.skyline.form.dal..*(..)) || " +
			//			  		 "execution(* com.skyline.form.entity..*(..)) || " +
			//			  		 "execution(* com.skyline.form.controller..*(..)) " ,
			pointcut = "execution(* com.skyline.form.controller..*(..))", throwing = "error")
	/**
	 * 
	 * @param joinPoint
	 * @param error
	 *  Catch exceptions which not caught in catch block and write them to the dataBase
	 */	
	public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {	
		String extractMethodName = ExtractMethodName(error);
		logger.error("logAfterThrowing() is running!");
		logger.error("hijacked : " + joinPoint.getSignature().getName());
		logger.error("Exception : " + error);
		logger.error("printStackTrace : ");
		logger.error(extractMethodName);
		generalUtilLogger.logWrite(LevelType.ASPECT_EXCEPTION, "System Exception - catch in LoggingAspect. joinPoint.getSignature=" + joinPoint.getSignature().getName(), "", ActivitylogType.AspectException, null, error);
	}

	private String ExtractMethodName(Throwable ex) {
//		String toReturn = "";
//		try {
//			if (ex != null) {
//				String methodFormat = "%s\n";
//				StackTraceElement[] st = ex.getStackTrace();
//				StringBuilder mName = new StringBuilder();
//				mName.append(String.format(methodFormat, "Exception: " + ex.toString()));
//				for (int i = 0; i < st.length; i++) {
//					mName.append(String.format(methodFormat, st[i].toString()));
//				}
//
//				toReturn = mName.toString();
//			} else {
//				toReturn = "StackTrace(logger) - no exception to trace!";
//			}
//		} catch (Exception e) {
//			toReturn = "StackTrace(logger) - Error!";
//		}
//		return toReturn;
		String toReturn = "";
		String smsg_ = "";
		try {
			if (ex != null) {
				String methodFormat = "%s\n";
				StackTraceElement[] st = ex.getStackTrace();
				StringBuilder mName = new StringBuilder();
				mName.append(String.format(methodFormat, "Exception: " + ex.toString()));
				for (int i = 0; i < st.length; i++) {
					smsg_ = String.format(methodFormat, st[i].toString());
					if(smsg_.contains("skyline.form")) {
						mName.append(String.format(methodFormat, st[i].toString()));
					}
				}
				
				toReturn = mName.toString();
			} else {
				toReturn = "StackTrace(logger) - no exception to trace!";
			}
		} catch (Exception e) {
			toReturn = "StackTrace(logger) - Error!";
		}
		return toReturn;
	}
}
