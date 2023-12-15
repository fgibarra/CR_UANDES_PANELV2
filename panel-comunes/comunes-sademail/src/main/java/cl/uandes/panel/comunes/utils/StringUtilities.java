package cl.uandes.panel.comunes.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

public class StringUtilities {

	private  static StringUtilities instance;
	private final String FECHA_HORA_FMT = "dd/MM/yy HH:mm:ss";
	private final String LONG_DEFAULT_FMT = "#,##0";
	
	private Logger logger = Logger.getLogger(getClass());
	
	public static StringUtilities getInstance() {
		if (instance == null) instance = new StringUtilities();
		return instance;
	}

	public String toString(java.sql.Timestamp timestamp) {
		return toString(timestamp, FECHA_HORA_FMT);
	}
	
	public String toString(java.sql.Timestamp timestamp, String fmt) {
		if (timestamp == null) return null;
		SimpleDateFormat sdf = new SimpleDateFormat(fmt, new java.util.Locale("es", "CL"));
		try {
			return sdf.format(new java.util.Date(timestamp.getTime()));
		} catch (Exception e) {
			logger.error(String.format("toString: %s no es un valor convertible a String. Error (%s)", 
					timestamp.toString(), e.getMessage()));
			return null;
		}
	}

	public Integer toInteger(String valor) {
		if (valor == null) return null;
		try {
			return Integer.valueOf(valor);
		} catch (Exception e) {
			logger.error(String.format("toInteger: %s no es un valor convertible a Integer. Error (%s)", 
					valor.toString(), e.getMessage()));
		}
		return null;
	}

	public Object toBigDecimal(Integer valor) {
		if (valor == null) return null;
		try {
			return BigDecimal.valueOf(valor.longValue());
		} catch (Exception e) {
			logger.error(String.format("toBigDecimal: %s no es un valor convertible a Integer. Error (%s)", 
					valor.toString(), e.getMessage()));
		}
		return null;
	}

	public String toString(Date date) {
		return toString(date, FECHA_HORA_FMT);
	}
	
	public String toString(Date date, String fmt) {
		if (date == null) return null;
		SimpleDateFormat sdf = new SimpleDateFormat(fmt, new java.util.Locale("es", "CL"));
		try {
			return sdf.format(date);
		} catch (Exception e) {
			logger.error(String.format("toString: %s no es un valor convertible a String. Error (%s)", 
					date.toString(), e.getMessage()));
			return null;
		}
	}
	
	public String format(Object dato) {
		if (dato == null) return null;
		if (dato instanceof Long)
			return format(dato, LONG_DEFAULT_FMT);
		throw new RuntimeException(String.format("Objeto %d de clase %s No tiene definido un PATTERN", dato, dato.getClass().getName()));
	}

	public String format(Object dato, String fmt) {
		if (dato == null) return null;
		if (dato instanceof Integer || dato instanceof Long ||dato instanceof Float || dato instanceof Double) {
			try {
				DecimalFormat df = new DecimalFormat(fmt);
				return df.format(dato);
			} catch (Exception  e) {
				throw new RuntimeException(e);
			}
		}
		throw new RuntimeException(String.format("Objeto %d de clase %s No tiene definido un FORMATTER", dato, dato.getClass().getName()));
	}

	public String dumpMap(Map<String, Object> headers) {
		StringBuffer sb = new StringBuffer();
		for (String key : headers.keySet()) {
			sb.append(String.format("%s = |%s| - ", key, headers.get(key)));
		}
		if (sb.length() > 3)
			sb.setLength(sb.length() - 3);
		return sb.toString();
	}
}
