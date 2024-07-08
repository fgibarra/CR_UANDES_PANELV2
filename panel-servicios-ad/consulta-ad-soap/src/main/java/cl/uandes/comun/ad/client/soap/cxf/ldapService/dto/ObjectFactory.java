package cl.uandes.comun.ad.client.soap.cxf.ldapService.dto;

import org.tempuri.ActivarDesactivarUsuario;
import org.tempuri.ActualizarUsuario;
import org.tempuri.ConsultarUsuario;
import org.tempuri.CrearUsuario;
import org.tempuri.DesbloquearUsuario;
import org.tempuri.EliminarUsuario;
import org.tempuri.ResetearPassword;
import org.tempuri.ValidarUsuario;

import cl.uandes.panel.comunes.json.serviciosLDAP.ServiciosLDAPRequest;

/**
 * @author fernando
 *
 */
public class ObjectFactory extends org.tempuri.ObjectFactory {

	public Object createValidarUsuarioRequest(ServiciosLDAPRequest request) {
		ValidarUsuario validarUsuario = new ValidarUsuario();
		validarUsuario.setCuenta(super.createValidarUsuarioCuenta(request.getUsuario().getCuenta()));
		return validarUsuario;
	}

	public Object createActivarResetearPasswordRequest(ServiciosLDAPRequest request) {
		ResetearPassword resetearPassword = new ResetearPassword();
		resetearPassword.setCuenta(super.createResetearPasswordCuenta(request.getUsuario().getCuenta()));
		resetearPassword.setNuevaPassword(super.createResetearPasswordCuenta(request.getUsuario().getPassword()));
		return resetearPassword;
	}

	public Object createDesbloquearUsuarioRequest(ServiciosLDAPRequest request) {
		DesbloquearUsuario desbloquearUsuario = new DesbloquearUsuario();
		desbloquearUsuario.setCuenta(super.createDesbloquearUsuarioCuenta(request.getUsuario().getCuenta()));
		return desbloquearUsuario;
	}

	public Object createCrearUsuarioRequest(ServiciosLDAPRequest request) {
		CrearUsuario crearUsuario = super.createCrearUsuario();
		org.datacontract.schemas._2004._07.servicioldap.ObjectFactory of = new org.datacontract.schemas._2004._07.servicioldap.ObjectFactory();
		org.datacontract.schemas._2004._07.servicioldap.Usuario usuario = of.createUsuario();
		usuario.setCuenta(of.createUsuarioCuenta(request.getUsuario().getCuenta()));
		usuario.setNombre(of.createUsuarioNombre(request.getUsuario().getNombre()));
		usuario.setPassword(of.createUsuarioPassword(request.getUsuario().getPassword()));
		usuario.setRama(of.createUsuarioRama(request.getUsuario().getRama()));
		usuario.setRut(of.createUsuarioRut(request.getUsuario().getRut()));
		if (request.getUsuario().getApellidos() != null)
			usuario.setApellidos(of.createUsuarioApellidos(request.getUsuario().getApellidos()));
		if (request.getUsuario().getComuna() != null)
			usuario.setComuna(of.createUsuarioComuna(request.getUsuario().getComuna()));
		if (request.getUsuario().getCorreo() != null)
			usuario.setCorreo(of.createUsuarioCorreo(request.getUsuario().getCorreo()));
		if (request.getUsuario().getDireccion() != null)
			usuario.setDireccion(of.createUsuarioDireccion(request.getUsuario().getDireccion()));
		if (request.getUsuario().getPidm() != null)
			usuario.setPidm(of.createUsuarioPidm(request.getUsuario().getPidm()));
		if (request.getUsuario().getNivel() != null)
			usuario.setNivel(of.createUsuarioNivel(request.getUsuario().getNivel()));
		if (request.getUsuario().getEstadoAcademico() != null)
			usuario.setEstadoAcademico(of.createUsuarioEstadoAcademico(request.getUsuario().getEstadoAcademico()));
		
		crearUsuario.setUsuario(createCrearUsuarioUsuario(usuario));
		return crearUsuario;
	}

	public Object createActualizarUsuarioRequest(ServiciosLDAPRequest request) {
		ActualizarUsuario actualizarUsuario = super.createActualizarUsuario();
		actualizarUsuario.setCuenta(super.createActualizarUsuarioCuenta(request.getUsuario().getCuenta()));
		if (request.getUsuario().getApellidos() != null)
			actualizarUsuario.setApellido(super.createActualizarUsuarioApellido(request.getUsuario().getApellidos()));
		if (request.getUsuario().getDireccion() != null)
			actualizarUsuario.setDireccion(super.createActualizarUsuarioDireccion(request.getUsuario().getDireccion()));
		if (request.getUsuario().getComuna() != null)
			actualizarUsuario.setComuna(super.createActualizarUsuarioComuna(request.getUsuario().getComuna()));
		if (request.getUsuario().getNombre() != null)
			actualizarUsuario.setNombre(super.createActualizarUsuarioNombre(request.getUsuario().getNombre()));
		if (request.getUsuario().getPidm() != null)
			actualizarUsuario.setPidm(super.createActualizarUsuarioPidm(request.getUsuario().getPidm()));
		if (request.getUsuario().getNivel() != null)
			actualizarUsuario.setNivel(super.createActualizarUsuarioNivel(request.getUsuario().getNivel()));
		if (request.getUsuario().getEstadoAcademico() != null)
			actualizarUsuario.setEstadoAcademico(super.createActualizarUsuarioEstadoAcademico(request.getUsuario().getEstadoAcademico()));
		
		return actualizarUsuario;
	}

	public Object createActivarDesactivarUsuarioRequest(ServiciosLDAPRequest request) {
		ActivarDesactivarUsuario activarDesactivarUsuario = new ActivarDesactivarUsuario();
		activarDesactivarUsuario.setActivar(request.getActivar());
		activarDesactivarUsuario.setCuenta(super.createActivarDesactivarUsuarioCuenta(request.getUsuario().getCuenta()));
		return activarDesactivarUsuario;
	}

	public Object createConsultarUsuarioRequest(ServiciosLDAPRequest request) {
		ConsultarUsuario consultarUsuario = new ConsultarUsuario();
		consultarUsuario.setCuenta(super.createActivarDesactivarUsuarioCuenta(request.getUsuario().getCuenta()));
		return consultarUsuario;
	}

	public Object createEliminarUsuarioRequest(ServiciosLDAPRequest request) {
		EliminarUsuario eliminarUsuario = new EliminarUsuario();
		eliminarUsuario.setCuenta(super.createActivarDesactivarUsuarioCuenta(request.getUsuario().getCuenta()));
		return eliminarUsuario;
	}
}
