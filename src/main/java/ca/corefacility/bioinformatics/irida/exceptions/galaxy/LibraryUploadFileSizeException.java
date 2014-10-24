package ca.corefacility.bioinformatics.irida.exceptions.galaxy;

/**
 * Exception thrown when there is a mismatch of file sizes within a Galaxy data library.
 * @author Aaron Petkau <aaron.petkau@phac-aspc.gc.ca>
 *
 */
public class LibraryUploadFileSizeException extends LibraryUploadException {
	private static final long serialVersionUID = 50534628995103073L;

	/**
	 * Constructs a new {@link LibraryUploadFileSizeException} with no information.
	 */
	public LibraryUploadFileSizeException() {
		super();
	}

	/**
	 * Constructs a new {@link LibraryUploadFileSizeException} with the given message and cause.
	 * @param message  The message explaining the error.
	 * @param cause  The cause of this message.
	 */
	public LibraryUploadFileSizeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new {@link LibraryUploadFileSizeException} with the given message.
	 * @param message  The message explaining the error.
	 */
	public LibraryUploadFileSizeException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@link LibraryUploadFileSizeException} with the given cause.
	 * @param cause  The cause of this error.
	 */
	public LibraryUploadFileSizeException(Throwable cause) {
		super(cause);
	}
}
