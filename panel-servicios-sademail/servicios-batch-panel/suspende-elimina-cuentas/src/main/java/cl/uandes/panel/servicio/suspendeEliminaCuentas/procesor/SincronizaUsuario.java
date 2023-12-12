package cl.uandes.panel.servicio.suspendeEliminaCuentas.procesor;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.batch.ContadoresSincronizarCuentas;
import cl.uandes.panel.comunes.json.batch.ProcesoDiarioRequest;
import cl.uandes.panel.comunes.servicios.dto.DatosKcoFunciones;
import cl.uandes.panel.comunes.utils.CountThreads;
import cl.uandes.panel.comunes.utils.ObjectFactory;
import cl.uandes.panel.comunes.utils.StringUtilities;
import cl.uandes.sadmemail.comunes.google.api.services.User;
import cl.uandes.sadmemail.comunes.report.json.Report;
import cl.uandes.sadmemail.comunes.report.json.ReportResponse;
import cl.uandes.sadmemail.comunes.utils.StringUtils;

public class SincronizaUsuario implements Processor {

    @PropertyInject(value = "uri.reportServices", defaultValue="http://localhost:8181/cxf/ESB/panel/reportServices")
	private String uriReportServices;

	@PropertyInject(value = "uri-gmailServices", defaultValue="http://localhost:8181/cxf/ESB/panel/gmailServices")
	private String gmailServices;

	@EndpointInject(uri = "sql-stored:classpath:sql/prd_actualiza_cuenta.sql?dataSource=#bannerDataSource")
	ProducerTemplate prd_actualiza_cuenta;

	@EndpointInject(uri = "sql-stored:classpath:sql/prd_registra_eliminacion.sql?dataSource=#bannerDataSource")
	ProducerTemplate prd_registra_eliminacion;

	@EndpointInject(uri = "sql:classpath:sql/insertMiResultadoErrores.sql?dataSource=#bannerDataSource")
	ProducerTemplate insertMiResultadoErrores;

	@EndpointInject(uri = "sql:classpath:sql/quitaFechaAviso.sql?dataSource=#bannerDataSource")
	ProducerTemplate quitaFechaAviso;

	@EndpointInject(uri = "sql:classpath:sql/marcaCuentaSuspendida.sql?dataSource=#bannerDataSource")
	ProducerTemplate marcaCuentaSuspendida;

	@EndpointInject(uri = "cxfrs:bean:rsReporteUsuario?continuationTimeout=-1")
	ProducerTemplate consultarUsageCuenta;
	String templateConsultarUsageCuenta = "%s/report/usage/%s";

	@EndpointInject(uri = "cxfrs:bean:rsSuspendeCuenta?continuationTimeout=-1")
	ProducerTemplate suspendeCuenta;
	String templateSuspendeCuenta = "%s/user/suspend/%s";

	@EndpointInject(uri = "cxfrs:bean:rsEliminaCuenta?continuationTimeout=-1")
	ProducerTemplate eliminaCuenta;
	String templateEliminaCuenta = "%s/user/delete/%s";

	@EndpointInject(uri = "cxfrs:bean:rsReactivaCuenta?continuationTimeout=-1")
	ProducerTemplate reactivarCuentaGmail;
	String templateReactivarCuentaGmail = "%s/user/reactivar/%s";

	
	@EndpointInject(uri = "velocity:templateMailAvisoSuspension.vm?contentCache=true")
	ProducerTemplate velocity;
	
	@EndpointInject(uri="direct:generaMail")
	ProducerTemplate sendmail;
	
	User user = null;
	ContadoresSincronizarCuentas contadores = null;
	Integer keyResultado;
	String proceso;
	String operaciones[];
	Long usoAlumnos;
	Long usoGenerales;
	Long usoProfesores;
	Long periodoGracia;
	
	Logger logger = Logger.getLogger(getClass());
	
	/**
	 * Viene en el Body los datos de la CUENTA RECUPERADOS DESDE gmail
	 * Busca los datos de uso asociados a la cuenta
	 * Actualiza estos datos en tabla MI_CUENTAS_GMAIL o actualiza/crea una entrada en tabla MI_CUENTAS_EXTERNAS
	 * (procedimiento prd_actualiza_cuenta)
	 * Procedimiento devuelve fecha de aviso registrado en la tabla y tipo de usuario 
	 * (PROFESOR, PREGRADO, POSTGRADO, DIPLOMADO, OTRO)
	 * si el uso es superior al que corresponde:
	 * 	- si no hay aviso, crear mail para avisar y marcar la fecha de aviso
	 *  - si lo hay y ha pasado mas del tiempo fijado -> suspender la cuenta
	 */
	@Override
	public void process(Exchange exchange) throws Exception {
		Message message = exchange.getIn();
		CountThreads countThread = (CountThreads)message.getHeader("countThread");
		user = (User) message.getBody();
		contadores = (ContadoresSincronizarCuentas) message.getHeader("contadoresSincronizarCuentas");
		contadores.incProcesados();
		keyResultado = (Integer)message.getHeader("keyResultado");
		proceso = (String)message.getHeader("proceso");
		operaciones = ((ProcesoDiarioRequest)message.getHeader("request")).getOperaciones();
		DatosKcoFunciones datosKcoFunciones = (DatosKcoFunciones)message.getHeader("DatosKcoFunciones");
		usoAlumnos = datosKcoFunciones.getParametros().getUsoAlumnos();
		usoGenerales = datosKcoFunciones.getParametros().getUsoGenerales();
		usoProfesores = datosKcoFunciones.getParametros().getUsoProfesores();
		periodoGracia = datosKcoFunciones.getParametros().getPeriodo().longValue();
		
		logger.info(String.format("SincronizaUsuario: contadores=%d user.id=%s user.email=%s",
				contadores.getCountProcesados(), user.getId(), user.getEmail()));
		Report reporte = getReporte(user.getId());
		if (reporte == null) {
			// no se pudo recuperar el reporte de uso, Abortar
			registraError(proceso, String.format("No se pudo recuperar reporte para cuenta %s de %s %s", 
					user.getEmail(), user.getGivenName(), user.getFamilyName()),
					new BigDecimal(keyResultado),user.getEmail());
		} else {
			// tenemos los datos para actualizar BD
			Map<String, Object> datos = actualizaBD(user, reporte);
			if (datos != null) {
				String estadoAcademico = (String) datos.get("ESTADO_ACADEMICO");
				java.sql.Date fechaAviso = (java.sql.Date) datos.get("FECHA_AVISO");
				
				logger.info(String.format("SincronizaUsuario: user.id=%s user.email=%s estadoAcademico=%s fecha_aviso=%s", 
						user.getId(), user.getEmail(), estadoAcademico, StringUtils.toString((Date) datos.get("fecha_aviso"))));
				
				// se pueden eliminar cuentas en base al estado academico
				if (hayQueEliminar(estadoAcademico))
					eliminarCuenta(user.getId());
				
				else if (StringUtils.estaContenido("suspender", operaciones)) {
					Long maxPermitido = getMaxPermitido(estadoAcademico);
					if (hayExcesoUso(maxPermitido, reporte)) {
						if (fechaAviso != null) {
							if (hayQueSuspender(fechaAviso)) {
								suspenderCuenta(user.getId());
							}
						} else {
							avisarSuspencion(exchange, user, reporte, maxPermitido);
						}
					} else  {
						// No hay exceso de uso.
						// si hubo aviso, eliminar el aviso
						if (fechaAviso != null) {
							quitaFechaAviso.requestBodyAndHeader(null, "idGmail", user.getId());
						}
						
						if (hayQueReactivar(maxPermitido, reporte)) {
							if (!reactivarCuenta(user)) {
								registraError(proceso, "No se pudo reactivar cuenta", new BigDecimal(keyResultado),user.getEmail());
								logger.info(String.format("SincronizaUsuario: no pudo reactivar cuenta %s", user.getEmail()));
							}
						}
					}
				}
			} else
				logger.error(String.format("No pudo actualizar BD para %s", user.getEmail()));
		}
		countThread.decCounter();
		logger.info(String.format("SincronizaUsuario: libera thread countThread=%d", countThread.getCounter()));
	}





	/**
	 * Los maximos se definen en los parametros de KCO_FUNCIONES
	 * @param estadoAcademico
	 * @return
	 */
	private Long getMaxPermitido(String estadoAcademico) {
		if (estadoAcademico.toUpperCase().equals("PROFESOR"))
			return usoProfesores;
		else if (estadoAcademico.toUpperCase().equals("NO_REGISTRA"))
			return usoGenerales;
		
		return usoAlumnos;
	}




	/**
	 * Invoca al API de reportes
	 * @param id
	 * @return
	 */
	protected Report getReporte(String id) {
		Map<String, Object> headers = new HashMap<String, Object>();
		String url = String.format(templateConsultarUsageCuenta, getUriReportServices(), id);
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, url);
		headers.put("CamelHttpMethod", "GET");
		Report reporte = null;
		ReportResponse res = null;
		try {
			res = (ReportResponse) ObjectFactory.procesaResponseImpl(
					(ResponseImpl)consultarUsageCuenta.requestBodyAndHeaders(null, headers), ReportResponse.class);
		} catch (Exception e) {
			contadores.incErrores();
			logger.error(String.format("ERROR: getReporte id=%s causa %s", id, e.getMessage()));
			registraError(proceso, e.getMessage(), new BigDecimal(keyResultado),user.getEmail());
			return null;
		}
		if (res.getCodigo() == 0)
			reporte  = res.getReporte();
		
		return reporte;
	}

	/**
	 * Invoca al STORE PROCEDURE prd_actualiza_cuenta
	 * @param user
	 * @param reporte
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> actualizaBD(User user, Report reporte) {
		logger.info(String.format("actualizaBD: login_name=%s", StringUtils.getNombreCuenta(user.getEmail())));
		String REPORT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("login_name", StringUtils.getNombreCuenta(user.getEmail()));
		headers.put("nombres", user.getGivenName());
		headers.put("apellidos", user.getFamilyName());
		headers.put("fecha_suspension", reporte.getIsDisabled()?new java.sql.Date(new java.util.Date().getTime()):null);
		headers.put("id_gmail", user.getId());
		headers.put("last_login", StringUtils.toTimeStamp(ajustaDate(reporte.getLastLoginTime()), REPORT_DATE_PATTERN));
		headers.put("fecha_creacion", StringUtils.toTimeStamp(ajustaDate(reporte.getCreationTime()), REPORT_DATE_PATTERN));
		headers.put("gmail_used_quota", reporte.getGmailUsedQuotaInMb());
		headers.put("drive_used_quota", reporte.getDriveUsedQuotaInMb());
		headers.put("photos_used_quota", reporte.getGplusPhotosUsedQuotaInMb());
		headers.put("total_quota", reporte.getTotalQuotaInMb());
		headers.put("used_quota", reporte.getUsedQuotaInMb());
		headers.put("used_quota_percentage", reporte.getUsedQuotaInPercentage());
		
		Map<String, Object> headersOut = null;
		try {
			headersOut = (Map<String, Object>) prd_actualiza_cuenta.requestBodyAndHeaders(null, headers);
		} catch (Exception e) {
			logger.error("Al invocar SP", e);
		}
		return headersOut;
	}




	private String ajustaDate(String lastLoginTime) {
		String valor = null;
		if (lastLoginTime != null) {
			valor = lastLoginTime.replace('T', ' ');
			logger.info(String.format("ajustaDate: desde %s a %s", lastLoginTime, valor));
		}
		return valor;
	}




	/**
	 * Registra el error en MI_RESULTADO
	 * @param format
	 */
	public void registraError(String tipo, String mensaje, BigDecimal keyResultado, String cuenta) {
		logger.info(String.format("registraError: tipo=%s mensaje=%s keyResultado=%d cuenta=%s",
				tipo, mensaje, keyResultado.intValue(), cuenta));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("TIPO", tipo);
		headers.put("CAUSA", mensaje);
		headers.put("KEY_GRUPO", null);
		headers.put("KEY_RESULTADO", keyResultado);
		headers.put("ID_USUARIO", cuenta);
		insertMiResultadoErrores.requestBodyAndHeaders(null, headers);
		contadores.incErrores();
	}

	

	
	/**
	 * 
	 * Si el uso reportado es mayor que el permitido
	 * @param reporte
	 * @return
	 */
	private boolean hayExcesoUso(Long maxPermitido, Report reporte) {
		if (StringUtils.estaContenido("suspender", operaciones)) {
			if (reporte.getUsedQuotaInMb() > maxPermitido)
				return true;
		}
		return false;
	}



	/**
	 * Dependiendo del estado academico se decide si se elimna la cuenta (ELIMINADO, ABANDONO ???)
	 * @param estadoAcademico
	 * @return
	 */
	private boolean hayQueEliminar(String estadoAcademico) {
		// TODO Auto-generated method stub
		if (StringUtils.estaContenido("eliminar", operaciones)) {
			;
		}
		return false;
	}




	private void eliminarCuenta(String id) {
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(getTemplateEliminaCuenta(),getGmailServices(), id));
		headers.put("CamelHttpMethod", "DELETE");
		Response response = null;
		try {
			response = (Response)eliminaCuenta.requestBody(null);
			if (response.getStatus() > 300) {
				registraError("eliminaCuenta", response.getStatusInfo().getReasonPhrase(), ObjectFactory.toBigDecimal(keyResultado), id);
			} else {
				contadores.incEliminadas();
				marcarCuentaEliminadaEnBD(id);
			}
		} catch (Exception e) {
			logger.error("eliminaCuenta", e);
			registraError("eliminaCuenta", e.getMessage(), ObjectFactory.toBigDecimal(keyResultado), id);
		}
	}




	@SuppressWarnings("unchecked")
	private void marcarCuentaEliminadaEnBD(String id) {
		Map<String, Object> headersOut = null;
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("id_gmail", id);
		headers.put("keyResultado", ObjectFactory.toBigDecimal(keyResultado));
		try {
			headersOut = (Map<String, Object>) prd_registra_eliminacion.requestBodyAndHeaders(null, headers);
			Integer key = ObjectFactory.toInteger((BigDecimal)headersOut.get("key"));
			logger.info(String.format("marcarCuentaEliminadaEnBD: cuenta %s copiada a tabla de eliminadas con key: %d",
					id, key==null ? -1 : key));
		} catch (Exception e) {
			logger.error("Al invocar SP prd_registra_eliminacion", e);
			registraError("eliminaCuenta",  e.getMessage(), ObjectFactory.toBigDecimal(keyResultado), id);
		}
		
	}




	/**
	 * Si la fecha de aviso es superior a la fecha actual mas los dias indicados en el parametro periodo retorna TRUE
	 * @param fechaAviso
	 * @return
	 */
	private boolean hayQueSuspender(Date fechaAviso) {
		if (StringUtils.estaContenido("suspender", operaciones)) {
			// calcular la LocalDate correspondiente a la fecha tope
			// agregar los dias
			LocalDate fechaTope = new Date(fechaAviso.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(periodoGracia);
			logger.info(String.format("hayQueSuspender: fechaTope=%s, now=%s", fechaTope, LocalDate.now()));
			return LocalDate.now().isAfter(fechaTope);
		}
		return false;
	}


	/**
	 * 
	 * Invoca al API para suspender la cuenta y la marca en la BD
	 * @param id
	 */
	private void suspenderCuenta(String id) {
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(getTemplateSuspendeCuenta(),getGmailServices(), id));
		headers.put("CamelHttpMethod", "PUT");
		Response response = null;
		try {
			response = (Response)suspendeCuenta.requestBody(null);
			if (response.getStatus() > 300) {
				registraError("suspenderCuenta", response.getStatusInfo().getReasonPhrase(), ObjectFactory.toBigDecimal(keyResultado), id);
			} else {
				contadores.incSuspendidas();
				marcaCuentaSuspendida.requestBodyAndHeader(null, "id_gmail", id);
			}
		} catch (Exception e) {
			logger.error("suspenderCuenta", e);
			registraError("suspenderCuenta", e.getMessage(), ObjectFactory.toBigDecimal(keyResultado), id);
		}
	}



	/**
	 * Envia email y marca fecha de aviso
	 * 
	 * - Prepara header para template con Velocity
	 * - invoca velocyty
	 * - prepara invocacion al servicio de envio de emails
	 * - envia el email
	 * 
	 * @param user
	 * @param reporte
	 */
	private void avisarSuspencion(Exchange exchange, User user, Report reporte, Long maxPermitido) {
		Map<String,Object> headers = new HashMap<String,Object>();
		
		headers.put("fechaProceso", StringUtilities.getInstance().toString(new java.util.Date(), "E, dd MMM yyyy HH:mm:ss z"));
		headers.put("nombre", user.getGivenName());
		headers.put("email", user.getEmail());
		headers.put("drive_used_quota_in_mb", reporte.getDriveUsedQuotaInMb());
		headers.put("drive_used_quota_in_mb_fmt", StringUtilities.getInstance().format(reporte.getDriveUsedQuotaInMb()));
		headers.put("gplus_photos_used_quota_in_mb", reporte.getGplusPhotosUsedQuotaInMb());
		headers.put("gplus_photos_used_quota_in_mb_fmt", StringUtilities.getInstance().format(reporte.getGplusPhotosUsedQuotaInMb()));
		headers.put("gmail_used_quota_in_mb", reporte.getGmailUsedQuotaInMb());
		headers.put("gmail_used_quota_in_mb_fmt", StringUtilities.getInstance().format(reporte.getGmailUsedQuotaInMb()));
		headers.put("used_quota_in_mb_fmt", StringUtilities.getInstance().format(reporte.getUsedQuotaInMb()));
		headers.put("quota_asignada_fmt", StringUtilities.getInstance().format(maxPermitido));
		headers.put("plazo", periodoGracia);
		
		Object cuerpo = velocity.requestBodyAndHeaders(null, headers);
		logger.info(String.format("avisarSuspencion: cuerpo mail=%s", cuerpo));
		
		if (cuerpo == null)
			return;
		
		sendmail.requestBody(cuerpo);
		
	}





	/**
	 * @param maxPermitido
	 * @param reporte
	 * @return
	 */
	private boolean hayQueReactivar(Long maxPermitido, Report reporte) {
		if (StringUtils.estaContenido("suspender", operaciones)) {
			if (reporte.getIsSuspended() && reporte.getUsedQuotaInMb() < maxPermitido)
				return true;
		}
		return false;
	}




	/**
	 * @param user
	 */
	private boolean reactivarCuenta(User user) {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("CamelHttpMethod", "PUT");
		headers.put("Exchange.DESTINATION_OVERRIDE_URL", 
				String.format(templateReactivarCuentaGmail,getGmailServices(), user.getId()));
		ResponseImpl response;
		try {
			response = (ResponseImpl)reactivarCuentaGmail.requestBodyAndHeaders(null, headers);
			if (response.getStatus() < 300) {
				return true;
		}
		else
			return false;
		} catch (CamelExecutionException e) {
			logger.error(String.format("reactivarCuenta: id=%s (%s) error: %s",
					user.getId(), user.getEmail(), e.getMessage()));
			return false;
		}
		
	}




	//============================================================================================================
	// Getters y Setters
	//============================================================================================================
	
	public synchronized String getUriReportServices() {
		return uriReportServices;
	}

	public synchronized void setUriReportServices(String uriReportServices) {
		this.uriReportServices = uriReportServices;
	}




	public synchronized String getGmailServices() {
		return gmailServices;
	}




	public synchronized void setGmailServices(String gmailServices) {
		this.gmailServices = gmailServices;
	}




	public synchronized String getTemplateConsultarUsageCuenta() {
		return templateConsultarUsageCuenta;
	}




	public synchronized String getTemplateSuspendeCuenta() {
		return templateSuspendeCuenta;
	}




	public synchronized String getTemplateEliminaCuenta() {
		return templateEliminaCuenta;
	}




}
