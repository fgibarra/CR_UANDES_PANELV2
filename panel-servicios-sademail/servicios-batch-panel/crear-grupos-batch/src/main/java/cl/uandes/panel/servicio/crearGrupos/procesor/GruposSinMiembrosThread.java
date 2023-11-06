package cl.uandes.panel.servicio.crearGrupos.procesor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.servicios.dto.GruposMiUandes;
import cl.uandes.panel.comunes.servicios.dto.ResultadoFuncion;
import cl.uandes.panel.comunes.json.batch.ContadoresCrearGrupos;

/**
 * Action que elimina los grupos que quedaron sin miembros
 * @author fernando
 *
 */
public class GruposSinMiembrosThread implements Processor {

    @PropertyInject(value = "crear-grupos-azure.uri-azureServices", defaultValue="http://localhost:8181/cxf/ESB/panelV3/azureServices")
	private String azureServices;
	
	private GruposThread delegate;
	
	@EndpointInject(uri = "sql:classpath:sql/deleteGrupo.sql?dataSource=#bannerDataSource")
	ProducerTemplate deleteGrupo;

	@EndpointInject(uri = "cxfrs:bean:rsDeleteGrupo?continuationTimeout=-1")
	//	@EndpointInject(uri = "direct:testDeleteGrupoAzure")
	ProducerTemplate deleteGrupoAzure;
	private final String uriDeleteGrupoTemplate = "%s/group/delete?groupId=%s";

	private ResultadoFuncion res;
	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		logger.info(String.format("delegate es %s NULO", delegate==null?"":"NO"));
		String url = null;
		String operacion = null;
		@SuppressWarnings("unchecked")
		List<GruposMiUandes> listaGrupos = (List<GruposMiUandes>)exchange.getIn().getHeader("listaGrupos");
		ContadoresCrearGrupos contadores = (ContadoresCrearGrupos)exchange.getIn().getHeader("contadoresCrearGrupos");
		res = (ResultadoFuncion)exchange.getIn().getHeader("ResultadoFuncion");
		
		logger.info(String.format("GruposSinMiembrosThread: listaGrupos: %d elementos", listaGrupos!=null?listaGrupos.size():0));
		if (listaGrupos != null && listaGrupos.size() > 0) {
			Map<String,Object> headers = exchange.getIn().getHeaders();
			for (GruposMiUandes grupo : listaGrupos) {
				url = String.format(uriDeleteGrupoTemplate, getAzureServices(), grupo.getGroupId());
				operacion = "DELETE";
				headers.put(Exchange.DESTINATION_OVERRIDE_URL, url);
				headers.put("CamelHttpMethod", operacion);
				Response response = (Response) deleteGrupoAzure.requestBodyAndHeaders(null, headers);
				
				if (response.getStatus() < 300) {
					contadores.incCountGruposSacadosAD();
					contadores.incCountGruposSacadosBD();
					// borrar de la BD
					headers.put("keyGrupo", new BigDecimal(grupo.getKey()));
					deleteGrupo.requestBodyAndHeaders(null, headers);
				} else {
					contadores.incCountErrores();
					delegate.registraError("deleteGrupoAzure", response.getStatusInfo().getReasonPhrase(), new BigDecimal(res.getKey()),new BigDecimal(grupo.getKey()));
				}
			}
		}
	}

	public String getAzureServices() {
		return azureServices;
	}

	public void setAzureServices(String azureServices) {
		this.azureServices = azureServices;
	}

	public GruposThread getDelegate() {
		return delegate;
	}

	public void setDelegate(GruposThread delegate) {
		this.delegate = delegate;
	}

}
