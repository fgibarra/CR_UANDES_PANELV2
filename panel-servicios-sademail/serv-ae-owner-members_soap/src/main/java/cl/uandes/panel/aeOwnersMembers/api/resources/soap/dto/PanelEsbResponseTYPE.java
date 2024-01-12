package cl.uandes.panel.aeOwnersMembers.api.resources.soap.dto;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "invocaServicioResponse", propOrder = {
		"codigo", "glosa"
})
public class PanelEsbResponseTYPE {

	@XmlElement(required = true)
	private Integer codigo;
	@XmlElement(required = true)
	private String glosa;

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
	
	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getGlosa() {
		return glosa;
	}

	public void setGlosa(String glosa) {
		this.glosa = glosa;
	}

}
