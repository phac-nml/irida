package ca.corefacility.bioinformatics.irida.web.controller.api.exception;

/**
 * An exception that can be used when the
 * {@link ca.corefacility.bioinformatics.irida.web.controller.api.RESTGenericController} fails to construct a resource.
 */
public class GenericsException extends RuntimeException {

	private static final long serialVersionUID = 7337013431300992746L;

	public GenericsException(String message) {
		super(message);
	}

	/**
	 * Construct a GenericsException with a given message and cause
	 * 
	 * @param message Why the exception is being thrown
	 * @param cause   What caused th exception
	 */
	public GenericsException(String message, Throwable cause) {
		super(message, cause);
	}
}
