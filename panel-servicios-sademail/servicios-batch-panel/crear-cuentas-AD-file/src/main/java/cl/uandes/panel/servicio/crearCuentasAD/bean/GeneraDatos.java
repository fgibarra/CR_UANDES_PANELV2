package cl.uandes.panel.servicio.crearCuentasAD.bean;

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

import cl.uandes.panel.comunes.servicios.dto.CuentasADDTO;

public class GeneraDatos {

	@PropertyInject(value = "crear-cuentas-gmail.proceso", defaultValue = "proceso")
	private String proceso;
	@PropertyInject(value = "crear-cuentas-gmail.kco-funcion", defaultValue = "crear_cuentas")
	private String kcoFuncion;

	@EndpointInject(uri = "sql:classpath:paraCrearCuentasAD.sql?dataSource=#bannerDataSource")
	ProducerTemplate paraCrearCuentasAD;
	
	private Logger logger = Logger.getLogger(getClass());

	/**
	 * Genera lista para crear cuentas AD a partir del archivo leido
	 * 
	 * @param exchange
	 */
	public void generaListaXfile(Exchange exchange) {
		Message message = exchange.getIn();
		GenericFile<?> body = (GenericFile<?>)message.getBody();
		List<CuentasADDTO> lista = new ArrayList<CuentasADDTO>();
		logger.info(String.format("generaListaXfile: body: %s\n%s", body.getClass().getSimpleName(), body));
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(body.getAbsoluteFilePath()),"ISO_8859_1"));
			String linea = null;
			String titulos = null;
			int numLinea = 0;
			while ((linea = reader.readLine()) != null) {
				if (linea.length() > 1) {
					logger.info(String.format("generaListaXfile: leido: %s", linea));
					linea = linea.replace("\",", "\u0003");
					linea = linea.replace("\"", "");
					if (numLinea == 0) {
						if (linea.matches(".*NOMBRES.*")) { 
							numLinea++;
							titulos = linea;
							logger.info(String.format("titulo: %s", titulos));
							continue;
						}
					}
					logger.info(String.format("generaListaXfile: linea a procesar: %s", linea));
					CuentasADDTO dto = new CuentasADDTO(titulos, linea);
					logger.info(String.format("dto: %s", dto));
					lista.add(dto);
				}
			}
		} catch (Exception e) {
			logger.error(String.format("generaListaXfile: body: %s\n%s", body.getClass().getSimpleName(), body), e);
		}
		logger.info(String.format("generaListaXfile: listaCuentas.size=%d", lista.size()));
		message.setHeader("listaCuentas", lista);
	}

	/**
	 * Genera lista para crear cuentas AD segun el request recibido
	 * 
	 * @param exchange
	 */
	@SuppressWarnings("unchecked")
	public void generaListaXrequest(Exchange exchange) {
		Message message = exchange.getIn();
		List<CuentasADDTO> lista = new ArrayList<CuentasADDTO>();
		
		List<Map<String, Object>> datos = (List<Map<String, Object>>) paraCrearCuentasAD.requestBody(null);
		if (datos != null && datos.size() > 0) {
			for (Map<String, Object> dato : datos) {
				lista.add(new CuentasADDTO(dato));
			}
		}
		message.setHeader("listaCuentas", lista);
	}

	/**
	 * Saca un elemento de la lista y lo coloca en el header pra su proceso por el Process
	 * @param exchange
	 */
	public void getCuentaFromListaCuentas(Exchange exchange) {
		Message message = exchange.getIn();
		@SuppressWarnings("unchecked")
		List<CuentasADDTO> lista = (List<CuentasADDTO>)message.getHeader("listaCuentas");
		CuentasADDTO dto = lista.remove(0);
		message.setHeader("CuentasADDTO", dto);
	}
	//===============================================================================================================
	// Getters y Setters
	//===============================================================================================================
	
	public String getProceso() {
		return proceso;
	}

	public void setProceso(String proceso) {
		this.proceso = proceso;
	}

	public String getKcoFuncion() {
		return kcoFuncion;
	}

	public void setKcoFuncion(String kcoFuncion) {
		this.kcoFuncion = kcoFuncion;
	}

}
