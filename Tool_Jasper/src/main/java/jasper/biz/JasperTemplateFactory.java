package jasper.biz;

import jasper.dal.DbBasicProvider;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;


/**
 * Creates reports for predefined cases.
 */
public class JasperTemplateFactory
{
//        String dbUrl = "";
//        String dbUser= "";
//        String dbPassword= "";
		Connection conn = null;
        String xmlTemplatePath= "";
        String xmlPath= "";
        String xmlTmpPath= "";
        private final String PORTRAIT_XML = "tPortrait.xml";
        private final String LANDSCAPE_XML = "tLandscape.xml";
        private final String PORTRAIT_XML_SUBREPROT = "tPortraitSubreport.xml";
        private final String LANDSCAPE_XML_SUBREPROT = "tLandscapeSubreport.xml";
        private final String PORTRAIT_XML_SUBREPROT_XLS = "tPortraitSubreportXls.xml";
        private final String LANDSCAPE_XML_SUBREPROT_XLS = "tLandscapeSubreportXls.xml";
        private final String AT_GROUP_PORTRAIT_XML = "tATGroupedpPortrait.xml";
        private final String  AT_GROUP_LANDSCAPE_XML = "tATGroupedpLandscape.xml";
        private final String  AT_GROUP_PORTRAIT_XML_SUBREPROT = "tATGroupedpPortraitSubreport.xml";
        private final String  AT_GROUP_LANDSCAPE_XML_SUBREPROT = "tATGroupedpLandscapeSubreport.xml";
        private final int PORTRAIT_WIDTH = 557;
        private final int LANDSCAPE_WIDTH = 804;
        private boolean isDataInjection = false;
        private String dataInjectionFilterFieldName = "";
        private String dataInjectionFilterParamName = "";
        private HashMap<String,String> mLang = null;
        private String subReportTitle = "";
 
	public JasperTemplateFactory(Connection conn, String xmlPath, String xmlTemplatePath, String xmlTmpPath)
	{
//		this.dbUrl = dbUrl;
//		this.dbUser = dbUser;
//		this.dbPassword = dbPassword;
		this.conn = conn;
		this.xmlPath = xmlPath;
		this.xmlTemplatePath = xmlTemplatePath;
		this.xmlTmpPath = xmlTmpPath;
	}
        
        public JasperTemplateFactory(Connection conn, String xmlPath, String xmlTemplatePath, String xmlTmpPath, HashMap<String,String> mLang)
        {
                this( conn,  xmlPath,  xmlTemplatePath,  xmlTmpPath);
                this.mLang = mLang;
        }
        
        public JasperTemplateFactory(Connection conn, String xmlPath, String xmlTemplatePath, String xmlTmpPath, Object mLang)
        {
                this( conn,  xmlPath,  xmlTemplatePath,  xmlTmpPath);
                try 
                {
                    this.mLang = (HashMap<String, String>)mLang; 
                }
                catch (Exception e) {
                    
                } 
        }
        
        public JasperTemplateFactory(Connection conn, String xmlPath, String xmlTemplatePath, String xmlTmpPath, HashMap<String,String> mLang, String subReportTitle)
        {
                this( conn,  xmlPath,  xmlTemplatePath,  xmlTmpPath);
                this.mLang = mLang;
                if(subReportTitle != null) 
                {
                    this.subReportTitle = subReportTitle;
                }
        }
         
	public String getJasperReport(String reportId, JasperReportType reportType, String reportTitle, String reportFilterDisplay, String tableName, String reportFilter) throws Exception 
	{
		return getJasperReport( reportId, reportType, reportTitle, reportFilterDisplay, tableName, reportFilter, true);
	}
         
	public String getJasperReport(String reportId, JasperReportType reportType, String reportTitle, String reportFilterDisplay, String tableName, String reportFilter, boolean isPortrait) throws Exception
	{
		String toReturn = null;
		String reportTemplate = "";
		String templateSql = " select  t.* from " + tableName + " t where 1=1 " + reportFilter;
		String templateField = "<field name=\"@fieldName@\" class=\"java.lang.String\"/>\n";
		String[] columnsPercentSize = null;
		int elementWidth = 0;
		int colIndex = 0;
		int xPosition = 0;
		DbBasicProvider dbProider = new DbBasicProvider();
    
		String templateFieldHeader = "\n" + "<staticText>\n" + "<reportElement\n" + "  mode=\"Opaque\"\n" + "  x=\"@elementPosition@\"\n" + "  y=\"0\"\n" + "  width=\"@elementWidth@\"\n" + "  height=\"35\"\n" + "  backcolor=\"@J!jasperHeaderColor@\"\n" + "  key=\"staticText-3\"/>\n" + "<box leftPadding=\"2\" rightPadding=\"2\" topPadding=\"2\" bottomPadding=\"2\">\n" + "  <pen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <topPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <leftPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <bottomPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <rightPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "</box>\n" + "<textElement>\n" + "  <font pdfFontName=\"@J!jasperHeaderFont@\" size=\"@J!jasperHeaderSize@\" isBold=\"true\"/>\n" + "</textElement>\n" + "<text><![CDATA[@fieldName@]]></text>\n" + "</staticText>\n";
		String templateFieldDetails = "<textField isStretchWithOverflow=\"true\" isBlankWhenNull=\"true\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >\n" + "    <reportElement\n" + "        x=\"@elementPosition@\"\n" + "        y=\"0\"\n" + "        width=\"@elementWidth@\"\n" + "        height=\"20\"\n" + "       key=\"textField-8\"\n" + "	isPrintWhenDetailOverflows=\"true\"\n" +  "      stretchType=\"RelativeToTallestObject\"/>\n"  + "    <box leftPadding=\"2\" rightPadding=\"2\" topPadding=\"2\" bottomPadding=\"2\">\n" + "      <pen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <topPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <leftPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <bottomPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <rightPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "    </box>\n" + "    <textElement  >\n" + "      <font fontName=\"@J!jasperFont@\" pdfFontName=\"@J!jasperFontPdf@\" isPdfEmbedded =\"@J!jasperIsPdfEmbedded@\" size=\"@J!jasperDetailsSize@\"/>\n" + "    </textElement>\n" + "    <textFieldExpression   class=\"java.lang.String\"><![CDATA[$F{@fieldName@}]]></textFieldExpression>\n" + "  </textField>";
    
		//get xml template  
		FileInputStream input = new FileInputStream(new File(xmlTemplatePath + "\\" + (isPortrait ? PORTRAIT_XML:LANDSCAPE_XML)));
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			sb.append(line + "\n");
		}
		reportTemplate = sb.toString();
    
		//get Column sizes (if defined) 
		columnsPercentSize = dbProider.getColumnSizeByTableName(conn, tableName,reportFilter);
                int sumPercent = 100;
                if (columnsPercentSize != null) 
                {
                    sumPercent =  getSumPercentgetSumPercent(columnsPercentSize);
                }
    
		//generate instance elements using the table info       
		List<String> colList = dbProider.getTableColumne(conn, tableName); 
		Iterator colIterator = colList.iterator();
    
		StringBuilder sbFields = new StringBuilder();
		StringBuilder sbHeader = new StringBuilder();
		StringBuilder sbDetails = new StringBuilder();
    
		if (columnsPercentSize == null)
		{ 
			elementWidth = (isPortrait ? PORTRAIT_WIDTH:LANDSCAPE_WIDTH) / (colList.size()); 
		}
		else
		{
			// if columnsPercentSize defined the elementWidth is the report width
                       elementWidth = (isPortrait ? PORTRAIT_WIDTH:LANDSCAPE_WIDTH); 
		}
    
		while (colIterator.hasNext())
		{
			String colName = (String) colIterator.next();
    
			//fields
			sbFields.append(templateField.replace("@fieldName@", colName));
			
			if (columnsPercentSize == null)
			//All columns are in the same size
			{
				//fields header 
				sbHeader.append(templateFieldHeader.replace("@elementPosition@", String.valueOf(colIndex * elementWidth)).replace("@elementWidth@", String.valueOf(elementWidth)).replace("@fieldName@", mLang == null ? colName : "@L!" + colName + "@" ));
				//fields detail
				sbDetails.append(templateFieldDetails.replace("@elementPosition@", String.valueOf(colIndex * elementWidth)).replace("@elementWidth@", String.valueOf(elementWidth)).replace("@fieldName@", colName));
			}
			else
			// Defined columns size
			{
				 if(Integer.parseInt(columnsPercentSize[colIndex].trim()) != 0) 
				 {
				     //fields header 
				     sbHeader.append(templateFieldHeader.replace("@elementPosition@", String.valueOf(xPosition)).replace("@elementWidth@", String.valueOf((elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent)).replace("@fieldName@", mLang == null ? colName : "@L!" + colName + "@"));
				     //fields detail
				     sbDetails.append(templateFieldDetails.replace("@elementPosition@", String.valueOf(xPosition)).replace("@elementWidth@", String.valueOf((elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent)).replace("@fieldName@", colName));
				     xPosition += (elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent;
				 }
			}
			//update colIndex
			colIndex++;
			
		} 
                
                reportTemplate = reportTemplate.replace("@sql@", templateSql);
		reportTemplate = reportTemplate.replace("@fields@", sbFields.toString());
		reportTemplate = reportTemplate.replace("@headers@", sbHeader.toString());
		reportTemplate = reportTemplate.replace("@details@", sbDetails.toString());
	        reportTemplate = reportTemplate.replace("@title@", reportTitle);
	        reportTemplate = reportTemplate.replace("@filterDisplay@", reportFilterDisplay);
		 
		//String reportPath, String reportPathTmp) 
		//JasperReportGenerator jrg = new JasperReportGenerator(conn); 
                
                JasperReportGenerator  jrg = new JasperReportGenerator(conn ,mLang); 
    
		//config parameters and replacers
		HashMap<String, String> hmReplacers = new HashMap<String, String>();
		HashMap<String, Object> hmParameters = new HashMap<String, Object>();
		hmReplacers.put("@replacerAll@", reportTemplate);
		//hmReplacers.put("@replacer2@", reportFilter);
    
		//call the jasperCore  get result 
		toReturn = jrg.getPath(reportId, "rATPlaceHolder", reportType, "", "", hmReplacers, hmParameters, xmlPath, xmlTmpPath);
    
		return toReturn;
	}
	
	public ByteArrayOutputStream getJasperReportStream(String reportId, JasperReportType reportType, String reportTitle, String reportFilterDisplay, String tableName, String reportFilter) throws Exception
	{
		return getJasperReportStream(reportId, reportType, reportTitle, reportFilterDisplay, tableName, reportFilter, true);
	}
         
	public ByteArrayOutputStream getJasperReportStream(String reportId, JasperReportType reportType, String reportTitle, String reportFilterDisplay, String tableName, String reportFilter, boolean isPortrait) throws Exception
	{
		ByteArrayOutputStream toReturn = null;
		String reportTemplate = "";
		String templateSql = " select  t.* from " + tableName + " t where 1=1 " + reportFilter;
		String templateField = "<field name=\"@fieldName@\" class=\"java.lang.String\"/>\n";
		String[] columnsPercentSize = null;
		int elementWidth = 0;
		int colIndex = 0;
		int xPosition = 0;
		DbBasicProvider dbProider = new DbBasicProvider();
	
		String templateFieldHeader = "\n" + "<staticText>\n" + "<reportElement\n" + "  mode=\"Opaque\"\n" + "  x=\"@elementPosition@\"\n" + "  y=\"0\"\n" + "  width=\"@elementWidth@\"\n" + "  height=\"35\"\n" + "  backcolor=\"@J!jasperHeaderColor@\"\n" + "  key=\"staticText-3\"/>\n" + "<box leftPadding=\"2\" rightPadding=\"2\" topPadding=\"2\" bottomPadding=\"2\">\n" + "  <pen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <topPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <leftPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <bottomPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <rightPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "</box>\n" + "<textElement>\n" + "  <font pdfFontName=\"@J!jasperHeaderFont@\" size=\"@J!jasperHeaderSize@\" isBold=\"true\"/>\n" + "</textElement>\n" + "<text><![CDATA[@fieldName@]]></text>\n" + "</staticText>\n";
		String templateFieldDetails = "<textField isStretchWithOverflow=\"true\" isBlankWhenNull=\"true\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >\n" + "    <reportElement\n" + "        x=\"@elementPosition@\"\n" + "        y=\"0\"\n" + "        width=\"@elementWidth@\"\n" + "        height=\"20\"\n" + "       key=\"textField-8\"\n" + "     isPrintWhenDetailOverflows=\"true\"\n" +  "      stretchType=\"RelativeToTallestObject\"/>\n"  + "    <box leftPadding=\"2\" rightPadding=\"2\" topPadding=\"2\" bottomPadding=\"2\">\n" + "      <pen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <topPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <leftPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <bottomPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <rightPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "    </box>\n" + "    <textElement  >\n" + "      <font fontName=\"@J!jasperFont@\" pdfFontName=\"@J!jasperFontPdf@\" isPdfEmbedded =\"@J!jasperIsPdfEmbedded@\" size=\"@J!jasperDetailsSize@\"/>\n" + "    </textElement>\n" + "    <textFieldExpression   class=\"java.lang.String\"><![CDATA[$F{@fieldName@}]]></textFieldExpression>\n" + "  </textField>";
	    
		//get xml template  
		FileInputStream input = new FileInputStream(new File(xmlTemplatePath + "\\" + (isPortrait ? PORTRAIT_XML:LANDSCAPE_XML)));
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			sb.append(line + "\n");
		}
		reportTemplate = sb.toString();
	
		//get Column sizes (if defined) 
		columnsPercentSize = dbProider.getColumnSizeByTableName(conn, tableName,reportFilter);
                int sumPercent = 100;
                if (columnsPercentSize != null) 
                {
                    sumPercent =  getSumPercentgetSumPercent(columnsPercentSize);
                }
	
		//generate instance elements using the table info       
		List<String> colList = dbProider.getTableColumne(conn, tableName);
		Iterator colIterator = colList.iterator();
	
		StringBuilder sbFields = new StringBuilder();
		StringBuilder sbHeader = new StringBuilder();
		StringBuilder sbDetails = new StringBuilder();
	
		if (columnsPercentSize == null)
		{ 
			 elementWidth = (isPortrait ? PORTRAIT_WIDTH:LANDSCAPE_WIDTH) / (colList.size()); 
		}
		else
		{
			// if columnsPercentSize defined the elementWidth is the report width
			elementWidth = (isPortrait ? PORTRAIT_WIDTH:LANDSCAPE_WIDTH); 
		}
	
		while (colIterator.hasNext())
		{
			String colName = (String) colIterator.next();
	
			//fields
			sbFields.append(templateField.replace("@fieldName@", colName));
			 
			if (columnsPercentSize == null)
			//All columns are in the same size
			{
				//fields header 
				sbHeader.append(templateFieldHeader.replace("@elementPosition@", String.valueOf(colIndex * elementWidth)).replace("@elementWidth@", String.valueOf(elementWidth)).replace("@fieldName@", mLang == null ? colName : "@L!" + colName + "@"));
				//fields detail
				sbDetails.append(templateFieldDetails.replace("@elementPosition@", String.valueOf(colIndex * elementWidth)).replace("@elementWidth@", String.valueOf(elementWidth)).replace("@fieldName@", colName));
			}
			else
			// Defined columns size
			{
			    if(Integer.parseInt(columnsPercentSize[colIndex].trim()) != 0) 
                            {
				//fields header 
				sbHeader.append(templateFieldHeader.replace("@elementPosition@", String.valueOf(xPosition)).replace("@elementWidth@", String.valueOf((elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent)).replace("@fieldName@", mLang == null ? colName : "@L!" + colName + "@"));
				//fields detail
				sbDetails.append(templateFieldDetails.replace("@elementPosition@", String.valueOf(xPosition)).replace("@elementWidth@", String.valueOf((elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent)).replace("@fieldName@", colName));
				xPosition += (elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent;
                            }
			}
			//update colIndex
			colIndex++;
			 
		} 
		
                reportTemplate = reportTemplate.replace("@sql@", templateSql);
		reportTemplate = reportTemplate.replace("@fields@", sbFields.toString());
		reportTemplate = reportTemplate.replace("@headers@", sbHeader.toString());
	        reportTemplate = reportTemplate.replace("@details@", sbDetails.toString());
		reportTemplate = reportTemplate.replace("@title@", reportTitle);
	        reportTemplate = reportTemplate.replace("@filterDisplay@", reportFilterDisplay);
	 
		 JasperReportGenerator jrg  = new JasperReportGenerator(conn,mLang); 
		 
		//config parameters and replacers
		HashMap<String, String> hmReplacers = new HashMap<String, String>();
		HashMap<String, Object> hmParameters = new HashMap<String, Object>();
		hmReplacers.put("@replacerAll@", reportTemplate);
		//hmReplacers.put("@replacer2@", reportFilter);
	
		//call the jasperCore get result 
		toReturn = jrg.getByteArrayOutputStream(reportId, "rATPlaceHolder", reportType, "", "", hmReplacers, hmParameters, xmlPath, xmlTmpPath);
	
		return toReturn;
	}
	
	public String getJasperReportCompilePath(String reportId, String tableName, String reportFilter) throws Exception
	{
		return getJasperReportCompilePath(reportId, tableName, reportFilter, true);
	}
         
	public String getJasperReportCompilePath(String reportId, String tableName, String reportFilter, boolean isPortrait) throws Exception
	{
		String toReturn = null;
		String reportTemplate = "";
		String templateSql = " select  t.* from " + tableName + " t where 1=1 " + reportFilter;
		String templateField = "<field name=\"@fieldName@\" class=\"java.lang.String\"/>\n";
		String[] columnsPercentSize = null;
		int elementWidth = 0;
		int colIndex = 0;
		int xPosition = 0;
		DbBasicProvider dbProider = new DbBasicProvider();
	
		String templateFieldHeader = "\n" + "<staticText>\n" + "<reportElement\n" + "  mode=\"Opaque\"\n" + "  x=\"@elementPosition@\"\n" + "  y=\"@elementYPosition@\"\n" + "  width=\"@elementWidth@\"\n" + "  height=\"35\"\n" + "  backcolor=\"@J!jasperHeaderColor@\"\n" + "  key=\"staticText-3\"/>\n" + "<box leftPadding=\"2\" rightPadding=\"2\" topPadding=\"2\" bottomPadding=\"2\">\n" + "  <pen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <topPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <leftPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <bottomPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <rightPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "</box>\n" + "<textElement>\n" + "  <font pdfFontName=\"@J!jasperHeaderFont@\" size=\"@J!jasperHeaderSize@\" isBold=\"true\"/>\n" + "</textElement>\n" + "<text><![CDATA[@fieldName@]]></text>\n" + "</staticText>\n"; //02122014 add title from view (@elementYPosition@  is 20 if title else 0)
		String templateFieldDetails = "<textField isStretchWithOverflow=\"true\" isBlankWhenNull=\"true\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >\n" + "    <reportElement\n" + "        x=\"@elementPosition@\"\n" + "        y=\"0\"\n" + "        width=\"@elementWidth@\"\n" + "        height=\"20\"\n" + "       key=\"textField-8\"\n" + "     isPrintWhenDetailOverflows=\"true\"\n" +  "      stretchType=\"RelativeToTallestObject\"/>\n"  + "    <box leftPadding=\"2\" rightPadding=\"2\" topPadding=\"2\" bottomPadding=\"2\">\n" + "      <pen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <topPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <leftPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <bottomPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <rightPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "    </box>\n" + "    <textElement  >\n" + "      <font fontName=\"@J!jasperFont@\" pdfFontName=\"@J!jasperFontPdf@\" isPdfEmbedded =\"@J!jasperIsPdfEmbedded@\" size=\"@J!jasperDetailsSize@\"/>\n" + "    </textElement>\n" + "    <textFieldExpression   class=\"java.lang.String\"><![CDATA[$F{@fieldName@}]]></textFieldExpression>\n" + "  </textField>";
	    String templateSubReportTitle = "";
	    if(subReportTitle.equals("")) 
	    {
	        templateSubReportTitle = "<textField isStretchWithOverflow=\"false\" isBlankWhenNull=\"false\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >\n" + 
	        "      <reportElement\n" + 
	        "              x=\"0\"\n" + 
	        "              y=\"0\"\n" + 
	        "              width=\"500\"\n" + 
	        "              height=\"20\"\n" + 
	        "              forecolor=\"@J!jasperSubReportHeaderColor@\"\n" +
	        "              key=\"textField-9\"/>\n" + 
	        "      <box></box>\n" + 
	        "      <textElement>\n" + 
	        "              <font pdfFontName=\"@J!jasperSubReportHeaderFont@\" size=\"@J!jasperSubReportHeaderSize@\" isBold=\"true\"/>\n" + 
	        "      </textElement>\n" + 
	        "      <textFieldExpression   class=\"java.lang.String\"><![CDATA[$F{JR_SUBREPORT_TITLE}]]></textFieldExpression>\n" + 
	        "</textField>";  //02122014 add title from view
	    }
	    else 
	    {
	        templateSubReportTitle = "<staticText >\n" + 
	        "      <reportElement\n" + 
	        "              x=\"0\"\n" + 
	        "              y=\"0\"\n" + 
	        "              width=\"500\"\n" + 
	        "              height=\"20\"\n" + 
	        "              forecolor=\"@J!jasperSubReportHeaderColor@\"\n" +
	        "              key=\"textField-9\"/>\n" + 
	        "      <box></box>\n" + 
	        "      <textElement>\n" + 
	        "              <font pdfFontName=\"@J!jasperSubReportHeaderFont@\" size=\"@J!jasperSubReportHeaderSize@\" isBold=\"true\"/>\n" + 
	        "      </textElement>\n" + 
	        "      <text><![CDATA[" + subReportTitle + "]]></text>\n" + 
	        "</staticText>";  //19022015 add title from setter / Constructor
	    }
                             
		//get xml template  
		FileInputStream input = new FileInputStream(new File(xmlTemplatePath + "\\" + (isPortrait ? PORTRAIT_XML_SUBREPROT:LANDSCAPE_XML_SUBREPROT)));
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			sb.append(line + "\n");
		}
		reportTemplate = sb.toString();
	
		//get Column sizes (if defined) 
		columnsPercentSize = dbProider.getColumnSizeByTableName(conn,tableName,reportFilter);
                int sumPercent = 100;
                if (columnsPercentSize != null) 
                {
                    sumPercent =  getSumPercentgetSumPercent(columnsPercentSize);
                }
            
		//generate instance elements using the table info       
		List<String> colList = dbProider.getTableColumne(conn,tableName);
		Iterator colIterator = colList.iterator();
	
		StringBuilder sbFields = new StringBuilder(dataInjectionFilterFieldName != null && !dataInjectionFilterFieldName.trim().equals("") ? "<field name=\"" + dataInjectionFilterFieldName + "\" class=\"java.lang.Object\"/>\n" : "");
		StringBuilder sbHeader = new StringBuilder();
		StringBuilder sbDetails = new StringBuilder();
	
		if (columnsPercentSize == null)
		{ 
			elementWidth = (isPortrait ? PORTRAIT_WIDTH:LANDSCAPE_WIDTH) / (colList.size()); 
		}
		else
		{
			// if columnsPercentSize defined the elementWidth is the report width
			elementWidth = (isPortrait ? PORTRAIT_WIDTH:LANDSCAPE_WIDTH);
		}
                
	         boolean isTitle = colList.contains("jr_subreport_title") || colList.contains("JR_SUBREPORT_TITLE") || !subReportTitle.equals(""); //02122014 add title from view
                 if(isTitle)
                 {
                     sbFields.append(templateField.replace("@fieldName@", "JR_SUBREPORT_TITLE"));
                     sbHeader.append(templateSubReportTitle);
                     templateFieldHeader = templateFieldHeader.replace("@elementYPosition@","20");
                 }
                 else 
                 {
                     templateFieldHeader = templateFieldHeader.replace("@elementYPosition@","0");
                 }
	
		while (colIterator.hasNext())
		{
			String colName = (String) colIterator.next();
                        
                        if(colName.toLowerCase().equals("jr_subreport_title")) //02122014 add title from view (pass jr_subreport_title field)
                        {
                            continue;
                        }
	
			//fields
			sbFields.append(templateField.replace("@fieldName@", colName));
                        
                        if (columnsPercentSize == null)
                        //All columns are in the same size
                        {
                                //fields header 
                                sbHeader.append(templateFieldHeader.replace("@elementPosition@", String.valueOf(colIndex * elementWidth)).replace("@elementWidth@", String.valueOf(elementWidth)).replace("@fieldName@", mLang == null ? colName : "@L!" + colName + "@"));
                                //fields detail
                                sbDetails.append(templateFieldDetails.replace("@elementPosition@", String.valueOf(colIndex * elementWidth)).replace("@elementWidth@", String.valueOf(elementWidth)).replace("@fieldName@", colName));
                        }
                        else
                        // Defined columns size
                        {
                                if(Integer.parseInt(columnsPercentSize[colIndex].trim()) != 0) 
                                {
                                    //fields header 
                                    sbHeader.append(templateFieldHeader.replace("@elementPosition@", String.valueOf(xPosition)).replace("@elementWidth@", String.valueOf((elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent)).replace("@fieldName@", mLang == null ? colName : "@L!" + colName + "@"));
                                    //fields detail
                                    sbDetails.append(templateFieldDetails.replace("@elementPosition@", String.valueOf(xPosition)).replace("@elementWidth@", String.valueOf((elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent)).replace("@fieldName@", colName));
                                    xPosition += (elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent;
                                }
                        }
                        //update colIndex
                        colIndex++;
		} 
		
                //data injection
                if(isDataInjection) 
                { 
                    if(dataInjectionFilterParamName != null && !dataInjectionFilterParamName.trim().equals("") && dataInjectionFilterFieldName != null && !dataInjectionFilterFieldName.trim().equals("")) // with filter
                    {
                        reportTemplate = reportTemplate.replace("@FILTER_PARAM_NAME@", dataInjectionFilterParamName);
                        reportTemplate = reportTemplate.replace("@filterExpression@","<filterExpression><![CDATA[new Boolean(String.valueOf($F{" +dataInjectionFilterFieldName + "}).equals(String.valueOf($P{" + dataInjectionFilterParamName + "})))]]></filterExpression>");
                    }
                    else // no filter
                    {
                        reportTemplate = reportTemplate.replace("@FILTER_FIELD_NAME@", "FILTER_DUMMY");
                        reportTemplate = reportTemplate.replace("@filterExpression@","");
                    }
                   
                    reportTemplate = reportTemplate.replace("@sql@", "/* " + templateSql + " */");
                    //...set data Injection values back to default
                    isDataInjection = false;
                    this.dataInjectionFilterFieldName = "";
                    this.dataInjectionFilterParamName = "";
                }
                else 
                {
                    reportTemplate = reportTemplate.replace("@FILTER_FIELD_NAME@", "FILTER_DUMMY");
                    reportTemplate = reportTemplate.replace("@filterExpression@","");
                    reportTemplate = reportTemplate.replace("@sql@", templateSql);
                }
                 
		reportTemplate = reportTemplate.replace("@fields@", sbFields.toString());
		reportTemplate = reportTemplate.replace("@headers@", sbHeader.toString());
		reportTemplate = reportTemplate.replace("@details@", sbDetails.toString());
	        reportTemplate = reportTemplate.replace("@headerBandHeight@", isTitle ? "55" : "35"); //02122014 add title from view
	 
		 JasperReportGenerator jrg  = new JasperReportGenerator(conn,mLang); 
		  
		//config parameters and replacers
		HashMap<String, String> hmReplacers = new HashMap<String, String>(); 
		hmReplacers.put("@replacerAll@", reportTemplate); 
	 
		toReturn = jrg.getCompiledPath(reportId, "rATPlaceHolder", xmlPath, xmlTmpPath,hmReplacers);
	
		return toReturn;
	}
         
        public JasperReport getJasperReportCompiled(String reportId, String tableName, String reportFilter) throws Exception
        {
                 return getJasperReportCompiled(reportId, tableName, reportFilter, true);
        }
        
        public JasperReport getJasperReportCompiled(String reportId, String tableName, String reportFilter, boolean isPortrait) throws Exception
        {
                 JasperReport toReturn = null;
                 String reportTemplate = "";
                 String templateSql = " select  t.* from " + tableName + " t where 1=1 " + reportFilter;
                 String templateField = "<field name=\"@fieldName@\" class=\"java.lang.String\"/>\n";
                 String[] columnsPercentSize = null;
                 int elementWidth = 0;
                 int colIndex = 0;
                 int xPosition = 0;
                 DbBasicProvider dbProider = new DbBasicProvider();
         
                 String templateFieldHeader = "\n" + "<staticText>\n" + "<reportElement\n" + "  mode=\"Opaque\"\n" + "  x=\"@elementPosition@\"\n" + "  y=\"@elementYPosition@\"\n" + "  width=\"@elementWidth@\"\n" + "  height=\"35\"\n" + "  backcolor=\"@J!jasperHeaderColor@\"\n" + "  key=\"staticText-3\"/>\n" + "<box leftPadding=\"2\" rightPadding=\"2\" topPadding=\"2\" bottomPadding=\"2\">\n" + "  <pen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <topPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <leftPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <bottomPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <rightPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "</box>\n" + "<textElement>\n" + "  <font pdfFontName=\"@J!jasperHeaderFont@\" size=\"@J!jasperHeaderSize@\" isBold=\"true\"/>\n" + "</textElement>\n" + "<text><![CDATA[@fieldName@]]></text>\n" + "</staticText>\n"; //02122014 add title from view (@elementYPosition@  is 20 if title else 0)
                 String templateFieldDetails = "<textField isStretchWithOverflow=\"true\" isBlankWhenNull=\"true\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >\n" + "    <reportElement\n" + "        x=\"@elementPosition@\"\n" + "        y=\"0\"\n" + "        width=\"@elementWidth@\"\n" + "        height=\"20\"\n" + "       key=\"textField-8\"\n" + "     isPrintWhenDetailOverflows=\"true\"\n" +  "      stretchType=\"RelativeToTallestObject\"/>\n"  + "    <box leftPadding=\"2\" rightPadding=\"2\" topPadding=\"2\" bottomPadding=\"2\">\n" + "      <pen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <topPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <leftPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <bottomPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <rightPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "    </box>\n" + "    <textElement  >\n" + "      <font fontName=\"@J!jasperFont@\" pdfFontName=\"@J!jasperFontPdf@\" isPdfEmbedded =\"@J!jasperIsPdfEmbedded@\" size=\"@J!jasperDetailsSize@\"/>\n" + "    </textElement>\n" + "    <textFieldExpression   class=\"java.lang.String\"><![CDATA[$F{@fieldName@}]]></textFieldExpression>\n" + "  </textField>";
                 String templateSubReportTitle = "";
                 if(subReportTitle.equals("")) 
                 {
                     templateSubReportTitle = "<textField isStretchWithOverflow=\"false\" isBlankWhenNull=\"false\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >\n" + 
                     "      <reportElement\n" + 
                     "              x=\"0\"\n" + 
                     "              y=\"0\"\n" + 
                     "              width=\"500\"\n" + 
                     "              height=\"20\"\n" + 
                     "              forecolor=\"@J!jasperSubReportHeaderColor@\"\n" +
                     "              key=\"textField-9\"/>\n" + 
                     "      <box></box>\n" + 
                     "      <textElement>\n" + 
                     "              <font pdfFontName=\"@J!jasperSubReportHeaderFont@\" size=\"@J!jasperSubReportHeaderSize@\" isBold=\"true\"/>\n" + 
                     "      </textElement>\n" + 
                     "      <textFieldExpression   class=\"java.lang.String\"><![CDATA[$F{JR_SUBREPORT_TITLE}]]></textFieldExpression>\n" + 
                     "</textField>";  //02122014 add title from view
                 }
                 else 
                 {
                     templateSubReportTitle = "<staticText >\n" + 
                     "      <reportElement\n" + 
                     "              x=\"0\"\n" + 
                     "              y=\"0\"\n" + 
                     "              width=\"500\"\n" + 
                     "              height=\"20\"\n" + 
                     "              forecolor=\"@J!jasperSubReportHeaderColor@\"\n" +
                     "              key=\"textField-9\"/>\n" + 
                     "      <box></box>\n" + 
                     "      <textElement>\n" + 
                     "              <font pdfFontName=\"@J!jasperSubReportHeaderFont@\" size=\"@J!jasperSubReportHeaderSize@\" isBold=\"true\"/>\n" + 
                     "      </textElement>\n" + 
                     "      <text><![CDATA[" + subReportTitle + "]]></text>\n" + 
                     "</staticText>";  //19022015 add title from setter / Constructor
                 }
                  
                 //get xml template  
                 FileInputStream input = new FileInputStream(new File(xmlTemplatePath + "\\" + (isPortrait ? PORTRAIT_XML_SUBREPROT:LANDSCAPE_XML_SUBREPROT)));
                 BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                 StringBuilder sb = new StringBuilder();
                 String line = null;
                 while ((line = reader.readLine()) != null)
                 {
                         sb.append(line + "\n");
                 }
                 reportTemplate = sb.toString();
         
                 //get Column sizes (if defined) 
                 columnsPercentSize = dbProider.getColumnSizeByTableName(conn,tableName,reportFilter);
                 int sumPercent = 100;
                 if (columnsPercentSize != null) 
                 {
                     sumPercent =  getSumPercentgetSumPercent(columnsPercentSize);
                 }
             
                 //generate instance elements using the table info       
                 List<String> colList = dbProider.getTableColumne(conn,tableName);
                 Iterator colIterator = colList.iterator();
         
                 StringBuilder sbFields = new StringBuilder(dataInjectionFilterFieldName != null && !dataInjectionFilterFieldName.trim().equals("") ? "<field name=\"" + dataInjectionFilterFieldName + "\" class=\"java.lang.Object\"/>\n" : "");
                 StringBuilder sbHeader = new StringBuilder();
                 StringBuilder sbDetails = new StringBuilder();
         
                 if (columnsPercentSize == null)
                 { 
                         elementWidth = (isPortrait ? PORTRAIT_WIDTH:LANDSCAPE_WIDTH) / (colList.size()); 
                 }
                 else
                 {
                         // if columnsPercentSize defined the elementWidth is the report width
                         elementWidth = (isPortrait ? PORTRAIT_WIDTH:LANDSCAPE_WIDTH);
                 }
                 
                 boolean isTitle = colList.contains("jr_subreport_title") || colList.contains("JR_SUBREPORT_TITLE") || !subReportTitle.equals(""); //02122014 add title from view
                 if(isTitle)
                 {
                     sbFields.append(templateField.replace("@fieldName@", "JR_SUBREPORT_TITLE"));
                     sbHeader.append(templateSubReportTitle);
                     templateFieldHeader = templateFieldHeader.replace("@elementYPosition@","20");
                 }
                 else 
                 {
                     templateFieldHeader = templateFieldHeader.replace("@elementYPosition@","0");
                 }
         
                 while (colIterator.hasNext())
                 {
                         String colName = (String) colIterator.next();
                         
                         if(colName.toLowerCase().equals("jr_subreport_title")) //02122014 add title from view (pass jr_subreport_title field)
                         {
                             continue;
                         }
         
                         //fields
                         sbFields.append(templateField.replace("@fieldName@", colName));
                         
                         if (columnsPercentSize == null)
                         //All columns are in the same size
                         {
                                 //fields header 
                                 sbHeader.append(templateFieldHeader.replace("@elementPosition@", String.valueOf(colIndex * elementWidth)).replace("@elementWidth@", String.valueOf(elementWidth)).replace("@fieldName@", mLang == null ? colName : "@L!" + colName + "@"));
                                 //fields detail
                                 sbDetails.append(templateFieldDetails.replace("@elementPosition@", String.valueOf(colIndex * elementWidth)).replace("@elementWidth@", String.valueOf(elementWidth)).replace("@fieldName@", colName));
                         }
                         else
                         // Defined columns size
                         {
                                 if(Integer.parseInt(columnsPercentSize[colIndex].trim()) != 0) 
                                 {
                                     //fields header 
                                     sbHeader.append(templateFieldHeader.replace("@elementPosition@", String.valueOf(xPosition)).replace("@elementWidth@", String.valueOf((elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent)).replace("@fieldName@", mLang == null ? colName : "@L!" + colName + "@"));
                                     //fields detail
                                     sbDetails.append(templateFieldDetails.replace("@elementPosition@", String.valueOf(xPosition)).replace("@elementWidth@", String.valueOf((elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent)).replace("@fieldName@", colName));
                                     xPosition += (elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent;
                                 }
                         }
                         //update colIndex
                         colIndex++;
                 } 
                 
                  //data injection
                  if(isDataInjection) 
                  { 
                      if(dataInjectionFilterParamName != null && !dataInjectionFilterParamName.trim().equals("") && dataInjectionFilterFieldName != null && !dataInjectionFilterFieldName.trim().equals("")) // with filter
                      {
                          reportTemplate = reportTemplate.replace("@FILTER_PARAM_NAME@", dataInjectionFilterParamName);
                          reportTemplate = reportTemplate.replace("@filterExpression@","<filterExpression><![CDATA[new Boolean(String.valueOf($F{" +dataInjectionFilterFieldName + "}).equals(String.valueOf($P{" + dataInjectionFilterParamName + "})))]]></filterExpression>");
                      }
                      else // no filter
                      {
                          reportTemplate = reportTemplate.replace("@FILTER_FIELD_NAME@", "FILTER_DUMMY");
                          reportTemplate = reportTemplate.replace("@filterExpression@","");
                      }
                     
                      reportTemplate = reportTemplate.replace("@sql@", "/* " + templateSql + " */");
                      //...set data Injection values back to default
                      isDataInjection = false;
                      this.dataInjectionFilterFieldName = "";
                      this.dataInjectionFilterParamName = "";
                  }
                  else 
                  {
                      reportTemplate = reportTemplate.replace("@FILTER_FIELD_NAME@", "FILTER_DUMMY");
                      reportTemplate = reportTemplate.replace("@filterExpression@","");
                      reportTemplate = reportTemplate.replace("@sql@", templateSql);
                  }
                     
                 reportTemplate = reportTemplate.replace("@fields@", sbFields.toString());
                 reportTemplate = reportTemplate.replace("@headers@", sbHeader.toString());
                 reportTemplate = reportTemplate.replace("@details@", sbDetails.toString());
                 reportTemplate = reportTemplate.replace("@headerBandHeight@", isTitle ? "55" : "35"); //02122014 add title from view
          
                  JasperReportGenerator jrg  = new JasperReportGenerator(conn,mLang); 
                   
                 //config parameters and replacers
                 HashMap<String, String> hmReplacers = new HashMap<String, String>(); 
                 hmReplacers.put("@replacerAll@", reportTemplate); 
          
                 toReturn = jrg.getCompiled(reportId, "rATPlaceHolder", xmlPath, xmlTmpPath,hmReplacers);
         
                 return toReturn;
         } 
         
        public String getJasperReportCompilePathDataInjection(String reportId, String tableName, String dataInjectionFilterFieldName, String dataInjectionFilterParamName, boolean isPortrait) throws Exception 
        {
                //set data injection class values
                isDataInjection = true;
                this.dataInjectionFilterFieldName = dataInjectionFilterFieldName != null ? dataInjectionFilterFieldName.toUpperCase() : null;
                this.dataInjectionFilterParamName = dataInjectionFilterParamName;
                //call...
                return getJasperReportCompilePath( reportId, tableName, " and 1=2 ", isPortrait);
        }
        
        public JasperReport getJasperReportCompiledDataInjection(String reportId, JSONObject jsonObject, String dataInjectionFilterFieldName, String dataInjectionFilterParamName, boolean isPortrait, int startWithColumn) throws Exception 
        {
                //set data injection class values
                isDataInjection = true;
                this.dataInjectionFilterFieldName = dataInjectionFilterFieldName != null ? dataInjectionFilterFieldName.toUpperCase() : null;
                this.dataInjectionFilterParamName = dataInjectionFilterParamName;
                //call...
                return getJasperReportCompiled( reportId, jsonObject, " and 1=2 ", isPortrait, startWithColumn);
        }
        
        private JasperReport getJasperReportCompiled(String reportId, JSONObject jsonObject, String reportFilter, boolean isPortrait, int startWithColumn) throws Exception {
				// TODO konsta like in getJasperReportCompiled( reportId, tableName, " and 1=2 ", isPortrait);
        	JasperReport toReturn = null;
            String reportTemplate = "";
//            String templateSql = " select  t.* from " + tableName + " t where 1=1 " + reportFilter;
            String templateSql = " select 1 from dual where 1=1 " + reportFilter;
            
            String templateField = "<field name=\"@fieldName@\" class=\"java.lang.String\"/>\n";
            String[] columnsPercentSize = null;
            int elementWidth = 0;
            int colIndex = 0;
            int xPosition = 0;
//            DbBasicProvider dbProider = new DbBasicProvider();
    
            String templateFieldHeader = "\n" + "<staticText>\n" + "<reportElement\n" + "  mode=\"Opaque\"\n" + "  x=\"@elementPosition@\"\n" + "  y=\"@elementYPosition@\"\n" + "  width=\"@elementWidth@\"\n" + "  height=\"35\"\n" + " forecolor=\"@J!jasperHeaderForeColor@\"\n" + "  backcolor=\"@J!jasperHeaderColor@\"\n" + "  key=\"staticText-3\"/>\n" + "<box leftPadding=\"2\" rightPadding=\"2\" topPadding=\"2\" bottomPadding=\"2\">\n" + "  <pen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <topPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <leftPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <bottomPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <rightPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "</box>\n" + "<textElement>\n" + "  <font pdfFontName=\"@J!jasperHeaderFont@\" size=\"@J!jasperHeaderSize@\" isBold=\"true\"/>\n" + "</textElement>\n" + "<text><![CDATA[@fieldName@]]></text>\n" + "</staticText>\n"; //02122014 add title from view (@elementYPosition@  is 20 if title else 0)
            String templateFieldDetails = "<textField isStretchWithOverflow=\"true\" isBlankWhenNull=\"true\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >\n" + "    <reportElement\n" + "        x=\"@elementPosition@\"\n" + "        y=\"0\"\n" + "        width=\"@elementWidth@\"\n" + "        height=\"20\"\n" + "       key=\"textField-8\"\n" + "     isPrintWhenDetailOverflows=\"true\"\n" +  "      stretchType=\"RelativeToTallestObject\"/>\n"  + "    <box leftPadding=\"2\" rightPadding=\"2\" topPadding=\"2\" bottomPadding=\"2\">\n" + "      <pen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <topPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <leftPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <bottomPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <rightPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "    </box>\n" + "    <textElement  >\n" + "      <font fontName=\"@J!jasperFont@\" pdfFontName=\"@J!jasperFontPdf@\" isPdfEmbedded =\"@J!jasperIsPdfEmbedded@\" size=\"@J!jasperDetailsSize@\"/>\n" + "    </textElement>\n" + "    <textFieldExpression   class=\"java.lang.String\"><![CDATA[$F{@fieldName@}]]></textFieldExpression>\n" + "  </textField>";
            String templateSubReportTitle = "";
            if(subReportTitle.equals("")) 
            {
                templateSubReportTitle = "<textField isStretchWithOverflow=\"false\" isBlankWhenNull=\"false\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >\n" + 
                "      <reportElement\n" + 
                "              x=\"0\"\n" + 
                "              y=\"0\"\n" + 
                "              width=\"500\"\n" + 
                "              height=\"20\"\n" + 
                "              forecolor=\"@J!jasperSubReportHeaderColor@\"\n" +
                "              key=\"textField-9\"/>\n" + 
                "      <box></box>\n" + 
                "      <textElement>\n" + 
                "              <font pdfFontName=\"@J!jasperSubReportHeaderFont@\" size=\"@J!jasperSubReportHeaderSize@\" isBold=\"true\"/>\n" + 
                "      </textElement>\n" + 
                "      <textFieldExpression   class=\"java.lang.String\"><![CDATA[$F{JR_SUBREPORT_TITLE}]]></textFieldExpression>\n" + 
                "</textField>";  //02122014 add title from view
            }
            else 
            {
                templateSubReportTitle = "<staticText >\n" + 
                "      <reportElement\n" + 
                "              x=\"0\"\n" + 
                "              y=\"0\"\n" + 
                "              width=\"500\"\n" + 
                "              height=\"20\"\n" + 
                "              forecolor=\"@J!jasperSubReportHeaderColor@\"\n" +
                "              key=\"textField-9\"/>\n" + 
                "      <box></box>\n" + 
                "      <textElement>\n" + 
                "              <font pdfFontName=\"@J!jasperSubReportHeaderFont@\" size=\"@J!jasperSubReportHeaderSize@\" isBold=\"true\"/>\n" + 
                "      </textElement>\n" + 
                "      <text><![CDATA[" + subReportTitle + "]]></text>\n" + 
                "</staticText>";  //19022015 add title from setter / Constructor
            }
             
            //get xml template  
            FileInputStream input = new FileInputStream(new File(xmlTemplatePath + "\\" + (isPortrait ? PORTRAIT_XML_SUBREPROT_XLS:LANDSCAPE_XML_SUBREPROT_XLS)));
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                    sb.append(line + "\n");
            }
            reader.close();
            reportTemplate = sb.toString();
    
            int sumPercent = 100;
            
            JSONArray jsonArr = (JSONArray)jsonObject.get("columns");
            Map<String,String> colMap = new HashMap<String,String>();

            
            List<String> colList = new ArrayList<String>();
            String s;
            JSONObject mp = null;

            String header = null, title = null, uniqueTitle = null;
            int indexSmart = 0;
            for (int i=0; i < jsonArr.length(); i++)
            {
            	//colHeader  
            	if (i >= startWithColumn)
            	{
	            	mp = jsonArr.getJSONObject(i);
	//            	colHeader[i] = mp.getString("title");
//	            	title = mp.getString("uniqueTitle");
//	            	colMap.put(title, mp.getString("title"));
	            	uniqueTitle = mp.getString("uniqueTitle");
	            	title = mp.getString("title");	            	
	            	if (!title.contains("_SMART")) 
	            	{
	            		colMap.put(uniqueTitle, title);
	            	} else 
	            	{
        				//header = colHeader[j].replace("_SMARTLINK", "");
        				indexSmart = title.indexOf("_SMART");
        				if (indexSmart != 0) {
        					colMap.put(uniqueTitle,title.substring(0, indexSmart));
        				} else {
        					colMap.put(uniqueTitle, "NA"); // in case if column header equals "_SMART..." (nothing before _smart)
        				}
	            	}
	            	colList.add(uniqueTitle);
            	}
            }


            Iterator colIterator = colList.iterator();
    
            StringBuilder sbFields = new StringBuilder(dataInjectionFilterFieldName != null && !dataInjectionFilterFieldName.trim().equals("") ? "<field name=\"" + dataInjectionFilterFieldName + "\" class=\"java.lang.String\"/>\n" : "");
            StringBuilder sbHeader = new StringBuilder();
            StringBuilder sbDetails = new StringBuilder();
    
            elementWidth = 100;
            
            boolean isTitle = colList.contains("jr_subreport_title") || colList.contains("JR_SUBREPORT_TITLE") || !subReportTitle.equals(""); //02122014 add title from view
            if(isTitle)
            {
            	int i =1;
                sbFields.append(templateField.replace("@fieldName@", "JR_SUBREPORT_TITLE"));
                sbHeader.append(templateSubReportTitle);
                templateFieldHeader = templateFieldHeader.replace("@elementYPosition@","20");
            }
            else 
            {
                templateFieldHeader = templateFieldHeader.replace("@elementYPosition@","0");
            }
    
            while (colIterator.hasNext())
            {
                    String colName = (String) colIterator.next();
                    
                    if(colName.toLowerCase().equals("jr_subreport_title")) 
                    {
                        continue;
                    }
    
                    //fields
                    if(!colName.toLowerCase().equals(dataInjectionFilterFieldName != null && !dataInjectionFilterFieldName.trim().equals("") ? dataInjectionFilterFieldName.toLowerCase() : ""))
                    {
                    	sbFields.append(templateField.replace("@fieldName@", colName));
                    }
                    
                    if (columnsPercentSize == null)
                    //All columns are in the same size
                    {
                            //fields header 
                            sbHeader.append(templateFieldHeader.replace("@elementPosition@", String.valueOf(colIndex * elementWidth)).replace("@elementWidth@", String.valueOf(elementWidth)).replace("@fieldName@", mLang == null ? colMap.get(colName) : "@L!" + colMap.get(colName) + "@"));
                            //fields detail
                            sbDetails.append(templateFieldDetails.replace("@elementPosition@", String.valueOf(colIndex * elementWidth)).replace("@elementWidth@", String.valueOf(elementWidth)).replace("@fieldName@", colName));
                    }
                    else
                    // Defined columns size
                    {
                            if(Integer.parseInt(columnsPercentSize[colIndex].trim()) != 0) 
                            {
                                //fields header 
                                sbHeader.append(templateFieldHeader.replace("@elementPosition@", String.valueOf(xPosition)).replace("@elementWidth@", String.valueOf((elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent)).replace("@fieldName@", mLang == null ? colMap.get(colName) : "@L!" + colMap.get(colName) + "@"));
                                //fields detail
                                sbDetails.append(templateFieldDetails.replace("@elementPosition@", String.valueOf(xPosition)).replace("@elementWidth@", String.valueOf((elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent)).replace("@fieldName@", colName));
                                xPosition += (elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent;
                            }
                    }
                    //update colIndex
                    colIndex++;
            } 
            
             //data injection
             if(isDataInjection) 
             { 
                 if(dataInjectionFilterParamName != null && !dataInjectionFilterParamName.trim().equals("") && dataInjectionFilterFieldName != null && !dataInjectionFilterFieldName.trim().equals("")) // with filter
                 {
                     reportTemplate = reportTemplate.replace("@FILTER_PARAM_NAME@", dataInjectionFilterParamName);
                     reportTemplate = reportTemplate.replace("@filterExpression@","<filterExpression><![CDATA[new Boolean(String.valueOf($F{" +dataInjectionFilterFieldName + "}).equals(String.valueOf($P{" + dataInjectionFilterParamName + "})))]]></filterExpression>");
                 }
                 else // no filter
                 {
                     reportTemplate = reportTemplate.replace("@FILTER_FIELD_NAME@", "FILTER_DUMMY");
                     reportTemplate = reportTemplate.replace("@filterExpression@","");
                 }
                
                 reportTemplate = reportTemplate.replace("@sql@", "/* " + templateSql + " */");
                 //...set data Injection values back to default
                 isDataInjection = false;
                 this.dataInjectionFilterFieldName = "";
                 this.dataInjectionFilterParamName = "";
             }
             else 
             {
                 reportTemplate = reportTemplate.replace("@FILTER_FIELD_NAME@", "FILTER_DUMMY");
                 reportTemplate = reportTemplate.replace("@filterExpression@","");
                 reportTemplate = reportTemplate.replace("@sql@", templateSql);
             }
                
            reportTemplate = reportTemplate.replace("@fields@", sbFields.toString());
            reportTemplate = reportTemplate.replace("@headers@", sbHeader.toString());
            reportTemplate = reportTemplate.replace("@details@", sbDetails.toString());
            reportTemplate = reportTemplate.replace("@headerBandHeight@", isTitle ? "55" : "35"); //02122014 add title from view
     
             JasperReportGenerator jrg  = new JasperReportGenerator(conn/*,mLang*/); 
              
            //config parameters and replacers
            HashMap<String, String> hmReplacers = new HashMap<String, String>(); 
            hmReplacers.put("@replacerAll@", reportTemplate); 
     
            toReturn = jrg.getCompiled(reportId, "rATPlaceHolder", xmlPath, xmlTmpPath,hmReplacers);
    
            return toReturn;
		}

		//        public JasperReport getJasperReportCompiledDataInjection(String reportId, getJasperReportCompiledDataInjection tableName, String dataInjectionFilterFieldName, String dataInjectionFilterParamName, boolean isPortrait) throws Exception
        public JasperReport getJasperReportCompiledDataInjection(String reportId, String tableName, String dataInjectionFilterFieldName, String dataInjectionFilterParamName, boolean isPortrait) throws Exception 
        {
                //set data injection class values
                isDataInjection = true;
                this.dataInjectionFilterFieldName = dataInjectionFilterFieldName != null ? dataInjectionFilterFieldName.toUpperCase() : null;
                this.dataInjectionFilterParamName = dataInjectionFilterParamName;
                //call...
                return getJasperReportCompiled( reportId, tableName, " and 1=2 ", isPortrait);
        }
         
        public String getAtGroupedJasperReport(String reportId, JasperReportType reportType, String reportTitle, String reportFilterDisplay, String tableName, String reportFilter) throws Exception 
        {
            return getAtGroupedJasperReport( reportId,  reportType,  reportTitle,  reportFilterDisplay,  tableName,  reportFilter, true);
        } 
	
	public String getAtGroupedJasperReport(String reportId, JasperReportType reportType, String reportTitle, String reportFilterDisplay, String tableName, String reportFilter, boolean isPortrait) throws Exception
	{
		String toReturn = null;
		String reportTemplate = "";
		String templateSql = " select  t.*, max(t.change_id) over (partition by id order by change_id desc) as MAX_CHANGE_ID from " + tableName + " t where 1=1 " + reportFilter; //...order by change_id desc because the max function somtimes change the order
		String templateField = "<field name=\"@fieldName@\" class=\"java.lang.String\"/>\n";
		String[] columnsPercentSize = null;
		int elementWidth = 0;
		int colIndex = 0;
		int xPosition = 0;
		DbBasicProvider dbProider = new DbBasicProvider();

		String templateFieldHeader = "\n" + "<staticText>\n" + "<reportElement\n" + "  mode=\"Opaque\"\n" + "  x=\"@elementPosition@\"\n" + "  y=\"0\"\n" + "  width=\"@elementWidth@\"\n" + "  height=\"35\"\n" + "  backcolor=\"@J!jasperHeaderColor@\"\n" + "  key=\"staticText-3\"/>\n" + "<box leftPadding=\"2\" rightPadding=\"2\" topPadding=\"2\" bottomPadding=\"2\">\n" + "  <pen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <topPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <leftPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <bottomPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <rightPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "</box>\n" + "<textElement>\n" + "  <font pdfFontName=\"@J!jasperHeaderFont@\" size=\"@J!jasperHeaderSize@\" isBold=\"true\"/>\n" + "</textElement>\n" + "<text><![CDATA[@fieldName@]]></text>\n" + "</staticText>\n";
		String templateFieldDetails = "<textField isStretchWithOverflow=\"true\" isBlankWhenNull=\"true\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >\n" + "    <reportElement\n" + "        style=\"MarkFirstLine\"\n" + "        x=\"@elementPosition@\"\n" + "        y=\"0\"\n" + "        width=\"@elementWidth@\"\n" + "        height=\"20\"\n" + "       key=\"textField-8\"\n" + "     isPrintWhenDetailOverflows=\"true\"\n" +  "      stretchType=\"RelativeToBandHeight\"/>\n" + "    <box leftPadding=\"2\" rightPadding=\"2\" topPadding=\"2\" bottomPadding=\"2\">\n" + "      <pen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <topPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <leftPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <bottomPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <rightPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "    </box>\n" + "    <textElement  >\n" + "      <font fontName=\"@J!jasperFont@\" pdfFontName=\"@J!jasperFontPdf@\" isPdfEmbedded =\"@J!jasperIsPdfEmbedded@\" size=\"@J!jasperDetailsSize@\"/>\n" + "    </textElement>\n" + "    <textFieldExpression   class=\"java.lang.String\"><![CDATA[$F{@fieldName@}]]></textFieldExpression>\n" + "  </textField>";

		//get xml template  
		//FileInputStream input = new FileInputStream(new File(xmlTemplatePath + "\\tATGroupedpPortrait.xml"));
	        FileInputStream input = new FileInputStream(new File(xmlTemplatePath + "\\" + (isPortrait ? AT_GROUP_PORTRAIT_XML:AT_GROUP_LANDSCAPE_XML)));
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			sb.append(line + "\n");
		}
		reportTemplate = sb.toString();

		//get Column sizes (if defined) 
		columnsPercentSize = dbProider.getColumnSizeByTableName(conn,tableName,reportFilter);
                int sumPercent = 100;
                if (columnsPercentSize != null) 
                {
                    sumPercent =  getSumPercentgetSumPercent(columnsPercentSize);
                }

		//generate instance elements using the table info	
		List<String> colList = dbProider.getTableColumne(conn,tableName);
		Iterator colIterator = colList.iterator();

		StringBuilder sbFields = new StringBuilder();
		StringBuilder sbHeader = new StringBuilder();
		StringBuilder sbDetails = new StringBuilder();

		if (columnsPercentSize == null)
		{
			elementWidth = (isPortrait ? PORTRAIT_WIDTH:LANDSCAPE_WIDTH) / (colList.size()); 
		}
		else
		{
			// if columnsPercentSize defined the elementWidth is the report width
			elementWidth =(isPortrait ? PORTRAIT_WIDTH:LANDSCAPE_WIDTH); 
		}

		while (colIterator.hasNext())
		{
			String colName = (String) colIterator.next();

			//fields
			sbFields.append(templateField.replace("@fieldName@", colName));
			
			String nameDisplay = "";
			
			if (colName.length() > 1)
			{
			    nameDisplay = colName.substring(0,1).toUpperCase() + colName.substring(1).toLowerCase();
			}
			else
			{
			    nameDisplay = colName;
			}
			 
			if (columnsPercentSize == null)
			//All columns are in the same size
			{ 
				//fields header 
				sbHeader.append(templateFieldHeader.replace("@elementPosition@", String.valueOf(colIndex * elementWidth)).replace("@elementWidth@", String.valueOf(elementWidth)).replace("@fieldName@", mLang == null ? nameDisplay : "@L!" + nameDisplay + "@"));
				//fields detail
				sbDetails.append(templateFieldDetails.replace("@elementPosition@", String.valueOf(colIndex * elementWidth)).replace("@elementWidth@", String.valueOf(elementWidth)).replace("@fieldName@", colName));
			}
			else
			// Defined columns size
			{
			    if(Integer.parseInt(columnsPercentSize[colIndex].trim()) != 0) 
                            {
				//fields header 
				sbHeader.append(templateFieldHeader.replace("@elementPosition@", String.valueOf(xPosition)).replace("@elementWidth@", String.valueOf((elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent)).replace("@fieldName@", mLang == null ? nameDisplay : "@L!" + nameDisplay + "@"));
				//fields detail
				sbDetails.append(templateFieldDetails.replace("@elementPosition@", String.valueOf(xPosition)).replace("@elementWidth@", String.valueOf((elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent)).replace("@fieldName@", colName));
				xPosition += (elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent;
                            }
			}
			//update colIndex
			colIndex++; 
		}
	        sbFields.append("<field name=\"CHANGE_ID\" class=\"java.lang.String\"/>\n");
		sbFields.append("<field name=\"MAX_CHANGE_ID\" class=\"java.lang.String\"/>\n");
		
	        reportTemplate = reportTemplate.replace("@sql@", templateSql);
		reportTemplate = reportTemplate.replace("@fields@", sbFields.toString());
		reportTemplate = reportTemplate.replace("@headers@", sbHeader.toString());
		reportTemplate = reportTemplate.replace("@details@", sbDetails.toString());
	        reportTemplate = reportTemplate.replace("@title@", reportTitle);
	        reportTemplate = reportTemplate.replace("@filterDisplay@", reportFilterDisplay);
 
		//String reportPath, String reportPathTmp) 
		JasperReportGenerator jrg = new JasperReportGenerator(conn,mLang);

		//config parameters and replacers
		HashMap<String, String> hmReplacers = new HashMap<String, String>();
		HashMap<String, Object> hmParameters = new HashMap<String, Object>();
		hmReplacers.put("@replacerAll@", reportTemplate);
		//hmReplacers.put("@replacer2@", reportFilter);

		//call the jasperCore  get result 
		toReturn = jrg.getPath(reportId, "rATPlaceHolder", reportType, "", "", hmReplacers, hmParameters, xmlPath, xmlTmpPath);

		return toReturn;
	}

        public ByteArrayOutputStream getAtGroupedJasperReportStream(String reportId, JasperReportType reportType, String reportTitle, String reportFilterDisplay, String tableName, String reportFilter) throws Exception 
        {
            return getAtGroupedJasperReportStream(reportId, reportType, reportTitle, reportFilterDisplay, tableName, reportFilter, true);
        }
        
	public ByteArrayOutputStream getAtGroupedJasperReportStream(String reportId, JasperReportType reportType, String reportTitle, String reportFilterDisplay, String tableName, String reportFilter, boolean isPortrait) throws Exception
	{
		ByteArrayOutputStream toReturn = null;
		String reportTemplate = "";
		String templateSql = " select  t.*, max(t.change_id) over (partition by id order by change_id desc) as MAX_CHANGE_ID from " + tableName + " t where 1=1 " + reportFilter; //...order by change_id desc because the max function somtimes change the order
		String templateField = "<field name=\"@fieldName@\" class=\"java.lang.String\"/>\n";
		String[] columnsPercentSize = null;
		int elementWidth = 0;
		int colIndex = 0;
		int xPosition = 0;
		DbBasicProvider dbProider = new DbBasicProvider();

		String templateFieldHeader = "\n" + "<staticText>\n" + "<reportElement\n" + "  mode=\"Opaque\"\n" + "  x=\"@elementPosition@\"\n" + "  y=\"0\"\n" + "  width=\"@elementWidth@\"\n" + "  height=\"35\"\n" + "  backcolor=\"@J!jasperHeaderColor@\"\n" + "  key=\"staticText-3\"/>\n" + "<box leftPadding=\"2\" rightPadding=\"2\" topPadding=\"2\" bottomPadding=\"2\">\n" + "  <pen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <topPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <leftPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <bottomPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <rightPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "</box>\n" + "<textElement>\n" + "  <font pdfFontName=\"@J!jasperHeaderFont@\" size=\"@J!jasperHeaderSize@\" isBold=\"true\"/>\n" + "</textElement>\n" + "<text><![CDATA[@fieldName@]]></text>\n" + "</staticText>\n";
		String templateFieldDetails = "<textField isStretchWithOverflow=\"true\" isBlankWhenNull=\"true\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >\n" + "    <reportElement\n" + "        style=\"MarkFirstLine\"\n" + "        x=\"@elementPosition@\"\n" + "        y=\"0\"\n" + "        width=\"@elementWidth@\"\n" + "        height=\"20\"\n" + "       key=\"textField-8\"\n"  + "     isPrintWhenDetailOverflows=\"true\"\n" + "      stretchType=\"RelativeToBandHeight\"/>\n" + "    <box leftPadding=\"2\" rightPadding=\"2\" topPadding=\"2\" bottomPadding=\"2\">\n" + "      <pen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <topPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <leftPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <bottomPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <rightPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "    </box>\n" + "    <textElement  >\n" + "      <font fontName=\"@J!jasperFont@\" pdfFontName=\"@J!jasperFontPdf@\" isPdfEmbedded =\"@J!jasperIsPdfEmbedded@\" size=\"@J!jasperDetailsSize@\"/>\n" + "    </textElement>\n" + "    <textFieldExpression   class=\"java.lang.String\"><![CDATA[$F{@fieldName@}]]></textFieldExpression>\n" + "  </textField>";

		//get xml template  
                FileInputStream input = new FileInputStream(new File(xmlTemplatePath + "\\" + (isPortrait ? AT_GROUP_PORTRAIT_XML:AT_GROUP_LANDSCAPE_XML)));
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			sb.append(line + "\n");
		}
		reportTemplate = sb.toString();

		//get Column sizes (if defined) 
		columnsPercentSize = dbProider.getColumnSizeByTableName(conn,tableName,reportFilter);
                int sumPercent = 100;
                if (columnsPercentSize != null) 
                {
                    sumPercent =  getSumPercentgetSumPercent(columnsPercentSize);
                }

		//generate instance elements using the table info       
		List<String> colList = dbProider.getTableColumne(conn,tableName);
		Iterator colIterator = colList.iterator();

		StringBuilder sbFields = new StringBuilder();
		StringBuilder sbHeader = new StringBuilder();
		StringBuilder sbDetails = new StringBuilder();

		if (columnsPercentSize == null)
		{ 
		         elementWidth = (isPortrait ? PORTRAIT_WIDTH:LANDSCAPE_WIDTH) / (colList.size());
		}
		else
		{
			// if columnsPercentSize defined the elementWidth is the report width
		        elementWidth = (isPortrait ? PORTRAIT_WIDTH:LANDSCAPE_WIDTH); 
		}

		while (colIterator.hasNext())
		{
			String colName = (String) colIterator.next();

			//fields
			sbFields.append(templateField.replace("@fieldName@", colName));
			
			String nameDisplay = "";
			
			if (colName.length() > 1)
			{
			    nameDisplay = colName.substring(0,1).toUpperCase() + colName.substring(1).toLowerCase();
			}
			else
			{
			    nameDisplay = colName;
			}
			 
			if (columnsPercentSize == null)
			//All columns are in the same size
			{
				//fields header 
				sbHeader.append(templateFieldHeader.replace("@elementPosition@", String.valueOf(colIndex * elementWidth)).replace("@elementWidth@", String.valueOf(elementWidth)).replace("@fieldName@", mLang == null ? nameDisplay : "@L!" + nameDisplay + "@"));
				//fields detail
				sbDetails.append(templateFieldDetails.replace("@elementPosition@", String.valueOf(colIndex * elementWidth)).replace("@elementWidth@", String.valueOf(elementWidth)).replace("@fieldName@", colName));
			}
			else
			// Defined columns size
			{
			    if(Integer.parseInt(columnsPercentSize[colIndex].trim()) != 0) 
                            {
				//fields header 
				sbHeader.append(templateFieldHeader.replace("@elementPosition@", String.valueOf(xPosition)).replace("@elementWidth@", String.valueOf((elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent)).replace("@fieldName@", mLang == null ? nameDisplay : "@L!" + nameDisplay + "@"));
				//fields detail
				sbDetails.append(templateFieldDetails.replace("@elementPosition@", String.valueOf(xPosition)).replace("@elementWidth@", String.valueOf((elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent)).replace("@fieldName@", colName));
				xPosition += (elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent;
                            }
			}
			//update colIndex
			colIndex++; 
		}
	        sbFields.append("<field name=\"CHANGE_ID\" class=\"java.lang.String\"/>\n");
		sbFields.append("<field name=\"MAX_CHANGE_ID\" class=\"java.lang.String\"/>\n");
		
                reportTemplate = reportTemplate.replace("@sql@", templateSql);
		reportTemplate = reportTemplate.replace("@fields@", sbFields.toString());
		reportTemplate = reportTemplate.replace("@headers@", sbHeader.toString());
		reportTemplate = reportTemplate.replace("@details@", sbDetails.toString());
	        reportTemplate = reportTemplate.replace("@title@", reportTitle);
	        reportTemplate = reportTemplate.replace("@filterDisplay@", reportFilterDisplay);
 
		//String reportPath, String reportPathTmp) 
		JasperReportGenerator jrg = new JasperReportGenerator(conn, mLang);

		//config parameters and replacers
		HashMap<String, String> hmReplacers = new HashMap<String, String>();
		HashMap<String, Object> hmParameters = new HashMap<String, Object>();
		hmReplacers.put("@replacerAll@", reportTemplate);
		//hmReplacers.put("@replacer2@", reportFilter);

		//call the jasperCore  get result 
		toReturn = jrg.getByteArrayOutputStream(reportId, "rATPlaceHolder", reportType, "", "", hmReplacers, hmParameters, xmlPath, xmlTmpPath);

		return toReturn;
	}

        public String geAtGroupedJasperReportCompilePath(String reportId, String tableName, String reportFilter) throws Exception
        {
            return  geAtGroupedJasperReportCompilePath( reportId,  tableName,  reportFilter, true);
        }
        
        public String geAtGroupedJasperReportCompilePathDataInjection(String reportId, String tableName, String dataInjectionFilterFieldName, String dataInjectionFilterParamName, boolean isPortrait) throws Exception 
        {
                //set data injection class values
                isDataInjection = true;
                this.dataInjectionFilterFieldName = dataInjectionFilterFieldName != null ? dataInjectionFilterFieldName.toUpperCase() : null;
                this.dataInjectionFilterParamName = dataInjectionFilterParamName;
                //call...
                return geAtGroupedJasperReportCompilePath( reportId, tableName, " and 1=2 ", isPortrait);
        }
        
	public String geAtGroupedJasperReportCompilePath(String reportId, String tableName, String reportFilter, boolean isPortrait) throws Exception
	{
		String toReturn = null;
		String reportTemplate = "";
		String templateSql = " select  t.*, max(t.change_id) over (partition by id order by change_id desc) as MAX_CHANGE_ID from " + tableName + " t where 1=1 " + reportFilter; //...order by change_id desc because the max function somtimes change the order
		String templateField = "<field name=\"@fieldName@\" class=\"java.lang.String\"/>\n";
		String[] columnsPercentSize = null;
		int elementWidth = 0;
		int colIndex = 0;
		int xPosition = 0;
		DbBasicProvider dbProider = new DbBasicProvider();
    
		String templateFieldHeader = "\n" + "<staticText>\n" + "<reportElement\n" + "  mode=\"Opaque\"\n" + "  x=\"@elementPosition@\"\n" + "  y=\"@elementYPosition@\"\n" + "  width=\"@elementWidth@\"\n" + "  height=\"35\"\n" + "  backcolor=\"@J!jasperHeaderColor@\"\n" + "  key=\"staticText-3\"/>\n" + "<box leftPadding=\"2\" rightPadding=\"2\" topPadding=\"2\" bottomPadding=\"2\">\n" + "  <pen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <topPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <leftPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <bottomPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <rightPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "</box>\n" + "<textElement>\n" + "  <font pdfFontName=\"@J!jasperHeaderFont@\" size=\"@J!jasperHeaderSize@\" isBold=\"true\"/>\n" + "</textElement>\n" + "<text><![CDATA[@fieldName@]]></text>\n" + "</staticText>\n"; //02122014 add title from view (@elementYPosition@  is 20 if title else 0)
		String templateFieldDetails = "<textField isStretchWithOverflow=\"true\" isBlankWhenNull=\"true\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >\n" + "    <reportElement\n" + "        style=\"MarkFirstLine\"\n" + "        x=\"@elementPosition@\"\n" + "        y=\"0\"\n" + "        width=\"@elementWidth@\"\n" + "        height=\"20\"\n" + "       key=\"textField-8\"\n"  + "     isPrintWhenDetailOverflows=\"true\"\n" + "      stretchType=\"RelativeToBandHeight\"/>\n" + "    <box leftPadding=\"2\" rightPadding=\"2\" topPadding=\"2\" bottomPadding=\"2\">\n" + "      <pen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <topPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <leftPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <bottomPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <rightPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "    </box>\n" + "    <textElement  >\n" + "      <font fontName=\"@J!jasperFont@\" pdfFontName=\"@J!jasperFontPdf@\" isPdfEmbedded =\"@J!jasperIsPdfEmbedded@\" size=\"@J!jasperDetailsSize@\"/>\n" + "    </textElement>\n" + "    <textFieldExpression   class=\"java.lang.String\"><![CDATA[$F{@fieldName@}]]></textFieldExpression>\n" + "  </textField>";
                String templateSubReportTitle = "";
                if(subReportTitle.equals("")) 
                {
                    templateSubReportTitle = "<textField isStretchWithOverflow=\"false\" isBlankWhenNull=\"false\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >\n" + 
                    "      <reportElement\n" + 
                    "              x=\"0\"\n" + 
                    "              y=\"0\"\n" + 
                    "              width=\"500\"\n" + 
                    "              height=\"20\"\n" + 
                    "              forecolor=\"@J!jasperSubReportHeaderColor@\"\n" +
                    "              key=\"textField-9\"/>\n" + 
                    "      <box></box>\n" + 
                    "      <textElement>\n" + 
                    "              <font pdfFontName=\"@J!jasperSubReportHeaderFont@\" size=\"@J!jasperSubReportHeaderSize@\" isBold=\"true\"/>\n" + 
                    "      </textElement>\n" + 
                    "      <textFieldExpression   class=\"java.lang.String\"><![CDATA[$F{JR_SUBREPORT_TITLE}]]></textFieldExpression>\n" + 
                    "</textField>";  //02122014 add title from view
                }
                else 
                {
                    templateSubReportTitle = "<staticText >\n" + 
                    "      <reportElement\n" + 
                    "              x=\"0\"\n" + 
                    "              y=\"0\"\n" + 
                    "              width=\"500\"\n" + 
                    "              height=\"20\"\n" + 
                    "              forecolor=\"@J!jasperSubReportHeaderColor@\"\n" +
                    "              key=\"textField-9\"/>\n" + 
                    "      <box></box>\n" + 
                    "      <textElement>\n" + 
                    "              <font pdfFontName=\"@J!jasperSubReportHeaderFont@\" size=\"@J!jasperSubReportHeaderSize@\" isBold=\"true\"/>\n" + 
                    "      </textElement>\n" + 
                    "      <text><![CDATA[" + subReportTitle + "]]></text>\n" + 
                    "</staticText>";  //19022015 add title from setter / Constructor
                }
                             
		//get xml template  
                FileInputStream input = new FileInputStream(new File(xmlTemplatePath + "\\" + (isPortrait ? AT_GROUP_PORTRAIT_XML_SUBREPROT:AT_GROUP_LANDSCAPE_XML_SUBREPROT)));
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			sb.append(line + "\n");
		}
		reportTemplate = sb.toString();
    
		//get Column sizes (if defined) 
		columnsPercentSize = dbProider.getColumnSizeByTableName(conn,tableName,reportFilter);
                int sumPercent = 100;
                if (columnsPercentSize != null) 
                {
                    sumPercent =  getSumPercentgetSumPercent(columnsPercentSize);
                }
    
		//generate instance elements using the table info       
		List<String> colList = dbProider.getTableColumne(conn,tableName);
		Iterator colIterator = colList.iterator();
    
                StringBuilder sbFields = new StringBuilder(dataInjectionFilterFieldName != null && !dataInjectionFilterFieldName.trim().equals("") ? "<field name=\"" + dataInjectionFilterFieldName + "\" class=\"java.lang.Object\"/>\n" : "");
		StringBuilder sbHeader = new StringBuilder();
		StringBuilder sbDetails = new StringBuilder();
    
		if (columnsPercentSize == null)
		{ 
		        elementWidth = (isPortrait ? PORTRAIT_WIDTH:LANDSCAPE_WIDTH) / (colList.size()); 
		}
		else
		{
			// if columnsPercentSize defined the elementWidth is the report width
		        elementWidth = (isPortrait ? PORTRAIT_WIDTH:LANDSCAPE_WIDTH);
		}
    
	        boolean isTitle = colList.contains("jr_subreport_title") || colList.contains("JR_SUBREPORT_TITLE") || !subReportTitle.equals(""); //02122014 add title from view
                 if(isTitle)
                 {
                     sbFields.append(templateField.replace("@fieldName@", "JR_SUBREPORT_TITLE"));
                     sbHeader.append(templateSubReportTitle);
                     templateFieldHeader = templateFieldHeader.replace("@elementYPosition@","20");
                 }
                 else 
                 {
                     templateFieldHeader = templateFieldHeader.replace("@elementYPosition@","0");
                 }
                 
		while (colIterator.hasNext())
		{
			String colName = (String) colIterator.next();
                        
                        if(colName.toLowerCase().equals("jr_subreport_title")) //02122014 add title from view (pass jr_subreport_title field)
                        {
                            continue;
                        }
    
			//fields
			sbFields.append(templateField.replace("@fieldName@", colName));
			
			String nameDisplay = "";
			
			if (colName.length() > 1)
			{
			    nameDisplay = colName.substring(0,1).toUpperCase() + colName.substring(1).toLowerCase();
			}
			else
			{
			    nameDisplay = colName;
			}
			 
			if (columnsPercentSize == null)
			//All columns are in the same size
			{
				//fields header 
				sbHeader.append(templateFieldHeader.replace("@elementPosition@", String.valueOf(colIndex * elementWidth)).replace("@elementWidth@", String.valueOf(elementWidth)).replace("@fieldName@", mLang == null ? nameDisplay : "@L!" + nameDisplay + "@"));
				//fields detail
				sbDetails.append(templateFieldDetails.replace("@elementPosition@", String.valueOf(colIndex * elementWidth)).replace("@elementWidth@", String.valueOf(elementWidth)).replace("@fieldName@", colName));
			}
			else
			// Defined columns size
			{
			    if(Integer.parseInt(columnsPercentSize[colIndex].trim()) != 0) 
                            {
				//fields header 
				sbHeader.append(templateFieldHeader.replace("@elementPosition@", String.valueOf(xPosition)).replace("@elementWidth@", String.valueOf((elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent)).replace("@fieldName@", mLang == null ? nameDisplay : "@L!" + nameDisplay + "@"));
				//fields detail
				sbDetails.append(templateFieldDetails.replace("@elementPosition@", String.valueOf(xPosition)).replace("@elementWidth@", String.valueOf((elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent)).replace("@fieldName@", colName));
				xPosition += (elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent;
                            }
			}
			//update colIndex
			colIndex++;
		 
		}
	        sbFields.append("<field name=\"CHANGE_ID\" class=\"java.lang.String\"/>\n");
		sbFields.append("<field name=\"MAX_CHANGE_ID\" class=\"java.lang.String\"/>\n");
		
                //data injection 
                if(isDataInjection) 
                { 
                    if(dataInjectionFilterParamName != null && !dataInjectionFilterParamName.trim().equals("") && dataInjectionFilterFieldName != null && !dataInjectionFilterFieldName.trim().equals("")) // with filter
                    {
                        reportTemplate = reportTemplate.replace("@FILTER_PARAM_NAME@", dataInjectionFilterParamName);
                        reportTemplate = reportTemplate.replace("@filterExpression@","<filterExpression><![CDATA[new Boolean(String.valueOf($F{" +dataInjectionFilterFieldName + "}).equals(String.valueOf($P{" + dataInjectionFilterParamName + "})))]]></filterExpression>");
                    }
                    else // no filter
                    {
                        reportTemplate = reportTemplate.replace("@FILTER_FIELD_NAME@", "FILTER_DUMMY");
                        reportTemplate = reportTemplate.replace("@filterExpression@","");
                    }
                   
                    reportTemplate = reportTemplate.replace("@sql@", "/* " + templateSql + " */");
                    //...set data Injection values back to default
                    isDataInjection = false;
                    this.dataInjectionFilterFieldName = "";
                    this.dataInjectionFilterParamName = "";
                }
                else 
                {
                    reportTemplate = reportTemplate.replace("@FILTER_FIELD_NAME@", "FILTER_DUMMY");
                    reportTemplate = reportTemplate.replace("@filterExpression@","");
                    reportTemplate = reportTemplate.replace("@sql@", templateSql);
                }
                    
		reportTemplate = reportTemplate.replace("@fields@", sbFields.toString());
		reportTemplate = reportTemplate.replace("@headers@", sbHeader.toString());
		reportTemplate = reportTemplate.replace("@details@", sbDetails.toString());
	        reportTemplate = reportTemplate.replace("@headerBandHeight@", isTitle ? "55" : "35"); //02122014 add title from view
		
		//String reportPath, String reportPathTmp) 
		JasperReportGenerator jrg = new JasperReportGenerator(conn, mLang);
    
		//config parameters and replacers
		HashMap<String, String> hmReplacers = new HashMap<String, String>();
		HashMap<String, String> hmParameters = new HashMap<String, String>();
		hmReplacers.put("@replacerAll@", reportTemplate); 
	 
		toReturn = jrg.getCompiledPath(reportId, "rATPlaceHolder", xmlPath, xmlTmpPath,hmReplacers);
	
		return toReturn;
	}
     
        public JasperReport geAtGroupedJasperReportCompiled(String reportId, String tableName, String reportFilter) throws Exception
        {
           return  geAtGroupedJasperReportCompiled( reportId,  tableName,  reportFilter, true);
        }
        
        public JasperReport geAtGroupedJasperReportCompiledDataInjection(String reportId, String tableName, String dataInjectionFilterFieldName, String dataInjectionFilterParamName, boolean isPortrait) throws Exception 
        {
                //set data injection class values
                isDataInjection = true;
                this.dataInjectionFilterFieldName = dataInjectionFilterFieldName != null ? dataInjectionFilterFieldName.toUpperCase() : null;
                this.dataInjectionFilterParamName = dataInjectionFilterParamName;
                //call...
                return geAtGroupedJasperReportCompiled( reportId, tableName, " and 1=2 ", isPortrait);
        }
     
        public JasperReport geAtGroupedJasperReportCompiled(String reportId, String tableName, String reportFilter, boolean isPortrait) throws Exception
        {
                 JasperReport toReturn = null;
                 String reportTemplate = "";
                 String templateSql = " select  t.*, max(t.change_id) over (partition by id order by change_id desc) as MAX_CHANGE_ID from " + tableName + " t where 1=1 " + reportFilter; //...order by change_id desc because the max function somtimes change the order
                 String templateField = "<field name=\"@fieldName@\" class=\"java.lang.String\"/>\n";
                 String[] columnsPercentSize = null;
                 int elementWidth = 0;
                 int colIndex = 0;
                 int xPosition = 0;
                 DbBasicProvider dbProider = new DbBasicProvider();
         
                 String templateFieldHeader = "\n" + "<staticText>\n" + "<reportElement\n" + "  mode=\"Opaque\"\n" + "  x=\"@elementPosition@\"\n" + "  y=\"@elementYPosition@\"\n" + "  width=\"@elementWidth@\"\n" + "  height=\"35\"\n" + "  backcolor=\"@J!jasperHeaderColor@\"\n" + "  key=\"staticText-3\"/>\n" + "<box leftPadding=\"2\" rightPadding=\"2\" topPadding=\"2\" bottomPadding=\"2\">\n" + "  <pen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <topPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <leftPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <bottomPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "  <rightPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "</box>\n" + "<textElement>\n" + "  <font pdfFontName=\"@J!jasperHeaderFont@\" size=\"@J!jasperHeaderSize@\" isBold=\"true\"/>\n" + "</textElement>\n" + "<text><![CDATA[@fieldName@]]></text>\n" + "</staticText>\n"; //02122014 add title from view (@elementYPosition@  is 20 if title else 0)
                 String templateFieldDetails = "<textField isStretchWithOverflow=\"true\" isBlankWhenNull=\"true\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >\n" + "    <reportElement\n" + "        style=\"MarkFirstLine\"\n" + "        x=\"@elementPosition@\"\n" + "        y=\"0\"\n" + "        width=\"@elementWidth@\"\n" + "        height=\"20\"\n" + "       key=\"textField-8\"\n"  + "     isPrintWhenDetailOverflows=\"true\"\n" + "      stretchType=\"RelativeToBandHeight\"/>\n" + "    <box leftPadding=\"2\" rightPadding=\"2\" topPadding=\"2\" bottomPadding=\"2\">\n" + "      <pen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <topPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <leftPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <bottomPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "      <rightPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n" + "    </box>\n" + "    <textElement  >\n" + "      <font fontName=\"@J!jasperFont@\" pdfFontName=\"@J!jasperFontPdf@\" isPdfEmbedded =\"@J!jasperIsPdfEmbedded@\" size=\"@J!jasperDetailsSize@\"/>\n" + "    </textElement>\n" + "    <textFieldExpression   class=\"java.lang.String\"><![CDATA[$F{@fieldName@}]]></textFieldExpression>\n" + "  </textField>";
            String templateSubReportTitle = "";
            if(subReportTitle.equals("")) 
            {
                templateSubReportTitle = "<textField isStretchWithOverflow=\"false\" isBlankWhenNull=\"false\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >\n" + 
                "      <reportElement\n" + 
                "              x=\"0\"\n" + 
                "              y=\"0\"\n" + 
                "              width=\"500\"\n" + 
                "              height=\"20\"\n" + 
                "              forecolor=\"@J!jasperSubReportHeaderColor@\"\n" +
                "              key=\"textField-9\"/>\n" + 
                "      <box></box>\n" + 
                "      <textElement>\n" + 
                "              <font pdfFontName=\"@J!jasperSubReportHeaderFont@\" size=\"@J!jasperSubReportHeaderSize@\" isBold=\"true\"/>\n" + 
                "      </textElement>\n" + 
                "      <textFieldExpression   class=\"java.lang.String\"><![CDATA[$F{JR_SUBREPORT_TITLE}]]></textFieldExpression>\n" + 
                "</textField>";  //02122014 add title from view
            }
            else 
            {
                templateSubReportTitle = "<staticText >\n" + 
                "      <reportElement\n" + 
                "              x=\"0\"\n" + 
                "              y=\"0\"\n" + 
                "              width=\"500\"\n" + 
                "              height=\"20\"\n" + 
                "              forecolor=\"@J!jasperSubReportHeaderColor@\"\n" +
                "              key=\"textField-9\"/>\n" + 
                "      <box></box>\n" + 
                "      <textElement>\n" + 
                "              <font pdfFontName=\"@J!jasperSubReportHeaderFont@\" size=\"@J!jasperSubReportHeaderSize@\" isBold=\"true\"/>\n" + 
                "      </textElement>\n" + 
                "      <text><![CDATA[" + subReportTitle + "]]></text>\n" + 
                "</staticText>";  //19022015 add title from setter / Constructor
            }
                                
                 //get xml template  
                 FileInputStream input = new FileInputStream(new File(xmlTemplatePath + "\\" + (isPortrait ? AT_GROUP_PORTRAIT_XML_SUBREPROT:AT_GROUP_LANDSCAPE_XML_SUBREPROT)));
                 BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                 StringBuilder sb = new StringBuilder();
                 String line = null;
                 while ((line = reader.readLine()) != null)
                 {
                         sb.append(line + "\n");
                 }
                 reportTemplate = sb.toString();
         
                 //get Column sizes (if defined) 
                 columnsPercentSize = dbProider.getColumnSizeByTableName(conn,tableName,reportFilter);
                 int sumPercent = 100;
                 if (columnsPercentSize != null) 
                 {
                     sumPercent =  getSumPercentgetSumPercent(columnsPercentSize);
                 }
         
                 //generate instance elements using the table info       
                 List<String> colList = dbProider.getTableColumne(conn,tableName);
                 Iterator colIterator = colList.iterator();
         
                 StringBuilder sbFields = new StringBuilder(dataInjectionFilterFieldName != null && !dataInjectionFilterFieldName.trim().equals("") ? "<field name=\"" + dataInjectionFilterFieldName + "\" class=\"java.lang.Object\"/>\n" : "");
                 StringBuilder sbHeader = new StringBuilder();
                 StringBuilder sbDetails = new StringBuilder();
         
                 if (columnsPercentSize == null)
                 { 
                         elementWidth = (isPortrait ? PORTRAIT_WIDTH:LANDSCAPE_WIDTH) / (colList.size()); 
                 }
                 else
                 {
                         // if columnsPercentSize defined the elementWidth is the report width
                         elementWidth = (isPortrait ? PORTRAIT_WIDTH:LANDSCAPE_WIDTH);
                 }
                 
                boolean isTitle = colList.contains("jr_subreport_title") || colList.contains("JR_SUBREPORT_TITLE") || !subReportTitle.equals(""); //02122014 add title from view
                if(isTitle)
                {
                        sbFields.append(templateField.replace("@fieldName@", "JR_SUBREPORT_TITLE"));
                        sbHeader.append(templateSubReportTitle);
                        templateFieldHeader = templateFieldHeader.replace("@elementYPosition@","20");
                }
                else 
                {
                         templateFieldHeader = templateFieldHeader.replace("@elementYPosition@","0");
                }
         
                 while (colIterator.hasNext())
                 {
                         String colName = (String) colIterator.next();
                         
                         if(colName.toLowerCase().equals("jr_subreport_title")) //02122014 add title from view (pass jr_subreport_title field)
                         {
                             continue;
                         }
         
                         //fields
                         sbFields.append(templateField.replace("@fieldName@", colName));
                         
                         String nameDisplay = "";
                         
                         if (colName.length() > 1)
                         {
                             nameDisplay = colName.substring(0,1).toUpperCase() + colName.substring(1).toLowerCase();
                         }
                         else
                         {
                             nameDisplay = colName;
                         }
                          
                         if (columnsPercentSize == null)
                         //All columns are in the same size
                         {
                                 //fields header 
                                 sbHeader.append(templateFieldHeader.replace("@elementPosition@", String.valueOf(colIndex * elementWidth)).replace("@elementWidth@", String.valueOf(elementWidth)).replace("@fieldName@", mLang == null ? nameDisplay : "@L!" + nameDisplay + "@"));
                                 //fields detail
                                 sbDetails.append(templateFieldDetails.replace("@elementPosition@", String.valueOf(colIndex * elementWidth)).replace("@elementWidth@", String.valueOf(elementWidth)).replace("@fieldName@", colName));
                         }
                         else
                         // Defined columns size
                         {
                             if(Integer.parseInt(columnsPercentSize[colIndex].trim()) != 0) 
                             {
                                 //fields header 
                                 sbHeader.append(templateFieldHeader.replace("@elementPosition@", String.valueOf(xPosition)).replace("@elementWidth@", String.valueOf((elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent)).replace("@fieldName@", mLang == null ? nameDisplay : "@L!" + nameDisplay + "@"));
                                 //fields detail
                                 sbDetails.append(templateFieldDetails.replace("@elementPosition@", String.valueOf(xPosition)).replace("@elementWidth@", String.valueOf((elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent)).replace("@fieldName@", colName));
                                 xPosition += (elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim())) / sumPercent;
                             }
                         }
                         //update colIndex
                         colIndex++;
                  
                 }
                 sbFields.append("<field name=\"CHANGE_ID\" class=\"java.lang.String\"/>\n");
                 sbFields.append("<field name=\"MAX_CHANGE_ID\" class=\"java.lang.String\"/>\n");
                 
                //data injection 
                if(isDataInjection) 
                { 
                    if(dataInjectionFilterParamName != null && !dataInjectionFilterParamName.trim().equals("") && dataInjectionFilterFieldName != null && !dataInjectionFilterFieldName.trim().equals("")) // with filter
                    {
                        reportTemplate = reportTemplate.replace("@FILTER_PARAM_NAME@", dataInjectionFilterParamName);
                        reportTemplate = reportTemplate.replace("@filterExpression@","<filterExpression><![CDATA[new Boolean(String.valueOf($F{" +dataInjectionFilterFieldName + "}).equals(String.valueOf($P{" + dataInjectionFilterParamName + "})))]]></filterExpression>");
                    }
                    else // no filter
                    {
                        reportTemplate = reportTemplate.replace("@FILTER_FIELD_NAME@", "FILTER_DUMMY");
                        reportTemplate = reportTemplate.replace("@filterExpression@","");
                    }
                   
                    reportTemplate = reportTemplate.replace("@sql@", "/* " + templateSql + " */");
                    //...set data Injection values back to default
                    isDataInjection = false;
                    this.dataInjectionFilterFieldName = "";
                    this.dataInjectionFilterParamName = "";
                }
                else 
                {
                    reportTemplate = reportTemplate.replace("@FILTER_FIELD_NAME@", "FILTER_DUMMY");
                    reportTemplate = reportTemplate.replace("@filterExpression@","");
                    reportTemplate = reportTemplate.replace("@sql@", templateSql);
                }
        
                 reportTemplate = reportTemplate.replace("@fields@", sbFields.toString());
                 reportTemplate = reportTemplate.replace("@headers@", sbHeader.toString());
                 reportTemplate = reportTemplate.replace("@details@", sbDetails.toString());
                 reportTemplate = reportTemplate.replace("@headerBandHeight@", isTitle ? "55" : "35"); //02122014 add title from view
                 
                 //String reportPath, String reportPathTmp) 
                 JasperReportGenerator jrg = new JasperReportGenerator(conn, mLang);
         
                 //config parameters and replacers
                 HashMap<String, String> hmReplacers = new HashMap<String, String>();
                 HashMap<String, String> hmParameters = new HashMap<String, String>();
                 hmReplacers.put("@replacerAll@", reportTemplate); 
          
                 toReturn = jrg.getCompiled(reportId, "rATPlaceHolder", xmlPath, xmlTmpPath,hmReplacers);
         
                 return toReturn;
        } 
        
	public String getAtChangesJasperReport(String reportId, JasperReportType reportType, String reportTitle, String reportFilterDisplay, String tableName, String reportIDFilter) throws Exception
	{
		String toReturn = null;
		StringBuilder sbCol = new StringBuilder();
		StringBuilder sbValFormat = new StringBuilder();
		StringBuilder sbColFormat = new StringBuilder();
		StringBuilder sbColNames = new StringBuilder();
		DbBasicProvider dbProider = new DbBasicProvider();

		List<String> colList = dbProider.getAuditTrailTableColumne(conn,tableName);
		Iterator colIterator = colList.iterator();

		while (colIterator.hasNext())
		{
			sbCol.append(colIterator.next() + ",");
		}

		getATColInfoUsingConfDelimiter(sbCol.toString().substring(0, sbCol.toString().length() - 1), sbValFormat, sbColFormat, sbColNames);

		//String reportPath, String reportPathTmp) 
		JasperReportGenerator jrg = new JasperReportGenerator(conn, mLang);

		//config parameters and replacers
		HashMap<String, String> hmReplacers = new HashMap<String, String>();
		hmReplacers.put("@reportInstance@", "(new jasper.biz.JasperReportHelper())");
	        hmReplacers.put("@title@", reportTitle); 
	        hmReplacers.put("@filterDisplay@",reportFilterDisplay);  
		HashMap<String, Object> hmParameters = new HashMap<String, Object>();
		hmParameters.put("ReportParameter1", sbValFormat.toString());
		hmParameters.put("ReportParameter2", sbColFormat.toString());
		hmParameters.put("ReportParameter3", sbColNames.toString());
		hmParameters.put("ReportParameter4", tableName);
		hmParameters.put("ReportParameter5", xmlPath);
		hmParameters.put("ReportParameter6", xmlTmpPath);
		hmParameters.put("ReportParameter7", reportId);
		hmParameters.put("ReportParameter8", reportIDFilter);

		//call the jasperCore  get result
		toReturn = jrg.getPath(reportId, "rATBasicMain.xml", reportType, "", "", hmReplacers, hmParameters, xmlPath, xmlTmpPath);

		return toReturn;
	}
	     
	public ByteArrayOutputStream getAtChangesJasperReportStream(String reportId, JasperReportType reportType, String reportTitle, String reportFilterDisplay, String tableName, String reportIDFilter) throws Exception
	{
		ByteArrayOutputStream toReturn = null;
		StringBuilder sbCol = new StringBuilder();
		StringBuilder sbValFormat = new StringBuilder();
		StringBuilder sbColFormat = new StringBuilder();
		StringBuilder sbColNames = new StringBuilder();
		DbBasicProvider dbProider = new DbBasicProvider();
    
		List<String> colList = dbProider.getAuditTrailTableColumne(conn,tableName);
		Iterator colIterator = colList.iterator();
    
		while (colIterator.hasNext())
		{
			sbCol.append(colIterator.next() + ",");
		}
    
		getATColInfoUsingConfDelimiter(sbCol.toString().substring(0, sbCol.toString().length() - 1), sbValFormat, sbColFormat, sbColNames);
    
		//String reportPath, String reportPathTmp) 
		JasperReportGenerator jrg = new JasperReportGenerator(conn, mLang);
    
		//config parameters and replacers
		HashMap<String, String> hmReplacers = new HashMap<String, String>();
		hmReplacers.put("@reportInstance@", "(new jasper.biz.JasperReportHelper())"); 
	        hmReplacers.put("@title@", reportTitle); 
	        hmReplacers.put("@filterDisplay@",reportFilterDisplay); 
		HashMap<String, Object> hmParameters = new HashMap<String, Object>();
		hmParameters.put("ReportParameter1", sbValFormat.toString());
		hmParameters.put("ReportParameter2", sbColFormat.toString());
		hmParameters.put("ReportParameter3", sbColNames.toString());
		hmParameters.put("ReportParameter4", tableName);
		hmParameters.put("ReportParameter5", xmlPath);
		hmParameters.put("ReportParameter6", xmlTmpPath);
		hmParameters.put("ReportParameter7", reportId);
		hmParameters.put("ReportParameter8", reportIDFilter); 
    
		//call the jasperCore  get result
		toReturn = jrg.getByteArrayOutputStream(reportId, "rATBasicMain.xml", reportType, "", "", hmReplacers, hmParameters, xmlPath, xmlTmpPath);
    
		return toReturn;
	}

        public void getATColInfoUsingConfDelimiter(String sCol, StringBuilder sbValFormat, StringBuilder sbColFormat, StringBuilder sbColNames) throws Exception
        {
                String[] arrCol = sCol.split(",");
                GeneralBiz generalBiz = new GeneralBiz();
                String delimiter = generalBiz.getProperty(xmlPath,"jasperDelimiter");
                StringBuilder sbValFormatTmp = new StringBuilder() ;
                
                if( arrCol.length == 1 )
                {
                    String alias_ = arrCol[0];
                    sbColFormat.append("\"" + alias_ + "\"");
                    //sbValFormat.append("CONCAT_CLOB(nvl(to_clob(t1.\"" + alias_ + "\"),to_clob('na_unique1')),to_clob('" + delimiter + "'))");
                    sbValFormatTmp.append("nvl(to_clob(t1.\"" + alias_ + "\"),to_clob('na_unique1'))");
                    sbColNames.append("\"" + alias_ + "\""); 
                }
                else 
                {
                    for (int i = 0; i < arrCol.length; i++)
                    {
                           String alias_ = arrCol[i]; 
                           String tmpForamt =sbValFormatTmp.toString();
                        
                            if (i != arrCol.length - 1)
                            {
                                    sbColFormat.append("\"" + alias_ + "\"" + delimiter);
                                    if (i ==0)
                                    { 
                                         sbValFormatTmp.append("CONCAT_CLOB(nvl(to_clob(t1.\"" + alias_ + "\"),to_clob('na_unique1')),to_clob('" + delimiter + "'))");
                                    }
                                    else
                                    {   
                                        sbValFormatTmp = new StringBuilder("CONCAT_CLOB( ").append(tmpForamt).append(",").append("CONCAT_CLOB(nvl(to_clob(t1.\"" + alias_ + "\"),to_clob('na_unique1')),to_clob('" + delimiter + "'))").append(" )");
                                    }
                                    sbColNames.append("\"" + alias_ + "\",");
                            }
                            else
                            { 
                                    sbColFormat.append("\"" + alias_ + "\"");
                                    sbValFormatTmp = new StringBuilder("CONCAT_CLOB( ").append(tmpForamt).append(",").append("nvl(to_clob(t1.\"" + alias_ + "\"),to_clob('na_unique1'))").append(" )");
                                    sbColNames.append("\"" + alias_ + "\"");
                            }
                    }
                } 
            sbValFormat.append(sbValFormatTmp.toString());
            
        }
    
        private int getSumPercentgetSumPercent(String[] columnsPercentSize)
        {
            int toRetutn = 0;
            try 
            {
                for(int i = 0; i< columnsPercentSize.length;i++) 
                {
                    toRetutn += Integer.parseInt(columnsPercentSize[i].trim());
                }
            }
            catch (Exception e) 
            {
                toRetutn = 100;
            }
            return toRetutn;
        }

    public void setMLang(HashMap<String, String> mLang) 
    {
        this.mLang = mLang;
    }

    public Map<String, String> getMLang() 
    {
        return mLang;
    }

    public void setSubReportTitle(String subReportTitle) 
    {
        this.subReportTitle = subReportTitle;
    }

    public String getSubReportTitle() 
    {
        return subReportTitle;
    } 
    
	public boolean createIreport(String fileName, String reportTitle, String reportFilterDisplay, String tableName,
			boolean isPortrait) throws Exception {
		boolean isSuccess = false;
//		StringBuilder rNameToCompile = new StringBuilder(fileName);
		File targetFile = new File(xmlPath + "\\" + fileName + ".xml");
		if (!targetFile.exists()) {
			String reportTemplate = "";
			String templateSql = " select  t.* from " + tableName + " t where 1=2";
			String templateField = "<field name=\"@fieldName@\" class=\"java.lang.String\"/>\n";
			String[] columnsPercentSize = null;
			int elementWidth = 0;
			int colIndex = 0;
			int xPosition = 0;
			DbBasicProvider dbProider = new DbBasicProvider();

			String templateFieldHeader = "\n" + "<staticText>\n" + "<reportElement\n" + "  mode=\"Opaque\"\n"
					+ "  x=\"@elementPosition@\"\n" + "  y=\"0\"\n" + "  width=\"@elementWidth@\"\n"
					+ "  height=\"35\"\n" + "  backcolor=\"@J!jasperHeaderColor@\"\n" + "  key=\"staticText-3\"/>\n"
					+ "<box leftPadding=\"2\" rightPadding=\"2\" topPadding=\"2\" bottomPadding=\"2\">\n"
					+ "  <pen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n"
					+ "  <topPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n"
					+ "  <leftPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n"
					+ "  <bottomPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n"
					+ "  <rightPen lineWidth=\"0.25\" lineColor=\"#000000\"/>\n" + "</box>\n" + "<textElement>\n"
					+ "  <font pdfFontName=\"@J!jasperHeaderFont@\" size=\"@J!jasperHeaderSize@\" isBold=\"true\"/>\n"
					+ "</textElement>\n" + "<text><![CDATA[@fieldName@]]></text>\n" + "</staticText>\n";
			String templateFieldDetails = "<textField isStretchWithOverflow=\"true\" isBlankWhenNull=\"true\" evaluationTime=\"Now\" hyperlinkType=\"None\"  hyperlinkTarget=\"Self\" >\n"
					+ "    <reportElement\n" + "        x=\"@elementPosition@\"\n" + "        y=\"0\"\n"
					+ "        width=\"@elementWidth@\"\n" + "        height=\"20\"\n" + "       key=\"textField-8\"\n"
					+ "	isPrintWhenDetailOverflows=\"true\"\n" + "      stretchType=\"RelativeToTallestObject\"/>\n"
					+ "    <box leftPadding=\"2\" rightPadding=\"2\" topPadding=\"2\" bottomPadding=\"2\">\n"
					+ "      <pen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n"
					+ "      <topPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n"
					+ "      <leftPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n"
					+ "      <bottomPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n"
					+ "      <rightPen lineWidth=\"0.25\" lineStyle=\"Solid\" lineColor=\"#000000\"/>\n"
					+ "    </box>\n" + "    <textElement  >\n"
					+ "      <font fontName=\"@J!jasperFont@\" pdfFontName=\"@J!jasperFontPdf@\" isPdfEmbedded =\"@J!jasperIsPdfEmbedded@\" size=\"@J!jasperDetailsSize@\"/>\n"
					+ "    </textElement>\n"
					+ "    <textFieldExpression   class=\"java.lang.String\"><![CDATA[$F{@fieldName@}]]></textFieldExpression>\n"
					+ "  </textField>";

			// get xml template
			FileInputStream input = new FileInputStream(
					new File(xmlTemplatePath + "\\" + (isPortrait ? PORTRAIT_XML : LANDSCAPE_XML)));
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			reportTemplate = sb.toString();

			// get Column sizes (if defined)
			columnsPercentSize = dbProider.getColumnSizeByTableName(conn,tableName, " and 1=1 ");
			int sumPercent = 100;
			if (columnsPercentSize != null) {
				sumPercent = getSumPercentgetSumPercent(columnsPercentSize);
			}

			// generate instance elements using the table info
			List<String> colList = dbProider.getTableColumne(conn,tableName);
			Iterator colIterator = colList.iterator();

			StringBuilder sbFields = new StringBuilder();
			StringBuilder sbHeader = new StringBuilder();
			StringBuilder sbDetails = new StringBuilder();

			if (columnsPercentSize == null) {
				elementWidth = (isPortrait ? PORTRAIT_WIDTH : LANDSCAPE_WIDTH) / (colList.size());
			} else {
				// if columnsPercentSize defined the elementWidth is the report
				// width
				elementWidth = (isPortrait ? PORTRAIT_WIDTH : LANDSCAPE_WIDTH);
			}

			while (colIterator.hasNext()) {
				String colName = (String) colIterator.next();

				// fields
				sbFields.append(templateField.replace("@fieldName@", colName));

				if (columnsPercentSize == null)
				// All columns are in the same size
				{
					// fields header
					sbHeader.append(
							templateFieldHeader.replace("@elementPosition@", String.valueOf(colIndex * elementWidth))
									.replace("@elementWidth@", String.valueOf(elementWidth))
									.replace("@fieldName@", mLang == null ? colName : "@L!" + colName + "@"));
					// fields detail
					sbDetails.append(templateFieldDetails
							.replace("@elementPosition@", String.valueOf(colIndex * elementWidth))
							.replace("@elementWidth@", String.valueOf(elementWidth)).replace("@fieldName@", colName));
				} else
				// Defined columns size
				{
					if (Integer.parseInt(columnsPercentSize[colIndex].trim()) != 0) {
						// fields header
						sbHeader.append(templateFieldHeader.replace("@elementPosition@", String.valueOf(xPosition))
								.replace("@elementWidth@",
										String.valueOf(
												(elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim()))
														/ sumPercent))
								.replace("@fieldName@", mLang == null ? colName : "@L!" + colName + "@"));
						// fields detail
						sbDetails
								.append(templateFieldDetails.replace("@elementPosition@", String.valueOf(xPosition))
										.replace("@elementWidth@", String.valueOf(
												(elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim()))
														/ sumPercent))
										.replace("@fieldName@", colName));
						xPosition += (elementWidth * Integer.parseInt(columnsPercentSize[colIndex].trim()))
								/ sumPercent;
					}
				}
				// update colIndex
				colIndex++;

			}

			reportTemplate = reportTemplate.replace("@sql@", templateSql);
			reportTemplate = reportTemplate.replace("@fields@", sbFields.toString());
			reportTemplate = reportTemplate.replace("@headers@", sbHeader.toString());
			reportTemplate = reportTemplate.replace("@details@", sbDetails.toString());
			reportTemplate = reportTemplate.replace("@title@", reportTitle);
			reportTemplate = reportTemplate.replace("@filterDisplay@", reportFilterDisplay);
 
			//JasperReportGenerator jrg = new JasperReportGenerator(conn, mLang);

			// config parameters and replacers
			HashMap<String, String> hmReplacers = new HashMap<String, String>();
			HashMap<String, Object> hmParameters = new HashMap<String, Object>();
			hmReplacers.put("@replacerAll@", reportTemplate);
			// hmReplacers.put("@replacer2@", reportFilter);

			//..(code form report generator)
			InputStream inputModified = null;
			JasperDesign design = null;
			GeneralBiz generalBiz = new GeneralBiz(); 
			
			//xml
			input = new FileInputStream(new File(xmlPath + "\\rATPlaceHolder"));
			inputModified = generalBiz.InputStreamModified(xmlPath, input, hmReplacers, mLang, fileName);
			generalBiz.copyFileUsingFileStreams(inputModified ,targetFile);
			
			//Compile?
//			input = new FileInputStream(new File(xmlPath + "\\rATPlaceHolder"));
//			inputModified = generalBiz.InputStreamModified(xmlPath, input, hmReplacers, mLang);
//			design = JRXmlLoader.load(inputModified);
//			JasperCompileManager.compileReportToFile(design, xmlPath + "\\" + fileName + ".jasper");

			input.close();
			inputModified.close();
			isSuccess = true;
		}
		return isSuccess;
	}
}
