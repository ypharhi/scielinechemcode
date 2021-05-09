package com.skyline.form.dal;

public interface ChemMatrixTaskDao {
	
	void buildMoleculesMatrix();

	void updateMoleculeMatrix(String formId, String struct, String x, String y, String userId);
	
//	void setMoleculeMatrix(BufferedImage matrixImage) throws IOException;

//	void setMoleculeMatrixInit() throws IOException;

//	BufferedImage getMoleculeMatrixImage();

}
