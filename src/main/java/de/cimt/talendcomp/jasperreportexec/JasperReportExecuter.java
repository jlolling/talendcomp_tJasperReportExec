package de.cimt.talendcomp.jasperreportexec;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import net.sf.jasperreports.crosstabs.JRCrosstab;
import net.sf.jasperreports.engine.JRAbstractExporter;
import net.sf.jasperreports.engine.JRBreak;
import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRComponentElement;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JREllipse;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRFrame;
import net.sf.jasperreports.engine.JRGenericElement;
import net.sf.jasperreports.engine.JRImage;
import net.sf.jasperreports.engine.JRLine;
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
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsAbstractExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdsExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRPptxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRElementsVisitor;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

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
				jasperDesign = JRXmlLoader.load(currentJrxmlFile);
			} catch (Exception e) {
				compileException = e;
				return;
			}
			if (fixLanguage) {
				jasperDesign.setLanguage(JasperReport.LANGUAGE_JAVA); // to be
																		// sure
																		// if
																		// not
																		// set
																		// within
																		// the
																		// report
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
		// traverse through the report and gather the sub reports to compile them
		JRElementsVisitor.visitReport(jasperReport, new JRVisitor() {

			@Override
			public void visitSubreport(JRSubreport subreport) {
				String expression = subreport.getExpression()
						.getText()
						.replace(".jasper", ".jrxml");
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
			// .jasper file exists already
			if (dbConnection != null) {
				if (dbConnection.isClosed()) {
					throw new Exception("Connection is closed!");
				}
				jasperPrint = JasperFillManager.fillReport(
						getJasperFileName(mainJrxmlFile.getAbsolutePath()),
						parameterMap, dbConnection);
			} else if (jrDataSource != null) {
				jasperPrint = JasperFillManager.fillReport(
						getJasperFileName(mainJrxmlFile.getAbsolutePath()),
						parameterMap, jrDataSource);
			} else {
				throw new Exception("No Connection or JRDataSource available to fill the report");
			}
		} else {
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
		}
		if (jasperPrint != null) {
			numberReportPages = jasperPrint.getPages().size();
		}
	}

	private JRAbstractExporter createPdfExporter() {
		setupOutputFile("pdf");
		JRAbstractExporter exporter = new JRPdfExporter();
		if (pdfCompressed != null) {
			exporter.setParameter(
					JRPdfExporterParameter.IS_COMPRESSED,
					pdfCompressed);
		}
		if (pdfCreateBatchModeBookmarks != null) {
			exporter.setParameter(
					JRPdfExporterParameter.IS_CREATING_BATCH_MODE_BOOKMARKS,
					pdfCreateBatchModeBookmarks);
		}
		if (pdfOwnerPassword != null) {
			exporter.setParameter(JRPdfExporterParameter.OWNER_PASSWORD, pdfOwnerPassword);
		}
		if (pdfUserPassword != null) {
			exporter.setParameter(JRPdfExporterParameter.USER_PASSWORD, pdfUserPassword);
		}
		if (pdfEncrypted != null) {
			exporter.setParameter(JRPdfExporterParameter.IS_ENCRYPTED, pdfEncrypted);
			if (pdfEncryptionKey128Bit != null) {
				exporter.setParameter(JRPdfExporterParameter.IS_128_BIT_KEY, pdfEncryptionKey128Bit);
			}
		}
		if (pdfTagged != null) {
			exporter.setParameter(JRPdfExporterParameter.IS_TAGGED, pdfTagged);
		} else {
			exporter.setParameter(JRPdfExporterParameter.IS_TAGGED, false);
		}
		if (pdfAuthor != null) {
			exporter.setParameter(JRPdfExporterParameter.METADATA_AUTHOR, pdfAuthor);
		}
		if (pdfCreator != null) {
			exporter.setParameter(JRPdfExporterParameter.METADATA_CREATOR, pdfCreator);
		}
		if (pdfKeywords != null) {
			exporter.setParameter(JRPdfExporterParameter.METADATA_KEYWORDS,	pdfKeywords);
		}
		if (pdfTitle != null) {
			exporter.setParameter(JRPdfExporterParameter.METADATA_TITLE, pdfTitle);
		}
		if (pdfSubject != null) {
			exporter.setParameter(JRPdfExporterParameter.METADATA_SUBJECT, pdfSubject);
		}
		if (pdfVersion != null) {
			exporter.setParameter(
					JRPdfExporterParameter.PDF_VERSION, 
					pdfVersion.charAt(0));
		}
		return exporter;
	}

	private void setupXlsExporter(JRAbstractExporter exporter) {
		if (xlsDetectCellType != null) {
			exporter.setParameter(
					JRXlsAbstractExporterParameter.IS_DETECT_CELL_TYPE,
					xlsDetectCellType);
		}
		if (xlsIgnoreCellBackground != null) {
			exporter.setParameter(
					JRXlsAbstractExporterParameter.IS_IGNORE_CELL_BACKGROUND,
					xlsIgnoreCellBackground);
		}
		if (xlsOnePagePerSheet != null) {
			exporter.setParameter(
					JRXlsAbstractExporterParameter.IS_ONE_PAGE_PER_SHEET,
					xlsOnePagePerSheet);
		}
		if (xlsRemoveEmptySpaceBetweenColumns != null) {
			exporter.setParameter(
					JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS,
					xlsRemoveEmptySpaceBetweenColumns);
		}
		if (xlsRemoveEmptySpaceBetweenRows != null) {
			exporter.setParameter(
					JRXlsAbstractExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,
					xlsRemoveEmptySpaceBetweenRows);
		}
		if (xlsWhitePageBackground != null) {
			exporter.setParameter(
					JRXlsAbstractExporterParameter.IS_WHITE_PAGE_BACKGROUND,
					xlsWhitePageBackground);
		}
	}

	private JRAbstractExporter createXlsExporter() {
		setupOutputFile("xls");
		JRXlsExporter exporter = new JRXlsExporter();
		setupXlsExporter(exporter);
		if (xlsTemplateWorkbookFile != null) {
			exporter.setWorkbookTemplate(xlsTemplateWorkbookFile);
			exporter.setWorkbookTemplateKeepSheets(xlsKeepWorkbookTemplateSheets);
		}
		return exporter;
	}

	private JRAbstractExporter createXlsxExporter() {
		setupOutputFile("xlsx");
		JRXlsxExporter exporter = new JRXlsxExporter();
		setupXlsExporter(exporter);
		if (xlsTemplateWorkbookFile != null) {
			exporter.setWorkbookTemplate(xlsTemplateWorkbookFile);
			exporter.setWorkbookTemplateKeepSheets(xlsKeepWorkbookTemplateSheets);
		}
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
		JRAbstractExporter exporter;
		if ("PDF".equalsIgnoreCase(outputFormat)) {
			exporter = createPdfExporter();
		} else if ("XLS".equalsIgnoreCase(outputFormat)) {
			exporter = createXlsExporter();
		} else if ("XLSX".equalsIgnoreCase(outputFormat)) {
			exporter = createXlsxExporter();
		} else if ("PPTX".equalsIgnoreCase(outputFormat)) {
			exporter = new JRPptxExporter();
			setupOutputFile("pptx");
		} else if ("ODS".equalsIgnoreCase(outputFormat)) {
			setupOutputFile("ods");
			exporter = new JROdsExporter();
		} else if ("RTF".equalsIgnoreCase(outputFormat)) {
			setupOutputFile("rtf");
			exporter = new JRRtfExporter();
		} else if ("HTML".equalsIgnoreCase(outputFormat)) {
			setupOutputFile("html");
			exporter = new JRHtmlExporter();
			exporter.setParameter(JRHtmlExporterParameter.CHARACTER_ENCODING, "UTF-8");
		} else if ("CSV".equalsIgnoreCase(outputFormat)) {
			setupOutputFile("csv");
			exporter = new JRCsvExporter();
			exporter.setParameter(JRCsvExporterParameter.FIELD_DELIMITER, "|");
			exporter.setParameter(JRCsvExporterParameter.RECORD_DELIMITER, "\n");
			exporter.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "UTF-8");
		} else if ("TXT".equalsIgnoreCase(outputFormat)) {
			setupOutputFile("txt");
			exporter = new JRTextExporter();
			exporter.setParameter(JRTextExporterParameter.CHARACTER_ENCODING, "UTF-8");
		} else if ("DOCX".equalsIgnoreCase(outputFormat)) {
			setupOutputFile("docx");
			exporter = new JRDocxExporter();
		} else if ("ODT".equalsIgnoreCase(outputFormat)) {
			setupOutputFile("odt");
			exporter = new JROdtExporter();
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
		exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, outputFile);
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
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
			String valueAsString, 
			String dataType, 
			String pattern)
			throws Exception {
		parameterMap.put(parameterName,
				Util.convertToDatatype(valueAsString, dataType, pattern));
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
			throw new Exception("XML File:" + file.getAbsolutePath() + " cannot be read");
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

}