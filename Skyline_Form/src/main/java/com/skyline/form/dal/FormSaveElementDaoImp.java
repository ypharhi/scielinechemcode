package com.skyline.form.dal;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mindfusion.diagramming.Diagram;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.LookupType;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilForm;

@Repository("FormSaveElementDao")
public class FormSaveElementDaoImp implements FormSaveElementDao {
	
	private static final Logger logger = LoggerFactory.getLogger(FormSaveElementDaoImp.class);
	
	@Autowired
	private GeneralDao generalDao;
	
	@Autowired
	private GeneralUtil generalUtil;
	
	@Autowired
	private FormDao formDao;
	
	@Autowired
	private FormSaveDao formSaveDao;
	 
	@Autowired
	private GeneralUtilForm generalUtilForm;
	
	@Autowired
	private UploadFileDao uploadFileDao;
	
	@Override
	public String saveFormTestConfigData(Map<String, String> paramMonitoringValueMap,
			Map<String, String> paramMonitoringUomMap, String formId) {
		if (paramMonitoringValueMap.isEmpty()) {
			return "0";
		}
		StringBuilder using = new StringBuilder();

		for (Map.Entry<String, String> entry : paramMonitoringValueMap.entrySet()) {
			using.append("select '");
			using.append(formId + "' PARENT_ID,'");
			using.append(entry.getValue() + "' VALUE,'");
			using.append(paramMonitoringUomMap.get(entry.getKey() + "_uom") + "' UOM_ID,'");
			using.append(entry.getKey() + "' CONFIG_ID");
			using.append(" from dual union all\n");

		}

		using.delete(using.lastIndexOf(" union all"), using.lastIndexOf("l") + 1).toString();

		String sql = "merge into fg_formtest_data p using ( " + using.toString() + " ) t1"
				+ " on( p.PARENT_ID=t1.PARENT_ID and p.CONFIG_ID=t1.CONFIG_ID ) "
				+ "when not matched then insert (PARENT_ID, VALUE, UOM_ID, CONFIG_ID) values (t1.PARENT_ID, t1.VALUE, t1.UOM_ID, t1.CONFIG_ID) "
				+ "when matched then update set p.VALUE = t1.VALUE,p.UOM_ID = t1.UOM_ID";
		logger.info("savFormTestConfigData sql=" + sql);
		return String.valueOf(generalDao.updateSingleStringNoTryCatch(sql));
	}

	@Override
	public String saveFormMonitoParamData(Map<String, String> paramMonitoringValueMap,
			Map<String, String> paramMonitoringUomMap, Map<String, String> paramMonitoringFormIdMap, String formId,
			String jsonSource) {
		if (paramMonitoringValueMap.isEmpty()) {
			return "0";
		}
		StringBuilder using = new StringBuilder();

		//		String sql = "update fg_formMonitoParam_data t set t.IS_ACTIVE = '0' where t.PARENT_ID = '" + formId + "' and nvl(t.IS_ACTIVE,'1') <> '0' ";
		//String calcIdentifier = getStructFormId("MonitorparamData.CalcIdentifier");ab 30/08/18
		String calcIdentifier = formSaveDao.getStructFileId("MonitorparamData.CalcIdentifier");
		for (Map.Entry<String, String> entry : paramMonitoringValueMap.entrySet()) {
			using.append("select '");
			using.append(formId + "' PARENT_ID,'");
			using.append(entry.getKey() + "' NAME,'");
			using.append(entry.getValue() + "' VALUE,'");
			using.append(paramMonitoringUomMap.get(entry.getKey() + "_uom_id") + "' UOM_ID,'");
			using.append(paramMonitoringFormIdMap.get(entry.getKey()) + "' CONFIG_ID, TO_CLOB('");
			using.append(jsonSource + "') JSON_SOURCE, '" + calcIdentifier + "' CALCIDENTIFIER");

			using.append(" from dual union all\n");

		}

		using.delete(using.lastIndexOf(" union all"), using.lastIndexOf("l") + 1).toString();

		String sql = "insert into fg_formMonitoParam_data (PARENT_ID,NAME,VALUE,UOM_ID,CONFIG_ID,JSON_SOURCE,CALCIDENTIFIER) "
				+ using.toString();
		generalDao.updateSingleStringNoTryCatch(sql);
		return calcIdentifier;
	}

	@Override
	public String saveDynamicParams(String data) {
		if (generalUtil.getEmpty(data, "").equals("")) {
			return "0";
		}
		StringBuilder using = new StringBuilder();
		JSONObject row, dataJSONObject = new JSONObject(data);
		Iterator<?> keys = dataJSONObject.keys();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			if (dataJSONObject.get(key) instanceof JSONObject) {
				row = (JSONObject) dataJSONObject.get(key);

				using.append("select '");
				using.append(key + "' ORDER_,'");
				using.append(row.getString("label") + "' LABEL,'");
				using.append(row.getString("parentId") + "' PARENT_ID,'");
				using.append(row.getString("active") + "' ACTIVE");
				using.append(" from dual union all\n");
			}
		}
		using.delete(using.lastIndexOf(" union all"), using.lastIndexOf("l") + 1).toString();

		String sql = "merge into FG_DYNAMICPARAMS p using ( " + using.toString() + " ) t1"
				+ " on( p.PARENT_ID=t1.PARENT_ID and p.ORDER_=t1.ORDER_ ) "
				+ "when not matched then insert (ORDER_, LABEL, PARENT_ID, ACTIVE) values (t1.ORDER_, t1.LABEL, t1.PARENT_ID, t1.ACTIVE) "
				+ "when matched then update set  p.LABEL = t1.LABEL, p.ACTIVE = t1.ACTIVE";
		logger.info("saveDynamicParams sql=" + sql);
		return String.valueOf(generalDao.updateSingleStringNoTryCatch(sql));
	}
	
	@Override
	public String addMonitorParam(String formId, String formCode) {
		String count = generalDao.selectSingleStringNoException("select count(*) from fg_s_ParamMonitoring_pivot where 1=1 and PARENTID='"+formId+"'");
		int r_ = 0;
		if(!generalUtil.getNull(count).equals("1")){
		String newformId = formSaveDao.getStructFormId("ParamMonitoring");
		String userId = generalUtil.getSessionUserId();
		String sql = "insert into fg_s_ParamMonitoring_pivot "
				+ "(formid, Timestamp,Change_By,active,Formcode,parammonitoringname,parentid,formcode_entity,created_by,creation_date)"
				+ " values('" + newformId + "',sysdate,'" + userId + "','1','ParamMonitoring','NA','" + formId
				+ "','ParamMonitoring','" + userId + "',sysdate)";
		r_ = generalDao.updateSingleStringNoTryCatch(sql);
		}
		return String.valueOf(r_);
		//		String insert = insertStructTableByFormId(sql, "FG_S_PARAMMONITORING_PIVOT", newformId);
		//		return insert;
	}
	
	@Override
	public String saveMonitoringParam(String formId, String newVal, String name, String oldVal, boolean isUom) {
		String update = "";
		String sql = "";
		//String whereOldVal = "";
		/*if (oldVal.isEmpty()) {
			whereOldVal = " is null ";
		}
		else
		{
			whereOldVal = " = '" + oldVal + "'";
		}*/
		/*String canBeChanged = generalDao.selectSingleString("select 1 from FG_FORMMONITOPARAM_DATA where parent_id = "+ formId 
				+" and name = '" + name + "' and "+ columnName + whereOldVal);
		if(canBeChanged.isEmpty()){
			return "-1";
		}*/

		sql = "select parammonitoringobj from Fg_s_Parammonitoring_Pivot where formid = '" + formId
				+ "' and sessionid is null and active = 1";
		String jsonStr = generalDao.selectSingleString(sql);
		if (generalUtil.getNull(jsonStr).isEmpty() || !generalUtil.getNull(jsonStr).contains("\"" + name + "\":")) {
			JSONObject json;
			if (!generalUtil.getNull(jsonStr).isEmpty()) {
				json = new JSONObject(jsonStr);
			} else {
				json = new JSONObject();
			}
			List<Map<String, String>> mpListMap = generalUtilForm.getCurrrentNameInfoAllContainsName("MP", name);
			String defaultUom = generalUtil.getNull(mpListMap.get(0).get("DEFAULT_UOM_OBJ"), "");
			JSONObject js = new JSONObject();
			JSONObject js_uom = new JSONObject();
			js.put("val", "");
			String formid = formDao.getFromInfoLookup("mp", LookupType.NAME, name, "id");
			js.put("formid", formid);
			js_uom.put("val", generalUtil.getJsonValById(defaultUom, "ID"));
			js_uom.put("text", generalUtil.getJsonValById(defaultUom, "VAL"));
			json.put(name, js);
			json.put(name + "_uom", js_uom);
			jsonStr = json.toString();
		}
		if (isUom) {
			name = name + "_uom";
		}

		//JSONObject json = new JSONObject(jsonStr);
		String jsonVal = "";// generalUtil.getJsonValById(jsonStr, name);
		String newJson = "";//generalUtil.updateJsonValById(jsonVal, "val", newVal);
		if (isUom) {
			String text = formDao.getFromInfoLookup("uom", LookupType.ID, newVal, "name");
			jsonVal = generalUtil.getJsonValById(jsonStr, name);
			newJson = generalUtil.updateJsonValById(jsonVal, "val", newVal);
			newJson = generalUtil.updateJsonValById(newJson, "text", text);
		} else {
			jsonVal = generalUtil.getJsonValById(jsonStr, name);
			newJson = generalUtil.updateJsonValById(jsonVal, "val", newVal);
		}
		JSONObject jsonObject = new JSONObject(jsonStr);
		String updatedJson = "";
		if (!(jsonObject.isNull(name))) {
			jsonObject.put(name, new JSONObject(newJson));
			updatedJson = jsonObject.toString();
		}
		//update Fg_s_Parammonitoring_Pivot
		sql = "update Fg_s_Parammonitoring_Pivot set parammonitoringobj = '" + updatedJson + "' where formId = '"
				+ formId + "' and sessionid is null and active = 1";
		update = formSaveDao.updateStructTableByFormId(sql, "Fg_s_Parammonitoring_Pivot",
				Arrays.asList("parammonitoringobj"), formId);
		//update FG_FORMMONITOPARAM_DATA by parent_id & name
		/*sql = "update FG_FORMMONITOPARAM_DATA set " + columnName + " = '" + newVal + "' where parent_id = '" + formId + "' and name ='" + name +"'";
		update = formSaveDao.updateSingleString(sql);
		
		//update FG_FORMMONITOPARAM_DATA (json_sourc) by parent_id
		sql = "update FG_FORMMONITOPARAM_DATA set json_source = '"
				+ updatedJson + "' where parent_id = '" + formId + "'";
		update = formSaveDao.updateSingleString(sql);*/
		return update;

	} 
	
	@Override
	public String saveRichText(String formCode, DataBean dataBean, boolean isTransactional) {
		//String elementId = formSaveDao.getStructFormId(formCode + "." + dataBean.getCode());//ab 30/08/18
		String elementId = formSaveDao.getStructFileId(formCode + "." + dataBean.getCode());//always get new elementID
		String clobString = generalUtil.getJsonValById(dataBean.getVal(), "value");
		String plainText = generalUtil.getJsonValById(dataBean.getVal(), "plainText");

		String toReturn = uploadFileDao.saveRichText(elementId, clobString, plainText, isTransactional);
		if (toReturn.equals("-1")) {
			return "-1";
		}
		return elementId;
	}
	
	@Override
	public String saveDiagram(String formCode, DataBean dataBean,String elementId) throws Exception {
		String clobString = generalUtil.getJsonValById(dataBean.getVal(), "value");
		//String toReturn = uploadFileDao.saveStringAsClob(elementId, clobString);

		PreparedStatement prStmt = null;
		Connection con = null;
		String sql = "";
		Map<String, String> diagramInfo = new HashMap<String, String>();

		try {
			con = generalDao.getConnectionFromDataSurce();
			Diagram diagram = new Diagram();
			diagram.loadFromJson(generalUtil.getJsonValById(dataBean.getVal(), "value"));
			BufferedImage image = diagram.createImage();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
			baos.flush();
			byte[] imageBytes = baos.toByteArray();
			
			String image_id = formSaveDao.getStructFileId("DIAGRAM_IMAGE.png");
			uploadFileDao.saveDiagramImage(imageBytes, "DIAGRAM_IMAGE.png", image_id);
			baos.close();
			sql = "insert into fg_diagram (element_id, element_name, content, content_type, image_id) "
					+ " values(?,?,?,?,?)";

			prStmt = con.prepareStatement(sql);
			diagramInfo.put("(1) elementID = element_id ", elementId);
			prStmt.setString(1, elementId);
			diagramInfo.put(
					"(2) "+formCode + "." + dataBean.getCode()+" = element_name",
					formCode + "." + dataBean.getCode());
			prStmt.setString(2, formCode + "." + dataBean.getCode());
			diagramInfo.put("(3) clobString = content of the diagram json", clobString);
			prStmt.setString(3, clobString);
			diagramInfo.put("(4) content_type = content_type of the element-not in use","");
			prStmt.setString(4, "");
			diagramInfo.put("(5) image_id = image_id of the diagram - referenced to fg_files",
					image_id);
			prStmt.setString(5, image_id);
			prStmt.execute();
			//			prStmt.close();
			//generalUtilLogger.logWrite(LevelType.DEBUG, "saved diagram", "", ActivitylogType.SaveEvent, marvinInfo);
		} catch (Exception e) {
			throw new Exception(generalUtil.getSpringMessagesByKey("FAILED_SAVE_DIAGRAM",
					"Save diagram failure. Please, try again or call your administrator."));
		} finally {
			try {
				if (prStmt != null) {
					prStmt.close();
				}
				/*
				 * if (con != null) { con.close(); }
				 */ //->
				generalDao.releaseConnectionFromDataSurce(con);
			} catch (Exception e) {
				/*generalUtilLogger.logWrite(LevelType.ERROR, "Exception in release recources ", "-1",
						ActivitylogType.SaveEvent, null, e);*/
			}
		}
		return elementId;
	}
}
