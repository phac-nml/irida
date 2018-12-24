package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Exception thrown when a {@link ca.corefacility.bioinformatics.irida.pipeline.results.updater.AnalysisSampleUpdater} fails during post processing
 */
public class PostProcessingException extends Exception {

	private static final long serialVersionUID = 7166415585213320003L;

	public PostProcessingException(String message) {
		super(message);
	}

	public PostProcessingException(String message, Throwable cause) {
		super(message, cause);
	}
}
