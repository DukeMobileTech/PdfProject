import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

public class PdfReader {
	private final String SURVEYS_FOLDER = "src/surveys";
	private final String IMAGES_FOLDER = "src/images/";
	private final String DATA_FILE = "src/properties.txt"; //NB: Page numbers are numbered from zero
	private List<String> mySurveyNames;
	private Map<Integer, List<int[]>> mySurveyResponses; //Maps page number to question numbers & x and y coordinates 
	private int fileNumber = 0;
	private List<ResponseImage> myResponseImages;
	
	public PdfReader() {
		start();
	}

	private void readPropertiesFile() {
		mySurveyResponses = new HashMap<Integer, List<int[]>>();
		File propertiesFile = new File(DATA_FILE);
		try {
			Scanner documentInput = new Scanner(propertiesFile);
			while (documentInput.hasNext()) {
				String line = documentInput.nextLine();
				String[] numbers = line.split(",");
				int pageNumber = Integer.parseInt(numbers[0]);
				int questionNumber = Integer.parseInt(numbers[1]);
				int startX = Integer.parseInt(numbers[2]);
				int startY = Integer.parseInt(numbers[3]);
				int width = Integer.parseInt(numbers[4]);
				int height = Integer.parseInt(numbers[5]);
				int[] imageDetails = { questionNumber, startX, startY, width,
						height };
				if (mySurveyResponses.containsKey(pageNumber)) {
					List<int[]> list = mySurveyResponses.get(pageNumber);
					list.add(imageDetails);
					mySurveyResponses.put(pageNumber, list);
				} else {
					List<int[]> questionAndCoordinates = new ArrayList<int[]>();
					questionAndCoordinates.add(imageDetails);
					mySurveyResponses.put(pageNumber, questionAndCoordinates);
				}
			}
			
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private List<String> getSurveyFileNames() {
		File folder = new File(SURVEYS_FOLDER);
		File[] listOfFiles = folder.listFiles();
		List<String> fileNames = new ArrayList<String>();
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				fileNames.add(listOfFiles[i].getName());
			}
		}
		return fileNames;
	}

	private void getQuestionResponseImage(String pdfName) {
		try {
			PDDocument document = null;
			File file = new File(SURVEYS_FOLDER + "/" + pdfName);
			document = PDDocument.load(file);
			@SuppressWarnings("unchecked")
			List<PDPage> pages = document.getDocumentCatalog().getAllPages();
			for (Integer pageNum : mySurveyResponses.keySet()) {
				PDPage page = (PDPage) pages.get(pageNum);
				PDResources resources = page.getResources();
				Map<?, ?> pageImages = resources.getImages();
				if (pageImages != null) {
					Iterator<?> imageIter = pageImages.keySet().iterator();
					while (imageIter.hasNext()) {
						String key = (String) imageIter.next();
						PDXObjectImage image = (PDXObjectImage) pageImages.get(key);
						BufferedImage buff = image.getRGBImage();
						for (int k = 0; k < mySurveyResponses.get(pageNum).size(); k++) {
							int[] myArray = mySurveyResponses.get(pageNum).get(k);
							BufferedImage clippedImg = buff.getSubimage(myArray[1],myArray[2], myArray[3], myArray[4]);
							File outputfile = new File(IMAGES_FOLDER + fileNumber + ".png");
							fileNumber++;
							ImageIO.write(clippedImg, "png", outputfile);	
							createResponseImageObjects(file.getName(), outputfile.getName(), myArray[0], pageNum);
						}		
					}
				}
			}
			document.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void createResponseImageObjects(String fileName1, String fileName2, int questionNumber, int pageNumber) {
		ResponseImage image = new ResponseImage(fileName1, questionNumber, fileName2, pageNumber);
		myResponseImages.add(image);
	}

	public void start() {
		readPropertiesFile();
		myResponseImages = new ArrayList<ResponseImage>();
		mySurveyNames = getSurveyFileNames();
		for (String name : mySurveyNames) {
			getQuestionResponseImage(name);
		}
		
		//Write images to Excel workbook
		new ExcelImageWriter(myResponseImages);
	}

}
