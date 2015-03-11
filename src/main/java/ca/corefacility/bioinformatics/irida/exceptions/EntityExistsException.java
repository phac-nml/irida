package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * When an entity to be created in the database shares an identifier with an
 * existing entity.
 * 
 */
public class EntityExistsException extends RuntimeException {

	private static final long serialVersionUID = 7353646703650984698L;
	private String fieldName;

	/**
	 * Construct a new {@link EntityExistsException} with the specified message.
	 * 
	 * @param message
	 *            the message explaining the exception.
	 */
	public EntityExistsException(String message) {
		super(message);
	}

	/**
	 * Construct a new {@link EntityExistsException} with the specified message
	 * and cause.
	 * 
	 * @param message
	 *            the message explaining the exception.
	 * @param cause
	 *            the original cause of the exception.
	 */
	public EntityExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Construct a new {@link EntityExistsException} with the specified message.
	 * 
	 * @param message
	 *            the message explaining the exception.
	 * @param fieldName
	 *            The name of the field causing the exception
	 */
	public EntityExistsException(String message, String fieldName) {
		this(message);
		this.fieldName = fieldName;
	}

	/**
	 * Get the field name that caused the exception
	 * 
	 * @return The name of the field that already exists in the database
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Set the field name that was in duplicate and caused the exception
	 * 
	 * @param fieldName
	 *            The field name causing the exception
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
}
