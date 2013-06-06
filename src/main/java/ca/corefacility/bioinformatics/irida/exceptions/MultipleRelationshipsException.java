package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * When an multiple types of {@link Relationship} exist between two entities and only one was requested.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class MultipleRelationshipsException extends RuntimeException {

    /**
     * Construct a new {@link MultipleRelationshipsException} with a message.
     *
     * @param message the message to report.
     */
    public MultipleRelationshipsException(String message) {
        super(message);
    }
}
