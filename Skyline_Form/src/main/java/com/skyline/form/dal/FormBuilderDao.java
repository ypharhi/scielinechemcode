package com.skyline.form.dal;

import java.util.List;
import java.util.Map;

import com.skyline.form.bean.ElementInfoAuditTrailMeta;
import com.skyline.form.bean.Form;
import com.skyline.form.bean.FormEntity;
import com.skyline.form.bean.InfData;

public interface FormBuilderDao {

	public List<FormEntity> getFormEntity(String formCode, String type);

	/**
	 * Important!!! This implementation must update StateManager on bean change
	 * @param formEntity
	 * @return
	 */
	public String newFormEntity(FormEntity formEntity);

	public String deleteFormEntity(String formCode, String entityImpCode);

	public String newForm(String formCode, String description, String active, String form_type, String title,
			String subtitle, String useAsTemplate, String order, String groupName, String formCodeEntity,
			String ignoreNav, String useCache);

	public List<Form> getForm(String formCode, String formType, boolean includeInactiveForms);

	public List<FormEntity> getFormEntityByView(String formCode, String type);

	public String createLike(String newFormCode, String oldFormCode);

	public String updateCatalogForNewForm(String formCode, String formType);

	public String canDeleteFormEntity(String formCode, String formEntity);

	public String getFormEntityInit(String formCode, String entityCode, String type);

	public List<InfData> getFromInfDataList(String formCode);

	public void createBookmarks(String formCode, String bookmarkPrefix, String noEntityimpcodeList_in);

	void updateElementInfoAuditTrailMetaTmpTable(Map<String, ElementInfoAuditTrailMeta> formElementDisplayMap);
}
