package com.skyline.customer.adama;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.entitypool.ElementDynamicParamsImp;

public class ReportsHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(ReportsHelper.class);

	public String doQRcodeImgFileName(String qrCode, String ireportPath) throws WriterException, IOException {
		String fileName = null;
		int size = 250;
		String fileType = "png";
		Date date = new Date();

		fileName = ireportPath + "\\tmp\\" + qrCode + "_" + date.getTime();
		File outputfile = new File(fileName);

		Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
		hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

		// Now with zxing version 3.3.0 you could change border size (white border size to just 1)
		hintMap.put(EncodeHintType.MARGIN, 1); /* default = 4 */
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix byteMatrix = qrCodeWriter.encode(qrCode, BarcodeFormat.QR_CODE, size, size, hintMap);
		int CrunchifyWidth = byteMatrix.getWidth();
		BufferedImage image = new BufferedImage(CrunchifyWidth, CrunchifyWidth, BufferedImage.TYPE_INT_RGB);
		image.createGraphics();

		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, CrunchifyWidth, CrunchifyWidth);
		graphics.setColor(Color.BLACK);

		for (int i = 0; i < CrunchifyWidth; i++) {
			for (int j = 0; j < CrunchifyWidth; j++) {
				if (byteMatrix.get(i, j)) {
					graphics.fillRect(i, j, 1, 1);
				}
			}
		}
		ImageIO.write(image, fileType, outputfile);
		return fileName;
	}

	// get blob from FG_FILES by id. Created for show ChemAxon picture in sub reports. In reports (not subreports) it is possible to show picture directly without this method 
	public String getBlobAsInputStream(String pCSV, String ireportPath) //1st param is DB_URL, 2 - DB_USER, 3 - DB_PASSWORD, 4 - blobId; 2nd param ireportPath
	{
		String[] arr = pCSV.split(",");
		//		DbBasicProvider dbBasicProvider = null;
		InputStream is = null;
		String fileName = ireportPath + "\\empty_imp.png";
		FileOutputStream fos = null;
		ResultSet rs = null;
		Statement stmt = null;
		Connection conn = null;
		GeneralDao generalDao = null;
		//		String url ="", user ="", password="";
		try {
			//			url = arr[0];
			//			user = arr[1];
			//			password = arr[2];
			if (arr[3] != null && !arr[3].equals("null") && ireportPath != null && !ireportPath.equals("null")) {
				Date date = new Date();
				fileName = ireportPath + "\\tmp\\" + arr[3] + "_" + date.getTime();

				String sql = "select t.file_content from FG_FILES t where t.file_id = " + arr[3];
				logger.info("/* SQL getBlobAsInputStream sql=*/ " + sql);
				WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
				generalDao = (GeneralDao) context.getBean("GeneralDao");
				conn = generalDao.getConnectionFromDataSurce();
				stmt = conn.createStatement();

				System.out.println(sql);
				rs = stmt.executeQuery(sql);

				fos = new FileOutputStream(new File(fileName));

				if (rs.next()) {
					is = rs.getBinaryStream(1);
					byte[] buffer = new byte[1024];
					if (is.equals(null)) {
						fileName = null;
					}
					while (is.read(buffer) > 0) {
						fos.write(buffer);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			//            logWrite(url,user,password,e);
		} catch (Exception ex) {
			ex.printStackTrace();
			//        	logWrite(url,user,password,ex);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (fos != null) {
					fos.close();
				}
				if (rs != null) {
					rs.close();
				}

				if (stmt != null) {
					stmt.close();
				}

				if (conn != null) {
					generalDao.releaseConnectionFromDataSurce(conn);
				}
			} catch (Exception ex) {
			}
		}
		return fileName;
	}

	public String getClobAsInputStream(String pCSV) //1st param is DB_URL, 2 - DB_USER, 3 - DB_PASSWORD, 4 - clobId
	{
		String[] arr = pCSV.split(",");
		//		DbBasicProvider dbBasicProvider = null;
		ResultSet rs = null;
		Statement stmt = null;
		InputStream is = null;
		String result = null;
		Connection conn = null;
		GeneralDao generalDao = null;
		//		String url ="", user ="", password="";
		try {
			//			url = arr[0];
			//			user = arr[1];
			//			password = arr[2];
			if (arr[3] != null && !arr[3].equals("null")) {
				//				dbBasicProvider = new DbBasicProvider(url,user,password);
				//				ResultSet rs = dbBasicProvider.getResultSet("select t.file_content_text from FG_RICHTEXT t where t.file_id = " + arr[3]);
				String sql = "select t.file_content_text from FG_RICHTEXT t where t.file_id = " + arr[3];
				logger.info("/* SQL getClobAsInputStream sql=*/ " + sql);
				WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
				generalDao = (GeneralDao) context.getBean("GeneralDao");
				conn = generalDao.getConnectionFromDataSurce();
				stmt = conn.createStatement();

				System.out.println(sql);
				rs = stmt.executeQuery(sql);

				if (rs.next()) {
					is = rs.getBinaryStream(1);
				}
				Scanner s = new Scanner(is).useDelimiter("\\A");
				result = s.hasNext() ? s.next() : "";
			}
			//			result = IOUtils.toString(is, StandardCharsets.UTF_8);
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}

				if (rs != null) {
					rs.close();
				}

				if (stmt != null) {
					stmt.close();
				}

				if (conn != null) {
					generalDao.releaseConnectionFromDataSurce(conn);
				}
			} catch (Exception ex) {
			}
		}
		return result;
	}

	public String getDynamicParamDisplay(String json) {
		WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		ElementDynamicParamsImp elementDynamicParamsImp = (ElementDynamicParamsImp) context
				.getBean("ElementDynamicParamsImp");
		System.out.println("getDynamicParamDisplay JSON=" + json);
		System.out
				.println("elementDynamicParamsImp.jsonToDisplay(json)=" + elementDynamicParamsImp.jsonToDisplay(json));
		return elementDynamicParamsImp.jsonToDisplay(json);
	}

	public String getClobAsRichText(String richTextId) {
		WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		GeneralDao generalDao = (GeneralDao) context.getBean("GeneralDao");
		//kd 20032019: after change richtext element try to change t.file_content instead t.file_content_text and check on ExperimentPrCR summary report
		String sql = "select t.file_content from fg_richtext t where t.file_id = " + richTextId;
		return generalDao.getSingleStringFromClob(sql);
	}

	public String getClobAsTextOrRich(String richTextId, String type) {
		WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
		GeneralDao generalDao = (GeneralDao) context.getBean("GeneralDao");
		//kd 20032019: after change richtext element try to change t.file_content instead t.file_content_text and check on ExperimentPrCR summary report
		String sql;
		if (type.equals("html")) {
			sql = "select t.file_content from fg_richtext t where t.file_id = " + richTextId;
			return generalDao.getSingleStringFromClob(sql) + "&nbsp;";
		} else {
			sql = "select t.file_content_text from fg_richtext t where t.file_id = " + richTextId;
			return generalDao.getSingleStringFromClob(sql);
		}
		/*if (strForCheck.contains("li") || strForCheck.contains("ol")) {
			strForCheck = strForCheck.replaceAll("li>", "td>");
			strForCheck = strForCheck.replaceAll("ol>", "tr>");}*/
	}
	
	public String getExperimentSample(String experimentId) {
		return "TODO get sample by experiment id " + experimentId + " (getExperimentSample)";
	}

	//	private String extractMethodNameToString(Throwable ex) {
	//		String toReturn = "";
	//		String smsg_ = "";
	//		try {
	//			if (ex != null) {
	//				String methodFormat = "%s\n";
	//				StackTraceElement[] st = ex.getStackTrace();
	//				StringBuilder mName = new StringBuilder();
	//				mName.append(String.format(methodFormat, "Exception: " + ex.toString()));
	//				for (int i = 0; i < st.length; i++) {
	//					smsg_ = String.format(methodFormat, st[i].toString());
	//					if(smsg_.contains("com.skyline")) {
	//						mName.append(String.format(methodFormat, st[i].toString()));
	//					}
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
	//	}
}
