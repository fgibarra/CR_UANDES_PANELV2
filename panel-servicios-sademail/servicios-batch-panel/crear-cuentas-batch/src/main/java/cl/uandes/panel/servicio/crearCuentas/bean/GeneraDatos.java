package cl.uandes.panel.servicio.crearCuentas.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Header;
import org.apache.camel.Message;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.servicios.dto.DatosCuentasBanner;
import cl.uandes.panel.comunes.servicios.dto.DatosKcoFunciones;
import cl.uandes.panel.comunes.json.batch.ProcesoDiarioRequest;
import cl.uandes.panel.comunes.json.batch.ProcesoDiarioResponse;
import cl.uandes.panel.comunes.json.batch.ContadoresCrearCuentas;

/**
 * Multiples metodos usados en las rutas Camel del proceso
 * @author fernando
 *setPeriodo
 */
public class GeneraDatos {

    @PropertyInject(value = "crear-cuentas-gmail.proceso", defaultValue="proceso")
	private String proceso;
    @PropertyInject(value = "crear-cuentas-gmail.kco-funcion", defaultValue="crear_cuentas")
	private String kcoFuncion;
	private final String tiposCuenta[] = {"Alumnos"};
	
	private Logger logger = Logger.getLogger(getClass());
	
	public void getKcoFuncion(Exchange exchange) {
		exchange.getIn().setHeader("proceso", getProceso());
	}

	public String getPeriodo(@Header(value = "DatosKcoFunciones")DatosKcoFunciones data) {
		if (data != null) {
			return data.getPeriodo();
		}
		return null;
	}
	
	/**
	 * Dejar en el header los datos necesarios para actualizar las tablas KCO_FUNCIONES y MI_RESULTADOS
	 * @param exchange
	 */
	public void resultadosABd(Exchange exchange) {
		Message message = exchange.getIn();
		// KCO_FUNCIONES
		logger.info(String.format("para KCO_FUNCIONES: key: %d", message.getHeader("keyResultado")));
		message.setHeader("key", message.getHeader("keyResultado"));
		
		// MI_RESULTADOS
		String comentario = new ContadoresCrearCuentas((Integer)message.getHeader("countProcesados"), 
				(Integer)message.getHeader("countErrores"),
				(Integer)message.getHeader("countAgregadosBD"), 
				(Integer)message.getHeader("countAgregadosAD")).toString();
		message.setHeader("jsonResultado",comentario.toString());
		logger.info(String.format("para MI_RESULTADOS: comentario:%s grupos_solicitados: %d grupos_creados:%d "+
				"miembros_solicitados:%d, miembros_creados:%d",
				message.getHeader("jsonResultado"), 
				message.getHeader("countProcesados"), message.getHeader("countErrores"),
				message.getHeader("countAgregadosBD"), message.getHeader("countAgregadosAD")));
	}
	
	/**
	 * Dejar en el Body la respuesta a la invocacion del proceso diario
	 * @param exchange
	 */
	public void procesoDiarioResponse(Exchange exchange) {
		Message message = exchange.getIn();
		ProcesoDiarioResponse response = new ProcesoDiarioResponse(0, (String)message.getHeader("proceso"), 
				String.format("OK: %s", message.getHeader("jsonResultado")), (Integer)message.getHeader("keyResultado"));
		logger.info(String.format("procesoDiarioResponse: response: %s", response));
		message.setBody(response);
	}
	/* ==========================================================================================================
	 * Para probar offline
	 * 
	 * para probar sin tener que invocar con soapui
	 */
	public void setBody(Exchange exchange) {
		ProcesoDiarioRequest req = new ProcesoDiarioRequest("generaCuentas", tiposCuenta);
		logger.info(String.format("generaRequestCrearCuentas: req:%s", req));
		exchange.getIn().setBody(req);
		exchange.getIn().setHeader("proceso", getKcoFuncion());
	}
	
	public ProcesoDiarioResponse setErrorInicializacion(@Header(value = "exception")Throwable exception, Exchange exchange) {
		String mensaje = exception.getMessage();
		return new ProcesoDiarioResponse(-1, getProceso(), mensaje, (Integer)exchange.getIn().getHeader("keyResultado") );
	}
	
	/**
	 * Recupera el ResultSet, genera la lista de DTOs para el proceso de las cuentas y lo deja en el header
	 * @param exchange
	 */
	public void factoryCuentas(Exchange exchange) {
		Message message = exchange.getIn();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> listaDatos = (List<Map<String, Object>>) message.getBody();
		List<DatosCuentasBanner> listaCuentas = new ArrayList<DatosCuentasBanner>();
		for (Map<String, Object> datos : listaDatos) {
			listaCuentas.add(new DatosCuentasBanner(datos));
		}
		Map<String, Object> headers = message.getHeaders();
		headers.put("listaCuentas", listaCuentas);
		headers.put("countCuentas", Integer.valueOf(0));
		headers.put("countErrores", Integer.valueOf(0));
		logger.info(String.format("listaCuentas.size: %d", listaCuentas.size()));
	}
	
	/**
	 * Saca de la lista de cuentas un elemento y lo deja para ser procesado en el loop
	 * @param exchange
	 */
	public void getCuentaFromListaCuentas(@Header(value = "listaCuentas")List<DatosCuentasBanner> listaCuentas, Exchange exchange) {
		Message message = exchange.getIn();
		DatosCuentasBanner dto = listaCuentas.remove(0);
		message.setHeader("DatosCuentasBanner", dto);
		logger.info(String.format("keyResultado %d", exchange.getIn().getHeader("keyResultado")));
		logger.info(String.format("getCuentaFromListaCuentas DatosCuentasBanner %s", dto));
	}
	
	// ==========================================================================================================
	// Getters y setters
	// ==========================================================================================================
	public String getKcoFuncion() {
		return kcoFuncion;
	}

	public void setKcoFuncion(String kcoFuncion) {
		this.kcoFuncion = kcoFuncion;
	}

	public String getProceso() {
		return proceso;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}
}
