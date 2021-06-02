package com.skyline.form.dal;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilLogger;

import chemaxon.formats.MolExporter;
import chemaxon.formats.MolImporter;
import chemaxon.jchem.db.JChemSearch;
import chemaxon.jchem.db.UpdateHandler;
import chemaxon.sss.SearchConstants;
import chemaxon.sss.search.JChemSearchOptions;
import chemaxon.struc.Molecule;
import chemaxon.util.ConnectionHandler;

@Repository("ChemDao")
public class ChemDaoMarvinImp implements ChemDao {

	@Autowired
	public GeneralUtilLogger generalUtilLogger;

	private static final Logger logger = LoggerFactory.getLogger(ChemDaoMarvinImp.class);
	@Value("${jdbc.driverClassName}")
	private String driverClassName;
	@Value("${jdbc.username}")
	private String username;
	@Value("${jdbc.url}")
	private String url;
	@Value("${jdbc.password}")
	private String password;
	//	@Value("${chemmarvin.imgprop}")
	//    private String imgprop = "png:w500,b32,#ffffff";
	@Value("${chem.imgpropreact:png:w900,h250,b32,#ffffff}")
	private String imgprop;// = "png:w2000,h250,b32,#ffffff";
	//	   String url = "jdbc:oracle:thin:@localhost:1521:javaDemo";

	@Value("${imagePath:c:/Logs}")
	private String imagePath;

	@Autowired
	private GeneralDao generalDao;

	@Autowired
	private FormSaveDao formSaveDao;

	@Autowired
	private GeneralUtil generalUtil;

	//	@Autowired
	//    private ChemService chemSerive;

	@Autowired
	private UploadFileDao uploadFileDao;

//	@Autowired
//	private IntegrationValidation integrationValidation;

	@Value("${jdbc.username}")
	private String DB_USER;

	private ConnectionHandler connHandler;

	private ConnectionHandler connHandlerDoc;

	private ConnectionHandler connHandlerDeleted;

	@Value("${chem.isSaveJChem:1}")
	private int isSaveJChem;

	@Value("${chem.isSearchSubStructure:1}")
	private int isSearchSubStructure;

	private final String chemSearchType = "smiles";

	@Value("${chem.saveChemFormCodeList:InvItemMaterial}")
	private String saveChemFormCodeList;

	public ChemDaoMarvinImp() {

	}

	@PostConstruct
	public void setConnectionHandlerAll() {
		setConnectionHandler();
		setConnectionHandlerDoc();
		setConnectionHandlerDeleted();
	}

	public void setConnectionHandler() {
		// TODO Auto-generated method stub
		connHandler = new ConnectionHandler();
		connHandler.setUrl(url);
		connHandler.setDriver(driverClassName);
		connHandler.setPropertyTable("JCHEMPROPERTIES");
		connHandler.setLoginName(username);
		connHandler.setPassword(password);
		try {
			connHandler.connectToDatabase();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			connHandler = null;
			generalUtilLogger.logWrite(LevelType.ERROR, "Exception in init jchem connHandler ", "-1",
					ActivitylogType.ChemMolSearchTask, null, e1);
		}
	}

	public void setConnectionHandlerDoc() {
		// TODO Auto-generated method stub
		connHandlerDoc = new ConnectionHandler();
		connHandlerDoc.setUrl(url);
		connHandlerDoc.setDriver(driverClassName);
		connHandlerDoc.setPropertyTable("JCHEMPROPERTIES_DOC");
		connHandlerDoc.setLoginName(username);
		connHandlerDoc.setPassword(password);
		try {
			connHandlerDoc.connectToDatabase();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			connHandlerDoc = null;
			generalUtilLogger.logWrite(LevelType.ERROR, "Exception in init jchem connHandlerDoc ", "-1",
					ActivitylogType.ChemMolSearchTask, null, e1);
		}
	}

	public void setConnectionHandlerDeleted() {
		// TODO Auto-generated method stub
		connHandlerDeleted = new ConnectionHandler();
		connHandlerDeleted.setUrl(url);
		connHandlerDeleted.setDriver(driverClassName);
		connHandlerDeleted.setPropertyTable("JCHEMPROPERTIES_DELETED");
		connHandlerDeleted.setLoginName(username);
		connHandlerDeleted.setPassword(password);
		try {
			connHandlerDeleted.connectToDatabase();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			connHandlerDeleted = null;
			generalUtilLogger.logWrite(LevelType.ERROR, "Exception in init jchem connHandlerDeleted ", "-1",
					ActivitylogType.ChemMolSearchTask, null, e1);
		}
	}

	@Override
	public String getNewChemImg(String fullData) {
		String toReturn = "";
		try {
			Document xml = convertStringToXml(fullData);
			String cml = convertXmlToString(xml);
			Molecule molecule = MolImporter.importMol(updateEmptyMarvin(cml));
			byte[] fullImg = (byte[]) MolExporter.exportToObject(molecule, imgprop);
			//        byte[] fullImg = (byte[])MolExporter.exportToObject(molecule, "png:w900,h250,b32,#ffffff");//1000/250
			String fullImgId = saveChemImage(fullImg, "MOLECULE_FULL_IMAGE.png", "image/png");
			toReturn = fullImgId;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return toReturn;
	}

	@Override
	public String saveChemData(String formCode, String formId, String elementID, String fullData, String formCodeFull,
			String isNew) throws ParserConfigurationException, SAXException, IOException, TransformerException,
			ClassNotFoundException, SQLException, Exception {
		int molOrderCounter = 1;

		// Get current ID
		//	        String currID = formSaveDao.getStructFormId(formCodeFull);
		Document xml = convertStringToXml(fullData);
		NodeList reaction = xml.getElementsByTagName("reaction");
		boolean isReaction = reaction.getLength() > 0;
		String cml = convertXmlToString(xml);
		Molecule molecule = MolImporter.importMol(updateEmptyMarvin(cml));
		byte[] fullImg = (byte[]) MolExporter.exportToObject(molecule, imgprop);
		//	        byte[] fullImg = (byte[])MolExporter.exportToObject(molecule, "png:w900,h250,b32,#ffffff");//1000/250
		String fullImgId = saveChemImage(fullImg, "MOLECULE_FULL_IMAGE.png", "image/png");
		//String dataId = formSaveDao.getStructFormId("MOL_DATA_ID");ab 30/08/18
		String dataId = formSaveDao.getStructFileId("MOL_DATA_ID");
		uploadFileDao.saveStringAsClob(dataId, fullData);
		String molType = "";

		if (isReaction) {
			HashMap<Integer, ArrayList<HashMap<String, Object>>> reactionMolecules = new HashMap<Integer, ArrayList<HashMap<String, Object>>>();

			// Reactants
			reactionMolecules.put(1, getReactionMolecules("reactantList", xml));
			// Agents
			reactionMolecules.put(2, getReactionMolecules("agentList", xml));
			// Products
			reactionMolecules.put(3, getReactionMolecules("productList", xml));

			for (Integer key : reactionMolecules.keySet()) {
				switch (key) {
					// reactants
					case 1:
						molType = "R";
						for (HashMap<String, Object> formats : reactionMolecules.get(key)) {
							saveMarvinMolecule(elementID, dataId, formats, molType, molOrderCounter++, fullImgId);
						}
						break;
					// agents
					case 2:
						//	                    molOrderCounter = 1;
						molType = "A";
						for (HashMap<String, Object> formats : reactionMolecules.get(key)) {
							saveMarvinMolecule(elementID, dataId, formats, molType, molOrderCounter++, fullImgId);
						}
						break;
					// products
					case 3:
						//	                    molOrderCounter = 1;
						molType = "P";
						for (HashMap<String, Object> formats : reactionMolecules.get(key)) {
							saveMarvinMolecule(elementID, dataId, formats, molType, molOrderCounter++, fullImgId);
						}
						break;

					default:
						molType = "";
						break;
				}
			}

		} else {
			// Single molecule
			//	            NodeList moleculeList = xml.getElementsByTagName("molecule");
			//	            cml = marvinService.convertXmlToString(xml); 
			HashMap<String, Object> formats = getMoleculeFormats(updateEmptyMarvin(cml));
			molType = "S";

			saveMarvinMolecule(elementID, dataId, formats, molType, molOrderCounter, fullImgId);
			//			if (isSaveJChem == 1 && !formId.equals("-1") && ("," + generalUtil.getNull(saveChemFormCodeList) + ",")
			//					.contains(generalUtil.getNull(formCode))) {
			//				saveJChem(elementID, formId, formCodeFull, molType, isNew, fullData, DB_USER + ".FG_CHEM_SEARCH");
			//			} // yp 28072019 this code is been called from the save event of invitemmaterial
		}

		return elementID;
	}

	@Override
	public JSONObject saveDocData(String fullData) throws ParserConfigurationException, SAXException, IOException,
			TransformerException, ClassNotFoundException, SQLException, Exception {

		JSONObject jo = new JSONObject();

		//image
		Molecule molecule = MolImporter.importMol(fullData);
		byte[] fullImg = (byte[]) MolExporter.exportToObject(molecule, "png:w500,h300,b32,#ffffff,marginSize0");//imgprop
		String fullImgId = saveChemImage(fullImg, "MOLECULE_FULL_IMAGE.png", "image/png");
		jo.put("imgId", fullImgId);

		//chem
		String chemId = saveJChemDoc(fullData, DB_USER + ".FG_CHEM_DOC_SEARCH");
		jo.put("chemId", chemId);
		return jo;
	}

	private String saveJChemDoc(String fullData, String tableName) throws Exception {
		String toReturn = "";
		if (connHandlerDoc == null || !connHandlerDoc.isConnected()) {
			setConnectionHandlerDoc();
		}

		int updateMode = UpdateHandler.INSERT;
		UpdateHandler uh = new UpdateHandler();
		try {
			uh = new UpdateHandler(connHandlerDoc, updateMode, tableName, null);
			uh.setStructure(fullData);
			int cd_id = uh.execute(true); // True is parameter for return cd_id. In case duplication .execute returns id of exists structure with minus, e.g. -192
			toReturn = String.valueOf(cd_id);
		} finally {
			uh.close();
		}

		return toReturn;
	}

	private void saveMarvinMolecule(String elementID, String dataId, HashMap<String, Object> formats, String molType,
			int molOrder, String fullImgId) throws Exception {
		PreparedStatement prStmt = null;
		Connection con = null;
		String sql = "";
		Map<String, String> marvinInfo = new HashMap<String, String>();

		String molImgId = "";
		String molDataId = "";
		String dataIdMolInReacrion = "";

		try {
			//			Class.forName(driverClassName);

			//			con = DriverManager.getConnection(url, username, password);
			con = generalDao.getConnectionFromDataSurce();
			molDataId = uploadFileDao.saveStringAsClobRenderId("chem_mol_data_matrix", ((String) formats.get("mol")));
			if (!molType.equals("S")) { // not single 
				//dataIdMolInReacrion = formSaveDao.getStructFormId("MOL_IN_REACTION_DATA_ID");ab 30/08/18
				dataIdMolInReacrion = formSaveDao.getStructFileId("MOL_IN_REACTION_DATA_ID");
				uploadFileDao.saveStringAsClob(dataIdMolInReacrion, "<cml><MDocument><MChemicalStruct>"
						+ ((String) formats.get("cml")) + "</MChemicalStruct></MDocument></cml>");
				molImgId = saveChemImage((byte[]) formats.get("img"), "MOLECULE_IMAGE.png", "image/png");
			}
			sql = "insert into fg_chem_doodle_data (parent_id, reaction_all_data, mol_data, smiles_data, inchi_data, mol_attr, mol_type, mol_order, MOL_IMG_FILE_ID, FULL_IMG_FILE_ID,REACTION_ALL_DATA_LINK,MOL_CML) "
					+ " values(?,?,?,?,?,?,?,?,?,?,?,?)";

			prStmt = con.prepareStatement(sql);
			marvinInfo.put("(1) elementID = parent_id ", elementID);
			prStmt.setString(1, elementID);
			marvinInfo.put(
					"(2) fullData = reaction_all_data - not in use TODO move old data to clob (we support it until we will move the old data)",
					"");
			prStmt.setString(2, "");
			marvinInfo.put("(3) mol_data = mol_data_matrix for a single molecule in case of reaction", molDataId);
			prStmt.setString(3, molDataId);
			marvinInfo.put("(4) smiles_data = smiles for a single molecule in case of reaction",
					(String) formats.get("smiles"));
			prStmt.setString(4, (String) formats.get("smiles"));
			marvinInfo.put("(5) inchi_data = inchi for a single molecule in case of reaction",
					(String) formats.get("inchi"));
			prStmt.setString(5, (String) formats.get("inchi"));
			marvinInfo.put("(6) mol_attr for a single molecule in case of reaction",
					(String) formats.get("attributes"));
			prStmt.setString(6, (String) formats.get("attributes"));
			marvinInfo.put("(7) mol_type = S single / R - reactant in reaction / P -product in reaction", molType);
			prStmt.setString(7, molType);
			marvinInfo.put("(8) mol_order the order inside the reaction (1 in single)", String.valueOf(molImgId));
			prStmt.setInt(8, molOrder);
			marvinInfo.put("(9) MOL_IMG_FILE_ID for a single molecule in case of reaction - empty in single nolecule",
					String.valueOf(molOrder));
			prStmt.setString(9, molImgId);
			marvinInfo.put("(10) FULL_IMG_FILE_ID img of the canvs id", String.valueOf(fullImgId));
			prStmt.setString(10, fullImgId);
			marvinInfo.put("(11) reactionDataId = refer to clob file with cml of the canvas", dataId);
			prStmt.setString(11, dataId);
			marvinInfo.put("(12) MOL_CML = refer to clob file with cml of this molecule in the reaction",
					dataIdMolInReacrion);
			prStmt.setString(12, dataIdMolInReacrion);
			prStmt.execute();
			//			prStmt.close();
			generalUtilLogger.logWrite(LevelType.DEBUG, "saved chem", "", ActivitylogType.ChemMol, marvinInfo);
		} catch (Exception e) {
			generalUtilLogger.logWrite(LevelType.ERROR, "saved chem failure", "", ActivitylogType.ChemMol, marvinInfo,
					e);
			throw new Exception(generalUtil.getSpringMessagesByKey("FAILED_SAVE_CHEM",
					"Save molecule failure. Please, try again or call your administrator."));
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
				generalUtilLogger.logWrite(LevelType.ERROR, "Exception in release recources ", "-1",
						ActivitylogType.ChemMol, null, e);
			}
		}
	}

	@Override
	public void saveJChem(String formId, String elementId) throws Exception {
		if (isSaveJChem == 1) {
			String tableName = DB_USER + ".FG_CHEM_SEARCH";
			String molType = "S";
			String fullFormCode = "InvItemMaterial.structure";
			String fullData = getMRVContent(elementId);

			if (connHandler == null || !connHandler.isConnected()) {
				setConnectionHandler();
			}

			removeRowJChem(formId);
  
			if (!isEmptyCanvas(fullData)) {
				int updateMode = UpdateHandler.INSERT;
				UpdateHandler uh = new UpdateHandler();
				try {
					uh = new UpdateHandler(connHandler, updateMode, tableName, "formId,fullFormCode,molType,elementId");

					uh.setStructure(fullData);
					uh.setValueForAdditionalColumn(1, formId);
					uh.setValueForAdditionalColumn(2, fullFormCode);
					uh.setValueForAdditionalColumn(3, molType);
					uh.setValueForAdditionalColumn(4, elementId);
					int cd_id = uh.execute(true); // True is parameter for return cd_id. In case duplication .execute returns id of exists structure with minus, e.g. -192.
					if (cd_id < 0) { 
						checkDuplication(String.valueOf(Math.abs(cd_id)));
					}
				}
				finally {
					uh.close();
				}
			} else if (isEmptyCanvas(fullData)) {
				return;
			}
		}
	}
	
	@Override
	public void saveJChemDeleted(String formId, String elementId) throws Exception {
		if (isSaveJChem == 1) {
			String tableName = "FG_CHEM_DELETED_SEARCH";
			String molType = "S";
			String fullFormCode = "InvItemMaterial.structure";
			String fullData = getMRVContent(elementId);
			
			removeRowJChem(formId);
	
			if (!isEmptyCanvas(fullData)) {
				int updateMode = UpdateHandler.INSERT;
				UpdateHandler uh = new UpdateHandler();
				try {
					uh = new UpdateHandler(connHandlerDeleted, updateMode, tableName,
							"formId,fullFormCode,molType,elementId");
					uh.setStructure(fullData);
					uh.setValueForAdditionalColumn(1, formId);
					uh.setValueForAdditionalColumn(2, fullFormCode);
					uh.setValueForAdditionalColumn(3, molType);
					uh.setValueForAdditionalColumn(4, elementId);
					uh.execute(true); // True is parameter for return cd_id. In case duplication .execute returns id of exists structure with minus, e.g. -192
				} finally {
					uh.close();
				}
			}
		}
	}
	
	private void removeRowJChem(String formId) throws Exception { 
		String fullFormCode = "InvItemMaterial.structure";
		
		//FG_CHEM_SEARCH
		String isExists = generalDao.selectSingleStringNoException(
				"select distinct 1 from FG_CHEM_SEARCH where formId = '" + formId + "'");
		
		if (generalUtil.getNull(isExists).equals("1")) {
			
			if (connHandler == null || !connHandler.isConnected()) {
				setConnectionHandler();
			}
			
			UpdateHandler.deleteRows(connHandler, "FG_CHEM_SEARCH",
					" WHERE formId = '" + formId + "' and fullFormCode = '" + fullFormCode + "'");
		}
		
		//FG_CHEM_DELETED_SEARCH
		isExists = generalDao.selectSingleStringNoException(
				"select distinct 1 from FG_CHEM_DELETED_SEARCH where formId = '" + formId + "'");
		
		if (generalUtil.getNull(isExists).equals("1")) {
			
			if (connHandlerDeleted == null || !connHandlerDeleted.isConnected()) {
				setConnectionHandlerDeleted();
			}
			
			UpdateHandler.deleteRows(connHandlerDeleted, "FG_CHEM_DELETED_SEARCH",
					" WHERE formId = '" + formId + "' and fullFormCode = '" + fullFormCode + "'");
		}
	}

	private void checkDuplication(String cd_id) throws Exception {
		String itemsList = generalDao.selectSingleStringNoException(
				"select LISTAGG(i.FORMID, ',') WITHIN GROUP (ORDER BY i.FORMID) as formid_list from " + DB_USER
						+ ".fg_chem_search s, fg_s_invitemmaterial_all_v i where s.fullformcode = 'InvItemMaterial.structure' and s.elementid = i.STRUCTURE and s.cd_id = "
						+ cd_id);
		if (!generalUtil.getNull(itemsList).isEmpty() && !itemsList.equals("0")) {
			throw new Exception(
					"{\"eMsg\":\"The structure you are trying to add already exists in the system\",\"itemsId\":\""
							+ itemsList + "\"}");
		} else {
			generalUtilLogger.logWrite(LevelType.WARN, "cd_id " + cd_id + " is not connected to material ",
					"-1", ActivitylogType.ChemMolSearchTask, null);
		}
		
	}

	@Override
	public int saveJChemWithBySchedTask(String elementId, String formId, String fullFormCode, String molType,
			String fullData, String tableName) {
		int retVal = 0;

		if (connHandler == null || !connHandler.isConnected()) {
			setConnectionHandler();
		}

		if (!isEmptyCanvas(fullData)) {
			int updateMode = UpdateHandler.INSERT;
			UpdateHandler uh = new UpdateHandler();
			try {
				uh = new UpdateHandler(connHandler, updateMode, tableName, "formId,fullFormCode,molType,elementId");

				uh.setStructure(fullData);
				uh.setValueForAdditionalColumn(1, formId);
				uh.setValueForAdditionalColumn(2, fullFormCode);
				uh.setValueForAdditionalColumn(3, molType);
				uh.setValueForAdditionalColumn(4, elementId);
				int cd_id = uh.execute(true); // True is parameter for return cd_id. In case duplication .execute returns id of exists structure with minus, e.g. -192.
				if (cd_id < 0) {
					retVal = cd_id;
					System.out.println("id of exists structure: " + String.valueOf(Math.abs(cd_id)));
					generalUtilLogger.logWrite(LevelType.ERROR,
							"id of exists structure: " + String.valueOf(Math.abs(cd_id)), formId,
							ActivitylogType.ChemMol, null);
				}

			} catch (Exception e) {
				System.out.println("jchem insertion failure e=" + e);
				generalUtilLogger.logWrite(LevelType.ERROR, "Exception, fg_chem_doodle_data.parent_id= " + elementId,
						formId, ActivitylogType.ChemMol, null, e);
			} finally {
				try {
					uh.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return retVal;
	}

	private String searchJChem(String mol) {
		String result = "-1";

		if (connHandler == null || !connHandler.isConnected()) {
			setConnectionHandler();
		}

		if (isSearchSubStructure == 1) {

			//		Need license in ENV (for example VARS NAME:CHEMAXON_LICENSE_URL / PATH: instruction) the license.cxl is in the Adama\License\chemaxon-license.cxl. 
			//	    See instruction in Adama\License\instruction.txt
			try {
				JChemSearch searcher = new JChemSearch(); // Create searcher object
				searcher.setQueryStructure(mol);
				searcher.setConnectionHandler(connHandler);
				searcher.setStructureTable(DB_USER + ".FG_CHEM_SEARCH");
				JChemSearchOptions searchOptions = new JChemSearchOptions(SearchConstants.SUBSTRUCTURE);
				searcher.setSearchOptions(searchOptions);
				searcher.run();
				System.out.println("Count of results is: " + searcher.getResultCount());
				int[] res = searcher.getResults(); //get array of cd_id from fg_chem_search

				String[] b = new String[res.length];
				for (int i = 0; i < res.length; i++) {
					b[i] = "\'" + res[i] + "\'";
				}
				String csv = StringUtils.arrayToDelimitedString(b, ",");

				result = generalDao.getCSVBySql("select s.formid from " + DB_USER
						+ ".FG_CHEM_SEARCH s where s.cd_id in (" + generalUtil.getEmpty(csv, "-1") + ")", false); //get csv string with formid. formid is fg_s_invitemmaterial_pivot.structure

			} catch (Exception e) {
				// TODO Auto-generated catch block
				generalUtilLogger.logWrite(LevelType.ERROR, "Exception in searchJChem", "-1",
						ActivitylogType.ChemMolSearchTask, null, e);

			}
		} else {//search full structure by smiles
			result = generalDao.getCSVBySql("select formid from FG_S_INVITEMMATERIAL_ALL_V t" + " where t."
					+ chemSearchType + " = '" + mol + "' and t.ACTIVE = '1'", false);
		}
		return generalUtil.getEmpty(result, "-1");
	}

	private String searchDeletedJChem(String mol) {
		String result = "-1";

		if (connHandlerDeleted == null || !connHandlerDeleted.isConnected()) {
			setConnectionHandlerDeleted();
		}

		if (isSearchSubStructure == 1) {

			//		Need license in ENV (for example VARS NAME:CHEMAXON_LICENSE_URL / PATH: instruction) the license.cxl is in the Adama\License\chemaxon-license.cxl. 
			//	    See instruction in Adama\License\instruction.txt
			try {
				JChemSearch searcher = new JChemSearch(); // Create searcher object
				searcher.setQueryStructure(mol);
				searcher.setConnectionHandler(connHandlerDeleted);
				searcher.setStructureTable("FG_CHEM_DELETED_SEARCH");
				JChemSearchOptions searchOptions = new JChemSearchOptions(SearchConstants.SUBSTRUCTURE);
				searcher.setSearchOptions(searchOptions);
				searcher.run();
				System.out.println("Count of results is: " + searcher.getResultCount());
				int[] res = searcher.getResults(); //get array of cd_id from fg_chem_search

				String[] b = new String[res.length];
				for (int i = 0; i < res.length; i++) {
					b[i] = "\'" + res[i] + "\'";
				}
				String csv = StringUtils.arrayToDelimitedString(b, ",");

				result = generalDao.getCSVBySql("select s.formid from " + "FG_CHEM_DELETED_SEARCH s where s.cd_id in ("
						+ generalUtil.getEmpty(csv, "-1") + ")", false); //get csv string with formid. formid is fg_s_invitemmaterial_pivot.structure

			} catch (Exception e) {
				// TODO Auto-generated catch block
				generalUtilLogger.logWrite(LevelType.ERROR, "Exception in searchDeletedJChem", "-1",
						ActivitylogType.ChemMolSearchTask, null, e);

			}
		} else {//search full structure by smiles
			result = generalDao.getCSVBySql("select formid from FG_S_INVITEMMATERIAL_ALL_V t" + " where t."
					+ chemSearchType + " = '" + mol + "' and t.ACTIVE = '1'", false);
		}
		return generalUtil.getEmpty(result, "-1");
	}

	private String searchDoc(String smiles) {
		String result = "-1";

		if (connHandlerDoc == null || !connHandlerDoc.isConnected()) {
			setConnectionHandlerDoc();
		}

		if (isSearchSubStructure == 1) {

			//		Need license in ENV (for example VARS NAME:CHEMAXON_LICENSE_URL / PATH: instruction) the license.cxl is in the Adama\License\chemaxon-license.cxl. 
			//	    See instruction in Adama\License\instruction.txt
			try {
				JChemSearch searcher = new JChemSearch(); // Create searcher object
				searcher.setQueryStructure(smiles);
				searcher.setConnectionHandler(connHandlerDoc);
				searcher.setStructureTable(DB_USER + ".FG_CHEM_DOC_SEARCH");
				JChemSearchOptions searchOptions = new JChemSearchOptions(SearchConstants.SUBSTRUCTURE);
				searcher.setSearchOptions(searchOptions);
				searcher.run();
				System.out.println("Count of doc results is: " + searcher.getResultCount());
				int[] res = searcher.getResults(); //get array of cd_id from fg_chem_search

				String[] b = new String[res.length];
				for (int i = 0; i < res.length; i++) {
					b[i] = "\'" + res[i] + "\'";
				}
				String csv = StringUtils.arrayToDelimitedString(b, "','");

				result = generalDao.getCSVBySql("select file_id from fg_files_fast_v where FILE_CHEM_ID in ('"
						+ generalUtil.getEmpty(csv, "-1") + "')", false);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				generalUtilLogger.logWrite(LevelType.ERROR, "Exception in searchDoc", "-1",
						ActivitylogType.ChemMolSearchTask, null, e);

			}
		} else {//search full structure by smiles
			result = generalDao.getCSVBySql(
					"select f.file_id from fg_files_fast_v f where f.FILE_CHEM_ID in (select to_char(t.cd_id) from FG_CHEM_DOC_SEARCH t where t.CD_SMILES = '"
							+ smiles + "')",
					false);
		}
		return generalUtil.getEmpty(result, "-1");
	}

	@Override
	public String getMRVContent(String ID) {
		String str = "";
		if (ID.startsWith("cml_copy_link")) {
			str = generalDao
					.getSingleStringFromClobNoException("select f.file_content from FG_CLOB_FILES f where f.file_id = '"
							+ ID.replace("cml_copy_link", "") + "' ");
		} else {
			str = generalDao.getSingleStringFromClobNoException("select nvl(c.reaction_all_data, f.file_content) "
					+ " from fg_i_chem_data_v c, FG_CLOB_FILES f  where  c.parent_id = '" + ID
					+ "' and c.REACTION_ALL_DATA_LINK = f.file_id(+) ");
		}
		return str;
	}

	private String saveChemImage(byte[] arr, String fileName, String fileType) {
		String elementID = "";
		try {
			logger.info("saveFile call /formCodeFull: MOLECULE_IMAGE.png");
			//elementID = formSaveDao.getStructFormId("MOLECULE_IMAGE.png");ab 30/08/18
			elementID = formSaveDao.getStructFileId("MOLECULE_IMAGE.png");
			logger.info("saveFile call /elementID: " + elementID);
			uploadFileDao.saveChemImageFile(arr, "", elementID);
		} catch (Throwable ex) //use Throwable to catch - java.lang.AbstractMethodError:
		{ // Trap SQL errors
			elementID = "-1";
			generalUtilLogger.logWrite(ex);
		}
		return elementID;
	}

	private String updateEmptyMarvin(String cml) {
		// TODO Auto-generated method stub
		if (generalUtil.getNull(cml).equals("") || generalUtil.getNull(cml).equals("<cml><MDocument/></cml>")) {
			cml = "<cml><MDocument></MDocument></cml>";
		}
		return cml;
	}

	@Override
	public String cleanJChemSearchTable() {
		String retVal = "1";
		if (connHandler == null || !connHandler.isConnected()) {
			setConnectionHandler();
		}
		try {
			UpdateHandler.deleteRows(connHandler, DB_USER + ".FG_CHEM_SEARCH",
					" WHERE fullFormCode = 'InvItemMaterial.structure'");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.out.println("jchem delete failure e=" + e1);
			retVal = "0";
			generalUtilLogger.logWrite(LevelType.ERROR,
					"Exception, Clean FG_CHEM_SEARCH failed", "-1",
					ActivitylogType.DuplicatedMaterials, null, e1);
		}
		return retVal;
	}

	@Override
	public void deleteRowJChemSearchTableNoMaterial(String formId) {
		try {
			removeRowJChem(formId);
		} catch (Exception e) {
			// do nothing
		}
	}

	//	@Override
	private HashMap<String, Object> getMoleculeFormats(String cml) throws IOException {
		// Converting into a molecule format
		Molecule molecule = MolImporter.importMol(cml);

		String mol = new String(MolExporter.exportToBinFormat(molecule, "mol"));
		String smiles = new String(MolExporter.exportToBinFormat(molecule, "smiles"));
		String inchi = "na";

		// Adding all the properties to a hashmap
		HashMap<String, Object> formats = new HashMap<String, Object>();
		formats.put("cml", cml);
		formats.put("mol", mol);
		formats.put("inchi", inchi);
		formats.put("smiles", smiles);
		byte[] img_byte_array = (byte[]) MolExporter.exportToObject(molecule, imgprop);
		formats.put("img", img_byte_array);

		String attributes = new JSONObject().put("molFormula", molecule.getFormula()).put("molMass", "-1")
				.put("atoms", "-1").put("name", "dummy when no License").toString();

		// Adding also the attributes
		formats.put("attributes", attributes);

		return formats;
	}

	@Override
	public ArrayList<HashMap<String, Object>> getReactionMolecules(String parentName, Document xml)
			throws TransformerException, IOException {
		Node list = xml.getElementsByTagName(parentName).item(0);

		// If there are no reactants/agents/products,
		// an empty list needs to be returned.
		if (list == null) {
			return new ArrayList<HashMap<String, Object>>();
		}

		ArrayList<HashMap<String, Object>> moleculesArray = new ArrayList<HashMap<String, Object>>();
		NodeList molecules = ((Element) list).getElementsByTagName("molecule");

		// Adding all the molecules properties into an array
		for (int i = 0; i < molecules.getLength(); i++) {
			String moleculeString = convertXmlToString(molecules.item(i));
			HashMap<String, Object> formats = getMoleculeFormats(moleculeString);
			moleculesArray.add(formats);
		}

		return moleculesArray;
	}

	@Override
	public Document convertStringToXml(String xml) throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document xmlDoc = db.parse(new InputSource(new StringReader(xml)));

		return xmlDoc;
	}

	@Override
	public String convertXmlToString(Node node) throws TransformerException {
		StringWriter sw = new StringWriter();

		Transformer t = TransformerFactory.newInstance().newTransformer();
		t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		t.transform(new DOMSource(node), new StreamResult(sw));

		return sw.toString();
	}

	@Override
	public String getMaterialListByStructVal(String inputVal) {
		String materialIdList = "";
		String smailes_ = getSmailsByStructVal(inputVal, "-1");
		materialIdList = searchJChem(smailes_);
		String materialCancelledList = searchDeletedJChem(smailes_);
		String toReturn = materialIdList;
		if (materialIdList.equals("-1")) {
			toReturn = materialCancelledList;
		} else if (!materialCancelledList.equals("-1")) {
			toReturn = materialIdList + "," + materialCancelledList;
		}
		return toReturn;
	}

	@Override
	public String getFileListByStructVal(String inputVal) {
		String fileIdList = "";
		String smailes_ = getSmailsByStructVal(inputVal, "-1");
		fileIdList = searchDoc(smailes_);
		return fileIdList;
	}

	@Override
	public String getSmailsByStructVal(String inputVal, String defaultWhenEmpty) {
		String smilesToReturn = "";
		if (generalUtil.getNull(inputVal).trim().equals("") || isEmptyCanvas(inputVal)) {
			smilesToReturn = defaultWhenEmpty;//"'ALL'";
		} else {
			HashMap<String, Object> formats = null;
			Document xml = null;
			String cml = "";

			try {
				xml = convertStringToXml(inputVal);
				cml = convertXmlToString(xml);
				formats = getMoleculeFormats(cml);
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (formats != null) {
				smilesToReturn = (String) formats.get(chemSearchType);
			}
		}
		return smilesToReturn;
	}

	private boolean isEmptyCanvas(String fullData) {
		// TODO Auto-generated method stub
		return !generalUtil.getNull(fullData).contains("atom");
	}

	private boolean isEmptyMol(String molVal) {
		// TODO define what is an empty molecule if needed
		return false;
	}
}
