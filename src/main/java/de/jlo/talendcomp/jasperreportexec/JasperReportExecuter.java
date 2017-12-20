/**
 * Copyright 2015 Jan Lolling jan.lolling@gmail.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.jlo.talendcomp.jasperreportexec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.xml.sax.InputSource;

import net.sf.jasperreports.crosstabs.JRCrosstab;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRBreak;
import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JREllipse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRFrame;
import net.sf.jasperreports.engine.JRGenericElement;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRImage;
import net.sf.jasperreports.engine.JRLine;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPart;
import net.sf.jasperreports.engine.JRRectangle;
import net.sf.jasperreports.engine.JRStaticText;
import net.sf.jasperreports.engine.JRSubreport;
import net.sf.jasperreports.engine.JRTextField;
import net.sf.jasperreports.engine.JRVisitor;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.query.JRXPathQueryExecuterFactory;
import net.sf.jasperreports.engine.type.SectionTypeEnum;
import net.sf.jasperreports.engine.util.JRElementsVisitor;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.engine.xml.JRXmlDigesterFactory;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.AbstractXlsReportConfiguration;
import net.sf.jasperreports.export.HtmlExporterOutput;
import net.sf.jasperreports.export.SimpleCsvExporterConfiguration;
import net.sf.jasperreports.export.SimpleCsvReportConfiguration;
import net.sf.jasperreports.export.SimpleDocxExporterConfiguration;
import net.sf.jasperreports.export.SimpleDocxReportConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterConfiguration;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleHtmlReportConfiguration;
import net.sf.jasperreports.export.SimpleOdsExporterConfiguration;
import net.sf.jasperreports.export.SimpleOdsReportConfiguration;
import net.sf.jasperreports.export.SimpleOdtExporterConfiguration;
import net.sf.jasperreports.export.SimpleOdtReportConfiguration;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import net.sf.jasperreports.export.SimplePptxExporterConfiguration;
import net.sf.jasperreports.export.SimplePptxReportConfiguration;
import net.sf.jasperreports.export.SimpleRtfExporterConfiguration;
import net.sf.jasperreports.export.SimpleRtfReportConfiguration;
import net.sf.jasperreports.export.SimpleTextExporterConfiguration;
import net.sf.jasperreports.export.SimpleTextReportConfiguration;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import net.sf.jasperreports.export.SimpleXlsxExporterConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import net.sf.jasperreports.export.WriterExporterOutput;
import net.sf.jasperreports.export.type.PdfVersionEnum;
import net.sf.jasperreports.parts.subreport.SubreportPartComponent;

public class JasperReportExecuter {

	private static final HashMap<String, JasperReport> reportMap = new HashMap<String, JasperReport>();
	private static final HashMap<String, Long> lastCompiledMap = new HashMap<String, Long>();
	private JasperReport mainJasperReport = null;
	private JasperPrint jasperPrint = null;
	private Connection dbConnection = null;
	private JRDataSource jrDataSource;
	private File mainJrxmlFile = null;
	private String outputDir = null;
	private String outputFileNameWithoutExt = null;
	private String outputFormat = null;
	private Map<String, Object> parameterMap = new HashMap<String, Object>();
	private SimpleDateFormat fileTimestampSDF = null;
	private String outputFile = null;
	private boolean overwriteFiles = true;
	private boolean createDirIfNecessary = true;
	private Boolean xlsDetectCellType = null;
	private Boolean xlsWhitePageBackground = null;
	private Boolean xlsOnePagePerSheet = null;
	private Boolean xlsIgnoreCellBackground = null;
	private Boolean xlsRemoveEmptySpaceBetweenRows = null;
	private Boolean xlsRemoveEmptySpaceBetweenColumns = null;
	private String xlsTemplateWorkbookFile = null;
	private boolean xlsKeepWorkbookTemplateSheets = false;
	private String pdfOwnerPassword = null; // will also used to let the
											// document be encrypted
	private String pdfUserPassword = null;
	private Boolean pdfEncrypted = null;
	private Boolean pdfEncryptionKey128Bit = null;
	private Boolean pdfCompressed = null;
	private Boolean pdfCreateBatchModeBookmarks = null;
	private Boolean pdfTagged = null;
	private String pdfAuthor = null;
	private String pdfCreator = null;
	private String pdfKeywords = null;
	private String pdfSubject = null;
	private String pdfTitle = null;
	private String pdfVersion = null;
	private int numberReportPages = 0;
	private boolean fixLanguage = false;
	private String queryString = null;
	private List<JRParameter> listJRParameters = null;
	private boolean printJRParameters = false;
	private boolean replaceJrxmlRef = true;

	public String getOutputDir() {
		return outputDir;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public String getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}

	public File getJrxmlFile() {
		return mainJrxmlFile;
	}

	public String getOutputFileNameWithoutExt() {
		return outputFileNameWithoutExt;
	}

	public void setFileTimestampPattern(String pattern) {
		if (pattern != null && pattern.isEmpty() == false) {
			fileTimestampSDF = new SimpleDateFormat(pattern);
		} else {
			fileTimestampSDF = null;
		}
	}

	public void setJrxmlFile(String file) {
		if (file != null) {
			mainJrxmlFile = new File(file);
		} else {
			throw new IllegalArgumentException("jrxml file is needed!");
		}
	}
	
	private void retrieveJRParameter() throws JRException {
		if (mainJasperReport == null) {
			mainJasperReport = (JasperReport) JRLoader.loadObjectFromFile(getJasperFileName(mainJrxmlFile.getAbsolutePath()));
		}
		listJRParameters = new ArrayList<JRParameter>();
		JRParameter[] allReportParams = mainJasperReport.getParameters();
		for (JRParameter rp : allReportParams) {
			listJRParameters.add(rp);
		}
	}
	
	public void checkInputParameters() throws Exception {
		retrieveJRParameter();
		if (printJRParameters) {
			System.out.println("List report parameters (except system parameters):");
			boolean emptyList = true;
			for (JRParameter rp : listJRParameters) {
				if (rp.isSystemDefined() == false) {
					System.out.println(rp.getName() + ": type=" + rp.getValueClassName() + (rp.getNestedTypeName() != null ? ", nested type=" + rp.getNestedTypeName() : ""));
					emptyList = false;
				}
			}
			if (emptyList) {
				System.out.println("Report does not have any none-system parameters.");
			}
		}
		// check for wrong input parameters
		List<String> wrongInputParameters = new ArrayList<String>();
		for (Map.Entry<String, Object> inputParamEntry : parameterMap.entrySet()) {
			boolean wrongDataType = false;
			boolean notFound = true;
			JRParameter matchingReportParameter = null;
			for (JRParameter rp : listJRParameters) {
				// try to find the input parameters in the report
				if (rp.getName().equals(inputParamEntry.getKey())) {
					notFound = false;
					matchingReportParameter = rp;
					// check the data type
					if (inputParamEntry.getValue() != null) {
						if (rp.getValueClass().isAssignableFrom(inputParamEntry.getValue().getClass()) == false) {
							wrongDataType = true;
						}
					}
					break; // stop search for this input parameters
				}
			}
			if (notFound) {
				wrongInputParameters.add(inputParamEntry.getKey() + ": Not found in report (perhapse a typo?)");
			} else if (wrongDataType) {
				wrongInputParameters.add(inputParamEntry.getKey() + ": Input type:" + inputParamEntry.getValue().getClass() + " is not compatible to the report parameter type: " + matchingReportParameter.getValueClassName());
			}
		}
		// check if we miss some parameters
		List<String> missingInputParameters = new ArrayList<String>();
		for (JRParameter rp : listJRParameters) {
			if (rp.isForPrompting() && rp.isSystemDefined() == false) {
				boolean notFound = true;
				for (Map.Entry<String, Object> inputParamEntry : parameterMap.entrySet()) {
					if (rp.getName().equals(inputParamEntry.getKey())) {
						notFound = false;
						break;
					}
				}
				if (notFound) {
					missingInputParameters.add(rp.getName() + ": Is missing. Type: " + rp.getValueClassName());
				}
			}
		}
		StringBuilder message = new StringBuilder();
		if (wrongInputParameters.isEmpty() == false) {
			message.append("Input parameters does not match to the report parameters:\n");
			for (String s : wrongInputParameters) {
				message.append(s);
				message.append("\n");
			}
		}
		if (missingInputParameters.isEmpty() == false) {
			message.append("Missing input parameters:\n");
			for (String s : missingInputParameters) {
				message.append(s);
				message.append("\n");
			}
		}
		if (message.length() > 0) {
			throw new Exception(message.toString());
		}
	}

	/**
	 * set the output file with full path but without the extension of the
	 * target file file extensions will be added depending the output format
	 * 
	 * @param outputFileWithoutExt
	 */
	public void setOutputFileNameWithoutExt(String outputFileWithoutExt) {
		this.outputFileNameWithoutExt = outputFileWithoutExt;
	}

	private boolean needCompiling(String jrxmlFilePath) throws Exception {
		File jasperFile = new File(getJasperFileName(jrxmlFilePath));
		File f = new File(jrxmlFilePath);
		if (jasperFile.canRead()
				&& (f.lastModified() < jasperFile.lastModified())) {
			return false;
		} else {
			JasperReport jasperReport = reportMap.get(jrxmlFilePath);
			Long lastCompiledAt = lastCompiledMap.get(jrxmlFilePath);
			if (jasperReport != null && lastCompiledAt != null
					&& (f.lastModified() < lastCompiledAt)) {
				return false;
			} else {
				return true;
			}
		}
	}

	private void setupOutputFile(String extension) {
		if (fileTimestampSDF != null) {
			outputFileNameWithoutExt = outputFileNameWithoutExt + "_"
					+ fileTimestampSDF.format(new Date());
		}
		if (outputDir.endsWith("/")) {
			outputFile = outputDir + outputFileNameWithoutExt + "." + extension;
		} else {
			outputFile = outputDir + "/" + outputFileNameWithoutExt + "."
					+ extension;
		}
	}

	public String getOutputFile() {
		return outputFile;
	}

	/**
	 * compiles the report and keep the compiled report in memory (static)
	 * 
	 * @throws Exception
	 *             if compiling fails
	 */
	public void compileReport() throws Exception {
		compileException = null;
		if (mainJrxmlFile == null) {
			throw new Exception("jrxmlFile not set");
		}
		if (mainJrxmlFile.canRead() == false) {
			throw new Exception("jrxmlFile " + mainJrxmlFile.getAbsolutePath()
					+ " cannot be read");
		}
		compileReport(mainJrxmlFile.getAbsolutePath(), true);
		if (compileException != null) {
			throw compileException;
		}
		mainJasperReport = reportMap.get(mainJrxmlFile.getAbsolutePath());
	}

	private Exception compileException = null;
	
	private JasperDesign retrieveJasperDesign(File jrxmlFile) throws Exception {
		JRXmlLoader loader = new JRXmlLoader(
					DefaultJasperReportsContext.getInstance(), 
					JRXmlDigesterFactory.createDigester(DefaultJasperReportsContext.getInstance()));
		if (jrxmlFile.getName().toLowerCase().endsWith(".jasper")) {
			jrxmlFile = new File(jrxmlFile.getAbsolutePath().replace(".jasper", ".jrxml"));
		}
		if (jrxmlFile.exists() == false) {
			throw new Exception("jrxml file: " + jrxmlFile.getAbsolutePath() + " does not exist.");
		}
		if (replaceJrxmlRef) {
			String jrxmlContent = IOUtils.toString(new FileInputStream(jrxmlFile), "UTF-8");
			if (jrxmlContent.contains(".jrxml")) {
				// jrxml reference detected
				jrxmlContent = jrxmlContent.replace(".jrxml", ".jasper");
				jrxmlFile.renameTo(new File(jrxmlFile.getAbsolutePath()+".original"));
				Writer out = new FileWriter(jrxmlFile.getAbsolutePath());
				IOUtils.write(jrxmlContent.getBytes(), out, "UTF-8");
				out.flush();
				out.close();
			}
		}
		FileInputStream fis = new FileInputStream(jrxmlFile);
		InputSource source = new InputSource(fis);
		source.setEncoding("UTF-8");
		JasperDesign jasperDesign = loader.loadXML(source);
		return jasperDesign;
	}

	private void compileReport(String jrxmlFilePath, boolean isMainReport) throws Exception {
		File baseDir = new File(jrxmlFilePath).getParentFile();
		try {
			addPathToClasspath(baseDir);
		} catch (Exception e) {
			String message = "Failed to add dir " + baseDir.getAbsolutePath()
					+ " to classpath." + e.getMessage();
			throw new Exception(message, e);
		}
		final File currentJrxmlFile = new File(jrxmlFilePath);
		JasperReport jasperReport = null;
		String jasperFile = jrxmlFilePath.replace(".jrxml", ".jasper");
		if (needCompiling(jrxmlFilePath)) {
			// iterate through the report and find the sub reports and compile
			// them if needed
			// Compile sub reports
			JasperDesign jasperDesign = null;
			try {
				jasperDesign = retrieveJasperDesign(currentJrxmlFile);
			} catch (Exception e) {
				compileException = e;
				return;
			}
			if (fixLanguage) {
				jasperDesign.setLanguage(JasperReport.LANGUAGE_JAVA);
			}
			try {
				jasperReport = JasperCompileManager.compileReport(jasperDesign);
				System.out.println("Save jasper file " + jasperFile);
				JRSaver.saveObject(jasperReport, jasperFile);
			} catch (JRException e) {
				compileException = e;
				return;
			}
			reportMap.put(jrxmlFilePath, jasperReport);
			lastCompiledMap.put(jrxmlFilePath, System.currentTimeMillis());
		} else {
			jasperReport = (JasperReport) JRLoader.loadObjectFromFile(jasperFile);
		}
		if (isMainReport && jasperReport.getQuery() != null) {
			queryString = jasperReport.getQuery().getText();
		}
		if (jasperReport.getSectionType().equals(SectionTypeEnum.BAND)) {
			// this is a normal report
			// traverse through the report and gather the sub reports to compile them
			JRElementsVisitor.visitReport(jasperReport, new JRVisitor() {
				
				@Override
				public void visitSubreport(JRSubreport subreport) {
					String expression = subreport
							.getExpression()
							.getText();
					expression = expression.replace(".jasper", ".jrxml");
					StringTokenizer st = new StringTokenizer(expression, "\"");
					String subReportPath = null;
					while (st.hasMoreTokens()) {
						// take the last part of the name
						subReportPath = st.nextToken().trim();
					}
					try {
						File test = new File(subReportPath);
						if (test.isAbsolute()) {
							// recursive call to compile
							compileReport(subReportPath, false);
						} else {
							// recursive call to compile
							compileReport(currentJrxmlFile.getParent() + "/" + subReportPath, false);
						}
					} catch (Exception e) {
						compileException = e;
					}
				}

				@Override
				public void visitBreak(JRBreak breakElement) {}

				@Override
				public void visitChart(JRChart chart) {}

				@Override
				public void visitCrosstab(JRCrosstab crosstab) {}

				@Override
				public void visitElementGroup(JRElementGroup elementGroup) {}

				@Override
				public void visitEllipse(JREllipse ellipse) {}

				@Override
				public void visitFrame(JRFrame frame) {}

				@Override
				public void visitImage(JRImage image) {}

				@Override
				public void visitLine(JRLine line) {}

				@Override
				public void visitRectangle(JRRectangle rectangle) {}

				@Override
				public void visitStaticText(JRStaticText staticText) {}

				@Override
				public void visitTextField(JRTextField textField) {}

				@Override
				public void visitComponentElement(JRComponentElement componentElement) {}

				@Override
				public void visitGenericElement(JRGenericElement element) {}
				
			});
		} else {
			// this is a book and a book does not have subreports but SubreportPartComponents
			JRGroup[] groups = jasperReport.getGroups();
			for (JRGroup g : groups) {
				JRPart[] parts = g.getGroupHeaderSection().getParts();
				if (parts != null) {
					for (JRPart part : parts) {
						if (part.getComponent() instanceof SubreportPartComponent) {
							SubreportPartComponent sr = (SubreportPartComponent) part.getComponent();
							String expression = sr.getExpression().getText();
							StringTokenizer st = new StringTokenizer(expression, "\"");
							String subReportPath = null;
							while (st.hasMoreTokens()) {
								// take the last part of the name
								subReportPath = st.nextToken().trim();
							}
							compileReport(currentJrxmlFile.getParent() + "/" + subReportPath, false);
						}
					}
				}
				parts = g.getGroupFooterSection().getParts();
				if (parts != null) {
					for (JRPart part : parts) {
						if (part.getComponent() instanceof SubreportPartComponent) {
							SubreportPartComponent sr = (SubreportPartComponent) part.getComponent();
							String expression = sr.getExpression().getText();
							StringTokenizer st = new StringTokenizer(expression, "\"");
							String subReportPath = null;
							while (st.hasMoreTokens()) {
								// take the last part of the name
								subReportPath = st.nextToken().trim();
							}
							compileReport(currentJrxmlFile.getParent() + "/" + subReportPath, false);
						}
					}
				}
			}
		}
	}

	private String getJasperFileName(String jrxmlFileName) {
		int pos = jrxmlFileName.lastIndexOf('.');
		return jrxmlFileName.substring(0, pos) + ".jasper";
	}

	/**
	 * fills the compiled report with the data from the connection
	 * 
	 * @throws Exception
	 */
	public void fillReport() throws Exception {
		if (mainJasperReport == null) {
			mainJasperReport = (JasperReport)JRLoader.loadObjectFromFile(getJasperFileName(mainJrxmlFile.getAbsolutePath()));
		}
		// report compiled
		if (dbConnection != null) {
			if (dbConnection.isClosed()) {
				throw new Exception("Connection is closed!");
			}
			jasperPrint = JasperFillManager.fillReport(mainJasperReport,
					parameterMap, dbConnection);
		} else if (jrDataSource != null) {
			jasperPrint = JasperFillManager.fillReport(mainJasperReport,
					parameterMap, jrDataSource);
		} else {
			throw new Exception("No Connection or JRDataSource available to fill the report");
		}
		if (jasperPrint != null) {
			numberReportPages = jasperPrint.getPages().size();
		}
	}

	private void setupSpreadsheetConfiguration(AbstractXlsReportConfiguration reportConfiguration) {
		if (xlsDetectCellType != null) {
			reportConfiguration.setDetectCellType(xlsDetectCellType);
		}
		if (xlsIgnoreCellBackground != null) {
			reportConfiguration.setIgnoreCellBackground(xlsIgnoreCellBackground);
		}
		if (xlsOnePagePerSheet != null) {
			reportConfiguration.setOnePagePerSheet(xlsOnePagePerSheet);
		}
		if (xlsRemoveEmptySpaceBetweenColumns != null) {
			reportConfiguration.setRemoveEmptySpaceBetweenColumns(xlsRemoveEmptySpaceBetweenColumns);
		}
		if (xlsRemoveEmptySpaceBetweenRows != null) {
			reportConfiguration.setRemoveEmptySpaceBetweenRows(xlsRemoveEmptySpaceBetweenRows);
		}
		if (xlsWhitePageBackground != null) {
			reportConfiguration.setWhitePageBackground(xlsWhitePageBackground);
		}
	}
	
	private JRAbstractExporter<?, ?, ?, ?> createPdfExporter() {
		setupOutputFile("pdf");
		JRPdfExporter exporter = new JRPdfExporter();
		SimplePdfExporterConfiguration exportConfiguration = new SimplePdfExporterConfiguration();
		if (pdfCompressed != null) {
			exportConfiguration.setCompressed(pdfCompressed);
		}
		if (pdfCreateBatchModeBookmarks != null) {
			exportConfiguration.setCreatingBatchModeBookmarks(pdfCreateBatchModeBookmarks);
		}
		if (pdfOwnerPassword != null) {
			exportConfiguration.setOwnerPassword(pdfOwnerPassword);
		}
		if (pdfUserPassword != null) {
			exportConfiguration.setUserPassword(pdfUserPassword);
		}
		if (pdfEncrypted != null) {
			exportConfiguration.setEncrypted(pdfEncrypted);
			if (pdfEncryptionKey128Bit != null) {
				exportConfiguration.set128BitKey(pdfEncryptionKey128Bit);
			}
		}
		if (pdfTagged != null) {
			exportConfiguration.setTagged(pdfTagged);
		} else {
			exportConfiguration.setTagged(false);
		}
		if (pdfAuthor != null) {
			exportConfiguration.setMetadataAuthor(pdfAuthor);
		}
		if (pdfCreator != null) {
			exportConfiguration.setMetadataCreator(pdfCreator);
		}
		if (pdfKeywords != null) {
			exportConfiguration.setMetadataKeywords(pdfKeywords);
		}
		if (pdfTitle != null) {
			exportConfiguration.setMetadataTitle(pdfTitle);
		}
		if (pdfSubject != null) {
			exportConfiguration.setMetadataSubject(pdfSubject);
		}
		if (pdfVersion != null) {
			exportConfiguration.setPdfVersion(PdfVersionEnum.getByName(pdfVersion));
		}
		exporter.setConfiguration(exportConfiguration);
		// report configuration
		SimplePdfReportConfiguration reportConfiguration = new SimplePdfReportConfiguration();
		exporter.setConfiguration(reportConfiguration);
		// output configuration
		SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(outputFile);
		exporter.setExporterOutput(exporterOutput);
		return exporter;
	}

	private JRAbstractExporter<?, ?, ?, ?> createXlsExporter() {
		setupOutputFile("xls");
		JRXlsExporter exporter = new JRXlsExporter();
		SimpleXlsExporterConfiguration exportConfiguration = new SimpleXlsExporterConfiguration();
		if (xlsTemplateWorkbookFile != null) {
			exportConfiguration.setWorkbookTemplate(xlsTemplateWorkbookFile);
			exportConfiguration.setKeepWorkbookTemplateSheets(xlsKeepWorkbookTemplateSheets);
		}
		exporter.setConfiguration(exportConfiguration);
		SimpleXlsReportConfiguration reportConfiguration = new SimpleXlsReportConfiguration();
		setupSpreadsheetConfiguration(reportConfiguration);
		exporter.setConfiguration(reportConfiguration);
		// output configuration
		SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(outputFile);
		exporter.setExporterOutput(exporterOutput);
		return exporter;
	}

	private JRAbstractExporter<?, ?, ?, ?> createXlsxExporter() {
		setupOutputFile("xlsx");
		JRXlsxExporter exporter = new JRXlsxExporter();
		SimpleXlsxExporterConfiguration exportConfiguration = new SimpleXlsxExporterConfiguration();
		if (xlsTemplateWorkbookFile != null) {
			exportConfiguration.setWorkbookTemplate(xlsTemplateWorkbookFile);
			exportConfiguration.setKeepWorkbookTemplateSheets(xlsKeepWorkbookTemplateSheets);
		}
		exporter.setConfiguration(exportConfiguration);
		SimpleXlsxReportConfiguration reportConfiguration = new SimpleXlsxReportConfiguration();
		setupSpreadsheetConfiguration(reportConfiguration);
		exporter.setConfiguration(reportConfiguration);
		// output configuration
		SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(outputFile);
		exporter.setExporterOutput(exporterOutput);
		return exporter;
	}
	
	private JRAbstractExporter<?, ?, ?, ?> createHtmlExporter() {
		setupOutputFile("html");
		HtmlExporter exporter = new HtmlExporter();
		SimpleHtmlExporterConfiguration exportConfiguration = new SimpleHtmlExporterConfiguration();
		exporter.setConfiguration(exportConfiguration);
		SimpleHtmlReportConfiguration reportConfiguration = new SimpleHtmlReportConfiguration();
		exporter.setConfiguration(reportConfiguration);
		HtmlExporterOutput exporterOutput = new SimpleHtmlExporterOutput(outputFile, "UTF-8");
		exporter.setExporterOutput(exporterOutput);
		return exporter;
	}

	private JRAbstractExporter<?, ?, ?, ?> createCsvExporter() {
		setupOutputFile("csv");
		JRCsvExporter exporter = new JRCsvExporter();
		SimpleCsvExporterConfiguration exportConfiguration = new SimpleCsvExporterConfiguration();
		exportConfiguration.setFieldDelimiter("|");
		exportConfiguration.setRecordDelimiter("\n");
		exporter.setConfiguration(exportConfiguration);
		SimpleCsvReportConfiguration reportConfiguration = new SimpleCsvReportConfiguration();
		exporter.setConfiguration(reportConfiguration);
		WriterExporterOutput exporterOutput = new SimpleWriterExporterOutput(outputFile, "UTF-8");
		exporter.setExporterOutput(exporterOutput);
		return exporter;
	}

	private JRAbstractExporter<?, ?, ?, ?> createTxtExporter() {
		setupOutputFile("txt");
		JRTextExporter exporter = new JRTextExporter();
		SimpleTextExporterConfiguration exportConfiguration = new SimpleTextExporterConfiguration();
		exportConfiguration.setLineSeparator("\n");
		exportConfiguration.setPageSeparator("\n\n");
		exporter.setConfiguration(exportConfiguration);
		SimpleTextReportConfiguration reportConfiguration = new SimpleTextReportConfiguration();
		exporter.setConfiguration(reportConfiguration);
		WriterExporterOutput exporterOutput = new SimpleWriterExporterOutput(outputFile, "UTF-8");
		exporter.setExporterOutput(exporterOutput);
		return exporter;
	}
	
	private JRAbstractExporter<?, ?, ?, ?> createPptxExporter() {
		setupOutputFile("pptx");
		JRPptxExporter exporter = new JRPptxExporter();
		SimplePptxExporterConfiguration exportConfiguration = new SimplePptxExporterConfiguration();
		exporter.setConfiguration(exportConfiguration);
		SimplePptxReportConfiguration reportConfiguration = new SimplePptxReportConfiguration();
		exporter.setConfiguration(reportConfiguration);
		SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(outputFile);
		exporter.setExporterOutput(exporterOutput);
		return exporter;
	}
	
	private JRAbstractExporter<?, ?, ?, ?> createOdsExporter() {
		setupOutputFile("ods");
		JROdsExporter exporter = new JROdsExporter();
		SimpleOdsExporterConfiguration exportConfiguration = new SimpleOdsExporterConfiguration();
		if (xlsTemplateWorkbookFile != null) {
			exportConfiguration.setWorkbookTemplate(xlsTemplateWorkbookFile);
			exportConfiguration.setKeepWorkbookTemplateSheets(xlsKeepWorkbookTemplateSheets);
		}
		exporter.setConfiguration(exportConfiguration);
		SimpleOdsReportConfiguration reportConfiguration = new SimpleOdsReportConfiguration();
		setupSpreadsheetConfiguration(reportConfiguration);
		exporter.setConfiguration(reportConfiguration);
		SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(outputFile);
		exporter.setExporterOutput(exporterOutput);
		return exporter;
	}
	
	private JRAbstractExporter<?, ?, ?, ?> createOdtExporter() {
		setupOutputFile("odt");
		JROdtExporter exporter = new JROdtExporter();
		SimpleOdtExporterConfiguration exportConfiguration = new SimpleOdtExporterConfiguration();
		exporter.setConfiguration(exportConfiguration);
		SimpleOdtReportConfiguration reportConfiguration = new SimpleOdtReportConfiguration();
		exporter.setConfiguration(reportConfiguration);
		SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(outputFile);
		exporter.setExporterOutput(exporterOutput);
		return exporter;
	}

	private JRAbstractExporter<?, ?, ?, ?> createRtfExporter() {
		setupOutputFile("rtf");
		JRRtfExporter exporter = new JRRtfExporter();
		SimpleRtfExporterConfiguration exportConfiguration = new SimpleRtfExporterConfiguration();
		exporter.setConfiguration(exportConfiguration);
		SimpleRtfReportConfiguration reportConfiguration = new SimpleRtfReportConfiguration();
		exporter.setConfiguration(reportConfiguration);
		WriterExporterOutput exporterOutput = new SimpleWriterExporterOutput(outputFile, "UTF-8");
		exporter.setExporterOutput(exporterOutput);
		return exporter;
	}

	private JRAbstractExporter<?, ?, ?, ?> createDocxExporter() {
		setupOutputFile("docx");
		JRDocxExporter exporter = new JRDocxExporter();
		SimpleDocxExporterConfiguration exportConfiguration = new SimpleDocxExporterConfiguration();
		exporter.setConfiguration(exportConfiguration);
		SimpleDocxReportConfiguration reportConfiguration = new SimpleDocxReportConfiguration();
		exporter.setConfiguration(reportConfiguration);
		SimpleOutputStreamExporterOutput exporterOutput = new SimpleOutputStreamExporterOutput(outputFile);
		exporter.setExporterOutput(exporterOutput);
		return exporter;
	}
	
	/**
	 * creates the output formats add the appropriated file extension to the
	 * file
	 * 
	 * @throws Exception
	 */
	public void exportReport() throws Exception {
		if (outputFileNameWithoutExt == null) {
			throw new Exception("Option outputFileWithoutExt not set");
		}
		JRAbstractExporter<?, ?, ?, ?> exporter;
		if ("PDF".equalsIgnoreCase(outputFormat)) {
			exporter = createPdfExporter();
		} else if ("XLS".equalsIgnoreCase(outputFormat)) {
			exporter = createXlsExporter();
		} else if ("XLSX".equalsIgnoreCase(outputFormat)) {
			exporter = createXlsxExporter();
		} else if ("PPTX".equalsIgnoreCase(outputFormat)) {
			exporter = createPptxExporter();
		} else if ("ODS".equalsIgnoreCase(outputFormat)) {
			exporter = createOdsExporter();
		} else if ("RTF".equalsIgnoreCase(outputFormat)) {
			exporter = createRtfExporter();
		} else if ("HTML".equalsIgnoreCase(outputFormat)) {
			exporter = createHtmlExporter();
		} else if ("CSV".equalsIgnoreCase(outputFormat)) {
			exporter = createCsvExporter();
		} else if ("TXT".equalsIgnoreCase(outputFormat)) {
			exporter = createTxtExporter();
		} else if ("DOCX".equalsIgnoreCase(outputFormat)) {
			exporter = createDocxExporter();
		} else if ("ODT".equalsIgnoreCase(outputFormat)) {
			exporter = createOdtExporter();
		} else {
			throw new Exception(
					"exportReport failed: Unknown output file format:"
							+ outputFormat);
		}
		if (overwriteFiles == false && new File(outputFile).exists()) {
			throw new Exception("Output file " + outputFile
					+ " already exists!");
		}
		if (createDirIfNecessary) {
			Util.ensureParentExists(new File(outputFile));
		}
		exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
		exporter.exportReport();
	}
	
	/**
	 * set a report parameter value
	 * 
	 * @param parameterName
	 * @param value
	 */
	public void setParameterValue(String parameterName, Object value) {
		parameterMap.put(parameterName, value);
	}

	/**
	 * adds an value to a collection report parameter
	 * 
	 * @param parameterName
	 * @param value
	 */
	public void addValueToCollectionReportParameter(
			String parameterName,
			Object value) {
		@SuppressWarnings("unchecked")
		Collection<Object> list = (Collection<Object>) parameterMap
				.get(parameterName);
		if (list == null) {
			list = new ArrayList<Object>();
			parameterMap.put(parameterName, list);
		}
		list.add(value);
	}

	/**
	 * set the database connection for the report this connection will used as
	 * data source within the report
	 * 
	 * @param conn
	 */
	public void setConnection(Connection conn) {
		this.dbConnection = conn;
	}

	/**
	 * set the report parameter REPORT_LOCALE
	 * 
	 * @param locale
	 *            e.g. "de_DE" or "en_UK"
	 */
	public void setOutputLocale(String locale) {
		if (locale != null && locale.isEmpty() == false) {
			parameterMap.put("REPORT_LOCALE", new Locale(locale));
		}
	}

	/**
	 * set the values (a collection encoded as string) for a report parameter
	 * 
	 * @param parameterName
	 * @param valuesAsString
	 *            delimited values of the collection
	 * @param delimiter
	 *            delimiter to separate the values
	 * @param dataType
	 *            the data type of the parameter (the string values will be
	 *            converted into this type)
	 * @param pattern
	 *            date pattern
	 * @throws Exception
	 */
	public void setAndConvertParameterCollectionValues(
			String parameterName,
			String valuesAsString, 
			String delimiter, 
			String dataType,
			String pattern) throws Exception {
		parameterMap.put(parameterName, Util.convertToList(valuesAsString,
				dataType, delimiter, pattern));
	}

	/**
	 * set the value (as string) for a report parameter
	 * 
	 * @param parameterName
	 * @param valuesAsString
	 *            single value as string
	 * @param dataType
	 *            the data type of the parameter (the string values will be
	 *            converted into this type)
	 * @param pattern
	 *            date pattern
	 * @throws Exception
	 */
	public void setAndConvertParameterValue(
			String parameterName,
			Object valueAsString, 
			String dataType, 
			String pattern)
			throws Exception {
		parameterMap.put(parameterName,
				Util.convertToDatatype(String.valueOf(valueAsString), dataType, pattern));
	}

	public Object getParameterValue(String parameterName) {
		return parameterMap.get(parameterName);
	}

	public Map<String, Object> getParameterMap() {
		return parameterMap;
	}

	public boolean isCreateDirIfNecessary() {
		return createDirIfNecessary;
	}

	public void setCreateDirIfNecessary(boolean createDirIfNecessary) {
		this.createDirIfNecessary = createDirIfNecessary;
	}

	public boolean isOverwriteFiles() {
		return overwriteFiles;
	}

	public void setOverwriteFiles(boolean overwriteFiles) {
		this.overwriteFiles = overwriteFiles;
	}

	public void setXlsDetectCellType(boolean xlsDetectCellType) {
		this.xlsDetectCellType = xlsDetectCellType;
	}

	public void setXlsWhitePageBackground(boolean xlsWhitePageBackground) {
		this.xlsWhitePageBackground = xlsWhitePageBackground;
	}

	public void setXlsOnePagePerSheet(boolean xlsOnePagePerSheet) {
		this.xlsOnePagePerSheet = xlsOnePagePerSheet;
	}

	public void setXlsIgnoreCellBackground(boolean xlsIgnoreCellBackground) {
		this.xlsIgnoreCellBackground = xlsIgnoreCellBackground;
	}

	public void setXlsRemoveEmptySpaceBetweenRows(
			boolean xlsRemoveEmptySpaceBetweenRows) {
		this.xlsRemoveEmptySpaceBetweenRows = xlsRemoveEmptySpaceBetweenRows;
	}

	public void setXlsRemoveEmptySpaceBetweenColumns(
			boolean xlsRemoveEmptySpaceBetweenColumns) {
		this.xlsRemoveEmptySpaceBetweenColumns = xlsRemoveEmptySpaceBetweenColumns;
	}

	public int getNumberReportPages() {
		return numberReportPages;
	}

	public void setPdfPassword(String pdfPassword) {
		this.pdfOwnerPassword = pdfPassword;
	}

	public void setPdfTagged(boolean pdfTagged) {
		this.pdfTagged = pdfTagged;
	}

	public void setPdfSubject(String pdfSubject) {
		this.pdfSubject = pdfSubject;
	}

	public void setPdfTitle(String pdfTitle) {
		this.pdfTitle = pdfTitle;
	}

	public void setPdfVersion(String pdfVersion) {
		this.pdfVersion = pdfVersion;
	}

	public void setPdfUserPassword(String pdfUserPassword) {
		this.pdfUserPassword = pdfUserPassword;
	}

	public void setPdfOwnerPassword(String pdfOwnerPassword) {
		this.pdfOwnerPassword = pdfOwnerPassword;
	}

	public void setPdfCreator(String pdfCreator) {
		this.pdfCreator = pdfCreator;
	}

	public void setPdfKeywords(String pdfKeywords) {
		this.pdfKeywords = pdfKeywords;
	}

	public void setPdfEncrypted(boolean pdfEncrypted) {
		this.pdfEncrypted = pdfEncrypted;
	}

	public void setPdfAuthor(String pdfAuthor) {
		this.pdfAuthor = pdfAuthor;
	}

	public void setPdfCompressed(boolean compressed) {
		this.pdfCompressed = compressed;
	}

	public void setPdfCreateBatchModeBookmarks(
			Boolean pdfCreateBatchModeBookmarks) {
		this.pdfCreateBatchModeBookmarks = pdfCreateBatchModeBookmarks;
	}

	public void setPdfEncryptionKey128Bit(Boolean pdfEncryptionKey128Bit) {
		this.pdfEncryptionKey128Bit = pdfEncryptionKey128Bit;
	}

	public boolean isFixLanguage() {
		return fixLanguage;
	}

	public void setFixLanguage(boolean fixLanguage) {
		this.fixLanguage = fixLanguage;
	}

	private static void addPathToClasspath(File dir) throws Exception {
		URL url = dir.toURI().toURL();
		URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader
				.getSystemClassLoader();
		Class<?> urlClass = URLClassLoader.class;
		Method method = urlClass.getDeclaredMethod(
				"addURL",
				new Class[] { URL.class }); // already existing URLs will be ignored
		method.setAccessible(true);
		method.invoke(urlClassLoader, new Object[] { url });
	}
	
	/**
	 * set an dummy data source which returns always null values as much as needed
	 * @param numberRecords number of provided empty records
	 */
	public void setDummyDataSource(int numberRecords) {
		jrDataSource = new DummyDataSource(numberRecords);
	}
	
	/**
	 * must be called AFTER compileReport() !!
	 * @param filePath
	 * @param selectExpression
	 * @param datePattern
	 * @param numberPattern
	 * @throws Exception
	 */
	public void setXmlDataSource(String filePath, String selectExpression, String datePattern, String numberPattern) throws Exception {
		File file = new File(filePath);
		if (file.canRead() == false) {
			throw new Exception("XML File:" + file.getAbsolutePath() + " cannot be read or does not exist.");
		}
		JRXmlDataSource xmlDs = null;
		if (selectExpression != null && selectExpression.isEmpty() == false) {
			xmlDs = new JRXmlDataSource(file, selectExpression, false);
		} else if (queryString != null && queryString.isEmpty() == false) {
			xmlDs = new JRXmlDataSource(file, queryString, false);
		} else {
			xmlDs = new JRXmlDataSource(file, false);
		}
		if (datePattern != null && datePattern.isEmpty() == false) {
			xmlDs.setDatePattern(datePattern);
		}
		if (numberPattern != null && numberPattern.isEmpty() == false) {
			xmlDs.setNumberPattern(numberPattern);
		}
		parameterMap.put(JRXPathQueryExecuterFactory.XML_FILE, file);
		parameterMap.put(JRXPathQueryExecuterFactory.PARAMETER_XML_DATA_DOCUMENT, new DocumentProxy(file));
		jrDataSource = xmlDs;
	}

	public String getXlsTemplateWorkbookFile() {
		return xlsTemplateWorkbookFile;
	}

	public void setXlsTemplateWorkbookFile(String xlsTemplateWorkbookFile) {
		this.xlsTemplateWorkbookFile = xlsTemplateWorkbookFile;
	}

	public boolean isXlsKeepWorkbookTemplateSheets() {
		return xlsKeepWorkbookTemplateSheets;
	}

	public void setXlsKeepWorkbookTemplateSheets(
			boolean xlsKeepWorkbookTemplateSheets) {
		this.xlsKeepWorkbookTemplateSheets = xlsKeepWorkbookTemplateSheets;
	}

	public String getQueryString() {
		return queryString;
	}

	public boolean isPrintJRParameters() {
		return printJRParameters;
	}

	public void setPrintJRParameters(boolean printJRParameters) {
		this.printJRParameters = printJRParameters;
	}

	public void setReportProperty(String propName, String value) {
		mainJasperReport.setProperty(propName, value);
	}
	
}