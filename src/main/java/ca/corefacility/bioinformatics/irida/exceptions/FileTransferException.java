package ca.corefacility.bioinformatics.irida.exceptions;

import ca.corefacility.bioinformatics.irida.repositories.remote.SequenceFileRemoteRepository;

/**
 * Thrown when the download of a file from {@link SequenceFileRemoteRepository}
 * returns an invalid file.
 */
public class FileTransferException extends Exception {

	private static final long serialVersionUID = 4198528260471319889L;

	public FileTransferException(String message) {
		super(message);
	}
}
