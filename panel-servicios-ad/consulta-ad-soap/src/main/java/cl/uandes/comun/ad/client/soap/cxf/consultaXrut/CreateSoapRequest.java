package cl.uandes.comun.ad.client.soap.cxf.consultaXrut;

import java.io.IOException;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;

/**
 * Bean para generar el Request en base a las clases definidas en el package<br>
 * cl.uandes.comun.ad.client.soap.cxf.consultaXrut.dto
 * @author fernando
 *
 */
public class CreateSoapRequest {

	Logger logger = Logger.getLogger(getClass());
	
    @PropertyInject(value = "consultaXrut.operationName", defaultValue="consulta")
    private String operationName;
    @PropertyInject(value = "consultaXrut.soapAction", defaultValue="consulta")
    private String soapAction;

    private String template;
    
    public CreateSoapRequest(String templateName) {
    	java.io.BufferedReader rr = new java.io.BufferedReader(new java.io.InputStreamReader(getClass().getClassLoader().getResourceAsStream(templateName)));
    	StringBuffer sb = new StringBuffer();
    	String line;
        try {
			while ((line = rr.readLine()) != null) 
			        sb.append(line);
		} catch (IOException e) {
			;
		}
        this.template = sb.toString();    	
    }    /**
	 * Deja en el Body el request a enviar al SOAP<br>
	 * @param usr RUT a consultar
	 * @param exchange message Camel
	 */
	public void createSoapBody(@Header("id") String usr, Exchange exchange) {
		/* 
		cl.uandes.comun.ad.client.soap.cxf.consultaXrut.dto.ObjectFactory factory = new cl.uandes.comun.ad.client.soap.cxf.consultaXrut.dto.ObjectFactory();
		cl.uandes.comun.ad.client.soap.cxf.consultaXrut.dto.ConsultaRutAdRequest datos = factory.createConsultaRutAdRequest(usr, null);
		
		 
		exchange.getIn().setBody(datos);
		/* 
		
		exchange.getIn().setHeader("OPERATION_NAME", operationName);
		exchange.getIn().setHeader("operation", operationName);
		exchange.getIn().setHeader("SOAPAction", soapAction);

		/* */
		String envelop = String.format(template, usr);
		logger.info(String.format("envelop: %s", envelop));
		exchange.getIn().setBody(envelop);
		
	}

	public String getOperationName() {
		return operationName;
	}

	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	public String getSoapAction() {
		return soapAction;
	}

	public void setSoapAction(String soapAction) {
		this.soapAction = soapAction;
	}

}
