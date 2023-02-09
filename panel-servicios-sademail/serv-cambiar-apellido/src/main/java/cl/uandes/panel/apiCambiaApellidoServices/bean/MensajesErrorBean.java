package cl.uandes.panel.apiCambiaApellidoServices.bean;

public class MensajesErrorBean implements MensajesError {

	private String notFoundBanner;
	private String noPudoAccederGmail;
	
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

}
