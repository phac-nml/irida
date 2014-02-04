package ca.corefacility.bioinformatics.irida.exceptions;

public class UploadConnectionException extends UploadException {
	private static final long serialVersionUID = 736574425314133873L;

	public UploadConnectionException() {
		super();
	}

	public UploadConnectionException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UploadConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

	public UploadConnectionException(String message) {
		super(message);
	}

	public UploadConnectionException(Throwable cause) {
		super(cause);
	}
}
