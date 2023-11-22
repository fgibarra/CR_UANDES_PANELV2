package cl.uandes.comun.ad.client.soap.cxf.consultaXrut.dto;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * @author fernando
 *
 */
@XmlRegistry
public class ObjectFactory {
	private final static QName _consultaRutAdRequest_QNAME = new QName("urn:consulta", "consulta");
	private final static QName _datos_QNAME = new QName("urn:datos", "datos");
	private final static QName _consultaRutAdResponse_QNAME = new QName("urn:retorno", "retorno");
	
	public ObjectFactory() {
		super();
	}

	public ConsultaRutAdRequest createConsultaRutAdRequest() {
		return new ConsultaRutAdRequest();
	}
	
	public ConsultaRutAdRequest createConsultaRutAdRequest(String id, String pwd) {
		ConsultaRutAdRequest request = new ConsultaRutAdRequest();
		request.setDatos(createDatos(id, pwd));
		return request;
	}
	
    @XmlElementDecl(namespace = "urn:consulta", name = "consulta")
    public JAXBElement<ConsultaRutAdRequest> createConsultaRutAdRequest(ConsultaRutAdRequest value) {
    	return new JAXBElement<ConsultaRutAdRequest>(_consultaRutAdRequest_QNAME, ConsultaRutAdRequest.class, null, value);
    }
    
    public Datos createDatos() {
    	return new Datos();
    }

    public Datos createDatos(String id, String pwd) {
    	Datos datos = new Datos();
    	datos.setId(id);
    	datos.setPwd(pwd);
    	return datos;
    }

    @XmlElementDecl(namespace = "urn:datos", name = "datos", scope=ConsultaRutAdRequest.class)
    public JAXBElement<Datos> createDatos(Datos value) {
    	return new JAXBElement<Datos>(_datos_QNAME, Datos.class, null, value);
    }
    
    public Retorno createConsultaRutAdResponse() {
    	return new Retorno();
    }
    
    @XmlElementDecl(namespace = "urn:retorno", name = "retorno")
    public JAXBElement<Retorno> createConsultaRutAdResponse(Retorno value) {
    	return new JAXBElement<Retorno>(_consultaRutAdResponse_QNAME, Retorno.class, null, value);
    }
    
    
}
