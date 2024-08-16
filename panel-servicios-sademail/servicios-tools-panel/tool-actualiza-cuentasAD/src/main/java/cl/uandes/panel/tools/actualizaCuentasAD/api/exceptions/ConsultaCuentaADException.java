package cl.uandes.panel.tools.actualizaCuentasAD.api.exceptions;

public class ConsultaCuentaADException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7479623481639868736L;

	public ConsultaCuentaADException() {
		super();
	}

	public ConsultaCuentaADException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ConsultaCuentaADException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConsultaCuentaADException(String message) {
		super(message);
	}

	public ConsultaCuentaADException(Throwable cause) {
		super(cause);
	}

}
