package cl.uandes.comun.ad.client.soap.cxf.ldapService;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;

import cl.uandes.comun.ad.client.soap.cxf.ldapService.dto.ObjectFactory;
import cl.uandes.panel.comunes.json.serviciosLDAP.ServiciosLDAPRequest;
import cl.uandes.panel.comunes.json.serviciosLDAP.Usuario;

/**
 * @author fernando
 *
 */
public class CreateSoapRequest {

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

    private Logger logger = Logger.getLogger(getClass());
	
	public void createSoapBody(@Header("Operacion") String operacion, Exchange exchange) {
		ServiciosLDAPRequest request = (ServiciosLDAPRequest)exchange.getIn().getBody();
		ObjectFactory factory = new ObjectFactory();
		logger.info(String.format(" operacion: %s request: %s", operacion, request==null?"ES NULO":request.toString()));
		exchange.getIn().setHeader("OPERATION_NAME", operacion);
		exchange.getIn().setHeader("SOAPAction", String.format("http://tempuri.org/ILDAPService/%s",operacion));
		if (operacion.equals(activarDesactivarUsuario))
			exchange.getIn().setBody(factory.createActivarDesactivarUsuarioRequest(request));
		else if (operacion.equals(actualizarUsuario))	
			exchange.getIn().setBody(factory.createActualizarUsuarioRequest(request));
		else if (operacion.equals(crearUsuario))	
			exchange.getIn().setBody(factory.createCrearUsuarioRequest(request));
		else if (operacion.equals(desbloquearUsuario))	
			exchange.getIn().setBody(factory.createDesbloquearUsuarioRequest(request));
		else if (operacion.equals(resetearPassword))	
			exchange.getIn().setBody(factory.createActivarResetearPasswordRequest(request));
		else if (operacion.equals(validarUsuario))	
			exchange.getIn().setBody(factory.createValidarUsuarioRequest(request));
		exchange.getIn().setHeader("operacionOrigen", operacion);
	}

	public String getActivarDesactivarUsuario() {
		return activarDesactivarUsuario;
	}

	public void setActivarDesactivarUsuario(String activarDesactivarUsuario) {
		this.activarDesactivarUsuario = activarDesactivarUsuario;
	}

	public String getActualizarUsuario() {
		return actualizarUsuario;
	}

	public void setActualizarUsuario(String actualizarUsuario) {
		this.actualizarUsuario = actualizarUsuario;
	}

	public String getCrearUsuario() {
		return crearUsuario;
	}

	public void setCrearUsuario(String crearUsuario) {
		this.crearUsuario = crearUsuario;
	}

	public String getDesbloquearUsuario() {
		return desbloquearUsuario;
	}

	public void setDesbloquearUsuario(String desbloquearUsuario) {
		this.desbloquearUsuario = desbloquearUsuario;
	}

	public String getResetearPassword() {
		return resetearPassword;
	}

	public void setResetearPassword(String resetearPassword) {
		this.resetearPassword = resetearPassword;
	}

	public String getValidarUsuario() {
		return validarUsuario;
	}

	public void setValidarUsuario(String validarUsuario) {
		this.validarUsuario = validarUsuario;
	}
	
	public void createRequestValidarUsuario(Exchange exchange) {
		ServiciosLDAPRequest request = new ServiciosLDAPRequest("ValidarUsuario", null, new Usuario("fgibarra",null,null,null,null,null,null,null,null,null,null,null,null,null));
		exchange.getIn().setBody(request);
	}
	public void createRequestActivarDesactivarUsuario(Exchange exchange) {
		ServiciosLDAPRequest request = new ServiciosLDAPRequest("ActivarDesactivarUsuario", null, new Usuario("fgibarra",null,null,null,null,null,null,null,null,null,null,null,null,null));
		exchange.getIn().setBody(request);
	}
	public void createRequestActualizarUsuario(Exchange exchange) {
		ServiciosLDAPRequest request = new ServiciosLDAPRequest("ActualizarUsuario", null, new Usuario("fgibarra","password","rama","rut","Fernando","Ibarra","correo","telefono","direccion","comuna","cargo","depto","jefatura","compania"));
		exchange.getIn().setBody(request);
	}
	public void createRequestCrearUsuario(Exchange exchange) {
		ServiciosLDAPRequest request = new ServiciosLDAPRequest("CrearUsuario", null, new Usuario("fgibarra","password","rama","rut","Fernando","Ibarra","correo","telefono","direccion","comuna","cargo","depto","jefatura","compania"));
		exchange.getIn().setBody(request);
	}
	public void createRequestDesbloquearUsuario(Exchange exchange) {
		ServiciosLDAPRequest request = new ServiciosLDAPRequest("DesbloquearUsuario", null, new Usuario("fgibarra","password","rama","rut","Fernando","Ibarra","correo","telefono","direccion","comuna","cargo","depto","jefatura","compania"));
		exchange.getIn().setBody(request);
	}
	public void createRequestResetearPassword(Exchange exchange) {
		ServiciosLDAPRequest request = new ServiciosLDAPRequest("ResetearPassword", null, new Usuario("fgibarra","password","rama","rut","Fernando","Ibarra","correo","telefono","direccion","comuna","cargo","depto","jefatura","compania"));
		exchange.getIn().setBody(request);
	}
}
