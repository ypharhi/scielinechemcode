package com.skyline.customer.adama;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.BeanType;
import com.skyline.form.bean.DataBean;
import com.skyline.form.bean.Form;
import com.skyline.form.bean.LevelType;
import com.skyline.form.bean.LookupType;
import com.skyline.form.bean.Result;
import com.skyline.form.bean.ValidationCode;
import com.skyline.form.bean.WebixOutput;
import com.skyline.form.dal.ChemDao;
import com.skyline.form.dal.FormDao;
import com.skyline.form.dal.FormSaveDao;
import com.skyline.form.dal.FormSaveElementDao;
import com.skyline.form.dal.GeneralDao;
import com.skyline.form.dal.SearchSqlDao;
import com.skyline.form.dal.UploadFileDao;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilConfig;
import com.skyline.form.service.GeneralUtilDesignData;
import com.skyline.form.service.GeneralUtilFavorite;
import com.skyline.form.service.GeneralUtilForm;
import com.skyline.form.service.GeneralUtilFormState;
import com.skyline.form.service.GeneralUtilLogger;
import com.skyline.form.service.GeneralUtilNotificationEvent;
import com.skyline.form.service.GeneralUtilPermission;
import com.skyline.form.service.IntegrationCalc;
import com.skyline.form.service.IntegrationDT;
import com.skyline.form.service.IntegrationEvent;
import com.skyline.form.service.IntegrationValidation;
import com.skyline.form.service.SpringNameDictionary;

import chemaxon.calculations.clean.Cleaner;
import chemaxon.formats.MolExporter;
import chemaxon.formats.MolImporter;
import chemaxon.struc.Molecule;
import chemaxon.struc.RxnMolecule;
import jasper.biz.JasperDataSourceSupplier;
import jasper.biz.JasperReportGenerator;
import jasper.biz.JasperReportType;
import jasper.biz.JasperTemplateFactory;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRResultSetDataSource;

@Service
public class IntegrationEventAdamaImp implements IntegrationEvent {

	@Autowired
	private CommonFunc commonFunc;

	@Autowired
	private FormIdCalc formIdCalc;

	@Autowired
	private FormDao formDao;

	@Autowired
	private GeneralUtil generalUtil;

	@Autowired
	private GeneralUtilConfig generalUtilConfig;
	
	@Autowired
	private GeneralUtilDesignData  generalUtilDesignData;

	@Autowired
	private GeneralUtilForm generalUtilForm;

	@Autowired
	private GeneralUtilFormState generalUtilFormState;

	@Autowired
	private GeneralDao generalDao;

	@Autowired
	private FormSaveDao formSaveDao;

	@Autowired
	private FormSaveElementDao formSaveElementDao;

	@Autowired
	private IntegrationWFAdamaImp integrationWFAdamaImp;

	@Autowired
	private GeneralUtilNotificationEvent generalUtilNotificationEvent;

	@Autowired
	protected UploadFileDao uploadFileDao;

	@Autowired
	private IntegrationCalc integrationCalc;

	@Autowired
	private IntegrationValidation integrationValidation;

	@Autowired
	protected GeneralUtilLogger generalUtilLogger;
	
	@Autowired
	private GeneralUtilFavorite generalUtilFavorite;

	@Autowired
	private ChemDao chemDao;
	
	@Autowired
	private GeneralUtilPermission generalUtilPermission;
	
	@Autowired
	public IntegrationDT integrationDTAdamaImp;

	@Value("${removeEmptyStructOnSinchronizashen:0}")
	private String removeEmptyStructOnSinchronizashen;

	// Note: the search type in the reaction can be and defined in chem.searchType prop (smiles[default]/inchi/mol[has a problem because of the location coordinates that effects the matrix - don't use it]) that are already exists in the tables we look at as columns
	@Value("${chem.searchType:smiles}")
	private String chemSearchType; //"smiles" 

	@Value("${jdbc.url}")
	private String DB_URL;

	@Value("${jdbc.username}")
	private String DB_USER;

	@Value("${jdbc.password}")
	private String DB_PASSWORD;

	@Value("${ireportPath}")
	private String DIR_JASPER_XML;
	
	@Autowired
	private CloneExperiment cloneExperiment;

	private final int startColumnIndexActions = 5;
	private final int startColumnIndexMaterial = 3;
	private final int startColumnIndexParameters = 6;
	private final int startColumnIndexSummary = 2;

	private static final Logger logger = LoggerFactory.getLogger(IntegrationEventAdamaImp.class);

	private ScriptEngine engine;

	@Autowired
	private SearchSqlDao searchSqlDao;
	
	@Autowired
	private SpringNameDictionary springNameDictionary;

	private String parametersString;

	public IntegrationEventAdamaImp() {
		ScriptEngineManager factory = new ScriptEngineManager();
		this.engine = factory.getEngineByName("js");
	}

	public Map<String, String> getFormPreSaveCalcGeneral(String formCode, Map<String, String> elementValueMap) {
		//		Map<String, String> calcMap = new HashMap<String, String>();
		List<String> calcConfigIdList = formDao.getFromInfoLookupElementData("SysConfPreSaveCalc", LookupType.NAME,
				formCode, "ID");
		for (String calcId : calcConfigIdList) {
			Map<String, String> calcConfigMap = formDao.getFromInfoLookupAll("SysConfPreSaveCalc", LookupType.ID,
					calcId);

			String formula = calcConfigMap.get("FORMULAORFUNCTION").toUpperCase();
			for (Integer i = 1; i < 6; i++) {
				formula = formula.replaceAll("ARG" + i, elementValueMap.get(calcConfigMap.get("ARG" + i)));

			}
			Object result = null;
			//			ScriptEngineManager manager = new ScriptEngineManager();
			//			ScriptEngine engine = manager.getEngineByName("js");

			try {
				result = engine.eval(formula);
			} catch (javax.script.ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			elementValueMap.put(elementValueMap.get(calcConfigMap.get("RESULTELEMENT")), result.toString());

		}
		return elementValueMap;
	}

	@Override
	public String chemDoodleReactionTabUp(String reactionMrv, String parentId, String formCode)
			throws ParserConfigurationException, SAXException, IOException, TransformerException {
		RxnMolecule mol = new RxnMolecule();
		String wherePartTmpData = generalUtilFormState.getWherePartForTmpData(formCode, parentId);
		List<String> smilesReactant = generalDao
				.getListOfStringBySql("select  t.smiles from fg_s_materialref_all_v t where t.PARENTID = '" + parentId
						+ "' and t.ACTIVE = '1' and t.TABLETYPE = 'Reactant'" + wherePartTmpData);
		/*
		 * List<String> smilesReactant = generalDao.getListOfStringBySql("select m.SMILES from fg_s_invitemmaterial_all_v m where m.INVITEMMATERIAL_ID in (select  t.INVITEMMATERIAL_ID from fg_s_materialref_v t where t.PARENTID = '"
		 * + parentId + "' and t.ACTIVE = '1' and t.TABLETYPE = 'Reactant'" + wherePartTmpData +")" );//" + wherePartTmpData
		 */
		for (String smile : smilesReactant) {
			if (smile != null && !smile.equals("NA")) {
				Molecule reactant1 = MolImporter.importMol(smile);
				mol.addComponent(reactant1, RxnMolecule.REACTANTS);
			}
		}
		List<String> smilesProduct = generalDao
				.getListOfStringBySql("select  t.smiles from fg_s_materialref_all_v t where t.PARENTID = '" + parentId
						+ "' and t.ACTIVE = '1' and t.TABLETYPE = 'Product'" + wherePartTmpData);
		/*
		 * List<String> smilesProduct = generalDao.getListOfStringBySql("select m.SMILES from fg_s_invitemmaterial_all_v m where m.INVITEMMATERIAL_ID in (select  t.INVITEMMATERIAL_ID from fg_s_materialref_v t where t.PARENTID = '"
		 * + parentId + "' and t.ACTIVE = '1' and t.TABLETYPE = 'Product'" + wherePartTmpData +")" );//" + wherePartTmpData );
		 */
		for (String smile : smilesProduct) {
			if (smile != null && !smile.equals("NA")) {
				Molecule product = MolImporter.importMol(smile);
				mol.addComponent(product, RxnMolecule.PRODUCTS);
			}
		}

		Cleaner.clean(mol, 2, null);
		String xml = (MolExporter.exportToFormat(mol, "mrv:P"));
		//String smiles = (MolExporter.exportToFormat(mol, "smiles"));

		///String cml_copy_link = formSaveDao.getStructFormId("CML_COPY_LINK");ab 30/08/18
		//String cml_copy_link = formSaveDao.getStructFileId("CML_COPY_LINK");
		//uploadFileDao.saveStringAsClobNewConnection(cml_copy_link, xml);

		return xml;
	}

	public String chemDoodleCanvasUpdateData(String ID) {
		String toReturn = "";
		if (!ID.equals("-1")) {
			toReturn = chemDao.getMRVContent(ID);
		}
		return toReturn;
	}

	@Override
	public String chemDoodleReactionTabEvent(String reactionMrv, String parentId, String formCode)
			throws Exception {
		String wherePartTmpData = generalUtilFormState.getWherePartForTmpData(formCode, parentId);
		String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, parentId);
		String searchAttrName = chemSearchType;

		Document xml = chemDao.convertStringToXml(reactionMrv);

		HashMap<Integer, ArrayList<HashMap<String, Object>>> reactionMolecules = new HashMap<Integer, ArrayList<HashMap<String, Object>>>();

		// Reactants
		reactionMolecules.put(1, chemDao.getReactionMolecules("reactantList", xml));
		// Agents
		reactionMolecules.put(2, chemDao.getReactionMolecules("agentList", xml));
		// Products
		reactionMolecules.put(3, chemDao.getReactionMolecules("productList", xml));

		HashMap<String, ArrayList<String>> reactionSmiles = new HashMap<String, ArrayList<String>>();
		ArrayList<JSONObject> materialsArray = new ArrayList<JSONObject>();

		reactionSmiles.put("Reactant", new ArrayList<String>());
		reactionSmiles.put("Agent", new ArrayList<String>());
		reactionSmiles.put("Product", new ArrayList<String>());

		for (Integer key : reactionMolecules.keySet()) {
			switch (key) {
				// reactants
				case 1:
					for (HashMap<String, Object> formats : reactionMolecules.get(key)) {
						addAttrToDataStructures("Reactant", reactionSmiles, materialsArray, formats, searchAttrName);
					}
					break;
				// agents
				case 2:
					for (HashMap<String, Object> formats : reactionMolecules.get(key)) {
						addAttrToDataStructures("Agent", reactionSmiles, materialsArray, formats, searchAttrName);
					}
					break;
				// products
				case 3:
					for (HashMap<String, Object> formats : reactionMolecules.get(key)) {
						addAttrToDataStructures("Product", reactionSmiles, materialsArray, formats, searchAttrName);
					}
					break;

				default:
					break;
			}
		}

		String count = null;
		
		if(materialsArray == null || materialsArray.size() == 0) {
			throw new Exception(generalUtil.getSpringMessagesByKey("NO_REACTION_DATA_IN_SCHEM"));
		}

		for (int i = 0; i < materialsArray.size(); i++) {
			JSONObject matObj = materialsArray.get(i);
			String searchVal = (String) matObj.get("search");
			if (!searchVal.equals("")) {
				count = generalDao.selectSingleString("select count(*) from FG_S_INVITEMMATERIAL_ALL_V t where t."
						+ searchAttrName + " = '" + searchVal + "' and t.ACTIVE = '1'");
				if (count.equals("")) {
					matObj.put("result", "-1");
					return matObj.toString();
				}
				if (Integer.valueOf(count) < 1) {
					matObj.put("result", "noMaterialFound");
					String cml_ = "<cml><MDocument><MChemicalStruct>" + (String) matObj.get("cml")
							+ "</MChemicalStruct></MDocument></cml>";
					//String cml_copy_link = formSaveDao.getStructFormId("CML_COPY_LINK");
					String cml_copy_link = formSaveDao.getStructFileId("CML_COPY_LINK");
					uploadFileDao.saveStringAsClobNewConnection(cml_copy_link, cml_);
					matObj.put("cml_copy_link", cml_copy_link);
					return matObj.toString();
				}
				if (Integer.valueOf(count) > 1) {
					matObj.put("result", "noUniqueValue");
					return matObj.toString();
				}
			}
		}

		Map<String, String> mapRemoved = new HashMap<String, String>();

		int numRemoved = searchAndRemoveMaterial(reactionSmiles, parentId, mapRemoved, searchAttrName);
		JSONObject json = new JSONObject();

		String insert = "";
		Map<String, String> map = new HashMap<String, String>();
		int num = 0;
		for (String key : reactionSmiles.keySet()) {
			num += searchAndInsertMaterial(key, reactionSmiles, count, parentId, formCode, insert, wherePartTmpData,
					sessionId, searchAttrName, map);
			json.append("insertMaterial", map.get("insertMaterial"));
		}

		json.put("insertMaterial", map.get("insertMaterial"));
		json.put("result", map.get("result"));
		json.put("insert", num);
		json.put("removed", numRemoved);
		json.put("listMaterialToDelete", mapRemoved.get("deleteMaterials"));
		//return (new JSONObject().put("result", insert)).toString();
		return json.toString();
	}

	private int searchAndRemoveMaterial(HashMap<String, ArrayList<String>> reactionSmiles, String parentId,
			Map<String, String> mapRemoved, String searchAttrName) {
		String wherePartTmpData = generalUtilFormState.getWherePartForTmpData("MATERIALREF", parentId);
		String removeEmptyStruct = removeEmptyStructOnSinchronizashen;
		//		String count;
		List<String> materialId = new ArrayList<String>();

		int numOfRemovedMaterial = 0;
		// String userId = generalUtil.getSessionUserId();
		//		Map<String, String> materialMap = new HashMap<String, String>();

		for (String key : reactionSmiles.keySet()) {
			List<String> smiles = new ArrayList<String>();
			String s = "";
			for (String smile : reactionSmiles.get(key)) {
				smiles.add("'" + smile + "'");
			}
			s = generalUtil.listToCsv(smiles);

			if (!smiles.isEmpty()) {
				//s += "' '";
				List<String> materialRefIds = generalDao.getListOfStringBySql(
						"select distinct formId from FG_S_MATERIALREF_ALL_V t where t.parentid = '" + parentId
								+ "' and tabletype = '" + key + "'"
								+ (!removeEmptyStruct.equals("1") ? " and " + searchAttrName + " is not null " : "")
								+ wherePartTmpData);
				for (String materialRefId : materialRefIds) {
					String fid = generalDao.selectSingleStringNoException("select distinct t." + searchAttrName
							+ " as SMILES from FG_S_MATERIALREF_all_V t where t.formId = '" + materialRefId
							+ "'  and  t." + searchAttrName + " in (" + s + ")");
					///count = materialMap.get("COUNT");
					if (!generalUtil.getNull(fid).isEmpty()) {
						/*
						 * if (!generalUtil.getNull(materialMap.get("SMILES")).isEmpty()
						 * || (generalUtil.getNull(materialMap.get("SMILES")).isEmpty()
						 * && removeEmptyStruct.equals("1"))) {
						 */
						smiles.remove("'" + fid + "'");
						s = generalUtil.listToCsv(smiles);
					} else {
						numOfRemovedMaterial++;
						materialId.add(materialRefId);
					}
				}
			} else {//in case the drawing of the current key is empty
				List<String> materialRefIds = generalDao.getListOfStringBySql(
						"select distinct formId from FG_S_MATERIALREF_ALL_V t where t.parentid = '" + parentId
								+ "' and tabletype = '" + key + "'"
								+ (!removeEmptyStruct.equals("1") ? " and " + searchAttrName + " is not null " : "")
								+ wherePartTmpData);
				for (String materialRefId : materialRefIds) {
					numOfRemovedMaterial++;
					materialId.add(materialRefId);
				}
			}
		}
		mapRemoved.put("deleteMaterials", generalUtil.listToCsv(materialId));
		return numOfRemovedMaterial;
	}

	/*
	 * materialMap = generalDao.sqlToHashMap(
	 * String.format("select t.SMILES, t.TABLETYPE from FG_S_MATERIALREF_all_V t where t.formId =  '%1$s'",
	 * materialRefId));
	 * count = generalDao.selectSingleString(
	 * "select count(*) from fg_chem_doodle_data t where t.parent_id in (select s.CHEMDOODLEACT from fg_s_step_v s where s.CHEMDOODLEACT = t.parent_id and  t.smiles_data = '"
	 * + materialMap.get("SMILES") + "' and t.mol_type = DECODE('" + materialMap.get("TABLETYPE")
	 * + "','Product','P','Reactant','R','S') and s.formid =  '" + parentId + "' )");
	 * if (count.equals("0")) {
	 * if (!generalUtil.getNull(materialMap.get("SMILES")).isEmpty()
	 * || (generalUtil.getNull(materialMap.get("SMILES")).isEmpty()
	 * && removeEmptyStruct.equals("1"))) {
	 * materialId.add(materialRefId);
	 * //formSaveService.doRemove("MaterialRef", materialRefId, userId);
	 * numOfRemovedMaterial += 1;
	 * }
	 * }
	 * }
	 */

	private int searchAndInsertMaterial(String key, HashMap<String, ArrayList<String>> reactionSmiles, String count,
			String parentId, String formCode, String insert, String wherePartTmpData, String sessionId,
			String searchAttrName, Map<String, String> map) {
		//String formId;
		//String sql;

		JSONObject json = (map.get("insertMaterial") != null ? new JSONObject(map.get("insertMaterial"))
				: new JSONObject());
		int num = 0;
		//		String userId = generalUtil.getSessionUserId();
		for (String smile : reactionSmiles.get(key)) {
			if (!smile.equals("")) {
				count = generalDao.selectSingleString("select count(*) from FG_S_MATERIALREF_ALL_V t where t."
						+ searchAttrName + " = '" + smile + "' and t.TABLETYPE = '" + key + "' and t.parentid = '"
						+ parentId + "' and t.ACTIVE = '1' " + wherePartTmpData);
				if (count.equals("0")) {
					/*
					 * formId = formSaveDao.getStructFormId(formCode);
					 * sql = "insert into FG_S_MATERIALREF_PIVOT (FORMID,PARENTID,TIMESTAMP,active,INVITEMMATERIAL_ID,TABLETYPE,SESSIONID,CREATED_BY,CREATION_DATE)"
					 * + " select " + formId + "," + parentId + "," + "sysdate," + "'1'," + "t.INVITEMMATERIAL_ID,"
					 * + "'" + key + "', '" + sessionId + "','" + userId
					 * + "', sysdate from FG_S_INVITEMMATERIAL_ALL_V t where t." + searchAttrName + " = '" + smile
					 * + "' ";
					 * insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_MATERIALREF_PIVOT", formId);
					 */
					json.append(key, smile);
					num += 1;
				}
			}
		}
		map.put("insertMaterial", json.toString());
		//map.put("num", String.valueOf(num));
		map.put("result", insert);

		return num;
	}

	private void addAttrToDataStructures(String key, HashMap<String, ArrayList<String>> reactionSmiles,
			ArrayList<JSONObject> materialsArray, HashMap<String, Object> formats, String searchAttrName) {
		JSONObject jo = new JSONObject();
		String attrNameString = (String) formats.get(searchAttrName);
		String cml = (String) formats.get("cml");
		jo.put("search", attrNameString);
		jo.put("cml", cml);
		materialsArray.add(jo);
		reactionSmiles.get(key).add(attrNameString);
	}

	/*
	 * After Inf tabels update ,if form is caching update cach map
	 * @see com.skyline.form.service.IntegrationEvent#updateCach(java.lang.String)
	 */
	@Override
	public List<String> getUpdateCacheFormList(String formCode) {
		List<String> updateFormList = new ArrayList<String>();
		updateFormList.add(formCode);
		//		cachService.setCacheOnFormDataChange(formCode);

		//		
		//		select DISTINCT f.formcode, 'SELECT * FROM ' || T.TABLE_NAME || ';'--,T.COLUMN_NAME
		//		from user_tab_columns t, FG_FORM F
		//		where t.TABLE_NAME like 'FG_S_' || UPPER(F.FORMCODE) || '_INF_V'
		//		AND T.COLUMN_NAME NOT IN ('NAME','ID', 'FORMCODE')
		//		AND (F.USECACHE = 1)
		//	 

		if (generalUtil.getNull(formCode).equalsIgnoreCase("USER")
				|| generalUtil.getNull(formCode).equalsIgnoreCase("GROUP")
				|| generalUtil.getNull(formCode).equalsIgnoreCase("PERMISSIONOBJECT")
				|| generalUtil.getNull(formCode).equalsIgnoreCase("PERMISSIONSCHEME")) {
			//			cachService.setCacheOnFormDataChange("PermissionSRef");
			updateFormList.add("PermissionSRef");
		} else if (generalUtil.getNull(formCode).equalsIgnoreCase("UOM")) {
			updateFormList.add("MP");
		}

		return updateFormList;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public String generalEvent(long stateKey, Map<String, String> elementValueMap, String formCode, String formId,
			String userId, String isNew, String eventAction) throws Exception {

		String parentId = formId;//Here it is experimentSeries
		String reportFormat = "XLS";
		//		if (eventAction.equals("addFormulant")) 
		//		{
		//			String sql, insert = "";	
		//			String batchId=elementValueMap.get("batchId");
		//			String materialId=elementValueMap.get("materialId");
		//			
		//			formId = formSaveDao.getStructFormId("FormulantRef");
		//			sql = "insert into FG_S_FormulantRef_PIVOT (TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,FORMCODE,BATCH_ID,PARENTID,TABLETYPE,MATERIALID) VALUES (SYSDATE,'" + userId + "',' " + generalUtil.getSessionId() + "',1,'"+formId+"','FormulantRef','"+batchId+"','"+parentId+"',"
		//					+ "'expFr','"+materialId+"')";
		//			insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_FormulantRef_PIVOT", formId); //TODO Yaron - tmp data
		//			
		//		}
		//		if (eventAction.equals("addProductMixture")) 
		//		{
		//			String sql, insert = "";	
		//			String batchId=elementValueMap.get("batchId");
		//			String materialId=elementValueMap.get("materialId");
		//			
		//			formId = formSaveDao.getStructFormId("FormulantRef");
		//			sql = "insert into FG_S_FormulantRef_PIVOT (TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,FORMCODE,BATCH_ID,PARENTID,TABLETYPE,MATERIALID) VALUES (SYSDATE,'" + userId + "',' " + generalUtil.getSessionId() + "',1,'"+formId+"','FormulantRef','"+batchId+"','"+parentId+"',"
		//					+ "'productMixture','"+materialId+"')";
		//			insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_FormulantRef_PIVOT", formId); //TODO Yaron - tmp data
		//			
		//		}
		if(eventAction.equals("checkParametersStepFinished")){
			StringBuilder sbInfo = new StringBuilder();
			integrationValidation.validate(ValidationCode.CHECK_PARAMETERS_EXIST, formCode, formId, formId, sbInfo);
			if(!sbInfo.toString().isEmpty()){
				return sbInfo.toString();
			} else {
				return generalEvent(stateKey, elementValueMap, formCode, formId, userId, isNew, "changeStepStatusToFinish");
			}
		}
		if(eventAction.equals("changeStepStatusToFinish")){
			//any change here should be match to the preformsavestep finished status
			StringBuilder sbInfo = new StringBuilder();
			String expVersion = formDao.getFromInfoLookup("EXPERIMENT", LookupType.ID, formId,
					"EXPERIMENTVERSION");
			String protocol = formDao.getFromInfoLookup("EXPERIMENT", LookupType.ID, formId,
					"PROTOCOLTYPENAME");
			String finishedStatusId = formDao.getFromInfoLookup("STEPSTATUS", LookupType.NAME, "Finished",
					"id");
			String cancelledStatusId = formDao.getFromInfoLookup("STEPSTATUS", LookupType.NAME, "Cancelled",
					"id");
			List<String> stepIdList = generalDao.getListOfStringBySql("select step_id from fg_s_step_v where experiment_id = '"+formId+"' and status_id!='"+finishedStatusId+"' and status_id!='"+cancelledStatusId+"'");
			
			if(generalUtil.getNull(expVersion).isEmpty() || generalUtil.getNull(expVersion).equals("01")){
				if(protocol.equals("Organic")){
					//make consumption from all the connected batches
					String sql = "with SUM_DATA as"
							+ "(select distinct mr.BATCH_ID,"//mr.parentid as step_id,
							+ "	fg_get_uom_noraml(mr.quantityuom_id) quantity_uom,"
							+ " sum(to_number(fg_get_num_normal(mr.QUANTITY,mr.QUANTITYUOM_ID)))over(partition by mr.BATCH_ID) sum_batch"
							+ " from fg_s_materialref_all_v mr"
							+ " where mr.PARENTID in"
							+ " ("+generalUtil.listToCsv(stepIdList)+")"
							+ " and nvl(mr.ISSTANDART,0) = 0"
							+ " and MR.SESSIONID is null and nvl(MR.active,'1')='1'"
							+ " and MR.BATCH_ID is not null)"
						+ "select distinct"
						+ " sd.BATCH_ID,sd.sum_batch,sd.quantity_uom"//sd.step_id,
						+ " from SUM_DATA sd";
					List<Map<String,Object>> batchList = generalDao.getListOfMapsBySql(sql) ;

					try{
						for(Map<String, Object> batchData:batchList){
							commonFunc.postUseOfBatch(batchData.get("SUM_BATCH").toString(), batchData.get("QUANTITY_UOM").toString(), batchData.get("BATCH_ID").toString(), generalUtil.listToCsv(stepIdList), sbInfo);
						}
					}
					catch(Exception e){
						//if there was an exception, then check if it was by consumption exceeding or another one and throwing it as expected
						 sql = "with SUM_DATA as"
								+ "(select distinct mr.BATCH_ID,mr.parentid,mr.INVITEMMATERIALNAME,"
								+ " sum(to_number(fg_get_num_normal(mr.QUANTITY,mr.QUANTITYUOM_ID)))over(partition by mr.BATCH_ID) sum_batch"
								+ " from fg_s_materialref_all_v mr"
								+ " where mr.PARENTID in"
								+ " ("+generalUtil.listToCsv(stepIdList)+")"
								+ " and nvl(mr.ISSTANDART,0) = 0"
								+ " and MR.SESSIONID is null and nvl(MR.active,'1')='1'"
								+ ")"
							+ "select distinct"
							+ "'Step No.'||s.FORMNUMBERID||' > Materials:'|| listagg(sd.INVITEMMATERIALNAME,',')within group(order by sd.INVITEMMATERIALNAME)over(partition by s.step_id) step_data"
							+ " from fg_s_invitembatch_v b,"
							+ " fg_s_step_v s,"
							+ " SUM_DATA sd"
							+ " where sd.BATCH_ID = b.INVITEMBATCH_ID"
							+ " and to_number(sd.sum_batch)>to_number(fg_get_num_normal(b.QUANTITY,b.QUANTITYUOM_ID))"
							+ " and s.step_id = sd.PARENTID"
							+ " order by step_data";
						List<String> stepExceedList = generalDao.getListOfStringBySql(sql);
						String errorMessage = "";
						for(String stepMessage:stepExceedList){
							errorMessage +=stepMessage+"</br>";
						}
						if(!errorMessage.isEmpty()){//throw an error message of exceeding quantity
							integrationValidation.validate(ValidationCode.INVALID_CONSUMED_QUANTITY_COMPLETED_EXP, formCode, formId, errorMessage, sbInfo);
						} else {
							throw new Exception(e);
						}
					}
				} else {//Formulation
						//make consumption from all the connected batches
						String sql = "with SUM_DATA as"
								+ "(select distinct mr.BATCH_ID,"//mr.step_id,
								+ "	fg_get_uom_noraml(mr.result_uom_id) quantity_uom,"
								+ " sum(to_number(fg_get_num_normal(mr.result_value,mr.RESULT_UOM_ID)))over(partition by mr.BATCH_ID) sum_batch"
								+ " from fg_i_webix_output_all_v mr"
								+ " where mr.step_id in"
								+ " ("+generalUtil.listToCsv(stepIdList)+")"
								+ " and nvl(mr.ISSTANDART,0) = 0"
								+ " and MR.BATCH_ID is not null)"
							+ "select distinct"
							+ " sd.BATCH_ID,sd.sum_batch,sd.quantity_uom"
							+ " from SUM_DATA sd";
						List<Map<String,Object>> batchList = generalDao.getListOfMapsBySql(sql) ;
					try{
						for(Map<String, Object> batchData:batchList){
							commonFunc.postUseOfBatch(batchData.get("SUM_BATCH").toString(), batchData.get("QUANTITY_UOM").toString()
									, batchData.get("BATCH_ID").toString(), generalUtil.listToCsv(stepIdList), sbInfo);
						}
						/*List<Map<String, Object>> ascendingBatchInfo = generalDao.getListOfMapsBySql(
								"Select distinct BATCH_ID,RESULT_VALUE,RESULT_UOM_ID from fg_i_webix_output_all_v  where step_id = '"
										+ formId + "' and batch_id is not null and isstandart = 0");//TODO:get the batches that are not temporary under step*/
					}catch(Exception e){
						//if there was an exception, then check if it was by consumption exceeding or another one and throwing it as expected
						sql = "with SUM_DATA as"
								+ "(select distinct mr.BATCH_ID,mr.STEP_ID,mr.MATERIAL_ID,"
								+ " sum(to_number(fg_get_num_normal(mr.RESULT_VALUE,mr.RESULT_UOM_ID)))over(partition by mr.BATCH_ID) sum_batch"
								+ " from fg_i_webix_output_all_v mr"
								+ " where mr.STEP_ID in"
								+ " ("+generalUtil.listToCsv(stepIdList)+")"
								+ " and nvl(mr.ISSTANDART,0) = 0"
								+ " and MR.BATCH_ID is not null"
								+ ")"
							+ "select distinct"
							+ "'Step No.'||s.FORMNUMBERID||' > Materials:'|| listagg(b.MATERIALNAME,',')within group(order by b.MATERIALNAME)over(partition by s.step_id) step_data"
							+ " from fg_s_invitembatch_all_v b,"
							+ " fg_s_step_v s,"
							+ " SUM_DATA sd"
							+ " where sd.BATCH_ID = b.INVITEMBATCH_ID"
							+ " and to_number(sd.sum_batch)>to_number(fg_get_num_normal(b.QUANTITY,b.QUANTITYUOM_ID))"
							+ " and s.step_id = sd.STEP_ID"
							+ " order by step_data";
						List<String> stepExceedList = generalDao.getListOfStringBySql(sql);
						String errorMessage = "";
						for(String stepMessage:stepExceedList){
							errorMessage +=stepMessage+"</br>";
						}
						if(!errorMessage.isEmpty()){//throw an error message of exceeding quantity
							integrationValidation.validate(ValidationCode.INVALID_CONSUMED_QUANTITY_COMPLETED_EXP, formCode, formId, errorMessage, sbInfo);
						} else {
							throw new Exception(e);
						}
					}
				}
			}

			for(String step_id:stepIdList){
				formSaveDao.updateStructTableByFormId("update fg_s_step_pivot set finishdate = to_char(sysdate,'"+generalUtil.getConversionDateFormat()+"'),status_id = '"+finishedStatusId+"',CHANGE_BY='"+userId+"',TIMESTAMP=sysdate where formid = '"+step_id+"'"
						, "fg_s_step_pivot", Arrays.asList("finishDate","STATUS_ID","CHANGE_BY","TIMESTAMP"), step_id);
				//step not save on finished status if not all mandatory fields in self test under it were  filled
				if (protocol.equals("Formulation")) {
					integrationValidation.validate(ValidationCode.CHECK_SELEFTEST_VALIDATION, "", step_id, "", sbInfo);
				}	
				String stCompletedStatus = formDao.getFromInfoLookup("SELFTESTSTATUS", LookupType.NAME, "Completed", "id");
				String stCancelledStatus = formDao.getFromInfoLookup("SELFTESTSTATUS", LookupType.NAME, "Cancelled", "id");
				formSaveDao.updateStructTable("update fg_s_selftest_pivot t set t.status_id ='" + stCompletedStatus + "',CHANGE_BY='"+userId+"',TIMESTAMP=sysdate where  t.action_id in (select a.action_id from fg_s_action_v a where a.STEP_ID ='" + step_id + "') and t.status_id <> '" + stCancelledStatus + "'", "FG_S_SELFTEST_PIVOT",  Arrays.asList("STATUS_ID","CHANGE_BY","TIMESTAMP"), "ACTION_ID","(select a.action_id from fg_s_action_v a where a.STEP_ID ='" + formId + "')");
			}
		}
		if (eventAction.equals("addNewDocument")) {
			
			String uploadFileId = elementValueMap.get("elementId");
			String sessionId = (formCode.equals("SelfTest"))?"'"+generalUtilFormState.checkAndReturnSessionId("Document", formId)+"'":"NULL";
			parentId = elementValueMap.get("parentId");
			String docTableType = elementValueMap.get("docTableType");
			String documentId = formSaveDao.getStructFormId("Document");
			String sql = "insert into fg_s_document_pivot t (t.FORMID,t.TIMESTAMP,t.CHANGE_BY,t.CREATION_DATE,t.CREATED_BY,t.SESSIONID,t.ACTIVE,t.FORMCODE,t.FORMCODE_ENTITY, PARENTID, t.TABLETYPE, t.LINK_ATTACHMENT, t.DOCUMENTUPLOAD, t.DOCUMENTNAME, t.DESCRIPTION, t.SAMPLE_ID)"
					+ " values ('" + documentId + "', sysdate, " + userId + ", sysdate, " + userId
					+ ", "+sessionId +", 1, 'Document', 'Document', '" + elementValueMap.get("parentId") + "','"
					//+ ", SESSIONID, 1, 'Document', 'Document', '" + elementValueMap.get("parentId") + "','"
					//+ "documents','Attachment','" + uploadFileId + "','" + elementValueMap.get("documentName")
					+ docTableType+"','Attachment','" + uploadFileId + "','" + elementValueMap.get("documentName")
					+ "',NULL,NULL)";
			formSaveDao.insertStructTableByFormId(sql, "fg_s_document_pivot", documentId);
		}
		else if(eventAction.equals("getLastPreparationId")){
			//gets the last preparation step, only if it has products/solvents
			String sql = "select listagg(m.formid,',') within group(order by m.formid)"
					+ " from fg_s_step_v t,"
					+ " fg_s_materialref_v m"
					+ " where t.experiment_id = '" + elementValueMap.get("EXPERIMENT_ID") + "'"
					+ " and t.formnumberid = "
							+ "(select distinct max(to_number(formnumberId))"
							+ " from fg_s_step_v"
							+ " where experiment_id = '" + elementValueMap.get("EXPERIMENT_ID") + "'" 
							+ " and formId <> '"+ formId + "'"
							+ " and PREPARATION_RUN = 'Preparation'"
							+")"
					+ " and m.parentid = t.step_id"
					+ " and m.active = '1'"
					+ " and m.sessionid is null"
					+ " and m.tabletype in ('Product','Solvent')";//gets the last preparation step
			return generalDao.selectSingleStringNoException(sql);
		}
		else if (eventAction.equals("checkSampleHasAssayResult")) {
			if (generalUtil.getNull(elementValueMap.get("SAMPLE_ID")).isEmpty()) {
				return "1";//no need to display a confirmation
			}
			String materialProtocol = elementValueMap.get("materialProtocolType");
			String sql = "";
			if(materialProtocol.equals("Formulation")||materialProtocol.equals("Premix")){
				sql = "select distinct count(*) " 
					+ " from fg_i_sampleresults_v t"
					+ " where t.INVITEMMATERIAL_ID in \n"
					+ "(select material_id from fg_s_materialcomponent_v\n"
					+ " where parentid = '"+formId+"'\n"
					+ " and active = 1\n"
					+ generalUtilFormState.getWherePartForTmpData("materialcomponent", formId)+ ")"
					+ " and t.SAMPLE_ID = '" + elementValueMap.get("SAMPLE_ID") + "'" + " and t.RESULT_NAME='Assay'";
			} else {
				sql = "select distinct count(*) " + " from fg_i_sampleresults_v t"
					+ " where t.INVITEMMATERIAL_ID = '" + elementValueMap.get("INVITEMMATERIAL_ID") + "'"
					+ " and t.SAMPLE_ID = '" + elementValueMap.get("SAMPLE_ID") + "'" + " and t.RESULT_NAME='Assay'";
			}
			String res = generalDao.selectSingleStringNoException(sql);
			return res;
		}
		else if (eventAction.equals("insertReactionTableDataToClob")) {
			String plainText = elementValueMap.get("reactionTableData");
			generalUtilLogger.logWriter(LevelType.INFO, "reaction tables UI before save event:</br>" + plainText,
					ActivitylogType.ReactionDataUI, formId);
			String objectId = uploadFileDao.saveStringAsClobRenderId(formCode, plainText);
			return objectId;
		}
		else if (eventAction.equals("calcQuantity") || eventAction.equals("calcVolume") || eventAction.equals("calcMole")
				|| eventAction.equals("calcEquivalent") || eventAction.equals("calcMass")
				|| eventAction.equals("calcYield") || eventAction.equals("calcLimitingAgentOnChange")
				|| eventAction.equals("calcMoleOnChange") || eventAction.equals("calcQuantityOnChange")
				|| eventAction.equals("calcVolumeOnChange")
				|| eventAction.equals("calcByRatioType")
				|| eventAction.equals("calcReactionMassOnChange")
				|| eventAction.equals("calcConcInReactionMass")
				|| eventAction.equals("calcQuantityRate") || eventAction.equals("calcVolumeRate") || eventAction.equals("calcMoleRate")) {
			JSONObject json = new JSONObject(elementValueMap.get(eventAction));
			String toReturn ="";
			if(eventAction.equals("calcReactionMassOnChange")){
				toReturn = integrationCalc.doCalcUI("MaterialTripletCalc", eventAction, "",
						"", elementValueMap, null, null, formCode,
						json.getString("selectedFormId"), userId, json.getJSONObject("data"));
			}else{
			    toReturn = integrationCalc.doCalcUI("MaterialTripletCalc", eventAction, json.getString("mainArg"),
					json.getString("mainArgVal"), elementValueMap, null, null, formCode,
					json.getString("selectedFormId"), userId, json.getJSONObject("data"));
			}
			return toReturn;
			/*if(toReturn==null){
				return "The calculation was not perfomed because there are some missing arguments";
			} else {
				return toReturn;
			}*/
		}
		else if(eventAction.equals("calcWvOnChange")||eventAction.equals("calcWwGrkOnChange")
				||eventAction.equals("calcWwPOnChange")||eventAction.equals("calcFillerOnChange")
				||eventAction.equals("calcDensityOnChange")
				||eventAction.equals("calcBatchSizeOnChange")
				||eventAction.equals("calcActualOnChange")
				||eventAction.equals("calcPurityOnChange")){
			JSONObject json = new JSONObject(elementValueMap.get(eventAction));
			String toReturn ="";
			toReturn = integrationCalc.doCalcComposition("MaterialTripletCalc", eventAction, json.getString("mainArg"),
					json.getString("mainArgVal"), json.getString("mainArgLastVal"),elementValueMap, null, null, formCode,
					json.getString("selectedFormId"), userId, null);
			return toReturn;
		}
		else if(eventAction.equals("getPurityAndCalcComposition")){
			JSONObject json = new JSONObject(elementValueMap.get(eventAction));
			String toReturn ="";
			//execute the calculations when the purity is being changed
			toReturn = integrationCalc.doCalcComposition("MaterialTripletCalc", eventAction, json.getString("mainArg"),
					json.getString("mainArgVal"), json.getString("mainArgLastVal") ,elementValueMap, null, null, formCode,
					json.getString("selectedFormId"), userId, null);
			JSONObject purityChangeReturn = new JSONObject(toReturn);
			String updatedBatch = generalDao.selectSingleStringNoException("select batch_id from fg_S_composition_v where formid = '"+formId+"'\n"
					+ "and active = 1\n"
					+ generalUtilFormState
					.getWherePartForTmpData("composition", elementValueMap.get("parentId")));
			if(updatedBatch.equals(json.getString("mainArgVal"))){//the selected batch has validated well and the affected arguments(w/w%,w/w[gr/kg]..) were calculated with no exceptions.else- the last value has been returned
				JSONObject batchChange = new JSONObject(generalEvent(stateKey, elementValueMap, formCode, formId, userId, isNew, "getBatchData"));
				Iterator<String> keysItr = batchChange.keys();
				while (keysItr.hasNext()) {
					String key = keysItr.next();
					String val = generalUtil.getJsonValById(batchChange.toString(), key);
					if(purityChangeReturn.has(key)){
						JSONObject purityChangeval = new JSONObject(generalUtil.getJsonValById(purityChangeReturn.toString(), key));
						JSONObject batchChangeVal = new JSONObject(val);
						Iterator<String> batchkeysItr = batchChangeVal.keys();
						while (batchkeysItr.hasNext()) {
							String batchChangeKey = batchkeysItr.next();
							String batchChangeKeyVal =generalUtil.getJsonValById(batchChangeVal.toString(), batchChangeKey); 
							purityChangeval.put(batchChangeKey, batchChangeKeyVal);
						}
						purityChangeReturn.put(key, purityChangeval);
					}else{
						purityChangeReturn.put(key, val);
					}
				}
				toReturn = purityChangeReturn.toString();
			}
			return toReturn;
		}
		else if(eventAction.equals("checkBalance")){
			String toReturn ="";
			toReturn = integrationCalc.doCalcComposition("MaterialTripletCalc", eventAction, "",
					"", "",elementValueMap, null, null, formCode,
					formId, userId, null);
			return toReturn;
		}
		else if(eventAction.equals("checkBalanceBeforeDeleteStep")){
			String toReturn ="";
			String sql = "select composition_id\n"
					+ " from fg_s_composition_v\n"
					+ " where parentid = '"+formId+"'\n"
					+ " and active = 1\n"
					+ generalUtilFormState.getWherePartForTmpData("composition", formId);
			String formIdToDelete = generalDao.getCSVBySql(sql, false);
			elementValueMap.put("formIdToDelete", formIdToDelete);
			toReturn = integrationCalc.doCalcComposition("MaterialTripletCalc", "checkBalance", "",
					"", "",elementValueMap, null, null, formCode,
					formId, userId, null);
			return toReturn;
		}
		else if(eventAction.equals("clearCompositionArgs")){
			String toReturn ="";
			String []compositionArr = null;
			if(elementValueMap.containsKey("compositionIdList")){
				compositionArr = elementValueMap.get("compositionIdList").split(",");
			} else {
				String sql = "select composition_id\n"
						+ " from fg_s_composition_v\n"
						+ " where parentid = '"+formId+"'\n"
						+ " and active = 1\n"
						+ generalUtilFormState.getWherePartForTmpData("composition", formId);
				String compositionListCsv = generalDao.getCSVBySqlNoException(sql, false);
				compositionArr = compositionListCsv.split(",");
			}
			for(int i=0;i<compositionArr.length;i++){
				String sql = "update fg_s_composition_pivot\n"
						+ " set WW_GRK = 0,WW_P = 0,WV_GRL = 0,plannedToExpBatch = 0\n"
						+ " where formid = '"+compositionArr[i]+"'"
						+ " and active = 1\n"
						+ " and sessionid is null";
				formSaveDao.updateStructTableByFormId(sql, "fg_s_composition_pivot", Arrays.asList("WW_P","WW_GRK","WV_GRL","plannedToExpBatch"), compositionArr[i]);
//				integrationCalc.doCalcComposition("MaterialTripletCalc", "calcWwGrkOnChange", "WW_GRK", "0", "", elementValueMap, null, null, "Composition", compositionArr[i], userId, null);
			}
			return toReturn;
		}
		else if(eventAction.equals("updateMaterialRateType"))
		{
			try {
				String materialRefId = elementValueMap.get("materialRefId");
				String rateType = elementValueMap.get("rateType");
				
				String sql = "update FG_S_EXPRUNPLANNING_PIVOT t \n"
						+ "   set t.MATERIALRATETYPE = '" + rateType + "' \n"
						+ " 	  ,t.MATERIALRATEUOM = fg_get_uom_by_uomtype(fg_get_uomtype_by_ratetype('" + rateType + "')) \n"
						+" where  MATERIALREFID = '"+materialRefId+"' \n"
						+" and EXPERIMENTID = '"+elementValueMap.get("PARENT_ID")+"'"
						+ "and MATERIALREFID is not null \n"
						+" and t.sessionid is null and t.active = 1";
				formSaveDao.updateStructTableByFormId(sql, "FG_S_EXPRUNPLANNING_PIVOT",Arrays.asList(rateType), materialRefId); 
				
			} catch (Exception e) {
				generalUtilLogger.logWrite(e);
				return "-3";
			}
		}
		else if(eventAction.equals("calcMaterialRates"))
		{	
			StringBuilder sbCalcInfo = new StringBuilder();
			String toReturn = integrationCalc.doCalcRuns(formId, elementValueMap.get("PARENT_ID"),sbCalcInfo);
			
			return "{\"warningMsg\":\""+toReturn+"\",\"calcInfo\":\""+sbCalcInfo.toString()+"\"}";
		}
		else if(eventAction.equals("deleteRun")) {
			
			try {
				String removeId = elementValueMap.get("removeId");
				String sql = "delete from FG_S_EXPRUNPLANNING_PIVOT where formid = '"+removeId+"'";
				formSaveDao.deleteStructTableByFormId(sql,"FG_S_EXPRUNPLANNING_PIVOT", removeId);
				return "0";
			} catch (Exception e) {
				e.printStackTrace();
				return "-1";
			}
		}
		else if(eventAction.equals("checkRunStepsStatus"))
		{
			/*
			String sql = "select count(t.STEP_ID) \n"
						+" from fg_s_step_all_v t \n"
						+" where t.STEPSTATUSNAME = 'Planned' \n"
						+" and t.sourceRunSteps = 1 \n"
						+" and t.EXPERIMENT_ID = '"+formId+"'";*/
			
			String sql = "select count(t.STEP_ID) \n"
						+" from fg_s_step_all_v t \n"
						+" where nvl(upper(PREPARATION_RUN),'PREPARATION')='PREPARATION' \n"
						+" and t.STEPSTATUSNAME = 'Planned' \n"
						+" and t.EXPERIMENT_ID = '"+generalUtil.getNull(elementValueMap.get("EXPERIMENT_ID"),formId)+"'";//if the step called this event then takes the experiment from the map, otherwise, the experiment has called the event, and then takes the formId
			return generalDao.selectSingleStringNoException(sql);
		}
		else if(eventAction.equals("calculateAndCheckForStartedRuns"))
		{
			String experiment_id = formCode.equals("ExperimentCP")?formId:elementValueMap.get("EXPERIMENT_ID");//if the event was fired by step then takes its experiment_id, else-it's fired by the experiment and takes its formId
			String exprunplanningId = generalUtil.getEmpty(elementValueMap.get("runId")
						,generalDao.selectSingleStringNoException("select distinct  formId from fg_s_exprunplanning_v where runnumber = '"+elementValueMap.get("runNumber")+"' and experimentid = '"+experiment_id+"'"));
			StringBuilder sbCalcInfo = new StringBuilder();
			String warningMsg = integrationCalc.doCalcRuns(exprunplanningId, experiment_id, sbCalcInfo);
			if(!warningMsg.equals("")) {
				return "{\"warningMsg\":\""+warningMsg+"\"}";
			}
			else {
				String subMessage = "";			
//				String sql = "select distinct max(decode(t.RUNSTATUSNAME,'Planned','0','Active','1','2'))over(partition by t.EXPERIMENTID) as isExperimentHasStartedRuns \n"
//							+" from FG_S_EXPRUNPLANNING_ALL_V t \n"
//							+" where t.EXPERIMENTID = '"+formId+"'"
//							+" and t.EXPRUNPLANNING_ID <> '"+exprunplanningId+"'";
//				String isExperimentHasStartedRuns = generalDao.selectSingleStringNoException(sql);
				/*String isExperimentHasStartedRuns = elementValueMap.get("expHasStartedRuns");
				if(isExperimentHasStartedRuns.equals("1")){
					//in case experiment has active run (not finished), add confirm message
					subMessage = generalUtil.getSpringMessagesByKey("CONFIRM_PREVIOUS_RUNS_FINISHED_ONCREATE_RUN", "");
				}*/
				String message = generalUtil.getSpringMessagesByKey("CONFIRM_CREATE_RUN", new Object[] {subMessage}, "");
				return message;
			}
		}
		else if (eventAction.equals("checkPrevRunStepsPlannedActualValues")) {
			
			// get number of currently active run
			String sql = "select distinct t.RUNNUMBER from fg_s_exprunplanning_all_v t where t.RUNSTATUSNAME = 'Active' and t.EXPERIMENTID = '"+formId+"'";
			String activeRunNumber = generalDao.selectSingleStringNoException(sql);
			if(!activeRunNumber.equals("")) {			
				StringBuilder sb = new StringBuilder();
				integrationValidation.validate(ValidationCode.VALIDATE_PLANNED_ACTUAL_SIMILAR, formCode, formId, activeRunNumber, sb);
				if (!sb.toString().isEmpty()) {
					return sb.toString();
				}
			}
			return "1";
		}
		/**
		 * createRun - starts the run
		 * The event gets an run number and runId and clone all the steps that referred to that run.
		 */
		else if (eventAction.equals("createRun")) {
			
			String expHasStartedRuns = elementValueMap.get("expHasStartedRuns");
			/*if(expHasStartedRuns.equals("1"))
			{
				String expVersion = formDao.getFromInfoLookup("EXPERIMENT", LookupType.ID, formId,"EXPERIMENTVERSION");
				DateFormat dateFormat = new SimpleDateFormat(generalUtil.getConversionDateFormat());
				Date date = new Date();
				StringBuilder sbInfo = new StringBuilder();
				
				// before new Run creation, check possibility to change status of previous run Steps from 'Active' to 'Finished' 
				String activeRunStepsSql = "select t.STEP_ID from fg_s_step_all_v t where t.STEPSTATUSNAME = 'Active' and t.EXPERIMENT_ID = '"+formId+"'";
				List<String> listOfActiveRunSteps = generalDao.getListOfStringBySql(activeRunStepsSql);
				for(String stepId:listOfActiveRunSteps) {
					// change step finish date
					String updateSql = "update fg_s_step_pivot set finishdate = '"+dateFormat.format(date)+"' where formid = '"+stepId+"'";
					formSaveDao.updateStructTableByFormId(updateSql, "fg_s_step_pivot", Arrays.asList("finishdate"),stepId);
					
					//validate quantity					
					commonFunc.validateUsedQuantityOnStepStatusChangeToFinished(stepId, expVersion, sbInfo);
				}
			}	*/
			String experiment_id = formCode.equals("ExperimentCP")?formId:elementValueMap.get("EXPERIMENT_ID");//if the event was fired by step then takes its experiment_id, else-it's fired by the experiment and takes its formId
			String exprunplanningId = generalUtil.getEmpty(elementValueMap.get("runId")
					,generalDao.selectSingleStringNoException("select distinct formId from fg_s_exprunplanning_v where runnumber = '"+elementValueMap.get("runNumber")+"' and experimentid = '"+experiment_id+"'"));
			String runNumber = elementValueMap.get("runNumber");
			Map<String,String> toReturn = cloneExperiment.createRun(experiment_id, exprunplanningId, runNumber, userId);
			
			return toReturn==null?"1":generalUtil.mapToJson(toReturn);
		}
        else if (eventAction.equals("createRunFromHeaderDDl")) {
			String experiment_id = formCode.equals("ExperimentCP")?formId:elementValueMap.get("EXPERIMENT_ID");
        	String runNumber = generalUtil.getNull(elementValueMap.get("runNumber"));
			String sql = "select distinct formid from fg_s_exprunplanning_v where experimentid = '"+experiment_id+"' and runNumber = '"+runNumber+"'";
			String exprunplanningId = generalDao.selectSingleStringNoException(sql);
			if(!generalUtil.getNull(exprunplanningId).isEmpty() && !runNumber.isEmpty()){
				cloneExperiment.createRun(experiment_id, exprunplanningId, runNumber, userId);
			}else{
				return "-1";
			}
			
			return "";
		}
        else if(eventAction.equals("checkMaterialCopiedToStep")){
        	//get the parentid of the composition that is about to be deleted, in order to render the tables in the right step iframe
        	String sql = "select parentId\n"
        			+ " from fg_s_composition_v\n"
        			+ " where tabletype = 'stepComposition'\n"
        			+ " and parentid in "
        			+ "\r\t (select step_id from fg_S_step_v where experiment_id = '"+formId+"')\n"
					+ " and active = 1\n"
					+ " and invitemmaterial_id in "
					+ "\r\t (select invitemmaterial_id from fg_S_composition_v\n"
					+ " where formid = '"+elementValueMap.get("selectedRowId")+"'\n"
					+ generalUtilFormState.getWherePartForTmpData("composition", formId)
					+ ")";
        	List<String> stepListHaveMaterials = generalDao.getListOfStringBySql(sql);
        	return generalUtil.listToCsv(stepListHaveMaterials);
        }
        else if(eventAction.equals("removeMaterialsFromStep")){
        	String sql = "select composition_id\n"
        			+ " from fg_s_composition_v\n"
        			+ " where tabletype = 'stepComposition'\n"
        			+ " and parentid in "
        			+ "\r\t (select step_id from fg_S_step_v where experiment_id = '"+formId+"')\n"
					+ " and active = 1\n"
					+ " and invitemmaterial_id in "
					+ "\r\t (select invitemmaterial_id from fg_S_composition_v\n"
					+ " where formid = '"+elementValueMap.get("selectedRowId")+"'\n"
					+ generalUtilFormState.getWherePartForTmpData("composition", formId)
					+ ")";
        	List<String> compositionList = generalDao
        			.getListOfStringBySql(sql);
        	for(String composition_id:compositionList){
        		sql = "delete from fg_s_composition_pivot where formid = '"+composition_id+"'";
        		formSaveDao.deleteStructTable(sql, "fg_s_composition_pivot", "formid", composition_id);
        	}
        }
        else if(eventAction.equals("getStepCopiedMaterialListAndChange")) {
        	String oldMaterialId = elementValueMap.get("oldVal");
        	String newMaterialId = elementValueMap.get("newVal");
        	String sql = "select step_id\n"
        			+ "from fg_S_step_v\n"
        			+ "where experiment_id = '"+formId+"'";
        	List<String> stepList = generalDao.getListOfStringBySql(sql);
        	List<String> stepListHaveOldMaterial = new ArrayList<String>();
        	for(String stepId:stepList) {
        		sql = "select composition_id\n"
        				+ "from fg_s_composition_v\n"
        				+ "where parentid = '"+stepId+"'\n"
						+ "and active = 1\n"
						+ "and invitemmaterial_id = '"+oldMaterialId+"'\n"
						+ "and tableType = 'stepComposition'"
						+ generalUtilFormState.getWherePartForTmpData("composition", stepId);
        		List<String> compositionList = generalDao.getListOfStringBySql(sql);
        		if(compositionList.isEmpty()) {
        			continue;
        		}
        		stepListHaveOldMaterial.add(stepId);
        		for(String composition_id:compositionList) {
        			sql = "update fg_s_composition_pivot\n"
    					+ "set invitemmaterial_id = '"+newMaterialId+"'\n"
						+ "where formid = '"+composition_id+"'";
        			formSaveDao.updateStructTableByFormId(sql, "fg_s_composition_pivot", Arrays.asList("INVITEMMATERIAL_ID"),composition_id);
        		}
        	}
        	return generalUtil.listToCsv(stepListHaveOldMaterial);
        }
        else if(eventAction.equals("cloneImportedToPlannedCompositions")){
        	//first,delete all the composition records,and steps
        	generalDao.updateSingleStringNoTryCatch("delete from fg_s_composition_pivot where parentid ='"+formId+"' and tableType = 'expComposition'");
        	List<String> stepListtoDelete = generalDao.getListOfStringBySql("select formid from fg_s_step_v"
        			+ " where experiment_id = '"+formId+"'");
        	elementValueMap.put("stepList", generalUtil.listToCsv(stepListtoDelete));
        	if(!stepListtoDelete.isEmpty()){
        		generalEvent(stateKey, elementValueMap, formCode, formId, userId, isNew, "deleteSteps");
        	}
        	//second, insert the new compositions
        	String compositionParent_id = elementValueMap.get("compositionParent_id");
        	String importedRecipe_id = elementValueMap.get("importedRecipe_id");
        	String sessionId = generalUtilFormState.checkAndReturnSessionId("Composition", formId);
        	List<String> compositionList = generalDao.getListOfStringBySql("select distinct formid from fg_s_composition_v\n"
        			+ " where parentid = '"+compositionParent_id+"'\n"
					+ " and active=1 and sessionid is null"
					+ " order by formid");//generalUtilFormState.getWherePartForTmpData("composition", compositionParent_id));
        	for(String composition_id:compositionList){
        		String cloneCompositionFormId = formSaveDao.cloneStructTable(composition_id," and active = 1 and sessionid is null");
        		List<String> colList = Arrays.asList("parentId", "ACTIVE","SESSIONID","BATCH_ID","TABLETYPE","plannedToBatch","delta");
				String sql = "update FG_S_composition_PIVOT set parentId = '" + formId + "',ACTIVE = 1, SESSIONID ='"+sessionId+"',TABLETYPE='expComposition',plannedToBatch=null,BATCH_ID = null,delta=null where FORMID='"
						+ cloneCompositionFormId + "' ";
				formSaveDao.updateStructTableByFormId(sql, "FG_S_composition_PIVOT", colList,
						cloneCompositionFormId);
        	}
        	String formulationType_id = "";
        	String density = "";
        	String externalCode = "";
        	String batchSize = "";
        	String BATCHSIZE_UOM = "";
        	if(!compositionParent_id.isEmpty()){
	        	String parentFormCode = formDao.getFormCodeEntityBySeqId("",compositionParent_id);
	        	formulationType_id = formDao.getFromInfoLookup(parentFormCode, LookupType.ID, compositionParent_id, "FORMULATIONTYPE_ID");
	        	density = generalDao.selectSingleStringNoException("select density from fg_s_"+parentFormCode+"_V where formid = '"+compositionParent_id+"'");
	        	externalCode = generalDao.selectSingleStringNoException("select externalCode from fg_s_"+parentFormCode+"_V where formid = '"+compositionParent_id+"'");
	        	if(parentFormCode.equals("Experiment")){
		        	batchSize = generalDao.selectSingleStringNoException("select batchSize from fg_s_"+parentFormCode+"_V where formid = '"+compositionParent_id+"'");
		        	BATCHSIZE_UOM = generalDao.selectSingleStringNoException("select BATCHSIZE_UOM from fg_s_"+parentFormCode+"_V where formid = '"+compositionParent_id+"'");
	        	}
        	}
        	JSONObject json = new JSONObject();
        	json.put("FORMULATIONTYPE_ID", formulationType_id);
        	json.put("stepListToDelete", stepListtoDelete);
        	json.put("density", density);
        	json.put("externalCode", externalCode);
        	json.put("batchSize", batchSize);
        	json.put("BATCHSIZE_UOM", BATCHSIZE_UOM);
        	return json.toString();
        } 
        else if(eventAction.equals("cloneCompositionFromExpToStep")){
        	//insert the new compositions
        	String compositionsToClone = elementValueMap.get("compositionsToClone");
        	if(compositionsToClone.isEmpty()){
        		return "";
        	}
        	//building the planned composition details log in order to get validation of missing data(such as missing batch etc)
			String compositiontype = "Solid";//elementValueMap.get("COMPOSITIONTYPENAME");
			commonFunc.buildRecipeMaterialFuncLog(elementValueMap.get("EXPERIMENT_ID"), generalUtil.getSessionUserId(),compositiontype,"0",compositionsToClone);
			
        	JSONObject retVal = new JSONObject();
        	String sessionId = generalUtilFormState.checkAndReturnSessionId("Composition", formId);
        	String compositionWithZeroDelta = generalDao.selectSingleStringNoException("select distinct count(*) from fg_s_composition_v\n"
        			+ " where composition_id in ("+compositionsToClone+")\n"
					+ " and delta = 0\n"
					+ generalUtilFormState.getWherePartForTmpData("composition", elementValueMap.get("EXPERIMENT_ID"))
					+ " order by formid");
        	
        	retVal.put("alertMessage", compositionWithZeroDelta.equals("0")?"":"Notice! Materials with zero delta were not copied ");
        	
        	List<Map<String,Object>> compositionList = generalDao.getListOfMapsBySql("select distinct formid,sessionid from fg_s_composition_v\n"
        			+ " where composition_id in ("+compositionsToClone+")\n"
					+ " and delta <> 0\n"
					+ generalUtilFormState.getWherePartForTmpData("composition", elementValueMap.get("EXPERIMENT_ID"))
					+ " order by formid");//generalUtilFormState.getWherePartForTmpData("composition", compositionParent_id));
			Map<String,String> replaceFieldsMap = new HashMap<>();
			replaceFieldsMap.put("plannedToExpBatch", "plannedToBatch");
			List<String> clonedCompositionList = new ArrayList<>();
        	for(Map<String,Object> entry:compositionList){
        		String composition_id = entry.get("formid")==null?"":entry.get("formid").toString();
        		String wherePartsessionId = entry.get("sessionid")==null?"":entry.get("sessionid").toString();
        		String cloneWherepart = wherePartsessionId.isEmpty()?" and sessionid is null":" and sessionid = '"+wherePartsessionId+"'";
        		String cloneCompositionFormId = formSaveDao.cloneStructTable(composition_id,replaceFieldsMap,cloneWherepart);
        		clonedCompositionList.add(cloneCompositionFormId);
        		List<String> colList = Arrays.asList("parentId", "ACTIVE","SESSIONID","TABLETYPE","plannedToBatch","actual");
				String sql = "update FG_S_composition_PIVOT set parentId = '" + formId + "',ACTIVE = 1, SESSIONID = "+sessionId+",TABLETYPE='stepComposition',plannedToBatch=null,iscopiedfromplanned = 1,actual = null where FORMID='"
						+ cloneCompositionFormId + "' ";
				formSaveDao.updateStructTableByFormId(sql, "FG_S_composition_PIVOT", colList,
						cloneCompositionFormId);
        	}
        	retVal.put("newCompositionsList", generalUtil.listToCsv(clonedCompositionList));
        	return retVal.toString();
        }
        else if(eventAction.equals("getRowTypeData")){
        	String parentEntityId = elementValueMap.get("PARENT_ID");
			String parentEntity_formcode = formDao.getFormCodeBySeqId(parentEntityId);
			String scopeProjectId = null;
		/*	if(parentEntity_formcode.equals("ExperimentFor")){
				//deletes all the purities that exist in the current composition row
				integrationDTAdamaImp.onChangeDataTableCell(stateKey, parentEntity_formcode, parentEntityId, formCode, formId, userId
						, "PURITY", "", "", "", "");
			}*/
			String isFiller = generalDao.selectSingleStringNoException("select nvl(filler,'0')\n"
					+ "from fg_s_composition_v\n"
					+ "where formid = '"+formId+"'\n"
					+ "and active = 1\n"
					+ generalUtilFormState.getWherePartForTmpData("composition", parentEntityId));
			if(isFiller.equals("0")){
				integrationDTAdamaImp.onChangeDataTableCell(stateKey, parentEntity_formcode, parentEntityId, formCode, formId, userId
							, "ww_p", "", "", "", "");
				integrationDTAdamaImp.onChangeDataTableCell(stateKey, parentEntity_formcode, parentEntityId, formCode, formId, userId
							, "ww_grk", "", "", "", "");
				integrationDTAdamaImp.onChangeDataTableCell(stateKey, parentEntity_formcode, parentEntityId, formCode, formId, userId
							, "wv_grl", "", "", "", "");
				integrationDTAdamaImp.onChangeDataTableCell(stateKey, parentEntity_formcode, parentEntityId, formCode, formId, userId
							, "plannedToBatch", "", "", "", "");
				integrationDTAdamaImp.onChangeDataTableCell(stateKey, parentEntity_formcode, parentEntityId, formCode, formId, userId
							, "plannedToExpBatch", "", "", "", "");
				integrationDTAdamaImp.onChangeDataTableCell(stateKey, parentEntity_formcode, parentEntityId, formCode, formId, userId
							, "delta", "", "", "", "");
			}
			// make scope by project id (override the "project_id_scope_in" parameter null default- in the DB fg_get_material_list function)
			// Note: in the StepFr the rowMaterial change can only be made in the products table and for this table we need the scopeProjectId (if in the future it will be called from the material table we will need to specify it better)
			if(parentEntity_formcode.equals("StepFr")) {
				scopeProjectId = generalDao.selectSingleString("select project_id from fg_s_step_v where formid = " + parentEntityId + "");
				scopeProjectId = scopeProjectId.isEmpty()?null:scopeProjectId; // make null on failure (empty) to pass null as default for the DB function;
			}
			
        	JSONObject json = new JSONObject();
        	String sql = "select '{\"WW_P\":\"\",\"WW_GRK\":\"\",\"WV_GRL\":\"\",\"plannedToBatch\":\"\",\"delta\":\"\",\"plannedToExpBatch\":\"\""
        			+ ",\"material_list\":'||fg_get_material_list('',5000, '"+elementValueMap.get("rowTypeName")+"','false',is_autosave_in => 'true', project_id_scope_in => " + scopeProjectId + ")||'"
        			+ ",\"casnamberinf\":\"\",\"casName\":\"\", \n"
					+ "\"formulainf\":\"\",\"productDensity\":\"\",\"densityinf\":\"\",\"densityuom_id_inf\":\"\", \n"
					+ "\"mwinf\":\"\",\"mwuom_val\":\"\",\"MWUOM_ID_INF\":\"\",\"iupac_name\":\"\", \n"
					+ "\"MSDS\":\"\", \"batch_list\":\"\", \"sample_list\":\"\",\"function_list\":\"\",\"casNumber\":\"\""
					+ ",\"description\":\"\",\"MANUFACTURER_ID\":\"\","
					+ "\"purityinf\":\"100\",\"purity\":\"100\",\"purityuom_id_inf\":\"%\"}' "
					+ " from dual";
        	String materialData = generalDao.getSingleStringFromClobNoException(sql);
        	JSONObject materialDataJSON = null;
			try {
				materialDataJSON = new JSONObject(materialData);
			} catch (Exception e) {
				materialDataJSON = new JSONObject();
				e.printStackTrace();
			}

			String selectedFormId = elementValueMap.get("form_id");
			json = new JSONObject();
			json.put(selectedFormId, materialDataJSON);
			return json.toString();
        }
		else if (eventAction.equals("getMaterialData")) {
			String parentEntityId = elementValueMap.get("PARENT_ID");
			String parentEntity_formcode = formDao.getFormCodeBySeqId(parentEntityId);
			if(parentEntity_formcode.equals("ExperimentFor")){
				integrationDTAdamaImp.onChangeDataTableCell(stateKey, parentEntity_formcode, parentEntityId, formCode, formId, userId
						, "PURITY", "", "", "", "");
			}
			Map<String,String> resultData = new HashMap<>();
			if(elementValueMap.containsKey("SAMPLE_ID") && !elementValueMap.get("SAMPLE_ID").isEmpty()&&formCode.equals("MaterialComponent")){
				//gets the assay main result for the selected material
				String sqlString = "Select RESULT_VALUE,to_char(RESULT_DATE,'"+generalUtil.getConversionDateFormat()+"') RESULT_DATE\n"
						+ " from FG_I_SELECTEDRESULTS_V r\n"
						+ "where r.RESULT_MATERIAL_ID = '"+elementValueMap.get("invitemmaterial_id")+"' and r.SAMPLE_ID ='" + elementValueMap.get("SAMPLE_ID")
						+ "' and RESULT_NAME = 'Assay'";
				resultData = generalDao.sqlToHashMap(sqlString);
				String sessionId = generalUtilFormState.checkAndReturnSessionId("materialcomponent", elementValueMap.get("PARENT_ID"));
				sqlString = "update fg_s_materialcomponent_pivot\n"
						+ " set concentration = '"+generalUtil.getNull(resultData.get("RESULT_VALUE"))+"',\n"
						+ " approvalDate = '"+generalUtil.getNull(resultData.get("RESULT_DATE"))+"'"
						+ " where formid = '"+formId+"'\n"
						+ " and sessionid = '"+sessionId+"'\n"
						+ " and active = 1";
				formSaveDao.updateStructTableByFormId(sqlString, "fg_s_materialcomponent_pivot", Arrays.asList("approvalDate","concentration"), formId);
			}
			String sql = "";
			String materialFormcode = formDao.getFormCodeEntityBySeqId("",elementValueMap.get("invitemmaterial_id"));
			JSONObject json = new JSONObject();
			String parent_id = elementValueMap.get("PARENT_ID");
			if(materialFormcode.equals("InvItemMaterial")){
				sql = "select '{\"material_name\":\"'||t.INVITEMMATERIALNAME||'\",\"casnamberinf\":\"'||t.CASNUMBER||'\",\"casName\":\"'||t.CASNAME||'\", \n"
					+ "\"formulainf\":\"'||t.CHEMICALFORMULA||'\",\"productDensity\":\"'||fg_get_num_display(t.DENSITY,0,3)||'\",\"densityinf\":\"'||fg_get_num_display(t.DENSITY,0,3)||'\",\"densityuom_id_inf\":\"'||t.DENSITY_UOM_ID||'\", \n"
					+ "\"mwinf\":\"'||fg_get_num_display(t.mw,0,3)||'\",\"mwuom_val\":\"'||fg_get_Uom_display(t.MW_UOM_ID)||'\",\"MWUOM_ID_INF\":\"'||t.MW_UOM_ID||'\",\"iupac_name\":\"'||t.IUPACNAME||'\", \n"
					+ "\"MSDS\":'||nvl(FG_GET_SMART_LINK_OBJECT(t.msds ,'InvItemMaterial' ,t.\"FORMID\" ,'MSDS','file',0,'MSDS' ),'\"\"')||', \n"
					+ "\"batch_list\":'||fg_get_batch_list(invitemmaterial_id,null)||', \n"
					+ "\"sample_list\":'||fg_get_samples_list_by_parent('',"+parent_id+",invitemmaterial_id)||',\n"
					+ "\"casNumber\":\"'||t.CASNUMBER||'\",\"description\":\"'||t.description||'\",\"MANUFACTURER_ID\":\"'||m.manufacturername||'\",\n"
					+"\"function_list\":'||decode(t.coformulant,1,fg_get_function_list(selected_material_id_in=>invitemmaterial_id,selected_function_id_in => null,is_autosave_in=>'true',formCode_in=>'Composition'),'\"\"')||'\n"
					+(elementValueMap.containsKey("SAMPLE_ID")?",\"concentration\":\""+generalUtil.getNull(resultData.get("RESULT_VALUE"))+"\",\"approvalDate\":\""+generalUtil.getNull(resultData.get("RESULT_DATE"))+"\"'||'\n":"")
					//+ "\"FUNCTION_ID\":\"'||(select distinct first_value(f.MATERIALFUNCTION_ID)over(partition by t.formid)from FG_S_MATERIALFUNCTION_V f where INSTR(','||t.MATERIALFUNC_ID||',',','||f.MATERIALFUNCTION_ID(+)||',')>0)||'\""
					+ "}' "
					+ " from fg_s_invitemmaterial_v t,\n"
					+ " fg_s_manufacturer_all_v m"
					+ " where invitemmaterial_id = '"
					+ elementValueMap.get("invitemmaterial_id") + "'\n"
					+ " and t.MANUFACTURER_ID = m.MANUFACTURER_ID(+)\n";
			} else if(materialFormcode.equals("RecipeFormulation")){
				sql = "select '{\"material_name\":\"'||t.recipeformulationname||'\",\"casnamberinf\":\"\",\"casName\":\"\", \n"
						+ "\"formulainf\":\"\",\"productDensity\":\"\",\"densityinf\":\"\",\"densityuom_id_inf\":\"\", \n"
						+ "\"mwinf\":\"\",\"mwuom_val\":\"\",\"MWUOM_ID_INF\":\"\",\"iupac_name\":\"\", \n"
						+ "\"MSDS\":\"\", \n"
						+ "\"batch_list\":'||fg_get_batch_list(formid,null)||', \n"
						+ "\"casNumber\":\"\",\"description\":\"\",\"MANUFACTURER_ID\":\"\",\n"
						
						+(elementValueMap.containsKey("SAMPLE_ID")?",\"concentration\":\""+generalUtil.getNull(resultData.get("RESULT_VALUE"))+"\",\"approvalDate\":\""+generalUtil.getNull(resultData.get("RESULT_DATE"))+"\"'||'\n":"")
						//+ "\"FUNCTION_ID\":\"'||(select distinct first_value(f.MATERIALFUNCTION_ID)over(partition by t.formid)from FG_S_MATERIALFUNCTION_V f where INSTR(','||t.MATERIALFUNC_ID||',',','||f.MATERIALFUNCTION_ID(+)||',')>0)||'\""
						+ "}' "
						+ " from fg_s_recipeformulation_v t\n"
						+ " where recipeformulation_id = '"
						+ elementValueMap.get("invitemmaterial_id") + "'\n";
			}
			String materialData = generalDao.getSingleStringFromClobNoException(sql);
			if (materialData.equals("")) {
				sql = "select '{\"material_name\":\"\",\"casnamberinf\":\"\",\"casName\":\"\", \n"
						+ "\"formulainf\":\"\",\"productDensity\":\"\",\"densityinf\":\"\",\"densityuom_id_inf\":\"\", \n"
						+ "\"mwinf\":\"\",\"mwuom_val\":\"\",\"MWUOM_ID_INF\":\"\",\"iupac_name\":\"\", \n"
						+ "\"MSDS\":\"\", \"batch_list\":\"\", \"sample_list\":\"\",\"function_list\":\"\",\"FUNCTION_ID\":\"\",\"casNumber\":\"\""
						+ ",\"description\":\"\",\"MANUFACTURER_ID\":\"\","
						+ "\"purityinf\":\"100\",\"purity\":\"100\",\"purityuom_id_inf\":\"%\"\n"
						+ ",\"approvalDate\":\"\",\"concentration\":\"\"}' " 
						+ " from dual";
				materialData = generalDao.selectSingleStringNoException(sql);
			}
			JSONObject materialDataJSON = null;
			try {
				materialDataJSON = new JSONObject(materialData);
			} catch (Exception e) {
				materialDataJSON = new JSONObject();
				e.printStackTrace();
			}

			String selectedFormId = elementValueMap.get("form_id");
			json = new JSONObject();
			json.put(selectedFormId, materialDataJSON);
			return json.toString();

		}
		else if(eventAction.equals("openCompositionDetails")){
			//first, build the whole tree of the details
			String compositiontype = elementValueMap.get("COMPOSITIONTYPENAME");
			commonFunc.buildRecipeMaterialFuncLog(formId, userId,compositiontype,elementValueMap.get("density"),null);

			//second,copy the current data in the temporary table FG_RECIPE_MATERIAL_FUNC_LOG into the hst
			String sql = "insert into fg_RECIPE_MATERIAL_FUNC_HST\n"
					+ " select * from fg_RECIPE_MATERIAL_FUNC_LOG";
			generalDao.updateSingleStringNoTryCatch(sql);
			String sessionId = generalUtil.getSessionId();
			
			//third,  delete the current data from the FG_RECIPE_MATERIAL_FUNC_REPORT where the user sessionid is the current one
			sql = " delete from fg_RECIPE_MATERIAL_FUNC_REPORT\n"
				+ "where sessionid = '"+sessionId+"'\n"
				+ " and root_id = '"+formId+"'";
			generalDao.updateSingleStringNoTryCatch(sql);
			
			//fourth,  copy the current data from the temp table FG_RECIPE_MATERIAL_FUNC_LOG to FG_RECIPE_MATERIAL_FUNC_REPORT
			//NOTE! the fg_RECIPE_MATERIAL_FUNC_LOG is temporary r this current transaction and user. After this, the table data is dissipated
			sql = " insert into fg_RECIPE_MATERIAL_FUNC_REPORT\n"
				+ "(ADDITIONALINFO,COMPOSITION_ID,FILLER,FUNCTION_ID,GENERAL_RELATION_VAL,INVITEMBATCH_ID,INVITEMMATERIAL_ID,IS_ACTIVE_INGREDIENT,PARENTID,ROOT_COMPOSITION_ID,ROOT_ID,ROWTYPE,TABLETYPE,TIMESTAMP,USER_ID,WV_GRL,WW_GRK,WW_P\n"
				+ ",SESSIONID)\n"
				+ "select ADDITIONALINFO,COMPOSITION_ID,FILLER,FUNCTION_ID,GENERAL_RELATION_VAL,INVITEMBATCH_ID,INVITEMMATERIAL_ID,IS_ACTIVE_INGREDIENT,PARENTID,ROOT_COMPOSITION_ID,ROOT_ID,ROWTYPE,TABLETYPE,TIMESTAMP,USER_ID,WV_GRL,WW_GRK,WW_P\n"
				+ ",'"+sessionId+"'\n"
				+ "from fg_RECIPE_MATERIAL_FUNC_LOG";
			generalDao.updateSingleStringNoTryCatch(sql);
		}
		
		else if(eventAction.equals("buildLogAndValidateRecipeExperimentConnection")){//this operation occurs on experiment save
			String sql = "";
			String experimentStatusName = formDao.getFromInfoLookup("experimentstatus", LookupType.ID, elementValueMap.get("STATUS_ID"), "name");
			if (experimentStatusName.equals("Active")) {//fixed bug 8825
				//check cancelled materials
				integrationValidation.validate(ValidationCode.CHECK_COMPOSITION_HAS_CANCELLED_MATERIALS, formCode, formId, "", new StringBuilder());
			}
			//check purity exceeded the 100%
			sql = "select invitemmaterial_id,purity\n"
					+ "from fg_s_composition_v\n"
					+ "where tabletype = 'expComposition'\n"
					+ "and parentid = '"+formId+"'\n"
					+ "and active = 1\n"
					+ generalUtilFormState.getWherePartForTmpData("composition", formId);
			double sumPurity;
			List<Map<String,Object>> purityList = generalDao.getListOfMapsBySql(sql);
			for(Map<String,Object> composition:purityList){
				String compositionMaterial_id = composition.get("INVITEMMATERIAL_ID")!=null?composition.get("INVITEMMATERIAL_ID").toString():"";
				String purity = composition.get("PURITY")!=null?composition.get("PURITY").toString():"100";
				sumPurity = 0;
				String materialId="";
				if(purity.contains("{")){//supposed it's a json
					JSONObject purityJson = new JSONObject(purity);
					Iterator<String> materialResultPair = purityJson.keys();
					while (materialResultPair.hasNext()) {
						materialId = materialResultPair.next();
					    String value = generalUtil.getJsonValById(purity, materialId);
					    sumPurity +=(value.isEmpty()?0:Double.parseDouble(value));
					}
				} /*else {
					sumPurity +=(purity.isEmpty()?0:Double.parseDouble(purity));
				}*/
				if(sumPurity>100){
					String rowFormcode = formDao.getFormCodeBySeqId(compositionMaterial_id);
					String rowName = formDao.getFromInfoLookup(rowFormcode, LookupType.ID, compositionMaterial_id, "name");
					throw new Exception("Total of all assay results cannot exceed the 100%. Please correct the assay values in the composition '"+rowName+"'");
				}
			}
			
			String is_step_exist = generalDao.selectSingleStringNoException("select distinct count(*) from fg_S_step_v where experiment_id = '"+formId+"'");
			if(is_step_exist.equals("0") && experimentStatusName.equals("Planned")){
				return "";
			}

			//check that filler exist
			integrationValidation.validate(ValidationCode.CHECK_COMPOSITION_HAS_FILLER, formCode, formId, "", new StringBuilder());			
			//building the planned composition details log in order to get validation of missing data(such as missing batch etc)
			String compositiontype = elementValueMap.get("COMPOSITIONTYPENAME");
			commonFunc.buildRecipeMaterialFuncLog(formId, userId,compositiontype,elementValueMap.get("density"),null);
			
			//checks if the assay does not exceed a total of 100%
			if(elementValueMap.get("RECIPEFORMULATION_ID").isEmpty()){//no need to run all the validations if no recipe is connected
				return "";
			}
			//String experimentStatusName = formDao.getFromInfoLookup("experimentstatus", LookupType.ID, elementValueMap.get("STATUS_ID"), "name");
			String experimentVersion = elementValueMap.get("experimentVersion");
			if(Integer.parseInt(experimentVersion)!=1
					|| !experimentStatusName.equals("Planned")&&!experimentStatusName.equals("Active")){//the validation should run only when the status of the experiment is planned/active and the Experiment version = 1.
				return "";
			}
			//first, build the whole tree of the details
			commonFunc.buildImportedCompositionLog(formId, elementValueMap.get("compositionParent_id"), userId);
			
			//second,copy the current data in the temporary table FG_RECIPE_MATERIAL_FUNC_LOG into the hst
			sql = "insert into fg_RECIPE_MATERIAL_FUNC_HST\n"
					+ " select * from fg_RECIPE_MATERIAL_FUNC_LOG";
			generalDao.updateSingleStringNoTryCatch(sql);
			
			//third , check whether the experiment-recipe connection may disconnect
			Map<String,String> retVal = commonFunc.validateRecipeExperimentconnection(formId,elementValueMap.get("compositionParent_id"));
			
			//fourth, return the recipe-experiment connection that was found in the experiment
			sql = "select invitemmaterial_id\n"
					+ "from FG_RECIPE_MATERIAL_FUNC_LOG t,\n"
					+ "fg_s_recipeformulation_v r\n"
					+ "where t.invitemmaterial_id = r.recipeformulation_id\n"
					+ "and t.tabletype = 'plannedComposition'";
			String recipeList = generalDao.getCSVBySql(sql, false);
			retVal.put("recipeList", recipeList);
			return generalUtil.mapToJson(retVal);
		}
		else if (eventAction.equals("getBatchData")) {
			String parentEntityId = elementValueMap.get("PARENT_ID");
			String parentEntity_formcode = formDao.getFormCodeBySeqId(parentEntityId);
			if(parentEntity_formcode.equals("ExperimentFor")){
				String rowType = generalDao.selectSingleStringNoException("select rowtype from fg_s_composition_v\n"
						+ " where composition_id = '"+formId+"'\n"
						+ generalUtilFormState.getWherePartForTmpData("composition", parentEntityId));
				if(rowType.contains("Premix") || rowType.contains("Recipe")){
					//insert the results that were found for the sample that's defining the selected batch
					Map<String,String> materialResultPair = new HashMap<>();
					Deque<String> materialNestedList = new ArrayDeque<String>();
					String node_id = elementValueMap.get("invitembatch_id").equals("0")?"": elementValueMap.get("invitembatch_id");
					commonFunc.getMaterialResultList(node_id,materialNestedList,0,materialResultPair);
					JSONObject materialResultPairJson = generalUtil.mapToJsonObject(materialResultPair);
					String onChangeColumnVal = (materialResultPairJson.length()==0?"100":materialResultPairJson.toString());
					integrationDTAdamaImp.onChangeDataTableCell(stateKey, parentEntity_formcode, parentEntityId, formCode, formId, userId
								, "PURITY", onChangeColumnVal, "", "", "");
				}
			}
			JSONObject json = new JSONObject();
			String sql = "select '{\"purityinf\":\"'||nvl(t.purity,'100')||'\",\"purity\":\"'||nvl(t.purity,'100')||'\",\"purityuom_id_inf\":\"'||nvl(t.PURITYUOM_ID,'%')||'\","
					+ "\"MANUFACTURER_ID\":\"'||m.manufacturername||'\"}' "
					+ " from fg_s_invitembatch_v t,fg_s_manufacturer_v m" 
					+ " where invitembatch_id = '"
					+ elementValueMap.get("invitembatch_id") + "'"
					+ " and t.manufacturer_id = m.manufacturer_id(+)";
			String batchData = generalDao.selectSingleStringNoException(sql);
			if (batchData.equals("")) {
				sql = "select '{\"purityinf\":\"100\",\"purity\":\"100\",\"purityuom_id_inf\":\"%\",\"MANUFACTURER_ID\":\"\"}' " + " from dual";
				batchData = generalDao.selectSingleStringNoException(sql);
			}
			JSONObject batchDataJSON = null;
			try {
				batchDataJSON = new JSONObject(batchData);
			} catch (Exception e) {
				batchDataJSON = new JSONObject();
				e.printStackTrace();
			}
			String selectedFormId = elementValueMap.get("form_id");
			json = new JSONObject();
			json.put(selectedFormId, batchDataJSON);
			return json.toString();

		}
		else if(eventAction.equals("checkIfExpHasActiveSteps")){
			String sql = "select count(*) "
					+ " from fg_s_step_all_v"
					+ " where experiment_id = '"+formId+"'"
					+ " and stepstatusname not in('Finished','Completed')";
			String activeStepsCount = generalDao.selectSingleStringNoException(sql);
			return activeStepsCount;
		}
		else if (eventAction.equals("updateStepFormulantsTable")) {
			// not in use in new formulation experiment
//			try {
//				String dataString = elementValueMap.get("formulantsTableData");
//				//creating/updating the formulantref data
//				JSONObject tableDataObj = new JSONObject(dataString);
//				List<String> formulantRefIdList = generalDao
//						.getListOfStringBySql("select formid from fg_s_formulantref_v where parentid = '" + formId
//								+ "' and sessionid is null and nvl(active,1)=1 and upper(nvl(tabletype,'NA')) <> 'PRODUCTMIXTURE'"); // YP 06012020 - fix bug 7720 - it is a cosmetic fix in order to not delete the PRODUCTMIXTURE RECORDS (all of the formulation BL will be changed in future versions) - and the code was damaged  because of changes made for DEM :(
//				for (String formulantRefId : formulantRefIdList) {
//					if (!tableDataObj.has(formulantRefId)) {
//						//deleting the formulantref_id that does not exist any more in the current formulantRef's
//						formSaveDao.deleteStructTableByFormId(
//								"delete from fg_s_formulantref_pivot where formid = '" + formulantRefId + "'",
//								"fg_s_formulantref_pivot", formulantRefId);
//						continue;
//					}
//					//update current formulantref data
//					JSONObject rowData = tableDataObj.getJSONObject(formulantRefId);
//					if (generalUtil.getJsonValById(rowData.toString(), "MATERIALID").equals("0")
//							|| generalUtil.getJsonValById(rowData.toString(), "MATERIALID").isEmpty()) {
//						formSaveDao.deleteStructTableByFormId(
//								"delete from fg_s_formulantref_pivot where formid = '" + formulantRefId + "'",
//								"fg_s_formulantref_pivot", formulantRefId);
//						continue;
//					}
//					List<String> columnList = new ArrayList<>(Arrays.asList("TIMESTAMP", "CHANGE_BY"));
//					List<String> data = new ArrayList<>(Arrays.asList("sysdate", userId));
//					Iterator<String> rowItr = rowData.keys();
//					while (rowItr.hasNext()) {
//						String columnName = rowItr.next();
//						columnList.add(columnName);
//						String columnVal = generalUtil.getJsonValById(rowData.toString(), columnName);
//						data.add(columnVal == null ? columnVal : "'" + columnVal + "'");
//					}
//					String setSql = "";
//					for (int i = 0; i < columnList.size(); i++) {
//						setSql += setSql.isEmpty() ? columnList.get(i) + "=" + data.get(i)
//								: "," + columnList.get(i) + "=" + data.get(i);
//					}
//					String sql = "update fg_s_formulantref_pivot set " + setSql + " where formid = '" + formulantRefId
//							+ "'";
//					formSaveDao.updateStructTableByFormId(sql, "fg_s_formulantref_pivot", columnList, formulantRefId);
//				}
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		else if (eventAction.equals("updateChemImage")) {
			String fullData = generalDao
					.getSingleStringFromClobNoException("select file_content from fg_clob_files where file_id = '"
							+ elementValueMap.get("reaction_all_data_link") + "'");
			if (fullData.isEmpty()) {
				return "0";
			}
			String newImgId = chemDao.getNewChemImg(fullData);
			if (newImgId.isEmpty()) {
				return "0";
			}
			formSaveDao.updateSingleStringInfoNoTryCatch("update fg_chem_doodle_data set FULL_IMG_FILE_ID = '" + newImgId
					+ "' where parent_id = '" + elementValueMap.get("parent_id") + "'");
		}
		else if (eventAction.equals("stopQuery")) {
			searchSqlDao.stopQuery();
		}
		else if (eventAction.equals("checkManualResultsToDelete")) {
			String sql = "with FILT_IN AS (\r\n"
					+ " select /*+ MATERIALIZE */ SAMPLE_ID||','||COMPONENT_ID||','||REQUEST_ID AS FILT_IN_DATA\r\n"
					+ "    from (\r\n" + "      select DISTINCT  smpl.SAMPLE_ID,smplReq.REQUEST_ID,c.COMPONENT_ID \r\n"
					+ "      from FG_I_CONNECTION_REQSMPLDEXP_V smplReq, fG_S_SAMPLESELECT_ALL_V smpl, "
					+ " 	(select COMPONENT_ID, PARENTID from FG_S_COMPONENT_V" + "		where 1=1  "
					+ generalUtilFormState.getWherePartForTmpData("COMPONENT", formId) + ") c \r\n"
					+ "     where smpl.PARENTID = smplReq.EXPERIMENTDEST_ID(+) and smpl.SAMPLE_ID = smplReq.SAMPLE_ID(+) \r\n"
					+ "     and smpl.PARENTID = '" + formId + "' and c.PARENTID = '" + formId + "'\r\n" + "		"
					+ generalUtilFormState.getWherePartForTmpData("SampleSelect", formId) + "\r\n"
					+ "     ) ) select distinct LISTAGG(t.SampleName||';'||t.InvItemMaterialName||'=>'||t.result,'</br>') WITHIN GROUP (order by formid) \r\n"
					+ "from FG_S_MANUALRESULTSREF_all_V t \r\n" + "where t.PARENTID = '" + formId
					+ "' and SAMPLE_ID||','||COMPONENT_ID||','||REQUEST_ID \r\n"
					+ "not in (SELECT FILT_IN_DATA FROM FILT_IN) and nvl(IS_USER_OWN,'0') = '0' and t.RESULT is not null "
					+ generalUtilFormState.getWherePartForTmpData("MANUALRESULTSREF", formId);
			String res = generalUtil.getNull(generalDao.selectSingleStringNoException(sql));
			return res;

		}
		else if (eventAction.equals("checkSampleResult")) {
			//task 24503
			//When additional results are defined for the same sample, a warning message about main results definition should be displayed
			//When the sample has Self Test results only - No Warning message about duplicate results should be displayed in the analytical lab  - bug 7883
			String res="";
			String currentStatusName = formDao.getFromInfoLookup("ExperimentStatus", LookupType.ID,
					elementValueMap.get("STATUS_ID"), "name");
			String lastStatus_id = formDao.getFromInfoLookup("Experiment", LookupType.ID, formId, "STATUS_ID");
			String lastStatusName = formDao.getFromInfoLookup("experimentstatus", LookupType.ID, lastStatus_id, "name");
			String sampleName;
			if (generalUtil.getNull(currentStatusName).equals("Approved") && !lastStatusName.equals("Approved")) {
				List<String> samples = generalDao.getListOfStringBySql(
						"select distinct t.SAMPLE_ID from fg_s_ManualResultsRef_v t where t.RESULT is not null and t.PARENTID ='"
								+ formId + "'");
                String sql = String
						.format("select distinct sample_id from(select distinct t.INVITEMMATERIAL_ID,t.RESULT_NAME,t.sample_id from fg_r_experimentresult_basedt_v t where experimentdest_id = '"
								+ formId + "' and sample_id in(" + generalUtil.listToCsv(samples) + ")" + " INTERSECT "
								+ " select distinct t.INVITEMMATERIAL_ID,t.RESULT_NAME,t.sample_id from FG_i_SAMPLERESULTS_V t where  sample_id in ("
								+ generalUtil.listToCsv(samples) + ") and t.PROTOCOLTYPENAME <>'Organic')");
				List<String> samplesId = generalDao.getListOfStringBySql(sql);
				for (String sampleId : samplesId) {
					sampleName = formDao.getFromInfoLookup("Sample", LookupType.ID, sampleId, "name");
					if (res.isEmpty()) {
						res += "<a href='#' onClick=\"checkAndNavigate(['" + sampleId + "','Sample'])\" >" + sampleName
								+ "</a>";
					} else {
						res += "<a href='#' onClick=\"checkAndNavigate(['" + sampleId + "','Sample'])\" >" + ", "
								+ sampleName + "</a>";
					}
				}
			}
			return res;

		}else if (eventAction.equals("checkMainResults")) {
			//task 24608
			//experiment is approved, and its results set become the main sample results set
			List<String> sampleUpdatedForNotificationMessage = new ArrayList<String>();
			List<String> samples = generalDao.getListOfStringBySql("select distinct t.SAMPLE_ID from fg_s_ManualResultsRef_v t where t.RESULT is not null and t.PARENTID ='" +formId+"'");
			String sql = "";
			/*sql = "select distinct t.INVITEMMATERIAL_ID,t.RESULT_NAME,t.sample_id from fg_r_experimentresult_basedt_v t where experimentdest_id = '"+formId+"' and sample_id in("+generalUtil.listToCsv(samples)+")" + 
					" INTERSECT " + 
					" select distinct t.INVITEMMATERIAL_ID,t.RESULT_NAME,t.sample_id from FG_i_SAMPLERESULTS_V t where  sample_id in("+generalUtil.listToCsv(samples)+")";// and result_id in("+sampleResults+")*/
			sql = "select distinct t.sample_id ,listagg(sr.RESULT_ID,',') WITHIN GROUP (order by RESULT_ID) OVER (partition by sr.sample_id) as resultList  "+
					" from "+
					" (select t.INVITEMMATERIAL_ID,t.RESULT_NAME,t.sample_id from fg_r_experimentresult_basedt_v t where experimentdest_id = '"+formId+"' and sample_id in("+generalUtil.listToCsv(samples)+")"+ 
					" INTERSECT "+
					" select t.INVITEMMATERIAL_ID,t.RESULT_NAME,t.sample_id from FG_i_SAMPLERESULTS_V t where  sample_id in("+generalUtil.listToCsv(samples)+"))t, FG_I_SELECTEDRESULTS_V sr   "+
					" where t.SAMPLE_ID = sr.SAMPLE_ID and t.INVITEMMATERIAL_ID = sr.RESULT_MATERIAL_ID and t.RESULT_NAME = sr.RESULT_NAME";
            List<Map<String,Object>> listOfRemoveResultMaps = generalDao.getListOfMapsBySql(sql);
            sql = "select distinct t.sample_id ,listagg(sr.RESULT_ID,',') WITHIN GROUP (order by RESULT_ID) OVER (partition by sr.sample_id) as resultList  "+
					" from "+
					" (select t.INVITEMMATERIAL_ID,t.RESULT_NAME,t.sample_id from fg_r_experimentresult_basedt_v t where experimentdest_id = '"+formId+"' and sample_id in("+generalUtil.listToCsv(samples)+")"+ 
					" INTERSECT "+
					" select t.INVITEMMATERIAL_ID,t.RESULT_NAME,t.sample_id from FG_i_SAMPLERESULTS_V t where  sample_id in("+generalUtil.listToCsv(samples)+"))t, fg_r_experimentresult_basedt_v sr   "+
					" where t.SAMPLE_ID = sr.SAMPLE_ID and t.INVITEMMATERIAL_ID = sr.INVITEMMATERIAL_ID and t.RESULT_NAME = sr.RESULT_NAME and sr.experimentdest_id = '"+formId+"'";
            List<Map<String,Object>> listOfAddResultMaps = generalDao.getListOfMapsBySql(sql);
			
			for (String sampleId : samples) {
				/*if(listOfRemoveResultMaps == null || listOfRemoveResultMaps.isEmpty()){
					continue;
				}*/
				List<String> sampleResultsList = new ArrayList<String>();
				String sampleResults = generalUtil.getNull(generalDao.selectSingleStringNoException("select sampleresults from fg_s_sample_v t where t.sample_id = "+sampleId));
				if(!sampleResults.isEmpty()){
				sampleResultsList = new LinkedList<>(Arrays.asList(sampleResults.split(",")));
				}
				String[] removeResultsList = null;
				String[] addResultsList = null;
				for (Map<String, Object> entry : listOfRemoveResultMaps) {
					if(entry.get("SAMPLE_ID").equals(sampleId)){
						removeResultsList = entry.get("RESULTLIST").toString().split(",");
						for(String res:removeResultsList){
							sampleResultsList.remove(res);
						}
					}
				}
				for (Map<String, Object> entry : listOfAddResultMaps) {
					if(entry.get("SAMPLE_ID").equals(sampleId)){
						addResultsList = entry.get("RESULTLIST").toString().split(",");
						for(String res:addResultsList){
							sampleResultsList.add(res);
						}
					}
				}
				if(sampleResultsList!= null && !sampleResultsList.isEmpty()){
					sql = "update fg_s_sample_pivot set sampleresults = '"+generalUtil.listToCsv(sampleResultsList)+"' where formid = "+sampleId;
					formSaveDao.updateStructTableByFormId(sql, "fg_s_sample_pivot", Arrays.asList("sampleresults"), formId);
					sampleUpdatedForNotificationMessage.add(sampleId);
					//send notification SAMPLE_RESULT_TO_UPDATE 
					String experimentOwnerId = generalDao.selectSingleStringNoException("select t.OWNER_ID from FG_S_EXPERIMENT_V t,fg_s_sample_v s where s.EXPERIMENT_ID = t.experiment_id and s.sample_id = '" + sampleId + "'");
					String sampleName = generalDao.selectSingleString("select t.formnumberid from fg_s_sample_v t where formId ='" + sampleId + "'");
					String sampleLink= "<a id='"+sampleId+"';formCode='Sample';val='"+sampleName+"'></a>";
					generalUtilNotificationEvent.sendAdHocNotificationByObjIdVal(Arrays.asList(sampleLink), Arrays.asList(experimentOwnerId), generalUtil.getSpringMessagesByKey("SAMPLE_RESULT_TO_UPDATE", ""));
				}
			}
			commonFunc.refreshResultMv();
		}else if (eventAction.equals("sendNotifExpOwner")) {
			List<String> sampleUpdatedForNotificationMessage = new ArrayList<String>();
			List<String> samples = generalDao.getListOfStringBySql(
					"select distinct t.SAMPLE_ID from fg_s_ManualResultsRef_v t where t.RESULT is not null and t.PARENTID ='"
							+ formId + "'");
            String sql = String
					.format("select distinct sample_id from(select distinct t.INVITEMMATERIAL_ID,t.RESULT_NAME,t.sample_id from fg_r_experimentresult_basedt_v t where experimentdest_id = '"
							+ formId + "' and sample_id in(" + generalUtil.listToCsv(samples) + ")" + " INTERSECT "
							+ " select distinct t.INVITEMMATERIAL_ID,t.RESULT_NAME,t.sample_id from FG_i_SAMPLERESULTS_V t where  sample_id in ("
							+ generalUtil.listToCsv(samples) + "))");
			List<String> samplesId = generalDao.getListOfStringBySql(sql);
			for (String sampleId : samplesId) {
				String experimentOwnerId = generalDao.selectSingleStringNoException("select t.OWNER_ID from FG_S_EXPERIMENT_V t,fg_s_sample_v s where s.EXPERIMENT_ID = t.experiment_id and s.sample_id = '" + sampleId + "'");
			    String sampleName = generalDao.selectSingleString("select t.formnumberid from fg_s_sample_v t where formId ='" + sampleId + "'");
			    String sampleLink= "<a id='"+sampleId+"';formCode='Sample';val='"+sampleName+"'></a>";
				generalUtilNotificationEvent.sendAdHocNotificationByObjIdVal(Arrays.asList(sampleLink), Arrays.asList(experimentOwnerId), generalUtil.getSpringMessagesByKey("SAMPLE_NO_MAIN_RESULT", ""));
			    sampleUpdatedForNotificationMessage.add(sampleLink);
			}
			String experimentOwnerId = generalDao.selectSingleStringNoException("select t.APPROVER_ID from FG_S_EXPERIMENT_V t where t.experiment_id = '" + formId + "'");
		    generalUtilNotificationEvent.sendAdHocNotificationByObjIdVal(sampleUpdatedForNotificationMessage, Arrays.asList(experimentOwnerId), generalUtil.getSpringMessagesByKey("SAMPLE_NO_MAIN_RESULT", ""));
		    commonFunc.refreshResultMv();
	
		}
		if (eventAction.equals("copyMultiStep")) {
			String sql = "delete from fg_s_materialref_pivot where parentid = '" + formId + "'";
			formSaveDao.deleteStructTable(sql, "fg_s_materialref_pivot", "parentid", formId);
			copyReactionByMaterialList(formId, elementValueMap.get("materialref_id_list"), userId);
			String xml = chemDoodleReactionTabUp("", formId, "MaterialRef");
			/*String formCodeFull = "Step.chemDoodleAct";
			String elementId = formSaveDao.getStructFileId("Step.chemDoodleAct");//always get new elementID
			String fullArray = xml;
			String value = chemDao.saveChemData(formCode, formId, elementId, fullArray, formCodeFull, "1");
			sql = "update fg_s_step_pivot set CHEMDOODLEACT = '" + value + "' where formid = '" + formId + "'";
			formSaveDao.updateStructTableByFormId(sql, "fg_s_step_pivot", Arrays.asList("CHEMDOODLEACT"), formId);*/
			return xml;
		}
		if (eventAction.equals("checkExperimentCompletedCharSample")) {
			StringBuilder sb = new StringBuilder();
			//make an appropriation between characterizedSample and the existing sample
			List<String> charSampleList = new ArrayList<String>();
			charSampleList.addAll(Arrays.asList(elementValueMap.get("characterizedSample").split(",")));
			String sampleCsv = generalDao
					.selectSingleStringNoException("select sampleTable from fg_s_sampleselect_v where parentid='"
							+ formId + "'" + generalUtilFormState.getWherePartForTmpData("SampleSelect", formId));
			if (sampleCsv.isEmpty()) {
				elementValueMap.put("characterizedSample", "");
			} else {
				List<String> samplesToRemove = new ArrayList<>();
				List<String> sampleSelectList = Arrays.asList(sampleCsv.split(","));
				for (String sample : charSampleList) {
					if (sampleSelectList.contains(sample)) {
						continue;
					}
					samplesToRemove.add(sample);
				}
				charSampleList.removeAll(samplesToRemove);

				elementValueMap.put("characterizedSample", generalUtil.listToCsv(charSampleList));
			}

			//check the characterized sample by default if it's empty
			if (elementValueMap.get("characterizedSample").isEmpty()) {
				String sql = "with FILT_IN as (select distinct first_value(step_id) over (partition by experiment_id order by to_number(s.formNumberId) desc)  last_step from fg_s_step_v s where experiment_id = '"
						+ formId + "' )"//get the last step of the current experiment
						+ ", FILT_IN2 as (select distinct count(*) over (partition by step_id) cc,sample_mb" + " from "
						+ "(select distinct step_id,mb.sample_mb from fg_i_webix_massbalance_all_v  mb  where mb.is_isolated = '1'"
						+ " and mb.result_is_active = '1'" + " and mb.step_id =(select last_step from FILT_IN)))"
						+ " select distinct mb.sample_mb" + " from fg_s_sampleselect_all_v slct"
						+ " ,(select * from FILT_IN2) mb" + "  where mb.cc = 1" + " and slct.PARENTID = '" + formId
						+ "'" + " and slct.SAMPLE_ID = mb.sample_mb"
						+ generalUtilFormState.getWherePartForTmpData("sampleSelect", formId);

				String characterizedSample = generalDao.selectSingleStringNoException(sql);
				elementValueMap.put("characterizedSample", characterizedSample);
			}

			if (generalUtil.getNull(elementValueMap.get("characterizedSample")).isEmpty()) {
				String sampleselectCount = generalDao.selectSingleStringNoException("select count(*) from fg_s_sampleselect_v where parentid = '"+formId+"'");
				if(!sampleselectCount.equals("0")){//check if there are samples attached
					integrationValidation.validate(ValidationCode.CHARACTERIZED_SAMPLE_EMPTY, formCode, formId, formId, sb);
				}
			}
			return elementValueMap.get("characterizedSample") + ";" + sb.toString();
		}
		if (eventAction.equals("checkStepPlannedActualValues")) {
			StringBuilder sb = new StringBuilder();
			integrationValidation.validate(ValidationCode.VALIDATE_PLANNED_ACTUAL_SIMILAR, formCode, formId, "", sb);
			if (!sb.toString().isEmpty()) {
				return sb.toString();
			}
			
			return "1";
		}
		if (eventAction.equals("checkCancelMaterial")) {
			
			String status_id = formDao.getFromInfoLookup("materialStatus", LookupType.NAME, "Cancelled", "id");
			String materialId = elementValueMap.get("cancelledId");
			String materialStatus=generalDao.selectSingleString("select t.status_id From fg_s_invitemmaterial_pivot t "
					+ "where t.formid="+materialId);
			if(materialStatus.equals(status_id)) {
				throw new Exception("The material has already been canceled");
			}
			
			String materialFormCode = formDao.getFormCodeBySeqIdNoException(materialId);
			boolean isCancellationPerm = generalUtilPermission.isUserInSchemeByCrudl(materialFormCode, userId, "Cancellation");
			if(!isCancellationPerm) {
				throw new Exception("Cancellation is not allowed  (no permission)");
			}
			
			return "1";
		}
		if (eventAction.equals("cancelMaterial")) {
		
			String status_id = formDao.getFromInfoLookup("materialStatus", LookupType.NAME, "Cancelled", "id");
			String materialId = elementValueMap.get("cancelledId");
			
			//validate that all the batches are depleted
			integrationValidation.validate(ValidationCode.DEPLETE_BEFORE_CANCELL_MATERIAL, formCode,
					materialId, status_id, new StringBuilder());
			integrationValidation.validate(ValidationCode.CHEKIFCOMPONENT_BEFORE_CANCELL_MATERIAL, formCode,
					materialId, status_id, new StringBuilder());
			integrationValidation.validate(ValidationCode.CHECKIFRECIPECOMPOSITION_BEFORE_CANCELL_MATERIAL, formCode,
					materialId, status_id, new StringBuilder());

			
			String sql = "update FG_S_INVITEMMATERIAL_PIVOT set STATUS_ID = '" + status_id + "' where formid = '"
					+ materialId + "'";
			formSaveDao.updateStructTableByFormId(sql, "FG_S_INVITEMMATERIAL_PIVOT", Arrays.asList("STATUS_ID"),
					materialId);
			
		}
		if (eventAction.equals("deleteSteps")) {
			String[] stepArray = elementValueMap.get("stepList").split(",");
			for (String stepId : stepArray) {
				String sql = "update FG_S_Step_PIVOT set EXPERIMENT_ID = '-1', ACTIVE = 0, STEPNAME  = 'Deleted("
						+ stepId + ")' where formid = '" + stepId + "'";
				formSaveDao.updateStructTableByFormId(sql, "FG_S_STEP_PIVOT",
						Arrays.asList("EXPERIMENT_ID", "STEPNAME", "ACTIVE"), stepId);

				sql = "update FG_S_ACTION_PIVOT set EXPERIMENT_ID = '-1', ACTIVE = 0 where step_id = '" + stepId + "'";
				formSaveDao.updateStructTable(sql, "FG_S_ACTION_PIVOT", Arrays.asList("EXPERIMENT_ID", "ACTIVE"),
						"step_id", stepId);

				sql = "update FG_SEQUENCE set FORMIDNAME= 'Deleted(" + stepId + ")' where id = '" + stepId + "'";
				formSaveDao.updateSingleStringInfo(sql);
				
				if(formCode.equals("ExperimentCP"))
				{
					//update RUNS table
					sql = "delete from fg_s_exprunplanning_pivot where stepid = '" + stepId + "'";
					formSaveDao.deleteStructTableByFormId(sql,"fg_s_exprunplanning_pivot","");
				}
				
				//for now, Active steps cannot be deleted.
				//If in the future active steps could be deleted, then theres a need to handle the descendant entities,
				// such as : selftest, workup, sample, request, batch
			}

			//renumerating the steps
			String sqlString = "select t.FORMID, dense_rank() over (partition by t.EXPERIMENT_ID order by t.FORMNUMBERID) as \"FORMNUMBERID\" from fg_s_step_v t"
					+ " where t.FORMNUMBERID is not null and experiment_id=" + formId
					+ " and t.ACTIVE = 1 order by experiment_id, t.FORMNUMBERID";
			List<Map<String, Object>> listOfMaps = generalDao.getListOfMapsBySql(sqlString);
			for (Map<String, Object> entry : listOfMaps) {
				sqlString = "update FG_S_STEP_PIVOT set FORMNUMBERID = '"
						+ (entry.get("FORMNUMBERID") != null
								? String.format("%02d", Integer.parseInt(entry.get("FORMNUMBERID").toString())) : "")
						+ "' where FORMID = " + entry.get("FORMID");
				formSaveDao.updateStructTableByFormId(sqlString, "FG_S_STEP_PIVOT", Arrays.asList("FORMNUMBERID"),
						entry.get("FORMID").toString());
			}
		}
		if (eventAction.equals("deleteSpreadsheetTemplate")) {
			String spreadsheetTemplate_id = generalUtil.getNull(elementValueMap.get("spreadsheet_id"));
			String sql = "update FG_S_spreadsheettempla_PIVOT set ACTIVE = 0 where formid = '" + spreadsheetTemplate_id + "'";
			formSaveDao.updateStructTableByFormId(sql, "FG_S_spreadsheettempla_PIVOT",
					Arrays.asList("ACTIVE"), spreadsheetTemplate_id);
		}
		if (eventAction.equals("depleteBatches")) {
			String[] batchArray = elementValueMap.get("batchList").split(",");
			for (String batchId : batchArray) {
				String sql = "update FG_S_INVITEMBATCH_PIVOT set INUSEDEPLETED = 'Depleted', QUANTITY = 0 where formid = '"
						+ batchId + "'";
				formSaveDao.updateStructTableByFormId(sql, "FG_S_INVITEMBATCH_PIVOT",
						Arrays.asList("INUSEDEPLETED", "QUANTITY"), batchId);
			}
		}
		if (eventAction.equals("validateOtherReportSameNameExists")) {
			StringBuilder sb = new StringBuilder();
			integrationValidation.validate(ValidationCode.VALIDATE_REPORTNAME_EXIST, formCode,
					elementValueMap.get("nameId"), new String(elementValueMap.get("reportName") + "," + userId+ "," + elementValueMap.get("parentFormCode")), sb);
			if (!sb.toString().isEmpty()) {
				return sb.toString();
			}
			return "1";
		}
		if(eventAction.equals("getReportNameId")){
			String reportName = elementValueMap.get("reportName");
			String nameId = generalDao
					.selectSingleStringNoException("select distinct save_name_id from fg_formlastsavevalue_name t"
							+ " where save_name = '" + reportName + "' and userid = '" + userId + "'");
			return nameId;
		}
		if (eventAction.equals("deleteReport")) {
			formSaveDao.updateSingleStringInfo("delete from fg_formlastsavevalue_name where save_name_id = '"
					+ elementValueMap.get("nameId") + "'");
			formSaveDao.updateSingleStringInfo(
					"delete from fg_formlastsavevalue where save_name_id = '" + elementValueMap.get("nameId") + "'");
			return "1";
		}


		if (eventAction.equals("deleteReportDesign")) {

			formSaveDao.updateSingleStringInfo(
					"delete from fg_s_reportDesignExp_pivot where formid = '" + elementValueMap.get("nameId") + "'");

			return "1";
		}


		if (eventAction.equals("getReportList")) {
			List<Map<String, Object>> reportList = generalDao
					.getListOfMapsBySql("select save_name_id as id,save_name as name, to_char(creation_date,'"+generalUtil.getConversionDateFormat()+"') as creation_date"
							+ " from fg_formlastsavevalue_name" + " where active=1" + " and userid='" + userId + "' and formCode_Name ='"+elementValueMap.get("formCodeElement")+"'");
			JSONArray reportlistJson = new JSONArray();
			for (Map<String, Object> reportData : reportList) {
				JSONObject data = new JSONObject();
				data.put("Id", reportData.get("id"));
				data.put("name", reportData.get("name"));
				data.put("creation_date", reportData.get("creation_date"));
				reportlistJson.put(data);
			}
			return reportlistJson.toString();
		}
		if (eventAction.equals("getFormCodeBySeqId")) {
			return formDao.getFormCodeBySeqIdNoException(formId);
		}

		if (eventAction.equals("AddExpSeriesIndex")) {
			formId = formSaveDao.getStructFormId("FormulationPropRef");
			String sessionId = generalUtilFormState.checkAndReturnSessionId("FormulationPropRef", parentId);
			String sql;

			String nextExperimentIndex = "";
			nextExperimentIndex = generalDao.selectSingleString(
					"select max(nvl(fg_get_numeric(t.EXPERIMENTINDEX),0)) from Fg_s_FormulationPropRef_All_v t where t.PARENTID='"
							+ parentId + "'");
			if (generalUtil.getNull(nextExperimentIndex).isEmpty()) {
				nextExperimentIndex = "1";
			} else {
				int intOffNextExperimentIndex;
				intOffNextExperimentIndex = Integer.parseInt(nextExperimentIndex);
				intOffNextExperimentIndex += 1;
				nextExperimentIndex = String.valueOf(intOffNextExperimentIndex);
			}
			//sql = "insert into FG_S_ExpInSeries_PIVOT (TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE,EXPINSERIESNAME) VALUES (SYSDATE,'" + userId + "',null,1,'"+formId+"','"+parentId+"','ExpInSeries','"+nextExperimentIndex+"')";
			//insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_EXPINSERIES_PIVOT", formId); //TODO Yaron - tmp data
			sql = "insert into FG_S_FORMULATIONPROPREF_PIVOT (TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE,EXPERIMENTINDEX,CREATED_BY,CREATION_DATE) VALUES (SYSDATE,'"
					+ userId + "','" + sessionId + "',1,'" + formId + "','" + parentId + "','FormulationPropRef','"
					+ nextExperimentIndex + "','" + userId + "',SYSDATE)";
			formSaveDao.insertStructTableByFormId(sql, "FG_S_FORMULATIONPROPREF_PIVOT", formId);
		}
		if (eventAction.equals("getDesignReportList")) {

			List<Map<String, Object>> reportList = generalDao
					.getListOfMapsBySql("select formid as id,reportDesignExpname as name"
							+ " from fg_s_reportDesignExp_all_v" + " where active=1 and CREATED_BY='" + userId + "'");
			JSONArray reportlistJson = new JSONArray();
			for (Map<String, Object> reportData : reportList) {
				JSONObject data = new JSONObject();
				data.put("Id", reportData.get("id"));
				data.put("name", reportData.get("name"));
				reportlistJson.put(data);
			}
			return reportlistJson.toString();
		}
		if(eventAction.equals("getSpreadsheetOfTemplate")){
			String spreadshhet_id = generalDao.selectSingleStringNoException("select t.SPREADSHEET \n"
					+ " from fg_s_spreadsheettempla_v t\n"
					+ " where t.SPREADSHEETTEMPLA_ID = '"+elementValueMap.get("SPREADSHEETTEMPLATE_ID")+"'");
			String elementData = generalUtilFormState.getStringContent(spreadshhet_id, formCode, "spreadsheetExcel", formId);
			JSONObject js = new JSONObject(elementData);
			String spreadsheetData="";
			try {
				JSONObject jsspreadsheetData = (JSONObject)js.get("excelFullData");
				spreadsheetData = jsspreadsheetData.toString();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return spreadsheetData;
		}
		if(eventAction.equals("getSpreadsheetOfExpFor")){
			String spreadshhet_id = generalDao.selectSingleStringNoException("select t.spreadsheetExcel \n"
					+ " from fg_s_experiment_v t\n"
					+ " where t.formid = '"+elementValueMap.get("EXPERIMENTFOR_ID")+"'");
			String elementData = generalUtilFormState.getStringContent(spreadshhet_id, formCode, "spreadsheetExcel", formId);
			JSONObject js = new JSONObject(elementData);
			String spreadsheetData="";
			try {
				JSONObject jsspreadsheetData = (JSONObject)js.get("excelFullData");
				spreadsheetData = jsspreadsheetData.toString();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return spreadsheetData;
		}

		if(eventAction.equals("updateSpreadsheetFavorite")){
			try {
				String sql = "";
				String isFavoriteChecked = generalUtil.getNull(elementValueMap.get("isFavoriteChecked"));
				String idObjCsv = elementValueMap.get("obj_id_csv");
				List<String> idList = Arrays.asList(idObjCsv.split(",", -1));
				
				//update DB
				if (isFavoriteChecked.equals("0")) {
					sql = "delete from fg_favorite where object_id in ("
							+ idObjCsv + ") and creator_id ='" + userId + "'";
				} else if (isFavoriteChecked.equals("1")) {
					sql =   "insert into fg_favorite t (object_id,creator_id)\r\n" +
							"select t.id_,t.userId_ from \r\n"
							+ "(SELECT regexp_substr('" + idObjCsv + "', '[^,]+', 1, commas.column_value) as id_, '" + userId + "' as userId_ \r\n" + 
							"FROM table(cast(multiset\r\n" + 
							"                  (SELECT LEVEL\r\n" + 
							"                   FROM dual CONNECT BY LEVEL <= LENGTH (regexp_replace('" + idObjCsv + "', '[^,]+')) + 1) AS sys.OdciNumberList)) commas) t\r\n" +
							" WHERE NOT EXISTS (SELECT * FROM fg_favorite WHERE object_id = t.id_ and creator_id = '" + userId + "')";
				}
				
				formSaveDao.updateSingleStringInfoNoTryCatch(sql);
				
				//update session list
				if (isFavoriteChecked.equals("0")) {
					for (String id_ : idList) {
						generalUtilFavorite.removeFromFavoriteList(id_);
					}
					
				} else {
					for (String id_ : idList) {
						generalUtilFavorite.addToFavoriteList(id_);
					}
				}
			} catch (Exception e) {
				generalUtilLogger.logWriter(LevelType.ERROR, "Problem with favorit update!",
						ActivitylogType.GeneralError,"-1",e,"-1");
				return "-1";
			}
		}
		if (eventAction.equals("validateOtherReportDesignSameNameExists")) {
			StringBuilder sb = new StringBuilder();
			integrationValidation.validate(ValidationCode.VALIDATE_REPORTDESIGNNAME_EXIST, formCode, "",
					new String(elementValueMap.get("reportName") + "," + userId), sb);
			if (!sb.toString().isEmpty()) {
				return sb.toString();
			}
			return "1";
		}
		if (eventAction.equals("PrametricCalculation")) {
			try {
				generalUtilConfig.SaveEventHandler(formCode, formId, elementValueMap, userId, "PreSave");
				String jsn = generalUtil.mapToJson(elementValueMap);
				return jsn;
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (eventAction.equals("MassBalanceCalc")) {
			integrationCalc.doCalc("MassBallanceCalc", "OnSave", "", elementValueMap, null, null, formCode, formId,
					userId);
			String jsn = generalUtil.mapToJson(elementValueMap);
			return jsn;
		}
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
		else if (eventAction.equals("RemoveExpSeriesIndex")) {

			formId = formSaveDao.getStructFormId("FormulationPropRef");
			String removeIndexId = elementValueMap.get("removeIndexId");
			String table = "FG_S_FORMULATIONPROPREF_PIVOT";
			if (!removeIndexId.isEmpty()) {
				formSaveDao.deleteStructTable(
						String.format("delete from %1$s t where t.FORMID= %2$s", table, removeIndexId), table, "FORMID",
						removeIndexId);
			}

		}

		else if (eventAction.equals("checkRemoveExpSeriesIndex")) {
			String removeIndexId = elementValueMap.get("removeIndexId");

			String result = generalDao.selectSingleString(String.format(
					"select count(*) from fg_s_experiment_all_v t where t.ORIGINFORMULANTPROPREF=%1$s", removeIndexId));
			//Return 1 if possible to remove , 0 if not possible.
			if (result.isEmpty() || result.equals("0")) {
				return "1";
			}
			return "0";
		} else if (eventAction.equals("deleteAction")) {
			String removeIdCsvList = elementValueMap.get("removeId");
			String deleteActOrderNumber = elementValueMap.get("orderNum");
			
			String[] removeIdList = removeIdCsvList.split(",");
			for(int i=0;i<removeIdList.length;i++){
				String removeId = removeIdList[i];

				if(deleteActOrderNumber.isEmpty()){
					deleteActOrderNumber = generalUtil.getNull(generalDao.selectSingleStringNoException("select formnumberid from fg_s_action_v where action_id = '"+removeId+"'"));
				}
				if(!deleteActOrderNumber.isEmpty()){
				String sql = "update FG_S_ACTION_PIVOT set active = 0, EXPERIMENT_ID = '-1', actionname = 'Deleted', STEP_ID = '-1' where formId = '"
						+ removeId + "'";
				formSaveDao.updateStructTableByFormId(sql, "FG_S_ACTION_PIVOT",
						Arrays.asList("ACTIVE", "EXPERIMENT_ID", "ACTIONNAME", "STEP_ID"), removeId);
				formSaveDao.updateSingleStringInfo("update FG_SEQUENCE set FORMIDNAME = 'Deleted' where id = '" + removeId + "'");
	
				String sql1 = "update FG_S_WORKUP_PIVOT set active = 0, EXPERIMENT_ID = '-1' where ACTION_ID = '" + removeId
						+ "'";
				formSaveDao.updateStructTable(sql1, "FG_S_WORKUP_PIVOT", Arrays.asList("ACTIVE", "EXPERIMENT_ID"),
						"ACTION_ID", removeId);

				String sql2 = "update FG_S_SELFTEST_PIVOT set active = 0, EXPERIMENT_ID = '-1' where ACTION_ID = '"
						+ removeId + "'";
				formSaveDao.updateStructTable(sql2, "FG_S_SELFTEST_PIVOT", Arrays.asList("ACTIVE", "EXPERIMENT_ID"),
						"ACTiON_ID", removeId);
	
				String wherePart = "";
				String setPart = "";
				
				wherePart = " formNumberId >'" + deleteActOrderNumber + "' and STEP_ID = '" + formId + "'";
				setPart = " REPLACE( TO_CHAR(FORMNUMBERID-1,'00'),' ','') ";
	
				formSaveDao.updateStructTable(
						String.format(" update FG_S_ACTION_PIVOT set FORMNUMBERID = %1$s  where %2$s ", setPart, wherePart),
						"FG_S_ACTION_PIVOT", Arrays.asList("FORMNUMBERID"), "STEP_ID", formId);
				} else {
					try {
						String sql = "delete from FG_S_ACTION_PIVOT where formid = '" + removeId + "'";
						formSaveDao.deleteStructTableByFormId(sql, "FG_S_ACTION_PIVOT",
								removeId);
					} catch (Exception e) {
						e.printStackTrace();

					}
				}
			}

		}
		//		else if(eventAction.equals("removeFormulant")||eventAction.equals("removeProductMixture")) 
		//		{
		//			formId = formSaveDao.getStructFormId("FormulantRef");
		//			String removeId = elementValueMap.get("removeId"); 
		//			String table = "FG_S_FormulantRef_PIVOT";
		//			formSaveDao.deleteStructTable(String.format("delete from %1$s t where t.FORMID= %2$s", table, removeId),table, "FORMID", removeId);
		//			
		////		
		//			
		//		}
		else if(eventAction.equals("checkForPlannedTemplateAndUpdate")){
			String sql = "select distinct listagg(formid,',') within group(order by formid desc) PLANNED_TEMPLATE"
					+ " from fg_s_template_all_v"
					+ " where SOURCEEXPNO_ID = '"+elementValueMap.get("SOURCEEXPNO_ID")+"'"
					+ " and TEMPLATESTATUSNAME = 'Planned'";
			String plannedTemplateList = generalDao.selectSingleStringNoException(sql);
			if(generalUtil.getNull(plannedTemplateList).isEmpty()){
				return generalEvent(stateKey, elementValueMap, formCode, formId, userId, isNew, "updateTemplateVersion");
			} else {
				return "PLANNED:"+plannedTemplateList;
			}
			
		}

		else if (eventAction.equals("updateTemplateVersion")) {
			//first, obsolete all the 'planned version' for this template 
			if(elementValueMap.containsKey("plannedTempLst") && !elementValueMap.get("plannedTempLst").isEmpty()){
				String statusId = formDao.getFromInfoLookup("TEMPLATESTATUS", LookupType.NAME, "Obsolete", "id");
				String[] plannedTempLst = elementValueMap.get("plannedTempLst").split(",");
				for(String tempformId:plannedTempLst){
					String sql = "update fg_s_template_pivot set STATUS_ID = '"+statusId+"'"
							+ " where formid  = '"+tempformId+"'";
					formSaveDao.updateStructTableByFormId(sql, "fg_s_template_pivot", Arrays.asList("STATUS_ID"), tempformId);
				}	
			}
			//second, create a new version for current template.
			//get table name
			Form form = formDao.getFormInfoLookup(formCode, "%", true).get(0);
			String formCodeEntity = generalUtil.getNull(form.getFormCodeEntity(), formCode);
			String table = "FG_S_" + formCodeEntity + "_PIVOT";

			//Update status to Planned				
			String statusId = formDao.getFromInfoLookup("TEMPLATESTATUS", LookupType.NAME, "Planned", "id");

			//Update template version
			//Map<String, String> formVal = formDao.getFromInfoLookupAll(formCodeEntity, LookupType.ID, formId);
			//Integer version = Integer.valueOf(formVal.get("TEMPLATEVERSION")) + 1;
			String sql = "select max(to_number(TEMPLATEVERSION))"
					+ " from fg_s_template_v"
					+ " where SOURCEEXPNO_ID = '"+elementValueMap.get("SOURCEEXPNO_ID")+"'";
			Integer version = Integer.valueOf(generalDao.selectSingleStringNoException(sql))+1;
			String templateVersion = (version < 10) ? "0" + version.toString() : version.toString();

			//Update creation date
			DateFormat dateFormat = new SimpleDateFormat(generalUtil.getConversionDateFormat());
			Date date = new Date(); //current date for necessary cases

			//create planned form - Clone
			String cloneFormId = formSaveDao.cloneStructTable(formId);
			String sql_ = String.format(
					" update %1$s set  STATUS_ID = '%2$s', TEMPLATEVERSION = '%3$s', APPROVER = null, CREATIONDATE = '%4$s',ACTIVE = '1'  where FORMID =  %5$s",
					table, statusId, templateVersion, dateFormat.format(date), cloneFormId);
			List<String> colList = Arrays.asList("STATUS_ID", "TEMPLATEVERSION", "APPROVER", "CREATIONDATE");
			formSaveDao.updateStructTableByFormId(sql_, table, colList, cloneFormId);

			String expTemplateSelectFormId = generalDao.selectSingleString(
					"select distinct t.FORMID from FG_S_EXPTEMPLATESELECT_ALL_V t where parentid = '" + formId + "'");
			;
			String expTempcloneFormId = formSaveDao.cloneStructTable(expTemplateSelectFormId);
			sql_ = String.format(
					" update FG_S_EXPTEMPLATESELECT_PIVOT set  PARENTID = '%1$s', ACTIVE = 1 where FORMID =  %2$s",
					cloneFormId, expTempcloneFormId);
			colList = Arrays.asList("PARENTID", "ACTIVE");
			formSaveDao.updateStructTableByFormId(sql_, "FG_S_EXPTEMPLATESELECT_PIVOT", colList, expTempcloneFormId);

			return cloneFormId;

		}

		else if (eventAction.equals("isColumnInUse")) {
			String query = "Select count(*) from FG_S_EXPERIMENT_ALL_V e,FG_S_COLUMNSELECT_ALL_V c,FG_S_EXPERIMENTSTATUS_ALL_V s"
					+ " where c.PARENTID = e.EXPERIMENT_ID" + " and e.STATUS_ID = s.EXPERIMENTSTATUS_ID(+)"
					+ " and (s.EXPERIMENTSTATUSNAME = 'Active' or s.EXPERIMENTSTATUSNAME = 'Planned')"
					+ " and c.INVITEMCOLUMN_ID ='" + formId + "'" + " and c.SESSIONID is null";
			String isColumnInUse = generalDao.selectSingleString(query);
			return isColumnInUse != null ? generalUtil.getEmpty(isColumnInUse, "0") : "0";

		} else if (eventAction.equals("STATUS_WF_LIST_INFO")) {
			String toReturn = generalUtil
					.getNull(generalUtilFormState.getFormParam(stateKey, formCode).get("$P{STATUS_WF_LIST_INFO}"));
			return toReturn;
		} else if (eventAction.equals("STEPS_WF_LIST_INFO")) {
			String toReturn = generalUtil
					.getNull(generalUtilFormState.getFormParam(stateKey, formCode).get("$P{STEPS_WF_LIST_INFO}"));
			return toReturn;
		} else if (eventAction.equals("addMaterialToMotherLiquerTable")) {
			//String batchId = generalUtil.getJsonValById(elementValueMap.get("toReturn"), "Batch id");
			String batchId = elementValueMap.get("batchId");
			String sql = "delete from FG_S_WuCryMixDefineRef_PIVOT t where CREATEDBYUSER = 1  and parentid= " + formId
					+ "and TABLETYPE = 'MotherLiquor'";
			formSaveDao.deleteStructTable(sql, "FG_S_WuCryMixDefineRef_PIVOT", "parentid", formId);
			/*
			 * "delete from FG_S_WuCryMixDefineRef_PIVOT t where CREATEDBYUSER = 1  and parentid= " + formId
			 * + "and TABLETYPE = 'MotherLiquor'");
			 *///ta 290718 changed for search

			if (!generalUtil.getNull(batchId).isEmpty()) {
				//String sampleId = generalDao.selectSingleString("select b.SAMPLE_ID from fg_s_invitembatch_all_v b where b.INVITEMBATCH_ID ='"+ batchId+"'");
				List<String> sampleIds = generalDao
						.getListOfStringBySql("select t.sample_id  from FG_s_sample_V t where BATCH_ID = " + batchId
								+ " and sessionid is null");

				for (String sampleId : sampleIds) {

					List<Map<String, Object>> results = generalDao.getListOfMapsBySql(
							"select  t.RESULT_NAME, t.RESULT_MATERIAL_ID, t.RESULT_VALUE, t.RESULT_UOM_ID,t.RESULT_ID from FG_I_SELECTEDRESULTS_V t where t.RESULT_TEST_NAME = 'Analytical' and t.SAMPLE_ID ="
									+ sampleId);
					if (results != null && results.size() > 0) {

						commonFunc.setMaterialsInWorkup(results, "MotherLiquor", "WuCryMixDefineRef", "CONCENTRATION",
								formId, userId);
					}

					formDao.insertToSelectTable("SampleSelect", formId, "SAMPLETABLE", Arrays.asList(sampleId), true,
							userId,null);
				}
			}

		} else if (eventAction.equals("checkIfExperimentHasAllSelfTests")) {
			String toReturn = "";
			//not in use
//			String experimentID = formCode.equals("StepFr")?elementValueMap.get("EXPERIMENT_ID"):formId;
//			String sql = "select count(t.STEP_ID) \n" + " from fg_s_stepfr_all_v t, fg_s_stepstatus_all_v s \n"
//					+ " where t.STATUS_ID = s.FORMID \n"
//					+ " and t.STATUS_ID not in (select t1.FORMID from FG_S_STEPSTATUS_ALL_V t1 where t1.STEPSTATUSNAME in ('Finished','Cancelled')) \n"
//					+ " and t.STEP_ID != '%1$s' \n" 
//					+ " and t.EXPERIMENT_ID = '%2$s' \n";
//			String notFinishedStepsCount = generalUtil
//					.getNull(generalDao.selectSingleString(String.format(sql, formId, experimentID)));
//			if (!formCode.equals("StepFr") || notFinishedStepsCount.equals("0")) {//experiment turned to completed or all the steps are finished
//				sql = "select listagg(selftesttypename,',<br>') within group (order by selftesttypename) selftesttypes_list \n"
//						+ " from ( \n" + " select s.selftesttypename \n"
//						+ " from fg_s_project_all_v t, fg_s_experiment_all_v t1, fg_s_selftesttype_all_v s \n"
//						+ " where t.project_id = t1.project_id \n"
//						+ " and instr(','||s.FORMULATIONTYPE_ID||',', ','||t.FORMULATIONTYPE_ID||',') > 0 \n"
//						+ " and t1.formid = '%1$s' \n" + " minus \n" + " select s1.selftesttypename \n"
//						+ " from fg_s_selftest_all_v s1 \n"
//						+ " where s1.selftesttypename in (select s.selftesttypename \n"
//						+ " from fg_s_project_all_v t, fg_s_experiment_all_v t1, fg_s_selftesttype_all_v s \n"
//						+ " where t.project_id = t1.project_id \n"
//						+ " and instr(','||s.FORMULATIONTYPE_ID||',', ','||t.FORMULATIONTYPE_ID||',') > 0 \n"
//						+ " and t1.formid = '%1$s') \n" + " and s1.experiment_id = '%1$s')";
//				String list = generalUtil.getNull(generalDao.selectSingleString(String.format(sql, experimentID)));
//				if (!list.equals("")) {
//					Object[] criteriaInfoArray = new String[1];
//					criteriaInfoArray[0] = list;
//					toReturn = generalUtil.getSpringMessagesByKey("confirmSelfTestsNotPerformed", criteriaInfoArray,
//							"");
//				}
//			}
			return toReturn;
		} else if (eventAction.equals("requestSaveAndClose")) {
			String sampleList = generalUtil.getNull(elementValueMap.get("SMARTSELECTLIST")).isEmpty() ? "''"
					: elementValueMap.get("SMARTSELECTLIST");
			String sql = "select count(*) from fg_s_sampledata_dtPlanned_v where 1=1 and sample_id in (" + sampleList
					+ ") ";
			String toReturn = generalUtil.getNull(generalDao.selectSingleString(sql), "0");
			return toReturn;
		} else if (eventAction.equals("updateRequestStatus")) {
			String retVal;
			try {
				String status_id = formDao.getFromInfoLookup("requeststatus", LookupType.NAME, "Waiting", "id");
				String sql = "update fg_s_request_pivot t set t.requeststatus_id = '" + status_id
						+ "' where t.formid = '" + formId + "'";
				retVal = formSaveDao.updateStructTableByFormId(sql, "fg_s_request_pivot",
						Arrays.asList("REQUESTSTATUS_ID"), formId);
				///
				String operationTypeCount = generalUtil.getEmpty(
						generalDao.selectSingleString("select count(*) from fg_s_operationtype_all_v where parentid = '"
								+ formId + "' and sessionid is null"),
						"0");
				if (retVal.equals("1")) {
					if (Integer.parseInt(operationTypeCount) > 1) {
						List<String> operationTypeIdList = generalDao.getListOfStringBySql(
								"select t.formId from FG_S_OPERATIONTYPE_ALL_V t where t.PARENTID='" + formId + "'");

						List<String> sampleIdList = generalDao.getListOfStringBySql(
								"select distinct t.formId from Fg_s_SampleDataRef_All_v t  where t.PARENTID='" + formId
										+ "' "+generalUtilFormState.getWherePartForTmpData("SampleDataRef", formId));
						String experimentSelectId = generalDao.selectSingleString(
								"select distinct t.formId from Fg_s_experimentselect_pivot t where t.PARENTID='"
										+ formId + "' and sessionid is null and nvl(active,'1')='1'");

						commonFunc.splitSaveRequestEvent(formId, operationTypeIdList, userId,
								new ArrayList<String>(), new ArrayList<String>(), sampleIdList, "", "",
								experimentSelectId);

						//runs through all the requestselect in which the splitted request was added and replace them with their descendants
						List<String> requestdescendants = generalDao.getListOfStringBySql(
								"select formid from fg_s_request_pivot where PARENTREQUESTID = '" + formId + "'");
						List<String> requestUsedinSelectIds = generalDao.getListOfStringBySql(
								"select formid from fg_s_requestselect_pivot where instr(','||request_id||',',','||"
										+ formId + "||',')>0");
						for (String selectId : requestUsedinSelectIds) {
							sql = "update FG_S_REQUESTSELECT_PIVOT  set REQUEST_ID = SUBSTR(REGEXP_REPLACE(','||request_id||',',','||"
									+ formId + "||','," + "',"
									+ generalUtil.replaceLast(requestdescendants.toString(), "]", "")
											.replaceFirst("\\[", "").replaceAll("\\s*", "")
									+ ",'),2," + "LENGTH(SUBSTR(REGEXP_REPLACE(','||request_id||',',','||" + formId
									+ "||','," + "',"
									+ generalUtil.replaceLast(requestdescendants.toString(), "]", "")
											.replaceFirst("\\[", "").replaceAll("\\s*", "")
									+ ",'),2))-1) where formid = '" + selectId + "'";
							formSaveDao.updateStructTableByFormId(sql, "FG_S_REQUESTSELECT_PIVOT",
									Arrays.asList("REQUEST_ID"), selectId);
						}
					}

					String table = "FG_S_Request_PIVOT";
					List<String> colList = new ArrayList<String>();
					colList.add("isSplit");

					String setAdditionColumns = "";
					String formNumberId = formDao.getFromInfoLookup("request", LookupType.ID, formId, "formNumberId");
					if (Integer.parseInt(operationTypeCount) == 1 && !generalUtil.getNull("formNumberId").isEmpty()) {//if it has a single operation then updates its number to get the suffix 'OP1'
						//Make the whole requestNumber

						String requestNumber = formNumberId + "-OP1";

						//updates the request columns
						colList.add("requestName");
						setAdditionColumns = ",requestName = '" + requestNumber + "'";
					} else if (Integer.parseInt(operationTypeCount) > 1) {
						colList.add("isInUse");
						setAdditionColumns = ",isInUse = '0'";
					}
					String sql_ = "update " + table + " set isSplit = '1'" + setAdditionColumns + " where FORMID = '"
							+ formId + "'";
					formSaveDao.updateStructTableByFormId(sql_, table, colList, formId);
				}
				//
			} catch (Exception e) {
				retVal = "-1";
				e.printStackTrace();

			}
			return retVal;
		} else if (eventAction.equals("deleteSampleFromRequest")) {
			String sql_ = String.format("delete from FG_S_SampleDataRef_PIVOT where parentid ='%1$s'",
						 formId );
				formSaveDao.deleteStructTableByFormId(sql_, "FG_S_SampleDataRef_PIVOT", "");
		}else if (eventAction.equals("checkIfRequestHasSample")) {
			String sql = "";
			String parentRequestId = elementValueMap.get("parentId");
			String parentFormCode = formDao.getFormCodeBySeqId(parentRequestId);
			String toReturn = "";
			if (generalUtil.getNull(parentFormCode).equals("Action")) {
				if(generalUtil.getNull(elementValueMap.get("ACTION_SAMPLE_ID")).isEmpty()){
					String sample_cnt = generalDao.selectSingleStringNoException("select count(*) from fg_s_sampledataref_v where 1=1 and PARENTID='" + formId + "'"
				              + generalUtilFormState.getWherePartForTmpData("SampleDataRef", formId));
					if(generalUtil.getNull(sample_cnt).isEmpty()){
						toReturn = "0";
					}else if(generalUtil.getNull(sample_cnt).equals("1")){
						String singleSampleId = generalDao.selectSingleStringNoException("select distinct sampleid from fg_s_sampledataref_v where 1=1 and PARENTID='" + formId + "' " 
								 + generalUtilFormState.getWherePartForTmpData("SampleDataRef", formId));
						toReturn = "1"+";"+singleSampleId;
					}else{
						toReturn = sample_cnt;
					}
				}
				else{
					String sql_ = String.format("delete from FG_S_SampleDataRef_PIVOT where sampleid <> '%1$s' and parentid ='%2$s'",
							elementValueMap.get("ACTION_SAMPLE_ID"), formId );
					formSaveDao.deleteStructTableByFormId(sql_, "FG_S_SampleDataRef_PIVOT", "");
					toReturn = "1";
				}
			}
			else {
				sql = "select count(*) from fg_s_sampledataref_dt_v where 1=1 and PARENTID='" + formId + "' "
						+ generalUtilFormState.getWherePartForTmpData("SampleDataRef", formId);
				toReturn = generalUtil.getNull(generalDao.selectSingleString(sql), "0");
			}
			return parentFormCode +";"+toReturn;
		} else if (eventAction.equals("checkIfNewFormHasValidation")) {
			if(generalUtil.getNull(elementValueMap.get("newFormCode")).equals("Request (Copy Default)")){
				elementValueMap.put("newFormCode","Request");
			}
			//select validation part from json
			if (!formCode.equals("undefined") && !generalUtil.getNull(formCode).isEmpty()) {
				String validation = integrationWFAdamaImp.getValidationPartFromJson(formCode,
						elementValueMap.get("newFormCode"));
				StringBuilder sb = new StringBuilder();
				if (!generalUtil.getNull(validation).isEmpty()) {
					ValidationCode valid = ValidationCode.valueOf(validation);
					if (valid != null && !valid.equals("")) {
						try {
							integrationValidation.validate(valid, formCode, formId,
									generalUtil.getNull(elementValueMap.get("parentIdList")), sb);
							return sb.toString().isEmpty() ? "" : "1," + sb.toString();
						} catch (Exception e) {
							e.printStackTrace();
							return "2," + e.getMessage();
						}
					}

				}
			}

		} else if (eventAction.equals("CloneExperiment")) {
			String cloneExperimentId = formSaveDao.getStructFormId("EXPERIMENT");
			return cloneExperimentId;
		}

		else if (eventAction.equals("saveQuickAction")) {
			/*
			 * elementValueMap = initElementValueMapByDataBeanList(List<DataBean> dataBeanList,
			 * List<String> preventSaveElementList, List<DataBean> additinalDataSaveList,
			 * Map<String,String> elementAdditinalDataoMap, Map<String, ElementUIKeyValueDisplay> elementUIKeyValueDisplayMap,
			 * String formCode, String formId)
			 */
			String observationIdRichtext = formSaveElementDao.saveRichText(formCode,
					new DataBean("observation", elementValueMap.get("observation"), BeanType.CLOB, ""), true);
			boolean canNewByList = false;
			String toReturn;
			try {
				parentId = elementValueMap.get("STEP_ID");
				//validates it's allowed to create the current action
				List<String> newAvailableList = Arrays
						.asList(commonFunc.getNewAvailableFormListById(stateKey, parentId, "Action"));
				for (String newCode : newAvailableList) {
					if (newCode.equals("Action")) {
						canNewByList = true;
						break;
					}
				}
				if (!canNewByList) {//cannot create the current action because of permission/WF
					//throws an error message
					//					String stepFormCode = formDao.getFormCodeBySeqId(parentId);
					//					String wfList = generalUtil.getNull(
					//							generalUtilFormState.getFormParam(stateKey, stepFormCode).get("$P{STEPS_WF_LIST_INFO}"));
					String message = generalUtil.getSpringMessagesByKey("Warning,Creation of Action is not allowed",
							"");
					throw new Exception(message);
				}

				formId = formSaveDao.getStructFormId("Action");
				String sessionId = generalUtilFormState.checkAndReturnSessionId("Action", parentId);
				String nextFormNumberId = formIdCalc.getNextFormNumberIdByFormId("Action", formId, elementValueMap,
						userId, "FG_S_ACTION_PIVOT");//gets formnumberId

				//insert the new action
				String sql = "insert into FG_S_ACTION_PIVOT"
						+ " (CREATION_DATE,CREATED_BY,TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,FORMCODE,FORMCODE_ENTITY,ACTIONNAME,OBSERVATION,STARTTIME,STEP_ID,EXPERIMENT_ID,FORMNUMBERID,STARTDATE,ENDDATE)"
						+ " VALUES (SYSDATE,'" + userId + "',SYSDATE,'" + userId + "'," + sessionId + ",1,'" + formId
						+ "','Action','Action','" + elementValueMap.get("actionName") + "', '" + observationIdRichtext //remove start time - ta 030119 task 22995
						+ "','" + elementValueMap.get("time") + "','" + elementValueMap.get("STEP_ID") + "','"
						+ elementValueMap.get("EXPERIMENT_ID") + "','" + nextFormNumberId + "',to_char( sysdate, '"
						+ generalUtil.getConversionDateFormat() + "'),to_char( sysdate, '"
						+ generalUtil.getConversionDateFormat() + "'))";
				toReturn = formSaveDao.insertStructTableByFormId(sql, "FG_S_ACTION_PIVOT", formId);
				formSaveElementDao.addMonitorParam(formId, "Action");//insertParamMonitoring
				if (!toReturn.equals("0")) {//if the row was added
					toReturn = formId;
					canNewByList = false;
					if (!elementValueMap.get("newFormCode").isEmpty()) {
						//validates it's allowed to create the current action
						newAvailableList = Arrays.asList(
								commonFunc.getNewAvailableFormListById(stateKey, formId, elementValueMap.get("newFormCode")));
						for (String newCode : newAvailableList) {
							if (newCode.equals(elementValueMap.get("newFormCode"))) {
								canNewByList = true;
								break;
							}
						}
						if (canNewByList) {//can create the next formCode
							//push action into the stack
							String url = "init.request?stateKey=" + stateKey + "&formCode=Action&formId=" + formId
									+ "&userId=" + userId + "&avoidBreadCrumb=1" //TODO innerinitid check
									+ "&PARENT_ID=" + parentId;
							generalUtilFormState.pushIntoBackNavigationStack(stateKey, formId, "Action", "", "", url,
									userId);
						} else {
							//							String wfList = generalUtil.getNull(generalUtilFormState.getFormParam(stateKey, "Action")
							//									.get("$P{STEPS_WF_LIST_INFO}"));
							String message = generalUtil.getSpringMessagesByKey("Warning_WF_Action,Creation of "
									+ elementValueMap.get("newFormCode") + " is not allowed.", "");
							throw new Exception(message);
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				toReturn = commonFunc.doSaveOnException(e, formId, formCode).getErrorMsg();
			}
			return toReturn;
		} else if (eventAction.equals("saveCasObjToLog")) {
			/*
			 * kd 25112019 save cas log
			 */
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
//			String quantityOfRuns =  readAllBytesJava7("c:/tmp/1.txt"); //elementValueMap.get("quantityOfRuns").toString();
//			JsonElement je = jp.parse(generalUtil.mapToString("CAS RESULT", elementValueMap).replace("'", "''"));
			JsonElement je = jp.parse(elementValueMap.get("quantityOfRuns").toString());
//			JsonElement je = jp.parse(quantityOfRuns);
			String prettyJsonString = gson.toJson(je);
			
//			String niceFormattedJson = JsonWriter.formatJson(quantityOfRuns);
			
			
			String objectId = uploadFileDao.saveStringAsClobRenderId("CASResultLogReport", prettyJsonString);
			String sql = "insert into FG_CHEM_CAS_API_LOG (TIME_STAMP,user_id,material_form_id,resultid) VALUES (SYSDATE,'"
//					+ userId + "','" + formId + "','" + (generalUtil.mapToString("CAS RESULT", elementValueMap).replace("'", "''")) + "')";
					+ userId + "','" + formId + "','" + objectId + "')";
			String toReturn = "";
			try {
				toReturn = formSaveDao.updateSingleStringInfo(sql);
			} catch (Exception e) {
				try {
					generalUtilLogger.logWriter(LevelType.ERROR, "Problem with saveCasObjToLog! sql=" + sql,
							ActivitylogType.GeneralError,"-1",e,"-1");
				} catch (Exception e1) {
					generalUtilLogger.logWrite(e1);
				}
			}
			return toReturn;	
		} else if (eventAction.equals("saveCasObjToLogNotConvertMapToString")) {
			/*
			 * kd 25112019 save cas log
			 */
			String objectId = uploadFileDao.saveStringAsClobRenderId("CASResultLogReport", generalUtil.replaceDBUpdateVal(generalUtil.mapToString("CAS RESULT", elementValueMap)));
			String sql = "insert into FG_CHEM_CAS_API_LOG (TIME_STAMP,user_id,material_form_id,resultid) VALUES (SYSDATE,'"
//					+ userId + "','" + formId + "','" + (generalUtil.mapToString("CAS RESULT", elementValueMap).replace("'", "''")) + "')";
					+ userId + "','" + formId + "','" + objectId + "')";
			String toReturn = "";
			try {
				toReturn = formSaveDao.updateSingleStringInfo(sql);
			} catch (Exception e) {
				try {
					generalUtilLogger.logWriter(LevelType.ERROR, "Problem with saveCasObjToLog! sql=" + sql,
							ActivitylogType.GeneralError,"-1",e,"-1");
				} catch (Exception e1) {
					generalUtilLogger.logWrite(e1);
				}
			}
			return toReturn;
		} else if (eventAction.equals("saveCasObjToLogNotConvert")) {
			/*
			 * kd 25112019 save cas log
			 */
			String objectId = uploadFileDao.saveStringAsClobRenderId("CASResultLogReport", elementValueMap.get("quantityOfRuns").toString());
			String sql = "insert into FG_CHEM_CAS_API_LOG (TIME_STAMP,user_id,material_form_id,resultid) VALUES (SYSDATE,'"
					+ userId + "','" + formId + "','" + objectId + "')";
			String toReturn = "";
			try {
				toReturn = formSaveDao.updateSingleStringInfo(sql);
			} catch (Exception e) {
				try {
					generalUtilLogger.logWriter(LevelType.ERROR, "Problem with saveCasObjToLog! sql=" + sql,
							ActivitylogType.GeneralError,"-1",e,"-1");
				} catch (Exception e1) {
					generalUtilLogger.logWrite(e1);
				}
			}
			return toReturn;
		} else if (eventAction.equals("createNewForm")) {
			String toReturn = formId;
			boolean canNewByList = false;
			try {
				if (!elementValueMap.get("elementFormCode").isEmpty()) {
					//validates it's allowed to create the current action
					if(generalUtil.getNull(elementValueMap.get("elementFormCode")).equals("Request (Copy Default)")){
						String project_id = formDao.getFromInfoLookup(formCode, LookupType.ID,
								formId, "PROJECT_ID");
						String defaultRequest = generalDao.selectSingleStringNoException("select max(t.USEASDEFAULTDATA) from FG_S_REQUEST_V t where t.PROJECT_ID = '"+generalUtil.getNull(project_id)+"' and t.CREATOR_ID = '"+userId+"'");
						if(!generalUtil.getNull(defaultRequest).equals("1")){
							String message = generalUtil.getSpringMessagesByKey(
									"Warning_WF_Action,Default request does not exist for specific project and user.</br>Creation of Request (Copy Default) is not allowed.", "");

							throw new Exception(message);
						}
						elementValueMap.put("elementFormCode", "Request");
					}
					if (!elementValueMap.get("elementFormCode").equals("Document")) {
						
						List<String> newAvailableList = Arrays.asList(
								commonFunc.getNewAvailableFormListById(stateKey, formId, elementValueMap.get("elementFormCode")));
						for (String newCode : newAvailableList) {
							if (newCode.equals(elementValueMap.get("elementFormCode"))) {
								canNewByList = true;
								break;
							}
						}
					} else {
						canNewByList = true;
					}
					if (!canNewByList) {
						String formCodeName=generalUtil.getNull(elementValueMap.get("elementFormCode"));
						switch (formCodeName) {
						case "SelfTestMain":
							formCodeName="SelfTest";
							break;
						case "InvItemMaterialFr":
							formCodeName="Formulation Material";
							break;
						case "InvItemMaterialPr":
							formCodeName="Premix Material";
							break;
						
						default:
							break;
						}
						// formCodeName = generalUtil.getNull(elementValueMap.get("elementFormCode"))
								//.equals("SelfTestMain") ? "SelfTest" : elementValueMap.get("elementFormCode");
						String message = generalUtil.getSpringMessagesByKey(
								"Warning_WF_Action,Creation of " + formCodeName + " is not allowed.", "");
						throw new Exception(message);

					}
				}
			} catch (Exception e) {
				toReturn = commonFunc.doSaveOnException(e, formId, formCode).getErrorMsg();
				if(!e.getMessage().contains("Warning_WF")) {
					throw new Exception(toReturn);
				}
			}
			return toReturn;

		} else if(eventAction.equals("openSearchForm")){
			return "1";
		} else if(eventAction.equals("openPopupForm")){
			return "1";
		} else if(eventAction.equals("doSavePurityList")){
			JSONObject materialPurityJson = new JSONObject();
			for(Map.Entry<String, String> materialPurityPair:elementValueMap.entrySet()){
				String name = materialPurityPair.getKey();
				String value = materialPurityPair.getValue();
				if(name.startsWith("material_id_")){
					String material_id = name.substring(new String("material_id_").length());
					materialPurityJson.put(material_id, value);
				}
			}
			String composition_id = elementValueMap.get("parentId");
			String experiment_id = generalDao.selectSingleStringNoException("select distinct parentid from fg_s_composition_v\n"
					+ " where formid = '"+composition_id+"'\n"
					+ " and active =1\n");
			integrationDTAdamaImp.onChangeDataTableCell(stateKey, "ExperimentFor", experiment_id, "Composition", composition_id, userId
					, "PURITY", materialPurityJson.toString(), "", "", "");
		} else if (eventAction.equals("deleteTableRow")) {
			String retVal;
			try {
				String sql = "delete from FG_S_" + formCode + "_PIVOT where formid = '" + formId + "'";
				retVal = formSaveDao.deleteStructTableByFormId(sql, "FG_S_" + formCode.toUpperCase() + "_PIVOT",
						formId);
			} catch (Exception e) {
				retVal = "-1";
				e.printStackTrace();

			}
			return retVal;
		} else if (eventAction.equals("copyExpFormulationData")) {
			String table = "";
			Map<String, String> replaceFieldsMap;
			String sql = "";

			//FG_S_BATCHSELECT_PIVOT
			// not need to delete this TODO in cleanup job

			//FG_S_FORMULANTREF_PIVOT
			table = "FG_S_FORMULANTREF_PIVOT";
			//..delete from step
			formSaveDao.deleteStructTable(String.format("delete from %1$s t where t.PARENTID = '%2$s'", table, formId),
					table, "PARENTID", formId);

			//..insert (using clone) with FG_S_BATCHSELECT_PIVOT data
			List<String> formulantIdExpList = generalDao
					.getListOfStringBySql("Select formid from FG_S_FORMULANTREF_PIVOT where PARENTID = '"
							+ elementValueMap.get("EXPERIMENT_ID") + "' and sessionid is null and active = 1");
			for (String formulantIdExp : formulantIdExpList) {
				//clone FG_S_FORMULANTREF_PIVOT and get new ID (newFormIdMaterialRef)
				replaceFieldsMap = new HashMap<String, String>();
				replaceFieldsMap.put("PARENTID", formId);
				replaceFieldsMap.put("ACTIVE", "1");
				String newFormIdMaterialRef = formSaveDao.cloneStructTable(formulantIdExp, replaceFieldsMap,null);
				sql = "update FG_S_FORMULANTREF_PIVOT set active = 1 where formId = '" + newFormIdMaterialRef + "'";
				formSaveDao.updateStructTableByFormId(sql, "FG_S_FORMULANTREF_PIVOT", Arrays.asList("ACTIVE"), formId);

				//get the (single) FG_S_BATCHSELECT_PIVOT in experiment
				List<String> batchSelectId = generalDao
						.getListOfStringBySql("Select formid from FG_S_BATCHSELECT_PIVOT where PARENTID = '"
								+ formulantIdExp + "'  and sessionid is null and active = 1");
				// if exists copy it to the new material ref created in the step
				if (batchSelectId.size() > 0) {
					replaceFieldsMap = new HashMap<String, String>();
					replaceFieldsMap.put("PARENTID", newFormIdMaterialRef);
					replaceFieldsMap.put("ACTIVE", "1");
					String newFormIdBatchSelect = formSaveDao.cloneStructTable(batchSelectId.get(0), replaceFieldsMap,null);
					sql = "update FG_S_BATCHSELECT_PIVOT set active = 1 where formId = '" + newFormIdBatchSelect + "'";
					formSaveDao.updateStructTableByFormId(sql, "FG_S_BATCHSELECT_PIVOT", Arrays.asList("ACTIVE"),
							formId);
				}
			}

			//Formulation WEBIX
			//clean result
			//			sql = String.format("update FG_S_STEP_PIVOT set webixExpStep = null where formid = '%1$s'",formId);
			//			formSaveDao.updateStructTableByFormId(sql, "FG_S_STEP_PIVOT", Arrays.asList("webixExpStep"), formId);
			List<String> webixExpValList = generalDao
					.getListOfStringBySql("Select webixFormulCalc from FG_S_EXPERIMENT_PIVOT where formId = '"
							+ elementValueMap.get("EXPERIMENT_ID") + "'");
			String webixExpVal = "0";
			if (webixExpVal != null && webixExpValList.size() > 0) {
				webixExpVal = generalUtil.getNull(webixExpValList.get(0));
				sql = String.format("update FG_S_STEP_PIVOT set webixExpStep = '%1$s' where formid = '%2$s'",
						webixExpVal, formId);
				formSaveDao.updateStructTableByFormId(sql, "FG_S_STEP_PIVOT", Arrays.asList("webixExpStep"), formId);
			}

			//delete and update webix output
			sql = "delete from FG_WEBIX_OUTPUT t where t.step_id = '" + formId + "'";
			formSaveDao.updateSingleStringInfoNoTryCatch(sql);
			replaceFieldsMap = new HashMap<String, String>();
			replaceFieldsMap.put("STEP_ID", formId);
			replaceFieldsMap.put("RESULT_DATE", "sysdate");
			replaceFieldsMap.put("RESULT_TIME", "to_char( sysdate, 'HH24:MI:SS' ) ");
			replaceFieldsMap.put("WEBIX_CHANGE_BY", userId);
			replaceFieldsMap.put("RESULT_ID", getResultIdbyTable("FG_WEBIX_OUTPUT"));
			generalDao.cloneTable("FG_WEBIX_OUTPUT",
					" experiment_id = '" + elementValueMap.get("EXPERIMENT_ID") + "' and STEP_ID is null ",
					replaceFieldsMap);

			return webixExpVal;
		} else if (eventAction.equals("runUnitTest")) {
			// Run a java app in a separate system process
			int quantity = 1;
			try {
				quantity = Integer.parseInt(elementValueMap.get("quantityOfRuns"));
			} catch (Exception e) {
				logger.error("Problem with parse quantity of runs to int!");
				generalUtilLogger.logWrite(e);
			}
			Process proc;
			for (int i = 0; i < quantity; i++) {
				proc = Runtime.getRuntime().exec("java -jar c:\\logs\\jutest.jar");
				// Then retreive the process output
				proc.getInputStream();
				proc.getErrorStream();
			}
		} else if (eventAction.equals("exportSpecification")) {
			String flag = "0";
			//should take elementValueMap.get(smartSelectList) and clone each specification to another specificationRef with a new formid and parentid = elementvalueMap.get(SUBPROJECT_ID)
			//for the cloning- the func generalDao.clonetable  can be used when new formid should be sent in the map
			List<String> smartSelectList = new ArrayList<String>(
					Arrays.asList(elementValueMap.get("smartSelectList").split(",")));
			Map<String, String> replaceFieldsMap = new HashMap<String, String>();

			replaceFieldsMap.put("FORMID", "fg_get_struct_form_id('SPECIFICATIONREF')");
			replaceFieldsMap.put("SUBPROJECT_ID", elementValueMap.get("SUBPROJECT_ID"));
			replaceFieldsMap.put("PARENTID", elementValueMap.get("SUBPROJECT_ID"));
			for (String SpecificationRefId : smartSelectList) {
				String SpecificationId = generalDao.selectSingleString(
						"SELECT SPECIFICATION FROM fg_s_specificationref_all_v T WHERE t. SPECIFICATIONREF_ID="
								+ SpecificationRefId);
				String csvSpecifications = generalDao.getCSVBySql(
						"SELECT SPECIFICATION FROM fg_s_specificationref_all_v T WHERE t.subproject_id="
								+ elementValueMap.get("SUBPROJECT_ID") + " and SPECIFICATION = " + SpecificationId,
						true);
				if (!csvSpecifications.contains(SpecificationId)) {
					generalDao.cloneTable("FG_S_SPECIFICATIONREF_PIVOT", " formid = '" + SpecificationRefId + "'",
							replaceFieldsMap);

					flag = "1";
				}
			}
			return flag;

		} else if (eventAction.equals("importSpecification")) {
			String flag = "0";
			//should take elementValueMap.get(smartSelectList) and clone each specification to another specificationRef with a new formid and parentid = elementvalueMap.get(ORIGINSUBPROJECT_ID)
			//for the cloning- the func generalDao.clonetable  can be used when new formid should be sent in the map
			List<String> smartSelectList = new ArrayList<String>(
					Arrays.asList(elementValueMap.get("smartSelectList").split(",")));
			Map<String, String> replaceFieldsMap = new HashMap<String, String>();

			replaceFieldsMap.put("FORMID", "fg_get_struct_form_id('SPECIFICATIONREF')");
			replaceFieldsMap.put("SUBPROJECT_ID", elementValueMap.get("ORIGINSUBPROJECT_ID"));
			replaceFieldsMap.put("PARENTID", elementValueMap.get("ORIGINSUBPROJECT_ID"));
			for (String SpecificationRefId : smartSelectList) {
				String SpecificationId = generalDao.selectSingleString(
						"SELECT SPECIFICATION FROM fg_s_specificationref_all_v T WHERE t. SPECIFICATIONREF_ID="
								+ SpecificationRefId);
				String csvSpecifications = generalDao
						.getCSVBySql("SELECT SPECIFICATION FROM fg_s_specificationref_all_v T WHERE t.subproject_id="
								+ elementValueMap.get("ORIGINSUBPROJECT_ID") + " and SPECIFICATION = "
								+ SpecificationId, true);
				if (!csvSpecifications.contains(SpecificationId)) {

					generalDao.cloneTable("FG_S_SPECIFICATIONREF_PIVOT", " formid = '" + SpecificationRefId + "'",
							replaceFieldsMap);
					flag = "1";
				}
			}
			return flag;
		} else if (eventAction.equals("BatchAssociatedSamples")) {
			String selectedSamples = elementValueMap.get("selectedSampleCsv");
			if (selectedSamples.isEmpty()) {
				return "";
			}
			String sql = "select listagg(sample_id,',') within group(order by sample_id)" + " from fg_s_sample_v"
					+ " where batch_id is not null" + " and sample_id in (" + selectedSamples + ")"
					+ " and batch_id != '" + elementValueMap.get("parentId") + "'";
			return generalDao.selectSingleStringNoException(sql);
		} else if (eventAction.equals("StepSummaryReport")) { //kd 22102018 bypass way for creating report. It made for call report from table of Steps (Project management screen)
			//in this if and in the renderIreportNoFormParam() all sql defined hardcoded instead be taken from form parameters    
			String elementId = "-1";
			try {
				String stepId = formId;
				String url = elementValueMap.get("localHost");
				String sqlRepSaveData = "select * from FG_R_STEPSUM_FROM_PM_V where FORMID = " + stepId;
				String experimentName = "", stepName = "", conclusionKeyVal = "", stepIdKeyVal = "", protocolTypeName = "", experimentId = "", formNumberId = "";
				Connection con = null;
				CallableStatement stmt = null;
				try {
					//					con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
					con = generalDao.getConnectionFromDataSurce();
					stmt = con.prepareCall(sqlRepSaveData);
					stmt.execute();
					ResultSet rs = stmt.getResultSet();

					while (rs.next()) {
						experimentName = rs.getString(2);
						stepName = rs.getString(3);
						conclusionKeyVal = "{\"key\":\"conclussion\",\"val\":\"" + rs.getString(4) + "\"}";
						experimentId = rs.getString(5);
						protocolTypeName = rs.getString(6);
						formNumberId = rs.getString(7);
					}
					stepIdKeyVal = "{\"key\":\"STEP_ID\",\"val\":\"" + stepId + "\"}";
				} catch (Exception e) {
					logger.error("call renderIreport From PM from  Exception!");
					generalUtilLogger.logWrite(LevelType.ERROR, "Exception", formId, ActivitylogType.Creation, null, e);
				} finally {
					try {
						if (stmt != null) {
							stmt.close();
						}
						/*if (con != null) {
							con.close();
						}*/ //->
						generalDao.releaseConnectionFromDataSurce(con);

					} catch (Exception e) {
						generalUtilLogger.logWrite(LevelType.ERROR, "Exception", formId, ActivitylogType.Creation, null,
								e);
					}
				}
				Map<String, String> displayValuesMap = new LinkedHashMap<String, String>();
				String strDisplayValue = new String("[" + conclusionKeyVal + ", " + stepIdKeyVal + "]");
				//				displayValuesMap = generalUtil.stringToLnkHashMap("IREPORT_PRINT_", strDisplayValue, true);
				displayValuesMap = generalUtil.stringToLnkHashMap("", strDisplayValue, true);

				//				formService.initFormParam(stateKey, formCode, stepId, userId, new HashMap<String, String[]>());
				String pathFile = "";
				if (protocolTypeName.equals("Continuous Process")){
					pathFile = renderIreportNoFormParamCP(stateKey, formCode, /*"reportExpAndStepSumm",*/
							"experimentAndStepSummary_CP" + ".xml", "", reportFormat/*"PDF"*/, "False",
							"Experiment " + experimentName, stepName,
							url + "/skylineForm/init.request?stateKey=" + stateKey + "&formCode=" + formCode + "&formId="
									+ stepId + "&userId=" + userId
									+ "&avoidBreadCrumb=1&tableType=&urlCallParam=&PARENT_ID=-1", //This url for hypelink inside report //TODO innerinitid check
							displayValuesMap, generalUtil.getSessionId(), experimentId, formNumberId);
				} else{
					pathFile = renderIreportNoFormParam(stateKey, formCode, /*"reportExpAndStepSumm",*/
						"experimentAndStepSummary" + ".xml", "", reportFormat/*"PDF"*/, "False",
						"Experiment " + experimentName, stepName,
						url + "/skylineForm/init.request?stateKey=" + stateKey + "&formCode=" + formCode + "&formId="
								+ stepId + "&userId=" + userId
								+ "&avoidBreadCrumb=1&tableType=&urlCallParam=&PARENT_ID=-1", //This url for hypelink inside report //TODO innerinitid check
						displayValuesMap, generalUtil.getSessionId(), stepId);
				}
				System.out.println("step file path:" + pathFile);

				if (!generalUtil.getNull(pathFile).trim().equals("")) {
					elementId = uploadFileDao.saveFile(pathFile, "TEMP_IREPORT",
							"Experiment " + experimentName + "." + reportFormat, true);
				} else {
					generalUtilLogger.logWriter(LevelType.WARN, "Error in Step report from main screen",
							ActivitylogType.GeneralError, stepId);
				}
			} catch (Exception e) {
				elementId = "-1";
			}
			return elementId;
		} else if (eventAction.equals("ExperimentSummaryReport")) { //kd 22102018 bypass way for creating report. It made for call report from table of Experiment (Project management screen)
			//in this if and in the renderIreportNoFormParam() all sql defined hardcoded instead be taken from form parameters  
			String elementId = "-1";
			try {
				String experimentId = formId;
				String url = elementValueMap.get("localHost");
				String sqlRepSaveData = "select * from fg_r_experimentsum_from_pm_v where EXPERIMENT_ID = "
						+ experimentId;
				String experimentName = "", protocolTypeName = "";
				Connection con = null;
				CallableStatement stmt = null;
				try {
					//					con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
					con = generalDao.getConnectionFromDataSurce();
					stmt = con.prepareCall(sqlRepSaveData);
					stmt.execute();
					ResultSet rs = stmt.getResultSet();

					while (rs.next()) {
						experimentName = rs.getString(1);
						protocolTypeName = rs.getString(2);
					}
				} catch (Exception e) {
					logger.error("call renderIreport From PM from  Exception!");
					generalUtilLogger.logWrite(LevelType.ERROR, "Exception", "-1", ActivitylogType.Creation, null, e);
				} finally {
					try {
						if (stmt != null) {
							stmt.close();
						}
						/*if (con != null) {
							con.close();
						}*/ //->
						generalDao.releaseConnectionFromDataSurce(con);
					} catch (Exception e) {
						generalUtilLogger.logWrite(LevelType.ERROR, "Exception", "-1", ActivitylogType.Creation, null,
								e);
					}
				}
				String pathFile = "";
				if (protocolTypeName.equals("Continuous Process")){
					pathFile = renderIreportExperimentCP(stateKey, formCode, "experimentSummaryRep_CP" + ".xml", "",
							reportFormat/*"PDF"*/, "False", "Experiment " + experimentName, experimentName,
							url + "/skylineForm/init.request?stateKey=" + stateKey + "&formCode=" + formCode + "&formId="
									+ experimentId + "&userId=" + userId
									+ "&avoidBreadCrumb=1&tableType=&urlCallParam=&PARENT_ID=-1", //This url for hyperlink inside report //TODO innerinitid check
							generalUtil.getSessionId(), experimentId);
				} else{

				pathFile = renderIreportExperiment(stateKey, formCode, "experimentSummaryRep" + ".xml", "",
						reportFormat/*"PDF"*/, "False", "Experiment " + experimentName, experimentName,
						url + "/skylineForm/init.request?stateKey=" + stateKey + "&formCode=" + formCode + "&formId="
								+ experimentId + "&userId=" + userId
								+ "&avoidBreadCrumb=1&tableType=&urlCallParam=&PARENT_ID=-1", //This url for hyperlink inside report //TODO innerinitid check
						generalUtil.getSessionId(), experimentId);
				}
				System.out.println("experiment file path:" + pathFile);

				if (!generalUtil.getNull(pathFile).trim().equals("")) {
					elementId = uploadFileDao.saveFile(pathFile, "TEMP_IREPORT",
							"Experiment " + experimentName + "." + reportFormat, true);
				} else {
					generalUtilLogger.logWriter(LevelType.WARN, "Error in Experiment report from main screen",
							ActivitylogType.GeneralError, experimentId);
				}
			} catch (Exception e) {
				elementId = "-1";
			}
			return elementId;
		} else if (eventAction.equals("changeNotifMessageState")) {
			try {
				List<String> smartSelectList = new ArrayList<String>(
						Arrays.asList(elementValueMap.get("smartSelectList").split(",")));
				String messageState = elementValueMap.get("messageState").toString();
				String readState = (messageState.equals("read")) ? "1" : "0";
				String updateSql = "";
				for (String uniqueString : smartSelectList) {
					int ind = uniqueString.indexOf("_");
					String msgID = uniqueString.substring(0, ind);
					String msgUserID = uniqueString.substring(ind + 1, uniqueString.length());

					String sql = "select count(*) from FG_R_MESSAGES_STATE t \n" + "where t.message_id = '%1$s' \n"
							+ "and t.user_id = '%2$s'";
					String isRowExists = generalUtil
							.getNull(generalDao.selectSingleString(String.format(sql, msgID, msgUserID)));
					if (isRowExists.equals("0")) {
						updateSql = "insert into fg_r_messages_state(message_id, user_id, is_readed, is_deleted, updated_by, updated_date) \n"
								+ " values(" + msgID + ",'" + msgUserID + "'," + readState + ",0,'" + userId
								+ "',sysdate)";

					} else {
						updateSql = "update fg_r_messages_state \n" + " set is_readed = " + readState + ", \n"
								+ "	   is_deleted = 0, \n" + "    updated_by = '" + userId + "',  \n"
								+ "    updated_date = sysdate \n" + " where message_id = " + msgID + " \n"
								+ " and user_id = '" + msgUserID + "'";
					}
					formSaveDao.updateSingleStringInfoNoTryCatch(updateSql);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return generalUtilNotificationEvent.getMessageCount(userId, true);
		} else if (eventAction.equals("notifMessageReadAll")) {
			try {
				List<String> msgIds = generalDao.getListOfStringBySql(
						"select distinct t.ID from FG_R_MESSAGES_V t where t.\"Read\" = 'No' and t.user_id = '" + userId
								+ "'");
				String readState = "1";
				String updateSql = "";
				for (String msgID : msgIds) {
					String msgUserID = userId;

					String sql = "select count(*) from FG_R_MESSAGES_STATE t \n" + "where t.message_id = '%1$s' \n"
							+ "and t.user_id = '%2$s'";
					String isRowExists = generalUtil
							.getNull(generalDao.selectSingleString(String.format(sql, msgID, msgUserID)));
					if (isRowExists.equals("0")) {
						updateSql = "insert into fg_r_messages_state(message_id, user_id, is_readed, is_deleted, updated_by, updated_date) \n"
								+ " values(" + msgID + ",'" + msgUserID + "'," + readState + ",0,'" + userId
								+ "',sysdate)";

					} else {
						updateSql = "update fg_r_messages_state \n" + " set is_readed = " + readState + ", \n"
								+ "	   is_deleted = 0, \n" + "    updated_by = '" + userId + "',  \n"
								+ "    updated_date = sysdate \n" + " where message_id = " + msgID + " \n"
								+ " and user_id = '" + msgUserID + "'";
					}
					formSaveDao.updateSingleStringInfoNoTryCatch(updateSql);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return generalUtilNotificationEvent.getMessageCount(userId, true);
		} else if (eventAction.equals("deleteAndInsertMaterial")) {
			String sessionId = generalUtilFormState.checkAndReturnSessionId(formCode, parentId);
			String searchAttrName = chemSearchType;
			JSONObject json = new JSONObject(elementValueMap.get("insertMaterial"));

			String insert = "", sql, materialFormId;
			List<String> materialRefIds = elementValueMap.get("listMaterialToDelete").isEmpty()
					? new ArrayList<String>()
					: new ArrayList<String>(Arrays.asList(elementValueMap.get("listMaterialToDelete").split(",")));

			for (String materialRefId : materialRefIds) {
				commonFunc.doRemove("MaterialRef", materialRefId, userId);
			}
			if (JSONObject.getNames(json) == null) {
				return insert;
			}
			String smile;
			JSONArray smiles;
			String formnumberid = formDao.getFromInfoLookup("Step", LookupType.ID, formId, "formnumberid");
			for (String key : JSONObject.getNames(json)) {
				smiles = json.getJSONArray(key);
				String counter = "";
				String alias_ = "";
				if(key.toLowerCase().equals("product")){
					sql = "select count(*) \n"
						+ " from fg_s_materialref_v\n"
						+ " where parentId = '"+formId+"'\n"
						+ generalUtilFormState.getWherePartForTmpData("Materialref", formId)
						+" and tabletype = 'Product' and rownum<2";
					counter = generalDao.selectSingleStringNoException(sql);
				}
				for (int i = 0; i < smiles.length(); i++) {
					smile = smiles.getString(i);
					if (!smile.equals("")) {
						materialFormId = formSaveDao.getStructFormId(formCode);
						if(key.toLowerCase().equals("product")){
							alias_ = "Solution of step "+formnumberid+(counter.equals("")||counter.equals("0")?"":"."+counter);
						}
						sql = "insert into FG_S_MATERIALREF_PIVOT (FORMID,PARENTID,TIMESTAMP,active,INVITEMMATERIAL_ID,ALIAS_,TABLETYPE,SESSIONID,CREATED_BY,CREATION_DATE)"
								+ " select " + materialFormId + "," + formId + "," + "sysdate," + "'1',"
								+ "t.INVITEMMATERIAL_ID,'"+alias_+"'," + "'" + key + "', '" + sessionId + "','" + userId
								+ "', sysdate from FG_S_INVITEMMATERIAL_ALL_V t where t." + searchAttrName + " = '"
								+ smile + "' ";
						insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_MATERIALREF_PIVOT", materialFormId);
						if(key.toLowerCase().equals("product")){
							counter = counter.isEmpty()? "1":String.valueOf((Integer.parseInt(counter)+1));
						}
					}
				}
			}

			return insert;
		} else if (eventAction.equals("getformNumberID")) {
			/*String formCodeEntity = formDao.getFormCodeEntityBySeqId("", formId);
			if (formCodeEntity.equals("InvItemBatch") || formCodeEntity.equals("Sample")) {
				String sql = "select formnumberid from fg_s_" + formCodeEntity + "_v where formId='" + formId + "'";
				String formNumberId = generalDao.selectSingleStringNoException(sql);
				return formNumberId;
			}
			
			return "";*/
			return commonFunc.getformNumberIDSearchReport(formId);
		} else if (eventAction.equals("getSampleLinkState")) {
			String toReturn = "";
			JSONObject json = new JSONObject();
			String stateCode = "0"; // 0 OK, 1 WARN, 2 ERROR
			String message = "";
			
			try {
				String hasResult = "";
				String sampleStorage = "";
				String sampleTimePoint = "";
				String sampleExperimentName = "";
				String sampleName = "";
				String formExperimentName = generalUtil.getNull(elementValueMap.get("formNumberId"));
				String formStorage = generalUtil.getNull(elementValueMap.get("storageLastSelection"));
				
				//no storage selected in the experiment screen
				if(formStorage.equals("")) {
					json.put("stateCode", "2");
					json.put("message", "Please select storage condition!");
					json.put("sampleName", "NA");
					toReturn = json.toString();
				}
				
				if(toReturn.equals("")) { //TODO taro develop (3) remove LINKRESULTVALUE and LINKRESULTUOM(?) from sample it is in fg_results (and change the ocde bellow) + add batch to experiment creation (only for taro) 
					String sql = "SELECT distinct NULL AS D1,\r\n" + 
							"       max(to_char(decode(r.result_value, NULL, 0, 1))) over (partition by t.formId)  has_result,\r\n" + 
							"       t.linkexperimentid,\r\n" + 
							"       t.LINKSTORAGE,\r\n" + 
							"       t.linktimepoint,\r\n" + 
							"       t.formnumberid,\r\n" + 
							"       e.experimentname AS linkexperimentname\r\n" + 
							"FROM fg_s_sample_all_v t,\r\n" + 
							"     fg_s_experiment_v e,\r\n" + 
							"     fg_results r\r\n" + 
							"WHERE t.SAMPLE_ID = r.SAMPLE_ID\r\n" + 
							"AND   t.LINKEXPERIMENTID = e.experiment_id\r\n" + 
							"AND t.formId='" + formId + "'\r\n" + 
							"AND r.result_test_name = 'Stability'";
					
					List<Map<String, Object>> listOfMaps = generalDao.getListOfMapsBySql(sql);
					for (Map<String, Object> entry : listOfMaps) {
						hasResult = generalUtil.getNull((String)entry.get("HAS_RESULT"));
						sampleExperimentName = generalUtil.getNull((String)entry.get("LINKEXPERIMENTNAME"));
						sampleStorage = generalUtil.getNull((String)entry.get("LINKSTORAGE"));
						sampleTimePoint = generalUtil.getNull((String)entry.get("LINKTIMEPOINT"));
						sampleName = generalUtil.getNull((String)entry.get("FORMNUMBERID"));
					}
					
					if(!sampleExperimentName.equals("") && !sampleStorage.equals("") && !(formExperimentName + "," + formStorage).equals(sampleExperimentName + "," + sampleStorage)) { // not empty (has link) and different from last storage selection in the experiment screen
						message = "Please note: sample " + sampleName + " is already linked to Experiment " + sampleExperimentName + "/Storage Condition " + sampleStorage + "/TP " + sampleTimePoint +".";
						if(hasResult.equals("0")) {
							stateCode = "1";
							message += " Would you like to link this sample to this Storage Condition?";
						} else {
							stateCode = "2";
							message += " In order to link this sample, delete the entered results first.";
						}
					}
					
					json.put("stateCode", stateCode);
					json.put("message", message);
					
					json.put("sampleName", ((sampleName == null || sampleName.equals("")) ? formDao.getFromInfoLookup("Sample", LookupType.ID,formId, "name") : sampleName ));
					
					toReturn = json.toString();
				}
			} catch (Exception e) {
				generalUtilLogger.logWrite(e);
				json.put("stateCode", stateCode);
				json.put("message", "Error in sample evaluation");
				json.put("sampleName", "NA");
				toReturn = json.toString();
			}
			return toReturn;
		} else if (eventAction.equals("setSearchParam")) {
			generalUtilFormState.setFormParam(stateKey, formCode, "TEXTBOX",
					generalUtil.getNull(elementValueMap.get("textBox")));
			generalUtilFormState.setFormParam(stateKey, formCode, "SEARCHORNAVIGATE",
					generalUtil.getNull(elementValueMap.get("searchOrNavigate")));
			generalUtilFormState.setFormParam(stateKey, formCode, "INVENTORYLIST",
					generalUtil.getNull(elementValueMap.get("inventoryList")));
			generalUtilFormState.setFormParam(stateKey, formCode, "SEARCHINDOCSONLY",
					generalUtil.getNull(elementValueMap.get("srearchInDocsOnly")));
			generalUtilFormState.setFormParam(stateKey, formCode, "SEARCHINDOCSONLYADVANCE2",
					generalUtil.getNull(elementValueMap.get("searchInDocsOnly2")));	
			if(elementValueMap.get("searchOrNavigate").equals("Batch / Sample / Barcode Scan")){
				String textBox = commonFunc.getformNumberIDSearchReport(elementValueMap.get("textBox"));
				if(!textBox.isEmpty()){
					generalUtilFormState.setFormParam(stateKey, formCode, "TEXTBOX",
							generalUtil.getNull(textBox));
					return textBox;
				}
			}
			return "";
		} else if (eventAction.equals("getChemMaterialIdList")) {//search by chemdoodle - inserting required parameters
			String materilIdList = chemDao.getMaterialListByStructVal(elementValueMap.get("mrv"));
			String fileIdList = chemDao.getFileListByStructVal(elementValueMap.get("mrv"));
			generalUtilFormState.setFormParam(stateKey, formCode, "MATERIL_ID_LIST", materilIdList);
			generalUtilFormState.setFormParam(stateKey, formCode, "FILE_ID_LIST", fileIdList);
			generalUtilFormState.setFormParam(stateKey, formCode, "TEXTBOX",
					generalUtil.getNull(elementValueMap.get("textBox")));
			generalUtilFormState.setFormParam(stateKey, formCode, "SEARCHORNAVIGATE",
					generalUtil.getNull(elementValueMap.get("searchOrNavigate")));
			return materilIdList;
		} else if (eventAction.equals("getSampleResultId")) {
			String sample_id = "";
			if (formCode.equals("Sample")) {
				sample_id = formId;
			} else if (formCode.equals("InvItemSamplesMain")) {
				sample_id = generalUtilFormState.getFormParam(stateKey, formCode, "$P{CURRENT_ROW_UPPERTABLE}");
			}
			String sql = "select LISTAGG(isSingleAnalytical||';'||result_id, ',') WITHIN GROUP (ORDER BY 1) from\r\n" + 
					" fg_i_selectedautoresults_v where sample_id = '" + sample_id
					+"'";
			/*String sql = "select LISTAGG(result_id, ',') WITHIN GROUP (ORDER BY 1) from"
					+ "(select distinct result_id,sample_id,"
					+ "count(*) over (partition by sample_id,invitemmaterial_id,result_name) as material_count"
					+ " from fg_i_sampleresults_v)" + " where material_count = 1" + " and sample_id = '" + sample_id
					+ "'";*/
			String defaultRows = generalUtil.getNull(generalDao.selectSingleStringNoException(sql));
			return defaultRows.replace(",", "-");
		}else if (eventAction.equals("getSampleResultMain")) {
			String sample_id = "";
			sample_id = generalUtilFormState.getFormParam(stateKey, formCode, "$P{CURRENT_ROW_UPPERTABLE}");
			
			String sql = "select LISTAGG(result_id, ',') WITHIN GROUP (ORDER BY 1) from\r\n" + 
					" FG_I_SELECTEDRESULTS_V where sample_id = '" + sample_id
					+"'";
			/*String sql = "select LISTAGG(result_id, ',') WITHIN GROUP (ORDER BY 1) from"
					+ "(select distinct result_id,sample_id,"
					+ "count(*) over (partition by sample_id,invitemmaterial_id,result_name) as material_count"
					+ " from fg_i_sampleresults_v)" + " where material_count = 1" + " and sample_id = '" + sample_id
					+ "'";*/
			String defaultRows = generalUtil.getNull(generalDao.selectSingleStringNoException(sql));
			return defaultRows.replace(",", "-");
		}else if (eventAction.equals("getFavoriteSpreadsheet")) {
			String sql = "select LISTAGG(object_id, ',') WITHIN GROUP (ORDER BY 1) from\r\n" + 
					" fg_favorite where creator_id = '" + userId
					+"'";
			String defaultRows = generalUtil.getNull(generalDao.selectSingleStringNoException(sql));
			return defaultRows.replace(",", "-");
		}else if (eventAction.equals("cloneSpreadsheet")) {
			String spreadsheetFormId = formSaveDao.getStructFormId("SpreadsheetTempla");
			String sql = "insert into FG_S_SpreadsheetTempla_PIVOT (FORMID,formcode,formcode_entity,CLONEID,TIMESTAMP,creation_date,active,SPREADSHEET,CRATORLAB_ID,SPREADSHEETTEMPLANAME,CREATOR_ID,change_by,created_by,STATUS_ID,CREATIONDATE,document)"
					+ " select " + spreadsheetFormId + ",'SpreadsheetTempla','SpreadsheetTempla'," + formId + "," + "sysdate,sysdate," + "'1',"
					+ "t.spreadsheet,t.CRATORLAB_ID,'"+elementValueMap.get("spreadsheetTemplaName")+"','" + userId +"','" + userId +"','" + userId
					+ "',STATUS_ID,CREATIONDATE,document from FG_S_SpreadsheetTempla_PIVOT t where t.formid = '"
					+ formId + "' ";
			formSaveDao.insertStructTableByFormId(sql, "FG_S_SpreadsheetTempla_PIVOT", spreadsheetFormId);
			return spreadsheetFormId;
		} else if (eventAction.equals("generateHistoryReport")) {
			generalUtilFormState.setFormParam(stateKey, formCode, "ADHOC_FORMIDSEARCH",
					elementValueMap.get("formIdSearch"));
			return elementValueMap.get("formIdSearch");
		} else if (eventAction.equals("executeSQLGenerator")) { //used in SQLGenerator and in DynamicReportSql forms
			generalUtilFormState.setFormParam(stateKey, formCode, "QUERY_TEXT",
					elementValueMap.get("sqlText"));
			return "1";
		} else if (eventAction.equals("generateDynamicReport")) {
			String sqlTextContent = formDao.getFromInfoLookup("DynamicReportSql", LookupType.ID, elementValueMap.get("DYNAMICREPORTSQL_ID"),
					"SQLTEXT_CONTENT");
			generalUtilFormState.setFormParam(stateKey, formCode, "QUERY_TEXT",
					sqlTextContent);
			return "1";
		} else if (eventAction.equals("getNavigationProject")) {
			String parentFCode= formDao.getFormCodeBySeqId(formId); 
			String path = generalUtilFormState.getFormParam(stateKey, parentFCode, "$P{FORMPATH}"); //formar example{"path":[{"id":"181088","name":"Project:Naama_001"},{"id":"182921","name":"SubProject:Workups"},{"id":"249153","name":"ExperimentCP:2078-04-005-0015"},{"id":"249390","name":"Step:STEP 02"}]}
            String nameslastValue ="#";
            String projectByPath = "";
            
            String parentFCodeEntity= formDao.getFormCodeEntityBySeqId("",formId);
			if(parentFCodeEntity.equals("SelfTest") || parentFCodeEntity.equals("Workup")){
				parentFCodeEntity = "SW";
			}
			List<String> project = new ArrayList<String>();//formDao.getTreeData("PROJECT", "fg_i_tree_connection_v", "and "+parentFCodeEntity+" like '%@"+formId+"@%'");
			
			if (!generalUtil.getNull(path).isEmpty()) {
				JSONObject json = new JSONObject(path);
				JSONArray pathList = json.getJSONArray("path");
				for (int i = 0; i < pathList.length(); i++) {
					String p = pathList.get(i).toString();
					String[] detailsToDisplay = generalUtil.getJsonValById(p, "name").split(":");
					String id = generalUtil.getJsonValById(p, "id");
					String name = detailsToDisplay.length > 1 ? generalUtil.getJsonValById(p, "name").split(":")[1]
							: "";
					name = name.replaceAll("@", " ");
					String formC = detailsToDisplay.length > 1 ? generalUtil.getJsonValById(p, "name").split(":")[0]
							: "";
					if(formC.equals("Project")){
						project = formDao.getTreeData("PROJECT", "fg_i_tree_connection_proj_v", "and project like '%@"+id+"@%'",path);
					}
					if(project.isEmpty()){
						if(i == 0){
							projectByPath = "Project@"+id+"@"+name;
						}
						if(formC.equals(parentFCode)){//brother's information
							continue;
						}
					}
					
					//add run data between experiment to step in case runNumberDisplay exists (in continues process experiment)
					if (formC.equals("Step")) {
						String runNumberDisplay = formDao.getFromInfoLookup("STEP", LookupType.ID, id,
								"RUNNUMBERDISPLAY");
						if (!generalUtil.getNull(runNumberDisplay).isEmpty()) {
							String id_ = formDao.getFromInfoLookup("STEP", LookupType.ID, id, "EXPERIMENT_ID");
							String name_ = "Run " + runNumberDisplay;
							id_ += "#" + runNumberDisplay;
							nameslastValue += "," + name_ + "_node_a_" + id_;
						}
					}
					
					nameslastValue += "," + name + "_node_a_" + id;
					
					if (i == pathList.length() - 1) {
						nameslastValue += "," + name + "_node_a_" + id;
					}
					
				}
			}
			
			if(project.isEmpty()){
				project.add(projectByPath);
			}
			String projCsv= generalUtil.listToCsv(project);
			String[] result = projCsv.split("@");
			if(result.length>2){
				return (result[1]+"@"+result[2]+"@"+nameslastValue);
			}
		}
//		else if (eventAction.equals("insertExperimentResultsTableDataToClob")) { // taro develop not in use
//			String plainText = elementValueMap.get("experimentResultsTableData");
//			String objectId = uploadFileDao.saveStringAsClobRenderId(formCode, plainText);
//			return objectId;
//		}
		else if(eventAction.equals("checkSampleRequestDestLab")){
			String message = "";
			parentId = elementValueMap.get("parentId");
			String experimentLab = formDao.getFromInfoLookup("Experiment", LookupType.ID,
					parentId, "LABORATORY_ID");
           String sampleCsv = elementValueMap.get("sampleTable");
			if (!generalUtil.getNull(sampleCsv).isEmpty()) {
				String sql = "select distinct sr.SAMPLEID from fg_s_request_v t, fg_s_sampledataref_v sr where sr.PARENTID = t.REQUEST_ID "
						+ "and sr.sessionid is null and sr.active = 1 and sr.SAMPLEID in(" + sampleCsv
						+ ") and t.destlab_id <>'" + experimentLab + "'";
				List<String> samples = generalDao.getListOfStringBySql(sql);
				for (String sample : samples) {
					sql = "select distinct l.LaboratoryName  from FG_I_CONN_REQUEST_SMPL_V t,fg_s_laboratory_v l where t.sample_id = '"
							+ sample + "' and t.DESTLAB_ID<>'" + experimentLab + "' and t.DESTLAB_ID=l.laboratory_id";
					List<String> destLabName = generalDao.getListOfStringBySql(sql);
					String sampleName = formDao.getFromInfoLookup("Sample", LookupType.ID,
							sample, "name");
					if(destLabName!=null && !destLabName.isEmpty() && !sampleName.isEmpty()){
						message+= "You will test the sample "+sampleName+" sent to "+ generalUtil.listToCsv(destLabName)+".</br>";
					}
				}
				}
			if(!message.isEmpty()){
				message+="Are you sure?";
			}
			return message;
		} else if(eventAction.equals("checkRequestDestLab")){
			String message = "";
			String requestList = elementValueMap.get("REQUEST_ID");
			if (!generalUtil.getNull(requestList).isEmpty()){
				parentId = elementValueMap.get("parentId");
				String experimentLab = formDao.getFromInfoLookup("Experiment", LookupType.ID,
						parentId, "LABORATORY_ID");
				//check if Request from different destination lab is selected
				List<String> destLabId = generalDao.getListOfStringBySql("select distinct t.DESTLAB_ID"
						+ " from fg_s_request_v t, fg_s_sampledataref_v sr" + " where t.request_id in (" + requestList + ") and sr.PARENTID(+) = t.REQUEST_ID and t.DESTLAB_ID<>"+experimentLab);
				if (destLabId!= null && !destLabId.isEmpty()) {
					List<String> samples= generalDao.getListOfStringBySql("select distinct s.samplename from fg_s_request_v t, fg_s_sampledataref_v sr, fg_s_sample_v s where sr.PARENTID = t.REQUEST_ID "
							+ "and sr.sessionid is null and sr.active = 1 and s.sample_id = sr.SAMPLEID and t.REQUEST_ID in("+requestList
							+") and t.destlab_id in("+generalUtil.listToCsv(destLabId)+")");
					List<String> destLabName = generalDao.getListOfStringBySql("select distinct laboratoryname from fg_s_laboratory_v where formid in("+generalUtil.listToCsv(destLabId)+")");
					if(samples!= null && !samples.isEmpty() && destLabName!=null && !destLabName.isEmpty()){//if sample exist
						message = "Please notice- you will test sample no. "+ generalUtil.listToCsv(samples) +" that was sent to destination lab: "+ generalUtil.listToCsv(destLabName)+". \r\n" +
								"</br>Are you sure you want to continue?</br>" ;
					}else if(destLabName!=null && !destLabName.isEmpty()){
						message = "Please notice- you will test Request that was sent to destination lab: "+ generalUtil.listToCsv(destLabName)+". \r\n" +
								"</br>Are you sure you want to continue?</br>";
						}
							
				}
			}
			return message;
		} else if (eventAction.equals("createNewSelfTestWithData")) {
			try {
				// check if sample check is needed
				String samleList = generalDao.selectSingleStringNoException(
						"select sampletable from fg_s_sampleselect_v where parentId = '" + formId + "'");
				if (samleList == null || samleList.replaceAll(",", "").isEmpty()) {
					String tooltipMessage = generalUtil.getNull(elementValueMap.get("parmActionSampleMessage"),
							generalUtil.getSpringMessagesByKey("NO_SAMPLE_IN_ACTION_COPY", ""));
					String message = generalUtil.getSpringMessagesByKey("Warning_WF_Action," + tooltipMessage, "");
					throw new Exception(message);
				}
				// OK we have sample...

				// check if new selftest allowed
				boolean canNewByList = false;
				List<String> newAvailableList = Arrays
						.asList(commonFunc.getNewAvailableFormListById(stateKey, formId, "SelfTestMain"));
				for (String newCode : newAvailableList) {
					if (newCode.equals("SelfTestMain")) {
						canNewByList = true;
						break;
					}
				}

				if (!canNewByList) {
					String message = generalUtil.getSpringMessagesByKey(
							"Warning_WF_Action,Creation of " + "SelfTest" + " is not allowed.", "");
					throw new Exception(message);
				}
				// pass the check ->
			} catch (Exception e) {
				return commonFunc.doSaveOnException(e, formId, formCode).getErrorMsg();
			}

			String defaultDataFormId = elementValueMap.get("parmDefaultSelfTestID");
			if (!generalUtil.getNull(defaultDataFormId).equals("")) {

				// update replaceFieldsMap with columns that need to be handle (or set to empty
				// empty) in clone
				Map<String, String> replaceFieldsMap = new HashMap<String, String>();

				// action id with the current action (and to be on safe side: experiment / step)
				String actionId = formId;
				replaceFieldsMap.put("ACTION_ID", actionId);
				replaceFieldsMap.put("USEASDEFAULTDATA", "null");
				replaceFieldsMap.put("STEP_ID", elementValueMap.get("parmCurrentStepID"));
				replaceFieldsMap.put("EXPERIMENT_ID", elementValueMap.get("EXPERIMENT_ID"));

				// site unit lab for this current user
				Map<String, String> tmpUserInfo = formDao.getFromInfoLookupAll("user", LookupType.ID, userId);
				replaceFieldsMap.put("SITE_ID", tmpUserInfo.get("SITE_ID"));
				replaceFieldsMap.put("UNIT_ID", tmpUserInfo.get("UNIT_ID"));
				replaceFieldsMap.put("LABORATORY_ID", tmpUserInfo.get("LABORATORY_ID"));

				// active status as default
				String Status_id = formDao.getFromInfoLookup("SELFTESTSTATUS", LookupType.NAME, "Active", "id");
				replaceFieldsMap.put("STATUS_ID", Status_id);

				// clean up summary / samplenumber
				replaceFieldsMap.put("summary", "null");
				replaceFieldsMap.put("samplenumber", "null");

//				//sampleId
//				replaceFieldsMap.put("sampleId", "null");

				// update now creationDate
//				DateFormat dateFormat = new SimpleDateFormat(generalUtil.getConversionDateFormat());
//				Date date = new Date();	
//				replaceFieldsMap.put("creationDate", dateFormat.format(date)); 

				// *** use clone ***
				String cloneFormId = formSaveDao.cloneStructTable(defaultDataFormId, replaceFieldsMap,null);

				// make it active after clone
				String sql = "UPDATE FG_S_SELFTEST_PIVOT SET active = 1 WHERE formid = " + cloneFormId;
				formSaveDao.updateSingleStringInfoNoTryCatch(sql);

				// make onSelfTestNewEvent (code that was originally in main selftest)
				Map<String, String> paramMapReturn = new HashMap<String, String>();
				commonFunc.onSelfTestNewEvent(userId, cloneFormId, actionId, paramMapReturn);
				if (!generalUtil.getNull(paramMapReturn.get("ONINIT_FIRSTSAMPLEID")).equals("")
						&& !generalUtil.getNull(paramMapReturn.get("ONINIT_FIRSTSAMPLENUMBER")).equals("")) {
					sql = "update fg_s_selftest_pivot set sampleid = '"
							+ generalUtil.getNull(paramMapReturn.get("ONINIT_FIRSTSAMPLEID")) + "', samplenumber ='"
							+ generalUtil.getNull(paramMapReturn.get("ONINIT_FIRSTSAMPLENUMBER")) + "' where formid ='"
							+ cloneFormId + "'";
					formSaveDao.updateStructTableByFormId(sql, "fg_s_selftest_pivot",
							Arrays.asList("sampleid", "samplenumber"), cloneFormId);
				}

				// ** clone ref tables
				// set instrument-ext from sorce test
				sql = "select t.* from fg_s_SelfTest_pivot t where FORMID = '" + cloneFormId + "'";
				Map<String, String> dbVal = generalDao.sqlToHashMap(sql);
//				if (dbVal.get("INSTRUMENTEXT") != null && !(dbVal.get("INSTRUMENTEXT")).isEmpty()) {
//					// Update External instrument and webixAnalyticalSelfTest
//					sql = "update fg_s_SelfTest_pivot t set t.instrumentext ='" + dbVal.get("INSTRUMENTEXT")
//							+ "' where formid = '" + cloneFormId + "'";
//					formSaveDao.updateSingleStringInfoNoTryCatch(sql);
//				}

				String currentExperimentId = dbVal.get("EXPERIMENT_ID");
				String selfTestTypeId = dbVal.get("TYPE_ID");
				String sampleId = dbVal.get("SAMPLEID");
				cloneSelfTestRefData(defaultDataFormId, cloneFormId, currentExperimentId, selfTestTypeId, sampleId,
						userId);

				// update action selftestidholder
				sql = "update fg_s_action_pivot t set t.selftestidholder = '" + cloneFormId + "' where t.formid = '"
						+ actionId + "'";
				formSaveDao.updateSingleStringInfoNoTryCatch(sql);

			}
			return "0"; // TODO it is not clear what is needed to be return (only if we return value we
						// get to the code ... else if(customerFunction == "createNewSelfTestWithData")
						// in the ElementDataTableApiImpBL.js
		} else if (eventAction.equals("cleanDataEventClick")) {
			//clean ref data table
			commonFunc.cleanRefTableByFormCode("AnalytMethodSelect", formId, userId, "ANALYTMATHODTABLE" );
			commonFunc.cleanRefTableByFormCode("ColumnSelect", formId, userId, "COLUMN_ID");
			commonFunc.cleanRefTableByFormCode("InstrumentSelect", formId, userId, "INSTRUMENT_ID");
			commonFunc.cleanRefTableByFormCode("Document", formId, userId, null);
			commonFunc.cleanRefTableByFormCode("ResultRef", formId, userId, null);
			//clean result -> we should not remove in result to support temp data 
//			formSaveDao.updateSingleStringInfo("update fg_results t set t.result_is_active='0' where t.selftest_id ='" + formId + "'");
		} else if (eventAction.equals("cleanOnChangeSelfTestTypeToNonNumeric")) {
			//clean ref data table
			commonFunc.cleanRefTableByFormCode("AnalytMethodSelect", formId, userId,"ANALYTMATHODTABLE");
			commonFunc.cleanRefTableByFormCode("ColumnSelect", formId, userId, "COLUMN_ID");
			commonFunc.cleanRefTableByFormCode("ResultRef", formId, userId, null);
			//clean result -> we should not remove in result to support temp data 
//			formSaveDao.updateSingleStringInfo("update fg_results t set t.result_is_active='0' where t.selftest_id ='" + formId + "'");
		}else if (eventAction.equals("cleanDataEventClickRequest")) {
			//clean ref data table
			commonFunc.cleanRefTableByFormCode("OperationType", formId, userId, null);
			commonFunc.cleanRefTableByFormCode("UsersCrew", formId, userId, null);
			commonFunc.cleanRefTableByFormCode("GroupsCrew", formId, userId, null);
	   } else if (eventAction.equals("createNewExpFromRequest")) {
			StringBuilder sb = new StringBuilder();
			integrationValidation.validate(ValidationCode.CONFIRM_NEW_EXPERIMENT, formCode, formId, formId, sb);
			if (!sb.toString().isEmpty()) {
				return sb.toString();
			}
			return "";
		} else if (eventAction.equals("deleteOperationType")) {
			String retVal;
			try {
				String sql = "delete from FG_S_operationtype_PIVOT where PARENTID = '" + formId + "'";
				retVal = formSaveDao.deleteStructTableByFormId(sql, "FG_S_" + "operationtype".toUpperCase() + "_PIVOT",
						formId);
			} catch (Exception e) {
				retVal = "-1";
				e.printStackTrace();

			}
			return retVal;
		} else if (eventAction.equals("getStepDesign")) {

			// update or add new step design to map

			List<String> designStepList = new ArrayList<String>();
			Map<String, String> stepDesignMap = new HashMap<String, String>();
			for (Map.Entry<String, String> m : elementValueMap.entrySet()) {
				if (m.getKey().toLowerCase().contains("step") && m.getValue().equals("1")&&!m.getKey().equals("numConcentrationImpStep")) {
					
					 designStepList.add(m.getKey());
				}
				if (m.getKey().equals("numConcentrationImpStep") &&! m.getValue().equals("")) {
					designStepList.add(m.getKey()+";"+m.getValue());
				}
				}
			

			// update stepsCheckboxList
			generalUtilDesignData.addStepListDesignToMap(designStepList, elementValueMap.get("lastStepValue"));
			
		// update impuritiesList
		generalUtilDesignData.clearImpuritiesDesignForStep(elementValueMap.get("lastStepValue"));
		if(!elementValueMap.get("stepImpuritiesIdList").equals("")&&elementValueMap.get("stepImpuritiesIdList")!=null)
		{	
			generalUtilDesignData.addImpuritiesListToMap(
					Arrays.asList(elementValueMap.get("stepImpuritiesIdList").split("\\s*,\\s*")),
					elementValueMap.get("lastStepValue"));
		}

			// return the new changed step if exists
			StringBuilder sb = new StringBuilder();
			String spesificSimpleStepDate = "";
			String spesificImpuritiesStepDate = "";
			
			if (generalUtilDesignData.getStepDesignMapwithList().containsKey(elementValueMap.get("STEP_ID"))) {
				spesificSimpleStepDate = generalUtil.listToCsv(
						generalUtilDesignData.getSpesificListStepDesignToMap(elementValueMap.get("STEP_ID")));
				sb.append("stepsSimpleDesign:");
				sb.append(spesificSimpleStepDate);
				sb.append('@');
			}
			else  sb.append("stepsSimpleDesign:-1@");
			if (generalUtilDesignData.getStepImpuritesDesignMap().containsKey(elementValueMap.get("STEP_ID"))) {
				spesificImpuritiesStepDate = generalUtil.listToCsv(
						generalUtilDesignData.getSpesificImpuritiesListToMap(elementValueMap.get("STEP_ID")));
				sb.append("impuritiesStepDesign:");
				sb.append(spesificImpuritiesStepDate);
				sb.append('@');
			}
			else  sb.append("impuritiesStepDesign:-1@");
			
			
			return sb.toString();
			

		} else if (eventAction.equals("copyDesignStep")) {
			//get spesific design
			List<String> designStepList = new ArrayList<String>();
			for (Map.Entry<String, String> m : elementValueMap.entrySet()) {
				if (m.getKey().toLowerCase().contains("step") && m.getValue().equals("1")) {
					designStepList.add(m.getKey());
				}
				if (m.getKey().equals("numConcentrationImpStep") &&! m.getValue().equals("")) {
					designStepList.add(m.getKey()+";"+m.getValue());
				}
			}
			String copyStepsString = elementValueMap.get("usingSteps");
			String[] copyStepsList = copyStepsString.split(",");
			for (int i = 0; i < copyStepsList.length; i++) {
				generalUtilDesignData.addStepListDesignToMap(designStepList, copyStepsList[i]);
			}
			for (int i = 0; i < copyStepsList.length; i++) {
				generalUtilDesignData.addImpuritiesListToMap(Arrays.asList(elementValueMap.get("stepImpuritiesIdList").split("\\s*,\\s*")), copyStepsList[i]);
			}
			}
		 else if (eventAction.equals("getFormTitleWarningMessage")) {
			StringBuilder message = new StringBuilder();
			String expStatusName = "";
			List<String> samples= generalDao.getListOfStringBySql("select t.MESSAGE_ from FG_R_RESUSINGTOUPDATE_MSG_V t where t.FORMID = " + formId +" or t.experiment_id ="+ formId);
						
			if (samples != null && !samples.isEmpty()) {// if sample exist
				if (formCode.equals("Experiment")) {
					expStatusName = formDao.getFromInfoLookup("Experiment", LookupType.ID, formId, "STATUSNAME");
				}
				message.append("Please notice:\n");
				for (int i = 0; i < samples.size(); i++) {
					message.append(samples.get(i));
				}
			}
			 return expStatusName+";"+message.toString();

		} else if (eventAction.equals("getColumnDesign")) {
			 return getColDesign(elementValueMap,formId,userId);
		}

		else if (eventAction.equals("saveReportDesign")) {//insert data into session if need
			String designName="";
			if(elementValueMap.get("parentFormCode").equals("ReportDesignExp")) {
				preperReport(elementValueMap);
	
			}
			//add save form fields
			generalUtilDesignData.getDesignFormElementValueMap().put("project", elementValueMap.get("designProject"));
			generalUtilDesignData.getDesignFormElementValueMap().put("subProject", elementValueMap.get("designSubProject"));
			generalUtilDesignData.getDesignFormElementValueMap().put("reportDesignExpName", elementValueMap.get("designName"));
			generalUtilDesignData.getAdditionalInfo().put("project", elementValueMap.get("designProject"));
			generalUtilDesignData.getAdditionalInfo().put("subProject", elementValueMap.get("designSubProject"));
			generalUtilDesignData.getAdditionalInfo().put("reportDesignExpName", elementValueMap.get("designName"));
			designName=elementValueMap.get("designName");
			String deleteFromId = "";
			String action = "";
			String isOtherReportExist = "";
			//check if there is a design with same name to same user if there is one remove old and insert the new if not just insert new 
			isOtherReportExist = generalDao.selectSingleStringNoException(
					"select distinct 1 from fg_s_reportdesignexp_all_v t where ReportDesignExpName = '"
							+ designName + "' and t.CREATED_BY = '" + userId + "'");
			if (!isOtherReportExist.isEmpty())
				action = "RemoveOldAndInsert";
			else
				action = elementValueMap.get("actionType");
		
            //in case the same user create design with existing name
			if(action.equals("RemoveOldAndInsert")){
                 //if thre is more then one user that crete design with this name
				if (!isOtherReportExist.isEmpty())
					deleteFromId = generalDao.selectSingleString(
							"select t.formId from fg_s_reportdesignexp_all_v t where ReportDesignExpName = '"
									+designName + "' and t.CREATED_BY = '" + userId
									+ "'");
				else
					deleteFromId = formDao.getFromInfoLookup("ReportDesignExp", LookupType.NAME,
							designName, "id");
				formSaveDao.updateSingleStringInfo(
						"delete from fg_s_reportDesignExp_pivot where formid = '" + deleteFromId + "'");
			}
			
			//insert design to DB
				insertDesign(userId);
				


				
				}else if (eventAction.equals("importRecipeToFormulantRef")) {
			boolean insertFlag = false;
			
			try {
				String sql = "";
				String recipeId = "";
				String formulantRefId_ = "";
				recipeId = elementValueMap.get("RECIPE_ID");
				sql = "select materialIdList from FG_S_RECIPE_PIVOT where formId = '" + recipeId + "'";
				String materialIdList = generalDao.selectSingleStringNoException(sql);
				if(!materialIdList.equals("")) {
					String[] materialIdArray = materialIdList.split(",", -1);
					for (String materialId_ : materialIdArray) {
						formulantRefId_ = formSaveDao.getStructFormId("FormulantRef");
						sql = "insert into FG_S_FORMULANTREF_PIVOT (FORMID, PARENTID, MATERIALID, TABLETYPE, SOLID, AI, FORMCODE, FORMCODE_ENTITY, TIMESTAMP, CREATION_DATE, CREATED_BY, CHANGE_BY, ACTIVE) values ('"
								+ formulantRefId_ + "','" + formId + "','" + materialId_ + "','expFr','0','0','FormulantRef','FormulantRef',SYSDATE, SYSDATE,'" + userId + "','" + userId + "',1)"; // this formid is the FG_S_FORMULANTREF_PIVOT parentid
						formSaveDao.insertStructTableByFormId(sql, "FG_S_FORMULANTREF_PIVOT", formulantRefId_);
						insertFlag = true;
					}
				}
			} catch (Exception e) {
				generalUtilLogger.logWrite(e);
				throw new Exception("Recipe import failed!");
			}
			
			if(!insertFlag) {
				throw new Exception("No materials where found by this recipe!");
			}
		}
		else if(eventAction.equals("checkForCharacterMassBalance"))
		{
			String parentID = elementValueMap.get("parentID");
			String massBalanceActiveInx = elementValueMap.get("massBalanceActiveInx");
			String sql = "select max(character_massbalance||'@,@'||formcode||'@,@'||mass_balance_id) as character_massbalance \n"
						+" from (\n"
						+" select distinct nvl(m.chkcharactermassbalance,0) as charactermassbalance, m.massbalancename, e.formid as experiment_id, m.massbalancenumber as mass_balance_id, e.formcode \n"
						+"       ,decode(m.massbalancename,'','Experiment Mass Balance '||m.massbalancenumber||' ','Mass Balance '''||m.massbalancename||'''') as character_massbalance\n"
						+" from fg_s_experiment_pivot e, fg_s_experiment_massbalance m\n"
						+" where nvl(m.chkcharactermassbalance,0) = 1\n"
						+" and e.formid = m.parentid \n"
						+" and m.massbalancenumber <> '"+massBalanceActiveInx+"' \n"
						+" union all\n"
						+" select distinct nvl(sp.chkcharactermassbalance,0) as charactermassbalance, '' as massbalancename, ep.formid as experiment_id, sp.formid as mass_balance_id, sp.formcode \n"
						+"      ,'Step '||sp.stepname as character_massbalance\n"
						+" from fg_s_experiment_pivot ep, fg_s_step_pivot sp\n"
						+" where sp.experiment_id = ep.formid\n"
						+" and nvl(sp.chkcharactermassbalance,0) = 1\n"
						+" and sp.formid <> '" + formId + "' \n"
						+")\n"
						+" where experiment_id = '" + parentID + "'\n";
			
			return generalUtil.getEmpty(generalDao.selectSingleString(sql),"");
		}else if(eventAction.equals("refreshResultToUpdateMv"))
		{
			commonFunc.refreshResultMv();
			//generalDao.updateSingleString("begin dbms_mview.refresh('FG_I_RESUSINGTOUPDATE_MV'); end;");
		}
		else if(eventAction.equals("checkExpDuplicatRes"))
		{
			StringBuilder expDuplicatResMsg = null;
			String toReturn = "";
			String selectedExpCsv = elementValueMap.get("selectedExpCsv");
			StringBuilder noCharMsg=null;
			
			//toReturn = generalUtil.getNull(generalDao.selectSingleStringNoException("select distinct 1 from fg_i_resusingtoupdate_mv mv,fg_s_experiment_v ex where mv.experiment_id in ("+selectedExpCsv+") and ex.experiment_id = mv.experiment_id and mv.sampleid=ex.CHARACTERIZEDSAMPLE"));
			if(elementValueMap.get("isSetDesign").equals("false")){
				//if at least one experiment characteristic sample has duplicated results without “Main” selection, an error message should display
				String sql = "select distinct sample_id from( " + 
						" select count(*) over (partition by sample_id,invitemmaterial_id,result_name) as count_,sample_id from (\r\n" + 
						" select nvl2(sr.RESULT_ID,1,0) as selected_res,t.* from fg_i_sampleresults_v t, fg_i_selectedresults_v sr, fg_s_experiment_v ex,fg_s_step_v st where (t.sample_id = ex.CHARACTERIZEDSAMPLE or t.SAMPLE_ID = st.CHARACTERIZEDSAMPLE) and t.RESULT_ID = sr.RESULT_ID(+) and ex.experiment_id in ("+selectedExpCsv+") " + 
						" and ex.experiment_id = st.EXPERIMENT_ID(+)) " + 
						" where selected_res = 0) " + 
						" where count_>1";
				//fixed bug 8098
				/*String sql = "select distinct CHARACTERIZEDSAMPLE from( " + 
						"select distinct ex.CHARACTERIZEDSAMPLE from fg_i_resusingtoupdate_mv mv,fg_s_experiment_v ex where mv.experiment_id in ("+selectedExpCsv+") and ex.experiment_id = mv.experiment_id and mv.sampleid=ex.CHARACTERIZEDSAMPLE " + 
						"union all " + 
						"select distinct s.CHARACTERIZEDSAMPLE from fg_i_resusingtoupdate_mv mv,fg_s_experiment_v ex,fg_s_step_v s where mv.experiment_id in ("+selectedExpCsv+") and ex.experiment_id = mv.experiment_id and ex.experiment_id = s.experiment_id and mv.sampleid=s.CHARACTERIZEDSAMPLE)";*/
				
				if(!generalUtil.getNull(selectedExpCsv).isEmpty()){
					List<String> samplesId = generalDao.getListOfStringBySql(sql);
					for (String sampleId : samplesId) {
						String sampleName = formDao.getFromInfoLookup("Sample", LookupType.ID, sampleId, "name");
						if (expDuplicatResMsg == null) {
							expDuplicatResMsg = new StringBuilder();
							expDuplicatResMsg.append("<a href='#' onClick=\"checkAndNavigate(['" + sampleId + "','Sample'])\" >" + sampleName
									+ "</a>");
						} else {
							expDuplicatResMsg.append("<a href='#' onClick=\"checkAndNavigate(['" + sampleId + "','Sample'])\" >" + ", "
									+ sampleName + "</a>");
						}
					}
					
					toReturn = expDuplicatResMsg==null||expDuplicatResMsg.toString().isEmpty()?"":
							generalUtil.getSpringMessagesByKey("EXP_ANALYSIS_MAIN_RES", "")+" "+expDuplicatResMsg;
					
					noCharMsg=getNoCharasteristicSampleMessage(selectedExpCsv, "");
				}
				toReturn+=(noCharMsg==null?"":(!toReturn.isEmpty()?"</br>":"")+noCharMsg);
			}
			return toReturn;
		}
		else if(eventAction.equals("updateResults"))
		{
			if(formCode.equals("WorkupFeeding") || formCode.equals("WorkupDistillation")||formCode.equals("WorkupCrystallize")){
			String tableName="",resultColumn="",tableType="";
			if (formCode.equals("WorkupFeeding")) {
				tableName = "wufeedmaterialref";
				resultColumn = "ACTUALPURITY";

			} else if (formCode.equals("WorkupDistillation")) {
				tableName = "wudiststartmixref";
				resultColumn = "CONCENTRATION";
			}

			else if (formCode.equals("WorkupCrystallize")) {
				tableName = "WuCryMixDefineRef";
				tableType = "StartingMixtureDefinition";
				resultColumn = "CONCENTRATION";
			}

			String sql_ = "delete from FG_S_"+tableName+"_PIVOT where resultid_holder is not null and parentid = '"+formId+"'";
			formSaveDao.deleteStructTableByFormId(sql_, "FG_S_"+tableName+"_PIVOT", "");
			 
			List<Map<String, Object>> results = generalDao.getListOfMapsBySql(
					"select  t.RESULT_NAME, t.RESULT_MATERIAL_ID, t.RESULT_VALUE, t.RESULT_UOM_ID, t.RESULT_ID from FG_I_SELECTEDRESULTS_V t where t.RESULT_TEST_NAME = 'Analytical' and t.SAMPLE_ID ="
							+ elementValueMap.get("sampleId"));

			if (results != null) {
				commonFunc.setMaterialsInWorkup(results, tableType, tableName, resultColumn, formId, userId);
			}
			}
			else if(formCode.equals("Step")){
				JSONObject reactionTableData = new JSONObject(generalDao
						.getSingleStringFromClobNoException("select file_content from fg_clob_files where file_id = '"
								+ elementValueMap.get("reactionTableData") + "'"));
				List<String> materialRefIdList = generalDao
						.getListOfStringBySql("select formid from fg_s_materialref_v where parentid = '" + formId
								+ "' and sessionid is null and nvl(active,1)=1 and tabletype='Product'");
				for (String materialRefId : materialRefIdList) {
					//update current materialref data
					JSONObject rowData = reactionTableData.getJSONObject(materialRefId);
					if(generalUtil.getJsonValById(rowData.toString(), "tableType").equals("Product")){
						if(generalUtil.getJsonValById(rowData.toString(), "SAMPLE_ID").isEmpty()){
							rowData.put("resultid_holder", "");
							continue;
						}else{
							String assayRes = "";
							String sample_id = generalUtil.getJsonValById(rowData.toString(), "SAMPLE_ID");
							String material_id = generalUtil.getJsonValById(rowData.toString(), "INVITEMMATERIAL_ID");
							if(!sample_id.isEmpty() && !material_id.isEmpty()){
							    String sql ="select t.RESULT_ID,t.RESULT_VALUE from FG_I_SELECTEDRESULTS_V t where t.SAMPLE_ID = "+sample_id+" and t.RESULT_NAME = 'Assay' and t.RESULT_MATERIAL_ID = '"+material_id+"'";
							    Map<String,String> assayResMap = generalDao.sqlToHashMap(sql);
							    assayRes = assayResMap.get("RESULT_ID");
							    rowData.put("concInReactionMass", generalUtil.getNull(assayResMap.get("RESULT_VALUE")));
							}
							rowData.put("resultid_holder", generalUtil.getNull(assayRes));
						}
					}
					List<String> columnList = new ArrayList<>(Arrays.asList("TIMESTAMP", "CHANGE_BY"));
					List<String> data = new ArrayList<>(Arrays.asList("sysdate", userId));
					Iterator<String> rowItr = rowData.keys();
					while (rowItr.hasNext()) {
						String columnName = rowItr.next();
						columnList.add(columnName);
						String columnVal = generalUtil.getJsonValById(rowData.toString(), columnName);
						data.add(columnVal == null ? columnVal : "'" + columnVal + "'");
					}
					String setSql = "";
					for (int i = 0; i < columnList.size(); i++) {
						setSql += setSql.isEmpty() ? columnList.get(i) + "=" + data.get(i)
								: "," + columnList.get(i) + "=" + data.get(i);
					}
					String sql = "update fg_s_materialref_pivot set " + setSql + " where formid = '" + materialRefId
							+ "'";
					formSaveDao.updateStructTableByFormId(sql, "fg_s_materialref_pivot", columnList, materialRefId);
				}		
			}
			commonFunc.refreshResultMv();
		}else if(eventAction.equals("updateExpResultsMassBalance")){

			String stepId = generalUtil.getNull(elementValueMap.get("STEP_ID"));
			String webixTable = generalDao.selectSingleStringNoException(
					"select webixMassBalanceTable from fg_s_step_v where formid ='" + stepId + "'");
			boolean toUpdate = false;
			String sql = "select t.result_id,t.sample_mb as SAMPLE_ID,t.result_value,t.material_id,mv.result_name from fg_webix_output t ,fg_i_resusingtoupdate_mv mv where t.result_id = mv.formid and nvl(t.step_id,t.experiment_id)=mv.parentid and mv.parentid = '"
					+ stepId+"'";

			List<Map<String, Object>> listOfWebixResMaps = generalDao.getListOfMapsBySql(sql);
			if (listOfWebixResMaps != null && !listOfWebixResMaps.isEmpty()) {
				
				
				String updateWebixTable = "";
				for (Map<String, Object> entry : listOfWebixResMaps) {
					String mainResult = generalDao.selectSingleStringNoException(
							"select distinct t.RESULT_VALUE from FG_I_SELECTEDRESULTS_V t "
									+ " where t.RESULT_NAME = '" + entry.get("RESULT_NAME")
									+ "' and t.RESULT_MATERIAL_ID = '" + entry.get("MATERIAL_ID")
									+ "' and t.sample_id = '" + entry.get("SAMPLE_ID") + "' and rownum<=1");
					if (generalUtil.getNull(mainResult).isEmpty()) {
						continue;
					}
					// perc_of_product - update
					// formSaveDao.updateSingleStringInfoNoTryCatch(sql);
					updateWebixTable = "update fg_webix_output wbx set result_value = '" + mainResult + "'"
							+ " where wbx.result_id = '" + entry.get("RESULT_ID") + "'";

					String fullData = generalDao.getSingleStringFromClobNoException(
							"select file_content from fg_clob_files where file_id = '"
									+ generalUtil.getNull(webixTable) + "'");
					
					JSONArray webix_arr = new JSONArray(fullData);
					for(int j=0;j< webix_arr.length();j++){
						JSONObject currObj = webix_arr.getJSONObject(j);
						JSONArray dataArr = currObj.getJSONArray("data");
						JSONObject fieldsData = currObj.getJSONObject("fieldsData");
						String ddlSample = fieldsData.getString("ddlSample");
					for (int i = 0; i < dataArr.length(); i++) {
						String clob_perc_of_product = dataArr.getJSONObject(i).getString("perc_of_product");
						String material_id = dataArr.getJSONObject(i).getString("inv_item_material_id");
						String substance_type = "";
						try {
							substance_type = dataArr.getJSONObject(i).getString("substance_type");
						} catch (Exception e) {
							substance_type = "";
						}
						// result_type FG_I_MASSBALANCE_STEP_V
						String result_type = generalUtil.getNull(
								generalDao.selectSingleStringNoException("select case " + "  when lower('"
										+ entry.get("RESULT_NAME") + "') like '%impurity%' then 'impurity' "
										+ "  when lower('" + entry.get("RESULT_NAME")
										+ "') like '%assay%' then 'product' " + "  else '' end from dual"));
						if (clob_perc_of_product.equals(entry.get("RESULT_VALUE"))
								&& material_id.equals(entry.get("MATERIAL_ID"))
								&& ddlSample.equals(entry.get("SAMPLE_ID"))
								&& result_type.equals(substance_type)) {
							dataArr.getJSONObject(i).put("perc_of_product", mainResult);
							toUpdate = true;
						}
					}
					currObj.put("data", dataArr);
					webix_arr.put(j, currObj);
					}
					if (toUpdate) {
						
						sql = "update fg_clob_files set file_content = '" + webix_arr + "'" + " where file_id = '"
								+ generalUtil.getNull(webixTable) + "'";
						formSaveDao.updateSingleStringInfoNoTryCatch(sql);
						formSaveDao.updateSingleStringInfoNoTryCatch(updateWebixTable);
					}
				}
				
			}
			commonFunc.refreshResultMv();
		
		}else if(eventAction.equals("updateResultsMassBalance")){
			try {
				String ddlSample = "";
				if (formCode.equals("Step") || formCode.equals("Experiment")) {
					String tableID = generalUtil.getNull(elementValueMap.get("MBTABLE_ID"));
					String tableName = generalUtil.getNull(elementValueMap.get("entityName"))==null?"webixMassBalanceTable":elementValueMap.get("entityName");
					boolean toUpdate = false;
					if (!tableID.isEmpty()) {
						String streamOrder_ = tableID.substring(tableID.lastIndexOf("_") + 1);
						Integer streamOrder = Integer.parseInt(streamOrder_) - 1;
						String webixTable = generalDao.selectSingleStringNoException(
								"select "+tableName+" from fg_s_"+formCode+"_v where formid ='" + formId + "'");
						
						String sql = "select t.result_id,t.sample_mb as SAMPLE_ID,t.result_value,t.material_id,mv.result_name from fg_webix_output t ,fg_i_resusingtoupdate_mv mv where t.result_id = mv.formid and nvl(t.step_id,t.experiment_id)=mv.parentid and mv.parentid = "
								+ formId;

						List<Map<String, Object>> listOfWebixResMaps = generalDao.getListOfMapsBySql(sql);
						if (listOfWebixResMaps != null && !listOfWebixResMaps.isEmpty()) {
							String fullData = generalDao.getSingleStringFromClobNoException(
									"select file_content from fg_clob_files where file_id = '"
											+ generalUtil.getNull(webixTable) + "'");
							JSONArray webix_arr = new JSONArray(fullData);
							JSONObject currObj = webix_arr.getJSONObject(streamOrder);
							JSONArray dataArr = currObj.getJSONArray("data");
							JSONObject fieldsData = currObj.getJSONObject("fieldsData");
							ddlSample = fieldsData.getString("ddlSample");
							String updateWebixTable = "";
							for (Map<String, Object> entry : listOfWebixResMaps) {
								String mainResult = generalDao.selectSingleStringNoException(
										"select distinct t.RESULT_VALUE from FG_I_SELECTEDRESULTS_V t "
												+ " where t.RESULT_NAME = '" + entry.get("RESULT_NAME")
												+ "' and t.RESULT_MATERIAL_ID = '" + entry.get("MATERIAL_ID")
												+ "' and t.sample_id = '" + entry.get("SAMPLE_ID") + "' and rownum<=1");
								if (generalUtil.getNull(mainResult).isEmpty()) {
									continue;
								}
								// perc_of_product - update
								// formSaveDao.updateSingleStringInfoNoTryCatch(sql);
								updateWebixTable = "update fg_webix_output wbx set result_value = '" + mainResult + "'"
										+ " where wbx.result_id = '" + entry.get("RESULT_ID") + "'";
								for (int i = 0; i < dataArr.length(); i++) {
									String clob_perc_of_product = dataArr.getJSONObject(i).getString("perc_of_product");
									String material_id = dataArr.getJSONObject(i).getString("inv_item_material_id");
									String substance_type = "";
									try {
										substance_type = dataArr.getJSONObject(i).getString("substance_type");
									} catch (Exception e) {
										substance_type = "";
									}
									// result_type FG_I_MASSBALANCE_STEP_V
									String result_type = generalUtil.getNull(
											generalDao.selectSingleStringNoException("select case " + "  when lower('"
													+ entry.get("RESULT_NAME") + "') like '%impurity%' then 'impurity' "
													+ "  when lower('" + entry.get("RESULT_NAME")
													+ "') like '%assay%' then 'product' " + "  else '' end from dual"));
									if (clob_perc_of_product.equals(entry.get("RESULT_VALUE"))
											&& material_id.equals(entry.get("MATERIAL_ID"))
											&& ddlSample.equals(entry.get("SAMPLE_ID"))
											&& result_type.equals(substance_type)) {
										dataArr.getJSONObject(i).put("perc_of_product", mainResult);
										toUpdate = true;
									}
								}
							}
							if (toUpdate) {
								currObj.put("data", dataArr);
								webix_arr.put(streamOrder, currObj);
								sql = "update fg_clob_files set file_content = '" + webix_arr + "'" + " where file_id = '"
										+ generalUtil.getNull(webixTable) + "'";
								formSaveDao.updateSingleStringInfoNoTryCatch(sql);
								formSaveDao.updateSingleStringInfoNoTryCatch(updateWebixTable);

							}
						}
					}
				}
				commonFunc.refreshResultMv();
				return ddlSample;
			}
			catch(Exception e){
				//TODO
			}
		}else if(eventAction.equals("changeExpStatusToActive")){
			String approverId = elementValueMap.get("APPROVER_ID");//formDao.getFromInfoLookup("Experiment", LookupType.ID,formId, "APPROVER_ID");
			if(!userId.equals(approverId)){
				return "-1";
			}
		}else if(eventAction.equals("hideWebixMassBalanceButton")){
			String sampleId = generalUtil.getNull(elementValueMap.get("SAMPLE_ID"));
			/*String streamOrder_ = tableID.substring(tableID.lastIndexOf("_") + 1);
			Integer streamOrder = Integer.parseInt(streamOrder_);*/
			String sql ="select distinct 1 from fg_webix_output t, FG_I_RESUSINGTOUPDATE_MV mv where t.result_id = mv.formid and mv.parentid = '"+formId+"' and t.sample_mb ='"+sampleId+"' and t.result_is_active = 1";
			String isResToUpdate = generalDao.selectSingleStringNoException(sql);
			if(!generalUtil.getNull(isResToUpdate).isEmpty()){
				return "1";
			}
		}else if(eventAction.equals("ShowExpWebixMassBalanceBtn")){
			String stepId = elementValueMap.get("CURR_STEP_ID");//formDao.getFromInfoLookup("Experiment", LookupType.ID,formId, "APPROVER_ID");
			String resToUpdate = generalDao.selectSingleStringNoException("select distinct 1 from fg_i_resusingtoupdate_mv t where t.experiment_id = '"+formId+"' and t.type_ = 'Mass Balance' and t.parentid ='"+stepId+"'");
			if(!generalUtil.getNull(resToUpdate).isEmpty()){
				String expStatus = generalUtil.getNull(generalDao.selectSingleStringNoException("select distinct experimentStatusname from fg_s_step_all_v where step_id = '" + stepId + "'"));
				if(expStatus.equals("Completed")||expStatus.equals("Approved")||expStatus.equals("Failed")||expStatus.equals("Cancelled")){
					return "2";
				}
			}
			return generalUtil.getNull(resToUpdate);
			
		}else if(eventAction.equals("AddOptionsToDDl")){
			if(formCode.equals("Step")){
				String stepSequence = elementValueMap.get("formNumberId");
				JSONObject json = new JSONObject();
				String sql = " select distinct runnumber, runstatusname from fg_s_exprunplanning_all_v t where t.EXPERIMENTID = '" + generalUtil.getNull(elementValueMap.get("EXPERIMENT_ID")) + "' and t.STEPNUMBER = '"+generalUtil.getNull(stepSequence)+"' and lower(t.PREPARATION_RUN) = 'run'";
				//String sql = "select t.FORMNUMBERID, t.RUNNUMBER from fg_s_step_v t where t.formId = '" + formId + "' and t.runnumber is not null union all select t.FORMNUMBERID, t.runnumber from fg_s_step_v t where t.EXPERIMENT_ID = '" + generalUtil.getNull(elementValueMap.get("EXPERIMENT_ID")) + "' and t.runnumber is not null and formId<>'"+formId +"' and t.formNumberId = '"+stepSequence+"'";
				List<Map<String,Object>> runSteps = generalDao.getListOfMapsBySql(sql);
				for(Map<String, Object> stepData:runSteps){
					try{
					json.put(stepData.get("RUNNUMBER").toString(), stepData.get("RUNSTATUSNAME").toString());
					}catch(Exception e){
						json = new JSONObject();
					}
				}
				return json.toString();
			}
			else if(formCode.equals("ExperimentCP")){
				JSONObject json = new JSONObject();
				String sql = "select distinct '0' as runnumber , 'Active' runstatusname"
						+ "		from fg_s_step_v where experiment_id = '"+formId+"' and PREPARATION_RUN = 'Preparation'"
						+ " union all"
						+ " select distinct runnumber, runstatusname from fg_s_exprunplanning_all_v t where t.EXPERIMENTID ='"+formId+"'";
				List<Map<String,Object>> runSteps = generalDao.getListOfMapsBySql(sql);
				for(Map<String, Object> stepData:runSteps){
					try{
					json.put(stepData.get("RUNNUMBER").toString(), stepData.get("RUNSTATUSNAME").toString());
					}catch(Exception e){
						json = new JSONObject();
					}
				}
				return json.toString();
			}
			
		}else if(eventAction.equals("getFormIdByRunNumber")){
			String toReturn = "";
			try{
				String stepSequence = generalUtil.getNull(elementValueMap.get("formNumberId"));
				String sql = "select distinct formid from fg_s_step_v where experiment_id = '" + generalUtil.getNull(elementValueMap.get("EXPERIMENT_ID"))+"' and formnumberId = '"+stepSequence+"' and runNumber = '"+generalUtil.getNull(elementValueMap.get("RUNNUMBER"))+"'";
			    //String sql = "select formid from fg_s_step_v where runnumber = '"+stepSequence+"' and EXPERIMENT_ID = '" + generalUtil.getNull(elementValueMap.get("EXPERIMENT_ID"))+"' and formnumberid ='"+generalUtil.getNull(elementValueMap.get("formNumberId")+"'");
			    toReturn = generalUtil.getNull(generalDao.selectSingleStringNoException(sql));
			}catch(Exception e){
				toReturn = "-1";
			}
			return toReturn;
		}
		else if (eventAction.equals("getReportDesignData")) {
			String toReturn = "";
			String id = elementValueMap.get("designId");
			try {
				StringBuilder expDesignCsv = new StringBuilder();
				Map<String, List<String>> stepsDesignMap = new HashMap<String, List<String>>();
				Map<String, List<String>> impuritiesDesignStepMap = new HashMap<String, List<String>>();
				Map<String, List<String>> parametersDesignMap = new HashMap<String, List<String>>();
				;
				// Collect data from design form

				// expDesignCsv -experiment data-TODO change this code to query
				String sql = "select *  from Fg_s_Reportdesignexp_All_v t where t.formId='" + id + "'";
				Map<String, String> expDesignMap = new HashMap<String, String>();
				expDesignMap = generalDao.sqlToHashMap(sql);
				expDesignCsv = new StringBuilder();
				for (Map.Entry<String, String> m : expDesignMap.entrySet()) {
					if (!(m.getKey().toLowerCase().contains("step")) && (m.getValue().equals("1"))) {
						expDesignCsv.append(m.getKey());
						expDesignCsv.append(',');
					}
				}

				// stepsDesignMap-steps data
				sql = "select STEPSDESIGN from Fg_s_Reportdesignexp_All_v t where t.formId=" + id;
				String stepDesignId = generalDao.selectSingleString(sql);
				stepsDesignMap = new HashMap<String, List<String>>();
				stepsDesignMap = generalUtil.jsonStringToMapList(generalDao.selectSingleString(
						"select t.file_content from fg_clob_files t where t.file_id = '" + stepDesignId + "'"));

				// impuritiesDesignMap-impurities data
				sql = "select IMPURITIESSTEPSDESIGN from Fg_s_Reportdesignexp_All_v t where t.formId=" + id;
				String impuritiesDesignSteps = generalDao.selectSingleString(sql);
				impuritiesDesignStepMap = new HashMap<String, List<String>>();
				if (impuritiesDesignSteps != null)
					impuritiesDesignStepMap = generalUtil.jsonStringToMapList(impuritiesDesignSteps);

				// parametersDesignMap-parameters data
				sql = "select PARAMETERSDESIGN from Fg_s_Reportdesignexp_All_v t where t.formId=" + id;

				String parametersDesignId = generalDao.selectSingleString(sql);
				parametersDesignMap = new HashMap<String, List<String>>();
				String parameterDesignsString = "";
				if (parametersDesignId != null)
					parameterDesignsString = generalDao
							.selectSingleString("select t.file_content from fg_clob_files t where t.file_id = '"
									+ parametersDesignId + "'");
				if (!parameterDesignsString.equals("{}") && !parameterDesignsString.isEmpty())
					parametersDesignMap = generalUtil.jsonArrayStringToMapWithList(parameterDesignsString);

				// getDesignDataById(id, expDesignCsv, stepsDesignMap, impuritiesDesignStepMap,
				// parametersDesignMap);
				String htmlBuilder = "";
				String htmlStyle="<style>.prev{font-family:Arial;font-size:Small;}.header{font-weight: bold;font-size:Large;}.section{font-weight: bold;font-size:Medium;}.header2{font-weight: bold;font-size:Large;}</style>";
				htmlBuilder=htmlBuilder+htmlStyle;
				htmlBuilder = htmlBuilder+"<ul class=\"prev\">";
				// Experiment
				List<String> colDesignExpList = Arrays.asList(expDesignCsv.toString().split("\\s*,\\s*"));
				JSONObject jDictionary = springNameDictionary.getDictionaryJson();
				Map<String, String> ExperimentSectionMap = new HashMap<String, String>();
				Map<String, String> StepSectionMap = new HashMap<String, String>();
				ExperimentSectionMap.put("General Data", "");
				ExperimentSectionMap.put("Mass Balance", "");

				String expDataString = "";
				if(colDesignExpList.size()>1) {
				htmlBuilder = htmlBuilder + "<li ><p class=\"header\">Experiment Data</p><ul >";
				for (String colExpDesign : colDesignExpList) {

					if (jDictionary.has(colExpDesign.toUpperCase() + ".section")) {
						String colExpDesignSection = (String) jDictionary.get(colExpDesign.toUpperCase() + ".section");

						String colDesignExpression = (String) jDictionary.get(colExpDesign.toUpperCase()+ ".title");
						if (ExperimentSectionMap.get(colExpDesignSection).equals(""))
							ExperimentSectionMap.put(colExpDesignSection, colDesignExpression);
						else
							ExperimentSectionMap.put(colExpDesignSection,
									ExperimentSectionMap.get(colExpDesignSection) + " , " + colDesignExpression);

					}

				}
				if (!ExperimentSectionMap.get("General Data").equals(""))
					htmlBuilder = htmlBuilder + "<li><p class=\"header section\">General Data</p><ul><li>" + ExperimentSectionMap.get("General Data")
							+ "</li></ul></li>";
				if (!ExperimentSectionMap.get("Mass Balance").equals(""))
					htmlBuilder = htmlBuilder + "<li><p class=\"header section\">Mass Balance<p/><ul><li>" + ExperimentSectionMap.get("Mass Balance")
							+ "</li></ul></li>";
				/*if (parametersDesignMap.get("Experiment") != null) {
					if (!parametersDesignMap.get("Experiment").equals("")) {

						htmlBuilder = htmlBuilder + "<li ><p class=\"header section\">Parameters</p><ul><li>"
								+ generalUtil.listToCsv(parametersDesignMap.get("Experiment")) + "</li></ul></li>";
					}
				}*/
				htmlBuilder = htmlBuilder + "</ul></li>";
				}
				if(stepsDesignMap.size()==1&&stepsDesignMap.get(stepsDesignMap.keySet().toArray()[0]).contains("")&&impuritiesDesignStepMap.isEmpty())
					stepsDesignMap.clear();
				if(!stepsDesignMap.isEmpty()) {
				htmlBuilder = htmlBuilder + "<li ><p class=\"header\">Steps Data</p><ul>";
				for (Map.Entry<String, List<String>> step : stepsDesignMap.entrySet()) {
					if (!step.getValue().contains("")||impuritiesDesignStepMap.containsKey( step.getKey()))
						htmlBuilder = htmlBuilder + "<li ><p class=\"header2\">Step " + step.getKey() + "</p><ul>";
					StepSectionMap.put("General", "");
					StepSectionMap.put("Analytical Results", "");
					StepSectionMap.put("Reactants", "");
					StepSectionMap.put("Solvents", "");
					StepSectionMap.put("Mass Balance", "");
					StepSectionMap.put("Impurities", "");
					for (String colDesign : step.getValue()) {
						if (colDesign.contains("numConcentrationImpStep"))
							colDesign = colDesign.split(";")[0];
						if (jDictionary.has(colDesign.toUpperCase() + ".section")) {
							String colStepDesignSection = (String) jDictionary
									.get(colDesign.toUpperCase() + ".section");

							String colDesignExpression = (String) jDictionary.get(colDesign.toUpperCase() + ".title");
							if (StepSectionMap.get(colStepDesignSection).equals(""))
								StepSectionMap.put(colStepDesignSection, colDesignExpression);
							else
								StepSectionMap.put(colStepDesignSection,
										StepSectionMap.get(colStepDesignSection) + " , " + colDesignExpression);

						}

					}

					if (!StepSectionMap.get("General").equals(""))
						htmlBuilder = htmlBuilder + "<li c><p class=\"header section\">General</p><ul><li>" + StepSectionMap.get("General")
								+ "</li></ul></li>";
					if (!StepSectionMap.get("Analytical Results").equals(""))
						htmlBuilder = htmlBuilder + "<li ><p class=\"header section\">Analytical Results</p><ul><li>"
								+ StepSectionMap.get("Analytical Results") + "</li></ul></li>";
					if (!StepSectionMap.get("Reactants").equals(""))
						htmlBuilder = htmlBuilder + "<li ><p class=\"header section\">Reactants</p><ul><li>" + StepSectionMap.get("Reactants")
								+ "</li></ul></li>";
					if (!StepSectionMap.get("Solvents").equals(""))
						htmlBuilder = htmlBuilder + "<li ><p class=\"header section\">Solvents</p><ul><li>" + StepSectionMap.get("Solvents")
								+ "</li></ul></li>";
					if (!StepSectionMap.get("Mass Balance").equals(""))
						htmlBuilder = htmlBuilder + "<li ><p class=\"header section\">Mass Balance</p><ul><li>" + StepSectionMap.get("Mass Balance")
								+ "</li></ul></li>";

					//// impurities
					if (!StepSectionMap.get("Impurities").equals("")/*&&!impuritiesDesignStepMap.isEmpty()*/)
						htmlBuilder = htmlBuilder + "<li ><p class=\"header section \">Impurities</p><ul><li>" + StepSectionMap.get("Impurities")
								+ "</li></ul></li>";
					else if (impuritiesDesignStepMap.get(step.getKey()) != null) {
						
							List<String> ImpuritiesNames = new ArrayList<String>();
							ImpuritiesNames = generalDao.getListOfStringBySql(
									"select t.INVITEMMATERIALNAME from FG_S_INVITEMMATERIAL_ALL_V t where t.INVITEMMATERIAL_ID in ("
											+ generalUtil.listToCsv(impuritiesDesignStepMap.get(step.getKey())) + ")");

							htmlBuilder = htmlBuilder + "<li><p class=\"header section\">Impurities</p><ul><li>"
									+ generalUtil.listToCsv(ImpuritiesNames) + "</li></ul></li>";
						}
				
				
					//// parameters
				
				/*	if (parametersDesignMap.get(step.getKey().replace(" ", "")) != null
							|| parametersDesignMap.get("All steps") != null) {
						
							String params = "";
							if (parametersDesignMap.get(step.getKey().replace(" ", ""))!=null)
								params = generalUtil.listToCsv(parametersDesignMap.get(step.getKey().replace(" ", "")));
							else
								params = generalUtil.listToCsv(parametersDesignMap.get("All steps"));
							if ( parametersDesignMap.get(step.getKey().replace(" ", ""))!=null &&parametersDesignMap.get("All steps")!=null)
								params = generalUtil.listToCsv(parametersDesignMap.get("All steps")) + "," + generalUtil
										.listToCsv(parametersDesignMap.get(step.getKey().replace(" ", "")));
							htmlBuilder = htmlBuilder + "<li> <p class=\"header section\">Parameters</p><ul><li>" + params + "</li></ul></li>";
						}
				
					htmlBuilder = htmlBuilder + "</ul></li>";
			}*/
				
					htmlBuilder = htmlBuilder + "</ul></li>";
				
				}
				
				htmlBuilder = htmlBuilder + "</ul></li>";	
				}
				if(!parametersDesignMap.isEmpty()) {
				htmlBuilder = htmlBuilder + "<li ><p class=\"header\">Parameters</p><ul>";
				for (Map.Entry<String, List<String>> step:parametersDesignMap.entrySet()) {
					
					if (!step.getValue().contains("")) {
						if( step.getKey().equals("All steps"))
							htmlBuilder = htmlBuilder + "<li ><p><b>All steps:</b> "+generalUtil.listToCsv(step.getValue())+"</p></li>";
						else if( step.getKey().equals("Experiment"))
						    htmlBuilder = htmlBuilder + "<li ><p><b>Experiment: </b>"+generalUtil.listToCsv(step.getValue())+"</p></li>";
						
						else htmlBuilder = htmlBuilder + "<li ><p><b>Step "+step.getKey()+":</b> "+generalUtil.listToCsv(step.getValue())+"</p></li>";
					   
						
					}
				}
			
				}
				htmlBuilder = htmlBuilder + "</li></ul>";
				htmlBuilder = htmlBuilder + "</ul>";
				toReturn = htmlBuilder;
			} catch (Exception e) {

				toReturn = "-1";
				e.printStackTrace();
			}

			return toReturn;
		}
		// fill session wuth design data from DB
		else if (eventAction.equals("loadDesignDataToSession")) {
			String toReturn = "";
			try {
				//clear old data
				generalUtilDesignData.clearSession();
				fillDesignDataSession(elementValueMap.get("designId"));
			} catch (Exception e) {
				toReturn = "-1";
			}
			return toReturn;
		}
		else if (eventAction.equals("useDesign")) {
			String toReturn = "";
			try {
				// fill session with all information not fill during go from step to step
				preperReport(elementValueMap);
			} catch (Exception e) {
				toReturn = "-1";
			}
			return toReturn;
		}
		else if (eventAction.equals("clearDesign")) {
			String toReturn = "";
			try {
                 //clear temp session with design data
				generalUtilDesignData.clearSession();
				

			} catch (Exception e) {
				toReturn = "-1";
			}
			return toReturn;
		}
		//get Design data from session 
		else if (eventAction.equals("getDesignData")) {
			String toReturn = "";
			try {
				StringBuilder sb = new StringBuilder();
				String spesificSimpleStepDate = "";
				String spesificImpuritiesStepDate = "";

				if (generalUtilDesignData.getStepDesignMapwithList()
						.containsKey(generalUtilDesignData.getAdditionalInfo().get("STEP_ID"))) {
					spesificSimpleStepDate = generalUtil.listToCsv(generalUtilDesignData
							.getSpesificListStepDesignToMap(generalUtilDesignData.getAdditionalInfo().get("STEP_ID")));
					sb.append("stepsSimpleDesign:");
					sb.append(spesificSimpleStepDate);
					sb.append('@');
				} else
					sb.append("stepsSimpleDesign:-1@");
				if (generalUtilDesignData.getStepImpuritesDesignMap()
						.containsKey(generalUtilDesignData.getAdditionalInfo().get("STEP_ID"))) {
					spesificImpuritiesStepDate = generalUtil.listToCsv(generalUtilDesignData
							.getSpesificImpuritiesListToMap(generalUtilDesignData.getAdditionalInfo().get("STEP_ID")));
					sb.append("impuritiesStepDesign:");
					sb.append(spesificImpuritiesStepDate);
					sb.append('@');
				} else
					sb.append("impuritiesStepDesign:-1@");
				if (!generalUtilDesignData.getExperimentDataList().isEmpty()) {
					String experimentData = generalUtil.listToCsv(generalUtilDesignData.getExperimentDataList());
					sb.append("experimentData:");
					sb.append(experimentData);
					sb.append('@');
				} else
					sb.append("experimentData:-1@");
				if (!generalUtilDesignData.getParametersDataMap().isEmpty()) {
					String parametersData = generalUtil.MapOfListToJson(generalUtilDesignData.getParametersDataMap());
					sb.append("parametersData;");
					sb.append(parametersData);
					sb.append('@');
				} else
					sb.append("parametersData;-1@");
				if (!generalUtilDesignData.getAdditionalInfo().isEmpty()) {
					String additionalInfo = generalUtil.mapToJson(generalUtilDesignData.getAdditionalInfo());
					sb.append("additionalInfo;");
					sb.append(additionalInfo);
					sb.append('@');
				} else
					sb.append("additionalInfo;-1@");

				return sb.toString();
			} catch (Exception e) {
				toReturn = "-1";
			}
			return toReturn;

		}
		else if(eventAction.equals("getDesignIdOrNameFromSession")) {
		   String designHolder=elementValueMap.get("designHolder");
		   if(designHolder.equals("NAME")) {
			   if(generalUtilDesignData.getAdditionalInfo().get("reportDesignExpName")!=null) {
				   return generalUtilDesignData.getAdditionalInfo().get("reportDesignExpName");
			   }
			   else 	return generalUtilDesignData.getAdditionalInfo().get("REPORTDESIGNEXPNAME");
		   }		   
		   else if(designHolder.equals("ID")) {
			   String formDesignId="";
			   if(generalUtilDesignData.getAdditionalInfo().get("formId")!=null) {
				   return generalUtilDesignData.getAdditionalInfo().get("formId");
			   }
			   else 	return generalUtilDesignData.getAdditionalInfo().get("FORMID");
		   }
		
		}
		else if(eventAction.equals("updateFieldInSession")) {
			generalUtilDesignData.getAdditionalInfo().put(elementValueMap.get("field"), elementValueMap.get("value"));
		}
		else if(eventAction.equals("getProjectAndSubProjectFromSession")) {
			if(generalUtilDesignData.getAdditionalInfo().get("project")==null||generalUtilDesignData.getAdditionalInfo().get("subProject")==null)
				return generalUtilDesignData.getDesignFormElementValueMap().get("project")+'@'+generalUtilDesignData.getDesignFormElementValueMap().get("subProject");
			else	return	generalUtilDesignData.getAdditionalInfo().get("project")+'@'+generalUtilDesignData.getAdditionalInfo().get("subProject");
		}
		else if(eventAction.equals("checkDuplicateSpreadsheetName")) {
			String spreadsheetName = generalUtil.getNull(elementValueMap.get("spreadsheetTemplaName"));
			String sql = "select distinct t.formId||','||to_char(t.TIMESTAMP,'dd/MM/yyyy  HH24:MI:SS')||','||t.change_by from FG_S_SPREADSHEETTEMPLA_V t where lower(t.SpreadsheetTemplaName) = lower('"+spreadsheetName+"') and nvl(active,1)=1 and sessionid is null and t.CREATOR_ID = '"+userId+"' and formid <> '"+formId+"'";
			String res = generalDao.selectSingleStringNoException(sql);
			return generalUtil.getNull(res);
		}
		else if(eventAction.equals("checkNonFamiliar")) {
			String toReturn = "";
			if(formCode.startsWith("Experiment")){
				toReturn = commonFunc.get_inventory_unfamiliar_list(formId,Arrays.asList("-1"),formCode,null,true);
			}else if(formCode.startsWith("Step")){
				ArrayList<String> materialList = new ArrayList<>();
				if(formCode.equals("Step")){//organic/CP
					JSONObject reactionTableData = new JSONObject(elementValueMap.get("reactionData"));
					String[] currentFormIds = JSONObject.getNames(reactionTableData);
					if (currentFormIds != null) {
						TreeSet<String> insertFormIdList = new TreeSet<String>();
						insertFormIdList.addAll(Arrays.asList(currentFormIds));
						for (String materialRedId : insertFormIdList) {
							JSONObject rowData = reactionTableData.getJSONObject(materialRedId);
							String materialId = generalUtil.getJsonValById(rowData.toString(), "INVITEMMATERIAL_ID");
							if (materialId.equals("0")
									|| materialId.isEmpty()) {
								continue;
							}
							materialList.add(materialId);
						}
					}
				}
				String experimentId = elementValueMap.get("EXPERIMENT_ID");
				toReturn = commonFunc.get_inventory_unfamiliar_list(experimentId,Arrays.asList(formId),formCode,generalUtil.listToCsv(materialList),false);
			}
			return toReturn;
		}
		else if(eventAction.equals("trainedUser")) {
			String materialIds = elementValueMap.get("materialListNonFamiliar");
			commonFunc.trainedUser(materialIds,userId);
		}
		else if(eventAction.equals("copyFunctionTable")) {
			 String currentMaterialFuncId=elementValueMap.get("PARENTID");
		
			String  tableRef ="fg_s_FunctionRuleRef_pivot";
			 String colList = "," + generalDao.getTableColCsv(tableRef) + ",";
			colList = colList.replace(",PARENTID,", ",").replace(",SESSIONID,", ",");
			colList = colList.substring(1, colList.length() - 1);
			String sql = String.format(
					"insert into %1$s r (%2$s , PARENTID,SESSIONID )  select %2$s,%3$s,%4$s from fg_s_FunctionRuleRef_pivot t where t.parentId="+elementValueMap.get("MATERIALFUNC_ID")+" and t.sessionId is null"
							+ " and nvl(active,'1')='1'"
					,
					tableRef, colList,currentMaterialFuncId,generalUtilFormState.checkAndReturnSessionId("FunctionRuleRef", currentMaterialFuncId));
			formSaveDao.updateSingleStringInfoNoTryCatch(sql);	
			
			//String sql="select * from fg_s_FunctionRuleRef_pivot t where t.parentId="+elementValueMap.get("MATERIALFUNC_ID")+" and t.sessionId is null";
			
		} else if(eventAction.equals("createNewStepIframeData")) {
			String experimentId = formId;
			return commonFunc.createNewStepIframeData(experimentId,elementValueMap).toString();
		}
		else if (eventAction.equals("saveStepMinFrData")) {
			String sql = "update fg_s_step_pivot t set t." + elementValueMap.get("objToSave").split(";")[0] + " = '"
					+ elementValueMap.get("objToSave").split(";")[1] + "' where t.formid = '" + formId + "'";
			String retVal;
			retVal = formSaveDao.updateStructTableByFormId(sql, "fg_s_step_pivot",
					Arrays.asList(elementValueMap.get("objToSave").split(";")[0]), formId);
			return retVal;
		}
		else if(eventAction.equals("getExperimentGroupNumber"))
		{
			String groupNumber=generalDao.selectSingleString("select groupNumber from fg_s_experimentGroup_all_v where formId="+elementValueMap.get("experimentGroup_id"));
			return groupNumber;
		}
		else if(eventAction.equals("updateComponentTable"))
		{
		formCode="MaterialComponent";
		String newformId=formSaveDao.getStructFormId(formCode);
		String sessionId = generalUtilFormState.checkAndReturnSessionId("MaterialComponent", formId);
		String wherePart = "APPROVALDATE is null and MATERIAL_ID is null \r\n" + 
				"and MATERIALCOMPONENTNAME is null \r\n" + 
				"and CONCENTRATION is null \r\n" + 
				"and FUNCTION_ID is null ";
				
		String formEmptyId = generalDao.getCSVBySqlNoException(
				String.format("select %1$s from fg_s_%2$s_v where parentid ='%3$s' and sessionid ='%4$s' and %5$s",
						"MaterialComponent_id", formCode, formId, sessionId,wherePart),false);
		String material_id = elementValueMap.get("material_id");
		if (!generalUtil.getNull(material_id).isEmpty()) {
		//if(formEmptyId == null || formEmptyId.isEmpty()) // this condition is to avoid insert empty row if there is one for the parentid
		//{
			Map<String,String> resultData = new HashMap<>();
			if(elementValueMap.containsKey("SAMPLE_ID")&&!elementValueMap.get("SAMPLE_ID").isEmpty()){
				//gets the assay main result for the selected material
				String sqlString = "Select RESULT_VALUE,to_char(RESULT_DATE,'"+generalUtil.getConversionDateFormat()+"') AS RESULT_DATE\n"
						+ " from FG_I_SELECTEDRESULTS_V r\n"
						+ "where r.RESULT_MATERIAL_ID = '"+elementValueMap.get("material_id")+"' and r.SAMPLE_ID ='" + elementValueMap.get("SAMPLE_ID")
						+ "' and RESULT_NAME = 'Assay'";
				resultData = generalDao.sqlToHashMap(sqlString);
			}
			String sql = "insert into FG_S_MATERIALCOMPONENT_PIVOT "
					+ "(TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE,FORMCODE_ENTITY,CREATED_BY,CREATION_DATE,MATERIAL_ID,APPROVALDATE,CONCENTRATION)"
					+ " values (sysdate,'" + userId + "'," + sessionId + ",'1'," + newformId + "," + formId + ",'"
					+ formCode + "','" + formCode + "','" + userId + "',sysdate,'"+elementValueMap.get("material_id")+"','"+generalUtil.getNull(resultData.get("RESULT_DATE"))+"','"+generalUtil.getNull(resultData.get("RESULT_VALUE"))+"')";
		String	insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_" + formCode + "_PIVOT", newformId);
	}
		
	
		}
		else if(eventAction.equals("updateInstrumentTable"))
		{
			String instrument_id = elementValueMap.get("_INSTRUMENT_ID");
			if (!generalUtil.getNull(instrument_id).isEmpty()) {
				formCode = "InstrumentRef";
				String newformId = formSaveDao.getStructFormId(formCode);
				String sessionId = generalUtilFormState.checkAndReturnSessionId("InstrumentRef", formId);
				String sql = "insert into FG_S_" + formCode + "_PIVOT "
						+ "(TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE,FORMCODE_ENTITY,CREATED_BY,CREATION_DATE,INVITEMINSTRUMENT_ID)"
						+ " values (sysdate,'" + userId + "'," + sessionId + ",'1'," + newformId + "," + formId + ",'"
						+ formCode + "','" + formCode + "','" + userId + "',sysdate,'" + instrument_id + "')";
				String insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_" + formCode + "_PIVOT", newformId);
			}
		}
		else if(eventAction.equals("updateTestedComponentTable"))
		{
			String material_id = elementValueMap.get("_MATERIAL_ID");
			if (!generalUtil.getNull(material_id).isEmpty()) {
				formCode = "Component";
				String componentName = formDao.getFromInfoLookup("INVITEMMATERIAL", LookupType.ID, material_id, "name");
				
				String newformId = formSaveDao.getStructFormId(formCode);
				String sessionId = generalUtilFormState.checkAndReturnSessionId("Component", formId);
				String sql = "insert into FG_S_" + formCode + "_PIVOT "
						+ "(TIMESTAMP,CHANGE_BY,SESSIONID,ACTIVE,FORMID,PARENTID,FORMCODE,FORMCODE_ENTITY,CREATED_BY,CREATION_DATE,materialId,componentName,OUM_ID,coefficient,numOfStandardRows)"
						+ " values (sysdate,'" + userId + "'," + sessionId + ",'1'," + newformId + "," + formId + ",'"
						+ formCode + "','" + formCode + "','" + userId + "',sysdate,'" + material_id + "','"+componentName+"',fg_get_Uom_by_uomtype('time','min'),'1','1')";
				String insert = formSaveDao.insertStructTableByFormId(sql, "FG_S_" + formCode + "_PIVOT", newformId);
			}
		}
		else if(eventAction.equals("removeRowColumnSelect")) {
			try {
			String sessionId = generalUtilFormState.checkAndReturnSessionId("ColumnSelect", formId);
			
			String removedId = generalUtil.getNull(elementValueMap.get("removedId"));
			/*
			 * String sql = "update fg_s_columnselect_pivot set column_id = REGEXP_REPLACE(REPLACE(','||column_id||',',','||" + removedId + "||',',','),'^[,]|[,]$','')" + "where parentid = '" + formId + "' and active = 1 and sessionid='"+sessionId+"'"; String update = formSaveDao.updateStructTable(sql, "fg_s_batchselect_pivot", Arrays.asList("column_id"), "parentid", formId);
			 */
			String csvIds = generalDao.selectSingleStringNoException(
					"select column_id from fg_s_columnselect_v t where PARENTID='"+formId+"' and sessionid ='"+sessionId+"'");
			if(generalUtil.getNull(csvIds).isEmpty()) {
				csvIds = generalDao.selectSingleStringNoException(
						"select column_id from fg_s_columnselect_v t where PARENTID='"+formId+"' and sessionid is null");
				String colList = generalDao.getTableColCsv("FG_S_COLUMNSELECT_PIVOT");						
				String valList = colList
						.replace("CHANGE_BY", userId)
						.replace(",TIMESTAMP", ",sysdate").replace("CREATION_DATE", "sysdate")
						.replace("CREATED_BY", userId).replace("SESSIONID", sessionId);

				String sql = String.format(
						"insert into FG_S_%1$s_PIVOT (%2$s) select %3$s from FG_S_columnselect_PIVOT t where parentid = %5$s and sessionid is null",
						"COLUMNSELECT", colList, valList, "column_id", formId);
				
				String update = formSaveDao.updateSingleStringInfoNoTryCatch(sql);
				sql = String.format( "update FG_S_%1$s_PIVOT set %2$s = REGEXP_REPLACE(REPLACE(','||column_id||',',','||" + removedId + "||',',','),'^[,]|[,]$','') where  PARENTID='%5$s' and sessionid='%6$s'",
						"columnselect", "column_id", csvIds, "", formId, sessionId);
				update = formSaveDao.updateSingleStringInfoNoTryCatch(sql);
			}else {
			String sql = String.format( "update FG_S_%1$s_PIVOT set %2$s = REGEXP_REPLACE(REPLACE(','||column_id||',',','||" + removedId + "||',',','),'^[,]|[,]$','') where  PARENTID='%5$s' and sessionid='%6$s'",
					"columnselect", "column_id", csvIds, "", formId, sessionId);
			String update = formSaveDao.updateSingleStringInfoNoTryCatch(sql);
			}
			//delete the row of the select if no data exists  anymore
			if(generalDao.selectSingleStringNoException("select count(*)\n"
					+ " from fg_s_columnselect_v\n"
					+ " where column_id is not null\n"
					+ " and active =1 and nvl(sessionid,'"+sessionId+"')='"+sessionId+"'\n"
					+ " and parentid = '"+formId+"'").equals("0")){
				String sql = "delete from fg_s_columnselect_pivot where parentid = '"+formId+"'\n"
						+ "and active =1 ";
				formSaveDao.deleteStructTable(sql, "fg_s_columnselect_pivot", "parentid", formId);
			}
			}catch(Exception e) {
				return "-1";
			}
		}
		else if (eventAction.equals("cancelExperimentGroup")) {
			String sql = "update FG_S_ExperimentGroup_PIVOT set Active = '0' where formid = '"
					+ elementValueMap.get("cancelledId") + "'";
			formSaveDao.updateStructTableByFormId(sql, "FG_S_ExperimentGroup_PIVOT", Arrays.asList("Active"),
					elementValueMap.get("cancelledId"));
		}

		else if (eventAction.equals("getExperimentsPerGroup")) {
			generalUtilFormState.setFormParam(stateKey, formCode, "GROUPID",
					generalUtil.getNull(elementValueMap.get("groupId")));
		}

		else if(eventAction.equals("getColumnByDefault"))
		{
			String struct=elementValueMap.get("level");
			String impCode=elementValueMap.get("tableId");
			String formCodeMain=elementValueMap.get("formCodeMain");
			String columns = integrationDTAdamaImp.customerDTDefaultHiddenColumns(formCodeMain, impCode, struct);
			return columns;
		}
		else if (eventAction.equals("getExportToReport")) {
			String sql = "select LISTAGG(DOCUMENTUPLOAD, ',') WITHIN GROUP (ORDER BY 1) from \r\n" + 
					" fg_s_Document_v where PARENTID = '" + formId
					+"' and exportToReport=1 and sessionid is null";
			String defaultRows = generalUtil.getNull(generalDao.selectSingleStringNoException(sql));
			return defaultRows.replace(",", "-");
		}
		
		return null; 
	}
	
	private void copyReactionByMaterialList(String formId, String materialref_id_list, String userId) throws Exception {
		/**
		 * copy data to the reactants table from the products accepted by the materialref_id_list
		 */

		if (!generalUtil.getNull(materialref_id_list).equals("")) {
			List<String> productIdList = generalDao.getListOfStringBySql("select distinct formId"
					+ " from fg_s_materialref_v m" + " where m.TABLETYPE = 'Product'" + " and m.formid in ("
					+ materialref_id_list + ")" + " and nvl(active,'1')='1' and sessionid is null order by formId");
			for (String productId : productIdList) {
				String materilalRef_formId = formSaveDao.getStructFormId("MaterialRef");
				String sql = "insert into FG_S_MATERIALREF_PIVOT"
						+ "(FORMID,PARENTID,TIMESTAMP,ACTIVE,SESSIONID,CREATED_BY,CREATION_DATE,FORMCODE,FORMCODE_ENTITY,TABLETYPE,INVITEMMATERIAL_ID,BATCH_ID,QUANTITY,QUANTITYUOM_ID,densityInf,DENSITYUOM_ID_INF,VOLUOM_ID,MOLEUOM_ID,PURITYINF,PURITYUOM_ID_INF,ALIAS_,QUANTITYRATE,QUANTITYRATE_UOM,VOLRATEUOM_ID,MOLERATEUOM_ID)"
						+ "select distinct'" + materilalRef_formId + "','" + formId + "',sysdate,1,"+generalUtilFormState.getSessionId(formId)+",'" + userId
						+ "',sysdate,'MaterialRef','MaterialRef','Reactant'," + " m.INVITEMMATERIAL_ID,"
						+ " decode(count(b.invitembatch_id) over (partition by m.materialref_id,m.INVITEMMATERIAL_ID),1,b.invitembatch_id),"
						+ " m.MASS,m.MASSUOM_ID," + " DENSITY,DENSITY_UOM_ID,'"
						+ generalUtilForm.getDefaultValueOfElement("MaterialRef", "VOLUOM_ID") + "'" + ",'"
						+ generalUtilForm.getDefaultValueOfElement("MaterialRef", "MOLEUOM_ID") + "',"
						+ " m.CONCINREACTIONMASS,'"
						+ generalUtilForm.getDefaultValueOfElement("MaterialRef", "PURITY_UOM_ID")+"',"
						+ " ALIAS_, m.massrateforpp, m.MASSRATEPPUOM_ID,'"
						+ generalUtilForm.getDefaultValueOfElement("MaterialRef", "VOLRATEUOM_ID") + "'" + ",'"
						+ generalUtilForm.getDefaultValueOfElement("MaterialRef", "MOLERATEUOM_ID") + "'"
						+ " from fg_s_materialref_all_v m," + " fg_s_invitembatch_v b"
						+ " where m.INVITEMMATERIAL_ID = b.INVITEMMATERIAL_ID(+)" + " and m.formid = '" + productId
						+ "'" + " and nvl(m.active,'1')='1' and m.sessionid is null"
						+ " and b.PARENTID(+) = m.PARENTID";//mw,casNumber,casName,purity are taken from the materialref_all_v
				formSaveDao.insertStructTableByFormId(sql, "FG_S_MATERIALREF_PIVOT", materilalRef_formId);

				//calculate mole&volume&ratio quantity/volume
				Map<String, String> reactantElementValueMap = formDao.getFormElementValuesMap(materilalRef_formId,
						"MaterialRef");
				generalUtilLogger.logWriter(LevelType.INFO, "Calculate reactant " + materilalRef_formId + ".",
						ActivitylogType.Calculation, materilalRef_formId, userId);
				commonFunc.recalculateReactant(materilalRef_formId, userId, new ArrayList<String>(),
						reactantElementValueMap, "quantity", new StringBuilder(), false);
				commonFunc.recalculateReactant(materilalRef_formId, userId, new ArrayList<String>(),
						reactantElementValueMap, "quantityRate", new StringBuilder(), false);
			}
			/**
			 * copy data to the solvents table from the solvents accepted by materialref_id_list
			 */
			List<String> solventIdList = generalDao.getListOfStringBySql("select distinct formId"
					+ " from fg_s_materialref_v m" + " where m.TABLETYPE = 'Solvent'" + " and m.formid in ("
					+ materialref_id_list + ")" + " and nvl(active,'1')='1' and sessionid is null order by formid");
			for (String solventId : solventIdList) {
				String materilalRef_formId = formSaveDao.getStructFormId("MaterialRef");
				String sql = "insert into FG_S_MATERIALREF_PIVOT"
						+ "(FORMID,PARENTID,TIMESTAMP,ACTIVE,SESSIONID,CREATED_BY,CREATION_DATE,FORMCODE,FORMCODE_ENTITY,TABLETYPE,"
						+ "INVITEMMATERIAL_ID,BATCH_ID,QUANTITY,QUANTITYUOM_ID,VOLUME,VOLUOM_ID,MOLE,MOLEUOM_ID,actualPurity,ACTPURITYUOM_ID,"
						+ "densityInf,DENSITYUOM_ID_INF,quantratiototal,volratiototal,PURITYINF,PURITYUOM_ID_INF,RATIOTYPE_ID)"
						+ "select '" + materilalRef_formId + "','" + formId + "',sysdate,1,"+generalUtilFormState.getSessionId(formId)+",'" + userId
						+ "',sysdate,'MaterialRef','MaterialRef','Solvent'," + " m.INVITEMMATERIAL_ID," + " m.BATCH_ID,"
						+ " m.QUANTITY,m.QUANTITYUOM_ID," + " m.VOLUME,m.VOLUOM_ID," + " m.MOLE,m.MOLEUOM_ID,"
						+ " m.actualPurity,m.ACTPURITYUOM_ID," + " m.densityInf,m.DENSITYUOM_ID_INF,"
						+ " m.quantratiototal,m.volratiototal," + " m.PURITYINF,m.PURITYUOM_ID_INF,RATIOTYPE_ID"
						+ " from fg_s_materialref_v m" + " where m.formid = '" + solventId + "'"
						+ " and nvl(m.active,'1')='1' and m.sessionid is null";//mw,casNumber,casName,synonyms,smiles are taken from the materialref_all_v
				formSaveDao.insertStructTableByFormId(sql, "FG_S_MATERIALREF_PIVOT", materilalRef_formId);
			}
		}

	}

	@Override
	public void onClobAndResultEvent(String formCode, String resultValue, String output, String parent_id,
									Map<String, String> elementValueMap, String webixTableGroupNumber) {
		if (formCode.equals("SelfTest")) {
			commonFunc.insertSelfTestWebixToResulRef(resultValue, parent_id);
		} else {// prevent from perform the next code in Step MassBalance 
			List<Result> resultList = setValuesAsResultList(resultValue);
			commonFunc.insertToResults(resultList, parent_id, elementValueMap,false);

			List<WebixOutput> outputList = setValuesAsWebixOutputList(output, parent_id, formCode);
			if(outputList != null) {
				insertToWebixOutput(outputList, parent_id, webixTableGroupNumber);
			}
		}
	}

	private List<Result> setValuesAsResultList(String resultValue) {
		if (generalUtil.getNull(resultValue).equals("")) {
			return null;
		}
		List<Result> resultList = new ArrayList<Result>();
		JSONArray resultsList = new JSONArray(resultValue);//data that will be shown in the table
		int size = resultsList.length();
		for (int i = 0; i < size; i++) {
			String resultRow = resultsList.get(i).toString();
			//String experimentTypeName = generalUtil.getJsonValById(resultRow, "result_name");
			String materialName = formDao.getFromInfoLookup("InvItemMaterial", LookupType.ID,
					generalUtil.getJsonValById(resultRow, "result_material_id"), "name");
			resultList
					.add(new Result(generalUtil.getJsonValById(resultRow, "experiment_id"),
							generalUtil.getJsonValById(resultRow, "result_test_name"),
							generalUtil.getSpringMessagesByKey(generalDao
									.selectSingleString("select TESTEDCOMPTYPENAME from fg_s_component_all_v"
											+ " where parentid = '"
											+ generalUtil.getJsonValById(resultRow, "experiment_id") + "'"
											+ " and materialId = '"
											+ generalUtil.getJsonValById(resultRow, "result_material_id")
											+ "' and sessionid is null and nvl(active,'1') = '1' and rownum<=1")
									.replace(" ", ""), "")
							/*
							 * experimentTypeName.contains("Impurity") ? generalUtil
							 * .getSpringMessagesByKey(generalDao
							 * .selectSingleString("select TESTEDCOMPTYPENAME from fg_s_component_all_v"
							 * + " where parentid = '"
							 * + generalUtil.getJsonValById(resultRow, "experiment_id") + "'"
							 * + " and materialId = '"
							 * + generalUtil.getJsonValById(resultRow, "result_material_id")
							 * + "' and sessionid is null and nvl(active,'1') = '1' and rownum<=1")
							 * .replace(" ", ""), "")
							 * : experimentTypeName
							 */, generalUtil.getJsonValById(resultRow, "sample_id"), generalUtil.getJsonValById(resultRow, "result_value"), generalUtil.getJsonValById(resultRow, "result_uom_id"), generalUtil.getJsonValById(resultRow, "result_type"), generalUtil.getJsonValById(resultRow, "result_material_id"), generalUtil.getJsonValById(resultRow, "result_comment"), generalUtil.getJsonValById(resultRow, "selftest_id"), "percentage", "", "1", materialName, ""));
		}
		return resultList;
	}

	private List<WebixOutput> setValuesAsWebixOutputList(String outputValue, String parent_id, String formCode) {
		if (generalUtil.getNull(outputValue).equals("")) {
			return null;
		}
		List<WebixOutput> resultList = new ArrayList<WebixOutput>();
		String formCodeEntityP = formDao.getFormCodeEntityBySeqId("", parent_id);
		String experiment_id = "";
		String step_id = "";

		if (formCodeEntityP.toUpperCase().contains("STEP")) {
			experiment_id = formDao.getFromInfoLookup(formCodeEntityP, LookupType.ID, parent_id, "EXPERIMENT_ID");
			step_id = parent_id;
		} else {//it's an experiment
			experiment_id = parent_id;
			step_id = "";
		} 
		
		if (formCode.equals("Step") || formCode.equals("Experiment") || formCode.equals("ExperimentCP")) { // develop ExperimentCP "Continuous Process"
			String samplesCsv = generalUtil.getJsonValById(outputValue, "samplesList");
			String webixTableGroupNumber = generalUtil.getJsonValById(outputValue, "webixTableGroupNumber");//the number of the tab in which the following tables are displayed
			//removed these lines in order to fix bug 8298
			/*if (samplesCsv.isEmpty() && webixTableGroupNumber.isEmpty()) {
				return null;
			}*/
			
			JSONArray tablesData = new JSONArray(generalUtil.getJsonValById(outputValue, "tablesData"));//array of all streams data
			//runs on the streams
			for (int i = 0; i < tablesData.length(); i++) {
				String fieldsData = generalUtil.getJsonValById(tablesData.getString(i), "fieldsData");
				if (generalUtil.getJsonValById(fieldsData, "ddlSample").isEmpty()) {
					continue;
				}
				String stream_name = generalUtil.getJsonValById(fieldsData, "txtStream");
				String mass = generalUtil.getJsonValById(fieldsData, "txtMass");
				String sample_mb = generalUtil.getJsonValById(fieldsData, "ddlSample");
				String webixTableNumber = generalUtil.getJsonValById(tablesData.getString(i), "tableNumber");//the order of the tables in a single tab
				JSONArray rowsData = new JSONArray(generalUtil.getJsonValById(tablesData.getString(i), "rowsData"));
				if (rowsData.length() == 0) {
					resultList.add(new WebixOutput(experiment_id, step_id, "", mass, "", "", "", "NUMERIC", ""//TODO: add batch list of those created from current step and its material is the current product
							, "", "", samplesCsv, "", "", "", "", sample_mb, stream_name, String.valueOf(i),"1", webixTableGroupNumber));
					continue;
				}

				//runs through the rows in the stream
				for (int j = 0; j < rowsData.length(); j++) {
					String streamRow = rowsData.getJSONArray(j).getString(0);
					JSONObject indicationJson = new JSONObject();//
					indicationJson.put("is_chemical", generalUtil.getJsonValById(streamRow, "is_chemical"));
					indicationJson.put("is_yield", generalUtil.getJsonValById(streamRow, "is_yield"));
					indicationJson.put("is_isolated", generalUtil.getJsonValById(streamRow, "is_isolated"));
					indicationJson.put("is_summary", generalUtil.getJsonValById(streamRow, "is_summary"));
					String indication_mb = indicationJson.toString();
					String result_value = generalUtil.getJsonValById(streamRow, "perc_of_product");
					String result_type = generalUtil.getJsonValById(streamRow, "substance_type");
					String material_id = generalUtil.getJsonValById(streamRow, "material_id");
					String weight = generalUtil.getJsonValById(streamRow, "weight");
					String yield = generalUtil.getJsonValById(streamRow, "yield");
					String moles = generalUtil.getJsonValById(streamRow, "moles");
					String comment = generalUtil.getJsonValById(streamRow, "description");

					resultList.add(new WebixOutput(experiment_id, step_id, "", mass, "", result_type, result_value,
							"NUMERIC", comment//TODO: add batch list of those created from current step and its material is the current product
							, "", material_id, samplesCsv, weight, yield, moles, indication_mb, sample_mb, stream_name,
							String.valueOf(i), webixTableNumber, webixTableGroupNumber));
				}
			}
			//	resultList.add(new WebixOutput(experiment_id, step_id, "", "", "", "", "", "", "", "", "", samplesCsv));
		} else if (formCode.equals("ExperimentAn")) {
			JSONArray outList = new JSONArray(outputValue);//data that will be shown in the table
			for (int j = 0; j < outList.length(); j++) {
				JSONArray resultsList = new JSONArray(generalUtil.getJsonValById(outList.get(j).toString(), "data"));
				String batchId = "", currentMaterialId = "";
				for (int i = 0; i < resultsList.length(); i++) {
					String resultRow = resultsList.get(i).toString();
					String weight = generalUtil.getJsonValById(resultRow, "weight");
					JSONObject resultDataJson = new JSONObject();
					resultDataJson.put("ch1", generalUtil.getJsonValById(resultRow, "ch1"));
					resultDataJson.put("area", generalUtil.getJsonValById(resultRow, "area"));
					resultDataJson.put("dilution", generalUtil.getJsonValById(resultRow, "dilution"));
					resultDataJson.put("volume", generalUtil.getJsonValById(resultRow, "volume"));
					resultDataJson.put("retention", generalUtil.getJsonValById(resultRow, "retention"));
					resultDataJson.put("num", generalUtil.getJsonValById(resultRow, "num"));
					String comments = generalUtil.getJsonValById(resultRow, "comment");
					String sample_id = generalUtil.getEmpty(generalUtil.getJsonValById(resultRow, "sample_id"),
							batchId);//in case of the last standard
					String preparation_id = generalUtil.getJsonValById(resultRow, "preparationref_id");
					String material_id = generalUtil.getEmpty(generalUtil.getJsonValById(resultRow, "materialid"),
							currentMaterialId);//in case of the last standard
					String component_id = generalUtil.getEmpty(generalUtil.getJsonValById(resultRow, "component_id"),
							generalDao.selectSingleStringNoException("select component_id from fg_s_component_v"
									+ " where sessionid is null and active='1' and materialid = '" + material_id
									+ "' and rownum=1"));
					String weighting = generalUtil.getJsonValById(resultRow, "weighting");
					String batch_id = generalUtil.getEmpty(generalUtil.getJsonValById(resultRow, "batch_id"),
							formDao.getFromInfoLookup("invitembatch", LookupType.NAME,
									generalUtil.getJsonValById(resultRow, "batchname"), "id"));
					String result_name = generalUtil.getJsonValById(resultRow, "type");//standard or sample
					if (result_name.equals("Standard") && batchId.isEmpty() && currentMaterialId.isEmpty()) {
						batchId = sample_id;
						currentMaterialId = material_id;
					}
					if (result_name.equals("Sample") && preparation_id.isEmpty()) {//it's a row that is not related to any preparation,.it is displayed with no data for the first update of the calculation table
						continue;
					}
					resultList.add(new WebixOutput(experiment_id, step_id, batch_id, "", "Analytical", result_name, "",
							"", comments, "", material_id, "", weight, sample_id, component_id, preparation_id,
							resultDataJson.toString(), weighting));
				}
			}
		} else {
			try {
				JSONArray resultsList = new JSONArray(generalUtil.getJsonValById(outputValue, "rowsData"));//data that will be shown in the table
				String testName = "";
				if (!generalUtil.getNull(step_id).equals("")) {
					testName = generalDao.selectSingleString(
							"select t.STEPTYPENAME from fg_s_step_all_v t where step_id = '" + step_id + "'");
				}

				int size = resultsList.length();
				for (int i = 0; i < size; i++) {
					String resultRow = resultsList.get(i).toString();
					resultList
							.add(new WebixOutput(experiment_id, step_id, generalUtil.getJsonValById(resultRow, "batch_id"),
									generalUtil.getJsonValById(resultRow, "mass"), "Formulation", testName,
									generalUtil.getJsonValById(resultRow, "mass_in_final"), "NUMERIC", "", "weight",
									generalUtil.getJsonValById(resultRow, "material_id"), ""));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return resultList;
	}

	private void insertToWebixOutput(List<WebixOutput> outputList, String parent_id, String webixTableGroupNumber) {
		//first,updates the active flags of all the certain step id/selfTest_id to 0, for future information about the  deleted results
		String resetFlagSQL = "update FG_WEBIX_OUTPUT set RESULT_IS_ACTIVE = '0' \n"
							+" where (EXPERIMENT_ID = '"+ parent_id + "' and STEP_ID is NULL and TABLE_GROUP_INDEX_MB IS NOT NULL and TABLE_GROUP_INDEX_MB = '"+webixTableGroupNumber+"')" // for Experiment/ExperimentCP Mass Balance-every webix saving occurs separately
							+" or (EXPERIMENT_ID = '"+ parent_id + "' and STEP_ID is NULL and TABLE_GROUP_INDEX_MB IS NULL) \n"
							+" or STEP_ID = '" + parent_id + "'";
		formSaveDao.updateSingleStringInfoNoTryCatch(resetFlagSQL);
		//second, updates or insert the results to the result table
		String uom = "";
		String userId = generalUtil.getSessionUserId();
		List<String> uomList = formDao.getFromInfoLookupElementData("UOM", LookupType.NAME, "gr", "ID");
		for (String uomId : uomList) {
			Map<String, String> uomData = formDao.getFromInfoLookupAll("UOM", LookupType.ID, uomId);
			if (uomData.get("UOMTYPENAME").equalsIgnoreCase("weight")) {
				uom = uomId;
				break;
			}
		}
		if (outputList != null && !outputList.isEmpty()) {
			for (WebixOutput result : outputList) {
				String sql = "merge into FG_WEBIX_OUTPUT r using ( select '" + result.getStepId() + "' STEP_ID,'"
						+ result.getExperimentId() + "' EXPERIMENT_ID,'" + result.getResultTestName()
						+ "' RESULT_TEST_NAME,'" + result.getMass() + "' MASS,'" + result.getResultName()
						+ "' RESULT_NAME,'" + result.getBatchId() + "' BATCH_ID,'" + result.getMaterialId()
						+ "' MATERIAL_ID,'" + result.getResultType() + "' RESULT_TYPE,'" + result.getResultValue()
						+ "' RESULT_VALUE,'" + result.getResultComment() + "' RESULT_COMMENT,'" + uom
						+ "' RESULT_UOM_ID,'" + result.getSamplesCsv() + "' SAMPLES,'" + result.getWeight()
						+ "' WEIGHT,'" + result.getYield() + "' YIELD,'" + result.getMoles() + "' MOLES,'"
						+ result.getIndication_mb() + "' INDICATION_MB,'" + result.getSampleMB() + "' SAMPLE_MB,'"
						+ result.getAnalytic_data() + "' ANALYTIC_DATA,'" + result.getComponent_id()
						+ "' COMPONENT_ID,'" + result.getSample_id() + "' SAMPLE_ID,'" + result.getPreparation_id()
						+ "' PREPARATIONREF_ID,'" + result.getWeighting() + "' WEIGHTING,'" + result.getStreamData()
						+ "' STREAM_DATA, '"+result.getWebixTableNumber()+"' TABLE_INDEX_MB, '"+result.getWebixTableGroupNumber()+"' TABLE_GROUP_INDEX_MB from dual) t "
						+ " on ((r.EXPERIMENT_ID = t.EXPERIMENT_ID and t.STEP_ID is NULL and r.STEP_ID is NULL and t.TABLE_INDEX_MB = r.TABLE_INDEX_MB and t.TABLE_GROUP_INDEX_MB = r.TABLE_GROUP_INDEX_MB and r.MATERIAL_ID = t.MATERIAL_ID and r.SAMPLE_MB = t.SAMPLE_MB and r.RESULT_NAME = t.RESULT_NAME and t.SAMPLES is not NULL)"//for Experiment/ExperimentCP massbalance webix
						+ " or (r.STEP_ID = t.STEP_ID and r.MATERIAL_ID = t.MATERIAL_ID and r.SAMPLE_MB = t.SAMPLE_MB and r.RESULT_NAME = t.RESULT_NAME and t.SAMPLES is not NULL)"//for STEP massbalance webix
						+ " or (r.STEP_ID = t.STEP_ID and t.BATCH_ID is NULL and t.MATERIAL_ID is NULL and r.BATCH_ID is NULL and r.MATERIAL_ID is NULL and t.SAMPLES is NULL)" //a row that contains a step where batch and material are empty
						+ " or (r.STEP_ID = t.STEP_ID and r.BATCH_ID = t.BATCH_ID and t.SAMPLES is NULL and r.SAMPLES is NULL)"//a row that contains a step where batch is not empty
						+ " or (r.EXPERIMENT_ID = t.EXPERIMENT_ID and r.BATCH_ID = t.BATCH_ID and t.STEP_ID is NULL and r.STEP_ID is NULL and t.SAMPLES is NULL and r.SAMPLES is NULL and t.RESULT_TEST_NAME<>'Analytical')"//a row that contains an experiment without step where batch is not empty
						+ " or (r.STEP_ID = t.STEP_ID and r.MATERIAL_ID = t.MATERIAL_ID and t.BATCH_ID is NULL and r.BATCH_ID is NULL and t.SAMPLES is NULL and r.SAMPLES is NULL)"//a row that contains a step where material is not empty but batch is empty
						+ " or (r.EXPERIMENT_ID = t.EXPERIMENT_ID and r.MATERIAL_ID = t.MATERIAL_ID and t.BATCH_ID is NULL and r.BATCH_ID is NULL and t.STEP_ID is NULL and r.STEP_ID is NULL and t.SAMPLES is NULL and r.SAMPLES is NULL and t.RESULT_TEST_NAME<>'Analytical') "//a row that contains an experiment without step where material is not empty but batch is empty
						+ " or (t.RESULT_TEST_NAME='Analytical' and r.EXPERIMENT_ID = t.EXPERIMENT_ID and r.RESULT_NAME  = t.RESULT_NAME and r.MATERIAL_ID = t.MATERIAL_ID and r.SAMPLE_ID = t.SAMPLE_ID and nvl2(r.BATCH_ID,nullif(r.BATCH_ID,t.BATCH_ID),t.BATCH_ID) is null and r.WEIGHTING = t.WEIGHTING"
						+ " and fg_get_value_from_json(json_in => r.analytic_data,code_in =>'num') = fg_get_value_from_json(json_in => t.analytic_data,code_in =>'num')))"//a row that contains data input of calculation table in the analytical experiment and nullif(r.PREPARATIONREF_ID,t.PREPARATIONREF_ID) is null
						//when not matched then insert
						+ " when not matched then insert (EXPERIMENT_ID, STEP_ID, MASS, RESULT_TEST_NAME,RESULT_NAME,BATCH_ID,RESULT_VALUE,RESULT_TYPE,RESULT_DATE,WEBIX_CHANGE_BY,RESULT_TIME,RESULT_COMMENT,RESULT_IS_ACTIVE,MATERIAL_ID,RESULT_UOM_ID"
						+ ",SAMPLES,WEIGHT,MOLES,YIELD,INDICATION_MB,SAMPLE_MB,SAMPLE_ID,PREPARATIONREF_ID,COMPONENT_ID,ANALYTIC_DATA,WEIGHTING,STREAM_DATA,TABLE_INDEX_MB,TABLE_GROUP_INDEX_MB)"
						+ " values ("// + getResultIdbyTable("FG_WEBIX_OUTPUT")
						+ "t.EXPERIMENT_ID,t.STEP_ID,t.MASS,t.RESULT_TEST_NAME,t.RESULT_NAME,t.BATCH_ID,t.RESULT_VALUE,t.RESULT_TYPE,sysdate,'"
						+ userId + "',to_char( sysdate, 'HH24:MI:SS' ),t.RESULT_COMMENT,1,t.MATERIAL_ID,t.RESULT_UOM_ID"
						+ ",t.SAMPLES,t.WEIGHT,t.MOLES,t.YIELD,t.INDICATION_MB,t.SAMPLE_MB,t.SAMPLE_ID,t.PREPARATIONREF_ID,t.COMPONENT_ID,t.ANALYTIC_DATA,t.WEIGHTING,t.STREAM_DATA, t.TABLE_INDEX_MB, t.TABLE_GROUP_INDEX_MB) "
						//when matched then update
						+ "when matched then update set r.RESULT_VALUE = t.RESULT_VALUE, r.RESULT_DATE = sysdate,r.WEBIX_CHANGE_BY = '"
						+ userId
						+ "', r.RESULT_TIME = to_char( sysdate, 'HH24:MI:SS' ), r.RESULT_COMMENT = t.RESULT_COMMENT , r.RESULT_IS_ACTIVE = 1, r.MASS = t.MASS, r.RESULT_UOM_ID = t.RESULT_UOM_ID, r.MATERIAL_ID = t.MATERIAL_ID, r.EXPERIMENT_ID = t.EXPERIMENT_ID"
						+ ", r.SAMPLES = t.SAMPLES, r.WEIGHT = t.WEIGHT, r.YIELD = t.YIELD, r.MOLES = t.MOLES, r.INDICATION_MB = t.INDICATION_MB, r.RESULT_TYPE = t.RESULT_TYPE, r.SAMPLE_MB = t.SAMPLE_MB, r.RESULT_NAME = t.RESULT_NAME"
						+ ", r.SAMPLE_ID = t.SAMPLE_ID, r.COMPONENT_ID = t.COMPONENT_ID, r.ANALYTIC_DATA = t.ANALYTIC_DATA, r.PREPARATIONREF_ID = t.PREPARATIONREF_ID , r.WEIGHTING = t.WEIGHTING, r.STREAM_DATA = t.STREAM_DATA, r.TABLE_INDEX_MB = t.TABLE_INDEX_MB";

				formSaveDao.updateSingleStringInfoNoTryCatch(sql);
			}
		}
		//update webix data for search async not part of the transacrion
		//		String dbTransactionId = generalUtil.getDBTransaction(); // only from job
		//		generalTask.updateWebixSearch(dbTransactionId);
	}

	private String getResultIdbyTable(String table) {
		String resultId = "0";
		String currentResult = generalDao.selectSingleString("select max(TO_NUMBER(RESULT_ID)) from " + table);
		resultId = String.valueOf(Integer.parseInt(generalUtil.getEmpty(currentResult, resultId)) + 1);
		return resultId;
	}

	

	@Override
	/**
	 * copy the reaction data from the previous step to the current one.
	 * @param formId the formId of the current step to which the reaction of the previous step is copied.
	 * @param prevStepFormId the formId of the previous step from which the reaction is copied.
	 */
	public void copyReactionFromPrevStep(String formId, String prevStepFormId, String userId) throws Exception {
		/**
		 * copy data to the reactants&solvents tables from the products/solvents tables of the previous step
		 */
		List<String> materialreflist = generalDao.getListOfStringBySql("select distinct formId"
				+ " from fg_s_materialref_v m" + " where m.TABLETYPE in ('Product','Solvent')" + " and m.PARENTID in ("
				+ prevStepFormId + ")" + " and nvl(active,'1')='1' and sessionid is null");
		copyReactionByMaterialList(formId, generalUtil.listToCsv(materialreflist), userId);
	} 

	/**
	 * kd 17092018, 22102018  
	 * This method is used instead renderIreport for call from list of Experiment (button in table) and also (from 23072020) from Experiment form. On the Experiment form 
	 * is used a button, not ireport element Here are hard coded sql instead get it from map of from parameters file_, catalog should be correlated to names of files of reports (jasper .xml) 
	 * and catalogs are defined inside xml. Inside xml catalogs and names of files are defined as a parameters of the report and in subreport properties. There are 3 levels of this report. 
	 * All xmls which are used for Experiment Summary Report and for Step Summary Report.1st level .xml (EXPERIMENTANDSTEPSUMMARY.xml) is used only for Experiment Summary Report.
	 * .xmls of 2nd and 3rd level are used for both Experiment Summary Report and Step Summary report
	 * This algorithm and algorithm which is wired in the structure of xmls are related 
	*/
	private String renderIreportExperiment(long stateKey, String formCode, String fileName, String printTemplate,
			String reportType, String isDistinct, String title, String subTitle, String displayUrl, String sessionId,
			String experimentId) 
	{
		Connection conn = generalDao.getConnectionFromDataSurce();
		JasperReportGenerator jrg = null;
		String path_file = "";
		try {
			JasperDataSourceSupplier jrdss = new JasperDataSourceSupplier(conn, false);
			String sql = "";
			HashMap<String, String> mLang = new HashMap<String, String>();

			// preparations...
			HashMap<String, String> hmReportReplacerList = new HashMap<String, String>();
			// hmReportReplacerList.put("@replacer1@", " and id = 1 ");
			HashMap<String, Object> hmReportParameterList = new HashMap<String, Object>();
			//kd 04122018
			JasperTemplateFactory jf = new JasperTemplateFactory(conn, DIR_JASPER_XML, DIR_JASPER_XML + "\\TEMPLATE",
					DIR_JASPER_XML + "\\TMP");

			hmReportParameterList.put("currentUrl", displayUrl);
			hmReportParameterList.put("parameter_DB_URL", DB_URL);
			hmReportParameterList.put("parameter_DB_USER", DB_USER);
			hmReportParameterList.put("parameter_DB_PASSWORD", DB_PASSWORD);
			hmReportParameterList.put("parameter_DIR_JASPER_XML", DIR_JASPER_XML);

			//-2 parameters
			String file_ = "experimentAndStepSumExpParametersSubSub.xml";
			String catalog_ = "CEXPSUM_EXPPARAMETERS";

			String fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			String fileTmp_ = fileName_ + sessionId + (new Date()).getTime();
			String hideEmptyColumns = "";
			
			
			sql = "select * from FG_R_EXPSUM_PARAMETERS_V where EXPERIMENT_ID ='" + experimentId + "'";

//			25/02/2020 ab fixed bug: when there were not saved rows in "Planned" status, and status changed to "Active", there are no rows in table FG_S_MATERIALREF_ALL_V_PLAN
//			if (generalDao.selectSingleString("select count(t.experiment_id) from FG_S_MATERIALREF_ALL_V_PLAN t where t.experiment_id = " + experimentId) .equals("0")){
			if (generalDao.selectSingleString("select st.experimentstatusname from fg_s_experiment_v t, fg_s_experimentstatus_all_v st where t.status_id = st.experimentstatus_id and t.experiment_id = " + experimentId).equalsIgnoreCase("Planned")){
				sql = sql.replace("FG_R_EXPSUM_PARAMETERS_V", "FG_R_EXPSUM_PARAMETERS_PLN_V");
			}
			JSONObject jsonInsteadSql = generalDao.getJSONObjectOfDateTable(sql, hideEmptyColumns,
					"-1",null);
			if (!jsonInsteadSql.equals("")) {
				//createReportDataSource: 3th parameter is using  for set first column should in the table of report
				hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
						jrdss.createReportDataSource(jsonInsteadSql, "JRMapArrayDataSource",
								startColumnIndexMaterial));
				logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
						+ ", catalog_=" + catalog_);

				//getJasperReportCompiledDataInjection: 3th parameter is using  for set first column should in the table of report
				hmReportParameterList.put("SUB_REPORT_" + fileName_,
						jf.getJasperReportCompiledDataInjection(fileTmp_, jsonInsteadSql, "EXPERIMENT_ID",
								"EXPERIMENT_ID", false, startColumnIndexActions));
				logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase()
						+ ", fileName_=" + fileName_ + ", fileTmp_=" + fileTmp_);
			}

			// -1 Mass Balance Exp GR (Report)
			file_ = "experimentAndStepSumMassBalExpStreamsSubRep.xml";
			catalog_ = "CSTSUM_MASSBALANCEEXPGR";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
					jrdss.createReportDataSource(
							"select * from (select t.* from FG_R_EXPSUM_MASSBALANCE_GR_V t where t.EXPERIMENT_ID = "
									+ experimentId + ") ",
							"JRMapArrayDataSource"));
			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);

			// -1_sub Mass Balance Exp (Sub Report)
			file_ = "experimentAndStepSumMassBalExpStreamsSubSub.xml";
			catalog_ = "CEXPSUM_MASSBALANCE";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
					jrdss.createReportDataSource(
							"select t.* from fg_r_expsum_massbalance_v t where t.experiment_id = '" + experimentId + "'",
							"JRMapArrayDataSource"));
			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);
			
			
			// 0 step
			file_ = "EXPERIMENTANDSTEPSUMMARY.xml";
			catalog_ = "CSTSUM_REACTSCHEME";

			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
					jrdss.createReportDataSource(
							"select * from (select * from FG_R_STEPSUM_REACT_SCHEM_V where (EXPERIMENT_ID = "
									+ experimentId + "))",
							"JRMapArrayDataSource"));

			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);

			// 1 start material
			file_ = "EXPERIMENTANDSTEPSUMSTARTMATSUBREP.xml";
			catalog_ = "CSTSUMSTARTMATERIALS";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();
			hideEmptyColumns = "";
			jsonInsteadSql = new JSONObject();

			sql = "select * from FG_R_STEPSUM_START_MATER_V where EXPERIMENT_ID ='" + experimentId + "'";

			jsonInsteadSql = generalDao.getJSONObjectOfDateTable(sql, hideEmptyColumns, "-1",null);
			if (!jsonInsteadSql.equals("")) {
				//createReportDataSource: 3th parameter is using  for set first column should in the table of report
				hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
						jrdss.createReportDataSource(jsonInsteadSql, "JRMapArrayDataSource", startColumnIndexMaterial));
				logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
						+ ", catalog_=" + catalog_);

				//getJasperReportCompiledDataInjection: 3th parameter is using  for set first column should in the table of report
				//				hmReportParameterList.put("SUB_REPORT_" + fileName_,jf.getJasperReportCompiledDataInjection(fileTmp_,jsonInsteadSql,"STEP_ID","STEP_ID",false, startColumnIndexMaterial));							  
				hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
						.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
				logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase()
						+ ", fileName_=" + fileName_ + ", fileTmp_=" + fileTmp_);

				//PLN..
				file_ = "EXPERIMENTANDSTEPSUMSTARTMATSUBREP_PLN.xml";
				fileName_ = file_.replaceAll(".xml", "").toUpperCase();
				fileTmp_ = fileName_ + sessionId + (new Date()).getTime();
				jsonInsteadSql = generalDao.getJSONObjectOfDateTable(
						sql.replace("FG_R_STEPSUM_START_MATER_V",
								"FG_R_STEPSUM_START_MATER_PLN_V")/*"select * from kd_test t where 1=1"*/,
						hideEmptyColumns, "-1",null);
				hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
						.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			}

			// 2 actions
			file_ = "EXPERIMENTANDSTEPSUMACTIONSSUBREP.xml";
			catalog_ = "CSTSUMACTIONS";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();
			hideEmptyColumns = "";

			//sql = "select * from fg_r_actionsummary_dtaexc_v where EXPERIMENT_ID_FOR_REPORT = " + experimentId + "";
			//improve performance -- yp 13092020
			sql = "select * from table(FN_GET_R_ACTIONSUMMARY_EXC('" + experimentId + "'))";

			jsonInsteadSql = generalDao.getJSONObjectOfDateTable(sql, hideEmptyColumns, "-1",null);
			if (!jsonInsteadSql.equals("")) {
				//createReportDataSource: 3th parameter is using  for set first column should in the table of report
				hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
						jrdss.createReportDataSource(jsonInsteadSql, "JRMapArrayDataSource", startColumnIndexActions));
				logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
						+ ", catalog_=" + catalog_);

				//getJasperReportCompiledDataInjection: 3th parameter is using  for set first column should in the table of report
				hmReportParameterList.put("SUB_REPORT_" + fileName_, jf.getJasperReportCompiledDataInjection(fileTmp_,
						jsonInsteadSql, "STEP_ID", "STEP_ID", false, startColumnIndexActions));
				//				hmReportParameterList.put("SUB_REPORT_" + fileName_,jf.getJasperReportCompiledDataInjection(fileTmp_,jsonInsteadSql,"","",false, startColumnIndexActions));							  
				logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase()
						+ ", fileName_=" + fileName_ + ", fileTmp_=" + fileTmp_);
			}

			// 3 product
			file_ = "EXPERIMENTANDSTEPSUMPRODUCTSUBREP.xml";
			catalog_ = "CSTSUMPRODUCT";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
					jrdss.createReportDataSource(
							"select * from (select r.* from FG_R_STEPSUM_PRODUCTS_V r, fg_s_step_pivot s where r.STEP_ID = s.formid and s.EXPERIMENT_ID = "
									+ experimentId + ") ",
							"JRMapArrayDataSource"));
			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);

			// 4 Mass balance results
			file_ = "EXPERIMENTANDSTEPSUMMASSBALSUBREP.xml";
			catalog_ = "CSTSUMPRODUCT";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
					jrdss.createReportDataSource(
							"select * from (select r.* from FG_R_STEPSUM_PRODUCTS_V r, fg_s_step_pivot s where r.STEP_ID = s.formid and s.EXPERIMENT_ID = "
									+ experimentId + ") ",
							"JRMapArrayDataSource"));
			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);

			// 5 Mass balance streams group
			file_ = "EXPERIMENTANDSTEPSUMMASSBALSTREAMSSUBREP.xml";
			catalog_ = "CSTSUM_MASSBALANCEGR";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
					jrdss.createReportDataSource(
							"select * from (select r.* from FG_R_STEPSUM_MASSBALANCE_GR_V r, fg_s_step_pivot s where r.STEP_ID = s.formid and s.EXPERIMENT_ID = "
									+ experimentId + ") ",
							"JRMapArrayDataSource"));
			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);

			// 6 Mass balance streams details
			file_ = "EXPERIMENTANDSTEPSUMMASSBALSTREAMSSUBSUB.xml";
			catalog_ = "CSTSUM_MASSBALANCE";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
					jrdss.createReportDataSource(
							"select * from (select r.* from FG_R_STEPSUM_MASSBALANCE_V r, fg_s_step_pivot s where r.STEP_ID = s.formid and s.EXPERIMENT_ID = "
									+ experimentId + ") ",
							"JRMapArrayDataSource"));
			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);

			//			sql = "select * from (select r.* from FG_R_STEPSUM_REACT_SCHEM_V r, fg_s_step_pivot s where r.PARENTID = s.formid and s.EXPERIMENT_ID = " + experimentId + ")  where 1=1 ";
			sql = "select  * from (select * from FG_R_STEPSUM_REACT_SCHEM_V where (EXPERIMENT_ID = " + experimentId
					+ "))  where 1=1 ";
			JRDataSource myResultSetDS = new JRResultSetDataSource(generalDao.getResultSet(conn, sql));

			//  call..
			jrg = new JasperReportGenerator(myResultSetDS, mLang);
			path_file = jrg.getPath(fileName + (new Date()).getTime(), fileName,
					(reportType.equals("PDF") ? JasperReportType.PDF : JasperReportType.JXL_EXCEL), title, subTitle,
					hmReportReplacerList, hmReportParameterList, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp");
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		} catch (OutOfMemoryError er) {
			Exception e = new Exception(
					"OutOfMemoryError caught in 'public void renderIreportExperiment' function in FormApiService");
			generalUtilLogger.logWrite(e);
		} catch (NoSuchMethodError et) {
			Exception e = new Exception(
					"NoSuchMethodError caught in 'public void renderIreportExperiment' function in FormApiService");
			generalUtilLogger.logWrite(e);
			generalUtilLogger.logWrite(et);
			System.out.println("This is exception: " + et); //13122018 kd Temp for debug
		} finally {
			generalDao.releaseConnectionFromDataSurce(conn);
		}
		return path_file;
	}
	
	/**
	 * kd 17092018  
	 * This method is used instead renderIreport for call from list of Experiment (button in table) and also (from 23072020) from Experiment form in Continues Process. On the Experiment form (CP) 
	 * is used a button, not ireport element Here are hard coded sql instead get it from map of from parameters file_, catalog should be correlated to names of files of reports (jasper .xml) 
	 * and catalogs are defined inside xml. Inside xml catalogs and names of files are defined as a parameters of the report and in subreport properties. There are 3 levels of this report. 
	 * All xmls which are used for Experiment Summary Report and for Step Summary Report.1st level .xml (EXPERIMENTANDSTEPSUMMARY.xml) is used only for Experiment Summary Report.
	 * .xmls of 2nd and 3rd level are used for both Experiment Summary Report and Step Summary report
	 * This algorithm and algorithm which is wired in the structure of xmls are related 
	*/
	private String renderIreportExperimentCP(long stateKey, String formCode, String fileName, String printTemplate,
			String reportType, String isDistinct, String title, String subTitle, String displayUrl, String sessionId,
			String experimentId) 
	{
		Connection conn = generalDao.getConnectionFromDataSurce();
		JasperReportGenerator jrg = null;
		String path_file = "";
		try {
			JasperDataSourceSupplier jrdss = new JasperDataSourceSupplier(conn, false);
			String sql = "";
			HashMap<String, String> mLang = new HashMap<String, String>();

			//  preparations...
			HashMap<String, String> hmReportReplacerList = new HashMap<String, String>();
			// hmReportReplacerList.put("@replacer1@", " and id = 1 ");
			HashMap<String, Object> hmReportParameterList = new HashMap<String, Object>();
			//kd 04122018
			JasperTemplateFactory jf = new JasperTemplateFactory(conn, DIR_JASPER_XML, DIR_JASPER_XML + "\\TEMPLATE",
					DIR_JASPER_XML + "\\TMP");

			hmReportParameterList.put("currentUrl", displayUrl);
			hmReportParameterList.put("parameter_DB_URL", DB_URL);
			hmReportParameterList.put("parameter_DB_USER", DB_USER);
			hmReportParameterList.put("parameter_DB_PASSWORD", DB_PASSWORD);
			hmReportParameterList.put("parameter_DIR_JASPER_XML", DIR_JASPER_XML);

			// -1 Summary Table
			String file_ = "experimentAndStepSumSummarySubSub.xml";
			String catalog_ = "CEXPSUM_SUMMARY";

			String fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			String fileTmp_ = fileName_ + sessionId + (new Date()).getTime();
			String hideEmptyColumns = "";
			
			
			sql = "select * from FG_R_EXPSUM_SUMMARY_V where EXPERIMENT_ID ='" + experimentId + "'";
			JSONObject jsonInsteadSql = generalDao.getJSONObjectOfDateTable(sql, hideEmptyColumns, "-1",null);
			
			if (!jsonInsteadSql.equals("")) {
				//createReportDataSource: 3th parameter is using  for set first column should in the table of report
				hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
						jrdss.createReportDataSource(jsonInsteadSql, "JRMapArrayDataSource",
								startColumnIndexSummary));
				logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
						+ ", catalog_=" + catalog_);

				// Experiment 'Summary Table' file kd10052020  //TODO Uncomment after Yaron check what is problem
				hmReportParameterList.put("SUB_REPORT_" + fileName_,
						jf.getJasperReportCompiledDataInjection(fileTmp_, jsonInsteadSql, "EXPERIMENT_ID",
								"EXPERIMENT_ID", false, startColumnIndexSummary));
				logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase()
						+ ", fileName_=" + fileName_ + ", fileTmp_=" + fileTmp_);
			}
			
			// 0 step
			file_ = "EXPERIMENTANDSTEPSUMMARY_CP.xml";
			catalog_ = "CSTSUM_REACTSCHEME";

			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
					jrdss.createReportDataSource(
							"select * from (select * from FG_R_STEPSUM_MAIN_CP_V where (EXPERIMENT_ID = "
									+ experimentId + "))",
							"JRMapArrayDataSource"));

			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);
			
			// 0.1 run
			file_ = "EXPERIMENTANDSTEPSUMRUN.xml";
			catalog_ = "CSTSUM_RUN";

			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
					jrdss.createReportDataSource(
							"select * from (select * from FG_R_STEPSUM_RUN_V where (EXPERIMENT_ID = "
									+ experimentId + "))",
							"JRMapArrayDataSource"));

			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);

			// 1.2 start material, preparation runs
			file_ = "EXPERIMENTANDSTEPSUMSTARTMATPREPARATIONSUBREPCP.xml";
			catalog_ = "CSTSUMSTARTMATERIALSPREPARAT";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();
			hideEmptyColumns = "";
			jsonInsteadSql = new JSONObject();

			sql = "select * from FG_R_STEPSUM_START_MATPRP_CP_V where (\"Type\" = 'Reactant' or \"Type\" = 'Solvent') and EXPERIMENT_ID ='" + experimentId + "'";

			jsonInsteadSql = generalDao.getJSONObjectOfDateTable(sql, hideEmptyColumns, "-1",null);
			if (!jsonInsteadSql.equals("")) {
				//createReportDataSource: 3th parameter is using  for set first column should in the table of report
				hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
						jrdss.createReportDataSource(jsonInsteadSql, "JRMapArrayDataSource", startColumnIndexMaterial));
				logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
						+ ", catalog_=" + catalog_);

				hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
						.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
				logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase()
						+ ", fileName_=" + fileName_ + ", fileTmp_=" + fileTmp_);

				//PLN..
				file_ = "EXPERIMENTANDSTEPSUMSTARTMATPREPARATIONSUBREPCP_PLN.xml";
				fileName_ = file_.replaceAll(".xml", "").toUpperCase();
				fileTmp_ = fileName_ + sessionId + (new Date()).getTime();
				hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
						.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			}
			
			// 1.3 start material
			file_ = "EXPERIMENTANDSTEPSUMSTARTMATSUBREPCP.xml";
			catalog_ = "CSTSUMSTARTMATERIALSCP";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();
			hideEmptyColumns = "";
			jsonInsteadSql = new JSONObject();

			sql = "select * from FG_R_STEPSUM_START_MATER_CP_V where (\"Type\" = 'Reactant' or \"Type\" = 'Solvent') and EXPERIMENT_ID ='" + experimentId + "'";

			jsonInsteadSql = generalDao.getJSONObjectOfDateTable(sql, hideEmptyColumns, "-1",null);
			if (!jsonInsteadSql.equals("")) {
				//createReportDataSource: 3th parameter is using  for set first column should in the table of report
				hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
						jrdss.createReportDataSource(jsonInsteadSql, "JRMapArrayDataSource", startColumnIndexMaterial));
				logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
						+ ", catalog_=" + catalog_);

				//getJasperReportCompiledDataInjection: 3th parameter is using  for set first column should in the table of report
				//				hmReportParameterList.put("SUB_REPORT_" + fileName_,jf.getJasperReportCompiledDataInjection(fileTmp_,jsonInsteadSql,"STEP_ID","STEP_ID",false, startColumnIndexMaterial));							  
				hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
						.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
				logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase()
						+ ", fileName_=" + fileName_ + ", fileTmp_=" + fileTmp_);

				//PLN..
				file_ = "EXPERIMENTANDSTEPSUMSTARTMATSUBREPCP_PLN.xml";
				fileName_ = file_.replaceAll(".xml", "").toUpperCase();
				fileTmp_ = fileName_ + sessionId + (new Date()).getTime();
//				jsonInsteadSql = generalDao.getJSONObjectOfDateTable( // kd 07072020 commented because this code was not in use
//						sql.replace("FG_R_STEPSUM_START_MATER_CP_V",
//								"FG_R_STEPSUM_START_MATCP_PLN_V"),
//						hideEmptyColumns, "-1");
				hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
						.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			}

			// 2 actions
			file_ = "EXPERIMENTANDSTEPSUMACTIONSSUBREP.xml";
			catalog_ = "CSTSUMACTIONS";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();
			hideEmptyColumns = "";

			//sql = "select * from fg_r_actionsummary_dtaexc_r_v where EXPERIMENT_ID_FOR_REPORT = " + experimentId + ""; //02072020 kd instead fg_r_actionsummary_dtaexc_v
			//improve performance -- yp 13092020
			sql = "select * from table(FN_GET_R_ACTIONSUMMARY_EXC('" + experimentId + "'))";
			
			jsonInsteadSql = generalDao.getJSONObjectOfDateTable(sql, hideEmptyColumns, "-1",null);
			if (!jsonInsteadSql.equals("")) {
				//createReportDataSource: 3th parameter is using  for set first column should in the table of report
				hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
						jrdss.createReportDataSource(jsonInsteadSql, "JRMapArrayDataSource", startColumnIndexActions));
				logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
						+ ", catalog_=" + catalog_);

				//getJasperReportCompiledDataInjection: 3th parameter is using  for set first column should in the table of report
				hmReportParameterList.put("SUB_REPORT_" + fileName_, jf.getJasperReportCompiledDataInjection(fileTmp_,
						jsonInsteadSql, "STEP_ID", "STEP_ID", false, startColumnIndexActions));
				//				hmReportParameterList.put("SUB_REPORT_" + fileName_,jf.getJasperReportCompiledDataInjection(fileTmp_,jsonInsteadSql,"","",false, startColumnIndexActions));							  
				logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase()
						+ ", fileName_=" + fileName_ + ", fileTmp_=" + fileTmp_);
			}

			// 3 product CP
			file_ = "EXPERIMENTANDSTEPSUMPRODUCTSUBREPCP.xml";
			catalog_ = "CSTSUMSTARTMATERIALSPRODCP"; 
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
					jrdss.createReportDataSource(
							"select * from FG_R_STEPSUM_START_MATER_CP_V where (EXPERIMENT_ID = " + experimentId + ")", //(FORMNUMBERID = " + stepId + "))";
							"JRMapArrayDataSource"));
			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);

			// 4 Step Parameters  (creating sub-report dynamically (on fly). Need only name of .xml but not design inside)
			jsonInsteadSql = new JSONObject();
			file_ = "experimentAndStepSumStepParametersSubSub.xml";
			catalog_ = "CSTSUM_PARAMETERS";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			sql = "select * from FG_R_STEPSUM_PARAMETERS_V where (EXP_ID = " + experimentId + ")"; //EXPERIMENT_ID = " + experimentId + ")"; //(
			
//			25/02/2020 ab fixed bug: when there were not saved rows in "Planned" status, and status changed to "Active", there are no rows in table FG_S_MATERIALREF_ALL_V_PLAN
//			if (generalDao.selectSingleString("select count(t.experiment_id) from FG_S_MATERIALREF_ALL_V_PLAN t where t.step_id = " + stepId) .equals("0")){
//			if (generalDao.selectSingleString("select st.stepstatusname from fg_s_step_v t, fg_s_stepstatus_v st where t.status_id = st.stepstatus_id and t.formid = " + stepId).equalsIgnoreCase("Planned")){
//			if (generalDao.selectSingleString("select st.stepstatusname from fg_s_step_v t, fg_s_stepstatus_v st where t.status_id = st.stepstatus_id and t.EXPERIMENT_ID = " + experimentId).equalsIgnoreCase("Planned")){
//				sql = sql.replace("FG_R_STEPSUM_PARAMETERS_V", "FG_R_STEPSUM_PARAMETERS_PLN_V");
//			}
			jsonInsteadSql = generalDao.getJSONObjectOfDateTable(sql, hideEmptyColumns, "-1",null);
			
			sql = sql.replace("FG_R_STEPSUM_PARAMETERS_V", "FG_R_STEPSUM_PARAMETERS_PLN_V");
			JSONObject jsonInsteadSqlPln = generalDao.getJSONObjectOfDateTable(sql, hideEmptyColumns,
				"-1",null);
			if (!jsonInsteadSql.equals("")) {
				//createReportDataSource: 3th parameter is using  for set first column should in the table of report
				hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
						jrdss.createReportDataSource(jsonInsteadSql, "JRMapArrayDataSource",
								startColumnIndexParameters));
				
				logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
				+ ", catalog_=" + catalog_);
						
				hmReportParameterList.put("SUB_REPORT_" + fileName_,
						jf.getJasperReportCompiledDataInjection(fileTmp_, jsonInsteadSql, "PARENTID",
								"STEP_ID", false, startColumnIndexParameters));
				
				logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
						+ fileName_ + ", fileTmp_=" + fileTmp_);
			}
			// PLN...
			if (!jsonInsteadSqlPln.equals("")) {
				//createReportDataSource: 3th parameter is using  for set first column should in the table of report
				hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase() + "_PLN",
						jrdss.createReportDataSource(jsonInsteadSqlPln, "JRMapArrayDataSource",
								startColumnIndexParameters));
				
				file_ = "experimentAndStepSumStepParametersSubSub_PLN.xml";
				fileName_ = file_.replaceAll(".xml", "").toUpperCase();
				fileTmp_ = fileName_ + sessionId + (new Date()).getTime();
				
				logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
				+ ", catalog_=" + catalog_);
				
				hmReportParameterList.put("SUB_REPORT_" + fileName_,
						jf.getJasperReportCompiledDataInjection(fileTmp_, jsonInsteadSqlPln, "PARENTID",
								"STEP_ID", false, startColumnIndexParameters));
				
				logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
						+ fileName_ + ", fileTmp_=" + fileTmp_);
			}
			
			// 5 Mass balance results
			file_ = "EXPERIMENTANDSTEPSUMMASSBALSUBREP.xml";
			catalog_ = "CSTSUMPRODUCT";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
					jrdss.createReportDataSource(
							"select * from (select r.* from FG_R_STEPSUM_PRODUCTS_V r, fg_s_step_pivot s where r.STEP_ID = s.formid and s.EXPERIMENT_ID = "
									+ experimentId + ") ",
							"JRMapArrayDataSource"));
			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);

			// 6 Mass balance streams group
			file_ = "EXPERIMENTANDSTEPSUMMASSBALSTREAMSSUBREP.xml";
			catalog_ = "CSTSUM_MASSBALANCEGR";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
					jrdss.createReportDataSource(
							"select * from (select r.* from FG_R_STEPSUM_MASSBALANCE_GR_V r, fg_s_step_pivot s where r.STEP_ID = s.formid and s.EXPERIMENT_ID = "
									+ experimentId + ") ",
							"JRMapArrayDataSource"));
			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);

			// 7 Mass balance streams details
			file_ = "EXPERIMENTANDSTEPSUMMASSBALSTREAMSSUBSUB.xml";
			catalog_ = "CSTSUM_MASSBALANCE";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
					jrdss.createReportDataSource(
							"select * from (select r.* from FG_R_STEPSUM_MASSBALANCE_V r, fg_s_step_pivot s where r.STEP_ID = s.formid and s.EXPERIMENT_ID = "
									+ experimentId + ") ",
							"JRMapArrayDataSource"));
			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);
			
			// 8 conclusion
			file_ = "experimentAndStepSumConclusionSubRepCP.xml";
			catalog_ = "CSTSUM_CONCLUSION";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
					jrdss.createReportDataSource(
							"select * from FG_R_STEPSUM_CONCLUSION_V where (EXPERIMENT_ID = " + experimentId + ")", //(FORMNUMBERID = " + stepId + "))";
							"JRMapArrayDataSource"));
			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);

			// final
			//	sql = "select * from (select r.* from FG_R_STEPSUM_REACT_SCHEM_V r, fg_s_step_pivot s where r.PARENTID = s.formid and s.EXPERIMENT_ID = " + experimentId + ")  where 1=1 ";
			sql = "select  * from (select * from FG_R_EXPSUM_MAIN_CP_V where (EXPERIMENT_ID = " + experimentId
					+ "))  where 1=1 ";
			JRDataSource myResultSetDS = new JRResultSetDataSource(generalDao.getResultSet(conn, sql));

			//  call..
			jrg = new JasperReportGenerator(myResultSetDS, mLang);
			path_file = jrg.getPath(fileName + (new Date()).getTime(), fileName,
					(reportType.equals("PDF") ? JasperReportType.PDF : JasperReportType.JXL_EXCEL), title, subTitle,
					hmReportReplacerList, hmReportParameterList, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp");
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);
		} catch (OutOfMemoryError er) {
			Exception e = new Exception(
					"OutOfMemoryError caught in 'public void renderIreportExperiment' function in FormApiService");
			generalUtilLogger.logWrite(e);
		} catch (NoSuchMethodError et) {
			Exception e = new Exception(
					"NoSuchMethodError caught in 'public void renderIreportExperiment' function in FormApiService");
			generalUtilLogger.logWrite(e);
			generalUtilLogger.logWrite(et);
			System.out.println("This is exception: " + et); //13122018 kd Temp for debug
		} finally {
			generalDao.releaseConnectionFromDataSurce(conn);
		}
		return path_file;
	}
	/**
	 * kd 17092018  
	 * This method is used instead renderIreport for call from list of Steps (button in table) and from Step form (from 23072020) too. On the form Step is used a button, not ireport element 
	 * Here are hard coded sql instead get it from map of from parameters file_, catalog should be correlated to names of files of reports (jasper .xml) and catalogs are defined inside xml. 
	 * Inside xml catalogs and names of files are defined as a parameters of the report and in subreport properties. There are 2 levels of this report. All 4 xml are used for
	 * Experiment Summary Report and for Step Summary Report. xml of the 1st level of StepSummary Report (EXPERIMENTANDSTEPSUMSTARTMATSUBREP.xml) has Filter Expression in "Report query" 
	 * window of iReport application (JasperReport)
	 * This algorithm and algorithm which is wired in the structure of xmls are related 
	 */
	private String renderIreportNoFormParam(long stateKey, String formCode, String fileName,
			String printTemplate, String reportType, String isDistinct, String title, String subTitle,
			String displayUrl, Map<String, String> displayValuesObj, String sessionId, String stepId) 
	{
		Connection conn = generalDao.getConnectionFromDataSurce();
		JasperReportGenerator jrg = null;
		String path_file = "";
		try {
			JasperDataSourceSupplier jrdss = new JasperDataSourceSupplier(conn, false);
			String sql = "";
			HashMap<String, String> mLang = new HashMap<String, String>();

			//  preparations...
			HashMap<String, String> hmReportReplacerList = new HashMap<String, String>();
			// hmReportReplacerList.put("@replacer1@", " and id = 1 ");
			HashMap<String, Object> hmReportParameterList = new HashMap<String, Object>();
			//kd 04122018
			JasperTemplateFactory jf = new JasperTemplateFactory(conn, DIR_JASPER_XML, DIR_JASPER_XML + "\\TEMPLATE",
					DIR_JASPER_XML + "\\TMP");

			for (Map.Entry<String, String> entry : displayValuesObj.entrySet()) {
				String rKey = entry.getKey().replace("$P{", "").replace("}", "");
				String rVal = entry.getValue();
				hmReportParameterList.put(rKey, rVal);
				logger.info("Ireport displayValuesObj parameter key=" + rKey + ", val=" + rVal);
			}

			hmReportParameterList.put("currentUrl", displayUrl);
			hmReportParameterList.put("parameter_DB_URL", DB_URL);
			hmReportParameterList.put("parameter_DB_USER", DB_USER);
			hmReportParameterList.put("parameter_DB_PASSWORD", DB_PASSWORD);
			hmReportParameterList.put("parameter_DIR_JASPER_XML", DIR_JASPER_XML);
			
			// 1 start material
			String file_ = "EXPERIMENTANDSTEPSUMSTARTMATSUBREP.xml";
			String catalog_ = "CSTSUMSTARTMATERIALS"; // see this in experimentAndStepSummary.xml -> 1st subreport -> Properties -> Subreport tab
			String fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			String fileTmp_ = fileName_ + sessionId + (new Date()).getTime();
			String hideEmptyColumns = "";

			sql = "select  * from (select * from FG_R_STEPSUM_START_MATER_V where (STEP_ID = " + stepId + "))";// CSTSUMSTARTMATERIALS and FG_R_STEPSUM_START_MATER_V are relaited.

			JSONObject jsonInsteadSql = generalDao.getJSONObjectOfDateTable(sql, hideEmptyColumns, "-1",null);
			if (!jsonInsteadSql.equals("")) {
				//createReportDataSource: 3th parameter is using  for set first column should in the table of report
				hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
						jrdss.createReportDataSource(jsonInsteadSql, "JRMapArrayDataSource", startColumnIndexMaterial));
				logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
						+ ", catalog_=" + catalog_);
				//getJasperReportCompiledDataInjection: 3th parameter is using  for set first column should in the table of report						  
				hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
						.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
				logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase()
						+ ", fileName_=" + fileName_ + ", fileTmp_=" + fileTmp_);
				//PLN..
				file_ = "EXPERIMENTANDSTEPSUMSTARTMATSUBREP_PLN.xml";// in case of Planned are used this xml. In subreport of experimentAndStepSummary.xml there is switch between xmls 
				fileName_ = file_.replaceAll(".xml", "").toUpperCase();
				fileTmp_ = fileName_ + sessionId + (new Date()).getTime();
				jsonInsteadSql = generalDao.getJSONObjectOfDateTable(
						sql.replace("FG_R_STEPSUM_START_MATER_V",
								"FG_R_STEPSUM_START_MATER_PLN_V")/*"select * from kd_test t where 1=1"*/,
						hideEmptyColumns, "-1",null);
				hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
						.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			}

			// 2 actions
			file_ = "EXPERIMENTANDSTEPSUMACTIONSSUBREP.xml";
			catalog_ = "CSTSUMACTIONS";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();
			hideEmptyColumns = "";

			//sql = "select  * from (select * from fg_r_actionsummary_dtaexc_v where (STEP_ID = " + stepId + "))";
			//improve performance -- yp 13092020
			String experimentId = generalDao.selectSingleString("select distinct experiment_id from fg_s_step_v where step_id = '" + stepId + "'");
			sql = "select * from table(FN_GET_R_ACTIONSUMMARY_EXC('" + experimentId + "')) where (STEP_ID = '" + stepId + "')";
			
			jsonInsteadSql = generalDao.getJSONObjectOfDateTable(sql, hideEmptyColumns, "-1",null);
			if (!jsonInsteadSql.equals("")) {
				//createReportDataSource: 3th parameter is using  for set first column should in the table of report
				hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
						jrdss.createReportDataSource(jsonInsteadSql, "JRMapArrayDataSource", startColumnIndexActions));
				logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
						+ ", catalog_=" + catalog_);

				//getJasperReportCompiledDataInjection: 3th parameter is using  for set first column should in the table of report
				//				hmReportParameterList.put("SUB_REPORT_" + fileName_,jf.getJasperReportCompiledDataInjection(fileTmp_,jsonInsteadSql,"STEP_ID","STEP_ID",false, startColumnIndexActions));							  
				hmReportParameterList.put("SUB_REPORT_" + fileName_, jf.getJasperReportCompiledDataInjection(fileTmp_,
						jsonInsteadSql, "", "", false, startColumnIndexActions));
				logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase()
						+ ", fileName_=" + fileName_ + ", fileTmp_=" + fileTmp_);
			}
			// 3 product
			file_ = "EXPERIMENTANDSTEPSUMPRODUCTSUBREP.xml";
			catalog_ = "CSTSUMPRODUCT";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
					jrdss.createReportDataSource(
							"select  * from (select * from FG_R_STEPSUM_PRODUCTS_V where (STEP_ID = " + stepId + "))",
							"JRMapArrayDataSource"));
			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);

			// 5 parameters  (creating sub-report dynamically (on fly). Need only name of .xml but not design inside)
			jsonInsteadSql = new JSONObject();
			file_ = "experimentAndStepSumStepParametersSubSub.xml";
			catalog_ = "CSTSUM_PARAMETERS";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			sql = "select * from (select * from FG_R_STEPSUM_PARAMETERS_V where (PARENTID = " + stepId + "))";
			
//			25/02/2020 ab fixed bug: when there were not saved rows in "Planned" status, and status changed to "Active", there are no rows in table FG_S_MATERIALREF_ALL_V_PLAN
//			if (generalDao.selectSingleString("select count(t.experiment_id) from FG_S_MATERIALREF_ALL_V_PLAN t where t.step_id = " + stepId) .equals("0")){
			if (generalDao.selectSingleString("select st.stepstatusname from fg_s_step_v t, fg_s_stepstatus_v st where t.status_id = st.stepstatus_id and t.formid = " + stepId).equalsIgnoreCase("Planned")){
				sql = sql.replace("FG_R_STEPSUM_PARAMETERS_V", "FG_R_STEPSUM_PARAMETERS_PLN_V");
			}
			jsonInsteadSql = generalDao.getJSONObjectOfDateTable(sql, hideEmptyColumns, "-1",null);
			
			if (!jsonInsteadSql.equals("")) {
				// createReportDataSource: 3th parameter is using  for set first column should in the table of report
				hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
						jrdss.createReportDataSource(jsonInsteadSql, "JRMapArrayDataSource",
								startColumnIndexMaterial));
			
				logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
						+ ", catalog_=" + catalog_);
				
				hmReportParameterList.put("SUB_REPORT_" + fileName_,
						jf.getJasperReportCompiledDataInjection(fileTmp_, jsonInsteadSql, "PARENTID",
								"STEP_ID", false, startColumnIndexActions));
				
				logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
						+ fileName_ + ", fileTmp_=" + fileTmp_);
			}
			
			// 6 Mass balance results
			file_ = "EXPERIMENTANDSTEPSUMMASSBALSUBREP.xml";
			catalog_ = "CSTSUMPRODUCT";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
					jrdss.createReportDataSource(
							"select * from (select * from FG_R_STEPSUM_PRODUCTS_V where (STEP_ID = " + stepId + "))",
							"JRMapArrayDataSource"));
			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);

			// 7 Mass balance streams group
			file_ = "EXPERIMENTANDSTEPSUMMASSBALSTREAMSSUBREP.xml";
			catalog_ = "CSTSUM_MASSBALANCEGR";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(), jrdss.createReportDataSource(
					"select * from (select * from FG_R_STEPSUM_MASSBALANCE_GR_V where (STEP_ID = " + stepId + "))",
					"JRMapArrayDataSource"));
			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);

			// SUB_REPORT_EXPERIMENTANDSTEPSUMMASSBALSTREAMSSUBREP
			// 8 Mass balance streams details
			file_ = "EXPERIMENTANDSTEPSUMMASSBALSTREAMSSUBSUB.xml";
			catalog_ = "CSTSUM_MASSBALANCE";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
					jrdss.createReportDataSource(
							"select * from (select * from FG_R_STEPSUM_MASSBALANCE_V where (STEP_ID = " + stepId + "))",
							"JRMapArrayDataSource"));
			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);

			sql = "select  * from (select * from FG_R_STEPSUM_REACT_SCHEM_V where (PARENTID = " + stepId
					+ "))  where 1=1 ";
			JRDataSource myResultSetDS = new JRResultSetDataSource(generalDao.getResultSet(conn, sql));

			// call..
			jrg = new JasperReportGenerator(myResultSetDS, mLang);
			path_file = jrg.getPath(fileName + (new Date()).getTime(), fileName,
					(reportType.equals("PDF") ? JasperReportType.PDF : JasperReportType.JXL_EXCEL), title, subTitle,
					hmReportReplacerList, hmReportParameterList, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp");
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);

		} catch (OutOfMemoryError er) {
			Exception e = new Exception(
					"OutOfMemoryError caught in 'public void renderIreportNoFormParam' function in FormApiService");
			generalUtilLogger.logWrite(e);

		} catch (NoSuchMethodError et) {
			Exception e = new Exception(
					"NoSuchMethodError caught in 'public void renderIreport' function in FormApiService");
			generalUtilLogger.logWrite(e);
			System.out.println("This is exception: " + et); //13122018 kd Temp for debug
		} finally {
			generalDao.releaseConnectionFromDataSurce(conn);
		}
		return path_file;
	}
	
	/**
	 * kd 17092018  
	 * This method is used instead renderIreport for call from list of Steps (button in table) and from Step form (from 23072020) too in Continues Process. On the form Step (CP) is used a button, not ireport element 
	 * Here are hard coded sql instead get it from map of from parameters file_, catalog should be correlated to names of files of reports (jasper .xml) and catalogs are defined inside xml. 
	 * Inside xml catalogs and names of files are defined as a parameters of the report and in subreport properties. There are 2 levels of this report. All xmls which are used for
	 * Experiment Summary Report and for Step Summary Report. xml of the 1st level of StepSummary Report (EXPERIMENTANDSTEPSUMSTARTMATSUBREP.xml) has Filter Expression in "Report query" 
	 * window of iReport application (JasperReport)
	 * This algorithm and algorithm which is wired in the structure of xmls are related 
	*/
	private String renderIreportNoFormParamCP(long stateKey, String formCode, String fileName, String printTemplate, String reportType, String isDistinct, 
			 String title,String subTitle,String displayUrl, Map<String, String> displayValuesObj, String sessionId, String experimentId, String formNumberId) 
	{
		Connection conn = generalDao.getConnectionFromDataSurce();
		JasperReportGenerator jrg = null;
		String path_file = "";
		try {
			JasperDataSourceSupplier jrdss = new JasperDataSourceSupplier(conn, false);
			String sql = "";
			HashMap<String, String> mLang = new HashMap<String, String>();

			//  preparations...
			HashMap<String, String> hmReportReplacerList = new HashMap<String, String>();
			HashMap<String, Object> hmReportParameterList = new HashMap<String, Object>();
			// kd 04122018
			JasperTemplateFactory jf = new JasperTemplateFactory(conn, DIR_JASPER_XML, DIR_JASPER_XML + "\\TEMPLATE",
					DIR_JASPER_XML + "\\TMP");

			for (Map.Entry<String, String> entry : displayValuesObj.entrySet()) {
				String rKey = entry.getKey().replace("$P{", "").replace("}", "");
				String rVal = entry.getValue();
				hmReportParameterList.put(rKey, rVal);
				logger.info("Ireport displayValuesObj parameter key=" + rKey + ", val=" + rVal);
			}

			hmReportParameterList.put("currentUrl", displayUrl);
			hmReportParameterList.put("parameter_DB_URL", DB_URL);
			hmReportParameterList.put("parameter_DB_USER", DB_USER);
			hmReportParameterList.put("parameter_DB_PASSWORD", DB_PASSWORD);
			hmReportParameterList.put("parameter_DIR_JASPER_XML", DIR_JASPER_XML);
			
			// 0 start material CP
			String file_ = "EXPERIMENTANDSTEPSUMRUN.xml";
			String catalog_ = "CSTSUM_RUN";
			String fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			String fileTmp_ = fileName_ + sessionId + (new Date()).getTime();
			String hideEmptyColumns = "";

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
					jrdss.createReportDataSource(
							"select * from FG_R_STEPSUM_RUN_V where (EXPERIMENT_ID = " + experimentId + " and FORMNUMBERID = " + formNumberId + ")", //PARENTID = " + stepId + ")",
							"JRMapArrayDataSource"));
			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);
			
			// 1.01 start material, preparation runs
			file_ = "EXPERIMENTANDSTEPSUMSTARTMATPREPARATIONSUBREPCP.xml";
			catalog_ = "CSTSUMSTARTMATERIALSPREPARAT";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();
			hideEmptyColumns = "";
			sql = "select * from FG_R_STEPSUM_START_MATPRP_CP_V where (\"Type\" = 'Reactant' or \"Type\" = 'Solvent') and EXPERIMENT_ID ='" + experimentId + "'";

			JSONObject jsonInsteadSql = generalDao.getJSONObjectOfDateTable(sql, hideEmptyColumns, "-1",null);
			if (!jsonInsteadSql.equals("")) {
				//createReportDataSource: 3th parameter is using  for set first column should in the table of report
				hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
						jrdss.createReportDataSource(jsonInsteadSql, "JRMapArrayDataSource", startColumnIndexMaterial));
				logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
						+ ", catalog_=" + catalog_);

				hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
						.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
				logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase()
						+ ", fileName_=" + fileName_ + ", fileTmp_=" + fileTmp_);

				//PLN..
				file_ = "EXPERIMENTANDSTEPSUMSTARTMATPREPARATIONSUBREPCP_PLN.xml";
				fileName_ = file_.replaceAll(".xml", "").toUpperCase();
				fileTmp_ = fileName_ + sessionId + (new Date()).getTime();
				hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
						.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			}
			
			// 1.02 start material CP
			file_ = "EXPERIMENTANDSTEPSUMSTARTMATSUBREPCP.xml";
			catalog_ = "CSTSUMSTARTMATERIALSCP";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			sql = "select * from FG_R_STEPSUM_START_MATER_CP_V where (\"Type\" = 'Reactant' or \"Type\" = 'Solvent') and (EXPERIMENT_ID = " + experimentId + ")";//(FORMNUMBERID = " + stepId + "))";

			jsonInsteadSql = generalDao.getJSONObjectOfDateTable(sql, hideEmptyColumns, "-1",null);
			if (!jsonInsteadSql.equals("")) {
				//createReportDataSource: 3th parameter is using  for set first column should in the table of report
				hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
						jrdss.createReportDataSource(jsonInsteadSql, "JRMapArrayDataSource", startColumnIndexMaterial));
				logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
						+ ", catalog_=" + catalog_);
				//getJasperReportCompiledDataInjection: 3th parameter is using  for set first column should in the table of report						  
				hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
						.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
				logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase()
						+ ", fileName_=" + fileName_ + ", fileTmp_=" + fileTmp_);
				//PLN..
				file_ = "EXPERIMENTANDSTEPSUMSTARTMATSUBREPCP_PLN.xml";
				fileName_ = file_.replaceAll(".xml", "").toUpperCase();
				fileTmp_ = fileName_ + sessionId + (new Date()).getTime();
				hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
						.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			}

			// 2 actions
			file_ = "EXPERIMENTANDSTEPSUMACTIONSSUBREP.xml";
			catalog_ = "CSTSUMACTIONS";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();
			hideEmptyColumns = "";

			//sql = "select * from fg_r_actionsummary_dtaexc_r_v where (EXPERIMENT_ID_FOR_REPORT = " + experimentId + ")"; //(FORMNUMBERID = " + stepId + "))"; //02072020 kd instead fg_r_actionsummary_dtaexc_v
			//improve performance -- yp 13092020
			sql = "select * from table(FN_GET_R_ACTIONSUMMARY_EXC('" + experimentId + "'))";

			jsonInsteadSql = generalDao.getJSONObjectOfDateTable(sql, hideEmptyColumns, "-1",null);
			if (!jsonInsteadSql.equals("")) {
				//createReportDataSource: 3th parameter is using  for set first column should in the table of report
				hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
						jrdss.createReportDataSource(jsonInsteadSql, "JRMapArrayDataSource", startColumnIndexActions));
				logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
						+ ", catalog_=" + catalog_);

				//getJasperReportCompiledDataInjection: 3th parameter is using  for set first column should in the table of report
				//				hmReportParameterList.put("SUB_REPORT_" + fileName_,jf.getJasperReportCompiledDataInjection(fileTmp_,jsonInsteadSql,"STEP_ID","STEP_ID",false, startColumnIndexActions));							  
				hmReportParameterList.put("SUB_REPORT_" + fileName_, jf.getJasperReportCompiledDataInjection(fileTmp_,
						jsonInsteadSql, "STEP_ID", "STEP_ID", false, startColumnIndexActions));
				logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase()
						+ ", fileName_=" + fileName_ + ", fileTmp_=" + fileTmp_);
			}
			// 3 product CP
			file_ = "EXPERIMENTANDSTEPSUMPRODUCTSUBREPCP.xml";
			catalog_ = "CSTSUMSTARTMATERIALSPRODCP";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
					jrdss.createReportDataSource(
							"select * from FG_R_STEPSUM_START_MATER_CP_V where (EXPERIMENT_ID = " + experimentId + ")", //(FORMNUMBERID = " + stepId + "))";
							"JRMapArrayDataSource"));
			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);

			// 4 Step Parameters  (creating sub-report dynamically (on fly). Need only name of .xml but not design inside)
			jsonInsteadSql = new JSONObject();
			JSONObject jsonInsteadSqlPln = new JSONObject();
			file_ = "experimentAndStepSumStepParametersSubSub.xml";
			catalog_ = "CSTSUM_PARAMETERS";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			sql = "select * from FG_R_STEPSUM_PARAMETERS_V where (EXP_ID = " + experimentId + ")"; //EXPERIMENT_ID = " + experimentId + ")"; //(
			jsonInsteadSql = generalDao.getJSONObjectOfDateTable(sql, hideEmptyColumns, "-1",null);
			
			sql = sql.replace("FG_R_STEPSUM_PARAMETERS_V", "FG_R_STEPSUM_PARAMETERS_PLN_V");
			jsonInsteadSqlPln = generalDao.getJSONObjectOfDateTable(sql, hideEmptyColumns, "-1",null);
			if (!jsonInsteadSql.equals("")) {
				// createReportDataSource: 3th parameter is using  for set first column should in the table of report
				hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
						jrdss.createReportDataSource(jsonInsteadSql, "JRMapArrayDataSource",
								startColumnIndexParameters));
				
				logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
				+ ", catalog_=" + catalog_);
						
				hmReportParameterList.put("SUB_REPORT_" + fileName_,
						jf.getJasperReportCompiledDataInjection(fileTmp_, jsonInsteadSql, "PARENTID",
								"STEP_ID", false, startColumnIndexParameters));
				
				logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
						+ fileName_ + ", fileTmp_=" + fileTmp_);
			}
			// PLN...
			if (!jsonInsteadSqlPln.equals("")) {
				//createReportDataSource: 3th parameter is using  for set first column should in the table of report
				hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase() + "_PLN",
						jrdss.createReportDataSource(jsonInsteadSqlPln, "JRMapArrayDataSource",
								startColumnIndexParameters));
				
				file_ = "experimentAndStepSumStepParametersSubSub_PLN.xml";
				fileName_ = file_.replaceAll(".xml", "").toUpperCase();
				fileTmp_ = fileName_ + sessionId + (new Date()).getTime();
				
				logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
				+ ", catalog_=" + catalog_);
				
				hmReportParameterList.put("SUB_REPORT_" + fileName_,
						jf.getJasperReportCompiledDataInjection(fileTmp_, jsonInsteadSqlPln, "PARENTID",
								"STEP_ID", false, startColumnIndexParameters));
				
				logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
						+ fileName_ + ", fileTmp_=" + fileTmp_);
			}
			
			// 5 Mass balance results
			file_ = "EXPERIMENTANDSTEPSUMMASSBALSUBREP.xml";
			catalog_ = "CSTSUMPRODUCT";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
					jrdss.createReportDataSource(
							"select * from FG_R_STEPSUM_PRODUCTS_V where (EXPERIMENT_ID = " + experimentId + ")", //(FORMNUMBERID = " + stepId + "))";
							"JRMapArrayDataSource"));
			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);

			// 6 Mass balance streams group
			file_ = "EXPERIMENTANDSTEPSUMMASSBALSTREAMSSUBREP.xml";
			catalog_ = "CSTSUM_MASSBALANCEGR";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(), jrdss.createReportDataSource(
					"select * from FG_R_STEPSUM_MASSBALANCE_GR_V where (EXPERIMENT_ID = " + experimentId + ")", //(FORMNUMBERID = " + stepId + "))";
					"JRMapArrayDataSource"));
			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);

			// 7 Mass balance streams details
			// SUB_REPORT_EXPERIMENTANDSTEPSUMMASSBALSTREAMSSUBREP
			file_ = "EXPERIMENTANDSTEPSUMMASSBALSTREAMSSUBSUB.xml";
			catalog_ = "CSTSUM_MASSBALANCE";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
					jrdss.createReportDataSource(
							"select * from FG_R_STEPSUM_MASSBALANCE_V where (EXPERIMENT_ID = " + experimentId + ")", //(FORMNUMBERID = " + stepId + "))";
							"JRMapArrayDataSource"));
			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);
			
			// 9 conclusion
			file_ = "experimentAndStepSumConclusionSubRepCP.xml";
			catalog_ = "CSTSUM_CONCLUSION";
			fileName_ = file_.replaceAll(".xml", "").toUpperCase();
			fileTmp_ = fileName_ + sessionId + (new Date()).getTime();

			hmReportParameterList.put("DATA_SOURCE_" + catalog_.toUpperCase(),
					jrdss.createReportDataSource(
							"select * from FG_R_STEPSUM_CONCLUSION_V where (EXPERIMENT_ID = " + experimentId + ")", //(FORMNUMBERID = " + stepId + "))";
							"JRMapArrayDataSource"));
			logger.info("Ireport SUB-REPORT parameter catalog: key= DATA_SOURCE_" + catalog_.toUpperCase()
					+ ", catalog_=" + catalog_);
			hmReportParameterList.put("SUB_REPORT_" + fileName_, (new jasper.biz.JasperReportGenerator(mLang))
					.getCompiled(fileTmp_, file_, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp", null));
			logger.info("Ireport parameter SUB-REPORT file: key=SUB_REPORT_" + fileName_.toUpperCase() + ", fileName_="
					+ fileName_ + ", fileTmp_=" + fileTmp_);

			// final
//			sql = "select  * from (select * from FG_R_STEPSUM_REACT_SCHEM_V where (EXPERIMENT_ID = " + experimentId   //(FORMNUMBERID = " + stepId + ;
			sql = "select * from FG_R_STEPSUM_MAIN_CP_V where (EXPERIMENT_ID = " + experimentId  + " and FORMNUMBERID = " + formNumberId 
					+ ")";
			JRDataSource myResultSetDS = new JRResultSetDataSource(generalDao.getResultSet(conn, sql));

			// call..
			jrg = new JasperReportGenerator(myResultSetDS, mLang);
			path_file = jrg.getPath(fileName + (new Date()).getTime(), fileName,
					(reportType.equals("PDF") ? JasperReportType.PDF : JasperReportType.JXL_EXCEL), title, subTitle,
					hmReportReplacerList, hmReportParameterList, DIR_JASPER_XML, DIR_JASPER_XML + "/tmp");
		} catch (Exception ex) {
			generalUtilLogger.logWrite(ex);

		} catch (OutOfMemoryError er) {
			Exception e = new Exception(
					"OutOfMemoryError caught in 'public void renderIreportNoFormParam' function in FormApiService");
			generalUtilLogger.logWrite(e);

		} catch (NoSuchMethodError et) {
			Exception e = new Exception(
					"NoSuchMethodError caught in 'public void renderIreport' function in FormApiService");
			generalUtilLogger.logWrite(e);
			System.out.println("This is exception: " + et); //13122018 kd Temp for debug
		} finally {
			generalDao.releaseConnectionFromDataSurce(conn);
		}

		return path_file;
	}
	
	/**
	 * Copy self-test data from different steps
	 * @param sourceFormId
	 * @param cloneFormId
	 * @param selfTestType
	 * @param userId
	 */
	private void cloneSelfTestRefData(String sourceFormId, String cloneFormId, String currentExperimentId,
			String selfTestTypeId, String sampleId, String userId) {
		if (sourceFormId != null) {

			// Type clone  AnalytMethodSelect
			cloneExperiment.insertRef("AnalytMethodSelect", sourceFormId, userId, "SELFTEST", cloneFormId, null, null,
					"");
			
			// Instrument clone
			cloneExperiment.insertRef("InstrumentSelect", sourceFormId, userId, "SELFTEST", cloneFormId, null, null,
					"");
			// ColumnSelect
			cloneExperiment.insertRef("ColumnSelect", sourceFormId, userId, "SELFTEST", cloneFormId, null, null, "");
			
			// ResultRef
			cloneExperiment.insertRef("ResultRef", sourceFormId, userId, "SELFTEST", cloneFormId, null, null, "");

			// set instrument-ext from source test (not sure if needed)
			String sql = "select t.* from fg_s_SelfTest_pivot t where FORMID = '" + sourceFormId + "'";
			Map<String, String> dbVal = generalDao.sqlToHashMap(sql);
			if (dbVal.get("INSTRUMENTEXT") != null && !(dbVal.get("INSTRUMENTEXT")).isEmpty()) {
				// Update External instrument and webixAnalyticalSelfTest
				sql = "update fg_s_SelfTest_pivot t set t.instrumentext ='" + dbVal.get("INSTRUMENTEXT")
						+ "' where formid = '" + cloneFormId + "'";
				formSaveDao.updateSingleStringInfoNoTryCatch(sql);
			}

			// gets the results elements and their UOM
//			String ref ="ResultRef";
			String selfTestTypeName = formDao.getFromInfoLookup("selfTestType", LookupType.ID, selfTestTypeId, "name");

			List<Result> resultList = new ArrayList<Result>();
			List<String> resultElements = null;

			resultElements = Arrays.asList("normalization,NORMALUOM_ID","NONNUMERICRESULT,NA");// generalUtilForm.getResultElementList("ResultRef");
			
			List<String> resultRefIds = generalDao
					.getListOfStringBySql("select distinct FORMID from FG_S_ResultRef_ALL_V where PARENTID = "
							+ cloneFormId + " and SESSIONID is null and active = 1");

			// runs through the resultRef forms that are referenced to the current selftest (as in self test post save result)
			for (String resultId : resultRefIds) {// .get(i).entrySet()){
				// runs through the result elements in the resultRef entry
				Map<String, String> row = generalDao
						.getMetaDataRowValues("select distinct * from FG_S_ResultRef_ALL_V where FORMID = " + resultId);
				for (String element : resultElements) {
					String elementName = element.split(",")[0].trim();
					String UOMelement = element.split(",")[1].trim();
					if (row.get(elementName.toUpperCase()) == null) {
						continue;
					}
					// if the selftest is non numeric-> then takes the material that is connected to
					// the sample
					String materialId = selfTestTypeName
							.equals("Non-Numeric")
									? formDao.getFromInfoLookup("Sample", LookupType.ID, sampleId, "PRODUCTID")
									: (row.get("MATERIAL_ID") != null ? row.get("MATERIAL_ID").toString() : "");
					
					resultList.add(new Result(currentExperimentId, formDao.getFromInfoLookup("EXPERIMENT",
							LookupType.ID, currentExperimentId, "PROTOCOLTYPENAME")// test type
							, formDao.getFromInfoLookup("RESULTTYPE", LookupType.ID, row.get("RESULTTYPE_ID"), "name"),
							sampleId,
							row.get(elementName.toUpperCase()) != null ? row.get(elementName.toUpperCase()).toString()
									: "",
							UOMelement.equals("NA") ? ""
									: row.get(UOMelement.toUpperCase()) != null
											? row.get(UOMelement.toUpperCase()).toString()
											: "",
											(row.get(elementName.toUpperCase()) != null
											? row.get(elementName.toUpperCase()).toString()
													.matches("-?\\d+(\\.\\d+)?") ? "NUMERIC" : "STRING"
											: ""),
											materialId, row.get("COMMENTS") != null ? row.get("COMMENTS").toString() : "",
							cloneFormId// selftest_id
							, "", resultId, "0",
							row.containsKey("OPTIONALMATERIALNAME") ? row.get("OPTIONALMATERIALNAME") : "", ""));
				}
			}
			
			commonFunc.insertToResults(resultList, cloneFormId, null,false);
		}
	}
	private List<String> getImpuritiesByMaxConcentration(String level,String experimentsList)
	{
		List<String> impurities = null;
		if(level.equals("step")){
			impurities = generalDao.getListOfStringBySql(
					"select distinct stepname || ':' || rank_result_value  || ',' || invitemmaterialname  from (\r\n"
							+ "select DENSE_RANK() OVER (PARTITION BY t1.step_id ORDER BY result_value DESC ) AS rank_result_value,\r\n"
							+ "       t1.step_id,\r\n" + "       t1.experiment_id,\r\n"
							+ "       t1.FORMNUMBERID as stepname,\r\n" + "       t1.invitemmaterialname,\r\n"
							+ "       t1.result_value\r\n" + "from fg_i_expanalysis_impurity_st_v t1\r\n"
							+ "where t1.result_value is not null\r\n" + "and t1.experiment_id in (" + experimentsList
							+ ")\r\n" + ")\r\n"
							+ "order by  stepname || ':' || rank_result_value  || ',' || invitemmaterialname");
		}
		return impurities;
    
	}
	
	
		private void insertDesign(String userId)
		{

			String cols = generalDao.getTableColCsv("FG_S_REPORTDESIGNEXP_PIVOT");
			List<String> colsList = Arrays.asList(cols.toString().split("\\s*,\\s*"));
			String newFormId = formSaveDao.getStructFormId("ReportDesignExp");
			StringBuilder vals = new StringBuilder();
			StringBuilder csvCols = new StringBuilder();
			generalUtilDesignData.getAdditionalInfo().put("formId", newFormId);
			
			for (String col : colsList) {

				for (Map.Entry<String, String> map : generalUtilDesignData.getDesignFormElementValueMap().entrySet()) {
					if (map.getKey().toUpperCase().equals(col)) {

						if (col.equals("USINGSTEPS")) {
							vals.append("'{" + map.getValue() + "}'");
							csvCols.append(col);
						} else if (col.equals("PARAMETERSDESIGN")) {
							vals.append("''");
							csvCols.append(col);
						} else if (col.equals("STEPSDESIGN")) {
							vals.append("''");
							csvCols.append(col);
						} else if (col.equals("impuritiesStepsDesign".toUpperCase())) {
							vals.append("''");
							csvCols.append(col);
						}

						else if (col.equals("formId".toUpperCase())) {
							vals.append(newFormId);
							csvCols.append(col);
						}

						else {
							if (map.getValue().equals("")) {
								vals.append("''");
								csvCols.append(col);
							} else if (map.getValue().isEmpty()) {
								vals.append("''");
								csvCols.append(col);
							} else {
								vals.append("'" + map.getValue() + "'");
								csvCols.append(col);
							}
						}

						vals.append(",");
						csvCols.append(",");

					}
				}

			}

			String csvVals = generalUtil.replaceLast(vals.toString(), ",", "");
			String colsCsv = generalUtil.replaceLast(csvCols.toString(), ",", "");

			String sql_ = "insert into FG_S_REPORTDESIGNEXP_PIVOT"
					+ " (FORMID,TIMESTAMP,CREATION_DATE,CLONEID,TEMPLATEFLAG,CHANGE_BY,CREATED_BY,SESSIONID,ACTIVE,FORMCODE_ENTITY,FORMCODE,"
					+ colsCsv + ")" + " VALUES ('" + newFormId + "',SYSDATE,SYSDATE,null,null,'" + userId + "','" + userId
					+ "',null,1,'ReportDesignExp','ReportDesignExp'," + csvVals + ")";

			formSaveDao.insertStructTableByFormId(sql_, "FG_S_REPORTDESIGNEXP_PIVOT", newFormId);

			
			
		
			String StepsDesign = generalUtil.MapOfListToJson(generalUtilDesignData.getStepDesignMapwithList());
			String ImpuritiesStepsDesign = generalUtil.MapOfListToJson(generalUtilDesignData.getStepImpuritesDesignMap());
			String id_ = uploadFileDao.saveStringAsClobRenderId("ReportDesign", StepsDesign);
			String parametersId_ = uploadFileDao.saveStringAsClobRenderId("parametersReportDesign",
					generalUtilDesignData.getDesignFormElementValueMap().get("parametersDesign"));

			String sql_1 = "update fg_s_reportdesignexp_pivot set STEPSDESIGN  = '" + id_ + "',impuritiesStepsDesign='"
					+ ImpuritiesStepsDesign + "',parametersDesign='" + parametersId_ + "' where formId = '" + newFormId
					+ "'";
			formSaveDao.updateStructTableByFormId(sql_1, "FG_S_REPORTDESIGNEXP_PIVOT",
					Arrays.asList("stepsDesign,impuritiesStepsDesign,parametersDesign"), newFormId);

		}
//	private String cleanningWebixTableResult(String webixData, String cleanColumnsCSV) {
//		if(cleanColumnsCSV == null || cleanColumnsCSV.equals(""))
//		{
//			return webixData;
//		} 
//		JSONObject newWebixData = new JSONObject();
//		try {
//			JSONArray rowsData = new JSONArray(generalUtil.getJsonValById(webixData,"data"));	
//			//JSONArray rowsData = new JSONArray());
//			
//			JSONObject internalJObj; //kd 02122019 use this JSONObject for correct adding string to JSONArray. Otherwise \" symbols are added.
//			
//			String[] cleanColumnArray = cleanColumnsCSV.split(",");
//			
//			for (int i = 0; i < rowsData.length(); i++) {
//				String streamRow = rowsData.getString(i);
//				for(int j = 0; j < cleanColumnArray.length; j++)
//				{
//					streamRow = generalUtil.updateJsonValById(streamRow, cleanColumnArray[j], "");
//				}
//				internalJObj = new JSONObject(streamRow);
//				rowsData.put(i,internalJObj);
//			}			
//			newWebixData.put("data",rowsData);
//			
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return newWebixData.toString();
//	}
	
	



		private String getColDesign(Map<String, String> elementValueMap, String formId,String userId)
		{

			StringBuilder messages = new StringBuilder();

		//Collect data from design form 

			//expDesignCsv -experiment data
			
			StringBuilder expDesignCsv = new StringBuilder();
			for (String expData:generalUtilDesignData.getExperimentDataList()) {
					expDesignCsv.append(expData);
					expDesignCsv.append(',');
			}


			//stepsDesignMap-steps data
			
			Map<String, List<String>> stepsDesignMap = new HashMap<String, List<String>>();
			stepsDesignMap = generalUtilDesignData.getStepDesignMapwithList();
			List<String>stepDisplayImpurities = new ArrayList<>();

		   //impuritiesDesignMap-impurities data
			
			Map<String, List<String>> impuritiesDesignStepMap = new HashMap<String, List<String>>();

			impuritiesDesignStepMap = generalUtilDesignData.getStepImpuritesDesignMap();
			
			//runs through all the steps and get the steps that impurities where defined for in the design in order to display a proper message if no characteristic sample was selected for some of them
			for(Entry<String,List<String>> entry:stepsDesignMap.entrySet()){
				if(entry.getValue().contains("chkImpuritiesStep")){//checked All Impurities
					stepDisplayImpurities.add(entry.getKey());
				} else if(impuritiesDesignStepMap.containsKey(entry.getKey())){//defined a table of impurities
					stepDisplayImpurities.add(entry.getKey());
				}
				else{//defined max num of impurities
					for(String item:entry.getValue()){
						if(item.contains("numConcentrationImpStep")){
							stepDisplayImpurities.add(entry.getKey());
							break;
						}
					}//.
				}
			}

		    //impuritiesConcentrationDesignMap-impurities Concentration data of the steps
			Map<String, String> maxImpuritiesMap = new HashMap<String, String>();
			List<String> maxImpuritiesList = getImpuritiesByMaxConcentration("step",elementValueMap.get("experimentList"));
					for (String impurity : maxImpuritiesList) {
				String imp = maxImpuritiesMap.get(impurity.split(":")[0]);
				if (imp == null)
					maxImpuritiesMap.put(impurity.split(":")[0], impurity.split(":")[1]);
				else
					maxImpuritiesMap.put(impurity.split(":")[0],
							maxImpuritiesMap.get(impurity.split(":")[0]) + ";" + impurity.split(":")[1]);

			}

		    //parametersDesignMap-parameters data
			
			

			Map<String, List<String>> parametersDesignMap = new HashMap<String, List<String>>();
			
				parametersDesignMap = generalUtilDesignData.getParametersDataMap();


			
			
			//Declaration
			boolean isStepsAlert = false;
			boolean isImpuritiesAlert = false;
			String stepsAlert = "";
			String impuritiesAlert = "";
			boolean isParametersAlert = false;
			String parametersAlert = "";
			StringBuilder sb = new StringBuilder();

		   
			
			
			
			//current title from the table
			String currentColumnTitile = elementValueMap.get("currentColumnTitile");
			if (currentColumnTitile != null && !currentColumnTitile.isEmpty()) {

				JSONObject jDictionary = springNameDictionary.getDictionaryJson();
				List<String> showColumnList = new ArrayList<>();
				ArrayList<String> currentColumnTitileList = new ArrayList<String>(
						Arrays.asList(currentColumnTitile.split("@", -1)));
				
				
				//
				List<String> colDesignExpList = Arrays.asList(expDesignCsv.toString().split("\\s*,\\s*")); 
				List<String> colDesignListExpressions = new ArrayList<>();

		        //make colDesignListExpressions according to step and springNameDictionary-for
		        //experiment Data
				for (String colDesign : colDesignExpList) {
					if (jDictionary.has(colDesign.toUpperCase())) {
						String colDesignExpression = (String) jDictionary.get(colDesign.toUpperCase());
						colDesignListExpressions.add(colDesignExpression);
					} else {

						generalUtilLogger.logWriter(LevelType.INFO, colDesign + " not exists in form dictionary",
								ActivitylogType.DataTable, formId, userId);
					}
				}
				
		          //make colDesignListExpressions according to step and springNameDictionary-for
		         //impurities Data
				List<String> ImpuritiesNames = new ArrayList<String>();
				List<String> colDesignImpuritiesNamesExpressions = new ArrayList<String>();
				List<String> colDesignParametersExpressions = new ArrayList<String>();
				for (Map.Entry<String, List<String>> step : impuritiesDesignStepMap.entrySet()) {
					if (!step.getValue().contains("")) {
						ImpuritiesNames = generalDao.getListOfStringBySql(
								"select t.INVITEMMATERIALNAME from FG_S_INVITEMMATERIAL_ALL_V t where t.INVITEMMATERIAL_ID in ("
										+ generalUtil.listToCsv(step.getValue()) + ")");
					}
					for (String impName : ImpuritiesNames) {

						colDesignListExpressions.add("Step " + step.getKey().replace(" ", "") + "Impurity " + impName+",%");
						colDesignImpuritiesNamesExpressions.add("Step " + step.getKey().replace(" ", "") + "Impurity " + impName+",%");

					}

				}
				//colDesignListExpressions.addAll(colDesignImpuritiesNamesExpressions);

		         //make colDesignListExpressions according to ffstep and springNameDictionary-for
		        //parameters Data
				// get active parameters with Organic_Step_Parameters mp_type name
				List<String> activeParamNames = new ArrayList<String>();
				String organicStepParametersId = generalUtilForm.getCurrrentINameSingleStringInfo("MPType",
						"Organic_Step_Parameters", "ID");
				List<Map<String, String>> data = generalUtilForm.getCurrrentNameInfoAllContainsId("MP", "%");
				for (Map<String, String> map : data) {
					String mpTypeIdList = map.get("MPTYPE_ID");
					if (!generalUtil.getNull(mpTypeIdList).equals("")
							&& ("," + mpTypeIdList + ",").contains("," + organicStepParametersId + ",")) {
						activeParamNames.add(map.get("NAME"));
					}
				}

				for (Map.Entry<String, List<String>> parameterKey : parametersDesignMap.entrySet()) {
					if (!parameterKey.getKey().contains("Exp")) {
						if(!parameterKey.getKey().contains("All steps"))
						{
						for (String parameter : parameterKey.getValue()) {
							if (activeParamNames.contains(parameter)) {
								String colDesignExpression = "Step " + parameterKey.getKey().replace(" ", "") + parameter+"Parameter";
								colDesignListExpressions.add(colDesignExpression);
								colDesignListExpressions.add("Step " + parameterKey.getKey().replace(" ", "") + parameter+"Sign");
								colDesignListExpressions.add("Step " + parameterKey.getKey().replace(" ", "") + parameter+"Value 1");
								colDesignListExpressions.add("Step " + parameterKey.getKey().replace(" ", "") + parameter+"Value 2");
								colDesignListExpressions.add("Step " + parameterKey.getKey().replace(" ", "") + parameter+"Uom");
								colDesignParametersExpressions.add("Step " + parameterKey.getKey().replace(" ", "") + parameter+"Parameter");
							}
						}
						}
						else
						{
							for (String parameter : parameterKey.getValue()) {
								if (activeParamNames.contains(parameter)) {
									String colDesignExpression = "Step .*" + parameter+"Parameter";
									colDesignListExpressions.add(colDesignExpression);
									colDesignListExpressions.add("Step .*"+ parameter+"Sign");
									colDesignListExpressions.add("Step .*" + parameter+"Value 1");
									colDesignListExpressions.add("Step .*"+ parameter+"Value 2");
									colDesignListExpressions.add("Step .*"+ parameter+"Uom");
									colDesignParametersExpressions.add("Step .*" + parameter);
								}
						}
						}
					} else {
						for (String parameter : parameterKey.getValue()) {
							if (activeParamNames.contains(parameter)) {
								String colDesignExpression = parameter+"Parameter";
								colDesignListExpressions.add(colDesignExpression);
								colDesignListExpressions.add(parameter+"Sign");
								colDesignListExpressions.add(parameter+"Value 1");
								colDesignListExpressions.add(parameter+"Value 2");
								colDesignListExpressions.add(parameter+"Uom");
								colDesignParametersExpressions.add(colDesignExpression);
							}

						}
					}

				}


				//make colDesignListExpressions according to step and springNameDictionary-for
				//steps Data

				for (Map.Entry<String, List<String>> step : stepsDesignMap.entrySet()) {
					boolean isReactant = false;
					boolean isSolvent = false;
					if (!step.getValue().contains("")||impuritiesDesignStepMap.containsKey(step.getKey()))
						colDesignListExpressions.add("Step" + step.getKey());

					for (String colDesign : step.getValue()) {
						if (jDictionary.has(colDesign.toUpperCase())) {
							String colDesignExpression = (String) jDictionary.get(colDesign.toUpperCase());
							colDesignExpression = colDesignExpression.replace("<STEP_NUMBER>",
									step.getKey().replace(" ", "")); // according
							if (colDesignExpression.contains("Reactant")) // to
								isReactant = true;
							if (colDesignExpression.contains("Slovent")) // to
								isSolvent = true; // step
		                     
							colDesignListExpressions.add(colDesignExpression);

						} else {
							generalUtilLogger.logWriter(LevelType.INFO,
									colDesign.toUpperCase() + " not exists in form dictionary", ActivitylogType.DataTable,
									formId, userId);
						}
						if (colDesign.equals("chkImpuritiesStep")) {
							colDesignListExpressions.add("Step " + step.getKey().replace(" ", "") + "Impurity .*");
							colDesignImpuritiesNamesExpressions
									.add("Step " + step.getKey().replace(" ", "") + "Impurity .*");
						}
						if (colDesign.contains("numConcentrationImpStep")) {
							if (!maxImpuritiesMap.isEmpty()) {
								if(maxImpuritiesMap.containsKey(step.getKey().replace(" ", ""))){
									String[] s = maxImpuritiesMap.get(step.getKey().replace(" ", "")).split(";");
									List<String> imp = Arrays.asList(s);
									for (String impurity : imp) {
										if (Integer.parseInt(impurity.split(",")[0]) <= Integer
												.parseInt(colDesign.split(";")[1])) {
											colDesignListExpressions.add("Step " + step.getKey().replace(" ", "") + "Impurity "
													+ impurity.split(",")[1]+",%");
											colDesignImpuritiesNamesExpressions.add("Step " + step.getKey().replace(" ", "")
													+ "Impurity " + impurity.split(",")[1]+",%");
										}
									}
								}
							}

						}

					}
					//add reactant and solvent X column
					if (isReactant) {

						colDesignListExpressions.add("Reactant Step" + step.getKey() + "-Reactant .*");
					}
					if (isSolvent)
						colDesignListExpressions.add("Slovent Step" + step.getKey() + "-Solvent .*");

				}


				//add to showColumnList the columns that need to be display from the
				//currentColumnTitileList using colDesignListExpressions

				for (String colTitle : currentColumnTitileList) {
					boolean isMatch = false;// check colTitle exists in the design
					//adib 02072020- I added the if clause as a temporary solution for columns that always should be displayed until adding them in the ui of the design
					if(getConstantColumns(colTitle)){
						showColumnList.add(colTitle);
						isMatch = true;
					} else {
						for (String colDesignExpression : colDesignListExpressions) {
							try {
	
								if (colTitle.equals(colDesignExpression)||colTitle.matches(colDesignExpression)) {
									showColumnList.add(colTitle);
									isMatch = true;
									break;
								}
							} catch (Exception e) {
								if (colTitle.equals(colDesignExpression)) {
									showColumnList.add(colTitle);
									isMatch = true;
									break;
								}
								// TODO: handle exception
							}
	
						}
					}
					if (!isMatch) {

						//TODO build on step X column if this column change or remove we need to change this code

						if (colTitle.toLowerCase().contains("step")) {
							String stepColTitle = colTitle.substring(0, 7);

							try {

								if (stepColTitle.equals(colTitle)) {
									 //build  steps Alert if there is step not exists in design but there is in report
									stepsAlert = stepsAlert + " " + colTitle.replace("Step", "") + ",";
									isStepsAlert = true;
									/*
									 * messagesMap=generalUtil.addToList(colTitle,"STEPS_ALERT", messagesMap);
									 * List<String> messagesList = new ArrayList<String>();
									 * messagesList.add("STEPS_ALERT"); messagesMap.put(colTitle, messagesList);
									 */

								}
							} catch (Exception e) {

								//TODO: handle exception
							}

						}
					}
				}

				for (String impurityCol : colDesignImpuritiesNamesExpressions) {
					boolean isMatch = false;// check colTitle exists in the design
					for (String colTitle : currentColumnTitileList) {
						try {

							if (colTitle.equals(impurityCol)||colTitle.matches(impurityCol)) {
								isMatch = true;
							}
						} catch (Exception e) {
							if (colTitle.equals(impurityCol)) {
								isMatch = true;
							}
							// TODO: handle exception
						}

					}
					if (!isMatch) {

						//TODO build on step X column if this column change or remove we need to change this code
						 //build  impurity Alert if there is impurity not exists in report but there is in design
						if (impurityCol.contains(".*")) {
							impurityCol = impurityCol.replace(".*", "All Impurities");
						}
						String stepColTitle = impurityCol.substring(0, 7);
						String impurityName = impurityCol.substring(16, impurityCol.length());
						impuritiesAlert = impuritiesAlert + " " + stepColTitle + ":" + impurityName + ',';
						isImpuritiesAlert = true;

					}

				}
				for (String parameterCol : colDesignParametersExpressions) {
					boolean isMatch = false;// check colTitle exists in the design
					for (String colTitle : currentColumnTitileList) {
						try {
							
						
						if (colTitle.matches(parameterCol)) {
							isMatch = true;
							if (colTitle.contains("Step")) {
								if (!colDesignListExpressions.contains(colTitle.substring(0, 7))) {
									colDesignListExpressions.add(colTitle.substring(0, 7));
									showColumnList.add(colTitle.substring(0, 7));
								}
							}
						}
						} catch (Exception e) {
							// TODO: handle exception
						}

					}
					if (!isMatch) {

						//TODO build on step X column if this column change or remove we need to change this code
		                //build  parmeters Alert if there is parameters not exists in report but there is in design
						String stepColTitle = "";
						String parametereName = "";
						if (parameterCol.contains("Step")) {
							 if (parameterCol.contains("Step .*")) {
									stepColTitle = "All steps";
									parametereName = parameterCol.replace("Step .*", "");
							}
							 else{stepColTitle = parameterCol.substring(0, 7);
							parametereName = parameterCol.substring(7, parameterCol.length());}
							
						}
					     else {
							stepColTitle = "Experiment";
							parametereName = parameterCol;
						}
						parametersAlert = parametersAlert + " " + stepColTitle + ":" + parametereName.replace("Parameter", "") + ',';
						isParametersAlert = true;

					}

				}


				//update messages in list
				if (isParametersAlert) {

					messages.append("An inconsistency was found between the Report design and  the Actual Report data"
							+ generalUtil.replaceLast(parametersAlert, ",", ""));
					messages.append('@');
				}
				
				if (isImpuritiesAlert) {

					messages.append("An inconsistency was found between the Report design and  the Actual Report data"
							+ generalUtil.replaceLast(impuritiesAlert, ",", ""));
					messages.append('@');
				}


				if (isStepsAlert) {

					messages.append(
							"There are no designs for the following steps:" + generalUtil.replaceLast(stepsAlert, ",", ""));
					messages.append('@');
				}
				
				//if at least one experiment characteristic sample has duplicated results without “Main” selection, an error message should display
				String sql = "select distinct sample_id from( " + 
						" select count(*) over (partition by sample_id,invitemmaterial_id,result_name) as count_,sample_id from (\r\n" + 
						" select nvl2(sr.RESULT_ID,1,0) as selected_res,t.* from fg_i_sampleresults_v t, fg_i_selectedresults_v sr, fg_s_experiment_v ex,"
						+ " fg_s_step_v st "
						+ " where (t.sample_id = ex.CHARACTERIZEDSAMPLE or t.SAMPLE_ID = st.CHARACTERIZEDSAMPLE) and t.RESULT_ID = sr.RESULT_ID(+) and ex.experiment_id in ("+elementValueMap.get("experimentList")+") " + 
						" and ex.experiment_id = st.EXPERIMENT_ID(+)) " + 
						" where selected_res = 0) " + 
						" where count_>1";
				//fixed bug 8098
				/*String sql = "select distinct CHARACTERIZEDSAMPLE from( " + 
						"select distinct ex.CHARACTERIZEDSAMPLE from fg_i_resusingtoupdate_mv mv,fg_s_experiment_v ex where mv.experiment_id in ("+selectedExpCsv+") and ex.experiment_id = mv.experiment_id and mv.sampleid=ex.CHARACTERIZEDSAMPLE " + 
						"union all " + 
						"select distinct s.CHARACTERIZEDSAMPLE from fg_i_resusingtoupdate_mv mv,fg_s_experiment_v ex,fg_s_step_v s where mv.experiment_id in ("+selectedExpCsv+") and ex.experiment_id = mv.experiment_id and ex.experiment_id = s.experiment_id and mv.sampleid=s.CHARACTERIZEDSAMPLE)";*/
				StringBuilder expDuplicatResMsg = null;
				if(!generalUtil.getNull(elementValueMap.get("experimentList")).isEmpty()){
					List<String> samplesId = generalDao.getListOfStringBySql(sql);
					for (String sampleId : samplesId) {
						String sampleName = formDao.getFromInfoLookup("Sample", LookupType.ID, sampleId, "name");
						if (expDuplicatResMsg == null) {
							expDuplicatResMsg = new StringBuilder();
							expDuplicatResMsg.append("<a href='#' onClick=\"checkAndNavigate(['" + sampleId + "','Sample'])\" >" + sampleName
									+ "</a>");
						} else {
							expDuplicatResMsg.append("<a href='#' onClick=\"checkAndNavigate(['" + sampleId + "','Sample'])\" >" + ", "
									+ sampleName + "</a>");
						}
					}
					
					if(expDuplicatResMsg!=null&&!expDuplicatResMsg.toString().isEmpty()){
						messages.append(generalUtil.getSpringMessagesByKey("EXP_ANALYSIS_MAIN_RES", "")+" "+expDuplicatResMsg);
						messages.append('@');
					}
					
				}
				
				//displays a message of no characteristic sample has been chosen for the steps that defined in the design with impurities columns
				if(!stepDisplayImpurities.isEmpty()||true){//TODO:after the impurities block is added to the design, the true operand should be replaced with isImpuritiesDefinedInExperiment
					List<String> stepList = null;
					if(!stepDisplayImpurities.isEmpty()){
						stepList = generalDao.getListOfStringBySql("select step_id from fg_s_step_v\n"
								+ " where experiment_id in ("+elementValueMap.get("experimentList")+")\n"
								+ " and formnumberid in ("+generalUtil.listToCsv(stepDisplayImpurities)+")\n");
					}
					messages.append(getNoCharasteristicSampleMessage(elementValueMap.get("experimentList"),generalUtil.listToCsv(stepList)));
				}


				sb.append("messages");
				sb.append(messages);
				sb.append("@@@");
				sb.append("hiddeenList");

				//remove from currentColumnTitileList the columns that should be hidden
				for (String showColTitle : showColumnList) {
					currentColumnTitileList.remove(showColTitle);
				}

				//build the update string with the columns that should be hidden
				for (String colTitle : currentColumnTitileList) {
					sb.append(colTitle);
					sb.append("@");
				}

			}

			String toReturn = sb.toString();


			//return the update hidden list
			return generalUtil.replaceLast(toReturn, "@", "");

		}


private String getExperimentNoCharSampleDefined(String experimentList) {
	String sql = "select distinct listagg('<a href=\"#\" onClick=\"checkAndNavigate(['''|| t.experiment_id||''','''||t.formcode||'''])\" >'||t.experimentname||'</a>',',') within group (order by t.experimentname)\n"
			+ " from fg_s_experiment_v t,\n"
			+ " fg_s_sampleselect_v s\n"
			+ " where t.experiment_id in ("+experimentList+")\n"
			+ " and s.parentid = t.experiment_id\n"
			+ " and s.sessionid is null\n"
			+ " and s.active = 1\n"
			+ " and CHARACTERIZEDSAMPLE is null";
	return generalDao.selectSingleStringNoException(sql);
}

private boolean getConstantColumns(String colTitle) {
	List<String> constantColumns = Arrays.asList("Characteristic Sample","Analysis Source","Analytical Experiment","Assay %");
	try{
		if(constantColumns.contains(colTitle)){
			return true;
		}else{
			if(colTitle.matches("Impurity .*")){
				return true;
			}
		}
	}catch(Exception e){
		
	}
	return false;
}

public void preperReport(Map<String, String> elementValueMap) {
	//save elementValueMap in session
			generalUtilDesignData.setDesignFormElementValueMap(elementValueMap);
//save parameters in session
		generalUtilDesignData.setParametersDataMap(
				generalUtil.jsonArrayStringToMapWithList(elementValueMap.get("parametersDesign")));
//save experiment data in session
		generalUtilDesignData.getExperimentDataList().clear();
		generalUtilDesignData.getAdditionalInfo().clear();
		for (Map.Entry<String, String> m : elementValueMap.entrySet()) {
			if (!m.getKey().toLowerCase().contains("step") && m.getValue().equals("1")) {

				generalUtilDesignData.addExperimentDataToList(m.getKey());

			} else if (m.getValue() == null || (!m.getValue().equals("0") && !m.getValue().equals("1")
					&& !m.getKey().equals("parametersDesign") && !m.getKey().equals("stepsDesign"))) {
				generalUtilDesignData.addAdditionalInfo(m.getKey(), m.getValue());
			}

		}

//set last step data in session (not update onstep changing
		List<String> designStepList = new ArrayList<String>();
		for (Map.Entry<String, String> m : elementValueMap.entrySet()) {
			if (m.getKey().toLowerCase().contains("step") && m.getValue().equals("1")
					&& !m.getKey().equals("numConcentrationImpStep")) {

				designStepList.add(m.getKey());
			}
			if (m.getKey().equals("numConcentrationImpStep") && !m.getValue().equals("")) {
				designStepList.add(m.getKey() + ";" + m.getValue());
			}
		}

// update stepsList
		generalUtilDesignData.addStepListDesignToMap(designStepList, elementValueMap.get("STEP_ID"));

		// update impuritiesList
		generalUtilDesignData.clearImpuritiesDesignForStep(elementValueMap.get("STEP_ID"));
		if (!elementValueMap.get("stepImpuritiesIdList").equals("")
				&& elementValueMap.get("stepImpuritiesIdList") != null) {
			generalUtilDesignData.addImpuritiesListToMap(
					Arrays.asList(elementValueMap.get("stepImpuritiesIdList").split("\\s*,\\s*")),
					elementValueMap.get("STEP_ID"));
		}
		generalUtilDesignData.getAdditionalInfo().put("usingSteps", generalUtilDesignData.getAdditionalInfo().get("usingSteps")+','+ elementValueMap.get("STEP_ID"));
	}





	public void fillDesignDataSession(String designId) {
//Collect data from design form 
		generalUtilDesignData.clearSession();
//expDesignCsv -experiment data-;TODO change this code to query
		String sql = "select *  from Fg_s_Reportdesignexp_All_v t where t.formId='" + designId + "'";
		Map<String, String> expDesignMap = new HashMap<String, String>();
		expDesignMap = generalDao.sqlToHashMap(sql);
		StringBuilder expDesignCsv = new StringBuilder();

		for (Map.Entry<String, String> m : expDesignMap.entrySet()) {
			if (!(m.getKey().toLowerCase().contains("step")) && (m.getValue().equals("1"))) {
				generalUtilDesignData.addExperimentDataToList(m.getKey());
			} else if (m.getValue() == null || !m.getValue().equals("0") && !m.getValue().equals("1")
					&& !m.getKey().equals("parametersDesign") && !m.getKey().equals("stepsDesign")) {
				generalUtilDesignData.addAdditionalInfo(m.getKey(), m.getValue());
			}
		}
		generalUtilDesignData.getAdditionalInfo();
		generalUtilDesignData.getExperimentDataList();

//stepsDesignMap-steps data
		sql = "select STEPSDESIGN from Fg_s_Reportdesignexp_All_v t where t.formId=" + designId;
		String stepDesignId = generalDao.selectSingleString(sql);
//Map<String, List<String>> stepsDesignMap = new HashMap<String, List<String>>();
		generalUtilDesignData.setStepDesignMapwithList(generalUtil.jsonStringToMapList(generalDao.selectSingleString(
				"select t.file_content from fg_clob_files t where t.file_id = '" + stepDesignId + "'")));

		generalUtilDesignData.getStepDesignMapwithList();

//impuritiesDesignMap-impurities data
		sql = "select IMPURITIESSTEPSDESIGN from Fg_s_Reportdesignexp_All_v t where t.formId=" + designId;
		String impuritiesDesignSteps = generalDao.selectSingleString(sql);
//Map<String, List<String>> impuritiesDesignStepMap = new HashMap<String, List<String>>();
		if (impuritiesDesignSteps != null)
			generalUtilDesignData.setStepImpuritesDesignMap(generalUtil.jsonStringToMapList(impuritiesDesignSteps));

		generalUtilDesignData.getStepImpuritesDesignMap();
//impuritiesConcentrationDesignMap-impurities Concentration data
		/*
		 * Map<String, String> maxImpuritiesMap = new HashMap<String, String>();
		 * List<String> maxImpuritiesList =
		 * getImpuritiesByMaxConcentration(elementValueMap.get("experimentList")); for
		 * (String impurity : maxImpuritiesList) { String imp =
		 * maxImpuritiesMap.get(impurity.split(":")[0]); if (imp == null)
		 * maxImpuritiesMap.put(impurity.split(":")[0], impurity.split(":")[1]); else
		 * maxImpuritiesMap.put(impurity.split(":")[0],
		 * maxImpuritiesMap.get(impurity.split(":")[0]) + ";" + impurity.split(":")[1]);
		 * 
		 * }
		 */
//parametersDesignMap-parameters data
		sql = "select PARAMETERSDESIGN from Fg_s_Reportdesignexp_All_v t where t.formId=" + designId;

		String parametersDesignId = generalDao.selectSingleString(sql);
//Map<String, List<String>> parametersDesignMap = new HashMap<String, List<String>>();
		String parameterDesignsString = "";
		if (parametersDesignId != null)
			parameterDesignsString = generalDao.selectSingleString(
					"select t.file_content from fg_clob_files t where t.file_id = '" + parametersDesignId + "'");
		if (!parameterDesignsString.equals("{}") && !parameterDesignsString.isEmpty())
			generalUtilDesignData
					.setParametersDataMap(generalUtil.jsonArrayStringToMapWithList(parameterDesignsString));
		generalUtilDesignData.getParametersDataMap();

	}
	
	private StringBuilder getNoCharasteristicSampleMessage(String experimentList,String stepList){
		String sql = "select distinct 'Experiment '||t.experimentname||' '||listagg('"
				+ "<a href=\"#\" onClick=\"checkAndNavigate(['||t.step_id||','''||t.formcode||''',''Samples''])\" > Step'||t.formnumberid||'</a>',','"
				+ ") within group (order by t.step_id)over(partition by t.experiment_id)\n"
				+ " from "
					+ "(select distinct t.step_id,t.experiment_id,e.experimentname,t.FORMNUMBERID,t.formcode\n"
					+ " from fg_s_step_v t,\n"
					+ " fg_s_experiment_v e,\n"
					+ " fg_s_stepstatus_v st,\n"
					+ " fg_s_sampleselect_v s\n"
					+ " where t.CHARACTERIZEDSAMPLE is null\n"
					+ " and t.experiment_id = e.experiment_id\n"
					+ " and t.status_id = st.stepstatus_id(+)\n"
					+ " and "+(stepList==null || stepList.isEmpty()?"t.experiment_id in ("+(experimentList)+")": "t.step_id in ("+(stepList)+")")+"\n"
					+ " and st.stepstatusname not in ('Planned', 'Cancelled')\n"
					+ " and s.parentid = t.step_id\n"
					+ " and s.sessionid is null and s.active = '1'\n"
					+ " order by t.experiment_id) t";
		List<String> messagePerExp = generalDao.getListOfStringBySql(sql);
		
		String expMsg = getExperimentNoCharSampleDefined(experimentList);
		
		StringBuilder sb = new StringBuilder();
		if(!messagePerExp.isEmpty()||!generalUtil.getNull(expMsg).isEmpty()){
			sb.append("Please Note: The 'Characteristic sample'  was not selected for the following experiments & steps:");
		}

		if(!generalUtil.getNull(expMsg).isEmpty()){
			sb.append("</br>Experiments: "+expMsg);
		}
		for(String msg:messagePerExp){
			sb.append("</br>"+msg);
		}
		return sb;
	}

}

