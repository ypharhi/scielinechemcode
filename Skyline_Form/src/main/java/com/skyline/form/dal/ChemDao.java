package com.skyline.form.dal;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public interface ChemDao {

	/**
	 * Save chem data from the chem elements in fg_chem_doodle_data (with row for each molecule in reaction elements and images links) and in fg_chem_search (chemaxon table for sub structure search)
	 * Note: chem_doodle was the previous client side molecule structure program, the update one is marvinJS
	 * @param formCode
	 * @param formId
	 * @param elementID
	 * @param fullData - cml represent the data of the client side molecule structure 
	 * @param formCodeFull
	 * @param isNew
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws Exception
	 */
	String saveChemData(String formCode, String formId, String elementID, String fullData, String formCodeFull,
			String isNew) throws ParserConfigurationException, SAXException, IOException, TransformerException,
			ClassNotFoundException, SQLException, Exception;

	/**
	 * Save data from attachment files end with .mol (mol files) from the Document form in fg_chem_doc_search (chemaxon for searching this document by sub-structure search)
	 * @param fullData - mol file
	 * @return Json with the keys: imgId (file_id in fg_files holding the structure image) and chemId - the id of fg_chem_doc_search
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws Exception
	 */
	JSONObject saveDocData(String fullData, String formId) throws ParserConfigurationException, SAXException, IOException,
			TransformerException, ClassNotFoundException, SQLException, Exception;

	/**
	 * return the marvinJS data for the canvas display
	 * @param ID - element value
	 * @param isPln
	 * @return
	 */
	String getMRVContent(String ID);

	String cleanJChemSearchTable();

	//	String searchJChem(String mol);

	String getNewChemImg(String fullData, String formId);

	/**
	* 
	* @param xml
	*            The string that is the MRV structure,<br>
	*            needs to be converted<br>
	*            into XML for retrieval of elements<br>
	* @return The XML {@link org.w3c.dom.Document}
	* @throws ParserConfigurationException
	* @throws SAXException
	* @throws IOException
	*/
	public Document convertStringToXml(String xml) throws ParserConfigurationException, SAXException, IOException;

	/**
	 * Convert the MRV structure to a String,<br>
	 * in order to send it to the element,<br>
	 * for easy loading in the client side.
	 * 
	 * @param node
	 *            The active XML
	 * @return The required String for export to the element
	 * @throws TransformerException
	 */
	public String convertXmlToString(Node node) throws TransformerException;

	/**
	 * Gets all formats and some attributes from the molecule:<br>
	 * <br>
	 * <b>Formats:</b><br>
	 * MRV, Inchi, SMILES, MOL file<br>
	 * <br>
	 * <b>Attributes:</b><br>
	 * Formula, Mass, Atom count, Name (UIPAC)<br>
	 * 
	 * @param cml
	 * @return
	 * @throws IOException
	 */
	//    public HashMap<String, Object> getMoleculeFormats(String cml) throws IOException;

	/***
	 * Getting all the molecules from the reaction,<br>
	 * together with all the formats
	 * 
	 * @see com.skyline.form.service.ChemService#getMoleculeFormats()
	 * 
	 * @param parentName
	 *            The string that is the reaction part (Reactant, Product or
	 *            Agent)
	 * @param xml
	 *            The MRV XML
	 * @return
	 * @throws TransformerException
	 * @throws IOException
	 */
	public ArrayList<HashMap<String, Object>> getReactionMolecules(String parentName, Document xml)
			throws TransformerException, IOException;

	public String getMaterialListByStructVal(String value);

	public String getSmailsByStructVal(String value, String defaultWhenEmpty);

//	String deleteRowJChemSearchTable(String formId);

//	void setMoleculeMatrix(BufferedImage matrixImage) throws IOException;

//	void setMoleculeMatrixInit() throws IOException;

//	BufferedImage getMoleculeMatrixImage();

	/**
	 * - for sche task use only! - init all FG_CHEM_SEARCH records (the table FG_CHEM_SEARCH fully cleaned before this call)
	 * @param elementId
	 * @param formId
	 * @param fullFormCode
	 * @param molType
	 * @param fullData
	 * @param tableName
	 * @return
	 */
	int saveJChemWithBySchedTask(String elementId, String formId, String fullFormCode, String molType, String fullData,
			String tableName);

	String getFileListByStructVal(String inputVal);

	void saveJChem(String formId, String string) throws Exception;

	void saveJChemDeleted(String formId, String string) throws Exception;

	void deleteRowJChemSearchTableNoMaterial(String formId);
}
