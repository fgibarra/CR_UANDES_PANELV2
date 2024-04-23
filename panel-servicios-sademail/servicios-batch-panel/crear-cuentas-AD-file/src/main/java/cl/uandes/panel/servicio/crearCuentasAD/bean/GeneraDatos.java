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

import cl.uandes.panel.comunes.json.batch.ProcesoDiarioRequest;
import cl.uandes.panel.comunes.servicios.dto.CuentasADDTO;
import cl.uandes.panel.comunes.utils.CountThreads;
import cl.uandes.sadmemail.comunes.utils.StringUtils;

public class GeneraDatos {

	@PropertyInject(value = "crear-cuentas-gmail.proceso", defaultValue = "proceso")
	private String proceso;
	@PropertyInject(value = "crear-cuentas-gmail.kco-funcion", defaultValue = "crear_cuentas")
	private String kcoFuncion;
	@PropertyInject(value = "crear-cuentas-ad_posgrado.max_lista", defaultValue = "2000")
	private String maxListaStr;
	@PropertyInject(value = "crear-cuentas-ad_posgrado.periodo_inicial", defaultValue = "20240301")
	private String periodoInicial;
	
	@EndpointInject(uri = "sql:classpath:sql/paraCrearCuentasAD.sql?dataSource=#bannerDataSource")
	ProducerTemplate paraCrearCuentasAD;
	@EndpointInject(uri = "sql:classpath:sql/paraCrearCuentasAD_todos.sql?dataSource=#bannerDataSource")
	ProducerTemplate paraCrearCuentasADTotal;
	@EndpointInject(uri = "sql:classpath:sql/paraCrearCuentasAD_bdc.sql?dataSource=#bannerDataSource")
	ProducerTemplate paraCrearCuentasADBDC;
	
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
	public void generaListaXrequest(Exchange exchange) {
		Message message = exchange.getIn();
		List<CuentasADDTO> lista = new ArrayList<CuentasADDTO>();
		ProcesoDiarioRequest request = (ProcesoDiarioRequest)message.getHeader("request");
		if (request.getOperaciones() == null || request.getOperaciones().length == 0) {		
			lista = getListaNormal();
		} else {
			// si viene un numero, usa el sql paraCrearCuentasAD_todos
			String valor = request.getOperaciones()[0];
			if (StringUtils.esNumerico(valor)) {
				setMaxListaStr(valor);
				lista = getListaTotal();
				logger.info(String.format("generaListaXrequest: se procesaran %d registros leidos desde Banner", lista.size()));
			} else {
				if ("BDC".equalsIgnoreCase(valor)) {
					lista = getListaBdc();
					logger.info(String.format("generaListaXrequest: se procesaran %d registros leidos desde BDC", lista.size()));
				}
			}
		}
		message.setHeader("listaCuentas", lista);
	}

	private List<CuentasADDTO> getListaNormal() {
		List<CuentasADDTO> lista = new ArrayList<CuentasADDTO>();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> datos = (List<Map<String, Object>>) paraCrearCuentasAD.requestBody(null);
		if (datos != null && datos.size() > 0) {
			for (Map<String, Object> dato : datos) {
				lista.add(new CuentasADDTO(dato));
			}
		}
		logger.info(String.format("getListaNormal: elementos en la lista: %d", lista.size()));
		return lista;
	}
	
	private List<CuentasADDTO> getListaTotal() {
		List<CuentasADDTO> lista = new ArrayList<CuentasADDTO>();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> datos = (List<Map<String, Object>>) paraCrearCuentasADTotal.requestBody(null);
		if (datos != null && datos.size() > 0) {
			int count = getMaxLista();
			for (Map<String, Object> dato : datos) {
				lista.add(new CuentasADDTO(dato));
				if (--count <= 0)
					break;
			}
		}
		logger.info(String.format("getListaTotal: elementos en la lista: %d", lista.size()));
		return lista;
	}
	
	private List<CuentasADDTO> getListaBdc() {
		List<CuentasADDTO> lista = new ArrayList<CuentasADDTO>();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> datos = (List<Map<String, Object>>) 
						paraCrearCuentasADBDC.requestBodyAndHeader(null, "periodo", periodoInicial);
		if (datos != null && datos.size() > 0) {
			int count = getMaxLista();
			for (Map<String, Object> dato : datos) {
				lista.add(new CuentasADDTO(dato));
				if (--count <= 0)
					break;
			}
		}
		logger.info(String.format("getListaTotal: elementos en la lista: %d", lista.size()));
		return lista;
	}
	
	private int getMaxLista() {
		if (getMaxListaStr() == null)
			return 0;
		try {
			return Integer.valueOf(getMaxListaStr());
		} catch (NumberFormatException e) {
			logger.error(String.format("getMaxLista: error al convertir %s", getMaxListaStr()), e);
			return 0;
		}
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
		CountThreads countThread = (CountThreads) message.getHeader("countThread");
		countThread.incCounter();
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

	public String getMaxListaStr() {
		return maxListaStr;
	}

	public void setMaxListaStr(String maxListaStr) {
		this.maxListaStr = maxListaStr;
	}

	public String getPeriodoInicial() {
		return periodoInicial;
	}

	public void setPeriodoInicial(String periodoInicial) {
		this.periodoInicial = periodoInicial;
	}

}
