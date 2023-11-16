package cl.uandes.comun.ad.client.soap.cxf.consultaXrut;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultHeaderFilterStrategy;
import org.apache.log4j.Logger;

/**
 * Para filtrar datos no deseados en el header<br>
 * Se define en el tag:<br>
 * to id="_to1" uri="cxf:bean:cxfClientConsultaXrutEndpoint?dataFormat=MESSAGE&amp;headerFilterStrategy=#cxfConsultaXrutHeaderFilterStrategy"<br>
 * Nota: en este caso esta como una experiencia fallida
 * @author fernando
 *
 */
public class CxfHeaderFilterStrategy extends DefaultHeaderFilterStrategy {
	
	Logger logger = Logger.getLogger(getClass());
	
    @Override
    public boolean applyFilterToCamelHeaders(String headerName, Object headerValue, Exchange exchange) {
    	logger.info(String.format("headerName: %s | headerValue: %s", headerName, headerValue));
    	Object body = exchange.getIn().getBody();
    	logger.info(String.format("en el applyFilterToCamelHeaders clase del body |%s|", body.getClass().getName()));
        return true;
    }
}