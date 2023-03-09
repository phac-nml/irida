package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;
import ca.corefacility.bioinformatics.irida.ria.web.ajax.dto.analysis.FileChunkResponse;

import com.google.common.collect.Lists;

/**
 * Interface describing methods for performing storage actions
 */

public interface IridaFileStorageUtility {
	//Valid file extensions for sample file concatenation
	public static final List<String> VALID_CONCATENATION_EXTENSIONS = Lists.newArrayList("fastq", "fastq.gz");

	/**
	 * Get a file from storage
	 *
	 * @param file The {@link Path} to the file
	 * @return {@link IridaTemporaryFile} which includes the file and optional temporary directory
	 */
	public IridaTemporaryFile getTemporaryFile(Path file);

	/**
	 * Overloaded method to get a file from storage and add prefix to directory
	 *
	 * @param file   The {@link Path} to the file
	 * @param prefix The {@link String} prefix to add to the directory name
	 * @return {@link IridaTemporaryFile} which includes the file and optional temporary directory
	 */
	public IridaTemporaryFile getTemporaryFile(Path file, String prefix);

	/**
	 * Delete temporary downloaded file and/or directory.
	 *
	 * @param iridaTemporaryFile The {@link IridaTemporaryFile} object which includes the file path and/or directory
	 *                           path
	 */
	public void cleanupDownloadedLocalTemporaryFiles(IridaTemporaryFile iridaTemporaryFile);

	/**
	 * Write file to storage (azure, aws, or local)
	 *
	 * @param source                      The {@link Path} to the file
	 * @param target                      The {@link Path} to where file should be moved
	 * @param sequenceFileDir             The {@link Path} to sequence file directory
	 * @param sequenceFileDirWithRevision The {@link Path} to sequence file revision directory
	 */
	public void writeFile(Path source, Path target, Path sequenceFileDir, Path sequenceFileDirWithRevision);

	/**
	 * Delete file from storage (azure, aws, or local)
	 *
	 * @param file The {@link Path} of the file
	 */
	public void deleteFile(Path file);

	/**
	 * Delete folder from storage (azure, aws, or local)
	 *
	 * @param folder The {@link Path} of the folder
	 */
	public void deleteFolder(Path folder);

	/**
	 * Gets the file name from the storage type that the file is saved to (azure, aws, or local disk)
	 *
	 * @param file The path to the file for which to get name for
	 * @return {@link String} The file name for the file
	 */
	public String getFileName(Path file);

	/**
	 * Checks if file exists
	 *
	 * @param file The path to the file
	 * @return true if file exists otherwise false
	 */
	public boolean fileExists(Path file);

	/**
	 * Gets the file inputstream. ### Note: This method must be called in a ### try-with-resources block so that the ###
	 * underlying inputstream is closed.
	 *
	 * @param file The path to the file
	 * @return file inputstream
	 */
	public InputStream getFileInputStream(Path file);

	/**
	 * Checks if file is gzipped
	 *
	 * @param file The path to the file
	 * @return true if file is gzipped otherwise false
	 * @throws IOException if file can't be read
	 */
	public boolean isGzipped(Path file) throws IOException;

	/**
	 * Append a {@link SequenceFile} to a {@link Path} on the filesystem
	 *
	 * @param target the {@link Path} to append to
	 * @param file   the {@link SequenceFile} to append to the path
	 * @throws IOException if there is an error appending the file
	 */
	public void appendToFile(Path target, SequenceFile file) throws IOException;

	/**
	 * Get the extension of the files
	 *
	 * @param sequencingObjects The list of {@link SequencingObject} to get file extensions for
	 * @return The common extension of the files
	 * @throws IOException if the files have different or invalid extensions
	 */
	public String getFileExtension(List<? extends SequencingObject> sequencingObjects) throws IOException;

	/**
	 * Read the bytes for a file
	 *
	 * @param file The path to the file
	 * @return the bytes for the file
	 */
	public byte[] readAllBytes(Path file);

	/**
	 * Get file size in bytes
	 *
	 * @param file The {@link Path} to the file
	 * @return {@link Long} size of file in bytes retrieved from path
	 */
	public Long getFileSizeBytes(Path file);

	/**
	 * Get file in chunks
	 *
	 * @param file  The {@link Path} to the file
	 * @param seek  File pointer to where to start reading from
	 * @param chunk Size in bytes to read from seek point
	 * @return {@link FileChunkResponse} Response dto containing the text and file pointer
	 */
	public FileChunkResponse readChunk(Path file, Long seek, Long chunk);

	/**
	 * Check if the given directory is writable
	 *
	 * @param baseDirectory The directory to check write access for
	 * @return if the directory is writable or not
	 */
	public boolean checkWriteAccess(Path baseDirectory);

	/**
	 * Check if the storage type is local
	 *
	 * @return if the storage type is local or not
	 */
	public boolean isStorageTypeLocal();

	/**
	 * Get the storage type.
	 *
	 * @return {@link String} The storage type
	 */
	public String getStorageType();
}
