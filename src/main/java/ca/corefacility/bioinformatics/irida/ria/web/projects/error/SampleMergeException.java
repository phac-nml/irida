package ca.corefacility.bioinformatics.irida.ria.web.projects.error;

/**
 * Error thrown during the samples merge process
 */
public class SampleMergeException extends Exception {
	public SampleMergeException(String message) {
		super(message);
	}
}
