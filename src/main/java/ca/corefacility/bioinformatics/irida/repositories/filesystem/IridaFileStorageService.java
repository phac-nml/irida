package ca.corefacility.bioinformatics.irida.repositories.filesystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

import ca.corefacility.bioinformatics.irida.exceptions.ConcatenateException;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequenceFile;
import ca.corefacility.bioinformatics.irida.model.sequenceFile.SequencingObject;

import com.google.common.collect.Lists;

/**
 * Interface describing methods for performing storage actions
 */

public interface IridaFileStorageService {
	//Valid extensions to try to concatenate with this tool
	public static final List<String> VALID_EXTENSIONS = Lists.newArrayList("fastq", "fastq.gz");
	/**
	 * Get a temporarry file from storage
	 *
	 * @param file The {@link Path} to the file
	 * @return {@link File} which was retrieved from path
	 */
	public File getTemporaryFile(Path file);

	/**
	 * Get file size in bytes
	 *
	 * @param file The {@link Path} to the file
	 * @return {@link Long} size of file retrieved from path
	 */
	public Long getFileSize(Path file);

	/**
	 * Write file to storage (azure, aws, or local)
	 *
	 * @param source The {@link Path} to the file
	 * @param target The {@link Path} to where file should be moved
	 */
	public void writeFile(Path source, Path target, Path sequenceFileDir, Path sequenceFileDirWithRevision);

	/**
	 * Returns if the storage type is local or not
	 *
	 * @return {@link Boolean#TRUE} if local, {@link Boolean#FALSE} if not.
	 */
	public boolean storageTypeIsLocal();

	/**
	 * Gets the file name from the storage type that the file
	 * is saved to (azure, aws, or local disk)
	 *
	 * @param file The path to the file for which to get name for
	 * @return {@link String} The file name for the file
	 */
	public String getFileName(Path file);

	/**
	 * Deletes the file from (azure, aws, or local disk)
	 *
	 */
	public void deleteFile();

	/**
	 * Download the file from (azure, aws, or local disk)
	 *
	 */
	public void downloadFile();

	/**
	 * Downloads all the files of type `analysis-output` from
	 * (azure, aws, or local disk)
	 *
	 */
	public void downloadFiles();

	/**
	 * Checks if file exists
	 *
	 * @param file The path to the file
	 * @return true if file exists otherwise false
	 *
	 */
	public boolean fileExists(Path file);

	/**
	 * Gets the file inputstream
	 *
	 * @param file The path to the file
	 * @return file inputstream
	 *
	 */
	public InputStream getFileInputStream(Path file);

	/**
	 * Checks if file is gzipped
	 *
	 * @param file The path to the file
	 * @return true if file is gzipped otherwise false
	 * @throws IOException if file can't be read
	 *
	 */
	public boolean isGzipped(Path file) throws IOException;

	/**
	 * Append a {@link SequenceFile} to a {@link Path} on the filesystem
	 *
	 * @param target the {@link Path} to append to
	 * @param file   the {@link SequenceFile} to append to the path
	 * @throws ConcatenateException if there is an error appending the file
	 */
	public void appendToFile(Path target, SequenceFile file) throws ConcatenateException;

	/**
	 * Get the extension of the files to concatenate
	 *
	 * @param toConcatenate The list of {@link SequencingObject} to concatenate
	 * @return The common extension of the files
	 * @throws ConcatenateException if the files have different or invalid extensions
	 */
	public String getFileExtension(List<? extends SequencingObject> toConcatenate) throws ConcatenateException;
}
