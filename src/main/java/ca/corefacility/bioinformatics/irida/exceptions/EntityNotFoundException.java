package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * When an {@link Identifiable} entity cannot be found in the database.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class EntityNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 2074593569749610287L;

	/**
     * Construct a new {@link EntityNotFoundException} with the specified
     * message.
     *
     * @param message the message explaining the exception.
     */
    public EntityNotFoundException(String message) {
        super(message);
    }
}
