package ca.corefacility.bioinformatics.irida.repositories;

import java.nio.file.Path;

import ca.corefacility.bioinformatics.irida.exceptions.StorageException;
import ca.corefacility.bioinformatics.irida.model.SequenceFile;

/**
 * Special interface for storing {@link SequenceFile} on the filesystem.
 * 
 * @author Franklin Bristow <franklin.bristow@phac-aspc.gc.ca>
 * 
 */
public interface SequenceFileFilesystem {

	/**
	 * The {@link SequenceFile} *must* have an identifier before being passed to
	 * this method, because the identifier is used as an internal directory
	 * name.
	 * 
	 * @param object
	 *            the {@link SequenceFile} to store.
	 * @return a reference to the {@link SequenceFile} with the stored path.
	 * @throws IllegalArgumentException
	 *             if the {@link SequenceFile} does not have an identifier.
	 * @throws StorageException
	 *             if the file couldn't be written to disk.
	 */
	public SequenceFile writeSequenceFileToDisk(SequenceFile object) throws IllegalArgumentException, StorageException;

	/**
	 * Update file for the specified identifier.
	 * 
	 * @param id
	 *            the identifier.
	 * @param file
	 *            the new file.
	 * @return the location of the stored file.
	 * @throws IllegalArgumentException
	 *             if the id was null.
	 * @throws StorageException
	 *             if the file couldn't be written to disk.
	 */
	public Path updateSequenceFileOnDisk(Long id, Path file,Long revisionNumber) throws IllegalArgumentException, StorageException;
}
