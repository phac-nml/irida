package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * When an {@link Identifiable} entity to be created in the database shares an
 * identifier with an existing entity.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class EntityExistsException extends RuntimeException {

	private static final long serialVersionUID = 7353646703650984698L;

	/**
     * Construct a new {@link EntityExistsException} with the specified message.
     *
     * @param message the message explaining the exception.
     */
    public EntityExistsException(String message) {
        super(message);
    }
}
