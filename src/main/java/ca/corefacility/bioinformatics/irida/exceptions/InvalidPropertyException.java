package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Thrown when a property cannot be set or retrieved by a service class.
 */
public class InvalidPropertyException extends RuntimeException {

	private static final long serialVersionUID = -3928623996579573284L;

	private Class<? extends Object> affectedClass;

	/**
	 * Create a new {@link InvalidPropertyException} with the given invalid class
	 * 
	 * @param message       A message stating the exception
	 * @param affectedClass The class where the invalid property was requested
	 */
	public InvalidPropertyException(String message, Class<? extends Object> affectedClass) {
		super(message);
		this.affectedClass = affectedClass;
	}

	public InvalidPropertyException(String message, Class<? extends Object> affectedClass, Throwable cause) {
		super(message, cause);
		this.affectedClass = affectedClass;
	}

	public InvalidPropertyException(String message) {
		super(message);
	}

	public Class<? extends Object> getAffectedClass() {
		return affectedClass;
	}
}
