package jasper.biz;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Date;
import java.util.HashMap;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;


/**
 * Ireport data helper class
 */
public class JasperReportHelper
{
    public JasperReportHelper()
    {
    } 
    
    public String getATDetailsCompiledSubreportPath(String sValFormat, String sColFormat, String sColNames, String sTableName, String reportPath, String reportPathTmp, String sessionID) throws Exception
    {
	    String fileName = "";
	    InputStream input = null;
	    InputStream inputModified = null;
	    JasperDesign design = null;
	    Date date = new Date();
	    HashMap hmReportFindAndReplaceList = new HashMap();
	    GeneralBiz generalBiz = new GeneralBiz();

	    input = new FileInputStream(new File(reportPath + "\\rATBasicDetails.xml"));
	    hmReportFindAndReplaceList.put("@val_format@", sValFormat);
	    hmReportFindAndReplaceList.put("@col_format@", sColFormat);
	    hmReportFindAndReplaceList.put("@col_names@", sColNames);
	    hmReportFindAndReplaceList.put("@hst_table_name@", sTableName);
	    inputModified = generalBiz.InputStreamModified(reportPath,input, hmReportFindAndReplaceList,null,null);
	    
	    //copy inputModified to is1 and is2...
	    ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
	    byte[] buffer = new byte[1024];
	    int len;
	    while ((len = inputModified.read(buffer)) > -1 ) {
		baos.write(buffer, 0, len);
	    }
	    baos.flush(); 
	    InputStream is1 = new ByteArrayInputStream(baos.toByteArray()); 
	    InputStream is2 = new ByteArrayInputStream(baos.toByteArray()); 
	    
	     //print the sql using is2
	      try
	      {
	          BufferedReader reader = new BufferedReader(new InputStreamReader(is1));
	          StringBuilder sb = new StringBuilder();
	          String line = null;
	          while ((line = reader.readLine()) != null)
	          {
	               sb.append(line + "\n");
	          }  
	          String sFileContenet = sb.toString();
	          System.out.println("Sub report query(getATDetailsCompiledSubreportPath): " + sFileContenet.substring(sFileContenet.indexOf("<queryString>") + "<queryString>".length(),sFileContenet.indexOf("</queryString>"))); 
	      }
	      catch (Exception e)
	      {
	          System.out.println("Can't get sub report query!");
	      }
	     
	     //create the report using is2
	     design = JRXmlLoader.load(is2); 
	     
	     //close streams..
	     input.close();
	     inputModified.close(); 
	     is1.close();
	     is2.close();
	     
	     //create the compiled file and return it
	     fileName = reportPathTmp + "\\subreport" + sessionID + date.getTime();
	     JasperCompileManager.compileReportToFile(design, fileName);
 
	    return fileName;
    }
    
    public HashMap<String, String> getHashMapInstance(final String parseIt)
    { 
        HashMap<String, String> hm = new HashMap<String, String>();
        String[] fieldsArray = parseIt.split(";");
        for(int i = 0; i < fieldsArray.length; i++) 
        {
            //put key and value using the first comma as delimiter
            hm.put(fieldsArray[i].substring(0,fieldsArray[i].indexOf(",")),fieldsArray[i].substring(fieldsArray[i].indexOf(",")+1));
        }
        return hm;
    }
}
