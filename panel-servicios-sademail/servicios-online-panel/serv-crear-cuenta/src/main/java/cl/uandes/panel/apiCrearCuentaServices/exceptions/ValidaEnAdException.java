package cl.uandes.panel.apiCrearCuentaServices.exceptions;

public class ValidaEnAdException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8790378916886576972L;

	public ValidaEnAdException() {
	}

	public ValidaEnAdException(String message) {
		super(message);
	}

	public ValidaEnAdException(Throwable cause) {
		super(cause);
	}

	public ValidaEnAdException(String message, Throwable cause) {
		super(message, cause);
	}

	public ValidaEnAdException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
