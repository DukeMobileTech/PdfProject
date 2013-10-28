import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ExcelImageWriter {
	private static final String IMAGES_FOLDER = "src/images/";
	private List<ResponseImage> myImageObjects;
	private int topLeftCellRow = 2;
	private int topLeftCellCol = 3;
	private int cellRowOffset = 8;
	
	public ExcelImageWriter(List<ResponseImage> imageObjects) {
		myImageObjects = new ArrayList<ResponseImage>();
		myImageObjects = imageObjects;
		
		try {
			writeImageToCell();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeImageToCell() throws IOException {
		//create a new workbook, helper & sheet
	    Workbook wb = new XSSFWorkbook(); 
	    CreationHelper helper = wb.getCreationHelper();
    	Sheet sheet = wb.createSheet();

	    for(int k=0; k<myImageObjects.size(); k++) {
	    	//Create rows & cells and add question numbers with images
	    	Row row = sheet.createRow(topLeftCellRow);
	    	Cell cell = row.createCell(1);
	    	cell.setCellValue(myImageObjects.get(k).getQuestionNumber());
	    	//add picture data to this workbook.
	    	InputStream is = new FileInputStream(IMAGES_FOLDER + myImageObjects.get(k).getImageLocation());
	    	byte[] bytes = IOUtils.toByteArray(is);
	    	int pictureIdx = wb.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
	    	is.close();
	    	// Create the drawing patriarch.  This is the top level container for all shapes. 
	    	Drawing drawing = sheet.createDrawingPatriarch();
	    	//add a picture shape
	    	ClientAnchor anchor = helper.createClientAnchor();
	    	//set top-left corner of the picture, subsequent call of Picture#resize() will operate relative to it
	    	anchor.setCol1(topLeftCellCol);
	    	anchor.setRow1(topLeftCellRow);
	    	Picture pict = drawing.createPicture(anchor, pictureIdx);
	    	//auto-size picture relative to its top-left corner
	    	pict.resize();
	   
	    	topLeftCellRow +=cellRowOffset;	    	
	    }
	    
	    //save workbook
	    String file = "src/excelsheets/picture.xls";
	    if (wb instanceof XSSFWorkbook) file += "x";
	    FileOutputStream fileOut = new FileOutputStream(file);
	    wb.write(fileOut);
	    fileOut.close();
	}

}
