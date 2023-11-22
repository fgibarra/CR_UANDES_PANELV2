package cl.uandes.panel.servicio.crearGrupos.procesor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * @deprecated
 * Esta funcion no es necesaria
 * @author fernando
 *
 */
public class InicialiceCrearGruposVigentesPosgrado implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
/*
        try {
            Date horaPatida = new Date();
            logger.info("inicialiceGruposInprogressPostgrado: Inicializa BD");
            Session hSession = HibernateHelper.getSession(thread.getName());
            tx = hSession.beginTransaction();
            funcion = dao.getFuncion(hSession, GRUPOS_INPROGRESS_POSTGRADO);
            String[] args = new String[1];
            args = ut.generaArray(funcion.getParametros().trim());
            CommandLineParser parser = new CommandLineParser(args);
            String disabled = parser.getValue("disabled");
         	logger.debug("inicialiceGruposInprogressPostgrado: disabled="+disabled+" parameters="+funcion.getParametros());
            if (disabled != null && "true".equals(disabled.toLowerCase())) {
            	logger.debug("inicialiceGruposInprogressPostgrado: funcion DISABLED!");
            	setInicializado(false);
            	return;
            }
            String fuente = parser.getValue("fuente");
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
                logger.info("inicialiceGruposInprogressPostgrado: poblar Gmail");

                HashMap<String, Integer> solicitados =
                    dao.pueblaInprogressGroupMembersPostgrado(hSession,
                                           OWNER_EMAIL_PERMISSION);
                resultadoFuncion.setGruposSolicitados(solicitados.get("gruposSolicitados"));
                resultadoFuncion.setMiembrosSolicitados(solicitados.get("miembrosSolicitados"));

                logger.info("inicialiceGruposInprogressPostgrado: gruposSolicitados="+
                			resultadoFuncion.getGruposSolicitados()+
                			" miembrosSolicitados="+
                			resultadoFuncion.getGruposSolicitados());
                listaGrupos =
                        dao.getListaGruposInprogressPostgrado(hSession, fuente);
                if (listaGrupos == null) {
                    // fallo la gracion en funcion pueblaGroupMembers
                    throw new RuntimeException("fallo la gracion en funcion pueblaGroupMembers");
                }
                logger.info("inicialiceGruposInprogressPostgrado: listaGrupos.size="+listaGrupos.size());
                indxListaGrupo = 0;

                resultadoFuncion.setThreadsCreadas(getNumThreads());

                // guardar en la bd lo parametros iniciales del proceso
                resultadoFuncion =
                        dao.saveResultadoFuncion(hSession, resultadoFuncion);

                setInicializado(true);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (tx != null)
                tx.commit();
        }

 */
	}

}
