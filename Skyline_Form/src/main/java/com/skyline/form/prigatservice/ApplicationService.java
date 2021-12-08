package com.skyline.form.prigatservice;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.skyline.form.bean.BeanType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.dal.FormSaveDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.service.GeneralUtil;

@Service
public class ApplicationService {

	//	private static final Logger logger = LoggerFactory.getLogger(FormApiExcelService.class);

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private FormSaveDao formSaveDao;

	@Autowired
	private GeneralDao generalDao;

	protected NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public List<DataBean> getAppItems(String appId) {
		// TODO Auto-generated method stub
		List<DataBean> dataBeanList = new ArrayList<DataBean>();
		String sql = "select distinct to_char(t.applicationitem_id) as id, t.ApplicationItemName as name from FG_S_APPLICATIONITEM_v t where  t.APPLICATIONID = '"
				+ appId + "' and t.active = 1 order by t.applicationitem_id";
		List<Map<String, Object>> mapList = generalDao.getListOfMapsBySql(sql);
		for (Map<String, Object> map : mapList) {
			String name_ = map.get("NAME") == null?"":map.get("NAME").toString();
			dataBeanList.add(new DataBean(map.get("ID").toString(), name_, BeanType.NA,
					"appitemid as code - appitemname as value"));
		}
		return dataBeanList;
	}

	public List<DataBean> insertappitems(String appId, String formCode) {
		String newAppItemId = formSaveDao.getStructFormId(formCode);
		String newAppItemName = "";
		String userId = generalUtil.getSessionUserId();
		String sql = "insert into fg_s_applicationitem_pivot (formid,timestamp,creation_date,active,change_by,created_by,formcode_entity,formCode,applicationitemname,applicationid)\r\n"
				+ "values('" + newAppItemId + "',sysdate,sysdate,1,'" + userId + "','" + userId
				+ "','ApplicationItem','" + formCode + "','" + newAppItemName + "','" + appId + "')";
		generalDao.updateSingleStringNoTryCatch(sql);
		List<DataBean> dataBeanList = new ArrayList<DataBean>();
		dataBeanList.add(new DataBean(newAppItemId, newAppItemName, BeanType.NA,
				"new appitemid as code - appitemname as value"));
		return dataBeanList;
	}

	public void updateappitemname(String id, String val) {
		String userId = generalUtil.getSessionUserId();
		String val_ = val.replace("'", "''");
		String sql = "update fg_s_applicationitem_pivot t set t.applicationitemname = '" + val_ + "' where t.formid = '" + id + "'";
		generalDao.updateSingleStringNoTryCatch(sql);
	}

	public void deleteappitemname(String id) {
		String userId = generalUtil.getSessionUserId();
		String sql = "update fg_s_applicationitem_pivot t set t.active = 0 where t.formid = '" + id + "'";
		generalDao.updateSingleStringNoTryCatch(sql);
		
	}

}
