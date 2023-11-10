package cl.uandes.comun.ad.client.soap.cxf.ldapService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.cxf.binding.soap.Soap11;
import org.apache.cxf.binding.soap.SoapHeader;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.headers.Header;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.log4j.Logger;

/**
 * @author fernando
 *
 */
public class CxfHeaderOutInterceptor extends AbstractSoapInterceptor {

	private Logger logger = Logger.getLogger(getClass());
	
	public CxfHeaderOutInterceptor() {
		super(Phase.PREPARE_SEND_ENDING);
	}

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
//		StringBuffer sb = new StringBuffer();
		String action = "";
		for (java.util.Map.Entry<String,Object> entry : message.entrySet()) {
//			sb.append(entry.getKey()).append('=').append(entry.getValue()).append('\n');
			if (entry.getKey().equalsIgnoreCase("SOAPAction")) {
				action = (String) entry.getValue();
				break;
			}
		}
		logger.info(String.format("Entrando a CxfHeaderOutInterceptor: action=%s",
				action));
		
		if (message.getVersion() instanceof Soap11) {
            @SuppressWarnings("rawtypes")
			Map<String, List<String>> headers = CastUtils.cast((Map)message.get(Message.PROTOCOL_HEADERS));
            dump (headers, "antes");
            
            if (headers != null) {
            	List<String> sa = headers.get("SOAPAction");
            	if (sa == null) {
            		headers.put("SOAPAction", Arrays.asList(action));
                    dump (headers, "despues");
            	}
            }
		}
	}

	private void dump(Map<String, List<String>> headers, String ocacion) {
		logger.info(String.format("dump: %s", ocacion));
		for (String hdrKey : headers.keySet()) {
			StringBuffer sb = new StringBuffer();
			sb.append(String.format("hdrKey: %s\n", hdrKey));
			List<String> values = headers.get(hdrKey);
			logger.info(String.format("dump: values contiene size=%d",values.size()));
			for (String value : values) {
				sb.append(String.format("value: %s ", value));
			}
			sb.append("\n");
			logger.info(sb.toString());
		}		
	}
}
