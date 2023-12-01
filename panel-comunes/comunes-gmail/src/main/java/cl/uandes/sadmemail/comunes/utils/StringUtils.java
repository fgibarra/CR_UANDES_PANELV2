package cl.uandes.sadmemail.comunes.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

@SuppressWarnings("deprecation")
public class StringUtils {

	public final static String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
	public final static DateFormat DEFAULT_DATE_FORMATTER = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
	public final static String DIAHORA_DATE_PATTERN = "dd-MM-yy HH:mm";
	public final static DateFormat DIAHORA_DATE_FORMATTER = new SimpleDateFormat(DIAHORA_DATE_PATTERN);
	public final static String YYYYMMDD_DATE_PATTERN = "yyyyMMdd";
	public final static DateFormat YYYYMMDD_DATE_FORMATTER = new SimpleDateFormat(YYYYMMDD_DATE_PATTERN);
	public final static String YYYYMMDDHHMMSS_DATE_PATTERN = "yyyyMMddHHmmss";
	public final static DateFormat YYYYMMDDHHMMSS_DATE_FORMATTER = new SimpleDateFormat(YYYYMMDDHHMMSS_DATE_PATTERN);
	
	public static String toString(Object obj) {
		if (obj == null || obj instanceof String)
			return (String) obj;

		if (obj instanceof Integer || obj instanceof Float || obj instanceof Double || obj instanceof BigDecimal
				|| obj instanceof Short)
			return obj.toString();

		if (obj instanceof Timestamp || obj instanceof java.sql.Date)
			return DEFAULT_DATE_FORMATTER.format(obj);

		return obj.toString();
	}

	public static Long toLong(Object obj) {
		if (obj == null)
			return (Long)null;

		if (obj instanceof String)
			try {
				return Long.valueOf((String)obj);
			} catch (NumberFormatException e) {
				return (Long)null;
			}
		if (obj instanceof Integer)
			return Long.valueOf(((Integer)obj).longValue());
		if (obj instanceof Float)
			return Long.valueOf(((Float)obj).longValue());
		if (obj instanceof Double)
			return Long.valueOf(((Double)obj).longValue());
		if (obj instanceof BigDecimal)
			return Long.valueOf(((BigDecimal)obj).longValue());
		if (obj instanceof Short)
			return Long.valueOf(((Short)obj).longValue());
		return (Long)obj;
	}

	public static Integer toInt(Object obj) {
		if (obj == null)
			return (Integer)null;

		if (obj instanceof String)
			try {
				return Integer.valueOf((String)obj);
			} catch (NumberFormatException e) {
				return (Integer)null;
			}
		if (obj instanceof Long)
			return (Integer)obj;
		if (obj instanceof Float)
			return Integer.valueOf(((Float)obj).intValue());
		if (obj instanceof Double)
			return Integer.valueOf(((Double)obj).intValue());
		if (obj instanceof BigDecimal)
			return Integer.valueOf(((BigDecimal)obj).intValue());
		if (obj instanceof Short)
			return Integer.valueOf(((Short)obj).intValue());
		return (Integer)obj;
	}

	public static java.util.Date toDate(Timestamp timestamp) {
		if (timestamp == null) return null;
		return new java.util.Date(timestamp.getTime());
	}
	
	public static java.util.Date toDate(String fecha) {
		if (fecha == null) return null;
		try {
			Date date  = YYYYMMDD_DATE_FORMATTER.parse(fecha);
			return date;
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Timestamp toTimeStamp(String fecha) {
		if (fecha == null) return null;
		try {
			Date date  = DIAHORA_DATE_FORMATTER.parse(fecha);
			return new Timestamp(date.getTime());
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Timestamp toTimeStamp(String fecha, String pattern) {
		if (fecha == null) return null;
		try {
			DateFormat formatter = new SimpleDateFormat(pattern);
			Date date  = formatter.parse(fecha);
			return new Timestamp(date.getTime());
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static String obj2String(Object obj) {
		if (obj == null) return null;
		if (obj instanceof String) return (String)obj;
		if (obj instanceof Double) return String.format("%d", ((Double)obj).longValue());
		if (obj instanceof Float) return String.format("%d", ((Float)obj).longValue());
		if (obj instanceof Integer) return String.format("%d", (Integer)obj);
		if (obj instanceof Long) return String.format("%d", (Long)obj);
		return obj.toString();
	}

	public static String toComaSeparator(Object[] datos) {
		StringBuffer sb = new StringBuffer();
		if (datos != null) {
			for (Object d : datos) {
				sb.append(d!=null?d.toString():"NULO").append(',');
			}
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

	public static String toComaSeparator(List<?> datos) {
		StringBuffer sb = new StringBuffer();
		if (datos != null) {
			for (Object d : datos) {
				sb.append(d!=null?d.toString():"NULO").append(',');
			}
			sb.setLength(sb.length() - 1);
		}
		return sb.toString();
	}

	public static String rellena(String valor, int largo, char c) {
        if (valor == null) valor="";
        if (valor.length() < largo) {
            StringBuffer sb = new StringBuffer();
            for (int j=largo - valor.length(); j>0; j--)
                sb.append('0');
            sb.append(valor);
            return sb.toString();
        }

        return valor;
	}

    /**
     * Dump hexadecimal y ascii del buffer
     * @param dato
     * @return
     */
    public static String dumpHexadecimal(byte[] dato) {
        StringBuffer sb = new StringBuffer();
        StringBuffer ascii = new StringBuffer().append('|');
        sb.append(
            "0--|--|--|--|--|--|--|--|--|--10-|--|--|--|--|--|--|--|--|--")
          .append("20-|--|--|--|--|--|--|--|--|--").append("30-|--|--|--|--|--|--|--|--|--|");

        sb.append('\n').append('|');

        for (int len = dato.length, i = 0, j = 39; i < len; i++, j--) {
            byte c = dato[i];
            String s = Integer.toHexString((int) c);

            if (s.length() < 2) {
                sb.append('0');
            } else if (s.length() > 2) {
                s = s.substring(s.length() - 2);
            }

            sb.append(s).append('|');

            // parte ascii
            char ch = (char) c;

            if (((int) ch >= 32) && ((int) ch < 128)) {
                ascii.append(' ').append(ch);
            } else {
                ascii.append("  ");
            }

            ascii.append('|');

            if (j <= 0) {
                sb.append('\n').append(ascii).append('\n').append('|');
                ascii = new StringBuffer().append('|');
                j = 40;
            }
        }

        sb.append('\n').append(ascii).append('\n').append('|'); // el resto de los 40

        return sb.toString();
    }
    
	public static String escape2Html(String data) {
		return StringEscapeUtils.escapeHtml4(data);
	}
	
	public static String unescape2Html(String data) {
		if (data == null || data.isEmpty())
			return data;
		data = data.replace("&quot;", "'");
		String convertido = StringEscapeUtils.unescapeHtml4(data);
		return convertido;
	}
	

    /**
     * Valida que sea un rut valido. 
     * Si es mexico valida el rfc
     * Si no viene el - de separacion entre el rut y el dv lo inserta
     * en la penultima posicion para dejarlo en el formato ddddddddd-v
     * @param rut
     * @return
     */
    public static boolean verificaRut(String rut) {
        int index = rut.indexOf('-');
        String nuevoRut;

        if (index < 0) {
            nuevoRut = rut.substring(0, rut.length() - 1) + "-" +
                rut.substring(rut.length() - 1, rut.length());
        } else {
            nuevoRut = rut;
        }

        return esRutValido(nuevoRut);
    }
    /**
     * devuelve true si el dv corresponde
     * @return
     * @param rut en formato "dddddddd-v"
     */
    public static boolean esRutValido(String rut) {
        // se trata de Chile o el default
        int indx = rut.indexOf('-');

        if (indx < 0) {
            return false;
        }

        String digitos = rut.substring(0, indx);

        if (!esNumerico(digitos)) {
            return false;
        }

        if (indx == (rut.length() - 1)) {
            return false;
        }

        if ((rut.length() - indx) > 2) {
            return false;
        }

        char dv = rut.charAt(indx + 1);

        if (!Character.isDigit(dv) && ((dv != 'K') && (dv != 'k'))) {
            return false;
        }

        byte[] valor = digitos.getBytes();

        if (valor.length == 0) {
            return false;
        }

        int[] op = { 2, 3, 4, 5, 6, 7 };
        int sum = 0;

        for (int i = valor.length - 1, j = 0; i >= 0; i--) {
            sum += (((int) valor[i] - 48) * op[j]);

            if (++j >= op.length) {
                j = 0;
            }
        }

        sum = 11 - (sum % 11);

        int v = (int) dv - 48;

        if (v == 0) {
            v = 11;
        } else if (v > 9) {
            v = 10;
        }

        return sum == v;
    }

    /**
     * Devuelve true si el String numero esd un número
     *
     * @return boolean
     */
    public static boolean esNumerico(String numero) {
        try {
            @SuppressWarnings("unused")
			Double num = new Double(numero);

            return true;
        } catch (NumberFormatException er) {
            return false;
        }
    }


    public static String formateaRut(String rut) {
        int i = 0;
        String nuevoRut = "";
        String ch = "";

        if ((rut != null) && !"".equals(rut)) {
            while (i < rut.length()) {
                ch = rut.substring(i, i + 1);

                if (!ch.equals(".") && !ch.equals("-")) {
                    nuevoRut = nuevoRut + ch;
                }

                i++;
            }

            if (!verificaRut(nuevoRut)) {
                nuevoRut = "";
            }
        }

        return nuevoRut;
    }

	public static String desformateaRut(String rut) {
		if (rut != null)
			rut = rut.trim().replaceAll("[^\\w]", "").replaceAll("[.-]", "").toUpperCase();
		return rut;
	}
	
	public static String getNombreCuenta(String email) {
		String nombreCuenta = null;
		if (email != null)
			try {
				nombreCuenta = email.substring(0, email.indexOf('@'));
			} catch (Exception e) {
				nombreCuenta = email;
			}
		return nombreCuenta;
	}


}
