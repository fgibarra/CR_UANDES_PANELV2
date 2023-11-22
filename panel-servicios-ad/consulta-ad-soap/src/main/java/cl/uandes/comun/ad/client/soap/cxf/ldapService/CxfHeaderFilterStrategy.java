package cl.uandes.comun.ad.client.soap.cxf.ldapService;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultHeaderFilterStrategy;
import org.apache.log4j.Logger;

/**
 * @author fernando
 *
 */
public class CxfHeaderFilterStrategy extends DefaultHeaderFilterStrategy {

	Logger logger = Logger.getLogger(getClass());
	
    @Override
    public boolean applyFilterToCamelHeaders(String headerName, Object headerValue, Exchange exchange) {
    	logger.info(String.format("headerName: %s | headerValue: %s", headerName, headerValue));
        return true;
    }
}
