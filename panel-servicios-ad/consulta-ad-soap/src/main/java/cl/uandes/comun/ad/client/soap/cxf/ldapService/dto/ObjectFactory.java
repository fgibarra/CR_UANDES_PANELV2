package cl.uandes.comun.ad.client.soap.cxf.ldapService.dto;

import org.tempuri.ActivarDesactivarUsuario;
import org.tempuri.ActualizarUsuario;
import org.tempuri.CrearUsuario;
import org.tempuri.DesbloquearUsuario;
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
		validarUsuario.setNombreUsuario(super.createValidarUsuarioNombreUsuario(request.getUsuario().getCuenta()));
		return validarUsuario;
	}

	public Object createActivarResetearPasswordRequest(ServiciosLDAPRequest request) {
		ResetearPassword resetearPassword = new ResetearPassword();
		resetearPassword.setNombreUsuario(super.createResetearPasswordNombreUsuario(request.getUsuario().getCuenta()));
		resetearPassword.setNuevaPassword(super.createResetearPasswordNuevaPassword(request.getUsuario().getPassword()));
		return resetearPassword;
	}

	public Object createDesbloquearUsuarioRequest(ServiciosLDAPRequest request) {
		DesbloquearUsuario desbloquearUsuario = new DesbloquearUsuario();
		desbloquearUsuario.setNombreUsuario(super.createDesbloquearUsuarioNombreUsuario(request.getUsuario().getCuenta()));
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

		crearUsuario.setUsuario(createCrearUsuarioUsuario(usuario));
		return crearUsuario;
	}

	public Object createActualizarUsuarioRequest(ServiciosLDAPRequest request) {
		ActualizarUsuario actualizarUsuario = super.createActualizarUsuario();
		actualizarUsuario.setNombreUsuario(super.createActualizarUsuarioNombreUsuario(request.getUsuario().getCuenta()));
		if (request.getUsuario().getApellidos() != null)
			actualizarUsuario.setApellido(super.createActualizarUsuarioApellido(request.getUsuario().getApellidos()));
		if (request.getUsuario().getCargo() != null)
			actualizarUsuario.setCargo(super.createActualizarUsuarioCargo(request.getUsuario().getCargo()));
		if (request.getUsuario().getCompania() != null)
			actualizarUsuario.setCompania(super.createActualizarUsuarioCompania(request.getUsuario().getCompania()));
		if (request.getUsuario().getDepartamento() != null)
			actualizarUsuario.setDepartamento(super.createActualizarUsuarioDepartamento(request.getUsuario().getDepartamento()));
		if (request.getUsuario().getJefatura() != null)
			actualizarUsuario.setJefatura(super.createActualizarUsuarioJefatura(request.getUsuario().getJefatura()));
		if (request.getUsuario().getNombre() != null)
			actualizarUsuario.setNombre(super.createActualizarUsuarioNombre(request.getUsuario().getNombre()));
		if (request.getUsuario().getTelefono() != null)
			actualizarUsuario.setTelefono(super.createActualizarUsuarioTelefono(request.getUsuario().getTelefono()));
		return actualizarUsuario;
	}

	public Object createActivarDesactivarUsuarioRequest(ServiciosLDAPRequest request) {
		ActivarDesactivarUsuario activarDesactivarUsuario = new ActivarDesactivarUsuario();
		activarDesactivarUsuario.setActivar(request.getActivar());
		activarDesactivarUsuario.setNombreUsuario(super.createActivarDesactivarUsuarioNombreUsuario(request.getUsuario().getCuenta()));
		return activarDesactivarUsuario;
	}

}
