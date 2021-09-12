package com.skyline.form.entitypool;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.google.common.io.Files;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.entity.Element;
import com.skyline.form.service.FormBuilderService;

import jasper.biz.JasperDataSourceSupplier;
import jasper.biz.JasperReportGenerator;
import jasper.biz.JasperReportType;
import jasper.biz.JasperTemplateFactory;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRResultSetDataSource;

/**
 * The element holds the report design configuration (ireports xmls) and the data (catalog) for rendering pdf/ pdf preview (in embed display)/pdf preview static (in embed display - make report only once by formCode + impCode + formId) excel/ doc  reports using Tool_Jasper jar (jar application in this git)
 * Note:
 *  1) in the Jasper.comfig file: if _DEVELOP_GENERATE_COMPILE_FILE is exists (should be only during develop to be on the safe side) - save the ireport compiled file (the same file name with jasper instead of xml) in the report rendering process. This file will be used in the next rendering for make the process faster (=> commit the jasper file and comment _DEVELOP_GENERATE_COMPILE_FILE key for production)
 * 	2) in the Jasper.comfig file: if _DEVELOP_GENERATE_FINAL_XML is exists (must be only during develop!!!) basic xml designed file is created by jasper tool 
 * 	   for reports that are created on the fly based on SQLs / data table json data.
 *     This can make the develop faster (without building it from the start) using this call (this is an example that you can find in the code):
 *                JasperTemplateFactory jf = new JasperTemplateFactory(conn,DIR_JASPER_XML,DIR_JASPER_XML + "\\TEMPLATE",DIR_JASPER_XML+ "\\TMP");
 * 				  jf.getJasperReportCompiledDataInjection(fileTmp_,jsonInsteadSql,"","",false, startColumnIndexActions));
 * 		- 
    3) from jasper tool version 9.6.6 - if the ireport contains the parameter CONFIG_PARAM_PRINT_ON_LOAD_PDF - it will open the print window in the pdf automatically (mostly for labels) 
 	-- !!! PDF PREVIEW NOT SUPPORT DISPLAY DATA FROM THE FORM -> TODO!?
 */
public class ElementIreportImp extends Element {

	@Autowired
	private FormBuilderService formBuilderService;

	@Value("${jdbc.url}")
	private String DB_URL;

	@Value("${jdbc.username}")
	private String DB_USER;

	@Value("${jdbc.password}")
	private String DB_PASSWORD;

	@Value("${ireportPath}")
	private String DIR_JASPER_XML;
	
	@Autowired
	private GeneralDao generalDao;

	private String catalog;
	private String catalogadditionaldata;

	private String isDistinct;
	private String fileName;
	private String defualtTitle;
	private String defualtSubTitle;
	private String text;
	private String subReportFileList;
	private String subReportCatalogList;
	private String width;
	private String reportType;
	private final String DEFUALT_REPORT_TYPE = "PDF";
	private String printTemplate;
	private String blCustomerFunc;

	
	private static final Logger logger = LoggerFactory.getLogger(CatalogDBTableImp.class);


	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try{
			if(super.init(stateKey, formCode, impCode, initVal).equals(""))
			{
				boolean isCreated = false;
		
				text = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "text");
				width = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "width");
				subReportFileList = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "subReportFileList");
				subReportCatalogList = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "subReportCatalogList");
				catalog = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "catalog");
				catalogadditionaldata  = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "catalogadditionaldata");
				isDistinct = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "isDistinct");
				fileName = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "fileName");
				printTemplate = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "printTemplate");
				defualtTitle = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "defualtTitle");
				defualtSubTitle = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "defualtSubTitle");
				reportType = generalUtil.getEmpty(generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "reportType"),DEFUALT_REPORT_TYPE);
				blCustomerFunc = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "blCustomerFunc");

				logger.debug("start createIreport");
		
				WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
				GeneralDao generalDao = (GeneralDao) context.getBean("GeneralDao");
				Connection conn = generalDao.getConnectionFromDataSurce();
				String table = formBuilderService.getTableByCatalogDBBean(stateKey, formCode, catalog);
				JasperTemplateFactory jf = new JasperTemplateFactory(conn, DIR_JASPER_XML,
						DIR_JASPER_XML + "\\Template", DIR_JASPER_XML + "\\tmp");
				
				if (printTemplate.equals("")) // kd 05022018 added this check ! Better to move this code (10 rows) to FormApiService!
				{
					try {
						isCreated = jf.createIreport(fileName, defualtTitle, defualtSubTitle, table, true);//kd0312
					} catch (Exception e) {
						// TODO Auto-generated catch block
						generalUtilLogger.logWrite(e);
						e.printStackTrace();
						return "File  already exist";
					}
				}
				generalDao.releaseConnectionFromDataSurce(conn);
				logger.debug("ireport created = " + isCreated);
				return "";
			}
			return "Creation failed";
		}
		catch(Exception e)
		{
			generalUtilLogger.logWrite(e);
			return "Creation failed";
		}
	}

	public void ElementAutoCompl_eteDDLImp(String impCode, String initVal) {
		this.initVal = initVal;
		this.impCode = impCode;

	}

	@Override
	public Map<String, String> getInitHtml(long stateKey, String formId, boolean renderEmpty, String value, String userLastSaveVal, String domId, String inputAttribute,
			String doOnChangeJSCall, boolean isHidden, boolean isDisabled, boolean isMandatory) { //TODO support more than 1 ireport (make unique ids in form)
		HashMap<String, String> html = new HashMap<String, String>();
		 
		String uniqueId = "fireport_" + domId + "_" + new Date().getTime();
		
		//String formCode, String impCode, String fileName, String catalog, String isDistinct, String title, String subTitle
		
		//for element info -> start....
		StringBuilder sqlINfo = new StringBuilder();
		try {
			String[] subReportFileArray = generalUtil.getNull(subReportFileList).split(",");
			String[] subReportCatalogArray = generalUtil.getNull(subReportCatalogList).split(",");
			
			if(subReportFileArray.length > 0 && subReportFileArray.length != subReportCatalogArray.length) {
				logger.warn("sub report file list is smaller than catalog list! subReportFileList=" + subReportFileList + ", subReportCatalogList=" + subReportCatalogList);
			} else {
				for (int i = 0; i < subReportFileArray.length; i++) {
//					  String file_ = subReportFileArray[i]; 
					  String catalog_ = subReportCatalogArray[i]; 
					  //add catalog as parameter
					  if(! generalUtil.getNull(catalog_).equals("")) {
						  sqlINfo.append("sql list[" + i + "]: " + generalUtilFormState.getFormCatalogDBSql(stateKey, formCode, catalog_, "0", impCode,
									"ALL") + "<br />");
					  }
				}
			} 
			
			sqlINfo.append("sql: " + generalUtilFormState.getFormCatalogDBSql(stateKey, formCode, catalog, isDistinct.toLowerCase(), impCode,
					"ALL") + "</br>");
			html.put(domId + "_elementSQLInfo", sqlINfo.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//for element info Done!
		
		width = (width.equals("")) ? "width:300px;" : (width.indexOf("%") != -1) || (width.indexOf("px") != -1) ? "width:" + width + ";": "width:" + width + "px;";
		String hidden = (isHidden) ? "visibility:hidden;" : "";
		String disabled = (isDisabled) ? " disabledclass " : "";
		String irCode = "";
		if(reportType.equals("PDF_PREVIEW_STATIC") || reportType.equals("PDF_PREVIEW")) {
			String src_ = renderIreport(stateKey, formCode, impCode, fileName + ".xml", printTemplate, reportType, catalog, catalogadditionaldata,
					isDistinct, defualtTitle, defualtSubTitle, subReportFileList, subReportCatalogList, 
//					null, null, 
					generalUtil.getSessionId(),
					DIR_JASPER_XML + "/" + formCode +"_" + impCode + "_" + formId + ".pdf", reportType.equals("PDF_PREVIEW_STATIC"));
			
			String styleDiv = "width=\"100%\"  style=\"text-align: center;\"";
			String embed = "<embed width=\"90%\" height=\"650px\" src=\"" + src_ + "\">";
			irCode = "<div " + styleDiv + ">" + embed + "</div>";
		} else {
			irCode = "<form id='" + uniqueId
					+ "' name='fireport' action='ireport.request' method='post'>\r\n"
					+ "        <input type='hidden' name='irFormCode' value='" + formCode + "'/>\r\n"
					+ "        <input type='hidden' name='irImpCode' value='" + impCode + "'/>\r\n"
					+ "        <input type='hidden' name='irCatalog' value='" + catalog + "'/>\r\n"
					+ "        <input type='hidden' name='irCatalogadditionaldata' value='" + catalogadditionaldata + "'/>\r\n"
					+ "        <input type='hidden' name='irIsDistinct' value='" + isDistinct + "'/>\r\n"
					+ "        <input type='hidden' name='irFileName' value='" + fileName + "'/>\r\n"
					+ "        <input type='hidden' name='irPrintTemplate' value='" + printTemplate + "'/>\r\n"
					+ "        <input type='hidden' name='irReportType' value='" + reportType + "'/>\r\n"
					+ "        <input type='hidden' name='irTitle' value='" + defualtTitle + "'/>\r\n"
					+ "        <input type='hidden' name='irSubtitle' value='" + defualtSubTitle + "'/>\r\n"
					+ "        <input type='hidden' name='irSubReportFileList' value='" + subReportFileList + "'/>\r\n"
					+ "        <input type='hidden' name='irSubReportCatalogList' value='" + subReportCatalogList + "'/>\r\n"
					+ "        <input type='hidden' name='irElementsDisplayValues' value=''/>\r\n"
					+ "        <input type='hidden' name='irUrl' value=''/>\r\n"
					+ "        <input type='hidden' name='irStateKey' value='" + stateKey + "'/>\r\n"
//				+ "        <input type='hidden' name='ireportSql' id='ireportSql" + domId + "' value=''/>
					+ "</form>";
			String buttonCode = "<button type=\"button\" id=\"" + domId + "\" class=\"button ireport ignor_data_change" + disabled + "\" style=\"" + width + hidden
					+ "\" onclick=\"elementIreportButtonClickEvent('" + blCustomerFunc + "','" + uniqueId + "')\">"
					+ generalUtil.getSpringMessagesByKey(text.replaceAll(" ", "_"), text)
					+ "</button>";

			html.put(layoutBookMark, buttonCode);
		}
		
		html.put(layoutBookMark + "_html", irCode);
		return html;
	}

	@Override
	public String getHtmlBody(long stateKey, String formId, boolean renderEmpty, String value, String domId, String inputAttribute, String doOnChangeJSCall,
			boolean isHidden, boolean isDisabled, boolean isMandatory) { // TODO return js eval see demo jsp
		String htmlBody;
		htmlBody = "upDateElement({'isHidden':'" + isHidden + "','isDisabled':'" + isDisabled + "','val':'','domId':'"
				+ domId + "','type':'NA'});";
		return htmlBody;
	}


	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema = "schema:{ \r\n" +
				"	text:{  \n" + 
				"		      type:'string',\n" + 
				"		      title:'Button Label'\n" + 
				"	},\n" + 
				"	width:{  \r\n" + 
				"      type:'string',\r\n" + 
				"      title:'Button Width',\r\n" +
				"	   'default':'300px'\n" + 
				"   },\r\n" + 
				"	reportType:{  \r\n" + 
				"		type:'string',\n" + 
				"		title:'Report type',\n" + 
				"		'enum':['PDF','EXCEL','DOC','PDF_PREVIEW','PDF_PREVIEW_STATIC']\n" + 
				"   },\r\n" + 
				"   catalog:{  \r\n" +
				"      type:'string',\r\n" +
				"      title:'Catalog',\r\n" +
				"      'enum':[''].concat(getResourceValueByType('CATALOGDB_IMP_CODE'))\r\n" +
				"   },\r\n" +
				"   catalogadditionaldata:{  \r\n" +
				"      type:'string',\r\n" +
				"      title:'Catalog Additional Data',\r\n" +
				"      'enum':['na'].concat(getResourceValueByType('CATALOGDB_IMP_CODE'))\r\n" +
				"   },\r\n" +
				"	subReportFileList : {\r\n" +
				"		type : 'array',\r\n" +
				"		title : 'Sub report file',\r\n" +
				"		items : {\r\n" +
				"			enum : [''].concat(getResourceValueByType('PATH_IREPORT_POOL')),\r\n" +
				"			title : 'file:'\r\n" +
				"		}\r\n" +
				"	},\r\n" +
				"	subReportCatalogList : {\r\n" +
				"		type : 'array',\r\n" +
				"		title : 'Sub report catalog (for the above sub-report file in the same position)',\r\n" +
				"		items : {\r\n" +
				"			enum : ['na'].concat(getResourceValueByType('CATALOGDB_IMP_CODE')),\r\n" +
				"			title : 'catalog:'\r\n" +
				"		}\r\n" +
				"	},\r\n" +
				"   isDistinct:{  \r\n" +
				"      type:'string',\r\n" +
				"      title:'Hide Duplication',\r\n" +
				"      'enum': [  \r\n" +
				"         'False',\r\n" +
				"         'True'\r\n" +
				"      ]\r\n" +
				"   },\r\n" +
				"   fileName:{  \r\n" +
				"      type:'string',\r\n" +
				"      title:'File Name'\r\n" +
				"   },\r\n" +
				"   printTemplate:{  \r\n" +
				"      type:'string',\r\n" +
				"      title:'Print Template (file name in ireport template dir)'\r\n" +
				"   },\r\n" +
				"   defualtTitle:{  \r\n" +
				"      type:'string',\r\n" +
				"      title:'Title'\r\n" +
				"   },\r\n" +
				"   defualtSubTitle:{  \r\n" +
				"      type:'string',\r\n" +
				"      title:'Sub Title'\r\n" +
				"   },\r\n" +
				" blCustomerFunc:{  \r\n" + 
				" 	type:'string',\r\n" + 
				" 	title:'Customer function (if empty then setDisplayValuesForIreport() is invoke as default)',\r\n" + 		
				" }" +	
				(schema.equals("") ? "" : ",\n" + schema) +
				"\r\n" +
				"}";
		return schema;
	}
	
	private String renderIreport(long stateKey, String formCode, String impCode, String fileName, String printTemplate,
			String reportType, String catalog, String catalogadditionaldata, String isDistinct, String title, String subTitle,
			String subReportFileList, String subReportCatalogList, 
			//tring displayUrl, Map<String, String> displayValuesObj, 
			String sessionId,
			String ireportFileNameFullPath, boolean isStatic) {
		
		String relativePath = ""; // relative asd 
		
		if(!isStatic && new File(ireportFileNameFullPath).exists()) {
			relativePath =  getRelativePath(ireportFileNameFullPath);
			return relativePath;
		}
		
		JasperReportGenerator jrg = null;
		Connection conn = generalDao.getConnectionFromDataSurce();
		String path_file = "";
		try {
			JasperDataSourceSupplier jrdss = new JasperDataSourceSupplier(conn, false);
			String sql = "";
			HashMap<String, String> mLang = new HashMap<String, String>();

			// preparations...
			HashMap<String, String> hmReportReplacerList = new HashMap<String, String>();
			// hmReportReplacerList.put("@replacer1@", " and id = 1 ");
			HashMap<String, Object> hmReportParameterList = new HashMap<String, Object>();

			//add form state parameter to report hmReportParameterList
			for (Map.Entry<String, String> entry : generalUtilFormState.getFormParam(stateKey, formCode).entrySet()) {
				String rKey = entry.getKey().replace("$P{", "").replace("}", "");
				String rVal = entry.getValue();
				hmReportParameterList.put(rKey, rVal);
				logger.info("Ireport parameter key=" + rKey + ", val=" + rVal);
			}

//			for (Map.Entry<String, String> entry : displayValuesObj.entrySet()) {
//				String rKey = entry.getKey().replace("$P{", "").replace("}", "");
//				String rVal = entry.getValue();
//				hmReportParameterList.put(rKey, rVal);
//				logger.info("Ireport displayValuesObj parameter key=" + rKey + ", val=" + rVal);
//			}
//			hmReportParameterList.put("currentUrl", displayUrl);
			
			hmReportParameterList.put("parameter_DB_URL", DB_URL);
			hmReportParameterList.put("parameter_DB_USER", DB_USER);
			hmReportParameterList.put("parameter_DB_PASSWORD", DB_PASSWORD);
			hmReportParameterList.put("parameter_DIR_JASPER_XML", DIR_JASPER_XML);
			String[] subReportFileArray = generalUtil.getNull(subReportFileList).split(",");
			String[] subReportCatalogArray = generalUtil.getNull(subReportCatalogList).split(",");
			
			if (!generalUtil.getNull(catalogadditionaldata).equals("")
					&& !generalUtil.getNull(catalogadditionaldata).equals("na")) {
				sql = generalUtilFormState.getFormCatalogDBSql(stateKey, formCode, catalogadditionaldata, "0", impCode,
						"ALL");
				if (sql.contains("FG_R_FORMADDITIONALDATA_V")) {
					ResultSet rs = generalDao.getResultSet(conn, sql);
					while (rs.next()) {
						if (!generalUtil.getNull(rs.getString(2)).equals("")
								&& !generalUtil.getNull(rs.getString(3)).equals("")) {
							hmReportParameterList.put(rs.getString(2).toUpperCase(), rs.getString(3));
							logger.info("Ireport Additional data parameter: key= " + rs.getString(2).toUpperCase()
									+ ", value =" + rs.getString(3).toUpperCase());
						}
					}
				}
				hmReportParameterList.put("DATA_SOURCE_" + catalogadditionaldata.toUpperCase(),
						jrdss.createReportDataSource("select 1 from dual", "JRMapArrayDataSource"));
				logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_"
						+ catalogadditionaldata.toUpperCase() + ", catalog_=" + catalogadditionaldata);
			}

			if (subReportFileArray.length > 0 && subReportFileArray.length != subReportCatalogArray.length) {
				logger.warn("sub report file list is smaller than catalog list! subReportFileList=" + subReportFileList
						+ ", subReportCatalogList=" + subReportCatalogList);
			} else {
				for (int i = 0; i < subReportFileArray.length; i++) {
					String file_ = subReportFileArray[i];
					String catalog_ = subReportCatalogArray[i];
					//add catalog as parameter
					if (!generalUtil.getNull(catalog_).equals("")) {
						sql = generalUtilFormState.getFormCatalogDBSql(stateKey, formCode, catalog_, "0", impCode,
								"ALL");
						hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
								jrdss.createReportDataSource(sql, "JRMapArrayDataSource"));
						logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
								+ ", catalog_=" + catalog_);
					}
					//add file as parameter
					if (!generalUtil.getNull(file_).equals("")) {
						String fileName_ = file_.replaceAll(".xml", "").toUpperCase();
						String fileTmp_ = fileName_ + sessionId + (new Date()).getTime();
						hmReportParameterList.put("SUB_REPORT_" + fileName_,
								(new jasper.biz.JasperReportGenerator(mLang)).getCompiled(fileTmp_, file_,
										DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
						logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase()
								+ ", fileName_=" + fileName_ + ", fileTmp_=" + fileTmp_);
					}
				}
			}

			sql = generalUtilFormState.getFormCatalogDBSql(stateKey, formCode, catalog, isDistinct.toLowerCase(),
					impCode, "ALL");

			//kd 09042018 this "if" for exception which is appeared when catalog set to empty value in settings for IREPORT, in form builder.  
			if (sql.equals("")) {
				sql = "select 1 from dual";
			}

			JRDataSource myResultSetDS = new JRResultSetDataSource(generalDao.getResultSet(conn, sql));

			// the call
			jrg = new JasperReportGenerator(myResultSetDS, mLang);
			path_file = jrg.getPath(fileName + (new Date()).getTime(), fileName,
					(reportType.equals("PDF") ? JasperReportType.PDF : ((reportType.equals("DOC") ? JasperReportType.DOC :JasperReportType.JXL_EXCEL))), title, subTitle,
					hmReportReplacerList, hmReportParameterList, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp");
			
			if(isStatic) {
				//put the tmp report in the ireport pool for the next use
				Files.copy(new File(path_file), new File(ireportFileNameFullPath));
				relativePath = getRelativePath(ireportFileNameFullPath);
			} else {
				// return the relative path for the tmp report
				relativePath = getRelativePath(path_file);
			}

		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
			//			System.out.println("This is exception: " + ex); //29032018 kd Temp for debug
			// System.out.println("SystemReportsServlet.PasswordManagementReport
			// action: " + ex.toString()); 

		} catch (OutOfMemoryError er) {
			Exception e = new Exception(
					"OutOfMemoryError caught in 'public void renderIreport' function in FormApiService");
			generalUtilLogger.logWrite(e);

		} catch (NoSuchMethodError et) {
			Exception e = new Exception(
					"NoSuchMethodError caught in 'public void renderIreport' function in FormApiService");
			generalUtilLogger.logWrite(e);
			System.out.println("This is exception: " + et);
		} finally {
			generalDao.releaseConnectionFromDataSurce(conn);
		}

		return relativePath;
	}

	private String getRelativePath(String ireportFileNameFullPath) {
		int indx = ireportFileNameFullPath.indexOf("skylineFormWebapp");
		return "../" + ireportFileNameFullPath.substring(indx);
	}
}
