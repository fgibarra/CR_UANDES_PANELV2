package cl.uandes.comun.ad.procesor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;
import org.tempuri.ActivarDesactivarUsuarioResponse;
import org.tempuri.ActualizarUsuarioResponse;
import org.tempuri.CrearUsuarioResponse;
import org.tempuri.DesbloquearUsuarioResponse;
import org.tempuri.ResetearPasswordResponse;
import org.tempuri.ValidarUsuarioResponse;

import cl.uandes.comun.ad.client.soap.cxf.consultaXrut.dto.ConsultaRutAdResponse;
import cl.uandes.comun.ad.client.soap.cxf.consultaXrut.dto.Retorno;
import cl.uandes.panel.comunes.json.consultaXrut.ConsultaXrutResponse;
import cl.uandes.panel.comunes.json.serviciosLDAP.ServiciosLDAPResponse;

/**
 * Genera la respuesta del APIREST<br>
 * @author fernando
 *
 */
public class GeneraResponse implements Processor {

    @PropertyInject(value = "operacion.activarDesactivarUsuario", defaultValue="ActivarDesactivarUsuario")
    private String activarDesactivarUsuario;
    @PropertyInject(value = "operacion.actualizarUsuario", defaultValue="ActualizarUsuario")
    private String actualizarUsuario;
    @PropertyInject(value = "operacion.crearUsuario", defaultValue="CrearUsuario")
    private String crearUsuario;
    @PropertyInject(value = "operacion.desbloquearUsuario", defaultValue="DesbloquearUsuario")
    private String desbloquearUsuario;
    @PropertyInject(value = "operacion.resetearPassword", defaultValue="ResetearPassword")
    private String resetearPassword;
    @PropertyInject(value = "operacion.validarUsuario", defaultValue="ValidarUsuario")
    private String validarUsuario;
    @PropertyInject(value = "operacion.consultaXrut", defaultValue="consultaXrut")
    private String consultaXrut;

    private Logger logger = Logger.getLogger(getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		String operacion = (String)exchange.getIn().getHeader("Operacion");
		Object soapResponse = exchange.getIn().getBody();
		logger.info(String.format("GeneraResponse: operacion:%s  clase del soapResponse: %s", 
				operacion, soapResponse.getClass().getName()));
		if (operacion.equals(activarDesactivarUsuario))
			generaResponseActivarDesactivarUsuario(soapResponse, exchange);
		else if (operacion.equals(actualizarUsuario))	
			generaResponseActualizarUsuario(soapResponse, exchange);
		else if (operacion.equals(crearUsuario))	
			generaResponseCrearUsuario(soapResponse, exchange);
		else if (operacion.equals(desbloquearUsuario))	
			generaResponseDesbloquearUsuario(soapResponse, exchange);
		else if (operacion.equals(resetearPassword))	
			generaResponseResetearPassword(soapResponse, exchange);
		else if (operacion.equals(validarUsuario))	
			generaResponseValidarUsuario(soapResponse, exchange);
		else if (operacion.equals(consultaXrut))	
			generaResponseConsultaXrut(soapResponse, exchange);
		else if (operacion.equalsIgnoreCase("ERROR")) {
			Throwable ex = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
			if ("consultaXrut".equals((String)exchange.getIn().getHeader("operacionOrigen"))) {
				ConsultaXrutResponse response2 = new ConsultaXrutResponse(-1, ex.getMessage(),null,null,null,null,null,null,null,null,null,null);
				setInHeader(response2, exchange);
			} else {
				ServiciosLDAPResponse response = new ServiciosLDAPResponse(-1, ex.getMessage());
				setInHeader(response, exchange);
			}
		}
	}

	private void generaResponseActivarDesactivarUsuario(Object soapResponse, Exchange exchange) {
		ActivarDesactivarUsuarioResponse soapresponse = (ActivarDesactivarUsuarioResponse)soapResponse;
		ServiciosLDAPResponse response = new ServiciosLDAPResponse(0, soapresponse.getActivarDesactivarUsuarioResult().getValue());
		setInHeader(response, exchange);
	}

	private void generaResponseActualizarUsuario(Object soapResponse, Exchange exchange) {
		ActualizarUsuarioResponse soapresponse = (ActualizarUsuarioResponse)soapResponse;
		ServiciosLDAPResponse response = new ServiciosLDAPResponse(0, soapresponse.getActualizarUsuarioResult().getValue());
		setInHeader(response, exchange);
	}

	private void generaResponseCrearUsuario(Object soapResponse, Exchange exchange) {
		CrearUsuarioResponse soapresponse = (CrearUsuarioResponse)soapResponse;
		ServiciosLDAPResponse response = new ServiciosLDAPResponse(0, soapresponse.getCrearUsuarioResult().getValue());
		setInHeader(response, exchange);
	}

	private void generaResponseDesbloquearUsuario(Object soapResponse, Exchange exchange) {
		DesbloquearUsuarioResponse soapresponse = (DesbloquearUsuarioResponse)soapResponse;
		ServiciosLDAPResponse response = new ServiciosLDAPResponse(0, soapresponse.getDesbloquearUsuarioResult().getValue());
		setInHeader(response, exchange);
	}

	private void generaResponseResetearPassword(Object soapResponse, Exchange exchange) {
		ResetearPasswordResponse soapresponse = (ResetearPasswordResponse)soapResponse;
		ServiciosLDAPResponse response = new ServiciosLDAPResponse(0, soapresponse.getResetearPasswordResult().getValue());
		setInHeader(response, exchange);
	}

	private void generaResponseValidarUsuario(Object soapResponse, Exchange exchange) {
		ValidarUsuarioResponse soapresponse = (ValidarUsuarioResponse)soapResponse;
		ServiciosLDAPResponse response = new ServiciosLDAPResponse(0, soapresponse.getValidarUsuarioResult().getValue());
		setInHeader(response, exchange);
	}

	private void generaResponseConsultaXrut(Object soapResponse, Exchange exchange) {
		Retorno retorno = null;
		if (soapResponse instanceof Retorno)
			retorno = (Retorno)soapResponse;
		else {
			try {
				org.apache.xerces.dom.NodeImpl node = (org.apache.xerces.dom.NodeImpl)soapResponse;
				logger.info(String.format("NodeName es %s child es %s", 
						node.getLocalName(), node.getFirstChild().getLocalName()));
				
				JAXBContext context = JAXBContext.newInstance(ConsultaRutAdResponse.class);
				Unmarshaller un = context.createUnmarshaller();
				retorno = (Retorno)un.unmarshal(node.getFirstChild());
				logger.info(String.format("soapresponse es %s NULO", retorno!=null?"NO":""));
			} catch (JAXBException e) {
				logger.error("al convertir", e);
			}
		}
		String valor = retorno.getCantidad();
		Integer cantidad = valor!= null ? Integer.valueOf(valor) : null;
		ConsultaXrutResponse response = new ConsultaXrutResponse(0, "OK", cantidad, retorno.getRut(), 
				retorno.getUsuario(), retorno.getEmployeeid(), retorno.getDn(), retorno.getOu(),
				retorno.getCorreo(),retorno.getActiva(),retorno.getBloqueada(),retorno.getEstado());
		setInHeader(response, exchange);
	}

	private void setInHeader(Object response, Exchange exchange) {
		exchange.getIn().setBody(response);
		logger.info(String.format("deja en el body: %s", response));
	}
	
	public void unmarshalError(Exchange exchange) {
		String operacion = (String)exchange.getIn().getHeader("Operacion");
		logger.info(String.format("Entro al metodo unmarshalError para Operacion: %s", operacion));
		ServiciosLDAPResponse response = null;
		if (operacion.equals(activarDesactivarUsuario))
			response = new ServiciosLDAPResponse(-1, "No pudo consultar al AD");
		else if (operacion.equals(actualizarUsuario))	
			response = new ServiciosLDAPResponse(-1, "No pudo consultar al AD");
		else if (operacion.equals(crearUsuario))	
			response = new ServiciosLDAPResponse(-1, "No pudo consultar al AD");
		else if (operacion.equals(desbloquearUsuario))	
			response = new ServiciosLDAPResponse(-1, "No pudo consultar al AD");
		else if (operacion.equals(resetearPassword))	
			response = new ServiciosLDAPResponse(-1, "No pudo consultar al AD");
		else if (operacion.equals(validarUsuario))	
			response = new ServiciosLDAPResponse(-1, "No pudo consultar al AD");
		else if (operacion.equals(consultaXrut)) {
			ConsultaXrutResponse response2 = new ConsultaXrutResponse(-1, "No pudo consultar al AD",null,null,null,null,null,null,null,null,null,null);
			setInHeader(response2, exchange);
			return;
		}
		setInHeader(response, exchange);
	}
	
	public void postProcesaBody(Exchange exchange) {
		Message message = exchange.getIn();
		Object body = message.getBody();
		logger.info(String.format("clase del body %s", body.getClass().getCanonicalName()));
		String nn = new String((byte[])body);
		message.setBody(nn.replace(" xmlns:ns8=\"##default\"","").replace("ns8:", "").getBytes());
	}
}
