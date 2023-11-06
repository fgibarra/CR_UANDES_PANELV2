package cl.uandes.panel.comunes.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

public class StringUtilities {

	private  static StringUtilities instance;
	private final String FECHA_HORA_FMT = "dd/MM/yy HH:mm:ss";
	
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
}
