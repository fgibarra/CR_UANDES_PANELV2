package cl.uandes.panel.servicio.owners.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.bean.RegistrosComunes;
import cl.uandes.panel.comunes.servicios.dto.ResultadoFuncion;

public class GeneraDatos {

	private RegistrosComunes registrosBD;

	@EndpointInject(uri = "sql:classpath:sql/qryGruposFromNapOwners.sql?dataSource=#bannerDataSource")
	ProducerTemplate qryGruposFomNapOwners;

	private ResultadoFuncion res;
	private Logger logger = Logger.getLogger(getClass());

    /**
	 * Debe dejar listaGruposSincronizar en ${header.listaGruposSincronizar} los grupos 
	 * ledos desde NAP_GRUPO_OWNER
	 * @param exchange
	 */
	public void recuperaGrupos(Exchange exchange) {
		res = (ResultadoFuncion)exchange.getIn().getHeader("ResultadoFuncion");
		List<String> listaGruposSincronizar = new ArrayList<String>();
		try {
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> datos = (List<Map<String, Object>>) qryGruposFomNapOwners.requestBody(null);
			if (datos != null && datos.size() > 0) {
				for (Map<String, Object> map : datos) {
					listaGruposSincronizar.add((String)map.get("GROUP_NAME"));
				}
			}
		} catch (Exception e) {
			registrosBD.registraMiResultadoErrores("NA", "recuperaGrupos: al invocar qryGruposFomNapOwners", e, null, res.getKey());
		}
		exchange.getIn().setHeader("listaGruposSincronizar", listaGruposSincronizar);
		logger.info(String.format("recuperaGrupos: listaGruposSincronizar.size=%d", listaGruposSincronizar.size()));
	}
	
	/**
	 * Debe dejar en el body el group_name
	 * @param exchange
	 */
	public void recuperaGrupo(Exchange exchange) {
		Message message = exchange.getIn();
		String grupo = null;
		@SuppressWarnings("unchecked")
		List<String> listaGruposSincronizar = (List<String>) message.getHeader("listaGruposSincronizar");
		if (listaGruposSincronizar.size() > 0)
			grupo = listaGruposSincronizar.remove(0);
		message.setBody(grupo);
	}

	/**
	 * Debe dejar en el Body el grupo,cuenta de wwwwwrk_owners_grupo
	 * @param exchange
	 */
	public void recuperaGrupoCuenta(Exchange exchange) {
		
	}
	//======================================================================================================================================
	// Getters y Setters
	//======================================================================================================================================

	public synchronized RegistrosComunes getRegistrosBD() {
		return registrosBD;
	}

	public synchronized void setRegistrosBD(RegistrosComunes registrosBD) {
		this.registrosBD = registrosBD;
	}
	
}
