package com.skyline.form.service;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.BeanType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.StateLink;
import com.skyline.form.bean.WorkFlow;
import com.skyline.form.bean.WorkflowType;
import com.skyline.general.bean.DataTableParamModel;

//import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
//import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;

@Service
@PropertySource("classpath:app.properties")
public class GeneralUtil {
	 
	@Value("${wfPath}")
	private String wfPath;
	
	@Value("${conversionDateFormat}")
	private String conversionDateFormat; // used in: to_date/to_char db select's query date format and with SimpleDateFormat
	
	@Value("${selectDateQueryDateFormat}")
	private String selectDateQueryDateFormat; // we get this date format when using DB select query with DATE type column
	
	@Value("${userDateFormatServer}")
	private String userDateFormatServer; // user date format server side
	
	@Value("${userDateFormatClient}")
	private String userDateFormatClient; // usually used for comparison and validation with momentJS
	
	@Value("${datepickerFormat}")
	private String datepickerFormat; // format of datepicker element //datepickerFormat=dd/MM/yy example: 03/May/2017
	
	@Value("${savedConventionDbDateFormat}")
	private String savedConventionDbDateFormat; // date convention format that saved in the DB
	
	@Autowired
	private SpringMessages springMessages;
	
//	@Autowired
//	private GeneralDao generalDao;
	 
	@Autowired
	protected GeneralUtilLogger generalUtilLogger;
	
	@Autowired
	private GeneralUtilFavorite generalUtilFavorite;
	
	@Value("${precision}")
	private String precision; // precision
	
	@Autowired
	private Environment env;
	
	private ScriptEngine engine;

	private static final Logger logger = LoggerFactory.getLogger(GeneralUtil.class);
	
	public GeneralUtil() {
		ScriptEngineManager factory = new ScriptEngineManager();
		this.engine = factory.getEngineByName("JavaScript");
	}

	
	public String getNull(String str) {
		if (str == null || str.trim().equalsIgnoreCase("null")) {
			return "";
		}

		return str;
	}

	
	public String getNull(String str, String defaultString) {
		if (str == null || str.equals("null")) {
			return (defaultString != null) ? defaultString : "";
		}

		return str;
	}

	
	public String getEmpty(String str, String defaultString) {
		if (getNull(str).equals("")) {
			return defaultString;
		}
		return str;
	}   
	  
	public Map<String,Map<String,String>> jsonStringToMapofMaps(String json)
	{

		
		 Map<String, Map<String,String>> map = new HashMap<String,Map<String,String>>();
	       
	        JSONObject jObject = new JSONObject(json);
	        Iterator<?> keys = jObject.keys();

	        while( keys.hasNext() ){
	            String key = (String)keys.next();
	            JSONObject nestedjObject = jObject.getJSONObject(key);
	          Iterator<?> nestedkeys = nestedjObject.keys();
	          Map<String,String> valusMap = new HashMap<String,String>();
	          while( nestedkeys.hasNext() ){
	                String nestedKey = (String)nestedkeys.next();
		            String nestedValue = nestedjObject.getString(nestedKey); 
		            valusMap.put(nestedKey, nestedValue);
	          }
	            map.put(key,valusMap);
	        }
	       
			return map;
	        
	} 
	public Map<String,List<String>> jsonArrayStringToMapWithList(String json)
	{

		
		
	       
		 Map<String, List<String>> map = new HashMap<String,List<String>>();
	       
	        JSONObject jObject = new JSONObject(json);
	        Iterator<?> keys = jObject.keys();
	       

	        while( keys.hasNext() ){
	            String key = (String)keys.next();
	            JSONArray jArray = jObject.getJSONArray(key);
	            if (jArray != null) { 
	            	 List<String> listdata=new ArrayList<String>();
	               for (int i=0;i<jArray.length();i++){ 
	                listdata.add(jArray.getString(i));
	               } 
	               map.put(key,listdata);
	            } 
	      
	          
	          }
	       
			return map;
	        
	} 
	public Map<String,List<String>> jsonStringToMapList(String json)
	{

		
		 Map<String, List<String>> map = new HashMap<String,List<String>>();
	       
	        JSONObject jObject = new JSONObject(json);
	        Iterator<?> keys = jObject.keys();

	        while( keys.hasNext() ){
	            String key = (String)keys.next();
	            String nestedList = jObject.getString(key);
	            List<String> items = Arrays.asList(nestedList.split("\\s*,\\s*"));
	            map.put(key,items);
	          }
	          
	       
	       
			return map;
	        
	} 
	
	public String mapToString(String title, Map<String, String> map) {
		StringBuilder toReturn = new StringBuilder(title);
		for (Map.Entry<String, String> entry : map.entrySet()) {
			toReturn.append("\nid: " + entry.getKey() + ", value: " + entry.getValue());
		}
		return toReturn.toString();
	} 
	
	public String checkIdExistInColumnNameforExpData(String elementId, String column) {
		String c = column;
		c = column.replace(" ", "").toLowerCase();
		if (c.contains("experiment"))
			c.replace("experiment", "");
		else if (c.contains("exp"))
			c.replace("exp", "");

		String id = elementId.toLowerCase();
		if (id.contains("experiment"))
			id.replace("experiment", "");
		else if (id.contains("exp"))
			id.replace("exp", "");

		if (c.contains(id))
			return column;
		else
			return null;
	}
	
	public String mapToJson(Map<String, String> map) { 
		return (new JSONObject(map)).toString();
	} 
	
	public JSONObject mapToJsonObject(Map<String, String> map) { 
		return (new JSONObject(map));
	} 
	
	public String listOfMapsToJson(List<Map<String, Object>> list)
	{       
	    return new JSONArray(list).toString();
	}
	public String MapOfMapsToJson(Map<String,Map<String, String>> map)
	{  
		return (new JSONObject(map)).toString();
		/*JSONObject stepsjson =new JSONObject();
		for(Map.Entry<String, Map<String, String>> m: map.entrySet()) {
		   
		     for(Map.Entry<String, String> m1: m.getValue().entrySet()) {
		    	 stepsjson.put(m.getKey(), m.getValue());
		     }           
			}
			
			
	    return new stepsjson.toString();
	    */
	}
	public String replaceLast(String mainString, String subString, String replacement) {
		int index = mainString.lastIndexOf(subString);
		if (index == -1) {
			return mainString;
		}
		return mainString.substring(0, index) + replacement + mainString.substring(index + subString.length());
	}

	
	public List<DataBean> StringToList(String str) {
		List<DataBean> toReturn = new ArrayList<DataBean>();
		toReturn.add(new DataBean("", str, BeanType.NA, ""));
		return toReturn;
	}
	
	public List<DataBean> StringToList(String str, String info) {
		List<DataBean> toReturn = new ArrayList<DataBean>();
		toReturn.add(new DataBean("", str, BeanType.NA, info));
		return toReturn;
	}
   
	public List<String> getFilesNames(String path) {
		List<String> files = new ArrayList<String>();
		try {
			File folder = new File(path);
			File[] listOfFiles = folder.listFiles();
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					files.add(listOfFiles[i].getName());
				}
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
			files = null;
		}
		return files;
	}

	
	public int getUserId(HttpServletRequest request) {
		int UserId = getUserId_(request);
		request.setAttribute("userId",UserId);
		return UserId;
	}
	
	
	public void getUserName(HttpServletRequest request) {
		String userName = getUserName_(request);
		request.setAttribute("userName",userName);		
	}

	
	public void canEdit(HttpServletRequest request) {
		if (canEdit_(request)) {
			request.setAttribute("canEditCatalog", "yes");
		} else {
			request.setAttribute("canEditCatalog", "no");
		}
	}
	
	private boolean canEdit_(HttpServletRequest request) {		
		HttpSession session = request.getSession();
		if (session.getAttribute("userName") != null) {
			String userName = (String) session.getAttribute("userName");
			if (userName.equalsIgnoreCase("Admin") || userName.equalsIgnoreCase("system"))
				return true;
		}
		return false;
	}

	
	private int getUserId_(HttpServletRequest request) {		
		HttpSession session = request.getSession();
		if (session.getAttribute("userId") != null) {
			return Integer.valueOf((String)session.getAttribute("userId"));
		}
		return -1;
	}
	
	
	private String getUserName_(HttpServletRequest request) {		
		HttpSession session = request.getSession();
		if (session.getAttribute("userName") != null) {
			return (String) session.getAttribute("userName");			
		}
		return "";
	} 

	/**
	 * Select Date Query Date Format (we get this date format when using DB select query with DATE type column)
	 */
	
	public String getSelectDateQueryDateFormat() {
		return selectDateQueryDateFormat;
	}
	
	private static final String[] dateFormats = { 
            "yyyy-MM-dd'T'HH:mm:ss'Z'",   "yyyy-MM-dd'T'HH:mm:ssZ",
            "yyyy-MM-dd'T'HH:mm:ss",      "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "yyyy-MM-dd HH:mm:ss", 
            "MM/dd/yyyy HH:mm:ss",        "MM/dd/yyyy'T'HH:mm:ss.SSS'Z'", 
            "MM/dd/yyyy'T'HH:mm:ss.SSSZ", "MM/dd/yyyy'T'HH:mm:ss.SSS", 
            "MM/dd/yyyy'T'HH:mm:ssZ",     "MM/dd/yyyy'T'HH:mm:ss", 
            "yyyy:MM:dd HH:mm:ss",        "yyyyMMdd", };
	
	public String getDateFormat(String date){
		if (date != null) {
            for (String parse : dateFormats) {
                SimpleDateFormat sdf = new SimpleDateFormat(parse);
                try {
                    sdf.parse(date);
                    return parse;
                } catch (Exception e) {
                	
                }
            }
        }
		return getConversionDateFormat();
	}

	/**
	 * return user date format server side
	 * TODO: should be specified for each user
	 */
	
	public String getUserDateFormatServer() {
		return userDateFormatServer;
	}
	
	/**
	 *  used in: to_date/to_char db select's query date format and with SimpleDateFormat
	 */
	
	public String getConversionDateFormat() {
		return conversionDateFormat;
	}
	
	
	/**
	 *  used in: to_date/to_char db select's query date format with time and with SimpleDateFormat
	 */
	public String getConversionDateTimeFormat() {
		return conversionDateFormat + "  HH24:MI";
	}
	
	/**
	 *  used in: to_date/to_char db select's query date format with time and with SimpleDateFormat
	 */
	public String getConversionDateTimeSecondsFormat() {
		return conversionDateFormat + "  HH24:MI:SS";
	}
	
	/**
	 * get datepicker date format
	 */
	
	public String getDatepickerFormat() {
		return datepickerFormat;
	}
	
	/**
	 * get Client Dates' Format
	 * return  userDateFormatClient: usually used for comparison and validation with momentJS
	 *   	   savedConventionDbDateFormat: date convention format that saved in the DB
	 *         datepickerFormat: format of datepicker element
	 * return value example: "DD/MMM/YYYY;DD/MM/YYYY;dd/MM/yy"
	 */
	
	public String getClientDatesFormat() {
		return userDateFormatClient + ";" + savedConventionDbDateFormat + ";" + datepickerFormat;
	}
	
	
	public String removeSurroundedUpperComma(String val) {
		String valNoComma = val;
		if(getNull(val).startsWith("'") && getNull(val).endsWith("'")) {
			valNoComma = val.substring(1, val.length() - 1);
		}
		return valNoComma;
	}
	
	
	public String removeObjectFromJsonSchema(String schema, String objectName) {
		try {
			String str = schema;
			Pattern word = Pattern.compile(objectName);
			Matcher match = word.matcher(str);
			int cuantityOfStringInSchema = 1; // use this if need remove more then one section. Have to add to dependency and import org.apache.commons.lang.StringUtils; //StringUtils.countMatches(str, string);
			int[] startIndex = new int[cuantityOfStringInSchema];
			int[] endIndex = new int[cuantityOfStringInSchema];
			int indexOfFoundQuantity = 0, correctIndex = 0;
			String firstForFind = "\\{";
			char leftForFind = '{', rightForFind = '}';

			while (match.find()) {
				int indexString = match.start();
				startIndex[indexOfFoundQuantity] = indexString;
				String str2 = str.substring(indexString);
				Pattern word2 = Pattern.compile(firstForFind);
				Matcher match2 = word2.matcher(str2);

				if (match2.find()) {
					int startIndex2 = match2.start();
					int n = 1, i;
					for (i = startIndex2 + 1; n > 0; i++) {
						if (str2.charAt(i) == leftForFind) {
							n++;
						}
						if (str2.charAt(i) == rightForFind) {
							n--;
						}
					}
					correctIndex = 0;
					if (str2.length() > i) {
						i++;
					} else {
						correctIndex = str.lastIndexOf(',', startIndex[indexOfFoundQuantity]);
					}
					endIndex[indexOfFoundQuantity] = startIndex[indexOfFoundQuantity] + i;
					if (correctIndex > 0) {
						startIndex[indexOfFoundQuantity] = correctIndex;
					}
					indexOfFoundQuantity++;
				}
			}
			for (int j = 0; j < cuantityOfStringInSchema; j++) {
				if (j != 0) {
					startIndex[j] = startIndex[j] - (endIndex[j - 1] - startIndex[j - 1]);
					endIndex[j] = endIndex[j] - (endIndex[j - 1] - startIndex[j - 1]);
				}
				String cut = str.substring(startIndex[j], endIndex[j]);
				str = str.replace(cut, "");
			}
			return str;
		} catch (Exception e) {
			return schema;
		} 
	}

	
	public int getNullInt(String number, int defaultInt) {
		int toReturn = defaultInt;
		try {
			toReturn = Integer.valueOf(getEmpty(number,String.valueOf(defaultInt)));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block 
		}
		return toReturn;
	}
	
	
	public String getMd5(String SumpleString) {
		//Take a string and return its md5 hash as a hex digit string
		//Ilia Bulaevski	
		String result;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(SumpleString.getBytes());
			byte[] passwordHash = md.digest();

			result = asHex(passwordHash);
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
			return "";
		}
		return result;
	}
	
	private String asHex(byte hash[]) {
		//Private function to turn md5 result to 32 hex-digit string
		//Ilia Bulaevski
		StringBuffer buf = new StringBuffer(hash.length * 2);
		int i;

		for (i = 0; i < hash.length; i++) {
			if (((int) hash[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString((int) hash[i] & 0xff, 16));
		}

		return buf.toString();
	} // End asHex
	
	
	public boolean getNullBoolean(String value, boolean defaultVal) {
		// TODO Auto-generated method stub
		return getNullBoolean(value, defaultVal, "");
	}

	
	public boolean getNullBoolean(String value, boolean defaultBoolen, String logInfo) {
		boolean toReturn = defaultBoolen;
		try {
			if (!getNull(value).equals("")) {
				if(value.toLowerCase().equals("true") ) {
					toReturn = true;
				} else if(value.toLowerCase().equals("false")) {
					toReturn = false;
				} else {
//					ScriptEngineManager factory = new ScriptEngineManager();
//					ScriptEngine engine = factory.getEngineByName("JavaScript");
					toReturn = (Boolean) engine.eval(value);
					if(!getNull(logInfo).equals("")) {
						logger.debug(logInfo + ": boolean expression: " + value + ", defaultBoolen=" + defaultBoolen + ", returnVal=" + toReturn);
					}
				}
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
//			System.out.println("getNullBoolean error on value=" + value + ", e=" + e);
		}
		return toReturn;
	}
	
	public String getJsonValById (JSONObject jsonObject, String jsonStr, String id) {
		String value = "";
		try {
//			JSONObject jsonObject = new JSONObject(jsonStr);
			if (!(jsonObject.isNull(id))) {			
				if (jsonObject.get(id) instanceof JSONArray)
				{
					value =	jsonObject.getJSONArray(id).toString();				
				} else {
					value = jsonObject.getString(id);
				}
			}
		} catch (Exception e) {			
			generalUtilLogger.logWrite(e);
			logger.error("error in jsonStr:" + jsonObject);
			e.printStackTrace();
		} 
		return value;
	}

	
	public String getJsonValById (String jsonStr, String id) {
		String value = "";
		try {
			if(!jsonStr.isEmpty() && jsonStr.trim().startsWith("{")) {
				JSONObject jsonObject = new JSONObject(jsonStr);
				if (!(jsonObject.isNull(id))) {
					if (jsonObject.get(id) instanceof JSONArray) {
						value = jsonObject.getJSONArray(id).toString();
					} else {
						value = jsonObject.getString(id);
					}
				} 
			}
		} catch (Exception e) {		
			System.out.println(jsonStr);
			generalUtilLogger.logWrite(e);
			logger.error("error in jsonStr:" + jsonStr);
			e.printStackTrace();
		} 
		return value;
	}
	
	/** 
	 * update value of a given key in JSON object
	 * return updated JSON object
	 * 
	 **/
	public String updateJsonValById(String jsonStr, String key, String newValue) 
	{
		String updatedJsonObject = "";
		try 
		{
			if(!jsonStr.isEmpty() && jsonStr.trim().startsWith("{")) {
				JSONObject jsonObject = new JSONObject(jsonStr);
				if (!(jsonObject.isNull(key))) 
				{			
					jsonObject.put(key, newValue);
					updatedJsonObject = jsonObject.toString();
				}
				else
				{
					updatedJsonObject = jsonStr;
				}
			}
		} 
		catch (Exception e) 
		{			
//			updatedJsonObject = jsonStr;
//			generalUtilLogger.logWrite(e);
//			logger.error("error in jsonStr:" + jsonStr);
//			e.printStackTrace();
		} 
		return updatedJsonObject;
	}
	
	public boolean checkIfKeyExistsInJson(String jsonStr, String id)
	{
		boolean isExist = false;
		try 
		{
			if(!jsonStr.isEmpty() && jsonStr.trim().startsWith("{")) {
				JSONObject jsonObject = new JSONObject(jsonStr);
				if(jsonObject.has(id))
				{
					isExist = true;
				}
			}
		} 
		catch (Exception e) 
		{			
			//do nothing
		} 
		return isExist;
	}
	
	public String surroundUpperCommaOnVal(String sessionId) {
		String toReturn = sessionId;
		if(sessionId != null) {
			toReturn = "'" + toReturn+ "'";
		}
		return toReturn;
	}

	
	public String getSessionUserId() {
		// Add sessionId to the map
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession();
		return (String)session.getAttribute("userId");
	}
	
	public String getSessionBrowserName() {
		// Add sessionId to the map
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession();
		return (String)session.getAttribute("browserName");
	}
	
	public String getSessionUserName() {
		// Add sessionId to the map
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession();
		return (String)session.getAttribute("userName");
	}
	
	public String getSessionScreenList() {
		// Add sessionId to the map
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession();
		return (String)session.getAttribute("menuScreen");
	}
	
	public String getSessionId() {
		// Add sessionId to the map
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpSession session = attr.getRequest().getSession();
		return session.getId();
	}
	
	public String getSessionIdNoException(String defaultOnException) {
		// Add sessionId to the map
		String sessionId = defaultOnException;
		try {
			sessionId = getSessionId();
		} catch (Exception e) {
		}
		return sessionId;
	}
	
	
//	public void setBreadCrumbHtml(String value) {		
//		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//		HttpSession session = attr.getRequest().getSession();
//		session.setAttribute("breadcrumb", value);
//	}

	
	public List<String> getWfAvailableState(String jsonFile, WorkflowType wfType, String currentState) {
		List<String> wfAvailableStates = new LinkedList<String>();
		String path = wfPath+"/"+jsonFile;
		try {
			WorkFlow workflow= deserializeJsonToObject(path,WorkFlow.class);
			if(currentState.isEmpty())//it's a new form
			{
				wfAvailableStates.add(workflow.getFirstState());//add the first state
				return wfAvailableStates;
			}
			//runs through all the links in the workflow and add the linked states of currentState to wfAvailableState
			if(workflow != null) {
				for(StateLink link:workflow.getLinkDataArray())
				{
					if(link.getFromState().equalsIgnoreCase(currentState))
					{
						wfAvailableStates.add(link.getToState());
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
			wfAvailableStates = new LinkedList<String>();
		}
		
		return wfAvailableStates;
	}
	
	public List<String> getWfPreviousAvailableState(String jsonFile, WorkflowType wfType, String currentState) {
		List<String> wfAvailableStates = new LinkedList<String>();
		String path = wfPath+"/"+jsonFile;
		try {
			WorkFlow workflow= deserializeJsonToObject(path,WorkFlow.class);
			if(currentState.isEmpty())//it's a new form
			{
				wfAvailableStates.add(workflow.getFirstState());//add the first state
				return wfAvailableStates;
			}
			//runs through all the links in the workflow and add the linked states of currentState to wfAvailableState
			for(StateLink link:workflow.getLinkDataArray())
			{
				if(link.getToState().equalsIgnoreCase(currentState))
				{
					wfAvailableStates.add(link.getFromState());
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
			wfAvailableStates = new LinkedList<String>();
		}
		
		return wfAvailableStates;
	}
	
	public String getWfFirstState(String jsonFile, WorkflowType wfType) {
		String firstState = "";
		String path = wfPath+"/"+jsonFile;
		try {
			WorkFlow workflow= deserializeJsonToObject(path,WorkFlow.class);
			firstState =  workflow.getFirstState();//add the first state
		} catch (Exception e) {
			// TODO Auto-generated catch block
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		
		return firstState;
	}
	
	public String getWfValidation(String jsonFile, String currentState,String nextState) {
		String ValidationPart = "";
		String path = wfPath+"/"+jsonFile;
		try {
			WorkFlow workflow= deserializeJsonToObject(path,WorkFlow.class);
			if(workflow != null) {
				//runs through all the links in the workflow 
				for(StateLink link:workflow.getLinkDataArray())
				{
					if(link.getFromState().equalsIgnoreCase(currentState) && link.getToState().equals(nextState))
					{
						ValidationPart= link.getValidation();
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		
		return ValidationPart;
	}

	
	public <T> T deserializeJsonToObject(String path,Class<T> className) 
	{
		String jsonString;
		T res ;
		try 
		{			
			//jsonString = new String(Files.readAllBytes(Paths.get(path)), StandardCharsets.UTF_8);
			Scanner scanner = new Scanner(new File(path));
			jsonString = scanner.useDelimiter("\\Z").next();
			scanner.close();
			
		    GsonBuilder builder = new GsonBuilder();	    
		    Gson gson = builder.create();
		    res = gson.fromJson(jsonString, className);
		}
	    catch (Exception e) {
	    	generalUtilLogger.logWrite(LevelType.INFO, "no WF found for path=" + path, "", ActivitylogType.WorkFlowGeneral, null);
//			e.printStackTrace();
			res = null;
	    }
	    return res;
	}
	
	/**
	 * 
	 * @param jsonString - json String
	 * @param className 
	 * @return Object of the className in case of exception return null
	 */
	public <T> T deserializeJsonStringToObject(String jsonString,Class<T> className) 
	{ 
		T res ;
		try 
		{			 
		    GsonBuilder builder = new GsonBuilder();	    
		    Gson gson = builder.create();
		    res = gson.fromJson(jsonString, className);
		}
	    catch (JSONException e) {
	    	generalUtilLogger.logWrite(e);
			e.printStackTrace();
			res = null;
	    } 
	    return res;
	}

	
//	public String getStatusTableName(String formCode) {
//		String statusTable;
//		String path = wfPath+"/"+formCode.toLowerCase()+"_status.json";
//		try {
//			WorkFlow workflow= deserializeJsonToObject(path,WorkFlow.class);
//			statusTable= workflow.getStatusTableName();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			statusTable="";
//		}
//		return statusTable;
//	}

	
	public String formatParamInSigns(int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			sb.append((i > 0) ? "," : "").append("?");
		}

		return sb.toString();
	} 
	
	
	public JSONObject getTableModel(List<JSONArray> arrList, final DataTableParamModel param) 
    {
        String sEcho = param.sEcho;
        int iTotalRecords; // total number of records (unfiltered)
        int iTotalDisplayRecords; //value will be set when code filters levels by keyword
        JSONObject jsonResponse = null;
         
         try 
         {
             List<JSONArray> resultsList = arrList;//data that will be shown in the table
             iTotalRecords = resultsList.size();
             List<JSONArray> currentList = new LinkedList<JSONArray>();
             
             if (param.sSearchKeyword!= null && !param.sSearchKeyword.equals(""))
             {
                   for(int i=0; i< resultsList.size(); i++)
                   {
                       JSONArray row = resultsList.get(i);
                       for(int j=0; j < row.length(); j++)
                       {
                           if(param.bSearchable[j] && row.optString(j).toLowerCase().contains(param.sSearchKeyword.toLowerCase()))
                           {
                               currentList.add(row);
                               break;
                           }
                       }
                   }
                     resultsList = currentList;
                     currentList = new LinkedList<JSONArray>(); 
             }
             
             
             
             if(resultsList.size() > 0)
             {
                 for(int i=0; i < param.sSearch.length; i++)
                 {
                     if((param.sSearch[i] != null && !param.sSearch[i].equals("")) && param.bSearchable[i])
                     {
                         for(int j=0; j< resultsList.size(); j++)
                         {
                             JSONArray row = resultsList.get(j);
                             if(row.optString(i).toLowerCase().contains(param.sSearch[i].toLowerCase()))
                             {
                                 currentList.add(row);
                             }     
                         }
                          resultsList = currentList;
                          currentList = new LinkedList<JSONArray>();  
                         //System.out.println(resultsList.toString());
                     } 
                 }  
             }
             
             iTotalDisplayRecords = resultsList.size();// number of rows that match search criterion should be returned
                        
              Collections.sort(resultsList, new Comparator<JSONArray>()
              {
                      @Override
                      public int compare(JSONArray obj1, JSONArray obj2) 
                      {
                              int result = 0;
                              String dateFormat;
                              for(int i=0; i < param.iSortingCols; i++)
                              {
                                      int sortBy = param.iSortCol[i];
                                      if(param.bSortable[sortBy])
                                      {
                                          try //string, numeric, date, double
                                          {
                                              if(param.sSortType[i] != null && !param.sSortType[i].equals(""))
                                              {
                                                  if(param.sSortType[i].equals("numeric"))
                                                  {                                                     
                                                            result = new Integer(obj1.optInt(sortBy)).compareTo(new Integer(obj2.optInt(sortBy))) *
                                                                      (param.sSortDir[i].equals("asc") ? -1 : 1);
                                                      break;
                                                  }
                                                  else if(param.sSortType[i].equals("date"))
                                                  {
                                                      dateFormat = (param.sDateFormat[i].length() == 0)? param.defaultDateFormat:param.sDateFormat[i];
                                                      if(obj1.optString(sortBy).equals("") || obj2.optString(sortBy).equals(""))
                                                      {
                                                          result = obj1.optString(sortBy).compareToIgnoreCase(obj2.optString(sortBy)) *
                                                                          (param.sSortDir[i].equals("asc") ? -1 : 1);
                                                      }
                                                      else
                                                      {
                                                          try
                                                          {
                                                              result = new SimpleDateFormat(dateFormat).parse(obj1.optString(sortBy)).
                                                                      compareTo(new SimpleDateFormat(dateFormat).parse(obj2.optString(sortBy))) *
                                                                                                      (param.sSortDir[i].equals("asc") ? -1 : 1);
                                                          }
                                                          catch (Exception e) 
                                                          {
                                                              System.out.println("BaseBL Exception:  " + e.getMessage());
                                                          }                                                     
                                                      }
                                                      break;
                                                  }
                                                  else if(param.sSortType[i].equals("double"))
                                                  {                                                      
                                                             result = new Double(obj1.optDouble(sortBy)).compareTo(new Double(obj2.optDouble(sortBy))) *
                                                                     (param.sSortDir[i].equals("asc") ? -1 : 1);
                                                      break;
                                                  }
                                                  else
                                                  {
                                                      result = obj1.optString(sortBy).compareToIgnoreCase(obj2.optString(sortBy)) *
                                                                      (param.sSortDir[i].equals("asc") ? -1 : 1);  
                                                      break;
                                                  }
                                              }
                                              else
                                              {
                                                  result = obj1.optString(sortBy).compareToIgnoreCase(obj2.optString(sortBy)) *
                                                                  (param.sSortDir[i].equals("asc") ? -1 : 1);  
                                                  break;
                                              }
                                              
                                          }
                                          catch (Exception e) 
                                          {
                                              System.out.println(e.getMessage());
                                          }
                                      }
                                      if(result!=0) {
										return result;
									} else {
										continue;
									}
                              }
                              return result;
                      }
              });
              
             /** case table use scroll iDisplayLength = -1 **/
              if(param.iDisplayLength != -1)
              {    
                  if(resultsList.size()< param.iDisplayStart + param.iDisplayLength) 
                  {
                          resultsList = resultsList.subList(param.iDisplayStart, resultsList.size());
                  } 
                  else 
                  {
                          resultsList = resultsList.subList(param.iDisplayStart, param.iDisplayStart + param.iDisplayLength);
                  } 
              }
              
             System.out.println("getTableModel(): resultsList.toString(): " + resultsList.toString()); 
             
             jsonResponse = new JSONObject();   
             /*jsonResponse.put("aaData", resultsList);
             jsonResponse.put("sEcho", sEcho);
             jsonResponse.put("iTotalRecords", iTotalRecords);
             jsonResponse.put("iTotalDisplayRecords", iTotalDisplayRecords);*/
             jsonResponse.put("data", resultsList);
             jsonResponse.put("draw", sEcho);
             jsonResponse.put("recordsTotal", iTotalRecords);
             jsonResponse.put("recordsFiltered", iTotalDisplayRecords);
             
             System.out.println("jsonResponse.toString(): " + jsonResponse.toString()); 
             }
             catch (Exception e)
             {
            	 generalUtilLogger.logWrite(e);
            	 System.out.println("GeneralUtil.getTableModel(): " + e.toString());
             }
             return jsonResponse;
    }
	
	
	public String getSpringMessages(){
		return springMessages.getMessages();
	}
	
	public String getSpringMessagesByKey(String key, String defaultValue){		
		return springMessages.getSpringMessagesByKey(key, defaultValue);
	}
	
	
	public String getSpringMessagesByKey(String key){		
		return springMessages.getSpringMessagesByKey(key, key);
	} 
	
	/**
	 * get a code of message and displays it with accepted parameters.
	 * links can be part of the message.In order to add a link, use in the following pattern:<a id='[formid]';formCode='[formCode]';val='[text to display]'></a>
	 * @param key
	 * @param errorParameterArray
	 * @param defaultValue
	 * @return
	 */
	public String getSpringMessagesByKey(String key, Object[] errorParameterArray, String defaultValue){  
		String toReturn = defaultValue;
		try { 
			String msg = getSpringMessagesByKey(key,defaultValue);
			toReturn = MessageFormat.format(msg, errorParameterArray);
			Pattern p = Pattern.compile("\\<a(.*?)\\>\\<\\/a\\>");
			Matcher m = p.matcher(toReturn);
			while(m.find())
			{
			    String param = m.group(1); 
			    System.out.println(param);
			    String[] paramParts = param.split(Pattern.quote(";"));
		    	String id = replaceLast(paramParts[0].split("=")[1].replaceFirst("'", ""),"'","");
		    	String name = replaceLast(paramParts[2].split("=")[1].replaceFirst("'", ""),"'","");
		    	String formCode = replaceLast(paramParts[1].split("=")[1].replaceFirst("'", ""),"'","");
			    toReturn = toReturn.replace("<a"+param+"></a>", "<a onclick=checkAndNavigate(['"+id+"','"+formCode+"','','false'])><span>"+name+"</span></a>");
			}
			    
		} catch (Exception e) {
			// TODO Auto-generated catch block
			generalUtilLogger.logWrite(e);
       	 	System.out.println("GeneralUtil.getSpringMessagesByKey(): " + e.toString());
		}
		return toReturn;
	} 
	
	
	public String getPrecision() {		
		return precision;
	}
	
	
	public String changeFirstChar(String name, boolean toLowerCase) {
		// TODO Auto-generated method stub
		String firstChar = getNull(name).length() > 0 ? (toLowerCase ? name.substring(0,1).toLowerCase() : name.substring(0,1).toUpperCase()) : "";
		return getNull(name).length() > 1 ? getNull( firstChar + name.substring(1)) : firstChar;
	}
	
	public boolean isSqlLike(String val, String expressionVal) {
		return !getNull(expressionVal).equals("") && val.toUpperCase().matches(getNull(expressionVal).toUpperCase().replaceAll("%", ".*"));
	}


	public Map<String, String> jsonSimpleToMap(String keyPrefix, String urlCallParam) {
		// TODO Auto-generated method stub
		 Map<String, String> toReturn = new HashMap<String,String>();
		if(!getNull(urlCallParam).equals("")) {
			
			JSONObject jObject = new JSONObject(urlCallParam.trim());
			Iterator<?> jsKeys = jObject.keys();
			
			while (jsKeys.hasNext()) {
				try {
					String jsKey = (String) jsKeys.next();
					toReturn.put(getNull(keyPrefix).toUpperCase() + "_" + jsKey, getNull((jObject.get(jsKey)).toString(),jsKey));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return toReturn;
	}


	public String listToCsv(List<String> unfilteredList) {
		// TODO Auto-generated method stub
		String toReturn = "";
		if(unfilteredList != null && unfilteredList.size() > 0) { 
			try {
				toReturn = replaceLast(unfilteredList.toString().replaceFirst("\\[", ""), "]", "").replace(", ", ",");
			} catch (Exception e) {
				toReturn = unfilteredList.toString();
			} 
		}
		return toReturn; 
	}
	public String MapOfListToJson(Map<String,List<String>> map)
	{
		
		Map<String,String> helpMap=new HashMap<String, String>();
		for(Map.Entry<String,List<String>> m: map.entrySet()) {
			helpMap.put(m.getKey(), listToCsv(m.getValue()));
		          
			}
		return mapToJson(helpMap);
	}
	public Map<String, String> stringToLnkHashMap(String keyPrefix,  String displayValuesObj, boolean isUpperCase) {
		Map<String, String> toReturn = new LinkedHashMap<String, String>();
		try {
			JSONArray jsonArr = new JSONArray(displayValuesObj);
			JSONObject map = new JSONObject();
			for (int i = 0; i < jsonArr.length(); i++){
				map = jsonArr.getJSONObject(i);
				try {
					toReturn.put( getNull(keyPrefix) + (isUpperCase ? getNull(map.getString("key")).toUpperCase() : map.getString("key")), getNull(map.getString("val")));
				} catch (JSONException ex) {
					ex.printStackTrace();
					generalUtilLogger.logWrite(ex);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toReturn;
	}


	public long getNullLong(String val) {
		long toReturn = 0l;
		if(val != null) {
			try {
				toReturn = Long.valueOf(val);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				toReturn = 0l;
			}
		}
		return toReturn;
	}
	
	public String showFormPathDisplay(String path, String delimiter) {
		// TODO Auto-generated method stub
		String toReturn = "";
		JSONObject json = new JSONObject(path);
		JSONArray pathList = json.getJSONArray("path");
		for(int i= 0;i<pathList.length();i++){
			String p = pathList.get(i).toString();
			if(i == pathList.length()-1 ){
				toReturn += getJsonValById(p,"name").split(":")[1];
				//toReturn += getJsonValById(p,"name")
			}else{
				toReturn += getJsonValById(p,"name").split(":")[1] + delimiter;
			}
		}
		return toReturn;
	}
	
	
  
//	public void preFormSaveEventHandler(String formCode, String formId, Map<String, String> elementValueMap,
//			String userId) {
//		// TODO Auto-generated method stub
//		
//	}


//	public String getInfDataStringFromCachMap(String elementName, String formCode, LookupType lookupType,
//			String lookupval) throws Exception {
//		String returnData = "";
//		String columnTofunctionName = lookupType.getTypeName().substring(0, 1).toUpperCase()
//				+  lookupType.getTypeName().substring(1).toLowerCase();
//		List<InfData> form = cachDataMap.get(formCode);
//		for (InfData infData : form) {
//			
//				if ((InfData.class.getMethod("get" + columnTofunctionName).invoke(infData)).toString()
//						.equals(lookupval)) {
//					if (elementName.toUpperCase().equals("ID") || elementName.toUpperCase().equals("NAME")) {
//						String elementNameToFunction = elementName.substring(0, 1).toUpperCase() + elementName.substring(1).toLowerCase();
//						returnData = (InfData.class.getMethod("get" + elementNameToFunction).invoke(infData)).toString();
//					} else {
//						returnData = infData.getAttributes().getString(elementName);
//					} 
//			
//
//		}
//				}
//		return returnData;
//	}
	 
	public ApplicationContext getWebApplicationContex() {
		// Add sessionId to the map
		//ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		//HttpSession session = attr.getRequest().getSession();
		//return (String)session.getAttribute("userId");
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		 HttpSession session = attr.getRequest().getSession();
		 ApplicationContext ctx =
	                WebApplicationContextUtils.
	                      getWebApplicationContext(session.getServletContext());
		 return ctx;
	}


	public String getDBTransaction() {
		String dbTransactionId = "";
		try {
			if (TransactionSynchronizationManager.isActualTransactionActive()) {
				TransactionStatus status = null;
				status = TransactionAspectSupport.currentTransactionStatus();
				dbTransactionId = String.valueOf(status.hashCode());
			}
		} catch (NoTransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dbTransactionId;
	}


	public String getFirstCsv(String csvList) {
		String toReturn = csvList;
		try {
			if(csvList != null && csvList.contains(",")) {
				toReturn = csvList.split(",")[0];
			}
		} catch (Exception e) {
			toReturn = ""; 
			// do nothing
		}
		return toReturn;
	}
	
	public String handleClob(String val) {
		String toReturn = "to_clob('" + val + "')";
		if(val != null && val.length() > 3900) {
			toReturn = breakClob(val);
		} 
		return toReturn;
	}

	private String breakClob(String val) {
		String toReturn = "";
		int index = 0;
		while (index < val.length()) {
			toReturn = "CONCAT_CLOB(" + getEmpty(toReturn, "EMPTY_CLOB()") + ",'" + val.substring(index, Math.min(index + 3900,val.length())) + "')";
		    index += 3900;
		}
		return toReturn;
	}
	
	public Map<String,String> addNewImageToMatrix(BufferedImage overlay, String type, List<BufferedImage> source) throws IOException {
        Map<String,String> returnmap = new HashMap<>();
        overlay = resize(overlay,(int)Math.round(overlay.getWidth()*0.7),(int)Math.round(overlay.getHeight()*0.7));
        int imageType = "png".equalsIgnoreCase(type) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        
		BufferedImage image = source.get(0);

        // determine image type and handle correct transparency
        int newWidth = overlay.getWidth()*10;
        int newHeight = image.getHeight()+overlay.getHeight();
        newHeight = newHeight==0?overlay.getHeight():newHeight;
        BufferedImage concatImage = new BufferedImage(newWidth, newHeight, imageType);
            
        // initializes necessary graphic properties
        Graphics2D w = (Graphics2D) concatImage.getGraphics();
        w.drawImage(image, 0, 0, null);
        // calculates the coordinate where the String is painted
        int xPos = 0;
        int yPos = image.getHeight();

        // add text watermark to the image
        w.drawImage(overlay, xPos, yPos, null);
        source.set(0, concatImage);
        
        w.dispose();
        returnmap.put("x", String.valueOf(xPos));
    	returnmap.put("y", String.valueOf(yPos));
    	return returnmap;
    }
	
	public Map<String,String> addImageToMatrix(BufferedImage overlay, String type, List<BufferedImage> source, String x, String y, int numOfMaterialinImage) throws IOException {
        Map<String,String> returnmap = new HashMap<>();
        overlay = resize(overlay,(int)Math.round(overlay.getWidth()*0.7),(int)Math.round(overlay.getHeight()*0.7));
        int imageType = "png".equalsIgnoreCase(type) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
        if(numOfMaterialinImage==0)	{
        	//ImageIO.write( overlay, "png", source );
        	source.add(overlay);
        	numOfMaterialinImage++;
        	returnmap.put("x", "0");
        	returnmap.put("y", "0");
        	return returnmap;
        }
        
		BufferedImage image = source.get(0);

        // determine image type and handle correct transparency
        int newWidth = overlay.getWidth()*10;//x!=null?image.getWidth():(image.getWidth()/overlay.getWidth()<10?image.getWidth()+overlay.getWidth():image.getWidth());
        int newHeight = y!=null?image.getHeight():(numOfMaterialinImage%10>0?image.getHeight():image.getHeight()+overlay.getHeight());
        newHeight = newHeight==0?overlay.getHeight():newHeight;//it is the first write
        BufferedImage concatImage = new BufferedImage(newWidth, newHeight, imageType);
            
        // initializes necessary graphic properties
        Graphics2D w = (Graphics2D) concatImage.getGraphics();
        w.drawImage(image, 0, 0, null);
        // calculates the coordinate where the String is painted
        int xPos = Integer.parseInt(getNull(x,String.valueOf(numOfMaterialinImage%10*overlay.getWidth())));
        int yPos = Integer.parseInt(getNull(y,String.valueOf(newHeight-overlay.getHeight())));

        // add text watermark to the image
        w.drawImage(overlay, xPos, yPos, null);
        //ImageIO.write(concatImage, type, source);
        source.set(0, concatImage);
        if(x==null && y==null){
        	numOfMaterialinImage++;
        }
        w.dispose();
        returnmap.put("x", getNull(x, String.valueOf(xPos)));
    	returnmap.put("y", getNull(y, String.valueOf(yPos)));
    	return returnmap;
    }
	
	public BufferedImage resize(BufferedImage img, int width, int height) {
        Image tmp = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }
	
	/**
	 * return the property value by name from app.properties. if not found return defaultValue 
	 * @param porpName
	 * @return
	 */
	public String getPropByName(String porpName, String defaultValue) {
		String propVal = null;
		try {
			propVal = env.getProperty(porpName);
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
		}
		
		if(propVal == null) {
			propVal = defaultValue;
		}
		return propVal;
	}
	
	public String getRequestFormId() {
		// Add sessionId to the map
		ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		String formId = attr.getRequest().getParameter("formId");
		return formId;
	}
	public Map<String,List<String>> addToList(String MapKey ,String myItem,Map<String,List<String>> map) {
		
		Map<String,List<String>>  itemsMap=new HashMap<String,List<String>>(map);
		List<String> itemsList=itemsMap.get(MapKey);
	  if(itemsList == null) {
		  itemsList=new ArrayList<String>(itemsList);
	         itemsList.add(myItem);
	         itemsMap.put(MapKey, itemsList);
	    }
	  else {  
		 
	        // add if item is not already in list
	        if(!itemsList.contains(myItem.toLowerCase())) 
	        	itemsList.add(myItem.toLowerCase());
	        itemsMap.put(MapKey, itemsList);
	  }
	
	    return itemsMap;
	}
	
	public Map<String, String> initElementValueMapByBeanList(List<DataBean> dataBeanList) {
		Map<String, String> elementValueMap = new HashMap<String, String>();
		for (DataBean dataBean : dataBeanList) {
			elementValueMap.put(dataBean.getCode(), getNull(dataBean.getVal()));
		}
		return elementValueMap;
	}

	/**
	 * It's remove trim string from blaanks in the begin and in the end
	 * and remove '(' in the begin on String and ')' in the end
	 * @param str
	 * @return String
	 */
	public String trimBrackets(String str) {
		String trimmedStr = str.trim();
		if(trimmedStr.length() > 0)
		{
			return trimmedStr.charAt(0) == '(' && trimmedStr.charAt(trimmedStr.length()-1) == ')'?trimmedStr.substring(1,trimmedStr.length()-1):trimmedStr;
		} else 
			return trimmedStr;
	}
	/* public void  wordToPdf(String inputFile,String outputFile) throws Exception {
         System.out.println("inputFile:" + inputFile + ",outputFile:"+ outputFile);
         FileInputStream in=new FileInputStream(inputFile);
         XWPFDocument document=new XWPFDocument(in);
         File outFile=new File(outputFile);
         OutputStream out=new FileOutputStream(outFile);
         PdfOptions options=null;
         PdfConverter.getInstance().convert(document,out,options);
        
       }*/

	public boolean isInteger(String d_lastCriteria) {
		boolean toReturn = false;
		try {
			toReturn = d_lastCriteria != null && d_lastCriteria.matches("-?\\d+");
		} catch (Exception e) {
			// Do nothing
		}
		return toReturn;
	}


	public String getFavoritById(String formId) {
		return generalUtilFavorite.isFavorit(formId)?"yes":"no";
	}


	/**
	 * 
	 * @param format SimpleDateFormat string
	 * @return date as string or empty string on error
	 */
	public String getCurrentDateByFromat(String format) {
		String toReturn = "";
		try {
			SimpleDateFormat formatter= new SimpleDateFormat(format);
			Date date = new Date(System.currentTimeMillis());
			toReturn = formatter.format(date);
		} catch (Exception e) {
			toReturn = "";
		}
		return toReturn;
	}


	public String replaceDBUpdateVal(String value) {
		// TODO Auto-generated method stub
		return getNull(value).replaceAll("'", "''").replaceAll("–", "-");
	}


	public long generateStateKey(String userId) {
		long stateKey = 0l;
		try {
			String userIdNum = (userId != null && userId.length() > 3)?  userId.substring(userId.length() - 3): "000";
			stateKey = Long.valueOf(String.valueOf((new Date()).getTime()) + userIdNum);
		} catch (NumberFormatException e) {
			stateKey = (new Date()).getTime();
		}
		  
		return stateKey;
	}
    
}