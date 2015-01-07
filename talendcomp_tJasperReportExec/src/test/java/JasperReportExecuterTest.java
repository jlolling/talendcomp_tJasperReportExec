import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

import de.cimt.talendcomp.jasperreportexec.JasperReportExecuter;


public class JasperReportExecuterTest {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		test();
	}
	
	private static Connection createConnection() throws Exception {
		Class.forName("org.postgresql.Driver");
		Connection conn = DriverManager.getConnection(
				"jdbc:postgresql://localhost:5432/foodmart", 
				"postgres", 
				"postgres");
		return conn;
	}
	
	public static void test() throws Exception {
		JasperReportExecuter exec = new JasperReportExecuter();
		System.out.println("Configure executer");
		exec.setFixLanguage(true);
		exec.setOutputDir("/home/jlolling/test/jasper/");
		exec.setFileTimestampPattern("yyyy-MM-dd_HHmmss");
		exec.setOutputFileNameWithoutExt("test_excel_cell_type");
		exec.setJrxmlFile("/home/jlolling/test/jasper/test_excel_cell_type.jrxml");
		exec.setOutputFormat("xls");
		exec.setXlsDetectCellType(true);
		exec.setXlsIgnoreCellBackground(true);
		exec.setXlsWhitePageBackground(false);
		exec.setXlsRemoveEmptySpaceBetweenColumns(true);
		exec.setXlsRemoveEmptySpaceBetweenRows(true);
		exec.setPdfCreator("talend");
		exec.setPdfTitle("Statistik");
		exec.setPdfSubject("betreff");
		exec.setPdfAuthor("Jan");
		exec.setPdfTagged(true);
		exec.setPdfVersion("6");
		exec.setPdfCreateBatchModeBookmarks(true);
		exec.setPdfCompressed(true);
		exec.setPdfEncrypted(true);
		exec.setPdfUserPassword("lolli");
		System.out.println("Set connection executer");
		exec.setConnection(createConnection());
		System.out.println("Compile report");
		exec.compileReport();
		System.out.println("Fill report");
		exec.fillReport();
		System.out.println("Export report");
		exec.exportReport();
		System.out.println("-----------------");
		System.out.println("Parameters:");
		Map<String, Object> map = exec.getParameterMap();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			System.out.println(entry.getKey() + "=" + entry.getValue());
		}
		System.out.println("-----------------");
		System.out.println("Finished: " + exec.getOutputFile());
		System.out.println("Pages: " + exec.getNumberReportPages());
	}
	
	public static void testEnv() {
		System.out.println(System.getProperty("java.class.path"));
		// Font info is obtained from the current graphics environment.
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

		//--- Get an array of font names (smaller than the number of fonts)
		String[] fontNames = ge.getAvailableFontFamilyNames();
		System.out.println("getAvailableFontFamilyNames: ***********************************************");
		for (String fontName : fontNames) {
			System.out.println(fontName);
		}
		//--- Get an array of fonts.  It's preferable to use the names above.
		Font[] allFonts = ge.getAllFonts();
		System.out.println("allFonts: ***********************************************");
		for (Font f : allFonts) {
			System.out.println(f.getFontName());
		}
		
	}

}
