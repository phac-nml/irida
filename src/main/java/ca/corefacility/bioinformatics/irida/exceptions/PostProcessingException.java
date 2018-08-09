package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Exception thrown when a {@link ca.corefacility.bioinformatics.irida.pipeline.results.updater.AnalysisSampleUpdater} fails during post processing
 */
public class PostProcessingException extends Exception {
	public PostProcessingException(String message) {
		super(message);
	}

	public PostProcessingException(String message, Throwable cause) {
		super(message, cause);
	}
}
