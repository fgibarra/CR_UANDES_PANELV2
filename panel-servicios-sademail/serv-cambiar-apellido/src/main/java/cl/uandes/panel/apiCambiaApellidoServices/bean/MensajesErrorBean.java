package cl.uandes.panel.apiCambiaApellidoServices.bean;

public class MensajesErrorBean implements MensajesError {

	private String notFoundBanner;
	private String noPudoAccederGmail;
	private String notFoundPanel;
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
