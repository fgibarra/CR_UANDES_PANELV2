package cl.uandes.sadmemail.apiGmailServices.wao;

import java.util.Map;

import cl.uandes.sadmemail.apiGmailServices.wao.dto.CuentaGmailParameters;

public class GmailWAOesb extends GmailWAOBaseImpl implements GmailWAOBase {


	private CuentaGmailParameters cuentaGmailParameters;
	
	public GmailWAOesb(Map<String,Object> map) {
		super();
		cuentaGmailParameters = new CuentaGmailParameters();
		cuentaGmailParameters.setGmailDominio((String) map.get("GMAIL_DOMINIO"));
		cuentaGmailParameters.setGmailPasswd((String) map.get("GMAIL_PASSWD"));
		cuentaGmailParameters.setGmailAdmin((String) map.get("GMAIL_ADMIN"));
		init();
	}

	@Override
	protected String getGmailAdmin() {
		return cuentaGmailParameters != null ? cuentaGmailParameters.getGmailAdmin() : null;
	}

	@Override
	protected String getGmailPasswd() {
		return cuentaGmailParameters != null ? cuentaGmailParameters.getGmailPasswd() : null;
	}

	@Override
	protected String getGmailDominio() {
		return cuentaGmailParameters != null ? cuentaGmailParameters.getGmailDominio() : null;
	}
}
