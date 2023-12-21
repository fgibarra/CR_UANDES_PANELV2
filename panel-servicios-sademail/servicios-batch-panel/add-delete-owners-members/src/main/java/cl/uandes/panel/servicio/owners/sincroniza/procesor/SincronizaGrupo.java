/**
 * 
 */
package cl.uandes.panel.servicio.owners.sincroniza.procesor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.bean.RegistrosComunes;
import cl.uandes.panel.comunes.json.batch.ContadoresAsignarOwners;
import cl.uandes.panel.comunes.servicios.dto.ResultadoFuncion;
import cl.uandes.panel.comunes.utils.CountThreads;
import cl.uandes.panel.comunes.utils.ObjectFactory;
import cl.uandes.panel.servicio.owners.dto.NapGrupoOwnerDTO;
import cl.uandes.sadmemail.comunes.gmail.json.MemberRequest;
import cl.uandes.sadmemail.comunes.gmail.json.MemberResponse;
import cl.uandes.sadmemail.comunes.gmail.json.MembersRequest;
import cl.uandes.sadmemail.comunes.gmail.json.MembersResponse;
import cl.uandes.sadmemail.comunes.google.api.services.Member;
import cl.uandes.sadmemail.comunes.google.api.services.Members;

/**
 * @author sademail
 *
 */
public class SincronizaGrupo implements Processor {

	private RegistrosComunes registrosBD;

    @PropertyInject(value = "uri-gmailServices", defaultValue="http://localhost:8181/cxf/ESB/panel/gmailServices")
	private String gmailServices;

	@EndpointInject(uri = "sql:classpath:sql/qryOwners4grupo.sql?dataSource=#bannerDataSource")
	ProducerTemplate qryOwners4grupo;

	@EndpointInject(uri = "sql:classpath:sql/insertNapGrupoOwner.sql?dataSource=#bannerDataSource")
	ProducerTemplate insertNapGrupoOwner;

	@EndpointInject(uri = "sql:classpath:sql/updateNapGrupoOwner.sql?dataSource=#bannerDataSource")
	ProducerTemplate updateNapGrupoOwner;

	
    @EndpointInject(uri = "cxfrs:bean:rsRetrieveOwners?continuationTimeout=-1")
	ProducerTemplate retrieveOwners;
	String templateRetrieveOwners = "%s/members/retrieveOwners";
    
	@EndpointInject(uri = "cxfrs:bean:rsAddOwner?continuationTimeout=-1")
	ProducerTemplate addOwner;
	String templateAddOwner = "%s/member/addOwner";
    
	@EndpointInject(uri = "cxfrs:bean:rsDeleteOwner?continuationTimeout=-1")
	ProducerTemplate deleteOwner;
	String templateDeleteOwner = "%s/member/deleteOwner";
    
	private ResultadoFuncion res;
	private ContadoresAsignarOwners contadores;
	private CountThreads countThread;
	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		contadores = (ContadoresAsignarOwners)message.getHeader("contadoresAsignarOwners");
		countThread = (CountThreads)message.getHeader("countThread");
		res = (ResultadoFuncion)message.getHeader("ResultadoFuncion");
		try {
			countThread.incCounter();
			logger.info(String.format("SincronizaGrupo: countThread %d", countThread.getCounter()));
			String groupName = (String) message.getBody();
			Set<String> ownersInGmail = getOwnersInGmail(groupName);
			List<NapGrupoOwnerDTO> lista = getListaNapGrupoOwnerDTO(groupName);
			for (NapGrupoOwnerDTO dto : lista) {
				if (!ownersInGmail.contains(dto.getOwnerEmail())) {
					agregarOwner(groupName, dto);
					ownersInGmail.remove(dto.getOwnerEmail());
				}
				contadores.incCountProcesados();
			}
			
			// si quedan elementos en ownersInGmail, hay que agregarlos a la tabla de owners
			if (!ownersInGmail.isEmpty())
				agregarOwnersABD(groupName, ownersInGmail);
			
		} catch (Exception e) {
			logger.error("process", e);
			if (res != null)
				registrosBD.registraMiResultadoErrores("NA", null, e, null, res.getKey());
			contadores.incCountErrores();
		} finally {
			countThread.decCounter();
		}
	}

	/**
	 * Genera un Set con los emails registrados en GMAIL como owners del grupo
	 * @param groupName
	 * @return
	 */
	private Set<String> getOwnersInGmail(String groupName) {
		Set<String> ownersInGmail = new HashSet<String>();
		String token = null;
		MembersRequest request = null;
		String url = null;
		try {
			Map<String, Object> headers = new HashMap<String, Object>();
			url = String.format(templateRetrieveOwners, 
					getGmailServices(), groupName);
			headers.put(Exchange.DESTINATION_OVERRIDE_URL, url);
			headers.put("CamelHttpMethod", "POST");
			do {
				request = new MembersRequest(groupName, token);
				MembersResponse response = (MembersResponse) 
						ObjectFactory.procesaResponseImpl((ResponseImpl)retrieveOwners.requestBodyAndHeaders(
								request, headers),MembersResponse.class);
				if (response != null && response.getCodigo() == 0) {
					Members members = response.getMembers();
					token = members.getNextPageToken();
					List<Member> listaMembers = members.getListaMembers();
					for (Member member : listaMembers)
						ownersInGmail.add(member.getEmail());
				}
			} while (token != null);
		} catch (Exception e) {
			String apoyo = String.format("getOwnersInGmail: groupName=%s token=%s url=%s request=%s", 
					groupName, token==null?"Primera invocacion": token, url, request);
			logger.error(apoyo, e);
			registrosBD.registraMiResultadoErrores("NA", apoyo, e, null, res.getKey());
		}
		return ownersInGmail;
	}

	/**
	 * Recuopera de tabla NAP_GRUPO_OWMNER todos los owners para un determinado grupo
	 * @param groupName
	 * @return
	 */
	private List<NapGrupoOwnerDTO> getListaNapGrupoOwnerDTO(String groupName) {
		List<NapGrupoOwnerDTO> lista = new ArrayList<NapGrupoOwnerDTO>();
		try {
			@SuppressWarnings("unchecked")
			List<Map<String,Object>> datos = 
					(List<Map<String,Object>>)qryOwners4grupo.requestBodyAndHeader(null, "groupName", groupName);
			for (Map<String,Object> data : datos)
				lista.add(new NapGrupoOwnerDTO(data));
		} catch (Exception e) {
			String apoyo = String.format("getListaNapGrupoOwnerDTO: groupName=%s", groupName);
			logger.error(apoyo, e);
			registrosBD.registraMiResultadoErrores("NA", apoyo, e, null, res.getKey());
		}
		return lista;
	}

	/**
	 * Agregar un owner a grupo en GMAIL
	 * @param groupName
	 * @param ownerEmail
	 * @return
	 */
	private boolean agregarOwner(String groupName, NapGrupoOwnerDTO dto) {
		Map<String, Object> headers = new HashMap<String, Object>();
		boolean resultado = false;
		String ownerEmail = dto.getOwnerEmail();
		try {
			headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateAddOwner,getGmailServices()));
			headers.put("CamelHttpMethod", "POST");
			MemberRequest request = new MemberRequest(groupName, ownerEmail);
			MemberResponse response = (MemberResponse) ObjectFactory.procesaResponseImpl((ResponseImpl)addOwner.requestBodyAndHeaders(request, headers),MemberResponse.class);
			logger.info(String.format("agregarOwner: MemberResponse: %s", response!=null?response:"ES NULO"));
			if (response != null && (response.getCodigo() == 0 || (response.getMensaje() != null && (
					response.getMensaje().matches(".*already exist.*")) ||
					response.getMensaje().matches(".*409 Conflict.*")))
				) {
				contadores.incCountAgregadosGMAIL();
				updateNapGrupoOwner.requestBodyAndHeader(null, "key", ObjectFactory.toBigDecimal(dto.getKey()));
				resultado = true;
			} else {
				contadores.incCountErrores();
			}
		} catch (Exception e) {
			String apoyo = String.format("agregarOwner: groupName=%s ownerEmail=%s", groupName, ownerEmail);
			logger.error(apoyo, e);
			registrosBD.registraMiResultadoErrores("NA", apoyo, e, null, res.getKey());
			contadores.incCountErrores();
		}
		return resultado;
	}

	/**
	 * Agregar un owner a la tabla NAP_GRUPO_OWNER
	 * @param ownersInGmail
	 */
	private void agregarOwnersABD(String groupName, Set<String> ownersInGmail) {
		Map<String, Object> headers = new HashMap<String, Object>();
		try {
			if (ownersInGmail != null && ownersInGmail.size() > 0) {
				for (String ownerEmail : ownersInGmail) {
					logger.info(String.format("agregarOwnersABD: groupName=%s ownersInGmail=%s", groupName, ownersInGmail));
					headers.clear();
					headers.put("groupName", groupName);
					headers.put("email", ownerEmail);
					insertNapGrupoOwner.requestBodyAndHeaders(null, headers);
				}
			}
		} catch (Exception e) {
			String apoyo = String.format("agregarOwnersABD: groupName=%s ownersInGmail=%s", groupName, ownersInGmail);
			logger.error(apoyo, e);
			registrosBD.registraMiResultadoErrores("NA", apoyo, e, null, res.getKey());
			contadores.incCountErrores();
		}
	}

	//======================================================================================================================================
	// Getters y Setters
	//======================================================================================================================================

	public RegistrosComunes getRegistrosBD() {
		return registrosBD;
	}

	public void setRegistrosBD(RegistrosComunes registrosBD) {
		this.registrosBD = registrosBD;
	}

	public String getGmailServices() {
		return gmailServices;
	}

	public void setGmailServices(String gmailServices) {
		this.gmailServices = gmailServices;
	}

}
