
public class Main {
	private static String pdf_surveys_folder;
	private static String properties_data_file;
	private static String excel_output_folder;
	private static String clipped_images_folder;

	public static void main(String[] args) {
		System.out.println("MAIN");
		
		if (args.length != 4) {
			System.out.println("Incorrect arguments");
			System.exit(0);
		}
		else {
			properties_data_file = args[0];
			pdf_surveys_folder = args[1];
			clipped_images_folder = args[2];
			excel_output_folder = args[3];
			new PdfReader(properties_data_file, pdf_surveys_folder, clipped_images_folder, excel_output_folder);
		}
	}

}
