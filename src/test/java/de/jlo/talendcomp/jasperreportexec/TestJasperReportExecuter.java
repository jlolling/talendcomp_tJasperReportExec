package de.jlo.talendcomp.jasperreportexec;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.Test;

public class TestJasperReportExecuter {

	@Test
	public void test_csv_report_employee() throws Exception {
		JasperReportExecuter jre = new JasperReportExecuter();
		System.out.println("Delete previous jasper file...");
		File jasperFile = new File("input/employees_csv.jasper");
		if (jasperFile.exists()) {
			jasperFile.delete();
		}
		System.out.println("Compile report...");
		jre.setJrxmlFile("input/employees_csv.jrxml");
		jre.compileReport();
		jre.checkInputParameters();
		String sourceFile = "input/employees.csv";
		String charset = "UTF-8";
		String datePattern = "yyyy-MM-dd";
		String numberPattern = "#####";
		String rowSeparator = "\n";
		String fieldDelimiter = ",";
		String fieldList = "ID,FirstName,LastName,Position"; // must be delimited by the fieldDelimiter
		System.out.println("Set input file...");
		jre.setCsvDataSource(sourceFile, charset, datePattern, numberPattern, rowSeparator, fieldDelimiter, false, fieldList);
		jre.setOutputFormat("PDF");
		jre.setOutputDir("output/");
		jre.setOutputFileNameWithoutExt("employees_csv");
		jre.setFileTimestampPattern("yyyyMMdd_HHmmss");
		jre.setPdfVersion("4");
		System.out.println("Fill report...");
		jre.fillReport();
		System.out.println("Export report...");
		jre.exportReport();
		System.out.println("Check compare pdf files...");
		String outputFilePathExcepted = new File("input/employees_csv_expected.pdf").getAbsolutePath();
		String outputFilePathActual = jre.getOutputFile();
		String textExpected = getTextFromPDF(outputFilePathExcepted);
		String textActual = getTextFromPDF(outputFilePathActual);
		assertEquals(textExpected, textActual);
	}
	
	private String getTextFromPDF(String pdfFilePath) throws Exception {
        File file = new File(pdfFilePath);
        try (PDDocument document = Loader.loadPDF(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            String text = stripper.getText(document);
            return text;
        } catch (Exception e) {
            throw e;
        }		
	}
	
}
