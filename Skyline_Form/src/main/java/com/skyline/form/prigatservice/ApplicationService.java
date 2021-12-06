package com.skyline.form.prigatservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.skyline.form.bean.BeanType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.FormSaveDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.dal.UploadFileDao;
import com.skyline.form.service.FormApiExcelService;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.IntegrationEvent;
import com.skyline.form.service.IntegrationValidation;

@Service
public class ApplicationService {
	
	private static final Logger logger = LoggerFactory.getLogger(FormApiExcelService.class);

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private IntegrationEvent integrationEvent;

	@Autowired
	private IntegrationValidation integrationValidation;

	@Autowired
	private FormDao formDao;

	@Autowired
	private FormSaveDao formSaveDao;

	@Autowired
	private UploadFileDao uploadFileDao;

	@Autowired
	private GeneralDao generalDao;

	protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public List<DataBean> getAppItems(String appId) {
		// TODO Auto-generated method stub
		List<DataBean> dataBeanList = new ArrayList<DataBean>();
		String sql = "select distinct to_char(t.applicationitem_id) as id, t.ApplicationItemName as name from FG_S_APPLICATIONITEM_v t where t.active = 1";
		List<Map<String, Object>> mapList = generalDao.getListOfMapsBySql(sql);
		for (Map<String, Object> map : mapList) {
			dataBeanList.add(new DataBean(map.get("ID").toString(), map.get("NAME").toString(), BeanType.NA, "appitemid as code - appitemname as value"));
		}
		return dataBeanList;
	}

	public List<DataBean> insertappitems(String appId, String formCode) {
		String newAppItemId = formSaveDao.getStructFormId(formCode);
		String newAppItemName = "[" + formSaveDao.getStructFormId(formCode) + "]";
		String userId = generalUtil.getSessionUserId();
		String sql = "insert into fg_s_applicationitem_pivot (formid,timestamp,creation_date,active,change_by,created_by,formcode_entity,formCode,applicationitemname,applicationid)\r\n" + 
				"values('" + newAppItemId + "',sysdate,sysdate,1,'" + userId + "','" + userId + "','ApplicationItem','" + formCode + "','"+ newAppItemName + "','" + appId + "')";
		generalDao.updateSingleStringNoTryCatch(sql);
		List<DataBean> dataBeanList = new ArrayList<DataBean>();
		dataBeanList.add(new DataBean(newAppItemId,newAppItemName, BeanType.NA, "new appitemid as code - appitemname as value"));
		return dataBeanList;
	}

}

 

	
