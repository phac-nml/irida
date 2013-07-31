package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Thrown when a property cannot be set or retrieved by a service class.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class InvalidPropertyException extends RuntimeException {

	private static final long serialVersionUID = 8312518577495811389L;

	public InvalidPropertyException(String message) {
        super(message);
    }
}
