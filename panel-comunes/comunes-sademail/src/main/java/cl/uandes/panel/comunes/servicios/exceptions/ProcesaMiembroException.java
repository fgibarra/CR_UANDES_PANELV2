package cl.uandes.panel.comunes.servicios.exceptions;

public class ProcesaMiembroException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -899547609892580953L;

	public ProcesaMiembroException() {
		super();
	}

	public ProcesaMiembroException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ProcesaMiembroException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProcesaMiembroException(String message) {
		super(message);
	}

	public ProcesaMiembroException(Throwable cause) {
		super(cause);
	}

}
