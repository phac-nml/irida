package ca.corefacility.bioinformatics.irida.processing;

/**
 * Exception thrown when a {@link FileProcessor} fails to complete execution.
 */
public class FileProcessorException extends RuntimeException {

	private static final long serialVersionUID = 7389408065012958110L;

	public FileProcessorException(String message) {
		super(message);
	}

	public FileProcessorException(String message, Throwable e) {
		super(message, e);
	}
}
