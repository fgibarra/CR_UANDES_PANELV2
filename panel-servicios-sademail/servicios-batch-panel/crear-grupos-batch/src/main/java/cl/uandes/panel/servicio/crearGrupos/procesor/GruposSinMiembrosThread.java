package cl.uandes.panel.servicio.crearGrupos.procesor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.bean.RegistrosComunes;
import cl.uandes.panel.comunes.json.batch.ContadoresCrearGrupos;
import cl.uandes.panel.comunes.servicios.dto.GruposMiUandes;
import cl.uandes.panel.comunes.servicios.dto.ResultadoFuncion;

/**
 * Action que elimina los grupos que quedaron sin miembros
 * @author fernando
 *
 */
public class GruposSinMiembrosThread implements Processor {

    @PropertyInject(value = "crear-grupos-gmail.uri-gmailServices", defaultValue="http://localhost:8181/cxf/ESB/panel/gmailServices")
	private String gmailServices;
	
    @PropertyInject(value = "registrosComunes")
	private RegistrosComunes registrosBD;
		
	@EndpointInject(uri = "sql:classpath:sql/deleteGrupo.sql?dataSource=#bannerDataSource")
	ProducerTemplate deleteGrupo;

	@EndpointInject(uri = "cxfrs:bean:rsDeleteGrupo?continuationTimeout=-1")
	ProducerTemplate deleteGrupoGmail;
	private final String uriDeleteGrupoTemplate = "%s/group/delete/%s";

	private ResultadoFuncion res;
	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		try {
			logger.info(String.format("registrosBD es %s NULO", registrosBD==null?"":"NO"));
			String url = null;
			String operacion = null;
			@SuppressWarnings("unchecked")
			List<GruposMiUandes> listaGrupos = (List<GruposMiUandes>)exchange.getIn().getHeader("listaGrupos");
			ContadoresCrearGrupos contadores = (ContadoresCrearGrupos)exchange.getIn().getHeader("contadoresCrearGrupos");
			res = (ResultadoFuncion)exchange.getIn().getHeader("ResultadoFuncion");
			
			logger.info(String.format("GruposSinMiembrosThread: listaGrupos: %d elementos", listaGrupos!=null?listaGrupos.size():0));
			if (listaGrupos != null && listaGrupos.size() > 0) {
				Map<String,Object> headers = new HashMap<String,Object>(); 
				for (GruposMiUandes grupo : listaGrupos) {
					logger.info(String.format("GruposSinMiembros: delete grupo %s", grupo));
					url = String.format(uriDeleteGrupoTemplate, getGmailServices(), grupo.getGroupId());
					operacion = "DELETE";
					headers.put(Exchange.DESTINATION_OVERRIDE_URL, url);
					headers.put("CamelHttpMethod", operacion);
					Response response = (Response) deleteGrupoGmail.requestBodyAndHeaders(null, headers);
					
					if (response.getStatus() < 300) {
						contadores.incCountGruposSacadosAD();
						contadores.incCountGruposSacadosBD();
						// borrar de la BD
						headers.clear();
						headers.put("keyGrupo", new BigDecimal(grupo.getKey()));
						deleteGrupo.requestBodyAndHeaders(null, headers);
					} else {
						contadores.incCountErrores();
						registrosBD.registraMiResultadoErrores("NA", "recuperaGrupoGmail", response.getStatusInfo().getReasonPhrase(),
								grupo.getKey(), res.getKey());
					}
				}
			}
		} catch (Exception e) {
			if (res != null)
				registrosBD.registraMiResultadoErrores("NA", null, e, null, res.getKey());
		}
	}

	public String getGmailServices() {
		return gmailServices;
	}

	public void setGmailServices(String gmailServices) {
		this.gmailServices = gmailServices;
	}

	public RegistrosComunes getRegistrosBD() {
		return registrosBD;
	}

	public void setRegistrosBD(RegistrosComunes registrosBD) {
		this.registrosBD = registrosBD;
	}

}
