package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Exception that gets thrown when uploading data to a separate data management
 * system (e.g. Galaxy).
 * 
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 * 
 */
public class UploadException extends Exception {
	private static final long serialVersionUID = 8934376035189966872L;

	public UploadException() {
		super();
	}

	public UploadException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UploadException(String message, Throwable cause) {
		super(message, cause);
	}

	public UploadException(String message) {
		super(message);
	}

	public UploadException(Throwable cause) {
		super(cause);
	}
}
