package cl.uandes.panel.apiCrearCuentasServices.bean;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.camel.component.file.GenericFile;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.creaCuenta.CreaCuentaRequest;

public class ValidaEnAD {

	public void validaUserPrincipalName(Exchange exchange) {
		Message message = exchange.getIn();
		CreaCuentaRequest request = (CreaCuentaRequest)message.getHeader("request");
		String rut = request.getRut();
		if (rut != null) {
			
		}
	}
}
