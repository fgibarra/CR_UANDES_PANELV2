package cl.uandes.panel.tools.actualizaCuentasAD.api.exceptions;

public class ValidarCuentaADException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 132487726881255953L;

	public ValidarCuentaADException() {
		super();
	}

	public ValidarCuentaADException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ValidarCuentaADException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidarCuentaADException(String message) {
		super(message);
	}

	public ValidarCuentaADException(Throwable cause) {
		super(cause);
	}

}
