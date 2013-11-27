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
	private String images_folder;
	private List<ResponseImage> myImageObjects;
	private String output_folder;
	
	private int topLeftCellRow = 1;
	private int topLeftCellCol = 3;
	private int cellRowOffset = 12;
	
	public ExcelImageWriter(List<ResponseImage> imageObjects, String img_folder, String folder_name) {
		myImageObjects = new ArrayList<ResponseImage>();
		myImageObjects = imageObjects;
		images_folder = img_folder;
		output_folder = folder_name;
		
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
    	
    	createColumnHeaders(sheet);

	    for(int k=0; k<myImageObjects.size(); k++) {
	    	//Create rows & cells and add question numbers with images
	    	Row row = sheet.createRow(topLeftCellRow);
	    	Cell cell0 = row.createCell(0);
	    	cell0.setCellValue(myImageObjects.get(k).getSourceLocation());
	    	Cell cell = row.createCell(1);
	    	cell.setCellValue(myImageObjects.get(k).getQuestionNumber());
	    	
	    	//add picture data to this workbook.
	    	InputStream is = new FileInputStream(images_folder + myImageObjects.get(k).getImageLocation());
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
	    String file = output_folder + "pictures.xls";
	    if (wb instanceof XSSFWorkbook) file += "x";
	    FileOutputStream fileOut = new FileOutputStream(file);
	    wb.write(fileOut);
	    fileOut.close();
	    System.out.println("DONE");
	}

	private void createColumnHeaders(Sheet wbsheet) {
		Row row = wbsheet.createRow(0);
    	Cell cell0 = row.createCell(0);
    	cell0.setCellValue("File Name");
    	Cell cell1 = row.createCell(1);
    	cell1.setCellValue("Question #");
    	Cell cell2 = row.createCell(3);
    	cell2.setCellValue("Image");
	}

}
