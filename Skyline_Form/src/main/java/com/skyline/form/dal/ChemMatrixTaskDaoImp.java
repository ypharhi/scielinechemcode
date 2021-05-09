package com.skyline.form.dal;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Repository;

import com.google.common.io.Files;
import com.skyline.form.bean.ActivitylogType;
import com.skyline.form.bean.LevelType;
import com.skyline.form.service.GeneralUtil;
import com.skyline.form.service.GeneralUtilLogger;

@Repository("ChemMatrixTaskDao")
@Configuration
@EnableAsync
public class ChemMatrixTaskDaoImp implements ChemMatrixTaskDao {
	
	@Autowired
	private GeneralDao generalDao;
	
	@Autowired
	public GeneralUtilLogger generalUtilLogger;
	
	@Autowired
	private GeneralUtil generalUtil;
	
	private File moleculeMatrix = null;
	
	@Value("${imagePath:c:/Logs}")
	private String imagePath;
	
	@Value("${imagePathBU:c:/Logs/ic-molecule_init-bu.png}")
	private String imageBUFilePath;
	
	@Value("${chem.imgpropreact:png:w900,h250,b32,#ffffff}")
	private String imgprop;// = "png:w2000,h250,b32,#ffffff";
	
	@PostConstruct
	public void ChemMatrixTaskDaoImpPostConstruct() {
		setMoleculeMatrixInit();
	}
	
	private void setMoleculeMatrixInit() {
		try {
			File moleculeMatrixBU = new File(imageBUFilePath);
			File moleculeMatrixEmpty = new File(imagePath + "/ic-molecule_init.png");
			if(moleculeMatrixBU.exists()) {
				Files.copy(moleculeMatrixBU, new File(imagePath + "/ic-molecule.png"));
			} else {
				Files.copy(moleculeMatrixEmpty, new File(imagePath + "/ic-molecule.png"));
			}
			moleculeMatrix = new File(imagePath + "/ic-molecule.png");
			moleculeMatrix.setReadable(true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void buildMoleculesMatrix() {
		String sql = "select distinct m.formid from fg_chem_doodle_data c, fg_s_invitemmaterial_v m where c.parent_id = m.STRUCTURE";
		List<String> materialList = generalDao.getListOfStringBySql(sql);

		String imageDefaultWidth;
		String imageDefaultHeight;
		try {
			int firstCommaInd = imgprop.indexOf(",");
			//"png:w900,h250,b32,#ffffff"
			imageDefaultWidth = imgprop.substring(imgprop.indexOf("w") + 1, firstCommaInd);
			imageDefaultHeight = imgprop.substring(imgprop.indexOf("h") + 1, imgprop.indexOf(",", firstCommaInd + 1));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			imageDefaultWidth = "900";
			imageDefaultHeight = "250";
		}

		List<BufferedImage> conc = new ArrayList<>();
		int i = 0;
		for (String materialId : materialList) {
			InputStream imgBlob = null;
			try{
				sql = "select f.file_content" + " from fg_files f," + " fg_chem_doodle_data c,"
						+ " fg_s_invitemmaterial_v m" + " where f.file_id = c.full_img_file_id"
						+ " and c.parent_id = m.STRUCTURE" + " and m.formid = '" + materialId + "'";
				imgBlob = generalDao.getInputStreamFromBlob(sql);
				if (imgBlob == null) {
					//update the coordinates in the material
					sql = "update fg_s_invitemmaterial_pivot" + " set xCoordinateInMatrix = null,"
							+ " yCoordinateInMatrix = null" + " where formid = '" + materialId + "'";
					generalDao.updateSingleString(sql);
					continue;
				}
			} catch(Exception e){//if there was an exception then the molecule is not got into the molecules matrix
				continue;
			}
			
			try {
				BufferedImage image = ImageIO.read(imgBlob);
				if (image.getWidth() != Integer.parseInt(imageDefaultWidth)
						|| image.getHeight() != Integer.parseInt(imageDefaultHeight)) {
//					generalUtilLogger.logWrite(LevelType.ERROR, "There is problem with width/height of molecule image: "
//							+ image.getWidth() + "/" + image.getHeight() + " ", "", ActivitylogType.ChemMol, null);
					continue;
				}
				Map<String, String> coordinates = generalUtil.addImageToMatrix(image, "png", conc, null, null, i);
				i++;

				//update the coordinates in the material
				sql = "update fg_s_invitemmaterial_pivot" + " set xCoordinateInMatrix = '" + coordinates.get("x") + "',"
						+ " yCoordinateInMatrix = '" + coordinates.get("y") + "'" + " where formid = '" + materialId
						+ "'";
				generalDao.updateSingleString(sql);
			} catch (Exception ex) {
				generalUtilLogger.logWrite(LevelType.WARN,
						"Error in updating the molecule matrix image for molecule " + i, "", ActivitylogType.ChemMatrixMap,
						null, ex);
			}
			
		}

		if (conc.size() > 0) {
			BufferedImage matrixImage = conc.get(0);//generalUtil.resize(conc.get(0),Integer.parseInt(String.valueOf(Math.round(conc.get(0).getWidth()*0.7))),Integer.parseInt(String.valueOf(Math.round(conc.get(0).getHeight()*0.7))));
			try {
				setMoleculeMatrix(matrixImage);
				updateImageBUInMatrix();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				generalUtilLogger.logWrite(LevelType.WARN, "Error in updating the molecule matrix image", "",
						ActivitylogType.ChemMatrixMap, null, e);
			}
		}
	}

	@Async
	@Override
	public void updateMoleculeMatrix(String formId, String struct, String x, String y, String userId) {
		System.out.println("updateMoleculeMatrix start formId =" + formId);
		String sql = "select f.file_content from fg_chem_doodle_data c,fg_files f where c.parent_id='"
				+ struct + "'" + " and c.full_img_file_id = f.file_id";
		InputStream imgBlob = generalDao.getInputStreamFromBlob(sql);
		String newX = x;
		String newY = y;
		try {
			BufferedImage image = ImageIO.read(imgBlob);
			if (imgBlob == null) {
				 newX = "";
				 newY = "";
			} else {
				Map<String, String> coordinates = updateImageInMatrixSync(image,
						x, y);
				if (coordinates != null) {
					newX = coordinates.get("x");
					newY = coordinates.get("y");
				}
			}
			
			if(!newX.equals(x) || !newY.equals(y)) {
				sql = "update fg_s_invitemmaterial_pivot t set t.xcoordinateinmatrix = '" + newX + "', t.ycoordinateinmatrix = '" + newY + "' where t.formid = '" + formId + "'";
				generalDao.updateSingleString(sql);
			}
			
		} catch (Exception e) {
			generalUtilLogger.logWriter(LevelType.WARN, "Error in updating the molecule matrix when saving material id "
					+ formId, ActivitylogType.ChemMatrixMap, formId, e, userId);
		}
	}
	
	private Map<String, String> updateImageInMatrixSync(BufferedImage image, String x, String y) throws IOException, InterruptedException {
		Map<String, String> coordinates = null;
		synchronized (this) {
			BufferedImage moleculeMatrix = getMoleculeMatrixImage();
			if (moleculeMatrix != null) {
				List<BufferedImage> srcMoleculeMatrix = new ArrayList<>();
				srcMoleculeMatrix.add(moleculeMatrix);
				
				if (!x.isEmpty() && !y.isEmpty()) {
					coordinates = generalUtil.addImageToMatrix(image, "png", srcMoleculeMatrix, x, y, -1);
				} else {
					coordinates = generalUtil.addNewImageToMatrix(image, "png", srcMoleculeMatrix);
				}
				setMoleculeMatrix(srcMoleculeMatrix.get(0));
				updateImageBUInMatrix();
			}
		}
		return coordinates;
	}
	
	private void updateImageBUInMatrix() throws IOException {
		File moleculeMatrixBU = new File(imageBUFilePath);
		Files.copy(new File(imagePath + "/ic-molecule.png"), moleculeMatrixBU);
	}

	/**
	 * get the molecule matrix bufferedImage and write it to the physical file
	 */
	private void setMoleculeMatrix(BufferedImage matrixImage) throws IOException {
		if (moleculeMatrix == null) {
			moleculeMatrix = new File(imagePath + "/ic-molecule.png");
		}

		if (matrixImage != null) {
			try {
				ImageIO.write(matrixImage, "png", moleculeMatrix);
			} catch (IOException e) {
				e.printStackTrace();
				generalUtilLogger.logWriter(LevelType.WARN, "Error in writing to the molecule matrix image",
						ActivitylogType.ChemMatrixMap, "");
			}
			moleculeMatrix.setReadable(true);
		}
	}
	
	private BufferedImage getMoleculeMatrixImage() {
		if (moleculeMatrix != null) {
			BufferedImage image;
			try {
				image = ImageIO.read(moleculeMatrix);
				return image;
			} catch (IOException e) {
				generalUtilLogger.logWriter(LevelType.WARN, "Error in reading the molecule matrix image",
						ActivitylogType.ChemMatrixMap, "");
				return null;
			}
		}
		return null;
	}
}
