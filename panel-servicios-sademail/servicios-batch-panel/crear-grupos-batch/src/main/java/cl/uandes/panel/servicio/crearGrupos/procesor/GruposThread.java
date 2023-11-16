package cl.uandes.panel.servicio.crearGrupos.procesor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.PropertyInject;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.apache.log4j.Logger;

import cl.uandes.panel.comunes.json.batch.crearGrupos.GroupRequest;
import cl.uandes.panel.comunes.json.batch.crearGrupos.GroupResponse;
import cl.uandes.panel.comunes.json.batch.crearGrupos.IdCuentaCorreoResponse;
import cl.uandes.panel.comunes.json.batch.crearGrupos.MemberRequest;
import cl.uandes.panel.comunes.json.batch.crearGrupos.MemberResponse;
import cl.uandes.panel.comunes.json.batch.ContadoresCrearGrupos;
import cl.uandes.panel.comunes.servicios.dto.DatosKcoFunciones;
import cl.uandes.panel.comunes.servicios.dto.GruposMiUandes;
import cl.uandes.panel.comunes.servicios.dto.ResultadoFuncion;
import cl.uandes.panel.comunes.utils.ObjectFactory;
import cl.uandes.panel.servicio.crearGrupos.api.dto.DatosMemeberDTO;
/**
 * Ruta que procesa la lista de grupos PREGRADO recuperados desde la BD
 * @author fernando
 *
 */
public class GruposThread implements Processor {

    @PropertyInject(value = "crear-grupos-gmail.uri-gmailServices", defaultValue="http://localhost:8181/cxf/ESB/panel/gmailServices")
	private String gmailServices;
	
    @PropertyInject(value = "panelv2.dominio", defaultValue="uandes.cl")
	private String dominio;
	
	@EndpointInject(uri = "sql:classpath:sql/updateGrupoCreado.sql?dataSource=#bannerDataSource")
	ProducerTemplate saveGrupo;

	@EndpointInject(uri = "sql:classpath:sql/getIdsMiembroGrupos.sql?dataSource=#bannerDataSource")
	ProducerTemplate getIdsMiembroGrupos;

	@EndpointInject(uri = "sql:classpath:sql/deleteMember.sql?dataSource=#bannerDataSource")
	ProducerTemplate deleteMember;

	@EndpointInject(uri = "sql:classpath:sql/updateMember.sql?dataSource=#bannerDataSource")
	ProducerTemplate updateMember;

	@EndpointInject(uri = "sql:classpath:sql/insertMiResultadoErrores.sql?dataSource=#bannerDataSource")
	ProducerTemplate insertMiResultadoErrores;

	@EndpointInject(uri = "sql-stored:classpath:sql/prd_operaContadorMiResultados.sql?dataSource=#bannerDataSource")
	ProducerTemplate incrementa;

	@EndpointInject(uri = "sql:classpath:sql/updateMiCuentas.sql?dataSource=#bannerDataSource")
	ProducerTemplate updateMiCuentasGmail;

	@EndpointInject(uri = "sql:classpath:sql/updateMiGruposMiUandes.sql?dataSource=#bannerDataSource")
	ProducerTemplate updateMiGruposMiUandes;

		
	@EndpointInject(uri = "cxfrs:bean:rsCreateGrupo?continuationTimeout=-1")
	//	@EndpointInject(uri = "direct:testCreateGrupoGmail")
	ProducerTemplate createGrupoGmail;
	String templateCreateGrupoGmail = "%s/group/create";
	@EndpointInject(uri = "cxfrs:bean:rsRetrieveGrupo?continuationTimeout=-1")
	ProducerTemplate recuperaGrupoGmail;
	String templateRecuperaGrupoGmail = "%s/group/retrieve?groupId=%s";

	@EndpointInject(uri = "cxfrs:bean:rsSacarMember?continuationTimeout=-1")
	//	@EndpointInject(uri = "direct:testSacarMember")
	ProducerTemplate sacarMember;
	String templateSacarMember = "%s/member/deleteMember";

	@EndpointInject(uri = "cxfrs:bean:rsAgregarMember?continuationTimeout=-1")
	//	@EndpointInject(uri = "direct:testAgregarMember")
	ProducerTemplate agregarMember;
	String templateAgregarMember = "%s/member/addMember";
	
	@EndpointInject(uri = "cxfrs:bean:rsConsultarIdCuenta?continuationTimeout=-1")
	//	@EndpointInject(uri = "direct:testConsultarIdCuenta")
	ProducerTemplate consultarIdCuenta;
	String templateConsultarIdCuenta = "%s/idCuentaUsuario?email=%s";
	private ResultadoFuncion res;
	private ContadoresCrearGrupos contadores;
	
	private Logger logger = Logger.getLogger(getClass());
	
	@Override
	public void process(Exchange exchange) throws Exception {
		logger.info("Entrando a GruposThread");
		res = (ResultadoFuncion)exchange.getIn().getHeader("ResultadoFuncion");
		contadores = (ContadoresCrearGrupos)exchange.getIn().getHeader("contadoresCrearGrupos");
		DatosKcoFunciones datos = (DatosKcoFunciones)exchange.getIn().getHeader("DatosKcoFunciones");
		logger.info(String.format("GruposThread: datos: %s", datos));
		logger.info(String.format("GruposThread: res: %s", res));
		GruposMiUandes grupo = (GruposMiUandes)exchange.getIn().getHeader("grupoGmail");
		logger.info(String.format("GruposThread: grupo |%s|", grupo));
		contadores.incCountProcesados();
		
		if (crearGrupo(exchange, grupo, res)) {
			sacarMiembrosInactivos(exchange, new BigDecimal(grupo.getKey()), new BigDecimal(res.getKey()));
			agregarMiembrosActivos(exchange, new BigDecimal(grupo.getKey()), new BigDecimal(res.getKey()));
		}
	}
	/**
	 * Si en MI_GRUPOS_AZURE indica que no esta creado en Gmail, crearlo
	 * en caso contrario
	 * Validar que efectivamente exista creado en Gmail
	 * @param grupo
	 * @return
	 */
	private boolean crearGrupo(Exchange exchange, GruposMiUandes grupo, ResultadoFuncion miResultado) {
		Map<String,Object> headers = exchange.getIn().getHeaders();
		logger.info(String.format("crearGrupo: hay que crear? grupo.getCreadoGmail().booleanValue(): %b", grupo.getCreadoGmail().booleanValue()));
		if (!grupo.getCreadoGmail().booleanValue()) {
			/* validar que efectivamente no este creado. 
			 * Si lo esta solo actualizar el flag en MI_GRUPOS_AZURE
			 * Si no crearlo
			 */
			try {
				if (!existeEnGmail(grupo)) {
					/* crearlo en Gmail
					 * Si resulta OK:
					 * 		actualizar flag de creado en file en MI_GRUPOS_AZURE
					 * 		incrementar contador de grupos greados
					 */
					
					GroupRequest body = new GroupRequest(grupo.getGroupName(), grupo.getGroupDescription(), grupo.getEmailPermission(), getFechaExpiracion(grupo));
					headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateCreateGrupoGmail,getGmailServices()));
					headers.put("CamelHttpMethod", "POST");
					GroupResponse res;
					try {
						res = (GroupResponse)ObjectFactory.procesaResponseImpl((ResponseImpl) createGrupoGmail.requestBodyAndHeaders(body, headers), GroupResponse.class);
						contadores.incCountGruposAgregadosAD();
					} catch (Exception e) {
						logger.error("crearGrupo", e);
						contadores.incCountErrores();
						registraError("createGrupoGmail", e.getMessage(), new BigDecimal(miResultado.getKey()),new BigDecimal(grupo.getKey()));
						return false;
					}
					logger.info(String.format("Vuelve de crear crgupo con res: %s", res));
					
					if (res.getCodigo() == 0 || res.getMensaje().matches(".*creado.*")) {
						grupo.setGroupId(res.getGrupo().getId());
						grupo.setCreadoGmail(Boolean.TRUE);
						headers.put("keyGrupo", BigDecimal.valueOf(grupo.getKey()));
						headers.put("idGrupo", grupo.getGroupId());
						logger.info(String.format("update mi_resultados: key=%d", grupo.getKey()));
						saveGrupo.requestBodyAndHeaders(null, headers);
						contadores.incCountGruposAgregadosBD();
						headers.put("idContador", "GRUPOS_CREADOS");
						headers.put("p_operacion", "INCREMENTA");
						headers.put("key", new BigDecimal(miResultado.getKey()));
						logger.info(String.format("incrementar resultado: %d idContador: %s p_operacion: %s", 
								miResultado.getKey(), headers.get("idContador"), headers.get("p_operacion")));
						incrementa.requestBodyAndHeaders(null, headers);
					} else {
						contadores.incCountErrores();
						registraError("createGrupoGmail", res.getMensaje(), new BigDecimal(miResultado.getKey()),new BigDecimal(grupo.getKey()));
						return false;
					}
				} else {
					// !! existe. actualizar MI_GRUPOS_AZURE
					Map<String, Object> headers2 = new HashMap<String, Object>();
					headers2.put("creadoGmail", BigDecimal.ONE);
					headers2.put("keyGrupo", ObjectFactory.toBigDecimal(grupo.getKey()));
					logger.info(String.format("updateMiGruposMiUandes: creadoGmail=%d, key=%d",
							((BigDecimal)headers2.get("creadoGmail")).intValue(), 
							((BigDecimal)headers2.get("keyGrupo")).intValue()));
					
					updateMiGruposMiUandes.requestBodyAndHeaders(null, headers2);
					contadores.incCountGruposAgregadosBD();
				}
			} catch (Exception e) {
				logger.error("createGrupoGmail", e);
				contadores.incCountErrores();
				registraError("createGrupoGmail", e.getMessage(), new BigDecimal(miResultado.getKey()),new BigDecimal(grupo.getKey()));
				return false;
			}
		} else {
			/*
			 * validar que exista en Gmail
			 * Si no existe --> crearlo
			 * incrementar contador de grupos actualizados
			 */
			headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateRecuperaGrupoGmail, getGmailServices(), grupo.getGroupId()));
			headers.put("CamelHttpMethod", "GET");
			GroupResponse res;
			try {
				res = (GroupResponse) ObjectFactory.procesaResponseImpl(
						(ResponseImpl)recuperaGrupoGmail.requestBodyAndHeaders(null, headers), GroupResponse.class);
			} catch (Exception e) {
				contadores.incCountErrores();
				registraError("recuperaGrupoGmail", e.getMessage(), new BigDecimal(miResultado.getKey()),new BigDecimal(grupo.getKey()));
				return false;
			}
			if (res.getCodigo() != 0) {
				if (res.getMensaje().matches(".*Invalid object identifier*")) {
					// crearlo
					GroupRequest body = new GroupRequest(grupo.getGroupName(), grupo.getGroupDescription(), grupo.getEmailPermission(), getFechaExpiracion(grupo));
					headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateCreateGrupoGmail,getGmailServices()));
					headers.put("CamelHttpMethod", "POST");
					res = (GroupResponse) createGrupoGmail.requestBodyAndHeaders(body, headers);
					if (res.getCodigo() != 0) {
						contadores.incCountErrores();
						registraError("recuperaGrupoGmail", res.getMensaje(), new BigDecimal(miResultado.getKey()),new BigDecimal(grupo.getKey()));
						return false;
					}
					contadores.incCountGruposAgregadosAD();
				} else {
					contadores.incCountErrores();
					registraError("recuperaGrupoGmail", res.getMensaje(), new BigDecimal(miResultado.getKey()),new BigDecimal(grupo.getKey()));
					return false;
				}
			}
			// incrementar contador
			incrementa.requestBodyAndHeaders(null, headers);
			operaContador("GRUPOS_CREADOS", new BigDecimal(miResultado.getKey()));
		}
		return true;
	}
	
	private boolean existeEnGmail(GruposMiUandes grupo) throws Exception {
		if (grupo.getGroupId() == null)
			return false;
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateRecuperaGrupoGmail, getGmailServices(), 
				ObjectFactory.escape2Html(grupo.getGroupId())));
		headers.put("CamelHttpMethod", "GET");
		GroupResponse res = (GroupResponse) ObjectFactory.procesaResponseImpl(
				(ResponseImpl)recuperaGrupoGmail.requestBodyAndHeaders(null, headers), GroupResponse.class);
		logger.info(String.format("existeEnGmail: GroupResponse: %s", res));
		if (res.getCodigo() == 0)
			return true;
		if (res.getMensaje().matches(".*Invalid object identifier*"))
			return false;
		logger.info(String.format("existeEnGmail por codigo %b por msg %b",
				res.getCodigo() == 0, res.getMensaje().matches(".*Invalid object identifier*")));
		throw new RuntimeException(res.getMensaje());
	}
	
	private void operaContador(String idContador, BigDecimal key) {
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("idContador", idContador);
		headers.put("p_operacion", "INCREMENTA");
		headers.put("key", key);
		incrementa.requestBodyAndHeaders(null, headers);
	}
	
	private String getFechaExpiracion(GruposMiUandes grupo) {
		Integer tipoGrupo = grupo.getTipoGrupo();
		if (tipoGrupo == 3) {
			// Cursos fecha de expiracion es agosto (periodo 10), febrero periodo 20 o 90
			String periodo = grupo.getPeriodo().substring(4);
			Calendar cal = Calendar.getInstance();
			cal.setTime(new java.util.Date());
			int yearActual = Integer.valueOf(cal.get(Calendar.YEAR));
			if ("10".equals(periodo))
				return String.format("%d0831",yearActual);
			else
				return String.format("%d0131", yearActual+1);
		} else {
			// nunca
			return "20500201";
		}
	}
	private void sacarMiembrosInactivos(Exchange exchange, BigDecimal keyGrupo, BigDecimal keyResultado) {
		/*
		 * Recuperar lista de miembros que ya no estan en el grupo
		 * recorrer la lista
		 * invocar a la ruta que saca miembro del grupo
		 */
		logger.info(String.format("sacarMiembrosInactivos: de grupo %d", keyGrupo.intValue()));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("tipoOperacion", BigDecimal.ZERO);
		headers.put("keyGrupo", keyGrupo);
		logger.info(String.format("sacarMiembrosInactivos: tipoOperacion %d", ((BigDecimal)headers.get("tipoOperacion")).intValue()));
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> miembrosNoActivos = (List<Map<String, Object>>) getIdsMiembroGrupos.requestBodyAndHeaders(null, headers);
		logger.info(String.format("sacarMiembrosInactivos:miembrosNoActivos %d elementos", miembrosNoActivos.size()));
		List<DatosMemeberDTO> miembrosSacar = factoryListaGrupoMiembro(miembrosNoActivos);
		logger.info(String.format("sacarMiembrosInactivos:miembrosSacar %d elementos", miembrosSacar.size()));
		for (DatosMemeberDTO datos : miembrosSacar) {
			logger.info(String.format("sacarMiembrosInactivos:datos: %s", datos));
			headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateSacarMember,getGmailServices()));
			headers.put("CamelHttpMethod", "DELETE");
			Response response = (Response) sacarMember.requestBodyAndHeaders(datos.getRequest(), headers);
			if (response.getStatus() < 300) {
				headers.put("idMiembro", datos.getKeyGrupoMiembro().getIdMiembro());
				headers.put("keyGrupo", new BigDecimal(datos.getKeyGrupoMiembro().getKeyGrupo()));
				deleteMember.requestBodyAndHeaders(null, headers);
				contadores.incCountMiembrosSacadosBD();
				contadores.incCountMiembrosSacadosAD();
			} else {
				contadores.incCountErrores();
				registraError("sacarMember", response.getStatusInfo().toString(), keyResultado, datos);
			}
			operaContador("MIEMBROS_CREADOS", keyResultado);
		}
	}
	
	private void agregarMiembrosActivos(Exchange exchange, BigDecimal keyGrupo, BigDecimal keyResultado) throws Exception {
		/*
		 * Recuperar lista de miembros que hay que agregar al grupo
		 * recorrer la lista
		 * invocar a la ruta que agrega miembro al grupo
		 * Nota: intenta agregar a todos aunque ya esten en Gmail. Gmail responde con un error 
		 * 'One or more added object references already exist'
		 * de esta manera se asegura que lo que este en la BD tambien este en Gmail.
		 */
		logger.info(String.format("agregarMiembrosActivos: de grupo %d", keyGrupo.intValue()));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("tipoOperacion", BigDecimal.ONE);
		headers.put("keyGrupo", keyGrupo);
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> miembrosActivos = (List<Map<String, Object>>) getIdsMiembroGrupos.requestBodyAndHeaders(null, headers);
		List<DatosMemeberDTO> miembrosAgregar = factoryListaGrupoMiembro(miembrosActivos);
		for (DatosMemeberDTO datos : miembrosAgregar) {
			logger.info(String.format("agregarMiembrosActivos: miembro %s", datos));
			headers.put(Exchange.DESTINATION_OVERRIDE_URL, String.format(templateAgregarMember,getGmailServices()));
			headers.put("CamelHttpMethod", "POST");
			//logger.info(String.format("agregarMiembrosActivos: URL=%s", (String)headers.get(Exchange.DESTINATION_OVERRIDE_URL)));
			MemberResponse response = (MemberResponse) ObjectFactory.procesaResponseImpl((ResponseImpl)agregarMember.requestBody(datos.getRequest()),MemberResponse.class);
			if (response.getCodigo() == 0 || response.getMensaje().matches(".*One or more added object references already exist.*")) {
				headers.put("idMiembro", datos.getKeyGrupoMiembro().getIdMiembro());
				headers.put("keyGrupo", new BigDecimal(datos.getKeyGrupoMiembro().getKeyGrupo()));
				logger.info(String.format("actualizar member: %s,%d", headers.get("idMiembro"), datos.getKeyGrupoMiembro().getKeyGrupo()));
				updateMember.requestBodyAndHeaders(null, headers);
				contadores.incCountMiembrosAgregadosAD();
				contadores.incCountMiembrosAgregadosBD();
			} else {
				contadores.incCountErrores();
				registraError("agregarMember", response.getMensaje(), keyResultado, datos);
			}
			operaContador("MIEMBROS_CREADOS", keyResultado);
		}
	}

	private List<DatosMemeberDTO> factoryListaGrupoMiembro(List<Map<String, Object>> data) {
		List<DatosMemeberDTO> lista = new ArrayList<DatosMemeberDTO>();
		for (Map<String, Object> map : data) {
			DatosMemeberDTO dto = new DatosMemeberDTO(map);
			if (!dto.hayIdGmail()) {
				// La cuenta en MI_CUENTAS_AZURE no tiene su ID Gmail
				String idCuentaMail = recuperarIdCuentaMail(dto);
				if (idCuentaMail != null) {
					String groupId = dto.getRequest().getGroupId();
					dto.setRequest(new MemberRequest(groupId, idCuentaMail));
				} else {
					/*
					 * debe registrar en MI_RESULTADO_ERRORES los datos de la cuenta a la que no se le pudo recuperar o
					 * actualizar el ID en MI_CUENTAS_AZURE
					 */
					registraError("actualiza-id-cuenta", dto.getMsgError(), new BigDecimal(res.getKey()), dto);
					continue; // no se puede agregar este miembro
				}
			}
			lista.add(dto);
		}
		return lista;
	}
	
	private String recuperarIdCuentaMail(DatosMemeberDTO dto) {
		/*
		 * debe recuperar el ID desde Gmail con la cuenta de correo
		 * actualizar tabla MI_CUENTAS_AZURE
		 * devolver ese ID si todo sale OK
		 */
		String url = String.format(templateConsultarIdCuenta, 
				getGmailServices(), String.format("%s@%s", dto.getLoginName(), getDominio()));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put(Exchange.DESTINATION_OVERRIDE_URL, url);
		headers.put("CamelHttpMethod", "GET");
		IdCuentaCorreoResponse response = null;
		try {
			response = (IdCuentaCorreoResponse)consultarIdCuenta.requestBody(null);
		} catch (Exception e) {
			logger.error(String.format("Error al invocar url %s", url), e);
			dto.setMsgError(String.format("Error al consultar id de cuenta a AZURE: %s", e.getMessage()));
		}
		if (response != null) {
			if (response.getCodigo() != 0) {
				dto.setMsgError(response.getMensaje());
				return null;
			}
		} else {
			if (dto.getMsgError() == null)
				dto.setMsgError("No pudo consultar id de cuenta a AZURE");
			return null;
		}
		// actualizar este id en MI_CUENTAS_AZURE
		headers.clear();
		headers.put("idCuenta", response.getIdCuenta());
		headers.put("rut", dto.getKeyGrupoMiembro().getIdMiembro());
		logger.info(String.format("actualizar MI_CUENTAS_AZURE: %s,%s", 
				headers.get("idCuenta"), headers.get("rut")));
		updateMiCuentasGmail.requestBodyAndHeaders(null, headers);
		return response.getIdCuenta();
	}
	
	public void registraError(String tipo, String mensaje, BigDecimal keyResultado, DatosMemeberDTO datos) {
		logger.info(String.format("registraError: tipo=%s mensaje=%s keyResultado=%d DatosMemeberDTO=%s",
				tipo, mensaje, keyResultado.intValue(), datos));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("TIPO", tipo);
		headers.put("CAUSA", mensaje);
		headers.put("KEY_GRUPO", new BigDecimal(datos.getKeyGrupoMiembro().getKeyGrupo()));
		headers.put("KEY_RESULTADO", keyResultado);
		headers.put("ID_USUARIO", datos.getKeyGrupoMiembro().getIdMiembro());
		insertMiResultadoErrores.requestBodyAndHeaders(null, headers);
	}
	
	public void registraError(String tipo, String mensaje, BigDecimal keyResultado, BigDecimal keyGrupo) {
		logger.info(String.format("registraError: tipo=%s mensaje=%s keyResultado=%d keyGrupo=%d",
				tipo, mensaje, keyResultado.intValue(), keyGrupo.intValue()));
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("TIPO", tipo);
		headers.put("CAUSA", mensaje);
		headers.put("KEY_GRUPO", keyGrupo);
		headers.put("KEY_RESULTADO", keyResultado);
		headers.put("ID_USUARIO", "N/A");
		insertMiResultadoErrores.requestBodyAndHeaders(null, headers);
	}
/*
        Groups grupo = datos.getNextGrupo(miStack);
        if (grupo == null) {
            logger.debug(this.getName() + " termino thead numero "+this.numOrden);
            setWorking(false);
            return;
        }
        Stack<UsuarioGmail> miembrosFallados = new Stack<UsuarioGmail>();
        try {
              // crear el grupo en gmail
            if (crearGrupo(grupo)) {
                // hay que darle un poco de tiempo al gmail antes de agregar los miembros
                // Nota: se dio el caso que se creo el grupo pero cuando se trato de crear
                // el primer miembro no encontro el grupo.
                //logger.debug(this.getName() + " espera");
                Thread.sleep(1000);
                //logger.debug(this.getName() + " continua");

                // sacar a los miembros que dejaron el grupo
                List<UsuarioGmail> membersNA = datos.getListaMiembrosInactivos(grupo);
                logger.info(this.getName() +" "+grupo.getGroupName()+" tiene "+membersNA.size()+" miembros para SACAR");
                for (UsuarioGmail member :
                     (UsuarioGmail[])membersNA.toArray(new UsuarioGmail[0])) {
                    sacarMiembro(grupo, member, miembrosFallados);
                }
                int retry = miembrosFallados.isEmpty() ? 0 : 3;
                do {
                    if (!miembrosFallados.isEmpty()) {
                        logger.info(this.getName() + " reintenta con "+miembrosFallados.size()+" miembros fallados");
                        UsuarioGmail[] array = (UsuarioGmail[])miembrosFallados.toArray(new UsuarioGmail[0]);
                        miembrosFallados.clear();
                        for (UsuarioGmail member : array) {
                            sacarMiembro(grupo, member, miembrosFallados);
                        }
                    } else
                        break;

                } while (retry-- > 0);
                if (!miembrosFallados.isEmpty()) {
                    // registrar los errores en la BD
                    registraError("sacarMiembro", miembrosFallados);
                }

                // agregar los que se incorporan
                List<UsuarioGmail> members = datos.getListaMiembrosActivos(grupo);
                logger.info(this.getName() +" "+grupo.getGroupName()+" tiene "+members.size()+" miembros para AGREGAR");
                miembrosFallados.clear();

                for (UsuarioGmail member :
                     (UsuarioGmail[])members.toArray(new UsuarioGmail[0])) {
                    try {
                        if (member != null)
                            agregarMiembro(grupo, member, miembrosFallados);
                    } catch (CancelAgregarMiembroException e) {
                        logger.debug("cancelada la incorporacion de miembros por:"+e.getMessage());
                        break;
                    }
                }

                // reintentar con los fallados
                retry = miembrosFallados.isEmpty() ? 0 : 3;
                do {
                    if (!miembrosFallados.isEmpty()) {
                        logger.info(this.getName() + " reintenta con "+miembrosFallados.size()+" miembros fallados");
                        UsuarioGmail[] array = (UsuarioGmail[])miembrosFallados.toArray(new UsuarioGmail[0]);
                        miembrosFallados.clear();

                        for (UsuarioGmail member : (UsuarioGmail[])miembrosFallados.toArray(new UsuarioGmail[0])) {
                            try {
                                agregarMiembro(grupo, member, miembrosFallados);
                            } catch (CancelAgregarMiembroException e) {
                                logger.debug("cancelada la incorporacion de miembros por:"+e.getMessage());
                                break;
                            }
                        }
                    } else {
                        if (retry > 0)
                            logger.debug(this.getName() + " fallo al "+retry+" de crear grupo "+grupo.getGroupName());
                        break;
                    }
                } while (retry-- > 0);
                if (!miembrosFallados.isEmpty()) {
                    // registrar los errores en la BD
                    registraError("agregarMiembro", miembrosFallados);
                }
            }
        } catch (Exception e) {
            logger.error("doJob", e);
        }


	public boolean crearGrupo (Groups grupo) {
    	if (grupo.getCreadoGmail() == null)
    		grupo.setCreadoGmail(false);
        logger.debug("Thread "+this.getName()+" crearGrupo: crea grupo "+grupo.getGroupName()+
                     " ya creado="+ grupo.getCreadoGmail().toString()+" key="+grupo.getKey());
        try {
            GmailWAOesb wao = GmailWAOesb.getInstance(this.getName());
            if (!grupo.getCreadoGmail().booleanValue()) {
            // crear grupo en gmail
                Group result = wao.createGroup(grupo.getGroupName(),
                                grupo.getGroupName(),
                                grupo.getGroupDescription()!=null?grupo.getGroupDescription():grupo.getGroupName(),
                                grupo.getEmailPermission());
                // actualizar en group que esta creado en gmail
                grupo.setCreadoGmail(Boolean.valueOf(true));
                datos.saveGrupo(grupo);
                datos.incGrupoCreado();
                logger.info("Thread "+this.getName()+" crearGrupo: crea grupo "+grupo.getGroupName());
            } else {
                // validar que efectivamente este creado en gmail
            	Group result = wao.retrieveGroup(grupo.getGroupName());
                if (result == null) {
                    // el grupo no existe en gmail pero si en la BD
                    result = wao.createGroup(grupo.getGroupName(),
                                    grupo.getGroupName(),
                                    grupo.getGroupDescription(),
                                    grupo.getEmailPermission());
                }
                datos.incGrupoActualizado();
                logger.info("crearGrupo: grupo "+grupo.getGroupName()+" existe");
            }
            return true;
        } catch (Exception e) {
            logger.error("Thread "+this.getName()+" crearGrupo", e);// Error connecting with login URI
            String msg = e.getMessage();
            if (msg != null && msg.startsWith("EntityExists")) {
                grupo.setCreadoGmail(Boolean.valueOf(true));
                try {
                    datos.saveGrupo(grupo);
                    datos.incGrupoCreado();
                    return true;
                } catch (Exception ex) {
                    logger.error("Thread "+this.getName()+" crearGrupo: al sincronizarlo", ex);
                }
            }
            registraError("crearGrupo", msg, grupo, null);
            return false;
        }
    }

    @SuppressWarnings("unused")
	private void agregarMiembro(Groups grupo, UsuarioGmail member, Stack<UsuarioGmail> miembrosFallados)
        throws CancelAgregarMiembroException {
        if (member.getEmail() != null) {
            logger.info("Thread "+this.getName()+" agregarMiembro: agrega miembro "+ member.getEmail()+
                         " al grupo "+grupo.getGroupName());
            try {
                GmailWAOesb wao = GmailWAOesb.getInstance(this.getName());
                com.google.api.services.admin.directory.model.Member result = wao.addMemberToGroup(grupo.getGroupName(), member.getEmail());
                logger.debug("Thread "+this.getName()+" agregarMiembro: el email agregado es "+member.getEmail());

                 // Actualizar la BD para indicar que el miembro ya fue agregado
                datos.saveMember(grupo, member);
                datos.incMiembroCreado();
            } catch (Exception e) {
            	if (e instanceof GoogleJsonResponseException) {
            		GoogleJsonResponseException gje = (GoogleJsonResponseException)e;
            		String msgError = gje.getDetails().getMessage();
                    if (msgError != null && msgError.startsWith("Member already exists.")) {
                        // no se pudo crear el grupo por consiguiente tampoco se van a poder
                        // agregar los miembros. Cancelar la operacion
                        try {
							datos.saveMember(grupo, member);
	                        datos.incMiembroCreado();
						} catch (Exception e1) {
			                logger.error("Thread "+this.getName()+" agregarMiembro: actualizando en BD", e);
						}
                        return;
                    }
            	}
                logger.error("Thread "+this.getName()+" agregarMiembro", e);
                // guardar el miembro fallado para una segunda vuelta

                miembrosFallados.push(new Member(member, grupo, e.getMessage()));
            }
        } else {
            logger.info("Thread "+this.getName()+" agregarMiembro:  miembro "+ member.getUserId() +
                         " no se agrega al grupo "+grupo.getGroupName()+" porque tiene email NULL");
            registraError ("agregarMiembro", "No tiene email", grupo, member);
        }
    }

    private void sacarMiembro(Groups grupo, UsuarioGmail member, Stack<UsuarioGmail> miembrosFallados) {
        if (member.getEmail() != null) {
            logger.info("Thread "+this.getName()+" sacarMiembro: saca miembro "+ member.getEmail()+
                         " del grupo "+grupo.getGroupName());
            try {
                logger.debug("Thread "+this.getName()+" sacarMiembro: el email a sacar es "+member.getEmail());
                GmailWAOesb wao = GmailWAOesb.getInstance(this.getName());
                wao.deleteMemberFromGroup(grupo.getGroupName(), member.getEmail());

                 // Actualizar la BD para indicar que el miembro ya fue agregado
                datos.deleteMember(grupo, member);
                datos.incMiembroSacado();
            } catch (Exception e) {
            	if (e instanceof GoogleJsonResponseException) {
            		GoogleJsonResponseException gje = (GoogleJsonResponseException)e;
	                String msgError = gje.getMessage();
	                logger.error("Thread "+this.getName()+" sacarMiembro", e);
	                if (msgError != null && msgError.startsWith("EntityDoesNotExist")) {
	                    // no importa..
	                    try {
	                        datos.deleteMember(grupo, member);
	                    } catch (Exception ex) {
	                        logger.error("borrando un EntityDoesNotExist", e);
	                    }
	                    return;
	                }
            	} else {
            		String msgError = e.getMessage();
	                logger.error("Thread "+this.getName()+" sacarMiembro", e);
	                // guardar el miembro fallado para una segunda vuelta

	                miembrosFallados.push(new Member(member, grupo, msgError));
            	}
            }
        } else {
            logger.info("Thread "+this.getName()+" sacarMiembro:  miembro "+ member.getUserId() +
                         " no se saca del grupo "+grupo.getGroupName()+" porque tiene email NULL");
            registraError ("sacarMiembro", "No tiene email", grupo, member);
        }
    }


 */
	public String getGmailServices() {
		return gmailServices;
	}
	public void setGmailServices(String azureServices) {
		this.gmailServices = azureServices;
	}
	public String getDominio() {
		return dominio;
	}
	public void setDominio(String dominio) {
		this.dominio = dominio;
	}

}