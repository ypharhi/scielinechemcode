package jasper.biz;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.lowagie.text.pdf.BaseFont;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.base.JRBaseStaticText;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.FontKey;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JExcelApiExporterParameter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.export.PdfFont;
import net.sf.jasperreports.engine.fill.JRSwapFileVirtualizer;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSwapFile;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 * Create a report in following formats: file, bytes array and outputstream.
 */
public class JasperReportGenerator {
//	String urlConnection = "";
//	String userName = "";
//	String userPassword = ""; 
	Connection conn = null;
	JRDataSource jrDataSource = null;
	final int VIRTUALIZER_MAX_PAGE_SIZE_CACHE = 300;
	final int VIRTUALIZER_BLOCK_SIZE = 1024;
	final int VIRTUALIZER_MIN_GROW_COUNT = 1024;
	private HashMap<String, String> mLang = null;

	String HEBREW_ENCODING_EXCEL_CSV = "iso-8859-8";

	public JasperReportGenerator(Connection conn) {
		this.conn = conn;
	}

	public JasperReportGenerator(Connection conn, HashMap<String, String> mLang) {
		this(conn);
		this.mLang = mLang;
	}

	public JasperReportGenerator(JRDataSource jrDataSource) {
		this.jrDataSource = jrDataSource;
	}

	public JasperReportGenerator(JRDataSource jrDataSource, HashMap<String, String> mLang) {
		this(jrDataSource);
		this.mLang = mLang;
	}

	public JasperReportGenerator(HashMap<String, String> mLang) {
		this.mLang = mLang;
	}

	public JasperReportGenerator(Object obj) {
		try {
			this.mLang = (HashMap<String, String>) obj;
		} catch (Exception e) {

		}
	}

	public JasperReportGenerator() {

	}

	public ByteArrayOutputStream getByteArrayOutputStream(String sReportID, String sReportName,
			JasperReportType reportType, String sReportParameterTitle, String sReportParameterFilterDisplay,
			HashMap<String, String> hmReportReplacerList, HashMap<String, Object> hmReportParameterList,
			String reportPath, String reportPathTmp, boolean useVirtualizer) throws Exception {
		// init..
		ByteArrayOutputStream toReturn = new ByteArrayOutputStream();

		if (!useVirtualizer) // render report into output stream
		{
			JasperReport report = null;
			InputStream input = null;
			InputStream inputModified = null;
			JasperDesign design = null;
			JasperPrint print = null;
			GeneralBiz generalBiz = new GeneralBiz();

			// get data in case the builder has DB connection parameters
//                    if(!urlConnection.equals("")) 
//                    {
//                    	DbBasicProvider dbBasicProvider = DbBasicProvider.getInstance(urlConnection,userName,userPassword);
//                        System.out.println("JasperReportGenerator.getByteArrayOutputStream - openConnection() - conn=" + conn);
//                    }

			try {
				// put the report title and display filter if isn't there already
				if (hmReportParameterList == null) {
					hmReportParameterList = new HashMap<String, Object>();
				}

				if (hmReportReplacerList == null) {
					hmReportReplacerList = new HashMap<String, String>();
				}

				hmReportParameterList.put("ReportParameterTitle", sReportParameterTitle);
				hmReportReplacerList.put("@ReportReplacerTitle@", sReportParameterTitle);
				hmReportParameterList.put("ReportParameterFilterDisplay", sReportParameterFilterDisplay);
				hmReportReplacerList.put("@ReportReplacerFilterDisplay@", sReportParameterFilterDisplay);

				switch (reportType) {
				case PDF: {
					// Add arial font for pdf
					HashMap fontMap = new HashMap();
					PdfFont font = new PdfFont(reportPath + "\\ARIAL.TTF", BaseFont.IDENTITY_H, true); // (String
																										// pdfFontName,
																										// String
																										// pdfEncoding,
																										// Boolean
																										// isPdfEmbedded)
					FontKey key = new FontKey("Arial", false, false); // (String fontName, Boolean bold, Boolean italic)
					fontMap.put(key, font);
					PdfFont font2 = new PdfFont(reportPath + "\\ARIALBD.TTF", BaseFont.IDENTITY_H, true);
					FontKey key2 = new FontKey("Arial", true, false);
					fontMap.put(key2, font2);
					PdfFont font3 = new PdfFont(reportPath + "\\ARIALBI.TTF", BaseFont.IDENTITY_H, true);
					FontKey key3 = new FontKey("Arial", true, true);
					fontMap.put(key3, font3);
					PdfFont font4 = new PdfFont(reportPath + "\\ARIALI.TTF", BaseFont.IDENTITY_H, true);
					FontKey key4 = new FontKey("Arial", false, true);
					fontMap.put(key4, font4);

					JRPdfExporter exporter = new JRPdfExporter();
					exporter.setParameter(JRPdfExporterParameter.CHARACTER_ENCODING, "UTF-8");
					exporter.setParameter(JRPdfExporterParameter.FONT_MAP, fontMap);

					input = new FileInputStream(new File(reportPath + "\\" + sReportName));
					inputModified = generalBiz.InputStreamModified(reportPath, input, hmReportReplacerList, mLang,
							sReportName);
					design = JRXmlLoader.load(inputModified);
					input.close();
					inputModified.close();
					report = JasperCompileManager.compileReport(design);

					JRParameter[] jpr = report.getParameters();
					for (JRParameter jrParameter : jpr) {
						if (jrParameter.getName().startsWith("CONFIG_PARAM_PRINT_ON_LOAD_PDF")) {
							exporter.setParameter(JRPdfExporterParameter.PDF_JAVASCRIPT, "this.print();");
						}
					}

					if (jrDataSource == null) // DB connection
					{
						System.out.println("Jasper report query: " + report.getQuery().getText());
						print = JasperFillManager.fillReport(report, hmReportParameterList, conn);
					} else // JRDataSource object
					{
						print = JasperFillManager.fillReport(report, hmReportParameterList, jrDataSource);
					}

					exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT, print);
					exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, toReturn);
					exporter.exportReport();
				}
					break;

				case SIMPLE_EXCEL: {
					String sReportColumnExportCounter = "1";
					input = new FileInputStream(new File(reportPath + "\\" + sReportName));
					inputModified = generalBiz.InputStreamModified(reportPath, input, hmReportReplacerList, mLang,
							sReportName);
					design = JRXmlLoader.load(inputModified);
					input.close();
					inputModified.close();
					report = JasperCompileManager.compileReport(design);

					// build the excel data based on the sql in the xml file..
					Statement stmt = null;
					ResultSet rs = null;
					StringBuffer sbExcel = null;
					String sql = "";
					sql = report.getQuery().getText();
					stmt = conn.createStatement();
					rs = stmt.executeQuery(sql);
					ResultSetMetaData rsmd = rs.getMetaData();

					// trying to guess the number columns from the query that need to be shown in
					// the excel
					int colCount;
					HashMap map = (HashMap) design.getParametersMap();
					boolean isColumnNameFromHeader = true;
					String reportColumnExportCSV = "";
					if (map.containsKey("ReportColumnExportCSV")) // take the columns from SCV parameter
					{
						isColumnNameFromHeader = false;
						JRParameter jrpReportColumnExportCSV = (JRParameter) map.get("ReportColumnExportCounter");
						reportColumnExportCSV = (jrpReportColumnExportCSV.getDefaultValueExpression().getText())
								.replaceAll("\"", "");
						colCount = (reportColumnExportCSV.split(",")).length;

					} else if (map.containsKey("ReportColumnExportCounter")) // see if it defined as parameter (number
																				// in string type) -> take first
																				// ReportColumnExportCounter from report
																				// header
					{
						JRParameter jrpReportColumnExportCounter = (JRParameter) map.get("ReportColumnExportCounter");
						sReportColumnExportCounter = jrpReportColumnExportCounter.getDefaultValueExpression().getText();
						colCount = Integer.parseInt(sReportColumnExportCounter.replaceAll("\"", ""));
					} else if (report.getColumnHeader().getChildren().size() > 0) // take all from report header
					{
						colCount = report.getColumnHeader().getChildren().size();
					} else {
						colCount = 1; // make 1 as defualt
					}

					HashMap<String, String> mapColHeaders = new HashMap<String, String>();
					if (isColumnNameFromHeader) {
						Object[] oaCol = report.getColumnHeader().getChildren().toArray();
						for (int i = 0; i < oaCol.length; i++) {
							if (oaCol[i] instanceof JRBaseStaticText) {
								JRBaseStaticText st = (JRBaseStaticText) oaCol[i];
								mapColHeaders.put((st.getX() + 10000) + st.getKey(), st.getText());
							}
						}
					} else {
						String[] sCol = reportColumnExportCSV.split(",");
						for (int i = 0; i < sCol.length; i++) {
							mapColHeaders.put(String.valueOf(i + 10000), sCol[i]);
						}
					}

					List<String> sortedKeys = new ArrayList<String>(mapColHeaders.keySet());
					Collections.sort(sortedKeys);

					sbExcel = new StringBuffer();

					// build header
					sbExcel.append("<table border=1>\n");
					sbExcel.append("<tr>\n<td colspan='" + colCount + "' style='font-weight:bold'>")
							.append(mLang != null ? generalBiz.modifiedLangOnString(sReportParameterTitle, mLang)
									: sReportParameterTitle)
							.append("</td>\n</tr>\n");
					sbExcel.append("<tr>\n<td colspan='" + colCount + "' style='font-weight:bold'>")
							.append(mLang != null
									? generalBiz.modifiedLangOnString(sReportParameterFilterDisplay, mLang)
									: sReportParameterFilterDisplay)
							.append("</td>\n</tr>\n");
					sbExcel.append("<tr>\n");

					int index = 0;
					for (String key_ : sortedKeys) {
						if (index++ < colCount) {
							sbExcel.append("   <td>" + mapColHeaders.get(key_) + "</td>\n");
						}
					}
					sbExcel.append("</tr>\n");

					// build details
					while (rs.next()) {
						sbExcel.append("<tr>\n");
						for (int j = 1; j <= colCount; j++) {
							sbExcel.append("   <td>" + ((rs.getObject(j) == null) ? "" : rs.getObject(j)) + "</td>\n");
						}
						sbExcel.append("</tr>\n");
					}

					// close it
					sbExcel.append("</table>\n");

					// write it
					toReturn.write(sbExcel.toString().getBytes());
				}
					break;

				case JXL_EXCEL: {
					input = new FileInputStream(new File(reportPath + "\\" + sReportName));
					inputModified = generalBiz.InputStreamModified(reportPath, input, hmReportReplacerList, mLang,
							sReportName);
					design = JRXmlLoader.load(inputModified);
					input.close();
					inputModified.close();
					design.setPageFooter(null); // hide the page fotter
					design.setLastPageFooter(null); // hide the last page fotter
					report = JasperCompileManager.compileReport(design);

					if (jrDataSource == null) // DB connection
					{
						System.out.println("Jasper report query: " + report.getQuery().getText());
						print = JasperFillManager.fillReport(report, hmReportParameterList, conn);
					} else // JRDataSource object
					{
						print = JasperFillManager.fillReport(report, hmReportParameterList, jrDataSource);
					}

					JExcelApiExporter exporter = new JExcelApiExporter();
					exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
					exporter.setParameter(JExcelApiExporterParameter.IS_DETECT_CELL_TYPE, true);
					exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, toReturn);
					exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_CELL_BORDER, false);
					exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, false);

					exporter.exportReport();

					if (input != null) {
						input.close();
					}

					if (inputModified != null) {
						inputModified.close();
					}
				}
					break;

				case CSV_EXCEL: {
					input = new FileInputStream(new File(reportPath + "\\" + sReportName));
					inputModified = generalBiz.InputStreamModified(reportPath, input, hmReportReplacerList, mLang,
							sReportName);
					design = JRXmlLoader.load(inputModified);
					input.close();
					inputModified.close();
					design.setPageFooter(null); // hide the page fotter
					design.setLastPageFooter(null); // hide the last page fotter
					report = JasperCompileManager.compileReport(design);

					if (jrDataSource == null) // DB connection
					{
						System.out.println("Jasper report query: " + report.getQuery().getText());
						print = JasperFillManager.fillReport(report, hmReportParameterList, conn);
					} else // JRDataSource object
					{
						print = JasperFillManager.fillReport(report, hmReportParameterList, jrDataSource);
					}

					JRCsvExporter exporterCSV = new JRCsvExporter();
					exporterCSV.setParameter(JRXlsExporterParameter.JASPER_PRINT, print);
					exporterCSV.setParameter(JRXlsExporterParameter.OUTPUT_STREAM, toReturn);

					exporterCSV.exportReport();

					if (input != null) {
						input.close();
					}

					if (inputModified != null) {
						inputModified.close();
					}
				}
					break;
				}
			} catch (OutOfMemoryError e) {
				System.out.println(e.toString());
			} catch (Exception e) {
				System.out.println(e.toString());
			} finally {
//                        try
//                        { 
//                            if(!urlConnection.equals("")) 
//                            {
//                                System.out.println("JasperReportGenerator.getByteArrayOutputStream - connection.close() conn=" + conn);
////                                conn.close();
//                            }
//                        }
//                        catch (Exception e)
//                        {
//                            System.out.println("JasperReportGenerator.getByteArrayOutputStream - close connection failed conn=" + conn + ", e=" + e.toString());
//                        }
			}
		} else // use default getByteArrayOutputStream that invoke getPath (=> uses virtualizer
				// on big data)
		{
			toReturn = getByteArrayOutputStream(sReportID, sReportName, reportType, sReportParameterTitle,
					sReportParameterFilterDisplay, hmReportReplacerList, hmReportParameterList, reportPath,
					reportPathTmp);
		}
		return toReturn;
	}

	public ByteArrayOutputStream getByteArrayOutputStream(String sReportID, String sReportName,
			JasperReportType reportType, String sReportParameterTitle, String sReportParameterFilterDisplay,
			HashMap<String, String> hmReportReplacerList, HashMap<String, Object> hmReportParameterList,
			String reportPath, String reportPathTmp) throws Exception {
		ByteArrayOutputStream toReturn = new ByteArrayOutputStream();

		String path = getPath(sReportID, sReportName, reportType, sReportParameterTitle, sReportParameterFilterDisplay,
				hmReportReplacerList, hmReportParameterList, reportPath, reportPathTmp);

		InputStream isFlie = new FileInputStream(new File(path));
		BufferedInputStream fif = new BufferedInputStream(isFlie);
		int data;
		while ((data = fif.read()) != -1) {
			toReturn.write(data);
		}

		return toReturn;
	}

	public String getPath(String sReportID, String sReportName, JasperReportType reportType,
			String sReportParameterTitle, String sReportParameterFilterDisplay,
			HashMap<String, String> hmReportReplacerList, HashMap<String, Object> hmReportParameterList,
			String reportPath, String reportPathTmp) throws Exception {
		// init..
//		StringBuilder rNameToCompile = new StringBuilder(sReportName);
		String toReturn = reportPathTmp + "\\" + sReportID + (new Date()).getTime(); // add time in case some call
																						// forgot that it's unique
		JasperReport report = null;
		InputStream input = null;
		InputStream inputModified = null;
		JasperDesign design = null;
		JasperPrint print = null;
		GeneralBiz generalBiz = new GeneralBiz();
		JRSwapFileVirtualizer virtualizer = null;
		File reportCompiled = null;

		if (!generalBiz.isGenerateCompileFileOnDevelop(reportPath)) {
			try {
				reportCompiled = new File(reportPath + "\\" + sReportName.replace(".xml", "") + ".jasper");

				if (reportCompiled.exists()) {
					report = (JasperReport) JRLoader.loadObject(reportCompiled);
				}
			} catch (Exception e) {
				System.out.println("ERROR! failed loading the compiled file. e=" + e.getMessage());
			}
		}

		try {
			// put the report title and display filter if isn't there already
			if (hmReportParameterList == null) {
				hmReportParameterList = new HashMap<String, Object>();
			}

			if (hmReportReplacerList == null) {
				hmReportReplacerList = new HashMap<String, String>();
			}

			hmReportParameterList.put("ReportParameterTitle", sReportParameterTitle);
			hmReportReplacerList.put("@ReportReplacerTitle@", sReportParameterTitle);
			hmReportParameterList.put("ReportParameterFilterDisplay", sReportParameterFilterDisplay);
			hmReportReplacerList.put("@ReportReplacerFilterDisplay@", sReportParameterFilterDisplay);

			// use virtualizer
			/*
			 * virtualizer = new JRFileVirtualizer(100, reportPathTmp);
			 * hmReportParameterList.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
			 */
			JRSwapFile swapFile = new JRSwapFile(reportPathTmp, VIRTUALIZER_BLOCK_SIZE, VIRTUALIZER_MIN_GROW_COUNT);
			virtualizer = new JRSwapFileVirtualizer(VIRTUALIZER_MAX_PAGE_SIZE_CACHE, swapFile, true); // if more than
																										// VIRTUALIZER_MAX_PAGE_SIZE_CACHE
																										// pages we
																										// start saving
																										// data in the
																										// disck
			hmReportParameterList.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);

			switch (reportType) {
			case PDF: {
				// Add arial font for pdf
				toReturn = toReturn + ".pdf";
				HashMap fontMap = new HashMap();
				PdfFont font = new PdfFont(reportPath + "\\ARIAL.TTF", BaseFont.IDENTITY_H, true); // (String
																									// pdfFontName,
																									// String
																									// pdfEncoding,
																									// Boolean
																									// isPdfEmbedded)
				FontKey key = new FontKey("Arial", false, false); // (String fontName, Boolean bold, Boolean italic)
				fontMap.put(key, font);
				PdfFont font2 = new PdfFont(reportPath + "\\ARIALBD.TTF", BaseFont.IDENTITY_H, true);
				FontKey key2 = new FontKey("Arial", true, false);
				fontMap.put(key2, font2);
				PdfFont font3 = new PdfFont(reportPath + "\\ARIALBI.TTF", BaseFont.IDENTITY_H, true);
				FontKey key3 = new FontKey("Arial", true, true);
				fontMap.put(key3, font3);
				PdfFont font4 = new PdfFont(reportPath + "\\ARIALI.TTF", BaseFont.IDENTITY_H, true);
				FontKey key4 = new FontKey("Arial", false, true);
				fontMap.put(key4, font4);

				JRPdfExporter exporter = new JRPdfExporter();
				exporter.setParameter(JRPdfExporterParameter.CHARACTER_ENCODING, "UTF-8");
				exporter.setParameter(JRPdfExporterParameter.FONT_MAP, fontMap);

				if (report == null) {

					input = new FileInputStream(new File(reportPath + "\\" + sReportName));
					inputModified = generalBiz.InputStreamModified(reportPath, input, hmReportReplacerList, mLang,
							sReportName);
					design = JRXmlLoader.load(inputModified);
					input.close();
					inputModified.close();
					report = JasperCompileManager.compileReport(design);
				}

				JRParameter[] jpr = report.getParameters();
				for (JRParameter jrParameter : jpr) {
					if (jrParameter.getName().startsWith("CONFIG_PARAM_PRINT_ON_LOAD_PDF")) {
						exporter.setParameter(JRPdfExporterParameter.PDF_JAVASCRIPT, "this.print();");
					}
				}

				if (jrDataSource == null) // DB connection
				{
					System.out.println("Jasper report query: " + report.getQuery().getText());
					print = JasperFillManager.fillReport(report, hmReportParameterList, conn);
				} else // JRDataSource object
				{
					print = JasperFillManager.fillReport(report, hmReportParameterList, jrDataSource);
				}

				exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT, print);
				exporter.setParameter(JRPdfExporterParameter.OUTPUT_FILE_NAME, toReturn);
				exporter.exportReport();
				/*
				 * //render report without the font map toReturn = toReturn + ".pdf"; input =
				 * new FileInputStream(new File(reportPath + "\\" + sReportName)); inputModified
				 * = generalBiz.InputStreamModified(reportPath,input, hmReportReplacerList);
				 * design = JRXmlLoader.load(inputModified); input.close();
				 * inputModified.close(); report = JasperCompileManager.compileReport(design);
				 * System.out.println("Jasper report query: " + report.getQuery().getText());
				 * print = JasperFillManager.fillReport(report, hmReportParameterList, conn);
				 * JasperExportManager.exportReportToPdfFile(print,toReturn);
				 */
			}
				break;

			case SIMPLE_EXCEL: {
				toReturn = toReturn + ".xls";
				String sReportColumnExportCounter = "1";
				if (report == null) {

					input = new FileInputStream(new File(reportPath + "\\" + sReportName));
					inputModified = generalBiz.InputStreamModified(reportPath, input, hmReportReplacerList, mLang,
							sReportName);
					design = JRXmlLoader.load(inputModified);
					input.close();
					inputModified.close();
					report = JasperCompileManager.compileReport(design);
				}

				// build the excel data based on the sql in the xml file..
				Statement stmt = null;
				ResultSet rs = null;
				StringBuffer sbExcel = null;
				String sql = "";
				sql = report.getQuery().getText();
				stmt = conn.createStatement();
				rs = stmt.executeQuery(sql);
				ResultSetMetaData rsmd = rs.getMetaData();

				// trying to guess the number columns from the query that need to be shown in
				// the excel
				int colCount;
				HashMap map = (HashMap) design.getParametersMap();
				boolean isColumnNameFromHeader = true;
				String reportColumnExportCSV = "";
				if (map.containsKey("ReportColumnExportCSV")) // take the columns from SCV parameter
				{
					isColumnNameFromHeader = false;
					JRParameter jrpReportColumnExportCSV = (JRParameter) map.get("ReportColumnExportCSV");
					reportColumnExportCSV = (jrpReportColumnExportCSV.getDefaultValueExpression().getText())
							.replaceAll("\"", "");
					colCount = (reportColumnExportCSV.split(",")).length;
				} else if (map.containsKey("ReportColumnExportCounter")) // see if it defined as parameter (number in
																			// string type) -> take first
																			// ReportColumnExportCounter from report
																			// header
				{
					JRParameter jrpReportColumnExportCounter = (JRParameter) map.get("ReportColumnExportCounter");
					sReportColumnExportCounter = jrpReportColumnExportCounter.getDefaultValueExpression().getText();
					colCount = Integer.parseInt(sReportColumnExportCounter.replaceAll("\"", ""));
				} else if (report.getColumnHeader().getChildren().size() > 0) // take all from report header
				{
					colCount = report.getColumnHeader().getChildren().size();
				} else {
					colCount = 1; // make 1 as defualt
				}

				HashMap<String, String> mapColHeaders = new HashMap<String, String>();
				if (isColumnNameFromHeader) {
					Object[] oaCol = report.getColumnHeader().getChildren().toArray();
					for (int i = 0; i < oaCol.length; i++) {
						if (oaCol[i] instanceof JRBaseStaticText) {
							JRBaseStaticText st = (JRBaseStaticText) oaCol[i];
							mapColHeaders.put((st.getX() + 10000) + st.getKey(), st.getText());
						}
					}
				} else {
					String[] sCol = reportColumnExportCSV.split(",");
					for (int i = 0; i < sCol.length; i++) {
						mapColHeaders.put(String.valueOf(i + 10000), sCol[i]);
					}
				}

				List<String> sortedKeys = new ArrayList<String>(mapColHeaders.keySet());
				Collections.sort(sortedKeys);

				sbExcel = new StringBuffer();
				// build header
				sbExcel.append("<table border=1>\n");
				sbExcel.append("<tr>\n<td colspan='" + colCount + "' style='font-weight:bold'>")
						.append(mLang != null ? generalBiz.modifiedLangOnString(sReportParameterTitle, mLang)
								: sReportParameterTitle)
						.append("</td>\n</tr>\n");
				sbExcel.append("<tr>\n<td colspan='" + colCount + "' style='font-weight:bold'>")
						.append(mLang != null ? generalBiz.modifiedLangOnString(sReportParameterFilterDisplay, mLang)
								: sReportParameterFilterDisplay)
						.append("</td>\n</tr>\n");
				sbExcel.append("<tr>\n");

				int index = 0;
				for (String key_ : sortedKeys) {
					if (index++ < colCount) {
						sbExcel.append("   <td>" + mapColHeaders.get(key_) + "</td>\n");
					}
				}
				sbExcel.append("</tr>\n");

				// build details
				while (rs.next()) {
					sbExcel.append("<tr>\n");
					for (int j = 1; j <= colCount; j++) {
						sbExcel.append("   <td>" + ((rs.getObject(j) == null) ? "" : rs.getObject(j)) + "</td>\n");
					}
					sbExcel.append("</tr>\n");
				}

				// close it
				sbExcel.append("</table>\n");

				// output to a file
				// FileWriter out = new FileWriter(toReturn);
				FileOutputStream fileStream = new FileOutputStream(new File(toReturn));
				OutputStreamWriter out = new OutputStreamWriter(fileStream, "UTF-8");

				out.write(sbExcel.toString());
				out.flush();
				out.close();
			}
				break;

			case JXL_EXCEL: {
				toReturn = toReturn + ".xls";
				if (report == null) {

					input = new FileInputStream(new File(reportPath + "\\" + sReportName));
					inputModified = generalBiz.InputStreamModified(reportPath, input, hmReportReplacerList, mLang,
							sReportName);
					design = JRXmlLoader.load(inputModified);
					input.close();
					inputModified.close();
					design.setPageFooter(null); // hide the page fotter
					design.setLastPageFooter(null); // hide the last page fotter
					report = JasperCompileManager.compileReport(design);
				}

				if (jrDataSource == null) // DB connection
				{
					System.out.println("Jasper report query: " + report.getQuery().getText());
					print = JasperFillManager.fillReport(report, hmReportParameterList, conn);
				} else // JRDataSource object
				{
					print = JasperFillManager.fillReport(report, hmReportParameterList, jrDataSource);
				}

				JExcelApiExporter exporter = new JExcelApiExporter();
				exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
				exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, toReturn);
				exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_CELL_BORDER, false);
				exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, false);
				// exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, true);
				exporter.setParameter(JRXlsExporterParameter.CHARACTER_ENCODING, "UTF-8");

				exporter.exportReport();

				if (input != null) {
					input.close();
				}

				if (inputModified != null) {
					inputModified.close();
				}

			}
				break;

			case CSV_EXCEL: {
				toReturn = toReturn + ".csv";
				if (report == null) {

					input = new FileInputStream(new File(reportPath + "\\" + sReportName));
					inputModified = generalBiz.InputStreamModified(reportPath, input, hmReportReplacerList, mLang,
							sReportName);
					design = JRXmlLoader.load(inputModified);
					input.close();
					inputModified.close();
					design.setPageFooter(null); // hide the page fotter
					design.setLastPageFooter(null); // hide the last page fotter
					report = JasperCompileManager.compileReport(design);
				}

				if (jrDataSource == null) // DB connection
				{
					System.out.println("Jasper report query: " + report.getQuery().getText());
					print = JasperFillManager.fillReport(report, hmReportParameterList, conn);
				} else // JRDataSource object
				{
					print = JasperFillManager.fillReport(report, hmReportParameterList, jrDataSource);
				}

				JRCsvExporter exporterCSV = new JRCsvExporter();
				exporterCSV.setParameter(JRXlsExporterParameter.JASPER_PRINT, print);
				exporterCSV.setParameter(JRXlsExporterParameter.CHARACTER_ENCODING, HEBREW_ENCODING_EXCEL_CSV);
				exporterCSV.setParameter(JRXlsExporterParameter.OUTPUT_FILE_NAME, toReturn);
				exporterCSV.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, true);

				exporterCSV.exportReport();

				if (input != null) {
					input.close();
				}

				if (inputModified != null) {
					inputModified.close();
				}
			}
				break;
			}
		} finally {
			try {
				if (virtualizer != null) {
					virtualizer.cleanup();
				}

//		        if(!urlConnection.equals("")) 
//		        {
//		            System.out.println("JasperReportGenerator.getPath - connection.close() conn=" + conn);
////		            conn.close();
//		        }
			} catch (Exception e) {
				System.out.println(
						"JasperReportGenerator.getPath - close connection failed conn=" + conn + ", e=" + e.toString());
			}
		}
		return toReturn;
	}

	public String getCompiledPath(String sReportID, String sReportName, String reportPath, String reportPathTmp,
			HashMap<String, String> hmReportReplacerList) throws Exception {
		// init..
		GeneralBiz generalBiz = new GeneralBiz();
//		StringBuildesReportNamele = new StringBuilder(sReportName);
		if (!generalBiz.isGenerateCompileFileOnDevelop(reportPath)) {

			try {
				File reportCompiled = new File(reportPath + "\\" + sReportName.replace(".xml", "") + ".jasper");

				if (reportCompiled.exists()) {
					return reportPath + "\\" + sReportName.replace(".xml", "") + ".jasper"; // TODO check it!!!!!
				}
			} catch (Exception e) {
				System.out.println("ERROR! failed loading the compiled file. e=" + e.getMessage());
			}
		}

		String toReturn = reportPathTmp + "\\" + sReportID + (new Date()).getTime();
		InputStream input = null;
		JasperDesign design = null;
		InputStream inputModified = null;

		input = new FileInputStream(new File(reportPath + "\\" + sReportName));
		inputModified = generalBiz.InputStreamModified(reportPath, input, hmReportReplacerList, mLang, sReportName);

		// copy inputModified to is1 and is2...
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = inputModified.read(buffer)) > -1) {
			baos.write(buffer, 0, len);
		}
		baos.flush();
		InputStream is1 = new ByteArrayInputStream(baos.toByteArray());
		InputStream is2 = new ByteArrayInputStream(baos.toByteArray());

		// print the sql using is2
		if (jrDataSource == null) // DB connection -> print the sql
		{
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is1));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				String sFileContenet = sb.toString();
				System.out.println("Sub report query: "
						+ sFileContenet.substring(sFileContenet.indexOf("<queryString>") + "<queryString>".length(),
								sFileContenet.indexOf("</queryString>")));
			} catch (Exception e) {
				System.out.println("Can't get sub report query!");
			}
		}

		// create the report using is2
		design = JRXmlLoader.load(is2);

		try {
			JasperCompileManager.compileReportToFile(design, toReturn);
		} catch (Exception e) {
			System.out.println("JasperReportGenerator.getCompiledPath - Error in compile jasper!  " + e.toString());
		}

		// close streams..
		input.close();
		inputModified.close();
		is1.close();
		is2.close();

		return toReturn;
	}

	public JasperReport getCompiled(String sReportID, String sReportName, String reportPath, String reportPathTmp,
			HashMap<String, String> hmReportReplacerList) throws Exception {
		JasperReport toReturn = null;
		GeneralBiz generalBiz = new GeneralBiz();

		if (!generalBiz.isGenerateCompileFileOnDevelop(reportPath)) {

			try {
				File reportCompiled = new File(reportPath + "\\" + sReportName.replace(".xml", "") + ".jasper");

				if (reportCompiled.exists()) {
					toReturn = (JasperReport) JRLoader
							.loadObject(new File(reportPath + "\\" + sReportName.replace(".xml", "") + ".jasper"));// TODO
																													// check
																													// it!!!!!
					return toReturn;
				}
			} catch (Exception e) {
				toReturn = null;
				System.out.println("ERROR! failed loading the compiled file. e=" + e.getMessage());
			}
		}

		// init..
//        		StringBuildesReportNamele = new StringBuilder(sReportName);

		InputStream input = null;
		JasperDesign design = null;
		InputStream inputModified = null;
		input = new FileInputStream(new File(reportPath + "\\" + sReportName));
		inputModified = generalBiz.InputStreamModified(reportPath, input, hmReportReplacerList, mLang, sReportName);

		// copy inputModified to is1 and is2...
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len;
		while ((len = inputModified.read(buffer)) > -1) {
			baos.write(buffer, 0, len);
		}
		baos.flush();
		InputStream is1 = new ByteArrayInputStream(baos.toByteArray());
		InputStream is2 = new ByteArrayInputStream(baos.toByteArray());

		// print the sql using is2
		if (jrDataSource == null) // DB connection -> print the sql
		{
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is1));
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				String sFileContenet = sb.toString();
				System.out.println("Sub report query: "
						+ sFileContenet.substring(sFileContenet.indexOf("<queryString>") + "<queryString>".length(),
								sFileContenet.indexOf("</queryString>")));
			} catch (Exception e) {
				System.out.println("Can't get sub report query!");
			}
		}

		// create the report using is2
		design = JRXmlLoader.load(is2);

		try {
			toReturn = JasperCompileManager.compileReport(design);
		} catch (Exception e) {
			System.out.println("JasperReportGenerator.getCompiled - Error in compile jasper!  " + e.toString());
		}

		// close streams..
		input.close();
		inputModified.close();
		is1.close();
		is2.close();

		return toReturn;
	}

	public void setMLang(HashMap<String, String> mLang) {
		this.mLang = mLang;
	}

	public HashMap<String, String> getMLang() {
		return mLang;
	}

//        public void releaseResources() {
//        	DbBasicProvider db = DbBasicProvider.getInstance(urlConnection,userName,userPassword);
//        	db.releaseConnection();
//        }
}
