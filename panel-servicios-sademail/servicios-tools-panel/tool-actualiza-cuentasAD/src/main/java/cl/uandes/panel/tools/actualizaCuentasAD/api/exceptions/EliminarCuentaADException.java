package cl.uandes.panel.tools.actualizaCuentasAD.api.exceptions;

public class EliminarCuentaADException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4592944447846220402L;

	public EliminarCuentaADException() {
		super();
	}

	public EliminarCuentaADException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EliminarCuentaADException(String message, Throwable cause) {
		super(message, cause);
	}

	public EliminarCuentaADException(String message) {
		super(message);
	}

	public EliminarCuentaADException(Throwable cause) {
		super(cause);
	}

}
