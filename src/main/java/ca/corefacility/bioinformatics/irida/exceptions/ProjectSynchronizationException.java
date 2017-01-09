package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * An exception thrown when there is an error synchronizing a project.
 */
public class ProjectSynchronizationException extends RuntimeException {
	public ProjectSynchronizationException(String message, Throwable cause) {
		super(message, cause);
	}
}
