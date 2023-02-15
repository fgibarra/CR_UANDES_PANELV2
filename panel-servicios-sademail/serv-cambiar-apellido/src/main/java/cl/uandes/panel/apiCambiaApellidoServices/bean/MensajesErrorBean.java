package cl.uandes.panel.apiCambiaApellidoServices.bean;

import org.apache.camel.PropertyInject;

public class MensajesErrorBean implements MensajesError {

    @PropertyInject(value = "serv.cambiaApellido.notFoundBanner")
	private String notFoundBanner;
    @PropertyInject(value = "serv.cambiaApellido.noPudoAccederGmail")
	private String noPudoAccederGmail;
    @PropertyInject(value = "serv.cambiaApellido.notFoundPanel")
	private String notFoundPanel;
    @PropertyInject(value = "serv.cambiaApellido.actualizaGmail")
	private String actualizaGmail;
	
	@Override
	public String notFoundBannerException() {
		return notFoundBanner;
	}

	@Override
	public String noPudoAccederGmailException() {
		return noPudoAccederGmail;
	}

	//===============================================================================================================================
	// getters y setters
	//===============================================================================================================================
	
	public String getNotFoundBanner() {
		return notFoundBanner;
	}

	public void setNotFoundBanner(String notFoundBanner) {
		this.notFoundBanner = notFoundBanner;
	}

	public String getNoPudoAccederGmail() {
		return noPudoAccederGmail;
	}

	public void setNoPudoAccederGmail(String noPudoAccederGmail) {
		this.noPudoAccederGmail = noPudoAccederGmail;
	}

	public String getNotFoundPanel() {
		return notFoundPanel;
	}

	public void setNotFoundPanel(String notFoundPanel) {
		this.notFoundPanel = notFoundPanel;
	}

	public String getActualizaGmail() {
		return actualizaGmail;
	}

	public void setActualizaGmail(String actualizaGmail) {
		this.actualizaGmail = actualizaGmail;
	}

}
