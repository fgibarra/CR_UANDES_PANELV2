package cl.uandes.panel.servicio.owners.add_delete.procesor;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.bean.RegistrosComunes;
import cl.uandes.panel.comunes.json.aeOwnersMembers.AeOwnersMembersRequest;
import cl.uandes.panel.comunes.json.batch.ContadoresAsignarOwners;
import cl.uandes.panel.comunes.servicios.dto.ResultadoFuncion;
import cl.uandes.panel.servicio.owners.api.resource.OwnersRestService;
import java.sql.Array;
import oracle.jdbc.driver.OracleConnection;

public class GeneraWrkOwnersGrupos implements Processor {

	private RegistrosComunes registrosBD;
	private DataSource dataSource;

	@EndpointInject(uri = "sql-stored:classpath:sql/prd_pueblaWrkOwnersGroups.sql?dataSource=#bannerDataSource")
	ProducerTemplate pueblaWrkOwnersGroups;

	private ResultadoFuncion res;
	private ContadoresAsignarOwners contadores;
	private Logger logger = Logger.getLogger(getClass());
	/**
	 * Puebla la tabla WRK_OWNERS_GRUPOS para las combinaciones de programas y correos en el request
	 * Si el servicio es owner, poblar con todos los grupos y en la cuenta viene el email
	 * Si el servicio es member, programa contiene los grupos y cuenta contiene los ruts. 
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		contadores = (ContadoresAsignarOwners)message.getHeader("contadoresAsignarOwners");
		res = (ResultadoFuncion)message.getHeader("ResultadoFuncion");
		AeOwnersMembersRequest request =(AeOwnersMembersRequest)message.getHeader("request");
		logger.info(String.format("GeneraWrkOwnersGrupos: request: %s", request));
		String servicio = null;

		try {
			servicio = request.getServicio();
			logger.info(String.format("serviciosValidos [%s, %s]",
					OwnersRestService.serviciosValidos[OwnersRestService.OWNER],
					OwnersRestService.serviciosValidos[OwnersRestService.MEMBER]));
			if (servicio.equalsIgnoreCase(OwnersRestService.serviciosValidos[OwnersRestService.OWNER]))
				doProcesoOwner(exchange, request);
			else
				doProcesoMember(exchange, request);
			
		} catch (Exception e) {
			String apoyo = String.format("process: servicio=%s", servicio);
			logger.error(apoyo, e);
			if (res != null)
				registrosBD.registraMiResultadoErrores("NA", apoyo, e, null, res.getKey());
			contadores.incCountErrores();
		}

	}

	private void doProcesoOwner(Exchange exchange, AeOwnersMembersRequest request) {
		logger.info("invoca a doProcesoOwner");
		pueblaWrkOwnersGrupos (exchange, request);
	}

	private void pueblaWrkOwnersGrupos(Exchange exchange, AeOwnersMembersRequest request) {
		logger.info("invoca a pueblaWrkOwnersGrupos");
		Map<String,Object> headers = new HashMap<String,Object>();
		
		try {
			final OracleConnection conn = (OracleConnection)dataSource.getConnection();
			Array esbWrkOwnersProgramas = (Array) conn.createARRAY("ESB_WRK_OWNERS_PROGRAMAS", request.getCriterio().getProgramas().toArray(new String[0]));
			Array esbWrkOwnersCuentas = (Array) conn.createARRAY("ESB_WRK_OWNERS_GRUPOS", request.getCriterio().getCuentas().toArray(new String[0]));
			headers.put("servicio", request.getServicio());
			headers.put("funcion", request.getCriterio().getFuncion());
			headers.put("esbWrkOwnersProgramas", esbWrkOwnersProgramas);
			headers.put("esbWrkOwnersCuentas", esbWrkOwnersCuentas);
			pueblaWrkOwnersGroups.requestBodyAndHeaders(null, headers);
		} catch (SQLException e) {
			String apoyo = String.format("process: request=%s", request);
			logger.error(apoyo, e);
			if (res != null)
				registrosBD.registraMiResultadoErrores("NA", apoyo, e, null, res.getKey());
		}
	}

	private void doProcesoMember(Exchange exchange, AeOwnersMembersRequest request) {
		// TODO Auto-generated method stub
		logger.info("invoca a doProcesoMember");
		pueblaWrkOwnersGrupos (exchange, request);
	}

	//======================================================================================================================================
	// Getters y Setters
	//======================================================================================================================================

	public RegistrosComunes getRegistrosBD() {
		return registrosBD;
	}

	public void setRegistrosBD(RegistrosComunes registrosBD) {
		this.registrosBD = registrosBD;
	}

	public synchronized DataSource getDataSource() {
		return dataSource;
	}

	public synchronized void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
