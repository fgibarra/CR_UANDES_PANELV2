package cl.uandes.panel.tools.actualizaCuentasAD.api.exceptions;

public class CrearCuentaADException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6483188614066536634L;

	public CrearCuentaADException() {
		super();
	}

	public CrearCuentaADException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CrearCuentaADException(String message, Throwable cause) {
		super(message, cause);
	}

	public CrearCuentaADException(String message) {
		super(message);
	}

	public CrearCuentaADException(Throwable cause) {
		super(cause);
	}

}
