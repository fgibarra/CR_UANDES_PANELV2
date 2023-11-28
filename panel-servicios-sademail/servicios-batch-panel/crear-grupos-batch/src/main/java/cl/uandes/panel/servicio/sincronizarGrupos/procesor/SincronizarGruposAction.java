package cl.uandes.panel.servicio.sincronizarGrupos.procesor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.batch.ContadoresSincronizarGrupos;
import cl.uandes.panel.comunes.utils.ObjectFactory;
import cl.uandes.panel.servicio.sincronizarGrupos.bean.CountThreads;
import cl.uandes.sadmemail.comunes.gmail.json.MemberRequest;
import cl.uandes.sadmemail.comunes.gmail.json.MembersRequest;
import cl.uandes.sadmemail.comunes.gmail.json.MembersResponse;
import cl.uandes.sadmemail.comunes.google.api.services.Member;
import cl.uandes.sadmemail.comunes.google.api.services.Members;

public class SincronizarGruposAction implements Processor {

    @PropertyInject(value = "crear-grupos-gmail.uri-gmailServices", defaultValue="http://localhost:8181/cxf/ESB/panel/gmailServices")
	private String gmailServices;

    @EndpointInject(uri = "cxfrs:bean:rsRetrieveAllMembers?timeout=-1")
	ProducerTemplate retrieveAllMembers;
	String templateRetrieveAllMembers = "%s/members/retrieveMembers";
	
    @EndpointInject(uri = "cxfrs:bean:rsDeleteMember?timeout=-1")
	ProducerTemplate deleteMember;
	String templateDeleteMember = "%s/member/deleteMember";
	
	@EndpointInject(uri = "sql:classpath:sql_s/qryMiembro.sql?dataSource=#bannerDataSource")
	ProducerTemplate qryMiembro;

	private Logger logger = Logger.getLogger(getClass());
	private String nextPageToken;
	private ContadoresSincronizarGrupos contadores = null;
	
	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		String groupName = (String) message.getHeader("grupoGmail");
		
		contadores = (ContadoresSincronizarGrupos)message.getHeader("contadoresSincronizarGrupos");
		contadores.incProcesados();
		logger.info(String.format("SincronizarGruposAction: groupName %s", groupName));
		setNextPageToken(null);
		do {
			List<Member> listaMiembros = getListaMiembros(groupName, exchange);
			for (Member member : listaMiembros) {
				procesaMember(member, groupName, exchange);
			}
		} while (getNextPageToken() != null);
		Integer valor = ((CountThreads)message.getHeader("countThread")).decCounter();
		logger.info(String.format("SincronizarGruposAction: countThread=%d",valor));
	}

	private void procesaMember(Member member, String groupName, Exchange exchange) {
		String email = member.getEmail();
		if (email != null) {
			String loginName = email.substring(0, email.indexOf('@'));
			if (!estaEnNapMiembros(loginName, groupName, exchange)) {
				sacaMiembroDeGmail(email, groupName, exchange);
				contadores.incSacados();
			}
		}
	}

	private void sacaMiembroDeGmail(String email, String groupName, Exchange exchange) {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateDeleteMember,getGmailServices()));
		headers.put("CamelHttpMethod", "POST");
		MemberRequest body = new MemberRequest(groupName, email);
		try {
			deleteMember.requestBodyAndHeaders(body, headers);
		} catch (Exception e) {
			contadores.incErrores();
			logger.error(String.format("ERROR: email %s groupName %s", email, groupName), e);
		}
	}

	private boolean estaEnNapMiembros(String email, String groupName, Exchange exchange) {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("loginName", email);
		headers.put("groupName", groupName);
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> datos = (List<Map<String, Object>>) qryMiembro.requestBodyAndHeaders(null, headers);
		if (datos == null || datos.size() == 0)
			return false;
		String rut = (String) datos.get(0).get("ID_MIEMBRO");
		logger.info(String.format("loginName %s rut %s", email, rut));
		return true;
	}

	private List<Member> getListaMiembros(String groupName, Exchange exchange) {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateRetrieveAllMembers,getGmailServices()));
		headers.put("CamelHttpMethod", "POST");
		MembersRequest body = factoryMembersRequest(groupName);
		try {
			MembersResponse response = (MembersResponse) ObjectFactory.procesaResponseImpl((ResponseImpl) retrieveAllMembers.requestBodyAndHeaders(body, headers), MembersResponse.class);
			if (response.getCodigo() == 0) {
				Members members = response.getMembers();
				setNextPageToken(members.getNextPageToken());
				return members.getListaMembers();
			} else
				logger.error(String.format("ERROR: groupName %s msg %s", groupName, response.getMensaje()));
		} catch (Exception e ) {
			logger.error(String.format("ERROR: groupName %s", groupName), e);
			contadores.incErrores();
		}
		return new ArrayList<Member>();
	}

	private MembersRequest factoryMembersRequest(String groupName) {
		MembersRequest request = new MembersRequest(groupName, getNextPageToken());
		return request;
	}

	public String getGmailServices() {
		return gmailServices;
	}

	public void setGmailServices(String gmailServices) {
		this.gmailServices = gmailServices;
	}

	public String getNextPageToken() {
		return nextPageToken;
	}

	public void setNextPageToken(String nextPageToken) {
		this.nextPageToken = nextPageToken;
	}

}
