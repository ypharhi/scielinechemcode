package jasper.biz;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 * note: 1) if Jasper.config has _DEVELOP_GENERATE_COMPILE_FILE key we create compile jasper file (to make the render process faster). we do it if there no replacers in the file (that force on the fly compilation)
 * 		 2) if in the Jasper.config file there is the _DEVELOP_GENERATE_FINAL_XML key we save the xml in the tmp folder (using the xmlFileName) to make the develop easer by using the factory class in the call env (by sql /datatable json data) and then we have already the basic xml design.
 * @author YPharhi
 *
 */
public class GeneralBiz {
	public InputStream InputStreamModified(String xmlPath, InputStream isOriginal,
			HashMap<String, String> hmReportFindAndReplaceList, HashMap<String, String> hmLang, String xmlFileName) throws Exception {
		InputStream isToReturn = null;
		String sFileContenet = "";

		BufferedReader reader = new BufferedReader(new InputStreamReader(isOriginal));
		StringBuilder sb = new StringBuilder();
		String line = null;
		while ((line = reader.readLine()) != null) {
			sb.append(line + "\n");
		}
		isOriginal.close();

		sFileContenet = sb.toString();
		String sFileContenetCompare1 = sFileContenet; //content before replacers

		// replace replacers (we don't use the replaceSourceWithTokens function
		// because it does not replace the backslash as we expected)
		if (hmReportFindAndReplaceList != null) {
			Set set = hmReportFindAndReplaceList.entrySet();
			Iterator i = set.iterator();

			while (i.hasNext()) {
				Map.Entry mapEntry = (Map.Entry) i.next();
				String key = (String) mapEntry.getKey();
				String value = (String) mapEntry.getValue();

				if (key != null && !key.trim().equals("") && value != null) {
					sFileContenet = sFileContenet.replace(key, value);
				}
			}
		}
		
		String sFileContenetCompare2 = sFileContenet; //content after replacers
		
		// replace lang
		if (hmLang != null) {
			sFileContenet = replaceSourceWithTokens(sFileContenet, hmLang, "@(L!(.+?))@", 2);
		}

		// replace design
		HashMap<String, String> hmProp = getPropertiesHashMap(xmlPath);
		if (hmProp != null) {
			sFileContenet = replaceSourceWithTokens(sFileContenet, hmProp, "@(J!(.+?))@", 2);
		}
		 
		if (hmProp != null && hmProp.containsKey("_DEVELOP_GENERATE_FINAL_XML")) { 
			try {
				PrintWriter out = new PrintWriter(xmlPath + "\\tmp\\_DEVELOP_GENERATE_FINAL_XML_" + xmlFileName + "_" + (new Date()).getTime() + ".xml");
				out.write(sFileContenet);
				out.close();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (sFileContenetCompare1.equals(sFileContenetCompare2) && hmProp != null && hmProp.containsKey("_DEVELOP_GENERATE_COMPILE_FILE")) {  // sFileContenetCompare1.equals(sFileContenetCompare2) true if no replacers
			try {
				InputStream isGenerateCompile = new ByteArrayInputStream(sFileContenet.getBytes("UTF-8"));
				JasperDesign design = JRXmlLoader.load(isGenerateCompile);
				JasperCompileManager.compileReportToFile(design, xmlPath + "\\" + xmlFileName.replace(".xml", "") + ".jasper");
			} catch (Exception e) {
				System.out.println("Error in creating jasper compiled file (try to compile it in ireport). e=" + e.getMessage());
			} 
		}

		isToReturn = new ByteArrayInputStream(sFileContenet.getBytes("UTF-8"));

		return isToReturn;
	}

	public String modifiedLangOnString(String source, HashMap<String, String> hmLang) throws Exception {
		return replaceSourceWithTokens(source, hmLang, "@(L!(.+?))@", 2);
	}

	public HashMap<String, String> getPropertiesHashMap(String xmlPath) throws Exception {
		Properties prop;
		prop = new Properties();
		InputStream inputStream = new FileInputStream(xmlPath + "\\Jasper.config");
		prop.load(inputStream);

		Enumeration e = prop.propertyNames();
		HashMap<String, String> map = new HashMap<String, String>();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			map.put(key, prop.getProperty(key));
		}

		return map.size() > 0 ? map : null;
	}

	public String getProperty(String xmlPath, String name) throws Exception {
		Properties prop;
		prop = new Properties();
		InputStream inputStream = new FileInputStream(xmlPath + "\\Jasper.config");
		prop.load(inputStream);
		return prop.getProperty(name);
	}

	/**
	 * 
	 * @param source
	 * @param tokens
	 *            hash map of replacers
	 * @param patternString
	 *            regex
	 * @param patternGroupNumber
	 *            the group number for the matcher. note:Parenthesis () are used
	 *            to enable grouping of regex phrases.
	 * @return the source after replacing the map tokens keys that have
	 *         patternString prefix and suffix in the source string with the map
	 *         tokens values Note(warnning): the appendReplacement dosn't keep
	 *         the double backslash in the target string
	 */
	private String replaceSourceWithTokens(String source, Map<String, String> tokens, String patternString,
			int patternGroupNumber) {
		String template = source;
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(template);
		StringBuffer sb = new StringBuffer();
		String matchKey_ = "";
		while (matcher.find()) {
			matchKey_ = matcher.group(patternGroupNumber);
			matcher.appendReplacement(sb, tokens.containsKey(matchKey_) ? tokens.get(matchKey_) : matchKey_);
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	public void copyFileUsingFileStreams(InputStream input, File dest) throws IOException {
		 
		OutputStream output = null;
		try {
			output = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int bytesRead;
			while ((bytesRead = input.read(buf)) > 0) {
				output.write(buf, 0, bytesRead);
			}
		} finally {
			input.close();
			output.close();
		}
	}

	public boolean isGenerateCompileFileOnDevelop(String reportPath) {
		boolean toReturn = false;
		try {
			HashMap<String, String> hmProp = getPropertiesHashMap(reportPath);
			toReturn =  hmProp.containsKey("_DEVELOP_GENERATE_COMPILE_FILE");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return toReturn;
	}
}
