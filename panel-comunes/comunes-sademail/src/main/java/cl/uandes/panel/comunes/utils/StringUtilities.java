package cl.uandes.panel.comunes.utils;

import java.math.BigDecimal;
import java.nio.CharBuffer;
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

	public String dumpMapKeys(Map<String, Object> headers) {
		StringBuffer sb = new StringBuffer();
		for (String key : headers.keySet()) {
			sb.append(String.format("|%s| ", key));
		}
		if (sb.length() > 1)
			sb.setLength(sb.length() - 1);
		return sb.toString();
	}
	  /**
	   * Saca los acentos, tildes, puntitos arriba, apostrofos
	   * @param valor
	   * @return
	   */
	  public String toLetrasAscii(String valor) {
	    if (valor == null || valor.length() == 0)
	      return valor;
	    int len = valor.length();
	    char chars[] = new char[len];
	    valor.toLowerCase().getChars(0, len, chars, 0);
	    StringBuffer newValor = new StringBuffer();
	    for (int i = 0; i < len; i++) {
	      char cc = chars[i];
	      if (cc == ' ') {
	    	  newValor.append(cc);
		      continue;
	      }
	      if (cc > 'z') {
	        @SuppressWarnings("unused")
			char europeos[] = { 'á', 'é', 'è', 'í', 'ó', 'ú', 'ö', 'ü', 'ñ' };
	        char europeoshex[] =
	        { 0xE1, 0xE9, 0xE8, 0xED, 0xF3, 0xFA, 0xF6, 0xFC, 0xF1 };
	        char corresp[] = { 'a', 'e', 'e', 'i', 'o', 'u', 'o', 'u', 'n' };
	        for (int j = 0; j < europeoshex.length; j++)
	          if (cc == europeoshex[j]) {
	            cc = corresp[j];
	            break;
	          }
	        if (cc < 'a' || cc > 'z') {
	          // no es letra europea
	          continue;
	        }
	      }
	      if ((cc >= 'A' && cc <= 'Z') || (cc >= 'a' && cc <= 'z') ||
	          (cc >= '0' && cc <= '9') || cc == '.' || cc == '_')
	        newValor.append(cc);
	    }
	    return newValor.toString();
	  }

	  /**
	   * devuelve representacion hex del string entregado como parametro
	   */
	  public String representacionHexadecimal(String dato) {
	    return representacionHexadecimal(dato.getBytes());
	  }

	  /**
	   * devuelve representacion hex del string entregado como parametro
	   */
	  public String representacionHexadecimal(StringBuffer dato) {
	    int len = dato.length();
	    char[] x = new char[len];

	    for (int i = 0; i < len; i++)
	      x[i] = dato.charAt(i);

	    return representacionHexadecimal(x);
	  }

	  /**
	   * devuelve representacion hex del string entregado como parametro
	   */
	  public String representacionHexadecimal(CharBuffer dato) {
	    int len = dato.length();
	    char[] x = new char[len];

	    for (int i = 0; i < len; i++)
	      x[i] = dato.charAt(i);

	    return representacionHexadecimal(x);
	  }

	  /**
	   * devuelve representacion hex del string entregado como parametro
	   */
	  public String representacionHexadecimal(byte[] dato, int len) {
	    byte[] x = new byte[len];

	    for (int i = 0; i < len; i++)
	      x[i] = dato[i];

	    return representacionHexadecimal(x);
	  }

	  /**
	   * devuelve representacion hex del string entregado como parametro
	   */
	  public String representacionHexadecimal(byte[] dato) {
	    StringBuffer sb = new StringBuffer().append('|');

	    for (int len = dato.length, i = 0; i < len; i++) {
	      byte c = dato[i];
	      String s = Integer.toHexString((int)c);

	      if (s.length() < 2) {
	        sb.append('0');
	      } else if (s.length() > 2) {
	        s = s.substring(s.length() - 2);
	      }

	      sb.append(s).append('|');
	    }

	    return sb.toString();
	  }

	  /**
	   * devuelve la representacion hexadecimal
	   * @param dato
	   * @return
	   */
	  public String representacionHexadecimal(char[] dato) {
	    return representacionHexadecimal(dato, dato.length);
	  }

	  /**
	   * devuelve la representacion hexadecimal
	   * @param dato
	   * @param count
	   * @return
	   */
	  public String representacionHexadecimal(char[] dato, int count) {
	    StringBuffer sb = new StringBuffer().append('|');

	    for (int len = count, i = 0; i < len; i++) {
	      char c = dato[i];
	      String s = Integer.toHexString((int)c);

	      if (s.length() < 2) {
	        sb.append('0');
	      } else if (s.length() > 2) {
	        s = s.substring(s.length() - 2);
	      }

	      sb.append(s).append('|');
	    }

	    return sb.toString();
	  }

}
