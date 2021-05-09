package com.skyline.form.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JsonToSql {

	@Autowired
	public GeneralUtil generalUtil;

	private static final Logger logger = LoggerFactory.getLogger(FormApiService.class);

	public String getFromFGReportUserRole(String viewName, String filterJson, String typeOfReport,
			Map<String, String> mapRenderMetaData) {
		String result = "", strWhere = "";
		JSONObject jsFieldValue;
		JSONArray jsArrColumns = new JSONArray();
		StringBuilder sbColumns = new StringBuilder();

		if (typeOfReport.equals("Simple")) {
			JSONObject jsonObj = new JSONObject(filterJson);
			try {
				jsArrColumns = jsonObj.getJSONArray("columns");
				for (int i = 0; i < jsArrColumns.length(); i++) {
					if (i > 0) {
						//						sbColumns.append(", t.\""+jsArrColumns.get(i)+"\"");
						sbColumns.append(", \"" + jsArrColumns.get(i) + "\""); //Add t. if it need : ", t." instead ", "
					} else {
						//						sbColumns.append(" t.\""+jsArrColumns.get(i)+"\"");
						sbColumns.append(" \"" + jsArrColumns.get(i) + "\""); //Add t. if it need : " t." instead " "
					}
				}
				if (!jsonObj.getString("filters").equals("")) {
					strWhere = jsonObj.getString("filters");
					if (strWhere.contains("!= \"\"")) {
						strWhere = strWhere.replace("!= \"\"", "is not null");
					}
					if (strWhere.contains("!= \"null\"")) {
						strWhere = strWhere.replace("!= \"null\"", "is not null");
					}
					if (strWhere.contains("= \"\"")) {
						strWhere = strWhere.replace("= \"\"", "is null");
					}
					if (strWhere.contains("= \"null\"")) {
						strWhere = strWhere.replace("= \"null\"", "is null");
					}

					strWhere = " and " + normaliseWherePart(strWhere, mapRenderMetaData);
					; // Important: complete this by adding "t." to all fields in condition, not only at first like now
					strWhere = strWhere.replaceAll("\"", "'");
				}
				//				TEMP Comment: Uncomment next row after creating real views with real columns
				result = "select distinct" + sbColumns + " from " + viewName + " t where 1=1" + strWhere + " order by "
						+ sbColumns;
				//				result = "select distinct * from " + viewName + " where 1=1"+strWhere;  // Important: complete this by adding "t." to all fields in condition, not only at first like now

			} catch (Exception e) {
				logger.error("JsonToSql getFromFGReportUserRole Exception! " + e);
				//				System.out.println(e);
			}
		} else if (typeOfReport.equals("Custom")) {
			String[] customSectionValues = new String[1];
			StringBuilder fieldInWhere = new StringBuilder();

			customSectionValues[0] = generalUtil.getJsonValById(filterJson, "customSections");

			JSONArray jsonArr = new JSONArray(customSectionValues[0]);

			fieldInWhere.append("select distinct t.* from " + viewName + " t where 1=1");
			for (int i = 0; i < jsonArr.length(); i++) {
				jsFieldValue = jsonArr.getJSONObject(i);

				if (!jsFieldValue.getString("value").equals("")) {
					if (mapRenderMetaData.get(jsFieldValue.getString("field")).equals("DATE-RANGE")) //use this row after solve problem with DATE type...
					{
						if (jsFieldValue.getString("value").split("-").length > 1) {
							fieldInWhere.append(" and to_date(t.\"" + jsFieldValue.getString("field")
									+ "\",'dd/MM/yyyy')  >= to_date('"
									+ jsFieldValue.getString("value").split("-")[0].trim() + "','dd/MM/yyyy')");
							fieldInWhere.append(" and to_date(t.\"" + jsFieldValue.getString("field")
									+ "\",'dd/MM/yyyy')  <= to_date('"
									+ jsFieldValue.getString("value").split("-")[1].trim() + "','dd/MM/yyyy')");
						} else {
							fieldInWhere.append(" and to_date(t.\"" + jsFieldValue.getString("field")
									+ "\",'dd/MM/yyyy')  = to_date('"
									+ jsFieldValue.getString("value").split("-")[0].trim() + "','dd/MM/yyyy')");
						}

					} else if (mapRenderMetaData.get(jsFieldValue.getString("field")).equals("MATERIAL")) //use this row after solve problem with DATE type...
					{
						String field = jsFieldValue.getString("field"), value;
						//						int length = jsFieldValue.getJSONObject("value").length();
						int length = jsFieldValue.getJSONArray("value").length();
						if (length > 0) {
							fieldInWhere.append(" and (");
							for (int j = 0; j < length; j++) {
								try {
									//									value = jsFieldValue.getJSONObject("value").getJSONObject(jsFieldValue.getJSONObject("value").names().getString(j)).get("value").toString();
									value = jsFieldValue.getJSONArray("value").getString(j);
									if ((length > 1) && (j != length - 1)) {
										//jsFieldValue.getJSONObject("value").getJSONObject(String.valueOf(j)).get("value");
										fieldInWhere.append("\"" + field + "\" = '" + value + "' or ");
									} else {
										fieldInWhere.append("\"" + field + "\" = '" + value + "'");
									}
									//							fieldInWhere.append(" and to_date(t.\"" + jsFieldValue.getString("field") + "\",'dd/MM/yyyy')  <= to_date('" + jsFieldValue.getString("value").split("-")[1].trim() + "','dd/MM/yyyy')");
								} catch (Exception e) {
									logger.error("JsonToSql getFromFGReportUserRole Exception! " + e);
								}
							}
							fieldInWhere.append(")");
						}
					} else {
						fieldInWhere.append(" and t.\"" + jsFieldValue.getString("field") + "\" = '"
								+ jsFieldValue.getString("value") + "'");//.getString("field") + jsonArr.getString(0)/*getString("value")*/;
					}
				}
			}
			result = fieldInWhere.toString();
		}
		return result;
	}

	public static String normaliseWherePart(String strWhere, Map<String, String> mapRenderMetaData) // This method add to_date to fields with Date type and make case insensitive varchar2 fields
	{
		Object[] dateFieldsArray;
		boolean betweenMatchFound = false;

		// yp 15082019 - DATES BETWEEN / NOT BETWEEN WORK ARROUND (1) - change AND TO AND_DATE for between expressions
		String strWhereCopy = strWhere;
		Pattern pattern = Pattern.compile("BETWEEN\\s+\"\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d\"\\s+AND"); // for example BETWEEN "2019-08-01 00:00:00" AND 
		Matcher matcher = pattern.matcher(strWhereCopy);
		while (matcher.find()) {
			String found_ = matcher.group(0);
			strWhere = strWhere.replace(found_, found_ + "_DATE");
			betweenMatchFound = true;
		}

		List<String> listWhere = Arrays.asList(strWhere.split(" "));
		String s;
		int dataSubString = 0, secondDoubleQuotes;
		String[] fieldType = { "DATE", "VARCHAR2" };
		for (int j = 0; j < fieldType.length; j++) {
			try {
				if (betweenMatchFound) {
					mapRenderMetaData.put("AND_DATE", "DATE"); // yp 15082019 - DATES BETWEEN / NOT BETWEEN WORK ARROUND (2) - make the "AND_DATE" word to be "treated" as a field  (by add it to mapRenderMetaData) => the expression that comes after this will be parsed as date
				}
				dateFieldsArray = getKeyFromValue(mapRenderMetaData, fieldType[j]);
				if (dateFieldsArray.length > 0) {
					for (int i = 0; i < dateFieldsArray.length; i++) {
						if (listWhere.contains(dateFieldsArray[i].toString())) {
							dataSubString = strWhere.indexOf(dateFieldsArray[i].toString());
							secondDoubleQuotes = strWhere.indexOf('\"', dataSubString) + 1;
							s = strWhere.substring(strWhere.indexOf('\"', dataSubString),
									strWhere.indexOf('\"', secondDoubleQuotes) + 1).trim();
							if (fieldType[j].equals(fieldType[0])) //add "to_date" and make trunc if field type is DATE 
							{
								if (betweenMatchFound && dateFieldsArray[i].equals("AND_DATE")) { // yp 15082019 - DATES BETWEEN / NOT BETWEEN WORK ARROUND (3) - change back the AND_DATE to AND
									strWhere = strWhere.replace(dateFieldsArray[i].toString(), "AND");
								} else {
									strWhere = strWhere.replace(dateFieldsArray[i].toString(),
											" trunc(" + dateFieldsArray[i].toString() + ") ");
								}
								strWhere = strWhere.replace(s, " trunc(to_date( " + s + ",'yyyy-MM-dd HH24:MI:SS')) ");
							} else if (fieldType[j].equals(fieldType[1])) //add "lower" for case insensitive if field type is VARCHAR2
							{
								strWhere = strWhere.replace(s, " lower( " + s + " ) ");
								strWhere = strWhere.replace(dateFieldsArray[i].toString(),
										" lower( " + dateFieldsArray[i].toString() + " ) ");
							}
						}
					}
				}
			} catch (Exception e) {
				logger.error("JsonToSql normaliseWherePart (" + fieldType[j] + " part) Exception! " + e);
			}
		}
		return strWhere;
	}

	public static Object[] getKeyFromValue(Map hm, Object value) {
		ArrayList<Object> al = new ArrayList<>();
		for (Object o : hm.keySet()) {
			if (hm.get(o).equals(value)) {
				al.add(o);
			}
		}
		if (al.size() > 0) {
			return al.toArray();
		}
		return null;
	}
}