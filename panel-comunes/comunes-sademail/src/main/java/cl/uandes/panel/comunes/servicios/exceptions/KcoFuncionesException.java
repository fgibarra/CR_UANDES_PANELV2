package cl.uandes.panel.comunes.servicios.exceptions;

public class KcoFuncionesException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4333151068035047581L;

	public KcoFuncionesException() {
	}

	public KcoFuncionesException(String message) {
		super(message);
	}

	public KcoFuncionesException(Throwable cause) {
		super(cause);
	}

	public KcoFuncionesException(String message, Throwable cause) {
		super(message, cause);
	}

	public KcoFuncionesException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
