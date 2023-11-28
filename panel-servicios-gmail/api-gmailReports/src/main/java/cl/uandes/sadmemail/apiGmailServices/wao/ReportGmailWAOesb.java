package cl.uandes.sadmemail.apiGmailServices.wao;

import java.util.Map;

public class ReportGmailWAOesb extends ReportGmailWAOImpl {

	private String gmailAdmin;
	private String gmailPasswd;
	private String gmailDominio;
	
	public ReportGmailWAOesb(Map<String, Object> map) {
		super(map);
		this.gmailAdmin = (String) map.get("GMAIL_DOMINIO");
		this.gmailPasswd = (String) map.get("GMAIL_ADMIN");
		this.gmailDominio = (String) map.get("GMAIL_PASSWD");
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

	public static void main (String args[]) {
		Map<String, Object> parametros = new java.util.HashMap<String, Object>();
		parametros.put("GMAIL_DOMINIO", "miuandes.cl");
		parametros.put("GMAIL_ADMIN", "fgibarra@miuandes.cl");
		parametros.put("GMAIL_PASSWD", "pirquep64");
		ReportGmailWAOImpl rwao = new ReportGmailWAOesb(parametros);
		rwao.getReportUsuario("116633621628576129709");
	}
}
