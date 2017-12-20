import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.sql.Connection;
import java.sql.DriverManager;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import de.jlo.talendcomp.jasperreportexec.JasperReportExecuter;


public class JasperReportExecuterTest {
	
	private static Map<String, Object> globalMap = new HashMap<String, Object>();
	private static String currentComponent = null;

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		talendJobTest();
	}
	
	private static Connection createConnection() throws Exception {
		Class.forName("org.postgresql.Driver");
//		Connection conn = DriverManager.getConnection(
//				"jdbc:postgresql://on-0337-jll:5432/foodmart", 
//				"postgres", 
//				"postgres");
		Connection conn = DriverManager.getConnection(
				"jdbc:postgresql://dwhdbref01:5432/dwh_vts?charSet=UTF8", 
				"dwh_vts_owner", 
				"8d1d66f7c5a263714c79d84dcd70825fb3230766915af9c41d7a214ea78346b4");
		return conn;
	}
	
	private static Connection createConnectionMobileGoogle() throws Exception {
		Class.forName("com.mysql.jdbc.Driver");
//		Connection conn = DriverManager.getConnection(
//				"jdbc:postgresql://on-0337-jll:5432/foodmart", 
//				"postgres", 
//				"postgres");
		Connection conn = DriverManager.getConnection(
				"jdbc:mysql://on-0337-jll.local:3306/GOOGLE_ANALYTICS_DEV", 
				"tisadmin", 
				"tisadmin");
		return conn;
	}

	public static void test() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		JasperReportExecuter exec = new JasperReportExecuter();
		System.out.println("Configure executer");
		exec.setFixLanguage(true);
//		exec.setOutputDir("/var/testdata/jasper");
		exec.setOutputDir("/var/testdata/jasper/output/");
		exec.setFileTimestampPattern("yyyy-MM-dd_HHmmss");
//		exec.setOutputFileNameWithoutExt("test_excel_cell_type");
//		exec.setJrxmlFile("/var/testdata/jasper/test_excel_cell_type.jrxml");

		exec.setOutputFileNameWithoutExt("shipping_report");
		exec.setJrxmlFile("/var/testdata/jasper/shipping_report.jrxml");
		exec.setParameterValue("Country", "USA");
		exec.setParameterValue("RequestDate", sdf.parse("1996-08-14"));

//		exec.setOutputFileNameWithoutExt("unsampled_reports");
//		exec.setJrxmlFile("/Volumes/Data/projects/mobile/workspace_jasper/Mobile/reports/unsampled_reports_processing_per_month.jrxml");
//		exec.setParameterValue("Country", "USA");
//		exec.setParameterValue("RequestDate", sdf.parse("1996-08-14"));

		exec.setOutputFormat("pdf");
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
		exec.setPdfVersion("7");
		exec.setPdfCreateBatchModeBookmarks(true);
		exec.setPdfCompressed(true);
//		exec.setPdfEncrypted(true);
//		exec.setPdfUserPassword("lolli");
		System.out.println("Set connection");
		exec.setConnection(createConnection());
		System.out.println("Compile report");
		exec.compileReport();
		System.out.println("Check parameters");
		exec.checkInputParameters();
		System.out.println("Fill report");
		exec.fillReport();
		System.out.println("Export report");
		exec.exportReport();
		System.out.println("-----------------");
		System.out.println("Parameters and values:");
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
	
	public static void talendJobTest() throws Exception {
		
		de.jlo.talendcomp.jasperreportexec.JasperReportExecuter tJasperReportExec_1 = new de.jlo.talendcomp.jasperreportexec.JasperReportExecuter();
		try {
			tJasperReportExec_1
					.setJrxmlFile("/Data/projects/gvl/data/talend/dwh/dwh_vts/artist_report/designs/Kuenstler.jrxml");
			tJasperReportExec_1.setFixLanguage(false);
			System.out.println("compile...");
			tJasperReportExec_1.compileReport();
			tJasperReportExec_1.setConnection(createConnection());
		} catch (Exception e) {
			throw e;
		}

		/**
		 * [tJasperReportExec_1 begin ] stop
		 */
		/**
		 * [tJasperReportExec_1 main ] start
		 */


		System.out.println("configure...");
		tJasperReportExec_1
				.setOutputDir("/var/testdata/jasper/output/");
		tJasperReportExec_1
				.setOutputFileNameWithoutExt("artist_report");
		tJasperReportExec_1.setOutputFormat("PDF");
		tJasperReportExec_1.setOverwriteFiles(true);
		tJasperReportExec_1.setFileTimestampPattern("yyyyMMdd_HHmmss");
		tJasperReportExec_1.setOutputLocale(null);
		// PDF options
		tJasperReportExec_1.setPdfCompressed(false);
		tJasperReportExec_1.setPdfCreateBatchModeBookmarks(false);
		tJasperReportExec_1.setPdfEncrypted(false);
		tJasperReportExec_1.setPdfAuthor("Jan Lolling");
		tJasperReportExec_1.setPdfCreator("Jan Lolling @ cimt");
		tJasperReportExec_1.setPdfTitle("Test Report");
		tJasperReportExec_1.setPdfSubject("Test of component");
		tJasperReportExec_1.setPdfKeywords("Talend, Test");
		tJasperReportExec_1.setPdfVersion("6");
		// fill parameter if given
		tJasperReportExec_1.setParameterValue("UUID", "8aabe720-e17e-11e7-818f-5d1146319eed");
		tJasperReportExec_1.setParameterValue("BUSINESS_PARTNER_ID", 1013l);
		System.out.println("-----------------");
		System.out.println("Parameters and values:");
		Map<String, Object> map = tJasperReportExec_1.getParameterMap();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			System.out.println(entry.getKey() + "=" + entry.getValue());
		}
		System.out.println("-----------------");
		// fill report with data
		try {
			System.out.println("fill...");
			tJasperReportExec_1.fillReport();
		} catch (Exception e) {
			throw e;
		}
		// export report data
		try {
			System.out.println("export...");
			tJasperReportExec_1.exportReport();
			System.out.println("Finished: " + tJasperReportExec_1.getOutputFile());
			System.out.println("Pages: " + tJasperReportExec_1.getNumberReportPages());
		} catch (Exception e) {
			throw e;
		}

	}

	public static void testXML() throws Exception {
		de.jlo.talendcomp.jasperreportexec.JasperReportExecuter tJasperReportExec_1 = new de.jlo.talendcomp.jasperreportexec.JasperReportExecuter();
		try {
			tJasperReportExec_1
					.setJrxmlFile("/Data/Jaspersoft/workspace_test/MyReports/xml_test_main.jrxml");
			tJasperReportExec_1.setFixLanguage(false);
			System.out.println("Compile...");
			tJasperReportExec_1.compileReport();
			System.out.println("Set Datasource...");
			tJasperReportExec_1.setXmlDataSource(
					"/Data/Talend/testdata/xml/keyfigures.xml",
					null, "dd.MM.yyyy", "###0.00;-###0.00");
		} catch (Exception e) {
			globalMap.put("tJasperReportExec_1_ERROR_MESSAGE",
					e.getMessage());
			throw e;
		}

		/**
		 * [tJasperReportExec_1 begin ] stop
		 */
		/**
		 * [tJasperReportExec_1 main ] start
		 */

		currentComponent = "tJasperReportExec_1";

		tJasperReportExec_1
				.setOutputDir("/Data/Talend/testdata/xml/");
		tJasperReportExec_1.setOutputFileNameWithoutExt("keyfigures");
		tJasperReportExec_1.setOutputFormat("PDF");
		tJasperReportExec_1.setOverwriteFiles(true);
		tJasperReportExec_1.setFileTimestampPattern("yyyyMMdd_HHmmss");
		tJasperReportExec_1.setOutputLocale("de");
		// PDF options
		tJasperReportExec_1.setPdfCompressed(false);
		tJasperReportExec_1.setPdfCreateBatchModeBookmarks(false);
		tJasperReportExec_1.setPdfEncrypted(false);
		tJasperReportExec_1.setPdfVersion("4");
		// fill parameter if given
		// fill report with data
		globalMap.put("tJasperReportExec_1_REPORT_QUERY",
				tJasperReportExec_1.getQueryString());
		tJasperReportExec_1.setPrintJRParameters(true);
		tJasperReportExec_1.checkInputParameters();
		try {
			System.out.println("Fill...");
			tJasperReportExec_1.fillReport();
			globalMap.put("tJasperReportExec_1_NUMBER_OF_PAGES",
					tJasperReportExec_1.getNumberReportPages());
		} catch (Exception e) {
			globalMap.put("tJasperReportExec_1_ERROR_MESSAGE",
					e.getMessage());
			throw e;
		}
		// export report data
		try {
			System.out.println("Export...");
			tJasperReportExec_1.exportReport();
			globalMap.put("tJasperReportExec_1_OUTPUT_FILE",
					tJasperReportExec_1.getOutputFile());
			System.out.println(tJasperReportExec_1.getOutputFile());
		} catch (Exception e) {
			globalMap.put("tJasperReportExec_1_ERROR_MESSAGE",
					e.getMessage());
			throw e;
		}

	}
	
}
