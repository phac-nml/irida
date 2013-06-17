package ca.corefacility.bioinformatics.irida.web.controller.api.exception;

/**
 * An exception that can be used when the {@link ca.corefacility.bioinformatics.irida.web.controller.api.GenericController}
 * fails to construct a resource.
 *
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 */
public class GenericsException extends RuntimeException {
    public GenericsException(String message) {
        super(message);
    }
}
