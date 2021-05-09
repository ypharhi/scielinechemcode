package com.skyline.form.service;


import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

	public class SessionListener implements HttpSessionListener {
				
	    @Override
	    public void sessionCreated(HttpSessionEvent arg0) {
	          
	           System.out.println("sessionCreated");
	          
	    }
	    @Override
	    public void sessionDestroyed(HttpSessionEvent arg0) {
	          
	           System.out.println("sessionDestroyed");
	           
	           removeTempFile(arg0);
	           
	    }
	    private void removeTempFile(HttpSessionEvent sessionEvent){

	          HttpSession session = sessionEvent.getSession();
	          FormStateManager formStateManager = (FormStateManager)session.getAttribute("scopedTarget.formStateManager");
	          String sessionUserName = session.getAttribute("userName").toString();
	          String curTabStateKey = session.getAttribute("CURRENT_TAB_STATE_KEY").toString();
	          System.out.println("sessionUserName: " + sessionUserName);
	          System.out.println("session ID: " + session.getId());
	          System.out.println("curTabStateKey: " + curTabStateKey);
	          formStateManager.saveNavigationStackObject(sessionUserName, curTabStateKey);

//	          ApplicationContext ctx =
//	                WebApplicationContextUtils.
//	                      getWebApplicationContext(session.getServletContext());
//
//	          FormTask formTask =
//	                      (FormTask) ctx.getBean("FormTask");
//
//	          formTask.cleanup(); //TODO add it to scheduler in FormTask 
	    }
	}

