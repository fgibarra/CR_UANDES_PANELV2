package cl.uandes.panel.servicio.owners.add_delete.procesor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;
import org.apache.cxf.jaxrs.impl.ResponseImpl;

import cl.uandes.panel.comunes.bean.RegistrosComunes;
import cl.uandes.panel.comunes.json.aeOwnersMembers.AeOwnersMembersRequest;
import cl.uandes.panel.comunes.json.batch.ContadoresAsignarOwners;
import cl.uandes.panel.comunes.servicios.dto.ResultadoFuncion;
import cl.uandes.panel.comunes.utils.ObjectFactory;
import cl.uandes.panel.servicio.owners.api.resource.OwnersRestService;
import cl.uandes.panel.servicio.owners.dto.WrkOwnersGruposDTO;
import cl.uandes.sadmemail.comunes.gmail.json.MemberRequest;
import cl.uandes.sadmemail.comunes.gmail.json.MemberResponse;

public class ProcesaGrupoCuenta implements Processor {

	private RegistrosComunes registrosBD;
	private ResultadoFuncion res;
	private ContadoresAsignarOwners contadores;
	AeOwnersMembersRequest request;
	private Logger logger = Logger.getLogger(getClass());

    @PropertyInject(value = "uri-gmailServices", defaultValue="http://localhost:8181/cxf/ESB/panel/gmailServices")
	private String gmailServices;

	@EndpointInject(uri = "sql:classpath:sql/insertNapGrupoOwners.sql?dataSource=#bannerDataSource")
	ProducerTemplate insertNapGrupoOwners;

	@EndpointInject(uri = "sql:classpath:sql/insertNapGrupoMiembro.sql?dataSource=#bannerDataSource")
	ProducerTemplate insertNapGrupoMiembro;

	@EndpointInject(uri = "sql:classpath:sql/deleteNapGrupoOwners.sql?dataSource=#bannerDataSource")
	ProducerTemplate deleteNapGrupoOwners;

	@EndpointInject(uri = "sql:classpath:sql/deleteNapGrupoMiembro.sql?dataSource=#bannerDataSource")
	ProducerTemplate deleteNapGrupoMiembro;

	@EndpointInject(uri = "sql:classpath:sql/validaCuentaGrupo.sql?dataSource=#bannerDataSource")
	ProducerTemplate validaCuentaGrupo;

	@EndpointInject(uri = "sql:classpath:sql/validaGrupo.sql?dataSource=#bannerDataSource")
	ProducerTemplate validaGrupo;

	@EndpointInject(uri = "sql:classpath:sql/updateWrkOwnersGrupos.sql?dataSource=#bannerDataSource")
	ProducerTemplate updateWrkOwnersGrupos;
	
	@EndpointInject(uri = "cxfrs:bean:rsAddOwner?continuationTimeout=-1")
	ProducerTemplate addOwner;
	String templateAddOwner = "%s/member/addOwner";
    
	@EndpointInject(uri = "cxfrs:bean:rsDeleteOwner?continuationTimeout=-1")
	ProducerTemplate deleteOwner;
	String templateDeleteOwner = "%s/member/deleteOwner";
    
	@EndpointInject(uri = "cxfrs:bean:rsSacarMember?continuationTimeout=-1")
	ProducerTemplate sacarMember;
	String templateSacarMember = "%s/member/deleteMember";

	@EndpointInject(uri = "cxfrs:bean:rsAgregarMember?continuationTimeout=-1")
	ProducerTemplate agregarMember;
	String templateAgregarMember = "%s/member/addMember";
	
    
	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		contadores = (ContadoresAsignarOwners)message.getHeader("contadoresAsignarOwners");
		res = (ResultadoFuncion)message.getHeader("ResultadoFuncion");
		request =(AeOwnersMembersRequest)message.getHeader("request");
		WrkOwnersGruposDTO dto = (WrkOwnersGruposDTO)message.getHeader("WrkOwnersGruposDTO");
		try {
			if (dto != null) {
				if (dto.getFuncion().equalsIgnoreCase(OwnersRestService.funcionesValidos[OwnersRestService.ADD]))
					agrega(request.getServicio(), dto);
				else
					elimina(request.getServicio(), dto);
			}
		} catch (Exception e) {
			String apoyo = String.format("process: WrkOwnersGruposDTO=%s", dto);
			logger.error(apoyo, e);
			if (res != null)
				registrosBD.registraMiResultadoErrores("NA", apoyo, e, null, res.getKey());
			contadores.incCountErrores();
		}
		contadores.incCountProcesados();
	}

	private void agrega(String servicio, WrkOwnersGruposDTO dto) {
		if (servicio.equalsIgnoreCase(OwnersRestService.serviciosValidos[OwnersRestService.OWNER]))
			agregaOwner(dto);
		else
			eliminaOwner(dto);
	}

	private void elimina(String servicio, WrkOwnersGruposDTO dto) {
		if (servicio.equalsIgnoreCase(OwnersRestService.serviciosValidos[OwnersRestService.MEMBER]))
			agregaMember(dto);
		else
			eliminaMember(dto);
		
	}

	private void agregaOwner(WrkOwnersGruposDTO dto) {
		if (existeGrupo(dto)) {
			if (agregaOwnerEnGmail(dto)) {
				agregarOwnerEnBD(dto);
				actualizaWrkOwnersGrupos("OK", dto);
			} else
				actualizaWrkOwnersGrupos("KO", dto);
		} else {
			actualizaWrkOwnersGrupos("KO", dto);
			String causa = String.format("No existe grupo WrkOwnersGruposDTO: %s", dto);
			registrosBD.registraMiResultadoErrores(dto.getCorreo(), "agregaOwner", causa, null, res.getKey());
			contadores.incCountErrores();
		}
	}

	private void eliminaOwner(WrkOwnersGruposDTO dto) {
		if (existeGrupo(dto)) {
			if (eliminaOwnerEnGmail(dto)) {
				eliminaOwnerEnBD(dto);
				actualizaWrkOwnersGrupos("OK", dto);
			} else
				actualizaWrkOwnersGrupos("KO", dto);
		} else {
			actualizaWrkOwnersGrupos("KO", dto);
			String causa = String.format("No existe grupo WrkOwnersGruposDTO: %s", dto);
			registrosBD.registraMiResultadoErrores(dto.getCorreo(), "eliminaOwner", causa, null, res.getKey());
			contadores.incCountErrores();
		}
	}

	private void agregaMember(WrkOwnersGruposDTO dto) {
		if (existeCuentaGrupo(dto)) {
			if (agregaMemberEnGmail(dto)) {
				agregarMemberEnBD(dto);
				actualizaWrkOwnersGrupos("OK", dto);
			} else
				actualizaWrkOwnersGrupos("KO", dto);
		} else {
			actualizaWrkOwnersGrupos("KO", dto);
			String causa = String.format("No existe cuenta o grupo WrkOwnersGruposDTO: %s", dto);
			registrosBD.registraMiResultadoErrores(dto.getCorreo(), "agregaMember", causa, null, res.getKey());
			contadores.incCountErrores();
		}
	}

	private void eliminaMember(WrkOwnersGruposDTO dto) {
		if (existeCuentaGrupo(dto)) {
			if (eliminaMemberEnGmail(dto)) {
				eliminaMemberEnBD(dto);
				actualizaWrkOwnersGrupos("OK", dto);
			} else
				actualizaWrkOwnersGrupos("KO", dto);
		} else {
			actualizaWrkOwnersGrupos("KO", dto);
			String causa = String.format("No existe cuenta o grupo WrkOwnersGruposDTO: %s", dto);
			registrosBD.registraMiResultadoErrores(dto.getCorreo(), "eliminaMember", causa, null, res.getKey());
			contadores.incCountErrores();
		}
	}

	
	private boolean agregaOwnerEnGmail(WrkOwnersGruposDTO dto) {
		return operaGmail(true, templateAddOwner, addOwner, dto);
	}

	private boolean agregaMemberEnGmail(WrkOwnersGruposDTO dto) {
		return operaGmail(true, templateAgregarMember, agregarMember, dto);
	}

	private boolean eliminaOwnerEnGmail(WrkOwnersGruposDTO dto) {
		return operaGmail(false, templateDeleteOwner, deleteOwner, dto);
	}

	private boolean eliminaMemberEnGmail(WrkOwnersGruposDTO dto) {
		return operaGmail(false, templateSacarMember, sacarMember, dto);
	}

	private boolean operaGmail (boolean esAdd, String template, ProducerTemplate producerTemplate, WrkOwnersGruposDTO dto) {
		boolean resultado = false;
		Map<String, Object> headers = new HashMap<String, Object>();
		try {
			MemberRequest request = new MemberRequest(dto.getLista(), dto.getCorreo());
			headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(template,getGmailServices()));
			headers.put("CamelHttpMethod", "POST");
			if (esAdd) {
				MemberResponse response = (MemberResponse) ObjectFactory.procesaResponseImpl(
						(ResponseImpl)producerTemplate.requestBodyAndHeaders(request, headers),MemberResponse.class);
				if (response.getCodigo() == 0) {
					resultado = true;
					contadores.incCountAgregadosGMAIL();
				}
			} else {
				Response response = (Response) producerTemplate.requestBodyAndHeaders(request, headers);
				if (response.getStatus() < 300) {
					resultado = true;
					contadores.incCountSacadosGMAIL();
				}
			}
		} catch (Exception e) {
			String apoyo = String.format("WrkOwnersGruposDTO=%s template=%s producerTemplate=%s", 
					dto, template, producerTemplate.getDefaultEndpoint().getEndpointUri());
			registrosBD.registraMiResultadoErrores("NA", apoyo, e, null, res.getKey());
			contadores.incCountErrores();
		}
		return resultado;
	}

	private void agregarOwnerEnBD(WrkOwnersGruposDTO dto) {
		opereEnBD (true, insertNapGrupoOwners, dto);
	}

	private void agregarMemberEnBD(WrkOwnersGruposDTO dto) {
		opereEnBD (true, insertNapGrupoMiembro, dto);
	}

	private void eliminaOwnerEnBD(WrkOwnersGruposDTO dto) {
		opereEnBD (false, deleteNapGrupoOwners, dto);
	}

	private void eliminaMemberEnBD(WrkOwnersGruposDTO dto) {
		opereEnBD (false, deleteNapGrupoMiembro, dto);
	}

	private void opereEnBD (boolean esAdd, ProducerTemplate producerTemplate, WrkOwnersGruposDTO dto) {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("email", dto.getCorreoSinDominio());
		headers.put("groupName", dto.getLista());
		try {
			producerTemplate.requestBodyAndHeaders(null, headers);
			if (esAdd)
				contadores.incCountAgregadosBD();
			else
				contadores.incCountSacadosBD();
		} catch (Exception e) {
			String apoyo = String.format("WrkOwnersGruposDTO=%s producerTemplate=%s", dto, producerTemplate.getDefaultEndpoint().getEndpointUri());
			registrosBD.registraMiResultadoErrores("NA", apoyo, e, null, res.getKey());
			contadores.incCountErrores();
		}
	}
	
	private boolean existeCuentaGrupo(WrkOwnersGruposDTO dto) {
		boolean existe = false;
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("email", dto.getCorreoSinDominio());
		headers.put("groupName", dto.getLista());
		try {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> datos = (List<Map<String, Object>>)validaCuentaGrupo.requestBodyAndHeaders(null, headers);
			if (datos != null && datos.size() > 0)
				existe = true;
		} catch (Exception e) {
			String apoyo = String.format("WrkOwnersGruposDTO=%s producerTemplate=%s", dto, validaCuentaGrupo.getDefaultEndpoint().getEndpointUri());
			registrosBD.registraMiResultadoErrores("NA", apoyo, e, null, res.getKey());
		}
		return existe;
	}
	
	private boolean existeGrupo(WrkOwnersGruposDTO dto) {
		boolean existe = false;
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("email", dto.getCorreoSinDominio());
		headers.put("groupName", dto.getLista());
		try {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> datos = (List<Map<String, Object>>)validaGrupo.requestBodyAndHeaders(null, headers);
			if (datos != null && datos.size() > 0)
				existe = true;
		} catch (Exception e) {
			String apoyo = String.format("WrkOwnersGruposDTO=%s producerTemplate=%s", dto, validaGrupo.getDefaultEndpoint().getEndpointUri());
			registrosBD.registraMiResultadoErrores("NA", apoyo, e, null, res.getKey());
		}
		return existe;
	}

	private void actualizaWrkOwnersGrupos(String resultado, WrkOwnersGruposDTO dto) {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("rowid", dto.getRowid());
		headers.put("resultado", resultado);
		try {
			updateWrkOwnersGrupos.requestBodyAndHeaders(null, headers);
		} catch (Exception e) {
			String apoyo = String.format("template: %s WrkOwnersGruposDTO=%s resultado=%s",
					updateWrkOwnersGrupos.getDefaultEndpoint().getEndpointUri(), dto, resultado);
			registrosBD.registraMiResultadoErrores("NA", apoyo, e, null, res.getKey());
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
