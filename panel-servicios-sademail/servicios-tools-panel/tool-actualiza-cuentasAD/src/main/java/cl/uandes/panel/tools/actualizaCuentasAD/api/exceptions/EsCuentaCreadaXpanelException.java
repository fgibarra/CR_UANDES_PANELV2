package cl.uandes.panel.tools.actualizaCuentasAD.api.exceptions;

public class EsCuentaCreadaXpanelException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3874934871604194640L;

	public EsCuentaCreadaXpanelException() {
		super();
	}

	public EsCuentaCreadaXpanelException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EsCuentaCreadaXpanelException(String message) {
		super(message);
	}

	public EsCuentaCreadaXpanelException(Throwable cause) {
		super(cause);
	}

	public EsCuentaCreadaXpanelException(String message, ConsultaCuentaADException e) {
		super(message, e);
	}

}
