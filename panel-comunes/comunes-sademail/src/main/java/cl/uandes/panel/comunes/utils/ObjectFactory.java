package cl.uandes.panel.comunes.utils;

import java.io.SequenceInputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import cl.uandes.panel.comunes.servicios.dto.DatosKcoFunciones;
import cl.uandes.panel.comunes.servicios.dto.ResultadoFuncion;


/**
 * Crea objetos
 * 
 * @author fernando
 *
 */
public class ObjectFactory {

	private static Logger logger = Logger.getLogger(ObjectFactory.class);

	public static ResultadoFuncion createResultadoFuncion() {
		return new ResultadoFuncion();
	}

	public static ResultadoFuncion createResultadoFuncion(DatosKcoFunciones dto, BigDecimal key) {
		ResultadoFuncion res = new ResultadoFuncion();
		res.setKey(Integer.valueOf(key.intValue()));
		res.setFuncion(dto.getFuncion());
		res.setMaxThreads(dto.getMaxThread());
		res.setMinThreads(dto.getMinThread());
		res.setHoraComienzo(new Timestamp(new java.util.Date().getTime()));
		return res;
	}

	public static Integer toInteger(BigDecimal dato) {
		if (dato == null)
			return (Integer) null;
		try {
			return Integer.valueOf(dato.intValue());
		} catch (Exception e) {
			return null;
		}
	}

	public static BigDecimal toBigDecimal(Integer dato) {
		if (dato == null)
			return (BigDecimal) null;
		try {
			return BigDecimal.valueOf(dato.intValue());
		} catch (Exception e) {
			return null;
		}
	}

	public static BigDecimal toBigDecimal(String dato) {
		if (dato == null)
			return (BigDecimal) null;
		try {
			return BigDecimal.valueOf(Integer.valueOf(dato).intValue());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Saca del ResponseImpl el json que viene y lo devuelve como el objeto clss
	 * @param responseImpl
	 * @param clss
	 * @return
	 * @throws Exception
	 */
	public static Object procesaResponseImpl(ResponseImpl responseImpl, Class<?> clss) throws Exception {
		StringBuffer sb = new StringBuffer();
		java.io.SequenceInputStream in = (SequenceInputStream) responseImpl.getEntity();
		int incc;
		while ((incc = in.read()) != -1)
			sb.append((char) incc);
		String jsonString = sb.toString();

		jsonString = unescape2Html(jsonString);
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, false);

		try {
			return mapper.readValue(jsonString, clss);
		} catch (Exception e) {
			logger.error(String.format("json2java:ERROR al convertir a clase %s de json\n%s", clss, jsonString), e);
			throw e;
		}
	}

	public static Integer procesaResponseImpl(ResponseImpl responseImpl) {
		return responseImpl.getStatus();
	}

	public static String escape2Html(String data) {
		return StringEscapeUtils.escapeHtml(data);
	}
	
	public static String unescape2Html(String data) {
		if (data == null || data.isEmpty())
			return data;
		data = data.replace("&quot;", "'");
		String convertido = StringEscapeUtils.unescapeHtml(data);
		return convertido;
	}
}
