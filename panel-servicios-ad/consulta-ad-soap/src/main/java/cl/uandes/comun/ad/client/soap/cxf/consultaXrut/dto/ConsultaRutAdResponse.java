package cl.uandes.comun.ad.client.soap.cxf.consultaXrut.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author fernando
 *
 */
@XmlRootElement(name = "consultaResponse",namespace="urn:")
@XmlAccessorType(XmlAccessType.FIELD) // indica que las anotaciones van en el atributo
@XmlType(name = "consultaResponse", propOrder = {"retorno"})
public class ConsultaRutAdResponse {

	@XmlElement(name="retorno")
	Retorno retorno;

	public ConsultaRutAdResponse() {
		super();
	}

	public Retorno getRetorno() {
		return retorno;
	}

	public void setRetorno(Retorno retorno) {
		this.retorno = retorno;
	}

}
