package cl.uandes.panel.aeOwnersMembers.api.resources.soap.dto;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="request")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "request", propOrder = {
		"servicio", "criterio"
})
public class PanelRequestTYPE {

	@XmlElement(required = true)
	private String servicio;
	@XmlElement(required = true)
	private String criterio;

	@Override
	public String toString() {
		try {
			JAXBContext jc = JAXBContext.newInstance(this.getClass().getPackage().getName());
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			m.marshal(this, new PrintStream(out));
			//m.marshal(xml, System.out);
			return out.toString();
		} catch (Exception e) {
			return null;
		}
	}
	
	public String getServicio() {
		return servicio;
	}

	public void setServicio(String servicio) {
		this.servicio = servicio;
	}

	public String getCriterio() {
		return criterio;
	}

	public void setCriterio(String criterio) {
		this.criterio = criterio;
	}

}
