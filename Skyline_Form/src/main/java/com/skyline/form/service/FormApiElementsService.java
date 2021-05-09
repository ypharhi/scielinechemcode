package com.skyline.form.service;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skyline.form.bean.ActionBean;
import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.Form;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.SearchTreeBean;
import com.skyline.form.bean.SearchTreeBeanList;
import com.skyline.form.bean.SearchTreeCurrentData;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.FormSaveDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.dal.UploadFileDao;
import com.skyline.form.entity.Element;

import jasper.biz.GeneralBiz;
import jasper.biz.JasperDataSourceSupplier;
import jasper.biz.JasperReportGenerator;
import jasper.biz.JasperReportType;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRResultSetDataSource;

@Service
public class FormApiElementsService {

	private static final Logger logger = LoggerFactory.getLogger(FormApiService.class);

	@Value("${jdbc.url}")
	private String DB_URL;

	@Value("${jdbc.username}")
	private String DB_USER;

	@Value("${jdbc.password}")
	private String DB_PASSWORD;

	@Value("${ireportPath}")
	private String DIR_JASPER_XML;
	
	/*
	 * 
	 *kd 23122020 
	 *Limit for all names in the tree. 
	 *If name or name plus description (Project, SubProjct, Experiment, Step etc.)
	 *more than this limit then it to be cut for the tree and add tooltip
	 *
	*/
	@Value("${tree.lenghtofnameslimit:120}")
	private int treeLenghtOfNamesLimit;  
	
	@Value("${tree.treeSearchSizeLimit:1000}")
	private int treeSearchSizeLimit;   // if not set in prop the default value should match ElementTreeImp variable (-1 for no limit)

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private GeneralDao generalDao;

	@Autowired
	private FormDao formDao;

	@Autowired
	private GeneralUtilFormState generalUtilFormState;

	@Autowired
	private UploadFileDao uploadFileDao;
  
	@Autowired
	public GeneralUtilLogger generalUtilLogger;
	
	@Autowired
	public IntegrationDT integrationDTAdamaImp;
	
	@Autowired
	public GeneralUtilConfig generalUtilConfig;
	
	@Autowired
	public GeneralUtilPermission generalUtilPermission;
	
	@Autowired
	public FormSaveDao formSaveDao;
	

	private final String delimiter = "@";

	@Transactional
	public String saveSpreadsheet(DataBean dataBean,String isNew, String formCode, String formId) throws Exception{
		String elementId = generalUtil.getJsonValById(dataBean.getVal(), "elementID");//get the saved value
		String isChangedflag = "1";
		try{
			isChangedflag = generalUtil.getJsonValById(dataBean.getVal(),"isChangedflag");
		} catch(Exception e){
			e.printStackTrace();
		}
		if(isChangedflag.equals("1") || isNew.equals("1") 
				|| generalUtil.getNull(elementId).equals("")|| generalUtil.getNull(elementId).equals("-1")) { // changed / new (after cone)  / empty are condition to get a  new elementid. Note: if isNnew and isChangedflag = 0 with elementId (NOT EMPTY) then it is clone 
			elementId = formSaveDao.getStructFileId(formCode + "." + dataBean.getCode());//always get new elementID
			String value = generalUtil.getJsonValById(dataBean.getVal(), "value");
			uploadFileDao.saveStringAsClob(elementId, value);
		
			String formcodeEntity = formDao.getFormCodeEntityBySeqId(formCode, formId);
			String elementImpCode_ = dataBean.getCode();
			String table = "fg_s_"+formcodeEntity+"_pivot";
			String sql = "update "+table+"\n"
					+ " set "+elementImpCode_+" = '"+elementId+"'\n"
					+ " where formId = '"+formId+"'";
			formSaveDao.updateStructTableByFormId(sql, table, Arrays.asList(elementImpCode_), formId);
		}
		if (elementId.equals("-1")) {
			throw new Exception(generalUtil.getSpringMessagesByKey("FAILED_SAVE_CLOB",
					"Save failed. Please, try again or call your administrator."));
		}
		return elementId;
	}

	public String renderIreport(long stateKey, String formCode, String impCode, String fileName, String printTemplate,
			String reportType, String catalog, String isDistinct, String title, String subTitle,
			String subReportFileList, String subReportCatalogList, String displayUrl,
			Map<String, String> displayValuesObj, String sessionId) {

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

			for (Map.Entry<String, String> entry : displayValuesObj.entrySet()) {
				String rKey = entry.getKey().replace("$P{", "").replace("}", "");
				String rVal = entry.getValue();
				hmReportParameterList.put(rKey, rVal);
				logger.info("Ireport displayValuesObj parameter key=" + rKey + ", val=" + rVal);
			}
			hmReportParameterList.put("currentUrl", displayUrl);
			hmReportParameterList.put("parameter_DB_URL", DB_URL);
			hmReportParameterList.put("parameter_DB_USER", DB_USER);
			hmReportParameterList.put("parameter_DB_PASSWORD", DB_PASSWORD);
			hmReportParameterList.put("parameter_DIR_JASPER_XML", DIR_JASPER_XML);
			String[] subReportFileArray = generalUtil.getNull(subReportFileList).split(",");
			String[] subReportCatalogArray = generalUtil.getNull(subReportCatalogList).split(",");

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

			if ((!printTemplate.equals("")) && (displayValuesObj != null)) //kd 022018 this for put all data from screen to ireport .xml
			{
				boolean isCreated = false;

				try {
					isCreated = createIreportPrint(fileName, printTemplate, title, subTitle, displayValuesObj);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					generalUtilLogger.logWrite(e);
					e.printStackTrace();
					//return "File  already exist";

				}
				logger.debug("ireport created = " + isCreated);

				//				sql = "select 1 from dual";
				sql = generalUtilFormState.getFormCatalogDBSql(stateKey, formCode, catalog, isDistinct.toLowerCase(),
						impCode, "ALL"); //kd 29032018 made the same as in standart iReport mechanizm
			} else {

				sql = generalUtilFormState.getFormCatalogDBSql(stateKey, formCode, catalog, isDistinct.toLowerCase(),
						impCode, "ALL");
			}
			//kd 09042018 this "if" for exception which is appeared when catalog set to empty value in settings for IREPORT, in form builder.  
			if (sql.equals("")) {
				sql = "select 1 from dual";
			}

			JRDataSource myResultSetDS = new JRResultSetDataSource(generalDao.getResultSet(conn, sql));

			// the call
			jrg = new JasperReportGenerator(myResultSetDS, mLang);
			path_file = jrg.getPath(fileName + (new Date()).getTime(), fileName,
					(reportType.equals("PDF") ? JasperReportType.PDF : JasperReportType.JXL_EXCEL), title, subTitle,
					hmReportReplacerList, hmReportParameterList, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp");

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
			System.out.println("This is exception: " + et); //13122018 kd Temp for debug
			//			try {
			//				response.getWriter().print("<html><head><title>An error happened!</title></head>");
			//				response.getWriter().print("<body>Generate report error!!</body>");
			//				response.getWriter().println("</html>");
			//			} catch (IOException e2) {
			//				// TODO Auto-generated catch block
			//				e.printStackTrace();
			//			}
		} finally {
			generalDao.releaseConnectionFromDataSurce(conn);
		}

		return path_file;
	}

	public void renderIreport(long stateKey, String formCode, String impCode, String fileName, String printTemplate,
			String reportType, String catalog, String catalogadditionaldata, String isDistinct, String title,
			String subTitle, String subReportFileList, String subReportCatalogList, String displayUrl,
			Map<String, String> displayValuesObj, String sessionId, HttpServletResponse response) {
		Connection conn = generalDao.getConnectionFromDataSurce();
		JasperReportGenerator jrg = null;
		try {

			JasperDataSourceSupplier jrdss = new JasperDataSourceSupplier(conn, false);
			String sql = "";
			HashMap<String, String> mLang = new HashMap<String, String>();

			// preparations...
			HashMap<String, String> hmReportReplacerList = new HashMap<String, String>();
			// hmReportReplacerList.put("@replacer1@", " and id = 1 ");
			HashMap<String, Object> hmReportParameterList = new HashMap<String, Object>();

			for (Map.Entry<String, String> entry : generalUtilFormState.getFormParam(stateKey, formCode).entrySet()) {
				String rKey = entry.getKey().replace("$P{", "").replace("}", "");
				String rVal = entry.getValue();
				hmReportParameterList.put(rKey, rVal);
				logger.info("Ireport parameter key=" + rKey + ", val=" + rVal);
			}

			for (Map.Entry<String, String> entry : displayValuesObj.entrySet()) {
				String rKey = entry.getKey().replace("$P{", "").replace("}", "");
				String rVal = entry.getValue();
				hmReportParameterList.put(rKey, rVal);
				logger.info("Ireport displayValuesObj parameter key=" + rKey + ", val=" + rVal);
			}
			hmReportParameterList.put("currentUrl", displayUrl);
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
					if (!generalUtil.getNull(catalog_).equals("") && !generalUtil.getNull(catalog_).equals("na")) {
						sql = generalUtilFormState.getFormCatalogDBSql(stateKey, formCode, catalog_, "0", impCode,
								"ALL");
							hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
									jrdss.createReportDataSource(sql, "JRMapArrayDataSource"));
//						}
						logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
								+ ", catalog_=" + catalog_);
					}
					//add file as parameter
					if (!generalUtil.getNull(file_).equals("")) {
						String fileName_ = file_.replaceAll(".xml", "").toUpperCase();
						String fileTmp_ = fileName_ + sessionId + (new Date()).getTime();
							hmReportParameterList.put("SUB_REPORT_" + fileName_,
									(new jasper.biz.JasperReportGenerator(mLang)).getCompiled(fileTmp_, file_,//kd 05052020 this method uses created .xml (not on flight)
											DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
//						}
						logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase()
								+ ", fileName_=" + fileName_ + ", fileTmp_=" + fileTmp_);
					}
				}
			}

			if ((!printTemplate.equals("")) && (displayValuesObj != null)) //kd 022018 this for put all data from screen to ireport .xml
			{
				boolean isCreated = false;

				try {
					isCreated = createIreportPrint(fileName, printTemplate, title, subTitle, displayValuesObj);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					generalUtilLogger.logWrite(e);
					e.printStackTrace();
					//return "File  already exist";

				}
				logger.debug("ireport created = " + isCreated);

				//				sql = "select 1 from dual";
				sql = generalUtilFormState.getFormCatalogDBSql(stateKey, formCode, catalog, isDistinct.toLowerCase(),
						impCode, "ALL"); //kd 29032018 made the same as in standart iReport mechanizm
			} else {

				sql = generalUtilFormState.getFormCatalogDBSql(stateKey, formCode, catalog, isDistinct.toLowerCase(),
						impCode, "ALL");
			}
			//kd 09042018 this "if" for exception which is appeared when catalog set to empty value in settings for IREPORT, in form builder.  
			if (sql.equals("")) {
				sql = "select 1 from dual";
			}

			JRDataSource myResultSetDS = new JRResultSetDataSource(generalDao.getResultSet(conn, sql));

			// the call
			jrg = new JasperReportGenerator(myResultSetDS, mLang);
			ByteArrayOutputStream out = jrg.getByteArrayOutputStream(fileName + (new Date()).getTime(), fileName,
					(reportType.equals("PDF") ? JasperReportType.PDF : JasperReportType.JXL_EXCEL), title, subTitle,
					hmReportReplacerList, hmReportParameterList, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp");

			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + title + (reportType.equals("PDF") ? ".pdf\";" : ".xls\";"));
			// if(ComplyUtils.getNull(request.getParameter("fileDownloadFlag")).equals("1"))
			// response.setHeader("Set-Cookie", "fileDownload=true; path=/"); //
			// used with fileDownload.js (that must be implemented in each
			// client size to be useful)
			OutputStream stream = response.getOutputStream();
			try {
				out.writeTo(stream);
				stream.flush();
				stream.close();
			} catch (IOException e) {
				generalUtilLogger.logWrite(e);
				response.getWriter().print("<html><head><title>An error happened!</title></head>");
				response.getWriter().print("<body>Generate report error!</body>");
				response.getWriter().println("</html>");
				// System.out.println("report close by the user");
			}
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
			System.out.println("This is exception: " + ex); //29032018 kd Temp for debug
			// System.out.println("SystemReportsServlet.PasswordManagementReport
			// action: " + ex.toString()); 
			try {
				response.getWriter().print("<html><head><title>An error happened!</title></head>");
				response.getWriter().print("<body>Generate report error!!</body>");
				response.getWriter().println("</html>");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (OutOfMemoryError er) {
			Exception e = new Exception(
					"OutOfMemoryError caught in 'public void renderIreport' function in FormApiService");
			generalUtilLogger.logWrite(e);
			// System.out.println("SystemReportsServlet.PasswordManagementReport
			// action: " + er.toString());
			try {
				response.getWriter().print("<html><head><title>An error happened!</title></head>");
				response.getWriter().print("<body>Generate report error!!!</body>");
				response.getWriter().println("</html>");
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		} catch (NoSuchMethodError et) {
			Exception e = new Exception(
					"NoSuchMethodError caught in 'public void renderIreport' function in FormApiService");
			generalUtilLogger.logWrite(e);
			generalUtilLogger.logWrite(et);
			System.out.println("This is exception: " + et); //13122018 kd Temp for debug
			try {
				response.getWriter().print("<html><head><title>An error happened!</title></head>");
				response.getWriter().print("<body>Generate report error!!</body>");
				response.getWriter().println("</html>");
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			generalDao.releaseConnectionFromDataSurce(conn);
		}
	}
	
	/*
	 * kd 03112019
	 * TASK-24488 Sample Labels - Print. Print labels from list of Samples
	 * 
	 */
	public void renderIreportSample(String formCode, String impCode, String fileName, String printTemplate,
			String reportType, String catalog, String catalogadditionaldata, String isDistinct, String title,
			String subTitle, String subReportFileList, String subReportCatalogList, String displayUrl,
			String displayValuesObj,
			String sessionId, HttpServletResponse response) {
		Connection conn = generalDao.getConnectionFromDataSurce();
		JasperReportGenerator jrg = null;
		try {
			HashMap<String, String> mLang = new HashMap<String, String>();

			// preparations...
			HashMap<String, String> hmReportReplacerList = new HashMap<String, String>();
			HashMap<String, Object> hmReportParameterList = new HashMap<String, Object>();

			String sql_ = "select s.* from FG_R_OUTPUTLABEL_V s where s.SAMPLE_ID in (" + displayValuesObj + ")";
			
			hmReportParameterList.put("parameter_DB_URL", DB_URL);
			hmReportParameterList.put("parameter_DB_USER", DB_USER);
			hmReportParameterList.put("parameter_DB_PASSWORD", DB_PASSWORD);
			hmReportParameterList.put("parameter_DIR_JASPER_XML", DIR_JASPER_XML);

			JRDataSource myResultSetDS = new JRResultSetDataSource(generalDao.getResultSet(conn, sql_));

			// the call
			jrg = new JasperReportGenerator(myResultSetDS, mLang);
			ByteArrayOutputStream out = jrg.getByteArrayOutputStream(fileName + (new Date()).getTime(), fileName,
					(reportType.equals("PDF") ? JasperReportType.PDF : JasperReportType.JXL_EXCEL), title, subTitle,
					hmReportReplacerList, hmReportParameterList, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp");

			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition",
					"attachment; filename=\"" + title + (reportType.equals("PDF") ? ".pdf\";" : ".xls\";"));
			
			OutputStream stream = response.getOutputStream();
			try {
				out.writeTo(stream);
				stream.flush();
				stream.close();
			} catch (IOException e) {
				generalUtilLogger.logWrite(e);
				response.getWriter().print("<html><head><title>An error happened!</title></head>");
				response.getWriter().print("<body>Generate report error!</body>");
				response.getWriter().println("</html>");
			}
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
			System.out.println("This is exception: " + ex); 
			try {
				response.getWriter().print("<html><head><title>An error happened!</title></head>");
				response.getWriter().print("<body>Generate report error!!</body>");
				response.getWriter().println("</html>");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (OutOfMemoryError er) {
			Exception e = new Exception(
					"OutOfMemoryError caught in 'public void renderIreportSample' function in FormApiService");
			generalUtilLogger.logWrite(e);
			try {
				response.getWriter().print("<html><head><title>An error happened!</title></head>");
				response.getWriter().print("<body>Generate report error!!!</body>");
				response.getWriter().println("</html>");
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		} catch (NoSuchMethodError et) {
			Exception e = new Exception(
					"NoSuchMethodError caught in 'public void renderIreportSample' function in FormApiService");
			generalUtilLogger.logWrite(e);
			generalUtilLogger.logWrite(et);
			System.out.println("This is exception: " + et);
			try {
				response.getWriter().print("<html><head><title>An error happened!</title></head>");
				response.getWriter().print("<body>Generate report error!!</body>");
				response.getWriter().println("</html>");
			} catch (IOException e2) {
				e.printStackTrace();
			}
		} finally {
			generalDao.releaseConnectionFromDataSurce(conn);
		}
	}
	
	public String getSpecificTreeRoot(String metaData, String view, String wherePartFormId,String parentFormId,Long stateKey) {
		return getTreeChildren(metaData,view,wherePartFormId,"","",metaData,parentFormId,stateKey,"");
	}

	
	public String getTreeRoot(String view) {
		List<String> dataList = formDao.getTreeData("ROOT", view, "","");
		JSONArray ja = new JSONArray();
		JSONObject jo, info;
		//String[] dataArray = dataCSV.split(",");
		boolean children = false;
		if (dataList.isEmpty()) {//when no data	
			dataList.add("@-1@Expand Project@NA");
			//dataArray[0] = "@-1@Expand Project@NA";
		} else {
			children = true;
		}
		for (String data : dataList) {
			info = new JSONObject();
			info.put("child", data.split(delimiter)[3]);
			info.put("struct", data.split(delimiter)[0]);
			
			//kd 23122020 task: add tooltip for experiment name + description
			if (data.split(delimiter)[2].length() > treeLenghtOfNamesLimit) {
				info.put("title",data.split(delimiter)[2]);
			}
			
			jo = new JSONObject();
			jo.put("id", data.split(delimiter)[1]);
			if (data.split(delimiter)[2].length() > treeLenghtOfNamesLimit) { //kd 23122020 added condition and row for 1st case
				jo.put("text", data.split(delimiter)[2].substring(0, treeLenghtOfNamesLimit));
			} else {
				jo.put("text", data.split(delimiter)[2]);
			}

			jo.put("a_attr", info);
			jo.put("children", children);
			ja.put(jo);
		}
		return ja.toString();
	}

	public String getTreeChildren(String column, String view, String id, String parents, String structs,
			String currentstruct,String currentformId,Long stateKey,
			String criteria) {
		JSONArray ja = new JSONArray();
		JSONObject jo, info;
		String[] parentsArray = parents.split(",");
		String[] structsArray = structs.split(",");
		int parentsArrayLength = (parents.equals("")) ? 0 : parentsArray.length;
		StringBuilder wherePart = new StringBuilder();
		String curId = "";
		String addQuotes = "";
		try {
			if (column.isEmpty() && !generalDao.checkIfColumnExists(view, currentstruct)) {
				Form form = formDao.getFormInfoLookup(currentstruct, "%", true).get(0);
				String formCodeEntity = generalUtil.getNull(form.getFormCodeEntity(), currentstruct);
				currentstruct = formCodeEntity;
			}
			
			for (int i = 0; i < parentsArrayLength; i++) {
				curId = (parentsArray[i].indexOf("_node_") != -1)
						? parentsArray[i].substring(0, parentsArray[i].indexOf("_node_")) : parentsArray[i];
				addQuotes = structsArray[i];
				addQuotes = ((addQuotes.indexOf(" ") != -1) && (addQuotes.indexOf("\"") != 0)) ? "\"" + addQuotes + "\""
						: addQuotes;
				wherePart.append(" and " + addQuotes + " like '%" + delimiter + curId + delimiter + "%'");
			}
			curId = (id.indexOf("_node_") != -1) ? id.substring(0, id.indexOf("_node_")) : id;
			addQuotes = currentstruct;
			addQuotes = ((addQuotes.indexOf(" ") != -1) && (addQuotes.indexOf("\"") != 0)) ? "\"" + addQuotes + "\""
					: addQuotes;
			wherePart.append(" and " + addQuotes + " like '%" + delimiter + curId + delimiter + "%'");

			if (column.isEmpty()) {//trying to get the specific record
				String sqlGetChild = "select distinct " + currentstruct + " from " + view + " where 1=1 "+wherePart;
				String getRecordForChild = generalDao.selectSingleString(sqlGetChild);
				column = getRecordForChild.split(delimiter)[3];
			}
			
			if((column.equalsIgnoreCase("Project") || column.equalsIgnoreCase("SubProject")) && view.equalsIgnoreCase("FG_I_TREE_CONNECTION_V")){
				view = "fg_i_tree_connection_proj_v";
				if (column.equalsIgnoreCase("Project") && criteria != null && !criteria.isEmpty() && !criteria.equalsIgnoreCase("all")) {
					
					Map<String, String> sqlParam = new HashMap<String,String>();
					sqlParam.put("$P{STRUCT}", "Project");
					sqlParam.put("$P{USERID}", generalUtil.getSessionUserId());
					String creiteriaWherePart = generalUtilConfig.getCriterialSql("Project", criteria, "Main", sqlParam, null);
										
					wherePart.append(" and project_id in (" + creiteriaWherePart + ") ");
				}
			}
			String path = "";
			try {
				String parentFCode = formDao.getFormCodeBySeqId(currentformId);
				if (stateKey != null && !generalUtil.getNull(parentFCode).isEmpty()) {
					path = generalUtilFormState.getFormParam(stateKey, parentFCode, "$P{FORMPATH}");
				}
			}
			catch(Exception e){
				path = "";
			}
			List<String> dataList = formDao.getTreeData(column, view, wherePart.toString(),path);
			//String[] dataArray = dataList.split(",");

			Map<String, String[]> dataArrayOrderMap = new TreeMap<String, String[]>(); // using TreeMap for having a sort collection by key
			for (String data_ : dataList) {
				String[] recordArray = data_.split(delimiter);
				if ((recordArray.length == 4) && !(recordArray[1].equals("")) && !(recordArray[2].equals(""))) {
					String keyOrder_ = recordArray[2] + delimiter + recordArray[1];
					dataArrayOrderMap.put(keyOrder_.trim(), recordArray);//trim- in order that the order of the items will not be mess by a leading whitespace(a string with a leading whitespace shall be firstly ordered)
				} else if((recordArray.length == 5) && !(recordArray[1].equals("")) && !(recordArray[2].equals(""))){//adib 16072020 fixed bug 8372->The fifth element represents the order in which the items should be ordered by
					String keyOrder_ = recordArray[4];
					dataArrayOrderMap.put(keyOrder_.trim(), recordArray);//trim- in order that the order of the items will not be mess by a leading whitespace(a string with a leading whitespace shall be firstly ordered)
				}
			}
			
			if (dataArrayOrderMap != null) {
				for (Map.Entry<String, String[]> entry : dataArrayOrderMap.entrySet()) {
					String[] recordArray = entry.getValue();
					info = new JSONObject();
					info.put("formId", recordArray[1]);
					info.put("struct", recordArray[0]);
					info.put("child", recordArray[3]);
					if (recordArray[2].length() > treeLenghtOfNamesLimit) {  //kd 23122020
						info.put("title", recordArray[2]); 
					}

					jo = new JSONObject();
					jo.put("id", recordArray[2] + "_node_" + new Date().getTime() + "_" + recordArray[1]);
					if (recordArray[2].length() > treeLenghtOfNamesLimit) { //kd 23122020 added condition and row for 1st case
						jo.put("text", recordArray[2].substring(0, treeLenghtOfNamesLimit));
					} else {
						jo.put("text", recordArray[2]);
					}
					jo.put("a_attr", info);
					jo.put("children", true);
					ja.put(jo);
				}
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(LevelType.ERROR,
					"Error in getting tree children for struct " + currentstruct + "; formId = " + curId, "",
					ActivitylogType.GetTreeNodes, null, e);
			jo = new JSONObject();
			String errorMessage = generalUtil.getSpringMessagesByKey(String.valueOf("ERROR_GETTING_TREE"),
					new Object[] { currentstruct }, "ERROR_GETTING_TREE");
			jo.put("error", errorMessage);
			ja.put(jo);
		}
		return ja.toString();
	}

	public String getWebixAnalytCalcUpdatedData(String parentID, String domId) {
		JSONObject returnObject = new JSONObject();
		JSONObject tableObject = new JSONObject();
		JSONArray arr = new JSONArray();
		String tabledivhtml = "", tabledivid = "", tableid = "";

		List<JSONArray> fullDataArr = uploadFileDao.getWebixAnalytCalcData(parentID, true);
		for (int i = 0; i < fullDataArr.get(1).length(); i++) {
			List<JSONObject> currTableData = new ArrayList<JSONObject>();
			tableObject = new JSONObject();
			tabledivid = "";
			tableid = "";

			JSONArray table = fullDataArr.get(1).getJSONArray(i);
			String tableID = table.getString(0);
			String isBasic = table.getString(1);
			String tableHeader = table.getString(2);

			JSONArray rows = fullDataArr.get(0);
			for (int j = 0; j < rows.length(); j++) {
				JSONObject obj = rows.getJSONObject(j);
				if (obj.get("materialid").equals(tableID)) {
					currTableData.add(obj);
				}
			}

			tabledivid = "tableDiv_" + domId + "_" + i;
			tableid = "tableID_" + domId + "_" + i;
			tabledivhtml += "<div style=\"margin-top: 15px;\">"
					+ "<div class=\"webix_container\" isWebixTableHidden=\"false\" isBasic=\"" + isBasic
					+ "\" childTableID=\"" + tableid + "\" id=\"" + tabledivid + "\">" + "<h2 class=\"cssStaticData\">"
					+ tableHeader + "</h2>" + "</div>" + "</div>\n";

			tableObject.put("tableID", tableid);
			tableObject.put("tableDivID", tabledivid);
			tableObject.put("tableData", currTableData.toString());

			arr.put(tableObject);
		}
		if (tabledivhtml.equals("")) {
			tableObject = new JSONObject();
			tabledivid = "tableDiv_" + domId + "_0";
			tableid = "tableID_" + domId + "_0";
			tabledivhtml += "<div style=\"margin-top: 15px;\">"
					+ "<div class=\"webix_container\" isWebixTableHidden=\"false\" isBasic=\"\" childTableID=\""
					+ tableid + "\" id=\"" + tabledivid + "\">" + "<h2 class=\"cssStaticData\"></h2>" + "</div>"
					+ "</div>\n";
			tableObject.put("tableID", tableid);
			tableObject.put("tableDivID", tabledivid);
			tableObject.put("tableData", "[]");

			arr.put(tableObject);
		}
		returnObject.put("allTablesDiv", tabledivhtml);
		returnObject.put("tablesToInit", arr);

		return returnObject.toString();
	}
	public String getWebixCalcUpdatedData(String parentID, String domId, String objType){
		return getWebixCalcUpdatedData(parentID, domId, objType,"");
	}

	public String getWebixCalcUpdatedData(String parentID, String domId, String objType,String sampleId) {
		JSONObject returnObject = new JSONObject();

		JSONArray fullDataArr = new JSONArray();
		if (objType.equals("formulation")) {
			fullDataArr = uploadFileDao.getWebixFormulCalcData(parentID);
		} else if (objType.equals("experimentStep")) {
			fullDataArr = uploadFileDao.getWebixExperimentStepCalcData(parentID);
		} else if (objType.equals("massBalance")) {
			fullDataArr = uploadFileDao.getWebixMassBalanceCalcData(parentID);
		}else if(objType.equals("massBalanceResult")){
			fullDataArr = uploadFileDao.getWebixMassBalanceResCalcData(parentID,sampleId);
		}
		String tabledivid = "tableDiv_" + domId;
		String tableid = "tableID_" + domId;
		String tabledivhtml = "<div class=\"webix_container\" isWebixTableHidden=\"false\" childTableID=\"" + tableid
				+ "\" id=\"" + tabledivid + "\" style=\"margin-top: 15px;\"></div>";

		returnObject.put("tableID", tableid);
		returnObject.put("tableDivID", tabledivid);
		returnObject.put("tableDiv", tabledivhtml);
		returnObject.put("tableData", fullDataArr.toString());

		return returnObject.toString();
	}
	
	public String getWebixMassBalanceStepsInfoData(String parentID, String domId, String formCode, String runNumber) {
		JSONObject returnObject = new JSONObject();
				
		String query = "select distinct t.formid as stepid, t.stepname, t.webixmassbalancetable, nvl(t.chkcharactermassbalance,0) as isCharacterMassBalance \n"
						+" from FG_S_STEP_ALL_V t"
						+ " where nvl(t.webixmassbalancetable,'0') != '0' and t.experiment_id = '" + parentID +"'"
						+ (!generalUtil.getNull(runNumber).isEmpty()? " and RUNNUMBERDISPLAY = '"+runNumber+"'":"")
						+" order by t.stepname";
		try {
			List<Map<String, Object>> rsList = generalDao.getListOfMapsBySql(query);
			for (Map<String, Object> rsData : rsList) {
				String stepname = rsData.get("stepname").toString();
				String stepid = rsData.get("stepid").toString();
				String stepMBId = rsData.get("webixmassbalancetable").toString();
				String isCharacterMassBalance = rsData.get("isCharacterMassBalance").toString();
				
				JSONArray stepArr = new JSONArray();				
				String fullData = generalUtilFormState.getStringContent(stepMBId, formCode, domId, parentID);
				if(!fullData.equals(""))
				{
					String tabledivhtml = "", tableid = "", tabledivid = "";
					try 
					{
						JSONArray arr = new JSONArray(fullData);
						for(int i=0;i<arr.length();i++)
						{
							JSONObject dataObj = arr.getJSONObject(i);		
							String id = domId + "_"+stepid+"_"+(i+1);
							tabledivid = "tableDiv_"+id;
							tableid = "tableID_"+id;
																				
							tabledivhtml += "<div class=\"webix_container\" childTableID=\""+tableid+"\" id=\""+tabledivid+"\" style=\"margin-bottom: 20px;width: 100%;float: left;\"></div>";
							
							JSONObject currObject = new JSONObject();
							currObject.put("tableID", tableid);
							currObject.put("tableDivID", tabledivid);
							currObject.put("tableDiv", tabledivhtml);
							currObject.put("tableData", dataObj.toString());
						
							stepArr.put(currObject);
						}
						if(stepArr.length() > 0)
						{						
							returnObject.put(stepid, new JSONArray().put(isCharacterMassBalance).put(stepname).put(stepArr));
						}
				
					} 
					catch (Exception e) {
						generalUtilLogger.logWrite(e);
						e.printStackTrace();
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			generalUtilLogger.logWrite(e);
		}
		
		
		return returnObject.toString();
	}

	private boolean createIreportPrint(String fileName, String printTemplate, String reportTitle,
			String reportFilterDisplay, Map<String, String> mapFields) throws Exception {
		boolean isSuccess = false;
		HashMap<String, String> mLang = null;
		boolean isPortrait = true;
		if (printTemplate.contains("Landscape")) {
			isPortrait = false;
		}
		File targetFile = new File(DIR_JASPER_XML + "/" + fileName);
		if (!targetFile.exists()) {
			String reportTemplate = "";

			// get xml template
			FileInputStream input = new FileInputStream(
					new File(DIR_JASPER_XML + "/Template/" + printTemplate + ".xml")); //(isPortrait ? PORTRAIT_XML : LANDSCAPE_XML)));
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (!line.equals("			<band height=\"100\"  isSplitAllowed=\"true\" >")
						&& !line.equals("		<background>")) {
					sb.append(line + "\n");
				} else if (line.equals("		<background>")) {
					sb.append(createXmlRowsParametersAccordingMap(mapFields, line));
				} else if (line.equals("			<band height=\"100\"  isSplitAllowed=\"true\" >")) {
					sb.append(createXmlRowsAccordingMap(mapFields, line, isPortrait));
				}
			}
			reportTemplate = sb.toString();

			// config parameters and replacers
			HashMap<String, String> hmReplacers = new HashMap<String, String>();
			hmReplacers.put("@replacerAll@", reportTemplate);

			//..(code form report generator)
			InputStream inputModified = null;
			//			JasperDesign design = null;
			GeneralBiz generalBiz = new GeneralBiz();

			//xml
			input = new FileInputStream(new File(DIR_JASPER_XML + "\\rATPlaceHolder"));
			inputModified = generalBiz.InputStreamModified(DIR_JASPER_XML, input, hmReplacers, mLang, fileName);
			generalBiz.copyFileUsingFileStreams(inputModified, targetFile);

			input.close();
			inputModified.close();
			isSuccess = true;
		}
		return isSuccess;
	}

	private String createXmlRowsParametersAccordingMap(Map<String, String> map, String line) {
		StringBuilder sb = new StringBuilder();
		//		boolean isLabel = true; // first element for label, second for parameter. Checking if parameter, then put
		for (Map.Entry<String, String> entry : map.entrySet()) {
			//			sb.append("	<parameter name=\"" + entry.getValue() + "\" isForPrompting=\"false\" class=\"java.lang.String\"/>\n");
			//			if (!isLabel) {
			if (!entry.getKey().contains("IREPORT_PRINT_L")) {

				sb.append("	<parameter name=\"" + entry.getKey()
						+ "\" isForPrompting=\"false\" class=\"java.lang.String\"/>\n");
				//				isLabel = true;
			} else {
				//				isLabel = false;
			}
		}
		return sb.toString() + line;
	}

	private String createXmlRowsAccordingMap(Map<String, String> map, String line, boolean isPortrait) {
		StringBuilder sb = new StringBuilder();
		int x01 = 0, x02 = 127, y = 6, keyNo = 13, width = 151;
		boolean isFirstColumn = true, doNextRow = false, wasLabel = false; // first element for label, second for parameter. Checking if parameter, then put
		if (!isPortrait) // kd21022017
		{
			x01 = 402;
			x02 = 529;
			width = 275;
		}
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (entry.getKey().contains("IREPORT_PRINT_L")) {
				if (doNextRow) {
					doNextRow = false;
					y += 21;
				}
				if (isFirstColumn) {
					isFirstColumn = false;
					x01 = 0;
					x02 = 127;

				} else {
					isFirstColumn = true;
					if (!isPortrait) // kd21022017
					{
						x01 = 402;
						x02 = 529;
					} else {
						x01 = 278;
						x02 = 405;
					}
					doNextRow = true;
				}
				sb.append("			<staticText>\n" + "					<reportElement\n"
						+ "						x=\"" + x01 + "\"\n" + "						y=\"" + y + "\"\n"
						+ "						width=\"127\"\n" + "						height=\"15\"\n"
						+ "						key=\"staticText-" + keyNo + "\"/>\n"
						+ "					<box></box>\n" + "					<textElement>\n"
						+ "						<font/>\n" + "					</textElement>\n"
						+ "				<text><![CDATA[" + entry.getValue() + "]]></text>\n" //Static Text
						+ "			</staticText>\n");
				wasLabel = true;
			} else if (!entry.getKey().contains("IREPORT_PRINT_L") && wasLabel) {
				sb.append(
						"				<textField isStretchWithOverflow=\"false\" isBlankWhenNull=\"false\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >\n"
								+ "					<reportElement\n" + "						x=\"" + x02 + "\"\n"
								+ "						y=\"" + y + "\"\n" + "						width=\"" + width
								+ "\"\n" + "						height=\"15\"\n"
								+ "						key=\"textField-" + keyNo + "\"/>\n"
								+ "					<box></box>\n" + "					<textElement>\n"
								+ "						<font/>\n" + "					</textElement>\n"
								+ "				<textFieldExpression   class=\"java.lang.String\"><![CDATA[$P{"
								+ entry.getKey() + "}]]></textFieldExpression>\n" + "				</textField>\n");
				keyNo++;
				wasLabel = false;
			}
		}

		int bandHeight = y + 15 + 6;
		return line.replace("100", String.valueOf(bandHeight)) + "\n" + sb.toString();
	}

	public String updateWebixMassBalanceSamplesList(String samplesScope, String parentID, String runNumber) {
		JSONObject returnObject = new JSONObject();
		JSONArray dataArr = uploadFileDao.updateWebixMassBalanceSamplesList(samplesScope, parentID,runNumber);
		returnObject.put("listdata", dataArr.toString());
		return returnObject.toString();
	}
	
	public String getTooltipForWebixMassBalanceSamplesField(String sampleID) {
		String commentAsTooltip = uploadFileDao.getTooltipForWebixMassBalanceSamplesField(sampleID);
		return commentAsTooltip;
	}
	
	public String updateWebixResultTypeList(List<DataBean> dataBeanList) {
		JSONObject returnObject = new JSONObject();
		JSONArray dataArr = uploadFileDao.updateWebixResultTypeList(initElementValueMapByBeanList(dataBeanList));
		returnObject.put("listdata", dataArr.toString());
		return returnObject.toString();
	}

	private Map<String, String> initElementValueMapByBeanList(List<DataBean> dataBeanList) {
		Map<String, String> elementValueMap = new HashMap<String, String>();
		for (DataBean dataBean : dataBeanList) {
			elementValueMap.put(dataBean.getCode(), generalUtil.getNull(dataBean.getVal()));
		}
		return elementValueMap;
	}

	public ActionBean updateElementBody(long stateKey, String formCode, String formId, String elementName, String value) {
		String retVal = "";
		try {
			Element elem =generalUtilFormState.getElementBean(stateKey, formCode, elementName);
			retVal = elem.getHtmlBody(stateKey, formId, false, value, elementName, null, "");
			return new ActionBean("no action needed", generalUtil.StringToList(retVal), "");
		} catch (Exception ex) {
			return new ActionBean("no action needed", generalUtil.StringToList(retVal), ex.getMessage());
		}
	}

	public String getTreeSearchResult(String searchText, String searchCriteria, String userId, long stateKey) throws Exception {

//		searchText = "a";
		Connection conn = generalDao.getConnectionFromDataSurce();
		SearchTreeBeanList resultList = new SearchTreeBeanList();
		
		// init list
		resultList.add(new SearchTreeBean("-1","#","Expand Project", "-1", "Root", ""));
		
		Stack<String> pathStack = new Stack<String>();		
		Statement stmt = null;
		ResultSet rs = null;
		String creiteriaSql = "";
		String creiteriaSqlExperiment = "";
		try {
			List<String> projectFilterList = null;
			if(!generalUtil.getNull(searchCriteria).equals("") && !searchCriteria.equalsIgnoreCase("all")) {
				Map<String, String> sqlParam = new HashMap<String,String>();
				sqlParam.put("$P{STRUCT}", "Project");
				sqlParam.put("$P{USERID}", generalUtil.getSessionUserId());
				creiteriaSql = generalUtilConfig.getCriterialSql("Project", searchCriteria, "Main", sqlParam, null);
				projectFilterList = generalDao.getListOfStringBySql(creiteriaSql);
				creiteriaSqlExperiment = "select e.experiment_id from fg_s_experiment_v e where e.project_id in (" + creiteriaSql + ")";
			}
			
			//**** get all path jsons and all of the id's inside them.
			String sql = "";
			String userName = generalUtil.getSessionUserName();
			String removePermissions = "";
			if(userName != null && userName.equalsIgnoreCase("admin") || userName.equalsIgnoreCase("system")) { // admin and system do not need permission
				removePermissions = "--";
			} 
			
			sql =   "with search_result as (\r\n" + 
					"  select 'Project' as formcode_entity, t.project_id as id, t.projectname as name\r\n" + 
					"  from fg_s_project_v t\r\n" + 
					"  where INSTR(LOWER( t.projectname),LOWER('" + searchText + "')) > 0\r\n" + 
					"  " + removePermissions + " and exists (select 1 from fg_s_project_permlist_v p where p.id = t.formid and p.user_id = " + userId + ")\r\n" + 
					(creiteriaSql.equals("")?"":"  and t.project_id in (" + creiteriaSql + ") \r\n") +
					"  union all\r\n" + 
					"  select 'SubProject' as formcode_entity, t.subproject_id as id, t.subprojectname as name\r\n" + 
					"  from fg_s_subproject_v t\r\n" + 
					"  where INSTR(LOWER( t.subprojectname),LOWER('" + searchText + "')) > 0\r\n" + 
					"  " + removePermissions + " and exists (select 1 from fg_s_subproject_permlist_v p where p.id = t.formid and p.user_id = " + userId + ")\r\n" + 
					(creiteriaSql.equals("")?"":"  and t.project_id in (" + creiteriaSql + ") \r\n") +
					"  union all\r\n" + 
					"  select 'SubSubProject' as formcode_entity, t.subsubproject_id as id, t.subsubprojectname as name\r\n" + 
					"  from fg_s_subsubproject_v t\r\n" + 
					"  where INSTR(LOWER( t.subsubprojectname),LOWER('" + searchText + "')) > 0\r\n" + 
					"  " + removePermissions + " and exists (select 1 from fg_s_subsubproject_permlist_v p where p.id = t.formid and p.user_id = " + userId + ")\r\n" + 
					(creiteriaSql.equals("")?"":"  and t.project_id in (" + creiteriaSql + ") \r\n") +
					"  union all\r\n" + 
					"  select 'Experiment' as formcode_entity, t.experiment_id as id, t.FORMNUMBERID || ' ' || t.DESCRIPTION as name\r\n" + 
					"  from fg_s_experiment_v t\r\n" + 
					"  where INSTR(LOWER( t.FORMNUMBERID || ' ' || t.DESCRIPTION),LOWER('" + searchText + "')) > 0\r\n" + 
					"  " + removePermissions + " and exists (select 1 from fg_s_experiment_permlist_v p where p.id = t.formid and p.user_id = " + userId + ")\r\n" + 
					(creiteriaSql.equals("")?"":"  and t.project_id in (" + creiteriaSql + ") \r\n") +
					"  union all\r\n" + 
					"  select 'Step' as formcode_entity, t.step_id as id, t.stepname as name\r\n" + 
					"  from fg_s_step_v t\r\n" + 
					"  where INSTR(LOWER( t.stepname),LOWER('" + searchText + "')) > 0\r\n" + 
					"  " + removePermissions + " and exists (select 1 from FG_S_EXP_INHERIT_PERMLIST_V p where p.id = t.formid and p.user_id = " + userId + ")\r\n" + 
					(creiteriaSqlExperiment.equals("")?"":"  and t.experiment_id in (" + creiteriaSqlExperiment + ") \r\n") +
					"  union all\r\n" + 
					"  select 'Action' as formcode_entity, t.action_id as id, t.actionname as name\r\n" + 
					"  from fg_s_action_v t\r\n" + 
					"  where INSTR(LOWER( t.actionname),LOWER('" + searchText + "')) > 0\r\n" + 
					"  " + removePermissions + " and exists (select 1 from FG_S_EXP_INHERIT_PERMLIST_V p where p.id = t.experiment_id and p.user_id = " + userId + ")\r\n" + 
					(creiteriaSqlExperiment.equals("")?"":"  and t.experiment_id in (" + creiteriaSqlExperiment + ") \r\n") +
					"  union all\r\n" + 
					"  select t.formcode as formcode_entity, to_number(t.formid) as id, t.typename as name\r\n" + 
					"  from fg_i_stest_workup_v t\r\n" + 
					"  where INSTR(LOWER( t.typename),LOWER('" + searchText + "')) > 0\r\n" + 
					"  " + removePermissions + " and exists (select 1 from FG_S_EXP_INHERIT_PERMLIST_V p where p.id = t.experiment_id and p.user_id = " + userId + ")\r\n" + 
					(creiteriaSqlExperiment.equals("")?"":"  and t.experiment_id in (" + creiteriaSqlExperiment + ") \r\n") +
					") \r\n" + 
					"select f.formpath \r\n" + 
					"from fg_sequence f, search_result r\r\n" + 
					"where f.id = r.id and f.formpath is not null \r\n" + 
					"order by f.id desc"; 
			
			generalUtilLogger.logWrite(LevelType.INFO, "searct tree sql: " + sql, "-1", ActivitylogType.SQLEvent, null);

			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				final int batchSize = 100;
				int count = 0;

				// insert into FG_TREE_SEARCH_ID_TMP (temp table commit scope)
				PreparedStatement u = conn.prepareStatement( "insert into FG_TREE_SEARCH_ID_TMP(id) values(?)");

				while (rs.next()) { 
					String path = "";
					try {
						path = generalUtil.getNull(rs.getString("formpath")).replaceAll("\\n", "<br>");
						JSONObject json = new JSONObject(path);
						JSONArray pathListArry = json.getJSONArray("path");
						for (int i = 0; i < pathListArry.length(); i++) { 
							String id_ = (String)pathListArry.getJSONObject(i).get("id");
							if(id_ != null && !id_.isEmpty()) {
								u.setString(1, id_);
								u.addBatch();
								++count;
							}
						}
					} catch (Exception e) {
						generalUtilLogger.logWrite(LevelType.WARN, "search tree path: " + path + " is invalid (will be ignored)", "-1", ActivitylogType.GeneralError, null,e);
						path = "";
					}

					if (count % batchSize == 0) {
						//System.out.println("-----getTreeSearchResult exe count rows: " + batchSize);
						u.executeBatch();
						u.clearBatch();
					}
					
					if(!generalUtil.getNull(path).equals("") && (treeSearchSizeLimit < 0 || pathStack.size() < treeSearchSizeLimit)) {
						pathStack.push(path);
					} else {
						break;
					}

				}
				u.executeBatch();
				u.clearBatch();
			} finally {
				try {
					if (stmt != null) {
						stmt.close();
						rs.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//**** Done! get all path jsons and all of the id's inside them.
			
			//use the FG_TREE_SEARCH_ID_TMP ids for holding the current data of the path objects (in the path (json) it is update just by the night job and to take it from each authn object sql it will take more time)
			sql = "select t.id, t.tree_search as name, \r\n" + 
					"       t.formcode_entity,\r\n" + 
					"       decode(s.runnumberdisplay,null,null,'{\"type\":\"run\",\"experiment_id\":\"' || s.experiment_id || '\",\"run_number\": \"' || s.runnumberdisplay || '\"}') as adhoc_josn\r\n" + 
					"from FG_I_TREE_SEARCH_V t, FG_S_STEP_ALL_V S, FG_TREE_SEARCH_ID_TMP f\r\n" + 
					"where f.id = t.id\r\n" + 
					"and t.id = s.step_id(+)";
			System.out.println("check sql =" + sql);
			Map<String,SearchTreeCurrentData> currentDataMap = new HashMap<String,SearchTreeCurrentData>();
		    rs = generalDao.getResultSet(conn, sql);
			while (rs.next()) {
				String id_ = rs.getString("id");
				String name_ = rs.getString("name");
				String formcode_entity_ = rs.getString("formcode_entity");
				String adhoc_josn_ = rs.getString("adhoc_josn");
				JSONObject obj = null;
				if(adhoc_josn_ != null && !adhoc_josn_.isEmpty()) {
					try {
						obj = new JSONObject(adhoc_josn_);
					} catch (Exception e) {
						// Do nothing
					}
				}
				currentDataMap.put(id_, new SearchTreeCurrentData(id_,name_,formcode_entity_,obj));
			}
			//System.out.println("done map");
			
			while(!pathStack.empty()) {
				String path = pathStack.pop();
				if (!generalUtil.getNull(path).isEmpty()) {
					String parentIdHolder = "-1";
					JSONObject json = new JSONObject(path);
					JSONArray pathListArry = json.getJSONArray("path");
					for (int i = 0; i < pathListArry.length(); i++) {  
						try {
							JSONObject p = pathListArry.getJSONObject(i);
							String[] detailsToDisplay = (p.get("name").toString()).split(":");
							String id = p.get("id").toString();
							String name = detailsToDisplay.length > 1 ? detailsToDisplay[1] : "";
							String formC = detailsToDisplay.length > 1 ? detailsToDisplay[0] : "";
							JSONObject jAdhocParent = null;
							String formCEntity = formC;
							
							//skip filtered projects
							if(formC.equals("Project") && projectFilterList != null && !projectFilterList.contains(id)) {
								break;
							} 
							
							// replace with the current
							SearchTreeCurrentData searchTreeCurrentData = currentDataMap.get(id);
							if(searchTreeCurrentData != null) {
								name = generalUtil.getNull(searchTreeCurrentData.getText(),name);
								formCEntity = generalUtil.getNull(searchTreeCurrentData.getFormcode_entity(),formCEntity);
								jAdhocParent = searchTreeCurrentData.getAdhoc_json();
							}
							
							//adhoc parent handling...
							if(jAdhocParent != null) {
								//jAdhocParent type run ....
								if(jAdhocParent.get("type").toString().equalsIgnoreCase("run")) {
									String experiment_id = jAdhocParent.get("experiment_id").toString();
									String run_number = jAdhocParent.get("run_number").toString();
									//String adhocExperimentId = adhoc_parent.split("#")[0];
									String adhocName = "Run " + run_number;
									String adhocStruct = "Run";
									String adhocId = experiment_id + "#" + run_number;
									resultList.add(new SearchTreeBean(adhocName+"_node_"+adhocId,parentIdHolder,adhocName, adhocId, adhocStruct, ""));
									parentIdHolder = adhocName+"_node_"+adhocId;
								}
							}
							
							// cut name in case it's exceed limit - treeLenghtOfNamesLimit
							String cutName = name;
							String tooltip = "";
							if (name.length() > treeLenghtOfNamesLimit) {  //kd 23122020
								cutName = name.substring(0, treeLenghtOfNamesLimit);
								tooltip = name;
							}
							
							// will add if not added in previous iteration
							resultList.add(new SearchTreeBean(name+"_node_"+id,parentIdHolder,cutName, id, formCEntity, tooltip));
							
							if (!id.equals("#") && !id.equals("-1")) {
								parentIdHolder = name+"_node_" + id;
							} else {
								parentIdHolder = id;
							}
						} catch (Exception e) {
							generalUtilLogger.logWrite(LevelType.WARN, "search tree exception: " + e.getMessage() +", path: " + path, "-1", ActivitylogType.GeneralError, null, e);
							break;
						}
					}		
				
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			generalUtilLogger.logWrite(e);
			throw new Exception("Search tree error!");
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			generalDao.releaseConnectionFromDataSurce(conn);
		}
		
		return resultList.toJsonString();
	}
}
