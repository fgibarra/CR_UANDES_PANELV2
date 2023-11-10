package cl.uandes.comun.ad.client.soap.cxf.consultaXrut;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import cl.uandes.comun.ad.client.soap.cxf.consultaXrut.dto.ConsultaRutAdRequest;
import cl.uandes.comun.ad.client.soap.cxf.consultaXrut.dto.Retorno;

/**
 * Endpoint para usar WS wsLdapUandespanel.php
 * @author fernando
 *
 */
@WebService(targetNamespace = "urn:WSLDAPUANDES", name = "WS Consulta LDAP")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public interface ConsultaRutADService {

	@WebMethod
	@WebResult(name = "consultaResponse", targetNamespace = "urn:WSLDAPUANDES", partName = "retorno")
	public Retorno consulta(
			@WebParam(partName = "retorno", name = "retorno", targetNamespace = "urn:WSLDAPUANDES")
			ConsultaRutAdRequest datos);
}
