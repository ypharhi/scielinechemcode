package com.skyline.form.entitypool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;

import com.skyline.form.bean.BeanType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.entity.Layout;

/**
 * 
 * @author YPharhi
 * TODO description
 * note: afterSave event "reload" will send EDIT_ON_RELOAD in the URL
 */
public class LayoutDesignHtmlImp extends Layout {

	private String jspName;

	@Value("${HtmlPath}")
	private String Htmlpath;

	private String htmlName;

	@Value("${jspPath}")
	private String jspPath;

	private String newJsp;

	private String templateType; //folder

	private String templateName; //template name

	private String sourceJSP = "";
	
	private String afterSave = "";
	
	private String tabsCSV = "";
	
	private boolean fileBuildFlag = false;
	
	@Value("${DELETE_AND_CREATE_JSP_DEVELOP_MODE:false}")
	private boolean DELETE_AND_CREATE_JSP_DEVELOP_MODE;
	
	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try {
			if (super.init(stateKey, formCode, impCode, initVal).equals("")) {
				this.jspName = impCode;
				this.htmlName = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "htmlName");
				this.afterSave = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "afterSave");
				this.templateType = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "templateType");
				this.templateName = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "templateName");
				this.tabsCSV = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "tabsCSV");

				newJsp = jspPath + "/" + jspName + ".jsp";
				if (!fileBuildFlag || stateKey == 0l) {
					if (!buildJSP()) {
						return "Creation failed";
					}
					fileBuildFlag = true;
				}
				return "";
			}
			return "Creation failed";
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			return "Creation failed";
		}
	}

	private boolean buildJSP() {
		try {
			Scanner scanner = new Scanner(new File(Htmlpath + "/" + htmlName));
			String everything = scanner.useDelimiter("\\Z").next();
			scanner.close();
			scanner = new Scanner(new File(
					jspPath + "/" + templateType.substring(templateType.lastIndexOf("_") + 1) + "/" + templateName));
			sourceJSP = scanner.useDelimiter("\\Z").next();
			scanner.close();
			everything = everything.replaceAll("\\$", "\\\\\\$");
			sourceJSP = sourceJSP.replaceAll("@body@", everything);
			StringBuilder sb_before_ready = new StringBuilder();
			StringBuilder sb_ready = new StringBuilder();
			StringBuilder sb_function = new StringBuilder();
			StringBuilder sb_html = new StringBuilder();
			List<DataBean> listOfBookMarks = getImpResourceForFormBuilderWithSourceJSP(formCode, impCode, initVal, sourceJSP);
			for (int i = 1; i < listOfBookMarks.size(); i++) 
			{
				sb_before_ready.append("\t\t\\${");
				sb_before_ready.append(listOfBookMarks.get(i).getCode() + "_before_ready");
				sb_before_ready.append("}\n");				
				sb_ready.append("\t\t\\${");
				sb_ready.append(listOfBookMarks.get(i).getCode() + "_ready");
				sb_ready.append("}\n");
				sb_function.append("\t\\${");
				sb_function.append(listOfBookMarks.get(i).getCode() + "_function");
				sb_function.append("}\n");
				sb_html.append("\t\t\\${");
				sb_html.append(listOfBookMarks.get(i).getCode() + "_html");
				sb_html.append("}\n");
			}
			String[] tabs = tabsCSV.split(",");
			StringBuilder tabsSB = new StringBuilder();
			for (String tab : tabs) {
				String tabCodeAndSensitivity  = tab.replaceAll(" ", "");
				if(!tabCodeAndSensitivity.contains(";")) {
					tabCodeAndSensitivity += ";0";
				}
				String tabCode = tabCodeAndSensitivity.split(";")[0];
				String sensitivityLevel = tabCodeAndSensitivity.split(";")[1];
				
				tabsSB.append("<li><a href=\"#" + tabCode + "Tab\" sensitivitylevel_order=\"" + sensitivityLevel + "\" >"
						+ generalUtil.getSpringMessagesByKey(tabCode.replaceAll(" ", "_"), (generalUtil.getNull(tab).contains(";")? tab.substring(0,tab.indexOf(";")): tab))
						+ "</a></li>\n");
			}
			sourceJSP = sourceJSP.replaceAll("@bm_list_before_ready@", sb_before_ready.toString())
					.replaceAll("@bm_list_ready@", sb_ready.toString())
					.replaceAll("@bm_list_function@", sb_function.toString())
					.replaceAll("@bm_list_html@", sb_html.toString())
					.replaceAll("@afterSave@", (afterSave.equals("") ? "doNothing" : afterSave))
					.replaceAll("@tempalteTabs@	",tabsSB.toString());
						
			//	PrintWriter writer = new PrintWriter(newJsp, "UTF-8");
			PrintWriter writer = new PrintWriter(new FileOutputStream(newJsp, false));
			writer.println(sourceJSP);
			writer.close();
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			return false;
		}
		return true;
	}
	
	private List<DataBean> getImpResourceForFormBuilderWithSourceJSP(String formCode, String impCode, String initVal, String sourceJSP) {
		List<DataBean> dataBeanList = super.getImpResourceForFormBuilder(formCode, impCode, initVal); //super return the bean
		dataBeanList.add(new DataBean(impCode, impCode, BeanType.LAYOUT_IMP_CODE, "Info: " + impCode));
		String content = "";
		try {
			content = sourceJSP.substring(sourceJSP.indexOf("<!-- body -->"), sourceJSP.indexOf("</body>"));
			Pattern pattern = Pattern.compile("(\\$\\{)(.*?)(\\})");
			Matcher matcher = pattern.matcher(content);
			while (matcher.find()) {
				dataBeanList.add(new DataBean(matcher.group(2), matcher.group(2), BeanType.LAYOUT_ITEM_TEXT,
						"Info: " + matcher.group(2)));
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return dataBeanList;
	}

	@Override
	public String getJspName() {
		if(DELETE_AND_CREATE_JSP_DEVELOP_MODE) {
			buildJSP();
		} else {
			if(!fileBuildFlag) {
				File f = new File(newJsp);
				if (!f.exists()) {
					buildJSP();
				}
			}
		}
		return jspName;
	}

	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema = "schema:{\n" + 
				"    htmlName:{\n" + 
				"        type:'string',\n" + 
				"        title:'Html',\n" + 
				"        'enum':getResourceCodeValueInfoByType(\"PATH_HTML_POOL\")\n" + 
				"    },\n" + 
				"    templateType:{\n" + 
				"        type:'hidden',\n" + 
				"        title:'Form Type',\n" + 
				"        'default':getFormType()\n" + 
				"    },\n" + 
				"    afterSave:{\n" + 
				"        type:'string',\n" + 
				"        title:'Do After Save',\n" + 
				"        'enum':['','Close','Reload']\n" + 
				"    },\n" + 
				"    tabsCSV:{\n" + 
				"        type:'string',\n" + 
				"        title:'Tabs CSV'\n" + 			
				"    },\n" + 
				"    templateName:{\n" + 
				"        type:'string',\n" + 
				"        title:'Template',\n" + 
				"        'enum':getFilesNamesByFormType()\n" + 
				"    }\n" + 
				"}";

		return schema;
	}
	
	//single tone class functions
	
	
	@Override
	public List<DataBean> getImpResourceForFormBuilder(String formCode, String impCode, String initVal) {
		List<DataBean> dataBeanList = super.getImpResourceForFormBuilder(formCode, impCode, initVal); //super return the bean
		dataBeanList.add(new DataBean(impCode, impCode, BeanType.LAYOUT_IMP_CODE, "Info: " + impCode));
		String content = "";
		try {
			String newJsp_ = jspPath + "/" + impCode + ".jsp";
			Scanner scanner = new Scanner(new File(newJsp_));
			String sourceJSP_ = scanner.useDelimiter("\\Z").next();
			scanner.close();
			 
			content = sourceJSP_.substring(sourceJSP_.indexOf("<!-- body -->"), sourceJSP_.indexOf("</body>"));
			Pattern pattern = Pattern.compile("(\\$\\{)(.*?)(\\})");
			Matcher matcher = pattern.matcher(content);
			while (matcher.find()) {
				dataBeanList.add(new DataBean(matcher.group(2), matcher.group(2), BeanType.LAYOUT_ITEM_TEXT,
						"Info: " + matcher.group(2)));
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return dataBeanList;
	}
}
