package com.skyline.form.dal;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilLogger;

import oracle.jdbc.OracleTypes;
import oracle.sql.BLOB;
import oracle.sql.CLOB;

@Repository("UploadFileDao")
public class UploadFileDaoImp extends BasicDao implements UploadFileDao {

	@Autowired
	public GeneralUtilLogger generalUtilLogger;

	private static final Logger logger = LoggerFactory.getLogger(UploadFileDaoImp.class);
	@Value("${jdbc.driverClassName}")
	private String driverClassName;
	@Value("${jdbc.username}")
	private String username;
	@Value("${jdbc.url}")
	private String url;
	@Value("${jdbc.password}")
	private String password;
	////	@Value("${chemmarvin.imgprop}")
	//    private String imgprop = "png:w500,b32,#ffffff";
	////	   String url = "jdbc:oracle:thin:@localhost:1521:javaDemo";

	@Autowired
	private GeneralDao generalDao;

	@Autowired
	private FormSaveDao formSaveDao;

	@Autowired
	private GeneralUtil generalUtil;

	//	private JdbcTemplate jdbcTemplate;

	//	@Autowired
	//	public void setDataSource(DataSource dataSource) {
	//		this.jdbcTemplate = new JdbcTemplate(dataSource);		
	//	}

	@Override
	public void saveFileAsClob(String FILE_ID, MultipartFile file) {
		try {
			Map<String, String> simpleParameters = new HashMap<>();
			simpleParameters.put("file_id_in", FILE_ID);
			simpleParameters.put("file_name_in", file.getOriginalFilename());
			simpleParameters.put("CONTENT_TYPE_in", file.getContentType());
			Map<String, String> outParameters = new HashMap<>();
			outParameters.put("empty_clob_out", null);
			Map<String, Object> map = generalDao.callProcedureReturnsOutObject("", "FG_INSERT_CLOB_FILE",
					simpleParameters, outParameters, OracleTypes.CLOB);

			CLOB clob = (CLOB) map.get("empty_clob_out");
			ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes());

			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			Writer clobWriter = clob.getCharacterOutputStream();

			// Buffer to hold chunks of data to being written to the Clob.
			char[] buffer = new char[10 * 1024];

			// Read a chunk of data from the file input stream, and write the chunk to the Clob column output stream. 
			// Repeat till file has been fully read.
			int nread = 0; // Number of bytes read
			while ((nread = inputStreamReader.read(buffer)) != -1) // Read from file
			{
				clobWriter.write(buffer, 0, nread);
			}

			inputStreamReader.close();
			clobWriter.flush();
			clobWriter.close();
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
	}

	@Override
	public String saveStringAsClob(String elementID, String clobString) {
		String retVal = "1";

		try {
			Map<String, String> simpleParameters = new HashMap<>();
			simpleParameters.put("file_id_in", elementID);
			simpleParameters.put("file_name_in", null);
			simpleParameters.put("CONTENT_TYPE_in", null);
			Map<String, String> outParameters = new HashMap<>();
			outParameters.put("empty_clob_out", null);
			Map<String, Object> map = generalDao.callProcedureReturnsOutObject("", "FG_INSERT_CLOB_FILE",
					simpleParameters, outParameters, OracleTypes.CLOB);

			CLOB clob = (CLOB) map.get("empty_clob_out");
			setClob(clobString, clob);

		} catch (Exception e) {
			e.printStackTrace();
			generalUtilLogger.logWrite(e);
			retVal = "-1";
		}

		return retVal;
	}

	@Override
	public String saveFile(MultipartFile file, String formCodeFull) {

		String sql = "";
		CallableStatement stmtInsert = null;
		Connection con = null;
		String elementID = "";
		try {
			Class.forName(driverClassName);
			con = DriverManager.getConnection(url, username, password);

			logger.info("saveFile call /formCodeFull: " + formCodeFull);
			elementID = formSaveDao.getStructFileId(formCodeFull);
			logger.info("saveFile call /elementID: " + elementID);

			/*Map<String,String> simpleParameters=new HashMap<>();
			simpleParameters.put("file_id_in", elementID);
			simpleParameters.put("file_name_in", file.getOriginalFilename());
			simpleParameters.put("CONTENT_TYPE_in", file.getContentType());
			simpleParameters.put("is_temp_in", "0");
			Map<String,String> outParameters=new HashMap<>();
			outParameters.put("empty_blob_out", null);
			Map<String,Object> map = generalDao.callProcedureReturnsOutObject("", "FG_INSERT_FILE", simpleParameters, outParameters,java.sql.Types.BLOB);
			*/

			sql = "call FG_INSERT_FILE(?,?,?,?,?)";
			con.setAutoCommit(false);
			stmtInsert = con.prepareCall(sql);
			stmtInsert.setString(1, elementID);
			stmtInsert.setString(2, file.getOriginalFilename());
			stmtInsert.setString(3, file.getContentType());
			stmtInsert.setString(4, "0");
			stmtInsert.registerOutParameter(5, java.sql.Types.BLOB);
			stmtInsert.executeUpdate();

			// Get the Blob locator and open output stream for the Blob
			BLOB mapBlob = (BLOB) stmtInsert.getBlob(5);
			//BLOB mapBlob = (BLOB)map.get("empty_blob_out");

			OutputStream blobOutputStream = mapBlob.getBinaryOutputStream();

			// Open the sample file as a stream for insertion into the Blob column
			InputStream is = file.getInputStream();

			// Buffer to hold chunks of data to being written to the Blob.
			byte[] buffer = new byte[10 * 1024];

			// Read a chunk of data from the file input stream, and write the chunk to the Blob column output stream. 
			// Repeat till file has been fully read.
			int nread = 0; // Number of bytes read
			while ((nread = is.read(buffer)) != -1) // Read from file
			{
				blobOutputStream.write(buffer, 0, nread); // Write to Blob
			}

			// Close both streams
			is.close();
			blobOutputStream.flush();
			blobOutputStream.close();
			con.setAutoCommit(true);
			con.commit();
		} catch (Exception ex) { // Trap SQL errors
			elementID = "-1";
			generalUtilLogger.logWrite(ex);
			try {
				con.rollback();
			} catch (Exception sqlEx) {
				generalUtilLogger.logWrite(sqlEx);
			}
		} finally {
			try {
				if (con != null) {
					con.close();
				}

				if (stmtInsert != null) {
					stmtInsert.close();
				}

			} catch (Exception ex) {
				generalUtilLogger.logWrite(ex);
			}

		}
		return elementID;

	}

	@Override
	public String saveFile(String path_file, String formCodeFull, String fileName, boolean isTemp) {
		File file = new File(path_file);
		String sql = "";
		CallableStatement stmtInsert = null;
		Connection con = null;
		String elementID = "";
		try {
			logger.info("saveFile call /formCodeFull: " + formCodeFull);
			elementID = formSaveDao.getStructFileId(formCodeFull);
			logger.info("saveFile call /elementID: " + elementID);

			Class.forName(driverClassName);
			con = DriverManager.getConnection(url, username, password);

			sql = "call FG_INSERT_FILE(?,?,?,?,?)";
			con.setAutoCommit(false);
			stmtInsert = con.prepareCall(sql);
			stmtInsert.setString(1, elementID);
			stmtInsert.setString(2, generalUtil.getNull(fileName).equals("") ? file.getName() : fileName);
			stmtInsert.setString(3, Files.probeContentType(file.toPath()));
			stmtInsert.setString(4, isTemp ? "1" : "0");
			stmtInsert.registerOutParameter(5, java.sql.Types.BLOB);
			stmtInsert.executeUpdate();

			// Get the Blob locator and open output stream for the Blob
			BLOB mapBlob = (BLOB) stmtInsert.getBlob(5);

			/*Map<String,String> simpleParameters=new HashMap<>();
			simpleParameters.put("file_id_in", elementID);
			simpleParameters.put("file_name_in", generalUtil.getNull(fileName).equals("")?file.getName():fileName);
			simpleParameters.put("CONTENT_TYPE_in", Files.probeContentType(file.toPath()));
			simpleParameters.put("is_temp_in", isTemp?"1":"0");
			Map<String,String> outParameters=new HashMap<>();
			outParameters.put("empty_blob_out", null);
			Map<String,Object> map = generalDao.callProcedureReturnsOutObject("", "FG_INSERT_FILE", simpleParameters, outParameters,java.sql.Types.BLOB);
			
			BLOB mapBlob = (BLOB)map.get("empty_blob_out");*/

			//Blob mapBlob = rsBlob.getBlob(1);
			OutputStream blobOutputStream = mapBlob.getBinaryOutputStream();

			// Open the sample file as a stream for insertion into the Blob column
			InputStream is = new FileInputStream(file);

			// Buffer to hold chunks of data to being written to the Blob.
			byte[] buffer = new byte[10 * 1024];

			// Read a chunk of data from the file input stream, and write the chunk to the Blob column output stream. 
			// Repeat till file has been fully read.
			int nread = 0; // Number of bytes read
			while ((nread = is.read(buffer)) != -1) // Read from file
			{
				blobOutputStream.write(buffer, 0, nread); // Write to Blob
			}

			// Close both streams
			is.close();
			blobOutputStream.flush();
			blobOutputStream.close();
			con.setAutoCommit(true);
			con.commit();
		} catch (Exception ex) { // Trap SQL errors
			elementID = "-1";
			generalUtilLogger.logWrite(ex);
			try {
				con.rollback();
			} catch (Exception sqlEx) {
				generalUtilLogger.logWrite(sqlEx);
			}
		} finally {
			try {
				if (con != null) {
					con.close();
				}

				if (stmtInsert != null) {
					stmtInsert.close();
				}

			} catch (Exception ex) {
				generalUtilLogger.logWrite(ex);
			}

		}
		return elementID;

	}

	@Override
	public String saveByteArrayAsBlob(byte[] arr, String formCodeFull, String fileName) {
		PreparedStatement prstmt = null;
		String sql = "";
		String elementID = "";
		Connection con = null;

		try {
			elementID = formSaveDao.getStructFileId(formCodeFull);

			sql = "insert into FG_FILES (FILE_ID, FILE_NAME, CONTENT_TYPE, FILE_CONTENT, reference_form) VALUES (?,?,?,?,?)";

			Class.forName(driverClassName);
			con = DriverManager.getConnection(url, username, password);

			prstmt = con.prepareStatement(sql);
			prstmt.setString(1, elementID);
			prstmt.setString(2, fileName);
			prstmt.setString(3, "byte");
			prstmt.setBinaryStream(4, new ByteArrayInputStream(arr), arr.length);
			prstmt.setString(5, "");
			prstmt.execute();

		} catch (Exception e) {
			elementID = "";
			e.printStackTrace();
			generalUtilLogger.logWrite(e);
		} finally {
			try {
				if (con != null) {
					con.close();
				}
				if (prstmt != null) {
					prstmt.close();
				}
			} catch (Exception ex) {
				System.out.println(ex.toString());
				ex.printStackTrace();
			}
		}
		return elementID;
	}

	@Override
	public byte[] getByteArrayFromBlob(String FILE_ID) {
		byte[] bytes = null;
		try {

			bytes = generalDao.getBytesFromBlob(
					"select t.file_content" + " from fg_files t" + " where t.file_id = '" + FILE_ID + "'");

		} catch (Exception sqlEx) {
			bytes = null;
			logger.warn("sqlEx=" + sqlEx);
		}

		return bytes;
	}

	private InputStream getInputStreamFromURL(String URL) {
		// Create a new trust manager that trust all certificates
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		// Activate the new trust manager
		try {
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			System.out.println(e.toString());
			e.printStackTrace();
		}

		URL url;
		InputStream is = null;
		try {
			logger.info("getInputStreamFromURL call: /after trustManager/ URL=" + URL);
			url = new URL(URL);
			logger.info("getInputStreamFromURL call: /after new URL()");
			URLConnection connection = url.openConnection();
			logger.info("getInputStreamFromURL call: /after url.openConnection()");
			is = connection.getInputStream();
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			System.out.println(e.toString());
			e.printStackTrace();
		}
		return is;
	}

	private void setClob(String clobString, CLOB clob) throws Exception {
		StringReader inputStreamReader = new StringReader(clobString);
		Writer clobWriter = clob.getCharacterOutputStream();
		// Buffer to hold chunks of data to being written to the Clob.
		char[] buffer = new char[10 * 1024];

		// Read a chunk of data from the file input stream, and write the chunk to the Clob column output stream. 
		// Repeat till file has been fully read.
		int nread = 0; // Number of bytes read
		while ((nread = inputStreamReader.read(buffer)) != -1) // Read from file
		{
			clobWriter.write(buffer, 0, nread);
		}

		inputStreamReader.close();
		clobWriter.flush();
		clobWriter.close();
	}

	@Override
	public String getContentType(String FILE_ID) {
		return generalDao.selectSingleString("select CONTENT_TYPE from FG_FILES where FILE_ID = '" + FILE_ID + "'");
	}

	@Override
	public boolean isDisplayContent(String FILE_ID) {
		boolean toReturn = false;
		try {
			String counter = generalDao.selectSingleStringNoException("select count(*)" + " from fg_files t"
					+ " where t.file_id = '" + FILE_ID + "' and t.file_display_id is not null");

			if (generalUtil.getNullInt(counter, 0) != 0) {
				toReturn = true;
			}
		} catch (Exception sqlEx) { // Trap SQL errors 
			logger.warn("sqlEx=" + sqlEx);
		}

		return toReturn;
	}

	@Override
	public InputStream getContent(String FILE_ID, StringBuilder sbOriginalFilename) {
		InputStream blobStream = null;
		try {
			String fileName = generalDao.selectSingleStringNoException(
					"select t.file_name" + " from fg_files t" + " where t.file_id = '" + FILE_ID + "'");
			sbOriginalFilename.append(fileName);
			blobStream = generalDao.getInputStreamFromBlob(
					"select t.file_content" + " from fg_files t" + " where t.file_id = '" + FILE_ID + "'");
		} catch (Exception sqlEx) { // Trap SQL errors
			blobStream = null;
			logger.warn("sqlEx=" + sqlEx);
		}

		return blobStream;
	}

	@Override
	public InputStream getContentDisplay(String FILE_ID, StringBuilder sbOriginalFilename) {
		InputStream blobStream = null;
		try {
			String fileName = generalDao.selectSingleStringNoException(
					"select decode(t.file_display_id,null,t.file_name,(select d.file_name from fg_files_fast_v d where d.file_id = t.file_display_id))"
							+ " from fg_files_fast_v t" + " where t.file_id = '" + FILE_ID + "'");
			sbOriginalFilename.append(fileName);
			blobStream = generalDao.getInputStreamFromBlob(
					"select decode(t.file_display_id,null,t.file_content,(select d.file_content from fg_files d where d.file_id = t.file_display_id)) from fg_files t where  t.file_id  = '"
							+ FILE_ID + "'");
		} catch (Exception sqlEx) { // Trap SQL errors
			blobStream = null;
			logger.warn("sqlEx=" + sqlEx);
		}

		return blobStream;
	}

	@Override
	public String getStringContent(String ID, String formCode, String domID, String formID) {
		String str = "";
		String sql = "select t.file_content from fg_clob_files t where t.file_id = '" + ID + "'";
		try {
			str = generalDao.getSingleStringFromClob(sql);
		} catch (Exception e) {
			str = "[]";
			e.printStackTrace();
			generalUtilLogger.logWriter(LevelType.ERROR,
					"ERROR (formCode = " + formCode + ", domID = " + domID + ") in sql query: " + sql,
					ActivitylogType.SQLError, formID);
			generalUtilLogger.logWrite(e);
		}
		return str;
	}

	@Override
	public String getWebixContent(String ID, boolean isPln) {
		String sql = "select c.webix_data from " + (isPln ? "fg_i_webix_data_pln_v" : "fg_i_webix_data_v")
				+ " c where  c.parent_id = '" + ID + "'";
		String str = generalDao.getSingleStringFromClobNoException(sql);
		return str;
	}

	@Override
	public String getRichTextContent(String ID) {
		String str;
		try {
			str = generalDao
					.getSingleStringFromClob("select t.file_content from fg_richtext t where t.file_id = '" + ID + "'");
		} catch (Exception e) {
			logger.warn("No richtext found for id=" + ID);
			str = ID;
		}
		return str;
	}
	
	@Override
	public String getDiagramContent(String ID) {
		String str;
		try {
			str = generalDao
					.getSingleStringFromClobNoException("select t.content from fg_diagram t where t.element_id = '" + ID + "'");
		} catch (Exception e) {
			logger.warn("No diagram found for id=" + ID);
			str = ID;
		}
		return str;
	}

	@Override
	public String getFileName(String FILE_ID) {
		return generalDao.selectSingleString("select file_name from FG_FILES where FILE_ID = '" + FILE_ID + "'");
	}

	public void saveChemImageFile(byte[] arr, String imgURL, String objID) {
		PreparedStatement prstmt = null;
		String sql = "";
		Connection con = null;

		try {
			sql = "insert into FG_FILES (FILE_ID, FILE_NAME,CONTENT_TYPE,FILE_CONTENT, reference_form) VALUES (?,?,?,?,?)";

			/*Class.forName(driverClassName);
			con = DriverManager.getConnection(url, username, password);*/

			con = generalDao.getConnectionFromDataSurce();

			prstmt = con.prepareStatement(sql);
			prstmt.setString(1, objID);
			prstmt.setString(2, "MOLECULE_IMAGE.jpeg");
			prstmt.setString(3, "image/jpeg");
			prstmt.setBlob(4, new ByteArrayInputStream(arr), arr.length);
			prstmt.setString(5, "ChemDoodle");
			prstmt.execute();
			//			System.out.println("Image form URL: " + imgURL+" successfully saved in DB.");
		} catch (Exception e) {
			//			System.out.println("saveImage() call/ Exception: " + e.toString());
			e.printStackTrace();
			generalUtilLogger.logWrite(e);
		} finally {
			try {
				if (prstmt != null) {
					prstmt.close();
				}
				/*if (con != null)
				{
					con.close();
				}*/
				generalDao.releaseConnectionFromDataSurce(con);
			} catch (Exception ex) {
				System.out.println(ex.toString());
				ex.printStackTrace();
			}
		}

	}

	@Override
	public String saveRichText(String elementID, String clobString, String plainText, boolean isTransactional) {
		String retVal = "1";
		if (isTransactional) {
			try {
				Map<String, String> simpleParameters = new HashMap<>();
				simpleParameters.put("file_id_in", elementID);
				simpleParameters.put("file_name_in", null);
				simpleParameters.put("CONTENT_TYPE_in", null);
				Map<String, String> outParameters = new HashMap<>();
				outParameters.put("empty_clob_out", null);
				outParameters.put("empty_clob_plain_tex_out", null);
				Map<String, Object> map = generalDao.callProcedureReturnsOutObject("", "FG_INSERT_RICHTEXT",
						simpleParameters, outParameters, OracleTypes.CLOB);

				CLOB clob = (CLOB) map.get("empty_clob_out");
				setClob(clobString, clob);

				clob = (CLOB) map.get("empty_clob_plain_tex_out");
				setClob(plainText, clob);

			} catch (Exception e) {
				e.printStackTrace();
				generalUtilLogger.logWrite(e);
				retVal = "-1";
			}
		} else {

			String sql = "";
			CallableStatement stmtInsert = null;
			Connection con = null;

			try {
				Class.forName(driverClassName);
				con = DriverManager.getConnection(url, username, password);
				con.setAutoCommit(false);

				sql = "call FG_INSERT_RICHTEXT(?,?,?,?,?)";
				stmtInsert = con.prepareCall(sql);
				stmtInsert.setString(1, elementID);
				stmtInsert.setString(2, null);
				stmtInsert.setString(3, null);
				stmtInsert.registerOutParameter(4, java.sql.Types.CLOB);
				stmtInsert.registerOutParameter(5, java.sql.Types.CLOB);
				stmtInsert.executeUpdate();

				CLOB clob = (CLOB) stmtInsert.getClob(4);
				setClob(clobString, clob);

				clob = (CLOB) stmtInsert.getClob(5);
				setClob(plainText, clob);

				con.setAutoCommit(true);
				con.commit();

			} catch (Exception e) {
				e.printStackTrace();
				generalUtilLogger.logWrite(e);
				retVal = "-1";
			} finally {
				if (stmtInsert != null) {
					try {
						stmtInsert.close();
						con.close();
					} catch (Exception e) {
						generalUtilLogger.logWrite(e);
						e.printStackTrace();
					}
				}
			}
		}
		return retVal;
	}

	@Override
	public List<JSONArray> getWebixAnalytCalcData(String parentID, boolean isUpload) {

		JSONArray rows = new JSONArray();
		JSONArray tables = new JSONArray();
		List<JSONArray> currentList = new LinkedList<JSONArray>();
		String colName = "";

		try {
			Map<String, String> simpleParameters = new HashMap<>();
			simpleParameters.put("parent_id_in", parentID);
			simpleParameters.put("is_upload_in", (isUpload ? "1" : "0"));
			Map<String, String> outParameters = new HashMap<>();
			outParameters.put("o_result", null);
			outParameters.put("o_result_2", null);
			Map<String, Object> map = generalDao.callProcedureReturnsOutObject("", "FG_GET_ANALYTICAL_CALC_DATA",
					simpleParameters, outParameters, OracleTypes.CURSOR);

			ArrayList<?> rsLst = (ArrayList) map.get("o_result");
			for (Object rsDt : rsLst) {
				JSONObject obj = new JSONObject();
				for (Map.Entry<String, String> row : ((Map<String, String>) rsDt).entrySet()) {
					colName = row.getKey().toLowerCase();
					String value = generalUtil.getNull(String.valueOf(row.getValue()));
					obj.put(colName, value);
				}
				rows.put(obj);
			}
			currentList.add(0, rows);
			rsLst = (ArrayList) map.get("o_result_2");
			for (Object rsDt : rsLst) {
				JSONArray obj = new JSONArray();
				for (Map.Entry<String, String> row : ((Map<String, String>) rsDt).entrySet()) {
					obj.put(generalUtil.getNull(String.valueOf(row.getValue())));
				}
				tables.put(obj);
			}
			currentList.add(1, tables);

		} catch (Exception e) {
			currentList.add(0, new JSONArray());
			currentList.add(1, new JSONArray());
			e.printStackTrace();
			generalUtilLogger.logWrite(e);
		}
		
		return currentList;
	}

	@Override
	public JSONArray getWebixFormulCalcData(String parentID) {
		String sql = "select * from fg_wbx_formul_data where parent_id = " + parentID;
		return getWebixTableInitData(sql);
	}

	@Override
	public JSONArray getWebixExperimentStepCalcData(String parentID) {
		String sql = "select * from fg_wbx_expstep_data where parent_id = " + parentID;
		return getWebixTableInitData(sql);
	}
	
	@Override
	public JSONArray getWebixMassBalanceResCalcData(String parentID,String sampleId) {
		String sql = "select distinct w.sample_mb as sample_id,w.material_id as inv_item_material_id,m.InvItemMaterialName as material_name,w.result_value as perc_of_product,'' as sample_result_id,''as resultid_holder,w.result_name as substance_type,fg_get_value_from_json( w.indication_mb,'is_chemical') as is_chemical,fg_get_value_from_json( w.indication_mb,'is_isolated') as is_isolated,fg_get_value_from_json( w.indication_mb,'is_yield') as is_yield,fg_get_value_from_json( w.indication_mb,'is_summary') as is_summary,w.result_comment as description,w.moles,w.yield,w.weight,m.mw as molecula_weight from FG_WEBIX_OUTPUT w,fg_s_invitemmaterial_v m where m.invitemmaterial_id=w.material_id and w.sample_mb = '"+sampleId+"' and w.result_is_active = 1 and nvl(w.step_id,w.experiment_id) = " + parentID;
		//TODO
		return getWebixTableInitData(sql);
	}

	@Override
	public JSONArray getWebixMassBalanceCalcData(String parentID) {
		String sql = "select * from fg_wbx_massbalance_step where sample_id = " + parentID;
		return getWebixTableInitData(sql);
	}

	@Override
	public JSONArray updateWebixMassBalanceSamplesList(String samplesScope, String parentID, String runNumber) {
		String viewName = "";
		List<String> list = new ArrayList<String>();
		JSONArray jsonArr = new JSONArray();

		if (samplesScope.equals("step")) {
			viewName = "fg_wbx_massbalance_sampls_step";
		} else if (samplesScope.equals("experiment")) {
			viewName = "fg_wbx_massbalance_samples_exp";
		}
		String runnumberWherePart = "";
		if(samplesScope.equals("experiment")){
			runnumberWherePart = (!generalUtil.getNull(runNumber).isEmpty()?(runNumber.equals("0")?"and (nullif(RUNNUMBER,'0') is null or experiment_id!='"+parentID+"')":" and RUNNUMBER = '"+runNumber+"' and experiment_id='"+parentID+"'"):"");
		}
		String query = "select display_data from " + viewName + " where PARENTID = '" + parentID + "'"+runnumberWherePart;//TODO:add filter with runNumber
		list = generalDao.getListOfStringBySql(query);

		for (String str : list) {
			try {
				JSONObject obj = new JSONObject(str);
				jsonArr.put(obj);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return jsonArr;
	}
	
	@Override
	public String getTooltipForWebixMassBalanceSamplesField(String sampleID) {
		String query = "select fg_get_RichText_display(t.COMMENTSFORCOA) from fg_s_sample_v t where t.sample_id = '" + sampleID + "'";
		return generalDao.selectSingleString(query) == null ? "" : generalDao.selectSingleString(query);
	}
	
	@Override
	public JSONArray updateWebixResultTypeList(Map<String, String> elementValueMap) {
		
		String TYPE_ID = elementValueMap.get("TYPE_ID");
		List<String> list = new ArrayList<String>();
		JSONArray jsonArr = new JSONArray();
		
		String query = "select display_data from fg_wbx_selftest_restype_list where selftesttype_id = " + TYPE_ID;
		list = generalDao.getListOfStringBySql(query);
		
		for (String str : list) {
			try {
				JSONObject obj = new JSONObject(str);
				jsonArr.put(obj);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return jsonArr;
	}

	@Override
	public JSONArray getWebixGeneralTableData(String sql) {
		return getWebixTableInitData(sql);
	}

	private JSONArray getWebixTableInitData(String sqlObj) {
		
		JSONArray rows = new JSONArray();
		String colName = "";

		try {
			List<Map<String, Object>> rsList = generalDao.getListOfMapsBySql(sqlObj);
			for (Map<String, Object> rsData : rsList) {//runs on the rows
				JSONObject obj = new JSONObject();
				for (Map.Entry<String, Object> entry : rsData.entrySet()) {
					colName = entry.getKey().toLowerCase();
					String value = entry.getValue() == null ? null : entry.getValue().toString();
					obj.put(colName, value);
				}
				rows.put(obj);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			generalUtilLogger.logWrite(e);
		}
		
		return rows;
	}

	@Override
	public String saveStringAsClobRenderId(String desc, String data) {

		//String toReturnId = formSaveDao.getStructFormId(desc);ab 30/03/18
		String toReturnId = formSaveDao.getStructFileId(desc);

		if (saveStringAsClob(toReturnId, data).equals("-1")) {
			return "-1";
		}
		return toReturnId;
	}

	@Override
	public String saveStringAsClobNewConnection(String elementID, String clobString) {
		String sql = "";
		CallableStatement stmtInsert = null;
		Connection con = null;
		String retVal = "1";
		try {
			Class.forName(driverClassName);
			con = DriverManager.getConnection(url, username, password);

			con.setAutoCommit(false);
			sql = "call FG_INSERT_CLOB_FILE(?,?,?,?)";
			stmtInsert = con.prepareCall(sql);
			stmtInsert.setString(1, elementID);
			stmtInsert.setString(2, null);
			stmtInsert.setString(3, null);
			stmtInsert.registerOutParameter(4, java.sql.Types.CLOB);
			stmtInsert.executeUpdate();

			CLOB clob = (CLOB) stmtInsert.getClob(4);
			setClob(clobString, clob);
			con.setAutoCommit(true);
			con.commit();
		} catch (Exception e) {
			e.printStackTrace();
			generalUtilLogger.logWrite(e);
			retVal = "-1";
		} finally {
			if (stmtInsert != null) {
				try {
					stmtInsert.close();
					con.close();
				} catch (Exception e) {
					generalUtilLogger.logWrite(e);
					e.printStackTrace();
				}
			}
		}
		return retVal;

	}

	@Override
	public void updateUserBreadcrumbLink(String userName, String fileID) {
		try {
			generalDao.updateSingleString("update fg_s_user_pivot t set t.last_breadcrumb_link = '" + fileID
					+ "' where t.username = '" + userName + "'");
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
	}

	@Override
	public String getUserBreadcrumbLink(String userID) {
		String retVal = "";
		try {
			retVal = generalDao.selectSingleString(
					"select t.last_breadcrumb_link from fg_s_user_pivot t  where t.formid = '" + userID + "'");
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return retVal;
	}

	@Override
	public void saveDiagramImage(byte[] arr, String imgURL, String objID) {
		PreparedStatement prstmt = null;
		String sql = "";
		Connection con = null;

		try {
			sql = "insert into FG_FILES (FILE_ID, FILE_NAME,CONTENT_TYPE,FILE_CONTENT, reference_form) VALUES (?,?,?,?,?)";

			con = generalDao.getConnectionFromDataSurce();

			prstmt = con.prepareStatement(sql);
			prstmt.setString(1, objID);
			prstmt.setString(2, "DIAGRAM_IMAGE.png");
			prstmt.setString(3, "image/png");
			prstmt.setBlob(4, new ByteArrayInputStream(arr), arr.length);
			prstmt.setString(5, "diagram");
			prstmt.execute();
		} catch (Exception e) {
			e.printStackTrace();
			generalUtilLogger.logWrite(e);
		} finally {
			try {
				if (prstmt != null) {
					prstmt.close();
				}
				generalDao.releaseConnectionFromDataSurce(con);
			} catch (Exception ex) {
				System.out.println(ex.toString());
				ex.printStackTrace();
			}
		}
	}
}
