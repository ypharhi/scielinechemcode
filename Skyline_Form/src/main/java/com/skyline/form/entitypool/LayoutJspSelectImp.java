package com.skyline.form.entitypool;

import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;

import com.skyline.form.bean.BeanType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.entity.Layout;

/**
 * Jsp that can be useed by developers to have more flexible code
 * - keep the bookmarks between <!-- body --> and </body> the same format of tabs if the integration with the formbuilder display screen is important as in testForm.jsp for example).
 * @author comply
 *
 */
public class LayoutJspSelectImp extends Layout {
	private String jspName;
	@Value("${jspPath}")
	private String path;
	
	private String tabsCSV = "";

	@Override
	public String init(long stateKey, String formCode, String impCode, String initVal) {
		try{
			if(super.init(stateKey, formCode, impCode, initVal).equals(""))
			{
				this.jspName = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "jspName");
				jspName=jspName.substring(0, jspName.lastIndexOf('.'));
				this.tabsCSV = generalUtilForm.getJsonVal(stateKey, formCode, jsonInit, "tabsCSV");
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

	@Override
	public String getJspName() {
		return jspName;
	}
	
	@Override
	public String getInitSchemaVal() {
		String schema = super.getInitSchemaVal();
		schema = "schema:{ \r\n" + "jspName:{  \r\n" + "      type:'string',\r\n" + "      title:'Page',\r\n"
				+ "      'enum':getResourceCodeValueInfoByType(\"PATH_JSP\")\r\n" + "   }," +
				"    tabsCSV:{\n" + 
				"        type:'string',\n" + 
				"        title:'Tabs CSV'\n" + 			
				"    },\n"
				+ (schema.equals("") ? "" : ",\n" + schema) + "\r\n"
				+ "}";
		return schema;
	}
	
	//single tone class functions

	@Override
	public List<DataBean> getImpResourceForFormBuilder(String formCode, String impCode, String initVal) {
		List<DataBean> dataBeanList = super.getImpResourceForFormBuilder(formCode, impCode, initVal); //super return the bean
		String content = "";
		try {
			String jspName_ = generalUtil.getJsonValById(initVal, "jspName");
			jspName_ = (!jspName_.endsWith(".jsp"))?jspName_+ ".jsp":jspName_;
			Scanner scanner = new Scanner(new File(path + "\\" + jspName_));
			content = scanner.useDelimiter("\\Z").next();
			scanner.close();
			int start = content.indexOf("<!-- body -->");
			int end = content.indexOf("</body>");
			if(start > 0 && end > start) {
				content = content.substring(content.indexOf("<!-- body -->"), content.indexOf("</body>"));
				Pattern pattern = Pattern.compile("(\\$\\{)(.*?)(\\})");
				Matcher matcher = pattern.matcher(content);
				while (matcher.find()) {
					dataBeanList.add(new DataBean(matcher.group(2), matcher.group(2), BeanType.LAYOUT_ITEM_TEXT,
							"Info: " + matcher.group(2)));
				}
			}
		} catch (Exception e) {
			generalUtilLogger.logWrite(e);
			e.printStackTrace();
		}
		return dataBeanList;
	}
	
}
