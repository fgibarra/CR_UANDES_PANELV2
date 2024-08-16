package cl.uandes.panel.tools.actualizaCuentasAD.api.exceptions;

public class CrearEnTablaException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 655161265830756442L;

	public CrearEnTablaException() {
		super();
	}

	public CrearEnTablaException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CrearEnTablaException(String message, Throwable cause) {
		super(message, cause);
	}

	public CrearEnTablaException(String message) {
		super(message);
	}

	public CrearEnTablaException(Throwable cause) {
		super(cause);
	}

}
