package cl.uandes.sadmemail.apiReportServices.wao;

import java.util.Map;

public class ReportGmailWAOesb extends ReportGmailWAOImpl {

	protected String gmailAdmin;
	protected String gmailPasswd;
	protected String gmailDominio;
	
	public ReportGmailWAOesb(Map<String, Object> map) {
		super();
		this.gmailAdmin = (String) map.get("GMAIL_DOMINIO");
		this.gmailPasswd = (String) map.get("GMAIL_ADMIN");
		this.gmailDominio = (String) map.get("GMAIL_PASSWD");
		super.init();
	}

	@Override
	protected String getGmailAdmin() {
		return gmailAdmin;
	}

	@Override
	protected String getGmailPasswd() {
		return gmailPasswd;
	}

	@Override
	protected String getGmailDominio() {
		return gmailDominio;
	}
/*
	public static void main (String args[]) throws IOException {
		// instanciar el logger
		java.util.Properties properties = new java.util.Properties();
		properties.load(ReportGmailWAOesb.class.getClassLoader().getResourceAsStream("report_log4j.properties"));
		org.apache.log4j.PropertyConfigurator.configure(properties);
		
		Map<String, Object> parametros = new java.util.HashMap<String, Object>();
		parametros.put("GMAIL_DOMINIO", "miuandes.cl");
		parametros.put("GMAIL_ADMIN", "fgibarra@miuandes.cl");
		parametros.put("GMAIL_PASSWD", "pirquep64");
		ReportGmailWAOImpl rwao = new ReportGmailWAOesb(parametros);
		rwao.getReportUsuario("116633621628576129709");
	}
	*/
}
