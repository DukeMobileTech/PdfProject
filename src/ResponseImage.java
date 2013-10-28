
public class ResponseImage {
	private String sourceFileName;
	private int questionNumber;
	private int pageNumber;
	private String imageLocationPath;
	
	public ResponseImage(String fileName, int questionNum, String ImagePath, int pageNum) {
		sourceFileName = fileName;
		questionNumber = questionNum;
		imageLocationPath = ImagePath;
		pageNumber = pageNum;
	}

	public String getImageLocation() {
		return imageLocationPath;
	}
	
	public String getSourceLocation() {
		return sourceFileName;
	}
	
	public Integer getQuestionNumber() {
		return questionNumber;
	}
	
	public Integer getPageNumber() {
		return pageNumber;
	}
}
