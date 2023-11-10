package cl.uandes.comun.ad.client.soap.cxf.consultaXrut;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.cxf.binding.soap.Soap11;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.log4j.Logger;

/**
 * En teoria se podria agregar un header al message, pero no funciona
 * Se define en el blueprint tag: cxf:cxfEndpoint<br>
 * el el tag cxf:outInterceptors<br>
 * Nota: en este caso esta como una experiencia fallida
 * @author fernando
 *
 */
public class CxfHeaderOutInterceptor extends AbstractSoapInterceptor {

	private Logger logger = Logger.getLogger(getClass());
	
	public CxfHeaderOutInterceptor() {
		super(Phase.PREPARE_SEND_ENDING);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void handleMessage(SoapMessage message) throws Fault {
		logger.info("Entrando a CxfHeaderOutInterceptor");
		if (message.getVersion() instanceof Soap11) {
            Map<String, List<String>> headers = CastUtils.cast((Map)message.get(Message.PROTOCOL_HEADERS));
            dump (headers, "antes");
            if (headers != null) {
            	List<String> sa = headers.get("SOAPAction");
            	if (sa == null) {
            		headers.put("SOAPAction", Arrays.asList("urn:WSLDAPUANDES#consulta"));
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
