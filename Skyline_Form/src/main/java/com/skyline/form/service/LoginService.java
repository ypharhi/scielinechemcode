package com.skyline.form.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.skyline.form.dal.LoginDao;

@Service
public class LoginService { // TODO interface FormService and this should be FormServiceImp (in case we want to switch between different behaviors/algorithm) 

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private LoginDao loginDao;
	
	@Autowired
	private GeneralUtilPermission generalUtilPermission;
	
	@Autowired
	private GeneralUtilNotificationEvent generalUtilNotificationEvent;
	
	@Autowired
	GeneralUtilFavorite generalUtilFavorite;
	
	@Value("${isLdapAuthentication}")
	private String isLdapAuthentication;
	
	@Value("${ldapUrl}")
	private String ldapUrl;
	
	private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

	private static final String USER_MESSAGE = "Cannot connect due to an error"; // kd There in db are label with LabelCode: Cannot connect due to an error

	public ModelAndView loginAction(HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession();
		logger.info("start login servlet...");

		String userMessage = "";
		Map<String, String> paramMap = new HashMap<String, String>();

		// Checking user and password from client
		String username = "";
		String password = "";
		String browserName = "na";

		try {
			username = request.getParameter("txtUser");
			password = request.getParameter("txtPassword");
			browserName = request.getParameter("browserName");
		} catch (Exception e) {
			logger.error(e.getMessage() + ", username= " + username + "password= " + password + ", browserName=" + browserName);
			paramMap.put("msg", USER_MESSAGE + " (0)");	
			return new ModelAndView("Login",paramMap);
		}		

		
		boolean isSuccessful = false;

		int aRetCode = authenticateUser(username, password, request.getRemoteAddr());

		switch (aRetCode) {
		case -2: // error (probably with connection to DB)
			userMessage = "Cannot connect due to an error";
			break;
		case -1: // wrong  user/password
			userMessage = "Wrong user name or password!";
			break;
		case 0: // renew
			userMessage = "Please, change your password. First Entry";
			String userId = loginDao.getUserIdByUserName(username);		
			session.setAttribute("lastUserName", username);
			paramMap.put("USER_ID", userId);
			paramMap.put("lastUserName", username);
			session.setAttribute("lastUserId", userId);		
			paramMap.put("CHANGE_PASSWORD", "1");			
			break;
		case 1: // success
			isSuccessful = true;
			break;
		case 2: // password is due to expire (grace period)
			userMessage = "Please change your password as it will expire soon";
			isSuccessful = true;
			break;
		case 3: // the account is already locked
			userMessage = "The account is locked. Please contact your administrator";
			break;
		case 4: // expired
			userMessage = "Your password is expired. Please contact your administrator";
			break;
		case 9: // locked
			userMessage = "Maximum number of login retries exceeded.\nThe account has been locked.\nPlease, contact the system administrator";
			break;
		}

		if (isSuccessful) {			
			String currentUserInSession = generalUtil.getNull((String)session.getAttribute("userName"));
			//check if already logged in
			if(!currentUserInSession.equals("") && !currentUserInSession.equals(username)) {
				try {
					response.sendRedirect(request.getContextPath() + "/?PERMISSION_DENIED_MULTI_USER=1");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}
			String userId = loginDao.getUserIdByUserName(username);
			long stateKey = generalUtil.generateStateKey(userId);
			session.setAttribute("userId", userId);
			session.setAttribute("userName", username);	
			session.setAttribute("browserName", browserName);
			StringBuilder defaultMenu = new StringBuilder();
			String homePage = homePage(stateKey, userId,username,session,defaultMenu);
			session.setAttribute("homePage", homePage);
			session.setAttribute("MAIN_MENU", buildMainMenu(userId, username, defaultMenu.toString(),session));
			if(session.getAttribute("SHOW_RESTORE_BREADCRUMB_BUTTON") == null)
			{
				session.setAttribute("SHOW_RESTORE_BREADCRUMB_BUTTON", "1");
			}
			generalUtilNotificationEvent.getMessageCount(userId, true);
			generalUtilFavorite.initFavoriteList(loginDao.getUserFavoriteList(userId));
			return new ModelAndView("redirect:/" + homePage);
		}

	
		paramMap.put("msg", userMessage);	
		
		return new ModelAndView("Login",paramMap);
	}

	private String buildMainMenu( String userId, String username, String mainScreen,HttpSession session)
	{
		StringBuilder toReturn = new StringBuilder();
		Map<String, List<JSONObject>> listOfScreens = new LinkedHashMap<String, List<JSONObject>>();
		String href = "";
		boolean hasChild = false;
		String css = "";
		double dynamicPadding = 0.0;
		int listSize;
		
		try 
		{
			listOfScreens = generalUtilPermission.getMenuScreenListByUserId(userId, username, mainScreen);
			ArrayList<String> listOfFormCodeScreens = new ArrayList<String>();
			toReturn.append("<ul class=\"main-navbar dropdown menu\" data-dropdown-menu>");
			listSize = listOfScreens.size()+1;
			
			if(listSize < 5)
			{
				dynamicPadding = 2;
			}
			else if(listSize <= 8)
			{
				dynamicPadding = 100.0/(listSize*5);
			}
			else
			{
				dynamicPadding = 100.0/(listSize*10);
			}
			
			for(Map.Entry<String, List<JSONObject>> row: listOfScreens.entrySet())
			{
				String rowKey = row.getKey();          
				List<JSONObject> rowData = row.getValue();
				for(JSONObject o:rowData)
				{
					StringBuilder subChild = new StringBuilder();
					String order = o.optString("sub_category_order");
					if(!o.optString("formCode").equals("")){
						listOfFormCodeScreens.add(o.optString("formCode"));
					}
					css = "";
					href = "";
					if(order.equals("0"))
					{
						css = "class=\""+o.optString("css_class")+"\"";
					}
					if(rowKey.equals("Form Builder") && order.equals("0"))
					{
						//href =  "href='../skylineForm/demoFormBuilderMainInit.request?stateKey=@@STATEKEY_HOLDER@@&userId="+userId+"'";
						href =  "'../skylineForm/demoFormBuilderMainInit.request?stateKey=@@STATEKEY_HOLDER@@&userId="+userId+"'";
					}
					else if(rowKey.equals("Search Label") && order.equals("0"))
					{
						//href =  "href='#' onclick=\"openSearchLabelDialog();return false;\"";
						href =  "'#' onclick=\"openSearchLabelDialog();return false;\"";
					}
					else if(rowKey.equals("Notifications"))
					{
						//href = "href='../skylineForm/notification.request?formCode="+o.optString("formCode")+"&formId=-1&userId="+userId+"'";
						href = "'../skylineForm/notification.request?formCode="+o.optString("formCode")+"&formId=-1&userId="+userId+"'";
					}
					else if(o.optString("sub_category").equals("Reports") && !order.equals("0"))
					{
						//href = "href='../skylineForm/uireportInit.request?formCode="+o.optString("formCode")+"&formId=-1&userId="+userId+"&stateKey=@@STATEKEY_HOLDER@@'";
						href = "'../skylineForm/uireportInit.request?formCode="+o.optString("formCode")+"&formId=-1&userId="+userId+"&stateKey=@@STATEKEY_HOLDER@@'";
					}
					else if(o.optString("sub_category").equals("Reaction and Results Analysis"))
					{
						href="'#'";
						String reportSchemeHref = "'../skylineForm/init.request?formCode=ExperimentReport&formId=-1&userId="+userId+"&nameId=-1&stateKey=@@STATEKEY_HOLDER@@'";
						String reportDesignHref="openReportDesignScreen();";
						String subChildCss = " onmouseover=\"getReportList(this,false,'ExperimentReport')\" onclick=\"removeReportList(this)\"";
						subChild.append("<ul class=\"menu is-dropdown-submenu\" style=\"z-index:500\">")
						.append("<li "+subChildCss+" class=\"reportScheme-dropdown-submenu\">").append("<a ").append("href="+reportSchemeHref+">").append("Report Scheme").append("</a>").append("</li>")
						.append("</ul>");						
					}
					else if(o.optString("sub_category").equals("Experiment Analysis Report"))
					{
						href="'#'";
						String reportSchemeHref = "'../skylineForm/init.request?formCode=ExpAnalysisReport&formId=-1&userId="+userId+"&nameId=-1&stateKey=@@STATEKEY_HOLDER@@'";
						String reportDesignHref="openReportDesignScreen();";
						String subChildCss = " onmouseover=\"getReportList(this,false)\" onclick=\"removeReportList(this)\"";
						subChild.append("<ul class=\"menu is-dropdown-submenu\" style=\"z-index:500\">")
								.append("<li "+subChildCss+" class=\"reportScheme-dropdown-submenu\">").append("<a ").append("href="+reportSchemeHref+">").append("Report Scheme").append("</a>").append("</li>")
								.append("<li "+subChildCss+" class=\"reportDesign-dropdown-submenu\">").append("<a ").append("onClick="+reportDesignHref+">").append("Report Design").append("</a>").append("</li>")
								.append("</ul>");						
					}
					else if(o.optString("sub_category").equals("Stability Planning")) // patch "Taro develop" navigate to skyline BIO (in fg_i_screens_v we need to have: select 'Project Management' as category_, 2 as category_order, 'Stability Planning' as sub_category, 3 as sub_category_order, 'Dummy' as formCode, '' as class_, '1' as system_ FROM DUAL)
					{
						href = "'http://192.168.10.72/Skyline_Dev/wizstabproductlistservlet_'";//onmouseout=\"removeReportList(this)\"
					}
					else if(!o.optString("formCode").equals(""))
					{
							//href = "href='../skylineForm/init.request?formCode="+o.optString("formCode")+"&formId=-1&userId="+userId+"&stateKey=@@STATEKEY_HOLDER@@'";
						href = "'../skylineForm/init.request?formCode="+o.optString("formCode")+"&formId=-1&userId="+userId+"&stateKey=@@STATEKEY_HOLDER@@'";
					}
					else
					{
						//href = "href='#'";
						href = "'#'";
					}
					
					//css+=" onclick=\" return confirmWithOutSaveMainMenu();\"";
					String onClick = " onclick=\" confirmWithOutSaveMainMenu("+href+");\"";
					if(!order.equals("0") && !hasChild && rowData.size() > 1)
					{						
						toReturn.append("<ul class=\"menu is-dropdown-submenu\" style=\"z-index:500\">");
						hasChild = true;
					}
					toReturn.append("<li "+css+" style='padding-left:"+dynamicPadding+"%;'>").append("<a ").append("href='#' "+onClick+ " >").append(o.optString("sub_category")).append("</a>");
					if(!subChild.toString().isEmpty())
					{
						toReturn.append(subChild);
					}
					if(!order.equals("0"))
					{
						toReturn.append("</li>");
					}
				}
				if(hasChild)
				{
					toReturn.append("</ul>");
				}
				toReturn.append("</li>");
				hasChild = false;
			}
			
			session.setAttribute("menuScreen", generalUtil.listToCsv(listOfFormCodeScreens));
			
			//User guide:
			String userGuideLabel = (username != null && username.equalsIgnoreCase("system"))? "U-Guide":"User Guide"; //short label for system to avoid display in new line
			toReturn.append("<li class=\"user-guide\" style='padding-left:"+dynamicPadding+"%;'>").append("<a href='#' >" + userGuideLabel + "</a>");
			toReturn.append(generalUtilPermission.appendUserGuide(userId,username)); //fill user guide items
			toReturn.append("</li>");
			toReturn.append("</ul>");
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			toReturn = new StringBuilder();
		}
		
		return toReturn.toString();
	}

	/**
	 * Used to authenticate user and fetch his record as User object (basically for Login)
	 * 
	 * @param userName
	 *            - user name in Skyline DB
	 * @param password
	 *            - either LDAP password or Skyline password (if no LDAP for user)
	 * @param stationIpAddress
	 *            - remote address fetched from HttpRequest
	 * @param aRetCode
	 *            - an array of 1 int, used to "send by reference" the return-code
	 * @return <code>User</code> object if the user was authenticated, otherwise null
	 */
	public int authenticateUser(String userName, String password, String stationIpAddress) {	
		String encPassword = generalUtil.getMd5(password);
		boolean isLDAP = (generalUtil.getNull(isLdapAuthentication).equals("1")) ? true : false;
		
		if (isLDAP) {
			// get ldap name
			String ldapName = loginDao.getLDAPNameByUserName(userName);
			// whether to use LDAP or local authentication
			if (ldapName.equals("")) {
				return Integer.valueOf(loginDao.authenticateUser(userName, encPassword, true, stationIpAddress));				
			}
			// else authenticate by LDAP
			if (authenticateUserByLDAP(ldapName, password)) {	
				loginDao.writeToAccessLog(userName, stationIpAddress, true);
				return 1; // successful authentication				
			} else { // LDAP authentication failed
				loginDao.writeToAccessLog(userName, stationIpAddress, false);
				return -1;				
			}
		} else {
			return Integer.valueOf(loginDao.authenticateUser(userName, encPassword, true, stationIpAddress));
		}
	}

	private boolean authenticateUserByLDAP(String ldapName, String password) {
//		loginDao.writeLDAPInfo("LDAPAuthentication.authenticateUserByLDAP() ldapName=" + ldapName + ". password=XXXXX, ldapUrl=" + ldapUrl, null);
		boolean isAuthenticated = false;
		Hashtable<Object,String> env = new Hashtable<Object, String>();

		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL,generalUtil.getNull(ldapUrl));
		env.put(Context.SECURITY_PRINCIPAL, ldapName);
		env.put(Context.SECURITY_CREDENTIALS, password);

		try {
			if (ldapName == null || ldapName.trim().equals("") || password == null || password.trim().equals("")) {
				// LG.write("empty user or password", "LDAPAuthentication.authenticateUserByLDAP()", null);
				isAuthenticated = false;
			} else {
				// try to authenticate
				DirContext context = new InitialDirContext(env);
				// only if user is authenticated the code continutes (otherwise, an exception is thrown)
				isAuthenticated = true;
				String[] attributes = { "member" };
				SearchControls sc = new SearchControls();
				sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
				sc.setCountLimit(1000);
				sc.setReturningAttributes(attributes);
				sc.setReturningObjFlag(true);
				//            NamingEnumeration answer = context.search(props.getLDAPSearchDirectory(), "(objectclass=*)", sc );
			}
		} catch (Exception ex) {
			loginDao.writeLDAPInfo("LDAPAuthentication.authenticateUserByLDAP() ldapName=" + ldapName + ". password=XXXXX, ldapUrl=" + ldapUrl + ", error:: " + ex.toString(), ex);
			logger.error("LDAPAuthentication.authenticateUserByLDAP(): " + ex.toString());
			// LG.write(ex.toString(), "LDAPAuthentication.authenticateUserByLDAP()", ex);
			isAuthenticated = false;
		}

		return isAuthenticated;
	}
	
	private String homePage(long stateKey, String userId, String userName,HttpSession session, StringBuilder defaultMenu) {
		if (userName.equalsIgnoreCase("Admin")){
			session.setAttribute("header", "Admin");
			return "skylineForm/init.request?formCode=Main&formId=-1&userId=" + userId + "&stateKey=" + stateKey;
		} else if(userName.equalsIgnoreCase("System")) {
			session.setAttribute("header", "System");
			return "skylineForm/demoFormBuilderMainInit.request?stateKey=" + stateKey;
		}
		
		if(generalUtilPermission.isPermissionExists(userId)) {
			session.setAttribute("header", "user");
			defaultMenu.append("Project Management");
			return "skylineForm/init.request?formCode=Main&formId=-1&userId=" + userId+ "&stateKey=" + stateKey;
		} else {
			defaultMenu.append("");
			session.setAttribute("header", "user");
			return "skylineForm/init.request?formCode=NoPermission&formId=-1&userId=" + userId+ "&stateKey=" + stateKey;
		}
	}

	
	/**
	 * changePassword
	 * @param request
	 * @param response
	 * @return
	 */
	public ModelAndView changePassword(HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession();		
 
		Map<String, String> paramMap = new HashMap<String, String>();

		// Checking user and password from client
		String oldPassword = "", newPassword = "", txtUserId = "", lastUserId = "";		
		try {
			lastUserId = (String)session.getAttribute("lastUserId");
			oldPassword = request.getParameter("oldPassword");
			newPassword = request.getParameter("newPassword");
			txtUserId = request.getParameter("txtUserId");
		} catch (Exception e) {
			logger.error(e.getMessage() + ", username= " + lastUserId);		
			paramMap.put("msg", "Something went wrong");	
			return new ModelAndView("Login",paramMap);
		}
		
		if(!lastUserId.equals(txtUserId)){
			logger.error("lastUserId in the session is not equal to the 'txtUserId' field");		
			paramMap.put("msg", "Something went wrong");	
			return new ModelAndView("Login",paramMap);
		}
		String oldMd5 = generalUtil.getMd5(oldPassword);
		String newMd5 = generalUtil.getMd5(newPassword);
		
		String update = loginDao.changePassword(lastUserId,oldMd5,newMd5);
		if(update.equals("-1")){
			paramMap.put("msg", "Something went wrong");	
			return new ModelAndView("Login",paramMap);
		}
		else if(update.equals("0")){
			paramMap.put("msg", "Password is not correct");	
			return new ModelAndView("Login",paramMap);
		}
		request.setAttribute("txtUser", (String)session.getAttribute("lastUserName"));
		request.setAttribute("txtPassword", newPassword);     	
		return loginAction(request,response);
	}
}