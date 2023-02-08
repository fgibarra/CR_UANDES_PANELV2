package cl.uandes.panel.apiCambiaApellidoServices.bean;

public class MensajesErrorBean implements MensajesError {

	private String notFoundBanner;
	
	@Override
	public String notFoundBannerException() {
		return notFoundBanner;
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
