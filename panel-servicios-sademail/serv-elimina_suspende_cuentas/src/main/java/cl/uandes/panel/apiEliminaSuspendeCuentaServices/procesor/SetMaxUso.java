package cl.uandes.panel.apiEliminaSuspendeCuentaServices.procesor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.cambiacuenta.EliminaSuspendeCuentaRequest;

public class SetMaxUso implements Processor {

    @PropertyInject(value = "serv-elimina-suspende-cuentas.maxUso")
	private String maxUso;
    
	private Logger logger = Logger.getLogger(getClass());

	@Override
	public void process(Exchange exchange) throws Exception {
		EliminaSuspendeCuentaRequest request = (EliminaSuspendeCuentaRequest)exchange.getIn().getHeader("EliminaSuspendeCuentaRequest");
		if (request.getUso() != null && request.getUso() > 0)
			setMaxUso(String.format("%d", request.getUso()));

		logger.info(String.format("process: maxUso: %s leido desde %s", maxUso, request.getUso() != null?"REQUEST":"Propiedades"));
		exchange.getIn().setHeader("max_uso", Integer.valueOf(getMaxUso()));
	}

	public String getMaxUso() {
		return maxUso;
	}

	public void setMaxUso(String maxUso) {
		this.maxUso = maxUso;
	}

}
