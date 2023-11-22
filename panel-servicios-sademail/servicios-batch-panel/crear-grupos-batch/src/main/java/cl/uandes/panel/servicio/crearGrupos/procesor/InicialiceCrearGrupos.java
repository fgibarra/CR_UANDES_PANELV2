package cl.uandes.panel.servicio.crearGrupos.procesor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.servicios.dto.DatosKcoFunciones;
import cl.uandes.panel.comunes.servicios.dto.ResultadoFuncion;
import cl.uandes.panel.comunes.json.batch.ContadoresCrearGrupos;
import cl.uandes.panel.comunes.utils.ObjectFactory;


/**
 * Action que parte el proceso despues de leer los parametros guardado en la tabla KCO_FUNCIONES
 * @author fernando
 *
 */
public class InicialiceCrearGrupos implements Processor {
	@EndpointInject(uri = "sql:classpath:sql/updateKcoFunciones.sql?dataSource=#bannerDataSource")
	ProducerTemplate updateKcoFunciones;

	@EndpointInject(uri = "sql:classpath:sql/insertMiResultado.sql?dataSource=#bannerDataSource")
	ProducerTemplate insertMiResultado;

	@EndpointInject(uri = "direct:getKey") // recupera SEQUENCE para key
	ProducerTemplate routeGetkey;
	@EndpointInject(uri = "direct:creaResultado") // crea resultado
	ProducerTemplate creaResultado;
	
	private Logger logger = Logger.getLogger(getClass());
	
	/**
	 *	
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>>datos = (List<Map<String, Object>>)exchange.getIn().getBody();
		if (datos != null && datos.size() > 0) {
			
			DatosKcoFunciones data = new DatosKcoFunciones(datos.get(0));
			logger.info(String.format("InicialiceCrearGrupos: data: %s", data));

			logger.info(String.format("InicialiceCrearGrupos: actualiza partida del proceso %s", exchange.getIn().getHeader("proceso")));
			updateKcoFunciones.requestBodyAndHeaders(null, exchange.getIn().getHeaders());

			List<?> resRoute = (List<?>)routeGetkey.requestBody(null);
			//logger.info(String.format("InicialiceCrearGrupos: valor: %s clase: %s", resRoute, resRoute!=null?resRoute.getClass().getName():"es NULO"));
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>)resRoute.get(0);
			//logger.info(String.format("InicialiceCrearGrupos: map: %s clase: %s", map, map!=null?map.getClass().getName():"es NULO"));
			BigDecimal valor = (BigDecimal)map.get("NEXTVAL");
			//logger.info(String.format("InicialiceCrearGrupos: valor: %s clase: %s", valor, valor!=null?valor.getClass().getName():"es NULO"));
			
			// inicializa el resultado
			ResultadoFuncion res = ObjectFactory.createResultadoFuncion(data, valor);
			
			//logger.info(String.format("Antes de creaResultado: body: %s",getQryInsertResultadoFuncion()));
			Map<String, Object> headers = new HashMap<String,Object>();
			headers.put("key", BigDecimal.valueOf(res.getKey().longValue()));
			headers.put("funcion", res.getFuncion());
			headers.put("horaComienzo", res.getHoraComienzo());
			headers.put("minThreads", BigDecimal.valueOf(res.getMinThreads().longValue()));
			headers.put("maxThreads", BigDecimal.valueOf(res.getMaxThreads().longValue()));
			insertMiResultado.requestBodyAndHeaders(null, headers);
			//creaResultado.requestBodyAndHeaders(getQryInsertResultadoFuncion(), headers);

			// actualizar inicio del proceso en KCO_FUNCIONES
			exchange.getIn().setHeader("DatosKcoFunciones", data);
			exchange.getIn().setHeader("ResultadoFuncion", res);
			exchange.getIn().setHeader("contadoresCrearGrupos", new ContadoresCrearGrupos(0,0,0,0,0,0,0,0,0,0));
			exchange.getIn().setHeader("key", BigDecimal.valueOf(res.getKey().longValue()));
		}
	}
	
/*
        try {
            Date horaPatida = new Date();
            logger.info("inicialiceCrearGrupos: Inicializa BD");
            Session hSession = HibernateHelper.getSession(thread.getName());
            tx = hSession.beginTransaction();
            funcion = dao.getFuncion(hSession, CREAR_GRUPOS);
            CommandLineParser parser = new CommandLineParser(toArray(funcion.getParametros()));
            String disabled = parser.getValue("disabled");
         	logger.debug("inicialiceCrearGrupos: disabled="+disabled+" parameters="+funcion.getParametros());
            if (disabled != null && "true".equals(disabled.toLowerCase())) {
            	logger.debug("inicialiceCrearGrupos: funcion DISABLED!");
            	setInicializado(false);
            	return;
            }

            int year = Calendar.getInstance().get(Calendar.YEAR);
            String valor = parser.getValue("year");
            if (valor != null) {
            	year = Integer.valueOf(valor).intValue();
            }
            int periodo = Calendar.getInstance().get(Calendar.MONTH) < 6 ? 10 : 20;
            valor = parser.getValue("periodo");
            if (valor != null)
            	periodo = Integer.valueOf(valor).intValue();

            if (funcion != null) {
                // inicializar cabecera de resultados
                resultadoFuncion = new ResultadoFuncion();
                resultadoFuncion.setHoraComienzo(new Timestamp(horaPatida.getTime()));
                resultadoFuncion.setFuncion(funcion.getFuncion());
                resultadoFuncion.setMaxThreads(funcion.getMaxThread());
                resultadoFuncion.setMinThreads(funcion.getMinThread());
                resultadoFuncion.setGruposCreados(Integer.valueOf(0));
                resultadoFuncion.setMiembrosCreados(Integer.valueOf(0));
                resultadoFuncion =
                        dao.saveResultadoFuncion(hSession, resultadoFuncion);
                tx.commit();
                tx = hSession.beginTransaction();
                // poblar los datos
                logger.info("inicialiceCrearGrupos: poblar Gmail year="+year+" periodo="+periodo);
                /*
                 * Invoca al procedimiento VIA datasource del JBOSS.
                 *
                * /
                HashMap<String, Integer> solicitados =
                    dao.pueblaGroupMembers(hSession, year, periodo);

                if (solicitados != null) {
	                logger.info("inicialiceCrearGrupos: gruposSolicitados="+solicitados.get("gruposSolicitados"));
	                logger.info("inicialiceCrearGrupos: miembrosSolicitados="+solicitados.get("miembrosSolicitados"));
                } else
                	logger.info("inicialiceCrearGrupos: no volvio nada de pueblaGroupMembers");

                logger.info("inicialiceCrearGrupos: funcion.getParametros()="+funcion.getParametros());
                listaGrupos = dao.getListaGrupos(hSession, year, periodo);
                if (listaGrupos == null) {
                    // fallo la gracion en funcion pueblaGroupMembers
                    throw new RuntimeException("fallo la gracion en funcion pueblaGroupMembers");
                }
                logger.info("inicialiceCrearGrupos: listaGrupos="+listaGrupos.size());
                if (listaGrupos.size() > 0) {
                	StringBuffer sb = new StringBuffer();
                	for (Groups grupo : listaGrupos) {
                		sb.append(grupo.getGroupName()).append('\n');
                	}
                	logger.info(sb.toString());
                }
                // separar lista entre threads
                int num = funcion.getMaxThread() >= 2 ? funcion.getMaxThread() : 2;
                Stack<?>[] nStacks  = new Stack<?>[num];
                for (int i=0; i<nStacks.length; i++)
                	nStacks[i] = new Stack<Groups>();
                Stack<Groups> stack = null;
                int i=1;
                while (!listaGrupos.isEmpty()) {
                	Groups grupo = listaGrupos.remove(0);
                	if ("vigentes-todos".equals(grupo.getGroupName())) {
                		stack = (Stack<Groups>) nStacks[0];
                	} else {
                		stack = (Stack<Groups>) nStacks[i];
                		i = (i+1) % num;
                		i = (i == 0 ? i+1 : i);
                	}

            		stack.push(grupo);
                }
                // crear
                for (int j=0; j<nStacks.length; j++) {
                	stack = (Stack<Groups>) nStacks[j];
                	stackListasGrupo.push(stack);
                }

                resultadoFuncion.setThreadsCreadas(getNumThreads());
                resultadoFuncion.setGruposSolicitados(solicitados.get("gruposSolicitados"));
                resultadoFuncion.setMiembrosSolicitados(solicitados.get("miembrosSolicitados"));
                // guardar en la bd lo parametros iniciales del proceso
                resultadoFuncion =
                        dao.saveResultadoFuncion(hSession, resultadoFuncion);

                logger.info("inicialiceCrearGrupos: grupos: "+listaGrupos.size());
                setInicializado(true);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (tx != null)
                tx.commit();
        }

	}
 */

}
