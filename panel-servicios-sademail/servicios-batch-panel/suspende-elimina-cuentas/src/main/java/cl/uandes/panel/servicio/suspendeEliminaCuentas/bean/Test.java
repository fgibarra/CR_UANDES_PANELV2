package cl.uandes.panel.servicio.suspendeEliminaCuentas.bean;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.component.file.GenericFile;
import org.apache.log4j.Logger;

import cl.uandes.sadmemail.comunes.google.api.services.User;
import cl.uandes.sadmemail.comunes.report.json.Report;
import cl.uandes.panel.comunes.utils.JSonUtilities;
import cl.uandes.panel.servicio.suspendeEliminaCuentas.procesor.SincronizaUsuario;

public class Test {
	
	private SincronizaUsuario  sincronizarUsuarioBean;

	private Logger logger = Logger.getLogger(getClass());
	User user = null;
	Report report = null;
	
	public void getUser(Exchange exchange) {
		Message message = exchange.getIn();
		
		GenericFile body = (GenericFile) message.getBody();
		logger.info (String.format("la clase del body es: %s", body != null ? body.getClass().getName(): "ES NULO"));
		
		try {
			Reader reader = new FileReader((File)body.getBody());
			user = (User) JSonUtilities.getInstance().json2java(reader, User.class);
			logger.info((String.format("convertido user: %s", user)));
			message.setHeader("user", user);
		} catch (Exception e) {
			logger.error("al convertir user", e);
		}
	}

	public void getReport(Exchange exchange) {
		Message message = exchange.getIn();
		
		GenericFile body = (GenericFile) message.getBody();
		logger.info (String.format("la clase del body es: %s", body != null ? body.getClass().getName(): "ES NULO"));
		
		try {
			Reader reader = new FileReader((File)body.getBody());
			report = (Report) JSonUtilities.getInstance().json2java(reader, Report.class);
			logger.info((String.format("convertido report: %s", report)));
			message.setHeader("report", report);
		} catch (Exception e) {
			logger.error("al convertir report", e);
		}
	}

	public void invocaavisarSuspencion(Exchange exchange) {
		Message message = exchange.getIn();
//		User user = (User) message.getHeader("user");
		message.setHeader("user", user);
		logger.info((String.format("invocaavisarSuspencion user: %s", user)));
//		Report report = (Report) message.getHeader("report");
		message.setHeader("report", report);
		logger.info((String.format("invocaavisarSuspencion report: %s", report)));
		if (user == null || report == null) {
			logger.info("Que pasa user o report null");
		} else {
			sincronizarUsuarioBean.setPeriodoGracia(Long.valueOf(7));
			sincronizarUsuarioBean.avisarSuspencion(exchange, user, report, Long.valueOf(20000));
		}
		
	}
	public synchronized SincronizaUsuario getSincronizarUsuarioBean() {
		return sincronizarUsuarioBean;
	}

	public synchronized void setSincronizarUsuarioBean(SincronizaUsuario sincronizarUsuarioBean) {
		this.sincronizarUsuarioBean = sincronizarUsuarioBean;
	}
}
