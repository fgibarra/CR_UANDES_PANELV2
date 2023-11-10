package cl.uandes.comun.ad.client.soap.cxf.consultaXrut.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * @author fernando
 *
 */
@XmlRootElement(name = "consulta", namespace = "urn")
@XmlAccessorType(XmlAccessType.FIELD) // indica que las anotaciones van en el atributo
@XmlType(name = "consulta")
public class ConsultaRutAdRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8645217249155185985L;
	@XmlElement(name="datos")
	private Datos datos;

	public ConsultaRutAdRequest() {
		super();
	}

	public Datos getDatos() {
		return datos;
	}

	public void setDatos(Datos datos) {
		this.datos = datos;
	}

}
