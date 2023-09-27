package cl.uandes.panel.sincronizaGrupos.procesor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.log4j.Logger;

import cl.uandes.panel.sincronizaGrupos.dto.SincronizaGruposDTO;
import cl.uandes.sadmemail.comunes.gmail.json.MemberRequest;
import cl.uandes.sadmemail.comunes.gmail.json.MemberResponse;
import cl.uandes.sadmemail.comunes.utils.JSonUtilities;

public class ProcesaCuenta implements Processor {

	@EndpointInject(uri = "cxfrs:bean:retrieveMember") // recupera de Gmail miembros del grupo
	ProducerTemplate apiRetrieveMember;
	@EndpointInject(uri = "cxfrs:bean:addMember") // add miembro al grupo
	ProducerTemplate apiAddMember;
	
	private Logger logger = Logger.getLogger(getClass());

	private final String templateUriRetrieveMember = "http://localhost:8181/cxf/ESB/panel/gmailServices/member/retrieve";
	private final String templateUriAddMember = "http://localhost:8181/cxf/ESB/panel/gmailServices/member/addMember";
	@Override
	public void process(Exchange exchange) throws Exception {
		@SuppressWarnings("unchecked")
		List<SincronizaGruposDTO> lista = (List<SincronizaGruposDTO>) exchange.getIn().getHeader("listaCuentasGrupo");
		SincronizaGruposDTO dto = lista.remove(0);
		logger.info(String.format("a procesar dto: %s", dto));
		String resultadoPC = "OK";
		String accion = null;
		Integer countProcesados = (Integer)exchange.getIn().getHeader("countProcesados") + 1;
		
		MemberRequest request = new MemberRequest(dto.getGroupName(), dto.getLoginName());
		if (!estaEnGmail(request)) {
			MemberResponse response = creaMember(request);
			logger.info(String.format("miembro agregado: %b", response.getCodigo() == 0));
			if (response.getCodigo() != 0)
				resultadoPC = "KO";
			else {
				accion = "agregado";
				Integer countAgregados = (Integer)exchange.getIn().getHeader("countAgregados") + 1;
				exchange.getIn().setHeader("countAgregados", countAgregados);
			}
		}
		exchange.getIn().setHeader("countProcesados", countProcesados);
		exchange.getIn().setHeader("resultadoPC", resultadoPC);
		exchange.getIn().setHeader("keyDTO", dto.getKey());
		exchange.getIn().setHeader("accion", accion);
	}
	
	private boolean estaEnGmail(MemberRequest request) {
		final Map<String,Object> headers = new HashMap<String,Object>();
		
		// consultarlo a gmail
		headers.clear();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, templateUriRetrieveMember);
		headers.put("CamelHttpMethod", "POST");
		ResponseImpl response;
		try {
			response = (ResponseImpl) apiRetrieveMember.requestBodyAndHeaders(request, headers );
			if (response.getStatus() < 300) {
				try {
					StringBuffer sb = new StringBuffer();
					java.io.SequenceInputStream in = (java.io.SequenceInputStream) response.getEntity();
					int incc;
					while ((incc=in.read()) != -1)
						sb.append((char)incc);
					String jsonString =  sb.toString();
					MemberResponse res = (MemberResponse)JSonUtilities.getInstance().json2java(jsonString, 
											MemberResponse.class, false);
					logger.info(String.format("estaEnGmail: %b", res.getCodigo() == 0));
					return res.getCodigo() == 0;
				} catch (Exception e) {
					logger.error("crearEnGmail", e);
					return false;
				}
		}
		else
			return false;
		} catch (CamelExecutionException e) {
			logger.error(e.getMessage());
			return false;
		}
		
	}

	private MemberResponse creaMember(MemberRequest request) {
		final Map<String,Object> headers = new HashMap<String,Object>();
		
		// consultarlo a gmail
		headers.clear();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, templateUriAddMember);
		headers.put("CamelHttpMethod", "POST");
		ResponseImpl response;
		try {
			response = (ResponseImpl) apiAddMember.requestBodyAndHeaders(request, headers );
			if (response.getStatus() < 300) {
				try {
					StringBuffer sb = new StringBuffer();
					java.io.SequenceInputStream in = (java.io.SequenceInputStream) response.getEntity();
					int incc;
					while ((incc=in.read()) != -1)
						sb.append((char)incc);
					String jsonString =  sb.toString();
					return (MemberResponse)JSonUtilities.getInstance().json2java(jsonString, 
											MemberResponse.class, false);
				} catch (Exception e) {
					logger.error("crearEnGmail", e);
					return new MemberResponse(-1, e.getMessage(), null);
				}
			} else {
				return new MemberResponse(-1, response.getStatusInfo()!=null?response.getStatusInfo().getReasonPhrase():String.format("status: %d", response.getStatus()), null);
			}
		} catch (CamelExecutionException e) {
			logger.error(e.getMessage());
			return new MemberResponse(-1, e.getMessage(), null);
		}
	}

}
