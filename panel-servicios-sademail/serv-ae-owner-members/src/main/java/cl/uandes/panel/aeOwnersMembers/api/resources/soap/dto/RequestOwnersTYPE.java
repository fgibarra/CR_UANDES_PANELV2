package cl.uandes.panel.aeOwnersMembers.api.resources.soap.dto;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="requestOwners")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "requestOwners", propOrder = {
		"funcion", "programas", "cuentas"
})
public class RequestOwnersTYPE {

	private String funcion;
	@XmlElementWrapper(name="programas")
	@XmlElement(name="programa")
	private List<String> programas;
	@XmlElementWrapper(name="cuentas")
	@XmlElement(name="cuenta")
	private List<String> cuentas;
	
	
	public RequestOwnersTYPE() {
		super();
	}


	public RequestOwnersTYPE(String funcion, List<String> programas, List<String> cuentas) {
		super();
		this.funcion = funcion;
		this.programas = programas;
		this.cuentas = cuentas;
	}


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
	
	public String getFuncion() {
		return funcion;
	}


	public void setFuncion(String funcion) {
		this.funcion = funcion;
	}


	public List<String> getProgramas() {
		return programas;
	}


	public void setProgramas(List<String> programas) {
		this.programas = programas;
	}


	public List<String> getCuentas() {
		return cuentas;
	}


	public void setCuentas(List<String> cuentas) {
		this.cuentas = cuentas;
	}
	
	
}
