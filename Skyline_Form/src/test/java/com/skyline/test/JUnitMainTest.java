package com.skyline.test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.joda.time.DateTime;
import org.joda.time.Seconds;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The unittest is using selenium chromedriver. the driver is locate in: General\Doc\Installation\Tool_chromedriver\chromedriver.exe.  
   
 1. see makeTest function that describe the unit test flow.
 2. to summarize the places we have unit test code:
 	- FG_UNITTEST DB pack
 	- unit test scripts in the Maintenance forms (log in as system under maintenance -> "_system unit test pool". Note: fg_i_unittestdata_v view contains all the scripts information)
 	- unit test log under the: menu -> system log -> unittest log (the log is taken from fg_r_unitestlog_v view)
 	- js/bl_<customer>_unittest.js with helper functions used by the chromedriver  
 4	(FOR NEXT VERSIONS - for loading test by running the jar in parallel number of times) To make the .jar: 
 	1) right click on this file (or any other) from the enterprise explorer
 	2) Export -> Export - Java:Runnable JAR file 
	3) On the form Runnable Jar File Specification choose Test - SkylineForm (in Launch configuration) 
		Note: 1) this selection will appear only if you run this class at least one time as a "Java application"  (right click on this file -> run as -> java application)
			  2) after running this class as "Java application" make sure that under "run configuration"-> Java application -> JUnitMainTest -> jre tab:  you run it under jdk (not jre) this is written in the brackets.
	4) type C:\Logs\jutest.jar (in Export destination), choose "Copy required libraries into a sub-folder next to generated JAR, click Finish.
	5) In C:\Logs\jutest.jar should be: jutest.jar, app.properties (Unittest) and folder with jar's - jutest_lib
		Note!!!: Inside jutest_lib should be guava-23.4-jre.jar. (this jar need to be use only for unitest and we insert it manually to the jars folder), Adding this jar to the mavem may couse a problem(!?) in the jchem code, this is why it is in comment inside the pom
	
	 
 5. log screen (IN NEXT VERSIONS - running the unit test from the application server - FOR NOW IT IS HIDDEN)
	1) A log table of the unit test last run is in system log -> Unit test log (we have also a report on the run hst under Unit test hst log)
	2) In system log -> Unit test we have the ability to run the unit test from the system (if we have the jar described above), In order to run the test number of times simultaneously, we need to avoid the unit test cleanup (by setting the parameter doUnitestCleanup to n - never)
	
 Notes: !!!!
 	- in the js/bl_General_unittest.js we have the ability to fill the elements with default values. This ability was not been used yet
	 
 *
 */

public class JUnitMainTest {

	private final int BASIC_SLEEP_TIME = 30000;
	private final int DEFAULT_SLEEP_COUNTER = 30;
	private final String loginUserName = "unittestuser";
	private final String loginUserPassword = "1234";

	private static final Logger logger = LoggerFactory.getLogger(JUnitMainTest.class);
	private final String jdbcDriverClassName;
	private String UNITTEST_LEVEL; // list of level we want to check 1,2,3,....
	private final String jdbcUser;
	private final String loginUrl;
	String urlForFormsUnSQL;
	private final String jdbcUrl;
	private final String jdbcPassword;
	private String loginUserId;
	private String ignoreDBValidation;
	private Connection con = null;
	private String startDate, finishDate;
	private DateTime startDate2;
	private DateTime endDate;
	private static WebDriver wd = null;
	private int delayForVisualisation = 100;
	private int isHeadLess = 0;
	private String SAVEVALIDATIONSQL = "";
	private Properties properties;
//	private final String propertyFilePath = "c://Logs//app.properties";
	private final String propertyFilePath= "src//test//resources//app.properties";  
	private String unittestlogid = "-1";
	private String doUnitestCleanup = "s";
	private String compareToLastRun = "";
	private String chromedriverPath = "";
	 
	public JUnitMainTest() {
		BufferedReader reader;
		try {

			reader = new BufferedReader(new FileReader(propertyFilePath));
			properties = new Properties();
			try {
				properties.load(reader);
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			jdbcDriverClassName = properties.getProperty("jdbc.driverClassName");
			jdbcPassword = properties.getProperty("jdbc.password");
			jdbcUrl = properties.getProperty("jdbc.url");
			jdbcUser = properties.getProperty("jdbc.username");

			loginUrl = properties.getProperty("login_url");
			loginUserId = properties.getProperty("login_user_userId");

			urlForFormsUnSQL = properties.getProperty("urlForFormsUnSQL");

			UNITTEST_LEVEL = properties.getProperty("unittest_level"); // UNITTEST_LEVEL - 1 - basic , 2 - deeper ... (TODO control it from the running screen)

			ignoreDBValidation = properties.getProperty("ignoreDBValidation"); // 0 - to do db validation 

			if (properties.getProperty("delayForVisualisation") != null) {
				try {
					delayForVisualisation = Integer.parseInt(properties.getProperty("delayForVisualisation"));
				} catch (Exception e) {
					System.out.println(e);
				}
			}

			if (properties.getProperty("isHeadLess") != null) {
				try {
					isHeadLess = Integer.parseInt(properties.getProperty("isHeadLess"));
				} catch (Exception e) {
					System.out.println(e);
				}
			}

			SAVEVALIDATIONSQL = properties.getProperty("saveValidationSql");
			if (SAVEVALIDATIONSQL == null) {
				SAVEVALIDATIONSQL = "";
			}

			doUnitestCleanup = properties.getProperty("doUnitestCleanup");
			if (doUnitestCleanup == null) {
				doUnitestCleanup = "";
			}

			compareToLastRun = properties.getProperty("compareToLastRun");
			if (compareToLastRun == null) {
				compareToLastRun = "0";
			}
			
			chromedriverPath = properties.getProperty("chromedriverPath");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Configuration.properties not found at " + propertyFilePath);
		}
	}

	public static void main(String[] args) throws Exception {
		JUnitCore.main("com.skyline.test.JUnitMainTest");
	}

	@Test
	/**
	 * doDBValidation - use FG_UNITTEST.doDBValidation (function doDBValidation under in FG_UNITTEST pack) that check validation according to the UNITTEST_LEVEL (see app.prpoerties)
	 * @throws InterruptedException
	 * @throws SQLException
	 */
	public void makeTest() throws InterruptedException, SQLException {

		//get dates
		startDate = new SimpleDateFormat("dd.MM HH:mm:ss").format(Calendar.getInstance().getTime());
		startDate2 = new DateTime().minusDays(1);

		//DB connection
		Assert.assertFalse(!setDBConnection());

		//cleanup - using FG_UNITTEST.doUnitTestClean DB procedure that clean: 
		// * all the record the unittestuser (formid = 100) created 
		// * all the fg_formlastsavevalue_hst records change_by the unittestuser
		// * the fg_activity_log (by insert the record to the fg_activity_log_hst) in order to see if there are errors during the unittest run
		// * make the unittestuser deleted (for disable the login with this user)
		// * clean the unittest log (table FG_UNITEST_LOG)
		if (doUnitestCleanup.toLowerCase().contains("s")) {
			Assert.assertFalse(!sqlRun("begin FG_UNITTEST.doUnitTestClean; end;", "initDbVoid"));
		}

		//init unit test
		// * create or active the unittestuser with formid 100 and password 1234
		doDBInit();

		//doDBValidation
		// * make DB validation according to the prop ignoreDBValidation (1 ignore the validation 0 make it). 
		// * the validation is in FG_UNITTEST.isValidDB DB procedure, we can add there DB validation and give each check a level number to config how deep we make the validation according to the unittest_level prop 
		// * which is a csv of levels, for example 1,2,3 - where 1 - basic (Serenity), 2 - deeper ....
		// * as a default set unittest_level=1 for Serenity check and make the critical sql validations inside the FG_UNITTEST.isValidDB procedure under this level.
		if (ignoreDBValidation.equals("0")) {
			doDBValidation();
		}

		// ...login with unitestuser
		logger.info("call testLogin()..");
		Assert.assertFalse(!testLogin(loginUrl));
		writeToLog("", loginUserId, "", loginUrl + ": Test Login Passed", "Test Login", "0", "passed", "", "", null);

		//run the unittest script created in the maintenance (when log as admin under "_system unit test pool") this maintenance configuration is summarized in the fg_i_unittestdata_v view using by this function
		doMaintenanceScriptTest();
		Thread.sleep(5000);
		

		//in doSQLScriptTest we open and save forms according SAVEVALIDATIONSQL SQL (we use the FG_I_UNITTEST_GLOBAL_SAVE_V for this) this is a quick solution for regration (by check the activity log (the system log table) - errors and fg_formlastsavevalue_hst (the audit trail table) - data before and above a global code change) 
		if (!SAVEVALIDATIONSQL.equals("")) {
			doSQLScriptTest();
		}

		//backup audit trail values / log / scripts form this run and compare the audit trail values to last unit test run if compareToLastRun = 1 (configuration) 
		if(compareToLastRun != null && compareToLastRun.equals("1")) {
			Thread.sleep(5000);
//			WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
//			FormSchedTaskService formTask =
//					(FormTask) context.getBean("FormTask");
//			formTask.correctDataFromLastFormId(null); // TODO - find solution for run functions from main code
		
			Assert.assertFalse(
					!sqlRun("begin FG_UNITTEST.doUnitTestRunLogBackup(" + compareToLastRun + "); end;", "initDbVoid"));
		}
	}

	@Before
	public void setUp() throws Exception {

		logger.info("start setUp (@Before code) ...");

		//start chrome
		System.setProperty("webdriver.chrome.driver", chromedriverPath);
		ChromeOptions options = new ChromeOptions();
		if (isHeadLess == 1) {
			options.addArguments("--headless"); // use for hide the browser
		}
		options.addArguments("--start-maximized"); // use for hide the browser
		//		options.addArguments("--material-design-ink-drop-animation-speed=0,1"); // use for hide the browser. Not clear, how it's work. There are no examples
		//		options.addArguments("--window-size=1300,1000");
		wd = new ChromeDriver(options);
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
	}

	@After
	public void tearDown() {
		logger.info("start tearDown (@@After code) ...");
		try {
			if (doUnitestCleanup.toLowerCase().contains("e")) {
				Assert.assertFalse(!sqlRun("begin FG_UNITTEST.doUnitTestClean; end;", "initDbVoid"));
			}

		} catch (Exception e1) {
			// TODO Auto-generated catch block
		}

		try {
			// close wd
			wd.quit();
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		logger.info("Done!");
	}

	private void writeToLog(String configFormId, String user_id_, String unittestgroupname_, String message_,
			String action_, String waitingtime_, String test_status_, String fieldvalue_, String unitestlogid,
			Exception e) {
		if (e != null) {
			e.printStackTrace();
		}

		sqlRun(String.format("begin FG_UNITTEST.FG_PUT_TO_UNITTEST_LOG " + "(%s, %s, %s, %s, %s, %s, %s, %s, %s);end;",
				removeUpperComma(configFormId, 100), removeUpperComma(user_id_, 100),
				removeUpperComma(unittestgroupname_, 4000), removeUpperComma(message_, 4000),
				removeUpperComma(action_, 4000), removeUpperComma(waitingtime_, 100),
				removeUpperComma(test_status_, 100), removeUpperComma(fieldvalue_, 4000),
				removeUpperComma(unitestlogid, 100)), "putToLog");

	}

	private String removeUpperComma(String val, int maxLength) {
		String toReturn = val;
		if (val != null) {
			toReturn = (toReturn.equals("{}") ? "" : toReturn);
			toReturn = toReturn.replace("'", "");
			toReturn = (toReturn.length()) > maxLength ? toReturn.substring(0, maxLength - 3) + ".." : toReturn;
		} else {
			toReturn = "";
		}
		return "'" + toReturn + "'";
	}

	private void doDBValidation() {
		logger.info("start doDBValidation...");
		Assert.assertFalse(!sqlRun("begin FG_UNITTEST.isValidDB(" + UNITTEST_LEVEL + "); end;", "initDbVoid"));

	}

	private void doDBInit() {
		//DB
		Assert.assertFalse(!sqlRun("begin FG_UNITTEST.dodbinit; end;", "initDbVoid"));
	}

	private void doMaintenanceScriptTest() {

		logger.info("Start doMaintenanceScriptTest...");

		// init ut
		writeToLog("", loginUserId, "", "Start doMaintenanceScriptTest! dbInitProcess " + "Time Stamp: " + startDate,
				"dbInit", "0", "", "", "", null);

		// ... start scripts
		logger.info("call testScriptAuto()..");
		Assert.assertFalse(!testScriptAuto());

		logger.info("doMaintenanceScriptTest Done!");

	}

	private void doSQLScriptTest() {
		logger.info("Start doSQLScriptTest...");

		// init ut
		writeToLog("", loginUserId, "", "Start doSQLScriptTest! dbInitProcess " + "Time Stamp: " + startDate, "dbInit",
				"0", "", "", "", null);

		// ... start scripts according to SAVEVALIDATIONSQL
		logger.info("call testScriptBySaveValidationSql()..");
		Assert.assertFalse(!testScriptBySaveValidationSql());

		logger.info("doSQLScriptTest Done!");

	}

	private String stringFormat(String url_, String formName_) {
		return String.format(url_, formName_);
	}

	private String stringFormat(String url_, String formName_, String currFormId, String tableType, String parentId_) {
		return String.format(url_, formName_, currFormId, loginUserId, tableType, parentId_);
	}

	private boolean testLogin(String url) {
		boolean loginSuccess = false;
		try {
			wd.get(url);
			wd.findElement(By.name("txtUser")).click();
			wd.findElement(By.name("txtUser")).sendKeys(loginUserName);
			wd.findElement(By.name("txtPassword")).click();
			wd.findElement(By.name("txtPassword")).sendKeys(loginUserPassword);
			WebElement button = wd.findElement(By.name("login"));
			button.click();
			loginSuccess = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return loginSuccess;
	}

	private boolean sqlRun(String sql, String whatGet) {
		boolean isSuccessful = false;
		try {
			CallableStatement stmt = null;
			stmt = con.prepareCall(sql);
			stmt.execute();
			ResultSet rs = stmt.getResultSet();
			int i = 0;

			if (whatGet.equals("initDbVoid")) {
				isSuccessful = true;
			} else if (whatGet.equals("putToLog")) {
				isSuccessful = true;
			} else {
				while (rs.next()) {
					if (whatGet.equals("formId")) {
					} else if (whatGet.equals("userId")) {
						loginUserId = rs.getString(1);
					}
					i++;
				}
				isSuccessful = i > 0 ? true : false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return isSuccessful;
		} finally {
		}
		return isSuccessful;
	}

	private boolean testScriptAuto() {
		boolean projectSuccess = false;
		try {

			//			kostya
			String sql_ = "{ ? = call FG_UNITTEST.FG_GET_UNITTEST_ID() }";
			//String sql_ = "begin FG_UNITTEST.FG_GET_UNITTEST_ID; end;";

			CallableStatement statement = con.prepareCall(sql_);
			statement.registerOutParameter(1, java.sql.Types.INTEGER);

			statement.execute();
			//this is the main line
			unittestlogid = String.valueOf(statement.getLong(1));

			if (!sqlRun("select u.USER_ID from FG_S_USER_ALL_V u where u.USERNAME = '" + loginUserName + "'",
					"userId")) {
				writeToLog("", loginUserId, "", loginUserName + ": Get UserId failed", "get UserId", "0", "failed", "",
						unittestlogid, null);
				return false;
			} else {
				writeToLog("", loginUserId, "", loginUserName + ": Get UserId passed", "get UserId", "0", "passed", "",
						unittestlogid, null);
			}

			//String 
			sql_ = "select t.* from fg_i_unittestdata_v t where t.unittestlevels <= '" + UNITTEST_LEVEL + "'"; // UNITTEST_LEVEL - 1 - basic , 2 - deeper ...
			CallableStatement stmt = null;
			stmt = con.prepareCall(sql_);
			stmt.execute();
			ResultSet rs = stmt.getResultSet();
			Seconds seconds;
			int secondsInDay;
			//			int runNumber = 0;
			while (rs.next()) {
				String action_ = rs.getString("unittestaction");
				String fieldvalue_ = rs.getString("fieldvalue");
				if (fieldvalue_ != null && fieldvalue_.contains("{UNIQUE_ID}")) {
					fieldvalue_ = fieldvalue_.replaceAll("\\{UNIQUE_ID\\}", unittestlogid);
				}

				projectSuccess = doAction(action_, rs.getString("entityimpname"), fieldvalue_,
						rs.getString("unittestgroupname"), rs.getString("formid"), "", "");

				writeToLog(rs.getString("formid"), loginUserId, rs.getString("unittestgroupname"), null,
						rs.getString("unittestaction"), null, "passed",
						((rs.getString("entityimpname") == null) ? "" : rs.getString("entityimpname") + " -> ")
								+ fieldvalue_,
						unittestlogid, null);
				logger.info("- ************ action=" + action_ + " value=" + fieldvalue_);

				// log if failure and return
				if (!projectSuccess) {
					finishDate = new SimpleDateFormat("dd.MM HH:mm:ss").format(Calendar.getInstance().getTime());
					endDate = new DateTime();
					seconds = Seconds.secondsBetween(startDate2, endDate);
					secondsInDay = seconds.getSeconds() - 86400;

					writeToLog("", loginUserId, "",
							"Finish test! " + "Time Stamp On Start: " + startDate + ", Time Stamp On Finish: "
									+ finishDate + ". Test duration: " + secondsInDay + " sec.",
							"End Test", "0", "Failed", "", unittestlogid, null);
					return projectSuccess;
				}
			}

			// log the success..
			finishDate = new SimpleDateFormat("dd.MM HH:mm:ss").format(Calendar.getInstance().getTime());
			endDate = new DateTime();
			seconds = Seconds.secondsBetween(startDate2, endDate);
			secondsInDay = seconds.getSeconds() - 86400;

			writeToLog("", loginUserId, "",
					"Finish test! " + "Time Stamp On Start: " + startDate + ", Time Stamp On Finish: " + finishDate
							+ ". Test duration: " + secondsInDay + " sec.",
					"End Test", "0", "passed", "", unittestlogid, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return projectSuccess;
	}

	private boolean testScriptBySaveValidationSql() {
		boolean projectSuccess = false;
		try {
			String sql_ = "{ ? = call FG_UNITTEST.FG_GET_UNITTEST_ID() }";
			//String sql_ = "begin FG_UNITTEST.FG_GET_UNITTEST_ID; end;";

			CallableStatement statement = con.prepareCall(sql_);
			statement.registerOutParameter(1, java.sql.Types.INTEGER);

			statement.execute();
			//this is the main line
			unittestlogid = String.valueOf(statement.getLong(1));

			if (!sqlRun("select u.USER_ID from FG_S_USER_ALL_V u where u.USERNAME = '" + loginUserName + "'",
					"userId")) {
				writeToLog("", loginUserId, "", loginUserName + ": Get UserId failed", "get UserId", "0", "failed", "",
						unittestlogid, null);
				return false;
			} else {
				writeToLog("", loginUserId, "", loginUserName + ": Get UserId passed", "get UserId", "0", "passed", "",
						unittestlogid, null);
			}

			//String 
			//sql_ = "select t.* from fg_i_unittestdata_v t where t.unittestlevels <= '" + UNITTEST_LEVEL + "'"; // UNITTEST_LEVEL - 1 - basic , 2 - deeper ...

			sql_ = SAVEVALIDATIONSQL;

			CallableStatement stmt = null;
			stmt = con.prepareCall(sql_);
			stmt.execute();
			ResultSet rs = stmt.getResultSet();
			Seconds seconds;
			int secondsInDay;
			String parentId = "-1";
			String action_ = "";
			while (rs.next()) {

				parentId = rs.getString("PARENT_ID");
				if (parentId.equals("-1")) {
					action_ = "Open and Save Form";
					projectSuccess = doAction(action_, parentId, "using sql:" + sql_, rs.getString("FORMCODE"),
							rs.getString("FORM_ID"), "", "");
				} else {
					action_ = "Open and Save Popup";

					projectSuccess = doAction(action_, parentId, "using sql:" + sql_, rs.getString("FORMCODE"),
							rs.getString("FORM_ID"), rs.getString("PARENT_FORMCODE"), rs.getString("TABLETYPE"));
				}

				//make validation on hst
				projectSuccess = doAction("SQL for validation", rs.getString("FORM_ID"),
						"select count(*) from fg_s_" + rs.getString("FORMCODE") + "_pivot where formid= '"
								+ (parentId.equals("-1") ? rs.getString("FORM_ID") : rs.getString("FORM_ID"))
								+ "' and change_by = 100",
						rs.getString("FORMCODE"), rs.getString("FORM_ID"), "", "");

				writeToLog(rs.getString("FORM_ID"), loginUserId, rs.getString("FORMCODE"), null,
						"Navigate and click saveButton", null, "passed",
						"formId ->" + rs.getString("FORM_ID") + ", parentFormId -> " + rs.getString("PARENT_ID"),
						unittestlogid, null);

				// log if failure and return
				if (!projectSuccess) {
					finishDate = new SimpleDateFormat("dd.MM HH:mm:ss").format(Calendar.getInstance().getTime());
					endDate = new DateTime();
					seconds = Seconds.secondsBetween(startDate2, endDate);
					secondsInDay = seconds.getSeconds() - 86400;

					writeToLog("", loginUserId, "",
							"Finish test! " + "Time Stamp On Start: " + startDate + ", Time Stamp On Finish: "
									+ finishDate + ". Test duration: " + secondsInDay + " sec.",
							"End Test", "0", "Failed", "", unittestlogid, null);
					return projectSuccess;
				}
			}

			// log the success..
			finishDate = new SimpleDateFormat("dd.MM HH:mm:ss").format(Calendar.getInstance().getTime());
			endDate = new DateTime();
			seconds = Seconds.secondsBetween(startDate2, endDate);
			secondsInDay = seconds.getSeconds() - 86400;

			writeToLog("", loginUserId, "",
					"Finish test! " + "Time Stamp On Start: " + startDate + ", Time Stamp On Finish: " + finishDate
							+ ". Test duration: " + secondsInDay + " sec.",
					"End Test", "0", "passed", "", unittestlogid, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return projectSuccess;
	}

	private boolean doAction(String action, String fieldName, String fieldValue, String unitestGroupName,
			String configFormId, String parentFormCode, String tableType) throws InterruptedException {
		boolean gotCorrect = false, isSuccessful = false;
		int sleepTimeCurrent = BASIC_SLEEP_TIME / DEFAULT_SLEEP_COUNTER;
		int i = DEFAULT_SLEEP_COUNTER;

		if (delayForVisualisation > 0) {
			Thread.sleep(delayForVisualisation);
		}
		if (action.equals("Navigate")) {
			String navUrl = fieldValue;
			while (!gotCorrect) {
				try {
					//					wd.get(navUrl);
					((JavascriptExecutor) wd).executeScript("unitTestHelper_NavigationByUrl(\"" + fieldValue + "\");");
					if (!isSameUrl(wd.getCurrentUrl(), fieldValue)) {
						throw new Exception();
					}
					gotCorrect = true;
					isSuccessful = true;
				} catch (Exception e) {
					i--;
					if ((sleepTimeCurrent > 0) && (i > 0)) {
						Thread.sleep(sleepTimeCurrent);
					} else {
						gotCorrect = true;
						writeToLog(configFormId, loginUserId, unitestGroupName, e.toString(), action + ": " + fieldName,
								null, "Exception", navUrl, unittestlogid, e);
					}
				} finally {

				}
			}
		} else if (action.equals("Navigate by SQL")) {
			while (!gotCorrect) {
				try {
					CallableStatement stmt = null;
					stmt = con.prepareCall(stringFormat(fieldValue, loginUserId)); // use stringFormat for replace parameter in field value by current usraId;
					stmt.execute();
					ResultSet rs = stmt.getResultSet();

					while (rs.next()) {
						String url_ = stringFormat(urlForFormsUnSQL, rs.getString("FORMCODE"), rs.getString("FORM_ID"),
								tableType, rs.getString("PARENT_ID"));
						((JavascriptExecutor) wd).executeScript("unitTestHelper_NavigationByUrl(\"" + url_ + "\");");
						if (!isSameUrl(wd.getCurrentUrl(), url_)) {
							throw new Exception();
						}
					}
					gotCorrect = true;
					isSuccessful = true;
				} catch (Exception e) {
					i--;
					if ((sleepTimeCurrent > 0) && (i > 0)) {
						Thread.sleep(sleepTimeCurrent);
					} else {
						gotCorrect = true;
						writeToLog(configFormId, loginUserId, unitestGroupName, e.toString(), action, null,
								"Exception. Fault", "", unittestlogid, e);
					}
				} finally {

				}
			}
		} else if (action.equals("SetInput")) { // possible to use for check boxes. Value should be 1 
			while (!gotCorrect) {
				boolean isPopup = false;
				try {
					if (fieldValue.startsWith("PopupWindow.")) {

						wd.switchTo().frame("formIframeId");
						isPopup = true;

						wd.findElement(By.id(fieldName)).click();
						wd.findElement(By.id(fieldName)).clear();
						wd.findElement(By.id(fieldName)).sendKeys(fieldValue.replaceAll("PopupWindow.", ""), Keys.TAB);

					} else {
						wd.findElement(By.id(fieldName)).click();
						wd.findElement(By.id(fieldName)).clear();
						wd.findElement(By.id(fieldName)).sendKeys(fieldValue, Keys.TAB);
					}
					gotCorrect = true;
					isSuccessful = true;

				} catch (Exception e) {
					i--;
					if ((sleepTimeCurrent > 0) && (i > 0)) {
						Thread.sleep(sleepTimeCurrent);
					} else {
						gotCorrect = true;
						writeToLog(configFormId, loginUserId, unitestGroupName, e.toString(), action + ": " + fieldName,
								null, "Exception. Fault", fieldValue, unittestlogid, e);
					}
				} finally {
					if (isPopup) {
						wd.switchTo().parentFrame();
					}
				}
			}
		} else if (action.equals("Runjavascript")) {
			while (!gotCorrect) {
				try {
					((JavascriptExecutor) wd).executeScript(fieldValue);
					gotCorrect = true;
					isSuccessful = true;
				} catch (Exception e) {
					i--;
					if ((sleepTimeCurrent > 0) && (i > 0)) {
						Thread.sleep(sleepTimeCurrent);
					} else {
						gotCorrect = true;
						writeToLog(configFormId, loginUserId, unitestGroupName, e.toString(), action + ": " + fieldName,
								null, "Exception. Fault", fieldValue, unittestlogid, e);
					}
				} finally {

				}
			}
		} else if (action.equals("SetSelect")) {
			while (!gotCorrect) {
				boolean isPopup = false;
				try {
					String fieldValue_ = fieldValue;
					if (fieldValue.startsWith("PopupWindow.")) {
						fieldValue_ = fieldValue_.replaceAll("PopupWindow.", "");

						wd.switchTo().frame("formIframeId");
						isPopup = true;
					}

					((JavascriptExecutor) wd).executeScript(
							"unitTestHelper_SetChosenDDL(\"" + fieldName + "\", \"" + fieldValue_ + "\");");

					gotCorrect = true;
					isSuccessful = true;
				} catch (Exception e) {
					i--;
					if ((sleepTimeCurrent > 0) && (i > 0)) {
						Thread.sleep(sleepTimeCurrent);
					} else {
						gotCorrect = true;
						writeToLog(configFormId, loginUserId, unitestGroupName, e.toString(), action + ": " + fieldName,
								null, "Exception. Fault", fieldValue, unittestlogid, e);
					}
				} finally {
					if (isPopup) {
						wd.switchTo().parentFrame();
					}
				}
			}
		} else if (action.equals("Click")) {
			while (!gotCorrect) {
				boolean isPopup = false;

				try {
					String fieldValue_ = "";

					//next if for check if click for PopUp window 
					if (fieldValue.startsWith("PopupWindow.")) {
						fieldValue_ = fieldValue.replaceAll("PopupWindow.", "");

						wd.switchTo().frame("formIframeId");
						isPopup = true;

						((JavascriptExecutor) wd)
								.executeScript("document.getElementById('" + fieldValue_ + "').click()");

						//next if for execute script saved in field value 
					} else if (fieldValue.startsWith("$")) {
						if (fieldValue.startsWith("$PopupWindow.")) {
							fieldValue_ = fieldValue.replaceAll("PopupWindow.", "");

							wd.switchTo().frame("formIframeId");

							if (fieldValue_.contains("input:radio")) {
								((JavascriptExecutor) wd).executeScript(fieldValue_ + ".attr('checked',true)");
							} else {
								((JavascriptExecutor) wd).executeScript(fieldValue_ + ".click()");
							}
						} else {
							if (fieldValue.contains("input:radio")) {
								((JavascriptExecutor) wd).executeScript(fieldValue + ".attr('checked',true)");
							} else {
								((JavascriptExecutor) wd).executeScript(fieldValue + ".click()");
							}
						}
					} else if (fieldValue.endsWith(".New")) {
						fieldValue_ = fieldValue.replaceAll(".New", ""); //checked
						((JavascriptExecutor) wd)
								.executeScript("unitTestHelper_clickWhenReady('" + fieldValue_ + "_wrapper .dataTableApiNew')");
					} else if (fieldValue.endsWith(".Add")) {
						fieldValue_ = fieldValue.replaceAll(".Add", "");
						((JavascriptExecutor) wd)
							.executeScript("unitTestHelper_clickWhenReady('" + fieldValue_ + "_wrapper .dataTableApiAdd')");
					} else if (fieldValue.endsWith(".View")) {
						fieldValue_ = fieldValue.replaceAll(".View", ""); //checked
						((JavascriptExecutor) wd)
							.executeScript("unitTestHelper_clickWhenReady('" + fieldValue_ + "_wrapper .dataTableApiView')");
					} else if (fieldValue.endsWith(".Edit")) {
						fieldValue_ = fieldValue.replaceAll(".Edit", "");
						((JavascriptExecutor) wd)
							.executeScript("unitTestHelper_clickWhenReady('" + fieldValue_ + "_wrapper .dataTableApiEditShared')");
					} else if (fieldValue.endsWith(".Clone")) {
						Thread.sleep(3000); //TODO sleep with loop when until ready
						fieldValue_ = fieldValue.replaceAll(".Clone", "");
						((JavascriptExecutor) wd)
							.executeScript("unitTestHelper_clickWhenReady('" + fieldValue_ + "_wrapper .dataTableApiClone')");
					} else if (fieldValue.startsWith("Tab_.")) {
						fieldValue_ = fieldValue.replaceAll("Tab_.", "");
						((JavascriptExecutor) wd).
						//								executeScript("$('#" + groupId + " a[href=\"#"+  tab + "\"]').trigger('click').click()");
								executeScript("$(' a[href=\"#" + fieldValue_ + "\"]').trigger('click').click()");
					} else {
						WebElement element = wd.findElement(By.id(fieldValue));
						while (!element.isEnabled()) {
							System.out.println("is Enabled:" + element.isEnabled());
							Thread.sleep(1000); //???
						}

						((JavascriptExecutor) wd)
								.executeScript("document.getElementById('" + fieldValue + "').click()");
					}
					gotCorrect = true;
					isSuccessful = true;
				} catch (Exception e) {
					i--;
					if ((sleepTimeCurrent > 0) && (i > 0)) {
						Thread.sleep(sleepTimeCurrent);
					} else {
						gotCorrect = true;
						writeToLog(configFormId, loginUserId, unitestGroupName, e.toString(), action, null,
								"Exception. Fault", fieldValue, unittestlogid, e);
					}
				} finally {
					if (isPopup) {
						wd.switchTo().parentFrame();
					}
				}
			}
		} else if (action.equals("SQL for validation")) {
			CallableStatement stmt = null;
			while (!gotCorrect) {
				try {
					System.out.println("validation sql:" + fieldValue);
					stmt = con.prepareCall(fieldValue);
					stmt.execute();
					ResultSet rs = stmt.getResultSet();
					while (rs.next()) {
						if (!rs.getString(1).equals("0")) {
							gotCorrect = true;
							isSuccessful = true;
						} else {
							i--;
							if ((sleepTimeCurrent > 0) && (i > 0)) {
								Thread.sleep(sleepTimeCurrent);
							} else {
								gotCorrect = true;
								writeToLog(configFormId, loginUserId, unitestGroupName,
										"\"Validation sql\" returned false!", action, null, "Fault", fieldValue,
										unittestlogid, null);
							}
						}
					}
				} catch (Exception e) {
					i--;
					if ((sleepTimeCurrent > 0) && (i > 0)) {
						Thread.sleep(sleepTimeCurrent);
						writeToLog(configFormId, loginUserId, unitestGroupName, e.toString(), action,
								String.valueOf(sleepTimeCurrent), "Exception. Added pause", fieldValue, unittestlogid,
								e);
					} else {
						gotCorrect = true;
						writeToLog(configFormId, loginUserId, unitestGroupName, e.toString(), action, null,
								"Exception. Fault", fieldValue, unittestlogid, e);
					}
				} finally {

				}
			}
		} else if (action.equals("New")) {
			while (!gotCorrect) {
				try {
					((JavascriptExecutor) wd).executeScript("document.getElementById('newButton').click()");
					WebElement mySelectElement = wd.findElement(By.id(fieldName));
					Select dropdown = new Select(mySelectElement);
					dropdown.selectByVisibleText(fieldValue);

					((JavascriptExecutor) wd).executeScript("document.getElementById('btnWFDialogContinue').click()");

					gotCorrect = true;
					isSuccessful = true;
				} catch (Exception e) {
					i--;
					if ((sleepTimeCurrent > 0) && (i > 0)) {
						Thread.sleep(sleepTimeCurrent);
					} else {
						gotCorrect = true;
						writeToLog(configFormId, loginUserId, unitestGroupName, e.toString(), action, null,
								"Exception. Fault", fieldValue, unittestlogid, e);
					}
				} finally {
					wd.switchTo().parentFrame();
				}
			}
		} else if (action.equals("Open and Save Form")) {
			/**
			 *
			 * parameters of doAction(String action, String fieldName, String fieldValue, String unitestGroupName, String configFormId, String parentFormCode, String tableType)
			 * action = "Open and Save Form"
			 * fieldName = rs.getString("PARENT_ID")
			 * fieldValue -> value of saveValidationSql from app.properties
			 * unitestGroupName = rs.getString("FORMCODE")
			 * configFormId = rs.getString("FORM_ID")
			 * 
			 */
			while (!gotCorrect) {
				try {

					String url_ = stringFormat(urlForFormsUnSQL, unitestGroupName, configFormId, tableType,
							parentFormCode);
					((JavascriptExecutor) wd).executeScript("unitTestHelper_NavigationByUrl(\"" + url_ + "\");");
					if (!isSameUrl(wd.getCurrentUrl(), url_)) {
						throw new Exception();
					}

					//some hard-coded actions using makeOpenSaveActionByFormCode
					makeOpenSaveActionByFormCode();

					WebElement element = null;
					String saveName = "saveButton";
					try {
						element = wd.findElement(By.id("saveButton"));
					} catch (Exception e) {
						saveName = "save_";
						element = wd.findElement(By.id("save_"));
					}

					while (!element.isEnabled()) {
						System.out.println("is Enabled:" + element.isEnabled());
					}

					((JavascriptExecutor) wd).executeScript("document.getElementById('" + saveName + "').click()");

					gotCorrect = true;
					isSuccessful = true;
				} catch (Exception e) {
					i--;
					if ((sleepTimeCurrent > 0) && (i > 0)) {
						Thread.sleep(sleepTimeCurrent);
					} else {
						gotCorrect = true;
						writeToLog(configFormId, loginUserId, unitestGroupName, e.toString(), action, null,
								"Exception. Fault", "", unittestlogid, e);
					}
				} finally {

				}
			}
		} else if (action.equals("Open and Save Popup")) {
			/**
			 *
			 * parameters of doAction(String action, String fieldName, String fieldValue, String unitestGroupName, String configFormId, String parentFormCode, String tableType)
			 * action = "Open and Save Popup"
			 * fieldName = rs.getString("PARENT_ID")
			 * fieldValue -> value of saveValidationSql from app.properties
			 * unitestGroupName -> rs.getString("FORMCODE")
			 * configFormId = rs.getString("FORM_ID")
			 * parentFormCode = rs.getString("PARENT_FORMCODE")
			 * tableType = rs.getString("TABLETYPE")
			 * 
			 */

			String page = stringFormat(urlForFormsUnSQL, unitestGroupName, configFormId, tableType,
					fieldName /*parentFormCode*/);
			while (!gotCorrect) {
				try {
					//open main form (Project, SubProject, Experiment...)

					String url_ = stringFormat(urlForFormsUnSQL, parentFormCode, fieldName, "", "");
					((JavascriptExecutor) wd).executeScript("unitTestHelper_NavigationByUrl(\"" + url_ + "\");");
					if (!isSameUrl(wd.getCurrentUrl(), url_)) {
						throw new Exception();
					}

					//open popup
					((JavascriptExecutor) wd).executeScript(
							"unitTestHelper_OpenSaveAction_OpenInFrame(\"" + page + "\", \"" + configFormId + "\");");

					//save the popup
					wd.switchTo().frame("formIframeId");
					WebElement element = wd.findElement(By.id("save_"));
					while (!element.isEnabled()) {
						System.out.println("is Enabled:" + element.isEnabled());
						Thread.sleep(1000);
					}
					((JavascriptExecutor) wd).executeScript("document.getElementById('save_').click()");

					//check if popup close
					element = null;
					Thread.sleep(200);
					boolean isPopupClose = false;
					int popupCloseCounter = 5;
					while (!isPopupClose && popupCloseCounter > 0) {
						try {
							element = wd.findElement(By.id("saveButton"));
							isPopupClose = true;
						} catch (Exception e) {
							popupCloseCounter--;
							Thread.sleep(500);
						}
					}

					//check element element Enabled
					while (!element.isEnabled()) {
						System.out.println("is Enabled:" + element.isEnabled());
						Thread.sleep(1000);
					}

					//click save
					((JavascriptExecutor) wd).executeScript("document.getElementById('saveButton').click()");

					gotCorrect = true;
					isSuccessful = true;
				} catch (Exception e) {
					i--;
					if ((sleepTimeCurrent > 0) && (i > 0)) {
						Thread.sleep(sleepTimeCurrent);
					} else {
						gotCorrect = true;
						writeToLog(configFormId, loginUserId, unitestGroupName, e.toString(), action, null,
								"Exception. Fault", "", unittestlogid, e);
					}
				} finally {

				}
			}
		}

		return isSuccessful;
	}

	private void makeOpenSaveActionByFormCode() throws Exception {
		//sleep
		Thread.sleep(2000);

		//get formCode
		String formCode = (wd.findElement(By.id("formCode"))).getAttribute("value");

		//Step
		if (formCode.equalsIgnoreCase("step")) {
			for (int i = 0; i < 5; i++) {
				logger.info(
						"- ************ action=unitTestHelper_OpenSaveAction_StepReactionClickCalcByIndex -> click ["
								+ i + "] calc icon");
				((JavascriptExecutor) wd)
						.executeScript("unitTestHelper_OpenSaveAction_StepReactionClickCalcByIndex(" + i + ");");
				Thread.sleep(2000);
			}
		}
	}

	private boolean isSameUrl(String currentUrl, String navigationUrl) {
		boolean toReturn = false;
		try {
			Map<String, String> cmap = getUrlParamMap(currentUrl);
			Map<String, String> nmap = getUrlParamMap(navigationUrl);
			toReturn = cmap.get("formCode").equals(nmap.get("formCode"));
		} catch (Exception e) {

		}
		return toReturn;
	}

	private Map<String, String> getUrlParamMap(String url) {
		String[] params = url.substring(url.indexOf("?") + 1).split("&");
		Map<String, String> map = new HashMap<String, String>();
		for (String param : params) {
			if (param.contains("=")) {
				String name = param.split("=", -1)[0];
				String value = param.split("=", -1)[1];
				map.put(name, value);
			}
		}
		return map;
	}

	private boolean setDBConnection() {
		// TODO Auto-generated method stub
		boolean initSuccess = false;
		try {
			// init connection
			setDbConnection();
			initSuccess = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
		return initSuccess;
	}

	private void setDbConnection() {
		try {
			Class.forName(jdbcDriverClassName);
			con = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}