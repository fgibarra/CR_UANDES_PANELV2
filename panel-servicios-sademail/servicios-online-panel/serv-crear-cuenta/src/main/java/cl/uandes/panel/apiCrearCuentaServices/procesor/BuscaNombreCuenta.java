package cl.uandes.panel.apiCrearCuentaServices.procesor;

import java.io.SequenceInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.log4j.Logger;

import cl.uandes.panel.apiCrearCuentaServices.dto.DatosMiCuentasGmailDTO;
import cl.uandes.panel.apiCrearCuentaServices.dto.DatosSpridenDTO;
import cl.uandes.sadmemail.comunes.gmail.json.AliasResponse;
import cl.uandes.sadmemail.comunes.gmail.json.UserResponse;
import cl.uandes.sadmemail.comunes.utils.JSonUtilities;

public class BuscaNombreCuenta implements Processor {

	private Logger logger = Logger.getLogger(getClass());
	
	/**
	 * buscar un loginName que no exista un nickname o una cuenta con ese nombre
	 * en ${header.datosSpriden} queda actualizado el nombre a usar
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		DatosSpridenDTO datosSpriden = (DatosSpridenDTO)exchange.getIn().getHeader("datosSpriden");
		String loginName = (String)exchange.getIn().getHeader("loginName");
		DatosMiCuentasGmailDTO datosMiCGdto = new DatosMiCuentasGmailDTO(datosSpriden, loginName);
		Integer secuence = (Integer)exchange.getIn().getHeader("lastSecuence");
		String loginNameBase = datosSpriden.getLoginName();
		boolean notFound = false;
		// loop hasta no encontrar
		do {
			// buscar un nickname
			if (hayNickName(datosMiCGdto)) {
				// si existe incrementar la secuencia en 1
				secuence++;
				datosMiCGdto.setLoginName(String.format("%s%d", loginNameBase, secuence));
				notFound = true;
				continue;
			}
			// buscar una cuenta
			if (hayCuenta(datosMiCGdto)) {
				// si existe incrementar la secuencia en 1
				secuence++;
				datosMiCGdto.setLoginName(String.format("%s%d", loginNameBase, secuence));
				notFound = true;
				continue;
			}
			notFound = false;
		} while (notFound);
		
		exchange.getIn().setHeader("datosMiCuentasGmail", datosMiCGdto);
	}

	@EndpointInject(uri = "cxfrs:bean:consultaNickNameGMail") // recupera de Gmail nickName
	ProducerTemplate apiHayNickName;
	private final String templateUriHayNickName = "http://localhost:8181/cxf/ESB/panel/gmailServices/nickName/retrieve/%s";
	private boolean hayNickName(DatosMiCuentasGmailDTO datosMiCGdto) {
		final Map<String,Object> headers = new HashMap<String,Object>();
		
		// consultarlo a gmail
		headers.clear();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateUriHayNickName, datosMiCGdto.getLoginName()));
		headers.put("CamelHttpMethod", "GET");
		try {
			AliasResponse obj = (AliasResponse)procesaResponseImpl((ResponseImpl)apiHayNickName.requestBodyAndHeaders(null, headers), AliasResponse.class);
			return obj.getHayAlias();
		} catch (Exception e) {
			logger.error("hayNickName", e);
		}
		return false;
	}

	@EndpointInject(uri = "cxfrs:bean:consultaGMail") // recupera de Gmail cuenta
	ProducerTemplate apiHayCuenta;
	private final String templateUriHayCuenta = "http://localhost:8181/cxf/ESB/panel/gmailServices/user/retrieve/%s";
	private boolean hayCuenta(DatosMiCuentasGmailDTO datosMiCGdto) {
		final Map<String,Object> headers = new HashMap<String,Object>();
		
		// consultarlo a gmail
		headers.clear();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateUriHayCuenta, datosMiCGdto.getLoginName()));
		headers.put("CamelHttpMethod", "GET");
		try {
			UserResponse obj = (UserResponse)procesaResponseImpl((ResponseImpl)apiHayCuenta.requestBodyAndHeaders(null, headers), UserResponse.class);
			return obj.getCodigo() < 0 ? Boolean.FALSE : Boolean.TRUE;
		} catch (Exception e) {
			logger.error("hayCuenta", e);
		}
		return false;
	}

	protected Object procesaResponseImpl(ResponseImpl responseImpl, Class<?> clss) throws Exception {
		StringBuffer sb = new StringBuffer();
		java.io.SequenceInputStream in = (SequenceInputStream) responseImpl.getEntity();
		int incc;
		while ((incc=in.read()) != -1)
			sb.append((char)incc);
		String jsonString =  sb.toString();
		
		int status = responseImpl.getStatus();
		logger.info(String.format("procesaResponseImpl: Http status: %d errorsExist [%b]", status, jsonString.contains("errorsExist")));
		if (status >= 300) {
			return null;
		}

		return JSonUtilities.getInstance().json2java(jsonString, clss, false);
	}
}
