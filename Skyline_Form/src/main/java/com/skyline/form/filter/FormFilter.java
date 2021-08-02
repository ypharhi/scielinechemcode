package com.skyline.form.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.skyline.form.service.GeneralUtil;
public class FormFilter implements Filter {
	  
	@Autowired
	private GeneralUtil generalUtil;
	
//	@Value("${useJspCaching:1}")
//	private String useJspCaching;

	public FormFilter() {
		// TODO Auto-generated constructor stub
	}

	public void destroy() {
		// TODO Auto-generated method stub
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {	
		
		HttpServletRequest httpRequest = (HttpServletRequest) request;		
		HttpSession session = httpRequest.getSession();
		HttpServletResponse httpResponse = (HttpServletResponse) response;
//		httpResponse.setHeader("Access-Control-Allow-Origin", "*");
//		httpResponse.setHeader("X-Frame-Options", "allow-from *");
//		httpResponse.setHeader("Access-Control-Allow-Origin", "*");
//		if(!useJspCaching.equals("1")) {
			
		// Set to expire far in the past.
		httpResponse.setHeader("Expires", "Sat, 6 May 1995 12:00:00 GMT");
		
		// Set standard HTTP/1.1 no-cache headers.
		httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		
		// Set IE extended HTTP/1.1 no-cache headers (use addHeader).
		httpResponse.addHeader("Cache-Control", "post-check=0, pre-check=0");
		
		// Set standard HTTP/1.0 no-cache header.
		httpResponse.setHeader("Pragma", "no-cache");
//		}
		
		@SuppressWarnings("unchecked") //kd 28112017 This block is temp (till //end kd 28112017). For development period only! Delete it after development 
		Map<String, String[]> reqMap = request.getParameterMap(); //kd 28112017 2 temp rows
		try
		{
			if (reqMap.get("flagTempDevelop") != null){
				if (reqMap.get("flagTempDevelop")[0].equals("1")) //kd 28112017 This 2 temp rows. Only for development period 
				{
					chain.doFilter(request, response);
					return;
				}
			}
		} catch (Exception e)
		{
			
		} //end kd 28112017. For development period only! Delete it after development
		
		
				  
		if (session.getAttribute("userId") == null) {			
		//	httpResponse.sendRedirect(integration.getTimeOutURL()); old
			//httpResponse.sendRedirect(httpResponse.encodeRedirectURL(httpRequest.getContextPath() + "/"));	
			httpResponse.sendRedirect(httpRequest.getContextPath() + "/");
			return;
		} else {
			@SuppressWarnings("unchecked")
			Map<String, String[]> requestMap = request.getParameterMap(); 
			//security - check request URL is equal to the login session value - the rest is in authen sql
			if(requestMap.get("userId") != null && requestMap.get("userId").length > 0 && !generalUtil.getNull(requestMap.get("userId")[0]).equals(String.valueOf(session.getAttribute("userId")))) {
				httpResponse.sendRedirect(httpRequest.getContextPath() + "/?PERMISSION_DENIED_MULTI_USER=1");
				return;
			}
		}
		
		//GZip code BU - we first try to check performance by using the tomcat compress=on prop
//		try {
//			HttpServletRequest req = (HttpServletRequest) request;
//			HttpServletResponse res = (HttpServletResponse) response;
//
//			if (isGZipEncoding(req)) {
//				GZipResponse zipResponse = new GZipResponse(res);
//				res.setHeader("Content-Encoding", "gzip");
//				chain.doFilter(request, zipResponse);
//				zipResponse.flush();
//			} else {
//				chain.doFilter(request, response);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//			chain.doFilter(request, response);
//		} catch (ServletException e) {
//			e.printStackTrace();
//			chain.doFilter(request, response);
//		}
	}
	
//	/**
//	   * Determine if the browser supports GZIP
//	   * @param request
//	   * @return
//	   */
//	private boolean isGZipEncoding(HttpServletRequest request) {
//	    StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
//		String queryString = request.getQueryString();
//		if (queryString != null && !queryString.contains("Experiment") && !queryString.contains("init.request")) {
//			return false;
//		}
//		
//		String encoding = request.getHeader("Accept-Encoding");
//		if (encoding.indexOf("gzip") != -1) {
//			if(requestURL.toString().contains("onElementDataTableApiChange") || requestURL.toString().contains("init.request")) {
//		    	return true;
//		    }
//		}
//		return false;
//	}

	public void init(FilterConfig fConfig) throws ServletException {
		//Support Autowiring on Filter Class
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, fConfig.getServletContext());
	}

}
