package cl.uandes.panel.tools.actualizaCuentasAD.api.exceptions;

public class EliminarDeTablaException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2528105378639222974L;

	public EliminarDeTablaException() {
		super();
	}

	public EliminarDeTablaException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EliminarDeTablaException(String message, Throwable cause) {
		super(message, cause);
	}

	public EliminarDeTablaException(String message) {
		super(message);
	}

	public EliminarDeTablaException(Throwable cause) {
		super(cause);
	}

}
