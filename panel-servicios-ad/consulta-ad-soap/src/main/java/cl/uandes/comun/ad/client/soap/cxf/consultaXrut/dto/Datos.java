package cl.uandes.comun.ad.client.soap.cxf.consultaXrut.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author fernando
 *
 */
@XmlRootElement(name = "datos", namespace = "urn:WSLDAPUANDES")
@XmlAccessorType(XmlAccessType.FIELD) // indica que las anotaciones van en el atributo
@XmlType(name = "tns:datos", propOrder = { "id", "pwd" })
public class Datos implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1460130847891222871L;
	@XmlElement(name = "id")
	private String id;
	@XmlElement(name = "pwd")
	private String pwd;

	public Datos() {
		super();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id=").append(id).append('\n');
		sb.append("pwd=").append(pwd).append('\n');
		return sb.toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

}
