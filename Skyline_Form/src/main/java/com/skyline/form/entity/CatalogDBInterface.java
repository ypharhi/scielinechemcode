package com.skyline.form.entity;

import java.util.Map;

import com.skyline.form.service.FormTempData;

public interface CatalogDBInterface extends CatalogInterface {

	/**
	 * 
	 * @return sql with current where part statement after filter (dataType = "meta": fast sql with no data,"row": one row (not in use),"All": sql with data according form state - "TODO dataType as enum >v9.6
	 */
	public String getSql(long stateKey, Map<String, String> formCatalogMap, FormTempData formTempDataMap,
			String isDistinct, String dataType);

	/**
	 * 
	 * @return DB table / view name
	 */
	public String getTableName();

}
