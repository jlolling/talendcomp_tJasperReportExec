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
	
	public static Object convertToDatatype(Object value, String targetDataType, String pattern) throws Exception {
		if (value != null) {
			if ("String".equalsIgnoreCase(targetDataType)) {
				return convertToString(value, pattern);
			} else if ("BigDecimal".equalsIgnoreCase(targetDataType)) {
				return convertToBigDecimal(value);
			} else if ("Boolean".equalsIgnoreCase(targetDataType)) {
				return convertToBoolean(value);
			} else if ("Date".equalsIgnoreCase(targetDataType)) {
				return convertToDate(value, pattern);
			} else if ("Double".equalsIgnoreCase(targetDataType)) {
				return convertToDouble(value);
			} else if ("Float".equalsIgnoreCase(targetDataType)) {
				return convertToFloat(value);
			} else if ("Int".equalsIgnoreCase(targetDataType) || "Integer".equalsIgnoreCase(targetDataType)) {
				return convertToInteger(value);
			} else if ("Long".equalsIgnoreCase(targetDataType)) {
				return convertToLong(value);
			} else if ("Short".equalsIgnoreCase(targetDataType)) {
				return convertToShort(value);
			} else if ("Timestamp".equalsIgnoreCase(targetDataType)) {
				return convertToTimestamp(value, pattern);
			} else {
				throw new Exception("Unsupported dataType:" + targetDataType);
			}
		} else {
			return null;
		}
	}
	
	public static String convertToString(Object value, String pattern) {
		String s = null;
		if (value instanceof String) {
			return (String) value;
		} else if (value instanceof Number) {
			return NumberFormat.getInstance().format(value);
		} else if (value instanceof Date) {
			return new SimpleDateFormat(pattern).format((Date) value);
		} else if (value != null) {
			return String.valueOf(value);
		}
		return s;
	}
	
	/**
	 * concerts the string format into a Date
	 * @param dateString
	 * @param pattern
	 * @return the resulting Date
	 */
	public static Date convertToDate(Object value, String pattern) throws Exception {
		if (value == null) {
			return null;
		} else if (value instanceof String) {
			if (pattern == null || pattern.isEmpty()) {
				throw new Exception("convertToDate failed: pattern cannot be null or empty");
			}
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(pattern);
				return sdf.parse((String) value);
			} catch (Throwable t) {
				throw new Exception("Failed to convert string to date:" + t.getMessage(), t);
			}
		} else if (value instanceof Date) {
			return (Date) value;
		} else {
			throw new Exception("Type: " + value.getClass().getName() + " cannot be converted to Date");
		}
	}
	
	public static Timestamp convertToTimestamp(Object value, String pattern) throws Exception {
		Date date = convertToDate(value, pattern);
		if (date != null) {
			return new Timestamp(date.getTime());
		} else {
			return null;
		}
	}

	public static Boolean convertToBoolean(Object value) throws Exception {
		if (value == null) {
			return null;
		} else {
			if (value instanceof Boolean) {
				return (Boolean) value;
			} else if (value instanceof String) {
				if ("true".equals(value)) {
					return true;
				} else if ("false".equals(value)) {
					return false;
				} else {
					throw new Exception("Value:" + value + " is not a boolean value!");
				}
			} else {
				throw new Exception("Value:" + value + " is not a boolean value!");
			}
		}
	}

	public static Double convertToDouble(Object value) throws Exception {
		if (value == null) {
			return null;
		} else if (value instanceof Number) {
			return ((Number) value).doubleValue();
		} else if (value instanceof String) {
			NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
			return nf.parse((String) value).doubleValue();
		} else {
			throw new Exception("Type: " + value.getClass().getName() + " cannot be converted to Double");
		}
	}

	public static Float convertToFloat(Object value) throws Exception {
		if (value == null) {
			return null;
		} else if (value instanceof Number) {
			return ((Number) value).floatValue();
		} else if (value instanceof String) {
			NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
			return nf.parse((String) value).floatValue();
		} else {
			throw new Exception("Type: " + value.getClass().getName() + " cannot be converted to Float");
		}
	}
	
	public static Integer convertToInteger(Object value) throws Exception {
		if (value == null) {
			return null;
		} else if (value instanceof Number) {
			return ((Number) value).intValue();
		} else if (value instanceof String) {
			NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
			return nf.parse((String) value).intValue();
		} else {
			throw new Exception("Type: " + value.getClass().getName() + " cannot be converted to Integer");
		}
	}

	public static Short convertToShort(Object value) throws Exception {
		if (value == null) {
			return null;
		} else if (value instanceof Number) {
			return ((Number) value).shortValue();
		} else if (value instanceof String) {
			NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
			return nf.parse((String) value).shortValue();
		} else {
			throw new Exception("Type: " + value.getClass().getName() + " cannot be converted to Short");
		}
	}

	public static Long convertToLong(Object value) throws Exception {
		if (value == null) {
			return null;
		} else if (value instanceof Long) {
			return (Long) value;
		} else if (value instanceof Number) {
			return ((Number) value).longValue();
		} else if (value instanceof String) {
			NumberFormat nf = NumberFormat.getInstance(Locale.ENGLISH);
			return nf.parse((String) value).longValue();
		} else {
			throw new Exception("Type: " + value.getClass().getName() + " cannot be converted to Long");
		}
	}

	public static BigDecimal convertToBigDecimal(Object value) throws Exception {
		try {
			if (value == null) {
				return null;
			} else if (value instanceof BigDecimal) {
				return (BigDecimal) value;
			} else if (value instanceof Long) {
				return new BigDecimal((Long) value);
			} else if (value instanceof Integer) {
				return new BigDecimal((Integer) value);
			} else if (value instanceof String) {
				if (((String) value).isEmpty()) {
					return null;
				}
				return new BigDecimal((String) value);
			} else {
				throw new Exception("Type: " + value.getClass().getName() + " cannot be converted to BigDecimal");
			}
		} catch (Throwable e) {
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
