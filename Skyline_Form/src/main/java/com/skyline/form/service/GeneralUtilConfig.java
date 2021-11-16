package com.skyline.form.service;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.skyline.form.bean.LookupType;
import com.skyline.form.dal.FormDao;

@Service
public class GeneralUtilConfig {

	@Autowired
	private FormDao formDao;

	@Autowired
	public GeneralUtil generalUtil;

	@Value("${precision}")
	private String precision; // precision

	private static final Logger logger = LoggerFactory.getLogger(GeneralUtilConfig.class);

	public void SaveEventHandler(String formCode, String formId, Map<String, String> elementValueMap, String userId,
			String eventType) throws ScriptException, ParseException {

		//TODO?
	}

	public String getCriterialSql(String struct, String criteria, String formCode, Map<String, String> sqlParam,
			List<String> unfilteredList) {

		String sqlText = "";
		String tableName = "SysConfSQLCriteria";
		List<String> confSQLList = formDao.getFromInfoLookupElementData(tableName, LookupType.NAME,
				criteria + "." + struct + "." + formCode, "ID");

		Map<String, String> confSQLMap = new HashMap<String, String>(); //formDao.getFromInfoLookupAll(tableName, LookupType.NAME, criteria+"."+struct+"."+formCode);	//My Items.Project.Main-SysConfSQLCriteria		

		for (String confSQLId : confSQLList) {
			confSQLMap = formDao.getFromInfoLookupAll(tableName, LookupType.ID, confSQLId);

			if (!generalUtil.getNull(confSQLMap.get("IGNORE")).isEmpty() && confSQLMap.get("IGNORE").equals("1")) {
				continue;
			}
			if (generalUtil.getNull(sqlText).isEmpty()) {
				sqlText = confSQLMap.get("SQLTEXT");
				if (!generalUtil.getNull(sqlText).isEmpty()) {
					Pattern p = Pattern.compile("\\$P\\{(.*?)\\}");
					Matcher m = p.matcher(sqlText);
					while (m.find()) {
						//set param val info
						String paramValAndInfo = m.group(1);
						String paramVal = "";
						String paramInfo = "";
						if (paramValAndInfo.contains(";")) {
							paramVal = paramValAndInfo.split(";")[0];
							paramInfo = paramValAndInfo.split(";")[1];
						} else {
							paramVal = paramValAndInfo;
							paramInfo = paramValAndInfo;
						}

						// replace ..
						if (!generalUtil.getNull(sqlParam.get("$P{" + paramVal + "}")).isEmpty()
								&& !generalUtil.getNull(sqlParam.get("$P{" + paramVal + "}")).startsWith("$P{")) {
							sqlText = sqlText.replace("$P{" + paramVal + "}", sqlParam.get("$P{" + paramVal + "}"));
						} else if (!generalUtil.getNull(sqlParam.get(paramVal)).isEmpty()
								&& !generalUtil.getNull(sqlParam.get(paramVal)).startsWith("$P{")) {
							sqlText = sqlText.replace("$P{" + paramVal + "}", sqlParam.get(paramVal));
						} else {
							sqlText = sqlText.replace("$P{" + paramVal + "}", "null");
							//add no criteria info
							String toInfo = "";
							try {
								toInfo = generalUtil.getSpringMessagesByKey(paramInfo, "");
								if (unfilteredList != null && !unfilteredList.contains(toInfo)) {
									unfilteredList.add(toInfo);
								}
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			} else {
				//TODO: write warn if confSQLList greater 2 items
				logger.warn(
						"The SQL criteria contains more than one item in " + criteria + "." + struct + "." + formCode);
			}
		}

		if (generalUtil.getNull(sqlText).isEmpty()) {
			//sqlText = "select t." + struct + "_ID from fg_s_" + struct + "_all_v t where 1=1";
		}
		return sqlText;
	}

}