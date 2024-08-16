package cl.uandes.panel.tools.actualizaCuentasAD.api.exceptions;

public class ExisteCuentaADException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4611008113890086450L;

	public ExisteCuentaADException() {
		super();
	}

	public ExisteCuentaADException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ExisteCuentaADException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExisteCuentaADException(String message) {
		super(message);
	}

	public ExisteCuentaADException(Throwable cause) {
		super(cause);
	}

}
