package ca.corefacility.bioinformatics.irida.exceptions;

/**
 * Exception to be thrown if the upload metadata file does not match any
 * of the predetermined formats.
 */
public class MetadataImportFileTypeNotSupportedError extends RuntimeException {
	public MetadataImportFileTypeNotSupportedError(String extension) {
		super("Importing metadata does not support: [" + extension + "] file type.");
	}
}
