package com.skyline.form.dal;

import org.json.JSONObject;

public interface SearchSqlDao {

	void stopQuery();

	JSONObject getJSONObjectOfDataTable(String sql, String hideEmptyColumns, String topRowsNum, int indx);

}
