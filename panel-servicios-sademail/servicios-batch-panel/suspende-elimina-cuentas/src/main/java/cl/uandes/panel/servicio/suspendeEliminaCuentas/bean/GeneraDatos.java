package cl.uandes.panel.servicio.suspendeEliminaCuentas.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.batch.ProcesoDiarioResponse;
import cl.uandes.panel.comunes.utils.CountThreads;
import cl.uandes.panel.comunes.utils.ObjectFactory;
import cl.uandes.sadmemail.comunes.gmail.json.AllUsersRequest;
import cl.uandes.sadmemail.comunes.gmail.json.AllUsersResponse;
import cl.uandes.sadmemail.comunes.google.api.services.User;

public class GeneraDatos {

    @PropertyInject(value = "se-cuentas-gmail.nocturno.funcion", defaultValue="suspender_eliminar_cuentas")
	private String funcion;

	@PropertyInject(value = "uri.gmailServices", defaultValue="http://localhost:8181/cxf/ESB/panel/gmailServices")
	private String gmailServices;

	@EndpointInject(uri = "cxfrs:bean:rsRetrieveAllUsers?continuationTimeout=-1")
	ProducerTemplate rsRetrieveAllUsers;
	String templateRecuperaGrupoGmail = "%s/user/retrieveAllUsers";

	private Logger logger = Logger.getLogger(getClass());
	
	/**
	 * Invoca al api rsRetrieveAllUsers para recuperar todos los usuario
	 * Deja en el header el token devuelto
	 * Define el header.hayToken = tru/false dependiendo si hay mas usuarios que recuperar
	 * Deja en header.listaUsuariosSincronizar los usuarios recuperados
	 * Inicializa el contador de Threads
	 * @param exchange
	 */
	public void recuperaUsuariosGmail(Exchange exchange) {
		Message message = exchange.getIn();
		String token = (String) message.getHeader("retrieveAllUsers.token");
		AllUsersResponse response = retrieveAllUsers(token);
		if (response != null && response.getCodigo() == 0) {
			message.setHeader("retrieveAllUsers.token", response.getNextToken());
			message.setHeader("listaUsuariosSincronizar", response.getListaUsuarios());
			message.setHeader("countThread", new CountThreads());
		}
	}
	
	/**
	 * Invoca al API de serviciosGmail para recuperar todos los usuarios
	 * @param token
	 * @return
	 */
	private AllUsersResponse retrieveAllUsers(String token) {
		AllUsersResponse response = null;
		Map<String,Object> headers = new HashMap<String,Object>();
		AllUsersRequest request = new AllUsersRequest(token, Boolean.FALSE);
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(getTemplateRecuperaGrupoGmail(),getGmailServices()));
		headers.put("CamelHttpMethod", "POST");
		try {
			response = (AllUsersResponse)ObjectFactory.procesaResponseImpl(
					(ResponseImpl)rsRetrieveAllUsers.requestBody(request),AllUsersResponse.class);
		} catch (Exception e) {
			logger.error("recuperaUsuariosGmail", e);
		}
		return response;
	}
	/**
	 * Saca el primer elemento de la lista header.listaUsuariosSincronizar y lo deja en el Body para su proceso
	 * @param exchange
	 */
	public void recuperaUsuario(Exchange exchange) {
		Message message = exchange.getIn();
		@SuppressWarnings("unchecked")
		List<User> listaUsuarios = (List<User>) message.getHeader("listaUsuariosSincronizar");
		logger.info(String.format("recuperaUsuario: cantidad de usuarios en la lista=%d antes", listaUsuarios.size()));
		if (listaUsuarios.size() > 0) {
			User user = listaUsuarios.remove(0);
			logger.info(String.format("recuperaUsuario: procesa usuario: %s cantidad de usuarios en la lista=%d despues",
					user, listaUsuarios.size()));
			message.setBody(user);
			CountThreads countThread = (CountThreads)message.getHeader("countThread");
			countThread.incCounter();
		}
		
	}

	public ProcesoDiarioResponse setErrorInicializacion(@Header(value = "exception")Throwable exception, Exchange exchange) {
		String mensaje = exception.getMessage();
		return new ProcesoDiarioResponse(-1, getFuncion(), mensaje, (Integer)exchange.getIn().getHeader("keyResultado") );
	}
	
	//==========================================================================================================
	// Getters
	//==========================================================================================================

	
	public synchronized String getGmailServices() {
		return gmailServices;
	}

	public synchronized void setGmailServices(String gmailServices) {
		this.gmailServices = gmailServices;
	}

	public synchronized String getTemplateRecuperaGrupoGmail() {
		return templateRecuperaGrupoGmail;
	}


	public synchronized String getFuncion() {
		return funcion;
	}


	public synchronized void setFuncion(String funcionBD) {
		this.funcion = funcionBD;
	}

}
