package de.cimt.talendcomp.jasperreportexec;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {
	
	/**
	 * converts a list of values into a collection
	 * @param valuesSeparated
	 * @param dataType String|date|Double|
	 * @param delimiter
	 * @return the list
	 */
	public static java.util.List<Object> convertToList(String valuesSeparated, String dataType, String delimiter, String pattern) throws Exception {
		java.util.List<Object> resultList = new java.util.ArrayList<Object>();
		java.util.StringTokenizer st = new java.util.StringTokenizer(valuesSeparated, delimiter);
		while (st.hasMoreElements()) {
			String value = st.nextToken();
			resultList.add(convertToDatatype(value, dataType, pattern));
		}
		return resultList;
	}
	
	public static Object convertToDatatype(String value, String dataType, String pattern) throws Exception {
		if (value != null && value.isEmpty() == false) {
			if ("String".equalsIgnoreCase(dataType)) {
				return value;
			} else if ("BigDecimal".equalsIgnoreCase(dataType)) {
				return convertToBigDecimal(value);
			} else if ("Boolean".equalsIgnoreCase(dataType)) {
				return convertToBoolean(value);
			} else if ("Date".equalsIgnoreCase(dataType)) {
				return convertToDate(value, pattern);
			} else if ("Double".equalsIgnoreCase(dataType)) {
				return convertToDouble(value);
			} else if ("Float".equalsIgnoreCase(dataType)) {
				return convertToFloat(value);
			} else if ("Int".equalsIgnoreCase(dataType) || "Integer".equalsIgnoreCase(dataType)) {
				return convertToInteger(value);
			} else if ("Long".equalsIgnoreCase(dataType)) {
				return convertToLong(value);
			} else if ("Short".equalsIgnoreCase(dataType)) {
				return convertToShort(value);
			} else if ("Timestamp".equalsIgnoreCase(dataType)) {
				return convertToTimestamp(value, pattern);
			} else {
				throw new Exception("Unsupported dataType:" + dataType);
			}
		} else {
			return null;
		}
	}
	
	/**
	 * concerts the string format into a Date
	 * @param dateString
	 * @param pattern
	 * @return the resulting Date
	 */
	public static Date convertToDate(String dateString, String pattern) throws Exception {
		if (dateString == null || dateString.isEmpty()) {
			return null;
		}
		if (pattern == null || pattern.isEmpty()) {
			throw new Exception("convertToDate failed: pattern cannot be null or empty");
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			return sdf.parse(dateString);
		} catch (Throwable t) {
			throw new Exception("Failed to convert string to date:" + t.getMessage(), t);
		}
	}
	
	public static Timestamp convertToTimestamp(String dateString, String pattern) throws Exception {
		Date date = convertToDate(dateString, pattern);
		if (date != null) {
			return new Timestamp(date.getTime());
		} else {
			return null;
		}
	}

	public static Boolean convertToBoolean(String value) throws Exception {
		if (value == null || value.isEmpty()) {
			return null;
		} else {
			if ("true".equals(value)) {
				return true;
			} else if ("false".equals(value)) {
				return false;
			} else {
				throw new Exception("Value:" + value + " is not a boolean value!");
			}
		}
	}

	public static Double convertToDouble(String value) throws Exception {
		if (value == null || value.isEmpty()) {
			return null;
		}
		NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
		return nf.parse(value).doubleValue();
	}

	public static Integer convertToInteger(String value) throws Exception {
		if (value == null || value.isEmpty()) {
			return null;
		}
		NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
		return nf.parse(value).intValue();
	}
	
	public static Float convertToFloat(String value) throws Exception {
		if (value == null || value.isEmpty()) {
			return null;
		}
		NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
		return nf.parse(value).floatValue();
	}

	public static Short convertToShort(String value) throws Exception {
		if (value == null || value.isEmpty()) {
			return null;
		}
		NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
		return nf.parse(value).shortValue();
	}

	public static Long convertToLong(String value) throws Exception {
		if (value == null || value.isEmpty()) {
			return null;
		}
		NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
		return nf.parse(value).longValue();
	}

	public static BigDecimal convertToBigDecimal(String value) throws Exception {
		if (value == null || value.isEmpty()) {
			return null;
		}
		try {
			return new BigDecimal(value);
		} catch (RuntimeException e) {
			throw new Exception("convertToBigDecimal:" + value + " failed:" + e.getMessage(), e);
		}
	}
	
	public static File ensureParentExists(File f) {
		File parent = f.getParentFile();
		if (parent.exists() == false) {
			parent.mkdirs();
		}
		return parent;
	}

}
