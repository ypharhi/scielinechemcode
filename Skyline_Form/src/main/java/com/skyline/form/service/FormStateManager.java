package com.skyline.form.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import com.skyline.form.bean.Form;
import com.skyline.form.bean.FormType;
import com.skyline.form.bean.LookupType;
import com.skyline.form.bean.NavigationBean;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.UploadFileDao;
import com.skyline.form.entity.Catalog;
import com.skyline.form.entity.Element;
import com.skyline.form.entity.Layout;


@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class FormStateManager implements Serializable {
	 //TODO key - add to map key
 
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private FormStateFactory formStateFactory;
	
	@Autowired
    private GeneralUtil generalUtil;
	
	@Autowired
    private FormDao formDao;
	
	@Autowired
    private UploadFileDao uploadFileDao;
	
	@Autowired
	private GeneralUtilLogger generalUtilLogger;
	
	private Map<String, FormState> formManagerMap = new HashMap<String, FormState>();
	
	private Map<String,Deque<NavigationBean>> navigationMap = new LinkedHashMap<String,Deque<NavigationBean>>();
	
	private Map<String,Map<String,String>> sessionAttrMap = new HashMap<String,Map<String,String>>();
	
	private FormTempData formTempDataMap;
	
	@Value("${breadcrumbDisplayedValuesNum:4}")
	private int breadcrumbDisplayedValuesNum;
	
	private static final Logger logger = LoggerFactory.getLogger(FormStateManager.class);

	public FormStateManager() {
		formTempDataMap = new FormTempData();
	}

	public void initForm(boolean isNewFormId, Map<String,String> lastSaveValMap, long stateKey, String formCode, String userId, String formId, String nameId, String urlCallParam, Map<String, String[]> requestMap, Map<String, String> outParamMap) {
//		if(formManagerMap.containsKey(getStateKeyString(formCode, stateKey))) {
//			formManagerMap.remove(getStateKeyString(formCode, stateKey));
//		} // remove not needed
		FormState formState = formStateFactory.getFormState();
		formManagerMap.put(getStateKeyString(formCode, stateKey), formState);
		
		formState.initFormState(isNewFormId, lastSaveValMap, stateKey, formCode, userId, formId, nameId, urlCallParam, requestMap, outParamMap); 
	}
	
//	public void initFormBuilder(long stateKey, String formCode) {
//		if(!formManagerMap.containsKey(formCode)) {
//			formManagerMap.put(formCode, new FormState(formCode));
//		}
//	}
	 
	public FormState getFormState(long stateKey, String formCode) {
		return formManagerMap.get(getStateKeyString(formCode, stateKey));
	}
	
	public void setFormState(long stateKey, String formCode, FormState formState) {
		formManagerMap.put(getStateKeyString(formCode, stateKey), formState);
	}
	 
	// -- Param
	public Map<String, String> getFormParam(long stateKey, String formCode) {
		return formManagerMap.get(getStateKeyString(formCode, stateKey)).getFormParam();
	}

	public String getFormParam(long stateKey, String formCode, String key) {
		return formManagerMap.get(getStateKeyString(formCode, stateKey)).getFormParam(key);
	}  
	
	public void setFormParam(long stateKey, String formCode, String key, String val) { 
		formManagerMap.get(getStateKeyString(formCode, stateKey)).setFormParam(key, val);
	}
	
	public void setFormParam(long stateKey, String formCode, Map<String, String> map) {
		formManagerMap.get(getStateKeyString(formCode, stateKey)).setFormParam(map);
	}
	
	private void cleanFormParam(long stateKey, String formCode) {
		formManagerMap.get(getStateKeyString(formCode, stateKey)).getFormParam().clear();
		
	}
	
	// -- Bean
//	public Map<String, Object> getFormBean(long stateKey, String formCode) {
//		return formManagerMap.get(getStateKeyString(formCode, stateKey)).getFormBean();
//	}
	
//	public void setFormBeanAndInit(long stateKey, String formCode, String entityImpClass, String entityImpCode, String entityImpInit) { 
//		Entity entity = (Entity)getFormBean(stateKey, formCode,entityImpCode);
//		if(entity == null || entity.getInitVal() == null || !entity.getInitVal().equals(entityImpInit)) {
//			entity = entityFactory.getEntity(entityImpClass);
//			entity.init(stateKey, formCode, entityImpCode, entityImpInit);
//			setFormBean(stateKey, formCode, entityImpCode, entity);
//		} 
//	}  
	 
//	public void setFormBean(long stateKey, String formCode, Map<String, Object> map) {
//		formManagerMap.get(getStateKeyString(formCode, stateKey)).setFormBean(map);
//	}

//	public void setFormBean(long stateKey, String formCode, String key, Object val) {
//		formManagerMap.get(getStateKeyString(formCode, stateKey)).setFormBean(key, val);
//	}

	public Object getFormBean(long stateKey, String formCode, String key) {
		return formManagerMap.get(getStateKeyString(formCode, stateKey)).getFormBean(key);
	}
	
	public void removeFormBean(long stateKey, String formCode, String entityImpCode) {
		formManagerMap.get(getStateKeyString(formCode, stateKey)).removeFormBean(entityImpCode); 
	} 
	
	public void cleanFormBean(long stateKey, String formCode) {   
		formManagerMap.get(getStateKeyString(formCode, stateKey)).getFormBean().clear();
	}
   
	// -- Catalog
	public Map<String, String> getFormCatalog(long stateKey, String formCode, String sourceElementImpCode) {
		return formManagerMap.get(getStateKeyString(formCode, stateKey)).getFormCatalog(sourceElementImpCode);
	}
	
//	public String getFormCatalog(long stateKey, String formCode, String sourceElementImpCode, String key) {
//		return formManagerMap.get(getStateKeyString(formCode, stateKey)).getFormCatalog(key,sourceElementImpCode);
//	}

	/**
	 * 
	 * @param key
	 * @param val - holds the catalog filter information / selection - the catalog implementation need to parse it & the element java-script need to pass it - according the following format:
	 * 		  single selection - 'val1'
	 * 		  multiple selection - 'val1','val2'
	 * 		  all selection - ALL (the string ALL)
	 * 		  no selection - empty string or null (or the null string)
	 * 		  TODO ... > comparable -  in version 9.7 - generally it should be like the concept we designed the dataTable filter element
	 */
	public void setFormCatalog(long stateKey, String formCode, String key, String val) {
		formManagerMap.get(getStateKeyString(formCode, stateKey)).setFormCatalog(key, val);
	}
	
	public void setFormCatalog(long stateKey, String formCode, Map<String, String> map) {
		formManagerMap.get(getStateKeyString(formCode, stateKey)).setFormCatalog(map);
	}
 
	public void cleanFormCatalog(long stateKey, String formCode) {   
		formManagerMap.get(getStateKeyString(formCode, stateKey)).cleanFormCatalog(); 
	}
	
	// -- Value selection  
	public Map<String, String> getFormValue(long stateKey, String formCode) {
		return formManagerMap.get(getStateKeyString(formCode, stateKey)).getFormValue();
	}

	public String getFormValue(long stateKey, String formCode, String key) {
		return formManagerMap.get(getStateKeyString(formCode, stateKey)).getFormValue(key);
	}

	public void setFormValue(long stateKey, String formCode, String key, String val) {
		formManagerMap.get(getStateKeyString(formCode, stateKey)).setFormValue(key, val);
	}

	public void setFormValue(long stateKey, String formCode, Map<String, String> map) {
		formManagerMap.get(getStateKeyString(formCode, stateKey)).setFormValue(map);
	}
	 
	public void cleanFormValue(long stateKey, String formCode) {
		formManagerMap.get(getStateKeyString(formCode, stateKey)).getFormValue().clear();
	}
	 
	public List<Catalog> getCatalogList(long stateKey, String formCode) {
		//TODO from bean map
		return formManagerMap.get(getStateKeyString(formCode, stateKey)).getCatalogList();
	}
 
	public List<Layout> getLayoutList(long stateKey, String formCode) {
		return formManagerMap.get(getStateKeyString(formCode, stateKey)).getLayoutList();
	}
  
	public List<Element> getElementList(long stateKey, String formCode) {
		return formManagerMap.get(getStateKeyString(formCode, stateKey)).getElementList();
	}
 
//	public void setMapTree(long stateKey, String code, Map<Integer, List<Element>> mapTree) {
//		formManagerMap.get(code).setMapTree(mapTree);
//	}

	public String getSummary(long stateKey, String formCode, boolean showParam) {
		return formManagerMap.get(getStateKeyString(formCode, stateKey)).getSummary(showParam);
		
	}

	public String getFormId(long stateKey, String formCode) {
		return formManagerMap.get(getStateKeyString(formCode, stateKey)).getFormId();
	}

	public Map<Integer, List<Element>> getElementMapTree(long stateKey, String formCode) {
		return formManagerMap.get(getStateKeyString(formCode, stateKey)).getElementMapTree();
	}

	public int getCurrentLevelByElementCode(long stateKey, String formCode, String currentElementCode) {
		return formManagerMap.get(getStateKeyString(formCode, stateKey)).getCurrentLevelByElementCode(currentElementCode);
	}

	public boolean isParenteElement(long stateKey, String formCode, Element element, String currentElementCode) {
		return formManagerMap.get(getStateKeyString(formCode, stateKey)).isParenteElement(element, currentElementCode);
	}

	public String getUserId(long stateKey, String formCode) {
		return formManagerMap.get(getStateKeyString(formCode, stateKey)).getUserId();
	}

	public Element getAuthorizationElement(long stateKey, String formCode) {
		// TODO Auto-generated method stub
		return formManagerMap.get(getStateKeyString(formCode, stateKey)).getAuthorizationElement();
	}

//	public Map<String, String> getFormCatalogMap(long stateKey, String formCode) { //yp1512
//		// TODO Auto-generated method stub
//		return formManagerMap.get(getStateKeyString(formCode, stateKey)).getFormCatalogMap();
//	}

	public void cleanForm(long stateKey, String formCode) {//TODO key - check remove and size
		
		if(formManagerMap.containsKey(getStateKeyString(formCode, stateKey))) {
			this.cleanFormBean(stateKey, formCode);
			this.cleanFormCatalog(stateKey, formCode);
			this.cleanFormValue(stateKey, formCode);
			this.cleanFormParam(stateKey, formCode);
			formManagerMap.remove(getStateKeyString(formCode, stateKey));
		}
	}
	
	private String getStateKeyString(String formCode, long stateKey) {
		// TODO Auto-generated method stub
		return formCode + ((stateKey > 0l)?stateKey:"");
	}
	
	private void initNavigationStack(long stateKey) {
		if(!navigationMap.containsKey(String.valueOf(stateKey))) {
			navigationMap.put(String.valueOf(stateKey), new ArrayDeque<NavigationBean>());//init navigation stack 
		}
		
	}

	private Deque<NavigationBean> getBackNavigationStack(long stateKey) {
		//Deque<NavigationBean> backNavigationStack = new ArrayDeque<NavigationBean>();//init navigation stack 
		if(!navigationMap.containsKey(String.valueOf(stateKey))) {
			navigationMap.put(String.valueOf(stateKey), new ArrayDeque<NavigationBean>());//init navigation stack 
		}
		return navigationMap.get(String.valueOf(stateKey));
	}

	private void clearBackNavigationStack(long stateKey) {
		if(navigationMap.containsKey(String.valueOf(stateKey))) {
			navigationMap.get(String.valueOf(stateKey)).clear();
		}
		
	}
	
	public void saveNavigationStackObject(String sessionUserName, String curTabStateKey)
	{
		Deque<NavigationBean> navigationStackObj = new ArrayDeque<NavigationBean>();
		
		try {
			
			if(curTabStateKey == null || curTabStateKey.equals(""))
			{
				// get first saved value(Deque<NavigationBean>)	
				navigationStackObj = getFirstKeyValue(navigationMap);		
			}
			else
			{
				navigationStackObj = getBackNavigationStack(Long.parseLong(curTabStateKey));
			}
			if(navigationStackObj != null)
			{
				byte[] byteArrayObject = getByteArrayObject(navigationStackObj);
				
				if(byteArrayObject != null)
				{
					String fileID = uploadFileDao.saveByteArrayAsBlob(byteArrayObject, "breadcrumbs.serializedObject", "breadcrumbs.serializedObject");
					if(!fileID.equals(""))
					{
						uploadFileDao.updateUserBreadcrumbLink(sessionUserName, fileID);
					}
				}
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
	}
	
	/* Get the first element of a map. A map doesn't guarantee the insertion order */
	private <K,E> E getFirstKeyValue(Map<K,E> map)
	{
        E value = null;
        try {
			if(map != null && map.size() > 0)
			{
			    Map.Entry<K,E> entry =  map.entrySet().iterator().next();
			    if(entry != null)
			    {
			        value = entry.getValue();
			        //System.out.println(entry.getKey()+": "+entry.getValue());
			    }
			}
		} catch (Exception e) 
        {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
        return  value;
    }
	
	public String restoreURLFromLastSavedBreadcrumb(String userID, long stateKey)
	{
		String url = "";
		
		try {
			Deque<NavigationBean> navigationBeanRestored = getNavigationStackObject(userID, stateKey);
			if(navigationBeanRestored.size() > 0)
			{
				//System.out.println("restored navigation been: ");
				for(NavigationBean navigationBean: navigationBeanRestored) {
					//System.out.println("before: " + navigationBean.toString());
					String oldStateKey = String.valueOf(navigationBean.getStateKey());						
					String _url = navigationBean.getUrl().replace(oldStateKey, String.valueOf(stateKey)); 
					navigationBean.setUrl(_url);
					navigationBean.setStateKey(stateKey);
					//System.out.println("after: " + navigationBean.toString());
				}
				navigationMap.replace(String.valueOf(stateKey), navigationBeanRestored);
				NavigationBean restoredBean = navigationBeanRestored.peek(); // get last pushed been from stack
				url = restoredBean.getUrl();
			}
			else
			{
				url = navigationMap.get(String.valueOf(stateKey)).peek().getUrl();
			}
			//update BreadCrumbHtml according to stack
	        setBreadCrumbHtml(stateKey, toStringBreadCrumb(stateKey,false));
		} 
		catch (Exception e) 
		{
			url = navigationMap.get(String.valueOf(stateKey)).peek().getUrl();
			generalUtilLogger.logWrite(e);
			e.printStackTrace();			
		}
		return url;
	}
	
	public byte[] getByteArrayFromBlobWrapper(String fileID) {
		byte[] byteArrayObject = null;
		try {
			byteArrayObject = uploadFileDao.getByteArrayFromBlob(fileID);
		} catch (Exception e) {
			// Do Nothing
		}
		return byteArrayObject;
	}
	
	@SuppressWarnings("unchecked")
	private Deque<NavigationBean> getNavigationStackObject(String userID, long stateKey)
	{				
		Deque<NavigationBean> navigationBeanRestored = new ArrayDeque<NavigationBean>(); 
		try 
		{
			String fileID = uploadFileDao.getUserBreadcrumbLink(userID);
			if(!fileID.equals(""))
			{
				byte[] byteArrayObject = uploadFileDao.getByteArrayFromBlob(fileID);
				
				if(byteArrayObject != null)
				{
					navigationBeanRestored = (Deque<NavigationBean>)getJavaObject(byteArrayObject);					
				}
			}
		} 
		catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return navigationBeanRestored;
	}
	
	private byte[] getByteArrayObject(Object originalObject)
	{
	    
        byte[] byteArrayObject = null;
        try {
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(originalObject);
            
            oos.close();
            bos.close();
            byteArrayObject = bos.toByteArray();
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
            generalUtilLogger.logWrite(e);
            byteArrayObject = null;
        }
        return byteArrayObject;
    }
	
	private Object getJavaObject(byte[] convertObject)
	{
		Object javaObject;       
        ByteArrayInputStream bais;
        ObjectInputStream ins;
        try 
        {
        
	        bais = new ByteArrayInputStream(convertObject);	        
	        ins = new ObjectInputStream(bais);
	        javaObject = ins.readObject();	        
	        ins.close();

        }
        catch (Exception e) 
        {
        	e.printStackTrace();
        	generalUtilLogger.logWrite(e);
        	javaObject = new Object();
        }
        return javaObject;
}
	
	public void pushIntoBackNavigationStack(long stateKey, String formId,String formCode, String tab, String info, String url,String userId){
        boolean isExists = false;
        boolean isNavigationIgnore = false;
        initNavigationStack(stateKey);
        try {
			List<Form> formList = formDao.getFormInfoLookup(formCode, "%", true);
			Deque<NavigationBean> navigationBeanArray = getBackNavigationStack(stateKey);
			
			//check if the form is in the menu
			
			//workaround for forms that aren't created through FormBuilder, like: Reports screen 
			if(formList.isEmpty())
			{
				clearBackNavigationStack(stateKey);
			}
			else
			{
				isNavigationIgnore = isNavigationIgnore(formList);
				if(isNavigationClear(formList))
				{
					clearBackNavigationStack(stateKey);
				} 
	      
				if(!formList.isEmpty() && !isNavigationIgnore(formList)) {
				    //if(backNavigationStack.isEmpty()||!formList.get(0).getIgnoreNav().equals("0"))
				    if(navigationBeanArray.isEmpty())
				    {
						if (!formCode.equals("Main")) {//Displays home page and current page after refresh
			               navigationBeanArray.push(new NavigationBean(stateKey, "-1", "Main", "", "",
									"init.request?formCode=Main&formId=-1&userId="+userId+"&stateKey=" + stateKey));
						}
				    	navigationBeanArray.push(new NavigationBean(stateKey, formId, formCode, "", "", url));            
				    } 
				    else {
				        if(!navigationBeanArray.peek().getFormCode().equals(formCode)) { //the last one isn't this
				             //check if visited in this url in past 
				            for(NavigationBean NavigationBean: navigationBeanArray) {
				                //if(NavigationBean.getUrl().equals(url))
				            	if(NavigationBean.getFormCode().equals(formCode))
				                { 
				                    isExists=true;
				                    break;
				                } 
				            }
				            
				            //if visited (the url exists in the stack) - pop to remove circle (comment the code if you want circles)
				            if(isExists) {  
				               //while(!(navigationBeanArray.pop().getUrl()).equals(url)) {}
				            	while(!(navigationBeanArray.pop().getFormCode()).equals(formCode)) {}
				            }
				            
				            // we always push the last formcode
				            navigationBeanArray.push(new NavigationBean(stateKey, formId, formCode, "", "", url));
				            
				        } else { // yp 14052018 - replace it with the new one
				        	if(formId != null && !navigationBeanArray.peek().getFormId().equals(formId)) {
				        		navigationBeanArray.pop();
				        		navigationBeanArray.push(new NavigationBean(stateKey, formId, formCode, "", "", url));
				        	}
				        }
				    }
				} 
			}	
		} 
        catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			clearBackNavigationStack(stateKey);
		}
        
        //update BreadCrumbHtml according to stack
        setBreadCrumbHtml(stateKey, toStringBreadCrumb(stateKey,isNavigationIgnore));
    }
	
	private void setBreadCrumbHtml(long stateKey, String stringBreadCrumb) {
		// TODO Auto-generated method stub
		setSessionAttr(stateKey, "breadcrumb", stringBreadCrumb);
	}
	
	public String popFromBackNavigationStack(long stateKey, HttpServletRequest request, String formCode,String formCode_request) {
        try {
        	Deque<NavigationBean> backNavigationStack = getBackNavigationStack(stateKey);
        	// home page on empty
            if (backNavigationStack.isEmpty()) {
                HttpSession session = request.getSession();
                return String.valueOf("../" + session.getAttribute("homePage"));
            }
            
            List<Form> formList = formDao.getFormInfoLookup(formCode, "%", true);
            List<Form> formCode_requestList = formDao.getFormInfoLookup(formCode_request, "%", true);
            boolean isNavigationIgnore = isNavigationIgnore(formList);
            boolean isRequestNavigationIgnore = isNavigationIgnore(formCode_requestList);//checking whether the form from which we navigate is marked as "ignore navigation" (in which case it does not appear in the stack and no pop is required)
            boolean doPop = !formList.isEmpty() && !isNavigationIgnore; //check just to be on the safe side (ignored aren't in the stack)
                     
        	boolean oneStepBackOnClose = true;
			while (!backNavigationStack.element().getFormCode().equals(formCode) && doPop) //kd 27112017 fixed bug, happened when click on more then 1 back (Main> Project> SubProject) moved only on 0ne step back ('Main> Project' instead 'Main')
        	{
				backNavigationStack.pop();
        		oneStepBackOnClose = false;
        	}
        	
        	if (oneStepBackOnClose && doPop)
        	{
        		if(!isRequestNavigationIgnore){
        			backNavigationStack.pop();
        		}
        	}
        	
        	if(backNavigationStack.isEmpty()) {
        		HttpSession session = request.getSession();
        		return String.valueOf("../" + session.getAttribute("homePage"));
        	}
            
            setBreadCrumbHtml(stateKey, toStringBreadCrumb(stateKey,isNavigationIgnore));
            String url = backNavigationStack.peek().getUrl();
            return url + "&isback=1";
            
        } catch (Exception e) {
//            logger.error("popFromBackNavigationStack outter exception: \n");
            generalUtilLogger.logWrite(e);
            try {
                HttpSession session = request.getSession();
                return String.valueOf("../" + session.getAttribute("homePage"));
            } catch (Exception ex) {
                logger.error("popFromBackNavigationStack inner exception: \n");
                generalUtilLogger.logWrite(ex);
                return request.getContextPath() + "/";
            }
        }
    }
	
	public String toStringBreadCrumb(long stateKey,boolean isNavigationIgnore_lastForm)
	{
        String breadcrumbString = "";
        Deque<NavigationBean> backNavigationStack = getBackNavigationStack(stateKey);        
        int backNavigationStackSize = backNavigationStack.size();
        
        if (backNavigationStackSize == 1) {
            return breadcrumbString;
        } 
        else 
        {        	
        	String dropdownBreadcrumbString = "";
        	String dropdownBreadcrumbDiv = "";
        	int counter = 0;
        	String arrowRightIcon = "<span><img src=\"../skylineFormWebapp/images/arrow_right.png\" class=\"arrow_right\"  style=\"margin-bottom: 2px;margin-left: 6px;\"> </span> \n";

        	for (NavigationBean NavigationBean : backNavigationStack) 
            {
        		if (backNavigationStack.peek().getFormCode().equals(NavigationBean.getFormCode())) 
                {
        			if(isNavigationIgnore_lastForm){//Fixed a bug that happened when the last form is marked as "ignore .." so the last one in the breadcrumb stack could not be clicked.
        				breadcrumbString = "<a href='#' onClick=\"confirmWithOutSaveLink(doBack,this)\" class=\"breadcrumb_link\" id=\""
	                            + NavigationBean.getFormCode() + "link\" name=\"" + NavigationBean.getFormCode() + "\" >"
	                            + generalUtil.getSpringMessagesByKey(NavigationBean.getFormCode(), "")
	                            + getCustomAddition(NavigationBean.getFormCode(),NavigationBean.getFormId())//TODO:transfer the hard coded to a customer package
	                            + "</a>";
        			}else{
        				breadcrumbString = "<a href='#' style=\"margin-left: 5px; font-size: 10pt;cursor:default;\">"
                            + generalUtil.getSpringMessagesByKey(NavigationBean.getFormCode(), "")
                            + getCustomAddition(NavigationBean.getFormCode(),NavigationBean.getFormId())//TODO:transfer the hard coded to a customer package
                            + "</a>";
                    }
                } 
                else 
                {
                    if(NavigationBean.getFormCode().equals("Main"))//TODO:transfer the hard coded to a customer package
                    {
                    	if(!dropdownBreadcrumbString.equals(""))
                    	{
	                    	/* dropdownBreadcrumbDiv - hidden div to hold rest of(not to display) links */
                    		dropdownBreadcrumbDiv = arrowRightIcon
	                    							+ "<div class=\"breadcrumb-dropdown\"> \n"
	                    							+"<img src=\"../skylineFormWebapp/images/three_dots.png\" class=\"three_dots\"  style=\"cursor: pointer;margin-bottom: 2px;margin-left: 5px;\"\n"
	                    							+ "    onClick=\"openBreadcrumbDropdown()\" > \n"
	                    							+ "<div id=\"breadcrumbDropdownDiv\" class=\"breadcrumb-dropdown-content\"> \n"
	                    							+	dropdownBreadcrumbString
													+ "</div> \n"
													+ "</div>";
                    	}
                    	breadcrumbString = "<img src=\"../skylineFormWebapp/images/home-icon.png\" class=\"home_icon\"  style=\"cursor: pointer;margin-bottom: 5px;\""
                    						+ " onClick=\"confirmWithOutSaveLink(doBack,this)\" "
                    						+ "id=\""+ NavigationBean.getFormCode() + "link\" name=\"" + NavigationBean.getFormCode() + "\""
                    						+ ">"
                    						
                    						+ dropdownBreadcrumbDiv
                    						+ arrowRightIcon
                    						+ breadcrumbString;
                    }
                    else
                    {
	                	String hrefStr = "<a href='#' onClick=\"confirmWithOutSaveLink(doBack,this)\" class=\"breadcrumb_link\" id=\""
			                            + NavigationBean.getFormCode() + "link\" name=\"" + NavigationBean.getFormCode() + "\" >"
			                            + generalUtil.getSpringMessagesByKey(NavigationBean.getFormCode(), "")
			                            + getCustomAddition(NavigationBean.getFormCode(),NavigationBean.getFormId())//TODO:transfer the hard coded to a customer package
			                            + "</a>"
			                            + arrowRightIcon;
	                	if(counter >= breadcrumbDisplayedValuesNum)
	                	{
                    		dropdownBreadcrumbString = hrefStr + dropdownBreadcrumbString;
	                	}
	                	else
	                	{
	                    	breadcrumbString = hrefStr + breadcrumbString;
	                	}
                    }
                }
                counter++;
            }
        }
        return breadcrumbString;
    }
	
	private String getCustomAddition(String formCode, String formId) {
		String toReturn = "";
		if(formCode.startsWith("Step")){
			toReturn = " "+formDao.getFromInfoLookup("Step", LookupType.ID, formId, "FORMNUMBERID");
		}
		return toReturn;
	}

	private boolean isNavigationIgnore(List<Form> formList) {
		//ignore "pop-ups" and we ignore navigation in main (cross save) forms (this made in the form configuration -> ...getIgnoreNav().equals("1"))
		boolean toReturn = false;
		toReturn =  formList.get(0).getFormType().equals(FormType.MAINTENANCE.getTypeName())
				|| formList.get(0).getFormType().equals(FormType.SELECT.getTypeName())
				|| formList.get(0).getFormType().equals(FormType.SMARTSEARCH.getTypeName())
				|| formList.get(0).getFormType().equals(FormType.ATTACHMENT.getTypeName())
				|| formList.get(0).getFormType().equals(FormType.REF.getTypeName())
				|| formList.get(0).getFormType().equals(FormType.REPORT.getTypeName())
				|| formList.get(0).getIgnoreNav().equals("1"); 
		return toReturn;
	}

	private boolean isNavigationClear(List<Form> formList) {
		// clear
		return formList.get(0).getFormCode().equals("Main");
	}
	
	public String getSessionAttr(long stateKey, String key) {
		String toReturn = "";
		if(sessionAttrMap.containsKey(String.valueOf(stateKey))) {
			toReturn = sessionAttrMap.get(String.valueOf(stateKey)).get(key);
		}
		return toReturn;
	}
	
	public void setSessionAttr(long stateKey, String key, String val) {
		if(!sessionAttrMap.containsKey(String.valueOf(stateKey))) {
			sessionAttrMap.put(String.valueOf(stateKey), new HashMap<String,String>());
		}
		sessionAttrMap.get(String.valueOf(stateKey)).put(key, val);
	}

	public List<String> getFormCodeTransactionList(String formId, String formCode) {
		// TODO Auto-generated method stub
		return formTempDataMap.getFormCodeTransactionList(formId, formCode);
	}

	public boolean isOpenTransaction(String formId) {
		// TODO Auto-generated method stub
		return formTempDataMap.isOpenTransaction(formId);
	}

	public boolean openTransaction(long stateKey, String formCode, String userId, String formId) {
		boolean toReturn = false;
		Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
		if (FormType.valueOf(form.getFormType()) == FormType.STRUCT
				|| FormType.valueOf(form.getFormType()) == FormType.INVITEM
				|| formCode.equals("MaterialFunction")) {
			formTempDataMap.openTransaction(formId, formCode);
			toReturn = true;
		}
		return toReturn;
	}

	public String checkAndReturnSessionId(String formCode, String parentId) {
		// TODO Auto-generated method stub
		List<Form> formList = formDao.getFormInfoLookup(formCode, "%", true);
		Form form = formList.get(0);
		return formTempDataMap.checkAndReturnSessionId(form, parentId);
	}

	public String getWherePartForTmpData(String formCode, String parentId) {
		// TODO Auto-generated method stub
		return formDao.getWherePartForTmpData(getSessionId(parentId), formCode, parentId);
	}

	public String getWherePartForTmpDataByFormId(String formCode, String parentId) {
		// TODO Auto-generated method stub
		return formDao.getWherePartForTmpDataByFormId(getSessionId(parentId), formCode, parentId);
	}
	
	public String getSessionId(String formId) {
		// TODO Auto-generated method stub
		return formTempDataMap.getSessionId(formId);
	}

	public void closeTransaction(String formId) {
		// TODO Auto-generated method stub
		formTempDataMap.closeTransaction(formId);
	}

	public String getMainMenuHtml(long stateKey) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public FormTempData getFormTempDataMap() {
		return formTempDataMap; 
	}
}
